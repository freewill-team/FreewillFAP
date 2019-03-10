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
import freewill.nextgen.data.SpeedKOSystemEntity;
import freewill.nextgen.hmi.utils.Messages;

/**
 * A form for editing a single record.
 *
 * Using responsive layouts, the form can be displayed either sliding out on the
 * side of the view or filling the whole screen - see the theme for the related
 * CSS rules.
 */
@SuppressWarnings("serial")
public class SpeedKOSystemForm extends SpeedKOSystemFormDesign {
	
    private SpeedCrudLogic viewLogic;
    private BeanFieldGroup<SpeedKOSystemEntity> fieldGroup;
    private boolean editable = false;
    
    @SuppressWarnings({ "rawtypes"})
    public SpeedKOSystemForm(SpeedCrudLogic logic) {
        super();
        //addStyleName("product-form");
        this.viewLogic = logic;
        this.setMargin(true);
        this.setSpacing(true);
        this.setHeight("100%");
        
        patina1.setCaption(Messages.get().getKey("patina1"));
        patina2.setCaption(Messages.get().getKey("patina2"));
        
        fieldGroup = new BeanFieldGroup<SpeedKOSystemEntity>(SpeedKOSystemEntity.class);
        // Falla Bind automatico fieldGroup.bindMemberFields(this);
        fieldGroup.bind(ganador,   "ganador");
        fieldGroup.bind(pat1gana1, "pat1gana1");
        fieldGroup.bind(pat1gana2, "pat1gana2");
        fieldGroup.bind(pat1gana3, "pat1gana3");
        fieldGroup.bind(pat2gana1, "pat2gana1");
        fieldGroup.bind(pat2gana2, "pat2gana2");
        fieldGroup.bind(pat2gana3, "pat2gana3");
        fieldGroup.bind(pat1tiempo1, "pat1tiempo1");
        fieldGroup.bind(pat1tiempo2, "pat1tiempo2");
        fieldGroup.bind(pat1tiempo3, "pat1tiempo3");
        fieldGroup.bind(pat2tiempo1, "pat2tiempo1");
        fieldGroup.bind(pat2tiempo2, "pat2tiempo2");
        fieldGroup.bind(pat2tiempo3, "pat2tiempo3");
        
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
        pat1gana1.addValueChangeListener(e -> {
        		pat2gana1.setValue(!pat1gana1.getValue());
        	});
        pat1gana2.addValueChangeListener(e -> {
	    		pat2gana2.setValue(!pat1gana2.getValue());
	    	});
        pat1gana3.addValueChangeListener(e -> {
	    		pat2gana3.setValue(!pat1gana3.getValue());
	    	});
        pat2gana1.addValueChangeListener(e -> {
	    		pat1gana1.setValue(!pat2gana1.getValue());
	    	});
        pat2gana2.addValueChangeListener(e -> {
	    		pat1gana2.setValue(!pat2gana2.getValue());
	    	});
        pat2gana3.addValueChangeListener(e -> {
	    		pat1gana3.setValue(!pat2gana3.getValue());
	    	});
        pat1gana1.setImmediate(true);
        pat1gana2.setImmediate(true);
        pat1gana3.setImmediate(true);
        pat2gana1.setImmediate(true);
        pat2gana2.setImmediate(true);
        pat2gana3.setImmediate(true);

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
            	SpeedKOSystemEntity rec = fieldGroup.getItemDataSource().getBean();
            	rec.setGanadorStr(ganador.getItemCaption(ganador.getValue()));
            	if(viewLogic!=null){
            		viewLogic.saveRecordKO(rec);
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

    public void editRecord(SpeedKOSystemEntity rec, boolean editable) {
        if (rec == null) {
            rec = new SpeedKOSystemEntity();
            fieldGroup.setItemDataSource(new BeanItem<SpeedKOSystemEntity>(rec));
            save.setEnabled(false);
            ganador.removeAllItems();
            patina1.setValue("");
            patina2.setValue("");
            return;
        }
        
        fieldGroup.setItemDataSource(new BeanItem<SpeedKOSystemEntity>(rec));
        this.editable = editable;

        // before the user makes any changes, disable validation error indicator
        // of the product name field (which may be empty)
        //nombre.setValidationVisible(false);

        // Scroll to the top
        // As this is not a Panel, using JavaScript
        String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
        Page.getCurrent().getJavaScript().execute(scrollScript);
        
        ganador.removeAllItems();
        ganador.addItem(rec.getPatinador1());
        ganador.setItemCaption(rec.getPatinador1(), rec.getNombre1()+" "+rec.getApellidos1());
        ganador.addItem(rec.getPatinador2());
        ganador.setItemCaption(rec.getPatinador2(), rec.getNombre2()+" "+rec.getApellidos2());
        ganador.setValue(rec.getGanador());
        patina1.setValue(rec.getNombre1()+" "+rec.getApellidos1());
        patina2.setValue(rec.getNombre2()+" "+rec.getApellidos2());
        
        formHasChanged();
    }

    private void formHasChanged() {
        // show validation errors after the user has changed something
        //nombre.setValidationVisible(true);

        BeanItem<SpeedKOSystemEntity> item = fieldGroup.getItemDataSource();
        if (item != null) {
        	//SpeedKOSystemEntity rec = item.getBean();
        	//CheckGanador();
        }
        patina1.setEnabled(false);
        patina2.setEnabled(false);
        save.setEnabled(editable && EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.COORD));
    }
    		
}
