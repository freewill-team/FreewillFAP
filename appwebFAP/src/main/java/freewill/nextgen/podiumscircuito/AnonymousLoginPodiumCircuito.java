package freewill.nextgen.podiumscircuito;

import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import freewill.nextgen.authentication.AccessControl;
import freewill.nextgen.hmi.utils.Messages;

/**
 * UI content when the user is not logged in yet.
 */
@SuppressWarnings("serial")
public class AnonymousLoginPodiumCircuito extends CssLayout {

    //private AccessControl accessControl;
    VerticalLayout centeringLayout = null;
    Label loginInfoText = null;

    public AnonymousLoginPodiumCircuito(AccessControl accessControl) {
        //this.accessControl = accessControl;       
        if (accessControl.signIn("anonimo", "Anonimo9!", VaadinService.getCurrentRequest().getRemoteHost())) {      	
        	PodiumsCircuitoView results = new PodiumsCircuitoView();
        	results.addStyleName("login-screen");
        	buildUI(results);
        	results.setInfoPanel(this);
        } else {
            buildUI(new Label(Messages.get().getKey("anonimousnotallowed"), ContentMode.HTML));
        }
    }

	private void buildUI(Component loginForm) {
        addStyleName("login-screen");

        // loginForm is centered in the available part of the screen

        // layout to center login form when there is sufficient screen space
        // - see the theme for how this is made responsive for various screen sizes
        //VerticalLayout 
        centeringLayout = new VerticalLayout();
        centeringLayout.setStyleName("centering-layout");
        
        centeringLayout.addComponent(loginForm);
        centeringLayout.setComponentAlignment(loginForm,
                Alignment.MIDDLE_CENTER);
        
        // information text about logging in
        CssLayout loginInformation = buildLoginInformation();

        addComponent(loginInformation);
        addComponent(centeringLayout);
        
    }

    private CssLayout buildLoginInformation() {
        CssLayout loginInformation = new CssLayout();
        loginInformation.setStyleName("login-information");
    	
        loginInfoText = new Label(
                "<h1>"+ Messages.get().getKey("apptitle") +"</h1>",
                ContentMode.HTML);
        loginInformation.addComponent(loginInfoText);
        return loginInformation;
    }
    
    public void setCircuito(String circuitoStr){
    	String circuito = "<h1> Resultados <b>" + circuitoStr +"</b></h1>";
    	loginInfoText.setValue( 
    			"<h1>"+ Messages.get().getKey("apptitle") +"</h1>" +circuito);
    }
    
}
