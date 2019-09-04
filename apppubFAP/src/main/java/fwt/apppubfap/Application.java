package fwt.apppubfap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import freewill.nextgen.common.MonitoredProcess;
import freewill.nextgen.common.entities.ServiceEntity;
import freewill.nextgen.common.rtdbclient.RtdbDataService;

@SpringBootApplication
@EnableScheduling
public class Application {
	
	// Global variables to this class
	private static int MainCycle = 5000;							// Main loop cycle in milliseconds
	private static String PROCESSID = "AppPublicFAP";				// Process Name
	private static String SERVICEID = "HMI";						// Service Name
	private static ConfigurableApplicationContext context = null;	// to stop the Rest services
	@Value("${server.port:8080}")
	private int port;											// Process https port
	
	@Value("${bltserver:localhost:8846}")
    private String BLTSERVER;							// Backend Server and port (alarmsrvmock)
	
	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Application.class);
		context = app.run(args);
	}
	
	/*@PostConstruct
	 * javax.annotation.postconstruct
    public void init(){
        // To be executed at application startup
		try {
			// Mandatory initialization
			MonitoredProcess process = new MonitoredProcess(PROCESSID, SERVICEID);
			BltClient.get().initializeService(process);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }*/
	
	public Application(){
		// Start the control thread
    	ControlThread controlThread = new ControlThread();
    	controlThread.start();
	}
	
	class ControlThread extends Thread {
		
		public ControlThread(){
			//
		}
		
        @Override
        public void run() {
            // register the process
        	MonitoredProcess process = new MonitoredProcess(PROCESSID, SERVICEID);
        	process.echo("Entering into init()");
        	// It reads properties
        	MainCycle = process.readConfigPropInt("MainCycle", 5000);
        	// Register the used service port
        	String serviceId = process.getProcess().getSite()+":"+
        			process.getProcess().getServer()+":"+process.getProcess().getService();
        	ServiceEntity service = (ServiceEntity) 
        			RtdbDataService.get().getEntityById(serviceId, ServiceEntity.class);
        	/*service.setPort(port);
        	RtdbDataService.get().updateEntity(service, ServiceEntity.class);*/
        	
        	// Main loop
    		while(process.getProcess().getStopProcess()==false) // near-Infinite loop
    		{
    			// Refresh status in processMonitor
    			process.checkin();
    		
    			// Actually it does nothing, as all the work is done by the BltLoader.class
    			//System.out.println("Next cycle...");
    			// Wait until next cycle
    			process.echo("Next cycle");
    			process.sleep(MainCycle);
            }
    		// Stops Rest services
    		context.close();
    		process.echo("Stoping "+service.getID());
    		// checkout process
    		process.checkout();
    		System.out.println("Everything stopped");
        }
    }
	
}