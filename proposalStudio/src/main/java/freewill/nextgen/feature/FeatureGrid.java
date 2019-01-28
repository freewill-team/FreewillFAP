package freewill.nextgen.feature;

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

import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.FeatureEntity;
import freewill.nextgen.hmi.utils.Messages;
import freewill.nextgen.proposalStudio.EntryPoint;

/**
 * Grid of records, handling the visual presentation and filtering of a set of
 * items. This version uses an in-memory data source that is suitable for small
 * data sets.
 */
@SuppressWarnings("serial")
public class FeatureGrid extends Grid {

    @SuppressWarnings("unused")
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
    
    public FeatureGrid() {
        setSizeFull();

        setSelectionMode(SelectionMode.SINGLE);

        BeanItemContainer<FeatureEntity> container = 
        	new BeanItemContainer<FeatureEntity>(FeatureEntity.class);
        setContainerDataSource(container);
        if (EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.SUPER))
        	setColumns("ID", "level", "title", "imagename", "description", "active", "company"); //, "timestamp");																									
        else
        	setColumns("level", "title", "imagename", "description"); //, "active", "timestamp");	
        
        // Add an traffic light icon in front of pointType
        //getColumn("active").setConverter(pointtypeConverter).setRenderer(new HtmlRenderer());
        getColumn("imagename").setConverter(new StringToStringConverter()).setRenderer(new HtmlRenderer());
        getColumn("description").setMinimumWidth(500);
        getColumn("level").setMaximumWidth(120);
        //getColumn("active").setMaximumWidth(80);
        
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
                else if (cellReference.getPropertyId().equals("imagename")) {
                    return "align-center";
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
                    "title", filterString, true, false);
            SimpleStringFilter descFilter = new SimpleStringFilter(
                    "description", filterString, true, false);
            SimpleStringFilter pointtypeFilter = new SimpleStringFilter(
                    "active", filterString, true, false);
            SimpleStringFilter responseFilter = new SimpleStringFilter(
                    "level", filterString, true, false);
            getContainer().addContainerFilter(
                    new Or(nameFilter, descFilter, pointtypeFilter, responseFilter));
        }

    }

    @SuppressWarnings("unchecked")
	private BeanItemContainer<FeatureEntity> getContainer() {
        return (BeanItemContainer<FeatureEntity>) super.getContainerDataSource();
    }

    @Override
    public FeatureEntity getSelectedRow() throws IllegalStateException {
        return (FeatureEntity) super.getSelectedRow();
    }

    public void setRecords(Collection<FeatureEntity> records) {
        getContainer().removeAllItems();
        getContainer().addAll(records);
    }

	public void refresh(FeatureEntity rec) {
        // We avoid updating the whole table through the backend here so we can
        // get a partial update for the grid
        BeanItem<FeatureEntity> item = getContainer().getItem(rec);
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

    public void remove(FeatureEntity rec) {
        getContainer().removeItem(rec);
    }
    
    public class StringToStringConverter implements Converter<String, String> {

        public Class<String> getModelType() {
            return String.class;
        }

        public Class<String> getPresentationType() {
            return String.class;
        }

		@Override
		public String convertToModel(String text, Class<? extends String> targetType, Locale locale)
				throws com.vaadin.data.util.converter.Converter.ConversionException {
			if (text == null) {
                return null;
            }
            return text;
		}

		@Override
		public String convertToPresentation(String name, Class<? extends String> targetType, Locale locale)
				throws com.vaadin.data.util.converter.Converter.ConversionException {
			if (name == null) {
                return null;
            } else {			
				String color = "";
				String iconCode = "";
	            if (!name.equals("")) {
	            	color = "#f54993"; // red
	                iconCode = "<span class=\"v-icon\" style=\"font-family: "
		                    + FontAwesome.FILE_IMAGE_O.getFontFamily() + ";color:" + color
		                    + "\">&#x"
		                    + Integer.toHexString(FontAwesome.FILE_IMAGE_O.getCodepoint())
		                    + ";</span>";
	            } else {
	                color = "#2dd085"; // green
	            }
	            
            	return iconCode; 
            }
		}
    }
    
}
