package freewill.nextgen.dashboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Notification;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.common.entities.KpiValue;
import freewill.nextgen.data.FeatureEntity;
import freewill.nextgen.data.MappingEntity;
import freewill.nextgen.data.ProductEntity;
import freewill.nextgen.data.ProjectEntity;
import freewill.nextgen.data.ProjectEntity.ProjectStatusEnum;
import freewill.nextgen.data.RequirementEntity;
import freewill.nextgen.hmi.utils.Messages;

@SuppressWarnings("serial")
public final class DashboardView extends Panel implements View {

    public final String VIEW_NAME = Messages.get().getKey("dashboardview.viewname");

    private DashboardLogic viewLogic = new DashboardLogic(this);
    private Label titleLabel;
    private CssLayout dashboardPanels;
    private final VerticalLayout root;
    private Long project = null;
    private Component chart = null;
    private Component piechart = null;
    private Component barchart = null;
    List<ProjectEntity> projects = null;

    public DashboardView() {
        addStyleName(ValoTheme.PANEL_BORDERLESS);
        setSizeFull();

        root = new VerticalLayout();
        root.setSizeFull();
        root.setMargin(true);
        root.setSpacing(true);
        root.addStyleName("dashboard-view");
        setContent(root);
        Responsive.makeResponsive(root);

        // All this will be done in enter()
        /*root.addComponent(buildHeader());
        root.addComponent(buildSparklines());
        Component content = buildContent();
        root.addComponent(content);
        root.setExpandRatio(content, 1);*/

        // All the open sub-windows should be closed whenever the root layout
        // gets clicked.
        root.addLayoutClickListener(new LayoutClickListener() {
            @Override
            public void layoutClick(final LayoutClickEvent event) {
                //DashboardEventBus.post(new CloseOpenWindowsEvent());
            }
        });
    }

    private Component buildSparklines() {
        CssLayout sparks = new CssLayout();
        sparks.addStyleName("sparks");
        sparks.setWidth("100%");
        Responsive.makeResponsive(sparks);

        SparklineJs s = new SparklineJs(Messages.get().getKey("product")+"s", 
        		Messages.get().getKey("active")+" vs No "+Messages.get().getKey("active"),
        		viewLogic.countActiveProducts(), 
        		viewLogic.countNoActiveProducts(),
        		FontAwesome.PAPERCLIP);
        sparks.addComponent(s);

        s = new SparklineJs(Messages.get().getKey("project")+"s",
        		Messages.get().getKey("active")+" vs "+Messages.get().getKey("closed"),
        		viewLogic.countActiveProjects(), 
        		viewLogic.countClosedProjects(),
        		FontAwesome.PRODUCT_HUNT);
        sparks.addComponent(s);

        s = new SparklineJs(Messages.get().getKey("requirement")+"s",
        		Messages.get().getKey("pending")+" vs "+Messages.get().getKey("resolved"),
        		//"Pending vs Resolved", 
        		viewLogic.countPendingRequirements(), 
        		viewLogic.countResolvedRequirements(),
        		FontAwesome.TICKET);
        sparks.addComponent(s);

        s = new SparklineJs(Messages.get().getKey("feature")+"s",
        		Messages.get().getKey("active")+" vs No "+Messages.get().getKey("active"),
        		//"Active vs No Active", 
        		viewLogic.countActiveFeatures(), 
        		viewLogic.countNoActiveFeatures(),
        		FontAwesome.DATABASE);
        sparks.addComponent(s);

        return sparks;
    }

    /*private Component buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);

        titleLabel = new Label(VIEW_NAME);
        titleLabel.setId(VIEW_NAME);
        titleLabel.setSizeUndefined();
        titleLabel.addStyleName(ValoTheme.LABEL_BOLD);
        titleLabel.addStyleName(ValoTheme.LABEL_COLORED);
        titleLabel.addStyleName(ValoTheme.LABEL_HUGE);
        titleLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(titleLabel);

        return header;
    }*/

    private Component buildContent() {
    	System.out.println("Filtering by Project #"+project);
    	
        dashboardPanels = new CssLayout();
        dashboardPanels.addStyleName("dashboard-panels");
        Responsive.makeResponsive(dashboardPanels);

        dashboardPanels.addComponent(builtProjectsByStatus());
        
        chart = builtPendingVsResolvedReqsByProject();
        dashboardPanels.addComponent(chart);
        
        piechart = builtRequirementsByStatusByProject();
        dashboardPanels.addComponent(piechart);
        
        barchart = builtDocumentsByAnswerByProject();
        dashboardPanels.addComponent(barchart);
        
        return dashboardPanels;
    }
    
    @SuppressWarnings("deprecation")
	private long TrimDate(Date t){
    	long tmp = t.getTime()/1000;
		tmp = tmp*1000; // removes milliseconds
		Date time = new Date(tmp);
		time.setHours(03);
		time.setMinutes(01);
		time.setSeconds(01);
    	return time.getTime();
    }
    
    private static class DataItem {
    	public int xv = 0;
    	public int yv = 0;
    	public DataItem(int x, int y) {
			xv = x;
			yv = y;
		}
    }
    
    private Component builtPendingVsResolvedReqsByProject() {	
    	ProjectEntity prj = viewLogic.getProjectById(project);
    	String title = Messages.get().getKey("created")+" vs "+
    	    	Messages.get().getKey("resolved")+" "+
    	    	Messages.get().getKey("project")+": "+prj.getName();
    	
    	HashMap<Long, DataItem> hmap = new HashMap<Long, DataItem>();
    	// First process created&resolved requirements by date
    	List<RequirementEntity> reqs = (List<RequirementEntity>) viewLogic.getRequirementsByProject(project);   
        for(RequirementEntity rec : reqs)
        {
           try 
           {
        	   Long time = TrimDate(rec.getCreated());
        	   //System.out.println(rec.getTimestamp()+">"+time);
        	   DataItem item = hmap.get(time);
        	   if(item==null){
        		   	// Add new value
        		   DataItem newitem = new DataItem(1,0);
        		   	hmap.put(time, newitem);
        	   }
        	   else{
        		   	// Update existing value
        		   	item.xv++;
        		   	hmap.replace(time, item);
        	   }
        	   if(rec.getResolved()){
       		   		time = TrimDate(rec.getTimestamp());
       		   		//System.out.println(rec.getTimestamp()+"="+time);
       		   		item = hmap.get(time);
       		   		if(item==null){
       		   			// Add new value
       		   			DataItem newitem = new DataItem(0,1);
       		   			hmap.put(time, newitem);
       		   		}
       		   		else{
       		   			// Update existing value
       		   			item.yv++;
       		   			hmap.replace(time, item);
       		   		}
	           }
           }
           catch (Exception e) 
           {
              System.err.println("Error adding to series");
           }
        }
        
        // Fulfill the XY Series
        List<KpiValue> s1 = new ArrayList<KpiValue>();
        List<KpiValue> s2 = new ArrayList<KpiValue>();
        double value2 = 0.0;   
        double value1 = 0.0; 
        SortedSet<Long> keys = new TreeSet<Long>(hmap.keySet());
        for (long key : keys) { 
        	DataItem item = hmap.get(key);
        	value1 = value1 + item.xv;
        	value2 = value2 + item.yv;
        	KpiValue v1 = new KpiValue();
        	v1.setDate(new Date(key));
        	v1.setValue(value1);
        	KpiValue v2 = new KpiValue();
        	v2.setDate(new Date(key));
        	v2.setValue(value2);
        	s1.add(v1);
        	s2.add(v2);
        	//System.out.println(key+": "+item.xv+","+item.yv+" "+value1+","+value2);
        }

        // Finally, create the line chart
        LineChartJs chart = new LineChartJs(title);
        chart.addSerie(Messages.get().getKey("created"), s1);
        chart.addSerie(Messages.get().getKey("resolved"), s2);
    	return createContentWrapper(chart, true);
    }

    private Component builtDocumentsByAnswerByProject() {
    	ProjectEntity prj = viewLogic.getProjectById(project);
    	String title = Messages.get().getKey("answers")+" by "+
    	    	Messages.get().getKey("product")+" "+
    	    	Messages.get().getKey("project")+": "+prj.getName();
    	
    	List<ProductEntity> productList = (List<ProductEntity>) viewLogic.getProducts();
    	String[] products = new String[productList.size()+1];
    	boolean[] addProduct = new boolean[productList.size()+1];
    	int k = 0;
    	products[k] = "Unmapped";
    	addProduct[k] = true;
    	k++;
    	//System.out.println("Project = "+project);
    	for(ProductEntity product : productList){
    		//System.out.println("Product project = "+product.getProject());
    		if(product.getProject()==null
    		|| product.getProject()==0L
    		|| (long)product.getProject()==(long)project)
    			addProduct[k] = true;
    		else
    			addProduct[k] = false;
    		products[k] = product.getName();
    		//System.out.println("Product printed = "+addProduct[k]);
    		k++;
    	}
    	String[] respuestas = prj.getAnswers().split(",");
    	double[][] values = new double[respuestas.length][products.length];
    	
    	// Gets the list of Documents
    	//List<Object[]> recs = (List<Object[]>) viewLogic.getTOCPerProject(project);
    	List<MappingEntity> recs = (List<MappingEntity>) viewLogic.getMappingByProject(project);
    	/*data[0] = "Req.CustomID";
    	data[1] = "Req.Description";
    	data[2] = "Response";
    	data[3] = "Mapped To";
    	data[4] = "Product";*/
    	//for(Object[] rec : recs){
    	for(MappingEntity rec : recs){
    		if(rec.getResponse().isEmpty()) continue; // just in case
    		int i = Arrays.asList(respuestas).indexOf(rec.getResponse());//rec[2]);
    		int j = 0;
    		if(rec.getDoc()!=null && rec.getDoc()!=0L){
    			FeatureEntity feature = viewLogic.getFeatureById(rec.getDoc());
    			if(feature!=null){
    				ProductEntity product = viewLogic.getProductById(feature.getProduct());
    				j = Arrays.asList(products).indexOf(product.getName());//rec[4]);
    			}
    		}
			if(i!=-1){
				if(j!=-1)
					values[i][j] = values[i][j] + 1;
				else
					values[i][0] = values[i][0] + 1;
			}
		}
    	
    	// Finally, create the bar chart
        BarChartJs chart = new BarChartJs(title);
        
        for (int i=0; i<respuestas.length; i++){
    		List<KpiValue> s = new ArrayList<KpiValue>();
    		for (int j=0; j<products.length; j++) {
    			KpiValue v = new KpiValue(products[j]);
            	v.setValue(values[i][j]);
            	if(addProduct[j]==false) continue;
            	s.add(v);
            	//System.out.println("S="+respuestas[i]+" "+products[j]+" "+values[i][j]);
            }
    		chart.addSerie(respuestas[i], s);
    	}
    	return createContentWrapper(chart, true);
    }

    private Component builtProjectsByStatus() {
    	HashMap<String, Double> data = new HashMap<String, Double>();
    	for(ProjectStatusEnum s:ProjectStatusEnum.values()){
    		data.put(s.toString(), 0.0);
    	}
		List<ProjectEntity> recs = (List<ProjectEntity>) viewLogic.getProjects();
		for(ProjectEntity rec:recs){
			Double current = data.get(rec.getStatus().toString());
			if(current!=null){
				current+=1.0;
				data.replace(rec.getStatus().toString(), current);
			}
		}
		String title = Messages.get().getKey("projectsbystatus");
    	return createContentWrapper(new PieChartJs(title, data), false);
    }
        
    private Component builtRequirementsByStatusByProject() {
    	ProjectEntity prj = viewLogic.getProjectById(project);
    	String title = Messages.get().getKey("requirement")+"s by "+
    	        Messages.get().getKey("status")+" "+
    	    	Messages.get().getKey("project")+": "+prj.getName();
    	
    	List<RequirementEntity> reqs = (List<RequirementEntity>) viewLogic.getRequirementsByProject(project);
    	double resolved = 0.0;
        double pending  = 0.0;
        double unassigned = 0.0;
        for(RequirementEntity rec : reqs)
        {
        	if(rec.getResolved())
        		resolved+=1.0;
        	else if(rec.getAssignedto()==null || rec.getAssignedto()==0)
        		unassigned+=1.0;
        	else 
        		pending+=1.0;
        }
    	
        HashMap<String, Double> data = new HashMap<String, Double>();
        data.put(Messages.get().getKey("resolved"), resolved);
        data.put(Messages.get().getKey("unassigned"), unassigned);
	    data.put(Messages.get().getKey("pending"), pending);
	    
    	return createContentWrapper(new PieChartJs(title, data), true);
    }
    
    private Component createContentWrapper(final Component content, boolean menu) {
        final CssLayout slot = new CssLayout();
        slot.setWidth("100%");
        slot.addStyleName("dashboard-panel-slot");

        CssLayout card = new CssLayout();
        //VerticalLayout card = new VerticalLayout();
        card.setWidth("100%");
        card.addStyleName(ValoTheme.LAYOUT_CARD);

        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.addStyleName("dashboard-panel-toolbar");
        toolbar.setWidth("100%");

        Label caption = new Label(content.getCaption());
        caption.addStyleName(ValoTheme.LABEL_H4);
        caption.addStyleName(ValoTheme.LABEL_COLORED);
        caption.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        content.setCaption(null);

        MenuBar tools = new MenuBar();
        tools.addStyleName(ValoTheme.MENUBAR_BORDERLESS);
        MenuItem max = tools.addItem("", FontAwesome.EXPAND, new Command() {

            @Override
            public void menuSelected(final MenuItem selectedItem) {
                if (!slot.getStyleName().contains("max")) {
                    selectedItem.setIcon(FontAwesome.COMPRESS);
                    toggleMaximized(slot, true);
                } else {
                    slot.removeStyleName("max");
                    selectedItem.setIcon(FontAwesome.EXPAND);
                    toggleMaximized(slot, false);
                }
            }
        });
        max.setStyleName("icon-only");
        
        if(menu){
	        MenuItem root = tools.addItem("", FontAwesome.COG, null);
	        
	        for (ProjectEntity s : projects) {
	        	if(!s.getActive()) continue;
	        	root.addItem(s.getName(), new Command() {
	                @Override
	                public void menuSelected(final MenuItem selectedItem) {
	                	project = s.getID();
	                	dashboardPanels.removeComponent(chart);
	                	chart = builtPendingVsResolvedReqsByProject();
	                	dashboardPanels.addComponent(chart);
	                	dashboardPanels.removeComponent(piechart);
	                	piechart = builtRequirementsByStatusByProject();
	                	dashboardPanels.addComponent(piechart);
	                	dashboardPanels.removeComponent(barchart);
	                	barchart = builtDocumentsByAnswerByProject();
	                	dashboardPanels.addComponent(barchart);
	                    //Notification.show("Filtering by Project #"+s.getID());
	                }
	            });
	        }
	        
        }
        
        toolbar.addComponents(caption, tools);
        toolbar.setExpandRatio(caption, 1);
        toolbar.setComponentAlignment(caption, Alignment.MIDDLE_LEFT);

        card.addComponents(toolbar, content);
        slot.addComponent(card);
        return slot;
    }

    @Override
    public void enter(final ViewChangeEvent event) {
    	projects = (List<ProjectEntity>) viewLogic.getProjects();
    	project = projects.get(0).getID();
    	root.removeAllComponents();
    	//root.addComponent(buildHeader());
        root.addComponent(buildSparklines());
        Component content = buildContent();
        root.addComponent(content);
        root.setExpandRatio(content, 1);
    }

    public void dashboardNameEdited(final String name) {
        titleLabel.setValue(name);
    }

    private void toggleMaximized(final Component panel, final boolean maximized) {
        for (Iterator<Component> it = root.iterator(); it.hasNext();) {
            it.next().setVisible(!maximized);
        }
        dashboardPanels.setVisible(true);

        for (Iterator<Component> it = dashboardPanels.iterator(); it.hasNext();) {
            Component c = it.next();
            c.setVisible(!maximized);
        }

        if (maximized) {
            panel.setVisible(true);
            panel.addStyleName("max");
        } else {
            panel.removeStyleName("max");
        }
    }

    public void showError(String msg) {
        Notification.show(msg, Type.ERROR_MESSAGE);
    }

    public void showSaveNotification(String msg) {
        Notification.show(msg, Type.TRAY_NOTIFICATION);
    }
    
}
