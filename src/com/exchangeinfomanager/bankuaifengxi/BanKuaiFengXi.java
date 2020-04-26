package com.exchangeinfomanager.bankuaifengxi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultTreeModel;

import javax.swing.tree.TreePath;


import org.apache.log4j.Logger;
import org.jfree.data.time.TimeSeries;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.exchangeinfomanager.News.NewsCache;
import com.exchangeinfomanager.News.NewsLabelServices;
import com.exchangeinfomanager.News.NewsServices;
import com.exchangeinfomanager.News.ExternalNewsType.CreateExternalNewsDialog;
import com.exchangeinfomanager.News.ExternalNewsType.DuanQiGuanZhuServices;
import com.exchangeinfomanager.News.ExternalNewsType.QiangShi;
import com.exchangeinfomanager.News.ExternalNewsType.QiangShiServices;
import com.exchangeinfomanager.News.ExternalNewsType.RuoShiServices;
import com.exchangeinfomanager.NodesServices.SvsForNodeOfBanKuai;
import com.exchangeinfomanager.NodesServices.SvsForNodeOfDaPan;
import com.exchangeinfomanager.NodesServices.SvsForNodeOfStock;
import com.exchangeinfomanager.Services.ServicesForNews;
import com.exchangeinfomanager.Services.ServicesForNewsLabel;
import com.exchangeinfomanager.ServicesOfDisplayNodeInfo.DisPlayNodeSuoShuBanKuaiListPanel;
import com.exchangeinfomanager.ServicesOfDisplayNodeInfo.DisPlayNodeSuoShuBanKuaiListServices;
import com.exchangeinfomanager.ServicesOfDisplayNodeInfo.DisplayNodeExchangeDataServices;
import com.exchangeinfomanager.ServicesOfDisplayNodeInfo.DisplayNodeExchangeDataServicesPanel;
import com.exchangeinfomanager.ServicesOfDisplayNodeInfo.DisplayNodeInfoPanel;
import com.exchangeinfomanager.ServicesOfDisplayNodeInfo.DisplayNodeJiBenMianService;
import com.exchangeinfomanager.ServicesOfDisplayNodeInfo.DisplayNodeSellBuyInfoServices;
import com.exchangeinfomanager.ServicesOfDisplayNodeInfo.DisplayNodesRelatedNewsServices;
import com.exchangeinfomanager.ServicesOfDisplayNodeInfo.DisplayNodesRelatedTagsServices;
import com.exchangeinfomanager.StockCalendar.JStockCalendarDateChooser;
import com.exchangeinfomanager.StockCalendar.StockCalendar;
import com.exchangeinfomanager.Tag.Tag;
import com.exchangeinfomanager.TagLabel.LabelTag;
import com.exchangeinfomanager.TagLabel.TagsPanel;
import com.exchangeinfomanager.TagServices.CacheForInsertedTag;
import com.exchangeinfomanager.TagServices.TagsServiceForNodes;
import com.exchangeinfomanager.Trees.AllCurrentTdxBKAndStoksTree;
import com.exchangeinfomanager.Trees.BanKuaiAndStockTree;
import com.exchangeinfomanager.Trees.CreateExchangeTree;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiGeGuExternalInfoTable;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiGeGuTable;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiGeGuTableModel;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiInfoTable;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiInfoTableModel;
import com.exchangeinfomanager.bankuaifengxi.CandleStick.BanKuaiFengXiCandlestickPnl;

import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartPnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiNodeCombinedCategoryPnl;
import com.exchangeinfomanager.bankuaifengxi.PieChart.BanKuaiFengXiPieChartCjePnl;
import com.exchangeinfomanager.bankuaifengxi.PieChart.BanKuaiFengXiPieChartCjlPnl;
import com.exchangeinfomanager.bankuaifengxi.PieChart.BanKuaiFengXiPieChartPnl;
import com.exchangeinfomanager.bankuaifengxi.xmlhandlerforbkfx.BkfxWeeklyFileResultXmlHandler;
import com.exchangeinfomanager.bankuaifengxi.xmlhandlerforbkfx.ServiceOfBkFxEbkXml;
import com.exchangeinfomanager.bankuaifengxi.xmlhandlerforbkfx.ServicesForBkfxEbkOutPutFile;
import com.exchangeinfomanager.bankuaifengxi.xmlhandlerforbkfx.ServicesForBkfxEbkOutPutFileDirectRead;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.commonlib.ParseBanKuaiWeeklyFielGetBanKuaisProcessor;
import com.exchangeinfomanager.commonlib.ParseBanKuaiWeeklyFielGetStocksProcessor;
import com.exchangeinfomanager.commonlib.ReminderPopToolTip;
import com.exchangeinfomanager.commonlib.ScrollUtil;
import com.exchangeinfomanager.commonlib.SystemAudioPlayed;
import com.exchangeinfomanager.commonlib.jstockcombobox.JStockComboBox;
import com.exchangeinfomanager.commonlib.jstockcombobox.JStockComboBoxModel;
import com.exchangeinfomanager.database.BanKuaiDbOperation;

import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.gui.subgui.DateRangeSelectPnl;
import com.exchangeinfomanager.gui.subgui.PaoMaDeng2;
import com.exchangeinfomanager.guifactory.JLabelFactory;
import com.exchangeinfomanager.guifactory.JPanelFactory;
import com.exchangeinfomanager.guifactory.JTextFactory;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.DaPan;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.nodes.treerelated.NodesTreeRelated;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.exchangeinfomanager.zhidingyibankuai.PnlZhiDingYiBanKuai;
import com.exchangeinfomanager.zhidingyibankuai.TDXZhiDingYiBanKuaiServices;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;

import com.google.common.collect.Multimap;

import com.google.common.io.Files;


import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import com.toedter.calendar.JDateChooser;
import javax.swing.JCheckBox;
import javax.swing.JComponent;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.beans.PropertyChangeEvent;
import java.awt.event.MouseAdapter;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.plaf.metal.MetalScrollBarUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.UIManager;
import javax.swing.AbstractAction;
import javax.swing.Action;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JFileChooser;
import javax.swing.JTabbedPane;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.SwingWorker.StateValue;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;

import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiGeGuBasicTable;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiGeGuBasicTableModel;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.BoxLayout;
import java.awt.FlowLayout;
import javax.swing.JTable;


public class BanKuaiFengXi extends JDialog 
{
	private static final long serialVersionUID = 1L;

	private BanKuaiAndStockTree treeofbkstk;
	/**
	 * Create the dialog.
	 * @param bkcyl2 
	 */
	public BanKuaiFengXi(StockInfoManager stockmanager1)
	{
		this.stockmanager = stockmanager1;
		
		treeofbkstk = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks();
		
		this.sysconfig = SystemConfigration.getInstance();
		this.nodeinfotocsv = new NodeInfoToCsv ();
		this.bkfxremind = new BanKuaiFengXiRemindXmlHandler ();
		this.bkggmatchcondition = new BanKuaiGeGuMatchCondition ();

		this.globecalwholeweek = true; //计算整周

		initializeGui ();
		createEvents ();
		setUpChartDataListeners ();

		initializePaoMaDeng ();
		
		adjustDate (LocalDate.now());
		
	}
	
	private static Logger logger = Logger.getLogger(BanKuaiFengXi.class);
	
	private String globeperiod;
	private Boolean globecalwholeweek; //计算整周
	private BanKuaiFengXiRemindXmlHandler bkfxremind;
//	private ExportCondition displayexpc;
	private LocalDate lastselecteddate;

	private SystemConfigration sysconfig;
	private StockInfoManager stockmanager;
	BanKuaiGeGuMatchCondition bkggmatchcondition;

	private ExportTask2 exporttask;

	private ArrayList<BanKuaiGeGuMatchCondition> exportcond;
	
	private NodeInfoToCsv nodeinfotocsv;

	private Set<BarChartPanelHightLightColumnListener> chartpanelhighlightlisteners;
	private Set<BarChartPanelDataChangedListener> barchartpanelbankuaidatachangelisteners;
	private Set<PieChartPanelDataChangedListener> piechartpanelbankuaidatachangelisteners;
//	private Set<BarChartPanelDataChangedListener> barchartpanelstockofbankuaidatachangelisteners;
	private Set<BarChartPanelDataChangedListener> barchartpanelstockdatachangelisteners;
		
	/*
	 * 点击一个板块
	 */
	private void unifiedOperationsAfterUserSelectABanKuai (BanKuai selectedbk)
	{
		refreshCurentBanKuaiFengXiResult (selectedbk,globeperiod); //板块内个股的分析结果
		
		refreshCurrentBanKuaiTags (selectedbk,globeperiod);
		
		Integer alreadyin = ((JStockComboBoxModel)combxsearchbk.getModel()).hasTheNode(selectedbk.getMyOwnCode());
		if(alreadyin == -1)
			combxsearchbk.updateUserSelectedNode(selectedbk);
		
		displayNodeInfo(selectedbk);
		
		refreshBanKuaiGeGuTableHightLight ();
		
		tabbedPanebk.setSelectedIndex(2);
		
		tableBkZhanBi.repaint(); //
		
		cyltreecopy.searchAndLocateNodeInTree (selectedbk);
		
		showReminderMessage (bkfxremind.getBankuairemind());
		
		((BanKuaiGeGuTableModel)tableTempGeGu.getModel()).setInterSectionBanKuai(selectedbk); //为临时个股做准备
	}
	private void refreshCurrentBanKuaiTags(BanKuai selectedbk, String globeperiod2) 
	{
		 TagsServiceForNodes tagsevofbk = new TagsServiceForNodes (selectedbk);
		 Collection<Tag> tags;
		try {
			tags = tagsevofbk.getTags();
			selectedbk.setNodeTags(tags);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			tagsevofbk = null;
		}
		
		List<BkChanYeLianTreeNode> allbkgg = selectedbk.getAllGeGuOfBanKuaiInHistory();
		if(allbkgg == null)
			return;
		
		
		for(BkChanYeLianTreeNode stockofbk : allbkgg)   {
		    	if( ((StockOfBanKuai)stockofbk).isInBanKuaiAtSpecificDate(this.dateChooser.getLocalDate() )  ) {
		    		Stock stock = ((StockOfBanKuai)stockofbk).getStock ();
		    		TagsServiceForNodes tagsevofnode = new TagsServiceForNodes (stock);
	    			Collection<Tag> tagsofstock;
					try {
						tagsofstock = tagsevofnode.getTags();
		    			stock.setNodeTags(tagsofstock);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						tagsevofnode = null;
					}
		    	 }
		}
			
		SvsForNodeOfBanKuai svrforbk = new SvsForNodeOfBanKuai ();
		selectedbk = svrforbk.getAllGeGuOfBanKuai(selectedbk);
		Collection<BkChanYeLianTreeNode> bkstock = selectedbk.getSpecificPeriodBanKuaiGeGu (this.dateChooser.getLocalDate(),0);
		if(bkstock == null || bkstock.isEmpty())
			return;
			
		bkstock.add(selectedbk);
		TagsServiceForNodes lbnodedbservice = new TagsServiceForNodes (bkstock);
		CacheForInsertedTag bkstkkwcache = new CacheForInsertedTag (lbnodedbservice);
		lbnodedbservice.setCache(bkstkkwcache);
		pnlbktags.initializeTagsPanel (lbnodedbservice,bkstkkwcache);
	}
	protected void refreshCurentBanKuaiFengXiResult(BanKuai selectedbk,String period)  
	{
		clearTheGuiBeforDisplayNewInfoSection2 ();
		clearTheGuiBeforDisplayNewInfoSection3 ();
		tabbedPanebk.setSelectedIndex(0);
		tabbedPanebkzb.setSelectedIndex(0);
		
		LocalDate curselectdate = null;
		try{
			curselectdate = dateChooser.getLocalDate();// dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		} catch (java.lang.NullPointerException e) {
			JOptionPane.showMessageDialog(null,"日期有误!","Warning",JOptionPane.WARNING_MESSAGE);
			return;
		}
		SvsForNodeOfBanKuai svsbk = new SvsForNodeOfBanKuai ();
		BanKuai zhishubk =  (BanKuai) svsbk.getNode("999999");
		//一次性同步板块数据以及所属个股数据
		try {
			svsbk.syncBanKuaiAndItsStocksForSpecificTime(selectedbk, CommonUtility.getSettingRangeDate(curselectdate,"large"), curselectdate,
					globeperiod,this.globecalwholeweek);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		svsbk = null;
		
		//显示K线
		paneldayCandle.displayQueKou(false);
		refreshKXianOfTDXNodeWithSuperBanKuai ( selectedbk, zhishubk );
		
		//板块所属个股
		if(selectedbk.getBanKuaiLeiXing().equals(BanKuai.HASGGWITHSELFCJL)) { //有个股才需要更新，有些板块是没有个股的
			
			//如果板块的分析结果个股数目>0，则要把符合条件的个股标记好
//			BkChanYeLianTreeNode nodeincyltree = this.bkcyl.getBkChanYeLianTree().getSpecificNodeByHypyOrCode(selectedbk.getMyOwnCode(), BkChanYeLianTreeNode.TDXBK);
//			BanKuaiTreeRelated treerelated = (BanKuaiTreeRelated)nodeincyltree.getNodeTreeRelated ();
//			Integer patchfilestocknum = ((BanKuaiTreeRelated)treerelated).getStocksNumInParsedFileForSpecificDate (curselectdate);
//			if(patchfilestocknum != null && patchfilestocknum >0) {
//				Set<String> stkofbkset = this.bkcyl.getBanKuaiFxSetOfSpecificDate(selectedbk.getMyOwnCode(), curselectdate);
//				
//				for(String stkofbk : stkofbkset ) {
//					StockOfBanKuai stkinbk = selectedbk.getBanKuaiGeGu(stkofbk);
//	    			StockOfBanKuaiTreeRelated stofbktree = (StockOfBanKuaiTreeRelated)stkinbk.getNodeTreeRelated();
//	        		stofbktree.setStocksNumInParsedFile (curselectdate,true);
//				}
//				
//				stkofbkset= null;
//			}
			//板块所属个股占比info
			((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).refresh(selectedbk, curselectdate,period);
			((BanKuaiGeGuBasicTableModel)tableExternalInfo.getModel()).refresh(selectedbk, curselectdate,period);
			//更改显示日期
			tabbedPanegegu.setTitleAt(0, "当前周" + curselectdate);
			//显示2周的板块个股pie chart
			for(PieChartPanelDataChangedListener tmplistener : piechartpanelbankuaidatachangelisteners) {
				tmplistener.updateDate(selectedbk, curselectdate, 0,globeperiod);
			}
		}
		
		//板块自身占比
		for(BarChartPanelDataChangedListener tmplistener : barchartpanelbankuaidatachangelisteners) {
			tmplistener.updatedDate(selectedbk, CommonUtility.getSettingRangeDate(curselectdate,"basic"),curselectdate,globeperiod);
		}

	}
	/*
	 * 用户选择一个板块的column后的相应操作
	 */
	protected void refreshAfterUserSelectBanKuaiColumn (BanKuai bkcur, String selectedinfo) 
	{
 		LocalDate selectdate = LocalDate.parse(selectedinfo);
		chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(selectdate));
		
		//当周所有新闻
		DaPan dp = (DaPan) treeofbkstk.getSpecificNodeByHypyOrCode("000000", BkChanYeLianTreeNode.DAPAN);
		DisplayNodesRelatedNewsServices bknews = new DisplayNodesRelatedNewsServices (dp);
		bknews.setTimeRangeForInfoRange(selectdate, selectdate);
		DisplayNodeInfoPanel displaybknewspnl = new DisplayNodeInfoPanel (bknews);
//		System.out.println("Height : " + sclpinfosummary.getViewport().getSize().height + "\nWidth :" + sclpinfosummary.getViewport().getSize().width);
		Dimension size4 = new Dimension(sclpinfosummary.getViewport().getSize().width,  displaybknewspnl.getContentHeight() + 10 );
		displaybknewspnl.setPreferredSize(size4);
		displaybknewspnl.setMinimumSize(size4);
		displaybknewspnl.setMaximumSize(size4);
		pnlextrainfo.add (displaybknewspnl,0);
		
		setUserSelectedColumnMessage(bkcur,selectdate);
		
		showReminderMessage (bkfxremind.getBankuaicolumnremind() );

		//根据设置，显示选定周分析数据
		if(!menuItemnonshowselectbkinfo.getText().contains("X"))
			return;
		
		//选定周的板块排名情况
		LocalDate cursetdate = dateChooser.getLocalDate(); 
		if(!CommonUtility.isInSameWeek(selectdate,cursetdate) ) {
			SvsForNodeOfBanKuai svsbk = new SvsForNodeOfBanKuai ();
			List<TDXNodes> bkwithcje = svsbk.getBanKuaiFenXiQualifiedNodes(selectdate, globeperiod, globecalwholeweek,false);
			svsbk = null;
			((BanKuaiInfoTableModel)tableselectedwkbkzb.getModel()).addBanKuai( bkwithcje );
			((BanKuaiInfoTableModel)tableselectedwkbkzb.getModel()).refresh(selectdate,0,globeperiod);
			
			//显示板块个股
			if(bkcur.getBanKuaiLeiXing().equals(BanKuai.HASGGWITHSELFCJL) ) {//应该是有个股的板块点击才显示她的个股，
//				refreshSpecificBanKuaiFengXiResult (bkcur,selectdate,globeperiod);
				
				LocalDate formerdate = ((BanKuaiGeGuTableModel)tablexuandingzhou.getModel()).getShowCurDate();
				if(formerdate != null) { //前面有显示过，把她挪到tab 4
					//显示选定周+1股票排名情况
					((BanKuaiGeGuTableModel)tablexuandingplusone.getModel()).refresh(bkcur, formerdate,globeperiod);
					tabbedPanegegu.setTitleAt(5,  formerdate.toString() );
				}
				
				//显示选定周股票排名情况
				((BanKuaiGeGuTableModel)tablexuandingzhou.getModel()).refresh(bkcur, selectdate,globeperiod);
				tabbedPanegegu.setTitleAt(2, this.getTabbedPanegeguTabTiles(2) + selectdate);
				
				//显示选定周-1股票排名情况
				LocalDate selectdate2 = selectdate.minus(1,ChronoUnit.WEEKS).with(DayOfWeek.FRIDAY);
				((BanKuaiGeGuTableModel)tablexuandingminusone.getModel()).refresh(bkcur, selectdate2,globeperiod);
				tabbedPanegegu.setTitleAt(3,  selectdate2 + "(-1)");

				
				//显示选定周-2股票排名情况
				LocalDate selectdate3 = selectdate.plus(1,ChronoUnit.WEEKS).with(DayOfWeek.FRIDAY);
				((BanKuaiGeGuTableModel)tablexuandingminustwo.getModel()).refresh(bkcur, selectdate3,globeperiod);
				tabbedPanegegu.setTitleAt(4,  selectdate3 + "(+1)");
				
				refreshBanKuaiGeGuTableHightLight ();
					
				panelLastWkGeGucjeZhanBi.updateDate(bkcur, selectdate, 0,globeperiod); //显示选定周PIE
			}
			
			tabbedPanebk.setTitleAt(1, "选定周" + selectdate);
			//表中找到板块
			Integer rowindex = ((BanKuaiInfoTableModel)tableselectedwkbkzb.getModel() ).getBanKuaiRowIndex(bkcur.getMyOwnCode());
			if(rowindex >0) {
				int modelRow = tableselectedwkbkzb.convertRowIndexToView(rowindex);
				
				tableselectedwkbkzb.setRowSelectionInterval(modelRow, modelRow);
			    tableselectedwkbkzb.scrollRectToVisible(tableselectedwkbkzb.getCellRect(0, 0, true));
			    Rectangle rec = new Rectangle(tableGuGuZhanBiInBk.getCellRect(modelRow, 0, true));
				tableselectedwkbkzb.scrollRectToVisible(rec);
			}
		} 
		
		SystemAudioPlayed.playSound();
	}

	/**
	 * 点击一个 个股
	 * @param selectstock
	 * @param selectedGeguTable
	 */
	protected void unifiedOperationsAfterUserSelectAStock(StockOfBanKuai selectstock) 
	{
			Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
			setCursor(hourglassCursor);
			
			try{ //如果用户选择的和上次选择的个股一样，不重复做板块查找
				String stockcodeincbx;
				stockcodeincbx = ((Stock)combxstockcode.getSelectedItem()).getMyOwnCode();

				if(!selectstock.getMyOwnCode().equals( stockcodeincbx ) ) {
					 combxstockcode.updateUserSelectedNode (selectstock.getStock());
				}
			} catch (java.lang.NullPointerException e) {
				 combxstockcode.updateUserSelectedNode (selectstock.getStock());
			}
			
			clearTheGuiBeforDisplayNewInfoSection3 ();
			tabbedPanebk.setSelectedIndex(2);
			
			this.panelGgDpCjeZhanBi.setDrawAverageDailyCjeOfWeekLine(true); //保证个股显示是上边是日均成交额，下边是占比线
			
			findInputedNodeInTable ( selectstock.getMyOwnCode() );
			hightlightSpecificSector (selectstock); //餅圖
//			refreshGeGuFengXiResult (selectstock); //个股对板块的数据
			
			refreshTDXGeGuZhanBi ( selectstock.getStock() ); //个股对大盘数据
			
			paneldayCandle.displayQueKou(true);
//			refreshKXianOfTDXNodeWithSuperBanKuai (selectstock, selectstock.getBanKuai() ); //个股对板块的K线
			refreshKXianOfTDXNodeWithSuperBanKuai (selectstock, null ); //如果bankuai = null,则优先显示K线和成交量
			
			//同步板块和个股高亮的日期
			LocalDate curselecteddate = panelbkwkcjezhanbi.getCurSelectedDate();
			chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(curselecteddate));
			
			displayNodeInfo (selectstock.getStock());

			refreshCurrentStockTags (selectstock.getStock() );
			//在产业链树上寻找该个股
			cyltreecopy.searchAndLocateNodeInTree (selectstock.getStock());
			
			showReminderMessage (bkfxremind.getStockremind());
			
			hourglassCursor = null;
			Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
			setCursor(hourglassCursor2);
	}

	private void refreshCurrentStockTags(Stock selectedstock) 
	{
		Collection<BkChanYeLianTreeNode> bkstock = new HashSet<> ();
		bkstock.add(selectedstock);
		TagsServiceForNodes lbnodedbservice = new TagsServiceForNodes (bkstock);
		CacheForInsertedTag bkstkkwcache = new CacheForInsertedTag (lbnodedbservice);
		lbnodedbservice.setCache(bkstkkwcache);
		pnlstocktags.initializeTagsPanel (lbnodedbservice,bkstkkwcache);
	}
	/*
	 * 在个股表中发现输入的个股,返回在主表中是否发现改个股
	 */
	protected Boolean findInputedNodeInTable(String nodecode) 
	{
		Boolean found = false; int rowindex;
		nodecode = nodecode.substring(0,6);
		if(((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel() ).getRowCount() > 0) {
			 rowindex = ((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel() ).getStockRowIndex(nodecode);
			 
			if(rowindex >= 0) {
				found = true;
				
				int modelRow = tableGuGuZhanBiInBk.convertRowIndexToView(rowindex);
//				int modelRow = rowindex;
				int curselectrow = tableGuGuZhanBiInBk.getSelectedRow();
				if( curselectrow != modelRow) {
					tableGuGuZhanBiInBk.setRowSelectionInterval(modelRow, modelRow);
					tableGuGuZhanBiInBk.scrollRectToVisible(new Rectangle(tableGuGuZhanBiInBk.getCellRect(modelRow, 0, true)));
					
					//在当前表就有的话，就把相关PANEL清空
					panelGgDpCjeZhanBi.resetDate();
				}
			}
		}
		
		
		if( ((BanKuaiGeGuTableModel)tablexuandingzhou.getModel() ).getRowCount() >0) {
			rowindex = ((BanKuaiGeGuTableModel)tablexuandingzhou.getModel() ).getStockRowIndex(nodecode);
			if(rowindex >=0) {
				int modelRow = tablexuandingzhou.convertRowIndexToView(rowindex);
//				int modelRow = rowindex;
				int curselectrow = tablexuandingzhou.getSelectedRow();
				if( curselectrow != modelRow) {
					tablexuandingzhou.setRowSelectionInterval(modelRow, modelRow);
					tablexuandingzhou.scrollRectToVisible(new Rectangle(tablexuandingzhou.getCellRect(modelRow, 0, true)));
				}
			}
		}
		
		
		if( ((BanKuaiGeGuTableModel)tablexuandingminusone.getModel() ).getRowCount() >0 ) {
			rowindex = ((BanKuaiGeGuTableModel)tablexuandingminusone.getModel() ).getStockRowIndex(nodecode);
			if(rowindex >=0) {
				int modelRow = tablexuandingminusone.convertRowIndexToView(rowindex);
//				int modelRow = rowindex;
				int curselectrow = tablexuandingminusone.getSelectedRow();
				if( curselectrow != modelRow)  {
					tablexuandingminusone.setRowSelectionInterval(modelRow, modelRow);
					tablexuandingminusone.scrollRectToVisible(new Rectangle(tablexuandingminusone.getCellRect(modelRow, 0, true)));
				}
			}
		}
		

		if( ((BanKuaiGeGuTableModel)tablexuandingminustwo.getModel() ).getRowCount() > 0) {
			rowindex = ((BanKuaiGeGuTableModel)tablexuandingminustwo.getModel() ).getStockRowIndex(nodecode);
			if(rowindex >=0){
				int modelRow = tablexuandingminustwo.convertRowIndexToView(rowindex);
//				int modelRow = rowindex;
				int curselectrow = tablexuandingminustwo.getSelectedRow();
				if( curselectrow != modelRow) {
					tablexuandingminustwo.setRowSelectionInterval(modelRow, modelRow);
					tablexuandingminustwo.scrollRectToVisible(new Rectangle(tablexuandingminustwo.getCellRect(modelRow, 0, true)));
				}
			}
		}

		if(((BanKuaiGeGuTableModel)tablexuandingplusone.getModel() ).getRowCount() > 0) {
			rowindex = ((BanKuaiGeGuTableModel)tablexuandingplusone.getModel() ).getStockRowIndex(nodecode);
			if(rowindex >=0)  {
				int modelRow = tablexuandingplusone.convertRowIndexToView(rowindex);
//				int modelRow = rowindex;
				int curselectrow = tablexuandingplusone.getSelectedRow();
				if( curselectrow != modelRow)  {
					tablexuandingplusone.setRowSelectionInterval(modelRow, modelRow);
					tablexuandingplusone.scrollRectToVisible(new Rectangle(tablexuandingplusone.getCellRect(modelRow, 0, true)));
				}
			}
		}
		
		
		if(((BanKuaiGeGuBasicTableModel)tableExternalInfo.getModel() ).getRowCount() >0 ) {
			rowindex = ((BanKuaiGeGuBasicTableModel)tableExternalInfo.getModel() ).getStockRowIndex(nodecode);
			if(rowindex >= 0) {
				int modelRow = tableExternalInfo.convertRowIndexToView(rowindex);
//				int modelRow = rowindex;
				int curselectrow = tableExternalInfo.getSelectedRow();
				if( curselectrow != modelRow) {
					tableExternalInfo.setRowSelectionInterval(modelRow, modelRow);
					tableExternalInfo.scrollRectToVisible(new Rectangle(tableExternalInfo.getCellRect(modelRow, 0, true)));
				}
			}
		}
		if(((BanKuaiGeGuBasicTableModel)tableTempGeGu.getModel() ).getRowCount() >0 ) {
			rowindex = ((BanKuaiGeGuBasicTableModel)tableTempGeGu.getModel() ).getStockRowIndex(nodecode);
			if(rowindex >= 0) {
				int modelRow = tableTempGeGu.convertRowIndexToView(rowindex);
//				int modelRow = rowindex;
				int curselectrow = tableTempGeGu.getSelectedRow();
				if( curselectrow != modelRow) {
					tableTempGeGu.setRowSelectionInterval(modelRow, modelRow);
					tableTempGeGu.scrollRectToVisible(new Rectangle(tableTempGeGu.getCellRect(modelRow, 0, true)));
				}
			}
		}
		
		
		return found;
	}

	
	/*
	 * 
	 */
	private void adjustDate(LocalDate dateneedtobeadjusted)
	{
		//每周日一是新的一周开始，因为还没有导入数据，会显示为没有数据，所以把时间调整到上一周六
				DayOfWeek dayofweek = LocalDate.now().getDayOfWeek();
				if(dayofweek.equals(DayOfWeek.SUNDAY) ) {
					 LocalDate saturday = dateneedtobeadjusted.minus(1,ChronoUnit.DAYS);
					 this.dateChooser.setLocalDate(saturday);
					 
				} else if(dayofweek.equals(DayOfWeek.MONDAY) && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) <19 ) {
					LocalDate saturday = dateneedtobeadjusted.minus(2,ChronoUnit.DAYS);
					this.dateChooser.setLocalDate(saturday);
					 
				} else
					this.dateChooser.setDate(new Date ());
				btnresetdate.setEnabled(false);
				
				

	}
//	private void setUpZhongDianGuanZhuBanKuai ()
//	{
//		ServicesForNews zdgzsvs = this.dateChooser.getStockCalendar().getDuanQiGuanZhuService();
//		//DuanQiGuanZhuServices
//		zdgzsvs.getCache().addCacheListener(tableBkZhanBi);
//	}
	/*
	 * 用户在界面的操作，各个模块的协同操作
	 */
	private void setUpChartDataListeners ()
	{
		this.chartpanelhighlightlisteners = new HashSet<>();
//		this.barchartpanelstockofbankuaidatachangelisteners = new HashSet<>();
		this.barchartpanelbankuaidatachangelisteners = new HashSet<>();
		this.piechartpanelbankuaidatachangelisteners = new HashSet<>();
		this.barchartpanelstockdatachangelisteners = new HashSet<>();
		
		
		barchartpanelbankuaidatachangelisteners.add(panelbkwkcjezhanbi);
		barchartpanelbankuaidatachangelisteners.add(pnlbkwkcjlzhanbi);
		//板块pie chart
		piechartpanelbankuaidatachangelisteners.add(pnlcurwkggcjezhanbi);
		piechartpanelbankuaidatachangelisteners.add(panelLastWkGeGucjeZhanBi);
		
		
		//个股对板块
//		barchartpanelstockofbankuaidatachangelisteners.add(panelgegucje); //个股对于板块交易额,一般看个股对大盘的成交额，这个就不看了
//		barchartpanelstockofbankuaidatachangelisteners.add(panelggbkcjezhanbi);
		barchartpanelstockdatachangelisteners.add(panelGgDpCjeZhanBi);
		barchartpanelstockdatachangelisteners.add(panelggdpcjlwkzhanbi);
		//用户点击bar chart的一个column, highlight bar chart
		chartpanelhighlightlisteners.add(panelGgDpCjeZhanBi);
		chartpanelhighlightlisteners.add(panelbkwkcjezhanbi);
		chartpanelhighlightlisteners.add(paneldayCandle);
		chartpanelhighlightlisteners.add(panelggdpcjlwkzhanbi);
		chartpanelhighlightlisteners.add(pnlbkwkcjlzhanbi);
		
		//同步几个表要highlight的数据
		this.bkggmatchcondition.addCacheListener(tableGuGuZhanBiInBk);
		this.bkggmatchcondition.addCacheListener(tableExternalInfo);
		this.bkggmatchcondition.addCacheListener(tablexuandingzhou);
		this.bkggmatchcondition.addCacheListener(tablexuandingminusone);
		this.bkggmatchcondition.addCacheListener(tablexuandingminustwo);
		this.bkggmatchcondition.addCacheListener(tablexuandingplusone);
		this.bkggmatchcondition.addCacheListener(tableTempGeGu);
		
		this.bkggmatchcondition.addCacheListener(panelbkwkcjezhanbi);
		this.bkggmatchcondition.addCacheListener(panelGgDpCjeZhanBi);
		
	}
	/*
	 * 所有板块占比增长率的排名
	 */
	private void gettBanKuaiZhanBiRangedByGrowthRateOfPeriod (String period)
	{
		this.globeperiod = period;
		if(chxbxwholeweek.isSelected())
			globecalwholeweek = true;
		else
			globecalwholeweek = false;
		
    	LocalDate curselectdate = dateChooser.getLocalDate(); 
    	LocalDate requirestart = CommonUtility.getSettingRangeDate(curselectdate,"middle").with(DayOfWeek.MONDAY);
    	
    	SvsForNodeOfDaPan	svsdp = new SvsForNodeOfDaPan ();
    	svsdp.getNodeData("",requirestart, curselectdate,this.globeperiod,this.globecalwholeweek);
    	svsdp = null;
    	SvsForNodeOfBanKuai svsbk = new SvsForNodeOfBanKuai ();
		List<TDXNodes> bkwithcje = svsbk.getBanKuaiFenXiQualifiedNodes (curselectdate,this.globeperiod,this.globecalwholeweek,true);
		((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).addBanKuai( bkwithcje );
		((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).refresh(curselectdate,0,globeperiod);
		
    	//显示大盘成交量
		NumberFormat percentFormat = NumberFormat.getPercentInstance(Locale.CHINA);
    	percentFormat.setMinimumFractionDigits(1);
    	DecimalFormat df2 = new DecimalFormat(".##");
    	TDXNodes childnode = (TDXNodes) svsbk.getNode("000300");
    	NodeXPeriodData bkdata = ((TDXNodes)childnode).getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		try {
				Double cjerec = bkdata.getChengJiaoEr(curselectdate,0);
				Double zhanbi = bkdata.getChenJiaoErZhanBi(curselectdate, 0);
				lblhscje.setText( df2.format(cjerec /100000000.0) +  " (" + percentFormat.format (zhanbi) + ")");
		} catch (java.lang.NullPointerException e) {
				lblhscje.setText( "本周没有数据" );
		}
			
		childnode =  (TDXNodes) svsbk.getNode("999999"); 
		bkdata = ((TDXNodes)childnode).getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		try {
				Double cjerec = bkdata.getChengJiaoEr(curselectdate,0);
				Double zhanbi = bkdata.getChenJiaoErZhanBi(curselectdate, 0);
				lblshcje.setText( df2.format(cjerec /100000000.0) +  " (" + percentFormat.format (zhanbi) + ")");
		} catch (java.lang.NullPointerException e) {
				lblshcje.setText( "本周没有数据" );
		}
		 
		childnode =  (TDXNodes) svsbk.getNode("399006");	
		bkdata = ((TDXNodes)childnode).getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		try {
				Double cjerec = bkdata.getChengJiaoEr(curselectdate,0);
				Double zhanbi = bkdata.getChenJiaoErZhanBi(curselectdate, 0);
				lblcybcje.setText( df2.format(cjerec /100000000.0) +  " (" + percentFormat.format (zhanbi) + ")");
		} catch (java.lang.NullPointerException e) {
				lblcybcje.setText( "本周没有数据" );
		}
			
		childnode =  (TDXNodes) svsbk.getNode("000016");//(TDXNodes)this.allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode("000016", BkChanYeLianTreeNode.TDXBK); 
		bkdata = ((TDXNodes)childnode).getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		try {
				Double cjerec = bkdata.getChengJiaoEr(curselectdate,0);
				Double zhanbi = bkdata.getChenJiaoErZhanBi(curselectdate, 0);
				lbl50cje.setText( df2.format(cjerec /100000000.0) +  " (" + percentFormat.format (zhanbi) + ")");
		} catch (java.lang.NullPointerException e) {
				lbl50cje.setText( "本周没有数据" );
		}
		//设置下拉框的当前时间，为显示关注板块个股使用
		((JStockComboBoxModel)combxsearchbk.getModel() ).setCurrentDataDate(curselectdate);
		((JStockComboBoxModel)combxstockcode.getModel() ).setCurrentDataDate(curselectdate);
		
		curselectdate = null;
		svsbk = null;
//		cacheAll = null;
		SystemAudioPlayed.playSound();
	}
	/*
	 * 对板块个股表高亮
	 */
	private void refreshBanKuaiGeGuTableHightLight ()
	{
//		setExportMainConditionBasedOnUserSelection (displayexpc);
		
		setExportMainConditionBasedOnUserSelection (bkggmatchcondition);
		
//		bkfxhighlightvaluesoftableslisteners.forEach(l -> l.hightLightFxValues( displayexpc ) );

		//对于panel来说，hightLightFxValues第一个参数不能用，因为panel 都是 节点对她的上级的占比
//		panelGgDpCjeZhanBi.hightLightFxValues(displayexpc);
//		panelbkwkcjezhanbi.hightLightFxValues(displayexpc);
		

	}
	/*
	 * 把当前的板块当周符合条件的导出
	 */
	private void exportBanKuaiWithGeGuOnCondition2 ()
	{
	
		if(exportcond == null || exportcond.size() == 0) {
			if(!ckboxshowcje.isSelected() && !ckbxdpmaxwk.isSelected() && !chkliutongsz.isSelected() && !ckbxcjemaxwk.isSelected()){
				JOptionPane.showMessageDialog(null,"未设置导出条件，请先设置导出条件！");
				return;
			} else if(exportcond == null) { //用户设置条件后可能直接点击导出，而没有先点击加入，这里是统一界面的行为
				exportcond = new ArrayList<BanKuaiGeGuMatchCondition> ();
				initializeExportConditions ();
			} else
				initializeExportConditions ();
		}

		String msg =  "导出耗时较长，请先确认条件是否正确。\n是否导出？";
		int exchangeresult = JOptionPane.showConfirmDialog(null,msg , "确实导出？", JOptionPane.OK_CANCEL_OPTION);
		if(exchangeresult == JOptionPane.CANCEL_OPTION)
				return;
		
		LocalDate curselectdate = dateChooser.getLocalDate();//dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		String dateshowinfilename = null;
		if(globeperiod == null  || globeperiod.equals(NodeGivenPeriodDataItem.WEEK))
			dateshowinfilename = "week" + curselectdate.with(DayOfWeek.FRIDAY).toString().replaceAll("-","");
		else if(globeperiod.equals(NodeGivenPeriodDataItem.DAY))
			dateshowinfilename = "day" + curselectdate.toString().replaceAll("-","");
		else if(globeperiod.equals(NodeGivenPeriodDataItem.MONTH))
			dateshowinfilename = "month" +  curselectdate.withDayOfMonth(curselectdate.lengthOfMonth()).toString().replaceAll("-","");
		String exportfilename = sysconfig.getTDXModelMatchExportFile () + "TDX模型个股" + dateshowinfilename + ".EBK";
		File filefmxx = new File( exportfilename );
		
		if( !filefmxx.getParentFile().exists() ) {  
            //如果目标文件所在的目录不存在，则创建父目录  
            logger.debug("目标文件所在目录不存在，准备创建它！");  
            if(!filefmxx.getParentFile().mkdirs()) {  
                System.out.println("创建目标文件所在目录失败！");  
                return ;  
            }  
        }  
		
		try {
				if (filefmxx.exists()) {
					filefmxx.delete();
					filefmxx.createNewFile();
				} else
					filefmxx.createNewFile();
		} catch (Exception e) {
				e.printStackTrace();
				return ;
		}
//		pnl_paomd.refreshMessage(title+paomad);
		exporttask = new ExportTask2(exportcond, curselectdate,globeperiod,filefmxx);
		exporttask.addPropertyChangeListener(new PropertyChangeListener() {
		      @Override
		      public void propertyChange(final PropertyChangeEvent eventexport) {
		        switch (eventexport.getPropertyName()) {
		        case "progress":
		        	progressBarExport.setIndeterminate(false);
		        	progressBarExport.setString("正在导出..." + (Integer) eventexport.getNewValue() + "%(,点击取消导出)");
		        	progressBarExport.setToolTipText("点击取消导出");
		          break;
		        case "state":
		          switch ((StateValue) eventexport.getNewValue()) {
		          case DONE:
		        	exportCancelAction.putValue(Action.NAME, "导出条件个股");
		            try {
		              final int count = exporttask.get();
		              //保存XML
//		              bkcyl.parseWeeklyBanKuaiFengXiFileToXmlAndPatchToCylTree (filefmxx,curselectdate.with(DayOfWeek.FRIDAY) );
		              
		              int exchangeresult = JOptionPane.showConfirmDialog(null, "导出完成，是否打开" + filefmxx.getAbsolutePath() + "查看","导出完成", JOptionPane.OK_CANCEL_OPTION);
		      		  if(exchangeresult == JOptionPane.CANCEL_OPTION) {
		      			  progressBarExport.setString(" ");
		      			  return;
		      		  }
		      		  try {
		      			String path = filefmxx.getAbsolutePath();
		      			Runtime.getRuntime().exec("explorer.exe /select," + path);
		      		  } catch (IOException e1) {
		      				e1.printStackTrace();
		      		  }
		      		progressBarExport.setString(" ");
		      		System.gc();
		            } catch (final CancellationException e) {
		            	try {
							exporttask.get();
						} catch (InterruptedException | ExecutionException | CancellationException e1) {
//							e1.printStackTrace();
						}
		            	progressBarExport.setIndeterminate(false);
		            	progressBarExport.setValue(0);
		            	JOptionPane.showMessageDialog(null, "导出条件个股被终止！", "导出条件个股",JOptionPane.WARNING_MESSAGE);
		            	progressBarExport.setString("导出设置条件个股");
		            } catch (final Exception e) {
//		              JOptionPane.showMessageDialog(Application.this, "The search process failed", "Search Words",
//		                  JOptionPane.ERROR_MESSAGE);
		            }

		            exporttask = null;
		            break;
		          case STARTED:
		          case PENDING:
		        	  exportCancelAction.putValue(Action.NAME, "取消导出");
		        	  progressBarExport.setVisible(true);
		        	  progressBarExport.setIndeterminate(true);
		            break;
		          }
		          break;
		        }
		      }
		    });
		
		exporttask.execute();
//		try {
//			exporttask.get();
//		} catch (InterruptedException | ExecutionException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	/*
	 * 
	 */
	private void clearTheGuiBeforDisplayNewInfoSection1 ()
	{
		tabbedPanebk.setSelectedIndex(0);
		((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).deleteAllRows();
		
		btnresetdate.setEnabled(true);
		
		ckboxparsefile.setSelected(false);
		tfldparsedfile.setText("");
		
		paneldayCandle.resetDate();
		
//		editorPanebankuai.resetSelectedBanKuai ();
		
		
	}
	private void clearTheGuiBeforDisplayNewInfoSection2 ()
	{
		((BanKuaiInfoTableModel)tableselectedwkbkzb.getModel()).deleteAllRows();
		tabbedPanebk.setTitleAt(1, "选定周" );
		
		tabbedPanegegu.setSelectedIndex(0);
		
		panelbkwkcjezhanbi.resetDate();
			
		pnlcurwkggcjezhanbi.resetDate();
		
		
		panelLastWkGeGucjeZhanBi.resetDate();
		
		tabbedPanegegu.setTitleAt(0, this.getTabbedPanegeguTabTiles(0) );
		tabbedPanegegu.setTitleAt(1, this.getTabbedPanegeguTabTiles(1) );
		tabbedPanegegu.setTitleAt(2, this.getTabbedPanegeguTabTiles(2) );
		tabbedPanegegu.setTitleAt(3, this.getTabbedPanegeguTabTiles(3) );
		tabbedPanegegu.setTitleAt(4, this.getTabbedPanegeguTabTiles(4) );
		tabbedPanegegu.setTitleAt(5, this.getTabbedPanegeguTabTiles(5) );
		
		((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).deleteAllRows();
		((BanKuaiGeGuBasicTableModel)tableExternalInfo.getModel()).deleteAllRows();
		((BanKuaiGeGuTableModel)tablexuandingzhou.getModel()).deleteAllRows();
		((BanKuaiGeGuTableModel)tablexuandingminusone.getModel()).deleteAllRows();
		((BanKuaiGeGuTableModel)tablexuandingminustwo.getModel()).deleteAllRows();
		((BanKuaiGeGuTableModel)tablexuandingplusone.getModel()).deleteAllRows();
		((BanKuaiGeGuBasicTableModel)tableExternalInfo.getModel()).deleteAllRows();
		
		menuItemchengjiaoer.setText("X 按成交额排名");
    	menuItemliutong.setText("按流通市值排名");
    	menuItemtimerangezhangfu.setText("按阶段涨幅排名");
    	
//		editorPanenodeinfo.setText("");
		
		pnlstocktags.removeAll();
	}
	/*
	 * 指定tabbedPanegegu的title，否则会出现几个地方不一致
	 */
	private String getTabbedPanegeguTabTiles (int tabindex)
	{
		String[] jtabbedpanetitle = { "当前周", "其他信息","选定周","选定周-1","选定周+1","上次选定周"};
		return jtabbedpanetitle[tabindex];
	}
	private void clearTheGuiBeforDisplayNewInfoSection3 ()
	{
		tabbedPanegeguzhanbi.setSelectedIndex(0);
		
		panelGgDpCjeZhanBi.resetDate();
		
		panelggdpcjlwkzhanbi.resetDate();
	}
	
	
	/*
	 * 显示个股相对板块的占比的chart
	 */
	private void refreshGeGuBanKuaiFengXiResult (StockOfBanKuai stock)
	{
//			LocalDate curselectdate = dateChooser.getLocalDate();//dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//
//			barchartpanelstockofbankuaidatachangelisteners.forEach(l -> l.updatedDate(stock, curselectdate, 0,globeperiod));
	}
	/*
	 * 用户选择个股后，显示个股自身对大盘的各种占比
	 */
	private void refreshTDXGeGuZhanBi (Stock selectstock)
	{
		selectstock.setHasReviewedToday();
		
		LocalDate curselectdate = dateChooser.getLocalDate();// dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//		selectstock = allbksks.getStock(selectstock,CommonUtility.getSettingRangeDate(curselectdate, "large"),curselectdate,
//				NodeGivenPeriodDataItem.WEEK,this.globecalwholeweek);
		
		
		for (BarChartPanelDataChangedListener tmplistener : barchartpanelstockdatachangelisteners) {
			tmplistener.updatedDate(selectstock, CommonUtility.getSettingRangeDate(curselectdate, "basic"),curselectdate, globeperiod);
		}
	}
	/*
	 * 板块和个股的K线，可以同时显示，用以对比研究
	 */
	private void refreshKXianOfTDXNodeWithSuperBanKuai (TDXNodes selectnode,TDXNodes superbankuai)
	{
		LocalDate curselectdate = dateChooser.getLocalDate();
		if(this.globecalwholeweek)
			curselectdate = curselectdate.with(DayOfWeek.SATURDAY);
		
		TDXNodes tmpnode = null;
		if(selectnode.getType() == BkChanYeLianTreeNode.TDXGG) {
//			//日线K线走势，目前K线走势和成交量在日线和日线以上周期是分开的，所以调用时候要特别小心，以后会合并
			tmpnode = selectnode;
		} else if(selectnode.getType() == BkChanYeLianTreeNode.TDXBK) {
			tmpnode = selectnode;
		} else if (selectnode.getType() == BkChanYeLianTreeNode.BKGEGU) {
			tmpnode = ((StockOfBanKuai)selectnode).getStock() ;
		}
		
		 if(superbankuai != null  && superbankuai.getType() == BkChanYeLianTreeNode.TDXBK) {
			 if(treeofbkstk.getSpecificNodeByHypyOrCode(superbankuai.getMyOwnCode(), BkChanYeLianTreeNode.TDXBK ) != null) { //临时个股没有板块
				 SvsForNodeOfBanKuai svsbk = new SvsForNodeOfBanKuai ();
				 superbankuai = (TDXNodes) svsbk.getNodeData(superbankuai, CommonUtility.getSettingRangeDate(curselectdate, "large"),curselectdate, 
						 NodeGivenPeriodDataItem.WEEK,this.globecalwholeweek);
				 svsbk.syncNodeData(superbankuai);
				 
				 paneldayCandle.updatedDate(superbankuai,tmpnode,CommonUtility.getSettingRangeDate(curselectdate, "basic"),curselectdate,NodeGivenPeriodDataItem.DAY);
			 } else //没有superbankuai,
				 paneldayCandle.updatedDate(tmpnode,CommonUtility.getSettingRangeDate(curselectdate, "basic"),curselectdate,NodeGivenPeriodDataItem.DAY);
		 } else
			 paneldayCandle.updatedDate(tmpnode,CommonUtility.getSettingRangeDate(curselectdate, "basic"),curselectdate,NodeGivenPeriodDataItem.DAY);

		
		ServicesForNewsLabel svslabel = new NewsLabelServices ();
//		ServicesForNews svsdqgz = new DuanQiGuanZhuServices ();
//    	NewsCache dqgzcache = new NewsCache ("ALL",svsdqgz,svslabel,LocalDate.now().minusMonths(6),LocalDate.now().plusMonths(6));
//    	svsdqgz.setCache(dqgzcache);
//    	paneldayCandle.displayZhiShuGuanJianRiQiToGui(dqgzcache.produceNews());
    	
    	ServicesForNews svsnews = new NewsServices ();
    	NewsCache newcache = new NewsCache (selectnode.getMyOwnCode(),svsnews,svslabel,LocalDate.now().minusMonths(6),LocalDate.now().plusMonths(6));
    	svsnews.setCache(newcache);
    	paneldayCandle.displayNodeNewsToGui (newcache.produceNews() );
	}
	/*
	 * 
	 */
	private void displayStockSuoShuBanKuai(Stock selectstock) 
	{
		DisPlayNodeSuoShuBanKuaiListServices svsstkbk = new DisPlayNodeSuoShuBanKuaiListServices (selectstock);
		DisPlayNodeSuoShuBanKuaiListPanel stkbkpnl = new DisPlayNodeSuoShuBanKuaiListPanel (svsstkbk);
//		stkbkpnl.setPreferredSize(new Dimension(200,30));
		editorPanebankuai.setViewportView(stkbkpnl);
		ScrollUtil.scroll(editorPanebankuai, ScrollUtil.BOTTOM);
		ScrollUtil.scroll(editorPanebankuai, ScrollUtil.LEFT);
		
		stkbkpnl.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
            	if (evt.getPropertyName().equals(DisPlayNodeSuoShuBanKuaiListPanel.URLSELECTED_PROPERTY)) {
            		
            		String selbk = evt.getNewValue().toString();
    				String selbkcode;
    				if(selbk != null)
    					 selbkcode = selbk.trim().substring(1, 7);
    				else 
    					return;
    				
    				int rowindex = 0;
    				try {
    					rowindex = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuaiRowIndex(selbkcode);
    				} catch (java.lang.NullPointerException  e) {
    					e.printStackTrace();
    					return;
    				}
    				if(rowindex != -1) {
    					
    					Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
    					setCursor(hourglassCursor);
    					
    					int modelRow = tableBkZhanBi.convertRowIndexToView(rowindex);
    					tableBkZhanBi.setRowSelectionInterval(modelRow, modelRow);
    					tableBkZhanBi.scrollRectToVisible(new Rectangle(tableBkZhanBi.getCellRect(modelRow, 0, true)));
    					
    					//找到用户选择的板块
    					BanKuai selectedbk = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(rowindex);
    					unifiedOperationsAfterUserSelectABanKuai (selectedbk);
    					
    					//找不到该股票
    					String stockcode = combxstockcode.getSelectedItem().toString().substring(0, 6);
    					if(!findInputedNodeInTable (stockcode) )
    						JOptionPane.showMessageDialog(null,"在某个个股表内没有发现该股，可能在这个时间段内该股停牌","Warning",JOptionPane.WARNING_MESSAGE);
    					
//    					Toolkit.getDefaultToolkit().beep();
    					hourglassCursor = null;
    					Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
    					setCursor(hourglassCursor2);
    					
    					SystemAudioPlayed.playSound();
    				} else {
    					JOptionPane.showMessageDialog(null,"该板块设置为不显示在板块列表中，请在 ‘重点关注与产业链’ 变更设置！","Warning",JOptionPane.WARNING_MESSAGE);
    				}
            	}
            }
		});

		
//		editorPanebankuai.displayBanKuaiListContentsForStock(selectstock);
		
	}
	/*
	 * 显示用户点击bar column后应该提示的信息
	 */
	private void setUserSelectedColumnMessage(TDXNodes node,LocalDate seldate) 
	{
		DisplayNodeExchangeDataServices svsnodedata = new DisplayNodeExchangeDataServices (node, seldate, this.globeperiod);
		DisplayNodeExchangeDataServicesPanel nodedatapnl = new DisplayNodeExchangeDataServicesPanel (svsnodedata) ;
		Dimension size = new Dimension(scrldailydata.getViewport().getSize().width, nodedatapnl.getContentHeight() + 10  );
		nodedatapnl.setPreferredSize(size);
		nodedatapnl.setMinimumSize(size);
		nodedatapnl.setMaximumSize(size);
		tfldselectedmsg.add(nodedatapnl);
		tfldselectedmsg.revalidate();
		tfldselectedmsg.repaint();
		
		nodedatapnl.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {

                if (evt.getPropertyName().equals(DisplayNodeExchangeDataServicesPanel.EXPORTCSV_PROPERTY)) {

//                	Multimap<String, LocalDate> nodespecificdatainfo = tfldselectedmsg.getDisplayBanKuaiSpecificDateInfo();
//                	for (Map.Entry entry : nodespecificdatainfo.entries()) 
//                	{ 
//                		String nodecode = (String) entry.getKey();
//                		LocalDate selectdate = (LocalDate) entry.getValue();
//                		
//                		SvsForNodeOfBanKuai svsbk = new SvsForNodeOfBanKuai ();
//                		BanKuai bk = (BanKuai) svsbk.getNode(nodecode);
//                		svsbk = null;
//                		List<TDXNodes> tmplist = new ArrayList<> ();
//                		tmplist.add(bk);
//                		exportTDXNodesDataToCsv(tmplist,selectdate.toString());
//                		tmplist = null;
//                	}
//                	nodespecificdatainfo = tfldselectedmsg.getDisplayStockSpecificDateInfo();
//                	for (Map.Entry entry : nodespecificdatainfo.entries()) 
//                	{ 
//                		String nodecode = (String) entry.getKey();
//                		LocalDate selectdate = (LocalDate) entry.getValue();
//                		
//                		SvsForNodeOfStock svsstk = new SvsForNodeOfStock	();
//                		Stock stock = (Stock) svsstk.getNode(nodecode);
//                		svsstk = null;
//                		List<TDXNodes> tmplist = new ArrayList<TDXNodes>();
//                		tmplist.add(stock);
//                		exportTDXNodesDataToCsv( tmplist ,selectdate.toString());
//                		tmplist = null;
//                	}
                }
            }
		});
//		tfldselectedmsg.displayNodeSpecificDateInfo(node, seldate ,this.globeperiod);
	}
	private void setUserSelectedColumnMessage(String htmlstring) 
	{
		pnl_paomd.refreshMessage(htmlstring);
//		tfldselectedmsg.displayNodeSelectedInfo (htmlstring);
	}
	/*
	 * 用户点击几个地方，显示气泡提示信息
	 */
	private void showReminderMessage (String showmsg)
	{
		ReminderPopToolTip tip = new ReminderPopToolTip();  
        tip.setToolTip(null,showmsg);
	}
	/*
	 * 
	 */
	private void createEvents() 
	{
		lblshcje.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				BanKuai shanghai = (BanKuai) treeofbkstk.getSpecificNodeByHypyOrCode("999999", BkChanYeLianTreeNode.TDXBK);
				unifiedOperationsAfterUserSelectABanKuai (shanghai);
				
				// 定位
				Integer rowindex = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel() ).getBanKuaiRowIndex("999999");
				if(rowindex != null && rowindex >0) {
						int modelRow = tableBkZhanBi.convertRowIndexToView(rowindex);
						tableBkZhanBi.scrollRectToVisible(new Rectangle(tableBkZhanBi.getCellRect(modelRow, 0, true)));
						tableBkZhanBi.setRowSelectionInterval(modelRow, modelRow);
				}
			}
		});
		lblshanghai.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				BanKuai shanghai = (BanKuai) treeofbkstk.getSpecificNodeByHypyOrCode("999999", BkChanYeLianTreeNode.TDXBK);
				unifiedOperationsAfterUserSelectABanKuai (shanghai);
				
				// 定位
				Integer rowindex = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel() ).getBanKuaiRowIndex("999999");
				if(rowindex != null && rowindex >0) {
						int modelRow = tableBkZhanBi.convertRowIndexToView(rowindex);
						tableBkZhanBi.scrollRectToVisible(new Rectangle(tableBkZhanBi.getCellRect(modelRow, 0, true)));
						tableBkZhanBi.setRowSelectionInterval(modelRow, modelRow);
				}
			}
		});
		lblhscje.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				BanKuai husheng = (BanKuai) treeofbkstk.getSpecificNodeByHypyOrCode("000300", BkChanYeLianTreeNode.TDXBK);
				unifiedOperationsAfterUserSelectABanKuai (husheng);
				
				// 定位
				Integer rowindex = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel() ).getBanKuaiRowIndex("000300");
				if(rowindex != null && rowindex >0) {
						int modelRow = tableBkZhanBi.convertRowIndexToView(rowindex);
						tableBkZhanBi.scrollRectToVisible(new Rectangle(tableBkZhanBi.getCellRect(modelRow, 0, true)));
						tableBkZhanBi.setRowSelectionInterval(modelRow, modelRow);
				}
			}
		});
		lblhusheng.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				BanKuai husheng = (BanKuai) treeofbkstk.getSpecificNodeByHypyOrCode("000300", BkChanYeLianTreeNode.TDXBK);
				unifiedOperationsAfterUserSelectABanKuai (husheng);
				
				// 定位
				Integer rowindex = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel() ).getBanKuaiRowIndex("000300");
				if(rowindex != null && rowindex >0) {
						int modelRow = tableBkZhanBi.convertRowIndexToView(rowindex);
						tableBkZhanBi.scrollRectToVisible(new Rectangle(tableBkZhanBi.getCellRect(modelRow, 0, true)));
						tableBkZhanBi.setRowSelectionInterval(modelRow, modelRow);
				}
			}
		});
		lbl50cje.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				BanKuai szfifty = (BanKuai) treeofbkstk.getSpecificNodeByHypyOrCode("000016", BkChanYeLianTreeNode.TDXBK);
				unifiedOperationsAfterUserSelectABanKuai (szfifty);
				
				// 定位
				Integer rowindex = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel() ).getBanKuaiRowIndex("000016");
				if(rowindex != null && rowindex >0) {
						int modelRow = tableBkZhanBi.convertRowIndexToView(rowindex);
						tableBkZhanBi.scrollRectToVisible(new Rectangle(tableBkZhanBi.getCellRect(modelRow, 0, true)));
						tableBkZhanBi.setRowSelectionInterval(modelRow, modelRow);
				}
			}
			
		});
		lblfifty.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				BanKuai szfifty = (BanKuai) treeofbkstk.getSpecificNodeByHypyOrCode("000016", BkChanYeLianTreeNode.TDXBK);
				unifiedOperationsAfterUserSelectABanKuai (szfifty);
				
				// 定位
				Integer rowindex = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel() ).getBanKuaiRowIndex("000016");
				if(rowindex != null && rowindex >0) {
						int modelRow = tableBkZhanBi.convertRowIndexToView(rowindex);
						tableBkZhanBi.scrollRectToVisible(new Rectangle(tableBkZhanBi.getCellRect(modelRow, 0, true)));
						tableBkZhanBi.setRowSelectionInterval(modelRow, modelRow);
				}
			}
			
		});
		lblcybcje.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				BanKuai cyb = (BanKuai) treeofbkstk.getSpecificNodeByHypyOrCode("399006", BkChanYeLianTreeNode.TDXBK);
				unifiedOperationsAfterUserSelectABanKuai (cyb);
				
				// 定位
				Integer rowindex = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel() ).getBanKuaiRowIndex("399006");
				if(rowindex != null && rowindex >0) {
						int modelRow = tableBkZhanBi.convertRowIndexToView(rowindex);
						tableBkZhanBi.scrollRectToVisible(new Rectangle(tableBkZhanBi.getCellRect(modelRow, 0, true)));
						tableBkZhanBi.setRowSelectionInterval(modelRow, modelRow);
				}
			}
		});
		lblchuangyeban.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				BanKuai cyb = (BanKuai) treeofbkstk.getSpecificNodeByHypyOrCode("399006", BkChanYeLianTreeNode.TDXBK);
				unifiedOperationsAfterUserSelectABanKuai (cyb);
				
				// 定位
				Integer rowindex = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel() ).getBanKuaiRowIndex("399006");
				if(rowindex != null && rowindex >0) {
						int modelRow = tableBkZhanBi.convertRowIndexToView(rowindex);
						tableBkZhanBi.scrollRectToVisible(new Rectangle(tableBkZhanBi.getCellRect(modelRow, 0, true)));
						tableBkZhanBi.setRowSelectionInterval(modelRow, modelRow);
				}
			}
		});
		
		menuItemTempGeGuFromFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				parseTempGeGuFromFileFunciotns ();
        		
			}
			
		});
		
		menuItemTempGeGuFromTDXSw.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				parseTempGeGuFromTDXSwFunciotns();
        		
			}
			
		});
		menuItemTempGeGuFromZhidingbk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				parseTempGeGuFromZhiDingBanKuaiFunciotn();
        		
			}
		});
		
		pnlbktags.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {

                if (evt.getPropertyName().equals(LabelTag.PROPERTYCHANGEDATAGWASSELECTED )) {
                	Collection<Tag> newvalue = (Collection<Tag>) evt.getNewValue();
                	 ((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel() ).setCurrentHighlightKeyWords (newvalue);
                	 tableGuGuZhanBiInBk.repaint();
                }
            }
		});
		
		btnresetdate.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				LocalDate cursettingdate = dateChooser.getLocalDate(); //.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	        	if(!cursettingdate.equals(LocalDate.now())) {
	        		adjustDate (LocalDate.now());
	        	} else {
	        		btnresetdate.setEnabled(false);
	        	}
	
			}
		});
		chxbxwholeweek.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				if(chxbxwholeweek.isSelected() ) {
					globecalwholeweek = true;
				}	else {
					globecalwholeweek = false;
				}
				
				int action = JOptionPane.showConfirmDialog(null, "需要重新计算分析结果，是否继续？","警告", JOptionPane.YES_NO_OPTION);
				if(0 == action) {
					clearTheGuiBeforDisplayNewInfoSection1 ();
					clearTheGuiBeforDisplayNewInfoSection2 ();
					clearTheGuiBeforDisplayNewInfoSection3 ();
					
					gettBanKuaiZhanBiRangedByGrowthRateOfPeriod (globeperiod);
				} else {
					chxbxwholeweek.setSelected(!chxbxwholeweek.isSelected());
				}
			}
		});
		
		menuItemQiangShigg.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				int row = tableGuGuZhanBiInBk.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择一个板块","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				int modelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(row); 
				StockOfBanKuai stock = ((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).getStock(modelRow);

        		QiangShi qs =  new 	QiangShi(stock.getStock(), "描述", LocalDate.now(), LocalDate.now(), "详细描述", "强势个股",new HashSet<>(),"URL");
        		ServicesForNews svsqs = new QiangShiServices ();
        		CreateExternalNewsDialog createnewDialog = new CreateExternalNewsDialog (svsqs);
                createnewDialog.setNews(qs);
                createnewDialog.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
                createnewDialog.setVisible(true);
		
			}
			
		});

		menuItemRuoShigg.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				int row = tableGuGuZhanBiInBk.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择一个板块","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				int modelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(row); 
				StockOfBanKuai stock = ((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).getStock(modelRow);

        		QiangShi qs =  new 	QiangShi(stock.getStock(), "描述", LocalDate.now(), LocalDate.now(), "详细描述", "强势个股",new HashSet<>(),"URL");
        		ServicesForNews svsqs = new RuoShiServices ();
        		CreateExternalNewsDialog createnewDialog = new CreateExternalNewsDialog (svsqs);
                createnewDialog.setNews(qs);
                createnewDialog.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
                createnewDialog.setVisible(true);
        		
			}
			
		});
		   
		menuItemQiangShibk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				int row = tableBkZhanBi.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择一个板块","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				int modelRow = tableBkZhanBi.convertRowIndexToModel(row); 
				BanKuai bankuai = ((BanKuaiInfoTableModel) tableBkZhanBi.getModel()).getBanKuai(modelRow);

				
				QiangShi qs =  new 	QiangShi(bankuai, "描述", LocalDate.now(), LocalDate.now(), "详细描述", "强势个股",new HashSet<>(),"URL");
        		ServicesForNews svsqs = new QiangShiServices ();
        		CreateExternalNewsDialog createnewDialog = new CreateExternalNewsDialog (svsqs);
                createnewDialog.setNews(qs);
                createnewDialog.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
                createnewDialog.setVisible(true);
		
			}
			
		});

		
		menuItemRuoShibk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				int row = tableBkZhanBi.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择一个板块","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				int modelRow = tableBkZhanBi.convertRowIndexToModel(row); 
				BanKuai bankuai = ((BanKuaiInfoTableModel) tableBkZhanBi.getModel()).getBanKuai(modelRow);
				
				QiangShi qs =  new 	QiangShi(bankuai, "描述", LocalDate.now(), LocalDate.now(), "详细描述", "强势个股",new HashSet<>(),"URL");
        		ServicesForNews svsqs = new RuoShiServices ();
        		CreateExternalNewsDialog createnewDialog = new CreateExternalNewsDialog (svsqs);
                createnewDialog.setNews(qs);
                createnewDialog.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
                createnewDialog.setVisible(true);
				
			}
			
		});
	
		this.addMouseListener(new MouseAdapter() {
			
	        public void mouseClicked(MouseEvent evt) 
	        {
	        	
	        }
		});
		
		chbxquekou.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				refreshBanKuaiGeGuTableHightLight ();
				
			}
		});
		
		btnexportcsv.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				startupExportToCsv ();
			}
		});
		
		paneldayCandle.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {

                if (evt.getPropertyName().equals(BanKuaiFengXiCandlestickPnl.ZHISHU_PROPERTY)) {
                    @SuppressWarnings("unchecked")
                    
                    TDXNodes stockofbank = paneldayCandle.getCurDisplayedNode ();
                    
                    String zhishuinfo = evt.getNewValue().toString();
                    if(zhishuinfo.toLowerCase().equals("bankuaizhisu") ) {
                    	
                    	int row = tableBkZhanBi.getSelectedRow();
        				if(row <0) {
        					JOptionPane.showMessageDialog(null,"请选择一个板块！","Warning",JOptionPane.WARNING_MESSAGE);
        					return;
        				}
        				int modelRow = tableBkZhanBi.convertRowIndexToModel(row);
        				BanKuai selectedbk = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(modelRow);
                    	
        				paneldayCandle.displayQueKou(true);
        				refreshKXianOfTDXNodeWithSuperBanKuai ( stockofbank, selectedbk );
                		
                		
                    } else if(zhishuinfo.toLowerCase().equals("dapanzhishu") ) {
                    	String danpanzhishu = JOptionPane.showInputDialog(null,"请输入叠加的大盘指数", "999999");
//                    	BanKuai zhishubk =  (BanKuai) allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode(danpanzhishu.toLowerCase(),BkChanYeLianTreeNode.TDXBK);
                    	SvsForNodeOfBanKuai svsbk = new SvsForNodeOfBanKuai ();
                    	BanKuai zhishubk = (BanKuai) svsbk.getNode(danpanzhishu.toLowerCase());
                    	svsbk = null;
                    	if(zhishubk == null)  {
        					JOptionPane.showMessageDialog(null,"指数代码有误！","Warning",JOptionPane.WARNING_MESSAGE);
        					return;
        				}

                    	if(stockofbank.getType() == BkChanYeLianTreeNode.TDXGG || stockofbank.getType() == BkChanYeLianTreeNode.BKGEGU )
                    		paneldayCandle.displayQueKou(true);
                    	else
                    		paneldayCandle.displayQueKou(false);
                    	refreshKXianOfTDXNodeWithSuperBanKuai ( stockofbank, zhishubk );
                    	
                    } else if(zhishuinfo.toLowerCase().equals("zhishuguanjianriqi") ) {
//                		Collection<InsertedMeeting> zhishukeylists = newsdbopt.getZhiShuKeyDates(null, null);
//                		paneldayCandle.updateZhiShuKeyDates (zhishukeylists);
                    }

                } 
             }
        });

		menuItemAddRmvBkToYellow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
            	int row = tableBkZhanBi.getSelectedRow();
    			int modelRow = tableBkZhanBi.convertRowIndexToModel(row);
    			BanKuai bk = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(modelRow);
    			NodesTreeRelated filetree = bk.getNodeTreeRelated ();
    			if(filetree.selfIsMatchModel(dateChooser.getLocalDate() ) ) 
    				filetree.setSelfIsMatchModel(dateChooser.getLocalDate(), false);
    			else
    				filetree.setSelfIsMatchModel(dateChooser.getLocalDate(), true);
    			
    			tableBkZhanBi.repaint();
            }
        });
		
		menuItemsiglebktocsv.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	int row = tableBkZhanBi.getSelectedRow();
    			int modelRow = tableBkZhanBi.convertRowIndexToModel(row);
    			BanKuai bk = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(modelRow);
    			
    			List<TDXNodes> tmplist = new ArrayList<TDXNodes> ();
    			tmplist.add(bk);
    			exportTDXNodesDataToCsv (tmplist,"single");
    			tmplist = null;
            }
        });
		menuItemsiglestocktocsv.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
            	int row = tableGuGuZhanBiInBk.getSelectedRow();
    			int modelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(row);
    			StockOfBanKuai selectstock = ((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).getStock (modelRow);
    			List<TDXNodes> tmplist = new ArrayList<TDXNodes> ();
    			tmplist.add(selectstock.getStock() );
    			exportTDXNodesDataToCsv (tmplist,"single");
    			tmplist = null;
            }
        });
		menuItemAddRmvStockToYellow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
            	int row = tableGuGuZhanBiInBk.getSelectedRow();
    			int modelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(row);
    			StockOfBanKuai selectstock = ((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).getStock (modelRow);
    			Stock stock = selectstock.getStock();
    			NodesTreeRelated filetree = stock.getNodeTreeRelated ();
    			if(filetree.selfIsMatchModel(dateChooser.getLocalDate() ) ) 
    				filetree.setSelfIsMatchModel(dateChooser.getLocalDate(), false);
    			else
    				filetree.setSelfIsMatchModel(dateChooser.getLocalDate(), true);
    			
    			tableGuGuZhanBiInBk.repaint();
            }
        });
		
		
		menuItemnonshowselectbkinfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if( menuItemnonshowselectbkinfo.getText().contains("X") ) {
            		menuItemnonshowselectbkinfo.setText("选择同时计算该周分析数据");
            		panelbkwkcjezhanbi.setAllowDrawAnnoation(false);
            	} else {
            		menuItemnonshowselectbkinfo.setText("X选择同时计算该周分析数据");
            		panelbkwkcjezhanbi.setAllowDrawAnnoation(true);
            	}
            }
        });
		
		menuItemnonfixperiod.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	calStockNoneFixPeriodDpMXXWk ();
            }
        });

        menuItemliutong.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            	menuItemliutong.setText("X 按流通市值排名");
            	menuItemchengjiaoer.setText("按成交额排名");
            	menuItemtimerangezhangfu.setText("按阶段涨幅排名");
            	
            	((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).sortTableByLiuTongShiZhi();
            	((BanKuaiGeGuBasicTableModel)tableExternalInfo.getModel()).sortTableByLiuTongShiZhi();
            	((BanKuaiGeGuTableModel)tablexuandingzhou.getModel()).sortTableByLiuTongShiZhi();
            	((BanKuaiGeGuTableModel)tablexuandingminusone.getModel()).sortTableByLiuTongShiZhi();
            	((BanKuaiGeGuTableModel)tablexuandingminustwo.getModel()).sortTableByLiuTongShiZhi();
            	((BanKuaiGeGuTableModel)tablexuandingplusone.getModel()).sortTableByLiuTongShiZhi();
            }
        });
        menuItemchengjiaoer.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            	menuItemchengjiaoer.setText("X 按成交额排名");
            	menuItemliutong.setText("按流通市值排名");
            	menuItemtimerangezhangfu.setText("按阶段涨幅排名");
            	
            	((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).sortTableByChenJiaoEr();
            	((BanKuaiGeGuBasicTableModel)tableExternalInfo.getModel()).sortTableByChenJiaoEr();
        		((BanKuaiGeGuTableModel)tablexuandingzhou.getModel()).sortTableByChenJiaoEr();
        		((BanKuaiGeGuTableModel)tablexuandingminusone.getModel()).sortTableByChenJiaoEr();
        		((BanKuaiGeGuTableModel)tablexuandingminustwo.getModel()).sortTableByChenJiaoEr();
        		((BanKuaiGeGuTableModel)tablexuandingplusone.getModel()).sortTableByChenJiaoEr();
            }
        });
        menuItemtimerangezhangfu.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            	menuItemchengjiaoer.setText("按成交额排名");
            	menuItemliutong.setText("按流通市值排名");
            	
            	DateRangeSelectPnl datachoose = new DateRangeSelectPnl ( LocalDate.now().minusDays(1), LocalDate.now() ); 
        		JOptionPane.showMessageDialog(null, datachoose,"选择时间段", JOptionPane.OK_CANCEL_OPTION);
        		
        		LocalDate searchstart = datachoose.getDatachoosestart();
        		LocalDate searchend = datachoose.getDatachooseend();
        		
        		menuItemtimerangezhangfu.setText("X 按阶段涨幅排名" + searchstart.toString() + "~" + searchend.toString());
        		
        		int row = tableBkZhanBi.getSelectedRow();
    			int modelRow = tableBkZhanBi.convertRowIndexToModel(row);
    			BanKuai bk = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(modelRow);
    			NodeXPeriodData bknodexdata = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.DAY);
    			LocalDate curohlcstart = bknodexdata.getOHLCRecordsStartDate();
    			LocalDate curohlcend = bknodexdata.getOHLCRecordsEndDate();
    			
    			if(searchstart.isBefore(curohlcstart) || searchend.isAfter(curohlcend) ) {
    				JOptionPane.showMessageDialog(null,"选择时间段数据缺失，请先同步数据或者调整时间段。","Warning",JOptionPane.WARNING_MESSAGE);
    				
    				menuItemtimerangezhangfu.setText("按阶段涨幅排名");
    				
    				return ;
    			}
            	
            	((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).sortTableByTimeRangeZhangFu(searchstart,searchend,NodeGivenPeriodDataItem.DAY);
            	((BanKuaiGeGuBasicTableModel)tableExternalInfo.getModel()).sortTableByTimeRangeZhangFu(searchstart,searchend,NodeGivenPeriodDataItem.DAY);
        		((BanKuaiGeGuTableModel)tablexuandingzhou.getModel()).sortTableByTimeRangeZhangFu(searchstart,searchend,NodeGivenPeriodDataItem.DAY);
        		((BanKuaiGeGuTableModel)tablexuandingminusone.getModel()).sortTableByTimeRangeZhangFu(searchstart,searchend,NodeGivenPeriodDataItem.DAY);
        		((BanKuaiGeGuTableModel)tablexuandingminustwo.getModel()).sortTableByTimeRangeZhangFu(searchstart,searchend,NodeGivenPeriodDataItem.DAY);
        		((BanKuaiGeGuTableModel)tablexuandingplusone.getModel()).sortTableByTimeRangeZhangFu(searchstart,searchend,NodeGivenPeriodDataItem.DAY);
            }
        });
        

//        menuItemstocktocsv.addActionListener(new ActionListener() {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//            	 List<TDXNodes> stocklist = ((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel() ).getAllStocks();
//            	 exportTDXNodesDataToCsv (stocklist,"all");
//            	 stocklist = null;
//            }
//
//			
//        });
//        menuItembktocsv.addActionListener(new ActionListener() {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//            	List<TDXNodes> bklist = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel() ).getAllBanKuai();
//            	exportTDXNodesDataToCsv (bklist,"all");
//            	bklist = null;
//            }
//        });
//        menuItemyangxianbktocsv.addActionListener(new ActionListener() {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//            	List<TDXNodes> bklist = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel() ).getAllBanKuai();
//            	exportTDXNodesDataToCsv (bklist,"onlyyangxianbk");
//            	bklist = null;
//            }
//        });
        tabbedPanebk.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getButton() == MouseEvent.BUTTON1) {
                	String selecttitle = tabbedPanebk.getTitleAt( tabbedPanebk.getSelectedIndex() );
                	if(selecttitle.contains("选定周"))
                		tabbedPanegegu.setSelectedIndex(2);
                } else if (e.getButton() == MouseEvent.BUTTON3) {
//                	if(tabbedPanebk.getSelectedIndex() == 0)
//                		jPopupMenuoftabbedpanebk.show(tabbedPanebk, e.getX(),   e.getY());
                }
            }

        });

        
		tabbedPanegegu.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getButton() == MouseEvent.BUTTON1) {
//                    pane.setSelectedComponent(panel);
                } else if (e.getButton() == MouseEvent.BUTTON3) {
//                    JPopupMenu jPopupMenu = new JPopupMenu();
//                   
//                    jPopupMenu.add(menuItemzongshizhi);
//                    menuItemzongshizhi.setEnabled(false);
//                    jPopupMenu.add(menuItemliutong);
//                    jPopupMenu.add(menuItemchengjiaoer);
                    
                   
                	jPopupMenuoftabbedpane.show(tabbedPanegegu, e.getX(),   e.getY());
                }
            }

        });
		
//		btnshizhifx.addMouseListener(new MouseAdapter() {
//			
//
//			@Override
//			public void mouseClicked(MouseEvent arg0) {
//				startupGeGuShiZhiFenXi (true);
//				
//			}
//		});
		
//		menuItemRmvNodeFmFile.addActionListener(new ActionListener() {
//			@Override
//
//			public void actionPerformed(ActionEvent evt) {
////				int bkrow = tableBkZhanBi.getSelectedRow();
////				if(bkrow <0) {
////					JOptionPane.showMessageDialog(null,"请选择一个板块！","Warning",JOptionPane.WARNING_MESSAGE);
////					return;
////				}
//				
////				int bkmodelRow = tableBkZhanBi.convertRowIndexToModel(bkrow);
////				BanKuai selectedbk = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(bkmodelRow);
//				
////				int ggrow = tableGuGuZhanBiInBk.getSelectedRow();
////				if(ggrow <0) {
////					JOptionPane.showMessageDialog(null,"请选择一个股票！","Warning",JOptionPane.WARNING_MESSAGE);
////					return;
////				}
////				
////				int ggmodelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(ggrow);
////				StockOfBanKuai selectstock = ((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).getStock (ggmodelRow);
////
////				LocalDate curselectdate = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//			}
//			
//		});
		
		cyltreecopy.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
//            	chanYeLianTreeMousePressed(evt);
//            	logger.debug("get action notice at bkcyl");
    	        TreePath closestPath = cyltreecopy.getClosestPathForLocation(evt.getX(), evt.getY());

    	        if(closestPath != null) {
    	            Rectangle pathBounds = cyltreecopy.getPathBounds(closestPath);
    	            int maxY = (int) pathBounds.getMaxY();
    	            int minX = (int) pathBounds.getMinX();
    	            int maxX = (int) pathBounds.getMaxX();
    	            if (evt.getY() > maxY) 
    	            	cyltreecopy.clearSelection();
    	            else if (evt.getX() < minX || evt.getX() > maxX) 
    	            	cyltreecopy.clearSelection();
    	        }
    	        TreePath[] treePaths = cyltreecopy.getSelectionPaths();
    	    	BkChanYeLianTreeNode node = (BkChanYeLianTreeNode)cyltreecopy.getLastSelectedPathComponent();
    	    	if( node != null && node.getType() == BkChanYeLianTreeNode.TDXBK ) {
    	    		Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
        			setCursor(hourglassCursor);
        			
        			String nodecode = node.getMyOwnCode();
					int rowindex = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuaiRowIndex( nodecode );
					if(rowindex == -1) {
						JOptionPane.showMessageDialog(null,"板块不在分析表中，可到板块设置修改！","Warning", JOptionPane.WARNING_MESSAGE);
						hourglassCursor = null;
	    				Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
	        			setCursor(hourglassCursor2);
						return;
					}
        			
//    	    		BanKuai bankuai = (BanKuai)allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode(node.getMyOwnCode(), BkChanYeLianTreeNode.TDXBK);
					SvsForNodeOfBanKuai svsbk = new SvsForNodeOfBanKuai ();
					BanKuai bankuai = (BanKuai) svsbk.getNode(node.getMyOwnCode());
					svsbk = null;
    				combxsearchbk.updateUserSelectedNode(bankuai);
    				
//    				tabbedPanebk.setSelectedIndex(2);
    				
    				hourglassCursor = null;
    				Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
        			setCursor(hourglassCursor2);

    				SystemAudioPlayed.playSound();
    	    	}
    	        
            }
        });

		pnlbkwkcjlzhanbi.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) 
			{
				int rowbk = tableBkZhanBi.getSelectedRow();
				if(rowbk <0) 
					return;
				
				String nodecode = evt.getOldValue().toString();
				
				Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
				setCursor(hourglassCursor);

				int modelRow = tableBkZhanBi.convertRowIndexToModel(rowbk);
				BanKuai bkcur = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(modelRow);

                if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.SELECTED_PROPERTY)) {
                    @SuppressWarnings("unchecked")
                    String selectedinfo = evt.getNewValue().toString();
                    refreshAfterUserSelectBanKuaiColumn (bkcur,selectedinfo);
                    
                } else if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.MOUSEDOUBLECLICK_PROPERTY)) {
                	String datekey = evt.getNewValue().toString();
                	try{
                		displayNodeLargerPeriodData (bkcur,LocalDate.parse(datekey));
                	} catch (java.time.format.DateTimeParseException e) {
                		displayNodeLargerPeriodData (bkcur,null);
                	}
                }
                
                SystemAudioPlayed.playSound();
                
                hourglassCursor = null;
				Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
				setCursor(hourglassCursor2);

			}
		});
		
		panelbkwkcjezhanbi.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
            	int rowbk = tableBkZhanBi.getSelectedRow();
				if(rowbk <0) 
					return;
				
				Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
				setCursor(hourglassCursor);

				int modelRow = tableBkZhanBi.convertRowIndexToModel(rowbk);
				BanKuai bkcur = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(modelRow);

                if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.SELECTED_PROPERTY)) {
                    @SuppressWarnings("unchecked")
                    String selectedinfo = evt.getNewValue().toString();
                    refreshAfterUserSelectBanKuaiColumn (bkcur,selectedinfo);
                    
                } else if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.MOUSEDOUBLECLICK_PROPERTY)) {
                	String datekey = evt.getNewValue().toString();
                	try{
                		displayNodeLargerPeriodData (bkcur,LocalDate.parse(datekey));
                	} catch (java.time.format.DateTimeParseException e) {
                		displayNodeLargerPeriodData (bkcur,null);
                	}
                }
                
//                SystemAudioPlayed.playSound();
                
                hourglassCursor = null;
				Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
				setCursor(hourglassCursor2);
            }
        });
		
		panelggdpcjlwkzhanbi.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				
//				StockOfBanKuai selectstock = null;
//				int stockrow = tableGuGuZhanBiInBk.getSelectedRow();
//				if(stockrow != -1) {
//					int modelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(stockrow);
//					selectstock = ((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).getStock(modelRow);
//				}
				
				Stock selectstock = (Stock) panelggdpcjlwkzhanbi.getCurDisplayedNode ();

                if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.SELECTED_PROPERTY)) {
                    @SuppressWarnings("unchecked")
                    String selectedinfo = evt.getNewValue().toString();
                    
//                    org.jsoup.nodes.Document doc = Jsoup.parse(selectedinfo);
//            		org.jsoup.select.Elements body = doc.select("body");
//            		org.jsoup.select.Elements dl = body.select("dl");
//            		org.jsoup.select.Elements li = dl.get(0).select("li");
//            		String selecteddate = li.get(0).text();
            		LocalDate datekey = LocalDate.parse(selectedinfo);
    				chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(datekey));
    				
    				setUserSelectedColumnMessage(selectstock, datekey);
    				
//    				if(cbxshizhifx.isSelected()) { //显示市值排名
//						dispalyStockShiZhiFengXiResult (selectstock,datekey);
//					};
					
//					SystemAudioPlayed.playSound();
                } else if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.MOUSEDOUBLECLICK_PROPERTY)) {
                	String datekey = evt.getNewValue().toString();
       	        	try{
                		displayNodeLargerPeriodData (selectstock,LocalDate.parse(datekey));
                	} catch (java.time.format.DateTimeParseException e) {
                		displayNodeLargerPeriodData (selectstock,null);
                	}
                }
			}
		});
		
		panelGgDpCjeZhanBi.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
				
				Stock selectstock = (Stock) panelGgDpCjeZhanBi.getCurDisplayedNode ();

                if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.SELECTED_PROPERTY)) {
                    @SuppressWarnings("unchecked")
                    String selectedinfo = evt.getNewValue().toString();
                    

            		LocalDate datekey = LocalDate.parse(selectedinfo);
    				chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(datekey));
    				
    				setUserSelectedColumnMessage(selectstock, datekey);
    				
    				String reminder = "提示: \n"
    						+ "1. 个股的占比和成交量/成交额是否背离？ \n"
    						+ "2. K线和大盘关键日期的对应走势。"
    						;
    				showReminderMessage (bkfxremind.getStockcolumnremind() );
    				
//    				if(cbxshizhifx.isSelected()) { //显示市值排名
//						dispalyStockShiZhiFengXiResult (selectstock,datekey);
//					};
					
                } else if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.MOUSEDOUBLECLICK_PROPERTY)) {
                	String datekey = evt.getNewValue().toString();
       	        	try{
                		displayNodeLargerPeriodData (selectstock,LocalDate.parse(datekey));
                	} catch (java.time.format.DateTimeParseException e) {
                		displayNodeLargerPeriodData (selectstock,null);
                	}
                }
            }
        });
		
		//添加导出条件
		btnaddexportcond.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				if(SwingUtilities.isLeftMouseButton(arg0) ) {
					
//					if(!ckboxshowcje.isSelected() && !ckbxdpmaxwk.isSelected() && !chkliutongsz.isSelected() && !ckbxcjemaxwk.isSelected()){
//						JOptionPane.showMessageDialog(null,"未设置导出条件，请先设置导出条件！");
//						return;
//					} 
					
					initializeExportConditions ();
					
				} else if (SwingUtilities.isRightMouseButton(arg0)) {
					if( exportcond != null) { 
						exportcond.clear();
						
						btnaddexportcond.setText(String.valueOf(0));
						btnaddexportcond.setToolTipText("<html>导出条件设置(鼠标右键删除设置)<br></html>");
					}
					
				} else if (SwingUtilities.isMiddleMouseButton(arg0)) {
					
				}
		
			}
		});
		
		//突出显示个股WK部分
		tfldltszmin.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				if( Strings.isNullOrEmpty(tfldltszmin.getText() )) {
					JOptionPane.showMessageDialog(null,"请设置突出显示板块MaxWk！");
					ckbxdpmaxwk.setSelected(false);
					return;
				}
				if(chkliutongsz.isSelected() ) {
					refreshBanKuaiGeGuTableHightLight ();
				}
			}
		});
		
		//突出显示成交额大于设置值
		ckbxcjemaxwk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if( Strings.isNullOrEmpty(tfldcjemaxwk.getText() )) {
					JOptionPane.showMessageDialog(null,"请设置突出显示板块MaxWk！");
					ckbxcjemaxwk.setSelected(false);
					return;
				}
				refreshBanKuaiGeGuTableHightLight ();
				
			}
		});
		ckbxhuanshoulv.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if( Strings.isNullOrEmpty(tfldhuanshoulv.getText() )) {
					JOptionPane.showMessageDialog(null,"请设置突出显示换手率！");
					ckbxhuanshoulv.setSelected(false);
					return;
				}
				refreshBanKuaiGeGuTableHightLight ();
			}
		});
		chckbxdpminwk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if( Strings.isNullOrEmpty(tflddpminwk.getText())  ) {
					JOptionPane.showMessageDialog(null,"请设置DPMINWK！");
					chckbxdpminwk.setSelected(false);
					return;
				}
				refreshBanKuaiGeGuTableHightLight ();
			}
		});
		chkliutongsz.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if( Strings.isNullOrEmpty(tfldltszmin.getText()) && Strings.isNullOrEmpty(tfldltszmax.getText()) ) {
					JOptionPane.showMessageDialog(null,"请设置流通市值区间！");
					chkliutongsz.setSelected(false);
					return;
				}
				refreshBanKuaiGeGuTableHightLight ();
			}
		});
		//突出显示大盘WK部分
		tflddisplaydpmaxwk.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				if( Strings.isNullOrEmpty(tflddisplaydpmaxwk.getText() )) {
					JOptionPane.showMessageDialog(null,"请设置突出显示板块MaxWk！");
					ckbxdpmaxwk.setSelected(false);
					return;
				}
				refreshBanKuaiGeGuTableHightLight ();
				
			}
		});
		ckbxdpmaxwk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if( Strings.isNullOrEmpty(tflddisplaydpmaxwk.getText() )) {
					JOptionPane.showMessageDialog(null,"请设置突出显示MaxWk！");
					ckbxdpmaxwk.setSelected(false);
					return;
				}
				refreshBanKuaiGeGuTableHightLight ();
			}
		});
		//paresefile
		ckboxparsefile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				
			
				if(ckboxparsefile.isSelected()) {
					if( Strings.isNullOrEmpty(tfldparsedfile.getText() )) {
						Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
						setCursor(hourglassCursor);
						
						chooseParsedFile (null);
						
						tableBkZhanBi.repaint();
						
						hourglassCursor = null;
						Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
						setCursor(hourglassCursor2);
					}
				} else
					tfldparsedfile.setText("");
			}
		});
		
		tfldshowcje.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				refreshBanKuaiGeGuTableHightLight ();
			}
		});
		chbxzhangfu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if( Strings.isNullOrEmpty(tfldzhangfumin.getText()) &&  Strings.isNullOrEmpty(tfldzhangfumax.getText()) ) {
					JOptionPane.showMessageDialog(null,"请设置突出显示的涨幅！");
					chbxzhangfu.setSelected(false);
					return;
				}
				refreshBanKuaiGeGuTableHightLight ();
			}
		});
		
		ckbxma.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if( Strings.isNullOrEmpty(tfldweight.getText()) &&  Strings.isNullOrEmpty(tfldweight.getText()) ) {
					JOptionPane.showMessageDialog(null,"请设置突出显示的均线值！");
					ckbxma.setSelected(false);
					return;
				}
				refreshBanKuaiGeGuTableHightLight ();
			}
		});
		ckboxshowcje.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				if( Strings.isNullOrEmpty(tfldshowcje.getText()) &&  Strings.isNullOrEmpty(tfldshowcjemax.getText()) ) {
					JOptionPane.showMessageDialog(null,"请设置突出显示的成交额！");
					ckboxshowcje.setSelected(false);
					return;
				}
				refreshBanKuaiGeGuTableHightLight ();
			}
		});
		/*
		 * 查找pie chart内点击的那个个股 
		 */
		pnlcurwkggcjezhanbi.addPropertyChangeListener(new PropertyChangeListener() 
		{

	            public void propertyChange(PropertyChangeEvent evt) {

	                if (evt.getPropertyName().equals(BanKuaiFengXiPieChartPnl.SELECTED_PROPERTY)) {
	                	String curstockcode = evt.getNewValue().toString();
	                	findInputedNodeInTable (curstockcode);
	                	
	                	StockOfBanKuai sob = ((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel() ).getStock(curstockcode.substring(0, 6));
	                	hightlightSpecificSector (sob);
	                }
	            }
		});
		panelLastWkGeGucjeZhanBi.addPropertyChangeListener(new PropertyChangeListener() 
		{

            public void propertyChange(PropertyChangeEvent evt) {

                if (evt.getPropertyName().equals(BanKuaiFengXiPieChartPnl.SELECTED_PROPERTY)) {
                	String curstockcode = evt.getNewValue().toString();
                	findInputedNodeInTable (curstockcode);
                	
                	StockOfBanKuai sob = ((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel() ).getStock(curstockcode.substring(0, 6));
                	hightlightSpecificSector (sob);
                }
                
                
            }
		});


		combxsearchbk.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) 
			{
				if(arg0.getStateChange() == ItemEvent.SELECTED) {
					updatedComboxAndFengXiResult ();
					
				}
			}
			
		});
		
		combxstockcode.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) 
			{
				if(arg0.getStateChange() == ItemEvent.SELECTED) {
					BkChanYeLianTreeNode userinputnode = combxstockcode.getUserInputNode();
					String nodecode;
					if(userinputnode == null) { 
						nodecode = combxstockcode.getSelectedItem().toString();
						JOptionPane.showMessageDialog(null,"不是个股代码，重新输入！","Warning", JOptionPane.WARNING_MESSAGE);
						return;
					} else {
//						if(userinputnode.getType() == BkChanYeLianTreeNode.TDXBK ) { //如果用户输入查出的是板块，
//							JOptionPane.showMessageDialog(null,"不是个股代码，重新输入！","Warning", JOptionPane.WARNING_MESSAGE);
//							return;
//						}
							
						nodecode = userinputnode.getMyOwnCode();
						try{ //如果用户选择的和上次选择的个股一样，不重复做板块查找
//							String stockcodeincbx = ((BkChanYeLianTreeNode)combxstockcode.getSelectedItem()).getMyOwnCode();
							displayStockSuoShuBanKuai((Stock)userinputnode);
							
						} catch (java.lang.NullPointerException e) {
							e.printStackTrace();
//							cbxstockcode.updateUserSelectedNode (selectstock.getStock());
						} catch (java.lang.StringIndexOutOfBoundsException e) {
							logger.debug((String)combxstockcode.getSelectedItem());
						}
//						displayStockSuoShuBanKuai((Stock)userinputnode);
					}
					
//					int rowindex = tableGuGuZhanBiInBk.getSelectedRow();
//					int modelRow = tableGuGuZhanBiInBk.convertRowIndexToView(rowindex);
					if(findInputedNodeInTable (nodecode)) { 
						panelGgDpCjeZhanBi.resetDate();
						paneldayCandle.resetDate();
					}
					
//					if(!findInputedNodeInTable (nodecode)) { //如果没有找到
//						tableGuGuZhanBiInBk.setRowSelectionInterval(rowindex,rowindex);
//						tableGuGuZhanBiInBk.getSelectionModel().clearSelection() ; //把当前在table中选择取消，以免用户觉得已经找到
						//既然取消了table中的个股，就需要把几个图表的信息清楚，否则用户再去双击这些图表，会出现问题。
						
//						panelGgDpCjeZhanBi.resetDate();
//						paneldayCandle.resetDate();
//					} else { //在当前表就有的话，就把相关PANEL清空
//						panelGgDpCjeZhanBi.resetDate();
//						paneldayCandle.resetDate();
//					}
						
			}
				
				if(arg0.getStateChange() == ItemEvent.DESELECTED) {
				}
			}
		});
		
//		btnsixmonthbefore.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseClicked(MouseEvent e) {
//				LocalDate startday = dateChooser.getLocalDate();
//				LocalDate requirestart = startday.with(DayOfWeek.MONDAY).minus(1,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
//				dateChooser.setDate(Date.from(requirestart.atStartOfDay(ZoneId.systemDefault()).toInstant() ) );
//				
//				
//	    		lastselecteddate = requirestart;
//	    		
//	    		btnsixmonthafter.setEnabled(true);
//	    		btnresetdate.setEnabled(true);
//			}
//		});
//		btnsixmonthafter.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseClicked(MouseEvent e) {
//				if(!btnsixmonthafter.isEnabled())
//					return ;
//				
//				LocalDate startday = dateChooser.getLocalDate();//dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//				LocalDate requirestart = startday.with(DayOfWeek.SATURDAY).plus(1,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
//				dateChooser.setDate(Date.from(requirestart.atStartOfDay(ZoneId.systemDefault()).toInstant() ) );
//    		
//	    		lastselecteddate = startday;
//	    		
//	    		btnresetdate.setEnabled(true);
//
//			}
//		});

		dateChooser.addPropertyChangeListener(new PropertyChangeListener() {
		    @Override
		    public void propertyChange(PropertyChangeEvent e) {
		    	if("date".equals(e.getPropertyName() ) ) {
		    		
		    		JDateChooser wybieraczDat = (JDateChooser) e.getSource();
		    		LocalDate newdate = wybieraczDat.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		    		
		    		if( (lastselecteddate == null) || ( !newdate.isEqual( lastselecteddate) ) ) {
		    			Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
						setCursor(hourglassCursor);
						
						clearTheGuiBeforDisplayNewInfoSection1 ();
						clearTheGuiBeforDisplayNewInfoSection2 ();
						clearTheGuiBeforDisplayNewInfoSection3 ();
						
						cyltreecopy.setCurrentDisplayedWk (newdate);
			    		DefaultTreeModel treeModel = (DefaultTreeModel) cyltreecopy.getModel();
			    		treeModel.reload();
			    		
			    		gettBanKuaiZhanBiRangedByGrowthRateOfPeriod (NodeGivenPeriodDataItem.WEEK);
			    		
			    		lastselecteddate = newdate;
			    		
			    		hourglassCursor = null;
			    		Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
						setCursor(hourglassCursor2);
		    		}
		    	}

		    }
		});
		/*
		 * 
		 */
//		editorPanebankuai.addPropertyChangeListener(new PropertyChangeListener() {
//
//            public void propertyChange(PropertyChangeEvent evt) {
//            	if (evt.getPropertyName().equals(BanKuaiListEditorPane.URLSELECTED_PROPERTY)) {
//            		
//            		String selbk = evt.getNewValue().toString();
//    				String selbkcode;
//    				if(selbk != null)
//    					 selbkcode = selbk.trim().substring(1, 7);
//    				else 
//    					return;
//
//    				int rowindex = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuaiRowIndex(selbkcode);
//    				if(rowindex != -1) {
//    					
//    					Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
//    					setCursor(hourglassCursor);
//    					
//    					int modelRow = tableBkZhanBi.convertRowIndexToView(rowindex);
//    					tableBkZhanBi.setRowSelectionInterval(modelRow, modelRow);
//    					tableBkZhanBi.scrollRectToVisible(new Rectangle(tableBkZhanBi.getCellRect(modelRow, 0, true)));
//    					
//    					//找到用户选择的板块
//    					BanKuai selectedbk = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(rowindex);
//    					unifiedOperationsAfterUserSelectABanKuai (selectedbk);
//    					
//    					//找不到该股票
//    					String stockcode = combxstockcode.getSelectedItem().toString().substring(0, 6);
//    					if(!findInputedNodeInTable (stockcode) )
//    						JOptionPane.showMessageDialog(null,"在某个个股表内没有发现该股，可能在这个时间段内该股停牌","Warning",JOptionPane.WARNING_MESSAGE);
//    					
////    					Toolkit.getDefaultToolkit().beep();
//    					hourglassCursor = null;
//    					Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
//    					setCursor(hourglassCursor2);
//    					
//    					SystemAudioPlayed.playSound();
//    				} else {
//    					JOptionPane.showMessageDialog(null,"该板块设置为不显示在板块列表中，请在 ‘重点关注与产业链’ 变更设置！","Warning",JOptionPane.WARNING_MESSAGE);
//    				}
//            	}
//            }
//        });
		
		tableGuGuZhanBiInBk.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) 
			{
			}
		});
		/*
		 * 
		 */
		tableGuGuZhanBiInBk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				int row = tableGuGuZhanBiInBk.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择一个股票！","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				int modelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(row);
				StockOfBanKuai selectstock = ((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).getStock (modelRow);
				
				if (arg0.getClickCount() == 1) { //用户点击一下
					unifiedOperationsAfterUserSelectAStock (selectstock);
				}
				if (arg0.getClickCount() == 2) {
				}
			}
		});
		
		tableTempGeGu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				int row = tableTempGeGu.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择一个股票！","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
//				clearTheGuiBeforDisplayNewInfoSection1 ();
				clearTheGuiBeforDisplayNewInfoSection2 ();
				clearTheGuiBeforDisplayNewInfoSection3 ();
				
				int modelRow = tableTempGeGu.convertRowIndexToModel(row);
				StockOfBanKuai selectstock = ((BanKuaiGeGuTableModel)tableTempGeGu.getModel()).getStock (modelRow);
				
				if (arg0.getClickCount() == 1) { //用户点击一下
					unifiedOperationsAfterUserSelectAStock (selectstock);
				}
				if (arg0.getClickCount() == 2) {
				}
				
				tabbedPanegegu.setSelectedIndex(6);
			}
		});
		
		tablexuandingzhou.addMouseListener(new MouseAdapter() 
		{
			public void mouseClicked(MouseEvent arg0) {
				int row = tablexuandingzhou.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择一个股票！","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				int modelRow = tablexuandingzhou.convertRowIndexToModel(row);
				StockOfBanKuai selectstock = ((BanKuaiGeGuTableModel)tablexuandingzhou.getModel()).getStock (modelRow);
				
				if (arg0.getClickCount() == 1) {
					unifiedOperationsAfterUserSelectAStock (selectstock);
				}
				if (arg0.getClickCount() == 2) {
				}
			}
		});
		tablexuandingminustwo.addMouseListener(new MouseAdapter() 
		{
			public void mouseClicked(MouseEvent arg0) {
				int row = tablexuandingminustwo.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择一个股票！","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				int modelRow = tablexuandingminustwo.convertRowIndexToModel(row);
				StockOfBanKuai selectstock = ((BanKuaiGeGuTableModel)tablexuandingminustwo.getModel()).getStock (modelRow);
	
				if (arg0.getClickCount() == 1) {
					unifiedOperationsAfterUserSelectAStock (selectstock);
				}
				 if (arg0.getClickCount() == 2) {
				 }
			}
		}); 
		tablexuandingminusone.addMouseListener(new MouseAdapter() 
		{
			public void mouseClicked(MouseEvent arg0) {
				int row = tablexuandingminusone.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择一个股票！","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				int modelRow = tablexuandingminusone.convertRowIndexToModel(row);
				StockOfBanKuai selectstock = ((BanKuaiGeGuTableModel)tablexuandingminusone.getModel()).getStock (modelRow);
	
				if (arg0.getClickCount() == 1) {
					unifiedOperationsAfterUserSelectAStock (selectstock);
				}
				 if (arg0.getClickCount() == 2) {
				 }
			}
		});
		tablexuandingplusone.addMouseListener(new MouseAdapter() 
		{
			public void mouseClicked(MouseEvent arg0) {
				int row = tablexuandingplusone.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择一个股票！","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				int modelRow = tablexuandingplusone.convertRowIndexToModel(row);
				StockOfBanKuai selectstock = ((BanKuaiGeGuTableModel)tablexuandingplusone.getModel()).getStock (modelRow);
	
				if (arg0.getClickCount() == 1) {
					unifiedOperationsAfterUserSelectAStock (selectstock);
				}
				 if (arg0.getClickCount() == 2) {
				 }
			}
		});
		tableExternalInfo.addMouseListener(new MouseAdapter() 
		{
			public void mouseClicked(MouseEvent arg0) {
				int row = tableExternalInfo.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择一个股票！","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				int modelRow = tableExternalInfo.convertRowIndexToModel(row);
				StockOfBanKuai selectstock = ((BanKuaiGeGuBasicTableModel)tableExternalInfo.getModel()).getStock (modelRow);
	
				if (arg0.getClickCount() == 1) {
					unifiedOperationsAfterUserSelectAStock (selectstock);
				}
				 if (arg0.getClickCount() == 2) {
				 }
			}
		});
		
		
		tableBkZhanBi.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				int row = tableBkZhanBi.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择一个板块！","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
				setCursor(hourglassCursor);
				
				int modelRow = tableBkZhanBi.convertRowIndexToModel(row);
				BanKuai selectedbk = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(modelRow);
				unifiedOperationsAfterUserSelectABanKuai (selectedbk);
				
				hourglassCursor = null;
				Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
				setCursor(hourglassCursor2);
//				Toolkit.getDefaultToolkit().beep();
				SystemAudioPlayed.playSound();

			}
		});
	}
	/*
	 * 
	 */
	private void parseTempGeGuFromZhiDingBanKuaiFunciotn ()
	{
		String result = JOptionPane.showInputDialog("请输入板块代码");
		if(result != null) {
			BanKuai bk = (BanKuai) treeofbkstk.getSpecificNodeByHypyOrCode(result.trim(), BkChanYeLianTreeNode.TDXBK);
			if(bk != null) {
				SvsForNodeOfBanKuai svsbk = new SvsForNodeOfBanKuai ();
				bk = svsbk.getAllGeGuOfBanKuai (bk); 
				List<BkChanYeLianTreeNode> allbkgg = bk.getAllGeGuOfBanKuaiInHistory();
				List<String> listNames = allbkgg.stream().map(u -> u.getMyOwnCode()).collect(Collectors.toList());
				parseTempGeGeFromList (listNames);
			}
		}
	}
	/*
	 * 
	 */
	protected void parseTempGeGuFromTDXSwFunciotns()
	{
		TDXZhiDingYiBanKuaiServices svstdxzdy = new TDXZhiDingYiBanKuaiServices ();
		PnlZhiDingYiBanKuai pnltdxzdy = new PnlZhiDingYiBanKuai (svstdxzdy);
		
		int action = JOptionPane.showConfirmDialog(null, pnltdxzdy, "导入通达信个股", JOptionPane.OK_CANCEL_OPTION);
		if(0 != action) 
			return;
		
		List<String> result = pnltdxzdy.getSelectedZdyBkGeGu ();
		parseTempGeGeFromList (result);
		
//		tabbedPanegegu.setToolTipTextAt(6,filename);
	}
	/*
	 * 
	 */
	private void parseTempGeGeFromList (List<String> readparsefileLines )
	{
		Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
		setCursor(hourglassCursor);
		
		LocalDate curselectdate = null;
		try{
			curselectdate = dateChooser.getLocalDate();// dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		} catch (java.lang.NullPointerException e) {
			JOptionPane.showMessageDialog(null,"日期有误!","Warning",JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		BanKuai tempbankuai = new BanKuai ("TEMPBANKUAI","TEMPBANKUAI");
		SvsForNodeOfStock svstock = new SvsForNodeOfStock ();
		for(String tmpgegu : readparsefileLines) {
			Stock tmpstock = (Stock) treeofbkstk.getSpecificNodeByHypyOrCode(tmpgegu, BkChanYeLianTreeNode.TDXGG);
			try {
				tmpstock = (Stock) svstock.getNodeData( tmpstock , CommonUtility.getSettingRangeDate(curselectdate,"middle"), curselectdate,
						globeperiod,this.globecalwholeweek);
				svstock.syncNodeData(tmpstock);
			} catch (java.lang.NullPointerException e) {
				logger.info(tmpgegu + "数据有误！");
				e.printStackTrace();
			}

			StockOfBanKuai bkofst = new StockOfBanKuai(tempbankuai,tmpstock);
			LocalDate joindate = LocalDate.parse("1997-01-01");
			LocalDate leftdate = LocalDate.parse("3000-01-01");
			DateTime joindt= new DateTime(joindate.getYear(), joindate.getMonthValue(), joindate.getDayOfMonth(), 0, 0, 0, 0);
			DateTime leftdt = new DateTime(leftdate.getYear(), leftdate.getMonthValue(), leftdate.getDayOfMonth(), 0, 0, 0, 0);
			Interval joinleftinterval = new Interval(joindt, leftdt);
			bkofst.addInAndOutBanKuaiInterval(joinleftinterval);
			
			tempbankuai.addNewBanKuaiGeGu(bkofst);
		}
		
		setExportMainConditionBasedOnUserSelection (bkggmatchcondition);
		((BanKuaiGeGuTableModel)tableTempGeGu.getModel()).refresh(tempbankuai, curselectdate,globeperiod);
		
		svstock = null;
		
		hourglassCursor = null;
		Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
		setCursor(hourglassCursor2);

		SystemAudioPlayed.playSound();
	}
	
	
	/*
	 * 
	 */
	protected void parseTempGeGuFromFileFunciotns()
	{
		String parsedpath = sysconfig.getTDXModelMatchExportFile ();
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		chooser.setCurrentDirectory(new File(parsedpath) );
		
		String filename;
		if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			if(chooser.getSelectedFile().isDirectory())
		    	filename = (chooser.getSelectedFile()+ "\\").replace('\\', '/');
		    else
		    	filename = (chooser.getSelectedFile()).toString().replace('\\', '/');
		} else
			return;
		
		File fileebk = null;
		if(filename.endsWith("EBK")) {
			fileebk = new File( filename );
			try {
					if (!fileebk.exists()) 
						return ;
			} catch (Exception e) {
					e.printStackTrace();
					return ;
			}
		}
		
		List<String> readparsefileLines = null;
		try { //读出个股
			readparsefileLines = Files.readLines(fileebk,Charsets.UTF_8,new ParseBanKuaiWeeklyFielGetStocksProcessor ());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		parseTempGeGeFromList (readparsefileLines);
//		LocalDate curselectdate = null;
//		try{
//			curselectdate = dateChooser.getLocalDate();// dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//		} catch (java.lang.NullPointerException e) {
//			JOptionPane.showMessageDialog(null,"日期有误!","Warning",JOptionPane.WARNING_MESSAGE);
//			return;
//		}
//		
//		BanKuai tempbankuai = new BanKuai ("TEMPBANKUAI","TEMPBANKUAI");
//		SvsForNodeOfStock svstock = new SvsForNodeOfStock ();
//		for(String tmpgegu : readparsefileLines) {
//			Stock tmpstock = (Stock) treeofbkstk.getSpecificNodeByHypyOrCode(tmpgegu, BkChanYeLianTreeNode.TDXGG);
//			try {
//				tmpstock = (Stock) svstock.getNodeData( tmpstock , CommonUtility.getSettingRangeDate(curselectdate,"middle"), curselectdate,
//						globeperiod,this.globecalwholeweek);
//				svstock.syncNodeData(tmpstock);
//			} catch (java.lang.NullPointerException e) {
//				logger.info(tmpgegu + "数据有误！");
//				e.printStackTrace();
//			}
//
//			StockOfBanKuai bkofst = new StockOfBanKuai(tempbankuai,tmpstock);
//			LocalDate joindate = LocalDate.parse("1997-01-01");
//			LocalDate leftdate = LocalDate.parse("3000-01-01");
//			DateTime joindt= new DateTime(joindate.getYear(), joindate.getMonthValue(), joindate.getDayOfMonth(), 0, 0, 0, 0);
//			DateTime leftdt = new DateTime(leftdate.getYear(), leftdate.getMonthValue(), leftdate.getDayOfMonth(), 0, 0, 0, 0);
//			Interval joinleftinterval = new Interval(joindt, leftdt);
//			bkofst.addInAndOutBanKuaiInterval(joinleftinterval);
//			
//			tempbankuai.addNewBanKuaiGeGu(bkofst);
//		}
//		
//		setExportMainConditionBasedOnUserSelection (bkggmatchcondition);
//		((BanKuaiGeGuTableModel)tableTempGeGu.getModel()).refresh(tempbankuai, curselectdate,globeperiod);
		
		tabbedPanegegu.setToolTipTextAt(6,filename);
		
//		svstock = null;
	}
	/*
	 * 
	 */
	protected void updatedComboxAndFengXiResult() 
	{
		BkChanYeLianTreeNode userinputnode = combxsearchbk.getUserInputNode();
		if(userinputnode == null ) { //如果用户输入查出的是板块，
			JOptionPane.showMessageDialog(null,"不是板块代码，重新输入！","Warning", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		String nodecode = userinputnode.getMyOwnCode();
		Integer rowindex = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuaiRowIndex( nodecode );
		
		if(rowindex == null || rowindex == -1) {
			JOptionPane.showMessageDialog(null,"股票/板块代码有误！或板块不在分析表中，可到板块设置修改！","Warning", JOptionPane.WARNING_MESSAGE);
			return;
		}

		int curselectrow = tableBkZhanBi.getSelectedRow();
		int modelRow = tableBkZhanBi.convertRowIndexToView(rowindex);
		if(modelRow == curselectrow)
		    	return;		    
		tableBkZhanBi.setRowSelectionInterval(modelRow, modelRow);
		tableBkZhanBi.scrollRectToVisible(new Rectangle(tableBkZhanBi.getCellRect(modelRow, 0, true)));
		BanKuai selectedbk = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(rowindex);
				
		unifiedOperationsAfterUserSelectABanKuai (selectedbk);
	}
	
	/*
	 * 对不确定周期计算占比，以周占比作为比较对象
	 */
	protected void calStockNoneFixPeriodDpMXXWk() 
	{
		DateRangeSelectPnl datachoose = new DateRangeSelectPnl (1); 
		JOptionPane.showMessageDialog(null, datachoose,"选择时间段", JOptionPane.OK_CANCEL_OPTION);
		
		LocalDate searchstart = datachoose.getDatachoosestart();
		LocalDate searchend = datachoose.getDatachooseend();
		
		int ggrow = tableGuGuZhanBiInBk.getSelectedRow();
		if(ggrow <0) {
			JOptionPane.showMessageDialog(null,"请选择一个股票！","Warning",JOptionPane.WARNING_MESSAGE);
			return;
		}
		int ggmodelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(ggrow);
		StockOfBanKuai selectstock = ((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).getStock (ggmodelRow);

		SvsForNodeOfStock svsstk = new SvsForNodeOfStock	();
		Number[] result = svsstk.getTDXNodeNoneFixPeriodDpMinMaxWk(selectstock.getStock(), searchstart, searchend);
		svsstk = null;
		Double zhanbi = (Double) result[0];
		Integer dpzbresult = (Integer) result[1];
		
		String htmlstring = "";
		org.jsoup.nodes.Document doc = Jsoup.parse(htmlstring);
		Elements body = doc.getElementsByTag("body");
		for(Element elbody : body) {
//			org.jsoup.nodes.Element htmldiv = elbody.appendElement("div");
//			org.jsoup.select.Elements divnodecodename = content.select("nodecode");
//    		org.jsoup.select.Elements nodetype = content.select("nodetype");
//			htmldiv.appendChild( selectstock.getStock().getMyOwnName() );
//			htmldiv.appendChild( nodetype.get(0) );
			
			 org.jsoup.nodes.Element dl = elbody.appendElement("dl");
			 
			 org.jsoup.nodes.Element li3 = dl.appendElement("li");
			 li3.appendText(searchstart.toString());
			 
			 org.jsoup.nodes.Element li1 = dl.appendElement("li");
			 org.jsoup.nodes.Element li4 = dl.appendElement("li");
			 if(dpzbresult == null) 
				 li1.appendText("占比MinWk=数据不完整无法计算" );
			 else if(dpzbresult<0 ) {
				 li1.appendText("占比MaxWk=0" );
				 li4.appendText("占比MinWk=" + dpzbresult);
			 } else if(dpzbresult>0) {
				 li1.appendText("占比MaxWk=" + dpzbresult);
				 li4.appendText("占比MinWk=0" );
			 }
			 
			 DecimalFormat decimalformate = new DecimalFormat("%#0.00000");
			 org.jsoup.nodes.Element li2 = dl.appendElement("li");
			 li2.appendText("占比" + decimalformate.format(zhanbi) );
		}
		
		setUserSelectedColumnMessage (doc.toString());
	}
	/*
	 * 导出用户选择的node的单独信息到CSV
	 */
//	protected void exportUserSelectedNodeInfoToCsv(Multimap<String, LocalDate> nodespecificdatainfo)
//	{
//		for (Map.Entry<TDXNodes, LocalDate> entry : nodespecificdatainfo.entrySet()) {
//		    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
//		    entry.
//		    
//		}
		
//		org.jsoup.nodes.Document doc = Jsoup.parse(nodespecificdatainfo);
//		org.jsoup.select.Elements divs = doc.select("div");
//		for(org.jsoup.nodes.Element div : divs) {
//			org.jsoup.select.Elements nodecodes = div.select("nodecode");
//			try{
//    			String nodecodename = nodecodes.get(0).text();
//    			String nodecode = nodecodename.substring(0,6);
//    			
//    			org.jsoup.select.Elements nodetype = div.select("nodetype");
//    			
//    			org.jsoup.select.Elements lis = div.select("li");
//    			String data = lis.get(0).text();
//    			if(Integer.parseInt(nodetype.text() ) == BkChanYeLianTreeNode.TDXGG) 
//    					exportAllStockToCsv(nodecode+data);
//    			else
//    				exportAllBanKuaiToCsv (nodecode+data);
//			} catch (java.lang.IndexOutOfBoundsException e) {
//				
//			}
//		}
//	}
	/**
	 * 
	 * @param nodelist
	 * @param exporttype
	 */
	private void exportTDXNodesDataToCsv (List<TDXNodes> nodelist, String exporttypeordate)
	{
		btnexportcsv.setEnabled(true);
		
		LocalDate curselectdate = dateChooser.getLocalDate();//dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		
		for(TDXNodes tmpnode : nodelist) {

			if(exporttypeordate.toLowerCase().equals("all")) { //list里面没一个当前日期数据
				nodeinfotocsv.addNodeToCsvList(tmpnode, curselectdate, curselectdate);
			} else
			if(exporttypeordate.toLowerCase().equals("single") ) {
				DateRangeSelectPnl datachoose = new DateRangeSelectPnl (52); 
				JOptionPane.showMessageDialog(null, datachoose,"为" + tmpnode.getMyOwnName() + "选择导出时间段", JOptionPane.OK_CANCEL_OPTION);
				LocalDate nodestart = datachoose.getDatachoosestart();
				LocalDate nodeend = datachoose.getDatachooseend();
				
				
				if(tmpnode.getType() == BkChanYeLianTreeNode.TDXBK) {
					SvsForNodeOfBanKuai svsbk = new SvsForNodeOfBanKuai ();
					tmpnode = (TDXNodes) svsbk.getNodeData(tmpnode, nodestart, nodeend, this.globeperiod, this.globecalwholeweek);
					svsbk = null;
				} else
				if(tmpnode.getType() == BkChanYeLianTreeNode.TDXGG) {
					SvsForNodeOfStock svsstk = new SvsForNodeOfStock	();
					tmpnode = (TDXNodes) svsstk.getNodeData(tmpnode, nodestart, nodeend, this.globeperiod, this.globecalwholeweek);
					svsstk = null;
				}
				SvsForNodeOfDaPan	svsdp = new SvsForNodeOfDaPan ();
				svsdp.getNodeData("999999", nodestart, nodeend, this.globeperiod, this.globecalwholeweek);
				svsdp = null;
//				allbksks.getDaPan(nodestart, nodeend, globeperiod, globecalwholeweek);
				
				nodeinfotocsv.addNodeToCsvList(tmpnode, nodestart, nodeend);
				
				nodestart = null;
				nodeend = null;
				datachoose = null;
			} else { //用户点击的信息导出
				nodeinfotocsv.addNodeToCsvList(tmpnode, LocalDate.parse(exporttypeordate), LocalDate.parse(exporttypeordate) );
			}
		}
	}
	
	/*
	 * 
	 */
	protected void startupExportToCsv() 
	{
		Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
		setCursor(hourglassCursor);
		
		JOptionPane.showMessageDialog(null, nodeinfotocsv,  "导出数据到CSV", JOptionPane.OK_CANCEL_OPTION);
		nodeinfotocsv.clearCsvDataSet();
		
		btnexportcsv.setEnabled(false);
		
		hourglassCursor = null;
		Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
		setCursor(hourglassCursor2);
//		
	}

	/*
	 * 
	 */
//	protected TimeSeries fengXiMatchedStockNumFromFormerExportedFile(ArrayList<ExportCondition> exportcond2,
//			String globeperiod2, int parseInt) 
//	{
//	
//		return null;
//	}
	/*
	 * 
	 */
	protected void displayNodeInfo(BkChanYeLianTreeNode selectednode) 
	{
		DisplayNodeJiBenMianService nodejbm = new DisplayNodeJiBenMianService (selectednode);
		DisplayNodeInfoPanel displaybkjbmpnl = new DisplayNodeInfoPanel (nodejbm);
		Dimension size = new Dimension(sclpinfosummary.getViewport().getSize().width,  displaybkjbmpnl.getContentHeight() + 10 );
		displaybkjbmpnl.setPreferredSize(size);
		displaybkjbmpnl.setMinimumSize(size);
		displaybkjbmpnl.setMaximumSize(size);
		pnlextrainfo.add (displaybkjbmpnl);
		
		DisplayNodesRelatedTagsServices nodetags =  new DisplayNodesRelatedTagsServices (selectednode);
		DisplayNodeInfoPanel displaybktagspnl = new DisplayNodeInfoPanel (nodetags);
		Dimension size4 = new Dimension(sclpinfosummary.getViewport().getSize().width,  displaybktagspnl.getContentHeight() + 10 );
		displaybktagspnl.setPreferredSize(size4);
		displaybktagspnl.setMinimumSize(size4);
		displaybktagspnl.setMaximumSize(size4);
		pnlextrainfo.add (displaybktagspnl);
		
		if(selectednode.getType() == BkChanYeLianTreeNode.TDXBK) {
			DisplayNodesRelatedNewsServices bknews = new DisplayNodesRelatedNewsServices (selectednode);
			bknews.setTimeRangeForInfoRange(this.dateChooser.getLocalDate(), this.dateChooser.getLocalDate());
			DisplayNodeInfoPanel displaybknewspnl = new DisplayNodeInfoPanel (bknews);
			Dimension size2 = new Dimension(sclpinfosummary.getViewport().getSize().width, displaybknewspnl.getContentHeight() + 10 );
			displaybknewspnl.setPreferredSize(size2);
			displaybknewspnl.setMinimumSize(size2);
			displaybknewspnl.setMaximumSize(size2);
			pnlextrainfo.add (displaybknewspnl);
		}
		
		if(selectednode.getType() == BkChanYeLianTreeNode.TDXGG) {
			DisplayNodeSellBuyInfoServices nodesellbuy = new DisplayNodeSellBuyInfoServices (selectednode);
			DisplayNodeInfoPanel displaybksbpnl = new DisplayNodeInfoPanel (nodesellbuy);
			Dimension size3 = new Dimension(sclpinfosummary.getViewport().getSize().width, displaybksbpnl.getContentHeight() + 10);
			displaybksbpnl.setPreferredSize(size3);
			displaybksbpnl.setMinimumSize(size3);
			displaybksbpnl.setMaximumSize(size3);
			pnlextrainfo.add (displaybksbpnl);
		}

		setUserSelectedColumnMessage( (TDXNodes)selectednode, this.dateChooser.getLocalDate());
	}
	/*
	 * 用户双击某个node占比chart，则放大显示该node一年内的占比所有数据
	 */
	protected void displayNodeLargerPeriodData(TDXNodes node, LocalDate datekey) 
	{
		if(!this.globecalwholeweek) {
			JOptionPane.showMessageDialog(null,"不是整周分析模式，不支持更大范围显示分析结果数据!","Warning",JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		//保证显示时间范围为当前日期前后有数据的36个月(3年)
		LocalDate curselectdate = dateChooser.getLocalDate().with(DayOfWeek.FRIDAY);
		LocalDate requireend = curselectdate.with(DayOfWeek.MONDAY).plus(6,ChronoUnit.MONTHS).with(DayOfWeek.FRIDAY);
		LocalDate requirestartd = curselectdate.with(DayOfWeek.MONDAY).minus(18,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		DateTime requiredstartdt= new DateTime(requirestartd.getYear(), requirestartd.getMonthValue(), requirestartd.getDayOfMonth(), 0, 0, 0, 0);
		DateTime requiredenddt = new DateTime(requireend.getYear(), requireend.getMonthValue(), requireend.getDayOfMonth(), 0, 0, 0, 0);
		Interval requiredinterval = new Interval(requiredstartdt, requiredenddt);
		
		NodeXPeriodData nodexdate = node.getNodeXPeroidData(globeperiod);
		LocalDate nodestart = nodexdate.getOHLCRecordsStartDate();
		LocalDate nodeend = LocalDate.now();//nodexdate.getAmoRecordsEndDate();
		DateTime nodestartdt= new DateTime(nodestart.getYear(), nodestart.getMonthValue(), nodestart.getDayOfMonth(), 0, 0, 0, 0);
		DateTime nodeenddt = new DateTime(nodeend.getYear(), nodeend.getMonthValue(), nodeend.getDayOfMonth(), 0, 0, 0, 0);
		Interval nodeinterval = new Interval(nodestartdt, nodeenddt);
		
		Interval overlapinterval = requiredinterval.overlap(nodeinterval);
		DateTime overlapstartdt = overlapinterval.getStart();
		DateTime overlapenddt = overlapinterval.getEnd();
		LocalDate overlapldstartday = LocalDate.of(overlapstartdt.getYear(), overlapstartdt.getMonthOfYear(), overlapstartdt.getDayOfMonth()).with(DayOfWeek.MONDAY);
		LocalDate overlapldendday = LocalDate.of(overlapenddt.getYear(), overlapenddt.getMonthOfYear(), overlapenddt.getDayOfMonth()).with(DayOfWeek.FRIDAY);
		
		//没有3年数据，补足3年
		long numberOfMonth = ChronoUnit.MONTHS.between(overlapldstartday, overlapldendday);
		if(numberOfMonth <36);
			overlapldstartday = overlapldstartday.with(DayOfWeek.MONDAY).minus( (36-numberOfMonth) ,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);

		//同步数据
		BanKuai bkcur = null; 
		SvsForNodeOfBanKuai svsbk = new SvsForNodeOfBanKuai ();
		if(node.getType() == BkChanYeLianTreeNode.TDXBK) {
			node = (TDXNodes) svsbk.getNodeData(node, overlapldstartday, overlapldendday, globeperiod, globecalwholeweek);
			svsbk.syncNodeData(node);
		} else 
		if(node.getType() == BkChanYeLianTreeNode.TDXGG) { 
			SvsForNodeOfStock svsstk = new SvsForNodeOfStock	();
			svsstk.getNodeData(node, overlapldstartday, overlapldendday, globeperiod, true);
			svsstk.syncNodeData(node);
			//如果是个股的话，还要显示其当前所属的板块占比信息，所以要把板块的数据也找出来。
			int row = tableBkZhanBi.getSelectedRow();
			if(row != -1) {
				int modelRow = tableBkZhanBi.convertRowIndexToModel(row);
				bkcur = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(modelRow);
				bkcur = (BanKuai) svsbk.getNodeData(bkcur, overlapldstartday, overlapldendday, globeperiod, globecalwholeweek);
				svsbk.syncNodeData(bkcur);
			}
		} else if(node.getType() == BkChanYeLianTreeNode.BKGEGU ) {
			BanKuai bk = ((StockOfBanKuai)node).getBanKuai();
			node = (TDXNodes) svsbk.getNodeData(node, overlapldstartday, overlapldendday, globeperiod, globecalwholeweek);
			svsbk.syncNodeData(node);
		} else if(node.getType() == BkChanYeLianTreeNode.DAPAN ) {
//			node = this.allbksks.getDaPan (requirestart.plusWeeks(1),globeperiod); //同步大盘数据,否则在其他地方会出错
		}
		
		SvsForNodeOfDaPan	svsdp = new SvsForNodeOfDaPan ();
		svsdp.getNodeData("", overlapldstartday, overlapldendday, globeperiod, this.globecalwholeweek);
		svsdp = null;
		
		BanKuaiFengXiLargePnl largeinfo = null;
		if(node.getType() == BkChanYeLianTreeNode.TDXBK) {
			DaPan treeroot = (DaPan) treeofbkstk.getModel().getRoot();
			largeinfo = new BanKuaiFengXiLargePnl (treeroot, node, overlapldstartday, overlapldendday, globeperiod);
			
		} else if(node.getType() == BkChanYeLianTreeNode.TDXGG || node.getType() == BkChanYeLianTreeNode.BKGEGU) { 
			DaPan treeroot = (DaPan) treeofbkstk.getModel().getRoot();
			largeinfo = new BanKuaiFengXiLargePnl ( bkcur , node, overlapldstartday, overlapldendday, globeperiod);
		}
		
		if(datekey != null)
			largeinfo.highLightSpecificBarColumn(datekey);
		else
			largeinfo.highLightSpecificBarColumn(curselectdate);
		
		JOptionPane.showMessageDialog(null, largeinfo, node.getMyOwnCode()+node.getMyOwnName()+ "大周期分析结果", JOptionPane.OK_CANCEL_OPTION);

		svsbk = null;
		largeinfo = null;
		System.gc();
	}
	/*
	 * 每周都要导出的条件在这里设置，一次设置节约时间
	 */
	
//	protected void initializeNormalExportConditions()
//	{
//		if( exportcond == null)
//			exportcond = new ArrayList<ExportCondition> ();
//		else
//			exportcond.clear();
//		
//		ExportCondition expc1 = new ExportCondition ("5.8",null,"4",null,null,null);
//		exportcond.add(expc1);
//
//		ExportCondition expc2 = new ExportCondition ("5.8","7",null,"5",null,null);
//		exportcond.add(expc2);
//		
//		ExportCondition expc3 = new ExportCondition ("2.8",null,"4",null,"30","880529");
//		exportcond.add(expc3);
//		
//		btnaddexportcond.setToolTipText("");
//		decrorateExportButton (expc1);
//		decrorateExportButton (expc2);
//		decrorateExportButton (expc3);
//	}
	/*
	 * 用户单个条件的设置
	 */
	protected void initializeExportConditions () 
	{
		if( exportcond == null)
			exportcond = new ArrayList<BanKuaiGeGuMatchCondition> ();
		
		BanKuaiGeGuMatchCondition expc = new BanKuaiGeGuMatchCondition ();
		setExportMainConditionBasedOnUserSelection (expc);

		ExtraExportConditions extraexportcondition = new ExtraExportConditions (expc);
		
		int extraresult = JOptionPane.showConfirmDialog(null,extraexportcondition , "附加导出条件:", JOptionPane.OK_CANCEL_OPTION);
		if(extraresult == JOptionPane.OK_OPTION) { //其他导出条件 
			
			expc = extraexportcondition.getSettingCondition ();
			if( expc.shouldExportOnlyCurrentBanKuai() ) {
					int row = tableBkZhanBi.getSelectedRow();
					if(row <0) {
						JOptionPane.showMessageDialog(null,"请选择一个板块！","Warning",JOptionPane.WARNING_MESSAGE);
						return ;
					}
					
					int modelRow = tableBkZhanBi.convertRowIndexToModel(row);
					BanKuai selectedbk = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(modelRow);
					String exportbk = selectedbk.getMyOwnCode();
					expc.setSettingBanKuai(exportbk);
				}
			exportcond.add(expc);
		}
		
//		decrorateExportButton (expc);
		
//		return expc;
	}
//	/*
//	 * 用户设置条件后，在buttion 的toolstip显示用户设置的条件
//	 */
//	private void decrorateExportButton(BanKuaiGeGuMatchCondition expc) 
//	{
//				//在tooltips显示当前有几个已经添加的条件及内容
//				String htmlstring = btnaddexportcond.getToolTipText();
//				org.jsoup.nodes.Document doc = Jsoup.parse(htmlstring);
//				org.jsoup.select.Elements content = doc.select("body");
//				content.append(expc.getTooltips());
//				htmlstring = doc.toString();
//				btnaddexportcond.setToolTipText(htmlstring);
//				
//				int curconditionnum;
//				try {
//					String num = btnaddexportcond.getText() ;
//				 curconditionnum = Integer.parseInt(btnaddexportcond.getText() ) ;
//				 curconditionnum ++ ;
//				} catch (java.lang.NumberFormatException e) {
//					curconditionnum = 1;
//				}
//				btnaddexportcond.setText(String.valueOf(curconditionnum));
//	}
	/*
	 * 同时兼任 界面显示的选择 
	 */
	private void setExportMainConditionBasedOnUserSelection (BanKuaiGeGuMatchCondition expc)
	{
		if(chbxzhangfu.isSelected() ) {
			String showzfmin; String showzfmax;
			if( !Strings.isNullOrEmpty(tfldzhangfumin.getText()) ) {
				showzfmin =  tfldzhangfumin.getText() ;
				expc.setSettingZhangFuMin (Double.parseDouble(showzfmin) );
			} else {
				expc.setSettingZhangFuMin (Double.parseDouble(null) );
			}
			
			if( !Strings.isNullOrEmpty(tfldzhangfumax.getText()) ) {
				showzfmax =  tfldzhangfumax.getText() ;
				expc.setSettingZhangFuMax (Double.parseDouble(showzfmax) );
			} else {
				expc.setSettingZhangFuMax (null );
			}
		} else {
			expc.setSettingZhangFuMax (null );
			expc.setSettingZhangFuMin (null );
		}

			
		if(ckbxma.isSelected() ) 
			expc.setSettingMaFormula(tfldweight.getText());
		else
			expc.setSettingMaFormula(null);
	
		if(ckbxcjemaxwk.isSelected() ) 
			expc.setSettingChenJiaoErMaxWk(  Integer.parseInt( tfldcjemaxwk.getText()) );
		else
			expc.setSettingChenJiaoErMaxWk(null);
		
		if(ckbxdpmaxwk.isSelected() ) 
			expc.setSettingDpMaxWk(Integer.parseInt( tflddisplaydpmaxwk.getText() ) );
		else
			expc.setSettingDpMaxWk(null );
		
		if(chckbxdpminwk.isSelected() ) {
			expc.setSettingDpMinWk(Integer.parseInt(tflddpminwk.getText())  );
		} else
			expc.setSettingDpMinWk (null);
		
		if(ckboxshowcje.isSelected()) {
			String showcjemin; String showcjemax;
			if( !Strings.isNullOrEmpty(tfldshowcje.getText()) ) {
				showcjemin =  tfldshowcje.getText() ;
				expc.setSettingChenJiaoErMin (Double.parseDouble(showcjemin) );
			} else {
				expc.setSettingChenJiaoErMin (Double.parseDouble(null) );
			}
			
			if( !Strings.isNullOrEmpty(tfldshowcjemax.getText()) ) {
				showcjemax =  tfldshowcjemax.getText() ;
				expc.setSettingChenJiaoErMax (Double.parseDouble(showcjemax) );
			} else {
				expc.setSettingChenJiaoErMax (null );
			}
		} else {
			expc.setSettingChenJiaoErMax (null );
			expc.setSettingChenJiaoErMin (null );
		}
		
		if(ckbxhuanshoulv.isSelected())
			expc.setSettingHuanShouLv(Double.parseDouble( tfldhuanshoulv.getText() ) );
		else 
			expc.setSettingHuanShouLv( null );
		
		if(chkliutongsz.isSelected()) {
			String showltszmin;String showltszmax;
			if( !Strings.isNullOrEmpty(tfldltszmin.getText()) ) {
				showltszmin =  tfldltszmin.getText() ;
			} else
				showltszmin = null;
			
			if( !Strings.isNullOrEmpty(tfldltszmax.getText()) ) {
				showltszmax = tfldltszmax.getText();
			} else
				showltszmax = null;
			
			expc.setSettingLiuTongShiZhiMin(Double.parseDouble(showltszmin) );
			expc.setSettingLiuTongShiZhiMax(Double.parseDouble(showltszmax) );
		} else {
			expc.setSettingLiuTongShiZhiMin(null );
			expc.setSettingLiuTongShiZhiMax(null );
		}

		if(chbxquekou.isSelected()) {
			expc.setHuiBuDownQueKou(true);
			expc.setZhangTing(true);
		} else {
			expc.setHuiBuDownQueKou(false);
			expc.setZhangTing(false);
		}
	}
	/*
	 * 
	 */
	protected void chooseParsedFile(String filename) 
	{
		//先选择文件
		if(filename == null) {
			String parsedpath = sysconfig.getTDXModelMatchExportFile ();
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			chooser.setCurrentDirectory(new File(parsedpath) );
			
			if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			    if(chooser.getSelectedFile().isDirectory())
			    	filename = (chooser.getSelectedFile()+ "\\").replace('\\', '/');
			    else
			    	filename = (chooser.getSelectedFile()).toString().replace('\\', '/');
			} else
				return;
		}
		
		if(!filename.endsWith("EBK") && !filename.endsWith("XML")) { //不是板块文件
			tfldparsedfile.setText("文件错误！请重新选择文件！");
			ckboxparsefile.setSelected(false);
			return;
		}
		
		ServicesForBkfxEbkOutPutFileDirectRead bkfxfh = new ServicesForBkfxEbkOutPutFileDirectRead ();
		LocalDate edbfiledate = bkfxfh.setBkfeOutPutFile(filename);
		if(edbfiledate != null) {
			int exchangeresult = JOptionPane.showConfirmDialog(null,"文件指定日期是" + edbfiledate + "。是否更改到该日期？", "是否更改日期？", JOptionPane.OK_CANCEL_OPTION);
			if(exchangeresult != JOptionPane.CANCEL_OPTION) {
				LocalDate curselectdate = dateChooser.getLocalDate();
				if(!curselectdate.equals(edbfiledate) ) 
					this.dateChooser.setLocalDate(edbfiledate);
			 } else { //用户可以作为本周文件
				 bkfxfh.resetBkfxFileDate(this.dateChooser.getLocalDate());
			 }
		} else {
			bkfxfh.resetBkfxFileDate(this.dateChooser.getLocalDate());
		}
//		int exchangeresult = JOptionPane.showConfirmDialog(null,"文件指定日期是" + edbfiledate + "。是否更改到该日期？", "是否更改日期？", JOptionPane.OK_CANCEL_OPTION);
//		if(exchangeresult != JOptionPane.CANCEL_OPTION) {
//			LocalDate curselectdate = dateChooser.getLocalDate();
//			if(!curselectdate.equals(edbfiledate) ) 
//				this.dateChooser.setLocalDate(edbfiledate);
//		 } else { //用户可以作为本周文件
//			 bkfxfh.resetBkfxFileDate(this.dateChooser.getLocalDate());
//		 }
				
		 bkfxfh.patchOutPutFileToTrees (treeofbkstk );
		 tfldparsedfile.setText(filename);
	}
	
	
		/*
	 * 跑马灯
	 */
	private void initializePaoMaDeng() 
	{
		// TODO Auto-generated method stub
		String title = "明日计划:";
//		Calendar cal = Calendar.getInstance();
//		cal.add(Calendar.DAY_OF_MONTH, -1);
//		Date ystday = cal.getTime();
		//asinglestockinfomation = new ASingleStockOperations("");
		PaoMaDengServices svspmd = new PaoMaDengServices ();
		String paomad = svspmd.getPaoMaDengInfo();
		
		if(!paomad.isEmpty())
			pnl_paomd.refreshMessage(title+paomad);
		else 
			pnl_paomd.refreshMessage(null);
	}
	/*
	 * 在本周各個股票占比的餅圖顯示選中的股票
	 */
	protected void hightlightSpecificSector(StockOfBanKuai selectstock) 
	{
		String stockcode = selectstock.getMyOwnCode();
		try {
			 String stockname = selectstock.getMyOwnName().trim(); 
			 for(PieChartPanelDataChangedListener tmplistener : piechartpanelbankuaidatachangelisteners) {
					tmplistener.hightlightSpecificSector (stockcode+stockname);
			 }
		 } catch ( java.lang.NullPointerException e) {
			 for(PieChartPanelDataChangedListener tmplistener : piechartpanelbankuaidatachangelisteners) {
					tmplistener.hightlightSpecificSector (stockcode);
			 }
		 }
	}
	
	private BanKuaiFengXiNodeCombinedCategoryPnl panelGgDpCjeZhanBi;
	private BanKuaiFengXiNodeCombinedCategoryPnl panelbkwkcjezhanbi;
	private BanKuaiFengXiNodeCombinedCategoryPnl panelggdpcjlwkzhanbi;
	private BanKuaiFengXiNodeCombinedCategoryPnl pnlbkwkcjlzhanbi;

	private BanKuaiFengXiPieChartCjePnl pnlcurwkggcjezhanbi;
	private BanKuaiFengXiPieChartCjePnl panelLastWkGeGucjeZhanBi;
	
	private BanKuaiFengXiCandlestickPnl paneldayCandle;
	
	private BanKuaiInfoTable tableBkZhanBi;
	private BanKuaiInfoTable tableselectedwkbkzb;
	private BanKuaiGeGuTable tableGuGuZhanBiInBk;
	private BanKuaiGeGuTable tablexuandingzhou;
	private BanKuaiGeGuTable tablexuandingminustwo; //new BanKuaiGeGuTable (this.stockmanager);
	private BanKuaiGeGuTable tablexuandingminusone;
	private BanKuaiGeGuTable tablexuandingplusone;
	private BanKuaiGeGuExternalInfoTable tableExternalInfo;
	
	private final JPanel contentPanel = new JPanel();
	private JButton okButton;
	private JStockCalendarDateChooser dateChooser; //https://toedter.com/jcalendar/
	private JScrollPane sclpleft;
	
	
	private JTextField tfldweight;
	
	private JScrollPane editorPanebankuai;
	private JButton btnresetdate;
	private JStockComboBox combxstockcode;
	
	private JTabbedPane tabbedPanegegu;
	
	private JTextField tfldshowcje;
	private JTextField tfldparsedfile;
	private JTabbedPane tabbedPanegeguzhanbi;
//	private JTextArea tfldselectedmsg;
	private JPanel tfldselectedmsg;
	private JStockComboBox combxsearchbk;
	private JCheckBox ckboxshowcje;
	private JCheckBox ckboxparsefile;
//	private DisplayBkGgInfoEditorPane editorPanenodeinfo;
	private JLabel lblhscje;
	private JLabel lblshcje;
	private JLabel lblshanghai;
	

	
	private JCheckBox ckbxdpmaxwk;
	private JTextField tflddisplaydpmaxwk;
	private PaoMaDeng2 pnl_paomd;
	private JTabbedPane tabbedPanebk;
	private JButton btnexportcsv;
	private Action exportCancelAction;
	private Action bkfxCancelAction;
	private JCheckBox chkliutongsz;
	private JTextField tfldltszmin;
	private JTextField tfldcjemaxwk;
	private JCheckBox ckbxcjemaxwk;
	
	private JLabel btnaddexportcond;
	private JCheckBox ckbxma;
	private JTextField tfldhuanshoulv;
	private JCheckBox ckbxhuanshoulv;
	private BanKuaiAndStockTree cyltreecopy;
	private JMenuItem menuItemRmvNodeFmFile;
	
	private JTextField tfldshowcjemax;
	
	private JPopupMenu jPopupMenuoftabbedpane;
	private JMenuItem menuItemliutong ; //系统默认按流通市值排名
	private JMenuItem menuItemzongshizhi ;
	private JMenuItem menuItemchengjiaoer ;
//	private JMenuItem menuItemstocktocsv ;
	
//	private JPopupMenu jPopupMenuoftabbedpanebk;
//	private JMenuItem menuItembktocsv ; //系统默认按流通市值排名
	private JMenuItem menuItemsiglestocktocsv;
	private JProgressBar progressBarExport;
	private JMenuItem menuItemsiglebktocsv;
	private JTextField tfldltszmax;
	private JCheckBox chbxquekou;
	private JLabel lblcybcje;
	private JLabel lbl50cje;
	private JTextField tflddpminwk;
	private JCheckBox chckbxdpminwk;
	private JMenuItem menuItemnonfixperiod;
	private JScrollPane scrollPane_1;
	
	private JTabbedPane tabbedPanebkzb;
	private JMenuItem menuItemnonshowselectbkinfo;
	private JMenuItem menuItemQiangShibk;
	private JMenuItem menuItemRuoShibk;
	private JMenuItem menuItemQiangShigg;
	private JMenuItem menuItemRuoShigg;
//	private JMenuItem menuItemyangxianbktocsv;
	private JMenuItem menuItemtimerangezhangfu;
	private JCheckBox chxbxwholeweek;
	private TagsPanel pnlbktags;
	private TagsPanel pnlstocktags;
	private JPanel pnlextrainfo;
	private JScrollPane scrldailydata;
	private JScrollPane sclpinfosummary;

	private JCheckBox chbxzhangfu;

	private JTextField tfldzhangfumin;

	private JTextField tfldzhangfumax;
	private BanKuaiGeGuTable tableTempGeGu;

	private JMenuItem menuItemTempGeGuFromFile;
	private JMenuItem menuItemTempGeGuFromTDXSw;
	private JLabel lblchuangyeban;
	private JLabel lblfifty;
	private JLabel lblhusheng;

	private JMenuItem menuItemAddRmvStockToYellow;

	private JMenuItem menuItemAddRmvBkToYellow;

	private JMenuItem menuItemTempGeGuFromZhidingbk;
	
	
	private void initializeGui() {
		setTitle("\u677F\u5757\u5206\u6790");
		setBounds(100, 100, 1923, 1033);
		getContentPane().setLayout(new BorderLayout());
		
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.EAST);
		
		UIManager.put("TabbedPane.selected", Color.yellow); 
		
		JPanel panel = new JPanel();
		JPanel panel_1 = new JPanel();
		
		JPanel panel_2 = new JPanel();
		
		paneldayCandle = new BanKuaiFengXiCandlestickPnl();
//		paneldayCandle.setBorder(new TitledBorder(null, "\u677F\u5757/\u4E2A\u80A1K\u7EBF\u8D70\u52BF", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		
		scrldailydata = new JScrollPane();
		
		editorPanebankuai = new JScrollPane();
		
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(18)
							.addComponent(panel, 0, 0, Short.MAX_VALUE))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addContainerGap()
							.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 382, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
								.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 1107, Short.MAX_VALUE)
								.addComponent(editorPanebankuai, GroupLayout.DEFAULT_SIZE, 1107, Short.MAX_VALUE))
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addGap(18)
									.addComponent(scrldailydata, GroupLayout.PREFERRED_SIZE, 375, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addGap(14)
									.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 380, GroupLayout.PREFERRED_SIZE))))
						.addComponent(paneldayCandle, GroupLayout.PREFERRED_SIZE, 1501, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(panel, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING, false)
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addGap(237)
									.addComponent(panel_2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(editorPanebankuai, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 831, GroupLayout.PREFERRED_SIZE))))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(paneldayCandle, GroupLayout.PREFERRED_SIZE, 327, GroupLayout.PREFERRED_SIZE)
							.addGap(5)
							.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 274, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(scrldailydata, GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)))
					.addContainerGap())
		);
		
		tfldselectedmsg = new JPanel ();
		scrldailydata.setViewportView(tfldselectedmsg);
		tfldselectedmsg.setLayout(new BoxLayout(tfldselectedmsg, BoxLayout.Y_AXIS));
//		tfldselectedmsg.setEditable(false);
		
		pnlcurwkggcjezhanbi = new BanKuaiFengXiPieChartCjePnl(0);
		tabbedPane.addTab("\u677F\u5757\u5F53\u5468\u6210\u4EA4\u989D\u4E2A\u80A1\u5360\u6BD4", null, pnlcurwkggcjezhanbi, null);
		pnlcurwkggcjezhanbi.setBorder(new TitledBorder(null, "\u677F\u5757\u5F53\u524D\u5468\u4E2A\u80A1\u5360\u6BD4", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		panelLastWkGeGucjeZhanBi = new BanKuaiFengXiPieChartCjePnl(-1);
		tabbedPane.addTab("-1\u5468/\u9009\u5B9A\u5468", null, panelLastWkGeGucjeZhanBi, null);
		panelLastWkGeGucjeZhanBi.setBorder(new TitledBorder(null, "\u677F\u5757\u4E0A\u4E00\u5468\u4E2A\u80A1\u5360\u6BD4", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		tabbedPanegeguzhanbi = new JTabbedPane(JTabbedPane.TOP);
		
		this.panelGgDpCjeZhanBi = new BanKuaiFengXiNodeCombinedCategoryPnl("CJE");
		this.panelGgDpCjeZhanBi.setAllowDrawAnnoation(false);
		this.panelGgDpCjeZhanBi.setDrawQueKouLine(false);
		this.panelGgDpCjeZhanBi.setDrawZhangDieTingLine(false);
		this.panelGgDpCjeZhanBi.setDrawAverageDailyCjeOfWeekLine(true);
		tabbedPanegeguzhanbi.addTab("\u4E2A\u80A1\u989D\u5360\u6BD4", null, panelGgDpCjeZhanBi, null);
		
		
		panelggdpcjlwkzhanbi = new BanKuaiFengXiNodeCombinedCategoryPnl("CJL");
		
		tabbedPanegeguzhanbi.addTab("\u4E2A\u80A1\u91CF\u5360\u6BD4", null, panelggdpcjlwkzhanbi, null);
		
		tabbedPanebkzb = new JTabbedPane(JTabbedPane.TOP);
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(
			gl_panel_2.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_panel_2.createSequentialGroup()
					.addComponent(tabbedPanebkzb, GroupLayout.PREFERRED_SIZE, 567, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(tabbedPanegeguzhanbi, GroupLayout.PREFERRED_SIZE, 533, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		gl_panel_2.setVerticalGroup(
			gl_panel_2.createParallelGroup(Alignment.TRAILING)
				.addComponent(tabbedPanebkzb, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 534, Short.MAX_VALUE)
				.addComponent(tabbedPanegeguzhanbi, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 534, Short.MAX_VALUE)
		);
		
		panelbkwkcjezhanbi = new BanKuaiFengXiNodeCombinedCategoryPnl("CJE");
		tabbedPanebkzb.addTab("\u677F\u5757\u989D\u5360\u6BD4", null, panelbkwkcjezhanbi, null);
//		panelbkwkcjezhanbi.setBorder(new TitledBorder(null, "\u677F\u5757\u6210\u4EA4\u989D\u5360\u6BD4", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panelbkwkcjezhanbi.setAllowDrawAnnoation(false);
		
		pnlbkwkcjlzhanbi = new BanKuaiFengXiNodeCombinedCategoryPnl("CJL");
		
		tabbedPanebkzb.addTab("\u677F\u5757\u91CF\u5360\u6BD4", null, pnlbkwkcjlzhanbi, null);
		panel_2.setLayout(gl_panel_2);
		
		tabbedPanegegu = new JTabbedPane(JTabbedPane.TOP);
		
		tabbedPanebk = new JTabbedPane(JTabbedPane.TOP);
		combxstockcode = new JStockComboBox(BkChanYeLianTreeNode.TDXGG);
		
		combxsearchbk = new JStockComboBox(BkChanYeLianTreeNode.TDXBK);
		combxsearchbk.setEditable(true);
		
		pnl_paomd = new PaoMaDeng2();
		pnl_paomd.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		
		exportCancelAction = new AbstractAction("导出条件个股") {

		      private static final long serialVersionUID = 4669650683189592364L;

		      @Override
		      public void actionPerformed(final ActionEvent e) {
		    	
		        if (exporttask == null) { 
		        	exportBanKuaiWithGeGuOnCondition2();
//		        	String msg =  "导出常规条件设置个股还是导出当前设置个股？选择“确定”将导出常规条件设置个股。";
//			  		int exchangeresult = JOptionPane.showConfirmDialog(null,msg , "导出选择", JOptionPane.OK_CANCEL_OPTION);
//			  		if(exchangeresult == JOptionPane.CANCEL_OPTION) //用户选择导出当前设置 
//			  			exportBanKuaiWithGeGuOnCondition2();
//			  		else { //用户选择导出常规设置
//			  			initializeNormalExportConditions ();
//			  			exportBanKuaiWithGeGuOnCondition2();
//			  		}
			  			
		        } else {
		        	exporttask.cancel(true);
		        }
		      }
		 };
		
		btnexportcsv = new JButton();
		btnexportcsv.setText("\u5BFC\u51FA\u5230CSV");
		btnexportcsv.setEnabled(false);
		
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addGap(10)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_panel_1.createSequentialGroup()
							.addGap(10)
							.addComponent(pnl_paomd, GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(combxstockcode, GroupLayout.PREFERRED_SIZE, 115, GroupLayout.PREFERRED_SIZE)
							.addContainerGap())
						.addGroup(gl_panel_1.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING, false)
								.addGroup(gl_panel_1.createSequentialGroup()
									.addComponent(combxsearchbk, 0, 0, Short.MAX_VALUE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btnexportcsv)
									.addGap(141))
								.addGroup(gl_panel_1.createSequentialGroup()
									.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING, false)
										.addComponent(tabbedPanebk, Alignment.TRAILING, 0, 0, Short.MAX_VALUE)
										.addComponent(tabbedPanegegu, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE))
									.addContainerGap())))))
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addComponent(combxsearchbk, GroupLayout.PREFERRED_SIZE, 29, Short.MAX_VALUE)
						.addComponent(btnexportcsv, GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(tabbedPanebk, GroupLayout.PREFERRED_SIZE, 370, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(tabbedPanegegu, GroupLayout.PREFERRED_SIZE, 361, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addComponent(combxstockcode, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
						.addComponent(pnl_paomd, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE))
					.addGap(33))
		);
		
		sclpleft = new JScrollPane();
		tabbedPanebk.addTab("\u5F53\u524D\u5468", null, sclpleft, null);
		tabbedPanebk.setBackgroundAt(0, Color.ORANGE);
		
		tableBkZhanBi = new BanKuaiInfoTable(this.stockmanager);	
		
		sclpleft.setViewportView(tableBkZhanBi);
		
		scrollPane_1 = new JScrollPane();
		tabbedPanebk.addTab("\u9009\u5B9A\u5468", null, scrollPane_1, null);
		
		tableselectedwkbkzb = new BanKuaiInfoTable(this.stockmanager);
		scrollPane_1.setViewportView(tableselectedwkbkzb);
		
		sclpinfosummary = new JScrollPane();
		tabbedPanebk.addTab("\u7EFC\u5408\u4FE1\u606F", null, sclpinfosummary, null);
		
		pnlextrainfo = new JPanel();
		sclpinfosummary.setViewportView(pnlextrainfo);
		pnlextrainfo.setLayout(new BoxLayout(pnlextrainfo, BoxLayout.Y_AXIS));
		
//		editorPanenodeinfo = new DisplayBkGgInfoEditorPane();
//		editorPanenodeinfo.setClearContentsBeforeDisplayNewInfo(true);
//		sclpinfosummary.setViewportView(editorPanenodeinfo);
//		editorPanenodeinfo.setEditable(false);
		
		JPanel panel_3 = new JPanel();
		tabbedPanebk.addTab("\u5173\u952E\u8BCD", null, panel_3, null);
		panel_3.setLayout(new BoxLayout(panel_3, BoxLayout.Y_AXIS));
		
		pnlbktags = new TagsPanel("",TagsPanel.HIDEHEADERMODE,TagsPanel.PARTCONTROLMODE);
		panel_3.add(pnlbktags);
		
		pnlstocktags = new TagsPanel("",TagsPanel.HIDEHEADERMODE,TagsPanel.PARTCONTROLMODE);
		panel_3.add(pnlstocktags);
		
		JScrollPane scrollPane = new JScrollPane();
		tabbedPanebk.addTab("\u4EA7\u4E1A\u94FE", null, scrollPane, null);
		
//		InvisibleTreeModel treeModel = (InvisibleTreeModel)this.cyldbopt.getBkChanYeLianTree().getModel();
		cyltreecopy = CreateExchangeTree.CreateTreeOfChanYeLian();//new BanKuaiAndStockTree(treeModel,"cyltreecopy");
		
		scrollPane.setViewportView(cyltreecopy);
		
		
		
		JScrollPane scrollPanedangqian = new JScrollPane();
		tabbedPanegegu.addTab("当前周", null, scrollPanedangqian, null);
		tabbedPanegegu.setBackgroundAt(0, Color.ORANGE);
		
		
		tableGuGuZhanBiInBk = new BanKuaiGeGuTable (this.stockmanager);
		
//		tableGuGuZhanBiInBk.hideZhanBiColumn(1);
//		tableGuGuZhanBiInBk.sortByZhanBiGrowthRate();
		scrollPanedangqian.setViewportView(tableGuGuZhanBiInBk);
		
		JScrollPane scrollPanGeGuExtralInfo = new JScrollPane();
		tabbedPanegegu.addTab("\u5176\u4ED6\u4FE1\u606F", null, scrollPanGeGuExtralInfo, null);
		tableExternalInfo = new BanKuaiGeGuExternalInfoTable (this.stockmanager);
		scrollPanGeGuExtralInfo.setViewportView(tableExternalInfo);
		
		JScrollPane scrollPanexuanding = new JScrollPane();
		tabbedPanegegu.addTab("选定周", null, scrollPanexuanding, null);
		tabbedPanegegu.setBackgroundAt(2, UIManager.getColor("MenuItem.selectionBackground"));
		tablexuandingzhou = new BanKuaiGeGuTable (this.stockmanager);
		scrollPanexuanding.setViewportView(tablexuandingzhou);
		
		JScrollPane scrollPanexuandingminusone = new JScrollPane();
		tabbedPanegegu.addTab("\u9009\u5B9A\u5468-1", null, scrollPanexuandingminusone, null);
//		tabbedPanegegu.setBackgroundAt(3, Color.LIGHT_GRAY);
		tablexuandingminusone = new BanKuaiGeGuTable (this.stockmanager);
		scrollPanexuandingminusone.setViewportView(tablexuandingminusone);
		
		JScrollPane scrollPanexuandingminustwo = new JScrollPane();
		tabbedPanegegu.addTab("\u9009\u5B9A\u5468-2", null, scrollPanexuandingminustwo, null);
		tablexuandingminustwo = new BanKuaiGeGuTable (this.stockmanager);
		scrollPanexuandingminustwo.setViewportView(tablexuandingminustwo);
		
		JScrollPane scrollPanexuandingplusone = new JScrollPane();
		tabbedPanegegu.addTab("\u9009\u5B9A\u5468+1", null, scrollPanexuandingplusone, null);
		
		tablexuandingplusone = new BanKuaiGeGuTable (this.stockmanager);
		scrollPanexuandingplusone.setViewportView(tablexuandingplusone);
		
		JScrollPane scrollPaneTempGeGu = new JScrollPane();
		tableTempGeGu = new BanKuaiGeGuTable (this.stockmanager);
		scrollPaneTempGeGu.setViewportView(tableTempGeGu);
		JScrollBar scrollbarTempGeGu = new JScrollBar(JScrollBar.VERTICAL);
		scrollbarTempGeGu.setUnitIncrement(10);
	    scrollbarTempGeGu.setUI(new MetalScrollBarUI() { //https://stackoverflow.com/questions/14176848/java-coloured-scroll-bar-search-result
	    	//https://java-swing-tips.blogspot.com/search/label/Matcher?m=0
	      @Override protected void paintTrack(
	            Graphics g, JComponent c, Rectangle trackBounds) {
	        super.paintTrack(g, c, trackBounds);
//	        Rectangle rect = tabbedPanegegu.getBounds();
//	        double sy = trackBounds.getHeight() / rect.getHeight();
//	        AffineTransform at = AffineTransform.getScaleInstance(1.0, sy);
	        g.setColor(Color.BLUE.darker());
	        
	        BanKuai interbk = ((BanKuaiGeGuTableModel)tableTempGeGu.getModel()).getInterSetctionBanKuai();
		    if(interbk!= null) {
		    	List<BkChanYeLianTreeNode> allstks = ((BanKuaiGeGuTableModel)tableTempGeGu.getModel() ).getAllStocks();
		    	int rownum = allstks.size();
		    	for(BkChanYeLianTreeNode tmpnode : allstks) {
		    		if( interbk.getBanKuaiGeGu (tmpnode.getMyOwnCode())  != null) {
		    			Integer rowindex = ((BanKuaiGeGuTableModel)tableTempGeGu.getModel() ).getStockRowIndex(tmpnode.getMyOwnCode() );
						if( rowindex  != null && rowindex >=0 ) {
							int modelRow = tableTempGeGu.convertRowIndexToView(rowindex);
//							int width = tableTempGeGu.getCellRect(modelRow, 0, true).width;
//							int height = tableTempGeGu.getCellRect(modelRow, 0, true).height;
//						    Rectangle r = new Rectangle(width, height * modelRow);
//						    Rectangle s = at.createTransformedShape(r).getBounds();
						    int h = 2; //Math.max(2, s.height-2);
						    int zuobiaoy = (trackBounds.height* modelRow)/rownum ;
					        g.fillRect(trackBounds.x+2, zuobiaoy , trackBounds.width, h);
						}
		    		}
		    	}
		    }
	      }
	    });
	    scrollPaneTempGeGu.setVerticalScrollBar(scrollbarTempGeGu);
		tabbedPanegegu.addTab("\u4E34\u65F6\u4E2A\u80A1", null, scrollPaneTempGeGu, null);
		tabbedPanegegu.setBackgroundAt(6, Color.CYAN);
		
		
		
		panel_1.setLayout(gl_panel_1);
		
//		dateChooser = new JDateChooser();
		dateChooser = new JStockCalendarDateChooser(new StockCalendar());
		dateChooser.setDateFormatString("yyyy-MM-dd");
		dateChooser.setDate(new Date() );
		
		bkfxCancelAction = new AbstractAction("重置") {

		      private static final long serialVersionUID = 4669650683189592364L;

		      @Override
		      public void actionPerformed(final ActionEvent e) {
//		        if (bkfxtask == null) {
//		        	if(!btnresetdate.isEnabled())
//		        		return;
//		        	
//		        	LocalDate cursettingdate = dateChooser.getLocalDate(); //.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//		        	if(!cursettingdate.equals(LocalDate.now())) {
////		        		LocalDate newdate =  LocalDate.now();
//		        		adjustDate (LocalDate.now());
////						dateChooser.setDate(Date.from(newdate.atStartOfDay(ZoneId.systemDefault()).toInstant() ) );
////						btnresetdate.setEnabled(false);
////						btnsixmonthafter.setEnabled(false);
//		        	} else {
//		        		btnresetdate.setEnabled(false);
//		        		
//		        	}
//		        } else { //当在做板块分析时候，如果用户调整时间，就取消板块分析任务。其实感觉也没多大必要
//		        	bkfxtask.cancel(true);
//		        }
		      }
		    		
		 };
		
		btnresetdate = new JButton(bkfxCancelAction);
		btnresetdate.setText("\u4ECA\u5929");
		
		lblshanghai = new JLabel("\u4E0A\u8BC1");
		
		
		lblhusheng = new JLabel("\u6CAA\u6DF1300");
		
		lblshcje = new JLabel("New label");
		
		lblhscje = new JLabel("New label");
		
		lblchuangyeban = new JLabel("\u521B\u4E1A\u677F");
		
		lblcybcje = new JLabel("New label");
		
		lblfifty = new JLabel("50\u6307");
		
		lbl50cje = new JLabel("New label");
		
		chxbxwholeweek = new JCheckBox("\u8BA1\u7B97\u5B8C\u6574\u5468");
		chxbxwholeweek.setToolTipText("\u5982\u4E0D\u52FE\u9009\uFF0C\u5219\u8BA1\u7B97\u5230\u88AB\u9009\u62E9\u7684\u90A3\u4E00\u5929");
		chxbxwholeweek.setSelected(true);
		
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING, false)
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(dateChooser, GroupLayout.PREFERRED_SIZE, 133, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(chxbxwholeweek)
							.addGap(18)
							.addComponent(btnresetdate))
						.addGroup(gl_panel.createSequentialGroup()
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING, false)
								.addGroup(gl_panel.createSequentialGroup()
									.addComponent(lblchuangyeban)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(lblcybcje, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addGroup(gl_panel.createSequentialGroup()
									.addComponent(lblshanghai)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(lblshcje, GroupLayout.PREFERRED_SIZE, 124, GroupLayout.PREFERRED_SIZE)))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel.createSequentialGroup()
									.addComponent(lblhusheng)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(lblhscje, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addGroup(gl_panel.createSequentialGroup()
									.addComponent(lblfifty)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(lbl50cje, GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)))))
					.addContainerGap(22, Short.MAX_VALUE))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
						.addComponent(dateChooser, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
							.addComponent(chxbxwholeweek)
							.addComponent(btnresetdate, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblshanghai)
						.addComponent(lblshcje)
						.addComponent(lblhusheng)
						.addComponent(lblhscje))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblchuangyeban)
						.addComponent(lblcybcje)
						.addComponent(lblfifty)
						.addComponent(lbl50cje))
					.addGap(9))
		);
		panel.setLayout(gl_panel);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("\u5173\u95ED");
				okButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) {
						BanKuaiFengXi.this.setVisible(false);
						BanKuaiFengXi.this.dispatchEvent(new WindowEvent(BanKuaiFengXi.this, WindowEvent.WINDOW_CLOSING));
					}
				});
				okButton.setActionCommand("OK");
				getRootPane().setDefaultButton(okButton);
			}
			
			ckbxma = new JCheckBox("\u7A81\u51FACLOSE vs. MA");
			ckbxma.setSelected(true);
			ckbxma.setFont(new Font("宋体", Font.PLAIN, 12));
			ckbxma.setForeground(new Color(0,153,153) );
			
			
			tfldweight = new JTextField ();//JTextFactory.createTextField();
			tfldweight.setPreferredSize(new Dimension(30, 25));
			tfldweight.setToolTipText("\u4F8B\uFF1A(>=250 && <60) || >30");
			tfldweight.setForeground(new Color(0,153,153) );
			tfldweight.setText(">=250 || >60");
//			tfldweight.setColumns(5);
			
			ckboxshowcje = new JCheckBox("\u7A81\u51FA\u6210\u4EA4\u989D\u533A\u95F4(\u4EBF)");
			ckboxshowcje.setBackground(Color.LIGHT_GRAY);
			ckboxshowcje.setForeground(Color.YELLOW);
			
	
			
			tfldshowcje = new JTextField();
			tfldshowcje.setPreferredSize(new Dimension(30, 25));
			tfldshowcje.setText("2.18");
			tfldshowcje.setForeground(Color.BLUE);
//			tfldshowcje.setColumns(4);
			
			ckboxparsefile = new JCheckBox("\u5206\u6790\u6587\u4EF6");
			ckboxparsefile.setToolTipText("\u5206\u6790\u6587\u4EF6");
			ckboxparsefile.setFont(new Font("宋体", Font.ITALIC, 12));
			ckboxparsefile.setForeground(Color.ORANGE);
			
			
			tfldparsedfile = new JTextField();
			tfldparsedfile.setForeground(Color.ORANGE);
			tfldparsedfile.setColumns(18);
			tfldparsedfile.setToolTipText(tfldparsedfile.getText());
			
			ckbxdpmaxwk = new JCheckBox("\u7A81\u51FADPMAXWK>=");
			ckbxdpmaxwk.setSelected(true);
			
			ckbxdpmaxwk.setForeground(Color.RED);
			
			tflddisplaydpmaxwk = new JTextField();
			tflddisplaydpmaxwk.setPreferredSize(new Dimension(20, 25));
			tflddisplaydpmaxwk.setForeground(Color.RED);
			tflddisplaydpmaxwk.setText("4");
//			tflddisplaydpmaxwk.setColumns(2);
			
			chkliutongsz = new JCheckBox("\u7A81\u51FA\u6D41\u901A\u5E02\u503C(\u4EBF)");
			chkliutongsz.setSelected(true);
			chkliutongsz.setBackground(Color.WHITE);
			
			chkliutongsz.setForeground(Color.MAGENTA);
			
			tfldltszmin = new JTextField();
			tfldltszmin.setPreferredSize(new Dimension(30, 25));
			tfldltszmin.setForeground(Color.MAGENTA);
			tfldltszmin.setText("5");
//			tfldltszmin.setColumns(2);
			
			ckbxcjemaxwk = new JCheckBox("\u7A81\u51FA\u5468\u65E5\u5E73\u5747\u6210\u4EA4\u989DMAXWK>=");
			ckbxcjemaxwk.setSelected(true);
			ckbxcjemaxwk.setToolTipText("\u7A81\u51FA\u5468\u65E5\u5E73\u5747\u6210\u4EA4\u989DMAXWK>=");
			ckbxcjemaxwk.setFont(new Font("宋体", Font.PLAIN, 12));
			ckbxcjemaxwk.setForeground(Color.CYAN);
			
			tfldcjemaxwk = new JTextField();
			tfldcjemaxwk.setText("3");
			tfldcjemaxwk.setPreferredSize(new Dimension(20, 25));
			tfldcjemaxwk.setForeground(Color.CYAN);
//			tfldcjemaxwk.setColumns(2);
			
			btnaddexportcond = JLabelFactory.createButton("",35, 25);
//			btnaddexportcond = new JButton("") {
//				 public Point getToolTipLocation(MouseEvent e) {
//				        return new Point(20, -30);
//				      }
//			};
//			btnaddexportcond.addActionListener(new ActionListener() {
//				public void actionPerformed(ActionEvent arg0) {
//				}
//			});
			btnaddexportcond.setHorizontalAlignment(SwingConstants.LEFT);
			btnaddexportcond.setToolTipText("<html>\u5BFC\u51FA\u6761\u4EF6\u8BBE\u7F6E(\u659C\u4F53\u9009\u9879\u4E0D\u5728\u5BFC\u51FA\u8303\u56F4\u5185):<br></html>");
			
			btnaddexportcond.setIcon(new ImageIcon(BanKuaiFengXi.class.getResource("/images/add-circular-outlined-button.png")));
			
			ckbxhuanshoulv = new JCheckBox("\u7A81\u51FA\u6362\u624B\u7387>=");
			ckbxhuanshoulv.setForeground(Color.BLUE);
			
			tfldhuanshoulv = new JTextField();
			tfldhuanshoulv.setPreferredSize(new Dimension(25, 25));
			tfldhuanshoulv.setText("30");
//			tfldhuanshoulv.setColumns(2);
			
//			btnexportmodelgegu = new JButton("\u5BFC\u51FA\u6761\u4EF6\u4E2A\u80A1");
			
			tfldshowcjemax = new JTextField();
			tfldshowcjemax.setPreferredSize(new Dimension(30, 25));
//			tfldshowcjemax.setColumns(10);
			
			progressBarExport = new JProgressBar();
			progressBarExport.setPreferredSize(new Dimension(95, 25));
			progressBarExport.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) {
					if (exporttask == null) { 
			        	exportBanKuaiWithGeGuOnCondition2();
//			        	String msg =  "导出常规条件设置个股还是导出当前设置个股？选择“确定”将导出常规条件设置个股。";
//				  		int exchangeresult = JOptionPane.showConfirmDialog(null,msg , "导出选择", JOptionPane.OK_CANCEL_OPTION);
//				  		if(exchangeresult == JOptionPane.CANCEL_OPTION) //用户选择导出当前设置 
//				  			exportBanKuaiWithGeGuOnCondition2();
//				  		else { //用户选择导出常规设置
//				  			initializeNormalExportConditions ();
//				  			exportBanKuaiWithGeGuOnCondition2();
//				  		}
				  			
			        } else {
			        	exporttask.cancel(true);
			        }
				}
			});
//			progressBarExport.setValue(0);
			progressBarExport.setString("点击导出条件个股");
			progressBarExport.setToolTipText("点击导出条件个股");
	        progressBarExport.setStringPainted(true);
			
			tfldltszmax = new JTextField();
			tfldltszmax.setPreferredSize(new Dimension(30, 25));
			tfldltszmax.setText("80");
			tfldltszmax.setForeground(Color.MAGENTA);
//			tfldltszmax.setColumns(10);
			
			chbxquekou = new JCheckBox("\u7A81\u51FA\u56DE\u8865\u4E0B\u8DF3/\u4E0A\u8DF3");
			chbxquekou.setToolTipText("\u7A81\u51FA\u56DE\u8865\u4E0B\u8DF3/\u4E0A\u8DF3");
			chbxquekou.setFont(new Font("宋体", Font.ITALIC, 12));
			
			chbxquekou.setForeground(Color.PINK);
			
			chckbxdpminwk = new JCheckBox("\u7A81\u51FADPMINWK>=");
			chckbxdpminwk.setToolTipText("\u7A81\u51FADPMINWK>=");
			chckbxdpminwk.setFont(new Font("宋体", Font.ITALIC, 12));
			chckbxdpminwk.setForeground(Color.GREEN);
			
			tflddpminwk = new JTextField();
			tflddpminwk.setPreferredSize(new Dimension(20, 25));
			tflddpminwk.setText("8");
//			tflddpminwk.setColumns(10);
			
			chbxzhangfu = new JCheckBox("\u7A81\u51FA\u6DA8\u8DCC\u5E45");
			chbxzhangfu.setSelected(true);
			chbxzhangfu.setToolTipText("\u7A81\u51FA\u6DA8\u8DCC\u5E45");
			chbxzhangfu.setForeground(Color.PINK);
			chbxzhangfu.setFont(new Font("宋体", Font.ITALIC, 12));
			
			tfldzhangfumin = new JTextField();
			tfldzhangfumin.setPreferredSize(new Dimension(25, 25));
			tfldzhangfumin.setText("7.5");
//			tfldzhangfumin.setColumns(2);
			
			tfldzhangfumax = new JTextField();
			tfldzhangfumax.setPreferredSize(new Dimension(25, 25));
			tfldzhangfumax.setText("20");
//			tfldzhangfumax.setColumns(2);
			
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
			buttonPane.add(btnaddexportcond);
			buttonPane.add(progressBarExport);
			buttonPane.add(ckbxma);
			buttonPane.add(tfldweight);
			buttonPane.add(ckboxshowcje);
			buttonPane.add(tfldshowcje);
			buttonPane.add(tfldshowcjemax);
			buttonPane.add(ckbxdpmaxwk);
			buttonPane.add(tflddisplaydpmaxwk);
			buttonPane.add(chckbxdpminwk);
			buttonPane.add(tflddpminwk);
			buttonPane.add(chkliutongsz);
			buttonPane.add(tfldltszmin);
			buttonPane.add(tfldltszmax);
			buttonPane.add(ckbxcjemaxwk);
			buttonPane.add(tfldcjemaxwk);
			buttonPane.add(ckbxhuanshoulv);
			buttonPane.add(tfldhuanshoulv);
			buttonPane.add(chbxquekou);
			buttonPane.add(chbxzhangfu);
			buttonPane.add(tfldzhangfumin);
			buttonPane.add(tfldzhangfumax);
			buttonPane.add(ckboxparsefile);
			buttonPane.add(tfldparsedfile);
			buttonPane.add(okButton);
		}
		
		reFormatGui ();
	}
	/*
	 * 个股表里的板块占比MAX暂时不用，先隐藏
	 */
	private void reFormatGui ()
	{
//		tableGuGuZhanBiInBk.getTableHeader().getColumnModel().getColumn(5).setMaxWidth(0);
//		tableGuGuZhanBiInBk.getTableHeader().getColumnModel().getColumn(5).setMinWidth(0);
//		tableGuGuZhanBiInBk.getTableHeader().getColumnModel().getColumn(5).setWidth(0);
//		tableGuGuZhanBiInBk.getTableHeader().getColumnModel().getColumn(5).setPreferredWidth(0);
//		
//		tablexuandingzhou.getTableHeader().getColumnModel().getColumn(5).setMaxWidth(0);
//		tablexuandingzhou.getTableHeader().getColumnModel().getColumn(5).setMinWidth(0);
//		tablexuandingzhou.getTableHeader().getColumnModel().getColumn(5).setWidth(0);
//		tablexuandingzhou.getTableHeader().getColumnModel().getColumn(5).setPreferredWidth(0);
		
//		tableExternalInfo.getTableHeader().getColumnModel().getColumn(5).setMaxWidth(0);
//		tableExternalInfo.getTableHeader().getColumnModel().getColumn(5).setMinWidth(0);
//		tableExternalInfo.getTableHeader().getColumnModel().getColumn(5).setWidth(0);
//		tableExternalInfo.getTableHeader().getColumnModel().getColumn(5).setPreferredWidth(0);
		
//		tablexuandingplusone.getTableHeader().getColumnModel().getColumn(5).setMaxWidth(0);
//		tablexuandingplusone.getTableHeader().getColumnModel().getColumn(5).setMinWidth(0);
//		tablexuandingplusone.getTableHeader().getColumnModel().getColumn(5).setWidth(0);
//		tablexuandingplusone.getTableHeader().getColumnModel().getColumn(5).setPreferredWidth(0);
//		
//		tablexuandingminusone.getTableHeader().getColumnModel().getColumn(5).setMaxWidth(0);
//		tablexuandingminusone.getTableHeader().getColumnModel().getColumn(5).setMinWidth(0);
//		tablexuandingminusone.getTableHeader().getColumnModel().getColumn(5).setWidth(0);
//		tablexuandingminusone.getTableHeader().getColumnModel().getColumn(5).setPreferredWidth(0);
//		
//		tablexuandingminustwo.getTableHeader().getColumnModel().getColumn(5).setMaxWidth(0);
//		tablexuandingminustwo.getTableHeader().getColumnModel().getColumn(5).setMinWidth(0);
//		tablexuandingminustwo.getTableHeader().getColumnModel().getColumn(5).setWidth(0);
//		tablexuandingminustwo.getTableHeader().getColumnModel().getColumn(5).setPreferredWidth(0);
		//
		
		jPopupMenuoftabbedpane = new JPopupMenu();
		menuItemliutong = new JMenuItem(" 按流通市值排名"); 
		menuItemzongshizhi = new JMenuItem("按总市值排名");
		menuItemchengjiaoer = new JMenuItem("X 按成交额排名"); //系统默认按成交额排名
		menuItemtimerangezhangfu = new JMenuItem("按阶段涨幅排名"); //系统默认按成交额排名
//		menuItemstocktocsv = new JMenuItem("导出所有个股到CSV");
//      
		jPopupMenuoftabbedpane.add(menuItemzongshizhi);
       menuItemzongshizhi.setEnabled(false);
       jPopupMenuoftabbedpane.add(menuItemliutong);
       jPopupMenuoftabbedpane.add(menuItemchengjiaoer);
       jPopupMenuoftabbedpane.add(menuItemtimerangezhangfu);
//       jPopupMenuoftabbedpane.add(menuItemstocktocsv);
       
//       jPopupMenuoftabbedpanebk = new JPopupMenu();
//       menuItembktocsv = new JMenuItem("导出所有板块到CSV");
//       jPopupMenuoftabbedpanebk.add(menuItembktocsv);
       
//       menuItemyangxianbktocsv = new JMenuItem("导出周线阳线板块到CSV");
//       jPopupMenuoftabbedpanebk.add(menuItemyangxianbktocsv);
       
       menuItemAddRmvBkToYellow = new JMenuItem("设置/取消黄标");
       tableBkZhanBi.getPopupMenu().add(menuItemAddRmvBkToYellow);
       
       menuItemsiglebktocsv = new JMenuItem("导出板块到CSV");
       menuItemQiangShibk = new JMenuItem("设为强势板块");
	   menuItemRuoShibk = new JMenuItem("设为弱势板块");
//	   tableBkZhanBi.getPopupMenu().add(menuItemQiangShibk);
//	   tableBkZhanBi.getPopupMenu().add(menuItemRuoShibk);
	   tableBkZhanBi.getPopupMenu().add(menuItemsiglebktocsv);
       
//       menuItemRmvNodeFmFile = new JMenuItem("剔除出模型文件") ;
//       menuItemRmvNodeFmFile.setEnabled(false);
//       tableGuGuZhanBiInBk.getPopupMenu().add(menuItemRmvNodeFmFile);
		
	   menuItemQiangShigg = new JMenuItem("设为强势个股");
	   menuItemRuoShigg = new JMenuItem("设为弱势个股");
//	   tableGuGuZhanBiInBk.getPopupMenu().add(menuItemQiangShigg);
//	   tableGuGuZhanBiInBk.getPopupMenu().add(menuItemRuoShigg);
	   
       menuItemsiglestocktocsv = new JMenuItem("导出个股到CSV");
       tableGuGuZhanBiInBk.getPopupMenu().add(menuItemsiglestocktocsv);
       
       menuItemnonfixperiod = new JMenuItem("不定周期DPM??WK");
       tableGuGuZhanBiInBk.getPopupMenu().add(menuItemnonfixperiod);
       
       menuItemAddRmvStockToYellow = new JMenuItem("设置/取消黄标");
       tableGuGuZhanBiInBk.getPopupMenu().add(menuItemAddRmvStockToYellow);
       
       menuItemnonshowselectbkinfo = new JMenuItem("同时计算选定周分析数据");
       panelbkwkcjezhanbi.addMenuItem (menuItemnonshowselectbkinfo,null);
       
       JPopupMenu popupMenuGeguNews = new JPopupMenu () ;
       menuItemTempGeGuFromFile = new JMenuItem("文件导入");
       popupMenuGeguNews.add(menuItemTempGeGuFromFile);
       menuItemTempGeGuFromTDXSw = new JMenuItem("通达信自定义板块导入");
       popupMenuGeguNews.add(menuItemTempGeGuFromTDXSw);
       menuItemTempGeGuFromZhidingbk = new JMenuItem("指定板块导入");
       popupMenuGeguNews.add(menuItemTempGeGuFromZhidingbk);
       tableTempGeGu.getTableHeader().setComponentPopupMenu (popupMenuGeguNews);
       
       
       
	}
	/*
	 * 根据星期几已经本周几个交易日自动设置成交量的值
	 */
//	private void setHighLightChenJiaoEr() 
//	{
//		LocalDate curselectdate = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//		if(LocalDate.now().with(DayOfWeek.FRIDAY).equals(curselectdate.with(DayOfWeek.FRIDAY) ) ) { //说明在当前周，按星期来设置
//			if(curselectdate.getDayOfWeek() == DayOfWeek.MONDAY )
//				tfldshowcje.setText("0");
//			else if(curselectdate.getDayOfWeek() == DayOfWeek.TUESDAY )
//				tfldshowcje.setText("1.2");
//			else if(curselectdate.getDayOfWeek() == DayOfWeek.WEDNESDAY )
//				tfldshowcje.setText("2.5");
//			else if(curselectdate.getDayOfWeek() == DayOfWeek.THURSDAY )
//				tfldshowcje.setText("3.7");
//			else if(curselectdate.getDayOfWeek() == DayOfWeek.FRIDAY )
//				tfldshowcje.setText("4.9");
//			else if(curselectdate.getDayOfWeek() == DayOfWeek.SATURDAY || curselectdate.getDayOfWeek() == DayOfWeek.SUNDAY)
//				tfldshowcje.setText("5.8");
//			
//		} else { //应该根据该周有多少个交易日来设置，而不是简单的按1.2*5 约=5.8
//			int tradingdays = this.bkdbopt.getTradingDaysOfTheWeek (curselectdate);
//			if(tradingdays < 5)
//				tfldshowcje.setText(String.valueOf(1.15*tradingdays).substring(0, 3) );
//			else
//				tfldshowcje.setText("5.8");
//		}
//		
//	}
	
		/*
	 * 閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓缂寸尨鎷锋椂澶敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹涓閿熸枻鎷烽敓绔鎷蜂笓閿熻剼杈炬嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹GUI閿熺潾鍑ゆ嫹浣块敓鏂ゆ嫹
	 * http://www.javacreed.com/swing-worker-example/
	 * https://github.com/javacreed/swing-worker-example/blob/master/src/main/java/com/javacreed/examples/swing/worker/part3/Application.java
	 */
	class ExportTask2 extends SwingWorker<Integer, String>  
	{
		private LocalDate selectiondate;
		private File outputfile;
		private BanKuaiAndStockTree bkcyltree;
		private ArrayList<BanKuaiGeGuMatchCondition> expclist;
		private String period;
		 

		public ExportTask2 (ArrayList<BanKuaiGeGuMatchCondition> exportcond,LocalDate selectiondate,String period,File outputfile)
		{
			this.bkcyltree = treeofbkstk;
			this.expclist = exportcond;
			this.selectiondate = selectiondate;
			this.period = period;
			this.outputfile = outputfile;
		}
		/*
	     * Main task. Executed in background thread.
	     */
	    @Override
	    public Integer doInBackground() 
	    {
	    	Charset charset = Charset.forName("GBK") ;

	    	ArrayList<TDXNodes> outputnodeslist = new ArrayList<TDXNodes> ();
			
			String ouputfilehead2 = "";
//			int progressint = 30;
			for(BanKuaiGeGuMatchCondition expc : expclist) {
				if (isCancelled()) 
					 return null;
				
				setProgress(30);
				try{
				ExportMatchedNode exportaction = new ExportMatchedNode (expc);
				Set<TDXNodes> outputnodes = exportaction.checkTDXNodeMatchedCurSettingConditons(selectiondate, globeperiod);
				outputnodeslist.addAll(outputnodes);
				
				ouputfilehead2 = ouputfilehead2 + expc.getConditionsDescriptions ();
				
				} catch(Exception e) {
					e.printStackTrace();
				}
				
				setProgress(50);

			}	
			
			//对导出的板块和个股按照周成交额进行排序，这样导入到通达信后能自动按照成交额排序
			Collections.sort(outputnodeslist, new NodeChenJiaoErComparator(selectiondate,0,period) );
			
			try {
				Files.append("<导出日期:" + selectiondate.toString() + ">"+ System.getProperty("line.separator") ,outputfile, charset);
				Files.append("<导出周期:" + period + ">"+ System.getProperty("line.separator") ,outputfile, charset);
				Files.append("<" + ouputfilehead2 + ">" + System.getProperty("line.separator") ,outputfile, charset);
			} catch (IOException e1) {
				e1.printStackTrace();
			} 
			
			//写入板块分析文件
			for(BkChanYeLianTreeNode node : outputnodeslist) {
				 String outputstock;
				 String cjs = ((TDXNodes)node).getSuoShuJiaoYiSuo();
				 if(cjs.trim().toLowerCase().equals("sh"))
					outputstock= "1" + node.getMyOwnCode().trim();
				else
					outputstock ="0" + node.getMyOwnCode().trim();
				
				 outputstock = outputstock +  System.getProperty("line.separator");
				 try {
						Files.append(outputstock,outputfile, charset);
					} catch (IOException e) {
						e.printStackTrace();
				}
			}
		
			//生产gephi文件
//			GephiFilesGenerator gephigenerator = new GephiFilesGenerator (allbksks);
//			gephigenerator.generatorGephiFile(outputfile, dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), globeperiod);
  			
  			setProgress( 90 );
  			
  			setProgress( 100 );
  			
  			outputnodeslist = null;
			outputfile = null;
		
			return 100;
	    }

	    /*
	     * Executed in event dispatching thread
	     */
	    @Override
	    public void done() 
	    {
	    	try {
				int i = get();
			} catch (InterruptedException | ExecutionException | CancellationException e) {
//				e.printStackTrace();
			}
	      
//	    	Toolkit.getDefaultToolkit().beep();
	    	SystemAudioPlayed.playSound();
	    }
//	    public void run ()
//	    {
//	    	
//	    }
	    public void cancel ()
	    {
	    	try {
				int i = get ();
				logger.debug("export be canceled");
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
	    }
	}
}

/*
 * 
 */
class NodeChenJiaoErComparator implements Comparator<TDXNodes> 
{
	private String period;
	private LocalDate compareDate;
	private int difference;
	public NodeChenJiaoErComparator (LocalDate compareDate, int difference, String period )
	{
		this.period = period;
		this.compareDate = compareDate;
		this.difference = difference;
	}
    public int compare(TDXNodes node1, TDXNodes node2) {
        Double cje1 = node1.getNodeXPeroidData( period).getChengJiaoEr(compareDate, difference) ;
        Double cje2 = node2.getNodeXPeroidData( period).getChengJiaoEr(compareDate, difference);
        
        return cje2.compareTo(cje1);
    }
}


