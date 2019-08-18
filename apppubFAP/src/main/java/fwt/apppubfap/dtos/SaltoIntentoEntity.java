package fwt.apppubfap.dtos;

import java.io.Serializable;

/**
 * Session Bean implementation class SpeedTimeTrial
 */
@SuppressWarnings("serial")
public class SaltoIntentoEntity implements Serializable, Cloneable {
	
	private Long id;				// ID
	private Long company;			// Company the record belongs to
	private Long saltoPatinadorId;	// Parent SaltoPatinadorEntity
	private int ronda;
	private ResultEnum salto1;
	private ResultEnum salto2;
	private ResultEnum salto3;
	private int altura;
	
	public enum ResultEnum{
		PENDIENTE(""),
		PASA("Pasa"),
		OK("Ok"),
		FALLO("Fallo");
		private final String type;
		ResultEnum(String t){ type = t; }
		public String toString(){ return type; }
	}
	
    /**
     * Default constructor. 
     */
	public SaltoIntentoEntity(){
    	// Void with no-args as requested for non-enhanced JPA entities
    	//id = 0;
    	altura = 0;
    	salto1 = ResultEnum.PENDIENTE;
    	salto2 = ResultEnum.PENDIENTE;
    	salto3 = ResultEnum.PENDIENTE;
    	ronda = 0;
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

		if (obj instanceof SaltoIntentoEntity && obj.getClass().equals(getClass())) {
			return this.id.equals(((SaltoIntentoEntity) obj).id);
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
	public SaltoIntentoEntity clone() throws CloneNotSupportedException {
		return (SaltoIntentoEntity) super.clone();
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

	public Long getSaltoPatinadorId() {
		return saltoPatinadorId;
	}

	public void setSaltoPatinadorId(Long saltoPatinadorId) {
		this.saltoPatinadorId = saltoPatinadorId;
	}

	public ResultEnum getSalto1() {
		return salto1;
	}

	public void setSalto1(ResultEnum salto1) {
		this.salto1 = salto1;
	}

	public ResultEnum getSalto2() {
		return salto2;
	}

	public void setSalto2(ResultEnum salto2) {
		this.salto2 = salto2;
	}

	public ResultEnum getSalto3() {
		return salto3;
	}

	public void setSalto3(ResultEnum salto3) {
		this.salto3 = salto3;
	}

	public int getAltura() {
		return altura;
	}

	public void setAltura(int altura) {
		this.altura = altura;
	}

	public int getRonda() {
		return ronda;
	}

	public void setRonda(int ronda) {
		this.ronda = ronda;
	}
	
}
