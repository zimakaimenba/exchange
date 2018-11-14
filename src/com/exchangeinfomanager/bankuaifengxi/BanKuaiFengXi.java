package com.exchangeinfomanager.bankuaifengxi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
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
import java.awt.event.WindowEvent;
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
import com.exchangeinfomanager.commonlib.SystemAudioPlayed;
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
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
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
import java.nio.file.FileSystems;
import java.nio.file.Path;
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
	public BanKuaiFengXi(StockInfoManager stockmanager1)
	{
		this.stockmanager = stockmanager1;
		this.allbksks = AllCurrentTdxBKAndStoksTree.getInstance();
		this.bkcyl = BanKuaiAndChanYeLian2.getInstance();
		this.sysconfig = SystemConfigration.getInstance();
		this.bkdbopt = new BanKuaiDbOperation ();
		
		initializeGui ();
		createEvents ();
		setUpChartDataListeners ();

		initializePaoMaDeng ();
		
		//ÿ����һ���µ�һ�ܿ�ʼ����Ϊ��û�е������ݣ�����ʾΪû�����ݣ����԰�ʱ���������һ����
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
		
		btnsixmonthafter.setEnabled(false);
		btnresetdate.setEnabled(false);
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
	private GeGuShiZhiFenXi ggszfx;	
	private Set<BarChartPanelHightLightColumnListener> chartpanelhighlightlisteners;
	private Set<BarChartPanelDataChangedListener> barchartpanelbankuaidatachangelisteners;
	private Set<BarChartPanelDataChangedListener> piechartpanelbankuaidatachangelisteners;
	private Set<BarChartPanelDataChangedListener> barchartpanelstockofbankuaidatachangelisteners;
	private Set<BarChartPanelDataChangedListener> barchartpanelstockdatachangelisteners;
	private Set<BarChartHightLightFxDataValueListener> bkfxhighlightvalueslisteners;
	
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
		this.bkfxhighlightvalueslisteners = new HashSet<>();
		//�û������飬�������bar chart������ݸ���
		barchartpanelbankuaidatachangelisteners.add(panelbkcje);
		barchartpanelbankuaidatachangelisteners.add(panelbkwkzhanbi);
		//���pie chart
		piechartpanelbankuaidatachangelisteners.add(pnllastestggzhanbi);
		piechartpanelbankuaidatachangelisteners.add(panelLastWkGeGuZhanBi);
		//���ɶ԰��
//		barchartpanelstockofbankuaidatachangelisteners.add(panelgegucje); //���ɶ��ڰ�齻�׶�,һ�㿴���ɶԴ��̵ĳɽ������Ͳ�����
		barchartpanelstockofbankuaidatachangelisteners.add(panelggbkcjezhanbi);
		//�������ɱ���
//		barchartpanelstockdatachangelisteners.add(paneldayCandle);
		barchartpanelstockdatachangelisteners.add(pnlggdpcje);
		barchartpanelstockdatachangelisteners.add(panelGgDpCjeZhanBi);
		//�û����bar chart��һ��column, highlight bar chart
		chartpanelhighlightlisteners.add(panelGgDpCjeZhanBi);
		chartpanelhighlightlisteners.add(panelgegubkcje);
		chartpanelhighlightlisteners.add(panelggbkcjezhanbi);
		chartpanelhighlightlisteners.add(panelbkcje);
		chartpanelhighlightlisteners.add(pnlggdpcje);
		chartpanelhighlightlisteners.add(panelbkwkzhanbi);
		chartpanelhighlightlisteners.add(paneldayCandle);
		
		//ͬ��������Ҫhighlight������
		bkfxhighlightvalueslisteners.add(tableGuGuZhanBiInBk);
		bkfxhighlightvalueslisteners.add(tablexuandingzhou);
		bkfxhighlightvalueslisteners.add(tablexuandingminusone);
		bkfxhighlightvalueslisteners.add(tablexuandingminustwo);
		bkfxhighlightvalueslisteners.add(tablexuandingplusone);
		bkfxhighlightvalueslisteners.add(tablexuandingplustwo);
		
	}
	
	/*
	 * ���а��ռ�������ʵ�����
	 */
	private void initializeBanKuaiZhanBiByGrowthRate (String period)
	{
//		Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
//		setCursor(hourglassCursor);
		
		this.globeperiod = period;
    	LocalDate curselectdate = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    	
    	BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)this.allbksks.getAllBkStocksTree().getModel().getRoot();
		int bankuaicount = allbksks.getAllBkStocksTree().getModel().getChildCount(treeroot);
        
		for(int i=0;i< bankuaicount; i++) {
			
			BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode) this.allbksks.getAllBkStocksTree().getModel().getChild(treeroot, i);
			if(childnode.getType() != BanKuaiAndStockBasic.TDXBK) 
				continue;
			
			if( ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL) ||  ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL)  ) //��Щָ����û�и��ɺͳɽ����ģ�������ȽϷ�Χ
				continue;
			
			if( !((BanKuai)childnode).isShowinbkfxgui() )
				continue;
			
			String bkcode = childnode.getMyOwnCode();
			logger.debug(bkcode);
			childnode = (BanKuai)this.allbksks.getBanKuai(((BanKuai)childnode), curselectdate,period); 
			BanKuai.BanKuaiNodeXPeriodData bkxdata = (BanKuai.BanKuaiNodeXPeriodData)childnode.getNodeXPeroidData(StockGivenPeriodDataItem.WEEK);
			if(bkxdata.hasRecordInThePeriod(curselectdate, 0) != null )//��鵱��û������Ҳ�����ǣ����һ�㲻����û�����ݣ�û������˵���ð�����ܻ�û�е��������߹�ȥ�У����ڳɽ����Ѿ����������ݿ�
				((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).addBanKuai(((BanKuai)childnode));
    		
    		//��ʾ���̳ɽ���
    		if(bkcode.equals("399001") ) {
    			BanKuai.BanKuaiNodeXPeriodData bkszdata = (BanKuai.BanKuaiNodeXPeriodData)childnode.getNodeXPeroidData(StockGivenPeriodDataItem.WEEK);
    			try {
    				Double cjerec = bkszdata.getChengJiaoEr(curselectdate,0);
    				lblszcje.setText( cjerec.toString() );
    			} catch (java.lang.NullPointerException e) {
    				lblszcje.setText( "����û������" );
    			}
    			
    		} else if(bkcode.equals("999999") ) {
    			BanKuai.BanKuaiNodeXPeriodData bkshdata = (BanKuai.BanKuaiNodeXPeriodData)childnode.getNodeXPeroidData(StockGivenPeriodDataItem.WEEK);
    			try {
    				Double cjerec = bkshdata.getChengJiaoEr(curselectdate,0);
    				lblshcje.setText( cjerec.toString() );
    			} catch (java.lang.NullPointerException e) {
    				lblshcje.setText( "����û������" );
    			}
    		} 
    			
		}
	
		((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).refresh(curselectdate,0,period,null);
		
//		Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
//		setCursor(hourglassCursor2);
//		Toolkit.getDefaultToolkit().beep();
		SystemAudioPlayed.playSound();
		
	}

	
	/*
	 * ���а��ռ�������ʵ�����
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
		        	progressBarsys.setString("���ڽ��а���������..." + (Integer) event.getNewValue() + "%");
		          break;
		        case "state":
		          switch ((StateValue) event.getNewValue()) {
		          case DONE:
		        	bkfxCancelAction.putValue(Action.NAME, "����");
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
		            	JOptionPane.showMessageDialog(null, "��������ֹ��", "��ֹ������", JOptionPane.WARNING_MESSAGE);
		            } catch (final Exception e) {
		            	e.printStackTrace();
		            }

		            bkfxtask = null;
		            break;
		          case STARTED:
		        	  //��صİ���disable
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
		        	  bkfxCancelAction.putValue(Action.NAME, "��ֹ������");
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
	 * �ѵ�ǰ�İ�鵱�ܷ��������ĵ���
	 */
	private void exportBanKuaiWithGeGuOnCondition2 ()
	{
		
		
		if(exportcond == null || exportcond.size() == 0) {
			if(!ckboxshowcje.isSelected() && !cbxdpmaxwk.isSelected() && !cbxbkmaxwk.isSelected() && !chkcjemaxwk.isSelected()){
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
		
		LocalDate curselectdate = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		String dateshowinfilename = null;
		if(globeperiod == null  || globeperiod.equals(StockGivenPeriodDataItem.WEEK))
			dateshowinfilename = "week" + curselectdate.with(DayOfWeek.FRIDAY).toString().replaceAll("-","");
		else if(globeperiod.equals(StockGivenPeriodDataItem.DAY))
			dateshowinfilename = "day" + curselectdate.toString().replaceAll("-","");
		else if(globeperiod.equals(StockGivenPeriodDataItem.MONTH))
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
		        	progressBarExport.setString("���ڵ���..." + (Integer) eventexport.getNewValue() + "%");
		          break;
		        case "state":
		          switch ((StateValue) eventexport.getNewValue()) {
		          case DONE:
		        	exportCancelAction.putValue(Action.NAME, "������������");
		            try {
		              final int count = exporttask.get();
		              //����XML
		              bkcyl.parseWeeklyBanKuaiFengXiFileToXmlAndPatchToCylTree (filefmxx,curselectdate.with(DayOfWeek.FRIDAY) );
		              
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
						} catch (InterruptedException | ExecutionException e1) {
							e1.printStackTrace();
						}
		            	progressBarExport.setIndeterminate(false);
		            	progressBarExport.setValue(0);
		            	JOptionPane.showMessageDialog(null, "�����������ɱ���ֹ��", "������������",JOptionPane.WARNING_MESSAGE);
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
	 * ��ʾ����ռ�Ⱥ͸���
	 */
	protected void refreshCurentBanKuaiFengXiResult(BanKuai selectedbk,String period) 
	{
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
		tabbedPanegegu.setTitleAt(1, "ѡ����");
		tabbedPanegegu.setTitleAt(2, "ѡ����-1");
		tabbedPanegegu.setTitleAt(3, "ѡ����-2");
		tabbedPanegegu.setTitleAt(4, "ѡ����+1");
		tabbedPanegegu.setTitleAt(5, "ѡ����+2");
//		tfldselectedmsg.setText("");
		tabbedPanebk.setSelectedIndex(0);
		paneldayCandle.resetDate();
		pnlggdpcje.resetDate();
		paneldayCandle.resetDate();
		cbxshizhifx.setSelected(false);
		
		((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).deleteAllRows();
		((BanKuaiGeGuTableModel)tablexuandingzhou.getModel()).deleteAllRows();
		((BanKuaiGeGuTableModel)tablexuandingminusone.getModel()).deleteAllRows();
		((BanKuaiGeGuTableModel)tablexuandingminustwo.getModel()).deleteAllRows();
		((BanKuaiGeGuTableModel)tablexuandingplusone.getModel()).deleteAllRows();
		((BanKuaiGeGuTableModel)tablexuandingplustwo.getModel()).deleteAllRows();
		
		LocalDate curselectdate = null;
		try{
			curselectdate = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		} catch (java.lang.NullPointerException e) {
			JOptionPane.showMessageDialog(null,"��������","Warning",JOptionPane.WARNING_MESSAGE);
			return;
		}
		//�������ռ��

		for(BarChartPanelDataChangedListener tmplistener : barchartpanelbankuaidatachangelisteners) {
			tmplistener.updatedDate(selectedbk, curselectdate, 0,globeperiod);
		}
		
		//���°����������
		if(selectedbk.getBanKuaiLeiXing().equals(BanKuai.HASGGWITHSELFCJL)) { //�и��ɲ���Ҫ���£���Щ�����û�и��ɵ�
			
			selectedbk = allbksks.getAllGeGuOfBanKuai (selectedbk,period);
			
			//������ķ������������Ŀ��0����Ҫ�ѷ��������ĸ��ɱ�Ǻ�
			BkChanYeLianTreeNode nodeincyltree = this.bkcyl.getBkChanYeLianTree().getSpecificNodeByHypyOrCode(selectedbk.getMyOwnCode(), BanKuaiAndStockBasic.TDXBK);
			BanKuaiTreeRelated treerelated = (BanKuaiTreeRelated)nodeincyltree.getNodeTreerelated ();
			Integer patchfilestocknum = ((BanKuai.BanKuaiTreeRelated)treerelated).getStocksNumInParsedFileForSpecificDate (curselectdate);
			if(patchfilestocknum != null && patchfilestocknum >0) {
				Set<String> stkofbkset = this.bkcyl.getBanKuaiFxSetOfSpecificDate(selectedbk.getMyOwnCode(), curselectdate);
				
				for(String stkofbk : stkofbkset ) {
					StockOfBanKuai stkinbk = selectedbk.getBanKuaiGeGu(stkofbk);
	    			StockOfBanKuaiTreeRelated stofbktree = (StockOfBanKuaiTreeRelated)stkinbk.getNodeTreerelated();
	        		stofbktree.setStocksNumInParsedFile (curselectdate,true);
				}
				
				stkofbkset= null;
			}
			
			((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).refresh(selectedbk, curselectdate,period);
			
			//������ʾ����
			tabbedPanegegu.setTitleAt(0, "��ǰ��" + curselectdate);
			
			//��ʾ2�ܵİ�����pie chart
//			piechartpanelbankuaidatachangelisteners.forEach(l -> l.updatedDate(selectbk, curselectdate, 0,globeperiod));
			for(BarChartPanelDataChangedListener tmplistener : piechartpanelbankuaidatachangelisteners) {
				tmplistener.updatedDate(selectedbk, curselectdate, 0,globeperiod);
			}
			
			
		}
	}
	/*
	 * ���óɽ�������ռ�ȵ���ʾ
	 */
//	private void setDisplayNoticeLevelForPanels() 
//	{
//		panelbkwkzhanbi.setDaZiJinValueMarker (0.01); //����1%�ǰ��ǿ�Ƶı���
//		panelgegubkcje.setDaZiJinValueMarker ( 580000000); //5.8��
//		panelgegubkcje.setDaZiJinValueMarker (1000000000);
//		panelgegubkcje.setDaZiJinValueMarker (1500000000);
//		panelgegubkcje.setDaZiJinValueMarker (2000000000);
//		
//	}

	/*
	 * ��������ʾ�û���ѡ���ܸ���ռ��������������
	 */
	private void refreshSpecificBanKuaiFengXiResult (BanKuai selectedbk, LocalDate selecteddate,String period)
	{
		selectedbk = allbksks.getAllGeGuOfBanKuai (selectedbk,period);
//		ArrayList<StockOfBanKuai> allbkge = selectedbk.getSpecificPeriodBanKuaiGeGu(selecteddate,period);
//		HashSet<String> stockinparsefile = selectedbk.getNodetreerelated().getParseFileStockSet ();
		
		//��ʾѡ���ܹ�Ʊ�������
		((BanKuaiGeGuTableModel)tablexuandingzhou.getModel()).refresh(selectedbk, selecteddate,period);
		tabbedPanegegu.setTitleAt(1, "ѡ����" + selecteddate);
		
		//��ʾѡ����-1��Ʊ�������
		LocalDate selectdate2 = selecteddate.minus(1,ChronoUnit.WEEKS).with(DayOfWeek.FRIDAY);
//		if( checkDateBetweenBanKuaiDate (selectedbk,selectdate2) ) //锟斤拷锟揭拷懈锟斤拷锟斤拷锟斤拷锟�
			((BanKuaiGeGuTableModel)tablexuandingminusone.getModel()).refresh(selectedbk, selectdate2,period);
			tabbedPanegegu.setTitleAt(2,  selectdate2 + "(-1)");
//		else
//			((BanKuaiGeGuTableModel)tablexuandingminusone.getModel()).deleteAllRows();
		
		//��ʾѡ����-2��Ʊ�������
		LocalDate selectdate3 = selecteddate.minus(2,ChronoUnit.WEEKS).with(DayOfWeek.FRIDAY);
//		if( checkDateBetweenBanKuaiDate (selectedbk,selectdate3) )
			((BanKuaiGeGuTableModel)tablexuandingminustwo.getModel()).refresh(selectedbk, selectdate3,period);
			tabbedPanegegu.setTitleAt(3,  selectdate3 + "(-2)");
//		else 
//			((BanKuaiGeGuTableModel)tablexuandingminustwo.getModel()).deleteAllRows();
		
		//��ʾѡ����+1��Ʊ�������
		LocalDate selectdate4 = selecteddate.plus(1,ChronoUnit.WEEKS).with(DayOfWeek.FRIDAY);
//		if( checkDateBetweenBanKuaiDate (selectedbk,selectdate4) )
			((BanKuaiGeGuTableModel)tablexuandingplusone.getModel()).refresh(selectedbk, selectdate4,period);
			tabbedPanegegu.setTitleAt(4,  selectdate4 + "(+1)");
//		else
//			((BanKuaiGeGuTableModel)tablexuandingplusone.getModel()).deleteAllRows();
		
		//��ʾѡ����+2��Ʊ�������
		LocalDate selectdate5 = selecteddate.plus(2,ChronoUnit.WEEKS).with(DayOfWeek.FRIDAY);
//		if( checkDateBetweenBanKuaiDate (selectedbk,selectdate5) )
			((BanKuaiGeGuTableModel)tablexuandingplustwo.getModel()).refresh(selectedbk, selectdate5,period);
			tabbedPanegegu.setTitleAt(5,  selectdate5 + "(+2)");
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
	 * ��ʾ������԰���ռ�ȵ�chart
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
	 * �û�ѡ����ɺ���ʾ��������K�������Ѿ���������Դ��̵ĸ���ռ��
	 */
	private void refreshGeGuKXianZouShi (Stock selectstock2)
	{
		LocalDate curselectdate = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate requireend = curselectdate.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = curselectdate.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange(),ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		
		selectstock2 = allbksks.getStock(selectstock2,curselectdate,StockGivenPeriodDataItem.WEEK);
		//����K�����ƣ�ĿǰK�����ƺͳɽ��������ߺ��������������Ƿֿ��ģ����Ե���ʱ��Ҫ�ر�С�ģ��Ժ��ϲ�
		selectstock2 = allbksks.getStockKXian(selectstock2,curselectdate,StockGivenPeriodDataItem.DAY);
		
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
		editorPanebankuai.displayBanKuaiListContents(selectstock);
		 
//		 selectstock = bkdbopt.getTDXBanKuaiForAStock (selectstock); //ͨ���Ű����Ϣ
//		 HashMap<String, String> suosusysbankuai = ((Stock)selectstock).getGeGuCurSuoShuTDXSysBanKuaiList();
//		 Set<String> union =  suosusysbankuai.keySet();
//		 Multimap<String,String> suoshudaleibank = bkcyl.checkBanKuaiSuoSuTwelveDaLei (  union ); //��ð���Ƿ�����12��������
//		 
//		 String htmlstring = "<html> "
//		 		+ "<body>"
//		 		+ " <p>�������:";
//	     for(String suoshubankcode : union ) {
//	    	 Collection<String> daleilist = suoshudaleibank.get(suoshubankcode);
//	    	 String displayedbkformate = "\"" + suoshubankcode + suosusysbankuai.get(suoshubankcode) + "\"";
//	    	 if(daleilist.size()>0)
//	    			htmlstring  +=   "<a style=\"color:red\" href=\"openBanKuaiAndChanYeLianDialog\">  " + displayedbkformate + daleilist.toString()  + "</a> ";
//	    		else
//	    			htmlstring  += "<a href=\"openBanKuaiAndChanYeLianDialog\"> " + displayedbkformate + "</a> ";
//	     } 
//	     htmlstring += "</p>";
//	     		
//	     htmlstring += "</body>"
//					+ "</html>";
//	    	 
//	     editorPanebankuai.setText(htmlstring);
//	     editorPanebankuai.setCaretPosition(0);
	     
	}
	/*
	 * ��ʾ�û����bar column��Ӧ����ʾ����Ϣ
	 */
	private void setUserSelectedColumnMessage(String selttooltips) 
	{
		String allstring = selttooltips + "\n";
		
		tfldselectedmsg.setText( allstring + tfldselectedmsg.getText() + "\n");
		tfldselectedmsg.setCaretPosition(0);
//		 JScrollBar verticalScrollBar = scrollPaneuserselctmsg.getVerticalScrollBar();
//		 JScrollBar horizontalScrollBar = scrollPaneuserselctmsg.getHorizontalScrollBar();
////		 verticalScrollBar.setValue(verticalScrollBar.getMaximum() );
//		 Rectangle visible = scrollPaneuserselctmsg.getVisibleRect();
//		 visible.y = 0;
//		 scrollPaneuserselctmsg.scrollRectToVisible(visible);
		 
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
		//����panel��˵��hightLightFxValues��һ�����������ã���Ϊpanel ���� �ڵ�������ϼ���ռ��
		panelGgDpCjeZhanBi.hightLightFxValues(null,dpmaxwk, null, null,null) ;
		pnlggdpcje.hightLightFxValues(null,null, null, cjemaxwk,null) ;
		panelbkcje.hightLightFxValues(null,null, null, cjemaxwk,null) ;
		panelbkwkzhanbi.hightLightFxValues(null,dpmaxwk, null, null,null) ;
		panelggbkcjezhanbi.hightLightFxValues(null,bkmaxwk, null, null,null) ;
	}
	/*
	 * 
	 */
	private void createEvents() 
	{
		cbxshizhifx.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
			}
		});
		
		btnshizhifx.addMouseListener(new MouseAdapter() {
			

			@Override
			public void mouseClicked(MouseEvent arg0) {
				startupGeGuShiZhiFenXi (true);
				
			}
		});
		
		menuItemRmvNodeFmFile.addActionListener(new ActionListener() {
			@Override

			public void actionPerformed(ActionEvent evt) {
				int bkrow = tableBkZhanBi.getSelectedRow();
				if(bkrow <0) {
					JOptionPane.showMessageDialog(null,"��ѡ��һ����飡","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				int bkmodelRow = tableBkZhanBi.convertRowIndexToModel(bkrow);
//				BanKuai selectedbk = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(bkmodelRow);
				
				int ggrow = tableGuGuZhanBiInBk.getSelectedRow();
				if(ggrow <0) {
					JOptionPane.showMessageDialog(null,"��ѡ��һ����Ʊ��","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				int ggmodelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(ggrow);
//				StockOfBanKuai selectstock = ((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).getStock (ggmodelRow);
//
//				LocalDate curselectdate = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			}
			
		});
		
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
    	    		Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
        			setCursor(hourglassCursor);
        			
    	    		BanKuai bankuai = (BanKuai)allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode(node.getMyOwnCode(), BanKuaiAndStockBasic.TDXBK);
//    	    		refreshCurentBanKuaiFengXiResult (bankuai,globeperiod);
//    				displayNodeInfo(bankuai);
    				cbxsearchbk.updateUserSelectedNode(bankuai);
    				
    				tabbedPanebk.setSelectedIndex(2);
    				
    				hourglassCursor = null;
    				Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
        			setCursor(hourglassCursor2);
//    				Toolkit.getDefaultToolkit().beep();
    				SystemAudioPlayed.playSound();
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
    				setUserSelectedColumnMessage(tooltip);
    				
    				if(bkcur.getBanKuaiLeiXing().equals(BanKuai.HASGGWITHSELFCJL) ) {//Ӧ�����и��ɵİ��������ʾ���ĸ��ɣ� 
    					LocalDate selectdate = CommonUtility.formateStringToDate(datekey.toString());
    					LocalDate cursetdate = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    					if(!CommonUtility.isInSameWeek(selectdate,cursetdate) ) {
    						panelselectwkgeguzhanbi.setBanKuaiCjeNeededDisplay(bkcur,Integer.parseInt(tfldweight.getText() ), selectdate ,globeperiod );
        					
        					//��ʾѡ���ܹ�Ʊռ��������������
        					refreshSpecificBanKuaiFengXiResult (bkcur,selectdate,globeperiod);
    					}
    					
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
    				
    				String tooltip = selectedinfo.substring(10,selectedinfo.length());
    				setUserSelectedColumnMessage(tooltip);
    				
    				if(cbxshizhifx.isSelected()) { //��ʾ��ֵ����
						dispalyStockShiZhiFengXiResult (selectstock,datekey);
					};
					
					SystemAudioPlayed.playSound();
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
    				setUserSelectedColumnMessage(tooltip);
				}
			}
		});
		/*
		 * 
		 */
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
    				setUserSelectedColumnMessage(tooltip);
                }
			}
		});
		/*
		 * 
		 */
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
					btnaddexportcond.setToolTipText("<html>������������<br></html>");
				}
			}
		});
		
		//��ӵ�������
		btnaddexportcond.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				
//				if( exportcond == null)
//					exportcond = new ArrayList<ExportCondition> ();
				
				if(!ckboxshowcje.isSelected() && !cbxdpmaxwk.isSelected() && !cbxbkmaxwk.isSelected() && !chkcjemaxwk.isSelected()){
					JOptionPane.showMessageDialog(null,"δ���õ����������������õ���������");
					return;
				} 
				
				initializeExportConditions ();
			}
		});
		
		//ͻ����ʾ����WK����
		tfldbkmaxwk.addActionListener(new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				if( Strings.isNullOrEmpty(tfldbkmaxwk.getText() )) {
					JOptionPane.showMessageDialog(null,"������ͻ����ʾ���MaxWk��");
					cbxdpmaxwk.setSelected(false);
					return;
				}
				if(cbxbkmaxwk.isSelected() ) {
					refreshBanKuaiGeGuTableHightLight ();
				}
			}
		});
		
		//ͻ����ʾ�ɽ����������ֵ
		chkcjemaxwk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if( Strings.isNullOrEmpty(tfldcjemaxwk.getText() )) {
					JOptionPane.showMessageDialog(null,"������ͻ����ʾ���MaxWk��");
					chkcjemaxwk.setSelected(false);
					return;
				}
				refreshBanKuaiGeGuTableHightLight ();
				
			}
		});
		cbxhuanshoulv.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if( Strings.isNullOrEmpty(tfldhuanshoulv.getText() )) {
					JOptionPane.showMessageDialog(null,"������ͻ����ʾ�����ʣ�");
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
					JOptionPane.showMessageDialog(null,"������ͻ����ʾ���MaxWk��");
					cbxbkmaxwk.setSelected(false);
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
					cbxdpmaxwk.setSelected(false);
					return;
				}
				refreshBanKuaiGeGuTableHightLight ();
				
			}
		});
		cbxdpmaxwk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if( Strings.isNullOrEmpty(tflddisplaydpmaxwk.getText() )) {
					JOptionPane.showMessageDialog(null,"������ͻ����ʾMaxWk��");
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
//						JOptionPane.showMessageDialog(null,"��ѡ��ÿ�հ���ļ���");
//						ckboxparsefile.setSelected(false);
//						return;
						Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
						setCursor(hourglassCursor);
						
						chooseParsedFile (null);
						
						hourglassCursor = null;
						Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
						setCursor(hourglassCursor2);
					}
					
//					chooseParsedFile (tfldparsedfile.getText());
//					((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).setShowParsedFile(true);
//					tableGuGuZhanBiInBk.repaint();
				}
			
				if(!ckboxparsefile.isSelected()) {
//					((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).setShowParsedFile(false);
//					tableGuGuZhanBiInBk.repaint();
					Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
					setCursor(hourglassCursor);
					
					ckboxparsefile.setSelected(true);
					
					hourglassCursor = null;
					Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
					setCursor(hourglassCursor2);
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
					if(userinputnode.getType() == BanKuaiAndStockBasic.TDXGG ) { //����û����������ǰ�飬
						JOptionPane.showMessageDialog(null,"���ǰ����룬�������룡","Warning", JOptionPane.WARNING_MESSAGE);
						return;
					}
//					else if(userinputnode == null) { //�������������ĸû���ҵ�
////						nodecode = cbxsearchbk.getSelectedItem().toString();
//					} 
					
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
							refreshCurentBanKuaiFengXiResult (selectedbk,globeperiod);
							displayNodeInfo(selectedbk);
							cbxsearchbk.updateUserSelectedNode(selectedbk);
					} else 	{
						JOptionPane.showMessageDialog(null,"��Ʊ/���������������ƴ������","Warning", JOptionPane.WARNING_MESSAGE);
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
					if(userinputnode == null) { //�������������ĸû���ҵ�
						nodecode = cbxstockcode.getSelectedItem().toString();
					} else {
						if(userinputnode.getType() == BanKuaiAndStockBasic.TDXBK ) { //����û����������ǰ�飬
							JOptionPane.showMessageDialog(null,"���Ǹ��ɴ��룬�������룡","Warning", JOptionPane.WARNING_MESSAGE);
							return;
						}
							
						nodecode = userinputnode.getMyOwnCode();
						try{ //����û�ѡ��ĺ��ϴ�ѡ��ĸ���һ�������ظ���������
							String stockcodeincbx = ((String)cbxstockcode.getSelectedItem()).substring(0, 6);
							if(cbxstockcode.isEnabled() ) { //�����
								displayStockSuoShuBanKuai((Stock)userinputnode);
							} else
								cbxstockcode.setEnabled(true);
						} catch (java.lang.NullPointerException e) {
							e.printStackTrace();
//							cbxstockcode.updateUserSelectedNode (selectstock.getStock());
						}
//						displayStockSuoShuBanKuai((Stock)userinputnode);
					}
					
					int rowindex = tableGuGuZhanBiInBk.getSelectedRow();
//					int modelRow = tableGuGuZhanBiInBk.convertRowIndexToView(rowindex);
					
					if(!findInputedNodeInTable (nodecode)) { //���û���ҵ�
//						tableGuGuZhanBiInBk.setRowSelectionInterval(rowindex,rowindex);
						tableGuGuZhanBiInBk.getSelectionModel().clearSelection() ; //�ѵ�ǰ��table��ѡ��ȡ���������û������Ѿ��ҵ�
						//��Ȼȡ����table�еĸ��ɣ�����Ҫ�Ѽ���ͼ�����Ϣ����������û���ȥ˫����Щͼ����������⡣
						pnlggdpcje.resetDate();
						panelGgDpCjeZhanBi.resetDate();
						paneldayCandle.resetDate();
					}
						
				}
				
				if(arg0.getStateChange() == ItemEvent.DESELECTED) {
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
	    		
	    		btnsixmonthafter.setEnabled(true);
	    		btnresetdate.setEnabled(true);
			}
		});
		btnsixmonthafter.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(!btnsixmonthafter.isEnabled())
					return ;
				
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
	    		
	    		btnresetdate.setEnabled(true);

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
		    			Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
						setCursor(hourglassCursor);
						
						btnsixmonthafter.setEnabled(true);
						btnresetdate.setEnabled(true);
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
			    		tabbedPanegegu.setTitleAt(0, "��ǰ��");
			    		tabbedPanegegu.setTitleAt(1, "ѡ����");
			    		tabbedPanebk.setSelectedIndex(0);
			    		ckboxparsefile.setSelected(false);
			    		tfldparsedfile.setText("");
			    		
			    		bkcyl.getBkChanYeLianTree().setCurrentDisplayedWk (newdate);
			    		DefaultTreeModel treeModel = (DefaultTreeModel) bkcyl.getBkChanYeLianTree().getModel();
			    		treeModel.reload();
			    		
			    		setHighLightChenJiaoEr();
			    		
			    		initializeBanKuaiZhanBiByGrowthRate (StockGivenPeriodDataItem.WEEK);
			    		
			    		lastselecteddate = newdate;
			    		
			    		hourglassCursor = null;
			    		Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
						setCursor(hourglassCursor2);
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
					
					Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
					setCursor(hourglassCursor);
					
					int modelRow = tableBkZhanBi.convertRowIndexToView(rowindex);
					tableBkZhanBi.setRowSelectionInterval(modelRow, modelRow);
					tableBkZhanBi.scrollRectToVisible(new Rectangle(tableBkZhanBi.getCellRect(modelRow, 0, true)));
					
					//�ҵ��û�ѡ��İ��
					BanKuai selectedbk = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(rowindex);
					refreshCurentBanKuaiFengXiResult (selectedbk,globeperiod);
					displayNodeInfo(selectedbk);
//					patchParsedFile (selectedbk);
					cbxsearchbk.updateUserSelectedNode(selectedbk);
					
					//�Ҳ����ù�Ʊ
					String stockcode = cbxstockcode.getSelectedItem().toString().substring(0, 6);
					if(!findInputedNodeInTable (stockcode) )
						JOptionPane.showMessageDialog(null,"��ĳ�����ɱ���û�з��ָùɣ����������ʱ����ڸù�ͣ��","Warning",JOptionPane.WARNING_MESSAGE);
					
//					Toolkit.getDefaultToolkit().beep();
					hourglassCursor = null;
					Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
					setCursor(hourglassCursor2);
					
					SystemAudioPlayed.playSound();
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
					JOptionPane.showMessageDialog(null,"��ѡ��һ����Ʊ��","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				int modelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(row);
				StockOfBanKuai selectstock = ((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).getStock (modelRow);
				
				if (arg0.getClickCount() == 1) { //�û����һ��
					Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
					setCursor(hourglassCursor);
					
					try{ //����û�ѡ��ĺ��ϴ�ѡ��ĸ���һ�������ظ���������
						String stockcodeincbx = ((String)cbxstockcode.getSelectedItem()).substring(0, 6);
						if(!selectstock.getMyOwnCode().equals( stockcodeincbx ) ) {
//							cbxstockcode.updateUserSelectedNode (selectstock.getStock());
							cbxstockcode.setEnabled(false); //���ⴥ��cbxstockcode��itemchange�¼���������2�����ݿ�
							displayStockSuoShuBanKuai((Stock)cbxstockcode.updateUserSelectedNode (selectstock.getStock()));
						}
					} catch (java.lang.NullPointerException e) {
						cbxstockcode.updateUserSelectedNode (selectstock.getStock());
					}
					
					hightlightSpecificSector (selectstock); //D
					refreshGeGuFengXiResult (selectstock); //���ɶ԰�������
					refreshGeGuKXianZouShi (selectstock.getStock()); //K��
					displayStockCurrentFxResult (tableGuGuZhanBiInBk);
					
//					if(cbxshizhifx.isSelected()) {
//						LocalDate curselectdate = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//						dispalyStockShiZhiFengXiResult (selectstock,curselectdate);
//					};
					cbxshizhifx.setSelected(false);
					
					hourglassCursor = null;
					Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
					setCursor(hourglassCursor2);
//					Toolkit.getDefaultToolkit().beep();
//					SystemAudioPlayed.playSound();
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
					JOptionPane.showMessageDialog(null,"��ѡ��һ����Ʊ��","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				int modelRow = tablexuandingzhou.convertRowIndexToModel(row);
				StockOfBanKuai selectstock = ((BanKuaiGeGuTableModel)tablexuandingzhou.getModel()).getStock (modelRow);
	
				if (arg0.getClickCount() == 1) {
					hightlightSpecificSector (selectstock);
					refreshGeGuFengXiResult (selectstock);
					refreshGeGuKXianZouShi (selectstock.getStock()); //K��
					cbxstockcode.updateUserSelectedNode (selectstock.getStock()); 
					displayStockSuoShuBanKuai((Stock)cbxstockcode.updateUserSelectedNode (selectstock.getStock()));
					displayStockCurrentFxResult (tablexuandingzhou);
					
//					Toolkit.getDefaultToolkit().beep();
					SystemAudioPlayed.playSound();
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
					JOptionPane.showMessageDialog(null,"��ѡ��һ����Ʊ��","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				int modelRow = tablexuandingminustwo.convertRowIndexToModel(row);
				StockOfBanKuai selectstock = ((BanKuaiGeGuTableModel)tablexuandingminustwo.getModel()).getStock (modelRow);
	
				if (arg0.getClickCount() == 1) {
					hightlightSpecificSector (selectstock);
					hightlightSpecificSector (selectstock);
					refreshGeGuFengXiResult (selectstock);
					refreshGeGuKXianZouShi (selectstock.getStock()); //K��
					cbxstockcode.updateUserSelectedNode (selectstock.getStock()); 
					displayStockSuoShuBanKuai((Stock)cbxstockcode.updateUserSelectedNode (selectstock.getStock()));
					displayStockCurrentFxResult (tablexuandingminustwo);
					
//					Toolkit.getDefaultToolkit().beep();
					SystemAudioPlayed.playSound();
					
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
					JOptionPane.showMessageDialog(null,"��ѡ��һ����Ʊ��","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				int modelRow = tablexuandingminusone.convertRowIndexToModel(row);
				StockOfBanKuai selectstock = ((BanKuaiGeGuTableModel)tablexuandingminusone.getModel()).getStock (modelRow);
	
				if (arg0.getClickCount() == 1) {
					hightlightSpecificSector (selectstock);
					hightlightSpecificSector (selectstock);
					refreshGeGuFengXiResult (selectstock);
					refreshGeGuKXianZouShi (selectstock.getStock()); //K��
					cbxstockcode.updateUserSelectedNode (selectstock.getStock()); 
					displayStockSuoShuBanKuai((Stock)cbxstockcode.updateUserSelectedNode (selectstock.getStock()));
					displayStockCurrentFxResult (tablexuandingminusone);
					
//					Toolkit.getDefaultToolkit().beep();
					SystemAudioPlayed.playSound();
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
					JOptionPane.showMessageDialog(null,"��ѡ��һ����Ʊ��","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				int modelRow = tablexuandingplusone.convertRowIndexToModel(row);
				StockOfBanKuai selectstock = ((BanKuaiGeGuTableModel)tablexuandingplusone.getModel()).getStock (modelRow);
	
				if (arg0.getClickCount() == 1) {
					hightlightSpecificSector (selectstock);
					hightlightSpecificSector (selectstock);
					refreshGeGuFengXiResult (selectstock);
					refreshGeGuKXianZouShi (selectstock.getStock()); //K��
					cbxstockcode.updateUserSelectedNode (selectstock.getStock()); 
					displayStockSuoShuBanKuai((Stock)cbxstockcode.updateUserSelectedNode (selectstock.getStock()));
					displayStockCurrentFxResult (tablexuandingplusone);
					
//					Toolkit.getDefaultToolkit().beep();
					SystemAudioPlayed.playSound();
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
					JOptionPane.showMessageDialog(null,"��ѡ��һ����Ʊ��","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				int modelRow = tablexuandingplustwo.convertRowIndexToModel(row);
				StockOfBanKuai selectstock = ((BanKuaiGeGuTableModel)tablexuandingplustwo.getModel()).getStock (modelRow);
	
				if (arg0.getClickCount() == 1) {
					hightlightSpecificSector (selectstock);
					hightlightSpecificSector (selectstock);
					refreshGeGuFengXiResult (selectstock);
					refreshGeGuKXianZouShi (selectstock.getStock()); //K��
					cbxstockcode.updateUserSelectedNode (selectstock.getStock()); 
					displayStockSuoShuBanKuai((Stock)cbxstockcode.updateUserSelectedNode (selectstock.getStock()));
					displayStockCurrentFxResult (tablexuandingplustwo);
					
//					Toolkit.getDefaultToolkit().beep();
					SystemAudioPlayed.playSound();
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
				chooseParsedFile (null);
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
				refreshCurentBanKuaiFengXiResult (selectedbk,globeperiod);
				displayNodeInfo(selectedbk);
				cbxsearchbk.updateUserSelectedNode(selectedbk);
				
				hourglassCursor = null;
				Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
				setCursor(hourglassCursor2);
//				Toolkit.getDefaultToolkit().beep();
				SystemAudioPlayed.playSound();
				//װ��ÿ��ģ���ļ�
			}
		});
	}
	protected void dispalyStockShiZhiFengXiResult(StockOfBanKuai selectstock, LocalDate curselectdate) 
	{
		Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
		setCursor(hourglassCursor);
		
		String displayedresult;
		if(ggszfx == null) {
			startupGeGuShiZhiFenXi (false);
			displayedresult = ggszfx.getStockShiZhiDuiBiFenXiResult (selectstock.getMyOwnCode(),curselectdate);
		} else 
			displayedresult = ggszfx.getStockShiZhiDuiBiFenXiResult (selectstock.getMyOwnCode(),curselectdate);
		
		
		setUserSelectedColumnMessage (displayedresult);
		
		hourglassCursor = null;
		Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
		setCursor(hourglassCursor2);
	}

	/*
	 * 
	 */
	protected void startupGeGuShiZhiFenXi(boolean showthedialog) 
	{
		Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
		setCursor(hourglassCursor);
		
		if(ggszfx == null) {
			ggszfx = new GeGuShiZhiFenXi ();
			ggszfx.setModal(false);
			ggszfx.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		}
		
		if(showthedialog) {
			if(!ggszfx.isVisible() ) {
				ggszfx.setVisible(true);
			} 
			
			ggszfx.toFront();
		}
			
		
		
		hourglassCursor = null;
		Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
		setCursor(hourglassCursor2);
		
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
	private void displayStockCurrentFxResult(BanKuaiGeGuTable curtable) 
	{
		int row = curtable.getSelectedRow();
		if(row <0) {
			JOptionPane.showMessageDialog(null,"����ѡ��һ������","Warning",JOptionPane.WARNING_MESSAGE);
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
		result = result + "��ͨ��ֵ:" + liutongshizhi/100000000 + "��" + "����ֵ:" + zongshizhi/100000000 + "\n";

		result = null;
		
		//�@ʾ���ɵ���Ϣ
		editorPanenodeinfo.setClearContentsBeforeDisplayNewInfo (false);
		editorPanenodeinfo.displayNodeAllInfo(selectstock.getStock());
		editorPanenodeinfo.setClearContentsBeforeDisplayNewInfo (true);
	}
	/*
	 * �û�˫��ĳ��nodeռ��chart����Ŵ���ʾ��nodeһ���ڵ�ռ����������
	 */
	protected void displayNodeLargerPeriodData(BkChanYeLianTreeNode node, LocalDate datekey) 
	{
		LocalDate curselectdate = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalDate requireend = curselectdate.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange(),ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		LocalDate requirestart = requireend.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange(),ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		
		BanKuai bkcur = null;
		if(node.getType() == BanKuaiAndStockBasic.TDXBK) {
			node = this.allbksks.getBanKuai((BanKuai)node, requirestart.plusWeeks(1),globeperiod);
		} else if(node.getType() == BanKuaiAndStockBasic.TDXGG) { 
			node = this.allbksks.getStock((Stock)node, requirestart.plusWeeks(1),globeperiod);
			node = this.allbksks.getStockKXian((Stock)node, requirestart.plusWeeks(1), StockGivenPeriodDataItem.DAY);
			
			//����Ǹ��ɵĻ�����Ҫ��ʾ�䵱ǰ�����İ��ռ����Ϣ������Ҫ�Ѱ�������Ҳ�ҳ�����
			int row = tableBkZhanBi.getSelectedRow();
			if(row != -1) {
				int modelRow = tableBkZhanBi.convertRowIndexToModel(row);
				bkcur = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(modelRow);
				bkcur = this.allbksks.getBanKuai((BanKuai)bkcur, requirestart.plusWeeks(1),globeperiod);
			}
			
		} else if(node.getType() == BanKuaiAndStockBasic.BKGEGU) {
			BanKuai bk = ((StockOfBanKuai)node).getBanKuai();
			this.allbksks.getBanKuai((BanKuai)bk, requirestart.plusWeeks(1),globeperiod);
			node = this.allbksks.getGeGuOfBanKuai(bk, node.getMyOwnCode() ,globeperiod);
		}
		
		this.allbksks.getDaPan (requirestart.plusWeeks(1),globeperiod); //ͬ����������,�����������ط������
		
//		NodeXPeriodDataBasic nodexdate = node.getNodeXPeroidData(globeperiod);
		
		SystemAudioPlayed.playSound();
		
		BanKuaiFengXiLargePnl largeinfo = null;
		if(node.getType() == BanKuaiAndStockBasic.TDXBK) {
//			DaPan treeroot = (DaPan)this.allbksks.getAllBkStocksTree().getModel().getRoot();
			
			largeinfo = new BanKuaiFengXiLargePnl (null, node, requirestart.plusWeeks(1).with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange(),ChronoUnit.MONTHS).with(DayOfWeek.MONDAY)
					, curselectdate, globeperiod);
			
		} else if(node.getType() == BanKuaiAndStockBasic.TDXGG) { 
			largeinfo = new BanKuaiFengXiLargePnl (bkcur, node, requirestart.plusWeeks(1).with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange(),ChronoUnit.MONTHS).with(DayOfWeek.MONDAY)
					, curselectdate, globeperiod);
		}
		
		if(datekey != null)
			largeinfo.highLightSpecificBarColumn(datekey);
		
//		long start=System.currentTimeMillis(); //��ȡ��ʼʱ��
		JOptionPane.showMessageDialog(null, largeinfo, node.getMyOwnCode()+node.getMyOwnName()+ "�����ڷ������", JOptionPane.OK_CANCEL_OPTION);
//		long end=System.currentTimeMillis(); //��ȡ����ʱ��
//		logger.debug("��������ʱ�䣺 "+(end-start)+"ms");
		
		largeinfo = null;
		System.gc();
	}
	/*
	 * ÿ�ܶ�Ҫ�������������������ã�һ�����ý�Լʱ��
	 */
	protected void initializeNormalExportConditions()
	{
		if( exportcond == null)
			exportcond = new ArrayList<ExportCondition> ();
		else
			exportcond.clear();
		
		ExportCondition expc1 = new ExportCondition ("5.8",null,"4",null,null,null);
		exportcond.add(expc1);

		ExportCondition expc2 = new ExportCondition ("5.8","7",null,"5",null,null);
		exportcond.add(expc2);
		
		ExportCondition expc3 = new ExportCondition ("2.8",null,"4",null,"30","880529");
		exportcond.add(expc3);
		
		btnaddexportcond.setToolTipText("");
		decrorateExportButton (expc1);
		decrorateExportButton (expc2);
		decrorateExportButton (expc3);
	}
	/*
	 * �û���������������
	 */
	protected void initializeExportConditions() 
	{
//		String exportbk = null;
//		String msg =  "���������Ƿ��޶��ڽ���ǰ��������ã�";
//		int exchangeresult = JOptionPane.showConfirmDialog(null,msg , "��ӵ�������", JOptionPane.OK_CANCEL_OPTION);
//		if(exchangeresult == JOptionPane.OK_OPTION) { //��һЩ��飬����¹ɣ����ܵ��������Ƚ����⣬���Ե�������
//			int row = tableBkZhanBi.getSelectedRow();
//			if(row <0) {
//				JOptionPane.showMessageDialog(null,"��ѡ��һ����飡","Warning",JOptionPane.WARNING_MESSAGE);
//				return;
//			}
//			
//			int modelRow = tableBkZhanBi.convertRowIndexToModel(row);
//			BanKuai selectedbk = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(modelRow);
//			exportbk = selectedbk.getMyOwnCode();
//		}

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
	
		if( exportcond == null)
			exportcond = new ArrayList<ExportCondition> ();
		
		ExportCondition expc = new ExportCondition (exportcjelevel,exportcjemaxwklevel,exportdpmaxwklevel,exportbkmaxwklevel,exporthsl,null);
		
		ExtraExportConditions extraexportcondition = new ExtraExportConditions ();
		int extraresult = JOptionPane.showConfirmDialog(null,extraexportcondition , "���ӵ�������:", JOptionPane.OK_CANCEL_OPTION);
		if(extraresult == JOptionPane.OK_OPTION) { //������������ 
			expc.setExportSTStocks ( extraexportcondition.shouldExportSTStocks() );
			expc.setHaveDaYangXianUnderCertainChenJiaoEr ( extraexportcondition.shouldHaveDaYangXianUnderCertainChenJiaoEr() );
			
			if(extraexportcondition.shouldOnlyExportCurrentBankuai() ) {
				int row = tableBkZhanBi.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"��ѡ��һ����飡","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				int modelRow = tableBkZhanBi.convertRowIndexToModel(row);
				BanKuai selectedbk = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(modelRow);
				String exportbk = selectedbk.getMyOwnCode();
				expc.setSettingbk(exportbk);
			}
		}
		
		exportcond.add(expc);
		
		decrorateExportButton (expc);
		
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
			} catch (java.time.format.DateTimeParseException e) {
					tfldparsedfile.setText("�ļ���ʽ����������ѡ���ļ���");
					ckboxparsefile.setSelected(false);
					return;
			}
			LocalDate curselectdate = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			if(!curselectdate.equals(localDate) ) {
				ZoneId zone = ZoneId.systemDefault();
				Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
				this.dateChooser.setDate(Date.from(instant));
			}
			
			ckboxparsefile.setSelected(true);
			tfldparsedfile.setText(filename);
			
			if(xmlfileexist) {
				this.bkcyl.patchWeeklyBanKuaiFengXiXmlFileToCylTree (filexminconfigpath,localDate);
			} else { //������Ҫ����
				this.bkcyl.parseWeeklyBanKuaiFengXiFileToXmlAndPatchToCylTree (fileebk,localDate);
			}

//			tableBkZhanBi.repaint();
			filexminconfigpath = null;
			filexminconfigpath = null;

	}
	
	
	/*
	 * �ڸ��ɱ��з�������ĸ���
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
					
					//�ڵ�ǰ����еĻ����Ͱ����PANEL���
					pnlggdpcje.resetDate();
					panelGgDpCjeZhanBi.resetDate();
					paneldayCandle.resetDate();
				}
			}
		}
		
		
		if( ((BanKuaiGeGuTableModel)tablexuandingzhou.getModel() ).getRowCount() >0) {
			rowindex = ((BanKuaiGeGuTableModel)tablexuandingzhou.getModel() ).getStockRowIndex(nodecode);
			if(rowindex <0) {
				notfound = true;
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
				notfound = true;
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
				notfound = true;
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
		
		
		if(((BanKuaiGeGuTableModel)tablexuandingplustwo.getModel() ).getRowCount() >0 ) {
			rowindex = ((BanKuaiGeGuTableModel)tablexuandingplustwo.getModel() ).getStockRowIndex(nodecode);
			if(rowindex <0) {
				notfound = true;
			} else {
				int modelRow = tablexuandingplustwo.convertRowIndexToView(rowindex);
				int curselectrow = tablexuandingplustwo.getSelectedRow();
				if( curselectrow != modelRow) {
					tablexuandingplustwo.setRowSelectionInterval(modelRow, modelRow);
					tablexuandingplustwo.scrollRectToVisible(new Rectangle(tablexuandingplustwo.getCellRect(modelRow, 0, true)));
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
		editorPanenodeinfo.setText("");
		editorPanenodeinfo.displayNodeAllInfo(selectedbk);
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
	private JMenuItem menuItemRmvNodeFmFile;
	private JButton btnshizhifx;
	private JCheckBox cbxshizhifx;
	
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
//		panelbkcje.setBarDisplayedColor(Color.CYAN);
		
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
		
		
		exportCancelAction = new AbstractAction("������������") {

		      private static final long serialVersionUID = 4669650683189592364L;

		      @Override
		      public void actionPerformed(final ActionEvent e) {
		    	
		        if (exporttask == null) { 
		        	String msg =  "���������������ø��ɻ��ǵ�����ǰ���ø��ɣ�ѡ��ȷ���������������������ø��ɡ�";
			  		int exchangeresult = JOptionPane.showConfirmDialog(null,msg , "����ѡ��", JOptionPane.OK_CANCEL_OPTION);
			  		if(exchangeresult == JOptionPane.CANCEL_OPTION) //�û�ѡ�񵼳���ǰ���� 
			  			exportBanKuaiWithGeGuOnCondition2();
			  		else { //�û�ѡ�񵼳���������
			  			initializeNormalExportConditions ();
			  			exportBanKuaiWithGeGuOnCondition2();
			  		}
			  			
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
		
		tableBkZhanBi = new BanKuaiInfoTable(this.stockmanager);	
		
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
		tabbedPanegegu.addTab("��ǰ��", null, scrollPanedangqian, null);
		tabbedPanegegu.setBackgroundAt(0, Color.ORANGE);
		
		
		tableGuGuZhanBiInBk = new BanKuaiGeGuTable (this.stockmanager);
//		tableGuGuZhanBiInBk.hideZhanBiColumn(1);
//		tableGuGuZhanBiInBk.sortByZhanBiGrowthRate();
		scrollPanedangqian.setViewportView(tableGuGuZhanBiInBk);
		
		JScrollPane scrollPanexuanding = new JScrollPane();
		tabbedPanegegu.addTab("ѡ����", null, scrollPanexuanding, null);
		tabbedPanegegu.setBackgroundAt(1, UIManager.getColor("MenuItem.selectionBackground"));
		
		tablexuandingzhou = new BanKuaiGeGuTable (this.stockmanager);
//		tablexuandingzhou.hideZhanBiColumn(1);

		scrollPanexuanding.setViewportView(tablexuandingzhou);
		
		menuItemRmvNodeFmFile = new JMenuItem("�޳���ģ���ļ�") ;
		tableGuGuZhanBiInBk.getPopupMenu().add(menuItemRmvNodeFmFile);
		
		JScrollPane scrollPanexuandingminusone = new JScrollPane();
		tabbedPanegegu.addTab("\u9009\u5B9A\u5468-1", null, scrollPanexuandingminusone, null);
		tabbedPanegegu.setBackgroundAt(2, Color.LIGHT_GRAY);
		
		tablexuandingminusone = new BanKuaiGeGuTable (this.stockmanager);
//		tablexuandingminusone.hideZhanBiColumn(1);
		scrollPanexuandingminusone.setViewportView(tablexuandingminusone);
		
		JScrollPane scrollPanexuandingminustwo = new JScrollPane();
		tabbedPanegegu.addTab("\u9009\u5B9A\u5468-2", null, scrollPanexuandingminustwo, null);
		
		tablexuandingminustwo = new BanKuaiGeGuTable (this.stockmanager);
//		tablexuandingminustwo.hideZhanBiColumn(1);
		scrollPanexuandingminustwo.setViewportView(tablexuandingminustwo);
		
		JScrollPane scrollPanexuandingplusone = new JScrollPane();
		tabbedPanegegu.addTab("\u9009\u5B9A\u5468+1", null, scrollPanexuandingplusone, null);
		
		tablexuandingplusone = new BanKuaiGeGuTable (this.stockmanager);
//		tablexuandingplusone.hideZhanBiColumn(1);
		scrollPanexuandingplusone.setViewportView(tablexuandingplusone);
		
		JScrollPane scrollPanexuandingplustwo = new JScrollPane();
		tabbedPanegegu.addTab("\u9009\u5B9A\u5468+2", null, scrollPanexuandingplustwo, null);
		
		tablexuandingplustwo = new BanKuaiGeGuTable (this.stockmanager);
//		tablexuandingplustwo.hideZhanBiColumn(1);
		scrollPanexuandingplustwo.setViewportView(tablexuandingplustwo);
		
//		tableBkZhanBi = new BanKuaiGeGuTable (this.stockmanager);
		
		
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
		        	
		        	LocalDate cursettingdate = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		        	if(!cursettingdate.equals(LocalDate.now())) {
		        		LocalDate newdate =  LocalDate.now();
						dateChooser.setDate(Date.from(newdate.atStartOfDay(ZoneId.systemDefault()).toInstant() ) );
						btnresetdate.setEnabled(false);
						btnsixmonthafter.setEnabled(false);
		        	} else {
		        		btnresetdate.setEnabled(false);
		        		btnsixmonthafter.setEnabled(false);
		        	}
		        } else { //������������ʱ������û�����ʱ�䣬��ȡ��������������ʵ�о�Ҳû����Ҫ
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
		progressBarsys.setFont(new Font("����", Font.PLAIN, 9));
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
			
			cbxggquanzhong = new JCheckBox("\u5254\u9664\u80A1\u7968\u6743\u91CD<=");
			cbxggquanzhong.setEnabled(false);
			
			tfldweight = new JTextField();
			tfldweight.setEditable(false);
			tfldweight.setEnabled(false);
			tfldweight.setText("0");
			tfldweight.setColumns(10);
			
			ckboxshowcje = new JCheckBox("\u7A81\u51FA\u663E\u793A\u6210\u4EA4\u989D>(\u4EBF)");
			ckboxshowcje.setBackground(Color.LIGHT_GRAY);
			ckboxshowcje.setForeground(Color.YELLOW);
			
	
			
			tfldshowcje = new JTextField();
			tfldshowcje.setForeground(Color.BLUE);
			tfldshowcje.setColumns(10);
			this.setHighLightChenJiaoEr ();
			
			ckboxparsefile = new JCheckBox("\u6BCF\u5468\u5206\u6790\u6587\u4EF6");
			ckboxparsefile.setForeground(Color.ORANGE);
			
			
			tfldparsedfile = new JTextField();
			tfldparsedfile.setForeground(Color.ORANGE);
			tfldparsedfile.setEditable(false);
			tfldparsedfile.setColumns(10);
			tfldparsedfile.setToolTipText(tfldparsedfile.getText());
			
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
			
			cbxhuanshoulv = new JCheckBox("\u7A81\u51FA\u6362\u624B\u7387>=");
			cbxhuanshoulv.setForeground(Color.BLUE);
			
			tfldhuanshoulv = new JTextField();
			tfldhuanshoulv.setText("30");
			tfldhuanshoulv.setColumns(10);
			
			btnshizhifx = new JButton("\u8BE6\u7EC6\u5E02\u503C\u5206\u6790");
			
			
			cbxshizhifx = new JCheckBox("\u8F93\u51FA\u5E02\u503C\u5206\u6790\u7ED3\u679C");
			
			
			
			GroupLayout gl_buttonPane = new GroupLayout(buttonPane);
			gl_buttonPane.setHorizontalGroup(
				gl_buttonPane.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_buttonPane.createSequentialGroup()
						.addContainerGap()
						.addComponent(btnaddexportcond, GroupLayout.PREFERRED_SIZE, 62, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnClear, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(cbxggquanzhong)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(tfldweight, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(ckboxshowcje)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(tfldshowcje, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
						.addGap(4)
						.addComponent(cbxdpmaxwk)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(tflddisplaydpmaxwk, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(cbxbkmaxwk)
						.addGap(12)
						.addComponent(tfldbkmaxwk, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(chkcjemaxwk)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(tfldcjemaxwk, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(cbxhuanshoulv)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(tfldhuanshoulv, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(ckboxparsefile)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(tfldparsedfile, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnChosPasFile)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(cbxshizhifx)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnshizhifx)
						.addGap(39)
						.addComponent(okButton, GroupLayout.PREFERRED_SIZE, 56, GroupLayout.PREFERRED_SIZE)
						.addGap(1638)
						.addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
						.addGap(337))
			);
			gl_buttonPane.setVerticalGroup(
				gl_buttonPane.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_buttonPane.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_buttonPane.createParallelGroup(Alignment.TRAILING)
							.addComponent(cancelButton, GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
							.addGroup(gl_buttonPane.createSequentialGroup()
								.addGroup(gl_buttonPane.createParallelGroup(Alignment.LEADING)
									.addComponent(cbxhuanshoulv)
									.addGroup(gl_buttonPane.createParallelGroup(Alignment.BASELINE)
										.addComponent(tfldhuanshoulv, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(ckboxparsefile, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
									.addGroup(gl_buttonPane.createParallelGroup(Alignment.BASELINE)
										.addComponent(btnClear)
										.addComponent(cbxggquanzhong, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(tfldweight, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(ckboxshowcje, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(tfldshowcje, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(cbxdpmaxwk, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(tflddisplaydpmaxwk, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(cbxbkmaxwk)
										.addComponent(tfldbkmaxwk, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(chkcjemaxwk)
										.addComponent(tfldcjemaxwk, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
										.addComponent(tfldparsedfile, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
									.addComponent(btnaddexportcond, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
								.addContainerGap())
							.addGroup(gl_buttonPane.createSequentialGroup()
								.addGroup(gl_buttonPane.createParallelGroup(Alignment.BASELINE)
									.addComponent(btnChosPasFile, GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
									.addComponent(cbxshizhifx)
									.addComponent(btnshizhifx)
									.addComponent(okButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addContainerGap())))
			);
			buttonPane.setLayout(gl_buttonPane);
		}
		

	
	}
	/*
	 * �������ڼ��Ѿ����ܼ����������Զ����óɽ�����ֵ
	 */
	private void setHighLightChenJiaoEr() 
	{
		LocalDate curselectdate = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		if(LocalDate.now().with(DayOfWeek.FRIDAY).equals(curselectdate.with(DayOfWeek.FRIDAY) ) ) { //˵���ڵ�ǰ�ܣ�������������
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
			
		} else { //Ӧ�ø��ݸ����ж��ٸ������������ã������Ǽ򵥵İ�1.2*5 Լ=5.8
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
			
//			this.setTooltips ();
		}
		
		private Boolean shouldnotexportststocks;
		private Double havedayangxianundercertainchenjiaoer;
		private Double settingcje;
		private Integer settindpgmaxwk;
		private Integer settinbkgmaxwk;
		private Integer settingcjemaxwk;
		private Double setSettinghsl;
		private String settingbk;
		private String tooltips = "";

		public void setHaveDaYangXianUnderCertainChenJiaoEr (double shouldHaveDaYangXianUnderCertainChenJiaoEr) 
		{
			this.havedayangxianundercertainchenjiaoer = shouldHaveDaYangXianUnderCertainChenJiaoEr;
			if(shouldHaveDaYangXianUnderCertainChenJiaoEr > 0.0)
				this.tooltips = this.tooltips + "�ɽ���С��" + shouldHaveDaYangXianUnderCertainChenJiaoEr +  "������5%�����ߡ�";
		}
		public Double shouldHaveDaYangXianUnderCertainChenJiaoEr ()
		{
			return this.havedayangxianundercertainchenjiaoer * 100000000;
		}
		public void setExportSTStocks(boolean shouldNotExportSTStocks) 
		{
			this.shouldnotexportststocks = shouldNotExportSTStocks;
			if(shouldNotExportSTStocks)
				this.tooltips = this.tooltips + "������ST���ɡ�";
		}
		public Boolean shouldNotExportSTStocks()
		{
			return this.shouldnotexportststocks;
		}

		private void setSettingbk(String exportbk) 
		{
			if(exportbk != null ) {
				this.settingbk = exportbk;
				this.tooltips = this.tooltips + "�޶��ڰ��" + settingbk + "�ڡ�";
			} else
				this.settingbk = "";
		}
		public String getSettingbk()
		{
			return this.settingbk;
		}
//		private void setTooltips() 
//		{
//			this.tooltips = this.tooltips + "</br>";
//		}
		public String getTooltips ()
		{
			return this.tooltips + "</br>";
		}
		public Double getSettingcje() {
			return settingcje * 100000000;
		}
		private void setSettingcje(String exportcjelevel) {
			if(exportcjelevel != null) {
				this.settingcje = Double.parseDouble( exportcjelevel );
				this.tooltips = this.tooltips + "�ɽ���>=" + settingcje + "��";
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
				this.tooltips = this.tooltips + "����MAXWK>=" +  settindpgmaxwk + "��";
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
				this.tooltips = this.tooltips + "���MAXWK>=" + settinbkgmaxwk + "��";
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
				this.tooltips = this.tooltips + "�ɽ���MAXWK>=" + settingcjemaxwk + "��";
			}
			else
				this.settingcjemaxwk = -100000000;
		}
		private void setSettinghsl(String exporthsl) 
		{
			if(exporthsl != null) {
				this.setSettinghsl = Double.parseDouble(exporthsl);
				this.tooltips = this.tooltips + "������>=" + exporthsl + "%";
			} else
				this.setSettinghsl = -1.0;
		}
		public Double getSettinghsl ()
		{
			return this.setSettinghsl;
		}
	}
	/*
	 * 锟斤拷锟斤拷锟斤拷锟缴猴拷时太锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷�锟斤拷锟竭筹拷专锟脚达拷锟斤拷锟斤拷锟斤拷锟斤拷GUI锟睫凤拷使锟斤拷
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
		 * 
		 */
		private Integer exportBanKuaiByCondition (BkChanYeLianTreeNode treeroot,ExportCondition expc,ArrayList<BkChanYeLianTreeNode> outputnodeslist)
		{
			String settingbk = expc.getSettingbk();
			Double settingcje = expc.getSettingcje() ;
			Integer settindpgmaxwk = expc.getSettindpgmaxwk();
			Integer settinbkgmaxwk = expc.getSettinbkgmaxwk();
			Integer seetingcjemaxwk = expc.getSettingcjemaxwk();
			Double settinghsl = expc.getSettinghsl();
			
			int bankuaicount = bkcyltree.getModel().getChildCount(treeroot);
			
			//�������
			if(settindpgmaxwk < 0) //�԰����˵��dpmaxwk�������ã�����԰����˵ֻ��ɽ���û�����塣
				return 30;
			
			for(int i=0;i< bankuaicount; i++) {
					if (isCancelled())
						 return null;
					
					BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode)bkcyltree.getModel().getChild(treeroot, i);
					if(childnode.getType() != BanKuaiAndStockBasic.TDXBK)
						continue;
					

					if( ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL) 
					 ||  ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL) //��Щָ����û�и��ɺͳɽ����ģ�������ȽϷ�Χ 
					 ||  ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.NOGGWITHSELFCJL) ) //�������и��ɵİ��
						continue;
					
					if( !settingbk.isEmpty()  ) {
						if(!childnode.getMyOwnCode().equals(settingbk))
							continue;
					}
					
					NodeXPeriodDataBasic nodexdata = childnode.getNodeXPeroidData(period);
					if(nodexdata.hasRecordInThePeriod(selectiondate,0) ) { //��鵱��û������Ҳ�����ǣ����һ�㲻����û�����ݣ�û������˵���ð�����ܻ�û�е��������߹�ȥ�У�����ǰ���ڳɽ����Ѿ����ٴ������ݿ�
							Double recordcje = nodexdata.getChengJiaoEr(selectiondate, 0);
							Integer recordmaxbkwk = nodexdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(selectiondate,0);
							Integer recordmaxcjewk =nodexdata.getChenJiaoErMaxWeekOfSuperBanKuai(selectiondate,0);
							
							
							if( recordcje >= settingcje &&  recordmaxbkwk >= settindpgmaxwk && recordmaxcjewk >= seetingcjemaxwk ) { //�������������� ; ���͸��ɲ�һ����ֻ��һ��ռ��
								if(!outputnodeslist.contains(childnode)){
									outputnodeslist.add(childnode);
								}
							 }
					}
			}
		
			
			return 30;
		}
		/*
		 *˵���û�û��ѡ����ɰ��ռ��������ֱ����tree �ĸ��ɣ������ 
		 */
		private Integer exportStockByCondition (BkChanYeLianTreeNode treeroot,ExportCondition expc,ArrayList<BkChanYeLianTreeNode> outputnodeslist)
		{
			String settingbk = expc.getSettingbk();
			Double settingcje = expc.getSettingcje() ;
			Integer settindpgmaxwk = expc.getSettindpgmaxwk();
			Integer settinbkgmaxwk = expc.getSettinbkgmaxwk();
			Integer seetingcjemaxwk = expc.getSettingcjemaxwk();
			Double settinghsl = expc.getSettinghsl();
			
			if(settindpgmaxwk <0) // �û�û������maxdpwk;
				return 30;
			
			if(settinbkgmaxwk >0 ) //�û������˰���ڲ��ıȽϣ�Ҫ������ķ���
				return 30;
			
			if(!settingbk.isEmpty()) //�û������˰�飬Ҫ������ķ���
				return 30;
			
			int bankuaicount = bkcyltree.getModel().getChildCount(treeroot);
			
			for(int i=0;i< bankuaicount; i++) {
				if (isCancelled())
					 return null;
				
				BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode)bkcyltree.getModel().getChild(treeroot, i);
				if(childnode.getType() != BanKuaiAndStockBasic.TDXGG)
					continue;

				childnode = allbksks.getStock((Stock)childnode,selectiondate,StockGivenPeriodDataItem.WEEK);
				
				try{
					if(childnode.getMyOwnName().toUpperCase().contains("ST") && expc.shouldNotExportSTStocks() )
						continue;
				} catch (java.lang.NullPointerException e) {
					continue;
//					e.printStackTrace();
//					logger.debug(childnode.getMyOwnName());
				}

				NodeXPeriodDataBasic nodexdata = childnode.getNodeXPeroidData(period);
				 //��鵱��û������Ҳ�����ǣ����һ�㲻����û�����ݣ�û������˵���ð�����ܻ�û�е��������߹�ȥ�У����ڳɽ����Ѿ����������ݿ�
				// ͬʱ�����е��¹�Ҳ������
				if(nodexdata.hasRecordInThePeriod(selectiondate,0) && !((StockNodeXPeriodData)nodexdata).isVeryVeryNewXinStock()   ) {
						Double recordcje = nodexdata.getChengJiaoEr(selectiondate, 0);
						Integer recordmaxbkwk = nodexdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(selectiondate,0);
						Integer recordmaxcjewk =nodexdata.getChenJiaoErMaxWeekOfSuperBanKuai(selectiondate,0);
						Double recordhsl = ( (StockNodeXPeriodData)nodexdata).getSpecificTimeHuanShouLv(selectiondate, 0);
						
						if( recordcje >= settingcje &&  recordmaxbkwk >= settindpgmaxwk 
								&& recordmaxcjewk >= seetingcjemaxwk && recordhsl>=settinghsl) { 
							
							if( expc.shouldHaveDaYangXianUnderCertainChenJiaoEr() >0 ) { //����ɽ���С����һ��������ǰ3�ܱ����д����߻�������2����������
								if( recordcje < expc.shouldHaveDaYangXianUnderCertainChenJiaoEr() ) {
									Boolean skiptonextstock = true;
									int searchbtmindex = -3; //�趨��ǰ���ݼ����Ҵ����ߣ����ڶ�Ϊ3��
									for(int wkindex = 0;wkindex > searchbtmindex;wkindex--) { //��ǰ3�ܵ����ݣ�������û�д�����
										Double hzdf = ( (StockNodeXPeriodData)nodexdata).getSpecificTimeHighestZhangDieFu(selectiondate, wkindex);
										if(hzdf != null && hzdf >= 5.0) { //�������Ƿ������ڶ�Ϊ5%
											skiptonextstock = false;
											break;
										} else 
										if(hzdf == null) { //˵������ͣ�ƻ����Ǵ������У���Ҫ��ǰ�����
											searchbtmindex = searchbtmindex -1;
										}
										
									}
									
//									if(skiptonextstock) { //���û�д����ߣ�������û������2����������
//										Double recordcjelast = nodexdata.getChengJiaoEr(selectiondate, -1);
//										Integer recordmaxbkwklast = nodexdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(selectiondate,-1);
//										Integer recordmaxcjewklast =nodexdata.getChenJiaoErMaxWeekOfSuperBanKuai(selectiondate,-1);
//										Double recordhsllast = ( (StockNodeXPeriodData)nodexdata).getSpecificTimeHuanShouLv(selectiondate, -1);
//										if(recordcjelast == null ||   recordmaxbkwklast == null  || recordmaxcjewklast == null || recordhsllast == null) { 
//											//Ϊ�գ�˵������ͣ�ƻ��ߴ������У�Ŀǰ��Ϊ�Ͳ��ٲ���һ����
//											skiptonextstock = false;
//										} else										
//										if( recordcjelast >= settingcje &&  recordmaxbkwklast >= settindpgmaxwk 
//												&& recordmaxcjewklast >= seetingcjemaxwk && recordhsllast >= settinghsl) {
//											skiptonextstock = false;
//										}
//									}
									
									if(skiptonextstock)
										continue;
								}
							}
							
							if(!outputnodeslist.contains(childnode)){
								outputnodeslist.add(childnode);
							}
						}
				}
			}
			
			return 50;
		}
		/*
		 * �԰���ڵĸ��ɰ���������
		 */
		private Integer exportStockOfBanKuaiByCondition (BkChanYeLianTreeNode treeroot,ExportCondition expc,ArrayList<BkChanYeLianTreeNode> outputnodeslist)
		{
			String settingbk = expc.getSettingbk();
			Double settingcje = expc.getSettingcje() ;
			Integer settindpgmaxwk = expc.getSettindpgmaxwk();
			Integer settinbkgmaxwk = expc.getSettinbkgmaxwk();
			Integer seetingcjemaxwk = expc.getSettingcjemaxwk();
			Double settinghsl = expc.getSettinghsl();
			
			if(settinbkgmaxwk <0)
				return 60;
			
			int bankuaicount = bkcyltree.getModel().getChildCount(treeroot);
			
			for(int i=0;i< bankuaicount; i++) {
				if (isCancelled())
					 return null;
				
				BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode)bkcyltree.getModel().getChild(treeroot, i);
				if(childnode.getType() != BanKuaiAndStockBasic.TDXBK)
					continue;
				
				if(!settingbk.isEmpty()) { //����޶��˰�飬��ֻ�ڸð������
					if(!childnode.getMyOwnCode().equals(settingbk))
						continue;
				}
				
				if( ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL) 
				 ||  ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL) //��Щָ����û�и��ɺͳɽ����ģ�������ȽϷ�Χ 
				 ||  ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.NOGGWITHSELFCJL) ) //�������и��ɵİ��
					continue;
				
				NodeXPeriodDataBasic nodexdata = childnode.getNodeXPeroidData(period);
				if(nodexdata.hasRecordInThePeriod(selectiondate,0)  ) { //��鵱��û������Ҳ�����ǣ����һ�㲻����û�����ݣ�û������˵���ð�����ܻ�û�е��������߹�ȥ�У����ڳɽ����Ѿ����������ݿ�
					childnode = allbksks.getAllGeGuOfBanKuai (((BanKuai)childnode),period);
					Set<StockOfBanKuai> rowbkallgg = ((BanKuai)childnode).getSpecificPeriodBanKuaiGeGu(selectiondate,0,period);
					for (StockOfBanKuai stockofbankuai : rowbkallgg) {
						 if (isCancelled()) 
							 return null;
						 
						 try {
							 if(stockofbankuai.getMyOwnName().toUpperCase().contains("ST") && expc.shouldNotExportSTStocks() )
									continue;
						 } catch (java.lang.NullPointerException e) {
							 continue;
//								e.printStackTrace();
//								logger.debug(childnode.getMyOwnName());
							}
						 
						 
						 NodeXPeriodDataBasic stockxdataforbk = stockofbankuai.getNodeXPeroidData( period);
						 if(!stockxdataforbk.hasRecordInThePeriod(selectiondate, 0)  )
							 continue;
						 
						 Double recordcje = stockxdataforbk.getChengJiaoEr(selectiondate, 0);
						 Integer recordmaxbkwk = stockxdataforbk.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(selectiondate,0) ;
						 
						 Stock ggstock = stockofbankuai.getStock();
						 NodeXPeriodDataBasic stockxdata = ggstock.getNodeXPeroidData(globeperiod);
						 
						 if( ((StockNodeXPeriodData)stockxdata).isVeryVeryNewXinStock() ) // �����е��¹�Ҳ������
							 continue;
						 
						 Integer recordmaxdpwk = stockxdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(selectiondate,0);
						 Integer recordmaxcjewk = stockxdata.getChenJiaoErMaxWeekOfSuperBanKuai(selectiondate,0);
						 Double recordhsl = ((StockNodeXPeriodData)stockxdata).getSpecificTimeHuanShouLv(selectiondate, 0);
						 
						 if(recordcje >= settingcje &&  recordmaxbkwk >= settinbkgmaxwk
									 && recordmaxcjewk >= seetingcjemaxwk && recordmaxdpwk >=  settindpgmaxwk && recordhsl>=settinghsl)  { //�������������� ; 
							 
								if( expc.shouldHaveDaYangXianUnderCertainChenJiaoEr() >0 ) { //����ɽ���С����һ��������ǰ3�ܱ����д�����
									if( recordcje < expc.shouldHaveDaYangXianUnderCertainChenJiaoEr() ) {
										Boolean skiptonextstock = true;
										for(int wkindex=0;wkindex>-3;wkindex--) {
											Double hzdf = ( (StockNodeXPeriodData)nodexdata).getSpecificTimeHighestZhangDieFu(selectiondate, wkindex);
											if(hzdf != null && hzdf >= 5.0) {
												skiptonextstock = false;
												break;
											}
										}
										
										if(skiptonextstock) { //���û�д����ߣ�������û������2�ܷ���
											Double recordcjelast = stockxdataforbk.getChengJiaoEr(selectiondate,-1);
											Integer recordmaxbkwklast = stockxdataforbk.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(selectiondate,-1) ;
											Integer recordmaxdpwklast = stockxdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(selectiondate,-1);
											Integer recordmaxcjewklast = stockxdata.getChenJiaoErMaxWeekOfSuperBanKuai(selectiondate,-1);
											Double recordhsllast = ((StockNodeXPeriodData)stockxdata).getSpecificTimeHuanShouLv(selectiondate, -1);
											
											if(recordcjelast >= settingcje &&  recordmaxbkwklast >= settinbkgmaxwk
													 && recordmaxcjewklast >= seetingcjemaxwk && recordmaxdpwklast >=  settindpgmaxwk && recordhsllast >=settinghsl)  { //�������������� ;
												skiptonextstock = false;
											}
										}
										
										if(skiptonextstock)
											continue;
									}
								}
							 
							 if(!outputnodeslist.contains(ggstock)) {
									outputnodeslist.add(ggstock);
							 }
						 } 
					}
				}
				
			}
			
			return 80;
		}
		/*
	     * Main task. Executed in background thread.
	     */
	    @Override
	    public Integer doInBackground() 
	    {
	    	Charset charset = Charset.forName("GBK") ;
				
	    	BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)bkcyltree.getModel().getRoot();
			int bankuaicount = bkcyltree.getModel().getChildCount(treeroot);
//			HashSet<String> outputresultset = new HashSet<String> ();
			ArrayList<BkChanYeLianTreeNode> outputnodeslist = new ArrayList<BkChanYeLianTreeNode> ();
			
			String ouputfilehead2 = "";
			int progressint = 30;
			for(ExportCondition expc : expclist) {
				if (isCancelled()) 
					 return null;
				
				String settingbk = expc.getSettingbk();
				Double settingcje = expc.getSettingcje() ;
				Integer settindpgmaxwk = expc.getSettindpgmaxwk();
				Integer settinbkgmaxwk = expc.getSettinbkgmaxwk();
				Integer seetingcjemaxwk = expc.getSettingcjemaxwk();
				Double settinghsl = expc.getSettinghsl();
				
				ouputfilehead2 = ouputfilehead2 + "[����:"+ "�޶��ڰ��:" + settingbk + "��;�ɽ���>=" + settingcje + ";����maxwk>=" + settindpgmaxwk
										+ ";���maxwk>=" + settinbkgmaxwk + ";�ɽ���maxwk>=" + seetingcjemaxwk
										+ ";������>=" + settinghsl
										+"]"
								;
				
				//�������
				exportBanKuaiByCondition (treeroot, expc, outputnodeslist);
				exportStockByCondition (treeroot, expc, outputnodeslist);
				exportStockOfBanKuaiByCondition (treeroot, expc, outputnodeslist);
				
				setProgress(progressint);
				progressint = progressint + 20;
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
		
			//����gephi�ļ�
//			GephiFilesGenerator gephigenerator = new GephiFilesGenerator (allbksks);
//			gephigenerator.generatorGephiFile(outputfile, dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), globeperiod);
  			
  			setProgress( 90 );
  			//�Ѱ������ļ��Ľ��תΪXML��������XML����
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
				get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
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
				if(childnode.getType() != BanKuaiAndStockBasic.TDXBK) 
					continue;
				
				if( ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL) ||  ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL)  ) //��Щָ����û�и��ɺͳɽ����ģ�������ȽϷ�Χ
					continue;
				
				if( !((BanKuai)childnode).isShowinbkfxgui() )
					continue;
				
				String bkcode = childnode.getMyOwnCode();
				logger.debug(bkcode);
				childnode = (BanKuai)this.allbksks.getBanKuai(((BanKuai)childnode), curselectdate,period); 
				BanKuai.BanKuaiNodeXPeriodData bkxdata = (BanKuai.BanKuaiNodeXPeriodData)childnode.getNodeXPeroidData(period);
	    		if(bkxdata.hasRecordInThePeriod(curselectdate, 0) != null ) {//��鵱��û������Ҳ�����ǣ����һ�㲻����û�����ݣ�û������˵���ð�����ܻ�û�е��������߹�ȥ�У����ڳɽ����Ѿ����������ݿ�
	    			((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).addBanKuai(((BanKuai)childnode));
	    		}
	    		
	    		//��ʾ���̳ɽ���
	    		
	    		if(bkcode.equals("399001") ) {
	    			BanKuai.BanKuaiNodeXPeriodData bkszdata = (BanKuai.BanKuaiNodeXPeriodData)childnode.getNodeXPeroidData(StockGivenPeriodDataItem.WEEK);
	    			try {
		    			Double cjerec = bkszdata.getChengJiaoEr(curselectdate,0);
	    				lblszcje.setText( cjerec.toString() );
	    			} catch (java.lang.NullPointerException e) {
	    				lblszcje.setText( "����û�гɽ���" );
	    			}
	    			
	    		} else if(bkcode.equals("999999") ) {
	    			BanKuai.BanKuaiNodeXPeriodData bkshdata = (BanKuai.BanKuaiNodeXPeriodData)childnode.getNodeXPeroidData(StockGivenPeriodDataItem.WEEK);
	    			try {
	    				Double cjerec = bkshdata.getChengJiaoEr(curselectdate,0);
	    				lblshcje.setText( cjerec.toString() );
	    			} catch (java.lang.NullPointerException e) {
	    				lblshcje.setText( "����û�гɽ���" );
	    			}
	    		}
			}

			((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).refresh(curselectdate,0,period,null);
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
        
        return cje2.compareTo(cje1);
    }
}

