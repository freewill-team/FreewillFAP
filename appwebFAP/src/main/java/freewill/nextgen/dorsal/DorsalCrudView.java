package freewill.nextgen.dorsal;

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
public class DorsalCrudView extends CssLayout implements View {

	@SuppressWarnings("unused")
	private static String ICC_PERMISSION = "ICC_ALARMS_PERMISSION";
    public final String VIEW_NAME = Messages.get().getKey("registro");
    private Long competicion = null;
    private SelectCircuito selectcircuito = null;
    private VerticalLayout selectionarea = new VerticalLayout();
    private SelectCompeticionSpark selectcampeonato = null;
    
    public DorsalCrudView() {
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
        Notification.show(msg, Type.WARNING_MESSAGE); //ERROR_MESSAGE);
    }

    public void showSaveNotification(String msg) {
        Notification.show(msg, Type.TRAY_NOTIFICATION);
    }

	private SelectCompeticionSpark createSelectCampeonato(Long circuito){
		return new SelectCompeticionSpark(circuito,
        		new ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
                    	competicion = Long.parseLong(event.getButton().getId());
                    	gotoIntroData();
                    }
              	});
	}
	
	public void gotoIntroData() {
		removeAllComponents();
		addComponent(new DorsalCrudGrid(competicion, DorsalCrudView.this));
	}
	
}
