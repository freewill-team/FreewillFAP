package freewill.nextgen.blts;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import freewill.nextgen.blts.daos.StyleRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.Style;
import freewill.nextgen.blts.entities.UserEntity;

/** 
 * File:   StyleManager.java
 * Date:   19/08/2018
 * Author: Benito Vela
 * Refs:   None
 * 
 * This static class provides the business logic to manage Style
 * (create, update, remove, getlist).
 * 
**/

@RestController
@RequestMapping("/Style")
public class StyleManager {
	
	@Autowired
	StyleRepository repository;
	
	@Autowired
	UserRepository userrepo;

	@RequestMapping("/create")
	public Style add(@RequestBody Style rec) throws Exception {
		if(rec!=null){
			// Injects the new record
			System.out.println("Saving Style..."+rec.toString());
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		UserEntity user = userrepo.findByLoginname(auth.getName());
    		rec.setCompany(user.getCompany());
    		
			Style res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;
	}
	
	@RequestMapping("/update")
	public Style update(@RequestBody Style rec) throws Exception {
		if(rec!=null){
			System.out.println("Updating Style..."+rec);
			Style res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;	
	}
	
	@RequestMapping("/delete/{recId}")
	public boolean remove(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Deleting Style..."+recId);
			Style rec = repository.findById(recId);
			if(rec!=null){
				repository.delete(rec);
				return true;
			}
		}
		return false;	
	}
	
	@RequestMapping("/getlist")
	public List<Style> getlist() throws Exception {
		System.out.println("Getting Entire Style List...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		return (List<Style>) repository.findByCompany(user.getCompany());
	}
	
	@RequestMapping("/get/{recId}")
	public Style get(@PathVariable Long recId) throws Exception {
		System.out.println("Retrieving Style..."+recId);
		return repository.findById(recId);
	}
	
	@Transactional
	@RequestMapping("/deleteall")
	public Style deleteall() throws Exception {
		System.out.println("Deletting Entire Style List...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		repository.deleteByCompany(user.getCompany());
		Style rec = new Style();
		return rec;
	}
	
}