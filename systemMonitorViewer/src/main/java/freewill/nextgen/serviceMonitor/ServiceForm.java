package freewill.nextgen.serviceMonitor;

import org.apache.log4j.Logger;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Field;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.common.entities.ServiceEntity;
import freewill.nextgen.common.rtdbclient.RtdbDataService;
import freewill.nextgen.hmi.common.ConfirmDialog;
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
public class ServiceForm extends ServiceFormDesign {
	
	private Logger log = Logger.getLogger(ServiceForm.class);
	FontAwesome ICON_GOOD = FontAwesome.CHECK_CIRCLE;
	FontAwesome ICON_FAIL = FontAwesome.EXCLAMATION_TRIANGLE;
	FontAwesome ICON_STOP = FontAwesome.STOP_CIRCLE;
	FontAwesome ICON_STARTING = FontAwesome.HOURGLASS_START;
    private BeanFieldGroup<ServiceEntity> fieldGroup;

    @SuppressWarnings("rawtypes")
    public ServiceForm() {
        super();
        addStyleName("product-form");
        
        fieldGroup = new BeanFieldGroup<ServiceEntity>(ServiceEntity.class);
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
        		ServiceEntity rec = fieldGroup.getItemDataSource().getBean();
    			if(rec==null) return;
        		log.error("Entrando en Stop Service "+rec.getID());
        		ConfirmDialog cd = new ConfirmDialog(Messages.get().getKey("reallystopallprocesses"));
            	cd.setOKAction(new ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
            			cd.close();
            			// Issue command for stopping this service
            			RtdbDataService.get().stopService(rec.getID(),
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
        		ServiceEntity rec = fieldGroup.getItemDataSource().getBean();
    			if(rec==null) return;
    			log.error("Entrando en Start Service "+rec.getID());
	            // Issue command for starting this service
	        	RtdbDataService.get().startService(rec.getID(),
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
    }

	public void editRecord(ServiceEntity rec) {
    	System.out.println("Enterig editRecord with "+rec);
        if (rec == null) return;
                
        fieldGroup.setItemDataSource(new BeanItem<ServiceEntity>(rec));

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
        //status.setValue(rec.getStatus());
        formHasChanged();
    }

	private void formHasChanged() {
        // show validation errors after the user has changed something
    	name.setValidationVisible(true);

        BeanItem<ServiceEntity> item = fieldGroup.getItemDataSource();
        if (item != null) {
        	ServiceEntity rec = item.getBean();
        	if(rec!=null){
        		// 
        	}
        }
    }
	
}
