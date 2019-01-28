package freewill.nextgen.mail;

import java.util.Collection;
import java.util.Locale;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.*;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.HtmlRenderer;

import freewill.nextgen.common.entities.MailServerEntity;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.hmi.utils.Messages;
import freewill.nextgen.proposalStudio.EntryPoint;

/**
 * Grid of records, handling the visual presentation and filtering of a set of
 * items. This version uses an in-memory data source that is suitable for small
 * data sets.
 */
@SuppressWarnings("serial")
public class MailServerGrid extends Grid {

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

    public MailServerGrid() {
        setSizeFull();

        setSelectionMode(SelectionMode.SINGLE);

        BeanItemContainer<MailServerEntity> container = 
        	new BeanItemContainer<MailServerEntity>(MailServerEntity.class);
        setContainerDataSource(container);
        if (EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.SUPER))
        	setColumns("id", "label", "description", "active", "hostname", "port", "company");
        else
        	setColumns("id", "label", "description", "active", "hostname", "port");

        // Add an traffic light icon in front of pointType
        getColumn("active").setConverter(pointtypeConverter).setRenderer(new HtmlRenderer());
        getColumn("id").setWidth(60);
        //getColumn("expiredDate").setWidth(180);
        
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
	private BeanItemContainer<MailServerEntity> getContainer() {
        return (BeanItemContainer<MailServerEntity>) super.getContainerDataSource();
    }

    @Override
    public MailServerEntity getSelectedRow() throws IllegalStateException {
        return (MailServerEntity) super.getSelectedRow();
    }

    public void setRecords(Collection<MailServerEntity> records) {
        getContainer().removeAllItems();
        getContainer().addAll(records);
    }

	public void refresh(MailServerEntity rec) {
        // We avoid updating the whole table through the backend here so we can
        // get a partial update for the grid
        BeanItem<MailServerEntity> item = getContainer().getItem(rec);
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

    public void remove(MailServerEntity rec) {
        getContainer().removeItem(rec);
    }
}
