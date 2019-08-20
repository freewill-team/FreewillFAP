package fwt.apppubfap.speed;

import java.util.List;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import fwt.apppubfap.dtos.SpeedKOSystemEntity;
import fwt.apppubfap.dtos.SpeedKOSystemEntity.EliminatoriaEnum;

@SuppressWarnings("serial")
public class ArbolKOSystem extends VerticalLayout {
	
	private Button[][] container1 = null;
	private Button[][] container2 = null;
	private int numLevels = 3;
	private boolean consolacion = true; // TODO configurable
	private int additionalrow = 0;

	public ArbolKOSystem(EliminatoriaEnum levels){
        this.setSizeFull();
        this.setSpacing(false);
		this.setPadding(false);
        this.getElement().getStyle().set("overflow", "auto");
        //this.getElement().getStyle().set("background-color", "yellow");
        
        //consolacion = EntryPoint.get().getConfigBoolean(ConfigItemEnum.FINALCONSOLACIONSPEED);
        additionalrow = (consolacion?1:0);
		
		this.numLevels = levels.ordinal()+1;
		int rows = (int)(Math.pow(2.0, numLevels));
		container1 = new Button[numLevels][rows+additionalrow];
		container2 = new Button[numLevels][rows+additionalrow];
		
		HorizontalLayout hlayout = new HorizontalLayout();
		hlayout.setSizeFull();
		hlayout.setPadding(false);
		hlayout.setSpacing(true);
		//hlayout.getElement().getStyle().set("background-color", "green");
		hlayout.setDefaultVerticalComponentAlignment(Alignment.STRETCH);
		this.add(hlayout);
		
		//for(int i=0;i<numLevels;i++){
		for(int i=numLevels-1;i>=0;i--){
			
			VerticalLayout vlayout = new VerticalLayout();
			vlayout.setSizeFull();
			vlayout.setPadding(false);
			vlayout.setSpacing(true);
			vlayout.setAlignItems(Alignment.STRETCH);
			//vlayout.getElement().getStyle().set("background-color", "orange");
			hlayout.add(vlayout);
			
			int n = (int)(Math.pow(2.0, i));
			for(int j=0; j<n;j++){
				
				VerticalLayout layout = new VerticalLayout();
				layout.setWidth("100%");
				layout.setPadding(false);
				layout.setSpacing(false);
				layout.setAlignItems(Alignment.CENTER);
				//layout.getElement().getStyle().set("background-color", "blue");
				Button label1 = new Button("Grupo "+i+","+j);
				label1.setWidth("100%");
				Button label2 = new Button("Grupo "+i+","+j);
				label2.setWidth("100%");
				layout.add(label1, label2);
				
				//int m = (int)(Math.pow(2.0, numLevels-i-1));
				//grid.addComponent(layout, numLevels-i-1, j*m, numLevels-i-1, (j+1)*m-1);
				
				vlayout.add(layout);
				vlayout.setFlexGrow(1, layout);
				
				container1[i][j] = label1;
				container2[i][j] = label2;
			}
		}
		
		if(consolacion){
			// Final de consolacion
			VerticalLayout layout = new VerticalLayout();
			layout.setWidth("100%");
			layout.setPadding(false);
			layout.setSpacing(false);
			layout.setAlignItems(Alignment.CENTER);
			//layout.getElement().getStyle().set("background-color", "blue");
			Button label1 = new Button("Grupo "+0+","+1);
			label1.setWidth("100%");
			Button label2 = new Button("Grupo "+0+","+1);
			label2.setWidth("100%");
			layout.add(label1, label2);
			
			//grid.addComponent(layout, numLevels-1, rows, numLevels-1, rows);
			
			this.add(layout);
			this.setHorizontalComponentAlignment(Alignment.END, layout);
			
			container1[0][1] = label1;
			container2[0][1] = label2;
		}
		
	}
	
	public void setItems(List<SpeedKOSystemEntity> records) {
        for(SpeedKOSystemEntity rec:records){
        	//System.out.println("Rec="+rec);
        	Button label1 = container1[rec.getEliminatoria().ordinal()][rec.getGrupo()];
        	Button label2 = container2[rec.getEliminatoria().ordinal()][rec.getGrupo()];
        	if(label1!=null){
        		label1.setText(rec.getDorsal1()+" "+rec.getNombre1()+" "+rec.getApellidos1());
        		if(rec.getGanador()!=null && rec.getPatinador1()!=null &&
        				rec.getGanador().longValue()==rec.getPatinador1().longValue()){
        			label1.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        			label2.addThemeVariants(ButtonVariant.LUMO_ERROR);
        		}
        	}
        	if(label2!=null){
        		label2.setText(rec.getDorsal2()+" "+rec.getNombre2()+" "+rec.getApellidos2());
        		if(rec.getGanador()!=null && rec.getPatinador2()!=null &&
        				rec.getGanador().longValue()==rec.getPatinador2().longValue()){
        			label2.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        			label1.addThemeVariants(ButtonVariant.LUMO_ERROR);
        		}
        	}
        }
    }
	
}
