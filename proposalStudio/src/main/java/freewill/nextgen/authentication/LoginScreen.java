package freewill.nextgen.authentication;

import java.io.Serializable;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.common.Utils.ADMSservice;
import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.hmi.utils.Messages;

/**
 * UI content when the user is not logged in yet.
 */
@SuppressWarnings("serial")
public class LoginScreen extends CssLayout {

    private TextField username;
    private PasswordField password;
    private Button login;
    private Button forgotPassword;
    private LoginListener loginListener;
    private AccessControl accessControl;
    VerticalLayout centeringLayout = null;

    public LoginScreen(AccessControl accessControl, LoginListener loginListener) {
        this.loginListener = loginListener;
        this.accessControl = accessControl;
        buildUI();
        username.focus();
    }

	private void buildUI() {
        addStyleName("login-screen");

        // login form, centered in the available part of the screen
        Component loginForm = buildLoginForm();

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

    private Component buildLoginForm() {
        FormLayout loginForm = new FormLayout();
        loginForm.setMargin(true);
        loginForm.setSpacing(true);

        loginForm.addStyleName("login-form");
        loginForm.setSizeUndefined();
        
        Label title = new Label(Messages.get().getKey("loginto"), ContentMode.HTML);
        title.addStyleName(ValoTheme.LABEL_BOLD);
        title.addStyleName(ValoTheme.LABEL_LARGE);
        title.addStyleName(ValoTheme.LABEL_H2);
        loginForm.addComponent(title);
                
        loginForm.addComponent(username = new TextField(Messages.get().getKey("username")));
        username.setWidth(16, Unit.EM);
        loginForm.addComponent(password = new PasswordField(Messages.get().getKey("password")));
        password.setWidth(16, Unit.EM);
        password.setDescription("Write here your password");
        CssLayout buttons = new CssLayout();
        buttons.setStyleName("buttons");
        loginForm.addComponent(buttons);

        buttons.addComponent(login = new Button(Messages.get().getKey("login")));
        login.setDisableOnClick(true);
        login.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    login();
                } finally {
                    login.setEnabled(true);
                }
            }
        });
        login.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        login.addStyleName(ValoTheme.BUTTON_FRIENDLY);

        buttons.addComponent(forgotPassword = new Button(Messages.get().getKey("forgotpass")));
        forgotPassword.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                //showNotification(new Notification("Hint: Try anything"));
            	Component newpasswordLayout = buildNewPasswordForm();
            	centeringLayout.replaceComponent(loginForm, newpasswordLayout);
            }
        });
        forgotPassword.addStyleName(ValoTheme.BUTTON_LINK);
        return loginForm;
    }

    private CssLayout buildLoginInformation() {
        CssLayout loginInformation = new CssLayout();
        loginInformation.setStyleName("login-information");
        Label loginInfoText = new Label(
                "<h1>"+ Messages.get().getKey("LoginScreen.title") +"</h1>"
                        + Messages.get().getKey("LoginScreen.info"),
                ContentMode.HTML);
        loginInformation.addComponent(loginInfoText);
        return loginInformation;
    }

    private void login() {
        if (accessControl.signIn(username.getValue(), password.getValue(),
        		VaadinService.getCurrentRequest().getRemoteHost())) {
            loginListener.loginSuccessful();
        } else {
            showNotification(new Notification(Messages.get().getKey("error"),
            		Messages.get().getKey("LoginScreen.loginFail"),
                    Notification.Type.HUMANIZED_MESSAGE));
            username.focus();
        }
    }

    private void showNotification(Notification notification) {
        // keep the notification visible a little while after moving the
        // mouse, or until clicked
        notification.setDelayMsec(2000);
        notification.show(Page.getCurrent());
    }

    public interface LoginListener extends Serializable {
        void loginSuccessful();
    }
    
    private Component buildNewPasswordForm() {
        FormLayout loginForm = new FormLayout();

        loginForm.addStyleName("login-form");
        loginForm.setSizeUndefined();
        loginForm.setMargin(false);
        
        loginForm.addComponent(new Label("<h><b>"+
        		"Request a New Password"+"</b></h1>", ContentMode.HTML));
                
        loginForm.addComponent(username = new TextField(Messages.get().getKey("username")));
        username.setWidth(16, Unit.EM);
        
        Label resultado = new Label(Messages.get().getKey("LoginScreen.resetPassOk"));
        resultado.setStyleName(ValoTheme.LABEL_SUCCESS);
        resultado.setVisible(false);
        loginForm.addComponent(resultado);
        
        loginForm.addComponent(login = new Button("Request"));
        login.setDisableOnClick(true);
        login.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                	// Resetear contraseña y Enviar correo
                	boolean result = BltClient.get().resetPassword(username.getValue(), ADMSservice.RTS.toString());
                	if(result){
                		showNotification(new Notification("A new Password has been requested. Please check your e-mail.", // i18N
                				Notification.Type.HUMANIZED_MESSAGE));
                			resultado.setVisible(true);
                	}
                	else{
                		showNotification(new Notification("Error. Problem requesting new password.", // i18N
                    		Notification.Type.ERROR_MESSAGE));
                	}
                }
                catch(Exception e){
                	showNotification(new Notification(e.getMessage(),
                    		Notification.Type.ERROR_MESSAGE));
                } 
                finally {
                    login.setEnabled(false);
                }
            }
        });
        login.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        login.addStyleName(ValoTheme.BUTTON_FRIENDLY);

        return loginForm;
    }
    
}
