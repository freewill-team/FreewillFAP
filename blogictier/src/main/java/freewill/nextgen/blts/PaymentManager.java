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

import freewill.nextgen.blts.daos.PaymentRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.PaymentEntity;
import freewill.nextgen.blts.entities.UserEntity;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;

/** 
 * File:   PaymentManager.java
 * Date:   06/11/2019
 * Author: Benito Vela
 * Refs:   None
 * 
 * This static class provides the business logic to manage PaymentEntity
 * (create, update, remove, getlist).
 * 
**/

@RestController
@RequestMapping("/PaymentEntity")
public class PaymentManager {
	
	@Autowired
	PaymentRepository repository;
	
	@Autowired
	UserRepository userrepo;
	
	@Transactional
	@RequestMapping("/create")
	public PaymentEntity add(@RequestBody PaymentEntity rec) throws Exception {
		if(rec!=null){
			// Injects the new record
			System.out.println("Saving PaymentEntity..."+rec.toString());
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		UserEntity user = userrepo.findByLoginname(auth.getName());
    		rec.setCompany(user.getCompany());
    		rec.setCreated(new Date());
    		
    		PaymentEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;
	}
	
	@RequestMapping("/update")
	public PaymentEntity update(@RequestBody PaymentEntity rec) throws Exception {
		if(rec!=null){
			System.out.println("Updating PaymentEntity..."+rec);
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		UserEntity user = userrepo.findByLoginname(auth.getName());
			
			PaymentEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;	
	}
	
	@Transactional
	@RequestMapping("/delete/{recId}")
	public boolean remove(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Deleting PaymentEntity..."+recId);
			PaymentEntity rec = repository.findById(recId);
			if(rec!=null){
				repository.delete(rec);
				return true;
			}
		}
		return false;	
	}
	
	@RequestMapping("/getlist")
	public List<PaymentEntity> getlist() throws Exception {
		System.out.println("Getting Entire PaymentEntity List...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		if(user.getRole()==UserRoleEnum.SUPER)
			return (List<PaymentEntity>) repository.findAll();
		else
			return (List<PaymentEntity>) repository.findByCompany(user.getCompany());
	}
	
	@RequestMapping("/getbydate/{lsdate}/{ledate}")
	public List<PaymentEntity> getbydate(
			@PathVariable Long lsdate, @PathVariable Long ledate
			) throws Exception {
		System.out.println("Getting PaymentEntity List by date..."
				+lsdate+","+ledate);
		Date sdate = new Date(lsdate);
		Date edate = new Date(ledate);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		return (List<PaymentEntity>) repository.findByTimestampBetween(
				user.getCompany(), sdate, edate);
	}
	
	@RequestMapping("/getbydateandstudent/{lsdate}/{ledate}/{student}")
	public List<PaymentEntity> getbydateandstudent(
			@PathVariable Long lsdate, @PathVariable Long ledate,
			@PathVariable String student) throws Exception {
		System.out.println("Getting PaymentEntity List by date and student..."
				+lsdate+","+ledate+","+student);
		Date sdate = new Date(lsdate);
		Date edate = new Date(ledate);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		return (List<PaymentEntity>) repository.findByStudentAndTimestampBetween(
				user.getCompany(), student, sdate, edate);
	}
	
	@RequestMapping("/get/{recId}")
	public PaymentEntity get(@PathVariable Long recId) throws Exception {
		System.out.println("Retrieving PaymentEntity..."+recId);
		return repository.findById(recId);
	}
	
}