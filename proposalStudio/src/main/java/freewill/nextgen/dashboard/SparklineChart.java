package freewill.nextgen.dashboard;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
@Deprecated
public class SparklineChart extends VerticalLayout {

    public SparklineChart(final String name, final String leyend, final long current, final long total) {
        setSizeUndefined();
        addStyleName("spark");
        setDefaultComponentAlignment(Alignment.TOP_CENTER);

        Label value = new Label(current + "/"+ total);
        value.setSizeUndefined();
        value.addStyleName(ValoTheme.LABEL_HUGE);
        addComponent(value);

        Label title = new Label(name);
        title.setSizeUndefined();
        title.addStyleName(ValoTheme.LABEL_LARGE);
        title.addStyleName(ValoTheme.LABEL_LIGHT);
        addComponent(title);

        Label highLow = new Label(leyend);
        highLow.addStyleName(ValoTheme.LABEL_TINY);
        highLow.addStyleName(ValoTheme.LABEL_LIGHT);
        highLow.setSizeUndefined();
        addComponent(highLow);

    }

}
