package freewill.nextgen.competicion.derrapes;

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
import freewill.nextgen.data.DerrapesRondaEntity;
import freewill.nextgen.hmi.utils.Messages;

/**
 * A form for editing a single record.
 *
 * Using responsive layouts, the form can be displayed either sliding out on the
 * side of the view or filling the whole screen - see the theme for the related
 * CSS rules.
 */
@SuppressWarnings("serial")
public class DerrapesRondaForm extends DerrapesRondaFormDesign {
	
    private DerrapesCrudLogic viewLogic;
    private BeanFieldGroup<DerrapesRondaEntity> fieldGroup;
    
    @SuppressWarnings({ "rawtypes"})
    public DerrapesRondaForm(DerrapesCrudLogic logic) {
        super();
        //addStyleName("product-form");
        this.viewLogic = logic;
        this.setMargin(true);
        this.setSpacing(true);
        this.setHeight("100%");
        
        save.setEnabled(false);
        ganador1.setRequired(true);
        ganador2.setRequired(true);
        //ganador3.setRequired(true);
        //ganador4.setRequired(true);
        /*patina1.setCaption(Messages.get().getKey("patina1"));
        patina2.setCaption(Messages.get().getKey("patina2"));
        patina3.setCaption(Messages.get().getKey("patina3"));
        patina4.setCaption(Messages.get().getKey("patina4"));*/
        
        fieldGroup = new BeanFieldGroup<DerrapesRondaEntity>(DerrapesRondaEntity.class);
        // Falla Bind automatico fieldGroup.bindMemberFields(this);
        fieldGroup.bind(ganador1, "ganador1");
        fieldGroup.bind(ganador2, "ganador2");
        fieldGroup.bind(ganador3, "ganador3");
        fieldGroup.bind(ganador4, "ganador4");

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
            	System.out.println("Entrando en Commit...");
            	//DerrapesRondaEntity rec = fieldGroup.getItemDataSource().getBean();
            	if(ganador1.getValue()==ganador2.getValue() 
            		|| ganador1.getValue()==ganador3.getValue() 
            		|| ganador1.getValue()==ganador4.getValue() 
            		|| ganador2.getValue()==ganador3.getValue() 
            		|| ganador2.getValue()==ganador4.getValue() 
            				|| (ganador3.getValue()==ganador4.getValue() && ganador3.getValue()!=null))
            			throw new CommitException("Ganador 1 y Ganador 2 no pueden ser iguales.");
            }

            @Override
            public void postCommit(CommitEvent commitEvent)
                    throws CommitException {
            	System.out.println("Entrando en Update Record...");
            	DerrapesRondaEntity rec = fieldGroup.getItemDataSource().getBean();
            	rec.setGanadorStr1(ganador1.getItemCaption(ganador1.getValue()));
            	rec.setGanadorStr2(ganador2.getItemCaption(ganador2.getValue()));
            	rec.setGanadorStr3(ganador3.getItemCaption(ganador3.getValue()));
            	rec.setGanadorStr4(ganador4.getItemCaption(ganador4.getValue()));
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
                	e.printStackTrace();
                	System.out.println(e.getMessage());
                	System.out.println(e.getCause().getMessage());
                    Notification n = new Notification(e.getCause().getMessage(), Type.ERROR_MESSAGE);
                    n.setDelayMsec(500);
                    n.show(getUI().getPage());
                }
            }
        });
        
    }

    public void editRecord(DerrapesRondaEntity rec) {
        if (rec == null) {
            rec = new DerrapesRondaEntity();
            fieldGroup.setItemDataSource(new BeanItem<DerrapesRondaEntity>(rec));
            save.setEnabled(false);
            ganador1.removeAllItems();
            ganador2.removeAllItems();
            ganador3.removeAllItems();
            ganador4.removeAllItems();
            /*patina1.setValue("");
            patina2.setValue("");
            patina3.setValue("");
            patina4.setValue("");*/
            this.setEnabled(false);
            return;
        }
        
        this.setEnabled(true);
        fieldGroup.setItemDataSource(new BeanItem<DerrapesRondaEntity>(rec));

        // before the user makes any changes, disable validation error indicator
        // of the product name field (which may be empty)
        //nombre.setValidationVisible(false);

        // Scroll to the top
        // As this is not a Panel, using JavaScript
        String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
        Page.getCurrent().getJavaScript().execute(scrollScript);
        
        ganador1.removeAllItems();
        ganador1.addItem(rec.getPatinador1());
        ganador1.setItemCaption(rec.getPatinador1(), rec.getNombre1()+" "+rec.getApellidos1());
        ganador1.addItem(rec.getPatinador2());
        ganador1.setItemCaption(rec.getPatinador2(), rec.getNombre2()+" "+rec.getApellidos2());
        ganador1.addItem(rec.getPatinador3());
        ganador1.setItemCaption(rec.getPatinador3(), rec.getNombre3()+" "+rec.getApellidos3());
        ganador1.addItem(rec.getPatinador4());
        ganador1.setItemCaption(rec.getPatinador4(), rec.getNombre4()+" "+rec.getApellidos4());
        
        ganador2.removeAllItems();
        ganador2.addItem(rec.getPatinador1());
        ganador2.setItemCaption(rec.getPatinador1(), rec.getNombre1()+" "+rec.getApellidos1());
        ganador2.addItem(rec.getPatinador2());
        ganador2.setItemCaption(rec.getPatinador2(), rec.getNombre2()+" "+rec.getApellidos2());
        ganador2.addItem(rec.getPatinador3());
        ganador2.setItemCaption(rec.getPatinador3(), rec.getNombre3()+" "+rec.getApellidos3());
        ganador2.addItem(rec.getPatinador4());
        ganador2.setItemCaption(rec.getPatinador4(), rec.getNombre4()+" "+rec.getApellidos4());
        
        ganador3.removeAllItems();
        ganador3.addItem(rec.getPatinador1());
        ganador3.setItemCaption(rec.getPatinador1(), rec.getNombre1()+" "+rec.getApellidos1());
        ganador3.addItem(rec.getPatinador2());
        ganador3.setItemCaption(rec.getPatinador2(), rec.getNombre2()+" "+rec.getApellidos2());
        ganador3.addItem(rec.getPatinador3());
        ganador3.setItemCaption(rec.getPatinador3(), rec.getNombre3()+" "+rec.getApellidos3());
        ganador3.addItem(rec.getPatinador4());
        ganador3.setItemCaption(rec.getPatinador4(), rec.getNombre4()+" "+rec.getApellidos4());
        
        ganador4.removeAllItems();
        ganador4.addItem(rec.getPatinador1());
        ganador4.setItemCaption(rec.getPatinador1(), rec.getNombre1()+" "+rec.getApellidos1());
        ganador4.addItem(rec.getPatinador2());
        ganador4.setItemCaption(rec.getPatinador2(), rec.getNombre2()+" "+rec.getApellidos2());
        ganador4.addItem(rec.getPatinador3());
        ganador4.setItemCaption(rec.getPatinador3(), rec.getNombre3()+" "+rec.getApellidos3());
        ganador4.addItem(rec.getPatinador4());
        ganador4.setItemCaption(rec.getPatinador4(), rec.getNombre4()+" "+rec.getApellidos4());
        
        /*patina1.setValue(rec.getNombre1()+" "+rec.getApellidos1());
        patina2.setValue(rec.getNombre2()+" "+rec.getApellidos2());
        patina3.setValue(rec.getNombre3()+" "+rec.getApellidos3());
        patina4.setValue(rec.getNombre4()+" "+rec.getApellidos4());*/
        ganador1.setValue(rec.getGanador1());
		ganador2.setValue(rec.getGanador2());
		ganador3.setValue(rec.getGanador3());
		ganador4.setValue(rec.getGanador4());
        
        formHasChanged();
    }

    private void formHasChanged() {
        // show validation errors after the user has changed something
        //nombre.setValidationVisible(true);

        BeanItem<DerrapesRondaEntity> item = fieldGroup.getItemDataSource();
        if (item != null) {
        	DerrapesRondaEntity rec = item.getBean();
        	if(rec!=null){
        		//
        	}
        }
        /*patina1.setEnabled(false);
        patina2.setEnabled(false);
        patina3.setEnabled(false);
        patina4.setEnabled(false);*/
        save.setEnabled(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.COORD));
    }
    		
}
