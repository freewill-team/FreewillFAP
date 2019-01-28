package freewill.nextgen.blts;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import freewill.nextgen.blts.daos.CircuitoRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.CircuitoEntity;
import freewill.nextgen.blts.entities.UserEntity;

/** 
 * File:   CircuitoManager.java
 * Date:   04/12/2018
 * Author: Benito Vela
 * Refs:   None
 * 
 * This static class provides the business logic to manage Circuito
 * (create, update, remove, getlist).
 * 
**/

@RestController
@RequestMapping("/CircuitoEntity")
public class CircuitoManager {
	
	@Autowired
	CircuitoRepository repository;
	
	@Autowired
	UserRepository userrepo;

	@RequestMapping("/create")
	public CircuitoEntity add(@RequestBody CircuitoEntity rec) throws Exception {
		if(rec!=null){
			// Injects the new record
			System.out.println("Saving Circuito..."+rec.toString());
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		UserEntity user = userrepo.findByLoginname(auth.getName());
    		rec.setCompany(user.getCompany());
    		
			CircuitoEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;
	}
	
	@RequestMapping("/update")
	public CircuitoEntity update(@RequestBody CircuitoEntity rec) throws Exception {
		if(rec!=null){
			System.out.println("Updating Circuito..."+rec);
			CircuitoEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;	
	}
	
	@Transactional
	@RequestMapping("/delete/{recId}")
	public boolean remove(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Deleting Circuito..."+recId);
			CircuitoEntity rec = repository.findById(recId);
			if(rec!=null){
				repository.delete(rec);
				return true;
			}
		}
		return false;	
	}
	
	@RequestMapping("/getlist")
	public List<CircuitoEntity> getlist() throws Exception {
		System.out.println("Getting Entire Circuito List...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		List<CircuitoEntity> recs = repository.findByCompanyOrderByTemporadaDesc(user.getCompany());
		return recs;
	}
	
	@RequestMapping("/get/{recId}")
	public CircuitoEntity get(@PathVariable Long recId) throws Exception {
		System.out.println("Retrieving Circuito..."+recId);
		return repository.findById(recId);
	}
	
	@RequestMapping("/getCircuito/{year}")
	public CircuitoEntity getCircuito(@PathVariable Integer year) throws Exception {
		System.out.println("Getting Circuito By Year..."+year);
		//Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//UserEntity user = userrepo.findByLoginname(auth.getName());
		CircuitoEntity rec = repository.findByTemporada(year);
		return rec;
	}
}