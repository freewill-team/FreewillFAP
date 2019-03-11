package freewill.nextgen.palmares;

import java.io.File;
import java.util.Collection;
import java.util.List;

import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
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
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.circuito.SelectCircuito;
import freewill.nextgen.competicion.SelectCompeticion;
import freewill.nextgen.data.ParticipanteEntity;
import freewill.nextgen.data.PatinadorEntity;
import freewill.nextgen.data.RankingEntity;
import freewill.nextgen.genericCrud.GenericGrid;
import freewill.nextgen.hmi.utils.Export2Xls;
import freewill.nextgen.hmi.utils.Messages;
import freewill.nextgen.patinador.SelectPatinadorDialog;

/**
 * A view for performing create-read-update-delete operations on records.
 *
 * See also {@link StatusCrudLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */

@SuppressWarnings("serial")
public class PalmaresCrudView extends CssLayout implements View {

	@SuppressWarnings("unused")
	private static String ICC_PERMISSION = "ICC_ALARMS_PERMISSION";
    public final String VIEW_NAME = Messages.get().getKey("palmares");
    private GenericGrid<ParticipanteEntity> grid;
    private PalmaresCrudLogic viewLogic = new PalmaresCrudLogic(this);
    private Label patinadorLabel = null;
    private VerticalLayout barAndGridLayout = null;
    
    public PalmaresCrudView() {
        setSizeFull();
        addStyleName("crud-view");        
        HorizontalLayout topLayout = createTopBar();
        
        grid = new GenericGrid<ParticipanteEntity>(ParticipanteEntity.class,
            	"id", "fecha", "competicionStr", "categoriaStr", "clasificacion", 
            	"mejorMarca", "clubStr");
        grid.addSelectionListener(new SelectionListener() {
            @Override
            public void select(SelectionEvent event) {
                viewLogic.rowSelected(grid.getSelectedRow());
            }
        });

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
    }

	@SuppressWarnings({ "unchecked", "deprecation" })
	public HorizontalLayout createTopBar() {
        
        patinadorLabel = new Label("");
        patinadorLabel.setStyleName(ValoTheme.LABEL_LARGE);
        patinadorLabel.addStyleName(ValoTheme.LABEL_COLORED);
        patinadorLabel.addStyleName(ValoTheme.LABEL_BOLD);
        
        Button findRecord = new Button(Messages.get().getKey("patinador"));
        findRecord.addStyleName(ValoTheme.BUTTON_PRIMARY);
        findRecord.setIcon(FontAwesome.SEARCH);
        findRecord.addClickListener(new ClickListener() {
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
            				patinadorLabel.setValue(
            						user.getNombre()+" "+user.getApellidos()+
            						" (F.Fed.: "+user.getFichaFederativa()+")");
                    		viewLogic.findRecords(user.getId());
            			}
                    }
                });
            	getUI().addWindow(cd);
            }
        });

        Button printButton = new Button(Messages.get().getKey("acta"));
		//printButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		printButton.setIcon(FontAwesome.DOWNLOAD);
		printButton.addClickListener(e -> {
    		File file = Export2Xls.get().createXLS(
    				(List<ParticipanteEntity>)grid.getContainerDataSource().getItemIds(),
    				ParticipanteEntity.class,
    				("Palmarés "+patinadorLabel.getValue()).toUpperCase(),
    				"fecha", "competicionStr", "categoriaStr", "clasificacion",
    				"mejorMarca", "clubStr");
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
        topLayout.addComponent(findRecord);
        topLayout.addComponent(patinadorLabel);
        topLayout.addComponent(printButton);
        //topLayout.setComponentAlignment(patinadorLabel, Alignment.MIDDLE_LEFT);
        topLayout.setExpandRatio(patinadorLabel, 1);
        topLayout.setStyleName("top-bar");
        return topLayout;
    }

    @Override
    public void enter(ViewChangeEvent event) {
    	viewLogic.enter(event.getParameters());
    	patinadorLabel.setValue("... Seleccione un patinador");
    	grid.setRecords(null);
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

    public void selectRow(ParticipanteEntity row) {
        ((SelectionModel.Single) grid.getSelectionModel()).select(row);
    }

    public ParticipanteEntity getSelectedRow() {
        return grid.getSelectedRow();
    }

    public void editRecord(ParticipanteEntity rec) {    
        /*if (rec != null) {
            form.addStyleName("visible");
            form.setEnabled(true);
        } else {
            form.removeStyleName("visible");
            form.setEnabled(false);
        }
        form.editRecord(rec);*/
    }

    public void showRecords(Collection<ParticipanteEntity> records) {
        grid.setRecords(records);
        if(records!=null && records.size()>0)
        	grid.sort("fecha", SortDirection.DESCENDING);
    }

    public void refreshRecord(ParticipanteEntity rec) {
        grid.refresh(rec);
        grid.scrollTo(rec);
    }

    public void removeRecord(ParticipanteEntity rec) {
        grid.remove(rec);
    }
	
}
