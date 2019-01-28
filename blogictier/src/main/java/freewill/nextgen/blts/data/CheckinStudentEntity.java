package freewill.nextgen.blts.data;

import java.io.Serializable;
import javax.persistence.*;

import freewill.nextgen.blts.BltAuditingEntityListener;

/**
 * Session Bean implementation class StudentEntity
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "CHECKINSTUDENTENTITY")
@EntityListeners(BltAuditingEntityListener.class)
public class CheckinStudentEntity implements Serializable, Cloneable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;				// ID
	private String name;			// Student Name
	private boolean active;			// whether the student attended the event
	private boolean recovery;		// whether it is a recovery
	private Long company;			// Company the project belongs to
	private Long checkin_id;		// Parent Checkin Id
	
    /**
     * Default constructor. 
     */
	public CheckinStudentEntity(){
    	// Void with no-args as requested for non-enhanced JPA entities
    	//id = 0;
    	name = "";
    	active = true;
    	recovery = false;
    	//company = null;
    }
    
    public String toString()
    {
    	return  id + ","+
    			name + "," +
    			active + "," +
    			recovery;
    }
    
    @Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (this.id == null) {
			return false;
		}

		if (obj instanceof CheckinStudentEntity && obj.getClass().equals(getClass())) {
			return this.id.equals(((CheckinStudentEntity) obj).id);
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
	public CheckinStudentEntity clone() throws CloneNotSupportedException {
		return (CheckinStudentEntity) super.clone();
	}
    
    public void setId(long id)
    {
    	this.id=id;
    }
    
    public Long getId()
    {
    	return id;
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

	public boolean getRecovery() {
		return recovery;
	}

	public void setRecovery(boolean recovery) {
		this.recovery = recovery;
	}

	public Long getCheckin_id() {
		return checkin_id;
	}

	public void setCheckin_id(Long checkin_id) {
		this.checkin_id = checkin_id;
	}
    
}
