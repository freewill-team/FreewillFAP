package freewill.nextgen.dashboard;

import java.text.DecimalFormat;

import com.vaadin.server.Resource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class SparklineJs extends HorizontalLayout {
	
	private Button iconBtn = null;

    public SparklineJs(final String name, final String leyend, final long current, 
    		final long total, Resource icon) {
    	String value = current + "/"+ total;
    	drawLayout(name, leyend, value, icon);
    }

    public SparklineJs(final String name, final String leyend, 
    		final long current, Resource icon) {
    	String value = ""+current;
    	drawLayout(name, leyend, value, icon);
    }
    public SparklineJs(final String name, final String leyend, final double current, 
    		final String units, Resource icon) {
        DecimalFormat df = new DecimalFormat("##,###,##0.00");
        String valuestr = df.format(current)+" "+units;
        drawLayout(name, leyend, valuestr, icon);
    }
    
    private void drawLayout(String name, String leyend, String value, Resource icon){
    	this.setSizeUndefined();
    	this.setMargin(false);
        this.setStyleName("spark");
        //this.addStyleName(ValoTheme.LAYOUT_CARD);
        
    	VerticalLayout bar = new VerticalLayout();
        bar.setWidth("4px");
        bar.setHeight("100%");
        bar.setStyleName("leftBorderBlue");
        bar.addComponent(new Label(" "));
    	
        VerticalLayout layout = new VerticalLayout();
        layout.setDefaultComponentAlignment(Alignment.TOP_CENTER);
        //layout.setMargin(true);
    	
        iconBtn = new Button(value);
        iconBtn.setIcon(icon);
        iconBtn.setStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
        iconBtn.addStyleName(ValoTheme.BUTTON_HUGE);
        layout.addComponent(iconBtn);
        
        Label title = new Label(name);
        title.setSizeUndefined();
        title.addStyleName(ValoTheme.LABEL_LARGE);
        title.addStyleName(ValoTheme.LABEL_LIGHT);
        layout.addComponent(title);

        if(!leyend.equals("")){
        	Label highLow = new Label(leyend);
            highLow.addStyleName(ValoTheme.LABEL_TINY);
            highLow.addStyleName(ValoTheme.LABEL_LIGHT);
            highLow.setSizeUndefined();
        	layout.addComponent(highLow);
        }
        
        //this.addComponent(bar);
        this.addComponent(layout);
        this.setExpandRatio(layout, 1);
    }
    
    public void addClickListener(ClickListener listener){
    	iconBtn.addClickListener(listener);
    }
}
