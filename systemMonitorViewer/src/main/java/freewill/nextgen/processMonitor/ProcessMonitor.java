package freewill.nextgen.processMonitor;

import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.data.Item;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.common.entities.ProcessEntity;
import freewill.nextgen.common.entities.ServerEntity;
import freewill.nextgen.common.entities.ServiceEntity;
import freewill.nextgen.common.rtdbclient.RtdbDataService;
import freewill.nextgen.managementConsole.EntryPoint;
import freewill.nextgen.serverMonitor.ServerForm;
import freewill.nextgen.serviceMonitor.ServiceForm;

@SuppressWarnings("serial")
public class ProcessMonitor extends CssLayout implements View {
	
	private ProcessesGrid grid = null;
	private ProcessesTree tree = null;
	private Logger log = Logger.getLogger(ProcessMonitor.class);
	//private String selectedServer = "";
	//private String selectedService = "";
	private Object selectedNode = null;
	private ProcessForm processForm = null;
	private ServerForm serverForm = null;
	private ServiceForm serviceForm = null;

	public ProcessMonitor(){
		setSizeFull();
		addStyleName("crud-view");
		
		processForm = new ProcessForm();
		serverForm = new ServerForm();
		serviceForm = new ServiceForm();
		
        tree = new ProcessesTree();
        tree.setStyleName(ValoTheme.TREETABLE_SMALL);
        
        grid = new ProcessesGrid();
        grid.setStyleName(ValoTheme.TABLE_SMALL);
        grid.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
        
        HorizontalSplitPanel data = new HorizontalSplitPanel();
        data.setSizeFull();
        data.setStyleName("main-screen");
		data.setWidth("100%");
		data.setFirstComponent(tree);
        data.setSecondComponent(grid);
        data.setMaxSplitPosition(25, Unit.PERCENTAGE);
        data.setMinSplitPosition(10, Unit.PERCENTAGE);
        data.setSplitPosition(18, Unit.PERCENTAGE);
        data.setStyleName("crud-view");
       
        VerticalLayout barAndGridLayout = new VerticalLayout();
        //barAndGridLayout.addComponent(topLayout);
        barAndGridLayout.addComponent(data);
        barAndGridLayout.setMargin(false);
        barAndGridLayout.setSpacing(true);
        barAndGridLayout.setSizeFull();
        barAndGridLayout.setExpandRatio(data, 1);
        barAndGridLayout.setStyleName("crud-main-layout");
        addComponent(barAndGridLayout);
        addComponent(processForm);
        addComponent(serverForm);
        addComponent(serviceForm);
        
        grid.addSelectionListener(new SelectionListener() {
            @Override
            public void select(SelectionEvent event) {
            	showProcess(grid.getSelectedRow());
            }
        });
        
        tree.addItemClickListener(new ItemClickEvent.ItemClickListener() {
        	@Override
			public void itemClick(ItemClickEvent event) {
				Item obj = event.getItem();
        		if(obj!=null){
        			selectedNode = obj.getItemProperty("node").getValue();
        			if(selectedNode instanceof ServerEntity){
        				ServerEntity rec = (ServerEntity) selectedNode;
        				rec = (ServerEntity) RtdbDataService.get().getEntityById(rec.getID(), ServerEntity.class); 
        				showServer(rec);
        			}
        			else if(selectedNode instanceof ServiceEntity){
        				ServiceEntity rec = (ServiceEntity) selectedNode;
        				rec = (ServiceEntity) RtdbDataService.get().getEntityById(rec.getID(), ServiceEntity.class); 
        				showService(rec);
        			}
        			refreshProcesses();
        			
        			/*String itemId = (String) obj.getItemProperty("caption").getValue();
        			Object parentId = tree.getParent(event.getItemId());
        			if(parentId!=null){
        				String parent = (String) tree.getItem(parentId).getItemProperty("caption").getValue();
        				if(parent.equals("Root")){
        					selectedServer = itemId;
        					selectedService = "";
        					ServerEntity rec = (ServerEntity) RtdbDataService.get().getEntityById(
        			    		Utils.NOSYSTEM+":"+itemId, ServerEntity.class); 
        					showServer(rec);
        				}
        				else{
        					selectedServer = parent;
        					selectedService = itemId;
        					ServiceEntity rec = (ServiceEntity) RtdbDataService.get().getEntityById(
        			    		Utils.NOSYSTEM+":"+parent+":"+itemId, ServiceEntity.class); 
        					showService(rec);
        				}
        			}
        			else{
        				selectedServer = "";
    					selectedService = "";
        			}
        			Refresh();*/
        		}
			}
		});
        
    }

	public void refreshProcesses() {
		try{
			List<ProcessEntity> procs = null;
			if(selectedNode instanceof ServerEntity){
				ServerEntity rec = (ServerEntity) selectedNode;
				procs = RtdbDataService.get().getProcessesByServer(rec);
			}
			else if(selectedNode instanceof ServiceEntity){
				ServiceEntity rec = (ServiceEntity) selectedNode;
				procs = RtdbDataService.get().getProcessesByService(rec);
			}
			else{
				procs = RtdbDataService.get().getProcesses();
			}
			List<ServerEntity> servers = RtdbDataService.get().getEntities(ServerEntity.class);
			List<ServiceEntity> services = RtdbDataService.get().getEntities(ServiceEntity.class);
	    	grid.setRecords(procs);
	    	tree.setRecords(servers, services);
		}
		catch(Exception e){
			log.error(e.getMessage());
		}
    }
	
	/*private void Refresh() {
		try{
			List<ProcessEntity> procs = null;
			if(selectedServer!="" && selectedService!="")
				procs = RtdbDataService.get().getProcessesByService(selectedServer, selectedService);
			else if(selectedServer!="" && selectedService=="")
				procs = RtdbDataService.get().getProcessesByServer(selectedServer);
			else
				procs = RtdbDataService.get().getProcesses();
			List<ServerEntity> servers = RtdbDataService.get().getEntities(ServerEntity.class);
			List<ServiceEntity> services = RtdbDataService.get().getEntities(ServiceEntity.class);
	    	grid.setRecords(procs);
	    	tree.setRecords(servers, services);
		}
		catch(Exception e){
			log.error(e.getMessage());
		}
    }*/
	
	@Override
	public void enter(ViewChangeEvent event) {
		// Activates refresh
		EntryPoint.get().addPollListener(event2 ->refreshProcesses());
		EntryPoint.get().setPollInterval(5000);
		//Refresh();
		refreshProcesses();
	}
	
	public void showProcess(ProcessEntity rec) {
		serverForm.removeStyleName("visible");
        serverForm.setVisible(false);
        serviceForm.removeStyleName("visible");
        serviceForm.setVisible(false);
        if (rec != null) {
            processForm.addStyleName("visible");
            processForm.setVisible(true);
        } else {
        	processForm.removeStyleName("visible");
        	processForm.setVisible(false);
        }
        processForm.editRecord(rec);
    }
	
	public void showServer(ServerEntity rec) {
		processForm.removeStyleName("visible");
    	processForm.setVisible(false);
    	serviceForm.removeStyleName("visible");
        serviceForm.setVisible(false);
	    if (rec != null) {
	        serverForm.addStyleName("visible");
	        serverForm.setVisible(true);
	    } else {
	        serverForm.removeStyleName("visible");
	        serverForm.setVisible(false);
	    }
	    serverForm.editRecord(rec);
    }
	
	public void showService(ServiceEntity rec) {
		processForm.removeStyleName("visible");
    	processForm.setVisible(false);
    	serverForm.removeStyleName("visible");
        serverForm.setVisible(false);
	    if (rec != null) {
	        serviceForm.addStyleName("visible");
	        serviceForm.setVisible(true);
	    } else {
	        serviceForm.removeStyleName("visible");
	        serviceForm.setVisible(false);
	    }
	    serviceForm.editRecord(rec);
    }
	
}
