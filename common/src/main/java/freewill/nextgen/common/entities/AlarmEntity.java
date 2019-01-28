package freewill.nextgen.common.entities;

import java.io.Serializable;
import java.util.Date;

import freewill.nextgen.common.Utils.CategoryEnum;
import freewill.nextgen.common.Utils.SeverityEnum;

/**
 * Session Bean implementation class Alarm
 */

@SuppressWarnings("serial")
public class AlarmEntity implements Serializable, Cloneable {
	
	private Long id;				// Alarm ID
	private Date timestamp;			// Time stamp when the alarm was generated
	private String message;			// Alarm message
	private String point;			// Point which generates the alarm
	private String pointType;		// Point Type (analog, status, rate, multistate, etc.)
	private SeverityEnum severity;	// Alarm severity (hi, med, low, etc.)
	private boolean flashing; 		// Whether this alarm is flashing
	private boolean remove;			// Whether this alarm needs to be removed/purged when acknowledged
	private String parentPoint;		// Parent point (if exists)
	private CategoryEnum category;	// Alarm category (comms, system, telemetry, etc.)
	
    /**
     * Default constructor. 
     */
    public AlarmEntity(){
    	// Void with no-args as requested for non-enhanced JPA entities
    }
	
    public AlarmEntity(Long i, Date ts, String msg, String point, 
    	String parent, String ptype, SeverityEnum svr, CategoryEnum cat, 
    	boolean remove, boolean flash){
    	id = i;
    	timestamp = ts;
    	message = msg;
    	this.point = point;
    	parentPoint = parent;
    	pointType = ptype;
    	severity = svr;
    	category = cat;
    	this.remove = remove;
    	flashing = flash;
    }
    
    public String toString()
    {
    	return  id + " "+
    			flashing + " " + 
    			timestamp + " " + 
    			message + " " +
    			point + " " +
    			pointType + " " +
    			severity + " " + 
    			category + " " +
    			"[" +parentPoint + "] " +
    			remove;
    }
    
    @Override
	public AlarmEntity clone() throws CloneNotSupportedException {
		return (AlarmEntity) super.clone();
	}
    
    public void setID(long id)
    {
    	this.id=id;
    }
    
    public long getId()
    {
    	return id;
    }
    
    public void setTimestamp(Date ts)
    {
    	timestamp = ts;
    }
    
    public Date getTimestamp()
    {
    	return timestamp;
    }
    
    public void setMessage(String msg)
    {
    	message = msg;
    }
    
    public String getMessage()
    {
    	return message;
    }
    
    public void setPoint(String pnt)
    {
    	point = pnt;
    }
    
    public String getPoint()
    {
    	return point;
    }
    
    public void setSeverity(SeverityEnum svr)
    {
    	severity = svr;
    }
    
    public SeverityEnum getSeverity()
    {
    	return severity;
    }
    
    public void setCategory(CategoryEnum cat)
    {
    	category = cat;
    }
    
    public CategoryEnum getCategory()
    {
    	return category;
    }
    
    public void setRemove(boolean val)
    {
    	remove = val;
    }
    
    public boolean getRemove()
    {
    	return remove;
    }

	public String getPointType() {
		return pointType;
	}

	public void setPointType(String pointType) {
		this.pointType = pointType;
	}
    
	public String getParentPoint(){
		return parentPoint;
	}
	
	public void setParentPoint(String parent){
		parentPoint = parent;
	}

	public boolean getFlashing() {
		return flashing;
	}

	public void setFlashing(boolean flashing) {
		this.flashing = flashing;
	}
	
}
