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

import freewill.nextgen.blts.daos.ProjectRepository;
import freewill.nextgen.blts.daos.RequirementRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.ProjectEntity;
import freewill.nextgen.blts.data.RequirementEntity;
import freewill.nextgen.blts.entities.UserEntity;
import freewill.nextgen.common.Messages;
import freewill.nextgen.common.entities.EmailEntity;
import freewill.nextgen.common.rtdbclient.RtdbDataService;

/** 
 * Requirement:   RequirementManager.java
 * Date:   22/10/2017
 * Author: Benito Vela
 * Refs:   None
 * 
 * This static class provides the business logic to manage Requirement
 * (create, update, remove, getlist).
 * 
**/

@RestController
@RequestMapping("/RequirementEntity")
public class RequirementManager {
	
	@Autowired
	RequirementRepository repository;
	
	@Autowired
	UserRepository userrepo;
	
	@Autowired
	ProjectRepository prjrepo;

	@RequestMapping("/create")
	public RequirementEntity add(@RequestBody RequirementEntity rec) throws Exception {
		if(rec!=null){
			// Injects the new record
			System.out.println("Saving Requirement..."+rec.toString());
			rec.setID(repository.getMaxId()+1);
			rec.setTimestamp(new Date());
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		UserEntity user = userrepo.findByLoginname(auth.getName());
    		rec.setCompany(user.getCompany());
    		
			RequirementEntity res = repository.save(rec);
			System.out.println("Id = "+res.getID());
			return res;
		}
		return null;
	}
	
	@RequestMapping("/update")
	public RequirementEntity update(@RequestBody RequirementEntity rec) throws Exception {
		if(rec!=null){
			System.out.println("Updating Requirement..."+rec);
			rec.setTimestamp(new Date());
			RequirementEntity res = repository.save(rec);
			System.out.println("Id = "+res.getID());
			return res;
		}
		return null;	
	}
	
	@RequestMapping("/delete/{recId}")
	public boolean remove(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Deleting Requirement..."+recId);
			RequirementEntity rec = repository.findById(recId);
			if(rec!=null){
				repository.delete(rec);
				return true;
			}
		}
		return false;	
	}
	
	@RequestMapping("/getlist")
	public List<RequirementEntity> getlist() throws Exception {
		System.out.println("Getting Entire Requirement List...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		return (List<RequirementEntity>) repository.findByCompany(user.getCompany());
	}
	
	@RequestMapping("/get/{recId}")
	public RequirementEntity get(@PathVariable Long recId) throws Exception {
		System.out.println("Retrieving Requirement..."+recId);
		return repository.findById(recId);
	}
	
	@RequestMapping("/getbyproject/{recId}")
	public List<RequirementEntity> getlistbyproject(@PathVariable Long recId) throws Exception {
		System.out.println("Getting Requirement List per project..."+recId);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		return (List<RequirementEntity>) repository.findByCompanyAndProjectOrderByCustomidAsc(user.getCompany(), recId);
	}
	
	@RequestMapping("/getmyreqsbyproject/{recId}")
	public List<RequirementEntity> getmyreqsbyproject(@PathVariable Long recId) throws Exception {
		System.out.println("Getting Requirement List per project and user..."+recId);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		return (List<RequirementEntity>) repository.findByAssignedtoAndProjectOrderByCustomidAsc(user.getID(), recId);
	}
	
	@RequestMapping("/countResolvedRequirements")
	public RequirementEntity countResolvedRequirements() throws Exception {
		System.out.println("Getting countResolvedRequirements...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		RequirementEntity rec = new RequirementEntity();
		rec.setID(repository.countByCompanyAndResolved(user.getCompany(), true));
		return rec;
	}
	
	@RequestMapping("/countPendingRequirements")
	public RequirementEntity countPendingRequirements() throws Exception {
		System.out.println("Getting countPendingRequirements...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		RequirementEntity rec = new RequirementEntity();
		rec.setID(repository.countByCompanyAndResolved(user.getCompany(), false));
		return rec;
	}
	
	@RequestMapping("/assignto/{userId}")
	public boolean assignto(@RequestBody RequirementEntity rec, @PathVariable Long userId) throws Exception {
		if(rec!=null){
			UserEntity user = userrepo.findById(userId);
			ProjectEntity project = prjrepo.findById(rec.getProject());
			if(user==null || project==null) return false;
			
			System.out.println("Assigning Requirement to..."+userId);
			rec.setAssignedto(user.getID());
			RequirementEntity res = repository.save(rec);
			System.out.println("Id = "+res.getID());
			
			// Send notification by email
			String title = Messages.get().getKey("requirementassigned", System.getProperty("LOCALE"));
			String message = String.format(
					Messages.get().getKey("requirementassignedmsg", System.getProperty("LOCALE")),
					user.getName(), rec.getCustomid(), project.getName(), rec.getDescription());
			if(!user.getEmail().equals(""))
				RtdbDataService.get().pushEmail(new EmailEntity(
					user.getEmail(),
					/*"FreeWill requirement assigned",
		    		"Dear "+user.getName() +",\n\n"+
		    		"According to your coordinator request, a requirement has been assigned to you.\n"+
		    		"Requirement Information:\n"+
		    		"      CustomID: "+rec.getCustomid()+"\n"+
		    		"       Project: "+project.getName()+"\n"+
		    		"   Description: "+rec.getDescription()+"\n\n"+
		    		"Best regards.\n",*/
					title, message, user.getCompany()
			    	));
			
			return true;
		}
		return false;	
	}
	
}