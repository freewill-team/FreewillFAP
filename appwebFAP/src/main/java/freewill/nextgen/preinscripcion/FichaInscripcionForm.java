package freewill.nextgen.preinscripcion;

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
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.CompeticionEntity;
import freewill.nextgen.data.InscripcionEntity;
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
public class FichaInscripcionForm extends FichaInscripcionFormDesign {

    private BeanFieldGroup<InscripcionEntity> fieldGroup;
    private PreinscripcionCrudLogic viewLogic;
    private CompeticionEntity competicion = null;
    private boolean preinscripcionAbierta = false;
    
    @SuppressWarnings("rawtypes")
    public FichaInscripcionForm(PreinscripcionCrudLogic sampleCrudLogic) {
        super();
        addStyleName("product-form");
        this.viewLogic = sampleCrudLogic;
        
        fieldGroup = new BeanFieldGroup<InscripcionEntity>(InscripcionEntity.class); 
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
            	//System.out.println("Entrando en Send Record...");
            	ConfirmDialog cd = new ConfirmDialog(
            			"Tras enviar las inscripciones, ya no podrá editarlas. Desea continuar?");
            	cd.setOKAction(new ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
            			cd.close();
            			InscripcionEntity rec = fieldGroup.getItemDataSource().getBean();
                    	if(viewLogic!=null){
                    		viewLogic.sendRecord(rec);
                    		removeStyleName("visible");
                    	}
                    }
                });
            	getUI().addWindow(cd);
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
            			InscripcionEntity rec = fieldGroup.getItemDataSource().getBean();
            			if(viewLogic!=null){
            				removeStyleName("visible");
            				viewLogic.deleteRecord(rec);
            			}
                    }
                });
            	getUI().addWindow(cd);
            }
        });
        
    }
    
	public void editRecord(InscripcionEntity rec, CompeticionEntity competi, 
			boolean abierta) {
		//System.out.println("Entrando en editRecord()");
		this.competicion = competi;
		this.preinscripcionAbierta = abierta;
        if (rec == null) {
            rec = new InscripcionEntity();
            fieldGroup.setItemDataSource(new BeanItem<InscripcionEntity>(rec));
            return;
        }
        fieldGroup.setItemDataSource(new BeanItem<InscripcionEntity>(rec));
        
        // before the user makes any changes, disable validation error indicator
        // of the product name field (which may be empty)
        coordinador.setValidationVisible(false);
        
        // Scroll to the top
        // As this is not a Panel, using JavaScript
        String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
        Page.getCurrent().getJavaScript().execute(scrollScript);
        
        if(competicion!=null) {
    		save.setEnabled(!rec.getEnviado() && competi.getActive() && preinscripcionAbierta);
    	}
        
        formHasChanged();
    }
	
	private void formHasChanged() {
		//System.out.println("Entrando en formHasChanged()");
        // show validation errors after the user has changed something
		coordinador.setValidationVisible(true);

        // only products that have been saved should be removable
        //boolean canRemoveRecord = false;
        BeanItem<InscripcionEntity> item = fieldGroup.getItemDataSource();
        if (item != null) {
        	InscripcionEntity rec = item.getBean();
        	if(rec!=null){
        		//canRemoveRecord = (rec.getId() != null);
        	}
        }
        boolean admin = EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.ADMIN);
        coordinador.setEnabled(admin);
        clubStr.setEnabled(admin);
        fechaEnvio.setEnabled(admin);
        email.setEnabled(admin);
        telefono.setEnabled(admin);
        enviado.setEnabled(admin);
        delete.setVisible(admin);
    }
    
}