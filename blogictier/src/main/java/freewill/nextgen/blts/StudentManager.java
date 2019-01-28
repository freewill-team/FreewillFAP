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

import freewill.nextgen.blts.daos.StudentRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.StudentEntity;
import freewill.nextgen.blts.entities.UserEntity;

/** 
 * File:   StudentManager.java
 * Date:   31/10/2018
 * Author: Benito Vela
 * Refs:   None
 * 
 * This static class provides the business logic to manage Student
 * (create, update, remove, getlist).
 * 
**/

@RestController
@RequestMapping("/StudentEntity")
public class StudentManager {
	
	@Autowired
	StudentRepository repository;
	
	@Autowired
	UserRepository userrepo;

	@RequestMapping("/create")
	public StudentEntity add(@RequestBody StudentEntity rec) throws Exception {
		if(rec!=null){
			// Injects the new record
			System.out.println("Saving Student..."+rec.toString());
			rec.setCreated(new Date());
			rec.setTimestamp(new Date());
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		UserEntity user = userrepo.findByLoginname(auth.getName());
    		rec.setCompany(user.getCompany());
    		
    		StudentEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;
	}
	
	@RequestMapping("/update")
	public StudentEntity update(@RequestBody StudentEntity rec) throws Exception {
		if(rec!=null){
			System.out.println("Updating Student..."+rec);
			rec.setTimestamp(new Date());
			StudentEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;	
	}
	
	@Transactional
	@RequestMapping("/delete/{recId}")
	public boolean remove(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Deleting Student..."+recId);
			StudentEntity rec = repository.findById(recId);
			if(rec!=null){
				repository.delete(rec);
				return true;
			}
		}
		return false;	
	}
	
	@RequestMapping("/getlist")
	public List<StudentEntity> getlist() throws Exception {
		System.out.println("Getting Entire Student List...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		List<StudentEntity> recs = repository.findByCompany(user.getCompany());
		return recs;
	}
	
	@RequestMapping("/get/{recId}")
	public StudentEntity get(@PathVariable Long recId) throws Exception {
		System.out.println("Retrieving Student..."+recId);
		return repository.findById(recId);
	}
	
	@RequestMapping("/countActiveStudents")
	public StudentEntity countActiveStudents() throws Exception {
		System.out.println("Getting countActiveStudents...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		StudentEntity rec = new StudentEntity();
		rec.setId(repository.countByCompanyAndActive(user.getCompany(), true));
		return rec;
	}
	
	@RequestMapping("/countNoActiveStudents")
	public StudentEntity countNoActiveStudents() throws Exception {
		System.out.println("Getting countNoActiveStudents...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		StudentEntity rec = new StudentEntity();
		rec.setId(repository.countByCompanyAndActive(user.getCompany(), false));
		return rec;
	}
	
}