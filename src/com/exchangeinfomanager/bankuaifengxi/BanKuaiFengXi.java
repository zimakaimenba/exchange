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
import java.util.Map;
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
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Cache;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.DBLabelService;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.DBMeetingService;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.EventService;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.InsertedMeeting;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.LabelService;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Meeting;
import com.exchangeinfomanager.bankuaifengxi.CandleStick.BanKuaiFengXiCandlestickPnl;

import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartPnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiNodeCombinedCategoryPnl;
import com.exchangeinfomanager.bankuaifengxi.PieChart.BanKuaiFengXiPieChartCjePnl;
import com.exchangeinfomanager.bankuaifengxi.PieChart.BanKuaiFengXiPieChartCjlPnl;
import com.exchangeinfomanager.bankuaifengxi.PieChart.BanKuaiFengXiPieChartPnl;

import com.exchangeinfomanager.commonlib.CommonUtility;

import com.exchangeinfomanager.commonlib.ReminderPopToolTip;
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
import com.exchangeinfomanager.nodes.operations.AllCurrentTdxBKAndStoksTree;
import com.exchangeinfomanager.nodes.operations.BanKuaiAndStockTree;
import com.exchangeinfomanager.nodes.operations.InvisibleTreeModel;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.StockNodesXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.nodes.treerelated.BanKuaiTreeRelated;
import com.exchangeinfomanager.nodes.treerelated.StockOfBanKuaiTreeRelated;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
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


import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiGeGuBasicTable;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiGeGuBasicTableModel;
import java.awt.Font;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


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
//		this.fxrminder = new BanKuaiFengXiReminderXmlHandler ();
		
		
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
	private Collection<InsertedMeeting> zhishukeylists; //ָ���ؼ�����
	
	private Set<BarChartPanelHightLightColumnListener> chartpanelhighlightlisteners;
	private Set<BarChartPanelDataChangedListener> barchartpanelbankuaidatachangelisteners;
	private Set<PieChartPanelDataChangedListener> piechartpanelbankuaidatachangelisteners;
	private Set<BarChartPanelDataChangedListener> barchartpanelstockofbankuaidatachangelisteners;
	private Set<BarChartPanelDataChangedListener> barchartpanelstockdatachangelisteners;
	private Set<BarChartHightLightFxDataValueListener> bkfxhighlightvaluesoftableslisteners;
	
	/*
	 * ���һ�����
	 */
	private void unifiedOperationsAfterUserSelectABanKuai (BanKuai selectedbk)
	{
		refreshCurentBanKuaiFengXiResult (selectedbk,globeperiod); //����ڸ��ɵķ������
		
		Integer alreadyin = ((JStockComboBoxModel)combxsearchbk.getModel()).hasTheNode(selectedbk.getMyOwnCode());
		if(alreadyin == -1)
			combxsearchbk.updateUserSelectedNode(selectedbk);
		
		displayNodeInfo(selectedbk);
		
		refreshBanKuaiGeGuTableHightLight ();
		
		tableBkZhanBi.repaint(); //
		
		String reminder = "��ʾ: \n"
						+ "1. ע����ռ���Ƿ������\n"
						+ "2. ���Ѱ���Ƿ�ͬʱ������\n"
						+ "3. �к�����֧�ָð�顣\n"
						;
		showReminderMessage (reminder);
	}
	/**
	 * ���һ�� ����
	 * @param selectstock
	 * @param selectedGeguTable
	 */
	protected void performActionsForGeGuTablesAfterUserSelected(StockOfBanKuai selectstock,BanKuaiGeGuBasicTable selectedGeguTable) 
	{
			Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
			setCursor(hourglassCursor);
			
			try{ //����û�ѡ��ĺ��ϴ�ѡ��ĸ���һ�������ظ���������
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
			hightlightSpecificSector (selectstock); //D
//			refreshGeGuFengXiResult (selectstock); //���ɶ԰�������
			
			paneldayCandle.displayQueKou(true);
			refreshKXianOfTDXNodeWithSuperBanKuai (selectstock, selectstock.getBanKuai() ); //���ɶ԰���K��
			
			refreshTDXGeGuZhanBi (selectstock.getStock()); //���ɶԴ�������
			//ͬ�����͸��ɸ���������
			LocalDate curselecteddate = panelbkwkcjezhanbi.getCurSelectedDate();
			chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(curselecteddate));
			
			displayStockCurrentFxResult (selectedGeguTable);
			
			String reminder = "��ʾ: \n"
					+ "1. ����ռ�����ʸߵĶ��ע��"
					+ "2. ����ռ�Ⱥͳɽ���/�ɽ����Ƿ��롣"
					;
			showReminderMessage (reminder);
			
//			cbxshizhifx.setSelected(false);
			
			hourglassCursor = null;
			Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
			setCursor(hourglassCursor2);
		
	}
	/*
	 * 
	 */
	private void adjustDate(LocalDate dateneedtobeadjusted)
	{
		//ÿ����һ���µ�һ�ܿ�ʼ����Ϊ��û�е������ݣ�����ʾΪû�����ݣ����԰�ʱ���������һ����
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
				btnresetdate.setEnabled(false);

	}
	/*
	 * �û��ڽ���Ĳ���������ģ���Эͬ����
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
		//���pie chart
		piechartpanelbankuaidatachangelisteners.add(pnllastestggzhanbi);
		piechartpanelbankuaidatachangelisteners.add(panelLastWkGeGuZhanBi);
		piechartpanelbankuaidatachangelisteners.add(pnllastestggcjlzhanbi);
		
		
		//���ɶ԰��
//		barchartpanelstockofbankuaidatachangelisteners.add(panelgegucje); //���ɶ��ڰ�齻�׶�,һ�㿴���ɶԴ��̵ĳɽ������Ͳ�����
//		barchartpanelstockofbankuaidatachangelisteners.add(panelggbkcjezhanbi);
		barchartpanelstockdatachangelisteners.add(panelGgDpCjeZhanBi);
		barchartpanelstockdatachangelisteners.add(panelggdpcjlwkzhanbi);
		//�û����bar chart��һ��column, highlight bar chart
		chartpanelhighlightlisteners.add(panelGgDpCjeZhanBi);
		chartpanelhighlightlisteners.add(panelbkwkcjezhanbi);
		chartpanelhighlightlisteners.add(paneldayCandle);
		chartpanelhighlightlisteners.add(panelggdpcjlwkzhanbi);
		chartpanelhighlightlisteners.add(pnlbkwkcjlzhanbi);
		
		//ͬ��������Ҫhighlight������
		bkfxhighlightvaluesoftableslisteners.add(tableGuGuZhanBiInBk);
		bkfxhighlightvaluesoftableslisteners.add(tableExternalInfo);
		bkfxhighlightvaluesoftableslisteners.add(tablexuandingzhou);
		bkfxhighlightvaluesoftableslisteners.add(tablexuandingminusone);
		bkfxhighlightvaluesoftableslisteners.add(tablexuandingminustwo);
		bkfxhighlightvaluesoftableslisteners.add(tablexuandingplusone);
		bkfxhighlightvaluesoftableslisteners.add(tableExternalInfo);
	}
	/*
	 * ���а��ռ�������ʵ�����
	 */
	private void gettBanKuaiZhanBiRangedByGrowthRateOfPeriod (String period)
	{
		this.globeperiod = period;
    	LocalDate curselectdate = dateChooser.getLocalDate(); 

    	List<TDXNodes> bkwithcje = initializeBanKuaiZhanBiByGrowthRate (curselectdate);
		((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).addBanKuai( bkwithcje );
		((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).refresh(curselectdate,0,globeperiod);
		
		//��ȡ�ý׶ε�ָ���ؼ�����
//		LocalDate requirestart = curselectdate.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange() + 3,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
//		LocalDate requirestart = CommonUtility.getSettingRangeDate(curselectdate,"middle").with(DayOfWeek.MONDAY);
//		
//		Integer[] wantednewstype = {Integer.valueOf(Meeting.ZHISHUDATE)};
//		EventService allDbmeetingService = new DBMeetingService ();
//	    Cache cacheAll = new Cache("ALL",allDbmeetingService, null,requirestart,LocalDate.now(),wantednewstype);
//		
//		zhishukeylists = cacheAll.produceMeetings();
		
    	//��ʾ���̳ɽ���
		NumberFormat percentFormat = NumberFormat.getPercentInstance(Locale.CHINA);
    	percentFormat.setMinimumFractionDigits(1);
    	DecimalFormat df2 = new DecimalFormat(".##");
    	TDXNodes childnode = (TDXNodes)this.allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode("000300", BkChanYeLianTreeNode.TDXBK);
    	NodeXPeriodData bkdata = ((TDXNodes)childnode).getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		try {
				Double cjerec = bkdata.getChengJiaoEr(curselectdate,0);
				Double zhanbi = bkdata.getChenJiaoErZhanBi(curselectdate, 0);
				lblszcje.setText( df2.format(cjerec /100000000.0) +  " (" + percentFormat.format (zhanbi) + ")");
		} catch (java.lang.NullPointerException e) {
				lblszcje.setText( "����û������" );
		}
			
		childnode = (TDXNodes)this.allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode("999999", BkChanYeLianTreeNode.TDXBK); 
		bkdata = ((TDXNodes)childnode).getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		try {
				Double cjerec = bkdata.getChengJiaoEr(curselectdate,0);
				Double zhanbi = bkdata.getChenJiaoErZhanBi(curselectdate, 0);
				lblshcje.setText( df2.format(cjerec /100000000.0) +  " (" + percentFormat.format (zhanbi) + ")");
		} catch (java.lang.NullPointerException e) {
				lblshcje.setText( "����û������" );
		}
		 
		childnode = (TDXNodes)this.allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode("399006", BkChanYeLianTreeNode.TDXBK);	
		bkdata = ((TDXNodes)childnode).getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		try {
				Double cjerec = bkdata.getChengJiaoEr(curselectdate,0);
				Double zhanbi = bkdata.getChenJiaoErZhanBi(curselectdate, 0);
				lblcybcje.setText( df2.format(cjerec /100000000.0) +  " (" + percentFormat.format (zhanbi) + ")");
		} catch (java.lang.NullPointerException e) {
				lblcybcje.setText( "����û������" );
		}
			
		childnode = (TDXNodes)this.allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode("000016", BkChanYeLianTreeNode.TDXBK); 
		bkdata = ((TDXNodes)childnode).getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		try {
				Double cjerec = bkdata.getChengJiaoEr(curselectdate,0);
				Double zhanbi = bkdata.getChenJiaoErZhanBi(curselectdate, 0);
				lbl50cje.setText( df2.format(cjerec /100000000.0) +  " (" + percentFormat.format (zhanbi) + ")");
		} catch (java.lang.NullPointerException e) {
				lbl50cje.setText( "����û������" );
		}
		
		curselectdate = null;
//		cacheAll = null;
		SystemAudioPlayed.playSound();
	}
	/*
	 * ���curselectdate������
	 */
	private List<TDXNodes> initializeBanKuaiZhanBiByGrowthRate (LocalDate curselectdate)
	{
//		LocalDate requirestart = curselectdate.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange() + 3,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		LocalDate requirestart = CommonUtility.getSettingRangeDate(curselectdate,"middle").with(DayOfWeek.MONDAY);
		
    	BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)this.allbksks.getAllBkStocksTree().getModel().getRoot();
		int bankuaicount = allbksks.getAllBkStocksTree().getModel().getChildCount(treeroot);
		
		this.allbksks.getDaPan(requirestart, curselectdate,this.globeperiod);
		
		List<TDXNodes> bkwithcje = new ArrayList<TDXNodes> ();
		for(int i=0;i< bankuaicount; i++) {
			
			BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode) this.allbksks.getAllBkStocksTree().getModel().getChild(treeroot, i);
			if(childnode.getType() != BkChanYeLianTreeNode.TDXBK) 
				continue;
			
			if( !((BanKuai)childnode).isShowinbkfxgui() )
				continue;
			
			if( ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL) 
					||  ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL)  ) //��Щָ����û�и��ɺͳɽ����ģ�������ȽϷ�Χ
				continue;
				
			childnode = this.allbksks.getBanKuai( (BanKuai)childnode, requirestart, curselectdate,globeperiod);
			
			NodeXPeriodData bkxdata = ((TDXNodes)childnode).getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
			if(bkxdata.getIndexOfSpecificDateOHLCData(curselectdate, 0) != null)//��鵱��û������Ҳ�����ǣ����һ�㲻����û�����ݣ�û������˵���ð�����ܻ�û�е��������߹�ȥ�У����ڳɽ����Ѿ����������ݿ�
				bkwithcje.add( (BanKuai)childnode );

		}
	
		requirestart = null;
		return bkwithcje;
	}
	/*
	 * �û�ѡ��һ������column�����Ӧ����
	 */
	protected void refreshAfterUserSelectBanKuaiColumn (BanKuai bkcur, String selectedinfo) 
	{
 		LocalDate selectdate = LocalDate.parse(selectedinfo);
		chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(selectdate));
		
		editorPanenodeinfo.displayAllNewsOfSpecificWeek (selectdate);
		
		setUserSelectedColumnMessage(bkcur,selectedinfo);
		
		String reminder = "��ʾ: \n"
						+ "1. ����ռ�Ⱥͳɽ���/�ɽ����Ƿ��룿 \n"
						+ "2. ȱ�ڵ���ɡ�"
						;
		showReminderMessage (reminder);

		//�������ã���ʾѡ���ܷ�������
		if(!menuItemnonshowselectbkinfo.getText().contains("X"))
			return;
			
			//ѡ���ܵİ���������
			LocalDate cursetdate = dateChooser.getLocalDate(); 
			if(!CommonUtility.isInSameWeek(selectdate,cursetdate) ) {
				allbksks.syncBanKuaiAndItsStocksForSpecificTime (bkcur, CommonUtility.getSettingRangeDate(selectdate,"large"), selectdate, globeperiod);
				
				List<TDXNodes> bkwithcje = initializeBanKuaiZhanBiByGrowthRate (selectdate);
				((BanKuaiInfoTableModel)tableselectedwkbkzb.getModel()).addBanKuai( bkwithcje );
				((BanKuaiInfoTableModel)tableselectedwkbkzb.getModel()).refresh(selectdate,0,globeperiod);
				
				tabbedPanebk.setTitleAt(1, "ѡ����" + selectdate);
				//�����ҵ����
				Integer rowindex = ((BanKuaiInfoTableModel)tableselectedwkbkzb.getModel() ).getBanKuaiRowIndex(bkcur.getMyOwnCode());
				if(rowindex >0) {
						int modelRow = tableselectedwkbkzb.convertRowIndexToView(rowindex);
						tableselectedwkbkzb.setRowSelectionInterval(modelRow, modelRow);
						tableselectedwkbkzb.scrollRectToVisible(new Rectangle(tableGuGuZhanBiInBk.getCellRect(modelRow, 0, true)));
				}
				
				//��ʾ������
				if(bkcur.getBanKuaiLeiXing().equals(BanKuai.HASGGWITHSELFCJL) ) {//Ӧ�����и��ɵİ��������ʾ���ĸ��ɣ�
					refreshSpecificBanKuaiFengXiResult (bkcur,selectdate,globeperiod);
				}
					
			} 
			
			SystemAudioPlayed.playSound();
	}
	/*
	 * ��������ʾ�û���ѡ���ܸ���ռ��������������
	 */
	private void refreshSpecificBanKuaiFengXiResult (BanKuai selectedbk, LocalDate selecteddate,String period)
	{
		//��ʾѡ���ܹ�Ʊ�������
		((BanKuaiGeGuTableModel)tablexuandingzhou.getModel()).refresh(selectedbk, selecteddate,period);
		tabbedPanegegu.setTitleAt(2, this.getTabbedPanegeguTabTiles(2) + selecteddate);
		
		//��ʾѡ����-1��Ʊ�������
		LocalDate selectdate2 = selecteddate.minus(1,ChronoUnit.WEEKS).with(DayOfWeek.FRIDAY);
		((BanKuaiGeGuTableModel)tablexuandingminusone.getModel()).refresh(selectedbk, selectdate2,period);
		tabbedPanegegu.setTitleAt(3,  selectdate2 + "(-1)");

		
		//��ʾѡ����-2��Ʊ�������
		LocalDate selectdate3 = selecteddate.minus(2,ChronoUnit.WEEKS).with(DayOfWeek.FRIDAY);
		((BanKuaiGeGuTableModel)tablexuandingminustwo.getModel()).refresh(selectedbk, selectdate3,period);
		tabbedPanegegu.setTitleAt(4,  selectdate3 + "(-2)");

		
		//��ʾѡ����+1��Ʊ�������
		LocalDate selectdate4 = selecteddate.plus(1,ChronoUnit.WEEKS).with(DayOfWeek.FRIDAY);
		((BanKuaiGeGuTableModel)tablexuandingplusone.getModel()).refresh(selectedbk, selectdate4,period);
		tabbedPanegegu.setTitleAt(5,  selectdate4 + "(+1)");
		
		refreshBanKuaiGeGuTableHightLight ();
	}
	

	/*
	 * �԰����ɱ����
	 */
	private void refreshBanKuaiGeGuTableHightLight ()
	{
		setExportMainConditionBasedOnUserSelection (displayexpc);
		
		bkfxhighlightvaluesoftableslisteners.forEach(l -> l.hightLightFxValues( displayexpc ) );

		//����panel��˵��hightLightFxValues��һ�����������ã���Ϊpanel ���� �ڵ�������ϼ���ռ��
		panelGgDpCjeZhanBi.hightLightFxValues(displayexpc);
		panelbkwkcjezhanbi.hightLightFxValues(displayexpc);
		

	}
	/*
	 * ��ʾ����ڵĸ��ɵĵ�ռ��
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
			JOptionPane.showMessageDialog(null,"��������!","Warning",JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		//����ָ֤��һ����ʾK��
		BanKuai zhishubk =  (BanKuai) allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode("999999",BkChanYeLianTreeNode.TDXBK);
		//һ����ͬ����������Լ�������������
		allbksks.syncBanKuaiAndItsStocksForSpecificTime (selectedbk, CommonUtility.getSettingRangeDate(curselectdate,"large"), curselectdate, globeperiod);
		
		//��ʾK��
		paneldayCandle.displayQueKou(false);
		refreshKXianOfTDXNodeWithSuperBanKuai ( selectedbk, zhishubk );
		
//		//�������ռ��
//		for(BarChartPanelDataChangedListener tmplistener : barchartpanelbankuaidatachangelisteners) {
//			tmplistener.updatedDate(selectedbk, CommonUtility.getSettingRangeDate(curselectdate,"basic"),curselectdate,globeperiod);
//		}
		//�����������
		if(selectedbk.getBanKuaiLeiXing().equals(BanKuai.HASGGWITHSELFCJL)) { //�и��ɲ���Ҫ���£���Щ�����û�и��ɵ�
			
			//������ķ������������Ŀ>0����Ҫ�ѷ��������ĸ��ɱ�Ǻ�
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
			//�����������ռ��info
			((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).refresh(selectedbk, curselectdate,period);
			((BanKuaiGeGuBasicTableModel)tableExternalInfo.getModel()).refresh(selectedbk, curselectdate,period);
			//������ʾ����
			tabbedPanegegu.setTitleAt(0, "��ǰ��" + curselectdate);
			//��ʾ2�ܵİ�����pie chart
			for(PieChartPanelDataChangedListener tmplistener : piechartpanelbankuaidatachangelisteners) {
				tmplistener.updateDate(selectedbk, curselectdate, 0,globeperiod);
			}
		}
		
		//�������ռ��
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
	 * ���а��ռ�������ʵ�����
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
		        	bkfxCancelAction.putValue(Action.NAME, "����");
		        	LocalDate cursettingdate = dateChooser.getLocalDate();// dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		        	if(cursettingdate.equals(LocalDate.now())) 
		        		btnresetdate.setEnabled(false);
		        	
		        	dateChooser.setEnabled(true);
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
		            	
		            	JOptionPane.showMessageDialog(null, "��������ֹ��", "��ֹ������", JOptionPane.WARNING_MESSAGE);
		            } catch (final Exception e) {
		            	e.printStackTrace();
		            }

		            bkfxtask = null;
		            break;
		          case STARTED:
		        	  //��صİ���disable
		        	  dateChooser.setEnabled(false);
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
		        	  bkfxCancelAction.putValue(Action.NAME, "��ֹ������");
		        	  
		            break;
		          }
		          break;
		        }
		      }
		    });
		
    	bkfxtask.execute();
	}
	/*
	 * �ѵ�ǰ�İ�鵱�ܷ��������ĵ���
	 */
	private void exportBanKuaiWithGeGuOnCondition2 ()
	{
	
		if(exportcond == null || exportcond.size() == 0) {
			if(!ckboxshowcje.isSelected() && !ckbxdpmaxwk.isSelected() && !chkliutongsz.isSelected() && !ckbxcjemaxwk.isSelected()){
				JOptionPane.showMessageDialog(null,"δ���õ����������������õ���������");
				return;
			} else if(exportcond == null) { //�û��������������ֱ�ӵ����������û���ȵ�����룬������ͳһ�������Ϊ
				exportcond = new ArrayList<ExportCondition> ();
				initializeExportConditions ();
			} else
				initializeExportConditions ();
		}

		String msg =  "������ʱ�ϳ�������ȷ�������Ƿ���ȷ��\n�Ƿ񵼳���";
		int exchangeresult = JOptionPane.showConfirmDialog(null,msg , "ȷʵ������", JOptionPane.OK_CANCEL_OPTION);
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
		String exportfilename = sysconfig.getTDXModelMatchExportFile () + "TDXģ�͸���" + dateshowinfilename + ".EBK";
		File filefmxx = new File( exportfilename );
		
		if(!filefmxx.getParentFile().exists()) {  
            //���Ŀ���ļ����ڵ�Ŀ¼�����ڣ��򴴽���Ŀ¼  
            logger.debug("Ŀ���ļ�����Ŀ¼�����ڣ�׼����������");  
            if(!filefmxx.getParentFile().mkdirs()) {  
                System.out.println("����Ŀ���ļ�����Ŀ¼ʧ�ܣ�");  
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
		        	progressBarExport.setString("���ڵ���..." + (Integer) eventexport.getNewValue() + "%(,���ȡ������)");
		        	progressBarExport.setToolTipText("���ȡ������");
		          break;
		        case "state":
		          switch ((StateValue) eventexport.getNewValue()) {
		          case DONE:
		        	exportCancelAction.putValue(Action.NAME, "������������");
		            try {
		              final int count = exporttask.get();
		              //����XML
//		              bkcyl.parseWeeklyBanKuaiFengXiFileToXmlAndPatchToCylTree (filefmxx,curselectdate.with(DayOfWeek.FRIDAY) );
		              
		              int exchangeresult = JOptionPane.showConfirmDialog(null, "������ɣ��Ƿ��" + filefmxx.getAbsolutePath() + "�鿴","�������", JOptionPane.OK_CANCEL_OPTION);
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
		            	JOptionPane.showMessageDialog(null, "�����������ɱ���ֹ��", "������������",JOptionPane.WARNING_MESSAGE);
		            	progressBarExport.setString("����������������");
		            } catch (final Exception e) {
//		              JOptionPane.showMessageDialog(Application.this, "The search process failed", "Search Words",
//		                  JOptionPane.ERROR_MESSAGE);
		            }

		            exporttask = null;
		            break;
		          case STARTED:
		          case PENDING:
		        	  exportCancelAction.putValue(Action.NAME, "ȡ������");
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
//		tabbedPanebk.setTitleAt(1, "ѡ����" );
		
		btnresetdate.setEnabled(true);
		
		ckboxparsefile.setSelected(false);
		tfldparsedfile.setText("");
		
		paneldayCandle.resetDate();
		
		editorPanebankuai.resetSelectedBanKuai ();
		
		
	}
	private void clearTheGuiBeforDisplayNewInfoSection2 ()
	{
		((BanKuaiInfoTableModel)tableselectedwkbkzb.getModel()).deleteAllRows();
		tabbedPanebk.setTitleAt(1, "ѡ����" );
		
		tabbedPanegegu.setSelectedIndex(0);
		
		panelbkwkcjezhanbi.resetDate();
		
		pnllastestggzhanbi.resetDate();
		
		
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
		
		menuItemchengjiaoer.setText("X ���ɽ�������");
    	menuItemliutong.setText("����ͨ��ֵ����");
    	menuItemtimerangezhangfu.setText("���׶��Ƿ�����");
    	
		editorPanenodeinfo.setText("");
	}
	/*
	 * ָ��tabbedPanegegu��title���������ּ����ط���һ��
	 */
	private String getTabbedPanegeguTabTiles (int tabindex)
	{
		String[] jtabbedpanetitle = { "��ǰ��", "������Ϣ","ѡ����","ѡ����-1","ѡ����-2","ѡ����+1"};
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
	 * ��ʾ������԰���ռ�ȵ�chart
	 */
	private void refreshGeGuBanKuaiFengXiResult (StockOfBanKuai stock)
	{
//			LocalDate curselectdate = dateChooser.getLocalDate();//dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//
//			barchartpanelstockofbankuaidatachangelisteners.forEach(l -> l.updatedDate(stock, curselectdate, 0,globeperiod));
	}
	/*
	 * �û�ѡ����ɺ���ʾ��������Դ��̵ĸ���ռ��
	 */
	private void refreshTDXGeGuZhanBi (Stock selectstock)
	{
		selectstock.setHasReviewedToday();
		
		LocalDate curselectdate = dateChooser.getLocalDate();// dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		selectstock = allbksks.getStock(selectstock,CommonUtility.getSettingRangeDate(curselectdate, "large"),curselectdate,NodeGivenPeriodDataItem.WEEK);
		
		
		for (BarChartPanelDataChangedListener tmplistener : barchartpanelstockdatachangelisteners) {
			tmplistener.updatedDate(selectstock, CommonUtility.getSettingRangeDate(curselectdate, "basic"),curselectdate, globeperiod);
		}
	}
	/*
	 * ���͸��ɵ�K�ߣ�����ͬʱ��ʾ�����ԶԱ��о�
	 */
	private void refreshKXianOfTDXNodeWithSuperBanKuai (TDXNodes selectnode,TDXNodes superbankuai)
	{
		LocalDate curselectdate = dateChooser.getLocalDate();// dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate requireend = curselectdate.with(DayOfWeek.SATURDAY);
		
		TDXNodes tmpnode = null;
		if(selectnode.getType() == BkChanYeLianTreeNode.TDXGG) {
			selectnode = allbksks.getStock((Stock)selectnode,CommonUtility.getSettingRangeDate(curselectdate, "large"),curselectdate,NodeGivenPeriodDataItem.WEEK);
			//����K�����ƣ�ĿǰK�����ƺͳɽ��������ߺ��������������Ƿֿ��ģ����Ե���ʱ��Ҫ�ر�С�ģ��Ժ��ϲ�
			this.allbksks.syncStockData((Stock)selectnode);
			
			tmpnode = selectnode;
		} else if(selectnode.getType() == BkChanYeLianTreeNode.TDXBK) {
			selectnode = allbksks.getBanKuai( (BanKuai)selectnode, CommonUtility.getSettingRangeDate(curselectdate, "large"),curselectdate, NodeGivenPeriodDataItem.WEEK);
			this.allbksks.syncBanKuaiData( (BanKuai)selectnode);
			
			tmpnode = selectnode;
		} else if (selectnode.getType() == BkChanYeLianTreeNode.BKGEGU) {
//			Stock stock = allbksks.getStock( ((StockOfBanKuai)selectnode).getStock(),CommonUtility.getSettingRangeDate(curselectdate, "large"),curselectdate,NodeGivenPeriodDataItem.WEEK);
			this.allbksks.syncStockData( ((StockOfBanKuai)selectnode).getStock() );
			
			tmpnode = ((StockOfBanKuai)selectnode).getStock() ;
		}
		
		 if(superbankuai.getType() == BkChanYeLianTreeNode.TDXBK) {
			 superbankuai = allbksks.getBanKuai( (BanKuai)superbankuai, CommonUtility.getSettingRangeDate(curselectdate, "large"),curselectdate, NodeGivenPeriodDataItem.WEEK);
			 this.allbksks.syncBanKuaiData( (BanKuai)superbankuai);
		 }
		
		
		this.allbksks.getDaPanKXian (CommonUtility.getSettingRangeDate(curselectdate, "large"),curselectdate,NodeGivenPeriodDataItem.DAY); 

		paneldayCandle.updatedDate(superbankuai,tmpnode,CommonUtility.getSettingRangeDate(curselectdate, "basic"),requireend,NodeGivenPeriodDataItem.DAY);

		Integer[] wantednewstype = {Integer.valueOf(Meeting.ZHISHUDATE)};
		EventService allDbmeetingService = new DBMeetingService ();
	    Cache cacheAll = new Cache("ALL",allDbmeetingService, null,CommonUtility.getSettingRangeDate(curselectdate, "basic"),requireend,wantednewstype);
		zhishukeylists = cacheAll.produceMeetings();
		paneldayCandle.updateZhiShuKeyDates (zhishukeylists); //ָ���ؼ�����
		
		cacheAll = null;
		allDbmeetingService = null;
	}
	/*
	 * 
	 */
	private void displayStockSuoShuBanKuai(Stock selectstock) 
	{
		editorPanebankuai.displayBanKuaiListContentsForStock(selectstock);
		
	}
	/*
	 * ��ʾ�û����bar column��Ӧ����ʾ����Ϣ
	 */
	private void setUserSelectedColumnMessage(TDXNodes node,String seldate) 
	{
		tfldselectedmsg.displayNodeSpecificDateInfo(node,LocalDate.parse(seldate),this.globeperiod);
//    	String htmlstring = node.getNodeXPeroidDataInHtml(LocalDate.parse(seldate),this.globeperiod);
//		tfldselectedmsg.displayNodeSelectedInfo (htmlstring);
	}
	private void setUserSelectedColumnMessage(String htmlstring) 
	{
		tfldselectedmsg.displayNodeSelectedInfo (htmlstring);
	}
	/*
	 * �û���������ط�����ʾ������ʾ��Ϣ
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
		menuItemQiangShigg.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				int row = tableGuGuZhanBiInBk.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"��ѡ��һ�����","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				int modelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(row); 
				StockOfBanKuai stock = ((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).getStock(modelRow);

				
        		String title = "ǿ�Ƹ��ɰ��";
        		String owner = "";
        		String keywords = "ǿ�Ƹ��ɰ��";
        		LocalDate mDate = dateChooser.getLocalDate();
        		int meetingtype = Meeting.QIANSHI;
        		
        		//Set up description of all GPC
        		String descriptions = "";
        		BanKuaiAndStockTree cyltree = bkcyl.getBkChanYeLianTree();
        		int bankuaicount = cyltree.getModel().getChildCount(cyltree.getModel().getRoot());
    			for(int i=0;i< bankuaicount; i++) {
    				
    				BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode)cyltree.getModel().getChild(cyltree.getModel().getRoot(), i);
    				String nodename = childnode.getMyOwnName();
    				String nodecode = childnode.getMyOwnCode();
    				descriptions = descriptions + nodecode.toUpperCase() + "-" + nodename +   "\n";
    			}
				
				Meeting meeting = new Meeting(title, mDate,
		        		descriptions, keywords, new HashSet<>(),"URL",owner,meetingtype);
				meeting.setNewsOwnerCodes(stock.getMyOwnCode() + "gg"); //
				EventService newsDbService = new DBMeetingService ();
				try {
					newsDbService.createMeeting(meeting);
					dateChooser.getStockCalendar().refreshStockCalander ();
				} catch (SQLException e) {
					e.printStackTrace();
				}
//				newsdbopt.addBanKuaiNews(meeting);
		
			}
			
		});

		menuItemRuoShigg.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				int row = tableGuGuZhanBiInBk.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"��ѡ��һ�����","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				int modelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(row); 
				StockOfBanKuai stock = ((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).getStock(modelRow);

				
        		String title = "���Ƹ��ɰ��";
        		String owner = "";
        		String keywords = "���Ƹ��ɰ��"; 
        		
        		LocalDate mDate = dateChooser.getLocalDate();
        		int meetingtype = Meeting.RUOSHI;
        		
        		//Set up description of all GPC
        		String descriptions = "";
        		BanKuaiAndStockTree cyltree = bkcyl.getBkChanYeLianTree();
        		int bankuaicount = cyltree.getModel().getChildCount(cyltree.getModel().getRoot());
    			for(int i=0;i< bankuaicount; i++) {
    				
    				BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode)cyltree.getModel().getChild(cyltree.getModel().getRoot(), i);
    				String nodename = childnode.getMyOwnName();
    				String nodecode = childnode.getMyOwnCode();
    				descriptions = descriptions + nodecode.toUpperCase() + "-" + nodename +   "\n";
    			}
        		
				
				Meeting meeting = new Meeting(title, mDate,
		        		descriptions, keywords, new HashSet<>(),"URL",owner,meetingtype);
				meeting.setNewsOwnerCodes(stock.getMyOwnCode() + "gg"); //
				EventService newsDbService = new DBMeetingService ();
				try {
					newsDbService.createMeeting(meeting);
					dateChooser.getStockCalendar().refreshStockCalander ();
				} catch (SQLException e) {
					e.printStackTrace();
				}
//				newsdbopt.addBanKuaiNews(meeting);
        		
			}
			
		});
		   
		menuItemQiangShibk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				int row = tableBkZhanBi.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"��ѡ��һ�����","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				int modelRow = tableBkZhanBi.convertRowIndexToModel(row); 
				BanKuai bankuai = ((BanKuaiInfoTableModel) tableBkZhanBi.getModel()).getBanKuai(modelRow);

				
        		String title = "ǿ�ư��";
        		String owner = bankuai.getMyOwnCode();
        		String keywords = "ǿ�ư��";
        		LocalDate mDate = dateChooser.getLocalDate();
        		int meetingtype = Meeting.QIANSHI;
        		
        		//Set up description of all GPC
        		String descriptions = "";
        		BanKuaiAndStockTree cyltree = bkcyl.getBkChanYeLianTree();
        		int bankuaicount = cyltree.getModel().getChildCount(cyltree.getModel().getRoot());
    			for(int i=0;i< bankuaicount; i++) {
    				
    				BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode)cyltree.getModel().getChild(cyltree.getModel().getRoot(), i);
    				String nodename = childnode.getMyOwnName();
    				String nodecode = childnode.getMyOwnCode();
    				descriptions = descriptions + nodecode.toUpperCase() + "-" + nodename +   "\n";
    			}
        		
				
				Meeting meeting = new Meeting(title, mDate,
		        		descriptions, keywords, new HashSet<>(),"URL",owner,meetingtype);
				meeting.setNewsOwnerCodes(bankuai.getMyOwnCode() + "bk"); //
				EventService newsDbService = new DBMeetingService ();
				try {
					newsDbService.createMeeting(meeting);
					dateChooser.getStockCalendar().refreshStockCalander ();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
		});

		
		menuItemRuoShibk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				int row = tableBkZhanBi.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"��ѡ��һ�����","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				int modelRow = tableBkZhanBi.convertRowIndexToModel(row); 
				BanKuai bankuai = ((BanKuaiInfoTableModel) tableBkZhanBi.getModel()).getBanKuai(modelRow);

				
        		String title = "���Ƹ��ɰ��";
        		String owner = "";
        		String keywords = "���Ƹ��ɰ��"; 
        		
        		LocalDate mDate = dateChooser.getLocalDate();
        		int meetingtype = Meeting.RUOSHI;
        		
        		//Set up description of all GPC
        		String descriptions = "";
        		BanKuaiAndStockTree cyltree = bkcyl.getBkChanYeLianTree();
        		int bankuaicount = cyltree.getModel().getChildCount(cyltree.getModel().getRoot());
    			for(int i=0;i< bankuaicount; i++) {
    				
    				BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode)cyltree.getModel().getChild(cyltree.getModel().getRoot(), i);
    				String nodename = childnode.getMyOwnName();
    				String nodecode = childnode.getMyOwnCode();
    				descriptions = descriptions + nodecode.toUpperCase() + "-" + nodename +   "\n";
    			}
        		
				
				Meeting meeting = new Meeting(title, mDate,
		        		descriptions, keywords, new HashSet<>(),"URL",owner,meetingtype);
				meeting.setNewsOwnerCodes(bankuai.getMyOwnCode() + "bk"); 
				EventService newsDbService = new DBMeetingService ();
				try {
					newsDbService.createMeeting(meeting);
					dateChooser.getStockCalendar().refreshStockCalander ();
				} catch (SQLException e) {
					e.printStackTrace();
				}
//				newsdbopt.addBanKuaiNews(meeting);
			}
			
		});

		
		
		((InvisibleTreeModel)bkcyl.getBkChanYeLianTree().getModel()).addTreeModelListener( new  TreeModelListener () {

			@Override
			public void treeNodesChanged(TreeModelEvent arg0) {
//				updateBkcylTree ();
			}

			@Override
			public void treeNodesInserted(TreeModelEvent arg0) {
//				updateBkcylTree ();
			}

			@Override
			public void treeNodesRemoved(TreeModelEvent arg0) {
//				updateBkcylTree ();
			}

			@Override
			public void treeStructureChanged(TreeModelEvent arg0) {
//				updateBkcylTree ();
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

                	Multimap<String, LocalDate> nodespecificdatainfo = tfldselectedmsg.getDisplayBanKuaiSpecificDateInfo();
                	for (Map.Entry entry : nodespecificdatainfo.entries()) 
                	{ 
                		String nodecode = (String) entry.getKey();
                		LocalDate selectdate = (LocalDate) entry.getValue();
                		
                		BanKuai bk = (BanKuai) allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode(nodecode, BkChanYeLianTreeNode.TDXBK);
                		List<TDXNodes> tmplist = new ArrayList<> ();
                		tmplist.add(bk);
                		exportTDXNodesDataToCsv(tmplist,selectdate.toString());
                		tmplist = null;
                	}
                	nodespecificdatainfo = tfldselectedmsg.getDisplayStockSpecificDateInfo();
                	for (Map.Entry entry : nodespecificdatainfo.entries()) 
                	{ 
                		String nodecode = (String) entry.getKey();
                		LocalDate selectdate = (LocalDate) entry.getValue();
                		
                		Stock stock = (Stock) allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode(nodecode, BkChanYeLianTreeNode.TDXGG);
                		List<TDXNodes> tmplist = new ArrayList<TDXNodes>();
                		tmplist.add(stock);
                		exportTDXNodesDataToCsv( tmplist ,selectdate.toString());
                		tmplist = null;
                	}
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
        					JOptionPane.showMessageDialog(null,"��ѡ��һ����飡","Warning",JOptionPane.WARNING_MESSAGE);
        					return;
        				}
        				int modelRow = tableBkZhanBi.convertRowIndexToModel(row);
        				BanKuai selectedbk = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(modelRow);
                    	
        				paneldayCandle.displayQueKou(true);
        				refreshKXianOfTDXNodeWithSuperBanKuai ( stockofbank, selectedbk );
                		
                		
                    } else if(zhishuinfo.toLowerCase().equals("dapanzhishu") ) {
                    	String danpanzhishu = JOptionPane.showInputDialog(null,"��������ӵĴ���ָ��", "999999");
                    	BanKuai zhishubk =  (BanKuai) allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode(danpanzhishu.toLowerCase(),BkChanYeLianTreeNode.TDXBK);
                    	if(zhishubk == null)  {
        					JOptionPane.showMessageDialog(null,"ָ����������","Warning",JOptionPane.WARNING_MESSAGE);
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
		
		menuItemnonshowselectbkinfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if( menuItemnonshowselectbkinfo.getText().contains("X") ) {
            		menuItemnonshowselectbkinfo.setText("ѡ��ͬʱ������ܷ�������");
            		panelbkwkcjezhanbi.setAllowDrawAnnoation(false);
            	} else {
            		menuItemnonshowselectbkinfo.setText("Xѡ��ͬʱ������ܷ�������");
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
            	menuItemliutong.setText("X ����ͨ��ֵ����");
            	menuItemchengjiaoer.setText("���ɽ�������");
            	menuItemtimerangezhangfu.setText("���׶��Ƿ�����");
            	
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
            	menuItemchengjiaoer.setText("X ���ɽ�������");
            	menuItemliutong.setText("����ͨ��ֵ����");
            	menuItemtimerangezhangfu.setText("���׶��Ƿ�����");
            	
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
            	menuItemchengjiaoer.setText("���ɽ�������");
            	menuItemliutong.setText("����ͨ��ֵ����");
            	
            	
            	DateRangeSelectPnl datachoose = new DateRangeSelectPnl (1); 
        		JOptionPane.showMessageDialog(null, datachoose,"ѡ��ʱ���", JOptionPane.OK_CANCEL_OPTION);
        		
        		LocalDate searchstart = datachoose.getDatachoosestart();
        		LocalDate searchend = datachoose.getDatachooseend();
        		
        		menuItemtimerangezhangfu.setText("X ���׶��Ƿ�����" + searchstart.toString() + "~" + searchend.toString());
        		
        		int row = tableBkZhanBi.getSelectedRow();
    			int modelRow = tableBkZhanBi.convertRowIndexToModel(row);
    			BanKuai bk = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(modelRow);
    			NodeXPeriodData bknodexdata = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.DAY);
    			LocalDate curohlcstart = bknodexdata.getOHLCRecordsStartDate();
    			LocalDate curohlcend = bknodexdata.getOHLCRecordsEndDate();
    			
    			if(searchstart.isBefore(curohlcstart) || searchend.isAfter(curohlcend) ) {
    				JOptionPane.showMessageDialog(null,"ѡ��ʱ�������ȱʧ������ͬ�����ݻ��ߵ���ʱ��Ρ�","Warning",JOptionPane.WARNING_MESSAGE);
    				
    				menuItemtimerangezhangfu.setText("���׶��Ƿ�����");
    				
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
                	if(selecttitle.contains("ѡ����"))
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
////					JOptionPane.showMessageDialog(null,"��ѡ��һ����飡","Warning",JOptionPane.WARNING_MESSAGE);
////					return;
////				}
//				
////				int bkmodelRow = tableBkZhanBi.convertRowIndexToModel(bkrow);
////				BanKuai selectedbk = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(bkmodelRow);
//				
////				int ggrow = tableGuGuZhanBiInBk.getSelectedRow();
////				if(ggrow <0) {
////					JOptionPane.showMessageDialog(null,"��ѡ��һ����Ʊ��","Warning",JOptionPane.WARNING_MESSAGE);
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
						JOptionPane.showMessageDialog(null,"��鲻�ڷ������У��ɵ���������޸ģ�","Warning", JOptionPane.WARNING_MESSAGE);
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
    				
    				setUserSelectedColumnMessage(selectstock, selectedinfo);
    				
//    				if(cbxshizhifx.isSelected()) { //��ʾ��ֵ����
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
    				
    				setUserSelectedColumnMessage(selectstock, selectedinfo);
    				
    				String reminder = "��ʾ: \n"
    						+ "1. ���ɵ�ռ�Ⱥͳɽ���/�ɽ����Ƿ��룿 \n"
    						+ "2. K�ߺʹ��̹ؼ����ڵĶ�Ӧ���ơ�"
    						;
    				showReminderMessage (reminder);
    				
//    				if(cbxshizhifx.isSelected()) { //��ʾ��ֵ����
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
		
		//��ӵ�������
		btnaddexportcond.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				if(SwingUtilities.isLeftMouseButton(arg0) ) {
					
					if(!ckboxshowcje.isSelected() && !ckbxdpmaxwk.isSelected() && !chkliutongsz.isSelected() && !ckbxcjemaxwk.isSelected()){
						JOptionPane.showMessageDialog(null,"δ���õ����������������õ���������");
						return;
					} 
					
					initializeExportConditions ();
					
				} else if (SwingUtilities.isRightMouseButton(arg0)) {
					if( exportcond != null) { 
						exportcond.clear();
						
						btnaddexportcond.setText(String.valueOf(0));
						btnaddexportcond.setToolTipText("<html>������������(����Ҽ�ɾ������)<br></html>");
					}
					
				} else if (SwingUtilities.isMiddleMouseButton(arg0)) {
					
				}
		
			}
		});
		
		//ͻ����ʾ����WK����
		tfldltszmin.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				if( Strings.isNullOrEmpty(tfldltszmin.getText() )) {
					JOptionPane.showMessageDialog(null,"������ͻ����ʾ���MaxWk��");
					ckbxdpmaxwk.setSelected(false);
					return;
				}
				if(chkliutongsz.isSelected() ) {
					refreshBanKuaiGeGuTableHightLight ();
				}
			}
		});
		
		//ͻ����ʾ�ɽ����������ֵ
		ckbxcjemaxwk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if( Strings.isNullOrEmpty(tfldcjemaxwk.getText() )) {
					JOptionPane.showMessageDialog(null,"������ͻ����ʾ���MaxWk��");
					ckbxcjemaxwk.setSelected(false);
					return;
				}
				refreshBanKuaiGeGuTableHightLight ();
				
			}
		});
		ckbxhuanshoulv.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if( Strings.isNullOrEmpty(tfldhuanshoulv.getText() )) {
					JOptionPane.showMessageDialog(null,"������ͻ����ʾ�����ʣ�");
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
					JOptionPane.showMessageDialog(null,"������DPMINWK��");
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
					JOptionPane.showMessageDialog(null,"��������ͨ��ֵ���䣡");
					chkliutongsz.setSelected(false);
					return;
				}
				refreshBanKuaiGeGuTableHightLight ();
			}
		});
		//ͻ����ʾ����WK����
		tflddisplaydpmaxwk.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				if( Strings.isNullOrEmpty(tflddisplaydpmaxwk.getText() )) {
					JOptionPane.showMessageDialog(null,"������ͻ����ʾ���MaxWk��");
					ckbxdpmaxwk.setSelected(false);
					return;
				}
				refreshBanKuaiGeGuTableHightLight ();
				
			}
		});
		ckbxdpmaxwk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if( Strings.isNullOrEmpty(tflddisplaydpmaxwk.getText() )) {
					JOptionPane.showMessageDialog(null,"������ͻ����ʾMaxWk��");
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
					JOptionPane.showMessageDialog(null,"������ͻ����ʾ�ľ���ֵ��");
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
					JOptionPane.showMessageDialog(null,"������ͻ����ʾ�ĳɽ��");
					ckboxshowcje.setSelected(false);
					return;
				}
				refreshBanKuaiGeGuTableHightLight ();
			}
		});
		/*
		 * ����pie chart�ڵ�����Ǹ����� 
		 */
		pnllastestggzhanbi.addPropertyChangeListener(new PropertyChangeListener() 
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
		panelLastWkGeGuZhanBi.addPropertyChangeListener(new PropertyChangeListener() 
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
						JOptionPane.showMessageDialog(null,"���Ǹ��ɴ��룬�������룡","Warning", JOptionPane.WARNING_MESSAGE);
						return;
					} else {
//						if(userinputnode.getType() == BkChanYeLianTreeNode.TDXBK ) { //����û����������ǰ�飬
//							JOptionPane.showMessageDialog(null,"���Ǹ��ɴ��룬�������룡","Warning", JOptionPane.WARNING_MESSAGE);
//							return;
//						}
							
						nodecode = userinputnode.getMyOwnCode();
						try{ //����û�ѡ��ĺ��ϴ�ѡ��ĸ���һ�������ظ���������
//							String stockcodeincbx = ((BkChanYeLianTreeNode)combxstockcode.getSelectedItem()).getMyOwnCode();
							displayStockSuoShuBanKuai((Stock)userinputnode);
							
//							if(cbxstockcode.isEnabled() ) { //�����
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
					
					if(!findInputedNodeInTable (nodecode)) { //���û���ҵ�
//						tableGuGuZhanBiInBk.setRowSelectionInterval(rowindex,rowindex);
//						tableGuGuZhanBiInBk.getSelectionModel().clearSelection() ; //�ѵ�ǰ��table��ѡ��ȡ���������û������Ѿ��ҵ�
						//��Ȼȡ����table�еĸ��ɣ�����Ҫ�Ѽ���ͼ�����Ϣ����������û���ȥ˫����Щͼ����������⡣
						
//						panelGgDpCjeZhanBi.resetDate();
//						paneldayCandle.resetDate();
					} else { //�ڵ�ǰ����еĻ����Ͱ����PANEL���
//						panelGgDpCjeZhanBi.resetDate();
//						paneldayCandle.resetDate();
					}
						
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
						
			    		bkcyl.getBkChanYeLianTree().setCurrentDisplayedWk (newdate);
			    		DefaultTreeModel treeModel = (DefaultTreeModel) bkcyl.getBkChanYeLianTree().getModel();
			    		treeModel.reload();
			    		
//			    		setHighLightChenJiaoEr();
			    		
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
    					
    					//�ҵ��û�ѡ��İ��
    					BanKuai selectedbk = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(rowindex);
    					unifiedOperationsAfterUserSelectABanKuai (selectedbk);
//    					refreshCurentBanKuaiFengXiResult (selectedbk,globeperiod);
//    					displayNodeInfo(selectedbk);
////    					patchParsedFile (selectedbk);
//    					combxsearchbk.updateUserSelectedNode(selectedbk);
//    					refreshBanKuaiGeGuTableHightLight ();
    					
    					//�Ҳ����ù�Ʊ
    					String stockcode = combxstockcode.getSelectedItem().toString().substring(0, 6);
    					if(!findInputedNodeInTable (stockcode) )
    						JOptionPane.showMessageDialog(null,"��ĳ�����ɱ���û�з��ָùɣ����������ʱ����ڸù�ͣ��","Warning",JOptionPane.WARNING_MESSAGE);
    					
//    					Toolkit.getDefaultToolkit().beep();
    					hourglassCursor = null;
    					Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
    					setCursor(hourglassCursor2);
    					
    					SystemAudioPlayed.playSound();
    				} else {
    					JOptionPane.showMessageDialog(null,"�ð������Ϊ����ʾ�ڰ���б��У����� ���ص��ע���ҵ���� ������ã�","Warning",JOptionPane.WARNING_MESSAGE);
    				}
            	}
            }
        });
		
		tableGuGuZhanBiInBk.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) 
			{
//				if(e.getKeyCode() == KeyEvent.VK_UP) {
//					int row = tableGuGuZhanBiInBk.getSelectedRow();
//					if(row <0) {
//						JOptionPane.showMessageDialog(null,"��ѡ��һ����Ʊ��","Warning",JOptionPane.WARNING_MESSAGE);
//						return;
//					}
//					
//					int modelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(row -1 );
//					StockOfBanKuai selectstock = ((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).getStock (modelRow);
//					performActionsForGeGuTablesAfterUserSelected (selectstock,tableGuGuZhanBiInBk);
//				}
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
					JOptionPane.showMessageDialog(null,"��ѡ��һ����Ʊ��","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				int modelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(row);
				StockOfBanKuai selectstock = ((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).getStock (modelRow);
				
				if (arg0.getClickCount() == 1) { //�û����һ��
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
					JOptionPane.showMessageDialog(null,"��ѡ��һ����Ʊ��","Warning",JOptionPane.WARNING_MESSAGE);
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
					JOptionPane.showMessageDialog(null,"��ѡ��һ����Ʊ��","Warning",JOptionPane.WARNING_MESSAGE);
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
					JOptionPane.showMessageDialog(null,"��ѡ��һ����Ʊ��","Warning",JOptionPane.WARNING_MESSAGE);
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
					JOptionPane.showMessageDialog(null,"��ѡ��һ����Ʊ��","Warning",JOptionPane.WARNING_MESSAGE);
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
					JOptionPane.showMessageDialog(null,"��ѡ��һ����Ʊ��","Warning",JOptionPane.WARNING_MESSAGE);
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
					JOptionPane.showMessageDialog(null,"��ѡ��һ����飡","Warning",JOptionPane.WARNING_MESSAGE);
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
	
	protected void updatedComboxAndFengXiResult() 
	{
		BkChanYeLianTreeNode userinputnode = combxsearchbk.getUserInputNode();
		if(userinputnode == null ) { //����û����������ǰ�飬
			JOptionPane.showMessageDialog(null,"���ǰ����룬�������룡","Warning", JOptionPane.WARNING_MESSAGE);
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
			JOptionPane.showMessageDialog(null,"��Ʊ/���������󣡻��鲻�ڷ������У��ɵ���������޸ģ�","Warning", JOptionPane.WARNING_MESSAGE);
		}
		
	}
	
	/*
	 * �Բ�ȷ�����ڼ���ռ�ȣ�����ռ����Ϊ�Ƚ϶���
	 */
	protected void calStockNoneFixPeriodDpMXXWk() 
	{
		DateRangeSelectPnl datachoose = new DateRangeSelectPnl (1); 
		JOptionPane.showMessageDialog(null, datachoose,"ѡ��ʱ���", JOptionPane.OK_CANCEL_OPTION);
		
		LocalDate searchstart = datachoose.getDatachoosestart();
		LocalDate searchend = datachoose.getDatachooseend();
		
		int ggrow = tableGuGuZhanBiInBk.getSelectedRow();
		if(ggrow <0) {
			JOptionPane.showMessageDialog(null,"��ѡ��һ����Ʊ��","Warning",JOptionPane.WARNING_MESSAGE);
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
				 li1.appendText("ռ��MinWk=���ݲ������޷�����" );
			 else if(dpzbresult<0 ) {
				 li1.appendText("ռ��MaxWk=0" );
				 li4.appendText("ռ��MinWk=" + dpzbresult);
			 } else if(dpzbresult>0) {
				 li1.appendText("ռ��MaxWk=" + dpzbresult);
				 li4.appendText("ռ��MinWk=0" );
			 }
			 
			 DecimalFormat decimalformate = new DecimalFormat("%#0.00000");
			 org.jsoup.nodes.Element li2 = dl.appendElement("li");
			 li2.appendText("ռ��" + decimalformate.format(zhanbi) );
		}
		
		setUserSelectedColumnMessage (doc.toString());
	}
	/*
	 * �����û�ѡ���node�ĵ�����Ϣ��CSV
	 */
	protected void exportUserSelectedNodeInfoToCsv(Multimap<String, LocalDate> nodespecificdatainfo)
	{
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
	}
	/**
	 * 
	 * @param nodelist
	 * @param exporttype
	 */
	private void exportTDXNodesDataToCsv (List<TDXNodes> nodelist, String exporttype)
	{
		btnexportcsv.setEnabled(true);
		
		LocalDate curselectdate = dateChooser.getLocalDate();//dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		
		for(TDXNodes tmpnode : nodelist) {
//			TDXNodes node ;
//			if(tmpnode.getType() == BkChanYeLianTreeNode.BKGEGU) {
//				node = (Stock) this.allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode(tmpnode.getMyOwnCode(), BkChanYeLianTreeNode.TDXGG);
//			} else {
//				node = tmpnode;
//				allbksks.syncBanKuaiAndItsStocksForSpecificTime ((BanKuai)node, 
//							CommonUtility.getSettingRangeDate(curselectdate,"basic"), curselectdate, globeperiod);
//			}
			
			

			if(exporttype.toLowerCase().equals("all")) { //list����ûһ����ǰ��������
				nodeinfotocsv.addNodeToCsvList(tmpnode, curselectdate, curselectdate);
			}
//			else if(exporttype.toLowerCase().equals("onlyyangxianbk") ) {
//				NodeXPeriodData nodexdata = node.getNodeXPeroidData(globeperiod);
//				Double zhangfu = nodexdata.getSpecificOHLCZhangDieFu(dateChooser.getLocalDate(), 0);
//				if(zhangfu <0)
//					nodeinfotocsv.addNodeToCsvList(node, curselectdate, curselectdate);
//			}
			else if(exporttype.toLowerCase().equals("single") ) {
				DateRangeSelectPnl datachoose = new DateRangeSelectPnl (52); 
				JOptionPane.showMessageDialog(null, datachoose,"Ϊ" + tmpnode.getMyOwnName() + "ѡ�񵼳�ʱ���", JOptionPane.OK_CANCEL_OPTION);
				LocalDate nodestart = datachoose.getDatachoosestart();
				LocalDate nodeend = datachoose.getDatachooseend();

				nodeinfotocsv.addNodeToCsvList(tmpnode, nodestart, nodeend);
				
				nodestart = null;
				nodeend = null;
				datachoose = null;
			} else { //�û��������Ϣ����
//				String date = exporttype.substring(0,6);
				nodeinfotocsv.addNodeToCsvList(tmpnode, LocalDate.parse(exporttype), LocalDate.parse(exporttype));
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
		
		JOptionPane.showMessageDialog(null, nodeinfotocsv,  "�������ݵ�CSV", JOptionPane.OK_CANCEL_OPTION);
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
	 * �@ʾ�û��x��Ă��ɵĻ�����Ϣ
	 */
	private void displayStockCurrentFxResult(BanKuaiGeGuBasicTable curtable) 
	{
		int row = curtable.getSelectedRow();
		if(row <0) {
			JOptionPane.showMessageDialog(null,"����ѡ��һ������","Warning",JOptionPane.WARNING_MESSAGE);
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
		NodeXPeriodData nodexdata = selectstock.getStock().getNodeXPeroidData(globeperiod);
		Double liutongshizhi = ((StockNodesXPeriodData)nodexdata).getSpecificTimeLiuTongShiZhi(curtableshowwknum, 0);
		Double zongshizhi = ((StockNodesXPeriodData)nodexdata).getSpecificTimeZongShiZhi(curtableshowwknum, 0);
		if(liutongshizhi != null  )
			result = result + "��ͨ��ֵ:" + liutongshizhi/100000000 + "��";
		if(zongshizhi != null)
			result = result + "����ֵ:" + zongshizhi/100000000 + "\n";

		result = null;
		
		//�@ʾ���ɵ���Ϣ
		editorPanenodeinfo.setClearContentsBeforeDisplayNewInfo (false);
		editorPanenodeinfo.displayNodeAllInfo(selectstock.getStock());
		editorPanenodeinfo.setClearContentsBeforeDisplayNewInfo (true);
	}
	/*
	 * �û�˫��ĳ��nodeռ��chart����Ŵ���ʾ��nodeһ���ڵ�ռ����������
	 */
	protected void displayNodeLargerPeriodData(TDXNodes node, LocalDate datekey) 
	{
		//��֤��ʾʱ�䷶ΧΪ��ǰ����ǰ�������ݵ�36����(3��)
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
		
		//û��3�����ݣ�����3��
		long numberOfMonth = ChronoUnit.MONTHS.between(overlapldstartday, overlapldendday);
		if(numberOfMonth <36);
			overlapldstartday = overlapldstartday.with(DayOfWeek.MONDAY).minus( (36-numberOfMonth) ,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);

			//ͬ������
		BanKuai bkcur = null;
		if(node.getType() == BkChanYeLianTreeNode.TDXBK) {
			node = this.allbksks.getBanKuai((BanKuai)node, overlapldstartday,overlapldendday,globeperiod);
			this.allbksks.syncBanKuaiData( (BanKuai)node );
		} else if(node.getType() == BkChanYeLianTreeNode.TDXGG) { 
			node = this.allbksks.getStock((Stock)node, overlapldstartday,overlapldendday,globeperiod);
			 this.allbksks.syncStockData( (Stock)node );
			//����Ǹ��ɵĻ�����Ҫ��ʾ�䵱ǰ�����İ��ռ����Ϣ������Ҫ�Ѱ�������Ҳ�ҳ�����
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
//			node = this.allbksks.getDaPan (requirestart.plusWeeks(1),globeperiod); //ͬ����������,�����������ط������
		}
			
		
		this.allbksks.getDaPan (overlapldstartday,overlapldendday,globeperiod); //ͬ����������,�����������ط������
		this.allbksks.syncDaPanData ();
//		this.allbksks.getDaPanKXian (requirestart.plusWeeks(1),curselectdate,NodeGivenPeriodDataItem.DAY); //ͬ����������,�����������ط������
		
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
		
//		long start=System.currentTimeMillis(); //��ȡ��ʼʱ��
		JOptionPane.showMessageDialog(null, largeinfo, node.getMyOwnCode()+node.getMyOwnName()+ "�����ڷ������", JOptionPane.OK_CANCEL_OPTION);
//		long end=System.currentTimeMillis(); //��ȡ����ʱ��
//		logger.debug("��������ʱ�䣺 "+(end-start)+"ms");
		
//		String userselectnodeinfo = largeinfo.getUserSelectedColumnMessage ();
//		if(userselectnodeinfo != null ){
//    		
////    		nodeinfotocsv.clearCsvDataSet();
//    		exportUserSelectedNodeInfoToCsv (userselectnodeinfo);
//		}
		
		largeinfo = null;
		System.gc();
	}
	/*
	 * ÿ�ܶ�Ҫ�������������������ã�һ�����ý�Լʱ��
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
	 * �û���������������
	 */
	protected ExportCondition initializeExportConditions () 
	{
		if( exportcond == null)
			exportcond = new ArrayList<ExportCondition> ();
		
		ExportCondition expc = this.setExportMainConditionBasedOnUserSelection (new ExportCondition () );

		ExtraExportConditions extraexportcondition = expc.getExtraExportConditions();
//		extraexportcondition = new ExtraExportConditions ();
		
		int extraresult = JOptionPane.showConfirmDialog(null,extraexportcondition , "���ӵ�������:", JOptionPane.OK_CANCEL_OPTION);
		if(extraresult == JOptionPane.OK_OPTION) { //������������ 
			
//		expc.setExportSTStocks ( extraexportcondition.shouldExportSTStocks() );
			
//			if(ckboxshowcje.isSelected() )
//				expc.setChenJiaoEr(exportcjelevel, exportcjelevelmax);
			
			if(expc.shouldOnlyExportCurrentBankuai() ) {
				int row = tableBkZhanBi.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"��ѡ��һ����飡","Warning",JOptionPane.WARNING_MESSAGE);
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
	 * ͬʱ���� ������ʾ��ѡ�� 
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
	 * �û�������������buttion ��toolstip��ʾ�û����õ�����
	 */
	private void decrorateExportButton(ExportCondition expc) 
	{
				//��tooltips��ʾ��ǰ�м����Ѿ���ӵ�����������
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
		//��ѡ���ļ�
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
		
		if(!filename.endsWith("EBK") && !filename.endsWith("XML")) { //���ǰ���ļ�
			tfldparsedfile.setText("�ļ�����������ѡ���ļ���");
			ckboxparsefile.setSelected(false);
			return;
		}
//			((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).setShowParsedFile(true);
			
			//�ҵ���Ӧ��XML
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
						tfldparsedfile.setText("XML�ļ�λ�ô�������ָ����Ŀ¼������ѡ���ļ���");
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
					 int exchangeresult = JOptionPane.showConfirmDialog(null,"�ļ�ָ��������" + localDate + "���Ƿ���ĵ������ڣ�", "�Ƿ�������ڣ�", JOptionPane.OK_CANCEL_OPTION);
					 if(exchangeresult != JOptionPane.CANCEL_OPTION) {
						 	LocalDate curselectdate = dateChooser.getLocalDate();
							if(!curselectdate.equals(localDate) ) {
								ZoneId zone = ZoneId.systemDefault();
								Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
								this.dateChooser.setDate(Date.from(instant));
							}
					 } else { //�û�������Ϊ�����ļ�
						 localDate = dateChooser.getLocalDate();
					 }
			} catch (java.time.format.DateTimeParseException e) {
					tfldparsedfile.setText("�ļ���δ�������ڣ���Ϊ�����ļ�����");
					localDate = dateChooser.getLocalDate();
			}
			
			
			ckboxparsefile.setSelected(true);
			tfldparsedfile.setText(filename);
			
			if(xmlfileexist) {
				this.bkcyl.patchWeeklyBanKuaiFengXiXmlFileToCylTree (filexminconfigpath,localDate);
			} else { //������Ҫ����
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
	 * �ڸ��ɱ��з�������ĸ���
	 */
	protected boolean findInputedNodeInTable(String nodecode) 
	{
		Boolean notfound = false; int rowindex;
		nodecode = nodecode.substring(0,6);
		if(((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel() ).getRowCount() > 0) {
			 rowindex = ((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel() ).getStockRowIndex(nodecode);
			 
			if(rowindex <0) {
				notfound = true;
			} else {
//				int modelRow = tableGuGuZhanBiInBk.convertRowIndexToView(rowindex);
				int modelRow = rowindex;
				int curselectrow = tableGuGuZhanBiInBk.getSelectedRow();
				if( curselectrow != modelRow) {
					tableGuGuZhanBiInBk.setRowSelectionInterval(modelRow, modelRow);
					tableGuGuZhanBiInBk.scrollRectToVisible(new Rectangle(tableGuGuZhanBiInBk.getCellRect(modelRow, 0, true)));
					
					//�ڵ�ǰ����еĻ����Ͱ����PANEL���
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
//				int modelRow = tablexuandingzhou.convertRowIndexToView(rowindex);
				int modelRow = rowindex;
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
//				int modelRow = tablexuandingminusone.convertRowIndexToView(rowindex);
				int modelRow = rowindex;
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
//				int modelRow = tablexuandingminustwo.convertRowIndexToView(rowindex);
				int modelRow = rowindex;
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
//				int modelRow = tablexuandingplusone.convertRowIndexToView(rowindex);
				int modelRow = rowindex;
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
//				int modelRow = tableExternalInfo.convertRowIndexToView(rowindex);
				int modelRow = rowindex;
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
	 * �����
	 */
	private void initializePaoMaDeng() 
	{
		// TODO Auto-generated method stub
		String title = "���ռƻ�:";
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
	 * �ڱ��ܸ�����Ʊռ�ȵ�D�@ʾ�x�еĹ�Ʊ
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

	private BanKuaiFengXiPieChartCjePnl pnllastestggzhanbi;
	private BanKuaiFengXiPieChartCjePnl panelLastWkGeGuZhanBi;
	
	private BanKuaiFengXiCandlestickPnl paneldayCandle;
	private BanKuaiFengXiPieChartCjlPnl pnllastestggcjlzhanbi;
	
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
	private PaoMaDeng2 pnl_paomd;
	private JTabbedPane tabbedPanebk;
	private JButton btnexportcsv;
	private Action exportCancelAction;
	private Action bkfxCancelAction;
	private JCheckBox chkliutongsz;
	private JTextField tfldltszmin;
	private JTextField tfldcjemaxwk;
	private JCheckBox ckbxcjemaxwk;
	
	private JButton btnaddexportcond;
	private JCheckBox ckbxma;
	private JTextField tfldhuanshoulv;
	private JCheckBox ckbxhuanshoulv;
	private BanKuaiAndStockTree cyltreecopy;
	private JMenuItem menuItemRmvNodeFmFile;
	
	private JTextField tfldshowcjemax;
	
	private JPopupMenu jPopupMenuoftabbedpane;
	private JMenuItem menuItemliutong ; //ϵͳĬ�ϰ���ͨ��ֵ����
	private JMenuItem menuItemzongshizhi ;
	private JMenuItem menuItemchengjiaoer ;
//	private JMenuItem menuItemstocktocsv ;
	
//	private JPopupMenu jPopupMenuoftabbedpanebk;
//	private JMenuItem menuItembktocsv ; //ϵͳĬ�ϰ���ͨ��ֵ����
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
		
		JPanel panel_2 = new JPanel();
		
		JScrollPane scrollPanestockbk = new JScrollPane();
		
		editorPanebankuai = new BanKuaiListEditorPane();
		scrollPanestockbk.setViewportView(editorPanebankuai);
		
		paneldayCandle = new BanKuaiFengXiCandlestickPnl();
//		paneldayCandle.setBorder(new TitledBorder(null, "\u677F\u5757/\u4E2A\u80A1K\u7EBF\u8D70\u52BF", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		
		JScrollPane scrollPane_3 = new JScrollPane();
		
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
						.addComponent(paneldayCandle, GroupLayout.DEFAULT_SIZE, 1107, Short.MAX_VALUE)
						.addComponent(panel_2, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 1107, Short.MAX_VALUE)
						.addComponent(scrollPanestockbk, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 1107, Short.MAX_VALUE))
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(14)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 380, GroupLayout.PREFERRED_SIZE)
								.addComponent(panelLastWkGeGuZhanBi, 0, 0, Short.MAX_VALUE)))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(18)
							.addComponent(scrollPane_3, GroupLayout.PREFERRED_SIZE, 375, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(paneldayCandle, GroupLayout.PREFERRED_SIZE, 327, GroupLayout.PREFERRED_SIZE)
							.addContainerGap())
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addComponent(panel, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE)
									.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
										.addGroup(gl_contentPanel.createSequentialGroup()
											.addGap(237)
											.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 532, GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(scrollPanestockbk, GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE)
											.addPreferredGap(ComponentPlacement.RELATED))
										.addGroup(gl_contentPanel.createSequentialGroup()
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 831, GroupLayout.PREFERRED_SIZE))))
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 311, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(panelLastWkGeGuZhanBi, GroupLayout.PREFERRED_SIZE, 289, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(scrollPane_3, GroupLayout.PREFERRED_SIZE, 303, GroupLayout.PREFERRED_SIZE)))
							.addGap(34))))
		);
		
		tfldselectedmsg = new BanKuaiListEditorPane ();
		scrollPane_3.setViewportView(tfldselectedmsg);
		tfldselectedmsg.setEditable(false);
		
		pnllastestggzhanbi = new BanKuaiFengXiPieChartCjePnl(0);
		tabbedPane.addTab("\u677F\u5757\u5F53\u5468\u6210\u4EA4\u989D\u4E2A\u80A1\u5360\u6BD4", null, pnllastestggzhanbi, null);
		pnllastestggzhanbi.setBorder(new TitledBorder(null, "\u677F\u5757\u5F53\u524D\u5468\u4E2A\u80A1\u5360\u6BD4", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		pnllastestggcjlzhanbi = new BanKuaiFengXiPieChartCjlPnl();
		pnllastestggcjlzhanbi.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		tabbedPane.addTab("\u677F\u5757\u5F53\u5468\u6210\u4EA4\u91CF\u4E2A\u80A1\u5360\u6BD4", null, pnllastestggcjlzhanbi, null);
		
		tabbedPanegeguzhanbi = new JTabbedPane(JTabbedPane.TOP);
		
		panelGgDpCjeZhanBi = new BanKuaiFengXiNodeCombinedCategoryPnl("CJE");
		panelGgDpCjeZhanBi.setAllowDrawAnnoation(false);
		panelGgDpCjeZhanBi.setDrawQueKouLine(false);
		panelGgDpCjeZhanBi.setDrawZhangDieTingLine(false);
		tabbedPanegeguzhanbi.addTab("\u4E2A\u80A1\u989D\u5360\u6BD4", null, panelGgDpCjeZhanBi, null);
		
		panelggdpcjlwkzhanbi = new BanKuaiFengXiNodeCombinedCategoryPnl("CJL");
		
		tabbedPanegeguzhanbi.addTab("\u4E2A\u80A1\u91CF\u5360\u6BD4", null, panelggdpcjlwkzhanbi, null);
		
		tabbedPanebkzb = new JTabbedPane(JTabbedPane.TOP);
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panel_2.createSequentialGroup()
					.addContainerGap()
					.addComponent(tabbedPanebkzb, GroupLayout.PREFERRED_SIZE, 557, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(tabbedPanegeguzhanbi, GroupLayout.PREFERRED_SIZE, 533, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		gl_panel_2.setVerticalGroup(
			gl_panel_2.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addGroup(gl_panel_2.createParallelGroup(Alignment.TRAILING)
						.addComponent(tabbedPanebkzb, GroupLayout.DEFAULT_SIZE, 522, Short.MAX_VALUE)
						.addComponent(tabbedPanegeguzhanbi, GroupLayout.DEFAULT_SIZE, 522, Short.MAX_VALUE))
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
		
		
		exportCancelAction = new AbstractAction("������������") {

		      private static final long serialVersionUID = 4669650683189592364L;

		      @Override
		      public void actionPerformed(final ActionEvent e) {
		    	
		        if (exporttask == null) { 
		        	exportBanKuaiWithGeGuOnCondition2();
//		        	String msg =  "���������������ø��ɻ��ǵ�����ǰ���ø��ɣ�ѡ��ȷ���������������������ø��ɡ�";
//			  		int exchangeresult = JOptionPane.showConfirmDialog(null,msg , "����ѡ��", JOptionPane.OK_CANCEL_OPTION);
//			  		if(exchangeresult == JOptionPane.CANCEL_OPTION) //�û�ѡ�񵼳���ǰ���� 
//			  			exportBanKuaiWithGeGuOnCondition2();
//			  		else { //�û�ѡ�񵼳���������
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
		tabbedPanegegu.addTab("��ǰ��", null, scrollPanedangqian, null);
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
		tabbedPanegegu.addTab("ѡ����", null, scrollPanexuanding, null);
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
		
		bkfxCancelAction = new AbstractAction("����") {

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
		        		
		        	}
		        } else { //������������ʱ������û�����ʱ�䣬��ȡ��������������ʵ�о�Ҳû����Ҫ
		        	bkfxtask.cancel(true);
		        }
		      }
		    		
		 };
		
		btnresetdate = new JButton(bkfxCancelAction);
		btnresetdate.setText("\u4ECA\u5929");
		
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
							.addComponent(btnresetdate))
						.addGroup(gl_panel.createSequentialGroup()
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING, false)
								.addGroup(gl_panel.createSequentialGroup()
									.addComponent(label)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(lblcybcje, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addGroup(gl_panel.createSequentialGroup()
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
						.addComponent(btnresetdate, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
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
			
			ckbxma = new JCheckBox("\u7A81\u51FACLOSE vs. MA");
			ckbxma.setFont(new Font("����", Font.PLAIN, 12));
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
			ckboxparsefile.setFont(new Font("����", Font.ITALIC, 12));
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
			ckbxcjemaxwk.setFont(new Font("����", Font.PLAIN, 12));
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
//			        	String msg =  "���������������ø��ɻ��ǵ�����ǰ���ø��ɣ�ѡ��ȷ���������������������ø��ɡ�";
//				  		int exchangeresult = JOptionPane.showConfirmDialog(null,msg , "����ѡ��", JOptionPane.OK_CANCEL_OPTION);
//				  		if(exchangeresult == JOptionPane.CANCEL_OPTION) //�û�ѡ�񵼳���ǰ���� 
//				  			exportBanKuaiWithGeGuOnCondition2();
//				  		else { //�û�ѡ�񵼳���������
//				  			initializeNormalExportConditions ();
//				  			exportBanKuaiWithGeGuOnCondition2();
//				  		}
				  			
			        } else {
			        	exporttask.cancel(true);
			        }
				}
			});
//			progressBarExport.setValue(0);
			progressBarExport.setString("���������������");
			progressBarExport.setToolTipText("���������������");
	        progressBarExport.setStringPainted(true);
			
			tfldltszmax = new JTextField();
			tfldltszmax.setText("300");
			tfldltszmax.setForeground(Color.MAGENTA);
			tfldltszmax.setColumns(10);
			
			chbxquekou = new JCheckBox("\u7A81\u51FA\u56DE\u8865\u4E0B\u8DF3/\u4E0A\u8DF3");
			chbxquekou.setToolTipText("\u7A81\u51FA\u56DE\u8865\u4E0B\u8DF3/\u4E0A\u8DF3");
			chbxquekou.setFont(new Font("����", Font.ITALIC, 12));
			chbxquekou.setSelected(true);
			
			chbxquekou.setForeground(Color.PINK);
			
			chckbxdpminwk = new JCheckBox("\u7A81\u51FADPMINWK>=");
			chckbxdpminwk.setToolTipText("\u7A81\u51FADPMINWK>=");
			chckbxdpminwk.setFont(new Font("����", Font.ITALIC, 12));
			chckbxdpminwk.setSelected(true);
			chckbxdpminwk.setForeground(Color.GREEN);
			
			tflddpminwk = new JTextField();
			tflddpminwk.setText("8");
			tflddpminwk.setColumns(10);
			
			GroupLayout gl_buttonPane = new GroupLayout(buttonPane);
			gl_buttonPane.setHorizontalGroup(
				gl_buttonPane.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_buttonPane.createSequentialGroup()
						.addContainerGap()
						.addComponent(btnaddexportcond, GroupLayout.PREFERRED_SIZE, 62, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(progressBarExport, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
						.addGap(56)
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
								.addContainerGap())))
			);
			buttonPane.setLayout(gl_buttonPane);
		}
		
		reFormatGui ();
	}
	/*
	 * ���ɱ���İ��ռ��MAX��ʱ���ã�������
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
		menuItemliutong = new JMenuItem(" ����ͨ��ֵ����"); 
		menuItemzongshizhi = new JMenuItem("������ֵ����");
		menuItemchengjiaoer = new JMenuItem("X ���ɽ�������"); //ϵͳĬ�ϰ��ɽ�������
		menuItemtimerangezhangfu = new JMenuItem("���׶��Ƿ�����"); //ϵͳĬ�ϰ��ɽ�������
//		menuItemstocktocsv = new JMenuItem("�������и��ɵ�CSV");
//      
		jPopupMenuoftabbedpane.add(menuItemzongshizhi);
       menuItemzongshizhi.setEnabled(false);
       jPopupMenuoftabbedpane.add(menuItemliutong);
       jPopupMenuoftabbedpane.add(menuItemchengjiaoer);
       jPopupMenuoftabbedpane.add(menuItemtimerangezhangfu);
//       jPopupMenuoftabbedpane.add(menuItemstocktocsv);
       
//       jPopupMenuoftabbedpanebk = new JPopupMenu();
//       menuItembktocsv = new JMenuItem("�������а�鵽CSV");
//       jPopupMenuoftabbedpanebk.add(menuItembktocsv);
       
//       menuItemyangxianbktocsv = new JMenuItem("�����������߰�鵽CSV");
//       jPopupMenuoftabbedpanebk.add(menuItemyangxianbktocsv);
       
       menuItemsiglebktocsv = new JMenuItem("������鵽CSV");
       menuItemQiangShibk = new JMenuItem("��Ϊǿ�ư��");
	   menuItemRuoShibk = new JMenuItem("��Ϊ���ư��");
	   tableBkZhanBi.getPopupMenu().add(menuItemQiangShibk);
	   tableBkZhanBi.getPopupMenu().add(menuItemRuoShibk);
	   tableBkZhanBi.getPopupMenu().add(menuItemsiglebktocsv);
       
//       menuItemRmvNodeFmFile = new JMenuItem("�޳���ģ���ļ�") ;
//       menuItemRmvNodeFmFile.setEnabled(false);
//       tableGuGuZhanBiInBk.getPopupMenu().add(menuItemRmvNodeFmFile);
		
	   menuItemQiangShigg = new JMenuItem("��Ϊǿ�Ƹ���");
	   menuItemRuoShigg = new JMenuItem("��Ϊ���Ƹ���");
	   tableGuGuZhanBiInBk.getPopupMenu().add(menuItemQiangShigg);
	   tableGuGuZhanBiInBk.getPopupMenu().add(menuItemRuoShigg);
	   
       menuItemsiglestocktocsv = new JMenuItem("�������ɵ�CSV");
       tableGuGuZhanBiInBk.getPopupMenu().add(menuItemsiglestocktocsv);
       
       menuItemnonfixperiod = new JMenuItem("��������DPM??WK");
       tableGuGuZhanBiInBk.getPopupMenu().add(menuItemnonfixperiod);
       
       menuItemnonshowselectbkinfo = new JMenuItem("ͬʱ����ѡ���ܷ�������");
       panelbkwkcjezhanbi.addMenuItem (menuItemnonshowselectbkinfo,null);
       
       
       
	}
	/*
	 * �������ڼ��Ѿ����ܼ����������Զ����óɽ�����ֵ
	 */
//	private void setHighLightChenJiaoEr() 
//	{
//		LocalDate curselectdate = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//		if(LocalDate.now().with(DayOfWeek.FRIDAY).equals(curselectdate.with(DayOfWeek.FRIDAY) ) ) { //˵���ڵ�ǰ�ܣ�������������
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
//		} else { //Ӧ�ø��ݸ����ж��ٸ������������ã������Ǽ򵥵İ�1.2*5 Լ=5.8
//			int tradingdays = this.bkdbopt.getTradingDaysOfTheWeek (curselectdate);
//			if(tradingdays < 5)
//				tfldshowcje.setText(String.valueOf(1.15*tradingdays).substring(0, 3) );
//			else
//				tfldshowcje.setText("5.8");
//		}
//		
//	}
	
		/*
	 * 锟斤拷锟斤拷锟斤拷锟缴猴拷时太锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷�锟斤拷锟竭筹拷专锟脚达拷锟斤拷锟斤拷锟斤拷锟斤拷GUI锟睫凤拷使锟斤拷
	 * http://www.javacreed.com/swing-worker-example/
	 * https://github.com/javacreed/swing-worker-example/blob/master/src/main/java/com/javacreed/examples/swing/worker/part3/Application.java
	 */
	class ExportTask2 extends SwingWorker<Integer, String>  
	{
		private LocalDate selectiondate;
		private File outputfile;
		private BanKuaiAndStockTree bkcyltree;
		private ArrayList<ExportCondition> expclist;
		private String period;
		 

		public ExportTask2 (AllCurrentTdxBKAndStoksTree allbksks, ArrayList<ExportCondition> exportcond,LocalDate selectiondate,String period,File outputfile)
		{
			this.bkcyltree = allbksks.getAllBkStocksTree();
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
				
				setProgress(50);

			}	
			
			//�Ե����İ��͸��ɰ����ܳɽ�����������������뵽ͨ���ź����Զ����ճɽ�������
			Collections.sort(outputnodeslist, new NodeChenJiaoErComparator(selectiondate,0,period) );
			
			try {
				Files.append("<��������:" + selectiondate.toString() + ">"+ System.getProperty("line.separator") ,outputfile, charset);
				Files.append("<��������:" + period + ">"+ System.getProperty("line.separator") ,outputfile, charset);
				Files.append("<" + ouputfilehead2 + ">" + System.getProperty("line.separator") ,outputfile, charset);
			} catch (IOException e1) {
				e1.printStackTrace();
			} 
			
			//д��������ļ�
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
		
			//����gephi�ļ�
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
	
	/*
	 * ��������ʱ̫����������һ���߳�ר�Ŵ���������GUI�޷�ʹ��
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
				
				if( ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL) ||  ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL)  ) //��Щָ����û�и��ɺͳɽ����ģ�������ȽϷ�Χ
					continue;
				
				if( !((BanKuai)childnode).isShowinbkfxgui() )
					continue;
				
				String bkcode = childnode.getMyOwnCode();
//				LocalDate requirestart = curselectdate.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange() + 3,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
				LocalDate requirestart = CommonUtility.getSettingRangeDate(curselectdate,"middle").with(DayOfWeek.MONDAY);
				childnode = (BanKuai)this.allbksks.getBanKuai(((BanKuai)childnode), requirestart,curselectdate,period); 
				NodeXPeriodData bkxdata = ((BanKuai)childnode).getNodeXPeroidData(period);
	    		if(bkxdata.getIndexOfSpecificDateOHLCData(curselectdate, 0) != null ) {//��鵱��û������Ҳ�����ǣ����һ�㲻����û�����ݣ�û������˵���ð�����ܻ�û�е��������߹�ȥ�У����ڳɽ����Ѿ����������ݿ�
	    			((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).addBanKuai(((BanKuai)childnode));
	    		}
	    		
	    		//��ʾ���̳ɽ���
	    		
	    		if(bkcode.equals("999999") ) {
	    			 NodeXPeriodData bkshdata = ((BanKuai)childnode).getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
	    			try {
		    			Double cjerec = bkshdata.getChengJiaoEr(curselectdate,0);
	    				lblshcje.setText( cjerec.toString() );
	    			} catch (java.lang.NullPointerException e) {
	    				lblshcje.setText( "����û�гɽ���" );
	    			}
	    			
	    		} else if(bkcode.equals("399001") ) {
	    			NodeXPeriodData bkszdata = ((BanKuai)childnode).getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
	    			try {
	    				Double cjerec = bkszdata.getChengJiaoEr(curselectdate,0);
	    				lblszcje.setText( cjerec.toString() );
	    			} catch (java.lang.NullPointerException e) {
	    				lblszcje.setText( "����û�гɽ���" );
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

