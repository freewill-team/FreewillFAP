package freewill.nextgen.gestioncategorias;

import java.io.File;
import java.util.Collection;
import java.util.List;

import com.vaadin.addon.contextmenu.ContextMenu;
import com.vaadin.addon.contextmenu.MenuItem;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.data.sort.SortDirection;
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
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.data.CompeticionEntity;
import freewill.nextgen.data.CategoriaEntity;
import freewill.nextgen.data.CategoriaEntity.AccionEnum;
import freewill.nextgen.genericCrud.GenericGrid;
import freewill.nextgen.hmi.utils.Export2Xls;
import freewill.nextgen.hmi.utils.Messages;

/**
 * A view for performing create-read-update-delete operations on records.
 *
 * See also {@link StatusCrudLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */

@SuppressWarnings("serial")
public class GestionCrudGrid extends CssLayout {

	@SuppressWarnings("unused")
	private static String ICC_PERMISSION = "ICC_ALARMS_PERMISSION";
    public final String VIEW_NAME = Messages.get().getKey("gestioncategorias");
    private GenericGrid<CategoriaEntity> grid;
    private GestionCrudLogic viewLogic = new GestionCrudLogic(this);
    private Long competicion = null;
    private CompeticionEntity competi = null;
    private Label competicionLabel = null;
    private VerticalLayout barAndGridLayout = null;
    private boolean edicionAbierta = true;
    //private GestionCrudView parent = null;
    private ContextMenu menu = null;
    
    public GestionCrudGrid(Long competicion, GestionCrudView parent) {
    	this.competicion = competicion;
    	
        setSizeFull();
        addStyleName("crud-view");        
        HorizontalLayout topLayout = createTopBar();
        
        grid = new GenericGrid<CategoriaEntity>(CategoriaEntity.class,
            	"id", "modalidad", "nombre", "genero", "hombres", "mujeres", "total", "accion");	
        grid.addSelectionListener(new SelectionListener() {
            @Override
            public void select(SelectionEvent event) {
                //viewLogic.rowSelected(grid.getSelectedRow(), competi, preinscripcionAbierta);
            }
        });
        
        // Menu contextual con acciones disponibles
        menu = new ContextMenu(grid, true);
        for(AccionEnum accion:AccionEnum.values()){
        	final MenuItem basic = menu.addItem(accion.toString(), e -> {
        		if(edicionAbierta)
        			viewLogic.executeAction(getSelectedRow(), accion, competicion);
        		else
        			showError("Ya estamos fuera del período de edicion.");
            });
            basic.setIcon(FontAwesome.PARAGRAPH);
        }

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
        
        edicionAbierta = true;
    	competi = viewLogic.getCompeticion(this.competicion);
    	if(competi!=null){
    		competicionLabel.setValue("Gestión Categorias "+competi.getNombre());
    		viewLogic.init(competi);
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
    				(List<CategoriaEntity>)grid.getContainerDataSource().getItemIds(),
    				CategoriaEntity.class,
    				competicionLabel.getValue().toUpperCase(),
    				"modalidad", "nombre", "genero", "hombres", "mujeres", "total");
    		if(file!=null){
    			FileResource resource = new FileResource(file);
    			Page.getCurrent().open(resource, "Export File", false);
    		    // Finally, removes the temporal file
    		    // file.delete();
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

    public void selectRow(CategoriaEntity row) {
        ((SelectionModel.Single) grid.getSelectionModel()).select(row);
    }

    public CategoriaEntity getSelectedRow() {
        return grid.getSelectedRow();
    }

    public void showRecords(Collection<CategoriaEntity> records) {
        grid.setRecords(records);
        grid.sort("modalidad", SortDirection.ASCENDING);
    }

    public void refreshRecord(CategoriaEntity rec) {
        grid.refresh(rec);
        grid.scrollTo(rec);
    }
    
    public void removeRecord(CategoriaEntity rec) {
        grid.remove(rec);
    }

	public void setEdicionAbierta(boolean b) {
		edicionAbierta = b;
	}
	
}
