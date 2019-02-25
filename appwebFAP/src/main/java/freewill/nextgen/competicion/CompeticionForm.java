package freewill.nextgen.competicion;

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
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.CircuitoEntity;
import freewill.nextgen.data.CompeticionEntity;
import freewill.nextgen.data.CompeticionEntity.TipoCompeticionEnum;
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
public class CompeticionForm extends CompeticionFormDesign implements CustomFormInterface<CompeticionEntity> {
	
    private GenericCrudLogic<CompeticionEntity> viewLogic;
    private BeanFieldGroup<CompeticionEntity> fieldGroup;
    //private Logger log = Logger.getLogger(CompeticionForm.class);
    
    @SuppressWarnings("rawtypes")
    public CompeticionForm() {
        super();
        addStyleName("product-form");
        
        for (TipoCompeticionEnum s : TipoCompeticionEnum.values()) {
            tipo.addItem(s);
        }
        tipo.setRequired(true);
        circuito.setRequired(true);
        fechaInicio.setDateFormat("      dd/MM/yyyy");
        fechaFin.setDateFormat("      dd/MM/yyyy");
        fechaFinInscripcion.setDateFormat("      dd/MM/yyyy");
        
        fieldGroup = new BeanFieldGroup<CompeticionEntity>(CompeticionEntity.class);
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
            	CompeticionEntity rec = fieldGroup.getItemDataSource().getBean();
            	rec.setCircuitoStr(circuito.getItemCaption(circuito.getValue()));
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
            		removeStyleName("visible");
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
            			CompeticionEntity rec = fieldGroup.getItemDataSource().getBean();
            			if(viewLogic!=null){
            				viewLogic.deleteRecord(rec);
            			}
                    }
                });
            	getUI().addWindow(cd);
            }
        });
        
        closeCompeticion.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	if(viewLogic!=null)
            		viewLogic.cancelRecord();
            	else
            		removeStyleName("visible");
            	
            	ConfirmDialog cd = new ConfirmDialog("Esta acción cerrará esta Competición y consolidará\nlos datos de Ranking. ¿Desea Continuar?");
            	cd.setOKAction(new ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
            			cd.close();
            			closeCompeticion();
                    }
                });
            	getUI().addWindow(cd);
            }
        });
        
    }

    public void editRecord(CompeticionEntity rec) {
        if (rec == null) {
            rec = new CompeticionEntity();
            fieldGroup.setItemDataSource(new BeanItem<CompeticionEntity>(rec));
            return;
        }
        fieldGroup.setItemDataSource(new BeanItem<CompeticionEntity>(rec));

        // before the user makes any changes, disable validation error indicator
        // of the product name field (which may be empty)
        nombre.setValidationVisible(false);

        // Scroll to the top
        // As this is not a Panel, using JavaScript
        String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
        Page.getCurrent().getJavaScript().execute(scrollScript);
        
        try{
        	// Rellenar ComboBox Circuito
            circuito.removeAllItems();
            Collection<CircuitoEntity> circuitos = BltClient.get().getEntities(CircuitoEntity.class,
    				EntryPoint.get().getAccessControl().getTokenKey());
    		for (CircuitoEntity s : circuitos) {
    			circuito.addItem(s.getId());
    			circuito.setItemCaption(s.getId(), s.getNombre());
    	    }
    		circuito.setValue(rec.getCircuito());
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
        boolean canRemoveRecord = false;
        BeanItem<CompeticionEntity> item = fieldGroup.getItemDataSource();
        if (item != null) {
        	CompeticionEntity rec = item.getBean();
        	if(rec!=null){
        		canRemoveRecord = (rec.getId() != null);
        	}
        	closeCompeticion.setEnabled(rec.getActive());
        }
        active.setVisible(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.ADMIN));
        delete.setEnabled(canRemoveRecord && EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.ADMIN));
        save.setEnabled(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.COORD));
    }
    
    @Override
	public void setLogic(GenericCrudLogic<CompeticionEntity> logic) {
		viewLogic = logic;
	}

	@Override
	public Component getComponent() {
		return this;
	}
	
	private void closeCompeticion() {
		try{
			CompeticionEntity rec = fieldGroup.getItemDataSource().getBean();
			if(rec!=null){
	        	BltClient.get().executeCommand(
	        		"/closeCompeticion/"+rec.getId(),
	        		CompeticionEntity.class,
	        		EntryPoint.get().getAccessControl().getTokenKey());
	        }
    	}
		catch(Exception e){
			e.printStackTrace();
			//log.error(e.getMessage());
			//view.showError(e.getMessage());
		}
	}
	
}
