package freewill.nextgen.common.entities;

import java.io.Serializable;
import java.util.Date;

import freewill.nextgen.common.Utils.CategoryEnum;
import freewill.nextgen.common.Utils.SeverityEnum;

/**
 * Session Bean implementation class Company
 */

@SuppressWarnings("serial")
public class EventEntity implements Serializable, Cloneable {
	
	// common to AlarmEntity
	private Long id;
	private Date timestamp;			// Time stamp when the alarm was generated
	private String message;			// Message/description of the event
	private String point;			// Point which generates the alarm
	private String pointType;		// Point Type (analog, status, rate, multistate, etc.)
	private SeverityEnum severity;	// Alarm severity (hi, med, low, etc.)
	private String parentPoint;		// Parent point (if exists)
	private CategoryEnum category;	// Alarm category (comms, system, telemetry, etc.)
	// Additional fields
	private String username;		// User who generates the event
	private String console;			// Console or Server where the event was generated
	private Long company;			// Company the Event belongs to

    /**
     * Default constructor. 
     */
    public EventEntity(){
    	// Void with no-args as requested for non-enhanced JPA entities
    }
	
    public EventEntity(Date ts, String msg, String pnt, 
        	String pr, String pt, 
        	SeverityEnum svr, CategoryEnum cat, 
        	String un, String cn){
        timestamp = ts;
        message = msg;
        point = pnt;
        parentPoint = pr;
        pointType = pt;
        severity = svr;
        category = cat;
    	username = un;
    	console = cn;
    }
    
    public String toString()
    {
    	return  timestamp + " " + 
    			message + " " +
    			point + " " +
    			pointType + " " +
    			severity + " " + 
    			category + " " +
    			"[" +parentPoint + "] " +
    			username + "," +
    			console;
    }
    
    public void setTimestamp(Date ts)
    {
    	timestamp = ts;
    }
    
    public Date getTimestamp()
    {
    	return timestamp;
    }
    
    public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getConsole() {
		return console;
	}

	public void setConsole(String console) {
		this.console = console;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPoint() {
		return point;
	}

	public void setPoint(String point) {
		this.point = point;
	}

	public String getPointType() {
		return pointType;
	}

	public void setPointType(String pointType) {
		this.pointType = pointType;
	}

	public SeverityEnum getSeverity() {
		return severity;
	}

	public void setSeverity(SeverityEnum severity) {
		this.severity = severity;
	}

	public String getParentPoint() {
		return parentPoint;
	}

	public void setParentPoint(String parentPoint) {
		this.parentPoint = parentPoint;
	}

	public CategoryEnum getCategory() {
		return category;
	}

	public void setCategory(CategoryEnum category) {
		this.category = category;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getCompany() {
		return company;
	}

	public void setCompany(Long company) {
		this.company = company;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (this.id == null) {
			return false;
		}

		if (obj instanceof EventEntity && obj.getClass().equals(getClass())) {
			return this.id.equals(((EventEntity) obj).id);
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 43 * hash + (id == null ? 0 : id.hashCode());
		return hash;
	}

	@Override
	public EventEntity clone() throws CloneNotSupportedException {
		return (EventEntity) super.clone();
	}
	
}
