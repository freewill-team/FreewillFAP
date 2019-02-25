package freewill.nextgen.club;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.ClubEntity;
import freewill.nextgen.genericCrud.CustomFormInterface;
import freewill.nextgen.genericCrud.GenericCrudLogic;
import freewill.nextgen.hmi.common.ConfirmDialog;
import freewill.nextgen.hmi.utils.Messages;

/**
 * A form for editing a single record.
 *
 * Using responsive layouts, the form can be displayed either sliding out on the
 * side of the view or filling the whole screen - see the theme for the related
 * CSS rules.
 */
@SuppressWarnings("serial")
public class ClubForm extends ClubFormDesign implements CustomFormInterface<ClubEntity> {
	
    private GenericCrudLogic<ClubEntity> viewLogic;
    private BeanFieldGroup<ClubEntity> fieldGroup;

    @SuppressWarnings("rawtypes")
    public ClubForm() {
        super();
        addStyleName("product-form");

        image.setSizeFull();
        Add.addStyleName(ValoTheme.BUTTON_SMALL);
        Del.addStyleName(ValoTheme.BUTTON_SMALL);
        imageLayout.setExpandRatio(imageLayout2, 1L);
        imageLayout.setExpandRatio(image, 2L);
        
        fieldGroup = new BeanFieldGroup<ClubEntity>(ClubEntity.class);
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

        fieldGroup.addCommitHandler(new CommitHandler() {

            @Override
            public void preCommit(CommitEvent commitEvent)
                    throws CommitException {
            	//System.out.println("Entrando en Commit...");
            }

            @Override
            public void postCommit(CommitEvent commitEvent)
                    throws CommitException {
            	//System.out.println("Entrando en Update Record...");
            	ClubEntity rec = fieldGroup.getItemDataSource().getBean();
            	if(viewLogic!=null){
            		viewLogic.saveRecord(rec);
            	}
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
                if(viewLogic!=null)
            		viewLogic.cancelRecord();
            	else
            		removeStyleName("visible");
            }
        });

        delete.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	ConfirmDialog cd = new ConfirmDialog("Do you really want to remove this record?");
            	cd.setOKAction(new ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
            			cd.close();
            			ClubEntity rec = fieldGroup.getItemDataSource().getBean();
            			if(viewLogic!=null){
            				viewLogic.deleteRecord(rec);
            			}
                    }
                });
            	getUI().addWindow(cd);
            }
        });
        
        Add.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	getUI().addWindow(new ClubLoadImage(ClubForm.this));
            }
        });
        
        Del.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	ClubEntity rec = fieldGroup.getItemDataSource().getBean();
            	rec.setImage(new byte[0]);
            	image.setSource(null);
            	fieldGroup.setItemDataSource(new BeanItem<ClubEntity>(rec));
            }
        });
        
    }

    public void editRecord(ClubEntity rec) {
        if (rec == null) {
            rec = new ClubEntity();
            fieldGroup.setItemDataSource(new BeanItem<ClubEntity>(rec));
            return;
        }
        fieldGroup.setItemDataSource(new BeanItem<ClubEntity>(rec));

        // before the user makes any changes, disable validation error indicator
        // of the product name field (which may be empty)
        nombre.setValidationVisible(false);

        // Scroll to the top
        // As this is not a Panel, using JavaScript
        String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
        Page.getCurrent().getJavaScript().execute(scrollScript);
        
        // Company Logo
        image.setSource(null);
        byte[] data = rec.getImage();
    	if(data!=null && data.length>0){
        	StreamResource resource = new StreamResource(
        			new StreamResource.StreamSource() {
        				@Override
        				public InputStream getStream() {
        					return new ByteArrayInputStream(data);
        				}
        	       }, rec.getImagename()/*rec.getName()+".jpg"*/);
        	image.setSource(resource);
        	image.setWidthUndefined();
        	//image.setHeightUndefined();
    	}
        
        formHasChanged();
    }

    private void formHasChanged() {
        // show validation errors after the user has changed something
        nombre.setValidationVisible(true);

        // only products that have been saved should be removable
        boolean canRemoveRecord = false;
        BeanItem<ClubEntity> item = fieldGroup.getItemDataSource();
        if (item != null) {
        	ClubEntity rec = item.getBean();
        	if(rec!=null){
        		canRemoveRecord = (rec.getId() != null);
        	}
        }
        delete.setEnabled(canRemoveRecord);
        save.setEnabled(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.COORD));
        delete.setVisible(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.ADMIN));
    }
    
    @Override
	public void setLogic(GenericCrudLogic<ClubEntity> logic) {
		viewLogic = logic;
	}

	@Override
	public Component getComponent() {
		return this;
	}

	public void addImage(File tempFile) {
		try {
    		FileResource resource = new FileResource(tempFile);
            image.setSource(resource);
        	// first converts File to byte[]
    		byte[] array = Files.readAllBytes(tempFile.toPath());
        	// then saves image into Company
    		ClubEntity rec = fieldGroup.getItemDataSource().getBean();
        	rec.setImage(array);
        	rec.setImagename(tempFile.getName());
        	fieldGroup.setItemDataSource(new BeanItem<ClubEntity>(rec));
        	// tempFile.delete();
    	} catch (IOException e) {
			e.printStackTrace();
			Notification n = new Notification(
                    "Error uploading image file", Type.ERROR_MESSAGE);
            n.setDelayMsec(500);
            n.show(getUI().getPage());
		}
	}
		
}
