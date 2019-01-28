package freewill.nextgen.categoria;

import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.Responsive;

import freewill.nextgen.circuito.SelectCircuito;
import freewill.nextgen.categoria.SelectModalidad;
import freewill.nextgen.data.CategoriaEntity.ModalidadEnum;

@SuppressWarnings("serial")
public class SelectCircuitoAndCategoria extends Panel {

	private VerticalLayout content = null;
    private int modalidad = 0;
    private ModalidadEnum modalidadenum = null;
    private SelectCircuito selectcircuito = null;
    private VerticalLayout selectionarea = new VerticalLayout();
    private SelectModalidad selectmodalidad = null;
    private SelectCategoriaByRanking selectcategoria = null;
    private ClickListener action;
    
    public SelectCircuitoAndCategoria(ClickListener action) {
    	this.addStyleName(ValoTheme.PANEL_BORDERLESS);
    	setSizeFull();
    	this.action = action;
    	
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
	            	selectmodalidad = createselectmodalidad();
	                selectionarea.addComponent(selectmodalidad);
	            }
    		});
    	
    	selectmodalidad = createselectmodalidad();
        
    	content.removeAllComponents();
    	content.addComponent(selectcircuito);
    	content.addComponent(selectionarea);
        selectionarea.removeAllComponents();
        selectionarea.setMargin(false);
        selectionarea.setSpacing(false);
        selectionarea.addComponent(selectmodalidad);
        content.setExpandRatio(selectionarea, 1);
    }
	
	private SelectModalidad createselectmodalidad(){
		return new SelectModalidad(
        		new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
            	//modalidadStr = event.getButton().getDescription();
            	modalidad = Integer.parseInt(event.getButton().getId());
            	modalidadenum = ModalidadEnum.values()[modalidad];
            	if(selectcategoria!=null)
            		selectionarea.removeComponent(selectcategoria);
            	selectcategoria = new SelectCategoriaByRanking(getCircuito(), modalidadenum, action);
            	selectionarea.addComponent(selectcategoria);
            }
        });
	}

	public Long getCircuito() {
		return selectcircuito.getValue();
	}
	
	public String getCircuitoStr() {
		return selectcircuito.getCaption();
	}

	public void showCircuito(boolean visible){
		selectcircuito.setVisible(visible);
	}
	
}
