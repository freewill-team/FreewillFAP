package freewill.nextgen.circuito;

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
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.CircuitoEntity;
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
public class CircuitoForm extends CircuitoFormDesign implements CustomFormInterface<CircuitoEntity> {

    private BeanFieldGroup<CircuitoEntity> fieldGroup;
    private GenericCrudLogic<CircuitoEntity> viewLogic;
    
    @SuppressWarnings("rawtypes")
    public CircuitoForm() {
        super();
        addStyleName("product-form");
        
        nombre.setRequired(true);
        temporada.setRequired(true);
        
        fieldGroup = new BeanFieldGroup<CircuitoEntity>(CircuitoEntity.class); 
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
            	//System.out.println("Entrando en postCommit...");
				CircuitoEntity rec = fieldGroup.getItemDataSource().getBean();
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
                	e.printStackTrace();
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
            			CircuitoEntity rec = fieldGroup.getItemDataSource().getBean();
            			if(viewLogic!=null){
            				viewLogic.deleteRecord(rec);
            			}
                    }
                });
            	getUI().addWindow(cd);
            }
        });
        
    }
    
	public void editRecord(CircuitoEntity rec) {
        if (rec == null) {
            rec = new CircuitoEntity();
            fieldGroup.setItemDataSource(new BeanItem<CircuitoEntity>(rec));
            return;
        }
        fieldGroup.setItemDataSource(new BeanItem<CircuitoEntity>(rec));
        
        // before the user makes any changes, disable validation error indicator
        // of the product name field (which may be empty)
        nombre.setValidationVisible(false);
        
        // Scroll to the top
        // As this is not a Panel, using JavaScript
        String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
        Page.getCurrent().getJavaScript().execute(scrollScript);
        
        formHasChanged();
    }
	
	private void formHasChanged() {
        // show validation errors after the user has changed something
		nombre.setValidationVisible(true);

        // only products that have been saved should be removable
        boolean canRemoveRecord = false;
        BeanItem<CircuitoEntity> item = fieldGroup.getItemDataSource();
        if (item != null) {
        	CircuitoEntity rec = item.getBean();
        	if(rec!=null){
        		canRemoveRecord = (rec.getId() != null);
        	}
        }
        delete.setEnabled(canRemoveRecord);
        delete.setVisible(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.ADMIN));
    }

	@Override
	public void setLogic(GenericCrudLogic<CircuitoEntity> logic) {
		viewLogic = logic;
	}

	@Override
	public Component getComponent() {
		return this;
	}
    
}
