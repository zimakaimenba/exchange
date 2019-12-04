package com.exchangeinfomanager.TagManagment;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.exchangeinfomanager.Services.TagService;
import com.exchangeinfomanager.Tag.Tag;
import com.exchangeinfomanager.TagLabel.TagsPanel;
import com.exchangeinfomanager.TagServices.CacheForInsertedTag;
import com.exchangeinfomanager.TagServices.TagsServiceForNodes;
import com.exchangeinfomanager.TagServices.TagsServiceForSystemTags;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.InsertedMeeting;
import com.exchangeinfomanager.commonlib.JUpdatedTextField;
import com.exchangeinfomanager.commonlib.TableCellListener;
import com.exchangeinfomanager.guifactory.JLabelFactory;
import com.exchangeinfomanager.guifactory.JPanelFactory;
import com.exchangeinfomanager.guifactory.JTextFactory;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

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
	public void setPreSearchOrHaveTags (String orhavetags)
	{
		tfldoneoftags.setText( tfldoneoftags.getText() + " " + orhavetags + "  ");
	}
	
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
                System.out.println("Row   : " + tcl.getRow());
                System.out.println("Column: " + tcl.getColumn());
                System.out.println("Old   : " + tcl.getOldValue());
                System.out.println("New   : " + tcl.getNewValue());
            }
        };

        TableCellListener tcl = new TableCellListener(tblnews, action);
		
	}

	public void createEastSeachPanel ()
	{
		pnldisplayallsyskw = new TagsPanel("所有系统关键字",null, TagsPanel.FULLCONTROLMODE);;
        
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

	}


}

class TagSearchOnNodesTableModel extends DefaultTableModel 
{
	private List<BkChanYeLianTreeNode> info;
	String[] jtableTitleStrings = { "选择", "关键词","代码","名称"};
	private Set<Integer> selectedrowindex ;
	
	public TagSearchOnNodesTableModel ()
	{
		selectedrowindex = new HashSet<> ();
	}
	
	public void refresh (Collection<BkChanYeLianTreeNode> searchresult)
	{
		if (searchresult instanceof List)
		  info = (List)searchresult;
		else
		  info = new ArrayList(searchresult);
		
		selectedrowindex.clear();
		
		fireTableDataChanged();
	}
	
	public Collection<BkChanYeLianTreeNode> getSelectedNodes ()
	{
		Collection<BkChanYeLianTreeNode> selectednode = new HashSet<> ();
		for(int i= 0; i<this.getRowCount() ; i++) {
			Boolean result = (Boolean) this.getValueAt(i, 0);
			if(result) {
				selectednode.add(this.info.get(i));
			}
		}
		
		return selectednode;
	}
	
	    @Override
	    public int getColumnCount() 
	    {
	        return jtableTitleStrings.length;
	    } 
	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	if(this.info.isEmpty())
	    		return null;
	    	
	    	BkChanYeLianTreeNode node = info.get(rowIndex);
	    	TagsServiceForNodes tagsservicesfornode = new TagsServiceForNodes (node);
	    	Collection<Tag> nodetags = null;
			try {
				nodetags = tagsservicesfornode.getTags();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	String tagslist = " ";
	    	for (Iterator<Tag> it = nodetags.iterator(); it.hasNext(); ) {
	    		Tag t = it.next();
	    		tagslist = tagslist + t.getName() + "  ";
	    	}
	    	
	    	Object value = "??";
	    	switch (columnIndex) {
            case 0:
                value = !selectedrowindex.contains(Integer.valueOf(rowIndex) );
                break;
            case 1:
            	value = tagslist;
            	break;
            case 2:
            	value = node.getMyOwnCode();
                break;
            case 3:
            	value = node.getMyOwnName();
                break;

	    	}

	    	return value;
	    }

     public Class<?> getColumnClass(int columnIndex) {
		      Class clazz = String.class;
		      switch (columnIndex) {
		      case 0:
		    	  clazz = Boolean.class;
		    	  break;
		        case 1:
			          clazz = String.class;
			          break;
		        case 2:
			          clazz = String.class;
			          break;
		        case 3:
			          clazz = String.class;
			          break;
		      }
		      
		      return clazz;
		}
	    
	    public String getColumnName(int column){ 
	    	return jtableTitleStrings[column];
	    }//设置表格列名 
		

	    public boolean isCellEditable(int row,int column) 
	    {
	    	return  column == 0; 
		}
	    
	    public int getRowCount() 
		{
			 if(this.info == null)
				 return 0;
			 else if(this.info.isEmpty()  )
				 return 0;
			 else
				 return this.info.size();
		}
	    
	    public void setValueAt(Object value, int row, int column)
	    {
	    	if(column == 0 && (Boolean)value == false)
	    		selectedrowindex.add(row);
	    	else if(column == 0 && (Boolean)value == true)
	    		selectedrowindex.remove(row);
	     }

}


class TagSearchOnNewsTableModel extends DefaultTableModel 
{
	private List<InsertedMeeting> info;
	String[] jtableTitleStrings = { "选择","关键词", "新闻","详细信息"};
	private Set<Integer> selectedrowindex ;
	
	public TagSearchOnNewsTableModel ()
	{
		selectedrowindex = new HashSet<> ();
	}
	
	public void refresh (Collection<InsertedMeeting> searchresult)
	{
		if (searchresult instanceof List)
		  info = (List)searchresult;
		else
		  info = new ArrayList(searchresult);
		
		selectedrowindex.clear();
		
		fireTableDataChanged();
	}
	
	    @Override
	    public int getColumnCount() 
	    {
	        return jtableTitleStrings.length;
	    } 
	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	if(this.info.isEmpty())
	    		return null;
	    	
	    	InsertedMeeting news = info.get(rowIndex);
	    	
	    	Object value = "??";
	    	switch (columnIndex) {
            case 0:
                value = !selectedrowindex.contains(Integer.valueOf(rowIndex) );
                break;
            case 1:
            	value = news.getKeyWords();
            	break;
            case 2:
            	value = news.getTitle();
                break;
            case 3:
            	value = news.getDescription();
                break;

	    	}

	    	return value;
	    }

     public Class<?> getColumnClass(int columnIndex) {
		      Class clazz = String.class;
		      switch (columnIndex) {
		      case 0:
		    	  clazz = Boolean.class;
		    	  break;
		        case 1:
			          clazz = String.class;
			          break;
		        case 2:
			          clazz = String.class;
			          break;
		        case 3:
			          clazz = String.class;
			          break;
		      }
		      
		      return clazz;
		}
	    
	    public String getColumnName(int column){ 
	    	return jtableTitleStrings[column];
	    }//设置表格列名 
		

	    public boolean isCellEditable(int row,int column) 
	    {
	    	return  column == 0; 
		}
	    
	    public int getRowCount() 
		{
			 if(this.info == null)
				 return 0;
			 else if(this.info.isEmpty()  )
				 return 0;
			 else
				 return this.info.size();
		}
	    
	    public void setValueAt(Object value, int row, int column)
	    {
	    	if(column == 0 && (Boolean)value == false)
	    		selectedrowindex.add(row);
	    	else if(column == 0 && (Boolean)value == true)
	    		selectedrowindex.remove(row);
	     }

}
