package freewill.nextgen.hmi.common;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class SparklineChart extends VerticalLayout {

    public SparklineChart(String name, String leyend, 
    		long current, long total,
    		boolean showchart) {
        setSizeUndefined();
        addStyleName(ValoTheme.LAYOUT_CARD);
        setMargin(true);
        setSpacing(true);
        addStyleName("spark");
        setDefaultComponentAlignment(Alignment.TOP_CENTER);
        
        String valores = current + " / "+ total;
        if(total==-1L){
        	valores = current + "";
        	total = 100;
        }
        Label value = new Label(valores);
        value.setSizeUndefined();
        value.addStyleName(ValoTheme.LABEL_HUGE);
        value.addStyleName(ValoTheme.LABEL_COLORED);
        value.addStyleName(ValoTheme.LABEL_BOLD);
        addComponent(value);
        
        Label highLow = new Label(leyend);
        highLow.addStyleName(ValoTheme.LABEL_TINY);
        highLow.addStyleName(ValoTheme.LABEL_LIGHT);
        highLow.setSizeUndefined();
        addComponent(highLow);

        if(showchart){
	        // FaviconProgressIndicator pi = new FaviconProgressIndicator();
	        ProgressBar pi = new ProgressBar();
	        pi.setSizeFull();
	        pi.setImmediate(true);
	        float val = (float)current;
	        val = val / total;
	        pi.setValue( val );
	        pi.setVisible(true);
	        pi.markAsDirty();
	        pi.setWidth("130px");
	        addComponent(pi);
        }
        
        Label title = new Label(name);
        title.setSizeUndefined();
        title.addStyleName(ValoTheme.LABEL_LARGE);
        title.addStyleName(ValoTheme.LABEL_LIGHT);
        addComponent(title);
        
    }

}
