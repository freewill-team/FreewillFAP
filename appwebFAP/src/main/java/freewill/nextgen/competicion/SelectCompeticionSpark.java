package freewill.nextgen.competicion;

import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import java.util.List;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Button.ClickListener;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.hmi.common.Sparkline;
import freewill.nextgen.data.CompeticionEntity;

@SuppressWarnings("serial")
public class SelectCompeticionSpark extends VerticalLayout {
	
	ClickListener action = null;
	
	public SelectCompeticionSpark(Long circuito, ClickListener action){
		//this.addComponent(new GenericHeader(VIEW_NAME, FontAwesome.FOLDER));
		this.setSizeFull();
        this.setMargin(true);
        this.setSpacing(false);
        this.addStyleName("dashboard-view");
        //this.addStyleName(ValoTheme.LAYOUT_CARD); // temporal
        Responsive.makeResponsive(this);
        
        Label title = new Label("Seleccione una Competición...");
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
        //sparks.addStyleName("dashboard-panels");
        sparks.setWidth("100%");
        Responsive.makeResponsive(sparks);
        this.addComponent(sparks);
        
        /*HorizontalLayout expander = new HorizontalLayout();
        expander.setWidth("100%");
        this.addComponent(expander);
        this.setExpandRatio(expander, 1);*/
        
        try{
        	// filtrar por circuito actual
        	if(circuito==null) return;
        	
        	List<CompeticionEntity> campeonatos = BltClient.get().executeQuery(
	        		"/getCompeticiones/"+circuito/*.getId()*/, CompeticionEntity.class,
	        		EntryPoint.get().getAccessControl().getTokenKey());
	        
	        for(CompeticionEntity campeonato:campeonatos){
	        	Sparkline layout = new Sparkline(
	        			campeonato.getNombre(), //circuito.getNombre(),
	            		FontAwesome.TROPHY, campeonato.getId());
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
