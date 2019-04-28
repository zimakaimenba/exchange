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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
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

import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiGeGuTable;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiGeGuTableModel;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiInfoTableModel;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.DisplayBkGgInfoEditorPane;
import com.exchangeinfomanager.commonlib.JUpdatedTextField;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.SubBanKuai;
import com.exchangeinfomanager.nodes.SubGuPiaoChi;
import com.exchangeinfomanager.nodes.nodexdata.TDXNodeGivenPeriodDataItem;
import com.exchangeinfomanager.nodes.operations.AllCurrentTdxBKAndStoksTree;
import com.exchangeinfomanager.nodes.operations.BanKuaiAndStockTree;
import com.exchangeinfomanager.nodes.operations.InvisibleTreeModel;
import com.exchangeinfomanager.nodes.treerelated.NodesTreeRelated;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.common.io.LineProcessor;
import com.toedter.calendar.JDateChooser;

public class BanKuaiAndChanYeLianGUI2  extends JPanel 
{
	public BanKuaiAndChanYeLianGUI2 (BanKuaiAndChanYeLian2 bkcyl1, StockInfoManager stockInfoManager1,AllCurrentTdxBKAndStoksTree bkstk1)
	{
		this.bkcyl = bkcyl1;
		this.stockInfoManager = stockInfoManager1;
		this.bkstk = bkstk1;
//		cyltree = bkcyl.getBkChanYeLianTree();
		
//		bkdbopt = new BanKuaiDbOperation ();
		
		initializeGui ();
		updateInfoToGui ();
		
		createEvents ();
	}
	public static final int UP=0, LEFT=1, RIGHT=2, DOWN=3, NONE=4;
	
	private AllCurrentTdxBKAndStoksTree bkstk;
	private StockInfoManager stockInfoManager;
	private BanKuaiAndChanYeLian2 bkcyl;
//	private BanKuaiAndStockTree cyltree;
//	protected BanKuaiDbOperation bkdbopt;
	private String currentselectedtdxbk = "";
	
	private void updateInfoToGui() 
	{
		treeScrollPane.setViewportView(this.bkcyl.getBkChanYeLianTree());
	}
	/*
     * 选择板块相关的子产业链，个股 
     */
    private void getReleatedInfoAndActionsForTreePathNode (TreePath closestPath)
    {
    	TreePath[] treePaths = bkcyl.getBkChanYeLianTree().getSelectionPaths();
    	BkChanYeLianTreeNode node = (BkChanYeLianTreeNode)bkcyl.getBkChanYeLianTree().getLastSelectedPathComponent();
    	
    	String nodecode;
    	try{
    		 nodecode = node.getMyOwnCode();
    	} catch (java.lang.NullPointerException ex) {
    		return;
    	}
    	 
        if(!nodecode.equals(currentselectedtdxbk)) { //和当前的板块不一样，
        	if(node.getType() == BkChanYeLianTreeNode.GPC) { //找出板块州所属的板块国以及不在自己下面的所有板块
        		selectionActionsForGPC (node);
        	} else if (node.getType() == BkChanYeLianTreeNode.SUBGPC ) { //找出所有不在自己范围内的板块
        		selectionActionsForSubGPC (node);
        	}  else if (node.getType() == BkChanYeLianTreeNode.TDXBK ) { //找出所有在自己范围内的板块和个股
        		selectionActionsForBanKuai (node);
        	}  else if (node.getType() == BkChanYeLianTreeNode.SUBBK ) { //找出所有在自己范围内的板块和个股
        		selectionActionsForSubBanKuai (node);
        	}  else if (node.getType() == BkChanYeLianTreeNode.TDXGG || node.getType() == BkChanYeLianTreeNode.BKGEGU ) { //找出所有在自己范围内的板块和个股
        		selectionActionsForStocks (node);
        	}
        	
  	       	currentselectedtdxbk = node.getMyOwnCode();
         }
    }
	/*
	 * 
	 */
	private void selectionActionsForStocks(BkChanYeLianTreeNode node) 
	{
		btnAddSubBk.setEnabled(false);
		//清空SUBGPC表
		
		((BkChanYeLianTreeNodeListTableModel)(tablesubcyl.getModel())).deleteAllRows();
		((BkChanYeLianTreeNodeListTableModel)(tablebkgegu.getModel())).deleteAllRows();
		
		
	}
	/*
	 * 
	 */
	private void selectionActionsForSubBanKuai(BkChanYeLianTreeNode node) 
	{
		btnAddSubBk.setEnabled(false);
		
		BkChanYeLianTreeNode parent = getBanKuaiOfANode (node);
		
		 List<BkChanYeLianTreeNode> tmpsubbk =     bkcyl.getSubBanKuai (parent.getMyOwnCode());
        ((BkChanYeLianTreeNodeListTableModel)(tablesubcyl.getModel())).refresh(tmpsubbk);
		
//		BkChanYeLianTreeNode parent = (BkChanYeLianTreeNode)node.getParent();
		BanKuai bankuai = (BanKuai)bkstk.getAllBkStocksTree().getSpecificNodeByHypyOrCode(parent.getMyOwnCode(), BkChanYeLianTreeNode.TDXBK);
        if( bankuai.getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL) || bankuai.getBanKuaiLeiXing().equals(BanKuai.HASGGWITHSELFCJL) ) {
        //读出该板块当前所有的个股，读出的是本周在该板块内存在的所有个股，而不是当天在该板块存在的个股
	  	   LocalDate curselectdate = LocalDate.now();
	  	   LocalDate requirestart = curselectdate.with(DayOfWeek.MONDAY).minus(9,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		 		
	  	     bankuai = bkstk.getBanKuai(bankuai.getMyOwnCode(), requirestart,curselectdate,TDXNodeGivenPeriodDataItem.WEEK);
	  	     bankuai = bkstk.getAllGeGuOfBanKuai ( bankuai,TDXNodeGivenPeriodDataItem.WEEK);
	  	     
	  	     Set<StockOfBanKuai> allbkge = bankuai.getSpecificPeriodBanKuaiGeGu(curselectdate,0,TDXNodeGivenPeriodDataItem.WEEK);
	  	     
	  	     ((BkChanYeLianTreeNodeListTableModel)(tablebkgegu.getModel())).refresh(new ArrayList (allbkge) );
	  	     
        }
		
        tmpsubbk = null;
	}
	/*
	 * 找到subbk所属的bankuai 
	 */
	private BkChanYeLianTreeNode getBanKuaiOfANode(BkChanYeLianTreeNode node) 
	{
		BkChanYeLianTreeNode parent = (BkChanYeLianTreeNode)node.getParent();
		if(parent.getType() == BkChanYeLianTreeNode.TDXBK)
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
	       	List <BkChanYeLianTreeNode> tmpsubbk = this.bkcyl.getSubBanKuai (node.getMyOwnCode());

	        ((BkChanYeLianTreeNodeListTableModel)(tablesubcyl.getModel())).refresh(tmpsubbk);
	       
	        BanKuai bankuai = (BanKuai)bkstk.getAllBkStocksTree().getSpecificNodeByHypyOrCode(node.getMyOwnCode(), BkChanYeLianTreeNode.TDXBK);
	        if( bankuai.getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL) || bankuai.getBanKuaiLeiXing().equals(BanKuai.HASGGWITHSELFCJL) ) {
	        //读出该板块当前所有的个股，读出的是本周在该板块内存在的所有个股，而不是当天在该板块存在的个股
		  	    LocalDate curselectdate = LocalDate.now();
		  	    LocalDate requirestart = curselectdate.with(DayOfWeek.MONDAY).minus(9,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		 		
		  	    bankuai = bkstk.getBanKuai(bankuai.getMyOwnCode(), requirestart, curselectdate,TDXNodeGivenPeriodDataItem.WEEK);
		  	    bankuai = bkstk.getAllGeGuOfBanKuai ( bankuai,TDXNodeGivenPeriodDataItem.WEEK);
		  	     
		  	    Set<StockOfBanKuai> allbkge = bankuai.getSpecificPeriodBanKuaiGeGu(curselectdate,0,TDXNodeGivenPeriodDataItem.WEEK);

		  	    ((BkChanYeLianTreeNodeListTableModel)(tablebkgegu.getModel())).refresh(new ArrayList<BkChanYeLianTreeNode> (allbkge) );
	        }
	       	
// 	      读出该板块相关的新闻
// 	    notesPane.displayNodeAllInfo(node);
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
		((BkChanYeLianTreeNodeListTableModel)(tablesubcyl.getModel())).deleteAllRows();
		
		//显示所有不在自己范围内的板块
		List<BkChanYeLianTreeNode> tmpbknobelongs = new ArrayList<BkChanYeLianTreeNode> ();
		Set<String> nodebkset = this.bkcyl.getBkChanYeLianTree().getSpecificTypeNodesCodesSet (node.getMyOwnCode(),BkChanYeLianTreeNode.SUBGPC,BkChanYeLianTreeNode.TDXBK);
		if(nodebkset != null) {
			Set<String> allbkstset = this.bkstk.getAllBkStocksTree().getSpecificTypeNodesCodesSet ("000000",BkChanYeLianTreeNode.DAPAN,BkChanYeLianTreeNode.TDXBK);
			SetView<String> differencebk = Sets.difference(allbkstset,nodebkset ); //应该删除的
			 
			for(String bkcode : differencebk) {
				BkChanYeLianTreeNode tmpnode = this.bkstk.getAllBkStocksTree().getSpecificNodeByHypyOrCode(bkcode, BkChanYeLianTreeNode.TDXBK);
//				BanKuai bk = new BanKuai (tmpnode.getMyOwnCode(),tmpnode.getMyOwnName());
				tmpbknobelongs.add(tmpnode);
			}
		}
		
		((BkChanYeLianTreeNodeListTableModel)(tablebkgegu.getModel())).refresh(tmpbknobelongs);
	}
	/*
	 * 
	 */
	private void selectionActionsForGPC(BkChanYeLianTreeNode node) 
	{
		if(node.getMyOwnName().equals("其他")) {//其他是把所有没有加入GPC的板块放在这里，所以没必要特别显示她没有的。
			HashMap<String, String> tmpsubgpc = new HashMap<String, String>  ();
			((BkChanYeLianTreeNodeListTableModel)(tablesubcyl.getModel())).deleteAllRows();
			((BkChanYeLianTreeNodeListTableModel)(tablebkgegu.getModel())).deleteAllRows ();
			tmpsubgpc = null;
			return;
		}
		
		btnAddSubBk.setEnabled(true);
		//显示所有子板块国
		List<BkChanYeLianTreeNode> tmpsubgpc = bkcyl.getSubGuPiaoChi (node.getMyOwnCode());
		((BkChanYeLianTreeNodeListTableModel)(tablesubcyl.getModel())).refresh( tmpsubgpc );
		
		//显示所有不在自己范围内的板块
		Set<String> nodebkset = this.bkcyl.getBkChanYeLianTree().getSpecificTypeNodesCodesSet (node.getMyOwnCode(),BkChanYeLianTreeNode.GPC,BkChanYeLianTreeNode.TDXBK);
		Set<String> allbkstset = this.bkstk.getAllBkStocksTree().getSpecificTypeNodesCodesSet ("000000",BkChanYeLianTreeNode.DAPAN,BkChanYeLianTreeNode.TDXBK);
		SetView<String> differencebk = Sets.difference(allbkstset,nodebkset ); 
		List<BkChanYeLianTreeNode> tmpbknobelongs = new ArrayList<BkChanYeLianTreeNode> (); 
		for(String bkcode : differencebk) {
			BkChanYeLianTreeNode tmpnode = this.bkstk.getAllBkStocksTree().getSpecificNodeByHypyOrCode(bkcode, BkChanYeLianTreeNode.TDXBK);
			tmpbknobelongs.add(tmpnode);
		}
		((BkChanYeLianTreeNodeListTableModel)(tablebkgegu.getModel())).refresh(tmpbknobelongs);
		
		nodebkset = null;
		allbkstset = null;
	}
	/*
	 * 
	 */
	public boolean saveCylXmlAndZdgzXml () //GEN-FIRST:event_saveButtonActionPerformed 
	{
		
		
		if( this.bkcyl.saveChanYeLianTreeToXML() ) {
			
			JOptionPane.showMessageDialog(null, "保存产业链XML失败！请查找原因。","Warning", JOptionPane.WARNING_MESSAGE);
			return false;
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

		TreePath closestPath = this.bkcyl.getBkChanYeLianTree().getSelectionPath();
         BkChanYeLianTreeNode node = (BkChanYeLianTreeNode)closestPath.getLastPathComponent();
         String nodecode = node.getMyOwnCode();
         int nodetype = node.getType();
         if(nodetype == BkChanYeLianTreeNode.GPC) {
        	 title = "请输入板块国名称";
         }else 
         if( nodetype == BkChanYeLianTreeNode.TDXBK) {
        	 title = "请输入子板块名称";
         } else
        	 return;
         
        String newsubbk = JOptionPane.showInputDialog(null,title,"输入", JOptionPane.QUESTION_MESSAGE);
 		if(newsubbk == null)
 			return;
        
 		BkChanYeLianTreeNode newsubcylcode = null ;
 		if(nodetype == BkChanYeLianTreeNode.GPC) {
 			newsubcylcode = this.bkcyl.addNewSubGuoPiaoChi (  nodecode,newsubbk.trim() );
        } else 
        if( nodetype == BkChanYeLianTreeNode.TDXBK) {
        	newsubcylcode = this.bkcyl.addNewSubBanKuai (nodecode,newsubbk.trim() );
        } 
 		
		if(newsubcylcode != null)
			((BkChanYeLianTreeNodeListTableModel)(tablesubcyl.getModel())).addRow(newsubcylcode);
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
			 
			 BkChanYeLianTreeNode subcode = ((BkChanYeLianTreeNodeListTableModel)(tablesubcyl.getModel())).getNode(row);
			 
			 this.bkcyl.addNewNode (subcode, direction);
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
	  		
	  		BkChanYeLianTreeNode subcode = ((BkChanYeLianTreeNodeListTableModel)(tablebkgegu.getModel())).getNode(row);
	  		
//	  		BkChanYeLianTreeNode nodestock = this.bkstk.getAllBkStocksTree().getSpecificNodeByHypyOrCode("880472", BkChanYeLianTreeNode.TDXBK);
//			BanKuai bkinallbkst = (BanKuai) bkcyl.getBkChanYeLianTree().getSpecificNodeByHypyOrCode(subcode.getMyOwnCode(),subcode.getType());
//	  		this.bkcyl.addNewNode (bkinallbkst,direction);
	  		this.bkcyl.addNewNode (subcode,direction);
	   }
	/*
    * 产业链树上定位用户选择的板块，只选择到板块一级，子板块不找
    */
   private void findBanKuaiInTree(String bkinputed) 
   {
	   BkChanYeLianTreeNode findnode = bkcyl.getBkChanYeLianTree().getSpecificNodeByHypyOrCode (bkinputed,BkChanYeLianTreeNode.TDXBK);
		TreePath bkpath = new TreePath(findnode.getPath());
		if(bkpath != null) {
			bkcyl.getBkChanYeLianTree().setSelectionPath(bkpath);
			bkcyl.getBkChanYeLianTree().scrollPathToVisible(bkpath);
			bkcyl.getBkChanYeLianTree().expandTreePathAllNode(bkpath);
		}

		selectionActionsForBanKuai (findnode);
	}
	/*
	 * 
	 */
	private void createEvents() 
	{
		btnopencylxml.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				 bkcyl.openChanYeLianXmlInWinSystem ();
			}
		});
		
		tfldfindgegu.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				String nodeid = tfldfindgegu.getText().trim();
				int rowindex = ((BkChanYeLianTreeNodeListTableModel)(tablebkgegu.getModel())).getNodeLineIndex(nodeid);
				
				if(rowindex != -1) {
					tablebkgegu.setRowSelectionInterval(rowindex, rowindex);
					tablebkgegu.scrollRectToVisible(new Rectangle(tablebkgegu.getCellRect(rowindex, 0, true)));
				} else 	{
					JOptionPane.showMessageDialog(null,"股票/板块代码或名称拼音有误！","Warning", JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		btnfindgegu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String nodeid = tfldfindgegu.getText().trim();
				int rowindex = ((BkChanYeLianTreeNodeListTableModel)(tablebkgegu.getModel())).getNodeLineIndex(nodeid);
				
				if(rowindex != -1) {
					tablebkgegu.setRowSelectionInterval(rowindex, rowindex);
					tablebkgegu.scrollRectToVisible(new Rectangle(tablebkgegu.getCellRect(rowindex, 0, true)));
				} else 	{
					JOptionPane.showMessageDialog(null,"股票/板块代码有误！","Warning", JOptionPane.WARNING_MESSAGE);
				}

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
				
				BkChanYeLianTreeNode delnode = (BkChanYeLianTreeNode) bkcyl.getBkChanYeLianTree().getSelectionPath().getLastPathComponent();
				bkcyl.deleteNodes(delnode);
				btnSaveAll.setEnabled(true);
			}
		});
		
		bkcyl.getBkChanYeLianTree().addMouseListener(new java.awt.event.MouseAdapter() {
	            public void mousePressed(java.awt.event.MouseEvent evt) {
//	            	chanYeLianTreeMousePressed(evt);
//	            	logger.debug("get action notice at bkcyl");
	    	        TreePath closestPath = bkcyl.getBkChanYeLianTree().getClosestPathForLocation(evt.getX(), evt.getY());

	    	        if(closestPath != null) {
	    	            Rectangle pathBounds = bkcyl.getBkChanYeLianTree().getPathBounds(closestPath);
	    	            int maxY = (int) pathBounds.getMaxY();
	    	            int minX = (int) pathBounds.getMinX();
	    	            int maxX = (int) pathBounds.getMaxX();
	    	            if (evt.getY() > maxY) 
	    	            	bkcyl.getBkChanYeLianTree().clearSelection();
	    	            else if (evt.getX() < minX || evt.getX() > maxX) 
	    	            	bkcyl.getBkChanYeLianTree().clearSelection();
	    	        }
	    	        getReleatedInfoAndActionsForTreePathNode ( closestPath);
	            }
	        });
		
	}
	private JTable tablebkgegu;
	private JUpdatedTextField tfldfindbk;
	private JUpdatedTextField tfldfindgegu;
	private JButton deleteButton;
	private JButton btnfindbk;
	private JButton btnfindgegu;
	private JScrollPane scrollPanegegu;
	private JButton btnAddSubBk;
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
	private JButton btnopencylxml;
	private JPopupMenu popupMenu;
	private JMenuItem mntmNewMenuItem;
	private JTable tablesubcyl;
	private DisplayBkGgInfoEditorPane notesPane;
	private JButton btnSaveAll;

	private void initializeGui() 
	{
		
		JPanel panelcyltree = new JPanel();
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(106)
					.addComponent(panelcyltree, 0, 0, Short.MAX_VALUE)
					.addGap(4))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
					.addGap(5)
					.addComponent(panelcyltree, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addGap(69))
		);
		
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
		
		tfldfindbk = new JUpdatedTextField();
		tfldfindbk.setColumns(10);
		
		btnfindbk = new JButton("\u5B9A\u4F4D\u677F\u5757");
		
		tfldfindgegu = new JUpdatedTextField();
		tfldfindgegu.setColumns(10);
		
		btnfindgegu = new JButton("\u5B9A\u4F4D\u4E2A\u80A1");
		
		btnopencylxml = new JButton("\u6253\u5F00XML");
		
		
		btnSaveAll = new JButton("\u4FDD\u5B58\u6240\u6709\u4FEE\u6539");
		btnSaveAll.setForeground(Color.RED);
		
		btnadddalei = new JButton("\u589E\u52A0\u80A1\u7968\u6C60");
		btnadddalei.setEnabled(false);
		
		btndeldalei = new JButton("\u5220\u9664\u80A1\u7968\u6C60");
		btndeldalei.setEnabled(false);
		
		btnGenTDXCode = new JButton("\u751F\u6210TDX\u4EE3\u7801");
		
		
		GroupLayout gl_panelcyltree = new GroupLayout(panelcyltree);
		gl_panelcyltree.setHorizontalGroup(
			gl_panelcyltree.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelcyltree.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING, false)
						.addGroup(gl_panelcyltree.createSequentialGroup()
							.addComponent(deleteButton)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(tfldfindbk, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnfindbk)
							.addGap(18)
							.addComponent(btnopencylxml)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btnGenTDXCode))
						.addComponent(jSplitPane, GroupLayout.PREFERRED_SIZE, 504, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_panelcyltree.createSequentialGroup()
							.addComponent(btnadddalei)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btndeldalei)
							.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(btnSaveAll)))
					.addGap(12)
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
					.addGroup(gl_panelcyltree.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_panelcyltree.createParallelGroup(Alignment.BASELINE)
							.addComponent(btnAddSubBk)
							.addComponent(btnSaveAll))
						.addGroup(gl_panelcyltree.createParallelGroup(Alignment.BASELINE)
							.addComponent(btnadddalei)
							.addComponent(btndeldalei)))
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
								.addComponent(btnopencylxml, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnGenTDXCode))
							.addGroup(gl_panelcyltree.createParallelGroup(Alignment.BASELINE)
								.addComponent(tfldfindgegu, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnfindgegu)))
						.addComponent(deleteButton, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
					.addGap(13))
		);
		
		BkChanYeLianTreeNodeListTableModel subcylmode = new BkChanYeLianTreeNodeListTableModel ();
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
		
		
		BkChanYeLianTreeNodeListTableModel bkstmodel = new BkChanYeLianTreeNodeListTableModel ();
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
		
		//ZdgzBanKuaiDetailXmlTableModel xmlaccountsmodel = new ZdgzBanKuaiDetailXmlTableModel( );
		//tableZdgzBankDetails = new  JTable(xmlaccountsmodel)
//		CurZdgzBanKuaiTableModel curzdgzbkmodel = new CurZdgzBanKuaiTableModel ();
		int preferedwidth = 170;
		
		
		
//		CurZdgzBanKuaiTableModel curzdgzbkmodel = new CurZdgzBanKuaiTableModel (); 
//		tableCurZdgzbk = new  JTable(curzdgzbkmodel) {
//		ZdgzBanKuaiDetailXmlTableModel xmlaccountsmodel = new ZdgzBanKuaiDetailXmlTableModel( );
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

class BkChanYeLianTreeNodeListTableModel extends DefaultTableModel 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String[] jtableTitleStrings = { "板块/个股代码", "板块/个股名称"};
	private List<BkChanYeLianTreeNode> nodelist; //包含代码和名称
	
	public BkChanYeLianTreeNodeListTableModel ()
	{
//		nodelist = new ArrayList<BkChanYeLianTreeNode> ();
	}
	public void setTableTitles (String[] tabletitle)
	{
		this.jtableTitleStrings = tabletitle;
	}
	public int getNodeLineIndex(String nodeid) 
	{
		if(nodelist == null)
			return -1;
		for(int i = 0;i <  nodelist.size() ; i++) {
			if(nodelist.get(i).getMyOwnCode().equals(nodeid))
				return i;
		}
		return -1;
	}

	public void refresh(List<BkChanYeLianTreeNode> nodelist)  
	{
		this.nodelist = nodelist;
		this.fireTableDataChanged();
	}
	public void addRow (BkChanYeLianTreeNode newnode) 
	{
		if(this.nodelist == null)
			nodelist = new ArrayList<BkChanYeLianTreeNode> ();
		else
			nodelist.add(newnode);
		
		this.fireTableDataChanged();
	}
	public BkChanYeLianTreeNode getNode (int row)
	{
		return this.nodelist.get(row);
	}
	public String getNodeName(int row) 
	{
		// TODO Auto-generated method stub
		return nodelist.get(row).getMyOwnName();
	}
	public String getNodeCode(int row) 
	{
		// TODO Auto-generated method stub
		return nodelist.get(row).getMyOwnCode();
	}
	
	public String getColumnName(int column) 
	{ 
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
		 if(this.nodelist == null)
			 return 0;
		 else 
			 return this.nodelist.size();
	 }
	 
	 public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	Object value = "??";
	    	switch (columnIndex) {
	    	case 0:
                value = nodelist.get(rowIndex).getMyOwnCode();
                break;
            case 1:
            	value = nodelist.get(rowIndex).getMyOwnName();
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
	 
	 public void deleteAllRows()
	    {
	    	if(this.nodelist == null)
				 return ;
			 else 
				 nodelist.clear();
	    	
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


