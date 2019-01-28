package freewill.nextgen.common.entities;

import java.io.Serializable;
import java.util.Date;

import freewill.nextgen.common.Utils.ServiceStatusEnum;

/** 
 * File:   ServiceEntity.java
 * Date:   02/09/2017
 * Author: Benito Vela
 * Refs:   Used to aggregate processes in a major entity.
 * 		   The Business Logic (Blogictier) will be accessible trough the server 
 * 		   and port fields, using the BltClient class.
 * 
**/

@SuppressWarnings("serial")
public class ServiceEntity implements Serializable {
	
	// Internal Variables
	private String ID = "";					// Fullname of the Service = Site:Server:Service - Unique
	private String name = ""; 				// Name of the Service
	private Date timestamp;					// Last Timestamp when the Service was updated
	public ServiceStatusEnum status = 
			ServiceStatusEnum.FAILED; 		// State of the Service
	private String server = "unknown";		// name of the server where the Service is running
	private String site = "unknown";		// name of the Site where the Service is running
	private int port = 8445;				// port of the Blogictier process
	private boolean stopProcess = false;	// Indicates whether the service needs to be shouted down
	
	/*
	 * Default constructor
	 */
	public ServiceEntity(String name, String hostName, String site)
	{
		setName(name);
		setStatus(ServiceStatusEnum.STOP);
		setTimestamp(new Date());
		setServer(hostName);
		setSite(site);
		setID(site+":"+hostName+":"+name);
	}
	
	// Alternative constructor
	public ServiceEntity()
	{
		// EMPTY OBJECT - required for some features
	}
	
	public String toString(){
		return 	getID() +"\t"+
				getName() +"\t"+
				getServer() + "\t" +
				getSite() +"\t"+
				getStatus() +"\t" +
				getStopProcess() +"\t"+
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
	
	public void setStatus(ServiceStatusEnum stat) {
		status = stat;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public boolean getStopProcess() {
		return stopProcess;
	}

	public void setStopProcess(boolean stopProcess) {
		this.stopProcess = stopProcess;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
}
