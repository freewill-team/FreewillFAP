package freewill.nextgen.blts;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import freewill.nextgen.blts.daos.CompanyRepository;
import freewill.nextgen.blts.daos.StyleRepository;
import freewill.nextgen.blts.data.CompanyEntity;
import freewill.nextgen.blts.data.Style;
import freewill.nextgen.blts.data.Style.StyleEnum;

/** 
 * File:   CompanyManager.java
 * Date:   22/10/2017
 * Author: Benito Vela
 * Refs:   None
 * 
 * This static class provides the business logic to manage Company
 * (create, update, remove, getlist).
 * 
**/

@RestController
@RequestMapping("/CompanyEntity")
public class CompanyManager {
	
	@Autowired
	CompanyRepository repository;
	
	@Autowired
	StyleRepository stylerepo;
	
	@Transactional
	@RequestMapping("/create")
	public CompanyEntity add(@RequestBody CompanyEntity rec) throws Exception {
		if(rec!=null){
			// Injects the new record
			System.out.println("Saving Company..."+rec.toString());
			rec.setID(repository.getMaxId()+1);
			rec.setTimestamp(new Date());
			CompanyEntity res = repository.save(rec);
			System.out.println("Id = "+res.getID());
			// create default styles
    		for(StyleEnum sty:StyleEnum.values()){
    			Style style = new freewill.nextgen.blts.data.Style();
    			style.setCompany(res.getID());
    			style.setLevel(sty);
    			style.setStyleid(sty.toString());
    			switch(sty){
    				case H1: style.setName("Ttulo1"); break;
    				case H2: style.setName("Ttulo2"); break;
    				case H3: style.setName("Ttulo3"); break;
    				case H4: style.setName("Ttulo4"); break;
    				case H5: style.setName("Ttulo5"); break;
    				case H6: style.setName("Ttulo6"); break;
    				case H7: style.setName("Ttulo7"); break;
    				case H8: style.setName("Ttulo8"); break;
    				case PARAGRAM: style.setName("VIÑETA"); break;
    				case FIGURE: style.setName("Figure"); break;
    				default: style.setName("Normal"); break;
    			}
    			stylerepo.save(style);
    		}
			return res;
		}
		return null;
	}
	
	@RequestMapping("/update")
	public CompanyEntity update(@RequestBody CompanyEntity rec) throws Exception {
		if(rec!=null){
			System.out.println("Updating Company..."+rec);
			rec.setTimestamp(new Date());
			CompanyEntity res = repository.save(rec);
			System.out.println("Id = "+res.getID());
			return res;
		}
		return null;	
	}
	
	@Transactional
	@RequestMapping("/delete/{recId}")
	public boolean remove(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Deleting Company..."+recId);
			CompanyEntity rec = repository.findById(recId);
			if(rec!=null){
				repository.delete(rec);
				// delete styles as well
				stylerepo.deleteByCompany(recId);
				//prjrepo.deleteByCompany(recId);
				//prdrepo.deleteByCompany(recId);
				//featrepo.deleteByCompany(recId);
				//reqsrepo.deleteByCompany(recId);
				return true;
			}
		}
		return false;	
	}
	
	@RequestMapping("/getlist")
	public List<CompanyEntity> getlist() throws Exception {
		System.out.println("Getting Entire Company List...");
		return (List<CompanyEntity>) repository.findAll();
	}
	
	@RequestMapping("/get/{recId}")
	public CompanyEntity get(@PathVariable Long recId) throws Exception {
		System.out.println("Retrieving Company..."+recId);
		return repository.findById(recId);
	}
	
}