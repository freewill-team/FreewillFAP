package freewill.nextgen.blts;

import java.util.Date;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import freewill.nextgen.blts.daos.JobSchedulerRepository;
import freewill.nextgen.blts.entities.JobScheduled;
import freewill.nextgen.blts.entities.JobScheduled.JobStatusEnum;
import freewill.nextgen.common.Utils.AlarmDic;
import freewill.nextgen.common.Utils.ADMSservice;
import freewill.nextgen.common.entities.EventEntity;
import freewill.nextgen.common.rtdbclient.RtdbDataService;

/** 
 * File:   JobSchedulerManager.java
 * Date:   14/10/2017
 * Author: Benito Vela
 * Refs:   None
 * 
 * This static class provides the business logic to manage JobScheduled
 * (create, update, remove, getlist).
 * 
**/

@RestController
@RequestMapping("/JobScheduled")
public class JobSchedulerManager {
	
	@Autowired
	JobSchedulerRepository repository;

	@RequestMapping("/create")
	public JobScheduled add(@RequestBody JobScheduled rec) throws Exception {
		if(rec!=null){
			// Injects the new record
			System.out.println("Saving JobScheduled..."+rec.toString());
			JobScheduled res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;
	}
	
	@RequestMapping("/update")
	public JobScheduled update(@RequestBody JobScheduled rec) throws Exception {
		if(rec!=null){
			System.out.println("Updating JobScheduled..."+rec);
			JobScheduled res = repository.save(rec);
			System.out.println("Id = "+res.getId());
			return res;
		}
		return null;	
	}
	
	@RequestMapping("/delete/{recId}")
	public boolean remove(@PathVariable Long recId) throws Exception {
		if(recId!=null){
			System.out.println("Deleting JobScheduled..."+recId);
			JobScheduled rec = repository.findById(recId);
			if(rec!=null){
				JobScheduled job_ = (JobScheduled)
						RtdbDataService.get().getEntityById(rec.getId(), JobScheduled.class);
				if(job_!=null)
					RtdbDataService.get().deleteEntity(rec, JobScheduled.class);
				repository.delete(rec);
				return true;
			}
		}
		return false;
	}
	
	@RequestMapping("/getlist")
	public List<JobScheduled> getlist() throws Exception {
		System.out.println("Getting Entire JobScheduled List...");
		return (List<JobScheduled>) repository.findAll();
	}
	
	@RequestMapping("/get/{recId}")
	public JobScheduled get(@PathVariable Long recId) throws Exception {
		System.out.println("Retrieving JobScheduled..."+recId);
		return repository.findById(recId);
	}
	
	@RequestMapping("/toggle/{username}/{console}")
	public boolean toggle(@RequestBody JobScheduled rec, 
			@PathVariable String username, 
			@PathVariable String console) throws Exception {
		if( username==null || console==null || rec==null)
			return false;
		
		boolean newstate = !rec.getActive();
		String newstatestr = (newstate==true? "Enabled":"Disabled"); // i18n
		rec.setActive(newstate);
		if(newstate==true)
			rec.setState(JobStatusEnum.RUN);
		else
			rec.setState(JobStatusEnum.STOP);
		
		System.out.println("Updating..."+rec+ " to " +newstatestr);
		JobScheduled res = repository.save(rec);
		
		// It also injects a new event storing the change
		RtdbDataService.get().pushEvent(new EventEntity(
			new Date(), 
			String.format(AlarmDic.ALM0011.toString(), res.getId(), newstatestr),
			res.getLabel(),
			ADMSservice.RTS.toString(), // ParentPoint
			"Job Scheduler", // PointType
			AlarmDic.ALM0011.getSeverity(),
			AlarmDic.ALM0011.getCategory(),
			username,
			console
			));
		
		return true;
	}
	
}