package freewill.nextgen.blts;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import freewill.nextgen.blts.daos.RequirementRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.Requirement2Entity;


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
@RequestMapping("/Requirement2Entity")
public class Requirement2Manager {
	
	@Autowired
	RequirementRepository repository;
	
	@Autowired
	UserRepository userrepo;
	
	@RequestMapping("/getbyproject/{recId}")
	public List<Requirement2Entity> getbyproject(@PathVariable Long recId) throws Exception {
		System.out.println("Getting Entire Requirement With User By Project List...");
		//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//UserEntity user = userrepo.findByLoginname(auth.getName());
		List<Requirement2Entity> recs = (List<Requirement2Entity>) repository.findByProjectWithUser(recId);
		recs.addAll((List<Requirement2Entity>) repository.findByProjectWithNoUser(recId));
		return  recs;
	}
	
}