package freewill.nextgen.blts;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import freewill.nextgen.blts.daos.MailServerRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.entities.MailServerEntity;
import freewill.nextgen.blts.entities.UserEntity;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;

/** 
 * File:   MailServerEntityManager.java
 * Date:   29/11/2017
 * Author: Benito Vela
 * Refs:   None
 * 
 * This static class provides the business logic to manage MailServerEntity
 * (create, update, remove, getlist).
 * 
**/

@RestController
@RequestMapping("/MailServerEntity")
public class MailServerManager {
	
	@Autowired
	MailServerRepository repository;
	
	@Autowired
	UserRepository userrepo;

	@RequestMapping("/create")
	public MailServerEntity add(@RequestBody MailServerEntity rec) throws Exception {
		if(rec!=null){
			// Injects the new record
			System.out.println("Saving MailServerEntity..."+rec.toString());
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		UserEntity user = userrepo.findByLoginname(auth.getName());
    		rec.setCompany(user.getCompany());
			
			MailServerEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;
	}
	
	@RequestMapping("/update")
	public MailServerEntity update(@RequestBody MailServerEntity rec) throws Exception {
		if(rec!=null){
			System.out.println("Updating MailServerEntity..."+rec);
			MailServerEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;	
	}
	
	@RequestMapping("/delete/{recId}")
	public boolean remove(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Deleting MailServerEntity..."+recId);
			MailServerEntity rec = repository.findById(recId);
			if(rec!=null){
				repository.delete(rec);
				return true;
			}
		}
		return false;	
	}
	
	@RequestMapping("/getlist")
	public List<MailServerEntity> getlist() throws Exception {
		System.out.println("Getting Entire MailServerEntity List...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		if(user.getRole()==UserRoleEnum.SUPER)
			return (List<MailServerEntity>) repository.findAll();
		else
			return (List<MailServerEntity>) repository.findByCompany(user.getCompany());
	}
	
	@RequestMapping("/get/{recId}")
	public MailServerEntity get(@PathVariable Long recId) throws Exception {
		System.out.println("Retrieving MailServerEntity..."+recId);
		return repository.findById(recId);
	}
	
}