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

import freewill.nextgen.blts.daos.CheckinRepository;
import freewill.nextgen.blts.daos.CheckinStudentRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.CheckinEntity;
import freewill.nextgen.blts.data.CheckinStudentEntity;
import freewill.nextgen.blts.entities.UserEntity;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;

/** 
 * File:   CheckinManager.java
 * Date:   04/11/2018
 * Author: Benito Vela
 * Refs:   None
 * 
 * This static class provides the business logic to manage CheckinEntity
 * (create, update, remove, getlist).
 * 
**/

@RestController
@RequestMapping("/CheckinEntity")
public class CheckinManager {
	
	@Autowired
	CheckinRepository repository;
	
	@Autowired
	CheckinStudentRepository studentrepo;
	
	@Autowired
	UserRepository userrepo;
	
	@Transactional
	@RequestMapping("/create")
	public CheckinEntity add(@RequestBody CheckinEntity rec) throws Exception {
		if(rec!=null){
			// Injects the new record
			System.out.println("Saving CheckinEntity..."+rec.toString());
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		UserEntity user = userrepo.findByLoginname(auth.getName());
    		rec.setCompany(user.getCompany());
    		rec.setCreated(new Date());
    		for(CheckinStudentEntity std:rec.getStudents()){
    			std.setCompany(user.getCompany());
    			//System.out.println(std);
    		}
    		
    		CheckinEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			for(CheckinStudentEntity std:rec.getStudents()){
				System.out.println(std);
    		}
			res = repository.findById(res.getId());
			return res;
		}
		return null;
	}
	
	@RequestMapping("/update")
	public CheckinEntity update(@RequestBody CheckinEntity rec) throws Exception {
		if(rec!=null){
			System.out.println("Updating CheckinEntity..."+rec);
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		UserEntity user = userrepo.findByLoginname(auth.getName());
			for(CheckinStudentEntity std:rec.getStudents()){
				std.setCompany(user.getCompany());
				System.out.println(std);
    		}
			
			CheckinEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			for(CheckinStudentEntity std:rec.getStudents()){
				System.out.println(std);
    		}
			res = repository.findById(res.getId());
			return res;
		}
		return null;	
	}
	
	@Transactional
	@RequestMapping("/delete/{recId}")
	public boolean remove(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Deleting CheckinEntity..."+recId);
			CheckinEntity rec = repository.findById(recId);
			if(rec!=null){
				repository.delete(rec);
				return true;
			}
		}
		return false;	
	}
	
	@RequestMapping("/getlist")
	public List<CheckinEntity> getlist() throws Exception {
		System.out.println("Getting Entire CheckinEntity List...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		if(user.getRole()==UserRoleEnum.SUPER)
			return (List<CheckinEntity>) repository.findAll();
		else if(user.getRole()==UserRoleEnum.COORD || user.getRole()==UserRoleEnum.ADMIN)
			return (List<CheckinEntity>) repository.findByCompany(user.getCompany());
		else 
			return (List<CheckinEntity>) repository.findByCompanyAndTeacher(user.getCompany(), user.getName());
	}
	
	@RequestMapping("/get/{recId}")
	public CheckinEntity get(@PathVariable Long recId) throws Exception {
		System.out.println("Retrieving CheckinEntity..."+recId);
		return repository.findById(recId);
	}
	
}