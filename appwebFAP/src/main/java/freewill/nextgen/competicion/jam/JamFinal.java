package freewill.nextgen.competicion.jam;

import java.util.List;

import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Grid.SelectionModel;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;


import freewill.nextgen.data.JamShowEntity;
import freewill.nextgen.genericCrud.GenericGrid;
import freewill.nextgen.hmi.utils.Messages;

@SuppressWarnings("serial")
public class JamFinal extends VerticalLayout {
	
	public final String VIEW_NAME = Messages.get().getKey("Jamshow");
	private Long competicion = null;
	private String competicionStr = "";
	private Long categoria = null;
	private String categoriaStr = "";
	private GenericGrid<JamShowEntity> grid;
	private JamShowForm form;
	private JamCrudLogic viewLogic;
	private JamCrudView parent = null;
	
	public JamFinal(Long categoria, String labelcategoria, Long competicion, 
			String label, JamCrudView parent){
		this.competicion = competicion;
		this.competicionStr = label;
		this.categoria = categoria;
		this.categoriaStr = labelcategoria;
		this.parent = parent;
		
		viewLogic = new JamCrudLogic(this);
		
		grid = new GenericGrid<JamShowEntity>(JamShowEntity.class,
        		"id", "orden1", "dorsal", "nombre", "apellidos", 
        		"penalizaciones",
        		"tecnicaJuez1", "artisticaJuez1",
        		"tecnicaJuez2", "artisticaJuez2",
        		"tecnicaJuez3", "artisticaJuez3");
		
		grid.addSelectionListener(new SelectionListener() {
            @Override
            public void select(SelectionEvent event) {
            	viewLogic.rowSelected(grid.getSelectedRow());
            }
        });
        
        form = new JamShowForm(viewLogic);
       
        
        HorizontalLayout gridLayout = new HorizontalLayout();
        gridLayout.setWidth("100%");
        gridLayout.setMargin(true);
        gridLayout.setSpacing(true);
        gridLayout.addComponent(grid);
        gridLayout.setExpandRatio(grid, 2);
        
        gridLayout.addComponent(form);
	    gridLayout.setExpandRatio(form, 1);
       
        
		HorizontalLayout topLayout = createTopBar();
	    //addComponent(new GenericHeader(VIEW_NAME, FontAwesome.FOLDER));
	    addComponent(topLayout);
	    addComponent(gridLayout);
	    setSizeFull();
	    setExpandRatio(gridLayout, 1);
	    setStyleName("crud-main-layout");
	    
	    viewLogic.initGrid(this.competicion, this.categoria);
	}
	
	public HorizontalLayout createTopBar() {
		/*
		ComboBox selectRonda = new ComboBox();
		selectRonda.setNullSelectionAllowed(false);
        for (EliminatoriaEnum s : EliminatoriaEnum.values()) {
        	selectRonda.addItem(s);
        }
        if(eliminatoria!=null){
        	selectRonda.setValue(eliminatoria);
        	selectRonda.setEnabled(false);
        }
        else
        	selectRonda.setValue(EliminatoriaEnum.CUARTOS);
		*/
		
		Button prevButton = new Button(Messages.get().getKey("prev"));
		prevButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		prevButton.setIcon(FontAwesome.ARROW_LEFT);
		prevButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	// Previous screen
            	parent.gotoJamFinal();
            }
        });
		prevButton.setEnabled(false);
		
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
        
        Label competicionLabel = new Label(competicionStr + " / " + categoriaStr);
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
    	form.editRecord(rec);
    }

    public void showRecords(List<JamShowEntity> records) {
        grid.setRecords(records); 
        if(records!=null && records.size()>0)
    		this.selectRow(records.get(0));
    }

    public void refreshRecord(JamShowEntity rec) {
        grid.refresh(rec);
        grid.scrollTo(rec);
    }

    public void removeRecord(JamShowEntity rec) {
        // Not allowed here grid.remove(rec);
    }
    
}
