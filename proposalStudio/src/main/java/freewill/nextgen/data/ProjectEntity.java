package freewill.nextgen.data;

import java.io.Serializable;
import java.util.Date;

//import freewill.nextgen.common.Messages;
import freewill.nextgen.common.entities.UserEntity.LanguageEnum;
import freewill.nextgen.hmi.utils.Messages;

/**
 * Session Bean implementation class Project
 */
@SuppressWarnings("serial")
public class ProjectEntity implements Serializable, Cloneable {
	
	private Long id;				// ID
	private Date created;			// Date the project was created
	private Date timestamp;			// Last Time stamp when the record was updated
	private String name;			// Project Name
	private String description;		// Project long description
	private boolean active;			// Project status
	private Date deliverydate;		// Intended Project Delivery date
	private String answers;			// List of "answers" to be used with requirements
	private Long company;			// Company the project belongs to
	private byte[] logo;			// Project's Logo
	private String imagename;		// Name of file with Logo image
	private LanguageEnum language;	// User Language/Locale
	private ProjectStatusEnum status; // Project status
	public enum ProjectStatusEnum{
		OPEN("Open"),
		CLOSED("Closed"),
		CANCELED("Canceled"),
		LOST("Lost"),
		WON("Won"),
		EXECUTION("OnExecution");
		private final String type;
		ProjectStatusEnum(String t){ type = t; }
		public String toString(){
			//return Messages.get().getKey(type, System.getProperty("LOCALE"));
			return Messages.get().getKey(type);
		} 
	}
	
    /**
     * Default constructor. 
     */
    @SuppressWarnings("deprecation")
	public ProjectEntity(){
    	// Void with no-args as requested for non-enhanced JPA entities
    	//id = 0;
    	timestamp = new Date();
    	name = "";
    	active = true;
    	created = new Date();
    	deliverydate = new Date();
    	deliverydate.setYear(2028);
    	description = "";
    	//company = null;
    	answers = "Soportado,No Soportado,Desarrollo,Configuracion,Roadmap";
    	status = ProjectStatusEnum.OPEN;
    	logo = new byte[0];
    	language = LanguageEnum.EN;
    }
	
    public ProjectEntity(String fn, Date dd, String as, Long cp){
    	//id = 0;
    	timestamp = new Date();
    	created = new Date();
    	name = fn;
    	active = true;
    	deliverydate = dd;
    	description = "";
    	answers = as;
    	company = cp;
    	status = ProjectStatusEnum.OPEN;
    	logo = new byte[0];
    	language = LanguageEnum.EN;
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

		if (obj instanceof ProjectEntity && obj.getClass().equals(getClass())) {
			return this.id.equals(((ProjectEntity) obj).id);
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
	public ProjectEntity clone() throws CloneNotSupportedException {
		return (ProjectEntity) super.clone();
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
    
    public void setActive(boolean val)
    {
    	active = val;
    }
    
    public boolean getActive()
    {
    	return active;
    }
	
    public void setDeliverydate(Date ts)
    {
    	deliverydate = ts;
    }
    
    public Date getDeliverydate()
    {
    	return deliverydate;
    }
    
    public void setAnswers(String val)
    {
    	answers = val;
    }
    
    public String getAnswers()
    {
    	return answers;
    }

	public Long getCompany() {
		return company;
	}

	public void setCompany(Long company) {
		this.company = company;
	}

	public byte[] getLogo() {
		return logo;
	}

	public void setLogo(byte[] data) {
		if(data==null){
			logo = new byte[0];
			return;
		}
		this.logo = data.clone();
	}

	public LanguageEnum getLanguage() {
		return language;
	}

	public void setLanguage(LanguageEnum language) {
		this.language = language;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getImagename() {
		return imagename;
	}

	public void setImagename(String imagename) {
		this.imagename = imagename;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ProjectStatusEnum getStatus() {
		return status;
	}

	public void setStatus(ProjectStatusEnum status) {
		this.status = status;
	}
    
}
