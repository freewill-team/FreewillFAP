package freewill.nextgen.categoria;

import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import com.vaadin.server.Resource;
import com.vaadin.server.Responsive;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Button.ClickListener;

import freewill.nextgen.hmi.common.Sparkline;
import freewill.nextgen.data.CategoriaEntity.ModalidadEnum;

@SuppressWarnings("serial")
public class SelectModalidad extends VerticalLayout {
	
	ClickListener action = null;
	
	public SelectModalidad(ClickListener action){
		//this.addComponent(new GenericHeader(VIEW_NAME, FontAwesome.FOLDER));
		this.setSizeFull();
        this.setMargin(true);
        this.setSpacing(false);
        this.addStyleName("dashboard-view");
        Responsive.makeResponsive(this);
        
        Label title = new Label("Seleccione una Modalidad...");
        title.setStyleName(ValoTheme.LABEL_LARGE);
        title.addStyleName(ValoTheme.LABEL_COLORED);
        //this.addComponent(title);
        
        HorizontalLayout hlayout = new HorizontalLayout();
        hlayout.setSizeFull();
        hlayout.setMargin(false); // true
        hlayout.setSpacing(true);
        hlayout.addStyleName("sparks");
        hlayout.addComponents(new Label(), title);
        hlayout.setExpandRatio(title, 1);
        this.addComponent(hlayout);
        
        CssLayout sparks = new CssLayout();
        sparks.addStyleName("sparks");
        sparks.setWidth("100%");
        Responsive.makeResponsive(sparks);
        this.addComponent(sparks);
        
        try{
        	for(ModalidadEnum modalidad:ModalidadEnum.values()){
        		Resource icon =null;
        		switch(modalidad){
        		case SPEED: icon = new ThemeResource("img/speed.png"); break;
        		case JUMP: icon = new ThemeResource("img/salto.png"); break;
        		case SLIDE: icon = new ThemeResource("img/derrapes.png"); break;
        		case CLASSIC: icon = new ThemeResource("img/classic.png"); break;
        		case BATTLE: icon = new ThemeResource("img/battle.png"); break;
        		case JAM: icon = new ThemeResource("img/jam.png"); break;
        		}
	        	Sparkline layout = new Sparkline(
	        			modalidad.toString(), icon, (long)modalidad.ordinal());
	        	layout.addClickListener(action);
	        	sparks.addComponent(layout);
	        }
    	}
		catch(Exception e){
			//log.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
}
