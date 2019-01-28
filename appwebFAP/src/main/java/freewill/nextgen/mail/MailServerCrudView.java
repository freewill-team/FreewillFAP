package freewill.nextgen.mail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

import com.vaadin.event.FieldEvents;
import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid.SelectionModel;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.common.entities.MailServerEntity;
import freewill.nextgen.genericCrud.GenericCrudLogic;
import freewill.nextgen.genericCrud.ImportGrid;
import freewill.nextgen.hmi.utils.ExportToXls;
import freewill.nextgen.hmi.utils.ImportFromXls;
import freewill.nextgen.hmi.utils.Messages;

/**
 * A view for performing create-read-update-delete operations on records.
 *
 * See also {@link StatusCrudLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */

@SuppressWarnings("serial")
public class MailServerCrudView extends CssLayout implements View {

	@SuppressWarnings("unused")
	private static String ICC_PERMISSION = "ICC_ALARMS_PERMISSION";
    public final String VIEW_NAME = Messages.get().getKey("mailservers");
    public final String VIEW_KEY = "mailservers";
    private MailServerGrid grid;
    private MailServerForm form;

    private MailServerCrudLogic viewLogic = new MailServerCrudLogic(this);
    private Button newRecord;
    private File tempFile = null; // temporal file for imports from Xls

    public MailServerCrudView() {
    	
        setSizeFull();
        addStyleName("crud-view");
        HorizontalLayout topLayout = createTopBar();

        grid = new MailServerGrid();
        grid.addSelectionListener(new SelectionListener() {

            @Override
            public void select(SelectionEvent event) {
                viewLogic.rowSelected(grid.getSelectedRow());
            }
        });

        form = new MailServerForm(viewLogic);

        VerticalLayout barAndGridLayout = new VerticalLayout();
        //barAndGridLayout.addComponent(new GenericHeader(VIEW_NAME, FontAwesome.MAIL_FORWARD));
        barAndGridLayout.addComponent(topLayout);
        barAndGridLayout.addComponent(grid);
        //barAndGridLayout.setMargin(true);
        //barAndGridLayout.setSpacing(true);
        barAndGridLayout.setSizeFull();
        barAndGridLayout.setExpandRatio(grid, 1);
        barAndGridLayout.setStyleName("crud-main-layout");

        addComponent(barAndGridLayout);
        addComponent(form);

        //viewLogic.init();
    }

    @SuppressWarnings("deprecation")
	public HorizontalLayout createTopBar() {
        TextField filter = new TextField();
        filter.setStyleName("filter-textfield");
        filter.setInputPrompt("Filter");
        
        filter.setImmediate(true);
        filter.addTextChangeListener(new FieldEvents.TextChangeListener() {
            @Override
            public void textChange(FieldEvents.TextChangeEvent event) {
                grid.setFilter(event.getText());
            }
        });
        
        Button exportBtn = new Button(Messages.get().getKey("export2excel"));
		exportBtn.setIcon(FontAwesome.FILE_EXCEL_O);
		exportBtn.addClickListener(new ClickListener() {
			@SuppressWarnings("unchecked")
			@Override
            public void buttonClick(ClickEvent event) {
				File file = ExportToXls.get().createXLS(
						(List<Object>)grid.getContainerDataSource().getItemIds(),
						MailServerEntity.class, "id", "label", "description", "active", 
						"hostname", "port", "username", "password");
				if(file!=null){
					FileResource resource = new FileResource(file);
					Page.getCurrent().open(resource, "Export File", false);
		    		// Finally, removes the temporal file
		    		// file.delete();
				}
            }
        });

		// Create and configure the upload component for new requirements
        Upload upload = new Upload(Messages.get().getKey("import"), new Upload.Receiver() {
            @Override
            public OutputStream receiveUpload(String filename, String mimeType) {
            	try {
            		// Here, we'll stored the uploaded file as a temporary file
            		if(!filename.toUpperCase().endsWith("XLSX")){
            			Notification.show("Not an XLSX file. Upload interrupted."); // i18n
	            		return null;
	            	}
	            	tempFile = File.createTempFile("temp", ".xlsx");
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
				long maxsize = 50; // 50 Mb
				if(size>maxsize){
					showSaveNotification("File bigger than "+maxsize+"Mb. Upload interrupted."); // i18n
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
	            		Notification.show("File uploaded successfully."); // i18n
	            		List<MailServerEntity> list = ImportFromXls.get().getList(tempFile, MailServerEntity.class);
	            		// Open modal windows with import results
	            		GenericCrudLogic<MailServerEntity> logic = new GenericCrudLogic<MailServerEntity>(null, MailServerEntity.class, "id");
	            		getUI().addWindow(new ImportGrid<MailServerEntity>(MailServerEntity.class, list, logic,
	            				"id", "login", "creationDate", "expiredDate", 
	     						"enumBlockedType", "blockedDate"));
	            		tempFile.delete();
	            		viewLogic.init();
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
        upload.setButtonCaption("Import");
        //upload.setIcon(FontAwesome.UPLOAD);
        upload.setCaption(null);
        upload.setImmediate(true);
		
        newRecord = new Button(Messages.get().getKey("new"));
        newRecord.addStyleName(ValoTheme.BUTTON_PRIMARY);
        newRecord.setIcon(FontAwesome.PLUS_CIRCLE);
        newRecord.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                viewLogic.newRecord();
            }
        });

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setSpacing(true);
        topLayout.setMargin(true);
        topLayout.setWidth("100%");
        topLayout.addComponent(filter);
        //topLayout.addComponent(exportBtn);
        //topLayout.addComponent(upload);
        topLayout.addComponent(newRecord);
        topLayout.setComponentAlignment(filter, Alignment.MIDDLE_LEFT);
        topLayout.setExpandRatio(filter, 1);
        topLayout.setStyleName("top-bar");
        return topLayout;
    }

    @Override
    public void enter(ViewChangeEvent event) {
    	/*if(!EntryPoint.get().getAccessControl().hasUserPermission(ICC_PERMISSION)){
    		UI.getCurrent().getNavigator().navigateTo("No Permission/"+ICC_PERMISSION);
    		return;
    	}*/
    	viewLogic.init();
        viewLogic.enter(event.getParameters());
    }

    public void showError(String msg) {
        Notification.show(msg, Type.ERROR_MESSAGE);
    }

    public void showSaveNotification(String msg) {
        Notification.show(msg, Type.TRAY_NOTIFICATION);
    }

    public void setNewRecordEnabled(boolean enabled) {
    	newRecord.setEnabled(enabled);
    }

    public void clearSelection() {
    	try{
    		grid.getSelectionModel().reset();
    	}
    	catch(Exception e){
    		System.out.println("clearSelection: "+e.getMessage());
    	}
    }

    public void selectRow(MailServerEntity row) {
        ((SelectionModel.Single) grid.getSelectionModel()).select(row);
    }

    public MailServerEntity getSelectedRow() {
        return grid.getSelectedRow();
    }

    public void editRecord(MailServerEntity rec) {
        if (rec != null) {
            form.addStyleName("visible");
            form.setEnabled(true);
        } else {
            form.removeStyleName("visible");
            form.setEnabled(false);
        }
        form.editRecord(rec);
    }

    public void showRecords(Collection<MailServerEntity> records) {
        grid.setRecords(records);
    }

    public void refreshRecord(MailServerEntity rec) {
        grid.refresh(rec);
        grid.scrollTo(rec);
    }

    public void removeRecord(MailServerEntity rec) {
        grid.remove(rec);
    }

}
