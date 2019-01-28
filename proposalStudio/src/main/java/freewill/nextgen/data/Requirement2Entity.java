package freewill.nextgen.data;

import java.io.Serializable;

/**
 * Session Bean implementation class Requirement
 */
@SuppressWarnings("serial")
public class Requirement2Entity implements Serializable, Cloneable {
	
	private Long id;				// ID
	private String user;			// User Name
	private String customid;		// Requirement custom id
	private boolean resolved;		// Resolved?
	private String description;		// Requirement long description
	private Long project;			// Project the requirement belongs to
	private Long company;			// Company the requirement belongs to
	
    /**
     * Default constructor. 
     */
    public Requirement2Entity(){
    	// Void with no-args as requested for non-enhanced JPA entities
    	//id = 0;
    	customid = "";
    	resolved = false;
    	description = "";
    	//project = null;
    }
	
    public Requirement2Entity(String id, String ds, Long pj){
    	//id = 0;
    	customid = id;
    	resolved = false;
    	description = ds;
    	project = pj;
    }
    
    public String toString()
    {
    	return  id + ","+
    			user + "," +
    			customid + "," +
    			resolved + "," +
    			description;
    }
    
    @Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (this.id == null) {
			return false;
		}

		if (obj instanceof Requirement2Entity && obj.getClass().equals(getClass())) {
			return this.id.equals(((Requirement2Entity) obj).id);
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
	public Requirement2Entity clone() throws CloneNotSupportedException {
		return (Requirement2Entity) super.clone();
	}
    
    public void setId(long id)
    {
    	this.id=id;
    }
    
    public Long getId()
    {
    	return id;
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
	
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
	
}
