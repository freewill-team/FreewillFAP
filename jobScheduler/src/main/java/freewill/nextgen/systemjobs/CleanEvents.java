package freewill.nextgen.systemjobs;

import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.common.entities.EventEntity;

public class CleanEvents {
	
	// dummy class to demonstrate/test the Job Scheduler functionality
	public CleanEvents(){
		// do nothing
	}

	// this method is mandatory so the Job Scheduler works properly
	public void Run(String token, String args){
		System.out.print("Executing CleanEvents with args="+args+" token="+token);
		int window = 30;
		try{
			window = Integer.parseInt(args);
		}
		catch(Exception e){
			window = 30;
		}
		
		try{
			BltClient.get().executeCommand("deleteByPeriod/"+window, EventEntity.class, token);
			System.out.print("Success Executing CleanEvents");
		}
		catch(Exception e){
			System.out.print("Fail Executing CleanEvents");
		}
	}
	
}
