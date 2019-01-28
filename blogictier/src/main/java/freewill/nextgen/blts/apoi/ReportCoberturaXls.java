package freewill.nextgen.blts.apoi;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import freewill.nextgen.blts.daos.FeatureRepository;
import freewill.nextgen.blts.daos.MappingRepository;
import freewill.nextgen.blts.daos.ProductRepository;
import freewill.nextgen.blts.daos.RequirementRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.CompanyEntity;
import freewill.nextgen.blts.data.FeatureEntity;
import freewill.nextgen.blts.data.ProductEntity;
import freewill.nextgen.blts.data.ProjectEntity;
import freewill.nextgen.blts.data.Style.StyleEnum;
import freewill.nextgen.common.Messages;

public class ReportCoberturaXls extends Report {
	
	public ReportCoberturaXls(String name, String report, ProjectEntity prj, 
			CompanyEntity cpy, Set<Long> products,
			FeatureRepository featrepo,
			ProductRepository productrepo,
			RequirementRepository reqsrepo,
			MappingRepository mapsrepo,
			UserRepository userrepo){
		try{
			// Set Output File Name
			if(products==null)
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
			
			// Creates the Word Document
			ApoiXlsExport doc = new ApoiXlsExport(tempFile, cpy.getXlsxtemplate());
			doc.setHeader(report);
			
			// Print the headings
			Object data[] = new Object[4];
			int j = 0;
			data[0] = Messages.get().getKey("product", System.getProperty("LOCALE"));
			data[1] = Messages.get().getKey("chapter", System.getProperty("LOCALE"));
        	data[2] = Messages.get().getKey("feature", System.getProperty("LOCALE"));
        	data[3] = "#"+Messages.get().getKey("requirement", System.getProperty("LOCALE"))+"s";
        	doc.addRow(j, data, true, true);
        	j++;
        	
        	for(Long product:products){
        		ProductEntity prd = productrepo.findById(product);
				
				// Gets the list of Features
				List<FeatureEntity> recs = new ArrayList<FeatureEntity>();
				getlistbyparent(product, 0L, recs, featrepo);
				System.out.println("Docs to print =  "+recs.size()+" product="+product);
				
        		// Process the List
				for(FeatureEntity rec : recs){
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
	    			data[3] = mapsrepo.countByDoc(rec.getID());
	    			
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

	private void getlistbyparent(Long prd, Long parent, List<FeatureEntity> output,
			FeatureRepository repo) throws Exception {
		List<FeatureEntity> recs = repo.findByProductAndParentOrderByIdAsc(prd, parent);
		for(FeatureEntity rec : recs){
			output.add(rec);
			getlistbyparent(prd, rec.getID(), output, repo);
		}
	}
	
}
