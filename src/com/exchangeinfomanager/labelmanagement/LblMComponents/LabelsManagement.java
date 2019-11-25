package com.exchangeinfomanager.labelmanagement.LblMComponents;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Menu;
import java.awt.Point;
import java.awt.Rectangle;
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
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.exchangeinfomanager.StockCalendar.ColorScheme;
import com.exchangeinfomanager.StockCalendar.JUpdatedLabel;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiInfoTableModel;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiPopUpMenuForTable;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.DBLabelService;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.InsertedMeeting;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.JPanelFactory;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.LabelDialog;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Meeting;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.InsertedMeeting.Label;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.NewsPnl2.TDXNodesZhiShuGJRQPnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartPnl;
import com.exchangeinfomanager.commonlib.WrapLayout;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.labelmanagement.DBSystemTagsService;
import com.exchangeinfomanager.labelmanagement.LabelCache;
import com.exchangeinfomanager.labelmanagement.LabelCacheListener;
import com.exchangeinfomanager.labelmanagement.TagService;
import com.exchangeinfomanager.labelmanagement.Tag.CombineTagsDialog;
import com.exchangeinfomanager.labelmanagement.Tag.CreateTagDialog;
import com.exchangeinfomanager.labelmanagement.Tag.InsertedTag;
import com.exchangeinfomanager.labelmanagement.Tag.ModifyTagDialog;
import com.exchangeinfomanager.labelmanagement.Tag.Tag;
import com.exchangeinfomanager.labelmanagement.Tag.TagDialog;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.border.LineBorder;

public class LabelsManagement extends JPanel implements LabelCacheListener
{
	private String title;
	private String displaymode;
	private String controlmode;
	/**
	 * Create the panel.
	 */
	public LabelsManagement(String title,String displaymode,String controlmode)
	{
		this.title = title;
		
		if(displaymode == null)
			this.displaymode = "displayhead";
		else
			this.displaymode = displaymode;
		
		this.controlmode = controlmode;
	}
	public LabelsManagement(String title)
	{
		this.title = title;
		this.displaymode = "displayhead";
	}
	
	public static String HIDEHEADERMODE = "hidehead";
	public static String ONLYIPLAYMODE = "only_display_mode";
	public static String FULLCONTROLMODE = "full_control_mode";
	public static String PARTCONTROLMODE = "part_control_mode";

	
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this); //	https://stackoverflow.com/questions/4690892/passing-a-value-between-components/4691447#4691447
	public void addPropertyChangeListener(PropertyChangeListener listener) {
	        pcs.addPropertyChangeListener(listener);
	}
	public static final String ADDNEWTAGSTONODE = "add_new_tags_to_node";
	public static final String NODEADDNEWTAGSNEEDSYSUPDAT = "node_add_new_tags_need_sys_upated";
	public static final String NODESBEENDELETED = "nodes_been_deleted";
	public static final String NODESBEENEDIT = "nodes_been_edited";
	
	public void initializeLabelsManagement(TagService lbdbservice, LabelCache cache) 
	{
		this.lbdbservice =  lbdbservice;
		this.cache =  cache;
		cache.addCacheListener(this);
		
		createGui ();
		initializeTags ();
		
		createEvetns ();
	}
	
	private void initializeTags()
	{
		Collection<Tag> alltags = cache.produceLabels();
		
		pnllabelcontain.removeAll();
		for (Tag l : alltags) {
			LabelTag label = new LabelTag (l);
			this.initializeMenu (label);
			label.addPropertyChangeListener(new PropertyChanged() );
			pnllabelcontain.add(label);
		}
		
		pnllabelcontain.revalidate();
	}
	
	@Override
	public void onLabelChange(LabelCache cache) {
		initializeTags ();
		
	}
	
	private void createEvetns() 
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
	private LabelCache cache;
	private JPopupMenu Pmenu ;

	private JTextField tfldsearchkw;

	private JButton btnkaddtocur;

	private JMenuItem menuItemAddToCur;
	private JPopupMenu selfMenu;
	private JMenuItem menuItemAddNew;
	private JMenuItem menuItemReset;
	
	public void createGui ()
	{
		Color color = ColorScheme.BACKGROUND;
        this.setLayout(new BorderLayout(0,0));
 
        JPanel pnlup = new JPanel ();
		pnlup.setLayout (new FlowLayout(FlowLayout.LEFT) );
		JLabel lblbkkw = new JLabel(this.title);
		JLabel lblbkfengge = new JLabel("  ");
		tfldsearchkw = new JTextField ("                 ");
		
		pnlup.add(lblbkkw);
		pnlup.add(lblbkfengge);
		pnlup.add(tfldsearchkw);
        
		JScrollPane sclpcenter = new JScrollPane();
		pnllabelcontain = new JPanel();
		pnllabelcontain.setLayout(new WrapLayout(WrapLayout.CENTER, 5, 5));
		pnllabelcontain.setBackground(color);
	    sclpcenter.setViewportView (pnllabelcontain);
	    
	    
	    if(!this.displaymode.equals(LabelsManagement.HIDEHEADERMODE ))
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
					
					Collection<Tag> tagslist = cache.produceSelectedLabels();

					PropertyChangeEvent evtzd = new PropertyChangeEvent(this, LabelsManagement.ADDNEWTAGSTONODE , "", tagslist );
		            pcs.firePropertyChange(evtzd);
				}
				
			});
			
			if( this.controlmode.equals(LabelsManagement.FULLCONTROLMODE ) ) {
				Pmenu.add(menuItemAddToCur,0);
			} else if ( this.controlmode.equals(LabelsManagement.PARTCONTROLMODE)  ) {
				
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
            	}
            }
	}
	 
	private void combinMenuAction() 
	{
		CombineTagsDialog combineTagDialog = new CombineTagsDialog (lbdbservice);
		combineTagDialog.setLabel(new Tag("Combined label", ColorScheme.GREY_WHITER));
		combineTagDialog.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
		combineTagDialog.setVisible(true);
        
        PropertyChangeEvent evtzd = new PropertyChangeEvent(this, LabelsManagement.NODESBEENEDIT , "", combineTagDialog.getLabel () );
        pcs.firePropertyChange(evtzd);
	}
	private void addMenuAction ()
	{
		CreateTagDialog createTagDialog = new CreateTagDialog (lbdbservice);
        createTagDialog.setLabel(new Tag("Create label", ColorScheme.GREY_WHITER));
        createTagDialog.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
        createTagDialog.setVisible(true);
        
        PropertyChangeEvent evtzd = new PropertyChangeEvent(this, LabelsManagement.NODEADDNEWTAGSNEEDSYSUPDAT , "", createTagDialog.getLabel () );
        pcs.firePropertyChange(evtzd);
	}
	private void editMenuAction ()
	{
		 JLabel menulbl  = (JLabel) Pmenu.getInvoker();
	   	 String clickname = menulbl.getName();
	   	 Collection<Tag> tagslist = cache.produceSelectedLabels();
	   	 for (Iterator<Tag> lit = tagslist.iterator(); lit.hasNext(); ) {
		        Tag f = lit.next();
		        if(f.getName().equals(clickname) ) {
		        	ModifyTagDialog mdl = new ModifyTagDialog (lbdbservice);
		        	mdl.setLabel(f);
		        	mdl.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
		            mdl.setVisible(true);
		            
		            PropertyChangeEvent evtzd = new PropertyChangeEvent(this, LabelsManagement.NODESBEENEDIT , "", mdl.getLabel () );
		            pcs.firePropertyChange(evtzd);
		        }
	   	 }
	}
	private void deleteMenuAction ()
	{
		String warningmsg = "";
		if(lbdbservice instanceof DBSystemTagsService) {
			warningmsg = "警告:确定删除? 删除后无法恢复!";
		} else
			warningmsg = "警告:确定从当前移除?";
		int exchangeresult = JOptionPane.showConfirmDialog(null,warningmsg , "询问?", JOptionPane.OK_CANCEL_OPTION);
		if(exchangeresult == JOptionPane.CANCEL_OPTION)
				return;
		
		Collection<Tag> tagslist = cache.produceSelectedLabels();
		try {
			lbdbservice.deleteLabels(tagslist);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		PropertyChangeEvent evtzd = new PropertyChangeEvent(this, LabelsManagement.NODESBEENDELETED , "", tagslist );
        pcs.firePropertyChange(evtzd);
	
	}
	protected void resetAction() 
	{
		Collection<Tag> curlabl = cache.produceLabels();
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
		if(text.trim().isEmpty())
			text = "abcdefghigklmnopqrstuvwxyz";
		
		Component[] pnltags = pnllabelcontain.getComponents();
		for( Component tmpc :  pnltags) {
        	Tag l = ((LabelTag)tmpc).getTag();
        	Boolean chkresult = l.checkHanYuPinYing (text.trim() );
        	if(chkresult) {
        		LineBorder line = new LineBorder(Color.MAGENTA, 2, true);
            	((JPanel)tmpc).setBorder(line);
        	}
        }
	}

}

