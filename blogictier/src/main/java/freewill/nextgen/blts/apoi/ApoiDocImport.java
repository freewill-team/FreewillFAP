package freewill.nextgen.blts.apoi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPicture;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFStyle;
import org.apache.poi.xwpf.usermodel.XWPFStyles;

import freewill.nextgen.blts.ConfigManager;
import freewill.nextgen.blts.data.FeatureEntity;
import freewill.nextgen.blts.data.Style;
import freewill.nextgen.blts.data.Style.StyleEnum;

public class ApoiDocImport {
	private XWPFDocument workbook = null;
	private String filename = "";
	private ConfigManager configManager = new ConfigManager();
	
	public ApoiDocImport(String fname) throws Exception {
        InputStream excelStream = null;
        filename = fname;
        try {
        	System.out.println("ApoiDocImport opening " + filename + " docx file...");
        	File excelFile = new File(fname);
            excelStream = new FileInputStream(excelFile);
            // High level representation of a workbook.
            workbook = new XWPFDocument(excelStream);
        }
        catch (FileNotFoundException fileNotFoundException) {
        	System.out.println("The file does not exists: " + fileNotFoundException);
		}
	}
	
	public ApoiDocImport(File excelFile) throws Exception {
        InputStream excelStream = null;
        filename = excelFile.getName();
        try {
        	System.out.println("ApoiDocImport opening " + filename + " docx file...");
            excelStream = new FileInputStream(excelFile);
            // High level representation of a workbook.
            workbook = new XWPFDocument(excelStream);
        }
        catch (FileNotFoundException fileNotFoundException) {
        	System.out.println("The file does not exists: " + fileNotFoundException);
		}
	}
	
	public void CloseDoc() throws Exception {
		if(workbook!=null){
			// workbook.close();
		    System.out.println("ApoiDocImport " + filename + " read successfully");
		}
	}
	
	public List<FeatureEntity> getFeatures(List<Style> styleList) throws Exception{
		boolean dbg = false;
		List<FeatureEntity> recs = new ArrayList<FeatureEntity>();
		if(workbook!=null){
			String lasttitle="";
			File tempFile = null;
			boolean titleSaved = true;
			StyleEnum laststyle = StyleEnum.NORMAL;
			
			for (XWPFParagraph p : workbook.getParagraphs()){
				String texto = "";
				StyleEnum style = StyleEnum.NORMAL;
				// Gets paragraph text
				texto = p.getParagraphText();
				if(dbg) System.out.println("\nParagraph = " + texto);
				// gets paragraph first image
				for (XWPFRun r : p.getRuns()) {
				    for (XWPFPicture pic : r.getEmbeddedPictures()) {
				    	byte[] img = pic.getPictureData().getData();
				    	String ext = pic.getPictureData().suggestFileExtension();
				    	if(dbg) System.out.println("Found IMAGE: "+pic.getDescription() + " Extension="+ext);
				    	if(img!=null && img.length>10 && !ext.equals("emf")){
				    		long size = img.length/1024/1024; //bytes->kb->Mb
				    		if(dbg) System.out.println("             (" + size +" Mbytes)");
							long maxsize = configManager.getConfigLong("uploadmaximage");
							if(size<=maxsize){
					    		if(tempFile!=null) tempFile.delete();
					    		tempFile = File.createTempFile("temp_", "."+ext); 
					    		FileOutputStream outputStream = new FileOutputStream(tempFile.getAbsolutePath());
					    		outputStream.write(img);
					    		outputStream.close();
					    		break;
							}
				    	}
				    }
				    if(tempFile!=null) break;
				}
				
				// gets paragraph style
				String styleID = p.getStyleID();
				if(styleID!=null){
					Style sty = findStyle(styleList, styleID);
					if(sty!=null) 
						style = sty.getLevel();
					else{
						if(dbg) System.out.println("StyleID "+styleID+" not found. Using NORMAL.");
						style = StyleEnum.NORMAL;
					}
				}
				else{
					// If no style
					if(dbg) System.out.println("Paragraph without StyleID. Using NORMAL.");
					style = StyleEnum.NORMAL;
				}
				if(dbg) System.out.println("Detected/Converted Style = " + styleID + "/" + style);
				
				if(style.ordinal()<=StyleEnum.H8.ordinal()){
					// It is a new title
					// verify first whether the last title was already saved
					if(titleSaved==false){
						// it is a title without text
						FeatureEntity item = new FeatureEntity();
						item.setTitle(lasttitle);
						item.setLevel(laststyle);
						item.setDescription("");    
						recs.add(item);
						if(dbg) System.out.println("Recorded = " + item.toString());
						titleSaved=true;
					}
					lasttitle = texto;
					laststyle = style;
					if(dbg) System.out.println("NEW TITLE ("+style+") = " + texto);
					titleSaved=false;
					// continue with the new text
				}
				else if(!texto.equals("")){
					// It is a new paragraph
					// Saves new Feature
					FeatureEntity item = new FeatureEntity();
					item.setDescription(texto);
					
					if(titleSaved==false){
						item.setTitle(lasttitle);
						item.setLevel(laststyle);
						titleSaved=true;
					}
					else{
						item.setTitle("");
						item.setLevel(style);
					}
					
		            if(tempFile!=null){
		            	byte[] data = Files.readAllBytes(tempFile.toPath());
		            	item.setImage(data);
		            	item.setImagename(tempFile.getName());
		            	tempFile.delete();
		            	tempFile = null;
		            }
		            
		            recs.add(item);
		            if(dbg) System.out.println("Recorded = " + item.toString());
				}
			}
			
			if(tempFile!=null) tempFile.delete();
		}
		return recs;
	}
	
	public List<Style> getUsedStyles() throws Exception{
		boolean dbg = false;
		List<Style> recs = new ArrayList<Style>();
		if(workbook!=null){
			XWPFStyles styles = workbook.getStyles();
			
			for (XWPFParagraph p : workbook.getParagraphs()){
				String texto = "";
				// default style is normal
				StyleEnum level = StyleEnum.NORMAL;
				// Gets paragraph text
				texto = p.getParagraphText();
				if(texto.equals("")) continue;
				if(dbg) System.out.println("\nParagraph = " + texto);
				// gets paragraph style
				String styleID = p.getStyleID();
				if(styleID==null){
					if(dbg) System.out.println("Paragraph without StyleID. Ignoring.");
					continue;
				}
				// Add it only if not in the list yet
				if(findStyle(recs, styleID)!=null) continue;
				
				String style = "NORMAL";
				if(styleID!=null){
					XWPFStyle stylePa = styles.getStyle(styleID);  
					style = stylePa.getName().toUpperCase();
					if(style.equals("")) continue;
				}
				if(dbg) System.out.println("Detected Style = " + style + "/" + styleID);
				
				String levelstr = "00";
				if(style.startsWith("HEADING "))
					levelstr = style.replace("HEADING ", "H").trim();
				else if(style.startsWith("HEADING"))
					levelstr = style.replace("HEADING", "H").trim();
				else if(style.startsWith("H"))
					levelstr = style.replace("H", "H").trim();
				else if(style.startsWith("TTULO "))
					levelstr = style.replace("TTULO ", "H").trim();
				else if(style.startsWith("TTULO"))
					levelstr = style.replace("TTULO", "H").trim();
				else if(style.startsWith("TÍTULO "))
					levelstr = style.replace("TÍTULO ", "H").trim();
				else if(style.startsWith("TÍTULO"))
					levelstr = style.replace("TÍTULO", "H").trim();
				else if(style.contains("PLANTILLA OFERTA"))
					levelstr = style.replace(" PLANTILLA OFERTA", "").trim().replace("T", "H");
				else if(style.contains("LIST PARA") && !texto.equals(""))
					levelstr = "PARAGRAM";
				else if(style.contains("VIÑETA") && !texto.equals(""))
					levelstr = "PARAGRAM";
				else if(style.contains("FIGUR"))
					levelstr = "FIGURE";
				
				if(!levelstr.equals("00")){
					// It is a style. gets also new level
					try{
						level = StyleEnum.valueOf(levelstr);
					}
					catch(Exception e){
						System.out.println("Error parsing = " + levelstr+". Using NORMAL.");
						level = StyleEnum.NORMAL;
					}
				}
				if(dbg) System.out.println("Will be converted to = " + level);
				
				// Creates Style object
				Style item = new Style();
				item.setName(style);
				item.setStyleid(styleID);
				item.setLevel(level);
				recs.add(item);
				
			}
		}
		return recs;
	}
	
	private Style findStyle(List<Style> recs, String style){
		for(Style rec : recs){
			if(rec.getStyleid().equals(style))
				return rec;
		}
		return null;
	}

	public List<Style> getAllStyles(){
		try {
			return getUsedStyles();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Style>();
		}		
	}
	
	
}
