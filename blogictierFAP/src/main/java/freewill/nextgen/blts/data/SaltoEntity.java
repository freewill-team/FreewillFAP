package freewill.nextgen.blts.data;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

import freewill.nextgen.blts.BltAuditingEntityListener;
import freewill.nextgen.blts.data.SaltoIntentoEntity.ResultEnum;

/**
 * Session Bean implementation class SpeedTimeTrial
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "SALTOENTITY")
@EntityListeners(BltAuditingEntityListener.class)
public class SaltoEntity implements Serializable, Cloneable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;				// ID
	private String nombre;			// Nombre del Patinador
    private String apellidos;		// Apellidos
	private Long company;			// Company the record belongs to
	private Long patinador;
	private Long competicion;
	private Long categoria;
	private Integer dorsal;
	private int orden;				// Puesto de salida del patinador
	private int mejorSalto;			// en cms
	private int clasificacion;		// Puesto en el que quedó el patinador
	private int numeroOKs;
	private int numeroSaltos;
	private int numeroFallos;
	private int alturaPrimerFallo;
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "saltoPatinadorId")
	private List<SaltoIntentoEntity> intentos;
	@Transient
	private int ronda;
	@Transient
	private ResultEnum salto1;
	@Transient
	private ResultEnum salto2;
	@Transient
	private ResultEnum salto3;
	@Transient
	private int altura;
	
    /**
     * Default constructor. 
     */
	public SaltoEntity(){
    	// Void with no-args as requested for non-enhanced JPA entities
    	//id = 0;
    	nombre = "";
    	apellidos = "";
    	orden = 0;
    	clasificacion = 0;
    	mejorSalto = 0;
    	numeroOKs = 0;
    	numeroSaltos = 0;
    	numeroFallos = 0;
    	alturaPrimerFallo = 0;
    	ronda = 0;
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

		if (obj instanceof SaltoEntity && obj.getClass().equals(getClass())) {
			return this.id.equals(((SaltoEntity) obj).id);
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
	public SaltoEntity clone() throws CloneNotSupportedException {
		return (SaltoEntity) super.clone();
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

	public int getOrden() {
		return orden;
	}

	public void setOrden(int orden) {
		this.orden = orden;
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

	public int getMejorSalto() {
		return mejorSalto;
	}

	public void setMejorSalto(int mejorSalto) {
		this.mejorSalto = mejorSalto;
	}

	public int getNumeroOKs() {
		return numeroOKs;
	}

	public void setNumeroOKs(int numeroOk) {
		this.numeroOKs = numeroOk;
	}

	public int getNumeroSaltos() {
		return numeroSaltos;
	}

	public void setNumeroSaltos(int numeroSaltos) {
		this.numeroSaltos = numeroSaltos;
	}

	public int getNumeroFallos() {
		return numeroFallos;
	}

	public void setNumeroFallos(int numeroFallos) {
		this.numeroFallos = numeroFallos;
	}

	public int getAlturaPrimerFallo() {
		return alturaPrimerFallo;
	}

	public void setAlturaPrimerFallo(int alturaPrimerFallo) {
		this.alturaPrimerFallo = alturaPrimerFallo;
	}

	public List<SaltoIntentoEntity> getIntentos() {
		return intentos;
	}

	public void setIntentos(List<SaltoIntentoEntity> intentos) {
		this.intentos = intentos;
	}
	
	public ResultEnum getSalto1() {
		return salto1;
	}

	public void setSalto1(ResultEnum salto1) {
		this.salto1 = salto1;
	}

	public ResultEnum getSalto2() {
		return salto2;
	}

	public void setSalto2(ResultEnum salto2) {
		this.salto2 = salto2;
	}

	public ResultEnum getSalto3() {
		return salto3;
	}

	public void setSalto3(ResultEnum salto3) {
		this.salto3 = salto3;
	}

	public int getAltura() {
		return altura;
	}

	public void setAltura(int altura) {
		this.altura = altura;
	}

	public int getRonda() {
		return ronda;
	}

	public void setRonda(int ronda) {
		this.ronda = ronda;
	}

	public Integer getDorsal() {
		return dorsal;
	}

	public void setDorsal(Integer dorsal) {
		this.dorsal = dorsal;
	}
	
}
