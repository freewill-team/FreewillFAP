package freewill.nextgen.resultados;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

import freewill.nextgen.categoria.SelectCompeticionAndCategoria;
import freewill.nextgen.hmi.utils.Messages;

/**
 * A view for performing create-read-update-delete operations on records.
 *
 * See also {@link StatusCrudLogic} for fetching the data, the actual CRUD
 * operations and controlling the view based on events from outside.
 */

@SuppressWarnings("serial")
public class ResultadosCrudView extends VerticalLayout implements View {

	@SuppressWarnings("unused")
	private static String ICC_PERMISSION = "ICC_ALARMS_PERMISSION";
    public final String VIEW_NAME = Messages.get().getKey("resultados");
    private Long competicion = null;
    private String competicionStr = "";
    private Long categoria = null;
    private String categoriaStr = "";
    private SelectCompeticionAndCategoria selectCompeticionAndCategoria = null;
    private AnonymousLogin parentPanel = null;
    
    public ResultadosCrudView() {
        setSizeFull();
        addStyleName("crud-view");
        setSpacing(false);
        setMargin(false);
    }

    @Override
    public void enter(ViewChangeEvent event) {
    	
    	selectCompeticionAndCategoria = new SelectCompeticionAndCategoria(
    			new ClickListener() {
    	            @Override
    	            public void buttonClick(final ClickEvent event) {
    	            	competicionStr = selectCompeticionAndCategoria.getCompeticionStr();
    	                competicion = selectCompeticionAndCategoria.getCompeticion();
    	                categoriaStr = event.getButton().getDescription();
    	                categoria = Long.parseLong(event.getButton().getId());
    	                gotoResultados();
    	            }
    	        });
        removeAllComponents();
        addComponent(selectCompeticionAndCategoria);
    }

	public void showError(String msg) {
        Notification.show(msg, Type.ERROR_MESSAGE);
    }

    public void showSaveNotification(String msg) {
        Notification.show(msg, Type.TRAY_NOTIFICATION);
    }

	public void gotoResultados() {
		removeAllComponents();
		ResultadosActaFinal resultados = new ResultadosActaFinal(
				categoria, categoriaStr,
				competicion, competicionStr, 
    			ResultadosCrudView.this);
    	addComponent(resultados);
		if(parentPanel!=null){
			parentPanel.setCompeticion(competicionStr+" / "+categoriaStr);
			resultados.setLabelVisible(false);
		}
	}
	
	public void setInfoPanel(AnonymousLogin parentPanel){
    	this.parentPanel = parentPanel;
    }
	
}
