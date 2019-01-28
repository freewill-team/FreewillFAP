package freewill.nextgen.common.entities;

import java.io.Serializable;
import java.util.Date;

import freewill.nextgen.common.Utils.ServiceStatusEnum;

/** 
 * File:   ServerEntity.java
 * Date:   02/09/2017
 * Author: Benito Vela
 * Refs:   Used in ProcessMonitor
 * 
**/

@SuppressWarnings("serial")
public class ServerEntity implements Serializable {
	
	// Internal Variables
	private String ID = "";					// Fullname of the Server = Site:Server - Unique
	private String name = ""; 				// Name of the Server
	private Date timestamp;					// Last Timestamp when the Server was updated
	public ServiceStatusEnum status = 
			ServiceStatusEnum.FAILED; 		// State of the Server
	private String site = "unknown";		// name of the Site where the Server is running
	private boolean stopProcess = false;	// Indicates whether the Server needs to be shouted down
	private String osname = "unknown";		// OS version (Windows, Linux) where the process is running
	
	/*
	 * Default constructor
	 */
	public ServerEntity(String name, String site, String osName)
	{
		setName(name);
		setStatus(ServiceStatusEnum.STOP);
		setTimestamp(new Date());
		setSite(site);
		setID(site+":"+name);
		setOsname(osName);
	}
	
	// Alternative constructor
	public ServerEntity()
	{
		// EMPTY OBJECT - required for some features
	}
	
	public String toString(){
		return 	getID() +"\t"+
				getName() +"\t"+
				getSite() +"\t"+
				getStatus() +"\t" +
				getStopProcess() +"\t"+
				getOsname() + "\t" +
				getTimestamp();
	}
	
	public void Refresh()
	{
		setStatus(ServiceStatusEnum.GOOD);
		setTimestamp(new Date());
	}
	
	/*
	 * IMPORTANT - in order to SQLDataService() CRUD actions to work properly, all setters and 
	 * getters mandatory must manage basic types (long, integer, String, double and float) 
	 */

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}
	
	public Date getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(Date time) {
		this.timestamp = time;
	}

	public ServiceStatusEnum getStatus() {
		return status;
	}
	
	public void setStatus(ServiceStatusEnum status) {
		this.status = status;
	}

	public boolean getStopProcess() {
		return stopProcess;
	}

	public void setStopProcess(boolean stopProcess) {
		this.stopProcess = stopProcess;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getOsname() {
		return osname;
	}

	public void setOsname(String osname) {
		this.osname = osname;
	}
	
}
