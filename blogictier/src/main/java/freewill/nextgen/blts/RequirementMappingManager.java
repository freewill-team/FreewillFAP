package freewill.nextgen.blts;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import freewill.nextgen.blts.daos.FeatureRepository;
import freewill.nextgen.blts.daos.MappingRepository;
import freewill.nextgen.blts.daos.ProductRepository;
import freewill.nextgen.blts.daos.RequirementRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.FeatureEntity;
import freewill.nextgen.blts.data.MappingEntity;
import freewill.nextgen.blts.data.ProductEntity;
import freewill.nextgen.blts.data.RequirementEntity;
import freewill.nextgen.blts.data.RequirementMapping;
import freewill.nextgen.blts.entities.UserEntity;

/** 
 * Requirement:   RequirementMappingManager.java
 * Date:   21/10/2018
 * Author: Benito Vela
 * Refs:   None
 * 
 * This static class provides the business logic to manage RequirementMapping
 * (getbyproject).
 * 
**/

@RestController
@RequestMapping("/RequirementMapping")
public class RequirementMappingManager {
	
	@Autowired
	RequirementRepository repository;
	
	@Autowired
	MappingRepository mappingrepo;
	
	@Autowired
	FeatureRepository featrepo;
	
	@Autowired
	ProductRepository productrepo;
	
	@Autowired
	UserRepository userrepo;
	
	@RequestMapping("/getbyproject/{recId}")
	public List<RequirementMapping> getbyproject(@PathVariable Long recId) throws Exception {
		System.out.println("Getting Entire Requirement Mapping List By Project ...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		
		List<RequirementEntity> reqs = (List<RequirementEntity>)
				repository.findByCompanyAndProjectOrderByCustomidAsc(user.getCompany(), recId);
		
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
			rec.setProject(req.getProject());
			
			UserEntity user2 = userrepo.findById(req.getAssignedto());
			if(user2!=null)
				rec.setUser(user2.getName());
			
			MappingEntity map = mappingrepo.findByReq(req.getID());
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

	@RequestMapping("/getbyfeature/{recId}")
	public List<RequirementMapping> getbyfeature(@PathVariable Long recId) throws Exception {
		System.out.println("Getting Requirement Mapping List By Feature ...");
		//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//UserEntity user = userrepo.findByLoginname(auth.getName());
		
		List<MappingEntity> maps = mappingrepo.findByDoc(recId);
		
		List<RequirementMapping> recs = new ArrayList<RequirementMapping>();
		
		for(MappingEntity map:maps){
			RequirementMapping rec = new RequirementMapping();
			rec.setId(map.getReq());
			rec.setDoc(-1L);
			rec.setUser("");
			rec.setResponse(map.getResponse());
			rec.setText(map.getText());
			rec.setLaboreffort(map.getLaboreffort());
			rec.setTotalcost(map.getTotalcost());
			rec.setNotes(map.getNotes());
			rec.setMapping("");
			rec.setProduct("");
			rec.setProject(-1L);
			
			RequirementEntity req = repository.findById(map.getReq());
			if(req!=null){
				rec.setCustomid(req.getCustomid());
				rec.setDescription(req.getDescription());
				rec.setResolved(req.getResolved());
				rec.setType(req.getType());
				rec.setCategory(req.getCategory());
				rec.setProject(req.getProject());
			
				UserEntity user2 = userrepo.findById(req.getAssignedto());
				if(user2!=null)
					rec.setUser(user2.getName());
			}
			
			if(map.getDoc()!=null && map.getDoc()>0L){
				FeatureEntity feat = featrepo.findById(map.getDoc());
				if(feat!=null){
					rec.setDoc(map.getDoc());
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
			
			recs.add(rec);
		}
		return recs;
	}
	
	@RequestMapping("/getmyreqsbyproject/{recId}")
	public List<RequirementMapping> getmyreqsbyproject(@PathVariable Long recId) throws Exception {
		System.out.println("Getting Entire Requirement Mapping List By Project and User ...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		
		List<RequirementEntity> reqs = (List<RequirementEntity>)
				repository.findByAssignedtoAndProjectOrderByCustomidAsc(user.getID(), recId);
		
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
			rec.setProject(req.getProject());
			
			UserEntity user2 = userrepo.findById(req.getAssignedto());
			if(user2!=null)
				rec.setUser(user2.getName());
			
			MappingEntity map = mappingrepo.findByReq(req.getID());
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