package freewill.nextgen.mail;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Field;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.common.entities.UserEntity;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.common.entities.MailServerEntity;
import freewill.nextgen.genericCrud.GenericGrid;
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
public class MailServerForm extends MailServerFormDesign {

    private MailServerCrudLogic viewLogic;
    private BeanFieldGroup<MailServerEntity> fieldGroup;
    private GenericGrid<UserEntity> available = null;
    private GenericGrid<UserEntity> assigned = null;
    private Logger log = Logger.getLogger(MailServerForm.class);

    @SuppressWarnings("rawtypes")
    public MailServerForm(MailServerCrudLogic sampleCrudLogic) {
        super();
        addStyleName("product-form");
        viewLogic = sampleCrudLogic;

        fieldGroup = new BeanFieldGroup<MailServerEntity>(MailServerEntity.class);
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
            	System.out.println("Entrando en preCommit...");
            }
            
            @Override
            public void postCommit(CommitEvent commitEvent)
                    throws CommitException {
            	System.out.println("Entrando en postCommit...");
            	
            	MailServerEntity rec = fieldGroup.getItemDataSource().getBean();
            	
            	if(viewLogic!=null)
            		viewLogic.saveRecord(rec, false);
            	else 
            		removeStyleName("visible");
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
            	ConfirmDialog cd = new ConfirmDialog(Messages.get().getKey("reallyremovequestion"));
            	cd.setOKAction(new ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
            			cd.close();
            			MailServerEntity rec = fieldGroup.getItemDataSource().getBean();
            			if(viewLogic!=null){
            				viewLogic.deleteRecord(rec);
            				//delete.setEnabled(false);
            			}
                    }
                });
            	getUI().addWindow(cd);
            }
        });
        
        available = new GenericGrid<UserEntity>(UserEntity.class, "ID", "name", "role");
        available.setStyleName(ValoTheme.TABLE_SMALL);
        AvailableLayout.addComponent(available);
        AvailableLayout.setCaption(Messages.get().getKey("availableusers"));
        AvailableLayout.setHeight("150px");
        
        assigned = new GenericGrid<UserEntity>(UserEntity.class, "ID", "name", "role");
        assigned.setStyleName(ValoTheme.TABLE_SMALL);
        AssignedLayout.addComponent(assigned);
        AssignedLayout.setCaption(Messages.get().getKey("assignedusers"));
        AssignedLayout.setHeight("150px");
        
        add.setIcon(FontAwesome.ARROW_DOWN);
        add.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	UserEntity item = available.getSelectedRow();
            	if(item!=null) {
            		//System.out.println("");
            		available.remove(item);
            		assigned.refresh(item);
            		assigned.scrollTo(item);
            		try{
            			MailServerEntity rec = fieldGroup.getItemDataSource().getBean();
	            		List<UserEntity> dests = rec.getDestinations();
	            		if(dests==null)
	            			dests = new ArrayList<UserEntity>();
	            		dests.add(item);
	            		BltClient.get().updateEntity(rec, MailServerEntity.class,
	            				EntryPoint.get().getAccessControl().getTokenKey());
            		}
            		catch(Exception e){
            			log.error(e.getMessage());
            		}
            	}
            }
        });
        
        del.setIcon(FontAwesome.ARROW_UP);
        del.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	UserEntity item = (UserEntity) assigned.getSelectedRow();
            	if(item!=null) {
            		//System.out.println("");
            		assigned.remove(item);
            		available.refresh(item);
            		available.scrollTo(item);
            		try{
            			MailServerEntity rec = fieldGroup.getItemDataSource().getBean();
	            		List<UserEntity> dests = rec.getDestinations();
	            		if(dests!=null){
		            		dests.remove(item);
		            		BltClient.get().updateEntity(rec, MailServerEntity.class,
		            				EntryPoint.get().getAccessControl().getTokenKey());
	            		}
            		}
            		catch(Exception e){
            			log.error(e.getMessage());
            		}
            	}
            }
        });
        
    }

	public void editRecord(MailServerEntity rec) {
        if (rec == null) {
            rec = new MailServerEntity();
        }
        fieldGroup.setItemDataSource(new BeanItem<MailServerEntity>(rec));
        
        // before the user makes any changes, disable validation error indicator
        // of the product name field (which may be empty)
        Label.setValidationVisible(false);

        // Scroll to the top
        // As this is not a Panel, using JavaScript
        String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
        Page.getCurrent().getJavaScript().execute(scrollScript);
        
        try{
	        available.setRecords(BltClient.get().getEntities(UserEntity.class,
	        		EntryPoint.get().getAccessControl().getTokenKey()));
	        if(rec.getDescription()!=null){
	        	assigned.setRecords(rec.getDestinations());
	        	for(UserEntity pro:rec.getDestinations())
	        		available.remove(pro);
	        }
        }
		catch(Exception e){
			log.error(e.getMessage());
		}
        
        formHasChanged();
    }

    private void formHasChanged() {
        // show validation errors after the user has changed something
        Label.setValidationVisible(true);
        
        // only products that have been saved should be removable
        boolean canRemoveRecord = false;
        //boolean canSaveRecord = false;
        BeanItem<MailServerEntity> item = fieldGroup.getItemDataSource();
        if (item != null) {
        	MailServerEntity rec = item.getBean();
        	if(rec!=null){
        		canRemoveRecord = (rec.getId() != null);
        	}
        }
        delete.setEnabled(canRemoveRecord);
        Company.setVisible(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.SUPER));
        //save.setEnabled(canSaveRecord);
        add.setEnabled(canRemoveRecord);
        del.setEnabled(canRemoveRecord);
    }
    
}
