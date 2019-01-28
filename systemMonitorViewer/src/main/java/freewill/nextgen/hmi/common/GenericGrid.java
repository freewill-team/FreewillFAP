package freewill.nextgen.hmi.common;

import java.util.Collection;
import java.util.Locale;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.data.util.converter.StringToBooleanConverter;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Grid;
import com.vaadin.data.util.converter.*;
import com.vaadin.ui.renderers.HtmlRenderer;

import freewill.nextgen.hmi.utils.Messages;

/**
 * Grid of records, handling the visual presentation and filtering of a set of
 * items. This version uses an in-memory data source that is suitable for small
 * data sets.
 */
@SuppressWarnings("serial")
public class GenericGrid<T> extends Grid {
	
	private String idfield = "";

    private StringToBooleanConverter pointtypeConverter = new StringToBooleanConverter() {
        @Override
        public String convertToPresentation(Boolean pointstatus,
                java.lang.Class<? extends String> targetType, Locale locale)
                throws Converter.ConversionException {
            //String text = super.convertToPresentation(pointstatus, targetType, locale);

            String color = "";
            if (pointstatus == true) {
                color = "#2dd085"; // green
            //} else if (pointstatus == false) {
            //    color = "#ffc66e"; // yellow
            } else {
                color = "#f54993"; // red
            }

            String iconCode = "<span class=\"v-icon\" style=\"font-family: "
                    + FontAwesome.CIRCLE.getFontFamily() + ";color:" + color
                    + "\">&#x"
                    + Integer.toHexString(FontAwesome.CIRCLE.getCodepoint())
                    + ";</span>";

            return iconCode;
        };
    };

    public GenericGrid(Class<T> entity, String idfield, String... fields) {
    	
        setSizeFull();
        setSelectionMode(SelectionMode.SINGLE);
        this.idfield = idfield;
        
        BeanItemContainer<T> container = new BeanItemContainer<T>(entity);
        setContainerDataSource(container);
        // setColumns("id", "label", "description");
        this.removeAllColumns();
        this.addColumn(idfield);
        for (String field : fields) {
        	try{
	        	java.lang.reflect.Field member = entity.getDeclaredField(field);
	            if(member==null) continue;
				String paramType = member.getType().toGenericString();
	        	if(paramType.contains("CustomersClass") ||
	        		paramType.contains("Vehicle") ||
	        		paramType.contains("VehicleType") ||
	        		paramType.contains("Personnel") ||
	        		paramType.contains("OrgHierarchyNode") ||
	        		paramType.contains("OperHierarchyNode") ||
	        		paramType.contains("CustomersSubClass")) 
	        		continue;
	        	Column col = this.addColumn(field);
	        	if(field.contains("label") || field.contains("Label"))
	        		col.setWidth(140);
	        	if(field.contains("timestamp"))
	        		col.setWidth(180);
				if(paramType.contains("oolean")){
					col.setConverter(pointtypeConverter).setRenderer(new HtmlRenderer());
					col.setWidth(80);
				}
        	}
        	catch(Exception e){
        		System.out.println("Error en GenericGrid: " + e.getMessage());
        	}
        }
        getColumn(idfield).setWidth(60);
        
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
                    "label", filterString, true, false);
            getContainer().addContainerFilter(
                    new Or(nameFilter));
        }

    }

    @SuppressWarnings("unchecked")
	private BeanItemContainer<T> getContainer() {
        return (BeanItemContainer<T>) super.getContainerDataSource();
    }

	@SuppressWarnings("unchecked")
	@Override
    public T getSelectedRow() throws IllegalStateException {
        return (T) super.getSelectedRow();
    }

    public void setRecords(Collection<T> records) {
        getContainer().removeAllItems();
        if(records!=null)
        	getContainer().addAll(records);
    }

    @SuppressWarnings("rawtypes")
	public void refresh(T rec) {
        // We avoid updating the whole table through the backend here so we can
        // get a partial update for the grid
        BeanItem<T> item = getContainer().getItem(rec);
        if (item != null) {
            // Updated record
            MethodProperty p = (MethodProperty) item.getItemProperty(idfield); //"ID");
            if(p!=null)
            	p.fireValueChange();
        } else {
            // New record
            getContainer().addBean(rec);
        }
    }

    public void remove(T rec) {
        getContainer().removeItem(rec);
    }
}
