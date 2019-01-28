package freewill.nextgen.file;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Field;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.FileEntity;
import freewill.nextgen.data.ProjectEntity;
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
public class FileForm extends FileFormDesign {

    private FileCrudLogic viewLogic;
    private BeanFieldGroup<FileEntity> fieldGroup;
    FileDownloader fileDownloader = null;

    @SuppressWarnings("rawtypes")
    public FileForm(FileCrudLogic sampleCrudLogic) {
        super();
        addStyleName("product-form");
        viewLogic = sampleCrudLogic;
        
        Description.setRows(4);
        download.setIcon(FontAwesome.DOWNLOAD);
        byte[] data = new byte[0];
        StreamResource resource = new StreamResource(
        		new StreamResource.StreamSource() {
        			@Override
        			public InputStream getStream() {
        				return new ByteArrayInputStream(data);
        			}
        		}, "Dummy");
        fileDownloader = new FileDownloader(resource);
        fileDownloader.extend(download);

        fieldGroup = new BeanFieldGroup<FileEntity>(FileEntity.class);
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
            	//FileEntity rec = fieldGroup.getItemDataSource().getBean();
            }

            @Override
            public void postCommit(CommitEvent commitEvent)
                    throws CommitException {
            	//System.out.println("Entrando en Update Record...");
            	FileEntity rec = fieldGroup.getItemDataSource().getBean();
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
                    Notification n = new Notification( e.getMessage()
                            /*"Please re-check the fields"*/, Type.ERROR_MESSAGE);
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
            	ConfirmDialog cd = new ConfirmDialog("Do you really want to remove this record?");
            	cd.setOKAction(new ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
            			cd.close();
            			FileEntity rec = fieldGroup.getItemDataSource().getBean();
                        viewLogic.deleteRecord(rec);
                    }
                });
            	getUI().addWindow(cd);
            }
        });
    }

    public void editRecord(FileEntity rec) {
        if (rec == null) {
            rec = new FileEntity();
        }
        
        // Rellenar ComboBox Projects a partir de Company
        Project.removeAllItems();
        try{
	        Collection<ProjectEntity> projects = BltClient.get().getEntities(ProjectEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
	        for (ProjectEntity s : projects) {
	        	Project.addItem(s.getID());
	        	Project.setItemCaption(s.getID(), s.getName());
	        }
	        
	        if(rec.getID()!=null){
		        FileEntity res = viewLogic.findRecord(rec.getID());
		        rec.setImage(res.getImage());
	        }
	    }
        catch(Exception e){
        	e.printStackTrace();
        }
        
        fieldGroup.setItemDataSource(new BeanItem<FileEntity>(rec));

        // before the user makes any changes, disable validation error indicator
        // of the product name field (which may be empty)
        Name.setValidationVisible(false);

        // Scroll to the top
        // As this is not a Panel, using JavaScript
        String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
        Page.getCurrent().getJavaScript().execute(scrollScript);
        
        // Other
        download.setEnabled(false);
        final byte[] data = rec.getImage();
    	if(data!=null && data.length>0){
    		StreamResource resource = new StreamResource(
        		new StreamResource.StreamSource() {
        			@Override
        			public InputStream getStream() {
        				return new ByteArrayInputStream(data);
        			}
        		}, rec.getName());
        	download.setEnabled(true);
        	fileDownloader.setFileDownloadResource(resource);
    	}
    	
    }

    private void formHasChanged() {
        // show validation errors after the user has changed something
        Name.setValidationVisible(true);
        
        // only products that have been saved should be removable
        boolean canRemoveRecord = false;
        BeanItem<FileEntity> item = fieldGroup.getItemDataSource();
        if (item != null) {
        	FileEntity rec = item.getBean();
        	if(rec!=null)
        		canRemoveRecord = (rec.getID() != null);
        	// enables download boton only if file exists
            final byte[] data = rec.getImage();
        	download.setEnabled(data!=null && data.length>0);
        }
        delete.setEnabled(canRemoveRecord);
        Company.setVisible(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.SUPER));
        
        delete.setVisible(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.COORD));
        save.setVisible(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.COORD));
    }
}
