package freewill.nextgen.blts;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import freewill.nextgen.blts.daos.LocationRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.LocationEntity;
import freewill.nextgen.blts.entities.UserEntity;

/** 
 * File:   LocationManager.java
 * Date:   21/09/2018
 * Author: Benito Vela
 * Refs:   None
 * 
 * This static class provides the business logic to manage Location
 * (create, update, remove, getlist).
 * 
**/

@RestController
@RequestMapping("/LocationEntity")
public class LocationManager {
	
	@Autowired
	LocationRepository repository;
	
	@Autowired
	UserRepository userrepo;

	@RequestMapping("/create")
	public LocationEntity add(@RequestBody LocationEntity rec) throws Exception {
		if(rec!=null){
			// Injects the new record
			System.out.println("Saving Location..."+rec.toString());
			rec.setCreated(new Date());
			rec.setTimestamp(new Date());
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		UserEntity user = userrepo.findByLoginname(auth.getName());
    		rec.setCompany(user.getCompany());
    		
			LocationEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;
	}
	
	@RequestMapping("/update")
	public LocationEntity update(@RequestBody LocationEntity rec) throws Exception {
		if(rec!=null){
			System.out.println("Updating Location..."+rec);
			rec.setTimestamp(new Date());
			LocationEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;	
	}
	
	@Transactional
	@RequestMapping("/delete/{recId}")
	public boolean remove(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Deleting Location..."+recId);
			LocationEntity rec = repository.findById(recId);
			if(rec!=null){
				repository.delete(rec);
				return true;
			}
		}
		return false;	
	}
	
	@RequestMapping("/getlist")
	public List<LocationEntity> getlist() throws Exception {
		System.out.println("Getting Entire Location List...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		List<LocationEntity> recs = repository.findByCompany(user.getCompany());
		return recs;
	}
	
	@RequestMapping("/get/{recId}")
	public LocationEntity get(@PathVariable Long recId) throws Exception {
		System.out.println("Retrieving Location..."+recId);
		return repository.findById(recId);
	}
	
	@RequestMapping("/countActiveLocations")
	public LocationEntity countActiveLocations() throws Exception {
		System.out.println("Getting countActiveLocations...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		LocationEntity rec = new LocationEntity();
		rec.setId(repository.countByCompanyAndActive(user.getCompany(), true));
		return rec;
	}
	
	@RequestMapping("/countNoActiveLocations")
	public LocationEntity countNoActiveLocations() throws Exception {
		System.out.println("Getting countNoActiveLocations...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		LocationEntity rec = new LocationEntity();
		rec.setId(repository.countByCompanyAndActive(user.getCompany(), false));
		return rec;
	}
	
}