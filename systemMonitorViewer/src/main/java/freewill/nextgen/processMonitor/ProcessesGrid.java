package freewill.nextgen.processMonitor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.StringToEnumConverter;
import com.vaadin.data.util.converter.StringToLongConverter;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.HtmlRenderer;

import freewill.nextgen.common.Utils.ServiceStatusEnum;
import freewill.nextgen.common.entities.ProcessEntity;
import freewill.nextgen.hmi.utils.Messages;

/**
 * Grid of records, handling the visual presentation and filtering of a set of
 * items. This version uses an in-memory data source that is suitable for small
 * data sets.
 */
@SuppressWarnings("serial")
public class ProcessesGrid extends Grid {
	
	FontAwesome ICON_GOOD = FontAwesome.CHECK_CIRCLE;
	FontAwesome ICON_FAIL = FontAwesome.EXCLAMATION_TRIANGLE;
	FontAwesome ICON_STOP = FontAwesome.STOP_CIRCLE;
	FontAwesome ICON_STARTING = FontAwesome.HOURGLASS_START;

    /*private StringToIntegerConverter statusConverter = new StringToIntegerConverter() {
    	@Override
        public String convertToPresentation(Integer point,
                java.lang.Class<? extends String> targetType, Locale locale)
                throws Converter.ConversionException {
            String text = super.convertToPresentation(point, targetType, locale);
            try{
            	text = ServiceStatusEnum.values()[point].toString();
            }
            catch(Exception e){
            	text = "Error!";
            }
            return text;
        };
    };*/
	
	private StringToEnumConverter statusConverter = new StringToEnumConverter() {
        @Override
        public String convertToPresentation(@SuppressWarnings("rawtypes") Enum point,
                java.lang.Class<? extends String> targetType, Locale locale)
                throws Converter.ConversionException {
            String text = super.convertToPresentation(point, targetType, locale);

            String color = "";
            FontAwesome icono = null;
            if (point == ServiceStatusEnum.GOOD) {
                color = "#2dd085"; // green
                icono = ICON_GOOD;
            } else if (point == ServiceStatusEnum.STOP) {
                color = "#ffc66e"; // yellow
                icono = ICON_STOP;
            } else if (point == ServiceStatusEnum.STARTING) {
                color = "#00b0ca"; // blue
                icono = ICON_STARTING;
            } else { // Failed
                color = "#f54993"; // red
                icono = ICON_FAIL;
            }

            String iconCode = "<span class=\"v-icon\" style=\"font-family: "
                    + icono.getFontFamily() + ";color:" + color
                    + "\">&#x"
                    + Integer.toHexString(icono.getCodepoint())
                    + ";</span>";

            return iconCode + " " + text;
        };
    };
    
    private StringToLongConverter dateConverter = new StringToLongConverter() {
    	@SuppressWarnings("deprecation")
		@Override
        public String convertToPresentation(Long point,
                java.lang.Class<? extends String> targetType, Locale locale)
                throws Converter.ConversionException {
            String text = super.convertToPresentation(point, targetType, locale);
            try{
            	Date date = new Date(point);
            	text = ""+date.toLocaleString();
            }
            catch(Exception e){
            	text = "Error!";
            }
            return text;
        };
    };
    
    public ProcessesGrid() {
        setSizeFull();

        setSelectionMode(SelectionMode.SINGLE);

        BeanItemContainer<ProcessEntity> container = 
        	new BeanItemContainer<ProcessEntity>(ProcessEntity.class);
        setContainerDataSource(container);
        setColumns("name", "service", "server", "status", "timeout", "timestamp", "restartOnFailure");
        
        // Add column renderer
        getColumn("status").setConverter(statusConverter).setRenderer(new HtmlRenderer());
        getColumn("timestamp").setConverter(dateConverter).setRenderer(new HtmlRenderer());
        getColumn("name").setWidth(200);
        getColumn("timeout").setWidth(90);
        getColumn("status").setWidth(90);
        getColumn("timestamp").setWidth(200);

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
	private BeanItemContainer<ProcessEntity> getContainer() {
        return (BeanItemContainer<ProcessEntity>) super.getContainerDataSource();
    }

    @Override
    public ProcessEntity getSelectedRow() throws IllegalStateException {
        return (ProcessEntity) super.getSelectedRow();
    }

    public void setRecords(List<ProcessEntity> records) {
    	if(getContainer()==null || getContainer().size()==0){
    		// Empty container
	        getContainer().removeAllItems();
	        getContainer().addAll(records);
	        return;
    	}
        // Not empty container
        List<ProcessEntity> listfound = new ArrayList<>();
        List<ProcessEntity> toremove = new ArrayList<>();
    	for(ProcessEntity item : getContainer().getItemIds()){
    		boolean found = false;
    		System.out.println("Item  = "+item.getID());
    		for(ProcessEntity rec : records){
    			System.out.println("Rec  = "+rec.getID());
        		if(rec.getID().equals(item.getID())){
        			found = true;
        			if(item.getTimestamp()!=rec.getTimestamp()
        				|| item.getStatus()!=rec.getStatus()){
        				item.setTimestamp(rec.getTimestamp());
        				item.setStatus(rec.getStatus());
        				refresh(item); // Updates existing records
        			}
        			listfound.add(rec); // add to list of found records
        		}
        	}
    		if(found==false){
    			toremove.add(item); // Add to list of records to be removed
    		}
    	}
    	System.out.println("Found  = "+listfound.size());
    	System.out.println("Remove = "+toremove.size());
    	// Removes no existing records
    	for(ProcessEntity rec : toremove){
    		this.remove(rec);
    	}
    	// Adds new items to the container
    	for(ProcessEntity rec : records){
    		if(!listfound.contains(rec))
    			getContainer().addBean(rec); // add only not found recs
    	}
    }

    @SuppressWarnings("rawtypes")
	public void refresh(ProcessEntity rec) {
        // We avoid updating the whole table through the backend here so we can
        // get a partial update for the grid
        BeanItem<ProcessEntity> item = getContainer().getItem(rec);
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

    public void remove(ProcessEntity rec) {
        getContainer().removeItem(rec);
    }
    
    public void removeFilters() {
        getContainer().removeAllContainerFilters();
	}

    public void filterbyServer(String service) {
		removeFilters();
		SimpleStringFilter nameFilter = new SimpleStringFilter(
                "server", service, true, false);
        getContainer().addContainerFilter(
                new Or(nameFilter));
	}

    public void filterbyService(String service, String server) {
		removeFilters();
		SimpleStringFilter nameFilter = new SimpleStringFilter(
                "server", server, true, false);
        SimpleStringFilter pointtypeFilter = new SimpleStringFilter(
                "service", service, true, false);
        getContainer().addContainerFilter(
                new And(nameFilter, pointtypeFilter));
	}
    
}
