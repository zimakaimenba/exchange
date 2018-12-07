package com.exchangeinfomanager.bankuaifengxi.ai;

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
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockTree;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.DaPan;
import com.exchangeinfomanager.asinglestockinfo.HanYuPinYing;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.asinglestockinfo.Stock.StockNodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.StockGivenPeriodDataItem;
import com.exchangeinfomanager.asinglestockinfo.StockOfBanKuai;
import com.exchangeinfomanager.bankuaichanyelian.BanKuaiAndChanYeLian2;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiGeGuTable;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiGeGuTableModel;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiInfoTable;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiInfoTableModel;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.DisplayBkGgInfoEditorPane;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.CacheListener;
import com.exchangeinfomanager.bankuaifengxi.BanKuaiFengXi;
import com.exchangeinfomanager.bankuaifengxi.BanKuaiFengXiLargePnl;
import com.exchangeinfomanager.bankuaifengxi.BarChartHightLightFxDataValueListener;
import com.exchangeinfomanager.bankuaifengxi.BarChartPanelDataChangedListener;
import com.exchangeinfomanager.bankuaifengxi.BarChartPanelHightLightColumnListener;
import com.exchangeinfomanager.bankuaifengxi.BanKuaiFengXi.ExportCondition;
import com.exchangeinfomanager.bankuaifengxi.CandleStick.BanKuaiFengXiCandlestickPnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartCjePnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartCjeZhanbiPnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartCjlZhanbiPnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartPnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiNodeCombinedCategoryPnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBarOfName.BanKuaiFengXiCategoryBarChartHuanShouLvPnl;
import com.exchangeinfomanager.bankuaifengxi.Gephi.GephiFilesGenerator;
import com.exchangeinfomanager.bankuaifengxi.PieChart.BanKuaiFengXiPieChartCjePnl;
import com.exchangeinfomanager.bankuaifengxi.PieChart.BanKuaiFengXiPieChartCjlPnl;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.commonlib.JLocalDataChooser.JLocalDateChooser;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.gui.subgui.BanKuaiListEditorPane;
import com.exchangeinfomanager.gui.subgui.JStockComboBox;
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
import javax.swing.DefaultComboBoxModel;



public class WeeklyExportFileFengXi extends JDialog {

	/**
	 * Create the dialog.
	 */
	public WeeklyExportFileFengXi()
	{
//		this.sysconfig = SystemConfigration.getInstance();
		this.bkdbopt = new BanKuaiDbOperation ();

		initializeGui ();
		initializeShiZhiRangeLevel ();
		
		choosedate.setLocalDate(LocalDate.now());
		createEvents ();
	}
	public WeeklyExportFileFengXi(LocalDate serachdate,String stockcode)
	{
		tfldstockcode.setText(stockcode);
		
		
//		this.sysconfig = SystemConfigration.getInstance();
		this.bkdbopt = new BanKuaiDbOperation ();

		initializeGui ();
		initializeShiZhiRangeLevel ();
		
		choosedate.setLocalDate(serachdate);
		
		createEvents ();
	}
	

	private LocalDate lastselecteddate;
	
	private SystemConfigration sysconfig;
	private StockInfoManager stockmanager;
	private BanKuaiDbOperation bkdbopt;
	private static Logger logger = Logger.getLogger(BanKuaiFengXi.class);
	private ArrayList<ExportCondition> exportcond;
	private String globeperiod;
	
	private ArrayList<Double> shizhirangeLevel1;
	private ArrayList<Double> shizhirangeLevel2;
	private ArrayList<Double> shizhirangeLevel3;
	private ArrayList<Double> shizhirangeLevel4;
		
//	private Set<BarChartPanelHightLightColumnListener> chartpanelhighlightlisteners;
//	private Set<BarChartPanelDataChangedListener> barchartpanelbankuaidatachangelisteners;
//	private Set<BarChartPanelDataChangedListener> piechartpanelbankuaidatachangelisteners;
//	private Set<BarChartPanelDataChangedListener> barchartpanelstockofbankuaidatachangelisteners;
//	private Set<BarChartPanelDataChangedListener> barchartpanelstockdatachangelisteners;
//	private Set<BarChartHightLightFxDataValueListener> bkfxhighlightvalueslisteners;
	/*
	 * 
	 */
	private void initializeShiZhiRangeLevel ()
	{
		this.shizhirangeLevel1 = new  ArrayList<Double> ();
		this.shizhirangeLevel2 = new  ArrayList<Double> ();
		this.shizhirangeLevel3 = new  ArrayList<Double> ();
		this.shizhirangeLevel4 = new  ArrayList<Double> ();

		//0-30
		this.shizhirangeLevel1.add(30.0);
		//30-50
		this.shizhirangeLevel2.add(50.0);
		//50-150
		this.shizhirangeLevel3.add(90.0);
		this.shizhirangeLevel3.add(150.0);
		//150
		this.shizhirangeLevel4.add(300.0);
		this.shizhirangeLevel4.add(500.0);
		this.shizhirangeLevel4.add(700.0);
		this.shizhirangeLevel4.add(30000.0); //现在股市还没有30000亿的股票，这样就把所有市值的股票都包括了

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
		
		int stocknumber =0 ;
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
					stocknumber ++;
					stock = this.bkcyl.getStock(stock, lastselecteddate, period);
				}
			}
		}
		
		tfldexportcondlists.setText(tfldexportcondlists.getText() + "\n" + "共有个股:" + stocknumber + "个。");
		boolean zongshizhi = true;
		if(zongshizhi) {
			stocks.sort((node1, node2) -> { 
				 Double shizhi1 = ((StockNodeXPeriodData)node1.getNodeXPeroidData( period)).getSpecificTimeZongShiZhi(lastselecteddate, 0) ;
		         Double shizhi2 = ((StockNodeXPeriodData)node2.getNodeXPeroidData( period)).getSpecificTimeZongShiZhi(lastselecteddate, 0) ;
				 return shizhi1.compareTo(shizhi2);
			});

			dispalyStocksToLevel4PnlGuiByZongShiZhi (stocks,lastselecteddate,period);
			dispalyStocksToLevel3PnlGuiByZongShiZhi (stocks,lastselecteddate,period);
			dispalyStocksToLevel2PnlGuiByZongShiZhi (stocks,lastselecteddate,period);
			dispalyStocksToLevel1PnlGuiByZongShiZhi (stocks,lastselecteddate,period);
		} else {
			
		}
		
		stocks = null;
	}
	private void dispalyStocksToLevel4PnlGuiByZongShiZhi(ArrayList<Stock> stocks, LocalDate lastselecteddate2,
			String period) 
	{
		ArrayList<Stock> finialstocks = new ArrayList<Stock> ();
		Double maxshizhi = shizhirangeLevel4.get(shizhirangeLevel4.size()-1) * 100000000;
		Double minshizhi = shizhirangeLevel3.get(shizhirangeLevel3.size()-1) * 100000000;
		
		String title ="(";
		for(int i=0;i<shizhirangeLevel4.size();i++ ) {
			title = title + shizhirangeLevel4.get(i) + "亿,";
			Double upshizhi = shizhirangeLevel4.get(i) * 100000000;
			Double lowshizhi ;
			if(i == 0)
				lowshizhi = shizhirangeLevel3.get(shizhirangeLevel3.size()-1) * 100000000;
			else
				lowshizhi = shizhirangeLevel4.get(i-1) * 100000000;
			
			ArrayList<Stock> tmpstocks = new ArrayList<Stock> ();
			for(Stock stock : stocks) {
				StockNodeXPeriodData nodexdata = (StockNodeXPeriodData)stock.getNodeXPeroidData(period);
				Double zongshizhi = nodexdata.getSpecificTimeZongShiZhi(lastselecteddate2, 0);
				if(zongshizhi > minshizhi  && zongshizhi < upshizhi  && zongshizhi >= lowshizhi) {
					tmpstocks.add(stock);
				}
			}
			//开始对换手率排序
			Collections.sort(tmpstocks, new NodeHuanShouComparator(lastselecteddate2,0,period) );
			finialstocks.addAll(tmpstocks);
			tmpstocks = null;
		}
		//display to gui
		pnlgghsllevel4.updatedDate(finialstocks, shizhirangeLevel4, "zongshizhi", lastselecteddate2, period,minshizhi/100000000+ "亿以上" + title + ")");
	}
	private void dispalyStocksToLevel3PnlGuiByZongShiZhi(ArrayList<Stock> stocks, LocalDate lastselecteddate2,
			String period) 
	{
		ArrayList<Stock> finialstocks = new ArrayList<Stock> ();
		Double maxshizhi = shizhirangeLevel3.get(shizhirangeLevel3.size()-1) * 100000000;
		Double minshizhi = shizhirangeLevel2.get(shizhirangeLevel2.size()-1) * 100000000;
		
		String title ="(";
		for(int i=0;i<shizhirangeLevel3.size();i++ ) {
			title = title + shizhirangeLevel3.get(i) + "亿,";
			Double upshizhi = shizhirangeLevel3.get(i) * 100000000;
			Double lowshizhi ;
			if(i == 0)
				lowshizhi = shizhirangeLevel2.get(shizhirangeLevel2.size()-1) * 100000000;
			else
				lowshizhi = shizhirangeLevel3.get(i-1) * 100000000;
			
			ArrayList<Stock> tmpstocks = new ArrayList<Stock> ();
			for(Stock stock : stocks) {
				StockNodeXPeriodData nodexdata = (StockNodeXPeriodData)stock.getNodeXPeroidData(period);
				Double zongshizhi = nodexdata.getSpecificTimeZongShiZhi(lastselecteddate2, 0);
				if(zongshizhi < maxshizhi && zongshizhi >= minshizhi && zongshizhi < upshizhi  && zongshizhi >= lowshizhi) {
					tmpstocks.add(stock);
				}
			}
			//开始对换手率排序
			Collections.sort(tmpstocks, new NodeHuanShouComparator(lastselecteddate2,0,period) );
			finialstocks.addAll(tmpstocks);
			tmpstocks = null;
		}
		//display to gui
		pnlgghsllevel3.updatedDate(finialstocks, shizhirangeLevel3, "zongshizhi", lastselecteddate2, period,minshizhi/100000000 + "亿到" + maxshizhi/100000000+ "亿"+ title + ")");
		
	}
	/*
	 * 
	 */
	private void dispalyStocksToLevel2PnlGuiByZongShiZhi(ArrayList<Stock> stocks, LocalDate lastselecteddate2, String period)
	{
 		ArrayList<Stock> finialstocks = new ArrayList<Stock> ();
 		Double maxshizhi = shizhirangeLevel2.get(shizhirangeLevel2.size()-1) * 100000000;
 		Double minshizhi = shizhirangeLevel1.get(shizhirangeLevel1.size()-1) * 100000000;
 		
 		String title ="(";
		for(int i=0;i<shizhirangeLevel2.size();i++ ) {
			title = title + shizhirangeLevel2.get(i) + "亿,";
			Double upshizhi = shizhirangeLevel2.get(i) * 100000000;
			Double lowshizhi ;
			if(i == 0)
				lowshizhi = shizhirangeLevel1.get(shizhirangeLevel1.size()-1) * 100000000;
			else
				lowshizhi = shizhirangeLevel2.get(i-1) * 100000000;
			
			ArrayList<Stock> tmpstocks = new ArrayList<Stock> ();
			for(Stock stock : stocks) {
				StockNodeXPeriodData nodexdata = (StockNodeXPeriodData)stock.getNodeXPeroidData(period);
				Double zongshizhi = nodexdata.getSpecificTimeZongShiZhi(lastselecteddate2, 0);
				if(zongshizhi < maxshizhi && zongshizhi >=  lowshizhi
						&& zongshizhi < upshizhi  && zongshizhi >= lowshizhi) {
					tmpstocks.add(stock);
				}
			}
			//开始对换手率排序
			Collections.sort(tmpstocks, new NodeHuanShouComparator(lastselecteddate2,0,period) );
			finialstocks.addAll(tmpstocks);
			tmpstocks = null;
		}
		//display to gui
		pnlgghsllevel2.updatedDate(finialstocks, shizhirangeLevel2, "zongshizhi", lastselecteddate2, period,minshizhi/100000000 + "亿到" + maxshizhi/100000000 + "亿"+ title + ")");
	}
	/*
	 * 
	 */
	private void dispalyStocksToLevel1PnlGuiByZongShiZhi(ArrayList<Stock> stocks, LocalDate lastselecteddate2, String period)
	{
 		ArrayList<Stock> finialstocks = new ArrayList<Stock> ();
 		Double maxshizhi = shizhirangeLevel1.get(shizhirangeLevel1.size()-1) * 100000000 ;
 		
 		String title ="(";
		for(int i=0;i<shizhirangeLevel1.size();i++ ) {
			title = title + shizhirangeLevel1.get(i) + "亿,";
			Double upshizhi = shizhirangeLevel1.get(i) * 100000000;
			Double lowshizhi ;
			if(i == 0)
				lowshizhi = 0.0;
			else
				lowshizhi = shizhirangeLevel1.get(i-1) * 100000000;
			
			
			ArrayList<Stock> tmpstocks = new ArrayList<Stock> ();
			for(Stock stock : stocks) {
				StockNodeXPeriodData nodexdata = (StockNodeXPeriodData)stock.getNodeXPeroidData(period);
				Double zongshizhi = nodexdata.getSpecificTimeZongShiZhi(lastselecteddate2, 0);
				if(zongshizhi < maxshizhi  && zongshizhi < upshizhi  && zongshizhi >= lowshizhi) {
					tmpstocks.add(stock);
				}
			}
			//开始对换手率排序
			Collections.sort(tmpstocks, new NodeHuanShouComparator(lastselecteddate2,0,period) );
			finialstocks.addAll(tmpstocks);
			tmpstocks = null;
		}
		//display to gui
		pnlgghsllevel1.updatedDate(finialstocks, shizhirangeLevel1, "zongshizhi", lastselecteddate2, period, "0到" + maxshizhi/100000000 + "亿"+ title + ")");
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
		pnlgghsllevel1.updatedDate(finialstocks, shizhirangelist, "liutongshizhi", lastselecteddate2, period);
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
		barchartpanelbankuaidatachangelisteners.add(pnlgghsllevel1);
		//板块pie chart
		piechartpanelbankuaidatachangelisteners.add(pnllastestggzhanbi);
		//个股对板块
//		barchartpanelstockofbankuaidatachangelisteners.add(panelgegucje); //个股对于板块交易额,一般看个股对大盘的成交额，这个就不看了
		barchartpanelstockofbankuaidatachangelisteners.add(panelggbkcjezhanbi);
		barchartpanelstockdatachangelisteners.add(panelGgDpCjeZhanBi);
		//用户点击bar chart的一个column, highlight bar chart
		chartpanelhighlightlisteners.add(panelGgDpCjeZhanBi);
		chartpanelhighlightlisteners.add(panelggbkcjezhanbi);
		chartpanelhighlightlisteners.add(pnlgghsllevel1);
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
		btnappyshizhilevel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
//				tfldL1
			}
		});
		
		btnChosPasFile.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mouseClicked(MouseEvent arg0) {
				chooseParsedFile ();
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
	private JTextField tfldparsedfile;
	private JButton btnChosPasFile;
	private Action exportCancelAction;
	private Action bkfxCancelAction;
	private BanKuaiFengXiCategoryBarChartHuanShouLvPnl pnlgghsllevel3;
	private BanKuaiFengXiCategoryBarChartHuanShouLvPnl pnlgghsllevel4;
	private BanKuaiFengXiCategoryBarChartHuanShouLvPnl pnlgghsllevel2;
	private JTextField tfldszfj;
	private JButton btnappyshizhilevel;
	private JTable table;
	private JTextField tfldstockcode;
	private JButton btnsearchstock;
	private JButton button;
	private JButton button_1;
	private JButton button_2;
	private JLocalDateChooser choosedate;
	private JTextArea textArea;
	private JComboBox<String> comboBox_1;
	private JCheckBox chckbxNewCheckBox;
	
	private void initializeGui() {
		
		setTitle("\u677F\u5757\u5206\u6790");
		setBounds(100, 100, 1550, 900);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.NORTH);
		
		JPanel panel = new JPanel();
		{
			okButton = new JButton("OK");
			okButton.setActionCommand("OK");
			getRootPane().setDefaultButton(okButton);
		}
		
		pnlgghsllevel3 = new BanKuaiFengXiCategoryBarChartHuanShouLvPnl();
		pnlgghsllevel3.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		pnlgghsllevel4 = new BanKuaiFengXiCategoryBarChartHuanShouLvPnl();
		pnlgghsllevel4.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		pnlgghsllevel2 = new BanKuaiFengXiCategoryBarChartHuanShouLvPnl();
		pnlgghsllevel2.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		JLabel lblLevel = new JLabel("\u5E02\u503C\u5206\u7EA7(\u4EBF)");
		lblLevel.setToolTipText("\u786E\u4FDD\u6570\u5B57\u6309\u5927\u5C0F\u6392\u5217,\u9017\u53F7\u9694\u5F00\uFF01");
		
		tfldszfj = new JTextField();
		tfldszfj.setText("20,30,50,200,400,800,");
		tfldszfj.setToolTipText("\u786E\u4FDD\u6570\u5B57\u6309\u5927\u5C0F\u6392\u5217,\u9017\u53F7\u9694\u5F00\uFF01");
		tfldszfj.setColumns(10);
		
		btnappyshizhilevel = new JButton("\u5E94\u7528");
		btnappyshizhilevel.setToolTipText("\u786E\u4FDD\u6570\u5B57\u6309\u5927\u5C0F\u6392\u5217,\u9017\u53F7\u9694\u5F00\uFF01");
		
		btnappyshizhilevel.setEnabled(false);
		
		JComboBox<String> comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"\u6D41\u901A\u5E02\u503C", "\u603B\u5E02\u503C"}));
		
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
		
		comboBox_1 = new JComboBox();
		comboBox_1.setModel(new DefaultComboBoxModel(new String[] {"\u6D41\u901A\u5E02\u503C", "\u603B\u5E02\u503C"}));
		
		JButton btnaddlevel = new JButton("\u6DFB\u52A0\u65B0\u5206\u7EA7");
		
		JButton btndel = new JButton("\u5220\u9664\u6307\u5B9A\u5206\u7EA7");
		
		JScrollPane scrollPane = new JScrollPane();
		
		chckbxNewCheckBox = new JCheckBox("\u6BCF\u5468\u5206\u6790\u6587\u4EF6");
		
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(comboBox_1, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblLevel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(tfldszfj, GroupLayout.PREFERRED_SIZE, 231, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addGap(784)
									.addComponent(btnappyshizhilevel)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(tfldparsedfile, GroupLayout.PREFERRED_SIZE, 201, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(btnChosPasFile)
									.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addComponent(okButton, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING, false)
										.addComponent(pnlgghsllevel4, Alignment.LEADING, 0, 0, Short.MAX_VALUE)
										.addComponent(pnlgghsllevel3, Alignment.LEADING, 0, 0, Short.MAX_VALUE))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btnaddlevel)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btndel)
									.addGap(198)
									.addComponent(chckbxNewCheckBox))))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(panel, GroupLayout.PREFERRED_SIZE, 273, GroupLayout.PREFERRED_SIZE)
							.addGap(14)
							.addComponent(pnlgghsllevel2, GroupLayout.PREFERRED_SIZE, 1223, GroupLayout.PREFERRED_SIZE))
						.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 275, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(panel, GroupLayout.PREFERRED_SIZE, 621, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 177, GroupLayout.PREFERRED_SIZE))
						.addComponent(pnlgghsllevel2, GroupLayout.PREFERRED_SIZE, 244, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
							.addComponent(comboBox_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(lblLevel)
							.addComponent(tfldszfj, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(btnaddlevel)
							.addComponent(btndel)
							.addComponent(chckbxNewCheckBox))
						.addGroup(Alignment.TRAILING, gl_contentPanel.createSequentialGroup()
							.addGap(249)
							.addComponent(pnlgghsllevel3, GroupLayout.PREFERRED_SIZE, 232, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(pnlgghsllevel4, GroupLayout.PREFERRED_SIZE, 234, GroupLayout.PREFERRED_SIZE)
							.addGap(5)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
								.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(okButton)
								.addComponent(btnChosPasFile, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
								.addComponent(tfldparsedfile, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnappyshizhilevel))))
					.addContainerGap())
		);
		
		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		
		tfldstockcode = new JTextField();
		tfldstockcode.setColumns(10);
		
		btnsearchstock = new JButton("\u6DFB\u52A0\u4E2A\u80A1");
		
		choosedate = new JLocalDateChooser();
		
		button = new JButton("<");
		
		button_1 = new JButton(">");
		
		button_2 = new JButton("\u91CD\u7F6E");
		
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)
						.addGroup(gl_panel.createSequentialGroup()
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING, false)
								.addGroup(gl_panel.createSequentialGroup()
									.addComponent(choosedate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(button))
								.addComponent(tfldstockcode))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
								.addGroup(gl_panel.createSequentialGroup()
									.addComponent(button_1)
									.addGap(18)
									.addComponent(button_2))
								.addComponent(btnsearchstock, GroupLayout.PREFERRED_SIZE, 102, GroupLayout.PREFERRED_SIZE))))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(choosedate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
							.addComponent(button)
							.addComponent(button_1)
							.addComponent(button_2)))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(tfldstockcode, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnsearchstock, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 528, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(81, Short.MAX_VALUE))
		);
		
		table = new JTable();
		scrollPane_1.setViewportView(table);
		panel.setLayout(gl_panel);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
			
			buttonPane.add(comboBox_1);
			buttonPane.add(lblLevel);
			buttonPane.add(tfldszfj);
			buttonPane.add(btnaddlevel);
			buttonPane.add(btndel);
			
			buttonPane.add(chckbxNewCheckBox);
			buttonPane.add(tfldparsedfile);
		}
		

	
	}
}
	
