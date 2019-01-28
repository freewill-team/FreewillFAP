package freewill.nextgen.feature;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.data.FeatureEntity;

/**
 * Simple name editor Window.
 */
@SuppressWarnings("serial")
public class DocumentShowImage extends Window {

    private Image image = null;
    private FeatureEntity parent = null;

    public DocumentShowImage(FeatureEntity rec) {
    	parent = rec;
        setCaption("Show Image");
        setModal(true);
        setClosable(true);
        setResizable(false);
        setWidth(800.0f, Unit.PIXELS);

        addStyleName("edit-dashboard");

        setContent(buildContent());
    }

    private Component buildContent() {
        VerticalLayout result = new VerticalLayout();
        result.setMargin(true);
        result.setSpacing(true);
        image = new Image(); // Empty image
        image.setSizeFull();
        //image.setWidth("440px");
        //image.setHeight("220px");
        
        byte[] data = parent.getImage();
        if(data!=null && data.length>0){
        	StreamResource resource = new StreamResource(
        		new StreamResource.StreamSource() {
        			@Override
        			public InputStream getStream() {
        				return new ByteArrayInputStream(data);
        			}
        	    }, parent.getImagename());
        	image.setSource(resource);
        	image.setSizeFull();
    	}
        
        result.addComponent(image);
        result.addComponent(buildFooter());
        return result;
    }

	private Component buildFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setSpacing(true);
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Unit.PERCENTAGE);
        
        Button cancel = new Button("Close");
        cancel.addStyleName(ValoTheme.BUTTON_PRIMARY);
        cancel.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
            	close();
            }
        });
        cancel.setClickShortcut(KeyCode.ESCAPE, null);
        
        Label expander = new Label("");

        footer.addComponents(expander, cancel);
        footer.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
        footer.setExpandRatio(expander, 1);
        
        return footer;
    }

}
