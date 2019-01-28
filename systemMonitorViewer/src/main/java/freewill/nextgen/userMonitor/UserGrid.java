package freewill.nextgen.userMonitor;

import java.util.ArrayList;
import java.util.List;
//import java.util.Locale;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.MethodProperty;
//import com.vaadin.data.util.converter.Converter;
//import com.vaadin.data.util.converter.StringToBooleanConverter;
//import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Grid;
//import com.vaadin.ui.renderers.HtmlRenderer;

import freewill.nextgen.common.entities.LoginEntity;
import freewill.nextgen.hmi.utils.Messages;

/**
 * Grid of records, handling the visual presentation and filtering of a set of
 * items. This version uses an in-memory data source that is suitable for small
 * data sets.
 */
@SuppressWarnings("serial")
public class UserGrid extends Grid {
	
	/*private StringToBooleanConverter pointtypeConverter = new StringToBooleanConverter() {
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
    };*/
    
    public UserGrid() {
        setSizeFull();
        setSelectionMode(SelectionMode.SINGLE);

        BeanItemContainer<LoginEntity> container = 
        	new BeanItemContainer<LoginEntity>(LoginEntity.class);
        setContainerDataSource(container);
        setColumns("name", "console", "application", /*"uuid",*/ "lastCheckin");
        
        // Add column renderer
        //getColumn("active").setConverter(pointtypeConverter).setRenderer(new HtmlRenderer());
        //getColumn("description").setWidth(500);
        //getColumn("lastCheckin").setWidth(200);

        for(Column col:this.getColumns()){
        	col.setHeaderCaption(Messages.get().getKey(col.getPropertyId().toString()));
        }
        
        // Align columns using a style generator and theme rule until #15438
        setCellStyleGenerator(new CellStyleGenerator() {

            @Override
            public String getStyle(CellReference cellReference) {
                if (cellReference.getPropertyId().equals("lastCheckin")) {
                    return "align-right";
                }
                return null;
            }
        });
        
    }

    @SuppressWarnings("unchecked")
	private BeanItemContainer<LoginEntity> getContainer() {
        return (BeanItemContainer<LoginEntity>) super.getContainerDataSource();
    }

    @Override
    public LoginEntity getSelectedRow() throws IllegalStateException {
        return (LoginEntity) super.getSelectedRow();
    }

    public void setRecords(List<LoginEntity> records) {
        if(getContainer()==null || getContainer().size()==0){
    		// Empty container
	        getContainer().removeAllItems();
	        getContainer().addAll(records);
	        return;
    	}
        // Not empty container
        List<LoginEntity> listfound = new ArrayList<>();
        List<LoginEntity> toremove = new ArrayList<>();
    	for(LoginEntity item : getContainer().getItemIds()){
    		boolean found = false;
    		for(LoginEntity rec : records){
        		if(rec.getID()==item.getID()){
        			found = true;
        			if(item.getLastCheckin().getTime()!=rec.getLastCheckin().getTime()){
        				item.setLastCheckin(rec.getLastCheckin());
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
    	for(LoginEntity rec : toremove){
    		this.remove(rec);
    	}
    	// Adds new items to the container
    	for(LoginEntity rec : records){
    		if(!listfound.contains(rec))
    			getContainer().addBean(rec); // add only not found recs
    	}
    }

    @SuppressWarnings("rawtypes")
	public void refresh(LoginEntity rec) {
        // We avoid updating the whole table through the backend here so we can
        // get a partial update for the grid
        BeanItem<LoginEntity> item = getContainer().getItem(rec);
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

    public void remove(LoginEntity rec) {
        getContainer().removeItem(rec);
    }
    
    public void removeFilters() {
        getContainer().removeAllContainerFilters();
	}
    
}
