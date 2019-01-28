package freewill.nextgen.jobScheduler;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Date;
import java.util.List;

import freewill.nextgen.common.MonitoredProcess;
import freewill.nextgen.common.Utils.ADMSservice;
import freewill.nextgen.common.Utils.AlarmDic;
import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.common.entities.AlarmEntity;
import freewill.nextgen.common.entities.JobScheduled;
import freewill.nextgen.common.entities.JobScheduled.JobStatusEnum;
import freewill.nextgen.common.rtdbclient.RtdbDataService;

/** 
 * File:   JobScheduler.java
 * Date:   15/10/2017
 * Author: Benito Vela
 * Refs:   None
 * 
 * This program is for executing periodical tasks: scripts, java programs, etc.
 * 
**/

public class JobScheduler extends MonitoredProcess {
	
	// Global variables to this class
	private static int MainCycle = 5000;							// Main loop cycle in milliseconds
	private static String PROCESSID = "JobScheduler";				// Process Name
	private static String SERVICEID = ADMSservice.RTS.toString();	// Service Name
	private static String MSECPERCYCLE = "MsecPerCycle";
	
	public JobScheduler() {
		super(PROCESSID, SERVICEID);
		// It is mandatory the super() function to be called
	}
	
	/**
	 * @param args the jobScheduler program does not requires any input parameter
	 */
	public static void main(String[] args)
	{
		JobScheduler obj = new JobScheduler();
		obj.Run();
	}
	
	public void Run()
	{		
		// It reads properties
		MainCycle = this.readConfigPropInt("MainCycle", 5000);
		String username = this.readConfigPropString("BLTUSERNAME", "foo");
		String password = this.readConfigPropString("BLTPASSWORD", "foo");
		 
		this.getLogger().info("Starting "+this.getFullID()+"...");
		echo("Starting "+this.getFullID()+"...");
		
		String tokenKey = BltClient.get().waitUntilToken(username, password, SERVICEID);
	    
		cleanExistingJobsInRtdb();
		
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
			
			// Executes periodical tasks
			executeTasks(tokenKey);
			
			// calculate and publish time consumed during the cycle
			long delta = (new Date().getTime())-ini; 
			this.setKpiValue(MSECPERCYCLE, delta);
			
			// Wait until next cycle
			echo("Waiting for next cycle...");
			if(MainCycle-(int)delta > 0)
				sleep(MainCycle-(int)delta);
		}
		
		echo("Stoping "+this.getFullID());
		// checkout process
		checkout();
	}

	private void cleanExistingJobsInRtdb() {
		// Clean existing Jobs in the Rtdb
		try{
			List<JobScheduled> list = RtdbDataService.get().getEntities(JobScheduled.class);
			for(JobScheduled rec:list){
				RtdbDataService.get().deleteEntity(rec, JobScheduled.class);
			}
		}
		catch(Exception e){
			this.getLogger().error("Error cleaning up existing Jobs in the Rtdb");
		}
	}

	private void executeTasks(String token) {
		// Executes periodical tasks
		try{
			List<JobScheduled> jobsList = BltClient.get().getEntities(JobScheduled.class, token);
			for(JobScheduled job:jobsList){
				// Also Gets the Job from the Rtdb, as here we have the last execution time
				JobScheduled job_ = (JobScheduled)
						RtdbDataService.get().getEntityById(job.getId(), JobScheduled.class);
				if(job_!=null)
					job.setLastExec(job_.getLastExec());
				else
					job.setLastExec(new Date());
				
				// Ignore disabled and failed jobs
				if(job.getActive() && job.getState()==JobStatusEnum.RUN){
					long nextexec = job.getLastExec().getTime() + getDelta(job.getCron())*1000;
					Date nowDate = new Date();
					long now = nowDate.getTime();
					if(now>=nextexec){
						// Executes this Jobs now
						if(executeCommand(job.getCommand(), job.getParams())==false){
							// if fails, set state to Fail and injects alarm
							job.setState(JobStatusEnum.FAILED);
							RtdbDataService.get().createAlarm(new AlarmEntity(null,
								nowDate, 
								String.format(AlarmDic.ALM0012.toString(), job.getLabel()),
								job.getLabel(),
								SERVICEID,
								PROCESSID,
								AlarmDic.ALM0012.getSeverity(),
								AlarmDic.ALM0012.getCategory(),
								true,
								true
								));
							BltClient.get().updateEntity(job, JobScheduled.class, token);
						}
						// Updates the job last successful execution time
						job.setLastExec(nowDate);
					}
					
				}
				// Updates the Job status in the Rtdb only
				if(job_==null)
					RtdbDataService.get().createEntity(job, JobScheduled.class);
				else
					RtdbDataService.get().updateEntity(job, JobScheduled.class);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			this.getLogger().error(e.getMessage());
		}
	}

	private long getDelta(String cron) {
		// Parses cron string to get delta in seconds
		long delta = 0;
		try{
			String cads[] = cron.split(" ");
			for(int i=0; i<cads.length; i++){ 
				int dd = 0;
				try{
					if(cads[i].contains("*"))
						dd = 0;
					else
						dd = Integer.parseInt(cads[i]);
				}
				catch(Exception e2){
					this.getLogger().error("Error during Cron string processing: "+cron+">"+cads[i]);
				}
				
				switch(i){
					case 0: // seconds
						delta += dd;
						break;
					case 1: // minutes
						delta += dd*60;
						break;
					case 2: // hours
						delta += dd*60*60;
						break;
					case 3: // days
						delta += dd*60*60*24;
						break;
					case 4: // months - not implemented
						break;
					case 5: // years - not implemented
						break;
				}
			}
		}
		catch(Exception e){
			this.getLogger().error("Error during Cron string processing: "+cron);
			this.getLogger().error(e.getMessage());
		}
		return delta;
	}
	
	@SuppressWarnings("rawtypes")
	private boolean executeCommand(String command, String params) {
		// Executes the command
		try{
			// Creates an instance of the command
			Object obj = (Object) this.LoadClass(command);
			// Gets the Run(String) method
	        Class[] cArg = new Class[1];
	        cArg[0] = String.class; 
			Method method = obj.getClass().getMethod("Run", cArg);
			// Executes the Run(String args) method
			method.invoke(obj, params);
			
			return true; // everything OK
		}
		catch(Exception e){
			this.getLogger().error(e.getMessage());
		}
		return false; // something failed
	}
	
	// This function will dynamically load a new class given the package and class name
    @SuppressWarnings("unchecked")
	private <C> C LoadClass(String classpath) throws ClassNotFoundException {
        File pluginsDir = new File(System.getProperty("user.dir"));
        for (File jar : pluginsDir.listFiles()) {
        	try {
	            @SuppressWarnings("deprecation")
				ClassLoader loader = URLClassLoader.newInstance(
	                new URL[] { jar.toURL() },
	                getClass().getClassLoader()
	            );
	            Class<?> clazz = Class.forName(classpath, true, loader);
	            return (C) clazz.newInstance();
	
	        } catch (ClassNotFoundException e) {
	            // There might be multiple JARs in the directory,
	            // so keep looking
	            continue;
	        } catch (MalformedURLException e) {
	            e.printStackTrace();
	        } catch (IllegalAccessException e) {
	            e.printStackTrace();
	        } catch (InstantiationException e) {
	            e.printStackTrace();
	        }
        }
        throw new ClassNotFoundException("Class " + classpath
            + " wasn't found in directory " + System.getProperty("user.dir"));
    }
    
}
