package freewill.nextgen.hmi.common;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.data.CompanyEntity;

/**
 * Responsive navigation menu presenting a list of available views to the user.
 */
@SuppressWarnings("serial")
public class MenuLegacy extends CssLayout {

    private static final String VALO_MENUITEMS = "valo-menuitems";
    private static final String VALO_MENU_TOGGLE = "valo-menu-toggle";
    private static final String VALO_MENU_VISIBLE = "valo-menu-visible";
    private Navigator navigator;
    private Map<String, Button> viewButtons = new HashMap<String, Button>();

    private CssLayout menuItemsLayout;
    private CssLayout menuPart;

    public MenuLegacy(Navigator navigator) {
    	
    	System.out.println("Entrando en Menu, instance = "+new Random().nextInt());
    	
        this.navigator = navigator;
        setPrimaryStyleName(ValoTheme.MENU_ROOT);
        menuPart = new CssLayout();
        menuPart.addStyleName(ValoTheme.MENU_PART);

        // header of the menu
        //final HorizontalLayout top = new HorizontalLayout();
        final VerticalLayout top = new VerticalLayout();
        //top.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        top.addStyleName(ValoTheme.MENU_TITLE);
        top.addStyleName("menu-title");
        top.setSpacing(false); //true);
        top.setMargin(false);
        
        //final VerticalLayout labels = new VerticalLayout();
        Label title = new Label("Free<b>Style</b>", ContentMode.HTML);
        title.addStyleName(ValoTheme.LABEL_H3);
        //title.addStyleName(ValoTheme.LABEL_BOLD);
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
        //image.setWidth("100px");
    	image.setHeight("62px");
        //image.setStyleName("logo");
        top.addComponent(image);
        
        //top.addComponent(title);
        menuPart.addComponent(top);

        // logout menu item
        MenuBar logoutMenu = new MenuBar();
        logoutMenu.addItem("Logout ("
        	+EntryPoint.get().getAccessControl().getPrincipalName()
        	+")", FontAwesome.SIGN_OUT, new Command() {
	            @Override
	            public void menuSelected(MenuItem selectedItem) {
	            	EntryPoint.get().userLogout();
	            }
        });

        logoutMenu.addStyleName("user-menu");
        menuPart.addComponent(logoutMenu);

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
        showMenu.addStyleName(ValoTheme.BUTTON_PRIMARY);
        showMenu.addStyleName(ValoTheme.BUTTON_SMALL);
        showMenu.addStyleName(VALO_MENU_TOGGLE);
        showMenu.setIcon(FontAwesome.NAVICON);
        menuPart.addComponent(showMenu);

        // container for the navigation buttons, which are added by addView()
        menuItemsLayout = new CssLayout();
        menuItemsLayout.setPrimaryStyleName(VALO_MENUITEMS);
        menuPart.addComponent(menuItemsLayout);

        addComponent(menuPart);
    }

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
    public void addView(View view, final String name, Resource icon) {
        navigator.addView(name, view);
        createViewButton(name, name, icon);
    }

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
    
}
