package fwt.apppubfap;

//import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.AppLayoutMenu;
import com.vaadin.flow.component.applayout.AppLayoutMenuItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
//import com.vaadin.flow.server.VaadinSession;

import fwt.apppubfap.authentication.AccessControl;
import fwt.apppubfap.classic.ClassicView;
import fwt.apppubfap.derrapes.DerrapesView;
import fwt.apppubfap.speed.SpeedView;
import fwt.apppubfap.salto.SaltoView;
import fwt.apppubfap.battle.BattleView;
import fwt.apppubfap.jam.JamView;

@SuppressWarnings("serial")
@Push
@Route
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=no")
public class MainView extends Div {
	
	private static AccessControl accessControl = new AccessControl();
	    
	public String getPrincipalName(){
	    return accessControl.getPrincipalName();
	}
	
	public MainView() {
		this.setSizeFull();
		if (accessControl.signIn("anonimo", "Anonimo9!", 
				VaadinService.getCurrentRequest().getRemoteHost()))    	
			this.add(showMainView());
		//else
		//	this.add(new ErrorView());
	}
	
	private AppLayout showMainView() {
		
		AppLayout layout = new AppLayout();
		
		Image logo = new Image("images/freewill-logo-small.png", "CronoWeb");
		logo.setHeight("40px");
		layout.setBranding(logo);
		
		Image speedIcon = new Image("images/speed.png", "Speed");
		speedIcon.setHeight("34px");
		Image saltoIcon = new Image("images/salto.png", "Salto");
		saltoIcon.setHeight("34px");
		Image derrapesIcon = new Image("images/derrapes.png", "Derrapes");
		derrapesIcon.setHeight("34px");
		Image classicIcon = new Image("images/classic.png", "Classic");
		classicIcon.setHeight("34px");
		Image battleIcon = new Image("images/battle.png", "Battle");
		battleIcon.setHeight("34px");
		Image jamIcon = new Image("images/jam.png", "Jam");
		jamIcon.setHeight("34px");
		
		AppLayoutMenu menu = layout.createMenu();
		/*AppLayoutMenuItem route0 = new AppLayoutMenuItem(
				VaadinIcon.SIGN_OUT.create(), "Salir", 
				e -> logout()
				);*/
		AppLayoutMenuItem route1 = new AppLayoutMenuItem(
				speedIcon, " Speed", 
				e -> layout.setContent(new SpeedView())
				);
		AppLayoutMenuItem route2 = new AppLayoutMenuItem(
				saltoIcon, " Salto",
				e -> layout.setContent(new SaltoView())
				);
		AppLayoutMenuItem route3 = new AppLayoutMenuItem(
				derrapesIcon, " Derrapes",
				e -> layout.setContent(new DerrapesView())
				);
		AppLayoutMenuItem route4 = new AppLayoutMenuItem(
				classicIcon, " Classic",
				e -> layout.setContent(new ClassicView())
				);
		AppLayoutMenuItem route5 = new AppLayoutMenuItem(
				battleIcon, " Battle",
				e -> layout.setContent(new BattleView())
				);
		AppLayoutMenuItem route6 = new AppLayoutMenuItem(
				jamIcon, " Jam",
				e -> layout.setContent(new JamView())
				);
		
		menu.addMenuItems(route1, route2, route3, route4, route5, route6/*, route0*/);
		
		VerticalLayout texto = new VerticalLayout();
		texto.add(new Label("Seleccione una Modalidad en el Menú para ver los Resultados..."));
		texto.setSizeFull();
		texto.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
		layout.setContent(texto);
		
		return layout;
	}

	/*private void logout() {
		VaadinSession.getCurrent().getSession().invalidate();
		UI.getCurrent().getPage().reload();
	}*/
	
}
