package freewill.nextgen.apoi;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;

import javax.imageio.ImageIO;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.XWPFStyles;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTStyle;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STStyleType;

import freewill.nextgen.data.Style;
import freewill.nextgen.data.Style.StyleEnum;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;

public class ApoiDocExport {
	private CustomXWPFDocument document = null;
	private FileOutputStream out = null;
	private InputStream in = null;
	private String filename;
	private int FG = 1;
	private List<Style> styleList = null;
	
	/*public ApoiDocExport(String fname) throws Exception {
		// create Blank Word Document
		filename = fname;
		out = new FileOutputStream(new File(filename));
		in = this.getClass().getResourceAsStream("/Template2.docx");
		document = new CustomXWPFDocument(in);
		System.out.println("ApoiDoc " + filename + " open successfully");
		//document.createNumbering();
		this.addHeadingStyle("Heading1", 1);
		this.addHeadingStyle("Heading2", 2);
		this.addHeadingStyle("Heading3", 3);
		this.addHeadingStyle("Heading4", 4);
		this.addHeadingStyle("Heading5", 5);
	}*/
	
	public ApoiDocExport(File file, byte[] template, List<Style> styles) throws Exception {
		// create Blank Word Document
		styleList = styles;
		filename = file.getAbsolutePath();
		out = new FileOutputStream(file);
		if(template==null || template.length<10)
			in = this.getClass().getResourceAsStream("/Template2.docx");
		else
			in = new ByteArrayInputStream(template);
		document = new CustomXWPFDocument(in);
		System.out.println("ApoiDoc " + filename + " open successfully");
		//document.createNumbering();
		
		/*// Copy Styles from Template
		InputStream dotx = this.getClass().getResourceAsStream("/Template2.dotx");
		XWPFDocument template = new XWPFDocument(dotx);  
		// let's copy styles from template to new doc
		XWPFStyles newStyles = document.createStyles();
		newStyles.setStyles(template.getStyle());*/
		
		/*// Create required styles for headings
		this.addHeadingStyle("Heading1", 0);
		this.addHeadingStyle("Heading2", 1);
		this.addHeadingStyle("Heading3", 2);
		this.addHeadingStyle("Heading4", 3);
		this.addHeadingStyle("Heading5", 4);*/
	}
	
	public void Replace(String key, String value){
		if(document!=null && out!=null){
			
			for (XWPFParagraph p : document.getParagraphs()){
				//StringBuilder sb = new StringBuilder();
				for (XWPFRun r : p.getRuns()) {
				    String text = r.getText(0);
				    if (text != null && text.contains(key)) {
				        text = text.replace(key, value);
				        r.setText(text, 0);
				    }
				}
			}
			
			List<XWPFHeader> headers = document.getHeaderList();
			for(XWPFHeader header : headers){
				for (XWPFParagraph p : header.getParagraphs()){
					for (XWPFRun r : p.getRuns()) {
					    String text = r.getText(0);
					    if (text != null && text.contains(key)) {
					        text = text.replace(key, value);
					        r.setText(text, 0);
					    }
					}
				}
			}
			
			List<XWPFFooter> footers = document.getFooterList();
			for(XWPFFooter footer : footers){
				for (XWPFParagraph p : footer.getParagraphs()){
					for (XWPFRun r : p.getRuns()) {
					    String text = r.getText(0);
					    if (text != null && text.contains(key)) {
					        text = text.replace(key, value);
					        r.setText(text, 0);
					    }
					}
				}
			}
			
        }
	}
	
	@Deprecated
	public void AddTOC() throws Exception {
		if(document!=null && out!=null){
			// Creates Table Of Contents
			document.createTOC();
		}
	}
	
	public void AddBullet(String text){
		if(document!=null && out!=null){
			//create Paragraph
		    XWPFParagraph paragraph = document.createParagraph();
		    paragraph.setAlignment(ParagraphAlignment.BOTH);
		    String style = findStyle(StyleEnum.PARAGRAM); // Bullet
		    paragraph.setStyle(style);
		    XWPFRun run = paragraph.createRun();
		    //run.setFontSize(12);
		    run.setText(text);
		}
	}
	
	public void AddTitle(String text, int level) throws Exception {
		if(document!=null && out!=null){
			//create paragraph Title
		    XWPFParagraph paragraph = document.createParagraph();
		    paragraph.setAlignment(ParagraphAlignment.LEFT);
		    
		    //String style = "Heading "+level;
		    StyleEnum styleenum = StyleEnum.values()[level-1];
		    String style = findStyle(styleenum); // "Ttulo"+level;
		    paragraph.setStyle(style);
		    XWPFRun run = paragraph.createRun();
		    //run.setBold(true);
		    //run.setFontSize(20-level*2);
		    //run.setText(title + text);
		    run.setText(text);
		    //paragraph.setStyle(style);
		}
	}

	private String findStyle(StyleEnum styleenum) {
		if(styleList==null) return StyleEnum.NORMAL.toString();
		for(Style style:styleList){
			if(style.getLevel()==styleenum){
				System.out.println("Style "+styleenum+"="+style.getName());
				return style.getName();
			}
		}
		return StyleEnum.NORMAL.toString();
	}

	public void AddParagraph(String text) throws Exception {
		if(document!=null && out!=null){
			//create Paragraph
		    XWPFParagraph paragraph = document.createParagraph();
		    paragraph.setAlignment(ParagraphAlignment.BOTH);
		    String style = findStyle(StyleEnum.NORMAL); // Normal
		    paragraph.setStyle(style);
		    XWPFRun run = paragraph.createRun();
		    //run.setBold(false);
		    //run.setFontSize(12);
		    run.setText(text);
		}
	}
	
	public void AddParagraph(String text, boolean bold) throws Exception {
		if(document!=null && out!=null){
			//create Paragraph
		    XWPFParagraph paragraph = document.createParagraph();
		    paragraph.setAlignment(ParagraphAlignment.BOTH);
		    String style = findStyle(StyleEnum.NORMAL); // Normal
		    paragraph.setStyle(style);
		    XWPFRun run = paragraph.createRun();
		    run.setBold(bold);
		    //run.setFontSize(12);
		    run.setText(text);
		}
	}
	
	public void AddNewPage() throws Exception {
		if(document!=null && out!=null){
			//create Paragraph
		    XWPFParagraph paragraph = document.createParagraph();
		    paragraph.setPageBreak(true);
		    //XWPFRun run = 
		    	paragraph.createRun();
		    //run.setBold(false);
		    //run.setFontSize(12);
		    //run.addBreak(BreakType.PAGE);
		}
	}
	
	@SuppressWarnings("unused")
	@Deprecated
	private XWPFParagraph CreateParagraph(String text, boolean bold) throws Exception {
		if(document!=null){
			// creates Paragraph, but it does not add it to the document
		    XWPFParagraph paragraph = document.createParagraph();
		    paragraph.setAlignment(ParagraphAlignment.LEFT);
		    XWPFRun run = paragraph.createRun();
		    //run.setFontSize(12);
		    //run.setBold(bold);
		    run.setText(text);
		    return paragraph;
		}
		return null;
	}
	
	public void AddTable(String[][] data) throws Exception {
		// it creates a table
	    XWPFTable table = document.createTable();
	    table.setWidth(2000);
		// Add data to table
	    // First row
	    XWPFTableRow tableRow = table.getRow(0);
	    tableRow.getCell(0).setText(data[0][0]);
		for(int j=1; j<data[0].length; j++){
    		//XWPFParagraph p = CreateParagraph(data[0][j], true);
    		//tableRow.addNewTableCell().addParagraph(p);
    		tableRow.addNewTableCell().setText(data[0][j]);
    		//tableRow.getCell(j).setColor("#696969"); // Black
    	}
		// Rest of rows
	    for(int i=1; i<data.length; i++){
	    	//create row
	    	tableRow = table.createRow();
		    for(int j=0; j<data[0].length; j++){
		    	//XWPFParagraph p = CreateParagraph(data[i][j], false);
	    		//tableRow.getCell(j).addParagraph(p);
		    	tableRow.getCell(j).setText(data[i][j]);
		    }
	    }
	}
	
	public void CloseDocument() throws Exception {
		if(document!=null && out!=null){
			AddParagraph("");
			AddParagraph("End of Document.");
			AddParagraph("");
			// Forces update of Table Of Contents
			document.enforceUpdateFields();
			// Writes the Document in file system
			document.write(out);
			out.close();
			// Not-required document.close();
		    System.out.println("ApoiDoc " + filename + " written successfully");
		}
	}
	
	public void AddImage(String imgFile, String title, double size) throws Exception {
		if(document!=null && out!=null){
			int format = XWPFDocument.PICTURE_TYPE_JPEG;
			if(imgFile.endsWith(".emf")) format = XWPFDocument.PICTURE_TYPE_EMF;
            else if(imgFile.endsWith(".wmf")) format = XWPFDocument.PICTURE_TYPE_WMF;
            else if(imgFile.endsWith(".pict")) format = XWPFDocument.PICTURE_TYPE_PICT;
            else if(imgFile.endsWith(".jpeg") || imgFile.endsWith(".jpg")) format = XWPFDocument.PICTURE_TYPE_JPEG;
            else if(imgFile.endsWith(".png")) format = XWPFDocument.PICTURE_TYPE_PNG;
            else if(imgFile.endsWith(".dib")) format = XWPFDocument.PICTURE_TYPE_DIB;
            else if(imgFile.endsWith(".gif")) format = XWPFDocument.PICTURE_TYPE_GIF;
            else if(imgFile.endsWith(".tiff")) format = XWPFDocument.PICTURE_TYPE_TIFF;
            else if(imgFile.endsWith(".eps")) format = XWPFDocument.PICTURE_TYPE_EPS;
            else if(imgFile.endsWith(".bmp")) format = XWPFDocument.PICTURE_TYPE_BMP;
            else if(imgFile.endsWith(".wpg")) format = XWPFDocument.PICTURE_TYPE_WPG;
            else {
                System.err.println("Unsupported picture: " + imgFile +
                        ". Expected emf|wmf|pict|jpeg|png|dib|gif|tiff|eps|bmp|wpg");
                return;
            }
			try{
				BufferedImage bimg = ImageIO.read(new File(imgFile));
				int width          = bimg.getWidth();
				int height         = bimg.getHeight();
				double a = 480.0/width*size;
				width = (int) (width*a);
				height = (int) (height*a);
			
			    FileInputStream is = new FileInputStream(imgFile);
			    String blipId = document.addPictureData(is, format);
				document.createPicture(blipId, document.getNextPicNameNumber(XWPFDocument.PICTURE_TYPE_JPEG), width, height);
				// run.addPicture(is, XWPFDocument.PICTURE_TYPE_JPEG, imgFile, Units.toEMU(400), Units.toEMU(275));
				if(!title.equals("")){
					XWPFParagraph paragraph = document.createParagraph();    
				    XWPFRun run = paragraph.createRun();
				    paragraph.setAlignment(ParagraphAlignment.CENTER);
				    String style = findStyle(StyleEnum.FIGURE); // Figure
				    paragraph.setStyle(style);
				    //paragraph.setStyle("Figure");
					run.setText("Figure "+(FG++)+": " + title);
					//run.setText(title);
				    //run.setBold(true);
				    run.addCarriageReturn();
				}
			}
			catch(Exception e){
				System.err.println("Error reading picture: " + imgFile);
				return;
			}
		}
	}
	
	public void AddImage(InputStream is, String title, int format, int w, int h) throws Exception {
		if(document!=null && out!=null){
			
		    String blipId = document.addPictureData(is, format);
			document.createPicture(blipId, document.getNextPicNameNumber(format), w, h);
			//run.addPicture(is, format, title, Units.toEMU(400), Units.toEMU(275));
			if(!title.equals("")){
				XWPFParagraph paragraph = document.createParagraph();    
			    XWPFRun run = paragraph.createRun();
			    paragraph.setAlignment(ParagraphAlignment.CENTER);
			    String style = findStyle(StyleEnum.FIGURE); // Figure
			    paragraph.setStyle(style);
			    //paragraph.setStyle("Figure");
				run.setText("Figure "+(FG++)+": " + title);
				//run.setText(title);
			    //run.setBold(true);
			    run.addCarriageReturn();
			}
		}     
	}
	
	@Deprecated
	public void AddCover(String title1, String title2, String date) throws Exception {
		if(document!=null && out!=null){
			for(int i=0;i<6;i++) this.AddParagraph("");
			
			//create paragraph
		    XWPFParagraph paragraph = document.createParagraph();
		    paragraph.setAlignment(ParagraphAlignment.CENTER);
		    String style = findStyle(StyleEnum.NORMAL); // Title1
		    paragraph.setStyle(style);
		    //paragraph.setStyle("Title1"); 
		    
		    XWPFRun run = paragraph.createRun();
		    run.setBold(true);
		    run.setFontSize(28);
		    run.setText(title1);
		    
		    paragraph = document.createParagraph();
		    paragraph.setAlignment(ParagraphAlignment.CENTER);
		    paragraph.setStyle("Title2");
		    run = paragraph.createRun();
		    run.setFontSize(20);
		    run.setText(title2);
		    
		    paragraph = document.createParagraph();
		    paragraph.setAlignment(ParagraphAlignment.CENTER);
		    paragraph.setStyle("Title3");
		    run = paragraph.createRun();
		    run.setFontSize(14);
		    run.setText(date);
		    
		    for(int i=0;i<6;i++) this.AddParagraph("");
		}
	}
	
	@Deprecated
	public void AddCover(String title1, String title2, String date, byte[] image, String imagefile) throws Exception {
		if(document!=null && out!=null){
			for(int i=0;i<3;i++) this.AddParagraph("");
			
			// Add Logo/Image
			if(image!=null && image.length>10){
				String ext = getExtension(imagefile);
				File file = File.createTempFile("temp_", ext);
				FileOutputStream fos = new FileOutputStream(file.getAbsolutePath());
				fos.write(image);
				fos.close();
    			this.AddImage(file.getAbsolutePath(), "", 1.0);
    			file.delete();
			}
			
			//create paragraph
		    XWPFParagraph paragraph = document.createParagraph();
		    paragraph.setAlignment(ParagraphAlignment.CENTER);
		    paragraph.setStyle("Title1"); 
		    
		    XWPFRun run = paragraph.createRun();
		    run.setBold(true);
		    run.setFontSize(28);
		    run.setText(title1);
		    
		    paragraph = document.createParagraph();
		    paragraph.setAlignment(ParagraphAlignment.CENTER);
		    paragraph.setStyle("Title2");
		    run = paragraph.createRun();
		    run.setFontSize(20);
		    run.setText(title2);
		    
		    paragraph = document.createParagraph();
		    paragraph.setAlignment(ParagraphAlignment.CENTER);
		    paragraph.setStyle("Title3");
		    run = paragraph.createRun();
		    run.setFontSize(14);
		    run.setText(date);
		    
		    for(int i=0;i<3;i++) this.AddParagraph("");
		}
	}
	
	public void AddSection(String section) throws Exception {
		if(document!=null && out!=null){
			String[] array = section.split("\n");
			for(int i=0; i<array.length; i++){
				this.AddParagraph(array[i]);
			}
		}
	}
	
	private String getExtension(String file){
		int i = file.lastIndexOf('.');
		if (i >= 0) {
		    return "."+file.substring(i+1);
		}
		return ".docx";
	}
	
	@SuppressWarnings("unused")
	@Deprecated
	private void addHeadingStyle(String strStyleId, int headingLevel) {

	    CTStyle ctStyle = CTStyle.Factory.newInstance();
	    ctStyle.setStyleId(strStyleId);

	    CTString styleName = CTString.Factory.newInstance();
	    styleName.setVal(strStyleId);
	    ctStyle.setName(styleName);

	    CTDecimalNumber indentNumber = CTDecimalNumber.Factory.newInstance();
	    indentNumber.setVal(BigInteger.valueOf(headingLevel));

	    // lower number > style is more prominent in the formats bar
	    ctStyle.setUiPriority(indentNumber);

	    CTOnOff onoffnull = CTOnOff.Factory.newInstance();
	    ctStyle.setUnhideWhenUsed(onoffnull);

	    // style shows up in the formats bar
	    ctStyle.setQFormat(onoffnull);

	    // style defines a heading of the given level
	    CTPPr ppr = CTPPr.Factory.newInstance();
	    ppr.setOutlineLvl(indentNumber);
	    ctStyle.setPPr(ppr);

	    XWPFStyle style = new XWPFStyle(ctStyle);

	    // is a null op if already defined
	    XWPFStyles styles = document.createStyles();

	    style.setType(STStyleType.PARAGRAPH);
	    styles.addStyle(style);

	}
	
}
