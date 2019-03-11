package freewill.nextgen.preinscripcion;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import freewill.nextgen.circuito.SelectCircuito;
import freewill.nextgen.competicion.SelectCompeticionSpark;
import freewill.nextgen.hmi.utils.Messages;

/**
 * A view for performing create-read-update-delete operations on records.
 *
 * See also {@link StatusCrudLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */

@SuppressWarnings("serial")
public class PreinscripcionCrudView extends CssLayout implements View {

	@SuppressWarnings("unused")
	private static String ICC_PERMISSION = "ICC_ALARMS_PERMISSION";
    public final String VIEW_NAME = Messages.get().getKey("inscripciones");
    private Long competicion = null;
    //private String competicionStr = "";
    private VerticalLayout selectionarea = new VerticalLayout();
    private SelectCircuito selectcircuito = null;
    private SelectCompeticionSpark selectcampeonato = null;
    private InscripcionEnum tipoForm = InscripcionEnum.INSCRIPCION;
    
    public enum InscripcionEnum{
    	PREINSCRIPCION,
    	INSCRIPCION;
    }
    
    public PreinscripcionCrudView(InscripcionEnum tipo) {
    	this.tipoForm = tipo;
        setSizeFull();
        addStyleName("crud-view");
    }

    @Override
    public void enter(ViewChangeEvent event) {
    	
    	selectcircuito = new SelectCircuito();
    	selectcircuito.addAction(
    		new ValueChangeListener() {
	            public void valueChange(ValueChangeEvent event) {
	            	selectionarea.removeAllComponents();
	            	selectcampeonato = createSelectCampeonato(selectcircuito.getValue());
	                selectionarea.addComponent(selectcampeonato);
	            }
    		});
    	
    	selectcampeonato = createSelectCampeonato(selectcircuito.getValue());
        
        removeAllComponents();
        addComponent(selectcircuito);
        addComponent(selectionarea);
        selectionarea.removeAllComponents();
        selectionarea.addComponent(selectcampeonato);
    }

    public void showError(String msg) {
        Notification.show(msg, Type.ERROR_MESSAGE);
    }

    public void showSaveNotification(String msg) {
        Notification.show(msg, Type.TRAY_NOTIFICATION);
    }

	private SelectCompeticionSpark createSelectCampeonato(Long circuito){
		return new SelectCompeticionSpark(circuito,
        		new ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
                    	/*if(tipoForm == InscripcionEnum.PREINSCRIPCION)
                    		competicionStr = "Pre-Inscripciones "+event.getButton().getDescription();
                    	else
                    		competicionStr = "Inscripciones "+event.getButton().getDescription();*/
                    	competicion = Long.parseLong(event.getButton().getId());
                    	gotoIntroData();
                    }
              	});
	}
	
	public void gotoIntroData() {
		removeAllComponents();
		addComponent(new PreinscripcionCrudGrid(
				tipoForm, competicion, 
    			PreinscripcionCrudView.this));
	}
	
}
