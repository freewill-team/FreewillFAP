package freewill.nextgen.processMonitor;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
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
public class ProcessFormDesign extends CssLayout {
	protected TextField name;
	protected TextField timestamp2;
	protected TextField timeout;
	protected Label state;
	protected CheckBox restartOnFailure;
	
	protected VerticalLayout statebox;
	protected TabSheet  tabsheet;
	
	protected TextField site;
	protected TextField server;
	protected TextField service;
	protected TextField fullpath;
	protected VerticalLayout details;
	protected VerticalLayout config;
	protected VerticalLayout kpis;
	
	protected Label space;
    protected Button start;
    protected Button stop;
    protected Button close;
    protected Button delete;

    public ProcessFormDesign() {
        Design.read(this);
    }
}