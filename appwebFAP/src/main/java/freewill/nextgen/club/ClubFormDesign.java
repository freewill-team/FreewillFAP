package freewill.nextgen.club;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
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
public class ClubFormDesign extends CssLayout {
	protected TextField nombre;
    protected TextField coordinador;
	protected TextField email;
    protected TextField telefono;
    protected TextField direccion;
    protected TextField localidad;
    protected TextField provincia;
    
    protected Image image;
    protected Button Add;
    protected Button Del;
    protected HorizontalLayout imageLayout;
    protected VerticalLayout imageLayout2;
    
    protected Button save;
    protected Button cancel;
    protected Button delete;

    public ClubFormDesign() {
        Design.read(this);
    }
}