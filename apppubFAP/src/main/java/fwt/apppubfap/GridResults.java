package fwt.apppubfap;

import java.util.List;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import fwt.apppubfap.dtos.ParticipanteEntity;

@SuppressWarnings("serial")
public class GridResults extends Composite<Div> {
	
	private Grid<ParticipanteEntity> grid1 = null;
	
	public GridResults() {
		
		grid1 = new Grid<>(ParticipanteEntity.class);
		grid1.setWidth("100%");
		grid1.setColumns("dorsal");
	    grid1.addColumn(new ComponentRenderer<>(rec -> {
	        	if(rec.getClasificacion()>990)
	            	return new Label("");
	            else
	            	return new Label(""+rec.getClasificacion());
	        })).setHeader("Clasificación").setSortable(true);
	    grid1.addColumns("nombre", "apellidos");
	    grid1.addColumn(new ComponentRenderer<>(rec -> {
	        	if(rec.getDorsal()==0)
	            	return new Label("No Presentado");
	            else
	            	return new Label(""+rec.getDorsal());
	        })).setHeader("Dorsal").setSortable(false);
        grid1.getColumnByKey("apellidos").setWidth("200px");
        grid1.getColumnByKey("nombre").setSortable(false);
        grid1.getColumnByKey("apellidos").setSortable(false);
        grid1.getColumnByKey("dorsal").setVisible(false);
        
        getContent().add(grid1);
        getContent().setSizeFull();
        
	}

	public void setItems(List<ParticipanteEntity> recs) {
		grid1.setItems(recs);
	}
	
}

