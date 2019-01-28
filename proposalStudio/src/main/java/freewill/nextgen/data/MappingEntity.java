package freewill.nextgen.data;

import java.io.Serializable;
import java.util.Date;

/**
 * Session Bean implementation class Mapping
 */
@SuppressWarnings("serial")
public class MappingEntity implements Serializable, Cloneable {
	
	private Long id;				// ID
	private Long doc;				// ID Document
	private Long req;				// ID Requirement
	private Date timestamp;			// Last Time stamp when the record was updated
	private String text;			// Additional Text
	private String response;		// Requirement response (compliant, not-compliant, project specific, etc.
	private byte[] image;			// Additional image
	private String imagename;		// Name of file with Document image
	private float laboreffort;		// Labor effort in hours
	private float totalcost;		// Calculated Total cost
	private String notes;			// Internal Notes
	private Long deliverable;		// Deliverable associated to this requirement
	
    /**
     * Default constructor. 
     */
    public MappingEntity(){
    	// Void with no-args as requested for non-enhanced JPA entities
    	text = "";
    	timestamp = new Date();
    	doc = -1L;
    	req = -1L;
    	response = "";
    	laboreffort = 0;
    	totalcost = 0;
    	image = new byte[0];
    }
	
    public MappingEntity(Long id, Long ir, String tx, String rp){
    	//id = 0;
    	timestamp = new Date();
    	text = tx;
    	doc = id;
    	req = ir;
    	response = rp;
    	laboreffort = 0;
    	totalcost = 0;
    	image = new byte[0];
    }
    
    public String toString()
    {
    	return  id + ","+
    			text + "," +
    			doc + "," +
    			req + "," +
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

		if (obj instanceof MappingEntity && obj.getClass().equals(getClass())) {
			return this.id.equals(((MappingEntity) obj).id);
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
	public MappingEntity clone() throws CloneNotSupportedException {
		return (MappingEntity) super.clone();
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
    
    public void setText(String val)
    {
    	text = val;
    }
    
    public String getText()
    {
    	return text;
    }
    
    public void setDoc(long id)
    {
    	this.doc=id;
    }
    
    public Long getDoc()
    {
    	return doc;
    }
    
    public void setReq(long id)
    {
    	this.req=id;
    }
    
    public Long getReq()
    {
    	return req;
    }

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}
	
	public byte[] getImage()
	{
		return image;
	}
	
	public void setImage( byte[] data )
	{
		if(data==null){
			image = new byte[0];
			return;
		}
		this.image = data.clone();
	}

	public float getLaboreffort() {
		return laboreffort;
	}

	public void setLaboreffort(float laboreffort) {
		this.laboreffort = laboreffort;
	}

	public float getTotalcost() {
		return totalcost;
	}

	public void setTotalcost(float totalcost) {
		this.totalcost = totalcost;
	}

	public String getImagename() {
		return imagename;
	}

	public void setImagename(String imagename) {
		this.imagename = imagename;
	}

	public Long getDeliverable() {
		return deliverable;
	}

	public void setDeliverable(Long deliverable) {
		this.deliverable = deliverable;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
	
}
