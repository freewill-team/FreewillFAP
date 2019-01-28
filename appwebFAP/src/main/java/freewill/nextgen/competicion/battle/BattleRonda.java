package freewill.nextgen.competicion.battle;

import java.util.List;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.data.BattleRondaEntity;
import freewill.nextgen.data.CompeticionEntity;
import freewill.nextgen.data.BattleRondaEntity.EliminatoriaEnum;
import freewill.nextgen.genericCrud.GenericCrudLogic;
import freewill.nextgen.hmi.utils.Messages;

@SuppressWarnings("serial")
public class BattleRonda extends VerticalLayout {
	
	public final String VIEW_NAME = Messages.get().getKey("kosystem");
	private Long competicion = null;
	private String competicionStr = "";
	private Long categoria = null;
	private String categoriaStr = "";
	private ArbolBattle grid;
	private BattleRondaForm form;
	private BattleCrudLogic viewLogic;
	private BattleCrudView parent = null;
	private boolean competiClosed = false;

	public BattleRonda(Long categoria, String labelcategoria, Long competicion, 
			String label, EliminatoriaEnum ronda, BattleCrudView parent){
		this.competicion = competicion;
		this.competicionStr = label;
		this.categoria = categoria;
		this.categoriaStr = labelcategoria;
		//this.ronda = ronda;
		this.parent = parent;
		
		viewLogic = new BattleCrudLogic(null, this);
		
		List<BattleRondaEntity> records = viewLogic.initKO(this.competicion, this.categoria);
		if(ronda==null)
			ronda = viewLogic.existeKO(competicion, categoria);
		
		grid = new ArbolBattle(ronda, new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
            	BattleRondaEntity rec = null;
            	String recId = event.getButton().getId();
            	if(recId!=null){
            		rec = viewLogic.findRecordKO(recId);
            	}
            	form.editRecord(rec);
            }
        });
        
        form = new BattleRondaForm(viewLogic);
        
        HorizontalLayout gridLayout = new HorizontalLayout();
        gridLayout.setSizeFull();
        //gridLayout.setWidth("100%");
        gridLayout.setMargin(true);
        gridLayout.setSpacing(false); // true
        gridLayout.addComponent(grid);
        gridLayout.addComponent(form);
        gridLayout.setExpandRatio(grid, 3);
        gridLayout.setExpandRatio(form, 1);
		
		HorizontalLayout topLayout = createTopBar();
	    //addComponent(new GenericHeader(VIEW_NAME, FontAwesome.FOLDER));
	    addComponent(topLayout);
	    addComponent(gridLayout);
	    setSizeFull();
	    setExpandRatio(gridLayout, 1);
	    setStyleName("crud-main-layout");
	    
	    grid.setRecords(records);
	    
	    GenericCrudLogic<CompeticionEntity> competiLogic = 
	    		new GenericCrudLogic<CompeticionEntity>(null, CompeticionEntity.class, "id");
	    CompeticionEntity competi = competiLogic.findRecord(""+competicion);
	    competiClosed = competi.getActive();
	    form.setEnabled(competiClosed);
	}
	
	public HorizontalLayout createTopBar() {
		
		Button prevButton = new Button(Messages.get().getKey("prev"));
		prevButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		prevButton.setIcon(FontAwesome.ARROW_LEFT);
		prevButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                // Previous screen
            	parent.gotoTrial();
            }
        });
		prevButton.setEnabled(true);
		
		Button nextButton = new Button(Messages.get().getKey("next"));
		nextButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		nextButton.setIcon(FontAwesome.ARROW_RIGHT);
		nextButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                // Next screen
            	parent.gotoActaFinal();
            }
        });
		nextButton.setEnabled(true);
        
        Label competicionLabel = new Label(competicionStr+" / "+categoriaStr);
        competicionLabel.setStyleName(ValoTheme.LABEL_LARGE);
        competicionLabel.addStyleName(ValoTheme.LABEL_COLORED);
        competicionLabel.addStyleName(ValoTheme.LABEL_BOLD);

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
        topLayout.setSpacing(true);
        topLayout.setMargin(true);
        topLayout.setWidth("100%");
        topLayout.addComponent(competicionLabel);
        topLayout.addComponent(prevButton);
        topLayout.addComponent(nextButton);
        topLayout.setComponentAlignment(competicionLabel, Alignment.MIDDLE_LEFT);
        topLayout.setExpandRatio(competicionLabel, 1);
        topLayout.setStyleName("top-bar");
        return topLayout;
    }
	
	public void showError(String msg) {
        Notification.show(msg, Type.ERROR_MESSAGE);
    }

    public void showSaveNotification(String msg) {
        Notification.show(msg, Type.TRAY_NOTIFICATION);
    }
    
    public void clearSelection() {
    	try{
    		//grid.getSelectionModel().reset();
    	}
    	catch(Exception e){
    		System.out.println("clearSelection: "+e.getMessage());
    	}
    }

    public void selectRow(BattleRondaEntity row) {
        //((SelectionModel.Single) grid.getSelectionModel()).select(row);
    }

    public BattleRondaEntity getSelectedRow() {
        return null;//grid.getSelectedRow();
    }

    public void editRecord(BattleRondaEntity rec) {
        //form.editRecord(rec);
    }

    public void showRecords(List<BattleRondaEntity> records) {
        if(records!=null && records.size()>0)
        	grid.setRecords(records);
    }

    public void refreshRecord(BattleRondaEntity rec) {
        //grid.refresh(rec);
        //grid.scrollTo(rec);
    }
    
}
