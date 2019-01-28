package freewill.nextgen.blts;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import freewill.nextgen.blts.daos.FileRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.FileEntity;
import freewill.nextgen.blts.entities.UserEntity;

/** 
 * File:   FileManager.java
 * Date:   22/10/2017
 * Author: Benito Vela
 * Refs:   None
 * 
 * This static class provides the business logic to manage File
 * (create, update, remove, getlist).
 * 
**/

@RestController
@RequestMapping("/FileEntity")
public class FileManager {
	
	@Autowired
	FileRepository repository;
	
	@Autowired
	UserRepository userrepo;

	@RequestMapping("/create")
	public FileEntity add(@RequestBody FileEntity rec) throws Exception {
		if(rec!=null){
			// Injects the new record
			System.out.println("Saving File..."+rec.toString());
			rec.setID(repository.getMaxId()+1);
			rec.setTimestamp(new Date());
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		UserEntity user = userrepo.findByLoginname(auth.getName());
    		rec.setCompany(user.getCompany());
    		
			FileEntity res = repository.save(rec);
			System.out.println("Id = "+res.getID());
			return res;
		}
		return null;
	}
	
	@RequestMapping("/update")
	public FileEntity update(@RequestBody FileEntity rec) throws Exception {
		if(rec!=null){
			System.out.println("Updating File..."+rec);
			rec.setTimestamp(new Date());
			FileEntity res = repository.save(rec);
			System.out.println("Id = "+res.getID());
			return res;
		}
		return null;	
	}
	
	@RequestMapping("/delete/{recId}")
	public boolean remove(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Deleting File..."+recId);
			FileEntity rec = repository.findById(recId);
			if(rec!=null){
				repository.delete(rec);
				return true;
			}
		}
		return false;	
	}
	
	@RequestMapping("/getlist")
	public List<FileEntity> getlist() throws Exception {
		System.out.println("Getting Entire File List...");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		List<FileEntity> recs = (List<FileEntity>) repository.findByCompany(user.getCompany());
		for(FileEntity rec:recs){
			rec.setImage(new byte[0]);
		}
		return recs;
	}
	
	@RequestMapping("/get/{recId}")
	public FileEntity get(@PathVariable Long recId) throws Exception {
		System.out.println("Retrieving File..."+recId);
		return repository.findById(recId);
	}
	
	@RequestMapping("/getbyproject/{recId}")
	public List<FileEntity> getlistbyproject(@PathVariable Long recId) throws Exception {
		System.out.println("Getting File List per project..."+recId);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		UserEntity user = userrepo.findByLoginname(auth.getName());
		List<FileEntity> recs = repository.findByCompanyAndProject(user.getCompany(), recId);
		for(FileEntity rec:recs){
			rec.setImage(new byte[0]);
		}
		return recs;
	}
	
}