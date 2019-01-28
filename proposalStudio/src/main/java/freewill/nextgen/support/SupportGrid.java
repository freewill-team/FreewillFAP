package freewill.nextgen.support;

import java.util.Collection;
import java.util.Locale;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.StringToBooleanConverter;
import com.vaadin.data.util.converter.StringToLongConverter;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.HtmlRenderer;

import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.common.entities.UserEntity;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.SupportEntity;
import freewill.nextgen.hmi.utils.Messages;
import freewill.nextgen.proposalStudio.EntryPoint;

/**
 * Grid of records, handling the visual presentation and filtering of a set of
 * items. This version uses an in-memory data source that is suitable for small
 * data sets.
 */
@SuppressWarnings("serial")
public class SupportGrid extends Grid {

    private StringToBooleanConverter pointtypeConverter = new StringToBooleanConverter() {
        @Override
        public String convertToPresentation(Boolean pointstatus,
                java.lang.Class<? extends String> targetType, Locale locale)
                throws Converter.ConversionException {
            //String text = super.convertToPresentation(pointstatus, targetType, locale);

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

            return iconCode /*+ " " + text*/;
        };
    };
    
    private StringToLongConverter userConverter = new StringToLongConverter() {
        @Override
        public String convertToPresentation(Long point,
                java.lang.Class<? extends String> targetType, Locale locale)
                throws Converter.ConversionException {
            //String text = super.convertToPresentation(point, targetType, locale);

            String userName = "Undefined";
            if(point!=null && point!=0){
            	try {
					UserEntity rec = (UserEntity) BltClient.get().getEntityById(""+point, 
							UserEntity.class,
						EntryPoint.get().getAccessControl().getTokenKey());
					if(rec!=null)
			            userName = rec.getName();
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
            return userName;
        };
    };

    public SupportGrid() {
        setSizeFull();

        setSelectionMode(SelectionMode.SINGLE);

        BeanItemContainer<SupportEntity> container = 
        	new BeanItemContainer<SupportEntity>(SupportEntity.class);
        setContainerDataSource(container);
        if (EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.SUPER))
        	setColumns("ID", "created", "description", "severity", "resolved", "user");
        else
        	setColumns("ID", "created", "description", "severity", "resolved");
        
        // Add an traffic light icon in front of pointType
        getColumn("resolved").setConverter(pointtypeConverter).setRenderer(new HtmlRenderer());
        getColumn("ID").setMaximumWidth(80);
        getColumn("resolved").setMaximumWidth(100);
        getColumn("description").setMinimumWidth(300);
        if (EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.SUPER))
        	getColumn("user").setConverter(userConverter).setRenderer(new HtmlRenderer());
        
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
                    "description", filterString, true, false);
            SimpleStringFilter pointtypeFilter = new SimpleStringFilter(
                    "active", filterString, true, false);
            getContainer().addContainerFilter(
                    new Or(nameFilter, pointtypeFilter));
        }
    }

    @SuppressWarnings("unchecked")
	private BeanItemContainer<SupportEntity> getContainer() {
        return (BeanItemContainer<SupportEntity>) super.getContainerDataSource();
    }

    @Override
    public SupportEntity getSelectedRow() throws IllegalStateException {
        return (SupportEntity) super.getSelectedRow();
    }

    public void setRecords(Collection<SupportEntity> records) {
        getContainer().removeAllItems();
        getContainer().addAll(records);
    }

	public void refresh(SupportEntity rec) {
        // We avoid updating the whole table through the backend here so we can
        // get a partial update for the grid
        BeanItem<SupportEntity> item = getContainer().getItem(rec);
        if (item != null) {
            // Updated record
        	item.setBean(rec);
        	System.out.println("REFRESH="+rec.getID()+" "+rec.getComments());
            //MethodProperty p = (MethodProperty) item.getItemProperty("ID");
            //p.fireValueChange();
        } else {
            // New record
            getContainer().addBean(rec);
        }
    }

    public void remove(SupportEntity rec) {
        getContainer().removeItem(rec);
    }
}
