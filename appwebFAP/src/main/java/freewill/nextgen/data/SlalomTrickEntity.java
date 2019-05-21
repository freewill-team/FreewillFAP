package freewill.nextgen.data;

import java.io.Serializable;

/**
 * Session Bean implementation class Config
 */
@SuppressWarnings("serial")
public class SlalomTrickEntity implements Serializable, Cloneable {
	
	private Long id;				// ID
	private String nombre;
	private TrickFamilyEnum familia;
	private int valor;
	private Long company;
	
	public enum TrickFamilyEnum{
		ELASTICIDAD("Elasticidad"),
		SENTADOS("Sentados"),
		SALTOS("Saltos"),
		LINEALES("Lineales"),
		GIROS("Giros");
		private final String type;
		TrickFamilyEnum(String t){ type = t; }
		public String toString(){
			return type; //Messages.get().getKey(type, System.getProperty("LOCALE"));
		}
	}
	
    /**
     * Default constructor. 
     */
    public SlalomTrickEntity(){
    	// Void with no-args as requested for non-enhanced JPA entities
    	//id = 0;
    	familia = TrickFamilyEnum.ELASTICIDAD;
    	nombre = "";
    	valor = 10;
    	//company=null;
    }
    
    public String toString()
    {
    	return  id+"/"+nombre+"/"+familia+"/"+valor;
    }
    
    @Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (this.id == null) {
			return false;
		}

		if (obj instanceof SlalomTrickEntity && obj.getClass().equals(getClass())) {
			return this.id.equals(((SlalomTrickEntity) obj).id);
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
	public SlalomTrickEntity clone() throws CloneNotSupportedException {
		return (SlalomTrickEntity) super.clone();
	}
    
    public void setId(long id)
    {
    	this.id=id;
    }
    
    public Long getId()
    {
    	return id;
    }
    
    public void setFamilia(TrickFamilyEnum val)
    {
    	familia = val;
    }
    
    public TrickFamilyEnum getFamilia()
    {
    	return familia;
    }
	
    public void setValor(int val)
    {
    	valor = val;
    }
    
    public int getValor()
    {
    	return valor;
    }

	public Long getCompany() {
		return company;
	}

	public void setCompany(Long company) {
		this.company = company;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String name) {
		this.nombre = name;
	}
	
}
