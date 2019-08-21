package fwt.apppubfap.speed;

import java.util.List;

import com.vaadin.flow.component.Component;
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
        this.setPadding(false);
		this.setSpacing(false);
        //this.getElement().getStyle().set("overflow", "auto");
        
        //consolacion = EntryPoint.get().getConfigBoolean(ConfigItemEnum.FINALCONSOLACIONSPEED);
        additionalrow = (consolacion?1:0);
		
		this.numLevels = levels.ordinal()+1;
		int rows = (int)(Math.pow(2.0, numLevels));
		container1 = new Button[numLevels][rows+additionalrow];
		container2 = new Button[numLevels][rows+additionalrow];
		
		HorizontalLayout hlayout = new HorizontalLayout();
		hlayout.setSizeFull();
		hlayout.setPadding(false);
		hlayout.setSpacing(false);
		//hlayout.getElement().getStyle().set("background-color", "yellow");
		hlayout.getElement().getStyle().set("width", "auto");
		hlayout.getElement().getStyle().set("height", "auto");
		hlayout.setDefaultVerticalComponentAlignment(Alignment.STRETCH);
		this.add(hlayout);
		
		for(int i=numLevels-1;i>=0;i--){
			
			Celda vlayout = new Celda();
			//vlayout.setColors("orange", "green");
			hlayout.add(vlayout);
			
			int n = (int)(Math.pow(2.0, i));
			for(int j=0; j<n;j++){
				Celda celda = new Celda();
				celda.setColors("#F0F0F0", "white");
				
				Button label1 = new Button("Grupo "+i+","+j);
				label1.setWidth("100%");
				Button label2 = new Button("Grupo "+i+","+j);
				label2.setWidth("100%");
				celda.addCelda(label1);
				celda.addCelda(label2);
				vlayout.addCelda(celda);
				
				container1[i][j] = label1;
				container2[i][j] = label2;
				
				if(i==0 && j==0 && consolacion){ 
					// Final de consolacion - celda 0,1
					Celda celda2 = new Celda();
					celda2.setColors("#F0F0F0", "white");
					
					Button label12 = new Button("Grupo "+0+","+1);
					label12.setWidth("100%");
					Button label22 = new Button("Grupo "+0+","+1);
					label22.setWidth("100%");
					celda2.addCelda(label12);
					celda2.addCelda(label22);
					vlayout.add(celda2);
					vlayout.setVerticalComponentAlignment(Alignment.END, celda2);
					
					container1[0][1] = label12;
					container2[0][1] = label22;
				}
			}
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
	
	private class Celda extends HorizontalLayout {
		
		VerticalLayout panel = new VerticalLayout();
		
		public Celda() {
			this.setPadding(false);
			this.setSpacing(false);
			this.getElement().getStyle().set("width", "100%");
			this.getElement().getStyle().set("height", "auto");
			this.setDefaultVerticalComponentAlignment(Alignment.CENTER);
			
			panel.setPadding(true);
			panel.setSpacing(true);
			panel.getElement().getStyle().set("width", "100%");
			panel.getElement().getStyle().set("height", "auto");
			this.add(panel);
		}
		
		public void addCelda(Component component){
			panel.add(component);
		}
		
		public void setColors(String dentro, String fuera){
			this.getElement().getStyle().set("background-color", fuera);
			panel.getElement().getStyle().set("background-color", dentro);
		}
		
	}

}
