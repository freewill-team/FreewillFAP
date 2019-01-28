package freewill.nextgen.blts.apoi;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import freewill.nextgen.blts.daos.FeatureRepository;
import freewill.nextgen.blts.daos.MappingRepository;
import freewill.nextgen.blts.daos.ProductRepository;
import freewill.nextgen.blts.daos.RequirementRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.CompanyEntity;
import freewill.nextgen.blts.data.FeatureEntity;
import freewill.nextgen.blts.data.MappingEntity;
import freewill.nextgen.blts.data.ProductEntity;
import freewill.nextgen.blts.data.ProjectEntity;
import freewill.nextgen.blts.data.RequirementEntity;
import freewill.nextgen.blts.data.RequirementMapping;
import freewill.nextgen.common.Messages;
import freewill.nextgen.blts.data.RequirementEntity.ReqTypeEnum;
import freewill.nextgen.blts.entities.UserEntity;

public class ReportTOCXls extends Report {
	
	public ReportTOCXls(String name, String report, ProjectEntity prj, 
			CompanyEntity cpy, boolean extended,
			FeatureRepository featrepo,
			ProductRepository productrepo,
			RequirementRepository reqsrepo,
			MappingRepository mapsrepo,
			UserRepository userrepo){
		try{
			// Set Output File Name
			String prjname = prj.getName();
			String filename = name+"-"+report+" ("+prjname+")-";
			
			// Create first a temporal file
			File tempFile = File.createTempFile(filename, ".xlsx");
			
			// Gets Company Template	
			// Creates the Word Document
			ApoiXlsExport doc = new ApoiXlsExport(tempFile, cpy.getXlsxtemplate());
			doc.setHeader(report);
			
			// Set the headings
			Object data[] = null;
			if(extended)
				data = new Object[6];
			else
				data = new Object[5];
			int j = 0;
			data[0] = Messages.get().getKey("reqid", System.getProperty("LOCALE"));
        	data[1] = Messages.get().getKey("reqdesc", System.getProperty("LOCALE"));
        	data[2] = Messages.get().getKey("response", System.getProperty("LOCALE"));
        	data[3] = Messages.get().getKey("mappedto", System.getProperty("LOCALE"));
        	data[4] = Messages.get().getKey("product", System.getProperty("LOCALE"));
        	if(extended)
        		data[5] = Messages.get().getKey("explanation", System.getProperty("LOCALE"));
        	doc.addRow(j, data, true, true);
        	j++;
        	
        	// Get data for the report
        	List<RequirementMapping> recs = getmapsbyproject(
        			prj.getID(), cpy.getID(), 
        			reqsrepo, mapsrepo, userrepo, featrepo, productrepo);
        	
        	// Process the List
        	for(RequirementMapping rec : recs){	
	        	data[0] = rec.getCustomid();
	        	data[1] = rec.getDescription();
	        	data[2] = rec.getResponse();
	        	data[3] = rec.getMapping();
	        	data[4] = rec.getProduct();
	        	if(extended){
	        		data[5] = rec.getText();
	        	}
	        	
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
