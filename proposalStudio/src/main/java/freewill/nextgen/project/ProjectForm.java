package freewill.nextgen.project;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Locale;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Field;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.common.entities.UserEntity.LanguageEnum;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.ProjectEntity;
import freewill.nextgen.data.ProjectEntity.ProjectStatusEnum;
import freewill.nextgen.hmi.common.ConfirmDialog;
import freewill.nextgen.hmi.utils.Messages;
import freewill.nextgen.proposalStudio.EntryPoint;

/**
 * A form for editing a single record.
 *
 * Using responsive layouts, the form can be displayed either sliding out on the
 * side of the view or filling the whole screen - see the theme for the related
 * CSS rules.
 */
@SuppressWarnings("serial")
public class ProjectForm extends ProjectFormDesign {

    private ProjectCrudLogic viewLogic;
    private BeanFieldGroup<ProjectEntity> fieldGroup;

    @SuppressWarnings("rawtypes")
	public ProjectForm(ProjectCrudLogic sampleCrudLogic) {
        super();
        addStyleName("product-form");
        viewLogic = sampleCrudLogic;
        
        for (LanguageEnum s : LanguageEnum.values()) {
            Language.addItem(s);
        }
        
        for (ProjectStatusEnum s : ProjectStatusEnum.values()) {
            Status.addItem(s);
        }
        
        Deliverydate.setResolution(Resolution.DAY);
        Deliverydate.setLocale(new Locale(EntryPoint.get().getAccessControl().getLocale()));

        fieldGroup = new BeanFieldGroup<ProjectEntity>(ProjectEntity.class);
        fieldGroup.bindMemberFields(this);

        // perform validation and enable/disable buttons while editing
        ValueChangeListener valueListener = new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                formHasChanged();
            }
        };
        for (Field f : fieldGroup.getFields()) {
            f.addValueChangeListener(valueListener);
            f.setCaption(Messages.get().getKey(f.getCaption())); // Translations
        }
        
        image.setSizeFull();
        Add.addStyleName(ValoTheme.BUTTON_SMALL);
        Del.addStyleName(ValoTheme.BUTTON_SMALL);
        imageLayout.setExpandRatio(imageLayout2, 1L);
        imageLayout.setExpandRatio(image, 2L);

        fieldGroup.addCommitHandler(new CommitHandler() {

            @Override
            public void preCommit(CommitEvent commitEvent)
                    throws CommitException {
            	//System.out.println("Entrando en Commit...");
            	Collection IDs = List.getItemIds(); 
            	String valores="";
            	int i = 0;
            	for(Object s:IDs) {
            		if(i==0)
            			valores+=s;
            		else
            			valores+=","+s;
            		i++;
            	}
            	Answers.setValue(valores);
            }

            @Override
            public void postCommit(CommitEvent commitEvent)
                    throws CommitException {
            	//System.out.println("Entrando en Update Record...");
            	ProjectEntity rec = fieldGroup.getItemDataSource().getBean();
            	viewLogic.saveRecord(rec, false);
            }
        });

        save.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                try {
                    fieldGroup.commit();
                    // only if validation succeeds
                } catch (CommitException e) {
                	System.out.println(e.getMessage());
                    Notification n = new Notification(
                            "Please re-check the fields", Type.ERROR_MESSAGE);
                    n.setDelayMsec(500);
                    n.show(getUI().getPage());
                }
            }
        });

        cancel.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                viewLogic.cancelRecord();
            }
        });

        delete.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	ConfirmDialog cd = new ConfirmDialog("Do you really want to remove this record?"
            			+"\n(This action will also remove related files and requirements)");
            	cd.setOKAction(new ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
            			cd.close();
            			ProjectEntity rec = fieldGroup.getItemDataSource().getBean();
                        viewLogic.deleteRecord(rec);
                    }
                });
            	getUI().addWindow(cd);
            }
        });
        
        Add.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	getUI().addWindow(new ProjectLoadImage(ProjectForm.this));
            }
        });
        
        Del.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	ProjectEntity rec = fieldGroup.getItemDataSource().getBean();
            	rec.setLogo(new byte[0]);
            	image.setSource(null);
            	fieldGroup.setItemDataSource(new BeanItem<ProjectEntity>(rec));
            }
        });
                
        List.setRows(3);
        Answers.setVisible(false);
        //AddResp = new Button("Add New");
        AddResp.addStyleName(ValoTheme.BUTTON_SMALL);
        AddResp.setIcon(FontAwesome.ARROW_RIGHT);
        //DelResp = new Button("Delete");
        DelResp.addStyleName(ValoTheme.BUTTON_SMALL);
        DelResp.setIcon(FontAwesome.ARROW_LEFT);
        
        Answers1Layout.setCaption(Messages.get().getKey("answers"));
        Answers1Layout.setExpandRatio(Answer, 2L);
        Answers1Layout.setExpandRatio(Answers2Layout, 1L);
        Answers1Layout.setExpandRatio(List, 2L);
        
        AddResp.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	if(Answer.getValue()!=""){
            		List.addItem(Answer.getValue());
            		Answer.setValue("");
            	}
            }
        });
        
        DelResp.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	if(List.getValue()!=null){
            		List.removeItem(List.getValue());
            	}
            }
        });
        
    }

    public void editRecord(ProjectEntity rec) {
        if (rec == null) {
            rec = new ProjectEntity();
        }
        
        if(rec.getID()!=null){
	        ProjectEntity res = viewLogic.findRecord(rec.getID());
	        rec.setLogo(res.getLogo());
	    }
        
        fieldGroup.setItemDataSource(new BeanItem<ProjectEntity>(rec));

        // before the user makes any changes, disable validation error indicator
        // of the product name field (which may be empty)
        Name.setValidationVisible(false);

        // Scroll to the top
        // As this is not a Panel, using JavaScript
        String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
        Page.getCurrent().getJavaScript().execute(scrollScript);
        
        // Other
        image.setSource(null);
        byte[] data = rec.getLogo();
    	if(data!=null && data.length>0){
        	StreamResource resource = new StreamResource(
        			new StreamResource.StreamSource() {
        				@Override
        				public InputStream getStream() {
        					return new ByteArrayInputStream(data);
        				}
        	       }, rec.getImagename()/*rec.getName()+".jpg"*/);
        	image.setSource(resource);
    	}
    	
    	String[] valores = rec.getAnswers().split(",");
    	List.removeAllItems();
    	for(String s:valores){
    		List.addItem(s);
    	}
    	
    }

    private void formHasChanged() {
        // show validation errors after the user has changed something
        Name.setValidationVisible(true);

        // only products that have been saved should be removable
        boolean canRemoveRecord = false;
        BeanItem<ProjectEntity> item = fieldGroup.getItemDataSource();
        if (item != null) {
        	ProjectEntity rec = item.getBean();
        	if(rec!=null){
        		canRemoveRecord = (rec.getID() != null);
        	}
        }
        delete.setEnabled(canRemoveRecord);
        Company.setVisible(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.SUPER));
        
        delete.setVisible(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.COORD));
        save.setVisible(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.COORD));
    }
    
    public void addImage(File tempFile) {
		try {
    		FileResource resource = new FileResource(tempFile);
            image.setSource(resource);
        	// first converts File to byte[]
    		byte[] array = Files.readAllBytes(tempFile.toPath());
        	// then saves image into Project
    		ProjectEntity rec = fieldGroup.getItemDataSource().getBean();
        	rec.setLogo(array);
        	rec.setImagename(tempFile.getName());
        	fieldGroup.setItemDataSource(new BeanItem<ProjectEntity>(rec));
        	// tempFile.delete();
    	} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
}
