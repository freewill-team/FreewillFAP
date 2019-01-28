package freewill.nextgen.common;

/** 
 * File:   Utils.java
 * Date:   01/09/2017
 * Author: Benito Vela
 * Refs:   None
 * 
 * This class collect common functions and static variables (static strings).
 * It also includes the Alarm/Event Dictionary and Enums definitions.
 * 
 */

public class Utils {
	
	// Common static variables 
	public static String SYSTEMUSER = "SystemUser";	// System UserName
	public static String PROCESSES = "Processes";
	public static String SERVERS = "Servers";
	public static String SERVICES = "Services";
	public static String NOCONSOLE = "No-Console";
	public static String NOSYSTEM = "No-System";
	
	public enum ADMSservice{ 
		INFRA("Infrastructure"),
		RTS("Realtime"),
		HIS("Historical"),
		HMI("HMI");
		private final String type;
		ADMSservice(String t){ type = t; }
		public String toString(){ return type; }
	}
	
	// Common functions
	
	public static String findOSName()
	{
		String OS = System.getProperty("os.name");
		return OS;
	}

    public static String findHostName()
	{
        String hostname = "";
		String osname = findOSName().toLowerCase();

        if (osname.indexOf("win") >= 0) 
        {
            hostname = System.getenv("COMPUTERNAME");
        } 
        else if (osname.indexOf("nix") >= 0 || osname.indexOf("nux") >= 0) 
        {
            hostname = System.getenv("HOSTNAME");
        }
        else
        {
        	hostname = "Undetermine";
        }
        return hostname;
    }
	
    // Alarm/Event Dictionary and Enums
    
    public enum AlarmDic{
    	// Alarms/Events for Processes, Services, Servers
		ALM0001("ALM0001", SeverityEnum.HIGH, CategoryEnum.SYSTEM),
		ALM0002("ALM0002", SeverityEnum.NONE, CategoryEnum.USER),
		ALM0003("ALM0003", SeverityEnum.NONE, CategoryEnum.SYSTEM),
		ALM0004("ALM0004", SeverityEnum.NONE, CategoryEnum.SYSTEM),
		ALM0005("ALM0005", SeverityEnum.LOW,  CategoryEnum.USER),
		ALM0006("ALM0006", SeverityEnum.LOW,  CategoryEnum.USER),
		ALM0007("ALM0007", SeverityEnum.HIGH, CategoryEnum.SYSTEM),
		ALM0008("ALM0008", SeverityEnum.LOW,  CategoryEnum.USER),
		ALM0009("ALM0009", SeverityEnum.LOW,  CategoryEnum.USER),
		ALM0010("ALM0010", SeverityEnum.LOW,  CategoryEnum.USER),
		// Alarms/Events for Job Scheduler 
		ALM0011("ALM0011", SeverityEnum.LOW,  CategoryEnum.USER),
		ALM0012("ALM0012", SeverityEnum.HIGH, CategoryEnum.SYSTEM),
		// Alarms/Events for Users
		ALM0021("ALM0021", SeverityEnum.NONE, CategoryEnum.USER),
		ALM0022("ALM0022", SeverityEnum.HIGH, CategoryEnum.USER),
		ALM0023("ALM0023", SeverityEnum.NONE, CategoryEnum.USER),
		ALM0024("ALM0024", SeverityEnum.NONE, CategoryEnum.USER),
		// Events for Auditory
		ALM0031("ALM0031", SeverityEnum.LOW,  CategoryEnum.CONFIG),
		// Events for Mails
		ALM0041("ALM0041", SeverityEnum.MEDIUM, CategoryEnum.APPLICATION),
		// Alarms for telemetry/dpManager
		ALM0051("Return-To-Normal (%s)", 				SeverityEnum.NONE, 	CategoryEnum.TELEMETRY),
		ALM0052("Change to Normal state (%s)", 			SeverityEnum.NONE, 	CategoryEnum.TELEMETRY),
		ALM0053("Change to Abnormal state (%s)", 		SeverityEnum.MEDIUM, CategoryEnum.TELEMETRY),
		ALM0054("ROC %s limit violated (%s)", 			SeverityEnum.MEDIUM, CategoryEnum.TELEMETRY),
		ALM0055("LORAW %s limit violated (%s)", 		SeverityEnum.MEDIUM, CategoryEnum.TELEMETRY),
		ALM0056("HIRAW %s limit violated (%s)", 		SeverityEnum.MEDIUM, CategoryEnum.TELEMETRY),
		ALM0057("LO1 %s limit violated (%s)", 			SeverityEnum.MEDIUM, CategoryEnum.TELEMETRY),
		ALM0058("HI1 %s limit violated (%s)", 			SeverityEnum.MEDIUM, CategoryEnum.TELEMETRY),
		ALM0059("LO2 %s limit violated (%s)", 			SeverityEnum.MEDIUM, CategoryEnum.TELEMETRY),
		ALM0060("HI2 %s limit violated (%s)", 			SeverityEnum.MEDIUM, CategoryEnum.TELEMETRY),
		ALM0061("LO3 %s limit violated (%s)", 			SeverityEnum.MEDIUM, CategoryEnum.TELEMETRY),
		ALM0062("HI3 %s limit violated (%s)", 			SeverityEnum.MEDIUM, CategoryEnum.TELEMETRY),
		ALM0063("LO4 %s limit violated (%s)", 			SeverityEnum.MEDIUM, CategoryEnum.TELEMETRY),
		ALM0064("HI4 %s limit violated (%s)", 			SeverityEnum.MEDIUM, CategoryEnum.TELEMETRY),
		// Alarms/Events for SysMonitor
		ALM0071("ALM0071", SeverityEnum.HIGH, CategoryEnum.SYSTEM),
		ALM0072("ALM0072", SeverityEnum.HIGH, CategoryEnum.SYSTEM),
		ALM0073("ALM0073", SeverityEnum.HIGH, CategoryEnum.SYSTEM),
		// Dummy
		ALM9999("ALM9999", SeverityEnum.NONE, CategoryEnum.SYSTEM);
    	
		private final String message;
		private final SeverityEnum severity;
		private final CategoryEnum category;
		
		AlarmDic(String t, SeverityEnum sv, CategoryEnum ct){ 
			message = t; 
			severity = sv;
			category = ct;
		}
		
		public String toString(){ 
			return Messages.get().getKey(message, System.getProperty("LOCALE"));
		}
		
		public SeverityEnum getSeverity(){
			return severity;
		}
		
		public CategoryEnum getCategory(){
			return category;
		}
		
	}
    
    public enum SeverityEnum{ 
		NONE("None"),
		MINOR("Minor"),
		LOW("Low"), 
		MEDIUM("Medium"),
		HIGH("High"),
		MAJOR("Major"),
		CRITICAL("Critical");
		private final String type;
		SeverityEnum(String t){ type = t; }
		public String toString(){
			return Messages.get().getKey(type, System.getProperty("LOCALE"));
			//return type; 
			}
	}
	
	public enum CategoryEnum{ 
		NONE("None"),
		TELEMETRY("Telemetry"), 
		COMMS("Comms"),
		SYSTEM("System"),
		APPLICATION("Application"),
		CONFIG("Config-Audit"),
		USER("User-Action");
		private final String type;
		CategoryEnum(String t){ type = t; }
		public String toString(){ 
			return Messages.get().getKey(type, System.getProperty("LOCALE"));
			//return type; 
			}
	}
	
	public enum ServiceStatusEnum{ 
		GOOD("Good"),
		FAILED("Failed"),
		STARTING("Starting"),
		STOP("Stop");
		private final String type;
		ServiceStatusEnum(String t){ type = t; }
		public String toString(){
			return Messages.get().getKey(type, System.getProperty("LOCALE"));
			//return type; 
		} 
	}
	
	public enum LanguageEnum{ 
		ES("Spanish", "es"),
		EN("English", "en"),
		PT("Portuguese","pt"),
		FR("French","fr");
		private final String type;
		private final String locale;
		LanguageEnum(String t, String l){ type = t; locale = l;}
		public String toString(){
			return Messages.get().getKey(type, System.getProperty("LOCALE"));
			//return type; 
		}
		public String toLocale(){
			return locale; 
		}
	}
	
	public enum PointTypeEnum{
		TELEMETERED("Telemetered"), 
		MANUALENTRY("Manual Entry"), 
		CALCULATED("Calculated");
		private final String type;
		PointTypeEnum(String t){ type = t; }
		public String toString(){ return type; } 
	}
	
	// Enumerations for the Data Processing Manager
	
	public enum PointClassEnum{ 
		ANALOG("Analog"), 
		STATUS("Status"),
		MULTISTATE("Multistate"),
		ACCUM("Accum");
		private final String type;
		PointClassEnum(String t){ type = t; }
		public String toString(){ return type; } 
	}
	
	public enum FlagEnum{
		LO1("Lo1"), 
		HI1("Hi1"), 
		LO2("Lo2"), 
		HI2("Hi2"), 
		LO3("Lo3"), 
		HI3("Hi3"), 
		LO4("Lo4"), 
		HI4("Hi4"), 
		LORAW("LoRaw"), 
		HIRAW("HiRaw"), 
		ROC("Roc"), 
		ADC("Adc"),
		FLASH("Flashing"), 
		ABNORMAL("Abnormal"), 
		NOFRESH("NoFresh"), 
		OFFSCAN("Offscan"), 
		TAGEXISTS("Taged"), 
		ALMINH("AlmInh"), 
		EVTINH("EvtInh");
		private final String type;
		FlagEnum(String t){ type = t; }
		public String toString(){ return type; } 
	}
	
}
