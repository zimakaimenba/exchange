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
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;


import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.exchangeinfomanager.StockCalendar.ColorScheme;

import com.exchangeinfomanager.commonlib.WrapLayout;

import com.exchangeinfomanager.labelmanagement.DBSystemTagsService;
import com.exchangeinfomanager.labelmanagement.TagCache;
import com.exchangeinfomanager.labelmanagement.TagCacheListener;
import com.exchangeinfomanager.labelmanagement.TagService;
import com.exchangeinfomanager.labelmanagement.Tag.CombineTagsDialog;
import com.exchangeinfomanager.labelmanagement.Tag.CreateTagDialog;
import com.exchangeinfomanager.labelmanagement.Tag.InsertedTag;
import com.exchangeinfomanager.labelmanagement.Tag.ModifyTagDialog;
import com.exchangeinfomanager.labelmanagement.Tag.Tag;



import javax.swing.JButton;
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
	}
	public TagsPanel(String title)
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
	
	public void initializeTagsPanel(TagService lbdbservice, TagCache cache) 
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
		Collection<Tag> alltags = cache.produceTags();
		
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
	public void onTagChange(TagCache cache) {
		cache.refreshTags();
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
	private TagCache cache;
	private JPopupMenu Pmenu ;

	private JTextField tfldsearchkw;

	private JButton btnkaddtocur;

	private JMenuItem menuItemAddToCur;
	private JPopupMenu selfMenu;
	private JMenuItem menuItemAddNew;
	private JMenuItem menuItemReset;
	
	public void createGui ()
	{
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
			} else if ( this.controlmode.equals(TagsPanel.PARTCONTROLMODE)  ) {
				
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
		combineTagDialog.setLabel(new Tag("     ", ColorScheme.GREY_WHITER));
		combineTagDialog.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
		combineTagDialog.setVisible(true);
        
        PropertyChangeEvent evtzd = new PropertyChangeEvent(this, TagsPanel.NODESBEENEDIT , "", combineTagDialog.getLabel () );
        pcs.firePropertyChange(evtzd);
	}
	private void addMenuAction ()
	{
		CreateTagDialog createTagDialog = new CreateTagDialog (lbdbservice);
        createTagDialog.setLabel(new Tag("     ", ColorScheme.GREY_WHITER));
        createTagDialog.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
        createTagDialog.setVisible(true);
        
        PropertyChangeEvent evtzd = new PropertyChangeEvent(this, TagsPanel.NODEADDNEWTAGSNEEDSYSUPDAT , "", createTagDialog.getLabel () );
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
		            
	            PropertyChangeEvent evtzd = new PropertyChangeEvent(this, TagsPanel.NODESBEENEDIT , "", mdl.getLabel () );
	            pcs.firePropertyChange(evtzd);
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
		
		Collection<Tag> tagslist = cache.produceSelectedTags();
		try {
			lbdbservice.deleteTags(tagslist);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		PropertyChangeEvent evtzd = new PropertyChangeEvent(this, TagsPanel.NODESBEENDELETED , "", tagslist );
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
		if(text.trim().isEmpty())
			text = "abcdefghigklmnopqrstuvwxyz";
		
		Component[] pnltags = pnllabelcontain.getComponents();
		for( Component tmpc :  pnltags) {
        	Tag l = ((LabelTag)tmpc).getTag();
        	Boolean chkresult = l.checkHanYuPinYing (text.trim() );
        	if(chkresult) {
        		LineBorder line = new LineBorder(Color.MAGENTA, 2, true);
            	((JPanel)tmpc).setBorder(line);
        	} else {
        		LineBorder line = new LineBorder(l.getColor(), 2, true);
            	((JPanel)tmpc).setBorder(line);
        	}
        }
	}

}

