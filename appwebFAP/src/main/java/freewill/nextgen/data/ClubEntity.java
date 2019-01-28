package freewill.nextgen.data;

import java.io.Serializable;

/**
 * Session Bean implementation class PatinadorEntity
 */
@SuppressWarnings("serial")
public class ClubEntity implements Serializable, Cloneable {
	
    private Long id;
    private String nombre;
    private String direccion;
    private String localidad;
    private String provincia;
    private String coordinador;
    private String email;
    private String telefono;
    private Long company;
    private byte[] image;			// Company Logo
	private String imagename;		// Name of file with Company Logo
    
    /**
     * Default constructor. 
     */
	public ClubEntity(){
		// Void with no-args as requested for non-enhanced JPA entities
    	//id = 0;
    	nombre = "";
    	direccion = "";
    	localidad = "";
    	provincia = "";
    	coordinador = "";
    	email = "";
    	telefono = "";
    	image = new byte[0];
    	// company = null;
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

		if (obj instanceof ClubEntity && obj.getClass().equals(getClass())) {
			return this.id.equals(((ClubEntity) obj).id);
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
	public ClubEntity clone() throws CloneNotSupportedException {
		return (ClubEntity) super.clone();
	}
    
    public String getNombre() {
        return this.nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getLocalidad() {
        return this.localidad;
    }
    
    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }
    
    public String getProvincia() {
        return this.provincia;
    }
    
    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
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

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	public String getImagename() {
		return imagename;
	}

	public void setImagename(String imagename) {
		this.imagename = imagename;
	}
    
}
