package freewill.nextgen.blts.apoi;

import java.io.File;
import java.util.Date;
import java.util.List;

import freewill.nextgen.blts.daos.DeliverableRepository;
import freewill.nextgen.blts.daos.MappingRepository;
import freewill.nextgen.blts.daos.RequirementRepository;
import freewill.nextgen.blts.daos.StyleRepository;
import freewill.nextgen.blts.data.CompanyEntity;
import freewill.nextgen.blts.data.DeliverableEntity;
import freewill.nextgen.blts.data.MappingEntity;
import freewill.nextgen.blts.data.ProjectEntity;
import freewill.nextgen.blts.data.RequirementEntity;
import freewill.nextgen.common.Messages;

public class ReportDeliverables extends Report {
	
	@SuppressWarnings("deprecation")
	public ReportDeliverables(String name, String report, ProjectEntity prj,
			CompanyEntity cpy,
			RequirementRepository reqsrepo,
			MappingRepository mapsrepo,
			DeliverableRepository delivrepo,
			StyleRepository stylerepo){
		try{
			// Set Output File Name
			if(prj==null)
				return; // Throw Error
			String prjname = prj.getName();
			String filename = name+"-"+report+" ("+prjname+")-";
			
			// Create first a temporal file
			File tempFile = File.createTempFile(filename, ".docx");
			
			// Gets Company Template
			// Creates the Word Document
			Date date = new Date();
			ApoiDocExport doc = new ApoiDocExport(tempFile, cpy.getDocxtemplate(), 
					stylerepo.findByCompany(cpy.getID()));
			doc.Replace("###COMPANY###", cpy.getName());
			doc.Replace("###REPORT###", report);
			doc.Replace("###TITLE###", name);
			doc.Replace("###PROJECT###", prjname);
			doc.Replace("###DATE###", date.toLocaleString());
			
			// Gets the list of Deliverables
			doc.AddTitle(Messages.get().getKey("deliverablecrudview.viewname", System.getProperty("LOCALE")), 1);
			
			// Get data for the report
			List<DeliverableEntity> recs = delivrepo.findByCompanyAndProject(cpy.getID(), prj.getID());
			List<MappingEntity> maps = mapsrepo.findByProject(prj.getID());
			
			// Process the List item
			for(DeliverableEntity deliv : recs){
		       	// Add Paragraph with Deliverable
				String title = deliv.getName();
				String desc = deliv.getDescription();
					
				doc.AddTitle(title, 2);
				doc.AddParagraph(desc);
		    		
				// Fueron obtenidos fuera de este bucle
				for(MappingEntity map : maps){
			   		if(map.getDeliverable()==null) continue;
			   		if(map.getDeliverable().longValue()==deliv.getID().longValue()){
			   			//System.out.println("Added new paragraph");
			   			RequirementEntity req = reqsrepo.findById(map.getID());
				   		doc.AddParagraph(req.getCustomid(), true);
				   		doc.AddParagraph(req.getDescription());
				   		String txt = Messages.get().getKey("response", System.getProperty("LOCALE"))
				   				+": "+map.getResponse();
				   		doc.AddParagraph(txt, true);
				   		doc.AddParagraph(map.getText());
				   		/*// Also print additional image
				   		if(map.getImage()!=null && map.getImage().length>10){
				    		String ext = getExtension(map.getImagename()); 
				    		File file = File.createTempFile("temp_", ext);
				    		FileOutputStream fos = new FileOutputStream(file.getAbsolutePath());
				    		fos.write(map.getImage());
				    		fos.close();
					    	doc.AddImage(file.getAbsolutePath(), title);
					    	file.delete();
				    	}*/
			   		}
		    	}
			}
			
			// Close the Word Document
    		//doc.AddTitle("Indice del Documento",1);
			//doc.AddTOC();
			doc.AddParagraph("");
			doc.Replace("###PROJECT###", prjname);
			doc.CloseDocument();
			
			// Set internal variables
			setFile(tempFile);
			setSuccess(true);
		}
		catch(Exception e){
			e.printStackTrace();
			setSuccess(false);
		}
	}

	/*private String getExtension(String file){
		int i = file.lastIndexOf('.');
		if (i >= 0) {
		    return "."+file.substring(i+1);
		}
		return ".jpg";
	}*/
	
}
