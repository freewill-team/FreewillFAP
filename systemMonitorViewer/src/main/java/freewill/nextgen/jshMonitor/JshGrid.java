package freewill.nextgen.jshMonitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.StringToBooleanConverter;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.HtmlRenderer;

import freewill.nextgen.common.entities.JobScheduled;
import freewill.nextgen.hmi.utils.Messages;

/**
 * Grid of records, handling the visual presentation and filtering of a set of
 * items. This version uses an in-memory data source that is suitable for small
 * data sets.
 */
@SuppressWarnings("serial")
public class JshGrid extends Grid {
	
	private StringToBooleanConverter pointtypeConverter = new StringToBooleanConverter() {
        @Override
        public String convertToPresentation(Boolean pointstatus,
                java.lang.Class<? extends String> targetType, Locale locale)
                throws Converter.ConversionException {
            String text = super.convertToPresentation(pointstatus, targetType, locale);

            String color = "";
            if (pointstatus == true) {
                color = "#2dd085"; // green
            } else {
                color = "#f54993"; // red
            }

            String iconCode = "<span class=\"v-icon\" style=\"font-family: "
                    + FontAwesome.CIRCLE.getFontFamily() + ";color:" + color
                    + "\">&#x"
                    + Integer.toHexString(FontAwesome.CIRCLE.getCodepoint())
                    + ";</span>";

            return iconCode + " " + text;
        };
    };
    
    public JshGrid() {
        setSizeFull();
        setSelectionMode(SelectionMode.SINGLE);

        BeanItemContainer<JobScheduled> container = 
        	new BeanItemContainer<JobScheduled>(JobScheduled.class);
        setContainerDataSource(container);
        setColumns("label", "description", "state", "active", "cron", "lastExec", "command", "params");
        
        // Add column renderer
        getColumn("active").setConverter(pointtypeConverter).setRenderer(new HtmlRenderer());
        getColumn("description").setWidth(500);
        getColumn("lastExec").setWidth(200);

        for(Column col:this.getColumns()){
        	col.setHeaderCaption(Messages.get().getKey(col.getPropertyId().toString()));
        }
        
        // Align columns using a style generator and theme rule until #15438
        setCellStyleGenerator(new CellStyleGenerator() {

            @Override
            public String getStyle(CellReference cellReference) {
                if (cellReference.getPropertyId().equals("timestamp")) {
                    return "align-right";
                }
                return null;
            }
        });
        
    }

    @SuppressWarnings("unchecked")
	private BeanItemContainer<JobScheduled> getContainer() {
        return (BeanItemContainer<JobScheduled>) super.getContainerDataSource();
    }

    @Override
    public JobScheduled getSelectedRow() throws IllegalStateException {
        return (JobScheduled) super.getSelectedRow();
    }

    public void setRecords(List<JobScheduled> records) {
        if(getContainer()==null || getContainer().size()==0){
    		// Empty container
	        getContainer().removeAllItems();
	        getContainer().addAll(records);
	        return;
    	}
        // Not empty container
        List<JobScheduled> listfound = new ArrayList<>();
        List<JobScheduled> toremove = new ArrayList<>();
    	for(JobScheduled item : getContainer().getItemIds()){
    		boolean found = false;
    		for(JobScheduled rec : records){
        		if(rec.getId()==item.getId()){
        			found = true;
        			if(item.getLastExec().getTime()!=rec.getLastExec().getTime()
        				|| item.getActive()!=rec.getActive()
        				|| item.getState()!=rec.getState()){
        				item.setLastExec(rec.getLastExec());
        				item.setActive(rec.getActive());
        				item.setState(rec.getState());
        				refresh(item); // Updates existing records
        			}
        			listfound.add(rec); // add to list of found records
        		}
        	}
    		if(found==false){
    			toremove.add(item); // Add to list of records to be removed
    		}
    	}
    	// Removes no existing records
    	for(JobScheduled rec : toremove){
    		this.remove(rec);
    	}
    	// Adds new items to the container
    	for(JobScheduled rec : records){
    		if(!listfound.contains(rec))
    			getContainer().addBean(rec); // add only not found recs
    	}
    }

    @SuppressWarnings("rawtypes")
	public void refresh(JobScheduled rec) {
        // We avoid updating the whole table through the backend here so we can
        // get a partial update for the grid
        BeanItem<JobScheduled> item = getContainer().getItem(rec);
        if (item != null) {
            // Updated record
            MethodProperty p = (MethodProperty) item.getItemProperty("ID");
            if(p!=null)
            	p.fireValueChange();
        } else {
            // New record
            getContainer().addBean(rec);
        }
    }

    public void remove(JobScheduled rec) {
        getContainer().removeItem(rec);
    }
    
    public void removeFilters() {
        getContainer().removeAllContainerFilters();
	}
    
}
