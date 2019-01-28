package freewill.nextgen.userMonitor;

import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.common.entities.LoginEntity;
import freewill.nextgen.common.rtdbclient.RtdbDataService;
import freewill.nextgen.managementConsole.EntryPoint;


@SuppressWarnings("serial")
public class UserMonitor extends CssLayout implements View {
	
	private UserGrid grid = null;
	private Logger log = Logger.getLogger(UserMonitor.class);

	public UserMonitor(){
		setSizeFull();
	    addStyleName("crud-view");
	    HorizontalLayout topLayout = createTopBar();
        
        grid = new UserGrid();
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
            	// viewLogic.rowSelected(grid.getSelectedRow());
            }
        });
        
        /*ContextMenu menu = new ContextMenu(grid, true);
        final MenuItem basic = menu.addItem(Messages.get().getKey("toggleactive"), evt -> {
            // Toggle Active
        	try {
        		if(grid.getSelectedRow()==null) return;
	        	LoginEntity rec = (LoginEntity)
	        		BltClient.get().getEntityById(""+grid.getSelectedRow().getId(), LoginEntity.class,
	        				EntryPoint.get().getAccessControl().getTokenKey());
	        	if(rec!=null){
	        		toggleJob(rec);
	        	}
        	}
    		catch(Exception e){
    			log.error(e.getMessage());
    		}
        });
        basic.setIcon(FontAwesome.HAND_O_UP);*/
        
    }
	
	public HorizontalLayout createTopBar() {
		
        /*Button ackbtn = new Button(Messages.get().getKey("disableall"));
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
        });*/
                
        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setMargin(true);
        topLayout.setSpacing(true);
        topLayout.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        topLayout.setWidth("100%");
        //topLayout.addComponent(ackbtn);
        //topLayout.setComponentAlignment(ackbtn, Alignment.MIDDLE_RIGHT);
        //topLayout.setExpandRatio(ackbtn, 1);
        topLayout.setStyleName("top-bar");
        return topLayout;
    }

	public void Refresh() {
		try{
			// We read the newest info directly from the Rtdb
			List<LoginEntity> recs = RtdbDataService.get().getEntities(LoginEntity.class);
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
	
	/*private void toggleJob(LoginEntity rec) {
		try{
			boolean result = (boolean)BltClient.get().executeCommand(
					"toggle/"+EntryPoint.get().getAccessControl().getUserLogin()
					+"/"+VaadinService.getCurrentRequest().getRemoteHost(),
					rec, LoginEntity.class,
					EntryPoint.get().getAccessControl().getTokenKey());
			if(result==false)
				showNotification(Messages.get().getKey("togglefailed"));
		}
		catch(Exception e){
			log.error(e.getMessage());
		}
	}*/
	
	/*private void disableAllJobs() {
		try{
			for(Object item : grid.getContainerDataSource().getItemIds()){
			    // Now get the actual item from the table.
			    LoginEntity rec = (LoginEntity)
		        		BltClient.get().getEntityById(""+((LoginEntity)item).getId(), LoginEntity.class,
		        				EntryPoint.get().getAccessControl().getTokenKey());
		        	if(rec!=null){
		        		if(rec.getActive())
		        			BltClient.get().executeCommand(
		        				"toggle/"+EntryPoint.get().getAccessControl().getUserLogin()
		        				+"/"+VaadinService.getCurrentRequest().getRemoteHost(),
		        				rec, LoginEntity.class, 
		        				EntryPoint.get().getAccessControl().getTokenKey());
		        	}
			}
		}
		catch(Exception e){
			log.error(e.getMessage());
		}
	}*/

	public void showNotification(String msg) {
        Notification.show(msg, Type.TRAY_NOTIFICATION);
    }
	
}
