package freewill.nextgen.competicion.classic;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Button;
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
public class ClassicShowFormDesign extends CssLayout {
	protected TextField tecnicaJuez1;				
	protected TextField artisticaJuez1;	
	protected TextField tecnicaJuez2;				
	protected TextField artisticaJuez2;	
	protected TextField tecnicaJuez3;				
	protected TextField artisticaJuez3;	
	protected TextField penalizaciones;
	protected TextField dorsal;
	protected TextField nombre;
	protected TextField apellidos;
	
    protected Button save;
    protected Button cancel;
    
    protected TabSheet tabSheet;
    protected Tab tabTodo;
    protected Tab tabJuez1;
    protected Tab tabJuez2;
    protected Tab tabJuez3;
    protected TextField tecnicaJuez1b;
	protected TextField artisticaJuez1b;	
	protected TextField tecnicaJuez2b;				
	protected TextField artisticaJuez2b;	
	protected TextField tecnicaJuez3b;				
	protected TextField artisticaJuez3b;	
	protected Button save1;
	protected Button save2;
	protected Button save3;

    public ClassicShowFormDesign() {
        Design.read(this);
    }
}