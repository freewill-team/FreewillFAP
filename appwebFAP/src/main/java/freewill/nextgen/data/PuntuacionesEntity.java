package freewill.nextgen.data;

import java.io.Serializable;

/**
 * Session Bean implementation class PuntuacionesEntity
 */
@SuppressWarnings("serial")
public class PuntuacionesEntity implements Serializable, Cloneable {
	
	private Long id;				// ID
	private Long company;			// Company the project belongs to
	private int clasificacion;
	private int puntosCampeonato;
	private int puntosCopa;
	private int puntosTrofeo;
	
    /**
     * Default constructor. 
     */
	public PuntuacionesEntity(){
    	// Void with no-args as requested for non-enhanced JPA entities
    	//id = 0;
		clasificacion = 0;
		puntosCampeonato = 0;
		puntosCopa = 0;
		puntosTrofeo = 0;
    	//company = null;
    }
    
    public String toString()
    {
    	return  id + ","+
    			"";
    }
    
    @Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (this.id == null) {
			return false;
		}

		if (obj instanceof PuntuacionesEntity && obj.getClass().equals(getClass())) {
			return this.id.equals(((PuntuacionesEntity) obj).id);
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
	public PuntuacionesEntity clone() throws CloneNotSupportedException {
		return (PuntuacionesEntity) super.clone();
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

	public int getClasificacion() {
		return clasificacion;
	}

	public void setClasificacion(int clasificacion) {
		this.clasificacion = clasificacion;
	}

	public int getPuntosCampeonato() {
		return puntosCampeonato;
	}

	public void setPuntosCampeonato(int puntosCampeonato) {
		this.puntosCampeonato = puntosCampeonato;
	}

	public int getPuntosCopa() {
		return puntosCopa;
	}

	public void setPuntosCopa(int puntosCopa) {
		this.puntosCopa = puntosCopa;
	}

	public int getPuntosTrofeo() {
		return puntosTrofeo;
	}

	public void setPuntosTrofeo(int puntosTrofeo) {
		this.puntosTrofeo = puntosTrofeo;
	}
		
}
