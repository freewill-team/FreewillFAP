package fwt.apppubfap.classic;

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
import fwt.apppubfap.dtos.ClassicShowEntity;
import fwt.apppubfap.dtos.CompeticionEntity;
import fwt.apppubfap.dtos.ParticipanteEntity;
import fwt.apppubfap.SelectCategoria;
import fwt.apppubfap.authentication.CurrentUser;
import fwt.apppubfap.dtos.CategoriaEntity;
import fwt.apppubfap.dtos.CategoriaEntity.ModalidadEnum;

@SuppressWarnings("serial")
public class ClassicView extends VerticalLayout {
	
	private FeederThread thread;
	private Grid<ClassicShowEntity> grid2 = null;
	private Grid<ParticipanteEntity> grid1 = null;
	private CompeticionEntity competicion = null;
	private CategoriaEntity categoria = null;
	private SelectCategoria selectCategoria = null;
	private String currentToken = "";

	public ClassicView(CompeticionEntity competicion){
		this.competicion = competicion;
		this.setSizeFull();
		this.setSpacing(false);
		this.setMargin(false);
		this.setPadding(false);
        
        selectCategoria = new SelectCategoria(competicion,
        		ModalidadEnum.CLASSIC, e -> {
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
		
		grid1 = new Grid<>(ParticipanteEntity.class);
        grid1.setWidth("100%");
        grid1.setColumns("dorsal", "nombre", "apellidos");
        grid1.addColumn(new ComponentRenderer<>(rec -> {
        	if(rec.getClasificacion()>990)
            	return new Label("No Presentado");
            else
            	return new Label(""+rec.getClasificacion());
        })).setHeader("Clasificación").setSortable(true);
        
		grid2 = new Grid<>(ClassicShowEntity.class);
        grid2.setWidth("100%");
        grid2.setColumns("dorsal", "nombre", "apellidos",
        		"totalTecnica", "penalizaciones", 
        		"sumaPV", "PVLocales", "totalTecnica", "PVTotal", "puntuacionTotal",
        		"clasificacionFinal");
        
        Image icon = new Image("images/classic.png", "Classic");
		icon.setHeight("20px");
        
        Button title = new Button(categoria.getNombre()+"/"+competicion.getNombre());
        title.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        //title.setIcon(icon);
        title.setWidth("100%");
        
        Tab tab1 = new Tab("Clasificación");
        Tab tab2 = new Tab("Puntuaciones");
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
	            if(grid2!=null){
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
	    	
			List<ClassicShowEntity> recs2 = BltClient.get().executeQuery(
        			"/getResultadosRT/"+competicion.getId()+"/"+categoria.getId(),
        			ClassicShowEntity.class,
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
        private final ClassicView view;

        public FeederThread(UI ui, ClassicView view) {
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
