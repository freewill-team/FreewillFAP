package freewill.nextgen.genericCrud;

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

import freewill.nextgen.hmi.utils.Messages;

/**
 * Simple name editor Window.
 */
@SuppressWarnings("serial")
public class ImportGrid<T> extends Window {

	private GenericGrid<T> grid;
    private GenericCrudLogic<T> viewLogic = null;
    List<T> list = null;
    Class<T> myentity = null;

    public ImportGrid(Class<T> entity, List<T> list, GenericCrudLogic<T> parentLogic,
    		 String idfield, String... fields) {
    	viewLogic = parentLogic;
    	myentity = entity;
    	this.list = list;
        setCaption(Messages.get().getKey("uploadrecords"));
        setModal(true);
        setClosable(false);
        setResizable(false);
        setWidth(800.0f, Unit.PIXELS);

        addStyleName("edit-dashboard");

        setContent(buildContent(entity, idfield, fields));
    }

    private Component buildContent(Class<T> entity, String idfield, String... fields) {
        VerticalLayout result = new VerticalLayout();
        result.setMargin(true);
        result.setSpacing(true);
        
        grid = new GenericGrid<T>(entity, idfield, fields);
        grid.setRecords(list);
        /*for(T obj:list){
        	System.out.println("Rec="+obj.toString());
        }*/
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
        
        Button check = new Button(Messages.get().getKey("check"));
        check.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
            	// checks whether the data can be uploaded
            	if(viewLogic!=null)
            		viewLogic.checkRecords(list);
                close();
            }
        });
        check.setClickShortcut(KeyCode.ENTER, null);
        
        Button cancel = new Button(Messages.get().getKey("cancel"));
        cancel.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                close();
            }
        });
        cancel.setClickShortcut(KeyCode.ESCAPE, null);

        Button save = new Button(Messages.get().getKey("save"));
        save.addStyleName(ValoTheme.BUTTON_PRIMARY);
        save.addClickListener(new ClickListener() {
            @SuppressWarnings("unchecked")
			@Override
            public void buttonClick(final ClickEvent event) {
                // saves all records
            	/*List<T> recs = new ArrayList<T>();
            	Collection<Object> reqs = grid.getSelectedRows();
            	for(Object obj : reqs){
            		T rec = (T)obj;
            		recs.add(rec);
            	}
            	viewLogic.saveRecords(recs);*/
            	if(viewLogic!=null)
            		viewLogic.saveRecords((List<T>) grid.getSelectedRows());
                close();
            }
        });

        Label expander = new Label();
        
        footer.addComponents(/*check,*/ expander, cancel, save);
        footer.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
        footer.setExpandRatio(expander, 1);
        return footer;
    }
    
}
