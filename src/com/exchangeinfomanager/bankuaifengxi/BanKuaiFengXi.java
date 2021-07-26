package com.exchangeinfomanager.bankuaifengxi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import javax.swing.border.EmptyBorder;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;


import org.apache.commons.io.comparator.LastModifiedFileComparator;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.exchangeinfomanager.A.MainGui.StockInfoManager;
import com.exchangeinfomanager.Core.Nodes.BanKuai;
import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.Core.Nodes.CylTreeNestedSetNode;
import com.exchangeinfomanager.Core.Nodes.DaPan;
import com.exchangeinfomanager.Core.Nodes.Stock;
import com.exchangeinfomanager.Core.Nodes.StockOfBanKuai;
import com.exchangeinfomanager.Core.Nodes.TDXNodes;
import com.exchangeinfomanager.Core.NodesDataServices.ServicesForNode;
import com.exchangeinfomanager.Core.NodesDataServices.ServicesForNodeBanKuai;
import com.exchangeinfomanager.Core.NodesDataServices.SvsForNodeOfBanKuai;
import com.exchangeinfomanager.Core.NodesDataServices.SvsForNodeOfDZHBanKuai;
import com.exchangeinfomanager.Core.NodesDataServices.SvsForNodeOfStock;
import com.exchangeinfomanager.Core.Nodexdata.NodeXPeriodData;
import com.exchangeinfomanager.Core.Nodexdata.ohlcvaprimarydata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.Core.Trees.BanKuaiAndStockTree;
import com.exchangeinfomanager.Core.Trees.CreateExchangeTree;
import com.exchangeinfomanager.Core.Trees.CylTreeUpdatedListener;
import com.exchangeinfomanager.Core.Trees.TreeOfChanYeLian;
import com.exchangeinfomanager.Core.exportimportrelated.NodesTreeRelated;
import com.exchangeinfomanager.News.News;
import com.exchangeinfomanager.News.NewsCache;
import com.exchangeinfomanager.News.NewsLabelServices;
import com.exchangeinfomanager.News.NewsServices;
import com.exchangeinfomanager.News.ServicesForNews;
import com.exchangeinfomanager.News.ServicesForNewsLabel;
import com.exchangeinfomanager.News.ExternalNewsType.CreateExternalNewsDialog;
import com.exchangeinfomanager.News.ExternalNewsType.DuanQiGuanZhu;
import com.exchangeinfomanager.News.ExternalNewsType.DuanQiGuanZhuServices;
import com.exchangeinfomanager.News.ExternalNewsType.QiangShi;
import com.exchangeinfomanager.News.ExternalNewsType.QiangShiServices;
import com.exchangeinfomanager.News.ExternalNewsType.RuoShiServices;
import com.exchangeinfomanager.ServicesOfDisplayNodeInfo.DisPlayNodeSuoShuBanKuaiListPanel;
import com.exchangeinfomanager.ServicesOfDisplayNodeInfo.DisPlayNodeSuoShuBanKuaiListServices;
import com.exchangeinfomanager.ServicesOfDisplayNodeInfo.DisplayNodeExchangeDataServices;
import com.exchangeinfomanager.ServicesOfDisplayNodeInfo.DisplayNodeExchangeDataServicesPanel;
import com.exchangeinfomanager.ServicesOfDisplayNodeInfo.DisplayNodeGuDongInfoServices;
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
import com.exchangeinfomanager.bankuaifengxi.CandleStick.BanKuaiFengXiCandlestickPnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartCjePnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartPnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiNodeCombinedCategoryPnl;
import com.exchangeinfomanager.bankuaifengxi.HighlightAndExportNodes.BanKuaiAndGeGuMatchingConditions;
import com.exchangeinfomanager.bankuaifengxi.HighlightAndExportNodes.BkfxHightLightForGeGuPropertyFilePnl;

import com.exchangeinfomanager.bankuaifengxi.HighlightAndExportNodes.SetExportNodeConditionPnl;
import com.exchangeinfomanager.bankuaifengxi.LineChart.BanKuaiFengXiLineChartPnl;
import com.exchangeinfomanager.bankuaifengxi.PieChart.BanKuaiFengXiPieChartCjePnl;

import com.exchangeinfomanager.bankuaifengxi.PieChart.BanKuaiFengXiPieChartPnl;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.VoiceEngine;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.WeeklyAnalysis;
import com.exchangeinfomanager.bankuaifengxi.bankuaigegumergetable.BanKuaiGeGuMergeTable;
import com.exchangeinfomanager.bankuaifengxi.bankuaigegumergetable.BanKuaiGeGuMergeTableModel;
import com.exchangeinfomanager.bankuaifengxi.bankuaigegutable.BanKuaiGeGuBasicTable;
import com.exchangeinfomanager.bankuaifengxi.bankuaigegutable.BanKuaiGeGuBasicTableModel;
import com.exchangeinfomanager.bankuaifengxi.bankuaigegutable.BanKuaiGeGuExternalInfoTableFromPropertiesFile;
import com.exchangeinfomanager.bankuaifengxi.bankuaiinfotable.BanKuaiInfoTable;
import com.exchangeinfomanager.bankuaifengxi.bankuaiinfotable.BanKuaiInfoTableModel;
import com.exchangeinfomanager.bankuaifengxi.gegutobankuaistable.GeGuToBanKuaiTable;
import com.exchangeinfomanager.bankuaifengxi.gegutobankuaistable.GeGuToBanKuaiTableModel;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.commonlib.ParseBanKuaiWeeklyFielGetStocksProcessor;
import com.exchangeinfomanager.commonlib.ReminderPopToolTip;
import com.exchangeinfomanager.commonlib.ScrollUtil;
import com.exchangeinfomanager.commonlib.JScrollBarNodesMark.JScrollBarNodesMark;
import com.exchangeinfomanager.commonlib.jstockcombobox.JStockComboBox;
import com.exchangeinfomanager.commonlib.jstockcombobox.JStockComboBoxModel;
import com.exchangeinfomanager.gui.subgui.DateRangeSelectPnl;
import com.exchangeinfomanager.gui.subgui.PaoMaDeng2;
import com.exchangeinfomanager.guifactory.JLabelFactory;
import com.exchangeinfomanager.guifactory.JPanelFactory;
import com.exchangeinfomanager.systemconfigration.SetupSystemConfiguration;
import com.exchangeinfomanager.zhidingyibankuai.PnlZhiDingYiBanKuai;
import com.exchangeinfomanager.zhidingyibankuai.TDXZhiDingYiBanKuaiServices;
import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;



import com.google.common.io.Files;


import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import javax.swing.LayoutStyle.ComponentPlacement;

import javax.swing.JScrollPane;
import javax.swing.JSeparator;

import com.toedter.calendar.JDateChooser;

import net.miginfocom.swing.MigLayout;

import javax.swing.JCheckBox;

import java.beans.PropertyChangeListener;
import java.io.File;

import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;


import java.sql.SQLException;
import java.text.DecimalFormat;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

import java.time.temporal.ChronoUnit;
import java.beans.PropertyChangeEvent;
import java.awt.event.MouseAdapter;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


import javax.swing.UIManager;

import javax.swing.AbstractAction;
import javax.swing.Action;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JFileChooser;
import javax.swing.JTabbedPane;

import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;



import com.exchangeinfomanager.bankuaifengxi.bankuaigegutable.BanKuaiGeGuExternalInfoTableModelFromPropertiesFile;
import com.exchangeinfomanager.bankuaifengxi.bankuaigegutable.BanKuaiGeGuTableFromPropertiesFile;
import com.exchangeinfomanager.bankuaifengxi.bankuaigegutable.BanKuaiGeGuTableModelFromPropertiesFile;
import com.exchangeinfomanager.bankuaifengxi.BankuaiAndGeguTableBasic.BanKuaiandGeGuTableBasic;
import com.exchangeinfomanager.bankuaifengxi.BankuaiAndGeguTableBasic.BandKuaiAndGeGuTableBasicModel;



import javax.swing.BoxLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.KeyboardFocusManager;
import java.awt.Point;

import javax.swing.JTable;



public class BanKuaiFengXi extends JDialog 
{
	private static final long serialVersionUID = 1L;
	/**
	 * Create the dialog.
	 * @param bkcyl2 
	 */
	public BanKuaiFengXi(StockInfoManager stockmanager1, String screenresoltuion)
	{
		this.stockmanager = stockmanager1;
		
		this.nodeinfotocsv = new NodeInfoToCsv ();
		this.bkfxremind = new BanKuaiFengXiRemindXmlHandler ();
		this.bkggmatchcondition = new BanKuaiAndGeGuMatchingConditions ();
		this.globecalwholeweek = true; //计算整周
		
		setupBkfxSettingProperties ();
		setupPredefinedExportFormula ();
		
		initializeGuiOf2560Resolution ();
		initializeKuaiJieZhishuPnl ();
			
		createEvents ();
		setUpChartDataListeners ();

		initializePaoMaDeng ();
		
		adjustDate ( LocalDate.now());
	}
	
	private static Logger logger = Logger.getLogger(BanKuaiFengXi.class);
 	
	private Properties bkfxsettingprop;
	private String globeperiod;
	private Boolean globecalwholeweek; //计算整周
	private BanKuaiFengXiRemindXmlHandler bkfxremind;
	private LocalDate lastselecteddate;
	private StockInfoManager stockmanager;
	private BanKuaiAndGeGuMatchingConditions bkggmatchcondition;
	private VoiceEngine readengine;
	private NodeInfoToCsv nodeinfotocsv;

	private Set<BarChartPanelHightLightColumnListener> chartpanelhighlightlisteners;
	private Set<BarChartPanelDataChangedListener> barchartpanelbankuaidatachangelisteners;
	private Set<PieChartPanelDataChangedListener> piechartpanelbankuaidatachangelisteners;
	private Set<BarChartPanelDataChangedListener> barchartpanelstockdatachangelisteners;
	private Set<BanKuaiGeGuBasicTable> tablebkggtablesetWithoutTemp;
	private Set<BanKuaiGeGuBasicTable> tablebkggtableset;
	private Set<BanKuaiandGeGuTableBasic> tablealltableset;
	private Set<BanKuaiandGeGuTableBasic> tableallbktablesset;
	/*
	 * 
	 */
	private void adjustDate(LocalDate dateneedtobeadjusted)
	{
		BanKuai tdxsh = (BanKuai) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode("999999", BkChanYeLianTreeNode.TDXBK);
		LocalDate lastestdate = tdxsh.getShuJuJiLuInfo().getJyjlmaxdate();
		if(lastestdate != null) {
			this.dateChooser.setLocalDate(lastestdate);
			btnresetdate.setEnabled(false);
			return;
		}
		
		//如果上证没有数据，那就按自然日来设置每周日一是新的一周开始，因为还没有导入数据，会显示为没有数据，所以把时间调整到上一周六
				DayOfWeek dayofweek = LocalDate.now().getDayOfWeek();
				if(dayofweek.equals(DayOfWeek.SUNDAY) ) {
					 LocalDate friday = dateneedtobeadjusted.minus(2,ChronoUnit.DAYS);
					 this.dateChooser.setLocalDate(friday);
				} else if(dayofweek.equals(DayOfWeek.SATURDAY) ) {
					 LocalDate friday = dateneedtobeadjusted.minus(1,ChronoUnit.DAYS);
					 this.dateChooser.setLocalDate(friday);
				} else if(dayofweek.equals(DayOfWeek.MONDAY) && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) <19 ) {
					LocalDate friday = dateneedtobeadjusted.minus(3,ChronoUnit.DAYS);
					this.dateChooser.setLocalDate(friday);
				} else if( !dayofweek.equals(DayOfWeek.SUNDAY) && !dayofweek.equals(DayOfWeek.SATURDAY) &&
						Calendar.getInstance().get(Calendar.HOUR_OF_DAY) <19 ) //当天的数据要晚上才导入，白天看到的是昨天的数据
					this.dateChooser.setLocalDate(LocalDate.now().minus(1,ChronoUnit.DAYS)); 
				else
					this.dateChooser.setLocalDate(LocalDate.now());
				
				btnresetdate.setEnabled(false);
	}
	/*
	 * 
	 */
	private void setupBkfxSettingProperties() 
	{		
		try {
			bkfxsettingprop = new Properties();
			String propFileName =  (new SetupSystemConfiguration()).getBankuaifenxiSettingPropertiesFile();
			FileInputStream inputStream = new FileInputStream(propFileName);
			if (inputStream != null) 
				bkfxsettingprop.load(inputStream);
			 
			inputStream.close();
		} catch (Exception e) {	e.printStackTrace();} finally {}
	}
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
		this.tablealltableset = new HashSet<>();
		this.tablebkggtableset = new HashSet<>();
		this.tablebkggtablesetWithoutTemp = new HashSet<>();
		this.tableallbktablesset  = new HashSet<>();
		
		this.tablealltableset.add(tableGuGuZhanBiInBk);
		this.tablealltableset.add(tableExternalInfo);
		this.tablealltableset.add(tablexuandingzhou);
		this.tablealltableset.add(tablexuandingminusone);
		this.tablealltableset.add(tablexuandingminustwo);
		this.tablealltableset.add(tablexuandingplusone);
		this.tablealltableset.add(tableTempGeGu);
		this.tablealltableset.add(tableBkZhanBi);
		this.tablealltableset.add(tableselectedwkbkzb);
		this.tablealltableset.add(tblDzhBkCurWkZhanBi);
		this.tablealltableset.add(tblDzhBkCurSelectedWkZhanBi);
		this.tablealltableset.add(tblmergegegubkinfo);
		this.tablealltableset.add(tblmergegeguinfoinallbk);
		
		this.tableallbktablesset.add(tableBkZhanBi);
		this.tableallbktablesset.add(tableselectedwkbkzb);
		this.tableallbktablesset.add(tblDzhBkCurWkZhanBi);
		this.tableallbktablesset.add(tblDzhBkCurSelectedWkZhanBi);
		
		this.tablebkggtableset.add(tableGuGuZhanBiInBk);
		this.tablebkggtableset.add(tableExternalInfo);
		this.tablebkggtableset.add(tablexuandingzhou);
		this.tablebkggtableset.add(tablexuandingminusone);
		this.tablebkggtableset.add(tablexuandingminustwo);
		this.tablebkggtableset.add(tablexuandingplusone);
		this.tablebkggtableset.add(tableTempGeGu);
		
		this.tablebkggtablesetWithoutTemp.add(tableGuGuZhanBiInBk);
		this.tablebkggtablesetWithoutTemp.add(tableExternalInfo);
		this.tablebkggtablesetWithoutTemp.add(tablexuandingzhou);
		this.tablebkggtablesetWithoutTemp.add(tablexuandingminusone);
		this.tablebkggtablesetWithoutTemp.add(tablexuandingminustwo);
		this.tablebkggtablesetWithoutTemp.add(tablexuandingplusone);
		
		barchartpanelbankuaidatachangelisteners.add(panelbkwkcjezhanbi);
		barchartpanelbankuaidatachangelisteners.add(pnlbkwkcjlzhanbi);
		//板块pie chart
		piechartpanelbankuaidatachangelisteners.add(pnlcurwkggcjezhanbi);
		
		//个股对板块
//		barchartpanelstockofbankuaidatachangelisteners.add(panelgegucje); //个股对于板块交易额,一般看个股对大盘的成交额，这个就不看了
//		barchartpanelstockofbankuaidatachangelisteners.add(panelggbkcjezhanbi);
		barchartpanelstockdatachangelisteners.add(panelGgDpCjeZhanBi);
		barchartpanelstockdatachangelisteners.add(panelggdpcjlwkzhanbi);
		//用户点击bar chart的一个column, highlight bar chart
		chartpanelhighlightlisteners.add(panelGgDpCjeZhanBi);
		chartpanelhighlightlisteners.add(panelbkwkcjezhanbi);
		chartpanelhighlightlisteners.add(pnlStockCandle);
		chartpanelhighlightlisteners.add(pnlBanKuaiCandle);
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
		this.bkggmatchcondition.addCacheListener(tableBkZhanBi);
		this.bkggmatchcondition.addCacheListener(tableselectedwkbkzb);
		this.bkggmatchcondition.addCacheListener(panelbkwkcjezhanbi);
		this.bkggmatchcondition.addCacheListener(panelGgDpCjeZhanBi);
		this.bkggmatchcondition.addCacheListener(panelggdpcjlwkzhanbi);
		this.bkggmatchcondition.addCacheListener(pnlbkwkcjlzhanbi);
		this.bkggmatchcondition.addCacheListener(tblmergegegubkinfo);
		this.bkggmatchcondition.addCacheListener(tblmergegeguinfoinallbk);
		this.bkggmatchcondition.addCacheListener(tableBkSocialbkCurwkData);
		this.bkggmatchcondition.addCacheListener(tableBkSocialbkSelectwkData);
		this.bkggmatchcondition.addCacheListener(tblDzhBkCurWkZhanBi);
		this.bkggmatchcondition.addCacheListener(tblDzhBkCurSelectedWkZhanBi);
		
		((BandKuaiAndGeGuTableBasicModel)tableGuGuZhanBiInBk.getModel()).setHighLightBanKuaiAndGeGuMatchingCondition (bkggmatchcondition);
		((BandKuaiAndGeGuTableBasicModel)tableExternalInfo.getModel()).setHighLightBanKuaiAndGeGuMatchingCondition (bkggmatchcondition);
		((BandKuaiAndGeGuTableBasicModel)tablexuandingzhou.getModel()).setHighLightBanKuaiAndGeGuMatchingCondition (bkggmatchcondition);
		((BandKuaiAndGeGuTableBasicModel)tablexuandingminusone.getModel()).setHighLightBanKuaiAndGeGuMatchingCondition (bkggmatchcondition);
		((BandKuaiAndGeGuTableBasicModel)tablexuandingminustwo.getModel()).setHighLightBanKuaiAndGeGuMatchingCondition (bkggmatchcondition);
		((BandKuaiAndGeGuTableBasicModel)tablexuandingplusone.getModel()).setHighLightBanKuaiAndGeGuMatchingCondition (bkggmatchcondition);
		((BandKuaiAndGeGuTableBasicModel)tableTempGeGu.getModel()).setHighLightBanKuaiAndGeGuMatchingCondition (bkggmatchcondition);
		((BandKuaiAndGeGuTableBasicModel)tableBkZhanBi.getModel()).setHighLightBanKuaiAndGeGuMatchingCondition (bkggmatchcondition);
		((BandKuaiAndGeGuTableBasicModel)tableselectedwkbkzb.getModel()).setHighLightBanKuaiAndGeGuMatchingCondition (bkggmatchcondition);
		((BandKuaiAndGeGuTableBasicModel)tblmergegegubkinfo.getModel()).setHighLightBanKuaiAndGeGuMatchingCondition (bkggmatchcondition);
		((BandKuaiAndGeGuTableBasicModel)tblmergegeguinfoinallbk.getModel()).setHighLightBanKuaiAndGeGuMatchingCondition (bkggmatchcondition);
		((BandKuaiAndGeGuTableBasicModel)tableBkSocialbkCurwkData.getModel()).setHighLightBanKuaiAndGeGuMatchingCondition (bkggmatchcondition);
		((BandKuaiAndGeGuTableBasicModel)tableBkSocialbkSelectwkData.getModel()).setHighLightBanKuaiAndGeGuMatchingCondition (bkggmatchcondition);
		((BandKuaiAndGeGuTableBasicModel)tblDzhBkCurWkZhanBi.getModel()).setHighLightBanKuaiAndGeGuMatchingCondition (bkggmatchcondition);
		((BandKuaiAndGeGuTableBasicModel)tblDzhBkCurSelectedWkZhanBi.getModel()).setHighLightBanKuaiAndGeGuMatchingCondition (bkggmatchcondition);
		
		dateChooser.addJCalendarDateChangeListener (combxsearchbk);
		dateChooser.addJCalendarDateChangeListener (combxstockcode);
		dateChooser.addJCalendarDateChangeListener (bkhlpnl);
		dateChooser.addJCalendarDateChangeListener (pnlsetexportcond);
	}
	/*
	 * 所有板块占比增长率的排名
	 */
	private void gettBanKuaiZhanBiRangedByGrowthRateOfPeriod (LocalDate curselectdate, String period)
	{
		this.globeperiod = period;

		List<BkChanYeLianTreeNode> bkwithcje = getBanKuaiFenXiQualifiedNodes (curselectdate,this.globeperiod,this.globecalwholeweek,true);
		((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).addBanKuai( bkwithcje );
    	((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).refresh(curselectdate,globeperiod);
    	tableBkZhanBi.revalidate();
    	tableBkZhanBi.repaint();

		//如果TEMP TABLE有个股，需要重新设置一下日期，否则high light用的是老日期，
		if(((BanKuaiGeGuBasicTableModel)this.tableTempGeGu.getModel()).getRowCount() >0 ) {
			TDXNodes tmpbk = ((BanKuaiGeGuBasicTableModel)this.tableTempGeGu.getModel()).getCurDispalyBandKuai ();
			((BanKuaiGeGuBasicTableModel)this.tableTempGeGu.getModel()).refresh((BanKuai)tmpbk, curselectdate, globeperiod);
		}
		
		//把当前计算结果一些数据先存下来
		Boolean onweekend = false;
		DayOfWeek dayofweek = this.dateChooser.getLocalDate().getDayOfWeek();
		if(dayofweek.equals(DayOfWeek.SUNDAY) || dayofweek.equals(DayOfWeek.SATURDAY) || dayofweek.equals(DayOfWeek.FRIDAY) )
			onweekend = true;

		if(!this.globecalwholeweek || onweekend ) { //
			curselectdate = complexSolutionForDateChooserWithCalWholeWeek ();
			
			int rowcount = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getRowCount();
			int columnIndexCjeZbGr = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getKeyWrodColumnIndex ("CjeZbGrowRate");
			int columnIndexCjlZbGr = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getKeyWrodColumnIndex ("CjlZbGrowRate");
			for(int i=0;i<rowcount;i++) {
				BanKuai bk = (BanKuai) ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getNode(i);
				Double cjezhgr = (Double) ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getValueAt(i, columnIndexCjeZbGr);
				Double cjlzhgr = (Double) ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getValueAt(i, columnIndexCjlZbGr);
				
				NodeXPeriodData bkxdataday = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.DAY);
				bkxdataday.addDaPanChenJiaoErZhanBiGrowingRate (curselectdate,cjezhgr);
				bkxdataday.addDaPanChenJiaoLiangZhanBiGrowingRate (curselectdate,cjlzhgr);
			}
		}
	}
	/*
	 * 
	 */
	private void gettDZHBanKuaiZhanBiRangedByGrowthRateOfPeriod (LocalDate curselectdate,String period) 
	{
		this.globeperiod = period;
		
    	LocalDate requirestart = CommonUtility.getSettingRangeDate(curselectdate,"middle").with(DayOfWeek.MONDAY);
    	//这里必须先同步一下TDXDAN, 因为refresh里面的superbk用的tdx dapan，必须要有数据
    	DaPan tdxdapan = (DaPan) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode("000000", BkChanYeLianTreeNode.DAPAN);
    	tdxdapan.getServicesForNode(true).getNodeData(tdxdapan, requirestart, curselectdate,this.globeperiod,this.globecalwholeweek);
    	tdxdapan.getServicesForNode(true).getNodeKXian(tdxdapan, requirestart, curselectdate,this.globeperiod,this.globecalwholeweek);
    	tdxdapan.getServicesForNode(false);
    	
    	List<BkChanYeLianTreeNode> dzhbk = getDZHBanKuaiFenXiQualifiedNodes (curselectdate,this.globeperiod,this.globecalwholeweek,true);
		((BanKuaiInfoTableModel)tblDzhBkCurWkZhanBi.getModel()).addBanKuai( dzhbk );
		((BanKuaiInfoTableModel)tblDzhBkCurWkZhanBi.getModel()).refresh(curselectdate,globeperiod);
		tblDzhBkCurWkZhanBi.revalidate();
		tblDzhBkCurWkZhanBi.repaint();
		
		//如果TEMP TABLE有个股，需要重新设置一下日期，否则high light用的是老日期，
		if(((BanKuaiGeGuBasicTableModel)this.tableTempGeGu.getModel()).getRowCount() >0 ) {
			TDXNodes tmpbk = ((BanKuaiGeGuBasicTableModel)this.tableTempGeGu.getModel()).getCurDispalyBandKuai ();
			((BanKuaiGeGuBasicTableModel)this.tableTempGeGu.getModel()).refresh((BanKuai)tmpbk, curselectdate, globeperiod);
		}
		
		//把当前计算结果一些数据先存下来
		Boolean onweekend = false;
		DayOfWeek dayofweek = this.dateChooser.getLocalDate().getDayOfWeek();
		if(dayofweek.equals(DayOfWeek.SUNDAY) || dayofweek.equals(DayOfWeek.SATURDAY) || dayofweek.equals(DayOfWeek.FRIDAY) )
			onweekend = true;

		if(!this.globecalwholeweek || onweekend ) { //
			curselectdate = complexSolutionForDateChooserWithCalWholeWeek ();
			
			int rowcount = ((BanKuaiInfoTableModel)tblDzhBkCurWkZhanBi.getModel()).getRowCount();
			int columnIndexCjeZbGr = ((BanKuaiInfoTableModel)tblDzhBkCurWkZhanBi.getModel()).getKeyWrodColumnIndex ("CjeZbGrowRate");
			int columnIndexCjlZbGr = ((BanKuaiInfoTableModel)tblDzhBkCurWkZhanBi.getModel()).getKeyWrodColumnIndex ("CjlZbGrowRate");
			for(int i=0;i<rowcount;i++) {
				BanKuai bk = (BanKuai) ((BanKuaiInfoTableModel)tblDzhBkCurWkZhanBi.getModel()).getNode(i);
				Double cjezhgr = (Double) ((BanKuaiInfoTableModel)tblDzhBkCurWkZhanBi.getModel()).getValueAt(i, columnIndexCjeZbGr);
				Double cjlzhgr = (Double) ((BanKuaiInfoTableModel)tblDzhBkCurWkZhanBi.getModel()).getValueAt(i, columnIndexCjlZbGr);
				
				NodeXPeriodData bkxdataday = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.DAY);
				bkxdataday.addDaPanChenJiaoErZhanBiGrowingRate (curselectdate,cjezhgr);
				bkxdataday.addDaPanChenJiaoLiangZhanBiGrowingRate (curselectdate,cjlzhgr);
			}
		}
	}
	/*
	 * 
	 */
	private List<BkChanYeLianTreeNode> getBanKuaiFenXiQualifiedNodes (LocalDate checkdate, String globeperiod, Boolean globecalwholeweek, Boolean forcetogetnodedataagain)
	{
		Set<String> testset = new HashSet<>();
		testset.add("999999");testset.add("399001");testset.add("880468");testset.add("880467");
		
		List<BkChanYeLianTreeNode> bkwithcje = new ArrayList<> ();
		SvsForNodeOfBanKuai svsbk = new SvsForNodeOfBanKuai ();
		
		LocalDate requirestart = CommonUtility.getSettingRangeDate(checkdate,"large").with(DayOfWeek.MONDAY);
		
		BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getModel().getRoot();
		int bankuaicount = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getModel().getChildCount(treeroot);
		for(int i=0;i< bankuaicount; i++) {
			
			BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getModel().getChild(treeroot, i);
			if(childnode.getType() != BkChanYeLianTreeNode.TDXBK) 
				continue;
			
			if( !((BanKuai)childnode).getBanKuaiOperationSetting().isShowinbkfxgui() )
				continue;
			
			if( ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL) 
					||  ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL)  ) //有些指数是没有个股和成交量的，不列入比较范围
				continue;
			
//			if(!testset.contains(childnode.getMyOwnCode()))
//				continue;
			
			LocalDate jlmaxdate = ((BanKuai)childnode).getShuJuJiLuInfo().getJyjlmaxdate();
			LocalDate jlmindate = ((BanKuai)childnode).getShuJuJiLuInfo().getJyjlmindate();
			if(jlmaxdate == null && jlmindate == null)
				continue; //bankuai without data records should not be displayed
			
			if(forcetogetnodedataagain)
				childnode = svsbk.getNodeData( (BanKuai)childnode, requirestart, checkdate,globeperiod,globecalwholeweek);
			
			NodeXPeriodData bkxdata = ((TDXNodes)childnode).getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
			if(bkxdata.getIndexOfSpecificDateOHLCData(checkdate) != null)//板块当周没有数据也不考虑，板块一般不可能没有数据，没有数据说明该板块这周还没有诞生，或者过去有，现在成交量已经不存入数据库
				bkwithcje.add( (BanKuai)childnode );
		}
		
		svsbk = null;
		return bkwithcje;
	}
	/*
	 * 
	 */
	private List<BkChanYeLianTreeNode> getDZHBanKuaiFenXiQualifiedNodes (LocalDate checkdate, String globeperiod, Boolean globecalwholeweek, Boolean forcetogetnodedataagain)
	{
		List<BkChanYeLianTreeNode> bkwithcje = new ArrayList<> ();

		LocalDate requirestart = CommonUtility.getSettingRangeDate(checkdate,"large").with(DayOfWeek.MONDAY);
		BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode) CreateExchangeTree.CreateTreeOfDZHBanKuaiAndStocks().getModel().getRoot();
		int bankuaicount = CreateExchangeTree.CreateTreeOfDZHBanKuaiAndStocks().getModel().getChildCount(treeroot);
		for(int i=0;i< bankuaicount; i++) {
			
			BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode) CreateExchangeTree.CreateTreeOfDZHBanKuaiAndStocks().getModel().getChild(treeroot, i);
			if(childnode.getType() != BkChanYeLianTreeNode.DZHBK) 
				continue;
			
			if( !((BanKuai)childnode).getBanKuaiOperationSetting().isShowinbkfxgui() )
				continue;
			
			if( ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL) 
					||  ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL)  ) //有些指数是没有个股和成交量的，不列入比较范围
				continue;
			
			LocalDate jlmaxdate = ((BanKuai)childnode).getShuJuJiLuInfo().getJyjlmaxdate();
			LocalDate jlmindate = ((BanKuai)childnode).getShuJuJiLuInfo().getJyjlmindate();
			if(jlmaxdate == null && jlmindate == null)
				continue; //bankuai without data records should not be displayed
			
			if(forcetogetnodedataagain) 
				childnode = ((BanKuai)childnode).getServicesForNode(true).getNodeData( (BanKuai)childnode, requirestart, checkdate,globeperiod,globecalwholeweek);

			NodeXPeriodData bkxdata = ((TDXNodes)childnode).getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
			if(bkxdata.getIndexOfSpecificDateOHLCData(checkdate) != null)//板块当周没有数据也不考虑，板块一般不可能没有数据，没有数据说明该板块这周还没有诞生，或者过去有，现在成交量已经不存入数据库
				bkwithcje.add( (BanKuai)childnode );
		}
		
		return bkwithcje;
	}
	/*
	 * 大盘数据在更改时间或更改计算完整周模式时候一次性同步
	 */
	private void unifiedOperationForDaPanSyncData (LocalDate curselectdate, String period)
	{
		LocalDate requirestart = CommonUtility.getSettingRangeDate(curselectdate,"large").with(DayOfWeek.MONDAY);
		DaPan dapan = (DaPan) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode("000000", BkChanYeLianTreeNode.DAPAN);
    	dapan.getServicesForNode(true).getNodeData(dapan, requirestart, curselectdate,period,this.globecalwholeweek);
    	dapan.getServicesForNode(true).getNodeKXian(dapan, requirestart, curselectdate,NodeGivenPeriodDataItem.DAY,this.globecalwholeweek);
    	dapan.getServicesForNode(false);
	}
	/*
	 * 点击一个板块
	 */
	private void unifiedOperationsAfterUserSelectABanKuai (BanKuai selectedbk)
	{
		clearTheGuiBeforDisplayNewInfoSection2 ();
		clearTheGuiBeforDisplayNewInfoSection3 ();
		
		LocalDate curselectdate = null;
		try{	curselectdate = dateChooser.getLocalDate();
		} catch (java.lang.NullPointerException e) {	JOptionPane.showMessageDialog(null,"日期有误!","Warning",JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		unifiedTabbedPanSelectionActions (selectedbk,curselectdate );

		refreshCurentBanKuaiFengXiResult (selectedbk,curselectdate, globeperiod); //板块内个股的分析结果
		
//		refreshCurrentBanKuaiTags (selectedbk,globeperiod);
//		diaplayTDXNodeNewsToKXianPnl (selectedbk);
		
		displayNodeInfo(selectedbk);
		
		cyltreecopy.searchAndLocateNodeInTree (selectedbk);
		bksocialtreecopy.searchAndLocateNodeInTree (selectedbk);
		showBanKuaiSocialFriendsWeeklyData (tableBkSocialbkCurwkData,selectedbk,curselectdate);
		
		String ShowBanKuaiRemindInfo   = bkfxsettingprop.getProperty ("ShowBanKuaiRemindInfo");
		if(ShowBanKuaiRemindInfo  != null && ShowBanKuaiRemindInfo .toUpperCase().equals("TRUE") )
				showReminderMessage (bkfxremind.getBankuairemind() );
		
		preOperationForMainBkAndTempBkIntersection ();
		
		bkggmatchcondition.setSettingBanKuai(selectedbk.getMyOwnCode()); //为可能导出做准备
		
		setFirstSelectedTab ();
		findInputedNodeInBanKuaiTables(selectedbk.getMyOwnCode());
		
		tableBkZhanBi.revalidate();
    	tableBkZhanBi.repaint();
	}
	/*
	 * 
	 */
	protected void refreshCurentBanKuaiFengXiResult(BanKuai selectedbk,LocalDate curselectdate, String period)  
	{
		selectedbk.getShuJuJiLuInfo().setHasReviewedToday(true);

		ServicesForNodeBanKuai svsbk = (ServicesForNodeBanKuai) selectedbk.getBanKuaiService (true);
		//一次性同步板块数据以及所属个股数据
		try {	svsbk.syncBanKuaiAndItsStocksForSpecificTime(selectedbk, CommonUtility.getSettingRangeDate(curselectdate,"large"), curselectdate,
					globeperiod,this.globecalwholeweek);
		} catch (SQLException e) {	e.printStackTrace(); }
		svsbk = null;
		
		//显示K线
		pnlBanKuaiCandle.displayQueKou(false);
		BanKuai zhishubk =  (BanKuai) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode("999999", BkChanYeLianTreeNode.TDXBK );
		pnlBanKuaiCandle.updatedDate(zhishubk,selectedbk,CommonUtility.getSettingRangeDate(curselectdate, "basic"),curselectdate,NodeGivenPeriodDataItem.DAY);
		
		String[] cjecjlzbgrkw = {"CjeZbGrowRate", "CjlZbGrowRate"};
		pnlbkcjecjezbgr.updatedDate(selectedbk, curselectdate.minus(1,ChronoUnit.WEEKS).with(DayOfWeek.MONDAY), curselectdate.with(DayOfWeek.FRIDAY),
				NodeGivenPeriodDataItem.DAY, cjecjlzbgrkw);
		 
		//板块所属个股
		if(selectedbk.getBanKuaiLeiXing().equals(BanKuai.HASGGWITHSELFCJL)) { //有个股才需要更新，有些板块是没有个股的
			//板块所属个股占比info
			((BanKuaiGeGuTableModelFromPropertiesFile)tableGuGuZhanBiInBk.getModel()).refresh(selectedbk, curselectdate,period);
			((BanKuaiGeGuExternalInfoTableModelFromPropertiesFile)tableExternalInfo.getModel()).refresh(selectedbk, curselectdate,period); 

			//显示2周的板块个股pie chart
			for(PieChartPanelDataChangedListener tmplistener : piechartpanelbankuaidatachangelisteners) 
				tmplistener.updateDate(selectedbk, curselectdate, 0,globeperiod);
//			piechartpanelbankuaidatachangelisteners.forEach(l -> l.updateDate(selectedbk, curselectdate, 0,globeperiod) );
		}
		
		//板块成交额占比显示大盘的周平均成交额，以便和板块的周平均成交额对比
		DaPan treeroot = (DaPan) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getModel().getRoot();
		panelbkwkcjezhanbi.setDisplayBarOfSpecificBanKuaiCjeCjlInsteadOfSelfCjeCjl (treeroot); 
		//板块自身占比
		for(BarChartPanelDataChangedListener tmplistener : barchartpanelbankuaidatachangelisteners) 
			tmplistener.updatedDate(selectedbk, CommonUtility.getSettingRangeDate(curselectdate,"basic"),curselectdate,globeperiod);
//		barchartpanelbankuaidatachangelisteners.forEach(l -> l.updatedDate(selectedbk, CommonUtility.getSettingRangeDate(curselectdate,"basic"),curselectdate,globeperiod) );
		
		return;
	}
	/*
	 * 用户选择一个板块的column后的相应操作
	 */
	protected void refreshAfterUserSelectBanKuaiColumn (TDXNodes bkcur, LocalDate selectdate) 
	{
		chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(selectdate));
		barchartpanelbankuaidatachangelisteners.forEach(l -> l.setAnnotations( selectdate ) );
		
		//当周所有新闻
		DaPan dp = (DaPan) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode("000000", BkChanYeLianTreeNode.DAPAN);
		DisplayNodesRelatedNewsServices bknews = new DisplayNodesRelatedNewsServices (dp);
		bknews.setTimeRangeForInfoRange(selectdate, selectdate);
		DisplayNodeInfoPanel displaybknewspnl = new DisplayNodeInfoPanel (bknews);

		Dimension size4 = new Dimension(sclpinfosummary.getViewport().getSize().width,  displaybknewspnl.getContentHeight() + 10 );
		displaybknewspnl.setPreferredSize(size4);
		displaybknewspnl.setMinimumSize(size4);
		displaybknewspnl.setMaximumSize(size4);
		pnlextrainfo.add (displaybknewspnl,0);
		
		showBanKuaiSocialFriendsWeeklyData (tableBkSocialbkSelectwkData,bkcur,selectdate);
		setUserSelectedColumnMessage(bkcur,selectdate);
		
		String ShowSelectBanKuaiColumnRemindInfo    = bkfxsettingprop.getProperty ("ShowSelectBanKuaiColumnRemindInfo");
		if(ShowSelectBanKuaiColumnRemindInfo   != null && ShowSelectBanKuaiColumnRemindInfo.equalsIgnoreCase("TRUE") )
			showReminderMessage (bkfxremind.getBankuaicolumnremind() );

		//根据设置，显示选定周分析数据
		if(!menuItemnonshowselectbkinfo.getText().contains("X"))	return;
		
		if(bkcur.getType() == BkChanYeLianTreeNode.DAPAN  ) return ; // maybe selected node is DaPan, which has no gegu.
	
		LocalDate cursetdate = dateChooser.getLocalDate(); 
		if(CommonUtility.isInSameWeek(selectdate,cursetdate) )		return;
		
			if(bkcur.getType() == BkChanYeLianTreeNode.TDXBK) {
//				DaPan dapan = (DaPan) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode("000000", BkChanYeLianTreeNode.DAPAN);
				List<BkChanYeLianTreeNode> bkwithcje = getBanKuaiFenXiQualifiedNodes(selectdate, globeperiod, globecalwholeweek,false);
				((BanKuaiInfoTableModel)tableselectedwkbkzb.getModel()).addBanKuai( bkwithcje );
				((BanKuaiInfoTableModel)tableselectedwkbkzb.getModel()).refresh(selectdate,globeperiod);
				
				unifiedTabbedPanSelectionActions (bkcur,selectdate);
			} else if(bkcur.getType() == BkChanYeLianTreeNode.DZHBK) {
//				DaPan dapan = (DaPan) CreateExchangeTree.CreateTreeOfDZHBanKuaiAndStocks().getSpecificNodeByHypyOrCode("000000", BkChanYeLianTreeNode.DAPAN);
				List<BkChanYeLianTreeNode> bkwithcje = getDZHBanKuaiFenXiQualifiedNodes(selectdate, globeperiod, globecalwholeweek,false);
				((BanKuaiInfoTableModel)tblDzhBkCurSelectedWkZhanBi.getModel()).addBanKuai( bkwithcje );
				((BanKuaiInfoTableModel)tblDzhBkCurSelectedWkZhanBi.getModel()).refresh(selectdate,globeperiod);
				
				unifiedTabbedPanSelectionActions (bkcur,selectdate);
			}
			
			findInputedNodeInBanKuaiTables (bkcur.getMyOwnCode());
			
			//显示板块个股
			if( ((BanKuai)bkcur).getBanKuaiLeiXing().equals(BanKuai.HASGGWITHSELFCJL) ) {//应该是有个股的板块点击才显示她的个股，
				LocalDate formerdate = ((BanKuaiGeGuBasicTableModel)tablexuandingzhou.getModel()).getCurDisplayedDate();
				if(formerdate != null)  //上一次的选定周数据挪到tab 4
					((BanKuaiGeGuTableModelFromPropertiesFile)tablexuandingplusone.getModel()).refresh(((BanKuai)bkcur), formerdate,globeperiod);
				
				//显示选定周股票排名情况
				((BanKuaiGeGuTableModelFromPropertiesFile)tablexuandingzhou.getModel()).refresh(((BanKuai)bkcur), selectdate,globeperiod);
				//显示选定周-1股票排名情况
				LocalDate selectdate2 = selectdate.minus(1,ChronoUnit.WEEKS).with(DayOfWeek.FRIDAY);
				((BanKuaiGeGuTableModelFromPropertiesFile)tablexuandingminusone.getModel()).refresh(((BanKuai)bkcur), selectdate2,globeperiod);
				//显示选定周-2股票排名情况
				LocalDate selectdate3 = selectdate.plus(1,ChronoUnit.WEEKS).with(DayOfWeek.FRIDAY);
				((BanKuaiGeGuTableModelFromPropertiesFile)tablexuandingminustwo.getModel()).refresh(((BanKuai)bkcur), selectdate3,globeperiod);

				preOperationForMainBkAndTempBkIntersection ();
			}
			
			String[] cjecjlzbgrkw = {"CjeZbGrowRate", "CjlZbGrowRate"};
			pnlCjeZbGrXuandingZhou.updatedDate(bkcur, selectdate.minus(1,ChronoUnit.WEEKS).with(DayOfWeek.MONDAY), selectdate.with(DayOfWeek.FRIDAY),
					NodeGivenPeriodDataItem.DAY, cjecjlzbgrkw);
	}
	/*
	 * 
	 */
	private void showBanKuaiSocialFriendsWeeklyData (BanKuaiInfoTable socialtable, BkChanYeLianTreeNode node,LocalDate curselectdate)
	{
//		((BanKuaiInfoTableModel)socialtable.getModel()).deleteAllRows();		
//		List<BkChanYeLianTreeNode> result = bksocialtreecopy.getSpecificNodeByHypyOrCodeList(node.getMyOwnCode(), node.getType());
//		for(BkChanYeLianTreeNode tmpnode : result ) {
//			CylTreeNestedSetNode ttmpnode = (CylTreeNestedSetNode)tmpnode;
//			BkChanYeLianTreeNode parent;
//			try {	parent = (BkChanYeLianTreeNode) ttmpnode.getParent();
//			} catch (java.lang.NullPointerException e) { return; }
//			
//			List<BkChanYeLianTreeNode> slidingnodelist = null;
//			if(parent.getType() != BkChanYeLianTreeNode.DAPAN) {
//				if(parent.getType() == BkChanYeLianTreeNode.DZHBK) {
//					try {
//						BkChanYeLianTreeNode bkparent = CreateExchangeTree.CreateTreeOfDZHBanKuaiAndStocks().getSpecificNodeByHypyOrCode(parent.getMyOwnCode(), BkChanYeLianTreeNode.DZHBK);
//						((BanKuaiInfoTableModel)socialtable.getModel()).addBanKuai ( (BanKuai)bkparent);
//					} catch ( java.lang.NullPointerException e) {e.printStackTrace();}
//				} else if(parent.getType() == BkChanYeLianTreeNode.TDXBK) {
//					try {
//						BkChanYeLianTreeNode bkparent = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(parent.getMyOwnCode(), BkChanYeLianTreeNode.TDXBK);
//						((BanKuaiInfoTableModel)socialtable.getModel()).addBanKuai ( (BanKuai)bkparent);
//					} catch ( java.lang.NullPointerException e) {e.printStackTrace();}
//				}
//				
//				slidingnodelist = bksocialtreecopy.getSlidingInChanYeLianInfo (node.getMyOwnCode(),node.getType());
//				for(BkChanYeLianTreeNode tmpnode : slidingnodelist) {
//					if(tmpnode.getType() == BkChanYeLianTreeNode.DZHBK) {
//						try {
//							BkChanYeLianTreeNode friend = CreateExchangeTree.CreateTreeOfDZHBanKuaiAndStocks().getSpecificNodeByHypyOrCode(tmpnode.getMyOwnCode(), BkChanYeLianTreeNode.DZHBK);
//							((BanKuaiInfoTableModel)socialtable.getModel()).addBanKuai ( (BanKuai)friend);
//						} catch ( java.lang.NullPointerException e) {e.printStackTrace();}
//					} else if(tmpnode.getType() == BkChanYeLianTreeNode.TDXBK) {
//						try {
//							BkChanYeLianTreeNode friend = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(tmpnode.getMyOwnCode(), BkChanYeLianTreeNode.TDXBK);
//							((BanKuaiInfoTableModel)socialtable.getModel()).addBanKuai ( (BanKuai)friend);
//						} catch ( java.lang.NullPointerException e) {e.printStackTrace();}
//					}
//				}
//			}
//			
//			List<BkChanYeLianTreeNode> childnodelist = bksocialtreecopy.getChildrenInChanYeLianInfo(node.getMyOwnCode(),node.getType());
//			for(BkChanYeLianTreeNode tmpnode : childnodelist) {
//				if(tmpnode.getType() == BkChanYeLianTreeNode.DZHBK) {
//					try {
//						BkChanYeLianTreeNode friend = CreateExchangeTree.CreateTreeOfDZHBanKuaiAndStocks().getSpecificNodeByHypyOrCode(tmpnode.getMyOwnCode(), BkChanYeLianTreeNode.DZHBK);
//						((BanKuaiInfoTableModel)socialtable.getModel()).addBanKuai ( (BanKuai)friend);
//					} catch ( java.lang.NullPointerException e) {e.printStackTrace();}
//				} else if(tmpnode.getType() == BkChanYeLianTreeNode.TDXBK) {
//					try {
//						BkChanYeLianTreeNode friend = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(tmpnode.getMyOwnCode(), BkChanYeLianTreeNode.TDXBK);
//						((BanKuaiInfoTableModel)socialtable.getModel()).addBanKuai ( (BanKuai)friend);
//					} catch ( java.lang.NullPointerException e) {e.printStackTrace();}
//				}
//			}
//		}
		if(node.getType() == BkChanYeLianTreeNode.DAPAN) return;
		
		BkChanYeLianTreeNode parent;
		try {	parent = (BkChanYeLianTreeNode)bksocialtreecopy.getSpecificNodeByHypyOrCode(node.getMyOwnCode(), node.getType()).getParent();
		} catch (java.lang.NullPointerException e) { return; }
		List<BkChanYeLianTreeNode> slidingnodelist = null;
		if(parent.getType() != BkChanYeLianTreeNode.DAPAN) {
			if(parent.getType() == BkChanYeLianTreeNode.DZHBK) {
				try {
					BkChanYeLianTreeNode bkparent = CreateExchangeTree.CreateTreeOfDZHBanKuaiAndStocks().getSpecificNodeByHypyOrCode(parent.getMyOwnCode(), BkChanYeLianTreeNode.DZHBK);
					((BanKuaiInfoTableModel)socialtable.getModel()).addBanKuai ( (BanKuai)bkparent);
				} catch ( java.lang.NullPointerException e) {e.printStackTrace();}
			} else if(parent.getType() == BkChanYeLianTreeNode.TDXBK) {
				try {
					BkChanYeLianTreeNode bkparent = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(parent.getMyOwnCode(), BkChanYeLianTreeNode.TDXBK);
					((BanKuaiInfoTableModel)socialtable.getModel()).addBanKuai ( (BanKuai)bkparent);
				} catch ( java.lang.NullPointerException e) {e.printStackTrace();}
			}
			
			slidingnodelist = bksocialtreecopy.getSlidingInChanYeLianInfo (node.getMyOwnCode(),node.getType());
			for(BkChanYeLianTreeNode tmpnode : slidingnodelist) {
				if(tmpnode.getType() == BkChanYeLianTreeNode.DZHBK) {
					try {
						BkChanYeLianTreeNode friend = CreateExchangeTree.CreateTreeOfDZHBanKuaiAndStocks().getSpecificNodeByHypyOrCode(tmpnode.getMyOwnCode(), BkChanYeLianTreeNode.DZHBK);
						((BanKuaiInfoTableModel)socialtable.getModel()).addBanKuai ( (BanKuai)friend);
					} catch ( java.lang.NullPointerException e) {e.printStackTrace();}
				} else if(tmpnode.getType() == BkChanYeLianTreeNode.TDXBK) {
					try {
						BkChanYeLianTreeNode friend = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(tmpnode.getMyOwnCode(), BkChanYeLianTreeNode.TDXBK);
						((BanKuaiInfoTableModel)socialtable.getModel()).addBanKuai ( (BanKuai)friend);
					} catch ( java.lang.NullPointerException e) {e.printStackTrace();}
				}
			}
		}
		
		List<BkChanYeLianTreeNode> childnodelist = bksocialtreecopy.getChildrenInChanYeLianInfo(node.getMyOwnCode(),node.getType());
		for(BkChanYeLianTreeNode tmpnode : childnodelist) {
			if(tmpnode.getType() == BkChanYeLianTreeNode.DZHBK) {
				try {
					BkChanYeLianTreeNode friend = CreateExchangeTree.CreateTreeOfDZHBanKuaiAndStocks().getSpecificNodeByHypyOrCode(tmpnode.getMyOwnCode(), BkChanYeLianTreeNode.DZHBK);
					((BanKuaiInfoTableModel)socialtable.getModel()).addBanKuai ( (BanKuai)friend);
				} catch ( java.lang.NullPointerException e) {e.printStackTrace();}
			} else if(tmpnode.getType() == BkChanYeLianTreeNode.TDXBK) {
				try {
					BkChanYeLianTreeNode friend = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(tmpnode.getMyOwnCode(), BkChanYeLianTreeNode.TDXBK);
					((BanKuaiInfoTableModel)socialtable.getModel()).addBanKuai ( (BanKuai)friend);
				} catch ( java.lang.NullPointerException e) {e.printStackTrace();}
			}
		}
		
		((BanKuaiInfoTableModel)socialtable.getModel()).refresh(curselectdate,globeperiod);
	}
	/*
	 * 
	 */
	private void preOperationForMainBkAndTempBkIntersection ()
	{
		BanKuai curmainbk = (BanKuai)((BanKuaiGeGuBasicTableModel)tableGuGuZhanBiInBk.getModel()).getCurDispalyBandKuai(); //突出和当前主板块的交集个股
		BanKuai tempbankuai = (BanKuai)((BanKuaiGeGuBasicTableModel)tableTempGeGu.getModel()).getCurDispalyBandKuai();
		
		if(curmainbk != null && tempbankuai != null ) {
			((BanKuaiGeGuBasicTableModel)tableTempGeGu.getModel()).setInterSectionBanKuai(curmainbk); 
			((BanKuaiGeGuBasicTableModel)tableGuGuZhanBiInBk.getModel()).setInterSectionBanKuai(tempbankuai);
			((BanKuaiGeGuBasicTableModel)tableExternalInfo.getModel()).setInterSectionBanKuai(tempbankuai);
			
			LocalDate xdzdate = ((BanKuaiGeGuBasicTableModel)tablexuandingzhou.getModel()).getCurDisplayedDate(); //突出和当前主板块的交集个股
			if(xdzdate != null) ((BanKuaiGeGuBasicTableModel)tablexuandingzhou.getModel()).setInterSectionBanKuai(tempbankuai);
			
			xdzdate = ((BanKuaiGeGuBasicTableModel)tablexuandingminusone.getModel()).getCurDisplayedDate(); //突出和当前主板块的交集个股
			if(xdzdate != null) ((BanKuaiGeGuBasicTableModel)tablexuandingminusone.getModel()).setInterSectionBanKuai(tempbankuai);
			
			xdzdate = ((BanKuaiGeGuBasicTableModel)tablexuandingminustwo.getModel()).getCurDisplayedDate(); //突出和当前主板块的交集个股
			if(xdzdate != null) ((BanKuaiGeGuBasicTableModel)tablexuandingminustwo.getModel()).setInterSectionBanKuai(tempbankuai);
			
			xdzdate = ((BanKuaiGeGuBasicTableModel)tablexuandingplusone.getModel()).getCurDisplayedDate(); //突出和当前主板块的交集个股
			if(xdzdate != null) ((BanKuaiGeGuBasicTableModel)tablexuandingplusone.getModel()).setInterSectionBanKuai(tempbankuai);
			
		} else 
		if(curmainbk != null && tempbankuai == null ) {
				((BanKuaiGeGuBasicTableModel)tableTempGeGu.getModel()).setInterSectionBanKuai(curmainbk); 
				((BanKuaiGeGuBasicTableModel)tableGuGuZhanBiInBk.getModel()).setInterSectionBanKuai(null);
				((BanKuaiGeGuBasicTableModel)tableExternalInfo.getModel()).setInterSectionBanKuai(null);
		} else
		if(curmainbk == null && tempbankuai != null ) {
				((BanKuaiGeGuBasicTableModel)tableTempGeGu.getModel()).setInterSectionBanKuai(null); 
				((BanKuaiGeGuBasicTableModel)tableGuGuZhanBiInBk.getModel()).setInterSectionBanKuai(tempbankuai);
				((BanKuaiGeGuBasicTableModel)tableExternalInfo.getModel()).setInterSectionBanKuai(tempbankuai);
				
				LocalDate xdzdate = ((BanKuaiGeGuBasicTableModel)tablexuandingzhou.getModel()).getCurDisplayedDate(); //突出和当前主板块的交集个股
				if(xdzdate != null) ((BanKuaiGeGuBasicTableModel)tablexuandingzhou.getModel()).setInterSectionBanKuai(tempbankuai);
				
				xdzdate = ((BanKuaiGeGuBasicTableModel)tablexuandingminusone.getModel()).getCurDisplayedDate(); //突出和当前主板块的交集个股
				if(xdzdate != null) ((BanKuaiGeGuBasicTableModel)tablexuandingminusone.getModel()).setInterSectionBanKuai(tempbankuai);
				
				xdzdate = ((BanKuaiGeGuBasicTableModel)tablexuandingminustwo.getModel()).getCurDisplayedDate(); //突出和当前主板块的交集个股
				if(xdzdate != null) ((BanKuaiGeGuBasicTableModel)tablexuandingminustwo.getModel()).setInterSectionBanKuai(tempbankuai);
				
				xdzdate = ((BanKuaiGeGuBasicTableModel)tablexuandingplusone.getModel()).getCurDisplayedDate(); //突出和当前主板块的交集个股
				if(xdzdate != null) ((BanKuaiGeGuBasicTableModel)tablexuandingplusone.getModel()).setInterSectionBanKuai(tempbankuai);
		}
	}
	/*
	 * 
	 */
	protected void saveBanKuaiDailyDataToDatabase() 
	{

		SvsForNodeOfBanKuai svsbk = new SvsForNodeOfBanKuai ();
	
//		for (BanKuaiandGeGuTableBasic tmpbktbl : tableallbktablesset ) {
//			if( ((BanKuaiInfoTableModel)tmpbktbl.getModel()).getRowCount() <=0)
//				continue;
//			
//			LocalDate curselectdate =  ((BanKuaiInfoTableModel)tmpbktbl.getModel()).getCurDisplayedDate();
//			int rowcount = ((BanKuaiInfoTableModel)tmpbktbl.getModel()).getRowCount();
//			int columnIndexCjeZbGr = ((BanKuaiInfoTableModel)tmpbktbl.getModel()).getKeyWrodColumnIndex ("CjeZbGrowRate");
//			int columnIndexCjlZbGr = ((BanKuaiInfoTableModel)tmpbktbl.getModel()).getKeyWrodColumnIndex ("CjlZbGrowRate");
//			for(int i=0;i<rowcount;i++) {
//				BanKuai bk = (BanKuai) ((BanKuaiInfoTableModel)tmpbktbl.getModel()).getNode(i);
//				Double cjezhgr = (Double) ((BanKuaiInfoTableModel)tmpbktbl.getModel()).getValueAt(i, columnIndexCjeZbGr);
//				if(cjezhgr == 100.0) 
//					continue; //新板块，数据无意义
//				
//				String[] kwlists =  {"CjeZbGrowRate","CjlZbGrowRate" }; 
//				svsbk.saveBanKuaiExtraDataToDatabase(bk, curselectdate,kwlists);
//			}
//		}
		
		LocalDate curselectdate = complexSolutionForDateChooserWithCalWholeWeek ();
		String title  = tabbedPanebk.getTitleAt(tabbedPanebk.getSelectedIndex() );
//		if(!title.contains("大智慧")) { //通达信
			if(((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getRowCount() <=0)
				return;
			int rowcount = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getRowCount();
			int columnIndexCjeZbGr = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getKeyWrodColumnIndex ("CjeZbGrowRate");
			int columnIndexCjlZbGr = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getKeyWrodColumnIndex ("CjlZbGrowRate");
			for(int i=0;i<rowcount;i++) {
				BanKuai bk = (BanKuai) ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getNode(i);
				Double cjezhgr = (Double) ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getValueAt(i, columnIndexCjeZbGr);
				if(cjezhgr == 100.0) 
					continue; //新板块，数据无意义
				
				String[] kwlists =  {"CjeZbGrowRate","CjlZbGrowRate" }; 
				svsbk.saveBanKuaiExtraDataToDatabase(bk, curselectdate,kwlists);
			}
//		} else { //大智慧
			if(((BanKuaiInfoTableModel)tblDzhBkCurWkZhanBi.getModel()).getRowCount() <=0)
				return;
			rowcount = ((BanKuaiInfoTableModel)tblDzhBkCurWkZhanBi.getModel()).getRowCount();
			columnIndexCjeZbGr = ((BanKuaiInfoTableModel)tblDzhBkCurWkZhanBi.getModel()).getKeyWrodColumnIndex ("CjeZbGrowRate");
			columnIndexCjlZbGr = ((BanKuaiInfoTableModel)tblDzhBkCurWkZhanBi.getModel()).getKeyWrodColumnIndex ("CjlZbGrowRate");
			for(int i=0;i<rowcount;i++) {
				BanKuai bk = (BanKuai) ((BanKuaiInfoTableModel)tblDzhBkCurWkZhanBi.getModel()).getNode(i);
				Double cjezhgr = (Double) ((BanKuaiInfoTableModel)tblDzhBkCurWkZhanBi.getModel()).getValueAt(i, columnIndexCjeZbGr);
				if(cjezhgr == 100.0) 
					continue; //新板块，数据无意义
				
				String[] kwlists =  {"CjeZbGrowRate","CjlZbGrowRate" }; 
				svsbk.saveBanKuaiExtraDataToDatabase(bk, curselectdate,kwlists);
			}
//		}
					
		svsbk = null;
	}
	/*
	 * 
	 */
	private void refreshDaPanFengXiResult ()
	{
		LocalDate curselectdate = null;
		try{	curselectdate = dateChooser.getLocalDate();// dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		} catch (java.lang.NullPointerException e) {	JOptionPane.showMessageDialog(null,"日期有误!","Warning",JOptionPane.WARNING_MESSAGE);
			return;
		}
		DaPan dapan = (DaPan) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode("000000", BkChanYeLianTreeNode.DAPAN);
    	dapan.getServicesForNode(true).getNodeData(dapan,CommonUtility.getSettingRangeDate(curselectdate,"large"), curselectdate,
				globeperiod,this.globecalwholeweek );
    	dapan.getServicesForNode(false);
		//板块成交额占比显示大盘的周平均成交额，以便和板块的周平均成交额对比
		panelbkwkcjezhanbi.setDisplayBarOfSpecificBanKuaiCjeCjlInsteadOfSelfCjeCjl (dapan); 
		//板块自身占比
		for(BarChartPanelDataChangedListener tmplistener : barchartpanelbankuaidatachangelisteners) 
				tmplistener.updatedDate(dapan, CommonUtility.getSettingRangeDate(curselectdate,"basic"),curselectdate,globeperiod);
		
		return;
	}
		/*
	 * 
	 */
	protected void unifiedOperationsForDisplayUserSelectedNodeOnStockPnl(TDXNodes selectnode)
	{
		Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
		setCursor(hourglassCursor);
		
		clearTheGuiBeforDisplayNewInfoSection3 ();

		this.panelGgDpCjeZhanBi.setDrawAverageDailyCjeOfWeekLine(true); //保证个股显示是上边是日均成交额，下边是占比线
		
		DaPan treeroot = (DaPan) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getModel().getRoot();
		panelGgDpCjeZhanBi.setDisplayBarOfSpecificBanKuaiCjeCjlInsteadOfSelfCjeCjl (treeroot); 
		
		LocalDate curselectdate = dateChooser.getLocalDate();
		if(this.globecalwholeweek)		curselectdate = curselectdate.with(DayOfWeek.SATURDAY);
		for (BarChartPanelDataChangedListener tmplistener : barchartpanelstockdatachangelisteners) {
			tmplistener.updatedDate(selectnode, CommonUtility.getSettingRangeDate(curselectdate, "basic"),curselectdate, globeperiod);
		}
		
		unifiedTabbedPanSelectionActions (selectnode,curselectdate);
		
		//显示K线
		pnlStockCandle.displayQueKou(true);
		pnlStockCandle.updatedDate(selectnode,CommonUtility.getSettingRangeDate(curselectdate, "basic"),curselectdate,NodeGivenPeriodDataItem.DAY );
		tabpnlKxian.setSelectedIndex(2);
		
		//同步板块和个股高亮的日期
		LocalDate curselecteddate = panelbkwkcjezhanbi.getCurSelectedDate();
		chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(curselecteddate));
		
		//在产业链树上寻找该个股
		cyltreecopy.searchAndLocateNodeInTree (selectnode);

		diaplayTDXNodeNewsToKXianPnl (selectnode);
		displayNodeInfo (selectnode);
		
		hourglassCursor = null;
		Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
		setCursor(hourglassCursor2);
	}
	/**
	 * 点击一个 个股
	 * @param selectstock
	 * @param selectedGeguTable
	 */
	protected void unifiedOperationsAfterUserSelectAStock(StockOfBanKuai selectstockofbankuai, Boolean readinfoout) 
	{
		Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
		setCursor(hourglassCursor);
		
		clearTheGuiBeforDisplayNewInfoSection3 ();

		Stock selectstock = selectstockofbankuai.getStock();
		combxstockcode.updateUserSelectedNode (selectstock);
		
		hightlightSpecificSector (selectstockofbankuai); //餅圖

		LocalDate curselectdate = dateChooser.getLocalDate();
		if(this.globecalwholeweek)		curselectdate = curselectdate.with(DayOfWeek.FRIDAY);
		
		unifiedTabbedPanSelectionActions (selectstock,curselectdate);
		
		this.panelGgDpCjeZhanBi.setDrawAverageDailyCjeOfWeekLine(true); //保证个股显示是上边是日均成交额，下边是占比线
		selectstock.getShuJuJiLuInfo().setHasReviewedToday(true);
		
		DaPan treeroot = (DaPan) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getModel().getRoot();
		panelGgDpCjeZhanBi.setDisplayBarOfSpecificBanKuaiCjeCjlInsteadOfSelfCjeCjl (treeroot); 
		
		for (BarChartPanelDataChangedListener tmplistener : barchartpanelstockdatachangelisteners) {
			tmplistener.updatedDate(selectstock, CommonUtility.getSettingRangeDate(curselectdate, "basic"),curselectdate, globeperiod);
		}
		//显示K线
		pnlStockCandle.displayQueKou(true);
		pnlStockCandle.updatedDate(selectstock,CommonUtility.getSettingRangeDate(curselectdate, "basic"),curselectdate,NodeGivenPeriodDataItem.DAY );
		tabpnlKxian.setSelectedIndex(2);
		
//		diaplayTDXNodeNewsToKXianPnl (selectstock);
//		refreshCurrentStockTags (selectstock);
		//同步板块和个股高亮的日期
		LocalDate curselecteddate = panelbkwkcjezhanbi.getCurSelectedDate();
		chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(curselecteddate));
		
		displayNodeInfo (selectstock);
		
		//在产业链树上寻找该个股
		cyltreecopy.searchAndLocateNodeInTree (selectstock);
		
		String ShouwGeGuRemindInfo     = bkfxsettingprop.getProperty ("ShouwGeGuRemindInfo");
		if(ShouwGeGuRemindInfo    != null && ShouwGeGuRemindInfo.toUpperCase().equals("TRUE") )
			showReminderMessage (bkfxremind.getStockremind());
		//语言播报
		String readingsettinginprop  = bkfxsettingprop.getProperty ("readoutfxresultinvoice");
		if(readingsettinginprop != null && readingsettinginprop.toUpperCase().equals("TRUE") && readinfoout)
			readAnalysisResult ( selectstockofbankuai.getBanKuai(),  selectstockofbankuai.getStock(), this.dateChooser.getLocalDate() );
		
		hourglassCursor = null;
		Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
		setCursor(hourglassCursor2);
	}
	/*
	 * 
	 */
	private void setupPredefinedExportFormula() {
		String PreSetExportConditionFormulaCount = bkfxsettingprop.getProperty("PreSetExportConditionFormulaCount");
		if(PreSetExportConditionFormulaCount == null  || Integer.parseInt(PreSetExportConditionFormulaCount) == 0)
			return ;
		List<String> formula = new ArrayList<> ();
		for(int i=0;i<Integer.parseInt(PreSetExportConditionFormulaCount);i++) {
			String PreSetExportConditionFormula  = bkfxsettingprop.getProperty ("PreSetExportConditionFormula" + String.valueOf(i+1) );
			if(PreSetExportConditionFormula != null)
				formula.add(PreSetExportConditionFormula);
		}
		if(!formula.isEmpty())
			this.bkggmatchcondition.setPredefinedExportConditionFormula (formula);
	}
	/*
	 * 
	 */
	private void readAnalysisResult(BanKuai banKuai, Stock stock, LocalDate analysisdate) 
	{
		String anaresult = WeeklyAnalysis.BanKuaiGeGuMatchConditionAnalysis(stock, analysisdate, this.bkggmatchcondition);
		
		if(analysisdate.equals(this.dateChooser.getLocalDate()) ) { //过去的信息就不要在读里面这些信息了，已经读过了
			if(!Strings.isNullOrEmpty(stock.getNodeJiBenMian().getGainiantishi()  ) 
					|| !Strings.isNullOrEmpty(stock.getNodeJiBenMian().getFumianxiaoxi()  )  )
				anaresult = anaresult + "有概念提示。";
			
			Collection<Tag> nodetags = stock.getNodeTags();
			if(nodetags != null && !nodetags.isEmpty() ) {
				anaresult = anaresult + "关键词有";
				for (Iterator<Tag> lit = nodetags.iterator(); lit.hasNext(); ) {
			        Tag f = lit.next();
			        anaresult = anaresult + f.getName() ;
			    }
			}
		}
		
		if(!Strings.isNullOrEmpty(anaresult) ) {
			readengine = new VoiceEngine (anaresult);
			readengine.execute();
		}
	}
	/*
	 * 
	 */
	private void clearGeGuTablesFilter ()	{
		tablebkggtableset.forEach(l -> l.resetTableHeaderFilter());
	}
	/*
	 * 板块和个股的K线，可以同时显示，用以对比研究
	 */
	private void diaplayTDXNodeNewsToKXianPnl (TDXNodes selectnode)
	{
		ServicesForNewsLabel svslabel = new NewsLabelServices ();
		ServicesForNews svsnews = new NewsServices ();
    	NewsCache newcache = new NewsCache (selectnode.getMyOwnCode(),svsnews,svslabel,LocalDate.now().minusMonths(6),LocalDate.now().plusMonths(6));
    	svsnews.setCache(newcache);
    	if(BkChanYeLianTreeNode.isBanKuai(selectnode) ) 	pnlBanKuaiCandle.displayNodeNewsToGui (newcache.produceNews() );
    	else		pnlStockCandle.displayNodeNewsToGui (newcache.produceNews() );
	}
		/*
	 * 
	 */
	private void displayStockSuoShuBanKuai(Stock selectstock) 
	{
		DisPlayNodeSuoShuBanKuaiListServices svsstkbk = new DisPlayNodeSuoShuBanKuaiListServices (selectstock);
		int row = tableBkZhanBi.getSelectedRow();
		if(row >0) {
			int modelRow = tableBkZhanBi.convertRowIndexToModel(row); 
			BanKuai bankuai = (BanKuai) ((BanKuaiInfoTableModel) tableBkZhanBi.getModel()).getNode(modelRow);
			svsstkbk.setHightSameNameBanKuai(bankuai);
		} else svsstkbk.setHightSameNameBanKuai(null);
		svsstkbk.setDateForInfoCurrentNode(this.dateChooser.getLocalDate());
		
		DisPlayNodeSuoShuBanKuaiListPanel stkbkpnl = new DisPlayNodeSuoShuBanKuaiListPanel (svsstkbk);
		editorPanebankuai.setViewportView(stkbkpnl);
		ScrollUtil.scroll(editorPanebankuai, ScrollUtil.BOTTOM);
		ScrollUtil.scroll(editorPanebankuai, ScrollUtil.LEFT);
		
		stkbkpnl.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
            	if (evt.getPropertyName().equals(DisPlayNodeSuoShuBanKuaiListPanel.URLSELECTED_PROPERTY)) {
            		String selbk = evt.getNewValue().toString();
    				String selbkcode;
    				if(selbk != null)	 selbkcode = selbk.trim().substring(1, 7);
    				else 	return;
    				
    				Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
					setCursor(hourglassCursor);
					
    				Boolean findbk = false;
					try	{
						if(((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getRowCount() <=0) 
                			gettBanKuaiZhanBiRangedByGrowthRateOfPeriod (dateChooser.getLocalDate(),globeperiod);
						
    					int rowindex = 0;
    					tableBkZhanBi.resetTableHeaderFilter ();
    					rowindex = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getNodeRowIndex(selbkcode);
    					
    					if(rowindex >0) {
        					int modelRow = tableBkZhanBi.convertRowIndexToView(rowindex);
        					tableBkZhanBi.setRowSelectionInterval(modelRow, modelRow);
        					tableBkZhanBi.scrollRectToVisible(new Rectangle(tableBkZhanBi.getCellRect(modelRow, 0, true)));
        					
        					//找到用户选择的板块
        					BkChanYeLianTreeNode selectedbk = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getNode(rowindex);
        					Integer alreadyin = ((JStockComboBoxModel)combxsearchbk.getModel()).hasTheNode(selectedbk.getMyOwnCode());
        					if(alreadyin == -1)	combxsearchbk.updateUserSelectedNode(selectedbk);
        					findbk = true;
        					
        					//找不到该股票
        					String stockcode = combxstockcode.getSelectedItem().toString().substring(0, 6);
        					clearGeGuTablesFilter ();
        					if(!findInputedNodeInStcokTables (stockcode) )
        						JOptionPane.showMessageDialog(null,"在某个股表内没有发现该股，可能在这个时间段内该股停牌","Warning",JOptionPane.WARNING_MESSAGE);
        				}
    				}	catch (java.lang.NullPointerException  e) { logger.info("板块列表中没有找到 " + selbkcode + "，请转到板块设置中修改该板块设置。");}
					if(!findbk) {
						try {
							if(((BanKuaiInfoTableModel)tblDzhBkCurWkZhanBi.getModel()).getRowCount() <=0) 
	                			gettDZHBanKuaiZhanBiRangedByGrowthRateOfPeriod (dateChooser.getLocalDate(),globeperiod);

	    					int rowindexindzh =0;
	    					try {
	        					tblDzhBkCurWkZhanBi.resetTableHeaderFilter ();
	        					rowindexindzh = ((BanKuaiInfoTableModel)tblDzhBkCurWkZhanBi.getModel()).getNodeRowIndex(selbkcode);
	        				} 	catch (java.lang.NullPointerException  e) { logger.info("板块列表中没有找到 " + selbkcode + "，请转到板块设置中修改该板块设置。");}
	    					if(rowindexindzh>0) {
	    						int modelRow = tblDzhBkCurWkZhanBi.convertRowIndexToView(rowindexindzh);
	    						tblDzhBkCurWkZhanBi.setRowSelectionInterval(modelRow, modelRow);
	    						tblDzhBkCurWkZhanBi.scrollRectToVisible(new Rectangle(tblDzhBkCurWkZhanBi.getCellRect(modelRow, 0, true)));
	        					
	        					//找到用户选择的板块
	        					BkChanYeLianTreeNode selectedbk = ((BanKuaiInfoTableModel)tblDzhBkCurWkZhanBi.getModel()).getNode(rowindexindzh);
	        					Integer alreadyin = ((JStockComboBoxModel)combxsearchbk.getModel()).hasTheNode(selectedbk.getMyOwnCode());
	        					if(alreadyin == -1)	combxsearchbk.updateUserSelectedNode(selectedbk);
//	        					unifiedOperationsAfterUserSelectABanKuai ( (BanKuai)selectedbk );
	        					findbk = true;
	        					//找不到该股票
	        					String stockcode = combxstockcode.getSelectedItem().toString().substring(0, 6);
	        					clearGeGuTablesFilter ();
	        					if(!findInputedNodeInStcokTables (stockcode) )
	        						JOptionPane.showMessageDialog(null,"在某个股表内没有发现该股，可能在这个时间段内该股停牌","Warning",JOptionPane.WARNING_MESSAGE);
	    					} 
	    				}	catch (java.lang.NullPointerException  e) { logger.info("板块列表中没有找到 " + selbkcode + "，请转到板块设置中修改该板块设置。");			;}
					}
					
					if(!findbk)	JOptionPane.showMessageDialog(null,"该板块设置为'不显示在板块列表'中，请在 ‘重点关注与产业链’ 变更设置！","Warning",JOptionPane.WARNING_MESSAGE);
					
					hourglassCursor = null;
    				Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
    				setCursor(hourglassCursor2);
    				
					playAnaylsisFinishNoticeSound();
            	}
            }
		});

		return;
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
                }
            }
		});

	}
	private void setUserSelectedColumnMessage(String htmlstring) 
	{
		pnl_paomd.refreshMessage(htmlstring);
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
	private  void displayAnalysisResultByDateChooser (LocalDate userselecteddate)
	{
		Boolean tdxtblhasdata = false; Boolean dzhtblhasdata = false;
		if(((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getRowCount() >0) 
			tdxtblhasdata = true;
		if(((BanKuaiInfoTableModel)tblDzhBkCurWkZhanBi.getModel()).getRowCount() >0)
			dzhtblhasdata = true;
			
		clearTheGuiBeforDisplayNewInfoSection1 ();
		clearTheGuiBeforDisplayNewInfoSection2 ();
		clearTheGuiBeforDisplayNewInfoSection3 ();
		
		if(lastselecteddate == null) { //user first set date
			String firstshowvendordata = bkfxsettingprop.getProperty ("firstshowvendordata");
    		if(firstshowvendordata == null || firstshowvendordata.equalsIgnoreCase("TDX")) {
    			unifiedOperationForDaPanSyncData (userselecteddate , NodeGivenPeriodDataItem.WEEK);
    			gettBanKuaiZhanBiRangedByGrowthRateOfPeriod (userselecteddate, NodeGivenPeriodDataItem.WEEK);
    			unifiedTabbedPanSelectionActions ( (BanKuai)CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode("999999", BkChanYeLianTreeNode.TDXBK)
    				,userselecteddate );
    		}
    		else	if(firstshowvendordata.equalsIgnoreCase("DZH")) {
    			gettDZHBanKuaiZhanBiRangedByGrowthRateOfPeriod (userselecteddate, NodeGivenPeriodDataItem.WEEK);
    			unifiedTabbedPanSelectionActions ( (BanKuai)CreateExchangeTree.CreateTreeOfDZHBanKuaiAndStocks().getSpecificNodeByHypyOrCode("999999", BkChanYeLianTreeNode.DZHBK)
	    				,userselecteddate );
    		}
    		
    		
		}	else  { 
			lbllastselect.setText(lastselecteddate.toString());
			unifiedOperationForDaPanSyncData (userselecteddate, globeperiod);
    		if(tdxtblhasdata || (!tdxtblhasdata &&  !dzhtblhasdata) ) 
    			gettBanKuaiZhanBiRangedByGrowthRateOfPeriod (dateChooser.getLocalDate(),globeperiod);
			if(dzhtblhasdata) 
    			gettDZHBanKuaiZhanBiRangedByGrowthRateOfPeriod (dateChooser.getLocalDate(),globeperiod);
		}
		
		lastselecteddate = userselecteddate;
		
		playAnaylsisFinishNoticeSound();
	}
	
	private Boolean isCurrentWeekOrPreviousWeek (LocalDate date)
	{
		return CommonUtility.isInSameWeek(LocalDate.now(), date);
	}
	/*
	 * 
	 */
	private void complexSolutionForCalWholeWeekStatusTransfer  (boolean globcalwkbefore, boolean globcalwkafter)
	{
		LocalDate userselecteddate = this.dateChooser.getLocalDate() ;
		DayOfWeek dayofweek = this.dateChooser.getLocalDate().getDayOfWeek();
		
		boolean iswkendbutnotfriday = false;
		if(dayofweek.equals(DayOfWeek.SUNDAY) || dayofweek.equals(DayOfWeek.SATURDAY)  )
			iswkendbutnotfriday = true;
		
		if(iswkendbutnotfriday) //不用重新计算 
			return;
		
		JOptionPane.showMessageDialog(null,"将重新计算数据，点击开始计算！","Warning",JOptionPane.WARNING_MESSAGE);
		
		this.globecalwholeweek = globcalwkafter;
		if(globecalwholeweek)	 yuanzhiOperationForGlobecalwholeweekOfTrue ();
		else	yuanzhiOperationForGlobecalwholeweekOfFalse ();
		
		displayAnalysisResultByDateChooser (userselecteddate);
	}
	/*
	 * 
	 */
	private void yuanzhiOperationForGlobecalwholeweekOfTrue ()
	{
		globecalwholeweek = true;
		
		chxbxwholeweek.setText("当前选择任何一天均计算完整周");
		chxbxwholeweek.setForeground(Color.BLACK);
		menuItemSaveCurWkDataToData.setEnabled(false);
		chxbxwholeweek.setSelected(true);
		
	}
	private void yuanzhiOperationForGlobecalwholeweekOfFalse ()
	{
		globecalwholeweek = false;
		
		chxbxwholeweek.setText("当前仅选择周末计算完整周");
		chxbxwholeweek.setForeground(Color.RED);
		chxbxwholeweek.setSelected(false);
		menuItemSaveCurWkDataToData.setEnabled(true);
		
		startRestartBackGroundDownload ();
	}
	/*
	 * 
	 */
	private LocalDate complexSolutionForDateChooserWithCalWholeWeek ()
	{
		LocalDate finialdate = null;
		LocalDate userselecteddate = this.dateChooser.getLocalDate() ;
		
		BanKuai shdapan = (BanKuai)CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode("999999", BkChanYeLianTreeNode.TDXBK);
		LocalDate sjjlmaxdate = shdapan.getShuJuJiLuInfo().getJyjlmaxdate();
		Boolean hasselecteddatedata = false;
		if(userselecteddate.isAfter(sjjlmaxdate)) hasselecteddatedata = false;
		else hasselecteddatedata = true;
		Boolean iscurwk = isCurrentWeekOrPreviousWeek (userselecteddate);
		Boolean iswkend = false; Boolean isworkingday = true;
		DayOfWeek dayofweek = this.dateChooser.getLocalDate().getDayOfWeek();
		if(dayofweek.equals(DayOfWeek.SUNDAY) || dayofweek.equals(DayOfWeek.SATURDAY) || dayofweek.equals(DayOfWeek.SATURDAY) )
			iswkend = true;
		if(dayofweek.equals(DayOfWeek.SUNDAY) || dayofweek.equals(DayOfWeek.SATURDAY)  )
			isworkingday = false;
		
		if( !this.globecalwholeweek) {
			if( isworkingday && !hasselecteddatedata)	finialdate = sjjlmaxdate;
			else if( isworkingday && hasselecteddatedata) finialdate = userselecteddate;
			
			if( !isworkingday  && !hasselecteddatedata)	finialdate = sjjlmaxdate;
			else	if( !isworkingday  && hasselecteddatedata)	finialdate = userselecteddate.with(DayOfWeek.FRIDAY);;
		}
		
		if( this.globecalwholeweek ) {
			if( isworkingday && !hasselecteddatedata)	finialdate = sjjlmaxdate;
			else if( isworkingday && hasselecteddatedata) finialdate = userselecteddate.with(DayOfWeek.FRIDAY);;
			
			if( !isworkingday  && !hasselecteddatedata)	finialdate = sjjlmaxdate;
			else	if( !isworkingday  && hasselecteddatedata)	finialdate = userselecteddate.with(DayOfWeek.FRIDAY);
		}

		return finialdate;
	}

	/*
	 * 
	 */
	private void createEvents() 
	{
		panelGgDpCjeZhanBi.setProperties(this.bkfxsettingprop);
		panelbkwkcjezhanbi.setProperties(this.bkfxsettingprop);
		panelggdpcjlwkzhanbi.setProperties(this.bkfxsettingprop);
		pnlbkwkcjlzhanbi.setProperties(this.bkfxsettingprop);
		
		panelGgDpCjeZhanBi.setChangeNodeDisplayDateRange(false);
		panelbkwkcjezhanbi.setChangeNodeDisplayDateRange(false);
		panelggdpcjlwkzhanbi.setChangeNodeDisplayDateRange(false);
		pnlbkwkcjlzhanbi.setChangeNodeDisplayDateRange(false);
		
		this.panelGgDpCjeZhanBi.setAllowDrawAnnoation(false);
		this.panelGgDpCjeZhanBi.setDrawQueKouLine(false);
		this.panelGgDpCjeZhanBi.setDrawZhangDieTingLine(false);
		this.panelGgDpCjeZhanBi.setDrawAverageDailyCjeOfWeekLine(true);
		this.panelGgDpCjeZhanBi.setDisplayZhanBiInLine(true);
		
		this.panelbkwkcjezhanbi.setAllowDrawAnnoation(false);
		this.panelbkwkcjezhanbi.setDisplayZhanBiInLine(true);
		
		this.pnlbkwkcjlzhanbi.setDisplayZhanBiInLine(true);
		
		this.panelggdpcjlwkzhanbi.setDisplayZhanBiInLine(true);
		
		dateChooser.addPropertyChangeListener(new PropertyChangeListener() {
		    @Override
		    public void propertyChange(PropertyChangeEvent e) {
		    	if("date".equals(e.getPropertyName() ) ) {
		    		JDateChooser wybieraczDat = (JDateChooser) e.getSource();
		    		LocalDate newdate = wybieraczDat.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		    		
		    		Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
					setCursor(hourglassCursor);
					
					displayAnalysisResultByDateChooser (newdate);
					
					hourglassCursor = null;
					Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
					setCursor(hourglassCursor2);
		    	}
		    }
		});
		
		chxbxwholeweek.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
				setCursor(hourglassCursor);
				
				if(chxbxwholeweek.isSelected() ) {
					complexSolutionForCalWholeWeekStatusTransfer (false, true);
				} else {
					complexSolutionForCalWholeWeekStatusTransfer (true, false);
				}
				
				hourglassCursor = null;
				Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
				setCursor(hourglassCursor2);
			}
		});
		
		menuItemSaveCurWkDataToData.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				if(menuItemSaveCurWkDataToData.isEnabled()) {
					Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
					setCursor(hourglassCursor);
					
					saveBanKuaiDailyDataToDatabase ();
					
					playAnaylsisFinishNoticeSound();
					hourglassCursor = null;
					Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
					setCursor(hourglassCursor2);
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
		menuItemTempGeGuClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				((BanKuaiGeGuBasicTableModel)tableTempGeGu.getModel()).deleteAllRows();
				
				((BanKuaiGeGuBasicTableModel)tableGuGuZhanBiInBk.getModel()).setInterSectionBanKuai(null); 
				((BanKuaiGeGuBasicTableModel)tableExternalInfo.getModel()).setInterSectionBanKuai(null); 
				((BanKuaiGeGuBasicTableModel)tablexuandingzhou.getModel()).setInterSectionBanKuai(null); 
				((BanKuaiGeGuBasicTableModel)tablexuandingminusone.getModel()).setInterSectionBanKuai(null); 
				((BanKuaiGeGuBasicTableModel)tablexuandingminustwo.getModel()).setInterSectionBanKuai(null); 
				((BanKuaiGeGuBasicTableModel)tablexuandingplusone.getModel()).setInterSectionBanKuai(null); 
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
		
		menuItemQiangShigg.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				StockOfBanKuai stock = getSelectedStockOfBanKuai ();
				if(stock == null) return;
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
				StockOfBanKuai stock = getSelectedStockOfBanKuai ();
				if(stock == null) return;
        		QiangShi qs =  new 	QiangShi(stock.getStock(), "描述", LocalDate.now(), LocalDate.now(), "详细描述", "强势个股",new HashSet<>(),"URL");
        		ServicesForNews svsqs = new RuoShiServices ();
        		CreateExternalNewsDialog createnewDialog = new CreateExternalNewsDialog (svsqs);
                createnewDialog.setNews(qs);
                createnewDialog.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
                createnewDialog.setVisible(true);
			}
		});
		
		ActionListener ActionListenerForCompareToOtherBk = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				String danpanzhishu = JOptionPane.showInputDialog(null,"请输入对比板块代码", "      ");
				if(Strings.isNullOrEmpty(danpanzhishu) ) return;
				
            	BanKuai selectedbk = (BanKuai) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(danpanzhishu.toLowerCase(), BkChanYeLianTreeNode.TDXBK );
            	if(selectedbk == null)	selectedbk = (BanKuai) CreateExchangeTree.CreateTreeOfDZHBanKuaiAndStocks().getSpecificNodeByHypyOrCode(danpanzhishu.toLowerCase(), BkChanYeLianTreeNode.DZHBK );
            	if(selectedbk == null)  {	JOptionPane.showMessageDialog(null,"指数代码有误！","Warning",JOptionPane.WARNING_MESSAGE);	return;}

            	ServicesForNodeBanKuai svsbk = (ServicesForNodeBanKuai) selectedbk.getBanKuaiService (true);
                LocalDate curselectdate = dateChooser.getLocalDate();
        		//一次性同步板块数据以及所属个股数据
        		try {	svsbk.syncBanKuaiAndItsStocksForSpecificTime(selectedbk, CommonUtility.getSettingRangeDate(curselectdate,"large"), curselectdate,
        					globeperiod,globecalwholeweek);
        		} catch (SQLException e) {	e.printStackTrace(); }
        		svsbk = null;
            	unifiedOperationsForDisplayUserSelectedNodeOnStockPnl (selectedbk);
			}
		};
		menuItemcompareToOtherBk.addActionListener(ActionListenerForCompareToOtherBk);
		menuItemcompareToOtherBkDZH.addActionListener(ActionListenerForCompareToOtherBk);

		menuItemQiangShibk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				BanKuai bankuai = getSelectedTDXBanKuai ();
				if(bankuai ==null) return;
				QiangShi qs =  new 	QiangShi(bankuai, "描述", LocalDate.now(), LocalDate.now(), "详细描述", "强势个股",new HashSet<>(),"URL");
        		ServicesForNews svsqs = new QiangShiServices ();
        		CreateExternalNewsDialog createnewDialog = new CreateExternalNewsDialog (svsqs);
                createnewDialog.setNews(qs);
                createnewDialog.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
                createnewDialog.setVisible(true);
			}
		});
		menuItemQiangShibkDZH.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				BanKuai bankuai = getSelectedDZHBanKuai ();
				if(bankuai ==null) return;
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
				BanKuai bankuai = getSelectedTDXBanKuai ();
				QiangShi qs =  new 	QiangShi(bankuai, "描述", LocalDate.now(), LocalDate.now(), "详细描述", "强势个股",new HashSet<>(),"URL");
        		ServicesForNews svsqs = new RuoShiServices ();
        		CreateExternalNewsDialog createnewDialog = new CreateExternalNewsDialog (svsqs);
                createnewDialog.setNews(qs);
                createnewDialog.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
                createnewDialog.setVisible(true);
			}
		});
		menuItemRuoShibkDZH.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				BanKuai bankuai = getSelectedDZHBanKuai ();
				QiangShi qs =  new 	QiangShi(bankuai, "描述", LocalDate.now(), LocalDate.now(), "详细描述", "强势个股",new HashSet<>(),"URL");
        		ServicesForNews svsqs = new RuoShiServices ();
        		CreateExternalNewsDialog createnewDialog = new CreateExternalNewsDialog (svsqs);
                createnewDialog.setNews(qs);
                createnewDialog.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
                createnewDialog.setVisible(true);
			}
		});
	
		btnexportcsv.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				startupExportToCsv ();
			}
		});
		
		pnlStockCandle.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {

                if (evt.getPropertyName().equals(BanKuaiFengXiCandlestickPnl.ZHISHU_PROPERTY)) {
                    @SuppressWarnings("unchecked")
                    
                    TDXNodes stockofbank = pnlStockCandle.getCurDisplayedNode ();
                    
                    BanKuai selectedbk = null;
                    String zhishuinfo = evt.getNewValue().toString();
                    if(zhishuinfo.toLowerCase().equals("bankuaizhisu") ) {
                    	int row = tableBkZhanBi.getSelectedRow();
        				if(row <0) {JOptionPane.showMessageDialog(null,"请选择一个板块！","Warning",JOptionPane.WARNING_MESSAGE);	return;}
        				int modelRow = tableBkZhanBi.convertRowIndexToModel(row);
        				selectedbk = (BanKuai) ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getNode(modelRow);
                    } else if(zhishuinfo.toLowerCase().equals("dapanzhishu") ) {
                    	String danpanzhishu = JOptionPane.showInputDialog(null,"请输入叠加的大盘指数", "999999");
                    	selectedbk = (BanKuai) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(danpanzhishu.toLowerCase(), BkChanYeLianTreeNode.TDXBK );
                    	if(selectedbk == null)  {	JOptionPane.showMessageDialog(null,"指数代码有误！","Warning",JOptionPane.WARNING_MESSAGE);	return;}
                    } else if(zhishuinfo.toLowerCase().equals("zhishuguanjianriqi") ) {
//                		Collection<InsertedMeeting> zhishukeylists = newsdbopt.getZhiShuKeyDates(null, null);
//                		paneldayCandle.updateZhiShuKeyDates (zhishukeylists);
                    }
                    
                    LocalDate curselectdate = dateChooser.getLocalDate();
            		if(globecalwholeweek)		curselectdate = curselectdate.with(DayOfWeek.SATURDAY);
                    pnlStockCandle.displayQueKou(true);
                    pnlStockCandle.updatedDate(selectedbk,stockofbank,CommonUtility.getSettingRangeDate(curselectdate, "basic"),curselectdate,NodeGivenPeriodDataItem.DAY);

                } 
             }
        });

		menuItemDuanQiGuanZhu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
//            	int row = tableBkZhanBi.getSelectedRow();
//    			int modelRow = tableBkZhanBi.convertRowIndexToModel(row);
//    			BanKuai bk = (BanKuai) ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getNode(modelRow);
    			
            	BanKuai bk = getSelectedTDXBanKuai ();
            	if(bk == null) return;
    			DuanQiGuanZhu dqgz = new DuanQiGuanZhu(bk, "描述", dateChooser.getLocalDate(), dateChooser.getLocalDate(), "详细描述", "",  new HashSet<>(),"URL");
    			ServicesForNewsLabel svslabel = new NewsLabelServices ();
    			DuanQiGuanZhuServices svsdqgz = new DuanQiGuanZhuServices ();
    	    	NewsCache dqgzcache = new NewsCache ("ALL",svsdqgz,svslabel,LocalDate.now().minusMonths(6),LocalDate.now().plusMonths(6));
    	    	svsdqgz.setCache(dqgzcache);
    			CreateExternalNewsDialog createnewDialog = new CreateExternalNewsDialog (svsdqgz);
                createnewDialog.setNews(dqgz);
                createnewDialog.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width)/2 - getWidth()/2, (Toolkit.getDefaultToolkit().getScreenSize().height)/2 - getHeight()/2);
                createnewDialog.setVisible(true);
            }
        });

		menuItemsiglebktocsv.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	BanKuai bk = getSelectedTDXBanKuai ();
            	if(bk == null) return;
    			List<TDXNodes> tmplist = new ArrayList<TDXNodes> ();
    			tmplist.add(bk);
    			exportTDXNodesDataToCsv (tmplist,"single");
    			tmplist = null;
            }
        });
		menuItemsiglebktocsvDZH.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	BanKuai bk = getSelectedDZHBanKuai ();
            	if(bk == null) return;
    			List<TDXNodes> tmplist = new ArrayList<TDXNodes> ();
    			tmplist.add(bk);
    			exportTDXNodesDataToCsv (tmplist,"single");
    			tmplist = null;
            }
        });
		
		stkhistorycsvfileMenu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
//				int row = tableGuGuZhanBiInBk.getSelectedRow();
//				if(row <0) {	JOptionPane.showMessageDialog(null,"请选择一个板块","Warning",JOptionPane.WARNING_MESSAGE);
//					return;
//				}
//				int modelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(row); 
//				StockOfBanKuai bstkofbk = (StockOfBanKuai) ((BanKuaiGeGuBasicTableModel)tableGuGuZhanBiInBk.getModel()).getNode(modelRow);
				StockOfBanKuai bstkofbk = getSelectedStockOfBanKuai ();
				if(bstkofbk == null) return;
				getBanKuaiHistoryCsvAndAnalysisFile ((JMenu)e.getSource(),bstkofbk.getStock());
			}
			
			public void mouseClicked(MouseEvent e) {}
		});
		
		historycsvfileMenu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
//				int row = tableBkZhanBi.getSelectedRow();
//				int modelRow = tableBkZhanBi.convertRowIndexToModel(row);
//				BanKuai bk = (BanKuai) ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getNode(modelRow);
				BanKuai bk =   getSelectedTDXBanKuai ();
				if(bk ==null) return;
				getBanKuaiHistoryCsvAndAnalysisFile ((JMenu)e.getSource(),bk);
			}
			
			public void mouseClicked(MouseEvent e) {}
		});
		menuItemsGeGuToBksXuanDingZhou.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	int row = tablexuandingzhou.getSelectedRow();
            	if(row <0) {	JOptionPane.showMessageDialog(null,"请选择一个股票！","Warning",JOptionPane.WARNING_MESSAGE);
					return;
            	}
    			int modelRow = tablexuandingzhou.convertRowIndexToModel(row);
    			StockOfBanKuai selectstock = (StockOfBanKuai) ((BanKuaiGeGuBasicTableModel)tablexuandingzhou.getModel()).getNode (modelRow);
            	
    			LocalDate curdisplaydate = ((BanKuaiGeGuBasicTableModel)tablexuandingzhou.getModel()).getCurDisplayedDate();
    			calculateAllGeGuBanKuaiInfo (selectstock.getStock(),curdisplaydate);
    			tabpnlKxian.setTitleAt(3, selectstock.getMyOwnCode() + "所有板块信息(" + curdisplaydate.toString() + ")" );
    			tabpnlKxian.setSelectedIndex(3);
            }
        });
		menuItemsGeGuToBks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//            	int row = tableGuGuZhanBiInBk.getSelectedRow();
//            	if(row <0) {	JOptionPane.showMessageDialog(null,"请选择一个股票！","Warning",JOptionPane.WARNING_MESSAGE);
//					return;
//            	}
//    			int modelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(row);
//    			StockOfBanKuai selectstock = (StockOfBanKuai) ((BanKuaiGeGuBasicTableModel)tableGuGuZhanBiInBk.getModel()).getNode (modelRow);
            	StockOfBanKuai selectstock = getSelectedStockOfBanKuai ();
            	if(selectstock == null) return;
    			LocalDate curselectdate = dateChooser.getLocalDate();
    			calculateAllGeGuBanKuaiInfo (selectstock.getStock(),curselectdate);
    			tabpnlKxian.setTitleAt(3, selectstock.getMyOwnCode() + "所有板块信息(" + curselectdate.toString() + ")" );
    			tabpnlKxian.setSelectedIndex(3);
            }
        });
		menuItemsMrjh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//            	int row = tableGuGuZhanBiInBk.getSelectedRow();
//            	if(row <0) {
//            		JOptionPane.showMessageDialog(null,"请选择一个股票！","Warning",JOptionPane.WARNING_MESSAGE);
//					return;
//            	}
//    			int modelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(row);
//    			StockOfBanKuai selectstock = (StockOfBanKuai) ((BanKuaiGeGuBasicTableModel)tableGuGuZhanBiInBk.getModel()).getNode (modelRow);
    			
    			StockOfBanKuai selectstock = getSelectedStockOfBanKuai ();
    			if(selectstock == null) return;
    			String mrjh = JOptionPane.showInputDialog(null,"请输入明日计划内容:","明日计划", JOptionPane.QUESTION_MESSAGE);
    			if(mrjh.isEmpty()) 	return;
    			
    			PaoMaDengServices pmdsvs = new PaoMaDengServices ();
    			pmdsvs.setPaoMaDengInfo(selectstock.getStock(), mrjh);
            }
        });
		menuItemsiglestocktocsv.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//            	int row = tableGuGuZhanBiInBk.getSelectedRow();
//            	if(row <0) {
//            		JOptionPane.showMessageDialog(null,"请选择一个股票！","Warning",JOptionPane.WARNING_MESSAGE);
//					return;
//            	}
//    			int modelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(row);
//    			StockOfBanKuai selectstock = (StockOfBanKuai) ((BanKuaiGeGuBasicTableModel)tableGuGuZhanBiInBk.getModel()).getNode (modelRow);
    			
    			StockOfBanKuai selectstock = getSelectedStockOfBanKuai ();
    			if(selectstock == null) return;
    			List<TDXNodes> tmplist = new ArrayList<TDXNodes> ();
    			tmplist.add(selectstock.getStock() );
    			exportTDXNodesDataToCsv (tmplist,"single");
    			tmplist = null;
            }
        });
		menuItemAddRmvStockToRed.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
//            	int row = tableGuGuZhanBiInBk.getSelectedRow();
//            	if(row <0) {
//            		JOptionPane.showMessageDialog(null,"请选择一个股票！","Warning",JOptionPane.WARNING_MESSAGE);
//					return;
//            	}
//    			int modelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(row);
//    			StockOfBanKuai selectstock = (StockOfBanKuai) ((BanKuaiGeGuBasicTableModel)tableGuGuZhanBiInBk.getModel()).getNode (modelRow);
    			
    			StockOfBanKuai selectstock = getSelectedStockOfBanKuai ();
    			if(selectstock == null) return;
    			Stock stock = selectstock.getStock();
    			NodesTreeRelated filetree = stock.getNodeTreeRelated ();
//    			String colorcode = String.format("#%02x%02x%02x", Color.RED.getRed(), Color.RED.getGreen(), Color.RED.getGreen() );
//    			if(filetree.selfIsMatchModel(dateChooser.getLocalDate() ,colorcode ) ) 
//    				filetree.setSelfIsMatchModel(dateChooser.getLocalDate(), colorcode,false);
//    			else
//    				filetree.setSelfIsMatchModel(dateChooser.getLocalDate(), colorcode, true);
    			if(filetree.selfIsMatchModel(dateChooser.getLocalDate() ,Color.RED ) ) 
    				filetree.setSelfIsMatchModel(dateChooser.getLocalDate(), Color.RED,false);
    			else
    				filetree.setSelfIsMatchModel(dateChooser.getLocalDate(), Color.RED, true);
    			
    			tableGuGuZhanBiInBk.repaint();
            }
        });
		menuItemAddRmvBkToYellow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	BanKuai bk =  getSelectedTDXBanKuai ();
            	if( bk ==null) return;
    			NodesTreeRelated filetree = bk.getNodeTreeRelated ();
    			if(filetree.selfIsMatchModel(dateChooser.getLocalDate() ,Color.YELLOW ) ) 
    				filetree.setSelfIsMatchModel(dateChooser.getLocalDate(), Color.YELLOW,false);
    			else
    				filetree.setSelfIsMatchModel(dateChooser.getLocalDate(), Color.YELLOW, true);
    			
    			tableBkZhanBi.repaint();
            }
        });
		menuItemAddRmvBkToYellowDZH.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	BanKuai bk =  getSelectedDZHBanKuai ();
            	if( bk ==null) return;
    			NodesTreeRelated filetree = bk.getNodeTreeRelated ();
    			if(filetree.selfIsMatchModel(dateChooser.getLocalDate() ,Color.YELLOW ) ) 
    				filetree.setSelfIsMatchModel(dateChooser.getLocalDate(), Color.YELLOW,false);
    			else
    				filetree.setSelfIsMatchModel(dateChooser.getLocalDate(), Color.YELLOW, true);
    			
    			tableBkZhanBi.repaint();
            }
        });

		menuItemAddRmvBkToRedSign.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
    			BanKuai bk =  getSelectedTDXBanKuai ();
    			if( bk ==null) return;
    			NodesTreeRelated filetree = bk.getNodeTreeRelated ();
    			if(filetree.selfIsMatchModel(dateChooser.getLocalDate() ,Color.RED ) ) 
    				filetree.setSelfIsMatchModel(dateChooser.getLocalDate(), Color.RED,false);
    			else
    				filetree.setSelfIsMatchModel(dateChooser.getLocalDate(), Color.RED, true);
    			
    			tableBkZhanBi.repaint();
            }
        });
		menuItemAddRmvBkToRedSignDZH.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
    			BanKuai bk =  getSelectedDZHBanKuai ();
    			if( bk ==null) return;
    			NodesTreeRelated filetree = bk.getNodeTreeRelated ();
    			if(filetree.selfIsMatchModel(dateChooser.getLocalDate() ,Color.RED ) ) 
    				filetree.setSelfIsMatchModel(dateChooser.getLocalDate(), Color.RED,false);
    			else
    				filetree.setSelfIsMatchModel(dateChooser.getLocalDate(), Color.RED, true);
    			
    			tableBkZhanBi.repaint();
            }
        });
		
		menuItemAddRmvStockToYellow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
    			StockOfBanKuai selectstock = getSelectedStockOfBanKuai ();
    			if(selectstock == null) return;
    			Stock stock = selectstock.getStock();
    			NodesTreeRelated filetree = stock.getNodeTreeRelated ();
    			if(filetree.selfIsMatchModel(dateChooser.getLocalDate() ,Color.YELLOW ) ) 
    				filetree.setSelfIsMatchModel(dateChooser.getLocalDate(), Color.YELLOW,false);
    			else
    				filetree.setSelfIsMatchModel(dateChooser.getLocalDate(), Color.YELLOW, true);
    			
    			tableGuGuZhanBiInBk.repaint();
            }
        });
		
		menuItemnonshowselectbkinfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if( menuItemnonshowselectbkinfo.getText().contains("X") ) {
            		menuItemnonshowselectbkinfo.setText("选择同时计算该周分析数据");
            		panelbkwkcjezhanbi.setAllowDrawAnnoation(false);
            		pnlbkwkcjlzhanbi.setAllowDrawAnnoation(false);
            	} else {
            		menuItemnonshowselectbkinfo.setText("X选择同时计算该周分析数据");
            		panelbkwkcjezhanbi.setAllowDrawAnnoation(true);
            		pnlbkwkcjlzhanbi.setAllowDrawAnnoation(true);
            	}
            }
        });
		
		menuItemnonfixperiod.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	calStockNoneFixPeriodDpMXXWk ();
            }
        });
		menuItemmarkallreviewedtoday.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	int selectindex = tabbedPanegegu.getSelectedIndex();
            	if(selectindex <6) {
            		List<BkChanYeLianTreeNode> allgegu = ((BanKuaiGeGuTableModelFromPropertiesFile)tableGuGuZhanBiInBk.getModel()).getAllNodes();
        			if(allgegu == null  || allgegu.size() == 0)		 ;
        			else
        				for(BkChanYeLianTreeNode tmpnode : allgegu) 
        					((StockOfBanKuai)tmpnode).getStock().getShuJuJiLuInfo().setHasReviewedToday(true);
            	}
            	if(selectindex ==6) {
            		List<BkChanYeLianTreeNode> allgegu = ((BanKuaiGeGuTableModelFromPropertiesFile)tableTempGeGu.getModel()).getAllNodes();
        			if(allgegu == null  || allgegu.size() == 0)		 ;
        			else
        				for(BkChanYeLianTreeNode tmpnode : allgegu) 
        					((StockOfBanKuai)tmpnode).getStock().getShuJuJiLuInfo().setHasReviewedToday(true);
            	}
    			
    			tablebkggtableset.forEach(l -> l.repaint());
            }
        });
        menuItemcancelreviewedtoday.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	int selectindex = tabbedPanegegu.getSelectedIndex();
            	if(selectindex <6) {
            		List<BkChanYeLianTreeNode> allgegu = ((BanKuaiGeGuTableModelFromPropertiesFile)tableGuGuZhanBiInBk.getModel()).getAllNodes();
        			if(allgegu == null  || allgegu.size() == 0)		 ;
        			else
        				for(BkChanYeLianTreeNode tmpnode : allgegu) 
        					((StockOfBanKuai)tmpnode).getStock().getShuJuJiLuInfo().setHasReviewedToday(false);

            	}
            	if(selectindex ==6) {
            		List<BkChanYeLianTreeNode> allgegu = ((BanKuaiGeGuTableModelFromPropertiesFile)tableTempGeGu.getModel()).getAllNodes();
        			if(allgegu == null  || allgegu.size() == 0)		 ;
        			else
        				for(BkChanYeLianTreeNode tmpnode : allgegu) 
        					((StockOfBanKuai)tmpnode).getStock().getShuJuJiLuInfo().setHasReviewedToday(false);
            	}
    			tablebkggtableset.forEach(l -> l.repaint());
            }
        });
        
        menuItemcancelBanKaiReviewedtoday.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
    			List<BkChanYeLianTreeNode> allgegu = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getAllNodes();
    			if(allgegu == null  || allgegu.size() == 0)		 ;
    			else
    				for(BkChanYeLianTreeNode tmpnode : allgegu) 
    					((BanKuai)tmpnode).getShuJuJiLuInfo().setHasReviewedToday(false);
    			
    			tableBkZhanBi.repaint();
    			tableselectedwkbkzb.repaint();
            }
        });
        menuItemcancelAllNodesReviewedtoday.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getModel().getRoot();
        		int bankuaicount = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getModel().getChildCount(treeroot);
        		for(int i=0;i< bankuaicount; i++) {
        			
        			BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getModel().getChild(treeroot, i);
        			if(childnode.getType() != BkChanYeLianTreeNode.TDXBK || childnode.getType() != BkChanYeLianTreeNode.TDXGG) 
        				continue;
        			
        			if( !((BanKuai)childnode).getBanKuaiOperationSetting().isShowinbkfxgui() )
        				continue;
        			
        			((TDXNodes)childnode).getShuJuJiLuInfo().setHasReviewedToday(false);
        		}
    			
    			tableBkZhanBi.repaint();
    			tableselectedwkbkzb.repaint();
            }
        });
        
        menuItemtimerangezhangfu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//            	menuItemchengjiaoer.setText("按成交额排名");
//            	menuItemliutong.setText("按流通市值排名");
            	
            	DateRangeSelectPnl datachoose = new DateRangeSelectPnl ( LocalDate.now().minusDays(1), LocalDate.now() ); 
        		JOptionPane.showMessageDialog(null, datachoose,"选择时间段", JOptionPane.OK_CANCEL_OPTION);
        		
        		Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
    			setCursor(hourglassCursor);
        		
        		LocalDate searchstart = datachoose.getDatachoosestart();
        		LocalDate searchend = datachoose.getDatachooseend();
        		
        		menuItemtimerangezhangfu.setText("X 按阶段涨幅排名" + searchstart.toString() + "~" + searchend.toString());
        		
        		BanKuai bk = null;
        		String selecttitle = tabbedPanegegu.getTitleAt( tabbedPanegegu.getSelectedIndex() );
        		if(selecttitle.contains("临时")) 
        			bk = (BanKuai)((BanKuaiGeGuBasicTableModel)tableTempGeGu.getModel()).getCurDispalyBandKuai();
        		else {
        			int row = tableBkZhanBi.getSelectedRow();
        			int modelRow = tableBkZhanBi.convertRowIndexToModel(row);
        			bk = (BanKuai) ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getNode(modelRow);
        		}
        		if(bk == null)  return;
        		
    			if(selecttitle.contains("临时")) {
    				((BandKuaiAndGeGuTableBasicModel)tableTempGeGu.getModel()).setTimeRangeZhangFuDates (searchstart,searchend);
    				((BanKuaiGeGuBasicTableModel)tableTempGeGu.getModel()).sortTableByKeywords("TimeRangeZhangFu",false);
    			}
    			else {
    				for( BanKuaiGeGuBasicTable tmptbl : tablebkggtablesetWithoutTemp) {
    					((BandKuaiAndGeGuTableBasicModel)tmptbl.getModel()).setTimeRangeZhangFuDates (searchstart,searchend);
        				((BanKuaiGeGuBasicTableModel)tmptbl.getModel()).sortTableByKeywords("TimeRangeZhangFu",false);
    				}
    			}
    			
    			hourglassCursor = null;
				Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
    			setCursor(hourglassCursor2);
            }
        });

        tabbedPanebk.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                if (e.getButton() == MouseEvent.BUTTON1) {
                	Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
        			setCursor(hourglassCursor);
        			
                	String selecttitle = tabbedPanebk.getTitleAt( tabbedPanebk.getSelectedIndex() ).trim();
                	switch(selecttitle) {
                	case "当前周":	
                		if(((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getRowCount() <=0) {
	                			gettBanKuaiZhanBiRangedByGrowthRateOfPeriod (dateChooser.getLocalDate(),globeperiod);
                			playAnaylsisFinishNoticeSound();
                		}
                	break;
                	case "选定周":	tabbedPanegegu.setSelectedIndex(2);
                	break;
                	case "大智慧当前周":	
                		if(((BanKuaiInfoTableModel)tblDzhBkCurWkZhanBi.getModel()).getRowCount() <=0) {
                			gettDZHBanKuaiZhanBiRangedByGrowthRateOfPeriod (dateChooser.getLocalDate(),globeperiod);
                			playAnaylsisFinishNoticeSound();
                		}
                	break;
                	case "大智慧选定周":	tabbedPanegegu.setSelectedIndex(2);
                	break;
                	}
                	
                	hourglassCursor = null;
    				Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
        			setCursor(hourglassCursor2);
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                	 jPopupMenuoftabbedpanebk.show(tabbedPanebk, e.getX(),   e.getY());
                }
            }

        });

		tabbedPanegegu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
//                    pane.setSelectedComponent(panel);
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                	jPopupMenuoftabbedpane.show(tabbedPanegegu, e.getX(),   e.getY());
                }
            }
        });
		
		tabbedPanegegu.addChangeListener(new ChangeListener() {
	        public void stateChanged(ChangeEvent e) {
	            int i = tabbedPanegegu.getSelectedIndex();
	            Component sclp = tabbedPanegegu.getComponentAt (i);
	            JViewport viewport = ((JScrollPane)sclp).getViewport(); 
	            BanKuaiGeGuBasicTable tblgg = (BanKuaiGeGuBasicTable)viewport.getView(); 
	            LocalDate selectdate = ((BandKuaiAndGeGuTableBasicModel)tblgg.getModel()).getCurDisplayedDate();
	            if(selectdate != null && !CommonUtility.isInSameWeek(selectdate,dateChooser.getLocalDate() ) )
	            	chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(selectdate));
	        }
	    });
		
		
		cyltreecopy.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
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
    	    	String nodecode = node.getMyOwnCode();
    	    	Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
    			setCursor(hourglassCursor);
    			BanKuai bankuai = null;;
    	    	if( node != null && node.getType() == BkChanYeLianTreeNode.TDXBK ) {
					int rowindex = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getNodeRowIndex( nodecode );
					if(rowindex == -1) {
						JOptionPane.showMessageDialog(null,"板块不在分析表中，可到板块设置修改！","Warning", JOptionPane.WARNING_MESSAGE);
					}
        			
					bankuai = (BanKuai) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(node.getMyOwnCode(), BkChanYeLianTreeNode.TDXBK );
    	    	} if( node != null && node.getType() == BkChanYeLianTreeNode.DZHBK ) {
    	    		int rowindex = ((BanKuaiInfoTableModel)tblDzhBkCurWkZhanBi.getModel()).getNodeRowIndex( nodecode );
					if(rowindex == -1) {
						JOptionPane.showMessageDialog(null,"板块不在分析表中，可到板块设置修改！","Warning", JOptionPane.WARNING_MESSAGE);
					}
					
    	    	}
    	    	if(bankuai != null) {
    	    		combxsearchbk.updateUserSelectedNode(bankuai);
    	    		playAnaylsisFinishNoticeSound();
    	    	}
    	    	
				hourglassCursor = null;
				Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
    			setCursor(hourglassCursor2);
    	        
            }
        });

		PropertyChangeListener propertychangelistenerforbankuaipnl = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) 
			{
				Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
				setCursor(hourglassCursor);
				TDXNodes bkcur = pnlbkwkcjlzhanbi.getCurDisplayedNode ();

                if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.SELECTED_PROPERTY)) {
                    tabpnlKxian.setSelectedIndex(0);
                    if(evt.getNewValue() != null ) {
                    	@SuppressWarnings("unchecked")
                        String selectedinfo = evt.getNewValue().toString();
                    	LocalDate selectdate = LocalDate.parse(selectedinfo);
                        refreshAfterUserSelectBanKuaiColumn (bkcur,selectdate);
                        playAnaylsisFinishNoticeSound();
                    }
                } else if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.MOUSEDOUBLECLICK_PROPERTY)) {
                	String key = evt.getNewValue().toString();
                	List<String> tmpbkinfo = Splitter.on(",").omitEmptyStrings().splitToList(key); 
                	try{  displayNodeLargerPeriodData (bkcur, LocalDate.parse(tmpbkinfo.get(1)), tmpbkinfo.get(0));
                	} catch (java.time.format.DateTimeParseException e) { displayNodeLargerPeriodData (bkcur,null,tmpbkinfo.get(0)); }
                }

                hourglassCursor = null;
				Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
				setCursor(hourglassCursor2);
			}
		};
		pnlbkwkcjlzhanbi.addPropertyChangeListener(propertychangelistenerforbankuaipnl);
		panelbkwkcjezhanbi.addPropertyChangeListener(propertychangelistenerforbankuaipnl);
		
		PropertyChangeListener propertychangelistenerforstockpnl = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
				TDXNodes selectstock = (TDXNodes) panelGgDpCjeZhanBi.getCurDisplayedNode ();
                if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.SELECTED_PROPERTY)) {
                    tabpnlKxian.setSelectedIndex(2);
                    if(evt.getNewValue() != null) {
                        @SuppressWarnings("unchecked")
                        String selectedinfo = evt.getNewValue().toString();
                        LocalDate datekey = LocalDate.parse(selectedinfo);
        				chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(datekey));
        				
        				setUserSelectedColumnMessage(selectstock, datekey);
        				
        				if( !(selectstock instanceof Stock) ) return;
        				
        				String ShowSelectGeGuColumnRemindInfo    = bkfxsettingprop.getProperty ("ShowSelectGeGuColumnRemindInfo");
        				if(ShowSelectGeGuColumnRemindInfo   != null && ShowSelectGeGuColumnRemindInfo  .toUpperCase().equals("TRUE") )
        					showReminderMessage (bkfxremind.getStockcolumnremind() );
        				
        				Class<? extends Object> source = evt.getSource().getClass();
        				Boolean result = source.equals(BanKuaiFengXiCategoryBarChartCjePnl.class);
        				if(source.equals(BanKuaiFengXiCategoryBarChartCjePnl.class) ) {
        					String readingsettinginprop  = bkfxsettingprop.getProperty ("readoutfxresultinvoice");
        					if(readingsettinginprop != null  && readingsettinginprop.toUpperCase().equals("TRUE"))
        						readAnalysisResult ( null,  (Stock)selectstock, datekey );
        				}
                    }
                } else if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.MOUSEDOUBLECLICK_PROPERTY)) {
                	String key = evt.getNewValue().toString();
                	Class<? extends Object> source = evt.getSource().getClass();
                	List<String> tmpbkinfo = Splitter.on(",").omitEmptyStrings().splitToList(key); //内蒙板块|880232|3|1|0|32
                	try{   		displayNodeLargerPeriodData (selectstock,LocalDate.parse(tmpbkinfo.get(1)),tmpbkinfo.get(0));
                	} catch (java.time.format.DateTimeParseException e) {
                		displayNodeLargerPeriodData (selectstock,null,tmpbkinfo.get(0));
                	}
                }
            }
        };
		panelggdpcjlwkzhanbi.addPropertyChangeListener(propertychangelistenerforstockpnl);
		panelGgDpCjeZhanBi.addPropertyChangeListener(propertychangelistenerforstockpnl);
		/*
		 * 查找pie chart内点击的那个个股 
		 */
		pnlcurwkggcjezhanbi.addPropertyChangeListener(new PropertyChangeListener() 
		{
	            public void propertyChange(PropertyChangeEvent evt) {

	                if (evt.getPropertyName().equals(BanKuaiFengXiPieChartPnl.SELECTED_PROPERTY)) {
	                	String curstockcode = evt.getNewValue().toString();
	                	clearGeGuTablesFilter ();
	                	findInputedNodeInStcokTables (curstockcode);
	                	
	                	StockOfBanKuai sob = (StockOfBanKuai) ((BanKuaiGeGuBasicTableModel)tableGuGuZhanBiInBk.getModel() ).getNode(curstockcode.substring(0, 6));
	                	hightlightSpecificSector (sob);
	                }
	            }
		});
		pnlCjeZbGrXuandingZhou.addPropertyChangeListener(new PropertyChangeListener() 
		{
            public void propertyChange(PropertyChangeEvent evt) {

                if (evt.getPropertyName().equals(BanKuaiFengXiPieChartPnl.SELECTED_PROPERTY)) {
                	String curstockcode = evt.getNewValue().toString();
                	clearGeGuTablesFilter ();
                	findInputedNodeInStcokTables (curstockcode);
                	
                	StockOfBanKuai sob = (StockOfBanKuai) ((BanKuaiGeGuBasicTableModel)tableGuGuZhanBiInBk.getModel() ).getNode(curstockcode.substring(0, 6));
                	hightlightSpecificSector (sob);
                }
                
                
            }
		});

		combxsearchbk.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) 
			{
				if(arg0.getStateChange() == ItemEvent.SELECTED) {
					BkChanYeLianTreeNode userinputnode = combxsearchbk.getUserInputNode();
					if(userinputnode == null ) { //如果用户输入查出的是板块，
						JOptionPane.showMessageDialog(null,"不是板块代码，重新输入！","Warning", JOptionPane.WARNING_MESSAGE);
						return;
					}
					
					Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
					setCursor(hourglassCursor);
					
					if(userinputnode.getType() ==  BkChanYeLianTreeNode.DZHBK && ((BanKuaiInfoTableModel)tblDzhBkCurWkZhanBi.getModel()).getRowCount() <=0  ) 
    					gettDZHBanKuaiZhanBiRangedByGrowthRateOfPeriod (dateChooser.getLocalDate(),globeperiod);
					if(userinputnode.getType() ==  BkChanYeLianTreeNode.TDXBK && ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getRowCount() <=0) 
						gettBanKuaiZhanBiRangedByGrowthRateOfPeriod (dateChooser.getLocalDate(),globeperiod);
					
					//找不到该股票
					String stockcode = combxsearchbk.getSelectedItem().toString().substring(0, 6);
					clearGeGuTablesFilter ();
					if(!findInputedNodeInBanKuaiTables (stockcode) ) 
						JOptionPane.showMessageDialog(null,"在板块表内没有发现该股，可能板块指数还未创建！","Warning",JOptionPane.WARNING_MESSAGE);
					else
						unifiedOperationsAfterUserSelectABanKuai ( (BanKuai)userinputnode);
					
					playAnaylsisFinishNoticeSound();
					hourglassCursor = null;
					Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
					setCursor(hourglassCursor2);
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
						nodecode = userinputnode.getMyOwnCode();
						try{ //如果用户选择的和上次选择的个股一样，不重复做板块查找
//							String stockcodeincbx = ((BkChanYeLianTreeNode)combxstockcode.getSelectedItem()).getMyOwnCode();
							displayStockSuoShuBanKuai((Stock)userinputnode);
						} catch (java.lang.NullPointerException e) {		e.printStackTrace();
//							cbxstockcode.updateUserSelectedNode (selectstock.getStock());
						} catch (java.lang.StringIndexOutOfBoundsException e) {	logger.debug((String)combxstockcode.getSelectedItem());}
					}

					if(findInputedNodeInStcokTables (nodecode)) { 
						panelGgDpCjeZhanBi.resetData();
						pnlStockCandle.resetData();
					}
			}
				
				if(arg0.getStateChange() == ItemEvent.DESELECTED) {}
			}
		});
		
		lbllastselect.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				LocalDate curdate = dateChooser.getLocalDate();
				dateChooser.setLocalDate(LocalDate.parse(lbllastselect.getText()));
			}
		});
		/*
		 * 
		 */
		tblmergeggtobks.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				int row = tblmergeggtobks.getSelectedRow();
				if(row <0) { JOptionPane.showMessageDialog(null,"请选择一个股票！","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				int modelRow = tblmergeggtobks.convertRowIndexToModel(row);
				BkChanYeLianTreeNode node = ((BanKuaiGeGuMergeTableModel)tblmergeggtobks.getModel()).getNode (modelRow);
				
				if (arg0.getClickCount() == 1) {} //用户点击一下

				if (arg0.getClickCount() == 2) {
					Integer alreadyin = ((JStockComboBoxModel)combxsearchbk.getModel()).hasTheNode( node.getMyOwnCode());
					if(alreadyin == -1)	combxsearchbk.updateUserSelectedNode( (BanKuai)node);
				}
					
			}
		});
		tableBkSocialbkCurwkData.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				int row = tableBkSocialbkCurwkData.getSelectedRow();
				if(row <0) { return;}
				int modelRow = tableBkSocialbkCurwkData.convertRowIndexToModel(row);
				BkChanYeLianTreeNode selectedbk = ((BanKuaiInfoTableModel)tableBkSocialbkCurwkData.getModel()).getNode (modelRow);
				
				if (arg0.getClickCount() == 1) { //用户点击一下
					Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
					setCursor(hourglassCursor);

					Integer alreadyin = ((JStockComboBoxModel)combxsearchbk.getModel()).hasTheNode( selectedbk.getMyOwnCode());
					if(alreadyin == -1)	combxsearchbk.updateUserSelectedNode( (BanKuai)selectedbk);
					
					hourglassCursor = null;
					Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
					setCursor(hourglassCursor2);
				}
				if (arg0.getClickCount() == 2) {}
			}
		});
		/*
		 * 
		 */
		tableGuGuZhanBiInBk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
//				int row = tableGuGuZhanBiInBk.getSelectedRow();
//				if(row <0) { JOptionPane.showMessageDialog(null,"请选择一个股票！","Warning",JOptionPane.WARNING_MESSAGE);
//					return;
//				}
//				int modelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(row);
//				StockOfBanKuai selectstock = (StockOfBanKuai) ((BanKuaiGeGuBasicTableModel)tableGuGuZhanBiInBk.getModel()).getNode (modelRow);
				
				StockOfBanKuai selectstock = getSelectedStockOfBanKuai ();
				if (arg0.getClickCount() == 1) { //用户点击一下
					int col = tableGuGuZhanBiInBk.columnAtPoint(arg0.getPoint());
					if(col == 1)	unifiedOperationsAfterUserSelectAStock (selectstock,true); //播报语音
					else			unifiedOperationsAfterUserSelectAStock (selectstock,false);
				}
				if (arg0.getClickCount() == 2) {}
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
				StockOfBanKuai selectstock = (StockOfBanKuai) ((BanKuaiGeGuBasicTableModel)tableTempGeGu.getModel()).getNode (modelRow);
				
				if (arg0.getClickCount() == 1) { //用户点击一下
					int col = tableGuGuZhanBiInBk.columnAtPoint(arg0.getPoint());
					if(col == 1)	unifiedOperationsAfterUserSelectAStock (selectstock,true);
					else			unifiedOperationsAfterUserSelectAStock (selectstock,false);
				}
			
				tabbedPanegegu.setSelectedIndex(6);
			}
		});
		
		tablexuandingzhou.addMouseListener(new MouseAdapter() 
		{
			public void mouseClicked(MouseEvent arg0) {
				int row = tablexuandingzhou.getSelectedRow();
				if(row <0) {	JOptionPane.showMessageDialog(null,"请选择一个股票！","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				int modelRow = tablexuandingzhou.convertRowIndexToModel(row);
				StockOfBanKuai selectstock = (StockOfBanKuai) ((BanKuaiGeGuBasicTableModel)tablexuandingzhou.getModel()).getNode (modelRow);
				
				if (arg0.getClickCount() == 1) {
					int col = tableGuGuZhanBiInBk.columnAtPoint(arg0.getPoint());
					if(col == 1)	unifiedOperationsAfterUserSelectAStock (selectstock,true);
					else			unifiedOperationsAfterUserSelectAStock (selectstock,false);
				}
				if (arg0.getClickCount() == 2) {
				}
			}
		});
		tablexuandingminustwo.addMouseListener(new MouseAdapter() 
		{
			public void mouseClicked(MouseEvent arg0) {
				int row = tablexuandingminustwo.getSelectedRow();
				if(row <0) { JOptionPane.showMessageDialog(null,"请选择一个股票！","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				int modelRow = tablexuandingminustwo.convertRowIndexToModel(row);
				StockOfBanKuai selectstock = (StockOfBanKuai) ((BanKuaiGeGuBasicTableModel)tablexuandingminustwo.getModel()).getNode (modelRow);
	
				if (arg0.getClickCount() == 1) {
					int col = tableGuGuZhanBiInBk.columnAtPoint(arg0.getPoint());
					if(col == 1)	unifiedOperationsAfterUserSelectAStock (selectstock,true);
					else unifiedOperationsAfterUserSelectAStock (selectstock,false);
				}
				 if (arg0.getClickCount() == 2) { }
			}
		}); 
		tablexuandingminusone.addMouseListener(new MouseAdapter() 
		{
			public void mouseClicked(MouseEvent arg0) {
				int row = tablexuandingminusone.getSelectedRow();
				if(row <0) {JOptionPane.showMessageDialog(null,"请选择一个股票！","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				int modelRow = tablexuandingminusone.convertRowIndexToModel(row);
				StockOfBanKuai selectstock = (StockOfBanKuai) ((BanKuaiGeGuBasicTableModel)tablexuandingminusone.getModel()).getNode (modelRow);
	
				if (arg0.getClickCount() == 1) {
					int col = tableGuGuZhanBiInBk.columnAtPoint(arg0.getPoint());
					if(col == 1)	unifiedOperationsAfterUserSelectAStock (selectstock,true);
					else	unifiedOperationsAfterUserSelectAStock (selectstock,false);
				}
				 if (arg0.getClickCount() == 2) { }
			}
		});
		tablexuandingplusone.addMouseListener(new MouseAdapter() 
		{
			public void mouseClicked(MouseEvent arg0) {
				int row = tablexuandingplusone.getSelectedRow();
				if(row <0) {	JOptionPane.showMessageDialog(null,"请选择一个股票！","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				int modelRow = tablexuandingplusone.convertRowIndexToModel(row);
				StockOfBanKuai selectstock = (StockOfBanKuai) ((BanKuaiGeGuBasicTableModel)tablexuandingplusone.getModel()).getNode (modelRow);
	
				if (arg0.getClickCount() == 1) {
					int col = tableGuGuZhanBiInBk.columnAtPoint(arg0.getPoint());
					if(col == 1)	unifiedOperationsAfterUserSelectAStock (selectstock,true);
					else			unifiedOperationsAfterUserSelectAStock (selectstock,false);
				}
				 if (arg0.getClickCount() == 2) { }
			}
		});
		tableExternalInfo.addMouseListener(new MouseAdapter() 
		{
			public void mouseClicked(MouseEvent arg0) {
				int row = tableExternalInfo.getSelectedRow();
				if(row <0) {	JOptionPane.showMessageDialog(null,"请选择一个股票！","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				int modelRow = tableExternalInfo.convertRowIndexToModel(row);
				StockOfBanKuai selectstock = (StockOfBanKuai) ((BanKuaiGeGuBasicTableModel)tableExternalInfo.getModel()).getNode (modelRow);
	
				if (arg0.getClickCount() == 1) {
					int col = tableGuGuZhanBiInBk.columnAtPoint(arg0.getPoint());
					if(col == 1)
						unifiedOperationsAfterUserSelectAStock (selectstock,true);
					else
						unifiedOperationsAfterUserSelectAStock (selectstock,false); 
				}
				 if (arg0.getClickCount() == 2) { }
			}
		});
		tblDzhBkCurSelectedWkZhanBi.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				int row = tblDzhBkCurSelectedWkZhanBi.getSelectedRow();
				if(row <0) {	JOptionPane.showMessageDialog(null,"请选择一个板块！","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				int modelRow = tblDzhBkCurSelectedWkZhanBi.convertRowIndexToModel(row);
				BanKuai selectedbk = (BanKuai) ((BanKuaiInfoTableModel)tblDzhBkCurSelectedWkZhanBi.getModel()).getNode(modelRow);

				
				Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
				setCursor(hourglassCursor);

				Integer alreadyin = ((JStockComboBoxModel)combxsearchbk.getModel()).hasTheNode( selectedbk.getMyOwnCode());
				if(alreadyin == -1)	combxsearchbk.updateUserSelectedNode( (BanKuai)selectedbk);
				
				hourglassCursor = null;
				Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
				setCursor(hourglassCursor2);

//				playAnaylsisFinishNoticeSound();
			}
		});

		
		tblDzhBkCurWkZhanBi.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				BanKuai selectedbk = getSelectedDZHBanKuai ();
				
				Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
				setCursor(hourglassCursor);

				Integer alreadyin = ((JStockComboBoxModel)combxsearchbk.getModel()).hasTheNode( selectedbk.getMyOwnCode());
				if(alreadyin == -1)	combxsearchbk.updateUserSelectedNode( (BanKuai)selectedbk);
				
				hourglassCursor = null;
				Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
				setCursor(hourglassCursor2);

//				playAnaylsisFinishNoticeSound();

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
				int modelRow = tableBkZhanBi.convertRowIndexToModel(row);
				BanKuai selectedbk = (BanKuai) ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getNode(modelRow);
				
				Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
				setCursor(hourglassCursor);
				
				Integer alreadyin = ((JStockComboBoxModel)combxsearchbk.getModel()).hasTheNode(selectedbk.getMyOwnCode());
				if(alreadyin == -1)	combxsearchbk.updateUserSelectedNode(selectedbk);
				
				hourglassCursor = null;
				Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
				setCursor(hourglassCursor2);
			}
		});
		
		tableselectedwkbkzb.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
//				int row = tableselectedwkbkzb.getSelectedRow();
//				if(row <0) {
//					JOptionPane.showMessageDialog(null,"请选择一个板块！","Warning",JOptionPane.WARNING_MESSAGE);
//					return;
//				}
//				int modelRow = tableselectedwkbkzb.convertRowIndexToModel(row);
//				BanKuai selectedbk = (BanKuai) ((BanKuaiInfoTableModel)tableselectedwkbkzb.getModel()).getNode(modelRow);
				BanKuai selectedbk =  getXuanDingZhouSelectedTDXBanKuai ();
				Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
				setCursor(hourglassCursor);
				
				Integer alreadyin = ((JStockComboBoxModel)combxsearchbk.getModel()).hasTheNode(selectedbk.getMyOwnCode());
				if(alreadyin == -1)	combxsearchbk.updateUserSelectedNode(selectedbk);
				
				hourglassCursor = null;
				Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
				setCursor(hourglassCursor2);

				playAnaylsisFinishNoticeSound();

			}
		});
	}
	
	/*
	 * 计算个股所有板块的数据
	 */
	protected void calculateAllGeGuBanKuaiInfo(Stock stock, LocalDate curselectdate) 
	{
		((BanKuaiInfoTableModel)tblmergegegubkinfo.getModel()).deleteAllRows(); 
		((GeGuToBanKuaiTableModel)tblmergegeguinfoinallbk.getModel()).deleteAllRows();
		((BanKuaiGeGuMergeTableModel)tblmergeggtobks.getModel()).deleteAllRows();
		
		LocalDate requirestart = CommonUtility.getSettingRangeDate(this.dateChooser.getLocalDate(),"large").with(DayOfWeek.MONDAY);
		
		SvsForNodeOfBanKuai svsbk = new SvsForNodeOfBanKuai ();
		Set<BkChanYeLianTreeNode> curbklist = stock.getGeGuCurSuoShuTDXSysBanKuaiList();
		for(BkChanYeLianTreeNode tmpbk : curbklist) {
			if( ((BanKuai)tmpbk).getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL) 
					||  ((BanKuai)tmpbk).getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL)  ) //有些指数是没有个股和成交量的，不列入比较范围
				continue;
			
			if( !((BanKuai)tmpbk).getBanKuaiOperationSetting().isShowinbkfxgui() )
				continue;
			
			tmpbk = svsbk.getNodeData( (BanKuai)tmpbk, requirestart, this.dateChooser.getLocalDate(),globeperiod,globecalwholeweek);
			((BanKuaiInfoTableModel)tblmergegegubkinfo.getModel()).addBanKuai((BanKuai)tmpbk);
		}
		svsbk = null;
		
		SvsForNodeOfDZHBanKuai svsdzhbk = new SvsForNodeOfDZHBanKuai ();
		Set<BkChanYeLianTreeNode> curdzhbklist = stock.getGeGuCurSuoShuDZHSysBanKuaiList();
		for(BkChanYeLianTreeNode tmpbk : curdzhbklist) {
			if( ((BanKuai)tmpbk).getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL) 
					||  ((BanKuai)tmpbk).getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL)  ) //有些指数是没有个股和成交量的，不列入比较范围
				continue;
			
			if( !((BanKuai)tmpbk).getBanKuaiOperationSetting().isShowinbkfxgui() )
				continue;
			
			tmpbk = svsdzhbk.getNodeData( (BanKuai)tmpbk, requirestart, this.dateChooser.getLocalDate(),globeperiod,globecalwholeweek);
			((BanKuaiInfoTableModel)tblmergegegubkinfo.getModel()).addBanKuai((BanKuai)tmpbk);
		}
		svsbk = null;
		
		((BanKuaiInfoTableModel)tblmergegegubkinfo.getModel()).refresh(curselectdate, this.globeperiod); 
		((GeGuToBanKuaiTableModel)tblmergegeguinfoinallbk.getModel()).refresh(stock, curselectdate, this.globeperiod);
		((BanKuaiGeGuMergeTableModel)tblmergeggtobks.getModel()).refresh();
	}
	/*
	 * 
	 */
	protected void getBanKuaiHistoryCsvAndAnalysisFile(JMenu jMenu, TDXNodes node) 
	{
		if(jMenu.getMenuComponentCount() >0) {
//			Component[] mc = historycsvfileMenu.getMenuComponents();
			Component curmenuitem = historycsvfileMenu.getMenuComponent(0);
			if(curmenuitem != null  && curmenuitem.getName().contains(node.getMyOwnCode() ))
				return;
		}
		
		jMenu.removeAll();
		
		String installpath = (new SetupSystemConfiguration()).getSystemInstalledPath();
		File dir = new File(installpath + "/dailydata/csv");
		
		String csvandanalysisfile = bkfxsettingprop.getProperty ("csvandanalysisfile");
		List<File> fileList = new ArrayList<> ();
		if(csvandanalysisfile != null) {
			List<String> fileprofrixlist = Splitter.on(",").omitEmptyStrings().splitToList(csvandanalysisfile);
			for(String profrix : fileprofrixlist) {
				File[] tmpfileList = dir.listFiles(new FilenameFilter() {
				    public boolean accept(File dir, String name) {
				    	Boolean nameqlf = name.contains(node.getMyOwnCode() );
				    	Boolean extensionqlf = name.toUpperCase().endsWith(profrix.trim().toUpperCase() ) ; 
				        return  nameqlf && extensionqlf;
				    }
				});
//				FileFilter profrixfileFilter = new WildcardFileFilter("*." + profrix.toUpperCase(), IOCase.INSENSITIVE);      			// For taking both .JPG and .jpg files (useful in *nix env)
//				File[] tmpfileList = dir.listFiles(profrixfileFilter);
				if (tmpfileList.length > 0)           
					/** The oldest file comes first **/     
					Arrays.sort(tmpfileList, LastModifiedFileComparator.LASTMODIFIED_COMPARATOR); //filesList now contains all the JPG/jpg files in sorted order    
				fileList.addAll( new ArrayList<File>(Arrays.asList(tmpfileList)) );
			}
		}
		for(File tmpfile : fileList) {
			if (tmpfile.isDirectory())		continue;
			if(!tmpfile.isFile())			continue;
		    
		    	String singlefilename = tmpfile.getName();
		    	JMenuItem menuitemhistorycsvfile = new JMenuItem (singlefilename ); 
		    	jMenu.add(menuitemhistorycsvfile);
		    	menuitemhistorycsvfile.setName(singlefilename );
		    	menuitemhistorycsvfile.addActionListener(new ActionListener() {
		            @Override
		            public void actionPerformed(ActionEvent e) {
		            	JMenuItem actionmenu = (JMenuItem)e.getSource();
		            	String actionmenufilename = actionmenu.getName();
		            	String extension = Files.getFileExtension(actionmenufilename).toUpperCase();
		            	switch(extension) {
		            	case "CSV" :
		            		try {
								String cmd = "rundll32 url.dll,FileProtocolHandler " + installpath + "/dailydata/csv/" +  actionmenufilename;
								Process p  = Runtime.getRuntime().exec(cmd);
								p.waitFor();
							} catch (Exception e1){
								e1.printStackTrace();
							}
			            	
			            	try {
				      			File filecsv = new File( installpath + "/dailydata/csv/" +  actionmenufilename );
			            		String path = filecsv.getAbsolutePath();
				      			Runtime.getRuntime().exec("explorer.exe /select," + path );
				      		} catch (IOException e1) {
				      				e1.printStackTrace();
				      		}
			            	
		            		break;
		            	case "XLS" :
		            		try {
								String cmd = "rundll32 url.dll,FileProtocolHandler " + installpath + "/dailydata/csv/" +  actionmenufilename;
								Process p  = Runtime.getRuntime().exec(cmd);
								p.waitFor();
							} catch (Exception e1){
								e1.printStackTrace();
							}
			            	
			            	try {
				      			File filecsv = new File( installpath + "/dailydata/csv/" +  actionmenufilename );
			            		String path = filecsv.getAbsolutePath();
				      			Runtime.getRuntime().exec("explorer.exe /select," + path );
				      		} catch (IOException e1) {
				      				e1.printStackTrace();
				      		}
			            	
		            		break;
		            	case "PBIX" :
		            		try {
								String cmd = "rundll32 url.dll,FileProtocolHandler " + installpath + "/dailydata/csv/" +  actionmenufilename;
								Process p  = Runtime.getRuntime().exec(cmd);
								p.waitFor();
							} catch (Exception e1){
								e1.printStackTrace();
							}
			            	
//			            	try {
//				      			File filecsv = new File( installpath + "/dailydata/csv/" +  actionmenufilename );
//			            		String path = filecsv.getAbsolutePath();
//				      			Runtime.getRuntime().exec("explorer.exe /select," + path );
//				      		} catch (IOException e1) {
//				      				e1.printStackTrace();
//				      		}
//			            	
//		            		break;
		            	}
		            }
		        });
		    	
		    
		}

		if(fileList.size() == 0) {
			JMenuItem menuitemhistorycsvfile = new JMenuItem ("没有历史CSV文件" );
			menuitemhistorycsvfile.setName("没有历史CSV文件");
			jMenu.add(menuitemhistorycsvfile);
		}
	
	}

	/*
	 * 
	 */
	private void parseTempGeGuFromZhiDingBanKuaiFunciotn ()
	{
		String result = JOptionPane.showInputDialog("请输入板块代码");
		if(result != null) {
			BanKuai bk = (BanKuai) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(result.trim(), BkChanYeLianTreeNode.TDXBK);
			if(bk == null)	bk = (BanKuai) CreateExchangeTree.CreateTreeOfDZHBanKuaiAndStocks().getSpecificNodeByHypyOrCode(result.trim(), BkChanYeLianTreeNode.DZHBK);
			
			if(bk == null)	return;
			
				ServicesForNodeBanKuai svsbk = bk.getBanKuaiService(true);
				bk = svsbk.getAllGeGuOfBanKuai (bk); 
				List<BkChanYeLianTreeNode> allbkgg = bk.getAllGeGuOfBanKuaiInHistory();
				List<String> listNames = allbkgg.stream().map(u -> u.getMyOwnCode()).collect(Collectors.toList());
				parseTempGeGeFromList (listNames);
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
	}
	/*
	 * 
	 */
	private void parseTempGeGeFromList (List<String> readparsefileLines )
	{
		BanKuai tempbankuai = null;
		
		List<BkChanYeLianTreeNode> curstocksintable = ((BanKuaiGeGuBasicTableModel)tableTempGeGu.getModel()).getAllNodes();
		if( curstocksintable != null && curstocksintable.size() >0) {
			int extraresult = JOptionPane.showConfirmDialog(null,"临时个股表当前不为空，点击 是 先清空表，点击 否 把新个股添加表中." , "Warning!", JOptionPane.OK_CANCEL_OPTION);
			if(extraresult == JOptionPane.OK_OPTION) { //其他导出条件
				((BanKuaiGeGuBasicTableModel)tableTempGeGu.getModel()).deleteAllRows();
				curstocksintable = null;
				tempbankuai = new BanKuai ("TEMPBANKUAI","TEMPBANKUAI");
				tempbankuai.setBanKuaiLeiXing (BanKuai.HASGGWITHSELFCJL);
			} else
				tempbankuai = (BanKuai)((BanKuaiGeGuBasicTableModel)tableTempGeGu.getModel()).getCurDispalyBandKuai();
		} else {
			tempbankuai = new BanKuai ("TEMPBANKUAI","TEMPBANKUAI");
			tempbankuai.setBanKuaiLeiXing (BanKuai.HASGGWITHSELFCJL);
		}
		
		Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
		setCursor(hourglassCursor);
		
		LocalDate curselectdate = null;
		try{	curselectdate = dateChooser.getLocalDate();// dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		} catch (java.lang.NullPointerException e) {JOptionPane.showMessageDialog(null,"日期有误!","Warning",JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		SvsForNodeOfStock svstock = new SvsForNodeOfStock ();
		for(String tmpgegu : readparsefileLines) {
			if( curstocksintable != null) {
				Boolean found = false; //if has already in tempbk, dont retrieve data again.
				for (BkChanYeLianTreeNode tmpstockofbk : curstocksintable  ) {
					if(tmpstockofbk.getMyOwnCode().equals(tmpgegu) ) {
						found = true;
						break;
					}
				}
				
				if(found)	continue;
			}

			Stock tmpstock = (Stock) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(tmpgegu, BkChanYeLianTreeNode.TDXGG);
			if(tmpstock == null)	continue;
			try {
				tmpstock = (Stock) svstock.getNodeData( tmpstock , CommonUtility.getSettingRangeDate(curselectdate,"middle"),  LocalDate.now(),
						globeperiod,this.globecalwholeweek);
				svstock.syncNodeData(tmpstock);
			} catch (java.lang.NullPointerException e) {logger.info(tmpgegu + "数据有误！");e.printStackTrace();}

			StockOfBanKuai bkofst = new StockOfBanKuai(tempbankuai,tmpstock);
			LocalDate joindate = LocalDate.parse("1997-01-01");
			LocalDate leftdate = LocalDate.parse("3000-01-01");
			DateTime joindt= new DateTime(joindate.getYear(), joindate.getMonthValue(), joindate.getDayOfMonth(), 0, 0, 0, 0);
			DateTime leftdt = new DateTime(leftdate.getYear(), leftdate.getMonthValue(), leftdate.getDayOfMonth(), 0, 0, 0, 0);
			Interval joinleftinterval = new Interval(joindt, leftdt);
			bkofst.addInAndOutBanKuaiInterval(joinleftinterval);
			
			tempbankuai.addNewBanKuaiGeGu(bkofst);
		}
		
		((BanKuaiGeGuTableModelFromPropertiesFile)tableTempGeGu.getModel()).refresh(tempbankuai, curselectdate,globeperiod); 
		
		preOperationForMainBkAndTempBkIntersection (); 
		
		svstock = null;
		hourglassCursor = null;
		Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
		setCursor(hourglassCursor2);

		playAnaylsisFinishNoticeSound();
	}
	/*
	 * 
	 */
	protected void parseTempGeGuFromFileFunciotns()
	{
		String parsedpath = (new SetupSystemConfiguration()).getTDXModelMatchExportFile ();
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		chooser.setCurrentDirectory(new File(parsedpath) );
		
		String filename;
		if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			if(chooser.getSelectedFile().isDirectory())    	filename = (chooser.getSelectedFile()+ "\\").replace('\\', '/');
		    else  filename = (chooser.getSelectedFile()).toString().replace('\\', '/');
		} else	return;
		
		File fileebk = null;
		if(filename.endsWith("EBK")) {
			fileebk = new File( filename );
			try {	if (!fileebk.exists()) 	return ;
			} catch (Exception e) {	e.printStackTrace();	return ;	}
		}
		
		List<String> readparsefileLines = null;
		try { //读出个股
			readparsefileLines = Files.readLines(fileebk,Charsets.UTF_8,new ParseBanKuaiWeeklyFielGetStocksProcessor ());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		parseTempGeGeFromList (readparsefileLines);
		
		tabbedPanegegu.setToolTipTextAt(6,filename);
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
		
		StockOfBanKuai selectstock = getSelectedStockOfBanKuai ();
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
				String csvexportdefaulttimerangebymonthes = bkfxsettingprop.getProperty ("csvexportdefaulttimerangebymonthes");
				Integer timerange = 52;
				if(csvexportdefaulttimerangebymonthes != null) 
					timerange = Integer.parseInt(csvexportdefaulttimerangebymonthes);
				
				DateRangeSelectPnl datachoose = new DateRangeSelectPnl (timerange); 
				JOptionPane.showMessageDialog(null, datachoose,"为" + tmpnode.getMyOwnName() + "选择导出时间段", JOptionPane.OK_CANCEL_OPTION);
				LocalDate nodestart = datachoose.getDatachoosestart();
				LocalDate nodeend = datachoose.getDatachooseend();
		
				if(BkChanYeLianTreeNode.isBanKuai(tmpnode) ) {
					((BanKuai)tmpnode).getServicesForNode(true).getNodeData(tmpnode, nodestart, nodeend, this.globeperiod, this.globecalwholeweek);
					((BanKuai)tmpnode).getServicesForNode(true).syncNodeData(tmpnode);
					((BanKuai)tmpnode).getServicesForNode(false);
				} else
				if(tmpnode.getType() == BkChanYeLianTreeNode.TDXGG) {
					SvsForNodeOfStock svsstk = new SvsForNodeOfStock	();
					tmpnode = (TDXNodes) svsstk.getNodeData(tmpnode, nodestart, nodeend, this.globeperiod, this.globecalwholeweek);
					svsstk.syncNodeData(tmpnode);
					svsstk = null;
				}
				DaPan dapan = (DaPan) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode("000000", BkChanYeLianTreeNode.DAPAN);
		    	dapan.getServicesForNode(true).getNodeData (dapan,nodestart, nodeend, this.globeperiod, this.globecalwholeweek);
		    	dapan.getServicesForNode(false);
		    	
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
	}
	/*
	 * 
	 */
	protected void displayNodeInfo(BkChanYeLianTreeNode selectednode) 
	{
		JTabbedPane extrainfotabpane = new JTabbedPane(JTabbedPane.TOP);
		extrainfotabpane.setUI(new com.exchangeinfomanager.commonlib.MetalBorderlessTabbedPaneUI());
		
		DisplayNodeJiBenMianService nodejbm = new DisplayNodeJiBenMianService (selectednode);
		DisplayNodeInfoPanel displaybkjbmpnl = new DisplayNodeInfoPanel (nodejbm);
		if(displaybkjbmpnl.isHtmlContainedUsefulInfo() ) {
			Dimension size = new Dimension(sclpinfosummary.getViewport().getSize().width,  displaybkjbmpnl.getContentHeight() + 10 );
			displaybkjbmpnl.setPreferredSize(size);
			displaybkjbmpnl.setMinimumSize(size);
			displaybkjbmpnl.setMaximumSize(size);
			extrainfotabpane.addTab(selectednode.getMyOwnName() + "基本面", displaybkjbmpnl);
		}
		
		if(BkChanYeLianTreeNode.isBanKuai(selectednode)) {
			DisplayNodesRelatedNewsServices bknews = new DisplayNodesRelatedNewsServices (selectednode);
			bknews.setTimeRangeForInfoRange(this.dateChooser.getLocalDate(), this.dateChooser.getLocalDate());
			DisplayNodeInfoPanel displaybknewspnl = new DisplayNodeInfoPanel (bknews);
			if(displaybknewspnl.isHtmlContainedUsefulInfo() ) {
				Dimension size2 = new Dimension(sclpinfosummary.getViewport().getSize().width, displaybknewspnl.getContentHeight() + 10 );
				displaybknewspnl.setPreferredSize(size2);
				displaybknewspnl.setMinimumSize(size2);
				displaybknewspnl.setMaximumSize(size2);
				
				extrainfotabpane.addTab(selectednode.getMyOwnName() + "新闻", displaybknewspnl);
			}
		}
		
		if(selectednode.getType() == BkChanYeLianTreeNode.TDXGG) {
			DisplayNodeSellBuyInfoServices nodesellbuy = new DisplayNodeSellBuyInfoServices (selectednode);
			DisplayNodeInfoPanel displaybksbpnl = new DisplayNodeInfoPanel (nodesellbuy);
			if(displaybksbpnl.isHtmlContainedUsefulInfo ()) {
				Dimension size3 = new Dimension(sclpinfosummary.getViewport().getSize().width, displaybksbpnl.getContentHeight() + 10);
				displaybksbpnl.setPreferredSize(size3);
				displaybksbpnl.setMinimumSize(size3);
				displaybksbpnl.setMaximumSize(size3);
				
				extrainfotabpane.addTab(selectednode.getMyOwnName() + "买卖关注记录", displaybksbpnl);	
			}
			
			DisplayNodeGuDongInfoServices svsgd = new DisplayNodeGuDongInfoServices(selectednode);
			DisplayNodeInfoPanel gdpnl = new DisplayNodeInfoPanel (svsgd);
			if(gdpnl.isHtmlContainedUsefulInfo ()) {
				Dimension size3 = new Dimension(sclpinfosummary.getViewport().getSize().width, gdpnl.getContentHeight() + 10);
				gdpnl.setPreferredSize(size3);
				gdpnl.setMinimumSize(size3);
				gdpnl.setMaximumSize(size3);
				
				extrainfotabpane.addTab(selectednode.getMyOwnName() + "股东信息", gdpnl);
			}
		}
		
		TagsPanel pnlnodetags = new TagsPanel ("",TagsPanel.HIDEHEADERMODE,TagsPanel.PARTCONTROLMODE);
		extrainfotabpane.addTab(selectednode.getMyOwnName() + "关键词", pnlnodetags);
		extrainfotabpane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                	Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
        			setCursor(hourglassCursor);
        			
                	String selecttitle = extrainfotabpane.getTitleAt( extrainfotabpane.getSelectedIndex() ).trim();
                	if(selecttitle.contains("关键词"))  selecttitle = "关键词";
                	switch(selecttitle) {
                	case "关键词":	
                		BkChanYeLianTreeNode node = nodejbm.getDisplayedNode();
                		if(BkChanYeLianTreeNode.isBanKuai(node) ) {
                			SvsForNodeOfBanKuai svrforbk = new SvsForNodeOfBanKuai ();
                    		svrforbk.getAllGeGuOfBanKuai((BanKuai) node);
                    		Collection<BkChanYeLianTreeNode> bkstock = ((BanKuai) node).getSpecificPeriodBanKuaiGeGu (dateChooser.getLocalDate());
                    		svrforbk = null;
                    		if(bkstock == null || bkstock.isEmpty())	return;
                    			
                    		bkstock.add(node);
                    		TagsServiceForNodes lbnodedbservice = new TagsServiceForNodes (bkstock);
                    		CacheForInsertedTag bkstkkwcache = new CacheForInsertedTag (lbnodedbservice);
                    		lbnodedbservice.setCache(bkstkkwcache);
                    		pnlnodetags.initializeTagsPanel (lbnodedbservice,bkstkkwcache);
                    		pnlnodetags.revalidate();
                    		pnlnodetags.repaint();
                		} else {
                			Collection<BkChanYeLianTreeNode> bkstock = new HashSet<> ();
                			bkstock.add(node);
                			TagsServiceForNodes lbnodedbservice = new TagsServiceForNodes (bkstock);
                			CacheForInsertedTag bkstkkwcache = new CacheForInsertedTag (lbnodedbservice);
                			lbnodedbservice.setCache(bkstkkwcache);
                			pnlnodetags.initializeTagsPanel (lbnodedbservice,bkstkkwcache);
                			pnlnodetags.validate();
                			pnlnodetags.repaint();
                		}
                		playAnaylsisFinishNoticeSound();
                	break;
                	}
                	
                	hourglassCursor = null;
    				Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
        			setCursor(hourglassCursor2);
                } else if (e.getButton() == MouseEvent.BUTTON3) {}
            }
		});
		
		pnlnodetags.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {

                if (evt.getPropertyName().equals(LabelTag.PROPERTYCHANGEDATAGWASSELECTED )) {
                	Collection<Tag> newvalue = (Collection<Tag>) evt.getNewValue();
                	 ((BanKuaiGeGuBasicTableModel)tableGuGuZhanBiInBk.getModel() ).setCurrentHighlightKeyWords (newvalue);
                	 tableGuGuZhanBiInBk.repaint();
                }
            }
		});
		
		pnlextrainfo.add(extrainfotabpane);
		sclpinfosummary.getVerticalScrollBar().setValue(pnlextrainfo.getHeight() - extrainfotabpane.getHeight() );
		
		setUserSelectedColumnMessage( (TDXNodes)selectednode, this.dateChooser.getLocalDate());
	}
	/*
	 * 用户双击某个node占比chart，则放大显示该node一年内的占比所有数据
	 */
	protected void displayNodeLargerPeriodData(TDXNodes node, LocalDate datekey, String guisource) 
	{
		LocalDate isInNotCalWholeWeekModeData = null;
		if(!this.globecalwholeweek) {
			NodeXPeriodData nodexdatawk = ((TDXNodes)node).getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
			NodeXPeriodData nodexdataday = ((TDXNodes)node).getNodeXPeroidData(NodeGivenPeriodDataItem.DAY);
			isInNotCalWholeWeekModeData = nodexdatawk.isInNotCalWholeWeekMode ();
		}
		
		//保证显示时间范围为当前日期前后有数据的36个月(3年)
		LocalDate curselectdate = dateChooser.getLocalDate().with(DayOfWeek.FRIDAY);
		long numberOfMonthBetweenCurrentSelectionWithNow = ChronoUnit.MONTHS.between(curselectdate, LocalDate.now() ) + 1;
		long n=0; if(numberOfMonthBetweenCurrentSelectionWithNow >=18) n=18; else n = numberOfMonthBetweenCurrentSelectionWithNow;
		long m = 12 * 3  - n; 
		LocalDate requireend = curselectdate.plus(n,ChronoUnit.MONTHS).with(DayOfWeek.FRIDAY);
		LocalDate requirestartd = curselectdate.minus(m,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		LocalDate bufferdatastartday  = requirestartd.minus(2 * 36,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY); 
		
//		long start=System.currentTimeMillis(); //获取结束时间
//		System.out.println("......显示large PNL 数据准备开始" + LocalTime.now() + " \r\n");
		//同步数据
		ServicesForNode svsnode = node.getServicesForNode(true);
		node = (TDXNodes) svsnode.getNodeData(node, bufferdatastartday, requireend, globeperiod, true);
		svsnode.syncNodeData(node);
		node.getServicesForNode(false);
//		System.out.println("......显示large PNL 数据准备开始 NODE 数据准备结束" + LocalTime.now() + " \r\n");
		if(node.getType() == BkChanYeLianTreeNode.TDXGG) { 
			BanKuai bkcur = null;
			//如果是个股的话，还要显示其当前所属的板块占比信息，所以要把板块的数据也找出来。
			int row = tableBkZhanBi.getSelectedRow();
			if(row != -1) {
				int modelRow = tableBkZhanBi.convertRowIndexToModel(row);
				bkcur = (BanKuai) ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getNode(modelRow);
				bkcur = (BanKuai) bkcur.getServicesForNode(true).getNodeData(bkcur, bufferdatastartday, requireend, globeperiod, true);
				bkcur.getServicesForNode(true).syncNodeData(bkcur);
				bkcur.getServicesForNode(false);
//				System.out.println("......显示large PNL 数据准备开始 板块数据准备结束" + LocalTime.now() + " \r\n");
			}
		}
				
		if(node.getType() != BkChanYeLianTreeNode.DAPAN) {
			DaPan dapan = (DaPan) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode("000000", BkChanYeLianTreeNode.DAPAN);
			dapan.getServicesForNode(true).getNodeData(dapan, bufferdatastartday, requireend, this.globeperiod, true);
			dapan.getServicesForNode(true).getNodeKXian(dapan, bufferdatastartday, requireend,NodeGivenPeriodDataItem.DAY,true);
			dapan.getServicesForNode(false);
//			System.out.println("......显示large PNL 数据准备 DAPAN数据准备结束" + LocalTime.now() + " \r\n");
		}
//		long end=System.currentTimeMillis(); //获取结束时间
//		System.out.println("......显示large PNL 数据准备结束" + LocalTime.now() + "。.....导入耗费时间： "+(end-start)+"ms \r\n");

				BanKuaiFengXiLargePnl largeinfo = null;
				String guitype = "CJE";
				if( guisource.contains("CJL") )
					guitype = "CJL";
				else if( guisource.contains("CJE") )
					guitype = "CJE";
				
				if(node.getType() == BkChanYeLianTreeNode.DAPAN  ) {
					largeinfo = new BanKuaiFengXiLargePnl (node, node, requirestartd, requireend, globeperiod,guitype,this.bkfxsettingprop);
				} else
				if( BkChanYeLianTreeNode.isBanKuai(node)) {
					DaPan treeroot = (DaPan) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getModel().getRoot();
					largeinfo = new BanKuaiFengXiLargePnl (treeroot, node, requirestartd, requireend, globeperiod,guitype,this.bkfxsettingprop);
				} else 
				if(node.getType() == BkChanYeLianTreeNode.TDXGG || node.getType() == BkChanYeLianTreeNode.BKGEGU) { 
					DaPan treeroot = (DaPan) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getModel().getRoot();
					largeinfo = new BanKuaiFengXiLargePnl ( treeroot , node, requirestartd, requireend, globeperiod,guitype,this.bkfxsettingprop);
				}
				
				if(datekey != null)	largeinfo.highLightSpecificBarColumn(datekey);
				else	largeinfo.highLightSpecificBarColumn(curselectdate);
//				end=System.currentTimeMillis(); //获取结束时间
//				System.out.println(".......显示large GUI GUI准备结束" + LocalTime.now() + "。.....导入耗费时间： "+(end-start)+"ms \r\n");
				
				playAnaylsisFinishNoticeSound ();
				largeinfo.nodecombinedpnl.setProperties(this.bkfxsettingprop);
//				System.out.println(".......显示large ready to display GUI " + LocalTime.now() + "。.....导入耗费时间： "+(end-start)+"ms \r\n");
				JOptionPane.showMessageDialog(null, largeinfo, node.getMyOwnCode()+node.getMyOwnName()+ "大周期分析结果", JOptionPane.OK_CANCEL_OPTION);
//				System.out.println(".......显示large GUI显示结束" + LocalTime.now() + "。.....导入耗费时间： "+(end-start)+"ms \r\n");
				largeinfo = null;
				System.gc();
				
				if(!this.globecalwholeweek) { //如果是非计算完整周模式，显示完要恢复到原来的状态，因为现在NODE的状态是保存的都是完整周的数据。
					node = (TDXNodes) node.getServicesForNode(true).getNodeData(node, CommonUtility.getSettingRangeDate(curselectdate, "basic")
							, isInNotCalWholeWeekModeData, NodeGivenPeriodDataItem.WEEK, false);
					svsnode.syncNodeData(node);
					node.getServicesForNode(false);
					
					if(node.getType() != BkChanYeLianTreeNode.DAPAN) {
						DaPan dapan = (DaPan) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode("000000", BkChanYeLianTreeNode.DAPAN);
						dapan.getServicesForNode(true).getNodeData(node, CommonUtility.getSettingRangeDate(curselectdate, "basic")
								, isInNotCalWholeWeekModeData, NodeGivenPeriodDataItem.WEEK, false);
						dapan.getServicesForNode(true).getNodeKXian(node, CommonUtility.getSettingRangeDate(curselectdate, "basic")
								, isInNotCalWholeWeekModeData, NodeGivenPeriodDataItem.WEEK, false);
						dapan.getServicesForNode(false);
//						System.out.println("......显示large PNL 数据准备 DAPAN数据准备结束" + LocalTime.now() + " \r\n");
					}
					
				}
	}
	/*
	 * 跑马灯
	 */
	private void initializePaoMaDeng() 
	{
		String displayinfo = "";
		String PaoMaDengDisplayMrjh   = bkfxsettingprop.getProperty ("PaoMaDengDisplayMrjh");
		if(PaoMaDengDisplayMrjh  != null && PaoMaDengDisplayMrjh .toUpperCase().equals("TRUE") ) {
			String title = "明日计划:";
			PaoMaDengServices svspmd = new PaoMaDengServices ();
			displayinfo = title + svspmd.getPaoMaDengInfo();
		}
			
		String PaoMaDengDisplayNews   = bkfxsettingprop.getProperty ("PaoMaDengDisplayNews");
		if(PaoMaDengDisplayNews  != null && PaoMaDengDisplayNews .toUpperCase().equals("TRUE") ) {
			ServicesForNews svsnews = dateChooser.getStockCalendar().getNewsService();
			Collection<News> allnews = svsnews.getCache().produceNews();
			for(News tmpnews : allnews) {
				displayinfo = displayinfo + "   [ " + tmpnews.getStart().toString() + "  " + tmpnews.getTitle() + " ]   ";
	        }
		}
		
		if(!displayinfo.isEmpty())	pnl_paomd.refreshMessage(displayinfo);
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
	private BanKuaiFengXiLineChartPnl pnlCjeZbGrXuandingZhou;
	private BanKuaiFengXiCandlestickPnl pnlStockCandle;
	private BanKuaiInfoTable tableBkZhanBi;
	private BanKuaiInfoTable tableselectedwkbkzb;
	private BanKuaiGeGuTableFromPropertiesFile tableGuGuZhanBiInBk;
	private BanKuaiGeGuExternalInfoTableFromPropertiesFile tableExternalInfo;
	private BanKuaiGeGuTableFromPropertiesFile tablexuandingzhou;
	private BanKuaiGeGuTableFromPropertiesFile tablexuandingminustwo; //new BanKuaiGeGuTable (this.stockmanager);
	private BanKuaiGeGuTableFromPropertiesFile tablexuandingminusone;
	private BanKuaiGeGuTableFromPropertiesFile tablexuandingplusone;
	private BanKuaiGeGuTableFromPropertiesFile tableTempGeGu;
	private final JPanel contentPanel = new JPanel();
	private JStockCalendarDateChooser dateChooser; //https://toedter.com/jcalendar/
	private JScrollPane sclpcurwkdata;
	private JScrollPane editorPanebankuai;
	private JButton btnresetdate;
	private JStockComboBox combxstockcode;
	private JTabbedPane tabbedPanegegu;
	private JTabbedPane tabbedPanegeguzhanbi;
	private JPanel tfldselectedmsg;
	private JStockComboBox combxsearchbk;
	private PaoMaDeng2 pnl_paomd;
	private JTabbedPane tabbedPanebk;
	private JButton btnexportcsv;
	private Action bkfxCancelAction;
	private JPopupMenu jPopupMenuoftabbedpane;
	private JMenuItem menuItemsiglestocktocsv;
	private JMenuItem menuItemsiglebktocsv;
	private JMenuItem menuItemnonfixperiod;
	private JScrollPane sclpselectedwkdata;
	private JTabbedPane tabbedPanebkzb;
	private JMenuItem menuItemnonshowselectbkinfo;
	private JMenuItem menuItemQiangShibk;
	private JMenuItem menuItemRuoShibk;
	private JMenuItem menuItemQiangShigg;
	private JMenuItem menuItemRuoShigg;
	private JMenuItem menuItemtimerangezhangfu;
	private JCheckBox chxbxwholeweek;
	private TagsPanel pnlbktags;
	private TagsPanel pnlstocktags;
	private JPanel pnlextrainfo;
	private JScrollPane scrldailydata;
	private JScrollPane sclpinfosummary;
	private JMenuItem menuItemTempGeGuFromFile;
	private JMenuItem menuItemTempGeGuFromTDXSw;
//	private JComboBox cbbxmore;
	private JMenuItem menuItemAddRmvStockToYellow;
	private JMenuItem menuItemAddRmvBkToYellow;
	private JMenuItem menuItemTempGeGuFromZhidingbk;
	private JMenuItem menuItemsMrjh;
	private JMenuItem menuItemTempGeGuClear;
	private JMenuItem menuItemAddRmvStockToRed;
	private JMenuItem menuItemDuanQiGuanZhu;
	private JPanel pnlZhiShu;
	private JMenu historycsvfileMenu;
	private BkfxHightLightForGeGuPropertyFilePnl bkhlpnl;
	private JMenu stkhistorycsvfileMenu;
	private JMenuItem menuItemcancelreviewedtoday;
	private JLabel lbllastselect;
	private JTabbedPane tabpnlKxian;
	private BanKuaiFengXiCandlestickPnl pnlBanKuaiCandle;
	public SetExportNodeConditionPnl pnlsetexportcond;
	private JPopupMenu jPopupMenuoftabbedpanebk;
	private JMenuItem menuItemcancelBanKaiReviewedtoday;
	private JMenuItem menuItemAddRmvBkToRedSign;
	private JMenuItem menuItemcancelAllNodesReviewedtoday;
	private JMenuItem menuItemsGeGuToBks;
	private BanKuaiGeGuMergeTable tblmergeggtobks;
	private BanKuaiInfoTable tblmergegegubkinfo;
	private GeGuToBanKuaiTable tblmergegeguinfoinallbk;
	private JMenuItem menuItemsGeGuToBksXuanDingZhou;
	private JScrollPane scrollPangegutobankuaisInfo;
	private JMenuItem menuItemcompareToOtherBk;
	private TreeOfChanYeLian bksocialtreecopy;
	private BanKuaiAndStockTree cyltreecopy;
	private BanKuaiInfoTable tableBkSocialbkCurwkData;
	private JPanel pnlsocialfriendbkdata;
	private JScrollPane sclpselectwkbkfriendsdata;
	private BanKuaiInfoTable tableBkSocialbkSelectwkData;
	private JMenuItem menuItemmarkallreviewedtoday;
	private BanKuaiInfoTable tblDzhBkCurWkZhanBi;
	private BanKuaiInfoTable tblDzhBkCurSelectedWkZhanBi;
	private JMenuItem menuItemAddRmvBkToRedSignDZH;
	private JMenuItem menuItemAddRmvBkToYellowDZH;
	private JMenuItem menuItemQiangShibkDZH;
	private JMenuItem menuItemRuoShibkDZH;
	private JMenuItem menuItemDuanQiGuanZhuDZH;
	private JMenuItem menuItemsiglebktocsvDZH;
	private JMenu historycsvfileMenuDZH;
	private JMenuItem menuItemcompareToOtherBkDZH;
	private JMenuItem menuItemSaveCurWkDataToData;
	private BanKuaiFengXiLineChartPnl pnlbkcjecjezbgr;
	private JTabbedPane tabbedPanePie;
	private JLabel holdbackgrounddownload;

	private void initializeGuiOf2560Resolution ()
	{
		setTitle("\u677F\u5757\u5206\u6790");
		
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		setBounds(10, 10, 2520, 1033);
		getContentPane().setLayout(new BorderLayout());
		
		contentPanel.setBorder(new EmptyBorder(1, 1, 1, 1));
		getContentPane().add(contentPanel, BorderLayout.EAST);
		
		UIManager.put("TabbedPane.selected", Color.yellow); 
		
		JPanel panel = new JPanel();
		JPanel panel_1 = new JPanel();
		
		JPanel panel_2 = new JPanel();
//		paneldayCandle.setBorder(new TitledBorder(null, "\u677F\u5757/\u4E2A\u80A1K\u7EBF\u8D70\u52BF", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		tabbedPanePie = new JTabbedPane(JTabbedPane.TOP);
		tabbedPanePie.setUI(new com.exchangeinfomanager.commonlib.MetalBorderlessTabbedPaneUI());
		scrldailydata = new JScrollPane();
		
		editorPanebankuai = new JScrollPane();
		
		tabpnlKxian = new JTabbedPane(JTabbedPane.TOP);
		tabpnlKxian.setFont(new Font("宋体", Font.PLAIN, 10));
		tabpnlKxian.setUI(new com.exchangeinfomanager.commonlib.MetalBorderlessTabbedPaneUI());
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING, false)
						.addComponent(panel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(panel_1, GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
								.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 1587, Short.MAX_VALUE)
								.addComponent(editorPanebankuai, GroupLayout.DEFAULT_SIZE, 1587, Short.MAX_VALUE))
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addGap(18)
									.addComponent(scrldailydata, GroupLayout.PREFERRED_SIZE, 375, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addGap(14)
									.addComponent(tabbedPanePie, GroupLayout.PREFERRED_SIZE, 380, GroupLayout.PREFERRED_SIZE))))
						.addComponent(tabpnlKxian, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 1980, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(panel, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addGap(243)
									.addComponent(panel_2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(editorPanebankuai, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addGap(12)
									.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 831, GroupLayout.PREFERRED_SIZE))))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(tabpnlKxian, GroupLayout.PREFERRED_SIZE, 330, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(tabbedPanePie, GroupLayout.PREFERRED_SIZE, 274, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(scrldailydata, GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)))
					.addContainerGap())
		);
		
		pnlBanKuaiCandle = new BanKuaiFengXiCandlestickPnl();
		tabpnlKxian.addTab("\u677F\u5757K\u7EBF", null, pnlBanKuaiCandle, null);
		
		String bkzbtablepropFileName =   (new SetupSystemConfiguration()).getSystemInstalledPath() + "/config/" +  bkfxsettingprop.getProperty ("BkfxBanKuaiTableInfoSettingFile")  + "/";
		pnlsocialfriendbkdata = new JPanel();
		tabpnlKxian.addTab("\u677F\u5757SocialFriends", null, pnlsocialfriendbkdata, null);
		pnlsocialfriendbkdata.setLayout(new BoxLayout(pnlsocialfriendbkdata, BoxLayout.X_AXIS));
		JScrollPane sclpcurrentsocialbkdata = new JScrollPane();
		pnlsocialfriendbkdata.add(sclpcurrentsocialbkdata);
		tableBkSocialbkCurwkData = new BanKuaiInfoTable(this.stockmanager,bkzbtablepropFileName);
		sclpcurrentsocialbkdata.setViewportView(tableBkSocialbkCurwkData);
		sclpselectwkbkfriendsdata = new JScrollPane();
		pnlsocialfriendbkdata.add(sclpselectwkbkfriendsdata);
		tableBkSocialbkSelectwkData = new BanKuaiInfoTable(this.stockmanager,bkzbtablepropFileName);
		sclpselectwkbkfriendsdata.setViewportView(tableBkSocialbkSelectwkData);
		
		pnlStockCandle = new BanKuaiFengXiCandlestickPnl();
		tabpnlKxian.addTab("\u4E2A\u80A1K\u7EBF", null, pnlStockCandle, null);
		
		scrollPangegutobankuaisInfo = new JScrollPane();
		tabpnlKxian.addTab("个股所有板块数据", null, scrollPangegutobankuaisInfo, null);
		String BkfxGeGuToBanKuaisMergeTableInfoSettingFile = bkfxsettingprop.getProperty ("BkfxGeGuToBanKuaisMergeTableInfoSettingFile") ;
		List<String> mergeprop = Splitter.on(",").omitEmptyStrings().splitToList(BkfxGeGuToBanKuaisMergeTableInfoSettingFile);
		String BkfxGeGuToBanKuaisMergeTableInfoSettingFilebkfile =   (new SetupSystemConfiguration()).getSystemInstalledPath() + "/config/" + mergeprop.get(0).trim()   + "/";
		tblmergegegubkinfo = new BanKuaiInfoTable (null,BkfxGeGuToBanKuaisMergeTableInfoSettingFilebkfile);
		tblmergegegubkinfo.getRowSorter().setSortKeys(null);
		String bkmergekeywds = mergeprop.get(1).trim();
		String BkfxGeGuToBanKuaisMergeTableInfoSettingFileggfile =   (new SetupSystemConfiguration()).getSystemInstalledPath() + "/config/" + mergeprop.get(2).trim()   + "/";
		tblmergegeguinfoinallbk = new GeGuToBanKuaiTable (BkfxGeGuToBanKuaisMergeTableInfoSettingFileggfile );
		String ggmergekeywds = mergeprop.get(3).trim();
		tblmergeggtobks = new BanKuaiGeGuMergeTable (tblmergegegubkinfo, bkmergekeywds, tblmergegeguinfoinallbk, ggmergekeywds);
		scrollPangegutobankuaisInfo.setViewportView(tblmergeggtobks);
		
		tfldselectedmsg = new JPanel ();
		scrldailydata.setViewportView(tfldselectedmsg);
		tfldselectedmsg.setLayout(new BoxLayout(tfldselectedmsg, BoxLayout.Y_AXIS));
//		tfldselectedmsg.setEditable(false);
		
		pnlcurwkggcjezhanbi = new BanKuaiFengXiPieChartCjePnl(0);
		tabbedPanePie.addTab("CJE\u4E2A\u80A1\u5360\u6BD4", null, pnlcurwkggcjezhanbi, null);
		pnlcurwkggcjezhanbi.setBorder(new TitledBorder(null, "\u677F\u5757\u5F53\u524D\u5468\u4E2A\u80A1\u5360\u6BD4", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		pnlbkcjecjezbgr = new BanKuaiFengXiLineChartPnl();
		tabbedPanePie.addTab("板块CJEZBGR/CJLZBGR", null, pnlbkcjecjezbgr, null);
		
		pnlCjeZbGrXuandingZhou = new BanKuaiFengXiLineChartPnl();
		tabbedPanePie.addTab("CjeZbGr\u9009\u5B9A\u5468", null, pnlCjeZbGrXuandingZhou, null);
//		pnlCjeZbGrXuandingZhou.setBorder(new TitledBorder(null, "\u677F\u5757\u4E0A\u4E00\u5468\u4E2A\u80A1\u5360\u6BD4", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		tabbedPanegeguzhanbi = new JTabbedPane(JTabbedPane.TOP);
		tabbedPanegeguzhanbi.setFont(new Font("宋体", Font.PLAIN, 10));
		tabbedPanegeguzhanbi.setUI(new com.exchangeinfomanager.commonlib.MetalBorderlessTabbedPaneUI());
		this.panelGgDpCjeZhanBi = new BanKuaiFengXiNodeCombinedCategoryPnl("CJE");
		tabbedPanegeguzhanbi.addTab("\u4E2A\u80A1\u989D\u5360\u6BD4", null, panelGgDpCjeZhanBi, null);
		
		panelggdpcjlwkzhanbi = new BanKuaiFengXiNodeCombinedCategoryPnl("CJL");
		tabbedPanegeguzhanbi.addTab("\u4E2A\u80A1\u91CF\u5360\u6BD4", null, panelggdpcjlwkzhanbi, null);
		
		tabbedPanebkzb = new JTabbedPane(JTabbedPane.TOP);
		tabbedPanebkzb.setFont(new Font("宋体", Font.PLAIN, 10));
		tabbedPanebkzb.setUI(new com.exchangeinfomanager.commonlib.MetalBorderlessTabbedPaneUI());
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(
			gl_panel_2.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_panel_2.createSequentialGroup()
					.addContainerGap()
					.addComponent(tabbedPanebkzb, GroupLayout.PREFERRED_SIZE, 734, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(tabbedPanegeguzhanbi, GroupLayout.DEFAULT_SIZE, 823, Short.MAX_VALUE)
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

		pnlbkwkcjlzhanbi = new BanKuaiFengXiNodeCombinedCategoryPnl("CJL");
		tabbedPanebkzb.addTab("\u677F\u5757\u91CF\u5360\u6BD4", null, pnlbkwkcjlzhanbi, null);
		panel_2.setLayout(gl_panel_2);
		
		tabbedPanegegu = new JTabbedPane(JTabbedPane.TOP);
		tabbedPanegegu.setFont(new Font("宋体", Font.PLAIN, 11));
		tabbedPanegegu.setUI(new com.exchangeinfomanager.commonlib.MetalBorderlessTabbedPaneUI());
		tabbedPanebk = new JTabbedPane(JTabbedPane.TOP);
		tabbedPanebk.setFont(new Font("宋体", Font.PLAIN, 11));
//		tabbedPanebk.setFont( new Font( "Dialog", Font.BOLD|Font.ITALIC, 24 ) );
		tabbedPanebk.setUI(new com.exchangeinfomanager.commonlib.MetalBorderlessTabbedPaneUI());
		combxstockcode = new JStockComboBox(BkChanYeLianTreeNode.TDXGG);
		
		combxsearchbk = new JStockComboBox();
		combxsearchbk.setEditable(true);
		
		pnl_paomd = new PaoMaDeng2();
		pnl_paomd.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		
		btnexportcsv = new JButton();
		btnexportcsv.setText("\u5BFC\u51FA\u5230CSV");
		btnexportcsv.setEnabled(false);
		
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addGap(10)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING)
						.addGroup(Alignment.LEADING, gl_panel_1.createSequentialGroup()
							.addComponent(pnl_paomd, GroupLayout.PREFERRED_SIZE, 261, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(combxstockcode, GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
							.addContainerGap())
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(combxsearchbk, GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnexportcsv)
							.addGap(141))
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(tabbedPanegegu, GroupLayout.DEFAULT_SIZE, 441, Short.MAX_VALUE)
							.addContainerGap())))
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addComponent(tabbedPanebk, GroupLayout.DEFAULT_SIZE, 441, Short.MAX_VALUE)
					.addContainerGap())
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
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING, false)
						.addComponent(pnl_paomd, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(combxstockcode, GroupLayout.PREFERRED_SIZE, 31, Short.MAX_VALUE))
					.addContainerGap())
		);
		
		sclpcurwkdata = new JScrollPane();
		tabbedPanebk.addTab("\u5F53\u524D\u5468", null, sclpcurwkdata, null);
		tabbedPanebk.setBackgroundAt(0, Color.ORANGE);
		
//		String bkzbtablepropFileName =   (new SetupSystemConfiguration()).getSystemInstalledPath() + "/config/" +  bkfxsettingprop.getProperty ("BkfxBanKuaiTableInfoSettingFile")  + "/";
		tableBkZhanBi = new BanKuaiInfoTable(this.stockmanager,bkzbtablepropFileName);	
		
		sclpcurwkdata.setViewportView(tableBkZhanBi);
		
		sclpselectedwkdata = new JScrollPane();
		tabbedPanebk.addTab("\u9009\u5B9A\u5468", null, sclpselectedwkdata, null);
		
		tableselectedwkbkzb = new BanKuaiInfoTable(this.stockmanager,bkzbtablepropFileName);
		sclpselectedwkdata.setViewportView(tableselectedwkbkzb);
		
		JScrollPane sclpdzhbk = new JScrollPane();
		tabbedPanebk.addTab("\u5927\u667A\u6167\u5F53\u524D\u5468", null, sclpdzhbk, null);
		
		tblDzhBkCurWkZhanBi = new BanKuaiInfoTable(this.stockmanager,bkzbtablepropFileName);
		sclpdzhbk.setViewportView(tblDzhBkCurWkZhanBi);
		
		JScrollPane sclpdahselectedwkbk = new JScrollPane();
		tabbedPanebk.addTab("\u5927\u667A\u6167\u9009\u5B9A\u5468", null, sclpdahselectedwkbk, null);
		
		tblDzhBkCurSelectedWkZhanBi = new BanKuaiInfoTable(this.stockmanager,bkzbtablepropFileName);
		sclpdahselectedwkbk.setViewportView(tblDzhBkCurSelectedWkZhanBi);
		
		sclpinfosummary = new JScrollPane();
		tabbedPanebk.addTab("\u7EFC\u5408\u4FE1\u606F", null, sclpinfosummary, null);
		
		pnlextrainfo = new JPanel();
		sclpinfosummary.setViewportView(pnlextrainfo);
		pnlextrainfo.setLayout(new BoxLayout(pnlextrainfo, BoxLayout.Y_AXIS));
		
//		JPanel pnlbkstocktags = new JPanel();
//		tabbedPanebk.addTab("\u5173\u952E\u8BCD", null, pnlbkstocktags, null);
//		pnlbkstocktags.setLayout(new BoxLayout(pnlbkstocktags, BoxLayout.Y_AXIS));
		
		JScrollPane scrollPanecyl = new JScrollPane();
		tabbedPanebk.addTab("\u4EA7\u4E1A\u94FE", null, scrollPanecyl, null);
//		InvisibleTreeModel treeModel = (InvisibleTreeModel)this.cyldbopt.getBkChanYeLianTree().getModel();
		cyltreecopy = CreateExchangeTree.CreateTreeOfChanYeLian();//new BanKuaiAndStockTree(treeModel,"cyltreecopy");
		scrollPanecyl.setViewportView(cyltreecopy);
		
		JScrollPane scrollPanesocialbktree = new JScrollPane();
		tabbedPanebk.addTab("\u677F\u5757SocialTree", null, scrollPanesocialbktree, null);
		bksocialtreecopy = CreateExchangeTree.CreateCopyOfTDXBankuaiSocialTree();
		CreateExchangeTree.CreateTreeOfBanKuaiSocialFriends().addCylTreeUpdatedListener (bksocialtreecopy);
		scrollPanesocialbktree.setViewportView(bksocialtreecopy);
		
		String ggzbtablepropFileName =   (new SetupSystemConfiguration()).getSystemInstalledPath() + "/config/" +  bkfxsettingprop.getProperty ("BkfxGeGuTableInfoSettingFile")  + "/";
		String ggexternaltablepropFileName =   (new SetupSystemConfiguration()).getSystemInstalledPath() + "/config/" +  bkfxsettingprop.getProperty ("BkfxGeGuExternalTableInfoSettingFile")  + "/";
		JScrollPane scrollPanedangqian = new JScrollPane();
		tabbedPanegegu.addTab("当前周", null, scrollPanedangqian, null);
		tabbedPanegegu.setBackgroundAt(0, Color.ORANGE);
		tableGuGuZhanBiInBk = new BanKuaiGeGuTableFromPropertiesFile (this.stockmanager,ggzbtablepropFileName);
		scrollPanedangqian.setViewportView(tableGuGuZhanBiInBk);
		JScrollBarNodesMark scrollbarGuGuZhanBiInBk = new JScrollBarNodesMark (tableGuGuZhanBiInBk);
	    scrollPanedangqian.setVerticalScrollBar(scrollbarGuGuZhanBiInBk);
		
		JScrollPane scrollPanGeGuExtralInfo = new JScrollPane();
		tabbedPanegegu.addTab("\u5176\u4ED6\u4FE1\u606F", null, scrollPanGeGuExtralInfo, null);
		tableExternalInfo = new BanKuaiGeGuExternalInfoTableFromPropertiesFile (this.stockmanager,ggexternaltablepropFileName);
		scrollPanGeGuExtralInfo.setViewportView(tableExternalInfo);
		JScrollBarNodesMark scrollbarExternalGeGu = new JScrollBarNodesMark (tableExternalInfo);
		scrollPanGeGuExtralInfo.setVerticalScrollBar(scrollbarExternalGeGu);
		
		JScrollPane scrollPanexuanding = new JScrollPane();
		tabbedPanegegu.addTab("选定周", null, scrollPanexuanding, null);
		tablexuandingzhou = new BanKuaiGeGuTableFromPropertiesFile (this.stockmanager,ggzbtablepropFileName);
		scrollPanexuanding.setViewportView(tablexuandingzhou);
		JScrollBarNodesMark scrollbarxuandingzhou = new JScrollBarNodesMark (tablexuandingzhou);
		scrollPanexuanding.setVerticalScrollBar(scrollbarxuandingzhou);
		
		JScrollPane scrollPanexuandingminusone = new JScrollPane();
		tabbedPanegegu.addTab("\u9009\u5B9A\u5468-1", null, scrollPanexuandingminusone, null);
		tablexuandingminusone = new BanKuaiGeGuTableFromPropertiesFile (this.stockmanager,ggzbtablepropFileName);
		scrollPanexuandingminusone.setViewportView(tablexuandingminusone);
		JScrollBarNodesMark scrollbarxuandingminusone = new JScrollBarNodesMark (tablexuandingminusone);
		scrollPanexuandingminusone.setVerticalScrollBar(scrollbarxuandingminusone);
		
		JScrollPane scrollPanexuandingminustwo = new JScrollPane();
		tabbedPanegegu.addTab("\u9009\u5B9A\u5468-2", null, scrollPanexuandingminustwo, null);
		tablexuandingminustwo = new BanKuaiGeGuTableFromPropertiesFile (this.stockmanager,ggzbtablepropFileName);
		scrollPanexuandingminustwo.setViewportView(tablexuandingminustwo);
		JScrollBarNodesMark scrollbarxuandingminustwo = new JScrollBarNodesMark (tablexuandingminustwo);
		scrollPanexuandingminustwo.setVerticalScrollBar(scrollbarxuandingminustwo);
		
		JScrollPane scrollPanexuandingplusone = new JScrollPane();
		tabbedPanegegu.addTab("\u9009\u5B9A\u5468+1", null, scrollPanexuandingplusone, null);
		
		tablexuandingplusone = new BanKuaiGeGuTableFromPropertiesFile (this.stockmanager,ggzbtablepropFileName);
		scrollPanexuandingplusone.setViewportView(tablexuandingplusone);
		JScrollBarNodesMark scrollbarxuandingplusone = new JScrollBarNodesMark (tablexuandingplusone);
		scrollPanexuandingplusone.setVerticalScrollBar(scrollbarxuandingplusone);
		
		JScrollPane scrollPaneTempGeGu = new JScrollPane();
		String tempggzbtablepropFileName =   (new SetupSystemConfiguration()).getSystemInstalledPath() + "/config/" +  bkfxsettingprop.getProperty ("BkfxTempGeGuTableInfoSettingFile")  + "/";
		tableTempGeGu = new BanKuaiGeGuTableFromPropertiesFile (this.stockmanager,tempggzbtablepropFileName);
		scrollPaneTempGeGu.setViewportView(tableTempGeGu);
		
		JScrollBarNodesMark scrollbarTempGeGu = new JScrollBarNodesMark (tableTempGeGu);
	    scrollPaneTempGeGu.setVerticalScrollBar(scrollbarTempGeGu);
		
		tabbedPanegegu.addTab("\u4E34\u65F6\u4E2A\u80A1", null, scrollPaneTempGeGu, null);
		tabbedPanegegu.setBackgroundAt(6, Color.CYAN);
		
		panel_1.setLayout(gl_panel_1);
		
//		dateChooser = new JDateChooser();
		dateChooser = new JStockCalendarDateChooser(new StockCalendar());
		dateChooser.setDateFormatString("yyyy-MM-dd");
//		dateChooser.setDate(new Date() );
		
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
		
		chxbxwholeweek = new JCheckBox("\u9009\u62E9\u4EFB\u4F55\u4E00\u5929\u90FD\u8BA1\u7B97\u5B8C\u6574\u5468");
		chxbxwholeweek.setToolTipText("\u5982\u4E0D\u52FE\u9009\uFF0C\u5219\u8BA1\u7B97\u5230\u88AB\u9009\u62E9\u7684\u90A3\u4E00\u5929");
		chxbxwholeweek.setSelected(true);
		
		pnlZhiShu = new JPanel();
		
		lbllastselect = new JLabel("New label");
		
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(dateChooser, GroupLayout.PREFERRED_SIZE, 133, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(chxbxwholeweek)
							.addGap(18)
							.addComponent(btnresetdate)
							.addGap(18)
							.addComponent(lbllastselect))
						.addComponent(pnlZhiShu, GroupLayout.PREFERRED_SIZE, 473, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
						.addComponent(dateChooser, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
							.addComponent(chxbxwholeweek)
							.addComponent(btnresetdate, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
							.addComponent(lbllastselect)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(pnlZhiShu, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		
		panel.setLayout(gl_panel);
		
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
			//high light panel
			String ggzbtablepropFileName1 =   (new SetupSystemConfiguration()).getSystemInstalledPath() + "/config/" +  bkfxsettingprop.getProperty ("BkfxGeGuTableInfoSettingFile")  + "/";
			String ggexternaltablepropFileName1 =   (new SetupSystemConfiguration()).getSystemInstalledPath() + "/config/" +  bkfxsettingprop.getProperty ("BkfxGeGuExternalTableInfoSettingFile")  + "/";
			String bkzbtablepropFileName1 =   (new SetupSystemConfiguration()).getSystemInstalledPath() + "/config/" +  bkfxsettingprop.getProperty ("BkfxBanKuaiTableInfoSettingFile")  + "/";
			bkhlpnl = new BkfxHightLightForGeGuPropertyFilePnl (this.bkggmatchcondition,ggzbtablepropFileName1,ggexternaltablepropFileName1,bkzbtablepropFileName1);
//			bkhlpnl.setCurrentDisplayDate(this.dateChooser.getLocalDate() );
//			bkhlpnl.setCurrentExportPeriod(this.globeperiod);
			buttonPane.add(bkhlpnl);
			
			bkhlpnl.addPropertyChangeListener(new PropertyChangeListener() {

	            public void propertyChange(PropertyChangeEvent evt) {

	                if (evt.getPropertyName().equals(BkfxHightLightForGeGuPropertyFilePnl.TIMESHOULDCHANGE_PROPERTY)) {
	                    @SuppressWarnings("unchecked")
	                    LocalDate newdata = (LocalDate) evt.getNewValue();
	                    LocalDate curselectdate = dateChooser.getLocalDate();
	    				if(!curselectdate.equals(newdata) ) dateChooser.setLocalDate(newdata);
	                } 
	             }
	        });
			//Export data			
			pnlsetexportcond = new SetExportNodeConditionPnl (this.bkggmatchcondition);
			pnlsetexportcond.setStockInfoManager(this.stockmanager );
			buttonPane.add(pnlsetexportcond);
			//
			holdbackgrounddownload = JLabelFactory.createButton("",25, 25);
			holdbackgrounddownload.setHorizontalAlignment(SwingConstants.LEFT);
			holdbackgrounddownload.setToolTipText("后台下载数据暂停,点击重启下载。");
			holdbackgrounddownload.setIcon(new ImageIcon(BanKuaiFengXi.class.getResource("/images/trafficlight-in-green.png")));
			holdbackgrounddownload.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) 
				{
					if(SwingUtilities.isLeftMouseButton(arg0) ) {
						startRestartBackGroundDownload ();
					} else 
					if (SwingUtilities.isRightMouseButton(arg0)) {
					} else if (SwingUtilities.isMiddleMouseButton(arg0)) {							
					}
				}
			});
			buttonPane.add(holdbackgrounddownload);
		}
		
		reFormatTDXBanKuaiGui ();
		reFormatDZHBanKuaiGui ();
		reFormatStockGui ();
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//* 所有辅助性的函数都在这里
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private void startRestartBackGroundDownload ()
	{
		if(holdbackgrounddownload.getToolTipText().contains("重启")) {
			ImageIcon icon = stockmanager.setGetNodeDataFromDbWhenSystemIdleThreadStatus(false);
			holdbackgrounddownload.setToolTipText("后台下载数据,点击暂停下载。");
			holdbackgrounddownload.setIcon(icon);
		} else {
			if(globecalwholeweek) { //非完整周不缓存数据，否则可能出错
				ImageIcon icon = stockmanager.setGetNodeDataFromDbWhenSystemIdleThreadStatus(true);
				holdbackgrounddownload.setToolTipText("后台下载数据暂停,点击重启下载。");
				holdbackgrounddownload.setIcon(icon);
			} else	JOptionPane.showMessageDialog(null,"系统处于计算非完整周状态， 为避免错误，将无法缓存数据!","Warning",JOptionPane.WARNING_MESSAGE);
		}
	}
	/*
	 * 个股表里的板块占比MAX暂时不用，先隐藏
	 */
	private void  reFormatTDXBanKuaiGui ()
	{

	     jPopupMenuoftabbedpanebk = new JPopupMenu();
	     menuItemcancelBanKaiReviewedtoday = new JMenuItem("取消已经阅读状态"); //系统默认按成交额排名
	     jPopupMenuoftabbedpanebk.add(menuItemcancelBanKaiReviewedtoday);
	     menuItemcancelAllNodesReviewedtoday = new JMenuItem("取消所有板块个股阅读状态"); //系统默认按成交额排名
	     jPopupMenuoftabbedpanebk.add(menuItemcancelAllNodesReviewedtoday);
	     menuItemSaveCurWkDataToData = new JMenuItem("保存本表数据到数据库");
	     menuItemSaveCurWkDataToData.setEnabled(false);
	     jPopupMenuoftabbedpanebk.add(menuItemSaveCurWkDataToData);
	     
		   JMenu biaojiMenu = new JMenu("标记板块");
	       menuItemAddRmvBkToRedSign = new JMenuItem("设置/取消红标");
	       biaojiMenu.add(menuItemAddRmvBkToRedSign );
	       menuItemAddRmvBkToYellow = new JMenuItem("设置/取消黄标");
	       biaojiMenu.add(menuItemAddRmvBkToYellow );
	       
	       menuItemQiangShibk = new JMenuItem("强势板块");
		   biaojiMenu .add(menuItemQiangShibk);
		   menuItemRuoShibk = new JMenuItem("弱势板块");
		   biaojiMenu .add(menuItemRuoShibk);
		   menuItemDuanQiGuanZhu = new JMenuItem("短期关注");
		   biaojiMenu .add(menuItemDuanQiGuanZhu);
	       
	       tableBkZhanBi.getPopupMenu().add(biaojiMenu);
	       
	       JMenu bkcsvMenu = new JMenu("CSV");
		   tableBkZhanBi.getPopupMenu().add(bkcsvMenu);
	       menuItemsiglebktocsv = new JMenuItem("导出CSV");
	       bkcsvMenu.add(menuItemsiglebktocsv);
	       historycsvfileMenu = new JMenu("历史分析文件");
	       bkcsvMenu.add(historycsvfileMenu);
	       
	       menuItemcompareToOtherBk = new JMenuItem("对比板块");
	       tableBkZhanBi.getPopupMenu().add(menuItemcompareToOtherBk);
	       
	       menuItemnonshowselectbkinfo = new JMenuItem("同时分析选定周数据");
	       panelbkwkcjezhanbi.addMenuItem (menuItemnonshowselectbkinfo,0);
	}
	private void reFormatDZHBanKuaiGui ()
	{
		   JMenu biaojiMenu = new JMenu("标记板块");
	       menuItemAddRmvBkToRedSignDZH = new JMenuItem("设置/取消红标");
	       biaojiMenu.add(menuItemAddRmvBkToRedSignDZH );
	       menuItemAddRmvBkToYellowDZH = new JMenuItem("设置/取消黄标");
	       biaojiMenu.add(menuItemAddRmvBkToYellowDZH );
	       
	       menuItemQiangShibkDZH = new JMenuItem("强势板块");
		   biaojiMenu .add(menuItemQiangShibkDZH);
		   menuItemRuoShibkDZH = new JMenuItem("弱势板块");
		   biaojiMenu .add(menuItemRuoShibkDZH);
		   menuItemDuanQiGuanZhuDZH = new JMenuItem("短期关注");
		   biaojiMenu .add(menuItemDuanQiGuanZhuDZH);
	       
		   tblDzhBkCurWkZhanBi.getPopupMenu().add(biaojiMenu);
	       
	       JMenu bkcsvMenu = new JMenu("CSV");
	       tblDzhBkCurWkZhanBi.getPopupMenu().add(bkcsvMenu);
	       menuItemsiglebktocsvDZH = new JMenuItem("导出CSV");
	       bkcsvMenu.add(menuItemsiglebktocsvDZH);
	       historycsvfileMenuDZH = new JMenu("历史分析文件");
	       bkcsvMenu.add(historycsvfileMenuDZH);
	       
	       menuItemcompareToOtherBkDZH = new JMenuItem("对比板块");
	       tblDzhBkCurWkZhanBi.getPopupMenu().add(menuItemcompareToOtherBkDZH);
	}
	private void reFormatStockGui ()
	{
		jPopupMenuoftabbedpane = new JPopupMenu();
		menuItemtimerangezhangfu = new JMenuItem("按阶段涨幅排名"); //系统默认按成交额排名
       jPopupMenuoftabbedpane.add(menuItemtimerangezhangfu);
      
       menuItemcancelreviewedtoday = new JMenuItem("取消已经阅读状态"); //系统默认按成交额排名
       jPopupMenuoftabbedpane.add(menuItemcancelreviewedtoday);
       
       menuItemmarkallreviewedtoday = new JMenuItem("全部标记已经阅读"); //系统默认按成交额排名
       jPopupMenuoftabbedpane.add(menuItemmarkallreviewedtoday);
       	   
	   menuItemsMrjh = new JMenuItem("明日计划");
	   tableGuGuZhanBiInBk.getPopupMenu().add(menuItemsMrjh);
	   
	   menuItemsGeGuToBks = new JMenuItem("计算个股所有板块");
	   tableGuGuZhanBiInBk.getPopupMenu().add(menuItemsGeGuToBks);
	   
	   JMenu stkcsvMenu = new JMenu("CSV");
	   tableGuGuZhanBiInBk.getPopupMenu().add(stkcsvMenu);
	   menuItemsiglestocktocsv = new JMenuItem("导出CSV");
       stkcsvMenu.add(menuItemsiglestocktocsv);
       stkhistorycsvfileMenu = new JMenu("历史分析文件");
       stkcsvMenu.add(stkhistorycsvfileMenu);
       
       menuItemnonfixperiod = new JMenuItem("不定周期DPM??WK");
       tableGuGuZhanBiInBk.getPopupMenu().add(menuItemnonfixperiod);
       
       JMenu stockbiaojiMenu = new JMenu("标记个股");
       menuItemAddRmvStockToRed = new JMenuItem("设置/取消红标");
       stockbiaojiMenu .add(menuItemAddRmvStockToRed );
       menuItemAddRmvStockToYellow = new JMenuItem("设置/取消黄标");
       stockbiaojiMenu .add(menuItemAddRmvStockToYellow );
       menuItemQiangShigg = new JMenuItem("设为强势个股");
	   menuItemRuoShigg = new JMenuItem("设为弱势个股");
	   stockbiaojiMenu .add(menuItemQiangShigg);
	   stockbiaojiMenu .add(menuItemRuoShigg);
       tableGuGuZhanBiInBk.getPopupMenu().add(stockbiaojiMenu);
       
       menuItemsGeGuToBksXuanDingZhou = new JMenuItem("计算个股所有板块");
       tablexuandingzhou.getPopupMenu().add(menuItemsGeGuToBksXuanDingZhou);
       
       JPopupMenu popupMenuGeguNews = new JPopupMenu () ;
       menuItemTempGeGuFromFile = new JMenuItem("文件导入");
       popupMenuGeguNews.add(menuItemTempGeGuFromFile);
       menuItemTempGeGuFromTDXSw = new JMenuItem("通达信自定义板块导入");
       popupMenuGeguNews.add(menuItemTempGeGuFromTDXSw);
       menuItemTempGeGuFromZhidingbk = new JMenuItem("指定板块导入");
       popupMenuGeguNews.add(menuItemTempGeGuFromZhidingbk);
       menuItemTempGeGuClear = new JMenuItem("清空");
       popupMenuGeguNews.add(menuItemTempGeGuClear);
       
       tableTempGeGu.getTableHeader().getComponentPopupMenu().add(menuItemTempGeGuFromFile,0);
       tableTempGeGu.getTableHeader().getComponentPopupMenu().add(menuItemTempGeGuFromTDXSw,0);
       tableTempGeGu.getTableHeader().getComponentPopupMenu().add(menuItemTempGeGuFromZhidingbk,0);
       tableTempGeGu.getTableHeader().getComponentPopupMenu().add(menuItemTempGeGuClear,0);
       JSeparator separator = new JSeparator ();
       tableTempGeGu.getTableHeader().getComponentPopupMenu().add(separator,4);
	}
	/**
	 * 
	 */
	private void clearTheGuiBeforDisplayNewInfoSection1 ()
	{
//		tabbedPanebk.setSelectedIndex(0);
		((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).deleteAllRows();
		((BanKuaiInfoTableModel)tblDzhBkCurWkZhanBi.getModel()).deleteAllRows();
		((BanKuaiInfoTableModel)tblDzhBkCurWkZhanBi.getModel()).deleteAllRows();
		((BanKuaiInfoTableModel)tblDzhBkCurSelectedWkZhanBi.getModel()).deleteAllRows();
		btnresetdate.setEnabled(true);
		pnlStockCandle.resetData();
		pnlBanKuaiCandle.resetData();
	}
	private void clearTheGuiBeforDisplayNewInfoSection2 ()
	{
		((BanKuaiInfoTableModel)tableselectedwkbkzb.getModel()).deleteAllRows();
		tabbedPanebk.setTitleAt(1, "选定周" );
		
		tabbedPanegegu.setSelectedIndex(0);
		
		panelbkwkcjezhanbi.resetData();
		pnlbkwkcjlzhanbi.resetData();
		pnlcurwkggcjezhanbi.resetData();
		pnlCjeZbGrXuandingZhou.resetData();
		pnlbkcjecjezbgr.resetData();
		
		tabbedPanegegu.setTitleAt(0, this.getTabbedPanegeguTabTiles(0) );
		tabbedPanegegu.setTitleAt(1, this.getTabbedPanegeguTabTiles(1) );
		tabbedPanegegu.setTitleAt(2, this.getTabbedPanegeguTabTiles(2) );
		tabbedPanegegu.setTitleAt(3, this.getTabbedPanegeguTabTiles(3) );
		tabbedPanegegu.setTitleAt(4, this.getTabbedPanegeguTabTiles(4) );
		tabbedPanegegu.setTitleAt(5, this.getTabbedPanegeguTabTiles(5) );
		
		((BanKuaiGeGuBasicTableModel)tableGuGuZhanBiInBk.getModel()).deleteAllRows();
		((BanKuaiGeGuBasicTableModel)tableExternalInfo.getModel()).deleteAllRows();
		((BanKuaiGeGuBasicTableModel)tablexuandingzhou.getModel()).deleteAllRows();
		((BanKuaiGeGuBasicTableModel)tablexuandingminusone.getModel()).deleteAllRows();
		((BanKuaiGeGuBasicTableModel)tablexuandingminustwo.getModel()).deleteAllRows();
		((BanKuaiGeGuBasicTableModel)tablexuandingplusone.getModel()).deleteAllRows();
		((BanKuaiInfoTableModel)tableBkSocialbkCurwkData.getModel()).deleteAllRows();
		((BanKuaiInfoTableModel)tableBkSocialbkSelectwkData.getModel()).deleteAllRows();
				
//		menuItemchengjiaoer.setText("X 按成交额排名");
//    	menuItemliutong.setText("按流通市值排名");
    	menuItemtimerangezhangfu.setText("按阶段涨幅排名");
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
		panelGgDpCjeZhanBi.resetData();
		panelggdpcjlwkzhanbi.resetData();
		
		((BanKuaiGeGuMergeTableModel)tblmergeggtobks.getModel()).deleteAllRows();
		tblmergeggtobks.repaint();
		
		tabpnlKxian.setTitleAt(3, "个股板块计算");
	}
	/*
	 * 
	 */
	private void unifiedTabbedPanSelectionActions (TDXNodes node, LocalDate analysisdate)
	{
		LocalDate curdate = this.dateChooser.getLocalDate();
		if(node.getType() == BkChanYeLianTreeNode.TDXBK ) {
			if( CommonUtility.isInSameWeek( analysisdate, curdate) ) {
				tabbedPanebk.setSelectedIndex(0);
				tabbedPanebk.setTitleAt(0,"当前周");
				
				tabbedPanegegu.setSelectedIndex(0);
				tabbedPanegegu.setTitleAt(0,"当前周" );
				tabbedPanePie.setSelectedIndex(1);
			}
			else {	tabbedPanebk.setSelectedIndex(1);
					tabbedPanebk.setTitleAt(1, "选定周" + analysisdate );
					
					tabbedPanegegu.setSelectedIndex(2);
					tabbedPanegegu.setTitleAt(2,"选定周" + analysisdate );
					
					tabbedPanePie.setSelectedIndex(2);
			}
			
			tabbedPanebkzb.setSelectedIndex(0);
			tabpnlKxian.setSelectedIndex(1);
		} else	
		if(node.getType() == BkChanYeLianTreeNode.DZHBK ) {
			if( CommonUtility.isInSameWeek( analysisdate, curdate) )	{
				tabbedPanebk.setSelectedIndex(2);
				tabbedPanebk.setTitleAt(2,"大智慧当前周");
				
				tabbedPanegegu.setSelectedIndex(0);
				tabbedPanegegu.setTitleAt(0,"大智慧当前周" );
				tabbedPanePie.setSelectedIndex(1);
			}
			else {	tabbedPanebk.setSelectedIndex(3);
					tabbedPanebk.setTitleAt(3, "大智慧选定周" + analysisdate );
					
					tabbedPanegegu.setSelectedIndex(2);
					tabbedPanegegu.setTitleAt(2,"大智慧选定周" + analysisdate );
					tabbedPanePie.setSelectedIndex(2);
			}
			
			tabbedPanebkzb.setSelectedIndex(0);
			tabpnlKxian.setSelectedIndex(1);
		} else	
		if(node.getType() == BkChanYeLianTreeNode.TDXGG ) {
//			tabbedPanebk.setSelectedIndex(2);
			tabpnlKxian.setSelectedIndex(2);
			return;
		} 
		
		if( ((BanKuai)node).getBanKuaiLeiXing().equals(BanKuai.HASGGWITHSELFCJL) ) {//应该是有个股的板块点击才显示她的个股，
			LocalDate formerdate = ((BanKuaiGeGuBasicTableModel)tablexuandingzhou.getModel()).getCurDisplayedDate();
			if(formerdate != null) { //上一次的选定周数据挪到tab 4
				//显示选定周+1股票排名情况
				tabbedPanegegu.setTitleAt(5,  formerdate.toString() );
			}
			//显示选定周股票排名情况
//			tabbedPanegegu.setTitleAt(2, this.getTabbedPanegeguTabTiles(2) + analysisdate);
			//显示选定周-1股票排名情况
			LocalDate selectdate2 = analysisdate.minus(1,ChronoUnit.WEEKS).with(DayOfWeek.FRIDAY);
			tabbedPanegegu.setTitleAt(3,  selectdate2 + "(-1)");
			//显示选定周-2股票排名情况
			LocalDate selectdate3 = analysisdate.plus(1,ChronoUnit.WEEKS).with(DayOfWeek.FRIDAY);
			tabbedPanegegu.setTitleAt(4,  selectdate3 + "(+1)");
		}
	}
	/*
	 * 
	 */
	private void setFirstSelectedTab ()
	{
		String BanKuaiTabPanelFirstSelectedTab  = bkfxsettingprop.getProperty ("BanKuaiTabPanelFirstSelectedTab");
		if(BanKuaiTabPanelFirstSelectedTab != null && BanKuaiTabPanelFirstSelectedTab.equalsIgnoreCase("CJE"))
			tabbedPanebkzb.setSelectedIndex(0);
		else
			tabbedPanebkzb.setSelectedIndex(1);

		String GeGuTabPanelFirstSelectedTab  = bkfxsettingprop.getProperty ("GeGuTabPanelFirstSelectedTab");
		if(GeGuTabPanelFirstSelectedTab != null && GeGuTabPanelFirstSelectedTab.equalsIgnoreCase("CJE"))
			tabbedPanegeguzhanbi.setSelectedIndex(0);
		else
			tabbedPanegeguzhanbi.setSelectedIndex(1);
	}
	/*
	 * 在个股表中发现输入的个股,返回在主表中是否发现改个股
	 */
	private Boolean findInputedNodeInStcokTables(String nodecode) 
	{
		int rowindexcount = 0;
		nodecode = nodecode.substring(0,6);
		
		for(BanKuaiandGeGuTableBasic tmptable : this.tablebkggtableset ) {
			Boolean found = findInputedNodeInSpecificTable( tmptable,  nodecode) ;
			if(found != null && found) rowindexcount ++;
		}
		if(rowindexcount>0)	return true;
		else return false;
	}
	private Boolean findInputedNodeInBanKuaiTables(String nodecode) 
	{
		int rowindexcount = 0;
		nodecode = nodecode.substring(0,6);
		
		for(BanKuaiandGeGuTableBasic tmptable : this.tableallbktablesset ) {
			Boolean found = findInputedNodeInSpecificTable( tmptable,  nodecode) ;
			if(found != null && found) rowindexcount ++;
		}
		if(rowindexcount>0)	return true;
		else return false;
	}
	private Boolean findInputedNodeInSpecificTable(BanKuaiandGeGuTableBasic tmptable, String nodecode) 
	{

		if(  ((BandKuaiAndGeGuTableBasicModel)tmptable.getModel() ).getRowCount() <= 0 ) 
			return null;
		
		Component focus = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
		if(tmptable == focus)
			return true;

		int rowindex = ((BandKuaiAndGeGuTableBasicModel)tmptable.getModel() ).getNodeRowIndex(nodecode);
		if(rowindex < 0) return false;
				
		if (!(tmptable.getParent() instanceof JViewport))	return null;

		int modelRow = tmptable.convertRowIndexToView(rowindex); //convertRowIndexToModel
//	    JViewport viewport = (JViewport)tmptable.getParent();
//	        // This rectangle is relative to the table where the
//	        // northwest corner of cell (0,0) is always (0,0).
//	    Rectangle rect = tmptable.getCellRect(modelRow, 0, true);
//	        // The location of the viewport relative to the table
//	    Point pt = viewport.getViewPosition();
//	        // Translate the cell location so that it is relative
//	        // to the view, assuming the northwest corner of the
//	        // view is (0,0)
//	    rect.setLocation(rect.x-pt.x, rect.y-pt.y);
//
//	    tmptable.scrollRectToVisible(rect);
		
		JViewport viewport = (JViewport)tmptable.getParent();
        // view dimension
        Dimension dim = viewport.getExtentSize();
        // cell dimension
        Dimension dimOne = new Dimension(0,0);

        // This rectangle is relative to the table where the
        // northwest corner of cell (0,0) is always (0,0).
        Rectangle rect = tmptable.getCellRect(modelRow, 0, true);
        Rectangle rectOne;
        if (modelRow+1<tmptable.getRowCount()) {
            
            rectOne = tmptable.getCellRect(modelRow+1, 0, true);
            dimOne.width=rectOne.x-rect.x;
            dimOne.height=rectOne.y-rect.y;
        }

        // '+ veiw dimension - cell dimension' to set first selected row on the top
        rect.setLocation(rect.x+dim.width-dimOne.width, rect.y+dim.height-dimOne.height);

        tmptable.scrollRectToVisible(rect);
		try {	tmptable.setRowSelectionInterval(modelRow, modelRow);
		} catch ( java.lang.IllegalArgumentException e) { e.printStackTrace(); System.out.print(modelRow ); return false;}
//			int curselectrow = tmptable.getSelectedRow();
//			try {
//				tmptable.setRowSelectionInterval(modelRow, modelRow);
//				tmptable.scrollRectToVisible(new Rectangle(tmptable.getCellRect(modelRow, 0, true)));
//			} catch (java.lang.IllegalArgumentException ex) {}
		
		tmptable.revalidate();
		tmptable.repaint();
		
		return true;
	}
	private StockOfBanKuai getSelectedStockOfBanKuai ()
	{
		int row = tableGuGuZhanBiInBk.getSelectedRow();
		if(row <0) {
			JOptionPane.showMessageDialog(null,"请选择一个个股","Warning",JOptionPane.WARNING_MESSAGE);
			return null;
		}
		int modelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(row); 
		StockOfBanKuai stock = (StockOfBanKuai) ((BanKuaiGeGuBasicTableModel)tableGuGuZhanBiInBk.getModel()).getNode(modelRow);
		return stock;
	}
	private BanKuai getSelectedTDXBanKuai ()
	{
		int row = tableBkZhanBi.getSelectedRow();
		if(row <0) {
			JOptionPane.showMessageDialog(null,"请选择一个板块","Warning",JOptionPane.WARNING_MESSAGE);
			return null;
		}
		int modelRow = tableBkZhanBi.convertRowIndexToModel(row); 
		BanKuai bankuai = (BanKuai) ((BanKuaiInfoTableModel) tableBkZhanBi.getModel()).getNode(modelRow);
		return bankuai;
	}
	private BanKuai getXuanDingZhouSelectedTDXBanKuai ()
	{
		int row = tableselectedwkbkzb.getSelectedRow();
		if(row <0) {
			JOptionPane.showMessageDialog(null,"请选择一个板块","Warning",JOptionPane.WARNING_MESSAGE);
			return null;
		}
		int modelRow = tableselectedwkbkzb.convertRowIndexToModel(row); 
		BanKuai bankuai = (BanKuai) ((BanKuaiInfoTableModel) tableselectedwkbkzb.getModel()).getNode(modelRow);
		return bankuai;
	}
	private BanKuai getSelectedDZHBanKuai ()
	{
		int row = tblDzhBkCurWkZhanBi.getSelectedRow();
		if(row <0) {	JOptionPane.showMessageDialog(null,"请选择一个板块！","Warning",JOptionPane.WARNING_MESSAGE);
			return null;
		}
		int modelRow = tblDzhBkCurWkZhanBi.convertRowIndexToModel(row);
		BanKuai selectedbk = (BanKuai) ((BanKuaiInfoTableModel)tblDzhBkCurWkZhanBi.getModel()).getNode(modelRow);
		return selectedbk;
	}
	private BanKuai getXuanDingZhouSelectedDZHBanKuai ()
	{
		int row = tblDzhBkCurSelectedWkZhanBi.getSelectedRow();
		if(row <0) {	JOptionPane.showMessageDialog(null,"请选择一个板块！","Warning",JOptionPane.WARNING_MESSAGE);
			return null;
		}
		int modelRow = tblDzhBkCurSelectedWkZhanBi.convertRowIndexToModel(row);
		BanKuai selectedbk = (BanKuai) ((BanKuaiInfoTableModel)tblDzhBkCurSelectedWkZhanBi.getModel()).getNode(modelRow);
		return selectedbk;
	}
	/**
	 * 
	 */
	private void playAnaylsisFinishNoticeSound ()
	{
		String prop_analysisfinishnoticesoundfile = bkfxsettingprop.getProperty("analysisfinishnoticesoundfile");
		if(prop_analysisfinishnoticesoundfile == null) return;
		
		String analysisfinishnoticesoundfile = (new SetupSystemConfiguration()).getSystemInstalledPath() + "\\audio\\" + prop_analysisfinishnoticesoundfile;
		CommonUtility.playSound(analysisfinishnoticesoundfile);
	}
	/*
	 * 
	 */
	private void initializeKuaiJieZhishuPnl ()
	{
		String zhishunumber  = bkfxsettingprop.getProperty ("kuaijiezhishunumber");
		int count = Integer.parseInt(zhishunumber);
		for(int i=1; i<=count; i++) {
			String propname = "kuaijiezhishu" + String.valueOf(i) + "_name";
			String zhishuname  = bkfxsettingprop.getProperty (propname);
			if(zhishuname == null)
				continue;
			
			propname = "kuaijiezhishu" + String.valueOf(i) + "_code";
			String zhishucode  = bkfxsettingprop.getProperty (propname);
			JLabel lblNewLabel = new JLabel(zhishuname);
			if(zhishucode != null) {
				lblNewLabel.setName(zhishucode);
				lblNewLabel.setToolTipText(zhishucode);
			} else {
				lblNewLabel.setName("NoCode");
				lblNewLabel.setToolTipText("NoCode");
			}
			propname = "kuaijiezhishu" + String.valueOf(i) + "_color";
			String zhishucolor  = bkfxsettingprop.getProperty (propname);
			if(zhishucolor != null )
				lblNewLabel.setForeground( Color.decode(zhishucolor)  );
			
			pnlZhiShu.add(lblNewLabel);
			pnlZhiShu.add(new JLabel("  "));
			
			lblNewLabel.addMouseListener(new MouseAdapter() {
				 @Override
			        public void mouseClicked(MouseEvent e) {
			            super.mouseClicked(e);
			            JLabel label = (JLabel) e.getSource();
			            
			        	if (e.getClickCount() == 1) { 
			        		String zhishu_code = label.getName();
			        		if(zhishu_code.trim().toUpperCase().equals("NOCODE"))
			        			return;
			        		
			        		if(zhishu_code.trim().equals("000000")) {
			        			pnlBanKuaiCandle.resetData();
			        			pnlStockCandle.resetData();
			        			clearTheGuiBeforDisplayNewInfoSection2 ();
			        			clearTheGuiBeforDisplayNewInfoSection3 ();
			        			tabbedPanebk.setSelectedIndex(0);
			        			tabbedPanebkzb.setSelectedIndex(0);
			        			
			        			refreshDaPanFengXiResult ();
			        		}
			        		else {
			        			BanKuai shanghai = (BanKuai) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(zhishu_code, BkChanYeLianTreeNode.TDXBK);
			    				unifiedOperationsAfterUserSelectABanKuai (shanghai);
			    				
			    				// 定位
			    				Integer rowindex = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel() ).getNodeRowIndex(zhishu_code);
			    				if(rowindex != null && rowindex >0) {
			    						int modelRow = tableBkZhanBi.convertRowIndexToView(rowindex);
			    						tableBkZhanBi.scrollRectToVisible(new Rectangle(tableBkZhanBi.getCellRect(modelRow, 0, true)));
			    						tableBkZhanBi.setRowSelectionInterval(modelRow, modelRow);
			    				}
			        		}
			        			
			        	}
			        }
			});
		}
	}
}