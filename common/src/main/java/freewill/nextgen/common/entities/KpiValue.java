package freewill.nextgen.common.entities;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class KpiValue implements Serializable, Cloneable {
	
    	private String name = "";
    	private double value = 0.0;
    	private Date date = new Date(0);
    	
    	public KpiValue(){
    		// Constructor
    	}
    	
    	public KpiValue(String kpi){
    		// Constructor
    		name = kpi;
    	}
    	
		public void setDate(Date v){
    		date = v;
    	}
    	
    	public Date getDate(){
    		return date;
    	}
    	
    	public void setName(String v)
        {
    		name = v;
        }
        
        public String getName()
        {
        	return name;
        }
    	
    	public void setValue(double v)
        {
    		value = v;
        }
        
        public double getValue()
        {
        	return value;
        }
        
        @Override
    	public KpiValue clone() throws CloneNotSupportedException {
    		return (KpiValue) super.clone();
    	}
        
    }