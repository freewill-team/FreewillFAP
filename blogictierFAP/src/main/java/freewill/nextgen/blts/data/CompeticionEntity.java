package freewill.nextgen.blts.data;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import freewill.nextgen.blts.BltAuditingEntityListener;

/**
 * Session Bean implementation class CompeticionEntity
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "COMPETICIONENTITY")
@EntityListeners(BltAuditingEntityListener.class)
public class CompeticionEntity implements Serializable, Cloneable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;				// ID
	private Date fechaInicio;		
	private Date fechaFin;
	private Date fechaFinInscripcion;
	private String nombre;			// Nombre del Campeonato
	private String organizador;		// Nombre del Club que organiza el campeonato
	private boolean active;			// Campeonato status
	private Long company;			// Company the project belongs to
	private Long circuito;			// Id del Circuito al que pertenece el Campeonato
	private String circuitoStr;		// Circuito al que pertenece el Campeonato
	private TipoCompeticionEnum tipo;
	private String localidad;
	private Boolean speed;
	private Boolean classic;
	private Boolean battle;
	private Boolean jam;
	private Boolean derrapes;
	private Boolean salto;
	
	public enum TipoCompeticionEnum{
		A("Campeonato"),
		B("Copa"),
		C("Trofeo");
		private final String type;
		TipoCompeticionEnum(String t){ type = t; }
		public String toString(){ return type; }
	}
	
    /**
     * Default constructor. 
     */
	public CompeticionEntity(){
    	// Void with no-args as requested for non-enhanced JPA entities
    	//id = 0;
    	fechaFin = new Date();
    	nombre = "";
    	active = true;
    	fechaInicio = new Date();
    	fechaFinInscripcion = new Date();
    	setOrganizador("");
    	tipo = TipoCompeticionEnum.B;
    	localidad = "";
    	speed = false;
    	classic = false;
    	battle = false;
    	jam = false;
    	derrapes = false;
    	salto = false;
    	//circuito = null;
    	//company = null;
    }
    
    public String toString()
    {
    	return  id + ","+
    			nombre + "," +
    			active + "," +
    			fechaFin;
    }
    
    @Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (this.id == null) {
			return false;
		}

		if (obj instanceof CompeticionEntity && obj.getClass().equals(getClass())) {
			return this.id.equals(((CompeticionEntity) obj).id);
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
	public CompeticionEntity clone() throws CloneNotSupportedException {
		return (CompeticionEntity) super.clone();
	}
    
    public void setId(long id)
    {
    	this.id=id;
    }
    
    public Long getId()
    {
    	return id;
    }
    
    public void setActive(boolean val)
    {
    	active = val;
    }
    
    public boolean getActive()
    {
    	return active;
    }
	
	public Long getCompany() {
		return company;
	}

	public void setCompany(Long company) {
		this.company = company;
	}

	public Long getCircuito() {
		return circuito;
	}

	public void setCircuito(Long circuito) {
		this.circuito = circuito;
	}

	public String getOrganizador() {
		return organizador;
	}

	public void setOrganizador(String organizador) {
		this.organizador = organizador;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
    
	@SuppressWarnings("deprecation")
	public Date getFechaInicio() {
		fechaInicio.setHours(0);
		fechaInicio.setMinutes(0);
		return fechaInicio;
	}

	@SuppressWarnings("deprecation")
	public void setFechaInicio(Date fechaInicio) {
		fechaInicio.setHours(0);
		fechaInicio.setMinutes(0);
		this.fechaInicio = fechaInicio;
	}

	@SuppressWarnings("deprecation")
	public Date getFechaFin() {
		fechaFin.setHours(23);
		fechaFin.setMinutes(59);
		return fechaFin;
	}

	@SuppressWarnings("deprecation")
	public void setFechaFin(Date fechaFin) {
		fechaFin.setHours(23);
		fechaFin.setMinutes(59);
		this.fechaFin = fechaFin;
	}

	public TipoCompeticionEnum getTipo() {
		return tipo;
	}

	public void setTipo(TipoCompeticionEnum tipo) {
		this.tipo = tipo;
	}

	public String getLocalidad() {
		return localidad;
	}

	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}

	public String getCircuitoStr() {
		return circuitoStr;
	}

	public void setCircuitoStr(String circuitoStr) {
		this.circuitoStr = circuitoStr;
	}

	@SuppressWarnings("deprecation")
	public Date getFechaFinInscripcion() {
		fechaFinInscripcion.setHours(23);
		fechaFinInscripcion.setMinutes(59);
		return fechaFinInscripcion;
	}

	@SuppressWarnings("deprecation")
	public void setFechaFinInscripcion(Date fechaFinInscripcion) {
		fechaFinInscripcion.setHours(23);
		fechaFinInscripcion.setMinutes(59);
		this.fechaFinInscripcion = fechaFinInscripcion;
	}

	public Boolean getSpeed() {
		return speed;
	}

	public void setSpeed(Boolean speed) {
		this.speed = speed;
	}

	public Boolean getClassic() {
		return classic;
	}

	public void setClassic(Boolean classic) {
		this.classic = classic;
	}

	public Boolean getBattle() {
		return battle;
	}

	public void setBattle(Boolean battle) {
		this.battle = battle;
	}

	public Boolean getDerrapes() {
		return derrapes;
	}

	public void setDerrapes(Boolean derrapes) {
		this.derrapes = derrapes;
	}

	public Boolean getJam() {
		return jam;
	}

	public void setJam(Boolean jam) {
		this.jam = jam;
	}

	public Boolean getSalto() {
		return salto;
	}

	public void setSalto(Boolean salto) {
		this.salto = salto;
	}
    
}
