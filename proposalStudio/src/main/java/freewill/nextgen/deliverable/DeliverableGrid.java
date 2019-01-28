package freewill.nextgen.deliverable;

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
import freewill.nextgen.data.DeliverableEntity;
import freewill.nextgen.hmi.utils.Messages;
import freewill.nextgen.proposalStudio.EntryPoint;

/**
 * Grid of records, handling the visual presentation and filtering of a set of
 * items. This version uses an in-memory data source that is suitable for small
 * data sets.
 */
@SuppressWarnings("serial")
public class DeliverableGrid extends Grid {

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
    
    /*private StringToLongConverter prjConverter = new StringToLongConverter() {
        @Override
        public String convertToPresentation(Long point,
                java.lang.Class<? extends String> targetType, Locale locale)
                throws Converter.ConversionException {
            //String text = super.convertToPresentation(point, targetType, locale);

            String prjName = "Global";
            if(point!=null && point!=0){
            	try {
					ProjectEntity rec = (ProjectEntity) BltClient.get().getEntityById(""+point, 
						ProjectEntity.class,
						EntryPoint.get().getAccessControl().getTokenKey());
					if(rec!=null)
			            prjName = rec.getName();
				} catch (Exception e) {
					e.printStackTrace();
				}
            }
            return prjName;
        };
    };*/
    
    public DeliverableGrid() {
        setSizeFull();

        setSelectionMode(SelectionMode.SINGLE);

        BeanItemContainer<DeliverableEntity> container = 
        	new BeanItemContainer<DeliverableEntity>(DeliverableEntity.class);
        setContainerDataSource(container);
        if (EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.SUPER))
        	setColumns("ID", "name", "description", "resolved", /*"project",*/ "company"); //, "timestamp");
        else
        	setColumns("ID", "name", "description", "resolved" /*",project"*/); //, "timestamp");
        
        // Add an traffic light icon in front of pointType
        getColumn("resolved").setConverter(pointtypeConverter).setRenderer(new HtmlRenderer());
        //getColumn("project").setConverter(prjConverter).setRenderer(new HtmlRenderer());
        getColumn("description").setMinimumWidth(300);
        getColumn("ID").setMaximumWidth(80);
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

    public void toggleSelectionMode(){
    	SelectionModel selectionModel = this.getSelectionModel();
    	if (selectionModel instanceof SelectionModel.Single)
    		setSelectionMode(SelectionMode.MULTI);
    	else
    		setSelectionMode(SelectionMode.SINGLE);
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
                    "resolved", filterString, true, false);
            SimpleStringFilter descFilter = new SimpleStringFilter(
                    "description", filterString, true, false);
            getContainer().addContainerFilter(
                    new Or(nameFilter, pointtypeFilter, descFilter));
        }
    }

    public void setFilter(Long filterString) {
        getContainer().removeAllContainerFilters();
        if (filterString!=null) {
            SimpleStringFilter projFilter = new SimpleStringFilter(
                    "project", filterString.toString(), true, false);
            getContainer().addContainerFilter(
                    new Or(projFilter));
        }
    }
    
    @SuppressWarnings("unchecked")
	private BeanItemContainer<DeliverableEntity> getContainer() {
        return (BeanItemContainer<DeliverableEntity>) super.getContainerDataSource();
    }

    @Override
    public DeliverableEntity getSelectedRow() throws IllegalStateException {
    	return (DeliverableEntity) super.getSelectedRow();
    }

    public void setRecords(Collection<DeliverableEntity> records) {
        getContainer().removeAllItems();
        if(records!=null)
        	getContainer().addAll(records);
    }

	public void refresh(DeliverableEntity rec) {
        // We avoid updating the whole table through the backend here so we can
        // get a partial update for the grid
        BeanItem<DeliverableEntity> item = getContainer().getItem(rec);
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

    public void remove(DeliverableEntity rec) {
        getContainer().removeItem(rec);
    }
}
