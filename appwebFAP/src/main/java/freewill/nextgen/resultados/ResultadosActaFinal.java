package freewill.nextgen.resultados;

import java.io.File;
import java.util.List;

import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.label.ContentMode;
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

import freewill.nextgen.appwebFAP.EntryPoint;
import freewill.nextgen.common.entities.UserEntity.UserRoleEnum;
import freewill.nextgen.data.CategoriaEntity;
import freewill.nextgen.data.CompeticionEntity;
import freewill.nextgen.data.ParticipanteEntity;
import freewill.nextgen.genericCrud.GenericGrid;
import freewill.nextgen.hmi.utils.Export2Xls;
import freewill.nextgen.hmi.utils.Messages;

@SuppressWarnings("serial")
public class ResultadosActaFinal extends VerticalLayout {
	
	public final String VIEW_NAME = Messages.get().getKey("resultadosacta");
	private Long competicion = null;
	private String competicionStr = "";
	private Long categoria = null;
	private String categoriaStr = "";
	private GenericGrid<ParticipanteEntity> grid;
	private ResultadosCrudLogic viewLogic;
	private ResultadosCrudView parent = null;
	private CategoriaEntity category = null;

	public ResultadosActaFinal(Long categoria, String labelcategoria, ResultadosCrudView parent){
		this.categoria = categoria;
		this.categoriaStr = labelcategoria;
		this.parent = parent;
		
		viewLogic = new ResultadosCrudLogic(this);
		
		category = viewLogic.findRecord(""+categoria);
		
		switch(category.getModalidad()){
		case SLIDE:
		case BATTLE:
		case JAM:
		case CLASSIC:
			grid = new GenericGrid<ParticipanteEntity>(ParticipanteEntity.class,
					"id", "dorsal", "clasificacion", "nombre", "apellidos", "clubStr", "puntuacion");
			break;
		case JUMP:
		case SPEED:
			grid = new GenericGrid<ParticipanteEntity>(ParticipanteEntity.class,
					"id", "dorsal", "clasificacion", "nombre", "apellidos", "clubStr", "puntuacion", "mejorMarca");
			break;
		}
        
        HorizontalLayout gridLayout = new HorizontalLayout();
        gridLayout.setSizeFull();
        gridLayout.setMargin(true);
        gridLayout.setSpacing(true);
        gridLayout.addComponent(grid);
        
        CompeticionEntity competi = viewLogic.findLastCompeticion();
        if(competi!=null){
		    competicion = competi.getId();
		    competicionStr = competi.getNombre();
		    viewLogic.initGrid(this.competicion, this.categoria);
        }
        
		HorizontalLayout topLayout = createTopBar();
	    //addComponent(new GenericHeader(VIEW_NAME, FontAwesome.FOLDER));
	    addComponent(topLayout);
	    addComponent(gridLayout);
	    setSizeFull();
	    setExpandRatio(gridLayout, 1);
	    setStyleName("crud-main-layout");
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
    				(List<ParticipanteEntity>)grid.getContainerDataSource().getItemIds(),
    				ParticipanteEntity.class,
    				("Resultados "+competicionStr+" / "+categoriaStr).toUpperCase(),
    				/*"dorsal",*/ "orden", "nombre", "apellidos", "clubStr", "puntuacion");
    		if(file!=null){
    			FileResource resource = new FileResource(file);
    			Page.getCurrent().open(resource, "Export File", false);
    		    // Finally, removes the temporal file
    		    // file.delete();
    		}
        });
        
        Label competicionLabel = new Label("<h3><b>Resultados "+competicionStr+" / "+categoriaStr+"</b></h3>", 
        		ContentMode.HTML);
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
        if(EntryPoint.get().getAccessControl().isUserInRole(UserRoleEnum.USER))
        	topLayout.addComponent(printButton);
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

    public void showRecords(List<ParticipanteEntity> records) {
        grid.setRecords(records);
    }
    
}
