package freewill.nextgen.jshMonitor;

import java.io.File;
import java.io.FileWriter;
import com.vaadin.server.FileResource;
import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.server.Page;

import freewill.nextgen.common.bltclient.BltClient;
import freewill.nextgen.common.entities.JobScheduled;
import freewill.nextgen.hmi.utils.ApoiXlsExport;
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
public class JshCrudLogic implements Serializable {

    private JshMonitor view;
    private Logger log = null;

    public JshCrudLogic(JshMonitor simpleCrudView) {
        view = simpleCrudView;
        log = Logger.getLogger(view.getClass());
    }
    
    public void refreshGrid() {
	    try{
	    	editRecord(null);
	    	if(view!=null){
	        	view.setNewRecordEnabled(true);
	        	view.showRecords(BltClient.get().getEntities(JobScheduled.class,
	        			EntryPoint.get().getAccessControl().getTokenKey()));
	    	}
	    }
		catch(Exception e){
			log.error(e.getMessage());
		}
    }
    
    public void cancelRecord() {
		setFragmentParameter("");
        if(view!=null){
	        view.clearSelection();
	        view.editRecord(null);
        }
	}

    /**
     * Update the fragment without causing navigator to change view
     */
    private void setFragmentParameter(String recId) {
        String fragmentParameter;
        if (recId == null || recId.isEmpty()) {
            fragmentParameter = "";
        } else {
            fragmentParameter = recId;
        }

        Page page = EntryPoint.getCurrent().getPage();
        if(view!=null)
        	page.setUriFragment("!" + view.VIEW_NAME + "/" + fragmentParameter, false);
    }
    
    @SuppressWarnings("deprecation")
	public void printAllPage(List<JobScheduled> collection) {
    	// Exportar a Excel todos las registros visibles
    	try{
    		// Create first a temporal file
    		File tempFile = File.createTempFile("jshexport", ".xlsx");
    		// Creates the Word Document
    		ApoiXlsExport doc = new ApoiXlsExport(tempFile, null);
    		doc.setHeader("Jsh Export");
    		// Set the headings
    		int j = 0;
    		Object data[] = new Object[6];
    		data[0] = "id";
    		data[1] = "label";
    		data[2] = "description";
    		data[3] = "active";
    		data[4] = "cron";
    		data[5] = "state";
    		doc.addRow(j, data, true);
    		j++;
    		// Process the List     	
    		for(JobScheduled rec : collection){
    		    data[0] = rec.getId();
    			data[1] = rec.getLabel();
    			data[2] = rec.getDescription();
    		    data[3] = rec.getActive();
    		   	data[4] = rec.getCron();
    		   	data[5] = rec.getState();
    		   	doc.addRow(j, data, false);
    		   	j++;
    		}
    		// Close the Word Document
    		int[] widths = {-1,-1,-1,-1,-1,-1}; // Column widths in characters
    		doc.CloseDocument(widths);
    		// Downloads the file
    		FileResource resource = new FileResource(tempFile);
    		Page.getCurrent().open(resource, "Jsh Export File", false);
    		// Finally, removes the temporal file
    		//tempFile.delete();
    	}
		catch(Exception e){
			log.error(e.getMessage());
		}
	}
    
    void writeLine(FileWriter writer, Object data[])
    {
    	String SEPARATOR = ",";
    	int i = 0;
    	try {
	    	for(Object obj : data) {
	    		String txt = obj.toString();
	    		if(txt.contains(SEPARATOR)) 
	    			txt = "\""+txt+"\"";
	    		if(i==0)
					writer.write(txt);
				else
	    			writer.write(SEPARATOR + txt);
	    		i++;
	    	}
	    	writer.write(System.lineSeparator());
    	}
		catch(Exception e){
			log.error(e.getMessage());
		}
    }

	@SuppressWarnings("deprecation")
	public void exportAllPage(List<JobScheduled> collection) {
		// Export a CSV todos las eventos visibles
		try{
			// Create first a temporal file
			File tempFile = File.createTempFile("jshexport", ".csv");
			FileWriter writer = new FileWriter(tempFile.getAbsolutePath());
			// Set the headings
			Object data[] = new Object[6];
			data[0] = "id";
    		data[1] = "label";
    		data[2] = "description";
    		data[3] = "active";
    		data[4] = "cron";
    		data[5] = "state";
			writeLine(writer, data);
			// Process the List     	
			for(JobScheduled rec : collection){
				data[0] = rec.getId();
    			data[1] = rec.getLabel();
    			data[2] = rec.getDescription();
    		    data[3] = rec.getActive();
    		   	data[4] = rec.getCron();
    		   	data[5] = rec.getState();
	        	writeLine(writer, data);
			}
			// Close the Word Document
			writer.close();
			// Downloads the file
			FileResource resource = new FileResource(tempFile);
			Page.getCurrent().open(resource, "Jsh Export File", false);
			// Finally, removes the temporal file
			//tempFile.delete();
		}
		catch(Exception e){
			log.error(e.getMessage());
		}
	}

	public void enter(String recId) {
		if (recId != null && !recId.isEmpty()) {
            if (recId.equals("new")) {
                newRecord();
            } else {
                // Ensure this is selected even if coming directly here from
                // login
                try {
                    int pid = Integer.parseInt(recId);
                    JobScheduled rec = findRecord(pid);
                    if(view!=null)
                    	view.selectRow(rec);
                } catch (NumberFormatException e) {
                }
            }
        }
	}
	
	private JobScheduled findRecord(int recId) {
		try{
			JobScheduled rec = (JobScheduled) BltClient.get().getEntityById(""+recId, 
					JobScheduled.class,
					EntryPoint.get().getAccessControl().getTokenKey());
			return rec;
		}
		catch(Exception e){
			log.error(e.getMessage());
		}
		return null;
    }

    public void saveRecord(JobScheduled rec) {
    	try{
	    	if(view!=null){
	    		JobScheduled res = null;
	    		
	    		if(rec.getId()==null)
	        		res = (JobScheduled) BltClient.get().createEntity(rec, JobScheduled.class,
	        				EntryPoint.get().getAccessControl().getTokenKey());
	        	else
	        		res = (JobScheduled) BltClient.get().updateEntity(rec, JobScheduled.class,
	        				EntryPoint.get().getAccessControl().getTokenKey());
	    		
		        view.showSaveNotification(rec.getLabel() + " (" + res.getId() + ") updated");
		        view.clearSelection();
		        view.editRecord(null);
		        view.refreshRecord(res);
	    	}
	        setFragmentParameter("");
    	}
		catch(Exception e){
			log.error(e.getMessage());
		}
    }

    public void deleteRecord(JobScheduled rec) {
    	try{
	        if(view!=null){
	        	BltClient.get().deleteEntity(""+rec.getId(), JobScheduled.class,
	        			EntryPoint.get().getAccessControl().getTokenKey());
		        view.showSaveNotification(rec.getLabel() + " (" + rec.getId() + ") removed");
		        view.clearSelection();
		        view.editRecord(null);
		        view.removeRecord(rec);
	        }
	        setFragmentParameter("");
    	}
		catch(Exception e){
			log.error(e.getMessage());
		}
    }

    public void editRecord(JobScheduled rec) {
        if (rec == null) {
            setFragmentParameter("");
        } else {
            setFragmentParameter(rec.getId() + "");
        }
        if(view!=null)
        	view.editRecord(rec);
    }

    public void newRecord() {
        view.clearSelection();
        setFragmentParameter("new");
        JobScheduled rec = new JobScheduled();
        view.editRecord(rec);
    }

    public void rowSelected(JobScheduled rec) {
        view.editRecord(rec);
    }
    
}
