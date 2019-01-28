package freewill.nextgen.hmi.common;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Simple name editor Window.
 */
@SuppressWarnings("serial")
public class ConfirmUseTermsDialog extends Window {
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
	
    public ConfirmUseTermsDialog() {
    	setCaption("Please Confirm Use Terms Agreement:");
        setModal(true);
        setClosable(false);
        setResizable(false);
        setWidth(450.0f, Unit.PIXELS);

        addStyleName("edit-dashboard");

        setContent(buildContent());
    }

    private Component buildContent() {
    	String message = "\nPlease download FreeWill Technologies's Terms and Conditions Agreement at:\n";
    	Link link = new Link("Terms and Conditions Agreement", 
    			new ExternalResource("http://www.freewill-technologies.es/TermsAndConditionsAgreement.pdf"));
    	CheckBox accept = new CheckBox("I have read and accept the Terms and Conditions.");
    	
    	String message2 = "\nPlease read FreeWill Technologies's Privacy Policy at:\n";
    	Link link2 = new Link("Privacy Policy", 
    			new ExternalResource("http://www.freewill-technologies.es/policy_es.html"));
    	CheckBox accept2 = new CheckBox("I have read and accept the Privacy Policy.");
    	
    	accept.addValueChangeListener(e ->{
        	save.setEnabled(accept.getValue()&&accept2.getValue());
        });
    	accept2.addValueChangeListener(e ->{
    		save.setEnabled(accept.getValue()&&accept2.getValue());
        });
    	
        VerticalLayout result = new VerticalLayout();
        result.setMargin(true);
        result.setSpacing(true);
        result.addComponent(new Label(message));
        result.addComponent(link);
        result.addComponent(accept);
        result.addComponent(new Label(message2));
        result.addComponent(link2);
        result.addComponent(accept2);
        //result.setComponentAlignment(msg, Alignment.MIDDLE_CENTER);
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

        save = new Button("Save");
        save.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
            	isOK = true;
                close();
            }
        });
        save.addStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(KeyCode.ENTER, null);
        save.setEnabled(false);

        footer.addComponents(cancel, save);
        footer.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        footer.setComponentAlignment(cancel, Alignment.MIDDLE_RIGHT);
        footer.setComponentAlignment(save, Alignment.MIDDLE_LEFT);
        
        return footer;
    }

}
