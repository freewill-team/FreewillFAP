package freewill.nextgen.gestioncategorias;

import org.apache.log4j.Logger;

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
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.data.CategoriaEntity;
import freewill.nextgen.data.ParticipanteEntity;
import freewill.nextgen.genericCrud.GenericGrid;
import freewill.nextgen.hmi.utils.Messages;

/**
 * A form for editing a single record.
 *
 * Using responsive layouts, the form can be displayed either sliding out on the
 * side of the view or filling the whole screen - see the theme for the related
 * CSS rules.
 */
@SuppressWarnings("serial")
public class GestionForm extends GestionFormDesign {

    private GestionCrudLogic viewLogic;
    private BeanFieldGroup<CategoriaEntity> fieldGroup;
    private GenericGrid<ParticipanteEntity> patines = null;
    private Logger log = Logger.getLogger(GestionForm.class);

    @SuppressWarnings("rawtypes")
    public GestionForm(GestionCrudLogic sampleCrudLogic) {
        super();
        addStyleName("product-form");
        viewLogic = sampleCrudLogic;

        fieldGroup = new BeanFieldGroup<CategoriaEntity>(CategoriaEntity.class);
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
            	System.out.println("Entrando en preCommit...");
            }
            
            @Override
            public void postCommit(CommitEvent commitEvent)
                    throws CommitException {
            	System.out.println("Entrando en postCommit...");	
            	/*CategoriaEntity rec = fieldGroup.getItemDataSource().getBean();	
            	if(viewLogic!=null)
            		viewLogic.saveRecord(rec);
            	else 
            		removeStyleName("visible");*/
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
        
        patines = new GenericGrid<ParticipanteEntity>(
        		ParticipanteEntity.class, "id", "nombre", "apellidos", "categoriaStr");
        patines.setStyleName(ValoTheme.TABLE_SMALL);
        patinLayout.addComponent(patines);
        patinLayout.setCaption(Messages.get().getKey("participantes"));
        patinLayout.setHeight("300px");
        
    }

	public void editRecord(Long competicion, CategoriaEntity rec) {
        if (rec == null) {
            rec = new CategoriaEntity();
            return;
        }
        fieldGroup.setItemDataSource(new BeanItem<CategoriaEntity>(rec));
        
        // before the user makes any changes, disable validation error indicator
        // of the product name field (which may be empty)
        nombre.setValidationVisible(false);

        // Scroll to the top
        // As this is not a Panel, using JavaScript
        String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
        Page.getCurrent().getJavaScript().execute(scrollScript);
        
        try{
	        patines.setRecords(BltClient.get().executeQuery(
	        		"/getByCompeticionAndCategoria/"+competicion+"/"+rec.getId(),
	        		ParticipanteEntity.class,
	        		EntryPoint.get().getAccessControl().getTokenKey()));
        }
		catch(Exception e){
			log.error(e.getMessage());
		}
        
        formHasChanged();
    }

    private void formHasChanged() {
        // show validation errors after the user has changed something
        nombre.setValidationVisible(true);
        
        // only products that have been saved should be removable
        //boolean canRemoveRecord = false;
        BeanItem<CategoriaEntity> item = fieldGroup.getItemDataSource();
        if (item != null) {
        	CategoriaEntity rec = item.getBean();
        	if(rec!=null){
        		//canRemoveRecord = (rec.getId() != null);
        	}
        }
        //modalidad.setVisible(false);
        //genero.setVisible(false);
    }
    
}
