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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;				// ID
	private ConfigItemEnum name;	// Config key name
	private String value;			// Config value
	private Long company;
	
	public enum ConfigItemEnum{
		MAXUPLOADFILESIZE("Maximum upload file size (Mb)", "10"),
		MAXNUMCONOSDERRIBADOS("Máximo número conos derribados para nulo", "5"),
		FINALCONSOLACIONSPEED("Final de Consolación (3 y 4 puestos) en Speed", "true"),
		KOSYSTEMITALIANA("KO System a la Italiana", "false"),
		PENALIZAZIONCONOS("Penalización por cono derribado (ms)", "200"),
		PARTICIPANTESMINIMO("Participantes mínimo por categoría", "3"),
		EMAILREENVIOINSCRIPCION("Email reenvío inscripciones", ""),
		MINPARTICIPACIONESCIRCUITO("Mínimo participaciones para cálculo ranking circuito", "1");
		private final String type;
		private final String defval;
		ConfigItemEnum(String t, String v){ type = t; defval=v;}
		public String toString(){
			return type; //Messages.get().getKey(type, System.getProperty("LOCALE"));
		}
		public String defaultVal(){
			return defval;
		}
	}
	
    /**
     * Default constructor. 
     */
    public ConfigEntity(){
    	// Void with no-args as requested for non-enhanced JPA entities
    	//id = 0;
    	name = ConfigItemEnum.MAXUPLOADFILESIZE;
    	value = "";
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
    
    public void setId(long id)
    {
    	this.id=id;
    }
    
    public Long getId()
    {
    	return id;
    }
    
    public void setName(ConfigItemEnum val)
    {
    	name = val;
    }
    
    public ConfigItemEnum getName()
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

	public Long getCompany() {
		return company;
	}

	public void setCompany(Long company) {
		this.company = company;
	}
	
}
