package freewill.nextgen.hmi.common;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.appwebFAP.EntryPoint;

/**
 * View shown when trying to navigate to a view and user has no allowed permission
 * {@link com.vaadin.navigator.Navigator}.
 * 
 * 
 */
@SuppressWarnings("serial")
public class NoPermissionView extends VerticalLayout implements View {

    private Label explanation;

    public NoPermissionView() {
        setMargin(true);
        setSpacing(true);

        Label header = new Label("No granted permission to access this view.");
        header.addStyleName(Reindeer.LABEL_H1);
        addComponent(header);
        addComponent(explanation = new Label());
        
        final Button logout = new Button("Return to the login form...", 
        		new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
            	EntryPoint.get().userLogout();
            }
        });
        logout.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
        logout.setIcon(FontAwesome.SIGN_OUT);
        addComponent(logout);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
    	explanation.setValue(String.format(
    			"Should you access this view, please contact "
    			+"your system administrator (Required permission is '%s').", event.getParameters()));
    }
}
