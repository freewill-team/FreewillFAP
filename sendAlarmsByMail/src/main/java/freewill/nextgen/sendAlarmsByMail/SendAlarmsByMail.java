package freewill.nextgen.sendAlarmsByMail;

import java.util.Date;
import java.util.List;

import freewill.nextgen.common.MonitoredProcess;
import freewill.nextgen.common.Utils;
import freewill.nextgen.common.Utils.ADMSservice;
import freewill.nextgen.common.Utils.AlarmDic;
import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.common.entities.EmailEntity;
import freewill.nextgen.common.entities.EventEntity;
import freewill.nextgen.common.entities.MailServerEntity;
import freewill.nextgen.common.entities.UserEntity;
import freewill.nextgen.common.mail.Email;
import freewill.nextgen.common.rtdbclient.RtdbDataService;

/** 
 * File:   SendAlarmsByMail.java
 * Date:   05/10/2017
 * Author: Benito Vela
 * Refs:   None
 * 
 * This program is for processing of events/alarms injected into
 * temporary alarmQueue, and it sends these events/alarms by Mail.
 * 
**/

public class SendAlarmsByMail extends MonitoredProcess {
	
	// Global variables to this class
	private static int MainCycle = 5000;							// Main loop cycle in milliseconds
	private static int MaxNumMails =  9;							// maximum # of mails to be sent each cycle
	private static String PROCESSID = "SendAlarmsByMail";			// Process Name
	private static String SERVICEID = ADMSservice.RTS.toString();	// Service Name
	private static String MSECPERSAMPLE = "MsecPerSample";
	
	public SendAlarmsByMail() {
		super(PROCESSID, SERVICEID);
		// It is mandatory the super() function to be called
	}
	
	/**
	 * @param args the SendAlarmsByMail program does not requires any input parameter
	 */
	public static void main(String[] args)
	{
		SendAlarmsByMail obj = new SendAlarmsByMail();
		obj.Run();
	}
	
	public void Run()
	{		
		// It reads properties
		MainCycle = this.readConfigPropInt("MainCycle", 5000);
		MaxNumMails = this.readConfigPropInt("MaxNumMails", 99);
		String username = this.readConfigPropString("BLTUSERNAME", "foo");
		String password = this.readConfigPropString("BLTPASSWORD", "foo");
		 
		echo("Starting "+this.getFullID()+"...");
		
		String tokenKey = BltClient.get().waitUntilToken(username, password, SERVICEID);
		
		// Initializes performance Monitoring
		this.addKpi(MSECPERSAMPLE);
	        
		// Main loop
		while(this.getProcess().getStopProcess()==false) // near-Infinite loop
		{
			// refresh its own status in processList
			this.checkin();
			
			// Purges and process the event Queue
			purgeEmailQueue(tokenKey);
				
			// Wait until next cycle
			echo("Waiting for next cycle...");
			sleep(MainCycle);
		}
		
		echo("Stoping "+this.getFullID());
		// checkout process
		checkout();
	}

	private void purgeEmailQueue(String token) {
		EmailEntity rec = null;
		try{
			Date tini = new Date();
			int i = MaxNumMails; 	// maximum # of mails to be sent each cycle
			
			List<MailServerEntity> servers = BltClient.get().getEntities(MailServerEntity.class, token);
			
			while((rec=RtdbDataService.get().retrieveEmail())!=null){
				// Sends this event/alarm by Mail to all configured recipients
				echo("New Alarm to be sent: "+rec);
				for(MailServerEntity server:servers){
					echo("Server: "+server.getLabel()+" Active: "+server.getActive());
					// distribuir alarmas por categorias ?
					if(server.getActive() && server.getCompany()==rec.getCompany()){
						
						if(rec.getEmail()!=null && !rec.getEmail().isEmpty()){
							echo("User: N/A Mail: "+rec.getEmail());
							echo("Sending...");
							boolean res = Email.get().Send(rec.getEmail(), 
									rec.getSubject(),
						    		rec.getMessage(),
						    		server.getHostname(),
						    		server.getPort(),
						    		server.getUsername(),
						    		server.getPassword());
							if(res==false){
								// Reports that SendMail has failed
								RtdbDataService.get().pushEvent(new EventEntity(
									new Date(), 
									String.format(AlarmDic.ALM0041.toString(), rec.getEmail(), rec.getSubject()),
									PROCESSID,
									SERVICEID,
									Utils.PROCESSES,
									AlarmDic.ALM0041.getSeverity(),
									AlarmDic.ALM0041.getCategory(),
									Utils.SYSTEMUSER,
									Utils.NOCONSOLE
									));
								echo("FAIL");
							}
							else
								echo("OK");
						}
						else if(server.getDestinations()!=null){
							for(UserEntity person:server.getDestinations()){
								echo("Person: "+person.getName()+" Mail: "+person.getEmail());
								if(person.getEmail()!=null && !person.getEmail().isEmpty()
										&& person.getActive()){
									echo("Sending...");
									boolean res = Email.get().Send(person.getEmail(), 
											rec.getSubject(),
								    		rec.getMessage(),
								    		server.getHostname(),
								    		server.getPort(),
								    		server.getUsername(),
								    		server.getPassword());
									if(res==false){
										// Reports that SendMail has failed
										RtdbDataService.get().pushEvent(new EventEntity(
											new Date(), 
											String.format(AlarmDic.ALM0041.toString(), person.getEmail(), rec.getSubject()),
											PROCESSID,
											SERVICEID,
											Utils.PROCESSES,
											AlarmDic.ALM0041.getSeverity(),
											AlarmDic.ALM0041.getCategory(),
											Utils.SYSTEMUSER,
											Utils.NOCONSOLE
											));
										echo("FAIL");
									}
									else
										echo("OK");
								}
							}
						}
								
						// refresh its status, as sending the mail is time expensive
						this.checkin();
					}
				}
				// Avoids this process to jeopardize the CPU
				if(i--<1) break;
			}
			
			Date tfin = new Date();
			echo("purgeAlarmQueue - Processed "+(MaxNumMails-i)+" alarms in "+
					(tfin.getTime()-tini.getTime())+" msec");
			
			if( (MaxNumMails-i) >0 ){
				double kpi = (tfin.getTime()-tini.getTime())/(MaxNumMails-i);
				this.setKpiValue(MSECPERSAMPLE, kpi); // msec per sample
			}
		}
		catch(Exception e){
			this.getLogger().error(e.getMessage());
			// sends the last failed alarm back to the queue
			if(rec!=null)
				RtdbDataService.get().pushEmail(rec);
		}
	}

}
