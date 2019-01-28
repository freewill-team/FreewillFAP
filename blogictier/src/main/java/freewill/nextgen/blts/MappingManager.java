package freewill.nextgen.blts;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import freewill.nextgen.blts.daos.CompanyRepository;
import freewill.nextgen.blts.daos.MappingRepository;
import freewill.nextgen.blts.daos.RequirementRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.CompanyEntity;
import freewill.nextgen.blts.data.MappingEntity;
import freewill.nextgen.blts.data.RequirementEntity;
import freewill.nextgen.blts.entities.UserEntity;

/** 
 * File:   MappingManager.java
 * Date:   22/10/2017
 * Author: Benito Vela
 * Refs:   None
 * 
 * This static class provides the business logic to manage Mapping
 * (create, update, remove, getlist).
 * 
**/

@RestController
@RequestMapping("/MappingEntity")
public class MappingManager {
	
	@Autowired
	MappingRepository repository;
	
	@Autowired
	UserRepository userrepo;
	
	@Autowired
	CompanyRepository comprepo;
	
	@Autowired
	RequirementRepository reqrepo;

	@RequestMapping("/create")
	public MappingEntity add(@RequestBody MappingEntity rec) throws Exception {
		if(rec!=null){
			// Injects the new record
			System.out.println("Saving Mapping..."+rec.toString());
			rec.setID(repository.getMaxId()+1);
			rec.setTimestamp(new Date());
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		UserEntity user = userrepo.findByLoginname(auth.getName());
    		CompanyEntity company = comprepo.findById(user.getCompany());
    		rec.setTotalcost(company.getLaborcostrate()*rec.getLaboreffort());
    		
			MappingEntity res = repository.save(rec);
			System.out.println("Id = "+res.getID());
			
			// Set requirement as Resolved
			RequirementEntity req = reqrepo.findById(rec.getReq());
			if(req!=null){
				System.out.println("Set requirement as Resolved..."+req.getID());
				req.setResolved(true);
				reqrepo.save(req);
			}
			
			return res;
		}
		return null;
	}
	
	@RequestMapping("/update")
	public MappingEntity update(@RequestBody MappingEntity rec) throws Exception {
		if(rec!=null){
			System.out.println("Updating Mapping..."+rec);
			rec.setTimestamp(new Date());
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		UserEntity user = userrepo.findByLoginname(auth.getName());
    		CompanyEntity company = comprepo.findById(user.getCompany());
	    	rec.setTotalcost(company.getLaborcostrate()*rec.getLaboreffort());
			
			MappingEntity res = repository.save(rec);
			System.out.println("Id = "+res.getID());
			return res;
		}
		return null;	
	}
	
	@RequestMapping("/delete/{recId}")
	public boolean remove(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Deleting Mapping..."+recId);
			MappingEntity rec = repository.findById(recId);
			if(rec!=null){
				repository.delete(rec);
				
				// Set requirement as Unresolved
				RequirementEntity req = reqrepo.findById(rec.getReq());
				if(req!=null){
					System.out.println("Set requirement as Unresolved..."+req.getID());
					req.setResolved(false);
					reqrepo.save(req);
				}
				
				return true;
			}
		}
		return false;	
	}
	
	@RequestMapping("/getbyfeature/{recId}")
	public List<MappingEntity> getbyfeature(@PathVariable Long recId) throws Exception {
		System.out.println("Getting Mapping List for Doc..."+recId);
		//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//UserEntity user = userrepo.findByLoginname(auth.getName());
		return (List<MappingEntity>) repository.findByDoc(recId);
	}
	
	@RequestMapping("/get/{recId}")
	public MappingEntity get(@PathVariable Long recId) throws Exception {
		System.out.println("Retrieving Mapping..."+recId);
		return repository.findById(recId);
	}
	
	@RequestMapping("/getbyreq/{recId}")
	public MappingEntity getbyreq(@PathVariable Long recId) throws Exception {
		System.out.println("Getting Mapping for Req..."+recId);
		//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//UserEntity user = userrepo.findByLoginname(auth.getName());
		return repository.findByReq(recId);
	}

	@RequestMapping("/getbyproject/{recId}")
	public List<MappingEntity> getbyproject(@PathVariable Long recId) throws Exception {
		System.out.println("Getting Mapping List for Project..."+recId);
		//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//UserEntity user = userrepo.findByLoginname(auth.getName());
		return (List<MappingEntity>) repository.findByProject(recId);
	}
	
	@RequestMapping("/countMappingsByfeature/{recId}")
	public MappingEntity countMappingsByfeature(@PathVariable Long recId) throws Exception {
		System.out.println("Getting countMappingsByfeature..."+recId);
		MappingEntity rec = new MappingEntity();
		rec.setID(repository.countByDoc(recId));
		return rec;
	}
	
}