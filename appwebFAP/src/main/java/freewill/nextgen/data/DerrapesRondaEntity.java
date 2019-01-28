package freewill.nextgen.data;

import java.io.Serializable;

/**
 * Session Bean implementation class SpeedKOSystem
 */
@SuppressWarnings("serial")
public class DerrapesRondaEntity implements Serializable, Cloneable {
	
	private Long id;				// ID
	private String nombre1;			// Nombre del Patinador 1
    private String apellidos1;		// Apellidos 1
    private String nombre2;			// Nombre del Patinador 2
    private String apellidos2;		// Apellidos 2
    private String nombre3;			// Nombre del Patinador 3
    private String apellidos3;		// Apellidos 3
    private String nombre4;			// Nombre del Patinador 4
    private String apellidos4;		// Apellidos 4
	private Long company;			// Company the record belongs to
	private int grupo;				// Para identificar emparejamientos sucesivos
	private Long patinador1;
	private Long patinador2;
	private Long patinador3;
	private Long patinador4;
	private Long competicion;
	private Long categoria;
	private EliminatoriaEnum eliminatoria;
	private Long ganador1;
	private String ganadorStr1;
	private Long ganador2;
	private String ganadorStr2;
	private Long ganador3;
	private String ganadorStr3;
	private Long ganador4;
	private String ganadorStr4;
	
	public enum EliminatoriaEnum{
		FINAL("Final"),
		SEMIS("Semifinal"),
		CUARTOS("Cuartos");
		private final String type;
		EliminatoriaEnum(String t){ type = t; }
		public String toString(){ return type; }
	}
	
    /**
     * Default constructor. 
     */
	public DerrapesRondaEntity(){
    	// Void with no-args as requested for non-enhanced JPA entities
    	//id = 0;
    	nombre1 = "";
    	apellidos1 = "";
    	nombre2 = "";
    	apellidos2 = "";
    	nombre3 = "";
    	apellidos3 = "";
    	nombre4 = "";
    	apellidos4 = "";
    	eliminatoria = EliminatoriaEnum.SEMIS;
    	ganadorStr1 = "";
    	ganadorStr2 = "";
    	ganadorStr3 = "";
    	ganadorStr4 = "";
    	//company = null;
    }
    
    public String toString()
    {
    	return  id + ","+
    			nombre1 + " " + apellidos1 +
    			"," + nombre2 + " " + apellidos2 +
    			"," + nombre3 + " " + apellidos3 +
    			"," + nombre4 + " " + apellidos4 +
    			categoria;
    }
    
    @Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (this.id == null) {
			return false;
		}

		if (obj instanceof DerrapesRondaEntity && obj.getClass().equals(getClass())) {
			return this.id.equals(((DerrapesRondaEntity) obj).id);
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
	public DerrapesRondaEntity clone() throws CloneNotSupportedException {
		return (DerrapesRondaEntity) super.clone();
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

	public Long getPatinador1() {
		return patinador1;
	}

	public void setPatinador1(Long patinador) {
		this.patinador1 = patinador;
	}

	public Long getCompeticion() {
		return competicion;
	}

	public void setCompeticion(Long competicion) {
		this.competicion = competicion;
	}

	public Long getCategoria() {
		return categoria;
	}

	public void setCategoria(Long categoria) {
		this.categoria = categoria;
	}

	public String getNombre2() {
		return nombre2;
	}

	public void setNombre2(String nombre2) {
		this.nombre2 = nombre2;
	}

	public String getApellidos2() {
		return apellidos2;
	}

	public void setApellidos2(String apellidos2) {
		this.apellidos2 = apellidos2;
	}

	public Long getPatinador2() {
		return patinador2;
	}

	public void setPatinador2(Long patinador2) {
		this.patinador2 = patinador2;
	}

	public EliminatoriaEnum getEliminatoria() {
		return eliminatoria;
	}

	public void setEliminatoria(EliminatoriaEnum eliminatoria) {
		this.eliminatoria = eliminatoria;
	}

	public int getGrupo() {
		return grupo;
	}

	public void setGrupo(int grupo) {
		this.grupo = grupo;
	}

	public Long getGanador1() {
		return ganador1;
	}

	public void setGanador1(Long ganador) {
		this.ganador1 = ganador;
	}

	public String getNombre3() {
		return nombre3;
	}

	public void setNombre3(String nombre3) {
		this.nombre3 = nombre3;
	}

	public String getApellidos3() {
		return apellidos3;
	}

	public void setApellidos3(String apellidos3) {
		this.apellidos3 = apellidos3;
	}

	public String getNombre4() {
		return nombre4;
	}

	public void setNombre4(String nombre4) {
		this.nombre4 = nombre4;
	}

	public String getApellidos4() {
		return apellidos4;
	}

	public void setApellidos4(String apellidos4) {
		this.apellidos4 = apellidos4;
	}

	public Long getPatinador3() {
		return patinador3;
	}

	public void setPatinador3(Long patinador3) {
		this.patinador3 = patinador3;
	}

	public Long getPatinador4() {
		return patinador4;
	}

	public void setPatinador4(Long patinador4) {
		this.patinador4 = patinador4;
	}

	public Long getGanador2() {
		return ganador2;
	}

	public void setGanador2(Long ganador2) {
		this.ganador2 = ganador2;
	}

	public String getGanadorStr2() {
		return ganadorStr2;
	}

	public void setGanadorStr2(String ganadorStr2) {
		this.ganadorStr2 = ganadorStr2;
	}

	public String getGanadorStr1() {
		return ganadorStr1;
	}

	public void setGanadorStr1(String ganadorStr1) {
		this.ganadorStr1 = ganadorStr1;
	}

	public Long getGanador3() {
		return ganador3;
	}

	public void setGanador3(Long ganador3) {
		this.ganador3 = ganador3;
	}

	public String getGanadorStr3() {
		return ganadorStr3;
	}

	public void setGanadorStr3(String ganadorStr3) {
		this.ganadorStr3 = ganadorStr3;
	}

	public Long getGanador4() {
		return ganador4;
	}

	public void setGanador4(Long ganador4) {
		this.ganador4 = ganador4;
	}

	public String getGanadorStr4() {
		return ganadorStr4;
	}

	public void setGanadorStr4(String ganadorStr4) {
		this.ganadorStr4 = ganadorStr4;
	}
    	
}
