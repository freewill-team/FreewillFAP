package freewill.nextgen.competicion.speed;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import freewill.nextgen.categoria.SelectCampeonatoAndCategoria;
import freewill.nextgen.data.CategoriaEntity.ModalidadEnum;
import freewill.nextgen.data.SpeedKOSystemEntity.EliminatoriaEnum;
import freewill.nextgen.data.SpeedTimeTrialEntity.RondaEnum;
import freewill.nextgen.hmi.utils.Messages;

/**
 * A view for performing create-read-update-delete operations on records.
 *
 * See also {@link StatusCrudLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */

@SuppressWarnings("serial")
public class SpeedCrudView extends VerticalLayout implements View {

	@SuppressWarnings("unused")
	private static String ICC_PERMISSION = "ICC_ALARMS_PERMISSION";
    public final String VIEW_NAME = Messages.get().getKey("speed");
    private Long competicion = null;
    private String competicionStr = "";
    private Long categoria = null;
    private String categoriaStr = "";
    SelectCampeonatoAndCategoria selectCampeonatoAndCategoria = null;
    
    public SpeedCrudView() {
        setSizeFull();
        addStyleName("crud-view");
        setSpacing(false);
        setMargin(false);
    }

    @Override
    public void enter(ViewChangeEvent event) {
    	
    	selectCampeonatoAndCategoria = new SelectCampeonatoAndCategoria(ModalidadEnum.SPEED,
    			new ClickListener() {
    	            @Override
    	            public void buttonClick(final ClickEvent event) {
    	                competicionStr = selectCampeonatoAndCategoria.getCompeticionStr();
    	                competicion = selectCampeonatoAndCategoria.getCompeticion();
    	                categoriaStr = event.getButton().getDescription();
    	                categoria = Long.parseLong(event.getButton().getId());
    	                gotoTimeTrial(RondaEnum.PRIMERA);
    	            }
    	        });
        
        removeAllComponents();
        addComponent(selectCampeonatoAndCategoria);
    }

    public void showError(String msg) {
        Notification.show(msg, Type.ERROR_MESSAGE);
    }

    public void showSaveNotification(String msg) {
        Notification.show(msg, Type.TRAY_NOTIFICATION);
    }

	public Long getCompeticion() {
		return competicion;
	}

	public void gotoTimeTrial(RondaEnum ronda) {
		removeAllComponents();
    	addComponent(new SpeedTimeTrial(
    			categoria, categoriaStr,
    			competicion, competicionStr, 
    			ronda, SpeedCrudView.this));
	}

	public void gotoKOsystem(EliminatoriaEnum eliminatoria) {
		removeAllComponents();
		addComponent(new SpeedKOsystem(
    			categoria, categoriaStr,
    			competicion, competicionStr, 
    			eliminatoria, SpeedCrudView.this));
	}
	
	public void gotoActaFinal() {
		// Crea acta final
		removeAllComponents();
		addComponent(new SpeedActaFinal(
    			categoria, categoriaStr,
    			competicion, competicionStr, 
    			SpeedCrudView.this));
	}
	
}
