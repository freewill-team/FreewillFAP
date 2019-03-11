package freewill.nextgen.preinscripcion;

import java.util.Collection;
import java.util.List;

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
import freewill.nextgen.data.CategoriaEntity;
import freewill.nextgen.data.CompeticionEntity;
import freewill.nextgen.data.ParejaJamEntity;
import freewill.nextgen.data.PatinadorEntity;
import freewill.nextgen.hmi.utils.Messages;
import freewill.nextgen.parejajam.SelectParejaJamDialog;
import freewill.nextgen.patinador.SelectPatinadorDialog;
import freewill.nextgen.preinscripcion.PreinscripcionCrudView.InscripcionEnum;

/**
 * A form for editing a single record.
 *
 * Using responsive layouts, the form can be displayed either sliding out on the
 * side of the view or filling the whole screen - see the theme for the related
 * CSS rules.
 */
@SuppressWarnings("serial")
public class PreinscripcionForm extends PreinscripcionFormDesign {

    private BeanFieldGroup<PatinadorEntity> fieldGroup;
    private PreinscripcionCrudLogic viewLogic;
    private CompeticionEntity competicion = null;
    private boolean preinscripcionAbierta = false;
    private InscripcionEnum tipoForm = InscripcionEnum.INSCRIPCION;
    
    @SuppressWarnings("rawtypes")
    public PreinscripcionForm(PreinscripcionCrudLogic sampleCrudLogic) {
        super();
        addStyleName("product-form");
        this.viewLogic = sampleCrudLogic;
        
        fechaNacimiento.setDateFormat("      dd/MM/yyyy");
        idPareja.setVisible(false);
        
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
            System.out.println("formhaschanged applied to "+f.getCaption());
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
            		rec.setCatSpeed(idCatSpeed.getItemCaption(idCatSpeed.getValue()));
                    rec.setCatSalto(idCatSalto.getItemCaption(idCatSalto.getValue()));
                    rec.setCatDerrapes(idCatDerrapes.getItemCaption(idCatDerrapes.getValue()));
                    rec.setCatJam(idCatJam.getItemCaption(idCatJam.getValue()));
                    rec.setCatClassic(idCatClassic.getItemCaption(idCatClassic.getValue()));
                    rec.setCatBattle(idCatBattle.getItemCaption(idCatBattle.getValue()));
            		viewLogic.saveRecord(rec, competicion, preinscripcionAbierta);
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
        
        idCatSpeed.setCaption(null); //Messages.get().getKey("categoria"));
        idCatSalto.setCaption(null); //Messages.get().getKey("categoria"));
        idCatDerrapes.setCaption(null); //Messages.get().getKey("categoria"));
        idCatJam.setCaption(null); //Messages.get().getKey("categoria"));
        idCatClassic.setCaption(null); //Messages.get().getKey("categoria"));
        idCatBattle.setCaption(null); //Messages.get().getKey("categoria"));
        /*speed.setWidth("120px");
        salto.setWidth("120px");
        derrapes.setWidth("120px");
        jam.setWidth("120px");
        classic.setWidth("120px");
        battle.setWidth("120px");*/
        
        parejaBtn.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	// Abre la ventana de seleccion de patinador Pareja
				List<ParejaJamEntity> students = viewLogic.getParejasJam();
				
				SelectParejaJamDialog cd = new SelectParejaJamDialog(students);
            	cd.setOKAction(new ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
            			cd.close();
            			ParejaJamEntity users= cd.getSelected();
            			if(users!=null){
            				parejaJam.setValue(""+users.getId());
            				PatinadorEntity rec = fieldGroup.getItemDataSource().getBean();
            				if(users.getPatinador1().longValue()==rec.getId().longValue()){
	            				idPareja.setValue(""+users.getPatinador2());
	                    		nombrePareja.setValue(users.getNombre2());
	                    		apellidosPareja.setValue(users.getApellidos2());
            				}
            				else if(users.getPatinador2().longValue()==rec.getId().longValue()){
            					idPareja.setValue(""+users.getPatinador1());
	                    		nombrePareja.setValue(users.getNombre1());
	                    		apellidosPareja.setValue(users.getApellidos1());
            				}
            				else{
            					 Notification n = new Notification(
            	                    "Pareja incorrecta para este patinador.", Type.ERROR_MESSAGE);
            	                    n.setDelayMsec(500);
            	                    n.show(getUI().getPage());
            				}
            			}
                    }
                });
            	getUI().addWindow(cd);
			}
        });
        
        idCatSpeed.removeAllItems();
        idCatSalto.removeAllItems();
        idCatDerrapes.removeAllItems();
        idCatJam.removeAllItems();
        idCatClassic.removeAllItems();
        idCatBattle.removeAllItems();
        Collection<CategoriaEntity> categorias = viewLogic.getCategorias();
        for(CategoriaEntity cat:categorias){
        	if(!EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.ADMIN) 
        			&& cat.getActive()==false) continue;
        	switch(cat.getModalidad()){
			case SPEED: 
				idCatSpeed.addItem(cat.getId());
				idCatSpeed.setItemCaption(cat.getId(), cat.getNombre());
				break;
			case CLASSIC: 
				idCatClassic.addItem(cat.getId());
				idCatClassic.setItemCaption(cat.getId(), cat.getNombre());
				break;
			case BATTLE: 
				idCatBattle.addItem(cat.getId());
				idCatBattle.setItemCaption(cat.getId(), cat.getNombre());
				break;
			case JAM: 
				idCatJam.addItem(cat.getId());
				idCatJam.setItemCaption(cat.getId(), cat.getNombre());
				break;
			case SLIDE: 
				idCatDerrapes.addItem(cat.getId());
				idCatDerrapes.setItemCaption(cat.getId(), cat.getNombre());
				break;
			case JUMP: 
				idCatSalto.addItem(cat.getId());
				idCatSalto.setItemCaption(cat.getId(), cat.getNombre());
				break;
			}
        }
        idCatSpeed.setImmediate(true);
        idCatSalto.setImmediate(true);
        idCatDerrapes.setImmediate(true);
        idCatJam.setImmediate(true);
        idCatClassic.setImmediate(true);
        idCatBattle.setImmediate(true);
    }
    
	public void editRecord(PatinadorEntity rec, CompeticionEntity competi, 
			boolean abierta, InscripcionEnum tipo) {
		//System.out.println("Entrando en editRecord()");
		this.competicion = competi;
		this.preinscripcionAbierta = abierta;
		this.tipoForm = tipo;
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
        
        if(competicion!=null) {
    		save.setEnabled(competi.getActive() && preinscripcionAbierta);
    		parejaBtn.setEnabled(competicion.getActive());
    		speedLayout.setVisible(competicion.getSpeed());
    		saltoLayout.setVisible(competicion.getSalto());
    		derrapesLayout.setVisible(competicion.getDerrapes());
    		classicLayout.setVisible(competicion.getClassic());
    		battleLayout.setVisible(competicion.getBattle());
    		jamLayout.setVisible(competicion.getJam());
    	}
        idCatSpeed.setValue(rec.getIdCatSpeed());
        idCatSalto.setValue(rec.getIdCatSalto());
        idCatDerrapes.setValue(rec.getIdCatDerrapes());
        idCatJam.setValue(rec.getIdCatJam());
        idCatClassic.setValue(rec.getIdCatClassic());
        idCatBattle.setValue(rec.getIdCatBattle());
        
        formHasChanged();
    }
	
	private void formHasChanged() {
		//System.out.println("Entrando en formHasChanged()");
        // show validation errors after the user has changed something
		nombre.setValidationVisible(true);

        // only products that have been saved should be removable
        //boolean canRemoveRecord = false;
        BeanItem<PatinadorEntity> item = fieldGroup.getItemDataSource();
        if (item != null) {
        	PatinadorEntity rec = item.getBean();
        	if(rec!=null){
        		//canRemoveRecord = (rec.getId() != null);
        		jam.setEnabled(true);
        		parejaLayout.setVisible(jam.getValue());
        		idCatSpeed.setEnabled(speed.getValue() && tipoForm==InscripcionEnum.INSCRIPCION);
        		idCatSalto.setEnabled(salto.getValue() && tipoForm==InscripcionEnum.INSCRIPCION);
        		idCatDerrapes.setEnabled(derrapes.getValue() && tipoForm==InscripcionEnum.INSCRIPCION);
        		idCatJam.setEnabled(jam.getValue() && tipoForm==InscripcionEnum.INSCRIPCION);
        		idCatClassic.setEnabled(classic.getValue() && tipoForm==InscripcionEnum.INSCRIPCION);
        		idCatBattle.setEnabled(battle.getValue() && tipoForm==InscripcionEnum.INSCRIPCION);
        		if(rec.getJam() && rec.getIdPareja()==null){
		            parejaBtn.setEnabled(false);
			        jam.setEnabled(false);
			        idCatJam.setEnabled(false);
        		}
        	}
        }
        parejaJam.setVisible(false);
        nombre.setEnabled(false);
        apellidos.setEnabled(false);
        fechaNacimiento.setEnabled(false);
        genero.setEnabled(false);
        clubStr.setEnabled(false);
        fichaFederativa.setEnabled(false);
        idPareja.setEnabled(false);
		nombrePareja.setEnabled(false);
		apellidosPareja.setEnabled(false);
    }
    
}