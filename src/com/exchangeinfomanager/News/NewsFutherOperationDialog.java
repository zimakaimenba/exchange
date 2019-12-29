package com.exchangeinfomanager.News;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.exchangeinfomanager.Services.ServicesForNews;
import com.exchangeinfomanager.Tag.Tag;
import com.exchangeinfomanager.TagLabel.TagsPanel;
import com.exchangeinfomanager.TagManagment.TagSearchOnNewsTableModel;
import com.exchangeinfomanager.TagManagment.TagSearchOnNodesTableModel;
import com.exchangeinfomanager.TagServices.CacheForInsertedTag;
import com.exchangeinfomanager.TagServices.TagsServiceForSystemTags;
import com.exchangeinfomanager.commonlib.JUpdatedTextField;
import com.exchangeinfomanager.commonlib.TableCellListener;
import com.exchangeinfomanager.guifactory.JLabelFactory;
import com.exchangeinfomanager.guifactory.JPanelFactory;
import com.exchangeinfomanager.guifactory.JTextFactory;

public class NewsFutherOperationDialog extends NewsDialog<News> 
{


//	protected static final int WIDTH = 350;
//    protected static final int HEIGHT = 500;
//    protected static final int PADDING = 20;
    protected static final int TITLE_SIZE = 40;
    protected static final int TITLE_FONT_SIZE = 20;
    
	protected JPanel eastPanel;
	protected JPanel nodesandnewsPanel;
	protected JTable tblofnodes;
	protected JTable tblnews;
//	private TagsPanel pnldisplayallsyskw;
//	private CacheForInsertedTag syscache;
//	protected TagsServiceForSystemTags sysservice;
//	protected JUpdatedTextField tfldmusthavetags;
//	protected JUpdatedTextField tfldoneoftags;


	/**
	 * Create the panel.
	 */
	public NewsFutherOperationDialog(ServicesForNews NewsService)
	{
		super(NewsService);
//	    super.setTitle("Create");
	    
	    createGui ();
		
		createEvents ();
		
//		sysservice = new TagsServiceForSystemTags ();
//		syscache = new CacheForInsertedTag (sysservice);
//		sysservice.setCache(syscache);
	}

	private void createEvents() 
	{
		
	}

//	private Boolean setNews (News event)
//	{
//		Boolean result = super.setNews(event);
//		
//		event.getNewsOwnerCodes();
//		
//		
//	}
	private void createGui()
	{
		this.setLayout(new BorderLayout () );
		this.eastPanel = JPanelFactory.createFixedSizePanel(NewsDialog.NEWSWIDTH, NewsDialog.NEWSHEIGHT+20, PADDING);
	    this.eastPanel.setLayout(new BoxLayout(this.eastPanel, BoxLayout.PAGE_AXIS));
//		this.eastPanel.setLayout(new BorderLayout () );

	    this.add(this.eastPanel, BorderLayout.WEST);
	    this.eastPanel.add(super.newCenterPanel); //∞—news dialogº”»Î

	    createNodesAndNewsPanel ();
	    
        super.setModalityType(ModalityType.APPLICATION_MODAL);
        super.setSize(new Dimension(WHOLESIZEWIDTH, WHOLESIZEHIGHT));
        super.setResizable(true);

	}

	private void createNodesAndNewsPanel() 
	{
		this.nodesandnewsPanel = JPanelFactory.createPanel();
	    this.add(nodesandnewsPanel, BorderLayout.CENTER);
	    nodesandnewsPanel.setLayout(new BoxLayout(nodesandnewsPanel, BoxLayout.Y_AXIS));
	    
		JScrollPane nodepane = new JScrollPane ();
        nodepane.setAutoscrolls(true);
        TagSearchOnNodesTableModel nodetablemodel = new TagSearchOnNodesTableModel ();
        tblofnodes = new JTable (nodetablemodel);
        tblofnodes.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(35);
        tblofnodes.getTableHeader().getColumnModel().getColumn(0).setMinWidth(35);
        tblofnodes.getTableHeader().getColumnModel().getColumn(0).setWidth(35);
        tblofnodes.getTableHeader().getColumnModel().getColumn(0).setPreferredWidth(35);
//        tblofnodes.getTableHeader().getColumnModel().getColumn(2).setMaxWidth(90);
//        tblofnodes.getTableHeader().getColumnModel().getColumn(2).setMinWidth(90);
//        tblofnodes.getTableHeader().getColumnModel().getColumn(2).setWidth(90);
//        tblofnodes.getTableHeader().getColumnModel().getColumn(2).setPreferredWidth(90);
        TableRowSorter<TableModel> sorterfortblofnodes = new TableRowSorter<TableModel>(tblofnodes.getModel());
        tblofnodes.setRowSorter(sorterfortblofnodes);
        nodepane.setViewportView(tblofnodes);
        nodesandnewsPanel.add(nodepane);
        
        JScrollPane newspane = new JScrollPane ();
        newspane.setAutoscrolls(true);
        TagSearchOnNewsTableModel newstablemodel = new TagSearchOnNewsTableModel ();
        tblnews = new JTable (newstablemodel);
        tblnews.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(35);
		tblnews.getTableHeader().getColumnModel().getColumn(0).setMinWidth(35);
		tblnews.getTableHeader().getColumnModel().getColumn(0).setWidth(35);
		tblnews.getTableHeader().getColumnModel().getColumn(0).setPreferredWidth(35);
		tblnews.getTableHeader().getColumnModel().getColumn(2).setMaxWidth(90);
		tblnews.getTableHeader().getColumnModel().getColumn(2).setMinWidth(90);
		tblnews.getTableHeader().getColumnModel().getColumn(2).setWidth(90);
		tblnews.getTableHeader().getColumnModel().getColumn(2).setPreferredWidth(90);
		TableRowSorter<TableModel> sorterfortblnews = new TableRowSorter<TableModel>(tblnews.getModel());
		tblnews.setRowSorter(sorterfortblnews);
		newspane.setViewportView(tblnews);
        nodesandnewsPanel.add(newspane);
        
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
}
