package freewill.nextgen.rankingabsoluto;

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
import freewill.nextgen.data.CategoriaEntity.ModalidadEnum;
import freewill.nextgen.data.RankingAbsEntity;
import freewill.nextgen.genericCrud.GenericGrid;
import freewill.nextgen.hmi.utils.Export2Xls;
import freewill.nextgen.hmi.utils.Messages;

@SuppressWarnings("serial")
public class RankingAbsActaFinal extends CssLayout {
	
	public final String VIEW_NAME = Messages.get().getKey("rankingabsacta");
	private ModalidadEnum modalidad;
	private GenericGrid<RankingAbsEntity> grid;
	private RankingAbsCrudLogic viewLogic;
	private RankingAbsCrudView parent = null;
	private RankingAbsForm form;

	public RankingAbsActaFinal(ModalidadEnum modalidad, RankingAbsCrudView parent){
		this.modalidad = modalidad;
		this.parent = parent;
		setSizeFull();
        addStyleName("crud-view");
        HorizontalLayout topLayout = createTopBar();
		
		viewLogic = new RankingAbsCrudLogic(this);
		
		grid = new GenericGrid<RankingAbsEntity>(RankingAbsEntity.class,
				"id", "orden", "nombre", "apellidos", "clubStr", "puntuacion",
				"categoriaStr",
				"puntos1", "competicion1", "puntos2", "competicion2", 
			    "puntos3", "competicion3", "puntos4", "competicion4"
				);
		grid.addSelectionListener(new SelectionListener() {
	        @Override
	        public void select(SelectionEvent event) {
	            viewLogic.rowSelected(grid.getSelectedRow());
	        }
	    });
		
		form = new RankingAbsForm(viewLogic);
		
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
	    
	    viewLogic.initGrid(this.modalidad);
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
    				(List<RankingAbsEntity>)grid.getContainerDataSource().getItemIds(),
    				RankingAbsEntity.class,
    				(VIEW_NAME+" "+modalidad).toUpperCase(),
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
            		RankingAbsEntity rec = new RankingAbsEntity();
            		rec.setModalidad(modalidad);
    	        	editRecord(rec);
            	}
            	catch(Exception e){
            		showError(e.getMessage());
            	};
            }
        });
        
        Label competicionLabel = new Label(VIEW_NAME+" "+modalidad);
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

    public void showRecords(List<RankingAbsEntity> records) {
        grid.setRecords(records);
    }
    
    public void refreshRecord(RankingAbsEntity rec) {
        grid.refresh(rec);
        grid.scrollTo(rec);
    }

    public void removeRecord(RankingAbsEntity rec) {
        grid.remove(rec);
    }
    
    public RankingAbsEntity getSelectedRow() {
        return grid.getSelectedRow();
    }

	public void editRecord(RankingAbsEntity rec) {
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

    public void selectRow(RankingAbsEntity row) {
        ((SelectionModel.Single) grid.getSelectionModel()).select(row);
    }
    
}
