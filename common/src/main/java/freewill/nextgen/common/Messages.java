package freewill.nextgen.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Properties;

/*
 * This Messages class enables internationalization.
 * As it follows the singleton model, it will be used for all the apps in the same JVM.
 * 
 */

@SuppressWarnings("serial")
public class Messages implements Serializable {
	
	private static final String BASE_NAME = "/properties/i18n_en.properties"; // Default Props file
	private static final String BASE = "/properties/i18n_";
	private static final String NAME = ".properties";
	private static final String defaultlocale = "en"; // Default Locale
	private static Messages INSTANCE;
	private static HashMap<String,Properties> localeList = 
			new HashMap<String,Properties>(); // Contains a HashMap with the different Locale files
	
	private Messages(){
		// Constructor
		// Set default property file / default locale is "en"
		try {
			Properties props = new Properties();
			// Este codigo lee el fichero de un directorio dentro del jar
			props.load(this.getClass().getResourceAsStream(BASE_NAME));
			localeList.put(defaultlocale, props);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public synchronized static Messages get() {
        if (INSTANCE == null) {
            INSTANCE = new Messages();
        }
        return INSTANCE;
    }
	
	private Properties addLocale(String locale) {
		// Add new property file to the HashMap according to the new locale
		String propfile = "";
	    try {
	    	Properties props = new Properties();
	    	propfile = BASE + locale + NAME;
			props.load(this.getClass().getResourceAsStream(propfile));
			localeList.put(locale, props);
			return props;
		} 
		catch (Exception e) {
			//e.printStackTrace();
			System.out.println("Property file "+propfile+" not Found. Default will be used");
		}
	    // Use default property file instead
	    return localeList.get(defaultlocale);
	}
	
	public final String getKey(final String key, String locale) {
	    try {
	    	// First verifies whether a localeFile has been loaded previously
	    	Properties props = localeList.get(locale);
	    	if( props==null ){
	    		// Load a new Locale file to the HashMap
	    		props = addLocale(locale);
	    	}
	    	
	    	String value = props.getProperty(key);
	        return new String(value); //.getBytes("ISO-8859-1"), "UTF-8");
	    }
	    catch (Exception e) {
	    	//e.printStackTrace();
	        return '!' + key + '!';
	    }
	}
	
}
