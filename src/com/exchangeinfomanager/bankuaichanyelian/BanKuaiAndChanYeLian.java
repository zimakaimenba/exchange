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

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.DaPan;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.asinglestockinfo.SubnodeButton;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiGeGuTable;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiGeGuTableModel;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.DisplayBkGgInfoEditorPane;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.ChanYeLianNews;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.ChanYeLianNewsPanel;
import com.exchangeinfomanager.bankuaifengxi.BanKuaiFengXi;
import com.exchangeinfomanager.bankuaifengxi.BanKuaiFengXiBarChartPnl;
import com.exchangeinfomanager.bankuaifengxi.BanKuaiFengXiPieChartPnl;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.gui.subgui.BanKuaiPopUpMenu;
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

public class BanKuaiAndChanYeLian extends JPanel 
{
	private static final long serialVersionUID = 1L;

	public BanKuaiAndChanYeLian (StockInfoManager stockInfoManager2 ) 
	{
		initializeSysConfig ();
		this.stockInfoManager = stockInfoManager2;
		this.bkdbopt = new BanKuaiDbOperation ();

		this.cylxmhandler = new ChanYeLianXMLHandler ();
		this.zdgzbkxmlhandler = new TwelveZhongDianGuanZhuXmlHandler ();
		
		treechanyelian = initializeBkChanYeLianXMLTree();
		DaPan treerootdapan = (DaPan)treechanyelian.getModel().getRoot();
		BanKuai sh = this.getBanKuai("999999", LocalDate.now());
		BanKuai sz = this.getBanKuai("399001", LocalDate.now());
		treerootdapan.setShangHai(sh);
		treerootdapan.setShenZhen(sz);
		
		zdgzbkmap = zdgzbkxmlhandler.getZdgzBanKuaiFromXmlAndUpatedToCylTree(treechanyelian);
//		startGui ();
	}
	public void startGui ()
	{
		initializeGui ();
		initializeAllDaLeiZdgzTableFromXml ();
		initializeBanKuaiParsedFile ();
		createEvents ();
		if(cylxmhandler.hasXmlRevised())
			btnSaveAll.setEnabled(true);
	}

	public static final int UP=0, LEFT=1, RIGHT=2, DOWN=3, NONE=4;
	
	private SystemConfigration sysconfig;
	HashMap<String, ArrayList<BkChanYeLianTreeNode>> zdgzbkmap;
	private ChanYeLianXMLHandler cylxmhandler;
    private TwelveZhongDianGuanZhuXmlHandler zdgzbkxmlhandler;
    private BkChanYeLianTree treechanyelian;
    private BanKuaiDbOperation bkdbopt;
	private StockInfoManager stockInfoManager;
	private BanKuaiFengXi bkfx ;

	private String currentselectedtdxbk = "";

	private HashSet<String> stockinfile;

	
	
	/*
	 * 获得板块，并同步上证/深圳指数的成交量，以保住板块和上证/深圳的成交量记录日期跨度是完全的。
	 */
	public BanKuai getBanKuai (BanKuai bankuai,LocalDate requiredrecordsday)
	{
		LocalDate bkstartday = bankuai.getRecordsStartDate();
		LocalDate bkendday = bankuai.getRecordsEndDate();
		
		LocalDate requireend = requiredrecordsday.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = requiredrecordsday.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange(),ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		
		//判断时间的相关关系，以便决定是否需要到数据库中查询新纪录
		if(bkstartday == null || bkendday == null) { //还没有数据，直接找
			bankuai = bkdbopt.getBanKuaiZhanBi (bankuai,requirestart,requireend,requirestart);
		} else	{
			HashMap<String,LocalDate> startend = nodeTimePeriodRelation (bkstartday,bkendday,requirestart,requireend);
			if(!startend.isEmpty()) {
				LocalDate searchstart,searchend,position;
				searchstart = startend.get("searchstart"); 
				searchend = startend.get("searchend");
				position = 	startend.get("position");	
				bankuai = bkdbopt.getBanKuaiZhanBi (bankuai,searchstart,searchend,position);
			}
		}
	
		String bkcode = bankuai.getMyOwnCode();
		if(!bkcode.equals("399001") && !bkcode.equals("999999") ) { //2个大盘指数
			sysncDaPanChenJiaoEr (requiredrecordsday);
		}
		
		return bankuai;
		
	}
	public BanKuai getBanKuai (String bkcode,LocalDate requiredrecordsday) 
	{
		BanKuai bankuai = (BanKuai) treechanyelian.getSpecificNodeByHypyOrCode(bkcode);
		bankuai = this.getBanKuai (bankuai,requiredrecordsday);
		return bankuai;
	}
	/*
	 * 
	 */
	public BanKuai getAllGeGuOfBanKuai (BanKuai bankuai) 
	{
		LocalDate bkstartday = bankuai.getRecordsStartDate();
		LocalDate bkendday = bankuai.getRecordsEndDate();
		
		if(bkstartday == null || bkendday == null) {
			bkendday = dchgeguwkzhanbi.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();;
			bkstartday = bkendday.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange(),ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		}
		//同步板块的个股
		bankuai = bkdbopt.getTDXBanKuaiGeGuOfHyGnFg (bankuai,bkstartday,bkendday);
		HashMap<String, Stock> allbkgg = bankuai.getAllBanKuaiGeGu();
		if(allbkgg != null )
			for (Map.Entry<String, Stock> entry : allbkgg.entrySet()) {  
				  Stock stock = entry.getValue();
				  stock = this.getGeGuOfBanKuai(bankuai, stock );
				  
			} 
		return bankuai;
	}
	private Stock getGeGuOfBanKuai(BanKuai bankuai, String stockcode)
	{
		Stock stock = bankuai.getBanKuaiGeGu(stockcode);
		stock = this.getGeGuOfBanKuai( bankuai,  stock);
		return stock;
	}
	private Stock getGeGuOfBanKuai(BanKuai bankuai, Stock stock)
	{
		LocalDate bkstartday = bankuai.getRecordsStartDate();
		LocalDate bkendday = bankuai.getRecordsEndDate();
		
		LocalDate stockstartday = stock.getRecordsStartDate();
		LocalDate stockendday = stock.getRecordsEndDate();
		
		if(stockstartday == null || stockendday == null ) { //还没有数据，直接找
			stock = bkdbopt.getGeGuZhanBiOfBanKuai (bankuai.getMyOwnCode(),stock, bkstartday, bkendday,bkstartday);
		} else {
			HashMap<String,LocalDate> startend = nodeTimePeriodRelation (stockstartday,stockendday,bkstartday,bkendday);
			if(!startend.isEmpty()) {
				LocalDate searchstart,searchend,position;
				searchstart = startend.get("searchstart"); 
				searchend = startend.get("searchend");
				position = 	startend.get("position");	
				stock = bkdbopt.getGeGuZhanBiOfBanKuai (bankuai.getMyOwnCode(),stock,searchstart,searchend,position);
			}
		}
		
		return stock;

	}
	/*
	 * 返回这个板块的某个个股的成交量,不做板块是否包含该个股的检查
	 */
	public Stock getGeGuOfBanKuai(String bkcode, String stockcode) 
	{
		BanKuai bankuai = (BanKuai) treechanyelian.getSpecificNodeByHypyOrCode(bkcode);
		
		LocalDate bkstartday = bankuai.getRecordsStartDate();
		LocalDate bkendday = bankuai.getRecordsEndDate();
		
		Stock stock = bankuai.getBanKuaiGeGu(stockcode);
		LocalDate stockstartday = stock.getRecordsStartDate();
		LocalDate stockendday = stock.getRecordsEndDate();
		
		if(stockstartday == null || stockendday == null ) { //还没有数据，直接找
			stock = bkdbopt.getGeGuZhanBiOfBanKuai (bkcode,stock, bkstartday, bkendday,bkstartday);
		} else {
			HashMap<String,LocalDate> startend = nodeTimePeriodRelation (stockstartday,stockendday,bkstartday,bkendday);
			if(!startend.isEmpty()) {
				LocalDate searchstart,searchend,position;
				searchstart = startend.get("searchstart"); 
				searchend = startend.get("searchend");
				position = 	startend.get("position");	
				stock = bkdbopt.getGeGuZhanBiOfBanKuai (bkcode,stock,searchstart,searchend,position);
			}
		}

		return stock;
	}
	/*
	 * 同步大盘成交额
	 */
	private void sysncDaPanChenJiaoEr (LocalDate requiredrecordsday)
	{
		BanKuai shdpbankuai = (BanKuai) treechanyelian.getSpecificNodeByHypyOrCode("999999");
		BanKuai szdpbankuai = (BanKuai) treechanyelian.getSpecificNodeByHypyOrCode("399001");
		
		LocalDate bkstartday = shdpbankuai.getRecordsStartDate();
		LocalDate bkendday = shdpbankuai.getRecordsEndDate();
		
		LocalDate requireend = requiredrecordsday.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = requiredrecordsday.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange(),ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		
		
		//判断时间的相关关系，以便决定是否需要到数据库中查询新纪录
		if(bkstartday == null || bkendday == null) { //还没有数据，直接找
			shdpbankuai = bkdbopt.getBanKuaiZhanBi (shdpbankuai,requirestart,requireend,requirestart);
			szdpbankuai = bkdbopt.getBanKuaiZhanBi (szdpbankuai,requirestart,requireend,requirestart);
		} else	{
			HashMap<String,LocalDate> startend = nodeTimePeriodRelation (bkstartday,bkendday,requirestart,requireend);
			LocalDate searchstart,searchend,position;
			if(!startend.isEmpty()) {
				searchstart = startend.get("searchstart"); 
				searchend = startend.get("searchend");
				position = 	startend.get("position");	
				shdpbankuai = bkdbopt.getBanKuaiZhanBi (shdpbankuai,searchstart,searchend,position);
				szdpbankuai = bkdbopt.getBanKuaiZhanBi (szdpbankuai,searchstart,searchend,position);
			}
		}
	}
	/*
	 * 确定时间点直接的关系
	 */
	private  HashMap<String, LocalDate> nodeTimePeriodRelation (LocalDate curstart,LocalDate curend, LocalDate requiredstart, LocalDate requiredend) 
	{
		HashMap<String,LocalDate> startend = new HashMap<String,LocalDate> (); 
		if(  CommonUtility.isInSameWeek(curstart,requiredstart) &&  CommonUtility.isInSameWeek(requiredend,curend)    ) {//数据完整
			return startend;
		}	
		else if( (requiredstart.isAfter(curstart) || requiredstart.isEqual(curstart) ) && (requiredend.isBefore(curend) || requiredend.isEqual(curend) ) ) {  //数据完整
			return startend;
		}
		else if( !CommonUtility.isInSameWeek(curstart,requiredstart)  && requiredstart.isBefore(curstart) //部分完整1,前缺失
				&& (requiredend.isBefore(curend) || CommonUtility.isInSameWeek(requiredend,curend) )    ) {
			LocalDate searchstart,searchend;
			searchstart = requiredstart; 
			searchend = curstart.with(DayOfWeek.SATURDAY).minus(1,ChronoUnit.WEEKS);
			startend.put("searchstart", searchstart);
			startend.put("searchend",  searchend);
			startend.put("position",curstart);
			return startend;
		}
		else if ( (CommonUtility.isInSameWeek(curstart,requiredstart) || requiredstart.isAfter(curstart) )  //部分完整2，后缺失
				&& requiredend.isAfter(curend) &&  !CommonUtility.isInSameWeek(requiredend,curend)    ) {
			LocalDate searchstart,searchend;
			searchstart = curend.with(DayOfWeek.MONDAY).plus(1,ChronoUnit.WEEKS); 
			searchend = requiredend;
			startend.put("searchstart", searchstart);
			startend.put("searchend", searchend);
			startend.put("position",curend);
			return startend;
		}
		else if( requiredstart.isBefore(curstart) && requiredend.isAfter(curend)  ) {//部分完整3， 前后双缺失，这种情况目前似乎不可能发生，暂时不写
			System.out.println("当前似乎不可能，");
			return startend;
		}

		return null;
	}
	
	private  BkChanYeLianTree initializeBkChanYeLianXMLTree()
	{
		BkChanYeLianTreeNode topNode = cylxmhandler.getBkChanYeLianXMLTree();
		BkChanYeLianTree tmptreechanyelian = new BkChanYeLianTree(topNode);
		return tmptreechanyelian;
	}
	public BkChanYeLianTree getBkChanYeLianTree () {
		return treechanyelian;
	}
	/*
	 * 判断是否属于重点关注板块
	 */
	public Multimap<String,String> checkBanKuaiSuoSuTwelveDaLei (Set<String> union)
	{
		return zdgzbkxmlhandler.subBkSuoSuTwelveDaLei (union);
	}
	/*
	 * 股票所属产业链
	 */
	public Stock getStockChanYeLianInfo (Stock stockbasicinfo2)
	{
		cylxmhandler.getStockChanYeLianInfo(stockbasicinfo2);
		return stockbasicinfo2;
	}
	/*
	 * 获取个股产业链
	 */
	public ArrayList<String> getGeGuChanYeLian(String stockcode)
	{
		return cylxmhandler.getGeGuChanYeLian(stockcode);
	}
	

	/*
	 * 
	 */
	private void initializeAllDaLeiZdgzTableFromXml ()
	{
		((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).refresh(zdgzbkmap);
		try {
			tableCurZdgzbk.setRowSelectionInterval(0,0);
			int row = tableCurZdgzbk.getSelectedRow();
			String selecteddalei = ((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).getZdgzDaLei (row);
			
			unifyDisplaysInDifferentCompOnGui (selecteddalei,0);
		} catch (java.lang.IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		
	}

	private String getCurSelectedDaLei ()
    {
    	int row = tableCurZdgzbk.getSelectedRow();
		if(row <0) {
			return null;
		} 
		
		 String selecteddalei = ((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).getZdgzDaLei (row);
		 tableCurZdgzbk.setRowSelectionInterval(row, row);
		 return  selecteddalei;
    }
	/*
	 * 统一界面上各个部件鼠标点击后的动作，保住一致。
	 */
	private void unifyDisplaysInDifferentCompOnGui (String selecteddalei,int selectrowfordaleiinsubcyl) 
	{
		//for 重点关注
		((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).refresh(zdgzbkmap);
		int selecteddaleirow = ((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).getDaLeiIndex (selecteddalei);
		tableCurZdgzbk.setRowSelectionInterval(selecteddaleirow,selecteddaleirow);
		
		//for sub重点关注
		((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).refresh(zdgzbkmap,selecteddalei);
		if( ((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).getRowCount() != 0)
			tableZdgzBankDetails.setRowSelectionInterval(selectrowfordaleiinsubcyl,selectrowfordaleiinsubcyl);
		
		BkChanYeLianTreeNode curselectnode = ((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).getGuanZhuBanKuaiInfo(selectrowfordaleiinsubcyl);
//		if(curselectnode != null) {
//
//			((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).fireTableDataChanged();
//			tableCurZdgzbk.setRowSelectionInterval(selecteddaleirow,selecteddaleirow);
//		}
		//System.out.println("current node is  " + currentselectedtdxbk );

		//for tree
		if(curselectnode != null) {
			TreePath nodepath = new TreePath(curselectnode.getPath());
			treechanyelian.expandTreePathAllNode( new TreePath(curselectnode.getPath()) );
		   		
		    //for 个股Talble
			getReleatedInfoAndActionsForTreePathNode (new TreePath(curselectnode.getPath()) );
			
//            this.setSelectionPath(bkpath);
//    	     this.scrollPathToVisible(bkpath);
//    	     this.expandTreePathAllNode(bkpath);
		}
	}
    
    /*
     * 和选择板块相关的子产业链，个股 
     */
    private void getReleatedInfoAndActionsForTreePathNode (TreePath closestPath)
    {
    	bkfxpnl.resetDate ();
        pnlGeGuZhanBi.resetDate();
        
    	 BanKuai bknode = (BanKuai) closestPath.getPathComponent(1);
    	 String tdxbkcode = bknode.getMyOwnCode();
    	 
    	 HashSet<String> stockinparsefile = bknode.getParseFileStockSet ();
         if(!tdxbkcode.equals(currentselectedtdxbk)) { //和当前的板块不一样，
  	       	//鼠标点击某个树，读出所属的通达信板块，在读出该板块的产业链子板块 
  	       	HashMap<String, String> tmpsubbk = bkdbopt.getSubBanKuai (tdxbkcode);
//  	       	((BanKuaiSubChanYeLianTableModel)(tablesubcyl.getModel())).setRowCount(0);
  	        ((BanKuaiSubChanYeLianTableModel)(tablesubcyl.getModel())).refresh(tmpsubbk);
  	       
	  	     //读出该板块当前所有的个股，读出的是本周在该板块内存在的所有个股，而不是当天在该板块存在的个股
	  	     LocalDate curselectdate = dchgeguwkzhanbi.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	  	     bknode = this.getBanKuai(bknode, curselectdate);
	  	     bknode = this.getAllGeGuOfBanKuai (bknode);
	  	     //装配上每日板块文件
	  	     if( stockinfile != null &&  stockinfile.size()>0)
	  	    	 treechanyelian.updateParseFileInfoToTreeFromSpecificNode (bknode,stockinfile,dchgeguwkzhanbi.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() );
//			model.nodeStructureChanged(bknode);
	  	     
	  	     HashMap<String, Stock> allbkge = bknode.getAllBanKuaiGeGu();
  	       	((BanKuaiGeGuTableModel)(tablebkgegu.getModel())).refresh(bknode, curselectdate);
  	       	tablebkgegu.sortByParsedFile();
  	       	
  	       	int row = tableCurZdgzbk.getSelectedRow();
  	       	((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).fireTableDataChanged();
  	       	if(row >= 0)
  	       		tableCurZdgzbk.setRowSelectionInterval(row,row);
  	       	else
  	       		tableCurZdgzbk.setRowSelectionInterval(0,0);
  	       	
//	  	     读出该板块相关的新闻
	  	    BkChanYeLianTreeNode curselectedbknode = (BkChanYeLianTreeNode) closestPath.getLastPathComponent();
	  	    notesPane.displayNodeAllInfo(curselectedbknode);
//	  	    createChanYeLianNewsHtml (curselectedbknode);
//	  	    displayNodeBasicInfo(curselectedbknode);
  	       	
  	       	currentselectedtdxbk = tdxbkcode;
         }
    }
	/*
	 * 加子节点
	 */
	  private void addSubnodeButtonMouseMoved(java.awt.event.MouseEvent evt) //GEN-FIRST:event_addSubnodeButtonMouseMoved 
	  {
	        SubnodeButton button = (SubnodeButton) addSubnodeButton;
	        String key;
	        if (System.getProperty("os.name").startsWith("Mac OS X")) key = "CMD";
	        else key = "CTRL";
	        int x = evt.getX();
	        //System.out.print("x=" + x + " ");
	        int y = evt.getY();
	        //System.out.print(y);
	        //System.out.print("y=" + y + " ");
	        if (y<19 && x+y<30 && x<19) {
	            button.setDirection(BanKuaiAndChanYeLian.UP);
	            button.setIcon(addAboveIcon);
	            button.setToolTipText("Add above ("+key+"-UP)");
	        }
	        else if (y>=19 && x-y < 0 && x<19){
	            button.setDirection(BanKuaiAndChanYeLian.DOWN);
	            button.setIcon(addBelowIcon);
	            button.setToolTipText("Add below ("+key+"-DOWN)");
	        }
	        else if (x+y>30 && x-y>0){
	            button.setDirection(BanKuaiAndChanYeLian.RIGHT);
	            button.setIcon(addChildIcon);
	            button.setToolTipText("Add subnode ("+key+"-RIGHT)");
	        }
	        else {
	            button.setDirection(BanKuaiAndChanYeLian.NONE);
	            button.setIcon(addSubnodeIcon);
	            button.setToolTipText("Add subnode");
	        }
	   }
	
	  private void addSubnodeButtonMouseExited(java.awt.event.MouseEvent evt) //GEN-FIRST:event_addSubnodeButtonMouseExited 
	  {
	        addSubnodeButton.setIcon(addSubnodeIcon);
	        addSubnodeButton.setToolTipText("Add subnode");
	  }
	  
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

			 treechanyelian.addNewNode (BkChanYeLianTreeNode.SUBBK,subcode,subname,direction);
			 btnSaveAll.setEnabled(true);
			 
		}//GEN-LAST:event_addSubnodeButtonActionPerformed

	    /*
	     * 显示板块内股票的周占比, 显示的都是本周的而不是当天的
	     */
	    protected void displayBanKuaiGeGuZhanBi(BanKuai bknode) 
	    {
//	    	if(cbxtichuquanzhong.isSelected())
//	    		pnlGeGuZhanBi.setBanKuaiNeededDisplay(bknode,Integer.parseInt(tfldquanzhong.getText() ),CommonUtility.getWeekNumber(dchgeguwkzhanbi.getDate()  ) );
//	    	else
//	    		pnlGeGuZhanBi.setBanKuaiNeededDisplay(bknode, -1 ,CommonUtility.getWeekNumber(dchgeguwkzhanbi.getDate()  ) );
			
		}
	    /*
	     * 显示板块周在整个市场的占比
	     */
	    private void displayBanKuaiZhanBi(BanKuai bankuai) 
	    {
//	    	Date endday = CommonUtility.getLastDayOfWeek(dchgeguwkzhanbi.getDate() );
//	    	Date startday = CommonUtility.getDateOfSpecificMonthAgo(dchgeguwkzhanbi.getDate() ,sysconfig.banKuaiFengXiMonthRange() );
//	    	bankuai = bkdbopt.getBanKuaiZhanBi (bankuai,startday,endday);
//	    	
//	    	bkfxpnl.setNodeZhanBiByWeek(bankuai,endday);
  	

		}
    

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
	  	            button.setDirection(BanKuaiAndChanYeLian.UP);
	  	            button.setIcon(addAboveIcon);
	  	            button.setToolTipText("Add above ("+key+"-UP)");
	  	        }
	  	        else if (y>=19 && x-y < 0 && x<19){
	  	            button.setDirection(BanKuaiAndChanYeLian.DOWN);
	  	            button.setIcon(addBelowIcon);
	  	            button.setToolTipText("Add below ("+key+"-DOWN)");
	  	        }
	  	        else if (x+y>30 && x-y>0){
	  	            button.setDirection(BanKuaiAndChanYeLian.RIGHT);
	  	            button.setIcon(addChildIcon);
	  	            button.setToolTipText("Add subnode ("+key+"-RIGHT)");
	  	        }
	  	        else {
	  	            button.setDirection(BanKuaiAndChanYeLian.NONE);
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
	  		
	  		String gegucode = ((BanKuaiGeGuTableModel)(tablebkgegu.getModel())).getStockCode(row);
	  		String geguname = ((BanKuaiGeGuTableModel)(tablebkgegu.getModel())).getStockName(row);
	  		
	  		 treechanyelian.addNewNode (BkChanYeLianTreeNode.BKGEGU,gegucode,geguname,direction);
	     }

	    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
			  if(treechanyelian.deleteNodes () )
//				  cylneedsave = true;
				  btnSaveAll.setEnabled(true);
		        
		    }//GEN-LAST:event_deleteButtonActionPerformed

	   
		   /*
		    * 产业链树上定位用户选择的板块，只选择到板块一级，子板块不找
		    */
		   protected TreePath findBanKuaiInTree(String bkinputed) 
		   {
		    	@SuppressWarnings("unchecked")
		    	TreePath bkpath = treechanyelian.locateNodeByNameOrHypyOrBkCode (bkinputed,true);
		    	if(bkpath != null) {
//		            this.setSelectionPath(bkpath);
//		    	     this.scrollPathToVisible(bkpath);
//		    	     this.expandTreePathAllNode(bkpath);
		    		getReleatedInfoAndActionsForTreePathNode (bkpath); //显示和板块相关的子产业链和个股
		    	}
		    	
			    return bkpath;
			}
		    public boolean isXmlEdited ()
		    {
		    	if(btnSaveAll.isEnabled() )
		    		return true;
		    	else
		    		return false;
		    }
		    
		    private void initializeBanKuaiParsedFile() 
			{
				String parsedpath = sysconfig.getBanKuaiParsedFileStoredPath ();
				if(parsedpath == null || parsedpath == "")
					return;
				
				Date date=new Date();//取时间
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				calendar.add(calendar.DATE,-1);//把日期往后增加一天.整数往后推,负数往前移动
				date=calendar.getTime(); //这个时间就是日期往后推一天的结果 
				SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
				String dateString = formatter.format(date);
//				System.out.println(dateString);
				
				String parsefilename = parsedpath + dateString + ".ebk";
//				tfldparsefilename.setText(parsefilename);
				
				parseSelectedBanKuaiFile (parsefilename);
			}
		    /*
		     * 处理每天生成的板块文件
		     */
		    private void parseSelectedBanKuaiFile (String filename)
		    {
		    	File parsefile = new File(filename);
		    	if(!parsefile.exists() )
		    		return;
		    	
		    	tfldparsefilename.setText(filename);
				List<String> readparsefileLines = null;
				try {
					readparsefileLines = Files.readLines(parsefile,Charsets.UTF_8,new ParseBanKuaiFielProcessor ());
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				stockinfile = new HashSet<String> (readparsefileLines);
				BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)treechanyelian.getModel().getRoot();
				treeroot.setParseFileStockSet(stockinfile);
				
				//优先显示重点关注的板块，其他板块等点击时候才显示，否则计算量太大。计算量大，暂时还是不用
//				for (Map.Entry<String,ArrayList<BkChanYeLianTreeNode>> entry : this.zdgzbkmap.entrySet()) {
//		    		 ArrayList<BkChanYeLianTreeNode> tmpzdgzlist = entry.getValue();
//		    		 
//		    		 for(BkChanYeLianTreeNode everyzdgznode : tmpzdgzlist) {
//		    			 TreePath bkpath = new TreePath(everyzdgznode.getPath());
//		    			 DefaultTreeModel model = (DefaultTreeModel) treechanyelian.getModel();
//						 TreeNode[] tempath = model.getPathToRoot(everyzdgznode);
//						 
//						 BkChanYeLianTreeNode parentnode = (BkChanYeLianTreeNode)tempath[1];
//						 if(parentnode.getType() == 4) {
//						   	parentnode = this.getBanKuai((BanKuai)parentnode, dchgeguwkzhanbi.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
//						   	parentnode = this.getAllGeGuOfBanKuai ( (BanKuai)parentnode);
//							treechanyelian.updateParseFileInfoToTreeFromSpecificNode (parentnode,stockinfile,dchgeguwkzhanbi.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() );
////							model.nodeStructureChanged(parentnode);
//						 }
//		    		 }
//		    	 }
				
				//下面代码是处理所有板块，计算量太大，暂时不用
//				BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)treechanyelian.getModel().getRoot();
//				int bankuaicount = treechanyelian.getModel().getChildCount(treeroot);
//				for(int i=0;i< bankuaicount; i++) {
//					BanKuai childnode = (BanKuai)treechanyelian.getModel().getChild(treeroot, i);
//					String bkcode = childnode.getMyOwnCode();
////					childnode = this.getBanKuai(childnode.getMyOwnCode(), dchgeguwkzhanbi.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
//					childnode = this.getAllGeGuOfBanKuai (childnode);
////					treechanyelian.updateTreeParseFileInfo (childnode,stockinfile,dchgeguwkzhanbi.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
//				}
				
//				treechanyelian.updateTreeParseFileInfo(stockinfile,dchgeguwkzhanbi.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() );
				
				
				((BanKuaiGeGuTableModel)tablebkgegu.getModel()).deleteAllRows(); //个股列表删除光
				//重点关注的2个表也要更新
				((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).fireTableDataChanged();
				((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).fireTableDataChanged();
		    }

		    /*
		     * 选择要分析的板块文件
		     */
		    private void selectBanKuaiParseFile ()
			{
		    	String parsedpath = sysconfig.getBanKuaiParsedFileStoredPath ();
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				chooser.setCurrentDirectory(new File(parsedpath) );
				
				if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
				    
				    String linuxpath;
				    if(chooser.getSelectedFile().isDirectory())
				    	linuxpath = (chooser.getSelectedFile()+ "\\").replace('\\', '/');
				    else
				    	linuxpath = (chooser.getSelectedFile()).toString().replace('\\', '/');
				    
//				    System.out.println(linuxpath);
				    tfldparsefilename.setText(linuxpath);
				    
				    parseSelectedBanKuaiFile (linuxpath);
				}
				
			} 
		    public String getParsedFileName ()
		    {
		    	try {
		    		return tfldparsefilename.getText();
		    	} catch (java.lang.NullPointerException e) {
		    		return "";
		    	}
		    }
		    

		    /*
		     * 保存2个XML 
		     */
	public boolean saveCylXmlAndZdgzXml () //GEN-FIRST:event_saveButtonActionPerformed 
	{
		if(btnSaveAll.isEnabled() ) {
			BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)treechanyelian.getModel().getRoot();
			if(!cylxmhandler.saveTreeToChanYeLianXML(treeroot) ) {
				JOptionPane.showMessageDialog(null, "保存产业链XML失败！请查找原因。","Warning", JOptionPane.WARNING_MESSAGE);
				return false;
			}
			
			if( !zdgzbkxmlhandler.saveAllZdgzbkToXml () ) {
				JOptionPane.showMessageDialog(null, "保存重点关注股票池XML失败！请查找原因。","Warning", JOptionPane.WARNING_MESSAGE);
				return false;
			}
		}

		btnSaveAll.setEnabled(false);
		return true;
	}	    
	private void createEvents() 
	{
		
		tfldparsefilename.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String linuxpath = tfldparsefilename.getText();
			    
			    parseSelectedBanKuaiFile (linuxpath);
			}
		});
		
//		mntmgephi.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent arg0) {
//				
//			}
//		});
		
		cbxtichuquanzhong.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) 
			{
				tfldquanzhong.setEnabled(!tfldquanzhong.isEnabled());
			}
		});
		/*
		 * 
		 */
		btndisplaybkfx.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
//				 读出该板块近一年的占比走势
				try {
					TreePath closestPath = treechanyelian.getSelectionPath();
					BanKuai bknode = (BanKuai) closestPath.getPathComponent(1);
			    	 String tdxbk = bknode.getMyOwnName(); 
			    	 String tdxbkcode = bknode.getMyOwnCode();
			    	 
			    	 Date actiondate = dchgeguwkzhanbi.getDate();
//			  	     displayBanKuaiZhanBi (tdxbkcode,tdxbk,actiondate);
//			  	     displayBanKuaiGeGuZhanBi (tdxbkcode,tdxbk,actiondate);
			    	 displayBanKuaiZhanBi (bknode);
			  	   	 displayBanKuaiGeGuZhanBi (bknode);
				} catch ( java.lang.NullPointerException e) {
					System.out.println("没有板块被选择！");
				}
				
			}
		});
		/*
		 * 
		 */
		dchgeguwkzhanbi.addPropertyChangeListener(new PropertyChangeListener() {
		    @Override
		    public void propertyChange(PropertyChangeEvent e) {
		    	
		    	TreePath closestPath = treechanyelian.getSelectionPath();
		    	if(closestPath != null ) {
		    		 BkChanYeLianTreeNode bknode = (BkChanYeLianTreeNode) closestPath.getPathComponent(1);
			    	 String tdxbk = bknode.getMyOwnName(); 
			    	 String tdxbkcode = bknode.getMyOwnCode();
			    	 
			    	if("date".equals(e.getPropertyName())) {
//			    		Date actiondate = dchgeguwkzhanbi.getDate(); 
//			    		displayBanKuaiGeGuZhanBi (tdxbkcode,tdxbk,actiondate);
//			    		displayBanKuaiZhanBi (tdxbkcode,tdxbk,actiondate);
			    	}
		    	}
		    }
		});
		
		buttonCjlFx.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				chengJiaoLiangFengXi ();
			}
		});
		
		
		btnopencylxml.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				cylxmhandler.openChanYeLianXmlInWinSystem ();
			}
		});
		
		btnopenzdgzxml.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent arg0) {
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				zdgzbkxmlhandler.openZdgzXmlInWinSystem ();
			}
		});
		
		btnSaveAll.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				saveCylXmlAndZdgzXml ();
			}
		});
		
		btnGenTDXCode.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				TDXFormatedOpt.parseZdgzBkToTDXCode(zdgzbkxmlhandler.getZdgzBkDetail ());
			}
		});
		
		btndeldalei.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				deleteDaLeiGuPiaoChi ();
			}
		});
		/*
		 * 增加大类
		 */
		btnadddalei.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent arg0) 
			{
				String newdaleiname = JOptionPane.showInputDialog(null,"请输入新的股票池名称:","添加股票池", JOptionPane.QUESTION_MESSAGE);
				if( !newdaleiname.isEmpty() && !zdgzbkmap.keySet().contains(newdaleiname)) {
					ArrayList<BkChanYeLianTreeNode> tmpgzbklist = new ArrayList<BkChanYeLianTreeNode> (); 
					zdgzbkmap.put(newdaleiname, tmpgzbklist);
					
					unifyDisplaysInDifferentCompOnGui (newdaleiname,0);
					
//					zdgzxmlneedsave = true;
					btnSaveAll.setEnabled(true);
				} else
					JOptionPane.showMessageDialog(null,"股票池名称已经存在！","Warning",JOptionPane.WARNING_MESSAGE);
			}
		});

		
				btnChsParseFile.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) {
						selectBanKuaiParseFile ();
						
					}
				});
				
				/*
				 * 
				 */
				tableZdgzBankDetails.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) 
					{
						int row = tableZdgzBankDetails.getSelectedRow();
						if(row <0) {
							JOptionPane.showMessageDialog(null,"请选择一个产业链","Warning",JOptionPane.WARNING_MESSAGE);
							return;
						} 
						
						int rowInZdgz = tableCurZdgzbk.getSelectedRow();
						String zdgzdalei = ((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).getZdgzDaLei(rowInZdgz);
						
						BkChanYeLianTreeNode selectedgzbk = ((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).getGuanZhuBanKuaiInfo(row);
						
						unifyDisplaysInDifferentCompOnGui (zdgzdalei,row);
					}


				});
				/*
				 * 
				 */
				btnCylRemoveFromZdgz.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) 
					{
						removeTreeChanYeLianNodeFromZdgz (arg0);
					}
				});

				
				/*
				 * 把产业链加入到重点关注
				 */
				btnCylAddToZdgz.addMouseListener(new MouseAdapter() {
					 @Override
					public void mouseClicked(MouseEvent arg0) 
					{
						 adddTreeChanYeLianNodeToZdgz ( arg0);
		
					}
				});

				
				
				buttonremoveoffical.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) 
					{
						removeChanYeLianFromOffical ();
					}
				});
				
				
				buttonaddofficial.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) 
					{
						addChanYeLianToOffical ();
					}
					

				});
				
				tableCurZdgzbk.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) 
					{
						int row = tableCurZdgzbk.getSelectedRow();
						if(row <0) {
							JOptionPane.showMessageDialog(null,"请选择一个大类","Warning",JOptionPane.WARNING_MESSAGE);
							return;
						} 
						
						//initializeSingleDaLeiZdgzTableFromXml (0);
						String selecteddalei = ((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).getZdgzDaLei (row);
						ArrayList<BkChanYeLianTreeNode> selectedgzbklist = ((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).getDaLeiChanYeLianList(selecteddalei);
						BkChanYeLianTreeNode selectedgzbk = null ;
						try {
							 selectedgzbk = selectedgzbklist.get(0);
						} catch ( java.lang.IndexOutOfBoundsException e) {
							
						}
						
						unifyDisplaysInDifferentCompOnGui(selecteddalei,0);
						

					}

				});
				
				tfldfindgegu.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) {
						tfldfindgegu.setText("");
					}
				});
				
				tfldfindgegu.addActionListener(new AbstractAction() {
					public void actionPerformed(ActionEvent arg0) {
						String bkinputed = tfldfindgegu.getText();
						int rowindex = ((BanKuaiGeGuTableModel)tablebkgegu.getModel()).getStockRowIndex(bkinputed);
						int modelRow = tablebkgegu.convertRowIndexToView(rowindex);
						if(rowindex != -1) {
							tablebkgegu.setRowSelectionInterval(modelRow, modelRow);
							tablebkgegu.scrollRectToVisible(new Rectangle(tablebkgegu.getCellRect(modelRow, 0, true)));
						}
						
					}
				});
				
				tfldfindbk.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) {
						tfldfindbk.setText("");
					}
				});
				
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
				
				addKeyListener(new KeyAdapter() {
					@Override
					public void keyTyped(KeyEvent e) 
					{
						 int id = e.getID();
						 String keyString;
					        if (id == KeyEvent.KEY_TYPED) {
					            char c = e.getKeyChar();
					            keyString = "key character = '" + c + "'";
					            System.out.println(keyString);
					        } else {
					        	int keyCode = e.getKeyCode();
					            keyString = "key code = " + keyCode
					                    + " ("
					                    + KeyEvent.getKeyText(keyCode)
					                    + ")";
					        }
					        
					}
				});
				

		        treechanyelian.addMouseListener(new java.awt.event.MouseAdapter() {
		            public void mousePressed(java.awt.event.MouseEvent evt) {
//		            	chanYeLianTreeMousePressed(evt);
//		            	System.out.println("get action notice at bkcyl");
		    	        TreePath closestPath = treechanyelian.getClosestPathForLocation(evt.getX(), evt.getY());

		    	        if(closestPath != null) {
		    	            Rectangle pathBounds = treechanyelian.getPathBounds(closestPath);
		    	            int maxY = (int) pathBounds.getMaxY();
		    	            int minX = (int) pathBounds.getMinX();
		    	            int maxX = (int) pathBounds.getMaxX();
		    	            if (evt.getY() > maxY) 
		    	            	treechanyelian.clearSelection();
		    	            else if (evt.getX() < minX || evt.getX() > maxX) 
		    	            	treechanyelian.clearSelection();
		    	        }
		    	        getReleatedInfoAndActionsForTreePathNode ( closestPath);
		            }
		        });

		        
				btnAddSubBk.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) 
					{
						String newsubbk = JOptionPane.showInputDialog(null,"请输入子板块名称:","增加子版块", JOptionPane.QUESTION_MESSAGE);
						if(newsubbk == null)
							return;
						
//						if(bkdbopt.getSysBkSet().contains(newsubbk) ) {
//							JOptionPane.showMessageDialog(null,"输入子版块名称与通达信板块名称冲突,请重新输入!");
//							return ;
//						}
						
						TreePath closestPath = treechanyelian.getSelectionPath();
//				        System.out.println(closestPath);
				         BkChanYeLianTreeNode tdxbk = (BkChanYeLianTreeNode)closestPath.getPathComponent(1);
				         String tdxbkcode = tdxbk.getMyOwnCode();
				        
						String newsubcylcode = bkdbopt.addNewSubBanKuai (tdxbkcode,newsubbk.trim() ); 
						if(newsubcylcode != null)
							((BanKuaiSubChanYeLianTableModel)(tablesubcyl.getModel())).addRow(newsubcylcode,newsubbk);
						else
							JOptionPane.showMessageDialog(null,"添加失败，可能是因为重名！","Warning",JOptionPane.WARNING_MESSAGE);
//			  	        ((BanKuaiSubChanYeLianTableModel)(tablesubcyl.getModel())).fireTableDataChanged ();
						
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
		        

		        
		        deleteButton.addActionListener(new java.awt.event.ActionListener() {
		            public void actionPerformed(java.awt.event.ActionEvent evt) {
		                deleteButtonActionPerformed(evt);
		            }
		        });
			}
	    
	
	
	protected void chengJiaoLiangFengXi() 
	{
//		String curselectedbknodecode = null;
//		try {
//			TreePath closestPath = treechanyelian.getSelectionPath();
//			BkChanYeLianTreeNode curselectedbknode;
//			curselectedbknode = (BkChanYeLianTreeNode) closestPath.getLastPathComponent();
//		    String curselectedbknodename = curselectedbknode.getMyOwnName();
//		    curselectedbknodecode = curselectedbknode.getMyOwnCode();
//		} catch (java.lang.NullPointerException e) {
//			BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)this.treechanyelian.getModel().getRoot();
//			BkChanYeLianTreeNode curselectedbknode;
//			curselectedbknode = (BanKuai)this.treechanyelian.getModel().getChild(treeroot, 0);
//		}

		if(bkfx == null)
			if(this.stockInfoManager.getBanKuaiFengXi() == null ) {
//				bkfx = new BanKuaiFengXi (treechanyelian,curselectedbknodecode,"",dchgeguwkzhanbi.getDate());
				bkfx = new BanKuaiFengXi (stockInfoManager,this);
				bkfx.setModal(false);
				bkfx.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				bkfx.setVisible(true);
//				stockInfoManager.setBanKuaiFengXi(bkfx);
			} else {
				bkfx = this.stockInfoManager.getBanKuaiFengXi();
			}
//		bkfx.setBanKuaiEndiorPaneContents ( curselectedbknodecode );
//		bkfx.setFenXiDate ( dchgeguwkzhanbi.getDate() );
		if(!Strings.isNullOrEmpty(tfldparsefilename.getText()))
			bkfx.setParsedFile(tfldparsefilename.getText() );
		
		if(!bkfx.isVisible() ) {
			bkfx.setVisible(true);
		 } 
		bkfx.toFront();
	}
	
	public BanKuaiFengXi getBanKuaiFengXi ()
	{
		return bkfx;
	};

//	protected void addGeGuNews() 
//	{
//		int row = tablebkgegu.getSelectedRow();
//		if(row <0) {
//			JOptionPane.showMessageDialog(null,"请选择一个股票","Warning",JOptionPane.WARNING_MESSAGE);
//			return;
//		}
//		
//		String stockcode = ((BanKuaiGeGuTableModel) tablebkgegu.getModel()).getStockCode (row);
//		ChanYeLianNewsPanel cylnews = new ChanYeLianNewsPanel (stockcode);
//		int exchangeresult = JOptionPane.showConfirmDialog(null, cylnews, "增加个股新闻", JOptionPane.OK_CANCEL_OPTION);
//		System.out.print(exchangeresult);
//		if(exchangeresult == JOptionPane.CANCEL_OPTION)
//			return;
//	}

	protected void addChanYeLianNews() 
	{
		String selectnodecode = null;
		try {
			TreePath closestPath = treechanyelian.getSelectionPath();
			BkChanYeLianTreeNode selectednode = (BkChanYeLianTreeNode)closestPath.getLastPathComponent();
			 selectnodecode = selectednode.getMyOwnCode();
		} catch (java.lang.NullPointerException ex) {
			JOptionPane.showMessageDialog(null,"请选择产业板块！","Warning",JOptionPane.WARNING_MESSAGE);
		}
		
		ChanYeLianNewsPanel cylnews = new ChanYeLianNewsPanel (selectnodecode);
		int exchangeresult = JOptionPane.showConfirmDialog(null, cylnews, "增加产业链新闻", JOptionPane.OK_CANCEL_OPTION);
		System.out.print(exchangeresult);
		if(exchangeresult == JOptionPane.CANCEL_OPTION)
			return;
		
		bkdbopt.addBanKuaiNews(selectnodecode, cylnews.getInputedNews());
	}

	private void deleteDaLeiGuPiaoChi () 
	{
		int row = tableCurZdgzbk.getSelectedRow();
		if(row <0) {
			JOptionPane.showMessageDialog(null,"请选择一个股票池","Warning",JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		String daleiname = ((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).getZdgzDaLei(row);
		int n = JOptionPane.showConfirmDialog(null, "确定删除" + daleiname + "?", "删除股票池",JOptionPane.YES_NO_OPTION);//i=0/1  
		if(n == 0) {
			ArrayList<BkChanYeLianTreeNode> daleicyl = zdgzbkxmlhandler.getASubDaiLeiDetail(daleiname);
			for(BkChanYeLianTreeNode tmpgzbkinfo : daleicyl ) {
				 treechanyelian.removeZdgzBkCylInfoFromTreeNode (tmpgzbkinfo,false);
			}
			
			zdgzbkxmlhandler.deleteDaLei(daleiname);
			
			String selecteddalei = ((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).getZdgzDaLei (0);
			unifyDisplaysInDifferentCompOnGui (selecteddalei,0);
//			initializeAllDaLeiZdgzTableFromXml (null);
//			initializeSingleDaLeiZdgzTableFromXml (0);
			
//			zdgzxmlneedsave = true;
			btnSaveAll.setEnabled(true);
		}
		
	}
	
	/*
	 * 移除重点关注
	 */
	private void removeTreeChanYeLianNodeFromZdgz(MouseEvent arg0) 
	{
		int row = tableZdgzBankDetails.getSelectedRow();
		if(row <0) {
			JOptionPane.showMessageDialog(null,"请选择一个板块产业链","Warning",JOptionPane.WARNING_MESSAGE);
			return;
		}

		String daleiname = getCurSelectedDaLei();
		 if( daleiname != null) {
			  BkChanYeLianTreeNode gzcyl = ((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).getGuanZhuBanKuaiInfo(row);
			  
			  JiaRuJiHua jiarujihua = new JiaRuJiHua ( gzcyl.getMyOwnCode(), "移除重点" ); 
			  int exchangeresult = JOptionPane.showConfirmDialog(null, jiarujihua, "移除重点关注", JOptionPane.OK_CANCEL_OPTION);
			  if(exchangeresult == JOptionPane.CANCEL_OPTION)
					return;
					
			  int autoIncKeyFromApi =	bkdbopt.setZdgzRelatedActions (jiarujihua);
			 
			 treechanyelian.removeZdgzBkCylInfoFromTreeNode (gzcyl,false);
			 zdgzbkxmlhandler.removeGuanZhuBanKuai(daleiname, gzcyl);
			 unifyDisplaysInDifferentCompOnGui (daleiname,0);
			 
//			 zdgzxmlneedsave = true;
			 btnSaveAll.setEnabled(true);
		 } else
			 JOptionPane.showMessageDialog(null,"请选择一个大类","Warning",JOptionPane.WARNING_MESSAGE);
		
	}

	private void addChanYeLianToOffical ()
	{
		int row = tableZdgzBankDetails.getSelectedRow();
		if(row <0) {
			JOptionPane.showMessageDialog(null,"请选择一个产业链","Warning",JOptionPane.WARNING_MESSAGE);
			return ;
		}
		
		String selectedalei = getCurSelectedDaLei ();
		if( selectedalei != null) {
			 BkChanYeLianTreeNode gzbk = ((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).getGuanZhuBanKuaiInfo(row);
			 gzbk.setOfficallySelected(true);
			 
			 treechanyelian.addZdgzBkCylInfoToTreeNode(gzbk,true);
			 unifyDisplaysInDifferentCompOnGui (selectedalei,row);

//			 zdgzxmlneedsave = true;
			 btnSaveAll.setEnabled(true);
		} else
			 JOptionPane.showMessageDialog(null,"请选择一个大类","Warning",JOptionPane.WARNING_MESSAGE);
	}
	/*
	 * 从offical移除重点关注
	 */
	private void removeChanYeLianFromOffical ()
	{
		int row = tableZdgzBankDetails.getSelectedRow();
		if(row <0) {
			JOptionPane.showMessageDialog(null,"请选择一个产业链","Warning",JOptionPane.WARNING_MESSAGE);
			return ;
		}
		
		String selectedalei = getCurSelectedDaLei ();
		if( selectedalei != null) {
			 BkChanYeLianTreeNode gzbk = ((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).getGuanZhuBanKuaiInfo(row);
			 if(!gzbk.isOfficallySelected()) {
				 JOptionPane.showMessageDialog(null,"选择的产业链只是候补关注，不在当前重点关注股票池中,不用移出！","Warning",JOptionPane.WARNING_MESSAGE);
				 return;
			 }
				 
			 if(JOptionPane.showConfirmDialog(null, "是否直接从候补产业链中删除？","Warning", JOptionPane.YES_NO_OPTION) == 0) {
					zdgzbkxmlhandler.removeGuanZhuBanKuai(selectedalei, gzbk);
					unifyDisplaysInDifferentCompOnGui (selectedalei,0);
					treechanyelian.removeZdgzBkCylInfoFromTreeNode (gzbk,false);
				} else {
					unifyDisplaysInDifferentCompOnGui (selectedalei,row);
					treechanyelian.removeZdgzBkCylInfoFromTreeNode (gzbk,true);
				}

//			 zdgzxmlneedsave = true;
			 btnSaveAll.setEnabled(true);
			 
		} else
			 JOptionPane.showMessageDialog(null,"请选择一个大类","Warning",JOptionPane.WARNING_MESSAGE);
		
	}
		
	/*
	 * 加入重点关注
	 */
	private void adddTreeChanYeLianNodeToZdgz(MouseEvent arg0) 
	{
		 String daleiname = getCurSelectedDaLei();
		 if( daleiname != null) {
			 TreePath path = treechanyelian.getSelectionPath();
			 BkChanYeLianTreeNode nodewilladded = (BkChanYeLianTreeNode) treechanyelian.getSelectionPath().getLastPathComponent();
			 
			 JiaRuJiHua jiarujihua = new JiaRuJiHua ( nodewilladded.getMyOwnCode(), "加入重点" ); 
			 int exchangeresult = JOptionPane.showConfirmDialog(null, jiarujihua, "加入重点关注", JOptionPane.OK_CANCEL_OPTION);
			 if(exchangeresult == JOptionPane.CANCEL_OPTION)
				return;
				
			 int autoIncKeyFromApi =	bkdbopt.setZdgzRelatedActions (jiarujihua);

			 boolean isofficallyselected = false;
			 if(JOptionPane.showConfirmDialog(null, "是否直接加入重点关注？","Warning", JOptionPane.YES_NO_OPTION) == 1) {
				 nodewilladded.setOfficallySelected(false);
			 } else 
				 nodewilladded.setOfficallySelected(true);
			 
			 nodewilladded.setSelectedToZdgzTime( new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()  );
			 
			 treechanyelian.addZdgzBkCylInfoToTreeNode(nodewilladded,false);
			 zdgzbkxmlhandler.addNewGuanZhuBanKuai(daleiname, nodewilladded);
			 int addedrow = ((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).getGuanZhuBanKuaiInfoIndex(nodewilladded);
			 unifyDisplaysInDifferentCompOnGui (daleiname,addedrow);
			
			 
//			 zdgzxmlneedsave = true;
			 btnSaveAll.setEnabled(true);
		 }
		 else
			 JOptionPane.showMessageDialog(null,"请选择一个大类","Warning",JOptionPane.WARNING_MESSAGE);
		 
	}
	
	private void initializeSysConfig()
	{
		sysconfig = SystemConfigration.getInstance();
	}



	private JTextField tfldparsefilename;
	private JTable tableCurZdgzbk;
	private JTable tableZdgzBankDetails;
	private BanKuaiGeGuTable tablebkgegu;
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
	private JButton btnChsParseFile;
	private JButton addGeGuButton;
	private JButton addSubnodeButton;
	private JButton btnGenTDXCode;
	private JButton btnadddalei;
	private JButton btndeldalei;
	ImageIcon addBelowIcon, addAboveIcon, addChildIcon, addSubnodeIcon;
	private JScrollPane notesScrollPane;
	private JScrollPane treeScrollPane;
	private JSplitPane jSplitPane;
	private JButton btnSaveAll;
	private JButton btnopenzdgzxml;
	private JButton btnopencylxml;
	private JPopupMenu popupMenu;
	private JMenuItem mntmNewMenuItem;
	private JTable tablesubcyl;
	private DisplayBkGgInfoEditorPane notesPane;
	private JButton buttonCjlFx;
	private ChartPanel barchartPanel; //chart 
	private ChartPanel piechartPanel; //chart
	private JScrollPane sclpBanKuaiZhanBi;
	private JScrollPane sclpGeGuZhanBi;
	private JDateChooser dchgeguwkzhanbi;
	private JButton btndisplaybkfx;
	private BanKuaiFengXiBarChartPnl bkfxpnl ;
	private BanKuaiFengXiPieChartPnl pnlGeGuZhanBi;
	private JTextField tfldquanzhong;
	private JCheckBox cbxtichuquanzhong;
	private JMenuItem mntmgephi;
	private JMenuItem menuItemaddgz;
	private JMenuItem menuItemredian;

	private void initializeGui() 
	{
		JPanel panel = new JPanel();
		
		JPanel panelzdgz = new JPanel();
		
		JPanel panelcyltree = new JPanel();
		
		JPanel panel_1 = new JPanel();
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(panelzdgz, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 893, Short.MAX_VALUE)
						.addComponent(panelcyltree, Alignment.LEADING, 0, 0, Short.MAX_VALUE)
						.addComponent(panel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 893, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 610, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 61, GroupLayout.PREFERRED_SIZE)
					.addGap(2)
					.addComponent(panelzdgz, GroupLayout.PREFERRED_SIZE, 296, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panelcyltree, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(panel_1, GroupLayout.DEFAULT_SIZE, 834, Short.MAX_VALUE)
					.addGap(10))
		);
		
		sclpBanKuaiZhanBi = new JScrollPane();
		
		buttonCjlFx = new JButton("\u5468\u6210\u4EA4\u91CF\u5206\u6790");
		
		sclpGeGuZhanBi = new JScrollPane();
		
		btndisplaybkfx = new JButton("\u663E\u793A\u677F\u5757\u5206\u6790\u4FE1\u606F");
		
		cbxtichuquanzhong = new JCheckBox("\u5254\u9664\u80A1\u7968\u6743\u91CD<=");
		
		
		tfldquanzhong = new JTextField();
		tfldquanzhong.setEnabled(false);
		tfldquanzhong.setText("0");
		tfldquanzhong.setColumns(10);
		
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING, false)
						.addGroup(gl_panel_1.createSequentialGroup()
							.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
								.addComponent(sclpGeGuZhanBi, GroupLayout.PREFERRED_SIZE, 594, GroupLayout.PREFERRED_SIZE)
								.addComponent(sclpBanKuaiZhanBi, GroupLayout.PREFERRED_SIZE, 597, GroupLayout.PREFERRED_SIZE))
							.addContainerGap())
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(btndisplaybkfx)
							.addGap(122)
							.addComponent(cbxtichuquanzhong)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(tfldquanzhong, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(buttonCjlFx)
							.addGap(18))))
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addComponent(btndisplaybkfx)
						.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
							.addComponent(cbxtichuquanzhong)
							.addComponent(buttonCjlFx)
							.addComponent(tfldquanzhong, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(sclpGeGuZhanBi, GroupLayout.PREFERRED_SIZE, 353, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(sclpBanKuaiZhanBi, GroupLayout.DEFAULT_SIZE, 442, Short.MAX_VALUE))
		);
		
		pnlGeGuZhanBi = new BanKuaiFengXiPieChartPnl();
		pnlGeGuZhanBi.setBorder(new TitledBorder(null, "\u677F\u5757\u5185\u4E2A\u80A1\u5360\u6BD4", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		//设置显示到图片最右边
	    Rectangle bounds = sclpGeGuZhanBi.getViewport().getViewRect();
	    Dimension size = sclpGeGuZhanBi.getViewport().getViewSize();
	    int x = (size.width - bounds.width) ;
	    int y = (size.height - bounds.height) ;
	    sclpGeGuZhanBi.getViewport().setViewPosition(new Point(x, 0));
		sclpGeGuZhanBi.setViewportView(pnlGeGuZhanBi);
		
      
      
      
		
//		chartPanel = new ChartPanel(barchart);
//		panel_2 = new JPanel();
//		sclpBanKuaiZhanBi.setViewportView(panel_2);
		panel_1.setLayout(gl_panel_1);
		
		btnCylAddToZdgz = new JButton("\u52A0\u5165\u91CD\u70B9\u5173\u6CE8");
		
		btnCylRemoveFromZdgz = new JButton("\u79FB\u9664\u91CD\u70B9\u5173\u6CE8");
		
		addSubnodeButton = new SubnodeButton();
		addSubnodeButton.setIcon(new ImageIcon(BanKuaiAndChanYeLian.class.getResource("/images/subnode24.png")));
		
		addGeGuButton = new SubnodeButton();
		addGeGuButton.setIcon(new ImageIcon(BanKuaiAndChanYeLian.class.getResource("/images/subnode24.png")));
		
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
		
		
		GroupLayout gl_panelcyltree = new GroupLayout(panelcyltree);
		gl_panelcyltree.setHorizontalGroup(
			gl_panelcyltree.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelcyltree.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING)
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
							.addGap(10)
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
										.addComponent(btnAddSubBk)))))
						.addGroup(gl_panelcyltree.createSequentialGroup()
							.addComponent(btnCylAddToZdgz)
							.addGap(10)
							.addComponent(btnCylRemoveFromZdgz)))
					.addContainerGap(11, Short.MAX_VALUE))
		);
		gl_panelcyltree.setVerticalGroup(
			gl_panelcyltree.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelcyltree.createSequentialGroup()
					.addGroup(gl_panelcyltree.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnCylRemoveFromZdgz)
						.addComponent(btnCylAddToZdgz)
						.addComponent(btnAddSubBk))
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
					.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelcyltree.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panelcyltree.createParallelGroup(Alignment.BASELINE)
									.addComponent(tfldfindbk, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(btnfindbk)
									.addComponent(btnopencylxml, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_panelcyltree.createParallelGroup(Alignment.BASELINE)
									.addComponent(tfldfindgegu, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addComponent(btnfindgegu))))
						.addGroup(Alignment.TRAILING, gl_panelcyltree.createSequentialGroup()
							.addGap(10)
							.addComponent(deleteButton, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)))
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
		
		treeScrollPane.setViewportView(treechanyelian);
		treeScrollPane.grabFocus();
		treeScrollPane.getVerticalScrollBar().setValue(0);
		
		
		notesScrollPane = new JScrollPane();
		jSplitPane.setRightComponent(notesScrollPane);
		
		notesPane = new DisplayBkGgInfoEditorPane();
		
//		notesPane.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
//		notesPane.setEditable(false);
		notesScrollPane.setViewportView(notesPane);
		
		
		tablebkgegu = new  BanKuaiGeGuTable(this.stockInfoManager);
		tablebkgegu.hideZhanBiColumn(4);
		tablebkgegu.sortByParsedFile();
		
//		BanKuaiGeGuTableModel bkgegumapmdl = new BanKuaiGeGuTableModel();
//		tablebkgegu = new  JTable(bkgegumapmdl)
//		{
//			private static final long serialVersionUID = 1L;
//
//			public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
//					 
//			        Component comp = super.prepareRenderer(renderer, row, col);
//			        BanKuaiGeGuTableModel tablemodel = (BanKuaiGeGuTableModel)this.getModel(); 
//			        HashSet<String> stockinparsefile = tablemodel.getStockInParseFile();
//			        Object value = tablemodel.getValueAt(row, col);
//			        
//			        if (!isRowSelected(row)) {
//			        	comp.setBackground(getBackground());
//			        	comp.setForeground(getForeground());
//			        	int modelRow = convertRowIndexToModel(row);
//			        	String stockcode = (String)getModel().getValueAt(modelRow, 0);
//						if(stockinparsefile.contains(stockcode)) {
//							//comp.setBackground(Color.YELLOW);
//							comp.setForeground(Color.BLUE);
//						}
//			        }
//			        
//			        return comp;
//			}
//				    
//
//			
//			public String getToolTipText(MouseEvent e) 
//			{
//                String tip = null;
//                java.awt.Point p = e.getPoint();
//                int rowIndex = rowAtPoint(p);
//                int colIndex = columnAtPoint(p);
//
//                try {
//                    tip = getValueAt(rowIndex, colIndex).toString();
//                } catch (RuntimeException e1) {
//                	e1.printStackTrace();
//                }
//                return tip;
//            } 
//		};
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

		scrollPanegegu.setViewportView(tablebkgegu);
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
		        
		        if (!isRowSelected(row)) {
		        	comp.setBackground(this.getBackground()); 
		        	int modelRow = convertRowIndexToModel(row);
		        	String chanyelian = (String)getModel().getValueAt(modelRow, 1);
					if(chanyelian != null && !currentselectedtdxbk.equals("") && chanyelian.contains(currentselectedtdxbk)) 
						jc.setBorder( highlight );
		        }
		        
		        if (isRowSelected(row)) {
		        	comp.setBackground(this.getSelectionBackground());
		        	
		        	int modelRow = convertRowIndexToModel(row);
		        	String chanyelian = (String)getModel().getValueAt(modelRow, 1);
					if(chanyelian != null && !currentselectedtdxbk.equals("") && chanyelian.contains(currentselectedtdxbk)) 
						jc.setBorder( highlight );
		        	
		        }
		        
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
		
		JLabel label = new JLabel("\u89E3\u6790\u677F\u5757\u6587\u4EF6");
		
		tfldparsefilename = new JTextField();
		tfldparsefilename.setEditable(false);
		
		tfldparsefilename.setColumns(10);
		
		btnChsParseFile = new JButton("\u9009\u62E9\u6587\u4EF6");
		
		JSeparator separator = new JSeparator();
		
		btnSaveAll = new JButton("\u4FDD\u5B58\u4FEE\u6539");
		btnSaveAll.setEnabled(false);
		
		dchgeguwkzhanbi = new JDateChooser();
		dchgeguwkzhanbi.setDate(new Date());
		
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addContainerGap()
							.addComponent(dchgeguwkzhanbi, GroupLayout.PREFERRED_SIZE, 113, GroupLayout.PREFERRED_SIZE)
							.addGap(86)
							.addComponent(btnSaveAll)
							.addGap(89)
							.addComponent(label)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(tfldparsefilename, GroupLayout.PREFERRED_SIZE, 320, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
							.addComponent(btnChsParseFile))
						.addComponent(separator, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 892, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
							.addComponent(tfldparsefilename, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
							.addComponent(btnChsParseFile, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
							.addComponent(label)
							.addComponent(btnSaveAll))
						.addComponent(dchgeguwkzhanbi, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(separator, GroupLayout.PREFERRED_SIZE, 6, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		panel.setLayout(gl_panel);
		setLayout(groupLayout);
		
		
		
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
        popupMenu = new BanKuaiPopUpMenu(treechanyelian);
		addPopup(treechanyelian, popupMenu);

		bkfxpnl = new BanKuaiFengXiBarChartPnl ();
		bkfxpnl.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "\u677F\u5757\u5468\u5360\u6BD4", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		sclpBanKuaiZhanBi.setViewportView(bkfxpnl);
        //设置显示到图片最右边
        Rectangle bounds2 = sclpBanKuaiZhanBi.getViewport().getViewRect();
        Dimension size2 = sclpBanKuaiZhanBi.getViewport().getViewSize();
        int x2 = (size.width - bounds.width) ;
        int y2 = (size.height - bounds.height) ;
        sclpBanKuaiZhanBi.getViewport().setViewPosition(new Point(x2, y2));	
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
		if(bkcyltn.shouldBeRemovedWhenSaveXml())
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
			String cylinmapnode = tmpgzbkinfo.get(i).getNodeCurLocatedChanYeLian().trim();
			String cylinverfiednode = parent.getNodeCurLocatedChanYeLian().trim();

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
                value = tmpgzbk.getNodeCurLocatedChanYeLianByName();
                break;
            case 1:
            	try {
            		value = tmpgzbk.getSelectedToZdgzTime();
            	} catch (java.lang.NullPointerException e) {
            		value = "";
            	}
            	
                break;
            case 2:
                value = new Boolean(tmpgzbk.isOfficallySelected() );
                break;
            case 3:
            	if(tmpgzbk.getParseFileStockSet() != null && tmpgzbk.getParseFileStockSet().size() >0)
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
			if(tmpbkcyltn.shouldBeRemovedWhenSaveXml())
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
            			if(gznode.isOfficallySelected()) {
            				String chanyelian = gznode.getNodeCurLocatedChanYeLianByName(); 
            				
            				String seltime = "";
            				if(gznode.getSelectedToZdgzTime() != null)
            					seltime = gznode.getSelectedToZdgzTime();
            				result = result + chanyelian + "(" + seltime +")" + " | " + " ";
            				
            				if(gznode.getParseFileStockSet() != null && gznode.getParseFileStockSet().size() > 0)
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


class BanKuaiSubChanYeLianTableModel extends DefaultTableModel 
{
	private HashMap<String,String> bksubcylmap; //包含代码和名称
	private ArrayList<String> bksubcylcodelist;
	String[] jtableTitleStrings = { "子产业链代码", "子产业链名称"};
	
	
	BanKuaiSubChanYeLianTableModel ()
	{
	}

	public void addRow(String newsubcylcode, String newsubbk)
	{
		this.bksubcylmap.put(newsubcylcode, newsubbk);
		this.bksubcylcodelist = new ArrayList<String> (this.bksubcylmap.keySet());
		this.fireTableDataChanged();
	}

	public void refresh  (HashMap<String,String> bksubcylmap2)
	{
		this.bksubcylmap = bksubcylmap2;
		bksubcylcodelist = new ArrayList<String> (this.bksubcylmap.keySet());
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


//http://www.jfree.org/phpBB2/viewtopic.php?f=3&t=29571#
class CustomToolTipGenerator implements CategoryToolTipGenerator  
{
  public String generateToolTip(CategoryDataset dataset, int row, int column)   
  {
  	NumberFormat numberFormat = NumberFormat.getNumberInstance();
  	numberFormat.setMinimumFractionDigits(3);
  	String zhanbiperct = new DecimalFormat("%#0.000").format(dataset.getValue(row, column));
  	return "周" + column + "占比:" + zhanbiperct;
  }
}
