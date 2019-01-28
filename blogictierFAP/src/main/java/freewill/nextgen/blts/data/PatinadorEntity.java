package freewill.nextgen.blts.data;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import freewill.nextgen.blts.BltAuditingEntityListener;

/**
 * Session Bean implementation class PatinadorEntity
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "PATINADORENTITY")
@EntityListeners(BltAuditingEntityListener.class)
public class PatinadorEntity implements Serializable, Cloneable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;				// ID
	private Date created;			// Date the project was created
	private Date timestamp;			// Last Time stamp when the record was updated
	private String nombre;			// Nombre del Patinador
    private String apellidos;		// Apellidos
    private Date fechaNacimiento;	// fecha de Nacimiento
    private String dni;				// DNI
    private Long club;				// Id del Club al que pertenece
    private String clubStr;			// Club al que pertenece
    private String fichaFederativa;	// ID de la ficha de la RFEP
	private String email;			// Patinador email
	private boolean active;			// Patinador status
	private Long company;			// Company the project belongs to
	private GenderEnum genero;		// Genero Masculino/Femenino
	@Transient
	private boolean speed;
	@Transient
	private boolean classic;
	@Transient
	private boolean battle;
	@Transient
	private boolean jam;
	@Transient
	private boolean derrapes;
	@Transient
	private boolean salto;
	@Transient
	private Integer dorsal;
	@Transient
	private String catSpeed;
	@Transient
	private String catClassic;
	@Transient
	private String catBattle;
	@Transient
	private String catJam;
	@Transient
	private String catDerrapes;
	@Transient
	private String catSalto;
	@Transient
	private String nombrePareja;
	@Transient
	private String apellidosPareja;
	@Transient
	private Integer dorsalPareja;
	@Transient
	private Long idCatSpeed;
	@Transient
	private Long idCatClassic;
	@Transient
	private Long idCatBattle;
	@Transient
	private Long idCatJam;
	@Transient
	private Long idCatDerrapes;
	@Transient
	private Long idCatSalto;
	@Transient
	private Long idPareja; 				// Id de la pareja de Jam
	
	public enum GenderEnum{ 
		MALE("Masculino"),
		FEMALE("Femenino"),
		MIXTO("Mixto");
		private final String type;
		GenderEnum(String t){ type = t; }
		public String toString(){ return type; } 
	}
	
    /**
     * Default constructor. 
     */
	public PatinadorEntity(){
    	// Void with no-args as requested for non-enhanced JPA entities
    	//id = 0;
    	timestamp = new Date();
    	nombre = "";
    	apellidos = "";
    	active = true;
    	created = new Date();
    	email = "";
    	fechaNacimiento = new Date();
    	genero = GenderEnum.FEMALE;
    	dni = "";
    	club = null;
    	fichaFederativa = "";
    	speed = false;
    	classic = false;
    	battle = false;
    	jam = false;
    	derrapes = false;
    	salto = false;
    	dorsal = null;	
    	catSpeed = "";
    	catClassic = "";
    	catBattle = "";
    	catJam = "";
    	catDerrapes = "";
    	catSalto = "";
    	nombrePareja = "";
    	apellidosPareja = "";
    	dorsalPareja = null;
    	//company = null;
    }
    
    public String toString()
    {
    	ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        try {
            String json = mapper.writeValueAsString(this);
            return json;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "JsonError";
    }
    
    @Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (this.id == null) {
			return false;
		}

		if (obj instanceof PatinadorEntity && obj.getClass().equals(getClass())) {
			return this.id.equals(((PatinadorEntity) obj).id);
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
	public PatinadorEntity clone() throws CloneNotSupportedException {
		return (PatinadorEntity) super.clone();
	}
    
    public void setId(long id)
    {
    	this.id=id;
    }
    
    public Long getId()
    {
    	return id;
    }
    
    public void setTimestamp(Date ts)
    {
    	timestamp = ts;
    }
    
    public Date getTimestamp()
    {
    	return timestamp;
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

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public Date getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(Date fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public Long getClub() {
		return club;
	}

	public void setClub(Long club) {
		this.club = club;
	}

	public String getFichaFederativa() {
		return fichaFederativa;
	}

	public void setFichaFederativa(String fichaFederativa) {
		this.fichaFederativa = fichaFederativa;
	}

	public GenderEnum getGenero() {
		return genero;
	}

	public void setGenero(GenderEnum genero) {
		this.genero = genero;
	}

	public String getClubStr() {
		return clubStr;
	}

	public void setClubStr(String clubStr) {
		this.clubStr = clubStr;
	}
    
	public boolean getSpeed() {
		return speed;
	}

	public void setSpeed(boolean speed) {
		this.speed = speed;
	}

	public boolean getClassic() {
		return classic;
	}

	public void setClassic(boolean classic) {
		this.classic = classic;
	}

	public boolean getBattle() {
		return battle;
	}

	public void setBattle(boolean battle) {
		this.battle = battle;
	}

	public boolean getJam() {
		return jam;
	}

	public void setJam(boolean jam) {
		this.jam = jam;
	}

	public boolean getDerrapes() {
		return derrapes;
	}

	public void setDerrapes(boolean derrapes) {
		this.derrapes = derrapes;
	}

	public boolean getSalto() {
		return salto;
	}

	public void setSalto(boolean salto) {
		this.salto = salto;
	}
	
	public Integer getDorsal() {
		return dorsal;
	}

	public void setDorsal(Integer dorsal) {
		this.dorsal = dorsal;
	}

	public String getCatSpeed() {
		return catSpeed;
	}

	public void setCatSpeed(String catSpeed) {
		this.catSpeed = catSpeed;
	}

	public String getCatClassic() {
		return catClassic;
	}

	public void setCatClassic(String catClassic) {
		this.catClassic = catClassic;
	}

	public String getCatBattle() {
		return catBattle;
	}

	public void setCatBattle(String catBattle) {
		this.catBattle = catBattle;
	}

	public String getCatJam() {
		return catJam;
	}

	public void setCatJam(String catJam) {
		this.catJam = catJam;
	}

	public String getCatDerrapes() {
		return catDerrapes;
	}

	public void setCatDerrapes(String catDerrapes) {
		this.catDerrapes = catDerrapes;
	}

	public String getCatSalto() {
		return catSalto;
	}

	public void setCatSalto(String catSalto) {
		this.catSalto = catSalto;
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

	public Long getIdPareja() {
		return idPareja;
	}

	public void setIdPareja(Long patinadorPareja) {
		this.idPareja = patinadorPareja;
	}

	public Long getIdCatSpeed() {
		return idCatSpeed;
	}

	public void setIdCatSpeed(Long idCatSpeed) {
		this.idCatSpeed = idCatSpeed;
	}

	public Long getIdCatClassic() {
		return idCatClassic;
	}

	public void setIdCatClassic(Long idCatClassic) {
		this.idCatClassic = idCatClassic;
	}

	public Long getIdCatBattle() {
		return idCatBattle;
	}

	public void setIdCatBattle(Long idCatBattle) {
		this.idCatBattle = idCatBattle;
	}

	public Long getIdCatJam() {
		return idCatJam;
	}

	public void setIdCatJam(Long idCatJam) {
		this.idCatJam = idCatJam;
	}

	public Long getIdCatDerrapes() {
		return idCatDerrapes;
	}

	public void setIdCatDerrapes(Long idCatDerrapes) {
		this.idCatDerrapes = idCatDerrapes;
	}

	public Long getIdCatSalto() {
		return idCatSalto;
	}

	public void setIdCatSalto(Long idCatSalto) {
		this.idCatSalto = idCatSalto;
	}
    
}
