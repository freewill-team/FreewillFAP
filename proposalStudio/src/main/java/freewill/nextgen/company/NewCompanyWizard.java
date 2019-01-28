package freewill.nextgen.company;

import java.util.Calendar;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.common.entities.UserEntity;
import freewill.nextgen.common.entities.UserEntity.LanguageEnum;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.CompanyEntity;
import freewill.nextgen.data.CompanyEntity.PlanEnum;
import freewill.nextgen.hmi.utils.Messages;
import freewill.nextgen.proposalStudio.EntryPoint;

/**
 * A form for creating a New company,including its first (administrator) user.
 */
@SuppressWarnings("serial")
public class NewCompanyWizard extends CssLayout {
	
	public final String VIEW_NAME = Messages.get().getKey("newcompany");
    
	private TextField companyName = null;
	private TextField adminName = null;
	private TextField loginName = null;
	private TextField eMail = null;
	private ComboBox language = null;
	private VerticalLayout centeringLayout  = null;

    public NewCompanyWizard() {
    	buildUI();
    	companyName.focus();
    }
    
    private void buildUI() {
        addStyleName("login-screen");

        // New company form, centered in the available part of the screen
        Component newCompanyForm = createForm();

        // layout to center login form when there is sufficient screen space
        // - see the theme for how this is made responsive for various screen sizes
        // VerticalLayout 
        centeringLayout = new VerticalLayout();
        centeringLayout.setStyleName("centering-layout");
        
        centeringLayout.addComponent(newCompanyForm);
        centeringLayout.setComponentAlignment(newCompanyForm, Alignment.MIDDLE_CENTER);
        
        // information text about logging in
        CssLayout loginInformation = buildLoginInformation();

        addComponent(centeringLayout);
        addComponent(loginInformation);
    }
        
    private Component createForm(){
        FormLayout form = new FormLayout();
        form.addStyleName("login-form");
        form.setSizeUndefined();
        form.setMargin(true); // false
        
        // Set language combo box values
        language = new ComboBox(Messages.get().getKey("language"));
        for (LanguageEnum s : LanguageEnum.values()) {
            language.addItem(s);
        }
        language.setNullSelectionAllowed(false);
        language.setValue(LanguageEnum.EN);
        
        Label title = new Label("<h3><b>"+Messages.get().getKey("newcompany")+"</b></h3>", ContentMode.HTML);
        
        companyName = new TextField(Messages.get().getKey("company"));
        companyName.setDescription("Write here the name of your company");
        companyName.setWidth("350px");
        
        adminName = new TextField(Messages.get().getKey("Administrator"));
        adminName.setDescription("Write here the full name of your administrator user");
        adminName.setWidth("350px");
        
        loginName = new TextField(Messages.get().getKey("login"));
        loginName.setDescription("Specify here a nick/short name for your administrator user");
        loginName.setWidth("180px");
        
        eMail = new TextField(Messages.get().getKey("email"));
        eMail.setDescription("Write here the email address for your administrator user");
        eMail.setWidth("350px");
        
        Label leyend = new Label(Messages.get().getKey("newcompanyleyend"));
        leyend.setStyleName("wrap");
        leyend.setWidth("320px");
        
        Button submit = new Button(Messages.get().getKey("submit"));
        submit.setDisableOnClick(true);
        submit.setWidth("140px");
        submit.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
	            if(submitAction()){
	                centeringLayout.removeAllComponents();
	                Component confirmForm = createConfirmForm();
	                centeringLayout.addComponent(confirmForm);
	                centeringLayout.setComponentAlignment(confirmForm, Alignment.MIDDLE_CENTER);
	            }
	            else{
	                //showNotification("Uppss. Something went wrong.");
	                submit.setEnabled(true);
	            }
            }
        });
        submit.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        submit.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        
        form.addComponent(title);
        form.addComponent(companyName);
        form.addComponent(adminName);
        form.addComponent(loginName);
        form.addComponent(eMail);
        form.addComponent(language);
        form.addComponent(leyend);
        form.addComponent(submit);
        
        return form;
    }
    
    private void showNotification(String errormsg) {
        // keep the notification visible a little while after moving the
        // mouse, or until clicked
    	Notification notification = new Notification(errormsg, Notification.Type.HUMANIZED_MESSAGE);
        notification.setDelayMsec(2000);
        notification.show(Page.getCurrent());
    }
    
    private CssLayout buildLoginInformation() {
        CssLayout loginInformation = new CssLayout();
        loginInformation.setStyleName("login-information");
        Label loginInfoText = new Label("<h1>Proposal<b>Studio</b></h1>"
                + Messages.get().getKey("newcompany.info"),
                ContentMode.HTML);
        loginInformation.addComponent(loginInfoText);
        return loginInformation;
    }
    
    private boolean submitAction() {
    	// Check parameters
    	if(companyName.getValue().isEmpty()){
    		showNotification("Company Name cannot be empty");
    		companyName.focus();
    		return false;
    	}
    	if(adminName.getValue().isEmpty()){
    		showNotification("Administrator Name cannot be empty");
    		adminName.focus();
    		return false;
    	}
    	if(loginName.getValue().isEmpty()){
    		showNotification("Administrator Login cannot be empty");
    		loginName.focus();
    		return false;
    	}
    	if(eMail.getValue().isEmpty() || !eMail.getValue().contains("@")){
    		showNotification("E-mail address cannot be empty or invalid address");
    		eMail.focus();
    		return false;
    	}            
        System.out.println("NewCompanyWizard: parameters Ok");
        
    	// Creates new company
    	CompanyEntity company = null;
    	try{
	    	CompanyEntity rec = new CompanyEntity();
	    	rec.setActive(true);
	    	Calendar expirationdate = Calendar.getInstance();
	    	expirationdate.add(Calendar.MONTH, 1);
	    	rec.setExpirationdate(expirationdate.getTime());
	    	rec.setPlan(PlanEnum.BASIC);
	    	rec.setName(companyName.getValue());
	    	rec.setCurrency("€");
	    	company = (CompanyEntity) BltClient.get().createEntity(rec, 
	    			CompanyEntity.class, 
	    			EntryPoint.get().getAccessControl().getTokenKey());
	    	if(company==null || company.getID()==null){
	    		showNotification("Fail creating new Company. Please check values.");
	    		System.out.println("NewCompanyWizard: Fail creating new Company. Please check values.");
	    		companyName.focus();
	    		return false;
	    	}
	    	System.out.println("NewCompanyWizard: created company "+companyName.getValue());
    	}
    	catch(Exception e){
    		showNotification(e.getMessage());
    		System.out.println("NewCompanyWizard: "+e.getMessage());
    		companyName.focus();
    		return false;
    	}	
    	
    	// Creates new user
    	try{
	    	UserEntity user = new UserEntity();
	    	user.setName(adminName.getValue());
	    	user.setLoginname(loginName.getValue());
	    	user.setEmail(eMail.getValue());
	    	user.setCompany(company.getID());
	    	user.setLanguage((LanguageEnum)language.getValue());
	    	user.setRole(UserRoleEnum.ADMIN);
	    	user.setFirsttime(true);
	    	user = (UserEntity) BltClient.get().createEntity(user, 
	    			UserEntity.class, 
	    			EntryPoint.get().getAccessControl().getTokenKey());
	    	if(user==null || company.getID()==null){
	    		showNotification("Fail creating new Admin User. Removing new company.");
	    		System.out.println("NewCompanyWizard: Fail creating new Admin User. Removing new company.");
	    		loginName.focus();
	    		BltClient.get().deleteEntity(""+company.getID(),
	 	    			CompanyEntity.class, 
	 	    			EntryPoint.get().getAccessControl().getTokenKey());
	    		return false;
	    	}
	    	System.out.println("NewCompanyWizard: created user "+loginName.getValue());
    	}
    	catch(Exception e){
    		showNotification(e.getMessage()+". "+loginName.getValue()+" already exists.");
    		System.out.println("NewCompanyWizard: "+e.getMessage()+". "+loginName.getValue()+" already exists.");
    		adminName.focus();
    		try {
				BltClient.get().deleteEntity(""+company.getID(),
						CompanyEntity.class, 
						EntryPoint.get().getAccessControl().getTokenKey());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
    		return false;
    	}
    	
    	return true;
    }
    
    private Component createConfirmForm(){
    	VerticalLayout layout = new VerticalLayout();
    	layout.addStyleName("login-form");
    	layout.setSizeUndefined();
    	layout.setMargin(true); 
        
    	Label leyend = new Label(Messages.get().getKey("newcompanyok"));
    	Link link = new Link(Messages.get().getKey("gotofreewill"), 
            	new ExternalResource("http://www.freewill-technologies.es/"));
    	
    	layout.addComponent(leyend);
    	layout.addComponent(link);
    	return layout;
    }
}
