package freewill.nextgen.hmi.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
	
}
