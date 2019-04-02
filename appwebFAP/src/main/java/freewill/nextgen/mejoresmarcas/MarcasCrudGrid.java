package freewill.nextgen.mejoresmarcas;

import java.io.File;
import java.util.Collection;
import java.util.List;

import com.vaadin.addon.contextmenu.ContextMenu;
import com.vaadin.addon.contextmenu.MenuItem;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid.SelectionModel;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.data.ParticipanteEntity;
import freewill.nextgen.data.CategoriaEntity.ModalidadEnum;
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
public class MarcasCrudGrid extends CssLayout {

	@SuppressWarnings("unused")
	private static String ICC_PERMISSION = "ICC_ALARMS_PERMISSION";
    public final String VIEW_NAME = Messages.get().getKey("mejoresmarcas");
    private GenericGrid<ParticipanteEntity> grid;
    private MarcasCrudLogic viewLogic = new MarcasCrudLogic(this);
    private Label competicionLabel = null;
    private VerticalLayout barAndGridLayout = null;
    //private GestionCrudView parent = null;
    private ComboBox modalidad;
    
    public MarcasCrudGrid(MarcasCrudView parent) {
        setSizeFull();
        addStyleName("crud-view");        
        HorizontalLayout topLayout = createTopBar();
        
        grid = new GenericGrid<ParticipanteEntity>(ParticipanteEntity.class,
            "id", "fecha", "competicionStr", "categoriaStr", "nombre", "apellidos", "mejorMarca");
        
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
        
        modalidad.setValue(ModalidadEnum.SPEED);
        //viewLogic.init(ModalidadEnum.SPEED, "ASC");
    }

	@SuppressWarnings({ "unchecked", "deprecation" })
	public HorizontalLayout createTopBar() {
		
		modalidad = new ComboBox();
		modalidad.setNullSelectionAllowed(false);
		modalidad.addItem(ModalidadEnum.SPEED);
		modalidad.addItem(ModalidadEnum.JUMP);
		modalidad.addValueChangeListener(new ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                if (modalidad.getValue() != null) {
                	competicionLabel.setValue(VIEW_NAME+" "+modalidad.getValue());
                	if(modalidad.getValue()==ModalidadEnum.SPEED)
                		viewLogic.init(ModalidadEnum.SPEED, "ASC");
                	else
                		viewLogic.init(ModalidadEnum.JUMP, "DESC");
                }
            }
        });
		
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
    				(List<ParticipanteEntity>)grid.getContainerDataSource().getItemIds(),
    				ParticipanteEntity.class,
    				competicionLabel.getValue().toUpperCase(),
    				"fecha", "competicionStr", "categoriaStr", "nombre", "apellidos", "mejorMarca");
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
        topLayout.addComponent(modalidad);
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

    public void showRecords(Collection<ParticipanteEntity> records) {
        grid.setRecords(records);
        if(modalidad.getValue()==ModalidadEnum.SPEED)
        	grid.sort("mejorMarca", SortDirection.ASCENDING);
        else
        	grid.sort("mejorMarca", SortDirection.DESCENDING);
    }
	
}
