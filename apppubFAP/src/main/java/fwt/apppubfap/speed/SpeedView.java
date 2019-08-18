package fwt.apppubfap.speed;

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
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;

import freewill.nextgen.common.bltclient.BltClient;
import fwt.apppubfap.dtos.SpeedTimeTrialEntity;
import fwt.apppubfap.dtos.CompeticionEntity;
import fwt.apppubfap.dtos.ParticipanteEntity;
import fwt.apppubfap.SelectCategoria;
import fwt.apppubfap.authentication.CurrentUser;
import fwt.apppubfap.dtos.CategoriaEntity;
import fwt.apppubfap.dtos.CategoriaEntity.ModalidadEnum;

@SuppressWarnings("serial")
public class SpeedView extends VerticalLayout {
	
	private FeederThread thread;
	private Grid<SpeedTimeTrialEntity> grid1 = null;
	private Grid<ParticipanteEntity> grid2 = null;
	private CompeticionEntity competicion = null;
	private CategoriaEntity categoria = null;
	private SelectCategoria selectCategoria = null;
	private String currentToken = "";

	public SpeedView(){
		this.setSizeFull();
		this.setSpacing(false);
		this.setMargin(false);
        
        selectCategoria = new SelectCategoria(
        		ModalidadEnum.SPEED, e -> {
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
		
		grid1 = new Grid<>(SpeedTimeTrialEntity.class);
        grid1.setWidth("100%");
        grid1.setColumns("dorsal", "nombre", "apellidos", "tiempoAjustado1", "tiempoAjustado2", "mejorTiempo", "clasificacion");
        //grid.getColumnByKey("id").setWidth("40px");
        
        grid2 = new Grid<>(ParticipanteEntity.class);
        grid2.setWidth("100%");
        grid2.setColumns("dorsal", "nombre", "apellidos", "clasificacion");
        
        Image icon = new Image("images/speed.png", "Speed");
		icon.setHeight("20px");
        
        Button title = new Button(categoria.getNombre()+"/"+competicion.getNombre());
        title.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        //title.setIcon(icon);
        title.setWidth("100%");
        
        Tab tab1 = new Tab("Time Trial");
        tab1.add(grid1);
        Tab tab2 = new Tab("KO System");
        tab2.add(grid2);
        Tabs tabs = new Tabs();
	    tabs.add(tab1, tab2);
	    tabs.setSelectedTab(tab1);
        
        VerticalLayout barAndGridLayout = new VerticalLayout();
        barAndGridLayout.add(title);
        barAndGridLayout.add(tabs);
        barAndGridLayout.setMargin(false);
        barAndGridLayout.setSpacing(false);
        barAndGridLayout.setSizeFull();
        barAndGridLayout.setWidth("100%");
        barAndGridLayout.setFlexGrow(1, tabs);
        
		return barAndGridLayout;
	}

	public void Refresh() {
		try{
			//System.out.println("Updating grid...");
			List<SpeedTimeTrialEntity> recs1 = BltClient.get().executeQuery(
        			"/getResultadosRT/"+competicion.getId()+"/"+categoria.getId(),
        			SpeedTimeTrialEntity.class,
        			currentToken);
	    	grid1.setItems(recs1);
	    	
	    	List<ParticipanteEntity> recs2 = BltClient.get().executeQuery(
        			"/getResultados/"+competicion.getId()+"/"+categoria.getId(),
        			ParticipanteEntity.class,
        			currentToken);
	    	grid2.setItems(recs2);
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
        private final SpeedView view;

        public FeederThread(UI ui, SpeedView view) {
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
