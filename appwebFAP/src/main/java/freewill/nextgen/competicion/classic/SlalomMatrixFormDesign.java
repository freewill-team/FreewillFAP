package freewill.nextgen.competicion.classic;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
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
public class SlalomMatrixFormDesign extends CssLayout {
	protected TextField puntuacion;
	
    protected Button cancel;
    protected Button save;
    
    protected TabSheet tabSheet;
    protected Tab tabElasticidad;
    protected Tab tabSentados;
    protected Tab tabSaltos;
    protected Tab tabLineales;
    protected Tab tabGiros;
    
    protected ComboBox elasticidad;
    protected Button addElasticidad;
    protected ComboBox sentados;
    protected Button addSentados;

    public SlalomMatrixFormDesign() {
        Design.read(this);
    }
}