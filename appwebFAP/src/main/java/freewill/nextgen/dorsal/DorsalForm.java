package freewill.nextgen.dorsal;

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
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Field;
import com.vaadin.ui.Notification;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.data.CompeticionEntity;
import freewill.nextgen.data.PatinadorEntity;
import freewill.nextgen.hmi.utils.Messages;

/**
 * A form for editing a single record.
 *
 * Using responsive layouts, the form can be displayed either sliding out on the
 * side of the view or filling the whole screen - see the theme for the related
 * CSS rules.
 */
@SuppressWarnings("serial")
public class DorsalForm extends DorsalFormDesign {

    private BeanFieldGroup<PatinadorEntity> fieldGroup;
    private DorsalCrudLogic viewLogic;
    private Long competicion = null;
    private CompeticionEntity competi = null;
    private boolean checkinAbierto = false;
    
    @SuppressWarnings("rawtypes")
    public DorsalForm(DorsalCrudLogic sampleCrudLogic) {
        super();
        addStyleName("product-form");
        this.viewLogic = sampleCrudLogic;
        
        fechaNacimiento.setDateFormat("      dd/MM/yyyy");
        
        fieldGroup = new BeanFieldGroup<PatinadorEntity>(PatinadorEntity.class); 
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
            	PatinadorEntity rec = fieldGroup.getItemDataSource().getBean();
            	if(viewLogic!=null){
            		if(viewLogic.saveDorsal(rec, competicion)==false)
            			rec.setDorsal(0);
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
            	fieldGroup.discard();
                if(viewLogic!=null)
            		viewLogic.cancelRecord();
            	else
            		removeStyleName("visible");
            }
        });
        
    }
    
	public void editRecord(PatinadorEntity rec, Long competicion, boolean abierto) {
		this.competicion = competicion;
		this.checkinAbierto = abierto;
        if (rec == null) {
            rec = new PatinadorEntity();
            fieldGroup.setItemDataSource(new BeanItem<PatinadorEntity>(rec));
            return;
        }
        if(rec.getDorsal()==null) rec.setDorsal(0);
        fieldGroup.setItemDataSource(new BeanItem<PatinadorEntity>(rec));
        
        // before the user makes any changes, disable validation error indicator
        // of the product name field (which may be empty)
        nombre.setValidationVisible(false);
        
        // Scroll to the top
        // As this is not a Panel, using JavaScript
        String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
        Page.getCurrent().getJavaScript().execute(scrollScript);
        
        try{
            competi = (CompeticionEntity) BltClient.get().getEntityById(
            		""+competicion, CompeticionEntity.class,
    				EntryPoint.get().getAccessControl().getTokenKey());
    		if(competi!=null) {
    			save.setEnabled(competi.getActive() && checkinAbierto);
    			dorsal.setEnabled(competi.getActive());
    		}
    	}
        catch(Exception e){
            e.printStackTrace();
            Notification.show("Error: "+e.getMessage(), Type.ERROR_MESSAGE);
        }  
        
        formHasChanged();
    }
	
	private void formHasChanged() {
        // show validation errors after the user has changed something
		nombre.setValidationVisible(true);

        // only products that have been saved should be removable
        //boolean canRemoveRecord = false;
        BeanItem<PatinadorEntity> item = fieldGroup.getItemDataSource();
        if (item != null) {
        	PatinadorEntity rec = item.getBean();
        	if(rec!=null){
        		//canRemoveRecord = (rec.getId() != null);
        	}
        }   
        nombre.setEnabled(false);
        apellidos.setEnabled(false);
        fechaNacimiento.setEnabled(false);
        genero.setEnabled(false);
        clubStr.setEnabled(false);
        fichaFederativa.setEnabled(false);
        speed.setEnabled(false);
        salto.setEnabled(false);
        derrapes.setEnabled(false);
        jam.setEnabled(false);
        classic.setEnabled(false);
        battle.setEnabled(false);
    }
    
}
