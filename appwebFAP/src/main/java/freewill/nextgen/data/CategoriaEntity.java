package freewill.nextgen.data;

import java.io.Serializable;

/**
 * Session Bean implementation class CircuitoEntity
 */
@SuppressWarnings("serial")
public class CategoriaEntity implements Serializable, Cloneable {
	
	private Long id;
	private String nombre;
	private Long company;			// Company the project belongs to
	private ModalidadEnum modalidad;
	private int edadMaxima;
	private int edadMinima;
	private PatinadorEntity.GenderEnum genero;
	private Boolean active;
	private int hombres = 0;
	private int mujeres = 0;
	private AccionEnum accion = AccionEnum.NADA;
	
	public enum AccionEnum{
		NADA(""),
		DEFAULT("Valores por defecto"),
		DIVIDIR("Dividir en Masc/Fem"),
		UNIR("Crear Categoría Mixta"),
		BAJAR("Unir a Categoría Inferior"),
		SUBIR("Unir a Categoría Superior");
		private final String type;
		AccionEnum(String t){ type = t; }
		public String toString(){ return type; }
	}
	
	public enum ModalidadEnum{
		SPEED("Speed"),
		CLASSIC("Classic"),
		BATTLE("Battle"),
		JAM("Jam"),
		SLIDE("Derrapes"),
		JUMP("Salto");
		private final String type;
		ModalidadEnum(String t){ type = t; }
		public String toString(){ return type; }
	}
	
    /**
     * Default constructor. 
     */
	public CategoriaEntity(){
    	// Void with no-args as requested for non-enhanced JPA entities
    	//id = 0;
		nombre = "";
		modalidad = ModalidadEnum.SPEED;
		edadMaxima = 11;
		edadMinima = 1;
		genero = PatinadorEntity.GenderEnum.MIXTO;
		setActive(true);
    	//company = null;
    }
    
    public String toString()
    {
    	return  id + ","+
    			nombre + "," +
    			modalidad;
    }
    
    @Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (this.id == null) {
			return false;
		}

		if (obj instanceof CategoriaEntity && obj.getClass().equals(getClass())) {
			return this.id.equals(((CategoriaEntity) obj).id);
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 42 * hash + (id == null ? 0 : id.hashCode());
		return hash;
	}

	@Override
	public CategoriaEntity clone() throws CloneNotSupportedException {
		return (CategoriaEntity) super.clone();
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

	public ModalidadEnum getModalidad() {
		return modalidad;
	}

	public void setModalidad(ModalidadEnum modalidad) {
		this.modalidad = modalidad;
	}

	public int getEdadMaxima() {
		return edadMaxima;
	}

	public void setEdadMaxima(int edadMaxima) {
		this.edadMaxima = edadMaxima;
	}

	public int getEdadMinima() {
		return edadMinima;
	}

	public void setEdadMinima(int edadMinima) {
		this.edadMinima = edadMinima;
	}

	public PatinadorEntity.GenderEnum getGenero() {
		return genero;
	}

	public void setGenero(PatinadorEntity.GenderEnum genero) {
		this.genero = genero;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public int getHombres() {
		return hombres;
	}

	public void setHombres(int hombres) {
		this.hombres = hombres;
	}

	public int getMujeres() {
		return mujeres;
	}

	public void setMujeres(int mujeres) {
		this.mujeres = mujeres;
	}

	public AccionEnum getAccion() {
		return accion;
	}

	public void setAccion(AccionEnum accion) {
		this.accion = accion;
	}
	
	public int getTotal() {
		return hombres+mujeres;
	}
	
}
