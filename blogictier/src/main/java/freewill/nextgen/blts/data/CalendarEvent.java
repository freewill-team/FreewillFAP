package freewill.nextgen.blts.data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import freewill.nextgen.blts.BltAuditingEntityListener;

/**
 * Session Bean implementation class CalendarEvent
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "CALENDAREVENT")
@EntityListeners(BltAuditingEntityListener.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CalendarEvent implements Serializable, Cloneable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;				// ID
	private String caption;			// Event Name
	private String description;		// Event Description
	private Date start;				// Start Time
	private Date end;				// End Time
	private String location;		// Event Location
	private Double latitude;		// Location Gps latitude
	private Double longitude;		// Location Gps longitude
	private boolean allDay;			// Is all day event?
	private Long company;			// Company Id
	@ManyToMany(cascade = { CascadeType.REFRESH }, fetch = FetchType.LAZY)
	//@JoinTable(name = "STUDENTENTITY", joinColumns = @JoinColumn(name = "id"))
	private List<StudentEntity> students;	// List of students
	
    /**
     * Default constructor. 
     */
	public CalendarEvent(){
    	// Void with no-args as requested for non-enhanced JPA entities
    	//id = 0;
    	start = new Date();
    	end = new Date();
    	caption = "";
    	description = "";
    	location = "";
    	allDay = false;
    }
    
    public String toString()
    {
    	return  id+"/"+caption+"/"+location;
    }
    
    @Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (this.id == null) {
			return false;
		}

		if (obj instanceof CalendarEvent && obj.getClass().equals(getClass())) {
			return this.id.equals(((CalendarEvent) obj).id);
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 41 * hash + (id == null ? 0 : id.hashCode());
		return hash;
	}

	@Override
	public CalendarEvent clone() throws CloneNotSupportedException {
		return (CalendarEvent) super.clone();
	}
    
    public void setId(long id)
    {
    	this.id=id;
    }
    
    public Long getId()
    {
    	return id;
    }
    
    public void setCaption(String val)
    {
    	caption = val;
    }
    
    public String getCaption()
    {
    	return caption;
    }
    
    public void setAllDay(boolean val)
    {
    	allDay = val;
    }
    
    public boolean getAllDay()
    {
    	return allDay;
    }

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public Long getCompany() {
		return company;
	}

	public void setCompany(Long company) {
		this.company = company;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
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
	
	public List<StudentEntity> getStudents() {
		return students;
	}

	public void setDestinations(List<StudentEntity> students) {
		this.students = students;
	}
	
}
