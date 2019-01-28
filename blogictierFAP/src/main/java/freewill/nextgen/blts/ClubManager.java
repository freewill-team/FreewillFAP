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

import freewill.nextgen.blts.daos.ClubRepository;
import freewill.nextgen.blts.daos.PatinadorRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.ClubEntity;
import freewill.nextgen.blts.entities.UserEntity;

/** 
 * File:   ClubManager.java
 * Date:   02/12/2018
 * Author: Benito Vela
 * Refs:   None
 * 
 * This static class provides the business logic to manage Club
 * (create, update, remove, getlist).
 * 
**/

@RestController
@RequestMapping("/ClubEntity")
public class ClubManager {
	
	@Autowired
	ClubRepository repository;
	
	@Autowired
	UserRepository userrepo;
	
	@Autowired
	PatinadorRepository patirepo;

	@RequestMapping("/create")
	public ClubEntity add(@RequestBody ClubEntity rec) throws Exception {
		if(rec!=null){
			// Injects the new record
			System.out.println("Saving Club..."+rec.toString());
			//rec.setCreated(new Date());
			//rec.setTimestamp(new Date());
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		UserEntity user = userrepo.findByLoginname(auth.getName());
    		rec.setCompany(user.getCompany());
    		
			ClubEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;
	}
	
	@RequestMapping("/update")
	public ClubEntity update(@RequestBody ClubEntity rec) throws Exception {
		if(rec!=null){
			System.out.println("Updating Club..."+rec);
			//rec.setTimestamp(new Date());
			ClubEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;	
	}
	
	@Transactional
	@RequestMapping("/delete/{recId}")
	public boolean remove(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Deleting Club..."+recId);
			ClubEntity rec = repository.findById(recId);
			if(rec!=null){
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    		UserEntity user = userrepo.findByLoginname(auth.getName());
				long patinadores = patirepo.countByCompanyAndActive(user.getCompany(), true);
				if(patinadores>0)
					throw new IllegalArgumentException("No puede borrar Clubes con Patinadores activos."); 
				repository.delete(rec);
				return true;
			}
		}
		return false;	
	}
	
	@RequestMapping("/getlist")
	public List<ClubEntity> getlist() throws Exception {
		System.out.println("Getting Entire Club List...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		List<ClubEntity> recs = repository.findByCompanyOrderByNombreAsc(user.getCompany());
		return recs;
	}
	
	@RequestMapping("/get/{recId}")
	public ClubEntity get(@PathVariable Long recId) throws Exception {
		System.out.println("Retrieving Club..."+recId);
		return repository.findById(recId);
	}
	
}