package fwt.apppubfap.battle;

import java.util.List;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import fwt.apppubfap.dtos.BattleRondaEntity;
import fwt.apppubfap.dtos.BattleRondaEntity.EliminatoriaEnum;

@SuppressWarnings("serial")
public class ArbolBattle extends VerticalLayout {
	
	private Button[][] container1 = null;
	private Button[][] container2 = null;
	private Button[][] container3 = null;
	private Button[][] container4 = null;
	private int numLevels = 3;

	public ArbolBattle(EliminatoriaEnum levels){
        this.setSizeFull();
        this.setPadding(false);
		this.setSpacing(false);
        //this.getElement().getStyle().set("overflow", "auto");
		
		this.numLevels = levels.ordinal()+1;
		int rows = (int)(Math.pow(2.0, numLevels));
		container1 = new Button[numLevels][rows];
		container2 = new Button[numLevels][rows];
		container3 = new Button[numLevels][rows];
		container4 = new Button[numLevels][rows];
		
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
				Button label3 = new Button("Grupo "+i+","+j);
				label3.setWidth("100%");
				Button label4 = new Button("Grupo "+i+","+j);
				label4.setWidth("100%");
				celda.addCelda(label1);
				celda.addCelda(label2);
				celda.addCelda(label3);
				celda.addCelda(label4);
				vlayout.addCelda(celda);
				
				container1[i][j] = label1;
				container2[i][j] = label2;
				container3[i][j] = label3;
				container4[i][j] = label4;
			}
		}
	}
	
	public void setItems(List<BattleRondaEntity> records) {
        for(BattleRondaEntity rec:records){
        	//System.out.println("Rec="+rec);
        	Button label1 = container1[rec.getEliminatoria().ordinal()][rec.getGrupo()];
        	Button label2 = container2[rec.getEliminatoria().ordinal()][rec.getGrupo()];
        	Button label3 = container3[rec.getEliminatoria().ordinal()][rec.getGrupo()];
        	Button label4 = container4[rec.getEliminatoria().ordinal()][rec.getGrupo()];
        	
        	if(label1!=null){
        		label1.setText(rec.getDorsal1()+" "+rec.getNombre1()+" "+rec.getApellidos1());
        		
        		if(rec.getGanador1()!=null && rec.getPatinador1()!=null &&
        				rec.getGanador1().compareTo(rec.getPatinador1())==0){
        			label1.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        			label1.setText(label1.getText()+" (1)");
        		}
        		else if(rec.getGanador2()!=null && rec.getPatinador1()!=null &&
        				rec.getGanador2().compareTo(rec.getPatinador1())==0){
        			label1.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        			label1.setText(label1.getText()+" (2)");
        		}
        		else if(rec.getGanador3()!=null && rec.getPatinador1()!=null &&
        				rec.getGanador3().compareTo(rec.getPatinador1())==0){
        			if(rec.getEliminatoria()==EliminatoriaEnum.FINAL)
        				label1.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        			else
        				label1.addThemeVariants(ButtonVariant.LUMO_ERROR);
        			label1.setText(label1.getText()+" (3)");
        		}
        		else if(rec.getGanador4()!=null && rec.getPatinador1()!=null &&
        				rec.getGanador4().compareTo(rec.getPatinador1())==0){
        			label1.addThemeVariants(ButtonVariant.LUMO_ERROR);
        			label1.setText(label1.getText()+" (4)");
        		}
        	}
        	if(label2!=null){
        		label2.setText(rec.getDorsal2()+" "+rec.getNombre2()+" "+rec.getApellidos2());
        		
        		if(rec.getGanador1()!=null && rec.getPatinador2()!=null &&
        				rec.getGanador1().compareTo(rec.getPatinador2())==0){
        			label2.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        			label2.setText(label2.getText()+" (1)");
        		}
        		else if(rec.getGanador2()!=null && rec.getPatinador2()!=null &&
        				rec.getGanador2().compareTo(rec.getPatinador2())==0){
        			label2.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        			label2.setText(label2.getText()+" (2)");
        		}
        		else if(rec.getGanador3()!=null && rec.getPatinador2()!=null &&
        				rec.getGanador3().compareTo(rec.getPatinador2())==0){
        			if(rec.getEliminatoria()==EliminatoriaEnum.FINAL)
        				label2.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        			else
        				label2.addThemeVariants(ButtonVariant.LUMO_ERROR);
        			label2.setText(label2.getText()+" (3)");
        		}
        		else if(rec.getGanador4()!=null && rec.getPatinador2()!=null &&
        				rec.getGanador4().compareTo(rec.getPatinador2())==0){
        			label2.addThemeVariants(ButtonVariant.LUMO_ERROR);
        			label2.setText(label2.getText()+" (4)");
        		}
        	}
        	
        	if(label3!=null){
        		label3.setText(rec.getDorsal3()+" "+rec.getNombre3()+" "+rec.getApellidos3());
        		
        		if(rec.getGanador1()!=null && rec.getPatinador3()!=null && 
        				rec.getGanador1().compareTo(rec.getPatinador3())==0){
        			label3.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        			label3.setText(label3.getText()+" (1)");
        		}
        		else if(rec.getGanador2()!=null && rec.getPatinador3()!=null &&
        				rec.getGanador2().compareTo(rec.getPatinador3())==0){
        			label3.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        			label3.setText(label3.getText()+" (2)");
        		}
        		else if(rec.getGanador3()!=null && rec.getPatinador3()!=null &&
        				rec.getGanador3().compareTo(rec.getPatinador3())==0){
        			if(rec.getEliminatoria()==EliminatoriaEnum.FINAL)
        				label3.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        			else
        				label3.addThemeVariants(ButtonVariant.LUMO_ERROR);
        			label3.setText(label3.getText()+" (3)");
        		}
        		else if(rec.getGanador4()!=null && rec.getPatinador3()!=null &&
        				rec.getGanador4().compareTo(rec.getPatinador3())==0){
        			label3.addThemeVariants(ButtonVariant.LUMO_ERROR);
        			label3.setText(label3.getText()+" (4)");
        		}
        	}
        	if(label4!=null){
        		label4.setText(rec.getDorsal4()+" "+rec.getNombre4()+" "+rec.getApellidos4());
        		
        		if(rec.getGanador1()!=null && rec.getPatinador4()!=null &&
        				rec.getGanador1().compareTo(rec.getPatinador4())==0){
        			label4.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        			label4.setText(label4.getText()+" (1)");
        		}
        		else if(rec.getGanador2()!=null && rec.getPatinador4()!=null &&
        				rec.getGanador2().compareTo(rec.getPatinador4())==0){
        			label4.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        			label4.setText(label4.getText()+" (2)");
        		}
        		else if(rec.getGanador3()!=null && rec.getPatinador4()!=null &&
        				rec.getGanador3().compareTo(rec.getPatinador4())==0){
        			if(rec.getEliminatoria()==EliminatoriaEnum.FINAL)
        				label4.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        			else
        				label4.addThemeVariants(ButtonVariant.LUMO_ERROR);
        			label4.setText(label4.getText()+" (3)");
        		}
        		else if(rec.getGanador4()!=null && rec.getPatinador4()!=null &&
        				rec.getGanador4().compareTo(rec.getPatinador4())==0){	
        			label4.addThemeVariants(ButtonVariant.LUMO_ERROR);
        			label4.setText(label4.getText()+" (4)");
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
