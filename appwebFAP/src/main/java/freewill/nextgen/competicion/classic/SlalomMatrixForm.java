package freewill.nextgen.competicion.classic;

import java.util.Collection;

import com.vaadin.server.Page;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.data.ClassicShowEntity;
import freewill.nextgen.data.SlalomTrickEntity;
import freewill.nextgen.data.SlalomTrickEntity.TrickFamilyEnum;
import freewill.nextgen.genericCrud.GenericGrid;

/**
 * A form for editing a single record.
 *
 * Using responsive layouts, the form can be displayed either sliding out on the
 * side of the view or filling the whole screen - see the theme for the related
 * CSS rules.
 */
@SuppressWarnings("serial")
public class SlalomMatrixForm extends SlalomMatrixFormDesign {
	
    //private ClassicCrudLogic viewLogic;
    //private ClassicShowEntity classicShow = null;
    private GenericGrid<SlalomTrickEntity> grid = null;
    private int points = 0;
    
    public SlalomMatrixForm(/*ClassicCrudLogic logic*/) {
        super();
        addStyleName("product-form");
        //this.viewLogic = logic;

        delete.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	SlalomTrickEntity rec = grid.getSelectedRow();
            	if(rec!=null){
            		points -= rec.getValor();
            		grid.remove(rec);
            		puntuacion.setValue(""+points);
            	}
            }
        });
        
        cancel.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	close();
            }
        });
        
        try{
        	// Rellenar ComboBox elasticidad
        	elasticidad.removeAllItems();
            Collection<SlalomTrickEntity> recs = BltClient.get().executeQuery(
            		"/getbyfamily/"+TrickFamilyEnum.ELASTICIDAD.name(),
            		SlalomTrickEntity.class,
    				EntryPoint.get().getAccessControl().getTokenKey());
    		for (SlalomTrickEntity s : recs) {
    			elasticidad.addItem(s);
    			elasticidad.setItemCaption(s, s.getNombre()+" ("+s.getValor()+")");
    	    }
        }
        catch(Exception e){
        	e.printStackTrace();
        	Notification.show("Error: "+e.getMessage(), Type.ERROR_MESSAGE);
        }
        
        try{
        	// Rellenar ComboBox sentados
        	sentados.removeAllItems();
            Collection<SlalomTrickEntity> recs = BltClient.get().executeQuery(
            		"/getbyfamily/"+TrickFamilyEnum.SENTADOS.name(),
            		SlalomTrickEntity.class,
    				EntryPoint.get().getAccessControl().getTokenKey());
    		for (SlalomTrickEntity s : recs) {
    			sentados.addItem(s);
    			sentados.setItemCaption(s, s.getNombre()+" ("+s.getValor()+")");
    	    }
        }
        catch(Exception e){
        	e.printStackTrace();
        	Notification.show("Error: "+e.getMessage(), Type.ERROR_MESSAGE);
        }
        
        try{
        	// Rellenar ComboBox saltos
        	saltos.removeAllItems();
            Collection<SlalomTrickEntity> recs = BltClient.get().executeQuery(
            		"/getbyfamily/"+TrickFamilyEnum.SALTOS.name(),
            		SlalomTrickEntity.class,
    				EntryPoint.get().getAccessControl().getTokenKey());
    		for (SlalomTrickEntity s : recs) {
    			saltos.addItem(s);
    			saltos.setItemCaption(s, s.getNombre()+" ("+s.getValor()+")");
    	    }
        }
        catch(Exception e){
        	e.printStackTrace();
        	Notification.show("Error: "+e.getMessage(), Type.ERROR_MESSAGE);
        }
        
        try{
        	// Rellenar ComboBox lineales
        	lineales.removeAllItems();
            Collection<SlalomTrickEntity> recs = BltClient.get().executeQuery(
            		"/getbyfamily/"+TrickFamilyEnum.LINEALES.name(),
            		SlalomTrickEntity.class,
    				EntryPoint.get().getAccessControl().getTokenKey());
    		for (SlalomTrickEntity s : recs) {
    			lineales.addItem(s);
    			lineales.setItemCaption(s, s.getNombre()+" ("+s.getValor()+")");
    	    }
        }
        catch(Exception e){
        	e.printStackTrace();
        	Notification.show("Error: "+e.getMessage(), Type.ERROR_MESSAGE);
        }
        
        try{
        	// Rellenar ComboBox giros
        	giros.removeAllItems();
            Collection<SlalomTrickEntity> recs = BltClient.get().executeQuery(
            		"/getbyfamily/"+TrickFamilyEnum.GIROS.name(),
            		SlalomTrickEntity.class,
    				EntryPoint.get().getAccessControl().getTokenKey());
    		for (SlalomTrickEntity s : recs) {
    			giros.addItem(s);
    			giros.setItemCaption(s, s.getNombre()+" ("+s.getValor()+")");
    	    }
        }
        catch(Exception e){
        	e.printStackTrace();
        	Notification.show("Error: "+e.getMessage(), Type.ERROR_MESSAGE);
        }
        
        grid = new GenericGrid<SlalomTrickEntity>(
        		SlalomTrickEntity.class, "id", "nombre", "familia", "valor");
        grid.setHeight("240px");
        gridTricks.addComponent(grid);
        
        addElasticidad.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	SlalomTrickEntity rec = (SlalomTrickEntity) elasticidad.getValue();
            	if(rec!=null){
            		int pnts = rec.getValor();
            		if(continuidadElasticidad.getValue()) pnts++;
            		if(ritmoElasticidad.getValue()) pnts++;
            		if(footworkElasticidad.getValue()) pnts++;
            		if(limpiezaElasticidad.getValue()) pnts++;
            		rec.setValor(pnts);
            		grid.refresh(rec);
            		points += pnts;
            		puntuacion.setValue(""+points);
            	}
            }
        });
        
        addSentados.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	SlalomTrickEntity rec = (SlalomTrickEntity) sentados.getValue();
            	if(rec!=null){
            		int pnts = rec.getValor();
            		if(continuidadSentados.getValue()) pnts++;
            		if(ritmoSentados.getValue()) pnts++;
            		if(footworkSentados.getValue()) pnts++;
            		if(limpiezaSentados.getValue()) pnts++;
            		rec.setValor(pnts);
            		grid.refresh(rec);
            		points += pnts;
            		puntuacion.setValue(""+points);
            	}
            }
        });
        
        addSaltos.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	SlalomTrickEntity rec = (SlalomTrickEntity) saltos.getValue();
            	if(rec!=null){
            		int pnts = rec.getValor();
            		if(continuidadSaltos.getValue()) pnts++;
            		if(ritmoSaltos.getValue()) pnts++;
            		if(footworkSaltos.getValue()) pnts++;
            		if(limpiezaSaltos.getValue()) pnts++;
            		rec.setValor(pnts);
            		grid.refresh(rec);
            		points += pnts;
            		puntuacion.setValue(""+points);
            	}
            }
        });
        
        addLineales.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	SlalomTrickEntity rec = (SlalomTrickEntity) lineales.getValue();
            	if(rec!=null){
            		int pnts = rec.getValor();
            		if(continuidadLineales.getValue()) pnts++;
            		if(ritmoLineales.getValue()) pnts++;
            		if(footworkLineales.getValue()) pnts++;
            		if(limpiezaLineales.getValue()) pnts++;
            		rec.setValor(pnts);
            		grid.refresh(rec);
            		points += pnts;
            		puntuacion.setValue(""+points);
            	}
            }
        });
        
        addGiros.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	SlalomTrickEntity rec = (SlalomTrickEntity) giros.getValue();
            	if(rec!=null){
            		int pnts = rec.getValor();
            		if(continuidadGiros.getValue()) pnts++;
            		if(ritmoGiros.getValue()) pnts++;
            		if(footworkGiros.getValue()) pnts++;
            		if(limpiezaGiros.getValue()) pnts++;
            		rec.setValor(pnts);
            		grid.refresh(rec);
            		points += pnts;
            		puntuacion.setValue(""+points);
            	}
            }
        });
        
    }
    
    public void editRecord(/*ClassicShowEntity rec*/) {
        /*if (rec == null) {
            rec = new ClassicShowEntity();
            save.setEnabled(false);
            return;
        }*/
        //fieldGroup.setItemDataSource(new BeanItem<ClassicShowEntity>(rec));
        //classicShow = rec;
        points = 0;
		puntuacion.setValue(""+points);
		grid.setRecords(null);

        // before the user makes any changes, disable validation error indicator
        // of the product name field (which may be empty)
        //nombre.setValidationVisible(false);

        // Scroll to the top
        // As this is not a Panel, using JavaScript
        String scrollScript = "window.document.getElementById('" + getId() + "').scrollTop = 0;";
        Page.getCurrent().getJavaScript().execute(scrollScript);
        
        formHasChanged();
    }

    private void formHasChanged() {
        // show validation errors after the user has changed something
        //nombre.setValidationVisible(true);
        /*
        BeanItem<ClassicShowEntity> item = fieldGroup.getItemDataSource();
        if (item != null) {
        	ClassicShowEntity rec = item.getBean();
        	if(rec!=null){
        		//
        	}
        }
        */
    	puntuacion.setEnabled(false);
    	save.setVisible(false);
        //save.setEnabled(editable && EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.COORD));
    }

	public void close(){
		removeStyleName("visible");
		setEnabled(false);
	}
	
}
