package fwt.apppubfap.dtos;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class UsuarioDTO implements Serializable {

	private Long id;					// Id interno
	private Date date = new Date(0);	// Fecha de ultima modificación
	private boolean alumno = true;		// Indica si es alumno
	private String usuario = "";		// Persona a la que pertenece el crono
	private String email = "";			// Dirección de correo electrónico
	private Long idTenant;				// Id dueño de este registro
	
	public UsuarioDTO(){
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (getId() == null ? 0 : getId().hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final UsuarioDTO other = (UsuarioDTO) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}

	public boolean getAlumno() {
		return alumno;
	}

	public void setAlumno(boolean alumno) {
		this.alumno = alumno;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public Long getIdTenant() {
		return idTenant;
	}

	public void setIdTenant(Long idTenant) {
		this.idTenant = idTenant;
	}
	
}
