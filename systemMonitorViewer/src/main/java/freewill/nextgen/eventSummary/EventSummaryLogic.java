package freewill.nextgen.eventSummary;

import java.io.Serializable;
import java.util.Date;

import org.apache.log4j.Logger;

import com.vaadin.server.Page;

import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.common.entities.EventEntity;
import freewill.nextgen.managementConsole.EntryPoint;

/**
 * This class provides an interface for the logical operations between the CRUD
 * view, its parts like the record editor form and the data source, including
 * fetching and saving records.
 *
 * Having this separate from the view makes it easier to test various parts of
 * the system separately, and to e.g. provide alternative views for the same
 * data.
 */
@SuppressWarnings("serial")
public class EventSummaryLogic implements Serializable {

    private EventSummary view;
    private Logger log = null;

    public EventSummaryLogic(EventSummary simpleCrudView) {
        view = simpleCrudView;
        log = Logger.getLogger(view.getClass());
    }
    
    public void init(Date sdate, Date edate) {
    	try{
	    	System.out.println("Filtering Events between "+sdate+" and "+edate);
	    	view.showRecords(BltClient.get().executeQuery(
	    			"eventsByDate/"+sdate.getTime()+"/"+edate.getTime(), EventEntity.class,
	    			EntryPoint.get().getAccessControl().getTokenKey()));
    	}
		catch(Exception e){
			log.error(e.getMessage());
		}
    }

    /**
     * Update the fragment without causing navigator to change view
     */
    @SuppressWarnings("unused")
    private void setFragmentParameter(String recId) {
        String fragmentParameter;
        if (recId == null || recId.isEmpty()) {
            fragmentParameter = "";
        } else {
            fragmentParameter = recId;
        }

        Page page = EntryPoint.get().getPage();
        page.setUriFragment("!" + view.VIEW_NAME + "/" + fragmentParameter, false);
    }

    public void enter(String recId) {
        //
    }

    public void rowSelected(EventEntity rec) {
        //view.editRecord(rec);
    }

	/*public void deleteEvents() {
		int ne = view.getGrid().getContainerDataSource().getItemIds().size();
		for(Object item : view.getGrid().getContainerDataSource().getItemIds()){
		    // Now get the actual item from the table.
		    EventEntity rec = (EventEntity) item;
		    // Removes record
		    RtdbDataService.get().deleteEvent(rec);
		}
		setFragmentParameter("");
		view.showSaveNotification(ne+" Events have been removed");
		RtdbDataService.get().createEvent(new EventEntity(
				new Date(), 
				ne+" Events have been removed", // no lo meto en Diccionario ya que esta accion no debera estar permitida realmente
				"no-point",
				"no-parent",
				"no-pointtype",
				SeverityEnum.NONE,
				CategoryEnum.USER,
				EntryPoint.get().getAccessControl().getUserLogin(),
				VaadinService.getCurrentRequest().getRemoteHost()
				));
		view.enter(null);
	}*/

}
