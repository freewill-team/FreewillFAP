package freewill.nextgen.competicion;

import com.vaadin.ui.themes.ValoTheme;

import java.util.List;

import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.Responsive;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ComboBox;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.data.CompeticionEntity;

@SuppressWarnings("serial")
public class SelectCompeticionCombo extends HorizontalLayout {
	
	ValueChangeListener action = null;
	ComboBox combo = null;
	
	public SelectCompeticionCombo(){
		this.setWidth("100%"); //
        this.setMargin(true);
        this.setSpacing(true);
        this.addStyleName("dashboard-view");
        Responsive.makeResponsive(this);
        
        Label title = new Label("Seleccione una Competicion...");
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
        	List<CompeticionEntity> competis = BltClient.get().getEntities(
        			CompeticionEntity.class, 
	        		EntryPoint.get().getAccessControl().getTokenKey());
	        for(CompeticionEntity rec:competis){
	        	combo.addItem(rec.getId());
	        	combo.setItemCaption(rec.getId(), rec.getNombre());
	        }
	        
	        // Selecciona registro correspondiente a la ultima competicion
	        CompeticionEntity competi = (CompeticionEntity) BltClient.get().executeCommand(
        			"/getLastCompeticion", CompeticionEntity.class,
        			EntryPoint.get().getAccessControl().getTokenKey());
        	if(competi!=null)
        		combo.setValue(competi.getId());
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
