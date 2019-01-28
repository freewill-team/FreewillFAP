package freewill.nextgen.requirement;

import java.util.Collection;
import java.util.List;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.data.ProjectEntity;
import freewill.nextgen.data.RequirementEntity;
import freewill.nextgen.genericCrud.GenericGrid;
import freewill.nextgen.hmi.common.GenericCombo;
import freewill.nextgen.proposalStudio.EntryPoint;

/**
 * Simple name editor Window.
 */
@SuppressWarnings("serial")
public class RequirementUpload extends Window {

    private GenericGrid<RequirementEntity> grid = new GenericGrid<RequirementEntity>(
    		RequirementEntity.class, 
    		"ID", "customid", "description", "category");
    RequirementCrudLogic viewLogic = null;
    List<RequirementEntity> reqList = null;
    Object project = null;

    public RequirementUpload(List<RequirementEntity> reqs, Object prj, RequirementCrudLogic parentLogic) {
    	viewLogic = parentLogic;
    	reqList = reqs;
    	project = prj;
        setCaption("Upload Requirements");
        setModal(true);
        setClosable(false);
        setResizable(false);
        setWidth(800.0f, Unit.PIXELS);

        addStyleName("edit-dashboard");

        setContent(buildContent());
    }

    private Component buildContent() {
        VerticalLayout result = new VerticalLayout();
        result.setMargin(true);
        result.setSpacing(true);
        grid.setRecords(reqList);
        grid.setSelectionMode(SelectionMode.MULTI);
        result.addComponent(grid);
        result.addComponent(buildFooter());
        return result;
    }

    private Component buildFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setSpacing(true);
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth(100.0f, Unit.PERCENTAGE);
        
        Label label = new Label("Select Project");
        GenericCombo<ProjectEntity> projectcb = new GenericCombo<ProjectEntity>(ProjectEntity.class);
        if(project!=null)
        	projectcb.setValue(project);
        projectcb.setEnabled(false);
        
        Button cancel = new Button("Cancel");
        cancel.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                close();
            }
        });
        cancel.setClickShortcut(KeyCode.ESCAPE, null);

        Button save = new Button("Save");
        save.addStyleName(ValoTheme.BUTTON_PRIMARY);
        save.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                // saves all requirements
            	Collection<Object> reqs = grid.getSelectedRows();
            	//for(RequirementEntity rec : reqList){
            	for(Object obj : reqs){
            		RequirementEntity rec = (RequirementEntity)obj;
            		rec.setCompany(EntryPoint.get().getAccessControl().getUserEntity().getCompany());
            		viewLogic.saveRecord(rec, false);
            	}
                close();
            }
        });
        save.setClickShortcut(KeyCode.ENTER, null);
        //save.setEnabled(false);
        
        /*projectcb.addValueChangeListener(new ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
            	Long np = (Long) projectcb.getValue();
            	save.setEnabled(np>0);
            }
        });*/

        footer.addComponents(label, projectcb, cancel, save);
        footer.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
        footer.setExpandRatio(label, 1);
        return footer;
    }

}
