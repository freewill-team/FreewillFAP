package freewill.nextgen.dashboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.byteowls.vaadin.chartjs.ChartJs;
import com.byteowls.vaadin.chartjs.config.DonutChartConfig;
import com.byteowls.vaadin.chartjs.data.Dataset;
import com.byteowls.vaadin.chartjs.data.PieDataset;
import com.byteowls.vaadin.chartjs.options.Position;
import com.vaadin.server.Responsive;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class PieChartJs extends VerticalLayout {
	
	// Chart variables
	private String title = "";
	private HashMap<String, Double> piedata = null;

	public PieChartJs(String title, HashMap<String, Double> data){
		this.title = title;
		this.piedata = data;
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
		
		List<String> labels = new ArrayList<String>();
		for(String label:piedata.keySet())
			labels.add(label);
		
		DonutChartConfig config = new DonutChartConfig();
		config.data().labelsAsList(labels);
		
        config
            .data()
                //.labels("Red", "Green", "Yellow", "Grey", "Dark Grey")
                .addDataset(new PieDataset().label("Dataset 1"))
                .and();

        config.
            options()
                .rotation(Math.PI)
                .circumference(Math.PI)
                .cutoutPercentage(60)
                .responsive(true)
                .title()
                    .display(false)
                    .text(title)
                    .fontColor("rgb(0, 176, 202)")
                    .and()
                .animation()
                    .animateScale(false)
                    .animateRotate(true)
                    .and()
                 .legend()
                 	.position(Position.RIGHT)
                 	.and()
               .done();

        //String[] colors = new String[] {"#F7464A", "#46BFBD", "#FDB45C", "#949FB1", "#4D5360"};
        String[] colors = new String[] {"#00b0ca", "#dfdfdf", "#525252", "#000000", "#c7c7c7", "#F7464A", "#FFFFFF"};
        // azul-indra gris-claro gris-oscuro negro gris-medio no-lo-se blanco

        //List<String> labels = config.data().getLabels();
        for (Dataset<?, ?> ds : config.data().getDatasets()) {
            PieDataset lds = (PieDataset) ds;
            lds.backgroundColor(colors);
            List<Double> data = new ArrayList<>();
            for (int i = 0; i < labels.size(); i++) {
                //data.add((double) (Math.round(Math.random() * 100)));
                data.add(piedata.get(labels.get(i)));
            }
            lds.dataAsList(data);
        }
        
        ChartJs chart = new ChartJs(config);
        chart.setSizeFull();
        //chart.setJsLoggingEnabled(true);
        //chart.addClickListener((a,b) -> DemoUtils.notification(a, b, config.data().getDatasets().get(a)));
        chart.setWidth("60%");//80
        chart.setHeight("35%");//80
        return chart;
	}

}
