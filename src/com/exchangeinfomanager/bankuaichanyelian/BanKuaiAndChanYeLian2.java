package com.exchangeinfomanager.bankuaichanyelian;

import javax.swing.JPanel;

import java.awt.event.MouseEvent;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.table.AbstractTableModel;


import com.exchangeinfomanager.bankuai.gui.GuanZhuBanKuaiInfo;

import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.StockDbOperations;
import com.exchangeinfomanager.database.TwelveZhongDianGuanZhuXmlHandler;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;



import javax.swing.JButton;
import javax.swing.JSeparator;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;

public class BanKuaiAndChanYeLian2 extends JPanel 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 * @param stockInfoManager 
	 * @param bkdbopt2 
	 * @param zdgzbkxmlhandler 
	 * @param cylxmlhandler 
	 */
	public BanKuaiAndChanYeLian2 (StockInfoManager stockInfoManager2, BanKuaiDbOperation bkdbopt2,StockDbOperations stockdbopt, TwelveZhongDianGuanZhuXmlHandler zdgzbkxmlhandler2, ChanYeLianXMLHandler2 cylxmlhandler2) 
	{
		this.stockInfoManager = stockInfoManager2;
		this.bkdbopt = bkdbopt2;
		
		this.cylxmhandler = cylxmlhandler2;
		this.zdgzbkxmlhandler = zdgzbkxmlhandler2;
		daleidetailmap = zdgzbkxmlhandler.getZhongDianGuanZhuBanKuai ();
		
		initializeGui ();
		initializeSysConfig ();
		initializeAllDaLeiZdgzTableFromXml (null);
		initializeSingleDaLeiZdgzTableFromXml (0);
		updateZdgzInfoToBkCylTree ();
		
		createEvents ();
	}
	


	public static final int UP=0, LEFT=1, RIGHT=2, DOWN=3, NONE=4;

	private HashMap<String, ArrayList<GuanZhuBanKuaiInfo>> daleidetailmap;
	private StockInfoManager stockInfoManager;
    //javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
	//BkChanYeLianTreeNode topNode,  viewNodeParent, notesNode=null;
    //int viewNodeIndex;
    //DefaultTreeModel treeModel;
    boolean modifiedTitle = false;
    boolean editingNodeText = false;
    boolean ignoreExpansion = false;
//    int savedNodeCount = 0;
//    int currentNodeCount = 0;
    private ChanYeLianXMLHandler2 cylxmhandler;
    private TwelveZhongDianGuanZhuXmlHandler zdgzbkxmlhandler;
    private BkChanYeLianTree treechanyelian;
    BanKuaiDbOperation bkdbopt;
	private SystemConfigration sysconfig;
	private String currentselectedtdxbk;
	
	private void initializeSysConfig()
	{
		sysconfig = SystemConfigration.getInstance();
	}

	
	
	
	private JTextField tfldparsefilename;
	private JTable tableZdgzBankDetails;
	private JTable tableCurZdgzbk;
	private JTable tablebkgegu;
	private JTextField tfldfindbk;
	private JTextField tfldfindgegu;
	private JButton btnCylAddToZdgz;
	private JButton btnCylRemoveFromZdgz;
	private JButton saveButton;
	private JButton deleteButton;
	private JButton notesButton;
	private JTextPane textPane;
	private JButton btnfindbk;
	private JButton btnfindgegu;
	private JScrollPane scrollPanegegu;
	private JList lstTags;
	private JButton btnAddSubBk;
	private JButton buttonaddofficial;
	private JButton buttonremoveoffical;
	private JButton btnChsParseFile;



	private void initializeGui() 
	{
		JPanel panel = new JPanel();
		
		JPanel panelzdgz = new JPanel();
		
		JPanel panelcyltree = new JPanel();
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(panelcyltree, GroupLayout.DEFAULT_SIZE, 920, Short.MAX_VALUE)
						.addComponent(panelzdgz, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 920, Short.MAX_VALUE)
						.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
							.addGap(10)
							.addComponent(panel, GroupLayout.PREFERRED_SIZE, 883, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 61, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panelzdgz, GroupLayout.PREFERRED_SIZE, 362, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panelcyltree, GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		btnCylAddToZdgz = new JButton("\u52A0\u5165\u91CD\u70B9\u5173\u6CE8");
		
		btnCylRemoveFromZdgz = new JButton("\u79FB\u9664\u91CD\u70B9\u5173\u6CE8");
		
		JButton addSubnodeButton = new JButton("");
		addSubnodeButton.setIcon(new ImageIcon(BanKuaiAndChanYeLian2.class.getResource("/images/subnode24.png")));
		
		JButton addGeGuButton = new JButton("");
		addGeGuButton.setIcon(new ImageIcon(BanKuaiAndChanYeLian2.class.getResource("/images/subnode24.png")));
		
		btnAddSubBk = new JButton("\u589E\u52A0\u5B50\u677F\u5757");
		
		JScrollPane scrollPanesubbk = new JScrollPane();
		
		scrollPanegegu = new JScrollPane();
		
		JSplitPane splitPane = new JSplitPane();
		
		saveButton = new JButton("\u4FDD\u5B58\u4EA7\u4E1A\u94FE\u6811");
		saveButton.setEnabled(false);
		
		deleteButton = new JButton("\u5220\u9664\u8282\u70B9");
		deleteButton.setIcon(null);
		
		notesButton = new JButton("\u589E\u52A0\u5907\u6CE8");
		
		tfldfindbk = new JTextField();
		tfldfindbk.setColumns(10);
		
		btnfindbk = new JButton("\u5B9A\u4F4D\u677F\u5757");
		
		tfldfindgegu = new JTextField();
		tfldfindgegu.setColumns(10);
		
		btnfindgegu = new JButton("\u5B9A\u4F4D\u4E2A\u80A1");
		GroupLayout gl_panelcyltree = new GroupLayout(panelcyltree);
		gl_panelcyltree.setHorizontalGroup(
			gl_panelcyltree.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelcyltree.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelcyltree.createSequentialGroup()
							.addComponent(splitPane, GroupLayout.PREFERRED_SIZE, 379, GroupLayout.PREFERRED_SIZE)
							.addGap(17)
							.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panelcyltree.createSequentialGroup()
									.addComponent(addSubnodeButton)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(scrollPanesubbk, GroupLayout.PREFERRED_SIZE, 426, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_panelcyltree.createSequentialGroup()
									.addComponent(addGeGuButton)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING)
										.addGroup(gl_panelcyltree.createSequentialGroup()
											.addComponent(tfldfindbk, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(ComponentPlacement.UNRELATED)
											.addComponent(btnfindbk)
											.addGap(18)
											.addComponent(tfldfindgegu, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(ComponentPlacement.UNRELATED)
											.addComponent(btnfindgegu))
										.addComponent(scrollPanegegu, GroupLayout.PREFERRED_SIZE, 427, GroupLayout.PREFERRED_SIZE)))))
						.addGroup(gl_panelcyltree.createSequentialGroup()
							.addComponent(btnCylAddToZdgz)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btnCylRemoveFromZdgz)
							.addGap(267)
							.addComponent(btnAddSubBk))
						.addGroup(gl_panelcyltree.createSequentialGroup()
							.addComponent(saveButton)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(deleteButton)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(notesButton)))
					.addContainerGap(24, Short.MAX_VALUE))
		);
		gl_panelcyltree.setVerticalGroup(
			gl_panelcyltree.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelcyltree.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelcyltree.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnCylAddToZdgz)
						.addComponent(btnCylRemoveFromZdgz)
						.addComponent(btnAddSubBk))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING, false)
						.addGroup(gl_panelcyltree.createSequentialGroup()
							.addComponent(scrollPanesubbk, GroupLayout.PREFERRED_SIZE, 82, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(scrollPanegegu, 0, 0, Short.MAX_VALUE))
						.addComponent(splitPane, GroupLayout.PREFERRED_SIZE, 416, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panelcyltree.createParallelGroup(Alignment.BASELINE)
						.addComponent(saveButton)
						.addComponent(deleteButton, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
						.addComponent(notesButton)
						.addComponent(tfldfindbk, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnfindbk)
						.addComponent(tfldfindgegu, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnfindgegu))
					.addGap(12))
				.addGroup(gl_panelcyltree.createSequentialGroup()
					.addGap(67)
					.addComponent(addSubnodeButton)
					.addPreferredGap(ComponentPlacement.RELATED, 218, Short.MAX_VALUE)
					.addComponent(addGeGuButton)
					.addGap(139))
		);
		
		JScrollPane treeScrollPane = new JScrollPane();
		splitPane.setLeftComponent(treeScrollPane);
		
		treechanyelian = initializeBkChanYeLianXMLTree();
		treeScrollPane.setViewportView(treechanyelian);
		
		JScrollPane notesScrollPane = new JScrollPane();
		splitPane.setRightComponent(notesScrollPane);
		
		textPane = new JTextPane();
		notesScrollPane.setViewportView(textPane);
		
		BanKuaiGeGuTableModel bkgegumapmdl = new BanKuaiGeGuTableModel(null);
		tablebkgegu = new  JTable(bkgegumapmdl)
		{
			private static final long serialVersionUID = 1L;
			public String getToolTipText(MouseEvent e) 
			{
                String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);

                try {
                    tip = getValueAt(rowIndex, colIndex).toString();
                } catch (RuntimeException e1) {
                	e1.printStackTrace();
                }
                return tip;
            } 
		};

		scrollPanegegu.setViewportView(tablebkgegu);
		
		lstTags = new JList();
		scrollPanesubbk.setViewportView(lstTags);
		panelcyltree.setLayout(gl_panelcyltree);
		
		JButton btnSaveZdgz = new JButton("\u4FDD\u5B58\u91CD\u70B9\u5173\u6CE8");
		
		JButton btnGenTDXCode = new JButton("\u751F\u6210TDX\u4EE3\u7801");
		
		JScrollPane scrollPaneDaLeiDetail = new JScrollPane();
		
		buttonaddofficial = new JButton("->");
		
		buttonremoveoffical = new JButton("<-");
		
		JScrollPane scrollPaneDaLei = new JScrollPane();
		
		JButton btnadddalei = new JButton("\u589E\u52A0\u80A1\u7968\u6C60");
		
		JButton btndeldalei = new JButton("\u5220\u9664\u80A1\u7968\u6C60");
		
		JSeparator separator_1 = new JSeparator();
		GroupLayout gl_panelzdgz = new GroupLayout(panelzdgz);
		gl_panelzdgz.setHorizontalGroup(
			gl_panelzdgz.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelzdgz.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelzdgz.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelzdgz.createSequentialGroup()
							.addComponent(scrollPaneDaLeiDetail, GroupLayout.PREFERRED_SIZE, 318, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panelzdgz.createParallelGroup(Alignment.LEADING)
								.addComponent(buttonremoveoffical)
								.addComponent(buttonaddofficial)))
						.addGroup(gl_panelzdgz.createSequentialGroup()
							.addComponent(btnSaveZdgz)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnGenTDXCode)))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panelzdgz.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelzdgz.createSequentialGroup()
							.addComponent(btnadddalei)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btndeldalei))
						.addComponent(scrollPaneDaLei, GroupLayout.PREFERRED_SIZE, 505, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(12, Short.MAX_VALUE))
				.addGroup(Alignment.TRAILING, gl_panelzdgz.createSequentialGroup()
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(separator_1, GroupLayout.PREFERRED_SIZE, 906, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		gl_panelzdgz.setVerticalGroup(
			gl_panelzdgz.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelzdgz.createSequentialGroup()
					.addGroup(gl_panelzdgz.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelzdgz.createSequentialGroup()
							.addGap(89)
							.addComponent(buttonaddofficial)
							.addGap(77)
							.addComponent(buttonremoveoffical))
						.addGroup(gl_panelzdgz.createSequentialGroup()
							.addContainerGap()
							.addGroup(gl_panelzdgz.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnSaveZdgz)
								.addComponent(btnGenTDXCode)
								.addComponent(btnadddalei)
								.addComponent(btndeldalei))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panelzdgz.createParallelGroup(Alignment.LEADING)
								.addComponent(scrollPaneDaLei, GroupLayout.PREFERRED_SIZE, 308, GroupLayout.PREFERRED_SIZE)
								.addComponent(scrollPaneDaLeiDetail, GroupLayout.PREFERRED_SIZE, 308, GroupLayout.PREFERRED_SIZE))))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(separator_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		
		CurZdgzBanKuaiTableModel curzdgzbkmodel = new CurZdgzBanKuaiTableModel (); 
		tableCurZdgzbk = new  JTable(curzdgzbkmodel)
		{
			private static final long serialVersionUID = 1L;
			public String getToolTipText(MouseEvent e) 
			{
                String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);

                try {
                    tip = getValueAt(rowIndex, colIndex).toString();
                } catch (RuntimeException e1) {
                	e1.printStackTrace();
                }
                return tip;
            } 
		};
		int preferedwidth = 170;
		tableCurZdgzbk.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(preferedwidth);
		tableCurZdgzbk.getTableHeader().getColumnModel().getColumn(0).setMinWidth(preferedwidth);
		tableCurZdgzbk.getTableHeader().getColumnModel().getColumn(0).setWidth(preferedwidth);
		tableCurZdgzbk.getTableHeader().getColumnModel().getColumn(0).setPreferredWidth(preferedwidth);

		scrollPaneDaLei.setViewportView(tableCurZdgzbk);
		
		ZdgzBanKuaiDetailXmlTableModel xmlaccountsmodel = new ZdgzBanKuaiDetailXmlTableModel( );
		tableZdgzBankDetails = new JTable(xmlaccountsmodel){

			private static final long serialVersionUID = 1L;

			public String getToolTipText(MouseEvent e) {
                String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);

                try {
                    tip = getValueAt(rowIndex, colIndex).toString();
                } catch (RuntimeException e1) {
                    //catch null pointer exception if mouse is over an empty line
                }

                return tip;
            }
		};
		
		
		tableZdgzBankDetails.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(100);
		tableZdgzBankDetails.getTableHeader().getColumnModel().getColumn(0).setMinWidth(100);
		tableZdgzBankDetails.getTableHeader().getColumnModel().getColumn(0).setWidth(100);
		tableZdgzBankDetails.getTableHeader().getColumnModel().getColumn(0).setPreferredWidth(100);
//		tableZdgzBankDetails.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//		tableZdgzBankDetails.setPreferredScrollableViewportSize(java.awt.Toolkit.getDefaultToolkit().getScreenSize());
		scrollPaneDaLeiDetail.setViewportView(tableZdgzBankDetails);
		panelzdgz.setLayout(gl_panelzdgz);
		
		JLabel label = new JLabel("\u89E3\u6790\u677F\u5757\u6587\u4EF6");
		
		tfldparsefilename = new JTextField();
		tfldparsefilename.setColumns(10);
		
		btnChsParseFile = new JButton("\u9009\u62E9\u6587\u4EF6");
		
		JSeparator separator = new JSeparator();
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(label)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(tfldparsefilename, GroupLayout.PREFERRED_SIZE, 565, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnChsParseFile)
					.addContainerGap(67, Short.MAX_VALUE))
				.addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup()
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(separator, GroupLayout.PREFERRED_SIZE, 895, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(label)
						.addComponent(tfldparsefilename, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnChsParseFile, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(separator, GroupLayout.PREFERRED_SIZE, 6, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		panel.setLayout(gl_panel);
		setLayout(groupLayout);

		
	}
	
	private  BkChanYeLianTree initializeBkChanYeLianXMLTree()
	{
        
		BkChanYeLianTreeNode topNode = cylxmhandler.getBkChanYeLianXMLTree();
		
		BkChanYeLianTree tmptreechanyelian = new BkChanYeLianTree(topNode);
		return tmptreechanyelian;
		

	}
}


/*
 * 12个大类某个具体大类的关注内容表
 */
class CurZdgzBanKuaiTableModel extends AbstractTableModel 
{
	private HashMap<String,ArrayList<GuanZhuBanKuaiInfo>> gzbkmap;
	private String cbxDale;

	String[] jtableTitleStrings = {  "板块产业链","创建时间","入选"};
	
	CurZdgzBanKuaiTableModel ()
	{
//		this.gzbkmap =  gzbkmap;
//		this.cbxDale = cbxDale2;
	}

	public void refresh (HashMap<String,ArrayList<GuanZhuBanKuaiInfo>> gzbkmap,String cbxDale2) 
	{
		this.gzbkmap =  gzbkmap;
		this.cbxDale = cbxDale2;
	}
	public int getGuanZhuBanKuaiInfoIndex (GuanZhuBanKuaiInfo gzbk)
	{
		String currentdalei = this.cbxDale;
		ArrayList<GuanZhuBanKuaiInfo> tmpgzbkinfo = this.gzbkmap.get(currentdalei);
		return tmpgzbkinfo.indexOf(gzbk);
	}
	public GuanZhuBanKuaiInfo getGuanZhuBanKuaiInfo (int rowindex)
	{
		String currentdalei = this.cbxDale;
		ArrayList<GuanZhuBanKuaiInfo> tmpgzbkinfo = this.gzbkmap.get(currentdalei);
		return tmpgzbkinfo.get(rowindex);
	}

	 public int getRowCount() 
	 {
		 try {
			 String currentdalei = this.cbxDale;  
			 ArrayList<GuanZhuBanKuaiInfo> tmpgzbklist = gzbkmap.get(currentdalei);
			 return tmpgzbklist.size();
		 } catch (java.lang.NullPointerException e) {
			 return 0;
		 }
	 }

	    @Override
	    public int getColumnCount() 
	    {
	        return jtableTitleStrings.length;
	    
	    } 
//	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	if(gzbkmap == null)
	    		return null;
	    	
	    	String currentdalei = this.cbxDale;  
			ArrayList<GuanZhuBanKuaiInfo> tmpgzbklist = gzbkmap.get(currentdalei);
			GuanZhuBanKuaiInfo tmpgzbk = tmpgzbklist.get(rowIndex);
	    	
	    	Object value = "??";

	    	switch (columnIndex) {
            case 0:
                value = tmpgzbk.getBkchanyelian();
                break;
            case 1:
            	value = tmpgzbk.getSelectedtime();
                break;
            case 2:
                value = new Boolean(tmpgzbk.isOfficallySelected() );
                break;
	    	}

        return value;
	  }

     public Class<?> getColumnClass(int columnIndex) {
		      Class clazz = String.class;
		      switch (columnIndex) {
		      case 0:
		    	  clazz = String.class;
		    	  break;
		        case 1:
			          clazz = String.class;
			          break;
		        case 2:
			          clazz = Boolean.class;
			          break;
		      }
		      
		      return clazz;
		}
	    
	    public String getColumnName(int column){ 
	    	return jtableTitleStrings[column];
	    }//设置表格列名 

	    public boolean isCellEditable(int row,int column) {
	    	return false;
		}
	    
	    public void deleteAllRows()
	    {
	    }
//	    public void deleteRow(int row)
//	    {
//	    	String currentdalei = cbxDale.getSelectedItem().toString();  
//			ArrayList<GuanZhuBanKuaiInfo> tmpgzbklist = gzbkmap.get(currentdalei);
//			tmpgzbklist.remove(row);
//	    }
	    
}


/*
 * 重点关注板块表
 */
class ZdgzBanKuaiDetailXmlTableModel extends AbstractTableModel 
{
	private HashMap<String,ArrayList<GuanZhuBanKuaiInfo>> gzbkmap;
	
	String[] jtableTitleStrings = { "股票池", "关注板块"};
	private ArrayList<String> gzdalei;
	
	ZdgzBanKuaiDetailXmlTableModel ()
	{
//		this.gzbkmap =  gzbkmap;
//		try {
//			this.gzdalei = new ArrayList<String>(gzbkmap.keySet() );
//		} catch (java.lang.NullPointerException e)		{
//			
//		}
	}

	

	public void refresh (HashMap<String,ArrayList<GuanZhuBanKuaiInfo>> gzbkmap) 
	{
		this.gzbkmap =  gzbkmap;
		this.gzdalei = new ArrayList<String>(gzbkmap.keySet() );
	}

	 public int getRowCount() 
	 {
		return gzdalei.size();
	 }
	 
	 public String getZdgzDaLei (int row)
	 {
		 return (String)gzdalei.get(row);
	 }
	 public int getDaLeiIndex (String dalei)
	 {
		 return this.gzdalei.indexOf(dalei);
	 }

	    @Override
	    public int getColumnCount() 
	    {
	        return jtableTitleStrings.length;
	    
	    } 
//	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	if(gzbkmap == null)
	    		return null;
	    	
	    	
	    	Object value = "??";
	    	
	    	switch (columnIndex) {
            case 0:
                value = gzdalei.get(rowIndex);
                break;
            case 1:
            	String result = "";
            	try {
            		for( GuanZhuBanKuaiInfo tmpgzcyl : gzbkmap.get(gzdalei.get(rowIndex) )) {
            			if(tmpgzcyl.isOfficallySelected()) {
            				String chanyelian =  tmpgzcyl.getBkchanyelian();
    	            		String seltime = tmpgzcyl.getSelectedtime();
    	            		result = result + chanyelian + "(" + seltime +")" + "|" + " ";
            			}
            			
            		}
            	 } catch (java.lang.NullPointerException e) {
            		 
            	 }
            	value = result;
                break;
	    	}
        return value;
	  }

     public Class<?> getColumnClass(int columnIndex) {
		      Class clazz = String.class;
		      switch (columnIndex) {
		      case 0:
		    	  clazz = String.class;
		    	  break;
		        case 1:
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


class BanKuaiGeGuTableModel extends AbstractTableModel 
{
	private HashMap<String,String> bkgegumap;
	String[] jtableTitleStrings = { "股票代码", "股票名称"};
	private ArrayList<String> bkgeguname;
	
	BanKuaiGeGuTableModel (HashMap<String,String> bkgegu)
	{
		if(bkgegu != null) {
			this.bkgegumap = bkgegu;
			this.bkgeguname = new ArrayList<String> (bkgegu.keySet() );
		}
	}

	public void refresh  (HashMap<String,String> bkgegu)
	{
		this.bkgegumap = bkgegu;
		bkgeguname = new ArrayList<String> (bkgegu.keySet() );

		Collator collator = Collator.getInstance(Locale.CHINESE); //用中文排序
 		Collections.sort(bkgeguname,collator);
		
	}
	 public int getRowCount() 
	 {
		 if(this.bkgegumap == null)
			 return 0;
		 else if(this.bkgegumap.isEmpty()  )
			 return 0;
		 else
			 return this.bkgegumap.size();
	 }

	    @Override
	    public int getColumnCount() 
	    {
	        return jtableTitleStrings.length;
	    } 
	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	if(bkgegumap.isEmpty())
	    		return null;
	    	
	    	Object value = "??";
	    	switch (columnIndex) {
            case 0:
                value = bkgeguname.get(rowIndex);
                break;
            case 1:
            	value = bkgegumap.get( bkgeguname.get(rowIndex) );
                break;
	    	}

        return value;
	  }

     public Class<?> getColumnClass(int columnIndex) {
		      Class clazz = String.class;
		      switch (columnIndex) {
		      case 0:
		    	  clazz = String.class;
		    	  break;
		        case 1:
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
	    public String getStockCodeAndName (int row) 
	    {
	    	return bkgeguname.get(row) + bkgegumap.get( bkgeguname.get(row) );
	    }

	    
	    public void deleteAllRows()
	    {
	    	if(this.bkgegumap == null)
				 return ;
			 else 
				 bkgegumap.clear();
	    }
	    
	    public int getStockRowIndex (String stockcode) 
	    {
	    	int index = bkgeguname.indexOf(stockcode);
	    	return index;
	    }
	    
}