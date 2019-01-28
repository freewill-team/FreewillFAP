package freewill.nextgen.hmi.common;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Tree;
import com.vaadin.ui.UI;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.data.CompanyEntity;
import freewill.nextgen.hmi.utils.Messages;

/**
 * Responsive navigation menu presenting a list of available views to the user.
 */
@SuppressWarnings("serial")
public class Menu extends CssLayout {

    private static final String VALO_MENUITEMS = "valo-menuitems";
    private static final String VALO_MENU_TOGGLE = "valo-menu-toggle";
    private static final String VALO_MENU_VISIBLE = "valo-menu-visible";
    private Navigator navigator;
    private Map<String, Button> viewButtons = new HashMap<String, Button>();
    private CssLayout menuItemsLayout;
    private CssLayout menuPart;
    private Tree menuTree = null;
    private String parent = ""; // Current parent node. New child items will be added here.
    
    public Menu(Navigator navigator) {
    	
    	System.out.println("Entrando en Menu...");
    	
        this.navigator = navigator;
        setPrimaryStyleName(ValoTheme.MENU_ROOT);
        menuPart = new CssLayout();
        menuPart.addStyleName(ValoTheme.MENU_PART);

        // header of the menu
        final HorizontalLayout top = new HorizontalLayout();
        top.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        top.addStyleName(ValoTheme.MENU_TITLE);
        top.addStyleName("menu-title");
        top.setSpacing(true);
        top.setMargin(false);
        
        Label title = new Label("Free<b>Style</b>", ContentMode.HTML);
        title.addStyleName(ValoTheme.LABEL_H2); // H3
        title.setSizeUndefined();
        
        // Poner aqui el logo grabado en Company
        Image image = new Image(null, new ThemeResource("img/freewill-logo-small.png"));
        CompanyEntity compRec = EntryPoint.get().getAccessControl().getCompany();
        if(compRec!=null){
	        byte[] data = compRec.getImage();
	    	if(data!=null && data.length>0){
	        	StreamResource resource = new StreamResource(
	        			new StreamResource.StreamSource() {
	        				@Override
	        				public InputStream getStream() {
	        					return new ByteArrayInputStream(data);
	        				}
	        	       }, compRec.getImagename());
	        	image.setSource(resource);
	    	}
        }
    	image.setHeight("38px");
        image.setStyleName("logo");
        top.addComponent(image);
        top.addComponent(title);
        menuPart.addComponent(top);

        // logout menu item
        menuPart.addComponent(buildUserMenu());

        // button for toggling the visibility of the menu when on a small screen
        final Button showMenu = new Button("Menu", new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                if (menuPart.getStyleName().contains(VALO_MENU_VISIBLE)) {
                    menuPart.removeStyleName(VALO_MENU_VISIBLE);
                } else {
                    menuPart.addStyleName(VALO_MENU_VISIBLE);
                }
            }
        });
        showMenu.addStyleName(ValoTheme.BUTTON_SMALL);
        showMenu.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
        showMenu.addStyleName(VALO_MENU_TOGGLE);
        showMenu.setIcon(FontAwesome.NAVICON);
        menuPart.addComponent(showMenu);

        // container for the navigation buttons, which are added by addView()
        menuItemsLayout = new CssLayout();
        menuItemsLayout.setPrimaryStyleName(VALO_MENUITEMS);
        menuPart.addComponent(menuItemsLayout);

        addComponent(menuPart);
        
        // container for the navigation views, which are added by addView()
        menuTree = new Tree();
        menuTree.setWidth("100%");
        menuTree.setSelectable(true);
        menuTree.setMultiSelect(false);
        menuTree.setStyleName(ValoTheme.TREETABLE_COMPACT);
        menuTree.getContainerDataSource().addContainerProperty("icon", Resource.class, null);
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
        		}
			}
		});
        menuItemsLayout.addComponent(menuTree);
        
    }

    public void addView(View view, final String name, Resource icon) {
    	if(view!=null)
    		navigator.addView(name, view);
        createViewInTree(name, parent, icon);
    }
    
    public void addTitle(String title, Resource icon){
    	createViewInTree(title, null, icon);
    	this.parent = title;
    }
    
    /*public void addTitle(String title){
    	Button button = new Button(title);
        button.setPrimaryStyleName(ValoTheme.MENU_SUBTITLE);
        //button.setIcon(icon);
        menuItemsLayout.addComponent(button);
    }*/
    
    /**
     * Register a pre-created view instance in the navigation menu and in the
     * {@link Navigator}.
     *
     * @see Navigator#addView(String, View)
     *
     * @param view
     *            view instance to register
     * @param name
     *            view name
     * @param icon
     *            view icon in the menu
     */
    /*public void addView(View view, final String name, Resource icon) {
        navigator.addView(name, view);
        createViewButton(name, name, icon);
    }*/

    /**
     * Register a view in the navigation menu and in the {@link Navigator} based
     * on a view class.
     *
     * @see Navigator#addView(String, Class)
     *
     * @param viewClass
     *            class of the views to create
     * @param name
     *            view name
     * @param icon
     *            view icon in the menu
     */
    public void addView(Class<? extends View> viewClass, final String name, Resource icon) {
        navigator.addView(name, viewClass);
        createViewButton(name, name, icon);
    }

    private void createViewButton(final String name, String caption, Resource icon) {
        Button button = new Button(caption, new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                navigator.navigateTo(name);
            }
        });
        button.setPrimaryStyleName(ValoTheme.MENU_ITEM);
        button.setIcon(icon);
        menuItemsLayout.addComponent(button);
        // BVM currentmenuItemsLayout.addComponent(button);
        viewButtons.put(name, button);
    }

    /**
     * Highlights a view navigation button as the currently active view in the
     * menu. This method does not perform the actual navigation.
     *
     * @param viewName
     *            the name of the view to show as active
     */
    public void setActiveView(String viewName) {
        for (Button button : viewButtons.values()) {
            button.removeStyleName("selected");
        }
        Button selected = viewButtons.get(viewName);
        if (selected != null) {
            selected.addStyleName("selected");
        }
        menuPart.removeStyleName(VALO_MENU_VISIBLE);
    }
    
    private Component buildUserMenu() {
        final MenuBar settings = new MenuBar();
        settings.addStyleName("user-menu");
        
        MenuItem settingsItem = settings.addItem("", new ThemeResource("img/profile-pic-300px.jpg"), null);
        settingsItem.setText(EntryPoint.get().getAccessControl().getPrincipalName());
        
        settingsItem.addItem(Messages.get().getKey("micompanycrudview.viewname"), 
        		FontAwesome.BUILDING, new Command() {
            @Override
            public void menuSelected(final MenuItem selectedItem) {
            	UI.getCurrent().getNavigator().navigateTo(Messages.get().getKey("micompanycrudview.viewname"));
            }
        });
        settingsItem.addItem(Messages.get().getKey("miusercrudview.viewname"), 
        		FontAwesome.USER, new Command() {
            @Override
            public void menuSelected(final MenuItem selectedItem) {
            	UI.getCurrent().getNavigator().navigateTo(Messages.get().getKey("miusercrudview.viewname"));
            }
        });
        settingsItem.addSeparator();
        settingsItem.addItem("Logout", FontAwesome.SIGN_OUT, new Command() {
            @Override
            public void menuSelected(final MenuItem selectedItem) {
            	EntryPoint.get().userLogout();
            }
        });
        return settings;
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
    
}
