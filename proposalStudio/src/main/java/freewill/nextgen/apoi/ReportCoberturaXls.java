package freewill.nextgen.apoi;

import java.io.File;
import java.util.List;
import java.util.Set;

import freewill.nextgen.proposalStudio.EntryPoint;
import freewill.nextgen.data.CompanyEntity;
import freewill.nextgen.data.FeatureEntity;
import freewill.nextgen.data.ProductEntity;
import freewill.nextgen.data.ProjectEntity;
import freewill.nextgen.data.Style.StyleEnum;
import freewill.nextgen.hmi.utils.Messages;
import com.vaadin.ui.ProgressBar;

public class ReportCoberturaXls extends Report {
	
	public ReportCoberturaXls(String name, String report, Long project, Set<Long> products, ProgressBar pb){
		try{
			// Set ProgressBar 
        	pb.setValue(0f);
			// Set Output File Name
        	ProjectEntity prj = projectLogic.findRecord(project);
			if(prj==null)
				return; // Throw Error
			String prjname = prj.getName();
			String filename = name+"-"+report+" ("+prjname+")-";
			int H1 = 1;
			int H2 = 1;
			int H3 = 1;
			int H4 = 1;
			int H5 = 1;
			
			// Create first a temporal file
			File tempFile = File.createTempFile(filename, ".xlsx");
			
			// Gets Company Template
			CompanyEntity cpy = EntryPoint.get().getAccessControl().getCompany();
			
			// Creates the Word Document
			ApoiXlsExport doc = new ApoiXlsExport(tempFile, cpy.getXlsxtemplate());
			doc.setHeader(report);
			
			// Print the headings
			Object data[] = new Object[4];
			float i=1;
			int j = 0;
			data[0] = Messages.get().getKey("product");
			data[1] = Messages.get().getKey("chapter");
        	data[2] = Messages.get().getKey("feature");
        	data[3] = "#"+Messages.get().getKey("requirement")+"s";
        	doc.addRow(j, data, true, true);
        	j++;
        	
        	for(Long product:products){
        		// Gets the list of Features
				List<FeatureEntity> recs = (List<FeatureEntity>) featureLogic.getFeaturesByProduct(product);
				System.out.println("Docs to print =  "+recs.size()+" product="+product);
				ProductEntity prd = productLogic.findRecord(product);
				
				// Process the List
				for(FeatureEntity rec : recs){
					// Set ProgressBar 
		        	pb.setValue((i/recs.size()));
		        	i = i + 1f;
		        	
		        	// Product Name
		        	data[0] = prd.getName();
		        	
		        	// Chapter
		        	String idx ="";
		        	String mtitle = rec.getTitle();
		        	StyleEnum mlevel = rec.getLevel();
		        	if(!rec.getTitle().equals("") && mlevel.ordinal()<=StyleEnum.H8.ordinal()){
		        		int level = mlevel.ordinal()+1;
					    if (level == 1){
					    	idx = ""+(H1++)+". ";
					    	H2 = 1;
					    	H3 = 1;
					    	H4 = 1;
					    	H5 = 1;
					    }
					    else if(level == 2){
					    	idx = ""+(H1-1)+"."+(H2++)+". ";
					    	H3 = 1;
					    	H4 = 1;
					    	H5 = 1;
					    }
					    else if(level == 3){
					    	idx = ""+(H1-1)+"."+(H2-1)+"."+(H3++)+". ";
					    	H4 = 1;
					    	H5 = 1;
					    }
					    else if(level == 4){
					    	idx = ""+(H1-1)+"."+(H2-1)+"."+(H3-1)+"."+(H4++)+". ";
					    	H5 = 1;
					    }
					    else{
					    	idx = ""+(H1-1)+"."+(H2-1)+"."+(H3-1)+"."+(H4-1)+"."+(H5++)+". ";
					    }
					}
					else if(mlevel == StyleEnum.PARAGRAM){
						idx = "•";
						mtitle = rec.getDescription();
					}
					else{
						idx = "";
						mtitle = rec.getDescription();
					}
		        	data[1] = idx;
		        	
		        	// Feature Title
		        	data[2] = mtitle;
		        	
		        	// Get # Mapped Requirements
	    			data[3] = mappingLogic.countMappingsByfeature(rec.getID());
	    			
	    			if(rec.getLevel().ordinal()<=StyleEnum.H8.ordinal())
	    	    		doc.addRow(j, data, false, true);
	    	    	else
	    	    		doc.addRow(j, data, false, false);
		        	j++;
				}	
        	}
			// Close the Word Document
			int[] widths = {-1,-1,80,-1,-1,-1}; // Column widths in characters
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
