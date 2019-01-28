package freewill.nextgen.hmi.common;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Simple OK/Cancel selector Window.
 */
@SuppressWarnings("serial")
public class ConfirmDialog extends Window {
	private boolean isOK = false;
	private Button save = null;
	
	public boolean isOK(){
		return isOK;
	}
	
	public void setOKAction(ClickListener action){
		save.addClickListener(action);
		/*save.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
            	isOK = true;
                close();
            }
        });*/
	}
	
    public ConfirmDialog(String message) {
    	setCaption("Please Confirm:");
        setModal(true);
        setClosable(false);
        setResizable(false);
        setWidth(450.0f, Unit.PIXELS);

        addStyleName("edit-dashboard");

        setContent(buildContent(message));
    }

    private Component buildContent(String message) {
        VerticalLayout result = new VerticalLayout();
        result.setMargin(true);
        result.setSpacing(true);
        Label msg = new Label(message);
        result.addComponent(msg);
        result.setComponentAlignment(msg, Alignment.MIDDLE_CENTER);
        result.addComponent(buildFooter());
        return result;
    }

    private Component buildFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setSpacing(true);
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Unit.PERCENTAGE);
        
        Button cancel = new Button("Cancel");
        cancel.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
            	isOK = false;
                close();
            }
        });
        cancel.setClickShortcut(KeyCode.ESCAPE, null);

        save = new Button(" OK ");
        save.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
            	isOK = true;
                close();
            }
        });
        save.addStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(KeyCode.ENTER, null);

        footer.addComponents(cancel, save);
        footer.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        footer.setComponentAlignment(cancel, Alignment.MIDDLE_RIGHT);
        footer.setComponentAlignment(save, Alignment.MIDDLE_LEFT);
        
        return footer;
    }

}
