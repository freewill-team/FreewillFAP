package freewill.nextgen.parejajam;

import java.util.Collection;
import java.util.List;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.CategoriaEntity;
import freewill.nextgen.data.CategoriaEntity.ModalidadEnum;
import freewill.nextgen.data.ClubEntity;
import freewill.nextgen.data.ParejaJamEntity;
import freewill.nextgen.data.PatinadorEntity;
import freewill.nextgen.genericCrud.CustomFormInterface;
import freewill.nextgen.genericCrud.GenericCrudLogic;
import freewill.nextgen.hmi.common.ConfirmDialog;
import freewill.nextgen.hmi.utils.Messages;
import freewill.nextgen.patinador.SelectPatinadorDialog;

/**
 * A form for editing a single record.
 *
 * Using responsive layouts, the form can be displayed either sliding out on the
 * side of the view or filling the whole screen - see the theme for the related
 * CSS rules.
 */
@SuppressWarnings("serial")
public class ParejaJamForm extends ParejaJamFormDesign implements CustomFormInterface<ParejaJamEntity>{

    private BeanFieldGroup<ParejaJamEntity> fieldGroup;
    private GenericCrudLogic<ParejaJamEntity> viewLogic;
    
    @SuppressWarnings("rawtypes")
    public ParejaJamForm() {
    	super();
        addStyleName("product-form");
        
        club.setRequired(true);
        
        fieldGroup = new BeanFieldGroup<ParejaJamEntity>(ParejaJamEntity.class); 
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
				ParejaJamEntity rec = fieldGroup.getItemDataSource().getBean();
				rec.setClubStr(club.getItemCaption(club.getValue()));
				rec.setCategoriaStr(categoria.getItemCaption(categoria.getValue()));
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
            			ParejaJamEntity rec = fieldGroup.getItemDataSource().getBean();
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
            Collection<ClubEntity> recs = BltClient.get().getEntities(ClubEntity.class,
    				EntryPoint.get().getAccessControl().getTokenKey());
    		for (ClubEntity s : recs) {
    			club.addItem(s.getId());
    			club.setItemCaption(s.getId(), s.getNombre());
    	    }
    		club.setRequired(true);
        }
        catch(Exception e){
        	e.printStackTrace();
        	Notification.show("Error: "+e.getMessage(), Type.ERROR_MESSAGE);
        }
        
        try{
        	// Rellenar ComboBox Categoria
        	categoria.removeAllItems();
            Collection<CategoriaEntity> recs = BltClient.get().getEntities(CategoriaEntity.class,
    				EntryPoint.get().getAccessControl().getTokenKey());
    		for (CategoriaEntity s : recs) {
    			if(s.getModalidad()==ModalidadEnum.JAM){
	    			categoria.addItem(s.getId());
	    			categoria.setItemCaption(s.getId(), s.getNombre());
    			}
    	    }
    		categoria.setRequired(true);
        }
        catch(Exception e){
        	e.printStackTrace();
        	Notification.show("Error: "+e.getMessage(), Type.ERROR_MESSAGE);
        }
        
        patin1Btn.setCaption(Messages.get().getKey("patinador1"));
        patin1Btn.addStyleName(ValoTheme.BUTTON_PRIMARY);
        patin1Btn.setIcon(FontAwesome.SEARCH);
        patin1Btn.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
				// Abre la ventana de seleccion de patinador
				List<PatinadorEntity> students = getPatinadores();
				
				SelectPatinadorDialog cd = new SelectPatinadorDialog(students);
            	cd.setOKAction(new ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
            			cd.close();
            			PatinadorEntity user = cd.getSelected();
            			if(user!=null){
            				patinador1.setValue(""+user.getId());
                    		nombre1.setValue(user.getNombre());
                    		apellidos1.setValue(user.getApellidos());
                    		club.setValue(user.getClub());
            			}
                    }
                });
            	getUI().addWindow(cd);
			}
        });
        
        patin2Btn.setCaption(Messages.get().getKey("patinador2"));
        patin2Btn.addStyleName(ValoTheme.BUTTON_PRIMARY);
        patin2Btn.setIcon(FontAwesome.SEARCH);
        patin2Btn.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
				// Abre la ventana de seleccion de patinador Pareja
				List<PatinadorEntity> students = getPatinadores();
				
				SelectPatinadorDialog cd = new SelectPatinadorDialog(students);
            	cd.setOKAction(new ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
            			cd.close();
            			PatinadorEntity user = cd.getSelected();
            			if(user!=null){
            				patinador2.setValue(""+user.getId());
                    		nombre2.setValue(user.getNombre());
                    		apellidos2.setValue(user.getApellidos());
            			}
                    }
                });
            	getUI().addWindow(cd);
			}
        });
        
    }
    
	public void editRecord(ParejaJamEntity rec) {
        if (rec == null) {
            rec = new ParejaJamEntity();
            fieldGroup.setItemDataSource(new BeanItem<ParejaJamEntity>(rec));
            return;
        }
        fieldGroup.setItemDataSource(new BeanItem<ParejaJamEntity>(rec));
        
        // before the user makes any changes, disable validation error indicator
        // of the product name field (which may be empty)
        nombre1.setValidationVisible(false);
        
        // Scroll to the top
        // As this is not a Panel, using JavaScript
        String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
        Page.getCurrent().getJavaScript().execute(scrollScript);
        
        club.setValue(rec.getClub());
        categoria.setValue(rec.getCategoria());
        formHasChanged();
    }
	
	private void formHasChanged() {
        // show validation errors after the user has changed something
		nombre1.setValidationVisible(true);

        // only products that have been saved should be removable
        boolean canRemoveRecord = false;
        BeanItem<ParejaJamEntity> item = fieldGroup.getItemDataSource();
        if (item != null) {
        	ParejaJamEntity rec = item.getBean();
        	if(rec!=null){
        		canRemoveRecord = (rec.getId() != null);
        	}
        }
        patinador1.setVisible(false);
        patinador2.setVisible(false);
        
        delete.setEnabled(canRemoveRecord);
        delete.setVisible(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.ADMIN));
    }
    
	@Override
	public void setLogic(GenericCrudLogic<ParejaJamEntity> logic) {
		viewLogic = logic;
	}

	@Override
	public Component getComponent() {
		return this;
	}
	
	public List<PatinadorEntity> getPatinadores() {
		try{
	        return BltClient.get().getEntities(PatinadorEntity.class,
	        		EntryPoint.get().getAccessControl().getTokenKey());
    	}
		catch(Exception e){
			e.printStackTrace();
			//log.error(e.getMessage());
			//if(viewLogic!=null)
			//	viewLogic.showError(e.getMessage());
		}
		return null;
	}
	
}
