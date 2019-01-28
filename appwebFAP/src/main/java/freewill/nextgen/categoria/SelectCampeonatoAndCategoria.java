package freewill.nextgen.categoria;

import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.Responsive;

import freewill.nextgen.categoria.SelectCategoriaByParticipante;
import freewill.nextgen.circuito.SelectCircuito;
import freewill.nextgen.competicion.SelectCompeticion;
import freewill.nextgen.data.CategoriaEntity.ModalidadEnum;

@SuppressWarnings("serial")
public class SelectCampeonatoAndCategoria extends Panel {

	private VerticalLayout content = null;
    private Long competicion = null;
    private String competicionStr = "";
    //private Long categoria = null;
    //private String categoriaStr = "";
    private SelectCircuito selectcircuito = null;
    private VerticalLayout selectionarea = new VerticalLayout();
    private SelectCompeticion selectcampeonato = null;
    private SelectCategoriaByParticipante selectcategoria = null;
    private ClickListener action;
    private ModalidadEnum modalidad = null;
    
    public SelectCampeonatoAndCategoria(ModalidadEnum modalidad, ClickListener action) {
    	this.addStyleName(ValoTheme.PANEL_BORDERLESS);
    	setSizeFull();
    	this.action = action;
    	this.modalidad = modalidad;
    	
    	content = new VerticalLayout();
    	content.setSizeFull();
    	content.addStyleName("dashboard-view");
    	content.setSpacing(false);
    	content.setMargin(false);
    	this.setContent(content);
    	Responsive.makeResponsive(content);
    	
    	createContent();
    }

    private void createContent() {
    	
    	selectcircuito = new SelectCircuito();
    	selectcircuito.addAction(
    		new ValueChangeListener() {
	            public void valueChange(ValueChangeEvent event) {
	            	selectionarea.removeAllComponents();
	            	selectcampeonato = createSelectCampeonato(selectcircuito.getValue());
	                selectionarea.addComponent(selectcampeonato);
	            }
    		});
    	
    	selectcampeonato = createSelectCampeonato(selectcircuito.getValue());
        
    	content.removeAllComponents();
    	content.addComponent(selectcircuito);
    	content.addComponent(selectionarea);
        selectionarea.removeAllComponents();
        selectionarea.setMargin(false);
        selectionarea.setSpacing(false);
        selectionarea.addComponent(selectcampeonato);
        content.setExpandRatio(selectionarea, 1);
    }
	
	private SelectCompeticion createSelectCampeonato(Long circuito){
		return new SelectCompeticion(circuito,
        		new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
            	competicionStr = event.getButton().getDescription();
            	competicion = Long.parseLong(event.getButton().getId());
            	if(selectcategoria!=null)
            		selectionarea.removeComponent(selectcategoria);
            	selectcategoria = new SelectCategoriaByParticipante(competicion, modalidad, action);
            	selectionarea.addComponent(selectcategoria);
            }
        });
	}

	public Long getCompeticion() {
		return competicion;
	}
	
	public String getCompeticionStr() {
		return competicionStr;
	}
	
}
