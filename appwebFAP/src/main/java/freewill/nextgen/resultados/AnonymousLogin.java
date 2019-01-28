package freewill.nextgen.resultados;

import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import freewill.nextgen.authentication.AccessControl;
import freewill.nextgen.data.CompeticionEntity;
import freewill.nextgen.hmi.utils.Messages;
import freewill.nextgen.resultados.ResultadosCrudView;

/**
 * UI content when the user is not logged in yet.
 */
@SuppressWarnings("serial")
public class AnonymousLogin extends CssLayout {

    //private AccessControl accessControl;
    VerticalLayout centeringLayout = null;

    public AnonymousLogin(AccessControl accessControl) {
        //this.accessControl = accessControl;       
        if (accessControl.signIn("anonimo", "Anonimo9!", VaadinService.getCurrentRequest().getRemoteHost())) {      	
        	ResultadosCrudView results = new ResultadosCrudView();
        	results.enter(null);
        	results.addStyleName("login-screen");
        	buildUI(results);
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
        String competicionStr = "";
        
        ResultadosCrudLogic viewLogic = new ResultadosCrudLogic(null);
    	CompeticionEntity competi = viewLogic.findLastCompeticion();
        if(competi!=null)
        	competicionStr = "<h1> Resultados <b>" + competi.getNombre()+"</b></h1>";
    	
        Label loginInfoText = new Label(
                "<h1>"+ Messages.get().getKey("apptitle") +"</h1>" +competicionStr,
                ContentMode.HTML);
        loginInformation.addComponent(loginInfoText);
        return loginInformation;
    }
    
}
