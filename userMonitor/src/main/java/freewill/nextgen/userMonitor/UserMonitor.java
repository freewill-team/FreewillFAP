package freewill.nextgen.userMonitor;

import java.util.Date;
import java.util.List;

import freewill.nextgen.common.MonitoredProcess;
import freewill.nextgen.common.Utils.ADMSservice;
import freewill.nextgen.common.Utils.AlarmDic;
import freewill.nextgen.common.entities.AlarmEntity;
import freewill.nextgen.common.entities.LoginEntity;
import freewill.nextgen.common.rtdbclient.RtdbDataService;

/** 
 * File:   UserMonitor.java
 * Date:   11/03/2018
 * Author: Benito Vela
 * Refs:   None
 * 
 * This program is for monitoring of logged users. When a user heartbeat
 * is not longer received, an alarm is declared.
 * 
**/

public class UserMonitor extends MonitoredProcess {
	
	// Global variables to this class
	private static int MainCycle = 5000;							// Main loop cycle in milliseconds
	private static String PROCESSID = "UserMonitor";				// Process Name
	private static String SERVICEID = ADMSservice.INFRA.toString();	// Service Name
	private static int timeout = 180000;							// User heartbeat timeout
	private static String MSECPERCYCLE = "MsecPerCycle";
	
	public UserMonitor() {
		super(PROCESSID, SERVICEID);
		// It is mandatory the super() function to be called
	}
	
	/**
	 * @param args the ProcessMonitor program does not requires any input parameter
	 */
	public static void main(String[] args)
	{
		UserMonitor obj = new UserMonitor();
		obj.Run();
	}

	/**
	 * The sysMonitor's Run() does not requires any input parameter
	 */
	public void Run()
	{		
		// It reads properties
		MainCycle = this.readConfigPropInt("MainCycle", 5000);
		timeout = this.readConfigPropInt("UserTimeout", 180000);
		//String username = this.readConfigPropString("BLTUSERNAME", "foo");
		//String password = this.readConfigPropString("BLTPASSWORD", "foo");
		 
		echo("Starting "+this.getFullID()+"...");
		
		//String tokenKey = BltClient.get().waitUntilToken(username, password);
		
		// Initializes performance Monitoring
		this.addKpi(MSECPERCYCLE);
				
		// Active restart on failure for this process
		this.setRestartOnFailure(true);		
	    
		// Main loop
		while(this.getProcess().getStopProcess()==false) // near-Infinite loop
		{
			long ini = new Date().getTime();
			// refresh its own status in processList
			this.checkin();
				
			// Checks for user hearbeats
			verifyUsers();
			
			// calculate and publish time consumed during the cycle
			long delta = (new Date().getTime())-ini; 
			this.setKpiValue(MSECPERCYCLE, delta);
				
			// Wait until next cycle
			echo("Waiting for next cycle...");
			sleep(MainCycle);
		}
			
		// Close MBeanServer connection
		echo("Stoping "+this.getFullID());
		// checkout process
		checkout();
	}

	private void verifyUsers() {
		// Verifies whether some logged user has not send its heartbeat
		try{
			List<LoginEntity> loginList = RtdbDataService.get().getEntities(LoginEntity.class);
			
			// Process local processes status
			echo("Checking current logged users:");
	    	for(LoginEntity obj : loginList)
			{
	    		echo(obj.toString());
	    		
				// Checks process health
				if( (System.currentTimeMillis() - obj.getLastCheckin().getTime()) > timeout)
				{
					// Set process status to Fail
					echo("User '"+obj.getID()+"' declared as Inactive");
					
					// Generates Alarm
					Date tstamp = new Date();
					RtdbDataService.get().createAlarm(new AlarmEntity(null, 
								tstamp, 
								String.format(AlarmDic.ALM0024.toString(), obj.getName(), obj.getConsole()),
								obj.getID(),
								SERVICEID,
								PROCESSID,
								AlarmDic.ALM0024.getSeverity(),
								AlarmDic.ALM0024.getCategory(),
								true,
								true
							));
					/*// also injects the event
					RtdbDataService.get().pushEvent(new EventEntity(
								tstamp, 
								String.format(AlarmDic.ALM0024.toString(), obj.getName(), obj.getConsole()),
								obj.getID(),
								SERVICEID,
								PROCESSID,
								AlarmDic.ALM0024.getSeverity(),
								AlarmDic.ALM0024.getCategory(),
							    Utils.SYSTEMUSER,
							    Utils.NOCONSOLE
							));*/
					
					// Finally, unregister user from Logins table
	    	        RtdbDataService.get().userCheckout(obj.getName(), obj.getConsole(), obj.getApplication());
				}
			}
		}
		catch(Exception e){
			this.getLogger().error(e.getMessage());
		}
	}
	
}
