package freewill.nextgen.blts.apoi;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import freewill.nextgen.blts.daos.FeatureRepository;
import freewill.nextgen.blts.daos.ProductRepository;
import freewill.nextgen.blts.data.CompanyEntity;
import freewill.nextgen.blts.data.FeatureEntity;
import freewill.nextgen.blts.data.ProductEntity;
import freewill.nextgen.blts.data.ProjectEntity;
import freewill.nextgen.blts.data.Style.StyleEnum;

public class ReportFuncionalXls extends Report {
	
	public ReportFuncionalXls(String name, String report, ProjectEntity prj, 
			CompanyEntity cpy, Set<Long> products,
			FeatureRepository featrepo,
			ProductRepository productrepo){
		try{
			// Set Output File Name
			if(products==null)
				return; // Throw Error
			String filename = name+"-"+report+"-";
			
			// Create first a temporal file
			File tempFile = File.createTempFile(filename, ".xlsx");
			
			// Gets Company Template
			// Creates the Excel Document
			ApoiXlsExport doc = new ApoiXlsExport(tempFile, cpy.getXlsxtemplate());
			doc.setHeader(report);
			
			// Print the headings
			Object data[] = new Object[7];
			int j = 0;
			data[0] = "product";
			data[1] = "level";
        	data[2] = "title";
        	data[3] = "description";
        	data[4] = "active";
        	data[5] = "id";
        	data[6] = "parent";
        	doc.addRow(j, data, true, true);
        	j++;
			
			for(Long product:products){
				ProductEntity prd = productrepo.findById(product);
				
				// Gets the list of Features
				List<FeatureEntity> recs = new ArrayList<FeatureEntity>();
				getlistbyparent(product, 0L, recs, featrepo);
				System.out.println("Docs to print =  "+recs.size()+" product="+product);
				
				// Process the List item
				for(FeatureEntity rec : recs){
					// product name
		        	data[0] = prd.getName();
		        	// level
		        	data[1] = ""+rec.getLevel().toString(); //.ordinal()+1;
		        	// Feature Title
		        	data[2] = rec.getTitle();
		        	// description
	    			data[3] = rec.getDescription();
	    			// active
	    			data[4] = rec.getActive();
	    			// Id
	    	    	data[5] = rec.getID();
	    	    	// parent
	    	    	data[6] = rec.getParent();
	    			
	    	    	if(rec.getLevel().ordinal()<=StyleEnum.H8.ordinal())
	    	    		doc.addRow(j, data, false, true);
	    	    	else
	    	    		doc.addRow(j, data, false, false);
		        	j++;
				}
			}
			// Close the Word Document
			int[] widths = {-1,-1,60,80,-1,-1,-1}; // Column widths in characters
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

	private void getlistbyparent(Long prd, Long parent, List<FeatureEntity> output,
			FeatureRepository repo) throws Exception {
		List<FeatureEntity> recs = repo.findByProductAndParentOrderByIdAsc(prd, parent);
		for(FeatureEntity rec : recs){
			output.add(rec);
			getlistbyparent(prd, rec.getID(), output, repo);
		}
	}
	
}
