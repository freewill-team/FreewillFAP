package freewill.nextgen.hmi.common;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

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
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
    	explanation.setValue(String.format(
    			"Should you access this view, please contact "
    			+"your system administrator (Required permission is '%s').", event.getParameters()));
    }
}