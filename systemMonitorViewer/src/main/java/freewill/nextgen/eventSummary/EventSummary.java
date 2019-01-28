package freewill.nextgen.eventSummary;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid.SelectionModel;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.common.entities.EventEntity;
import freewill.nextgen.hmi.utils.Export2Xls;
import freewill.nextgen.hmi.utils.Messages;

/**
 * A view for performing create-read-update-delete operations on records.
 *
 * See also {@link StatusCrudLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */

@SuppressWarnings("serial")
public class EventSummary extends CssLayout implements View {

    public final String VIEW_NAME = Messages.get().getKey("events");
    private EventGrid grid = null;
    private EventSummaryLogic viewLogic = new EventSummaryLogic(this);
    //private Button delete = null;

    public EventSummary() {
    	
        setSizeFull();
        addStyleName("crud-view");
        HorizontalLayout topLayout = createTopBar();

        grid = new EventGrid(this);
        grid.setStyleName(ValoTheme.TABLE_SMALL);
        grid.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
        
        VerticalLayout barAndGridLayout = new VerticalLayout();
        barAndGridLayout.addComponent(topLayout);
        barAndGridLayout.addComponent(grid);
        barAndGridLayout.setMargin(false);
        barAndGridLayout.setSpacing(false);
        barAndGridLayout.setSizeFull();
        barAndGridLayout.setExpandRatio(grid, 1);
        barAndGridLayout.setStyleName("crud-main-layout");

        addComponent(barAndGridLayout);

        grid.addSelectionListener(new SelectionListener() {

            @Override
            public void select(SelectionEvent event) {
                viewLogic.rowSelected(grid.getSelectedRow());
            }
        });
        
    }

	public HorizontalLayout createTopBar() {
		
		Button exportBtn = new Button(Messages.get().getKey("export2excel"));
		exportBtn.setIcon(FontAwesome.FILE_EXCEL_O);
		exportBtn.addClickListener(new ClickListener() {
			@SuppressWarnings({ "unchecked", "deprecation" })
			@Override
            public void buttonClick(ClickEvent event) {
				File file = Export2Xls.get().createXLS((List<EventEntity>)grid.getContainerDataSource().getItemIds(),
						EventEntity.class, "timestamp", "severity", "point", "pointType", "message", 
						"parentPoint", "category", "username", "console");
				if(file!=null){
					FileResource resource = new FileResource(file);
					Page.getCurrent().open(resource, "Export File", false);
		    		// Finally, removes the temporal file
		    		// file.delete();
				}
            }
        });
        
        /*delete = new Button("Delete Events"); //Messages.getInstance().getKey("deleteevents"));
        delete.addStyleName(ValoTheme.BUTTON_DANGER);
        delete.setIcon(FontAwesome.WARNING);
        delete.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	ConfirmDialog cd = new ConfirmDialog("Do you really want to remove these Events?");
            	cd.setOKAction(new ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
            			cd.close();
            			//viewLogic.deleteEvents();
                    }
                });
            	getUI().addWindow(cd);
            }
        });*/
        
        Label expander = new Label();
                
        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setMargin(true);
        topLayout.setSpacing(true);
        topLayout.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        topLayout.setWidth("100%");
        topLayout.addComponent(exportBtn);
        topLayout.addComponent(expander);
        //topLayout.addComponent(delete);
        topLayout.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        topLayout.setExpandRatio(expander, 1);
        topLayout.setStyleName("top-bar");
        return topLayout;
    }

    @Override
    public void enter(ViewChangeEvent event) {
    	Date edate = new Date();
    	Date sdate = new Date(edate.getTime()-86400000L); // Last 24 hours
    	viewLogic.init(sdate, edate);
    	if(event!=null)
    		viewLogic.enter(event.getParameters());
        //delete.setEnabled(!EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.USER));
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

    public void selectRow(EventEntity row) {
        ((SelectionModel.Single) grid.getSelectionModel()).select(row);
    }

    public EventEntity getSelectedRow() {
        return grid.getSelectedRow();
    }

    public void showRecords(Collection<EventEntity> records) {
        grid.setRecords(records);
    }
    
    public EventGrid getGrid(){
    	return grid;
    }
    
    public void setFilter(Date startDate, Date endDate){
    	viewLogic.init(startDate, endDate);
    }

}
