package freewill.nextgen.hmi.common;

import com.vaadin.server.Resource;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class GenericHeader extends HorizontalLayout {

	public GenericHeader(String title, Resource icon){
		 Button topHeader = new Button(title);
	     topHeader.setIcon(icon);
	     topHeader.setStyleName(ValoTheme.BUTTON_BORDERLESS);
	     topHeader.addStyleName(ValoTheme.BUTTON_LARGE);
	     this.addComponent(topHeader);
	     //this.addStyleName("backColorBlue");
	     this.setWidth("100%");
	     this.setMargin(false);
	     this.setSpacing(true);
	}
	
}
