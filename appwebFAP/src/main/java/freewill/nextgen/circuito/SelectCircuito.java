package freewill.nextgen.circuito;

import com.vaadin.ui.themes.ValoTheme;

import java.util.Date;
import java.util.List;

import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.Responsive;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ComboBox;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.data.CircuitoEntity;

@SuppressWarnings("serial")
public class SelectCircuito extends HorizontalLayout {
	
	ValueChangeListener action = null;
	ComboBox combo = null;
	
	@SuppressWarnings("deprecation")
	public SelectCircuito(){
		this.setWidth("100%"); //
        this.setMargin(true);
        this.setSpacing(true);
        this.addStyleName("dashboard-view");
        Responsive.makeResponsive(this);
        
        Label title = new Label("Seleccione un Circuito...");
        title.setStyleName(ValoTheme.LABEL_LARGE);
        title.addStyleName(ValoTheme.LABEL_COLORED);
        //this.addComponent(title);
        
        combo = new ComboBox();
        combo.setImmediate(true);
        combo.setNullSelectionAllowed(false);
        combo.setWidth("100%");
        //this.addComponent(combo);
        
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.addStyleName("sparks");
        layout.addComponent(title);
        layout.addComponent(combo);
        layout.setExpandRatio(title, 1);
        layout.setExpandRatio(combo, 2);
        
        this.addComponent(layout);
        
        try{
        	// Rellena Combobox
        	List<CircuitoEntity> circuitos = BltClient.get().getEntities(
        			CircuitoEntity.class, 
	        		EntryPoint.get().getAccessControl().getTokenKey());
	        for(CircuitoEntity rec:circuitos){
	        	combo.addItem(rec.getId());
	        	combo.setItemCaption(rec.getId(), rec.getNombre());
	        }
	        
	        // Selecciona registro correspondiente al año actual, si existe
	        Date now = new Date();
        	CircuitoEntity circuito = (CircuitoEntity) BltClient.get().executeCommand(
        			"/getCircuito/"+(now.getYear()+1900), CircuitoEntity.class, 
        			EntryPoint.get().getAccessControl().getTokenKey());
        	if(circuito!=null)
        		combo.setValue(circuito.getId());
    	}
		catch(Exception e){
			//log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public Long getValue() {
		return (Long)combo.getValue();
	}
	
	public String getCaption() {
		return combo.getItemCaption(combo.getValue());
	}

	public void addAction(ValueChangeListener action) {
		combo.addValueChangeListener(action);
	}
	
}
