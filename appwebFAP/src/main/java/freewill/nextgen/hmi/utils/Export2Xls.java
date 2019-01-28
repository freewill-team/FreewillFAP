package freewill.nextgen.hmi.utils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

import org.apache.log4j.Logger;

/*
 * This Export2Xls class enables export of lists to Excel format.
 * As it follows the singleton model, it will be used for all the web sessions.
 * 
 */

public class Export2Xls {
	
	private static Export2Xls INSTANCE;
	private Logger log = Logger.getLogger(Export2Xls.class);
	
	public Export2Xls(){
		// Constructor
		try {
			// Initialization
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public synchronized static Export2Xls get() {
        if (INSTANCE == null) {
            INSTANCE = new Export2Xls();
        }
        return INSTANCE;
    }
	
	public <T> File createXLS(List<T> collection, 
			Class<T> entity, String title, String... fields) {
    	// Export to Excel
    	try{
    		// Create first a temporal file
    		File tempFile = File.createTempFile(title.replace("/", "-"), ".xlsx");
    		
    		// Creates the Excel Document and first sheet
    		ApoiXlsExport doc = new ApoiXlsExport(tempFile, null);
    		doc.setHeader(title); //entity.getSimpleName());
    		int fila = 0;
    		
    		// Set the Title
    		Object data[] = new Object[fields.length];
    		for(int i = 0; i<fields.length; i++){
    			data[i] = "";
    		}
    		/*data[1] = title;
    		doc.addRow(fila++, data, false, true);
    		data[1] = "";
    		doc.addRow(fila++, data, false, false);*/
    		
    		// Set the headings
    		for(int i = 0; i<fields.length; i++){
    			data[i] = Messages.get().getKey(fields[i]).toUpperCase();
    		}
    		doc.addRow(fila++, data, true, true);
    		
    		// Process the data List
    		for(T rec : collection){
    			for(int i = 0; i<fields.length; i++){
	    			String getMethod = "get"+ fields[i].substring(0, 1).toUpperCase()+fields[i].substring(1);
	    	    	Method method = entity.getMethod(getMethod);
	    			data[i] = method.invoke(rec);
    			}
    		   	doc.addRow(fila++, data, false, false);
    		}
    		// Close the Word Document
    		int widths[] = new int[fields.length];
    		for(int i = 0; i<widths.length; i++){
    			widths[i] = -1;
    		}
    		doc.CloseDocument(widths);
    		// Downloads the file
    		// FileResource resource = new FileResource(tempFile);
    		// Page.getCurrent().open(resource, "Crews Export File", false);
    		// tempFile.delete();
    		// The function who call this method
    		// is responsible for removing the temporal file
    		return tempFile;
    	}
    	catch(Exception e){
			log.error(e.getMessage());
			log.error(e.getStackTrace());
			e.printStackTrace();
    	}
    	return null;
	}
  	
}