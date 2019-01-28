package freewill.nextgen.dashboard;

import java.util.ArrayList;
import java.util.List;

import com.byteowls.vaadin.chartjs.ChartJs;
import com.byteowls.vaadin.chartjs.config.BarChartConfig;
import com.byteowls.vaadin.chartjs.data.BarDataset;
import com.byteowls.vaadin.chartjs.options.InteractionMode;
import com.byteowls.vaadin.chartjs.options.scale.Axis;
import com.byteowls.vaadin.chartjs.options.scale.DefaultScale;
import com.vaadin.server.Responsive;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

import freewill.nextgen.common.entities.KpiValue;

@SuppressWarnings("serial")
public class BarChartJs extends VerticalLayout {
	
	// Chart variables
	private String title = "";
	private BarChartConfig config = null;
	private int numTrends = 0;

	public BarChartJs(String title){
		this.title = title;
		this.setCaption(title);
		
		this.setSizeFull();
        //this.addStyleName("backColorWhite");
        //this.addStyleName(ValoTheme.LAYOUT_CARD);
        this.setMargin(true);
        this.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        Responsive.makeResponsive(this);
        
        Component chart = builtChart();
        this.addComponent(chart);
        //this.setExpandRatio(chart, 1L);
	}
	
	private Component builtChart() {
		
		config = new BarChartConfig();
        config.data()
            //.labels("January", "February", "March", "April", "May", "June", "July")
            //.addDataset(new BarDataset().label("Dataset 1").backgroundColor("rgba(220,220,220,0.5)"))
            .and()
        .options()
            .responsive(true)
            .title()
                .display(false)
                .text(title)
                .fontColor("rgb(0, 176, 202)")
                .and()
            .tooltips()
                .mode(InteractionMode.INDEX)
                .intersect(false)
                .and()
            .scales()
            	.add(Axis.X, new DefaultScale()
                    .stacked(true))
            	.add(Axis.Y, new DefaultScale()
                    .stacked(true))
            .and()
            .done();

        ChartJs chart = new ChartJs(config);
        chart.setSizeFull();
        //chart.setJsLoggingEnabled(true); 
        /*chart.addClickListener((a, b) -> {
            BarDataset dataset = (BarDataset) config.data().getDatasets().get(a);
            DemoUtils.notification(a, b, dataset);
        });*/
        chart.setWidth("85%");//90
        chart.setHeight("35%");//60
        return chart;
	}

	public void addSerie(String recName, List<KpiValue> samples) {
		String[] colors = new String[] {"#00b0ca", "#dfdfdf", "#525252", "#000000", "#c7c7c7", "#F7464A", "#FFFFFF"};
        // azul-indra gris-claro gris-oscuro negro gris-medio no-lo-se blanco
        BarDataset lds = new BarDataset().label(recName).backgroundColor(colors[numTrends]);
		config.data().addDataset(lds);
        for(KpiValue pnt:samples){
            lds.addData(pnt.getValue());
        }
        
        if(numTrends==0 && samples.size()>0){
        	List<String> labels = new ArrayList<String>();
        	for(KpiValue pnt:samples) {
        		labels.add(pnt.getName());
        	}
        	config.data().labelsAsList(labels);
        }
        numTrends++;
	}
	
}
