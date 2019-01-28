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

import freewill.nextgen.blts.daos.DeliverableRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.DeliverableEntity;
import freewill.nextgen.blts.entities.UserEntity;

/** 
 * Deliverable:   DeliverableManager.java
 * Date:   22/10/2017
 * Author: Benito Vela
 * Refs:   None
 * 
 * This static class provides the business logic to manage Deliverable
 * (create, update, remove, getlist).
 * 
**/

@RestController
@RequestMapping("/DeliverableEntity")
public class DeliverableManager {
	
	@Autowired
	DeliverableRepository repository;
	
	@Autowired
	UserRepository userrepo;

	@RequestMapping("/create")
	public DeliverableEntity add(@RequestBody DeliverableEntity rec) throws Exception {
		if(rec!=null){
			// Injects the new record
			System.out.println("Saving Deliverable..."+rec.toString());
			rec.setID(repository.getMaxId()+1);
			rec.setTimestamp(new Date());
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		UserEntity user = userrepo.findByLoginname(auth.getName());
    		rec.setCompany(user.getCompany());
    		
			DeliverableEntity res = repository.save(rec);
			System.out.println("Id = "+res.getID());
			return res;
		}
		return null;
	}
	
	@RequestMapping("/update")
	public DeliverableEntity update(@RequestBody DeliverableEntity rec) throws Exception {
		if(rec!=null){
			System.out.println("Updating Deliverable..."+rec);
			rec.setTimestamp(new Date());
			DeliverableEntity res = repository.save(rec);
			System.out.println("Id = "+res.getID());
			return res;
		}
		return null;	
	}
	
	@RequestMapping("/delete/{recId}")
	public boolean remove(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Deleting Deliverable..."+recId);
			DeliverableEntity rec = repository.findById(recId);
			if(rec!=null){
				repository.delete(rec);
				return true;
			}
		}
		return false;	
	}
	
	@RequestMapping("/getlist")
	public List<DeliverableEntity> getlist() throws Exception {
		System.out.println("Getting Entire Deliverable List...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		return (List<DeliverableEntity>) repository.findByCompany(user.getCompany());
	}
	
	@RequestMapping("/get/{recId}")
	public DeliverableEntity get(@PathVariable Long recId) throws Exception {
		System.out.println("Retrieving Deliverable..."+recId);
		return repository.findById(recId);
	}
	
	@RequestMapping("/getbyproject/{recId}")
	public List<DeliverableEntity> getlistbyproject(@PathVariable Long recId) throws Exception {
		System.out.println("Getting Deliverable List per project..."+recId);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		return (List<DeliverableEntity>) repository.findByCompanyAndProject(user.getCompany(), recId);
	}
	
}