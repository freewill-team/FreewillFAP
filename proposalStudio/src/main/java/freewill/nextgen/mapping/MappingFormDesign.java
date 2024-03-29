package freewill.nextgen.mapping;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
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
public class MappingFormDesign extends CssLayout {
    protected TextField Req;
    protected TextField Customid;
    protected TextArea ReqDesc;
    protected TextField Doc;
    protected TextField DocTitle;
    protected TextField DocProduct;
    protected TextArea DocDesc;
    protected ComboBox Response;
    protected TextField Laboreffort;
    protected TextField Totalcost;
    protected TextArea Text;
    protected TextField Search;
    protected VerticalLayout SearchLayout;
    protected HorizontalLayout SearchLayout2;
    protected TextArea notes;
    protected ComboBox deliverable;
    
    protected Button find;
    protected Button prev;
    protected Button next;
    
    protected Image image;
    protected Button Add;
    protected Button Del;
    protected HorizontalLayout imageLayout;
    protected VerticalLayout imageLayout2;
    
    protected Button save;
    protected Button cancel;
    protected Button delete;

    public MappingFormDesign() {
        Design.read(this);
    }
}