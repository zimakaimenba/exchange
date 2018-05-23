package com.exchangeinfomanager.bankuaichanyelian;

import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.text.Collator;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;
import org.jsoup.Jsoup;

import com.exchangeinfomanager.asinglestockinfo.AllCurrentTdxBKAndStoksTree;
import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTree;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.DaPan;
import com.exchangeinfomanager.asinglestockinfo.GuPiaoChi;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.asinglestockinfo.StockGivenPeriodDataItem;
import com.exchangeinfomanager.asinglestockinfo.StockOfBanKuai;
import com.exchangeinfomanager.asinglestockinfo.SubnodeButton;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiGeGuTable;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiGeGuTableModel;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiPopUpMenu;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.DisplayBkGgInfoEditorPane;

import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.ChanYeLianNewsPanel;
import com.exchangeinfomanager.bankuaifengxi.BanKuaiFengXi;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.gui.subgui.BuyStockNumberPrice;
import com.exchangeinfomanager.gui.subgui.JiaRuJiHua;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.exchangeinfomanager.tongdaxinreport.TDXFormatedOpt;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import com.sun.rowset.CachedRowSetImpl;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JSeparator;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent;
import com.toedter.calendar.JDateChooser;
import javax.swing.border.TitledBorder;
import javax.swing.JCheckBox;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.UIManager;

public class BanKuaiAndChanYeLian2 
{
	private static final long serialVersionUID = 1L;

	public BanKuaiAndChanYeLian2 (AllCurrentTdxBKAndStoksTree allbkstocks) 
	{
		this.sysconfig = SystemConfigration.getInstance();
		this.bkdbopt = new BanKuaiDbOperation ();

		this.cylxmhandler = new ChanYeLianXMLHandler ();
		this.zdgzbkxmlhandler = new TwelveZhongDianGuanZhuXmlHandler ();
		
		treechanyelian = cylxmhandler.getBkChanYeLianXMLTree();
		updatedChanYeLianTree (allbkstocks);
		
		zdgzbkmap = zdgzbkxmlhandler.getZdgzBanKuaiFromXmlAndUpatedToCylTree(treechanyelian);
	}
	
	

	private static Logger logger = Logger.getLogger(BanKuaiAndChanYeLian2.class);
	
	public static final int UP=0, LEFT=1, RIGHT=2, DOWN=3, NONE=4;
	
	protected SystemConfigration sysconfig;
	protected HashMap<String, ArrayList<BkChanYeLianTreeNode>> zdgzbkmap;
	protected ChanYeLianXMLHandler cylxmhandler;
	protected TwelveZhongDianGuanZhuXmlHandler zdgzbkxmlhandler;
    protected BkChanYeLianTree treechanyelian;
//    protected BkChanYeLianTree treeallstocks;
    protected BkChanYeLianTree treeallbankuaiandhisstocks;
    protected BanKuaiDbOperation bkdbopt;

//	private String currentselectedtdxbk = "";
	private HashSet<String> stockinfile;
	
	/*
	 * 
	 */
	public BkChanYeLianTree getBkChanYeLianTree ()
	{
		return treechanyelian;
	}
	/*
	 * 
	 */
	private void updatedChanYeLianTree(AllCurrentTdxBKAndStoksTree allbkstocks)
	{
		// �ȱ�֤12����Ʊ��һ��
		HashMap<String,String> gpcindb = bkdbopt.getGuPiaoChi ();
		
		BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)this.treechanyelian.getModel().getRoot();
		int bankuaicount = treechanyelian.getModel().getChildCount(treeroot);
		HashSet<String> gpcintree = new HashSet<String> ();
		for(int i=0;i< bankuaicount; i++) {
			BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode) this.treechanyelian.getModel().getChild(treeroot, i);
			gpcintree.add(childnode.getMyOwnCode());
		}
		
		SetView<String> differencegpc = Sets.difference(gpcindb.keySet(), gpcintree );
		for (String gpccode : differencegpc) {
			GuPiaoChi newpgc = new GuPiaoChi(gpccode,gpcindb.get(gpccode));
			treeroot.add(newpgc);
		}
		differencegpc = null;
		gpcintree = null;
		//��֤���һ�£�����Ӧ��ɾ���İ�鶼ɾ��������һ�£�ȱ�ٵİ�鶼�ŵ���������
		HashSet<BkChanYeLianTreeNode> cyltreebkset = this.treechanyelian.getSpecificNodesSet (BanKuaiAndStockBasic.TDXBK);
	    HashSet<BkChanYeLianTreeNode> allbks = allbkstocks.getAllBkStocksTree().getSpecificNodesSet (BanKuaiAndStockBasic.TDXBK);
	    
	    SetView<BkChanYeLianTreeNode> differencebkold = Sets.difference(cyltreebkset, allbks );
	    for(BkChanYeLianTreeNode oldnode : differencebkold) {
	    	kkk
	    }
		//��֤����һ�£�����Ӧ��ɾ���ĸ���ɾ��������һ��
		
 	}
	
	/*
	 * �ж��Ƿ������ص��ע���
	 */
	public Multimap<String,String> checkBanKuaiSuoSuTwelveDaLei (Set<String> union)
	{
		return zdgzbkxmlhandler.subBkSuoSuTwelveDaLei (union);
	}
	/*
	 * ��Ʊ������ҵ��
	 */
	public Stock getStockChanYeLianInfo (Stock stockbasicinfo2)
	{
		cylxmhandler.getStockChanYeLianInfo(stockbasicinfo2);
		return stockbasicinfo2;
	}
	/*
	 * ��ȡ���ɲ�ҵ��
	 */
	public ArrayList<String> getGeGuChanYeLian(String stockcode)
	{
		return cylxmhandler.getGeGuChanYeLian(stockcode);
	}
	/*
	 * 
	 */
//	private void initializeAllDaLeiZdgzTableFromXml ()
//	{
//		((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).refresh(zdgzbkmap);
//		try {
//			tableCurZdgzbk.setRowSelectionInterval(0,0);
//			int row = tableCurZdgzbk.getSelectedRow();
//			String selecteddalei = ((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).getZdgzDaLei (row);
//			
//			unifyDisplaysInDifferentCompOnGui (selecteddalei,0);
//		} catch (java.lang.IllegalArgumentException e) {
//			e.printStackTrace();
//		}
//		
//		
//	}

//	private String getCurSelectedDaLei ()
//    {
//    	int row = tableCurZdgzbk.getSelectedRow();
//		if(row <0) {
//			return null;
//		} 
//		
//		 String selecteddalei = ((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).getZdgzDaLei (row);
//		 tableCurZdgzbk.setRowSelectionInterval(row, row);
//		 return  selecteddalei;
//    }
	/*
	 * ͳһ�����ϸ��������������Ķ�������סһ�¡�
	 */
//	private void unifyDisplaysInDifferentCompOnGui (String selecteddalei,int selectrowfordaleiinsubcyl) 
//	{
//		//for �ص��ע
//		((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).refresh(zdgzbkmap);
//		int selecteddaleirow = ((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).getDaLeiIndex (selecteddalei);
//		tableCurZdgzbk.setRowSelectionInterval(selecteddaleirow,selecteddaleirow);
//		
//		//for sub�ص��ע
//		((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).refresh(zdgzbkmap,selecteddalei);
//		if( ((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).getRowCount() != 0)
//			tableZdgzBankDetails.setRowSelectionInterval(selectrowfordaleiinsubcyl,selectrowfordaleiinsubcyl);
//		
//		BkChanYeLianTreeNode curselectnode = ((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).getGuanZhuBanKuaiInfo(selectrowfordaleiinsubcyl);
////		if(curselectnode != null) {
////
////			((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).fireTableDataChanged();
////			tableCurZdgzbk.setRowSelectionInterval(selecteddaleirow,selecteddaleirow);
////		}
//		//logger.debug("current node is  " + currentselectedtdxbk );
//
//		//for tree
//		if(curselectnode != null) {
//			TreePath nodepath = new TreePath(curselectnode.getPath());
//			treechanyelian.expandTreePathAllNode( new TreePath(curselectnode.getPath()) );
//		   		
//		    //for ����Talble
//			getReleatedInfoAndActionsForTreePathNode (new TreePath(curselectnode.getPath()) );
//			
////            this.setSelectionPath(bkpath);
////    	     this.scrollPathToVisible(bkpath);
////    	     this.expandTreePathAllNode(bkpath);
//		}
//	}
    
    /*
     * ��ѡ������ص��Ӳ�ҵ�������� 
     */
//    private void getReleatedInfoAndActionsForTreePathNode (TreePath closestPath)
//    {
////    	bkfxpnl.resetDate ();
////        pnlGeGuZhanBi.resetDate();
//        
//    	 BanKuai bknode = (BanKuai) closestPath.getPathComponent(1);
//    	 String tdxbkcode = bknode.getMyOwnCode();
//    	 
//    	 HashSet<String> stockinparsefile = bknode.getNodetreerelated().getParseFileStockSet ();
//         if(!tdxbkcode.equals(currentselectedtdxbk)) { //�͵�ǰ�İ�鲻һ����
//  	       	//�����ĳ����������������ͨ���Ű�飬�ڶ����ð��Ĳ�ҵ���Ӱ�� 
//  	       	HashMap<String, String> tmpsubbk = bkdbopt.getSubBanKuai (tdxbkcode);
////  	       	((BanKuaiSubChanYeLianTableModel)(tablesubcyl.getModel())).setRowCount(0);
//  	        ((BanKuaiSubChanYeLianTableModel)(tablesubcyl.getModel())).refresh(tmpsubbk);
//  	       
//  	        if( bknode.getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL) || bknode.getBanKuaiLeiXing().equals(BanKuai.HASGGWITHSELFCJL) ) {
//  	        //�����ð�鵱ǰ���еĸ��ɣ��������Ǳ����ڸð���ڴ��ڵ����и��ɣ������ǵ����ڸð����ڵĸ���
//  		  	     LocalDate curselectdate = dchgeguwkzhanbi.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//  		  	     bknode = this.getBanKuai(bknode, curselectdate);
//  		  	     bknode = this.getAllGeGuOfBanKuai (bknode);
//  		  	     //װ����ÿ�հ���ļ�
//  		  	     if( stockinfile != null &&  stockinfile.size()>0)
//  		  	    	 treechanyelian.updateParseFileInfoToTreeFromSpecificNode (bknode,stockinfile,dchgeguwkzhanbi.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() );
////  				model.nodeStructureChanged(bknode);
//  		  	     
//  		  	     HashMap<String, Stock> allbkge = bknode.getAllBanKuaiGeGu();
//  	  	       	((BanKuaiGeGuTableModel)(tablebkgegu.getModel())).refresh(bknode, curselectdate);
//  	  	       	tablebkgegu.sortByParsedFile();
//  	  	       	
//  	  	       	int row = tableCurZdgzbk.getSelectedRow();
//  	  	       	((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).fireTableDataChanged();
//  	  	       	if(row >= 0)
//  	  	       		tableCurZdgzbk.setRowSelectionInterval(row,row);
//  	  	       	else
//  	  	       		tableCurZdgzbk.setRowSelectionInterval(0,0);
//  	        }
//  	       	
////	  	     �����ð����ص�����
//	  	    BkChanYeLianTreeNode curselectedbknode = (BkChanYeLianTreeNode) closestPath.getLastPathComponent();
//	  	    notesPane.displayNodeAllInfo(curselectedbknode);
////	  	    createChanYeLianNewsHtml (curselectedbknode);
////	  	    displayNodeBasicInfo(curselectedbknode);
//  	       	
//  	       	currentselectedtdxbk = tdxbkcode;
//         }
//    }
	/*
	 * ���ӽڵ�
	 */
//	  private void addSubnodeButtonMouseMoved(java.awt.event.MouseEvent evt) //GEN-FIRST:event_addSubnodeButtonMouseMoved 
//	  {
//	        SubnodeButton button = (SubnodeButton) addSubnodeButton;
//	        String key;
//	        if (System.getProperty("os.name").startsWith("Mac OS X")) key = "CMD";
//	        else key = "CTRL";
//	        int x = evt.getX();
//	        //System.out.print("x=" + x + " ");
//	        int y = evt.getY();
//	        //System.out.print(y);
//	        //System.out.print("y=" + y + " ");
//	        if (y<19 && x+y<30 && x<19) {
//	            button.setDirection(BanKuaiAndChanYeLian.UP);
//	            button.setIcon(addAboveIcon);
//	            button.setToolTipText("Add above ("+key+"-UP)");
//	        }
//	        else if (y>=19 && x-y < 0 && x<19){
//	            button.setDirection(BanKuaiAndChanYeLian.DOWN);
//	            button.setIcon(addBelowIcon);
//	            button.setToolTipText("Add below ("+key+"-DOWN)");
//	        }
//	        else if (x+y>30 && x-y>0){
//	            button.setDirection(BanKuaiAndChanYeLian.RIGHT);
//	            button.setIcon(addChildIcon);
//	            button.setToolTipText("Add subnode ("+key+"-RIGHT)");
//	        }
//	        else {
//	            button.setDirection(BanKuaiAndChanYeLian.NONE);
//	            button.setIcon(addSubnodeIcon);
//	            button.setToolTipText("Add subnode");
//	        }
//	   }
//	
//	  private void addSubnodeButtonMouseExited(java.awt.event.MouseEvent evt) //GEN-FIRST:event_addSubnodeButtonMouseExited 
//	  {
//	        addSubnodeButton.setIcon(addSubnodeIcon);
//	        addSubnodeButton.setToolTipText("Add subnode");
//	  }
//	  
//	  private void addSubnodeButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_addSubnodeButtonActionPerformed 
//	  {
////		  cylneedsave = true;
//			 
//			 int direction = ((SubnodeButton)addSubnodeButton).getDirection();
//			 int row = tablesubcyl.getSelectedRow() ;
//			 if( row <0) {
//				 JOptionPane.showMessageDialog(null,"��ѡ��һ���Ӱ��!");
//				 return;
//			 }
//			 
//			 String subcode = ((BanKuaiSubChanYeLianTableModel)(tablesubcyl.getModel())).getSubChanYeLianCode(row);
//			 String subname = ((BanKuaiSubChanYeLianTableModel)(tablesubcyl.getModel())).getSubChanYeLianName(row);
//
//			 treechanyelian.addNewNode (BkChanYeLianTreeNode.SUBBK,subcode,subname,direction);
//			 btnSaveAll.setEnabled(true);
//			 
//		}//GEN-LAST:event_addSubnodeButtonActionPerformed

	    /*
	     * ��ʾ����ڹ�Ʊ����ռ��, ��ʾ�Ķ��Ǳ��ܵĶ����ǵ����
	     */
//	    protected void displayBanKuaiGeGuZhanBi(BanKuai bknode) 
//	    {
////	    	if(cbxtichuquanzhong.isSelected())
////	    		pnlGeGuZhanBi.setBanKuaiNeededDisplay(bknode,Integer.parseInt(tfldquanzhong.getText() ),CommonUtility.getWeekNumber(dchgeguwkzhanbi.getDate()  ) );
////	    	else
////	    		pnlGeGuZhanBi.setBanKuaiNeededDisplay(bknode, -1 ,CommonUtility.getWeekNumber(dchgeguwkzhanbi.getDate()  ) );
//			
//		}
	    /*
	     * ��ʾ������������г���ռ��
	     */
//	    private void displayBanKuaiZhanBi(BanKuai bankuai) 
//	    {
////	    	Date endday = CommonUtility.getLastDayOfWeek(dchgeguwkzhanbi.getDate() );
////	    	Date startday = CommonUtility.getDateOfSpecificMonthAgo(dchgeguwkzhanbi.getDate() ,sysconfig.banKuaiFengXiMonthRange() );
////	    	bankuai = bkdbopt.getBanKuaiZhanBi (bankuai,startday,endday);
////	    	
////	    	bkfxpnl.setNodeZhanBiByWeek(bankuai,endday);
//  	
//
//		}
    

//		private void addGeGuButtonMouseMoved(java.awt.event.MouseEvent evt) //GEN-FIRST:event_addSubnodeButtonMouseMoved 
//	    {
//	  	        SubnodeButton button = (SubnodeButton) addGeGuButton;
//	  	        String key;
//	  	        if (System.getProperty("os.name").startsWith("Mac OS X")) key = "CMD";
//	  	        else key = "CTRL";
//	  	        int x = evt.getX();
//	  	        //System.out.print("x=" + x + " ");
//	  	        int y = evt.getY();
//	  	        //System.out.print(y);
//	  	        //System.out.print("y=" + y + " ");
//	  	        if (y<19 && x+y<30 && x<19) {
//	  	            button.setDirection(BanKuaiAndChanYeLian.UP);
//	  	            button.setIcon(addAboveIcon);
//	  	            button.setToolTipText("Add above ("+key+"-UP)");
//	  	        }
//	  	        else if (y>=19 && x-y < 0 && x<19){
//	  	            button.setDirection(BanKuaiAndChanYeLian.DOWN);
//	  	            button.setIcon(addBelowIcon);
//	  	            button.setToolTipText("Add below ("+key+"-DOWN)");
//	  	        }
//	  	        else if (x+y>30 && x-y>0){
//	  	            button.setDirection(BanKuaiAndChanYeLian.RIGHT);
//	  	            button.setIcon(addChildIcon);
//	  	            button.setToolTipText("Add subnode ("+key+"-RIGHT)");
//	  	        }
//	  	        else {
//	  	            button.setDirection(BanKuaiAndChanYeLian.NONE);
//	  	            button.setIcon(addSubnodeIcon);
//	  	            button.setToolTipText("Add subnode");
//	  	        }
//	  	    }
//	    
//	    
//	    private void addGeGuButtonMouseExited(java.awt.event.MouseEvent evt)  
//	    {
//	    	addGeGuButton.setIcon(addSubnodeIcon);
//	    	addGeGuButton.setToolTipText("��Ӹ���");
//	    }
	    
//	    private void addGeGuButtonActionPerformed(java.awt.event.ActionEvent evt) 
//	    {
////	    	cylneedsave = true;
//	    	addGeGunode(((SubnodeButton)addGeGuButton).getDirection());
//	    	btnSaveAll.setEnabled(true);
//	    }
	    
//	    public void addGeGunode(int direction)
//	    {
//	   	int row = tablebkgegu.getSelectedRow();
//	  		if(row <0) {
//	  			JOptionPane.showMessageDialog(null,"��ѡ��һ����Ʊ!","Warning",JOptionPane.WARNING_MESSAGE);
//	  			return;
//	  		}
//	  		
//	  		String gegucode = ((BanKuaiGeGuTableModel)(tablebkgegu.getModel())).getStockCode(row);
//	  		String geguname = ((BanKuaiGeGuTableModel)(tablebkgegu.getModel())).getStockName(row);
//	  		
//	  		 treechanyelian.addNewNode (BkChanYeLianTreeNode.BKGEGU,gegucode,geguname,direction);
//	     }

//	    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
//			  if(treechanyelian.deleteNodes () )
////				  cylneedsave = true;
//				  btnSaveAll.setEnabled(true);
//		        
//		    }//GEN-LAST:event_deleteButtonActionPerformed
//
//	   
//		   /*
//		    * ��ҵ�����϶�λ�û�ѡ��İ�飬ֻѡ�񵽰��һ�����Ӱ�鲻��
//		    */
//		   public TreePath findBanKuaiInTree(String bkinputed) 
//		   {
//		    	@SuppressWarnings("unchecked")
//		    	TreePath bkpath = treechanyelian.locateNodeByNameOrHypyOrBkCode (bkinputed,true);
//		    	if(bkpath != null) {
////		            this.setSelectionPath(bkpath);
////		    	     this.scrollPathToVisible(bkpath);
////		    	     this.expandTreePathAllNode(bkpath);
//		    		getReleatedInfoAndActionsForTreePathNode (bkpath); //��ʾ�Ͱ����ص��Ӳ�ҵ���͸���
//		    	}
//		    	
//			    return bkpath;
//			}
//		    public boolean isXmlEdited ()
//		    {
//		    	if(btnSaveAll.isEnabled() )
//		    		return true;
//		    	else
//		    		return false;
//		    }
		    
//		    private void initializeBanKuaiParsedFile() 
//			{
//				String parsedpath = sysconfig.getBanKuaiParsedFileStoredPath ();
//				if(parsedpath == null || parsedpath == "")
//					return;
//				
//				Date date=new Date();//ȡʱ��
//				Calendar calendar = Calendar.getInstance();
//				calendar.setTime(date);
//				calendar.add(calendar.DATE,-1);//��������������һ��.����������,������ǰ�ƶ�
//				date=calendar.getTime(); //���ʱ���������������һ��Ľ�� 
//				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
//				String dateString = formatter.format(date);
////				logger.debug(dateString);
//				
//				String parsefilename = parsedpath + dateString + ".ebk";
////				tfldparsefilename.setText(parsefilename);
//				
//				parseSelectedBanKuaiFile (parsefilename);
//			}
		    /*
		     * ����ÿ�����ɵİ���ļ�
		     */
//		    private void parseSelectedBanKuaiFile (String filename)
//		    {
//		    	File parsefile = new File(filename);
//		    	if(!parsefile.exists() )
//		    		return;
//		    	
//		    	tfldparsefilename.setText(filename);
//				List<String> readparsefileLines = null;
//				try {
//					readparsefileLines = Files.readLines(parsefile,Charsets.UTF_8,new ParseBanKuaiFielProcessor ());
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				
//				stockinfile = new HashSet<String> (readparsefileLines);
//				BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)treechanyelian.getModel().getRoot();
//				treeroot.getNodetreerelated().setParseFileStockSet(stockinfile);
//				
//				//������ʾ�ص��ע�İ�飬�������ȵ��ʱ�����ʾ�����������̫�󡣼���������ʱ���ǲ���
////				for (Map.Entry<String,ArrayList<BkChanYeLianTreeNode>> entry : this.zdgzbkmap.entrySet()) {
////		    		 ArrayList<BkChanYeLianTreeNode> tmpzdgzlist = entry.getValue();
////		    		 
////		    		 for(BkChanYeLianTreeNode everyzdgznode : tmpzdgzlist) {
////		    			 TreePath bkpath = new TreePath(everyzdgznode.getPath());
////		    			 DefaultTreeModel model = (DefaultTreeModel) treechanyelian.getModel();
////						 TreeNode[] tempath = model.getPathToRoot(everyzdgznode);
////						 
////						 BkChanYeLianTreeNode parentnode = (BkChanYeLianTreeNode)tempath[1];
////						 if(parentnode.getType() == 4) {
////						   	parentnode = this.getBanKuai((BanKuai)parentnode, dchgeguwkzhanbi.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
////						   	parentnode = this.getAllGeGuOfBanKuai ( (BanKuai)parentnode);
////							treechanyelian.updateParseFileInfoToTreeFromSpecificNode (parentnode,stockinfile,dchgeguwkzhanbi.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() );
//////							model.nodeStructureChanged(parentnode);
////						 }
////		    		 }
////		    	 }
//				
//				//��������Ǵ������а�飬������̫����ʱ����
////				BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)treechanyelian.getModel().getRoot();
////				int bankuaicount = treechanyelian.getModel().getChildCount(treeroot);
////				for(int i=0;i< bankuaicount; i++) {
////					BanKuai childnode = (BanKuai)treechanyelian.getModel().getChild(treeroot, i);
////					String bkcode = childnode.getMyOwnCode();
//////					childnode = this.getBanKuai(childnode.getMyOwnCode(), dchgeguwkzhanbi.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
////					childnode = this.getAllGeGuOfBanKuai (childnode);
//////					treechanyelian.updateTreeParseFileInfo (childnode,stockinfile,dchgeguwkzhanbi.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
////				}
//				
////				treechanyelian.updateTreeParseFileInfo(stockinfile,dchgeguwkzhanbi.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() );
//				
//				
//				((BanKuaiGeGuTableModel)tablebkgegu.getModel()).deleteAllRows(); //�����б�ɾ����
//				//�ص��ע��2����ҲҪ����
//				((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).fireTableDataChanged();
//				((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).fireTableDataChanged();
//		    }

		    /*
		     * ѡ��Ҫ�����İ���ļ�
		     */
//		    private void selectBanKuaiParseFile ()
//			{
//		    	String parsedpath = sysconfig.getBanKuaiParsedFileStoredPath ();
//				JFileChooser chooser = new JFileChooser();
//				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
//				chooser.setCurrentDirectory(new File(parsedpath) );
//				
//				if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
//				    
//				    String linuxpath;
//				    if(chooser.getSelectedFile().isDirectory())
//				    	linuxpath = (chooser.getSelectedFile()+ "\\").replace('\\', '/');
//				    else
//				    	linuxpath = (chooser.getSelectedFile()).toString().replace('\\', '/');
//				    
////				    logger.debug(linuxpath);
//				    tfldparsefilename.setText(linuxpath);
//				    
//				    parseSelectedBanKuaiFile (linuxpath);
//				}
//				
//			} 
//		    public String getParsedFileName ()
//		    {
//		    	try {
//		    		return tfldparsefilename.getText();
//		    	} catch (java.lang.NullPointerException e) {
//		    		return "";
//		    	}
//		    }
		    

		    /*
		     * ����2��XML 
		     */
//	public boolean saveCylXmlAndZdgzXml () //GEN-FIRST:event_saveButtonActionPerformed 
//	{
//		if(btnSaveAll.isEnabled() ) {
//			BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)treechanyelian.getModel().getRoot();
//			if(!cylxmhandler.saveTreeToChanYeLianXML(treeroot) ) {
//				JOptionPane.showMessageDialog(null, "�����ҵ��XMLʧ�ܣ������ԭ��","Warning", JOptionPane.WARNING_MESSAGE);
//				return false;
//			}
//			
//			if( !zdgzbkxmlhandler.saveAllZdgzbkToXml () ) {
//				JOptionPane.showMessageDialog(null, "�����ص��ע��Ʊ��XMLʧ�ܣ������ԭ��","Warning", JOptionPane.WARNING_MESSAGE);
//				return false;
//			}
//		}
//
//		btnSaveAll.setEnabled(false);
//		return true;
//	}	    
//	private void createEvents() 
//	{
//		
//		tfldparsefilename.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent arg0) {
//				String linuxpath = tfldparsefilename.getText();
//			    
//			    parseSelectedBanKuaiFile (linuxpath);
//			}
//		});
//		
////		mntmgephi.addActionListener(new ActionListener() {
////			public void actionPerformed(ActionEvent arg0) {
////				
////			}
////		});
//		
//		cbxtichuquanzhong.addItemListener(new ItemListener() {
//			public void itemStateChanged(ItemEvent arg0) 
//			{
//				tfldquanzhong.setEnabled(!tfldquanzhong.isEnabled());
//			}
//		});
//		/*
//		 * 
//		 */
//		btndisplaybkfx.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseClicked(MouseEvent arg0) 
//			{
////				 �����ð���һ���ռ������
//				try {
//					TreePath closestPath = treechanyelian.getSelectionPath();
//					BanKuai bknode = (BanKuai) closestPath.getPathComponent(1);
//			    	 String tdxbk = bknode.getMyOwnName(); 
//			    	 String tdxbkcode = bknode.getMyOwnCode();
//			    	 
//			    	 Date actiondate = dchgeguwkzhanbi.getDate();
////			  	     displayBanKuaiZhanBi (tdxbkcode,tdxbk,actiondate);
////			  	     displayBanKuaiGeGuZhanBi (tdxbkcode,tdxbk,actiondate);
//			    	 displayBanKuaiZhanBi (bknode);
//			  	   	 displayBanKuaiGeGuZhanBi (bknode);
//				} catch ( java.lang.NullPointerException e) {
//					logger.debug("û�а�鱻ѡ��");
//				}
//				
//			}
//		});
//		/*
//		 * 
//		 */
//		dchgeguwkzhanbi.addPropertyChangeListener(new PropertyChangeListener() {
//		    @Override
//		    public void propertyChange(PropertyChangeEvent e) {
//		    	
//		    	TreePath closestPath = treechanyelian.getSelectionPath();
//		    	if(closestPath != null ) {
//		    		 BkChanYeLianTreeNode bknode = (BkChanYeLianTreeNode) closestPath.getPathComponent(1);
//			    	 String tdxbk = bknode.getMyOwnName(); 
//			    	 String tdxbkcode = bknode.getMyOwnCode();
//			    	 
//			    	if("date".equals(e.getPropertyName())) {
////			    		Date actiondate = dchgeguwkzhanbi.getDate(); 
////			    		displayBanKuaiGeGuZhanBi (tdxbkcode,tdxbk,actiondate);
////			    		displayBanKuaiZhanBi (tdxbkcode,tdxbk,actiondate);
//			    	}
//		    	}
//		    }
//		});
//		
//		buttonCjlFx.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseClicked(MouseEvent arg0) 
//			{
//				chengJiaoLiangFengXi ();
//			}
//		});
//		
//		
//		btnopencylxml.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseClicked(MouseEvent e) {
//				cylxmhandler.openChanYeLianXmlInWinSystem ();
//			}
//		});
//		
//		btnopenzdgzxml.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseEntered(MouseEvent arg0) {
//			}
//			@Override
//			public void mouseClicked(MouseEvent e) {
//				zdgzbkxmlhandler.openZdgzXmlInWinSystem ();
//			}
//		});
//		
//		btnSaveAll.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseClicked(MouseEvent arg0) 
//			{
//				saveCylXmlAndZdgzXml ();
//			}
//		});
//		
//		btnGenTDXCode.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseClicked(MouseEvent e) 
//			{
//				TDXFormatedOpt.parseZdgzBkToTDXCode(zdgzbkxmlhandler.getZdgzBkDetail ());
//			}
//		});
//		
//		btndeldalei.addMouseListener(new MouseAdapter() {
//			
//			@Override
//			public void mouseClicked(MouseEvent e) {
//				deleteDaLeiGuPiaoChi ();
//			}
//		});
//		/*
//		 * ���Ӵ���
//		 */
//		btnadddalei.addMouseListener(new MouseAdapter() {
//
//			public void mouseClicked(MouseEvent arg0) 
//			{
//				String newdaleiname = JOptionPane.showInputDialog(null,"�������µĹ�Ʊ������:","��ӹ�Ʊ��", JOptionPane.QUESTION_MESSAGE);
//				if( !newdaleiname.isEmpty() && !zdgzbkmap.keySet().contains(newdaleiname)) {
//					ArrayList<BkChanYeLianTreeNode> tmpgzbklist = new ArrayList<BkChanYeLianTreeNode> (); 
//					zdgzbkmap.put(newdaleiname, tmpgzbklist);
//					
//					unifyDisplaysInDifferentCompOnGui (newdaleiname,0);
//					
////					zdgzxmlneedsave = true;
//					btnSaveAll.setEnabled(true);
//				} else
//					JOptionPane.showMessageDialog(null,"��Ʊ�������Ѿ����ڣ�","Warning",JOptionPane.WARNING_MESSAGE);
//			}
//		});
//
//		
//				btnChsParseFile.addMouseListener(new MouseAdapter() {
//					@Override
//					public void mouseClicked(MouseEvent arg0) {
//						selectBanKuaiParseFile ();
//						
//					}
//				});
//				
//				/*
//				 * 
//				 */
//				tableZdgzBankDetails.addMouseListener(new MouseAdapter() {
//					@Override
//					public void mouseClicked(MouseEvent e) 
//					{
//						int row = tableZdgzBankDetails.getSelectedRow();
//						if(row <0) {
//							JOptionPane.showMessageDialog(null,"��ѡ��һ����ҵ��","Warning",JOptionPane.WARNING_MESSAGE);
//							return;
//						} 
//						
//						int rowInZdgz = tableCurZdgzbk.getSelectedRow();
//						String zdgzdalei = ((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).getZdgzDaLei(rowInZdgz);
//						
//						BkChanYeLianTreeNode selectedgzbk = ((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).getGuanZhuBanKuaiInfo(row);
//						
//						unifyDisplaysInDifferentCompOnGui (zdgzdalei,row);
//					}
//
//
//				});
//				/*
//				 * 
//				 */
//				btnCylRemoveFromZdgz.addMouseListener(new MouseAdapter() {
//					@Override
//					public void mouseClicked(MouseEvent arg0) 
//					{
//						removeTreeChanYeLianNodeFromZdgz (arg0);
//					}
//				});
//
//				
//				/*
//				 * �Ѳ�ҵ�����뵽�ص��ע
//				 */
//				btnCylAddToZdgz.addMouseListener(new MouseAdapter() {
//					 @Override
//					public void mouseClicked(MouseEvent arg0) 
//					{
//						 adddTreeChanYeLianNodeToZdgz ( arg0);
//		
//					}
//				});
//
//				
//				
//				buttonremoveoffical.addMouseListener(new MouseAdapter() {
//					@Override
//					public void mouseClicked(MouseEvent arg0) 
//					{
//						removeChanYeLianFromOffical ();
//					}
//				});
//				
//				
//				buttonaddofficial.addMouseListener(new MouseAdapter() {
//					@Override
//					public void mouseClicked(MouseEvent arg0) 
//					{
//						addChanYeLianToOffical ();
//					}
//					
//
//				});
//				
//				tableCurZdgzbk.addMouseListener(new MouseAdapter() {
//					@Override
//					public void mouseClicked(MouseEvent arg0) 
//					{
//						int row = tableCurZdgzbk.getSelectedRow();
//						if(row <0) {
//							JOptionPane.showMessageDialog(null,"��ѡ��һ������","Warning",JOptionPane.WARNING_MESSAGE);
//							return;
//						} 
//						
//						//initializeSingleDaLeiZdgzTableFromXml (0);
//						String selecteddalei = ((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).getZdgzDaLei (row);
//						ArrayList<BkChanYeLianTreeNode> selectedgzbklist = ((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).getDaLeiChanYeLianList(selecteddalei);
//						BkChanYeLianTreeNode selectedgzbk = null ;
//						try {
//							 selectedgzbk = selectedgzbklist.get(0);
//						} catch ( java.lang.IndexOutOfBoundsException e) {
//							
//						}
//						
//						unifyDisplaysInDifferentCompOnGui(selecteddalei,0);
//						
//
//					}
//
//				});
//				
//				tfldfindgegu.addMouseListener(new MouseAdapter() {
//					@Override
//					public void mouseClicked(MouseEvent arg0) {
//						tfldfindgegu.setText("");
//					}
//				});
//				/*
//				 * ���Ҹ���
//				 */
//				tfldfindgegu.addActionListener(new AbstractAction() {
//					public void actionPerformed(ActionEvent arg0) {
//						String bkinputed = tfldfindgegu.getText();
//						int rowindex = ((BanKuaiGeGuTableModel)tablebkgegu.getModel()).getStockRowIndex(bkinputed);
//						int modelRow = tablebkgegu.convertRowIndexToView(rowindex);
//						if(rowindex != -1) {
//							tablebkgegu.setRowSelectionInterval(modelRow, modelRow);
//							tablebkgegu.scrollRectToVisible(new Rectangle(tablebkgegu.getCellRect(modelRow, 0, true)));
//						}
//						
//					}
//				});
//				
//				tfldfindbk.addMouseListener(new MouseAdapter() {
//					@Override
//					public void mouseClicked(MouseEvent arg0) {
//						tfldfindbk.setText("");
//					}
//				});
//				/*
//				 * ���Ұ��
//				 */
//				tfldfindbk.addActionListener(new AbstractAction() {
//					public void actionPerformed(ActionEvent arg0) {
//						String bkinputed = tfldfindbk.getText();
//						findBanKuaiInTree (bkinputed);
//					}
//				});
//				
//				btnfindbk.addMouseListener(new MouseAdapter() {
//					@Override
//					public void mouseClicked(MouseEvent arg0) 
//					{
//						String bkinputed = tfldfindbk.getText();
//						findBanKuaiInTree (bkinputed);
//					}
//				});
//				
//				addKeyListener(new KeyAdapter() {
//					@Override
//					public void keyTyped(KeyEvent e) 
//					{
//						 int id = e.getID();
//						 String keyString;
//					        if (id == KeyEvent.KEY_TYPED) {
//					            char c = e.getKeyChar();
//					            keyString = "key character = '" + c + "'";
//					            logger.debug(keyString);
//					        } else {
//					        	int keyCode = e.getKeyCode();
//					            keyString = "key code = " + keyCode
//					                    + " ("
//					                    + KeyEvent.getKeyText(keyCode)
//					                    + ")";
//					        }
//					        
//					}
//				});
//				
//
//		        treechanyelian.addMouseListener(new java.awt.event.MouseAdapter() {
//		            public void mousePressed(java.awt.event.MouseEvent evt) {
////		            	chanYeLianTreeMousePressed(evt);
////		            	logger.debug("get action notice at bkcyl");
//		    	        TreePath closestPath = treechanyelian.getClosestPathForLocation(evt.getX(), evt.getY());
//
//		    	        if(closestPath != null) {
//		    	            Rectangle pathBounds = treechanyelian.getPathBounds(closestPath);
//		    	            int maxY = (int) pathBounds.getMaxY();
//		    	            int minX = (int) pathBounds.getMinX();
//		    	            int maxX = (int) pathBounds.getMaxX();
//		    	            if (evt.getY() > maxY) 
//		    	            	treechanyelian.clearSelection();
//		    	            else if (evt.getX() < minX || evt.getX() > maxX) 
//		    	            	treechanyelian.clearSelection();
//		    	        }
//		    	        getReleatedInfoAndActionsForTreePathNode ( closestPath);
//		            }
//		        });
//
//		        
//				btnAddSubBk.addMouseListener(new MouseAdapter() {
//					@Override
//					public void mouseClicked(MouseEvent arg0) 
//					{
//						String newsubbk = JOptionPane.showInputDialog(null,"�������Ӱ������:","�����Ӱ��", JOptionPane.QUESTION_MESSAGE);
//						if(newsubbk == null)
//							return;
//						
////						if(bkdbopt.getSysBkSet().contains(newsubbk) ) {
////							JOptionPane.showMessageDialog(null,"�����Ӱ��������ͨ���Ű�����Ƴ�ͻ,����������!");
////							return ;
////						}
//						
//						TreePath closestPath = treechanyelian.getSelectionPath();
////				        logger.debug(closestPath);
//				         BkChanYeLianTreeNode tdxbk = (BkChanYeLianTreeNode)closestPath.getPathComponent(1);
//				         String tdxbkcode = tdxbk.getMyOwnCode();
//				        
//						String newsubcylcode = bkdbopt.addNewSubBanKuai (tdxbkcode,newsubbk.trim() ); 
//						if(newsubcylcode != null)
//							((BanKuaiSubChanYeLianTableModel)(tablesubcyl.getModel())).addRow(newsubcylcode,newsubbk);
//						else
//							JOptionPane.showMessageDialog(null,"���ʧ�ܣ���������Ϊ������","Warning",JOptionPane.WARNING_MESSAGE);
////			  	        ((BanKuaiSubChanYeLianTableModel)(tablesubcyl.getModel())).fireTableDataChanged ();
//						
//					}
//				});
//				
//
//
//				addGeGuButton.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
//			            public void mouseMoved(java.awt.event.MouseEvent evt) {
//			            	addGeGuButtonMouseMoved(evt);
//			            }
//			        });
//
//				addGeGuButton.addMouseListener(new java.awt.event.MouseAdapter() {
//			            public void mouseExited(java.awt.event.MouseEvent evt) {
//			            	addGeGuButtonMouseExited(evt);
//			            }
//			        });
//				addGeGuButton.addActionListener(new java.awt.event.ActionListener() {
//			            public void actionPerformed(java.awt.event.ActionEvent evt) {
//			                addGeGuButtonActionPerformed(evt);
//			            }
//			        });
//			
//
//				
//		        addSubnodeButton.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
//		            public void mouseMoved(java.awt.event.MouseEvent evt) {
//		                addSubnodeButtonMouseMoved(evt);
//		            }
//		        });
//		        addSubnodeButton.addMouseListener(new java.awt.event.MouseAdapter() {
//		            public void mouseExited(java.awt.event.MouseEvent evt) {
//		                addSubnodeButtonMouseExited(evt);
//		            }
//		        });
//		        addSubnodeButton.addActionListener(new java.awt.event.ActionListener() {
//		            public void actionPerformed(java.awt.event.ActionEvent evt) {
//		                addSubnodeButtonActionPerformed(evt);
//		            }
//		        });
//		        
//
//		        
//		        deleteButton.addActionListener(new java.awt.event.ActionListener() {
//		            public void actionPerformed(java.awt.event.ActionEvent evt) {
//		                deleteButtonActionPerformed(evt);
//		            }
//		        });
//			}
	    
	
	
//	protected void chengJiaoLiangFengXi() 
//	{
////		String curselectedbknodecode = null;
////		try {
////			TreePath closestPath = treechanyelian.getSelectionPath();
////			BkChanYeLianTreeNode curselectedbknode;
////			curselectedbknode = (BkChanYeLianTreeNode) closestPath.getLastPathComponent();
////		    String curselectedbknodename = curselectedbknode.getMyOwnName();
////		    curselectedbknodecode = curselectedbknode.getMyOwnCode();
////		} catch (java.lang.NullPointerException e) {
////			BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)this.treechanyelian.getModel().getRoot();
////			BkChanYeLianTreeNode curselectedbknode;
////			curselectedbknode = (BanKuai)this.treechanyelian.getModel().getChild(treeroot, 0);
////		}
//
//		if(bkfx == null)
//			if(this.stockInfoManager.getBanKuaiFengXi() == null ) {
////				bkfx = new BanKuaiFengXi (treechanyelian,curselectedbknodecode,"",dchgeguwkzhanbi.getDate());
//				bkfx = new BanKuaiFengXi (stockInfoManager,this);
//				bkfx.setModal(false);
//				bkfx.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
//				bkfx.setVisible(true);
////				stockInfoManager.setBanKuaiFengXi(bkfx);
//			} else {
//				bkfx = this.stockInfoManager.getBanKuaiFengXi();
//			}
////		bkfx.setBanKuaiEndiorPaneContents ( curselectedbknodecode );
////		bkfx.setFenXiDate ( dchgeguwkzhanbi.getDate() );
//		if(!Strings.isNullOrEmpty(tfldparsefilename.getText()))
//			bkfx.setParsedFile(tfldparsefilename.getText() );
//		
//		if(!bkfx.isVisible() ) {
//			bkfx.setVisible(true);
//		 } 
//		bkfx.toFront();
//	}
//	
//	public BanKuaiFengXi getBanKuaiFengXi ()
//	{
//		return bkfx;
//	};

//	protected void addGeGuNews() 
//	{
//		int row = tablebkgegu.getSelectedRow();
//		if(row <0) {
//			JOptionPane.showMessageDialog(null,"��ѡ��һ����Ʊ","Warning",JOptionPane.WARNING_MESSAGE);
//			return;
//		}
//		
//		String stockcode = ((BanKuaiGeGuTableModel) tablebkgegu.getModel()).getStockCode (row);
//		ChanYeLianNewsPanel cylnews = new ChanYeLianNewsPanel (stockcode);
//		int exchangeresult = JOptionPane.showConfirmDialog(null, cylnews, "���Ӹ�������", JOptionPane.OK_CANCEL_OPTION);
//		System.out.print(exchangeresult);
//		if(exchangeresult == JOptionPane.CANCEL_OPTION)
//			return;
//	}

//	protected void addChanYeLianNews() 
//	{
//		String selectnodecode = null;
//		try {
//			TreePath closestPath = treechanyelian.getSelectionPath();
//			BkChanYeLianTreeNode selectednode = (BkChanYeLianTreeNode)closestPath.getLastPathComponent();
//			 selectnodecode = selectednode.getMyOwnCode();
//		} catch (java.lang.NullPointerException ex) {
//			JOptionPane.showMessageDialog(null,"��ѡ���ҵ��飡","Warning",JOptionPane.WARNING_MESSAGE);
//		}
//		
//		ChanYeLianNewsPanel cylnews = new ChanYeLianNewsPanel (selectnodecode);
//		cylnews.setVisible(true);
//		int exchangeresult = JOptionPane.showConfirmDialog(null, cylnews, "���Ӳ�ҵ������", JOptionPane.OK_CANCEL_OPTION);
//		System.out.print(exchangeresult);
//		if(exchangeresult == JOptionPane.CANCEL_OPTION)
//			return;
		
//		bkdbopt.addBanKuaiNews(selectnodecode, cylnews.getInputedNews());
//	}

//	private void deleteDaLeiGuPiaoChi () 
//	{
//		int row = tableCurZdgzbk.getSelectedRow();
//		if(row <0) {
//			JOptionPane.showMessageDialog(null,"��ѡ��һ����Ʊ��","Warning",JOptionPane.WARNING_MESSAGE);
//			return;
//		}
//		
//		String daleiname = ((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).getZdgzDaLei(row);
//		int n = JOptionPane.showConfirmDialog(null, "ȷ��ɾ��" + daleiname + "?", "ɾ����Ʊ��",JOptionPane.YES_NO_OPTION);//i=0/1  
//		if(n == 0) {
//			ArrayList<BkChanYeLianTreeNode> daleicyl = zdgzbkxmlhandler.getASubDaiLeiDetail(daleiname);
//			for(BkChanYeLianTreeNode tmpgzbkinfo : daleicyl ) {
//				 treechanyelian.removeZdgzBkCylInfoFromTreeNode (tmpgzbkinfo,false);
//			}
//			
//			zdgzbkxmlhandler.deleteDaLei(daleiname);
//			
//			String selecteddalei = ((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).getZdgzDaLei (0);
//			unifyDisplaysInDifferentCompOnGui (selecteddalei,0);
////			initializeAllDaLeiZdgzTableFromXml (null);
////			initializeSingleDaLeiZdgzTableFromXml (0);
//			
////			zdgzxmlneedsave = true;
//			btnSaveAll.setEnabled(true);
//		}
//		
//	}
	
	/*
	 * �Ƴ��ص��ע
	 */
//	private void removeTreeChanYeLianNodeFromZdgz(MouseEvent arg0) 
//	{
//		int row = tableZdgzBankDetails.getSelectedRow();
//		if(row <0) {
//			JOptionPane.showMessageDialog(null,"��ѡ��һ������ҵ��","Warning",JOptionPane.WARNING_MESSAGE);
//			return;
//		}
//
//		String daleiname = getCurSelectedDaLei();
//		 if( daleiname != null) {
//			  BkChanYeLianTreeNode gzcyl = ((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).getGuanZhuBanKuaiInfo(row);
//			  
//			  JiaRuJiHua jiarujihua = new JiaRuJiHua ( gzcyl.getMyOwnCode(), "�Ƴ��ص�" ); 
//			  int exchangeresult = JOptionPane.showConfirmDialog(null, jiarujihua, "�Ƴ��ص��ע", JOptionPane.OK_CANCEL_OPTION);
//			  if(exchangeresult == JOptionPane.CANCEL_OPTION)
//					return;
//					
//			  int autoIncKeyFromApi =	bkdbopt.setZdgzRelatedActions (jiarujihua);
//			  jiarujihua.setDbRecordsId(autoIncKeyFromApi);
//			  
//			 treechanyelian.removeZdgzBkCylInfoFromTreeNode (gzcyl,false);
//			 zdgzbkxmlhandler.removeGuanZhuBanKuai(daleiname, gzcyl);
//			 unifyDisplaysInDifferentCompOnGui (daleiname,0);
//			 
//			 this.stockInfoManager.updateTableAfterZdgz (jiarujihua);
//			 
////			 zdgzxmlneedsave = true;
//			 btnSaveAll.setEnabled(true);
//		 } else
//			 JOptionPane.showMessageDialog(null,"��ѡ��һ������","Warning",JOptionPane.WARNING_MESSAGE);
//		
//	}

//	private void addChanYeLianToOffical ()
//	{
//		int row = tableZdgzBankDetails.getSelectedRow();
//		if(row <0) {
//			JOptionPane.showMessageDialog(null,"��ѡ��һ����ҵ��","Warning",JOptionPane.WARNING_MESSAGE);
//			return ;
//		}
//		
//		String selectedalei = getCurSelectedDaLei ();
//		if( selectedalei != null) {
//			 BkChanYeLianTreeNode gzbk = ((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).getGuanZhuBanKuaiInfo(row);
//			 gzbk.getNodetreerelated().setOfficallySelected(true);
//			 
//			 treechanyelian.addZdgzBkCylInfoToTreeNode(gzbk,true);
//			 unifyDisplaysInDifferentCompOnGui (selectedalei,row);
//
////			 zdgzxmlneedsave = true;
//			 btnSaveAll.setEnabled(true);
//		} else
//			 JOptionPane.showMessageDialog(null,"��ѡ��һ������","Warning",JOptionPane.WARNING_MESSAGE);
//	}
	/*
	 * ��offical�Ƴ��ص��ע
	 */
//	private void removeChanYeLianFromOffical ()
//	{
//		int row = tableZdgzBankDetails.getSelectedRow();
//		if(row <0) {
//			JOptionPane.showMessageDialog(null,"��ѡ��һ����ҵ��","Warning",JOptionPane.WARNING_MESSAGE);
//			return ;
//		}
//		
//		String selectedalei = getCurSelectedDaLei ();
//		if( selectedalei != null) {
//			 BkChanYeLianTreeNode gzbk = ((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).getGuanZhuBanKuaiInfo(row);
//			 if(!gzbk.getNodetreerelated().isOfficallySelected()) {
//				 JOptionPane.showMessageDialog(null,"ѡ��Ĳ�ҵ��ֻ�Ǻ򲹹�ע�����ڵ�ǰ�ص��ע��Ʊ����,�����Ƴ���","Warning",JOptionPane.WARNING_MESSAGE);
//				 return;
//			 }
//				 
//			 if(JOptionPane.showConfirmDialog(null, "�Ƿ�ֱ�ӴӺ򲹲�ҵ����ɾ����","Warning", JOptionPane.YES_NO_OPTION) == 0) {
//					zdgzbkxmlhandler.removeGuanZhuBanKuai(selectedalei, gzbk);
//					unifyDisplaysInDifferentCompOnGui (selectedalei,0);
//					treechanyelian.removeZdgzBkCylInfoFromTreeNode (gzbk,false);
//				} else {
//					unifyDisplaysInDifferentCompOnGui (selectedalei,row);
//					treechanyelian.removeZdgzBkCylInfoFromTreeNode (gzbk,true);
//				}
//
////			 zdgzxmlneedsave = true;
//			 btnSaveAll.setEnabled(true);
//			 
//		} else
//			 JOptionPane.showMessageDialog(null,"��ѡ��һ������","Warning",JOptionPane.WARNING_MESSAGE);
//		
//	}
		
	/*
	 * �����ص��ע
	 */
//	private void adddTreeChanYeLianNodeToZdgz(MouseEvent arg0) 
//	{
//		 String daleiname = getCurSelectedDaLei();
//		 if( daleiname != null) {
//			 TreePath path = treechanyelian.getSelectionPath();
//			 BkChanYeLianTreeNode nodewilladded = (BkChanYeLianTreeNode) treechanyelian.getSelectionPath().getLastPathComponent();
//			 
//			 JiaRuJiHua jiarujihua = new JiaRuJiHua ( nodewilladded.getMyOwnCode(), "�����ص�" ); 
//			 int exchangeresult = JOptionPane.showConfirmDialog(null, jiarujihua, "�����ص��ע", JOptionPane.OK_CANCEL_OPTION);
//			 if(exchangeresult == JOptionPane.CANCEL_OPTION)
//				return;
//				
//			 int autoIncKeyFromApi =	bkdbopt.setZdgzRelatedActions (jiarujihua);
//			 jiarujihua.setDbRecordsId(autoIncKeyFromApi);
//			  
//
//			 boolean isofficallyselected = false;
//			 if(JOptionPane.showConfirmDialog(null, "�Ƿ�ֱ�Ӽ����ص��ע��","Warning", JOptionPane.YES_NO_OPTION) == 1) {
//				 nodewilladded.getNodetreerelated().setOfficallySelected(false);
//			 } else 
//				 nodewilladded.getNodetreerelated().setOfficallySelected(true);
//			 
//			 nodewilladded.getNodetreerelated().setSelectedToZdgzTime( new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()  );
//			 
//			 treechanyelian.addZdgzBkCylInfoToTreeNode(nodewilladded,false);
//			 zdgzbkxmlhandler.addNewGuanZhuBanKuai(daleiname, nodewilladded);
//			 int addedrow = ((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).getGuanZhuBanKuaiInfoIndex(nodewilladded);
//			 unifyDisplaysInDifferentCompOnGui (daleiname,addedrow);
//			
//			 this.stockInfoManager.updateTableAfterZdgz (jiarujihua);
////			 zdgzxmlneedsave = true;
//			 btnSaveAll.setEnabled(true);
//		 }
//		 else
//			 JOptionPane.showMessageDialog(null,"��ѡ��һ������","Warning",JOptionPane.WARNING_MESSAGE);
//		 
//	}
	
	

}


