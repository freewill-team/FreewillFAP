package freewill.nextgen.blts.data;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

import freewill.nextgen.blts.BltAuditingEntityListener;

/**
 * Session Bean implementation class Company
 */
@SuppressWarnings({"serial", "deprecation"})
@Entity
@Table(name = "COMPANYENTITY")
@EntityListeners(BltAuditingEntityListener.class)
public class CompanyEntity implements Serializable, Cloneable {
	
	@Id
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;				// ID
	private Date timestamp;			// Last Time stamp when the record was updated
	private String name;			// Company Name
	private boolean active;			// Company status
	private Date expirationdate;	// Subscription Expiration date
	private PlanEnum plan;			// Contracted plan descriptor
	private Float lastinvoice;		// Last submitted invoice in euros
	private Float laborcostrate;	// Labor cost rate per hour (p.g. €/h)
	private String currency;		// Company Currency
	@Lob
	private byte[] image;			// Company Logo
	private String imagename;		// Name of file with Company Logo
	@Lob
	private byte[] docxtemplate;	// Word (docx) template file
	@Lob
	private byte[] xlsxtemplate;	// Word (xlsx) template file
	
	public enum PlanEnum{ 
		ENTERPRISE("Enterprise"),
		STANDARD("Advanced"),
		BASIC("Basic"),
		SCHOOL("School");
		private final String type;
		PlanEnum(String t){ type = t; }
		public String toString(){ return type; } 
	}
	
    /**
     * Default constructor. 
     */
	public CompanyEntity(){
    	// Void with no-args as requested for non-enhanced JPA entities
    	//id = 0;
    	timestamp = new Date();
    	name = "";
    	active = true;
    	plan = PlanEnum.ENTERPRISE;
    	expirationdate = new Date();
    	expirationdate.setYear(2028);
    	lastinvoice = 0F;
    	currency = "€";
    }
	
	public CompanyEntity(String fn){
    	//id = 0;
    	timestamp = new Date();
    	name = fn;
    	active = true;
    	plan = PlanEnum.ENTERPRISE;
    	expirationdate = new Date();
    	expirationdate.setYear(expirationdate.getYear()+1);
    	lastinvoice = 0F;
    	currency = "€";
    }
    
    public String toString()
    {
    	return  id + ","+
    			name + "," +
    			active + "," +
    			plan + "," +
    			expirationdate + "," +
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

		if (obj instanceof CompanyEntity && obj.getClass().equals(getClass())) {
			return this.id.equals(((CompanyEntity) obj).id);
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
	public CompanyEntity clone() throws CloneNotSupportedException {
		return (CompanyEntity) super.clone();
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

	public Date getExpirationdate() {
		return expirationdate;
	}

	public void setExpirationdate(Date expirationdate) {
		this.expirationdate = expirationdate;
	}

	public PlanEnum getPlan() {
		return plan;
	}

	public void setPlan(PlanEnum plan) {
		this.plan = plan;
	}

	public Float getLaborcostrate() {
		return laborcostrate;
	}

	public void setLaborcostrate(Float laborcostrate) {
		this.laborcostrate = laborcostrate;
	}
	
	public byte[] getImage()
	{
		return image;
	}
	
	public void setImage( byte[] image )
	{
		this.image = image.clone();
	}

	public String getImagename() {
		return imagename;
	}

	public void setImagename(String imagename) {
		this.imagename = imagename;
	}
	
	public byte[] getDocxtemplate()
	{
		return docxtemplate;
	}
	
	public void setDocxtemplate( byte[] data )
	{
		this.docxtemplate = data.clone();
	}
	
	public byte[] getXlsxtemplate()
	{
		return xlsxtemplate;
	}
	
	public void setXlsxtemplate( byte[] data )
	{
		this.xlsxtemplate = data.clone();
	}

	public Float getLastinvoice() {
		return lastinvoice;
	}

	public void setLastinvoice(Float lastinvoice) {
		this.lastinvoice = lastinvoice;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
}
