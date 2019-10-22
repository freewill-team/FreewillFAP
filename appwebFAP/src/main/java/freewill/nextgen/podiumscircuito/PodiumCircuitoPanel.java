package freewill.nextgen.podiumscircuito;

import java.util.List;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.data.RankingEntity;

@SuppressWarnings("serial")
public class PodiumCircuitoPanel extends VerticalLayout {
    
    public PodiumCircuitoPanel(final String name, List<RankingEntity> resultados) {
    	this.setSizeUndefined();
        this.setStyleName("spark");
        this.setMargin(false);
    	this.setSpacing(false);
    	this.setDefaultComponentAlignment(Alignment.TOP_LEFT); //TOP_CENTER);
    	
    	Label title = new Label(name);
        title.setSizeUndefined();
        title.setStyleName(ValoTheme.LABEL_H4);
        title.addStyleName(ValoTheme.LABEL_BOLD);
        this.addComponent(title);
        
    	for(RankingEntity rec:resultados){
    		String patin = ""+rec.getOrden()+".- "+
    				rec.getNombre()+" "+rec.getApellidos()+
    				" ("+rec.getPuntuacion()+")";
    		if(name.toUpperCase().contains("JAM "))
    			patin = patin + " / " + 
    				rec.getNombrePareja()+" "+rec.getApellidosPareja()+
    				" ("+rec.getPuntuacion()+")";
    		
    		Button label = new Button(patin);
	        label.setStyleName(ValoTheme.BUTTON_TINY);
	        label.addStyleName(ValoTheme.BUTTON_BORDERLESS);
	        if(!name.toUpperCase().contains("JAM "))
	        	label.addStyleName("truncate");
    		label.setWidth("100%");
    		//label.setSizeUndefined();
    		label.setEnabled(false);
        	this.addComponent(label);
        	
    		if(rec.getOrden()<=3){
    			label.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
    			label.setIcon(FontAwesome.TROPHY);
    		}
    	}
    }
    
}
