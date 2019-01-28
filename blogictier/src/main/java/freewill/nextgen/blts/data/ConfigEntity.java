package freewill.nextgen.blts.data;

import java.io.Serializable;
import javax.persistence.*;

import freewill.nextgen.blts.BltAuditingEntityListener;

/**
 * Session Bean implementation class Config
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "CONFIGENTITY")
@EntityListeners(BltAuditingEntityListener.class)
public class ConfigEntity implements Serializable, Cloneable {
	
	@Id
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;				// ID
	private String name;			// Config key name
	private String value;			// Config value
	
    /**
     * Default constructor. 
     */
    public ConfigEntity(){
    	// Void with no-args as requested for non-enhanced JPA entities
    	//id = 0;
    	name = "";
    	value = "";
    }
	
    public ConfigEntity(String k, String v){
    	//id = 0;
    	name = k;
    	value = v;
    }
    
    public String toString()
    {
    	return  id+"/"+name+"/"+value;
    }
    
    @Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (this.id == null) {
			return false;
		}

		if (obj instanceof ConfigEntity && obj.getClass().equals(getClass())) {
			return this.id.equals(((ConfigEntity) obj).id);
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
	public ConfigEntity clone() throws CloneNotSupportedException {
		return (ConfigEntity) super.clone();
	}
    
    public void setID(long id)
    {
    	this.id=id;
    }
    
    public Long getID()
    {
    	return id;
    }
    
    public void setName(String val)
    {
    	name = val;
    }
    
    public String getName()
    {
    	return name;
    }
	
    public void setValue(String val)
    {
    	value = val;
    }
    
    public String getValue()
    {
    	return value;
    }
	
}
