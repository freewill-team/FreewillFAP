package freewill.nextgen.company;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.addon.contextmenu.ContextMenu;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.apoi.ApoiDocImport;
import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.company.CompanyForm.TemplateEnum;
import freewill.nextgen.data.Style;
import freewill.nextgen.data.ConfigEntity.ConfigItemEnum;
import freewill.nextgen.data.Style.StyleEnum;
import freewill.nextgen.genericCrud.GenericGrid;

/**
 * Simple name editor Window.
 */
@SuppressWarnings("serial")
public class CompanyLoadTemplate extends Window {

    private CompanyForm parent = null;
    private File tempFile = null;
    private TemplateEnum templateType = null;
    private ProgressBar PB = null;
    private GenericGrid<Style> grid = null;
    private List<Style> styleList = null;
    private VerticalLayout result = null;

    public CompanyLoadTemplate(CompanyForm prnt, TemplateEnum type) {
    	parent = prnt;
    	templateType = type;
        setCaption("Upload Template File");
        setModal(true);
        setClosable(false);
        setResizable(false);
        setWidth(480.0f, Unit.PIXELS);
        
        grid = new GenericGrid<Style>(Style.class, "id", "name", "level");
        grid.getColumn("name").setHeaderCaption("Detected Word Style");
        grid.getColumn("level").setHeaderCaption("FreeWill Style");

        addStyleName("edit-dashboard");

        setContent(buildContent());
    }

    private Component buildContent() {
        result = new VerticalLayout();
        result.setMargin(true);
        result.setSpacing(true);
        PB = new ProgressBar();
        PB.setValue(0f);
        PB.setSizeFull();
        result.addComponent(PB);
        result.addComponent(buildFooter());
        return result;
    }

    @SuppressWarnings("deprecation")
	private Component buildFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setSpacing(true);
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Unit.PERCENTAGE);
        
        Button save = new Button("Save");
        save.addStyleName(ValoTheme.BUTTON_PRIMARY);
        save.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
            	if(templateType==TemplateEnum.STYLES){
            		for(Style sty:styleList){
        		        if(sty.getName().isEmpty() || sty.getName()==null 
        		        		|| sty.getName().equals("")){
        		        	Notification.show("You have to define all styles");
        		        	return;
        		        }
        	        }
            	}
            	// Save new data
            	parent.addTemplate(tempFile, templateType, styleList);
            	//tempFile.delete();
                close();
            }
        });
        save.setClickShortcut(KeyCode.ENTER, null);
        save.setEnabled(false);
        
        // Create and configure the upload component for new images
        Upload upload = new Upload("Upload Template File", new Upload.Receiver() {
            @Override
            public OutputStream receiveUpload(String filename, String mimeType) {
            	try {
            		PB.setValue(0.1f);
            		// Here, we'll stored the uploaded file as a temporary file
            		// usando mimeType, verifica que es un excel o un word y aplica la extension correcta
            		System.out.println("Filename="+filename);
            		System.out.println("MimeType="+mimeType);
            		System.out.println("Template="+templateType.toString());
            		if(!filename.toUpperCase().endsWith(templateType.toString())){
            			Notification.show("Not a "+templateType.toString()+" file. Upload interrupted.");
            			return null;
            		}
	            	tempFile = File.createTempFile("temp_", templateType.toString());
	            	return new FileOutputStream(tempFile);
            	} 
            	catch (IOException e) {
            		e.printStackTrace();
            		return null;
            	}
            }
        });

        upload.addListener(new Upload.StartedListener(){
        	@Override
			public void uploadStarted(StartedEvent event) {
        		if (event.getFilename().isEmpty()) {
			        upload.interruptUpload();
			        return;
        		}
				System.out.println("File Upload started... "+event.getContentLength()+" bytes");
				long size = event.getContentLength()/1024/1024; //bytes->kb->Mb
				PB.setValue(0.5f);
				long maxsize = EntryPoint.get().getConfigLong(ConfigItemEnum.MAXUPLOADFILESIZE);
				if(size>maxsize){
					Notification.show("File bigger than "+maxsize+"Mb. Upload interrupted.");
					upload.interruptUpload();
				}
			}
        });
        
        upload.addListener(new Upload.FinishedListener() {
            @Override
            public void uploadFinished(Upload.FinishedEvent finishedEvent) {
            	try {
            		if(tempFile==null) return;
            		// process the uploaded file and store the new requirements
            		if(tempFile.length()>10 /*bytes*/){
            			PB.setValue(1f);
	            		//Notification.show("File uploaded successfully.");
	            		//tempFile will be deleted later on
	                    save.setEnabled(true);
	                    if(templateType==TemplateEnum.STYLES){
	                    	ApoiDocImport doc = new ApoiDocImport(tempFile);
	                    	List<Style> entireList = doc.getAllStyles();
		            		doc.CloseDoc();
		            		result.replaceComponent(PB, grid);
		            		styleList = new ArrayList<Style>();
		            		for(StyleEnum sty:StyleEnum.values()){
		            			Style style = new Style();
		            			style.setLevel(sty);
		            			style.setStyleid(sty.toString());
		            			//style.setName("");
		            			switch(sty){
			        				case H1: style.setName("Ttulo1"); break;
			        				case H2: style.setName("Ttulo2"); break;
			        				case H3: style.setName("Ttulo3"); break;
			        				case H4: style.setName("Ttulo4"); break;
			        				case H5: style.setName("Ttulo5"); break;
			        				case H6: style.setName("Ttulo6"); break;
			        				case H7: style.setName("Ttulo7"); break;
			        				case H8: style.setName("Ttulo8"); break;
			        				case PARAGRAM: style.setName("VIÑETA"); break;
			        				case FIGURE: style.setName("Figure"); break;
			        				default: style.setName("Normal"); break;
		            			}
		            			styleList.add(style);
		            		}
		            		grid.setRecords(styleList);
		            		ContextMenu menu = new ContextMenu(grid, true);
		        	        menu.addItem("Change To...", e -> {
		        	        	// Do nothing
		        	        	});
		        	        for(Style sty:entireList){
		        		        menu.addItem(sty.getName(), e -> {
		        		        	Style style = grid.getSelectedRow();
		        		        	if(style==null) return;
		        		        	style.setName(sty.getName());
		        		        	grid.refresh(style);
		        		        });
		        	        }
		        	        grid.setSizeFull();
		        	        CompanyLoadTemplate.this.center();
	                    }
            		}
            		else{
            			// Do nothing
            			tempFile.delete();
            		}
            	} 
            	catch (Exception e) {
            		e.printStackTrace();
            		styleList = null;
            	}
            }
        });
        upload.setStyleName(ValoTheme.BUTTON_FRIENDLY);
        upload.setButtonCaption("Upload Template File");
        upload.setCaption(null);
        upload.setImmediate(true);
        
        Button cancel = new Button("Cancel");
        cancel.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
            	if(tempFile!=null)
            		tempFile.delete();
                close();
            }
        });
        cancel.setClickShortcut(KeyCode.ESCAPE, null);

        footer.addComponents(upload, cancel, save);
        footer.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
        footer.setExpandRatio(upload, 1);
        
        return footer;
    }
    
}
