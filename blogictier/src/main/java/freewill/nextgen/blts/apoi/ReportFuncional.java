package freewill.nextgen.blts.apoi;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import freewill.nextgen.blts.daos.FeatureRepository;
import freewill.nextgen.blts.daos.ProductRepository;
import freewill.nextgen.blts.daos.StyleRepository;
import freewill.nextgen.blts.data.CompanyEntity;
import freewill.nextgen.blts.data.FeatureEntity;
import freewill.nextgen.blts.data.ProductEntity;
import freewill.nextgen.blts.data.ProjectEntity;
import freewill.nextgen.blts.data.Style.StyleEnum;

public class ReportFuncional extends Report {
	
	@SuppressWarnings("deprecation")
	public ReportFuncional(String name, String report, ProjectEntity prj, 
			CompanyEntity cpy, Set<Long> products, 
			FeatureRepository featrepo,
			ProductRepository productrepo,
			StyleRepository stylerepo){
		try{
			// Set Output File Name
			if(products==null)
				return;
			String filename = name+"-"+report+"-";
			
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
			doc.Replace("###DATE###", date.toLocaleString());
			
			for(Long product:products){
				ProductEntity prd = productrepo.findById(product);
				doc.AddTitle(prd.getName(), 1);
				
				// Gets the list of Features
				List<FeatureEntity> recs = new ArrayList<FeatureEntity>();
				getlistbyparent(product, 0L, recs, featrepo);
				System.out.println("Docs to print =  "+recs.size()+" product="+product);
				
				// Process the List item
				for(FeatureEntity rec : recs){
					if(!rec.getActive()) continue; // ignore no-active records
					
		        	// Add Paragraph with Feature
		        	StyleEnum level = rec.getLevel();
					String title = rec.getTitle();
					String figtitle = rec.getDescription();
					String desc = rec.getDescription();
					
					if(!title.equals("") && level.ordinal()<=StyleEnum.H8.ordinal()){
						doc.AddTitle(title, level.ordinal()+2);
						figtitle = title;
					}
					else if(level == StyleEnum.PARAGRAM){
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
				}
			}
			
			// Close the Word Document
    		//doc.AddTitle("Indice del Documento",1);
			//doc.AddTOC();
			doc.AddParagraph("");
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
	
	private void getlistbyparent(Long prd, Long parent, List<FeatureEntity> output,
			FeatureRepository repo) throws Exception {
		List<FeatureEntity> recs = repo.findByProductAndParentOrderByIdAsc(prd, parent);
		for(FeatureEntity rec : recs){
			output.add(rec);
			getlistbyparent(prd, rec.getID(), output, repo);
		}
	}
	
}