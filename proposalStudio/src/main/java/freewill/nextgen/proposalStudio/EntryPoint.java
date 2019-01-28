package freewill.nextgen.proposalStudio;

import java.util.Date;

import javax.servlet.annotation.WebServlet;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Viewport;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.SessionDestroyEvent;
import com.vaadin.server.SessionDestroyListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.authentication.AccessControl;
import freewill.nextgen.authentication.BasicAccessControl;
import freewill.nextgen.authentication.LoginScreen;
import freewill.nextgen.authentication.LoginScreen.LoginListener;
import freewill.nextgen.common.Utils.ADMSservice;
import freewill.nextgen.common.Utils.AlarmDic;
import freewill.nextgen.common.Utils.CategoryEnum;
import freewill.nextgen.common.Utils.SeverityEnum;
import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.common.entities.EventEntity;
import freewill.nextgen.common.entities.UserEntity;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.common.rtdbclient.RtdbDataService;
import freewill.nextgen.company.NewCompanyWizard;
import freewill.nextgen.data.ConfigEntity;
import freewill.nextgen.hmi.common.ConfirmUseTermsDialog;
import freewill.nextgen.hmi.common.NoPermissionView;

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
@Theme("mytheme")
public class EntryPoint extends UI {
	
	private AccessControl accessControl = new BasicAccessControl();
	private static UserRoleEnum IMC_PERMISSION = UserRoleEnum.READONLY;
	private FeederThread heartbeat = null;
	
    @Override
    protected void init(VaadinRequest vaadinRequest) {
        
        Responsive.makeResponsive(this);
        setLocale(vaadinRequest.getLocale());
        getPage().setTitle("ProposalStudio");
        System.out.println("Entering Servlet init()");
        System.out.println("Path Info = "+vaadinRequest.getPathInfo());
        
        if(vaadinRequest.getParameter("newcompany")!=null 
           || vaadinRequest.getPathInfo().contains("newcompany")){
        	System.out.println("Invoking newcompany wizard...");
        	addStyleName(ValoTheme.UI_WITH_MENU);
            setContent(new NewCompanyWizard());
        	return;
        }
        
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
    	UserEntity rec = accessControl.getUserEntity();
    	// if user first time, asks for User Terms agreement
    	if(rec.getFirsttime()){
    		ConfirmUseTermsDialog cd = new ConfirmUseTermsDialog();
        	cd.setOKAction(new ClickListener() {
                @Override
                public void buttonClick(final ClickEvent event) {
        			try {
        				cd.close();
        				EntryPoint.get().createEvent("User '"+rec.getName()+"' has accepted Use Terms Agreement and Privacy Policy.");
            			rec.setFirsttime(false);
            			BltClient.get().updateEntity(rec,
            					UserEntity.class, 
            					EntryPoint.get().getAccessControl().getTokenKey());
						showMainView();
					} catch (Exception e) {
						Notification n = new Notification(e.getMessage(), Type.ERROR_MESSAGE);
	                    n.setDelayMsec(500);
	                    n.show(getUI().getPage());
					}
                }
            });
        	getUI().addWindow(cd);
    	}
    	else
    		drawMainView();
    }

    protected void drawMainView() {
    	// Creates main page with menu
    	addStyleName(ValoTheme.UI_WITH_MENU);
    	setContent(new MainScreen(EntryPoint.this));
        
        // Set Session timeout
        VaadinSession.getCurrent().getSession().setMaxInactiveInterval(240); // 4 minutes
        
        // Start the user heartbeat thread
        heartbeat = new FeederThread(
        		EntryPoint.get().getAccessControl().getUserLogin(), 
        		VaadinService.getCurrentRequest().getRemoteHost(),
        		"ProposalStudio"); 
        // TODO mientras debug 
        heartbeat.start();
    }

	/*Component header(){
    	HorizontalLayout layout = new HorizontalLayout();
    	layout.addStyleName("menu-title");
    	layout.setSpacing(true);
        layout.setMargin(false);
        layout.setWidth("100%");
        layout.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        
        Image image = new Image(null, new ThemeResource("img/freewill-logo-small.png"));
        image.setWidth("100px");
        layout.addComponent(image);
        layout.setComponentAlignment(image, Alignment.MIDDLE_LEFT);
        
        Label title = new Label("Proposal<b>Studio</b>", ContentMode.HTML);
        title.addStyleName(ValoTheme.LABEL_H3);
        title.addStyleName(ValoTheme.LABEL_BOLD);
        title.addStyleName(ValoTheme.LABEL_COLORED);
        title.setSizeUndefined();
        layout.addComponent(title);
        
        // logout menu item
        MenuBar logoutMenu = new MenuBar();
        logoutMenu.addItem("Logout("
        	+EntryPoint.get().getAccessControl().getPrincipalName()
        	+")", FontAwesome.SIGN_OUT, new Command() {
	            @Override
	            public void menuSelected(MenuItem selectedItem) {
	                userLogout();
	            }
        });
        logoutMenu.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        layout.addComponent(logoutMenu);
        
        layout.setExpandRatio(title, 1);
    	return layout;
    }*/

	public void userLogout() {
		// Injects successful logout event
        String username = EntryPoint.get().getAccessControl().getUserLogin();
        RtdbDataService.get().pushEvent(new EventEntity(
				new Date(), 
				String.format(AlarmDic.ALM0023.toString(), username, "ProposalStudio"),
				username,
				ADMSservice.HMI.toString(), // ParentPoint
				"ProposalStudio", // PointType
				AlarmDic.ALM0023.getSeverity(),
				AlarmDic.ALM0023.getCategory(),
				username,
				VaadinService.getCurrentRequest().getRemoteHost()
				));
        
        // Unregister user from Logins table
        heartbeat.interrupt();
        RtdbDataService.get().userCheckout(username, 
        		VaadinService.getCurrentRequest().getRemoteHost(),
        		"ProposalStudio");
        
        VaadinSession.getCurrent().getSession().invalidate();
        Page.getCurrent().reload();
	}
	
    public static EntryPoint get() {
        return (EntryPoint) UI.getCurrent();
    }
    
    public AccessControl getAccessControl() {
        return accessControl;
    }
    
    public long getConfigLong(String key){
		try {
			ConfigEntity rec = (ConfigEntity) BltClient.get().executeCommand("/getconfig/"+key, 
					ConfigEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
			long value = Long.parseLong(rec.getValue());
			return value;
		} catch (Exception e) {
			e.printStackTrace();
        	//throw new IllegalArgumentException("Fail to retrieve Record from Config Database");
			return 0L;
		}
    }
    
    public String getConfigString(String key){
		try {
			ConfigEntity rec = (ConfigEntity) BltClient.get().executeCommand("/getconfig/"+key, 
					ConfigEntity.class, 
					EntryPoint.get().getAccessControl().getTokenKey());
			return rec.getValue();
		} catch (Exception e) {
			e.printStackTrace();
        	//throw new IllegalArgumentException("Fail to retrieve Record from Config Database");
			return "";
		}
    }
    
    private void createEvent(String message) {
		// Create new Event
    	String username = EntryPoint.get().getAccessControl().getUserLogin();
        RtdbDataService.get().pushEvent(new EventEntity(
				new Date(), 
				message,
				username,
				ADMSservice.HMI.toString(), // ParentPoint
				"ProposalStudio", // PointType
				SeverityEnum.LOW,
				CategoryEnum.APPLICATION,
				username,
				VaadinService.getCurrentRequest().getRemoteHost()
				));
	}
    
    @WebServlet(urlPatterns = "/*", name = "ProposalStudio", asyncSupported = true)
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
                    // User heart-beat
                    RtdbDataService.get().userCheckin(name, console, application);
                }
                
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }
        }
    }
    
}
