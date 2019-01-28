package freewill.nextgen.common.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import freewill.nextgen.common.Utils.ServiceStatusEnum;

/** 
 * File:   ProcessEntity.java
 * Date:   12/09/2016
 * Author: Benito Vela
 * Refs:   Used in ProcessMonitor
 * 
**/

@SuppressWarnings("serial")
public class ProcessEntity implements Serializable, Cloneable {
	
	// Internal Variables
	private String ID = "";					// FullId of the process = Site:Server:Service:Process - Unique
	private String name = ""; 				// Name of the Service
	private long timestamp = 0;				// Last Timestamp when the Process was updated
	private ServiceStatusEnum status = 
			ServiceStatusEnum.FAILED; 		// State of the Process
	private int timeout = 20000;			// Expiration timeout in milliseconds
	private String server = "unknown";		// name of the server where the process is running
	//private String osname = "unknown";		// OS version (Windows, Linux) where the process is running
	private String service = "unknown";		// name of the Service this process belongs to
	private String site = "unknown";		// name of the Site where the process is running
	private boolean stopProcess = false;	// Indicates whether the process needs to be shouted down
	private String fullpath = "";			// Full path to the java process - used for starting process from IMC
	private boolean startProcess = false;	// Indicates whether the process needs to be started up
	private List<KpiValue> kpiList = new ArrayList<KpiValue>();  // Custom Performance KPIs
	private boolean restartOnFailure=false; // Indicates whether the process will be restarted when failed
	
	/*
	 * Default constructor
	 */
	public ProcessEntity(String name, String service, String site, int timeout, 
			String hostName, /*String osName,*/ String fullPath)
	{
		setName(name);
		status = ServiceStatusEnum.GOOD;
		setTimestamp(System.currentTimeMillis());
		setTimeout(timeout);
		setService(service);
		setSite(site);
		setServer(hostName);
		//setOsname(osName);
		setFullpath(fullPath);
		setID(site+":"+server+":"+service+":"+name);
	}
	
	// Alternative constructor
	public ProcessEntity()
	{
		// EMPTY OBJECT - required for some features
	}
	
	public String toString(){
		Date date = new Date(getTimestamp());
		return 	getID() +"\t"+
				getService() +"\t"+
				getServer() + "\t" +
				getSite() + "\t" +
				//getOsname() + "\t" +
				getStatus() +"\t" +
				getTimeout() + "\t" +
				getStopProcess() +"\t"+
				getFullpath() +"\t"+
				date;
	}
	
	public void Refresh()
	{
		status = ServiceStatusEnum.GOOD;
		//stopProcess = false;
		setTimestamp(System.currentTimeMillis());
	}
	
	@Override
	public ProcessEntity clone() throws CloneNotSupportedException {
		return (ProcessEntity) super.clone();
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(long time) {
		this.timestamp = time;
	}

	public ServiceStatusEnum getStatus() {
		return status;
	}

	public void setStatus(ServiceStatusEnum status) {
		this.status = status;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	/*public String getOsname() {
		return osname;
	}

	public void setOsname(String osname) {
		this.osname = osname;
	}*/

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public boolean getStopProcess() {
		return stopProcess;
	}

	public void setStopProcess(boolean stopProcess) {
		this.stopProcess = stopProcess;
	}

	public String getFullpath() {
		return fullpath;
	}

	public void setFullpath(String fullpath) {
		this.fullpath = fullpath;
	}

	public boolean getStartProcess() {
		return startProcess;
	}

	public void setStartProcess(boolean startProcess) {
		this.startProcess = startProcess;
	}
	
	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
		setID(site+":"+server+":"+service+":"+name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<KpiValue> getKpiList() {
		return kpiList;
	}

	public void setKpiList(List<KpiValue> kpis) {
		this.kpiList = kpis;
	}

	public boolean getRestartOnFailure() {
		return restartOnFailure;
	}

	public void setRestartOnFailure(boolean retartOnFailure) {
		this.restartOnFailure = retartOnFailure;
	}
	
}
