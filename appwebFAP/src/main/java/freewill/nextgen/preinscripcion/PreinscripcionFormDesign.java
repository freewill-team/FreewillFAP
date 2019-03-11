package freewill.nextgen.preinscripcion;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
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
public class PreinscripcionFormDesign extends CssLayout {
	protected TextField nombre;
    protected TextField apellidos;
    protected DateField fechaNacimiento;
    protected TextField genero;
    protected TextField clubStr;
    protected TextField fichaFederativa;
    protected CheckBox speed;
    protected CheckBox salto;
    protected CheckBox derrapes;
    protected CheckBox jam;
    protected CheckBox classic;
    protected CheckBox battle;
    protected ComboBox idCatSpeed;
    protected ComboBox idCatSalto;
    protected ComboBox idCatDerrapes;
    protected ComboBox idCatJam;
    protected ComboBox idCatClassic;
    protected ComboBox idCatBattle;
    protected HorizontalLayout parejaLayout;
    protected Button parejaBtn;
    protected TextField parejaJam;
    protected TextField idPareja;
    protected TextField nombrePareja;
    protected TextField apellidosPareja;
    protected HorizontalLayout speedLayout;
    protected HorizontalLayout saltoLayout;
    protected HorizontalLayout derrapesLayout;
    protected HorizontalLayout classicLayout;
    protected HorizontalLayout battleLayout;
    protected HorizontalLayout jamLayout;
    
    protected Button save;
    protected Button cancel;

    public PreinscripcionFormDesign() {
        Design.read(this);
    }
}