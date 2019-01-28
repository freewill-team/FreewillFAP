package freewill.nextgen.data;

import java.io.Serializable;
import java.util.Date;
import freewill.nextgen.data.Style.StyleEnum;;

/**
 * Session Bean implementation class Document
 */
@SuppressWarnings("serial")
public class FeatureEntity implements Serializable, Cloneable {
	
	private Long id;				// ID
	private Date timestamp;			// Last Time stamp when the record was updated
	private String title;			// Feature title
	private boolean active;			// Feature status)
	private String description;		// Feature long description
	private StyleEnum level;		// Feature style/level (1, 2, 3, 4)
	private Long company;			// Company the document belongs to
	private byte[] image;			// Feature image
	private String imagename;		// Name of the image file
	private Double imagesize;		// Image Size (per unit)
	private Long parent;			// Parent (Feature) ID
	private Long product;			// Parent product ID
	private String tags;			// List of "tags"
	private Long project;			// Parent project ID (0L for non-project specific)
	
    /**
     * Default constructor. 
     */
    public FeatureEntity(){
    	// Void with no-args as requested for non-enhanced JPA entities
    	//id = 0;
    	timestamp = new Date();
    	title = "";
    	active = true;
    	description = "";
    	level = StyleEnum.NORMAL; //DocLevelEnum.L1;
    	company = null;
    	parent = 0L;
    	product = 0L;
    	project = 0L;
    	setTags("");
    	image = new byte[0];
    	imagesize = 1.0;
    }
	
    public FeatureEntity(String tl, String ds, Long cp, Long pp){
    	//id = 0;
    	timestamp = new Date();
    	title = tl;
    	active = true;
    	description = ds;
    	level = StyleEnum.NORMAL;
    	company = cp;
    	parent = 0L;
    	product = pp;
    	project = 0L;
    	setTags("");
    	image = new byte[0];
    	imagesize = 1.0;
    }
    
    public String toString()
    {
    	return  id + ","+
    			level + "," +
    			parent + "," +
    			title + "," +
    			active + "," +
    			description + "," +
    			product + "," +
    			project + "," +
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

		if (obj instanceof FeatureEntity && obj.getClass().equals(getClass())) {
			return this.id.equals(((FeatureEntity) obj).id);
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
	public FeatureEntity clone() throws CloneNotSupportedException {
		return (FeatureEntity) super.clone();
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
    
    public void setTitle(String val)
    {
    	title = val;
    }
    
    public String getTitle()
    {
    	return title;
    }
    
    public void setActive(boolean val)
    {
    	active = val;
    }
    
    public boolean getActive()
    {
    	return active;
    }
	
    public void setDescription(String val)
    {
    	description = val;
    }
    
    public String getDescription()
    {
    	return description;
    }
    
    public void setLevel(StyleEnum val)
    {
    	level = val;
    }
    
    public StyleEnum getLevel()
    {
    	return level;
    }

	public Long getCompany() {
		return company;
	}

	public void setCompany(Long company) {
		this.company = company;
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

	public Long getParent() {
		return parent;
	}

	public void setParent(Long parent) {
		this.parent = parent;
	}

	public Long getProduct() {
		return product;
	}

	public void setProduct(Long product) {
		this.product = product;
	}

	public String getImagename() {
		return imagename;
	}

	public void setImagename(String imagename) {
		this.imagename = imagename;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public Long getProject() {
		return project;
	}

	public void setProject(Long project) {
		this.project = project;
	}

	public Double getImagesize() {
		return imagesize;
	}

	public void setImagesize(Double imagesize) {
		this.imagesize = imagesize;
		if(imagesize==null)
			this.imagesize = 1.0;
	}
	
}
