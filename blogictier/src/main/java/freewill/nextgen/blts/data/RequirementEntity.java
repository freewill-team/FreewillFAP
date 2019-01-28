package freewill.nextgen.blts.data;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

import freewill.nextgen.blts.BltAuditingEntityListener;
import freewill.nextgen.common.Messages;

/**
 * Session Bean implementation class Requirement
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "REQUIREMENTENTITY")
@EntityListeners(BltAuditingEntityListener.class)
public class RequirementEntity implements Serializable, Cloneable {
	
	@Id
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;				// ID
	private Date created;			// Date the requirement was created
	private Date timestamp;			// Last Time stamp when the record was updated
	private String customid;		// Requirement custom id
	private boolean resolved;		// Resolved?
	@Column(length=8096)
	private String description;		// Requirement long description
	private Long project;			// Project the requirement belongs to
	private Long company;			// Company the requirement belongs to
	private Long assignedto;		// User requirement resolution is assigned to
	private ReqTypeEnum type = ReqTypeEnum.REQ;	// Requirement type enum
	private ReqCategoryEnum category = ReqCategoryEnum.FUNCTIONAL;	// Requirement category enum
	public enum ReqTypeEnum{
		REQ("Requirement"),
		TITLE("Title");
		private final String type;
		ReqTypeEnum(String t){ type = t; }
		public String toString(){
			return Messages.get().getKey(type, System.getProperty("LOCALE"));
		}
	}
	public enum ReqCategoryEnum{
		FUNCTIONAL("Functional"),
		DOCUMENTATION("Documentation"),
		HARDWARE("Hardware"),
		SECURITY("Security"),
		PERFORMANCE("Performance"),
		AVAILABILITY("Availability"),
		QUALITY("Quality"),
		MANAGEMENT("Management");
		private final String type;
		ReqCategoryEnum(String t){ type = t; }
		public String toString(){
			return Messages.get().getKey(type, System.getProperty("LOCALE"));
		}
	}
	
    /**
     * Default constructor. 
     */
    public RequirementEntity(){
    	// Void with no-args as requested for non-enhanced JPA entities
    	//id = 0;
    	timestamp = new Date();
    	setCreated(new Date());
    	customid = "";
    	resolved = false;
    	description = "";
    	type = ReqTypeEnum.REQ;
    	category = ReqCategoryEnum.FUNCTIONAL;
    	//project = null;
    }
	
    public RequirementEntity(String id, String ds, Long pj){
    	//id = 0;
    	timestamp = new Date();
    	setCreated(new Date());
    	customid = id;
    	resolved = false;
    	description = ds;
    	type = ReqTypeEnum.REQ;
    	category = ReqCategoryEnum.FUNCTIONAL;
    	project = pj;
    }
    
    public String toString()
    {
    	return  id + ","+
    			customid + "," +
    			resolved + "," +
    			description + "," +
    			type + "," +
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

		if (obj instanceof RequirementEntity && obj.getClass().equals(getClass())) {
			return this.id.equals(((RequirementEntity) obj).id);
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
	public RequirementEntity clone() throws CloneNotSupportedException {
		return (RequirementEntity) super.clone();
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
    
    public void setCustomid(String val)
    {
    	customid = val;
    }
    
    public String getCustomid()
    {
    	return customid;
    }
    
    public void setResolved(boolean val)
    {
    	resolved = val;
    }
    
    public boolean getResolved()
    {
    	return resolved;
    }
	
    public void setDescription(String val)
    {
    	description = val;
    }
    
    public String getDescription()
    {
    	return description;
    }

	public Long getProject() {
		return project;
	}

	public void setProject(Long project) {
		this.project = project;
	}

	public Long getCompany() {
		return company;
	}

	public void setCompany(Long company) {
		this.company = company;
	}

	public Long getAssignedto() {
		return assignedto;
	}

	public void setAssignedto(Long assignedto) {
		this.assignedto = assignedto;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public ReqTypeEnum getType() {
		return type;
	}

	public void setType(ReqTypeEnum type) {
		this.type = type;
	}

	public ReqCategoryEnum getCategory() {
		return category;
	}

	public void setCategory(ReqCategoryEnum category) {
		this.category = category;
	}
	
}
