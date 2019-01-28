package freewill.nextgen.sysmon;

import java.util.Date;
import java.io.*;

import freewill.nextgen.common.MonitoredProcess;
import freewill.nextgen.common.Utils.ADMSservice;
import freewill.nextgen.common.Utils.AlarmDic;
import freewill.nextgen.common.entities.AlarmEntity;
import freewill.nextgen.common.entities.ServerPerformanceEntity;
import freewill.nextgen.common.rtdbclient.RtdbDataService;
import oshi.SystemInfo;

/** 
 * File:   SysMonitor.java
 * Date:   27/08/2016 (refactorized on 06/09/2017)
 * Author: Benito Vela
 * Refs:   None
 * 
 * This program is for monitoring of local machine health and performance:
 * - hard disk usage
 * - cpu usage
 * - RAM memory usage
 * - net I/O usage
 * - etc.
 * 
 * It collects those performance monitors and injects this data into the 
 * realtime In-memory DataGrid.
 * 
 */

public class SysMonitor extends MonitoredProcess {
	
	// Global variables to this class
	private ServerPerformanceEntity serverPerformance = null;		// To store local server Performance Monitors
	private int MainCycle = 10000;									// Main loop cycle
	private static String PROCESSID = "SystemMonitor";				// Process Name
	private static String SERVICEID = ADMSservice.INFRA.toString();	// Service Name
	// limites de alarma para SysMonitor
	private static double limFreeRAMMemoryPrct = 20;  		// Amount of free RAM memory in %
	private static double limSystemCpuLoad = 80; 			// "recent % cpu usage" for the whole system
    private static double limFreePartitionSpace = 10;		// Free hard disk capacity in %
	
	public SysMonitor() {
		super(PROCESSID, SERVICEID);
		// It is mandatory the super() function to be called
	}
	
	/**
	 * @param args the sysMonitor program does not requires any input parameter
	 */
	public static void main(String[] args) throws Exception
	{
		SysMonitor obj = new SysMonitor();
		obj.Run();
	}
	
	/**
	 * The sysMonitor's Run() does not requires any input parameter
	 */
	public void Run() throws Exception
	{
		echo("Starting "+this.getFullID()+"...");
		
		// It reads properties
		MainCycle = readConfigPropInt("MainCycle", 10000);
		limFreeRAMMemoryPrct = readConfigPropInt("limFreeRAMMemoryPrct", 20);
		limSystemCpuLoad = readConfigPropInt("limSystemCpuLoad", 80);
	    limFreePartitionSpace = readConfigPropInt("limFreePartitionSpace", 10);
		 
		echo("Starting "+this.getFullID()+"...");
		/*this.getLogger().error("Sysmon is able to publish the following performance monitors:");
		this.getLogger().error("  FreeJVMMemorySize");
		this.getLogger().error("  FreeRAMMemoryPrct");
		this.getLogger().error("  SystemCpuLoad, ");
		this.getLogger().error("  FreePartitionSpace0...N");
		this.getLogger().error("  NetworkUsage");*/
		
		// Creates the Performance Indices Entity to store measurements
		String serverId = getProcess().getSite()+":"+getProcess().getServer();
		serverPerformance = getServerPerformanceEntity(serverId);
		if(serverPerformance==null){
			this.getLogger().error("Error retrieving ServerPerformanceEntity. Stopping...");
			return;
		}
		
		// Main loop
		while(this.getProcess().getStopProcess()==false) // near-Infinite loop
		{
			// Refresh status in processMonitor
			checkin();
			
			// Read the performance monitors
			getPerformanceMonitors();
			
			// Check Alarm Limits
			checkAlarmLimits();
			
			// Print collected values
			printPerformanceMonitors();
			
			// Wait until next cycle
			echo("Waiting for next cycle...");
			sleep(MainCycle);
		}
		
		echo("Stoping "+this.getFullID());
		// checkout process
		checkout();
	}
	
	private void checkAlarmLimits() {
		// Check Alarm Limits
		if(limFreeRAMMemoryPrct>serverPerformance.getFreeRAMMemoryPrct()){
			generateAlarm(AlarmDic.ALM0071, "", limFreeRAMMemoryPrct, serverPerformance.getFreeRAMMemoryPrct());
		}
		if(limSystemCpuLoad<serverPerformance.getSystemCpuLoad()){
			generateAlarm(AlarmDic.ALM0072, "", limSystemCpuLoad, serverPerformance.getSystemCpuLoad());
		}
		for(int i = 0; i<serverPerformance.getPartitionSize(); i++){
			if(limFreePartitionSpace>serverPerformance.getFreePartitionSpace(i)){
				generateAlarm(AlarmDic.ALM0073, serverPerformance.getPartitionName(i),
						limFreePartitionSpace, serverPerformance.getFreePartitionSpace(i));
			}
		}
	}
	
	private void generateAlarm(AlarmDic alm, String aux, double lim, double val) {
		// Generates Alarm
		Date tstamp = new Date();
		RtdbDataService.get().createAlarm(new AlarmEntity(null, 
				tstamp, 
				String.format(alm.toString(), val, lim, aux),
				this.getProcess().getServer(),
				SERVICEID,
				PROCESSID,
				alm.getSeverity(),
				alm.getCategory(),
				true,
				true
				));
		/*// also injects the event
		RtdbDataService.get().pushEvent(new EventEntity(
				tstamp, 
				String.format(alm.toString(), val, lim, aux),
				this.getProcess().getServer(),
				SERVICEID,
				PROCESSID,
				alm.getSeverity(),
				alm.getCategory(),
			    Utils.SYSTEMUSER,
			    Utils.NOCONSOLE
				));*/
	}

	private void getPerformanceMonitors() throws Exception
	{
		// Read the performance monitors
		double kb = 1024;
		double mb = 1024*1024;
        double gb = 1024*1024*1024;
        //String CRLF = "\r\n";
        
        SystemInfo si = new SystemInfo();
	    
		serverPerformance.setFreeJVMMemorySize( (long)(Runtime.getRuntime().freeMemory() / kb) );
				//Returns the amount of free JVM memory in mbytes.
		
		serverPerformance.setTotalJVMMemorySize( (long)(Runtime.getRuntime().totalMemory() / kb) );
				//Returns the total amount of JVM memory in mbytes.
		 
		serverPerformance.setFreeRAMSpaceSize( (long)(si.getHardware().getMemory().getAvailable() / mb) );
				//Returns the amount of free RAM space in mbytes.
		
		serverPerformance.setTotalRAMSpaceSize( (long)(si.getHardware().getMemory().getTotal() / mb) );
				//Returns the total amount of RAM space in mbytes. 

		serverPerformance.setFreeRAMMemoryPrct( 100.0 * 
				si.getHardware().getMemory().getAvailable() / si.getHardware().getMemory().getTotal());
				// Amount of free RAM memory in %
		 
		serverPerformance.setSystemCpuTime( si.getHardware().getProcessor().getSystemUptime() );
				//Returns the CPU time used in nanoseconds.
		 
		//serverPerformance.setSystemCpuLoad( si.getHardware().getProcessor().getSystemCpuLoad()*100 );
		serverPerformance.setSystemCpuLoad( si.getHardware().getProcessor().getSystemCpuLoadBetweenTicks()*100 );
				//Returns the "recent % cpu usage" for the whole system.
		
		// Also the hard disks / partitions usage
		for(int i = 0; i<serverPerformance.getPartitionSize(); i++)
		{
			long totalCapacity=0;
			long usablePartitionSpace=0;
			File diskPartition = new File( serverPerformance.getPartitionName( i ));
			serverPerformance.setTotalPartitionSize( i, totalCapacity = (long)(diskPartition.getTotalSpace()/gb) );
			serverPerformance.setUsablePartitionSpace( i, usablePartitionSpace = (long)(diskPartition.getUsableSpace()/gb) );
			serverPerformance.setFreePartitionSpace( i, usablePartitionSpace*100.0/totalCapacity );
		}
    	
        // NIC (network) usage
		int nnics = si.getHardware().getNetworkIFs().length;
        for(int i=0; i<nnics; i++ ){
        	/*System.out.println("NIC = "+si.getHardware().getNetworkIFs()[i].getName());
        	System.out.println("MTU = "+si.getHardware().getNetworkIFs()[i].getMTU());
        	System.out.println("SPE = "+si.getHardware().getNetworkIFs()[i].getSpeed());
        	System.out.println(" UP = "+si.getHardware().getNetworkIFs()[i].getNetworkInterface().isUp());
        	System.out.println("ADR = "+si.getHardware().getNetworkIFs()[i].getNetworkInterface().getInetAddresses().nextElement());
        	System.out.println("/n");*/
        	if(si.getHardware().getNetworkIFs()[i].getNetworkInterface().isUp()){
        		si.getHardware().getNetworkIFs()[i].updateNetworkStats();
        		long data = si.getHardware().getNetworkIFs()[i].getBytesRecv()
        				+ si.getHardware().getNetworkIFs()[i].getBytesSent();
	        	long speed = si.getHardware().getNetworkIFs()[i].getSpeed();
	        	if(speed>0)
	        		serverPerformance.setNetworkUsage( data / speed );
	        	else
	        		serverPerformance.setNetworkUsage(-1);
		        if(mb>0)
		        	serverPerformance.setNetworkBandwith( (long)( speed / mb) );
		        else
		        	serverPerformance.setNetworkBandwith(-1);
		        break; // Just only the first NIC found and ignore the rest
        	}
        }
        
        // Finally, updates timestamp
        serverPerformance.setTimestamp(new Date());
        // Update Server Performance monitors in the In-Memory RTDB
     	RtdbDataService.get().updateEntity(serverPerformance, ServerPerformanceEntity.class);					
	}
	
	private void printPerformanceMonitors()
	{
		// Print the Performance Monitors to the console for Debug
		echo(serverPerformance.ToString());
	}
	
	private ServerPerformanceEntity getServerPerformanceEntity(String serverId){
		try {
	        this.echo("Invoke Register ServerPerformanceMonitor...");
	        // Register ServerPerformanceMonitor
	        //String serverId = getProcess().getSite()+":"+getProcess().getServer();
	        ServerPerformanceEntity rec = (ServerPerformanceEntity) 
	        		RtdbDataService.get().getEntityById(serverId, ServerPerformanceEntity.class);
	        if(rec==null){
	        	// Create new, as it does not exist
	        	rec = new ServerPerformanceEntity(serverId);
	        	RtdbDataService.get().createEntity(rec, ServerPerformanceEntity.class);
	        }
	        return rec;
	        
		} catch (Exception e) {
			this.getLogger().error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
} // End of sysMonitor Class
