package freewill.nextgen.company;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

import freewill.nextgen.data.CompanyEntity;
import freewill.nextgen.hmi.common.AboutView;
import freewill.nextgen.hmi.utils.Messages;
import freewill.nextgen.proposalStudio.EntryPoint;

import com.vaadin.ui.Notification.Type;

/**
 * A form for editing a single record.
 *
 * Using responsive layouts, the form can be displayed either sliding out on the
 * side of the view or filling the whole screen - see the theme for the related
 * CSS rules.
 */
@SuppressWarnings("serial")
public class CompanyAloneView extends VerticalLayout implements View {
	
	public final String VIEW_NAME = Messages.get().getKey("micompanycrudview.viewname");
    
    private CompanyForm form;
    private CompanyEntity compRec = null;
    private CompanyCrudLogic viewLogic = new CompanyCrudLogic(null);

    public CompanyAloneView() {
    	setSizeFull();
        //addStyleName("crud-view");
        setStyleName("about-view");
        addComponent(new AboutView());
        this.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        form = new CompanyForm(viewLogic);
        addComponent(form);
    }
   
	@Override
	public void enter(ViewChangeEvent event) {
		compRec = EntryPoint.get().getAccessControl().getCompany();
		editRecord(compRec);
	}
	
	public CompanyAloneView getView(){
		return this;
	}
	
	public void showError(String msg) {
        Notification.show(msg, Type.ERROR_MESSAGE);
    }

    public void showSaveNotification(String msg) {
        Notification.show(msg, Type.TRAY_NOTIFICATION);
    }

    public void editRecord(CompanyEntity rec) {
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
