package freewill.nextgen.common.entities;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class JobScheduled implements Serializable, Cloneable {

	public JobScheduled() {
		super();
	}

	public String toString(){
		return  id +" "+
				label +" "+
				lastExec +" "+
				description +" "+
				cron +" "+
				state +" "+
				active;
	}
	
	private Long id;
	private String label = "";
	private Date lastExec = new Date(); // This field is updated only in the RTDB
	private String description = "";
	private String cron = "10 * * * *"; // by default, executes every 10 seconds - cron string
	private boolean active = false;
	private JobStatusEnum state = JobStatusEnum.STOP;
	private String command="";
	private String params="";
	
	public enum JobStatusEnum{ 
		RUN("Running"),
		FAILED("Failed"),
		STOP("Stopped");
		private final String type;
		JobStatusEnum(String t){ type = t; }
		public String toString(){ return type; } // I18n
	}
	
	@Override
	public JobScheduled clone() throws CloneNotSupportedException {
		return (JobScheduled) super.clone();
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getLastExec() {
		return lastExec;
	}

	public void setLastExec(Date dt) {
		this.lastExec = dt;
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}	
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean getActive() {
		return active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}
	
	public JobStatusEnum getState() {
		return state;
	}

	public void setState(JobStatusEnum state) {
		this.state = state;
	}
	
	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}
	
	@Override
	public int hashCode() {
		final int prime = 32;
		int result = 1;
		result = prime * result + (id == null ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!getClass().isInstance(obj)) {
			return false;
		}
		final JobScheduled other = (JobScheduled) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

}