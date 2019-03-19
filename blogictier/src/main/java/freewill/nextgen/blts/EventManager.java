package freewill.nextgen.blts;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import freewill.nextgen.blts.daos.EventLogRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.JamShowEntity;
import freewill.nextgen.blts.entities.EventEntity;
import freewill.nextgen.blts.entities.UserEntity;

/** 
 * File:   EventManager.java
 * Date:   14/09/2017
 * Author: Benito Vela
 * Refs:   None
 * 
 * This static class provides the business logic to manage events
 * (create, getlist).
 * 
**/

@RestController
@RequestMapping("/EventEntity")
public class EventManager {
	
	@Autowired
	EventLogRepository repository;
	
	@Autowired
	UserRepository userrepo;
	
	/*@RequestMapping("/push")
	public boolean push(@RequestBody EventEntity evt) throws Exception {
		// Injects a new event
		RtdbDataService.get().pushEvent(evt);
		return true;
	}*/
	
	@RequestMapping("/create")
	public EventEntity add(@RequestBody EventEntity rec) throws Exception {
		if(rec!=null){
			// Injects the new record
			System.out.println("Saving EventEntity..."+rec.toString());
			// Not required as ID is autoincrement rec.setID(repository.getMaxId()+1);
			//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		//UserEntity user = userrepo.findByLoginname(auth.getName());
			UserEntity user = userrepo.findByLoginname(rec.getUsername());
    		//rec.setUsername(user.getLoginname());
			if(user!=null)
				rec.setCompany(user.getCompany());
			else 
				rec.setCompany(0L);
    		System.out.println("User = "+rec.getUsername());
    		System.out.println("Company = "+rec.getCompany());
			EventEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;
	}
	
	@RequestMapping("/getlist")
	public List<EventEntity> getlist() throws Exception {
		System.out.println("Getting Entire EventEntity List...");
		return (List<EventEntity>) repository.findAll();
	}
	
	@RequestMapping("/eventsByDate/{lsdate}/{ledate}")
	public List<EventEntity> query(@PathVariable Long lsdate, @PathVariable Long ledate) throws Exception {
		System.out.println("Getting eventsByDate List...");
		Date sdate = new Date(lsdate);
		Date edate = new Date(ledate);
		Pageable top1000 = new PageRequest(0, 1000);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		return (List<EventEntity>) repository.findByTimestampBetween(user.getCompany(), sdate, edate, top1000);
	}
	
	@RequestMapping("/deleteByPeriod/{days}")
	public boolean deleteByCompeticionAndCategoria(@PathVariable int days) throws Exception {
		System.out.println("Deleting EventEntity By Period..."+days);
		//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//UserEntity user = userrepo.findByLoginname(auth.getName());
		Date limdate = new Date();
		limdate.setTime(limdate.getTime()-days*86100);
		repository.deleteByTimestampBefore(limdate);
		return true;
	}
	
}