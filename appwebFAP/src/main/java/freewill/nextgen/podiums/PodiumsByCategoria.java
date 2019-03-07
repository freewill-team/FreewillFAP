package freewill.nextgen.podiums;

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
import freewill.nextgen.data.CategoriaEntity;
import freewill.nextgen.data.CategoriaEntity.ModalidadEnum;
import freewill.nextgen.data.ParticipanteEntity;

@SuppressWarnings("serial")
public class PodiumsByCategoria extends VerticalLayout {
	
	public PodiumsByCategoria(Long competicion, ModalidadEnum modalidad){
		this.setSizeFull();
        this.setMargin(true);
        this.setSpacing(false); //
        this.addStyleName("dashboard-view");
        Responsive.makeResponsive(this);
        
        Label title = new Label("Resultados por Categoría...");
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
        	// filtrar por modalidad actual
        	if(modalidad==null) return;
        	
	        List<CategoriaEntity> categorias = BltClient.get().executeQuery(
	        		"/getByModalidad/"+modalidad.name(), CategoriaEntity.class,
	        		EntryPoint.get().getAccessControl().getTokenKey());
	        
	        int numSparks = 0;
	        for(CategoriaEntity categoria:categorias){
	        	if(competicion!=null){
	        		List<ParticipanteEntity> resultados = BltClient.get().executeQuery(
	        			"/getResultados/"+competicion+"/"+categoria.getId(),
	        			ParticipanteEntity.class,
	        			EntryPoint.get().getAccessControl().getTokenKey());
	        		if(resultados!=null && resultados.size()>0){
	        			PodiumPanel layout = new PodiumPanel(
			        			categoria.getNombre(), resultados);
				        sparks.addComponent(layout);
	        			numSparks++;
	        		}
	        	}
	        	else{
	        		Sparkline layout = new Sparkline(
		        			categoria.getNombre(),
		            		FontAwesome.CHILD, categoria.getId());
		        	sparks.addComponent(layout);
		        	numSparks++;
	        	}
	        }
	        if(numSparks==0)
	        	title.setValue("No hay resultados para mostrar.");
    	}
		catch(Exception e){
			//log.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
}
