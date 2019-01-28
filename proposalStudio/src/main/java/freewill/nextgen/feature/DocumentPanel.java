package freewill.nextgen.feature;

import java.util.ArrayList;
import java.util.List;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Responsive;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.data.FeatureEntity;
import freewill.nextgen.data.Style.StyleEnum;
import freewill.nextgen.feature.DocumentPanelItem.SelectItemListener;

/**
 * A view for performing create-read-update-delete operations on records.
 */
@SuppressWarnings("serial")
public class DocumentPanel extends Panel implements View {

    VerticalLayout barAndGridLayout = null;
    VerticalLayout grid = null;
    private int H1 = 1;
	private int H2 = 1;
	private int H3 = 1;
	private int H4 = 1;
	private int H5 = 1;
	private int figure = 1;
	private int currpage = 1;
    private int lastpage = 1;
    private int pagesize = 20;
    private List<FeatureEntity> innerRecs = null;
    private String searchText = "";
    private boolean showMenu = true;
    private boolean showDebug = false;
    private FeatureEntity selectedRec = null;
    private List<SelectRecListener> listeners = new ArrayList<SelectRecListener>();
    private FeatureCrudLogic viewLogic = null;

    public DocumentPanel(FeatureCrudLogic logic, Long product, boolean menus, boolean debug) {
    	showMenu = menus;
    	showDebug = debug;
    	viewLogic = logic;
    	
    	this.setStyleName(ValoTheme.LAYOUT_CARD);
        setSizeFull();
        barAndGridLayout = new VerticalLayout();
        barAndGridLayout.setSizeFull();
        barAndGridLayout.setMargin(true);
        barAndGridLayout.setSpacing(false);
        barAndGridLayout.addStyleName("dashboard-view");
        setContent(barAndGridLayout);
        Responsive.makeResponsive(barAndGridLayout);
        
        grid = createGrid(product);
        drawGrid(currpage);
        
        barAndGridLayout.addComponent(grid);
        barAndGridLayout.setExpandRatio(grid, 1);
    }

    @Override
    public void enter(ViewChangeEvent event) {
    	//
    }
   
    private VerticalLayout createGrid(Long product) {
    	VerticalLayout layout = new VerticalLayout();
    	layout.setWidth("100%");
        layout.setSpacing(false);
        layout.setMargin(true); // false
        lastpage = 1;
        currpage = 1;
        
        if(product==null || product==0L) return layout;
        
        innerRecs = (List<FeatureEntity>) viewLogic.getFeaturesByProduct(product);
        lastpage = 1 + innerRecs.size() / pagesize;
        currpage = 1;
        
        // This function cannot call directly to drawGrid() as grid variable is not set yet
        // So first refresh is done in constructor DocumentPanel()
        
        return layout;
    }
    
    public void newGridItem(FeatureEntity rec, DocumentPanelItem parent) {
        if(parent!=null){
        	String idx = "";
        	if(rec.getLevel().ordinal()<=StyleEnum.H8.ordinal())
        		idx = parent.getIdx()+"X.";
        	else if(rec.getLevel()==StyleEnum.NORMAL)
               	idx = "";
            else if(rec.getLevel()==StyleEnum.PARAGRAM)
               	idx = "•";
            else if(rec.getLevel()==StyleEnum.FIGURE)
               	idx = "Figure "+figure++;
            else 
               	idx = "";
        	
        	int position = grid.getComponentIndex(parent)+1; // Default position
        	if(grid.getComponentCount()==0)
        		position = 0;
        	else 
        		//if(rec.getLevel().ordinal()<=StyleEnum.H8.ordinal())
        		position = getEndPosition(parent); // The new item goes to the end 
        	//else
        	//	position = getNextPosition(parent); // For new non-title items
        	
        	if(position>0)
        		grid.addComponent(new DocumentPanelItem(viewLogic, idx, rec, true, this, showMenu, showDebug), position);
        	else{
        		grid.addComponent(new DocumentPanelItem(viewLogic, idx, rec, true, this, showMenu, showDebug));
        		this.lastPage();
        	}
        }
        else{
        	// We assume that Level is H1
    	    String idx = ""+(H1++)+". ";
    	    H2 = 1;
    	    H3 = 1;
    	    H4 = 1;
    	    H5 = 1;
    	    figure = 1;
        	grid.addComponent(new DocumentPanelItem(viewLogic, idx, rec, true, this, showMenu, showDebug));
        }
    }
    
    public void removeGridItem(DocumentPanelItem panel){
    	grid.removeComponent(panel);
    }
    
    public int getCurrpage() {
		return currpage;
	}

	public int getLastpage() {
		return lastpage;
	}

	public int getPagesize() {
		return pagesize;
	}

	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}
	
	private void drawGrid(int page) {
        int first = (page-1)*pagesize; // 1->(1-1)*10=0, 2->(2-1)*10=10, 3->(3-1)*10=20 
        int last = first + pagesize;   // 1->0+10=10,    2->10+10=20,    3->20+10=30
        grid.removeAllComponents();
        int i = 0;
        H1 = 1;
        H2 = 1;
	    H3 = 1;
	    H4 = 1;
	    H5 = 1;
	    figure = 1;
	    if(innerRecs==null) return;
	    System.out.println("Displaying Features from "+first+" to "+last);
	    
	    for(FeatureEntity rec : innerRecs){
            String idx="";
            if(rec.getLevel().ordinal()<=StyleEnum.H8.ordinal()){
            	// it is a title
            	int level = rec.getLevel().ordinal()+1;
	            if (level == 1){
	    	    	idx = ""+(H1++)+".";
	    	    	H2 = 1;
	    	    	H3 = 1;
	    	    	H4 = 1;
	    	    	H5 = 1;
	    	    }
	    	    else if(level == 2){
	    	    	idx = ""+(H1-1)+"."+(H2++)+".";
	    	    	H3 = 1;
	    	    	H4 = 1;
	    	    	H5 = 1;
	    	    }
	    	    else if(level == 3){
	    	    	idx = ""+(H1-1)+"."+(H2-1)+"."+(H3++)+".";
	    	    	H4 = 1;
	    	    	H5 = 1;
	    	    }
	    	    else if(level == 4){
	    	    	idx = ""+(H1-1)+"."+(H2-1)+"."+(H3-1)+"."+(H4++)+".";
	    	    	H5 = 1;
	    	    }
	    	    else{
	    	    	idx = ""+(H1-1)+"."+(H2-1)+"."+(H3-1)+"."+(H4-1)+"."+(H5++)+".";
	    	    }
            }
            else if(rec.getLevel()==StyleEnum.NORMAL)
            	idx = "";
            else if(rec.getLevel()==StyleEnum.PARAGRAM)
            	idx = "•";
            else if(rec.getLevel()==StyleEnum.FIGURE){
            	idx = "Figure "+figure++;
            	//System.out.println(idx);
            }
            else 
            	idx = "";
            
            if(first<=i && i<last){
            	//System.out.println("Panel With = " + rec.toString());
            	DocumentPanelItem item = new DocumentPanelItem(viewLogic, idx, rec, false, this, showMenu, showDebug);
            	grid.addComponent(item);
            	if(searchText.length()>0){
            		if(item.getinnerRec().getTitle().contains(searchText)
            			|| item.getinnerRec().getDescription().contains(searchText)){
            			item.addStyleName("backColorBlue");
            		}
    			}
            	item.addListener(new SelectItemListener(){
					@Override
					public void selectItemListener() {
						System.out.println("DocumentPanel - Button clicked = "+item.getinnerRec().getID());
						selectedRec = item.getinnerRec();
						selectItem();
					}
            	});
            }
            i++;
        }
    }
    
	public void nextPage(){
		if(currpage>=lastpage)
			return;
		currpage++;
		drawGrid(currpage);
	}
	
	public void prevPage(){
		if(currpage<=1)
			return;
		currpage--;
		drawGrid(currpage);
	}
	
	public void setPage(int page){
		if(page<1)
			return;
		if(page>lastpage)
			return;
		currpage=page;
		drawGrid(currpage);
	}
	
	public void firstPage(){
		currpage=1;
		drawGrid(currpage);
	}
	
	public void lastPage(){
		currpage=lastpage;
		drawGrid(currpage);
	}

	public void findFeature(String text) {
		searchText = text;
		if(text.length()>0){
			for(FeatureEntity rec:innerRecs){
				if(rec.getDescription().contains(text) || rec.getTitle().contains(text)){
					int i = innerRecs.indexOf(rec);
					currpage = 1 + i / pagesize;
					break;
				}
			}
		}
		drawGrid(currpage);
	}
	
	public interface SelectRecListener {
        void selectRecListener();
    }
    
    public void addListener(SelectRecListener toAdd) {
        listeners.add(toAdd);
        System.out.println("DocumentPanel - Added new Listener "+listeners.size());
    }
    
    public void selectItem() {
        // Notify everybody that may be interested.
    	System.out.println("DocumentPanel - Procesing Listeners..."+listeners.size());
        for (SelectRecListener hl : listeners){
        	System.out.println("DocumentPanel - Executing Listener "+hl.toString());
        	hl.selectRecListener();
        }
    }
	
    public FeatureEntity getSelectedRec(){
    	return selectedRec;
    }
    
    private int getEndPosition(DocumentPanelItem parent){
    	int i = grid.getComponentIndex(parent)+1;
    	FeatureEntity rec = parent.getinnerRec();
    	System.out.println("En Position for Parent ParentId="+rec.getParent());
    	
    	while(i<grid.getComponentCount()){
    		DocumentPanelItem item = (DocumentPanelItem) grid.getComponent(i);
    		System.out.println("Item Id="+item.getinnerRec().getID());
    		System.out.println("Item Parent="+item.getinnerRec().getParent());
    		
    		if(item.getinnerRec().getParent() <= rec.getParent())
    			return i;
    		
    		i++;
    	}
    	return i;
    }
    
    /*private int getNextPosition(DocumentPanelItem parent){
    	int i = grid.getComponentIndex(parent)+1;
    	FeatureEntity rec = parent.getinnerRec();
    	System.out.println("Next Position for Parent Id="+rec.getID());
    	
    	while(i<grid.getComponentCount()){
    		DocumentPanelItem item = (DocumentPanelItem) grid.getComponent(i);
    		System.out.println("Item Id="+item.getinnerRec().getID());
    		System.out.println("Item Parent="+item.getinnerRec().getParent());
    		
    		if(item.getinnerRec().getParent() != rec.getID())
    			return i;
    		
    		i++;
    	}
    	return i;
    }*/
    
}
