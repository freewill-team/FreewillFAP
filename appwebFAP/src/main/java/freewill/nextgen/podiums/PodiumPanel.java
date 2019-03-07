package freewill.nextgen.podiums;

import java.text.DecimalFormat;
import java.util.List;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.data.ParticipanteEntity;

@SuppressWarnings("serial")
public class PodiumPanel extends VerticalLayout {
    
    public PodiumPanel(final String name, List<ParticipanteEntity> results) {
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
        
    	for(ParticipanteEntity rec:results){
    		String patin = ""+rec.getClasificacion()+".- "+
    				rec.getNombre()+" "+rec.getApellidos();
    		if(name.toUpperCase().contains("JAM "))
    			patin = patin + " / " + 
    				rec.getNombrePareja()+" "+rec.getApellidosPareja();
    		
    		Button label = new Button(patin);
	        label.setStyleName(ValoTheme.BUTTON_TINY);
	        label.addStyleName(ValoTheme.BUTTON_BORDERLESS);
    		label.setWidth("100%");
    		//label.setSizeUndefined();
    		label.setEnabled(false);
        	this.addComponent(label);
        	
    		if(rec.getClasificacion()<=3){
    			label.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
    			label.setIcon(FontAwesome.TROPHY);
    		}
    	}
    }
    
}