package freewill.nextgen.hmi.common;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import freewill.nextgen.hmi.utils.Messages;

@SuppressWarnings("serial")
public class AboutView extends VerticalLayout implements View {

    public final String VIEW_NAME = Messages.get().getKey("aboutview.viewname");

    public AboutView() {
        CustomLayout aboutContent = new CustomLayout("aboutview");
        aboutContent.setStyleName("about-content");

        // you can add Vaadin components in predefined slots in the custom layout
        Image image = new Image(null, new ThemeResource("img/freewill-logo-big.png"));
        image.setSizeFull();
        aboutContent.addComponent(image , "logo");
        		
        aboutContent.addComponent(new Label(FontAwesome.INFO_CIRCLE.getHtml()
                + " FreeWill Technologies "
                + "3.0.2", ContentMode.HTML), "info");
        
        setSizeFull();
        setStyleName("about-view");
        addComponent(aboutContent);
        setComponentAlignment(aboutContent, Alignment.MIDDLE_CENTER);
    }

    @Override
    public void enter(ViewChangeEvent event) {
    }

}
