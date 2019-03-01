package freewill.nextgen.competicion.battle;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import freewill.nextgen.categoria.SelectCompeticionSparkAndCategoria;
import freewill.nextgen.data.CategoriaEntity.ModalidadEnum;
import freewill.nextgen.data.BattleRondaEntity.EliminatoriaEnum;
import freewill.nextgen.hmi.utils.Messages;

/**
 * A view for performing create-read-update-delete operations on records.
 *
 * See also {@link StatusCrudLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */

@SuppressWarnings("serial")
public class BattleCrudView extends VerticalLayout implements View {

	@SuppressWarnings("unused")
	private static String ICC_PERMISSION = "ICC_ALARMS_PERMISSION";
    public final String VIEW_NAME = Messages.get().getKey("battle");
    private Long competicion = null;
    private String competicionStr = "";
    private Long categoria = null;
    private String categoriaStr = "";
    SelectCompeticionSparkAndCategoria selectCampeonatoAndCategoria = null;
    
    public BattleCrudView() {
        setSizeFull();
        addStyleName("crud-view");
        setSpacing(false);
        setMargin(false);
    }

    @Override
    public void enter(ViewChangeEvent event) {
    	
    	selectCampeonatoAndCategoria = new SelectCompeticionSparkAndCategoria(ModalidadEnum.BATTLE,
    			new ClickListener() {
    	            @Override
    	            public void buttonClick(final ClickEvent event) {
    	                competicionStr = selectCampeonatoAndCategoria.getCompeticionStr();
    	                competicion = selectCampeonatoAndCategoria.getCompeticion();
    	                categoriaStr = event.getButton().getDescription();
    	                categoria = Long.parseLong(event.getButton().getId());
    	                gotoTrial();     
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

	public void gotoTrial() {
		removeAllComponents();
    	addComponent(new BattlePreclasificacion(
    			categoria, categoriaStr,
    			competicion, competicionStr, 
    			BattleCrudView.this));
	}

	public void gotoKO(EliminatoriaEnum eliminatoria) {
		removeAllComponents();
		addComponent(new BattleRonda(
    			categoria, categoriaStr,
    			competicion, competicionStr, 
    			eliminatoria, BattleCrudView.this));
	}
	
	public void gotoActaFinal() {
		// Crea acta final
		removeAllComponents();
		addComponent(new BattleActaFinal(
    			categoria, categoriaStr,
    			competicion, competicionStr, 
    			BattleCrudView.this));
	}
	
}
