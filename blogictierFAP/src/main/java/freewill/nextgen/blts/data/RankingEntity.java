package freewill.nextgen.blts.data;

import java.io.Serializable;
import javax.persistence.*;

import freewill.nextgen.blts.BltAuditingEntityListener;

/**
 * Session Bean implementation class PuntuacionesEntity
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "RANKINGENTITY")
@EntityListeners(BltAuditingEntityListener.class)
public class RankingEntity implements Serializable, Cloneable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;				// ID
	private String nombre;			// Nombre del Patinador
    private String apellidos;		// Apellidos
    private String nombrePareja;	// Nombre del Patinador
    private String apellidosPareja;	// Apellidos
    private Long club;				// Id del Club al que pertenece
    private String clubStr;			// Club al que pertenece
	private Long company;			// Company the project belongs to
	private Long patinador;
	private Long patinadorPareja;
	private Long circuito;
	private Long competicion;
	private Long categoria;
	private String categoriaStr;
	private int puntuacion;			// Puntuacion acumulada obtenida
	private int puntos1;
	private int puntos2;
	private int puntos3;
	private int puntos4;
	private String competicion1;
	private String competicion2;
	private String competicion3;
	private String competicion4;
	@Transient
	private int orden;
	private Long parejaJam;
	
    /**
     * Default constructor. 
     */
	public RankingEntity(){
    	// Void with no-args as requested for non-enhanced JPA entities
    	//id = 0;
		nombre = "";
    	apellidos = "";
    	nombrePareja = "";
    	apellidosPareja = "";
    	clubStr = "";
    	puntuacion = 0;
    	orden = 0;
    	puntos1 = 0;
    	puntos2 = 0;
    	puntos3 = 0;
    	puntos4 = 0;
    	setCategoriaStr("");
    	//company = null;
    }
    
    public String toString()
    {
    	return  id + ","+
    			nombre + " " + apellidos + "," +
    			puntuacion + "," + circuito;
    }
    
    @Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (this.id == null) {
			return false;
		}

		if (obj instanceof RankingEntity && obj.getClass().equals(getClass())) {
			return this.id.equals(((RankingEntity) obj).id);
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
	public RankingEntity clone() throws CloneNotSupportedException {
		return (RankingEntity) super.clone();
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

	public String getClubStr() {
		return clubStr;
	}

	public void setClubStr(String clubStr) {
		this.clubStr = clubStr;
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

	public Long getCircuito() {
		return circuito;
	}

	public void setCircuito(Long circuito) {
		this.circuito = circuito;
	}

	public int getOrden() {
		return orden;
	}

	public void setOrden(int orden) {
		this.orden = orden;
	}

	public int getPuntos1() {
		return puntos1;
	}

	public void setPuntos1(int puntos1) {
		this.puntos1 = puntos1;
	}

	public int getPuntos2() {
		return puntos2;
	}

	public void setPuntos2(int puntos2) {
		this.puntos2 = puntos2;
	}

	public int getPuntos3() {
		return puntos3;
	}

	public void setPuntos3(int puntos3) {
		this.puntos3 = puntos3;
	}

	public int getPuntos4() {
		return puntos4;
	}

	public void setPuntos4(int puntos4) {
		this.puntos4 = puntos4;
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

	public String getCompeticion1() {
		return competicion1;
	}

	public void setCompeticion1(String competicion1) {
		this.competicion1 = competicion1;
	}

	public String getCompeticion2() {
		return competicion2;
	}

	public void setCompeticion2(String competicion2) {
		this.competicion2 = competicion2;
	}

	public String getCompeticion3() {
		return competicion3;
	}

	public void setCompeticion3(String competicion3) {
		this.competicion3 = competicion3;
	}

	public String getCompeticion4() {
		return competicion4;
	}

	public void setCompeticion4(String competicion4) {
		this.competicion4 = competicion4;
	}

	public String getCategoriaStr() {
		return categoriaStr;
	}

	public void setCategoriaStr(String categoriaStr) {
		this.categoriaStr = categoriaStr;
	}

	public Long getPatinadorPareja() {
		return patinadorPareja;
	}

	public void setPatinadorPareja(Long patinadorPareja) {
		this.patinadorPareja = patinadorPareja;
	}

	public Long getParejaJam() {
		return parejaJam;
	}

	public void setParejaJam(Long parejaJam) {
		this.parejaJam = parejaJam;
	}
	
}
