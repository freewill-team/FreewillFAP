package freewill.nextgen.eventSummary;

import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.StringToEnumConverter;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.common.Utils.SeverityEnum;
import freewill.nextgen.common.entities.EventEntity;
import freewill.nextgen.hmi.utils.Messages;

/**
 * Grid of records, handling the visual presentation and filtering of a set of
 * items. This version uses an in-memory data source that is suitable for small
 * data sets.
 */
@SuppressWarnings("serial")
public class EventGrid extends Grid {
	
	EventSummary view = null;

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

    @SuppressWarnings("deprecation")
	public EventGrid(EventSummary miview) {
    	view = miview;
        setSizeFull();
        setSelectionMode(SelectionMode.SINGLE);

        BeanItemContainer<EventEntity> container = 
        	new BeanItemContainer<EventEntity>(EventEntity.class);
        setContainerDataSource(container);
        setColumns("timestamp", "severity", "point", "pointType", "message", "parentPoint", "category", "username", "console");
        
        // Add an traffic light icon in front of pointType
        getColumn("severity").setConverter(severityConverter).setRenderer(new HtmlRenderer());
        getColumn("point").setWidth(300);
        getColumn("message").setWidth(600);
        getColumn("severity").setWidth(90);
        getColumn("timestamp").setWidth(200);
        sort("timestamp", SortDirection.DESCENDING);
        
        for(Column col:this.getColumns()){
        	col.setHeaderCaption(Messages.get().getKey(col.getPropertyId().toString()));
        }

        // Align columns using a style generator and theme rule until #15438
        setCellStyleGenerator(new CellStyleGenerator() {

            @Override
            public String getStyle(CellReference cellReference) {
                if (cellReference.getPropertyId().equals("company")) {
                    return "align-right";
                }
                return null;
            }
        });
        
        // Create a header row to hold column filters
        HeaderRow filterRow = this.appendHeaderRow();
        
        // Set up a filter for all columns
        for(Column col:this.getColumns()){
        	Object pid = col.getPropertyId();
        	//System.out.println("Pid="+pid);
        	
        	if(pid.equals("timestamp")){
        		HeaderCell cell = filterRow.getCell(pid);

                // Have an input fields to use for filter
        		HorizontalLayout datefieldLayout = new HorizontalLayout();
        		DateField startDateField = new DateField();
        		startDateField.addStyleName(ValoTheme.DATEFIELD_TINY);
        		startDateField.setWidth("80px");
        		DateField endDateField = new DateField();
        		endDateField.addStyleName(ValoTheme.DATEFIELD_TINY);
        		endDateField.setWidth("80px");
        		Label label = new Label(" - ");
        		datefieldLayout.addComponent(startDateField);
        		datefieldLayout.addComponent(label);
        		datefieldLayout.addComponent(endDateField);
        		
        		int browserOffset = Page.getCurrent().getWebBrowser().getTimezoneOffset();
        		Date now = new Date();
        		int eventOffset = now.getTimezoneOffset()*60*1000;
        		Date edate = new Date(now.getTime()+(browserOffset+eventOffset));
            	Date sdate = new Date(edate.getTime()-86400000L); // Last 24 hours
            	
            	startDateField.setResolution(Resolution.MINUTE);
            	endDateField.setResolution(Resolution.MINUTE);
                startDateField.setValue(sdate);
                endDateField.setValue(edate);
        		
        		startDateField.addValueChangeListener(getDateFieldValueChangeListener(
        			startDateField, endDateField, pid));
        		endDateField.addValueChangeListener(getDateFieldValueChangeListener(
        			startDateField, endDateField, pid));                
        		
                cell.setComponent(datefieldLayout);
        	}
        	else {
        		HeaderCell cell = filterRow.getCell(pid);

                // Have an input field to use for filter
                TextField filterField = new TextField();
                filterField.setWidthUndefined();
                filterField.addStyleName(ValoTheme.TEXTFIELD_TINY);

                // Update filter When the filter input is changed
                filterField.addTextChangeListener(change -> {
                    // Can't modify filters so need to replace
                    container.removeContainerFilters(pid);

                    // (Re)create the filter if necessary
                    if (! change.getText().isEmpty())
                        container.addContainerFilter(new SimpleStringFilter(pid, change.getText(), true, false));
                });
                
                cell.setComponent(filterField);
        	}
        	
        }
                
    }

    private ValueChangeListener getDateFieldValueChangeListener(
    		final DateField startDateField, final DateField endDateField,
    		final Object headerID) {
    		return new ValueChangeListener() {
    		
    		@SuppressWarnings("deprecation")
			@Override
    		public void valueChange(ValueChangeEvent event) {
    			int browserOffset = Page.getCurrent().getWebBrowser().getTimezoneOffset();
    	    	System.out.println("browserOffset="+browserOffset);
    	    	int eventOffset = endDateField.getValue().getTimezoneOffset()*60*1000;
        		System.out.println("eventOffset="+eventOffset);
        		
        		Date newStartDate = new Date(startDateField.getValue().getTime()-(browserOffset+eventOffset)); //startDateField.getValue();
    			Date endDate = new Date(endDateField.getValue().getTime()-(browserOffset+eventOffset)); //endDateField.getValue();
    			
    			view.setFilter(newStartDate, endDate);
    		}
    	};
    }
    
    /**
     * Filter the grid based on a search string that is searched for in the
     * name and pointType columns.
     *
     * @param filterString
     *            string to look for
     */
    public void setFilter(String filterString) {
        getContainer().removeAllContainerFilters();
        if (filterString.length() > 0) {
            SimpleStringFilter msgFilter = new SimpleStringFilter(
                    "message", filterString.toString(), true, false);
            SimpleStringFilter consoleFilter = new SimpleStringFilter(
                    "console", filterString.toString(), true, false);
            SimpleStringFilter userFilter = new SimpleStringFilter(
                    "username", filterString.toString(), true, false);
            SimpleStringFilter dateFilter = new SimpleStringFilter(
                    "timestamp", filterString.toString(), true, false);
            getContainer().addContainerFilter(
                    new Or(msgFilter, userFilter, consoleFilter, dateFilter));
        }
    }
    
    @SuppressWarnings("unchecked")
	private BeanItemContainer<EventEntity> getContainer() {
        return (BeanItemContainer<EventEntity>) super.getContainerDataSource();
    }

    @Override
    public EventEntity getSelectedRow() throws IllegalStateException {
        return (EventEntity) super.getSelectedRow();
    }

    @SuppressWarnings("deprecation")
	public void setRecords(Collection<EventEntity> records) {
    	int browserOffset = Page.getCurrent().getWebBrowser().getTimezoneOffset();
    	System.out.println("browserOffset="+browserOffset);
    	
    	for(EventEntity rec : records){
    		int eventOffset = rec.getTimestamp().getTimezoneOffset()*60*1000;
    		//System.out.println("eventOffset="+eventOffset);
    		Date newdate = new Date(rec.getTimestamp().getTime()+(browserOffset+eventOffset));
    		rec.setTimestamp(newdate);
    	}
    	
        getContainer().removeAllItems();
        getContainer().addAll(records);
        sort("timestamp", SortDirection.DESCENDING);
    }

	public void refresh(EventEntity rec) {
        // We avoid updating the whole table through the backend here so we can
        // get a partial update for the grid
        BeanItem<EventEntity> item = getContainer().getItem(rec);
        if (item != null) {
            // Updated record
        	item.setBean(rec);
            //MethodProperty p = (MethodProperty) item.getItemProperty("ID");
            //p.fireValueChange();
        } else {
            // New record
            getContainer().addBean(rec);
        }
    }

    public void remove(EventEntity rec) {
        getContainer().removeItem(rec);
    }
}
