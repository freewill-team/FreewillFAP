package freewill.nextgen.feature;

import java.util.Collection;

import freewill.nextgen.data.Style;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.ui.Grid;

/**
 * Grid of records, handling the visual presentation and filtering of a set of
 * items. This version uses an in-memory data source that is suitable for small
 * data sets.
 */
@SuppressWarnings("serial")
public class StyleGrid extends Grid {

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

            return iconCode;
        };
    };*/
    
    public StyleGrid() {
        setSizeFull();

        setSelectionMode(SelectionMode.SINGLE);

        BeanItemContainer<Style> container = new BeanItemContainer<Style>(Style.class);
        setContainerDataSource(container);
        setColumns("name", "level");	
        
        // Add an traffic light icon in front of pointType
        //getColumn("active").setConverter(pointtypeConverter).setRenderer(new HtmlRenderer());
        getColumn("name").setHeaderCaption("Detected Word Style");
        //getColumn("styleid").setWidth(200);
        getColumn("level").setHeaderCaption("FreeWill Style");

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
	private BeanItemContainer<Style> getContainer() {
        return (BeanItemContainer<Style>) super.getContainerDataSource();
    }

    @Override
    public Style getSelectedRow() throws IllegalStateException {
        return (Style) super.getSelectedRow();
    }

    public void setRecords(Collection<Style> records) {
        getContainer().removeAllItems();
        getContainer().addAll(records);
    }

    @SuppressWarnings("rawtypes")
	public void refresh(Style rec) {
        // We avoid updating the whole table through the backend here so we can
        // get a partial update for the grid
        BeanItem<Style> item = getContainer().getItem(rec);
        if (item != null) {
            // Updated record
            MethodProperty p = (MethodProperty) item.getItemProperty("styleid"); // ID
            p.fireValueChange();
        } else {
            // New record
            getContainer().addBean(rec);
        }
    }

    public void remove(Style rec) {
        getContainer().removeItem(rec);
    }
        
}
