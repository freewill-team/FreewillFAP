package freewill.nextgen.processMonitor;

import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.ui.Tree;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.common.Utils.ServiceStatusEnum;
import freewill.nextgen.common.entities.ServerEntity;
import freewill.nextgen.common.entities.ServiceEntity;

@SuppressWarnings("serial")
public class ProcessesTree extends Tree {
	
	private boolean firsttime = true;
	
	public ProcessesTree(){
		setSizeFull();
		setSelectable(true);
		setMultiSelect(false);
		setStyleName(ValoTheme.TREETABLE_COMPACT);
        setCaptionAsHtml(true);   
        setCaption("<b>Architecture Tree</b>");
	}

	public void setRecords(List<ServerEntity> servers, List<ServiceEntity> services) {
		HierarchicalContainer container = createTreeContent(servers, services);
        setContainerDataSource(container);
        // Expand all items
        if( firsttime==true ){
        	for (Object itemId:getItemIds())
        		expandItem(itemId);
        	firsttime = false;
        }
        this.setItemIconPropertyId("icon");
		this.setItemCaptionPropertyId("caption");
		this.setItemCaptionMode(ItemCaptionMode.PROPERTY);
	}

	@SuppressWarnings("unchecked")
	private HierarchicalContainer createTreeContent(List<ServerEntity> servers, List<ServiceEntity> services) {
		
		Resource ICON_ROOT = FontAwesome.GLOBE;
		Resource ICON_GOOD = FontAwesome.CHECK_CIRCLE;
		Resource ICON_FAIL = FontAwesome.EXCLAMATION_TRIANGLE;
		Resource ICON_STOP = FontAwesome.STOP_CIRCLE;
		
    	HierarchicalContainer container = new HierarchicalContainer();
    	container.addContainerProperty("icon", Resource.class, null);
    	container.addContainerProperty("caption", String.class, "Root");
    	container.addContainerProperty("node", Object.class, null);
    	
    	// Root Node
    	Item root = container.addItem("Root");
    	container.setChildrenAllowed("Root", true);
    	root.getItemProperty("icon").setValue(ICON_ROOT);
    	root.getItemProperty("caption").setValue("Root");
    	root.getItemProperty("node").setValue("Root");
    	
    	// First iteration populates Servers
    	for (ServerEntity obj : servers) {
    		String objId = obj.getName();	
	    	Object item = container.addItem();
	    	container.getContainerProperty(item,"caption").setValue(objId);
	    	container.setChildrenAllowed(item, true);
	    	container.setParent(item, "Root");
	    	if(obj.getStatus()==ServiceStatusEnum.GOOD)
	    		container.getContainerProperty(item,"icon").setValue(ICON_GOOD);
	    	else if(obj.getStatus()==ServiceStatusEnum.STOP)
	    		container.getContainerProperty(item,"icon").setValue(ICON_STOP);
	    	else
	    		container.getContainerProperty(item,"icon").setValue(ICON_FAIL);
	    	container.getContainerProperty(item,"node").setValue(obj);
	    	
		    // Second iteration populates Services
		    for (ServiceEntity obj2 : services) {
		    	if(!obj2.getServer().equals(objId)) continue;
		    	
		    	String objId2 = obj2.getName();
			    Object item2 = container.addItem();
			    container.getContainerProperty(item2,"caption").setValue(objId2);
			    container.setChildrenAllowed(item2, false);
			    container.setParent(item2, item);
			    if(obj2.getStatus()==ServiceStatusEnum.GOOD)
			    	container.getContainerProperty(item2,"icon").setValue(ICON_GOOD);
			    else if(obj2.getStatus()==ServiceStatusEnum.STOP)
		    		container.getContainerProperty(item2,"icon").setValue(ICON_STOP);
		    	else
		    		container.getContainerProperty(item2,"icon").setValue(ICON_FAIL);
			    container.getContainerProperty(item2,"node").setValue(obj2);
	    	}
    	}
    	
    	return container;		
	}
	
}
