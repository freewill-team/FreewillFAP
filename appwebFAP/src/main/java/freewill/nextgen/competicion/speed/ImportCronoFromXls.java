package freewill.nextgen.competicion.speed;

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
import freewill.nextgen.data.SpeedTimeTrialEntity;
import freewill.nextgen.data.ConfigEntity.ConfigItemEnum;
import freewill.nextgen.hmi.utils.ApoiXlsImport;

/*
 * This Export2Xls class enables import from Excel format to list.
 * As it follows the singleton model, it will be used for all the web sessions.
 * 
 */

public class ImportCronoFromXls {
	
	private static ImportCronoFromXls INSTANCE;
	private Logger log = Logger.getLogger(ImportCronoFromXls.class);
	
	public ImportCronoFromXls(){
		// Constructor
		try {
			// Initialization
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public synchronized static ImportCronoFromXls get() {
        if (INSTANCE == null) {
            INSTANCE = new ImportCronoFromXls();
        }
        return INSTANCE;
    }
	
	@SuppressWarnings("deprecation")
	public List<SpeedTimeTrialEntity> getList(File file) throws Exception{
		List<SpeedTimeTrialEntity> list = new ArrayList<SpeedTimeTrialEntity>();
		try{
			int numConos = EntryPoint.get().getConfigInt(ConfigItemEnum.MAXNUMCONOSDERRIBADOS);
			 
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
	            for (int r = 0; r < rows; r++) {
	            	if(r<3) continue; // Saltamos lad tres primeras filas
	                row = sheet.getRow(r);
	                if (row == null)
	                    break;
	                
	                SpeedTimeTrialEntity rec = new SpeedTimeTrialEntity();
				    // Process columns
	                try{
	                	cellValue = xlsdoc.getCellValue(row, 0);   
		                if(cellValue.contains("Count:"))
		                	break; // Llegamos al final
		                
		                String valores[] = cellValue.split(":");
		                rec.setId(Long.valueOf(valores[0]));
		                if(valores.length>2)
		                	rec.setNombre(valores[2]);
		                System.out.println("Id     = "+rec.getId());
		                System.out.println("Nombre = "+rec.getNombre());
		                
		                cellValue = xlsdoc.getCellValue(row, 6);
		                if(cellValue.contains("DQ"))
		                	  rec.setConos1(numConos);
		                else{
			                cellValue = xlsdoc.getCellValue(row, 3);
			                System.out.println("cellValue = "+cellValue);
			                Float val = Float.valueOf(cellValue.replaceAll(",", "."))*1000;
			                rec.setTiempo1A(val.intValue());
			                cellValue = xlsdoc.getCellValue(row, 4);
			                rec.setConos1(Integer.valueOf(cellValue));
		                }
		                
		                cellValue = xlsdoc.getCellValue(row, 10);
		                if(cellValue.contains("DQ"))
		                	  rec.setConos2(numConos);
		                else{
			                cellValue = xlsdoc.getCellValue(row, 7);
			                Float val = Float.valueOf(cellValue.replaceAll(",", "."))*1000;
			                rec.setTiempo2A(val.intValue());
			                cellValue = xlsdoc.getCellValue(row, 8);
			                rec.setConos2(Integer.valueOf(cellValue));
		                }
		                
		                list.add(rec);
	                }
	                catch(Exception e){
	                	log.error(e.getMessage());
	                	e.printStackTrace();
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
