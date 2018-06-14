package com.exchangeinfomanager.bankuaichanyelian;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.jfree.chart.ChartPanel;

import com.exchangeinfomanager.asinglestockinfo.AllCurrentTdxBKAndStoksTree;
import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTree;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.TreeRelated;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.asinglestockinfo.StockGivenPeriodDataItem;
import com.exchangeinfomanager.asinglestockinfo.StockOfBanKuai;
import com.exchangeinfomanager.asinglestockinfo.SubBanKuai;
import com.exchangeinfomanager.asinglestockinfo.SubGuPiaoChi;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiGeGuTable;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiGeGuTableModel;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiInfoTableModel;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.DisplayBkGgInfoEditorPane;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.common.io.LineProcessor;
import com.toedter.calendar.JDateChooser;



public class BanKuaiAndChanYeLianGUI2 <T extends BanKuaiAndChanYeLian2> extends JPanel 
{
	public BanKuaiAndChanYeLianGUI2 (T bkcyl1, StockInfoManager stockInfoManager1,AllCurrentTdxBKAndStoksTree bkstk1)
	{
		this.bkcyl = bkcyl1;
		this.stockInfoManager = stockInfoManager1;
		this.bkstk = bkstk1;
		
		bkdbopt = new BanKuaiDbOperation ();
		
		initializeGui ();
		updateInfoToGui ();
		
		createEvents ();
	}
	public static final int UP=0, LEFT=1, RIGHT=2, DOWN=3, NONE=4;
	
	private AllCurrentTdxBKAndStoksTree bkstk;
	private StockInfoManager stockInfoManager;
	private T bkcyl;
	private BkChanYeLianTree cyltree;
	protected BanKuaiDbOperation bkdbopt;
	private String currentselectedtdxbk = "";
	
	private void updateInfoToGui() 
	{
		cyltree = this.bkcyl.getBkChanYeLianTree();
		treeScrollPane.setViewportView(cyltree);
	}
	
	/*
     * 和选择板块相关的子产业链，个股 
     */
    private void getReleatedInfoAndActionsForTreePathNode (TreePath closestPath)
    {
    	TreePath[] treePaths = cyltree.getSelectionPaths();
    	BkChanYeLianTreeNode node = (BkChanYeLianTreeNode)cyltree.getLastSelectedPathComponent();
    	
    	String nodecode;
    	try{
    		 nodecode = node.getMyOwnCode();
    	} catch (java.lang.NullPointerException ex) {
    		return;
    	}
    	 
        if(!nodecode.equals(currentselectedtdxbk)) { //和当前的板块不一样，
        	if(node.getType() == BanKuaiAndStockBasic.GPC) { //找出板块州所属的板块国以及不在自己下面的所有板块
        		selectionActionsForGPC (node);
        	} else if (node.getType() == BanKuaiAndStockBasic.SUBGPC ){ //找出所有不在自己范围内的板块
        		selectionActionsForSubGPC (node);
        	}  else if (node.getType() == BanKuaiAndStockBasic.TDXBK ){ //找出所有在自己范围内的板块和个股
        		selectionActionsForBanKuai (node);
        	}  else if (node.getType() == BanKuaiAndStockBasic.SUBBK ){ //找出所有在自己范围内的板块和个股
        		selectionActionsForSubBanKuai (node);
        	}  else if (node.getType() == BanKuaiAndStockBasic.TDXGG || node.getType() == BanKuaiAndStockBasic.BKGEGU ){ //找出所有在自己范围内的板块和个股
        		selectionActionsForStocks (node);
        	}
        	
  	       	currentselectedtdxbk = node.getMyOwnCode();
         }
    }
	
	private void selectionActionsForStocks(BkChanYeLianTreeNode node) 
	{
		btnAddSubBk.setEnabled(false);
		//清空SUBGPC表
		HashMap<String, String> tmpsubgpc = new HashMap<String, String> ();
		((BanKuaiSubChanYeLianTableModel)(tablesubcyl.getModel())).refresh(tmpsubgpc,BanKuaiAndStockBasic.SUBGPC);
		((BanKuanOrStoksListTableModel)(tablebkgegu.getModel())).refresh(tmpsubgpc,BanKuaiAndStockBasic.TDXGG);
		
		tmpsubgpc = null;
	}
	/*
	 * 
	 */
	private void selectionActionsForSubBanKuai(BkChanYeLianTreeNode node) 
	{
		btnAddSubBk.setEnabled(false);
		
		BkChanYeLianTreeNode parent = getBanKuaiOfANode (node);
		
		//
		HashMap<String, String> tmpsubbk = bkdbopt.getSubBanKuai (parent.getMyOwnCode());
        ((BanKuaiSubChanYeLianTableModel)(tablesubcyl.getModel())).refresh(tmpsubbk,BanKuaiAndStockBasic.SUBBK);
		
//		BkChanYeLianTreeNode parent = (BkChanYeLianTreeNode)node.getParent();
		BanKuai bankuai = (BanKuai)bkstk.getAllBkStocksTree().getSpecificNodeByHypyOrCode(parent.getMyOwnCode(), BanKuaiAndStockBasic.TDXBK);
        if( bankuai.getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL) || bankuai.getBanKuaiLeiXing().equals(BanKuai.HASGGWITHSELFCJL) ) {
        //读出该板块当前所有的个股，读出的是本周在该板块内存在的所有个股，而不是当天在该板块存在的个股
	  	     LocalDate curselectdate = LocalDate.now();
	  	     bankuai = bkstk.getBanKuai(bankuai.getMyOwnCode(), curselectdate,StockGivenPeriodDataItem.WEEK);
	  	     bankuai = bkstk.getAllGeGuOfBanKuai ( bankuai,StockGivenPeriodDataItem.WEEK);
	  	     
	  	     Set<StockOfBanKuai> allbkge = bankuai.getSpecificPeriodBanKuaiGeGu(curselectdate,0,StockGivenPeriodDataItem.WEEK);
	  	     HashMap<String, String> tmpbknobelongs = new HashMap<String, String> ();
	  	     for(StockOfBanKuai bkgg : allbkge) {
	  	    	tmpbknobelongs.put(bkgg.getMyOwnCode(), bkgg.getMyOwnName());
	  	     }
	  	     ((BanKuanOrStoksListTableModel)(tablebkgegu.getModel())).refresh(tmpbknobelongs,BanKuaiAndStockBasic.TDXGG);
	  	     
	  	   tmpbknobelongs = null;
        }
		
        tmpsubbk = null;
	}
	private BkChanYeLianTreeNode getBanKuaiOfANode(BkChanYeLianTreeNode node) 
	{
		BkChanYeLianTreeNode parent = (BkChanYeLianTreeNode)node.getParent();
		if(parent.getType() == BanKuaiAndStockBasic.TDXBK)
			return parent; 
		else
			return getBanKuaiOfANode (parent);
	}

	/*
	 * 
	 */
	private void selectionActionsForBanKuai(BkChanYeLianTreeNode node) 
	{
		btnAddSubBk.setEnabled(true);
		
		//鼠标点击某个树，读出所属的通达信板块，在读出该板块的产业链子板块 
	       	HashMap<String, String> tmpsubbk = bkdbopt.getSubBanKuai (node.getMyOwnCode());
//	       	((BanKuaiSubChanYeLianTableModel)(tablesubcyl.getModel())).setRowCount(0);
	        ((BanKuaiSubChanYeLianTableModel)(tablesubcyl.getModel())).refresh(tmpsubbk,BanKuaiAndStockBasic.SUBBK);
	       
	        BanKuai bankuai = (BanKuai)bkstk.getAllBkStocksTree().getSpecificNodeByHypyOrCode(node.getMyOwnCode(), BanKuaiAndStockBasic.TDXBK);
	        if( bankuai.getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL) || bankuai.getBanKuaiLeiXing().equals(BanKuai.HASGGWITHSELFCJL) ) {
	        //读出该板块当前所有的个股，读出的是本周在该板块内存在的所有个股，而不是当天在该板块存在的个股
		  	     LocalDate curselectdate = LocalDate.now();
		  	     bankuai = bkstk.getBanKuai(bankuai.getMyOwnCode(), curselectdate,StockGivenPeriodDataItem.WEEK);
		  	     bankuai = bkstk.getAllGeGuOfBanKuai ( bankuai,StockGivenPeriodDataItem.WEEK);
		  	     
		  	     Set<StockOfBanKuai> allbkge = bankuai.getSpecificPeriodBanKuaiGeGu(curselectdate,0,StockGivenPeriodDataItem.WEEK);
		  	     HashMap<String, String> tmpbknobelongs = new HashMap<String, String> ();
		  	     for(StockOfBanKuai bkgg : allbkge) {
		  	    	tmpbknobelongs.put(bkgg.getMyOwnCode(), bkgg.getMyOwnName());
		  	     }
		  	     ((BanKuanOrStoksListTableModel)(tablebkgegu.getModel())).refresh(tmpbknobelongs,BanKuaiAndStockBasic.TDXGG);
	        }
	       	
// 	      读出该板块相关的新闻
 	    notesPane.displayNodeAllInfo(node);
// 	    createChanYeLianNewsHtml (curselectedbknode);
// 	    displayNodeBasicInfo(curselectedbknode);
	}
	/*
	 * 
	 */
	private void selectionActionsForSubGPC(BkChanYeLianTreeNode node) 
	{
		btnAddSubBk.setEnabled(false);
		//清空SUBGPC表
		HashMap<String, String> tmpsubgpc = new HashMap<String, String> ();
		((BanKuaiSubChanYeLianTableModel)(tablesubcyl.getModel())).refresh(tmpsubgpc,BanKuaiAndStockBasic.SUBGPC);
		
		//显示所有不在自己范围内的板块
		HashMap<String, String> tmpbknobelongs = new HashMap<String, String> ();
		HashSet<String> nodebkset = this.cyltree.getSpecificTypeNodesCodesSet (node.getMyOwnCode(),BanKuaiAndStockBasic.SUBGPC,BanKuaiAndStockBasic.TDXBK);
		if(nodebkset != null) {
			HashSet<String> allbkstset = this.bkstk.getAllBkStocksTree().getSpecificTypeNodesCodesSet ("000000",BanKuaiAndStockBasic.DAPAN,BanKuaiAndStockBasic.TDXBK);
			SetView<String> differencebk = Sets.difference(allbkstset,nodebkset ); //应该删除的
			 
			for(String bkcode : differencebk) {
						BkChanYeLianTreeNode tmpnode = this.bkstk.getAllBkStocksTree().getSpecificNodeByHypyOrCode(bkcode, BanKuaiAndStockBasic.TDXBK);
						tmpbknobelongs.put(bkcode, tmpnode.getMyOwnName());
			}
		}
		
		((BanKuanOrStoksListTableModel)(tablebkgegu.getModel())).refresh(tmpbknobelongs,BanKuaiAndStockBasic.TDXBK);
				
		tmpbknobelongs = null;
		tmpsubgpc = null;
	}
	/*
	 * 
	 */
	private void selectionActionsForGPC(BkChanYeLianTreeNode node) 
	{
		if(node.getMyOwnName().equals("其他")) {//其他是把所有没有加入GPC的板块放在这里，所以没必要特别显示她没有的。
			HashMap<String, String> tmpsubgpc = new HashMap<String, String>  ();
			((BanKuaiSubChanYeLianTableModel)(tablesubcyl.getModel())).refresh(tmpsubgpc,BanKuaiAndStockBasic.SUBGPC);
			((BanKuanOrStoksListTableModel)(tablebkgegu.getModel())).refresh(tmpsubgpc,BanKuaiAndStockBasic.TDXBK);
			tmpsubgpc = null;
			return;
		}
		
		btnAddSubBk.setEnabled(true);
		//显示所有子板块国
		HashMap<String, String> tmpsubgpc = bkdbopt.getSubGuPiaoChi (node.getMyOwnCode());
		((BanKuaiSubChanYeLianTableModel)(tablesubcyl.getModel())).refresh(tmpsubgpc,BanKuaiAndStockBasic.SUBGPC);
		
		//显示所有不在自己范围内的板块
		HashSet<String> nodebkset = this.cyltree.getSpecificTypeNodesCodesSet (node.getMyOwnCode(),BanKuaiAndStockBasic.GPC,BanKuaiAndStockBasic.TDXBK);
		HashSet<String> allbkstset = this.bkstk.getAllBkStocksTree().getSpecificTypeNodesCodesSet ("000000",BanKuaiAndStockBasic.DAPAN,BanKuaiAndStockBasic.TDXBK);
		SetView<String> differencebk = Sets.difference(allbkstset,nodebkset ); //应该删除的
		HashMap<String, String> tmpbknobelongs = new HashMap<String, String> (); 
		for(String bkcode : differencebk) {
			BkChanYeLianTreeNode tmpnode = this.bkstk.getAllBkStocksTree().getSpecificNodeByHypyOrCode(bkcode, BanKuaiAndStockBasic.TDXBK);
			tmpbknobelongs.put(bkcode, tmpnode.getMyOwnName());
		}
		((BanKuanOrStoksListTableModel)(tablebkgegu.getModel())).refresh(tmpbknobelongs,BanKuaiAndStockBasic.TDXBK);
		
		tmpbknobelongs = null;
		nodebkset = null;
		allbkstset = null;
	}
	
	public boolean saveCylXmlAndZdgzXml () //GEN-FIRST:event_saveButtonActionPerformed 
	{
		if(btnSaveAll.isEnabled() ) {
			BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)cyltree.getModel().getRoot();
			if(!bkcyl.saveChanYeLianTreeToXML(treeroot) ) {
				JOptionPane.showMessageDialog(null, "保存产业链XML失败！请查找原因。","Warning", JOptionPane.WARNING_MESSAGE);
				return false;
			}
			
//			if( !zdgzbkxmlhandler.saveAllZdgzbkToXml () ) {
//				JOptionPane.showMessageDialog(null, "保存重点关注股票池XML失败！请查找原因。","Warning", JOptionPane.WARNING_MESSAGE);
//				return false;
//			}
		}

		btnSaveAll.setEnabled(false);
		return true;
	}
	/*
	 * 
	 */
	private void createNewSubForGpcOrBanKuai ()
	{
		String title = "";

		TreePath closestPath = cyltree.getSelectionPath();
         BkChanYeLianTreeNode node = (BkChanYeLianTreeNode)closestPath.getLastPathComponent();
         String nodecode = node.getMyOwnCode();
         int nodetype = node.getType();
         if(nodetype == BanKuaiAndStockBasic.GPC) {
        	 title = "请输入板块国名称";
         }else 
         if( nodetype != BanKuaiAndStockBasic.TDXBK) {
        	 title = "请输入子板块名称";
         } else
        	 return;
         
        String newsubbk = JOptionPane.showInputDialog(null,title,"输入", JOptionPane.QUESTION_MESSAGE);
 		if(newsubbk == null)
 			return;
        
 		String newsubcylcode = null ;
 		if(nodetype == BanKuaiAndStockBasic.GPC) {
 			newsubcylcode = bkdbopt.addNewSubGuoPiaoChi (  nodecode,newsubbk.trim() );
        } else 
        if( nodetype != BanKuaiAndStockBasic.TDXBK) {
        	newsubcylcode = bkdbopt.addNewSubBanKuai (nodecode,newsubbk.trim() );
        } 
 		
		if(newsubcylcode != null)
			((BanKuaiSubChanYeLianTableModel)(tablesubcyl.getModel())).addRow(newsubcylcode,newsubbk);
		else
			JOptionPane.showMessageDialog(null,"添加失败，可能是因为重名！","Warning",JOptionPane.WARNING_MESSAGE);
	}
	/*
	 * 加子节点
	 */
	  private void addSubnodeButtonMouseMoved(java.awt.event.MouseEvent evt) //GEN-FIRST:event_addSubnodeButtonMouseMoved 
	  {
	        SubnodeButton button = (SubnodeButton) addSubnodeButton;
	        String key;
	        if (System.getProperty("os.name").startsWith("Mac OS X")) 
	        	key = "CMD";
	        else 
	        	key = "CTRL";
	        
	        int x = evt.getX();
	        //System.out.print("x=" + x + " ");
	        int y = evt.getY();
	        //System.out.print(y);
	        //System.out.print("y=" + y + " ");
	        if (y<19 && x+y<30 && x<19) {
	            button.setDirection(BanKuaiAndChanYeLianGUI2.UP);
	            button.setIcon(addAboveIcon);
	            button.setToolTipText("Add above ("+key+"-UP)");
	        }
	        else if (y>=19 && x-y < 0 && x<19){
	            button.setDirection(BanKuaiAndChanYeLianGUI2.DOWN);
	            button.setIcon(addBelowIcon);
	            button.setToolTipText("Add below ("+key+"-DOWN)");
	        }
	        else if (x+y>30 && x-y>0){
	            button.setDirection(BanKuaiAndChanYeLianGUI2.RIGHT);
	            button.setIcon(addChildIcon);
	            button.setToolTipText("Add subnode ("+key+"-RIGHT)");
	        }
	        else {
	            button.setDirection(BanKuaiAndChanYeLianGUI2.NONE);
	            button.setIcon(addSubnodeIcon);
	            button.setToolTipText("Add subnode");
	        }
	   }
	
	  private void addSubnodeButtonMouseExited(java.awt.event.MouseEvent evt) //GEN-FIRST:event_addSubnodeButtonMouseExited 
	  {
	        addSubnodeButton.setIcon(addSubnodeIcon);
	        addSubnodeButton.setToolTipText("Add subnode");
	  }
	  /*
	   * 
	   */
	  private void addSubnodeButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_addSubnodeButtonActionPerformed 
	  {
//		  cylneedsave = true;
			 
			 int direction = ((SubnodeButton)addSubnodeButton).getDirection();
			 int row = tablesubcyl.getSelectedRow() ;
			 if( row <0) {
				 JOptionPane.showMessageDialog(null,"请选择一个子板块!");
				 return;
			 }
			 
			 String subcode = ((BanKuaiSubChanYeLianTableModel)(tablesubcyl.getModel())).getSubChanYeLianCode(row);
			 String subname = ((BanKuaiSubChanYeLianTableModel)(tablesubcyl.getModel())).getSubChanYeLianName(row);
			 
			 this.addNewNode (((BanKuaiSubChanYeLianTableModel)tablesubcyl.getModel()).getTableNodeType(),subcode,subname,direction);
			 btnSaveAll.setEnabled(true);
			 
		}//GEN-LAST:event_addSubnodeButtonActionPerformed
	  	/*
	  	 * 添加个股节点
	  	 */
		private void addGeGuButtonMouseMoved(java.awt.event.MouseEvent evt) //GEN-FIRST:event_addSubnodeButtonMouseMoved 
	    {
	  	        SubnodeButton button = (SubnodeButton) addGeGuButton;
	  	        String key;
	  	        if (System.getProperty("os.name").startsWith("Mac OS X")) key = "CMD";
	  	        else key = "CTRL";
	  	        int x = evt.getX();
	  	        //System.out.print("x=" + x + " ");
	  	        int y = evt.getY();
	  	        //System.out.print(y);
	  	        //System.out.print("y=" + y + " ");
	  	        if (y<19 && x+y<30 && x<19) {
	  	            button.setDirection(BanKuaiAndChanYeLianGUI2.UP);
	  	            button.setIcon(addAboveIcon);
	  	            button.setToolTipText("Add above ("+key+"-UP)");
	  	        }
	  	        else if (y>=19 && x-y < 0 && x<19){
	  	            button.setDirection(BanKuaiAndChanYeLianGUI2.DOWN);
	  	            button.setIcon(addBelowIcon);
	  	            button.setToolTipText("Add below ("+key+"-DOWN)");
	  	        }
	  	        else if (x+y>30 && x-y>0){
	  	            button.setDirection(BanKuaiAndChanYeLianGUI2.RIGHT);
	  	            button.setIcon(addChildIcon);
	  	            button.setToolTipText("Add subnode ("+key+"-RIGHT)");
	  	        }
	  	        else {
	  	            button.setDirection(BanKuaiAndChanYeLianGUI2.NONE);
	  	            button.setIcon(addSubnodeIcon);
	  	            button.setToolTipText("Add subnode");
	  	        }
	  	    }
	    
	    
	    private void addGeGuButtonMouseExited(java.awt.event.MouseEvent evt)  
	    {
	    	addGeGuButton.setIcon(addSubnodeIcon);
	    	addGeGuButton.setToolTipText("添加个股");
	    }
	    
	    private void addGeGuButtonActionPerformed(java.awt.event.ActionEvent evt) 
	    {
//	    	cylneedsave = true;
	    	addGeGunode(((SubnodeButton)addGeGuButton).getDirection());
	    	btnSaveAll.setEnabled(true);
	    }
	    public void addGeGunode(int direction)
	    {
	    	int row = tablebkgegu.getSelectedRow();
	  		if(row <0) {
	  			JOptionPane.showMessageDialog(null,"请选择一个股票!","Warning",JOptionPane.WARNING_MESSAGE);
	  			return;
	  		}
	  		
	  		String gegucode = ((BanKuanOrStoksListTableModel)(tablebkgegu.getModel())).getStockCode(row);
	  		String geguname = ((BanKuanOrStoksListTableModel)(tablebkgegu.getModel())).getStockName(row);
	  		
	  		this.addNewNode ( ((BanKuanOrStoksListTableModel)(tablebkgegu.getModel())).getTableNodeType(),gegucode,geguname,direction);
	   }
	    /*
	     * 
	     */
	    private void addNewNode(int addnodetype, String subcode, String subname, int direction)
		{
			if (cyltree.getSelectionCount() == 1) {

				BkChanYeLianTreeNode newNode = null ;
				if(addnodetype == BanKuaiAndStockBasic.SUBGPC)
					newNode = new SubGuPiaoChi(subcode,subname);
				else if(addnodetype == BanKuaiAndStockBasic.TDXBK) { //把其他里面的该个股找出来
					newNode = cyltree.getSpecificNodeByHypyOrCode(subcode, BanKuaiAndStockBasic.TDXBK);
				} else if(addnodetype == BanKuaiAndStockBasic.SUBBK)
					newNode = new SubBanKuai(subcode,subname);
				else if(addnodetype == BanKuaiAndStockBasic.TDXGG)
					newNode = new Stock(subcode,subname);
				
				if(addnodetype == BanKuaiAndStockBasic.SUBGPC) { 
//					BkChanYeLianTreeNode parent = (BkChanYeLianTreeNode) this.getSelectionPath().getLastPathComponent();
//					if(parent.getType() == BanKuaiAndStockBasic.GPC)
						direction = BanKuaiAndChanYeLianGUI2.RIGHT; //板块国只能加在GPC板块州的下面
//					else if(parent.getType() == BanKuaiAndStockBasic.SUBGPC)
//						direction = BanKuaiAndChanYeLianGUI2.DOWN;
				} else
				if(addnodetype == BanKuaiAndStockBasic.SUBBK) { 
//					BkChanYeLianTreeNode parent = (BkChanYeLianTreeNode) this.getSelectionPath().getLastPathComponent();
//					if(parent.getType() == BanKuaiAndStockBasic.GPC)
						direction = BanKuaiAndChanYeLianGUI2.RIGHT; //子板块只能加在板块的下面
//					else if(parent.getType() == BanKuaiAndStockBasic.SUBGPC)
//						direction = BanKuaiAndChanYeLianGUI2.DOWN;
				} else
				if(addnodetype == BanKuaiAndStockBasic.TDXBK) { 
					BkChanYeLianTreeNode parent = (BkChanYeLianTreeNode) cyltree.getSelectionPath().getLastPathComponent();
					if(parent.getType() == BanKuaiAndStockBasic.GPC)
						direction = BanKuaiAndChanYeLianGUI2.RIGHT; //板块只能加在GPC板块州的下面
					else if(parent.getType() == BanKuaiAndStockBasic.TDXBK)
						direction = BanKuaiAndChanYeLianGUI2.DOWN; //板块不能加在板块下一级
				} else
				if(addnodetype == BanKuaiAndStockBasic.BKGEGU || addnodetype == BanKuaiAndStockBasic.TDXGG) {
					BkChanYeLianTreeNode parent = (BkChanYeLianTreeNode) cyltree.getSelectionPath().getLastPathComponent();
					if(parent.getType() == BanKuaiAndStockBasic.TDXBK)
						direction = BanKuaiAndChanYeLianGUI2.RIGHT; //个股只能加在板块的下面
					else if( parent.getType() == BkChanYeLianTreeNode.BKGEGU  ||  parent.getType() == BkChanYeLianTreeNode.TDXGG) { //父节点是个股不可以加
//						logger.debug("父节点是个股，不能有子板块");
	                	direction = BanKuaiAndChanYeLianGUI2.UP;
					}
				}
				
	            if (direction == BanKuaiAndChanYeLianGUI2.RIGHT){
	            	BkChanYeLianTreeNode parent = (BkChanYeLianTreeNode) cyltree.getSelectionPath().getLastPathComponent();
	                
	                boolean enableoperation = cyltree.checkNodeDuplicate (parent,newNode);
	            	if( enableoperation ) {
	                		JOptionPane.showMessageDialog(null,"同级中已经存在相同名称子版块或与父节点重名，不能重复添加!");
	                		return;
	                } 
	            	
	            	parent.add(newNode);
	            	try {
	            		TreeRelated tree = parent.getNodeTreerelated();
	            		tree.setExpansion(true);
	            	} catch(java.lang.NullPointerException e) {
//	            		e.printStackTrace();
	            	}
	            } 
	            
	            if (direction != BanKuaiAndChanYeLianGUI2.RIGHT){
	            	BkChanYeLianTreeNode currentNode = (BkChanYeLianTreeNode) cyltree.getSelectionPath().getLastPathComponent();
	            	BkChanYeLianTreeNode parent = (BkChanYeLianTreeNode) currentNode.getParent();
	                
	                boolean enableoperation = cyltree.checkNodeDuplicate (parent,newNode);
	            	if( enableoperation ) {
	                		JOptionPane.showMessageDialog(null,"同级中已经存在相同名称子版块或与父节点重名，不能重复添加!");
	                		return;
	                }
	                
	                int childIndex = parent.getIndex(currentNode);
	                if (direction == BanKuaiAndChanYeLianGUI2.DOWN) 
	                	parent.insert(newNode, childIndex+1);
	                else if (direction == BanKuaiAndChanYeLianGUI2.UP) 
	                	parent.insert(newNode, childIndex);
	                else
	                	return;
	            }

	            DefaultTreeModel treeModel = (DefaultTreeModel) cyltree.getModel();
	            treeModel.nodesWereInserted(newNode.getParent(), new int[] {newNode.getParent().getIndex(newNode)});
//	            cyltree.startEditingAtPath(new TreePath(newNode.getPath()));
	            
//	            TreePath bkpath = new TreePath(newNode.getPath());
//	            cyltree.setSelectionPath(bkpath);
//				cyltree.scrollPathToVisible(bkpath);
//				cyltree.expandTreePathAllNode(bkpath);
	            
				treeModel.reload();
				
				cyltree.setSelectionPath(new TreePath(((BkChanYeLianTreeNode)newNode.getParent()).getPath()));
				cyltree.scrollPathToVisible( new TreePath(newNode.getPath()));
//				cyltree.expandPath(new TreePath();
	        }
		}
	    /*
	     * 
	     */
		private boolean deleteNodes() 
		{
			BkChanYeLianTreeNode recylenode = cyltree.getSpecificNodeByHypyOrCode("GPC014", BanKuaiAndStockBasic.GPC);
			
			DefaultTreeModel treeModel = (DefaultTreeModel) cyltree.getModel();
			if (cyltree.getSelectionCount() > 0){
		            TreePath[] treePaths = cyltree.getSelectionPaths();
		            cyltree.sortPaths(cyltree,treePaths);
		            int topRow = cyltree.getRowForPath(treePaths[0]);
		            for (TreePath path : treePaths){
		            	BkChanYeLianTreeNode child = (BkChanYeLianTreeNode) path.getLastPathComponent();
//		            	if(child.getNodetreerelated().getInZdgzOfficalCount()>0 || child.getNodetreerelated().getInZdgzCandidateCount()>0) {
//		            		JOptionPane.showMessageDialog(null,"所选产业链是关注股票池候选或正式选择，请先在关注股票池中移除后再删除！","Warning",JOptionPane.WARNING_MESSAGE);
//		            		return false;
//		            	}

		            	BkChanYeLianTreeNode parent = (BkChanYeLianTreeNode) child.getParent();
		                if (parent != null){
		                    int childIndex = parent.getIndex(child);
		                    if(child.getType() == BkChanYeLianTreeNode.SUBGPC) { 
		                    	int nodechildcount = child.getChildCount();
		                    	if(nodechildcount >0 ) { //SUBGPC只能链接板块，如果有板块，要move 到其他
		                    		for(int i=0;i <nodechildcount; i++) {
		                    			BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode) this.cyltree.getModel().getChild(child, 0);
		                    			recylenode.add(childnode);
		                    		}
		                    	}
		                    	parent.remove(child);	
		                    }  else if(child.getType() == BkChanYeLianTreeNode.TDXBK) { //move 到其他
			            		recylenode.add(child);
			            	} else
			            		parent.remove(child);
		                    
		                    treeModel.nodesWereRemoved(parent, new int[] {childIndex}, new Object[] {child});
		                    if (parent.getChildCount() == 0) 
		                    	parent.getNodeTreerelated().setExpansion(false);
		                }
		            }
		            
		            if (cyltree.getVisibleRowCount() > 0) 
		            	cyltree.setSelectionRow(topRow);
		   }
		
		   treeModel.reload();
		   return true;
		}
	/*
    * 产业链树上定位用户选择的板块，只选择到板块一级，子板块不找
    */
   private void findBanKuaiInTree(String bkinputed) 
   {
	   BkChanYeLianTreeNode findnode = cyltree.getSpecificNodeByHypyOrCode (bkinputed,BkChanYeLianTreeNode.TDXBK);
		TreePath bkpath = new TreePath(findnode.getPath());
		if(bkpath != null) {
			cyltree.setSelectionPath(bkpath);
			cyltree.scrollPathToVisible(bkpath);
			cyltree.expandTreePathAllNode(bkpath);
		}

		selectionActionsForBanKuai (findnode);
	}

	/*
	 * 
	 */
	private void createEvents() 
	{
		btnAddSubBk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				String newsubbk = JOptionPane.showInputDialog(null,"请输入子板块名称:","增加子版块", JOptionPane.QUESTION_MESSAGE);
				if(newsubbk == null)
					return;
				
//				if(bkdbopt.getSysBkSet().contains(newsubbk) ) {
//					JOptionPane.showMessageDialog(null,"输入子版块名称与通达信板块名称冲突,请重新输入!");
//					return ;
//				}
				
				TreePath closestPath = cyltree.getSelectionPath();
//		        logger.debug(closestPath);
		         BkChanYeLianTreeNode tdxbk = (BkChanYeLianTreeNode)closestPath.getLastPathComponent();
		         if(tdxbk.getType() != BanKuaiAndStockBasic.TDXBK)
		        	 return;
		         
		        String tdxbkcode = tdxbk.getMyOwnCode();
				String newsubcylcode = bkdbopt.addNewSubBanKuai (tdxbkcode,newsubbk.trim() ); 
				if(newsubcylcode != null)
					((BanKuaiSubChanYeLianTableModel)(tablesubcyl.getModel())).addRow(newsubcylcode,newsubbk);
				else
					JOptionPane.showMessageDialog(null,"添加失败，可能是因为重名！","Warning",JOptionPane.WARNING_MESSAGE);
//	  	        ((BanKuaiSubChanYeLianTableModel)(tablesubcyl.getModel())).fireTableDataChanged ();
				
			}
		});
		
		tfldfindgegu.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				String nodeid = tfldfindgegu.getText().trim();
				int rowindex = ((BanKuanOrStoksListTableModel)(tablebkgegu.getModel())).getNodeLineIndex(nodeid);
				
				if(rowindex != -1) {
					tablebkgegu.setRowSelectionInterval(rowindex, rowindex);
					tablebkgegu.scrollRectToVisible(new Rectangle(tablebkgegu.getCellRect(rowindex, 0, true)));
				} else 	{
					JOptionPane.showMessageDialog(null,"股票/板块代码有误或名称拼音有误！","Warning", JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		btnfindgegu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String nodeid = tfldfindgegu.getText().trim();
				int rowindex = ((BanKuanOrStoksListTableModel)(tablebkgegu.getModel())).getNodeLineIndex(nodeid);
				
				if(rowindex != -1) {
					tablebkgegu.setRowSelectionInterval(rowindex, rowindex);
					tablebkgegu.scrollRectToVisible(new Rectangle(tablebkgegu.getCellRect(rowindex, 0, true)));
				} else 	{
					JOptionPane.showMessageDialog(null,"股票/板块代码有误或名称拼音有误！","Warning", JOptionPane.WARNING_MESSAGE);
				}

			}
		});
		
		tfldfindbk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				tfldfindbk.setText("");
			}
		});
		
		/*
		 * 查找板块
		 */
		tfldfindbk.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				String bkinputed = tfldfindbk.getText();
				findBanKuaiInTree (bkinputed);
			}
		});
		
		btnfindbk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				String bkinputed = tfldfindbk.getText();
				findBanKuaiInTree (bkinputed);
			}
		});
		
		addGeGuButton.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
            	addGeGuButtonMouseMoved(evt);
            }
        });

		addGeGuButton.addMouseListener(new java.awt.event.MouseAdapter() {
	            public void mouseExited(java.awt.event.MouseEvent evt) {
	            	addGeGuButtonMouseExited(evt);
	            }
	        });
		addGeGuButton.addActionListener(new java.awt.event.ActionListener() {
	            public void actionPerformed(java.awt.event.ActionEvent evt) {
	                addGeGuButtonActionPerformed(evt);
	            }
	        });

		addSubnodeButton.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                addSubnodeButtonMouseMoved(evt);
            }
        });
        addSubnodeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                addSubnodeButtonMouseExited(evt);
            }
        });
        addSubnodeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addSubnodeButtonActionPerformed(evt);
            }
        });
        
		btnAddSubBk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				createNewSubForGpcOrBanKuai ();
			}
		});
		
		btnSaveAll.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				saveCylXmlAndZdgzXml ();
			}
		});
		
		deleteButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
//				TreePath closestPath = cyltree.getClosestPathForLocation(arg0.getX(), arg0.getY());
//		    	cyltree.deleteNodes(closestPath);
				
				deleteNodes ();
			}
		});
		
		cyltree.addMouseListener(new java.awt.event.MouseAdapter() {
	            public void mousePressed(java.awt.event.MouseEvent evt) {
//	            	chanYeLianTreeMousePressed(evt);
//	            	logger.debug("get action notice at bkcyl");
	    	        TreePath closestPath = cyltree.getClosestPathForLocation(evt.getX(), evt.getY());

	    	        if(closestPath != null) {
	    	            Rectangle pathBounds = cyltree.getPathBounds(closestPath);
	    	            int maxY = (int) pathBounds.getMaxY();
	    	            int minX = (int) pathBounds.getMinX();
	    	            int maxX = (int) pathBounds.getMaxX();
	    	            if (evt.getY() > maxY) 
	    	            	cyltree.clearSelection();
	    	            else if (evt.getX() < minX || evt.getX() > maxX) 
	    	            	cyltree.clearSelection();
	    	        }
	    	        getReleatedInfoAndActionsForTreePathNode ( closestPath);
	            }
	        });
		
	}
	
//	private JTextField tfldparsefilename;
	private JTable tableCurZdgzbk;
	private JTable tableZdgzBankDetails;
	private JTable tablebkgegu;
	private JTextField tfldfindbk;
	private JTextField tfldfindgegu;
	private JButton btnCylAddToZdgz;
	private JButton btnCylRemoveFromZdgz;
	private JButton deleteButton;
	private JButton btnfindbk;
	private JButton btnfindgegu;
	private JScrollPane scrollPanegegu;
	private JButton btnAddSubBk;
	private JButton buttonaddofficial;
	private JButton buttonremoveoffical;
//	private JButton btnChsParseFile;
	private JButton addGeGuButton;
	private JButton addSubnodeButton;
	private JButton btnGenTDXCode;
	private JButton btnadddalei;
	private JButton btndeldalei;
	ImageIcon addBelowIcon, addAboveIcon, addChildIcon, addSubnodeIcon;
	private JScrollPane notesScrollPane;
	private JScrollPane treeScrollPane;
	private JSplitPane jSplitPane;
	private JButton btnopenzdgzxml;
	private JButton btnopencylxml;
	private JPopupMenu popupMenu;
	private JMenuItem mntmNewMenuItem;
	private JTable tablesubcyl;
	private DisplayBkGgInfoEditorPane notesPane;
	private JButton btnSaveAll;
//	private JButton buttonCjlFx;
//	private ChartPanel barchartPanel; //chart 
//	private ChartPanel piechartPanel; //chart
//	private JScrollPane sclpBanKuaiZhanBi;
//	private JScrollPane sclpGeGuZhanBi;
//	private JDateChooser dchgeguwkzhanbi;
//	private JButton btndisplaybkfx;
//	private BanKuaiFengXiBarChartPnl bkfxpnl ;
//	private BanKuaiFengXiPieChartPnl pnlGeGuZhanBi;
//	private JTextField tfldquanzhong;
//	private JCheckBox cbxtichuquanzhong;
//	private JMenuItem mntmgephi;
//	private JMenuItem menuItemaddgz;
//	private JMenuItem menuItemredian;

	

	private void initializeGui() 
	{
		
		JPanel panelzdgz = new JPanel();
		
		JPanel panelcyltree = new JPanel();
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(panelzdgz, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 893, Short.MAX_VALUE)
						.addComponent(panelcyltree, Alignment.LEADING, 0, 0, Short.MAX_VALUE))
					.addGap(4))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(0)
					.addComponent(panelzdgz, GroupLayout.PREFERRED_SIZE, 296, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panelcyltree, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		
		btnCylAddToZdgz = new JButton("\u52A0\u5165\u91CD\u70B9\u5173\u6CE8");
		
		btnCylRemoveFromZdgz = new JButton("\u79FB\u9664\u91CD\u70B9\u5173\u6CE8");
		
		addSubnodeButton = new SubnodeButton();
		addSubnodeButton.setIcon(new ImageIcon(BanKuaiAndChanYeLian2.class.getResource("/images/subnode24.png")));
		
		addGeGuButton = new SubnodeButton();
		addGeGuButton.setIcon(new ImageIcon(BanKuaiAndChanYeLian2.class.getResource("/images/subnode24.png")));
		
		btnAddSubBk = new JButton("\u589E\u52A0\u5B50\u677F\u5757");
		JScrollPane scrollPanesubbk = new JScrollPane();
		
		scrollPanegegu = new JScrollPane();
		
		jSplitPane = new JSplitPane();
		jSplitPane.setResizeWeight(0.36);
		
		deleteButton = new JButton("\u5220\u9664\u8282\u70B9");
		deleteButton.setIcon(null);
		
		tfldfindbk = new JTextField();
		tfldfindbk.setColumns(10);
		
		btnfindbk = new JButton("\u5B9A\u4F4D\u677F\u5757");
		
		tfldfindgegu = new JTextField();
		tfldfindgegu.setColumns(10);
		
		btnfindgegu = new JButton("\u5B9A\u4F4D\u4E2A\u80A1");
		
		btnopencylxml = new JButton("\u6253\u5F00XML");
		
		btnSaveAll = new JButton("\u4FDD\u5B58\u6240\u6709\u4FEE\u6539");
		btnSaveAll.setForeground(Color.RED);
		
		
		GroupLayout gl_panelcyltree = new GroupLayout(panelcyltree);
		gl_panelcyltree.setHorizontalGroup(
			gl_panelcyltree.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelcyltree.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING, false)
						.addGroup(gl_panelcyltree.createSequentialGroup()
							.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panelcyltree.createSequentialGroup()
									.addComponent(deleteButton)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(tfldfindbk, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btnfindbk)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(btnopencylxml))
								.addComponent(jSplitPane, GroupLayout.PREFERRED_SIZE, 504, GroupLayout.PREFERRED_SIZE))
							.addGap(12))
						.addGroup(gl_panelcyltree.createSequentialGroup()
							.addComponent(btnCylAddToZdgz)
							.addGap(10)
							.addComponent(btnCylRemoveFromZdgz)
							.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(btnSaveAll)
							.addGap(28)))
					.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelcyltree.createSequentialGroup()
							.addComponent(addGeGuButton, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panelcyltree.createSequentialGroup()
									.addComponent(tfldfindgegu, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btnfindgegu))
								.addComponent(scrollPanegegu, GroupLayout.PREFERRED_SIZE, 301, GroupLayout.PREFERRED_SIZE)))
						.addGroup(gl_panelcyltree.createSequentialGroup()
							.addComponent(addSubnodeButton, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING)
								.addComponent(scrollPanesubbk, GroupLayout.PREFERRED_SIZE, 301, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnAddSubBk))))
					.addContainerGap(11, Short.MAX_VALUE))
		);
		gl_panelcyltree.setVerticalGroup(
			gl_panelcyltree.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelcyltree.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelcyltree.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnCylRemoveFromZdgz)
						.addComponent(btnCylAddToZdgz)
						.addComponent(btnAddSubBk)
						.addComponent(btnSaveAll))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelcyltree.createSequentialGroup()
							.addComponent(scrollPanesubbk, GroupLayout.PREFERRED_SIZE, 127, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(scrollPanegegu, 0, 0, Short.MAX_VALUE))
						.addGroup(gl_panelcyltree.createSequentialGroup()
							.addGap(69)
							.addComponent(addSubnodeButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGap(186)
							.addComponent(addGeGuButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addComponent(jSplitPane, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 415, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelcyltree.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING)
							.addGroup(gl_panelcyltree.createParallelGroup(Alignment.BASELINE)
								.addComponent(tfldfindbk, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnfindbk)
								.addComponent(btnopencylxml, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
							.addGroup(gl_panelcyltree.createParallelGroup(Alignment.BASELINE)
								.addComponent(tfldfindgegu, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnfindgegu)))
						.addComponent(deleteButton, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
					.addGap(13))
		);
		
		BanKuaiSubChanYeLianTableModel subcylmode = new BanKuaiSubChanYeLianTableModel ();
		tablesubcyl = new JTable(subcylmode)
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
		scrollPanesubbk.setViewportView(tablesubcyl);
		
		treeScrollPane = new JScrollPane();
		jSplitPane.setLeftComponent(treeScrollPane);
		
//		treeScrollPane.setViewportView(treechanyelian);
		treeScrollPane.grabFocus();
		treeScrollPane.getVerticalScrollBar().setValue(0);
		
		
		notesScrollPane = new JScrollPane();
		jSplitPane.setRightComponent(notesScrollPane);
		
		notesPane = new DisplayBkGgInfoEditorPane();
		notesPane.setClearContentsBeforeDisplayNewInfo(true);
		
//		notesPane.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
//		notesPane.setEditable(false);
		notesScrollPane.setViewportView(notesPane);
		
		
		BanKuanOrStoksListTableModel bkstmodel = new BanKuanOrStoksListTableModel ();
		tablebkgegu = new  JTable(bkstmodel)
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
		
//		
//		//个股table也可以加个股新闻
//		JPopupMenu popupMenuGeguNews = new JPopupMenu();
//		JMenuItem menuItemAddNews = new JMenuItem("添加个股新闻");
//		JMenuItem menuItemMakeLongTou = new JMenuItem("设置股票权重");
//		popupMenuGeguNews.add(menuItemAddNews);
//		popupMenuGeguNews.add(menuItemMakeLongTou);
//		tablebkgegu.setComponentPopupMenu(popupMenuGeguNews);
//		menuItemAddNews.addActionListener(new ActionListener() {
//			@Override
//
//			public void actionPerformed(ActionEvent evt) {
//
//				addGeGuNews ();
//			}
//			
//		});
//		menuItemMakeLongTou.setComponentPopupMenu(popupMenuGeguNews);
//		menuItemMakeLongTou.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent evt) {
//				setGeGuWeightInBanKuai ();
//			}
//			
//		});

		panelcyltree.setLayout(gl_panelcyltree);
		
		btnGenTDXCode = new JButton("\u751F\u6210TDX\u4EE3\u7801");
		
		JScrollPane scrollPaneDaLeiDetail = new JScrollPane();
		
		buttonaddofficial = new JButton("->");
		
		buttonremoveoffical = new JButton("<-");
		
		JScrollPane scrollPaneDaLei = new JScrollPane();
		
		btnadddalei = new JButton("\u589E\u52A0\u80A1\u7968\u6C60");
				
		btndeldalei = new JButton("\u5220\u9664\u80A1\u7968\u6C60");
		
		
		JSeparator separator_1 = new JSeparator();
		
		btnopenzdgzxml = new JButton("\u6253\u5F00XML");
		
		GroupLayout gl_panelzdgz = new GroupLayout(panelzdgz);
		gl_panelzdgz.setHorizontalGroup(
			gl_panelzdgz.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panelzdgz.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelzdgz.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelzdgz.createSequentialGroup()
							.addGroup(gl_panelzdgz.createParallelGroup(Alignment.LEADING)
								.addComponent(scrollPaneDaLeiDetail, GroupLayout.PREFERRED_SIZE, 306, GroupLayout.PREFERRED_SIZE)
								.addGroup(gl_panelzdgz.createSequentialGroup()
									.addComponent(btnGenTDXCode)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btnopenzdgzxml)))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panelzdgz.createParallelGroup(Alignment.LEADING)
								.addComponent(buttonremoveoffical)
								.addComponent(buttonaddofficial))
							.addGroup(gl_panelzdgz.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panelzdgz.createSequentialGroup()
									.addGap(22)
									.addComponent(btnadddalei)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btndeldalei))
								.addGroup(gl_panelzdgz.createSequentialGroup()
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(scrollPaneDaLei, GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE))))
						.addComponent(separator_1, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 873, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_panelzdgz.setVerticalGroup(
			gl_panelzdgz.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelzdgz.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelzdgz.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_panelzdgz.createSequentialGroup()
							.addGroup(gl_panelzdgz.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnadddalei)
								.addComponent(btndeldalei)
								.addComponent(btnGenTDXCode)
								.addComponent(btnopenzdgzxml))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panelzdgz.createParallelGroup(Alignment.LEADING)
								.addComponent(scrollPaneDaLei, 0, 0, Short.MAX_VALUE)
								.addComponent(scrollPaneDaLeiDetail, GroupLayout.PREFERRED_SIZE, 242, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED))
						.addGroup(gl_panelzdgz.createSequentialGroup()
							.addComponent(buttonaddofficial)
							.addGap(77)
							.addComponent(buttonremoveoffical)
							.addGap(76)))
					.addComponent(separator_1, GroupLayout.PREFERRED_SIZE, 2, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		
		//ZdgzBanKuaiDetailXmlTableModel xmlaccountsmodel = new ZdgzBanKuaiDetailXmlTableModel( );
		//tableZdgzBankDetails = new  JTable(xmlaccountsmodel)
		CurZdgzBanKuaiTableModel curzdgzbkmodel = new CurZdgzBanKuaiTableModel ();
		tableZdgzBankDetails = new  JTable(curzdgzbkmodel)
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
			
			public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
				
				Border outside = new MatteBorder(1, 0, 1, 0, Color.RED);
				Border inside = new EmptyBorder(0, 1, 0, 1);
				Border highlight = new CompoundBorder(outside, inside);
				 
		        Component comp = super.prepareRenderer(renderer, row, col);
		        JComponent jc = (JComponent)comp;
		        CurZdgzBanKuaiTableModel tablemodel = (CurZdgzBanKuaiTableModel)this.getModel(); 
		        Object value = tablemodel.getValueAt(row, col);
		        
		        if(tablemodel.shouldRemovedNodeWhenSaveXml(row) && col == 0) {
		       	 	Font font=new Font("黑体",Font.BOLD + Font.ITALIC,14); 
		        	comp.setFont(font);
		        }
		        
		        return comp;
		    }
		};
		tableZdgzBankDetails.setToolTipText("tableZdgzBankDetails");
		int preferedwidth = 170;
		tableZdgzBankDetails.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(preferedwidth);
		tableZdgzBankDetails.getTableHeader().getColumnModel().getColumn(0).setMinWidth(preferedwidth);
		tableZdgzBankDetails.getTableHeader().getColumnModel().getColumn(0).setWidth(preferedwidth);
		tableZdgzBankDetails.getTableHeader().getColumnModel().getColumn(0).setPreferredWidth(preferedwidth);

		scrollPaneDaLeiDetail.setViewportView(tableZdgzBankDetails);
		
		
		
//		CurZdgzBanKuaiTableModel curzdgzbkmodel = new CurZdgzBanKuaiTableModel (); 
//		tableCurZdgzbk = new  JTable(curzdgzbkmodel) {
		ZdgzBanKuaiDetailXmlTableModel xmlaccountsmodel = new ZdgzBanKuaiDetailXmlTableModel( );
		tableCurZdgzbk = new  JTable(xmlaccountsmodel) {
			private static final long serialVersionUID = 1L;

			public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
				
				Border outside = new MatteBorder(1, 0, 1, 0, Color.RED);
				Border inside = new EmptyBorder(0, 1, 0, 1);
				Border highlight = new CompoundBorder(outside, inside);
				 
		        Component comp = super.prepareRenderer(renderer, row, col);
		        JComponent jc = (JComponent)comp;
		        ZdgzBanKuaiDetailXmlTableModel tablemodel = (ZdgzBanKuaiDetailXmlTableModel)this.getModel(); 
		        Object value = tablemodel.getValueAt(row, col);
		        
//		        if (!isRowSelected(row)) {
//		        	comp.setBackground(this.getBackground()); 
//		        	int modelRow = convertRowIndexToModel(row);
//		        	String chanyelian = (String)getModel().getValueAt(modelRow, 1);
//					if(chanyelian != null && !currentselectedtdxbk.equals("") && chanyelian.contains(currentselectedtdxbk)) 
//						jc.setBorder( highlight );
//		        }
//		        
//		        if (isRowSelected(row)) {
//		        	comp.setBackground(this.getSelectionBackground());
//		        	
//		        	int modelRow = convertRowIndexToModel(row);
//		        	String chanyelian = (String)getModel().getValueAt(modelRow, 1);
//					if(chanyelian != null && !currentselectedtdxbk.equals("") && chanyelian.contains(currentselectedtdxbk)) 
//						jc.setBorder( highlight );
//		        	
//		        }
		        
		        if(tablemodel.hasShouldRemovedNodeWhenSaveXml(row) && col == 0) {
		       	 	Font font=new Font("黑体",Font.BOLD + Font.ITALIC,14); 
		        	comp.setFont(font);
		        }
		        return comp;
		    }
			

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
		tableCurZdgzbk.setToolTipText("tableZdgzBankDetails");
		
		
		tableCurZdgzbk.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(80);
		tableCurZdgzbk.getTableHeader().getColumnModel().getColumn(0).setMinWidth(80);
		tableCurZdgzbk.getTableHeader().getColumnModel().getColumn(0).setWidth(80);
		tableCurZdgzbk.getTableHeader().getColumnModel().getColumn(0).setPreferredWidth(80);
		tableCurZdgzbk.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(390);
		tableCurZdgzbk.getTableHeader().getColumnModel().getColumn(1).setMinWidth(390);
		tableCurZdgzbk.getTableHeader().getColumnModel().getColumn(1).setWidth(390);
		tableCurZdgzbk.getTableHeader().getColumnModel().getColumn(1).setPreferredWidth(390);
//		tableZdgzBankDetails.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//		tableZdgzBankDetails.setPreferredScrollableViewportSize(java.awt.Toolkit.getDefaultToolkit().getScreenSize());
		scrollPaneDaLei.setViewportView(tableCurZdgzbk);
		panelzdgz.setLayout(gl_panelzdgz);
//		setLayout(groupLayout);
		
		
		
		java.util.ArrayList<java.awt.Image> imageList = new java.util.ArrayList<java.awt.Image>();
        imageList.add(new javax.swing.ImageIcon(getClass().getResource("/images/ginkgo16.png")).getImage());
        imageList.add(new javax.swing.ImageIcon(getClass().getResource("/images/ginkgo18.png")).getImage());
        imageList.add(new javax.swing.ImageIcon(getClass().getResource("/images/ginkgo20.png")).getImage());
        imageList.add(new javax.swing.ImageIcon(getClass().getResource("/images/ginkgo24.png")).getImage());
        imageList.add(new javax.swing.ImageIcon(getClass().getResource("/images/ginkgo32.png")).getImage());
        imageList.add(new javax.swing.ImageIcon(getClass().getResource("/images/ginkgo36.png")).getImage());
        
        //setIconImages(imageList);
        addBelowIcon = new javax.swing.ImageIcon(getClass().getResource("/images/subnodeBelow24.png"));
        addAboveIcon = new javax.swing.ImageIcon(getClass().getResource("/images/subnodeAbove24.png"));
        addSubnodeIcon = new javax.swing.ImageIcon(getClass().getResource("/images/subnode24.png"));
        addChildIcon = new javax.swing.ImageIcon(getClass().getResource("/images/subnodeChild24.png"));
        
        //tree 的弹出菜单
//        popupMenu = new BanKuaiPopUpMenu(this.stockInfoManager,treechanyelian);
//		addPopup(treechanyelian, popupMenu);
	}


	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}


/*
 * 12个大类某个具体大类的关注内容表
 */
class CurZdgzBanKuaiTableModel extends AbstractTableModel 
{
	private HashMap<String,ArrayList<BkChanYeLianTreeNode>> gzbkmap;
	private String cbxDale;

	String[] jtableTitleStrings = {  "板块产业链","创建时间","入选","发现"};
	
	CurZdgzBanKuaiTableModel ()
	{

	}

	public boolean shouldRemovedNodeWhenSaveXml(int row){
		BkChanYeLianTreeNode bkcyltn = this.getGuanZhuBanKuaiInfo(row);
		if(bkcyltn.getNodeTreerelated().shouldBeRemovedWhenSaveXml())
			return true;
		return false;
	}

	public void refresh (HashMap<String,ArrayList<BkChanYeLianTreeNode>> zdgzbkmap2,String cbxDale2) 
	{
		this.gzbkmap =  zdgzbkmap2;
		this.cbxDale = cbxDale2;
		this.fireTableDataChanged();
	}
	public int getGuanZhuBanKuaiInfoIndex (BkChanYeLianTreeNode parent)
	{
		String currentdalei = this.cbxDale;
		ArrayList<BkChanYeLianTreeNode> tmpgzbkinfo = this.gzbkmap.get(currentdalei);
		
		int findsimilarnode = -1;
		
		for(int i=0;i<tmpgzbkinfo.size();i++) {
			String cylinmapnode = tmpgzbkinfo.get(i).getNodeTreerelated().getNodeCurLocatedChanYeLian().trim();
			String cylinverfiednode = parent.getNodeTreerelated().getNodeCurLocatedChanYeLian().trim();

			if(cylinmapnode.equals(cylinverfiednode) ) 
				return i;
			else if(cylinmapnode.contains(cylinverfiednode) )
				findsimilarnode = i;
		}

		return findsimilarnode;
	}
	public BkChanYeLianTreeNode getGuanZhuBanKuaiInfo (int rowindex)
	{
		String currentdalei = this.cbxDale;
		ArrayList<BkChanYeLianTreeNode> tmpgzbkinfo = this.gzbkmap.get(currentdalei);
		try {
			return tmpgzbkinfo.get(rowindex);
		} catch (java.lang.IndexOutOfBoundsException ex) {
			return null;
		}
	}

	 public int getRowCount() 
	 {
		 try {
			 String currentdalei = this.cbxDale;  
			 ArrayList<BkChanYeLianTreeNode> tmpgzbklist = gzbkmap.get(currentdalei);
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
			ArrayList<BkChanYeLianTreeNode> tmpgzbklist = gzbkmap.get(currentdalei);
			if(tmpgzbklist == null)
				return null;
			BkChanYeLianTreeNode tmpgzbk = tmpgzbklist.get(rowIndex);
	    	
	    	Object value = "??";

	    	switch (columnIndex) {
            case 0:
                value = tmpgzbk.getNodeTreerelated().getNodeCurLocatedChanYeLianByName();
                break;
            case 1:
            	try {
            		value = tmpgzbk.getNodeTreerelated().getSelectedToZdgzTime();
            	} catch (java.lang.NullPointerException e) {
            		value = "";
            	}
            	
                break;
            case 2:
                value = new Boolean(tmpgzbk.getNodeTreerelated().isOfficallySelected() );
                break;
            case 3:
            	if(tmpgzbk.getNodeTreerelated().getParseFileStockSet() != null && tmpgzbk.getNodeTreerelated().getParseFileStockSet().size() >0)
            		value = new Boolean(true );
            	else 
            		value = new Boolean(false );
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
		        case 3:
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
			this.cbxDale = "";
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
	private HashMap<String, ArrayList<BkChanYeLianTreeNode>> gzbkmap;
	
	private String[] jtableTitleStrings = { "股票池", "关注板块","发现"};
	private ArrayList<String> gzdalei;
	private boolean foundstockinparsefile = false;
	
	ZdgzBanKuaiDetailXmlTableModel ()
	{
	}

	public void refresh (HashMap<String,ArrayList<BkChanYeLianTreeNode>> zdgzbkmap) 
	{
		this.gzbkmap =  zdgzbkmap;
		this.gzdalei = new ArrayList<String>(zdgzbkmap.keySet() );
		this.fireTableDataChanged();
	}

	 public int getRowCount() 
	 {
		 try {
			 return gzdalei.size();
		 } catch (java.lang.NullPointerException e) {
			 return 0;
		 }
	 }
	 
	 public String getZdgzDaLei (int row)
	 {
		 return (String)gzdalei.get(row);
	 }
	 public int getDaLeiIndex (String dalei)
	 {
		 return this.gzdalei.indexOf(dalei);
	 }
	 public ArrayList<BkChanYeLianTreeNode>  getDaLeiChanYeLianList (String dalei)
	 {
		 return gzbkmap.get(dalei) ;
	 }
	 public boolean hasShouldRemovedNodeWhenSaveXml (int rowindex)
	 {
		 boolean hasnode = false;
		String dalei = (String)gzdalei.get(rowindex);
		ArrayList<BkChanYeLianTreeNode> daleidetail = gzbkmap.get(dalei) ;
		for(BkChanYeLianTreeNode tmpbkcyltn : daleidetail)
			if(tmpbkcyltn.getNodetreerelated().shouldBeRemovedWhenSaveXml())
				hasnode = true;
		
		return hasnode;
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
            		ArrayList<BkChanYeLianTreeNode> zdgzsub = this.getDaLeiChanYeLianList(this.getZdgzDaLei(rowIndex));
            		if(zdgzsub.size() == 0)
            			foundstockinparsefile = false;
            		
            		for(BkChanYeLianTreeNode gznode : zdgzsub) {
            			if(gznode.getNodetreerelated().isOfficallySelected()) {
            				String chanyelian = gznode.getNodetreerelated().getNodeCurLocatedChanYeLianByName(); 
            				
            				String seltime = "";
            				if(gznode.getNodetreerelated().getSelectedToZdgzTime() != null)
            					seltime = gznode.getNodetreerelated().getSelectedToZdgzTime();
            				result = result + chanyelian + "(" + seltime +")" + " | " + " ";
            				
            				if(gznode.getNodetreerelated().getParseFileStockSet() != null && gznode.getNodetreerelated().getParseFileStockSet().size() > 0)
            					foundstockinparsefile = true;
            				else
            					foundstockinparsefile = false;
            			}
            		}

            	 } catch (java.lang.NullPointerException e) {
            		 e.printStackTrace();
            	 }
            	
            	value = result;
                break;
            case 2:
                value = new Boolean (foundstockinparsefile);
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

class BanKuanOrStoksListTableModel extends DefaultTableModel 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String[] jtableTitleStrings = { "板块/个股代码", "板块/个股名称"};
	private HashMap<String,String> bkstksmap; //包含代码和名称
	private ArrayList<String> bkstkscodelist;
	private int tablenodetype;
	
	BanKuanOrStoksListTableModel ()
	{
	}
	public int getNodeLineIndex (String nodeid)
	{
		return bkstkscodelist.indexOf(nodeid);
	}
	public String getStockName(int row) 
	{
		// TODO Auto-generated method stub
		return bkstksmap.get(bkstkscodelist.get(row));
	}
	public String getStockCode(int row) 
	{
		// TODO Auto-generated method stub
		return bkstkscodelist.get(row);
	}
	public void refresh(HashMap<String, String> tmpbknobelongs ,int tablenodetype1) 
	{
		this.bkstksmap = tmpbknobelongs;
		bkstkscodelist = new ArrayList<String> (this.bkstksmap.keySet());
		this.tablenodetype = tablenodetype1;
		
		this.fireTableDataChanged();
	}
	public int getTableNodeType ()
	{
		return this.tablenodetype;
	}
	public String getColumnName(int column) { 
    	return jtableTitleStrings[column];
    }//设置表格列名 
    @Override
    public int getColumnCount() 
    {
        return jtableTitleStrings.length;
    } 

    public boolean isCellEditable(int row,int column) {
    	return false;
	}
    
	 public int getRowCount() 
	 {
		 if(this.bkstksmap == null)
			 return 0;
		 else 
			 return this.bkstksmap.size();
	 }
	 
	 public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	Object value = "??";
	    	switch (columnIndex) {
	    	case 0:
                value = bkstkscodelist.get(rowIndex);
                break;
            case 1:
            	value = bkstksmap.get( bkstkscodelist.get(rowIndex) );
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

}

class BanKuaiSubChanYeLianTableModel extends DefaultTableModel 
{
	private HashMap<String,String> bksubcylmap; //包含代码和名称
	private ArrayList<String> bksubcylcodelist;
	String[] jtableTitleStrings = { "板块国/子产业链代码", "板块国/子产业链名称"};
	private int tablenodetype;
	
	
	BanKuaiSubChanYeLianTableModel ()
	{
	}

	public void refresh  (HashMap<String,String> bksubcylmap2,int tablenodetype1)
	{
		this.bksubcylmap = bksubcylmap2;
		bksubcylcodelist = new ArrayList<String> (this.bksubcylmap.keySet());
		
		this.tablenodetype = tablenodetype1;
		
		this.fireTableDataChanged();
	}
	public int getTableNodeType ()
	{
		return this.tablenodetype;
	}
	public void addRow(String newsubcylcode, String newsubbk)
	{
		this.bksubcylmap.put(newsubcylcode, newsubbk);
		this.bksubcylcodelist = new ArrayList<String> (this.bksubcylmap.keySet());
		this.fireTableDataChanged();
	}

	public int getRowCount() 
	 {
		 if(this.bksubcylmap == null)
			 return 0;
		 else 
			 return this.bksubcylmap.size();
	 }

	    @Override
	    public int getColumnCount() 
	    {
	        return jtableTitleStrings.length;
	    } 
	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	if(bksubcylmap.isEmpty())
	    		return null;
	    	
	    	Object value = "??";
	    	switch (columnIndex) {
            case 0:
                value = bksubcylcodelist.get(rowIndex);
                break;
            case 1:
            	value = bksubcylmap.get( bksubcylcodelist.get(rowIndex) );
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
	    
	    public String getColumnName(int column) { 
	    	return jtableTitleStrings[column];
	    }//设置表格列名 
		

	    public boolean isCellEditable(int row,int column) {
	    	return false;
		}
	    public String getSubChanYeLianCode (int row) 
	    {
	    	return bksubcylcodelist.get(row) ;
	    }
	    public String getSubChanYeLianName (int row) 
	    {
	    	return bksubcylmap.get( bksubcylcodelist.get(row) );
	    } 
	    
	    public void deleteAllRows()
	    {
	    	if(this.bksubcylmap == null)
				 return ;
			 else 
				 bksubcylmap.clear();
	    	
	    	this.fireTableDataChanged();
	    }
}

/*
 * google guava files 类，可以直接处理读出的line
 */
class ParseBanKuaiFielProcessor implements LineProcessor<List<String>> 
{
   
    private List<String> stocklists = Lists.newArrayList();
   
    @Override
    public boolean processLine(String line) throws IOException {
    	if(line.trim().length() ==7)
    		stocklists.add(line.substring(1));
        return true;
    }
    @Override
    public List<String> getResult() {
        return stocklists;
    }
}

//It’s a little trick to make a row automatically selected when the user right clicks on the table. Create a handler class for mouse-clicking events as follows:
class TableMouseListener extends MouseAdapter {
    
    private JTable table;
     
    public TableMouseListener(JTable table) {
        this.table = table;
    }
     
    @Override
    public void mousePressed(MouseEvent event) {
        // selects the row at which point the mouse is clicked
        Point point = event.getPoint();
        int currentRow = table.rowAtPoint(point);
        table.setRowSelectionInterval(currentRow, currentRow);
        
        
    }
}


