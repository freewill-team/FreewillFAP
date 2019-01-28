package freewill.nextgen.alarmMonitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.StringToEnumConverter;
import com.vaadin.data.util.converter.StringToLongConverter;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.renderers.ClickableRenderer.RendererClickEvent;
import com.vaadin.ui.renderers.ClickableRenderer.RendererClickListener;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.renderers.ImageRenderer;

import freewill.nextgen.common.Utils.SeverityEnum;
import freewill.nextgen.common.entities.AlarmEntity;
import freewill.nextgen.hmi.utils.Messages;

/**
 * Grid of records, handling the visual presentation and filtering of a set of
 * items. This version uses an in-memory data source that is suitable for small
 * data sets.
 */

@SuppressWarnings("serial")
public class AlarmsGrid extends Grid {
	
	//private Logger log = Logger.getLogger(AlarmsGrid.class);
	AlarmMonitor view = null;
    
    private StringToLongConverter blankConverter = new StringToLongConverter() {
    	@Override
        public String convertToPresentation(Long point,
                java.lang.Class<? extends String> targetType, Locale locale)
                throws Converter.ConversionException {
            return "o";
        };
    };
	
	private StringToEnumConverter severityConverter = new StringToEnumConverter() {
        @Override
        public String convertToPresentation(@SuppressWarnings("rawtypes") Enum point,
                java.lang.Class<? extends String> targetType, Locale locale)
                throws Converter.ConversionException {
            String text = super.convertToPresentation(point, targetType, locale);

            String color = "";
            if (point == SeverityEnum.NONE
            	|| point == SeverityEnum.MINOR) {
                color = "#2dd085"; // green
            } else if (point == SeverityEnum.LOW
            	|| point == SeverityEnum.MEDIUM) {
                color = "#ffc66e"; // yellow
            } else if (point == SeverityEnum.HIGH) {
                color = "#00b0ca"; // blue
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
    
    public AlarmsGrid(AlarmMonitor view) {
    	this.view = view;
        setSizeFull();
        setSelectionMode(SelectionMode.SINGLE);

        BeanItemContainer<AlarmEntity> container = 
        	new BeanItemContainer<AlarmEntity>(AlarmEntity.class);
        setContainerDataSource(container);
        setColumns("id", "timestamp", "flashing", "severity", "point", "pointType", 
        		"message", "parentPoint", "category");
        
        // Add column renderer
        getColumn("severity").setConverter(severityConverter).setRenderer(new HtmlRenderer());
        getColumn("message").setWidth(480);
        getColumn("severity").setWidth(90);
        getColumn("timestamp").setWidth(180);
        sort("timestamp", SortDirection.DESCENDING);
        getColumn("flashing").setWidth(60);
        getColumn("flashing").setRenderer( new ImageRenderer(),
        		new Converter<Resource, Boolean>(){

					@Override
					public Boolean convertToModel(Resource value, Class<? extends Boolean> targetType, Locale locale)
							throws com.vaadin.data.util.converter.Converter.ConversionException {
						return true;
					}

					@Override
					public Resource convertToPresentation(Boolean value, Class<? extends Resource> targetType,
							Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
						if(value){
							return new ThemeResource("img/alarm.gif");
						}
						else 
							return null;
					}

					@Override
					public Class<Boolean> getModelType() {
						return Boolean.class;
					}

					@Override
					public Class<Resource> getPresentationType() {
						return Resource.class;
					}
        });
        
        RendererClickListener clickListener = new RendererClickListener() {
            private static final long serialVersionUID = 1L;

			@Override
			public void click(RendererClickEvent event) {
				AlarmEntity rec = (AlarmEntity) event.getItemId();
				if(rec!=null)
					view.ackAlarm(rec);
			}
        };
        ButtonRenderer renderer = new ButtonRenderer(clickListener, "");
        this.getColumn("id").setConverter(blankConverter).setRenderer(renderer);
        
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
	private BeanItemContainer<AlarmEntity> getContainer() {
        return (BeanItemContainer<AlarmEntity>) super.getContainerDataSource();
    }

    @Override
    public AlarmEntity getSelectedRow() throws IllegalStateException {
        return (AlarmEntity) super.getSelectedRow();
    }

    public void setRecords(/*Collection*/List<AlarmEntity> records) {
    	if(getContainer()==null || getContainer().size()==0){
    		// Empty container
	        getContainer().removeAllItems();
	        getContainer().addAll(records);
	        sort("timestamp", SortDirection.DESCENDING);
	        return;
    	}
    	/*System.out.println(">>>Records items = "+records.size());
    	for(AlarmEntity rec : records){
    		System.out.println(rec);
    	}
    	System.out.println(">>>Container items = "+getContainer().getItemIds().size());*/
        // Not empty container
    	List<AlarmEntity> listfound = new ArrayList<>();
    	List<AlarmEntity> toremove = new ArrayList<>();
    	for(AlarmEntity item : getContainer().getItemIds()){
    		//System.out.println(">>>>>Processing Container ITEM = "+item);
    		boolean found = false;
    		for(AlarmEntity rec : records){
    			//System.out.println(">>>>>>>>Checking "+rec.getID());
        		if(rec.getId()==item.getId()){
        			found = true;
        			//System.out.println(">>>>>>>>>>>>>Found in RECORDs = "+rec);
        			if(item.getFlashing()!=rec.getFlashing()){
        				//System.out.println("UPDATING...");
        				item.setFlashing(rec.getFlashing());
        				refresh(item); // Updates existing records
        			}
        			listfound.add(rec); // add to list of found records
        		}
        	}
    		if(found==false){
    			toremove.add(item); // Add to list of records to be removed
    			//System.out.println(">>>>>Not found in RECORDs. Removing from container.");
    		}
    	}
    	// Removes no existing records
    	for(AlarmEntity rec : toremove){
    		this.remove(rec);
    		//System.out.println(">>>>>removing from Container = "+rec);
    	}
    	// Adds new items to the container
    	boolean found = false;
    	for(AlarmEntity rec : records){
    		if(!listfound.contains(rec)){
    			getContainer().addBean(rec); // add only not found recs
    			//System.out.println(">>>>>Adding from Records = "+rec);
    			found = true;
    		}
    	}
    	if(found)
    		this.showNotification(Messages.get().getKey("newalarmspending"));
        sort("timestamp", SortDirection.DESCENDING);
    }

    @SuppressWarnings("rawtypes")
	public void refresh(AlarmEntity rec) {
        // We avoid updating the whole table through the backend here so we can
        // get a partial update for the grid
        BeanItem<AlarmEntity> item = getContainer().getItem(rec);
        if (item != null) {
        	System.out.println("Exists "+item.getBean());
            // Updated record
            MethodProperty p = (MethodProperty) item.getItemProperty("ID");
            if(p!=null)
            	p.fireValueChange();
        } else {
            // New record
            getContainer().addBean(rec);
        }
    }

    public void remove(AlarmEntity rec) {
        getContainer().removeItem(rec);
    }
    
    public void removeFilters() {
        getContainer().removeAllContainerFilters();
	}
    
    /* public class MyButtonRenderer extends ButtonRenderer {
	    @Override
	    public void render(RendererCellReference cell, String text, Button button) {
	        boolean enabled = true;
	        if (text.startsWith("Disabled:")) {
	            text = text.substring("Disabled:".length());
	            enabled = false;
	        }
	        button.setText(text);
	        button.setEnabled(enabled);
	    }
	}*/
    
    public void showNotification(String msg) {
    	view.showNotification(msg);
        //Notification.show(msg, Type.TRAY_NOTIFICATION);
    }
    
}
