package fwt.apppubfap;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

//import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.AppLayoutMenu;
import com.vaadin.flow.component.applayout.AppLayoutMenuItem;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.InputStreamFactory;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinService;
//import com.vaadin.flow.server.VaadinSession;

import fwt.apppubfap.authentication.AccessControl;
import fwt.apppubfap.classic.ClassicView;
import fwt.apppubfap.derrapes.DerrapesView;
import fwt.apppubfap.dtos.CompanyEntity;
import fwt.apppubfap.speed.SpeedView;
import fwt.apppubfap.salto.SaltoView;
import fwt.apppubfap.battle.BattleView;
import fwt.apppubfap.jam.JamView;

@SuppressWarnings("serial")
@Push
@Route
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=no")
//@PWA(name = "Freestyle FAP", shortName="FAP", iconPath = "images/freewill-logo-small.png",
//offlineResources = {"images/freewill-logo-small.png"})
public class MainView extends Div {
	
	private static AccessControl accessControl = new AccessControl();
	private SelectCompeticion competicion = null;
	    
	public String getPrincipalName(){
	    return accessControl.getPrincipalName();
	}
	
	public MainView() {
		this.setSizeFull();
		if (accessControl.signIn("anonimo", "Anonimo9!", 
				VaadinService.getCurrentRequest().getRemoteHost()))    	
			this.add(showMainView());
		else{
			VerticalLayout texto = new VerticalLayout();
			texto.add(new Label("Upss, se ha producido un error al acceder a los datos!!!"));
			texto.setSizeFull();
			texto.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
			this.add(texto);
		}	
	}
	
	private AppLayout showMainView() {
		
		AppLayout layout = new AppLayout();
		
		VerticalLayout defaultView = createDefaultView();
		layout.setContent(defaultView);
		
		Image logo = new Image("images/freewill-logo-small.png", "CronoWeb");
		logo.setHeight("40px");
		logo.addClickListener(e -> layout.setContent(defaultView));
		layout.setBranding(logo);
		
		Image speedIcon = new Image("images/speed.png", "Speed");
		speedIcon.setHeight("32px");
		Image saltoIcon = new Image("images/salto.png", "Salto");
		saltoIcon.setHeight("32px");
		Image derrapesIcon = new Image("images/derrapes.png", "Derrapes");
		derrapesIcon.setHeight("32px");
		Image classicIcon = new Image("images/classic.png", "Classic");
		classicIcon.setHeight("32px");
		Image battleIcon = new Image("images/battle.png", "Battle");
		battleIcon.setHeight("32px");
		Image jamIcon = new Image("images/jam.png", "Jam");
		jamIcon.setHeight("32px");
		
		AppLayoutMenu menu = layout.createMenu();
		/*AppLayoutMenuItem route0 = new AppLayoutMenuItem(
				VaadinIcon.SIGN_OUT.create(), "Salir", 
				e -> logout()
				);*/
		AppLayoutMenuItem route1 = new AppLayoutMenuItem(
				speedIcon, " Speed", 
				e -> layout.setContent(new SpeedView(competicion.getCompeticion()))
				);
		AppLayoutMenuItem route2 = new AppLayoutMenuItem(
				saltoIcon, " Salto",
				e -> layout.setContent(new SaltoView(competicion.getCompeticion()))
				);
		AppLayoutMenuItem route3 = new AppLayoutMenuItem(
				derrapesIcon, " Derrapes",
				e -> layout.setContent(new DerrapesView(competicion.getCompeticion()))
				);
		AppLayoutMenuItem route4 = new AppLayoutMenuItem(
				classicIcon, " Classic",
				e -> layout.setContent(new ClassicView(competicion.getCompeticion()))
				);
		AppLayoutMenuItem route5 = new AppLayoutMenuItem(
				battleIcon, " Battle",
				e -> layout.setContent(new BattleView(competicion.getCompeticion()))
				);
		AppLayoutMenuItem route6 = new AppLayoutMenuItem(
				jamIcon, " Jam",
				e -> layout.setContent(new JamView(competicion.getCompeticion()))
				);
		AppLayoutMenuItem route0 = new AppLayoutMenuItem(
				VaadinIcon.ARROW_LEFT.create(), " Inicio",
				e -> layout.setContent(defaultView)
				);
		
		menu.addMenuItems(route1, route2, route3, route4, route5, route6, route0);
			
		return layout;
	}

	protected VerticalLayout createDefaultView() {
		VerticalLayout texto = new VerticalLayout();
		texto.setSizeFull();
		texto.setSpacing(true);
		texto.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
		
		competicion = new SelectCompeticion("Seleccione una Competición...");
		competicion.setWidth("80%");
		texto.add(competicion);
		
		Label label = new Label("Y una Modalidad en el Menú para ver los Resultados...");
		label.setWidth("80%");
		texto.add(label);
		
		// Toma imagen de Company
		Image image = new Image("images/LogoFAP550x160.png", "FAP");
		//image.setWidth("80%");
		image.setMaxWidth("80%");
		CompanyEntity company = accessControl.getCompany();
		if(company!=null){
			StreamResource src = creaResource(company.getImage(), company.getImagename());
			if(src!=null){
				image.setSrc(src);
			}
		}
		texto.add(image);
		
		return texto;
	}
	
	/*private void logout() {
		VaadinSession.getCurrent().getSession().invalidate();
		UI.getCurrent().getPage().reload();
	}*/
	
	private StreamResource creaResource(byte[] data, String filename) {
    	if(data!=null && data.length>0){
        	StreamResource src = new StreamResource(
        			filename, 
        			new InputStreamFactory(){
						@Override
						public InputStream createInputStream() {
							return new ByteArrayInputStream(data);
						}
        				
        			});
        	return src;
    	}
    	else 
    		return null;
	}
	
}
