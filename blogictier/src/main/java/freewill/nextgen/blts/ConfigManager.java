package freewill.nextgen.blts;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import freewill.nextgen.blts.daos.ConfigRepository;
import freewill.nextgen.blts.data.ConfigEntity;

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

	@RequestMapping("/create")
	public ConfigEntity add(@RequestBody ConfigEntity rec) throws Exception {
		if(rec!=null){
			ConfigEntity old = repository.findByName(rec.getName());
			if(old!=null)
				throw new IllegalArgumentException("Record already exists");
			
        	// Injects the new record
        	System.out.println("Saving Config..."+rec.toString());
        	rec.setID(repository.getMaxId()+1);
        	//rec.setTimestamp(new Date());
			ConfigEntity res = repository.save(rec);
			System.out.println("Id = "+res.getID());
			return res;
		}
		return null;
	}
	
	@RequestMapping("/update")
	public ConfigEntity update(@RequestBody ConfigEntity rec) throws Exception {
		if(rec!=null){
			System.out.println("Updating Config..."+rec);
			//rec.setTimestamp(new Date());
			ConfigEntity res = repository.save(rec);
			System.out.println("Id = "+res.getID());
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
		return (List<ConfigEntity>) repository.findAll();
	}
	
	@RequestMapping("/get/{recId}")
	public ConfigEntity get(@PathVariable Long recId) throws Exception {
		System.out.println("Retrieving Config..."+recId);
		return repository.findById(recId);
	}
	
	@RequestMapping("/getconfig/{key}")
	public ConfigEntity /*String*/ get(@PathVariable String key) throws Exception {
		if(key!=null){
			System.out.println("Retrieving Config key... "+key);
			ConfigEntity rec = repository.findByName(key);
			return rec;
		}
		return null;
	}
	
	public long getConfigLong(String key){
		try {
			ConfigEntity rec = get(key);
			long value = Long.parseLong(rec.getValue());
			return value;
		} catch (Exception e) {
			e.printStackTrace();
        	//throw new IllegalArgumentException("Fail to retrieve Record from Config Database");
			return 0L;
		}
	}
	
	public String getConfigString(String key){
		try {
			ConfigEntity rec = get(key);
			return rec.getValue();
		} catch (Exception e) {
			e.printStackTrace();
	        //throw new IllegalArgumentException("Fail to retrieve Record from Config Database");
			return "";
		}
	}
	
}