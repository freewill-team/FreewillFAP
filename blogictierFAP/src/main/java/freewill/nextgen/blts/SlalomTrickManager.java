package freewill.nextgen.blts;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import freewill.nextgen.blts.daos.SlalomTrickRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.SlalomTrickEntity;
import freewill.nextgen.blts.data.SlalomTrickEntity.TrickFamilyEnum;
import freewill.nextgen.blts.entities.UserEntity;

/** 
 * File:   SlalomTrickManager.java
 * Date:   19/10/2017
 * Author: Benito Vela
 * Refs:   None
 * 
 * This static class provides the business logic to manage SlalomTricks
 * (create, update, remove, getlist).
 * 
**/

@RestController
@RequestMapping("/SlalomTrickEntity")
public class SlalomTrickManager {
	
	@Autowired
	SlalomTrickRepository repository;
	
	@Autowired
	UserRepository userrepo;

	@RequestMapping("/create")
	public SlalomTrickEntity add(@RequestBody SlalomTrickEntity rec) throws Exception {
		if(rec!=null){
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		UserEntity user = userrepo.findByLoginname(auth.getName());
    		
			SlalomTrickEntity old = repository.findByNombreAndCompany(
					rec.getNombre(), user.getCompany());
			if(old!=null)
				throw new IllegalArgumentException("Record already exists");
			
    		rec.setCompany(user.getCompany());
        	// Injects the new record
        	System.out.println("Saving SlalomTrick..."+rec.toString());
			SlalomTrickEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;
	}
	
	@RequestMapping("/update")
	public SlalomTrickEntity update(@RequestBody SlalomTrickEntity rec) throws Exception {
		if(rec!=null){
			System.out.println("Updating SlalomTrick..."+rec);
			SlalomTrickEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;	
	}
	
	@RequestMapping("/delete/{recId}")
	public boolean remove(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Deleting SlalomTrick..."+recId);
			SlalomTrickEntity rec = repository.findById(recId);
			if(rec!=null){
				repository.delete(rec);
				return true;
			}
		}
		return false;	
	}
	
	@RequestMapping("/getlist")
	public List<SlalomTrickEntity> getlist() throws Exception {
		System.out.println("Getting Entire SlalomTrick List...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		return (List<SlalomTrickEntity>) repository.findByCompany(user.getCompany());
	}
	
	@RequestMapping("/get/{recId}")
	public SlalomTrickEntity get(@PathVariable Long recId) throws Exception {
		System.out.println("Retrieving SlalomTrick..."+recId);
		return repository.findById(recId);
	}
	
	@RequestMapping("/getbyfamily/{family}")
	public List<SlalomTrickEntity> getbyfamily(@PathVariable TrickFamilyEnum family) throws Exception {
		System.out.println("Getting SlalomTrick List by family..."+family);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		return (List<SlalomTrickEntity>) repository.findByFamiliaAndCompany(
				family, user.getCompany());
	}
	
	
}