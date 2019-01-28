package freewill.nextgen.blts.data;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

import freewill.nextgen.blts.BltAuditingEntityListener;

/**
 * Session Bean implementation class TeacherEntity
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "TEACHERENTITY")
@EntityListeners(BltAuditingEntityListener.class)
public class TeacherEntity implements Serializable, Cloneable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;				// ID
	private Date created;			// Date the project was created
	private Date timestamp;			// Last Time stamp when the record was updated
	private String name;			// Teacher Name
	private String imei;			// Teacher IMEI
	private boolean active;			// Teacher status
	private Long company;			// Company the project belongs to
	
    /**
     * Default constructor. 
     */
	public TeacherEntity(){
    	// Void with no-args as requested for non-enhanced JPA entities
    	//id = 0;
    	timestamp = new Date();
    	name = "";
    	active = true;
    	created = new Date();
    	imei = "";
    	//company = null;
    }
	
    public TeacherEntity(String fn, String as, Long cp){
    	//id = 0;
    	timestamp = new Date();
    	created = new Date();
    	name = fn;
    	active = true;
    	company = cp;
    	imei = as;
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

		if (obj instanceof TeacherEntity && obj.getClass().equals(getClass())) {
			return this.id.equals(((TeacherEntity) obj).id);
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 42 * hash + (id == null ? 0 : id.hashCode());
		return hash;
	}

	@Override
	public TeacherEntity clone() throws CloneNotSupportedException {
		return (TeacherEntity) super.clone();
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

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}
    
}
