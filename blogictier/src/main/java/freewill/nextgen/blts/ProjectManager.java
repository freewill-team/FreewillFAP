package freewill.nextgen.blts;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import freewill.nextgen.blts.daos.DeliverableRepository;
import freewill.nextgen.blts.daos.ProjectRepository;
import freewill.nextgen.blts.daos.RequirementRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.ProjectEntity;
import freewill.nextgen.blts.entities.UserEntity;

/** 
 * File:   ProjectManager.java
 * Date:   22/10/2017
 * Author: Benito Vela
 * Refs:   None
 * 
 * This static class provides the business logic to manage Project
 * (create, update, remove, getlist).
 * 
**/

@RestController
@RequestMapping("/ProjectEntity")
public class ProjectManager {
	
	@Autowired
	ProjectRepository repository;
	
	@Autowired
	RequirementRepository reqrepo;
	
	@Autowired
	DeliverableRepository deliverrepo;
	
	@Autowired
	UserRepository userrepo;

	@RequestMapping("/create")
	public ProjectEntity add(@RequestBody ProjectEntity rec) throws Exception {
		if(rec!=null){
			// Injects the new record
			System.out.println("Saving Project..."+rec.toString());
			rec.setID(repository.getMaxId()+1);
			rec.setCreated(new Date());
			rec.setTimestamp(new Date());
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		UserEntity user = userrepo.findByLoginname(auth.getName());
    		rec.setCompany(user.getCompany());
    		
			ProjectEntity res = repository.save(rec);
			System.out.println("Id = "+res.getID());
			return res;
		}
		return null;
	}
	
	@RequestMapping("/update")
	public ProjectEntity update(@RequestBody ProjectEntity rec) throws Exception {
		if(rec!=null){
			System.out.println("Updating Project..."+rec);
			rec.setTimestamp(new Date());
			ProjectEntity res = repository.save(rec);
			System.out.println("Id = "+res.getID());
			return res;
		}
		return null;	
	}
	
	@Transactional
	@RequestMapping("/delete/{recId}")
	public boolean remove(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Deleting Project..."+recId);
			ProjectEntity rec = repository.findById(recId);
			if(rec!=null){
				repository.delete(rec);
				// delete requirements, deliverables and mappings as well
				reqrepo.deleteByProject(recId);
				deliverrepo.deleteByProject(recId);
				return true;
			}
		}
		return false;	
	}
	
	@RequestMapping("/getlist")
	public List<ProjectEntity> getlist() throws Exception {
		System.out.println("Getting Entire Project List...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		List<ProjectEntity> recs = repository.findByCompany(user.getCompany());
		for(ProjectEntity rec:recs){
			rec.setLogo(new byte[0]);
		}
		return recs;
	}
	
	@RequestMapping("/get/{recId}")
	public ProjectEntity get(@PathVariable Long recId) throws Exception {
		System.out.println("Retrieving Project..."+recId);
		return repository.findById(recId);
	}
	
	@RequestMapping("/countActiveProjects")
	public ProjectEntity countActiveProjects() throws Exception {
		System.out.println("Getting countActiveProjects...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		ProjectEntity rec = new ProjectEntity();
		rec.setID(repository.countByCompanyAndActive(user.getCompany(), true));
		return rec;
	}
	
	@RequestMapping("/countNoActiveProjects")
	public ProjectEntity countNoActiveProjects() throws Exception {
		System.out.println("Getting countNoActiveProjects...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		ProjectEntity rec = new ProjectEntity();
		rec.setID(repository.countByCompanyAndActive(user.getCompany(), false));
		return rec;
	}
	
}