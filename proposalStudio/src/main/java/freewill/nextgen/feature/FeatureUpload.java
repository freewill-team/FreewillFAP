package freewill.nextgen.feature;

import java.io.File;
import java.util.Collection;
import java.util.List;
import freewill.nextgen.data.Style;
import freewill.nextgen.data.Style.StyleEnum;
import freewill.nextgen.hmi.common.GenericCombo;
import freewill.nextgen.proposalStudio.EntryPoint;
import freewill.nextgen.data.FeatureEntity;
import freewill.nextgen.data.ProductEntity;

import com.vaadin.addon.contextmenu.ContextMenu;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Simple name editor Window.
 */
@SuppressWarnings("serial")
public class FeatureUpload extends Window {

    private FeatureGrid grid = new FeatureGrid();
    private StyleGrid grid2 = new StyleGrid();
    DocumentAdvView view = null;
    List<FeatureEntity> recList = null;
    List<Style> styleList = null;
    Object product = null;
    private ContextMenu menu2 = null;
    private File tempFile = null;
    private TabSheet ts = null;
    private FeatureCrudLogic viewLogic = null;

    public FeatureUpload(List<Style> styles, List<FeatureEntity> recs, 
    		Object prd, File file, DocumentAdvView parentview, FeatureCrudLogic logic) {
    	viewLogic = logic;
    	view = parentview;
    	recList = recs;
    	styleList = styles;
    	product = prd;
    	tempFile = file;
        setCaption("Upload Features");
        setModal(true);
        setClosable(false);
        setResizable(false);
        setWidth(800.0f, Unit.PIXELS);

        addStyleName("edit-dashboard");

        setContent(buildContent());
    }

    private Component buildContent() {
        VerticalLayout result = new VerticalLayout();
        result.setMargin(true);
        result.setSpacing(true);
        ts = new TabSheet();
        ts.addStyleName("framed equal-width-tabs");
        
        if(styleList!=null){
	        grid2.setRecords(styleList);
	        ts.addTab(grid2, "Styles");
	        grid2.setSizeFull();
	        
	        menu2 = new ContextMenu(grid2, true);
	        menu2.addItem("Change To...", e -> {
	        	// Do nothing
	        	});
	        for(StyleEnum sty:StyleEnum.values()){
		        menu2.addItem(sty.toString(), e -> {
		        	Style style = grid2.getSelectedRow();
		        	Style styl2 = this.findStyle(style.getStyleid());
		        	styl2.setLevel(sty);
		        	style.setLevel(sty);
		        	grid2.refresh(style);
		        });
	        }
        }
        
        grid.setRecords(recList);
        grid.setSelectionMode(SelectionMode.MULTI);
        ts.addTab(grid, "Features");
        grid.setSizeFull();
        
        result.addComponent(ts);
        result.addComponent(buildFooter());
        return result;
    }

    private Component buildFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setSpacing(true);
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Unit.PERCENTAGE);
        
        Button reload = new Button("Reload File");
        reload.setIcon(FontAwesome.REFRESH);
        reload.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        reload.setVisible(styleList!=null);
        reload.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                recList = view.getFeatures(styleList);
                grid.setRecords(recList);
                ts.setSelectedTab(1);
            }
        });
        
        Label label = new Label("Select Product");
        GenericCombo<ProductEntity> productcb = new GenericCombo<ProductEntity>(ProductEntity.class);
        
        if(product!=null)
        	productcb.setValue(product);
        productcb.setEnabled(false);
        
        Button cancel = new Button("Cancel");
        cancel.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
            	tempFile.delete();
                close();
            }
        });
        cancel.setClickShortcut(KeyCode.ESCAPE, null);

        Button save = new Button("Save");
        save.addStyleName(ValoTheme.BUTTON_PRIMARY);
        save.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
            	boolean dbg = true;
                // saves selected records
            	FeatureEntity prevH1 = null;
            	FeatureEntity prevH2 = null;
            	FeatureEntity prevH3 = null;
            	FeatureEntity prevH4 = null;
            	FeatureEntity prevH5 = null;
            	FeatureEntity prevFt = null;
            	Collection<Object> docs = grid.getSelectedRows();
            	Long company = EntryPoint.get().getAccessControl().getUserEntity().getCompany();
            	for(Object obj : docs){
            		Long parent = 0L;
            		int level = 0;
            		FeatureEntity rec = (FeatureEntity)obj;
            		rec.setCompany(company);
            		if (productcb.getValue() != null) {
                      	ProductEntity product = (ProductEntity) productcb.getValue();
                      	Long prd = product.getID();
                      	rec.setProduct(prd);
            		}
            		if(rec.getLevel().ordinal()>StyleEnum.H8.ordinal()){
            			// it is not a title
            			//int newlevel = prevFt.getLevel().ordinal()+1;
            			//StyleEnum style = StyleEnum.values()[newlevel];
            			//rec.setLevel(style);
            			if(prevH1==null)
            	    		parent = 0L;
            	    	else
            	    		parent = prevFt.getID();
            		}
            		else{
            			// it is a title
            			level = rec.getLevel().ordinal()+1;
	            		if (level == 1){
	                    	parent = 0L;
	            	    }
	            	    else if(level == 2){
	            	    	if(prevH1==null)
	            	    		parent = 0L;
	            	    	else
	            	    		parent = prevH1.getID();
	            	    }
	            	    else if(level == 3){
	            	    	if(prevH2==null)
	            	    		parent = 0L;
	            	    	else
	            	    		parent = prevH2.getID();
	            	    }
	            	    else if(level == 4){
	            	    	if(prevH3==null)
	            	    		parent = 0L;
	            	    	else
	            	    		parent = prevH3.getID();
	            	    }
	            	    else if(level == 5){
	            	    	if(prevH4==null)
	            	    		parent = 0L;
	            	    	else
	            	    		parent = prevH4.getID();
	            	    }
	            	    else{
	            	    	if(prevH5==null)
	            	    		parent = 0L;
	            	    	else
	            	    		parent = prevH5.getID();
	            	    }
            		}
            		
            		rec.setParent(parent);
            		FeatureEntity savedRec = viewLogic.saveRecord(rec);
            		if(dbg) System.out.println("\nSaved = "+savedRec.toString()+"\n");
            		
                    if (level == 1){
                    	prevH1 = savedRec;
                    	prevFt = savedRec;
            	    }
            	    else if(level == 2){
            	    	prevH2 = savedRec;
            	    	prevFt = savedRec;
            	    }
            	    else if(level == 3){
            	    	prevH3 = savedRec;
            	    	prevFt = savedRec;
            	    }
            	    else if(level == 4){
            	    	prevH4 = savedRec;
            	    	prevFt = savedRec;
            	    }
            	    else if(level == 6){
            	    	prevH5 = savedRec;
            	    	prevFt = savedRec;
            	    }
            	}
            	view.refreshGrid();
            	tempFile.delete();
                close();
            }
        });
        save.setClickShortcut(KeyCode.ENTER, null);

        footer.addComponents(reload, label, productcb, cancel, save);
        footer.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
        footer.setExpandRatio(label, 1);
        return footer;
    }

    private Style findStyle(String style){
		for(Style rec : styleList){
			if(rec.getStyleid().equals(style))
				return rec;
		}
		return null;
	}
    
}
