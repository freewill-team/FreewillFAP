package freewill.nextgen.support;

import java.util.Locale;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Field;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.SupportEntity;
import freewill.nextgen.data.SupportEntity.SeverityEnum;
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
public class SupportForm extends SupportFormDesign {

    private SupportCrudLogic viewLogic;
    private BeanFieldGroup<SupportEntity> fieldGroup;

    @SuppressWarnings("rawtypes")
	public SupportForm(SupportCrudLogic sampleCrudLogic) {
        super();
        addStyleName("product-form");
        viewLogic = sampleCrudLogic;
        
        for (SeverityEnum s : SeverityEnum.values()) {
            Severity.addItem(s);
        }
        
        Details.setRows(5);
        Comments.setRows(10);
        Comments.setEnabled(false);
        Created.setResolution(Resolution.DAY);
        Created.setLocale(new Locale(EntryPoint.get().getAccessControl().getLocale()));
        Created.setEnabled(false);

        fieldGroup = new BeanFieldGroup<SupportEntity>(SupportEntity.class);
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
        Details.setCaption(Messages.get().getKey("details"));

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
            	SupportEntity rec = fieldGroup.getItemDataSource().getBean();
            	if(rec.getID()==null && rec.getDescription().equals("")){
            		// Set issue Description as it is a new record
            		rec.setDescription(Details.getValue());
            		viewLogic.saveRecord(rec, false);
            		Details.setValue("");
            	}
            	else if(!Details.getValue().equals("")){
            		// Add additional details to this support case
	            	viewLogic.addComment(rec, Details.getValue());
	            	Details.setValue("");
            	}
            	formHasChanged();
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
            	ConfirmDialog cd = new ConfirmDialog("Do you really want to close these record?");
            	cd.setOKAction(new ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
            			cd.close();
            			SupportEntity rec = fieldGroup.getItemDataSource().getBean();
                        //viewLogic.deleteRecord(rec);
            			rec.setResolved(true);
            			viewLogic.saveRecord(rec, false);
                    }
                });
            	getUI().addWindow(cd);
            }
        });
           
    }

    public void editRecord(SupportEntity rec) {
        if (rec == null) {
            rec = new SupportEntity();
        }
        fieldGroup.setItemDataSource(new BeanItem<SupportEntity>(rec));

        // before the user makes any changes, disable validation error indicator
        // of the product name field (which may be empty)
        //Name.setValidationVisible(false);
        Id.setValue("ID: "+rec.getID()+"/"+rec.getDescription());
        Created.setEnabled(false);
        Comments.setEnabled(false);
        
        if(!EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.SUPER)){
        	save.setEnabled(!rec.getResolved());
        }
        
        // Scroll to the top
        // As this is not a Panel, using JavaScript
        String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
        Page.getCurrent().getJavaScript().execute(scrollScript);
        
        formHasChanged();
    }

    private void formHasChanged() {
        // show validation errors after the user has changed something
        //Name.setValidationVisible(true);

        // only products that have been saved should be removable
        boolean canRemoveRecord = false;
        BeanItem<SupportEntity> item = fieldGroup.getItemDataSource();
        if (item != null) {
        	SupportEntity rec = item.getBean();
        	if(rec!=null){
        		canRemoveRecord = (rec.getID() != null);
        	}
        	if(rec.getDescription().equals(""))
        		Details.setCaption(Messages.get().getKey("details"));
        	else
        		Details.setCaption(Messages.get().getKey("moredetails"));
        }
        
        delete.setEnabled(canRemoveRecord);
        delete.setVisible(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.SUPER));
        User.setVisible(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.SUPER));
        Resolved.setEnabled(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.SUPER));
        Comments.setEnabled(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.SUPER));
        Severity.setEnabled(!canRemoveRecord || EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.SUPER));
        Created.setEnabled(false);
    }
    
}
