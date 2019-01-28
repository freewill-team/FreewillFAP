package freewill.nextgen.alarmMonitor;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.addon.contextmenu.ContextMenu;
import com.vaadin.addon.contextmenu.MenuItem;
import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Audio;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.common.Utils.CategoryEnum;
import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.common.entities.AlarmEntity;
import freewill.nextgen.common.rtdbclient.RtdbDataService;
import freewill.nextgen.hmi.common.ConfirmDialog;
import freewill.nextgen.hmi.utils.Export2Xls;
import freewill.nextgen.hmi.utils.Messages;
import freewill.nextgen.managementConsole.EntryPoint;

@SuppressWarnings("serial")
public class AlarmMonitor extends CssLayout implements View {
	
	private AlarmsGrid grid = null;
	private Label systemAlms = null;
	private Label operAlms = null;
	private Label totalAlms = null;
	private Logger log = Logger.getLogger(AlarmMonitor.class);
	private boolean freezeRefresh = false;
	private Audio audio = null;
	private boolean activeAudio = false;
	private Button audiobtn = null;

	public AlarmMonitor(){
		setSizeFull();
	    addStyleName("crud-view");
	    HorizontalLayout topLayout = createTopBar();
        
        grid = new AlarmsGrid(this);
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
        
        ContextMenu menu = new ContextMenu(grid, true);
        final MenuItem basic = menu.addItem(Messages.get().getKey("ackalarm"), evt -> {
            // Alarm acknowledged
        	try {
        		if(grid.getSelectedRow()==null) return;
	        	AlarmEntity rec = 
	        		RtdbDataService.get().getAlarmById(grid.getSelectedRow().getId());
	        	if(rec!=null){
	        		ackAlarm(rec);
	        	}
        	}
    		catch(Exception e){
    			e.printStackTrace();
    			log.error(e.getMessage());
    		}
        });
        basic.setIcon(FontAwesome.HAND_O_UP);
        
    }
	
	public HorizontalLayout createTopBar() {
		
		systemAlms = new Label(Messages.get().getKey("systemalarms"));
		systemAlms.setStyleName(ValoTheme.LABEL_FAILURE);
		
		operAlms = new Label(Messages.get().getKey("operalarms"));
		operAlms.setStyleName(ValoTheme.LABEL_FAILURE);
		
		totalAlms =  new Label(Messages.get().getKey("totalalarms"));
		totalAlms.setStyleName(ValoTheme.LABEL_FAILURE);
		
        Button ackbtn = new Button(Messages.get().getKey("acknowledgeall"));
        ackbtn.addStyleName(ValoTheme.BUTTON_DANGER);
        ackbtn.setIcon(FontAwesome.WARNING);
        ackbtn.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	ConfirmDialog cd = new ConfirmDialog(Messages.get().getKey("confirmack"));
            	cd.setOKAction(new ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
            			cd.close();
            			ackAllAlarms();
                    }
                });
            	getUI().addWindow(cd);
            }
        });
        
        Button freezebtn = new Button(); //Messages.get().getKey("freezerefresh"));
        freezebtn.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        freezebtn.setIcon(FontAwesome.PLAY);
        freezebtn.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	// Freeze Alarm Refresh
            	freezeRefresh = !freezeRefresh;
            	if(freezeRefresh)
            		freezebtn.setIcon(FontAwesome.STOP);
            	else
            		freezebtn.setIcon(FontAwesome.PLAY);
            }
        });
        
        Button exportbtn = new Button(); //Messages.get().getKey("export2excel"));
		exportbtn.setIcon(FontAwesome.FILE_EXCEL_O);
		exportbtn.addClickListener(new ClickListener() {
			@SuppressWarnings({ "unchecked", "deprecation" })
			@Override
            public void buttonClick(ClickEvent event) {
				File file = Export2Xls.get().createXLS((List<AlarmEntity>)grid.getContainerDataSource().getItemIds(),
						AlarmEntity.class,  "timestamp", "flashing", "severity", "point", "pointType", "message", 
						"parentPoint", "category");
				if(file!=null){
					FileResource resource = new FileResource(file);
					Page.getCurrent().open(resource, "Export File", false);
		    		// Finally, removes the temporal file
		    		// file.delete();
				}
            }
        });
		
		audiobtn = new Button();
		freezebtn.addStyleName(ValoTheme.BUTTON_DANGER);
		audiobtn.setIcon(FontAwesome.BELL_SLASH);
		audiobtn.setEnabled(false);
		audiobtn.addClickListener(new ClickListener() {
			@Override
            public void buttonClick(ClickEvent event) {
				activeAudio = false;
				audiobtn.setEnabled(false);
            }
        });
		
		audio = new Audio();
		audio.setSource(new ThemeResource("alarm01.wav"));
		// All Audio objects are disable via CSS theme
        
        HorizontalLayout kpis = new HorizontalLayout();
        kpis.setMargin(false);
        kpis.setSpacing(true);
        kpis.addComponent(systemAlms);
        kpis.addComponent(operAlms);
        kpis.addComponent(totalAlms);
                
        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setMargin(true);
        topLayout.setSpacing(true);
        topLayout.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        topLayout.addComponent(kpis);
        topLayout.setWidth("100%");
        topLayout.addComponent(freezebtn);
        topLayout.addComponent(exportbtn);
        topLayout.addComponent(audiobtn);
        topLayout.addComponent(audio);
        topLayout.addComponent(ackbtn);
        topLayout.setComponentAlignment(ackbtn, Alignment.MIDDLE_RIGHT);
        topLayout.setExpandRatio(ackbtn, 1);
        topLayout.setStyleName("top-bar");
        return topLayout;
    }

	public void Refresh() {
		if(activeAudio)
			audio.play();
		if(freezeRefresh)
			return;
		try{
			List<AlarmEntity> recs = RtdbDataService.get().getAlarms();
			setOperAlarms(recs);
	    	setSystemAlarms(recs);
	    	setTotalAlarms(recs);
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
		EntryPoint.get().setPollInterval(5000);
	}
	
	public void ackAlarm(AlarmEntity rec) {
		try{
			// Acknowledges the selected alarm
			boolean result = (boolean) BltClient.get().executeCommand(
					"ack/"+EntryPoint.get().getAccessControl().getUserLogin()
					+"/"+VaadinService.getCurrentRequest().getRemoteHost(),
					rec, AlarmEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
			if(result==false)
				showNotification(Messages.get().getKey("ackfailed"));
			// And forces grid update
			this.Refresh();
		}
		catch(Exception e){
			log.error(e.getMessage());
		}
	}
	
	private void ackAllAlarms() {
		try{
			for(Object item : grid.getContainerDataSource().getItemIds()){
			    // Now get the actual item from the table.
				AlarmEntity rec = (AlarmEntity)item;
			    if(rec!=null && rec.getFlashing()==true){
			    	// Acknowledges the selected alarm
					BltClient.get().executeCommand(
							"ack/"+EntryPoint.get().getAccessControl().getUserLogin()
							+"/"+VaadinService.getCurrentRequest().getRemoteHost(),
							rec, AlarmEntity.class, 
							EntryPoint.get().getAccessControl().getTokenKey());
			    }
			}
			// And forces grid update
			this.Refresh();
		}
		catch(Exception e){
			log.error(e.getMessage());
		}
	}
	
	private void setOperAlarms(List<AlarmEntity> recs){
		int num = 0;
		for(AlarmEntity rec:recs){
			if(rec.getCategory()==CategoryEnum.COMMS
				|| rec.getCategory()==CategoryEnum.TELEMETRY
				|| rec.getCategory()==CategoryEnum.USER)
					num++;
		}
		operAlms.setValue(num+" "+Messages.get().getKey("operalarms"));
		if(num>0)
			operAlms.setStyleName(ValoTheme.LABEL_FAILURE);
		else
			operAlms.setStyleName(ValoTheme.LABEL_SUCCESS);
	}
	
	private void setSystemAlarms(List<AlarmEntity> recs){
		int num = 0;
		for(AlarmEntity rec:recs){
			if(rec.getCategory()==CategoryEnum.SYSTEM
				|| rec.getCategory()==CategoryEnum.APPLICATION)
					num++;
		}
		systemAlms.setValue(num+" "+Messages.get().getKey("systemalarms"));
		if(num>0)
    		systemAlms.setStyleName(ValoTheme.LABEL_FAILURE);
    	else
    		systemAlms.setStyleName(ValoTheme.LABEL_SUCCESS);
	}
	
	private void setTotalAlarms(List<AlarmEntity> recs){
		int num = 0;
		if(recs!=null)
			num = recs.size();
    	totalAlms.setValue(num+" "+Messages.get().getKey("totalalarms"));
    	if(num>0)
			totalAlms.setStyleName(ValoTheme.LABEL_FAILURE);
		else
			totalAlms.setStyleName(ValoTheme.LABEL_SUCCESS);
	}
	
	public void showNotification(String msg) {	
	    audio.play();
		Notification.show(msg, Type.TRAY_NOTIFICATION);
		activeAudio = true;
		audiobtn.setEnabled(true);
    }
	
}
