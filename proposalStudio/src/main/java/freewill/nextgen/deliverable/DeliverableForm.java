package freewill.nextgen.deliverable;

import java.util.Collection;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.Page;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Field;
import com.vaadin.ui.Notification;

import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.DeliverableEntity;
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
public class DeliverableForm extends DeliverableFormDesign {

    private DeliverableCrudLogic viewLogic;
    private BeanFieldGroup<DeliverableEntity> fieldGroup;
    private boolean editable = false;

    @SuppressWarnings("rawtypes")
    public DeliverableForm(DeliverableCrudLogic sampleCrudLogic) {
        super();
        addStyleName("product-form");
        viewLogic = sampleCrudLogic;
        
        Description.setRows(8);
        
        fieldGroup = new BeanFieldGroup<DeliverableEntity>(DeliverableEntity.class);
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
            	DeliverableEntity rec = fieldGroup.getItemDataSource().getBean();
            	System.out.println("Deliverable Resolved = "+rec.getResolved());
            }

            @Override
            public void postCommit(CommitEvent commitEvent)
                    throws CommitException {
            	//System.out.println("Entrando en Update Record...");
            	DeliverableEntity rec = fieldGroup.getItemDataSource().getBean();
            	viewLogic.saveRecord(rec, saveAndNext.getValue());
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
                            /*"Please re-check the fields"*/, com.vaadin.ui.Notification.Type.ERROR_MESSAGE);
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
            			DeliverableEntity rec = fieldGroup.getItemDataSource().getBean();
                        viewLogic.deleteRecord(rec);
                    }
                });
            	getUI().addWindow(cd);
            }
        });
    }

    public void editRecord(DeliverableEntity rec, boolean editable) {
        if (rec == null) {
            rec = new DeliverableEntity();
        }
        this.editable = editable;
        
        // Rellenar ComboBox Projects a partir de Company
        Project.removeAllItems();
        try{
	        Collection<ProjectEntity> projects = BltClient.get().getEntities(ProjectEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
	        for (ProjectEntity s : projects) {
	        	Project.addItem(s.getID());
	        	Project.setItemCaption(s.getID(), s.getName());
	        }
	    }
        catch(Exception e){
        	e.printStackTrace();
        }
        
        fieldGroup.setItemDataSource(new BeanItem<DeliverableEntity>(rec));

        // before the user makes any changes, disable validation error indicator
        // of the product name field (which may be empty)
        Name.setValidationVisible(false);

        // Scroll to the top
        // As this is not a Panel, using JavaScript
        String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
        Page.getCurrent().getJavaScript().execute(scrollScript);
    }

    private void formHasChanged() {
        // show validation errors after the user has changed something
        Name.setValidationVisible(true);

        // only products that have been saved should be removable
        boolean canRemoveRecord = false;
        BeanItem<DeliverableEntity> item = fieldGroup.getItemDataSource();
        if (item != null) {
        	DeliverableEntity rec = item.getBean();
        	if(rec!=null)
        		canRemoveRecord = (rec.getID() != null);
        }
        delete.setEnabled(canRemoveRecord && editable);
        save.setEnabled(editable);
        
        Company.setVisible(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.SUPER));
        Resolved.setEnabled(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.COORD));
        
        delete.setVisible(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.USER));
        save.setVisible(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.USER));
    }
}
