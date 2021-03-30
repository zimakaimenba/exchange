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
import javax.swing.tree.DefaultTreeModel;

import javax.swing.tree.TreePath;


import org.apache.commons.io.comparator.LastModifiedFileComparator;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
import com.exchangeinfomanager.NodesServices.SvsForNodeOfBanKuai;
import com.exchangeinfomanager.NodesServices.SvsForNodeOfStock;
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
import com.exchangeinfomanager.Trees.BanKuaiAndStockTree;
import com.exchangeinfomanager.Trees.CreateExchangeTree;
import com.exchangeinfomanager.bankuaifengxi.CandleStick.BanKuaiFengXiCandlestickPnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartCjePnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartPnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiNodeCombinedCategoryPnl;
import com.exchangeinfomanager.bankuaifengxi.PieChart.BanKuaiFengXiPieChartCjePnl;

import com.exchangeinfomanager.bankuaifengxi.PieChart.BanKuaiFengXiPieChartPnl;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.VoiceEngine;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.WeeklyAnalysis;
import com.exchangeinfomanager.bankuaifengxi.bankuaigegutable.BanKuaiGeGuBasicTable;
import com.exchangeinfomanager.bankuaifengxi.bankuaigegutable.BanKuaiGeGuBasicTableModel;
import com.exchangeinfomanager.bankuaifengxi.bankuaigegutable.BanKuaiGeGuExternalInfoTableFromPropertiesFile;
import com.exchangeinfomanager.bankuaifengxi.bankuaiinfotable.BanKuaiInfoTable;
import com.exchangeinfomanager.bankuaifengxi.bankuaiinfotable.BanKuaiInfoTableModel;
import com.exchangeinfomanager.bankuaifengxi.xmlhandlerforbkfx.ServicesForBkfxEbkOutPutFileDirectRead;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.commonlib.ParseBanKuaiWeeklyFielGetStocksProcessor;
import com.exchangeinfomanager.commonlib.ReminderPopToolTip;
import com.exchangeinfomanager.commonlib.ScrollUtil;
import com.exchangeinfomanager.commonlib.SystemAudioPlayed;

import com.exchangeinfomanager.commonlib.jstockcombobox.JStockComboBox;
import com.exchangeinfomanager.commonlib.jstockcombobox.JStockComboBoxModel;


import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.gui.subgui.DateRangeSelectPnl;
import com.exchangeinfomanager.gui.subgui.PaoMaDeng2;
import com.exchangeinfomanager.guifactory.JLabelFactory;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.DaPan;
import com.exchangeinfomanager.nodes.ServicesForNode;
import com.exchangeinfomanager.nodes.ServicesForNodeBanKuai;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.nodes.treerelated.NodesTreeRelated;
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
import com.toedter.calendar.JDateChooser;
import javax.swing.JCheckBox;
import javax.swing.JComponent;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;


import java.sql.SQLException;
import java.text.DecimalFormat;

import java.time.DayOfWeek;
import java.time.LocalDate;

import java.time.ZoneId;

import java.time.temporal.ChronoUnit;
import java.beans.PropertyChangeEvent;
import java.awt.event.MouseAdapter;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import javax.swing.plaf.metal.MetalScrollBarUI;

import javax.swing.UIManager;
import javax.swing.AbstractAction;
import javax.swing.Action;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JFileChooser;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

import javax.swing.JScrollBar;

import com.exchangeinfomanager.bankuaifengxi.bankuaigegutable.BanKuaiGeGuExternalInfoTableModelFromPropertiesFile;
import com.exchangeinfomanager.bankuaifengxi.bankuaigegutable.BanKuaiGeGuTableFromPropertiesFile;
import com.exchangeinfomanager.bankuaifengxi.bankuaigegutable.BanKuaiGeGuTableModelFromPropertiesFile;
import com.exchangeinfomanager.bankuaifengxi.BankuaiAndGeguTableBasic.BandKuaiAndGeGuTableBasicModel;


import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.BoxLayout;
import java.awt.FlowLayout;



public class BanKuaiFengXi extends JDialog 
{
	private static final long serialVersionUID = 1L;

	private BanKuaiAndStockTree treeofbkstk;
	/**
	 * Create the dialog.
	 * @param bkcyl2 
	 */
	public BanKuaiFengXi(StockInfoManager stockmanager1, String screenresoltuion)
	{
		this.stockmanager = stockmanager1;
		
		treeofbkstk = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks();
		
		this.nodeinfotocsv = new NodeInfoToCsv ();
		this.bkfxremind = new BanKuaiFengXiRemindXmlHandler ();
		this.bkggmatchcondition = new BanKuaiGeGuMatchCondition ();

		this.globecalwholeweek = true; //��������
		
		setupBkfxSettingProperties ();
		setupBkfxHighLightSettingProperties ();
		
		if(screenresoltuion.equals("2560") )
			initializeGuiOf2560Resolution ();
		else 
			initializeGuiOfNormal ();
		initializeKuaiJieZhishuPnl ();
			
		createEvents ();
		setUpChartDataListeners ();

		initializePaoMaDeng ();
		
		adjustDate ( LocalDate.now());
	}
	
	private static Logger logger = Logger.getLogger(BanKuaiFengXi.class);
 	
	private Properties bkfxsettingprop;
	private Properties highlightpnlprop;
	private String globeperiod;
	private Boolean globecalwholeweek; //��������
	private BanKuaiFengXiRemindXmlHandler bkfxremind;
	private LocalDate lastselecteddate;

//	private SetupSystemConfiguration sysconfig;
	private StockInfoManager stockmanager;
	private BanKuaiGeGuMatchCondition bkggmatchcondition;
	private VoiceEngine readengine;
	
	private NodeInfoToCsv nodeinfotocsv;

	private Set<BarChartPanelHightLightColumnListener> chartpanelhighlightlisteners;
	private Set<BarChartPanelDataChangedListener> barchartpanelbankuaidatachangelisteners;
	private Set<PieChartPanelDataChangedListener> piechartpanelbankuaidatachangelisteners;
//	private Set<BarChartPanelDataChangedListener> barchartpanelstockofbankuaidatachangelisteners;
	private Set<BarChartPanelDataChangedListener> barchartpanelstockdatachangelisteners;

	private Set<BanKuaiGeGuBasicTable> tablebkggtableset;

	private void setupBkfxHighLightSettingProperties ()
	{
		// TODO Auto-generated method stub
		File directory = new File("");//�趨Ϊ��ǰ�ļ���
		String systeminstalledpath = null;
		try{
		    Properties properties = System.getProperties();
		    systeminstalledpath = toUNIXpath(properties.getProperty("user.dir")+ "\\"); //�û����г���ĵ�ǰĿ¼
		} catch(Exception e) {System.exit(0);}
		
		try {
			highlightpnlprop = new Properties();
			String propFileName =   (new SetupSystemConfiguration()).getSystemInstalledPath() + "/config/" +  bkfxsettingprop.getProperty ("BkfxHighlightPnlInfoSettingFile")  + "/";
			FileInputStream inputStream = new FileInputStream(propFileName);
			if (inputStream != null) 
				highlightpnlprop.load(inputStream);
			 
			inputStream.close();
			
		} catch (Exception e) {	e.printStackTrace();} finally {}
	}
	
	private void setupBkfxSettingProperties() 
	{
		// TODO Auto-generated method stub
		File directory = new File("");//�趨Ϊ��ǰ�ļ���
		String systeminstalledpath = null;
		try{
		    Properties properties = System.getProperties();
		    systeminstalledpath = toUNIXpath(properties.getProperty("user.dir")+ "\\"); //�û����г���ĵ�ǰĿ¼
		} catch(Exception e) {System.exit(0);}
		
		try {
			bkfxsettingprop = new Properties();
			String propFileName =  (new SetupSystemConfiguration()).getBankuaifenxiSettingPropertiesFile();
			FileInputStream inputStream = new FileInputStream(propFileName);
			if (inputStream != null) 
				bkfxsettingprop.load(inputStream);
			 
			inputStream.close();
			
		} catch (Exception e) {	e.printStackTrace();} finally {}
	}
	private  String toUNIXpath(String filePath) 
   	{
   		    return filePath.replace('\\', '/');
   	}
	/*
	 * �û��ڽ���Ĳ���������ģ���Эͬ����
	 */
	private void setUpChartDataListeners ()
	{
		this.chartpanelhighlightlisteners = new HashSet<>();
//		this.barchartpanelstockofbankuaidatachangelisteners = new HashSet<>();
		this.barchartpanelbankuaidatachangelisteners = new HashSet<>();
		this.piechartpanelbankuaidatachangelisteners = new HashSet<>();
		this.barchartpanelstockdatachangelisteners = new HashSet<>();
		this.tablebkggtableset = new HashSet<>();
		
		this.tablebkggtableset.add(tableGuGuZhanBiInBk);
		this.tablebkggtableset.add(tableExternalInfo);
		this.tablebkggtableset.add(tablexuandingzhou);
		this.tablebkggtableset.add(tablexuandingminusone);
		this.tablebkggtableset.add(tablexuandingminustwo);
		this.tablebkggtableset.add(tablexuandingplusone);
		this.tablebkggtableset.add(tableTempGeGu);
		
		barchartpanelbankuaidatachangelisteners.add(panelbkwkcjezhanbi);
		barchartpanelbankuaidatachangelisteners.add(pnlbkwkcjlzhanbi);
		//���pie chart
		piechartpanelbankuaidatachangelisteners.add(pnlcurwkggcjezhanbi);
		piechartpanelbankuaidatachangelisteners.add(panelLastWkGeGucjeZhanBi);
		
		
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
		
		((BandKuaiAndGeGuTableBasicModel)tableGuGuZhanBiInBk.getModel()).setDisplayMatchCondition (bkggmatchcondition);
		((BandKuaiAndGeGuTableBasicModel)tableExternalInfo.getModel()).setDisplayMatchCondition (bkggmatchcondition);
		((BandKuaiAndGeGuTableBasicModel)tablexuandingzhou.getModel()).setDisplayMatchCondition (bkggmatchcondition);
		((BandKuaiAndGeGuTableBasicModel)tablexuandingminusone.getModel()).setDisplayMatchCondition (bkggmatchcondition);
		((BandKuaiAndGeGuTableBasicModel)tablexuandingminustwo.getModel()).setDisplayMatchCondition (bkggmatchcondition);
		((BandKuaiAndGeGuTableBasicModel)tablexuandingplusone.getModel()).setDisplayMatchCondition (bkggmatchcondition);
		((BandKuaiAndGeGuTableBasicModel)tableTempGeGu.getModel()).setDisplayMatchCondition (bkggmatchcondition);
		((BandKuaiAndGeGuTableBasicModel)tableBkZhanBi.getModel()).setDisplayMatchCondition (bkggmatchcondition);
		((BandKuaiAndGeGuTableBasicModel)tableselectedwkbkzb.getModel()).setDisplayMatchCondition (bkggmatchcondition);
		
	}
	/*
	 * ���а��ռ�������ʵ�����
	 */
	private void gettBanKuaiZhanBiRangedByGrowthRateOfPeriod (String period)
	{
		this.globeperiod = period;
		
    	LocalDate curselectdate = dateChooser.getLocalDate(); 
    	LocalDate requirestart = CommonUtility.getSettingRangeDate(curselectdate,"middle").with(DayOfWeek.MONDAY);
    	
    	DaPan dapan = (DaPan) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode("000000", BkChanYeLianTreeNode.DAPAN);
    	dapan.getNodeData(requirestart, curselectdate,this.globeperiod,this.globecalwholeweek);
    	
		List<BkChanYeLianTreeNode> bkwithcje = getBanKuaiFenXiQualifiedNodes (curselectdate,this.globeperiod,this.globecalwholeweek,true);
		List<BkChanYeLianTreeNode> dzhbk = getDZHBanKuaiFenXiQualifiedNodes (curselectdate,this.globeperiod,this.globecalwholeweek,true);
		((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).addBanKuai( bkwithcje );
		((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).addBanKuai( dzhbk );
		((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).refresh(curselectdate,0,globeperiod);
		
		//����������ĵ�ǰʱ�䣬Ϊ��ʾ��ע������ʹ��
		((JStockComboBoxModel)combxsearchbk.getModel() ).setCurrentDataDate(curselectdate);
		((JStockComboBoxModel)combxstockcode.getModel() ).setCurrentDataDate(curselectdate);
		
		curselectdate = null;
		SystemAudioPlayed.playSound();
	}
	/*
	 * 
	 */
	private List<BkChanYeLianTreeNode> getBanKuaiFenXiQualifiedNodes (LocalDate checkdate, String globeperiod, Boolean globecalwholeweek, Boolean forcetogetnodedataagain)
	{
		List<BkChanYeLianTreeNode> bkwithcje = new ArrayList<BkChanYeLianTreeNode> ();
		SvsForNodeOfBanKuai svsbk = new SvsForNodeOfBanKuai ();
		
		BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getModel().getRoot();
		int bankuaicount = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getModel().getChildCount(treeroot);

		LocalDate requirestart = CommonUtility.getSettingRangeDate(checkdate,"large").with(DayOfWeek.MONDAY);
		for(int i=0;i< bankuaicount; i++) {
			
			BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getModel().getChild(treeroot, i);
			if(childnode.getType() != BkChanYeLianTreeNode.TDXBK) 
				continue;
			
			if( !((BanKuai)childnode).isShowinbkfxgui() )
				continue;
			
			if( ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL) 
					||  ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL)  ) //��Щָ����û�и��ɺͳɽ����ģ�������ȽϷ�Χ
				continue;
			
			if(forcetogetnodedataagain)
				childnode = svsbk.getNodeData( (BanKuai)childnode, requirestart, checkdate,globeperiod,globecalwholeweek);
			
			NodeXPeriodData bkxdata = ((TDXNodes)childnode).getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
			if(bkxdata.getIndexOfSpecificDateOHLCData(checkdate, 0) != null)//��鵱��û������Ҳ�����ǣ����һ�㲻����û�����ݣ�û������˵���ð�����ܻ�û�е��������߹�ȥ�У����ڳɽ����Ѿ����������ݿ�
				bkwithcje.add( (BanKuai)childnode );
		}
		
		return bkwithcje;
	}
	/*
	 * 
	 */
	private List<BkChanYeLianTreeNode> getDZHBanKuaiFenXiQualifiedNodes (LocalDate checkdate, String globeperiod, Boolean globecalwholeweek, Boolean forcetogetnodedataagain)
	{
		List<BkChanYeLianTreeNode> bkwithcje = new ArrayList<BkChanYeLianTreeNode> ();
		
		BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode) CreateExchangeTree.CreateTreeOfDZHBanKuaiAndStocks().getModel().getRoot();
		int bankuaicount = CreateExchangeTree.CreateTreeOfDZHBanKuaiAndStocks().getModel().getChildCount(treeroot);
		for(int i=0;i< bankuaicount; i++) {
			
			BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode) CreateExchangeTree.CreateTreeOfDZHBanKuaiAndStocks().getModel().getChild(treeroot, i);
			if(childnode.getType() != BkChanYeLianTreeNode.DZHBK) 
				continue;
			
			if( !((BanKuai)childnode).isShowinbkfxgui() )
				continue;
			
			if( ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL) 
					||  ((BanKuai)childnode).getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL)  ) //��Щָ����û�и��ɺͳɽ����ģ�������ȽϷ�Χ
				continue;
			
			bkwithcje.add( (BanKuai)childnode );

		}
		
		return bkwithcje;
	}
	/*
	 * ���һ�����
	 */
	private void unifiedOperationsAfterUserSelectABanKuai (BanKuai selectedbk)
	{
		refreshCurentBanKuaiFengXiResult (selectedbk,globeperiod); //����ڸ��ɵķ������
		
		refreshCurrentBanKuaiTags (selectedbk,globeperiod);
		
		Integer alreadyin = ((JStockComboBoxModel)combxsearchbk.getModel()).hasTheNode(selectedbk.getMyOwnCode());
		if(alreadyin == -1)
			combxsearchbk.updateUserSelectedNode(selectedbk);
		
		displayNodeInfo(selectedbk);
		
		tabbedPanebk.setSelectedIndex(2);
		
		tableBkZhanBi.repaint(); //
		
		cyltreecopy.searchAndLocateNodeInTree (selectedbk);
		
		String ShowBanKuaiRemindInfo   = bkfxsettingprop.getProperty ("ShowBanKuaiRemindInfo");
		if(ShowBanKuaiRemindInfo  != null && ShowBanKuaiRemindInfo .toUpperCase().equals("TRUE") )
				showReminderMessage (bkfxremind.getBankuairemind() );
		
		((BanKuaiGeGuBasicTableModel)tableTempGeGu.getModel()).setInterSectionBanKuai(selectedbk); //Ϊ��ʱ����ͻ���͵�ǰ��齻��������׼��
		BanKuai tmpbk = ((BanKuaiGeGuBasicTableModel)tableTempGeGu.getModel()).getCurDispalyBandKuai();
		if(tmpbk != null) {
			((BanKuaiGeGuBasicTableModel)tableGuGuZhanBiInBk.getModel()).setInterSectionBanKuai(tmpbk); //Ҳͻ������ʱ����н����İ��
		} else
			((BanKuaiGeGuBasicTableModel)tableGuGuZhanBiInBk.getModel()).setInterSectionBanKuai(null); 
		
		bkggmatchcondition.setSettingBanKuai(selectedbk.getMyOwnCode()); //Ϊ���ܵ�����׼��
		
		setFirstSelectedTab ();
	}
	/*
	 * 
	 */
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
					} catch (SQLException e) { e.printStackTrace();
					} finally {	tagsevofnode = null;}
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
	/*
	 * 
	 */
	private void refreshDaPanFengXiResult ()
	{
		paneldayCandle.resetDate();
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
		DaPan dapan = (DaPan) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode("000000", BkChanYeLianTreeNode.DAPAN);
    	dapan.getNodeData(CommonUtility.getSettingRangeDate(curselectdate,"large"), curselectdate,
				globeperiod,this.globecalwholeweek );

		//���ɽ���ռ����ʾ���̵���ƽ���ɽ���Ա�Ͱ�����ƽ���ɽ���Ա�
		panelbkwkcjezhanbi.setDisplayBarOfSpecificBanKuaiCjeCjlInsteadOfSelfCjeCjl (dapan); 
		//�������ռ��
		for(BarChartPanelDataChangedListener tmplistener : barchartpanelbankuaidatachangelisteners) {
				tmplistener.updatedDate(dapan, CommonUtility.getSettingRangeDate(curselectdate,"basic"),curselectdate,globeperiod);
		}
	}
	/*
	 * 
	 */
	protected void refreshCurentBanKuaiFengXiResult(BanKuai selectedbk,String period)  
	{
		clearTheGuiBeforDisplayNewInfoSection2 ();
		clearTheGuiBeforDisplayNewInfoSection3 ();
		tabbedPanebk.setSelectedIndex(0);
		tabbedPanebkzb.setSelectedIndex(0);
		
		selectedbk.getShuJuJiLuInfo().setHasReviewedToday(true);
		LocalDate curselectdate = null;
		try{
			curselectdate = dateChooser.getLocalDate();// dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		} catch (java.lang.NullPointerException e) {
			JOptionPane.showMessageDialog(null,"��������!","Warning",JOptionPane.WARNING_MESSAGE);
			return;
		}
		ServicesForNodeBanKuai svsbk = (ServicesForNodeBanKuai) selectedbk.getBanKuaiService ();
		//һ����ͬ����������Լ�������������
		try {
			svsbk.syncBanKuaiAndItsStocksForSpecificTime(selectedbk, CommonUtility.getSettingRangeDate(curselectdate,"large"), curselectdate,
					globeperiod,this.globecalwholeweek);
		} catch (SQLException e) {	e.printStackTrace(); }
		svsbk = null;
		
		//��ʾK��
		paneldayCandle.displayQueKou(false);
		BanKuai zhishubk =  (BanKuai) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode("999999", BkChanYeLianTreeNode.TDXBK );
		refreshKXianOfTDXNodeWithSuperBanKuai ( selectedbk, zhishubk, NodeGivenPeriodDataItem.DAY );
		
		//�����������
		if(selectedbk.getBanKuaiLeiXing().equals(BanKuai.HASGGWITHSELFCJL)) { //�и��ɲ���Ҫ���£���Щ�����û�и��ɵ�

			//�����������ռ��info
			((BanKuaiGeGuTableModelFromPropertiesFile)tableGuGuZhanBiInBk.getModel()).refresh(selectedbk, curselectdate,period);
			((BanKuaiGeGuExternalInfoTableModelFromPropertiesFile)tableExternalInfo.getModel()).refresh(selectedbk, curselectdate,period); 
			//������ʾ����
			tabbedPanegegu.setTitleAt(0, "��ǰ��" + curselectdate);
			//��ʾ2�ܵİ�����pie chart
			for(PieChartPanelDataChangedListener tmplistener : piechartpanelbankuaidatachangelisteners) {
				tmplistener.updateDate(selectedbk, curselectdate, 0,globeperiod);
			}
		}
		
		//���ɽ���ռ����ʾ���̵���ƽ���ɽ���Ա�Ͱ�����ƽ���ɽ���Ա�
		DaPan treeroot = (DaPan) treeofbkstk.getModel().getRoot();
		panelbkwkcjezhanbi.setDisplayBarOfSpecificBanKuaiCjeCjlInsteadOfSelfCjeCjl (treeroot); 
		//�������ռ��
		for(BarChartPanelDataChangedListener tmplistener : barchartpanelbankuaidatachangelisteners) {
			tmplistener.updatedDate(selectedbk, CommonUtility.getSettingRangeDate(curselectdate,"basic"),curselectdate,globeperiod);
		}
	}
	/*
	 * �û�ѡ��һ������column�����Ӧ����
	 */
	protected void refreshAfterUserSelectBanKuaiColumn (TDXNodes bkcur, String selectedinfo) 
	{
 		LocalDate selectdate = LocalDate.parse(selectedinfo);
		chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(selectdate));
		
		//������������
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
		
		String ShowSelectBanKuaiColumnRemindInfo    = bkfxsettingprop.getProperty ("ShowSelectBanKuaiColumnRemindInfo");
		if(ShowSelectBanKuaiColumnRemindInfo   != null && ShowSelectBanKuaiColumnRemindInfo  .toUpperCase().equals("TRUE") )
			showReminderMessage (bkfxremind.getBankuaicolumnremind() );

		//�������ã���ʾѡ���ܷ�������
		if(!menuItemnonshowselectbkinfo.getText().contains("X"))
			return;
		
		//ѡ���ܵİ���������
		if(bkcur.getType() != BkChanYeLianTreeNode.TDXBK ) // maybe selected node is DaPan, which has no gegu.
			return ;
		
		LocalDate cursetdate = dateChooser.getLocalDate(); 
		if(!CommonUtility.isInSameWeek(selectdate,cursetdate) ) {
			
			List<BkChanYeLianTreeNode> bkwithcje = getBanKuaiFenXiQualifiedNodes(selectdate, globeperiod, globecalwholeweek,false);
			((BanKuaiInfoTableModel)tableselectedwkbkzb.getModel()).addBanKuai( bkwithcje );
			((BanKuaiInfoTableModel)tableselectedwkbkzb.getModel()).refresh(selectdate,0,globeperiod);
			
			//��ʾ������
			if( ((BanKuai)bkcur).getBanKuaiLeiXing().equals(BanKuai.HASGGWITHSELFCJL) ) {//Ӧ�����и��ɵİ��������ʾ���ĸ��ɣ�
//				refreshSpecificBanKuaiFengXiResult (bkcur,selectdate,globeperiod);
				
				LocalDate formerdate = ((BanKuaiGeGuBasicTableModel)tablexuandingzhou.getModel()).getCurDisplayedDate();
				if(formerdate != null) { //ǰ������ʾ��������Ų��tab 4
					//��ʾѡ����+1��Ʊ�������
					((BanKuaiGeGuTableModelFromPropertiesFile)tablexuandingplusone.getModel()).refresh(((BanKuai)bkcur), formerdate,globeperiod);
					tabbedPanegegu.setTitleAt(5,  formerdate.toString() );
				}
				
				//��ʾѡ���ܹ�Ʊ�������
				((BanKuaiGeGuTableModelFromPropertiesFile)tablexuandingzhou.getModel()).refresh(((BanKuai)bkcur), selectdate,globeperiod);
				tabbedPanegegu.setTitleAt(2, this.getTabbedPanegeguTabTiles(2) + selectdate);
				
				//��ʾѡ����-1��Ʊ�������
				LocalDate selectdate2 = selectdate.minus(1,ChronoUnit.WEEKS).with(DayOfWeek.FRIDAY);
				((BanKuaiGeGuTableModelFromPropertiesFile)tablexuandingminusone.getModel()).refresh(((BanKuai)bkcur), selectdate2,globeperiod);
				tabbedPanegegu.setTitleAt(3,  selectdate2 + "(-1)");

				
				//��ʾѡ����-2��Ʊ�������
				LocalDate selectdate3 = selectdate.plus(1,ChronoUnit.WEEKS).with(DayOfWeek.FRIDAY);
				((BanKuaiGeGuTableModelFromPropertiesFile)tablexuandingminustwo.getModel()).refresh(((BanKuai)bkcur), selectdate3,globeperiod);
				tabbedPanegegu.setTitleAt(4,  selectdate3 + "(+1)");
				
				panelLastWkGeGucjeZhanBi.updateDate(bkcur, selectdate, 0,globeperiod); //��ʾѡ����PIE
			}
			
			tabbedPanebk.setTitleAt(1, "ѡ����" + selectdate);
			//�����ҵ����
			Integer rowindex = ((BanKuaiInfoTableModel)tableselectedwkbkzb.getModel() ).getNodeRowIndex(bkcur.getMyOwnCode());
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
	 * ���һ�� ����
	 * @param selectstock
	 * @param selectedGeguTable
	 */
	protected void unifiedOperationsAfterUserSelectAStock(StockOfBanKuai selectstock, Boolean readinfoout) 
	{
			Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
			setCursor(hourglassCursor);
			
			try{ //����û�ѡ��ĺ��ϴ�ѡ��ĸ���һ�������ظ���������
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
			
			this.panelGgDpCjeZhanBi.setDrawAverageDailyCjeOfWeekLine(true); //��֤������ʾ���ϱ����վ��ɽ���±���ռ����
			
//			findInputedNodeInTable ( selectstock.getMyOwnCode() );
			hightlightSpecificSector (selectstock); //D
//			refreshGeGuFengXiResult (selectstock); //���ɶ԰�������
			
			refreshTDXGeGuZhanBi ( selectstock.getStock() ); //���ɶԴ�������
			
			paneldayCandle.displayQueKou(true);
//			refreshKXianOfTDXNodeWithSuperBanKuai (selectstock, selectstock.getBanKuai() ); //���ɶ԰���K��
			refreshKXianOfTDXNodeWithSuperBanKuai (selectstock, null, NodeGivenPeriodDataItem.DAY ); //���bankuai = null,��������ʾK�ߺͳɽ���
			
			//ͬ�����͸��ɸ���������
			LocalDate curselecteddate = panelbkwkcjezhanbi.getCurSelectedDate();
			chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(curselecteddate));
			
			displayNodeInfo (selectstock.getStock());

			refreshCurrentStockTags (selectstock.getStock() );
			//�ڲ�ҵ������Ѱ�Ҹø���
			cyltreecopy.searchAndLocateNodeInTree (selectstock.getStock());
			
			String ShouwGeGuRemindInfo     = bkfxsettingprop.getProperty ("ShouwGeGuRemindInfo");
			if(ShouwGeGuRemindInfo    != null && ShouwGeGuRemindInfo   .toUpperCase().equals("TRUE") )
				showReminderMessage (bkfxremind.getStockremind());
			//���Բ���
			String readingsettinginprop  = bkfxsettingprop.getProperty ("readoutfxresultinvoice");
			if(readingsettinginprop != null && readingsettinginprop.toUpperCase().equals("TRUE") && readinfoout)
				readAnalysisResult ( selectstock.getBanKuai(),  selectstock.getStock(), this.dateChooser.getLocalDate() );
			
			hourglassCursor = null;
			Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
			setCursor(hourglassCursor2);
	}
	/*
	 * 
	 */
	private void readAnalysisResult(BanKuai banKuai, Stock stock, LocalDate analysisdate) 
	{
		String anaresult = WeeklyAnalysis.BanKuaiGeGuMatchConditionAnalysis(stock, analysisdate, this.bkggmatchcondition);
		
		if(analysisdate.equals(this.dateChooser.getLocalDate()) ) { //��ȥ����Ϣ�Ͳ�Ҫ�ڶ�������Щ��Ϣ�ˣ��Ѿ�������
			if(!Strings.isNullOrEmpty(stock.getNodeJiBenMian().getGainiantishi()  ) 
					|| !Strings.isNullOrEmpty(stock.getNodeJiBenMian().getFumianxiaoxi()  )  )
				anaresult = anaresult + "�и�����ʾ��";
			
			Collection<Tag> nodetags = stock.getNodeTags();
			if(nodetags != null && !nodetags.isEmpty() ) {
				anaresult = anaresult + "�ؼ�����";
				for (Iterator<Tag> lit = nodetags.iterator(); lit.hasNext(); ) {
			        Tag f = lit.next();
			        anaresult = anaresult + f.getName() ;
			    }
			}
		}
		
		if(!Strings.isNullOrEmpty(anaresult) ) {
			readengine = new VoiceEngine (anaresult);
//			readengin.readInformation (anaresult); //for testing
			readengine.execute();
			
//			if( readengine == null) {
//				readengine = new VoiceEngine (anaresult);
////				readengin.readInformation (anaresult); //for testing
//				readengine.execute();
//			} else {
//				readengine.cancel(true);
//				readengine = null;
//				readengine = new VoiceEngine (anaresult);
////				readengin.readInformation (anaresult); //for testing
//				readengine.execute();
//			}
		}
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
	 * 
	 */
	private void clearGeGuTablesFilter ()
	{
		tablebkggtableset.forEach(l -> l.resetTableHeaderFilter());
	}
	/*
	 * �ڸ��ɱ��з�������ĸ���,�������������Ƿ��ָĸ���
	 */
	protected Boolean findInputedNodeInTable(String nodecode) 
	{
		Boolean found = false; int rowindex;
		nodecode = nodecode.substring(0,6);
		
		for(BanKuaiGeGuBasicTable tmptable : tablebkggtableset ) {
			if(  ((BanKuaiGeGuBasicTableModel)tmptable.getModel() ).getRowCount() > 0 ) {
				rowindex = ((BanKuaiGeGuBasicTableModel)tmptable.getModel() ).getNodeRowIndex(nodecode);
				if(rowindex >= 0) {
					found = true;
					
					int modelRow = tmptable.convertRowIndexToView(rowindex);
					int curselectrow = tmptable.getSelectedRow();
					if(  curselectrow != modelRow) {
						try {
							tmptable.setRowSelectionInterval(modelRow, modelRow);
							tmptable.scrollRectToVisible(new Rectangle(tmptable.getCellRect(modelRow, 0, true)));
						} catch (java.lang.IllegalArgumentException ex) {}
					} 
				}
		}}

		return found;
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
					 this.dateChooser.setLocalDate(saturday);
					 
				} else if(dayofweek.equals(DayOfWeek.MONDAY) && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) <19 ) {
					LocalDate saturday = dateneedtobeadjusted.minus(2,ChronoUnit.DAYS);
					this.dateChooser.setLocalDate(saturday);
					 
				} else
					this.dateChooser.setDate(new Date ());
				btnresetdate.setEnabled(false);
				bkhlpnl.setCurrentDisplayDate(this.dateChooser.getLocalDate() ); //��������ʱ��Ҫ�õ���ǰ�����ڡ�
	}
	/**
	 * 
	 */
	private void clearTheGuiBeforDisplayNewInfoSection1 ()
	{
		tabbedPanebk.setSelectedIndex(0);
		((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).deleteAllRows();
		
		btnresetdate.setEnabled(true);
		
//		ckboxparsefile.setSelected(false);
//		tfldparsedfile.setText("");
		
		paneldayCandle.resetDate();
	}
	private void clearTheGuiBeforDisplayNewInfoSection2 ()
	{
		((BanKuaiInfoTableModel)tableselectedwkbkzb.getModel()).deleteAllRows();
		tabbedPanebk.setTitleAt(1, "ѡ����" );
		
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
		
		((BanKuaiGeGuBasicTableModel)tableGuGuZhanBiInBk.getModel()).deleteAllRows();
		((BanKuaiGeGuBasicTableModel)tableExternalInfo.getModel()).deleteAllRows();
		((BanKuaiGeGuBasicTableModel)tablexuandingzhou.getModel()).deleteAllRows();
		((BanKuaiGeGuBasicTableModel)tablexuandingminusone.getModel()).deleteAllRows();
		((BanKuaiGeGuBasicTableModel)tablexuandingminustwo.getModel()).deleteAllRows();
		((BanKuaiGeGuBasicTableModel)tablexuandingplusone.getModel()).deleteAllRows();
				
		menuItemchengjiaoer.setText("X ���ɽ�������");
    	menuItemliutong.setText("����ͨ��ֵ����");
    	menuItemtimerangezhangfu.setText("���׶��Ƿ�����");
    	
//		editorPanenodeinfo.setText("");
		
		pnlstocktags.removeAll();
	}
	/*
	 * ָ��tabbedPanegegu��title���������ּ����ط���һ��
	 */
	private String getTabbedPanegeguTabTiles (int tabindex)
	{
		String[] jtabbedpanetitle = { "��ǰ��", "������Ϣ","ѡ����","ѡ����-1","ѡ����+1","�ϴ�ѡ����"};
		return jtabbedpanetitle[tabindex];
	}
	private void clearTheGuiBeforDisplayNewInfoSection3 ()
	{
		tabbedPanegeguzhanbi.setSelectedIndex(0);
		
		panelGgDpCjeZhanBi.resetDate();
		
		panelggdpcjlwkzhanbi.resetDate();
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
		selectstock.getShuJuJiLuInfo().setHasReviewedToday(true);
		
		LocalDate curselectdate = dateChooser.getLocalDate();// dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		DaPan treeroot = (DaPan) treeofbkstk.getModel().getRoot();
		panelGgDpCjeZhanBi.setDisplayBarOfSpecificBanKuaiCjeCjlInsteadOfSelfCjeCjl (treeroot); 
		
		for (BarChartPanelDataChangedListener tmplistener : barchartpanelstockdatachangelisteners) {
			tmplistener.updatedDate(selectstock, CommonUtility.getSettingRangeDate(curselectdate, "basic"),curselectdate, globeperiod);
		}
	}
	/*
	 * ���͸��ɵ�K�ߣ�����ͬʱ��ʾ�����ԶԱ��о�
	 */
	private void refreshKXianOfTDXNodeWithSuperBanKuai (TDXNodes selectnode,TDXNodes superbankuai,String displayperiod)
	{
		LocalDate curselectdate = dateChooser.getLocalDate();
		if(this.globecalwholeweek)
			curselectdate = curselectdate.with(DayOfWeek.SATURDAY);
		
		TDXNodes tmpnode = null;
		if(selectnode.getType() == BkChanYeLianTreeNode.TDXGG) {
//			//����K�����ƣ�ĿǰK�����ƺͳɽ��������ߺ��������������Ƿֿ��ģ����Ե���ʱ��Ҫ�ر�С�ģ��Ժ��ϲ�
			tmpnode = selectnode;
		} else if(selectnode.getType() == BkChanYeLianTreeNode.TDXBK) {
			tmpnode = selectnode;
		} else if (selectnode.getType() == BkChanYeLianTreeNode.BKGEGU) {
			tmpnode = ((StockOfBanKuai)selectnode).getStock() ;
		} else if (selectnode.getType() == BkChanYeLianTreeNode.DZHBK) {
			tmpnode = selectnode;
		}
		
		 if(superbankuai != null  && superbankuai.getType() == BkChanYeLianTreeNode.TDXBK) {
			 if(treeofbkstk.getSpecificNodeByHypyOrCode(superbankuai.getMyOwnCode(), BkChanYeLianTreeNode.TDXBK ) != null) { //��ʱ����û�а��
				 SvsForNodeOfBanKuai svsbk = new SvsForNodeOfBanKuai ();
				 superbankuai = (TDXNodes) svsbk.getNodeData(superbankuai, CommonUtility.getSettingRangeDate(curselectdate, "large"),curselectdate, 
						 NodeGivenPeriodDataItem.WEEK,this.globecalwholeweek);
				 svsbk.syncNodeData(superbankuai);
				 
				 paneldayCandle.updatedDate(superbankuai,tmpnode,CommonUtility.getSettingRangeDate(curselectdate, "basic"),curselectdate,displayperiod);
			 } else //û��superbankuai,
				 paneldayCandle.updatedDate(tmpnode,CommonUtility.getSettingRangeDate(curselectdate, "basic"),curselectdate,displayperiod );
		 } else
			 paneldayCandle.updatedDate(tmpnode,CommonUtility.getSettingRangeDate(curselectdate, "basic"),curselectdate,displayperiod );

		
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
    				if(selbk != null)	 selbkcode = selbk.trim().substring(1, 7);
    				else 	return;
    				
    				int rowindex = 0;
    				try {
    					tableBkZhanBi.resetTableHeaderFilter ();
    					rowindex = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getNodeRowIndex(selbkcode);
    				} catch (java.lang.NullPointerException  e) { logger.info("����б���û���ҵ� " + selbkcode + "����ת������������޸ĸð�����á�");			return;}
    				
    				if(rowindex == -1) {
    					JOptionPane.showMessageDialog(null,"�ð������Ϊ����ʾ�ڰ���б��У����� ���ص��ע���ҵ���� ������ã�","Warning",JOptionPane.WARNING_MESSAGE);
    					return;
    				}

    					Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
    					setCursor(hourglassCursor);
    					
    					int modelRow = tableBkZhanBi.convertRowIndexToView(rowindex);
    					tableBkZhanBi.setRowSelectionInterval(modelRow, modelRow);
    					tableBkZhanBi.scrollRectToVisible(new Rectangle(tableBkZhanBi.getCellRect(modelRow, 0, true)));
    					
    					//�ҵ��û�ѡ��İ��
    					BkChanYeLianTreeNode selectedbk = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getNode(rowindex);
    					unifiedOperationsAfterUserSelectABanKuai ( (BanKuai)selectedbk );
    					
    					//�Ҳ����ù�Ʊ
    					String stockcode = combxstockcode.getSelectedItem().toString().substring(0, 6);
    					clearGeGuTablesFilter ();
    					if(!findInputedNodeInTable (stockcode) )
    						JOptionPane.showMessageDialog(null,"��ĳ�����ɱ���û�з��ָùɣ����������ʱ����ڸù�ͣ��","Warning",JOptionPane.WARNING_MESSAGE);
    					
    					hourglassCursor = null;
    					Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
    					setCursor(hourglassCursor2);
    					
    					SystemAudioPlayed.playSound();
            	}
            }
		});

		return;
	}
	/*
	 * ��ʾ�û����bar column��Ӧ����ʾ����Ϣ
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
		panelGgDpCjeZhanBi.setProperties(this.bkfxsettingprop);
		panelbkwkcjezhanbi.setProperties(this.bkfxsettingprop);
		panelggdpcjlwkzhanbi.setProperties(this.bkfxsettingprop);
		pnlbkwkcjlzhanbi.setProperties(this.bkfxsettingprop);
		
	
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
			}
		});
		
		pnlbktags.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {

                if (evt.getPropertyName().equals(LabelTag.PROPERTYCHANGEDATAGWASSELECTED )) {
                	Collection<Tag> newvalue = (Collection<Tag>) evt.getNewValue();
                	 ((BanKuaiGeGuBasicTableModel)tableGuGuZhanBiInBk.getModel() ).setCurrentHighlightKeyWords (newvalue);
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
				int action = JOptionPane.showConfirmDialog(null, "��Ҫ���¼������������Ƿ������","����", JOptionPane.YES_NO_OPTION);
				if(0 == action) {
					clearTheGuiBeforDisplayNewInfoSection1 ();
					clearTheGuiBeforDisplayNewInfoSection2 ();
					clearTheGuiBeforDisplayNewInfoSection3 ();
					
					if(chxbxwholeweek.isSelected())
						globecalwholeweek = true;
					else
						globecalwholeweek = false;
					
					gettBanKuaiZhanBiRangedByGrowthRateOfPeriod (globeperiod);
				} else {
					chxbxwholeweek.setSelected(!chxbxwholeweek.isSelected() );
				}
			}
		});
		
		menuItemQiangShigg.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				int row = tableGuGuZhanBiInBk.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"��ѡ��һ������","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				int modelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(row); 
				StockOfBanKuai stock = (StockOfBanKuai) ((BanKuaiGeGuBasicTableModel)tableGuGuZhanBiInBk.getModel()).getNode(modelRow);

        		QiangShi qs =  new 	QiangShi(stock.getStock(), "����", LocalDate.now(), LocalDate.now(), "��ϸ����", "ǿ�Ƹ���",new HashSet<>(),"URL");
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
					JOptionPane.showMessageDialog(null,"��ѡ��һ������","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				int modelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(row); 
				StockOfBanKuai stock = (StockOfBanKuai) ((BanKuaiGeGuBasicTableModel)tableGuGuZhanBiInBk.getModel()).getNode(modelRow);

        		QiangShi qs =  new 	QiangShi(stock.getStock(), "����", LocalDate.now(), LocalDate.now(), "��ϸ����", "ǿ�Ƹ���",new HashSet<>(),"URL");
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
					JOptionPane.showMessageDialog(null,"��ѡ��һ�����","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				int modelRow = tableBkZhanBi.convertRowIndexToModel(row); 
				BanKuai bankuai = (BanKuai) ((BanKuaiInfoTableModel) tableBkZhanBi.getModel()).getNode(modelRow);

				
				QiangShi qs =  new 	QiangShi(bankuai, "����", LocalDate.now(), LocalDate.now(), "��ϸ����", "ǿ�Ƹ���",new HashSet<>(),"URL");
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
					JOptionPane.showMessageDialog(null,"��ѡ��һ�����","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				int modelRow = tableBkZhanBi.convertRowIndexToModel(row); 
				BanKuai bankuai = (BanKuai) ((BanKuaiInfoTableModel) tableBkZhanBi.getModel()).getNode(modelRow);
				
				QiangShi qs =  new 	QiangShi(bankuai, "����", LocalDate.now(), LocalDate.now(), "��ϸ����", "ǿ�Ƹ���",new HashSet<>(),"URL");
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
        					JOptionPane.showMessageDialog(null,"��ѡ��һ����飡","Warning",JOptionPane.WARNING_MESSAGE);
        					return;
        				}
        				int modelRow = tableBkZhanBi.convertRowIndexToModel(row);
        				BanKuai selectedbk = (BanKuai) ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getNode(modelRow);
                    	
        				paneldayCandle.displayQueKou(true);
        				refreshKXianOfTDXNodeWithSuperBanKuai ( stockofbank, selectedbk, NodeGivenPeriodDataItem.DAY );
                		
                		
                    } else if(zhishuinfo.toLowerCase().equals("dapanzhishu") ) {
                    	String danpanzhishu = JOptionPane.showInputDialog(null,"��������ӵĴ���ָ��", "999999");
//                    	BanKuai zhishubk =  (BanKuai) allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode(danpanzhishu.toLowerCase(),BkChanYeLianTreeNode.TDXBK);
                    	SvsForNodeOfBanKuai svsbk = new SvsForNodeOfBanKuai ();
                    	BanKuai zhishubk = (BanKuai) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(danpanzhishu.toLowerCase(), BkChanYeLianTreeNode.TDXBK );
                    	svsbk = null;
                    	if(zhishubk == null)  {
        					JOptionPane.showMessageDialog(null,"ָ����������","Warning",JOptionPane.WARNING_MESSAGE);
        					return;
        				}

                    	if(stockofbank.getType() == BkChanYeLianTreeNode.TDXGG || stockofbank.getType() == BkChanYeLianTreeNode.BKGEGU )
                    		paneldayCandle.displayQueKou(true);
                    	else
                    		paneldayCandle.displayQueKou(false);
                    	refreshKXianOfTDXNodeWithSuperBanKuai ( stockofbank, zhishubk , NodeGivenPeriodDataItem.DAY);
                    	
                    } else if(zhishuinfo.toLowerCase().equals("zhishuguanjianriqi") ) {
//                		Collection<InsertedMeeting> zhishukeylists = newsdbopt.getZhiShuKeyDates(null, null);
//                		paneldayCandle.updateZhiShuKeyDates (zhishukeylists);
                    }

                } 
             }
        });

		menuItemDuanQiGuanZhu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
            	int row = tableBkZhanBi.getSelectedRow();
    			int modelRow = tableBkZhanBi.convertRowIndexToModel(row);
    			BanKuai bk = (BanKuai) ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getNode(modelRow);
    			
    			DuanQiGuanZhu dqgz = new DuanQiGuanZhu(bk, "����", dateChooser.getLocalDate(), dateChooser.getLocalDate(), "��ϸ����", "",  new HashSet<>(),"URL");
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
		
		menuItemAddRmvBkToYellow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
            	int row = tableBkZhanBi.getSelectedRow();
    			int modelRow = tableBkZhanBi.convertRowIndexToModel(row);
    			BanKuai bk = (BanKuai) ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getNode(modelRow);
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
    			BanKuai bk = (BanKuai) ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getNode(modelRow);
    			
    			List<TDXNodes> tmplist = new ArrayList<TDXNodes> ();
    			tmplist.add(bk);
    			exportTDXNodesDataToCsv (tmplist,"single");
    			tmplist = null;
            }
        });
		
		stkhistorycsvfileMenu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				int row = tableGuGuZhanBiInBk.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"��ѡ��һ�����","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				int modelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(row); 
				StockOfBanKuai bstkofbk = (StockOfBanKuai) ((BanKuaiGeGuBasicTableModel)tableGuGuZhanBiInBk.getModel()).getNode(modelRow);
				getBanKuaiHistoryCsvAndAnalysisFile ((JMenu)e.getSource(),bstkofbk.getStock());
			}
			
			public void mouseClicked(MouseEvent e) {}
		});
		
		historycsvfileMenu.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				int row = tableBkZhanBi.getSelectedRow();
				int modelRow = tableBkZhanBi.convertRowIndexToModel(row);
				BanKuai bk = (BanKuai) ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getNode(modelRow);
				getBanKuaiHistoryCsvAndAnalysisFile ((JMenu)e.getSource(),bk);
			}
			
			public void mouseClicked(MouseEvent e) {}
		});
		
		menuItemsMrjh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
            	int row = tableGuGuZhanBiInBk.getSelectedRow();
    			int modelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(row);
    			StockOfBanKuai selectstock = (StockOfBanKuai) ((BanKuaiGeGuBasicTableModel)tableGuGuZhanBiInBk.getModel()).getNode (modelRow);
    			
    			String mrjh = JOptionPane.showInputDialog(null,"���������ռƻ�����:","���ռƻ�", JOptionPane.QUESTION_MESSAGE);
    			if(mrjh.isEmpty()) 
    				return;
    			
    			PaoMaDengServices pmdsvs = new PaoMaDengServices ();
    			pmdsvs.setPaoMaDengInfo(selectstock.getStock(), mrjh);
            }
        });
		menuItemsiglestocktocsv.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
            	int row = tableGuGuZhanBiInBk.getSelectedRow();
    			int modelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(row);
    			StockOfBanKuai selectstock = (StockOfBanKuai) ((BanKuaiGeGuBasicTableModel)tableGuGuZhanBiInBk.getModel()).getNode (modelRow);
    			List<TDXNodes> tmplist = new ArrayList<TDXNodes> ();
    			tmplist.add(selectstock.getStock() );
    			exportTDXNodesDataToCsv (tmplist,"single");
    			tmplist = null;
            }
        });
		menuItemAddRmvStockToRed.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
            	int row = tableGuGuZhanBiInBk.getSelectedRow();
    			int modelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(row);
    			StockOfBanKuai selectstock = (StockOfBanKuai) ((BanKuaiGeGuBasicTableModel)tableGuGuZhanBiInBk.getModel()).getNode (modelRow);
    			Stock stock = selectstock.getStock();
    			NodesTreeRelated filetree = stock.getNodeTreeRelated ();
    			if(filetree.selfIsMatchModel(dateChooser.getLocalDate() ) ) 
    				filetree.setSelfIsMatchModel(dateChooser.getLocalDate(), false);
    			else
    				filetree.setSelfIsMatchModel(dateChooser.getLocalDate(), true);
    			
    			tableGuGuZhanBiInBk.repaint();
            }
        });
		menuItemAddRmvStockToYellow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
            	int row = tableGuGuZhanBiInBk.getSelectedRow();
    			int modelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(row);
    			StockOfBanKuai selectstock = (StockOfBanKuai) ((BanKuaiGeGuBasicTableModel)tableGuGuZhanBiInBk.getModel()).getNode (modelRow);
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
            	
            	((BanKuaiGeGuBasicTableModel)tableGuGuZhanBiInBk.getModel()).sortTableByLiuTongShiZhi();
            	((BanKuaiGeGuBasicTableModel)tableExternalInfo.getModel()).sortTableByLiuTongShiZhi();
            	((BanKuaiGeGuBasicTableModel)tablexuandingzhou.getModel()).sortTableByLiuTongShiZhi();
            	((BanKuaiGeGuBasicTableModel)tablexuandingminusone.getModel()).sortTableByLiuTongShiZhi();
            	((BanKuaiGeGuBasicTableModel)tablexuandingminustwo.getModel()).sortTableByLiuTongShiZhi();
            	((BanKuaiGeGuBasicTableModel)tablexuandingplusone.getModel()).sortTableByLiuTongShiZhi();
            }
        });
        menuItemchengjiaoer.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            	menuItemchengjiaoer.setText("X ���ɽ�������");
            	menuItemliutong.setText("����ͨ��ֵ����");
            	menuItemtimerangezhangfu.setText("���׶��Ƿ�����");
            	
            	((BanKuaiGeGuBasicTableModel)tableGuGuZhanBiInBk.getModel()).sortTableByChenJiaoEr();
            	((BanKuaiGeGuBasicTableModel)tableExternalInfo.getModel()).sortTableByChenJiaoEr();
        		((BanKuaiGeGuBasicTableModel)tablexuandingzhou.getModel()).sortTableByChenJiaoEr();
        		((BanKuaiGeGuBasicTableModel)tablexuandingminusone.getModel()).sortTableByChenJiaoEr();
        		((BanKuaiGeGuBasicTableModel)tablexuandingminustwo.getModel()).sortTableByChenJiaoEr();
        		((BanKuaiGeGuBasicTableModel)tablexuandingplusone.getModel()).sortTableByChenJiaoEr();
            }
        });
        menuItemcancelreviewedtoday.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
    			List<BkChanYeLianTreeNode> allgegu = ((BanKuaiGeGuTableModelFromPropertiesFile)tableGuGuZhanBiInBk.getModel()).getAllNodes();
    			if(allgegu == null  || allgegu.size() == 0)
    				return ;
    			
    			for(BkChanYeLianTreeNode tmpnode : allgegu) 
    				((StockOfBanKuai)tmpnode).getStock().getShuJuJiLuInfo().setHasReviewedToday(false);
    			
    			tablebkggtableset.forEach(l -> l.repaint());
            }
        });
        
        menuItemtimerangezhangfu.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            	menuItemchengjiaoer.setText("���ɽ�������");
            	menuItemliutong.setText("����ͨ��ֵ����");
            	
            	DateRangeSelectPnl datachoose = new DateRangeSelectPnl ( LocalDate.now().minusDays(1), LocalDate.now() ); 
        		JOptionPane.showMessageDialog(null, datachoose,"ѡ��ʱ���", JOptionPane.OK_CANCEL_OPTION);
        		
        		LocalDate searchstart = datachoose.getDatachoosestart();
        		LocalDate searchend = datachoose.getDatachooseend();
        		
        		menuItemtimerangezhangfu.setText("X ���׶��Ƿ�����" + searchstart.toString() + "~" + searchend.toString());
        		
        		BanKuai bk = null;
        		String selecttitle = tabbedPanegegu.getTitleAt( tabbedPanegegu.getSelectedIndex() );
        		if(selecttitle.contains("��ʱ")) {
        			bk = ((BanKuaiGeGuBasicTableModel)tableTempGeGu.getModel()).getCurDispalyBandKuai();
        		} else {
        			int row = tableBkZhanBi.getSelectedRow();
        			int modelRow = tableBkZhanBi.convertRowIndexToModel(row);
        			bk = (BanKuai) ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getNode(modelRow);
        		}
        		if(bk == null)
        			return;
        		
    			SvsForNodeOfBanKuai svsbk = new SvsForNodeOfBanKuai ();
    			//һ����ͬ����������Լ�������������
    			try {
    				svsbk.syncBanKuaiAndItsStocksForSpecificTime(bk, searchstart, searchend,globeperiod,globecalwholeweek);
    			} catch (SQLException ex) {ex.printStackTrace();}
    			
//    			NodeXPeriodData bknodexdata = bk.getNodeXPeroidData(NodeGivenPeriodDataItem.DAY);
//    			LocalDate curohlcstart = bknodexdata.getOHLCRecordsStartDate();
//    			LocalDate curohlcend = bknodexdata.getOHLCRecordsEndDate();
//    			if(searchstart.isBefore(curohlcstart) || searchend.isAfter(curohlcend) ) {
//    				JOptionPane.showMessageDialog(null,"ѡ��ʱ�������ȱʧ������ͬ�����ݻ��ߵ���ʱ��Ρ�","Warning",JOptionPane.WARNING_MESSAGE);
//    				menuItemtimerangezhangfu.setText("���׶��Ƿ�����");
//    				return ;
//    			}
    			if(selecttitle.contains("��ʱ")) {
    				((BanKuaiGeGuBasicTableModel)tableTempGeGu.getModel()).sortTableByTimeRangeZhangFu(searchstart,searchend,NodeGivenPeriodDataItem.DAY);
    			} else {
    				((BanKuaiGeGuBasicTableModel)tableGuGuZhanBiInBk.getModel()).sortTableByTimeRangeZhangFu(searchstart,searchend,NodeGivenPeriodDataItem.DAY);
                	((BanKuaiGeGuBasicTableModel)tableExternalInfo.getModel()).sortTableByTimeRangeZhangFu(searchstart,searchend,NodeGivenPeriodDataItem.DAY);
            		((BanKuaiGeGuBasicTableModel)tablexuandingzhou.getModel()).sortTableByTimeRangeZhangFu(searchstart,searchend,NodeGivenPeriodDataItem.DAY);
            		((BanKuaiGeGuBasicTableModel)tablexuandingminusone.getModel()).sortTableByTimeRangeZhangFu(searchstart,searchend,NodeGivenPeriodDataItem.DAY);
            		((BanKuaiGeGuBasicTableModel)tablexuandingminustwo.getModel()).sortTableByTimeRangeZhangFu(searchstart,searchend,NodeGivenPeriodDataItem.DAY);
            		((BanKuaiGeGuBasicTableModel)tablexuandingplusone.getModel()).sortTableByTimeRangeZhangFu(searchstart,searchend,NodeGivenPeriodDataItem.DAY);
    			}
            	
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
					int rowindex = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getNodeRowIndex( nodecode );
					if(rowindex == -1) {
						JOptionPane.showMessageDialog(null,"��鲻�ڷ������У��ɵ���������޸ģ�","Warning", JOptionPane.WARNING_MESSAGE);
						hourglassCursor = null;
	    				Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
	        			setCursor(hourglassCursor2);
						return;
					}
        			
					BanKuai bankuai = (BanKuai) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(node.getMyOwnCode(), BkChanYeLianTreeNode.TDXBK );
					
    				combxsearchbk.updateUserSelectedNode(bankuai);
    				
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
				BanKuai bkcur = (BanKuai) ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getNode(modelRow);

                if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.SELECTED_PROPERTY)) {
                    @SuppressWarnings("unchecked")
                    String selectedinfo = evt.getNewValue().toString();
                    refreshAfterUserSelectBanKuaiColumn (bkcur,selectedinfo);
                    
                } else if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.MOUSEDOUBLECLICK_PROPERTY)) {
                	String key = evt.getNewValue().toString();
                	List<String> tmpbkinfo = Splitter.on(",").omitEmptyStrings().splitToList(key); //���ɰ��|880232|3|1|0|32
                	try{
                		displayNodeLargerPeriodData (bkcur,LocalDate.parse(tmpbkinfo.get(1)),tmpbkinfo.get(0));
                	} catch (java.time.format.DateTimeParseException e) {
                		displayNodeLargerPeriodData (bkcur,null,tmpbkinfo.get(0));
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

//				int modelRow = tableBkZhanBi.convertRowIndexToModel(rowbk);
//				BanKuai bkcur = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(modelRow);
				TDXNodes bkcur = panelbkwkcjezhanbi.getCurDisplayedNode ();

                if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.SELECTED_PROPERTY)) {
                    @SuppressWarnings("unchecked")
                    String selectedinfo = evt.getNewValue().toString();
                    refreshAfterUserSelectBanKuaiColumn (bkcur,selectedinfo);
                    
                } else if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.MOUSEDOUBLECLICK_PROPERTY)) {
                	String key = evt.getNewValue().toString();
                	List<String> tmpbkinfo = Splitter.on(",").omitEmptyStrings().splitToList(key); //���ɰ��|880232|3|1|0|32
                	try{
                		displayNodeLargerPeriodData (bkcur,LocalDate.parse(tmpbkinfo.get(1)),tmpbkinfo.get(0));
                	} catch (java.time.format.DateTimeParseException e) {
                		displayNodeLargerPeriodData (bkcur,null,tmpbkinfo.get(0));
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
    				
//    				if(cbxshizhifx.isSelected()) { //��ʾ��ֵ����
//						dispalyStockShiZhiFengXiResult (selectstock,datekey);
//					};
					
//					SystemAudioPlayed.playSound();
                } else if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.MOUSEDOUBLECLICK_PROPERTY)) {
                	String key = evt.getNewValue().toString();
                	List<String> tmpbkinfo = Splitter.on(",").omitEmptyStrings().splitToList(key); //���ɰ��|880232|3|1|0|32
                	try{
                		displayNodeLargerPeriodData (selectstock,LocalDate.parse(tmpbkinfo.get(1)),tmpbkinfo.get(0));
                	} catch (java.time.format.DateTimeParseException e) {
                		displayNodeLargerPeriodData (selectstock,null,tmpbkinfo.get(0));
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
    				
    				String ShowSelectGeGuColumnRemindInfo    = bkfxsettingprop.getProperty ("ShowSelectGeGuColumnRemindInfo");
    				if(ShowSelectGeGuColumnRemindInfo   != null && ShowSelectGeGuColumnRemindInfo  .toUpperCase().equals("TRUE") )
    					showReminderMessage (bkfxremind.getStockcolumnremind() );
    				
    				Class<? extends Object> source = evt.getSource().getClass();
    				Boolean result = source.equals(BanKuaiFengXiCategoryBarChartCjePnl.class);
    				if(source.equals(BanKuaiFengXiCategoryBarChartCjePnl.class) ) {
    					String readingsettinginprop  = bkfxsettingprop.getProperty ("readoutfxresultinvoice");
    					if(readingsettinginprop != null  && readingsettinginprop.toUpperCase().equals("TRUE"))
    						readAnalysisResult ( null,  selectstock, datekey );
    				}
    				
//    				if(cbxshizhifx.isSelected()) { //��ʾ��ֵ����
//						dispalyStockShiZhiFengXiResult (selectstock,datekey);
//					};
					
                } else if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.MOUSEDOUBLECLICK_PROPERTY)) {
                	String key = evt.getNewValue().toString();
                	Class<? extends Object> source = evt.getSource().getClass();
                	List<String> tmpbkinfo = Splitter.on(",").omitEmptyStrings().splitToList(key); //���ɰ��|880232|3|1|0|32
                	try{
                		displayNodeLargerPeriodData (selectstock,LocalDate.parse(tmpbkinfo.get(1)),tmpbkinfo.get(0));
                	} catch (java.time.format.DateTimeParseException e) {
                		displayNodeLargerPeriodData (selectstock,null,tmpbkinfo.get(0));
                	}
                }
            }
        });
		/*
		 * ����pie chart�ڵ�����Ǹ����� 
		 */
		pnlcurwkggcjezhanbi.addPropertyChangeListener(new PropertyChangeListener() 
		{

	            public void propertyChange(PropertyChangeEvent evt) {

	                if (evt.getPropertyName().equals(BanKuaiFengXiPieChartPnl.SELECTED_PROPERTY)) {
	                	String curstockcode = evt.getNewValue().toString();
	                	clearGeGuTablesFilter ();
	                	findInputedNodeInTable (curstockcode);
	                	
	                	StockOfBanKuai sob = (StockOfBanKuai) ((BanKuaiGeGuBasicTableModel)tableGuGuZhanBiInBk.getModel() ).getNode(curstockcode.substring(0, 6));
	                	hightlightSpecificSector (sob);
	                }
	            }
		});
		panelLastWkGeGucjeZhanBi.addPropertyChangeListener(new PropertyChangeListener() 
		{

            public void propertyChange(PropertyChangeEvent evt) {

                if (evt.getPropertyName().equals(BanKuaiFengXiPieChartPnl.SELECTED_PROPERTY)) {
                	String curstockcode = evt.getNewValue().toString();
                	clearGeGuTablesFilter ();
                	findInputedNodeInTable (curstockcode);
                	
                	StockOfBanKuai sob = (StockOfBanKuai) ((BanKuaiGeGuBasicTableModel)tableGuGuZhanBiInBk.getModel() ).getNode(curstockcode.substring(0, 6));
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
							
						} catch (java.lang.NullPointerException e) {
							e.printStackTrace();
//							cbxstockcode.updateUserSelectedNode (selectstock.getStock());
						} catch (java.lang.StringIndexOutOfBoundsException e) {
							logger.debug((String)combxstockcode.getSelectedItem());
						}

					}

					if(findInputedNodeInTable (nodecode)) { 
						panelGgDpCjeZhanBi.resetDate();
						paneldayCandle.resetDate();
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
						
//						cyltreecopy.setCurrentDisplayedWk (newdate);
//			    		DefaultTreeModel treeModel = (DefaultTreeModel) cyltreecopy.getModel();
//			    		treeModel.reload();
			    		
			    		gettBanKuaiZhanBiRangedByGrowthRateOfPeriod (NodeGivenPeriodDataItem.WEEK);
			    		
			    		
						
			    		lastselecteddate = newdate;
			    		bkhlpnl.setCurrentDisplayDate(lastselecteddate );
			    		
			    		hourglassCursor = null;
			    		Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
						setCursor(hourglassCursor2);
		    		}
		    	}

		    }
		});

		
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
					JOptionPane.showMessageDialog(null,"��ѡ��һ����Ʊ��","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				int modelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(row);
				StockOfBanKuai selectstock = (StockOfBanKuai) ((BanKuaiGeGuBasicTableModel)tableGuGuZhanBiInBk.getModel()).getNode (modelRow);
				
				if (arg0.getClickCount() == 1) { //�û����һ��
					int col = tableGuGuZhanBiInBk.columnAtPoint(arg0.getPoint());
					if(col == 1)
						unifiedOperationsAfterUserSelectAStock (selectstock,true); //��������
					else
						unifiedOperationsAfterUserSelectAStock (selectstock,false);
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
					JOptionPane.showMessageDialog(null,"��ѡ��һ����Ʊ��","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
//				clearTheGuiBeforDisplayNewInfoSection1 ();
				clearTheGuiBeforDisplayNewInfoSection2 ();
				clearTheGuiBeforDisplayNewInfoSection3 ();
				
				int modelRow = tableTempGeGu.convertRowIndexToModel(row);
				StockOfBanKuai selectstock = (StockOfBanKuai) ((BanKuaiGeGuBasicTableModel)tableTempGeGu.getModel()).getNode (modelRow);
				
				if (arg0.getClickCount() == 1) { //�û����һ��
					int col = tableGuGuZhanBiInBk.columnAtPoint(arg0.getPoint());
					if(col == 1)
						unifiedOperationsAfterUserSelectAStock (selectstock,true);
					else
						unifiedOperationsAfterUserSelectAStock (selectstock,false);
				}
//				if (arg0.getClickCount() == 2) {
//				}
				
				tabbedPanegegu.setSelectedIndex(6);
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
				StockOfBanKuai selectstock = (StockOfBanKuai) ((BanKuaiGeGuBasicTableModel)tablexuandingzhou.getModel()).getNode (modelRow);
				
				if (arg0.getClickCount() == 1) {
					int col = tableGuGuZhanBiInBk.columnAtPoint(arg0.getPoint());
					if(col == 1)
						unifiedOperationsAfterUserSelectAStock (selectstock,true);
					else
						unifiedOperationsAfterUserSelectAStock (selectstock,false);
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
				StockOfBanKuai selectstock = (StockOfBanKuai) ((BanKuaiGeGuBasicTableModel)tablexuandingminustwo.getModel()).getNode (modelRow);
	
				if (arg0.getClickCount() == 1) {
					int col = tableGuGuZhanBiInBk.columnAtPoint(arg0.getPoint());
					if(col == 1)
						unifiedOperationsAfterUserSelectAStock (selectstock,true);
					else
						unifiedOperationsAfterUserSelectAStock (selectstock,false);
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
				StockOfBanKuai selectstock = (StockOfBanKuai) ((BanKuaiGeGuBasicTableModel)tablexuandingminusone.getModel()).getNode (modelRow);
	
				if (arg0.getClickCount() == 1) {
					int col = tableGuGuZhanBiInBk.columnAtPoint(arg0.getPoint());
					if(col == 1)
						unifiedOperationsAfterUserSelectAStock (selectstock,true);
					else
						unifiedOperationsAfterUserSelectAStock (selectstock,false);
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
				StockOfBanKuai selectstock = (StockOfBanKuai) ((BanKuaiGeGuBasicTableModel)tablexuandingplusone.getModel()).getNode (modelRow);
	
				if (arg0.getClickCount() == 1) {
					int col = tableGuGuZhanBiInBk.columnAtPoint(arg0.getPoint());
					if(col == 1)
						unifiedOperationsAfterUserSelectAStock (selectstock,true);
					else
						unifiedOperationsAfterUserSelectAStock (selectstock,false);
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
				StockOfBanKuai selectstock = (StockOfBanKuai) ((BanKuaiGeGuBasicTableModel)tableExternalInfo.getModel()).getNode (modelRow);
	
				if (arg0.getClickCount() == 1) {
					int col = tableGuGuZhanBiInBk.columnAtPoint(arg0.getPoint());
					if(col == 1)
						unifiedOperationsAfterUserSelectAStock (selectstock,true);
					else
						unifiedOperationsAfterUserSelectAStock (selectstock,false); 
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
				BanKuai selectedbk = (BanKuai) ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getNode(modelRow);
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
			if (tmpfile.isDirectory())
				continue;
			if(!tmpfile.isFile())
				continue;
		    
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
			JMenuItem menuitemhistorycsvfile = new JMenuItem ("û����ʷCSV�ļ�" );
			menuitemhistorycsvfile.setName("û����ʷCSV�ļ�");
			jMenu.add(menuitemhistorycsvfile);
		}
	
	}

	/*
	 * 
	 */
	private void parseTempGeGuFromZhiDingBanKuaiFunciotn ()
	{
		String result = JOptionPane.showInputDialog("�����������");
		if(result != null) {
			BanKuai bk = (BanKuai) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(result.trim(), BkChanYeLianTreeNode.TDXBK);
			if(bk == null)
				bk = (BanKuai) CreateExchangeTree.CreateTreeOfDZHBanKuaiAndStocks().getSpecificNodeByHypyOrCode(result.trim(), BkChanYeLianTreeNode.DZHBK);
			
			if(bk == null)
				return;
			
				ServicesForNodeBanKuai svsbk = bk.getBanKuaiService();
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
		
		int action = JOptionPane.showConfirmDialog(null, pnltdxzdy, "����ͨ���Ÿ���", JOptionPane.OK_CANCEL_OPTION);
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
		BanKuai tempbankuai = null;
		
		List<BkChanYeLianTreeNode> curstocksintable = ((BanKuaiGeGuBasicTableModel)tableTempGeGu.getModel()).getAllNodes();
		if( curstocksintable != null && curstocksintable.size() >0) {
			int extraresult = JOptionPane.showConfirmDialog(null,"��ʱ���ɱ�ǰ��Ϊ�գ���� �� ����ձ���� �� ���¸�����ӱ���." , "Warning!", JOptionPane.OK_CANCEL_OPTION);
			if(extraresult == JOptionPane.OK_OPTION) { //������������
				((BanKuaiGeGuBasicTableModel)tableTempGeGu.getModel()).deleteAllRows();
				curstocksintable = null;
				tempbankuai = new BanKuai ("TEMPBANKUAI","TEMPBANKUAI");
				tempbankuai.setBanKuaiLeiXing (BanKuai.HASGGWITHSELFCJL);
			} else
				tempbankuai = ((BanKuaiGeGuBasicTableModel)tableTempGeGu.getModel()).getCurDispalyBandKuai();
		} else {
			tempbankuai = new BanKuai ("TEMPBANKUAI","TEMPBANKUAI");
			tempbankuai.setBanKuaiLeiXing (BanKuai.HASGGWITHSELFCJL);
		}
		
		Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
		setCursor(hourglassCursor);
		
		LocalDate curselectdate = null;
		try{
			curselectdate = dateChooser.getLocalDate();// dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		} catch (java.lang.NullPointerException e) {
			JOptionPane.showMessageDialog(null,"��������!","Warning",JOptionPane.WARNING_MESSAGE);
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
				
				if(found)
					continue;
			}

			Stock tmpstock = (Stock) treeofbkstk.getSpecificNodeByHypyOrCode(tmpgegu, BkChanYeLianTreeNode.TDXGG);
			if(tmpstock == null)
				continue;
			try {
				tmpstock = (Stock) svstock.getNodeData( tmpstock , CommonUtility.getSettingRangeDate(curselectdate,"middle"), curselectdate,
						globeperiod,this.globecalwholeweek);
				svstock.syncNodeData(tmpstock);
			} catch (java.lang.NullPointerException e) {logger.info(tmpgegu + "��������");e.printStackTrace();}

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
		
		BanKuai curmainbk = ((BanKuaiGeGuBasicTableModel)tableGuGuZhanBiInBk.getModel()).getCurDispalyBandKuai(); //ͻ���͵�ǰ�����Ľ�������
		if(curmainbk != null) {
			((BanKuaiGeGuBasicTableModel)tableTempGeGu.getModel()).setInterSectionBanKuai(curmainbk); 
			((BanKuaiGeGuBasicTableModel)tableGuGuZhanBiInBk.getModel()).setInterSectionBanKuai(tempbankuai);
		}
		
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
		String parsedpath = (new SetupSystemConfiguration()).getTDXModelMatchExportFile ();
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
		try { //��������
			readparsefileLines = Files.readLines(fileebk,Charsets.UTF_8,new ParseBanKuaiWeeklyFielGetStocksProcessor ());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		parseTempGeGeFromList (readparsefileLines);
//		LocalDate curselectdate = null;
//		try{
//			curselectdate = dateChooser.getLocalDate();// dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//		} catch (java.lang.NullPointerException e) {
//			JOptionPane.showMessageDialog(null,"��������!","Warning",JOptionPane.WARNING_MESSAGE);
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
//				logger.info(tmpgegu + "��������");
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
		if(userinputnode == null ) { //����û����������ǰ�飬
			JOptionPane.showMessageDialog(null,"���ǰ����룬�������룡","Warning", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		String nodecode = userinputnode.getMyOwnCode();
		Integer rowindex = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getNodeRowIndex( nodecode );
		
		if(rowindex == null || rowindex == -1) {
			JOptionPane.showMessageDialog(null,"��Ʊ/���������󣡻��鲻�ڷ������У��ɵ���������޸ģ�","Warning", JOptionPane.WARNING_MESSAGE);
			return;
		}

		int curselectrow = tableBkZhanBi.getSelectedRow();
		int modelRow = tableBkZhanBi.convertRowIndexToView(rowindex);
		if(modelRow == curselectrow)
		    	return;		    
		tableBkZhanBi.setRowSelectionInterval(modelRow, modelRow);
		tableBkZhanBi.scrollRectToVisible(new Rectangle(tableBkZhanBi.getCellRect(modelRow, 0, true)));
		BkChanYeLianTreeNode selectedbk = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getNode(rowindex);
				
		unifiedOperationsAfterUserSelectABanKuai ( (BanKuai)selectedbk);
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
		StockOfBanKuai selectstock = (StockOfBanKuai) ((BanKuaiGeGuBasicTableModel)tableGuGuZhanBiInBk.getModel()).getNode (ggmodelRow);

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

			if(exporttypeordate.toLowerCase().equals("all")) { //list����ûһ����ǰ��������
				nodeinfotocsv.addNodeToCsvList(tmpnode, curselectdate, curselectdate);
			} else
			if(exporttypeordate.toLowerCase().equals("single") ) {
				String csvexportdefaulttimerangebymonthes = bkfxsettingprop.getProperty ("csvexportdefaulttimerangebymonthes");
				Integer timerange = 52;
				if(csvexportdefaulttimerangebymonthes != null) 
					timerange = Integer.parseInt(csvexportdefaulttimerangebymonthes);
				
				DateRangeSelectPnl datachoose = new DateRangeSelectPnl (timerange); 
				JOptionPane.showMessageDialog(null, datachoose,"Ϊ" + tmpnode.getMyOwnName() + "ѡ�񵼳�ʱ���", JOptionPane.OK_CANCEL_OPTION);
				LocalDate nodestart = datachoose.getDatachoosestart();
				LocalDate nodeend = datachoose.getDatachooseend();
				
				
				if(tmpnode.getType() == BkChanYeLianTreeNode.TDXBK) {
					SvsForNodeOfBanKuai svsbk = new SvsForNodeOfBanKuai ();
					tmpnode = (TDXNodes) svsbk.getNodeData(tmpnode, nodestart, nodeend, this.globeperiod, this.globecalwholeweek);
					svsbk.syncNodeData(tmpnode);
					svsbk = null;
				} else
				if(tmpnode.getType() == BkChanYeLianTreeNode.TDXGG) {
					SvsForNodeOfStock svsstk = new SvsForNodeOfStock	();
					tmpnode = (TDXNodes) svsstk.getNodeData(tmpnode, nodestart, nodeend, this.globeperiod, this.globecalwholeweek);
					svsstk.syncNodeData(tmpnode);
					svsstk = null;
				}
				DaPan dapan = (DaPan) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode("000000", BkChanYeLianTreeNode.DAPAN);
		    	dapan.getNodeData (nodestart, nodeend, this.globeperiod, this.globecalwholeweek);

		    	nodeinfotocsv.addNodeToCsvList(tmpnode, nodestart, nodeend);
				
				nodestart = null;
				nodeend = null;
				datachoose = null;
			} else { //�û��������Ϣ����
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
			extrainfotabpane.addTab(selectednode.getMyOwnName() + "������", displaybkjbmpnl);
		}
		
		DisplayNodesRelatedTagsServices nodetags =  new DisplayNodesRelatedTagsServices (selectednode);
		DisplayNodeInfoPanel displaybktagspnl = new DisplayNodeInfoPanel (nodetags);
		if(displaybktagspnl.isHtmlContainedUsefulInfo() ) {
			Dimension size4 = new Dimension(sclpinfosummary.getViewport().getSize().width,  displaybktagspnl.getContentHeight() + 10 );
			displaybktagspnl.setPreferredSize(size4);
			displaybktagspnl.setMinimumSize(size4);
			displaybktagspnl.setMaximumSize(size4);
			extrainfotabpane.addTab(selectednode.getMyOwnName() + "�ؼ���", displaybktagspnl);
		}
		
		if(selectednode.getType() == BkChanYeLianTreeNode.TDXBK) {
			DisplayNodesRelatedNewsServices bknews = new DisplayNodesRelatedNewsServices (selectednode);
			bknews.setTimeRangeForInfoRange(this.dateChooser.getLocalDate(), this.dateChooser.getLocalDate());
			DisplayNodeInfoPanel displaybknewspnl = new DisplayNodeInfoPanel (bknews);
			if(displaybknewspnl.isHtmlContainedUsefulInfo() ) {
				Dimension size2 = new Dimension(sclpinfosummary.getViewport().getSize().width, displaybknewspnl.getContentHeight() + 10 );
				displaybknewspnl.setPreferredSize(size2);
				displaybknewspnl.setMinimumSize(size2);
				displaybknewspnl.setMaximumSize(size2);
				
				extrainfotabpane.addTab(selectednode.getMyOwnName() + "����", displaybknewspnl);
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
				
				extrainfotabpane.addTab(selectednode.getMyOwnName() + "������ע��¼", displaybksbpnl);	
			}
			
			DisplayNodeGuDongInfoServices svsgd = new DisplayNodeGuDongInfoServices(selectednode);
			DisplayNodeInfoPanel gdpnl = new DisplayNodeInfoPanel (svsgd);
			if(gdpnl.isHtmlContainedUsefulInfo ()) {
				Dimension size3 = new Dimension(sclpinfosummary.getViewport().getSize().width, gdpnl.getContentHeight() + 10);
				gdpnl.setPreferredSize(size3);
				gdpnl.setMinimumSize(size3);
				gdpnl.setMaximumSize(size3);
				
				extrainfotabpane.addTab(selectednode.getMyOwnName() + "�ɶ���Ϣ", gdpnl);
			}
		}
		pnlextrainfo.add(extrainfotabpane);
		sclpinfosummary.getVerticalScrollBar().setValue(pnlextrainfo.getHeight() - extrainfotabpane.getHeight() );
		
		setUserSelectedColumnMessage( (TDXNodes)selectednode, this.dateChooser.getLocalDate());
	}
	/*
	 * �û�˫��ĳ��nodeռ��chart����Ŵ���ʾ��nodeһ���ڵ�ռ����������
	 */
	protected void displayNodeLargerPeriodData(TDXNodes node, LocalDate datekey, String guisource) 
	{
		if(!this.globecalwholeweek) {
			JOptionPane.showMessageDialog(null,"�������ܷ���ģʽ����֧�ָ���Χ��ʾ�����������!","Warning",JOptionPane.WARNING_MESSAGE);
			return;
		}
	
		//��֤��ʾʱ�䷶ΧΪ��ǰ����ǰ�������ݵ�48����(3��)
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
		if(numberOfMonth <48);
			overlapldstartday = overlapldstartday.with(DayOfWeek.MONDAY).minus( (48-numberOfMonth) ,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);

		//ͬ������
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
			//����Ǹ��ɵĻ�����Ҫ��ʾ�䵱ǰ�����İ��ռ����Ϣ������Ҫ�Ѱ�������Ҳ�ҳ�����
			int row = tableBkZhanBi.getSelectedRow();
			if(row != -1) {
				int modelRow = tableBkZhanBi.convertRowIndexToModel(row);
				bkcur = (BanKuai) ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getNode(modelRow);
				bkcur = (BanKuai) svsbk.getNodeData(bkcur, overlapldstartday, overlapldendday, globeperiod, globecalwholeweek);
				svsbk.syncNodeData(bkcur);
			}
		} else if(node.getType() == BkChanYeLianTreeNode.BKGEGU ) {
			BanKuai bk = ((StockOfBanKuai)node).getBanKuai();
			node = (TDXNodes) svsbk.getNodeData(node, overlapldstartday, overlapldendday, globeperiod, globecalwholeweek);
			svsbk.syncNodeData(node);
		} 
//		else if(node.getType() == BkChanYeLianTreeNode.DAPAN ) {
////			node = this.allbksks.getDaPan (requirestart.plusWeeks(1),globeperiod); //ͬ����������,�����������ط������
//		}
		
		DaPan dapan = (DaPan) CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode("000000", BkChanYeLianTreeNode.DAPAN);
    	dapan.getNodeData(overlapldstartday, overlapldendday, globeperiod, this.globecalwholeweek);
		
		
		BanKuaiFengXiLargePnl largeinfo = null;
		String guitype = "CJE";
		if( guisource.contains("CJL") )
			guitype = "CJL";
		else if( guisource.contains("CJE") )
			guitype = "CJE";
		
		if(node.getType() == BkChanYeLianTreeNode.DAPAN  ) {
			largeinfo = new BanKuaiFengXiLargePnl (node, node, overlapldstartday, overlapldendday, globeperiod,guitype,this.bkfxsettingprop);
		} else
		if(node.getType() == BkChanYeLianTreeNode.TDXBK  ) {
			DaPan treeroot = (DaPan) treeofbkstk.getModel().getRoot();
			largeinfo = new BanKuaiFengXiLargePnl (treeroot, node, overlapldstartday, overlapldendday, globeperiod,guitype,this.bkfxsettingprop);
		} else 
		if(node.getType() == BkChanYeLianTreeNode.TDXGG || node.getType() == BkChanYeLianTreeNode.BKGEGU) { 
			DaPan treeroot = (DaPan) treeofbkstk.getModel().getRoot();
			largeinfo = new BanKuaiFengXiLargePnl ( treeroot , node, overlapldstartday, overlapldendday, globeperiod,guitype,this.bkfxsettingprop);
		}
		
		if(datekey != null)
			largeinfo.highLightSpecificBarColumn(datekey);
		else
			largeinfo.highLightSpecificBarColumn(curselectdate);
		
		largeinfo.nodecombinedpnl.setProperties(this.bkfxsettingprop);
		JOptionPane.showMessageDialog(null, largeinfo, node.getMyOwnCode()+node.getMyOwnName()+ "�����ڷ������", JOptionPane.OK_CANCEL_OPTION);

		svsbk = null;
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
	 * �����
	 */
	private void initializePaoMaDeng() 
	{
		String displayinfo = "";
		String PaoMaDengDisplayMrjh   = bkfxsettingprop.getProperty ("PaoMaDengDisplayMrjh");
		if(PaoMaDengDisplayMrjh  != null && PaoMaDengDisplayMrjh .toUpperCase().equals("TRUE") ) {
			String title = "���ռƻ�:";
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
		
		if(!displayinfo.isEmpty())
			pnl_paomd.refreshMessage(displayinfo);
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

	private BanKuaiFengXiPieChartCjePnl pnlcurwkggcjezhanbi;
	private BanKuaiFengXiPieChartCjePnl panelLastWkGeGucjeZhanBi;
	
	private BanKuaiFengXiCandlestickPnl paneldayCandle;
	
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
	private JScrollPane sclpleft;
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
	private Action exportCancelAction;
	private Action bkfxCancelAction;
	private BanKuaiAndStockTree cyltreecopy;
	private JMenuItem menuItemRmvNodeFmFile;
	private JPopupMenu jPopupMenuoftabbedpane;
	private JMenuItem menuItemliutong ; //ϵͳĬ�ϰ���ͨ��ֵ����
	private JMenuItem menuItemzongshizhi ;
	private JMenuItem menuItemchengjiaoer ;
//	private JMenuItem menuItemstocktocsv ;
//	private JPopupMenu jPopupMenuoftabbedpanebk;
//	private JMenuItem menuItembktocsv ; //ϵͳĬ�ϰ���ͨ��ֵ����
	private JMenuItem menuItemsiglestocktocsv;
//	private JProgressBar progressBarExport;
	private JMenuItem menuItemsiglebktocsv;
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
//	private JMenu menuItemhistorycsvfile;
	private JMenu historycsvfileMenu;
	private BkfxHightLightPnl bkhlpnl;
	private JMenu stkhistorycsvfileMenu;
	private JMenuItem menuItemcancelreviewedtoday;
	
	
	private void initializeGuiOfNormal() {
		setTitle("\u677F\u5757\u5206\u6790");
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Double width = screenSize.getWidth();
		Double height = screenSize.getHeight();
		setBounds(10, 10,  width.intValue(), height.intValue());
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
		tabbedPane.setUI(new com.exchangeinfomanager.commonlib.MetalBorderlessTabbedPaneUI());
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
		tabbedPanegeguzhanbi.setUI(new com.exchangeinfomanager.commonlib.MetalBorderlessTabbedPaneUI());
		
		this.panelGgDpCjeZhanBi = new BanKuaiFengXiNodeCombinedCategoryPnl("CJE");
		this.panelGgDpCjeZhanBi.setAllowDrawAnnoation(false);
		this.panelGgDpCjeZhanBi.setDrawQueKouLine(false);
		this.panelGgDpCjeZhanBi.setDrawZhangDieTingLine(false);
		this.panelGgDpCjeZhanBi.setDrawAverageDailyCjeOfWeekLine(true);
		this.panelGgDpCjeZhanBi.setDisplayZhanBiInLine(true);
		tabbedPanegeguzhanbi.addTab("\u4E2A\u80A1\u989D\u5360\u6BD4", null, panelGgDpCjeZhanBi, null);
		
		
		panelggdpcjlwkzhanbi = new BanKuaiFengXiNodeCombinedCategoryPnl("CJL");
		this.panelggdpcjlwkzhanbi.setDisplayZhanBiInLine(true);
		
		tabbedPanegeguzhanbi.addTab("\u4E2A\u80A1\u91CF\u5360\u6BD4", null, panelggdpcjlwkzhanbi, null);
		
		tabbedPanebkzb = new JTabbedPane(JTabbedPane.TOP);
		tabbedPanebkzb.setUI(new com.exchangeinfomanager.commonlib.MetalBorderlessTabbedPaneUI());
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
		panelbkwkcjezhanbi.setAllowDrawAnnoation(false);
		this.panelbkwkcjezhanbi.setDisplayZhanBiInLine(true);
		tabbedPanebkzb.addTab("\u677F\u5757\u989D\u5360\u6BD4", null, panelbkwkcjezhanbi, null);

		pnlbkwkcjlzhanbi = new BanKuaiFengXiNodeCombinedCategoryPnl("CJL");
		this.pnlbkwkcjlzhanbi.setDisplayZhanBiInLine(true);
		tabbedPanebkzb.addTab("\u677F\u5757\u91CF\u5360\u6BD4", null, pnlbkwkcjlzhanbi, null);
		panel_2.setLayout(gl_panel_2);
		
		tabbedPanegegu = new JTabbedPane(JTabbedPane.TOP);
		tabbedPanegegu.setUI(new com.exchangeinfomanager.commonlib.MetalBorderlessTabbedPaneUI());
		
		tabbedPanebk = new JTabbedPane(JTabbedPane.TOP);
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
		

		String bkzbtablepropFileName =   (new SetupSystemConfiguration()).getSystemInstalledPath() + "/config/" +  bkfxsettingprop.getProperty ("BkfxBanKuaiTableInfoSettingFile")  + "/";
		tableBkZhanBi = new BanKuaiInfoTable(this.stockmanager,bkzbtablepropFileName);	
		
		sclpleft.setViewportView(tableBkZhanBi);
		
		scrollPane_1 = new JScrollPane();
		tabbedPanebk.addTab("\u9009\u5B9A\u5468", null, scrollPane_1, null);
		
		tableselectedwkbkzb = new BanKuaiInfoTable(this.stockmanager,bkzbtablepropFileName);
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
		
		
		String ggzbtablepropFileName =   (new SetupSystemConfiguration()).getSystemInstalledPath() + "/config/" +  bkfxsettingprop.getProperty ("BkfxGeGuTableInfoSettingFile")  + "/";
		String ggexternaltablepropFileName =   (new SetupSystemConfiguration()).getSystemInstalledPath() + "/config/" +  bkfxsettingprop.getProperty ("BkfxGeGuExternalTableInfoSettingFile")  + "/";
		JScrollPane scrollPanedangqian = new JScrollPane();
		tabbedPanegegu.addTab("��ǰ��", null, scrollPanedangqian, null);
		tabbedPanegegu.setBackgroundAt(0, Color.ORANGE);
		tableGuGuZhanBiInBk = new BanKuaiGeGuTableFromPropertiesFile (this.stockmanager,ggzbtablepropFileName);
//		tableGuGuZhanBiInBk.hideZhanBiColumn(1);
//		tableGuGuZhanBiInBk.sortByZhanBiGrowthRate();
		scrollPanedangqian.setViewportView(tableGuGuZhanBiInBk);
		
//		JScrollBar scrollbarGuGuZhanBiInBk = new JScrollBar(JScrollBar.VERTICAL);
//		scrollbarGuGuZhanBiInBk.setUnitIncrement(10);
//	    scrollbarGuGuZhanBiInBk.setUI(new MetalScrollBarUI() { //https://stackoverflow.com/questions/14176848/java-coloured-scroll-bar-search-result
//	    	//https://java-swing-tips.blogspot.com/search/label/Matcher?m=0
//	      @Override protected void paintTrack(
//	            Graphics g, JComponent c, Rectangle trackBounds) {
//	        super.paintTrack(g, c, trackBounds);
////	        Rectangle rect = tabbedPanegegu.getBounds();
////	        double sy = trackBounds.getHeight() / rect.getHeight();
////	        AffineTransform at = AffineTransform.getScaleInstance(1.0, sy);
//	        g.setColor(Color.BLUE.darker());
//	        
//	        BanKuai interbk = ((BanKuaiGeGuBasicTableModel)tableGuGuZhanBiInBk.getModel()).getInterSetctionBanKuai();
//		    if(interbk!= null) {
//		    	List<BkChanYeLianTreeNode> allstks = ((BanKuaiGeGuBasicTableModel)tableGuGuZhanBiInBk.getModel() ).getAllStocks();
//		    	int rownum = allstks.size();
//		    	for(BkChanYeLianTreeNode tmpnode : allstks) {
//		    		if( interbk.getBanKuaiGeGu (tmpnode.getMyOwnCode())  != null) {
//		    			Integer rowindex = ((BanKuaiGeGuBasicTableModel)tableGuGuZhanBiInBk.getModel() ).getStockRowIndex(tmpnode.getMyOwnCode() );
//						if( rowindex  != null && rowindex >=0 ) {
//							int modelRow = tableGuGuZhanBiInBk.convertRowIndexToView(rowindex);
////							int width = tableTempGeGu.getCellRect(modelRow, 0, true).width;
////							int height = tableTempGeGu.getCellRect(modelRow, 0, true).height;
////						    Rectangle r = new Rectangle(width, height * modelRow);
////						    Rectangle s = at.createTransformedShape(r).getBounds();
//						    int h = 2; //Math.max(2, s.height-2);
//						    int zuobiaoy = (trackBounds.height* modelRow)/rownum ;
//					        g.fillRect(trackBounds.x+2, zuobiaoy , trackBounds.width, h);
//						}
//		    		}
//		    	}
//		    }
//	      }
//	    });
//	    scrollPanedangqian.setVerticalScrollBar(scrollbarGuGuZhanBiInBk);
		
		JScrollPane scrollPanGeGuExtralInfo = new JScrollPane();
		tabbedPanegegu.addTab("\u5176\u4ED6\u4FE1\u606F", null, scrollPanGeGuExtralInfo, null);
		tableExternalInfo = new BanKuaiGeGuExternalInfoTableFromPropertiesFile (this.stockmanager,ggexternaltablepropFileName);
		scrollPanGeGuExtralInfo.setViewportView(tableExternalInfo);
		
		JScrollPane scrollPanexuanding = new JScrollPane();
		tabbedPanegegu.addTab("ѡ����", null, scrollPanexuanding, null);
		tabbedPanegegu.setBackgroundAt(2, UIManager.getColor("MenuItem.selectionBackground"));
		tablexuandingzhou = new BanKuaiGeGuTableFromPropertiesFile (this.stockmanager,ggzbtablepropFileName);
		scrollPanexuanding.setViewportView(tablexuandingzhou);
		
		JScrollPane scrollPanexuandingminusone = new JScrollPane();
		tabbedPanegegu.addTab("\u9009\u5B9A\u5468-1", null, scrollPanexuandingminusone, null);
//		tabbedPanegegu.setBackgroundAt(3, Color.LIGHT_GRAY);
		tablexuandingminusone = new BanKuaiGeGuTableFromPropertiesFile (this.stockmanager,ggzbtablepropFileName);
		scrollPanexuandingminusone.setViewportView(tablexuandingminusone);
		
		JScrollPane scrollPanexuandingminustwo = new JScrollPane();
		tabbedPanegegu.addTab("\u9009\u5B9A\u5468-2", null, scrollPanexuandingminustwo, null);
		tablexuandingminustwo = new BanKuaiGeGuTableFromPropertiesFile (this.stockmanager,ggzbtablepropFileName);
		scrollPanexuandingminustwo.setViewportView(tablexuandingminustwo);
		
		JScrollPane scrollPanexuandingplusone = new JScrollPane();
		tabbedPanegegu.addTab("\u9009\u5B9A\u5468+1", null, scrollPanexuandingplusone, null);
		
		tablexuandingplusone = new BanKuaiGeGuTableFromPropertiesFile (this.stockmanager,ggzbtablepropFileName);
		scrollPanexuandingplusone.setViewportView(tablexuandingplusone);
		
		JScrollPane scrollPaneTempGeGu = new JScrollPane();
		tableTempGeGu = new BanKuaiGeGuTableFromPropertiesFile (this.stockmanager,ggzbtablepropFileName);
		scrollPaneTempGeGu.setViewportView(tableTempGeGu);
		
//		JScrollBar scrollbarTempGeGu = new JScrollBar(JScrollBar.VERTICAL);
//		scrollbarTempGeGu.setUnitIncrement(10);
//	    scrollbarTempGeGu.setUI(new MetalScrollBarUI() { //https://stackoverflow.com/questions/14176848/java-coloured-scroll-bar-search-result
//	    	//https://java-swing-tips.blogspot.com/search/label/Matcher?m=0
//	      @Override protected void paintTrack(
//	            Graphics g, JComponent c, Rectangle trackBounds) {
//	        super.paintTrack(g, c, trackBounds);
////	        Rectangle rect = tabbedPanegegu.getBounds();
////	        double sy = trackBounds.getHeight() / rect.getHeight();
////	        AffineTransform at = AffineTransform.getScaleInstance(1.0, sy);
//	        g.setColor(Color.BLUE.darker());
//	        
//	        BanKuaiGeGuTableModelFromPropertiesFile tableTempGeGuModel = (BanKuaiGeGuTableModelFromPropertiesFile)tableTempGeGu.getModel(); 
//	        BanKuai interbk = tableTempGeGuModel.getInterSetctionBanKuai();
//		    if(interbk!= null) {
//		    	List<BkChanYeLianTreeNode> allstks = tableTempGeGuModel.getAllStocks();
//		    	int rownum = allstks.size();
//		    	for(BkChanYeLianTreeNode tmpnode : allstks) {
//		    		if( interbk.getBanKuaiGeGu (tmpnode.getMyOwnCode())  != null) {
//		    			Integer rowindex = tableTempGeGuModel.getStockRowIndex(tmpnode.getMyOwnCode() );
//						if( rowindex  != null && rowindex >=0 ) {
//							int modelRow = tableTempGeGu.convertRowIndexToView(rowindex);
////							int width = tableTempGeGu.getCellRect(modelRow, 0, true).width;
////							int height = tableTempGeGu.getCellRect(modelRow, 0, true).height;
////						    Rectangle r = new Rectangle(width, height * modelRow);
////						    Rectangle s = at.createTransformedShape(r).getBounds();
//						    int h = 2; //Math.max(2, s.height-2);
//						    int zuobiaoy = (trackBounds.height* modelRow)/rownum ;
//					        g.fillRect(trackBounds.x+2, zuobiaoy , trackBounds.width, h);
//						}
//		    		}
//		    	}
//		    }
//	      }
//	    });
//	    scrollPaneTempGeGu.setVerticalScrollBar(scrollbarTempGeGu);
		tabbedPanegegu.addTab("\u4E34\u65F6\u4E2A\u80A1", null, scrollPaneTempGeGu, null);
		tabbedPanegegu.setBackgroundAt(6, Color.CYAN);
		
		
		
		panel_1.setLayout(gl_panel_1);
		
//		dateChooser = new JDateChooser();
		dateChooser = new JStockCalendarDateChooser(new StockCalendar());
		dateChooser.setDateFormatString("yyyy-MM-dd");
		dateChooser.setDate(new Date() );
		
		bkfxCancelAction = new AbstractAction("����") {

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
//		        } else { //������������ʱ������û�����ʱ�䣬��ȡ��������������ʵ�о�Ҳû����Ҫ
//		        	bkfxtask.cancel(true);
//		        }
		      }
		    		
		 };
		
		btnresetdate = new JButton(bkfxCancelAction);
		btnresetdate.setText("\u4ECA\u5929");
		
		chxbxwholeweek = new JCheckBox("\u8BA1\u7B97\u5B8C\u6574\u5468");
		chxbxwholeweek.setToolTipText("\u5982\u4E0D\u52FE\u9009\uFF0C\u5219\u8BA1\u7B97\u5230\u88AB\u9009\u62E9\u7684\u90A3\u4E00\u5929");
		chxbxwholeweek.setSelected(true);
		
		pnlZhiShu = new JPanel();
		
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addContainerGap()
							.addComponent(dateChooser, GroupLayout.PREFERRED_SIZE, 133, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(chxbxwholeweek)
							.addGap(18)
							.addComponent(btnresetdate))
						.addComponent(pnlZhiShu, GroupLayout.PREFERRED_SIZE, 370, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
					.addComponent(pnlZhiShu, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE))
		);
		
		panel.setLayout(gl_panel);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			
		}
		
		reFormatGui ();
	}
	
	private void initializeGuiOf2560Resolution ()
	{

		setTitle("\u677F\u5757\u5206\u6790");
		setBounds(10, 10, 2520, 1033);
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
		tabbedPane.setUI(new com.exchangeinfomanager.commonlib.MetalBorderlessTabbedPaneUI());
		scrldailydata = new JScrollPane();
		
		editorPanebankuai = new JScrollPane();
		
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING, false)
						.addComponent(panel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(panel_1, GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
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
									.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 380, GroupLayout.PREFERRED_SIZE))))
						.addComponent(paneldayCandle, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 1977, GroupLayout.PREFERRED_SIZE))
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
							.addComponent(paneldayCandle, GroupLayout.PREFERRED_SIZE, 327, GroupLayout.PREFERRED_SIZE)
							.addGap(5)
							.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 274, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(scrldailydata, GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)))
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
		tabbedPanegeguzhanbi.setUI(new com.exchangeinfomanager.commonlib.MetalBorderlessTabbedPaneUI());
		this.panelGgDpCjeZhanBi = new BanKuaiFengXiNodeCombinedCategoryPnl("CJE");
		this.panelGgDpCjeZhanBi.setAllowDrawAnnoation(false);
		this.panelGgDpCjeZhanBi.setDrawQueKouLine(false);
		this.panelGgDpCjeZhanBi.setDrawZhangDieTingLine(false);
		this.panelGgDpCjeZhanBi.setDrawAverageDailyCjeOfWeekLine(true);
		this.panelGgDpCjeZhanBi.setDisplayZhanBiInLine(true);
		tabbedPanegeguzhanbi.addTab("\u4E2A\u80A1\u989D\u5360\u6BD4", null, panelGgDpCjeZhanBi, null);
		
		
		panelggdpcjlwkzhanbi = new BanKuaiFengXiNodeCombinedCategoryPnl("CJL");
		this.panelggdpcjlwkzhanbi.setDisplayZhanBiInLine(true);
		tabbedPanegeguzhanbi.addTab("\u4E2A\u80A1\u91CF\u5360\u6BD4", null, panelggdpcjlwkzhanbi, null);
		
		tabbedPanebkzb = new JTabbedPane(JTabbedPane.TOP);
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
		panelbkwkcjezhanbi.setAllowDrawAnnoation(false);
		this.panelbkwkcjezhanbi.setDisplayZhanBiInLine(true);
		tabbedPanebkzb.addTab("\u677F\u5757\u989D\u5360\u6BD4", null, panelbkwkcjezhanbi, null);
//		panelbkwkcjezhanbi.setBorder(new TitledBorder(null, "\u677F\u5757\u6210\u4EA4\u989D\u5360\u6BD4", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		
		pnlbkwkcjlzhanbi = new BanKuaiFengXiNodeCombinedCategoryPnl("CJL");
		this.pnlbkwkcjlzhanbi.setDisplayZhanBiInLine(true);
		tabbedPanebkzb.addTab("\u677F\u5757\u91CF\u5360\u6BD4", null, pnlbkwkcjlzhanbi, null);
		panel_2.setLayout(gl_panel_2);
		
		tabbedPanegegu = new JTabbedPane(JTabbedPane.TOP);
		tabbedPanegegu.setUI(new com.exchangeinfomanager.commonlib.MetalBorderlessTabbedPaneUI());
		tabbedPanebk = new JTabbedPane(JTabbedPane.TOP);
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
		
		sclpleft = new JScrollPane();
		tabbedPanebk.addTab("\u5F53\u524D\u5468", null, sclpleft, null);
		tabbedPanebk.setBackgroundAt(0, Color.ORANGE);
		
		String bkzbtablepropFileName =   (new SetupSystemConfiguration()).getSystemInstalledPath() + "/config/" +  bkfxsettingprop.getProperty ("BkfxBanKuaiTableInfoSettingFile")  + "/";
		tableBkZhanBi = new BanKuaiInfoTable(this.stockmanager,bkzbtablepropFileName);	
		
		sclpleft.setViewportView(tableBkZhanBi);
		
		scrollPane_1 = new JScrollPane();
		tabbedPanebk.addTab("\u9009\u5B9A\u5468", null, scrollPane_1, null);
		
		tableselectedwkbkzb = new BanKuaiInfoTable(this.stockmanager,bkzbtablepropFileName);
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
		
		
		String ggzbtablepropFileName =   (new SetupSystemConfiguration()).getSystemInstalledPath() + "/config/" +  bkfxsettingprop.getProperty ("BkfxGeGuTableInfoSettingFile")  + "/";
		String ggexternaltablepropFileName =   (new SetupSystemConfiguration()).getSystemInstalledPath() + "/config/" +  bkfxsettingprop.getProperty ("BkfxGeGuExternalTableInfoSettingFile")  + "/";
		JScrollPane scrollPanedangqian = new JScrollPane();
		tabbedPanegegu.addTab("��ǰ��", null, scrollPanedangqian, null);
		tabbedPanegegu.setBackgroundAt(0, Color.ORANGE);
		tableGuGuZhanBiInBk = new BanKuaiGeGuTableFromPropertiesFile (this.stockmanager,ggzbtablepropFileName);
//		tableGuGuZhanBiInBk.hideZhanBiColumn(1);
//		tableGuGuZhanBiInBk.sortByZhanBiGrowthRate();
		scrollPanedangqian.setViewportView(tableGuGuZhanBiInBk);
		
		JScrollBar scrollbarGuGuZhanBiInBk = new JScrollBar(JScrollBar.VERTICAL); //���Ҫʹ��windowbuilder�޸Ľ��棬������Ҫ�޸�
		scrollbarGuGuZhanBiInBk.setUnitIncrement(10);
	    scrollbarGuGuZhanBiInBk.setUI(new MetalScrollBarUI() { //https://stackoverflow.com/questions/14176848/java-coloured-scroll-bar-search-result
	    	//https://java-swing-tips.blogspot.com/search/label/Matcher?m=0
	      @Override protected void paintTrack(
	            Graphics g, JComponent c, Rectangle trackBounds) {
	        super.paintTrack(g, c, trackBounds);
//	        Rectangle rect = tabbedPanegegu.getBounds();
//	        double sy = trackBounds.getHeight() / rect.getHeight();
//	        AffineTransform at = AffineTransform.getScaleInstance(1.0, sy);
	        g.setColor(Color.BLUE.darker());
	        
	        BanKuai interbk = ((BanKuaiGeGuBasicTableModel)tableGuGuZhanBiInBk.getModel()).getInterSetctionBanKuai();
		    if(interbk!= null) {
		    	List<BkChanYeLianTreeNode> allstks = ((BanKuaiGeGuBasicTableModel)tableGuGuZhanBiInBk.getModel() ).getAllNodes();
		    	int rownum = allstks.size();
		    	for(BkChanYeLianTreeNode tmpnode : allstks) {
		    		if( interbk.getBanKuaiGeGu (tmpnode.getMyOwnCode())  != null) {
		    			Integer rowindex = ((BanKuaiGeGuBasicTableModel)tableGuGuZhanBiInBk.getModel() ).getNodeRowIndex(tmpnode.getMyOwnCode() );
						if( rowindex  != null && rowindex >=0 ) {
							int modelRow = tableGuGuZhanBiInBk.convertRowIndexToView(rowindex);
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
	    scrollPanedangqian.setVerticalScrollBar(scrollbarGuGuZhanBiInBk);
		
		JScrollPane scrollPanGeGuExtralInfo = new JScrollPane();
		tabbedPanegegu.addTab("\u5176\u4ED6\u4FE1\u606F", null, scrollPanGeGuExtralInfo, null);
		tableExternalInfo = new BanKuaiGeGuExternalInfoTableFromPropertiesFile (this.stockmanager,ggexternaltablepropFileName);
		scrollPanGeGuExtralInfo.setViewportView(tableExternalInfo);
		
		JScrollPane scrollPanexuanding = new JScrollPane();
		tabbedPanegegu.addTab("ѡ����", null, scrollPanexuanding, null);
//		tabbedPanegegu.setBackgroundAt(10, UIManager.getColor("MenuItem.selectionBackground"));
		tablexuandingzhou = new BanKuaiGeGuTableFromPropertiesFile (this.stockmanager,ggzbtablepropFileName);
		scrollPanexuanding.setViewportView(tablexuandingzhou);
		
		JScrollPane scrollPanexuandingminusone = new JScrollPane();
		tabbedPanegegu.addTab("\u9009\u5B9A\u5468-1", null, scrollPanexuandingminusone, null);
//		tabbedPanegegu.setBackgroundAt(3, Color.LIGHT_GRAY);
		tablexuandingminusone = new BanKuaiGeGuTableFromPropertiesFile (this.stockmanager,ggzbtablepropFileName);
		scrollPanexuandingminusone.setViewportView(tablexuandingminusone);
		
		JScrollPane scrollPanexuandingminustwo = new JScrollPane();
		tabbedPanegegu.addTab("\u9009\u5B9A\u5468-2", null, scrollPanexuandingminustwo, null);
		tablexuandingminustwo = new BanKuaiGeGuTableFromPropertiesFile (this.stockmanager,ggzbtablepropFileName);
		scrollPanexuandingminustwo.setViewportView(tablexuandingminustwo);
		
		JScrollPane scrollPanexuandingplusone = new JScrollPane();
		tabbedPanegegu.addTab("\u9009\u5B9A\u5468+1", null, scrollPanexuandingplusone, null);
		
		tablexuandingplusone = new BanKuaiGeGuTableFromPropertiesFile (this.stockmanager,ggzbtablepropFileName);
		scrollPanexuandingplusone.setViewportView(tablexuandingplusone);
		
		JScrollPane scrollPaneTempGeGu = new JScrollPane();
		tableTempGeGu = new BanKuaiGeGuTableFromPropertiesFile (this.stockmanager,ggzbtablepropFileName);
		scrollPaneTempGeGu.setViewportView(tableTempGeGu);
		
		JScrollBar scrollbarTempGeGu = new JScrollBar(JScrollBar.VERTICAL); //���Ҫʹ��windowbuilder�޸Ľ��棬������Ҫ�޸�
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
	        
	        BanKuaiGeGuTableModelFromPropertiesFile tableTempGeGuModel = ((BanKuaiGeGuTableModelFromPropertiesFile)tableTempGeGu.getModel()); 
	        BanKuai interbk = tableTempGeGuModel.getInterSetctionBanKuai();
		    if(interbk!= null) {
		    	List<BkChanYeLianTreeNode> allstks = tableTempGeGuModel.getAllNodes();
		    	int rownum = allstks.size();
		    	for(BkChanYeLianTreeNode tmpnode : allstks) {
		    		if( interbk.getBanKuaiGeGu (tmpnode.getMyOwnCode())  != null) {
		    			Integer rowindex = tableTempGeGuModel.getNodeRowIndex(tmpnode.getMyOwnCode() );
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
		
		bkfxCancelAction = new AbstractAction("����") {

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
//		        } else { //������������ʱ������û�����ʱ�䣬��ȡ��������������ʵ�о�Ҳû����Ҫ
//		        	bkfxtask.cancel(true);
//		        }
		      }
		    		
		 };
		
		btnresetdate = new JButton(bkfxCancelAction);
		btnresetdate.setText("\u4ECA\u5929");
		
		chxbxwholeweek = new JCheckBox("\u8BA1\u7B97\u5B8C\u6574\u5468");
		chxbxwholeweek.setToolTipText("\u5982\u4E0D\u52FE\u9009\uFF0C\u5219\u8BA1\u7B97\u5230\u88AB\u9009\u62E9\u7684\u90A3\u4E00\u5929");
		chxbxwholeweek.setSelected(true);
		
		pnlZhiShu = new JPanel();
		
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
							.addComponent(btnresetdate))
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
							.addComponent(btnresetdate, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)))
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
			bkhlpnl = new BkfxHightLightPnl (this.highlightpnlprop, this.bkggmatchcondition);
			bkhlpnl.setStockInfoManager(this.stockmanager );
			bkhlpnl.setCurrentDisplayDate(this.dateChooser.getLocalDate() );
			bkhlpnl.setCurrentExportPeriod(this.globeperiod);
			buttonPane.add(bkhlpnl);
			
			bkhlpnl.addPropertyChangeListener(new PropertyChangeListener() {

	            public void propertyChange(PropertyChangeEvent evt) {

	                if (evt.getPropertyName().equals(BkfxHightLightPnl.TIMESHOULDCHANGE_PROPERTY)) {
	                    @SuppressWarnings("unchecked")
	                    LocalDate newdata = (LocalDate) evt.getNewValue();
	                    LocalDate curselectdate = dateChooser.getLocalDate();
	    				if(!curselectdate.equals(newdata) ) 
	    					dateChooser.setLocalDate(newdata);
	                } 
	             }
	        });
			
			JLabel holdbackgrounddownload = JLabelFactory.createButton("",25, 25);
			holdbackgrounddownload.setHorizontalAlignment(SwingConstants.LEFT);
			holdbackgrounddownload.setToolTipText("��̨����������ͣ,����������ء�");
			holdbackgrounddownload.setIcon(new ImageIcon(BanKuaiFengXi.class.getResource("/images/trafficlight-in-green.png")));
			holdbackgrounddownload.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent arg0) 
				{
					if(SwingUtilities.isLeftMouseButton(arg0) ) {
						if(holdbackgrounddownload.getToolTipText().contains("����")) {
							ImageIcon icon = stockmanager.setGetNodeDataFromDbWhenSystemIdleThreadStatus(false);
//							holdbackgrounddownload.setText("��̨�������ݻָ�");
							holdbackgrounddownload.setToolTipText("��̨��������,�����ͣ���ء�");
							holdbackgrounddownload.setIcon(icon);
						} else {
							ImageIcon icon = stockmanager.setGetNodeDataFromDbWhenSystemIdleThreadStatus(true);
//							holdbackgrounddownload.setText("��̨����������ͣ");
							holdbackgrounddownload.setToolTipText("��̨����������ͣ,����������ء�");
							holdbackgrounddownload.setIcon(icon);
						}
						
					} else 
					if (SwingUtilities.isRightMouseButton(arg0)) {
					} else if (SwingUtilities.isMiddleMouseButton(arg0)) {							
					}
				}
			});
			buttonPane.add(holdbackgrounddownload);
		}
		
		reFormatGui ();

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
	 * ���ɱ���İ��ռ��MAX��ʱ���ã�������
	 */
	private void reFormatGui ()
	{
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
       
       menuItemcancelreviewedtoday = new JMenuItem("ȡ���Ѿ��Ķ�״̬"); //ϵͳĬ�ϰ��ɽ�������
       jPopupMenuoftabbedpane.add(menuItemcancelreviewedtoday);
       
//       jPopupMenuoftabbedpanebk = new JPopupMenu();
//       menuItembktocsv = new JMenuItem("�������а�鵽CSV");
//       jPopupMenuoftabbedpanebk.add(menuItembktocsv);
       
//       menuItemyangxianbktocsv = new JMenuItem("�����������߰�鵽CSV");
//       jPopupMenuoftabbedpanebk.add(menuItemyangxianbktocsv);
       
       JMenu biaojiMenu = new JMenu("��Ǹ���");
       JMenuItem menuItemAddRmvBkToRedSign = new JMenuItem("����/ȡ�����");
       biaojiMenu .add(menuItemAddRmvBkToRedSign );
       menuItemAddRmvBkToYellow = new JMenuItem("����/ȡ���Ʊ�");
       biaojiMenu .add(menuItemAddRmvBkToYellow );
       
       menuItemQiangShibk = new JMenuItem("ǿ�ư��");
	   biaojiMenu .add(menuItemQiangShibk);
	   menuItemRuoShibk = new JMenuItem("���ư��");
	   biaojiMenu .add(menuItemRuoShibk);
	   menuItemDuanQiGuanZhu = new JMenuItem("���ڹ�ע");
	   biaojiMenu .add(menuItemDuanQiGuanZhu);
       
       tableBkZhanBi.getPopupMenu().add(biaojiMenu);
   	   
	   JMenu bkcsvMenu = new JMenu("CSV");
	   tableBkZhanBi.getPopupMenu().add(bkcsvMenu);
       menuItemsiglebktocsv = new JMenuItem("����CSV");
       bkcsvMenu.add(menuItemsiglebktocsv);
       historycsvfileMenu = new JMenu("��ʷ�����ļ�");
       bkcsvMenu.add(historycsvfileMenu);
	   
       
//       menuItemRmvNodeFmFile = new JMenuItem("�޳���ģ���ļ�") ;
//       menuItemRmvNodeFmFile.setEnabled(false);
//       tableGuGuZhanBiInBk.getPopupMenu().add(menuItemRmvNodeFmFile);
		
	   menuItemsMrjh = new JMenuItem("���ռƻ�");
	   tableGuGuZhanBiInBk.getPopupMenu().add(menuItemsMrjh);
	   
	   JMenu stkcsvMenu = new JMenu("CSV");
	   tableGuGuZhanBiInBk.getPopupMenu().add(stkcsvMenu);
	   menuItemsiglestocktocsv = new JMenuItem("����CSV");
       stkcsvMenu.add(menuItemsiglestocktocsv);
       stkhistorycsvfileMenu = new JMenu("��ʷ�����ļ�");
       stkcsvMenu.add(stkhistorycsvfileMenu);
       
       menuItemnonfixperiod = new JMenuItem("��������DPM??WK");
       tableGuGuZhanBiInBk.getPopupMenu().add(menuItemnonfixperiod);
       
       JMenu stockbiaojiMenu = new JMenu("��Ǹ���");
       menuItemAddRmvStockToRed = new JMenuItem("����/ȡ�����");
       stockbiaojiMenu .add(menuItemAddRmvStockToRed );
       menuItemAddRmvStockToYellow = new JMenuItem("����/ȡ���Ʊ�");
       stockbiaojiMenu .add(menuItemAddRmvStockToYellow );
       menuItemQiangShigg = new JMenuItem("��Ϊǿ�Ƹ���");
	   menuItemRuoShigg = new JMenuItem("��Ϊ���Ƹ���");
	   stockbiaojiMenu .add(menuItemQiangShigg);
	   stockbiaojiMenu .add(menuItemRuoShigg);
       tableGuGuZhanBiInBk.getPopupMenu().add(stockbiaojiMenu);
       
       menuItemnonshowselectbkinfo = new JMenuItem("ͬʱ����ѡ��������");
       panelbkwkcjezhanbi.addMenuItem (menuItemnonshowselectbkinfo,0);
       
       JPopupMenu popupMenuGeguNews = new JPopupMenu () ;
       menuItemTempGeGuFromFile = new JMenuItem("�ļ�����");
       popupMenuGeguNews.add(menuItemTempGeGuFromFile);
       menuItemTempGeGuFromTDXSw = new JMenuItem("ͨ�����Զ����鵼��");
       popupMenuGeguNews.add(menuItemTempGeGuFromTDXSw);
       menuItemTempGeGuFromZhidingbk = new JMenuItem("ָ����鵼��");
       popupMenuGeguNews.add(menuItemTempGeGuFromZhidingbk);
       menuItemTempGeGuClear = new JMenuItem("���");
       popupMenuGeguNews.add(menuItemTempGeGuClear);
       tableTempGeGu.getTableHeader().setComponentPopupMenu (popupMenuGeguNews);
	}
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
			        		
			        		if(zhishu_code.trim().equals("000000"))
			        			refreshDaPanFengXiResult ();
			        		else {
			        			BanKuai shanghai = (BanKuai) treeofbkstk.getSpecificNodeByHypyOrCode(zhishu_code, BkChanYeLianTreeNode.TDXBK);
			    				unifiedOperationsAfterUserSelectABanKuai (shanghai);
			    				
			    				// ��λ
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