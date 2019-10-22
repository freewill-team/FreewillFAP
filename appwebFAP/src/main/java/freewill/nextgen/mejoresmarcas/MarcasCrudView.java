package freewill.nextgen.mejoresmarcas;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import freewill.nextgen.hmi.utils.Messages;

/**
 * A view for performing create-read-update-delete operations on records.
 *
 * See also {@link StatusCrudLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */

@SuppressWarnings("serial")
public class MarcasCrudView extends CssLayout implements View {

	@SuppressWarnings("unused")
	private static String ICC_PERMISSION = "ICC_ALARMS_PERMISSION";
    public final String VIEW_NAME = Messages.get().getKey("mejoresmarcas");
    
    public MarcasCrudView() {
    	setSizeFull();
        addStyleName("crud-view");
    }

    @Override
    public void enter(ViewChangeEvent event) {
    	gotoIntroData();
    }

    public void showError(String msg) {
        Notification.show(msg, Type.ERROR_MESSAGE);
    }

    public void showSaveNotification(String msg) {
        Notification.show(msg, Type.TRAY_NOTIFICATION);
    }
	
	public void gotoIntroData() {
		removeAllComponents();
		addComponent(new MarcasCrudGrid(MarcasCrudView.this));
	}
	
}
