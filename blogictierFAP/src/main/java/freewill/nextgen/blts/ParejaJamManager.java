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

import freewill.nextgen.blts.daos.ParejaJamRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.ParejaJamEntity;
import freewill.nextgen.blts.entities.UserEntity;

/** 
 * File:   ParejaJamManager.java
 * Date:   04/12/2018
 * Author: Benito Vela
 * Refs:   None
 * 
 * This static class provides the business logic to manage ParejaJam
 * (create, update, remove, getlist).
 * 
**/

@RestController
@RequestMapping("/ParejaJamEntity")
public class ParejaJamManager {
	
	@Autowired
	ParejaJamRepository repository;
	
	@Autowired
	UserRepository userrepo;

	@RequestMapping("/create")
	public ParejaJamEntity add(@RequestBody ParejaJamEntity rec) throws Exception {
		if(rec!=null){
			// Injects the new record
			System.out.println("Saving ParejaJam..."+rec.toString());
			if(rec.getPatinador1()==null || rec.getPatinador2()==null)
				throw new IllegalArgumentException("Debe indicar los dos integrantes de la pareja.");
			if(rec.getPatinador1().longValue()==rec.getPatinador2().longValue())
				throw new IllegalArgumentException("Los dos integrantes de la pareja no pueden ser el mismo.");
			if(rec.getCategoria()==null)
				throw new IllegalArgumentException("Debe indicar una categoria.");
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		UserEntity user = userrepo.findByLoginname(auth.getName());
    		rec.setCompany(user.getCompany());
    		
			ParejaJamEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;
	}
	
	@RequestMapping("/update")
	public ParejaJamEntity update(@RequestBody ParejaJamEntity rec) throws Exception {
		if(rec!=null){
			System.out.println("Updating ParejaJam..."+rec);
			if(rec.getPatinador1()==null || rec.getPatinador2()==null)
				throw new IllegalArgumentException("Debe indicar los dos integrantes de la pareja.");
			if(rec.getPatinador1().longValue()==rec.getPatinador2().longValue())
				throw new IllegalArgumentException("Los dos integrantes de la pareja no pueden ser el mismo.");
			if(rec.getCategoria()==null)
				throw new IllegalArgumentException("Debe indicar una categoria.");
			
			ParejaJamEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;	
	}
	
	@Transactional
	@RequestMapping("/delete/{recId}")
	public boolean remove(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Deleting ParejaJam..."+recId);
			ParejaJamEntity rec = repository.findById(recId);
			if(rec!=null){
				repository.delete(rec);
				return true;
			}
		}
		return false;	
	}
	
	@RequestMapping("/getlist")
	public List<ParejaJamEntity> getlist() throws Exception {
		System.out.println("Getting Entire ParejaJam List...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		List<ParejaJamEntity> recs = repository.findByCompany(user.getCompany());
		return recs;
	}
	
	@RequestMapping("/get/{recId}")
	public ParejaJamEntity get(@PathVariable Long recId) throws Exception {
		System.out.println("Retrieving ParejaJam..."+recId);
		return repository.findById(recId);
	}
	
}