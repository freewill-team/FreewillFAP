package freewill.nextgen.data;

import java.io.Serializable;

/**
 * Session Bean implementation class SpeedKOSystem
 */
@SuppressWarnings("serial")
public class SpeedKOSystemEntity implements Serializable, Cloneable {
	
	private Long id;				// ID
	private String nombre1;			// Nombre del Patinador 1
    private String apellidos1;		// Apellidos 1
    private String nombre2;			// Nombre del Patinador 2
    private String apellidos2;		// Apellidos 2
	private Long company;			// Company the record belongs to
	private int grupo;				// Para identificar emparejamientos sucesivos
	private Long patinador1;
	private Long patinador2;
	private Long competicion;
	private Long categoria;
	private EliminatoriaEnum eliminatoria;
	private int pat1tiempo1;		
	private int pat1tiempo2;		
	private int pat1tiempo3;	
	private int pat2tiempo1;		
	private int pat2tiempo2;		
	private int pat2tiempo3;
	private boolean pat1gana1;
	private boolean pat1gana2;
	private boolean pat1gana3;
	private boolean pat2gana1;
	private boolean pat2gana2;
	private boolean pat2gana3;
	private Long ganador;
	private String ganadorStr;
	
	public enum EliminatoriaEnum{
		FINAL("Final"),
		SEMIS("Semifinal"),
		CUARTOS("Cuartos"),
		OCTAVOS("Octavos"),
		DIECISEIS("Dieciseisavos");
		private final String type;
		EliminatoriaEnum(String t){ type = t; }
		public String toString(){ return type; }
	}
	
    /**
     * Default constructor. 
     */
	public SpeedKOSystemEntity(){
    	// Void with no-args as requested for non-enhanced JPA entities
    	//id = 0;
    	nombre1 = "";
    	apellidos1 = "";
    	nombre2 = "";
    	apellidos2 = "";
    	eliminatoria = EliminatoriaEnum.OCTAVOS;
    	ganadorStr = "";
    	grupo = 0;
    	pat1tiempo1 = 0;		
    	pat1tiempo2 = 0;		
    	pat1tiempo3 = 0;
    	pat2tiempo1 = 0;	
    	pat2tiempo2 = 0;	
    	pat2tiempo3 = 0;
    	pat1gana1 = false;
    	pat1gana2 = false;
    	pat1gana3 = false;
    	pat2gana1 = false;
    	pat2gana2 = false;
    	pat2gana3 = false;
    	//company = null;
    }
    
    public String toString()
    {
    	return  id + ","+
    			nombre1 + " " + apellidos1 +
    			"," + nombre2 + " " + apellidos2 +
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

		if (obj instanceof SpeedKOSystemEntity && obj.getClass().equals(getClass())) {
			return this.id.equals(((SpeedKOSystemEntity) obj).id);
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
	public SpeedKOSystemEntity clone() throws CloneNotSupportedException {
		return (SpeedKOSystemEntity) super.clone();
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

	public Long getGanador() {
		return ganador;
	}

	public void setGanador(Long ganador) {
		this.ganador = ganador;
	}

	public String getGanadorStr() {
		return ganadorStr;
	}

	public void setGanadorStr(String ganadorStr) {
		this.ganadorStr = ganadorStr;
	}

	public boolean getPat1gana1() {
		return pat1gana1;
	}

	public void setPat1gana1(boolean pat1gana1) {
		this.pat1gana1 = pat1gana1;
	}

	public boolean getPat1gana2() {
		return pat1gana2;
	}

	public void setPat1gana2(boolean pat1gana2) {
		this.pat1gana2 = pat1gana2;
	}

	public boolean getPat1gana3() {
		return pat1gana3;
	}

	public void setPat1gana3(boolean pat1gana3) {
		this.pat1gana3 = pat1gana3;
	}

	public boolean getPat2gana1() {
		return pat2gana1;
	}

	public void setPat2gana1(boolean pat2gana1) {
		this.pat2gana1 = pat2gana1;
	}

	public boolean getPat2gana2() {
		return pat2gana2;
	}

	public void setPat2gana2(boolean pat2gana2) {
		this.pat2gana2 = pat2gana2;
	}

	public boolean getPat2gana3() {
		return pat2gana3;
	}

	public void setPat2gana3(boolean pat2gana3) {
		this.pat2gana3 = pat2gana3;
	}

	public int getPat1tiempo1() {
		return pat1tiempo1;
	}

	public void setPat1tiempo1(int pat1tiempo1) {
		this.pat1tiempo1 = pat1tiempo1;
	}

	public int getPat1tiempo2() {
		return pat1tiempo2;
	}

	public void setPat1tiempo2(int pat1tiempo2) {
		this.pat1tiempo2 = pat1tiempo2;
	}

	public int getPat1tiempo3() {
		return pat1tiempo3;
	}

	public void setPat1tiempo3(int pat1tiempo3) {
		this.pat1tiempo3 = pat1tiempo3;
	}

	public int getPat2tiempo1() {
		return pat2tiempo1;
	}

	public void setPat2tiempo1(int pat2tiempo1) {
		this.pat2tiempo1 = pat2tiempo1;
	}

	public int getPat2tiempo2() {
		return pat2tiempo2;
	}

	public void setPat2tiempo2(int pat2tiempo2) {
		this.pat2tiempo2 = pat2tiempo2;
	}

	public int getPat2tiempo3() {
		return pat2tiempo3;
	}

	public void setPat2tiempo3(int pat2tiempo3) {
		this.pat2tiempo3 = pat2tiempo3;
	}
    	
}
