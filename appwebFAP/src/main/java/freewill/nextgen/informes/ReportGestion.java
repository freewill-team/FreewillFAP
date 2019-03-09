package freewill.nextgen.informes;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import freewill.nextgen.apoi.ApoiDocExport;
import freewill.nextgen.apoi.Report;
import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.data.CategoriaEntity;
import freewill.nextgen.data.CategoriaEntity.ModalidadEnum;
import freewill.nextgen.data.CompanyEntity;
import freewill.nextgen.data.CompeticionEntity;
import freewill.nextgen.data.ParticipanteEntity;
import freewill.nextgen.data.Style;
import freewill.nextgen.hmi.utils.Messages;

public class ReportGestion extends Report {
	
	private List<Style> styleList = null;
	
	List<Style> getStyles(){
		try {
			if(styleList == null)
				styleList = BltClient.get().getEntities(Style.class,
					EntryPoint.get().getAccessControl().getTokenKey());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return styleList;
	}
	
	@SuppressWarnings("deprecation")
	public ReportGestion(Long circuito, String circuitoStr, InformesLogic viewLogic){
		try{
			if(viewLogic==null) return;
			
			// Create first a temporal file
			File tempFile = File.createTempFile("Informe "+circuitoStr+" ", ".docx");
			
			// Gets Company Template
			CompanyEntity cpy = EntryPoint.get().getAccessControl().getCompany();
						
			// Creates the Word Document
			Date date = new Date();
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			ApoiDocExport doc = new ApoiDocExport(tempFile, cpy.getDocxtemplate(), getStyles());
			doc.Replace("###COMPANY###", cpy.getName());
			doc.Replace("###REPORT###", circuitoStr);
			doc.Replace("###TITLE###", "Informe "+circuitoStr);
			doc.Replace("###DATE###", df.format(date));
			
			doc.AddTitle("Pruebas "+circuitoStr, 1);
			doc.AddParagraph("Durante el presente año (" +circuitoStr+ ") han sido organizadas por "
					+ "los clubes, en colaboración con " + cpy.getName() +", las siguientes "
					+ "competiciones.");
			printCompeticiones(circuito, viewLogic, doc);
			
			doc.AddTitle("Resultados "+circuitoStr, 1);
			doc.AddParagraph("A continuación relacionaremos los resultados obtenidos en las "
					+ "diferentes pruebas celebradas durante el trascurso del circuito."
					);
			printResultadosCategorias(circuito, viewLogic, doc);
			
			doc.AddTitle("Mejores Marcas", 1);
			doc.AddParagraph("En este apartado procederemos a destacar las mejores marcas "
					+ "obtenidas por los deportistas en el presente año, para las modalidades "
					+ "Speed y Salto."
					);
			printMejoresMarcas(ModalidadEnum.SPEED, "ASC", viewLogic, doc);
			printMejoresMarcas(ModalidadEnum.JUMP, "DESC", viewLogic, doc);
					
			// Close the Word Document
			doc.AddParagraph("");
			doc.CloseDocument();
			
			// Set internal variables
			setFile(tempFile);
			setSuccess(true);
			
			//return tempFile;
		}
		catch(Exception e){
			e.printStackTrace();
			setSuccess(false);
		}
	}

	private void printCompeticiones(Long circuito, InformesLogic viewLogic, 
			ApoiDocExport doc) {
		try{
			List<CompeticionEntity> competiciones = viewLogic.getCompeticiones(circuito);
			for(CompeticionEntity rec:competiciones){
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				String text = "• " + df.format(rec.getFechaInicio()) + " " +
						rec.getNombre() + " - " + rec.getLocalidad() + "";
				doc.AddBullet(text);
			}
			doc.AddParagraph("");
			for(CompeticionEntity rec:competiciones){
				printDetalleCompeticion(rec, viewLogic, doc);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	private void printDetalleCompeticion(CompeticionEntity rec, InformesLogic viewLogic, 
			ApoiDocExport doc) {
		try{
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			doc.AddTitle(rec.getNombre(), 2);
			String table[][] = new String[6][2];
    		table[0][0] = Messages.get().getKey("competicion");
    		table[1][0] = Messages.get().getKey("fecha");
    		table[2][0] = Messages.get().getKey("organizador");
    		table[3][0] = Messages.get().getKey("localidad");
    		table[4][0] = Messages.get().getKey("modalidades");
    		table[5][0] = Messages.get().getKey("participantes");
    		table[0][1] = rec.getNombre();
    		table[1][1] = df.format(rec.getFechaInicio()) + " - " + df.format(rec.getFechaFin());
    		table[2][1] = rec.getOrganizador();
    		table[3][1] = rec.getLocalidad();
    		table[4][1] = (rec.getSpeed()?"Speed ":"") + (rec.getDerrapes()?"Derrapes ":"") +
    				(rec.getSalto()?"Salto ":"") + (rec.getClassic()?"Classic ":"") +
    				(rec.getBattle()?"Battle ":"") + (rec.getJam()?"Jam ":"");
    		table[5][1] = ""; // TODO
    		doc.AddTable(table);
    		doc.AddParagraph("");
    		
    		doc.AddTitle("Detalle Participantes por Modalidad", 3);
    		for(ModalidadEnum modalidad:ModalidadEnum.values()){
    			long suma = 0;
	    		List<CategoriaEntity> categorias = viewLogic.getCategorias(modalidad);
	    		String table2[][] = new String[categorias.size()+2][3]; // TODO borrar lineas en blanco
	    		table2[0][0]=Messages.get().getKey("modalidad");
	    		table2[0][1]=Messages.get().getKey("categoria");
	    		table2[0][2]=Messages.get().getKey("participantes");
	    		int j=1;
				// Process the List item
				for(CategoriaEntity cat:categorias){
					long count = viewLogic.getCountParticipantes(rec.getId(), cat.getId());
					if(count>0){
						table2[j][0]= ""+modalidad;
			    		table2[j][1]= cat.getNombre();
			    		table2[j][2]= ""+count;
			    		suma+=count;
			    		j++;
					}
				}
				table2[j][0]= ""+modalidad;
	    		table2[j][1]= "Total "+Messages.get().getKey("participantes");
	    		table2[j][2]= ""+suma;
	    		doc.AddTable(table2);
	    		doc.AddParagraph("");
    		}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	private void printMejoresMarcas(ModalidadEnum modalidad, String sortby, 
			InformesLogic viewLogic, ApoiDocExport doc) {
		try{
			doc.AddTitle("Mejores Marcas "+modalidad, 2);
			List<CategoriaEntity> categorias = viewLogic.getCategorias(modalidad);
			for(CategoriaEntity rec:categorias){
				List<ParticipanteEntity> collection = 
						viewLogic.getMejoresMarcas(rec.getId(), sortby);
				if(collection!=null && collection.size()>0)
					printMarcas(rec.getNombre(), collection, doc);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}	
	}

	private void printMarcas(String title, List<ParticipanteEntity> collection, 
			 ApoiDocExport doc) {
		try{
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			doc.AddTitle(title, 3);
			String table[][] = new String[collection.size()+1][4];
    		table[0][0]=Messages.get().getKey("fecha");
    		table[0][1]=Messages.get().getKey("competicion");
    		table[0][2]=Messages.get().getKey("patinador");
    		table[0][3]=Messages.get().getKey("mejorMarca");
    		int j=1;
			// Process the List item
			for(ParticipanteEntity rec:collection){
				table[j][0]=""+df.format(rec.getFecha());
				table[j][1]=""+rec.getCompeticionStr();
		    	table[j][2]=""+rec.getNombre() + " " + rec.getApellidos();
		    	table[j][3]=""+rec.getMejorMarca();
		    	if(rec.getCategoriaStr().toLowerCase().contains("speed"))
		    		table[j][3]+=" ms";
		    	else
		    		table[j][3]+=" cm";
	    		j++;
			}
    		doc.AddTable(table);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	private void printResultadosCategorias(Long circuito, InformesLogic viewLogic, 
			ApoiDocExport doc) {
		try{
			List<CategoriaEntity> categorias = viewLogic.getCategorias();
			List<CompeticionEntity> competiciones = viewLogic.getCompeticiones(circuito);
			for(CompeticionEntity comp:competiciones){
				String text = comp.getNombre() + " - " + comp.getLocalidad() + "";
				doc.AddTitle(text, 2);
				for(CategoriaEntity rec:categorias){
					List<ParticipanteEntity> collection = 
							viewLogic.getResultados(comp.getId(), rec.getId());
					if(collection!=null && collection.size()>0){
						printResultados(rec.getNombre(), collection, doc);
					}
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}	
	}

	private void printResultados(String title, 
			List<ParticipanteEntity> collection, ApoiDocExport doc) {
		try{
			doc.AddTitle(title, 3);
			String table[][] = new String[collection.size()+1][3];
    		table[0][0]=Messages.get().getKey("clasificacion");
    		table[0][1]=Messages.get().getKey("patinador");
    		table[0][2]=Messages.get().getKey("club");
    		int j=1;
			// Process the List item
			for(ParticipanteEntity rec:collection){
				table[j][0]=""+rec.getClasificacion();
		    	table[j][1]=""+rec.getNombre() + " " + rec.getApellidos();
		    	table[j][2]=""+rec.getClubStr();
	    		j++;
			}
    		doc.AddTable(table);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	private String getExtension(String file){
		int i = file.lastIndexOf('.');
		if (i >= 0) {
		    return "."+file.substring(i+1);
		}
		return ".jpg";
	}
	
	/*// Add image
	if(rec.getImage()!=null && rec.getImage().length>10){
		String ext = getExtension(rec.getImagename());
		File file = File.createTempFile("temp_", ext);
		FileOutputStream fos = new FileOutputStream(file.getAbsolutePath());
		fos.write(rec.getImage());
		fos.close();
		doc.AddImage(file.getAbsolutePath(), figtitle, rec.getImagesize());
		file.delete();
	}*/
	
}
