package freewill.nextgen.data;

import java.io.Serializable;
import java.util.Date;

import freewill.nextgen.hmi.utils.Messages;

//import freewill.nextgen.common.Messages;

/**
 * Session Bean implementation class support
 */
@SuppressWarnings("serial")
public class SupportEntity implements Serializable, Cloneable {
	
	private Long id;				// ID
	private Date created;			// Date the support case was created
	private Date timestamp;			// Last Time stamp when the record was updated
	private String description;		// support case long description
	private boolean resolved;		// support case status
	private Long user;				// User the support belongs to
	
	private String comments;		// support case comments
	private SeverityEnum severity;  // Support case Severity
	public enum SeverityEnum{
		LO("Low"),
		ME("Medium"),
		HI("High");
		private final String type;
		SeverityEnum(String t){ type = t; }
		public String toString(){
			//return Messages.get().getKey(type, System.getProperty("LOCALE"));
			return Messages.get().getKey(type);
		}
	}
	
    /**
     * Default constructor. 
     */
    public SupportEntity(){
    	// Void with no-args as requested for non-enhanced JPA entities
    	//id = 0;
    	timestamp = new Date();
    	resolved = false;
    	created = new Date();
    	description = "";
    	comments = "";
    	severity = SeverityEnum.LO;
    	user = null;
    }
    
    public String toString()
    {
    	return  id + ","+
    			created + "," +
    			resolved + "," +
    			severity + "," +
    			user + "," +
    			description + "," +
    			timestamp;
    }
    
    @Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (this.id == null) {
			return false;
		}

		if (obj instanceof SupportEntity && obj.getClass().equals(getClass())) {
			return this.id.equals(((SupportEntity) obj).id);
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
	public SupportEntity clone() throws CloneNotSupportedException {
		return (SupportEntity) super.clone();
	}
    
    public void setID(long id)
    {
    	this.id=id;
    }
    
    public Long getID()
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
    
    public void setResolved(boolean val)
    {
    	resolved = val;
    }
    
    public boolean getResolved()
    {
    	return resolved;
    }

	public Long getUser() {
		return user;
	}

	public void setUser(Long user) {
		this.user = user;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public SeverityEnum getSeverity() {
		return severity;
	}

	public void setSeverity(SeverityEnum severity) {
		this.severity = severity;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
    
}
