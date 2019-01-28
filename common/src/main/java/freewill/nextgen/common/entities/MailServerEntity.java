package freewill.nextgen.common.entities;

import java.io.Serializable;
import java.util.List;

/**
 * Session Bean implementation class MailServer
 */

@SuppressWarnings("serial")
public class MailServerEntity implements Serializable {
	
	private Long id;				// ID
	private String label;			// Label
	private String description;		// Description
	private String hostname;		// SMTP Mail server IP Address or Hostname
	private String port;			// Mail server Port
	private String username;		// Mail user account
	private String password;		// Mail user password
	private boolean active; 		// Whether this server is usable
	private List<UserEntity> destinations;	// List of recipients/destinations
	private Long company;			// Company the MailServer belongs to

    /**
     * Default constructor. 
     */
    public MailServerEntity(){
    	// Void with no-args as requested for non-enhanced JPA entities
		label = "";
		description = "";
		hostname = "smtp.gmail.com";
		port = "993";
		username = "";
		password = "";
		company = null;
		active = false;
    }
    
    public String toString()
    {
    	return  id + " "+
    			label + " "+
    			description + " "+
    			hostname + " "+
    			port + " "+
    			company + " "+
    			active;
    }
    
    public void setId(Long id)
    {
    	this.id=id;
    }
    
    public Long getId()
    {
    	return id;
    }

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public boolean getActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<UserEntity> getDestinations() {
		return destinations;
	}

	public void setDestinations(List<UserEntity> destinations) {
		this.destinations = destinations;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public int hashCode() {
		final int prime = 30;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final MailServerEntity other = (MailServerEntity) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}

	public Long getCompany() {
		return company;
	}

	public void setCompany(Long company) {
		this.company = company;
	}
	
}
