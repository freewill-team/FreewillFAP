package freewill.nextgen.dashboard;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.byteowls.vaadin.chartjs.ChartJs;
import com.byteowls.vaadin.chartjs.config.ScatterChartConfig;
import com.byteowls.vaadin.chartjs.data.ScatterDataset;
import com.byteowls.vaadin.chartjs.options.InteractionMode;
import com.byteowls.vaadin.chartjs.options.Position;
import com.byteowls.vaadin.chartjs.options.scale.Axis;
import com.byteowls.vaadin.chartjs.options.scale.CategoryScale;
import com.byteowls.vaadin.chartjs.options.scale.LinearScale;
import com.byteowls.vaadin.chartjs.utils.ColorUtils;
import com.vaadin.server.Responsive;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

import freewill.nextgen.common.entities.KpiValue;

@SuppressWarnings("serial")
public class LineChartJs extends VerticalLayout {
	
	// Chart variables
	private String title = "";
	private ScatterChartConfig lineConfig = null;
	private boolean firsttime = true;

	public LineChartJs(String title){
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
		
		lineConfig = new ScatterChartConfig();
        lineConfig.options()
            .responsive(true)
            .hover()
                .mode(InteractionMode.NEAREST)
                .intersect(false)
                .and()
            .title()
                .display(false)
                .text(title)
                .fontColor("rgb(0, 176, 202)")
                .and()
            .scales()
                .add(Axis.X, new LinearScale()
                		.position(Position.TOP)
                		.gridLines()
                		.zeroLineColor("rgba(0,0,0,1)")
                		.and())
                .add(Axis.Y, new LinearScale()
                		.display(true)
                		.position(Position.LEFT)
                		.id("y-axis-1")
                		)
                .add(Axis.X, new CategoryScale()
                		.display(true)
                		.position(Position.BOTTOM)
                		.ticks()
                		.autoSkip(true)
                		.and()
                		.scaleLabel()
                        .display(false)
                        .labelString("Time")
                        .and())
                .and()
           .done();

        ChartJs chart = new ChartJs(lineConfig);
        chart.setSizeFull();
        //chart.setJsLoggingEnabled(true);
        //chart.addClickListener((a,b) -> DemoUtils.notification(a, b, lineConfig.data().getDatasets().get(a)));
        chart.setWidth("85%");//90
        chart.setHeight("35%");//60
        return chart;
	}

	public void addSerie(String recName, List<KpiValue> samples) {
        ScatterDataset lds = new ScatterDataset().label(recName).xAxisID("x-axis-1").yAxisID("y-axis-1").fill(false);
		lineConfig.data().addDataset(lds);
		//for(int i=samples.size()-1;i>=0;i--){
        for(KpiValue pnt:samples) {
            lds.addData((double)pnt.getDate().getTime(), pnt.getValue());
        }
        lds.borderColor(ColorUtils.randomColor(0.3));
        lds.backgroundColor(ColorUtils.randomColor(0.5));
        lds.pointBorderColor(ColorUtils.randomColor(.7));
        lds.pointBackgroundColor(ColorUtils.randomColor(.5));
        lds.pointBorderWidth(1);
        
        if(firsttime && samples.size()>0){
        	firsttime = false;
        	List<String> dates = new ArrayList<String>();
        	SimpleDateFormat df = new SimpleDateFormat("yy-MM-dd"); // "yy-MM-dd HH:mm:ss"
        	//for(int i=samples.size()-1;i>=0;i--){
        	for(KpiValue pnt:samples) {
        		//CollectEntity pnt = samples.get(i);
        		dates.add(df.format(pnt.getDate()));
        	}
        	lineConfig.data().labelsAsList(dates);
        }
        
	}
	
}
