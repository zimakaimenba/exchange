package com.exchangeinfomanager.bankuaifengxi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
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

import com.exchangeinfomanager.StockCalendar.JStockCalendarDateChooser;
import com.exchangeinfomanager.StockCalendar.StockCalendar;
import com.exchangeinfomanager.bankuaichanyelian.BanKuaiAndChanYeLian2;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiGeGuExternalInfoTable;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiGeGuTable;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiGeGuTableModel;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiInfoTable;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiInfoTableModel;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.DisplayBkGgInfoEditorPane;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.InsertedMeeting;
import com.exchangeinfomanager.bankuaifengxi.CandleStick.BanKuaiFengXiCandlestickPnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartCjlZhanbiPnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartPnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiNodeCombinedCategoryPnl;
import com.exchangeinfomanager.bankuaifengxi.PieChart.BanKuaiFengXiPieChartCjePnl;
import com.exchangeinfomanager.bankuaifengxi.PieChart.BanKuaiFengXiPieChartCjlPnl;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.commonlib.FormatDoubleToShort;
import com.exchangeinfomanager.commonlib.SystemAudioPlayed;
import com.exchangeinfomanager.commonlib.jstockcombobox.DateRangeSelectPnl;
import com.exchangeinfomanager.commonlib.jstockcombobox.JStockComboBox;
import com.exchangeinfomanager.commonlib.jstockcombobox.JStockComboBoxModel;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.StockCalendarAndNewDbOperation;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.gui.subgui.BanKuaiListEditorPane;
import com.exchangeinfomanager.gui.subgui.PaoMaDeng2;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.DaPan;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.nodexdata.BanKuaiNodeXPeriodData;
import com.exchangeinfomanager.nodes.nodexdata.NodeXPeriodDataBasic;
import com.exchangeinfomanager.nodes.nodexdata.StockNodeXPeriodData;
import com.exchangeinfomanager.nodes.nodexdata.TDXNodeGivenPeriodDataItem;
import com.exchangeinfomanager.nodes.operations.AllCurrentTdxBKAndStoksTree;
import com.exchangeinfomanager.nodes.operations.BanKuaiAndStockTree;
import com.exchangeinfomanager.nodes.operations.InvisibleTreeModel;
import com.exchangeinfomanager.nodes.treerelated.BanKuaiTreeRelated;
import com.exchangeinfomanager.nodes.treerelated.StockOfBanKuaiTreeRelated;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.google.common.base.Strings;
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
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.beans.PropertyChangeEvent;
import java.awt.event.MouseAdapter;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
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
import javax.swing.JTable;

import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiGeGuBasicTable;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiGeGuBasicTableModel;
import java.awt.Font;


public class BanKuaiFengXi extends JDialog 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Create the dialog.
	 * @param bkcyl2 
	 */
	public BanKuaiFengXi(StockInfoManager stockmanager1)
	{
		this.stockmanager = stockmanager1;
		this.allbksks = AllCurrentTdxBKAndStoksTree.getInstance();
		this.bkcyl = BanKuaiAndChanYeLian2.getInstance();
		this.sysconfig = SystemConfigration.getInstance();
		this.bkdbopt = new BanKuaiDbOperation ();
		this.newsdbopt = new StockCalendarAndNewDbOperation ();
		this.nodeinfotocsv = new NodeInfoToCsv ();
		this.displayexpc = new ExportCondition () ;
		
		initializeGui ();
		createEvents ();
		setUpChartDataListeners ();

		initializePaoMaDeng ();
		
		adjustDate (LocalDate.now());
	}
	
	private ExportCondition displayexpc;
	private LocalDate lastselecteddate;
	private AllCurrentTdxBKAndStoksTree allbksks;
	private BanKuaiAndChanYeLian2 bkcyl;
	private SystemConfigration sysconfig;
	private StockInfoManager stockmanager;
	private BanKuaiDbOperation bkdbopt;
	private StockCalendarAndNewDbOperation newsdbopt;
	private static Logger logger = Logger.getLogger(BanKuaiFengXi.class);
	private ExportTask2 exporttask;
	private BanKuaiPaiXuTask bkfxtask;
	private ArrayList<ExportCondition> exportcond;
	private String globeperiod;
	private NodeInfoToCsv nodeinfotocsv;
	private Collection<InsertedMeeting> zhishukeylists; //指数关键日期
	
	private Set<BarChartPanelHightLightColumnListener> chartpanelhighlightlisteners;
	private Set<BarChartPanelDataChangedListener> barchartpanelbankuaidatachangelisteners;
	private Set<PieChartPanelDataChangedListener> piechartpanelbankuaidatachangelisteners;
	private Set<BarChartPanelDataChangedListener> barchartpanelstockofbankuaidatachangelisteners;
	private Set<BarChartPanelDataChangedListener> barchartpanelstockdatachangelisteners;
	private Set<BarChartHightLightFxDataValueListener> bkfxhighlightvaluesoftableslisteners;
	
	/*
	 * 
	 */
	private void adjustDate(LocalDate dateneedtobeadjusted)
	{
		//每周日一是新的一周开始，因为还没有导入数据，会显示为没有数据，所以把时间调整到上一周六
				DayOfWeek dayofweek = LocalDate.now().getDayOfWeek();
				if(dayofweek.equals(DayOfWeek.SUNDAY) ) {
					 LocalDate saturday = dateneedtobeadjusted.minus(1,ChronoUnit.DAYS);
//					 ZoneId zone = ZoneId.systemDefault();
//					 Instant instant = saturday.atStartOfDay().atZone(zone).toInstant();
//					 this.dateChooser.setDate(Date.from(instant));
					 this.dateChooser.setLocalDate(saturday);
					 
				} else if(dayofweek.equals(DayOfWeek.MONDAY) && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) <19 ) {
					LocalDate saturday = dateneedtobeadjusted.minus(2,ChronoUnit.DAYS);
//					 ZoneId zone = ZoneId.systemDefault();
//					 Instant instant = saturday.atStartOfDay().atZone(zone).toInstant();
//					 this.dateChooser.setDate(Date.from(instant));
					this.dateChooser.setLocalDate(saturday);
					 
				} else
					this.dateChooser.setDate(new Date ());
				
				btnsixmonthafter.setEnabled(false);
				btnresetdate.setEnabled(false);

	}
	/*
	 * 用户在界面的操作，各个模块的协同操作
	 */
	private void setUpChartDataListeners ()
	{
		this.chartpanelhighlightlisteners = new HashSet<>();
		this.barchartpanelstockofbankuaidatachangelisteners = new HashSet<>();
		this.barchartpanelbankuaidatachangelisteners = new HashSet<>();
		this.piechartpanelbankuaidatachangelisteners = new HashSet<>();
		this.barchartpanelstockdatachangelisteners = new HashSet<>();
		this.bkfxhighlightvaluesoftableslisteners = new HashSet<>();
		
		barchartpanelbankuaidatachangelisteners.add(panelbkwkcjezhanbi);
		barchartpanelbankuaidatachangelisteners.add(pnlbkwkcjlzhanbi);
		//板块pie chart
		piechartpanelbankuaidatachangelisteners.add(pnllastestggzhanbi);
		piechartpanelbankuaidatachangelisteners.add(panelLastWkGeGuZhanBi);
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
		bkfxhighlightvaluesoftableslisteners.add(tableGuGuZhanBiInBk);
		bkfxhighlightvaluesoftableslisteners.add(tableExternalInfo);
		bkfxhighlightvaluesoftableslisteners.add(tablexuandingzhou);
		bkfxhighlightvaluesoftableslisteners.add(tablexuandingminusone);
		bkfxhighlightvaluesoftableslisteners.add(tablexuandingminustwo);
		bkfxhighlightvaluesoftableslisteners.add(tablexuandingplusone);
		bkfxhighlightvaluesoftableslisteners.add(tableExternalInfo);
	}
	/*
	 * 所有板块占比增长率的排名
	 */
	private void gettBanKuaiZhanBiRangedByGrowthRateOfPeriod (String period)
	{
		this.globeperiod = period;
    	LocalDate curselectdate = dateChooser.getLocalDate(); 

    	List<BanKuai> bkwithcje = initializeBanKuaiZhanBiByGrowthRate (curselectdate);
		((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).addBanKuai( bkwithcje );
		((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).refresh(curselectdate,0,globeperiod);
		
		//获取该阶段的指数关键日期
		LocalDate requirestart = curselectdate.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange() + 3,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		zhishukeylists = newsdbopt.getZhiShuKeyDates(requirestart, curselectdate);
		
		
    	//显示大盘成交量
		NumberFormat percentFormat = NumberFormat.getPercentInstance(Locale.CHINA);
    	percentFormat.setMinimumFractionDigits(1);
    	DecimalFormat df2 = new DecimalFormat(".##");
    	TDXNodes childnode = (TDXNodes)this.allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode("000300", BkChanYeLianTreeNode.TDXBK);
    	NodeXPeriodDataBasic bkdata = ((TDXNodes)childnode).getNodeXPeroidData(TDXNodeGivenPeriodDataItem.WEEK);
		try {
				Double cjerec = bkdata.getChengJiaoEr(curselectdate,0);
				Double zhanbi = bkdata.getChenJiaoErZhanBi(curselectdate, 0);
				lblszcje.setText( df2.format(cjerec /100000000.0) +  " (" + percentFormat.format (zhanbi) + ")");
		} catch (java.lang.NullPointerException e) {
				lblszcje.setText( "本周没有数据" );
		}
			
		childnode = (TDXNodes)this.allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode("999999", BkChanYeLianTreeNode.TDXBK); 
		bkdata = ((TDXNodes)childnode).getNodeXPeroidData(TDXNodeGivenPeriodDataItem.WEEK);
		try {
				Double cjerec = bkdata.getChengJiaoEr(curselectdate,0);
				Double zhanbi = bkdata.getChenJiaoErZhanBi(curselectdate, 0);
				lblshcje.setText( df2.format(cjerec /100000000.0) +  " (" + percentFormat.format (zhanbi) + ")");
		} catch (java.lang.NullPointerException e) {
				lblshcje.setText( "本周没有数据" );
		}
		 
		childnode = (TDXNodes)this.allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode("399006", BkChanYeLianTreeNode.TDXBK);	
		bkdata = ((TDXNodes)childnode).getNodeXPeroidData(TDXNodeGivenPeriodDataItem.WEEK);
		try {
				Double cjerec = bkdata.getChengJiaoEr(curselectdate,0);
				Double zhanbi = bkdata.getChenJiaoErZhanBi(curselectdate, 0);
				lblcybcje.setText( df2.format(cjerec /100000000.0) +  " (" + percentFormat.format (zhanbi) + ")");
		} catch (java.lang.NullPointerException e) {
				lblcybcje.setText( "本周没有数据" );
		}
			
		childnode = (TDXNodes)this.allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode("000016", BkChanYeLianTreeNode.TDXBK); 
		bkdata = ((TDXNodes)childnode).getNodeXPeroidData(TDXNodeGivenPeriodDataItem.WEEK);
		try {
				Double cjerec = bkdata.getChengJiaoEr(curselectdate,0);
				Double zhanbi = bkdata.getChenJiaoErZhanBi(curselectdate, 0);
				lbl50cje.setText( df2.format(cjerec /100000000.0) +  " (" + percentFormat.format (zhanbi) + ")");
		} catch (java.lang.NullPointerException e) {
				lbl50cje.setText( "本周没有数据" );
		}
		
		SystemAudioPlayed.playSound();
	}
	/*
	 * 获得curselectdate周数据
	 */
	private List<BanKuai> initializeBanKuaiZhanBiByGrowthRate (LocalDate curselectdate)
	{
		LocalDate requirestart = curselectdate.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange() + 3,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		
    	BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)this.allbksks.getAllBkStocksTree().getModel().getRoot();
		int bankuaicount = allbksks.getAllBkStocksTree().getModel().getChildCount(treeroot);
		
		this.allbksks.getDaPan(requirestart, curselectdate,this.globeperiod);
		
		List<BanKuai> bkwithcje = new ArrayList<BanKuai> ();
		for(int i=0;i< bankuaicount; i++) {
			
			BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode) this.allbksks.getAllBkStocksTree().getModel().getChild(treeroot, i);
			if(childnode.getType() != BkChanYeLianTreeNode.TDXBK) 
				continue;
			
			if( ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL) ||  ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL)  ) //有些指数是没有个股和成交量的，不列入比较范围
				continue;
			
			if( !((BanKuai)childnode).isShowinbkfxgui() )
				continue;
			
			childnode = this.allbksks.getBanKuai( (BanKuai)childnode, requirestart, curselectdate,globeperiod);
			
			NodeXPeriodDataBasic bkxdata = ((TDXNodes)childnode).getNodeXPeroidData(TDXNodeGivenPeriodDataItem.WEEK);
			if(bkxdata.hasRecordInThePeriod(curselectdate, 0) )//板块当周没有数据也不考虑，板块一般不可能没有数据，没有数据说明该板块这周还没有诞生，或者过去有，现在成交量已经不存入数据库
				bkwithcje.add( (BanKuai)childnode );

		}
	
		return bkwithcje;
	}
	/*
	 * 
	 */
	private void unifiedOperationsAfterUserSelectABanKuai (BanKuai selectedbk)
	{
		refreshCurentBanKuaiFengXiResult (selectedbk,globeperiod);
		
		Integer alreadyin = ((JStockComboBoxModel)combxsearchbk.getModel()).hasTheNode(selectedbk.getMyOwnCode());
		if(alreadyin == -1)
			combxsearchbk.updateUserSelectedNode(selectedbk);
		
		displayNodeInfo(selectedbk);
		
		refreshBanKuaiGeGuTableHightLight ();
		
		tableBkZhanBi.repaint();
	}
	/*
	 * 对板块个股表高亮
	 */
	private void refreshBanKuaiGeGuTableHightLight ()
	{
		setExportMainConditionBasedOnUserSelection (displayexpc);
		
		bkfxhighlightvaluesoftableslisteners.forEach(l -> l.hightLightFxValues( displayexpc ) );

		//对于panel来说，hightLightFxValues第一个参数不能用，因为panel 都是 节点对她的上级的占比
		panelGgDpCjeZhanBi.hightLightFxValues(displayexpc);
		panelbkwkcjezhanbi.hightLightFxValues(displayexpc);
		

	}
	/*
	 * 显示板块内的个股的的占比
	 * 	 */
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
//			logger.debug("");
			return;
		}
//		this.allbksks.syncBanKuaiData (selectedbk); //减少一次查询数据的请求，节约时间
		
		//和上证指数一起显示
		BanKuai zhishubk =  (BanKuai) allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode("999999",BkChanYeLianTreeNode.TDXBK);
		paneldayCandle.displayQueKou(false);
		refreshKXianOfTDXNodeWithSuperBanKuai ( selectedbk, zhishubk );
		
//		//板块自身占比
//		for(BarChartPanelDataChangedListener tmplistener : barchartpanelbankuaidatachangelisteners) {
//			tmplistener.updatedDate(selectedbk, CommonUtility.getSettingRangeDate(curselectdate,"basic"),curselectdate,globeperiod);
//		}
		
		//更新板块所属个股
		if(selectedbk.getBanKuaiLeiXing().equals(BanKuai.HASGGWITHSELFCJL)) { //有个股才需要更新，有些板块是没有个股的
			
			selectedbk = allbksks.getAllGeGuOfBanKuai (selectedbk,period); //获取所有曾经是该板块的个股
			ArrayList<StockOfBanKuai> allbkgg = selectedbk.getAllGeGuOfBanKuaiInHistory();
			for(StockOfBanKuai stockofbk : allbkgg)   {
//				if(stockofbk.getMyOwnCode().equals("603259"))
//					logger.debug("test");

//				stockofbk = allbksks.getGeGuOfBanKuai(selectedbk, stockofbk,period ); //板块个股的占比，如果需要看板块成绩额饼图需要这个，目前为了速度暂时不用
		    	 if( stockofbk.isInBanKuaiAtSpecificDate(curselectdate)  ) { //确认当前还在板块内
		    		 Stock stock = allbksks.getStock(stockofbk.getMyOwnCode(), CommonUtility.getSettingRangeDate(curselectdate,"large"),curselectdate, period);
			    		allbksks.syncStockData(stock);
		    	 }
        		
			}
			//有了个股的缺口数据，就可以统计板块的缺口数据了
			allbksks.getBanKuaiQueKouInfo(selectedbk, CommonUtility.getSettingRangeDate(curselectdate,"large"), curselectdate, globeperiod);
			
			//如果板块的分析结果个股数目>0，则要把符合条件的个股标记好
			BkChanYeLianTreeNode nodeincyltree = this.bkcyl.getBkChanYeLianTree().getSpecificNodeByHypyOrCode(selectedbk.getMyOwnCode(), BkChanYeLianTreeNode.TDXBK);
			BanKuaiTreeRelated treerelated = (BanKuaiTreeRelated)nodeincyltree.getNodeTreeRelated ();
			Integer patchfilestocknum = ((BanKuaiTreeRelated)treerelated).getStocksNumInParsedFileForSpecificDate (curselectdate);
			if(patchfilestocknum != null && patchfilestocknum >0) {
				Set<String> stkofbkset = this.bkcyl.getBanKuaiFxSetOfSpecificDate(selectedbk.getMyOwnCode(), curselectdate);
				
				for(String stkofbk : stkofbkset ) {
					StockOfBanKuai stkinbk = selectedbk.getBanKuaiGeGu(stkofbk);
	    			StockOfBanKuaiTreeRelated stofbktree = (StockOfBanKuaiTreeRelated)stkinbk.getNodeTreeRelated();
	        		stofbktree.setStocksNumInParsedFile (curselectdate,true);
				}
				
				stkofbkset= null;
			}
			
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
		
		Integer rowindex = ((BanKuaiInfoTableModel)tableselectedwkbkzb.getModel() ).getBanKuaiRowIndex(selectedbk.getMyOwnCode());
		if(rowindex >0) {
				int modelRow = tableselectedwkbkzb.convertRowIndexToView(rowindex);
				tableselectedwkbkzb.setRowSelectionInterval(modelRow, modelRow);
				tableselectedwkbkzb.scrollRectToVisible(new Rectangle(tableGuGuZhanBiInBk.getCellRect(modelRow, 0, true)));
		}
	}
	/*
	 * 几个表显示用户在选择周个股占比增长率排名等
	 */
	private void refreshSpecificBanKuaiFengXiResult (BanKuai selectedbk, LocalDate selecteddate,String period)
	{
		//显示选定周股票排名情况
		((BanKuaiGeGuTableModel)tablexuandingzhou.getModel()).refresh(selectedbk, selecteddate,period);
		tabbedPanegegu.setTitleAt(2, this.getTabbedPanegeguTabTiles(2) + selecteddate);
		
		//显示选定周-1股票排名情况
		LocalDate selectdate2 = selecteddate.minus(1,ChronoUnit.WEEKS).with(DayOfWeek.FRIDAY);
		((BanKuaiGeGuTableModel)tablexuandingminusone.getModel()).refresh(selectedbk, selectdate2,period);
		tabbedPanegegu.setTitleAt(3,  selectdate2 + "(-1)");

		
		//显示选定周-2股票排名情况
		LocalDate selectdate3 = selecteddate.minus(2,ChronoUnit.WEEKS).with(DayOfWeek.FRIDAY);
		((BanKuaiGeGuTableModel)tablexuandingminustwo.getModel()).refresh(selectedbk, selectdate3,period);
		tabbedPanegegu.setTitleAt(4,  selectdate3 + "(-2)");

		
		//显示选定周+1股票排名情况
		LocalDate selectdate4 = selecteddate.plus(1,ChronoUnit.WEEKS).with(DayOfWeek.FRIDAY);
		((BanKuaiGeGuTableModel)tablexuandingplusone.getModel()).refresh(selectedbk, selectdate4,period);
		tabbedPanegegu.setTitleAt(5,  selectdate4 + "(+1)");
		
		refreshBanKuaiGeGuTableHightLight ();
	}
	
	/*
	 * 所有板块占比增长率的排名
	 */
	private void initializeBanKuaiZhanBiByGrowthRate2 (String period)
	{
		this.globeperiod = period;
    	LocalDate curselectdate = dateChooser.getLocalDate();//dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    	
    	bkfxtask = new BanKuaiPaiXuTask(this.allbksks,  curselectdate,period);
    	bkfxtask.addPropertyChangeListener(new PropertyChangeListener() {
		      @Override
		      public void propertyChange(final PropertyChangeEvent event) {
		        switch (event.getPropertyName()) {
		        case "progress":
		        	
		          break;
		        case "state":
		          switch ((StateValue) event.getNewValue()) {
		          case DONE:
		        	bkfxCancelAction.putValue(Action.NAME, "重置");
		        	LocalDate cursettingdate = dateChooser.getLocalDate();// dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		        	if(cursettingdate.equals(LocalDate.now())) 
		        		btnresetdate.setEnabled(false);
		        	
		        	dateChooser.setEnabled(true);
		        	btnsixmonthbefore.setEnabled(true);
		        	btnsixmonthafter.setEnabled(true);
		        	tableBkZhanBi.setEnabled(true);
//		        	btnexportmodelgegu.setEnabled(true);
		        	tableGuGuZhanBiInBk.setEnabled(true);
		        	ckbxma.setEnabled(true);
		        	ckboxshowcje.setEnabled(true);
		        	ckbxdpmaxwk.setEnabled(true);
		        	chkliutongsz.setEnabled(true);
		        	ckbxcjemaxwk.setEnabled(true);
		        	ckboxparsefile.setEnabled(true);
		        	btnaddexportcond.setEnabled(true);
//		        	btnClear.setEnabled(true);
		        	editorPanebankuai.setEnabled(true);
		        		
		            try {
		               int count = bkfxtask.get();

		             
		              
		              System.gc();
		            } catch (final CancellationException e) {
		            	
		            	JOptionPane.showMessageDialog(null, "板块分析中止！", "中止板块分析", JOptionPane.WARNING_MESSAGE);
		            } catch (final Exception e) {
		            	e.printStackTrace();
		            }

		            bkfxtask = null;
		            break;
		          case STARTED:
		        	  //相关的按键disable
		        	  dateChooser.setEnabled(false);
		        	  btnsixmonthbefore.setEnabled(false);
		        	  btnsixmonthafter.setEnabled(false);
		        	  tableBkZhanBi.setEnabled(false);
//		        	  btnexportmodelgegu.setEnabled(false);
		        	  tableGuGuZhanBiInBk.setEnabled(false);
		        	  ckbxma.setEnabled(false);
		        	  ckboxshowcje.setEnabled(false);
		        	  ckbxdpmaxwk.setEnabled(false);
		        	  chkliutongsz.setEnabled(false);
		        	  ckbxcjemaxwk.setEnabled(false);
		        	  ckboxparsefile.setEnabled(false);
		        	  btnaddexportcond.setEnabled(false);
//		        	  btnClear.setEnabled(false);
		        	  editorPanebankuai.setEnabled(false);
		        	  btnresetdate.setEnabled(true);
		        	  
		          case PENDING:
		        	  bkfxCancelAction.putValue(Action.NAME, "中止板块分析");
		        	  
		            break;
		          }
		          break;
		        }
		      }
		    });
		
    	bkfxtask.execute();
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
				exportcond = new ArrayList<ExportCondition> ();
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
		if(globeperiod == null  || globeperiod.equals(TDXNodeGivenPeriodDataItem.WEEK))
			dateshowinfilename = "week" + curselectdate.with(DayOfWeek.FRIDAY).toString().replaceAll("-","");
		else if(globeperiod.equals(TDXNodeGivenPeriodDataItem.DAY))
			dateshowinfilename = "day" + curselectdate.toString().replaceAll("-","");
		else if(globeperiod.equals(TDXNodeGivenPeriodDataItem.MONTH))
			dateshowinfilename = "month" +  curselectdate.withDayOfMonth(curselectdate.lengthOfMonth()).toString().replaceAll("-","");
		String exportfilename = sysconfig.getTDXModelMatchExportFile () + "TDX模型个股" + dateshowinfilename + ".EBK";
		File filefmxx = new File( exportfilename );
		
		if(!filefmxx.getParentFile().exists()) {  
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
		exporttask = new ExportTask2(this.allbksks,  exportcond, curselectdate,globeperiod,filefmxx);
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
		              bkcyl.parseWeeklyBanKuaiFengXiFileToXmlAndPatchToCylTree (filefmxx,curselectdate.with(DayOfWeek.FRIDAY) );
		              
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
//		((BanKuaiInfoTableModel)tableselectedwkbkzb.getModel()).deleteAllRows();
//		tabbedPanebk.setTitleAt(1, "选定周" );
		btnsixmonthafter.setEnabled(true);
		btnresetdate.setEnabled(true);
		
		ckboxparsefile.setSelected(false);
		tfldparsedfile.setText("");
		
		paneldayCandle.resetDate();
		
		editorPanebankuai.resetSelectedBanKuai ();
		
		
	}
	private void clearTheGuiBeforDisplayNewInfoSection2 ()
	{
		((BanKuaiInfoTableModel)tableselectedwkbkzb.getModel()).deleteAllRows();
		tabbedPanebk.setTitleAt(1, "选定周" );
		
		tabbedPanegegu.setSelectedIndex(0);
		
		panelbkwkcjezhanbi.resetDate();
		
		pnllastestggzhanbi.resetDate();
		
		panelselectwkgeguzhanbi.resetDate();
		panelLastWkGeGuZhanBi.resetDate();
		
		tabbedPanegegu.setTitleAt(0, this.getTabbedPanegeguTabTiles(0) );
		tabbedPanegegu.setTitleAt(1, this.getTabbedPanegeguTabTiles(1) );
		tabbedPanegegu.setTitleAt(2, this.getTabbedPanegeguTabTiles(2) );
		tabbedPanegegu.setTitleAt(3, this.getTabbedPanegeguTabTiles(3) );
		tabbedPanegegu.setTitleAt(4, this.getTabbedPanegeguTabTiles(4) );
		tabbedPanegegu.setTitleAt(5, this.getTabbedPanegeguTabTiles(5) );
		

//		cbxshizhifx.setSelected(false);
		
		((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).deleteAllRows();
		((BanKuaiGeGuBasicTableModel)tableExternalInfo.getModel()).deleteAllRows();
		((BanKuaiGeGuTableModel)tablexuandingzhou.getModel()).deleteAllRows();
		((BanKuaiGeGuTableModel)tablexuandingminusone.getModel()).deleteAllRows();
		((BanKuaiGeGuTableModel)tablexuandingminustwo.getModel()).deleteAllRows();
		((BanKuaiGeGuTableModel)tablexuandingplusone.getModel()).deleteAllRows();
		((BanKuaiGeGuBasicTableModel)tableExternalInfo.getModel()).deleteAllRows();
		
		menuItemchengjiaoer.setText("X 按成交额排名");
    	menuItemliutong.setText("按流通市值排名");
    	
		editorPanenodeinfo.setText("");
	}
	/*
	 * 指定tabbedPanegegu的title，否则会出现几个地方不一致
	 */
	private String getTabbedPanegeguTabTiles (int tabindex)
	{
		String[] jtabbedpanetitle = { "当前周", "其他信息","选定周","选定周-1","选定周-2","选定周+1"};
		return jtabbedpanetitle[tabindex];
	}
	private void clearTheGuiBeforDisplayNewInfoSection3 ()
	{
		tabbedPanegeguzhanbi.setSelectedIndex(0);
		
		panelGgDpCjeZhanBi.resetDate();
		
		panelggdpcjlwkzhanbi.resetDate();
//		tabbedPanegeguzhanbi.setSelectedIndex(0);
//		tabbedPanebk.setSelectedIndex(1);
//		paneldayCandle.resetDate();
		
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
//		LocalDate requirestart = curselectdate.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange() + 3,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
//		LocalDate requireend = curselectdate.with(DayOfWeek.SATURDAY);
//		LocalDate requirestart = curselectdate.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange(),ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		
		selectstock = allbksks.getStock(selectstock,CommonUtility.getSettingRangeDate(curselectdate, "large"),curselectdate,TDXNodeGivenPeriodDataItem.WEEK);
		
		
		for (BarChartPanelDataChangedListener tmplistener : barchartpanelstockdatachangelisteners) {
			tmplistener.updatedDate(selectstock, CommonUtility.getSettingRangeDate(curselectdate, "basic"),curselectdate, globeperiod);
		}
	}
	/*
	 * 板块和个股的K线，可以同时显示，用以对比研究
	 */
	private void refreshKXianOfTDXNodeWithSuperBanKuai (TDXNodes selectnode,TDXNodes superbankuai)
	{
		LocalDate curselectdate = dateChooser.getLocalDate();// dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate requireend = curselectdate.with(DayOfWeek.SATURDAY);
		
		TDXNodes tmpnode = null;
		if(selectnode.getType() == BkChanYeLianTreeNode.TDXGG) {
			selectnode = allbksks.getStock((Stock)selectnode,CommonUtility.getSettingRangeDate(curselectdate, "large"),curselectdate,TDXNodeGivenPeriodDataItem.WEEK);
			//日线K线走势，目前K线走势和成交量在日线和日线以上周期是分开的，所以调用时候要特别小心，以后会合并
			this.allbksks.syncStockData((Stock)selectnode);
			
			tmpnode = selectnode;
		} else if(selectnode.getType() == BkChanYeLianTreeNode.TDXBK) {
			selectnode = allbksks.getBanKuai( (BanKuai)selectnode, CommonUtility.getSettingRangeDate(curselectdate, "large"),curselectdate, TDXNodeGivenPeriodDataItem.WEEK);
			this.allbksks.syncBanKuaiData( (BanKuai)selectnode);
			
			tmpnode = selectnode;
		} else if (selectnode.getType() == BkChanYeLianTreeNode.BKGEGU) {
			Stock stock = allbksks.getStock( ((StockOfBanKuai)selectnode).getStock(),CommonUtility.getSettingRangeDate(curselectdate, "large"),curselectdate,TDXNodeGivenPeriodDataItem.WEEK);
			this.allbksks.syncStockData( ((StockOfBanKuai)selectnode).getStock() );
			
			tmpnode = ((StockOfBanKuai)selectnode).getStock() ;
		}
		
		 if(superbankuai.getType() == BkChanYeLianTreeNode.TDXBK) {
			 superbankuai = allbksks.getBanKuai( (BanKuai)superbankuai, CommonUtility.getSettingRangeDate(curselectdate, "large"),curselectdate, TDXNodeGivenPeriodDataItem.WEEK);
			 this.allbksks.syncBanKuaiData( (BanKuai)superbankuai);
		 }
		
		
		this.allbksks.getDaPanKXian (CommonUtility.getSettingRangeDate(curselectdate, "large"),curselectdate,TDXNodeGivenPeriodDataItem.DAY); 

		paneldayCandle.updatedDate(superbankuai,tmpnode,CommonUtility.getSettingRangeDate(curselectdate, "basic"),requireend,TDXNodeGivenPeriodDataItem.DAY);
		paneldayCandle.updateZhiShuKeyDates (zhishukeylists); //指数关键日期
	}
	/*
	 * 
	 */
	private void displayStockSuoShuBanKuai(Stock selectstock) 
	{
		editorPanebankuai.displayBanKuaiListContents(selectstock);
		
	}
	/*
	 * 显示用户点击bar column后应该提示的信息
	 */
	private void setUserSelectedColumnMessage(TDXNodes node,String seldate) 
	{
//    	LocalDate selecteddate = LocalDate.parse(seldate);
//    	NodeXPeriodDataBasic nodexdata = node.getNodeXPeroidData(this.globeperiod);
    	String htmlstring = node.getNodeXPeroidDataInHtml(LocalDate.parse(seldate),this.globeperiod);
		tfldselectedmsg.displayNodeSelectedInfo (htmlstring);
	}
	
	private void updateBkcylTree ()
	{
	
	}
	/*
	 * 
	 */
	private void createEvents() 
	{
		
	
		
		
		((InvisibleTreeModel)bkcyl.getBkChanYeLianTree().getModel()).addTreeModelListener( new  TreeModelListener () {

			@Override
			public void treeNodesChanged(TreeModelEvent arg0) {
				updateBkcylTree ();
			}

			@Override
			public void treeNodesInserted(TreeModelEvent arg0) {
				updateBkcylTree ();
			}

			@Override
			public void treeNodesRemoved(TreeModelEvent arg0) {
				updateBkcylTree ();
			}

			@Override
			public void treeStructureChanged(TreeModelEvent arg0) {
				updateBkcylTree ();
			}});
		
	
	
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
		
		tfldselectedmsg.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {

                if (evt.getPropertyName().equals(BanKuaiListEditorPane.EXPORTCSV_PROPERTY)) {
                	//如果是从tfldselectedmsg导出CSV，当前不能和从板块和个股导出CSV一起用，时间线的问题还没有解决方案
//                	int extraresult = JOptionPane.showConfirmDialog(null,"要导出到CSV，当前导出CSV的设置都将被清空，是否继续？" , "Warning", JOptionPane.OK_CANCEL_OPTION);
//            		if(extraresult == JOptionPane.CANCEL_OPTION)
//            			return;
            		
//            		nodeinfotocsv.clearCsvDataSet();
            		
                	String html = (String) evt.getNewValue();
                	
                	exportUserSelectedNodeInfoToCsv (html);
                	
                	
                }
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
                    	BanKuai zhishubk =  (BanKuai) allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode(danpanzhishu.toLowerCase(),BkChanYeLianTreeNode.TDXBK);
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
                		Collection<InsertedMeeting> zhishukeylists = newsdbopt.getZhiShuKeyDates(null, null);
                		paneldayCandle.updateZhiShuKeyDates (zhishukeylists);
                    }

                } 
             }
        });
//		lblshcje.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseClicked(MouseEvent arg0) {
////				LocalDate curselectdate = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
////				DaPan dapan = (DaPan)allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode("000000",BkChanYeLianTreeNode.DAPAN);
////				displayNodeLargerPeriodData (dapan,curselectdate);
//			}
//		});
//		lblszcje.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseClicked(MouseEvent arg0) {
////				LocalDate curselectdate = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
////				DaPan dapan = (DaPan)allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode("000000",BkChanYeLianTreeNode.DAPAN);
////				displayNodeLargerPeriodData (dapan,curselectdate);
//			}
//		});
		menuItemsiglebktocsv.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	exportAllBanKuaiToCsv("single");
            	
            }
        });
		menuItemsiglestocktocsv.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	exportAllStockToCsv("single");
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
            	calStockNoneFixPeriodDpMinWk ();
            }
        });

        menuItemliutong.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            	menuItemliutong.setText("X 按流通市值排名");
            	menuItemchengjiaoer.setText("按成交额排名");
            	((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).sortTableByLiuTongShiZhi();
            	((BanKuaiGeGuBasicTableModel)tableExternalInfo.getModel()).sortTableByLiuTongShiZhi();
            	((BanKuaiGeGuTableModel)tablexuandingzhou.getModel()).sortTableByLiuTongShiZhi();
            	((BanKuaiGeGuTableModel)tablexuandingminusone.getModel()).sortTableByLiuTongShiZhi();
            	((BanKuaiGeGuTableModel)tablexuandingminustwo.getModel()).sortTableByLiuTongShiZhi();
            	((BanKuaiGeGuTableModel)tablexuandingplusone.getModel()).sortTableByLiuTongShiZhi();
            	((BanKuaiGeGuTableModel)tableExternalInfo.getModel()).sortTableByLiuTongShiZhi();
            }
        });
        menuItemchengjiaoer.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            	menuItemchengjiaoer.setText("X 按成交额排名");
            	menuItemliutong.setText("按流通市值排名");
            	
            	((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).sortTableByChenJiaoEr();
            	((BanKuaiGeGuBasicTableModel)tableExternalInfo.getModel()).sortTableByChenJiaoEr();
        		((BanKuaiGeGuTableModel)tablexuandingzhou.getModel()).sortTableByChenJiaoEr();
        		((BanKuaiGeGuTableModel)tablexuandingminusone.getModel()).sortTableByChenJiaoEr();
        		((BanKuaiGeGuTableModel)tablexuandingminustwo.getModel()).sortTableByChenJiaoEr();
        		((BanKuaiGeGuTableModel)tablexuandingplusone.getModel()).sortTableByChenJiaoEr();
        		((BanKuaiGeGuTableModel)tableExternalInfo.getModel()).sortTableByChenJiaoEr();
            }
        });

        menuItemstocktocsv.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            	exportAllStockToCsv ("all");
            }

			
        });
        menuItembktocsv.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            	exportAllBanKuaiToCsv ("all");
            }
        });

        tabbedPanebk.addMouseListener(new MouseAdapter() {

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
                    
                	if(tabbedPanebk.getSelectedIndex() == 0)
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
		
		menuItemRmvNodeFmFile.addActionListener(new ActionListener() {
			@Override

			public void actionPerformed(ActionEvent evt) {
//				int bkrow = tableBkZhanBi.getSelectedRow();
//				if(bkrow <0) {
//					JOptionPane.showMessageDialog(null,"请选择一个板块！","Warning",JOptionPane.WARNING_MESSAGE);
//					return;
//				}
				
//				int bkmodelRow = tableBkZhanBi.convertRowIndexToModel(bkrow);
//				BanKuai selectedbk = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(bkmodelRow);
				
//				int ggrow = tableGuGuZhanBiInBk.getSelectedRow();
//				if(ggrow <0) {
//					JOptionPane.showMessageDialog(null,"请选择一个股票！","Warning",JOptionPane.WARNING_MESSAGE);
//					return;
//				}
//				
//				int ggmodelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(ggrow);
//				StockOfBanKuai selectstock = ((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).getStock (ggmodelRow);
//
//				LocalDate curselectdate = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			}
			
		});
		
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
        			
    	    		BanKuai bankuai = (BanKuai)allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode(node.getMyOwnCode(), BkChanYeLianTreeNode.TDXBK);
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
				
				Stock selectstock = (Stock) panelGgDpCjeZhanBi.getCurDisplayedNode ();

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
    				
    				setUserSelectedColumnMessage(selectstock, selectedinfo);
    				
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
				
//				StockOfBanKuai selectstock = null;
//				int stockrow = tableGuGuZhanBiInBk.getSelectedRow();
//				if(stockrow != -1) {
//					int modelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(stockrow);
//					selectstock = ((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).getStock(modelRow);
//				}
				
				Stock selectstock = (Stock) panelGgDpCjeZhanBi.getCurDisplayedNode ();

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
    				
    				setUserSelectedColumnMessage(selectstock, selectedinfo);
    				
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
		
		//添加导出条件
		btnaddexportcond.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				if(SwingUtilities.isLeftMouseButton(arg0) ) {
					
					if(!ckboxshowcje.isSelected() && !ckbxdpmaxwk.isSelected() && !chkliutongsz.isSelected() && !ckbxcjemaxwk.isSelected()){
						JOptionPane.showMessageDialog(null,"未设置导出条件，请先设置导出条件！");
						return;
					} 
					
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
//						if(Strings.isNullOrEmpty(tfldparsedfile.getText() ) )
//							chooseParsedFile (null);
//						else
//							chooseParsedFile (tfldparsedfile.getText() );
						
						hourglassCursor = null;
						Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
						setCursor(hourglassCursor2);
					}
				}
			
//				if(!ckboxparsefile.isSelected()) {
//					Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
//					setCursor(hourglassCursor);
//					
//					ckboxparsefile.setSelected(true);
//					
//					hourglassCursor = null;
//					Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
//					setCursor(hourglassCursor2);
//				}
			}
		});
		
		tfldshowcje.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
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
		pnllastestggcjlzhanbi.getPiePanel().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) 
			{
				String curstock = pnllastestggcjlzhanbi.getCurHightLightStock().toString();
				String curstockcode;
				if(curstock.length() >6 )
					 curstockcode = curstock.substring(0,6);
				else
					curstockcode= curstock;
				
				findInputedNodeInTable (curstockcode);
			}
		});
		 panelselectwkgegucjlzhanbi.getPiePanel().addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent arg0) 
				{
					String curstock = panelselectwkgegucjlzhanbi.getCurHightLightStock().toString();
					String curstockcode;
					if(curstock.length() >6 )
						 curstockcode = curstock.substring(0,6);
					else
						curstockcode= curstock;
					
					findInputedNodeInTable (curstockcode);
				}
			});
		pnllastestggzhanbi.getPiePanel().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) 
			{
				String curstock = pnllastestggzhanbi.getCurHightLightStock().toString();
				String curstockcode;
				if(curstock.length() >6 )
					 curstockcode = curstock.substring(0,6);
				else
					curstockcode= curstock;
				
				findInputedNodeInTable (curstockcode);
			}
		});
		
		panelLastWkGeGuZhanBi.getPiePanel().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) 
			{
				String curstock = panelLastWkGeGuZhanBi.getCurHightLightStock().toString();
				String curstockcode;
				if(curstock.length() >6 )
					 curstockcode = curstock.substring(0,6);
				else
					curstockcode= curstock;
				
				findInputedNodeInTable (curstockcode);
			}
		});
		
		panelselectwkgeguzhanbi.getPiePanel().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent arg0) 
			{
				String curstock = panelselectwkgeguzhanbi.getCurHightLightStock().toString();
				String curstockcode;
				if(curstock.length() >6 )
					 curstockcode = curstock.substring(0,6);
				else
					curstockcode= curstock;
				
				findInputedNodeInTable (curstockcode);
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
							
//							if(cbxstockcode.isEnabled() ) { //避免和
//								displayStockSuoShuBanKuai((Stock)userinputnode);
//							} else
//								cbxstockcode.setEnabled(true);
						} catch (java.lang.NullPointerException e) {
							e.printStackTrace();
//							cbxstockcode.updateUserSelectedNode (selectstock.getStock());
						} catch (java.lang.StringIndexOutOfBoundsException e) {
							logger.debug((String)combxstockcode.getSelectedItem());
						}
//						displayStockSuoShuBanKuai((Stock)userinputnode);
					}
					
					int rowindex = tableGuGuZhanBiInBk.getSelectedRow();
//					int modelRow = tableGuGuZhanBiInBk.convertRowIndexToView(rowindex);
					
					if(!findInputedNodeInTable (nodecode)) { //如果没有找到
//						tableGuGuZhanBiInBk.setRowSelectionInterval(rowindex,rowindex);
//						tableGuGuZhanBiInBk.getSelectionModel().clearSelection() ; //把当前在table中选择取消，以免用户觉得已经找到
						//既然取消了table中的个股，就需要把几个图表的信息清楚，否则用户再去双击这些图表，会出现问题。
						
//						panelGgDpCjeZhanBi.resetDate();
//						paneldayCandle.resetDate();
					} else { //在当前表就有的话，就把相关PANEL清空
//						panelGgDpCjeZhanBi.resetDate();
//						paneldayCandle.resetDate();
					}
						
				}
				
				if(arg0.getStateChange() == ItemEvent.DESELECTED) {
				}
			}
		});
		
		btnsixmonthbefore.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				LocalDate startday = dateChooser.getLocalDate();
				LocalDate requirestart = startday.with(DayOfWeek.MONDAY).minus(1,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
				dateChooser.setDate(Date.from(requirestart.atStartOfDay(ZoneId.systemDefault()).toInstant() ) );
				
				
	    		lastselecteddate = requirestart;
	    		
	    		btnsixmonthafter.setEnabled(true);
	    		btnresetdate.setEnabled(true);
			}
		});
		btnsixmonthafter.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(!btnsixmonthafter.isEnabled())
					return ;
				
				LocalDate startday = dateChooser.getLocalDate();//dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				LocalDate requirestart = startday.with(DayOfWeek.SATURDAY).plus(1,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
				dateChooser.setDate(Date.from(requirestart.atStartOfDay(ZoneId.systemDefault()).toInstant() ) );
    		
	    		lastselecteddate = startday;
	    		
	    		btnresetdate.setEnabled(true);

			}
		});

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
						
			    		bkcyl.getBkChanYeLianTree().setCurrentDisplayedWk (newdate);
			    		DefaultTreeModel treeModel = (DefaultTreeModel) bkcyl.getBkChanYeLianTree().getModel();
			    		treeModel.reload();
			    		
//			    		setHighLightChenJiaoEr();
			    		
			    		gettBanKuaiZhanBiRangedByGrowthRateOfPeriod (TDXNodeGivenPeriodDataItem.WEEK);
			    		
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
		editorPanebankuai.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
            	if (evt.getPropertyName().equals(BanKuaiListEditorPane.URLSELECTED_PROPERTY)) {
            		
//            		String selbk = editorPanebankuai.getSelectedBanKuai();
            		String selbk = evt.getNewValue().toString();
    				String selbkcode;
    				if(selbk != null)
    					 selbkcode = selbk.trim().substring(1, 7);
    				else 
    					return;

    				int rowindex = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuaiRowIndex(selbkcode);
    				if(rowindex != -1) {
    					
    					Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
    					setCursor(hourglassCursor);
    					
    					int modelRow = tableBkZhanBi.convertRowIndexToView(rowindex);
    					tableBkZhanBi.setRowSelectionInterval(modelRow, modelRow);
    					tableBkZhanBi.scrollRectToVisible(new Rectangle(tableBkZhanBi.getCellRect(modelRow, 0, true)));
    					
    					//找到用户选择的板块
    					BanKuai selectedbk = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(rowindex);
    					unifiedOperationsAfterUserSelectABanKuai (selectedbk);
//    					refreshCurentBanKuaiFengXiResult (selectedbk,globeperiod);
//    					displayNodeInfo(selectedbk);
////    					patchParsedFile (selectedbk);
//    					combxsearchbk.updateUserSelectedNode(selectedbk);
//    					refreshBanKuaiGeGuTableHightLight ();
    					
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
					performActionsForGeGuTablesAfterUserSelected (selectstock,tableGuGuZhanBiInBk);
				}
				if (arg0.getClickCount() == 2) {
				}
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
					performActionsForGeGuTablesAfterUserSelected (selectstock,tablexuandingzhou);
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
					performActionsForGeGuTablesAfterUserSelected (selectstock,tablexuandingminustwo);
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
					performActionsForGeGuTablesAfterUserSelected (selectstock,tablexuandingminusone);
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
					performActionsForGeGuTablesAfterUserSelected (selectstock,tablexuandingplusone);
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
					performActionsForGeGuTablesAfterUserSelected (selectstock,tableExternalInfo);
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
//				tableBkZhanBi.repaint();
				
				hourglassCursor = null;
				Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
				setCursor(hourglassCursor2);
//				Toolkit.getDefaultToolkit().beep();
				SystemAudioPlayed.playSound();

			}
		});
	}
	
	protected void updatedComboxAndFengXiResult() 
	{
		BkChanYeLianTreeNode userinputnode = combxsearchbk.getUserInputNode();
		if(userinputnode == null ) { //如果用户输入查出的是板块，
			JOptionPane.showMessageDialog(null,"不是板块代码，重新输入！","Warning", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		String nodecode = userinputnode.getMyOwnCode();
		int rowindex = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuaiRowIndex( nodecode );
		
		if(rowindex != -1) {
			    int curselectrow = tableBkZhanBi.getSelectedRow();
			    int modelRow = tableBkZhanBi.convertRowIndexToView(rowindex);
			    if(modelRow == curselectrow)
			    	return;		    
				
				tableBkZhanBi.setRowSelectionInterval(modelRow, modelRow);
				tableBkZhanBi.scrollRectToVisible(new Rectangle(tableBkZhanBi.getCellRect(modelRow, 0, true)));
				BanKuai selectedbk = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(rowindex);
				
				unifiedOperationsAfterUserSelectABanKuai (selectedbk);
		} else	{
			JOptionPane.showMessageDialog(null,"股票/板块代码有误！或板块不在分析表中，可到板块设置修改！","Warning", JOptionPane.WARNING_MESSAGE);
		}
		
	}
	/*
	 * 用户选择一个板块的column后的相应操作
	 */
	protected void refreshAfterUserSelectBanKuaiColumn (BanKuai bkcur, String selectedinfo) 
	{
//		org.jsoup.nodes.Document doc = Jsoup.parse(selectedinfo);
// 		org.jsoup.select.Elements body = doc.select("body");
// 		org.jsoup.select.Elements dl = body.select("dl");
// 		org.jsoup.select.Elements li = dl.get(0).select("li");
// 		String selecteddate = li.get(0).text();
 		LocalDate datekey = LocalDate.parse(selectedinfo);
		chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(datekey));
		
		editorPanenodeinfo.displayAllNewsOfSpecificWeek (datekey);
		
		setUserSelectedColumnMessage(bkcur,selectedinfo);


		//根据设置，显示选定周分析数据
		if(!menuItemnonshowselectbkinfo.getText().contains("X"))
			return;
			
			//选定周的板块排名情况
			LocalDate selectdate = CommonUtility.formateStringToDate(datekey.toString());
			LocalDate cursetdate = dateChooser.getLocalDate();// dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			if(!CommonUtility.isInSameWeek(selectdate,cursetdate) ) {
				List<BanKuai> bkwithcje = initializeBanKuaiZhanBiByGrowthRate (datekey);
				((BanKuaiInfoTableModel)tableselectedwkbkzb.getModel()).addBanKuai( bkwithcje );
				((BanKuaiInfoTableModel)tableselectedwkbkzb.getModel()).refresh(datekey,0,globeperiod);
				tabbedPanebk.setTitleAt(1, "选定周" + datekey);
				Integer rowindex = ((BanKuaiInfoTableModel)tableselectedwkbkzb.getModel() ).getBanKuaiRowIndex(bkcur.getMyOwnCode());
				if(rowindex >0) {
						int modelRow = tableselectedwkbkzb.convertRowIndexToView(rowindex);
						tableselectedwkbkzb.setRowSelectionInterval(modelRow, modelRow);
						tableselectedwkbkzb.scrollRectToVisible(new Rectangle(tableGuGuZhanBiInBk.getCellRect(modelRow, 0, true)));
//						tableselectedwkbkzb.setRowSelectionAllowed(false);
				}
			} 
			//显示板块个股
			if(bkcur.getBanKuaiLeiXing().equals(BanKuai.HASGGWITHSELFCJL) ) {//应该是有个股的板块点击才显示她的个股， 

				if(!CommonUtility.isInSameWeek(selectdate,cursetdate) ) {
//					panelselectwkgeguzhanbi.updateDate(bkcur, selectdate ,0, globeperiod );
					
					bkcur = allbksks.getAllGeGuOfBanKuai (bkcur,globeperiod); //获取所有曾经是该板块的个股
					
					ArrayList<StockOfBanKuai> allbkgg = bkcur.getAllGeGuOfBanKuaiInHistory();
					for(StockOfBanKuai stockofbk : allbkgg)   {

//							stockofbk = allbksks.getGeGuOfBanKuai(bkcur, stockofbk, globeperiod );
						 if( stockofbk.isInBanKuaiAtSpecificDate(selectdate)  ) { //确认当前还在板块内
								Stock stock = allbksks.getStock(stockofbk.getMyOwnCode(), CommonUtility.getSettingRangeDate(selectdate,"large"),selectdate, globeperiod );
					    		allbksks.syncStockData(stock);
							}
					}
					
					
					//显示选中周股票占比增加率排名等
					refreshSpecificBanKuaiFengXiResult (bkcur,selectdate,globeperiod);
					
					//把用户当前选定的个股找出来
//					int stockrow = tableGuGuZhanBiInBk.getSelectedRow();
//					if(stockrow <0) 
//						return;
//					int stockmodelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(stockrow);
//					StockOfBanKuai selectstock = ((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).getStock (stockmodelRow);
//					performActionsForGeGuTablesAfterUserSelected (selectstock,tableGuGuZhanBiInBk);
					
				} 
				
			}
			
			SystemAudioPlayed.playSound();
	}
	/*
	 * 对不确定周期计算占比，以周占比作为比较对象
	 */
	protected void calStockNoneFixPeriodDpMinWk() 
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

		
		Number[] result = this.allbksks.getTDXNodeNoneFixPeriodDpMinMaxWk(selectstock.getStock(), searchstart, searchend);
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
		
		setUserSelectedColumnMessage (selectstock.getStock(),doc.toString());
	}
	/*
	 * 导出用户选择的node的单独信息到CSV
	 */
	protected void exportUserSelectedNodeInfoToCsv(String html)
	{
		org.jsoup.nodes.Document doc = Jsoup.parse(html);
		org.jsoup.select.Elements divs = doc.select("div");
		for(org.jsoup.nodes.Element div : divs) {
			org.jsoup.select.Elements nodecodes = div.select("nodecode");
			try{
    			String nodecodename = nodecodes.get(0).text();
    			String nodecode = nodecodename.substring(0,6);
    			
    			org.jsoup.select.Elements nodetype = div.select("nodetype");
    			
    			org.jsoup.select.Elements lis = div.select("li");
    			String data = lis.get(0).text();
    			if(Integer.parseInt(nodetype.text() ) == BkChanYeLianTreeNode.TDXGG) 
    					exportAllStockToCsv(nodecode+data);
    			else
    				exportAllBanKuaiToCsv (nodecode+data);
			} catch (java.lang.IndexOutOfBoundsException e) {
				
			}
		}
	}
	/*
	 * 
	 */
	protected void exportAllStockToCsv(String type) 
	{
		btnexportcsv.setEnabled(true);
		
		if(type.toLowerCase().equals("all")) {
			int rowcount = ((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel() ).getRowCount();
			for(int i=0;i<rowcount;i++) {
				StockOfBanKuai stock = ((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel() ).getStock(i);
				LocalDate curselectdate = dateChooser.getLocalDate();//dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				nodeinfotocsv.addNodeToCsvList(stock.getStock(), curselectdate, curselectdate);
			}
		} else if(type.toLowerCase().equals("single") ) {
			int row = tableGuGuZhanBiInBk.getSelectedRow();
			int modelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(row);
			StockOfBanKuai selectstock = ((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).getStock (modelRow);
			NodeXPeriodDataBasic nodexdata = selectstock.getStock().getNodeXPeroidData(TDXNodeGivenPeriodDataItem.WEEK);
			LocalDate nodestart = nodexdata.getAmoRecordsStartDate();
			LocalDate nodeend = nodexdata.getAmoRecordsEndDate();
			nodeinfotocsv.addNodeToCsvList(selectstock.getStock(), nodestart, nodeend);
		} else {
			String stockcode = type.substring(0,6);
			String date = type.substring(6);
			Stock stock = (Stock) this.allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode(stockcode, BkChanYeLianTreeNode.TDXGG);
			nodeinfotocsv.addNodeToCsvList(stock, LocalDate.parse(date), LocalDate.parse(date));
		}
	}
	/*
	 * 
	 */
	protected void exportAllBanKuaiToCsv(String type) 
	{
		btnexportcsv.setEnabled(true);
		
		if(type.toLowerCase().equals("all")) {
			int rowcount = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel() ).getRowCount();
			for(int i=0;i<rowcount;i++) {
				BanKuai bk = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel() ).getBanKuai(i);
				LocalDate curselectdate = dateChooser.getLocalDate();//dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				nodeinfotocsv.addNodeToCsvList( bk, curselectdate, curselectdate);
			}
		} else if(type.toLowerCase().equals("single") ) {
			int row = tableBkZhanBi.getSelectedRow();
			int modelRow = tableBkZhanBi.convertRowIndexToModel(row);
			BanKuai bk = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(modelRow);
			NodeXPeriodDataBasic nodexdata = bk.getNodeXPeroidData(TDXNodeGivenPeriodDataItem.WEEK);
			LocalDate nodestart = nodexdata.getAmoRecordsStartDate();
			LocalDate nodeend = nodexdata.getAmoRecordsEndDate();
			nodeinfotocsv.addNodeToCsvList( bk, nodestart, nodeend);
		} else {
			String stockcode = type.substring(0,6);
			String date = type.substring(6);
			BanKuai bk = (BanKuai) this.allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode(stockcode, BkChanYeLianTreeNode.TDXBK);
			nodeinfotocsv.addNodeToCsvList(bk, LocalDate.parse(date), LocalDate.parse(date));
		}
	}
	/*
	 * 
	 */
	protected void performActionsForGeGuTablesAfterUserSelected(StockOfBanKuai selectstock,BanKuaiGeGuBasicTable selectedGeguTable) 
	{
			Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
			setCursor(hourglassCursor);
			
			try{ //如果用户选择的和上次选择的个股一样，不重复做板块查找
				String stockcodeincbx = ((Stock)combxstockcode.getSelectedItem()).getMyOwnCode();
				if(!selectstock.getMyOwnCode().equals( stockcodeincbx ) ) {
					 combxstockcode.updateUserSelectedNode (selectstock.getStock());
				}
			} catch (java.lang.NullPointerException e) {
				 combxstockcode.updateUserSelectedNode (selectstock.getStock());
			}
			
			
			clearTheGuiBeforDisplayNewInfoSection3 ();
			tabbedPanebk.setSelectedIndex(2);
			
			findInputedNodeInTable (selectstock.getMyOwnCode());
			hightlightSpecificSector (selectstock); //餅圖
//			refreshGeGuFengXiResult (selectstock); //个股对板块的数据
			
			paneldayCandle.displayQueKou(true);
			refreshKXianOfTDXNodeWithSuperBanKuai (selectstock, selectstock.getBanKuai() ); //个股对板块的K线
			
			refreshTDXGeGuZhanBi (selectstock.getStock()); //个股对大盘数据
			//同步板块和个股高亮的日期
			LocalDate curselecteddate = panelbkwkcjezhanbi.getCurSelectedDate();
			chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(curselecteddate));
			
			displayStockCurrentFxResult (selectedGeguTable);
			
			
//			cbxshizhifx.setSelected(false);
			
			hourglassCursor = null;
			Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
			setCursor(hourglassCursor2);
		
	}

//	protected void dispalyStockShiZhiFengXiResult(StockOfBanKuai selectstock, LocalDate curselectdate) 
//	{
//		Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
//		setCursor(hourglassCursor);
//		
//		String displayedresult;
//		if(ggszfx == null) {
//			startupGeGuShiZhiFenXi (false);
//			displayedresult = ggszfx.getStockShiZhiDuiBiFenXiResult (selectstock.getMyOwnCode(),curselectdate);
//		} else 
//			displayedresult = ggszfx.getStockShiZhiDuiBiFenXiResult (selectstock.getMyOwnCode(),curselectdate);
//		
//		
//		setUserSelectedColumnMessage (displayedresult);
//		
//		hourglassCursor = null;
//		Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
//		setCursor(hourglassCursor2);
//	}

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
	protected TimeSeries fengXiMatchedStockNumFromFormerExportedFile(ArrayList<ExportCondition> exportcond2,
			String globeperiod2, int parseInt) 
	{
	
		return null;
	}
	/*
	 * 顯示用户選擇的個股的基本信息
	 */
	private void displayStockCurrentFxResult(BanKuaiGeGuBasicTable curtable) 
	{
		int row = curtable.getSelectedRow();
		if(row <0) {
			JOptionPane.showMessageDialog(null,"请先选择一个个股","Warning",JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		int modelRow = curtable.convertRowIndexToModel(row);
		StockOfBanKuai selectstock = ((BanKuaiGeGuBasicTableModel)curtable.getModel()).getStock (modelRow);
		LocalDate curtableshowwknum = ((BanKuaiGeGuBasicTableModel)curtable.getModel()).getShowCurDate ();
		int columncount = curtable.getColumnCount();
		String result = "";
		for(int i=0; i<columncount;i ++) {
			result = result + curtable.getColumnName(i) + ":" + curtable.getValueAt(row, i) +";";
		}
		StockNodeXPeriodData nodexdata = (StockNodeXPeriodData) selectstock.getStock().getNodeXPeroidData(globeperiod);
		Double liutongshizhi = nodexdata.getSpecificTimeLiuTongShiZhi(curtableshowwknum, 0);
		Double zongshizhi = nodexdata.getSpecificTimeZongShiZhi(curtableshowwknum, 0);
		if(liutongshizhi != null  )
			result = result + "流通市值:" + liutongshizhi/100000000 + "亿";
		if(zongshizhi != null)
			result = result + "总市值:" + zongshizhi/100000000 + "\n";

		result = null;
		
		//顯示個股的信息
		editorPanenodeinfo.setClearContentsBeforeDisplayNewInfo (false);
		editorPanenodeinfo.displayNodeAllInfo(selectstock.getStock());
		editorPanenodeinfo.setClearContentsBeforeDisplayNewInfo (true);
	}
	/*
	 * 用户双击某个node占比chart，则放大显示该node一年内的占比所有数据
	 */
	protected void displayNodeLargerPeriodData(TDXNodes node, LocalDate datekey) 
	{
		//保证显示时间范围为当前日期前后有数据的36个月(3年)
		LocalDate curselectdate = dateChooser.getLocalDate().with(DayOfWeek.FRIDAY);
		LocalDate requireend = curselectdate.with(DayOfWeek.MONDAY).plus(6,ChronoUnit.MONTHS).with(DayOfWeek.FRIDAY);
		LocalDate requirestartd = curselectdate.with(DayOfWeek.MONDAY).minus(18,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		DateTime requiredstartdt= new DateTime(requirestartd.getYear(), requirestartd.getMonthValue(), requirestartd.getDayOfMonth(), 0, 0, 0, 0);
		DateTime requiredenddt = new DateTime(requireend.getYear(), requireend.getMonthValue(), requireend.getDayOfMonth(), 0, 0, 0, 0);
		Interval requiredinterval = new Interval(requiredstartdt, requiredenddt);
		
		NodeXPeriodDataBasic nodexdate = node.getNodeXPeroidData(globeperiod);
		LocalDate nodestart = nodexdate.getAmoRecordsStartDate();
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
		if(node.getType() == BkChanYeLianTreeNode.TDXBK) {
			node = this.allbksks.getBanKuai((BanKuai)node, overlapldstartday,overlapldendday,globeperiod);
			this.allbksks.syncBanKuaiData( (BanKuai)node );
		} else if(node.getType() == BkChanYeLianTreeNode.TDXGG) { 
			node = this.allbksks.getStock((Stock)node, overlapldstartday,overlapldendday,globeperiod);
			 this.allbksks.syncStockData( (Stock)node );
			//如果是个股的话，还要显示其当前所属的板块占比信息，所以要把板块的数据也找出来。
			int row = tableBkZhanBi.getSelectedRow();
			if(row != -1) {
				int modelRow = tableBkZhanBi.convertRowIndexToModel(row);
				bkcur = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(modelRow);
				bkcur = this.allbksks.getBanKuai((BanKuai)bkcur, overlapldstartday,overlapldendday, globeperiod);
				this.allbksks.syncBanKuaiData( (BanKuai)bkcur );
			}
			
		} else if(node.getType() == BkChanYeLianTreeNode.BKGEGU ) {
			BanKuai bk = ((StockOfBanKuai)node).getBanKuai();
			this.allbksks.getBanKuai((BanKuai)bk, overlapldstartday,overlapldendday,globeperiod);
			node = this.allbksks.getGeGuOfBanKuai(bk, node.getMyOwnCode() ,globeperiod);
		} else if(node.getType() == BkChanYeLianTreeNode.DAPAN ) {
//			node = this.allbksks.getDaPan (requirestart.plusWeeks(1),globeperiod); //同步大盘数据,否则在其他地方会出错
		}
			
		
		this.allbksks.getDaPan (overlapldstartday,overlapldendday,globeperiod); //同步大盘数据,否则在其他地方会出错
		this.allbksks.syncDaPanData ();
//		this.allbksks.getDaPanKXian (requirestart.plusWeeks(1),curselectdate,TDXNodeGivenPeriodDataItem.DAY); //同步大盘数据,否则在其他地方会出错
		
		BanKuaiFengXiLargePnl largeinfo = null;
		if(node.getType() == BkChanYeLianTreeNode.TDXBK) {
			DaPan treeroot = (DaPan)this.allbksks.getAllBkStocksTree().getModel().getRoot();
			largeinfo = new BanKuaiFengXiLargePnl (treeroot, node, overlapldstartday
					, overlapldendday, globeperiod);
			
		} else if(node.getType() == BkChanYeLianTreeNode.TDXGG || node.getType() == BkChanYeLianTreeNode.BKGEGU) { 
			largeinfo = new BanKuaiFengXiLargePnl (bkcur, node, overlapldstartday
					, overlapldendday, globeperiod);
		}
		
		if(datekey != null)
			largeinfo.highLightSpecificBarColumn(datekey);
		else
			largeinfo.highLightSpecificBarColumn(curselectdate);;
		
//		long start=System.currentTimeMillis(); //获取开始时间
		JOptionPane.showMessageDialog(null, largeinfo, node.getMyOwnCode()+node.getMyOwnName()+ "大周期分析结果", JOptionPane.OK_CANCEL_OPTION);
//		long end=System.currentTimeMillis(); //获取结束时间
//		logger.debug("程序运行时间： "+(end-start)+"ms");
		
		String userselectnodeinfo = largeinfo.getUserSelectedColumnMessage ();
		if(userselectnodeinfo != null ){
    		
//    		nodeinfotocsv.clearCsvDataSet();
    		exportUserSelectedNodeInfoToCsv (userselectnodeinfo);
		}
		
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
	protected ExportCondition initializeExportConditions () 
	{
		if( exportcond == null)
			exportcond = new ArrayList<ExportCondition> ();
		
		ExportCondition expc = this.setExportMainConditionBasedOnUserSelection (new ExportCondition () );

		ExtraExportConditions extraexportcondition = expc.getExtraExportConditions();
//		extraexportcondition = new ExtraExportConditions ();
		
		int extraresult = JOptionPane.showConfirmDialog(null,extraexportcondition , "附加导出条件:", JOptionPane.OK_CANCEL_OPTION);
		if(extraresult == JOptionPane.OK_OPTION) { //其他导出条件 
			
//		expc.setExportSTStocks ( extraexportcondition.shouldExportSTStocks() );
			
//			if(ckboxshowcje.isSelected() )
//				expc.setChenJiaoEr(exportcjelevel, exportcjelevelmax);
			
			if(expc.shouldOnlyExportCurrentBankuai() ) {
				int row = tableBkZhanBi.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择一个板块！","Warning",JOptionPane.WARNING_MESSAGE);
					return null;
				}
				
				int modelRow = tableBkZhanBi.convertRowIndexToModel(row);
				BanKuai selectedbk = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(modelRow);
				String exportbk = selectedbk.getMyOwnCode();
				expc.setSettingBanKuai(exportbk);
			}
			
//			expc.setHaveDaYangXianUnderCertainChenJiaoEr(extraexportcondition.shouldHaveDaYangXianUnderCertainChenJiaoEr(),
//					extraexportcondition.getCjeLevelUnderCertainChenJiaoErOfDaYangXian(),
//					extraexportcondition.getYangXianLevelUnderCertainChenJiaoErofDaYangXian());
			
//			expc.setLianXuFangLiangUnderCertainChenJiaoEr(extraexportcondition.shouldHaveFangLiangUnderCertainChenJiaoEr(),
//					extraexportcondition.getCjeLevelUnderCertainChenJiaoErOfLianXuFangLiang(),
//					extraexportcondition.getLianXuFangLianLevelUnderCertainChenJiaoErOfFangLiang());

		}
		
		exportcond.add(expc);
		
		decrorateExportButton (expc);
		
		return expc;
	}
	/*
	 * 同时兼任 界面显示的选择 
	 */
	private ExportCondition setExportMainConditionBasedOnUserSelection (ExportCondition expc)
	{
		if(ckbxma.isSelected() ) {
			expc.setSettingMAFormula(tfldweight.getText());
		} else
			expc.setSettingMAFormula(null);
	
		if(ckbxcjemaxwk.isSelected() ) {
//			cjemaxwk = Integer.parseInt(tfldcjemaxwk.getText() );
			expc.setSettingCjemaxwk(tfldcjemaxwk.getText());
		} else
			expc.setSettingCjemaxwk(null);
		
		if(ckbxdpmaxwk.isSelected() ) {
//			dpmaxwk = Integer.parseInt(tflddisplaydpmaxwk.getText() );
			expc.setSettinDpmaxwk(tflddisplaydpmaxwk.getText() );
		} else
			expc.setSettinDpmaxwk(null );
		if(chckbxdpminwk.isSelected() ) {
			expc.setSettinDpminwk (tflddpminwk.getText());
		} else
			expc.setSettinDpminwk (null);
		
		if(ckboxshowcje.isSelected()) {
			String showcjemin; String showcjemax;
			if( !Strings.isNullOrEmpty(tfldshowcje.getText()) ) {
				showcjemin =  tfldshowcje.getText() ;
			} else
				showcjemin = null;
			
			if( !Strings.isNullOrEmpty(tfldshowcjemax.getText()) ) {
				showcjemax =  tfldshowcjemax.getText() ;
			} else
				showcjemax = null;
			
			expc.setChenJiaoEr(showcjemin, showcjemax);
		} else {
//			showcjemin = null;
//			showcjemax = null;
			expc.setChenJiaoEr(null, null);
		}
		
		if(ckbxhuanshoulv.isSelected())
			expc.setSettingHsl( tfldhuanshoulv.getText() ) ;
		else 
			expc.setSettingHsl( null );
		
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
			
			expc.setLiuTongShiZhi(showltszmin, showltszmax);
		} else {
			expc.setLiuTongShiZhi(null, null);
		}

		if(chbxquekou.isSelected()) {
			expc.setDisplayHuiBuDownQueKou (true);
		} else
			expc.setDisplayHuiBuDownQueKou (false);
		
		return expc;
	}
	/*
	 * 用户设置条件后，在buttion 的toolstip显示用户设置的条件
	 */
	private void decrorateExportButton(ExportCondition expc) 
	{
				//在tooltips显示当前有几个已经添加的条件及内容
				String htmlstring = btnaddexportcond.getToolTipText();
				org.jsoup.nodes.Document doc = Jsoup.parse(htmlstring);
				org.jsoup.select.Elements content = doc.select("body");
				content.append(expc.getTooltips());
				htmlstring = doc.toString();
				btnaddexportcond.setToolTipText(htmlstring);
				
				int curconditionnum;
				try {
					String num = btnaddexportcond.getText() ;
				 curconditionnum = Integer.parseInt(btnaddexportcond.getText() ) ;
				 curconditionnum ++ ;
				} catch (java.lang.NumberFormatException e) {
					curconditionnum = 1;
				}
				btnaddexportcond.setText(String.valueOf(curconditionnum));
	}
	/*
	 * 
	 */
	protected void chooseParsedFile(String pasedpathintextfld) 
	{
		//先选择文件
		String filename = null;
		if(pasedpathintextfld == null) {
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
		} else {
			filename = pasedpathintextfld; 
		}
		
		if(!filename.endsWith("EBK") && !filename.endsWith("XML")) { //不是板块文件
			tfldparsedfile.setText("文件错误！请重新选择文件！");
			ckboxparsefile.setSelected(false);
			return;
		}
//			((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).setShowParsedFile(true);
			
			//找到对应的XML
			File fileebk = null;
			boolean xmlfileexist = false;
			File filexminconfigpath = null;
			String filenamedate = null;
			if(filename.endsWith("EBK")) {
				fileebk = new File( filename );
				String exportxmlfilename = sysconfig.getTDXModelMatchExportFile () +  fileebk.getName();
				String xmlfilename = exportxmlfilename.replace(".EBK", ".XML");
				filexminconfigpath = new File(xmlfilename);
				try {
						if (filexminconfigpath.exists()) 
							xmlfileexist = true;
				} catch (Exception e) {
						e.printStackTrace();
						return ;
				}
				
				 filenamedate = fileebk.getName().replaceAll("\\D+","");
				
			} else {
				filexminconfigpath = new File(filename);
				Path inputfilepath = FileSystems.getDefault().getPath(filexminconfigpath.getParent());
				Path systemrequiredpath = FileSystems.getDefault().getPath(sysconfig.getTDXModelMatchExportFile ());
				try {
					boolean cresult = java.nio.file.Files.isSameFile(systemrequiredpath, inputfilepath);
					if(!cresult ) {
						tfldparsedfile.setText("XML文件位置错误！请在指定的目录里重新选择文件！");
						ckboxparsefile.setSelected(false);
						return;
					} else {
						xmlfileexist = true;
						
						filenamedate = filexminconfigpath.getName().replaceAll("\\D+","");
					}
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			}
			
			
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
			LocalDate localDate = null;
			try{
					 localDate = LocalDate.parse(filenamedate, formatter);
					 int exchangeresult = JOptionPane.showConfirmDialog(null,"文件指定日期是" + localDate + "。是否更改到该日期？", "是否更改日期？", JOptionPane.OK_CANCEL_OPTION);
					 if(exchangeresult != JOptionPane.CANCEL_OPTION) {
						 	LocalDate curselectdate = dateChooser.getLocalDate();
							if(!curselectdate.equals(localDate) ) {
								ZoneId zone = ZoneId.systemDefault();
								Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
								this.dateChooser.setDate(Date.from(instant));
							}
					 } else { //用户可以作为本周文件
						 localDate = dateChooser.getLocalDate();
					 }
			} catch (java.time.format.DateTimeParseException e) {
					tfldparsedfile.setText("文件名未标明日期，作为本周文件处理！");
					localDate = dateChooser.getLocalDate();
			}
			
			
			ckboxparsefile.setSelected(true);
			tfldparsedfile.setText(filename);
			
			if(xmlfileexist) {
				this.bkcyl.patchWeeklyBanKuaiFengXiXmlFileToCylTree (filexminconfigpath,localDate);
			} else { //不存在要生成
				this.bkcyl.parseWeeklyBanKuaiFengXiFileToXmlAndPatchToCylTree (fileebk,localDate);
			}

			this.cyltreecopy.setCurrentDisplayedWk (localDate);
			
			InvisibleTreeModel treeModel = (InvisibleTreeModel)this.cyltreecopy.getModel();
			BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)treeModel.getRoot();
			treeModel.reload(treeroot);

			filexminconfigpath = null;
			filexminconfigpath = null;

	}
	
	
	/*
	 * 在个股表中发现输入的个股
	 */
	protected boolean findInputedNodeInTable(String nodecode) 
	{
		Boolean notfound = false; int rowindex;
		if(((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel() ).getRowCount() > 0) {
			 rowindex = ((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel() ).getStockRowIndex(nodecode);
			 
			if(rowindex <0) {
				notfound = true;
			} else {
				int modelRow = tableGuGuZhanBiInBk.convertRowIndexToView(rowindex);
				int curselectrow = tableGuGuZhanBiInBk.getSelectedRow();
				if( curselectrow != modelRow) {
					tableGuGuZhanBiInBk.setRowSelectionInterval(modelRow, modelRow);
					tableGuGuZhanBiInBk.scrollRectToVisible(new Rectangle(tableGuGuZhanBiInBk.getCellRect(modelRow, 0, true)));
					
					//在当前表就有的话，就把相关PANEL清空
					panelGgDpCjeZhanBi.resetDate();
//					paneldayCandle.resetDate();
				}
			}
		}
		
		
		if( ((BanKuaiGeGuTableModel)tablexuandingzhou.getModel() ).getRowCount() >0) {
			rowindex = ((BanKuaiGeGuTableModel)tablexuandingzhou.getModel() ).getStockRowIndex(nodecode);
			if(rowindex <0) {
//				notfound = true;
			} else {
				int modelRow = tablexuandingzhou.convertRowIndexToView(rowindex);
				int curselectrow = tablexuandingzhou.getSelectedRow();
				if( curselectrow != modelRow) {
					tablexuandingzhou.setRowSelectionInterval(modelRow, modelRow);
					tablexuandingzhou.scrollRectToVisible(new Rectangle(tablexuandingzhou.getCellRect(modelRow, 0, true)));
				}
			}
		}
		
		
		if( ((BanKuaiGeGuTableModel)tablexuandingminusone.getModel() ).getRowCount() >0 ) {
			rowindex = ((BanKuaiGeGuTableModel)tablexuandingminusone.getModel() ).getStockRowIndex(nodecode);
			if(rowindex <0) {
//				notfound = true;
			} else {
				int modelRow = tablexuandingminusone.convertRowIndexToView(rowindex);
				int curselectrow = tablexuandingminusone.getSelectedRow();
				if( curselectrow != modelRow)  {
					tablexuandingminusone.setRowSelectionInterval(modelRow, modelRow);
					tablexuandingminusone.scrollRectToVisible(new Rectangle(tablexuandingminusone.getCellRect(modelRow, 0, true)));
				}
			}
		}
		

		if( ((BanKuaiGeGuTableModel)tablexuandingminustwo.getModel() ).getRowCount() > 0) {
			rowindex = ((BanKuaiGeGuTableModel)tablexuandingminustwo.getModel() ).getStockRowIndex(nodecode);
			if(rowindex <0) {
//				notfound = true;
			} else {
				int modelRow = tablexuandingminustwo.convertRowIndexToView(rowindex);
				int curselectrow = tablexuandingminustwo.getSelectedRow();
				if( curselectrow != modelRow) {
					tablexuandingminustwo.setRowSelectionInterval(modelRow, modelRow);
					tablexuandingminustwo.scrollRectToVisible(new Rectangle(tablexuandingminustwo.getCellRect(modelRow, 0, true)));
				}
			}
		}

		if(((BanKuaiGeGuTableModel)tablexuandingplusone.getModel() ).getRowCount() > 0) {
			rowindex = ((BanKuaiGeGuTableModel)tablexuandingplusone.getModel() ).getStockRowIndex(nodecode);
			if(rowindex <0) {
				tablexuandingplusone.getSelectionModel().clearSelection();
			} else {
				int modelRow = tablexuandingplusone.convertRowIndexToView(rowindex);
				int curselectrow = tablexuandingplusone.getSelectedRow();
				if( curselectrow != modelRow)  {
					tablexuandingplusone.setRowSelectionInterval(modelRow, modelRow);
					tablexuandingplusone.scrollRectToVisible(new Rectangle(tablexuandingplusone.getCellRect(modelRow, 0, true)));
				}
			}
		}
		
		
		if(((BanKuaiGeGuBasicTableModel)tableExternalInfo.getModel() ).getRowCount() >0 ) {
			rowindex = ((BanKuaiGeGuBasicTableModel)tableExternalInfo.getModel() ).getStockRowIndex(nodecode);
			if(rowindex <0) {
//				notfound = true;
			} else {
				int modelRow = tableExternalInfo.convertRowIndexToView(rowindex);
				int curselectrow = tableExternalInfo.getSelectedRow();
				if( curselectrow != modelRow) {
					tableExternalInfo.setRowSelectionInterval(modelRow, modelRow);
					tableExternalInfo.scrollRectToVisible(new Rectangle(tableExternalInfo.getCellRect(modelRow, 0, true)));
				}
			}
		}
		
		return !notfound;
	}
	/*
	 * 
	 */
	protected void displayNodeInfo(BkChanYeLianTreeNode selectedbk) 
	{
//		editorPanebkinfo.setText("");
//		editorPanebkinfo.displayNodeAllInfo(selectedbk);
//		editorPanenodeinfo.setText("");
		editorPanenodeinfo.setClearContentsBeforeDisplayNewInfo (false);
		editorPanenodeinfo.displayAllNewsOfSpecificWeek (this.dateChooser.getLocalDate());
		editorPanenodeinfo.displayNodeAllInfo(selectedbk);
		editorPanenodeinfo.setClearContentsBeforeDisplayNewInfo (true);
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
		String paomad = bkdbopt.getMingRiJiHua();
		
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
			 pnllastestggzhanbi.hightlightSpecificSector (stockcode+stockname);
			 panelLastWkGeGuZhanBi.hightlightSpecificSector (stockcode+stockname);
			 panelselectwkgeguzhanbi.hightlightSpecificSector (stockcode+stockname);
			 pnllastestggcjlzhanbi.hightlightSpecificSector (stockcode+stockname);
			 panelselectwkgegucjlzhanbi.hightlightSpecificSector (stockcode+stockname);
		 } catch ( java.lang.NullPointerException e) {
			 pnllastestggzhanbi.hightlightSpecificSector (stockcode);
			 panelLastWkGeGuZhanBi.hightlightSpecificSector (stockcode);
			 panelselectwkgeguzhanbi.hightlightSpecificSector (stockcode);
			 pnllastestggcjlzhanbi.hightlightSpecificSector (stockcode);
			 panelselectwkgegucjlzhanbi.hightlightSpecificSector (stockcode);
		 }
		
	}
	private BanKuaiFengXiNodeCombinedCategoryPnl panelbkwkcjezhanbi;
	private BanKuaiFengXiPieChartCjePnl pnllastestggzhanbi;
	private BanKuaiFengXiPieChartCjePnl panelLastWkGeGuZhanBi;
	private BanKuaiFengXiPieChartCjePnl panelselectwkgeguzhanbi;
	private BanKuaiFengXiNodeCombinedCategoryPnl panelGgDpCjeZhanBi;
	private BanKuaiFengXiNodeCombinedCategoryPnl panelggdpcjlwkzhanbi;
	private BanKuaiFengXiCandlestickPnl paneldayCandle;
	private BanKuaiFengXiPieChartCjlPnl pnllastestggcjlzhanbi;
	private BanKuaiFengXiPieChartCjlPnl panelselectwkgegucjlzhanbi;
	
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
	private JButton cancelButton;
	private JStockCalendarDateChooser dateChooser; //https://toedter.com/jcalendar/
	private JScrollPane sclpleft;
	
	
	private JTextField tfldweight;
	
	private BanKuaiListEditorPane editorPanebankuai;
	private JButton btnresetdate;
	private JButton btnsixmonthbefore;
	private JButton btnsixmonthafter;
	private JStockComboBox combxstockcode;
	
	private JTabbedPane tabbedPanegegu;
	
	private JTextField tfldshowcje;
	private JTextField tfldparsedfile;
	private JTabbedPane tabbedPanegeguzhanbi;
//	private JTextArea tfldselectedmsg;
	private BanKuaiListEditorPane tfldselectedmsg;
	private JStockComboBox combxsearchbk;
	private JCheckBox ckboxshowcje;
	private JCheckBox ckboxparsefile;
	private DisplayBkGgInfoEditorPane editorPanenodeinfo;
	private JLabel lblszcje;
	private JLabel lblshcje;
	private JLabel lblNewLabel;
	

	
	private JCheckBox ckbxdpmaxwk;
	private JTextField tflddisplaydpmaxwk;
	private JScrollPane scrollPaneuserselctmsg;
	private PaoMaDeng2 pnl_paomd;
	private JTabbedPane tabbedPanebk;
	private JButton btnexportcsv;
	private Action exportCancelAction;
	private Action bkfxCancelAction;
	private JCheckBox chkliutongsz;
	private JTextField tfldltszmin;
	private JTextField tfldcjemaxwk;
	private JCheckBox ckbxcjemaxwk;
	
	
	private JTabbedPane tabbedPane_1;
	
	private JButton btnaddexportcond;
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
	private JMenuItem menuItemstocktocsv ;
	
	private JPopupMenu jPopupMenuoftabbedpanebk;
	private JMenuItem menuItembktocsv ; //系统默认按流通市值排名
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
	private BanKuaiFengXiNodeCombinedCategoryPnl pnlbkwkcjlzhanbi;
	private JTabbedPane tabbedPanebkzb;
	private JButton btnmoresetting;
	private JMenuItem menuItemnonshowselectbkinfo;
	
	
	private void initializeGui() {
		
		setTitle("\u677F\u5757\u5206\u6790");
		setBounds(100, 100, 1923, 1033);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.EAST);
		
		JPanel panel = new JPanel();
		JPanel panel_1 = new JPanel();
		
		panelLastWkGeGuZhanBi = new BanKuaiFengXiPieChartCjePnl(-1);
		panelLastWkGeGuZhanBi.setBorder(new TitledBorder(null, "\u677F\u5757\u4E0A\u4E00\u5468\u4E2A\u80A1\u5360\u6BD4", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		scrollPaneuserselctmsg = new JScrollPane();
		
		JPanel panel_2 = new JPanel();
		
		JScrollPane scrollPanestockbk = new JScrollPane();
		
		editorPanebankuai = new BanKuaiListEditorPane();
		scrollPanestockbk.setViewportView(editorPanebankuai);
		
		paneldayCandle = new BanKuaiFengXiCandlestickPnl();
//		paneldayCandle.setBorder(new TitledBorder(null, "\u677F\u5757/\u4E2A\u80A1K\u7EBF\u8D70\u52BF", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		
		tabbedPane_1 = new JTabbedPane(JTabbedPane.TOP);
		
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
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(scrollPanestockbk, GroupLayout.DEFAULT_SIZE, 1107, Short.MAX_VALUE)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(paneldayCandle, GroupLayout.DEFAULT_SIZE, 911, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(scrollPaneuserselctmsg, GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE))
						.addComponent(panel_2, GroupLayout.DEFAULT_SIZE, 1107, Short.MAX_VALUE))
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(14)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 380, GroupLayout.PREFERRED_SIZE)
								.addComponent(panelLastWkGeGuZhanBi, 0, 0, Short.MAX_VALUE)))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(18)
							.addComponent(tabbedPane_1, GroupLayout.PREFERRED_SIZE, 374, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(paneldayCandle, GroupLayout.PREFERRED_SIZE, 260, GroupLayout.PREFERRED_SIZE)
						.addComponent(scrollPaneuserselctmsg, GroupLayout.PREFERRED_SIZE, 260, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(panel, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addGap(173)
									.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 596, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(scrollPanestockbk, GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE)
									.addPreferredGap(ComponentPlacement.RELATED))
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 831, GroupLayout.PREFERRED_SIZE))))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 311, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(panelLastWkGeGuZhanBi, GroupLayout.PREFERRED_SIZE, 289, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(tabbedPane_1, GroupLayout.PREFERRED_SIZE, 309, GroupLayout.PREFERRED_SIZE)))
					.addGap(34))
		);
		
		panelselectwkgeguzhanbi = new BanKuaiFengXiPieChartCjePnl(0);
		tabbedPane_1.addTab("\u9009\u5B9A\u5468\u6210\u4EA4\u989D\u5360\u6BD4", null, panelselectwkgeguzhanbi, null);
		panelselectwkgeguzhanbi.setBorder(new TitledBorder(null, "\u9009\u5B9A\u5468\u5360\u6BD4", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		panelselectwkgegucjlzhanbi = new BanKuaiFengXiPieChartCjlPnl();
		panelselectwkgegucjlzhanbi.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		tabbedPane_1.addTab("\u9009\u5B9A\u5468\u6210\u4EA4\u91CF\u5360\u6BD4", null, panelselectwkgegucjlzhanbi, null);
		
		pnllastestggzhanbi = new BanKuaiFengXiPieChartCjePnl(0);
		tabbedPane.addTab("\u677F\u5757\u5F53\u5468\u6210\u4EA4\u989D\u4E2A\u80A1\u5360\u6BD4", null, pnllastestggzhanbi, null);
		pnllastestggzhanbi.setBorder(new TitledBorder(null, "\u677F\u5757\u5F53\u524D\u5468\u4E2A\u80A1\u5360\u6BD4", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		pnllastestggcjlzhanbi = new BanKuaiFengXiPieChartCjlPnl();
		pnllastestggcjlzhanbi.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		tabbedPane.addTab("\u677F\u5757\u5F53\u5468\u6210\u4EA4\u91CF\u4E2A\u80A1\u5360\u6BD4", null, pnllastestggcjlzhanbi, null);
		
		tfldselectedmsg = new BanKuaiListEditorPane ();
		scrollPaneuserselctmsg.setViewportView(tfldselectedmsg);
		tfldselectedmsg.setEditable(false);
		
		tabbedPanegeguzhanbi = new JTabbedPane(JTabbedPane.TOP);
		
		panelGgDpCjeZhanBi = new BanKuaiFengXiNodeCombinedCategoryPnl("CJE");
		panelGgDpCjeZhanBi.setAllowDrawAnnoation(false);
		tabbedPanegeguzhanbi.addTab("\u4E2A\u80A1\u989D\u5360\u6BD4", null, panelGgDpCjeZhanBi, null);
		
		panelggdpcjlwkzhanbi = new BanKuaiFengXiNodeCombinedCategoryPnl("CJL");
		
		tabbedPanegeguzhanbi.addTab("\u4E2A\u80A1\u91CF\u5360\u6BD4", null, panelggdpcjlwkzhanbi, null);
		
		tabbedPanebkzb = new JTabbedPane(JTabbedPane.TOP);
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addContainerGap()
					.addComponent(tabbedPanebkzb, GroupLayout.PREFERRED_SIZE, 541, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(tabbedPanegeguzhanbi)
					.addContainerGap())
		);
		gl_panel_2.setVerticalGroup(
			gl_panel_2.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
						.addComponent(tabbedPanegeguzhanbi, GroupLayout.DEFAULT_SIZE, 586, Short.MAX_VALUE)
						.addComponent(tabbedPanebkzb, GroupLayout.PREFERRED_SIZE, 586, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
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
		
		JScrollPane scrollPane_2 = new JScrollPane();
		tabbedPanebk.addTab("\u7EFC\u5408\u4FE1\u606F", null, scrollPane_2, null);
		
		editorPanenodeinfo = new DisplayBkGgInfoEditorPane();
		editorPanenodeinfo.setClearContentsBeforeDisplayNewInfo(true);
		scrollPane_2.setViewportView(editorPanenodeinfo);
		editorPanenodeinfo.setEditable(false);
		
		JScrollPane scrollPane = new JScrollPane();
		tabbedPanebk.addTab("\u4EA7\u4E1A\u94FE", null, scrollPane, null);
		
		InvisibleTreeModel treeModel = (InvisibleTreeModel)this.bkcyl.getBkChanYeLianTree().getModel();
		cyltreecopy = new BanKuaiAndStockTree(treeModel,"cyltreecopy");
		
		scrollPane.setViewportView(cyltreecopy);
		
		
		
		JScrollPane scrollPanedangqian = new JScrollPane();
		tabbedPanegegu.addTab("当前周", null, scrollPanedangqian, null);
		tabbedPanegegu.setBackgroundAt(0, Color.ORANGE);
		
		
		tableGuGuZhanBiInBk = new BanKuaiGeGuTable (this.stockmanager);
//		tableGuGuZhanBiInBk.hideZhanBiColumn(1);
//		tableGuGuZhanBiInBk.sortByZhanBiGrowthRate();
		scrollPanedangqian.setViewportView(tableGuGuZhanBiInBk);
		
		JScrollPane scrollPanGeGuExtralInfo = new JScrollPane();
		tabbedPanegegu.addTab("其他信 息", null, scrollPanGeGuExtralInfo, null);
		tableExternalInfo = new BanKuaiGeGuExternalInfoTable (this.stockmanager);
		scrollPanGeGuExtralInfo.setViewportView(tableExternalInfo);
		
		JScrollPane scrollPanexuanding = new JScrollPane();
		tabbedPanegegu.addTab("选定周", null, scrollPanexuanding, null);
		tabbedPanegegu.setBackgroundAt(2, UIManager.getColor("MenuItem.selectionBackground"));
		tablexuandingzhou = new BanKuaiGeGuTable (this.stockmanager);
		scrollPanexuanding.setViewportView(tablexuandingzhou);
		
		
		
		JScrollPane scrollPanexuandingminusone = new JScrollPane();
		tabbedPanegegu.addTab("\u9009\u5B9A\u5468-1", null, scrollPanexuandingminusone, null);
		tabbedPanegegu.setBackgroundAt(3, Color.LIGHT_GRAY);
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
		
		panel_1.setLayout(gl_panel_1);
		
//		dateChooser = new JDateChooser();
		dateChooser = new JStockCalendarDateChooser(new StockCalendar());
		dateChooser.setDateFormatString("yyyy-MM-dd");
		dateChooser.setDate(new Date() );
		
		bkfxCancelAction = new AbstractAction("重置") {

		      private static final long serialVersionUID = 4669650683189592364L;

		      @Override
		      public void actionPerformed(final ActionEvent e) {
		        if (bkfxtask == null) {
		        	if(!btnresetdate.isEnabled())
		        		return;
		        	
		        	LocalDate cursettingdate = dateChooser.getLocalDate(); //.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		        	if(!cursettingdate.equals(LocalDate.now())) {
//		        		LocalDate newdate =  LocalDate.now();
		        		adjustDate (LocalDate.now());
//						dateChooser.setDate(Date.from(newdate.atStartOfDay(ZoneId.systemDefault()).toInstant() ) );
//						btnresetdate.setEnabled(false);
//						btnsixmonthafter.setEnabled(false);
		        	} else {
		        		btnresetdate.setEnabled(false);
		        		btnsixmonthafter.setEnabled(false);
		        	}
		        } else { //当在做板块分析时候，如果用户调整时间，就取消板块分析任务。其实感觉也没多大必要
		        	bkfxtask.cancel(true);
		        }
		      }
		    		
		 };
		
		btnresetdate = new JButton(bkfxCancelAction);
		btnresetdate.setText("\u4ECA\u5929");
//		btnresetdate.setEnabled(false);
		
		btnsixmonthbefore = new JButton("<");
		
		btnsixmonthbefore.setToolTipText("\u5411\u524D1\u4E2A\u6708");
		
		btnsixmonthafter = new JButton(">");
		btnsixmonthafter.setToolTipText("\u5411\u540E1\u4E2A\u6708");
		
		lblNewLabel = new JLabel("\u4E0A\u8BC1");
		
		JLabel lblNewLabel_1 = new JLabel("\u6CAA\u6DF1300");
		
		lblshcje = new JLabel("New label");
		
		lblszcje = new JLabel("New label");
		
		JLabel label = new JLabel("\u521B\u4E1A\u677F");
		
		lblcybcje = new JLabel("New label");
		
		JLabel label_1 = new JLabel("50\u6307");
		
		lbl50cje = new JLabel("New label");
		
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING, false)
						.addGroup(gl_panel.createSequentialGroup()
							.addComponent(dateChooser, GroupLayout.PREFERRED_SIZE, 133, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnsixmonthbefore)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnsixmonthafter)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnresetdate))
						.addGroup(gl_panel.createSequentialGroup()
							.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING, false)
								.addGroup(Alignment.LEADING, gl_panel.createSequentialGroup()
									.addComponent(label)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(lblcybcje, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addGroup(Alignment.LEADING, gl_panel.createSequentialGroup()
									.addComponent(lblNewLabel)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(lblshcje, GroupLayout.PREFERRED_SIZE, 124, GroupLayout.PREFERRED_SIZE)))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel.createSequentialGroup()
									.addComponent(lblNewLabel_1)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(lblszcje, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addGroup(gl_panel.createSequentialGroup()
									.addComponent(label_1)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(lbl50cje, GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)))))
					.addContainerGap(78, Short.MAX_VALUE))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
						.addComponent(dateChooser, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
							.addComponent(btnsixmonthbefore, GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
							.addComponent(btnsixmonthafter, GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
							.addComponent(btnresetdate, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel)
						.addComponent(lblshcje)
						.addComponent(lblNewLabel_1)
						.addComponent(lblszcje))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(label)
						.addComponent(lblcybcje)
						.addComponent(label_1)
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
			{
				cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
			}
			
			ckbxma = new JCheckBox("\u7A81\u51FAMA");
			ckbxma.setFont(new Font("宋体", Font.PLAIN, 12));
			ckbxma.setSelected(true);
			ckbxma.setForeground(new Color(0,153,153) );
			
			
			tfldweight = new JTextField();
			tfldweight.setToolTipText("\u4F8B\uFF1A(>=250 && <60) || >30");
			tfldweight.setForeground(new Color(0,153,153) );
			tfldweight.setText(">=250");
			tfldweight.setColumns(10);
			
			ckboxshowcje = new JCheckBox("\u7A81\u51FA\u6210\u4EA4\u989D\u533A\u95F4(\u4EBF)");
			ckboxshowcje.setSelected(true);
			ckboxshowcje.setBackground(Color.LIGHT_GRAY);
			ckboxshowcje.setForeground(Color.YELLOW);
			
	
			
			tfldshowcje = new JTextField();
			tfldshowcje.setText("2.18");
			tfldshowcje.setForeground(Color.BLUE);
			tfldshowcje.setColumns(10);
//			this.setHighLightChenJiaoEr ();
			
			ckboxparsefile = new JCheckBox("\u5206\u6790\u6587\u4EF6");
			ckboxparsefile.setToolTipText("\u5206\u6790\u6587\u4EF6");
			ckboxparsefile.setFont(new Font("宋体", Font.ITALIC, 12));
			ckboxparsefile.setForeground(Color.ORANGE);
			
			
			tfldparsedfile = new JTextField();
			tfldparsedfile.setForeground(Color.ORANGE);
			tfldparsedfile.setColumns(10);
			tfldparsedfile.setToolTipText(tfldparsedfile.getText());
			
			ckbxdpmaxwk = new JCheckBox("\u7A81\u51FADPMAXWK>=");
			ckbxdpmaxwk.setSelected(true);
			
			ckbxdpmaxwk.setForeground(Color.RED);
			
			tflddisplaydpmaxwk = new JTextField();
			tflddisplaydpmaxwk.setForeground(Color.RED);
			tflddisplaydpmaxwk.setText("4");
			tflddisplaydpmaxwk.setColumns(10);
			
			chkliutongsz = new JCheckBox("\u7A81\u51FA\u6D41\u901A\u5E02\u503C(\u4EBF)");
			chkliutongsz.setBackground(Color.WHITE);
			
			chkliutongsz.setForeground(Color.MAGENTA);
			
			tfldltszmin = new JTextField();
			tfldltszmin.setForeground(Color.MAGENTA);
			tfldltszmin.setText("30");
			tfldltszmin.setColumns(10);
			
			ckbxcjemaxwk = new JCheckBox("\u7A81\u51FA\u6210\u4EA4\u989DMAXWK>=");
			ckbxcjemaxwk.setToolTipText("\u7A81\u51FA\u6210\u4EA4\u989DMAXWK>=");
			ckbxcjemaxwk.setFont(new Font("宋体", Font.PLAIN, 12));
			ckbxcjemaxwk.setForeground(Color.CYAN);
			
			tfldcjemaxwk = new JTextField();
			tfldcjemaxwk.setText("7");
			tfldcjemaxwk.setForeground(Color.CYAN);
			tfldcjemaxwk.setColumns(10);
			
			btnaddexportcond = new JButton("") {
				 public Point getToolTipLocation(MouseEvent e) {
				        return new Point(20, -30);
				      }
			};
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
			tfldhuanshoulv.setText("30");
			tfldhuanshoulv.setColumns(10);
			
//			btnexportmodelgegu = new JButton("\u5BFC\u51FA\u6761\u4EF6\u4E2A\u80A1");
			
			tfldshowcjemax = new JTextField();
			tfldshowcjemax.setColumns(10);
			
			progressBarExport = new JProgressBar();
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
			tfldltszmax.setText("300");
			tfldltszmax.setForeground(Color.MAGENTA);
			tfldltszmax.setColumns(10);
			
			chbxquekou = new JCheckBox("\u7A81\u51FA\u56DE\u8865\u4E0B\u8DF3/\u4E0A\u8DF3");
			chbxquekou.setToolTipText("\u7A81\u51FA\u56DE\u8865\u4E0B\u8DF3/\u4E0A\u8DF3");
			chbxquekou.setFont(new Font("宋体", Font.ITALIC, 12));
			chbxquekou.setSelected(true);
			
			chbxquekou.setForeground(Color.PINK);
			
			chckbxdpminwk = new JCheckBox("\u7A81\u51FADPMINWK>=");
			chckbxdpminwk.setToolTipText("\u7A81\u51FADPMINWK>=");
			chckbxdpminwk.setFont(new Font("宋体", Font.ITALIC, 12));
			chckbxdpminwk.setSelected(true);
			chckbxdpminwk.setForeground(Color.GREEN);
			
			tflddpminwk = new JTextField();
			tflddpminwk.setText("8");
			tflddpminwk.setColumns(10);
			
			btnmoresetting = new JButton("");
			btnmoresetting.setEnabled(false);
			btnmoresetting.setToolTipText("\u66F4\u591A\u8BBE\u7F6E");
			btnmoresetting.setIcon(new ImageIcon(BanKuaiFengXi.class.getResource("/images/icons8-more-details-15.png")));
			
			GroupLayout gl_buttonPane = new GroupLayout(buttonPane);
			gl_buttonPane.setHorizontalGroup(
				gl_buttonPane.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_buttonPane.createSequentialGroup()
						.addContainerGap()
						.addComponent(btnaddexportcond, GroupLayout.PREFERRED_SIZE, 62, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(progressBarExport, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
						.addGap(18)
						.addComponent(btnmoresetting, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(ckbxma)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(tfldweight, GroupLayout.PREFERRED_SIZE, 54, GroupLayout.PREFERRED_SIZE)
						.addGap(18)
						.addComponent(ckboxshowcje)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(tfldshowcje, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(tfldshowcjemax, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
						.addGap(6)
						.addComponent(ckbxdpmaxwk)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(tflddisplaydpmaxwk, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(chckbxdpminwk)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(tflddpminwk, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
						.addGap(43)
						.addComponent(chkliutongsz)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(tfldltszmin, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(tfldltszmax, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(ckbxcjemaxwk)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(tfldcjemaxwk, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(ckbxhuanshoulv)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(tfldhuanshoulv, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(chbxquekou)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(ckboxparsefile)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(tfldparsedfile, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
						.addGap(348)
						.addComponent(okButton, GroupLayout.PREFERRED_SIZE, 56, GroupLayout.PREFERRED_SIZE)
						.addGap(1670)
						.addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
						.addGap(337))
			);
			gl_buttonPane.setVerticalGroup(
				gl_buttonPane.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_buttonPane.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_buttonPane.createParallelGroup(Alignment.LEADING)
							.addGroup(gl_buttonPane.createSequentialGroup()
								.addGap(6)
								.addComponent(btnmoresetting)
								.addContainerGap())
							.addGroup(gl_buttonPane.createParallelGroup(Alignment.TRAILING)
								.addGroup(gl_buttonPane.createParallelGroup(Alignment.BASELINE)
									.addComponent(cancelButton, GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
									.addComponent(okButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addGroup(gl_buttonPane.createSequentialGroup()
									.addGroup(gl_buttonPane.createParallelGroup(Alignment.LEADING)
										.addComponent(ckbxhuanshoulv)
										.addGroup(gl_buttonPane.createParallelGroup(Alignment.BASELINE)
											.addComponent(tfldhuanshoulv, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addComponent(ckboxparsefile, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
											.addComponent(chbxquekou))
										.addGroup(gl_buttonPane.createParallelGroup(Alignment.BASELINE)
											.addComponent(ckboxshowcje, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
											.addComponent(tfldshowcje, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addComponent(ckbxdpmaxwk, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
											.addComponent(tflddisplaydpmaxwk, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addComponent(chkliutongsz)
											.addComponent(tfldltszmin, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addComponent(ckbxcjemaxwk)
											.addComponent(tfldcjemaxwk, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
											.addComponent(tfldparsedfile, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
											.addComponent(tfldshowcjemax, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addComponent(tfldltszmax, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addComponent(progressBarExport, GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
											.addComponent(chckbxdpminwk)
											.addComponent(tflddpminwk, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addComponent(tfldweight, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addComponent(ckbxma, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
										.addComponent(btnaddexportcond, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
									.addContainerGap()))))
			);
			buttonPane.setLayout(gl_buttonPane);
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
		menuItemstocktocsv = new JMenuItem("导出所有个股到CSV");
//      
		jPopupMenuoftabbedpane.add(menuItemzongshizhi);
       menuItemzongshizhi.setEnabled(false);
       jPopupMenuoftabbedpane.add(menuItemliutong);
       jPopupMenuoftabbedpane.add(menuItemchengjiaoer);
       jPopupMenuoftabbedpane.add(menuItemstocktocsv);
       
       jPopupMenuoftabbedpanebk = new JPopupMenu();
       menuItembktocsv = new JMenuItem("导出所有板块到CSV");
       jPopupMenuoftabbedpanebk.add(menuItembktocsv);
       
       menuItemsiglebktocsv = new JMenuItem("导出板块到CSV");
       tableBkZhanBi.getPopupMenu().add(menuItemsiglebktocsv);
       
       menuItemRmvNodeFmFile = new JMenuItem("剔除出模型文件") ;
       menuItemRmvNodeFmFile.setEnabled(false);
       tableGuGuZhanBiInBk.getPopupMenu().add(menuItemRmvNodeFmFile);
		
       menuItemsiglestocktocsv = new JMenuItem("导出个股到CSV");
       tableGuGuZhanBiInBk.getPopupMenu().add(menuItemsiglestocktocsv);
       
       menuItemnonfixperiod = new JMenuItem("不定周期DPM??WK");
       tableGuGuZhanBiInBk.getPopupMenu().add(menuItemnonfixperiod);
       
       menuItemnonshowselectbkinfo = new JMenuItem("同时计算选定周分析数据");
       panelbkwkcjezhanbi.addMenuItem (menuItemnonshowselectbkinfo,null);
       
       
       
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
//		private Double settingcje ;
//		private Integer settindpgmaxwk ;
//		private Integer settinbkgmaxwk ;
		private BanKuaiAndStockTree bkcyltree;
		private ArrayList<ExportCondition> expclist;
		private String period;
		 

		public ExportTask2 (AllCurrentTdxBKAndStoksTree allbksks, ArrayList<ExportCondition> exportcond,LocalDate selectiondate,String period,File outputfile)
		{
//			super ();
			this.bkcyltree = allbksks.getAllBkStocksTree();
			this.expclist = exportcond;
			this.selectiondate = selectiondate;
			this.period = period;
			this.outputfile = outputfile;
		}
		/*
		 * 不管用户怎么设置，所有关于板块的导出就放这里
		 */
//		private String exportBanKuaiByCondition (TDXNodes treeroot,ExportCondition expc,ArrayList<TDXNodes> outputnodeslist)
//		{
//			if ( expc.shouldOnlyExportStocksNotBanKuai() )
//				return null;
//			
//			if(expc.shouldOnlyExportCurrentBankuai() )
//				return null;
//			
//			if(expc.shouldOnlyExportBanKuaiOfZhanBiUp())
//				return null;
//
//			Boolean exportallbk = expc.shouldExportAllBanKuai(); 
//			
//			String settingbk = expc.getSettingBanKuai();
//			Double settingcje = expc.getSettingcje() ;
//			Integer settindpgmaxwk = expc.getSettinDpmaxwk();
//			if(settindpgmaxwk == null)
//				settindpgmaxwk = -1;
//			Integer seetingcjemaxwk = expc.getSettingCjemaxwk();
//			if(seetingcjemaxwk == null)
//				seetingcjemaxwk = -1;
//
//			
//			String ouputfilehead2 = "[板块导出条件:"+ "限定在板块:" + settingbk 
//					+ "内;大盘maxwk>=" + settindpgmaxwk
//					+ ";成交额maxwk>=" + seetingcjemaxwk
//					+"]"
//			;
//			
//			int bankuaicount = bkcyltree.getModel().getChildCount(treeroot);
//			
//			for(int i=0;i< bankuaicount; i++) {
//					if (isCancelled())
//						 return null;
//					
//					BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode)bkcyltree.getModel().getChild(treeroot, i);
//					if(childnode.getType() != BkChanYeLianTreeNode.TDXBK)
//						continue;
//					
//
//					if( ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL) 
//					 ||  ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL) //有些指数是没有个股和成交量的，不列入比较范围 
//					 ||  ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.NOGGWITHSELFCJL) ) //仅导出有个股的板块
//						continue;
//					
//					if(! exportallbk) {
//						if( expc.shouldOnlyExportCurrentBankuai()  && !Strings.isNullOrEmpty(settingbk) ) { //只导出指定板块
//							if(!childnode.getMyOwnCode().equals(settingbk))
//								continue;
//						} else 
//						if(expc.shouldOnlyExportBanKuaiOfZhanBiUp()) { //只导出zhanbiup的板块
//							NodeXPeriodDataBasic nodexdata = ((BanKuai)childnode).getNodeXPeroidData(period);
//							if(nodexdata.hasRecordInThePeriod(selectiondate,0) ) { 
//								Double cjezbchangerate = nodexdata.getChenJiaoErZhanBiGrowthRateOfSuperBanKuai(selectiondate, 0);
//								Double cjlzbchangerate = nodexdata.getChenJiaoLiangZhanBiGrowthRateOfSuperBanKuai(selectiondate, 0);
//								if(cjezbchangerate <0 && cjlzbchangerate <0)
//									continue;
//							} else
//								continue;
//						} else
//						if( ! ((BanKuai)childnode).isExportTowWlyFile() ) { //只导出板块自身设定
//							continue;
//						}
//					}
//					
//					Boolean checkresult = this.checkSpecificBanKuaiByCondition( (BanKuai)childnode, expc);
//					if(checkresult == null)
//						continue;
//					
//					if( checkresult && !outputnodeslist.contains(childnode)){
//						outputnodeslist.add((TDXNodes) childnode);
//					}
//							 
//					
//			}
//		
//			setProgress(30);
//			return ouputfilehead2;
//		}
		/*
		 * 几个地方都可以调用
		 */
//		private Boolean checkSpecificBanKuaiByCondition (BanKuai bk, ExportCondition expc) 
//		{
//			String settingbk = expc.getSettingBanKuai();
//			Double settingcje = expc.getSettingcje() ;
//			Integer settindpgmaxwk = expc.getSettinDpmaxwk();
//			if(settindpgmaxwk == null)
//				settindpgmaxwk = -1;
//			Integer settinbkgmaxwk = expc.getSettinbkgmaxwk();
//			Integer seetingcjemaxwk = expc.getSettingCjemaxwk();
//			if(seetingcjemaxwk == null)
//				seetingcjemaxwk = -1;
//			
//			
//			NodeXPeriodDataBasic nodexdata = bk.getNodeXPeroidData(period);
//			if(nodexdata.hasRecordInThePeriod(selectiondate,0) ) { //板块当周没有数据也不考虑，板块一般不可能没有数据，没有数据说明该板块这周还没有诞生，或者过去有，而当前现在成交量已经不再存入数据库
//					Double recordcje = nodexdata.getChengJiaoEr(selectiondate, 0);
//					Integer recordmaxbkwk = nodexdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(selectiondate,0);
//					Integer recordmaxcjewk =nodexdata.getChenJiaoErMaxWeekOfSuperBanKuai(selectiondate,0);
//					
//					
//					if( recordmaxbkwk >= settindpgmaxwk && recordmaxcjewk >= seetingcjemaxwk ) { //满足条件，导出 ; 板块和个股不一样，只有一个占比
//						return true;
//					 } else
//						 return false;
//			}
//			
//			return null;
//		}
		/*
		 *说明用户没有选择个股板块占比排序，那直接用tree 的个股，会更快 
		 */
//		private Boolean checkSpecificStockByCondition (Stock stock, ExportCondition expc)
//		{
//			String settingbk = expc.getSettingBanKuai();
//			Double settingcjemin = expc.getSettingCjemin() ;
//			if( settingcjemin == null)
//				settingcjemin = -1.0;
//			Double settingcjemax = expc.getSettingCjeMax() ;
//			if(  settingcjemax == null)
//				settingcjemax = 1000000000000.0;
//			Integer settindpgmaxwk = expc.getSettinDpmaxwk();
//			if( settindpgmaxwk == null)
//				settindpgmaxwk = -1;
//			Integer settinbkgmaxwk = expc.getSettinBkmaxwk();
//			if( settinbkgmaxwk == null)
//				settinbkgmaxwk = -1;
//			Integer seetingcjemaxwk = expc.getSettingCjemaxwk();
//			if( seetingcjemaxwk == null)
//				seetingcjemaxwk = -1;
//			Double settinghsl = expc.getSettingHsl();
//			if( settinghsl == null)
//				settinghsl = -1.0;
//			Boolean shouldnothaveST = expc.shouldNotExportSTStocks();
//			
//			Boolean shouldhavedayangxian = expc.shouldHaveDaYangXianUnderCertainChenJiaoEr();
//			Double cjelevelofyangxian = expc.getCjeLevelUnderCertaincChenJiaoErOfBigYangXian();
//			Double yangxianlevelofyangxian = expc.getDaYangXianUnderCertainChenJiaoEr();
//			
//			Boolean shouldhavelianxufl = expc.shouldHaveLianXuFangLangUnderCertainChenJiaoEr();
//			Double cjeleveloflianxufl = expc.getCjeLevelUnderCertaincChenJiaoErOfLianXuFangLiang();
//			Integer lianxuleveloflianxufl = expc.getFangLiangLevelUnderCertainChenJiaoEr();
//			
//			NodeXPeriodDataBasic nodexdata = stock.getNodeXPeroidData(period);
//			Double recordcje = nodexdata.getChengJiaoEr(selectiondate, 0);
//			Integer recordmaxbkwk = nodexdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(selectiondate,0);
//			Integer recordmaxcjewk =nodexdata.getChenJiaoErMaxWeekOfSuperBanKuai(selectiondate,0);
//			Double recordhsl = ( (StockNodeXPeriodData)nodexdata).getSpecificTimeHuanShouLv(selectiondate, 0);
//			
//			if( recordcje >= settingcjemin && recordcje <= settingcjemax &&  recordmaxbkwk >= settindpgmaxwk 
//					&& recordmaxcjewk >= seetingcjemaxwk && recordhsl >= settinghsl) { 
//				
//				Boolean notskiptonextstock = true;
//				
//				if( shouldhavedayangxian && recordcje < cjelevelofyangxian  ) { //如果成交量小于于一定量，就前3周必须有大阳线或者连续2周满足条件
//						Integer searchyangxianrange = -3;//设定往前回溯几周找大阳线，现在定为3周
//						for(int wkindex = 0;wkindex > searchyangxianrange;wkindex--) { //找前3周的数据，看看有没有大阳线
//							Double hzdf = ( (StockNodeXPeriodData)nodexdata).getSpecificTimeHighestZhangDieFu(selectiondate, wkindex);
//							if(hzdf != null && hzdf >= yangxianlevelofyangxian) { //大阳线涨幅，现在定为5%
//								notskiptonextstock = true;
//								break;
//							} else 
//							if(hzdf == null) { //说明改周停牌或者是大盘休市，那要往前多回溯
//								searchyangxianrange = searchyangxianrange -1;
//							} else
//								notskiptonextstock = false;
//							
//						}
//				} 
//				if( shouldhavelianxufl && recordcje < cjeleveloflianxufl && notskiptonextstock == false) { //如果成交量小于于一定量，就前3周必须有大阳线或者连续2周满足条件
//					Integer templianxuleveloflianxufl = 0- lianxuleveloflianxufl;
//					int lianxu = 0;
//					for(int wkindex = 0;wkindex > templianxuleveloflianxufl ; wkindex--) { 
//						Integer recordmaxbkwklast = nodexdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(selectiondate,wkindex);
//						if( recordmaxbkwklast != null && recordmaxbkwklast >= settindpgmaxwk) 
//							lianxu ++;
//						else
//						if(recordmaxbkwklast == null )
//							templianxuleveloflianxufl --;
//					}
//					if(lianxuleveloflianxufl == lianxu ) 
//						notskiptonextstock = true;
//					else
//						notskiptonextstock = false;
//				}
//				
//				//对于均线，为了提高速度，只有满足其他条件的现在才取得K线，计算均线
//				String maf = expc.getSettingMAFormula();
//				if(Strings.isNullOrEmpty(maf))
//					return notskiptonextstock;
//				
//				LocalDate requirestart = CommonUtility.getSettingRangeDate(selectiondate,"large");
//				stock = allbksks.getStockKXian(stock,requirestart,selectiondate,TDXNodeGivenPeriodDataItem.WEEK);
//				
//				
//				return notskiptonextstock;
//			}
//			
//			return false;
//		}
//		private String exportStockByCondition (TDXNodes treeroot,ExportCondition expc,ArrayList<TDXNodes> outputnodeslist)
//		{
//			Boolean exportallbk = expc.shouldExportAllBanKuai(); 
//			if(! exportallbk) //用户没有设置导出全部板块，就不用这个了。 
//				return null;
//			
//			if( expc.shouldOnlyExportBanKuaiOfZhanBiUp() )
//				return null;
//			
//			if( expc.shouldOnlyExportCurrentBankuai() )
//				return null;
//			
//			
//			String settingbk = expc.getSettingBanKuai();
//			Double settingcjemin = expc.getSettingCjemin() ;
//			if( settingcjemin == null)
//				settingcjemin = -1.0;
//			Double settingcjemax = expc.getSettingCjeMax() ;
//			if(  settingcjemax == null)
//				settingcjemax = 1000000000000.0;
//			Integer settindpgmaxwk = expc.getSettinDpmaxwk();
//			if( settindpgmaxwk == null)
//				settindpgmaxwk = -1;
//			Integer settinbkgmaxwk = expc.getSettinBkmaxwk();
//			if( settinbkgmaxwk == null)
//				settinbkgmaxwk = -1;
//			Integer seetingcjemaxwk = expc.getSettingCjemaxwk();
//			if( seetingcjemaxwk == null)
//				seetingcjemaxwk = -1;
//			Double settinghsl = expc.getSettingHsl();
//			if( settinghsl == null)
//				settinghsl = -1.0;
//			Boolean shouldnothaveST = expc.shouldNotExportSTStocks();
//			
//			Boolean shouldhavedayangxian = expc.shouldHaveDaYangXianUnderCertainChenJiaoEr();
//			Double cjelevelofyangxian = expc.getCjeLevelUnderCertaincChenJiaoErOfBigYangXian();
//			Double yangxianlevelofyangxian = expc.getDaYangXianUnderCertainChenJiaoEr();
//			
//			Boolean shouldhavelianxufl = expc.shouldHaveLianXuFangLangUnderCertainChenJiaoEr();
//			Double cjeleveloflianxufl = expc.getCjeLevelUnderCertaincChenJiaoErOfLianXuFangLiang();
//			Integer lianxuleveloflianxufl = expc.getFangLiangLevelUnderCertainChenJiaoEr();
//			
//			String outputfilehead2 = "[个股条件:"+ "限定在板块:" + settingbk + "内;成交额>=" + settingcjemin + "成交额<=" + settingcjemax
//					+ ";大盘maxwk>=" + settindpgmaxwk
//					+ ";成交额maxwk>=" + seetingcjemaxwk
//					+ ";换手率>=" + settinghsl
//					+"]"
//			;
//			
//			if(settindpgmaxwk <0) // 用户没有设置maxdpwk;
//				return null;
//			
//			if(settinbkgmaxwk >0 ) //用户设置了板块内部的比较，要用另外的方法,目前用不到
//				return null;
//			
//			if(!Strings.isNullOrEmpty(settingbk)) //用户设置了板块，要用另外的方法
//				return null;
//			
//			int bankuaicount = bkcyltree.getModel().getChildCount(treeroot);
//			
//			for(int i=0;i< bankuaicount; i++) {
//				if (isCancelled())
//					 return null;
//				
//				BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode)bkcyltree.getModel().getChild(treeroot, i);
//				if(childnode.getType() != BkChanYeLianTreeNode.TDXGG)
//					continue;
//				
//				if( ((Stock)childnode).isVeryVeryNewXinStock()  )
//					continue;
//					
//				LocalDate requirestart = CommonUtility.getSettingRangeDate(selectiondate,"large");
//				childnode = allbksks.getStock((Stock)childnode,requirestart,selectiondate,TDXNodeGivenPeriodDataItem.WEEK);
//				
//				try{
//					if(childnode.getMyOwnName().toUpperCase().contains("ST") && shouldnothaveST )
//						continue;
//				} catch (java.lang.NullPointerException e) {
//					continue;
//					e.printStackTrace();
//					logger.debug(childnode.getMyOwnName());
//				}
//
//				NodeXPeriodDataBasic nodexdata = ((TDXNodes)childnode).getNodeXPeroidData(period);
//				 //板块当周没有数据也不考虑，板块一般不可能没有数据，没有数据说明该板块这周还没有诞生，或者过去有，现在成交量已经不存入数据库
//				// 同时刚上市的新股也不考虑
//				if( nodexdata.hasRecordInThePeriod(selectiondate,0)  ) {
//					Boolean notskiptonextstock = checkSpecificStockByCondition (  (Stock)childnode,expc);
//					if(!outputnodeslist.contains(childnode) && notskiptonextstock)
//						outputnodeslist.add((TDXNodes) childnode);
//					
//						Double recordcje = nodexdata.getChengJiaoEr(selectiondate, 0);
//						Integer recordmaxbkwk = nodexdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(selectiondate,0);
//						Integer recordmaxcjewk =nodexdata.getChenJiaoErMaxWeekOfSuperBanKuai(selectiondate,0);
//						Double recordhsl = ( (StockNodeXPeriodData)nodexdata).getSpecificTimeHuanShouLv(selectiondate, 0);
//						
//						if( recordcje >= settingcjemin && recordcje <= settingcjemax &&  recordmaxbkwk >= settindpgmaxwk 
//								&& recordmaxcjewk >= seetingcjemaxwk && recordhsl >= settinghsl) { 
//							
//							Boolean notskiptonextstock = true;
//							
//							if( shouldhavedayangxian && recordcje < cjelevelofyangxian && settindpgmaxwk >0 ) { //如果成交量小于于一定量，就前3周必须有大阳线或者连续2周满足条件
//									Integer searchyangxianrange = -3;//设定往前回溯几周找大阳线，现在定为3周
//									for(int wkindex = 0;wkindex > searchyangxianrange;wkindex--) { //找前3周的数据，看看有没有大阳线
//										Double hzdf = ( (StockNodeXPeriodData)nodexdata).getSpecificTimeHighestZhangDieFu(selectiondate, wkindex);
//										if(hzdf != null && hzdf >= yangxianlevelofyangxian) { //大阳线涨幅，现在定为5%
//											notskiptonextstock = true;
//											break;
//										} else 
//										if(hzdf == null) { //说明改周停牌或者是大盘休市，那要往前多回溯
//											searchyangxianrange = searchyangxianrange -1;
//										} else
//											notskiptonextstock = false;
//										
//									}
//							} 
//							if( shouldhavelianxufl && recordcje < cjeleveloflianxufl && notskiptonextstock == false && settindpgmaxwk >0) { //如果成交量小于于一定量，就前3周必须有大阳线或者连续2周满足条件
//								Integer templianxuleveloflianxufl = 0- lianxuleveloflianxufl;
//								int lianxu = 0;
//								for(int wkindex = 0;wkindex > templianxuleveloflianxufl ; wkindex--) { 
//									Integer recordmaxbkwklast = nodexdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(selectiondate,wkindex);
//									if( recordmaxbkwklast != null && recordmaxbkwklast >= settindpgmaxwk) 
//										lianxu ++;
//									else
//									if(recordmaxbkwklast == null )
//										templianxuleveloflianxufl --;
//								}
//								if(lianxuleveloflianxufl == lianxu ) 
//									notskiptonextstock = true;
//								else
//									notskiptonextstock = false;
//							}
//							
//							if(!outputnodeslist.contains(childnode) && notskiptonextstock){
//								outputnodeslist.add((TDXNodes) childnode);
//							}
//						}
//				}
//			}
//			
//			return outputfilehead2;
//		}
		/*
		 * 对板块内的个股按条件导出, 经过考虑后，目前不着板块大于几周放量的情况，板块内的放量没有相对于大盘的放量有意义，放弃。该函数目前只用于用户
		 * 设定在某个板块内查找时候使用。用户设定在某个板块内查找时候，不考虑附加导出条件内的关于成交量小于某个值的设定
		 */
//		private String exportStockOfBanKuaiByCondition (TDXNodes treeroot,ExportCondition expc,ArrayList<TDXNodes> outputnodeslist)
//		{
//			Boolean exportallbk = expc.shouldExportAllBanKuai();
//			if(exportallbk) 
//				 return null;
//			
//			String settingbk = expc.getSettingBanKuai();
//			Double settingcjemin = expc.getSettingCjemin() ;
//			if( settingcjemin == null)
//				settingcjemin = -1.0;
//			Double settingcjemax = expc.getSettingCjeMax() ;
//			if(  settingcjemax == null)
//				settingcjemax = 1000000000000.0;
//			Integer settindpgmaxwk = expc.getSettinDpmaxwk();
//			if( settindpgmaxwk == null)
//				settindpgmaxwk = -1;
//			Integer seetingcjemaxwk = expc.getSettingCjemaxwk();
//			if( seetingcjemaxwk == null)
//				seetingcjemaxwk = -1;
//			Double settinghsl = expc.getSettingHsl();
//			if( settinghsl == null)
//				settinghsl = -1.0;
//			Boolean shouldnothaveST = expc.shouldNotExportSTStocks();
//			Boolean shouldhavedayangxian = expc.shouldHaveDaYangXianUnderCertainChenJiaoEr();
//			Double cjelevelofyangxian = expc.getCjeLevelUnderCertaincChenJiaoErOfBigYangXian();
//			Double yangxianlevelofyangxian = expc.getDaYangXianUnderCertainChenJiaoEr();
//			Boolean shouldhavelianxufl = expc.shouldHaveLianXuFangLangUnderCertainChenJiaoEr();
//			Double cjeleveloflianxufl = expc.getCjeLevelUnderCertaincChenJiaoErOfLianXuFangLiang();
//			Integer lianxuleveloflianxufl = expc.getFangLiangLevelUnderCertainChenJiaoEr();
//			
//			int bankuaicount = bkcyltree.getModel().getChildCount(treeroot);
//			Set<String> checkednodesset = new HashSet<String> ();
//			for(int i=0;i< bankuaicount; i++) {
//				if (isCancelled())
//					 return null;
//				
//				BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode)bkcyltree.getModel().getChild(treeroot, i);
//				if(childnode.getType() != BkChanYeLianTreeNode.TDXBK)
//					continue;
//				
//				if( ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL) 
//				 ||  ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL) //有些指数是没有个股和成交量的，不列入比较范围 
//				 ||  ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.NOGGWITHSELFCJL) ) //仅导出有个股的板块
//					continue;
//				
//				
//				
//				if( expc.shouldOnlyExportCurrentBankuai()  && !Strings.isNullOrEmpty(settingbk) ) { //只导出指定板块
//						if(!childnode.getMyOwnCode().equals(settingbk))
//							continue;
//				} else 
//				if(expc.shouldOnlyExportBanKuaiOfZhanBiUp()) { //只导出zhanbiup的板块
//						NodeXPeriodDataBasic nodexdata = ((BanKuai)childnode).getNodeXPeroidData(period);
//						if(nodexdata.hasRecordInThePeriod(selectiondate,0) ) { 
//							Double cjezbchangerate = nodexdata.getChenJiaoErZhanBiGrowthRateOfSuperBanKuai(selectiondate, 0);
//							Double cjlzbchangerate = nodexdata.getChenJiaoLiangZhanBiGrowthRateOfSuperBanKuai(selectiondate, 0);
//							if(cjezbchangerate <0 && cjlzbchangerate <0)
//								continue;
//						} else
//							continue;
//				} else
//				if( ! ((BanKuai)childnode).isExportTowWlyFile() ) { //只导出板块自身设定
//						continue;
//				}
//				
//				NodeXPeriodDataBasic bknodexdata = ((TDXNodes)childnode).getNodeXPeroidData(period);
//				if(!bknodexdata.hasRecordInThePeriod(selectiondate,0)  )  //bankuai当周没有数据也不考虑
//					continue;
//				
//				childnode = allbksks.getAllGeGuOfBanKuai ( (BanKuai)childnode,period);
//				Set<StockOfBanKuai> nowbkallgg = ((BanKuai)childnode).getSpecificPeriodBanKuaiGeGu(selectiondate,0,period);
//				for (StockOfBanKuai stockofbankuai : nowbkallgg) {
//						 if (isCancelled()) 
//							 return null;
//						 
//						 try {
//							 if(stockofbankuai.getMyOwnName().toUpperCase().contains("ST") && shouldnothaveST )
//									continue;
//						 } catch (java.lang.NullPointerException e) {
//
//						 }
//						 
//						 stockofbankuai =  allbksks.getGeGuOfBanKuai( ((BanKuai)childnode), stockofbankuai, period );
//						 NodeXPeriodDataBasic stockofbkxdata = stockofbankuai.getNodeXPeroidData( period);
//						 if(!stockofbkxdata.hasRecordInThePeriod(selectiondate, 0)  )
//							 continue;
//						 
//						 Double recordcje = stockofbkxdata.getChengJiaoEr(selectiondate, 0);
//						 int recordmaxbkwk;
//						 try{
//							 recordmaxbkwk = stockofbkxdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(selectiondate,0).intValue() ;
//						 } catch (java.lang.NullPointerException e) {
//							 recordmaxbkwk = 1000000;
//						 }
//						 
//						 
//						 Stock ggstock = stockofbankuai.getStock();
//						 if( ((Stock)ggstock).isVeryVeryNewXinStock() ) // 刚上市的新股也不考虑
//							 continue;
//						 
//						 if(outputnodeslist.contains( (TDXNodes)ggstock ) )
//							 continue;
//						 
//						 if( checkednodesset.contains(ggstock.getMyOwnCode() ) ) //已经检查过的stock就不用了，加快速度
//								 continue;
//						 
//						 ggstock = allbksks.getStock(ggstock, bknodexdata.getAmoRecordsStartDate(), bknodexdata.getAmoRecordsEndDate(), period);
//						 NodeXPeriodDataBasic stockxdata = ggstock.getNodeXPeroidData(globeperiod);
//						 if( stockxdata.hasRecordInThePeriod(selectiondate,0)    ) {
//								Boolean checkstock = checkSpecificStockByCondition (  ggstock,expc);
//								if(!outputnodeslist.contains(ggstock) && checkstock)
//									outputnodeslist.add( (TDXNodes)ggstock) ;
//								else if(!checkstock)
//									checkednodesset.add(ggstock.getMyOwnCode() );
//						 }	
//						 
//						 
//						 NodeXPeriodDataBasic stockxdata = ggstock.getNodeXPeroidData(globeperiod);
//						 Integer recordmaxdpwk = stockxdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(selectiondate,0);
//						 Integer recordmaxcjewk = stockxdata.getChenJiaoErMaxWeekOfSuperBanKuai(selectiondate,0);
//						 Double recordhsl = ((StockNodeXPeriodData)stockxdata).getSpecificTimeHuanShouLv(selectiondate, 0);
//						 
//						 if(recordcje >= settingcjemin && recordcje <= settingcjemax &&  recordmaxbkwk >= settinbkgmaxwk
//									 && recordmaxcjewk >= seetingcjemaxwk && recordmaxdpwk >=  settindpgmaxwk && recordhsl>=settinghsl)  { //满足条件，导出 ; 
//							 
//							 Boolean skiptonextstock = true;
//							 if( shouldhavedayangxian && recordcje < cjelevelofyangxian ) { //如果成交量小于于一定量，就前3周必须有大阳线或者连续2周满足条件,如果用户指定了板块就不考虑这2个限定了，没必要
//										Integer searchyangxianrange = -3;//设定往前回溯几周找大阳线，现在定为3周
//										for(int wkindex = 0;wkindex > searchyangxianrange;wkindex--) { //找前3周的数据，看看有没有大阳线
//											Double hzdf = ((StockNodeXPeriodData)stockxdata).getSpecificTimeHighestZhangDieFu(selectiondate, wkindex);
//											if(hzdf != null && hzdf >= yangxianlevelofyangxian) { //大阳线涨幅，现在定为5%
//												skiptonextstock = true;
//												break;
//											} else 
//											if(hzdf == null) { //说明改周停牌或者是大盘休市，那要往前多回溯
//												searchyangxianrange = searchyangxianrange -1;
//											} else
//												skiptonextstock = false;
//										}
//							} 
//							if( shouldhavelianxufl && recordcje < cjeleveloflianxufl && skiptonextstock == false ) { //如果成交量小于于一定量，就前3周必须有大阳线或者连续2周满足条件
//									Integer templianxuleveloflianxufl = 0- lianxuleveloflianxufl;
//									int lianxu = 0;
//									for(int wkindex = 0;wkindex > templianxuleveloflianxufl ; wkindex--) { 
//										Integer recordmaxbkwklast = ((StockNodeXPeriodData)stockxdata).getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(selectiondate,wkindex);
//										if( recordmaxbkwklast != null && recordmaxbkwklast >= settindpgmaxwk) 
//											lianxu ++;
//										else
//										if(recordmaxbkwklast == null )
//											templianxuleveloflianxufl --;
//									}
//									if(lianxuleveloflianxufl == lianxu ) 
//										skiptonextstock = true;
//									else
//										skiptonextstock = false;
//							}
//								
//							if(!outputnodeslist.contains(ggstock) && skiptonextstock)
//									outputnodeslist.add(ggstock);
//						 } 
//					}
//				
//				
//			}
//			checkednodesset = null;
//			return null;
//		}
		/*
	     * Main task. Executed in background thread.
	     */
	    @Override
	    public Integer doInBackground() 
	    {
	    	Charset charset = Charset.forName("GBK") ;
				
//	    	TDXNodes treeroot = (TDXNodes)bkcyltree.getModel().getRoot();
//			int bankuaicount = bkcyltree.getModel().getChildCount(treeroot);
//			HashSet<String> outputresultset = new HashSet<String> ();
			ArrayList<TDXNodes> outputnodeslist = new ArrayList<TDXNodes> ();
			
			String ouputfilehead2 = "";
//			int progressint = 30;
			for(ExportCondition expc : expclist) {
				if (isCancelled()) 
					 return null;
				
				setProgress(30);
				try{
				Set<TDXNodes> outputnodes = expc.checkTDXNodeMatchedCurSettingConditons(bkcyltree, selectiondate, globeperiod);
				outputnodeslist.addAll(outputnodes);
				
				ouputfilehead2 = ouputfilehead2 + expc.getConditionsDescriptions ();
				
				} catch(Exception e) {
					e.printStackTrace();
				}
				
				
//				ouputfilehead2 = ouputfilehead2 + expc.getTooltips() + "\n";
//				//导出板块
//				String outputhead = exportBanKuaiByCondition (treeroot, expc, outputnodeslist);
////				if(Strings.isNullOrEmpty(outputhead))
////					ouputfilehead2 = ouputfilehead2 + outputhead; 
//				setProgress(30);
//				outputhead = exportStockByCondition (treeroot, expc, outputnodeslist);
////				if(Strings.isNullOrEmpty(outputhead))
////					ouputfilehead2 = ouputfilehead2 + outputhead;
//				exportStockOfBanKuaiByCondition (treeroot, expc, outputnodeslist);
				
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
  			//把板块分析文件的结果转为XML，另生成XML保存
//  			bkcyl.parseWeeklyBanKuaiFengXiFileToXmlAndPatchToCylTree (outputfile.getName(),selectiondate);
  			

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
	
	/*
	 * 板块排序耗时太长，用另外一个线程专门处理，避免主GUI无法使用
	 * http://www.javacreed.com/swing-worker-example/
	 * https://github.com/javacreed/swing-worker-example/blob/master/src/main/java/com/javacreed/examples/swing/worker/part3/Application.java
	 */
	class BanKuaiPaiXuTask extends SwingWorker<Integer, String>  
	{
		private LocalDate curselectdate;
		private AllCurrentTdxBKAndStoksTree allbksks;
		private String period;
		 

		public BanKuaiPaiXuTask (AllCurrentTdxBKAndStoksTree allbksks,LocalDate selectiondate,String period)
		{
//			super ();
			this.allbksks = allbksks;
			this.curselectdate = selectiondate;
			this.period = period;
			
		}

		/*
	     * Main task. Executed in background thread.
	     */
	    @Override
	    public Integer doInBackground() 
	    {
	    	setProgress(0);
	    	DaPan treeroot = (DaPan)this.allbksks.getAllBkStocksTree().getModel().getRoot();
			int bankuaicount = allbksks.getAllBkStocksTree().getModel().getChildCount(treeroot);
	        
			for(int i=0;i< bankuaicount; i++) {
				setProgress(i*90/bankuaicount );
				
				if (isCancelled()) {
					i=0;
					 return null;
				}
				
				BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode) this.allbksks.getAllBkStocksTree().getModel().getChild(treeroot, i);
				if(childnode.getType() != BkChanYeLianTreeNode.TDXBK) 
					continue;
				
				if( ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL) ||  ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL)  ) //有些指数是没有个股和成交量的，不列入比较范围
					continue;
				
				if( !((BanKuai)childnode).isShowinbkfxgui() )
					continue;
				
				String bkcode = childnode.getMyOwnCode();
				LocalDate requirestart = curselectdate.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange() + 3,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
				childnode = (BanKuai)this.allbksks.getBanKuai(((BanKuai)childnode), requirestart,curselectdate,period); 
				BanKuaiNodeXPeriodData bkxdata = (BanKuaiNodeXPeriodData) ((BanKuai)childnode).getNodeXPeroidData(period);
	    		if(bkxdata.hasRecordInThePeriod(curselectdate, 0) != null ) {//板块当周没有数据也不考虑，板块一般不可能没有数据，没有数据说明该板块这周还没有诞生，或者过去有，现在成交量已经不存入数据库
	    			((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).addBanKuai(((BanKuai)childnode));
	    		}
	    		
	    		//显示大盘成交量
	    		
	    		if(bkcode.equals("999999") ) {
	    			BanKuaiNodeXPeriodData bkshdata = (BanKuaiNodeXPeriodData) ((BanKuai)childnode).getNodeXPeroidData(TDXNodeGivenPeriodDataItem.WEEK);
	    			try {
		    			Double cjerec = bkshdata.getChengJiaoEr(curselectdate,0);
	    				lblshcje.setText( cjerec.toString() );
	    			} catch (java.lang.NullPointerException e) {
	    				lblshcje.setText( "本周没有成交额" );
	    			}
	    			
	    		} else if(bkcode.equals("399001") ) {
	    			BanKuaiNodeXPeriodData bkszdata = (BanKuaiNodeXPeriodData)((BanKuai)childnode).getNodeXPeroidData(TDXNodeGivenPeriodDataItem.WEEK);
	    			try {
	    				Double cjerec = bkszdata.getChengJiaoEr(curselectdate,0);
	    				lblszcje.setText( cjerec.toString() );
	    			} catch (java.lang.NullPointerException e) {
	    				lblszcje.setText( "本周没有成交额" );
	    			}
	    		}
			}

			((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).refresh(curselectdate,0,period);
			setProgress(100);
			
			return 100;
	    }
	    
	    /*
	     * Executed in event dispatching thread
	     */
	    @Override
	    public void done() 	    
	    {
//	        Toolkit.getDefaultToolkit().beep();
	    	SystemAudioPlayed.playSound();
	    }
	}
}

/*
 * 
 */
class NodeChenJiaoErComparator implements Comparator<TDXNodes> {
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

