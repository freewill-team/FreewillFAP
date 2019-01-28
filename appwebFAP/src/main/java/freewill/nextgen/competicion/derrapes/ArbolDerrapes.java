package freewill.nextgen.competicion.derrapes;

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

import freewill.nextgen.data.DerrapesRondaEntity;
import freewill.nextgen.data.DerrapesRondaEntity.EliminatoriaEnum;

@SuppressWarnings("serial")
public class ArbolDerrapes extends Panel {
	
	private Label[][] container1 = null;
	private Label[][] container2 = null;
	private Label[][] container3 = null;
	private Label[][] container4 = null;
	private Button[][] containerBtn = null;
	int numLevels = 3;

	public ArbolDerrapes(EliminatoriaEnum levels, ClickListener action){
		setStyleName(ValoTheme.LAYOUT_CARD);
        setSizeFull();
        Responsive.makeResponsive(this);
		
		this.numLevels = levels.ordinal()+1;
		int rows = (int)(Math.pow(2.0, numLevels));
		container1 = new Label[numLevels][rows];
		container2 = new Label[numLevels][rows];
		container3 = new Label[numLevels][rows];
		container4 = new Label[numLevels][rows];
		containerBtn = new Button[numLevels][rows];
		
		VerticalLayout vlayout = new VerticalLayout();
		vlayout.setSizeFull();
		vlayout.setMargin(false);
		vlayout.setSpacing(false);
		vlayout.addStyleName("dashboard-view");
		
		GridLayout grid = new GridLayout(numLevels, rows);
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
				Label label3 = new Label("Grupo "+i+","+j);
				label3.setStyleName(ValoTheme.LABEL_FAILURE);
				label3.setWidth("100%");
				Label label4 = new Label("Grupo "+i+","+j);
				label4.setStyleName(ValoTheme.LABEL_FAILURE);	
				label4.setWidth("100%");
				layout.addComponents(label1, label2, label3, label4);
				
				Button button = new Button();
				button.setStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
				button.addStyleName(ValoTheme.BUTTON_LARGE);
				button.setIcon(FontAwesome.FORWARD);
				button.addClickListener(action);
				
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
				container3[i][j] = label3;
				container4[i][j] = label4;
				containerBtn[i][j] = button;
			}
		}
	}
	
	public void setRecords(List<DerrapesRondaEntity> records) {
        for(DerrapesRondaEntity rec:records){
        	System.out.println("DerrapesRondaEntity="+rec);
        	Label label1 = (Label) container1[rec.getEliminatoria().ordinal()][rec.getGrupo()];
        	Label label2 = (Label) container2[rec.getEliminatoria().ordinal()][rec.getGrupo()];
        	Label label3 = (Label) container3[rec.getEliminatoria().ordinal()][rec.getGrupo()];
        	Label label4 = (Label) container4[rec.getEliminatoria().ordinal()][rec.getGrupo()];
        	Button button = (Button) containerBtn[rec.getEliminatoria().ordinal()][rec.getGrupo()];
        	if(label1!=null){
        		label1.setValue(rec.getNombre1()+" "+rec.getApellidos1());
        		label1.setDescription(rec.getNombre1()+" "+rec.getApellidos1());
        		if(rec.getGanador1()!=null && rec.getGanador1()==rec.getPatinador1()){
        			label1.setStyleName(ValoTheme.LABEL_SUCCESS);
        			label1.setValue(label1.getValue()+" (1)");
        		}
        		else if(rec.getGanador2()!=null && rec.getGanador2()==rec.getPatinador1()){
        			label1.setStyleName(ValoTheme.LABEL_SUCCESS);
        			label1.setValue(label1.getValue()+" (2)");
        		}
        		else if(rec.getGanador3()!=null && rec.getGanador3()==rec.getPatinador1()){
        			if(rec.getEliminatoria()==EliminatoriaEnum.FINAL)
        				label1.setStyleName(ValoTheme.LABEL_SUCCESS);
        			else
        				label1.setStyleName(ValoTheme.LABEL_FAILURE);
        			label1.setValue(label1.getValue()+" (3)");
        		}
        		else if(rec.getGanador4()!=null && rec.getGanador4()==rec.getPatinador1()){	
        			label1.setStyleName(ValoTheme.LABEL_FAILURE);
        			label1.setValue(label1.getValue()+" (4)");
        		}
        	}
        	if(label2!=null){
        		label2.setValue(rec.getNombre2()+" "+rec.getApellidos2());
        		label2.setDescription(rec.getNombre2()+" "+rec.getApellidos2());
        		if(rec.getGanador1()!=null && rec.getGanador1()==rec.getPatinador2()){
        			label2.setStyleName(ValoTheme.LABEL_SUCCESS);
        			label2.setValue(label2.getValue()+" (1)");
        		}
        		else if(rec.getGanador2()!=null && rec.getGanador2()==rec.getPatinador2()){
        			label2.setStyleName(ValoTheme.LABEL_SUCCESS);
        			label2.setValue(label2.getValue()+" (2)");
        		}
        		else if(rec.getGanador3()!=null && rec.getGanador3()==rec.getPatinador2()){
        			if(rec.getEliminatoria()==EliminatoriaEnum.FINAL)
        				label2.setStyleName(ValoTheme.LABEL_SUCCESS);
        			else
        				label2.setStyleName(ValoTheme.LABEL_FAILURE);
        			label2.setValue(label2.getValue()+" (3)");
        		}
        		else if(rec.getGanador4()!=null && rec.getGanador4()==rec.getPatinador2()){	
        			label2.setStyleName(ValoTheme.LABEL_FAILURE);
        			label2.setValue(label2.getValue()+" (4)");
        		}
        	}
        	
        	if(label3!=null){
        		label3.setValue(rec.getNombre3()+" "+rec.getApellidos3());
        		label3.setDescription(rec.getNombre3()+" "+rec.getApellidos3());
        		if(rec.getGanador1()!=null && rec.getGanador1()==rec.getPatinador3()){
        			label3.setStyleName(ValoTheme.LABEL_SUCCESS);
        			label3.setValue(label3.getValue()+" (1)");
        		}
        		else if(rec.getGanador2()!=null && rec.getGanador2()==rec.getPatinador3()){
        			label3.setStyleName(ValoTheme.LABEL_SUCCESS);
        			label3.setValue(label3.getValue()+" (2)");
        		}
        		else if(rec.getGanador3()!=null && rec.getGanador3()==rec.getPatinador3()){
        			if(rec.getEliminatoria()==EliminatoriaEnum.FINAL)
        				label3.setStyleName(ValoTheme.LABEL_SUCCESS);
        			else
        				label3.setStyleName(ValoTheme.LABEL_FAILURE);
        			label3.setValue(label3.getValue()+" (3)");
        		}
        		else if(rec.getGanador4()!=null && rec.getGanador4()==rec.getPatinador3()){	
        			label3.setStyleName(ValoTheme.LABEL_FAILURE);
        			label3.setValue(label3.getValue()+" (4)");
        		}
        	}
        	if(label4!=null){
        		label4.setValue(rec.getNombre4()+" "+rec.getApellidos4());
        		label4.setDescription(rec.getNombre4()+" "+rec.getApellidos4());
        		if(rec.getGanador1()!=null && rec.getGanador1()==rec.getPatinador4()){
        			label4.setStyleName(ValoTheme.LABEL_SUCCESS);
        			label4.setValue(label4.getValue()+" (1)");
        		}
        		else if(rec.getGanador2()!=null && rec.getGanador2()==rec.getPatinador4()){
        			label4.setStyleName(ValoTheme.LABEL_SUCCESS);
        			label4.setValue(label4.getValue()+" (2)");
        		}
        		else if(rec.getGanador3()!=null && rec.getGanador3()==rec.getPatinador4()){
        			if(rec.getEliminatoria()==EliminatoriaEnum.FINAL)
        				label4.setStyleName(ValoTheme.LABEL_SUCCESS);
        			else
        				label4.setStyleName(ValoTheme.LABEL_FAILURE);
        			label4.setValue(label4.getValue()+" (3)");
        		}
        		else if(rec.getGanador4()!=null && rec.getGanador4()==rec.getPatinador4()){	
        			label4.setStyleName(ValoTheme.LABEL_FAILURE);
        			label4.setValue(label4.getValue()+" (4)");
        		}
        	}
        	
        	if(button!=null){
        		button.setId(""+rec.getId());
        	}
        }
    }
	
}
