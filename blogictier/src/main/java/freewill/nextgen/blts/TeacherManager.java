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

import freewill.nextgen.blts.daos.TeacherRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.TeacherEntity;
import freewill.nextgen.blts.entities.UserEntity;

/** 
 * File:   TeacherManager.java
 * Date:   21/09/2018
 * Author: Benito Vela
 * Refs:   None
 * 
 * This static class provides the business logic to manage Teacher
 * (create, update, remove, getlist).
 * 
**/

@RestController
@RequestMapping("/TeacherEntity")
public class TeacherManager {
	
	@Autowired
	TeacherRepository repository;
	
	@Autowired
	UserRepository userrepo;

	@RequestMapping("/create")
	public TeacherEntity add(@RequestBody TeacherEntity rec) throws Exception {
		if(rec!=null){
			// Injects the new record
			System.out.println("Saving Teacher..."+rec.toString());
			rec.setCreated(new Date());
			rec.setTimestamp(new Date());
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		UserEntity user = userrepo.findByLoginname(auth.getName());
    		rec.setCompany(user.getCompany());
    		
			TeacherEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;
	}
	
	@RequestMapping("/update")
	public TeacherEntity update(@RequestBody TeacherEntity rec) throws Exception {
		if(rec!=null){
			System.out.println("Updating Teacher..."+rec);
			rec.setTimestamp(new Date());
			TeacherEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;	
	}
	
	@Transactional
	@RequestMapping("/delete/{recId}")
	public boolean remove(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Deleting Teacher..."+recId);
			TeacherEntity rec = repository.findById(recId);
			if(rec!=null){
				repository.delete(rec);
				return true;
			}
		}
		return false;	
	}
	
	@RequestMapping("/getlist")
	public List<TeacherEntity> getlist() throws Exception {
		System.out.println("Getting Entire Teacher List...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		List<TeacherEntity> recs = repository.findByCompany(user.getCompany());
		return recs;
	}
	
	@RequestMapping("/get/{recId}")
	public TeacherEntity get(@PathVariable Long recId) throws Exception {
		System.out.println("Retrieving Teacher..."+recId);
		return repository.findById(recId);
	}
	
	@RequestMapping("/countActiveTeachers")
	public TeacherEntity countActiveTeachers() throws Exception {
		System.out.println("Getting countActiveTeachers...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		TeacherEntity rec = new TeacherEntity();
		rec.setId(repository.countByCompanyAndActive(user.getCompany(), true));
		return rec;
	}
	
	@RequestMapping("/countNoActiveTeachers")
	public TeacherEntity countNoActiveTeachers() throws Exception {
		System.out.println("Getting countNoActiveTeachers...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		TeacherEntity rec = new TeacherEntity();
		rec.setId(repository.countByCompanyAndActive(user.getCompany(), false));
		return rec;
	}
	
}