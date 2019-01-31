package freewill.nextgen.competicion.speed;

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
import freewill.nextgen.data.ConfigEntity.ConfigItemEnum;
import freewill.nextgen.data.SpeedTimeTrialEntity;
import freewill.nextgen.data.SpeedTimeTrialEntity.RondaEnum;
import freewill.nextgen.hmi.utils.Messages;

/**
 * A form for editing a single record.
 *
 * Using responsive layouts, the form can be displayed either sliding out on the
 * side of the view or filling the whole screen - see the theme for the related
 * CSS rules.
 */
@SuppressWarnings("serial")
public class SpeedTimeTrialForm2 extends SpeedTimeTrialFormDesign2 {
	
    private SpeedCrudLogic viewLogic;
    private BeanFieldGroup<SpeedTimeTrialEntity> fieldGroup;
    
    @SuppressWarnings("rawtypes")
    public SpeedTimeTrialForm2(SpeedCrudLogic logic) {
        super();
        //addStyleName("product-form");
        this.viewLogic = logic;
        this.setMargin(true);
        this.setSpacing(true);
        
        tiempo2A.setRequired(true);
        tiempo2B.setRequired(true);
        conos2.setRequired(true);
        tiempoAjustado2.setRequired(false);
        save.setEnabled(false);
        
        int numConos = EntryPoint.get().getConfigInt(ConfigItemEnum.MAXNUMCONOSDERRIBADOS);
        for(int i=0;i<=numConos;i++)
        	conos2.addItem(i);
        conos2.setItemCaption(numConos, "Nulo");
        
        fieldGroup = new BeanFieldGroup<SpeedTimeTrialEntity>(SpeedTimeTrialEntity.class);
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
            	SpeedTimeTrialEntity rec = fieldGroup.getItemDataSource().getBean();
            	if(viewLogic!=null){
            		viewLogic.saveRecordTiempo(rec, RondaEnum.SEGUNDA);
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

    public void editRecord(SpeedTimeTrialEntity rec) {
        if (rec == null) {
            rec = new SpeedTimeTrialEntity();
            fieldGroup.setItemDataSource(new BeanItem<SpeedTimeTrialEntity>(rec));
            save.setEnabled(false);
            return;
        }
        fieldGroup.setItemDataSource(new BeanItem<SpeedTimeTrialEntity>(rec));

        // before the user makes any changes, disable validation error indicator
        // of the product name field (which may be empty)
        //nombre.setValidationVisible(false);

        // Scroll to the top
        // As this is not a Panel, using JavaScript
        String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
        Page.getCurrent().getJavaScript().execute(scrollScript);
        
        tiempoAjustado2.setValue(""+rec.getTiempoAjustado2());
        formHasChanged();
    }

    private void formHasChanged() {
        // show validation errors after the user has changed something
        //nombre.setValidationVisible(true);

        BeanItem<SpeedTimeTrialEntity> item = fieldGroup.getItemDataSource();
        if (item != null) {
        	SpeedTimeTrialEntity rec = item.getBean();
        	if(rec!=null){
        		//
        	}
        }
        tiempoAjustado2.setEnabled(false);
        save.setEnabled(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.COORD));
    }
    		
}
