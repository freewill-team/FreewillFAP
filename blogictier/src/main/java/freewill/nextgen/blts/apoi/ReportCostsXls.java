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
import freewill.nextgen.blts.data.RequirementEntity.ReqTypeEnum;
import freewill.nextgen.blts.entities.UserEntity;
import freewill.nextgen.blts.data.RequirementMapping;
import freewill.nextgen.common.Messages;

public class ReportCostsXls extends Report {
	
	public ReportCostsXls(String name, String report, ProjectEntity prj, 
			CompanyEntity cpy,
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
			Object data[] = new Object[5];
			float totlabor=0, totcosts=0;
			int j = 0;
			data[0] = Messages.get().getKey("reqid", System.getProperty("LOCALE"));
        	data[1] = Messages.get().getKey("reqdesc", System.getProperty("LOCALE"));
			data[2] = Messages.get().getKey("laboreffort", System.getProperty("LOCALE"));
			data[3] = Messages.get().getKey("laborcost", System.getProperty("LOCALE"));
			data[4] = Messages.get().getKey("notes", System.getProperty("LOCALE"));
        	doc.addRow(j, data, true, true);
        	j++;
        	
        	// Get data for the report
        	List<RequirementMapping> recs = getmapsbyproject(
        			prj.getID(), cpy.getID(), 
        			reqsrepo, mapsrepo, userrepo, featrepo, productrepo);
        	
        	// Process the List
			for(RequirementMapping rec : recs){
		       	//if(rec.getLaboreffort()==0.0f) continue;
		       	totlabor+=rec.getLaboreffort();
		       	totcosts+=rec.getTotalcost();
		       	data[0] = rec.getCustomid();
		       	data[1] = rec.getDescription();
				data[2] = rec.getLaboreffort();
				data[3] = rec.getTotalcost();
				data[4] = rec.getNotes();
					
				if(rec.getType()==ReqTypeEnum.TITLE)
					doc.addRow(j, data, false, true);
				else
					doc.addRow(j, data, false, false);
		        j++;
			}
			data[0] = "";
        	data[1] = Messages.get().getKey("totals", System.getProperty("LOCALE"));
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
			        	ProductEntity product = productrepo.findById(feat.getID());
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
