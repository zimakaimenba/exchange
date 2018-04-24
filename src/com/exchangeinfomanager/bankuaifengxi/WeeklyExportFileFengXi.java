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
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;
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
import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.DaPan;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.asinglestockinfo.Stock.StockNodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.StockGivenPeriodDataItem;
import com.exchangeinfomanager.asinglestockinfo.StockOfBanKuai;
import com.exchangeinfomanager.bankuaichanyelian.BanKuaiAndChanYeLian;
import com.exchangeinfomanager.bankuaichanyelian.BkChanYeLianTree;
import com.exchangeinfomanager.bankuaichanyelian.HanYuPinYing;
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
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartHuanShouLvPnl;
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



public class WeeklyExportFileFengXi extends JDialog {

	/**
	 * Create the dialog.
	 */
	public WeeklyExportFileFengXi(StockInfoManager stockmanager1, BanKuaiAndChanYeLian bkcyl2)
	{
		this.stockmanager = stockmanager1;
		this.bkcyl = bkcyl2;
		this.sysconfig = SystemConfigration.getInstance();
		this.bkdbopt = new BanKuaiDbOperation ();

		initializeGui ();
		initializeShiZhiRangeLevel ();
		
		createEvents ();
	}

	private LocalDate lastselecteddate;
	private BanKuaiAndChanYeLian bkcyl;
	private SystemConfigration sysconfig;
	private StockInfoManager stockmanager;
	private BanKuaiDbOperation bkdbopt;
	private static Logger logger = Logger.getLogger(BanKuaiFengXi.class);
	private ArrayList<ExportCondition> exportcond;
	private String globeperiod;
	
	private ArrayList<Double> shizhiuprangelist;
	private ArrayList<Double> shizhimiddlerangelist;
	private ArrayList<Double> shizhidownrangelist;
	private Double pnlupmax ,pnlmiddlemax;
		
	private Set<BarChartPanelHightLightColumnListener> chartpanelhighlightlisteners;
	private Set<BarChartPanelDataChangedListener> barchartpanelbankuaidatachangelisteners;
	private Set<BarChartPanelDataChangedListener> piechartpanelbankuaidatachangelisteners;
	private Set<BarChartPanelDataChangedListener> barchartpanelstockofbankuaidatachangelisteners;
	private Set<BarChartPanelDataChangedListener> barchartpanelstockdatachangelisteners;
	private Set<BarChartHightLightFxDataValueListener> bkfxhighlightvalueslisteners;
	/*
	 * 
	 */
	private void initializeShiZhiRangeLevel ()
	{
		shizhiuprangelist = new  ArrayList<Double> ();
		shizhimiddlerangelist = new  ArrayList<Double> ();
		shizhidownrangelist = new  ArrayList<Double> ();
		
		this.shizhiuprangelist.add(30.0);
		this.shizhiuprangelist.add(50.0);
		
		this.shizhimiddlerangelist.add(90.0);
		this.shizhimiddlerangelist.add(150.0);
		
		this.shizhidownrangelist.add(300.0);
		this.shizhidownrangelist.add(500.0);
		this.shizhidownrangelist.add(700.0);
		
		pnlupmax = 50.0 * 100000000;
		pnlmiddlemax = 150.0 * 100000000;
		
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
		    
			File parsefile = new File(linuxpath);
	    	if(!parsefile.exists() )
	    		return;
	    	
	    	List<String> stockinfile = null;
//	    	Charset charset = Charset.forName("GBK") ;
			try {
				stockinfile = Files.readLines(parsefile,Charset.forName("GBK"));
			} catch (IOException e) {
				e.printStackTrace();
			}

			parsedSelectedFile(stockinfile);
		}
		
	}
	
	private void parsedSelectedFile(List<String> stockinfile)
	{
		//获得导出日期
		String exporttime = stockinfile.get(0);
		int timestart = exporttime.trim().indexOf(":");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		lastselecteddate = LocalDate.parse(exporttime.substring(timestart+1, exporttime.length()-1).trim(),formatter); //Files.append("<导出日期:" + selectiondate.toString() + ">"+ System.getProperty("line.separator") ,outputfile, charset);
		tflddaochuriqi.setText(lastselecteddate.toString());
		//获得导出周期
		String exportperiod = stockinfile.get(1);
		int periodstart = exportperiod.trim().indexOf(":");
		String period = exportperiod.substring(periodstart+1,exportperiod.length()-1);
		//获得导出条件
		tfldexportcondlists.setText("");
		String exportconditions = stockinfile.get(2);
		tfldexportcondlists.setText(exportconditions);
		
//		BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)this.bkcyl.getBkChanYeLianTree().getModel().getRoot();
		ArrayList<Stock> stocks = new ArrayList<Stock> (); 
		for(String everyline : stockinfile) {
			if(everyline.startsWith("1") || everyline.startsWith("0")) {
				if(everyline.startsWith("10") || everyline.startsWith("18") || everyline.startsWith("0399")) {
					String codetype = ""; //是板块 ,目前不是分析对象
				} else {
					String stockcode = everyline.substring(1, 7);
					Stock stock = (Stock)this.bkcyl.getBkChanYeLianTree().getSpecificNodeByHypyOrCode(stockcode,BanKuaiAndStockBasic.TDXGG);
					stocks.add(stock);
					stock = this.bkcyl.getStock(stock, lastselecteddate, period);
				}
			}
		}
		
		boolean zongshizhi = true;
		if(zongshizhi) {
			stocks.sort((node1, node2) -> { 
				 Double shizhi1 = ((StockNodeXPeriodData)node1.getNodeXPeroidData( period)).getSpecificTimeZongShiZhi(lastselecteddate, 0) ;
		         Double shizhi2 = ((StockNodeXPeriodData)node2.getNodeXPeroidData( period)).getSpecificTimeZongShiZhi(lastselecteddate, 0) ;
				 return shizhi1.compareTo(shizhi2);
			});

			dispalyStocksToUpPnlGuiByZongShiZhi (stocks,lastselecteddate,period);
			dispalyStocksToMiddlePnlGuiByZongShiZhi (stocks,lastselecteddate,period);
			dispalyStocksToDownPnlGuiByZongShiZhi (stocks,lastselecteddate,period);
		} else {
			
		}
		
		stocks = null;
	}
	private void dispalyStocksToDownPnlGuiByZongShiZhi(ArrayList<Stock> stocks, LocalDate lastselecteddate2,
			String period) 
	{
		ArrayList<Stock> finialstocks = new ArrayList<Stock> ();
		for(int i=0;i<shizhidownrangelist.size();i++ ) {
			Double upshizhi = shizhidownrangelist.get(i) * 100000000;
			Double lowshizhi ;
			if(i == 0)
				lowshizhi = shizhimiddlerangelist.get(shizhimiddlerangelist.size()-1) * 100000000;
			else
				lowshizhi = shizhidownrangelist.get(i-1) * 100000000;
			
			ArrayList<Stock> tmpstocks = new ArrayList<Stock> ();
			for(Stock stock : stocks) {
				StockNodeXPeriodData nodexdata = (StockNodeXPeriodData)stock.getNodeXPeroidData(period);
				Double zongshizhi = nodexdata.getSpecificTimeZongShiZhi(lastselecteddate2, 0);
				if(zongshizhi > pnlmiddlemax  && zongshizhi <= upshizhi  && zongshizhi >= lowshizhi) {
					tmpstocks.add(stock);
				}
			}
			//开始对换手率排序
			Collections.sort(tmpstocks, new NodeHuanShouComparator(lastselecteddate2,0,period) );
			finialstocks.addAll(tmpstocks);
			tmpstocks = null;
		}
		//display to gui
		panelgghsldown.updatedDate(finialstocks, shizhidownrangelist, "zongshizhi", lastselecteddate2, period);
	}
	private void dispalyStocksToMiddlePnlGuiByZongShiZhi(ArrayList<Stock> stocks, LocalDate lastselecteddate2,
			String period) 
	{
		ArrayList<Stock> finialstocks = new ArrayList<Stock> ();
		for(int i=0;i<shizhimiddlerangelist.size();i++ ) {
			Double upshizhi = shizhimiddlerangelist.get(i) * 100000000;
			Double lowshizhi ;
			if(i == 0)
				lowshizhi = shizhiuprangelist.get(shizhiuprangelist.size()-1) * 100000000;
			else
				lowshizhi = shizhimiddlerangelist.get(i-1) * 100000000;
			
			ArrayList<Stock> tmpstocks = new ArrayList<Stock> ();
			for(Stock stock : stocks) {
				StockNodeXPeriodData nodexdata = (StockNodeXPeriodData)stock.getNodeXPeroidData(period);
				Double zongshizhi = nodexdata.getSpecificTimeZongShiZhi(lastselecteddate2, 0);
				if(zongshizhi > pnlupmax && zongshizhi<= pnlmiddlemax && zongshizhi <= upshizhi  && zongshizhi >= lowshizhi) {
					tmpstocks.add(stock);
				}
			}
			//开始对换手率排序
			Collections.sort(tmpstocks, new NodeHuanShouComparator(lastselecteddate2,0,period) );
			finialstocks.addAll(tmpstocks);
			tmpstocks = null;
		}
		//display to gui
		panelgghslmiddle.updatedDate(finialstocks, shizhimiddlerangelist, "zongshizhi", lastselecteddate2, period);
		
	}
	/*
	 * 总市值
	 */
	private void dispalyStocksToUpPnlGuiByZongShiZhi(ArrayList<Stock> stocks, LocalDate lastselecteddate2, String period)
	{
 		ArrayList<Stock> finialstocks = new ArrayList<Stock> ();
		for(int i=0;i<shizhiuprangelist.size();i++ ) {
			Double upshizhi = shizhiuprangelist.get(i) * 100000000;
			Double lowshizhi ;
			if(i == 0)
				lowshizhi = 0.0;
			else
				lowshizhi = shizhiuprangelist.get(i-1) * 100000000;
			
			ArrayList<Stock> tmpstocks = new ArrayList<Stock> ();
			for(Stock stock : stocks) {
				StockNodeXPeriodData nodexdata = (StockNodeXPeriodData)stock.getNodeXPeroidData(period);
				Double zongshizhi = nodexdata.getSpecificTimeZongShiZhi(lastselecteddate2, 0);
				if(zongshizhi <= pnlupmax && zongshizhi <= upshizhi  && zongshizhi >= lowshizhi) {
					tmpstocks.add(stock);
				}
			}
			//开始对换手率排序
			Collections.sort(tmpstocks, new NodeHuanShouComparator(lastselecteddate2,0,period) );
			finialstocks.addAll(tmpstocks);
			tmpstocks = null;
		}
		//display to gui
		panelgghslup.updatedDate(finialstocks, shizhiuprangelist, "zongshizhi", lastselecteddate2, period);
	}
	/*
	 * 流通市值
	 */
	private void sortStocksByLiuTongShiZhiInFileAndDispalyToGui(LocalDate lastselecteddate2, ArrayList<Stock> stocks, String period)
	{
		stocks.sort((node1, node2) -> { 
			 Double shizhi1 = ((StockNodeXPeriodData)node1.getNodeXPeroidData( period)).getSpecificTimeLiuTongShiZhi(lastselecteddate2, 0) ;
	         Double shizhi2 = ((StockNodeXPeriodData)node2.getNodeXPeroidData( period)).getSpecificTimeLiuTongShiZhi(lastselecteddate2, 0) ;
			 return shizhi1.compareTo(shizhi2);
		});
//		Collections.sort(stocks, new NodeShiZhiComparator(lastselecteddate2,0,period,"zongshizhi") );
		ArrayList<Stock> finialstocks = new ArrayList<Stock> ();
		ArrayList<Stock> tmpstocks = new ArrayList<Stock> ();
		for(int i=0;i<shizhirangelist.size();i++ ) {
			Double upshizhi = shizhirangelist.get(i) * 100000000;
			Double lowshizhi ;
			if(i == 0)
				lowshizhi = 0.0;
			else
				lowshizhi = shizhirangelist.get(i-1) * 100000000;
			
			for(Stock stock : stocks) {
				StockNodeXPeriodData nodexdata = (StockNodeXPeriodData)stock.getNodeXPeroidData(period);
				Double liutongshizhi = nodexdata.getSpecificTimeLiuTongShiZhi(lastselecteddate2, 0);
				if(liutongshizhi <= upshizhi  && liutongshizhi >= lowshizhi) {
					tmpstocks.add(stock);
				}
			}
			//开始对换手率排序
			Collections.sort(tmpstocks, new NodeHuanShouComparator(lastselecteddate2,0,period) );
			finialstocks.addAll(tmpstocks);
		}
		//display to gui
		panelgghslup.updatedDate(finialstocks, shizhirangelist, "liutongshizhi", lastselecteddate2, period);
	}

	/*
	 * 
	 */

	class NodeHuanShouComparator implements Comparator<Stock> {
		private String period;
		private LocalDate compareDate;
		private int difference;
		private String shizhitype;
		public NodeHuanShouComparator (LocalDate compareDate, int difference, String period)
		{
			this.period = period;
			this.compareDate = compareDate;
			this.difference = difference;
		}
	    public int compare(Stock node1, Stock node2) 
	    {
	    	Double hsl1, hsl2;
	        hsl1 = ((StockNodeXPeriodData)node1.getNodeXPeroidData( period)).getSpecificTimeHuanShouLv(compareDate, difference) ;
	        hsl2 = ((StockNodeXPeriodData)node2.getNodeXPeroidData( period)).getSpecificTimeHuanShouLv(compareDate, difference) ;
	    	
//	        int reulst = hsl1.compareTo(hsl2);
	        return hsl1.compareTo(hsl2);
	    }
	}
	/*
	 * 
	 */
	class NodeShiZhiComparator implements Comparator<Stock> {
		private String period;
		private LocalDate compareDate;
		private int difference;
		private String shizhitype;
		public NodeShiZhiComparator (LocalDate compareDate, int difference, String period,String shizhitype)
		{
			this.period = period;
			this.compareDate = compareDate;
			this.difference = difference;
			this.shizhitype = shizhitype;
		}
	    public int compare(Stock node1, Stock node2) 
	    {
	    	Double shizhi1, shizhi2;
	    	if(this.shizhitype.trim().toLowerCase().contains("liutong")) {
		        shizhi1 = ((StockNodeXPeriodData)node1.getNodeXPeroidData( period)).getSpecificTimeLiuTongShiZhi(compareDate, difference) ;
		        shizhi2 = ((StockNodeXPeriodData)node2.getNodeXPeroidData( period)).getSpecificTimeLiuTongShiZhi(compareDate, difference) ;
	    	} else {
	    		shizhi1 = ((StockNodeXPeriodData)node1.getNodeXPeroidData( period)).getSpecificTimeZongShiZhi(compareDate, difference) ;
		        shizhi2 = ((StockNodeXPeriodData)node2.getNodeXPeroidData( period)).getSpecificTimeZongShiZhi(compareDate, difference) ;
	    	}
	        
//	    	int result = shizhi1.compareTo(shizhi2);
	        return shizhi1.compareTo(shizhi2);
	    }
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
		this.bkfxhighlightvalueslisteners = new HashSet<>();
		barchartpanelbankuaidatachangelisteners.add(panelgghslup);
		//板块pie chart
		piechartpanelbankuaidatachangelisteners.add(pnllastestggzhanbi);
		//个股对板块
//		barchartpanelstockofbankuaidatachangelisteners.add(panelgegucje); //个股对于板块交易额,一般看个股对大盘的成交额，这个就不看了
		barchartpanelstockofbankuaidatachangelisteners.add(panelggbkcjezhanbi);
		barchartpanelstockdatachangelisteners.add(panelGgDpCjeZhanBi);
		//用户点击bar chart的一个column, highlight bar chart
		chartpanelhighlightlisteners.add(panelGgDpCjeZhanBi);
		chartpanelhighlightlisteners.add(panelggbkcjezhanbi);
		chartpanelhighlightlisteners.add(panelgghslup);
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
    	LocalDate curselectdate = lastselecteddate;
    	
//    	HashMap<String, BanKuai> bkhascjl = new HashMap<String, BanKuai> ();
    	
    	BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)this.bkcyl.getBkChanYeLianTree().getModel().getRoot();
		int bankuaicount = bkcyl.getBkChanYeLianTree().getModel().getChildCount(treeroot);
        
		for(int i=0;i< bankuaicount; i++) {
			
			BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode) this.bkcyl.getBkChanYeLianTree().getModel().getChild(treeroot, i);
			if(childnode.getType() != BanKuaiAndStockBasic.TDXBK) 
				continue;
			
			if( ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL) ||  ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL)  ) //有些指数是没有个股和成交量的，不列入比较范围
				continue;
			
			if( !((BanKuai)childnode).isShowinbkfxgui() )
				continue;
			
			String bkcode = childnode.getMyOwnCode();
			logger.debug(bkcode);
			childnode = (BanKuai)this.bkcyl.getBanKuai(((BanKuai)childnode), curselectdate,period); 
			BanKuai.BanKuaiNodeXPeriodData bkxdata = (BanKuai.BanKuaiNodeXPeriodData)childnode.getNodeXPeroidData(StockGivenPeriodDataItem.WEEK);
			if(bkxdata.hasRecordInThePeriod(curselectdate, 0) != null )//板块当周没有数据也不考虑，板块一般不可能没有数据，没有数据说明该板块这周还没有诞生，或者过去有，现在成交量已经不存入数据库
				((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).addBanKuai(((BanKuai)childnode));
		}
		
		((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).refresh(curselectdate,period,null);
		
		Toolkit.getDefaultToolkit().beep();
	}
	/*
	 * 显示板块的占比和个股
	 */
	protected void refreshCurentBanKuaiFengXiResult(BanKuai selectedbk,String period) 
	{
		tfldselectedmsg.setText("");
		tabbedPanegegu.setSelectedIndex(0);
//		panelbkcje.resetDate();
//		panelbkwkzhanbi.resetDate();
//		pnllastestggzhanbi.resetDate();
//		panelbkwkzhanbi.resetDate();
//		panelggbkcjezhanbi.resetDate();
//		panelselectwkgeguzhanbi.resetDate();
//		panelLastWkGeGuZhanBi.resetDate();
//		panelGgDpCjeZhanBi.resetDate();
//		panelgegubkcje.resetDate();
		tabbedPanegegu.setTitleAt(1, "选定周");
		tfldselectedmsg.setText("");
		tabbedPanebk.setSelectedIndex(0);
//		paneldayCandle.resetDate();
//		pnlggdpcje.resetDate();
		paneldayCandle.resetDate();
		
		((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).deleteAllRows();
		((BanKuaiGeGuTableModel)tablexuandingzhou.getModel()).deleteAllRows();
		((BanKuaiGeGuTableModel)tablexuandingminusone.getModel()).deleteAllRows();
		((BanKuaiGeGuTableModel)tablexuandingminustwo.getModel()).deleteAllRows();
		((BanKuaiGeGuTableModel)tablexuandingplusone.getModel()).deleteAllRows();
		((BanKuaiGeGuTableModel)tablexuandingplustwo.getModel()).deleteAllRows();
		
		LocalDate curselectdate = lastselecteddate;
		//板块自身占比
//		final BanKuai selectbk = selectedbk;
//		barchartpanelbankuaidatachangelisteners.forEach(l -> l.updatedDate(selectbk, curselectdate, 0,globeperiod));
		for(BarChartPanelDataChangedListener tmplistener : barchartpanelbankuaidatachangelisteners) {
			tmplistener.updatedDate(selectedbk, curselectdate, 0,globeperiod);
		}
		
		
//		setDisplayNoticeLevelForPanels();
		
		//更新板块所属个股
		if(selectedbk.getBanKuaiLeiXing().equals(BanKuai.HASGGWITHSELFCJL)) { //有个股才需要更新，有些板块是没有个股的
			
			selectedbk = bkcyl.getAllGeGuOfBanKuai (selectedbk,period);
			
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
	 * 几个表显示用户在选择周个股占比增长率排名等
	 */
	private void refreshSpecificBanKuaiFengXiResult (BanKuai selectedbk, LocalDate selecteddate,String period)
	{
		selectedbk = bkcyl.getAllGeGuOfBanKuai (selectedbk,period);
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
			LocalDate curselectdate = lastselecteddate;
//			DaPan dapan =  (DaPan) bkcyl.getBkChanYeLianTree().getSpecificNodeByHypyOrCode("000000");
			
//			panelggbkcjezhanbi.resetDate();
//			panelGgDpCjeZhanBi.resetDate();
//			panelgegubkcje.resetDate();
//			panelggdpcjlwkzhanbi.resetDate();
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
		LocalDate curselectdate = lastselecteddate;
		LocalDate requireend = curselectdate.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = curselectdate.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange(),ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		
		selectstock2 = bkcyl.getStock(selectstock2,curselectdate,StockGivenPeriodDataItem.WEEK);
		//日线K线走势，目前K线走势和成交量在日线以上周期是分开的，所以调用时候要特别小心，以后会合并
		selectstock2 = bkcyl.getStockK(selectstock2,curselectdate,StockGivenPeriodDataItem.DAY);
		
		for (BarChartPanelDataChangedListener tmplistener : barchartpanelstockdatachangelisteners) {
			tmplistener.updatedDate(selectstock2, curselectdate, 0,globeperiod);
		}

		paneldayCandle.resetDate();
		paneldayCandle.updatedDate(selectstock2,requirestart,requireend,StockGivenPeriodDataItem.DAY);
		paneldayCandle.displayRangeHighLowValue(true);
	}
	/*
	 * 用户选择个股周成交量某个数据后，显示该数据前后50个交易日的日线K线走势
	 */
//	private void refreshGeGuKXianZouShiOfFiftyDays (BkChanYeLianTreeNode selectstock2,LocalDate selectedday)
//	{
//		LocalDate requirestart = selectedday.with(DayOfWeek.MONDAY).minus(2,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
//		LocalDate requireend = selectedday.with(DayOfWeek.MONDAY).plus(3,ChronoUnit.MONTHS).with(DayOfWeek.FRIDAY);
//		
//		paneldayCandle.resetDate();
//
//		//日线K线走势，目前K线走势和成交量在日线以上周期是分开的，所以调用时候要特别小心，以后会合并
//		BkChanYeLianTreeNode selectstock = bkcyl.getStockK(selectstock2.getMyOwnCode(),requireend,StockGivenPeriodDataItem.DAY);
//		paneldayCandle.updatedDate(selectstock,requirestart,requireend,StockGivenPeriodDataItem.DAY);
//		paneldayCandle.highLightSpecificDateCandleStickWithHighLowValue(selectedday, StockGivenPeriodDataItem.DAY, true);
//	}
	
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
	 * 
	 */
//	public void setParsedFile(String file) 
//	{
//		this.tfldparsedfile.setText(file );
//		BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)this.bkcyl.getBkChanYeLianTree().getModel().getRoot();
//		stockinfile = treeroot.getNodetreerelated().getParseFileStockSet(); 
//	}

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

//	private void refreshBanKuaiGeGuTableHightLight ()
//	{
//		Integer cjemaxwk; Integer bkmaxwk;Integer dpmaxwk ;Double showcje;
//		if(chkcjemaxwk.isSelected() ) {
//			cjemaxwk = Integer.parseInt(tfldcjemaxwk.getText() );
//		} else
//			cjemaxwk = 100000000;
//		if(cbxbkmaxwk.isSelected() ) {
//			bkmaxwk = Integer.parseInt(tfldbkmaxwk.getText() );
//		} else
//			bkmaxwk= 100000000;
//		if(cbxdpmaxwk.isSelected() ) {
//			dpmaxwk = Integer.parseInt(tflddisplaydpmaxwk.getText() );
//		} else
//			dpmaxwk = 100000000;
//		if(ckboxshowcje.isSelected()) {
//			showcje = Double.parseDouble( tfldshowcje.getText() ) * 100000000;
//		} else
//			showcje = -1.0;
//		
//		bkfxhighlightvalueslisteners.forEach(l -> l.hightLightFxValues(dpmaxwk, bkmaxwk, showcje, cjemaxwk) );
//		//对于panel来说，hightLightFxValues第一个参数不能用，因为panel 都是 节点对她的上级的占比
//		panelGgDpCjeZhanBi.hightLightFxValues(null,dpmaxwk, null, null) ;
//		pnlggdpcje.hightLightFxValues(null,null, null, cjemaxwk) ;
//		panelbkcje.hightLightFxValues(null,null, null, cjemaxwk) ;
//		panelbkwkzhanbi.hightLightFxValues(null,dpmaxwk, null, null) ;
//		panelggbkcjezhanbi.hightLightFxValues(null,bkmaxwk, null, null) ;
//	}
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
		
//		panelbkwkzhanbi.getChartPanel().addChartMouseListener(new ChartMouseListener() { 
//			@Override
//			public void chartMouseClicked(ChartMouseEvent e)  
//			{
//				int row = tableBkZhanBi.getSelectedRow();
//				if(row <0) 
//					return;
//
//				int modelRow = tableBkZhanBi.convertRowIndexToModel(row);
//				BanKuai bkcur = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(modelRow);
//				
//				java.awt.event.MouseEvent me = e.getTrigger();
//    	        if (me.getClickCount() == 2) {
//    	        	LocalDate datekey = panelbkwkzhanbi.getCurSelectedBarDate ();
//    	        	displayNodeLargerPeriodData (bkcur,datekey);
//    	        }
//			}
//
//			@Override
//			public void chartMouseMoved(ChartMouseEvent arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//			
//		});
//		panelbkwkzhanbi.addPropertyChangeListener(new PropertyChangeListener() {
//
//            public void propertyChange(PropertyChangeEvent evt) {
//            	int row = tableBkZhanBi.getSelectedRow();
//				if(row <0) 
//					return;
//
//				int modelRow = tableBkZhanBi.convertRowIndexToModel(row);
//				BanKuai bkcur = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(modelRow);
//
//                if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.SELECTED_PROPERTY)) {
//                    @SuppressWarnings("unchecked")
//                    String selectedinfo = evt.getNewValue().toString();
//                    
//					LocalDate datekey = LocalDate.parse(selectedinfo.substring(0, 10));
//    				chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(datekey));
//    				
//    				String tooltip = selectedinfo.substring(10,selectedinfo.length());
//    				ArrayList<JiaRuJiHua> fxjg = getZdgzFx (bkcur,CommonUtility.formateStringToDate(datekey.toString()),globeperiod);
//    				setUserSelectedColumnMessage(tooltip,fxjg);
//    				
//    				if(bkcur.getBanKuaiLeiXing().equals(BanKuai.HASGGWITHSELFCJL) ) {//应该是有个股的板块点击才显示她的个股， 
//    					LocalDate selectdate = CommonUtility.formateStringToDate(datekey.toString());
//    					panelselectwkgeguzhanbi.setBanKuaiCjeNeededDisplay(bkcur,Integer.parseInt(tfldweight.getText() ), selectdate ,globeperiod );
//    					
//    					//显示选中周股票占比增加率排名等
//    					refreshSpecificBanKuaiFengXiResult (bkcur,selectdate,globeperiod);
//    				}
//                }
//            }
//        });
//		panelGgDpCjeZhanBi.addPropertyChangeListener(new PropertyChangeListener() {
//
//            public void propertyChange(PropertyChangeEvent evt) {
//				
//				StockOfBanKuai selectstock = null;
//				int stockrow = tableGuGuZhanBiInBk.getSelectedRow();
//				if(stockrow != -1) {
//					int modelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(stockrow);
//					 selectstock = ((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).getStock(modelRow);
//				}
//
//                if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.SELECTED_PROPERTY)) {
//                    @SuppressWarnings("unchecked")
//                    String selectedinfo = evt.getNewValue().toString();
//                    
//					LocalDate datekey = LocalDate.parse(selectedinfo.substring(0, 10));
//    				chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(datekey));
//    				
////    				refreshGeGuKXianZouShiOfFiftyDays (selectstock,datekey);
//    				
//    				String tooltip = selectedinfo.substring(10,selectedinfo.length());
//    				ArrayList<JiaRuJiHua> fxjg = getZdgzFx (selectstock,CommonUtility.formateStringToDate(datekey.toString()),globeperiod);
//    				setUserSelectedColumnMessage(tooltip,fxjg);
//                }
//            }
//        });
//		panelGgDpCjeZhanBi.getChartPanel().addChartMouseListener(new ChartMouseListener() { 
//			@Override
//			public void chartMouseClicked(ChartMouseEvent e)  
//			{
//				StockOfBanKuai selectstock = null;
//				int stockrow = tableGuGuZhanBiInBk.getSelectedRow();
//				if(stockrow != -1) {
//					int modelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(stockrow);
//					 selectstock = ((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).getStock(modelRow);
//				}
//					
//				
//				java.awt.event.MouseEvent me = e.getTrigger();
//    	        if (me.getClickCount() == 2) {
//    	        	LocalDate datekey = panelGgDpCjeZhanBi.getCurSelectedBarDate ();
//    	        	displayNodeLargerPeriodData (selectstock.getStock(),datekey);
//    	        }
//			}
//			@Override
//			public void chartMouseMoved(ChartMouseEvent arg0) {
//			}
//		});
//		
//		panelggbkcjezhanbi.addPropertyChangeListener(new PropertyChangeListener() {
//			@Override
//			 public void propertyChange(PropertyChangeEvent evt)   
//			{
//			}
//		});
//		
		btngephi.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				File filegephi;

				if(tfldparsedfile.getText() != null && tfldparsedfile.getText().trim().length() != 0) {
					GephiFilesGenerator gephigenerator = new GephiFilesGenerator (bkcyl);
					filegephi = gephigenerator.generatorGephiFile(sysconfig.toUNIXpath(tfldparsedfile.getText().trim() ), lastselecteddate, globeperiod);
				} else {
					chooseParsedFile ();
					GephiFilesGenerator gephigenerator = new GephiFilesGenerator (bkcyl);
					filegephi = gephigenerator.generatorGephiFile(sysconfig.toUNIXpath(tfldparsedfile.getText().trim() ), lastselecteddate, globeperiod);
				}
				
				if(filegephi == null)
					return; 
				int exchangeresult = JOptionPane.showConfirmDialog(null, "Gephi文件生产成功，请在" + filegephi.getAbsolutePath() + "下查看！是否打开该目录？","导出完毕", JOptionPane.OK_CANCEL_OPTION);
	      		  if(exchangeresult == JOptionPane.CANCEL_OPTION) {
	      			  return;
	      		  }
	      		  try {
	      			String path = filegephi.getAbsolutePath();
	      			Runtime.getRuntime().exec("explorer.exe /select," + path);
	      		  } catch (IOException e1) {
	      				e1.printStackTrace();
	      		  }
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
//					hightlightSpecificSector (selectstock);
					refreshGeGuFengXiResult (selectstock); //个股对板块的数据
					refreshGeGuKXianZouShi (selectstock.getStock()); //K线
//					cbxstockcode.updateUserSelectedNode (selectstock.getStock()); 
//					displayStockSuoShuBanKuai((Stock)cbxstockcode.updateUserSelectedNode (selectstock.getStock()));
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
//					hightlightSpecificSector (selectstock);
					refreshGeGuFengXiResult (selectstock);
//					cbxstockcode.updateUserSelectedNode (selectstock.getStock()); 
//					displayStockSuoShuBanKuai((Stock)cbxstockcode.updateUserSelectedNode (selectstock.getStock()));
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
//					hightlightSpecificSector (selectstock);
					refreshGeGuFengXiResult (selectstock);
//					cbxstockcode.updateUserSelectedNode (selectstock.getStock()); 
//					displayStockSuoShuBanKuai((Stock)cbxstockcode.updateUserSelectedNode (selectstock.getStock()));
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
//					hightlightSpecificSector (selectstock);
					 refreshGeGuFengXiResult (selectstock);
//					 cbxstockcode.updateUserSelectedNode (selectstock.getStock()); 
//					 displayStockSuoShuBanKuai((Stock)cbxstockcode.updateUserSelectedNode (selectstock.getStock()));
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
//					hightlightSpecificSector (selectstock);
					refreshGeGuFengXiResult (selectstock);
//					cbxstockcode.updateUserSelectedNode (selectstock.getStock()); 
//					displayStockSuoShuBanKuai((Stock)cbxstockcode.updateUserSelectedNode (selectstock.getStock()));
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
//					hightlightSpecificSector (selectstock);
					refreshGeGuFengXiResult (selectstock);
//					cbxstockcode.updateUserSelectedNode (selectstock.getStock()); 
//					displayStockSuoShuBanKuai((Stock)cbxstockcode.updateUserSelectedNode (selectstock.getStock()));
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
//				patchParsedFile (selectedbk);
				Toolkit.getDefaultToolkit().beep();
				//装配每日模型文件
			}
		});
	}
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
		setUserSelectedColumnMessage (result, null);
		result = null;
	}
//	/*
//	 * 閿熸枻鎷风ず閿熺煫浼欐嫹閿熸枻鎷烽敓绲檃r column閿熸枻鎷峰簲閿熸枻鎷烽敓鏂ゆ嫹绀洪敓鏂ゆ嫹閿熸枻鎷锋伅
//	 */
//	private void setUserSelectedColumnMessage(String selttooltips,ArrayList<JiaRuJiHua> fxjg) 
//	{
//		String allstring = selttooltips + "\n";
//		if(fxjg !=null) {
//			for(JiaRuJiHua jrjh : fxjg) {
//				LocalDate actiondate = jrjh.getJiaRuDate();
//				String actiontype = jrjh.getGuanZhuType();
//				String shuoming = jrjh.getJiHuaShuoMing();
//				
//				allstring = allstring +  "[" + actiondate.toString() + actiontype +  " " + shuoming + "]" + "\n";
//			}
//		}
//		
//		tfldselectedmsg.setText( allstring + tfldselectedmsg.getText() + "\n");
//		tfldselectedmsg.setCaretPosition(0);
//	}

	/*
	 * 用户双击某个板块占比chart，则放大显示该node一年内的占比所有数据
	 */
	protected void displayNodeLargerPeriodData(BkChanYeLianTreeNode node, LocalDate datekey) 
	{
		LocalDate curselectdate = lastselecteddate;
		LocalDate requireend = curselectdate.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange(),ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		LocalDate requirestart = requireend.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange(),ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		
		if(node.getType() == BanKuaiAndStockBasic.TDXBK) {
			node = this.bkcyl.getBanKuai((BanKuai)node, requirestart.plusWeeks(1),globeperiod);
		} else if(node.getType() == BanKuaiAndStockBasic.TDXGG) {
			node = this.bkcyl.getStock((Stock)node, requirestart.plusWeeks(1),globeperiod);
			node = this.bkcyl.getStockK((Stock)node, requirestart.plusWeeks(1), StockGivenPeriodDataItem.DAY);
		} else if(node.getType() == BanKuaiAndStockBasic.BKGEGU) {
			BanKuai bk = ((StockOfBanKuai)node).getBanKuai();
			this.bkcyl.getBanKuai((BanKuai)bk, requirestart.plusWeeks(1),globeperiod);
			node = this.bkcyl.getGeGuOfBanKuai(bk, node.getMyOwnCode() ,globeperiod);
		}
		
		this.bkcyl.getDaPan (requirestart.plusWeeks(1),globeperiod); //同步大盘数据
		
		NodeXPeriodDataBasic nodexdate = node.getNodeXPeroidData(globeperiod);
		
		BanKuaiFengXiLargePnl largeinfo = new BanKuaiFengXiLargePnl (node, requirestart.plusWeeks(1).with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange(),ChronoUnit.MONTHS).with(DayOfWeek.MONDAY)
				, curselectdate, globeperiod);
		largeinfo.highLightSpecificBarColumn(datekey);
		JOptionPane.showMessageDialog(null, largeinfo, node.getMyOwnCode()+node.getMyOwnName()+ "大周期分析结果", JOptionPane.OK_CANCEL_OPTION);
		
		largeinfo = null;
		System.gc();
	}



//	protected void patchParsedFile(BanKuai selectedbk) 
//	{
//		if( stockinfile != null &&  stockinfile.size()>0)
// 	    	 this.bkcyl.getBkChanYeLianTree().updateParseFileInfoToTreeFromSpecificNode (selectedbk,stockinfile,dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() );
//	}

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
	 * 
	 */
//	protected void hightlightSpecificSector(StockOfBanKuai selectstock) 
//	{
//		String stockcode = selectstock.getMyOwnCode(); 
//		 try {
//			 String stockname = selectstock.getMyOwnName().trim(); 
//			 pnllastestggzhanbi.hightlightSpecificSector (stockcode+stockname);
//			 panelLastWkGeGuZhanBi.hightlightSpecificSector (stockcode+stockname);
//			 panelselectwkgeguzhanbi.hightlightSpecificSector (stockcode+stockname);
//			 pnllastestggcjlzhanbi.hightlightSpecificSector (stockcode+stockname);
//			 panelselectwkgegucjlzhanbi.hightlightSpecificSector (stockcode+stockname);
//		 } catch ( java.lang.NullPointerException e) {
//			 pnllastestggzhanbi.hightlightSpecificSector (stockcode);
//			 panelLastWkGeGuZhanBi.hightlightSpecificSector (stockcode);
//			 panelselectwkgeguzhanbi.hightlightSpecificSector (stockcode);
//			 pnllastestggcjlzhanbi.hightlightSpecificSector (stockcode);
//			 panelselectwkgegucjlzhanbi.hightlightSpecificSector (stockcode);
//		 }
//		
//	}
	private final JPanel contentPanel = new JPanel();
	private JButton okButton;
	private JButton cancelButton;
	private JScrollPane sclpleft;
	private BanKuaiInfoTable tableBkZhanBi;
//	private JTable tableGuGuZhanBiInBk;
	private BanKuaiGeGuTable tableGuGuZhanBiInBk;
	private BanKuaiFengXiCategoryBarChartHuanShouLvPnl panelgghslup;
	private BanKuaiFengXiPieChartCjePnl pnllastestggzhanbi;
	private BanKuaiGeGuTable tablexuandingzhou;
	private BanKuaiGeGuTable tablexuandingminustwo; //new BanKuaiGeGuTable (this.stockmanager);
	private BanKuaiGeGuTable tablexuandingminusone;
	private BanKuaiGeGuTable tablexuandingplusone;
	private BanKuaiGeGuTable tablexuandingplustwo;
	private JTabbedPane tabbedPanegegu;
	private JTextField tfldparsedfile;
	private JTextArea tfldselectedmsg;
	private JStockComboBox cbxsearchbk;
	private JButton btnChosPasFile;
	private DisplayBkGgInfoEditorPane editorPanenodeinfo;
	private JScrollPane scrollPaneuserselctmsg;
	private JTabbedPane tabbedPanebk;
	private Action exportCancelAction;
	private Action bkfxCancelAction;
	private BanKuaiFengXiPieChartCjlPnl pnllastestggcjlzhanbi;
	private JButton btngephi;
	private JTextField tflddaochuriqi;
	private JTextArea tfldexportcondlists;
	private BanKuaiFengXiCategoryBarChartHuanShouLvPnl panelgghslmiddle;
	private BanKuaiFengXiCategoryBarChartHuanShouLvPnl panelgghsldown;
	
	private void initializeGui() {
		
		setTitle("\u677F\u5757\u5206\u6790");
		setBounds(100, 100, 1923, 1033);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		JPanel panel_1 = new JPanel();
		
		scrollPaneuserselctmsg = new JScrollPane();
		
		JPanel panel_2 = new JPanel();
//		paneldayCandle.setBorder(new TitledBorder(null, "\u677F\u5757/\u4E2A\u80A1K\u7EBF\u8D70\u52BF", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		
		JScrollPane scrollPane = new JScrollPane();
		
		panelgghslup = new BanKuaiFengXiCategoryBarChartHuanShouLvPnl();
		panelgghslup.setBorder(new TitledBorder(null, "\u677F\u5757\u6210\u4EA4\u989D\u5360\u6BD4", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(panel_1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 1139, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addGroup(Alignment.TRAILING, gl_contentPanel.createParallelGroup(Alignment.LEADING)
									.addGroup(gl_contentPanel.createSequentialGroup()
										.addGap(36)
										.addComponent(scrollPaneuserselctmsg, GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE))
									.addGroup(gl_contentPanel.createSequentialGroup()
										.addGap(36)
										.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 370, GroupLayout.PREFERRED_SIZE)))
								.addGroup(Alignment.TRAILING, gl_contentPanel.createSequentialGroup()
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 380, GroupLayout.PREFERRED_SIZE))))
						.addComponent(panelgghslup, GroupLayout.PREFERRED_SIZE, 1544, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addGap(20)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
						.addGroup(Alignment.LEADING, gl_contentPanel.createSequentialGroup()
							.addComponent(panelgghslup, GroupLayout.PREFERRED_SIZE, 281, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 642, GroupLayout.PREFERRED_SIZE))
						.addGroup(Alignment.LEADING, gl_contentPanel.createSequentialGroup()
							.addComponent(panel, GroupLayout.PREFERRED_SIZE, 162, GroupLayout.PREFERRED_SIZE)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addGap(150)
									.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 147, GroupLayout.PREFERRED_SIZE)
									.addGap(27)
									.addComponent(scrollPaneuserselctmsg, GroupLayout.PREFERRED_SIZE, 102, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 186, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addGap(6)
									.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 765, Short.MAX_VALUE)))))
					.addGap(58))
		);
		
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
		
		panelgghslmiddle = new BanKuaiFengXiCategoryBarChartHuanShouLvPnl();
		
		panelgghsldown = new BanKuaiFengXiCategoryBarChartHuanShouLvPnl();
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
						.addComponent(panelgghslmiddle, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(panelgghsldown, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_panel_2.setVerticalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addContainerGap()
					.addComponent(panelgghslmiddle, GroupLayout.PREFERRED_SIZE, 315, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panelgghsldown, GroupLayout.PREFERRED_SIZE, 315, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(277, Short.MAX_VALUE))
		);
		panel_2.setLayout(gl_panel_2);
		
		tabbedPanegegu = new JTabbedPane(JTabbedPane.TOP);
		
		tabbedPanebk = new JTabbedPane(JTabbedPane.TOP);
		
		cbxsearchbk = new JStockComboBox(4);

		cbxsearchbk.setEditable(true);
		
		
//		exportCancelAction = new AbstractAction("导出条件个股") {
//
//		      private static final long serialVersionUID = 4669650683189592364L;
//
//		      @Override
//		      public void actionPerformed(final ActionEvent e) {
//		        if (exporttask == null) {
//		        	exportBanKuaiWithGeGuOnCondition2();
//		        } else {
//		        	exporttask.cancel(true);
//		        }
//		      }
//		 };
		
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING)
						.addComponent(tabbedPanegegu, Alignment.LEADING)
						.addComponent(cbxsearchbk, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 164, GroupLayout.PREFERRED_SIZE)
						.addComponent(tabbedPanebk, 0, 0, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addComponent(cbxsearchbk, GroupLayout.PREFERRED_SIZE, 32, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(tabbedPanebk, GroupLayout.PREFERRED_SIZE, 382, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(tabbedPanegegu, GroupLayout.PREFERRED_SIZE, 329, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		
		sclpleft = new JScrollPane();
		tabbedPanebk.addTab("\u677F\u5757\u5360\u6BD4", null, sclpleft, null);
		
		tableBkZhanBi = new BanKuaiInfoTable(this.stockmanager,this.bkcyl);	
		
		sclpleft.setViewportView(tableBkZhanBi);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		tabbedPanebk.addTab("\u677F\u5757\u4FE1\u606F", null, scrollPane_2, null);
		
		editorPanenodeinfo = new DisplayBkGgInfoEditorPane();
		editorPanenodeinfo.setClearContentsBeforeDisplayNewInfo(true);
		scrollPane_2.setViewportView(editorPanenodeinfo);
		editorPanenodeinfo.setEditable(false);
		
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
		
//		bkfxCancelAction = new AbstractAction("重置") {
//
//		      private static final long serialVersionUID = 4669650683189592364L;
//
//		      @Override
//		      public void actionPerformed(final ActionEvent e) {
//		        if (bkfxtask == null) {
//		        	LocalDate cursettingdate = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//		        	if(!cursettingdate.equals(LocalDate.now())) {
//		        		LocalDate newdate =  LocalDate.now();
//						dateChooser.setDate(Date.from(newdate.atStartOfDay(ZoneId.systemDefault()).toInstant() ) );
//		        	} else
//		        		btnresetdate.setEnabled(false);
//		        } else {
//		        	bkfxtask.cancel(true);
//		        }
//		      }
//		    		
//		 };
		
		
		tfldparsedfile = new JTextField();
		tfldparsedfile.setForeground(Color.BLACK);
		tfldparsedfile.setEditable(false);
		tfldparsedfile.setColumns(10);
		
		btnChosPasFile = new JButton("\u9009\u62E9\u6587\u4EF6");
		btnChosPasFile.setForeground(Color.BLACK);
		
		btngephi = new JButton("\u4EA7\u751FGephi");
		btngephi.setForeground(Color.BLACK);
		
		JLabel label = new JLabel("\u5BFC\u51FA\u65E5\u671F");
		
		tflddaochuriqi = new JTextField();
		tflddaochuriqi.setEditable(false);
		tflddaochuriqi.setColumns(10);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING, false)
						.addComponent(scrollPane_1, Alignment.LEADING)
						.addGroup(Alignment.LEADING, gl_panel.createSequentialGroup()
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(tfldparsedfile, GroupLayout.PREFERRED_SIZE, 203, GroupLayout.PREFERRED_SIZE)
								.addGroup(gl_panel.createSequentialGroup()
									.addComponent(label)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(tflddaochuriqi, GroupLayout.PREFERRED_SIZE, 151, GroupLayout.PREFERRED_SIZE)))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(btngephi)
								.addComponent(btnChosPasFile))))
					.addContainerGap(13, Short.MAX_VALUE))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(tfldparsedfile, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnChosPasFile, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(label)
						.addComponent(tflddaochuriqi, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btngephi))
					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		
		tfldexportcondlists = new JTextArea();
		tfldexportcondlists.setEditable(false);
		tfldexportcondlists.setLineWrap(true);
		scrollPane_1.setViewportView(tfldexportcondlists);
		tfldexportcondlists.setColumns(10);
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
//			this.setHighLightChenJiaoEr ();
			
			
			GroupLayout gl_buttonPane = new GroupLayout(buttonPane);
			gl_buttonPane.setHorizontalGroup(
				gl_buttonPane.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_buttonPane.createSequentialGroup()
						.addGap(1540)
						.addComponent(okButton, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE)
						.addGap(54)
						.addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
						.addGap(553))
			);
			gl_buttonPane.setVerticalGroup(
				gl_buttonPane.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_buttonPane.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_buttonPane.createParallelGroup(Alignment.BASELINE)
							.addComponent(okButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(cancelButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
			);
			buttonPane.setLayout(gl_buttonPane);
		}
		

	
	}
}
	
