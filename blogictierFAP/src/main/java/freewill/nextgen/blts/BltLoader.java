package freewill.nextgen.blts;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import freewill.nextgen.blts.daos.EventLogRepository;
import freewill.nextgen.common.MonitoredProcess;
import freewill.nextgen.common.entities.ServiceEntity;
import freewill.nextgen.common.rtdbclient.RtdbDataService;

@SpringBootApplication
@EnableResourceServer
@RestController
@EnableJpaAuditing
@EnableScheduling
public class BltLoader {
	
	// Global variables to this class
	private static int MainCycle = 5000;							// Main loop cycle in milliseconds
	private static String PROCESSID = "BusinessLogicTierFAP";		// Process Name
	private static String SERVICEID = "RealtimeFAP";				// Service Name
	private static ConfigurableApplicationContext context = null;	// to stop the Rest services
	@Value("${server.port:8447}")
	private int port;												// Blogictier process https port
	@Value("${historicalwindow:15}")
	private int historicalwindow;									// Events kept in historical, in days
	
	@Autowired
	EventLogRepository repoevent;
	
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
        	
        	/*System.out.println("Busco Servicio: "+serviceId);
        	System.out.println("en...");
        	List<ServiceEntity> list = RtdbDataService.get().getEntities(ServiceEntity.class);
    		for(ServiceEntity rec:list){
    			System.out.println("  "+rec.toString());
    		}*/
        	
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
	
	@Scheduled(fixedRate = 43200000, initialDelay = 20000) // ejecucion cada 12 horas
	public void scheduleTaskWithInitialDelay() {
	    //logger.info("Fixed Rate Task with Initial Delay :: Execution Time - {}", dateTimeFormatter.format(LocalDateTime.now()));
		Date limdate = new Date();
		limdate.setTime(limdate.getTime()-historicalwindow*86100);
		repoevent.deleteByTimestampBefore(limdate);
	}
	
}