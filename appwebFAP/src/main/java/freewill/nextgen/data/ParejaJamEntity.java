package freewill.nextgen.data;

import java.util.Date;
import java.io.Serializable;

/**
 * Session Bean implementation class ParticipanteEntity
 */
@SuppressWarnings("serial")
public class ParejaJamEntity implements Serializable, Cloneable {
	
	private Long id;				// ID
	private Long patinador1;
	private String nombre1;			// Nombre del Patinador
    private String apellidos1;		// Apellidos
    private Long patinador2; 		// Id de la pareja de Jam
	private String nombre2;			// Nombre del Patinador pareja de Jam
    private String apellidos2;		// Apellidos pareja de Jam
    private Long club;				// Id del Club al que pertenece
    private String clubStr;			// Club al que pertenece
	private Long company;			// Company the project belongs to
	private Long categoria;
	private String categoriaStr;
	private Date fecha;
	
    /**
     * Default constructor. 
     */
	public ParejaJamEntity(){
    	// Void with no-args as requested for non-enhanced JPA entities
    	//id = 0;
    	nombre1 = "";
    	apellidos1 = "";
    	nombre2 = "";
    	apellidos2 = "";
    	clubStr = "";
    	categoriaStr = "";
    	fecha= new Date(0);
    	//company = null;
    }
    
    public String toString()
    {
    	return  id + ","+
    			nombre1 + " " + apellidos1 + "," +
    			nombre2 + " " + apellidos2;
    }
    
    @Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (this.id == null) {
			return false;
		}

		if (obj instanceof ParejaJamEntity && obj.getClass().equals(getClass())) {
			return this.id.equals(((ParejaJamEntity) obj).id);
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
	public ParejaJamEntity clone() throws CloneNotSupportedException {
		return (ParejaJamEntity) super.clone();
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

	public String getNombre1() {
		return nombre1;
	}

	public void setNombre1(String nombre) {
		this.nombre1 = nombre;
	}

	public String getApellidos1() {
		return apellidos1;
	}

	public void setApellidos1(String apellidos) {
		this.apellidos1 = apellidos;
	}

	public Long getClub() {
		return club;
	}

	public void setClub(Long club) {
		this.club = club;
	}

	public Long getPatinador1() {
		return patinador1;
	}

	public void setPatinador1(Long patinador) {
		this.patinador1 = patinador;
	}

	public String getClubStr() {
		return clubStr;
	}

	public void setClubStr(String clubStr) {
		this.clubStr = clubStr;
	}
    
	public Long getPatinador2() {
		return patinador2;
	}

	public void setPatinador2(Long patinadorPareja) {
		this.patinador2 = patinadorPareja;
	}

	public String getNombre2() {
		return nombre2;
	}

	public void setNombre2(String nombrePareja) {
		this.nombre2 = nombrePareja;
	}

	public String getApellidos2() {
		return apellidos2;
	}

	public void setApellidos2(String apellidosPareja) {
		this.apellidos2 = apellidosPareja;
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

	public Long getCategoria() {
		return categoria;
	}

	public void setCategoria(Long categoria) {
		this.categoria = categoria;
	}
	
}
