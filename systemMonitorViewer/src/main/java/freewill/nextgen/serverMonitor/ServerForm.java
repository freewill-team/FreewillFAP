package freewill.nextgen.serverMonitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.common.entities.KpiValue;
import freewill.nextgen.common.entities.ServerEntity;
import freewill.nextgen.common.entities.ServerPerformanceEntity;
import freewill.nextgen.common.rtdbclient.RtdbDataService;
import freewill.nextgen.hmi.common.BarChartJs;
import freewill.nextgen.hmi.common.ConfirmDialog;
import freewill.nextgen.hmi.common.PieChartJs;
import freewill.nextgen.hmi.utils.Messages;
import freewill.nextgen.managementConsole.EntryPoint;

/**
 * A form for editing a single record.
 *
 * Using responsive layouts, the form can be displayed either sliding out on the
 * side of the view or filling the whole screen - see the theme for the related
 * CSS rules.
 */
@SuppressWarnings("serial")
public class ServerForm extends ServerFormDesign {
	
	private Logger log = Logger.getLogger(ServerForm.class);
	FontAwesome ICON_GOOD = FontAwesome.CHECK_CIRCLE;
	FontAwesome ICON_FAIL = FontAwesome.EXCLAMATION_TRIANGLE;
	FontAwesome ICON_STOP = FontAwesome.STOP_CIRCLE;
	FontAwesome ICON_STARTING = FontAwesome.HOURGLASS_START;
    private BeanFieldGroup<ServerEntity> fieldGroup;

    @SuppressWarnings("rawtypes")
    public ServerForm() {
        super();
        addStyleName("product-form");
        
        fieldGroup = new BeanFieldGroup<ServerEntity>(ServerEntity.class);
        fieldGroup.bindMemberFields(this);

        // perform validation and enable/disable buttons while editing
        ValueChangeListener valueListener = new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                formHasChanged();
            }
        };
        for (Field f : fieldGroup.getFields()) {
            f.addValueChangeListener(valueListener);
            f.setCaption(Messages.get().getKey(f.getCaption())); // Translations
        }

        close.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	removeStyleName("visible");
            }
        });
        
        stop.addClickListener(event -> {
        	// Stop process
        	try{
        		ServerEntity rec = fieldGroup.getItemDataSource().getBean();
    			if(rec==null) return;
        		log.error("Entrando en Stop Server "+rec.getID());
        		ConfirmDialog cd = new ConfirmDialog(Messages.get().getKey("reallystopallprocesses"));
            	cd.setOKAction(new ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
            			cd.close();
            			// Issue command for stopping this server
            			RtdbDataService.get().stopServer(rec.getID(),
            					EntryPoint.get().getAccessControl().getUserLogin(),
            					EntryPoint.get().getAccessControl().getConsole());
        	        	stop.setEnabled(false);
                    }
                });
            	getUI().addWindow(cd);
        	}
    		catch(Exception e){
    			log.error(e.getMessage());
    		}
        });
        
        start.addClickListener(event -> {
        	// Start Process 
        	try{
        		ServerEntity rec = fieldGroup.getItemDataSource().getBean();
    			if(rec==null) return;
    			log.error("Entrando en Start Server "+rec.getID());
	            // Issue command for starting this server
	        	RtdbDataService.get().startServer(rec.getID(),
	        			EntryPoint.get().getAccessControl().getUserLogin(),
	        			EntryPoint.get().getAccessControl().getConsole());
	        	start.setEnabled(false);
        	}
    		catch(Exception e){
    			log.error(e.getMessage());
    		}
        });
     
        statebox.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        statebox.setMargin(true);
        statebox.setSizeFull();
        state.addStyleName(ValoTheme.LABEL_HUGE);
        
        ramcpu.setHeight("200px");
        ramcpu.setMargin(true);
        disk.setHeight("200px");
        disk.setMargin(true);
    }

	public void editRecord(ServerEntity rec) {
    	System.out.println("Enterig editRecord with "+rec);
        if (rec == null) return;
                
        fieldGroup.setItemDataSource(new BeanItem<ServerEntity>(rec));

        // before the user makes any changes, disable validation error indicator
        // of the product name field (which may be empty)
        name.setValidationVisible(false);

        // Scroll to the top
        // As this is not a Panel, using JavaScript
        String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
        Page.getCurrent().getJavaScript().execute(scrollScript);
        
        String color = "";
        FontAwesome icono = null;
        switch(rec.getStatus()){
        	case GOOD:
	            color = "#2dd085"; // green
	            icono = ICON_GOOD;
	            stop.setEnabled(true);
	            start.setEnabled(false);
	            break;
	        case STOP:
	            color = "#ffc66e"; // yellow
	            icono = ICON_STOP;
	            stop.setEnabled(false);
	            start.setEnabled(true);
	            break;
        	case STARTING:
        		color = "#00b0ca"; // blue
        		icono = ICON_STARTING;
        		stop.setEnabled(false);
	            start.setEnabled(false);
        		break;
            default:
            	color = "#f54993"; // red
            	icono = ICON_FAIL;
            	stop.setEnabled(false);
	            start.setEnabled(true);
            	break;
        }
        state.setCaptionAsHtml(true);
        state.setValue("<span style=\'align: center; color: " + color + " !important;\'> " 
            + icono.getHtml()  + "</span>  <b>" + rec.getStatus().toString()+"</b>");
        
        ramcpu.removeAllComponents();
        disk.removeAllComponents();
        try{
        	ServerPerformanceEntity recpm = (ServerPerformanceEntity) 
     	    	RtdbDataService.get().getEntityById(rec.getID(), ServerPerformanceEntity.class);
        	ramcpu.addComponents(drawRamPie(recpm), drawCpuPie(recpm));
        	disk.addComponent(drawDiskBars(recpm));
		}
		catch(Exception e){
			log.error(e.getMessage());
			ramcpu.addComponents(new Label("Monitored Process KPIS missing"));
			disk.addComponent(new Label("Monitored Process KPIS missing"));
		}
        
        formHasChanged();
    }

	private void formHasChanged() {
        // show validation errors after the user has changed something
    	name.setValidationVisible(true);

        BeanItem<ServerEntity> item = fieldGroup.getItemDataSource();
        if (item != null) {
        	ServerEntity rec = item.getBean();
        	if(rec!=null){
        		// 
        	}
        }
    }
    
	private Component drawDiskBars(ServerPerformanceEntity svrPM) {
		List<KpiValue> free = new ArrayList<KpiValue>();
		List<KpiValue> used = new ArrayList<KpiValue>();
		for(int i=0; i<svrPM.getPartitionSize(); i++){
			KpiValue pnt1 = new KpiValue(svrPM.getPartitionName(i)+" ("+svrPM.getTotalPartitionSize(i)+"Gb)");
			pnt1.setValue(svrPM.getUsablePartitionSpace(i)*100.0/svrPM.getTotalPartitionSize(i));
			free.add(pnt1);
			KpiValue pnt2 = new KpiValue(svrPM.getPartitionName(i)+" ("+svrPM.getTotalPartitionSize(i)+"Gb)");
			pnt2.setValue(100.0-pnt1.getValue());
    		used.add(pnt2);
    	}
		BarChartJs chart = new BarChartJs("% Used Disk");
		chart.addSerie("Used", used);
		chart.addSerie("Free", free);
    	return chart;
	}
	
	private Component drawRamPie(ServerPerformanceEntity svrPM) {
		HashMap<String, Double> data = new HashMap<String, Double>();
		data.put("Used", svrPM.getTotalRAMSpaceSize()/1024.0-svrPM.getFreeRAMSpaceSize()/1024.0);
		data.put("Free", svrPM.getFreeRAMSpaceSize()/1024.0);
		PieChartJs chart = new PieChartJs(svrPM.getTotalRAMSpaceSize()/1024+"Gb RAM", data);
    	return chart;
	}
	
	private Component drawCpuPie(ServerPerformanceEntity svrPM) {
		HashMap<String, Double> data = new HashMap<String, Double>();
		data.put("Used", svrPM.getSystemCpuLoad());
		data.put("Free", 100 - svrPM.getSystemCpuLoad());
		PieChartJs chart = new PieChartJs("% CPU", data);
    	return chart;
	}
	
}
