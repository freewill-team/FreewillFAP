package freewill.nextgen.deliverable;

import java.util.Collection;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
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

import freewill.nextgen.data.CrudViewInterface;
import freewill.nextgen.data.DeliverableEntity;
import freewill.nextgen.data.ProjectEntity;
import freewill.nextgen.hmi.common.GenericCombo;
import freewill.nextgen.hmi.utils.Messages;

/**
 * A view for performing create-read-update-delete operations on records.
 *
 * See also {@link StatusCrudLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */
@SuppressWarnings("serial")
public class DeliverableCrudView extends CssLayout implements CrudViewInterface<DeliverableEntity> /*View*/ {

    public final String VIEW_NAME = Messages.get().getKey("deliverablecrudview.viewname");
    private DeliverableGrid grid = null;
    private DeliverableForm form = null;
    private DeliverableCrudLogic viewLogic = new DeliverableCrudLogic(this);
    private Button newRecord = null;
    private GenericCombo<ProjectEntity> projectcb = null;
    private ProjectEntity currentProject = null;
    
    public String getName(){
    	return VIEW_NAME;
    }

    public DeliverableCrudView() {
    	//System.out.println("Entrando en DeliverableCrudView, instance = "+new Random().nextInt());
        setSizeFull();
        addStyleName("crud-view");
        HorizontalLayout topLayout = createTopBar();

        grid = new DeliverableGrid();
        grid.addSelectionListener(new SelectionListener() {

            @Override
            public void select(SelectionEvent event) {
            	SelectionModel selectionModel = grid.getSelectionModel();
            	if (selectionModel instanceof SelectionModel.Single)
            		viewLogic.rowSelected(grid.getSelectedRow());
            }
        });
        
        form = new DeliverableForm(viewLogic);

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

	private HorizontalLayout createTopBar() {
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
        
        projectcb = new GenericCombo<ProjectEntity>(ProjectEntity.class);
        
        projectcb.addValueChangeListener(new ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
            	if (projectcb.getValue() != null) {
            		viewLogic.setProject(getProject());
                }
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
        topLayout.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
        topLayout.setWidth("100%");
        topLayout.addComponent(filter);
        topLayout.addComponent(projectcb);
        topLayout.addComponent(newRecord);
        topLayout.setComponentAlignment(filter, Alignment.MIDDLE_LEFT);
        topLayout.setExpandRatio(newRecord, 1);
        topLayout.setStyleName("top-bar");
        return topLayout;
    }

    @Override
    public void enter(ViewChangeEvent event) {
    	projectcb.Refresh();
    	//viewLogic.setProject(this.getProject());
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

    public void selectRow(DeliverableEntity row) {
        ((SelectionModel.Single) grid.getSelectionModel()).select(row);
    }

    public DeliverableEntity getSelectedRow() {
        return grid.getSelectedRow();
    }

    public void editRecord(DeliverableEntity rec) {
        if (rec != null) {
            form.addStyleName("visible");
            form.setEnabled(true);
            if(currentProject!=null)
            	form.editRecord(rec, currentProject.getActive());
            else
            	form.editRecord(rec, false);
        } else {
            form.removeStyleName("visible");
            form.setEnabled(false);
        }
    }

    public void showRecords(Collection<DeliverableEntity> records) {
        grid.setRecords(records);
    }

    public void refreshRecord(DeliverableEntity rec) {
        grid.refresh(rec);
        grid.scrollTo(rec);
    }

    public void removeRecord(DeliverableEntity rec) {
        grid.remove(rec);
    }

    public Long getProject() {
    	if (projectcb.getValue() != null) {
    		currentProject = (ProjectEntity) projectcb.getValue();
        	if(!currentProject.getActive()){
        		showSaveNotification(Messages.get().getKey("projectdisabled"));
        	}
        	return currentProject.getID();
        }
    	return null;
    }
    
}
