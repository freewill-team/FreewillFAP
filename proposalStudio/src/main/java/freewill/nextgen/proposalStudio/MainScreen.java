package freewill.nextgen.proposalStudio;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;

import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.company.CompanyAloneView;
import freewill.nextgen.company.CompanyCrudView;
import freewill.nextgen.config.ConfigCrudView;
import freewill.nextgen.dashboard.DashboardView;
import freewill.nextgen.data.CompanyEntity;
import freewill.nextgen.deliverable.DeliverableCrudView;
import freewill.nextgen.eventSummary.EventSummary;
import freewill.nextgen.feature.DocumentAdvView;
import freewill.nextgen.file.FileCrudView;
import freewill.nextgen.hmi.common.AboutView;
import freewill.nextgen.hmi.common.ErrorView;
import freewill.nextgen.hmi.common.Menu;
import freewill.nextgen.hmi.common.NoPermissionView;
import freewill.nextgen.mail.MailServerCrudView;
import freewill.nextgen.mapping.MappingCrudView;
import freewill.nextgen.product.ProductCrudView;
import freewill.nextgen.project.ProjectCrudView;
import freewill.nextgen.requirement.RequirementCrudView;
import freewill.nextgen.support.SupportCrudView;
import freewill.nextgen.user.UserAloneView;
import freewill.nextgen.user.UserCrudView;

@SuppressWarnings("serial")
public class MainScreen extends HorizontalLayout {
	
	private Menu menu = null;
	
	public MainScreen(EntryPoint ui){
		this.setStyleName("main-screen");
		
		// viewContainer is where all different forms/windows will be displayed
	    CssLayout viewContainer = new CssLayout();
	    viewContainer.addStyleName("valo-content");
	    viewContainer.setSizeFull();
	    
        // navigator will allow to change the current form/window
        final Navigator navigator = new Navigator(ui, viewContainer);
        navigator.setErrorView(ErrorView.class);
        menu = new Menu(navigator);
        
        // Add View for permission errors
        navigator.addView("No Permission", new NoPermissionView());
    	
        // Configure forms/windows to be displayed in the menu
        
        CompanyEntity compRec = EntryPoint.get().getAccessControl().getCompany();
        
        if(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.SUPER)){
	        CompanyCrudView companyView = new CompanyCrudView();
	        menu.addView(companyView, companyView.VIEW_NAME, FontAwesome.BUILDING);
        }
        else{
        	CompanyAloneView companyViewAlone = new CompanyAloneView();
	        menu.addView(companyViewAlone,
	        		companyViewAlone.VIEW_NAME+" ("+compRec.getName()+")", 
	        		FontAwesome.BUILDING);
        }
        
        if(!EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.SUPER)){
        	UserAloneView userViewAlone = new UserAloneView();
        	menu.addView(userViewAlone, userViewAlone.VIEW_NAME, FontAwesome.USER);
        }
        
        if(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.ADMIN)){
	        UserCrudView userView = new UserCrudView();
	        menu.addView(userView, userView.VIEW_NAME, FontAwesome.USERS);
        }
            
	    ProductCrudView productView = new ProductCrudView();
	    menu.addView(productView, productView.VIEW_NAME, FontAwesome.PAPERCLIP);
	        
		if(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.USER)){
		    DocumentAdvView docView = new DocumentAdvView();
		    menu.addView(docView, docView.VIEW_NAME, FontAwesome.DATABASE);
		}
		    
		ProjectCrudView projectView = new ProjectCrudView();
		menu.addView(projectView, projectView.VIEW_NAME, FontAwesome.PRODUCT_HUNT);
	    
		RequirementCrudView requirementView = new RequirementCrudView();
		menu.addView(requirementView, requirementView.VIEW_NAME, FontAwesome.TICKET);
		    
		DeliverableCrudView deliverableView = new DeliverableCrudView();
		menu.addView(deliverableView, deliverableView.VIEW_NAME, FontAwesome.FOLDER);
		    
		if(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.USER)){
		    MappingCrudView mappingView = new MappingCrudView();
		    menu.addView(mappingView, mappingView.VIEW_NAME, FontAwesome.COMPRESS);
		}
		    
		FileCrudView fileView = new FileCrudView();
		menu.addView(fileView, fileView.VIEW_NAME, FontAwesome.DOWNLOAD);
	    
		DashboardView dashboardView = new DashboardView();
		menu.addView(dashboardView, dashboardView.VIEW_NAME, FontAwesome.LINE_CHART);
		    
		if(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.ADMIN)){
		   	EventSummary eventView = new EventSummary();
			menu.addView(eventView, eventView.VIEW_NAME, FontAwesome.ANGELLIST);
		}
		    
		if(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.ADMIN)){
		   	MailServerCrudView emailView = new MailServerCrudView();
			menu.addView(emailView, emailView.VIEW_NAME, FontAwesome.ENVELOPE);
		}
		    
		SupportCrudView supportView = new SupportCrudView();
		menu.addView(supportView, supportView.VIEW_NAME, FontAwesome.BUG);
	        
	    if(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.SUPER)){
		    ConfigCrudView configView = new ConfigCrudView();
		    menu.addView(configView, configView.VIEW_NAME, FontAwesome.EDIT);
	    }
        
        AboutView aboutView = new AboutView();
        menu.addView(aboutView, aboutView.VIEW_NAME, FontAwesome.INFO_CIRCLE);

        // Default screen
        navigator.addViewChangeListener(viewChangeListener);
        navigator.navigateTo(aboutView.VIEW_NAME);

        // Create layout
        addComponent(menu);
        addComponent(viewContainer);
        setExpandRatio(viewContainer, 1);
        setSizeFull();
	}

	// notify the view menu about view changes so that it can display which view
    // is currently active
    ViewChangeListener viewChangeListener = new ViewChangeListener() {

        @Override
        public boolean beforeViewChange(ViewChangeEvent event) {
            return true;
        }

        @Override
        public void afterViewChange(ViewChangeEvent event) {
            menu.setActiveView(event.getViewName());
        }

    };
	
}
