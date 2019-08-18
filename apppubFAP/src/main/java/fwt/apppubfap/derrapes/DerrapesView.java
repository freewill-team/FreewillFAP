package fwt.apppubfap.derrapes;

import java.util.List;
import java.util.Optional;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import freewill.nextgen.common.bltclient.BltClient;
import fwt.apppubfap.dtos.ParticipanteEntity;
import fwt.apppubfap.dtos.CompeticionEntity;
import fwt.apppubfap.SelectCategoria;
import fwt.apppubfap.authentication.CurrentUser;
import fwt.apppubfap.dtos.CategoriaEntity;
import fwt.apppubfap.dtos.CategoriaEntity.ModalidadEnum;

@SuppressWarnings("serial")
public class DerrapesView extends VerticalLayout {
	
	private FeederThread thread;
	private Grid<ParticipanteEntity> grid = null;
	private CompeticionEntity competicion = null;
	private CategoriaEntity categoria = null;
	private SelectCategoria selectCategoria = null;
	private String currentToken = "";

	public DerrapesView(){
		this.setSizeFull();
		this.setSpacing(false);
		this.setMargin(false);
        
        selectCategoria = new SelectCategoria(
        		ModalidadEnum.SLIDE, e -> {
        			Optional<String> id = e.getSource().getId();
        			//Long catId = Long.parseLong(id.get());
        			categoria = selectCategoria.getCategoria(id.get());
        			competicion = selectCategoria.getCompeticion();
        			removeAll();
        			add(showResults());
        	        Refresh();
        			// Start the data feed thread
        			thread = new FeederThread(UI.getCurrent(), this);
        			thread.start();
        		});
        		
        add(selectCategoria);
        currentToken = CurrentUser.getTokenKey();
    }

	private Component showResults() {
		
		grid = new Grid<>(ParticipanteEntity.class);
        grid.setWidth("100%");
        grid.setColumns("dorsal", "nombre", "apellidos", "clasificacion");
        //grid.getColumnByKey("id").setWidth("40px");
        
        Image icon = new Image("images/derrapes.png", "Derrapes");
		icon.setHeight("20px");
        
        Button title = new Button(categoria.getNombre()+"/"+competicion.getNombre());
        title.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        //title.setIcon(icon);
        title.setWidth("100%");
        
        VerticalLayout barAndGridLayout = new VerticalLayout();
        barAndGridLayout.add(title);
        barAndGridLayout.add(grid);
        barAndGridLayout.setMargin(false);
        barAndGridLayout.setSpacing(false);
        barAndGridLayout.setSizeFull();
        barAndGridLayout.setWidth("100%");
        barAndGridLayout.setFlexGrow(1, grid);
        
		return barAndGridLayout;
	}

	public void Refresh() {
		try{
			List<ParticipanteEntity> recs = BltClient.get().executeQuery(
        			"/getResultados/"+competicion.getId()+"/"+categoria.getId(),
        			ParticipanteEntity.class,
        			currentToken);
	    	grid.setItems(recs);
		}
		catch(Exception e){
			e.printStackTrace();
			showNotification(e.getMessage());
		}
    }

	public void showNotification(String msg) {
        Notification.show(msg);
    }
	
	@Override
    protected void onDetach(DetachEvent detachEvent) {
        // Cleanup
		if(thread!=null && thread.isAlive())
			thread.interrupt();
        thread = null;
    }
	
	private static class FeederThread extends Thread {
        private final UI ui;
        private final DerrapesView view;

        public FeederThread(UI ui, DerrapesView view) {
            this.ui = ui;
            this.view = view;
        }

        @Override
        public void run() {
            try {
                // Update the data for a while
                while (true) {
                    // Sleep to emulate background work
                    Thread.sleep(30000);
                    ui.access(() -> view.Refresh());
                }
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }
        }
    }
	
}
