package freewill.nextgen.competicion.speed;

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

import freewill.nextgen.data.SpeedKOSystemEntity.EliminatoriaEnum;
import freewill.nextgen.data.CompeticionEntity;
import freewill.nextgen.data.SpeedKOSystemEntity;
import freewill.nextgen.data.SpeedTimeTrialEntity.RondaEnum;
import freewill.nextgen.genericCrud.GenericCrudLogic;
import freewill.nextgen.hmi.common.ConfirmDialog;
import freewill.nextgen.hmi.utils.Messages;

@SuppressWarnings("serial")
public class SpeedKOsystem extends VerticalLayout {
	
	public final String VIEW_NAME = Messages.get().getKey("kosystem");
	private Long competicion = null;
	private String competicionStr = "";
	private Long categoria = null;
	private String categoriaStr = "";
	private EliminatoriaEnum ronda = EliminatoriaEnum.FINAL;
	private ArbolKOSystem grid;
	private SpeedKOSystemForm form;
	private SpeedCrudLogic viewLogic;
	private SpeedCrudView parent = null;
	private boolean competiOpen = false;

	public SpeedKOsystem(Long categoria, String labelcategoria, Long competicion, 
			String label, EliminatoriaEnum ronda, SpeedCrudView parent){
		this.competicion = competicion;
		this.competicionStr = label;
		this.categoria = categoria;
		this.categoriaStr = labelcategoria;
		this.ronda = ronda;
		this.parent = parent;
		
		viewLogic = new SpeedCrudLogic(null, this);
		
		grid = new ArbolKOSystem(ronda, new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
            	SpeedKOSystemEntity rec = null;
            	String recId = event.getButton().getId();
            	if(recId!=null){
            		rec = viewLogic.findRecordKO(recId);
            	}
            	form.setEnabled(competiOpen);
            	form.editRecord(rec);
            }
        });
        
        form = new SpeedKOSystemForm(viewLogic);
        
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
	    
	    grid.setRecords(viewLogic.initKO(this.competicion, this.categoria, ronda));
	    
	    GenericCrudLogic<CompeticionEntity> competiLogic = 
	    		new GenericCrudLogic<CompeticionEntity>(null, CompeticionEntity.class, "id");
	    CompeticionEntity competi = competiLogic.findRecord(""+competicion);
	    competiOpen = competi.getActive();
	    form.setEnabled(competiOpen);
	}
	
	public HorizontalLayout createTopBar() {
		
		Button prevButton = new Button(Messages.get().getKey("prev"));
		prevButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		prevButton.setIcon(FontAwesome.ARROW_LEFT);
		prevButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                // Previous screen
            	parent.gotoTimeTrial(RondaEnum.RESULTADOS);
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
            	ConfirmDialog cd = new ConfirmDialog(
            			"Esta acción publicará los resultados en la web pública.\n" +
            			"¿ Desea continuar ?");
                cd.setOKAction(new ClickListener() {
                	@Override
                    public void buttonClick(final ClickEvent event) {
                    	cd.close();
                    	parent.gotoActaFinal();
                	}
                });
                getUI().addWindow(cd);
            }
        });
		nextButton.setEnabled(true);
        
        Label competicionLabel = new Label(competicionStr+" / "+categoriaStr+" / "+ronda);
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

    public void selectRow(SpeedKOSystemEntity row) {
        //((SelectionModel.Single) grid.getSelectionModel()).select(row);
    }

    public SpeedKOSystemEntity getSelectedRow() {
        //grid.getSelectedRow();
    	return null;
    }

    public void editRecord(SpeedKOSystemEntity rec) {
        form.editRecord(rec);
    }

    public void showRecords(List<SpeedKOSystemEntity> records) {
        if(records!=null && records.size()>0)
        	grid.setRecords(records);
    }

    public void refreshRecord(SpeedKOSystemEntity rec) {
        //grid.refresh(rec);
        //grid.scrollTo(rec);
    }
    
}
