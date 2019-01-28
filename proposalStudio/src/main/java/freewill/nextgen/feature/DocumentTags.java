package freewill.nextgen.feature;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Simple name editor Window.
 */
@SuppressWarnings("serial")
public class DocumentTags extends Window {

	private DocumentPanelItem parent = null;
	private TabSheet tabs = null;

    public DocumentTags(DocumentPanelItem prnt, String tags) {
    	this.parent = prnt;
        setCaption("Edit Feature Tags");
        setModal(true);
        setClosable(false);
        setResizable(false);
        setWidth(600.0f, Unit.PIXELS);

        addStyleName("edit-dashboard");

        setContent(buildContent(tags));
    }

    private Component buildContent(String tags) {
        VerticalLayout result = new VerticalLayout();
        result.setMargin(true);
        result.setSpacing(true);
        
        tabs = new TabSheet();
        //tabs.setWidth("100%");
        //tabs.setStyleName(ValoTheme.TABSHEET_FRAMED);
        //tabs.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
        if(tags==null) tags="";
        String[] valores = tags.split(",");
    	for(String s:valores){
    		System.out.println("Tab="+s);
    		if(s.isEmpty() || s.length()==0 || s.equals(" ")) continue;
    		HorizontalLayout layout = new HorizontalLayout();
    		tabs.addTab(layout, s).setClosable(true);
    	}
       
    	TextField newtag = new TextField();
    	newtag.setStyleName("filter-textfield");
    	newtag.setInputPrompt("Add New Tag...");
    	newtag.setImmediate(true);
    	
    	Button addtag = new Button();
    	addtag.setIcon(FontAwesome.LEVEL_UP);
    	addtag.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
            	String newtext = newtag.getValue().replace(",", "");
            	if(newtext.isEmpty() || newtext.length()==0 || newtext.equals(" ")) return;
            	HorizontalLayout dummy = new HorizontalLayout();
    			tabs.addTab(dummy, newtext).setClosable(true);
    			newtag.setValue("");
            }
        });
    	addtag.setClickShortcut(KeyCode.ENTER, null);
    	
    	HorizontalLayout newtaglayout = new HorizontalLayout();
    	//newtaglayout.setWidth("100%");
    	newtaglayout.addComponents(newtag, addtag);
    	
        result.addComponent(tabs);
        result.addComponent(newtaglayout);
        result.addComponent(buildFooter());
        return result;
    }

	private Component buildFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setSpacing(true);
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Unit.PERCENTAGE);
        
        Button save = new Button("Save");
        save.addStyleName(ValoTheme.BUTTON_PRIMARY);
        save.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
            	String otags = "";
            	for (Component component : tabs) {
            	    TabSheet.Tab tab = tabs.getTab(component);
            	    otags+=","+tab.getCaption();
            	}
            	if(otags.length()>0 && otags.startsWith(",")){
            		System.out.println("Tags="+otags);
            		otags = otags.substring(1);
            	}
            	System.out.println("Tags="+otags);
            	parent.updateTags(otags);
                close();
            }
        });
        
        Button cancel = new Button("Cancel");
        cancel.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                close();
            }
        });
        cancel.setClickShortcut(KeyCode.ESCAPE, null);
        
        Label expander = new Label("");

        footer.addComponents(expander, cancel, save);
        footer.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
        footer.setExpandRatio(expander, 1);
        
        return footer;
    }

}
