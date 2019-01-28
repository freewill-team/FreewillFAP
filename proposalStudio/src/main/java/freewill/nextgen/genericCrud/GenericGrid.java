package freewill.nextgen.genericCrud;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Locale;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
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
	
	@SuppressWarnings("unused")
	private String idfield = "";
	private String fields[];

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
        this.fields = fields;
        BeanItemContainer<T> container = new BeanItemContainer<T>(entity);
        setContainerDataSource(container);
        this.removeAllColumns();
        this.addColumn(idfield);
        for (String field : fields) {
        	try{
	        	java.lang.reflect.Field member = entity.getDeclaredField(field);
	            if(member==null) continue;
				String paramType = member.getType().toGenericString().toLowerCase();
				System.out.println("Grid ParamType="+paramType);
	        	Column col = this.addColumn(field);
	        	if(field.contains("label") || field.contains("Label"))
	        		col.setWidth(140);
	        	if(field.contains("timestamp"))
	        		col.setWidth(150);
				if(paramType.contains("boolean")){
					col.setConverter(pointtypeConverter).setRenderer(new HtmlRenderer());
					col.setWidth(80);
				}
				else if(!paramType.contains("string") && !paramType.contains("double") 
						 && !paramType.contains("float")  && !paramType.contains("integer")
						 && !paramType.equals("int")  && !paramType.contains("enum") 
						 && !paramType.contains("bigdecimal") && !paramType.contains("long")
						 && !paramType.contains("date")){
					System.out.println("HtmlRendered for = "+field);
					col.setRenderer( new HtmlRenderer(), new Converter<String, Object>() {
							
			                @Override
			                public Class<String> getPresentationType() {
			                    return String.class;
			                }

							@Override
							public String convertToPresentation(Object value, Class<? extends String> targetType,
									Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
								if(value==null)
									return "null";
								String text = "error";
								try{
									Method[] methods = value.getClass().getMethods();
									for(Method m:methods){
										if(m.getName().startsWith("get") && m.getName().endsWith("Label"))
											text = (String) m.invoke(value);
									}
								}
								catch(Exception e){
									System.out.println("Error en HtmlRenderer: " + e.getMessage());
								}
								return text;
							}

							@Override
							public T convertToModel(String value, Class<? extends Object> targetType, Locale locale)
									throws com.vaadin.data.util.converter.Converter.ConversionException {
								return null;
							}

							@Override
							public Class<Object> getModelType() {
								return Object.class;
							}
			            
					});
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
        /*if (filterString.length() > 0) {
            SimpleStringFilter nameFilter = new SimpleStringFilter(
                    "name", filterString, true, false);
            SimpleStringFilter labelFilter = new SimpleStringFilter(
                    "label", filterString, true, false);
            getContainer().addContainerFilter(
                    new Or(nameFilter, labelFilter));
        }*/
        if (filterString.length() > 0 && fields.length >0) {
        	SimpleStringFilter[] filters = new SimpleStringFilter[fields.length];
        	 for(int i=0; i<fields.length; i++) {
        		 filters[i] = new SimpleStringFilter(fields[i], filterString, true, false);
        	 }
        	 getContainer().addContainerFilter(new Or(filters));
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

	public void refresh(T rec) {
        // We avoid updating the whole table through the backend here so we can
        // get a partial update for the grid
        BeanItem<T> item = getContainer().getItem(rec);
        if (item != null) {
            // Updated record
        	item.setBean(rec);
            //MethodProperty p = (MethodProperty) item.getItemProperty(idfield); //"ID");
            //if(p!=null) p.fireValueChange();
        } else {
            // New record
            getContainer().addBean(rec);
        }
    }

    public void remove(T rec) {
        getContainer().removeItem(rec);
    }
}
