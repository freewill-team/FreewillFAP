package freewill.nextgen.resultados;

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
import com.vaadin.ui.Field;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.CategoriaEntity;
import freewill.nextgen.data.ClubEntity;
import freewill.nextgen.data.CompeticionEntity;
import freewill.nextgen.data.ParticipanteEntity;
import freewill.nextgen.data.PatinadorEntity;
import freewill.nextgen.hmi.common.ConfirmDialog;
import freewill.nextgen.hmi.utils.Messages;
import freewill.nextgen.participante.ParticipanteCrudLogic;
import freewill.nextgen.patinador.SelectPatinadorDialog;

/**
 * A form for editing a single record.
 *
 * Using responsive layouts, the form can be displayed either sliding out on the
 * side of the view or filling the whole screen - see the theme for the related
 * CSS rules.
 */
@SuppressWarnings("serial")
public class ParticipanteForm extends ParticipanteFormDesign {

    private BeanFieldGroup<ParticipanteEntity> fieldGroup;
    private ResultadosCrudLogic viewLogic;
    
    @SuppressWarnings("rawtypes")
    public ParticipanteForm(ResultadosCrudLogic logic) {
    	super();
        addStyleName("product-form");
        this.viewLogic = logic;
        
        fieldGroup = new BeanFieldGroup<ParticipanteEntity>(ParticipanteEntity.class); 
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
				ParticipanteEntity rec = fieldGroup.getItemDataSource().getBean();
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
            			ParticipanteEntity rec = fieldGroup.getItemDataSource().getBean();
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
        	// Rellenar ComboBox Competicion
        	competicion.removeAllItems();
            Collection<CompeticionEntity> recs = BltClient.get().getEntities(CompeticionEntity.class,
    				EntryPoint.get().getAccessControl().getTokenKey());
    		for (CompeticionEntity s : recs) {
    			competicion.addItem(s.getId());
    			competicion.setItemCaption(s.getId(), s.getNombre());
    	    }
    		competicion.setRequired(true);
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
    			categoria.addItem(s.getId());
    			categoria.setItemCaption(s.getId(), s.getNombre());
    	    }
    		categoria.setRequired(true);
        }
        catch(Exception e){
        	e.printStackTrace();
        	Notification.show("Error: "+e.getMessage(), Type.ERROR_MESSAGE);
        }
        
        patinBtn.setCaption(Messages.get().getKey("patinador"));
        patinBtn.addStyleName(ValoTheme.BUTTON_PRIMARY);
        patinBtn.setIcon(FontAwesome.SEARCH);
        patinBtn.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
				// Abre la ventana de seleccion de patinador
				List<PatinadorEntity> students = viewLogic.getPatinadores();
				
				SelectPatinadorDialog cd = new SelectPatinadorDialog(students);
            	cd.setOKAction(new ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
            			cd.close();
            			PatinadorEntity user = cd.getSelected();
            			if(user!=null){
            				patinador.setValue(""+user.getId());
                    		nombre.setValue(user.getNombre());
                    		apellidos.setValue(user.getApellidos());
                    		club.setValue(user.getClub());
            			}
                    }
                });
            	getUI().addWindow(cd);
			}
        });
        
        parejaBtn.setCaption(Messages.get().getKey("patinador"));
        parejaBtn.addStyleName(ValoTheme.BUTTON_PRIMARY);
        parejaBtn.setIcon(FontAwesome.SEARCH);
        parejaBtn.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
				// Abre la ventana de seleccion de patinador Pareja
				List<PatinadorEntity> students = viewLogic.getPatinadores();
				
				SelectPatinadorDialog cd = new SelectPatinadorDialog(students);
            	cd.setOKAction(new ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
            			cd.close();
            			PatinadorEntity user = cd.getSelected();
            			if(user!=null){
            				patinadorPareja.setValue(""+user.getId());
                    		nombrePareja.setValue(user.getNombre());
                    		apellidosPareja.setValue(user.getApellidos());
            			}
                    }
                });
            	getUI().addWindow(cd);
			}
        });
        
    }
    
	public void editRecord(ParticipanteEntity rec) {
        if (rec == null) {
            rec = new ParticipanteEntity();
            fieldGroup.setItemDataSource(new BeanItem<ParticipanteEntity>(rec));
            return;
        }
        fieldGroup.setItemDataSource(new BeanItem<ParticipanteEntity>(rec));
        
        // before the user makes any changes, disable validation error indicator
        // of the product name field (which may be empty)
        nombre.setValidationVisible(false);
        
        // Scroll to the top
        // As this is not a Panel, using JavaScript
        String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
        Page.getCurrent().getJavaScript().execute(scrollScript);
        
        club.setValue(rec.getClub());
        competicion.setValue(rec.getCompeticion());
        categoria.setValue(rec.getCategoria());
        
        formHasChanged();
    }
	
	private void formHasChanged() {
        // show validation errors after the user has changed something
		nombre.setValidationVisible(true);

        // only products that have been saved should be removable
        boolean canRemoveRecord = false;
        BeanItem<ParticipanteEntity> item = fieldGroup.getItemDataSource();
        if (item != null) {
        	ParticipanteEntity rec = item.getBean();
        	if(rec!=null){
        		canRemoveRecord = (rec.getId() != null);
        	}
        }
        patinBtn.setEnabled(!canRemoveRecord);
        patinador.setVisible(false);
        nombre.setEnabled(!canRemoveRecord);
        apellidos.setEnabled(!canRemoveRecord);
        patinadorPareja.setVisible(false);
        
        delete.setEnabled(canRemoveRecord);
        delete.setVisible(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.ADMIN));
    }
    
}
