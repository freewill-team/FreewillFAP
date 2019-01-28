package freewill.nextgen.mapping;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.data.FeatureEntity;
import freewill.nextgen.data.ProductEntity;
import freewill.nextgen.feature.DocumentPanel;
import freewill.nextgen.feature.DocumentPanel.SelectRecListener;
import freewill.nextgen.feature.FeatureCrudLogic;
import freewill.nextgen.hmi.common.GenericCombo;
import freewill.nextgen.hmi.utils.Messages;

/**
 * A form for editing a single record.
 *
 * Using responsive layouts, the form can be displayed either sliding out on the
 * side of the view or filling the whole screen - see the theme for the related
 * CSS rules.
 */
@SuppressWarnings("serial")
public class SearchForm extends CssLayout {

    private MappingCrudLogic viewLogic;
    private GenericCombo<ProductEntity> productcb = null;
    private DocumentPanel grid = null;
    private VerticalLayout barAndGridLayout = null;
    private TextField page = null;
    private List<FeatureEntity> docs = new ArrayList<FeatureEntity>();
    private int docidx = 0;
    private TextField Doc = null;
    //private Long projectId = null;
    FeatureCrudLogic featLogic = new FeatureCrudLogic(null);

    public SearchForm(MappingCrudLogic sampleCrudLogic) {
        super();
        this.setStyleName("product-form-wrapper");
        this.addStyleName("product-form");
        viewLogic = sampleCrudLogic;
        
        grid = new DocumentPanel(featLogic, 0L, false, false);
        grid.addListener(new SelectRecListener(){
			@Override
			public void selectRecListener() {
				FeatureEntity rec = grid.getSelectedRec();
				System.out.println("SearchForm - Close and set Feature = "+rec.getID());
				viewLogic.selectFeature(rec);
				close();
			}
    	});
        
        HorizontalLayout searchLayout = createSearchBar();
        HorizontalLayout topLayout = createTopBar();
        
        barAndGridLayout = new VerticalLayout();
        barAndGridLayout.addComponent(searchLayout);
        barAndGridLayout.addComponent(topLayout);
        barAndGridLayout.addComponent(grid);
        barAndGridLayout.setMargin(false);
        barAndGridLayout.setSpacing(true);
        barAndGridLayout.setSizeFull();
        barAndGridLayout.setExpandRatio(grid, 1);
        barAndGridLayout.setStyleName("crud-main-layout");

        addComponent(barAndGridLayout);
    }
    
    public HorizontalLayout createSearchBar() {
    	
    	TextField search = new TextField();
        search.setStyleName("filter-textfield");
        search.setInputPrompt("Search...");
         
        search.setImmediate(true);
        search.addTextChangeListener(new FieldEvents.TextChangeListener() {
            @Override
            public void textChange(FieldEvents.TextChangeEvent event) {
            	if(event.getText().equals("")) return;
                // Find first occurence
            	grid.findFeature(event.getText());
                page.setValue(grid.getCurrpage()+"/"+grid.getLastpage());
                // Also show all found occurences
                if (productcb.getValue() != null) {
                	ProductEntity product = (ProductEntity) productcb.getValue();
                	Long prd = product.getID();
	                List<FeatureEntity> recs = (List<FeatureEntity>) 
	                		featLogic.getByProductFiltered(event.getText(), prd);
	                docs.clear();
	                //System.out.println("Product="+prd);
	                for(FeatureEntity rec:recs){
	                	//System.out.println("Rec Product="+rec.toString());
	                	long recprd = rec.getProduct();
	                	if(recprd==prd){
	                		docs.add(rec);
	                		//System.out.println("Added");
	                	}
	                }
	            }
            	setDocumentInfo(docidx=0);
            }
        });
    	
    	Button close = new Button(Messages.get().getKey("cancel"));
        close.addStyleName(ValoTheme.BUTTON_PRIMARY);
        //close.setIcon(FontAwesome.FAST_FORWARD);
        close.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                viewLogic.closeSearchForm();
            }
        });
        
        Label expander = new Label("");
        
        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setSpacing(true);
        topLayout.setMargin(false);
        topLayout.setWidth("100%");
        topLayout.addComponent(search);
        topLayout.addComponent(navButtons());
        topLayout.addComponent(expander);
        topLayout.addComponent(close);
        topLayout.setExpandRatio(expander, 1L);
        topLayout.setStyleName("top-bar");
        return topLayout;
        
    }
    
	public HorizontalLayout createTopBar() {
        
		productcb = new GenericCombo<ProductEntity>(ProductEntity.class);
        
        productcb.addValueChangeListener(new ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                if (productcb.getValue() != null) {
                	barAndGridLayout.removeComponent(grid);
                    ProductEntity rec = (ProductEntity) productcb.getValue();
                	Long prd = rec.getID();
                	grid = new DocumentPanel(featLogic, prd, false, false);
                	grid.addListener(new SelectRecListener(){
             			@Override
             			public void selectRecListener() {
             				FeatureEntity rec = grid.getSelectedRec();
             				//System.out.println("SearchForm - Close and set Feature = "+rec.getID());
             				viewLogic.selectFeature(rec);
             				close();
             			}
                 	});
                	barAndGridLayout.addComponent(grid);
                	barAndGridLayout.setExpandRatio(grid, 1);
                	page.setValue(grid.getCurrpage()+"/"+grid.getLastpage());
                }
            }
        });
        
        Label expander = new Label("");
        
        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setSpacing(true);
        topLayout.setMargin(false);
        topLayout.setWidth("100%");
        topLayout.addComponent(pageButtons());
        topLayout.addComponent(expander);
        topLayout.addComponent(productcb);
        topLayout.setExpandRatio(expander, 1L);
        topLayout.setStyleName("top-bar");
        return topLayout;
    }
    
	public void enter(Long project) {
		//projectId = project;
		this.RefreshWithProject(project); // solo debe mostrar productos Global y los asociados al proyecto
		
	    barAndGridLayout.removeComponent(grid);
	    ProductEntity rec = (ProductEntity) productcb.getValue();
    	Long prd = rec.getID();
	    grid = new DocumentPanel(featLogic, prd, false, false);
	    grid.addListener(new SelectRecListener(){
			@Override
			public void selectRecListener() {
				FeatureEntity rec = grid.getSelectedRec();
				System.out.println("SearchForm - Close and set Feature = "+rec.getID());
				viewLogic.selectFeature(rec);
				close();
			}
    	});
	    barAndGridLayout.addComponent(grid);
	    barAndGridLayout.setExpandRatio(grid, 1);
	    addStyleName("visible");
		setEnabled(true);
		page.setValue(grid.getCurrpage()+"/"+grid.getLastpage());
		System.out.println("Entrando en SearchForm...");
	}
	
	private void RefreshWithProject(Long project) {
		// removes not required products
		productcb.Refresh();
		for(Object obj:productcb.getContainerDataSource().getItemIds()){
			ProductEntity rec = (ProductEntity) obj;
			if(rec.getProject()!=null && rec.getProject()!=0 && rec.getProject()!=project)
				productcb.getContainerDataSource().removeItem(obj);
		}
	}

	public void close(){
		removeStyleName("visible");
		setEnabled(false);
	}
	
	private void setDocumentInfo(int idx){
    	if(docs==null){
    		Doc.setValue("0/0");
    		return;
    	}
    	if(docs.size()>0 && idx>0){
    		if(idx<0) idx=0;
    		if(idx>=docs.size()) idx=docs.size()-1;
    		docidx=idx;
    		Doc.setValue((docidx+1)+"/"+docs.size());
    		grid.findFeature(docs.get(docidx).getDescription());
    		page.setValue(grid.getCurrpage()+"/"+grid.getLastpage());
    	}
    	else{
    		docidx=0;
    		Doc.setValue((docidx+1)+"/"+docs.size());
    	}
    }
	
	private HorizontalLayout navButtons(){
	    HorizontalLayout nav = new HorizontalLayout();
	    Button prev = new Button("<");
	 	prev.setIcon(FontAwesome.CARET_LEFT);
	 	prev.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
	 	prev.addStyleName(ValoTheme.BUTTON_SMALL);
	 	Doc = new TextField();
	 	Doc.setEnabled(false);
	 	Doc.setWidth("60px");
	 	Button next = new Button(">");
	 	next.setIcon(FontAwesome.CARET_RIGHT);
	 	next.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
	 	next.addStyleName(ValoTheme.BUTTON_SMALL);
	 	nav.addComponent(prev);
	 	nav.addComponent(Doc);
	 	nav.addComponent(next);
	 		
	 	next.addClickListener(new ClickListener() {
	 		@Override
	        public void buttonClick(ClickEvent event) {
	            docidx++;
	            setDocumentInfo(docidx);
	 		}
	 	});
	        
	    prev.addClickListener(new ClickListener() {
	        @Override
	        public void buttonClick(ClickEvent event) {
	            docidx--;
	            setDocumentInfo(docidx);
	        }
	    });
	 		
	    return nav;
	}
	
	private HorizontalLayout pageButtons() {
    	HorizontalLayout nav = new HorizontalLayout();
    	Button prev = new Button("<");
		prev.setIcon(FontAwesome.CARET_LEFT);
		prev.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
		prev.addStyleName(ValoTheme.BUTTON_SMALL);
		page = new TextField();
		//page.setEnabled(false);
		page.setWidth("80px");
		Button next = new Button(">");
		next.setIcon(FontAwesome.CARET_RIGHT);
		next.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
		next.addStyleName(ValoTheme.BUTTON_SMALL);
		nav.addComponent(prev);
        nav.addComponent(page);
        nav.addComponent(next);
		
		prev.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                grid.prevPage();
                page.setValue(grid.getCurrpage()+"/"+grid.getLastpage());
            }
        });
		
		next.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                grid.nextPage();
                page.setValue(grid.getCurrpage()+"/"+grid.getLastpage());
            }
        });
		
		page.addTextChangeListener(new FieldEvents.TextChangeListener() {
	        @Override
	        public void textChange(FieldEvents.TextChangeEvent event) {
	        	try{
	        		int pag = Integer.parseInt(event.getText());
		            grid.setPage(pag);
		            page.setValue(grid.getCurrpage()+"/"+grid.getLastpage());
	        	}
	        	catch(Exception e) {
	        		// do nothing if Integer.parseInt() fails
	        	}
	        }
	    });
        
    	return nav;
    }
	
}
