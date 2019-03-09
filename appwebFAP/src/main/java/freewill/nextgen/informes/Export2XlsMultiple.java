package freewill.nextgen.informes;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.log4j.Logger;

import freewill.nextgen.hmi.utils.ApoiXlsExport;
import freewill.nextgen.hmi.utils.Messages;

/*
 * This Export2Xls class enables export of lists to Excel format.
 * As it follows the singleton model, it will be used for all the web sessions.
 * 
 */

public class Export2XlsMultiple {
	
	private static Export2XlsMultiple INSTANCE;
	private Logger log = Logger.getLogger(Export2XlsMultiple.class);
	
	public Export2XlsMultiple(){
		// Constructor
		try {
			// Initialization
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public synchronized static Export2XlsMultiple get() {
        if (INSTANCE == null) {
            INSTANCE = new Export2XlsMultiple();
        }
        return INSTANCE;
    }
	
	public <T> File createXLS(String file, List<T>[] collections, 
			Class<T> entity, String[] title, String... fields) {
    	// Export to Excel
    	try{
    		// Create first a temporal file
    		File tempFile = File.createTempFile(file.replace("/", "-")+" ", ".xlsx");
    		
    		// Creates the Excel Document and first sheet
    		ApoiXlsExport doc = new ApoiXlsExport(tempFile, null);
    		
    		int i = 0;
    		for(List<T> collection:collections){
    			if(collection!=null && collection.size()>0){
    				printSheet(file, title[i], collection, entity, doc, fields);
    			}
    			i++;
    		}
    		
    		doc.closeDocument();
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

	private <T> void printSheet(String file, String title, List<T> collection, Class<T> entity, ApoiXlsExport doc, String... fields)
			throws Exception {
		int fila = 0;
		
		doc.createSheet(title);
		doc.setHeader(file+" / "+title);
		doc.setLandScape(true);
		
		Object data[] = new Object[fields.length];
		// Set the headings
		for(int i = 0; i<fields.length; i++){
			data[i] = Messages.get().getKey(fields[i]).toUpperCase();
		}
		doc.addRow(fila++, data, true, true);
		
		// Process the data List
		for(T rec : collection){
			for(int i = 0; i<fields.length; i++){
				try{
					// Obtiene datos
					String getMethod = "get"+ fields[i].substring(0, 1).toUpperCase()+fields[i].substring(1);
					Method method = entity.getMethod(getMethod);
					data[i] = method.invoke(rec);
					// Aplica converters
					String paramType = data[i].getClass().getTypeName().toLowerCase();
					if((paramType.contains("integer") && fields[i].toLowerCase().contains("tiempo"))
							|| (paramType.equals("int") && fields[i].toLowerCase().contains("tiempo"))){
						int value = (Integer) data[i];
						if(value>99999)
							data[i] = "Nulo";
					}
					else if((paramType.contains("integer") && fields[i].toLowerCase().contains("dorsal"))
							|| (paramType.equals("int") && fields[i].toLowerCase().contains("dorsal"))){
						if(data[i]==null)
							data[i] = "No Presentado";
						else{
							int value = (Integer) data[i];
							if(value==0)
								data[i] = "No Presentado";
						}
					}
					else if(paramType.contains("date") && fields[i].toLowerCase().contains("fecha")){
						if(data[i]==null)
							data[i] = "";
						else{
							Date value = (Date) data[i];
							SimpleDateFormat dt = new SimpleDateFormat("dd/MM/yyyy");
							data[i] = dt.format(value);
						}
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		   	doc.addRow(fila++, data, false, false);
		}
		
		// Close the Word Document
		int widths[] = new int[fields.length];
		for(int i = 0; i<widths.length; i++){
			widths[i] = -1;
		}
		doc.setWidths(widths);
	}
  	
}
