package freewill.nextgen.blts.data;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

import freewill.nextgen.blts.BltAuditingEntityListener;

/**
 * Session Bean implementation class Requirement
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "DELIVERABLEENTITY")
@EntityListeners(BltAuditingEntityListener.class)
public class DeliverableEntity implements Serializable, Cloneable {
	
	@Id
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;				// ID
	private Date created;			// Date the deliverable was created
	private Date timestamp;			// Last Time stamp when the record was updated
	private boolean resolved;		// Resolved?
	private String name;			// Deliverable Name
	private String description;		// Deliverable long description
	private Long project;			// Project the deliverable belongs to
	private Long company;			// Company the deliverable belongs to
	
    /**
     * Default constructor. 
     */
    public DeliverableEntity(){
    	// Void with no-args as requested for non-enhanced JPA entities
    	//id = 0;
    	timestamp = new Date();
    	setCreated(new Date());
    	resolved = false;
    	description = "";
    	name = "";
    	//project = null;
    }
	
    public DeliverableEntity(String id, String ns, String ds, Long pj){
    	//id = 0;
    	timestamp = new Date();
    	setCreated(new Date());
    	resolved = false;
    	name = ns;
    	description = ds;
    	project = pj;
    }
    
    public String toString()
    {
    	return  id + ","+
    			resolved + "," +
    			name + "," +
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

		if (obj instanceof DeliverableEntity && obj.getClass().equals(getClass())) {
			return this.id.equals(((DeliverableEntity) obj).id);
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
	public DeliverableEntity clone() throws CloneNotSupportedException {
		return (DeliverableEntity) super.clone();
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

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
