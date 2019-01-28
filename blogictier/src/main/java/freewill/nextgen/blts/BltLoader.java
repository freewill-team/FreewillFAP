package freewill.nextgen.blts;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import freewill.nextgen.common.MonitoredProcess;
import freewill.nextgen.common.Utils.ADMSservice;
import freewill.nextgen.common.entities.ServiceEntity;
import freewill.nextgen.common.rtdbclient.RtdbDataService;

@SpringBootApplication
@EnableResourceServer
@RestController
@EnableJpaAuditing
public class BltLoader {
	
	// Global variables to this class
	private static int MainCycle = 5000;							// Main loop cycle in milliseconds
	private static String PROCESSID = "BusinessLogicTier";			// Process Name
	private static String SERVICEID = ADMSservice.RTS.toString();	// Service Name
	private static ConfigurableApplicationContext context = null;	// to stop the Rest services
	@Value("${server.port:8445}")
	private int port;												// Blogictier process https port

	public static void main(String[] args) throws Exception {
		// Starts The Rest services
		SpringApplication app = new SpringApplication(BltLoader.class);
		context = app.run(args);
	}

	@RequestMapping("/")
	public String index(){
		return "BLTloader is ready";
	}
	
	public BltLoader(){
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
        	service.setPort(port);
        	RtdbDataService.get().updateEntity(service, ServiceEntity.class);
        	
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