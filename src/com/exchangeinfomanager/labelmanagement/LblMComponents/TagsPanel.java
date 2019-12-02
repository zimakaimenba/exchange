package com.exchangeinfomanager.labelmanagement.LblMComponents;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import java.awt.Point;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import com.exchangeinfomanager.Services.TagService;
import com.exchangeinfomanager.ServicesForNodes.SvsForNodeOfFileNodes;
import com.exchangeinfomanager.StockCalendar.ColorScheme;
import com.exchangeinfomanager.commonlib.JUpdatedTextField;
import com.exchangeinfomanager.commonlib.ParseBanKuaiWeeklyFielGetBanKuaisProcessor;
import com.exchangeinfomanager.commonlib.ParseBanKuaiWeeklyFielGetStocksProcessor;
import com.exchangeinfomanager.commonlib.WrapLayout;
import com.exchangeinfomanager.labelmanagement.TagsServiceForSystemTags;
import com.exchangeinfomanager.labelmanagement.TagCache;
import com.exchangeinfomanager.labelmanagement.TagCacheListener;
import com.exchangeinfomanager.labelmanagement.Tag.CombineTagsDialog;
import com.exchangeinfomanager.labelmanagement.Tag.CreateTagDialog;
import com.exchangeinfomanager.labelmanagement.Tag.InsertedTag;
import com.exchangeinfomanager.labelmanagement.Tag.ModifyTagDialog;
import com.exchangeinfomanager.labelmanagement.Tag.Tag;
import com.exchangeinfomanager.labelmanagement.TagSearch.JDialogForTagSearchMatrixPanelForWholeSearchTags;
import com.exchangeinfomanager.labelmanagement.TagSearch.JDislogForTagSearchMatrixPanelForTagsBundleAdd;
import com.exchangeinfomanager.labelmanagement.TagSearch.TagSearchMatrixPanelForWholeSearchTags;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.operations.AllCurrentTdxBKAndStoksTree;
import com.google.common.base.Charsets;
import com.google.common.io.Files;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.border.LineBorder;

public class TagsPanel extends JPanel implements TagCacheListener
{
	private String title;
	private String displaymode;
	private String controlmode;
	/**
	 * Create the panel.
	 */
	public TagsPanel(String title,String displaymode,String controlmode)
	{
		this.title = title;
		
		if(displaymode == null)
			this.displaymode = "displayhead";
		else
			this.displaymode = displaymode;
		
		this.controlmode = controlmode;
		
//		createGui ();
//		createEvents ();
	}
//	public TagsPanel(String title)
//	{
//		this.title = title;
//		this.displaymode = "displayhead";
//	}
	
	public static String HIDEHEADERMODE = "hidehead";
	public static String ONLYIPLAYMODE = "only_display_mode";
	public static String FULLCONTROLMODE = "full_control_mode";
	public static String PARTCONTROLMODE = "part_control_mode";
	public static String NONECONTROLMODE = "none_control_mode";
	public static String ADDNEWTAGSTONODE = "add_new_tags_to_node";
	public static String SELFFULLCONTROLMODE = "Tag_full_control_himself";
	
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this); //	https://stackoverflow.com/questions/4690892/passing-a-value-between-components/4691447#4691447
	public void addPropertyChangeListener(PropertyChangeListener listener) {
	        pcs.addPropertyChangeListener(listener);
	}
	
	public void initializeTagsPanel(TagService lbdbservice, TagCache cache) 
	{
		this.lbdbservice =  lbdbservice;
		this.cache =  cache;
		cache.addCacheListener(this);
		
		this.removeAll();
		createGui ();
		this.revalidate();
		this.repaint();
		
		initializeTags ();
		
		createEvents ();
	}
	
	private void initializeTags()
	{
		Collection<Tag> alltags = cache.produceTags();
		
		for (Tag l : alltags) {

			LabelTag label = new LabelTag (l);
			this.initializeMenu (label);
			label.addPropertyChangeListener(new PropertyChanged() );
			pnllabelcontain.add(label);
		}
	}
	
	@Override
	public void onTagChange(TagCache cache) {
		cache.refreshTags();
		initializeTags ();
		
	}
	
	private void createEvents() 
	{
		menuItemAddNew.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				addMenuAction ();
			}
			
		});
		menuItemReset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				resetAction ();
			}
			
		});
		
		tfldsearchkw.addKeyListener(new KeyAdapter() {
			
			public void keyPressed(KeyEvent  evt)
			{
				if(evt.getKeyCode() == KeyEvent.VK_ENTER)	{
					searchKeyWrodsInTagList (tfldsearchkw.getText().trim() );
			}
			}
		});
	}

	private JPanel pnllabelcontain;
	private TagService lbdbservice;
	private TagCache cache;
	private JPopupMenu Pmenu ;

	private JUpdatedTextField tfldsearchkw;

//	private JButton btnkaddtocur;

	private JMenuItem menuItemAddToCur;
	private JPopupMenu selfMenu;
	private JMenuItem menuItemAddNew;
	private JMenuItem menuItemReset;
	private JLabel searchcount;
	private JScrollPane sclpcenter;
	
	public void createGui ()
	{
        this.setLayout(new BorderLayout(0,0));
 
        JPanel pnlup = new JPanel ();
		pnlup.setLayout (new FlowLayout(FlowLayout.LEFT) );
		JLabel lblbkkw = new JLabel(this.title);
		JLabel lblbkfengge = new JLabel("  ");
		tfldsearchkw = new JUpdatedTextField ("                    ");
		tfldsearchkw.setCaretPosition(8);
		
		searchcount = new JLabel ("  ");
		
		pnlup.add(lblbkkw);
		pnlup.add(lblbkfengge);
		pnlup.add(tfldsearchkw);
		pnlup.add(searchcount);
        
		sclpcenter = new JScrollPane();
		pnllabelcontain = new JPanel();
		pnllabelcontain.setLayout(new WrapLayout(WrapLayout.CENTER, 5, 5));
		pnllabelcontain.setBackground(ColorScheme.BACKGROUND);
	    sclpcenter.setViewportView (pnllabelcontain);
	    
	    
	    if(!this.displaymode.equals(TagsPanel.HIDEHEADERMODE ))
	    	this.add(pnlup,BorderLayout.NORTH);
	    
		this.add(sclpcenter,BorderLayout.CENTER);
		
		selfMenu = new JPopupMenu("test");   
		menuItemAddNew = new JMenuItem("新增");
		menuItemReset = new JMenuItem("Reset");
		
		selfMenu.add(menuItemAddNew);
		selfMenu.add(menuItemReset);
		
		pnllabelcontain.addMouseListener( new DisPlayMenuController () );
		
	}
	
	 private void initializeMenu(LabelTag label) 
	 {
			Pmenu = label.getMenu();   
			menuItemAddToCur = new JMenuItem("加入到当前");
			
			menuItemAddToCur.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent evt) {
					
					Collection<Tag> tagslist = cache.produceSelectedTags();

					PropertyChangeEvent evtzd = new PropertyChangeEvent(this, TagsPanel.ADDNEWTAGSTONODE , "", tagslist );
		            pcs.firePropertyChange(evtzd);
				}
				
			});
			
			if( this.controlmode.equals(TagsPanel.FULLCONTROLMODE ) ) {
				Pmenu.add(menuItemAddToCur,0);
				JSeparator sp = new JSeparator ();
				Pmenu.add(sp,1);
			} else
			if ( this.controlmode.equals(TagsPanel.SELFFULLCONTROLMODE)  ) {
//				label.setMenuMode(TagsPanel.PARTCONTROLMODE);
			} else 
			if ( this.controlmode.equals(TagsPanel.PARTCONTROLMODE)  ) {
				label.setMenuMode(TagsPanel.PARTCONTROLMODE);
			} else if ( this.controlmode.equals(TagsPanel.NONECONTROLMODE)  ) {
				label.setMenuMode(TagsPanel.NONECONTROLMODE); 
			} else {
				Pmenu.removeAll();
				Pmenu.add(menuItemAddToCur);
			}
			
	 }

	 
	 private class DisPlayMenuController extends MouseAdapter 
	 {
		 @Override //https://stackoverflow.com/questions/11605426/popup-on-left-click
	     public void mouseClicked(MouseEvent evt) {
        	 super.mouseClicked(evt);
        	 Component source = (Component)evt.getSource();
        	 
        	 Point location = source.getLocation();
        	 Dimension size = source.getSize();

        	 int xPos = location.x ;
        	 int yPos = location.y ;
        	 selfMenu.show(source, xPos, yPos);
		 }
	 }

	 private class PropertyChanged implements PropertyChangeListener 
	 {
			private String property;
			
	        public void propertyChange(PropertyChangeEvent evt) {
            	if (evt.getPropertyName().equals(LabelTag.PROPERTYCHANGEDASADD)) {
            		addMenuAction ();
            	}   else 
            	if (evt.getPropertyName().equals(LabelTag.PROPERTYCHANGEDASDELETE)) {
            		deleteMenuAction ();
            	} else
            	if (evt.getPropertyName().equals(LabelTag.PROPERTYCHANGEDASEDIT)) {
            		editMenuAction ();
            	} else
            	if(evt.getPropertyName().equals(LabelTag.PROPERTYCHANGEDASCOMBINE)) {
            		combinMenuAction ();
            	} else
            	if(evt.getPropertyName().equals(LabelTag.PROPERTYCHANGEDASSEARCH)) {
                		searchInternetMenuAction ();
                } else
            	if(evt.getPropertyName().equals(LabelTag.PROPERTYCHANGEDBUNCHADD)) {
            		bundleAddMenuAction();
            	} else
            	if(evt.getPropertyName().equals(LabelTag.PROPERTYCHANGEDRELATEDNODES)) {
                	displayRelatedNodes();
                } else
                if(evt.getPropertyName().equals(LabelTag.PROPERTYCHANGEDATAGWASSELECTED)) {
                	
                	PropertyChangeEvent evtzd = new PropertyChangeEvent(this, LabelTag.PROPERTYCHANGEDATAGWASSELECTED , "", cache.produceSelectedTags() );
                    pcs.firePropertyChange(evtzd);
                }
            	
            }
	}
	 
	private void searchInternetMenuAction() 
	{
		Collection<Tag> seltlb = this.cache.produceSelectedTags();
		String googlesearchquery = "https://www.google.com/search?q=%22" ;
		for(Tag tmptag : seltlb) {
			googlesearchquery = googlesearchquery + tmptag.getName() + "    ";
		}
		googlesearchquery = googlesearchquery + "%22"; 
//		googlesearchquery = Stack+Exchange%22+OR+StackOverflow"
		
		
		String os = System.getProperty("os.name").toLowerCase();
	        Runtime rt = Runtime.getRuntime();
		
		try{

		    if (os.indexOf( "win" ) >= 0) {

		        // this doesn't support showing urls in the form of "page.html#nameLink" 
		        rt.exec( "rundll32 url.dll,FileProtocolHandler " + googlesearchquery);

		    } else if (os.indexOf( "mac" ) >= 0) {

		        rt.exec( "open " + googlesearchquery);

	            } else if (os.indexOf( "nix") >=0 || os.indexOf( "nux") >=0) {

		        // Do a best guess on unix until we get a platform independent way
		        // Build a list of browsers to try, in this order.
		        String[] browsers = {"epiphany", "firefox", "mozilla", "konqueror",
		       			             "netscape","opera","links","lynx"};
		        	
		        // Build a command string which looks like "browser1 "url" || browser2 "url" ||..."
		        StringBuffer cmd = new StringBuffer();
		        for (int i=0; i<browsers.length; i++)
		            cmd.append( (i==0  ? "" : " || " ) + browsers[i] +" \"" + googlesearchquery + "\" ");
		        	
		        rt.exec(new String[] { "sh", "-c", cmd.toString() });

	           } else {
	                return;
	           }
	       }catch (Exception e){
		    return;
	       }
	      return;
			
	}
	public void displayRelatedNodes() 
	{
		JDialogForTagSearchMatrixPanelForWholeSearchTags pnlsearchtags = new JDialogForTagSearchMatrixPanelForWholeSearchTags();
		
		pnlsearchtags.setPreSearchMustHaveTags (cache.produceSelectedTags());
		pnlsearchtags.setModal(true);
		pnlsearchtags.setVisible(true);
		
	}
	private void bundleAddMenuAction ()
	{
//		String parsedpath = "E:\\";
//		JFileChooser chooser = new JFileChooser();
//		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
//		chooser.setCurrentDirectory(new File(parsedpath) );
//		
//		String filename;
//		if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
//		    if(chooser.getSelectedFile().isDirectory())
//		    	filename = (chooser.getSelectedFile()+ "\\").replace('\\', '/');
//		    else
//		    	filename = (chooser.getSelectedFile()).toString().replace('\\', '/');
//		} else
//			return;
//	
//		if(!filename.endsWith("EBK") ) { //不是板块文件
//			JOptionPane.showMessageDialog(null,"不是通达信板块导出文件，请使用正确格式文件。","Warning",JOptionPane.WARNING_MESSAGE);
//	   		return;
//		}
//		
//		SvsForNodeOfFileNodes svsfornodefile = new SvsForNodeOfFileNodes (filename);
//		Collection<BkChanYeLianTreeNode> addnodeset = svsfornodefile.getAllNodes();
		
		JDislogForTagSearchMatrixPanelForTagsBundleAdd bnndladd = new JDislogForTagSearchMatrixPanelForTagsBundleAdd ();
		bnndladd.setPreSearchMustHaveTags (this.cache.produceSelectedTags());
		bnndladd.toFront();
		bnndladd.setVisible(true);
//		TagService tagserviceofbunch = new DBNodesTagsService (addnodeset);
//		TagCache tagcacheofbuch = new TagCache (tagserviceofbunch);
//		tagserviceofbunch.setCache(tagcacheofbuch);
//		Collection<Tag> seltag = this.cache.produceSelectedTags();
//		try {
//			tagserviceofbunch.createTags (seltag);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		tagserviceofbunch = null;
//		tagcacheofbuch = null;
		
		PropertyChangeEvent evtzd = new PropertyChangeEvent(this, LabelTag.PROPERTYCHANGEDBUNCHADD , "",this.cache.produceSelectedTags() );
        pcs.firePropertyChange(evtzd);
		
		
	}
	private void combinMenuAction() 
	{
		CombineTagsDialog combineTagDialog = new CombineTagsDialog (lbdbservice);
		combineTagDialog.setLabel(new Tag("     ", ColorScheme.GREY_WHITER));
		combineTagDialog.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
		combineTagDialog.setVisible(true);
        
        PropertyChangeEvent evtzd = new PropertyChangeEvent(this, LabelTag.PROPERTYCHANGEDASEDIT , "", combineTagDialog.getLabel () );
        pcs.firePropertyChange(evtzd);
	}
	private void addMenuAction ()
	{
		CreateTagDialog createTagDialog = new CreateTagDialog (lbdbservice);
        createTagDialog.setLabel(new Tag("     ", ColorScheme.GREY_WHITER));
        createTagDialog.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
        createTagDialog.setVisible(true);
        
        PropertyChangeEvent evtzd = new PropertyChangeEvent(this, LabelTag.PROPERTYCHANGEDASADD , "", createTagDialog.getLabel () );
        pcs.firePropertyChange(evtzd);
	}
	private void editMenuAction ()
	{
//		 JLabel menulbl  = (JLabel) Pmenu.getInvoker();
//	   	 String clickname = menulbl.getName();
	   	 Collection<Tag> tagslist = cache.produceSelectedTags();
	   	 if(tagslist.size() >1) {
	   		 JOptionPane.showMessageDialog(null,"请只选择一个Tag做修改!","Warning",JOptionPane.WARNING_MESSAGE);
	   		 return;
	   	 }
	   	 
	   	 for (Iterator<Tag> lit = tagslist.iterator(); lit.hasNext(); ) {
		        Tag f = lit.next();
	        	ModifyTagDialog mdl = new ModifyTagDialog (lbdbservice);
	        	mdl.setLabel(f );
	        	mdl.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
	            mdl.setVisible(true);
		            
	            PropertyChangeEvent evtzd = new PropertyChangeEvent(this, LabelTag.PROPERTYCHANGEDASEDIT , "", mdl.getLabel () );
	            pcs.firePropertyChange(evtzd);
	   	 }
	}
	private void deleteMenuAction ()
	{
		String warningmsg = "";
		if(lbdbservice instanceof TagsServiceForSystemTags) {
			warningmsg = "警告:确定删除? 删除后无法恢复!";
		} else
			warningmsg = "警告:确定从当前移除?";
		int exchangeresult = JOptionPane.showConfirmDialog(null,warningmsg , "询问?", JOptionPane.OK_CANCEL_OPTION);
		if(exchangeresult == JOptionPane.CANCEL_OPTION)
				return;
		
		Collection<Tag> tagslist = cache.produceSelectedTags();
		try {
			lbdbservice.deleteTags(tagslist);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		PropertyChangeEvent evtzd = new PropertyChangeEvent(this, LabelTag.PROPERTYCHANGEDASDELETE , "", tagslist );
        pcs.firePropertyChange(evtzd);
	
	}
	protected void resetAction() 
	{
		Collection<Tag> curlabl = cache.produceTags();
		for (Iterator<Tag> lit = curlabl.iterator(); lit.hasNext(); ) {
	        InsertedTag f = (InsertedTag) lit.next();
	        
	        if(!f.isSelected())
	        	continue;
	        f.setSelected(false);
	    }
		
		Component[] pnltags = pnllabelcontain.getComponents();
        for( Component tmpc :  pnltags) {
        	Tag l = ((LabelTag)tmpc).getTag();
        	LineBorder line = new LineBorder(l.getColor(), 2, true);
        	((JPanel)tmpc).setBorder(line);
        }
        
		this.revalidate();
	}
	
	protected void searchKeyWrodsInTagList(String text) 
	{
		searchcount.setText("");
		if(text.trim().isEmpty())
			text = "abcdefghigklmnopqrstuvwxyz";
		
		int searchresult = 0;
		Component[] pnltags = pnllabelcontain.getComponents();
		for( Component tmpc :  pnltags) {
        	Tag l = ((LabelTag)tmpc).getTag();
        	Boolean chkresult = l.checkHanYuPinYing (text.trim() );
        	if(chkresult) {
        		LineBorder line = new LineBorder(Color.MAGENTA, 2, true);
            	((JPanel)tmpc).setBorder(line);
            	
            	searchresult ++;
        	} else {
        		LineBorder line = new LineBorder(l.getColor(), 2, true);
            	((JPanel)tmpc).setBorder(line);
        	}
        }
		
		searchcount.setText("(" + String.valueOf(searchresult) + ")");
	}

}

