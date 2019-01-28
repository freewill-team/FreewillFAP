package freewill.nextgen.blts.data;

import java.io.Serializable;

import javax.persistence.*;

import freewill.nextgen.blts.BltAuditingEntityListener;

/**
 * Session Bean implementation class Style
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "STYLEENTITY")
@EntityListeners(BltAuditingEntityListener.class)
public class Style implements Serializable, Cloneable{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;								// ID
	private String name="";							// Style name
	private String styleid="";						// Style id
	private StyleEnum level= StyleEnum.NORMAL;		// Style enum assigned to this style
	private Long company;							// Company the style belongs to
	public enum StyleEnum{
		H1("h1"),
		H2("h2"),
		H3("h3"),
		H4("h4"),
		H5("h5"),
		H6("h6"),
		H7("h7"),
		H8("h8"),
		NORMAL("Normal"),
		PARAGRAM("Paragraph Item"), // TODO cambiar por BULLET
		FIGURE("Figure");
		private final String type;
		StyleEnum(String t){ type = t; }
		public String toString(){ return type; } 
	}
	
    /**
     * Default constructor. 
     */
    public Style(){
    	// Void with no-args as requested for non-enhanced JPA entities
    }
	
    public String toString()
    {
    	return  id + "," +
    			name + "," +
    			styleid + "," +
    			level;
    }
    
    public void setId(long id)
    {
    	this.id=id;
    }
    
    public Long getId()
    {
    	return id;
    }
	
    public String getName()
    {
    	return name;
    }
    
    public void setName(String val)
    {
    	name = val;
    }
    
    public String getStyleid()
    {
    	return styleid;
    }
    
    public void setStyleid(String val)
    {
    	styleid = val;
    }
    
    public void setLevel(StyleEnum val)
    {
    	level = val;
    }
    
    public StyleEnum getLevel()
    {
    	return level;
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
   			return this.id.equals(((Style) obj).id);
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
	public Style clone() throws CloneNotSupportedException {
		return (Style) super.clone();
	}

	public Long getCompany() {
		return company;
	}

	public void setCompany(Long company) {
		this.company = company;
	}
   	
}
