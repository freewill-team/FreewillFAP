package freewill.nextgen.blts.data;

import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import freewill.nextgen.blts.BltAuditingEntityListener;

/**
 * Session Bean implementation class ClassicShowEntity
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "CLASSICSHOWENTITY")
@EntityListeners(BltAuditingEntityListener.class)
public class ClassicShowEntity implements Serializable, Cloneable {
	
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
	private int orden1=0;			// Puesto de salida del patinador 1a ronda
	private int tecnicaJuez1=0;
	private int tecnicaJuez2=0;
	private int tecnicaJuez3=0;
	private int artisticaJuez1=0;
	private int artisticaJuez2=0;
	private int artisticaJuez3=0;
	private float totalJuez1=0;
	private float totalJuez2=0;
	private float totalJuez3=0;
	private int rankingJuez1=0;
	private int rankingJuez2=0;
	private int rankingJuez3=0;
	private float penalizaciones=0;
	private float sumaPV=0;
	private float PVLocales=0;
	private int totalTecnica=0;
    private float PVTotal=0;
    private float puntuacionTotal=0;
	private int clasificacionFinal=0; //Puesto en el que quedó el patinador en resultado final
	private float sumaPonderada = 0;
	
    /**
     * Default constructor. 
     */
	public ClassicShowEntity(){
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

		if (obj instanceof ClassicShowEntity && obj.getClass().equals(getClass())) {
			return this.id.equals(((ClassicShowEntity) obj).id);
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
	public ClassicShowEntity clone() throws CloneNotSupportedException {
		return (ClassicShowEntity) super.clone();
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

	public float getPenalizaciones() {
		return penalizaciones;
	}

	public void setPenalizaciones(float puntuacion) {
		penalizaciones = puntuacion;
	}
	
	public int getClasificacionFinal() {
		return clasificacionFinal;
	}

	public void setClasificacionFinal(int clasificacionFinal) {
		this.clasificacionFinal = clasificacionFinal;
	}

	public float getTotalJuez1() {
		return totalJuez1;
	}

	public void setTotalJuez1(float total) {
		totalJuez1 = total;
	}
	public float getTotalJuez2() {
		return totalJuez2;
	}

	public void setTotalJuez2(float total) {
		totalJuez2 = total;
	}
	public float getTotalJuez3() {
		return totalJuez3;
	}

	public void setTotalJuez3(float total) {
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

	public float getSumaPV() {
		return sumaPV;
	}

	public void setSumaPV(float sumaPV) {
		this.sumaPV = sumaPV;
	}

	public float getPVLocales() {
		return PVLocales;
	}

	public void setPVLocales(float pVLocales) {
		PVLocales = pVLocales;
	}

	public int getTotalTecnica() {
		return totalTecnica;
	}

	public void setTotalTecnica(int totalTecnica) {
		this.totalTecnica = totalTecnica;
	}

	public float getPVTotal() {
		return PVTotal;
	}

	public void setPVTotal(float totalPV) {
		PVTotal = totalPV;
	}

	public float getPuntuacionTotal() {
		return puntuacionTotal;
	}

	public void setPuntuacionTotal(float puntuacionTotal) {
		this.puntuacionTotal = puntuacionTotal;
	}

	public float getSumaPonderada() {
		return sumaPonderada;
	}

	public void setSumaPonderada(float sumaPonderada) {
		this.sumaPonderada = sumaPonderada;
	}
}
