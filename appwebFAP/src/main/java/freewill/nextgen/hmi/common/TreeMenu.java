package freewill.nextgen.hmi.common;

import java.io.File;
import java.net.MalformedURLException;  
import java.net.URL;  
import java.net.URLClassLoader;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.server.Resource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Responsive navigation menu presenting a list of available views to the user.
 */
@SuppressWarnings("serial")
public class TreeMenu extends VerticalLayout {

    private Navigator navigator;
    private Tree menuTree = null;
    private String parent = ""; // Current parent node. New child items will be added here.

    public TreeMenu(Navigator navigator) {
    	
        this.navigator = navigator;
        //this.setSizeFull();
        this.setWidth("200px");
        this.setSpacing(false);
        this.setMargin(false);    
        this.setDefaultComponentAlignment(Alignment.TOP_LEFT);
        this.setStyleName(ValoTheme.MENU_ROOT);
        
        // Search/filter text box
        TextField filter = new TextField();
        filter.setWidth("100%");
        filter.setStyleName("filter-textfield");
        filter.setInputPrompt("Filter");
        
        filter.setImmediate(true);
        filter.addTextChangeListener(new FieldEvents.TextChangeListener() {
            @Override
            public void textChange(FieldEvents.TextChangeEvent event) {
            	HierarchicalContainer container = (HierarchicalContainer) menuTree.getContainerDataSource();
                // filter menuTree
            	String filterString = event.getText();
            	if (filterString.length() > 0) {
            		container.removeAllContainerFilters();
                    SimpleStringFilter pointtypeFilter = new SimpleStringFilter("name", filterString, true, false);
                    container.addContainerFilter(pointtypeFilter);
                }
            	else
            		container.removeAllContainerFilters();
            }
        });
        
        // container for the navigation views, which are added by addView()
        menuTree = new Tree();
        menuTree.setWidth("100%");
        menuTree.setSelectable(true);
        menuTree.setMultiSelect(false);
        menuTree.setStyleName(ValoTheme.TREETABLE_COMPACT);
        menuTree.getContainerDataSource().addContainerProperty("icon", Resource.class, null); //
        menuTree.getContainerDataSource().addContainerProperty("name", String.class, "Root");
        menuTree.setItemIconPropertyId("icon"); //
        menuTree.setItemCaptionPropertyId("name");
        menuTree.setItemCaptionMode(ItemCaptionMode.PROPERTY); //
        
        menuTree.addItemClickListener(new ItemClickEvent.ItemClickListener() {
        	@Override
			public void itemClick(ItemClickEvent event) {
				Item obj = event.getItem();
        		if(obj!=null){
        			String view = (String) obj.getItemProperty("name").getValue();
        			//System.out.println("Selected View = "+view);
        			navigator.navigateTo(view);
        			setVisible(false);
        		}
			}
		});
        
        //VerticalLayout layTree = new VerticalLayout();
        Panel layTree = new Panel();
        layTree.setSizeFull();
        layTree.setWidth("100%");
        //layTree.setStyleName(ValoTheme.LAYOUT_CARD);
        //c.addComponent(menuTree);
        layTree.setContent(menuTree);
        this.addComponent(filter);
        this.addComponent(layTree);
        this.setExpandRatio(layTree, 1);
    }

    public void addView(View view, final String name, Resource icon) {
    	if(view!=null)
    		navigator.addView(name, view);
        createViewInTree(name, parent, icon);
    }
    
    public void addTitle(String title){
    	createViewInTree(title, null, null);
    	this.parent = title;
    }
    
    public void addView(View view, String name, String parent) {
    	if(view!=null)
    		navigator.addView(name, view);
        createViewInTree(name, parent, null);
    }
    
    public void addView(String classpath, String name, String parent) {
		try {
			View newentry = (View) this.LoadClass(classpath);
			this.addView(newentry, name, parent);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
    }

    @SuppressWarnings("unchecked")
	private void createViewInTree(final String name, String parent, Resource icon) {
    	System.out.println("Adding "+name +"/"+parent);
    	HierarchicalContainer container = (HierarchicalContainer) menuTree.getContainerDataSource();
    	if(parent==null){
    		// Add Root item
	    	Item root = container.addItem(name);
	    	container.setChildrenAllowed(name, true);
	    	root.getItemProperty("name").setValue(name);
	    	root.getItemProperty("icon").setValue(icon); //
    	}
    	else{
    			// Add child item
    	    	Object item = container.addItem();
    		    container.getContainerProperty(item,"name").setValue(name);
    		    container.getContainerProperty(item,"icon").setValue(icon); //
    		    container.setChildrenAllowed(item, true);
    		    container.setParent(item, parent);
    	}
    	// Expand all
    	//for (Object itemId:menuTree.getItemIds())
    	//	menuTree.expandItem(itemId);
    }

    public void setActiveView(String viewName) {
        // Do nothing
    }
    
    // This function will dynamically load a new class given the package and class name
    @SuppressWarnings("unchecked")
	private <C> C LoadClass(String classpath) throws ClassNotFoundException {
        File pluginsDir = new File(System.getProperty("user.dir"));
        for (File jar : pluginsDir.listFiles()) {
        	try {
	            @SuppressWarnings("deprecation")
				ClassLoader loader = URLClassLoader.newInstance(
	                new URL[] { jar.toURL() },
	                getClass().getClassLoader()
	            );
	            Class<?> clazz = Class.forName(classpath, true, loader);
	            return (C) clazz.newInstance();
	
	        } catch (ClassNotFoundException e) {
	            // There might be multiple JARs in the directory,
	            // so keep looking
	            continue;
	        } catch (MalformedURLException e) {
	            e.printStackTrace();
	        } catch (IllegalAccessException e) {
	            e.printStackTrace();
	        } catch (InstantiationException e) {
	            e.printStackTrace();
	        }
        }
        throw new ClassNotFoundException("Class " + classpath
            + " wasn't found in directory " + System.getProperty("user.dir"));
    }
    
    /*public void showCaptions(boolean visible){
    	if(visible){
    		menuTree.setItemIconPropertyId("icon");
    		menuTree.setItemCaptionPropertyId("name");
    		menuTree.setItemCaptionMode(ItemCaptionMode.PROPERTY);
    	}
    	else{
    		menuTree.setItemCaptionMode(ItemCaptionMode.ICON_ONLY);
    	}
    }*/
}
