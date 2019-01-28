package freewill.nextgen.user;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.entities.UserEntity;
import freewill.nextgen.hmi.common.AboutView;
import freewill.nextgen.hmi.utils.Messages;

/**
 * A form for editing a single record.
 *
 * Using responsive layouts, the form can be displayed either sliding out on the
 * side of the view or filling the whole screen - see the theme for the related
 * CSS rules.
 */
@SuppressWarnings("serial")
public class UserAloneView extends VerticalLayout implements View {
	
	public final String VIEW_NAME = Messages.get().getKey("miusercrudview.viewname");
    
    private UserForm form;
    private UserEntity userRec = null;
    private UserCrudLogic viewLogic = new UserCrudLogic(null);
    
    public UserAloneView() {
        setSizeFull();
        //addStyleName("crud-view");
        setStyleName("about-view");
        addComponent(new AboutView());
        this.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        form = new UserForm(viewLogic);
        addComponent(form);
    }
    
	@Override
	public void enter(ViewChangeEvent event) {
		userRec = EntryPoint.get().getAccessControl().getUserEntity();
		editRecord(userRec);
	}
	
	public void showError(String msg) {
        Notification.show(msg, Type.ERROR_MESSAGE);
    }

    public void showSaveNotification(String msg) {
        Notification.show(msg, Type.TRAY_NOTIFICATION);
    }
	   
    public void editRecord(UserEntity rec) {
        if (rec != null) {
            form.addStyleName("visible");
            form.setEnabled(true);
        } else {
            form.removeStyleName("visible");
            form.setEnabled(false);
        }
        form.editRecord(rec);
    }
    
}
