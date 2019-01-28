package freewill.nextgen.competicion.jam;

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
import freewill.nextgen.data.JamShowEntity;
import freewill.nextgen.hmi.utils.Messages;

/**
 * A form for editing a single record.
 *
 * Using responsive layouts, the form can be displayed either sliding out on the
 * side of the view or filling the whole screen - see the theme for the related
 * CSS rules.
 */
@SuppressWarnings("serial")
public class JamShowForm extends JamShowFormDesign {
	
    private JamCrudLogic viewLogic;
    private BeanFieldGroup<JamShowEntity> fieldGroup;
    
    @SuppressWarnings("rawtypes")
    public JamShowForm(JamCrudLogic logic) {
        super();
        //addStyleName("product-form");
        this.viewLogic = logic;
        this.setMargin(true);
        this.setSpacing(true);
        
        penalizaciones.setRequired(true);
        tecnicaJuez1.setRequired(true);
        artisticaJuez1.setRequired(true);
        tecnicaJuez2.setRequired(true);
        artisticaJuez2.setRequired(true);
        tecnicaJuez3.setRequired(true);
        artisticaJuez3.setRequired(true);
   
        save.setEnabled(false);
        
        fieldGroup = new BeanFieldGroup<JamShowEntity>(JamShowEntity.class);
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
            	JamShowEntity rec = fieldGroup.getItemDataSource().getBean();
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
        
    }
    

    public void editRecord(JamShowEntity rec) {
        if (rec == null) {
        	//TODO MMFL esto no deberia ocurrir borrar
            rec = new JamShowEntity();
            fieldGroup.setItemDataSource(new BeanItem<JamShowEntity>(rec));
            save.setEnabled(false);
            return;
        }
        fieldGroup.setItemDataSource(new BeanItem<JamShowEntity>(rec));

        // before the user makes any changes, disable validation error indicator
        // of the product name field (which may be empty)
        //nombre.setValidationVisible(false);

        // Scroll to the top
        // As this is not a Panel, using JavaScript
        String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
        Page.getCurrent().getJavaScript().execute(scrollScript);
        
        formHasChanged();
    }

    private void formHasChanged() {
        // show validation errors after the user has changed something
        //nombre.setValidationVisible(true);
        /*
        BeanItem<JamShowEntity> item = fieldGroup.getItemDataSource();
        if (item != null) {
        	JamShowEntity rec = item.getBean();
        	if(rec!=null){
        		//
        	}
        }
        */
        
        save.setEnabled(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.COORD));
    }
    		
}
