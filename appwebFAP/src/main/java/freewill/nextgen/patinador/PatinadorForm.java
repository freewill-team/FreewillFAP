package freewill.nextgen.patinador;

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
import freewill.nextgen.data.ClubEntity;
import freewill.nextgen.data.PatinadorEntity;
import freewill.nextgen.data.PatinadorEntity.GenderEnum;
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
public class PatinadorForm extends PatinadorFormDesign implements CustomFormInterface<PatinadorEntity> {

    private BeanFieldGroup<PatinadorEntity> fieldGroup;
    private GenericCrudLogic<PatinadorEntity> viewLogic;
    
    @SuppressWarnings("rawtypes")
    public PatinadorForm() {
        super();
        addStyleName("product-form");
        
        club.setRequired(true);
        //club.addValidator(new ComboValidator());
        genero.clear();
        genero.addItem(GenderEnum.MALE);
        genero.addItem(GenderEnum.FEMALE);
        genero.setRequired(true);
        fechaNacimiento.setDateFormat("      dd/MM/yyyy");
        
        fieldGroup = new BeanFieldGroup<PatinadorEntity>(PatinadorEntity.class); 
        // It cannot be binded automatically fieldGroup.bindMemberFields(this);
        fieldGroup.bind(nombre, "nombre");
        fieldGroup.bind(apellidos, "apellidos");
        fieldGroup.bind(fechaNacimiento, "fechaNacimiento");
        fieldGroup.bind(genero, "genero");
        fieldGroup.bind(dni, "dni");
        fieldGroup.bind(club, "club");
        fieldGroup.bind(fichaFederativa, "fichaFederativa");
        fieldGroup.bind(email, "email");
        fieldGroup.bind(active, "active");
        
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
				PatinadorEntity rec = fieldGroup.getItemDataSource().getBean();
				rec.setClubStr(club.getItemCaption(club.getValue()));
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
            			PatinadorEntity rec = fieldGroup.getItemDataSource().getBean();
            			if(viewLogic!=null){
            				viewLogic.deleteRecord(rec);
            			}
                    }
                });
            	getUI().addWindow(cd);
            }
        });
        
        try{
        	// Rellenar ComboBox Club
            club.removeAllItems();
            Collection<ClubEntity> clubs = BltClient.get().getEntities(ClubEntity.class,
    				EntryPoint.get().getAccessControl().getTokenKey());
    		for (ClubEntity s : clubs) {
    			club.addItem(s.getId());
    			club.setItemCaption(s.getId(), s.getNombre());
    	    }
        }
        catch(Exception e){
        	e.printStackTrace();
        	Notification.show("Error: "+e.getMessage(), Type.ERROR_MESSAGE);
        }
        
    }
    
	public void editRecord(PatinadorEntity rec) {
        if (rec == null) {
            rec = new PatinadorEntity();
            fieldGroup.setItemDataSource(new BeanItem<PatinadorEntity>(rec));
            return;
        }
        fieldGroup.setItemDataSource(new BeanItem<PatinadorEntity>(rec));
        
        // before the user makes any changes, disable validation error indicator
        // of the product name field (which may be empty)
        nombre.setValidationVisible(false);
        
        // Scroll to the top
        // As this is not a Panel, using JavaScript
        String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
        Page.getCurrent().getJavaScript().execute(scrollScript);
        
        /*try{
        	// Rellenar ComboBox Club
            club.removeAllItems();
            Collection<ClubEntity> clubs = BltClient.get().getEntities(ClubEntity.class,
    				EntryPoint.get().getAccessControl().getTokenKey());
    		for (ClubEntity s : clubs) {
    			club.addItem(s.getId());
    			club.setItemCaption(s.getId(), s.getNombre());
    	    }
    		club.setValue(rec.getClub());
        }
        catch(Exception e){
        	e.printStackTrace();
        	Notification.show("Error: "+e.getMessage(), Type.ERROR_MESSAGE);
        } Movido al Constructor */
        club.setValue(rec.getClub());
        
        formHasChanged();
        //nombre.focus(); // As per requested by Mar
    }
	
	private void formHasChanged() {
        // show validation errors after the user has changed something
		nombre.setValidationVisible(true);

        // only products that have been saved should be removable
        boolean canRemoveRecord = false;
        BeanItem<PatinadorEntity> item = fieldGroup.getItemDataSource();
        if (item != null) {
        	PatinadorEntity rec = item.getBean();
        	if(rec!=null){
        		canRemoveRecord = (rec.getId() != null);
        	}
        }
        delete.setEnabled(canRemoveRecord);
        delete.setVisible(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.ADMIN));
    }

	@Override
	public void setLogic(GenericCrudLogic<PatinadorEntity> logic) {
		viewLogic = logic;
	}

	@Override
	public Component getComponent() {
		return this;
	}
    
    /*private static final class ComboValidator extends AbstractValidator<Long> {

    	public ComboValidator() {
    		super("El campo 'Club' no puede estar vacío");
    	}

    	@Override
    	protected boolean isValidValue(Long value) {
    		if (value == null) {
    			return false;
    		}
    		return true;
    	}
    	
    	@Override
    	public Class<Long> getType() {
    		return Long.class;
    	}
    	
    }*/
    
}
