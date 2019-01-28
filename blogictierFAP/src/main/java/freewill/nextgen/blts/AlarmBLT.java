package freewill.nextgen.blts;

import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import freewill.nextgen.common.Utils.AlarmDic;
import freewill.nextgen.common.entities.AlarmEntity;
import freewill.nextgen.common.entities.EventEntity;
import freewill.nextgen.common.rtdbclient.RtdbDataService;

/** 
 * File:   AlarmBLT.java
 * Date:   14/09/2017
 * Author: Benito Vela
 * Refs:   None
 * 
 * This static class provides the business logic to manage alarms 
 * (create, update, acknowledge, remove, getlist).
 * 
**/

@RestController
@RequestMapping("/AlarmEntity")
public class AlarmBLT {

	@RequestMapping("/create")
	public AlarmEntity add(@RequestBody AlarmEntity alm) throws Exception {
		List<AlarmEntity> list = RtdbDataService.get().getAlarmsByPoint(alm.getPoint(), alm.getPointType());
		Boolean found = false;
		for(AlarmEntity rec : list){
			if(rec.getMessage().equals(alm.getMessage())){
				found=true;
				break;
			}
		}
		
		if(!found){ // Avoids repeated records in Alarms and Events
			// Injects the Alarm
			RtdbDataService.get().createAlarm(alm);
			/*// It also injects a new event
			RtdbDataService.get().pushEvent(new EventEntity(
					alm.getTimestamp(), 
					alm.getMessage(),
					alm.getPoint(),
					alm.getParentPoint(),
					alm.getPointType(),
					alm.getSeverity(),
					alm.getCategory(),
					Utils.SYSTEMUSER,
					Utils.findHostName()
					));*/
		}
		return alm;
	}
	
	@RequestMapping("/update")
	public AlarmEntity update(@RequestBody AlarmEntity alm) throws Exception {
		RtdbDataService.get().updateAlarm(alm);
		return alm;
	}

	@RequestMapping("/ack/{username}/{console}")
	public boolean ack(@RequestBody AlarmEntity alm, 
			@PathVariable String username, 
			@PathVariable String console) throws Exception {
		if( username==null || console==null || alm==null)
			return false;
		if( alm.getRemove() )
			RtdbDataService.get().deleteAlarm(alm);
		else{
			alm.setFlashing(false);
			RtdbDataService.get().updateAlarm(alm);
		}
		
		// It also injects a new event storing the acknowledgment
		RtdbDataService.get().pushEvent(new EventEntity(
			new Date(), 
			String.format(AlarmDic.ALM0002.toString(), alm.getPoint(), username, console),
			alm.getPoint(),
			alm.getParentPoint(),
			alm.getPointType(),
			alm.getSeverity(),
			alm.getCategory(),
			username,
			console
			));
		System.out.println("Alarm Acknowledged..."+alm.toString());
		
		return true;
	}
	
	@RequestMapping("/delete")
	public boolean remove(@RequestBody AlarmEntity alm) throws Exception {
		RtdbDataService.get().deleteAlarm(alm);
		return true;
	}
	
	@RequestMapping("/getlist")
	public List<AlarmEntity> getlist() throws Exception {
		return RtdbDataService.get().getAlarms();
	}
	
	@RequestMapping("/getbypoint")
	public List<AlarmEntity> getbypoint(@RequestBody AlarmEntity alm) throws Exception {
		return RtdbDataService.get().getAlarmsByPoint(alm.getPoint(), alm.getPointType());
	}
	
}