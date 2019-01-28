package freewill.nextgen.file;

import java.util.Collection;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.ui.Grid;

import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.FileEntity;
import freewill.nextgen.hmi.utils.Messages;
import freewill.nextgen.proposalStudio.EntryPoint;

/**
 * Grid of records, handling the visual presentation and filtering of a set of
 * items. This version uses an in-memory data source that is suitable for small
 * data sets.
 */
@SuppressWarnings("serial")
public class FileGrid extends Grid {

    /*private StringToBooleanConverter pointtypeConverter = new StringToBooleanConverter() {
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

            return iconCode; //+ " " + text;
        };
    };*/
    
    /*private StringToLongConverter projectConverter = new StringToLongConverter() {
        @Override
        public String convertToPresentation(Long point,
                java.lang.Class<? extends String> targetType, Locale locale)
                throws Converter.ConversionException {
            //String text = super.convertToPresentation(point, targetType, locale);

            String prjName = "Undefined";
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

    public FileGrid() {
        setSizeFull();

        setSelectionMode(SelectionMode.SINGLE);

        BeanItemContainer<FileEntity> container = 
        	new BeanItemContainer<FileEntity>(FileEntity.class);
        setContainerDataSource(container);
        if (EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.SUPER))
        	setColumns("ID", "name", "description", /*"project",*/ "company", "timestamp");
        else
        	setColumns("ID", "name", "description", /*"project",*/ "timestamp");
        
        // Add an traffic light icon in front of pointType
        //getColumn("project").setConverter(projectConverter).setRenderer(new HtmlRenderer());
        //getColumn("project").setHeaderCaption("Project");
        getColumn("ID").setMaximumWidth(80);
        getColumn("description").setMinimumWidth(300);
        
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
            SimpleStringFilter descFilter = new SimpleStringFilter(
                    "description", filterString, true, false);
            getContainer().addContainerFilter(
                    new Or(nameFilter, descFilter));
        }
    }
    
    @SuppressWarnings("unchecked")
	private BeanItemContainer<FileEntity> getContainer() {
        return (BeanItemContainer<FileEntity>) super.getContainerDataSource();
    }

    @Override
    public FileEntity getSelectedRow() throws IllegalStateException {
        return (FileEntity) super.getSelectedRow();
    }

    public void setRecords(Collection<FileEntity> records) {
        getContainer().removeAllItems();
        if(records!=null)
        	getContainer().addAll(records);
    }

	public void refresh(FileEntity rec) {
        // We avoid updating the whole table through the backend here so we can
        // get a partial update for the grid
        BeanItem<FileEntity> item = getContainer().getItem(rec);
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

    public void remove(FileEntity rec) {
        getContainer().removeItem(rec);
    }
}
