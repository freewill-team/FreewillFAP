package freewill.nextgen.config;

import java.util.Collection;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid.SelectionModel;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.data.ConfigEntity;
import freewill.nextgen.hmi.utils.Messages;

/**
 * A view for performing create-read-update-delete operations on records.
 *
 * See also {@link StatusCrudLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
@SuppressWarnings("serial")
public class ConfigCrudView extends CssLayout implements View {

    public final String VIEW_NAME = Messages.get().getKey("configcrudview.viewname");
    private ConfigGrid grid = null;
    private ConfigForm form = null;
    private ConfigCrudLogic viewLogic = new ConfigCrudLogic(this);
    private Button newRecord = null;

    public ConfigCrudView() {
    	
    	//System.out.println("Entrando en FileCrudView, instance = "+new Random().nextInt());
    	
        setSizeFull();
        addStyleName("crud-view");
        HorizontalLayout topLayout = createTopBar();

        grid = new ConfigGrid();
        grid.addSelectionListener(new SelectionListener() {

            @Override
            public void select(SelectionEvent event) {
                viewLogic.rowSelected(grid.getSelectedRow());
            }
        });

        form = new ConfigForm(viewLogic);

        VerticalLayout barAndGridLayout = new VerticalLayout();
        barAndGridLayout.addComponent(topLayout);
        barAndGridLayout.addComponent(grid);
        barAndGridLayout.setMargin(true);
        barAndGridLayout.setSpacing(true);
        barAndGridLayout.setSizeFull();
        barAndGridLayout.setExpandRatio(grid, 1);
        barAndGridLayout.setStyleName("crud-main-layout");

        addComponent(barAndGridLayout);
        addComponent(form);

        //viewLogic.init();
    }

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
        
        newRecord = new Button(Messages.get().getKey("new"));
        newRecord.addStyleName(ValoTheme.BUTTON_PRIMARY);
        newRecord.setIcon(FontAwesome.PLUS_CIRCLE);
        newRecord.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	viewLogic.newRecord();
            }
        });
                
        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setSpacing(true);
        topLayout.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        topLayout.setWidth("100%");
        topLayout.addComponent(filter);
        topLayout.addComponent(newRecord);
        topLayout.setComponentAlignment(newRecord, Alignment.MIDDLE_RIGHT);
        topLayout.setExpandRatio(newRecord, 1);
        topLayout.setStyleName("top-bar");
        return topLayout;
    }

    @Override
    public void enter(ViewChangeEvent event) {
    	viewLogic.init();
        viewLogic.enter(event.getParameters());
    }

    public void showError(String msg) {
        Notification.show(msg, Type.ERROR_MESSAGE);
    }

    public void showSaveNotification(String msg) {
        Notification.show(msg, Type.TRAY_NOTIFICATION);
    }

    public void setNewRecordEnabled(boolean enabled) {
    	newRecord.setEnabled(enabled);
    }

    public void clearSelection() {
        grid.getSelectionModel().reset();
    }

    public void selectRow(ConfigEntity row) {
        ((SelectionModel.Single) grid.getSelectionModel()).select(row);
    }

    public ConfigEntity getSelectedRow() {
        return grid.getSelectedRow();
    }

    public void editRecord(ConfigEntity rec) {
        if (rec != null) {
            form.addStyleName("visible");
            form.setEnabled(true);
            form.editRecord(rec);
        } else {
            form.removeStyleName("visible");
            form.setEnabled(false);
        }
    }

    public void showRecords(Collection<ConfigEntity> records) {
        grid.setRecords(records);
    }

    public void refreshRecord(ConfigEntity rec) {
        grid.refresh(rec);
        grid.scrollTo(rec);
    }

    public void removeRecord(ConfigEntity rec) {
        grid.remove(rec);
    }
    
}
