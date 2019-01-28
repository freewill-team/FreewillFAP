package freewill.nextgen.apoi;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;

public class ApoiXlsExport {
	//private static final String XML_ENCODING = "UTF-8";
	private XSSFWorkbook document = null;
	private XSSFSheet worksheet = null;
	private FileOutputStream out = null;
	private InputStream in = null;
	private String filename;
	
	/*public ApoiXlsExport(String fname) throws Exception {
		// create Blank Word Document
		filename = fname;
		out = new FileOutputStream(new File(filename));
		document = new XSSFWorkbook();
		worksheet = document.createSheet("Results");
		System.out.println("ApoiXlsExport " + filename + " open successfully");
	}*/
	
	public ApoiXlsExport(File file, byte[] template) throws Exception {
		// create Blank Word Document
		filename = file.getAbsolutePath();
		if(template==null || template.length<10)
			in = this.getClass().getResourceAsStream("/Template1.xlsx");
		else
			in = new ByteArrayInputStream(template);
		document = new XSSFWorkbook(in);
		worksheet = document.getSheetAt(0); //createSheet("Data");
		out = new FileOutputStream(file);
		System.out.println("ApoiXlsExport " + filename + " open successfully");
	}
	
	public void setHeader(String title){
		worksheet.getHeader().setCenter(title);
	}
	
	@SuppressWarnings("deprecation")
	public void addRow(int row, Object[] data, boolean ishead){
		if(document==null || worksheet==null) return;
		int i = 0;
		
		Font font = document.createFont();
	    font.setFontHeightInPoints((short)10);
	    font.setFontName("Arial");
	    font.setColor(IndexedColors.BLACK.getIndex());
	    CellStyle rowstyle = document.createCellStyle();
	    rowstyle.setFont(font);
	    rowstyle.setWrapText(true);
	    
	    // index from 0,0... cell A1 is cell(0,0)
	 	XSSFRow row1 = worksheet.createRow((short) row);
	 	XSSFDataFormat fmt = document.createDataFormat();
	 	row1.setRowStyle(rowstyle);
	 	
	 	for(Object item:data){
	 		CellStyle style = document.createCellStyle();
	 		style.setFont(font);
		    style.setWrapText(true);
		    if(ishead){
				style.setFillForegroundColor(HSSFColor.TEAL.index);
				style.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
				font.setBoldweight((short)2);
				font.setColor(HSSFColor.WHITE.index);
		    }
	 		
	 		XSSFCell cell = row1.createCell((short) i);
	 		if(item instanceof Integer)
	 			cell.setCellValue((int)item);
	 		else if(item instanceof Double)
	 			cell.setCellValue((double)item);
	 		else if(item instanceof Float)
	 			cell.setCellValue((float)item);
	 		else if(item instanceof Long)
	 			cell.setCellValue((long)item);
	 		else if(item instanceof Date){
	 			Date date = (Date)item;
	 			cell.setCellValue(date.toLocaleString());
	 		}
	 		else if(item instanceof Boolean)
	 			cell.setCellValue((boolean)item);
	 		else if(item instanceof String)
	 			cell.setCellValue((String)item);
	 		else
	 			cell.setCellValue((String)item);
			style.setAlignment(XSSFCellStyle.ALIGN_FILL);
			if(item instanceof String)
				style.setAlignment(XSSFCellStyle.ALIGN_LEFT);
			else	
				style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
			if(item instanceof Integer || item instanceof Long)
				style.setDataFormat(fmt.getFormat("0"));
			else if(item instanceof Double || item instanceof Float)
				style.setDataFormat(fmt.getFormat("#,###,##0.00"));
			else if(item instanceof Date)
				style.setDataFormat(fmt.getFormat("yyyy-mm-dd"));
			cell.setCellStyle(style);
			i++;
		}
	}
	
	public void CloseDocument(int[] widths) throws Exception {
		if(document!=null && out!=null){
			// sets column auto sizing
			//XSSFRow row = 
					document.getSheetAt(0).getRow(0);
			for(int colNum= 0; colNum<widths.length/*row.getLastCellNum()*/;colNum++){
				if(widths[colNum]>0){ 
					document.getSheetAt(0).setColumnWidth(colNum, widths[colNum]*256);
				}
				else
					document.getSheetAt(0).autoSizeColumn(colNum);
			}
			// Repeat first row
			worksheet.setRepeatingRows(CellRangeAddress.valueOf("1:1"));
			// Forces to recalculate formulas
			document.setForceFormulaRecalculation(true);
			// Writes the Document in file system
			document.write(out);
			out.close();
			// document.close();
		    System.out.println("ApoiXlsExport " + filename + " written successfully");
		}
	}
  	
}
