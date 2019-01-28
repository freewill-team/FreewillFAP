package freewill.nextgen.managementConsole;

import java.util.Date;

import javax.servlet.annotation.WebServlet;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Viewport;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.SessionDestroyEvent;
import com.vaadin.server.SessionDestroyListener;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.alarmMonitor.AlarmMonitor;
import freewill.nextgen.authentication.AccessControl;
import freewill.nextgen.authentication.BasicAccessControl;
import freewill.nextgen.authentication.LoginScreen;
import freewill.nextgen.authentication.LoginScreen.LoginListener;
import freewill.nextgen.common.Utils.ADMSservice;
import freewill.nextgen.common.Utils.AlarmDic;
import freewill.nextgen.common.entities.EventEntity;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.common.rtdbclient.RtdbDataService;
import freewill.nextgen.eventSummary.EventSummary;
import freewill.nextgen.jshMonitor.JshMonitor;
import freewill.nextgen.hmi.common.NoPermissionView;
import freewill.nextgen.hmi.utils.Messages;
import freewill.nextgen.processMonitor.ProcessMonitor;
import freewill.nextgen.userMonitor.UserMonitor;

/**
 * Main UI class of the application that shows either the login screen or the
 * main view of the application depending on whether a user is signed in.
 *
 * The @Viewport annotation configures the viewport meta tags appropriately on
 * mobile devices. Instead of device based scaling (default), using responsive
 * layouts.
 */
@SuppressWarnings("serial")
@Viewport("user-scalable=no,initial-scale=1.0")
//@Push
@Theme("mytheme")
//@Theme("valo")
public class EntryPoint extends UI {
	
	private AccessControl accessControl = new BasicAccessControl();
	private ProcessMonitor processMonitor = null;
	private AlarmMonitor alarmMonitor = null;
	private EventSummary eventSummary = null;
	private JshMonitor jshMonitor = null;
	private UserMonitor userMonitor = null;
	private static UserRoleEnum IMC_PERMISSION = UserRoleEnum.SUPER;
	private FeederThread heartbeat = null;
	//private EmailSummary eMails = null;
	
    @Override
    protected void init(VaadinRequest vaadinRequest) {
    	
    	Responsive.makeResponsive(this);
        setLocale(vaadinRequest.getLocale());
        getPage().setTitle("FreeWill Console");
        
        if (!accessControl.isUserSignedIn()) {
            setContent( new LoginScreen(accessControl, new LoginListener(){
            	@Override
                public void loginSuccessful() {
            		if(getAccessControl().isUserInRole(IMC_PERMISSION))
            			showMainView();
            		else{
            			setContent(new NoPermissionView());
            			VaadinSession.getCurrent().getSession().invalidate();
            		}
                }
            }));
        } else {
        	if(getAccessControl().isUserInRole(IMC_PERMISSION))
    			showMainView();
    		else{
    			setContent(new NoPermissionView());
    			VaadinSession.getCurrent().getSession().invalidate();
    		}
        }
        
        addDetachListener(new DetachListener() {
            @Override
            public void detach(DetachEvent event) {
            	if(heartbeat!=null && heartbeat.isAlive())
            		heartbeat.interrupt(); //stop();
            }
        });
    
    }

    protected void showMainView() {
    	// Creates main page with different tabs 
    	VerticalLayout layout = new VerticalLayout();
    	layout.setHeight("100%");
    	
    	TabSheet tabs = new TabSheet();
    	tabs.setSizeFull();
    	tabs.setHeight("100%");
    	
    	processMonitor = new ProcessMonitor();
    	tabs.addTab(processMonitor, Messages.get().getKey("processes"));
    	processMonitor.enter(null);
    	
    	alarmMonitor = new AlarmMonitor();
    	tabs.addTab(alarmMonitor, Messages.get().getKey("alarms"));
    	alarmMonitor.enter(null);
    	
    	eventSummary = new EventSummary();
    	tabs.addTab(eventSummary, Messages.get().getKey("events"));
    	
    	jshMonitor = new JshMonitor();
    	tabs.addTab(jshMonitor, Messages.get().getKey("jobscheduler"));
    	jshMonitor.enter(null);
    	
    	userMonitor = new UserMonitor();
    	tabs.addTab(userMonitor, Messages.get().getKey("usermonitor"));
    	userMonitor.enter(null);
    	
    	//eMails = new EmailSummary();
    	//tabs.addTab(eMails, Messages.get().getKey("emails"));
    	//eMails.enter(null);
    	
    	layout.addComponent(header());
    	layout.addComponent(tabs);
    	layout.setExpandRatio(tabs, 1);
        setContent(layout);
        
        // Set Session timeout
        VaadinSession.getCurrent().getSession().setMaxInactiveInterval(300); // 5 minutes
        
        // Start the user heartbeat thread
        heartbeat = new FeederThread(
        		EntryPoint.get().getAccessControl().getUserLogin(), 
        		VaadinService.getCurrentRequest().getRemoteHost(),
        		"FreeWill Console");
        heartbeat.start();
    }
    
    Component header(){
    	HorizontalLayout layout = new HorizontalLayout();
    	layout.addStyleName("menu-title");
    	layout.setSpacing(true);
        layout.setMargin(false);
        layout.setWidth("100%");
        layout.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        
        Image image = new Image(null, new ThemeResource("img/freewill-logo-small.png"));
        image.setWidth("90px");
        layout.addComponent(image);
        layout.setComponentAlignment(image, Alignment.MIDDLE_LEFT);
        
        Label title = new Label("<b>FreeWill Console</b>", ContentMode.HTML);
        title.addStyleName(ValoTheme.LABEL_H3);
        title.addStyleName(ValoTheme.LABEL_BOLD);
        title.addStyleName(ValoTheme.LABEL_COLORED);
        title.setSizeUndefined();
        layout.addComponent(title);
        
        // logout menu item
        MenuBar logoutMenu = new MenuBar();
        logoutMenu.addItem("Logout " /*("
        	+EntryPoint.get().getAccessControl().getPrincipalName() //.getUserName()
        	+")"*/, FontAwesome.SIGN_OUT, new Command() {

            @Override
            public void menuSelected(MenuItem selectedItem) {
                // Injects successful logout event
                String username = EntryPoint.get().getAccessControl().getUserLogin();
    	        RtdbDataService.get().pushEvent(new EventEntity(
        				new Date(), 
        				String.format(AlarmDic.ALM0023.toString(), username, "FreeWill Console"),
        				username,
        				ADMSservice.HMI.toString(), // ParentPoint
        				"FreeWill Console", // PointType
        				AlarmDic.ALM0023.getSeverity(),
        				AlarmDic.ALM0023.getCategory(),
        				username,
        				VaadinService.getCurrentRequest().getRemoteHost()
        				));
    	        
    	        // Unregister user from Logins table
    	        heartbeat.interrupt();
    	        RtdbDataService.get().userCheckout(username, 
    	        		VaadinService.getCurrentRequest().getRemoteHost(),
    	        		"FreeWill Console");
    	        
    	        VaadinSession.getCurrent().getSession().invalidate();
                Page.getCurrent().reload();
            }
        });
        logoutMenu.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        layout.addComponent(logoutMenu);
        
        layout.setExpandRatio(title, 1);
    	return layout;
    }

    public static EntryPoint get() {
        return (EntryPoint) UI.getCurrent();
    }
    
    public AccessControl getAccessControl() {
        return accessControl;
    }
    
    @WebServlet(urlPatterns = "/*", name = "SystemViewer", asyncSupported = true)
    @VaadinServletConfiguration(ui = EntryPoint.class, productionMode = false)
    public static class EntryPointServlet extends VaadinServlet implements SessionDestroyListener {
		@Override
		public void sessionDestroy(SessionDestroyEvent event) {
			//service.echo("Entering sessionDestroy()");
		}
    }
    
    class FeederThread extends Thread {
    	
    	private String name="";
    	private String console="";
    	private String application="";
    	
    	public FeederThread(String name, String console, String app){
    		this.name = name;
    		this.console = console;
    		this.application = app;
    		RtdbDataService.get().userCheckin(name, console, application);
    	}
    	
        @Override
        public void run() {
            try {
            	System.out.println("Refresh thread started...");
                // Update the data forever
                while(true) {
                    Thread.sleep(10000);
                    System.out.println("User Heartbeat"); 
                    /*access(new Runnable() {
                        @Override
                        public void run() {
                        	processMonitor.Refresh();
                        	alarmMonitor.Refresh();
                        }
                    });*/
                    // User heart-beat
                    RtdbDataService.get().userCheckin(name, console, application);
                }
                
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }
        }
    }
    
}
