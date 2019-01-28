package freewill.nextgen.blts;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import freewill.nextgen.blts.daos.ConfigRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.ConfigEntity;
import freewill.nextgen.blts.data.ConfigEntity.ConfigItemEnum;
import freewill.nextgen.blts.entities.UserEntity;

/** 
 * File:   ConfigManager.java
 * Date:   19/10/2017
 * Author: Benito Vela
 * Refs:   None
 * 
 * This static class provides the business logic to manage Configs
 * (create, update, remove, getlist).
 * 
**/

@RestController
@RequestMapping("/ConfigEntity")
public class ConfigManager {
	
	@Autowired
	ConfigRepository repository;
	
	@Autowired
	UserRepository userrepo;

	@RequestMapping("/create")
	public ConfigEntity add(@RequestBody ConfigEntity rec) throws Exception {
		if(rec!=null){
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		UserEntity user = userrepo.findByLoginname(auth.getName());
    		
			ConfigEntity old = repository.findByNameAndCompany(rec.getName(), user.getCompany());
			if(old!=null)
				throw new IllegalArgumentException("Record already exists");
			
    		rec.setCompany(user.getCompany());
        	// Injects the new record
        	System.out.println("Saving Config..."+rec.toString());
			ConfigEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;
	}
	
	@RequestMapping("/update")
	public ConfigEntity update(@RequestBody ConfigEntity rec) throws Exception {
		if(rec!=null){
			System.out.println("Updating Config..."+rec);
			ConfigEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;	
	}
	
	@RequestMapping("/delete/{recId}")
	public boolean remove(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Deleting Config..."+recId);
			ConfigEntity rec = repository.findById(recId);
			if(rec!=null){
				repository.delete(rec);
				return true;
			}
		}
		return false;	
	}
	
	@RequestMapping("/getlist")
	public List<ConfigEntity> getlist() throws Exception {
		System.out.println("Getting Entire Config List...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		return (List<ConfigEntity>) repository.findByCompany(user.getCompany());
	}
	
	@RequestMapping("/get/{recId}")
	public ConfigEntity get(@PathVariable Long recId) throws Exception {
		System.out.println("Retrieving Config..."+recId);
		return repository.findById(recId);
	}
	
	@RequestMapping("/getconfig/{key}")
	public ConfigEntity get(@PathVariable ConfigItemEnum key) throws Exception {
		if(key!=null){
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		UserEntity user = userrepo.findByLoginname(auth.getName());
			System.out.println("Retrieving Config key... "+key);
			ConfigEntity rec = repository.findByNameAndCompany(key, user.getCompany());
			if(rec==null){
				rec = new ConfigEntity();
				rec.setName(key);
				rec.setValue(key.defaultVal());
				rec.setCompany(user.getCompany());
			}
			return rec;
		}
		return null;
	}
	
}