package freewill.nextgen.blts;

import java.nio.file.Files;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import freewill.nextgen.blts.apoi.*;
import freewill.nextgen.blts.daos.CompanyRepository;
import freewill.nextgen.blts.daos.DeliverableRepository;
import freewill.nextgen.blts.daos.FeatureRepository;
import freewill.nextgen.blts.daos.FileRepository;
import freewill.nextgen.blts.daos.MappingRepository;
import freewill.nextgen.blts.daos.ProductRepository;
import freewill.nextgen.blts.daos.ProjectRepository;
import freewill.nextgen.blts.daos.RequirementRepository;
import freewill.nextgen.blts.daos.StyleRepository;
import freewill.nextgen.blts.daos.UserRepository;
import freewill.nextgen.blts.data.CompanyEntity;
import freewill.nextgen.blts.data.FileEntity;
import freewill.nextgen.blts.data.ProjectEntity;
import freewill.nextgen.blts.data.ReportInfo;
import freewill.nextgen.blts.entities.UserEntity;

/** 
 * Requirement:   ReportManager.java
 * Date:   21/10/2018
 * Author: Benito Vela
 * Refs:   None
 * 
 * This static class provides the business logic to manage generation of Reports
 * ().
 * 
**/

@RestController
@RequestMapping("/ReportInfo")
public class ReportManager {
	
	@Autowired
	DeliverableRepository delivrepo;
	
	@Autowired
	RequirementRepository reqsrepo;
	
	@Autowired
	MappingRepository mapsrepo;
	
	@Autowired
	FeatureRepository featrepo;
	
	@Autowired
	ProductRepository productrepo;
	
	@Autowired
	StyleRepository stylerepo;
	
	@Autowired
	ProjectRepository projectrepo;
	
	@Autowired
	FileRepository filerepo;
	
	@Autowired
	UserRepository userrepo;
	
	@Autowired
	CompanyRepository companyrepo;
	
	@RequestMapping("/create")
	public ReportInfo createReport(@RequestBody ReportInfo rec) throws Exception {
		if(rec!=null){
			// Injects the new record
			System.out.println("Generating ReportInfo..."+rec.toString());
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    		UserEntity user = userrepo.findByLoginname(auth.getName());
    		CompanyEntity company = companyrepo.findById(user.getCompany());
    		ProjectEntity project = projectrepo.findById(rec.getProject());
    		
    		// Generate report by type
    		Report rep = null;
    		switch(rec.getType()){
    		case FUNCTIONAL:
    			rep = new ReportFuncional(rec.getName(), rec.getType().toString(), 
    					project, company, rec.getProducts(),
    					featrepo, productrepo, stylerepo);
    			break;
    		case FUNCTIONALXLS:
    			rep = new ReportFuncionalXls(rec.getName(), rec.getType().toString(), 
    					project, company, rec.getProducts(),
    					featrepo, productrepo);
    			break;
    		case TENDER:
    			rep = new ReportTender(rec.getName(), rec.getType().toString(), 
    					project, company, rec.getProducts(),
    					featrepo, productrepo, reqsrepo, mapsrepo, userrepo, stylerepo);
    			break;
    		case RFI:
    			rep = new ReportRFI(rec.getName(), rec.getType().toString(), 
    					project, company,
    					featrepo, productrepo, reqsrepo, mapsrepo, userrepo, stylerepo);
    			break;		
    		case DESIGN:
    			rep = new ReportDesign(rec.getName(), rec.getType().toString(), 
    					project, company, rec.getProducts(),
    					featrepo, productrepo, reqsrepo, mapsrepo, userrepo, stylerepo);
    			break;	
    		case TOCXLS:
    			rep = new ReportTOCXls(rec.getName(), rec.getType().toString(), 
    					project, company, false,
    					featrepo, productrepo, reqsrepo, mapsrepo, userrepo);
    			break;
    		case TOCXLSEXT:
    			rep = new ReportTOCXls(rec.getName(), rec.getType().toString(), 
    					project, company, true,
    					featrepo, productrepo, reqsrepo, mapsrepo, userrepo);
    			break;
    		case COSTXLS:
    			rep = new ReportCostsXls(rec.getName(), rec.getType().toString(), 
    					project, company,
    					featrepo, productrepo, reqsrepo, mapsrepo, userrepo);
    			break;
    		case COVERAGE:
    			rep = new ReportCoberturaXls(rec.getName(), rec.getType().toString(), 
    					project, company, rec.getProducts(),
    					featrepo, productrepo, reqsrepo, mapsrepo, userrepo);
    			break;
    		case DELIVERY:
    			rep = new ReportDeliverables(rec.getName(), rec.getType().toString(), 
    					project, company,
    					reqsrepo, mapsrepo, delivrepo, stylerepo);
    			break;
    		}
    		
    		if(rep==null || rep.getFile()==null || !rep.isSuccess())
    			throw new Exception("Error generating Report File");
    		
    		FileEntity res = new FileEntity();
			res.setID(filerepo.getMaxId()+1);
            res.setName(rep.getFile().getName());
            res.setDescription(rec.getType().toString());
            res.setProject(rec.getProject());
            res.setCompany(user.getCompany());
            byte[] array = Files.readAllBytes(rep.getFile().toPath());
            res.setImage(array);
            res.setTimestamp(new Date());
            
            System.out.println("Saving File..."+rec.toString());
			res = filerepo.save(res);
			System.out.println("Id = "+res.getID());
            
            rep.getFile().delete();
            // Set RepoInfo Id with the resulted FileEntity Id and return it
            rec.setId(res.getID());
		}		
		return rec;
	}
	
}