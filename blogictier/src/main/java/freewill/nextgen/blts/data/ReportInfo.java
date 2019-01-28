package freewill.nextgen.blts.data;

import java.io.Serializable;
import java.util.Set;

/**
 * Session Bean implementation class Requirement
 */
@SuppressWarnings("serial")
public class ReportInfo implements Serializable, Cloneable {
	
	private Long id;				// Initially null, will be filled with FileEntity Id
	private String name;			// Report Name
	private ReportTypeEnum type;	// Report Type
	private Long project;			// Project the report belongs to
	private Set<Long> products;		// Products used to generate the report
	
	public enum ReportTypeEnum{
		FUNCTIONAL("Functional Description"),
		FUNCTIONALXLS("Functional Description (XLS)"),
		TENDER("Tender Proposal"),
		RFI("RFI Response"),
		DESIGN("Solution Design"),
		TOCXLS("Table Of Compliance"),
		TOCXLSEXT("Extended Table Of Compliance"),
		COSTXLS("Cost Estimation"),
		COVERAGE("Coverage Report"),
		DELIVERY("Deliverables Report");
		private final String type;
		ReportTypeEnum(String t){ type = t; }
		public String toString(){
			return type; //Messages.get().getKey(type, System.getProperty("LOCALE"));
		}
	}
	
    /**
     * Default constructor. 
     */
    public ReportInfo(){
    	// Void with no-args as requested for non-enhanced JPA entities
    	//id = 0;
    	name = "";
    	type = ReportTypeEnum.FUNCTIONAL;
    }
	
    public String toString()
    {
    	return  name + " "+
    			type;
    }
    
    @Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (this.name == null) {
			return false;
		}

		if (obj instanceof ReportInfo && obj.getClass().equals(getClass())) {
			return this.id.equals(((ReportInfo) obj).id);
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
	public ReportInfo clone() throws CloneNotSupportedException {
		return (ReportInfo) super.clone();
	}

	public void setId(long id)
    {
    	this.id=id;
    }
    
    public Long getId()
    {
    	return id;
    }
    
	public ReportTypeEnum getType() {
		return type;
	}

	public void setType(ReportTypeEnum type) {
		this.type = type;
	}
    
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getProject() {
		return project;
	}

	public void setProject(Long project) {
		this.project = project;
	}

	public Set<Long> getProducts() {
		return products;
	}

	public void setProducts(Set<Long> products) {
		this.products = products;
	}
	
}
