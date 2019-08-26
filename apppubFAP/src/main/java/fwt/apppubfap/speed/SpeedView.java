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
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import freewill.nextgen.common.bltclient.BltClient;
import fwt.apppubfap.dtos.SpeedTimeTrialEntity;
import fwt.apppubfap.dtos.CompeticionEntity;
import fwt.apppubfap.dtos.ParticipanteEntity;
import fwt.apppubfap.dtos.SpeedKOSystemEntity;
import fwt.apppubfap.dtos.SpeedKOSystemEntity.EliminatoriaEnum;
import fwt.apppubfap.SelectCategoria;
import fwt.apppubfap.authentication.CurrentUser;
import fwt.apppubfap.dtos.CategoriaEntity;
import fwt.apppubfap.dtos.CategoriaEntity.ModalidadEnum;

@SuppressWarnings("serial")
public class SpeedView extends VerticalLayout {
	
	private FeederThread thread;
	private Grid<SpeedTimeTrialEntity> grid1 = null;
	private ArbolKOSystem grid2 = null;
	private Grid<ParticipanteEntity> grid3 = null;
	private CompeticionEntity competicion = null;
	private CategoriaEntity categoria = null;
	private SelectCategoria selectCategoria = null;
	private String currentToken = "";
	private VerticalLayout barAndGridLayout = null;
	private EliminatoriaEnum eliminatoria = null;

	public SpeedView(CompeticionEntity competicion){
		this.competicion = competicion;
		this.setSizeFull();
		this.setSpacing(false);
		this.setMargin(false);
		this.setPadding(false);
        
        selectCategoria = new SelectCategoria(competicion,
        		ModalidadEnum.SPEED, e -> {
        			Optional<String> id = e.getSource().getId();
        			categoria = selectCategoria.getCategoria(id.get());
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
        grid1.setColumns("dorsal", "nombre", "apellidos");
        
        grid1.addColumn(new ComponentRenderer<>(rec -> {
        	if(rec.getTiempoAjustado1()>99999)
            	return new Label("Nulo");
            else
            	return new Label(""+rec.getTiempoAjustado1());
        })).setHeader("Tiempo R#1").setSortable(true);
        
        grid1.addColumn(new ComponentRenderer<>(rec -> {
        	if(rec.getTiempoAjustado2()>99999)
            	return new Label("Nulo");
            else
            	return new Label(""+rec.getTiempoAjustado2());
        })).setHeader("Tiempo R#2").setSortable(true);
        
        grid1.addColumn(new ComponentRenderer<>(rec -> {
        	if(rec.getMejorTiempo()>99999)
            	return new Label("Nulo");
            else
            	return new Label(""+rec.getMejorTiempo());
        })).setHeader("Mejor Tiempo").setSortable(true);
        
        grid1.addColumn("clasificacion");
        
        grid3 = new Grid<>(ParticipanteEntity.class);
        grid3.setWidth("100%");
        grid3.setColumns("dorsal", "nombre", "apellidos", "mejorMarca");
        grid3.addColumn(new ComponentRenderer<>(rec -> {
        	if(rec.getClasificacion()>990)
            	return new Label("No Presentado");
            else
            	return new Label(""+rec.getClasificacion());
        })).setHeader("Clasificación").setSortable(true);
        
        eliminatoria = existeKO(competicion.getId(), categoria.getId());
        if(eliminatoria!=null)
        	grid2 = new ArbolKOSystem(eliminatoria);
        
        Image icon = new Image("images/speed.png", "Speed");
		icon.setHeight("20px");
        
        Button title = new Button(categoria.getNombre()+"/"+competicion.getNombre());
        title.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        //title.setIcon(icon);
        title.setWidth("100%");
        
        Tab tab1 = new Tab("Time Trial");
        Tab tab2 = new Tab("KO System");
        Tab tab3 = new Tab("Clasificación");
        Tabs tabs = new Tabs();
	    tabs.add(tab1, tab2, tab3);
	    tabs.setSelectedTab(tab1);
        
        barAndGridLayout = new VerticalLayout();
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
	        	if(grid2!=null && barAndGridLayout.indexOf(grid2)>-1)
	        		barAndGridLayout.remove(grid2);
	        	if(barAndGridLayout.indexOf(grid3)>-1)
	        		barAndGridLayout.remove(grid3);
	        	barAndGridLayout.add(grid1);
	        	barAndGridLayout.setFlexGrow(1, grid1);
	        }
	        else if(tabs.getSelectedTab()==tab3){
	        	if(grid2!=null && barAndGridLayout.indexOf(grid2)>-1)
	        		barAndGridLayout.remove(grid2);
	        	if(barAndGridLayout.indexOf(grid1)>-1)
	        		barAndGridLayout.remove(grid1);
	        	barAndGridLayout.add(grid3);
	        	barAndGridLayout.setFlexGrow(1, grid3);
	        }
	        else{
	        	if(barAndGridLayout.indexOf(grid1)>-1)
	        		barAndGridLayout.remove(grid1);
	        	if(barAndGridLayout.indexOf(grid3)>-1)
	        		barAndGridLayout.remove(grid3);
	        	if(eliminatoria!=null && grid2!=null){
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
			List<SpeedTimeTrialEntity> recs1 = BltClient.get().executeQuery(
        			"/getResultadosRT/"+competicion.getId()+"/"+categoria.getId(),
        			SpeedTimeTrialEntity.class,
        			currentToken);
	    	grid1.setItems(recs1);
	    	
	    	if(eliminatoria!=null && grid2!=null){
	    		List<SpeedKOSystemEntity> recs2 = BltClient.get().executeQuery(
		    		"/findByCompeticionAndCategoriaAndEliminatoria/"+
		    				competicion.getId()+"/"+categoria.getId()+"/"+eliminatoria.name(),
					SpeedKOSystemEntity.class,
		    		currentToken);
	    		grid2.setItems(recs2);
	    	}
	    	
	    	List<ParticipanteEntity> recs3 = BltClient.get().executeQuery(
        			"/getResultados/"+competicion.getId()+"/"+categoria.getId(),
        			ParticipanteEntity.class,
        			currentToken);
	    	grid3.setItems(recs3);
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
	
	private EliminatoriaEnum existeKO(Long competicion, Long categoria) {
		try{
			SpeedKOSystemEntity rec = (SpeedKOSystemEntity) BltClient.get().executeCommand(
		    		"/existByCompeticionAndCategoria/"+competicion+"/"+categoria,
					SpeedKOSystemEntity.class,
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
