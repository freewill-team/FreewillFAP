package freewill.nextgen.jshMonitor;

import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.addon.contextmenu.ContextMenu;
import com.vaadin.addon.contextmenu.MenuItem;
import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid.SelectionModel;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.common.entities.JobScheduled;
import freewill.nextgen.common.rtdbclient.RtdbDataService;
import freewill.nextgen.hmi.common.ConfirmDialog;
import freewill.nextgen.hmi.utils.Messages;
import freewill.nextgen.managementConsole.EntryPoint;

@SuppressWarnings("serial")
public class JshMonitor extends CssLayout implements View {
	
	public final String VIEW_NAME = Messages.get().getKey("jobscheduler");
	private JshGrid grid = null;
	private JshForm form = null;
    private JshCrudLogic viewLogic = new JshCrudLogic(this);
    private Button newRecord;
	private Logger log = Logger.getLogger(JshMonitor.class);

	public JshMonitor(){
		setSizeFull();
	    addStyleName("crud-view");
	    HorizontalLayout topLayout = createTopBar();
	    
	    form = new JshForm(viewLogic);
        
        grid = new JshGrid();
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
        addComponent(form);
        
        grid.addSelectionListener(new SelectionListener() {
            @Override
            public void select(SelectionEvent event) {
            	viewLogic.rowSelected(grid.getSelectedRow());
            }
        });
        
        ContextMenu menu = new ContextMenu(grid, true);
        final MenuItem basic = menu.addItem(Messages.get().getKey("toggleactive"), evt -> {
            // Toggle Active
        	try {
        		if(grid.getSelectedRow()==null) return;
	        	JobScheduled rec = (JobScheduled)
	        		BltClient.get().getEntityById(""+grid.getSelectedRow().getId(), JobScheduled.class,
	        				EntryPoint.get().getAccessControl().getTokenKey());
	        	if(rec!=null){
	        		toggleJob(rec);
	        	}
        	}
    		catch(Exception e){
    			log.error(e.getMessage());
    		}
        });
        basic.setIcon(FontAwesome.HAND_O_UP);
        
    }
	
	public HorizontalLayout createTopBar() {
		
        Button ackbtn = new Button(Messages.get().getKey("disableall"));
        ackbtn.addStyleName(ValoTheme.BUTTON_DANGER);
        ackbtn.setIcon(FontAwesome.WARNING);
        ackbtn.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	ConfirmDialog cd = new ConfirmDialog(Messages.get().getKey("disablealljobs"));
            	cd.setOKAction(new ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
            			cd.close();
            			disableAllJobs();
                    }
                });
            	getUI().addWindow(cd);
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
        topLayout.setMargin(true);
        topLayout.setSpacing(true);
        topLayout.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        topLayout.setWidth("100%");
        topLayout.addComponent(newRecord);
        topLayout.addComponent(ackbtn);
        topLayout.setComponentAlignment(ackbtn, Alignment.MIDDLE_RIGHT);
        topLayout.setExpandRatio(ackbtn, 1);
        topLayout.setStyleName("top-bar");
        return topLayout;
    }

	public void Refresh() {
		try{
			//List<JobScheduled> recs = BltClient.get().getEntities(JobScheduled.class,
			//		EntryPoint.get().getAccessControl().getTokenKey());
			// We read the newest info directly from the Rtdb
			List<JobScheduled> recs = RtdbDataService.get().getEntities(JobScheduled.class);
	    	grid.setRecords(recs);
		}
		catch(Exception e){
			log.error(e.getMessage());
		}
    }

	@Override
	public void enter(ViewChangeEvent event) {
		// Activates refresh
		EntryPoint.get().addPollListener(event2 ->Refresh());
		EntryPoint.get().setPollInterval(6000);
	}
	
	private void toggleJob(JobScheduled rec) {
		try{
			boolean result = (boolean)BltClient.get().executeCommand(
					"toggle/"+EntryPoint.get().getAccessControl().getUserLogin()
					+"/"+VaadinService.getCurrentRequest().getRemoteHost(),
					rec, JobScheduled.class,
					EntryPoint.get().getAccessControl().getTokenKey());
			if(result==false)
				showNotification(Messages.get().getKey("togglefailed"));
		}
		catch(Exception e){
			log.error(e.getMessage());
		}
	}
	
	private void disableAllJobs() {
		try{
			for(Object item : grid.getContainerDataSource().getItemIds()){
			    // Now get the actual item from the table.
			    JobScheduled rec = (JobScheduled)
		        		BltClient.get().getEntityById(""+((JobScheduled)item).getId(), JobScheduled.class,
		        				EntryPoint.get().getAccessControl().getTokenKey());
		        	if(rec!=null){
		        		if(rec.getActive())
		        			BltClient.get().executeCommand(
		        				"toggle/"+EntryPoint.get().getAccessControl().getUserLogin()
		        				+"/"+VaadinService.getCurrentRequest().getRemoteHost(),
		        				rec, JobScheduled.class, 
		        				EntryPoint.get().getAccessControl().getTokenKey());
		        	}
			}
		}
		catch(Exception e){
			log.error(e.getMessage());
		}
	}

	public void showNotification(String msg) {
        Notification.show(msg, Type.TRAY_NOTIFICATION);
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
    	try{
    		grid.getSelectionModel().reset();
    	}
    	catch(Exception e){
    		System.out.println("clearSelection: "+e.getMessage());
    	}
    }

    public void selectRow(JobScheduled row) {
        ((SelectionModel.Single) grid.getSelectionModel()).select(row);
    }

    public JobScheduled getSelectedRow() {
        return grid.getSelectedRow();
    }

    public void editRecord(JobScheduled rec) {
        if (rec != null) {
            form.addStyleName("visible");
            form.setVisible(true);
        } else {
        	form.removeStyleName("visible");
        	form.setVisible(false);
        }
        form.editRecord(rec);
    }

    public void showRecords(List<JobScheduled> records) {
        grid.setRecords(records);
    }

    public void refreshRecord(JobScheduled rec) {
        grid.refresh(rec);
        grid.scrollTo(rec);
    }

    public void removeRecord(JobScheduled rec) {
        grid.remove(rec);
    }
	
}
