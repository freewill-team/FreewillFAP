package fwt.apppubfap;

import java.util.HashMap;
import java.util.List;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import freewill.nextgen.common.bltclient.BltClient;
import fwt.apppubfap.dtos.CategoriaEntity;
import fwt.apppubfap.authentication.CurrentUser;
import fwt.apppubfap.dtos.CategoriaEntity.ModalidadEnum;
import fwt.apppubfap.dtos.CompeticionEntity;
import fwt.apppubfap.dtos.ParticipanteEntity;

@SuppressWarnings("serial")
public class SelectCategoria extends VerticalLayout {
	
	private ModalidadEnum modalidad = ModalidadEnum.SLIDE;
	private ComponentEventListener<ClickEvent<Button>> listener = null;
	private CompeticionEntity competicion = null;
	private HashMap<String, CategoriaEntity> map = new HashMap<String, CategoriaEntity>();
	
	public SelectCategoria(CompeticionEntity competicion, ModalidadEnum modalidad,
			 ComponentEventListener<ClickEvent<Button>> listener){
		this.setSizeFull();
		this.setMargin(false);
		this.setSpacing(true);
		this.setPadding(false);
		this.setDefaultHorizontalComponentAlignment(Alignment.CENTER);
		
		this.modalidad = modalidad;
		this.listener = listener;
		
		// Obtiene ultima competicion correspondiente al año actual
		if(competicion==null)
			competicion = findCompeticion();
		if(competicion==null){
			add(new Label("Ultima competición no disponible."));
			return;
		}
		
		Button title = new Button(competicion.getNombre());
        title.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        //title.setIcon(icon);
        title.setWidth("100%");
        add(title);
		
		try{
			// Crea un boton por cada categoria	
			List<CategoriaEntity> categorias = BltClient.get().executeQuery(
			        "/getByModalidad/"+this.modalidad.name(), CategoriaEntity.class,
			        CurrentUser.getTokenKey());
			
			int numcategorias = 0;
			for(CategoriaEntity cat:categorias){
		        ParticipanteEntity rec = (ParticipanteEntity) BltClient.get().executeCommand(
			        "/countByCompeticionAndCategoria/"+competicion.getId()+"/"+cat.getId(), 
			        ParticipanteEntity.class, CurrentUser.getTokenKey());
		        
		        if(rec.getId()>0){
		        	Button btn = new Button(cat.getNombre());
		        	btn.setWidthFull();
		        	btn.addThemeVariants(ButtonVariant.LUMO_LARGE);
		        	btn.setId(""+cat.getId());
					btn.addClickListener(this.listener);
					add(btn);
					map.put(""+cat.getId(), cat);
					numcategorias++;
		        }
			}
			
			if(numcategorias==0){
	        	add(new Label("No hay categorias con patinadores para mostrar."));
				return;
			}
		}
		catch(Exception e){
			//log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	private CompeticionEntity findCompeticion() {
		try{
	        // Selecciona registro correspondiente a la ultima competicion
	        CompeticionEntity competi = (CompeticionEntity) BltClient.get().executeCommand(
        			"/getLastCompeticion", CompeticionEntity.class,
        			CurrentUser.getTokenKey());
        	return competi;
    	}
		catch(Exception e){
			//log.error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
	
	public CategoriaEntity getCategoria(String id){
		return map.get(id);
	}

	public CompeticionEntity getCompeticion(){
		return competicion;
	}
	
}
