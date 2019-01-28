package freewill.nextgen.serverMonitor;

import org.apache.log4j.Logger;

import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.common.Utils;
import freewill.nextgen.common.entities.ServerPerformanceEntity;
import freewill.nextgen.common.rtdbclient.RtdbDataService;
import freewill.nextgen.hmi.common.SparklineChart;
import freewill.nextgen.hmi.utils.Messages;

// Deprecated
@SuppressWarnings("serial")
public class ServerMonitor extends Window {
	
	private String server = "";
	private Logger log = Logger.getLogger(ServerMonitor.class);

	public ServerMonitor( String server ){
		this.server = server;
		setCaption(Messages.get().getKey("monitoringserver") +": " + server);
        setModal(true);
        setClosable(true);
        setResizable(false);
        addStyleName(ValoTheme.WINDOW_TOP_TOOLBAR);
        addStyleName("edit-dashboard");
        setContent(buildContent());
    }

    private Component buildContent() {
    	try{
	    	ServerPerformanceEntity svrPM = (ServerPerformanceEntity) 
	    			RtdbDataService.get().getEntityById(
	    			Utils.NOSYSTEM+":"+server, ServerPerformanceEntity.class);
	    	
	        int numobjs = svrPM.getPartitionSize() + 3;
	        GridLayout result = new GridLayout(numobjs/2+1, 2);
	        result.setMargin(true);
	        result.setSpacing(true);
	        if(svrPM!=null){
	        	Component ram = new SparklineChart(
	        			"RAM Usage", "Used/Total(Gb)",
	        			svrPM.getTotalRAMSpaceSize()/1024-svrPM.getFreeRAMSpaceSize()/1024, 
	        			svrPM.getTotalRAMSpaceSize()/1024,
	        			true);
	        	result.addComponent(ram);
	        	
	        	Component cpu = new SparklineChart(
	        			"CPU Usage", "% Used",
	        			(long)svrPM.getSystemCpuLoad(), -1L,
	        			true);
	        	result.addComponent(cpu);
	        	
	        	Component net = new SparklineChart(
	        			"NET Usage", "Used/Total(Mbps)",
	        			(long)(svrPM.getNetworkUsage()*svrPM.getNetworkBandwith()/100),
	        			svrPM.getNetworkBandwith(),
	        			true);
	        	result.addComponent(net);
	        	
	        	for(int i=0; i<svrPM.getPartitionSize(); i++){
	        		Component hdd = new SparklineChart(
		        			svrPM.getPartitionName(i) +" Usage", "Used/Total(Gb)",
		        			svrPM.getTotalPartitionSize(i)-svrPM.getUsablePartitionSpace(i), 
		        			svrPM.getTotalPartitionSize(i),
		        			true);
		        	result.addComponent(hdd);
	        	}
	        	
	        }
	        return result;
    	}
		catch(Exception e){
			log.error(e.getMessage());
		}
    	return null;
    }
    
}
