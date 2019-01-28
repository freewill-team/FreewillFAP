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

import freewill.nextgen.blts.daos.PuntuacionesRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.PuntuacionesEntity;
import freewill.nextgen.blts.entities.UserEntity;

/** 
 * File:   PuntuacionesManager.java
 * Date:   30/12/2018
 * Author: Benito Vela
 * Refs:   None
 * 
 * This static class provides the business logic to manage Puntuaciones
 * (create, update, remove, getlist).
 * 
**/

@RestController
@RequestMapping("/PuntuacionesEntity")
public class PuntuacionesManager {
	
	@Autowired
	PuntuacionesRepository repository;
	
	@Autowired
	UserRepository userrepo;

	@RequestMapping("/create")
	public PuntuacionesEntity add(@RequestBody PuntuacionesEntity rec) throws Exception {
		if(rec!=null){
			// Injects the new record
			System.out.println("Saving Puntuaciones..."+rec.toString());
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		UserEntity user = userrepo.findByLoginname(auth.getName());
    		rec.setCompany(user.getCompany());
    		
    		PuntuacionesEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;
	}
	
	@RequestMapping("/update")
	public PuntuacionesEntity update(@RequestBody PuntuacionesEntity rec) throws Exception {
		if(rec!=null){
			System.out.println("Updating Puntuaciones..."+rec);
			PuntuacionesEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;	
	}
	
	@Transactional
	@RequestMapping("/delete/{recId}")
	public boolean remove(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Deleting Puntuaciones..."+recId);
			PuntuacionesEntity rec = repository.findById(recId);
			if(rec!=null){
				repository.delete(rec);
				return true;
			}
		}
		return false;	
	}
	
	@RequestMapping("/getlist")
	public List<PuntuacionesEntity> getlist() throws Exception {
		System.out.println("Getting Entire Puntuaciones List...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		List<PuntuacionesEntity> recs = repository.findByCompanyOrderByClasificacionAsc(user.getCompany());
		return recs;
	}
	
	@RequestMapping("/get/{recId}")
	public PuntuacionesEntity get(@PathVariable Long recId) throws Exception {
		System.out.println("Retrieving Puntuaciones..."+recId);
		return repository.findById(recId);
	}
	
}