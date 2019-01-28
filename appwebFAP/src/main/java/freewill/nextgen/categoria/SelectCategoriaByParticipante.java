package freewill.nextgen.categoria;

import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import java.util.List;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Button.ClickListener;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.hmi.common.Sparkline;
import freewill.nextgen.data.CategoriaEntity;
import freewill.nextgen.data.CategoriaEntity.ModalidadEnum;
import freewill.nextgen.data.ParticipanteEntity;

@SuppressWarnings("serial")
public class SelectCategoriaByParticipante extends VerticalLayout {
	
	ClickListener action = null;
	
	public SelectCategoriaByParticipante(Long competicion, ModalidadEnum modalidad, ClickListener action){
		//this.addComponent(new GenericHeader(VIEW_NAME, FontAwesome.FOLDER));
		this.setSizeFull();
        this.setMargin(true);
        this.setSpacing(true);
        this.addStyleName("dashboard-view");
        Responsive.makeResponsive(this);
        
        Label title = new Label("Seleccione una Categoría...");
        title.setStyleName(ValoTheme.LABEL_LARGE);
        title.addStyleName(ValoTheme.LABEL_COLORED);
        this.addComponent(title);
        
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
	        
	        for(CategoriaEntity categoria:categorias){
	        	if(competicion!=null){
	        		ParticipanteEntity rec = (ParticipanteEntity) BltClient.get().executeCommand(
		        		"/countByCompeticionAndCategoria/"+competicion+"/"+categoria.getId(), 
		        		ParticipanteEntity.class, EntryPoint.get().getAccessControl().getTokenKey());
	        		
	        		if(rec.getId()>0){
		        		Sparkline layout = new Sparkline(
		        			categoria.getNombre(), rec.getId(),
		            		FontAwesome.PAPERCLIP, categoria.getId());
			        	layout.addClickListener(action);
			        	sparks.addComponent(layout);
	        		}
	        	}
	        	else{
	        		Sparkline layout = new Sparkline(
		        			categoria.getNombre(),
		            		FontAwesome.CHILD, categoria.getId());
		        	layout.addClickListener(action);
		        	sparks.addComponent(layout);
	        	}
	        }
    	}
		catch(Exception e){
			//log.error(e.getMessage());
			e.printStackTrace();
		}
	}
	
}
