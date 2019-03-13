package freewill.nextgen.competicion.speed;

import java.util.List;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.data.SpeedKOSystemEntity;
import freewill.nextgen.data.ConfigEntity.ConfigItemEnum;
import freewill.nextgen.data.SpeedKOSystemEntity.EliminatoriaEnum;

@SuppressWarnings("serial")
public class ArbolKOSystem extends Panel {
	
	private Label[][] container1 = null;
	private Label[][] container2 = null;
	private Button[][] containerBtn = null;
	private int numLevels = 3;
	private boolean consolacion = false;
	private int additionalrow = 0;

	public ArbolKOSystem(EliminatoriaEnum levels, ClickListener action){
		setStyleName(ValoTheme.LAYOUT_CARD);
        setSizeFull();
        Responsive.makeResponsive(this);
        
        consolacion = EntryPoint.get().getConfigBoolean(ConfigItemEnum.FINALCONSOLACIONSPEED);
        additionalrow = (consolacion?1:0);
		
		this.numLevels = levels.ordinal()+1;
		int rows = (int)(Math.pow(2.0, numLevels));
		container1 = new Label[numLevels][rows+additionalrow];
		container2 = new Label[numLevels][rows+additionalrow];
		containerBtn = new Button[numLevels][rows+additionalrow];
		
		VerticalLayout vlayout = new VerticalLayout();
		vlayout.setSizeFull();
		vlayout.setMargin(false);
		vlayout.setSpacing(false);
		vlayout.addStyleName("dashboard-view");
		
		GridLayout grid = new GridLayout(numLevels, rows+additionalrow);
		grid.setWidth("100%");
		grid.setMargin(true);
		grid.setSpacing(true);
		grid.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
		
		vlayout.addComponent(grid);
		setContent(vlayout);
        Responsive.makeResponsive(vlayout);
		
		for(int i=0;i<numLevels;i++){
			int n = (int)(Math.pow(2.0, i));
			for(int j=0; j<n;j++){
				VerticalLayout layout = new VerticalLayout();
				layout.setWidth("100%");
				layout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
				Label label1 = new Label("Grupo "+i+","+j);
				label1.setStyleName(ValoTheme.LABEL_FAILURE);
				label1.setWidth("100%");
				Label label2 = new Label("Grupo "+i+","+j);
				label2.setStyleName(ValoTheme.LABEL_FAILURE);
				label2.setWidth("100%");
				layout.addComponents(label1, label2);
				
				Button button = new Button();
				button.setStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
				button.addStyleName(ValoTheme.BUTTON_LARGE);
				button.setIcon(FontAwesome.FORWARD);
				button.addClickListener(action);
				button.setEnabled(false);
				
				HorizontalLayout celda = new HorizontalLayout();
				//celda.setStyleName(ValoTheme.LAYOUT_CARD); // temporal
				celda.setWidth("100%");
				celda.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
				celda.addComponents(layout, button);
				celda.setExpandRatio(layout, 1);
				
				int m = (int)(Math.pow(2.0, numLevels-i-1)); 
				// i=0 n=2^i=1 m=2^2=4 -> j=0 j*m=0*4=0 (j+1)*m=1*4=4
				// i=1 n=2^i=2 m=2^1=2 -> j=0 j*m=0*2=0 (j+1)*m=1*2=2
				// i=1 n=2^i=2 m=2^1=2 -> j=1 j*m=1*2=0 (j+1)*m=(1+1)*2=2
				//System.out.println("i="+i+" j="+j+" n="+n+" m="+m+" "+j*m+" "+((j+1)*m-1));
				grid.addComponent(celda, numLevels-i-1, j*m, numLevels-i-1, (j+1)*m-1);
				
				container1[i][j] = label1;
				container2[i][j] = label2;
				containerBtn[i][j] = button;
			}
		}
		
		if(consolacion){
			// Final de consolacion
			VerticalLayout layout = new VerticalLayout();
			layout.setWidth("100%");
			layout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
			Label label1 = new Label("Grupo "+0+","+1);
			label1.setStyleName(ValoTheme.LABEL_FAILURE);
			label1.setWidth("100%");
			Label label2 = new Label("Grupo "+0+","+1);
			label2.setStyleName(ValoTheme.LABEL_FAILURE);
			label2.setWidth("100%");
			layout.addComponents(label1, label2);
			
			Button button = new Button();
			button.setStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
			button.addStyleName(ValoTheme.BUTTON_LARGE);
			button.setIcon(FontAwesome.FORWARD);
			button.addClickListener(action);
			button.setEnabled(false);
			
			HorizontalLayout celda = new HorizontalLayout();
			//celda.setStyleName(ValoTheme.LAYOUT_CARD); // temporal
			celda.setWidth("100%");
			celda.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
			celda.addComponents(layout, button);
			celda.setExpandRatio(layout, 1);
			
			grid.addComponent(celda, numLevels-1, rows, numLevels-1, rows);
			
			container1[0][1] = label1;
			container2[0][1] = label2;
			containerBtn[0][1] = button;
		}
		
	}
	
	public void setRecords(List<SpeedKOSystemEntity> records) {
        for(SpeedKOSystemEntity rec:records){
        	Label label1 = (Label) container1[rec.getEliminatoria().ordinal()][rec.getGrupo()];
        	Label label2 = (Label) container2[rec.getEliminatoria().ordinal()][rec.getGrupo()];
        	Button button = (Button) containerBtn[rec.getEliminatoria().ordinal()][rec.getGrupo()];
        	if(label1!=null){
        		label1.setValue(rec.getDorsal1()+" "+rec.getNombre1()+" "+rec.getApellidos1());
        		if(rec.getGanador()!=null && rec.getPatinador1()!=null &&
        				rec.getGanador().longValue()==rec.getPatinador1().longValue()){
        			label1.setStyleName(ValoTheme.LABEL_SUCCESS);
        			label2.setStyleName(ValoTheme.LABEL_FAILURE);
        		}
        	}
        	if(label2!=null){
        		label2.setValue(rec.getDorsal2()+" "+rec.getNombre2()+" "+rec.getApellidos2());
        		if(rec.getGanador()!=null && rec.getPatinador2()!=null &&
        				rec.getGanador().longValue()==rec.getPatinador2().longValue()){
        			label2.setStyleName(ValoTheme.LABEL_SUCCESS);
        			label1.setStyleName(ValoTheme.LABEL_FAILURE);
        		}
        	}
        	if(button!=null){
        		button.setId(""+rec.getId());
        		if(!label1.getValue().contains("null") && !label2.getValue().contains("null"))
        			button.setEnabled(true);
        	}
        }
    }
	
}
