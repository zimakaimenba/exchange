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
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartCjePnl;
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
import javax.swing.BoxLayout;
import java.awt.FlowLayout;
import javax.swing.JTable;


public class GeGuTDXFengXi extends JDialog 
{
	private static final long serialVersionUID = 1L;

	private BanKuaiAndStockTree treeofbkstk;
	/**
	 * Create the dialog.
	 * @param bkcyl2 
	 */
	public GeGuTDXFengXi(StockInfoManager stockmanager1)
	{
		this.stockmanager = stockmanager1;
		
		treeofbkstk = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks();
		
		this.sysconfig = SystemConfigration.getInstance();
		this.nodeinfotocsv = new NodeInfoToCsv ();
		this.bkfxremind = new BanKuaiFengXiRemindXmlHandler ();
		this.bkggmatchcondition = new BanKuaiGeGuMatchCondition ();

		this.globecalwholeweek = true; //��������

		initializeGui ();
		createEvents ();
		setUpChartDataListeners ();

		initializePaoMaDeng ();
		
		adjustDate (LocalDate.now());
		
	}
	
	private static Logger logger = Logger.getLogger(BanKuaiFengXi.class);
	
	private String globeperiod;
	private Boolean globecalwholeweek; //��������
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
//	private Set<BarChartPanelDataChangedListener> barchartpanelbankuaidatachangelisteners;
//	private Set<PieChartPanelDataChangedListener> piechartpanelbankuaidatachangelisteners;
//	private Set<BarChartPanelDataChangedListener> barchartpanelstockofbankuaidatachangelisteners;
//	private Set<BarChartPanelDataChangedListener> barchartpanelstockdatachangelisteners;
		
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
		
		refreshBanKuaiGeGuTableHightLight ();
		
		tabbedPanebk.setSelectedIndex(2);
		
		tableBkZhanBi.repaint(); //
		
		cyltreecopy.searchAndLocateNodeInTree (selectedbk);
		
		showReminderMessage (bkfxremind.getBankuairemind());
		
		((BanKuaiGeGuTableModel)tableTempGeGu.getModel()).setInterSectionBanKuai(selectedbk); //Ϊ��ʱ������׼��
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
		
		tabbedPanebk.setSelectedIndex(0);
		
		
		LocalDate curselectdate = null;
		try{
			curselectdate = dateChooser.getLocalDate();// dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		} catch (java.lang.NullPointerException e) {
			JOptionPane.showMessageDialog(null,"��������!","Warning",JOptionPane.WARNING_MESSAGE);
			return;
		}
		SvsForNodeOfBanKuai svsbk = new SvsForNodeOfBanKuai ();
		BanKuai zhishubk =  (BanKuai) svsbk.getNode("999999");
		//һ����ͬ����������Լ�������������
		try {
			svsbk.syncBanKuaiAndItsStocksForSpecificTime(selectedbk, CommonUtility.getSettingRangeDate(curselectdate,"large"), curselectdate,
					globeperiod,this.globecalwholeweek);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		svsbk = null;
		
		//��ʾK��
		paneldayCandle.displayQueKou(false);
		refreshKXianOfTDXNodeWithSuperBanKuai ( selectedbk, zhishubk );
		
		//�����������
		if(selectedbk.getBanKuaiLeiXing().equals(BanKuai.HASGGWITHSELFCJL)) { //�и��ɲ���Ҫ���£���Щ�����û�и��ɵ�
			
			//������ķ������������Ŀ>0����Ҫ�ѷ��������ĸ��ɱ�Ǻ�
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
			//�����������ռ��info
			((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).refresh(selectedbk, curselectdate,period);
			((BanKuaiGeGuBasicTableModel)tableExternalInfo.getModel()).refresh(selectedbk, curselectdate,period);
			//������ʾ����
			tabbedPanegegu.setTitleAt(0, "��ǰ��" + curselectdate);
			//��ʾ2�ܵİ�����pie chart
//			for(PieChartPanelDataChangedListener tmplistener : piechartpanelbankuaidatachangelisteners) {
//				tmplistener.updateDate(selectedbk, curselectdate, 0,globeperiod);
//			}
		}
		
		//�������ռ��
//		for(BarChartPanelDataChangedListener tmplistener : barchartpanelbankuaidatachangelisteners) {
//			tmplistener.updatedDate(selectedbk, CommonUtility.getSettingRangeDate(curselectdate,"basic"),curselectdate,globeperiod);
//		}

	}
	/*
	 * �û�ѡ��һ������column�����Ӧ����
	 */
	protected void refreshAfterUserSelectBanKuaiColumn (BanKuai bkcur, String selectedinfo) 
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
		
		showReminderMessage (bkfxremind.getBankuaicolumnremind() );

		//�������ã���ʾѡ���ܷ�������
		if(!menuItemnonshowselectbkinfo.getText().contains("X"))
			return;
		
		//ѡ���ܵİ���������
		LocalDate cursetdate = dateChooser.getLocalDate(); 
		if(!CommonUtility.isInSameWeek(selectdate,cursetdate) ) {
			SvsForNodeOfBanKuai svsbk = new SvsForNodeOfBanKuai ();
			List<TDXNodes> bkwithcje = svsbk.getBanKuaiFenXiQualifiedNodes(selectdate, globeperiod, globecalwholeweek,false);
			svsbk = null;
			((BanKuaiInfoTableModel)tableselectedwkbkzb.getModel()).addBanKuai( bkwithcje );
			((BanKuaiInfoTableModel)tableselectedwkbkzb.getModel()).refresh(selectdate,0,globeperiod);
			
			//��ʾ������
			if(bkcur.getBanKuaiLeiXing().equals(BanKuai.HASGGWITHSELFCJL) ) {//Ӧ�����и��ɵİ��������ʾ���ĸ��ɣ�
//				refreshSpecificBanKuaiFengXiResult (bkcur,selectdate,globeperiod);
				
				LocalDate formerdate = ((BanKuaiGeGuTableModel)tablexuandingzhou.getModel()).getShowCurDate();
				if(formerdate != null) { //ǰ������ʾ��������Ų��tab 4
					//��ʾѡ����+1��Ʊ�������
					((BanKuaiGeGuTableModel)tablexuandingplusone.getModel()).refresh(bkcur, formerdate,globeperiod);
					tabbedPanegegu.setTitleAt(5,  formerdate.toString() );
				}
				
				//��ʾѡ���ܹ�Ʊ�������
				((BanKuaiGeGuTableModel)tablexuandingzhou.getModel()).refresh(bkcur, selectdate,globeperiod);
				tabbedPanegegu.setTitleAt(2, this.getTabbedPanegeguTabTiles(2) + selectdate);
				
				//��ʾѡ����-1��Ʊ�������
				LocalDate selectdate2 = selectdate.minus(1,ChronoUnit.WEEKS).with(DayOfWeek.FRIDAY);
				((BanKuaiGeGuTableModel)tablexuandingminusone.getModel()).refresh(bkcur, selectdate2,globeperiod);
				tabbedPanegegu.setTitleAt(3,  selectdate2 + "(-1)");

				
				//��ʾѡ����-2��Ʊ�������
				LocalDate selectdate3 = selectdate.plus(1,ChronoUnit.WEEKS).with(DayOfWeek.FRIDAY);
				((BanKuaiGeGuTableModel)tablexuandingminustwo.getModel()).refresh(bkcur, selectdate3,globeperiod);
				tabbedPanegegu.setTitleAt(4,  selectdate3 + "(+1)");
				
				refreshBanKuaiGeGuTableHightLight ();
					
				
			}
			
			tabbedPanebk.setTitleAt(1, "ѡ����" + selectdate);
			//�����ҵ����
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
	 * ���һ�� ����
	 * @param selectstock
	 * @param selectedGeguTable
	 */
	protected void unifiedOperationsAfterUserSelectAStock(StockOfBanKuai selectstock) 
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
			
			
			tabbedPanebk.setSelectedIndex(2);
			
			
			
			findInputedNodeInTable ( selectstock.getMyOwnCode() );
//			hightlightSpecificSector (selectstock); //D
//			refreshGeGuFengXiResult (selectstock); //���ɶ԰�������
			
//			refreshTDXGeGuZhanBi ( selectstock.getStock() ); //���ɶԴ�������
			
			paneldayCandle.displayQueKou(true);
			refreshKXianOfTDXNodeWithSuperBanKuai (selectstock, selectstock.getBanKuai() ); //���ɶ԰���K��
			
			//ͬ�����͸��ɸ���������
			
			
			displayNodeInfo (selectstock.getStock());

			refreshCurrentStockTags (selectstock.getStock() );
			//�ڲ�ҵ������Ѱ�Ҹø���
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
	 * �ڸ��ɱ��з�������ĸ���,�������������Ƿ��ָĸ���
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
				
				

	}
//	private void setUpZhongDianGuanZhuBanKuai ()
//	{
//		ServicesForNews zdgzsvs = this.dateChooser.getStockCalendar().getDuanQiGuanZhuService();
//		//DuanQiGuanZhuServices
//		zdgzsvs.getCache().addCacheListener(tableBkZhanBi);
//	}
	/*
	 * �û��ڽ���Ĳ���������ģ���Эͬ����
	 */
	private void setUpChartDataListeners ()
	{
		this.chartpanelhighlightlisteners = new HashSet<>();
//		this.barchartpanelstockofbankuaidatachangelisteners = new HashSet<>();
//		this.barchartpanelbankuaidatachangelisteners = new HashSet<>();
//		this.piechartpanelbankuaidatachangelisteners = new HashSet<>();
//		this.barchartpanelstockdatachangelisteners = new HashSet<>();
		chartpanelhighlightlisteners.add(paneldayCandle);
		
		//ͬ��������Ҫhighlight������
		this.bkggmatchcondition.addCacheListener(tableGuGuZhanBiInBk);
		this.bkggmatchcondition.addCacheListener(tableExternalInfo);
		this.bkggmatchcondition.addCacheListener(tablexuandingzhou);
		this.bkggmatchcondition.addCacheListener(tablexuandingminusone);
		this.bkggmatchcondition.addCacheListener(tablexuandingminustwo);
		this.bkggmatchcondition.addCacheListener(tablexuandingplusone);
		this.bkggmatchcondition.addCacheListener(tableTempGeGu);
		
	}
	/*
	 * ���а��ռ�������ʵ�����
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
		
    	//��ʾ���̳ɽ���
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
				lblhscje.setText( "����û������" );
		}
			
		childnode =  (TDXNodes) svsbk.getNode("999999"); 
		bkdata = ((TDXNodes)childnode).getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		try {
				Double cjerec = bkdata.getChengJiaoEr(curselectdate,0);
				Double zhanbi = bkdata.getChenJiaoErZhanBi(curselectdate, 0);
				lblshcje.setText( df2.format(cjerec /100000000.0) +  " (" + percentFormat.format (zhanbi) + ")");
		} catch (java.lang.NullPointerException e) {
				lblshcje.setText( "����û������" );
		}
		 
		childnode =  (TDXNodes) svsbk.getNode("399006");	
		bkdata = ((TDXNodes)childnode).getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		try {
				Double cjerec = bkdata.getChengJiaoEr(curselectdate,0);
				Double zhanbi = bkdata.getChenJiaoErZhanBi(curselectdate, 0);
				lblcybcje.setText( df2.format(cjerec /100000000.0) +  " (" + percentFormat.format (zhanbi) + ")");
		} catch (java.lang.NullPointerException e) {
				lblcybcje.setText( "����û������" );
		}
			
		childnode =  (TDXNodes) svsbk.getNode("000016");//(TDXNodes)this.allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode("000016", BkChanYeLianTreeNode.TDXBK); 
		bkdata = ((TDXNodes)childnode).getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
		try {
				Double cjerec = bkdata.getChengJiaoEr(curselectdate,0);
				Double zhanbi = bkdata.getChenJiaoErZhanBi(curselectdate, 0);
				lbl50cje.setText( df2.format(cjerec /100000000.0) +  " (" + percentFormat.format (zhanbi) + ")");
		} catch (java.lang.NullPointerException e) {
				lbl50cje.setText( "����û������" );
		}
		//����������ĵ�ǰʱ�䣬Ϊ��ʾ��ע������ʹ��
		((JStockComboBoxModel)combxsearchbk.getModel() ).setCurrentDataDate(curselectdate);
		((JStockComboBoxModel)combxstockcode.getModel() ).setCurrentDataDate(curselectdate);
		
		curselectdate = null;
		svsbk = null;
//		cacheAll = null;
		SystemAudioPlayed.playSound();
	}
	/*
	 * �԰����ɱ�����
	 */
	private void refreshBanKuaiGeGuTableHightLight ()
	{
//		setExportMainConditionBasedOnUserSelection (displayexpc);
		
		setExportMainConditionBasedOnUserSelection (bkggmatchcondition);
		
//		bkfxhighlightvaluesoftableslisteners.forEach(l -> l.hightLightFxValues( displayexpc ) );

		//����panel��˵��hightLightFxValues��һ�����������ã���Ϊpanel ���� �ڵ�������ϼ���ռ��
//		panelGgDpCjeZhanBi.hightLightFxValues(displayexpc);
//		panelbkwkcjezhanbi.hightLightFxValues(displayexpc);
		

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
				exportcond = new ArrayList<BanKuaiGeGuMatchCondition> ();
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
		
		if( !filefmxx.getParentFile().exists() ) {  
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
		exporttask = new ExportTask2(exportcond, curselectdate,globeperiod,filefmxx);
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
		
		btnresetdate.setEnabled(true);
		
		ckboxparsefile.setSelected(false);
		tfldparsedfile.setText("");
		
		paneldayCandle.resetDate();
		
//		editorPanebankuai.resetSelectedBanKuai ();
		
		
	}
	private void clearTheGuiBeforDisplayNewInfoSection2 ()
	{
		((BanKuaiInfoTableModel)tableselectedwkbkzb.getModel()).deleteAllRows();
		tabbedPanebk.setTitleAt(1, "ѡ����" );
		
		tabbedPanegegu.setSelectedIndex(0);
		
		
		
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
	 * �û�ѡ����ɺ���ʾ���������Դ��̵ĸ���ռ��
	 */
//	private void refreshTDXGeGuZhanBi (Stock selectstock)
//	{
//		selectstock.setHasReviewedToday();
//		
//		LocalDate curselectdate = dateChooser.getLocalDate();// dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
////		selectstock = allbksks.getStock(selectstock,CommonUtility.getSettingRangeDate(curselectdate, "large"),curselectdate,
////				NodeGivenPeriodDataItem.WEEK,this.globecalwholeweek);
//		
//		
//		for (BarChartPanelDataChangedListener tmplistener : barchartpanelstockdatachangelisteners) {
//			tmplistener.updatedDate(selectstock, CommonUtility.getSettingRangeDate(curselectdate, "basic"),curselectdate, globeperiod);
//		}
//	}
	/*
	 * ���͸��ɵ�K�ߣ�����ͬʱ��ʾ�����ԶԱ��о�
	 */
	private void refreshKXianOfTDXNodeWithSuperBanKuai (TDXNodes selectnode,TDXNodes superbankuai)
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
		}
		
		 if(superbankuai.getType() == BkChanYeLianTreeNode.TDXBK) {
			 if(treeofbkstk.getSpecificNodeByHypyOrCode(superbankuai.getMyOwnCode(), BkChanYeLianTreeNode.TDXBK ) != null) { //��ʱ����û�а��
				 SvsForNodeOfBanKuai svsbk = new SvsForNodeOfBanKuai ();
				 superbankuai = (TDXNodes) svsbk.getNodeData(superbankuai, CommonUtility.getSettingRangeDate(curselectdate, "large"),curselectdate, 
						 NodeGivenPeriodDataItem.WEEK,this.globecalwholeweek);
				 svsbk.syncNodeData(superbankuai);
			 }
		 }

		paneldayCandle.updatedDate(superbankuai,tmpnode,CommonUtility.getSettingRangeDate(curselectdate, "basic"),curselectdate,NodeGivenPeriodDataItem.DAY);
		pnlcje.updatedDate(tmpnode,CommonUtility.getSettingRangeDate(curselectdate, "basic"),curselectdate,NodeGivenPeriodDataItem.DAY);
		
		ServicesForNewsLabel svslabel = new NewsLabelServices ();
		ServicesForNews svsdqgz = new DuanQiGuanZhuServices ();
    	NewsCache dqgzcache = new NewsCache ("ALL",svsdqgz,svslabel,LocalDate.now().minusMonths(6),LocalDate.now().plusMonths(6));
    	svsdqgz.setCache(dqgzcache);
    	paneldayCandle.displayZhiShuGuanJianRiQiToGui(dqgzcache.produceNews());
    	
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
    					
    					//�ҵ��û�ѡ��İ��
    					BanKuai selectedbk = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(rowindex);
    					unifiedOperationsAfterUserSelectABanKuai (selectedbk);
    					
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

		
//		editorPanebankuai.displayBanKuaiListContentsForStock(selectstock);
		
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
		lblshcje.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				BanKuai shanghai = (BanKuai) treeofbkstk.getSpecificNodeByHypyOrCode("999999", BkChanYeLianTreeNode.TDXBK);
				unifiedOperationsAfterUserSelectABanKuai (shanghai);
				
				// ��λ
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
				
				// ��λ
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
				
				// ��λ
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
				
				// ��λ
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
				
				// ��λ
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
				
				// ��λ
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
				
				// ��λ
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
				
				// ��λ
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
				
				int action = JOptionPane.showConfirmDialog(null, "��Ҫ���¼������������Ƿ������","����", JOptionPane.YES_NO_OPTION);
				if(0 == action) {
					clearTheGuiBeforDisplayNewInfoSection1 ();
					clearTheGuiBeforDisplayNewInfoSection2 ();
					
					
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
					JOptionPane.showMessageDialog(null,"��ѡ��һ�����","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				int modelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(row); 
				StockOfBanKuai stock = ((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).getStock(modelRow);

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
					JOptionPane.showMessageDialog(null,"��ѡ��һ�����","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				int modelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(row); 
				StockOfBanKuai stock = ((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).getStock(modelRow);

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
				BanKuai bankuai = ((BanKuaiInfoTableModel) tableBkZhanBi.getModel()).getBanKuai(modelRow);

				
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
				BanKuai bankuai = ((BanKuaiInfoTableModel) tableBkZhanBi.getModel()).getBanKuai(modelRow);
				
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
        					JOptionPane.showMessageDialog(null,"��ѡ��һ����飡","Warning",JOptionPane.WARNING_MESSAGE);
        					return;
        				}
        				int modelRow = tableBkZhanBi.convertRowIndexToModel(row);
        				BanKuai selectedbk = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(modelRow);
                    	
        				paneldayCandle.displayQueKou(true);
        				refreshKXianOfTDXNodeWithSuperBanKuai ( stockofbank, selectedbk );
                		
                		
                    } else if(zhishuinfo.toLowerCase().equals("dapanzhishu") ) {
                    	String danpanzhishu = JOptionPane.showInputDialog(null,"��������ӵĴ���ָ��", "999999");
//                    	BanKuai zhishubk =  (BanKuai) allbksks.getAllBkStocksTree().getSpecificNodeByHypyOrCode(danpanzhishu.toLowerCase(),BkChanYeLianTreeNode.TDXBK);
                    	SvsForNodeOfBanKuai svsbk = new SvsForNodeOfBanKuai ();
                    	BanKuai zhishubk = (BanKuai) svsbk.getNode(danpanzhishu.toLowerCase());
                    	svsbk = null;
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
            	
            	DateRangeSelectPnl datachoose = new DateRangeSelectPnl ( LocalDate.now().minusDays(1), LocalDate.now() ); 
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
		
		//���ӵ�������
		btnaddexportcond.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				if(SwingUtilities.isLeftMouseButton(arg0) ) {
					
//					if(!ckboxshowcje.isSelected() && !ckbxdpmaxwk.isSelected() && !chkliutongsz.isSelected() && !ckbxcjemaxwk.isSelected()){
//						JOptionPane.showMessageDialog(null,"δ���õ����������������õ���������");
//						return;
//					} 
					
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
					JOptionPane.showMessageDialog(null,"������ͻ����ʾ���Ƿ���");
					chbxzhangfu.setSelected(false);
					return;
				}
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
//						displayStockSuoShuBanKuai((Stock)userinputnode);
					}
					
//					int rowindex = tableGuGuZhanBiInBk.getSelectedRow();
//					int modelRow = tableGuGuZhanBiInBk.convertRowIndexToView(rowindex);
					if(findInputedNodeInTable (nodecode)) { 
						
						paneldayCandle.resetDate();
					}
					
//					if(!findInputedNodeInTable (nodecode)) { //���û���ҵ�
//						tableGuGuZhanBiInBk.setRowSelectionInterval(rowindex,rowindex);
//						tableGuGuZhanBiInBk.getSelectionModel().clearSelection() ; //�ѵ�ǰ��table��ѡ��ȡ���������û������Ѿ��ҵ�
						//��Ȼȡ����table�еĸ��ɣ�����Ҫ�Ѽ���ͼ������Ϣ����������û���ȥ˫����Щͼ������������⡣
						
//						panelGgDpCjeZhanBi.resetDate();
//						paneldayCandle.resetDate();
//					} else { //�ڵ�ǰ�����еĻ����Ͱ����PANEL���
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
//    					//�ҵ��û�ѡ��İ��
//    					BanKuai selectedbk = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(rowindex);
//    					unifiedOperationsAfterUserSelectABanKuai (selectedbk);
//    					
//    					//�Ҳ����ù�Ʊ
//    					String stockcode = combxstockcode.getSelectedItem().toString().substring(0, 6);
//    					if(!findInputedNodeInTable (stockcode) )
//    						JOptionPane.showMessageDialog(null,"��ĳ�����ɱ���û�з��ָùɣ����������ʱ����ڸù�ͣ��","Warning",JOptionPane.WARNING_MESSAGE);
//    					
////    					Toolkit.getDefaultToolkit().beep();
//    					hourglassCursor = null;
//    					Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
//    					setCursor(hourglassCursor2);
//    					
//    					SystemAudioPlayed.playSound();
//    				} else {
//    					JOptionPane.showMessageDialog(null,"�ð������Ϊ����ʾ�ڰ���б��У����� ���ص��ע���ҵ���� ������ã�","Warning",JOptionPane.WARNING_MESSAGE);
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
					JOptionPane.showMessageDialog(null,"��ѡ��һ����Ʊ��","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				int modelRow = tableGuGuZhanBiInBk.convertRowIndexToModel(row);
				StockOfBanKuai selectstock = ((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).getStock (modelRow);
				
				if (arg0.getClickCount() == 1) { //�û����һ��
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
					JOptionPane.showMessageDialog(null,"��ѡ��һ����Ʊ��","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
//				clearTheGuiBeforDisplayNewInfoSection1 ();
				clearTheGuiBeforDisplayNewInfoSection2 ();
				
				
				int modelRow = tableTempGeGu.convertRowIndexToModel(row);
				StockOfBanKuai selectstock = ((BanKuaiGeGuTableModel)tableTempGeGu.getModel()).getStock (modelRow);
				
				if (arg0.getClickCount() == 1) { //�û����һ��
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
					JOptionPane.showMessageDialog(null,"��ѡ��һ����Ʊ��","Warning",JOptionPane.WARNING_MESSAGE);
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
					JOptionPane.showMessageDialog(null,"��ѡ��һ����Ʊ��","Warning",JOptionPane.WARNING_MESSAGE);
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
					JOptionPane.showMessageDialog(null,"��ѡ��һ����Ʊ��","Warning",JOptionPane.WARNING_MESSAGE);
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
					JOptionPane.showMessageDialog(null,"��ѡ��һ����Ʊ��","Warning",JOptionPane.WARNING_MESSAGE);
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
					JOptionPane.showMessageDialog(null,"��ѡ��һ����Ʊ��","Warning",JOptionPane.WARNING_MESSAGE);
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
		Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
		setCursor(hourglassCursor);
		
		LocalDate curselectdate = null;
		try{
			curselectdate = dateChooser.getLocalDate();// dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		} catch (java.lang.NullPointerException e) {
			JOptionPane.showMessageDialog(null,"��������!","Warning",JOptionPane.WARNING_MESSAGE);
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
				logger.info(tmpgegu + "��������");
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
		Integer rowindex = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuaiRowIndex( nodecode );
		
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
		BanKuai selectedbk = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(rowindex);
				
		unifiedOperationsAfterUserSelectABanKuai (selectedbk);
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
				DateRangeSelectPnl datachoose = new DateRangeSelectPnl (52); 
				JOptionPane.showMessageDialog(null, datachoose,"Ϊ" + tmpnode.getMyOwnName() + "ѡ�񵼳�ʱ���", JOptionPane.OK_CANCEL_OPTION);
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
	 * �û�˫��ĳ��nodeռ��chart����Ŵ���ʾ��nodeһ���ڵ�ռ����������
	 */
	protected void displayNodeLargerPeriodData(TDXNodes node, LocalDate datekey) 
	{
		if(!this.globecalwholeweek) {
			JOptionPane.showMessageDialog(null,"�������ܷ���ģʽ����֧�ָ���Χ��ʾ�����������!","Warning",JOptionPane.WARNING_MESSAGE);
			return;
		}
		
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
				bkcur = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(modelRow);
				bkcur = (BanKuai) svsbk.getNodeData(bkcur, overlapldstartday, overlapldendday, globeperiod, globecalwholeweek);
				svsbk.syncNodeData(bkcur);
			}
		} else if(node.getType() == BkChanYeLianTreeNode.BKGEGU ) {
			BanKuai bk = ((StockOfBanKuai)node).getBanKuai();
			node = (TDXNodes) svsbk.getNodeData(node, overlapldstartday, overlapldendday, globeperiod, globecalwholeweek);
			svsbk.syncNodeData(node);
		} else if(node.getType() == BkChanYeLianTreeNode.DAPAN ) {
//			node = this.allbksks.getDaPan (requirestart.plusWeeks(1),globeperiod); //ͬ����������,�����������ط������
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
	 * �û���������������
	 */
	protected void initializeExportConditions () 
	{
		if( exportcond == null)
			exportcond = new ArrayList<BanKuaiGeGuMatchCondition> ();
		
		BanKuaiGeGuMatchCondition expc = new BanKuaiGeGuMatchCondition ();
		setExportMainConditionBasedOnUserSelection (expc);

		ExtraExportConditions extraexportcondition = new ExtraExportConditions (expc);
		
		int extraresult = JOptionPane.showConfirmDialog(null,extraexportcondition , "���ӵ�������:", JOptionPane.OK_CANCEL_OPTION);
		if(extraresult == JOptionPane.OK_OPTION) { //������������ 
			
			expc = extraexportcondition.getSettingCondition ();
			if( expc.shouldExportOnlyCurrentBanKuai() ) {
					int row = tableBkZhanBi.getSelectedRow();
					if(row <0) {
						JOptionPane.showMessageDialog(null,"��ѡ��һ����飡","Warning",JOptionPane.WARNING_MESSAGE);
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
//	 * �û�������������buttion ��toolstip��ʾ�û����õ�����
//	 */
//	private void decrorateExportButton(BanKuaiGeGuMatchCondition expc) 
//	{
//				//��tooltips��ʾ��ǰ�м����Ѿ����ӵ�����������
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
	 * ͬʱ���� ������ʾ��ѡ�� 
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
		//��ѡ���ļ�
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
		
		if(!filename.endsWith("EBK") && !filename.endsWith("XML")) { //���ǰ���ļ�
			tfldparsedfile.setText("�ļ�����������ѡ���ļ���");
			ckboxparsefile.setSelected(false);
			return;
		}
		
		ServicesForBkfxEbkOutPutFileDirectRead bkfxfh = new ServicesForBkfxEbkOutPutFileDirectRead ();
		LocalDate edbfiledate = bkfxfh.setBkfeOutPutFile(filename);
		if(edbfiledate != null) {
			int exchangeresult = JOptionPane.showConfirmDialog(null,"�ļ�ָ��������" + edbfiledate + "���Ƿ���ĵ������ڣ�", "�Ƿ�������ڣ�", JOptionPane.OK_CANCEL_OPTION);
			if(exchangeresult != JOptionPane.CANCEL_OPTION) {
				LocalDate curselectdate = dateChooser.getLocalDate();
				if(!curselectdate.equals(edbfiledate) ) 
					this.dateChooser.setLocalDate(edbfiledate);
			 } else { //�û�������Ϊ�����ļ�
				 bkfxfh.resetBkfxFileDate(this.dateChooser.getLocalDate());
			 }
		} else {
			bkfxfh.resetBkfxFileDate(this.dateChooser.getLocalDate());
		}
//		int exchangeresult = JOptionPane.showConfirmDialog(null,"�ļ�ָ��������" + edbfiledate + "���Ƿ���ĵ������ڣ�", "�Ƿ�������ڣ�", JOptionPane.OK_CANCEL_OPTION);
//		if(exchangeresult != JOptionPane.CANCEL_OPTION) {
//			LocalDate curselectdate = dateChooser.getLocalDate();
//			if(!curselectdate.equals(edbfiledate) ) 
//				this.dateChooser.setLocalDate(edbfiledate);
//		 } else { //�û�������Ϊ�����ļ�
//			 bkfxfh.resetBkfxFileDate(this.dateChooser.getLocalDate());
//		 }
				
		 bkfxfh.patchOutPutFileToTrees (treeofbkstk );
		 tfldparsedfile.setText(filename);
	}
	
	
		/*
	 * ������
	 */
	private void initializePaoMaDeng() 
	{
		// TODO Auto-generated method stub
		String title = "���ռƻ�:";
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
	 * �ڱ��ܸ�����Ʊռ�ȵ�D�@ʾ�x�еĹ�Ʊ
	 */
//	protected void hightlightSpecificSector(StockOfBanKuai selectstock) 
//	{
//		String stockcode = selectstock.getMyOwnCode();
//		try {
//			 String stockname = selectstock.getMyOwnName().trim(); 
//			 for(PieChartPanelDataChangedListener tmplistener : piechartpanelbankuaidatachangelisteners) {
//					tmplistener.hightlightSpecificSector (stockcode+stockname);
//			 }
//		 } catch ( java.lang.NullPointerException e) {
//			 for(PieChartPanelDataChangedListener tmplistener : piechartpanelbankuaidatachangelisteners) {
//					tmplistener.hightlightSpecificSector (stockcode);
//			 }
//		 }
//	}
	
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
	private BanKuaiFengXiCategoryBarChartCjePnl pnlcje;
	
	
	private void initializeGui() {
		setTitle("\u677F\u5757\u5206\u6790");
		setBounds(100, 100, 1923, 1033);
		getContentPane().setLayout(new BorderLayout());
		
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.EAST);
		
		UIManager.put("TabbedPane.selected", Color.yellow); 
		
		JPanel panel = new JPanel();
		JPanel panel_1 = new JPanel();
		
		paneldayCandle = new BanKuaiFengXiCandlestickPnl();
		
		scrldailydata = new JScrollPane();
		
		editorPanebankuai = new JScrollPane();
		
		pnlcje = new BanKuaiFengXiCategoryBarChartCjePnl();
		
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
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(pnlcje, GroupLayout.DEFAULT_SIZE, 1108, Short.MAX_VALUE)
								.addComponent(editorPanebankuai, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 1108, Short.MAX_VALUE))
							.addGap(18)
							.addComponent(scrldailydata, GroupLayout.PREFERRED_SIZE, 375, GroupLayout.PREFERRED_SIZE))
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
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addComponent(pnlcje, GroupLayout.PREFERRED_SIZE, 263, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(editorPanebankuai, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE))
								.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 831, GroupLayout.PREFERRED_SIZE)))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(paneldayCandle, GroupLayout.PREFERRED_SIZE, 602, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(scrldailydata, GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)))
					.addContainerGap())
		);
		
		tfldselectedmsg = new JPanel ();
		scrldailydata.setViewportView(tfldselectedmsg);
		tfldselectedmsg.setLayout(new BoxLayout(tfldselectedmsg, BoxLayout.Y_AXIS));
		
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
		tabbedPanegegu.addTab("\u4E34\u65F6\u4E2A\u80A1", null, scrollPaneTempGeGu, null);
		tabbedPanegegu.setBackgroundAt(6, Color.CYAN);
		
		tableTempGeGu = new BanKuaiGeGuTable (this.stockmanager);
		scrollPaneTempGeGu.setViewportView(tableTempGeGu);
		
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
						GeGuTDXFengXi.this.setVisible(false);
						GeGuTDXFengXi.this.dispatchEvent(new WindowEvent(GeGuTDXFengXi.this, WindowEvent.WINDOW_CLOSING));
					}
				});
				okButton.setActionCommand("OK");
				getRootPane().setDefaultButton(okButton);
			}
			
			ckbxma = new JCheckBox("\u7A81\u51FACLOSE vs. MA");
			ckbxma.setFont(new Font("����", Font.PLAIN, 12));
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
			ckboxparsefile.setFont(new Font("����", Font.ITALIC, 12));
			ckboxparsefile.setForeground(Color.ORANGE);
			
			
			tfldparsedfile = new JTextField();
			tfldparsedfile.setForeground(Color.ORANGE);
			tfldparsedfile.setColumns(18);
			tfldparsedfile.setToolTipText(tfldparsedfile.getText());
			
			ckbxdpmaxwk = new JCheckBox("\u7A81\u51FADPMAXWK>=");
			
			ckbxdpmaxwk.setForeground(Color.RED);
			
			tflddisplaydpmaxwk = new JTextField();
			tflddisplaydpmaxwk.setPreferredSize(new Dimension(20, 25));
			tflddisplaydpmaxwk.setForeground(Color.RED);
			tflddisplaydpmaxwk.setText("4");
//			tflddisplaydpmaxwk.setColumns(2);
			
			chkliutongsz = new JCheckBox("\u7A81\u51FA\u6D41\u901A\u5E02\u503C(\u4EBF)");
			chkliutongsz.setBackground(Color.WHITE);
			
			chkliutongsz.setForeground(Color.MAGENTA);
			
			tfldltszmin = new JTextField();
			tfldltszmin.setPreferredSize(new Dimension(30, 25));
			tfldltszmin.setForeground(Color.MAGENTA);
			tfldltszmin.setText("5");
//			tfldltszmin.setColumns(2);
			
			ckbxcjemaxwk = new JCheckBox("\u7A81\u51FA\u5468\u65E5\u5E73\u5747\u6210\u4EA4\u989DMAXWK>=");
			ckbxcjemaxwk.setToolTipText("\u7A81\u51FA\u5468\u65E5\u5E73\u5747\u6210\u4EA4\u989DMAXWK>=");
			ckbxcjemaxwk.setFont(new Font("����", Font.PLAIN, 12));
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
			tfldltszmax.setPreferredSize(new Dimension(30, 25));
			tfldltszmax.setText("80");
			tfldltszmax.setForeground(Color.MAGENTA);
//			tfldltszmax.setColumns(10);
			
			chbxquekou = new JCheckBox("\u7A81\u51FA\u56DE\u8865\u4E0B\u8DF3/\u4E0A\u8DF3");
			chbxquekou.setToolTipText("\u7A81\u51FA\u56DE\u8865\u4E0B\u8DF3/\u4E0A\u8DF3");
			chbxquekou.setFont(new Font("����", Font.ITALIC, 12));
			
			chbxquekou.setForeground(Color.PINK);
			
			chckbxdpminwk = new JCheckBox("\u7A81\u51FADPMINWK>=");
			chckbxdpminwk.setToolTipText("\u7A81\u51FADPMINWK>=");
			chckbxdpminwk.setFont(new Font("����", Font.ITALIC, 12));
			chckbxdpminwk.setForeground(Color.GREEN);
			
			tflddpminwk = new JTextField();
			tflddpminwk.setPreferredSize(new Dimension(20, 25));
			tflddpminwk.setText("8");
//			tflddpminwk.setColumns(10);
			
			chbxzhangfu = new JCheckBox("\u7A81\u51FA\u6DA8\u8DCC\u5E45");
			chbxzhangfu.setToolTipText("\u7A81\u51FA\u6DA8\u8DCC\u5E45");
			chbxzhangfu.setForeground(Color.PINK);
			chbxzhangfu.setFont(new Font("����", Font.ITALIC, 12));
			
			tfldzhangfumin = new JTextField();
			tfldzhangfumin.setPreferredSize(new Dimension(25, 25));
			tfldzhangfumin.setText("5");
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
       
       menuItemAddRmvBkToYellow = new JMenuItem("����/ȡ���Ʊ�");
       tableBkZhanBi.getPopupMenu().add(menuItemAddRmvBkToYellow);
       
       menuItemsiglebktocsv = new JMenuItem("������鵽CSV");
       menuItemQiangShibk = new JMenuItem("��Ϊǿ�ư��");
	   menuItemRuoShibk = new JMenuItem("��Ϊ���ư��");
//	   tableBkZhanBi.getPopupMenu().add(menuItemQiangShibk);
//	   tableBkZhanBi.getPopupMenu().add(menuItemRuoShibk);
	   tableBkZhanBi.getPopupMenu().add(menuItemsiglebktocsv);
       
//       menuItemRmvNodeFmFile = new JMenuItem("�޳���ģ���ļ�") ;
//       menuItemRmvNodeFmFile.setEnabled(false);
//       tableGuGuZhanBiInBk.getPopupMenu().add(menuItemRmvNodeFmFile);
		
	   menuItemQiangShigg = new JMenuItem("��Ϊǿ�Ƹ���");
	   menuItemRuoShigg = new JMenuItem("��Ϊ���Ƹ���");
//	   tableGuGuZhanBiInBk.getPopupMenu().add(menuItemQiangShigg);
//	   tableGuGuZhanBiInBk.getPopupMenu().add(menuItemRuoShigg);
	   
       menuItemsiglestocktocsv = new JMenuItem("�������ɵ�CSV");
       tableGuGuZhanBiInBk.getPopupMenu().add(menuItemsiglestocktocsv);
       
       menuItemnonfixperiod = new JMenuItem("��������DPM??WK");
       tableGuGuZhanBiInBk.getPopupMenu().add(menuItemnonfixperiod);
       
       menuItemAddRmvStockToYellow = new JMenuItem("����/ȡ���Ʊ�");
       tableGuGuZhanBiInBk.getPopupMenu().add(menuItemAddRmvStockToYellow);
       
       menuItemnonshowselectbkinfo = new JMenuItem("ͬʱ����ѡ���ܷ�������");
       
       JPopupMenu popupMenuGeguNews = new JPopupMenu () ;
       menuItemTempGeGuFromFile = new JMenuItem("�ļ�����");
       popupMenuGeguNews.add(menuItemTempGeGuFromFile);
       menuItemTempGeGuFromTDXSw = new JMenuItem("ͨ�����Զ����鵼��");
       popupMenuGeguNews.add(menuItemTempGeGuFromTDXSw);
       tableTempGeGu.getTableHeader().setComponentPopupMenu (popupMenuGeguNews);
       
       
       
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
}



