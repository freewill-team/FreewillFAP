package freewill.nextgen.data;

import java.io.Serializable;
import java.util.Date;

/**
 * Session Bean implementation class Product
 */
@SuppressWarnings("serial")
public class ProductEntity implements Serializable, Cloneable {
	
	private Long id;				// ID
	private Date timestamp;			// Last Time stamp when the record was updated
	private String name;			// File name
	private String description;		// File long description
	private Long company;			// Company the document belongs to
	private boolean active;			// Document status
	private Long project;			// Project the document belongs to (0L if non-project specific)
	
    /**
     * Default constructor. 
     */
    public ProductEntity(){
    	// Void with no-args as requested for non-enhanced JPA entities
    	//id = 0;
    	timestamp = new Date();
    	name = "";
    	description = "";
    	company = 0L;
    	active=true;
    	project = 0L;
    }
	
    public ProductEntity(String nm, String ds, Long cp){
    	//id = 0;
    	timestamp = new Date();
    	name = nm;
    	description = ds;
    	company = cp;
    	active=true;
    	project = 0L;
    }
    
    public String toString()
    {
    	return  id + ","+
    			name + "," +
    			description + "," +
    			company + "," +
    			project + "," +
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

		if (obj instanceof ProductEntity && obj.getClass().equals(getClass())) {
			return this.id.equals(((ProductEntity) obj).id);
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
	public ProductEntity clone() throws CloneNotSupportedException {
		return (ProductEntity) super.clone();
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
    
    public void setName(String val)
    {
    	name = val;
    }
    
    public String getName()
    {
    	return name;
    }
	
    public void setDescription(String val)
    {
    	description = val;
    }
    
    public String getDescription()
    {
    	return description;
    }

	public Long getCompany() {
		return company;
	}

	public void setCompany(Long company) {
		this.company = company;
	}

	public boolean getActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Long getProject() {
		return project;
	}

	public void setProject(Long project) {
		this.project = project;
	}
	
}
