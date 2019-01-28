package freewill.nextgen.blts.data;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

import freewill.nextgen.blts.BltAuditingEntityListener;

/**
 * Session Bean implementation class StudentEntity
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "STUDENTENTITY")
@EntityListeners(BltAuditingEntityListener.class)
public class StudentEntity implements Serializable, Cloneable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;				// ID
	private Date created;			// Date the project was created
	private Date timestamp;			// Last Time stamp when the record was updated
	private String name;			// Student Name
	private String email;			// Student IMEI
	private boolean active;			// Student status
	private Long company;			// Company the project belongs to
	
    /**
     * Default constructor. 
     */
	public StudentEntity(){
    	// Void with no-args as requested for non-enhanced JPA entities
    	//id = 0;
    	timestamp = new Date();
    	name = "";
    	active = true;
    	created = new Date();
    	email = "";
    	//company = null;
    }
	
    public StudentEntity(String fn, String as, Long cp){
    	//id = 0;
    	timestamp = new Date();
    	created = new Date();
    	name = fn;
    	active = true;
    	company = cp;
    	email = as;
    }
    
    public String toString()
    {
    	return  id + ","+
    			name + "," +
    			active + "," +
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

		if (obj instanceof StudentEntity && obj.getClass().equals(getClass())) {
			return this.id.equals(((StudentEntity) obj).id);
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 40 * hash + (id == null ? 0 : id.hashCode());
		return hash;
	}

	@Override
	public StudentEntity clone() throws CloneNotSupportedException {
		return (StudentEntity) super.clone();
	}
    
    public void setId(long id)
    {
    	this.id=id;
    }
    
    public Long getId()
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
    
    public void setActive(boolean val)
    {
    	active = val;
    }
    
    public boolean getActive()
    {
    	return active;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
    
}
