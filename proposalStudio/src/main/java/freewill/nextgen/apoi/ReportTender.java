package freewill.nextgen.apoi;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;
import java.util.Set;

import freewill.nextgen.data.ProductEntity;
import freewill.nextgen.proposalStudio.EntryPoint;
import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.data.CompanyEntity;
import freewill.nextgen.data.FeatureEntity;
import freewill.nextgen.data.MappingEntity;
import freewill.nextgen.data.ProjectEntity;
import freewill.nextgen.data.RequirementEntity;
import freewill.nextgen.data.Style;
import freewill.nextgen.data.Style.StyleEnum;
import freewill.nextgen.hmi.utils.Messages;

import com.vaadin.ui.ProgressBar;

public class ReportTender extends Report {
	
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
	public ReportTender(String name, String report, Long project, Set<Long> products, ProgressBar pb){
		try{
			// Set ProgressBar 
        	pb.setValue(0f);
			// Set Output File Name
        	ProjectEntity prj = projectLogic.findRecord(project);
			if(prj==null)
				return; // Throw Error
			String prjname = prj.getName();
			String filename = name+"-"+report+" ("+prjname+")-";
			
			// Create first a temporal file
			File tempFile = File.createTempFile(filename, ".docx");
			
			// Gets Company Template
			CompanyEntity cpy = EntryPoint.get().getAccessControl().getCompany();
						
			// Creates the Word Document
			Date date = new Date();
			ApoiDocExport doc = new ApoiDocExport(tempFile, cpy.getDocxtemplate(), getStyles());
			doc.Replace("###COMPANY###", cpy.getName());
			doc.Replace("###REPORT###", report);
			doc.Replace("###TITLE###", name);
			doc.Replace("###PROJECT###", prjname);
			doc.Replace("###DATE###", date.toLocaleString());
			
			for(Long product:products){
				// Gets the list of Features
				List<FeatureEntity> recs = (List<FeatureEntity>) featureLogic.getFeaturesByProduct(product);
				System.out.println("Docs to print =  "+recs.size()+" product="+product);
				ProductEntity prd = productLogic.findRecord(product);
				doc.AddTitle(prd.getName(), 1);
			
				// Process the List item
				float i=1;
				for(FeatureEntity rec : recs){
					// Set ProgressBar
		        	pb.setValue((i/recs.size()));
		        	i = i + 1f;
		        	if(!rec.getActive()) continue; // ignore no-active records
		        	
		        	// Add Paragraph with Feature
		        	StyleEnum level = rec.getLevel();
					String title = rec.getTitle();
					String figtitle = rec.getDescription();
					String desc = rec.getDescription();
					
					if(level.ordinal()<=StyleEnum.H8.ordinal()){
						doc.AddTitle(title, level.ordinal()+2);
						figtitle = title;
					}
					else if(level == StyleEnum.PARAGRAM){
						//desc = "• " + desc;
						doc.AddBullet(desc);
						desc = "";
					}
					else if(level == StyleEnum.NORMAL){
						// do nothing
					}
					else if(level == StyleEnum.FIGURE){
						// do nothing
						desc = "";
					}
					else{
						// do nothing
					}
					
					if(!desc.equals(""))
						doc.AddParagraph(desc);
						
		    		// Add image
		    		if(rec.getImage()!=null && rec.getImage().length>10){
		    			String ext = getExtension(rec.getImagename());
		    			File file = File.createTempFile("temp_", ext);
		    			FileOutputStream fos = new FileOutputStream(file.getAbsolutePath());
		    			fos.write(rec.getImage());
		    			fos.close();
			    		doc.AddImage(file.getAbsolutePath(), figtitle, rec.getImagesize());
			    		file.delete();
		    		}
		    			
		    		// Get Mapped Requirements
		    		List<MappingEntity> maps = (List<MappingEntity>) mappingLogic.getMappingByFeature(rec.getID());
		    		if(maps.size()>0){
			    		String table[][] = new String[maps.size()+1][4];
			    		table[0][0]=Messages.get().getKey("reqid");
			    		table[0][1]=Messages.get().getKey("reqdesc");
			    		table[0][2]=Messages.get().getKey("response");
			    		table[0][3]=Messages.get().getKey("explanation");
			    		int j=1;
				    	for(MappingEntity map : maps){
				    		RequirementEntity req = requirementLogic.findRecord(map.getReq());
				    		if(req!=null){
				    			table[j][0]=""+req.getCustomid();
					    		table[j][1]=""+req.getDescription();
					    		table[j][2]=""+map.getResponse();
					    		table[j][3]=""+map.getText();
				    			j++;
				    		}
				    	}
			    		// Add a table with a summary of mapped requirements
				    	doc.AddParagraph("");
			    		doc.AddParagraph(Messages.get().getKey("requirement")+"s:", true);
			    		doc.AddTable(table);
			    		doc.AddParagraph("");
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

	private String getExtension(String file){
		int i = file.lastIndexOf('.');
		if (i >= 0) {
		    return "."+file.substring(i+1);
		}
		return ".jpg";
	}
	
}
