package freewill.nextgen.ranking;

import java.io.File;
import java.util.List;

import com.vaadin.event.SelectionEvent;
import com.vaadin.event.SelectionEvent.SelectionListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Grid.SelectionModel;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.CategoriaEntity;
import freewill.nextgen.data.RankingEntity;
import freewill.nextgen.genericCrud.GenericGrid;
import freewill.nextgen.hmi.utils.Export2Xls;
import freewill.nextgen.hmi.utils.Messages;

@SuppressWarnings("serial")
public class RankingActaFinal extends CssLayout {
	
	public final String VIEW_NAME = Messages.get().getKey("rankingacta");
	private Long circuito = null;
	private String circuitoStr = "";
	private Long categoria = null;
	private String categoriaStr = "";
	private GenericGrid<RankingEntity> grid;
	private RankingCrudLogic viewLogic;
	private RankingCrudView parent = null;
	private RankingForm form;
	private CategoriaEntity category = null;

	public RankingActaFinal(Long categoria, String labelcategoria, Long circuito, 
			String circuitolabel, RankingCrudView parent){
		this.circuito = circuito;
		this.circuitoStr = circuitolabel;
		this.categoria = categoria;
		this.categoriaStr = labelcategoria;
		this.parent = parent;
		setSizeFull();
        addStyleName("crud-view");
        HorizontalLayout topLayout = createTopBar();
		
		viewLogic = new RankingCrudLogic(this);
		
		category = viewLogic.findCategoria(""+categoria);
		
		switch(category.getModalidad()){
		case SLIDE:
		case BATTLE:	
		case CLASSIC:
		case JUMP:
		case SPEED:
			grid = new GenericGrid<RankingEntity>(RankingEntity.class,
				       "id", "orden", "nombre", "apellidos", "clubStr", "puntuacion",
				       //"categoriaStr",
				       "puntos1", "competicion1", "puntos2", "competicion2", 
			       		"puntos3", "competicion3", "puntos4", "competicion4"
				       );
			break;
		case JAM:
			grid = new GenericGrid<RankingEntity>(RankingEntity.class,
				       "id", "orden", "nombre", "apellidos", "nombrePareja", "apellidosPareja", 
				       "clubStr", "puntuacion", 
				       //"categoriaStr",
				       "puntos1", "competicion1", "puntos2", "competicion2", 
			       		"puntos3", "competicion3", "puntos4", "competicion4"
			       		);
			break;
		}
		
		grid.addSelectionListener(new SelectionListener() {
	        @Override
	        public void select(SelectionEvent event) {
	            viewLogic.rowSelected(grid.getSelectedRow());
	        }
	    });
		
		form = new RankingForm(viewLogic);
		
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
	    
	    viewLogic.initGrid(this.circuito, this.categoria);	
	}
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	public HorizontalLayout createTopBar() {
		
		Button prevButton = new Button(Messages.get().getKey("prev"));
		prevButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		prevButton.setIcon(FontAwesome.ARROW_LEFT);
		prevButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                // Previous screen
            	parent.enter(null);
            }
        });
		
		Button printButton = new Button(Messages.get().getKey("acta"));
		//printButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		printButton.setIcon(FontAwesome.DOWNLOAD);
		printButton.addClickListener(e -> {
    		File file = Export2Xls.get().createXLS(
    				(List<RankingEntity>)grid.getContainerDataSource().getItemIds(),
    				RankingEntity.class,
    				("Ranking "+circuitoStr+" / "+categoriaStr).toUpperCase(),
    				"orden", "nombre", "apellidos", "clubStr", "puntuacion",
				    "categoriaStr",
				    "puntos1", "competicion1", "puntos2", "competicion2", 
			        "puntos3", "competicion3", "puntos4", "competicion4"
    				);
    		if(file!=null){
    			FileResource resource = new FileResource(file);
    			Page.getCurrent().open(resource, "Export File", false);
    		    // Finally, removes the temporal file
    		    // file.delete();
    		}
        });
		
		Button newRecord = new Button(Messages.get().getKey("new"));
        newRecord.addStyleName(ValoTheme.BUTTON_PRIMARY);
        newRecord.setIcon(FontAwesome.PLUS_CIRCLE);
        newRecord.addClickListener(new ClickListener() {
			@Override
            public void buttonClick(ClickEvent event) {
            	try{
            		RankingEntity rec = new RankingEntity();
            		rec.setCategoria(categoria);
    	        	rec.setCircuito(circuito);
    	        	editRecord(rec);
            	}
            	catch(Exception e){
            		showError(e.getMessage());
            	};
            }
        });
        
        Label competicionLabel = new Label("Ranking "+circuitoStr+" / "+categoriaStr);
        competicionLabel.setStyleName(ValoTheme.LABEL_LARGE);
        competicionLabel.addStyleName(ValoTheme.LABEL_COLORED);
        competicionLabel.addStyleName(ValoTheme.LABEL_BOLD);

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
        topLayout.setSpacing(true);
        //topLayout.setMargin(true);
        topLayout.setWidth("100%");
        topLayout.addComponent(competicionLabel);
        topLayout.addComponent(prevButton);
        if(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.USER))
        	topLayout.addComponent(printButton);
        if(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.ADMIN))
        	topLayout.addComponent(newRecord);
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

    public void showRecords(List<RankingEntity> records) {
        grid.setRecords(records);
    }
    
    public void refreshRecord(RankingEntity rec) {
        grid.refresh(rec);
        grid.scrollTo(rec);
    }

    public void removeRecord(RankingEntity rec) {
        grid.remove(rec);
    }
    
    public RankingEntity getSelectedRow() {
        return grid.getSelectedRow();
    }

	public void editRecord(RankingEntity rec) {
        if (rec != null) {
            form.addStyleName("visible");
            form.setEnabled(true);
        } else {
            form.removeStyleName("visible");
            form.setEnabled(false);
        }
        form.editRecord(rec);
    }
    
    public void clearSelection() {
        grid.getSelectionModel().reset();
    }

    public void selectRow(RankingEntity row) {
        ((SelectionModel.Single) grid.getSelectionModel()).select(row);
    }
    
}
