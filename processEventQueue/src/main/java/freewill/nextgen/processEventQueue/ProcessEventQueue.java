package freewill.nextgen.processEventQueue;

import java.util.Date;

import freewill.nextgen.common.MonitoredProcess;
import freewill.nextgen.common.Utils.ADMSservice;
import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.common.entities.EventEntity;
import freewill.nextgen.common.rtdbclient.RtdbDataService;

/** 
 * File:   ProcessEventQueue.java
 * Date:   05/10/2017
 * Author: Benito Vela
 * Refs:   None
 * 
 * This program is for processing and storing of events injected into
 * temporary eventQueue.
 * 
**/

public class ProcessEventQueue extends MonitoredProcess {
	
	// Global variables to this class
	private static int MainCycle = 5000;							// Main loop cycle in milliseconds
	private static int MaxNumEvents = 999;							// maximum # of events to be processed each cycle
	private static String PROCESSID = "ProcessEventQueue";			// Process Name
	private static String SERVICEID = ADMSservice.RTS.toString();	// Service Name
	private static String MSECPERSAMPLE = "MsecPerSample";			// last cycle average msec per processed event
	
	public ProcessEventQueue() {
		super(PROCESSID, SERVICEID);
		// It is mandatory the super() function to be called
	}
	
	/**
	 * @param args the ProcessEventQueue program does not requires any input parameter
	 */
	public static void main(String[] args)
	{
		ProcessEventQueue obj = new ProcessEventQueue();
		obj.Run();
	}
	
	public void Run()
	{		
		// It reads properties
		MainCycle = this.readConfigPropInt("MainCycle", 5000);
		MaxNumEvents = this.readConfigPropInt("MaxNumEvents", 999);
		String username = this.readConfigPropString("BLTUSERNAME", "foo");
		String password = this.readConfigPropString("BLTPASSWORD", "foo");
		 
		echo("Starting "+this.getFullID()+"...");
		
		String tokenKey = BltClient.get().waitUntilToken(username, password, SERVICEID);
	        
		// Initializes performance Monitoring
		this.addKpi(MSECPERSAMPLE);
		
		// Active restart on failure for this process
		this.setRestartOnFailure(true);
		
		// Main loop
		while(this.getProcess().getStopProcess()==false) // near-Infinite loop
		{
			// refresh its own status in processList
			this.checkin();
			
			// Purges and process the event Queue
			purgeEventQueue(tokenKey);
				
			// Wait until next cycle
			echo("Waiting for next cycle...");
			sleep(MainCycle);
		}
		
		echo("Stoping "+this.getFullID());
		// checkout process
		checkout();
	}

	private void purgeEventQueue(String token) {
		EventEntity rec = null;
		try{
			Date tini = new Date();
			int i = MaxNumEvents; 	// maximum # of events to be processed each cycle
			
			while((rec=RtdbDataService.get().retrieveEvent())!=null){
				// Stores the event
				BltClient.get().createEntity(rec, EventEntity.class, token);
				
				// Avoids this process to jeopardize the CPU
				if(i--<1) break;
			}
			rec = null;
			
			Date tfin = new Date();
			echo("purgeEventQueue - Processed "+(MaxNumEvents-i)+" events in "+
					(tfin.getTime()-tini.getTime())+" msec");
			
			if( (MaxNumEvents-i) >0 ){
				double kpi = (tfin.getTime()-tini.getTime())/(MaxNumEvents-i);
				this.setKpiValue(MSECPERSAMPLE, kpi); // msec per sample
			}
		}
		catch(Exception e){
			this.getLogger().error(e.getMessage());
			// sends the last failed event back to the queue
			if(rec!=null)
				RtdbDataService.get().pushEvent(rec);
		}
	}

}
