package fwt.apppubfap;

import java.util.List;

import com.vaadin.flow.component.combobox.ComboBox;

import freewill.nextgen.common.bltclient.BltClient;
import fwt.apppubfap.authentication.CurrentUser;
import fwt.apppubfap.dtos.CompeticionEntity;

@SuppressWarnings("serial")
public class SelectCompeticion extends ComboBox<CompeticionEntity> {
	
	public SelectCompeticion(String caption){
		super(caption);
		this.setWidth("100%");
		this.setAllowCustomValue(false);
		this.setPreventInvalidInput(true);
		this.setRequired(true);
        try{
        	// Define caption
        	this.setItemLabelGenerator(CompeticionEntity::getNombre);
        	
        	// Rellena Combobox
        	List<CompeticionEntity> competis = BltClient.get().getEntities(
        			CompeticionEntity.class, 
	        		CurrentUser.getTokenKey());
	        this.setItems(competis);
	        
	        // Selecciona registro correspondiente a la ultima competicion
	        CompeticionEntity competi = (CompeticionEntity) BltClient.get().executeCommand(
        			"/getLastCompeticion", CompeticionEntity.class,
        			CurrentUser.getTokenKey());
        	if(competi!=null){
        		this.setValue(competi);
        	}
    	}
		catch(Exception e){
			//log.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public CompeticionEntity getCompeticion() {
		return (CompeticionEntity)this.getValue();
	}
	
}
