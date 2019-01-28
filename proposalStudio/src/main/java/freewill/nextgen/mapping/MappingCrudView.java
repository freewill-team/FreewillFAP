package freewill.nextgen.mapping;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid.SelectionModel;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.data.CrudViewInterface;
import freewill.nextgen.data.FeatureEntity;
import freewill.nextgen.data.MappingEntity;
import freewill.nextgen.data.ProjectEntity;
import freewill.nextgen.data.RequirementMapping;
import freewill.nextgen.hmi.common.ConfirmDialog;
import freewill.nextgen.hmi.common.GenericCombo;
import freewill.nextgen.hmi.common.GenericEntityUpload;
import freewill.nextgen.hmi.utils.ImportFromXls;
import freewill.nextgen.hmi.utils.Messages;
import freewill.nextgen.proposalStudio.EntryPoint;

import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

/**
 * A view for performing create-read-update-delete operations on records.
 *
 * See also {@link StatusCrudLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
@SuppressWarnings("serial")
public class MappingCrudView extends CssLayout implements CrudViewInterface<RequirementMapping> /*View*/ {

    public final String VIEW_NAME = Messages.get().getKey("mappingview.viewname");
    private MappingGrid grid = null;
    private MappingForm form = null;
    private MappingCrudLogic viewLogic = new MappingCrudLogic(this);
    private GenericCombo<ProjectEntity> projectcb = null;
    private SearchForm searchForm = null;
    private Button autoMap = null;
    private Upload upload = null;
    private ProjectEntity currentProject = null;
    private File tempFile = null;
    
    public String getName(){
    	return VIEW_NAME;
    }

    public MappingCrudView() {
    	
    	//System.out.println("Entrando en MappingCrudView, instance = "+new Random().nextInt());
    	
        setSizeFull();
        addStyleName("crud-view");
        HorizontalLayout topLayout = createTopBar();

        grid = new MappingGrid();
        grid.addSelectionListener(new SelectionListener() {

            @Override
            public void select(SelectionEvent event) {
                viewLogic.rowSelected(grid.getSelectedRow());
            }
        });

        form = new MappingForm(viewLogic);
        searchForm = new SearchForm(viewLogic);

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
        addComponent(searchForm);

        viewLogic.init();
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
        
        //label = new Label(" ");
        projectcb = new GenericCombo<ProjectEntity>(ProjectEntity.class);
        
        projectcb.addValueChangeListener(new ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
            	if (projectcb.getValue() != null) {
            		viewLogic.setProject(getProject());
                }
            }
        });
        
        autoMap = new Button(Messages.get().getKey("automapping"));
        autoMap.addStyleName(ValoTheme.BUTTON_PRIMARY);
        autoMap.setIcon(FontAwesome.COMPRESS);
        autoMap.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	ConfirmDialog cd = new ConfirmDialog(
            			"This process will try to map all pending requirements "
            			+"\nand it cannot be undone. "
            			+"\nDo you really want to proceed?");
            	cd.setOKAction(new ClickListener() {
                    @SuppressWarnings("unchecked")
					@Override
                    public void buttonClick(final ClickEvent event) {
                    	//System.out.println("Auto Mapping will be invoked...");
            			cd.close();
            			viewLogic.autoMapping((Collection<RequirementMapping>) grid.getContainerDataSource().getItemIds());
                    }
                });
            	getUI().addWindow(cd);
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
            			showSaveNotification("File uploaded successfully.");
	            		
	            		List<RequirementMapping> list = 
	            				ImportFromXls.get().getList(tempFile, RequirementMapping.class);
	            		// Open modal windows with import results
	            		GenericEntityUpload<RequirementMapping> mapUpload = 
	            				new GenericEntityUpload<RequirementMapping>(RequirementMapping.class, list, 
	            				"id", "user", "customid", "description", "resolved",
	            				"response", "doc", "text", "laboreffort", "totalcost", "notes");
	            		mapUpload.setOKAction(new ClickListener() {
	                        @Override
	                        public void buttonClick(final ClickEvent event) {
	                        	mapUpload.close();
	                			// actualiza solo los datos seleccionados
	                        	for(RequirementMapping rec:mapUpload.getSelectedRows()){
	                        		//System.out.println(rec.toString());
	                        		viewLogic.saveRecord(rec);
	                        	}
	                        }
	                    });
        				getUI().addWindow(mapUpload);
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
                
        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setSpacing(true);
        topLayout.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
        topLayout.setWidth("100%");
        topLayout.addComponent(filter);
        topLayout.addComponent(projectcb);
        topLayout.addComponent(upload);
        topLayout.addComponent(autoMap);
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
        closeSearchForm();
    }

    public void showError(String msg) {
        Notification.show(msg, Type.ERROR_MESSAGE);
    }

    public void showSaveNotification(String msg) {
        Notification.show(msg, Type.TRAY_NOTIFICATION);
    }

    public void setNewRecordEnabled(boolean enabled) {
    	// unused
    }

    public void clearSelection() {
        grid.getSelectionModel().reset();
    }

    public void selectRow(RequirementMapping row) {
        ((SelectionModel.Single) grid.getSelectionModel()).select(row);
    }

    public RequirementMapping getSelectedRow() {
        return grid.getSelectedRow();
    }

    public void editRecord(MappingEntity map, RequirementMapping rec) {
        if (rec != null) {
            form.addStyleName("visible");
            form.setEnabled(true);
            if(currentProject!=null)
            	form.editRecord(map, rec, currentProject.getActive());
            else
            	form.editRecord(map, rec, false);
        } else {
            form.removeStyleName("visible");
            form.setEnabled(false);
        }
    }
    
    public void showRecords(Collection<RequirementMapping> records) {
        grid.setRecords(records);
    }

    public void refreshRecord(RequirementMapping rec) {
        grid.refresh(rec);
        grid.scrollTo(rec);
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

	public void openSearchForm(Long project) {
		searchForm.enter(project);
	}
	
	public void closeSearchForm() {
		searchForm.close();
	}

	public void selectFeature(FeatureEntity rec) {
		System.out.println("MappingCrudView - Feature Selected = "+rec.getID());
		form.selectFeature(rec);
	}

	public void setAutoMappingEnabled(boolean b) {
		autoMap.setEnabled(b);
	}
	
	public void setUploadEnabled(boolean enabled) {
		upload.setEnabled(enabled);
	}

	@Override
	public void editRecord(RequirementMapping rec) {
		// unused
	}

	@Override
	public void removeRecord(RequirementMapping rec) {
		// unused
	}
	
}
