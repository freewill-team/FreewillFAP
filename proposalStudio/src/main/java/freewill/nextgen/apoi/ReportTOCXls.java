package freewill.nextgen.apoi;

import java.io.File;
import java.util.List;

import freewill.nextgen.proposalStudio.EntryPoint;
import freewill.nextgen.data.CompanyEntity;
import freewill.nextgen.data.ProjectEntity;
import freewill.nextgen.data.RequirementMapping;
import freewill.nextgen.data.RequirementEntity.ReqTypeEnum;
import freewill.nextgen.hmi.utils.Messages;
import com.vaadin.ui.ProgressBar;

public class ReportTOCXls extends Report {
	
	public ReportTOCXls(String name, String report, Long project, ProgressBar pb, boolean extended){
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
			File tempFile = File.createTempFile(filename, ".xlsx");
			
			// Gets the list of Requirements
			//List<RequirementEntity> recs = (List<RequirementEntity>) 
			//		requirementLogic.getRequirementsByProject(project);
			//System.out.println("Requirements to process = "+recs.size());
			List<RequirementMapping> recs = (List<RequirementMapping>) 
					mappingLogic.getRequirementsMappedByProject(project);
			
			// Gets Company Template
			CompanyEntity cpy = EntryPoint.get().getAccessControl().getCompany();
						
			// Creates the Word Document
			ApoiXlsExport doc = new ApoiXlsExport(tempFile, cpy.getXlsxtemplate());
			doc.setHeader(report);
			
			// Set the headings
			Object data[] = null;
			if(extended)
				data = new Object[6];
			else
				data = new Object[5];
			float i = 1;
			int j = 0;
			data[0] = Messages.get().getKey("reqid");
        	data[1] = Messages.get().getKey("reqdesc");
        	data[2] = Messages.get().getKey("response");
        	data[3] = Messages.get().getKey("mappedto");
        	data[4] = Messages.get().getKey("product");
        	if(extended)
        		data[5] = Messages.get().getKey("explanation");
        	doc.addRow(j, data, true, true);
        	j++;
        	// Process the List
        	//for(RequirementEntity rec : recs){
        	for(RequirementMapping rec : recs){	
				// Set ProgressBar 
	        	pb.setValue((i/recs.size()));
	        	i = i + 1f;
	        	
	        	data[2] = rec.getResponse();
	        	data[3] = rec.getMapping();
	        	data[4] = rec.getProduct();
	        	if(extended){
	        		data[5] = rec.getText();
	        	}
	        	
	        	data[0] = rec.getCustomid();
	        	data[1] = rec.getDescription();
	        	
	        	/*if( rec.getType()==ReqTypeEnum.TITLE ){
	        		data[2] = "";
		        	data[3] = "";
		        	data[4] = "";
		        	if(extended) data[5] = "";
	        	}
	        	else { //ReqTypeEnum.REQ
	        		MappingEntity map = mappingLogic.getMappingByReq(rec.getID());
		        	if(map!=null){
		        		FeatureEntity feat = featureLogic.findRecord(map.getDoc());
		        		ProductEntity product = productLogic.findRecord(feat.getProduct());
		        		if(doc==null || product==null){
		        			data[2] = "";
				        	data[3] = "";
				        	data[4] = "";
				        	if(extended) data[5] = "";
		        		}
		        		else {
				        	data[2] = map.getResponse();
				        	data[3] = feat.getTitle();
				        	data[4] = product.getName();
				        	if(extended){
				        		// 19-03-2018 decidí que solo mostraríamos los comentarios adicionales
				        		// data[5] = doc.getDescription() + " " + map.getText();
				        		data[5] = map.getText();
				        	}
				        	
				        	// Just in case Title is empty, go to Parent Feature
				        	if(feat.getTitle().equals("") && feat.getParent()!=null && feat.getParent()>0L){
				        		feat = featureLogic.findRecord(feat.getParent());
				        		data[3] = feat.getTitle();
				        	}
		        		}
		        	}
		        	else{
		        		data[2] = "";
			        	data[3] = "";
			        	data[4] = "";
			        	if(extended) data[5] = ""; 
		        	}
	        	}*/
	        	
	        	if(rec.getType()==ReqTypeEnum.TITLE)
					doc.addRow(j, data, false, true);
				else
					doc.addRow(j, data, false, false);
	        	j++;
			}
			
			// Close the Word Document
			int[] widths = {-1,80,-1,-1,-1,80}; // Column widths in characters
			doc.CloseDocument(widths);
			
			// Set internal variables
			setFile(tempFile);
			setSuccess(true);
		}
		catch(Exception e){
			e.printStackTrace();
			setSuccess(false);
		}
	}

}
