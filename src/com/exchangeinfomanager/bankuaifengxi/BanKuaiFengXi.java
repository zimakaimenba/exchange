package com.exchangeinfomanager.bankuaifengxi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import java.util.regex.Pattern;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreePath;

import org.jfree.chart.ChartFactory;
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

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.DaPan;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.bankuaichanyelian.BanKuaiAndChanYeLian;
import com.exchangeinfomanager.bankuaichanyelian.BkChanYeLianTree;
import com.exchangeinfomanager.bankuaichanyelian.HanYuPinYing;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiGeGuTable;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiGeGuTableModel;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.gui.subgui.BanKuaiListEditorPane;
import com.exchangeinfomanager.gui.subgui.JStockComboBox;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.google.common.collect.Multimap;

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
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.beans.PropertyChangeEvent;
import javax.swing.JTable;
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
import javax.swing.BoxLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JEditorPane;
import javax.swing.JTabbedPane;
import javax.swing.JComboBox;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.SystemColor;

public class BanKuaiFengXi extends JDialog {

	

	private StockInfoManager stockmanager;



	/**
	 * Create the dialog.
	 */
	public BanKuaiFengXi(StockInfoManager stockmanager1, BanKuaiAndChanYeLian bkcyl2)
	{
		this.stockmanager = stockmanager1;
		initializeGui ();
		this.bkcyl = bkcyl2;
//		this.bkcyltree = bkcyl.getBkChanYeLianTree();
//		if(parsedfilename2 != null)
//			this.tfldparsedfile.setText(parsedfilename2);
		this.bkdbopt = new BanKuaiDbOperation ();
		this.sysconfig = SystemConfigration.getInstance();

		createEvents ();
	}
	
//	private BkChanYeLianTree bkcyltree;
	private BanKuaiDbOperation bkdbopt;
//	private HashMap<String, BanKuai> allbkandzs;
	private LocalDate lastselecteddate;
	private BanKuaiAndChanYeLian bkcyl;
	private SystemConfigration sysconfig;
	
	/*
	 * 所有板块占比增长率的排名
	 */
	private void initializeBanKuaiZhanBiByGrowthRate ()
	{
    	LocalDate curselectdate = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    	
    	HashMap<String, BanKuai> bkhascjl = new HashMap<String, BanKuai> ();
    	
    	BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)this.bkcyl.getBkChanYeLianTree().getModel().getRoot();
		int bankuaicount = bkcyl.getBkChanYeLianTree().getModel().getChildCount(treeroot);
		for(int i=0;i< bankuaicount; i++) {
			BanKuai childnode = (BanKuai)this.bkcyl.getBkChanYeLianTree().getModel().getChild(treeroot, i);
			String bkcode = childnode.getMyOwnCode();
			childnode = this.bkcyl.getBanKuai(childnode.getMyOwnCode(), curselectdate); 
    		if(childnode.getChenJiaoErZhanBiInGivenPeriod() != null 
    				&& !childnode.getChenJiaoErZhanBiInGivenPeriod().isEmpty()  //有些指数是没有个股和成交量的，不列入比较范围
    				&& childnode.getSpecficChenJiaoErRecord(curselectdate) != null) //板块当周没有数据也不考虑，板块一般不可能没有数据，没有数据说明该板块这周还没有诞生，或者过去有，现在成交量已经不存入数据库
    			bkhascjl.put(bkcode, childnode);
		}

    	((BanKuaiFengXiZhanBiPaiMingTableModel)tableBkZhanBi.getModel()).refresh(bkhascjl,curselectdate);
	}
	/*
	 * 显示板块的占比和个股
	 */
	protected void refreshCurentBanKuaiFengXiResult(BanKuai selectedbk) 
	{
		panelbkcje.resetDate();
		panelbkwkzhanbi.resetDate();
		pnllastestggzhanbi.resetDate();
		panelbkwkzhanbi.resetDate();
		panelgeguwkzhanbi.resetDate();
		panelselectwkgeguzhanbi.resetDate();
		panelLastWkGeGuZhanBi.resetDate();
		panelgegucje.resetDate();
		tabbedPane.setTitleAt(1, "选定周");
		
		((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).deleteAllRows();
		((BanKuaiGeGuTableModel)tablexuandingzhou.getModel()).deleteAllRows();
		((BanKuaiGeGuTableModel)tablexuandingminusone.getModel()).deleteAllRows();
		((BanKuaiGeGuTableModel)tablexuandingminustwo.getModel()).deleteAllRows();
		((BanKuaiGeGuTableModel)tablexuandingplusone.getModel()).deleteAllRows();
		((BanKuaiGeGuTableModel)tablexuandingplustwo.getModel()).deleteAllRows();
		
		LocalDate curselectdate = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		//板块自身占比
		DaPan dapan =  (DaPan) bkcyl.getBkChanYeLianTree().getSpecificNodeByHypyOrCode("000000");
    	panelbkcje.setNodeJiaoYiErByWeek((BkChanYeLianTreeNode)selectedbk,curselectdate,dapan);
		panelbkwkzhanbi.setNodeZhanBiByWeek((BkChanYeLianTreeNode)selectedbk,curselectdate,dapan);
		
		//更新板块所属个股
		selectedbk = bkcyl.getAllGeGuOfBanKuai (selectedbk);
		((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).refreshByZhanBiGrowthRate(selectedbk, curselectdate);
		//更改显示日期
		tabbedPane.setTitleAt(0, "当前周" + curselectdate);
	  	      
		//显示2周的板块个股pie chart
		pnllastestggzhanbi.setBanKuaiNeededDisplay(selectedbk,Integer.parseInt(tfldweight.getText() ), curselectdate );
		panelLastWkGeGuZhanBi.setBanKuaiNeededDisplay(selectedbk,Integer.parseInt(tfldweight.getText() ),curselectdate.with(DayOfWeek.SATURDAY).minus(1,ChronoUnit.WEEKS) );
	}
	/*
	 * 几个表显示用户在选择周个股占比增长率排名等
	 */
	private void refreshSpecificBanKuaiFengXiResult (BanKuai selectedbk, LocalDate selecteddate)
	{
//		int tabCount = tabbedPane.getTabCount();
//	    for (int i = 0; i < tabCount; i++) {
//	    	Component comp = tabbedPane.getComponent(i);
//	   
//	    	String label = tabbedPane.getTitleAt(i);
//	    	System.out.println(i + label);
//	    	
//	    }
	    
		selectedbk = bkcyl.getAllGeGuOfBanKuai (selectedbk);
		HashMap<String, Stock> allbkge = selectedbk.getSpecificPeriodBanKuaiGeGu(selecteddate);
		HashSet<String> stockinparsefile = selectedbk.getParseFileStockSet ();
		
		//显示选定周股票排名情况
		((BanKuaiGeGuTableModel)tablexuandingzhou.getModel()).refreshByZhanBiGrowthRate(selectedbk, selecteddate);
		tabbedPane.setTitleAt(1, "选定周" + selecteddate);
		
		//显示选定周-1股票排名情况
		LocalDate selectdate2 = selecteddate.minus(1,ChronoUnit.WEEKS).with(DayOfWeek.MONDAY);
		if( checkDateBetweenBanKuaiDate (selectedbk,selectdate2) ) //板块要有该周数据
			((BanKuaiGeGuTableModel)tablexuandingminusone.getModel()).refreshByZhanBiGrowthRate(selectedbk, selectdate2);
		else
			((BanKuaiGeGuTableModel)tablexuandingminusone.getModel()).deleteAllRows();
		
		//显示选定周-2股票排名情况
		LocalDate selectdate3 = selecteddate.minus(2,ChronoUnit.WEEKS).with(DayOfWeek.MONDAY);
		if( checkDateBetweenBanKuaiDate (selectedbk,selectdate3) )
			((BanKuaiGeGuTableModel)tablexuandingminustwo.getModel()).refreshByZhanBiGrowthRate(selectedbk, selectdate3);
		else 
			((BanKuaiGeGuTableModel)tablexuandingminustwo.getModel()).deleteAllRows();
		
		//显示选定周+1股票排名情况
		LocalDate selectdate4 = selecteddate.plus(1,ChronoUnit.WEEKS).with(DayOfWeek.MONDAY);
		if( checkDateBetweenBanKuaiDate (selectedbk,selectdate4) )
			((BanKuaiGeGuTableModel)tablexuandingplusone.getModel()).refreshByZhanBiGrowthRate(selectedbk, selectdate4);
		else
			((BanKuaiGeGuTableModel)tablexuandingplusone.getModel()).deleteAllRows();
		
		//显示选定周+2股票排名情况
		LocalDate selectdate5 = selecteddate.plus(2,ChronoUnit.WEEKS).with(DayOfWeek.MONDAY);
		if( checkDateBetweenBanKuaiDate (selectedbk,selectdate5) )
			((BanKuaiGeGuTableModel)tablexuandingplustwo.getModel()).refreshByZhanBiGrowthRate(selectedbk, selectdate5);
		else
			((BanKuaiGeGuTableModel)tablexuandingplustwo.getModel()).deleteAllRows();
	}
	private boolean checkDateBetweenBanKuaiDate(BanKuai selectedbk, LocalDate selectdate) 
	{
		LocalDate bkend = selectedbk.getRecordsEndDate();
		LocalDate bkstart = selectedbk.getRecordsStartDate();
		
		if(  CommonUtility.isInSameWeek(bkstart,selectdate) ||  CommonUtility.isInSameWeek(bkend,selectdate)     
				|| (selectdate.isAfter(bkstart) && selectdate.isBefore(bkend)) ) 
			return true;
		else
			return false;
	}
	/*
	 * 显示个股在板块内的占比
	 */
	private void refreshGeGuFengXiResult (Stock stock)
	{
			LocalDate curselectdate = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			DaPan dapan =  (DaPan) bkcyl.getBkChanYeLianTree().getSpecificNodeByHypyOrCode("000000");
			
			panelgeguwkzhanbi.resetDate();
			panelgeguwkzhanbi.setNodeZhanBiByWeek(stock,curselectdate,dapan);
			
			panelgegucje.resetDate();
			panelgegucje.setNodeJiaoYiErByWeek(stock,curselectdate,dapan);
			
			Comparable datekey = panelbkwkzhanbi.getCurSelectedBarDate ();
			if(datekey != null) {
				panelgegucje.highLightSpecificBarColumn (datekey);
				panelgeguwkzhanbi.highLightSpecificBarColumn (datekey);
			}
			
			tflddingweigegu.setText(stock.getMyOwnCode());
	}

	
	public void setBanKuaiEndiorPaneContents (String fenxibkxml)
	{
		editorPanebankuai.setText(fenxibkxml);
	}
	public void setFenXiDate (Date fxdate)
	{
		dateChooser.setDate(fxdate);
	}

	
	private void displayStockSuoShuBanKuai(Stock selectstock) 
	{
//		 selectstock = bkdbopt.getTDXBanKuaiForAStock (selectstock); //通达信板块信息
		 HashMap<String, String> suosusysbankuai = ((Stock)selectstock).getGeGuSuoShuTDXSysBanKuaiList();
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
	

	private void createEvents() 
	{
		//显示选中的那一周的个股占比比例
		panelbkwkzhanbi.getChartPanel().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				//显示选择周的成交额占比
				String selectdate = panelbkwkzhanbi.getCurSelectedBarDate().toString();
				LocalDate selectdate1 = CommonUtility.formateStringToDate(selectdate);
				BanKuai bkcur = (BanKuai)panelbkwkzhanbi.getCurDisplayedNode ();
				
				panelselectwkgeguzhanbi.setBanKuaiNeededDisplay(bkcur,Integer.parseInt(tfldweight.getText() ), selectdate1  );
				
				//显示选中周股票占比增加率排名等
				refreshSpecificBanKuaiFengXiResult (bkcur,selectdate1);
			}
		});
		
		//同步几个panel
		panelbkcje.getChartPanel().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				Comparable datekey = panelbkcje.getCurSelectedBarDate ();
				panelbkwkzhanbi.highLightSpecificBarColumn (datekey);
				panelgegucje.highLightSpecificBarColumn (datekey);
				panelgeguwkzhanbi.highLightSpecificBarColumn (datekey);
			}
		});
		panelbkwkzhanbi.getChartPanel().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				Comparable datekey = panelbkwkzhanbi.getCurSelectedBarDate ();
				panelbkcje.highLightSpecificBarColumn (datekey);
				panelgegucje.highLightSpecificBarColumn (datekey);
				panelgeguwkzhanbi.highLightSpecificBarColumn (datekey);
			}
		});
		panelgegucje.getChartPanel().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				Comparable datekey = panelgegucje.getCurSelectedBarDate ();
				panelbkcje.highLightSpecificBarColumn (datekey);
				panelbkwkzhanbi.highLightSpecificBarColumn (datekey);
				panelgeguwkzhanbi.highLightSpecificBarColumn (datekey);
			}
		});
		panelgeguwkzhanbi.getChartPanel().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				Comparable datekey = panelgeguwkzhanbi.getCurSelectedBarDate ();
				panelbkcje.highLightSpecificBarColumn (datekey);
				panelbkwkzhanbi.highLightSpecificBarColumn (datekey);
				panelgegucje.highLightSpecificBarColumn (datekey);
			}
		});
		
		
		cbxstockcode.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) 
			{
				if(arg0.getStateChange() == ItemEvent.SELECTED) {
					BkChanYeLianTreeNode userinputnode = cbxstockcode.getUserInputNode();
					if(userinputnode.getType() == 6 ) {
						displayStockSuoShuBanKuai((Stock)userinputnode);
					}
//					statChangeActions ();
					
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
				
				panelbkwkzhanbi.resetDate();
	    		panelgeguwkzhanbi.resetDate();
	    		pnllastestggzhanbi.resetDate();
	    		panelLastWkGeGuZhanBi.resetDate();
	    		((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).deleteAllRows();
	    		initializeBanKuaiZhanBiByGrowthRate ();
	    		
	    		lastselecteddate = requirestart;


			}
		});
		btnsixmonthafter.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				LocalDate startday = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				LocalDate requirestart = startday.with(DayOfWeek.SATURDAY).plus(sysconfig.banKuaiFengXiMonthRange()-4,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
				dateChooser.setDate(Date.from(requirestart.atStartOfDay(ZoneId.systemDefault()).toInstant() ) );
				
				panelbkwkzhanbi.resetDate();
	    		panelgeguwkzhanbi.resetDate();
	    		pnllastestggzhanbi.resetDate();
	    		panelLastWkGeGuZhanBi.resetDate();
	    		((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).deleteAllRows();
	    		initializeBanKuaiZhanBiByGrowthRate ();
	    		
	    		lastselecteddate = startday;

			}
		});
		
	
		editorPanebankuai.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				String selbk = editorPanebankuai.getSelectedBanKuai(); 
				String selbkcode = selbk.trim().substring(1, 7);
				tflddwbk.setText(selbkcode);
				int rowindex = ((BanKuaiFengXiZhanBiPaiMingTableModel)tableBkZhanBi.getModel()).getBanKuaiRowIndex(selbkcode);
				if(rowindex != -1) {
					tableBkZhanBi.setRowSelectionInterval(rowindex, rowindex);
					tableBkZhanBi.scrollRectToVisible(new Rectangle(tableBkZhanBi.getCellRect(rowindex, 0, true)));
				}
				
				BanKuai selectedbk = ((BanKuaiFengXiZhanBiPaiMingTableModel)tableBkZhanBi.getModel()).getBanKuai(rowindex);
				refreshCurentBanKuaiFengXiResult (selectedbk);
				
			}
		});
		
		
		
		tflddwbk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				int rowindex = ((BanKuaiFengXiZhanBiPaiMingTableModel)tableBkZhanBi.getModel()).getBanKuaiRowIndex(tflddwbk.getText().trim());
				if(rowindex != -1) {
					tableBkZhanBi.setRowSelectionInterval(rowindex, rowindex);
					tableBkZhanBi.scrollRectToVisible(new Rectangle(tableBkZhanBi.getCellRect(rowindex, 0, true)));
				}
				
				BanKuai selectedbk = ((BanKuaiFengXiZhanBiPaiMingTableModel)tableBkZhanBi.getModel()).getBanKuai(rowindex);
				refreshCurentBanKuaiFengXiResult (selectedbk);
				
				
			}
		});
		tflddingweigegu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//
				int rowindex = ((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel() ).getStockRowIndex(tflddingweigegu.getText().trim());
				int modelRow = tableGuGuZhanBiInBk.convertRowIndexToView(rowindex);
				if(rowindex != -1) {
					tableGuGuZhanBiInBk.setRowSelectionInterval(modelRow, modelRow);
					tableGuGuZhanBiInBk.scrollRectToVisible(new Rectangle(tableGuGuZhanBiInBk.getCellRect(modelRow, 0, true)));
				}
				
				rowindex = ((BanKuaiGeGuTableModel)tablexuandingzhou.getModel() ).getStockRowIndex(tflddingweigegu.getText().trim());
				modelRow = tablexuandingzhou.convertRowIndexToView(rowindex);
				if(rowindex != -1) {
					tablexuandingzhou.setRowSelectionInterval(modelRow, modelRow);
					tablexuandingzhou.scrollRectToVisible(new Rectangle(tablexuandingzhou.getCellRect(modelRow, 0, true)));
				}
				
				rowindex = ((BanKuaiGeGuTableModel)tablexuandingminusone.getModel() ).getStockRowIndex(tflddingweigegu.getText().trim());
				modelRow = tablexuandingminusone.convertRowIndexToView(rowindex);
				if(rowindex != -1) {
					tablexuandingminusone.setRowSelectionInterval(modelRow, modelRow);
					tablexuandingminusone.scrollRectToVisible(new Rectangle(tablexuandingminusone.getCellRect(modelRow, 0, true)));
				}
				
				rowindex = ((BanKuaiGeGuTableModel)tablexuandingminustwo.getModel() ).getStockRowIndex(tflddingweigegu.getText().trim());
				modelRow = tablexuandingminustwo.convertRowIndexToView(rowindex);
				if(rowindex != -1) {
					tablexuandingminustwo.setRowSelectionInterval(modelRow, modelRow);
					tablexuandingminustwo.scrollRectToVisible(new Rectangle(tablexuandingminustwo.getCellRect(modelRow, 0, true)));
				}
				
				rowindex = ((BanKuaiGeGuTableModel)tablexuandingplusone.getModel() ).getStockRowIndex(tflddingweigegu.getText().trim());
				modelRow = tablexuandingplusone.convertRowIndexToView(rowindex);
				if(rowindex != -1) {
					tablexuandingplusone.setRowSelectionInterval(modelRow, modelRow);
					tablexuandingplusone.scrollRectToVisible(new Rectangle(tablexuandingplusone.getCellRect(modelRow, 0, true)));
				}
				
				rowindex = ((BanKuaiGeGuTableModel)tablexuandingplustwo.getModel() ).getStockRowIndex(tflddingweigegu.getText().trim());
				modelRow = tablexuandingplustwo.convertRowIndexToView(rowindex);
				if(rowindex != -1) {
					tablexuandingplustwo.setRowSelectionInterval(modelRow, modelRow);
					tablexuandingplustwo.scrollRectToVisible(new Rectangle(tablexuandingplustwo.getCellRect(modelRow, 0, true)));
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
				Stock selectstock = ((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).getStock (modelRow);

				if (arg0.getClickCount() == 1) {
					hightlightSpecificSector (selectstock);
					refreshGeGuFengXiResult (selectstock);
				}
				if (arg0.getClickCount() == 2) {
					 selectstock = (Stock)cbxstockcode.updateUserSelectedNode(selectstock);
					 displayStockSuoShuBanKuai (selectstock);
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
				Stock selectstock = ((BanKuaiGeGuTableModel)tablexuandingzhou.getModel()).getStock (modelRow);
	
				if (arg0.getClickCount() == 1) {
					hightlightSpecificSector (selectstock);
					refreshGeGuFengXiResult (selectstock);
				}
				if (arg0.getClickCount() == 2) {
					 selectstock = (Stock)cbxstockcode.updateUserSelectedNode(selectstock);
					 displayStockSuoShuBanKuai (selectstock);
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
				Stock selectstock = ((BanKuaiGeGuTableModel)tablexuandingminustwo.getModel()).getStock (modelRow);
	
				if (arg0.getClickCount() == 1) {
					hightlightSpecificSector (selectstock);
					refreshGeGuFengXiResult (selectstock);
				}
				 if (arg0.getClickCount() == 2) {
					 selectstock = (Stock)cbxstockcode.updateUserSelectedNode(selectstock);
					 displayStockSuoShuBanKuai (selectstock);
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
				Stock selectstock = ((BanKuaiGeGuTableModel)tablexuandingminusone.getModel()).getStock (modelRow);
	
				if (arg0.getClickCount() == 1) {
					hightlightSpecificSector (selectstock);
					 refreshGeGuFengXiResult (selectstock);
				}
				 if (arg0.getClickCount() == 2) {
					 selectstock = (Stock)cbxstockcode.updateUserSelectedNode(selectstock);
					 displayStockSuoShuBanKuai (selectstock);
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
				Stock selectstock = ((BanKuaiGeGuTableModel)tablexuandingplusone.getModel()).getStock (modelRow);
	
				if (arg0.getClickCount() == 1) {
					hightlightSpecificSector (selectstock);
					refreshGeGuFengXiResult (selectstock);
				}
				 if (arg0.getClickCount() == 2) {
					 selectstock = (Stock)cbxstockcode.updateUserSelectedNode(selectstock);
					 displayStockSuoShuBanKuai (selectstock);
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
				Stock selectstock = ((BanKuaiGeGuTableModel)tablexuandingplustwo.getModel()).getStock (modelRow);
	
				if (arg0.getClickCount() == 1) {
					hightlightSpecificSector (selectstock);
					refreshGeGuFengXiResult (selectstock);
				}
				 if (arg0.getClickCount() == 2) {
					 selectstock = (Stock)cbxstockcode.updateUserSelectedNode(selectstock);
					 displayStockSuoShuBanKuai (selectstock);
				 }
				
				 
			}
		});
		
		
		btnexportbk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
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
				
				
				BanKuai selectedbk = ((BanKuaiFengXiZhanBiPaiMingTableModel)tableBkZhanBi.getModel()).getBanKuai(row);
				refreshCurentBanKuaiFengXiResult (selectedbk);
			}
		});
		
		dateChooser.addPropertyChangeListener(new PropertyChangeListener() {
		    @Override
		    public void propertyChange(PropertyChangeEvent e) {
		    	if("date".equals(e.getPropertyName() ) ) {
		    		
		    		JDateChooser wybieraczDat = (JDateChooser) e.getSource();
		    		LocalDate newdate = wybieraczDat.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		    		
		    		if( newdate.isEqual(LocalDate.now() ) )
						btnresetdate.setEnabled(false);
		    		else
		    			btnresetdate.setEnabled(true);
		    		
		    		if( (lastselecteddate == null) || ( !newdate.isEqual( lastselecteddate) ) ) {
		    			panelbkcje.resetDate();
		    			panelgegucje.resetDate ();
		    			panelbkwkzhanbi.resetDate();
			    		panelgeguwkzhanbi.resetDate();
			    		pnllastestggzhanbi.resetDate();
			    		panelLastWkGeGuZhanBi.resetDate();
			    		panelselectwkgeguzhanbi.resetDate();
			    		((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).deleteAllRows();
			    		tabbedPane.setTitleAt(0, "当前周");
			    		tabbedPane.setTitleAt(1, "选定周");
			    		
			    		initializeBanKuaiZhanBiByGrowthRate ();
			    		
			    		lastselecteddate = newdate;
		    		}
		    	}
//		        System.out.println(e.getPropertyName()+ ": " + e.getNewValue());
		    }
		});
		
		btnresetdate.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
					LocalDate newdate =  LocalDate.now();
					dateChooser.setDate(Date.from(newdate.atStartOfDay(ZoneId.systemDefault()).toInstant() ) );
					
					if( (lastselecteddate == null) || ( newdate.isEqual( lastselecteddate) ) ) {
		    			panelbkwkzhanbi.resetDate();
			    		panelgeguwkzhanbi.resetDate();
			    		pnllastestggzhanbi.resetDate();
			    		panelLastWkGeGuZhanBi.resetDate();
			    		((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).deleteAllRows();
			    		initializeBanKuaiZhanBiByGrowthRate ();
			    		
			    		lastselecteddate = newdate;
			    		
//			    		btnresetdate.setEnabled(false);
		    		}
					
				}
			
		});
		
	}
	


	
	protected void hightlightSpecificSector(Stock selectstock) 
	{
		String stockcode = selectstock.getMyOwnCode(); 
		 try {
			 String stockname = selectstock.getMyOwnName().trim(); 
			 pnllastestggzhanbi.hightlightSpecificSector (stockcode+stockname);
			 panelLastWkGeGuZhanBi.hightlightSpecificSector (stockcode+stockname);
			 panelselectwkgeguzhanbi.hightlightSpecificSector (stockcode+stockname);
		 } catch ( java.lang.NullPointerException e) {
			 pnllastestggzhanbi.hightlightSpecificSector (stockcode);
			 panelLastWkGeGuZhanBi.hightlightSpecificSector (stockcode);
			 panelselectwkgeguzhanbi.hightlightSpecificSector (stockcode);
		 }
		
	}

	private final JPanel contentPanel = new JPanel();
	private JButton okButton;
	private JButton cancelButton;
	private JDateChooser dateChooser;
	private JScrollPane sclpleft;
	private JTable tableBkZhanBi;
//	private JTable tableGuGuZhanBiInBk;
	private BanKuaiGeGuTable tableGuGuZhanBiInBk;
	private BanKuaiFengXiBarChartPnl panelbkwkzhanbi;
	private JButton btnexportbk;
	private JTextField tfldweight;
	private BanKuaiFengXiBarChartPnl panelgeguwkzhanbi;
	private BanKuaiFengXiPieChartPnl pnllastestggzhanbi;
	private BanKuaiFengXiPieChartPnl panelLastWkGeGuZhanBi;
	private JTextField tflddwbk;
	private JTextField tflddingweigegu;
	private BanKuaiFengXiPieChartPnl panelselectwkgeguzhanbi;
	private BanKuaiListEditorPane editorPanebankuai;
	private JButton btnresetdate;
	private JButton btnsixmonthbefore;
	private JButton btnsixmonthafter;
	private BanKuaiGeGuTable tablexuandingzhou;
	private JStockComboBox cbxstockcode;
	private BanKuaiFengXiBarChartPnl panelbkcje;
	private BanKuaiFengXiBarChartPnl panelgegucje;
	private BanKuaiGeGuTable tablexuandingminustwo; //new BanKuaiGeGuTable (this.stockmanager);
	private BanKuaiGeGuTable tablexuandingminusone;
	private BanKuaiGeGuTable tablexuandingplusone;
	private BanKuaiGeGuTable tablexuandingplustwo;
	private JTabbedPane tabbedPane;
	
	private void initializeGui() {
		setTitle("\u677F\u5757\u5206\u6790");
		setBounds(100, 100, 1912, 1002);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		
		JPanel panel_1 = new JPanel();
		
		panelbkwkzhanbi = new BanKuaiFengXiBarChartPnl();
		
		panelbkwkzhanbi.setBorder(new TitledBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)), "\u677F\u5757\u534A\u5E74\u5185\u5468\u5360\u6BD4", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		
		pnllastestggzhanbi = new BanKuaiFengXiPieChartPnl();
		pnllastestggzhanbi.setBorder(new TitledBorder(null, "\u677F\u5757\u5F53\u524D\u5468\u4E2A\u80A1\u5360\u6BD4", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		panelgeguwkzhanbi = new BanKuaiFengXiBarChartPnl();
		panelgeguwkzhanbi.setBorder(new TitledBorder(null, "\u4E2A\u80A1\u534A\u5E74\u5185\u5468\u5360\u6BD4", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		panelLastWkGeGuZhanBi = new BanKuaiFengXiPieChartPnl();
		panelLastWkGeGuZhanBi.setBorder(new TitledBorder(null, "\u677F\u5757\u4E0A\u4E00\u5468\u4E2A\u80A1\u5360\u6BD4", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		panelselectwkgeguzhanbi = new BanKuaiFengXiPieChartPnl();
		panelselectwkgeguzhanbi.setBorder(new TitledBorder(null, "\u9009\u5B9A\u5468\u5360\u6BD4", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		panelbkcje = new BanKuaiFengXiBarChartPnl();
		panelbkcje.setBorder(new TitledBorder(null, "\u677F\u5757\u6210\u4EA4\u989D", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		panelgegucje = new BanKuaiFengXiBarChartPnl();
		panelgegucje.setBorder(new TitledBorder(null, "\u4E2A\u80A1\u6210\u4EA4\u989D", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 331, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(panelbkwkzhanbi, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 581, GroupLayout.PREFERRED_SIZE)
								.addComponent(panelbkcje, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(panelgeguwkzhanbi, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(panelgegucje, GroupLayout.PREFERRED_SIZE, 562, GroupLayout.PREFERRED_SIZE)))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(panel, GroupLayout.PREFERRED_SIZE, 1469, GroupLayout.PREFERRED_SIZE)
							.addGap(23)))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(pnllastestggzhanbi, GroupLayout.PREFERRED_SIZE, 372, Short.MAX_VALUE)
						.addComponent(panelLastWkGeGuZhanBi, 0, 0, Short.MAX_VALUE)
						.addComponent(panelselectwkgeguzhanbi, GroupLayout.PREFERRED_SIZE, 372, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(pnllastestggzhanbi, GroupLayout.PREFERRED_SIZE, 311, GroupLayout.PREFERRED_SIZE)
							.addContainerGap())
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(panel, GroupLayout.PREFERRED_SIZE, 54, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
										.addComponent(panelbkcje, GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
										.addComponent(panelgegucje, GroupLayout.PREFERRED_SIZE, 420, GroupLayout.PREFERRED_SIZE))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl_contentPanel.createParallelGroup(Alignment.BASELINE)
										.addComponent(panelbkwkzhanbi, GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE)
										.addComponent(panelgeguwkzhanbi, GroupLayout.PREFERRED_SIZE, 424, GroupLayout.PREFERRED_SIZE))
									.addContainerGap())
								.addComponent(panel_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 860, Short.MAX_VALUE)
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addGap(261)
									.addComponent(panelLastWkGeGuZhanBi, GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(panelselectwkgeguzhanbi, GroupLayout.PREFERRED_SIZE, 281, GroupLayout.PREFERRED_SIZE)
									.addGap(12))))))
		);
		
		BanKuaiFengXiZhanBiPaiMingTableModel tablelast2 = new BanKuaiFengXiZhanBiPaiMingTableModel ();
		
		BanKuaiFengXiZhanBiPaiMingTableModel tablelast2gr = new BanKuaiFengXiZhanBiPaiMingTableModel ();
		
		BanKuaiFengXiZhanBiPaiMingTableModel tablelast = new BanKuaiFengXiZhanBiPaiMingTableModel ();
		
		BanKuaiFengXiZhanBiPaiMingTableModel tablelastgr = new BanKuaiFengXiZhanBiPaiMingTableModel ();
		
		BanKuaiFengXiZhanBiPaiMingTableModel tablecurwk = new BanKuaiFengXiZhanBiPaiMingTableModel ();
		
		BanKuaiFengXiZhanBiPaiMingTableModel tablecurwkgr = new BanKuaiFengXiZhanBiPaiMingTableModel ();
		
		sclpleft = new JScrollPane();
		
		btnexportbk = new JButton("\u5BFC\u51FA\u677F\u5757");
		
		JButton button = new JButton("\u5BFC\u51FA\u4E2A\u80A1");
		
		JCheckBox chckbxNewCheckBox = new JCheckBox("\u5254\u9664\u80A1\u7968\u6743\u91CD<=");
		
		tfldweight = new JTextField();
		tfldweight.setText("0");
		tfldweight.setColumns(10);
		
		tflddwbk = new JTextField();
		
		tflddwbk.setColumns(10);
		
		tflddingweigegu = new JTextField();
		
		tflddingweigegu.setColumns(10);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)
						.addGroup(Alignment.TRAILING, gl_panel_1.createSequentialGroup()
							.addComponent(chckbxNewCheckBox)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(tfldweight, GroupLayout.PREFERRED_SIZE, 54, GroupLayout.PREFERRED_SIZE))
						.addComponent(sclpleft, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
						.addGroup(Alignment.TRAILING, gl_panel_1.createSequentialGroup()
							.addComponent(tflddwbk, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, 63, Short.MAX_VALUE)
							.addComponent(btnexportbk))
						.addGroup(Alignment.TRAILING, gl_panel_1.createSequentialGroup()
							.addComponent(tflddingweigegu, GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, 52, Short.MAX_VALUE)
							.addComponent(button)))
					.addContainerGap())
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addGap(6)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(tflddwbk, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnexportbk))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(sclpleft, GroupLayout.PREFERRED_SIZE, 374, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(chckbxNewCheckBox)
						.addComponent(tfldweight, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(button)
						.addComponent(tflddingweigegu, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		
		JScrollPane scrollPanedangqian = new JScrollPane();
		tabbedPane.addTab("当前周", null, scrollPanedangqian, null);
		tabbedPane.setBackgroundAt(0, Color.ORANGE);
		
		
		tableGuGuZhanBiInBk = new BanKuaiGeGuTable (this.stockmanager); 
//		GeGuFengXiZhanBiPaiMingTableModel ggzb = new GeGuFengXiZhanBiPaiMingTableModel ();
//		tableGuGuZhanBiInBk = new JTable(ggzb){
//			private static final long serialVersionUID = 1L;
//			
//			public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
//				 
//		        Component comp = super.prepareRenderer(renderer, row, col);
//		        GeGuFengXiZhanBiPaiMingTableModel tablemodel = (GeGuFengXiZhanBiPaiMingTableModel)this.getModel(); 
//		        HashSet<String> stockinparsefile = tablemodel.getStockInParseFile();
//		        Object value = tablemodel.getValueAt(row, col);
//		        
//		        if (!isRowSelected(row)) {
//		        	comp.setBackground(getBackground());
//		        	comp.setForeground(getForeground());
//		        	int modelRow = convertRowIndexToModel(row);
//		        	String stockcode = (String)getModel().getValueAt(modelRow, 0);
//					if(stockinparsefile.contains(stockcode)) {
//						//comp.setBackground(Color.YELLOW);
//						comp.setForeground(Color.BLUE);
//					}
//		        }
//		        
//		        return comp;
//			}
//			
//			
//			public String getToolTipText(MouseEvent e) 
//			{
//                String tip = null;
//                java.awt.Point p = e.getPoint();
//                int rowIndex = rowAtPoint(p);
//                int colIndex = columnAtPoint(p);
//
//                try {
//                    tip = getValueAt(rowIndex, colIndex).toString();
//                } catch(java.lang.NullPointerException e1) {
//                	tip = "";
//				}catch (RuntimeException e1) {
//                	e1.printStackTrace();
//                }
//                return tip;
//            } 
//		};
		
		scrollPanedangqian.setViewportView(tableGuGuZhanBiInBk);
		
		JScrollPane scrollPanexuanding = new JScrollPane();
		tabbedPane.addTab("选定周", null, scrollPanexuanding, null);
		tabbedPane.setBackgroundAt(1, UIManager.getColor("MenuItem.selectionBackground"));
		
		tablexuandingzhou = new BanKuaiGeGuTable (this.stockmanager);

		scrollPanexuanding.setViewportView(tablexuandingzhou);
		
		JScrollPane scrollPanexuandingminusone = new JScrollPane();
		tabbedPane.addTab("\u9009\u5B9A\u5468-1", null, scrollPanexuandingminusone, null);
		tabbedPane.setBackgroundAt(2, Color.LIGHT_GRAY);
		
		tablexuandingminusone = new BanKuaiGeGuTable (this.stockmanager);
		scrollPanexuandingminusone.setViewportView(tablexuandingminusone);
		
		JScrollPane scrollPanexuandingminustwo = new JScrollPane();
		tabbedPane.addTab("\u9009\u5B9A\u5468-2", null, scrollPanexuandingminustwo, null);
		
		tablexuandingminustwo = new BanKuaiGeGuTable (this.stockmanager);
		scrollPanexuandingminustwo.setViewportView(tablexuandingminustwo);
		
		JScrollPane scrollPanexuandingplusone = new JScrollPane();
		tabbedPane.addTab("\u9009\u5B9A\u5468+1", null, scrollPanexuandingplusone, null);
		
		tablexuandingplusone = new BanKuaiGeGuTable (this.stockmanager);
		scrollPanexuandingplusone.setViewportView(tablexuandingplusone);
		
		JScrollPane scrollPanexuandingplustwo = new JScrollPane();
		tabbedPane.addTab("\u9009\u5B9A\u5468+2", null, scrollPanexuandingplustwo, null);
		
		tablexuandingplustwo = new BanKuaiGeGuTable (this.stockmanager);
		scrollPanexuandingplustwo.setViewportView(tablexuandingplustwo);
		
//		tableBkZhanBi = new BanKuaiGeGuTable (this.stockmanager);
		
		BanKuaiFengXiZhanBiPaiMingTableModel bkzb = new BanKuaiFengXiZhanBiPaiMingTableModel ();
		tableBkZhanBi = new JTable(bkzb){
			private static final long serialVersionUID = 1L;
			
			public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
				 
		        Component comp = super.prepareRenderer(renderer, row, col);
		        BanKuaiFengXiZhanBiPaiMingTableModel tablemodel = (BanKuaiFengXiZhanBiPaiMingTableModel)this.getModel(); 
		        BanKuai bankuai = tablemodel.getBanKuai(row);
		        if(bankuai.getParseFileStockSet().size()>0)
		        	comp.setForeground(Color.BLUE);
		        else
		        	comp.setForeground(Color.black);
		        
		        return comp;
			}
			
//			public String getToolTipText(MouseEvent e) 
//			{
//                String tip = null;
//                java.awt.Point p = e.getPoint();
//                int rowIndex = rowAtPoint(p);
//                int colIndex = columnAtPoint(p);
//
//                try {
//                    tip = getValueAt(rowIndex, colIndex).toString();
//                } catch (RuntimeException e1) {
//                	e1.printStackTrace();
//                }
//                return tip;
//            } 
		};
		
		
		sclpleft.setViewportView(tableBkZhanBi);
		panel_1.setLayout(gl_panel_1);
		
		dateChooser = new JDateChooser();
		dateChooser.setDateFormatString("yyyy-MM-dd");
		
		JScrollPane scrollPane = new JScrollPane();
		
		btnresetdate = new JButton("\u91CD\u7F6E");
		btnresetdate.setEnabled(false);
		
		btnsixmonthbefore = new JButton("<");
		
		btnsixmonthbefore.setToolTipText("\u5411\u524D6\u4E2A\u6708");
		
		btnsixmonthafter = new JButton(">");
		btnsixmonthafter.setToolTipText("\u5411\u540E6\u4E2A\u6708");
		
		cbxstockcode = new JStockComboBox();
		
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(dateChooser, GroupLayout.PREFERRED_SIZE, 133, GroupLayout.PREFERRED_SIZE)
					.addGap(6)
					.addComponent(btnsixmonthbefore)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnsixmonthafter)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnresetdate)
					.addGap(27)
					.addComponent(cbxstockcode, GroupLayout.PREFERRED_SIZE, 182, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 937, GroupLayout.PREFERRED_SIZE)
					.addGap(653))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_panel.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
							.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnsixmonthbefore)
								.addComponent(btnsixmonthafter)
								.addComponent(btnresetdate, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
							.addComponent(dateChooser, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
						.addGap(34))
					.addGroup(gl_panel.createSequentialGroup()
						.addGap(7)
						.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
							.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
							.addComponent(cbxstockcode, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
						.addContainerGap(25, Short.MAX_VALUE)))
		);
		
		editorPanebankuai = new BanKuaiListEditorPane();
		
		scrollPane.setViewportView(editorPanebankuai);
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
			GroupLayout gl_buttonPane = new GroupLayout(buttonPane);
			gl_buttonPane.setHorizontalGroup(
				gl_buttonPane.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_buttonPane.createSequentialGroup()
						.addGap(1659)
						.addComponent(okButton, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE)
						.addGap(30)
						.addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
						.addContainerGap())
			);
			gl_buttonPane.setVerticalGroup(
				gl_buttonPane.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_buttonPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(cancelButton)
						.addComponent(okButton))
			);
			buttonPane.setLayout(gl_buttonPane);
		}
		

	}
}


class BanKuaiFengXiZhanBiPaiMingTableModel extends AbstractTableModel 
{
	String[] jtableTitleStrings = { "板块代码", "板块名称","占比增长率","max","成交额增长贡献率"};
//	HashMap<String,BanKuai> bkmap;
	List<Map.Entry<String, BanKuai>> entryList;
	LocalDate showzhbiwknum;
	
	BanKuaiFengXiZhanBiPaiMingTableModel ()
	{
	}

	public void refresh  (HashMap<String,BanKuai> curbkzslist, LocalDate curselectdate)
	{
		showzhbiwknum = curselectdate;
		entryList = new ArrayList<Map.Entry<String, BanKuai>>(curbkzslist.entrySet());

        Collections.sort(entryList, new Comparator<Map.Entry<String, BanKuai>>() {
            @Override
            public int compare(Map.Entry<String, BanKuai> bk1,
                               Map.Entry<String, BanKuai> bk2) {
            	
            	Double bk1zhanbi = bk1.getValue().getChenJiaoLiangZhanBiGrowthRateForAGivenPeriod (showzhbiwknum);
            	Double bk2zhanbi = bk2.getValue().getChenJiaoLiangZhanBiGrowthRateForAGivenPeriod (showzhbiwknum);
            	try {
            		return bk1zhanbi.compareTo(bk2zhanbi);
            	} catch (java.lang.NullPointerException e) {
            		if(bk2zhanbi == null && bk1zhanbi != null)
            			return bk1zhanbi.compareTo(-10000000.0);
            		else if(bk1zhanbi == null && bk2zhanbi != null)
            			return bk2zhanbi.compareTo(-10000000.0);
            		else 
            			return 0;
            	}
            }
            }
        );

        this.fireTableDataChanged();
	}
	
	 
	
	 public int getRowCount() 
	 {
//		 if(this.bkmap == null)
//			 return 0;
//		 else 
//			 return this.bkmap.size();
		 if(entryList == null)
			 return 0;
		 else
			 return entryList.size();
	 }

	    @Override
	    public int getColumnCount() 
	    {
	        return jtableTitleStrings.length;
	    } 
	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
//	    	if(bkmap.isEmpty())
//	    		return null;
//	    	
//	    	String[] bkcodeArray = bkmap.keySet().toArray(new String[bkmap.keySet().size()]);
//	    	String bkcode = bkcodeArray[rowIndex];
//	    	BanKuai thisbk = bkmap.get(bkcode);
//	    	String thisbkname = thisbk.getMyOwnName(); 
//	    	Double zhanbigrowthrate = thisbk.getChenJiaoLiangZhanBiGrowthRateForAGivenPeriod ();
//	    	NumberFormat percentFormat = NumberFormat.getPercentInstance();
//	    	
//	    	Object value = "??";
//	    	switch (columnIndex) {
//            case 0:
//                value = bkcode;
//                break;
//            case 1: 
//            	value = thisbkname;
//            	break;
//            case 2:
//            	value = percentFormat.format(zhanbigrowthrate);
//            	break;
//	    	}
	    	
	    	if(entryList.isEmpty() )
	    		return null;
	    	
	    	Entry<String, BanKuai> thisbk = entryList.get( entryList.size()-1-rowIndex );
	    	
	    	Object value = "??";
	    	switch (columnIndex) {
            case 0:
            	String bkcode = thisbk.getValue().getMyOwnCode();
                value = bkcode;
                break;
            case 1:
            	String thisbkname = thisbk.getValue().getMyOwnName();
            	value = thisbkname;
            	break;
            case 2:
            	Double zhanbigrowthrate = thisbk.getValue().getChenJiaoLiangZhanBiGrowthRateForAGivenPeriod (showzhbiwknum);
    	    	NumberFormat percentFormat = NumberFormat.getPercentInstance();
            	value = percentFormat.format(zhanbigrowthrate);
            	break;
            case 3:
            	int maxweek = thisbk.getValue().getChenJiaoLiangZhanBiMaxWeekForAGivenPeriod (showzhbiwknum);
            	value = maxweek;
            	break;
            case 4:
            	Double cjegrowthrate = thisbk.getValue().getChenJiaoErChangeGrowthRateForAGivenPeriod (showzhbiwknum);
    	    	NumberFormat percentFormat2 = NumberFormat.getPercentInstance();
            	value = percentFormat2.format(cjegrowthrate);
	    	}

	    	return value;
	  }

     public Class<?> getColumnClass(int columnIndex) {
		      Class clazz = String.class;
		      switch (columnIndex) {
		      case 0:
		    	  clazz = String.class;
		    	  break;
		        case 1:
			          clazz = String.class;
			          break;
		        case 2:
			          clazz = String.class;
			          break;
		        case 3:
			          clazz = Integer.class;
			          break;
		        case 4:
			          clazz = String.class;
			          break;
		      }
		      
		      return clazz;
	 }
	    
	    public String getColumnName(int column) { 
	    	return jtableTitleStrings[column];
	    }//设置表格列名 
		

	    public boolean isCellEditable(int row,int column) {
	    	return false;
		}
	    public String getBanKuaiCode (int row) 
	    {
	    	return (String)this.getValueAt(row,0);
	    }
	    public String getBanKuaiName (int row) 
	    {
	    	return (String)this.getValueAt(row,1);
	    } 
	    public BanKuai getBanKuai (int row)
	    {
	    	String bkcode = this.getBanKuaiCode(entryList.size()-1-row);
//	    	return bkmap.get(bkcode);
	    	return this.entryList.get(entryList.size()-1-row).getValue();
	    }

	    public int getBanKuaiRowIndex (String neededfindstring) 
	    {
	    		int index = -1;
	    		HanYuPinYing hypy = new HanYuPinYing ();
	    		
	    		for(int i=0;i<this.getRowCount();i++) {
	    			String bkcode = (String)this.getValueAt(i, 0);
	    			String bkname = (String)this.getValueAt(i,1); 
	    			if(bkcode.trim().equals(neededfindstring) ) {
	    				index = i;
	    				break;
	    			}

	    			String namehypy = hypy.getBanKuaiNameOfPinYin(bkname );
			   		if(namehypy.toLowerCase().equals(neededfindstring.trim().toLowerCase())) {
			   			index = i;
			   			break;
			   		}
	    		}
	    	
	   		
	   		return index;
	    }
	    
}

