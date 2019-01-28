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

public class ExportToXls {
	
	private static ExportToXls INSTANCE;
	private Logger log = Logger.getLogger(ExportToXls.class);
	
	public ExportToXls(){
		// Constructor
		try {
			// Initialization
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public synchronized static ExportToXls get() {
        if (INSTANCE == null) {
            INSTANCE = new ExportToXls();
        }
        return INSTANCE;
    }
	
	public <T> File createXLS(List<?> collection, Class<T> entity, String idfield, String... fields) {
    	// Export to Excel
    	try{
    		// Create first a temporal file
    		File tempFile = File.createTempFile(entity.getSimpleName(), ".xlsx");
    		
    		// Creates the Excel Document and first sheet
    		ApoiXlsExport doc = new ApoiXlsExport(tempFile, null);
    		doc.setHeader(entity.getSimpleName());
    		
    		// Builds and array with idfield and fields
    		String allfields[] = new String[fields.length+1];
    		allfields[0] = idfield;
    		for(int i = 1; i<fields.length+1; i++){
    			allfields[i] = fields[i-1];
    		}
    		
    		// Set the headings
    		doc.addRow(0, allfields, true, true);
    		
    		// Process the List
    		int j = 1;
    		for(Object rec : collection){
    			Object data[] = new Object[allfields.length];
    			for(int i = 0; i<allfields.length; i++){
	    			String getMethod = "get"+ allfields[i].substring(0, 1).toUpperCase()+allfields[i].substring(1);
	    			//System.out.println("METODO="+getMethod);
	    	    	Method method = entity.getMethod(getMethod);
	    			Object res = method.invoke(rec);
	    			data[i] = res;
	    			// convertir en int o long si el objeto es una Clase o un enumerado
	    			Class<?> result = method.getReturnType();
	    			//System.out.println("Return type="+result.getSimpleName());
	    			if(res==null){
	    				data[i] = 0;
	    			}
	    			else if(result.isEnum()){
	    				data[i] = ((Enum<?>)res).ordinal();
	    			}
	    			else{
		    			Method[] methods = result.getMethods();
		    			for(Method m:methods){
		    				System.out.println("Method="+m.getName());
		    				if(m.getName().startsWith("get") && m.getName().endsWith("Id")){
		    					data[i] = m.invoke(res);
		    					break;
		    				}
		    			}
	    			}
	    			
    			}
    		   	doc.addRow(j, data, false, false);
    		   	j++;
    		}
    		// Close the Word Document
    		int widths[] = new int[allfields.length];
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
