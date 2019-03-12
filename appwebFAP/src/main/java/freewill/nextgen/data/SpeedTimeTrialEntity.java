package freewill.nextgen.data;

import java.io.Serializable;

/**
 * Session Bean implementation class SpeedTimeTrial
 */
@SuppressWarnings("serial")
public class SpeedTimeTrialEntity implements Serializable, Cloneable {
	
	private Long id;				// ID
	private String nombre;			// Nombre del Patinador
    private String apellidos;		// Apellidos
	private Long company;			// Company the record belongs to
	private Long patinador;
	private Long competicion;
	private Long categoria;
	private Integer dorsal;
	private int orden1;				// Puesto de salida del patinador 1a ronda
	private int tiempo1A;
	private int tiempo1B;
	private int conos1;			
	private int tiempoAjustado1;
	private boolean valido1;
	private int orden2;				// Puesto de salida del patinador 2a ronda
	private int tiempo2A;
	private int tiempo2B;
	private int conos2;			
	private int tiempoAjustado2;
	private boolean valido2;
	private int mejorTiempo;		// en millisegundos
	private int clasificacion;		// Puesto en el que quedó el patinador en timetrial
	private int clasificacionFinal;	// Puesto en el que quedó el patinador en resultado final
	
	public enum RondaEnum{
		PRIMERA("Primera Ronda"),
		SEGUNDA("Segunda Ronda"),
		RESULTADOS("Resultados Time Trial"),
		KOSYSTEM("KO System");
		private final String type;
		RondaEnum(String t){ type = t; }
		public String toString(){ return type; }
	}
	
    /**
     * Default constructor. 
     */
	public SpeedTimeTrialEntity(){
    	// Void with no-args as requested for non-enhanced JPA entities
    	//id = 0;
    	nombre = "";
    	apellidos = "";
    	orden1 = 0;
    	orden2 = 0;
    	clasificacion = 0;
    	clasificacionFinal = 0;
    	//company = null;
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

		if (obj instanceof SpeedTimeTrialEntity && obj.getClass().equals(getClass())) {
			return this.id.equals(((SpeedTimeTrialEntity) obj).id);
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
	public SpeedTimeTrialEntity clone() throws CloneNotSupportedException {
		return (SpeedTimeTrialEntity) super.clone();
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

	public int getClasificacion() {
		return clasificacion;
	}

	public void setClasificacion(int clasificacion) {
		this.clasificacion = clasificacion;
	}

	public int getTiempo1A() {
		return tiempo1A;
	}

	public void setTiempo1A(int tiempo1a) {
		tiempo1A = tiempo1a;
	}

	public int getTiempo1B() {
		return tiempo1B;
	}

	public void setTiempo1B(int tiempo1b) {
		tiempo1B = tiempo1b;
	}

	public int getConos1() {
		return conos1;
	}

	public void setConos1(int conos1) {
		this.conos1 = conos1;
	}

	public int getTiempoAjustado1() {
		return tiempoAjustado1;
	}

	public void setTiempoAjustado1(int tiempoAjustado1) {
		this.tiempoAjustado1 = tiempoAjustado1;
	}

	public int getTiempo2A() {
		return tiempo2A;
	}

	public void setTiempo2A(int tiempo2a) {
		tiempo2A = tiempo2a;
	}

	public int getTiempo2B() {
		return tiempo2B;
	}

	public void setTiempo2B(int tiempo2b) {
		tiempo2B = tiempo2b;
	}

	public int getConos2() {
		return conos2;
	}

	public void setConos2(int conos2) {
		this.conos2 = conos2;
	}

	public int getTiempoAjustado2() {
		return tiempoAjustado2;
	}

	public void setTiempoAjustado2(int tiempoAjustado2) {
		this.tiempoAjustado2 = tiempoAjustado2;
	}

	public int getMejorTiempo() {
		return mejorTiempo;
	}

	public void setMejorTiempo(int mejorTiempo) {
		this.mejorTiempo = mejorTiempo;
	}

	public boolean getValido1() {
		return valido1;
	}

	public void setValido1(boolean valido1) {
		this.valido1 = valido1;
	}

	public boolean getValido2() {
		return valido2;
	}

	public void setValido2(boolean valido2) {
		this.valido2 = valido2;
	}

	/**
	 * @return the orden2
	 */
	public int getOrden2() {
		return orden2;
	}

	/**
	 * @param orden2 the orden2 to set
	 */
	public void setOrden2(int orden2) {
		this.orden2 = orden2;
	}

	public int getClasificacionFinal() {
		return clasificacionFinal;
	}

	public void setClasificacionFinal(int clasificacionFinal) {
		this.clasificacionFinal = clasificacionFinal;
	}

	public Integer getDorsal() {
		return dorsal;
	}

	public void setDorsal(Integer dorsal) {
		this.dorsal = dorsal;
	}
    
	public String getFullName(){
		return id+":"+dorsal+":"+nombre +" "+ apellidos;
	}
	
}
