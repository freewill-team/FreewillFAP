package freewill.nextgen.blts;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import freewill.nextgen.blts.daos.FeatureRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.FeatureEntity;
import freewill.nextgen.blts.entities.UserEntity;

/** 
 * Feature:   FeatureManager.java
 * Date:   22/10/2017
 * Author: Benito Vela
 * Refs:   None
 * 
 * This static class provides the business logic to manage Feature
 * (create, update, remove, getlist).
 * 
**/

@RestController
@RequestMapping("/FeatureEntity")
public class FeatureManager {
	
	@Autowired
	FeatureRepository repository;
	
	@Autowired
	UserRepository userrepo;

	@RequestMapping("/create")
	public FeatureEntity add(@RequestBody FeatureEntity rec) throws Exception {
		if(rec!=null){
			// Injects the new record
			System.out.println("Saving Feature..."+rec.toString());
			rec.setID(repository.getMaxId()+1);
			rec.setTimestamp(new Date());
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		UserEntity user = userrepo.findByLoginname(auth.getName());
    		rec.setCompany(user.getCompany());
    		
			FeatureEntity res = repository.save(rec);
			System.out.println("Id = "+res.getID());
			return res;
		}
		return null;
	}
	
	@RequestMapping("/update")
	public FeatureEntity update(@RequestBody FeatureEntity rec) throws Exception {
		if(rec!=null){
			System.out.println("Updating Feature..."+rec);
			rec.setTimestamp(new Date());
			FeatureEntity res = repository.save(rec);
			System.out.println("Id = "+res.getID());
			return res;
		}
		return null;	
	}
	
	@RequestMapping("/delete/{recId}")
	public boolean remove(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Deleting Feature..."+recId);
			FeatureEntity rec = repository.findById(recId);
			if(rec!=null){
				repository.delete(rec);
				return true;
			}
		}
		return false;	
	}
	
	@RequestMapping("/getlist")
	public List<FeatureEntity> getlist() throws Exception {
		System.out.println("Getting Entire Feature List...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		return (List<FeatureEntity>) repository.findByCompany(user.getCompany());
	}
	
	@RequestMapping("/get/{recId}")
	public FeatureEntity get(@PathVariable Long recId) throws Exception {
		System.out.println("Retrieving Feature..."+recId);
		return repository.findById(recId);
	}
	
	@RequestMapping("/getbyproduct/{recId}")
	public List<FeatureEntity> getlistbyproduct(@PathVariable Long recId) throws Exception {
		System.out.println("Getting Feature List per product (container)..."+recId);
		//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//UserEntity user = userrepo.findByLoginname(auth.getName());
		//return (List<FeatureEntity>) repository.findByCompanyAndProductOrderByIdAsc(user.getCompany(), recId);
		List<FeatureEntity> output = new ArrayList<FeatureEntity>();
		getlistbyparent(recId, 0L, output);
		return output;
	}
	
	private void getlistbyparent(Long prd, Long parent, List<FeatureEntity> output) throws Exception {
		List<FeatureEntity> recs = repository.findByProductAndParentOrderByIdAsc(prd, parent);
		for(FeatureEntity rec : recs){
			output.add(rec);
			getlistbyparent(prd, rec.getID(), output);
		}
	}
	
	@RequestMapping("/getbyproductfiltered/{recId}/{text}")
	public List<FeatureEntity> getbyproductfiltered(@PathVariable Long recId, @PathVariable String text) throws Exception {
		text = "%"+text+"%";
		System.out.println("Getting Feature List per product (filtered)..."+recId+"/"+text);
		List<FeatureEntity> result = new ArrayList<FeatureEntity>();
		result.addAll((List<FeatureEntity>) 
				repository.findByProductAndTitleLikeAndActiveOrderByIdAsc(recId, text, true));
		result.addAll((List<FeatureEntity>) 
				repository.findByProductAndDescriptionLikeAndActiveOrderByIdAsc(recId, text, true));
		return result;
	}
	
	@RequestMapping("/getfiltered/{text}")
	public List<FeatureEntity> getfiltered(@PathVariable String text) throws Exception {
		text = "%"+text+"%";
		System.out.println("Getting Feature List (filtered)..."+text);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		List<FeatureEntity> result = new ArrayList<FeatureEntity>();
		result.addAll((List<FeatureEntity>) 
				repository.findByCompanyAndTitleLikeAndActiveOrderByIdAsc(user.getCompany(), text, true));
		result.addAll((List<FeatureEntity>) 
				repository.findByCompanyAndDescriptionLikeAndActiveOrderByIdAsc(user.getCompany(), text, true));
		return result;
	}
	
	@RequestMapping("/hasChildDocument/{recId}")
	public FeatureEntity hasChildDocument(@PathVariable Long recId) throws Exception {
		System.out.println("Getting hasChildDocument..."+recId);
		List<FeatureEntity> recs = repository.findByProductAndParentOrderByIdAsc(0L, recId);
		FeatureEntity rec = new FeatureEntity();
		rec.setID(recs.size());
		return rec;
	}
	
	@RequestMapping("/countFeaturesPerProduct/{recId}")
	public FeatureEntity countFeaturesPerProduct(@PathVariable Long recId) throws Exception {
		System.out.println("Getting countFeaturesPerProduct..."+recId);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		FeatureEntity rec = new FeatureEntity();
		rec.setID(repository.countByCompanyAndProduct(user.getCompany(), recId));
		return rec;
	}
	
	@RequestMapping("/countActiveFeatures")
	public FeatureEntity countActiveFeatures() throws Exception {
		System.out.println("Getting countActiveFeatures...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		FeatureEntity rec = new FeatureEntity();
		rec.setID(repository.countByCompanyAndActive(user.getCompany(), true));
		return rec;
	}
	
	@RequestMapping("/countNoActiveFeatures")
	public FeatureEntity countNoActiveFeatures() throws Exception {
		System.out.println("Getting countNoActiveFeatures...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		FeatureEntity rec = new FeatureEntity();
		rec.setID(repository.countByCompanyAndActive(user.getCompany(), false));
		return rec;
	}
	
}