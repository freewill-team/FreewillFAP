package freewill.nextgen.hello;

public class Hello {
	
	// dummy class to demonstrate/test the Job Scheduler functionality
	public Hello(){
		// do nothing
	}

	// this method is mandatory so the Job Scheduler works properly
	public void Run(String args){
		System.out.print("Executing Hello with args="+args);
	}
	
}
