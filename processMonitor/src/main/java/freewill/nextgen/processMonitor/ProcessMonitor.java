package freewill.nextgen.processMonitor;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import freewill.nextgen.common.MonitoredProcess;
import freewill.nextgen.common.Utils;
import freewill.nextgen.common.Utils.ADMSservice;
import freewill.nextgen.common.Utils.AlarmDic;
import freewill.nextgen.common.Utils.ServiceStatusEnum;
import freewill.nextgen.common.entities.AlarmEntity;
import freewill.nextgen.common.entities.ProcessEntity;
import freewill.nextgen.common.entities.ServerEntity;
import freewill.nextgen.common.entities.ServiceEntity;
import freewill.nextgen.common.rtdbclient.RtdbDataService;

/** 
 * File:   ProcessMonitor.java
 * Date:   15/08/2017
 * Author: Benito Vela
 * Refs:   None
 * 
 * This program is for monitoring of local machine processes, and so calculate
 * both service and server health.
 * It is also able to start (previously registered) local processes.
 * 
**/

public class ProcessMonitor extends MonitoredProcess {
	
	// Global variables to this class
	private static int MainCycle = 5000;							// Main loop cycle in milliseconds
	private static String PROCESSID = "ProcessMonitor";				// Process Name
	private static String SERVICEID = ADMSservice.INFRA.toString();	// Service Name
	
	public ProcessMonitor() {
		super(PROCESSID, SERVICEID);
		// It is mandatory the super() function to be called
	}
	
	/**
	 * @param args the ProcessMonitor program does not requires any input parameter
	 */
	public static void main(String[] args)
	{
		ProcessMonitor obj = new ProcessMonitor();
		obj.Run();
	}

	/**
	 * The sysMonitor's Run() does not requires any input parameter
	 */
	public void Run()
	{		
		// It reads properties
		MainCycle = this.readConfigPropInt("MainCycle", 5000);
		 
		echo("Starting "+this.getFullID()+"...");
		
		// Initializes performance Monitoring
		//MonitoredProcessKPIs kpis = new MonitoredProcessKPIs(this.getProcess(), null);
		// Add additional KPIs here kpis.addKpi(MSECPERSAMPLE);
	        
		// Main loop
		while(this.getProcess().getStopProcess()==false) // near-Infinite loop
		{
			// refresh its own status in processList
			this.checkin();
				
			// Checks for local Processes Health
			verifyLocalProcesses();
			// Calculates local Services and Server Health
			verifyLocalServices();
			// Checks processes to be started
			startProcesses();
				
			// Wait until next cycle
			echo("Waiting for next cycle...");
			sleep(MainCycle);
		}
			
		// Close MBeanServer connection
		echo("Stoping "+this.getFullID());
		// checkout process
		checkout();
	}

	private void verifyLocalProcesses() {
		// Verifies whether every local process has Checked in on time
		try{
			List<ProcessEntity> processList = RtdbDataService.get().getProcesses();
			
			// Process local processes status
			echo("Checking current running processes:");
	    	for(ProcessEntity obj : processList)
			{
	    		if(!obj.getServer().equals(this.getProcess().getServer())) 
	    			continue; // Only check local processes
	    		
	    		echo(obj.toString());
	    		// Only checks processes with Good state. Failed or Stopped will be ignored.
	    		if(obj.getStatus() == ServiceStatusEnum.GOOD)
				{
					// Checks process health
					if( (System.currentTimeMillis() - obj.getTimestamp()) > obj.getTimeout())
					{
						// Set process status to Fail
						echo("VerifyProcess marked '"+obj.getID()+"' as Failed");
						obj.setStatus(ServiceStatusEnum.FAILED);
						// checks restart on failure
						if(obj.getRestartOnFailure())
							obj.setStartProcess(true);
						// saves process status
						RtdbDataService.get().updateProcess(obj);
						// Generates Alarm
						Date tstamp = new Date();
						RtdbDataService.get().createAlarm(new AlarmEntity(null, 
								tstamp, 
								String.format(AlarmDic.ALM0001.toString(), obj.getID()),
								obj.getID(),
								SERVICEID,
								PROCESSID,
								AlarmDic.ALM0001.getSeverity(),
								AlarmDic.ALM0001.getCategory(),
								true,
								true
								));
						/*// also injects the event
						RtdbDataService.get().pushEvent(new EventEntity(
								tstamp, 
								String.format(AlarmDic.ALM0001.toString(), obj.getID()),
								obj.getID(),
								SERVICEID,
								PROCESSID,
								AlarmDic.ALM0001.getSeverity(),
								AlarmDic.ALM0001.getCategory(),
							    Utils.SYSTEMUSER,
							    Utils.NOCONSOLE
								));*/
					}
				}
			}
		}
		catch(Exception e){
			this.getLogger().error(e.getMessage());
		}
	}

	private void verifyLocalServices() {
		// Now infers status of local services and local server
		try{
			List<ProcessEntity> processList = RtdbDataService.get().getProcesses();
			String serverId = Utils.NOSYSTEM+":"+getProcess().getServer();
			HashMap<String, ServiceEntity> services = new HashMap<String, ServiceEntity>();
			System.out.println("\nChecking Server "+serverId);
			
			// First set Local Server status to Starting (locally)
			ServerEntity server = (ServerEntity)
					RtdbDataService.get().getEntityById(serverId, ServerEntity.class);
			if(server!=null)
				server.setStatus(ServiceStatusEnum.STARTING);
			else 
				System.out.println("Error reading server "+serverId);
			
			// Second set local Services status to Starting (locally)
			List<ServiceEntity> serviceList = RtdbDataService.get().getEntities(ServiceEntity.class);
			for(ServiceEntity obj:serviceList){
				if(!obj.getServer().equals(this.getProcess().getServer())) 
	    			continue; // Only local services
				ServiceEntity service = services.get(obj.getID());
				if(service==null){ 
					// avoid duplicates
					obj.setStatus(ServiceStatusEnum.STARTING);
					services.put(obj.getID(), obj);
					System.out.println("Adding Service "+obj.getID());
				}
			}
			
			// Third Process local processes status to set final states
			echo("Checking current running processes:");
	    	for(ProcessEntity obj : processList)
			{
	    		if(!obj.getServer().equals(this.getProcess().getServer())) 
	    			continue; // Only check local processes
	    		
	    		String serviceId = serverId+":"+obj.getService();
	    		System.out.println("Using Process "+obj.getID()+ " status="+obj.getStatus());
				ServiceEntity service = services.get(serviceId);
	    		if(service!=null){
	    			System.out.println("Using Service "+serviceId+ " status="+service.getStatus());
	    			
	    			if(service.getStatus()==ServiceStatusEnum.STARTING)
	    				service.setStatus(obj.getStatus());
	    			if(server.getStatus()==ServiceStatusEnum.STARTING)
	    				server.setStatus(obj.getStatus());
	    			
	    			if(service.getStatus()==ServiceStatusEnum.GOOD &&
	    				(obj.getStatus()==ServiceStatusEnum.STOP ||
	    				obj.getStatus()==ServiceStatusEnum.FAILED) )
	    				service.setStatus(obj.getStatus());
	    			if(server.getStatus()==ServiceStatusEnum.GOOD &&
		    			(obj.getStatus()==ServiceStatusEnum.STOP ||
		   				obj.getStatus()==ServiceStatusEnum.FAILED) )
		   				server.setStatus(obj.getStatus());
	    			
	    			if(service.getStatus()==ServiceStatusEnum.STOP &&
		   				obj.getStatus()==ServiceStatusEnum.FAILED )
		   				service.setStatus(obj.getStatus());
		    		if(server.getStatus()==ServiceStatusEnum.STOP &&
		    			obj.getStatus()==ServiceStatusEnum.FAILED )
		    			server.setStatus(obj.getStatus());
		    		
		    		System.out.println("New Service status="+service.getStatus());
		    		System.out.println("New Server status="+server.getStatus());
	    		}
	    		else
	    			System.out.println("Error reading service "+serviceId);
			}
	    	
	    	// Finally, saves/stores final server states
	    	if(server!=null)
	    		RtdbDataService.get().updateEntity(server, ServerEntity.class);
    		System.out.println("Final Server status="+server.getStatus());
	    	for(ServiceEntity obj:services.values()){
				RtdbDataService.get().updateEntity(obj, ServiceEntity.class);
				System.out.println("Final Service status="+obj.getStatus());
			}
	    	
		}
		catch(Exception e){
			e.printStackTrace();
			this.getLogger().error(e.getMessage());
		}		
	}
	
	private void startProcesses() {
		// Checks whether some local process has been requested to be started, and proceed
		try{
			List<ProcessEntity> processList = RtdbDataService.get().getProcesses();
			
			// Process local processes status
			echo("Checking current running processes to be started:");
	    	for(ProcessEntity obj : processList)
			{
	    		if(!obj.getServer().equals(this.getProcess().getServer())) 
	    			continue; // Only check local processes
	    		
	    		echo(obj.toString());
	    		// Only checks processes with Non Good (Running) state.
	    		if(obj.getStatus() != ServiceStatusEnum.GOOD)
				{
					if(obj.getStartProcess()){
						// Start process
						echo("Starting process '"+obj.getID());
						obj.setStartProcess(false);
						RtdbDataService.get().updateProcess(obj);
						if(startLocalProcess(obj)==false){
							// Generates Alarm
							RtdbDataService.get().createAlarm(new AlarmEntity(null, 
									new Date(), 
									String.format(AlarmDic.ALM0007.toString(), obj.getID()),
									obj.getID(),
									SERVICEID,
									PROCESSID,
									AlarmDic.ALM0007.getSeverity(),
									AlarmDic.ALM0007.getCategory(),
									true,
									true
									));
						}
					}
				}
			}
		}
		catch(Exception e){
			this.getLogger().error(e.getMessage());
		}
	}
	
	private boolean startLocalProcess(ProcessEntity obj) {
		// Starts a Local Process
		try{
			String processName = obj.getFullpath();
			String command = "java -jar "+processName;
			Process process = Runtime.getRuntime().exec(command);
			this.sleep(500);
		    if(process.isAlive())
		    	return true;
		}
		catch(Exception e){
			this.getLogger().error(e.getMessage());
		}
		return false;
	}
}
