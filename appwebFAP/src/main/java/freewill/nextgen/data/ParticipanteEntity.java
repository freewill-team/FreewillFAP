package freewill.nextgen.data;

import java.io.Serializable;
import java.util.Date;

/**
 * Session Bean implementation class ParticipanteEntity
 */
@SuppressWarnings("serial")
public class ParticipanteEntity implements Serializable, Cloneable {
	
	private Long id;				// ID
	private String nombre;			// Nombre del Patinador
    private String apellidos;		// Apellidos
    private Long club;				// Id del Club al que pertenece
    private String clubStr;			// Club al que pertenece
	private Long company;			// Company the project belongs to
	private Long patinador;
	private Long patinadorPareja; 	// Id de la pareja de Jam
	private String nombrePareja;	// Nombre del Patinador pareja de Jam
    private String apellidosPareja;	// Apellidos pareja de Jam
	private Long circuito;
	private Long competicion;
	private Long categoria;
	private int dorsal;
	private int dorsalPareja;
	private int clasificacion;		// Puesto en el que quedó el patinador
	private int mejorMarca;			// Aplica a Classic, Derrapes, Battle y Jam; tiempo o altura saltada
	private int puntuacion;			// Puntuacion obtenida en esta prueba; para calculo ranking
	private String competicionStr;
	private String categoriaStr;
	private Date fecha;
	
    /**
     * Default constructor. 
     */
	public ParticipanteEntity(){
    	// Void with no-args as requested for non-enhanced JPA entities
    	//id = 0;
    	nombre = "";
    	apellidos = "";
    	nombrePareja = "";
    	apellidosPareja = "";
    	clubStr = "";
    	dorsal = 0;
    	dorsalPareja = 0;
    	clasificacion = 999;
    	mejorMarca = 0;
    	puntuacion = 0;
    	competicionStr = "";
    	categoriaStr = "";
    	fecha= new Date(0);
    	//company = null;
    }
    
    public String toString()
    {
    	return  id + ","+
    			nombre + " " + apellidos + "," +
    			dorsal;
    }
    
    @Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (this.id == null) {
			return false;
		}

		if (obj instanceof ParticipanteEntity && obj.getClass().equals(getClass())) {
			return this.id.equals(((ParticipanteEntity) obj).id);
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
	public ParticipanteEntity clone() throws CloneNotSupportedException {
		return (ParticipanteEntity) super.clone();
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

	public Long getClub() {
		return club;
	}

	public void setClub(Long club) {
		this.club = club;
	}

	public int getDorsal() {
		return dorsal;
	}

	public void setDorsal(int dorsal) {
		this.dorsal = dorsal;
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

	public int getPuntuacion() {
		return puntuacion;
	}

	public void setPuntuacion(int puntuacion) {
		this.puntuacion = puntuacion;
	}

	public int getMejorMarca() {
		return mejorMarca;
	}

	public void setMejorMarca(int mejorMarca) {
		this.mejorMarca = mejorMarca;
	}

	public int getClasificacion() {
		return clasificacion;
	}

	public void setClasificacion(int clasificacion) {
		this.clasificacion = clasificacion;
	}

	public String getClubStr() {
		return clubStr;
	}

	public void setClubStr(String clubStr) {
		this.clubStr = clubStr;
	}

	public Long getCircuito() {
		return circuito;
	}

	public void setCircuito(Long circuito) {
		this.circuito = circuito;
	}

	public Long getPatinadorPareja() {
		return patinadorPareja;
	}

	public void setPatinadorPareja(Long patinadorPareja) {
		this.patinadorPareja = patinadorPareja;
	}

	public String getNombrePareja() {
		return nombrePareja;
	}

	public void setNombrePareja(String nombrePareja) {
		this.nombrePareja = nombrePareja;
	}

	public String getApellidosPareja() {
		return apellidosPareja;
	}

	public void setApellidosPareja(String apellidosPareja) {
		this.apellidosPareja = apellidosPareja;
	}

	public int getDorsalPareja() {
		return dorsalPareja;
	}

	public void setDorsalPareja(int dorsalPareja) {
		this.dorsalPareja = dorsalPareja;
	}
    
	public String getCompeticionStr() {
		return competicionStr;
	}

	public void setCompeticionStr(String competicionStr) {
		this.competicionStr = competicionStr;
	}
	
	public String getCategoriaStr() {
		return categoriaStr;
	}

	public void setCategoriaStr(String categoriaStr) {
		this.categoriaStr = categoriaStr;
	}
	
	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	
}
