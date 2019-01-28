package freewill.nextgen.processMonitor;

import java.util.Date;

import org.apache.log4j.Logger;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Field;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.common.entities.KpiValue;
import freewill.nextgen.common.entities.ProcessEntity;
import freewill.nextgen.common.rtdbclient.RtdbDataService;
import freewill.nextgen.hmi.common.ConfirmDialog;
import freewill.nextgen.hmi.common.GenericGrid;
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
public class ProcessForm extends ProcessFormDesign {
	
	private Logger log = Logger.getLogger(ProcessForm.class);
	FontAwesome ICON_GOOD = FontAwesome.CHECK_CIRCLE;
	FontAwesome ICON_FAIL = FontAwesome.EXCLAMATION_TRIANGLE;
	FontAwesome ICON_STOP = FontAwesome.STOP_CIRCLE;
	FontAwesome ICON_STARTING = FontAwesome.HOURGLASS_START;
    private BeanFieldGroup<ProcessEntity> fieldGroup;
    GenericGrid<KpiValue> kpiGrid = null;

    @SuppressWarnings("rawtypes")
    public ProcessForm() {
        super();
        addStyleName("product-form");
        
        fieldGroup = new BeanFieldGroup<ProcessEntity>(ProcessEntity.class);
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
        
        delete.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	ConfirmDialog cd = new ConfirmDialog(Messages.get().getKey("reallyremovequestion"));
            	cd.setOKAction(new ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
            			cd.close();
            			ProcessEntity rec = fieldGroup.getItemDataSource().getBean();
            			if(rec!=null){
            				 RtdbDataService.get().deleteProcess(rec.getID(),
            						 EntryPoint.get().getAccessControl().getPrincipalName(),
            						 EntryPoint.get().getAccessControl().getConsole());
            				 removeStyleName("visible");
            			}
                    }
                });
            	getUI().addWindow(cd);
            }
        });
        
        stop.addClickListener(event -> {
        	// Stop process
        	try{
        		ProcessEntity rec = fieldGroup.getItemDataSource().getBean();
    			if(rec==null) return;
        		log.error("Entrando en Stop Process "+rec.getID());
        		ConfirmDialog cd = new ConfirmDialog(Messages.get().getKey("reallystopprocess"));
            	cd.setOKAction(new ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
            			cd.close();
            			// Issue command for stopping this process
            			RtdbDataService.get().stopProcess(rec.getID(),
            					EntryPoint.get().getAccessControl().getUserLogin(),
            					VaadinService.getCurrentRequest().getRemoteHost());
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
        		ProcessEntity rec = fieldGroup.getItemDataSource().getBean();
    			if(rec==null) return;
    			log.error("Entrando en Start Process "+rec.getID());
	            // Issue command for starting this process
	        	RtdbDataService.get().startProcess(rec.getID(),
	        			EntryPoint.get().getAccessControl().getUserLogin(),
    					VaadinService.getCurrentRequest().getRemoteHost());
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
        
        kpiGrid = new GenericGrid<KpiValue>(KpiValue.class, "name", "value");
        kpiGrid.getColumn("name").setWidth(280);
        kpiGrid.setStyleName(ValoTheme.TABLE_SMALL);
        kpis.addComponent(kpiGrid);
        kpis.setHeight("150px");
        details.setHeight("150px");
        config.setHeight("150px");
    }

    @SuppressWarnings("deprecation")
	public void editRecord(ProcessEntity rec) {
    	System.out.println("Enterig editRecord with "+rec);
        if (rec == null) return;
                
        fieldGroup.setItemDataSource(new BeanItem<ProcessEntity>(rec));

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
        state.setValue("   <span style=\'color: " + color + " !important;\'> " 
            + icono.getHtml()  + "</span>  <b>" + rec.getStatus().toString()+"</b>");
        
        Date date = new Date(rec.getTimestamp());
        timestamp2.setValue(date.toLocaleString());
        
        kpiGrid.setRecords(rec.getKpiList());
        
        formHasChanged();
    }

    private void formHasChanged() {
        // show validation errors after the user has changed something
    	name.setValidationVisible(true);

        BeanItem<ProcessEntity> item = fieldGroup.getItemDataSource();
        if (item != null) {
        	ProcessEntity rec = item.getBean();
        	if(rec!=null){
        		// 
        	}
        }
    }
    
}
