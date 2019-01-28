package freewill.nextgen.feature;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.vaadin.hene.expandingtextarea.ExpandingTextArea;

////import com.vaadin.addon.contextmenu.ContextMenu;
////import com.vaadin.addon.contextmenu.MenuItem;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.MouseEvents;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

import freewill.nextgen.data.FeatureEntity;
import freewill.nextgen.data.Style.StyleEnum;
import freewill.nextgen.hmi.utils.Messages;
import freewill.nextgen.hmi.common.ConfirmDialog;

@SuppressWarnings("serial")
public class DocumentPanelItem extends HorizontalLayout {

	private FeatureEntity innerRec = null;
	//private MenuBar menu = null;
	private MenuBar.MenuItem recid = null;
	////private ContextMenu menu = null;
	////private MenuItem recid = null;
	private DocumentPanel parent = null;
	private Image image = null;
	private Label tags = null;
	private String index = "";
	private boolean showMenu = true;
    private List<SelectItemListener> listeners = new ArrayList<SelectItemListener>();
    private FeatureCrudLogic viewLogic = null;
	
	public String getIdx(){
		return index;
	}
	
	public DocumentPanelItem(FeatureCrudLogic logic, String idx, FeatureEntity rec, boolean focus, 
			DocumentPanel prnt, boolean menus, boolean debug) {
		
		viewLogic = logic;
		
		VerticalLayout debugLayout = new VerticalLayout();
		Label iddebug = new Label(""+rec.getID());
		Label parentdebug = new Label(""+rec.getParent());
		debugLayout.setSpacing(false);
		debugLayout.setWidth("50px");
		debugLayout.addComponents(iddebug, parentdebug);
		
		showMenu = menus;
		innerRec = rec;
		parent = prnt;
		index = idx;
		String mtitle, mtextarea;
		if(idx.contains("•") || idx.equals("") || idx.contains("Figure")){
			mtitle = "  ";
			mtextarea = idx;
		}
		else{
			mtitle = idx;
			mtextarea = "  ";
		}
		
        this.setSpacing(false); // true
        this.setMargin(false);
        this.setSizeFull();
        this.setReadOnly(true);
        
        TextField title = new TextField(mtitle);
        title.setStyleName(ValoTheme.LABEL_BOLD);
        title.setValue(rec.getTitle());
        title.setWidth("100%");
        
        /*switch(rec.getLevel()){
	        case H1: title.addStyleName(ValoTheme.LABEL_H1); break;
	        case H2: title.addStyleName(ValoTheme.LABEL_H2); break;
	        case H3: title.addStyleName(ValoTheme.LABEL_H3); break;
	        case H4: title.addStyleName(ValoTheme.LABEL_H4); break;
	        case H5: title.addStyleName(ValoTheme.LABEL_H4); break;
	        case H6: title.addStyleName(ValoTheme.LABEL_H4); break;
	        case H7: title.addStyleName(ValoTheme.LABEL_H4); break;
	        case H8: title.addStyleName(ValoTheme.LABEL_H4); break;
			default: break;
        }*/
        
        image = new Image();
        int size = (int)(60.0*rec.getImagesize());
        image.setWidth(size+"%"); // Antes 50%
        byte[] data = rec.getImage();
        if(data!=null && data.length>0){
        	StreamResource resource = new StreamResource(
        		new StreamResource.StreamSource() {
        			@Override
        			public InputStream getStream() {
        				return new ByteArrayInputStream(data);
        			}
        	    }, rec.getImagename());
        	image.setSource(resource);
    	}
        
        image.addClickListener(new MouseEvents.ClickListener() {
			@Override
			public void click(MouseEvents.ClickEvent event) {
				getUI().addWindow(new DocumentShowImage(innerRec));
			}
        });
        
        TextArea desc = new ExpandingTextArea(mtextarea);
        desc.setWidth("100%");
        desc.setValue(rec.getDescription());
        desc.addStyleName(ValoTheme.TEXTAREA_BORDERLESS);
        
        String tagstxt = rec.getTags();
        if(tagstxt!=null)
        	tagstxt = tagstxt.replace(",", ", ");
        tags = new Label(tagstxt);
        tags.setStyleName(ValoTheme.LABEL_COLORED);
        tags.addStyleName(ValoTheme.LABEL_SMALL);
        tags.setWidth("100%");
        
        MenuBar menu = new MenuBar();
        //menu.setIcon(FontAwesome.NAVICON);
        //menu.setStyleName("icon-only");
        menu.addStyleName(ValoTheme.MENUBAR_BORDERLESS);
        menu.setWidth("100%");
        MenuBar.MenuItem options = menu.addItem("", null);
        options.setIcon(FontAwesome.NAVICON);
        options.setStyleName("icon-only");
        
        Button useit = new Button(Messages.get().getKey("useit"));
        useit.setStyleName(ValoTheme.BUTTON_SMALL);
        useit.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
        useit.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
            	selectItem();
            }
        });
        
        VerticalLayout vlayout = new VerticalLayout();
        vlayout.setSpacing(false);
        vlayout.setMargin(false);
        vlayout.setWidth("70px");
        vlayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        if(showMenu){
        	//vlayout.addComponent(menu2);
        	vlayout.addComponent(menu);
        }
        else
        	vlayout.addComponent(useit);
        
        FormLayout flayout = new FormLayout();
        flayout.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        flayout.setDefaultComponentAlignment(Alignment.TOP_LEFT);
        flayout.setSpacing(false);
        flayout.setMargin(false);
        flayout.setSizeFull();
        flayout.addComponent(title);
        flayout.addComponent(image);
        flayout.addComponent(desc);
        
        if(rec.getLevel().ordinal()>StyleEnum.H8.ordinal()) title.setVisible(false);
        
        VerticalLayout vflayout = new VerticalLayout();
        vflayout.setSpacing(false);
        vflayout.setMargin(false);
        vflayout.setSizeFull();
        vflayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        vflayout.addComponent(flayout);
        //vflayout.addComponent(image);
        vflayout.addComponent(tags);
        vflayout.setComponentAlignment(tags, Alignment.BOTTOM_CENTER);
        
        image.setVisible((data!=null && data.length>0));
        tags.setVisible((tagstxt!=null && tagstxt.length()>0));
        
        this.addComponent(vflayout);
        if(debug) this.addComponent(debugLayout);
        this.addComponent(vlayout);
        this.setExpandRatio(vflayout, 1);
        
        if(focus) title.focus();
        
        // Add here Listeners
        
        title.setImmediate(true);
        title.addTextChangeListener(new FieldEvents.TextChangeListener() {
            @Override
            public void textChange(FieldEvents.TextChangeEvent event) {
            	if(showMenu)
            		updateDocument(event.getText(), desc.getValue());
            }
        });
        
        desc.setImmediate(true);
        desc.addTextChangeListener(new FieldEvents.TextChangeListener() {
            @Override
            public void textChange(FieldEvents.TextChangeEvent event) {
            	if(showMenu)
            		updateDocument(title.getValue(), event.getText());
            }
        });
        
		// Configure context menu
        
		recid = options.addItem(""+rec.getID(), e -> {
        	innerRec.setActive(!innerRec.getActive());
        	if(innerRec.getActive() == true){
            	recid.setIcon(FontAwesome.TOGGLE_ON);
            	recid.setText(Messages.get().getKey("disable")+" ("+rec.getID()+")");
        	}
            else{
            	recid.setIcon(FontAwesome.TOGGLE_OFF);
            	recid.setText(Messages.get().getKey("enable")+" ("+rec.getID()+")");
            }
        	viewLogic.saveRecord(innerRec);
        });
        if(rec.getActive() == true){
        	recid.setIcon(FontAwesome.TOGGLE_ON);
        	recid.setText(Messages.get().getKey("disable")+" ("+rec.getID()+")");
        }
        else{
        	recid.setIcon(FontAwesome.TOGGLE_OFF);
        	recid.setText(Messages.get().getKey("enable")+" ("+rec.getID()+")");
        }
        
        options.addItem("Remove", e -> {
        	if(viewLogic.hasChildDocument(innerRec.getID())){
        		showSaveNotification("Features with children cannot be removed");
        		return;
        	}
        	ConfirmDialog cd = new ConfirmDialog("Do you really want to remove these Feature ("+innerRec.getID()+")?");
        	cd.setOKAction(new ClickListener() {
                @Override
                public void buttonClick(final ClickEvent event) {
        			cd.close();
        			removeDocument();
                }
            });
        	getUI().addWindow(cd);
        });
        
        options.addSeparator();
        MenuBar.MenuItem imageitem = options.addItem("Image", null);
        
        imageitem.addItem("Add Image...", e -> {
        	if(innerRec.getLevel().ordinal()!=StyleEnum.FIGURE.ordinal()){
        		showSaveNotification("It can be only used with Figure items");
        		return;
        	}
        	// Open modal window to upload image file
    		getUI().addWindow(new DocumentLoadImage(this));
        });
        
        imageitem.addItem("Remove Image", e -> {
        	innerRec.setImage(new byte[0]);
        	innerRec.setImagename("");
        	image.setSource(null);
        	viewLogic.saveRecord(innerRec);
        	showSaveNotification(innerRec.getTitle() + " (" + innerRec.getID() + ") updated");
        	image.setVisible(false);
        });
        
        MenuBar.MenuItem sizeitem = imageitem.addItem("Size", null);
        sizeitem.addItem("10%", e -> {
            innerRec.setImagesize(0.10);
            int isize = (int)(60.0*rec.getImagesize());
            image.setWidth(isize+"%");
 	        viewLogic.saveRecord(innerRec);
 	        showSaveNotification(innerRec.getTitle() + " (" + innerRec.getID() + ") updated");
        });
        sizeitem.addItem("25%", e -> {
            innerRec.setImagesize(0.25);
            int isize = (int)(60.0*rec.getImagesize());
            image.setWidth(isize+"%");
 	        viewLogic.saveRecord(innerRec);
 	        showSaveNotification(innerRec.getTitle() + " (" + innerRec.getID() + ") updated");
        });
        sizeitem.addItem("50%", e -> {
			innerRec.setImagesize(0.50);
			int isize = (int)(60.0*rec.getImagesize());
            image.setWidth(isize+"%");
	        viewLogic.saveRecord(innerRec);
	        showSaveNotification(innerRec.getTitle() + " (" + innerRec.getID() + ") updated");
		});
        sizeitem.addItem("75%", e -> {
			innerRec.setImagesize(0.75);
			int isize = (int)(60.0*rec.getImagesize());
            image.setWidth(isize+"%");
	        viewLogic.saveRecord(innerRec);
	        showSaveNotification(innerRec.getTitle() + " (" + innerRec.getID() + ") updated");
		});
        sizeitem.addItem("100%", e -> {
			innerRec.setImagesize(1.0);
			int isize = (int)(60.0*rec.getImagesize());
            image.setWidth(isize+"%");
	        viewLogic.saveRecord(innerRec);
	        showSaveNotification(innerRec.getTitle() + " (" + innerRec.getID() + ") updated");
		});
        sizeitem.addItem("125%", e -> {
			innerRec.setImagesize(1.25);
			int isize = (int)(60.0*rec.getImagesize());
            image.setWidth(isize+"%");
	        viewLogic.saveRecord(innerRec);
	        showSaveNotification(innerRec.getTitle() + " (" + innerRec.getID() + ") updated");
		});
        
        options.addSeparator();
        MenuBar.MenuItem additem = options.addItem("Add New", null);
        
        additem.addItem("Child Title", e -> {
        	// En Paragraphs, List y Figures Items no se pueden crear nuevos titulos
        	if(innerRec.getLevel().ordinal()>StyleEnum.H8.ordinal()){
        		showSaveNotification("It can only be used with Titles");
        		return;
        	}
        	FeatureEntity newrec = new FeatureEntity();
            newrec.setTitle("New Child Feature Title...");
            newrec.setDescription("");
            int level = innerRec.getLevel().ordinal();
            if(level<StyleEnum.H8.ordinal())
            	newrec.setLevel(StyleEnum.values()[level+1]);
            else
            	newrec.setLevel(StyleEnum.values()[level]);
            newrec.setParent(innerRec.getID());
            newrec.setCompany(innerRec.getCompany());
            newrec.setProduct(innerRec.getProduct());
            newrec.setActive(innerRec.getActive());
            viewLogic.saveRecord(newrec);
            // insert new feature after parent 
            parent.newGridItem(newrec, this);
        });
        
        additem.addItem("Paragraph", e -> {
        	// En List Items no se pueden crear nuevos registros
        	if(innerRec.getLevel().ordinal()==StyleEnum.PARAGRAM.ordinal()){
        		showSaveNotification("It cannot be used with List Items");
        		return;
        	}
        	FeatureEntity newrec = new FeatureEntity();
            newrec.setTitle("");
            newrec.setDescription("New Paragraph...");
            newrec.setLevel(StyleEnum.NORMAL);
            newrec.setParent(innerRec.getID());
            newrec.setCompany(innerRec.getCompany());
            newrec.setProduct(innerRec.getProduct());
            newrec.setActive(innerRec.getActive());
            viewLogic.saveRecord(newrec);
            // insert new feature after parent 
            parent.newGridItem(newrec, this);
        });
        
        additem.addItem("List Item", e -> {
        	// En List Items no se pueden crear nuevos registros
        	/*if(innerRec.getLevel().ordinal()==StyleEnum.PARAGRAM.ordinal()){
        		showSaveNotification("It cannot be used with List Items");
        		return;
        	}*/
        	FeatureEntity newrec = new FeatureEntity();
            newrec.setTitle("");
            newrec.setDescription("New List Item...");
            newrec.setLevel(StyleEnum.PARAGRAM);
            newrec.setParent(innerRec.getID());
            newrec.setCompany(innerRec.getCompany());
            newrec.setProduct(innerRec.getProduct());
            newrec.setActive(innerRec.getActive());
            viewLogic.saveRecord(newrec);
            // insert new feature after parent 
            parent.newGridItem(newrec, this);
        });
        
        additem.addItem("Figure", e -> {
        	// En List Items no se pueden crear nuevos registros
        	if(innerRec.getLevel().ordinal()==StyleEnum.PARAGRAM.ordinal()){
        		showSaveNotification("It cannot be used with List Items");
        		return;
        	}
        	FeatureEntity newrec = new FeatureEntity();
            newrec.setTitle("");
            newrec.setDescription("New Figure...");
            newrec.setLevel(StyleEnum.FIGURE);
            newrec.setParent(innerRec.getID());
            newrec.setCompany(innerRec.getCompany());
            newrec.setProduct(innerRec.getProduct());
            newrec.setActive(innerRec.getActive());
            viewLogic.saveRecord(newrec);
            // insert new feature after parent 
            parent.newGridItem(newrec, this);
        });
        
        options.addSeparator();
        MenuBar.MenuItem chgitem = options.addItem("Change to", null);
        
        chgitem.addItem("Title", e -> {
            innerRec.setLevel(StyleEnum.NORMAL);
            FeatureEntity parentRec = viewLogic.findRecord(innerRec.getParent());
            if(parentRec==null){
            	showSaveNotification("This item does no have a parent");
        		return;
            }
            int level = parentRec.getLevel().ordinal();
            if(level<StyleEnum.H8.ordinal())
            	innerRec.setLevel(StyleEnum.values()[level+1]);
            viewLogic.saveRecord(innerRec);
            desc.setCaption("");
            title.setVisible(false);
            showSaveNotification(innerRec.getTitle() + " (" + innerRec.getID() + ") updated");
        });
        
        chgitem.addItem("Paragraph", e -> {
            innerRec.setLevel(StyleEnum.NORMAL);
            viewLogic.saveRecord(innerRec);
            desc.setCaption("");
            title.setVisible(false);
            showSaveNotification(innerRec.getTitle() + " (" + innerRec.getID() + ") updated");
        });
        
        chgitem.addItem("List Item", e -> {
            innerRec.setLevel(StyleEnum.PARAGRAM);
            viewLogic.saveRecord(innerRec);
            desc.setCaption("•");
            title.setVisible(false);
            showSaveNotification(innerRec.getTitle() + " (" + innerRec.getID() + ") updated");
        });
        
        chgitem.addItem("Figure", e -> {
            innerRec.setLevel(StyleEnum.FIGURE);
            viewLogic.saveRecord(innerRec);
            desc.setCaption("Figure");
            title.setVisible(false);
            showSaveNotification(innerRec.getTitle() + " (" + innerRec.getID() + ") updated");
        });
        
        options.addSeparator();
        
        options.addItem("Edit Tags...", e -> {
        	// Open modal window to edit tags
    		getUI().addWindow(new DocumentTags(this, innerRec.getTags()));
        });
        
    }
	
	public void showSaveNotification(String msg) {
        Notification.show(msg, Type.TRAY_NOTIFICATION);
    }
    
    private void updateDocument(String title, String desc){
    	System.out.println("Updating Id = " + innerRec);
    	System.out.println("      Title = " + title);
    	System.out.println("      Desc  = " + desc);
    	innerRec.setTitle(title);
    	innerRec.setDescription(desc);
    	viewLogic.saveRecord(innerRec);
    	//showSaveNotification(innerRec.getTitle() + " (" + innerRec.getID() + ") updated");
    }
    
    private void removeDocument(){
    	viewLogic.deleteRecord(innerRec);
    	parent.removeGridItem(this);
    	//showSaveNotification(innerRec.getTitle() + " (" + innerRec.getID() + ") deleted");
    }
    
    public void addImage(File tempFile){
    	try {
    		FileResource resource = new FileResource(tempFile);
            image.setSource(resource);
        	// first converts File to byte[]
    		byte[] array = Files.readAllBytes(tempFile.toPath());
        	// then saves image into Document
        	innerRec.setImage(array);
        	innerRec.setImagename(tempFile.getName());
        	viewLogic.saveRecord(innerRec);
        	// tempFile.delete();
        	image.setVisible(true);
    	} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void updateTags(String tags){
    	try {
    		innerRec.setTags(tags);
    		viewLogic.saveRecord(innerRec);
        	this.tags.setValue(tags.replace(",", ", "));
            this.tags.setVisible((tags!=null && tags.length()>0));
    	} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
    public FeatureEntity getinnerRec(){
    	return innerRec;
    }

    public interface SelectItemListener {
        void selectItemListener();
    }
    
    public void addListener(SelectItemListener toAdd) {
        listeners.add(toAdd);
    }
    
    public void selectItem() {
        // Notify everybody that may be interested.
        for (SelectItemListener hl : listeners)
        	hl.selectItemListener();
    }

}
