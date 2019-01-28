package freewill.nextgen.hmi.common;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;

@SuppressWarnings("serial")
public class ComboTime extends CustomField<Double> {
	
	private ComboBox hour = new ComboBox();
	private ComboBox min = new ComboBox();
	
	public ComboTime(){
		super.setValue(0.0);
		for(int i=6; i<24; i++)
			hour.addItem(i);
		for(int i=0; i<60; i=i+15)
			min.addItem(i);
	}
	
	@Override
	public void setValue(Double value){
		if(value==null) return;
		int _hour = value.intValue();
		hour.setValue(_hour);
		double min_ = (value-_hour*1.0)*60.0;
		int _min = (int)min_;
		min.setValue(_min);
		super.setValue(value);
		System.out.println("ComboTime value="+value);
		System.out.println(">ComboTime Hour="+_hour);
		System.out.println(">ComboTime Min ="+_min);
	}
	
	@Override
	public Double getValue(){
		Double value = 0.0;
		int _hour = (int) hour.getValue();
		int _min = (int) min.getValue();
		value = _hour+_min/60.0;
		System.out.println("ComboTime Hour="+_hour);
		System.out.println("ComboTime Min ="+_min);
		System.out.println(">ComboTime value="+value);
		return value;
	}
	
	public int getHour(){
		return (int) hour.getValue();
	}
	
	public int getMinute(){
		return (int) min.getValue();
	}
	
	public void setHour(int val){
		hour.setValue(val);
	}
	
	public void setMinute(int val){
		min.setValue(val);
	}

	@Override
	protected Component initContent() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setSpacing(false);
		layout.setMargin(false);
		//layout.setWidth("100%");
		
		Label label = new Label(":");
		layout.addComponent(hour);
		layout.addComponent(label);
		layout.addComponent(min);
		hour.setWidth("80px");
		label.setWidth("15px");
		min.setWidth("80px");
		layout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
		
		//this.setWidth("100%");
        
		return layout;
	}

	@Override
	public Class<? extends Double> getType() {
		return Double.class;
	}
	
}
