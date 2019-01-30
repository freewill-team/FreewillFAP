package freewill.nextgen.genericCrud;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Locale;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Grid;
import com.vaadin.data.util.converter.*;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.data.ClubEntity;
import freewill.nextgen.data.CompeticionEntity;
import freewill.nextgen.data.SaltoIntentoEntity.ResultEnum;
import freewill.nextgen.data.CategoriaEntity;
import freewill.nextgen.data.CircuitoEntity;
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
            String iconFamily = FontAwesome.CIRCLE.getFontFamily();
            int iconCode = FontAwesome.CIRCLE.getCodepoint();
            if (pointstatus == true) {
                color = "#2dd085"; // green
                iconFamily = FontAwesome.CHECK.getFontFamily();
            	iconCode = FontAwesome.CHECK.getCodepoint();
            } else {
                color = "#f54993"; // red
                iconFamily = FontAwesome.MINUS_CIRCLE.getFontFamily();
            	iconCode = FontAwesome.MINUS_CIRCLE.getCodepoint();
            }

            String icon = "<span class=\"v-icon\" style=\"font-family: "
                    + iconFamily + ";color:" + color
                    + "\">&#x"
                    + Integer.toHexString(iconCode)
                    + ";</span>";

            return icon /*+ " "+ text*/;
        };
    };
    
    //private StringToLongConverter genericConverter = new StringToLongConverter() {
    class GenericConverter<Q> extends StringToLongConverter {
    	
    	private Class<Q> ientity = null;
    	private String ifield = null;
    	
    	public GenericConverter(Class<Q> myentity, String field){
    		this.ientity = myentity;
    		this.ifield = field;
    	}
    	
        @SuppressWarnings("unchecked")
		@Override
        public String convertToPresentation(Long point,
                java.lang.Class<? extends String> targetType, Locale locale)
                throws Converter.ConversionException {
            //String text = super.convertToPresentation(point, targetType, locale);

            String nombre = "Undefined";
            if(point!=null && point!=0){
				try {
					Q rec = (Q) BltClient.get().getEntityById(
							""+point, ientity, EntryPoint.get().getAccessControl().getTokenKey());
					if(rec!=null){
						Method[] methods = rec.getClass().getMethods();
						for(Method m:methods){
							if(m.getName().startsWith("get") && m.getName().endsWith(ifield))
								nombre = (String) m.invoke(rec);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
            return nombre;
        };
    };
    
    private StringToIntegerConverter timeConverter = new StringToIntegerConverter() {  	
		@Override
        public String convertToPresentation(Integer point,
                java.lang.Class<? extends String> targetType, Locale locale)
                throws Converter.ConversionException {
            String text = super.convertToPresentation(point, targetType, locale);
            if(point>99999)
            	return "Nulo";
            else
            	return text;
        };
    };
    
    private StringToIntegerConverter dorsalConverter = new StringToIntegerConverter() {  	
		@Override
        public String convertToPresentation(Integer point,
                java.lang.Class<? extends String> targetType, Locale locale)
                throws Converter.ConversionException {
            String text = super.convertToPresentation(point, targetType, locale);
            if(point==null || point==0)
            	return "No Presentado";
            else
            	return text;
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
        		Column col = this.addColumn(field);
	        	java.lang.reflect.Field member = entity.getDeclaredField(field);
	            if(member==null) continue;
				String paramType = member.getType().toGenericString().toLowerCase();
				//System.out.println("Grid ParamType="+paramType);
	        	if(field.contains("label") || field.contains("label") 
	        			|| field.contains("orden") || field.contains("clasificacion"))
	        		col.setWidth(120);
	        	if(field.contains("timestamp") || field.contains("dorsal"))
	        		col.setWidth(140);
				
	        	if(paramType.contains("boolean")){
					col.setConverter(pointtypeConverter).setRenderer(new HtmlRenderer());
					col.setWidth(80);
				}
				else if(paramType.equals("int") && field.toLowerCase().contains("tiempo")){
					col.setConverter(timeConverter).setRenderer(new HtmlRenderer());
				}
				else if(paramType.contains("integer") && field.toLowerCase().contains("dorsal")){
					col.setConverter(dorsalConverter).setRenderer(new HtmlRenderer());
				}
				else if(paramType.equals("int") && field.toLowerCase().contains("dorsal")){
					col.setConverter(dorsalConverter).setRenderer(new HtmlRenderer());
				}
				else if(paramType.contains("date")){
					col.setRenderer(new DateRenderer("%1$td/%1$tm/%1$tY"));
					col.setWidth(150);
				}
				else if(paramType.contains("long") && field.equals("club")){
					col.setRenderer( new HtmlRenderer(), 
							new GenericConverter<ClubEntity>(ClubEntity.class, "Nombre"));
				}
				else if(paramType.contains("long") && field.equals("categoria")){
					col.setRenderer( new HtmlRenderer(), 
							new GenericConverter<CategoriaEntity>(CategoriaEntity.class, "Nombre"));
				}
				else if(paramType.contains("long") && field.equals("competicion")){
					col.setRenderer( new HtmlRenderer(), 
							new GenericConverter<CompeticionEntity>(CompeticionEntity.class, "Nombre"));
				}
				else if(paramType.contains("long") && field.equals("circuito")){
					col.setRenderer( new HtmlRenderer(), 
							new GenericConverter<CircuitoEntity>(CircuitoEntity.class, "Nombre"));
				}
				else if(paramType.contains("enum") && field.contains("salto")){
					col.setConverter(saltoConverter).setRenderer(new HtmlRenderer());
					col.setWidth(120);
				}
				else if(!paramType.contains("string") && !paramType.contains("double") 
						 && !paramType.contains("float")  && !paramType.contains("integer")
						 && !paramType.equals("int")  && !paramType.contains("enum") 
						 && !paramType.contains("bigdecimal") && !paramType.contains("long")
						 && !paramType.contains("date")){
					//System.out.println("HtmlRendered for = "+field);
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
        getColumn(idfield).setHidden(true); // As per requested by Mar
        
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
        if (filterString.length() > 0 && fields.length >0) {
        	String filtros[] = filterString.split("&");
        	for(String filter:filtros){
	        	SimpleStringFilter[] filters = new SimpleStringFilter[fields.length];
	        	for(int i=0; i<fields.length; i++) {
	        		filters[i] = new SimpleStringFilter(fields[i], filter/*filterString*/, true, false);
	        	}
	        	getContainer().addContainerFilter(new Or(filters));
        	}
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
    
    private StringToEnumConverter saltoConverter = new StringToEnumConverter() {
        @Override
        public String convertToPresentation(@SuppressWarnings("rawtypes") Enum point,
                java.lang.Class<? extends String> targetType, Locale locale)
                throws Converter.ConversionException {
            String text = super.convertToPresentation(point, targetType, locale);

            String color = "";
            String iconFamily = FontAwesome.CIRCLE.getFontFamily();
            int iconCode = FontAwesome.CIRCLE.getCodepoint();
            switch((ResultEnum)point){
	            case PENDIENTE:
	            	color = "#ffffff"; // white
	            	iconFamily = FontAwesome.CIRCLE.getFontFamily();
	            	iconCode = FontAwesome.CIRCLE.getCodepoint();
	            	break;
	            case PASA:
	            	//color = "#ffc66e"; // yellow
	            	color = "#00b0ca"; // blue
	            	iconFamily = FontAwesome.TIMES.getFontFamily();
	            	iconCode = FontAwesome.TIMES.getCodepoint();
	            	break;
	            case OK:
	            	color = "#2dd085"; // green
	            	iconFamily = FontAwesome.CHECK.getFontFamily();
	            	iconCode = FontAwesome.CHECK.getCodepoint();
	            	break;
	            case FALLO:
	            	color = "#f54993"; // red
	            	iconFamily = FontAwesome.MINUS_CIRCLE.getFontFamily();
	            	iconCode = FontAwesome.MINUS_CIRCLE.getCodepoint();
	            	break;
            }

            String icon = "<span class=\"v-icon\" style=\"font-family: "
                    + iconFamily + ";color:" + color
                    + "\">&#x"
                    + Integer.toHexString(iconCode)
                    + ";</span>";

            return icon + " "+ text;
        };
    };
    
}
