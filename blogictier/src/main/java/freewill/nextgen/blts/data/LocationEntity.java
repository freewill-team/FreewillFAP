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
@Table(name = "LOCATIONENTITY")
@EntityListeners(BltAuditingEntityListener.class)
public class LocationEntity implements Serializable, Cloneable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;				// ID
	private Date created;			// Date the project was created
	private Date timestamp;			// Last Time stamp when the record was updated
	private String name;			// Location Name
	private Double latitude;		// Location Gps latitude
	private Double longitude;		// Location Gps longitude
	private boolean active;			// status
	private Long company;			// Company the project belongs to
	
    /**
     * Default constructor. 
     */
	public LocationEntity(){
    	// Void with no-args as requested for non-enhanced JPA entities
    	//id = 0;
    	timestamp = new Date();
    	name = "";
    	active = true;
    	created = new Date();
    	latitude = 37.3755307;
    	longitude = -6.0022902;
    	//company = null;
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

		if (obj instanceof LocationEntity && obj.getClass().equals(getClass())) {
			return this.id.equals(((LocationEntity) obj).id);
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
	public LocationEntity clone() throws CloneNotSupportedException {
		return (LocationEntity) super.clone();
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

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
    
}
