package freewill.nextgen.competicion.salto;

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

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.SaltoEntity;
import freewill.nextgen.data.SaltoIntentoEntity.ResultEnum;
import freewill.nextgen.hmi.utils.Messages;

/**
 * A form for editing a single record.
 *
 * Using responsive layouts, the form can be displayed either sliding out on the
 * side of the view or filling the whole screen - see the theme for the related
 * CSS rules.
 */
@SuppressWarnings("serial")
public class SaltoTrialForm extends SaltoTrialFormDesign {
	
    private SaltoCrudLogic viewLogic;
    private BeanFieldGroup<SaltoEntity> fieldGroup;
    private boolean editable = false;
    
    @SuppressWarnings("rawtypes")
    public SaltoTrialForm(SaltoCrudLogic logic) {
        super();
        addStyleName("product-form");
        this.viewLogic = logic;
        
        salto1.setRequired(true);
        salto2.setRequired(true);
        salto3.setRequired(true);
        for(ResultEnum s:ResultEnum.values()){
        	salto1.addItem(s);
            salto2.addItem(s);
            salto3.addItem(s);
        }
        
        fieldGroup = new BeanFieldGroup<SaltoEntity>(SaltoEntity.class);
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
            	SaltoEntity rec = fieldGroup.getItemDataSource().getBean();
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
            		fieldGroup.discard();
            	removeStyleName("visible");
            }
        });
        
    }

    public void editRecord(SaltoEntity rec, boolean showOnly2Jumps, boolean editable) {
        if (rec == null) {
            rec = new SaltoEntity();
            fieldGroup.setItemDataSource(new BeanItem<SaltoEntity>(rec));
            save.setEnabled(false);
            return;
        }
        fieldGroup.setItemDataSource(new BeanItem<SaltoEntity>(rec));
        this.editable = editable;

        // before the user makes any changes, disable validation error indicator
        // of the product name field (which may be empty)
        //nombre.setValidationVisible(false);

        // Scroll to the top
        // As this is not a Panel, using JavaScript
        String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
        Page.getCurrent().getJavaScript().execute(scrollScript);
        
        salto3.setVisible(!showOnly2Jumps);
        formHasChanged();
    }

    private void formHasChanged() {
        // show validation errors after the user has changed something
        //nombre.setValidationVisible(true);

        /*BeanItem<SaltoEntity> item = fieldGroup.getItemDataSource();
        if (item != null) {
        	SaltoEntity rec = item.getBean();
        	if(rec!=null){
        		//
        	}
        }*/
        dorsal.setEnabled(false);
    	nombre.setEnabled(false);
    	apellidos.setEnabled(false);
    	altura.setEnabled(false);
        save.setEnabled(editable && EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.COORD));
    }
    	
}
