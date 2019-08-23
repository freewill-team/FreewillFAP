package fwt.apppubfap.battle;

import java.util.List;
import java.util.Optional;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import freewill.nextgen.common.bltclient.BltClient;
import fwt.apppubfap.dtos.CompeticionEntity;
import fwt.apppubfap.dtos.BattleRondaEntity;
import fwt.apppubfap.dtos.BattleRondaEntity.EliminatoriaEnum;
import fwt.apppubfap.dtos.ParticipanteEntity;
import fwt.apppubfap.SelectCategoria;
import fwt.apppubfap.authentication.CurrentUser;
import fwt.apppubfap.dtos.CategoriaEntity;
import fwt.apppubfap.dtos.CategoriaEntity.ModalidadEnum;

@SuppressWarnings("serial")
public class BattleView extends VerticalLayout {
	
	private FeederThread thread;
	private Grid<ParticipanteEntity> grid1 = null;
	private ArbolBattle grid2 = null;
	private CompeticionEntity competicion = null;
	private CategoriaEntity categoria = null;
	private SelectCategoria selectCategoria = null;
	private String currentToken = "";
	private EliminatoriaEnum eliminatoria = null;

	public BattleView(){
		this.setSizeFull();
		this.setSpacing(false);
		this.setMargin(false);
		this.setPadding(false);
        
        selectCategoria = new SelectCategoria(
        		ModalidadEnum.BATTLE, e -> {
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
		
		grid1 = new Grid<>(ParticipanteEntity.class);
        grid1.setWidth("100%");
        grid1.setColumns("dorsal", "nombre", "apellidos");
        grid1.addColumn(new ComponentRenderer<>(rec -> {
        	if(rec.getClasificacion()>990)
            	return new Label("No Presentado");
            else
            	return new Label(""+rec.getClasificacion());
        })).setHeader("Clasificación");
		
		eliminatoria = existeKO(competicion.getId(), categoria.getId());
        if(eliminatoria!=null)
        	grid2 = new ArbolBattle(eliminatoria);
        
        Image icon = new Image("images/battle.png", "Battle");
		icon.setHeight("20px");
        
        Button title = new Button(categoria.getNombre()+"/"+competicion.getNombre());
        title.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        //title.setIcon(icon);
        title.setWidth("100%");
        
        Tab tab1 = new Tab("Clasificación");
        Tab tab2 = new Tab("Enfrentamientos");
        Tabs tabs = new Tabs();
	    tabs.add(tab1, tab2);
	    tabs.setSelectedTab(tab1);
        
        VerticalLayout barAndGridLayout = new VerticalLayout();
        barAndGridLayout.add(title);
        barAndGridLayout.add(tabs);
        barAndGridLayout.add(grid1);
        barAndGridLayout.setMargin(false);
        barAndGridLayout.setSpacing(true);
        barAndGridLayout.setPadding(false);
        barAndGridLayout.setSizeFull();
        barAndGridLayout.setWidth("100%");
        barAndGridLayout.setFlexGrow(1, grid1);
        
        tabs.addSelectedChangeListener(event -> {
	        if(tabs.getSelectedTab()==tab1){
	        	if(grid2!=null)
	        		barAndGridLayout.remove(grid2);
	        	barAndGridLayout.add(grid1);
	        	barAndGridLayout.setFlexGrow(1, grid1);
	        }
	        else{
	        	barAndGridLayout.remove(grid1);
	        	//eliminatoria = existeKO(competicion.getId(), categoria.getId());
	            if(eliminatoria!=null && grid2!=null){
	            	//grid2 = new ArbolKOSystem(eliminatoria);
	            	barAndGridLayout.add(grid2);
	            	barAndGridLayout.setFlexGrow(1, grid2);
	            }
	        }
	    });
        
		return barAndGridLayout;
	}

	public void Refresh() {
		try{
			//System.out.println("Updating grid...");
			List<ParticipanteEntity> recs1 = BltClient.get().executeQuery(
        			"/getResultados/"+competicion.getId()+"/"+categoria.getId(),
        			ParticipanteEntity.class,
        			currentToken);
	    	grid1.setItems(recs1);
	    	
	    	if(eliminatoria!=null && grid2!=null){
				List<BattleRondaEntity> recs2 = BltClient.get().executeQuery(
	        			"/findByCompeticionAndCategoria/"+competicion.getId()+"/"+categoria.getId(),
	        			BattleRondaEntity.class,
	        			currentToken);
		    	grid2.setItems(recs2);
	    	}
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
        private final BattleView view;

        public FeederThread(UI ui, BattleView view) {
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
	
	public EliminatoriaEnum existeKO(Long competicion, Long categoria) {
		try{
			BattleRondaEntity rec = (BattleRondaEntity) BltClient.get().executeCommand(
		    		"/existByCompeticionAndCategoria/"+competicion+"/"+categoria,
					BattleRondaEntity.class,
					currentToken);
			return rec.getEliminatoria();
		}
		catch(Exception e){
			e.printStackTrace();
			showNotification(e.getMessage());
		}
		return null;
	}
	
}
