package com.exchangeinfomanager.bankuaifengxi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
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
import java.time.LocalDate;
import java.time.ZoneId;
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
import javax.swing.SwingWorker;
import javax.swing.SwingWorker.StateValue;
import javax.swing.JProgressBar;

public class BanKuaiFengXi extends JDialog {

	/**
	 * Create the dialog.
	 */
	public BanKuaiFengXi(StockInfoManager stockmanager1, BanKuaiAndChanYeLian bkcyl2)
	{
		this.stockmanager = stockmanager1;
		this.bkcyl = bkcyl2;
		this.sysconfig = SystemConfigration.getInstance();
		this.bkdbopt = new BanKuaiDbOperation ();
		
		initializeGui ();
		createEvents ();
		setUpChartDataListeners ();

		initializePaoMaDeng ();
		
		this.dateChooser.setDate(new Date ());
	}
	
	private LocalDate lastselecteddate;
	private BanKuaiAndChanYeLian bkcyl;
	private SystemConfigration sysconfig;
	private StockInfoManager stockmanager;
	private HashSet<String> stockinfile;
	private BanKuaiDbOperation bkdbopt;
	private static Logger logger = Logger.getLogger(BanKuaiFengXi.class);
	private ExportTask2 exporttask;
	private BanKuaiPaiXuTask bkfxtask;
	private ArrayList<ExportCondition> exportcond;
	private String globeperiod;
	
	private Set<BarChartPanelHightLightListener> chartpanelhighlightlisteners;
	private Set<BarChartPanelDataChangedListener> barchartpanelbankuaidatachangelisteners;
	private Set<BarChartPanelDataChangedListener> piechartpanelbankuaidatachangelisteners;
	private Set<BarChartPanelDataChangedListener> barchartpanelstockofbankuaidatachangelisteners;
	private Set<BarChartPanelDataChangedListener> barchartpanelstockdatachangelisteners;
	private Set<BkfxHightLightValueListener> bkfxhighlightvalueslisteners;
	
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
		barchartpanelstockofbankuaidatachangelisteners.add(panelgegucje); //个股对于板块交易额
		barchartpanelstockofbankuaidatachangelisteners.add(panelgeguwkzhanbi);
		//独立个股本身
//		barchartpanelstockdatachangelisteners.add(paneldayCandle);
		barchartpanelstockdatachangelisteners.add(pnlggdpcje);
		barchartpanelstockdatachangelisteners.add(panelGeguDapanZhanBi);
		//用户点击bar chart的一个column, highlight bar chart
		chartpanelhighlightlisteners.add(panelGeguDapanZhanBi);
		chartpanelhighlightlisteners.add(panelgegucje);
		chartpanelhighlightlisteners.add(panelgeguwkzhanbi);
		chartpanelhighlightlisteners.add(panelbkcje);
		chartpanelhighlightlisteners.add(pnlggdpcje);
		chartpanelhighlightlisteners.add(panelbkwkzhanbi);
		
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
	private void initializeBanKuaiZhanBiByGrowthRate2 (String period)
	{	
		this.globeperiod = period;
    	LocalDate curselectdate = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    	
    	bkfxtask = new BanKuaiPaiXuTask(this.bkcyl,  curselectdate,globeperiod);
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
		              final int count = bkfxtask.get();
//		          	((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).refresh(bkhascjl,curselectdate,period);
		              progressBarsys.setIndeterminate(false);
		              progressBarsys.setString(" ");
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
		
		String exportfilename = sysconfig.getTDXModelMatchExportFile ()+ curselectdate.with(DayOfWeek.FRIDAY).toString().replaceAll("-","") + ".EBK";
		File filefmxx = new File( exportfilename );
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

		exporttask = new ExportTask2(this.bkcyl,  exportcond, curselectdate,globeperiod,filefmxx);
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
		              		              
		              int exchangeresult = JOptionPane.showConfirmDialog(null, "个股导出成功，请在" + filefmxx.getAbsolutePath() + "下查看！是否打开该目录？","导出完毕", JOptionPane.OK_CANCEL_OPTION);
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
		        	  exportCancelAction.putValue(Action.NAME, "取消导出个股");
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
		panelgeguwkzhanbi.resetDate();
		panelselectwkgeguzhanbi.resetDate();
		panelLastWkGeGuZhanBi.resetDate();
		panelGeguDapanZhanBi.resetDate();
		panelgegucje.resetDate();
		tabbedPanegegu.setTitleAt(1, "选定周");
		tfldselectedmsg.setText("");
		tabbedPanebk.setSelectedIndex(0);
		paneldayCandle.resetDate();
		pnlggdpcje.resetDate();
		
		((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).deleteAllRows();
		((BanKuaiGeGuTableModel)tablexuandingzhou.getModel()).deleteAllRows();
		((BanKuaiGeGuTableModel)tablexuandingminusone.getModel()).deleteAllRows();
		((BanKuaiGeGuTableModel)tablexuandingminustwo.getModel()).deleteAllRows();
		((BanKuaiGeGuTableModel)tablexuandingplusone.getModel()).deleteAllRows();
		((BanKuaiGeGuTableModel)tablexuandingplustwo.getModel()).deleteAllRows();
		
		LocalDate curselectdate = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		//板块自身占比
		final BanKuai selectbk = selectedbk;
		barchartpanelbankuaidatachangelisteners.forEach(l -> l.updatedDate(selectbk, curselectdate, 0,globeperiod));
    	
//		setDisplayNoticeLevelForPanels();
		
		//更新板块所属个股
		if(selectedbk.getBanKuaiLeiXing().equals(BanKuai.HASGGWITHSELFCJL)) { //有个股才需要更新，有些板块是没有个股的
			
			selectedbk = bkcyl.getAllGeGuOfBanKuai (selectedbk,period);
			
			((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).refresh(selectedbk, curselectdate,period);
			
			//更改显示日期
			tabbedPanegegu.setTitleAt(0, "当前周" + curselectdate);
			
			//显示2周的板块个股pie chart
			piechartpanelbankuaidatachangelisteners.forEach(l -> l.updatedDate(selectbk, curselectdate, 0,globeperiod));
		}
	}
	/*
	 * 设置成交量或者占比的提示
	 */
	private void setDisplayNoticeLevelForPanels() 
	{
		panelbkwkzhanbi.setDaZiJinValueMarker (0.01); //大于1%是板块强势的表现
		panelgegucje.setDaZiJinValueMarker ( 580000000); //5.8亿
		panelgegucje.setDaZiJinValueMarker (1000000000);
		panelgegucje.setDaZiJinValueMarker (1500000000);
		panelgegucje.setDaZiJinValueMarker (2000000000);
		
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
//		if( checkDateBetweenBanKuaiDate (selectedbk,selectdate2) ) //板块要有该周数据
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
//			DaPan dapan =  (DaPan) bkcyl.getBkChanYeLianTree().getSpecificNodeByHypyOrCode("000000");
			
			panelgeguwkzhanbi.resetDate();
			panelGeguDapanZhanBi.resetDate();
			panelgegucje.resetDate();
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
		
			Comparable datekey = panelbkwkzhanbi.getCurSelectedBarDate ();
			if(datekey != null) 
				chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(datekey));

	}
	/*
	 * 用户选择个股后，显示个股日线K线走势已经个股自身对大盘的各种占比
	 */
	private void refreshGeGuKXianZouShi (BkChanYeLianTreeNode selectstock2)
	{
		LocalDate curselectdate = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate requireend = curselectdate.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = curselectdate.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange(),ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		
		paneldayCandle.resetDate();
		
		Stock selectstock = (Stock)bkcyl.getStock(selectstock2.getMyOwnCode(),curselectdate,StockGivenPeriodDataItem.WEEK);
		selectstock = (Stock)bkcyl.getStockK(selectstock2.getMyOwnCode(),curselectdate,StockGivenPeriodDataItem.DAY);
		
		final Stock stock = selectstock;
		barchartpanelstockdatachangelisteners.forEach(l -> l.updatedDate(stock, curselectdate, 0,globeperiod));
		paneldayCandle.setNodeCandleStickDate(selectstock,requirestart,requireend,StockGivenPeriodDataItem.DAY);
	}
	/*
	 * 用户选择个股周成交量某个数据后，显示该数据前后50个交易日的日线K线走势
	 */
	private void refreshGeGuKXianZouShiOfFiftyDays (BkChanYeLianTreeNode selectstock2,LocalDate selectedday)
	{
		LocalDate requirestart = selectedday.with(DayOfWeek.MONDAY).minus(2,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		LocalDate requireend = selectedday.with(DayOfWeek.MONDAY).plus(3,ChronoUnit.MONTHS).with(DayOfWeek.FRIDAY);
		
		paneldayCandle.resetDate();

		BkChanYeLianTreeNode selectstock = bkcyl.getStock(selectstock2.getMyOwnCode(),requireend,StockGivenPeriodDataItem.DAY);
		paneldayCandle.setNodeCandleStickDate(selectstock,requirestart,requireend,selectedday,StockGivenPeriodDataItem.DAY);
	}
	
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
	     		
	     
//	     String stockcode = formatStockCode((String)cBxstockcode.getSelectedItem());
//	     ArrayList<String> gegucyl = ((Stock)nodeshouldbedisplayed).getGeGuAllChanYeLianInfo();
//	     for(String cyl : gegucyl) {
//	    	 htmlstring += " <p>个股产业链:"
//	    	 		+ "<a  href=\"openBanKuaiAndChanYeLianDialog\">  " + cyl
//	    	 		;
//	     }
//	     if(gegucyl.size()>0)
//	    	 htmlstring += "</p>";
	     
	     htmlstring += "</body>"
					+ "</html>";
	    	 
	     editorPanebankuai.setText(htmlstring);
	     editorPanebankuai.setCaretPosition(0);
	     
	}
	/*
	 * 
	 */
	public void setParsedFile(String file) 
	{
		this.tfldparsedfile.setText(file );
		BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)this.bkcyl.getBkChanYeLianTree().getModel().getRoot();
		stockinfile = treeroot.getNodetreerelated().getParseFileStockSet(); 
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

	private void refreshBanKuaiGeGuTableHightLight ()
	{
		Integer cjemaxwk; Integer bkmaxwk;Integer dpmaxwk ;Double showcje;
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
		
		bkfxhighlightvalueslisteners.forEach(l -> l.hightLightValues(dpmaxwk, bkmaxwk, showcje, cjemaxwk) );
	}

	private void createEvents() 
	{
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
				if( Strings.isNullOrEmpty(tfldparsedfile.getText() )) {
					JOptionPane.showMessageDialog(null,"请选择每日板块文件！");
					ckboxparsefile.setSelected(false);
					return;
				}
			
				if(ckboxparsefile.isSelected()) {
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
		
		//显示选中的那一周的个股占比比例
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
    	        	displayNodeLargerPeriodData (bkcur);
    	        }
    	        
    	        if (me.getClickCount() == 1) {
    	        	//显示选择的tooltip
    				String tooltip = panelbkwkzhanbi.getToolTipSelected ();
    				if(tooltip == null)
    					return;
    				
    				//显示选择周的分析记录等
    				ArrayList<JiaRuJiHua> fxjg = panelbkwkzhanbi.getCurSelectedFengXiJieGuo ();
    				setUserSelectedColumnMessage(tooltip,fxjg);
    			
    				//显示选择周的个股成交额占比
    				String selectdate = panelbkwkzhanbi.getCurSelectedBarDate().toString();
    				LocalDate selectdate1 = CommonUtility.formateStringToDate(selectdate);
    				
    				if(bkcur.getBanKuaiLeiXing().equals(BanKuai.HASGGWITHSELFCJL) ) {//应该是有个股的板块点击才显示她的个股， 
    					panelselectwkgeguzhanbi.setBanKuaiCjeNeededDisplay(bkcur,Integer.parseInt(tfldweight.getText() ), selectdate1 ,globeperiod );
    					
    					//显示选中周股票占比增加率排名等
    					refreshSpecificBanKuaiFengXiResult (bkcur,selectdate1,globeperiod);
    				}
    				
    				//同步几个panel
    				Comparable datekey = panelbkwkzhanbi.getCurSelectedBarDate ();
    				chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(datekey));
    	        }
			}

			@Override
			public void chartMouseMoved(ChartMouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			
		});
		
		//同步几个panel
		panelGeguDapanZhanBi.getChartPanel().addChartMouseListener(new ChartMouseListener() { 
			@Override
			public void chartMouseClicked(ChartMouseEvent e)  
			{
				BanKuai bkcur = null;
				int row = tableBkZhanBi.getSelectedRow();
				if(row != -1) {
					int modelRow = tableBkZhanBi.convertRowIndexToModel(row);
					bkcur = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(modelRow);
				}
				
				StockOfBanKuai selectstock = null;
				int stockrow = tableGuGuZhanBiInBk.getSelectedRow();
				if(stockrow != -1) {
					int modelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(stockrow);
					 selectstock = ((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).getStock(modelRow);
				}
					
				
				java.awt.event.MouseEvent me = e.getTrigger();
    	        if (me.getClickCount() == 2) {
    	        	displayNodeLargerPeriodData (selectstock.getStock());
    	        }
    	        
    	        if (me.getClickCount() == 1) {
    	        	//显示选择的tooltip
    				String tooltip = panelGeguDapanZhanBi.getToolTipSelected ();
    				if(tooltip == null)
    					return;
    				
    				//显示选择周的分析记录等
    				ArrayList<JiaRuJiHua> fxjg = panelGeguDapanZhanBi.getCurSelectedFengXiJieGuo ();
    				setUserSelectedColumnMessage(tooltip,fxjg);
    				//
    				Comparable datekey = panelGeguDapanZhanBi.getCurSelectedBarDate ();
    				chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(datekey));
    				
    				//显示个股在选择的日期前后50个交易日的走势
    				LocalDate selecteddate = CommonUtility.formateStringToDate(datekey.toString());
    				refreshGeGuKXianZouShiOfFiftyDays (selectstock,selecteddate);
    	        }
			}

			@Override
			public void chartMouseMoved(ChartMouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		panelbkcje.getChartPanel().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				//显示选择的tooltip
				String tooltip = panelbkcje.getToolTipSelected ();
				if(tooltip == null)
					return;
				
				//显示选择周的分析记录等
				ArrayList<JiaRuJiHua> fxjg = panelbkcje.getCurSelectedFengXiJieGuo ();
				setUserSelectedColumnMessage(tooltip,fxjg);
				
     			//
				Comparable datekey = panelbkcje.getCurSelectedBarDate ();
				chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(datekey));

//				////显示选择周的分析记录
//				String selectdate = datekey.toString();
//				LocalDate selectdate1 = CommonUtility.formateStringToDate(selectdate);
				
			}
		});
		
		pnlggdpcje.getChartPanel().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				//显示选择的tooltip
				String tooltip = pnlggdpcje.getToolTipSelected ();
				if(tooltip == null)
					return;
				
				//显示选择周的分析记录等
				ArrayList<JiaRuJiHua> fxjg = pnlggdpcje.getCurSelectedFengXiJieGuo ();
				setUserSelectedColumnMessage(tooltip,fxjg);
				
				//
				Comparable datekey = pnlggdpcje.getCurSelectedBarDate ();
				chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(datekey));
				
				//显示个股在选择的日期前后50个交易日的走势
				int row = tableGuGuZhanBiInBk.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择一个股票！","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				int modelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(row);
				StockOfBanKuai selectstock = ((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).getStock (modelRow);
				LocalDate selecteddate = CommonUtility.formateStringToDate(datekey.toString());
				refreshGeGuKXianZouShiOfFiftyDays (selectstock.getStock(),selecteddate);
			}
		});
		
		panelgegucje.getChartPanel().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				//显示选择的tooltip
				String tooltip = panelgegucje.getToolTipSelected ();
				if(tooltip == null)
					return;
				
				//显示选择周的分析记录等
				ArrayList<JiaRuJiHua> fxjg = panelgegucje.getCurSelectedFengXiJieGuo ();
				setUserSelectedColumnMessage(tooltip,fxjg);
				
				//
				Comparable datekey = panelgegucje.getCurSelectedBarDate ();
				chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(datekey));
				
//				//显示个股在选择的日期前后50个交易日的走势
//				int row = tableGuGuZhanBiInBk.getSelectedRow();
//				if(row <0) {
//					JOptionPane.showMessageDialog(null,"请选择一个股票！","Warning",JOptionPane.WARNING_MESSAGE);
//					return;
//				}
//				
//				int modelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(row);
//				StockOfBanKuai selectstock = ((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).getStock (modelRow);
//				LocalDate selecteddate = CommonUtility.formateStringToDate(datekey.toString());
//				refreshGeGuKXianZouShiOfFiftyDays (selectstock.getStock(),selecteddate);
			}
		});
		
		panelgeguwkzhanbi.getChartPanel().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				//显示选择的tooltip
				String tooltip = panelgeguwkzhanbi.getToolTipSelected ();
				if(tooltip == null)
					return;
				
				//显示选择周的分析记录等
				ArrayList<JiaRuJiHua> fxjg = panelgeguwkzhanbi.getCurSelectedFengXiJieGuo ();
				setUserSelectedColumnMessage(tooltip,fxjg);
				
			
				Comparable datekey = panelgeguwkzhanbi.getCurSelectedBarDate ();
				chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(datekey));
				
				//显示个股在选择的日期前后50个交易日的走势
				int row = tableGuGuZhanBiInBk.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择一个股票！","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				int modelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(row);
				StockOfBanKuai selectstock = ((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).getStock (modelRow);
				LocalDate selecteddate = CommonUtility.formateStringToDate(datekey.toString());
				refreshGeGuKXianZouShiOfFiftyDays (selectstock.getStock(),selecteddate);
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
					int modelRow = tableGuGuZhanBiInBk.convertRowIndexToView(rowindex);
					
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
		    			panelgegucje.resetDate ();
		    			panelbkwkzhanbi.resetDate();
			    		panelgeguwkzhanbi.resetDate();
			    		pnllastestggzhanbi.resetDate();
			    		panelLastWkGeGuZhanBi.resetDate();
			    		panelselectwkgeguzhanbi.resetDate();
			    		panelGeguDapanZhanBi.resetDate();
			    		((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).deleteAllRows();
			    		((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).deleteAllRows();
			    		tabbedPanegegu.setTitleAt(0, "当前周");
			    		tabbedPanegegu.setTitleAt(1, "选定周");
			    		tabbedPanebk.setSelectedIndex(0);
			    		setHighLightChenJiaoEr();
			    		
			    		initializeBanKuaiZhanBiByGrowthRate2 (StockGivenPeriodDataItem.WEEK);
			    		
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
					patchParsedFile (selectedbk);
					cbxsearchbk.updateUserSelectedNode(selectedbk);
					
					//找到该股票
					String stockcode = cbxstockcode.getSelectedItem().toString().substring(0, 6);
					if(!findInputedNodeInTable (stockcode) )
						JOptionPane.showMessageDialog(null,"在某个个股表内没有发现该股，可能在这个时间段内该股停牌","Warning",JOptionPane.WARNING_MESSAGE);
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
					refreshGeGuKXianZouShi (selectstock); //K线
					cbxstockcode.updateUserSelectedNode (selectstock.getStock()); 
					displayStockSuoShuBanKuai((Stock)cbxstockcode.updateUserSelectedNode (selectstock.getStock()));
					
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
				patchParsedFile (selectedbk);
				//装配每日模型文件
			}
		});
	}
	/*
	 * 用户双击某个板块占比chart，则放大显示该node一年内的占比所有数据
	 */
	protected void displayNodeLargerPeriodData(BkChanYeLianTreeNode node) 
	{
		LocalDate curselectdate = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate requireend = curselectdate.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange(),ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		LocalDate requirestart = requireend.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange(),ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		
		if(node.getType() == BanKuaiAndStockBasic.TDXBK) {
			node = this.bkcyl.getBanKuai((BanKuai)node, requirestart.plusWeeks(1),globeperiod);
		} else if(node.getType() == BanKuaiAndStockBasic.TDXGG) {
			node = this.bkcyl.getStock(node.getMyOwnCode(), requirestart.plusWeeks(1),globeperiod);
		} else if(node.getType() == BanKuaiAndStockBasic.BKGEGU) {
			BanKuai bk = ((StockOfBanKuai)node).getBanKuai();
			this.bkcyl.getBanKuai((BanKuai)bk, requirestart.plusWeeks(1),globeperiod);
			node = this.bkcyl.getGeGuOfBanKuai(bk, node.getMyOwnCode() ,globeperiod);
		} 
		
		NodeXPeriodDataBasic nodexdate = node.getNodeXPeroidData(globeperiod);
		
		BanKuaiFengXiBarCjeZhanBiLargePeriodChartPnl largeinfo = new BanKuaiFengXiBarCjeZhanBiLargePeriodChartPnl (node,nodexdate.getRecordsEndDate(),globeperiod);
		int exchangeresult = JOptionPane.showConfirmDialog(null, largeinfo, "大周期分析结果", JOptionPane.OK_CANCEL_OPTION);
		if(exchangeresult == JOptionPane.CANCEL_OPTION)
			return;
	}
	/*
	 * 用户双击某个板块的个股chart，则放大显示该node一年内的占比所有数据
	 */
//	protected void displayNodeLargerPeriodData(BanKuai bkcur,Stock stock) 
//	{
//		LocalDate curselectdate = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//		LocalDate requireend = curselectdate.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange(),ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
//		LocalDate requirestart = requireend.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange(),ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
//		
//		bkcur = this.bkcyl.getBanKuai(bkcur, requirestart.plusWeeks(1),globeperiod); //先把板块的数据扩大到相应的时间
//		stock = this.bkcyl.getGeGuOfBanKuai(bkcur, stock.getMyOwnCode(),globeperiod); //个股会根据板块的时间跨度查找数据
//		NodeXPeriodDataBasic nodexdate = bkcur.getNodeXPeroidData(globeperiod);
//		
////		DaPan dapan =  (DaPan) bkcyl.getBkChanYeLianTree().getSpecificNodeByHypyOrCode("000000");
//		BanKuaiFengXiBarGgDpLargePeriodChartPnl largeinfo = new BanKuaiFengXiBarGgDpLargePeriodChartPnl (stock,nodexdate.getRecordsEndDate(),globeperiod);
//		int exchangeresult = JOptionPane.showConfirmDialog(null, largeinfo, "个股一年分析结果", JOptionPane.OK_CANCEL_OPTION);
//		if(exchangeresult == JOptionPane.CANCEL_OPTION)
//			return;
//	}

	protected void initializeExportConditions() 
	{
		String exportcjelevel = null;
		String exportdpmaxwklevel = null;
		String exportbkmaxwklevel = null;
		String exportcjemaxwklevel = null;

		if(ckboxshowcje.isSelected())
			exportcjelevel = tfldshowcje.getText();
		if(cbxdpmaxwk.isSelected())
			exportdpmaxwklevel = tflddisplaydpmaxwk.getText();
		if(cbxbkmaxwk.isSelected())
			exportbkmaxwklevel = tfldbkmaxwk.getText();
		if(chkcjemaxwk.isSelected())
			exportcjemaxwklevel = tfldcjemaxwk.getText();
	
		ExportCondition expc = new ExportCondition (exportcjelevel,exportcjemaxwklevel,exportdpmaxwklevel,exportbkmaxwklevel);
		exportcond.add(expc);
		
		String htmlstring = btnaddexportcond.getToolTipText();
		 org.jsoup.nodes.Document doc = Jsoup.parse(htmlstring);
		 logger.debug(doc.toString());
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

	protected void patchParsedFile(BanKuai selectedbk) 
	{
		if( stockinfile != null &&  stockinfile.size()>0)
 	    	 this.bkcyl.getBkChanYeLianTree().updateParseFileInfoToTreeFromSpecificNode (selectedbk,stockinfile,dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate() );
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
		    
//		    parseSelectedBanKuaiFile (linuxpath);
		}
		
	}
	private void parseSelectedBanKuaiFile(String linuxpath) 
	{
		File parsefile = new File(linuxpath);
    	if(!parsefile.exists() )
    		return;
    	
    	List<String> readparsefileLines = null;
		try {
			readparsefileLines = Files.readLines(parsefile,Charsets.UTF_8,new ParseBanKuaiFielProcessor ());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		stockinfile = new HashSet<String> (readparsefileLines);
		BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)this.bkcyl.getBkChanYeLianTree().getModel().getRoot();
		treeroot.getNodetreerelated().setParseFileStockSet(stockinfile);
		
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

	private final JPanel contentPanel = new JPanel();
	private JButton okButton;
	private JButton cancelButton;
	private JStockCalendarDateChooser dateChooser; //https://toedter.com/jcalendar/
	private JScrollPane sclpleft;
	private BanKuaiInfoTable tableBkZhanBi;
//	private JTable tableGuGuZhanBiInBk;
	private BanKuaiGeGuTable tableGuGuZhanBiInBk;
	private BanKuaiFengXiBarChartGgCjeZhanbiPnl panelbkwkzhanbi;
	private JTextField tfldweight;
	private BanKuaiFengXiBarChartGgCjeZhanbiPnl panelgeguwkzhanbi;
	private BanKuaiFengXiPieChartCjePnl pnllastestggzhanbi;
	private BanKuaiFengXiPieChartCjePnl panelLastWkGeGuZhanBi;
	private BanKuaiFengXiPieChartCjePnl panelselectwkgeguzhanbi;
	private BanKuaiListEditorPane editorPanebankuai;
	private JButton btnresetdate;
	private JButton btnsixmonthbefore;
	private JButton btnsixmonthafter;
	private BanKuaiGeGuTable tablexuandingzhou;
	private JStockComboBox cbxstockcode;
	private BanKuaiFengXiBarChartCjePnl panelbkcje;
	private BanKuaiFengXiBarChartCjePnl panelgegucje;
	private BanKuaiGeGuTable tablexuandingminustwo; //new BanKuaiGeGuTable (this.stockmanager);
	private BanKuaiGeGuTable tablexuandingminusone;
	private BanKuaiGeGuTable tablexuandingplusone;
	private BanKuaiGeGuTable tablexuandingplustwo;
	private JTabbedPane tabbedPanegegu;
	private BanKuaiFengXiBarChartGgCjeZhanbiPnl panelGeguDapanZhanBi;
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
	private BanKuaiFengXiBarChartGgCjlZhanbiPnl panelggdpcjlwkzhanbi;
	private BanKuaiFengXiPieChartCjlPnl pnllastestggcjlzhanbi;
	private JTabbedPane tabbedPane_1;
	private BanKuaiFengXiPieChartCjlPnl panelselectwkgegucjlzhanbi;
	private JButton btnaddexportcond;
	private JButton btnClear;
	private JProgressBar progressBarsys;
	private JCheckBox cbxggquanzhong;
	private BanKuaiFengXiBarChartCjePnl pnlggdpcje;
	
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
		paneldayCandle.setBorder(new TitledBorder(null, "\u677F\u5757/\u4E2A\u80A1K\u7EBF\u8D70\u52BF", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
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
		
		panelbkcje = new BanKuaiFengXiBarChartCjePnl();
		panelbkcje.setBorder(new TitledBorder(null, "\u677F\u5757\u6210\u4EA4\u989D", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		panelbkwkzhanbi = new BanKuaiFengXiBarChartGgCjeZhanbiPnl();
		panelbkwkzhanbi.setBorder(new TitledBorder(null, "\u677F\u5757\u6210\u4EA4\u989D\u5360\u6BD4", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		
		tabbedPanegeguzhanbi = new JTabbedPane(JTabbedPane.TOP);
		
		panelGeguDapanZhanBi = new BanKuaiFengXiBarChartGgCjeZhanbiPnl();
		tabbedPanegeguzhanbi.addTab("\u5927\u76D8\u6210\u4EA4\u989D\u5360\u6BD4", null, panelGeguDapanZhanBi, null);
		
		panelgeguwkzhanbi = new BanKuaiFengXiBarChartGgCjeZhanbiPnl();
		tabbedPanegeguzhanbi.addTab("\u677F\u5757\u6210\u4EA4\u989D\u5360\u6BD4", null, panelgeguwkzhanbi, null);
		
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
		
		pnlggdpcje = new BanKuaiFengXiBarChartCjePnl();
		tabbedPane_2.addTab("\u5927\u76D8\u6210\u4EA4\u989D", null, pnlggdpcje, null);
		
		panelgegucje = new BanKuaiFengXiBarChartCjePnl();
		tabbedPane_2.addTab("\u677F\u5757\u6210\u4EA4\u989D", null, panelgegucje, null);
		
		panelggdpcjlwkzhanbi = new BanKuaiFengXiBarChartGgCjlZhanbiPnl();
		tabbedPanegeguzhanbi.addTab("\u5927\u76D8\u6210\u4EA4\u91CF\u5360\u6BD4", null, panelggdpcjlwkzhanbi, null);
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
			tfldbkmaxwk.setText("4");
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
			
			GroupLayout gl_buttonPane = new GroupLayout(buttonPane);
			gl_buttonPane.setHorizontalGroup(
				gl_buttonPane.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_buttonPane.createSequentialGroup()
						.addContainerGap()
						.addComponent(btnaddexportcond, GroupLayout.PREFERRED_SIZE, 62, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnClear, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
						.addGap(29)
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
						.addGap(40)
						.addComponent(ckboxparsefile)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(tfldparsedfile, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnChosPasFile)
						.addGap(250)
						.addComponent(okButton, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
						.addGap(259))
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
											.addComponent(btnClear))
										.addGroup(gl_buttonPane.createSequentialGroup()
											.addComponent(tfldcjemaxwk, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
											.addGap(2)))
									.addContainerGap())
								.addGroup(gl_buttonPane.createParallelGroup(Alignment.BASELINE)
									.addComponent(okButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addComponent(cancelButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addGroup(gl_buttonPane.createSequentialGroup()
									.addGroup(gl_buttonPane.createParallelGroup(Alignment.BASELINE)
										.addComponent(btnChosPasFile, GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
										.addComponent(tfldparsedfile, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
										.addComponent(ckboxparsefile, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
									.addGap(4)))))
			);
			buttonPane.setLayout(gl_buttonPane);
		}
		


	}
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
			
		} else {
			tfldshowcje.setText("5.8");
		}
		
	}
	
	class ExportCondition 
	{
		public ExportCondition (String exportcjelevel, String exportcjemaxwklevel, String exportdpmaxwklevel, String exportbkmaxwklevel)
		{
			
			this.setSettindpgmaxwk(exportdpmaxwklevel);
			this.setSettinbkgmaxwk(exportbkmaxwklevel);
			this.setSettingcje(exportcjelevel);
			this.setSettingcjemaxwk(exportcjemaxwklevel);
			this.setTooltips ();
		}
		
		private void setTooltips() 
		{
			this.tooltips = this.tooltips + "</br>";
		}
		public String getTooltips ()
		{
			return this.tooltips;
		}

		private Double settingcje;
		private Integer settindpgmaxwk;
		private Integer settinbkgmaxwk;
		private Integer settingcjemaxwk;
		private String tooltips = "";
		
		
		public Double getSettingcje() {
			return settingcje * 100000000;
		}
		public void setSettingcje(String exportcjelevel) {
			if(exportcjelevel != null) {
				this.settingcje = Double.parseDouble( exportcjelevel );
				this.tooltips = this.tooltips + "成交额>" + settingcje + "亿";
			}
			else
				this.settingcje = -10000000000000000.0;
		}
		public Integer getSettindpgmaxwk() {
			return settindpgmaxwk;
		}
		public void setSettindpgmaxwk(String exportdpmaxwklevel) {
			if(exportdpmaxwklevel != null) {
				this.settindpgmaxwk = Integer.parseInt( exportdpmaxwklevel );
				this.tooltips = this.tooltips + "大盘MAXWK>" +  settindpgmaxwk + "周";
			}
			else
				this.settindpgmaxwk = -100000000;
		}
		public Integer getSettinbkgmaxwk() {
			return settinbkgmaxwk;
		}
		public void setSettinbkgmaxwk(String exportbkmaxwklevel) {
			if(exportbkmaxwklevel != null) {
				this.settinbkgmaxwk = Integer.parseInt( exportbkmaxwklevel );
				this.tooltips = this.tooltips + "板块MAXWK>" + settinbkgmaxwk + "周";
			}
			else
				this.settinbkgmaxwk = -100000000;
		}
		public Integer getSettingcjemaxwk() {
			return settingcjemaxwk;
		}
		public void setSettingcjemaxwk(String exportcjemaxwklevel) {
			if(exportcjemaxwklevel != null) {
				this.settingcjemaxwk = Integer.parseInt( exportcjemaxwklevel );
				this.tooltips = this.tooltips + "成交额MAXWK>" + settingcjemaxwk + "周";
			}
			else
				this.settingcjemaxwk = -100000000;
		}


	}
	/*
	 * 导出个股耗时太长，用另外一个线程专门处理，避免主GUI无法使用
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
		 

		public ExportTask2 (BanKuaiAndChanYeLian bkcyl, ArrayList<ExportCondition> exportcond,LocalDate selectiondate,String period,File outputfile)
		{
//			super ();
			this.bkcyltree = bkcyl.getBkChanYeLianTree();
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
			HashSet<String> outputresultset = new HashSet<String> ();
			
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
				if(nodexdata.hasRecordInThePeriod(selectiondate,0) != null) { //板块当周没有数据也不考虑，板块一般不可能没有数据，没有数据说明该板块这周还没有诞生，或者过去有，现在成交量已经不存入数据库
					
					for(ExportCondition expc : expclist) {
						if (isCancelled()) 
							 return null;
						
						Double settingcje = expc.getSettingcje() ;
						Integer settindpgmaxwk = expc.getSettindpgmaxwk();
						Integer settinbkgmaxwk = expc.getSettinbkgmaxwk();
						Integer seetingcjemaxwk = expc.getSettingcjemaxwk();
						
		    			logger.debug("开始导出：条件 成交额 = " + settingcje + "dpmaxwk = " + settindpgmaxwk + " bkmaxwk = " + settinbkgmaxwk + "cjemaxwk= " + seetingcjemaxwk);
		    			logger.debug("导出板块" + childnode.getMyOwnCode() + childnode.getMyOwnName()  );
		    			
		    			
						Double recordcje = nodexdata.getChengJiaoEr(selectiondate, 0);
						Integer recordmaxbkwk = nodexdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(selectiondate);
						Integer recordmaxcjewk =nodexdata.getChenJiaoErMaxWeekOfSuperBanKuai(selectiondate);
						
						if( recordcje >= settingcje &&  recordmaxbkwk >= settindpgmaxwk && recordmaxcjewk >= seetingcjemaxwk ) { //满足条件，导出 ; 板块和个股不一样，只有一个占比
							 String cjs = childnode.getSuoShuJiaoYiSuo();
							 if(cjs.trim().toLowerCase().equals("sh"))
								 outputresultset.add("1" + childnode.getMyOwnCode().trim());
							 else
								 outputresultset.add("0" + childnode.getMyOwnCode().trim());
							 
							 logger.debug(childnode.getMyOwnCode() +  "满足导出条件(" + settindpgmaxwk + "," + settinbkgmaxwk + settingcje + ","  + seetingcjemaxwk +  "), "
								   		+ "数据("  +   recordmaxbkwk  + "," + recordcje + "," + recordmaxcjewk + ")")
										   ;
						 }
						
						//导出个股
						childnode = bkcyl.getAllGeGuOfBanKuai (((BanKuai)childnode),period);
						ArrayList<StockOfBanKuai> rowbkallgg = ((BanKuai)childnode).getSpecificPeriodBanKuaiGeGu(selectiondate,period);
						for (StockOfBanKuai stockofbankuai : rowbkallgg) {
							 if (isCancelled()) 
								 return null;

							 NodeXPeriodDataBasic stockxdataforbk = stockofbankuai.getNodeXPeroidData( period);
							 if(stockxdataforbk.hasRecordInThePeriod(selectiondate, 0))
								 continue;
							 
							 recordcje = stockxdataforbk.getChengJiaoEr(selectiondate, 0);
							 recordmaxbkwk = stockxdataforbk.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(selectiondate) ;
							 
							 Stock ggstock = stockofbankuai.getStock();
							 NodeXPeriodDataBasic stockxdata = ggstock.getNodeXPeroidData(globeperiod);
							 Integer recordmaxdpwk = stockxdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(selectiondate);
							 recordmaxcjewk = stockxdata.getChenJiaoErMaxWeekOfSuperBanKuai(selectiondate);
							 
							 if(recordcje >= settingcje &&  recordmaxbkwk >= settinbkgmaxwk
										 && recordmaxcjewk >= seetingcjemaxwk && recordmaxdpwk >=  settindpgmaxwk)  { //满足条件，导出 ; 
									   if(ggstock.getMyOwnCode().startsWith("60") )
										   outputresultset.add("1" + ggstock.getMyOwnCode().trim());
									   else
										   outputresultset.add("0" + ggstock.getMyOwnCode().trim());
									   
									   logger.debug(childnode.getMyOwnCode() + "个股：" + ggstock.getMyOwnCode() + "满足导出条件(" + settindpgmaxwk + "," + settinbkgmaxwk + settingcje + ","  + seetingcjemaxwk +  "), "
									   		+ "数据("  + recordmaxdpwk + "," +  recordmaxbkwk  + "," + recordcje + "," + recordmaxcjewk + ")")
											   ;   
							 } 
							
						}
						
						setProgress(i*90/bankuaicount );
		    		}
				}
	    	}

			for(String outputstock : outputresultset ) {
				 String outputresult = outputstock +  System.getProperty("line.separator");
				 try {
						Files.append(outputresult,outputfile, charset);
					} catch (IOException e) {
						e.printStackTrace();
					}
			 }
			 
			 setProgress( 100 );
			 
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
				// TODO Auto-generated catch block
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}
	
	/*
	 * 导出个股耗时太长，用另外一个线程专门处理，避免主GUI无法使用
	 * http://www.javacreed.com/swing-worker-example/
	 * https://github.com/javacreed/swing-worker-example/blob/master/src/main/java/com/javacreed/examples/swing/worker/part3/Application.java
	 */
	class BanKuaiPaiXuTask extends SwingWorker<Integer, String>  
	{
		private LocalDate curselectdate;
		private BanKuaiAndChanYeLian bkcyl;
		private String period;
		 

		public BanKuaiPaiXuTask (BanKuaiAndChanYeLian bkcyl,LocalDate selectiondate,String period)
		{
//			super ();
			this.bkcyl = bkcyl;
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
	    	DaPan treeroot = (DaPan)this.bkcyl.getBkChanYeLianTree().getModel().getRoot();
			int bankuaicount = bkcyl.getBkChanYeLianTree().getModel().getChildCount(treeroot);
	        
			for(int i=0;i< bankuaicount; i++) {
				setProgress(i*90/bankuaicount );
				
				if (isCancelled()) {
					i=0;
					 return null;
				}
				
				BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode) this.bkcyl.getBkChanYeLianTree().getModel().getChild(treeroot, i);
				if(childnode.getType() != BanKuaiAndStockBasic.TDXBK) 
					continue;
				
				if( ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL) ||  ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL)  ) //有些指数是没有个股和成交量的，不列入比较范围
					continue;
								
				String bkcode = childnode.getMyOwnCode();
				logger.debug(bkcode);
				childnode = (BanKuai)this.bkcyl.getBanKuai(((BanKuai)childnode), curselectdate,period); 
				BanKuai.BanKuaiNodeXPeriodData bkxdata = (BanKuai.BanKuaiNodeXPeriodData)childnode.getNodeXPeroidData(StockGivenPeriodDataItem.WEEK);
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

			((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).refresh(null,curselectdate,period);
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





