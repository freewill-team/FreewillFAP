package freewill.nextgen.preinscripcion;

import java.io.File;
import java.util.Collection;
import java.util.List;

import com.vaadin.event.FieldEvents;
import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid.SelectionModel;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.data.CompeticionEntity;
import freewill.nextgen.data.InscripcionEntity;
import freewill.nextgen.data.PatinadorEntity;
import freewill.nextgen.genericCrud.GenericGrid;
import freewill.nextgen.hmi.utils.Export2Xls;
import freewill.nextgen.hmi.utils.Messages;
import freewill.nextgen.preinscripcion.PreinscripcionCrudView.InscripcionEnum;

/**
 * A view for performing create-read-update-delete operations on records.
 *
 * See also {@link StatusCrudLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */

@SuppressWarnings("serial")
public class PreinscripcionCrudGrid extends CssLayout {

	@SuppressWarnings("unused")
	private static String ICC_PERMISSION = "ICC_ALARMS_PERMISSION";
    public final String VIEW_NAME = Messages.get().getKey("inscripciones");
    private GenericGrid<PatinadorEntity> grid;
    private PreinscripcionCrudLogic viewLogic = new PreinscripcionCrudLogic(this);
    private Long competicion = null;
    private CompeticionEntity competi = null;
    private Label competicionLabel = null;
    private VerticalLayout barAndGridLayout = null;
    private PreinscripcionForm form;
    private boolean preinscripcionAbierta = true;
    private InscripcionEnum tipoForm = InscripcionEnum.INSCRIPCION;
    //private PreinscripcionCrudView parent = null;
    private FichaInscripcionForm ficha;
    private InscripcionEntity inscripcion = null;
    private Button sendButton = null;
    
    public PreinscripcionCrudGrid(InscripcionEnum tipo, Long competicion, 
    		PreinscripcionCrudView parent) {
    	this.tipoForm = tipo;
    	this.competicion = competicion;
    	
        setSizeFull();
        addStyleName("crud-view");        
        HorizontalLayout topLayout = createTopBar();
        
        grid = new GenericGrid<PatinadorEntity>(PatinadorEntity.class,
            	"id", "nombre", "apellidos", "clubStr", 
            	"speed", "salto", "derrapes", "classic", "battle", "jam" );	
        grid.addSelectionListener(new SelectionListener() {
            @Override
            public void select(SelectionEvent event) {
                viewLogic.rowSelected(grid.getSelectedRow(), competi, preinscripcionAbierta);
            }
        });
        
        form = new PreinscripcionForm(viewLogic);
        ficha = new FichaInscripcionForm(viewLogic);

        barAndGridLayout = new VerticalLayout();
        //barAndGridLayout.addComponent(new GenericHeader(VIEW_NAME, FontAwesome.FOLDER));
        barAndGridLayout.addComponent(topLayout);
        barAndGridLayout.addComponent(grid);
        barAndGridLayout.setMargin(true);
        barAndGridLayout.setSpacing(true);
        barAndGridLayout.setSizeFull();
        barAndGridLayout.setExpandRatio(grid, 1);
        barAndGridLayout.setStyleName("crud-main-layout");
        
        addComponent(barAndGridLayout);
        addComponent(form);
        addComponent(ficha);
        
        preinscripcionAbierta = true;
       
    	competi = viewLogic.getCompeticion(this.competicion);
    	if(competi!=null){
    		if(tipoForm == InscripcionEnum.PREINSCRIPCION)
    			competicionLabel.setValue("Pre-Inscripciones "+competi.getNombre());
    	    else
    	    	competicionLabel.setValue("Inscripciones "+competi.getNombre());
    		viewLogic.init(competi, tipoForm);
    		grid.getColumn("speed").setHidden(!competi.getSpeed());
    		grid.getColumn("salto").setHidden(!competi.getSalto());
    		grid.getColumn("derrapes").setHidden(!competi.getDerrapes());
    		grid.getColumn("classic").setHidden(!competi.getClassic());
    		grid.getColumn("battle").setHidden(!competi.getBattle());
    		grid.getColumn("jam").setHidden(!competi.getJam());
    	}
    	
    	inscripcion = viewLogic.getFichaInscripcion(competicion);
    	if(inscripcion!=null){
    		this.editRecord(inscripcion, competi, preinscripcionAbierta);
    		if(inscripcion.getEnviado())
    			preinscripcionAbierta = false;
    	}
    	else{
    		preinscripcionAbierta = false;
    	}
    	
    }

	@SuppressWarnings({ "unchecked", "deprecation" })
	public HorizontalLayout createTopBar() {
		
        TextField filter = new TextField();
        filter.setStyleName("filter-textfield");
        filter.setInputPrompt("Filter");
        
        filter.setImmediate(true);
        filter.addTextChangeListener(new FieldEvents.TextChangeListener() {
            @Override
            public void textChange(FieldEvents.TextChangeEvent event) {
                grid.setFilter(event.getText());
            }
        });
        
        competicionLabel = new Label("");
        competicionLabel.setStyleName(ValoTheme.LABEL_LARGE);
        competicionLabel.addStyleName(ValoTheme.LABEL_COLORED);
        competicionLabel.addStyleName(ValoTheme.LABEL_BOLD);
        
        Button printButton = new Button(Messages.get().getKey("acta"));
		printButton.setIcon(FontAwesome.DOWNLOAD);
		printButton.addClickListener(e -> {
    		File file = Export2Xls.get().createXLS(
    				(List<PatinadorEntity>)grid.getContainerDataSource().getItemIds(),
    				PatinadorEntity.class,
    				competicionLabel.getValue().toUpperCase(),
    				"nombre", "apellidos", "clubStr", 
    				"catSpeed", "catSalto", "catDerrapes", "catClassic", "catBattle", "catJam");
    		if(file!=null){
    			FileResource resource = new FileResource(file);
    			Page.getCurrent().open(resource, "Export File", false);
    		    // Finally, removes the temporal file
    		    // file.delete();
    		}
        });
		
		sendButton = new Button(Messages.get().getKey("inscripcion"));
		sendButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		sendButton.setIcon(FontAwesome.SEND);
		sendButton.addClickListener(new ClickListener() {
	    	@Override
	        public void buttonClick(ClickEvent event) {
	            viewLogic.editRecord(inscripcion, competi, preinscripcionAbierta);
	        }
	    });

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
        topLayout.setSpacing(true);
        //topLayout.setMargin(true);
        topLayout.setWidth("100%");
        topLayout.addComponent(competicionLabel);
        topLayout.addComponent(filter);
        topLayout.addComponent(printButton);
        topLayout.addComponent(sendButton);
        topLayout.setComponentAlignment(competicionLabel, Alignment.MIDDLE_LEFT);
        topLayout.setExpandRatio(competicionLabel, 1);
        topLayout.setStyleName("top-bar");
        return topLayout;
    }

    public void showError(String msg) {
        Notification.show(msg, Type.ERROR_MESSAGE);
    }

    public void showSaveNotification(String msg) {
        Notification.show(msg, Type.TRAY_NOTIFICATION);
    }

    public void clearSelection() {
    	try{
    		grid.getSelectionModel().reset();
    	}
    	catch(Exception e){
    		System.out.println("clearSelection: "+e.getMessage());
    	}
    }

    public void selectRow(PatinadorEntity row) {
        ((SelectionModel.Single) grid.getSelectionModel()).select(row);
    }

    public PatinadorEntity getSelectedRow() {
        return grid.getSelectedRow();
    }

    public void editRecord(PatinadorEntity rec, CompeticionEntity competi, boolean preinscripcionAbierta) {    
        if (rec != null) {
            form.addStyleName("visible");
            form.setEnabled(true);
        } else {
            form.removeStyleName("visible");
            form.setEnabled(false);
        }
        form.editRecord(rec, competi, preinscripcionAbierta, tipoForm);
    }

    public void showRecords(Collection<PatinadorEntity> records) {
        grid.setRecords(records);
    }

    public void refreshRecord(PatinadorEntity rec) {
        grid.refresh(rec);
        grid.scrollTo(rec);
    }

    public void removeRecord(PatinadorEntity rec) {
        grid.remove(rec);
    }

	public void setPreinscripcionAbierta(boolean b) {
		preinscripcionAbierta = b;
	}

	public void editRecord(InscripcionEntity rec, CompeticionEntity competi, boolean preinscripcionAbierta) {    
        if (rec != null) {
        	ficha.addStyleName("visible");
            ficha.setEnabled(true);
        } else {
        	ficha.removeStyleName("visible");
            ficha.setEnabled(false);
        }
        ficha.editRecord(rec, competi, preinscripcionAbierta);
	}

	public void setFichaInscripcion(InscripcionEntity rec) {
		inscripcion = rec;
	}
	
}
