package freewill.nextgen.data;

import java.io.Serializable;
import java.util.Date;

/**
 * Session Bean implementation class File
 */
@SuppressWarnings("serial")
public class FileEntity implements Serializable, Cloneable {
	
	private Long id;				// ID
	private Date timestamp;			// Last Time stamp when the record was updated
	private String name;			// File name
	private String description;		// File long description
	private Long company;			// Company the document belongs to
	private Long project;			// Project the requirement belongs to
	private byte[] image;			// Document image
	
    /**
     * Default constructor. 
     */
    public FileEntity(){
    	// Void with no-args as requested for non-enhanced JPA entities
    	//id = 0;
    	timestamp = new Date();
    	name = "";
    	description = "";
    	company = null;
    	project = null;
    }
	
    public FileEntity(String nm, String ds, Long cp, Long pj){
    	//id = 0;
    	timestamp = new Date();
    	name = nm;
    	description = ds;
    	company = cp;
    	project = pj;
    }
    
    public String toString()
    {
    	return  id + ","+
    			name + "," +
    			description + "," +
    			project + "," +
    			company + "," +
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

		if (obj instanceof FileEntity && obj.getClass().equals(getClass())) {
			return this.id.equals(((FileEntity) obj).id);
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
	public FileEntity clone() throws CloneNotSupportedException {
		return (FileEntity) super.clone();
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
    
	public byte[] getImage()
	{
		return image;
	}
	
	public void setImage( byte[] image )
	{
		this.image = image.clone();
	}
	
	public Long getProject() {
		return project;
	}

	public void setProject(Long project) {
		this.project = project;
	}
	
}
