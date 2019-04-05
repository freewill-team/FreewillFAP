package freewill.nextgen.data;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Session Bean implementation class InscripcionEntity
 */
@SuppressWarnings("serial")
public class InscripcionEntity implements Serializable, Cloneable {
	
    private Long id;
    private String coordinador = "";
    private Long club;
    private String clubStr;
    private String email = "";
    private String telefono = "";
    private Long competicion;
    private Long company;
    private boolean enviado = false;
    private Date fechaEnvio = new Date();
    
    /**
     * Default constructor. 
     */
	public InscripcionEntity(){
		// Void with no-args as requested for non-enhanced JPA entities
    }
	
	public String toString(){
		ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        try {
            String json = mapper.writeValueAsString(this);
            return json;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "JsonError";
	}
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getId() {
        return this.id;
    }
    
    @Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (this.id == null) {
			return false;
		}

		if (obj instanceof InscripcionEntity && obj.getClass().equals(getClass())) {
			return this.id.equals(((InscripcionEntity) obj).id);
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 43 * hash + (id == null ? 0 : id.hashCode());
		return hash;
	}

	@Override
	public InscripcionEntity clone() throws CloneNotSupportedException {
		return (InscripcionEntity) super.clone();
	}

	public String getCoordinador() {
		return coordinador;
	}

	public void setCoordinador(String coordinador) {
		this.coordinador = coordinador;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public Long getCompany() {
		return company;
	}

	public void setCompany(Long company) {
		this.company = company;
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

	public Long getCompeticion() {
		return competicion;
	}

	public void setCompeticion(Long competicion) {
		this.competicion = competicion;
	}

	public boolean getEnviado() {
		return enviado;
	}

	public void setEnviado(boolean enviado) {
		this.enviado = enviado;
	}

	public Date getFechaEnvio() {
		return fechaEnvio;
	}

	public void setFechaEnvio(Date fechaEnvio) {
		this.fechaEnvio = fechaEnvio;
	}
    
}
