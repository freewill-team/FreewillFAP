package freewill.nextgen.competicion.jam;

import java.util.List;
import java.util.Date;

import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid.SelectionModel;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.competicion.classic.SlalomMatrixForm;
import freewill.nextgen.competicion.jam.JamShowForm;
import freewill.nextgen.data.JamShowEntity;
import freewill.nextgen.data.ClassicShowEntity;
import freewill.nextgen.data.CompeticionEntity;
import freewill.nextgen.genericCrud.GenericCrudLogic;
import freewill.nextgen.genericCrud.GenericGrid;
import freewill.nextgen.hmi.common.ConfirmDialog;
import freewill.nextgen.hmi.utils.Messages;

@SuppressWarnings("serial")
public class JamFinal extends CssLayout {
	
	public final String VIEW_NAME = Messages.get().getKey("Jamshow");
	private Long competicion = null;
	private String competicionStr = "";
	private Long categoria = null;
	private String categoriaStr = "";
	private GenericGrid<JamShowEntity> grid;
	private JamShowForm form;
	private SlalomMatrixForm formCalc;
	private JamCrudLogic viewLogic;
	private JamCrudView parent = null;
	private boolean competiOpen = false;
	private Button nextButton = null;
	private Button delete = null;
	
	public JamFinal(Long categoria, String labelcategoria, Long competicion, 
			String label, JamCrudView parent){
		this.competicion = competicion;
		this.competicionStr = label;
		this.categoria = categoria;
		this.categoriaStr = labelcategoria;
		this.parent = parent;
		viewLogic = new JamCrudLogic(this);
		
		setSizeFull();
        addStyleName("crud-view");
        HorizontalLayout topLayout = createTopBar();
		
		grid = new GenericGrid<JamShowEntity>(JamShowEntity.class,
				"id", "dorsalDuo", "orden1", "nombreDuo",
        		"penalizaciones",
        		"tecnicaJuez1", "artisticaJuez1", "sincronizacionJuez1", "totalJuez1", "rankingJuez1", 
        		"tecnicaJuez2", "artisticaJuez2", "sincronizacionJuez2", "totalJuez2", "rankingJuez2", 
        		"tecnicaJuez3", "artisticaJuez3", "sincronizacionJuez3", "totalJuez3", "rankingJuez3");

		//grid.setFrozenColumnCount(2);
		grid.addSelectionListener(new SelectionListener() {
            @Override
            public void select(SelectionEvent event) {
            	viewLogic.rowSelected(grid.getSelectedRow());
            }
        });
        
        form = new JamShowForm(viewLogic);
        formCalc = new SlalomMatrixForm();
          
        VerticalLayout barAndGridLayout = new VerticalLayout();
        barAndGridLayout.addComponent(topLayout);
        barAndGridLayout.addComponent(grid);
        barAndGridLayout.setMargin(true);
        barAndGridLayout.setSpacing(true);
        barAndGridLayout.setSizeFull();
        barAndGridLayout.setExpandRatio(grid, 1);
        barAndGridLayout.setStyleName("crud-main-layout");
        
        addComponent(barAndGridLayout);
        addComponent(form);
        addComponent(formCalc);
	    
	    viewLogic.initGrid(this.competicion, this.categoria);
	    
	    GenericCrudLogic<CompeticionEntity> competiLogic = 
	    		new GenericCrudLogic<CompeticionEntity>(null, CompeticionEntity.class, "id");
	    CompeticionEntity competi = competiLogic.findRecord(""+competicion);
	    competiOpen = competi.getActive();
	    if(competi.getFechaInicio().after(new Date())){
    		this.showError("Esta Competición aun no puede comenzar.");
    		competiOpen = false;
    		nextButton.setEnabled(false);
    	}
	    //form.setEnabled(competiOpen);
	}
	
	public HorizontalLayout createTopBar() {
		
		Button prevButton = new Button(/*Messages.get().getKey("prev")*/);
		prevButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		//prevButton.addStyleName("toggle-label");
		prevButton.setIcon(FontAwesome.ARROW_LEFT);
		prevButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	// Previous screen
            	parent.enter(null);
            }
        });
		prevButton.setEnabled(true);
		
		nextButton = new Button(/*Messages.get().getKey("next")*/);
		nextButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		//nextButton.addStyleName("toggle-label");
		nextButton.setIcon(FontAwesome.ARROW_RIGHT);
		nextButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {	
            	// Next screen
             	parent.gotoResultados();
            }
        });
		nextButton.setEnabled(true);
		
		delete = new Button("");
		delete.addStyleName(ValoTheme.BUTTON_DANGER);
		delete.setIcon(FontAwesome.REMOVE);
		delete.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	ConfirmDialog cd = new ConfirmDialog("Realmente desea eliminar TODOS los datos?");
            	cd.setOKAction(new ClickListener() {
                    @Override
                    public void buttonClick(final ClickEvent event) {
            			cd.close();
            			if(viewLogic!=null){
            				viewLogic.deleteAll(competicion, categoria);
            				parent.showSaveNotification("Datos borrados.");
            				setEnabled(false);
            			}
                    }
                });
            	getUI().addWindow(cd);
            }
        });
        
        Label competicionLabel = new Label(competicionStr + " / " + categoriaStr);
        competicionLabel.setStyleName(ValoTheme.LABEL_LARGE);
        competicionLabel.addStyleName(ValoTheme.LABEL_COLORED);
        competicionLabel.addStyleName(ValoTheme.LABEL_BOLD);
        //competicionLabel.addStyleName("toggle-label");
        
        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
        topLayout.setSpacing(true);
        //topLayout.setMargin(true);
        topLayout.setWidth("100%");
        if(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.ADMIN))
        	topLayout.addComponent(delete);
        topLayout.addComponent(competicionLabel);
        topLayout.addComponent(prevButton);
        topLayout.addComponent(nextButton);
        topLayout.setComponentAlignment(competicionLabel, Alignment.MIDDLE_LEFT);
        topLayout.setExpandRatio(competicionLabel, 1);
        topLayout.setStyleName("top-bar");
        return topLayout;
    }
	
	public void showError(String msg) {
        Notification.show(msg, Type.WARNING_MESSAGE); //ERROR_MESSAGE);
    }

    public void showSaveNotification(String msg) {
        Notification.show(msg, Type.TRAY_NOTIFICATION);
    }
    
    public void clearSelection() {
    	try{
    		grid.getSelectionModel().reset();
    	}
    	catch(Exception e){
    		System.out.println("clearSelection: "+e.getMessage());
    	}
    }

    public void selectRow(JamShowEntity row) {
        ((SelectionModel.Single) grid.getSelectionModel()).select(row);
    }

    public JamShowEntity getSelectedRow() {
        return grid.getSelectedRow();
    }

    public void editRecord(JamShowEntity rec) {
    	System.out.println("Entrando en editRecord "+rec);
    	if (rec != null) {
            form.addStyleName("visible");
            //form.setEnabled(true);
        } else {
            form.removeStyleName("visible");
            //form.setEnabled(false);
        }
        form.editRecord(rec, competiOpen);
    }

    public void showRecords(List<JamShowEntity> records) {
        grid.setRecords(records); 
        if(records!=null && records.size()>0){
    		grid.sort("orden1", SortDirection.DESCENDING);
    		//this.selectRow(records.get(0));
        }
    }

    public void refreshRecord(JamShowEntity rec) {
        grid.refresh(rec);
        grid.scrollTo(rec);
    }

    public void removeRecord(JamShowEntity rec) {
        // Not allowed here grid.remove(rec);
    }
    
	public void setGridColumns(int i) {
		grid.getColumn("tecnicaJuez1").setHidden(true);
		grid.getColumn("artisticaJuez1").setHidden(true);
		grid.getColumn("sincronizacionJuez1").setHidden(true);
		grid.getColumn("totalJuez1").setHidden(true);
		grid.getColumn("rankingJuez1").setHidden(true);
		grid.getColumn("tecnicaJuez2").setHidden(true);
		grid.getColumn("artisticaJuez2").setHidden(true);
		grid.getColumn("sincronizacionJuez2").setHidden(true);
		grid.getColumn("totalJuez2").setHidden(true);
		grid.getColumn("rankingJuez2").setHidden(true);
		grid.getColumn("tecnicaJuez3").setHidden(true);
		grid.getColumn("artisticaJuez3").setHidden(true);
		grid.getColumn("sincronizacionJuez3").setHidden(true);
		grid.getColumn("totalJuez3").setHidden(true);
		grid.getColumn("rankingJuez3").setHidden(true);
		switch(i){
    	case 0: 
    		grid.getColumn("tecnicaJuez1").setHidden(false);
    		grid.getColumn("artisticaJuez1").setHidden(false);
    		grid.getColumn("sincronizacionJuez1").setHidden(false);
    		grid.getColumn("totalJuez1").setHidden(false);
    		grid.getColumn("rankingJuez1").setHidden(false);
    		grid.getColumn("tecnicaJuez2").setHidden(false);
    		grid.getColumn("artisticaJuez2").setHidden(false);
    		grid.getColumn("sincronizacionJuez2").setHidden(false);
    		grid.getColumn("totalJuez2").setHidden(false);
    		grid.getColumn("rankingJuez2").setHidden(false);
    		grid.getColumn("tecnicaJuez3").setHidden(false);
    		grid.getColumn("artisticaJuez3").setHidden(false);
    		grid.getColumn("sincronizacionJuez3").setHidden(false);
    		grid.getColumn("totalJuez3").setHidden(false);
    		grid.getColumn("rankingJuez3").setHidden(false);
    		break;
    	case 1: 
    		grid.getColumn("tecnicaJuez1").setHidden(false);
    		grid.getColumn("artisticaJuez1").setHidden(false);
    		grid.getColumn("sincronizacionJuez1").setHidden(false);
    		grid.getColumn("totalJuez1").setHidden(false);
    		grid.getColumn("rankingJuez1").setHidden(false);
    		break;
    	case 2: 
    		grid.getColumn("tecnicaJuez2").setHidden(false);
    		grid.getColumn("artisticaJuez2").setHidden(false);
    		grid.getColumn("sincronizacionJuez2").setHidden(false);
    		grid.getColumn("totalJuez2").setHidden(false);
    		grid.getColumn("rankingJuez2").setHidden(false);
    		break;
    	case 3: 
    		grid.getColumn("tecnicaJuez3").setHidden(false);
    		grid.getColumn("artisticaJuez3").setHidden(false);
    		grid.getColumn("sincronizacionJuez3").setHidden(false);
    		grid.getColumn("totalJuez3").setHidden(false);
    		grid.getColumn("rankingJuez3").setHidden(false);
    		break;
    	}
	}

	public void openCalculadora(JamShowEntity rec) {
		System.out.println("Entrando en openCalculadora "+rec);
    	if (rec != null) {
    		formCalc.addStyleName("visible");
    		formCalc.setEnabled(true);
        } else {
        	formCalc.removeStyleName("visible");
        	formCalc.setEnabled(false);
        }
		formCalc.editRecord(/*rec*/);
	}
	
	public void closeCalculadora() {
		formCalc.close();
	}
}
