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

import freewill.nextgen.blts.daos.CalendarEventRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.CalendarEvent;
import freewill.nextgen.blts.entities.UserEntity;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;

/** 
 * File:   CalendarEventManager.java
 * Date:   23/09/2018
 * Author: Benito Vela
 * Refs:   None
 * 
 * This static class provides the business logic to manage CalendarEvent
 * (create, update, remove, getlist).
 * 
**/

@RestController
@RequestMapping("/CalendarEvent")
public class CalendarEventManager {
	
	@Autowired
	CalendarEventRepository repository;
	
	@Autowired
	UserRepository userrepo;
	
	@Transactional
	@RequestMapping("/create")
	public CalendarEvent add(@RequestBody CalendarEvent rec) throws Exception {
		if(rec!=null){
			// Injects the new record
			System.out.println("Saving CalendarEvent..."+rec.toString());
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		UserEntity user = userrepo.findByLoginname(auth.getName());
    		rec.setCompany(user.getCompany());
    		
			CalendarEvent res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;
	}
	
	@RequestMapping("/update")
	public CalendarEvent update(@RequestBody CalendarEvent rec) throws Exception {
		if(rec!=null){
			System.out.println("Updating CalendarEvent..."+rec);
			CalendarEvent res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;	
	}
	
	@Transactional
	@RequestMapping("/delete/{recId}")
	public boolean remove(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Deleting CalendarEvent..."+recId);
			CalendarEvent rec = repository.findById(recId);
			if(rec!=null){
				repository.delete(rec);
				return true;
			}
		}
		return false;	
	}
	
	@RequestMapping("/getlist")
	public List<CalendarEvent> getlist() throws Exception {
		System.out.println("Getting Entire CalendarEvent List...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		if(user.getRole()==UserRoleEnum.SUPER)
			return (List<CalendarEvent>) repository.findAll();
		else
			return (List<CalendarEvent>) repository.findByCompany(user.getCompany());
	}
	
	@RequestMapping("/get/{recId}")
	public CalendarEvent get(@PathVariable Long recId) throws Exception {
		System.out.println("Retrieving CalendarEvent..."+recId);
		return repository.findById(recId);
	}
	
}