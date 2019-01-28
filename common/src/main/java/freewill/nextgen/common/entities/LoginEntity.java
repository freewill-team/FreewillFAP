package freewill.nextgen.common.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@SuppressWarnings("serial")
public class LoginEntity implements Serializable, Cloneable {

	public LoginEntity() {
		super();
		setUuid(UUID.randomUUID().toString());
	}

	public String toString(){
		return  ID +" "+
				name +" "+
				console +" "+
				application +" "+
				uuid +" "+
				lastCheckin;
	}
	
	private String ID = "";				// FullId of the login = Console:UserName - Unique
	private String name = "";			// User name
	private String console = "";		// Console where the user logged in
	private Date lastCheckin = new Date(); // Last timestamp the using checks in
	private Long aorId = 0L;			// Area of Responsibility Id
	private String aorLabel = "";		// Area of Responsibility Label
	private String application = "";	// Application where the user logged in
	private String uuid = ""; 			// Unique UUID for instance identification
	
	@Override
	public LoginEntity clone() throws CloneNotSupportedException {
		return (LoginEntity) super.clone();
	}
	
	public String getID() {
		return ID;
	}

	public void setID(String id) {
		this.ID = id;
	}

	public Date getLastCheckin() {
		return lastCheckin;
	}

	public void setLastCheckin(Date dt) {
		this.lastCheckin = dt;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String label) {
		this.name = label;
	}
	
	@Override
	public int hashCode() {
		final int prime = 39;
		int result = 1;
		result = prime * result + (ID == null ? 0 : ID.hashCode());
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
		final LoginEntity other = (LoginEntity) obj;
		if (ID == null) {
			if (other.ID != null) {
				return false;
			}
		} else if (!ID.equals(other.ID)) {
			return false;
		}
		return true;
	}

	public String getConsole() {
		return console;
	}

	public void setConsole(String console) {
		this.console = console;
	}

	public Long getAorId() {
		return aorId;
	}

	public void setAorId(Long aorid) {
		this.aorId = aorid;
	}

	public String getAorLabel() {
		return aorLabel;
	}

	public void setAorLabel(String aorLabel) {
		this.aorLabel = aorLabel;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String app) {
		this.application = app;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
}