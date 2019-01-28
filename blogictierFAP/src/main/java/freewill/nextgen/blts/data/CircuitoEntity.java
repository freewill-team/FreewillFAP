package freewill.nextgen.blts.data;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import freewill.nextgen.blts.BltAuditingEntityListener;

/**
 * Session Bean implementation class CircuitoEntity
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "CIRCUITOENTITY")
@EntityListeners(BltAuditingEntityListener.class)
public class CircuitoEntity implements Serializable, Cloneable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;				// ID
	private String nombre;			// Circuito Name
	private int temporada;			// Año en el que se curca este circuito
	private Long company;			// Company the project belongs to
	
    /**
     * Default constructor. 
     */
	@SuppressWarnings("deprecation")
	public CircuitoEntity(){
    	// Void with no-args as requested for non-enhanced JPA entities
    	//id = 0;
		nombre = "";
		setTemporada(new Date().getYear());
    	//company = null;
    }
    
    public String toString()
    {
    	return  id + ","+
    			nombre + "," +
    			temporada;
    }
    
    @Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (this.id == null) {
			return false;
		}

		if (obj instanceof CircuitoEntity && obj.getClass().equals(getClass())) {
			return this.id.equals(((CircuitoEntity) obj).id);
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 42 * hash + (id == null ? 0 : id.hashCode());
		return hash;
	}

	@Override
	public CircuitoEntity clone() throws CloneNotSupportedException {
		return (CircuitoEntity) super.clone();
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

	public int getTemporada() {
		return temporada;
	}

	public void setTemporada(int temporada) {
		this.temporada = temporada;
	}
	
}
