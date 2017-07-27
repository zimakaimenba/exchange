package com.exchangeinfomanager.bankuai.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountInfoBasic;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.StockChiCangInfo;
import com.exchangeinfomanager.asinglestockinfo.ASingleStockInfo;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.ChanYeLianXMLHandler;
import com.exchangeinfomanager.database.StockDbOperations;
import com.exchangeinfomanager.database.TwelveZhongDianGuanZhuXmlHandler;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.exchangeinfomanager.tongdaxinreport.TDXFormatedOpt;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

import net.ginkgo.dom4jcopy.CustomTreeCellRenderer;
import net.ginkgo.dom4jcopy.GinkgoNode;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JComboBox;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.DefaultListModel ;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.AbstractAction;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.Collator;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.swing.JTable;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import org.apache.log4j.Logger;
import javax.swing.JTree;

public class TwelveZhongDianGuanZhuBanKuaiSheZhi extends JDialog {
	/**
	 * Create the dialog.
	 * @param zdgzbkxmlhandler2 
	 */
	public TwelveZhongDianGuanZhuBanKuaiSheZhi (BanKuaiDbOperation bkdbopt2,StockDbOperations stockdbopt, TwelveZhongDianGuanZhuXmlHandler zdgzbkxmlhandler,ChanYeLianXMLHandler cylxmhandler) 
	{
		this.bkdbopt = bkdbopt2;
		this.stockdbopt = stockdbopt;
		this.zdgzbkxmlhandler = zdgzbkxmlhandler;
		this.cylxmhandler = cylxmhandler;
		initializeSysConfig ();
		daleidetailmap = zdgzbkxmlhandler.getZhongDianGuanZhuBanKuai ();
		initializeGui ();
		createEvents();

		startDialog ();
	}
	
	public void startDialog ()
	{
		initializeChanYeLianXMLTree ();
		initializeAllDaLeiZdgzTableFromXml (null);
		initializeSingleDaLeiZdgzTableFromXml (0); 
	}

	private HashMap<String, ArrayList<GuanZhuBanKuaiInfo>> daleidetailmap;
	private TwelveZhongDianGuanZhuXmlHandler zdgzbkxmlhandler;
	private SystemConfigration sysconfig;
	private BanKuaiDbOperation bkdbopt;
	private StockDbOperations stockdbopt;
	//private String[] bankuaidaleiname = { "国新政策","成长股","涨价概念","次新股","重大事件","特定时间","超跌周期","券商参券","重组收购","稳定增长","业绩反转","黑天鹅受益"};
	private ChanYeLianXMLHandler cylxmhandler;
	
	public void initializeChanYeLianXMLTree ()
	{
		treechanyelian.setDragEnabled(false);
		treechanyelian.setEditable(false );
		treechanyelian.setRootVisible(false);

		GinkgoNode topNode = cylxmhandler.getBkChanYeLianXMLTree();
		//tree.setTransferHandler(new TreeTransferHandler());
		DefaultTreeModel treeModel = new DefaultTreeModel(topNode);
		treechanyelian.setModel(treeModel);
		
		//topNode.removeAllChildren();
		//topNode = cylxmhandler.getBkChanYeLianXMLTree();		
		treeModel.setRoot(topNode);
        treeModel.nodeStructureChanged(topNode);
	}
	
	private void initializeSysConfig()
	{
		sysconfig = SystemConfigration.getInstance();
	}
	

	private void initializeAllDaLeiZdgzTableFromXml (String daleiname )
	{
		
		((ZdgzBanKuaiDetailXmlTableModel)tableZdgzBankDetails.getModel()).refresh(daleidetailmap);
		((ZdgzBanKuaiDetailXmlTableModel)tableZdgzBankDetails.getModel()).fireTableDataChanged();
		
		if(daleiname == null)
			tableZdgzBankDetails.setRowSelectionInterval(0,0);
		else {
			int cursorpostion = ((ZdgzBanKuaiDetailXmlTableModel)tableZdgzBankDetails.getModel()).getDaLeiIndex(daleiname);
			tableZdgzBankDetails.setRowSelectionInterval(cursorpostion,cursorpostion);
		}
	}
	/*
	 * 参数为操作后光标位置
	 */
	private void initializeSingleDaLeiZdgzTableFromXml (int row)
	{
		String selectedalei = getCurSelectedDaLei ();
		if( selectedalei != null) {
			((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).refresh(daleidetailmap,selectedalei);
			((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).fireTableDataChanged();
		}
		
		if(((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).getRowCount() != 0)
			tableCurZdgzbk.setRowSelectionInterval(row,row);
	}
	
		
//	private boolean saveAllZdgzbkToXml ()
//	{
//		return zdgzbkxmlhandler.saveAllZdgzbkToXml () ;
//		
//	}
	
	/*
     * 展开所选取的节点
     */
    private static void expandTreeNode(JTree aTree, GinkgoNode aNode) {  
        if (aNode.isLeaf()) {  
          return;  
        }  
        aTree.expandPath(new TreePath( ( (GinkgoNode) aNode).getPath()));  
        int n = aNode.getChildCount();  
        for (int i = 0; i <n; i++) {  
          expandTreeNode(aTree, (GinkgoNode) aNode.getChildAt(i));  
        }  
     } 

    protected TreePath findBanKuaiInTree(String bkinputed) 
	{
    	@SuppressWarnings("unchecked")
    	 TreePath bkpath = null ;
	    Enumeration<GinkgoNode> e =  ((GinkgoNode) ((DefaultTreeModel)treechanyelian.getModel()).getRoot()).depthFirstEnumeration();
	    while (e.hasMoreElements()) { //不知道为什么最后会多一个null
	    	GinkgoNode node = e.nextElement();
	    	String pystr = node.getHanYuPingYin();
	    	System.out.println(pystr);
	        if (node.getHanYuPingYin().equalsIgnoreCase(bkinputed)) {
	             bkpath = new TreePath(node.getPath());
	             treechanyelian.setSelectionPath(bkpath);
	             treechanyelian.scrollPathToVisible(bkpath);
	             
	             expandTreeNode (treechanyelian,node);
	        }
	    }
	    
	    return null;
	}
    private String getCurSelectedDaLei ()
    {
    	int row = tableZdgzBankDetails.getSelectedRow();
		if(row <0) {
			return null;
		} 
		
		 String selecteddalei = ((ZdgzBanKuaiDetailXmlTableModel)tableZdgzBankDetails.getModel()).getZdgzDaLei (row);
		 tableZdgzBankDetails.setRowSelectionInterval(row, row);
		 return  selecteddalei;
    }
    
	private void createEvents() 
	{
		buttonremoveoffical.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				int row = tableCurZdgzbk.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择一个产业链","Warning",JOptionPane.WARNING_MESSAGE);
					return ;
				}
				
				String selectedalei = getCurSelectedDaLei ();
				if( selectedalei != null) {
					 GuanZhuBanKuaiInfo gzbk = ((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).getGuanZhuBanKuaiInfo(row);
					 gzbk.setOfficallySelected(false);
					 initializeAllDaLeiZdgzTableFromXml(selectedalei);
					 //zdgzbkxmlhandler.addNewGuanZhuBanKuai(selectedalei, gzbk);
				} else
					 JOptionPane.showMessageDialog(null,"请选择一个大类","Warning",JOptionPane.WARNING_MESSAGE);
				
				if(JOptionPane.showConfirmDialog(null, "是否直接从候补产业链中删除？","Warning", JOptionPane.YES_NO_OPTION) == 0) {
					GuanZhuBanKuaiInfo gzbk = ((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).getGuanZhuBanKuaiInfo(row);
					zdgzbkxmlhandler.removeGuanZhuBanKuai(selectedalei, gzbk);
					initializeSingleDaLeiZdgzTableFromXml (1);
				} else
					initializeSingleDaLeiZdgzTableFromXml (row);
				
				btnSave.setEnabled(true);
				
//				((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).refresh(zdgzbkxmlhandler.getZhongDianGuanZhuBanKuai (),selectedalei);
//				((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).fireTableDataChanged();
				
			}
		});
		
		buttonaddofficial.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				int row = tableCurZdgzbk.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择一个产业链","Warning",JOptionPane.WARNING_MESSAGE);
					return ;
				}
				
				String selectedalei = getCurSelectedDaLei ();
				if( selectedalei != null) {
					 GuanZhuBanKuaiInfo gzbk = ((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).getGuanZhuBanKuaiInfo(row);
					 gzbk.setOfficallySelected(true);
					 //zdgzbkxmlhandler.addNewGuanZhuBanKuai(selectedalei, gzbk);
				} else
					 JOptionPane.showMessageDialog(null,"请选择一个大类","Warning",JOptionPane.WARNING_MESSAGE);
				
				initializeAllDaLeiZdgzTableFromXml (selectedalei);
				initializeSingleDaLeiZdgzTableFromXml (row);
				btnSave.setEnabled(true);
//				((ZdgzBanKuaiDetailXmlTableModel)tableZdgzBankDetails.getModel()).refresh(daleidetailmap);
//				((ZdgzBanKuaiDetailXmlTableModel)tableZdgzBankDetails.getModel()).fireTableDataChanged();
//				
//				((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).refresh(zdgzbkxmlhandler.getZhongDianGuanZhuBanKuai (),selectedalei);
//				((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).fireTableDataChanged();
			}
		});
		
		btnadddalei.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				String newdaleiname = JOptionPane.showInputDialog(null,"请输入新的大类名称:","添加大类", JOptionPane.QUESTION_MESSAGE);
				if( !newdaleiname.isEmpty() && !daleidetailmap.keySet().contains(newdaleiname)) {
					ArrayList<GuanZhuBanKuaiInfo> tmpgzbklist = new ArrayList<GuanZhuBanKuaiInfo> (); 
					daleidetailmap.put(newdaleiname, tmpgzbklist);
					
					initializeAllDaLeiZdgzTableFromXml (newdaleiname);
					initializeSingleDaLeiZdgzTableFromXml (0);
					
					
//					((ZdgzBanKuaiDetailXmlTableModel)tableZdgzBankDetails.getModel()).refresh(daleidetailmap);
//					((ZdgzBanKuaiDetailXmlTableModel)tableZdgzBankDetails.getModel()).fireTableDataChanged();
//					
//					((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).refresh(daleidetailmap,newdaleiname);
//					((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).fireTableDataChanged();
					
					btnSave.setEnabled(true);
				}
			}
		});
		
		btndeldalei.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				int row = tableZdgzBankDetails.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择一个大类","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				String daleiname = ((ZdgzBanKuaiDetailXmlTableModel)tableZdgzBankDetails.getModel()).getZdgzDaLei(row);
				int n = JOptionPane.showConfirmDialog(null, "确定删除" + daleiname + "?", "删除大类",JOptionPane.YES_NO_OPTION);//i=0/1  
				if(n == 0) {
					daleidetailmap.remove(daleiname);
					
					initializeAllDaLeiZdgzTableFromXml (daleiname);
					initializeSingleDaLeiZdgzTableFromXml (row);
					
//					((ZdgzBanKuaiDetailXmlTableModel)tableZdgzBankDetails.getModel()).refresh(daleidetailmap);
//					((ZdgzBanKuaiDetailXmlTableModel)tableZdgzBankDetails.getModel()).fireTableDataChanged();
//					
//					((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).refresh(daleidetailmap,daleiname);
//					((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).fireTableDataChanged();
					
					btnSave.setEnabled(true);
				}
			}
		});
		
		tfldfindbk.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				String bkinputed = tfldfindbk.getText();
				findBanKuaiInTree (bkinputed);
			}
		});
		
		treechanyelian.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				TreePath closestPath = treechanyelian.getClosestPathForLocation(evt.getX(), evt.getY());
			       
		        System.out.println(closestPath);
		        if(closestPath != null) {
		            Rectangle pathBounds = treechanyelian.getPathBounds(closestPath);
		            int maxY = (int) pathBounds.getMaxY();
		            int minX = (int) pathBounds.getMinX();
		            int maxX = (int) pathBounds.getMaxX();
		            if (evt.getY() > maxY) treechanyelian.clearSelection();
		            else if (evt.getX() < minX || evt.getX() > maxX) treechanyelian.clearSelection();
		        }
		        
		        GinkgoNode parent = (GinkgoNode) closestPath.getLastPathComponent();
	        	expandTreeNode (treechanyelian,parent);
			}
		});
		
		tableZdgzBankDetails.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				int row = tableZdgzBankDetails.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择一个大类","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				} 
				
				initializeSingleDaLeiZdgzTableFromXml (0);
//				 String selecteddalei = ((ZdgzBanKuaiDetailXmlTableModel)tableZdgzBankDetails.getModel()).getZdgzDaLei (row);
//				 ((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).refresh(zdgzbkxmlhandler.getZhongDianGuanZhuBanKuai (),selecteddalei);
//				 ((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).fireTableDataChanged();
//				 
				 
//				 cbxDale.setSelectedItem(selecteddalei);
				
			}
		});
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) 
			{
				if(btnSave.isEnabled()) {
					int action = JOptionPane.showConfirmDialog(null, "更改后的信息未存！是否需要保存？","Warning", JOptionPane.YES_NO_OPTION);
					if(0 == action) {
						zdgzbkxmlhandler.saveAllZdgzbkToXml () ;
						//setZdgzMrmcInfoToDb();
					}
					 
				} else {
					dispose();
				}
			}
		});
		
		mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				TDXFormatedOpt.parseZdgzBkToTDXCode(zdgzbkxmlhandler.getZhongDianGuanZhuBanKuai ());
			}
		});

//		mntmNewMenuItem.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseClicked(MouseEvent e) 
//			{
//				
//			}
//		});
		
		btnSave.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				if( zdgzbkxmlhandler.saveAllZdgzbkToXml () )
					btnSave.setEnabled(false);
				else
					JOptionPane.showMessageDialog(null, "保存XML失败！请查找原因。","Warning", JOptionPane.WARNING_MESSAGE);
			}
		});
		
		btnLeftToRight.addMouseListener(new MouseAdapter() {
			 @Override
			public void mouseClicked(MouseEvent arg0) 
			{
				 TreePath path = treechanyelian.getSelectionPath();
				 GinkgoNode parent = (GinkgoNode) treechanyelian.getSelectionPath().getLastPathComponent();
				 
//				 System.out.println(path.toString());
//				 System.out.print(path.getPathCount() );
//				 System.out.print(parent.getStatus());
				 
				 GuanZhuBanKuaiInfo tmpgzbk = new GuanZhuBanKuaiInfo ();
				 String tdxbk = path.getPathComponent(1).toString();
				 String chanyelian = "";
				 if( parent.getStatus() != GinkgoNode.BKGEGU) //是个股的情况和不是个股的情况需要分开处理
					 for(int i=1;i<path.getPathCount();i++) { //不是个股，一直保存到底
						 chanyelian = chanyelian + path.getPathComponent(i).toString() + "->";
					 }
				 else { //是个股，最后一个个股不用保存
					 for(int i=1;i<path.getPathCount()-1;i++) {
						 chanyelian = chanyelian + path.getPathComponent(i).toString() + "->";
					 }
				 }
				 System.out.println(chanyelian);
				 
				 
				 
				 tmpgzbk.setSelectedtime(new Date ());
				 tmpgzbk.setBkchanyelian(chanyelian.substring(0, chanyelian.length()-2)); //去掉最后的->
				 tmpgzbk.setTdxbk(tdxbk);
				 if(JOptionPane.showConfirmDialog(null, "是否直接加入重点关注？","Warning", JOptionPane.YES_NO_OPTION) == 1) {
					 tmpgzbk.setOfficallySelected(false);
				 } else
					 tmpgzbk.setOfficallySelected(true);

				 String daleiname = getCurSelectedDaLei();
				 if( daleiname != null)
					 zdgzbkxmlhandler.addNewGuanZhuBanKuai(daleiname, tmpgzbk);
				 else
					 JOptionPane.showMessageDialog(null,"请选择一个大类","Warning",JOptionPane.WARNING_MESSAGE);
//				 HashMap<String, ArrayList<GuanZhuBanKuaiInfo>> allzdgzbkmap = zdgzbkxmlhandler.getZhongDianGuanZhuBanKuai ();
//				 ArrayList<GuanZhuBanKuaiInfo> tmpzdgzbklist; //右边显示的内容
//				 try {
//					tmpzdgzbklist = allzdgzbkmap.get(cbxDale.getSelectedItem().toString());
//					tmpzdgzbklist.add(tmpgzbk);
//				} catch (java.lang.NullPointerException e) {
//					tmpzdgzbklist = new ArrayList<GuanZhuBanKuaiInfo> ();
//					tmpzdgzbklist.add(tmpgzbk);
//				}
//				allzdgzbkmap.put(cbxDale.getSelectedItem().toString(), tmpzdgzbklist);
				 
				 initializeAllDaLeiZdgzTableFromXml (daleiname);
				 int rowindex = ((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).getGuanZhuBanKuaiInfoIndex(tmpgzbk);
				 initializeSingleDaLeiZdgzTableFromXml (rowindex);
				 
//				 ((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).refresh(zdgzbkxmlhandler.getZhongDianGuanZhuBanKuai (),daleiname);
//				 ((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).fireTableDataChanged();
//				 
//				 ((ZdgzBanKuaiDetailXmlTableModel)tableZdgzBankDetails.getModel()).refresh(zdgzbkxmlhandler.getZhongDianGuanZhuBanKuai ());
//				 ((ZdgzBanKuaiDetailXmlTableModel)tableZdgzBankDetails.getModel()).fireTableDataChanged();
				 
			     btnSave.setEnabled(true);
			     
			}
		});
		
		btnRightToLeft.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{

				int row = tableCurZdgzbk.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择产业链","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				
				 //HashMap<String, ArrayList<GuanZhuBanKuaiInfo>> allzdgzbkmap = zdgzbkxmlhandler.getZhongDianGuanZhuBanKuai ();
//				 ArrayList<GuanZhuBanKuaiInfo> tmpzdgzbklist = allzdgzbkmap.get(cbxDale.getSelectedItem().toString());
//				 tmpzdgzbklist.remove(row);
//				 allzdgzbkmap.put(cbxDale.getSelectedItem().toString(), tmpzdgzbklist);
				
				 //String daleiname = cbxDale.getSelectedItem().toString();
				String daleiname = getCurSelectedDaLei();
				 if( daleiname != null) {
					 GuanZhuBanKuaiInfo gzbk = ((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).getGuanZhuBanKuaiInfo(row);
					 zdgzbkxmlhandler.removeGuanZhuBanKuai(daleiname, gzbk);
				 }
				 else
					 JOptionPane.showMessageDialog(null,"请选择一个大类","Warning",JOptionPane.WARNING_MESSAGE);
				 
				 initializeAllDaLeiZdgzTableFromXml (daleiname);
				 initializeSingleDaLeiZdgzTableFromXml (0);
				 
				 
				 
					
				 //((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).refresh(allzdgzbkmap);
//				 ((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).refresh(zdgzbkxmlhandler.getZhongDianGuanZhuBanKuai (),daleiname );
//				 ((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).fireTableDataChanged();
				 
				 //((ZdgzBanKuaiDetailXmlTableModel)tableZdgzBankDetails.getModel()).refresh(allzdgzbkmap);
//				 ((ZdgzBanKuaiDetailXmlTableModel)tableZdgzBankDetails.getModel()).refresh(zdgzbkxmlhandler.getZhongDianGuanZhuBanKuai () );
//				 ((ZdgzBanKuaiDetailXmlTableModel)tableZdgzBankDetails.getModel()).fireTableDataChanged();
				 
			     btnSave.setEnabled(true);
			    
			}
		});
		
		okButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				if(btnSave.isEnabled()) {
					int action = JOptionPane.showConfirmDialog(null, "更改后的信息未存！是否需要保存？","Warning", JOptionPane.YES_NO_OPTION);
					if(0 == action) {
						zdgzbkxmlhandler.saveAllZdgzbkToXml () ;
					}
					dispose ();
					 
				} else {
					dispose();
				}
			}
		});
		
		cancelButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				dispose();
			}
		});
		
		btnBkGl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e)
			{
				try {
					BanKuaiGuanLi bkgldialog = new BanKuaiGuanLi(bkdbopt,stockdbopt,cylxmhandler);
					bkgldialog.startDialog();
					bkgldialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					bkgldialog.setModal(true);
					bkgldialog.setVisible(true);
					initializeChanYeLianXMLTree ();
				} catch (Exception eX) {
					eX.printStackTrace();
				}
			}
		});
		
	}
	

	private JButton okButton;
	private JButton cancelButton;
	private final JPanel contentPanel = new JPanel();
	private JButton btnLeftToRight;
	private JButton btnRightToLeft;
	private JButton btnBkGl;
	private JTable tableZdgzBankDetails;
	private JButton btnSave;
	private JMenuBar menuBar;
	private JMenu mnNewMenu;
	private JMenuItem mntmNewMenuItem;
	private JTree treechanyelian;
	private JTable tableCurZdgzbk;
	private JTextField tfldfindbk;
	private JButton btnadddalei;
	private JButton btndeldalei;
	private JButton buttonaddofficial;
	private JButton buttonremoveoffical;

	@SuppressWarnings("unchecked")
	private void initializeGui() 
	{
		setTitle("\u91CD\u70B9\u5173\u6CE8\u677F\u5757\u8BBE\u7F6E");
		setBounds(100, 100, 589, 691);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
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
		//scrollPane.setViewportView(tableZdgzBankDetails);
		
		JScrollPane scrollPane = new JScrollPane(tableZdgzBankDetails, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//		for (int i=0;i<bankuaidaleiname.length;i++) {
//			cbxDale.addItem(bankuaidaleiname[i]);
//		}
//		for (String str:daleidetailmap.keySet())
//				cbxDale.addItem(str);
		
		btnLeftToRight = new JButton("->");

		btnRightToLeft = new JButton("<-");

		btnBkGl = new JButton("\u677F\u5757\u5217\u8868\u7BA1\u7406");
		
		
		JScrollPane scrollPane_1 = new JScrollPane();
		
		JScrollPane scrollPane_2 = new JScrollPane();
		
		btnSave = new JButton("\u4FDD\u5B58\u91CD\u70B9\u5173\u6CE8\u677F\u5757");

		btnSave.setEnabled(false);
		
		tfldfindbk = new JTextField();
		tfldfindbk.setColumns(10);
		
		JButton button = new JButton("\u5B9A\u4F4D");
		
		btndeldalei = new JButton("\u5220\u9664\u5927\u7C7B");
		
		
		btnadddalei = new JButton("\u589E\u52A0\u5927\u7C7B");
		
		buttonaddofficial = new JButton("\u52A0\u5165\u91CD\u70B9\u5173\u6CE8");
		
		JLabel label = new JLabel("\u5019\u9009\u677F\u5757");
		
		buttonremoveoffical = new JButton("\u79FB\u9664\u91CD\u70B9\u5173\u6CE8");
		
		
		
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 231, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
										.addComponent(btnLeftToRight)
										.addComponent(btnRightToLeft)))
								.addComponent(label))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addComponent(buttonaddofficial)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(buttonremoveoffical))
								.addComponent(scrollPane_2, GroupLayout.PREFERRED_SIZE, 241, GroupLayout.PREFERRED_SIZE)))
						.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING, false)
							.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 538, GroupLayout.PREFERRED_SIZE)
							.addGroup(gl_contentPanel.createSequentialGroup()
								.addComponent(btnSave)
								.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(btnadddalei)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(btndeldalei)))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(btnBkGl)
							.addGap(18)
							.addComponent(tfldfindbk, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(button)))
					.addGap(15))
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnSave)
						.addComponent(btndeldalei)
						.addComponent(btnadddalei))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 222, GroupLayout.PREFERRED_SIZE)
					.addGap(14)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(label)
						.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
							.addComponent(buttonaddofficial)
							.addComponent(buttonremoveoffical)))
					.addGap(18)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(69)
							.addComponent(btnLeftToRight)
							.addGap(73)
							.addComponent(btnRightToLeft))
						.addComponent(scrollPane_2, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE)
						.addComponent(scrollPane_1, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 229, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnBkGl)
						.addComponent(tfldfindbk, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(button))
					.addContainerGap())
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
		scrollPane_2.setViewportView(tableCurZdgzbk);
		
		treechanyelian = new JTree();
		
		scrollPane_1.setViewportView(treechanyelian);
		
				
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("OK");
		
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				cancelButton = new JButton("Cancel");
	
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		mnNewMenu = new JMenu("\u64CD\u4F5C");
		menuBar.add(mnNewMenu);
		
		mntmNewMenuItem = new JMenuItem("\u751F\u6210\u901A\u8FBE\u4FE1\u4EE3\u7801");

		mnNewMenu.add(mntmNewMenuItem);
		
		this.setLocationRelativeTo(null);
		
	}
	

	
	private String formatDate(Date tmpdate)
	{
		SimpleDateFormat formatterhwy=new SimpleDateFormat("yy-MM-dd");
		return formatterhwy.format(tmpdate);
		//return formatterhwy;
	}
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



