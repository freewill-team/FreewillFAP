package freewill.nextgen.blts.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

import freewill.nextgen.blts.BltAuditingEntityListener;
import freewill.nextgen.common.entities.UserEntity.LanguageEnum;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;

/**
 * Session Bean implementation class User
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "USERENTITY")
@EntityListeners(BltAuditingEntityListener.class)
public class UserEntity implements Serializable, Cloneable {
	
	@Id
	//@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;				// ID
	private Date timestamp;			// Last Time stamp when the record was updated
	private String name;			// User Full Name
	private String loginname;		// login id
	private String password;		// User password
	private Long company;			// Company the user belongs to
	private boolean active;			// User account status
	private UserRoleEnum role;		// User role
	private LanguageEnum language;	// User Language/Locale
	private String email;			// E-mail for contact
	private boolean firsttime;		// whether the user has already logged in
	
    /**
     * Default constructor. 
     */
    public UserEntity(){
    	// Void with no-args as requested for non-enhanced JPA entities
    	//id = 0;
    	timestamp = new Date();
    	name = "";
    	loginname = "";
    	password = "";
    	//company = null;
    	active = true;
    	role = UserRoleEnum.USER; 
    	language = LanguageEnum.ES;
    	email = "";
    	firsttime = true;
    }
	
    public UserEntity(String fn, String ln, String pw, Long cp, 
    	UserRoleEnum rl, LanguageEnum lo, String em){
    	//id = 0;
    	timestamp = new Date();
    	name = fn;
    	loginname = ln;
    	password = pw;
    	company = cp;
    	active = true;
    	role = rl; 
    	language = lo;
    	email = em;
    	firsttime = true;
    }
    
    public String toString()
    {
    	return  id + ","+
    			name + "," +
    			loginname + "," +
    			password + "," +
    			company + "," +
    			active + "," +
    			role + "," +
    			language + "," +
    			email + "," +
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

		if (obj instanceof UserEntity && obj.getClass().equals(getClass())) {
			return this.id.equals(((UserEntity) obj).id);
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
	public UserEntity clone() throws CloneNotSupportedException {
		return (UserEntity) super.clone();
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
    
    public void setLoginname(String val)
    {
    	loginname = val;
    }
    
    public String getLoginname()
    {
    	return loginname;
    }
    
    public void setPassword(String val)
    {
    	password = val;
    }
    
    public String getPassword()
    {
    	return password;
    }
    
    public void setCompany(Long val)
    {
    	company = val;
    }
    
    public Long getCompany()
    {
    	return company;
    }
    
    public void setActive(boolean val)
    {
    	active = val;
    }
    
    public boolean getActive()
    {
    	return active;
    }
    
    public void setRole(UserRoleEnum val)
    {
    	role = val;
    }
    
    public UserRoleEnum getRole()
    {
    	return role;
    }
	
    public void setLanguage(LanguageEnum val)
    {
    	language = val;
    }
    
    public LanguageEnum getLanguage()
    {
    	return language;
    }

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean getFirsttime() {
		return firsttime;
	}

	public void setFirsttime(boolean firsttime) {
		this.firsttime = firsttime;
	}
    
}
