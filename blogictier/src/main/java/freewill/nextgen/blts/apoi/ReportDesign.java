package freewill.nextgen.blts.apoi;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import freewill.nextgen.blts.data.ProductEntity;
import freewill.nextgen.blts.daos.FeatureRepository;
import freewill.nextgen.blts.daos.MappingRepository;
import freewill.nextgen.blts.daos.ProductRepository;
import freewill.nextgen.blts.daos.RequirementRepository;
import freewill.nextgen.blts.daos.StyleRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.CompanyEntity;
import freewill.nextgen.blts.data.FeatureEntity;
import freewill.nextgen.blts.data.MappingEntity;
import freewill.nextgen.blts.data.ProjectEntity;
import freewill.nextgen.blts.data.RequirementEntity;
import freewill.nextgen.blts.data.RequirementMapping;
import freewill.nextgen.blts.data.Style.StyleEnum;
import freewill.nextgen.blts.entities.UserEntity;
import freewill.nextgen.common.Messages;

public class ReportDesign extends Report {
	
	@SuppressWarnings("deprecation")
	public ReportDesign(String name, String report, ProjectEntity prj, 
			CompanyEntity cpy, Set<Long> products,
			FeatureRepository featrepo,
			ProductRepository productrepo,
			RequirementRepository reqsrepo,
			MappingRepository mapsrepo,
			UserRepository userrepo,
			StyleRepository stylerepo){
		try{
			// Set Output File Name
			if(products==null)
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
			
			for(Long product:products){
				ProductEntity prd = productrepo.findById(product);
				doc.AddTitle(prd.getName(), 1);
				
				// Gets the list of Features
				List<FeatureEntity> recs = new ArrayList<FeatureEntity>();
				getfeatbyparent(product, 0L, recs, featrepo);
				System.out.println("Docs to print =  "+recs.size()+" product="+product);
				
				// Process the List item
				for(FeatureEntity rec : recs){
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
		    		List<MappingEntity> maps = mapsrepo.findByDoc(rec.getID());
			    	for(MappingEntity map : maps){
			    		RequirementEntity req = reqsrepo.findById(map.getReq());
			    		if(req!=null){
			    			// Print:      customid   response   explanation
				    		String txt = "("+req.getCustomid()+":"+map.getResponse()+") "+map.getText();
				    		doc.AddParagraph(txt);
			    		}
			    		// Also print additional image
			    		if(map.getImage()!=null && map.getImage().length>10){
				    		String ext = getExtension(map.getImagename()); 
				    		File file = File.createTempFile("temp_", ext);
				    		FileOutputStream fos = new FileOutputStream(file.getAbsolutePath());
				    		fos.write(map.getImage());
				    		fos.close();
					    	doc.AddImage(file.getAbsolutePath(), figtitle, 1.0);
					    	file.delete();
			    		}
		    		}
				}
			}
			
			// Get data for the TOC
			List<RequirementMapping> reqsmapped = getmapsbyproject(
					prj.getID(), cpy.getID(), 
					reqsrepo, mapsrepo, userrepo, featrepo, productrepo);
			
			// Add the Table Of Compliance (TOC)
			doc.AddTitle(Messages.get().getKey("TOC", System.getProperty("LOCALE")), 1);
    		doc.AddParagraph("");
			String table[][] = new String[reqsmapped.size()+1][5];
    		table[0][0]=Messages.get().getKey("reqid", System.getProperty("LOCALE"));
    		table[0][1]=Messages.get().getKey("reqdesc", System.getProperty("LOCALE"));
    		table[0][2]=Messages.get().getKey("response", System.getProperty("LOCALE"));
    		table[0][3]=Messages.get().getKey("mappedto", System.getProperty("LOCALE"));
    		table[0][4]=Messages.get().getKey("product", System.getProperty("LOCALE"));
    		int j=1;
	    	for(RequirementMapping map : reqsmapped){
	    		table[j][0]=""+map.getCustomid();
		    	table[j][1]=""+map.getDescription();
		    	table[j][2]=""+map.getResponse();
		    	table[j][3]=""+map.getMapping();
		    	table[j][4]=""+map.getProduct();
	    		j++;
	    	}
    		doc.AddTable(table);
			
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
	
	private void getfeatbyparent(Long prd, Long parent, List<FeatureEntity> output,
			FeatureRepository repo) throws Exception {
		List<FeatureEntity> recs = repo.findByProductAndParentOrderByIdAsc(prd, parent);
		for(FeatureEntity rec : recs){
			output.add(rec);
			getfeatbyparent(prd, rec.getID(), output, repo);
		}
	}
	
	public List<RequirementMapping> getmapsbyproject(Long recId, Long cpy, 
			RequirementRepository reqsrepo,
			MappingRepository mapsrepo,
			UserRepository userrepo,
			FeatureRepository featrepo,
			ProductRepository productrepo) throws Exception {
		System.out.println("Getting Entire Requirement Mapping List By Project ...");
		
		List<RequirementEntity> reqs = reqsrepo.findByCompanyAndProjectOrderByCustomidAsc(cpy, recId);
		
		List<RequirementMapping> recs = new ArrayList<RequirementMapping>();
		
		for(RequirementEntity req:reqs){
			RequirementMapping rec = new RequirementMapping();
			rec.setId(req.getID());
			rec.setCustomid(req.getCustomid());
			rec.setDescription(req.getDescription());
			rec.setResolved(req.getResolved());
			rec.setType(req.getType());
			rec.setCategory(req.getCategory());
			rec.setUser("");
			rec.setResponse("");
			rec.setDoc(-1L);
			rec.setText("");
			rec.setLaboreffort(0);
			rec.setTotalcost(0);
			rec.setNotes("");
			rec.setMapping("");
			rec.setProduct("");
			
			UserEntity user2 = userrepo.findById(req.getAssignedto());
			if(user2!=null)
				rec.setUser(user2.getName());
			
			MappingEntity map = mapsrepo.findByReq(req.getID());
			if(map!=null){
				rec.setResponse(map.getResponse());
				rec.setDoc(map.getDoc());
				rec.setText(map.getText());
				rec.setLaboreffort(map.getLaboreffort());
				rec.setTotalcost(map.getTotalcost());
				rec.setNotes(map.getNotes());
				
				if(map.getDoc()!=null && map.getDoc()>0L){
					FeatureEntity feat = featrepo.findById(map.getDoc());
					if(feat!=null){
						rec.setMapping(feat.getTitle());
						// Just in case Title is empty, go to Parent Feature
			        	if(feat.getTitle().equals("") && feat.getParent()!=null && feat.getParent()>0L){
			        		FeatureEntity fp = featrepo.findById(feat.getParent());
			        		rec.setMapping(fp.getTitle());
			        	}
			        	ProductEntity product = productrepo.findById(feat.getProduct());
			        	if(product!=null)
			        		rec.setProduct(product.getName());
					}
				}
			}
			recs.add(rec);
		}
		return recs;
	}
	
}
