package freewill.nextgen.blts.data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import freewill.nextgen.blts.BltAuditingEntityListener;

/**
 * Session Bean implementation class CheckinEntity
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "CHECKINENTITY")
@EntityListeners(BltAuditingEntityListener.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CheckinEntity implements Serializable, Cloneable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;				// ID
	private Date created;			// Fecha creación/inicio de la clase
	private Date closed;			// Fecha cierre/fin de la clase
	private String caption;			// Event Name
	private String teacher;			// Event Teacher
	private Date start;				// Start Time
	private Date end;				// End Time
	private String location;		// Event Location
	private Long company;			// Company Id
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "checkin_id")
	private List<CheckinStudentEntity> students;	// List of students
	
    /**
     * Default constructor. 
     */
	public CheckinEntity(){
    	// Void with no-args as requested for non-enhanced JPA entities
    	//id = 0;
    	start = new Date();
    	end = new Date();
    	caption = "";
    	teacher = "";
    	location = "";
    }
    
    public String toString()
    {
    	return id+"/"+caption+"/"+location;
    }
    
    @Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (this.id == null) {
			return false;
		}

		if (obj instanceof CheckinEntity && obj.getClass().equals(getClass())) {
			return this.id.equals(((CheckinEntity) obj).id);
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
	public CheckinEntity clone() throws CloneNotSupportedException {
		return (CheckinEntity) super.clone();
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

	public String getTeacher() {
		return teacher;
	}

	public void setTeacher(String teacher) {
		this.teacher = teacher;
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
	
	public List<CheckinStudentEntity> getStudents() {
		return students;
	}

	public void setStudents(List<CheckinStudentEntity> students) {
		this.students = students;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getClosed() {
		return closed;
	}

	public void setClosed(Date closed) {
		this.closed = closed;
	}
	
}
