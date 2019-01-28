package freewill.nextgen.config;

import java.util.Collection;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.ui.Grid;

import freewill.nextgen.data.ConfigEntity;
import freewill.nextgen.hmi.utils.Messages;

/**
 * Grid of records, handling the visual presentation and filtering of a set of
 * items. This version uses an in-memory data source that is suitable for small
 * data sets.
 */
@SuppressWarnings("serial")
public class ConfigGrid extends Grid {

    public ConfigGrid() {
        setSizeFull();

        setSelectionMode(SelectionMode.SINGLE);

        BeanItemContainer<ConfigEntity> container = 
        	new BeanItemContainer<ConfigEntity>(ConfigEntity.class);
        setContainerDataSource(container);
        setColumns("ID", "name", "value");
        getColumn("ID").setMaximumWidth(80);
        
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
            getContainer().addContainerFilter(
                    new Or(nameFilter));
        }
    }
    
    @SuppressWarnings("unchecked")
	private BeanItemContainer<ConfigEntity> getContainer() {
        return (BeanItemContainer<ConfigEntity>) super.getContainerDataSource();
    }

    @Override
    public ConfigEntity getSelectedRow() throws IllegalStateException {
        return (ConfigEntity) super.getSelectedRow();
    }

    public void setRecords(Collection<ConfigEntity> records) {
        getContainer().removeAllItems();
        if(records!=null)
        	getContainer().addAll(records);
    }

	public void refresh(ConfigEntity rec) {
        // We avoid updating the whole table through the backend here so we can
        // get a partial update for the grid
        BeanItem<ConfigEntity> item = getContainer().getItem(rec);
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

    public void remove(ConfigEntity rec) {
        getContainer().removeItem(rec);
    }
}
