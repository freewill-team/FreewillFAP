package freewill.nextgen.blts.entities;

import java.io.Serializable;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import freewill.nextgen.blts.BltAuditingEntityListener;

@SuppressWarnings("serial")
@Entity
@EntityListeners(BltAuditingEntityListener.class)
public class JobScheduled implements Serializable {

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
				command +" "+
				params +" "+
				active;
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "LABEL")
	private String label;

	//@Temporal(TemporalType.TIMESTAMP)
	//@Column(name = "LASTEXEC", columnDefinition = "TIMESTAMP", nullable=true)
	@Transient // This field will be updated only in the RTDB
	private Date lastExec = new Date();

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "CRON")
	private String cron;

	@Column(name = "ACTIVE")
	private boolean active;
	
	@Column(name = "STATE")
	private JobStatusEnum state;
	
	@Column(name = "COMMAND", nullable=true)
	private String command;
	
	@Column(name = "PARAMS", nullable=true)
	private String params;
	
	public enum JobStatusEnum{ 
		RUN("Running"),
		FAILED("Failed"),
		STOP("Stopped");
		private final String type;
		JobStatusEnum(String t){ type = t; }
		public String toString(){ return type; } // I18n
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

	public void setState(JobStatusEnum run) {
		this.state = run;
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