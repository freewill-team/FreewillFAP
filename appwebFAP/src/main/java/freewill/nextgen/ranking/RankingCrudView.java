package freewill.nextgen.ranking;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import freewill.nextgen.categoria.SelectCircuitoAndCategoria;
import freewill.nextgen.hmi.utils.Messages;

/**
 * A view for performing create-read-update-delete operations on records.
 *
 * See also {@link StatusCrudLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */

@SuppressWarnings("serial")
public class RankingCrudView extends VerticalLayout implements View {

	@SuppressWarnings("unused")
	private static String ICC_PERMISSION = "ICC_ALARMS_PERMISSION";
    public final String VIEW_NAME = Messages.get().getKey("ranking");
    private Long circuito = null;
    private String circuitoStr = "";
    private Long categoria = null;
    private String categoriaStr = "";
    SelectCircuitoAndCategoria selectCircuitoAndCategoria = null;
    
    public RankingCrudView() {
        setSizeFull();
        addStyleName("crud-view");
        setSpacing(false);
        setMargin(false);
    }

    @Override
    public void enter(ViewChangeEvent event) {
    	
    	selectCircuitoAndCategoria = new SelectCircuitoAndCategoria(
    			new ClickListener() {
    	            @Override
    	            public void buttonClick(final ClickEvent event) {
    	                circuitoStr = selectCircuitoAndCategoria.getCircuitoStr();
    	                circuito = selectCircuitoAndCategoria.getCircuito();
    	                categoriaStr = event.getButton().getDescription();
    	                categoria = Long.parseLong(event.getButton().getId());
    	                gotoRanking();
    	            }
    	        });
        
        removeAllComponents();
        addComponent(selectCircuitoAndCategoria);
    }

	public void showError(String msg) {
        Notification.show(msg, Type.ERROR_MESSAGE);
    }

    public void showSaveNotification(String msg) {
        Notification.show(msg, Type.TRAY_NOTIFICATION);
    }

	public Long getCircuito() {
		return circuito;
	}

	public void gotoRanking() {
		removeAllComponents();
		addComponent(new RankingActaFinal(
    			categoria, categoriaStr,
    			circuito, circuitoStr, 
    			RankingCrudView.this));
	}
		
}
