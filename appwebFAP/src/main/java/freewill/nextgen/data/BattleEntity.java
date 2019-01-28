package freewill.nextgen.data;

import java.io.Serializable;

/**
 * Session Bean implementation class Battle
 */
@SuppressWarnings("serial")
public class BattleEntity implements Serializable, Cloneable {
	
	private Long id;				// ID
	private String nombre;			// Nombre del Patinador
    private String apellidos;		// Apellidos
	private Long company;			// Company the record belongs to
	private Long patinador;
	private Long competicion;
	private Long categoria;
	private Integer dorsal;
	private int orden;				// Puesto de salida del patinador
	private int clasificacion;		// Puesto en el que quedó el patinador
	
    /**
     * Default constructor. 
     */
	public BattleEntity(){
    	// Void with no-args as requested for non-enhanced JPA entities
    	//id = 0;
    	nombre = "";
    	apellidos = "";
    	orden = 0;
    	clasificacion = 0;
    	//company = null;
    }
    
    public String toString()
    {
    	return  id + ","+
    			nombre + " " + apellidos+
    			"," + categoria;
    }
    
    @Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (this.id == null) {
			return false;
		}

		if (obj instanceof BattleEntity && obj.getClass().equals(getClass())) {
			return this.id.equals(((BattleEntity) obj).id);
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 40 * hash + (id == null ? 0 : id.hashCode());
		return hash;
	}

	@Override
	public BattleEntity clone() throws CloneNotSupportedException {
		return (BattleEntity) super.clone();
	}
    
    public void setId(long id)
    {
    	this.id=id;
    }
    
    public Long getId()
    {
    	return id;
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

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public Long getPatinador() {
		return patinador;
	}

	public void setPatinador(Long patinador) {
		this.patinador = patinador;
	}

	public Long getCompeticion() {
		return competicion;
	}

	public void setCompeticion(Long competicion) {
		this.competicion = competicion;
	}

	public Long getCategoria() {
		return categoria;
	}

	public void setCategoria(Long categoria) {
		this.categoria = categoria;
	}

	public int getClasificacion() {
		return clasificacion;
	}

	public void setClasificacion(int clasificacion) {
		this.clasificacion = clasificacion;
	}

	public int getOrden() {
		return orden;
	}

	public void setOrden(int orden) {
		this.orden = orden;
	}

	public Integer getDorsal() {
		return dorsal;
	}

	public void setDorsal(Integer dorsal) {
		this.dorsal = dorsal;
	}
    
}
