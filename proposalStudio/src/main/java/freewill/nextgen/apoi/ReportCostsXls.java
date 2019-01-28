package freewill.nextgen.apoi;

import java.io.File;
import java.util.List;

import freewill.nextgen.proposalStudio.EntryPoint;
import freewill.nextgen.data.CompanyEntity;
import freewill.nextgen.data.MappingEntity;
import freewill.nextgen.data.ProjectEntity;
import freewill.nextgen.data.RequirementEntity;
import freewill.nextgen.data.RequirementEntity.ReqTypeEnum;
import freewill.nextgen.hmi.utils.Messages;
import com.vaadin.ui.ProgressBar;

public class ReportCostsXls extends Report {
	
	public ReportCostsXls(String name, String report, Long project, ProgressBar pb){
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
			List<RequirementEntity> recs = (List<RequirementEntity>) requirementLogic.getRequirementsByProject(project);
			System.out.println("Requirements to process = "+recs.size());
			
			// Gets Company Template
			CompanyEntity cpy = EntryPoint.get().getAccessControl().getCompany();
						
			// Creates the Word Document
			ApoiXlsExport doc = new ApoiXlsExport(tempFile, cpy.getXlsxtemplate());
			doc.setHeader(report);
			
			// Process the List
			//String data[] = new String[4];
			Object data[] = new Object[5];
			float i=1, totlabor=0, totcosts=0;
			int j = 0;
			data[0] = Messages.get().getKey("reqid");
        	data[1] = Messages.get().getKey("reqdesc");
			data[2] = Messages.get().getKey("laboreffort");
			data[3] = Messages.get().getKey("laborcost");
			data[4] = Messages.get().getKey("notes");
        	doc.addRow(j, data, true, true);
        	j++;
			for(RequirementEntity rec : recs){
				// Set ProgressBar 
	        	pb.setValue((i/recs.size()));
	        	i = i + 1f;
	        	
	        	MappingEntity map = mappingLogic.getMappingByReq(rec.getID());
	        	if(map!=null){
		        	//if(rec.getLaboreffort()==0.0f) continue;
		        	totlabor+=(float)map.getLaboreffort();
		        	totcosts+=(float)map.getTotalcost();
		        	data[0] = rec.getCustomid();
		        	data[1] = rec.getDescription();
					data[2] = map.getLaboreffort();
					data[3] = map.getTotalcost();
					data[4] = map.getNotes();
					
					if(rec.getType()==ReqTypeEnum.TITLE)
						doc.addRow(j, data, false, true);
					else
						doc.addRow(j, data, false, false);
		        	j++;
	        	}
			}
			data[0] = "";
        	data[1] = Messages.get().getKey("totals");
        	data[2] = totlabor; //"=SUMA(C1:C"+(j-1)+")";
        	data[3] = totcosts; //"=SUMA(D1:D"+(j-1)+")";
        	data[4] = "";
        	doc.addRow(j, data, false, true);
        	
			// Close the Word Document
        	int[] widths = {-1,80,-1,-1,60,-1}; // Column widths in characters
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
