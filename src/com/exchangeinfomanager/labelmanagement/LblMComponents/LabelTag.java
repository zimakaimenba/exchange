package com.exchangeinfomanager.labelmanagement.LblMComponents;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import com.exchangeinfomanager.StockCalendar.ColorScheme;
import com.exchangeinfomanager.StockCalendar.JUpdatedLabel;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.JPanelFactory;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.labelmanagement.Tag.InsertedTag;
import com.exchangeinfomanager.labelmanagement.Tag.NodeInsertedTag;
import com.exchangeinfomanager.labelmanagement.Tag.Tag;



public class LabelTag extends JPanel
{
	protected static final String PROPERTYCHANGEDASADD = "PROPERTYCHANGED_AS_ADD";
	protected static final String PROPERTYCHANGEDASDELETE = "PROPERTYCHANGED_AS_DELETE";
	protected static final String PROPERTYCHANGEDASEDIT = "PROPERTYCHANGED_AS_EDIT";
	protected static final String PROPERTYCHANGEDASCOMBINE = "PROPERTYCHANGED_AS_COMBINED";
	protected static final String PROPERTYCHANGEDASSEARCH = "SEARCH_ON_INTERNET";
	private Color tagcolor;
//	private static Border EMPTY_BORDER = BorderFactory.createEmptyBorder();
//	private static LayoutManager FLOW_LAYOUT = new FlowLayout(FlowLayout.LEFT, 0, 0);
//	private static Color BG_COLOR = ColorScheme.BACKGROUND;
//	private static Color FG_COLOR = ColorScheme.BLACK_FONT;
	
	public LabelTag (Tag l)
	{
		this.tag =  l;

		if(l instanceof NodeInsertedTag)
			this.tagcolor = ((NodeInsertedTag)l).getNodeMachColor();
		else
			this.tagcolor = l.getColor() ;
		
		
		
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		this.setBorder(BorderFactory.createEmptyBorder());
		this.setName( l.getName() );

		JUpdatedLabel label = new JUpdatedLabel( l.getName() );
		label.addMouseListener(new ChangeKeyWordsPanelController());
	    label.setOpaque(true);
	    label.setName( l.getName() );
	    label.setToolTipText(l.getName() );
	    label.setForeground(Color.BLACK);
	    label.setBackground(this.tagcolor);  //
	    
	    if(  tag.isSelected() ) {
	    	LineBorder line = new LineBorder(Color.BLUE, 2, true);
	 	    this.setBorder(line);
	    } else {
	    	LineBorder line = new LineBorder( this.tagcolor, 2, true);
	 	    this.setBorder(line);
	    }
	    
	    
	    JLabel menulbl = new JLabel (new ImageIcon(LabelTag.class.getResource("/images/menu16.png")));
	    menulbl.setName( l.getName() );
	    menulbl.addMouseListener( new DisPlayMenuController () );
	    
	    this.add(label);
	    this.add(menulbl);
	    
	    initializeMenu ();
	    createEvents ();
	}
	
	public Tag getTag ()
	{
		return this.tag;
	}
	private void createEvents()
	{
		menuItemEdit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				

				PropertyChangeEvent evtzd = new PropertyChangeEvent(this, LabelTag.PROPERTYCHANGEDASEDIT , "", "EDIT" );
	            pcs.firePropertyChange(evtzd);
			}
			
		});
		
		menuItemDel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				

				PropertyChangeEvent evtzd = new PropertyChangeEvent(this, LabelTag.PROPERTYCHANGEDASDELETE , "", "DELETE" );
	            pcs.firePropertyChange(evtzd);
			}
			
		});
		
		menuItemAdd.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				PropertyChangeEvent evtzd = new PropertyChangeEvent(this, LabelTag.PROPERTYCHANGEDASADD , "", "ADD"  );
	            pcs.firePropertyChange(evtzd);
			}
		});
		
		menuItemCombined.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				PropertyChangeEvent evtzd = new PropertyChangeEvent(this, LabelTag.PROPERTYCHANGEDASCOMBINE , "", "COMBINE"  );
	            pcs.firePropertyChange(evtzd);
			}
		});
		menuItemSearchOnNet.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				PropertyChangeEvent evtzd = new PropertyChangeEvent(this, LabelTag.PROPERTYCHANGEDASSEARCH , "", "SEARCH"  );
	            pcs.firePropertyChange(evtzd);
			}
		});
		
	}

	private Tag tag;
	private JPopupMenu Pmenu;
	private JMenuItem menuItemEdit;
	private JMenuItem menuItemDel;
	private JMenuItem menuItemAdd;
	private JMenuItem menuItemCombined;
	
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this); //	https://stackoverflow.com/questions/4690892/passing-a-value-between-components/4691447#4691447
	private JMenuItem menuItemSearchOnNet;
	public void addPropertyChangeListener(PropertyChangeListener listener) {
	        pcs.addPropertyChangeListener(listener);
	}
	
	public JPopupMenu getMenu ()
	{
		return this.Pmenu;
	}
	 private void initializeMenu() 
	 {
			Pmenu = new JPopupMenu("test");   
			menuItemEdit = new JMenuItem("修改");
			menuItemDel = new JMenuItem("删除");
			menuItemCombined = new JMenuItem("合并");
			menuItemAdd = new JMenuItem("新增");
			menuItemSearchOnNet = new JMenuItem("网络查询"); 
			
			Pmenu.add(menuItemEdit);
			Pmenu.add(menuItemDel);
			Pmenu.add(menuItemCombined);
			Pmenu.add(menuItemAdd);
			Pmenu.add(menuItemSearchOnNet);
	 }
	
	private class ChangeKeyWordsPanelController extends MouseAdapter 
	{
	        @Override
	        public void mouseClicked(MouseEvent e) {
	        	 super.mouseClicked(e);
	        	 JLabel label = null;
	        	 try {
	        		 label = (JLabel) e.getSource();
	        	 } catch ( java.lang.ClassCastException exc) {
	        		 
	        	 }
	        	 try {
     	    		if(  tag.isSelected() )
     	    			unselectTag (tag, (JPanel)label.getParent());
     	    		else
     	    			selectTag (tag,  (JPanel)label.getParent());
	        	 } catch (java.lang.NullPointerException ex) {
	        		 
	        	 }
	        }
	        
	        
	}
	
	private void unselectTag(Tag tmptag, JPanel p)
    {
    	((InsertedTag)tmptag).setSelected(false);
        LineBorder line = new LineBorder( this.tagcolor, 2, true);
 	    p.setBorder(line);
    }
    private void selectTag (Tag tmptag, JPanel p)
    {
    	((InsertedTag)tmptag).setSelected(true);
        LineBorder line = new LineBorder(Color.BLUE, 2, true);
 	    p.setBorder(line);
    }
	 
	
	private class DisPlayMenuController extends MouseAdapter 
	{
		 @Override //https://stackoverflow.com/questions/11605426/popup-on-left-click
	     public void mouseClicked(MouseEvent evt) {
	       	 super.mouseClicked(evt);
	       	 Component source = (Component)evt.getSource();
	       	 JPanel p = (JPanel) source.getParent() ;
	       	 String labelname = p.getName();
	   	
	   		 tag.setSelected(true);
	   		 LineBorder line = new LineBorder(Color.BLUE, 2, true);
	   		 p.setBorder(line);
	       	 
	       	 Point location = source.getLocation();
	       	 Dimension size = source.getSize();
	
	       	 int xPos = location.x ;
	       	 int yPos = location.y ;
	       	 Pmenu.show(source, xPos, yPos);
		 }
	}
	
	
	

}
