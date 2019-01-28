package freewill.nextgen.feature;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.hmi.utils.Messages;

import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

/**
 * A form for editing a single record.
 *
 * Using responsive layouts, the form can be displayed either sliding out on the
 * side of the view or filling the whole screen - see the theme for the related
 * CSS rules.
 */
@SuppressWarnings("serial")
public class ContentTableForm extends CssLayout {

    private Tree grid = null;
    @SuppressWarnings("unused")
	private DocumentAdvView view = null;
    private FeatureCrudLogic viewLogic = null;

    public ContentTableForm(DocumentAdvView view, FeatureCrudLogic logic) {
    	this.view = view;
    	viewLogic = logic;
    	
        this.setStyleName("product-form-wrapper");
        this.addStyleName("product-form");
        this.addStyleName("reduced-width");
        
        Label title = new Label(Messages.get().getKey("tableofcontents"));
        title.setStyleName(ValoTheme.LABEL_COLORED);
        title.addStyleName(ValoTheme.LABEL_H2);
        grid = new Tree();
        
        VerticalLayout barAndGridLayout = new VerticalLayout();
        barAndGridLayout.addComponent(title);
        barAndGridLayout.addComponent(grid);
        //barAndGridLayout.setMargin(true);  
        //barAndGridLayout.setSpacing(true);
        barAndGridLayout.setSizeFull();
        barAndGridLayout.setExpandRatio(grid, 1);
        barAndGridLayout.setStyleName("crud-main-layout");

        addComponent(barAndGridLayout);
        
        grid.addItemClickListener(new ItemClickEvent.ItemClickListener() {
        	@Override
			public void itemClick(ItemClickEvent event) {
				Item obj = event.getItem();
        		if(obj!=null){
        			String filter = (String) obj.getItemProperty("Title").getValue();
        			view.setFilter(filter);
        			close();
        		}
			}
        });
        
    }
    
	public void enter(Long product) {
		addStyleName("visible");
		setEnabled(true);
		if(product!=null){
			Container records = viewLogic.getFeaturesContainer(product);
			grid.setContainerDataSource(records);
			grid.setItemCaptionPropertyId("Title");
		}
	}
	
	public void close(){
		removeStyleName("visible");
		setEnabled(false);
	}
	
}
