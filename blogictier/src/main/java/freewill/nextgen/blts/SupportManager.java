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

import freewill.nextgen.blts.daos.SupportRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.SupportEntity;
import freewill.nextgen.blts.entities.UserEntity;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.common.entities.EmailEntity;
import freewill.nextgen.common.rtdbclient.RtdbDataService;

/** 
 * File:   SupportManager.java
 * Date:   22/10/2017
 * Author: Benito Vela
 * Refs:   None
 * 
 * This static class provides the business logic to manage Support
 * (create, update, remove, getlist).
 * 
**/

@RestController
@RequestMapping("/SupportEntity")
public class SupportManager {
	
	@Autowired
	SupportRepository repository;
	
	@Autowired
	UserRepository userrepo;

	@SuppressWarnings("deprecation")
	@RequestMapping("/create")
	public SupportEntity add(@RequestBody SupportEntity rec) throws Exception {
		if(rec!=null){
			// Injects the new record
			System.out.println("Saving New Support..."+rec.toString());
			rec.setID(repository.getMaxId()+1);
			rec.setTimestamp(new Date());
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		UserEntity user = userrepo.findByLoginname(auth.getName());
    		rec.setUser(user.getID());
    		
    		Date now = new Date();
    		String text = now.toLocaleString() + "/" + user.getName() + ": "+ rec.getDescription()+ "\n";
    		rec.setComments(text);
    		
			SupportEntity res = repository.save(rec);
			System.out.println("Id = "+res.getID());
			
			// Send notification by email
			String title = "New Support Ticket Created #"+rec.getID();
					//Messages.get().getKey("supportcreated", System.getProperty("LOCALE"));
			String message = String.format(
					"New support ticket Id#%s created by user %s.\n"+
					"Issue Description: %s\n",
					//Messages.get().getKey("supporcreatedmsg", System.getProperty("LOCALE")),
					rec.getID(), user.getName(), rec.getDescription());
			RtdbDataService.get().pushEmail(new EmailEntity(
					"freewilltechnologies@gmail.com", // Siempre a la cuenta del Superuser
					title, message,
					0L // Para que el correo salga aunque esta Company no tenga configurado ningun servidor
					));
			return res;
		}
		return null;
	}
	
	@RequestMapping("/update")
	public SupportEntity update(@RequestBody SupportEntity rec) throws Exception {
		// Actually only used in order to close the ticket
		if(rec!=null){
			System.out.println("Updating Support..."+rec);
			rec.setTimestamp(new Date());
			SupportEntity res = repository.save(rec);
			System.out.println("Id = "+res.getID());
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		UserEntity user = userrepo.findByLoginname(auth.getName());
    		
    		if(user.getRole()==UserRoleEnum.SUPER) return res; // To avoid sending my own responses
    		
			// Send notification by email
			String title = "Support Ticket Updated #"+rec.getID();
					//Messages.get().getKey("supportupdated", System.getProperty("LOCALE"));
			String message = String.format(
					"Support ticket Id#%s updated by user %s.\n"+
					"Issue Description: %s\n",
					//Messages.get().getKey("supportupdatedmsg", System.getProperty("LOCALE")),
					rec.getID(), user.getName(), rec.getDescription());
			RtdbDataService.get().pushEmail(new EmailEntity(
					"freewilltechnologies@gmail.com", // Siempre a la cuenta del Superuser
					title, message,
					0L // Para que el correo salga aunque esta Company no tenga configurado ningun servidor
					));
			return res;
		}
		return null;	
	}
	
	@RequestMapping("/delete/{recId}")
	public boolean remove(@PathVariable Long recId) throws Exception {
		// Actually it must not be used; use close/update instead
		if(recId!=null){
			System.out.println("Deleting Support..."+recId);
			SupportEntity rec = repository.findById(recId);
			if(rec!=null){
				repository.delete(rec);
				return true;
			}
		}
		return false;	
	}
	
	@RequestMapping("/getlist")
	public List<SupportEntity> getlist() throws Exception {
		System.out.println("Getting Entire Support List...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		return (List<SupportEntity>) repository.findByUserOrderByCreatedDesc(user.getID());
	}
	
	@RequestMapping("/get/{recId}")
	public SupportEntity get(@PathVariable Long recId) throws Exception {
		System.out.println("Retrieving Support..."+recId);
		return repository.findById(recId);
	}
	
	@RequestMapping("/countActiveSupports")
	public SupportEntity countActiveSupports() throws Exception {
		System.out.println("Getting countActiveSupports...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		SupportEntity rec = new SupportEntity();
		rec.setID(repository.countByUserAndResolved(user.getID(), true));
		return rec;
	}
	
	@RequestMapping("/countNoActiveSupports")
	public SupportEntity countNoActiveSupports() throws Exception {
		System.out.println("Getting countNoActiveSupports...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		SupportEntity rec = new SupportEntity();
		rec.setID(repository.countByUserAndResolved(user.getID(), false));
		return rec;
	}
	
	@SuppressWarnings("deprecation")
	@RequestMapping("/addcomment/{recId}/{comment}")
	public SupportEntity addcomment(@PathVariable Long recId, @PathVariable String comment) throws Exception {
		System.out.println("Retrieving Support to be updated..."+recId);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		
		SupportEntity rec = repository.findById(recId);
		if(rec!=null){
			//System.out.println("Updating Support..."+rec);
			rec.setTimestamp(new Date());
			
			Date now = new Date();
			String text = rec.getComments();
			text = text + "\n" + now.toLocaleString() + "/" + user.getName() + ":";
    		text = text + "\n" + comment;
    		text = text + "\n";
    		rec.setComments(text);
			
			SupportEntity res = repository.save(rec);
			System.out.println("Id = "+res.getID());
    		
    		if(user.getRole()==UserRoleEnum.SUPER) return res; // To avoid sending my own responses
    		
			// Send notification by email
			String title = "Support Ticket Updated #"+rec.getID();
					//Messages.get().getKey("supportupdated", System.getProperty("LOCALE"));
			String message = String.format(
					"Support ticket Id#%s updated by user %s.\n"+
					"Issue Description: %s\n"+
					"New Comment:       %s\n",
					//Messages.get().getKey("supportupdatedmsg", System.getProperty("LOCALE")),
					rec.getID(), user.getName(), rec.getDescription(), comment);
			RtdbDataService.get().pushEmail(new EmailEntity(
					"freewilltechnologies@gmail.com", // Siempre a la cuenta del Superuser
					title, message,
					0L // Para que el correo salga aunque esta Company no tenga configurado ningun servidor
					));
			return res;
		}
		return null;
	}
	
}