package freewill.nextgen.categoria;

import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.ui.CssLayout;
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
        this.setSpacing(true);
        this.addStyleName("dashboard-view");
        //this.addStyleName(ValoTheme.LAYOUT_CARD); // temporal
        Responsive.makeResponsive(this);
        
        Label title = new Label("Seleccione una Modalidad...");
        title.setStyleName(ValoTheme.LABEL_LARGE);
        title.addStyleName(ValoTheme.LABEL_COLORED);
        this.addComponent(title);
        
        CssLayout sparks = new CssLayout();
        sparks.addStyleName("sparks");
        sparks.setWidth("100%");
        Responsive.makeResponsive(sparks);
        this.addComponent(sparks);
        
        /*HorizontalLayout expander = new HorizontalLayout();
        expander.setWidth("100%");
        this.addComponent(expander);
        this.setExpandRatio(expander, 1);*/
        
        try{
        	for(ModalidadEnum modalidad:ModalidadEnum.values()){
	        	Sparkline layout = new Sparkline(
	        			modalidad.toString(), 
	            		FontAwesome.TROPHY, (long)modalidad.ordinal());
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