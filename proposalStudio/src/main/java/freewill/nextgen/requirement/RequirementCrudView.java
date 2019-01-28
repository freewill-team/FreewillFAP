package freewill.nextgen.requirement;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import com.vaadin.addon.contextmenu.ContextMenu;
import com.vaadin.addon.contextmenu.MenuItem;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Grid.SelectionModel;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.apoi.ApoiXlsImport;
import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.common.entities.UserEntity;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.CrudViewInterface;
import freewill.nextgen.data.ProjectEntity;
import freewill.nextgen.data.Requirement2Entity;
import freewill.nextgen.data.RequirementEntity;
import freewill.nextgen.data.RequirementMapping;
import freewill.nextgen.hmi.common.ConfirmDialog;
import freewill.nextgen.hmi.common.GenericCombo;
import freewill.nextgen.hmi.utils.ExportToXls;
import freewill.nextgen.hmi.utils.Messages;
import freewill.nextgen.mapping.MappingCrudLogic;
import freewill.nextgen.proposalStudio.EntryPoint;

/**
 * A view for performing create-read-update-delete operations on records.
 *
 * See also {@link StatusCrudLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
@SuppressWarnings("serial")
public class RequirementCrudView extends CssLayout implements CrudViewInterface<Requirement2Entity> /*View*/ {

    public final String VIEW_NAME = Messages.get().getKey("requirementcrudview.viewname");
    private RequirementGrid grid = null;
    private RequirementForm form = null;
    private RequirementCrudLogic viewLogic = new RequirementCrudLogic(this);
    private MappingCrudLogic mappLogic = new MappingCrudLogic(null);
    private Button newRecord = null;
    private File tempFile = null; // protected
    private GenericCombo<ProjectEntity> projectcb = null;
    private ContextMenu menu = null;
    private CheckBox multiselect = null;
    private Upload upload = null;
    private ProjectEntity currentProject = null;
    
    public String getName(){
    	return VIEW_NAME;
    }

    public RequirementCrudView() {
    	
    	//System.out.println("Entrando en RequirementCrudView, instance = "+new Random().nextInt());
    	
        setSizeFull();
        addStyleName("crud-view");
        HorizontalLayout topLayout = createTopBar();

        grid = new RequirementGrid();
        grid.addSelectionListener(new SelectionListener() {

            @Override
            public void select(SelectionEvent event) {
            	SelectionModel selectionModel = grid.getSelectionModel();
            	if (selectionModel instanceof SelectionModel.Single)
            		viewLogic.rowSelected(grid.getSelectedRow());
            }
        });
            
        menu = new ContextMenu(grid, true);
        final MenuItem basic = menu.addItem("Basic Item", e -> {
            Notification.show("Action!");
        });
        basic.setIcon(FontAwesome.AUTOMOBILE);

        form = new RequirementForm(viewLogic);

        VerticalLayout barAndGridLayout = new VerticalLayout();
        barAndGridLayout.addComponent(topLayout);
        barAndGridLayout.addComponent(grid);
        barAndGridLayout.setMargin(true);
        barAndGridLayout.setSpacing(true);
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
        
        projectcb = new GenericCombo<ProjectEntity>(ProjectEntity.class);
        
        projectcb.addValueChangeListener(new ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
            	if (projectcb.getValue() != null) {
                	viewLogic.setProject(getProject());
                }
            }
        });
        
        // Create and configure the upload component for new requirements
        upload = new Upload("Load Requirements", new Upload.Receiver() {
            @Override
            public OutputStream receiveUpload(String filename, String mimeType) {
            	try {
            		// Here, we'll stored the uploaded file as a temporary file
            		if(!filename.toUpperCase().endsWith("XLSX")){
            			Notification.show("Not an XLSX file. Upload interrupted.");
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
				long maxsize = EntryPoint.get().getConfigLong("uploadmaxxls");
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
	            		//DataService.get().createEvent("Fichero salvado en: " + tempFile.getAbsolutePath());
            			showSaveNotification("File uploaded successfully.");
	            		ApoiXlsImport xls = new ApoiXlsImport(tempFile);
	            		List<RequirementEntity> reqs = xls.getReqs();
	            		xls.CloseXls();
	            		// Open modal windows with import results
	            		getUI().addWindow(new RequirementUpload(reqs, projectcb.getValue(), viewLogic));
	            		tempFile.delete();
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
        upload.setButtonCaption(Messages.get().getKey("import"));
        //upload.setIcon(FontAwesome.UPLOAD);
        upload.setCaption(null);
        upload.setImmediate(true);
        upload.setEnabled(false);
        
        Button exportBtn = new Button(Messages.get().getKey("export"));
		exportBtn.setIcon(FontAwesome.FILE_EXCEL_O);
		exportBtn.addClickListener(new ClickListener() {
			@SuppressWarnings({ "unchecked"})
			@Override
            public void buttonClick(ClickEvent event) {
				List<RequirementMapping> recs = mappLogic.getRequirementsMapped(
						(List<Requirement2Entity>)grid.getContainerDataSource().getItemIds());
				File file = ExportToXls.get().createXLS(
						recs, RequirementMapping.class,
						"id", "user", "customid", "description", "resolved", "category",
						"response", "doc", "text", "product", "laboreffort", "totalcost", "notes");
				if(file!=null){
					FileResource resource = new FileResource(file);
					Page.getCurrent().open(resource, "Export File", false);
		    		// Finally, removes the temporal file
		    		// file.delete();
				}
            }
        });

        newRecord = new Button(Messages.get().getKey("new"));
        newRecord.addStyleName(ValoTheme.BUTTON_PRIMARY);
        newRecord.setIcon(FontAwesome.PLUS_CIRCLE);
        newRecord.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                viewLogic.newRecord();
            }
        });
                
        multiselect = new CheckBox("Multiselect");
        multiselect.addValueChangeListener(e ->{
        	grid.toggleSelectionMode();
        });
        
        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setSpacing(true);
        topLayout.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
        topLayout.setWidth("100%");
        topLayout.addComponent(filter);
        topLayout.addComponent(projectcb);
        topLayout.addComponent(multiselect);
        topLayout.addComponent(upload);
        topLayout.addComponent(exportBtn);
        topLayout.addComponent(newRecord);
        topLayout.setComponentAlignment(filter, Alignment.MIDDLE_LEFT);
        topLayout.setExpandRatio(upload, 1);
        topLayout.setStyleName("top-bar");
        return topLayout;
    }

    @Override
    public void enter(ViewChangeEvent event) {
        projectcb.Refresh();
    	//viewLogic.setProject(this.getProject());
    	viewLogic.init();
        viewLogic.enter(event.getParameters());
        
        // Creates the AssingTo context menu...
        menu.removeItems();
        if(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.COORD)){
        	// Menu para borrado masivo
        	 menu.addItem("Delete Selection...", e -> {
        		 ConfirmDialog cd = new ConfirmDialog("Do you really want to remove these records?");
        		 cd.setOKAction(new ClickListener() {
                     @Override
                     public void buttonClick(final ClickEvent event) {
             			cd.close();
             			if(grid.getSelectionModel()==SelectionMode.SINGLE)
    		        		viewLogic.deleteRecord(getSelectedRow());
    		        	else{
    		        		Collection<Object> recs = grid.getSelectedRows();
    		        		System.out.println("Eliminando requisitos...");
    		        		for(Object obj : recs){
    		        			Requirement2Entity rec = (Requirement2Entity)obj;
    		        			System.out.println("Eliminando "+rec.toString());
    		        			viewLogic.deleteRecord(rec);
    		        		}
    		        	}
                     }
                 });
        		 getUI().addWindow(cd);
 	        });
        	// Menu para asignar
			try {
				List<UserEntity> users = BltClient.get().getEntities(UserEntity.class,
						EntryPoint.get().getAccessControl().getTokenKey());
				menu.addItem("Assign To...", e -> {
		        	// Do nothing
		        });
		        for(UserEntity user:users){
		        	if(user.getActive()==false) continue;
			        //final MenuItem basic = 
			        menu.addItem(user.getName(), e -> {
			        	if(grid.getSelectionModel()==SelectionMode.SINGLE)
			        		viewLogic.Assignedto(getSelectedRow(), user, (ProjectEntity) projectcb.getValue());
			        	else{
			        		Collection<Object> recs = grid.getSelectedRows();
			        		for(Object obj : recs){
			        			Requirement2Entity rec = (Requirement2Entity)obj;
			        			viewLogic.Assignedto(rec, user, (ProjectEntity) projectcb.getValue());
			        		}
			        	}
			        });
			        //basic.setIcon(FontAwesome.AUTOMOBILE);
		        }
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
    }

    public void showError(String msg) {
        Notification.show(msg, Type.ERROR_MESSAGE);
    }

    public void showSaveNotification(String msg) {
        Notification.show(msg, Type.TRAY_NOTIFICATION);
    }

    public void setNewRecordEnabled(boolean enabled) {
    	newRecord.setEnabled(enabled);
    	upload.setEnabled(enabled);
    }

    public void clearSelection() {
        grid.getSelectionModel().reset();
    }

    public void selectRow(Requirement2Entity row) {
        ((SelectionModel.Single) grid.getSelectionModel()).select(row);
    }

    public Requirement2Entity getSelectedRow() {
        return grid.getSelectedRow();
    }

    public void editRecord(RequirementEntity rec, Requirement2Entity rec2) {
        if (rec != null) {
            form.addStyleName("visible");
            form.setEnabled(true);
            if(currentProject!=null)
            	form.editRecord(rec, rec2, currentProject.getActive());
            else
            	form.editRecord(rec, rec2, false);
        } else {
            form.removeStyleName("visible");
            form.setEnabled(false);
        }
    }

    public void showRecords(Collection<Requirement2Entity> records) {
        grid.setRecords(records);
    }

    public void refreshRecord(Requirement2Entity rec) {
        grid.refresh(rec);
        grid.scrollTo(rec);
    }

    public void removeRecord(Requirement2Entity rec) {
        grid.remove(rec);
    }

    public Long getProject() {
    	if (projectcb.getValue() != null) {
    		currentProject = (ProjectEntity) projectcb.getValue();
        	if(!currentProject.getActive()){
        		showSaveNotification(Messages.get().getKey("projectdisabled"));
        	}
        	return currentProject.getID();
        }
    	return null;
    }

	@Override
	public void editRecord(Requirement2Entity rec) {
		// unused
	}
    
	public void removeRecord(RequirementEntity rec) {
        // unused
    }
	
	public void refreshRecord(RequirementEntity rec) {
		// unused
	}
	
}
