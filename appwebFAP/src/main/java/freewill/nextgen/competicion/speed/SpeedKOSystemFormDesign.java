package freewill.nextgen.competicion.speed;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
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
public class SpeedKOSystemFormDesign extends CssLayout {
	protected TextField patina1;
	protected TextField patina2;
	protected TextField pat1tiempo1;						
	protected TextField pat1tiempo2;
	protected TextField pat1tiempo3;
	protected TextField pat2tiempo1;						
	protected TextField pat2tiempo2;
	protected TextField pat2tiempo3;
	protected CheckBox  pat1gana1;
	protected CheckBox  pat1gana2;
	protected CheckBox  pat1gana3;
	protected CheckBox  pat2gana1;
	protected CheckBox  pat2gana2;
	protected CheckBox  pat2gana3;
	protected ComboBox  ganador;
    
    protected Button save;
    protected Button cancel;

    public SpeedKOSystemFormDesign() {
        Design.read(this);
    }
}