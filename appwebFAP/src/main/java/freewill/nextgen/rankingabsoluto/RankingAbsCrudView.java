package freewill.nextgen.rankingabsoluto;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;

import freewill.nextgen.categoria.SelectModalidad;
import freewill.nextgen.data.CategoriaEntity.ModalidadEnum;
import freewill.nextgen.hmi.utils.Messages;

/**
 * A view for performing create-read-update-delete operations on records.
 *
 * See also {@link StatusCrudLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */

@SuppressWarnings("serial")
public class RankingAbsCrudView extends VerticalLayout implements View {

	@SuppressWarnings("unused")
	private static String ICC_PERMISSION = "ICC_ALARMS_PERMISSION";
    public final String VIEW_NAME = Messages.get().getKey("rankingabs");
    private ModalidadEnum modalidad;
    SelectModalidad selectModalidad = null;
    
    public RankingAbsCrudView() {
        setSizeFull();
        addStyleName("crud-view");
        setSpacing(false);
        setMargin(false);
    }

    @Override
    public void enter(ViewChangeEvent event) {
    	
    	selectModalidad = new SelectModalidad(
    			new ClickListener() {
    	            @Override
    	            public void buttonClick(final ClickEvent event) {
    	            	int modalidadId = Integer.parseInt(event.getButton().getId());
    	            	modalidad = ModalidadEnum.values()[modalidadId];
    	                gotoRankingAbs();
    	            }
    	        });
    	
        removeAllComponents();
        addComponent(selectModalidad);
        addComponent(new HorizontalLayout());
        addComponent(new HorizontalLayout());
        addComponent(new HorizontalLayout());
    }

	public void showError(String msg) {
        Notification.show(msg, Type.ERROR_MESSAGE);
    }

    public void showSaveNotification(String msg) {
        Notification.show(msg, Type.TRAY_NOTIFICATION);
    }

	public void gotoRankingAbs() {
		removeAllComponents();
		addComponent(new RankingAbsActaFinal(
    			modalidad, RankingAbsCrudView.this));
	}
		
}
