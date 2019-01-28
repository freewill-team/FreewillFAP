package freewill.nextgen.mapping;

import java.util.Collection;
import java.util.Locale;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.StringToBooleanConverter;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.HtmlRenderer;

import freewill.nextgen.data.RequirementMapping;
import freewill.nextgen.hmi.utils.Messages;

/**
 * Grid of records, handling the visual presentation and filtering of a set of
 * items. This version uses an in-memory data source that is suitable for small
 * data sets.
 */
@SuppressWarnings("serial")
public class MappingGrid extends Grid {

    private StringToBooleanConverter pointtypeConverter = new StringToBooleanConverter() {
        @Override
        public String convertToPresentation(Boolean pointstatus,
                java.lang.Class<? extends String> targetType, Locale locale)
                throws Converter.ConversionException {
            //String text = super.convertToPresentation(pointstatus, targetType, locale);

            String color = "";
            if (pointstatus == true) {
                color = "#2dd085"; // green
            /*} else if (pointstatus == false) {
                color = "#ffc66e"; // yellow*/
            } else {
                color = "#f54993"; // red
            }

            String iconCode = "<span class=\"v-icon\" style=\"font-family: "
                    + FontAwesome.CIRCLE.getFontFamily() + ";color:" + color
                    + "\">&#x"
                    + Integer.toHexString(FontAwesome.CIRCLE.getCodepoint())
                    + ";</span>";

            return iconCode /*+ " " + text*/;
        };
    };
    
    /*private StringToLongConverter mappingConverter = new StringToLongConverter() {
        @Override
        public String convertToPresentation(Long point,
                java.lang.Class<? extends String> targetType, Locale locale)
                throws Converter.ConversionException {
            //String text = super.convertToPresentation(point, targetType, locale);

            String mapping = Messages.get().getKey("pending");
            if(point!=null && point>0L){
	            try {
            		MappingEntity rec = (MappingEntity) BltClient.get().executeCommand(
            			"/getbyreq/"+point, 
						MappingEntity.class,
						EntryPoint.get().getAccessControl().getTokenKey());
            		if(rec!=null){
            			mapping = rec.getResponse();
            			FeatureEntity doc = (FeatureEntity) BltClient.get().getEntityById(
            					""+rec.getDoc(), FeatureEntity.class,
            					EntryPoint.get().getAccessControl().getTokenKey());
        	            if(doc!=null){
        	            	if(!doc.getTitle().equals(""))
        	            		mapping = mapping + ": "+ doc.getTitle();
        	            	else 
        	            		mapping = mapping + ": "+ doc.getDescription();
        	            }
            		}
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
            return mapping;
        };
    };*/
    
    public MappingGrid() {
        setSizeFull();

        setSelectionMode(SelectionMode.SINGLE);

        BeanItemContainer<RequirementMapping> container = 
        	new BeanItemContainer<RequirementMapping>(RequirementMapping.class);
        setContainerDataSource(container);
        setColumns(/*"id",*/ "customid", "description", "resolved", "response", "mapping", "product");
        
        // Add an traffic light icon in front of pointType
        //getColumn("ID").setConverter(mappingConverter).setRenderer(new HtmlRenderer());
        getColumn("resolved").setConverter(pointtypeConverter).setRenderer(new HtmlRenderer());
        //getColumn("assignedto").setConverter(userConverter).setRenderer(new HtmlRenderer());
        getColumn("description").setMinimumWidth(300);
        getColumn("description").setMaximumWidth(550);
        getColumn("customid").setMaximumWidth(140);
        getColumn("resolved").setMaximumWidth(100);
        
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
            SimpleStringFilter nameFilter = new SimpleStringFilter(
                    "customid", filterString, true, false);
            SimpleStringFilter pointtypeFilter = new SimpleStringFilter(
                    "resolved", filterString, true, false);
            SimpleStringFilter descFilter = new SimpleStringFilter(
                    "description", filterString, true, false);
            SimpleStringFilter userFilter = new SimpleStringFilter(
                    "response", filterString, true, false);
            getContainer().addContainerFilter(
                    new Or(nameFilter, pointtypeFilter, userFilter, descFilter));
        }
    }

    @SuppressWarnings("unchecked")
	private BeanItemContainer<RequirementMapping> getContainer() {
        return (BeanItemContainer<RequirementMapping>) super.getContainerDataSource();
    }

    @Override
    public RequirementMapping getSelectedRow() throws IllegalStateException {
        return (RequirementMapping) super.getSelectedRow();
    }

    public void setRecords(Collection<RequirementMapping> records) {
        getContainer().removeAllItems();
        if(records!=null)
        	getContainer().addAll(records);
    }

	public void refresh(RequirementMapping rec) {
        // We avoid updating the whole table through the backend here so we can
        // get a partial update for the grid
        BeanItem<RequirementMapping> item = getContainer().getItem(rec);
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

    public void remove(RequirementMapping rec) {
        getContainer().removeItem(rec);
    }
}
