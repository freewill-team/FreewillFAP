package freewill.nextgen.common.entities;

import java.io.Serializable;
import java.util.Date;

/**
 * Session Bean implementation class Company
 */

@SuppressWarnings("serial")
public class EmailEntity implements Serializable, Cloneable {
	
	private Long id;
	private Date timestamp;			// Time stamp when the email was generated
	private String email;			// E-mail for contact
	private String subject;			// Subject of the email
	private String message;			// Message/description of the email
	private Long company;			// Company the email belongs to

    /**
     * Default constructor. 
     */
    public EmailEntity(){
    	// Void with no-args as requested for non-enhanced JPA entities
    }
    
    public EmailEntity(String rcp, String sbjt, String msg, Long cmpy) {
    	this.email = rcp;
		this.subject = sbjt;
		this.message = msg;
		this.company = cmpy;
	}

	public String toString()
    {
    	return  timestamp + " " + 
    			subject + " " +
    			message + " " +
    			company;
    }
    
    public void setTimestamp(Date ts)
    {
    	timestamp = ts;
    }
    
    public Date getTimestamp()
    {
    	return timestamp;
    }
    
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getCompany() {
		return company;
	}

	public void setCompany(Long company) {
		this.company = company;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (this.id == null) {
			return false;
		}

		if (obj instanceof EmailEntity && obj.getClass().equals(getClass())) {
			return this.id.equals(((EmailEntity) obj).id);
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
	public EmailEntity clone() throws CloneNotSupportedException {
		return (EmailEntity) super.clone();
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
}
