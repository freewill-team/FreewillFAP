package freewill.nextgen.dorsal;

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
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.data.CompeticionEntity;
import freewill.nextgen.data.PatinadorEntity;
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
public class DorsalCrudGrid extends CssLayout {

	@SuppressWarnings("unused")
	private static String ICC_PERMISSION = "ICC_ALARMS_PERMISSION";
    public final String VIEW_NAME = Messages.get().getKey("registro");
    private GenericGrid<PatinadorEntity> grid;
    private DorsalCrudLogic viewLogic = new DorsalCrudLogic(this);
    private Long competicion = null;
    private CompeticionEntity competi = null;
    private Label competicionLabel = null;
    private VerticalLayout barAndGridLayout = null;
    private DorsalForm form;
    private boolean checkinAbierto = true;
    
    public DorsalCrudGrid(Long competicion, DorsalCrudView parent) {
    	this.competicion = competicion;
    	
        setSizeFull();
        addStyleName("crud-view");        
        HorizontalLayout topLayout = createTopBar();
        
        grid = new GenericGrid<PatinadorEntity>(PatinadorEntity.class,
            	"id", "dorsal", "nombre", "apellidos", "clubStr", 
        		"speed", "salto", "derrapes", "classic", "battle", "jam" );	
        grid.addSelectionListener(new SelectionListener() {
            @Override
            public void select(SelectionEvent event) {
                viewLogic.rowSelected(grid.getSelectedRow(), competicion, checkinAbierto);
            }
        });
        
        form = new DorsalForm(viewLogic);

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
        
        competi = viewLogic.getCompeticion(this.competicion);
    	if(competi!=null){
    		competicionLabel.setValue(VIEW_NAME+" "+competi.getNombre());
    		viewLogic.init(competi);
    		grid.getColumn("speed").setHidden(!competi.getSpeed());
    		grid.getColumn("salto").setHidden(!competi.getSalto());
    		grid.getColumn("derrapes").setHidden(!competi.getDerrapes());
    		grid.getColumn("classic").setHidden(!competi.getClassic());
    		grid.getColumn("battle").setHidden(!competi.getBattle());
    		grid.getColumn("jam").setHidden(!competi.getJam());
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
		//printButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		printButton.setIcon(FontAwesome.DOWNLOAD);
		printButton.addClickListener(e -> {
    		File file = Export2Xls.get().createXLS(
    				(List<PatinadorEntity>)grid.getContainerDataSource().getItemIds(),
    				PatinadorEntity.class,
    				competicionLabel.getValue().toUpperCase(),
    				"id", "dorsal", "nombre", "apellidos", "clubStr", 
                	"speed", "classic", "battle", "jam", "derrapes", "salto");	
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
        Notification.show(msg, Type.WARNING_MESSAGE); //ERROR_MESSAGE);
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

    public void editRecord(PatinadorEntity rec, Long competicion, boolean checkinAbierto) {
        if (rec != null) {
            form.addStyleName("visible");
            form.setEnabled(true);
        } else {
            form.removeStyleName("visible");
            form.setEnabled(false);
        }
        form.editRecord(rec, competicion, checkinAbierto);
    }

    public void showRecords(Collection<PatinadorEntity> records) {
        grid.setRecords(records);
    }

    public void refreshRecord(PatinadorEntity rec) {
        grid.refresh(rec);
        grid.scrollTo(rec);
    }

    public void removeRecord(PatinadorEntity rec) {
        // Not allowed here grid.remove(rec);
    }
	
	public void setCheckinAbierto(boolean b) {
		checkinAbierto = b;
	}
	
}
