package freewill.nextgen.support;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.declarative.Design;

/** 
 * !! DO NOT EDIT THIS FILE !!
 * 
 * This class is generated by Vaadin Designer and will be overwritten.
 * 
 * Please make a subclass with logic and additional interfaces as needed,
 * e.g class LoginView extends LoginDesign implements View { … }
 */
@DesignRoot
@AutoGenerated
@SuppressWarnings("serial")
public class SupportFormDesign extends CssLayout {
    protected Label Id;
    protected TextArea Details;
    protected TextArea Comments;
    protected CheckBox Resolved;
    protected TextField User;
    protected ComboBox Severity;
    protected PopupDateField Created;
     
    protected Button save;
    protected Button cancel;
    protected Button delete;

    public SupportFormDesign() {
        Design.read(this);
    }
}