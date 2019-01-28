package freewill.nextgen.config;

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
import com.vaadin.ui.Notification.Type;

import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.ConfigEntity;
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
public class ConfigForm extends ConfigFormDesign {

    private ConfigCrudLogic viewLogic;
    private BeanFieldGroup<ConfigEntity> fieldGroup;

    @SuppressWarnings("rawtypes")
    public ConfigForm(ConfigCrudLogic sampleCrudLogic) {
        super();
        addStyleName("product-form");
        viewLogic = sampleCrudLogic;

        fieldGroup = new BeanFieldGroup<ConfigEntity>(ConfigEntity.class);
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
            	//ConfigEntity rec = fieldGroup.getItemDataSource().getBean();
            }

            @Override
            public void postCommit(CommitEvent commitEvent)
                    throws CommitException {
            	//System.out.println("Entrando en Update Record...");
            	ConfigEntity rec = fieldGroup.getItemDataSource().getBean();
            	viewLogic.saveRecord(rec);
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
            			ConfigEntity rec = fieldGroup.getItemDataSource().getBean();
                        viewLogic.deleteRecord(rec);
                    }
                });
            	getUI().addWindow(cd);
            }
        });
    }

    public void editRecord(ConfigEntity rec) {
        if (rec == null) {
            rec = new ConfigEntity();
        }
        
        fieldGroup.setItemDataSource(new BeanItem<ConfigEntity>(rec));

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
        BeanItem<ConfigEntity> item = fieldGroup.getItemDataSource();
        if (item != null) {
        	ConfigEntity rec = item.getBean();
        	if(rec!=null)
        		canRemoveRecord = (rec.getID() != null);
        }
        delete.setEnabled(canRemoveRecord);
        
        delete.setVisible(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.SUPER));
        save.setVisible(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.SUPER));
    }
}
