package freewill.nextgen.competicion.classic;

import java.io.File;
import java.util.List;

import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.data.ClassicShowEntity;
import freewill.nextgen.genericCrud.GenericGrid;
import freewill.nextgen.hmi.common.ConfirmDialog;
import freewill.nextgen.hmi.utils.Export2Xls;
import freewill.nextgen.hmi.utils.Messages;

@SuppressWarnings("serial")
public class ClassicResultados extends CssLayout {
	
	public final String VIEW_NAME = Messages.get().getKey("classicacta");
	private Long competicion = null;
	private String competicionStr = "";
	private Long categoria = null;
	private String categoriaStr = "";
	private GenericGrid<ClassicShowEntity> grid;
	private ClassicCrudLogic viewLogic;
	private ClassicCrudView parent = null;

	public ClassicResultados(Long categoria, String labelcategoria, Long competicion, 
			String label, ClassicCrudView parent){
		this.competicion = competicion;
		this.competicionStr = label;
		this.categoria = categoria;
		this.categoriaStr = labelcategoria;
		this.parent = parent;
		viewLogic = new ClassicCrudLogic();
		
		setSizeFull();
        addStyleName("crud-view");
        HorizontalLayout topLayout = createTopBar();
		
		grid = new GenericGrid<ClassicShowEntity>(ClassicShowEntity.class,
		       "id", "dorsal", "clasificacionFinal", "nombre", "apellidos",
		       "rankingJuez1", "rankingJuez2", "rankingJuez3",
		       "sumaPV", "PVLocales", "totalTecnica", "PVTotal", "puntuacionTotal"
		       //"rankingJuez1","rankingJuez2", "rankingJuez3", "orden1"
		       );
		grid.getColumn("dorsal").setWidth(80);
		grid.getColumn("clasificacionFinal").setWidth(120);
		VerticalLayout barAndGridLayout = new VerticalLayout();
	    barAndGridLayout.addComponent(topLayout);
	    barAndGridLayout.addComponent(grid);
	    barAndGridLayout.setMargin(true);
	    barAndGridLayout.setSpacing(true);
	    barAndGridLayout.setSizeFull();
	    barAndGridLayout.setExpandRatio(grid, 1);
	    barAndGridLayout.setStyleName("crud-main-layout");
	        
	    addComponent(barAndGridLayout);
		
	    showRecords(viewLogic.initGridResults(this.competicion, this.categoria));  	
	}
	
	@SuppressWarnings({ "deprecation", "unchecked" })
	public HorizontalLayout createTopBar() {
		
		Button prevButton = new Button(/*Messages.get().getKey("prev")*/);
		prevButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
		prevButton.setIcon(FontAwesome.ARROW_LEFT);
		prevButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                // Previous screen
            	parent.gotoClassicFinal();
            }
        });
		
		Button nextButton = new Button(/*Messages.get().getKey("next")*/);
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
		
		Button printButton = new Button(Messages.get().getKey("acta"));
		//printButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
		printButton.setIcon(FontAwesome.DOWNLOAD);
		printButton.addClickListener(e -> {
    		File file = Export2Xls.get().createXLS(
    				(List<ClassicShowEntity>)grid.getContainerDataSource().getItemIds(),
    				ClassicShowEntity.class,
    				("Resultados "+competicionStr+" / "+categoriaStr).toUpperCase(),
    				"clasificacionFinal", "dorsal", "nombre", "apellidos", 
    				"rankingJuez1", "rankingJuez2", "rankingJuez3",
    			    "sumaPV", "PVLocales", "totalTecnica", "PVTotal", "puntuacionTotal");
    		if(file!=null){
    			FileResource resource = new FileResource(file);
    			Page.getCurrent().open(resource, "Export File", false);
    		    // Finally, removes the temporal file
    		    // file.delete();
    		}
        });
        
        Label competicionLabel = new Label(competicionStr+" / "+categoriaStr);
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
        topLayout.addComponent(nextButton);
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

    public void showRecords(List<ClassicShowEntity> records) {
        grid.setRecords(records);
    }
    
}
