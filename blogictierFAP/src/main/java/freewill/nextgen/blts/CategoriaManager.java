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

import freewill.nextgen.blts.daos.CategoriaRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.CategoriaEntity;
import freewill.nextgen.blts.data.CategoriaEntity.ModalidadEnum;
import freewill.nextgen.blts.entities.UserEntity;

/** 
 * File:   CategoriaManager.java
 * Date:   05/12/2018
 * Author: Benito Vela
 * Refs:   None
 * 
 * This static class provides the business logic to manage Categoria
 * (create, update, remove, getlist).
 * 
**/

@RestController
@RequestMapping("/CategoriaEntity")
public class CategoriaManager {
	
	@Autowired
	CategoriaRepository repository;
	
	@Autowired
	UserRepository userrepo;

	@RequestMapping("/create")
	public CategoriaEntity add(@RequestBody CategoriaEntity rec) throws Exception {
		if(rec!=null){
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    	UserEntity user = userrepo.findByLoginname(auth.getName());
			CategoriaEntity old = repository.findByNombreAndCompany(rec.getNombre(), user.getCompany());
			if(old!=null)
				throw new IllegalArgumentException("Este registro ya existe. Cambie el nombre.");
			// Injects the new record
			System.out.println("Saving Categoria..."+rec.toString());
    		rec.setCompany(user.getCompany());
    		
			CategoriaEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;
	}
	
	@RequestMapping("/update")
	public CategoriaEntity update(@RequestBody CategoriaEntity rec) throws Exception {
		if(rec!=null){
			System.out.println("Updating Categoria..."+rec);
			CategoriaEntity res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;	
	}
	
	@Transactional
	@RequestMapping("/delete/{recId}")
	public boolean remove(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Deleting Categoria..."+recId);
			CategoriaEntity rec = repository.findById(recId);
			if(rec!=null){
				repository.delete(rec);
				return true;
			}
		}
		return false;	
	}
	
	@RequestMapping("/getlist")
	public List<CategoriaEntity> getlist() throws Exception {
		System.out.println("Getting Entire Categoria List...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		List<CategoriaEntity> recs = repository.findByCompanyOrderByNombreAsc(user.getCompany());
		return recs;
	}
	
	@RequestMapping("/get/{recId}")
	public CategoriaEntity get(@PathVariable Long recId) throws Exception {
		System.out.println("Retrieving Categoria..."+recId);
		return repository.findById(recId);
	}
	
	@RequestMapping("/getByModalidad/{modalidad}")
	public List<CategoriaEntity> getByModalidad(@PathVariable ModalidadEnum modalidad) throws Exception {
		System.out.println("Getting Categoria List By Modalidad..."+modalidad);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		List<CategoriaEntity> recs = repository.findByModalidadAndCompany(modalidad, user.getCompany());
		return recs;
	}
	
}