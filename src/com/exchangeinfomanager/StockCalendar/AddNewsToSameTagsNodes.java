package com.exchangeinfomanager.StockCalendar;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountInfoBasic;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.StockChiCangInfo;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Cache;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.CacheListener;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Meeting;
import com.exchangeinfomanager.labelmanagement.DBNodesTagsService;
import com.exchangeinfomanager.labelmanagement.DBSystemTagsService;
import com.exchangeinfomanager.labelmanagement.TagService;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.operations.AllCurrentTdxBKAndStoksTree;
import com.google.common.base.Splitter;

import javax.swing.JScrollPane;
import javax.swing.JTable;

public class AddNewsToSameTagsNodes extends JDialog implements CacheListener
{

	private final JPanel contentPanel = new JPanel();
	private JTable tblNodesLists;
	private Meeting news;
	private TagService dbserviceforsys;
	private AllCurrentTdxBKAndStoksTree allbkstk;

	/**
	 * Create the dialog.
	 */
	public AddNewsToSameTagsNodes(Cache cache) 
	{
		Set<BkChanYeLianTreeNode> all = new HashSet<> ();
		BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)this.allbkstk.getAllBkStocksTree().getModel().getRoot();
		all.add(treeroot);
		dbserviceforsys = new DBSystemTagsService ();
		
		initializeGui ();
		
		cache.addCacheListener(this);
	}
	
	private void inializeNewsTable ()
	{
		String keywords = news.getKeyWords();
		List<String> kwlist = Splitter.on(" ").omitEmptyStrings().splitToList(keywords);
		for(String kw : kwlist ) {
			Map<String, Integer> result = ((DBSystemTagsService)dbserviceforsys).getNodesSetOfSpecificTag(kw);
			
			kkk
		}
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
			{
				tblNodesLists = new JTable();
				scrollPane.setViewportView(tblNodesLists);
			}
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
		inializeNewsTable ();
		
		this.setVisible(true);
	}
}


class NewsKeyAndNodeMapTableModel extends DefaultTableModel 
{
	private List<String[]> info;
	String[] jtableTitleStrings = { "选择", "关键词","代码","名称"};
	
	public void addRows  ()
	{
		
		this.fireTableDataChanged();
	}
	 public int getRowCount() 
	 {
		 
	 }

	    @Override
	    public int getColumnCount() 
	    {
	        return jtableTitleStrings.length;
	    } 
	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	switch (columnIndex) {
            case 0:
                
                break;
            case 1:
            	
            	break;
            case 2:
            	
                break;
            case 3:
            	 
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
	    
//	    @Override
//	    public Class<?> getColumnClass(int columnIndex) {
//	        return // Return the class that best represents the column...
//	    }
	    
	    public String getColumnName(int column){ 
	    	return jtableTitleStrings[column];
	    }//设置表格列名 
		

	    public boolean isCellEditable(int row,int column) {
	    	return false;
		}
	    
}


