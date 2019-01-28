package freewill.nextgen.apoi;

import java.io.File;
import java.util.Date;
import java.util.List;

import freewill.nextgen.proposalStudio.EntryPoint;
import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.data.CompanyEntity;
import freewill.nextgen.data.DeliverableEntity;
import freewill.nextgen.data.MappingEntity;
import freewill.nextgen.data.ProjectEntity;
import freewill.nextgen.data.RequirementEntity;
import freewill.nextgen.data.Style;
import freewill.nextgen.hmi.utils.Messages;
import com.vaadin.ui.ProgressBar;

public class ReportDeliverables extends Report {
	
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
	public ReportDeliverables(String name, String report, Long project, ProgressBar pb){
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
			
			// Gets the list of Deliverables
			List<DeliverableEntity> recs = (List<DeliverableEntity>) deliverableLogic.getDeliverablesByProject(project);
			System.out.println("Deliverables to print = "+recs.size()+" project = "+project);
			doc.AddTitle(Messages.get().getKey("deliverablecrudview.viewname"), 1);
			
			// Get Mapped Requirements
			List<RequirementEntity> reqs = (List<RequirementEntity>) requirementLogic.getRequirementsByProject(project);
			System.out.println("Requirements to process = "+reqs.size());
			
			// Process the List item
			float i=1;
			for(DeliverableEntity deliv : recs){
				// Set ProgressBar
		       	pb.setValue(i/(recs.size()*reqs.size()));
		        	
		       	// Add Paragraph with Deliverable
				String title = deliv.getName();
				String desc = deliv.getDescription();
					
				doc.AddTitle(title, 2);
				doc.AddParagraph(desc);
		    		
				// Fueron obtenidos fuera de este bucle
				for(RequirementEntity req : reqs){
					i = i + 1f;
					
			   		MappingEntity map = mappingLogic.getMappingByReq(req.getID());
			   		if(map==null) continue;
			   		//System.out.println("Mapping to process = "+map.getID()+" Deliverable = "+map.getDeliverable());
			   		//System.out.println("Comparing with = "+deliv.getID());
			   		if(map.getDeliverable()==null) continue;
			   		if(map.getDeliverable().longValue()==deliv.getID().longValue()){
			   			//System.out.println("Added new paragraph");
				   		doc.AddParagraph(req.getCustomid(), true);
				   		doc.AddParagraph(req.getDescription());
				   		String txt = Messages.get().getKey("response")+": "+map.getResponse();
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
			   		//else
			   		//	System.out.println("Different");
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
