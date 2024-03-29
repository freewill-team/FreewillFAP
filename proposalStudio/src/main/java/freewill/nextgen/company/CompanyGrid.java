package freewill.nextgen.company;

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

import freewill.nextgen.data.CompanyEntity;
import freewill.nextgen.hmi.utils.Messages;

/**
 * Grid of records, handling the visual presentation and filtering of a set of
 * items. This version uses an in-memory data source that is suitable for small
 * data sets.
 */
@SuppressWarnings("serial")
public class CompanyGrid extends Grid {

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

    public CompanyGrid() {
        setSizeFull();
        
        setSelectionMode(SelectionMode.SINGLE);

        BeanItemContainer<CompanyEntity> container = 
        	new BeanItemContainer<CompanyEntity>(CompanyEntity.class);
        setContainerDataSource(container);
        setColumns("ID", "name", "expirationdate", "active", "timestamp");

        // Add an traffic light icon in front of pointType
        getColumn("active").setConverter(pointtypeConverter).setRenderer(new HtmlRenderer());
        getColumn("ID").setMaximumWidth(80);
        getColumn("active").setMaximumWidth(100);
        
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
                    "name", filterString, true, false);
            SimpleStringFilter pointtypeFilter = new SimpleStringFilter(
                    "active", filterString, true, false);
            getContainer().addContainerFilter(
                    new Or(nameFilter, pointtypeFilter));
        }
    }

    @SuppressWarnings("unchecked")
	private BeanItemContainer<CompanyEntity> getContainer() {
        return (BeanItemContainer<CompanyEntity>) super.getContainerDataSource();
    }

    @Override
    public CompanyEntity getSelectedRow() throws IllegalStateException {
        return (CompanyEntity) super.getSelectedRow();
    }

    public void setRecords(Collection<CompanyEntity> records) {
        getContainer().removeAllItems();
        if(records!=null)
        	getContainer().addAll(records);
    }

	public void refresh(CompanyEntity rec) {
        // We avoid updating the whole table through the backend here so we can
        // get a partial update for the grid
        BeanItem<CompanyEntity> item = getContainer().getItem(rec);
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

    public void remove(CompanyEntity rec) {
        getContainer().removeItem(rec);
    }
}
