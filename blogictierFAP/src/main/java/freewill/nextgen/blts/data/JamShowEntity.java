package freewill.nextgen.blts.data;

import java.io.Serializable;
import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import freewill.nextgen.blts.BltAuditingEntityListener;

/**
 * Session Bean implementation class JamShowEntity
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "JAMSHOWENTITY")
@EntityListeners(BltAuditingEntityListener.class)
public class JamShowEntity implements Serializable, Cloneable {
	
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
	private Long patinadorPareja;
	private String nombrePareja;
	private String apellidosPareja;
	private Integer dorsalPareja;
	private int orden1=0;			// Puesto de salida del patinador 1a ronda
	private int tecnicaJuez1=0;
	private int tecnicaJuez2=0;
	private int tecnicaJuez3=0;
	private int artisticaJuez1=0;
	private int artisticaJuez2=0;
	private int artisticaJuez3=0;
	//poner a @transient si no queremos guardarlo y rellenar el valor en el getter
	private int totalJuez1=0;
	private int totalJuez2=0;
	private int totalJuez3=0;
	@JsonIgnore
	private int rankingJuez1=0;
	@JsonIgnore
	private int rankingJuez2=0;
	@JsonIgnore
	private int rankingJuez3=0;
	private int penalizaciones=0;
	private int clasificacionFinal=0; //Puesto en el que quedó el patinador en resultado final
	
    /**
     * Default constructor. 
     */
	public JamShowEntity(){
    	// Void with no-args as requested for non-enhanced JPA entities
    	nombre = "";
    	apellidos = "";
    	orden1 = 0;
    	clasificacionFinal = 0;
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

		if (obj instanceof JamShowEntity && obj.getClass().equals(getClass())) {
			return this.id.equals(((JamShowEntity) obj).id);
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
	public JamShowEntity clone() throws CloneNotSupportedException {
		return (JamShowEntity) super.clone();
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

	public int getOrden1() {
		return orden1;
	}

	public void setOrden1(int orden) {
		this.orden1 = orden;
	}

	public Long getCategoria() {
		return categoria;
	}

	public void setCategoria(Long categoria) {
		this.categoria = categoria;
	}

	public int getTecnicaJuez1() {
		return tecnicaJuez1;
	}

	public void setTecnicaJuez1(int puntuacion) {
		tecnicaJuez1 = puntuacion;
	}
	
	public int getArtisticaJuez1() {
		return artisticaJuez1;
	}

	public void setArtisticaJuez1(int puntuacion) {
		artisticaJuez1 = puntuacion;
	}
	
	public int getTecnicaJuez2() {
		return tecnicaJuez2;
	}

	public void setTecnicaJuez2(int puntuacion) {
		tecnicaJuez2 = puntuacion;
	}
	public int getArtisticaJuez2() {
		return artisticaJuez2;
	}

	public void setArtisticaJuez2(int puntuacion) {
		artisticaJuez2 = puntuacion;
	}
	
	public int getTecnicaJuez3() {
		return tecnicaJuez3;
	}

	public void setTecnicaJuez3(int puntuacion) {
		tecnicaJuez3 = puntuacion;
	}
	public int getArtisticaJuez3() {
		return artisticaJuez3;
	}

	public void setArtisticaJuez3(int puntuacion) {
		artisticaJuez3 = puntuacion;
	}

	public int getPenalizaciones() {
		return penalizaciones;
	}

	public void setPenalizaciones(int puntuacion) {
		penalizaciones = puntuacion;
	}
	
	public int getClasificacionFinal() {
		return clasificacionFinal;
	}

	public void setClasificacionFinal(int clasificacionFinal) {
		this.clasificacionFinal = clasificacionFinal;
	}

	public int getTotalJuez1() {
		return totalJuez1;
	}

	public void setTotalJuez1(int total) {
		totalJuez1 = total;
	}
	public int getTotalJuez2() {
		return totalJuez2;
	}

	public void setTotalJuez2(int total) {
		totalJuez2 = total;
	}
	public int getTotalJuez3() {
		return totalJuez3;
	}

	public void setTotalJuez3(int total) {
		totalJuez3 = total;
	}

	public int getRankingJuez1() {
		return rankingJuez1;
	}

	public void setRankingJuez1(int rankingJuez1) {
		this.rankingJuez1 = rankingJuez1;
	}

	public int getRankingJuez2() {
		return rankingJuez2;
	}

	public void setRankingJuez2(int rankingJuez2) {
		this.rankingJuez2 = rankingJuez2;
	}

	public int getRankingJuez3() {
		return rankingJuez3;
	}

	public void setRankingJuez3(int rankingJuez3) {
		this.rankingJuez3 = rankingJuez3;
	}

	public Integer getDorsal() {
		return dorsal;
	}

	public void setDorsal(Integer dorsal) {
		this.dorsal = dorsal;
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

	public Integer getDorsalPareja() {
		return dorsalPareja;
	}

	public void setDorsalPareja(Integer dorsalPareja) {
		this.dorsalPareja = dorsalPareja;
	}

	public Long getPatinadorPareja() {
		return patinadorPareja;
	}

	public void setPatinadorPareja(Long patinadorPareja) {
		this.patinadorPareja = patinadorPareja;
	}
}
