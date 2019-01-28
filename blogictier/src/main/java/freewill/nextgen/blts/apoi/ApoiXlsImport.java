package freewill.nextgen.blts.apoi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import freewill.nextgen.blts.data.FeatureEntity;
import freewill.nextgen.blts.data.RequirementEntity;
import freewill.nextgen.blts.data.Style.StyleEnum;

public class ApoiXlsImport {
	XSSFWorkbook workbook = null;
	private String filename = "";
	
	public ApoiXlsImport(String fname) throws Exception {
        InputStream excelStream = null;
        filename = fname;
        try {
        	System.out.println("ApoiXLSImport opening " + filename + " xlsx file...");
        	File excelFile = new File(fname);
            excelStream = new FileInputStream(excelFile);
            // High level representation of a workbook.
            workbook = new XSSFWorkbook(excelStream);
        }
        catch (FileNotFoundException fileNotFoundException) {
        	System.out.println("The file does not exists: " + fileNotFoundException);
		}
	}
	
	public XSSFWorkbook getDoc(){
		return workbook;
	}
	
	public ApoiXlsImport(File excelFile) throws Exception {
        InputStream excelStream = null;
        filename = excelFile.getName();
        try {
        	System.out.println("ApoiXLSImport opening " + filename + " xlsx file...");
            excelStream = new FileInputStream(excelFile);
            // High level representation of a workbook.
            workbook = new XSSFWorkbook(excelStream);
        }
        catch (FileNotFoundException fileNotFoundException) {
        	System.out.println("The file does not exists: " + fileNotFoundException);
		}
	}
	
	public int getSize(){
		if(workbook!=null){
			// We chose the sheet is passed as parameter. 
			XSSFSheet sheet = workbook.getSheetAt(0);
            // I get the number of rows occupied on the sheet
            return sheet.getLastRowNum();
		}
		return 0;
	}
	
	public List<RequirementEntity> getReqs() throws Exception{
		List<RequirementEntity> reqs = new ArrayList<RequirementEntity>();
		if(workbook!=null){
			// We chose the sheet is passed as parameter. 
			XSSFSheet sheet = workbook.getSheetAt(0);
			// An object that allows us to read a row of the excel sheet, and extract from it the cell contents.
            XSSFRow row;
            // Initialize the object to read the value of the cell 
            //XSSFCell cell;
            // I get the number of rows occupied on the sheet
            int rows = sheet.getLastRowNum();
            // I get the number of columns occupied on the sheet
            int cols = 0;            
            // A string used to store the reading cell
            String cellValue;
            // Saves positions of "customid" and "description" columns 
            int CUSTOMID = -1, DESCRIPTION = -1; 
            // For this example we'll loop through the rows getting the data we want
            for (int r = 0; r <= rows; r++) {
                row = sheet.getRow(r);
                if (row == null){
                    break;
                }
                else{
                	cols = row.getLastCellNum();
                	if(r==0){ // First row, We check whether the headings are correct
                		for (int c = 0; c < cols; c++) {
	                		cellValue = getCellValue(row, c);                     
	                		if(cellValue.equals("customid"))
	                			CUSTOMID = c;                
	                		else if(cellValue.equals("description"))
	                			DESCRIPTION = c;
                		}
                		if( CUSTOMID==-1 )
                			throw new IllegalArgumentException("ApoiXlsImport failed: 'customid' column not found");
                		else if( DESCRIPTION==-1 )
                			throw new IllegalArgumentException("ApoiXlsImport failed: 'description' column not found");
                		continue;
                	}
                	// Rest of rows, add a new item to the list
                	RequirementEntity item = new RequirementEntity();
                    for (int c = 0; c < cols; c++) {
                    	cellValue = getCellValue(row, c);                       
                        if(c == CUSTOMID)
                        	item.setCustomid(cellValue);
                        else if(c == DESCRIPTION)
                        	item.setDescription(cellValue);
                    }
                    reqs.add(item);
                }
            }
		}
		return reqs;
	}
	
	public String getCellValue(XSSFRow row, int c) {
		return row.getCell(c) == null?"":
        	(row.getCell(c).getCellType() == Cell.CELL_TYPE_STRING)?row.getCell(c).getStringCellValue():
            (row.getCell(c).getCellType() == Cell.CELL_TYPE_NUMERIC)?"" + row.getCell(c).getNumericCellValue():
            (row.getCell(c).getCellType() == Cell.CELL_TYPE_BOOLEAN)?"" + row.getCell(c).getBooleanCellValue():
            (row.getCell(c).getCellType() == Cell.CELL_TYPE_BLANK)?"BLANK":
            (row.getCell(c).getCellType() == Cell.CELL_TYPE_FORMULA)?"FORMULA":
            (row.getCell(c).getCellType() == Cell.CELL_TYPE_ERROR)?"ERROR":"";
	}
	
	public void CloseXls() throws Exception {
		if(workbook!=null){
			// workbook.close();
		    System.out.println("ApoiXLSImport " + filename + " read successfully");
		}
	}
	
	public List<FeatureEntity> getFeatures() throws Exception{
		List<FeatureEntity> recs = new ArrayList<FeatureEntity>();
		if(workbook!=null){
			// We chose the sheet is passed as parameter. 
			XSSFSheet sheet = workbook.getSheetAt(0);
			// An object that allows us to read a row of the excel sheet, and extract from it the cell contents.
            XSSFRow row;
            // Initialize the object to read the value of the cell 
            //XSSFCell cell;
            // I get the number of rows occupied on the sheet
            int rows = sheet.getLastRowNum();
            // I get the number of columns occupied on the sheet
            int cols = 0;            
            // A string used to store the reading cell
            String cellValue;
            // Saves positions of "level", "title" and "description" columns 
            int TITLE = -1, DESCRIPTION = -1, LEVEL=-1; 
            // For this example we'll loop through the rows getting the data we want
            for (int r = 0; r <= rows; r++) {
                row = sheet.getRow(r);
                if (row == null){
                    break;
                }
                else{
                	cols = row.getLastCellNum();
                	if(r==0){ // First row, We check whether the headings are correct
                		for (int c = 0; c < cols; c++) {
	                		cellValue = getCellValue(row, c);                     
	                		if(cellValue.equals("title"))
	                			TITLE = c;                
	                		else if(cellValue.equals("description"))
	                			DESCRIPTION = c;
	                		else if(cellValue.equals("level"))
	                			LEVEL = c;
                		}
                		if( TITLE==-1 )
                			throw new IllegalArgumentException("ApoiXlsImport failed: 'title' column not found");
                		else if( DESCRIPTION==-1 )
                			throw new IllegalArgumentException("ApoiXlsImport failed: 'description' column not found");
                		else if( LEVEL==-1 )
                			throw new IllegalArgumentException("ApoiXlsImport failed: 'level' column not found");
                		continue;
                	}
                	// Rest of rows, add a new item to the list
                	FeatureEntity item = new FeatureEntity();
                    for (int c = 0; c < cols; c++) {
                    	cellValue = getCellValue(row, c);                       
                        if(c == TITLE)
                        	item.setTitle(cellValue);
                        else if(c == DESCRIPTION)
                        	item.setDescription(cellValue);
                        else if(c == LEVEL){
                        	//System.out.println("level="+cellValue);
                        	try{
                        		//item.setLevel(DocLevelEnum.valueOf(cellValue));
                        		item.setLevel(StyleEnum.valueOf(cellValue));
                        	}
                        	catch(Exception e){
                        		//item.setLevel(DocLevelEnum.L1);
                        		item.setLevel(StyleEnum.NORMAL);
                        	}
                        }
                    }
                    recs.add(item);
                }
            }
		}
		return recs;
	}
	
}
