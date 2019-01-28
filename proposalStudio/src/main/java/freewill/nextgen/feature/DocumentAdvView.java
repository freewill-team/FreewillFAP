package freewill.nextgen.feature;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.event.FieldEvents;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.apoi.ApoiDocImport;
import freewill.nextgen.apoi.ApoiXlsImport;
import freewill.nextgen.data.CrudViewInterface;
import freewill.nextgen.data.FeatureEntity;
import freewill.nextgen.data.ProductEntity;
import freewill.nextgen.data.Style;
import freewill.nextgen.data.Style.StyleEnum;
import freewill.nextgen.hmi.common.GenericCombo;
import freewill.nextgen.hmi.utils.Messages;
import freewill.nextgen.proposalStudio.EntryPoint;

/**
 * A view for performing create-read-update-delete operations on records.
 */
@SuppressWarnings("serial")
public class DocumentAdvView extends CssLayout implements CrudViewInterface<FeatureEntity> /*View*/ {

    public final String VIEW_NAME = Messages.get().getKey("documentcrudview.viewname");
    private GenericCombo<ProductEntity> productcb = null;
    private DocumentPanel grid = null;
    private VerticalLayout barAndGridLayout = null;
    private Button newRecord;
    private File tempFile = null;
    private TextField page = null;
    private ContentTableForm tree = null;
    private boolean debug = false;
    private List<FeatureEntity> docs = new ArrayList<FeatureEntity>();
    private int docidx = 0;
    private TextField Doc = null;
    private Upload upload = null;
    private FeatureCrudLogic viewLogic = new FeatureCrudLogic(this);
    
    public String getName(){
    	return VIEW_NAME;
    }
    
    public DocumentAdvView() {
    	//System.out.println("Entrando en DocumentAdvView, instance = "+new Random().nextInt());
    	setSizeFull();
        addStyleName("crud-view");
        
        grid = new DocumentPanel(viewLogic, 0L, true, debug);
        
        HorizontalLayout topLayout = createTopBar();
        tree = new ContentTableForm(this, viewLogic);
        tree.setEnabled(false);
        
        barAndGridLayout = new VerticalLayout();
        barAndGridLayout.addComponent(topLayout);
        barAndGridLayout.addComponent(grid);
        barAndGridLayout.setMargin(false);
        barAndGridLayout.setSpacing(false);
        barAndGridLayout.setSizeFull();
        barAndGridLayout.setExpandRatio(grid, 1);
        barAndGridLayout.setStyleName("crud-main-layout");

        addComponent(barAndGridLayout);
        addComponent(tree);
    }

	@SuppressWarnings("deprecation")
	public HorizontalLayout createTopBar() {
		
		Button showTree = new Button();
		showTree.setIcon(FontAwesome.NAVICON);
		showTree.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
		showTree.addStyleName(ValoTheme.BUTTON_SMALL);
		showTree.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                if(tree.isEnabled())
                	tree.close();
                else{ 
                	if (productcb.getValue() != null) {
                     	ProductEntity product = (ProductEntity) productcb.getValue();
                     	Long prd = product.getID();
                     	tree.enter(prd);
                	}
                }
            }
        });
		
		TextField search = new TextField();
        search.setStyleName("filter-textfield");
        search.setInputPrompt("Search...");
         
        search.setImmediate(true);
        search.addTextChangeListener(new FieldEvents.TextChangeListener() {
            @Override
            public void textChange(FieldEvents.TextChangeEvent event) {
            	debug = (event.getText().contains("show ids")? true:false);
                System.out.println("Debug = "+debug);
                if(event.getText().equals("")) return;
                // Find first occurence
            	grid.findFeature(event.getText());
                page.setValue(grid.getCurrpage()+"/"+grid.getLastpage());
                // Also show all found occurences
                if (productcb.getValue() != null) {
                	ProductEntity product = (ProductEntity) productcb.getValue();
                	Long prd = product.getID();
	                List<FeatureEntity> recs = (List<FeatureEntity>) 
	                		viewLogic.getByProductFiltered(event.getText(), prd);
	                docs.clear();
	                //System.out.println("Product="+prd);
	                for(FeatureEntity rec:recs){
	                	//System.out.println("Rec Product="+rec.toString());
	                	long recprd = rec.getProduct();
	                	if(recprd==prd){
	                		docs.add(rec);
	                		//System.out.println("Added");
	                	}
	                }
	            }
            	setDocumentInfo(docidx=0);
            }
        });
        
        productcb = new GenericCombo<ProductEntity>(ProductEntity.class);
        
        productcb.addValueChangeListener(new ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                refreshGrid();
            }
        });
        
        newRecord = new Button(Messages.get().getKey("new"));
        newRecord.addStyleName(ValoTheme.BUTTON_PRIMARY);
        newRecord.setIcon(FontAwesome.PLUS_CIRCLE);
        newRecord.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                if (productcb.getValue() != null) {
                	FeatureEntity rec = new FeatureEntity();
                    rec.setTitle("New Root Feature...");
                    rec.setLevel(StyleEnum.H1);
                    rec.setCompany(EntryPoint.get().getAccessControl().getUserEntity().getCompany());
                 	ProductEntity product = (ProductEntity) productcb.getValue();
                 	Long prd = product.getID();
                 	rec.setProduct(prd);
                 	FeatureEntity res = viewLogic.saveRecord(rec);
                    // Add new root feature to the end of the list
                    grid.lastPage();
                    grid.newGridItem(res, null);
                }
            }
        });
        newRecord.setEnabled(false);
        
        // Create and configure the upload component for new features
        upload = new Upload("Load Features", new Upload.Receiver() {
            @Override
            public OutputStream receiveUpload(String filename, String mimeType) {
            	try {
            		System.out.println("File = "+filename + " MimeType = "+mimeType);
            		// Here, we'll stored the uploaded file as a temporary file
	            	if(filename.toUpperCase().endsWith("XLSX")){
	            		tempFile = File.createTempFile("temp", ".xlsx");
	            	}
	            	else if(filename.toUpperCase().endsWith("DOCX")){
	            		tempFile = File.createTempFile("temp", ".docx");
	            	}
	            	else{
	            		Notification.show("Not a valid (XLSX or DOCX) file. Upload interrupted.");
	            		return null;
	            	}
	            	
	            	return new FileOutputStream(tempFile);
            	} 
            	catch (IOException e) {
            		e.printStackTrace();
            		return null;
            	}
            }
        });

        upload.addListener(new Upload.StartedListener(){
			@Override
			public void uploadStarted(StartedEvent event) {
				if (event.getFilename().isEmpty()) {
			        upload.interruptUpload();
			        return;
        		}
				System.out.println("File Upload started... "+event.getContentLength()+" bytes");
				long size = event.getContentLength()/1024/1024; //bytes->kb->Mb
				long maxsize = EntryPoint.get().getConfigLong("uploadmaxxls");
				if(size>maxsize){
					Notification.show("File bigger than "+maxsize+"Mb. Upload interrupted.");
					upload.interruptUpload();
				}
			}
        });
        
        upload.addListener(new Upload.FinishedListener() {
            @Override
            public void uploadFinished(Upload.FinishedEvent finishedEvent) {
            	try {
            		// process the uploaded file and store the new requirements
            		if(tempFile.length()>10 /*bytes*/){
            			showSaveNotification("File uploaded successfully.");
	            		List<FeatureEntity> reqs = null;
	            		List<Style> styles = null;
	            		if(tempFile.getAbsolutePath().endsWith("xlsx")){
		            		ApoiXlsImport xls = new ApoiXlsImport(tempFile);
		            		reqs = xls.getFeatures();
		            		xls.CloseXls();
	            		}
	            		else{
	            			ApoiDocImport doc = new ApoiDocImport(tempFile);
	            			styles = doc.getUsedStyles();
		            		reqs = doc.getFeatures(styles);
		            		doc.CloseDoc();
	            		}
	            		// Open modal windows with import results
	            		getUI().addWindow(new FeatureUpload(styles, reqs, productcb.getValue(), 
	            				tempFile, DocumentAdvView.this, viewLogic));
            		}
            		else{
            			// Do nothing
            			tempFile.delete();
            		}
            	} 
            	catch (Exception e) {
            		e.printStackTrace();
            	}
            }
        });
        upload.setStyleName(ValoTheme.BUTTON_FRIENDLY);
        upload.setButtonCaption(Messages.get().getKey("import"));
        //upload.setIcon(FontAwesome.UPLOAD);
        upload.setCaption(null);
        upload.setImmediate(true);
        upload.setEnabled(false);
        
        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setSpacing(true);
        topLayout.setMargin(true);
        topLayout.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
        topLayout.setWidth("100%");
        topLayout.addComponent(showTree);
        topLayout.addComponent(search);
        topLayout.addComponent(navButtons());
        topLayout.addComponent(productcb);
        topLayout.addComponent(pageButtons());
        topLayout.addComponent(upload);
        topLayout.addComponent(newRecord);
        topLayout.setExpandRatio(upload, 1);
        topLayout.setStyleName("top-bar");
        return topLayout;
    }

    @Override
    public void enter(ViewChangeEvent event) {
    	productcb.Refresh();
    	viewLogic.init();
    	//refreshGrid();
    }

    public void showError(String msg) {
        Notification.show(msg, Type.ERROR_MESSAGE);
    }

    public void showSaveNotification(String msg) {
        Notification.show(msg, Type.TRAY_NOTIFICATION);
    }
    
    public void refreshGrid() {
    	if (productcb.getValue() != null) {
    		barAndGridLayout.removeComponent(grid);
         	ProductEntity product = (ProductEntity) productcb.getValue();
         	Long prd = product.getID();
    	
	    	grid = new DocumentPanel(viewLogic, prd, true, debug);
	    	barAndGridLayout.addComponent(grid);
	    	barAndGridLayout.setExpandRatio(grid, 1);
	    	page.setValue(grid.getCurrpage()+"/"+grid.getLastpage());
	    	newRecord.setEnabled(prd!=0);
	    	upload.setEnabled(prd!=0);
	    	
    		if(!product.getActive()){
    			newRecord.setEnabled(false);
    			upload.setEnabled(false);
    			grid.setEnabled(false);
    			showSaveNotification(Messages.get().getKey("productdisabled"));
    		}
    	}
    }

    public List<FeatureEntity> getFeatures(List<Style> styles){
    	try {
    		ApoiDocImport doc = new ApoiDocImport(tempFile);
    		List<FeatureEntity> reqs = doc.getFeatures(styles);
    		doc.CloseDoc();
			return reqs;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
    
    public void setFilter(String filter){
    	grid.findFeature(filter);
        page.setValue(grid.getCurrpage()+"/"+grid.getLastpage());
    }
    
    private void setDocumentInfo(int idx){
    	if(docs==null){
    		Doc.setValue("0/0");
    		return;
    	}
    	if(docs.size()>0 && idx>0){
    		if(idx<0) idx=0;
    		if(idx>=docs.size()) idx=docs.size()-1;
    		docidx=idx;
    		Doc.setValue((docidx+1)+"/"+docs.size());
    		grid.findFeature(docs.get(docidx).getDescription());
    		page.setValue(grid.getCurrpage()+"/"+grid.getLastpage());
    	}
    	else{
    		docidx=0;
    		Doc.setValue((docidx+1)+"/"+docs.size());
    	}
    }
    
    private HorizontalLayout navButtons(){
    	HorizontalLayout nav = new HorizontalLayout();
        Button prev = new Button("<");
 		prev.setIcon(FontAwesome.CARET_LEFT);
 		prev.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
 		prev.addStyleName(ValoTheme.BUTTON_SMALL);
 		Doc = new TextField();
 		Doc.setEnabled(false);
 		Doc.setWidth("60px");
 		Button next = new Button(">");
 		next.setIcon(FontAwesome.CARET_RIGHT);
 		next.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
 		next.addStyleName(ValoTheme.BUTTON_SMALL);
 		nav.addComponent(prev);
 		nav.addComponent(Doc);
 		nav.addComponent(next);
 		
 		next.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	docidx++;
                setDocumentInfo(docidx);
            }
        });
        
        prev.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	docidx--;
                setDocumentInfo(docidx);
            }
        });
 		
 		return nav;
    }
    
    private HorizontalLayout pageButtons() {
    	HorizontalLayout nav = new HorizontalLayout();
    	Button prev = new Button("<");
		prev.setIcon(FontAwesome.CARET_LEFT);
		prev.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
		prev.addStyleName(ValoTheme.BUTTON_SMALL);
		page = new TextField();
		//page.setEnabled(false);
		page.setWidth("80px");
		Button next = new Button(">");
		next.setIcon(FontAwesome.CARET_RIGHT);
		next.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
		next.addStyleName(ValoTheme.BUTTON_SMALL);
		nav.addComponent(prev);
        nav.addComponent(page);
        nav.addComponent(next);
		
		prev.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                grid.prevPage();
                page.setValue(grid.getCurrpage()+"/"+grid.getLastpage());
            }
        });
		
		next.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                grid.nextPage();
                page.setValue(grid.getCurrpage()+"/"+grid.getLastpage());
            }
        });
		
		page.addTextChangeListener(new FieldEvents.TextChangeListener() {
	        @Override
	        public void textChange(FieldEvents.TextChangeEvent event) {
	        	try{
	        		int pag = Integer.parseInt(event.getText());
		            grid.setPage(pag);
		            page.setValue(grid.getCurrpage()+"/"+grid.getLastpage());
	        	}
	        	catch(Exception e) {
	        		// do nothing if Integer.parseInt() fails
	        	}
	        }
	    });
        
    	return nav;
    }

    public void setNewRecordEnabled(boolean enabled) {
    	newRecord.setEnabled(enabled);
    	upload.setEnabled(enabled);
    }

	@Override
	public void clearSelection() {
		// unused
	}

	@Override
	public void selectRow(FeatureEntity row) {
		// unused
	}

	@Override
	public FeatureEntity getSelectedRow() {
		// unused
		return null;
	}

	@Override
	public void editRecord(FeatureEntity rec) {
		// unused
	}

	@Override
	public void showRecords(Collection<FeatureEntity> records) {
		// unused
	}

	@Override
	public void refreshRecord(FeatureEntity rec) {
		// unused
	}

	@Override
	public void removeRecord(FeatureEntity rec) {
		// unused
	}
    
}
