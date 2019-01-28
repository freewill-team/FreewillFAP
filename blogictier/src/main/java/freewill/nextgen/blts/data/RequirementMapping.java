package freewill.nextgen.blts.data;

import java.io.Serializable;

import freewill.nextgen.blts.data.RequirementEntity.ReqCategoryEnum;
import freewill.nextgen.blts.data.RequirementEntity.ReqTypeEnum;

/**
 * Session Bean implementation class Requirement
 */
@SuppressWarnings("serial")
public class RequirementMapping implements Serializable, Cloneable {
	
	private Long id;				// ID
	private String user;			// User Name
	private String customid;		// Requirement custom id
	private boolean resolved;		// Resolved?
	private String description;		// Requirement long description
	private Long doc;				// ID feature
	private String text;			// Additional Text
	private String response;		// Requirement response (compliant, not-compliant, project specific, etc.
	private float laboreffort;		// Labor effort in hours
	private float totalcost;		// Calculated Total cost
	private String notes;			// Internal Notes
	private String mapping;			// Number and Title of feature mapped to this requirement; used in "Reportdesign"
	private String product;			// Product of featured mapped to this requirement; used in "Reportdesign"
	private ReqTypeEnum type = ReqTypeEnum.REQ;	// Requirement type enum
	private ReqCategoryEnum category = ReqCategoryEnum.FUNCTIONAL;	// Requirement category enum
	private Long project;			// Project this requirement belongs to
	
    /**
     * Default constructor. 
     */
    public RequirementMapping(){
    	// Void with no-args as requested for non-enhanced JPA entities
    	//id = 0;
    	user = "";
    	customid = "";
    	resolved = false;
    	description = "";
    	doc = null;
    	text = "";
    	response = "";
    	laboreffort = 0;
    	totalcost = 0;
    	notes = "";
    	mapping = "";
    	product = "";
    }
	
    public String toString()
    {
    	return  id + ","+
    			user + "," +
    			customid + "," +
    			resolved + "," +
    			description + "," +
    			doc + "," +
    			text + "," +
    			response;
    }
    
    @Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (this.id == null) {
			return false;
		}

		if (obj instanceof RequirementMapping && obj.getClass().equals(getClass())) {
			return this.id.equals(((RequirementMapping) obj).id);
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 49 * hash + (id == null ? 0 : id.hashCode());
		return hash;
	}

	@Override
	public RequirementMapping clone() throws CloneNotSupportedException {
		return (RequirementMapping) super.clone();
	}
    
    public void setId(long id)
    {
    	this.id=id;
    }
    
    public Long getId()
    {
    	return id;
    }
    
    public void setCustomid(String val)
    {
    	customid = val;
    }
    
    public String getCustomid()
    {
    	return customid;
    }
    
    public void setResolved(boolean val)
    {
    	resolved = val;
    }
    
    public boolean getResolved()
    {
    	return resolved;
    }
	
    public void setDescription(String val)
    {
    	description = val;
    }
    
    public String getDescription()
    {
    	return description;
    }
	
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Long getDoc() {
		return doc;
	}

	public void setDoc(Long doc) {
		this.doc = doc;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
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

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getMapping() {
		return mapping;
	}

	public void setMapping(String mapping) {
		this.mapping = mapping;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public ReqTypeEnum getType() {
		return type;
	}

	public void setType(ReqTypeEnum type) {
		this.type = type;
	}

	public ReqCategoryEnum getCategory() {
		return category;
	}

	public void setCategory(ReqCategoryEnum category) {
		this.category = category;
	}

	public Long getProject() {
		return project;
	}

	public void setProject(Long project) {
		this.project = project;
	}
	
}
