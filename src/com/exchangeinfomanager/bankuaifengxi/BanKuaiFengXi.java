package com.exchangeinfomanager.bankuaifengxi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.regex.Pattern;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.TimeSeries;
import org.jfree.ui.RectangleEdge;
import org.jsoup.Jsoup;

import com.exchangeinfomanager.StockCalendar.JStockCalendarDateChooser;
import com.exchangeinfomanager.StockCalendar.StockCalendar;
import com.exchangeinfomanager.asinglestockinfo.AllCurrentTdxBKAndStoksTree;
import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BanKuai.BanKuaiTreeRelated;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTree;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.TreeRelated;
import com.exchangeinfomanager.asinglestockinfo.DaPan;
import com.exchangeinfomanager.asinglestockinfo.HanYuPinYing;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.asinglestockinfo.Stock.StockNodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.StockGivenPeriodDataItem;
import com.exchangeinfomanager.asinglestockinfo.StockOfBanKuai;
import com.exchangeinfomanager.asinglestockinfo.StockOfBanKuai.StockOfBanKuaiTreeRelated;
import com.exchangeinfomanager.bankuaichanyelian.BanKuaiAndChanYeLian2;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiGeGuTable;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiGeGuTableModel;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiInfoTable;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiInfoTableModel;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.DisplayBkGgInfoEditorPane;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.CacheListener;
import com.exchangeinfomanager.bankuaifengxi.BanKuaiFengXi.ExportCondition;
import com.exchangeinfomanager.bankuaifengxi.CandleStick.BanKuaiFengXiCandlestickPnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartCjePnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartCjeZhanbiPnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartCjlZhanbiPnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartPnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiNodeCombinedCategoryPnl;
import com.exchangeinfomanager.bankuaifengxi.Gephi.GephiFilesGenerator;
import com.exchangeinfomanager.bankuaifengxi.PieChart.BanKuaiFengXiPieChartCjePnl;
import com.exchangeinfomanager.bankuaifengxi.PieChart.BanKuaiFengXiPieChartCjlPnl;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.gui.subgui.BanKuaiListEditorPane;
import com.exchangeinfomanager.gui.subgui.JStockComboBox;
import com.exchangeinfomanager.gui.subgui.JiaRuJiHua;
import com.exchangeinfomanager.gui.subgui.PaoMaDeng2;
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

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ImageIcon;
import net.miginfocom.swing.MigLayout;

import javax.swing.JScrollPane;
import com.toedter.components.JLocaleChooser;
import com.toedter.calendar.JDateChooser;
import javax.swing.JCheckBox;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.beans.PropertyChangeEvent;
import javax.swing.JTable;
import javax.swing.JTextArea;

import java.awt.event.MouseAdapter;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import java.awt.GridLayout;
import javax.swing.border.MatteBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JTabbedPane;
import javax.swing.JComboBox;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.SystemColor;
import java.awt.Toolkit;

import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.SwingWorker.StateValue;
import javax.swing.JProgressBar;
import javax.swing.JTree;

public class BanKuaiFengXi extends JDialog {

	/**
	 * Create the dialog.
	 * @param bkcyl2 
	 */
	public BanKuaiFengXi(StockInfoManager stockmanager1, AllCurrentTdxBKAndStoksTree allbksks2, BanKuaiAndChanYeLian2 bkcyl2)
	{
		this.stockmanager = stockmanager1;
		this.allbksks = allbksks2;
		this.bkcyl = bkcyl2;
		this.sysconfig = SystemConfigration.getInstance();
		this.bkdbopt = new BanKuaiDbOperation ();
		
		initializeGui ();
		createEvents ();
		setUpChartDataListeners ();

		initializePaoMaDeng ();
		
		//每周日一是新的一周开始，因为还没有导入数据，会显示为没有数据，所以把时间调整到上一周六
		DayOfWeek dayofweek = LocalDate.now().getDayOfWeek();
		if(dayofweek.equals(DayOfWeek.SUNDAY) ) {
			 LocalDate saturday = LocalDate.now().minus(1,ChronoUnit.DAYS);
			 ZoneId zone = ZoneId.systemDefault();
			 Instant instant = saturday.atStartOfDay().atZone(zone).toInstant();
			 this.dateChooser.setDate(Date.from(instant));
			 
		} else if(dayofweek.equals(DayOfWeek.MONDAY) && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) <19 ) {
			LocalDate saturday = LocalDate.now().minus(2,ChronoUnit.DAYS);
			 ZoneId zone = ZoneId.systemDefault();
			 Instant instant = saturday.atStartOfDay().atZone(zone).toInstant();
			 this.dateChooser.setDate(Date.from(instant));
			 
		} else
			this.dateChooser.setDate(new Date ());
	}
	
	private LocalDate lastselecteddate;
	private AllCurrentTdxBKAndStoksTree allbksks;
	private BanKuaiAndChanYeLian2 bkcyl;
	private SystemConfigration sysconfig;
	private StockInfoManager stockmanager;
	private HashSet<String> stockinfile;
	private BanKuaiDbOperation bkdbopt;
	private static Logger logger = Logger.getLogger(BanKuaiFengXi.class);
	private ExportTask2 exporttask;
	private BanKuaiPaiXuTask bkfxtask;
	private ArrayList<ExportCondition> exportcond;
	private String globeperiod;
		
	private Set<BarChartPanelHightLightColumnListener> chartpanelhighlightlisteners;
	private Set<BarChartPanelDataChangedListener> barchartpanelbankuaidatachangelisteners;
	private Set<BarChartPanelDataChangedListener> piechartpanelbankuaidatachangelisteners;
	private Set<BarChartPanelDataChangedListener> barchartpanelstockofbankuaidatachangelisteners;
	private Set<BarChartPanelDataChangedListener> barchartpanelstockdatachangelisteners;
	private Set<BarChartHightLightFxDataValueListener> bkfxhighlightvalueslisteners;
	
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
		this.bkfxhighlightvalueslisteners = new HashSet<>();
		//用户点击板块，板块自身bar chart相关数据更新
		barchartpanelbankuaidatachangelisteners.add(panelbkcje);
		barchartpanelbankuaidatachangelisteners.add(panelbkwkzhanbi);
		//板块pie chart
		piechartpanelbankuaidatachangelisteners.add(pnllastestggzhanbi);
		piechartpanelbankuaidatachangelisteners.add(panelLastWkGeGuZhanBi);
		//个股对板块
//		barchartpanelstockofbankuaidatachangelisteners.add(panelgegucje); //个股对于板块交易额,一般看个股对大盘的成交额，这个就不看了
		barchartpanelstockofbankuaidatachangelisteners.add(panelggbkcjezhanbi);
		//独立个股本身
//		barchartpanelstockdatachangelisteners.add(paneldayCandle);
		barchartpanelstockdatachangelisteners.add(pnlggdpcje);
		barchartpanelstockdatachangelisteners.add(panelGgDpCjeZhanBi);
		//用户点击bar chart的一个column, highlight bar chart
		chartpanelhighlightlisteners.add(panelGgDpCjeZhanBi);
		chartpanelhighlightlisteners.add(panelgegubkcje);
		chartpanelhighlightlisteners.add(panelggbkcjezhanbi);
		chartpanelhighlightlisteners.add(panelbkcje);
		chartpanelhighlightlisteners.add(pnlggdpcje);
		chartpanelhighlightlisteners.add(panelbkwkzhanbi);
		chartpanelhighlightlisteners.add(paneldayCandle);
		
		//同步几个表要highlight的数据
		bkfxhighlightvalueslisteners.add(tableGuGuZhanBiInBk);
		bkfxhighlightvalueslisteners.add(tablexuandingzhou);
		bkfxhighlightvalueslisteners.add(tablexuandingminusone);
		bkfxhighlightvalueslisteners.add(tablexuandingminustwo);
		bkfxhighlightvalueslisteners.add(tablexuandingplusone);
		bkfxhighlightvalueslisteners.add(tablexuandingplustwo);
		
	}
	
	/*
	 * 所有板块占比增长率的排名
	 */
	private void initializeBanKuaiZhanBiByGrowthRate (String period)
	{
		this.globeperiod = period;
    	LocalDate curselectdate = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    	
    	BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)this.allbksks.getAllBkStocksTree().getModel().getRoot();
		int bankuaicount = allbksks.getAllBkStocksTree().getModel().getChildCount(treeroot);
        
		for(int i=0;i< bankuaicount; i++) {
			
			BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode) this.allbksks.getAllBkStocksTree().getModel().getChild(treeroot, i);
			if(childnode.getType() != BanKuaiAndStockBasic.TDXBK) 
				continue;
			
			if( ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL) ||  ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL)  ) //有些指数是没有个股和成交量的，不列入比较范围
				continue;
			
			if( !((BanKuai)childnode).isShowinbkfxgui() )
				continue;
			
			String bkcode = childnode.getMyOwnCode();
			logger.debug(bkcode);
			childnode = (BanKuai)this.allbksks.getBanKuai(((BanKuai)childnode), curselectdate,period); 
			BanKuai.BanKuaiNodeXPeriodData bkxdata = (BanKuai.BanKuaiNodeXPeriodData)childnode.getNodeXPeroidData(StockGivenPeriodDataItem.WEEK);
			if(bkxdata.hasRecordInThePeriod(curselectdate, 0) != null )//板块当周没有数据也不考虑，板块一般不可能没有数据，没有数据说明该板块这周还没有诞生，或者过去有，现在成交量已经不存入数据库
				((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).addBanKuai(((BanKuai)childnode));
    		
    		//显示大盘成交量
    		if(bkcode.equals("399001") ) {
    			BanKuai.BanKuaiNodeXPeriodData bkszdata = (BanKuai.BanKuaiNodeXPeriodData)childnode.getNodeXPeroidData(StockGivenPeriodDataItem.WEEK);
    			try {
    				Double cjerec = bkszdata.getChengJiaoEr(curselectdate,0);
    				lblszcje.setText( cjerec.toString() );
    			} catch (java.lang.NullPointerException e) {
    				lblszcje.setText( "本周没有数据" );
    			}
    			
    		} else if(bkcode.equals("999999") ) {
    			BanKuai.BanKuaiNodeXPeriodData bkshdata = (BanKuai.BanKuaiNodeXPeriodData)childnode.getNodeXPeroidData(StockGivenPeriodDataItem.WEEK);
    			try {
    				Double cjerec = bkshdata.getChengJiaoEr(curselectdate,0);
    				lblshcje.setText( cjerec.toString() );
    			} catch (java.lang.NullPointerException e) {
    				lblshcje.setText( "本周没有数据" );
    			}
    		} 
    			
		}
	
		((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).refresh(curselectdate,period,null);
		
		Toolkit.getDefaultToolkit().beep();
	}

	
	/*
	 * 所有板块占比增长率的排名
	 */
	private void initializeBanKuaiZhanBiByGrowthRate2 (String period)
	{
		this.globeperiod = period;
    	LocalDate curselectdate = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    	
    	bkfxtask = new BanKuaiPaiXuTask(this.allbksks,  curselectdate,period);
    	bkfxtask.addPropertyChangeListener(new PropertyChangeListener() {
		      @Override
		      public void propertyChange(final PropertyChangeEvent event) {
		        switch (event.getPropertyName()) {
		        case "progress":
		        	progressBarsys.setIndeterminate(true);
		        	progressBarsys.setString("正在进行板块分析计算..." + (Integer) event.getNewValue() + "%");
		          break;
		        case "state":
		          switch ((StateValue) event.getNewValue()) {
		          case DONE:
		        	bkfxCancelAction.putValue(Action.NAME, "重置");
		        	LocalDate cursettingdate = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		        	if(cursettingdate.equals(LocalDate.now())) 
		        		btnresetdate.setEnabled(false);
		        	
		        	dateChooser.setEnabled(true);
		        	btnsixmonthbefore.setEnabled(true);
		        	btnsixmonthafter.setEnabled(true);
		        	tableBkZhanBi.setEnabled(true);
		        	btnexportmodelgegu.setEnabled(true);
		        	tableGuGuZhanBiInBk.setEnabled(true);
		        	cbxggquanzhong.setEnabled(true);
		        	ckboxshowcje.setEnabled(true);
		        	cbxdpmaxwk.setEnabled(true);
		        	cbxbkmaxwk.setEnabled(true);
		        	chkcjemaxwk.setEnabled(true);
		        	ckboxparsefile.setEnabled(true);
		        	btnaddexportcond.setEnabled(true);
		        	btnClear.setEnabled(true);
		        	editorPanebankuai.setEnabled(true);
		        		
		            try {
		               int count = bkfxtask.get();
//		          	((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).refresh(bkhascjl,curselectdate,period);
		              progressBarsys.setIndeterminate(false);
		              progressBarsys.setString(" ");
		              
		              System.gc();
		            } catch (final CancellationException e) {
		            	progressBarsys.setIndeterminate(false);
		            	progressBarsys.setValue(0);
		            	progressBarsys.setString(" ");
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
		        	  btnexportmodelgegu.setEnabled(false);
		        	  tableGuGuZhanBiInBk.setEnabled(false);
		        	  cbxggquanzhong.setEnabled(false);
		        	  ckboxshowcje.setEnabled(false);
		        	  cbxdpmaxwk.setEnabled(false);
		        	  cbxbkmaxwk.setEnabled(false);
		        	  chkcjemaxwk.setEnabled(false);
		        	  ckboxparsefile.setEnabled(false);
		        	  btnaddexportcond.setEnabled(false);
		        	  btnClear.setEnabled(false);
		        	  editorPanebankuai.setEnabled(false);
		        	  btnresetdate.setEnabled(true);
		        	  
		          case PENDING:
		        	  bkfxCancelAction.putValue(Action.NAME, "中止板块分析");
		        	  progressBarsys.setVisible(true);
		        	  progressBarsys.setIndeterminate(true);
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
		LocalDate curselectdate = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		
		if(exportcond == null || exportcond.size() == 0) {
			if(!ckboxshowcje.isSelected() && !cbxdpmaxwk.isSelected() && !cbxbkmaxwk.isSelected() && !chkcjemaxwk.isSelected()){
				JOptionPane.showMessageDialog(null,"未设置导出条件，请先设置导出条件！");
				return;
			} else if(exportcond == null){
				exportcond = new ArrayList<ExportCondition> ();
				initializeExportConditions ();
			} else
				initializeExportConditions ();
		}

		String msg =  "导出耗时较长，请先确认条件是否正确。\n是否导出？";
		int exchangeresult = JOptionPane.showConfirmDialog(null,msg , "确实导出？", JOptionPane.OK_CANCEL_OPTION);
		if(exchangeresult == JOptionPane.CANCEL_OPTION)
				return;
		
		String dateshowinfilename = null;
		if(globeperiod == null  || globeperiod.equals(StockGivenPeriodDataItem.WEEK))
			dateshowinfilename = "week" + curselectdate.with(DayOfWeek.FRIDAY).toString().replaceAll("-","");
		else if(globeperiod.equals(StockGivenPeriodDataItem.DAY))
			dateshowinfilename = "day" + curselectdate.toString().replaceAll("-","");
		else if(globeperiod.equals(StockGivenPeriodDataItem.MONTH))
			dateshowinfilename = "month" +  curselectdate.withDayOfMonth(curselectdate.lengthOfMonth()).toString().replaceAll("-","");
		String exportfilename = sysconfig.getTDXModelMatchExportFile ()+ dateshowinfilename + ".EBK";
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

		exporttask = new ExportTask2(this.allbksks,  exportcond, curselectdate,globeperiod,filefmxx);
		exporttask.addPropertyChangeListener(new PropertyChangeListener() {
		      @Override
		      public void propertyChange(final PropertyChangeEvent eventexport) {
		        switch (eventexport.getPropertyName()) {
		        case "progress":
		        	progressBarExport.setIndeterminate(false);
		        	progressBarExport.setString("正在导出..." + (Integer) eventexport.getNewValue() + "%");
		          break;
		        case "state":
		          switch ((StateValue) eventexport.getNewValue()) {
		          case DONE:
		        	exportCancelAction.putValue(Action.NAME, "导出条件个股");
		            try {
		              final int count = exporttask.get();
	  			
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
						} catch (InterruptedException | ExecutionException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
		            	progressBarExport.setIndeterminate(false);
		            	progressBarExport.setValue(0);
		            	JOptionPane.showMessageDialog(null, "导出条件个股被终止！", "导出条件个股",JOptionPane.WARNING_MESSAGE);
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
	 * 显示板块的占比和个股
	 */
	protected void refreshCurentBanKuaiFengXiResult(BanKuai selectedbk,String period) 
	{
		tfldselectedmsg.setText("");
		tabbedPanegegu.setSelectedIndex(0);
		panelbkcje.resetDate();
		panelbkwkzhanbi.resetDate();
		pnllastestggzhanbi.resetDate();
		panelbkwkzhanbi.resetDate();
		panelggbkcjezhanbi.resetDate();
		panelselectwkgeguzhanbi.resetDate();
		panelLastWkGeGuZhanBi.resetDate();
		panelGgDpCjeZhanBi.resetDate();
		panelgegubkcje.resetDate();
		tabbedPanegegu.setTitleAt(1, "选定周");
		tfldselectedmsg.setText("");
		tabbedPanebk.setSelectedIndex(0);
		paneldayCandle.resetDate();
		pnlggdpcje.resetDate();
		paneldayCandle.resetDate();
		
		((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).deleteAllRows();
		((BanKuaiGeGuTableModel)tablexuandingzhou.getModel()).deleteAllRows();
		((BanKuaiGeGuTableModel)tablexuandingminusone.getModel()).deleteAllRows();
		((BanKuaiGeGuTableModel)tablexuandingminustwo.getModel()).deleteAllRows();
		((BanKuaiGeGuTableModel)tablexuandingplusone.getModel()).deleteAllRows();
		((BanKuaiGeGuTableModel)tablexuandingplustwo.getModel()).deleteAllRows();
		
		LocalDate curselectdate = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		//板块自身占比

		for(BarChartPanelDataChangedListener tmplistener : barchartpanelbankuaidatachangelisteners) {
			tmplistener.updatedDate(selectedbk, curselectdate, 0,globeperiod);
		}
		
		//更新板块所属个股
		if(selectedbk.getBanKuaiLeiXing().equals(BanKuai.HASGGWITHSELFCJL)) { //有个股才需要更新，有些板块是没有个股的
			
			selectedbk = allbksks.getAllGeGuOfBanKuai (selectedbk,period);
			
			((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).refresh(selectedbk, curselectdate,period);
			
			//更改显示日期
			tabbedPanegegu.setTitleAt(0, "当前周" + curselectdate);
			
			//显示2周的板块个股pie chart
//			piechartpanelbankuaidatachangelisteners.forEach(l -> l.updatedDate(selectbk, curselectdate, 0,globeperiod));
			for(BarChartPanelDataChangedListener tmplistener : piechartpanelbankuaidatachangelisteners) {
				tmplistener.updatedDate(selectedbk, curselectdate, 0,globeperiod);
			}
		}
		
		System.gc();
	}
	/*
	 * 设置成交量或者占比的提示
	 */
	private void setDisplayNoticeLevelForPanels() 
	{
		panelbkwkzhanbi.setDaZiJinValueMarker (0.01); //大于1%是板块强势的表现
		panelgegubkcje.setDaZiJinValueMarker ( 580000000); //5.8亿
		panelgegubkcje.setDaZiJinValueMarker (1000000000);
		panelgegubkcje.setDaZiJinValueMarker (1500000000);
		panelgegubkcje.setDaZiJinValueMarker (2000000000);
		
	}

	/*
	 * 几个表显示用户在选择周个股占比增长率排名等
	 */
	private void refreshSpecificBanKuaiFengXiResult (BanKuai selectedbk, LocalDate selecteddate,String period)
	{
		selectedbk = allbksks.getAllGeGuOfBanKuai (selectedbk,period);
//		ArrayList<StockOfBanKuai> allbkge = selectedbk.getSpecificPeriodBanKuaiGeGu(selecteddate,period);
//		HashSet<String> stockinparsefile = selectedbk.getNodetreerelated().getParseFileStockSet ();
		
		//显示选定周股票排名情况
		((BanKuaiGeGuTableModel)tablexuandingzhou.getModel()).refresh(selectedbk, selecteddate,period);
		tabbedPanegegu.setTitleAt(1, "选定周" + selecteddate);
		
		//显示选定周-1股票排名情况
		LocalDate selectdate2 = selecteddate.minus(1,ChronoUnit.WEEKS).with(DayOfWeek.MONDAY);
//		if( checkDateBetweenBanKuaiDate (selectedbk,selectdate2) ) //閿熸枻鎷烽敓鎻亷鎷锋噲閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓锟
			((BanKuaiGeGuTableModel)tablexuandingminusone.getModel()).refresh(selectedbk, selectdate2,period);
//		else
//			((BanKuaiGeGuTableModel)tablexuandingminusone.getModel()).deleteAllRows();
		
		//显示选定周-2股票排名情况
		LocalDate selectdate3 = selecteddate.minus(2,ChronoUnit.WEEKS).with(DayOfWeek.MONDAY);
//		if( checkDateBetweenBanKuaiDate (selectedbk,selectdate3) )
			((BanKuaiGeGuTableModel)tablexuandingminustwo.getModel()).refresh(selectedbk, selectdate3,period);
//		else 
//			((BanKuaiGeGuTableModel)tablexuandingminustwo.getModel()).deleteAllRows();
		
		//显示选定周+1股票排名情况
		LocalDate selectdate4 = selecteddate.plus(1,ChronoUnit.WEEKS).with(DayOfWeek.MONDAY);
//		if( checkDateBetweenBanKuaiDate (selectedbk,selectdate4) )
			((BanKuaiGeGuTableModel)tablexuandingplusone.getModel()).refresh(selectedbk, selectdate4,period);
//		else
//			((BanKuaiGeGuTableModel)tablexuandingplusone.getModel()).deleteAllRows();
		
		//显示选定周+2股票排名情况
		LocalDate selectdate5 = selecteddate.plus(2,ChronoUnit.WEEKS).with(DayOfWeek.MONDAY);
//		if( checkDateBetweenBanKuaiDate (selectedbk,selectdate5) )
			((BanKuaiGeGuTableModel)tablexuandingplustwo.getModel()).refresh(selectedbk, selectdate5,period);
//		else
//			((BanKuaiGeGuTableModel)tablexuandingplustwo.getModel()).deleteAllRows();
	}
//	/*
//	 * 
//	 */
//	private boolean checkDateBetweenBanKuaiDate(BanKuai selectedbk, LocalDate selectdate) 
//	{
//		LocalDate bkend = selectedbk.getWkRecordsEndDate();
//		LocalDate bkstart = selectedbk.getWkRecordsStartDate();
//		
//		if(  CommonUtility.isInSameWeek(bkstart,selectdate) ||  CommonUtility.isInSameWeek(bkend,selectdate)     
//				|| (selectdate.isAfter(bkstart) && selectdate.isBefore(bkend)) ) 
//			return true;
//		else
//			return false;
//	}
	/*
	 * 显示个股相对板块的占比的chart
	 */
	private void refreshGeGuFengXiResult (StockOfBanKuai stock)
	{
			LocalDate curselectdate = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//			DaPan dapan =  (DaPan) allbksks.getBkChanYeLianTree().getSpecificNodeByHypyOrCode("000000");
			
			panelggbkcjezhanbi.resetDate();
			panelGgDpCjeZhanBi.resetDate();
			panelgegubkcje.resetDate();
			panelggdpcjlwkzhanbi.resetDate();
			tabbedPanegeguzhanbi.setSelectedIndex(0);
//			tfldselectedmsg.setText("");
			tabbedPanebk.setSelectedIndex(1);
			
			BanKuai bkcur;
			int row = tableBkZhanBi.getSelectedRow();
			if(row != -1) {
				int modelRow = tableBkZhanBi.convertRowIndexToModel(row);
				bkcur = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(modelRow);
			}

			barchartpanelstockofbankuaidatachangelisteners.forEach(l -> l.updatedDate(stock, curselectdate, 0,globeperiod));
		
//			Comparable datekey = panelbkwkzhanbi.getCurSelectedBarDate ();
//			if(datekey != null) 
//				chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(datekey));

	}
	/*
	 * 用户选择个股后，显示个股日线K线走势已经个股自身对大盘的各种占比
	 */
	private void refreshGeGuKXianZouShi (Stock selectstock2)
	{
		LocalDate curselectdate = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate requireend = curselectdate.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = curselectdate.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange(),ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		
		selectstock2 = allbksks.getStock(selectstock2,curselectdate,StockGivenPeriodDataItem.WEEK);
		//日线K线走势，目前K线走势和成交量在日线和日线以上周期是分开的，所以调用时候要特别小心，以后会合并
		selectstock2 = allbksks.getStockK(selectstock2,curselectdate,StockGivenPeriodDataItem.DAY);
		
		for (BarChartPanelDataChangedListener tmplistener : barchartpanelstockdatachangelisteners) {
			tmplistener.updatedDate(selectstock2, curselectdate, 0,globeperiod);
		}

		paneldayCandle.resetDate();
		paneldayCandle.updatedDate(selectstock2,requirestart,requireend,StockGivenPeriodDataItem.DAY);
		paneldayCandle.displayRangeHighLowValue(true);
	}
	/*
	 * 
	 */
	private void displayStockSuoShuBanKuai(Stock selectstock) 
	{
//		 selectstock = bkdbopt.getTDXBanKuaiForAStock (selectstock); //通达信板块信息
		 HashMap<String, String> suosusysbankuai = ((Stock)selectstock).getGeGuCurSuoShuTDXSysBanKuaiList();
		 Set<String> union =  suosusysbankuai.keySet();
		 Multimap<String,String> suoshudaleibank = bkcyl.checkBanKuaiSuoSuTwelveDaLei (  union ); //获得板块是否属于12个大类板块
		 
		 String htmlstring = "<html> "
		 		+ "<body>"
		 		+ " <p>所属板块:";
	     for(String suoshubankcode : union ) {
	    	 Collection<String> daleilist = suoshudaleibank.get(suoshubankcode);
	    	 String displayedbkformate = "\"" + suoshubankcode + suosusysbankuai.get(suoshubankcode) + "\"";
	    	 if(daleilist.size()>0)
	    			htmlstring  +=   "<a style=\"color:red\" href=\"openBanKuaiAndChanYeLianDialog\">  " + displayedbkformate + daleilist.toString()  + "</a> ";
	    		else
	    			htmlstring  += "<a href=\"openBanKuaiAndChanYeLianDialog\"> " + displayedbkformate + "</a> ";
	     } 
	     htmlstring += "</p>";
	     		
	     htmlstring += "</body>"
					+ "</html>";
	    	 
	     editorPanebankuai.setText(htmlstring);
	     editorPanebankuai.setCaretPosition(0);
	     
	}
	/*
	 * 显示用户点击bar column后应该提示的信息
	 */
	private void setUserSelectedColumnMessage(String selttooltips,ArrayList<JiaRuJiHua> fxjg) 
	{
		String allstring = selttooltips + "\n";
		if(fxjg !=null) {
			for(JiaRuJiHua jrjh : fxjg) {
				LocalDate actiondate = jrjh.getJiaRuDate();
				String actiontype = jrjh.getGuanZhuType();
				String shuoming = jrjh.getJiHuaShuoMing();
				
				allstring = allstring +  "[" + actiondate.toString() + actiontype +  " " + shuoming + "]" + "\n";
			}
		}
		
		tfldselectedmsg.setText( allstring + tfldselectedmsg.getText() + "\n");
//		 JScrollBar verticalScrollBar = scrollPaneuserselctmsg.getVerticalScrollBar();
//		 JScrollBar horizontalScrollBar = scrollPaneuserselctmsg.getHorizontalScrollBar();
////		 verticalScrollBar.setValue(verticalScrollBar.getMaximum() );
//		 Rectangle visible = scrollPaneuserselctmsg.getVisibleRect();
//		 visible.y = 0;
//		 scrollPaneuserselctmsg.scrollRectToVisible(visible);
		 tfldselectedmsg.setCaretPosition(0);
//		 horizontalScrollBar.setValue(horizontalScrollBar.getMinimum());
		
	}
	/*
	 * 
	 */
	private void refreshBanKuaiGeGuTableHightLight ()
	{
		Integer cjemaxwk; Integer bkmaxwk;Integer dpmaxwk ;Double showcje;Double showhsl;
		if(chkcjemaxwk.isSelected() ) {
			cjemaxwk = Integer.parseInt(tfldcjemaxwk.getText() );
		} else
			cjemaxwk = 100000000;
		if(cbxbkmaxwk.isSelected() ) {
			bkmaxwk = Integer.parseInt(tfldbkmaxwk.getText() );
		} else
			bkmaxwk= 100000000;
		if(cbxdpmaxwk.isSelected() ) {
			dpmaxwk = Integer.parseInt(tflddisplaydpmaxwk.getText() );
		} else
			dpmaxwk = 100000000;
		if(ckboxshowcje.isSelected()) {
			showcje = Double.parseDouble( tfldshowcje.getText() ) * 100000000;
		} else
			showcje = -1.0;
		if(cbxhuanshoulv.isSelected())
			showhsl = Double.parseDouble( tfldhuanshoulv.getText() ) ;
		else 
			showhsl = null;
		
		bkfxhighlightvalueslisteners.forEach(l -> l.hightLightFxValues(dpmaxwk, bkmaxwk, showcje, cjemaxwk,showhsl) );
		//对于panel来说，hightLightFxValues第一个参数不能用，因为panel 都是 节点对她的上级的占比
		panelGgDpCjeZhanBi.hightLightFxValues(null,dpmaxwk, null, null,null) ;
		pnlggdpcje.hightLightFxValues(null,null, null, cjemaxwk,null) ;
		panelbkcje.hightLightFxValues(null,null, null, cjemaxwk,null) ;
		panelbkwkzhanbi.hightLightFxValues(null,dpmaxwk, null, null,null) ;
		panelggbkcjezhanbi.hightLightFxValues(null,bkmaxwk, null, null,null) ;
	}
    /*
     * 
     */
    private  ArrayList<JiaRuJiHua> getZdgzFx( BkChanYeLianTreeNode curdisplayednode, LocalDate localDate,String period) 
    {
    	ArrayList<JiaRuJiHua> fxresult = bkdbopt.getZdgzFxjgForANodeOfGivenPeriod (curdisplayednode.getMyOwnCode(),localDate,period);
    	return fxresult;
	}
	/*
	 * 
	 */
	private void createEvents() 
	{
		cyltree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
//            	chanYeLianTreeMousePressed(evt);
//            	logger.debug("get action notice at bkcyl");
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
    	        TreePath[] treePaths = cyltree.getSelectionPaths();
    	    	BkChanYeLianTreeNode node = (BkChanYeLianTreeNode)cyltree.getLastSelectedPathComponent();
    	    	if(node.getType() == BanKuaiAndStockBasic.TDXBK) {
    	    		BanKuai bankuai = (BanKuai)allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode(node.getMyOwnCode(), BanKuaiAndStockBasic.TDXBK);
    	    		refreshCurentBanKuaiFengXiResult (bankuai,globeperiod);
    				displayNodeInfo(bankuai);
    				cbxsearchbk.updateUserSelectedNode(bankuai);
    				
    				Toolkit.getDefaultToolkit().beep();
    	    	}
    	        
            }
        });
		
		btntiaojiantongji.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				int row = tableBkZhanBi.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择一个板块","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				int modelRow = tableBkZhanBi.convertRowIndexToModel(row);
				BanKuai selectedbk = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(modelRow);
				if(exportcond.size()>0) {
//					selectedbk.getPeriodMatchConditionStockNum(exportcond, lastselecteddate, globeperiod, Integer.parseInt( tfldtongjizhouqi.getText() ) );
//					TimeSeries matchresult = selectedbk.getRangedMatchConditionStockNum (exportcond,globeperiod, Integer.parseInt( tfldtongjizhouqi.getText() ) );
					TimeSeries matchresult = fengXiMatchedStockNumFromFormerExportedFile (exportcond,globeperiod, Integer.parseInt( tfldtongjizhouqi.getText() ) );
					((BanKuaiFengXiCategoryBarChartCjeZhanbiPnl)panelbkwkzhanbi).updateConditionMatchData (selectedbk,globeperiod,matchresult);
				}
			}
		});
		
		panelbkwkzhanbi.getChartPanel().addChartMouseListener(new ChartMouseListener() { 
			@Override
			public void chartMouseClicked(ChartMouseEvent e)  
			{
				int row = tableBkZhanBi.getSelectedRow();
				if(row <0) 
					return;

				int modelRow = tableBkZhanBi.convertRowIndexToModel(row);
				BanKuai bkcur = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(modelRow);
				
				java.awt.event.MouseEvent me = e.getTrigger();
    	        if (me.getClickCount() == 2) {
    	        	LocalDate datekey = panelbkwkzhanbi.getCurSelectedBarDate ();
    	        	displayNodeLargerPeriodData (bkcur,datekey);
    	        }
			}

			@Override
			public void chartMouseMoved(ChartMouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		panelbkwkzhanbi.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
            	int row = tableBkZhanBi.getSelectedRow();
				if(row <0) 
					return;

				int modelRow = tableBkZhanBi.convertRowIndexToModel(row);
				BanKuai bkcur = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(modelRow);

                if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.SELECTED_PROPERTY)) {
                    @SuppressWarnings("unchecked")
                    String selectedinfo = evt.getNewValue().toString();
                    
					LocalDate datekey = LocalDate.parse(selectedinfo.substring(0, 10));
    				chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(datekey));
    				
    				String tooltip = selectedinfo.substring(10,selectedinfo.length());
    				ArrayList<JiaRuJiHua> fxjg = getZdgzFx (bkcur,CommonUtility.formateStringToDate(datekey.toString()),globeperiod);
    				setUserSelectedColumnMessage(tooltip,fxjg);
    				
    				if(bkcur.getBanKuaiLeiXing().equals(BanKuai.HASGGWITHSELFCJL) ) {//应该是有个股的板块点击才显示她的个股， 
    					LocalDate selectdate = CommonUtility.formateStringToDate(datekey.toString());
    					panelselectwkgeguzhanbi.setBanKuaiCjeNeededDisplay(bkcur,Integer.parseInt(tfldweight.getText() ), selectdate ,globeperiod );
    					
    					//显示选中周股票占比增加率排名等
    					refreshSpecificBanKuaiFengXiResult (bkcur,selectdate,globeperiod);
    				}
                }
            }
        });
		panelGgDpCjeZhanBi.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
				
				StockOfBanKuai selectstock = null;
				int stockrow = tableGuGuZhanBiInBk.getSelectedRow();
				if(stockrow != -1) {
					int modelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(stockrow);
					 selectstock = ((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).getStock(modelRow);
				}

                if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.SELECTED_PROPERTY)) {
                    @SuppressWarnings("unchecked")
                    String selectedinfo = evt.getNewValue().toString();
                    
					LocalDate datekey = LocalDate.parse(selectedinfo.substring(0, 10));
    				chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(datekey));
    				
//    				refreshGeGuKXianZouShiOfFiftyDays (selectstock,datekey);
    				
    				String tooltip = selectedinfo.substring(10,selectedinfo.length());
    				ArrayList<JiaRuJiHua> fxjg = getZdgzFx (selectstock,CommonUtility.formateStringToDate(datekey.toString()),globeperiod);
    				setUserSelectedColumnMessage(tooltip,fxjg);
                }
            }
        });
		panelGgDpCjeZhanBi.getChartPanel().addChartMouseListener(new ChartMouseListener() { 
			@Override
			public void chartMouseClicked(ChartMouseEvent e)  
			{
				StockOfBanKuai selectstock = null;
				int stockrow = tableGuGuZhanBiInBk.getSelectedRow();
				if(stockrow != -1) {
					int modelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(stockrow);
					 selectstock = ((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).getStock(modelRow);
				}
					
				
				java.awt.event.MouseEvent me = e.getTrigger();
    	        if (me.getClickCount() == 2) {
    	        	LocalDate datekey = panelGgDpCjeZhanBi.getCurSelectedBarDate ();
    	        	displayNodeLargerPeriodData (selectstock.getStock(),datekey);
    	        }
			}
			@Override
			public void chartMouseMoved(ChartMouseEvent arg0) {
			}
		});
		panelbkcje.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			 public void propertyChange(PropertyChangeEvent evt)  
			 {
				int row = tableBkZhanBi.getSelectedRow();
				if(row <0) 
					return;

				int modelRow = tableBkZhanBi.convertRowIndexToModel(row);
				BanKuai bkcur = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(modelRow);
				
				if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.SELECTED_PROPERTY)) {
					String selectedinfo = evt.getNewValue().toString();
                    
					LocalDate datekey = LocalDate.parse(selectedinfo.substring(0, 10));
    				chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(datekey));
    				
    				String tooltip = selectedinfo.substring(10,selectedinfo.length());
    				ArrayList<JiaRuJiHua> fxjg = getZdgzFx (bkcur,CommonUtility.formateStringToDate(datekey.toString()),globeperiod);
    				setUserSelectedColumnMessage(tooltip,fxjg);
				}
			}
		});
		pnlggdpcje.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			 public void propertyChange(PropertyChangeEvent evt)  
			 {
				StockOfBanKuai selectstock = null;
				int stockrow = tableGuGuZhanBiInBk.getSelectedRow();
				if(stockrow != -1) {
					int modelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(stockrow);
					 selectstock = ((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).getStock(modelRow);
				}
				
				if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.SELECTED_PROPERTY)) {
                    @SuppressWarnings("unchecked")
                    String selectedinfo = evt.getNewValue().toString();
                    
					LocalDate datekey = LocalDate.parse(selectedinfo.substring(0, 10));
    				chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(datekey));
    				
//    				refreshGeGuKXianZouShiOfFiftyDays (selectstock,datekey);
    				
    				String tooltip = selectedinfo.substring(10,selectedinfo.length());
    				ArrayList<JiaRuJiHua> fxjg = getZdgzFx (selectstock,CommonUtility.formateStringToDate(datekey.toString()),globeperiod);
    				setUserSelectedColumnMessage(tooltip,fxjg);
                }
			}
		});
		
		panelgegubkcje.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			 public void propertyChange(PropertyChangeEvent evt)   
			{
			}
		});
		
		panelggbkcjezhanbi.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			 public void propertyChange(PropertyChangeEvent evt)   
			{
			}
		});
		
		btnClear.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if( exportcond != null) { 
					exportcond.clear();
					
					btnaddexportcond.setText(String.valueOf(0));
					btnaddexportcond.setToolTipText("<html>导出条件设置<br></html>");
				}
			}
		});
		
		//添加导出条件
		btnaddexportcond.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				
				if( exportcond == null)
					exportcond = new ArrayList<ExportCondition> ();
				
				if(!ckboxshowcje.isSelected() && !cbxdpmaxwk.isSelected() && !cbxbkmaxwk.isSelected() && !chkcjemaxwk.isSelected()){
					JOptionPane.showMessageDialog(null,"未设置导出条件，请先设置导出条件！");
					return;
				} 
				
				initializeExportConditions ();
			}
		});
		
		//突出显示个股WK部分
		tfldbkmaxwk.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				if( Strings.isNullOrEmpty(tfldbkmaxwk.getText() )) {
					JOptionPane.showMessageDialog(null,"请设置突出显示板块MaxWk！");
					cbxdpmaxwk.setSelected(false);
					return;
				}
				if(cbxbkmaxwk.isSelected() ) {
					refreshBanKuaiGeGuTableHightLight ();
				}
			}
		});
		
		//突出显示成交额大于设置值
		chkcjemaxwk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if( Strings.isNullOrEmpty(tfldcjemaxwk.getText() )) {
					JOptionPane.showMessageDialog(null,"请设置突出显示板块MaxWk！");
					chkcjemaxwk.setSelected(false);
					return;
				}
				refreshBanKuaiGeGuTableHightLight ();
				
			}
		});
		cbxhuanshoulv.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if( Strings.isNullOrEmpty(tfldhuanshoulv.getText() )) {
					JOptionPane.showMessageDialog(null,"请设置突出显示换手率！");
					cbxhuanshoulv.setSelected(false);
					return;
				}
				refreshBanKuaiGeGuTableHightLight ();
			}
		});
		cbxbkmaxwk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if( Strings.isNullOrEmpty(tfldbkmaxwk.getText() )) {
					JOptionPane.showMessageDialog(null,"请设置突出显示板块MaxWk！");
					cbxbkmaxwk.setSelected(false);
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
					cbxdpmaxwk.setSelected(false);
					return;
				}
				refreshBanKuaiGeGuTableHightLight ();
				
			}
		});
		cbxdpmaxwk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if( Strings.isNullOrEmpty(tflddisplaydpmaxwk.getText() )) {
					JOptionPane.showMessageDialog(null,"请设置突出显示MaxWk！");
					cbxdpmaxwk.setSelected(false);
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
//						JOptionPane.showMessageDialog(null,"请选择每日板块文件！");
//						ckboxparsefile.setSelected(false);
//						return;
						
						chooseParsedFile ();
					}
					
					parseSelectedBanKuaiFile (tfldparsedfile.getText());
					((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).setShowParsedFile(true);
					tableGuGuZhanBiInBk.repaint();
				}
			
				if(!ckboxparsefile.isSelected()) {
					((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).setShowParsedFile(false);
					tableGuGuZhanBiInBk.repaint();
				}
			}
		});
		
		tfldshowcje.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				refreshBanKuaiGeGuTableHightLight ();
			}
		});
		
		ckboxshowcje.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				if( Strings.isNullOrEmpty(tfldshowcje.getText() )) {
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

		cbxsearchbk.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) 
			{
				if(arg0.getStateChange() == ItemEvent.SELECTED) {
					if( Strings.isNullOrEmpty( cbxsearchbk.getSelectedItem().toString()  ))
							return;
					
					BkChanYeLianTreeNode userinputnode = cbxsearchbk.getUserInputNode();
					String nodecode;
					if(userinputnode == null) { //可能是输入的字母没有找到
						nodecode = cbxsearchbk.getSelectedItem().toString();
					} else
						nodecode = userinputnode.getMyOwnCode();
					
					int rowindex = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuaiRowIndex( nodecode );
					
					if(rowindex != -1) {
						    int curselectrow = tableBkZhanBi.getSelectedRow();
						    int modelRow = tableBkZhanBi.convertRowIndexToView(rowindex);
						    if(modelRow == curselectrow)
						    	return;		    
							
							tableBkZhanBi.setRowSelectionInterval(modelRow, modelRow);
							tableBkZhanBi.scrollRectToVisible(new Rectangle(tableBkZhanBi.getCellRect(modelRow, 0, true)));
							BanKuai selectedbk = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(rowindex);
							refreshCurentBanKuaiFengXiResult (selectedbk,globeperiod);
							displayNodeInfo(selectedbk);
							cbxsearchbk.updateUserSelectedNode(selectedbk);
					} else 	{
						JOptionPane.showMessageDialog(null,"股票/板块代码有误或名称拼音有误！","Warning", JOptionPane.WARNING_MESSAGE);
					}

					
				}
			}
			
		});
		
		cbxstockcode.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) 
			{
				if(arg0.getStateChange() == ItemEvent.SELECTED) {
					BkChanYeLianTreeNode userinputnode = cbxstockcode.getUserInputNode();
					String nodecode;
					if(userinputnode == null) { //可能是输入的字母没有找到
						nodecode = cbxstockcode.getSelectedItem().toString();
					} else {
						nodecode = userinputnode.getMyOwnCode();
						displayStockSuoShuBanKuai((Stock)userinputnode);
					}
					
					int rowindex = tableGuGuZhanBiInBk.getSelectedRow();
//					int modelRow = tableGuGuZhanBiInBk.convertRowIndexToView(rowindex);
					
					if(!findInputedNodeInTable (nodecode))
						tableGuGuZhanBiInBk.setRowSelectionInterval(rowindex,rowindex);
				}
				
				if(arg0.getStateChange() == ItemEvent.DESELECTED) {
//					BkChanYeLianTreeNode userinputnode = cbxstockcode.getUserInputNode();
//					logger.info("test");
				
				}
			}
		});
		
		btnsixmonthbefore.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				LocalDate startday = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				LocalDate requirestart = startday.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange()-4,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
				dateChooser.setDate(Date.from(requirestart.atStartOfDay(ZoneId.systemDefault()).toInstant() ) );
				
//				panelbkwkzhanbi.resetDate();
//	    		panelgeguwkzhanbi.resetDate();
//	    		pnllastestggzhanbi.resetDate();
//	    		panelLastWkGeGuZhanBi.resetDate();
//	    		panelGeguDapanZhanBi.resetDate();
//	    		((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).deleteAllRows();
//	    		tfldselectedmsg.setText("");
////	    		initializeBanKuaiZhanBiByGrowthRate ();
	    		
	    		lastselecteddate = requirestart;


			}
		});
		btnsixmonthafter.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				LocalDate startday = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				LocalDate requirestart = startday.with(DayOfWeek.SATURDAY).plus(sysconfig.banKuaiFengXiMonthRange()-4,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
				dateChooser.setDate(Date.from(requirestart.atStartOfDay(ZoneId.systemDefault()).toInstant() ) );
				
//				panelbkwkzhanbi.resetDate();
//	    		panelgeguwkzhanbi.resetDate();
//	    		pnllastestggzhanbi.resetDate();
//	    		panelLastWkGeGuZhanBi.resetDate();
//	    		panelGeguDapanZhanBi.resetDate();
//	    		tfldselectedmsg.setText("");
//	    		((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).deleteAllRows();
////	    		initializeBanKuaiZhanBiByGrowthRate ();
	    		
	    		lastselecteddate = startday;

			}
		});
//		btnresetdate.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseClicked(MouseEvent arg0) {
//				
//					LocalDate newdate =  LocalDate.now();
//					dateChooser.setDate(Date.from(newdate.atStartOfDay(ZoneId.systemDefault()).toInstant() ) );
//					
//					if( (lastselecteddate == null) || ( newdate.isEqual( lastselecteddate) ) ) {
////		    			panelbkwkzhanbi.resetDate();
////			    		panelgeguwkzhanbi.resetDate();
////			    		pnllastestggzhanbi.resetDate();
////			    		panelLastWkGeGuZhanBi.resetDate();
////			    		panelGeguDapanZhanBi.resetDate();
////			    		((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).deleteAllRows();
////			    		tfldselectedmsg.setText("");
//////			    		initializeBanKuaiZhanBiByGrowthRate ();
////			    		
//			    		lastselecteddate = newdate;
//			    		
//			    		btnresetdate.setEnabled(false);
//		    		}
//					
//				}
//			
//		});
		dateChooser.addPropertyChangeListener(new PropertyChangeListener() {
		    @Override
		    public void propertyChange(PropertyChangeEvent e) {
		    	if("date".equals(e.getPropertyName() ) ) {
		    		
		    		JDateChooser wybieraczDat = (JDateChooser) e.getSource();
		    		LocalDate newdate = wybieraczDat.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		    		
//		    		if( newdate.isEqual(LocalDate.now() ) )
//						btnresetdate.setEnabled(false);
//		    		else
//		    			btnresetdate.setEnabled(true);
		    		
		    		if( (lastselecteddate == null) || ( !newdate.isEqual( lastselecteddate) ) ) {
		    			panelbkcje.resetDate();
		    			panelgegubkcje.resetDate ();
		    			panelbkwkzhanbi.resetDate();
			    		panelggbkcjezhanbi.resetDate();
			    		pnllastestggzhanbi.resetDate();
			    		panelLastWkGeGuZhanBi.resetDate();
			    		panelselectwkgeguzhanbi.resetDate();
			    		pnlggdpcje.resetDate();
			    		paneldayCandle.resetDate();
			    		panelGgDpCjeZhanBi.resetDate();
			    		((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).deleteAllRows();
			    		((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).deleteAllRows();
			    		tabbedPanegegu.setTitleAt(0, "当前周");
			    		tabbedPanegegu.setTitleAt(1, "选定周");
			    		tabbedPanebk.setSelectedIndex(0);
			    		setHighLightChenJiaoEr();
			    		
			    		initializeBanKuaiZhanBiByGrowthRate (StockGivenPeriodDataItem.WEEK);
			    		
			    		lastselecteddate = newdate;
		    		}
		    	}
//		        logger.info(e.getPropertyName()+ ": " + e.getNewValue());
		    }
		});
		
	
		editorPanebankuai.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				String selbk = editorPanebankuai.getSelectedBanKuai();
				String selbkcode;
				if(selbk != null)
					 selbkcode = selbk.trim().substring(1, 7);
				else 
					return;

				int rowindex = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuaiRowIndex(selbkcode);
				if(rowindex != -1) {
					int modelRow = tableBkZhanBi.convertRowIndexToView(rowindex);
					tableBkZhanBi.setRowSelectionInterval(modelRow, modelRow);
					tableBkZhanBi.scrollRectToVisible(new Rectangle(tableBkZhanBi.getCellRect(modelRow, 0, true)));
					
					//找到用户选择的板块
					BanKuai selectedbk = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(rowindex);
					refreshCurentBanKuaiFengXiResult (selectedbk,globeperiod);
					displayNodeInfo(selectedbk);
//					patchParsedFile (selectedbk);
					cbxsearchbk.updateUserSelectedNode(selectedbk);
					
					//找到该股票
					String stockcode = cbxstockcode.getSelectedItem().toString().substring(0, 6);
					if(!findInputedNodeInTable (stockcode) )
						JOptionPane.showMessageDialog(null,"在某个个股表内没有发现该股，可能在这个时间段内该股停牌","Warning",JOptionPane.WARNING_MESSAGE);
					
					Toolkit.getDefaultToolkit().beep();
				}		
			}
		});
		
		
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

				if (arg0.getClickCount() == 1) {
					hightlightSpecificSector (selectstock);
					refreshGeGuFengXiResult (selectstock); //个股对板块的数据
					refreshGeGuKXianZouShi (selectstock.getStock()); //K线
					cbxstockcode.updateUserSelectedNode (selectstock.getStock()); 
					displayStockSuoShuBanKuai((Stock)cbxstockcode.updateUserSelectedNode (selectstock.getStock()));
					displayStockCurrentFxResult (tableGuGuZhanBiInBk);
					
					Toolkit.getDefaultToolkit().beep();
				}
				if (arg0.getClickCount() == 2) {
//					 selectstock = (Stock)cbxstockcode.updateUserSelectedNode(selectstock);
//					 displayStockSuoShuBanKuai (selectstock);
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
					hightlightSpecificSector (selectstock);
					refreshGeGuFengXiResult (selectstock);
					cbxstockcode.updateUserSelectedNode (selectstock.getStock()); 
					displayStockSuoShuBanKuai((Stock)cbxstockcode.updateUserSelectedNode (selectstock.getStock()));
					displayStockCurrentFxResult (tablexuandingzhou);
				}
				if (arg0.getClickCount() == 2) {
//					 selectstock = (Stock)cbxstockcode.updateUserSelectedNode(selectstock);
//					 displayStockSuoShuBanKuai (selectstock);
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
					hightlightSpecificSector (selectstock);
					refreshGeGuFengXiResult (selectstock);
					cbxstockcode.updateUserSelectedNode (selectstock.getStock()); 
					displayStockSuoShuBanKuai((Stock)cbxstockcode.updateUserSelectedNode (selectstock.getStock()));
					displayStockCurrentFxResult (tablexuandingminustwo);
				}
				 if (arg0.getClickCount() == 2) {
//					 selectstock = (Stock)cbxstockcode.updateUserSelectedNode(selectstock);
//					 displayStockSuoShuBanKuai (selectstock);
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
					hightlightSpecificSector (selectstock);
					 refreshGeGuFengXiResult (selectstock);
					 cbxstockcode.updateUserSelectedNode (selectstock.getStock()); 
					 displayStockSuoShuBanKuai((Stock)cbxstockcode.updateUserSelectedNode (selectstock.getStock()));
					 displayStockCurrentFxResult (tablexuandingminusone);
				}
				 if (arg0.getClickCount() == 2) {
//					 selectstock = (Stock)cbxstockcode.updateUserSelectedNode(selectstock);
//					 displayStockSuoShuBanKuai (selectstock);
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
					hightlightSpecificSector (selectstock);
					refreshGeGuFengXiResult (selectstock);
					cbxstockcode.updateUserSelectedNode (selectstock.getStock()); 
					displayStockSuoShuBanKuai((Stock)cbxstockcode.updateUserSelectedNode (selectstock.getStock()));
					displayStockCurrentFxResult (tablexuandingplusone);
				}
				 if (arg0.getClickCount() == 2) {
//					 selectstock = (Stock)cbxstockcode.updateUserSelectedNode(selectstock);
//					 displayStockSuoShuBanKuai (selectstock);
				 }
			}
		});
		tablexuandingplustwo.addMouseListener(new MouseAdapter() 
		{
			public void mouseClicked(MouseEvent arg0) {
				int row = tablexuandingplustwo.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择一个股票！","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				int modelRow = tablexuandingplustwo.convertRowIndexToModel(row);
				StockOfBanKuai selectstock = ((BanKuaiGeGuTableModel)tablexuandingplustwo.getModel()).getStock (modelRow);
	
				if (arg0.getClickCount() == 1) {
					hightlightSpecificSector (selectstock);
					refreshGeGuFengXiResult (selectstock);
					cbxstockcode.updateUserSelectedNode (selectstock.getStock()); 
					displayStockSuoShuBanKuai((Stock)cbxstockcode.updateUserSelectedNode (selectstock.getStock()));
					displayStockCurrentFxResult (tablexuandingplustwo);
				}
				 if (arg0.getClickCount() == 2) {
//					 selectstock = (Stock)cbxstockcode.updateUserSelectedNode(selectstock);
//					 displayStockSuoShuBanKuai (selectstock);
				 }
				
				 
			}
		});
		
		btnChosPasFile.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mouseClicked(MouseEvent arg0) {
				chooseParsedFile ();
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
				BanKuai selectedbk = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(modelRow);
				refreshCurentBanKuaiFengXiResult (selectedbk,globeperiod);
				displayNodeInfo(selectedbk);
				cbxsearchbk.updateUserSelectedNode(selectedbk);
				Toolkit.getDefaultToolkit().beep();
				//装配每日模型文件
			}
		});
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
	 * 用户双击某个板块占比chart，则放大显示该node一年内的占比所有数据
	 */
	private void displayStockCurrentFxResult(BanKuaiGeGuTable curtable) 
	{
		int row = curtable.getSelectedRow();
		if(row <0) {
			JOptionPane.showMessageDialog(null,"请先选择一个个股","Warning",JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		int modelRow = curtable.convertRowIndexToModel(row);
		StockOfBanKuai selectstock = ((BanKuaiGeGuTableModel)curtable.getModel()).getStock (modelRow);
		int columncount = curtable.getColumnCount();
		String result = "";
		for(int i=0; i<columncount;i ++) {
			result = result + curtable.getColumnName(i) + ":" + curtable.getValueAt(row, i) +";";
		}
		StockNodeXPeriodData nodexdata = (StockNodeXPeriodData) selectstock.getStock().getNodeXPeroidData(globeperiod);
		Double liutongshizhi = nodexdata.getSpecificTimeLiuTongShiZhi(dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), 0);
		Double zongshizhi = nodexdata.getSpecificTimeZongShiZhi(dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), 0);
		result = result + "流通市值:" + liutongshizhi/100000000 + "亿" + "总市值:" + zongshizhi/100000000 + "\n";
		setUserSelectedColumnMessage (result, null);
		result = null;
	}
	/*
	 * 用户双击某个板块占比chart，则放大显示该node一年内的占比所有数据
	 */
	protected void displayNodeLargerPeriodData(BkChanYeLianTreeNode node, LocalDate datekey) 
	{
		LocalDate curselectdate = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate requireend = curselectdate.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange(),ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		LocalDate requirestart = requireend.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange(),ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		
		if(node.getType() == BanKuaiAndStockBasic.TDXBK) {
			node = this.allbksks.getBanKuai((BanKuai)node, requirestart.plusWeeks(1),globeperiod);
		} else if(node.getType() == BanKuaiAndStockBasic.TDXGG) {
			node = this.allbksks.getStock((Stock)node, requirestart.plusWeeks(1),globeperiod);
			node = this.allbksks.getStockK((Stock)node, requirestart.plusWeeks(1), StockGivenPeriodDataItem.DAY);
		} else if(node.getType() == BanKuaiAndStockBasic.BKGEGU) {
			BanKuai bk = ((StockOfBanKuai)node).getBanKuai();
			this.allbksks.getBanKuai((BanKuai)bk, requirestart.plusWeeks(1),globeperiod);
			node = this.allbksks.getGeGuOfBanKuai(bk, node.getMyOwnCode() ,globeperiod);
		}
		
		this.allbksks.getDaPan (requirestart.plusWeeks(1),globeperiod); //同步大盘数据
		
		NodeXPeriodDataBasic nodexdate = node.getNodeXPeroidData(globeperiod);
		
		BanKuaiFengXiLargePnl largeinfo = new BanKuaiFengXiLargePnl (node, requirestart.plusWeeks(1).with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange(),ChronoUnit.MONTHS).with(DayOfWeek.MONDAY)
				, curselectdate, globeperiod);
		largeinfo.highLightSpecificBarColumn(datekey);
		JOptionPane.showMessageDialog(null, largeinfo, node.getMyOwnCode()+node.getMyOwnName()+ "大周期分析结果", JOptionPane.OK_CANCEL_OPTION);
		
		largeinfo = null;
		System.gc();
	}
	/*
	 * 
	 */
	protected void initializeExportConditions() 
	{
		String exportbk = null;
		String msg =  "导出条件是否限定在仅当前板块起作用？";
		int exchangeresult = JOptionPane.showConfirmDialog(null,msg , "添加导出条件", JOptionPane.OK_CANCEL_OPTION);
		if(exchangeresult == JOptionPane.OK_OPTION) { //有一些板块，如次新股，可能导出条件比较特殊，可以单独设置
			int row = tableBkZhanBi.getSelectedRow();
			if(row <0) {
				JOptionPane.showMessageDialog(null,"请选择一个板块！","Warning",JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			int modelRow = tableBkZhanBi.convertRowIndexToModel(row);
			BanKuai selectedbk = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(modelRow);
			exportbk = selectedbk.getMyOwnCode();
		}

		String exportcjelevel = null;
		String exportdpmaxwklevel = null;
		String exportbkmaxwklevel = null;
		String exportcjemaxwklevel = null;
		String exporthsl = null;

		if(ckboxshowcje.isSelected())
			exportcjelevel = tfldshowcje.getText();
		if(cbxdpmaxwk.isSelected())
			exportdpmaxwklevel = tflddisplaydpmaxwk.getText();
		if(cbxbkmaxwk.isSelected())
			exportbkmaxwklevel = tfldbkmaxwk.getText();
		if(chkcjemaxwk.isSelected())
			exportcjemaxwklevel = tfldcjemaxwk.getText();
		if(cbxhuanshoulv.isSelected())
			exporthsl = tfldhuanshoulv.getText();
	
		ExportCondition expc = new ExportCondition (exportcjelevel,exportcjemaxwklevel,exportdpmaxwklevel,exportbkmaxwklevel,exporthsl,exportbk);
		exportcond.add(expc);
		
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
	protected void chooseParsedFile() 
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
		    
//		    logger.info(linuxpath);
		    tfldparsedfile.setText(linuxpath);
		    
		    if(ckboxparsefile.isSelected()) //只在需要的时候计算
		    	parseSelectedBanKuaiFile (linuxpath);
		}
	}
	/*
	 * 
	 */
	private void parseSelectedBanKuaiFile(String linuxpath) 
	{
		File parsefile = new File(linuxpath);
    	if(!parsefile.exists() )
    		return;
    	
    	String filename = parsefile.getName();
		filename = filename.replaceAll("\\D+","");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		LocalDate localDate = null;
		try{
			 localDate = LocalDate.parse(filename, formatter);
		} catch (java.time.format.DateTimeParseException e) {
			tfldparsedfile.setText("");
			return;
		}
		ZoneId zone = ZoneId.systemDefault();
		Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
		this.dateChooser.setDate(Date.from(instant));
    	
		//应该先从XML读取相关的数据，没有才去都文件一个个匹配
    	List<String> readparsefileLines = null;
		try {
			readparsefileLines = Files.readLines(parsefile,Charsets.UTF_8,new ParseBanKuaiFielProcessor ());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		stockinfile = new HashSet<String> (readparsefileLines);
		//现在产业链树上标记个股的数量
		BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)this.bkcyl.getBkChanYeLianTree().getModel().getRoot();
		patchParsedFileToTrees(treeroot,localDate,stockinfile);
		
		this.bkcyl.getBkChanYeLianTree().setCurrentDisplayedWk (localDate);
		DefaultTreeModel treeModel = (DefaultTreeModel) cyltree.getModel();
		treeModel.reload();
	}
	/*
	 * 
	 */
	private void patchParsedFileToTrees (BkChanYeLianTreeNode treeroot,LocalDate localDate, HashSet<String> stockinfile)
	{
		BkChanYeLianTreeNode treeChild;
		
		for (Enumeration<BkChanYeLianTreeNode> child = treeroot.children(); child.hasMoreElements();) {
			
            treeChild = (BkChanYeLianTreeNode) child.nextElement();
            
            int nodetype = treeChild.getType();
            if( nodetype == BkChanYeLianTreeNode.TDXBK) {
            	String tmpbkcode = treeChild.getMyOwnCode() ;
            	BkChanYeLianTree bkstkstree = this.allbksks.getAllBkStocksTree();
            	BanKuai nodeinallbktree = (BanKuai)bkstkstree.getSpecificNodeByHypyOrCode(tmpbkcode, BkChanYeLianTreeNode.TDXBK);
            	
            	if( nodeinallbktree.getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL) 
            			|| nodeinallbktree.getBanKuaiLeiXing().equals(BanKuai.NOGGWITHSELFCJL) 
            			|| nodeinallbktree.getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL)  ) //有些指数是没有个股不列入比较范围
					continue;

//            	String tmpname = treeChild.getMyOwnName();
            	nodeinallbktree = this.allbksks.getAllGeGuOfBanKuai(nodeinallbktree, StockGivenPeriodDataItem.WEEK);
            	Set<StockOfBanKuai> curbkallbkset = nodeinallbktree.getSpecificPeriodBanKuaiGeGu(localDate,0,StockGivenPeriodDataItem.WEEK);
            	HashSet<String> stkofbkset = new HashSet<String>  ();
            	for(StockOfBanKuai stkofbk : curbkallbkset) {
            		stkofbkset.add(stkofbk.getMyOwnCode());
            	}
            	
            	SetView<String>  intersectionbankuai = Sets.intersection(stockinfile, stkofbkset);
	    		BanKuaiTreeRelated treerelated = null;
				if(intersectionbankuai.size() > 0) {
					treerelated = (BanKuaiTreeRelated)treeChild.getNodeTreerelated ();
	    			treerelated.setStocksNumInParsedFile (localDate,intersectionbankuai.size());
				}
				
				for(String stkofbk : intersectionbankuai ) {
					StockOfBanKuai stkinbk = nodeinallbktree.getBanKuaiGeGu(stkofbk);
	    			StockOfBanKuaiTreeRelated stofbktree = (StockOfBanKuaiTreeRelated)stkinbk.getNodeTreerelated();
            		stofbktree.setStocksNumInParsedFile (localDate,true);
				}
				
				curbkallbkset = null;
				stkofbkset= null;
	        } 
	          
	        patchParsedFileToTrees(treeChild,localDate,stockinfile);
        }
	}

	/*
	 * 在个股表中发现输入的个股
	 */
	protected boolean findInputedNodeInTable(String nodecode) 
	{
		Boolean notfound = false;
		int rowindex = ((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel() ).getStockRowIndex(nodecode);
		if(rowindex <0) {
			tableGuGuZhanBiInBk.getSelectionModel().clearSelection();
			if(((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel() ).getRowCount() > 0)
				notfound = true;
		} else {
			int modelRow = tableGuGuZhanBiInBk.convertRowIndexToView(rowindex);
			int curselectrow = tableGuGuZhanBiInBk.getSelectedRow();
			if( curselectrow != modelRow) {
				tableGuGuZhanBiInBk.setRowSelectionInterval(modelRow, modelRow);
				tableGuGuZhanBiInBk.scrollRectToVisible(new Rectangle(tableGuGuZhanBiInBk.getCellRect(modelRow, 0, true)));
			}
		}
		
		rowindex = ((BanKuaiGeGuTableModel)tablexuandingzhou.getModel() ).getStockRowIndex(nodecode);
		if(rowindex <0) {
			tablexuandingzhou.getSelectionModel().clearSelection();
			if( ((BanKuaiGeGuTableModel)tablexuandingzhou.getModel() ).getRowCount() >0)
				notfound = true;
		} else {
			int modelRow = tablexuandingzhou.convertRowIndexToView(rowindex);
			int curselectrow = tablexuandingzhou.getSelectedRow();
			if( curselectrow != modelRow) {
				tablexuandingzhou.setRowSelectionInterval(modelRow, modelRow);
				tablexuandingzhou.scrollRectToVisible(new Rectangle(tablexuandingzhou.getCellRect(modelRow, 0, true)));
			}
		}
		
		rowindex = ((BanKuaiGeGuTableModel)tablexuandingminusone.getModel() ).getStockRowIndex(nodecode);
		if(rowindex <0) {
			tablexuandingminusone.getSelectionModel().clearSelection();
			if( ((BanKuaiGeGuTableModel)tablexuandingminusone.getModel() ).getRowCount() >0 )
				notfound = true;
		} else {
			int modelRow = tablexuandingminusone.convertRowIndexToView(rowindex);
			int curselectrow = tablexuandingminusone.getSelectedRow();
			if( curselectrow != modelRow)  {
				tablexuandingminusone.setRowSelectionInterval(modelRow, modelRow);
				tablexuandingminusone.scrollRectToVisible(new Rectangle(tablexuandingminusone.getCellRect(modelRow, 0, true)));
			}
		}

		rowindex = ((BanKuaiGeGuTableModel)tablexuandingminustwo.getModel() ).getStockRowIndex(nodecode);
		if(rowindex <0) {
			tablexuandingminustwo.getSelectionModel().clearSelection();
			if( ((BanKuaiGeGuTableModel)tablexuandingminustwo.getModel() ).getRowCount() > 0)
					notfound = true;
		} else {
			int modelRow = tablexuandingminustwo.convertRowIndexToView(rowindex);
			int curselectrow = tablexuandingminustwo.getSelectedRow();
			if( curselectrow != modelRow) {
				tablexuandingminustwo.setRowSelectionInterval(modelRow, modelRow);
				tablexuandingminustwo.scrollRectToVisible(new Rectangle(tablexuandingminustwo.getCellRect(modelRow, 0, true)));
			}
		}

		rowindex = ((BanKuaiGeGuTableModel)tablexuandingplusone.getModel() ).getStockRowIndex(nodecode);
		if(rowindex <0) {
			tablexuandingplusone.getSelectionModel().clearSelection();
			if(((BanKuaiGeGuTableModel)tablexuandingplusone.getModel() ).getRowCount() > 0)
				notfound = true;
		} else {
			int modelRow = tablexuandingplusone.convertRowIndexToView(rowindex);
			int curselectrow = tablexuandingplusone.getSelectedRow();
			if( curselectrow != modelRow)  {
				tablexuandingplusone.setRowSelectionInterval(modelRow, modelRow);
				tablexuandingplusone.scrollRectToVisible(new Rectangle(tablexuandingplusone.getCellRect(modelRow, 0, true)));
			}
		}
		
		rowindex = ((BanKuaiGeGuTableModel)tablexuandingplustwo.getModel() ).getStockRowIndex(nodecode);
		if(rowindex <0) {
			tablexuandingplusone.getSelectionModel().clearSelection();
			if(((BanKuaiGeGuTableModel)tablexuandingplustwo.getModel() ).getRowCount() >0 )
				notfound = true;
		} else {
			int modelRow = tablexuandingplustwo.convertRowIndexToView(rowindex);
			int curselectrow = tablexuandingplustwo.getSelectedRow();
			if( curselectrow != modelRow) {
				tablexuandingplustwo.setRowSelectionInterval(modelRow, modelRow);
				tablexuandingplustwo.scrollRectToVisible(new Rectangle(tablexuandingplustwo.getCellRect(modelRow, 0, true)));
			}
		}
		
		return !notfound;
	}
	/*
	 * 
	 */
	protected void displayNodeInfo(BanKuai selectedbk) 
	{
//		editorPanebkinfo.setText("");
//		editorPanebkinfo.displayNodeAllInfo(selectedbk);
		editorPanenodeinfo.setText("");
		editorPanenodeinfo.displayNodeAllInfo(selectedbk);
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
		else pnl_paomd.refreshMessage(null);
	}
	/*
	 * 
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

	private  JTextField tfldtongjizhouqi;
	private JButton btntiaojiantongji;
	private final JPanel contentPanel = new JPanel();
	private JButton okButton;
	private JButton cancelButton;
	private JStockCalendarDateChooser dateChooser; //https://toedter.com/jcalendar/
	private JScrollPane sclpleft;
	private BanKuaiInfoTable tableBkZhanBi;
//	private JTable tableGuGuZhanBiInBk;
	private BanKuaiGeGuTable tableGuGuZhanBiInBk;
	private BanKuaiFengXiCategoryBarChartPnl panelbkwkzhanbi;
	private JTextField tfldweight;
	private BanKuaiFengXiCategoryBarChartPnl panelggbkcjezhanbi;
	private BanKuaiFengXiPieChartCjePnl pnllastestggzhanbi;
	private BanKuaiFengXiPieChartCjePnl panelLastWkGeGuZhanBi;
	private BanKuaiFengXiPieChartCjePnl panelselectwkgeguzhanbi;
	private BanKuaiListEditorPane editorPanebankuai;
	private JButton btnresetdate;
	private JButton btnsixmonthbefore;
	private JButton btnsixmonthafter;
	private BanKuaiGeGuTable tablexuandingzhou;
	private JStockComboBox cbxstockcode;
	private BanKuaiFengXiCategoryBarChartPnl panelbkcje;
	private BanKuaiFengXiCategoryBarChartPnl panelgegubkcje;
	private BanKuaiGeGuTable tablexuandingminustwo; //new BanKuaiGeGuTable (this.stockmanager);
	private BanKuaiGeGuTable tablexuandingminusone;
	private BanKuaiGeGuTable tablexuandingplusone;
	private BanKuaiGeGuTable tablexuandingplustwo;
	private JTabbedPane tabbedPanegegu;
	private BanKuaiFengXiCategoryBarChartPnl panelGgDpCjeZhanBi;
	private JTextField tfldshowcje;
	private JTextField tfldparsedfile;
	private JTabbedPane tabbedPanegeguzhanbi;
	private JTextArea tfldselectedmsg;
	private JStockComboBox cbxsearchbk;
	private JButton btnChosPasFile;
	private JCheckBox ckboxshowcje;
	private JCheckBox ckboxparsefile;
	private DisplayBkGgInfoEditorPane editorPanenodeinfo;
	private JLabel lblshcje;
	private JLabel lblszcje;
	private JLabel lblNewLabel;
	private BanKuaiFengXiCandlestickPnl paneldayCandle;
	private JCheckBox cbxdpmaxwk;
	private JTextField tflddisplaydpmaxwk;
	private JScrollPane scrollPaneuserselctmsg;
	private PaoMaDeng2 pnl_paomd;
	private JTabbedPane tabbedPanebk;
	private JButton btnexportmodelgegu;
	private Action exportCancelAction;
	private Action bkfxCancelAction;
	private JProgressBar progressBarExport;
	private JCheckBox cbxbkmaxwk;
	private JTextField tfldbkmaxwk;
	private JTextField tfldcjemaxwk;
	private JCheckBox chkcjemaxwk;
	private BanKuaiFengXiCategoryBarChartCjlZhanbiPnl panelggdpcjlwkzhanbi;
	private BanKuaiFengXiPieChartCjlPnl pnllastestggcjlzhanbi;
	private JTabbedPane tabbedPane_1;
	private BanKuaiFengXiPieChartCjlPnl panelselectwkgegucjlzhanbi;
	private JButton btnaddexportcond;
	private JButton btnClear;
	private JProgressBar progressBarsys;
	private JCheckBox cbxggquanzhong;
	private BanKuaiFengXiCategoryBarChartPnl pnlggdpcje;
	private JTextField tfldhuanshoulv;
	private JCheckBox cbxhuanshoulv;
	private BkChanYeLianTree cyltree;
	
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
							.addContainerGap()
							.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 382, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(18)
							.addComponent(panel, 0, 0, Short.MAX_VALUE)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(paneldayCandle, GroupLayout.DEFAULT_SIZE, 911, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(scrollPaneuserselctmsg, GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE))
						.addComponent(panel_2, GroupLayout.DEFAULT_SIZE, 1107, Short.MAX_VALUE)
						.addComponent(scrollPanestockbk, GroupLayout.DEFAULT_SIZE, 1107, Short.MAX_VALUE))
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
						.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
							.addGroup(gl_contentPanel.createSequentialGroup()
								.addComponent(panel, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
									.addGroup(gl_contentPanel.createSequentialGroup()
										.addGap(167)
										.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 596, GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(scrollPanestockbk, GroupLayout.PREFERRED_SIZE, 51, GroupLayout.PREFERRED_SIZE))
									.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 854, GroupLayout.PREFERRED_SIZE)))
							.addGroup(Alignment.LEADING, gl_contentPanel.createSequentialGroup()
								.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 311, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(panelLastWkGeGuZhanBi, GroupLayout.PREFERRED_SIZE, 289, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(tabbedPane_1, GroupLayout.PREFERRED_SIZE, 309, GroupLayout.PREFERRED_SIZE)))))
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
		
		tfldselectedmsg = new JTextArea();
		scrollPaneuserselctmsg.setViewportView(tfldselectedmsg);
		tfldselectedmsg.setLineWrap(true);
		
		
		tfldselectedmsg.setEditable(false);
		tfldselectedmsg.setColumns(10);
		
		panelbkcje = new BanKuaiFengXiCategoryBarChartCjePnl();
		panelbkcje.setBorder(new TitledBorder(null, "\u677F\u5757\u6210\u4EA4\u989D", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		panelbkwkzhanbi = new BanKuaiFengXiCategoryBarChartCjeZhanbiPnl();
		panelbkwkzhanbi.setBorder(new TitledBorder(null, "\u677F\u5757\u6210\u4EA4\u989D\u5360\u6BD4", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		
		tabbedPanegeguzhanbi = new JTabbedPane(JTabbedPane.TOP);
		
		panelGgDpCjeZhanBi = new BanKuaiFengXiCategoryBarChartCjeZhanbiPnl();
		tabbedPanegeguzhanbi.addTab("\u5927\u76D8\u989D\u5360\u6BD4", null, panelGgDpCjeZhanBi, null);
		
		panelggbkcjezhanbi = new BanKuaiFengXiCategoryBarChartCjeZhanbiPnl();
		tabbedPanegeguzhanbi.addTab("\u677F\u5757\u989D\u5360\u6BD4", null, panelggbkcjezhanbi, null);
		
		JTabbedPane tabbedPane_2 = new JTabbedPane(JTabbedPane.TOP);
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
						.addComponent(panelbkwkzhanbi, GroupLayout.DEFAULT_SIZE, 540, Short.MAX_VALUE)
						.addComponent(panelbkcje, GroupLayout.PREFERRED_SIZE, 540, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel_2.createParallelGroup(Alignment.TRAILING)
						.addComponent(tabbedPanegeguzhanbi, GroupLayout.PREFERRED_SIZE, 541, Short.MAX_VALUE)
						.addComponent(tabbedPane_2, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 537, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_panel_2.setVerticalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
						.addComponent(panelbkcje, GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE)
						.addComponent(tabbedPane_2, GroupLayout.PREFERRED_SIZE, 253, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
						.addComponent(panelbkwkzhanbi, GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
						.addComponent(tabbedPanegeguzhanbi, GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE))
					.addContainerGap())
		);
		
		pnlggdpcje = new BanKuaiFengXiCategoryBarChartCjePnl();
		tabbedPane_2.addTab("\u5927\u76D8\u6210\u4EA4\u989D", null, pnlggdpcje, null);
		
		panelgegubkcje = new BanKuaiFengXiCategoryBarChartCjePnl();
		tabbedPane_2.addTab("\u677F\u5757\u6210\u4EA4\u989D", null, panelgegubkcje, null);
		
		panelggdpcjlwkzhanbi = new BanKuaiFengXiCategoryBarChartCjlZhanbiPnl();
		tabbedPanegeguzhanbi.addTab("\u5927\u76D8\u91CF\u5360\u6BD4", null, panelggdpcjlwkzhanbi, null);
		panel_2.setLayout(gl_panel_2);
		
		tabbedPanegegu = new JTabbedPane(JTabbedPane.TOP);
		
		tabbedPanebk = new JTabbedPane(JTabbedPane.TOP);
		cbxstockcode = new JStockComboBox(6);
		
		cbxsearchbk = new JStockComboBox(4);

		cbxsearchbk.setEditable(true);
		
		pnl_paomd = new PaoMaDeng2();
		pnl_paomd.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		
		exportCancelAction = new AbstractAction("导出条件个股") {

		      private static final long serialVersionUID = 4669650683189592364L;

		      @Override
		      public void actionPerformed(final ActionEvent e) {
		        if (exporttask == null) {
		        	exportBanKuaiWithGeGuOnCondition2();
		        } else {
		        	exporttask.cancel(true);
		        }
		      }
		 };
		 btnexportmodelgegu = new JButton(exportCancelAction);
		
		progressBarExport = new JProgressBar();
		progressBarExport.setValue(0);
        progressBarExport.setStringPainted(true);
		
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
							.addComponent(cbxstockcode, GroupLayout.PREFERRED_SIZE, 115, GroupLayout.PREFERRED_SIZE)
							.addContainerGap())
						.addGroup(gl_panel_1.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
							.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING, false)
								.addGroup(Alignment.TRAILING, gl_panel_1.createSequentialGroup()
									.addComponent(cbxsearchbk, 0, 0, Short.MAX_VALUE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btnexportmodelgegu)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(progressBarExport, GroupLayout.PREFERRED_SIZE, 118, GroupLayout.PREFERRED_SIZE)
									.addGap(17))
								.addGroup(Alignment.TRAILING, gl_panel_1.createSequentialGroup()
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
						.addComponent(cbxsearchbk, GroupLayout.PREFERRED_SIZE, 29, Short.MAX_VALUE)
						.addComponent(btnexportmodelgegu, GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE)
						.addComponent(progressBarExport, GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(tabbedPanebk, GroupLayout.PREFERRED_SIZE, 370, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(tabbedPanegegu, GroupLayout.PREFERRED_SIZE, 361, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addComponent(cbxstockcode, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
						.addComponent(pnl_paomd, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE))
					.addGap(33))
		);
		
		sclpleft = new JScrollPane();
		tabbedPanebk.addTab("\u677F\u5757\u5360\u6BD4", null, sclpleft, null);
		
		tableBkZhanBi = new BanKuaiInfoTable(this.stockmanager,this.allbksks);	
		
		sclpleft.setViewportView(tableBkZhanBi);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		tabbedPanebk.addTab("\u677F\u5757\u4FE1\u606F", null, scrollPane_2, null);
		
		editorPanenodeinfo = new DisplayBkGgInfoEditorPane();
		editorPanenodeinfo.setClearContentsBeforeDisplayNewInfo(true);
		scrollPane_2.setViewportView(editorPanenodeinfo);
		editorPanenodeinfo.setEditable(false);
		
		JScrollPane scrollPane = new JScrollPane();
		tabbedPanebk.addTab("\u4EA7\u4E1A\u94FE", null, scrollPane, null);
		
		cyltree = bkcyl.getBkChanYeLianTree();
		scrollPane.setViewportView(cyltree);
		
		JScrollPane scrollPanedangqian = new JScrollPane();
		tabbedPanegegu.addTab("当前周", null, scrollPanedangqian, null);
		tabbedPanegegu.setBackgroundAt(0, Color.ORANGE);
		
		
		tableGuGuZhanBiInBk = new BanKuaiGeGuTable (this.stockmanager);
		tableGuGuZhanBiInBk.hideZhanBiColumn(1);
		tableGuGuZhanBiInBk.sortByZhanBiGrowthRate();
		scrollPanedangqian.setViewportView(tableGuGuZhanBiInBk);
		
		JScrollPane scrollPanexuanding = new JScrollPane();
		tabbedPanegegu.addTab("选定周", null, scrollPanexuanding, null);
		tabbedPanegegu.setBackgroundAt(1, UIManager.getColor("MenuItem.selectionBackground"));
		
		tablexuandingzhou = new BanKuaiGeGuTable (this.stockmanager);
		tablexuandingzhou.hideZhanBiColumn(1);

		scrollPanexuanding.setViewportView(tablexuandingzhou);
		
		JScrollPane scrollPanexuandingminusone = new JScrollPane();
		tabbedPanegegu.addTab("\u9009\u5B9A\u5468-1", null, scrollPanexuandingminusone, null);
		tabbedPanegegu.setBackgroundAt(2, Color.LIGHT_GRAY);
		
		tablexuandingminusone = new BanKuaiGeGuTable (this.stockmanager);
		tablexuandingminusone.hideZhanBiColumn(1);
		scrollPanexuandingminusone.setViewportView(tablexuandingminusone);
		
		JScrollPane scrollPanexuandingminustwo = new JScrollPane();
		tabbedPanegegu.addTab("\u9009\u5B9A\u5468-2", null, scrollPanexuandingminustwo, null);
		
		tablexuandingminustwo = new BanKuaiGeGuTable (this.stockmanager);
		tablexuandingminustwo.hideZhanBiColumn(1);
		scrollPanexuandingminustwo.setViewportView(tablexuandingminustwo);
		
		JScrollPane scrollPanexuandingplusone = new JScrollPane();
		tabbedPanegegu.addTab("\u9009\u5B9A\u5468+1", null, scrollPanexuandingplusone, null);
		
		tablexuandingplusone = new BanKuaiGeGuTable (this.stockmanager);
		tablexuandingplusone.hideZhanBiColumn(1);
		scrollPanexuandingplusone.setViewportView(tablexuandingplusone);
		
		JScrollPane scrollPanexuandingplustwo = new JScrollPane();
		tabbedPanegegu.addTab("\u9009\u5B9A\u5468+2", null, scrollPanexuandingplustwo, null);
		
		tablexuandingplustwo = new BanKuaiGeGuTable (this.stockmanager);
		tablexuandingplustwo.hideZhanBiColumn(1);
		scrollPanexuandingplustwo.setViewportView(tablexuandingplustwo);
		
//		tableBkZhanBi = new BanKuaiGeGuTable (this.stockmanager);
		
		
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
		        	LocalDate cursettingdate = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		        	if(!cursettingdate.equals(LocalDate.now())) {
		        		LocalDate newdate =  LocalDate.now();
						dateChooser.setDate(Date.from(newdate.atStartOfDay(ZoneId.systemDefault()).toInstant() ) );
		        	} else
		        		btnresetdate.setEnabled(false);
		        } else {
		        	bkfxtask.cancel(true);
		        }
		      }
		    		
		 };
		
		btnresetdate = new JButton(bkfxCancelAction);
//		btnresetdate.setEnabled(false);
		
		btnsixmonthbefore = new JButton("<");
		
		btnsixmonthbefore.setToolTipText("\u5411\u524D6\u4E2A\u6708");
		
		btnsixmonthafter = new JButton(">");
		btnsixmonthafter.setToolTipText("\u5411\u540E6\u4E2A\u6708");
		
		lblNewLabel = new JLabel("\u4E0A\u8BC1");
		
		JLabel lblNewLabel_1 = new JLabel("\u6DF1\u8BC1");
		
		lblszcje = new JLabel("New label");
		
		lblshcje = new JLabel("New label");
		
		progressBarsys = new JProgressBar();
		progressBarsys.setFont(new Font("宋体", Font.PLAIN, 9));
		progressBarsys.setValue(0);
        progressBarsys.setStringPainted(true);
		
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING, false)
						.addComponent(progressBarsys, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addGroup(Alignment.LEADING, gl_panel.createSequentialGroup()
							.addComponent(dateChooser, GroupLayout.PREFERRED_SIZE, 133, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnsixmonthbefore)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnsixmonthafter)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnresetdate))
						.addGroup(Alignment.LEADING, gl_panel.createSequentialGroup()
							.addComponent(lblNewLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblszcje, GroupLayout.PREFERRED_SIZE, 124, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(lblNewLabel_1)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(lblshcje, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
					.addContainerGap(70, Short.MAX_VALUE))
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
						.addComponent(lblszcje)
						.addComponent(lblNewLabel_1)
						.addComponent(lblshcje))
					.addGap(2)
					.addComponent(progressBarsys, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		);
		panel.setLayout(gl_panel);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				getRootPane().setDefaultButton(okButton);
			}
			{
				cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
			}
			
			cbxggquanzhong = new JCheckBox("\u5254\u9664\u80A1\u7968\u6743\u91CD<=");
			
			tfldweight = new JTextField();
			tfldweight.setText("0");
			tfldweight.setColumns(10);
			
			ckboxshowcje = new JCheckBox("\u7A81\u51FA\u663E\u793A\u6210\u4EA4\u989D>(\u4EBF)");
			ckboxshowcje.setBackground(Color.LIGHT_GRAY);
			ckboxshowcje.setForeground(Color.YELLOW);
			
	
			
			tfldshowcje = new JTextField();
			tfldshowcje.setForeground(Color.BLUE);
			tfldshowcje.setColumns(10);
			this.setHighLightChenJiaoEr ();
			
			ckboxparsefile = new JCheckBox("\u6BCF\u65E5\u5206\u6790\u6587\u4EF6");
			ckboxparsefile.setForeground(Color.ORANGE);
			
			
			tfldparsedfile = new JTextField();
			tfldparsedfile.setForeground(Color.ORANGE);
			tfldparsedfile.setEditable(false);
			tfldparsedfile.setColumns(10);
			
			btnChosPasFile = new JButton("\u9009\u62E9\u6587\u4EF6");
			btnChosPasFile.setForeground(Color.ORANGE);
			
			cbxdpmaxwk = new JCheckBox("\u7A81\u51FA\u5360\u6BD4DPMAXWK>=");
			
			cbxdpmaxwk.setForeground(Color.RED);
			
			tflddisplaydpmaxwk = new JTextField();
			tflddisplaydpmaxwk.setForeground(Color.RED);
			tflddisplaydpmaxwk.setText("4");
			tflddisplaydpmaxwk.setColumns(10);
			
			cbxbkmaxwk = new JCheckBox("\u7A81\u51FA\u5360\u6BD4BKMAXWK>=");
			cbxbkmaxwk.setBackground(Color.WHITE);
			
			cbxbkmaxwk.setForeground(Color.MAGENTA);
			
			tfldbkmaxwk = new JTextField();
			tfldbkmaxwk.setForeground(Color.MAGENTA);
			tfldbkmaxwk.setText("5");
			tfldbkmaxwk.setColumns(10);
			
			chkcjemaxwk = new JCheckBox("\u7A81\u51FA\u6210\u4EA4\u989DMAXWK>=");
			chkcjemaxwk.setForeground(Color.CYAN);
			
			tfldcjemaxwk = new JTextField();
			tfldcjemaxwk.setText("7");
			tfldcjemaxwk.setForeground(Color.CYAN);
			tfldcjemaxwk.setColumns(10);
			
			btnaddexportcond = new JButton("") {
				 public Point getToolTipLocation(MouseEvent e) {
				        return new Point(20, -30);
				      }
			};
			btnaddexportcond.setHorizontalAlignment(SwingConstants.LEFT);
			btnaddexportcond.setToolTipText("<html>\u5BFC\u51FA\u6761\u4EF6\u8BBE\u7F6E:<br></html>");
			
			btnaddexportcond.setIcon(new ImageIcon(BanKuaiFengXi.class.getResource("/images/add-circular-outlined-button.png")));
			
			btnClear = new JButton("");
			btnClear.setIcon(new ImageIcon(BanKuaiFengXi.class.getResource("/images/delete.png")));
			
			btntiaojiantongji = new JButton("\u6761\u4EF6\u7EDF\u8BA1");
			btntiaojiantongji.setEnabled(false);
			
			tfldtongjizhouqi = new JTextField();
			tfldtongjizhouqi.setText("2");
			tfldtongjizhouqi.setColumns(10);
			
			JLabel label = new JLabel("\u5468\u671F");
			
			JLabel label_1 = new JLabel("          ");
			
			cbxhuanshoulv = new JCheckBox("\u7A81\u51FA\u6362\u624B\u7387>=");
			cbxhuanshoulv.setForeground(Color.BLUE);
			
			tfldhuanshoulv = new JTextField();
			tfldhuanshoulv.setText("30");
			tfldhuanshoulv.setColumns(10);
			
			
			GroupLayout gl_buttonPane = new GroupLayout(buttonPane);
			gl_buttonPane.setHorizontalGroup(
				gl_buttonPane.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_buttonPane.createSequentialGroup()
						.addContainerGap()
						.addComponent(btnaddexportcond, GroupLayout.PREFERRED_SIZE, 62, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnClear, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(btntiaojiantongji)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(tfldtongjizhouqi, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(label)
						.addGap(12)
						.addComponent(label_1)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(cbxggquanzhong)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(tfldweight, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(ckboxshowcje)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(tfldshowcje, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
						.addGap(18)
						.addComponent(cbxdpmaxwk)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(tflddisplaydpmaxwk, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(cbxbkmaxwk)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(tfldbkmaxwk, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(chkcjemaxwk)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(tfldcjemaxwk, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(cbxhuanshoulv)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(tfldhuanshoulv, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
						.addGap(43)
						.addComponent(ckboxparsefile)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(tfldparsedfile, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnChosPasFile)
						.addGap(166)
						.addComponent(okButton, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
						.addGap(337))
			);
			gl_buttonPane.setVerticalGroup(
				gl_buttonPane.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_buttonPane.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_buttonPane.createParallelGroup(Alignment.LEADING)
							.addGroup(gl_buttonPane.createSequentialGroup()
								.addComponent(btnaddexportcond, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
								.addGap(10))
							.addGroup(gl_buttonPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_buttonPane.createSequentialGroup()
									.addGroup(gl_buttonPane.createParallelGroup(Alignment.LEADING)
										.addGroup(gl_buttonPane.createParallelGroup(Alignment.BASELINE)
											.addComponent(cbxggquanzhong, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
											.addComponent(tfldweight, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addComponent(ckboxshowcje, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
											.addComponent(tfldshowcje, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addComponent(cbxdpmaxwk, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
											.addComponent(tflddisplaydpmaxwk, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addComponent(cbxbkmaxwk)
											.addComponent(tfldbkmaxwk, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addComponent(chkcjemaxwk)
											.addComponent(btnClear)
											.addComponent(btntiaojiantongji)
											.addComponent(tfldtongjizhouqi, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addComponent(label_1)
											.addComponent(label))
										.addGroup(gl_buttonPane.createSequentialGroup()
											.addGroup(gl_buttonPane.createParallelGroup(Alignment.BASELINE)
												.addComponent(tfldcjemaxwk, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
												.addComponent(cbxhuanshoulv))
											.addGap(2)))
									.addContainerGap())
								.addGroup(gl_buttonPane.createParallelGroup(Alignment.BASELINE)
									.addComponent(okButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addComponent(cancelButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addGroup(gl_buttonPane.createSequentialGroup()
									.addGroup(gl_buttonPane.createParallelGroup(Alignment.BASELINE)
										.addComponent(btnChosPasFile, GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
										.addComponent(tfldparsedfile, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
										.addComponent(ckboxparsefile, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(tfldhuanshoulv, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
									.addGap(4)))))
			);
			buttonPane.setLayout(gl_buttonPane);
		}
		

	
	}
	/*
	 * 根据星期几已经本周几个交易日自动设置成交量的值
	 */
	private void setHighLightChenJiaoEr() 
	{
		LocalDate curselectdate = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		if(LocalDate.now().with(DayOfWeek.FRIDAY).equals(curselectdate.with(DayOfWeek.FRIDAY) ) ) { //说明在当前周，按星期来设置
			if(curselectdate.getDayOfWeek() == DayOfWeek.MONDAY )
				tfldshowcje.setText("0");
			else if(curselectdate.getDayOfWeek() == DayOfWeek.TUESDAY )
				tfldshowcje.setText("1.2");
			else if(curselectdate.getDayOfWeek() == DayOfWeek.WEDNESDAY )
				tfldshowcje.setText("2.5");
			else if(curselectdate.getDayOfWeek() == DayOfWeek.THURSDAY )
				tfldshowcje.setText("3.7");
			else if(curselectdate.getDayOfWeek() == DayOfWeek.FRIDAY )
				tfldshowcje.setText("4.9");
			else if(curselectdate.getDayOfWeek() == DayOfWeek.SATURDAY || curselectdate.getDayOfWeek() == DayOfWeek.SUNDAY)
				tfldshowcje.setText("5.8");
			
		} else { //应该根据该周有多少个交易日来设置，而不是简单的按1.2*5 约=5.8
			int tradingdays = this.bkdbopt.getTradingDaysOfTheWeek (curselectdate);
			if(tradingdays < 5)
				tfldshowcje.setText(String.valueOf(1.15*tradingdays).substring(0, 3) );
			else
				tfldshowcje.setText("5.8");
		}
		
	}
	
	public class ExportCondition 
	{
		public ExportCondition (String exportcjelevel, String exportcjemaxwklevel, String exportdpmaxwklevel, String exportbkmaxwklevel,String exporthsl,String exportbk)
		{
			this.setSettingbk(exportbk);
			this.setSettindpgmaxwk(exportdpmaxwklevel);
			this.setSettinbkgmaxwk(exportbkmaxwklevel);
			this.setSettingcje(exportcjelevel);
			this.setSettingcjemaxwk(exportcjemaxwklevel);
			this.setSettinghsl(exporthsl);
			
			this.setTooltips ();
		}

		private Double settingcje;
		private Integer settindpgmaxwk;
		private Integer settinbkgmaxwk;
		private Integer settingcjemaxwk;
		private Double setSettinghsl;
		private String settingbk;
		private String tooltips = "";
		

		private void setSettingbk(String exportbk) 
		{
			if(exportbk != null ) {
				this.settingbk = exportbk;
				this.tooltips = this.tooltips + "限定在板块" + settingbk + "内";
			} else
				this.settingbk = "";
		}
		public String getSettingbk()
		{
			return this.settingbk;
		}
		private void setTooltips() 
		{
			this.tooltips = this.tooltips + "</br>";
		}
		public String getTooltips ()
		{
			return this.tooltips;
		}
		public Double getSettingcje() {
			return settingcje * 100000000;
		}
		private void setSettingcje(String exportcjelevel) {
			if(exportcjelevel != null) {
				this.settingcje = Double.parseDouble( exportcjelevel );
				this.tooltips = this.tooltips + "成交额>=" + settingcje + "亿";
			}
			else
				this.settingcje = -10000000000000000.0;
		}
		public Integer getSettindpgmaxwk() {
			return settindpgmaxwk;
		}
		private void setSettindpgmaxwk(String exportdpmaxwklevel) {
			if(exportdpmaxwklevel != null) {
				this.settindpgmaxwk = Integer.parseInt( exportdpmaxwklevel );
				this.tooltips = this.tooltips + "大盘MAXWK>=" +  settindpgmaxwk + "周";
			}
			else
				this.settindpgmaxwk = -100000000;
		}
		public Integer getSettinbkgmaxwk() {
			return settinbkgmaxwk;
		}
		private void setSettinbkgmaxwk(String exportbkmaxwklevel) {
			if(exportbkmaxwklevel != null) {
				this.settinbkgmaxwk = Integer.parseInt( exportbkmaxwklevel );
				this.tooltips = this.tooltips + "板块MAXWK>=" + settinbkgmaxwk + "周";
			}
			else
				this.settinbkgmaxwk = -100000000;
		}
		public Integer getSettingcjemaxwk() {
			return settingcjemaxwk;
		}
		private void setSettingcjemaxwk(String exportcjemaxwklevel) {
			if(exportcjemaxwklevel != null) {
				this.settingcjemaxwk = Integer.parseInt( exportcjemaxwklevel );
				this.tooltips = this.tooltips + "成交额MAXWK>=" + settingcjemaxwk + "周";
			}
			else
				this.settingcjemaxwk = -100000000;
		}
		private void setSettinghsl(String exporthsl) 
		{
			if(exporthsl != null) {
				this.setSettinghsl = Double.parseDouble(exporthsl);
				this.tooltips = this.tooltips + "换手率>=" + exporthsl + "%";
			} else
				this.setSettinghsl = -1.0;
		}
		public Double getSettinghsl ()
		{
			return this.setSettinghsl;
		}
	}
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
		private BkChanYeLianTree bkcyltree;
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
	     * Main task. Executed in background thread.
	     */
	    @Override
	    public Integer doInBackground() {
	    	Charset charset = Charset.forName("GBK") ;
				
	    	BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)bkcyltree.getModel().getRoot();
			int bankuaicount = bkcyltree.getModel().getChildCount(treeroot);
//			HashSet<String> outputresultset = new HashSet<String> ();
			ArrayList<BkChanYeLianTreeNode> outputnodeslist = new ArrayList<BkChanYeLianTreeNode> ();
			
			String ouputfilehead2 = "";
			for(ExportCondition expc : expclist) {
				if (isCancelled()) 
					 return null;
				
				String settingbk = expc.getSettingbk();
				Double settingcje = expc.getSettingcje() ;
				Integer settindpgmaxwk = expc.getSettindpgmaxwk();
				Integer settinbkgmaxwk = expc.getSettinbkgmaxwk();
				Integer seetingcjemaxwk = expc.getSettingcjemaxwk();
				Double settinghsl = expc.getSettinghsl();
				
				ouputfilehead2 = ouputfilehead2 + "[条件:"+ "限定在板块:" + settingbk + "内;成交额>=" + settingcje + ";大盘maxwk>=" + settindpgmaxwk
										+ ";板块maxwk>=" + settinbkgmaxwk + ";成交额maxwk>=" + seetingcjemaxwk
										+ ";换手率>=" + settinghsl
										+"]"
								;
				
				//导出板块
				if(settingbk.isEmpty() ) {
					for(int i=0;i< bankuaicount; i++) {
						if (isCancelled())
							 return null;
						
						BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode)bkcyltree.getModel().getChild(treeroot, i);
						if(childnode.getType() != BanKuaiAndStockBasic.TDXBK)
							continue;
						

						if( ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL) 
						 ||  ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL) //有些指数是没有个股和成交量的，不列入比较范围 
						 ||  ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.NOGGWITHSELFCJL) ) //仅导出有个股的板块
							continue;
						
						NodeXPeriodDataBasic nodexdata = childnode.getNodeXPeroidData(period);
						if(nodexdata.hasRecordInThePeriod(selectiondate,0) ) { //板块当周没有数据也不考虑，板块一般不可能没有数据，没有数据说明该板块这周还没有诞生，或者过去有，现在成交量已经不存入数据库
								Double recordcje = nodexdata.getChengJiaoEr(selectiondate, 0);
								Integer recordmaxbkwk = nodexdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(selectiondate,0);
								Integer recordmaxcjewk =nodexdata.getChenJiaoErMaxWeekOfSuperBanKuai(selectiondate,0);
								
								if( recordcje >= settingcje &&  recordmaxbkwk >= settindpgmaxwk && recordmaxcjewk >= seetingcjemaxwk ) { //满足条件，导出 ; 板块和个股不一样，只有一个占比
									if(!outputnodeslist.contains(childnode)){
										outputnodeslist.add(childnode);
									}
								 }
							
						}
					}
				}
								
				setProgress(30 );

				if(settinbkgmaxwk <0  && settingbk.isEmpty()) { //说明用户没有选择个股板块占比排序，那直接用tree 的个股，会更快
					for(int i=0;i< bankuaicount; i++) {
						if (isCancelled())
							 return null;
						
						BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode)bkcyltree.getModel().getChild(treeroot, i);
						if(childnode.getType() != BanKuaiAndStockBasic.TDXGG)
							continue;

						childnode = allbksks.getStock((Stock)childnode,selectiondate,StockGivenPeriodDataItem.WEEK);
						NodeXPeriodDataBasic nodexdata = childnode.getNodeXPeroidData(period);
						 //板块当周没有数据也不考虑，板块一般不可能没有数据，没有数据说明该板块这周还没有诞生，或者过去有，现在成交量已经不存入数据库
						// 同时刚上市的新股也不考虑
						if(nodexdata.hasRecordInThePeriod(selectiondate,0) && !((StockNodeXPeriodData)nodexdata).isVeryVeryNewXinStock()   ) {
								Double recordcje = nodexdata.getChengJiaoEr(selectiondate, 0);
								Integer recordmaxbkwk = nodexdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(selectiondate,0);
								Integer recordmaxcjewk =nodexdata.getChenJiaoErMaxWeekOfSuperBanKuai(selectiondate,0);
								Double recordhsl = ((StockNodeXPeriodData)nodexdata).getSpecificTimeHuanShouLv(selectiondate, 0);
								
								if( recordcje >= settingcje &&  recordmaxbkwk >= settindpgmaxwk 
										&& recordmaxcjewk >= seetingcjemaxwk && recordhsl>=settinghsl) { //满足条件，导出 ; 板块和个股不一样，只有一个占比
									if(!outputnodeslist.contains(childnode)){
										outputnodeslist.add(childnode);
									}
								}
						}
					}
					
					setProgress(60 );
				} else if(settinbkgmaxwk > 0 || !settingbk.isEmpty())  { //导出板块个股或者是用户限定了在某个板块查找
					for(int i=0;i< bankuaicount; i++) {
						if (isCancelled())
							 return null;
						
						BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode)bkcyltree.getModel().getChild(treeroot, i);
						if(childnode.getType() != BanKuaiAndStockBasic.TDXBK)
							continue;
						if(!settingbk.isEmpty()) { //如果限定了板块，就只在该板块内找
							if(!childnode.getMyOwnCode().equals(settingbk))
								continue;
						}
						if( ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL) 
						 ||  ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL) //有些指数是没有个股和成交量的，不列入比较范围 
						 ||  ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.NOGGWITHSELFCJL) ) //仅导出有个股的板块
							continue;
						
						NodeXPeriodDataBasic nodexdata = childnode.getNodeXPeroidData(period);
						if(nodexdata.hasRecordInThePeriod(selectiondate,0)  ) { //板块当周没有数据也不考虑，板块一般不可能没有数据，没有数据说明该板块这周还没有诞生，或者过去有，现在成交量已经不存入数据库
							childnode = allbksks.getAllGeGuOfBanKuai (((BanKuai)childnode),period);
							Set<StockOfBanKuai> rowbkallgg = ((BanKuai)childnode).getSpecificPeriodBanKuaiGeGu(selectiondate,0,period);
							for (StockOfBanKuai stockofbankuai : rowbkallgg) {
								 if (isCancelled()) 
									 return null;

								 NodeXPeriodDataBasic stockxdataforbk = stockofbankuai.getNodeXPeroidData( period);
								 if(!stockxdataforbk.hasRecordInThePeriod(selectiondate, 0)  )
									 continue;
								 
								 Double recordcje = stockxdataforbk.getChengJiaoEr(selectiondate, 0);
								 Integer recordmaxbkwk = stockxdataforbk.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(selectiondate,0) ;
								 
								 Stock ggstock = stockofbankuai.getStock();
								 NodeXPeriodDataBasic stockxdata = ggstock.getNodeXPeroidData(globeperiod);
								 if( ((StockNodeXPeriodData)stockxdata).isVeryVeryNewXinStock() ) // 刚上市的新股也不考虑
									 continue;
								 Integer recordmaxdpwk = stockxdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(selectiondate,0);
								 Integer recordmaxcjewk = stockxdata.getChenJiaoErMaxWeekOfSuperBanKuai(selectiondate,0);
								 Double recordhsl = ((StockNodeXPeriodData)stockxdata).getSpecificTimeHuanShouLv(selectiondate, 0);
								 
								 if(recordcje >= settingcje &&  recordmaxbkwk >= settinbkgmaxwk
											 && recordmaxcjewk >= seetingcjemaxwk && recordmaxdpwk >=  settindpgmaxwk && recordhsl>=settinghsl)  { //满足条件，导出 ; 
									 if(!outputnodeslist.contains(ggstock)){
											outputnodeslist.add(ggstock);
									 }
								 } 
							}
						}
					}
					
					setProgress(60 );
				}
			}
			
			setProgress( 80 );
			
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
				 String cjs = node.getSuoShuJiaoYiSuo();
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
			GephiFilesGenerator gephigenerator = new GephiFilesGenerator (allbksks);
  			gephigenerator.generatorGephiFile(outputfile, dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), globeperiod);
  			
  			setProgress( 90 );
  			//把板块分析文件的结果保存在XML里
  			parseSelectedBanKuaiFile (outputfile.getName());

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
				get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
	      
	    	Toolkit.getDefaultToolkit().beep();
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
				if(childnode.getType() != BanKuaiAndStockBasic.TDXBK) 
					continue;
				
				if( ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL) ||  ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL)  ) //有些指数是没有个股和成交量的，不列入比较范围
					continue;
				
				if( !((BanKuai)childnode).isShowinbkfxgui() )
					continue;
				
				String bkcode = childnode.getMyOwnCode();
				logger.debug(bkcode);
				childnode = (BanKuai)this.allbksks.getBanKuai(((BanKuai)childnode), curselectdate,period); 
				BanKuai.BanKuaiNodeXPeriodData bkxdata = (BanKuai.BanKuaiNodeXPeriodData)childnode.getNodeXPeroidData(period);
	    		if(bkxdata.hasRecordInThePeriod(curselectdate, 0) != null ) {//板块当周没有数据也不考虑，板块一般不可能没有数据，没有数据说明该板块这周还没有诞生，或者过去有，现在成交量已经不存入数据库
	    			((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).addBanKuai(((BanKuai)childnode));
	    		}
	    		
	    		//显示大盘成交量
	    		
	    		if(bkcode.equals("399001") ) {
	    			BanKuai.BanKuaiNodeXPeriodData bkszdata = (BanKuai.BanKuaiNodeXPeriodData)childnode.getNodeXPeroidData(StockGivenPeriodDataItem.WEEK);
	    			try {
		    			Double cjerec = bkszdata.getChengJiaoEr(curselectdate,0);
	    				lblszcje.setText( cjerec.toString() );
	    			} catch (java.lang.NullPointerException e) {
	    				lblszcje.setText( "本周没有成交额" );
	    			}
	    			
	    		} else if(bkcode.equals("999999") ) {
	    			BanKuai.BanKuaiNodeXPeriodData bkshdata = (BanKuai.BanKuaiNodeXPeriodData)childnode.getNodeXPeroidData(StockGivenPeriodDataItem.WEEK);
	    			try {
	    				Double cjerec = bkshdata.getChengJiaoEr(curselectdate,0);
	    				lblshcje.setText( cjerec.toString() );
	    			} catch (java.lang.NullPointerException e) {
	    				lblshcje.setText( "本周没有成交额" );
	    			}
	    		}
			}

			((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).refresh(curselectdate,period,null);
			setProgress(100);
			
			return 100;
	    }
	    
	    /*
	     * Executed in event dispatching thread
	     */
	    @Override
	    public void done() 	    
	    {
	        Toolkit.getDefaultToolkit().beep();
	    }
	}
}

/*
 * 
 */
class NodeChenJiaoErComparator implements Comparator<BkChanYeLianTreeNode> {
	private String period;
	private LocalDate compareDate;
	private int difference;
	public NodeChenJiaoErComparator (LocalDate compareDate, int difference, String period )
	{
		this.period = period;
		this.compareDate = compareDate;
		this.difference = difference;
	}
    public int compare(BkChanYeLianTreeNode node1, BkChanYeLianTreeNode node2) {
        Double cje1 = node1.getNodeXPeroidData( period).getChengJiaoEr(compareDate, difference) ;
        Double cje2 = node2.getNodeXPeroidData( period).getChengJiaoEr(compareDate, difference);
        
        return cje1.compareTo(cje2);
    }
}

/*
 * google guava files 
 */
class ParseBanKuaiFielProcessor implements LineProcessor<List<String>> 
{
   
    private List<String> stocklists = Lists.newArrayList();
   
    @Override
    public boolean processLine(String line) throws IOException {
    	if(line.trim().length() ==7) {
    		if(line.startsWith("1")) { //上海的个股或板块
    			if(line.startsWith("16")) { //上海的个股
    				stocklists.add(line.substring(1));
    			}
    		} else  {
    			if(line.startsWith("00") || line.startsWith("03") ) { //深圳的个股
    				stocklists.add(line.substring(1));
    			}
    		}
    	}
        return true;
    }
    @Override
    public List<String> getResult() {
        return stocklists;
    }
}