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

import freewill.nextgen.blts.daos.FeatureRepository;
import freewill.nextgen.blts.daos.ProductRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.ProductEntity;
import freewill.nextgen.blts.entities.UserEntity;

/** 
 * File:   ProductManager.java
 * Date:   22/10/2017
 * Author: Benito Vela
 * Refs:   None
 * 
 * This static class provides the business logic to manage Product
 * (create, update, remove, getlist).
 * 
**/

@RestController
@RequestMapping("/ProductEntity")
public class ProductManager {
	
	@Autowired
	ProductRepository repository;
	
	@Autowired
	FeatureRepository featrepo;
	
	@Autowired
	UserRepository userrepo;

	@RequestMapping("/create")
	public ProductEntity add(@RequestBody ProductEntity rec) throws Exception {
		if(rec!=null){
			// Injects the new record
			System.out.println("Saving Product..."+rec.toString());
			rec.setID(repository.getMaxId()+1);
			rec.setTimestamp(new Date());
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		UserEntity user = userrepo.findByLoginname(auth.getName());
    		rec.setCompany(user.getCompany());
    		
			ProductEntity res = repository.save(rec);
			System.out.println("Id = "+res.getID());
			return res;
		}
		return null;
	}
	
	@RequestMapping("/update")
	public ProductEntity update(@RequestBody ProductEntity rec) throws Exception {
		if(rec!=null){
			System.out.println("Updating Product..."+rec);
			rec.setTimestamp(new Date());
			ProductEntity res = repository.save(rec);
			System.out.println("Id = "+res.getID());
			return res;
		}
		return null;	
	}
	
	@Transactional
	@RequestMapping("/delete/{recId}")
	public boolean remove(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Deleting Product..."+recId);
			ProductEntity rec = repository.findById(recId);
			if(rec!=null){
				repository.delete(rec);
				// delete features and mappings as well
				featrepo.deleteByProduct(recId);
				// maprepo.deleteByProduct(recId);
				return true;
			}
		}
		return false;	
	}
	
	@RequestMapping("/getlist")
	public List<ProductEntity> getlist() throws Exception {
		System.out.println("Getting Entire Product List...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		return (List<ProductEntity>) repository.findByCompany(user.getCompany());
	}
	
	@RequestMapping("/get/{recId}")
	public ProductEntity get(@PathVariable Long recId) throws Exception {
		System.out.println("Retrieving Product..."+recId);
		return repository.findById(recId);
	}
	
	@RequestMapping("/countActiveProducts")
	public ProductEntity countActiveProducts() throws Exception {
		System.out.println("Getting countActiveProducts...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		ProductEntity rec = new ProductEntity();
		rec.setID(repository.countByCompanyAndActive(user.getCompany(), true));
		return rec;
	}
	
	@RequestMapping("/countNoActiveProducts")
	public ProductEntity countNoActiveProducts() throws Exception {
		System.out.println("Getting countNoActiveProducts...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		ProductEntity rec = new ProductEntity();
		rec.setID(repository.countByCompanyAndActive(user.getCompany(), false));
		return rec;
	}
	
}