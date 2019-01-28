package freewill.nextgen.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.Date;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import freewill.nextgen.common.Utils.AlarmDic;
import freewill.nextgen.common.Utils.CategoryEnum;
import freewill.nextgen.common.Utils.ServiceStatusEnum;
import freewill.nextgen.common.Utils.SeverityEnum;
import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.common.entities.EventEntity;
import freewill.nextgen.common.entities.KpiValue;
import freewill.nextgen.common.entities.ProcessEntity;
import freewill.nextgen.common.entities.ServerEntity;
import freewill.nextgen.common.entities.ServiceEntity;
import freewill.nextgen.common.rtdbclient.RtdbDataService;

/** 
 * File:   Service.java
 * Date:   15/08/2017
 * Author: Benito Vela
 * Refs:   None
 * 
 * This is a base class to implement background monitored services/processes by extending this class.
 * 
 * Monitoring is performed as follows: the new service/process is registered in the In-memory (RTDB) 
 * processMonitor's cached list (which implements the list of processes to be supervised) when the 
 * checkin() function is called by first time; later on, the developer is responsible to create a 
 * while(1) loop where a call to the checkin() function will periodically inform to the processMonitor 
 * service that the process is still alive. Finally, when the service is stopped or 
 * killed, a call to checkout() function will unregister the process, so no longer will be monitored.
 * 
 * When the checkin() function is not called on time (the period is set in checkin()), processMonitor 
 * will report that this process is dead or inactive.
 * 
 * It also set the login default Locale.
 * 
 */

public class MonitoredProcess {
	
	// Global variables to this class
	private Logger log = null;						// Class to Log application messages
	private Properties props = null;				// Class to manage configuration properties
	private ProcessEntity process = null;			// The process itself
	private String locale;							// Environment Variable - Locale "es", "en", ...
	
	// FUTURE manage multiple instances of a process in the same machine
	
	/**
	 * The default constructor
	 */
	public MonitoredProcess(String processname, String servicename)
	{
		// Create a new Process Entity
		process = new ProcessEntity(
				processname, 
				servicename, 
				Utils.NOSYSTEM,
				20000, 
				Utils.findHostName(),
				//Utils.findOSName(),
				currentJarFullPath());
		createLogger();
		createConfigReader();
		process.setTimeout(readConfigPropInt("timeout", 20000));
		setLocale(readConfigPropString("locale", "es")); // default locale is Spanish
		// If the parent application is an HMI, it can override this locale using setLocale()
		process.setSite(readConfigPropString("ADMSSITE", Utils.NOSYSTEM));
		
		// Initializes connection to the In-memory RTDB
		RtdbDataService.get().initializeService(this);
		
		// Initializes connection to BLTs
		BltClient.get().initializeService(this);
		
		// Create default performance KPIs
		addKpi(process, "ProcessRAMMemory");
		addKpi(process, "ProcessCpuLoad");
		// Additional custom KPis can be also added
		
		// Register the process, server and service in the Architecture Tree
		process = register(process);
		
		// It also injects a new event reporting the new Process is started
		RtdbDataService.get().pushEvent(new EventEntity(
			     new Date(), 
			     String.format(AlarmDic.ALM0003.toString(), process.getID()),
			     process.getID(),
			     process.getServer(),
			     Utils.PROCESSES,
			     SeverityEnum.NONE,
			     CategoryEnum.SYSTEM,
			     Utils.SYSTEMUSER,
			     Utils.NOCONSOLE
			     ));
		
		// Captures when the process is manually stopped/killed
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                checkout();
            }
        });
        
        // Makes the first check-in
        checkin();
	}

	private String currentJarFullPath() {
		String rawpath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
		System.out.println("RAWPATH="+rawpath);
		String fullpath = rawpath.replace("file:", "");
		if(fullpath.startsWith("/C:") || fullpath.startsWith("/D:") || fullpath.startsWith("/E:"))
			fullpath = fullpath.substring(1, fullpath.length());
		if(fullpath.contains("!/")){
			int i = fullpath.indexOf("!/");
			if(i>0)
				fullpath = fullpath.substring(0,i);
		}
		System.out.println("FULLPATH="+fullpath);
		return fullpath;
	}

	public String toString()
	{
		// Print to the console for Debug
		String cad = process.toString();
		return cad;
	}
	
	private void createLogger() {
		try {
			// This will set log4j.appender.file.File = this.getProcess().getID().log
			System.setProperty("admsnextgenlogfile.name",
					 System.getProperty("user.dir")+File.separator+"logs"+
					 File.separator+this.getProcess().getName()+".log");
			System.out.println("Log File = "+System.getProperty("admsnextgenlogfile.name"));
					 
			// Configure logger
			log = Logger.getLogger(MonitoredProcess.class);
			// Este codigo lee el fichero de un directorio fuera del jar
			//String logDirName = System.getProperty("user.dir");
			//String logFileName = logDirName + File.separator + "properties" + File.separator + "log4j.properties";
			//PropertyConfigurator.configure(logFileName);
			
			// Este codigo lee el fichero de un directorio dentro del jar
			String logFileName = "/properties/log4j.properties";
			//System.out.println("Config Log File = "+logFileName);
			PropertyConfigurator.configure(this.getClass().getResourceAsStream(logFileName));
			
			log.info("Created Logger for process '"+ this.getProcess().getID() +"'...");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	/*
	 * Setters and Getters for configurable properties
	 */
	
	public String getFullID() {
		return process.getID();
	}
	
	public Logger getLogger(){
		return log;
	}
	
	public ProcessEntity getProcess() {
		return process;
	}
	
	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
		System.setProperty("LOCALE", locale);
		log.info("Setting Locate to "+this.locale);
	}
	
	/*
	 * Register() - It registers the service and server in the In-memory database, so the
	 * 				processMonitor can update their statuses according to their 
	 * 				processes health.
	 */
	 public ProcessEntity register(ProcessEntity process){
		 registerServer(process);
		 registerService(process);
		 //registerServerPerformanceMonitor();
		 return registerProcess(process);
	 }
	 
	 private void registerServer(ProcessEntity process){
		try {
	        log.debug("Invoke Register Server...");
	        // Register Server
	        String serverId = process.getSite()+":"+process.getServer();
	        ServerEntity rec = (ServerEntity)
	        		RtdbDataService.get().getEntityById(serverId, ServerEntity.class);
	        if(rec==null){
	        	// Create new, as it does not exist
	        	rec = new ServerEntity(
	        			process.getServer(), 
	        			process.getSite(),
	        			Utils.findOSName()); 
	        	RtdbDataService.get().createEntity(rec, ServerEntity.class);
	        }
	        else{
	        	// just update it
	        	rec.Refresh();
			    RtdbDataService.get().updateEntity(rec, ServerEntity.class);
	        }
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
	 
	private void registerService(ProcessEntity process){
		try {
	        log.debug("Invoke Register Service...");
	        // Register Service
	        String serviceId = process.getSite()+":"+process.getServer()+":"+process.getService();
	        ServiceEntity rec = (ServiceEntity) 
	        		RtdbDataService.get().getEntityById(serviceId, ServiceEntity.class);
	        if(rec==null){
	        	// Create new, as it does not exist
	        	rec = new ServiceEntity(getProcess().getService(), process.getServer(), process.getSite());
	        	RtdbDataService.get().createEntity(rec, ServiceEntity.class);
	        }
	        else{
	        	// just update it
	        	rec.Refresh();
			    RtdbDataService.get().updateEntity(rec, ServiceEntity.class);
	        }	        
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
	
	// Deprecated. Now we use KPIList
	/*private void registerServerPerformanceMonitor(){
		try {
	        log.debug("Invoke Register ServerPerformanceMonitor...");
	        // Register ServerPerformanceMonitor
	        String serverId = getProcess().getSite()+":"+getProcess().getServer();
	        ServerPerformanceEntity rec = (ServerPerformanceEntity) 
	        		RtdbDataService.get().getEntityById(serverId, ServerPerformanceEntity.class);
	        if(rec==null){
	        	// Create new, as it does not exist
	        	rec = new ServerPerformanceEntity(serverId);
	        	RtdbDataService.get().createEntity(rec, ServerPerformanceEntity.class);
	        }
	        
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}*/
	 
	private ProcessEntity registerProcess(ProcessEntity process){
		try {
			log.debug("Invoke Register Process...");
	        // Register Process
		    ProcessEntity rec = RtdbDataService.get().getProcessById(process.getID());
		    if(rec==null){
		       	// Create new, as it does not exist
		        RtdbDataService.get().createProcess(process);
		        return process;
		    }
		    else{
		    	// just update it
		    	rec.Refresh();
			    RtdbDataService.get().updateProcess(rec);
			    return rec;
	        }	
		    
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return null;
	}
	
	/*
	 * Checkin() - It registers process in processMonitor service
	 * 			   The developer is responsible to periodically call to this method
	 * 			   in a while(true) loop, so the local processMonitor is informed that
	 * 			   this process is still alive. 
	 */
	public void checkin(){
		try {
	        log.debug("Invoke CheckinProcess...");
	        ProcessEntity rec = RtdbDataService.get().getProcessById(process.getID());
	        if(rec!=null){
	        	// just update it
			    rec.Refresh();
			    setKpiValue(rec, "ProcessRAMMemory", getRamUsage());
			    setKpiValue(rec, "ProcessCpuLoad", getCpuUsage());
			    RtdbDataService.get().updateProcess(rec);
			    process = rec;
	        }
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	/*
	 * checkout() - It unregisters process in processMonitor service
	 * 				Usually there is no need to call to this method, as it is already managed
	 * 				in the Runtime.getRuntime().addShutdownHook() event (see below).
	 * 				Anyway, the method is still public just in case.
	 */
	public void checkout(){
		try {
	        log.debug("Invoke CheckoutProcess...");
		    // RtdbDataService.get().deleteProcess(process);
	        ProcessEntity rec = RtdbDataService.get().getProcessById(process.getID());
	        if(rec!=null){
	        	// just update it
			    rec.setStatus(ServiceStatusEnum.STOP);
			    rec.setStartProcess(false);
			    rec.setStopProcess(false);
			    rec.setTimestamp(System.currentTimeMillis());
			    RtdbDataService.get().updateProcess(rec);
			    process = rec;
			    // It also injects a new event reporting the new Process is stopped
    		    RtdbDataService.get().pushEvent(new EventEntity(
    		    		new Date(), 
    		     		String.format(AlarmDic.ALM0004.toString(), process.getID()),
    		     		process.getID(),
    		     		process.getServer(),
    		     		Utils.PROCESSES,
    		     		SeverityEnum.NONE,
    		     		CategoryEnum.SYSTEM,
    		     		Utils.SYSTEMUSER,
    				    Utils.NOCONSOLE
    		     		));
	        }
		} catch (Exception e) {
			log.error(e.getMessage());
		}        
	}
	
	public void setRestartOnFailure(boolean value){
		try {
	        log.debug("Invoke setRestartOnFailure...");
	        ProcessEntity rec = RtdbDataService.get().getProcessById(process.getID());
	        if(rec!=null){
	        	rec.setRestartOnFailure(value);
	        	RtdbDataService.get().updateProcess(rec);
	        	process = rec;
	    		return;
	        }
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
	
	/*
	 * echo() - It provides access to the Logger output - just only debug messages
	 * 			For info/error/verbose use getLogger()
	 */
	public void echo(String message){
		if(log!=null)
			log.debug(message);
		else
			System.out.println(message);
	}
	
	/*
	 * sleep() - It waits for X milliseconds 
	 * 			 This function will be usually used within the while(true) loop
	 */
	public void sleep(int millisecs) 
	{
		try 
		{
			if(millisecs>0)
				Thread.sleep(millisecs);
		} 
		catch (InterruptedException e) 
		{
		    log.debug(e.getMessage());
		}
	}
	
	private void createConfigReader()
	{
		props = new Properties();
		InputStream input = null;
		try 
		{
			// The config file is outside the Jar file 
			String logDirName = System.getProperty("user.dir");
			String propFileName = logDirName + File.separator + "properties" + File.separator + "config.properties";
			input = new FileInputStream(propFileName);
			
			// load the properties file contains
			props.load(input);
			echo("Monitored Process Config Properties in "+propFileName);
			System.out.println("Monitored Process Config Properties in "+propFileName);
		} 
		catch (Exception ex) 
		{
			log.error("createConfigReader error:" + ex.getMessage());
		} 
	}
	
	/*
	 * Read config property and returns string
	 */
	public String readConfigPropString(String key, String defaultvalue){
		// get the property value and return a String
		String cad = "";
		try{
			cad = props.getProperty(key);
			if(cad==null) cad = defaultvalue; 
		}
		catch (Exception e)
		{
			cad = defaultvalue;
		}
		log.info("Read Config Prop "+key+" = " + cad);
		return cad;
	}
	
	/*
	 * Read config property and returns integer
	 */
	public int readConfigPropInt(String key, int defaultvalue){
		// get the property value and return a Integer
		int val = 0;
		try{
			val = Integer.parseInt(props.getProperty(key));
		}
		catch (Exception e)
		{
			val = defaultvalue;
		}
		log.info("Read Config Prop "+key+" = " + val);
		return val;
	}
	
	/*
	 * Functions for process KPIs management
	 */
	private double getRamUsage() {
		double kb = 1024;
		// Returns the amount of total JVM memory in mbytes
		return Runtime.getRuntime().totalMemory() / kb;
	}

	@SuppressWarnings("restriction")
	private double getCpuUsage() {
		// Returns the amount of cpu used by the JVM memory in %
		double load = 0;
		try{
			OperatingSystemMXBean mbean = (com.sun.management.OperatingSystemMXBean)
					ManagementFactory.getOperatingSystemMXBean();
			load = ((com.sun.management.OperatingSystemMXBean) mbean).getSystemCpuLoad();
		}
		catch (Exception e) {
			System.out.println("Error Getting JVM CPU: " +e.getMessage());
		}
		return load;
	}
	
	public void addKpi(String kpiname){
		try {
	        log.debug("Invoke AddKpi...");
	        ProcessEntity rec = RtdbDataService.get().getProcessById(process.getID());
	        if(rec!=null){
	        	for(KpiValue kpi:rec.getKpiList()){
	    			if(kpiname.equals(kpi.getValue()))
	    				return; // Nothing to do, already exists
	    		}
	        	KpiValue kpi = new KpiValue(kpiname);
	    		rec.getKpiList().add(kpi);
	    		//System.out.println("/n/n/nADDED new KPI = "+kpiname);
	        	RtdbDataService.get().updateProcess(rec);
	        	process = rec;
	        }
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
	
	private void addKpi(ProcessEntity rec, String kpiname){
		try {
	        log.debug("Invoke AddKpi2...");
	        if(rec!=null){
	        	for(KpiValue kpi:rec.getKpiList()){
	    			if(kpiname.equals(kpi.getValue()))
	    				return; // Nothing to do, already exists
	    		}
	        	KpiValue kpi = new KpiValue(kpiname);
	    		rec.getKpiList().add(kpi);
	    		//System.out.println("/n/n/nADDED new KPI = "+kpiname);
	        	//RtdbDataService.get().updateProcess(rec);
	        	//process = rec;
	        }
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
	
	public void removeKpi(String kpiname){
		try {
	        log.debug("Invoke removeKpi...");
	        ProcessEntity rec = RtdbDataService.get().getProcessById(process.getID());
	        if(rec!=null){
	        	for(KpiValue kpi:rec.getKpiList()){
	    			if(kpiname.equals(kpi.getValue())){
	    				rec.getKpiList().remove(kpi);
	    			}
	    		}
	        	RtdbDataService.get().updateProcess(rec);
	        	process = rec;
	        }
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
	
	public void setKpiValue(String kpiname, double value){
		try {
	        log.debug("Invoke setKpiValue...");
	        ProcessEntity rec = RtdbDataService.get().getProcessById(process.getID());
	        if(rec!=null){
	        	for(KpiValue kpi:rec.getKpiList()){
	    			if(kpiname.equals(kpi.getName())){
	    				//int i = process.getKpiList().indexOf(kpi);
	    				kpi.setValue(value);
	    				kpi.setDate(new Date());
	    				//System.out.println(">>> >>>Setting PKI value: "+kpiname+" "+value);
	    				//process.getKpiList().set(i, kpi);
	    				RtdbDataService.get().updateProcess(rec);
	    				process = rec;
	    				return;
	    			}
	    		}
	        }
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
	
	private void setKpiValue(ProcessEntity rec, String kpiname, double value){
		try {
	        log.debug("Invoke setKpiValue2...");
	        if(rec!=null){
	        	for(KpiValue kpi:rec.getKpiList()){
	    			if(kpiname.equals(kpi.getName())){
	    				kpi.setValue(value);
	    				kpi.setDate(new Date());
	    				//System.out.println(">>> >>>Setting PKI value: "+kpiname+" "+value);
	    				RtdbDataService.get().updateProcess(rec);
	    				process = rec;
	    				return;
	    			}
	    		}
	        }
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}
	
	public double getKpiValue(String kpiname){
		try {
	        log.debug("Invoke getKpiValue...");
	        ProcessEntity rec = RtdbDataService.get().getProcessById(process.getID());
	        if(rec!=null){
	        	for(KpiValue kpi:rec.getKpiList()){
	    			if(kpiname.equals(kpi.getName()))
	    				return kpi.getValue();
	    		}
	        }
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return 0;
	}
	
}
