package com.exchangeinfomanager.TagManagment;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.News.InsertedNews;
import com.exchangeinfomanager.News.News;
import com.exchangeinfomanager.Tag.Tag;
import com.exchangeinfomanager.Tag.TagService;
import com.exchangeinfomanager.TagLabel.TagsPanel;
import com.exchangeinfomanager.TagServices.CacheForInsertedTag;
import com.exchangeinfomanager.TagServices.TagsServiceForNodes;
import com.exchangeinfomanager.TagServices.TagsServiceForSystemTags;
import com.exchangeinfomanager.commonlib.JUpdatedTextField;
import com.exchangeinfomanager.commonlib.TableCellListener;
import com.exchangeinfomanager.guifactory.JLabelFactory;
import com.exchangeinfomanager.guifactory.JPanelFactory;
import com.exchangeinfomanager.guifactory.JTextFactory;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class TagSearchMatrixPanel extends JPanel 
{

	protected static final int WIDTH = 400;
    protected static final int HEIGHT = 600;
    protected static final int PADDING = 20;
    protected static final int TITLE_SIZE = 40;
    protected static final int TITLE_FONT_SIZE = 20;
    
	protected JPanel eastPanel;
	protected JPanel centerPanel;
	protected JTable tblofnodes;
	protected JTable tblnews;
	private TagsPanel pnldisplayallsyskw;
	private CacheForInsertedTag syscache;
	protected TagsServiceForSystemTags sysservice;
	protected JUpdatedTextField tfldmusthavetags;
	protected JUpdatedTextField tfldoneoftags;
	protected JCheckBox chkbxsearchgntsfmxx;

	/**
	 * Create the panel.
	 */
	public TagSearchMatrixPanel()
	{
		createGui ();
		
		createEvents ();
		
		sysservice = new TagsServiceForSystemTags ();
		syscache = new CacheForInsertedTag (sysservice);
		sysservice.setCache(syscache);
		
		pnldisplayallsyskw.initializeTagsPanel (sysservice,syscache);
	}
	public void setPreSearchMustHaveTags (Collection<Tag> musthavetags)
	{
		if(musthavetags == null) {
			tfldmusthavetags.setText("");
			return ;
		}
//		Joiner.on(" ").skipNulls().join(set_1);
		for(Tag tmpsltlbs : musthavetags) {
			String tagname = tmpsltlbs.getName();
			if ( ! tfldmusthavetags.getText().contains(tagname) )
				tfldmusthavetags.setText( tfldmusthavetags.getText() + tagname + "  ");
		}
	}
	public void setPreSearchOrHaveTags (Collection<Tag> musthavetags)
	{
		if(musthavetags == null) {
			tfldoneoftags.setText("");
			return;
		}
//		Joiner.on(" ").skipNulls().join(set_1);
		for(Tag tmpsltlbs : musthavetags) {
			String tagname = tmpsltlbs.getName();
			if ( ! tfldoneoftags.getText().contains(tagname) )
				tfldoneoftags.setText( tfldoneoftags.getText() + tagname + "  ");
		}
	}
	/*
	 * 
	 */
	public void setPreSearchOrHaveTags (String orhavetags)
	{
		tfldoneoftags.setText( tfldoneoftags.getText() + " " + orhavetags + "  ");
	}
	/*
	 * 
	 */
	public boolean shouldSearchGntsFmxx ()
	{
		if(chkbxsearchgntsfmxx.isSelected() )
			return true;
		else 
			return false;
	}
	/*
	 * 
	 */
	private void createEvents() 
	{
		pnldisplayallsyskw.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
            	if (evt.getPropertyName().equals(TagsPanel.ADDNEWTAGSTONODE)) {

    				Collection<Tag> sltlbs = syscache.produceSelectedTags();
    				setPreSearchMustHaveTags (sltlbs);
            	}
            }
		});
	}

	private void createGui()
	{
		this.setLayout(new BorderLayout () );
		this.eastPanel = JPanelFactory.createFixedSizePanel(WIDTH, HEIGHT, PADDING);
	    this.eastPanel.setLayout(new BoxLayout(this.eastPanel, BoxLayout.PAGE_AXIS));

	    this.add(eastPanel, BorderLayout.WEST);
	    createEastSeachPanel ();
	    
	    this.centerPanel = JPanelFactory.createPanel();
	    this.add(centerPanel, BorderLayout.CENTER);
	    centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
	    createCenterSeachPanel ();
		
	}

	private void createCenterSeachPanel() 
	{
		JScrollPane nodepane = new JScrollPane ();
        nodepane.setAutoscrolls(true);
        TagSearchOnNodesTableModel nodetablemodel = new TagSearchOnNodesTableModel ();
        tblofnodes = new JTable (nodetablemodel);
        nodepane.setViewportView(tblofnodes);
        
        centerPanel.add(nodepane);
        
        JScrollPane newspane = new JScrollPane ();
        newspane.setAutoscrolls(true);
        TagSearchOnNewsTableModel newstablemodel = new TagSearchOnNewsTableModel ();
        tblnews = new JTable (newstablemodel);
        newspane.setViewportView(tblnews);
        
        centerPanel.add(newspane);
        
        Action action = new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
                TableCellListener tcl = (TableCellListener)e.getSource();
//                System.out.println("Row   : " + tcl.getRow());
//                System.out.println("Column: " + tcl.getColumn());
//                System.out.println("Old   : " + tcl.getOldValue());
//                System.out.println("New   : " + tcl.getNewValue());
            }
        };

        TableCellListener tcl = new TableCellListener(tblnews, action);
	}
	/*
	 * 
	 */
	public void createEastSeachPanel ()
	{
		pnldisplayallsyskw = new TagsPanel("所有系统关键字",null, TagsPanel.FULLCONTROLMODE);
        
		JScrollPane despane = new JScrollPane ();
        despane.setAutoscrolls(true);
        despane.setViewportView(pnldisplayallsyskw);
        
        eastPanel.add(despane);
        eastPanel.add(Box.createVerticalStrut(10));
        
        eastPanel.add(JLabelFactory.createLabel("Must contain all of these Tags"));
        
        tfldmusthavetags = (JUpdatedTextField) JTextFactory.createTextField(TITLE_SIZE, TITLE_SIZE, TITLE_FONT_SIZE);
        tfldmusthavetags.setMouseLeftClearEnabled(false);
    	eastPanel.add(tfldmusthavetags);
    	
    	eastPanel.add(JLabelFactory.createLabel("At least one of these Tags"));
    	
    	tfldoneoftags = (JUpdatedTextField) JTextFactory.createTextField(TITLE_SIZE, TITLE_SIZE, TITLE_FONT_SIZE);
    	tfldoneoftags.setMouseLeftClearEnabled(false);
    	eastPanel.add(tfldoneoftags);
    	
    	eastPanel.add(JLabelFactory.createLabel("None of these Tags"));
    	
    	JUpdatedTextField tfldnoneoftags = (JUpdatedTextField) JTextFactory.createTextField(TITLE_SIZE, TITLE_SIZE, TITLE_FONT_SIZE);
    	tfldnoneoftags.setMouseLeftClearEnabled(false);
    	eastPanel.add(tfldnoneoftags);
    	
    	eastPanel.add(JLabelFactory.createLabel("Searching Fields:"));
    	
    	JUpdatedTextField tflsearchfield = (JUpdatedTextField) JTextFactory.createTextField(TITLE_SIZE, TITLE_SIZE, TITLE_FONT_SIZE);
    	tflsearchfield.setMouseLeftClearEnabled(false);
    	eastPanel.add(tflsearchfield);
    	
    	eastPanel.add(Box.createVerticalStrut(10));
    	
    	chkbxsearchgntsfmxx = new JCheckBox ("同时在概念提示和负面消息中搜索相同关键字。");
    	chkbxsearchgntsfmxx.setSelected(true);
    	eastPanel.add(chkbxsearchgntsfmxx);

	}


}


