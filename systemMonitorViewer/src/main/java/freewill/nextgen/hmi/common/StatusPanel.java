package freewill.nextgen.hmi.common;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.common.Utils.ServiceStatusEnum;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;

@SuppressWarnings("serial")
public class StatusPanel extends CustomField<ServiceStatusEnum> {
	
	private Label state = new Label("Failed");
	FontAwesome ICON_GOOD = FontAwesome.CHECK_CIRCLE;
	FontAwesome ICON_FAIL = FontAwesome.EXCLAMATION_TRIANGLE;
	FontAwesome ICON_STOP = FontAwesome.STOP_CIRCLE;
	FontAwesome ICON_STARTING = FontAwesome.HOURGLASS_START;
	
	public StatusPanel(){
		super.setValue(ServiceStatusEnum.FAILED);
	}
	
	@Override
	public void setValue(ServiceStatusEnum value){
		if(value==null) return;
		super.setValue(value);
		String color = "";
        FontAwesome icono = null;
        switch(value){
        	case GOOD:
	            color = "#2dd085"; // green
	            icono = ICON_GOOD;
	            break;
	        case STOP:
	            color = "#ffc66e"; // yellow
	            icono = ICON_STOP;
	            break;
        	case STARTING:
        		color = "#00b0ca"; // blue
        		icono = ICON_STARTING;
        		break;
            default:
            	color = "#f54993"; // red
            	icono = ICON_FAIL;
            	break;
        }
        state.setCaptionAsHtml(true);
        state.setValue("<span style=\'align: center; color: " + color + " !important;\'> " 
            + icono.getHtml()  + "</span>  <b>" + value.toString()+"</b>");
	}
	
	@Override
	public ServiceStatusEnum getValue(){
		return super.getValue();
	}

	@Override
	protected Component initContent() {
		HorizontalLayout layout = new HorizontalLayout();
		layout.setSpacing(true);
		layout.setMargin(true);
		layout.setSizeFull();
		layout.setStyleName(ValoTheme.LAYOUT_CARD);
		state.setCaptionAsHtml(true);
        state.addStyleName(ValoTheme.LABEL_HUGE);
		layout.addComponent(state);
		layout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		return layout;
	}

	@Override
	public Class<? extends ServiceStatusEnum> getType() {
		return ServiceStatusEnum.class;
	}
	
}
