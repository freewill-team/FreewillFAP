package freewill.nextgen.requirement;

import java.util.Collection;
import java.util.Locale;

import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.converter.StringToBooleanConverter;
//import com.vaadin.data.util.converter.StringToLongConverter;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Grid;
import com.vaadin.ui.renderers.HtmlRenderer;

import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.Requirement2Entity;
import freewill.nextgen.hmi.utils.Messages;
import freewill.nextgen.proposalStudio.EntryPoint;

/**
 * Grid of records, handling the visual presentation and filtering of a set of
 * items. This version uses an in-memory data source that is suitable for small
 * data sets.
 */
@SuppressWarnings("serial")
public class RequirementGrid extends Grid {

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
    
    /*private StringToLongConverter userConverter = new StringToLongConverter() {
        @Override
        public String convertToPresentation(Long point,
                java.lang.Class<? extends String> targetType, Locale locale)
                throws Converter.ConversionException {
            //String text = super.convertToPresentation(point, targetType, locale);

            String userName = "Unassigned";
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
    };*/

    public RequirementGrid() {
        setSizeFull();

        setSelectionMode(SelectionMode.SINGLE);

        BeanItemContainer<Requirement2Entity> container = 
        	new BeanItemContainer<Requirement2Entity>(Requirement2Entity.class);
        setContainerDataSource(container);
        if (EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.SUPER))
        	setColumns("id", "customid", "description", "resolved", "user", "company"); //assignedto "project", "timestamp");
        else
        	setColumns("id", "customid", "description", "resolved", "user"); //assignedto "project", "timestamp");
        
        // Add an traffic light icon in front of pointType
        getColumn("resolved").setConverter(pointtypeConverter).setRenderer(new HtmlRenderer());
        //getColumn("assignedto").setConverter(userConverter).setRenderer(new HtmlRenderer());
        getColumn("id").setMaximumWidth(80);
        getColumn("resolved").setMaximumWidth(100);
        getColumn("description").setMinimumWidth(300);
        getColumn("description").setMaximumWidth(550);
        getColumn("customid").setMaximumWidth(140);
        
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
                    "customid", filterString, true, false);
            SimpleStringFilter pointtypeFilter = new SimpleStringFilter(
                    "resolved", filterString, true, false);
            SimpleStringFilter descFilter = new SimpleStringFilter(
                    "description", filterString, true, false);
            SimpleStringFilter userFilter = new SimpleStringFilter(
                    "user", filterString, true, false);
            getContainer().addContainerFilter(
                    new Or(nameFilter, pointtypeFilter, userFilter, descFilter));
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
	private BeanItemContainer<Requirement2Entity> getContainer() {
        return (BeanItemContainer<Requirement2Entity>) super.getContainerDataSource();
    }

    @Override
    public Requirement2Entity getSelectedRow() throws IllegalStateException {
    	return (Requirement2Entity) super.getSelectedRow();
    }

    public void setRecords(Collection<Requirement2Entity> records) {
        getContainer().removeAllItems();
        if(records!=null)
        	getContainer().addAll(records);
    }

	public void refresh(Requirement2Entity rec) {
        // We avoid updating the whole table through the backend here so we can
        // get a partial update for the grid
        BeanItem<Requirement2Entity> item = getContainer().getItem(rec);
        if (item != null) {
            // Updated record
        	item.setBean(rec);
            //MethodProperty p = (MethodProperty) item.getItemProperty("id");
            //p.fireValueChange();
        } else {
            // New record
            getContainer().addBean(rec);
        }
    }

    public void remove(Requirement2Entity rec) {
        getContainer().removeItem(rec);
    }
}
