package com.exchangeinfomanager.StockCalendar;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import com.exchangeinfomanager.StockCalendar.AddNewsToSameTagsNodes.NodeKeyWordsMap;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountInfoBasic;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.StockChiCangInfo;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Cache;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.CacheListener;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.DBMeetingService;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.InsertedMeeting;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Meeting;
import com.exchangeinfomanager.labelmanagement.DBNodesTagsService;
import com.exchangeinfomanager.labelmanagement.DBSystemTagsService;
import com.exchangeinfomanager.labelmanagement.TagService;
import com.exchangeinfomanager.labelmanagement.Tag.Tag;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.operations.AllCurrentTdxBKAndStoksTree;
import com.google.common.base.Splitter;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

public class AddNewsToSameTagsNodes extends JDialog implements CacheListener
{
	private final JPanel contentPanel = new JPanel();
	private JTable tblNodesLists;
	private Meeting news;
	private TagService dbserviceforsys;
	private AllCurrentTdxBKAndStoksTree allbkstk;
	private JButton btnupdatetonode;

	/**
	 * Create the dialog.
	 */
	public AddNewsToSameTagsNodes(Cache cache) 
	{
		dbserviceforsys = new DBSystemTagsService ();
		allbkstk = AllCurrentTdxBKAndStoksTree.getInstance();
		
		super.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
		
		initializeGui ();
		createdEvents ();
		
		cache.addCacheListener(this);
	}
	
	private void createdEvents() 
	{
		btnupdatetonode.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				attachKeywordsToNodes ();
				
				
			}
		});
	}

	protected void attachKeywordsToNodes() 
	{
		List<NodeKeyWordsMap> sltresult = ((NewsKeyAndNodeMapTableModel)tblNodesLists.getModel () ).getUserSelectResult ();
		
		Set<BkChanYeLianTreeNode> bkstk = new HashSet<> ();
		DBMeetingService lbnodedbservice = new DBMeetingService ();
		
		for(NodeKeyWordsMap tmpmap : sltresult  ) {
			if(tmpmap.isSelected()) {
				BkChanYeLianTreeNode node = tmpmap.getNode();
				this.news.addMeetingToSpecificOwner(node.getMyOwnCode());
			}
		}
		try {
			lbnodedbservice.updateMeeting((InsertedMeeting) news);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.setVisible(false);
	}

	private Boolean  inializeNewsTable ()
	{
		List<AddNewsToSameTagsNodes.NodeKeyWordsMap> info = new ArrayList<> ();
		
		String keywords = news.getKeyWords();
		if(keywords.isBlank())
			return false;
		
		List<String> kwlist = Splitter.on(" ").omitEmptyStrings().splitToList(keywords);
		for(String kw : kwlist ) {
			Map<String, Integer> result = ((DBSystemTagsService)dbserviceforsys).getNodesSetOfSpecificTag(kw);

			for (Entry<String, Integer> entry : result.entrySet()) {
				String nodecode = entry.getKey();
				Integer nodetype = entry.getValue();
				
				BkChanYeLianTreeNode node = allbkstk.getAllBkStocksTree().getSpecificNodeByHypyOrCode(nodecode, nodetype);
				
				NodeKeyWordsMap tmpmap = new AddNewsToSameTagsNodes.NodeKeyWordsMap ();
				tmpmap.setNode(node);
				tmpmap.setKeywords(kw);
				tmpmap.setSelected(true);
				
				info.add (tmpmap);
			}
		}
		
		if(info.isEmpty())
			return false; 
		
		((NewsKeyAndNodeMapTableModel)tblNodesLists.getModel () ).refresh (info);
		return true;
	}


	private void initializeGui() 
	{
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane, BorderLayout.CENTER);
			{	try {
					NewsKeyAndNodeMapTableModel tablemodel = new NewsKeyAndNodeMapTableModel ();
					tblNodesLists = new JTable(tablemodel);
					scrollPane.setViewportView(tblNodesLists);
				} catch (java.lang.ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
				}
				
			}
		}
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, BorderLayout.NORTH);
			
			JLabel lbldes = new JLabel ("是否将新闻添加到使用同关键字的个股或板块？");
			btnupdatetonode = new JButton ("添加");
			
			panel.add(lbldes);
			panel.add(btnupdatetonode);
			
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
	}



	@Override
	public void onMeetingChange(Cache cache) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onLabelChange(Cache cache) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onMeetingAdded(Meeting m)
	{
		this.news = m;
		if( inializeNewsTable () ) {
			this.toFront();
			this.setVisible(true);
		} else
			this.setVisible(false);
	}
	
	class NodeKeyWordsMap 
	{
		private Boolean selected;
		private String keywords;
		private BkChanYeLianTreeNode nodecode;
		private String nodename;
		public Boolean isSelected() {
			return selected;
		}
		public void setSelected(Boolean selected) {
			this.selected = selected;
		}
		public String getKeywords() {
			return keywords;
		}
		public void setKeywords(String keywords) {
			this.keywords = keywords;
		}
		public BkChanYeLianTreeNode getNode() {
			return nodecode;
		}
		public void setNode(BkChanYeLianTreeNode node) {
			this.nodecode = node;
		}
		public String getNodename() {
			return nodename;
		}
		public void setNodename(String nodename) {
			this.nodename = nodename;
		}
		
	}
}


class NewsKeyAndNodeMapTableModel extends DefaultTableModel 
{
	private List<AddNewsToSameTagsNodes.NodeKeyWordsMap> info;
	String[] jtableTitleStrings = { "选择", "关键词","代码","名称"};
	
	public NewsKeyAndNodeMapTableModel ()
	{
	}
	
	public void refresh (List<AddNewsToSameTagsNodes.NodeKeyWordsMap> info1)
	{
		this.info = info1;
		
		fireTableDataChanged();
	}
	
	public List<AddNewsToSameTagsNodes.NodeKeyWordsMap> getUserSelectResult ()
	{
		return this.info;
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
	    	
	    	NodeKeyWordsMap tmpmap = this.info.get (rowIndex);
	    	Object value = "??";
	    	switch (columnIndex) {
            case 0:
                value = new Boolean (tmpmap.isSelected());
                break;
            case 1:
            	value = tmpmap.getKeywords();
            	break;
            case 2:
            	value = tmpmap.getNode().getMyOwnCode();
                break;
            case 3:
            	value = tmpmap.getNode().getMyOwnName();
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
	    	if(column == 0) {
	    		SwingUtilities.invokeLater(new Runnable(){public void run(){
	    			NodeKeyWordsMap tmprow = info.get(row);
		    		Boolean curst = tmprow.isSelected();
		    		tmprow.setSelected(!curst);
	    		}});
	    		
	    		return true;
	    	}
	    	else
	    		return false;
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

}


