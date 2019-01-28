package freewill.nextgen.proposalStudio;

import java.io.File;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.log4j.Logger;

import freewill.nextgen.common.MonitoredProcess;
import freewill.nextgen.common.Utils.ADMSservice;

public class ProposalStudio {
	
	// Global variables to this class
	private static int MainCycle = 5000;							// Main loop cycle in milliseconds
	private static String PROCESSID = "ProposalStudio";				// Process Name
	private static String SERVICEID = ADMSservice.HMI.toString();	// Service Name
	private static MonitoredProcess service = null;					// For process monitoring and logging
	
	public Logger getLogger(){
		return service.getLogger();
	}

    public static void main(String[] args) throws LifecycleException {
    	
    	// register the process
    	service = new MonitoredProcess(PROCESSID, SERVICEID);
    	service.echo("Entering into init()");
    	// It reads properties
    	MainCycle = service.readConfigPropInt("MainCycle", 5000);
    	int tomcatPort = service.readConfigPropInt("TomcatPort", 8892);
    	
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(tomcatPort);
        
		try {
			String webappDirLocation = new File(".").getAbsolutePath();
			
			Context ctx = tomcat.addContext("/", webappDirLocation);
	        System.out.println("configuring app with basedir: " + webappDirLocation);
			
	        Tomcat.addServlet(ctx, "EntryPointServlet", new EntryPoint.EntryPointServlet());      
	        ctx.addServletMapping("/*", "EntryPointServlet");
	        
	        // Initializes performance Monitoring
	    	//MonitoredProcessKPIs kpis = new MonitoredProcessKPIs(service.getProcess(), null);
	    	// Add additional KPIs here kpis.addKpi(MSECPERSAMPLE);
	        
	        tomcat.start();
	        //tomcat.getServer().await();
	        
	        // Main loop
    		while(service.getProcess().getStopProcess()==false) // near-Infinite loop
    		{
    			// Refresh status in processMonitor
    			// TODO during debug 
    			service.checkin();
    			
    			// Actually it does nothing, as all the work is done by Tomcat
    			// Wait until next cycle
            	service.echo("Next cycle");
    	        service.sleep(MainCycle);
            }
    		// Stops service
    		tomcat.stop();
    		service.echo("Stoping "+service.getFullID());
    		// checkout process
    		service.checkout();
    		System.out.println("Everything stopped");
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
}
