package freewill.nextgen.hmi.utils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.bltclient.BltClient;

/*
 * This Export2Xls class enables import from Excel format to list.
 * As it follows the singleton model, it will be used for all the web sessions.
 * 
 */

public class ImportFromXls {
	
	private static ImportFromXls INSTANCE;
	private Logger log = Logger.getLogger(ImportFromXls.class);
	
	public ImportFromXls(){
		// Constructor
		try {
			// Initialization
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public synchronized static ImportFromXls get() {
        if (INSTANCE == null) {
            INSTANCE = new ImportFromXls();
        }
        return INSTANCE;
    }
	
	@SuppressWarnings("deprecation")
	public <T> List<T> getList(File file, Class<T> entity) throws Exception{
		List<T> list = new ArrayList<T>();
		try{
			ApoiXlsImport xlsdoc = new ApoiXlsImport(file);
			XSSFWorkbook workbook = xlsdoc.getDoc();
			if(workbook!=null){
				// We chose the sheet is passed as parameter. 
				XSSFSheet sheet = workbook.getSheetAt(0);
				// An object that allows us to read a row of the excel sheet, and extract from it the cell contents.
	            XSSFRow row;
	            // I get the number of rows occupied on the sheet
	            int rows = sheet.getLastRowNum();
	            // I get the number of columns occupied on the sheet
	            int cols = 0;            
	            // A string used to store the reading cell
	            String cellValue;
	            // This array contains the headings/fields
	            String[] headings = null;
	            // For this example we'll loop through the rows getting the data we want
	            for (int r = 0; r <= rows; r++) {
	                row = sheet.getRow(r);
	                if (row == null){
	                    break;
	                }
	                else{
	                	cols = row.getLastCellNum();
	                	if(r==0){ 
	                		// First row, We fulfill the headings/fields 
	                		headings = new String[cols];
	                		for (int c = 0; c < cols; c++) {
		                		String value = xlsdoc.getCellValue(row, c);
		                		// infers the set method
		                		headings[c] = "set"+ value.substring(0, 1).toUpperCase()+value.substring(1);
	                		}
	                		continue;
	                	}
	                	// Rest of rows, add a new item to the list
				    	T rec = entity.newInstance();
				    	// Process columns
	                    for (int c = 0; c < cols; c++) {
	                    	try{
		                    	cellValue = xlsdoc.getCellValue(row, c);
		                    	System.out.println("CellValue = "+cellValue);
		                        Method method = null;
		                        for(Method m:entity.getMethods()){
		                        	if(m.getName().contains(headings[c])){
		                        		method = m;
		                        		break;
		                        	}
		                        }
		                        if(method==null){
		                        	log.error("setMethod not found for "+headings[c]);
		                        	continue;
		                        }
		                        
		                        String paramType = method.getParameterTypes()[0].toGenericString();
		                        System.out.println("methodName="+headings[c]+"("+paramType+")");

			    		    	if("int".equals(paramType) || paramType.contains("java.lang.Integer")){
			    		    		int i = cellValue.indexOf(".");
			    		    		if(i>0) cellValue = cellValue.substring(0, i);
			    		    		i = cellValue.indexOf(",");
			    		    		if(i>0) cellValue = cellValue.substring(0, i);
			    		    	    method.invoke(rec, Integer.valueOf(cellValue));
			    		    	}
			    		    	else if("boolean".equals(paramType) || paramType.contains("java.lang.Boolean")){
			    		    	    method.invoke(rec, Boolean.valueOf(cellValue));
			    		    	}
			    		    	else if(paramType.contains("java.lang.String")){
			    		    	    method.invoke(rec, (String)cellValue);
			    		    	}
			    		    	else if("long".equals(paramType) || paramType.contains("java.lang.Long")){
			    		    		int i = cellValue.indexOf(".");
			    		    		if(i>0) cellValue = cellValue.substring(0, i);
			    		    		i = cellValue.indexOf(",");
			    		    		if(i>0) cellValue = cellValue.substring(0, i);
			    		    	    method.invoke(rec, Long.valueOf(cellValue));
			    		    	}
			    		    	else if("double".equals(paramType) || paramType.contains("java.lang.Double"))
			    		    	    method.invoke(rec, Double.valueOf(cellValue));
			    		    	else if("float".equals(paramType) || paramType.contains("java.lang.Float"))
			    		    	    method.invoke(rec, Float.valueOf(cellValue));
			    		    	else if("date".equals(paramType) || paramType.contains("java.util.Date"))
			    		    	    method.invoke(rec, new Date(cellValue));
			    		    	else if(method.getParameterTypes()[0].isEnum()){
			    		    		// Krunch enumerations 
			    		    		int i = cellValue.indexOf(".");
			    		    		if(i>0) cellValue = cellValue.substring(0, i);
			    		    		i = cellValue.indexOf(",");
			    		    		if(i>0) cellValue = cellValue.substring(0, i);
			    		    		Integer id = Integer.valueOf(cellValue);
			    		    		Class<?> clazz = method.getParameterTypes()[0];                              
			    		    		method.invoke(rec, clazz.getEnumConstants()[id]);
			    		    	}
			    		    	else{
			    		    		// it is a class
			    		    		int i = cellValue.indexOf(".");
			    		    		if(i>0) cellValue = cellValue.substring(0, i);
			    		    		i = cellValue.indexOf(",");
			    		    		if(i>0) cellValue = cellValue.substring(0, i);
			    		    		Long id = Long.valueOf(cellValue);
			    		    		if(id!=null && id>0){
			    		    			// instanciar objeto y salvar
			    		    			try{
			    		    				Class<?> clazz = method.getParameterTypes()[0]; 
			    		    				Object obj = BltClient.get().getEntityById(""+id, clazz,
			    		    						EntryPoint.get().getAccessControl().getTokenKey());
			    		    				if(obj!=null)
			    		    					method.invoke(rec, obj);
			    		    			}
			    		    			catch(Exception e){
			    		    				log.error(e.getMessage());
				                			e.printStackTrace();
			    		    			}
			    		    		}
			    		    		// Else, do nothing. is null
			    		    	}
			    		    	//	log.error("paramType not binded");
	                    	}
	                    	catch(Exception e){
	                			log.error(e.getMessage());
	                			e.printStackTrace();
	                		}
	                    }
	                    list.add(rec);
	                }
	            }
			}
		}
		catch(Exception e){
			log.error(e.getMessage());
			e.printStackTrace();
		}
		return list;
	}
  	
}
