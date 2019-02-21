package freewill.nextgen.ranking;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Button;
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
public class RankingFormDesign extends CssLayout {
	protected Button patinBtn;
	protected TextField patinador;
	protected TextField nombre;
    protected TextField apellidos;
    protected TextField club;
    protected TextField clubStr;
    protected TextField orden;
    protected TextField puntos1;
    protected TextField puntos2;
    protected TextField puntos3;
    protected TextField puntos4;
    protected TextField puntuacion;
    protected ComboBox competicion;
    protected ComboBox categoria;
    
    protected Button save;
    protected Button cancel;
    protected Button delete;

    public RankingFormDesign() {
        Design.read(this);
    }
}