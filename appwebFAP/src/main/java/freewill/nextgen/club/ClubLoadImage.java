package freewill.nextgen.club;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.FileResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.data.ConfigEntity.ConfigItemEnum;

/**
 * Simple name editor Window.
 */
@SuppressWarnings("serial")
public class ClubLoadImage extends Window {

    private Image image = null;
    private ClubForm parent = null;
    private File tempFile = null;

    public ClubLoadImage(ClubForm prnt) {
    	parent = prnt;
        setCaption("Upload Image");
        setModal(true);
        setClosable(false);
        setResizable(false);
        setWidth(460.0f, Unit.PIXELS);

        addStyleName("edit-dashboard");

        setContent(buildContent());
    }

    private Component buildContent() {
        VerticalLayout result = new VerticalLayout();
        result.setMargin(true);
        result.setSpacing(true);
        image = new Image(); // Empty image
        image.setSizeFull();
        image.setWidth("440px");
        image.setHeight("220px");
        result.addComponent(image);
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
            	// Assign new image to Feature
            	parent.addImage(tempFile);
            	//tempFile.delete();
                close();
            }
        });
        save.setClickShortcut(KeyCode.ENTER, null);
        save.setEnabled(false);
        
        // Create and configure the upload component for new images
        Upload upload = new Upload("Upload Image", new Upload.Receiver() {
            @Override
            public OutputStream receiveUpload(String filename, String mimeType) {
            	try {
            		// Here, we'll stored the uploaded file as a temporary file
            		// usando mimeType, verifica que es image y aplica la extension correcta
            		//System.out.println("Filename="+filename);
            		//System.out.println("MimeType="+mimeType);
            		String[] tipos = mimeType.split("/");
            		if(tipos.length<2 || tipos[0].equals("image")==false){
            			Notification.show("Not an image file. Upload interrupted.");
            			return null;
            		}
	            	tempFile = File.createTempFile("temp_", "."+tipos[1]);
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
            		// process the uploaded file and store the new requirements
            		if(tempFile.length()>10 /*bytes*/){
	            		//Notification.show("File uploaded successfully.");
	            		FileResource resource = new FileResource(tempFile);
	                    //image = new Image(tempFile.getName(), resource);
	                    image.setSource(resource);
	                    image.setSizeFull();
	            		//tempFile will be deleted later on
	                    save.setEnabled(true);
	                    ClubLoadImage.this.center();
            		}
            		else{
            			// Do nothing
            			tempFile.delete();
            		}
            	} 
            	catch (Exception e) {
            		e.printStackTrace();
            	}
            }
        });
        upload.setStyleName(ValoTheme.BUTTON_FRIENDLY);
        upload.setButtonCaption("Upload Image");
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
