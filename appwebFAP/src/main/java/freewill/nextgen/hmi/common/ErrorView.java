package freewill.nextgen.hmi.common;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

import freewill.nextgen.hmi.utils.Messages;

/**
 * View shown when trying to navigate to a view that does not exist using
 * {@link com.vaadin.navigator.Navigator}.
 * 
 * 
 */
@SuppressWarnings("serial")
public class ErrorView extends VerticalLayout implements View {

    public ErrorView() {
        setMargin(true);
        setSpacing(true);

        Label header = new Label(Messages.get().getKey("errorNavigation"));
        header.addStyleName(Reindeer.LABEL_H1);
        addComponent(header);
        addComponent(new Label(Messages.get().getKey("errorNavigation2")));
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
       //
    }
}
