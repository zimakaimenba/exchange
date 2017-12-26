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
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiInfoTable;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiInfoTableModel;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.DisplayBkGgInfoEditorPane;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.gui.subgui.BanKuaiListEditorPane;
import com.exchangeinfomanager.gui.subgui.JStockComboBox;
import com.exchangeinfomanager.gui.subgui.JiaRuJiHua;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
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
import javax.swing.SwingConstants;

public class BanKuaiFengXi extends JDialog {

	/**
	 * Create the dialog.
	 */
	public BanKuaiFengXi(StockInfoManager stockmanager1, BanKuaiAndChanYeLian bkcyl2)
	{
		this.stockmanager = stockmanager1;
		this.bkcyl = bkcyl2;
		initializeGui ();
		this.sysconfig = SystemConfigration.getInstance();

		createEvents ();
		dateChooser.setDate(new Date ());
	}
	
	private LocalDate lastselecteddate;
	private BanKuaiAndChanYeLian bkcyl;
	private SystemConfigration sysconfig;
	private StockInfoManager stockmanager;
	private HashSet<String> stockinfile;
	
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

			childnode = this.bkcyl.getBanKuai(childnode, curselectdate); 
    		if(childnode.getChenJiaoErZhanBiInGivenPeriod() != null 
    				&& !childnode.getChenJiaoErZhanBiInGivenPeriod().isEmpty()  //有些指数是没有个股和成交量的，不列入比较范围
    				&& childnode.getSpecficChenJiaoErRecord(curselectdate) != null) //板块当周没有数据也不考虑，板块一般不可能没有数据，没有数据说明该板块这周还没有诞生，或者过去有，现在成交量已经不存入数据库
    			bkhascjl.put(bkcode, childnode);
    		
    		//显示大盘成交量
    		if(bkcode.equals("399001") ) {
    			ChenJiaoZhanBiInGivenPeriod cjerec = childnode.getSpecficChenJiaoErRecord(curselectdate);
    			try {
    				String szcje = cjerec.getMyOwnChengJiaoEr().toString();
    				lblszcje.setText( szcje );
    			} catch (java.lang.NullPointerException e) {
    				lblszcje.setText( "本周没有成交额" );
    			}
    			
    		} else if(bkcode.equals("999999") ) {
    			ChenJiaoZhanBiInGivenPeriod cjerec = childnode.getSpecficChenJiaoErRecord(curselectdate);
    			try {
    				lblshcje.setText( cjerec.getMyOwnChengJiaoEr().toString() );
    			} catch (java.lang.NullPointerException e) {
    				lblshcje.setText( "本周没有成交额" );
    			}
    		} 
    			
		}

    	((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).refresh(bkhascjl,curselectdate);
	}
	/*
	 * 显示板块的占比和个股
	 */
	protected void refreshCurentBanKuaiFengXiResult(BanKuai selectedbk) 
	{
		tfldselectedmsg.setText("");
		tabbedPane.setSelectedIndex(0);
		panelbkcje.resetDate();
		panelbkwkzhanbi.resetDate();
		pnllastestggzhanbi.resetDate();
		panelbkwkzhanbi.resetDate();
		panelgeguwkzhanbi.resetDate();
		panelselectwkgeguzhanbi.resetDate();
		panelLastWkGeGuZhanBi.resetDate();
		panelGeguDapanZhanBi.resetDate();
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
    	panelbkcje.setNodeJiaoYiErByWeek((BkChanYeLianTreeNode)selectedbk,curselectdate,dapan); //交易额
		panelbkwkzhanbi.setNodeZhanBiByWeek((BkChanYeLianTreeNode)selectedbk,curselectdate,dapan); //占比
		
		panelbkwkzhanbi.setDaZiJinValueMarker (0.01); //大于1%是板块强势的表现
		
		//更新板块所属个股
		selectedbk = bkcyl.getAllGeGuOfBanKuai (selectedbk);
		((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).refresh(selectedbk, curselectdate);
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
		((BanKuaiGeGuTableModel)tablexuandingzhou.getModel()).refresh(selectedbk, selecteddate);
		tabbedPane.setTitleAt(1, "选定周" + selecteddate);
		
		//显示选定周-1股票排名情况
		LocalDate selectdate2 = selecteddate.minus(1,ChronoUnit.WEEKS).with(DayOfWeek.MONDAY);
		if( checkDateBetweenBanKuaiDate (selectedbk,selectdate2) ) //板块要有该周数据
			((BanKuaiGeGuTableModel)tablexuandingminusone.getModel()).refresh(selectedbk, selectdate2);
		else
			((BanKuaiGeGuTableModel)tablexuandingminusone.getModel()).deleteAllRows();
		
		//显示选定周-2股票排名情况
		LocalDate selectdate3 = selecteddate.minus(2,ChronoUnit.WEEKS).with(DayOfWeek.MONDAY);
		if( checkDateBetweenBanKuaiDate (selectedbk,selectdate3) )
			((BanKuaiGeGuTableModel)tablexuandingminustwo.getModel()).refresh(selectedbk, selectdate3);
		else 
			((BanKuaiGeGuTableModel)tablexuandingminustwo.getModel()).deleteAllRows();
		
		//显示选定周+1股票排名情况
		LocalDate selectdate4 = selecteddate.plus(1,ChronoUnit.WEEKS).with(DayOfWeek.MONDAY);
		if( checkDateBetweenBanKuaiDate (selectedbk,selectdate4) )
			((BanKuaiGeGuTableModel)tablexuandingplusone.getModel()).refresh(selectedbk, selectdate4);
		else
			((BanKuaiGeGuTableModel)tablexuandingplusone.getModel()).deleteAllRows();
		
		//显示选定周+2股票排名情况
		LocalDate selectdate5 = selecteddate.plus(2,ChronoUnit.WEEKS).with(DayOfWeek.MONDAY);
		if( checkDateBetweenBanKuaiDate (selectedbk,selectdate5) )
			((BanKuaiGeGuTableModel)tablexuandingplustwo.getModel()).refresh(selectedbk, selectdate5);
		else
			((BanKuaiGeGuTableModel)tablexuandingplustwo.getModel()).deleteAllRows();
	}
	/*
	 * 
	 */
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
	 * 显示个股在板块和大盘的占比的chart
	 */
	private void refreshGeGuFengXiResult (Stock stock)
	{
			LocalDate curselectdate = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			DaPan dapan =  (DaPan) bkcyl.getBkChanYeLianTree().getSpecificNodeByHypyOrCode("000000");
			
			panelgeguwkzhanbi.resetDate();
			panelGeguDapanZhanBi.resetDate();
			panelgegucje.resetDate();
			tabbedPanegeguzhanbi.setSelectedIndex(0);
			
			panelgeguwkzhanbi.setNodeZhanBiByWeek(stock,curselectdate,dapan); //个股板块占比
			panelGeguDapanZhanBi.setNodeAndDaPanZhanBiByWeek(stock,curselectdate,dapan); //个股大盘占比
			panelgegucje.setNodeJiaoYiErByWeek(stock,curselectdate,dapan); //个股对于板块交易额
			
			Comparable datekey = panelbkwkzhanbi.getCurSelectedBarDate ();
			if(datekey != null) {
				panelgegucje.highLightSpecificBarColumn (datekey);
				panelgeguwkzhanbi.highLightSpecificBarColumn (datekey);
				panelGeguDapanZhanBi.highLightSpecificBarColumn (datekey);
			}
	}

	
//	public void setBanKuaiEndiorPaneContents (String fenxibkxml)
//	{
//		editorPanebankuai.setText(fenxibkxml);
//	}
//	public void setFenXiDate (Date fxdate)
//	{
//		dateChooser.setDate(fxdate);
//	}

	
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
	/*
	 * 
	 */
	public void setParsedFile(String file) 
	{
		this.tfldparsedfile.setText(file );
		BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)this.bkcyl.getBkChanYeLianTree().getModel().getRoot();
		stockinfile = treeroot.getParseFileStockSet(); 
	}

	private void createEvents() 
	{
		chckbxmaxwk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if( Strings.isNullOrEmpty(tflddisplaymaxwk.getText() )) {
					JOptionPane.showMessageDialog(null,"请设置突出显示MaxWk！");
					chckbxmaxwk.setSelected(false);
					return;
				}
				
				if(chckbxmaxwk.isSelected() ) {
					Integer maxwk = Integer.parseInt(tflddisplaymaxwk.getText() );
					((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).setDisplayBkMaxWk(maxwk);
					tableGuGuZhanBiInBk.repaint();
				}
				
				if(!chckbxmaxwk.isSelected() ) {
					((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).setDisplayBkMaxWk(100000000);
					tableGuGuZhanBiInBk.repaint();
				}
				
			}
		});
		
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
		
		ckboxshowcje.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				if( Strings.isNullOrEmpty(tfldshowcje.getText() )) {
					JOptionPane.showMessageDialog(null,"请设置突出显示的成交额！");
					ckboxshowcje.setSelected(false);
					return;
				}
					
				if(ckboxshowcje.isSelected()) {
					Double showcje = Double.parseDouble( tfldshowcje.getText() ) * 100000000;
					((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).setDisplayChenJiaoEr(showcje);


					((BanKuaiGeGuTableModel)tablexuandingzhou.getModel()).setDisplayChenJiaoEr(showcje);
					tablexuandingzhou.repaint();
					
					((BanKuaiGeGuTableModel)tablexuandingminusone.getModel()).setDisplayChenJiaoEr(showcje);
					tablexuandingminusone.repaint();
					
					((BanKuaiGeGuTableModel)tablexuandingminustwo.getModel()).setDisplayChenJiaoEr(showcje);
					tablexuandingminustwo.repaint();
					
					((BanKuaiGeGuTableModel)tablexuandingplusone.getModel()).setDisplayChenJiaoEr(showcje);
					tablexuandingplusone.repaint();
					
					((BanKuaiGeGuTableModel)tablexuandingplustwo.getModel()).setDisplayChenJiaoEr(showcje);
					tablexuandingplustwo.repaint();
				}
				
				if(!ckboxshowcje.isSelected()) {
					((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).setDisplayChenJiaoEr(-1.0);
					tableGuGuZhanBiInBk.repaint();
					
					((BanKuaiGeGuTableModel)tablexuandingzhou.getModel()).setDisplayChenJiaoEr(-1.0);
					tablexuandingzhou.repaint();
					
					((BanKuaiGeGuTableModel)tablexuandingminusone.getModel()).setDisplayChenJiaoEr(-1.0);
					tablexuandingminusone.repaint();
					
					((BanKuaiGeGuTableModel)tablexuandingminustwo.getModel()).setDisplayChenJiaoEr(-1.0);
					tablexuandingminustwo.repaint();
					
					((BanKuaiGeGuTableModel)tablexuandingplusone.getModel()).setDisplayChenJiaoEr(-1.0);
					tablexuandingplusone.repaint();
					
					((BanKuaiGeGuTableModel)tablexuandingplustwo.getModel()).setDisplayChenJiaoEr(-1.0);
					tablexuandingplustwo.repaint();

				}
				
			}
		});
		/*
		 * 查找pie chart内点击的那个个股 
		 */
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
		panelbkwkzhanbi.getChartPanel().addMouseListener(new MouseAdapter() { 
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				//显示选择的tooltip
				String tooltip = panelbkwkzhanbi.getToolTipSelected ();
//				tfldselectedmsg.setText(tooltip);
				
				//显示选择周的分析记录等
				ArrayList<JiaRuJiHua> fxjg = panelbkwkzhanbi.getCurSelectedFengXiJieGuo ();
				String allstring = tooltip + "\n";
				if(fxjg !=null) {
					for(JiaRuJiHua jrjh : fxjg) {
						LocalDate actiondate = jrjh.getJiaRuDate();
						String actiontype = jrjh.getGuanZhuType();
						String shuoming = jrjh.getJiHuaShuoMing();
						
						allstring = allstring +  "[" + actiondate.toString() + actiontype +  " " + shuoming + "]" + "\n";
					}
				}
				tfldselectedmsg.setText( "" );
				tfldselectedmsg.setText( allstring );
				
				//显示选择周的成交额占比
				String selectdate = panelbkwkzhanbi.getCurSelectedBarDate().toString();
				LocalDate selectdate1 = CommonUtility.formateStringToDate(selectdate);
				BanKuai bkcur = (BanKuai)panelbkwkzhanbi.getCurDisplayedNode ();
				
				panelselectwkgeguzhanbi.setBanKuaiNeededDisplay(bkcur,Integer.parseInt(tfldweight.getText() ), selectdate1  );

				//显示选中周股票占比增加率排名等
				refreshSpecificBanKuaiFengXiResult (bkcur,selectdate1);
				
				//同步几个panel
				Comparable datekey = panelbkwkzhanbi.getCurSelectedBarDate ();
				panelGeguDapanZhanBi.highLightSpecificBarColumn (datekey);
				panelgegucje.highLightSpecificBarColumn (datekey);
				panelgeguwkzhanbi.highLightSpecificBarColumn (datekey);
				panelbkcje.highLightSpecificBarColumn (datekey);
			}
		});
		
		//同步几个panel
		panelGeguDapanZhanBi.getChartPanel().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				//显示选择的tooltip
				String tooltip = panelGeguDapanZhanBi.getToolTipSelected ();
//				tfldselectedmsg.setText(tooltip);
				
				//显示选择周的分析记录等
				ArrayList<JiaRuJiHua> fxjg = panelGeguDapanZhanBi.getCurSelectedFengXiJieGuo ();
				String allstring = tooltip + "\n";
				if(fxjg !=null) {
					for(JiaRuJiHua jrjh : fxjg) {
						LocalDate actiondate = jrjh.getJiaRuDate();
						String actiontype = jrjh.getGuanZhuType();
						String shuoming = jrjh.getJiHuaShuoMing();
						
						allstring = allstring +  "[" + actiondate.toString() + actiontype +  " " + shuoming + "]" + "\n";
					}
				}
				tfldselectedmsg.setText( "" );
				tfldselectedmsg.setText( allstring );
				//
				Comparable datekey = panelGeguDapanZhanBi.getCurSelectedBarDate ();
				panelbkwkzhanbi.highLightSpecificBarColumn (datekey);
				panelgegucje.highLightSpecificBarColumn (datekey);
				panelgeguwkzhanbi.highLightSpecificBarColumn (datekey);
				panelbkcje.highLightSpecificBarColumn (datekey);
			}
		});
		panelbkcje.getChartPanel().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				//显示选择的tooltip
				String tooltip = panelbkcje.getToolTipSelected ();
//				tfldselectedmsg.setText(tooltip);
				//显示选择周的分析记录等
				ArrayList<JiaRuJiHua> fxjg = panelbkcje.getCurSelectedFengXiJieGuo ();
				String allstring = tooltip + "\n";
				if(fxjg !=null) {
					for(JiaRuJiHua jrjh : fxjg) {
						LocalDate actiondate = jrjh.getJiaRuDate();
						String actiontype = jrjh.getGuanZhuType();
						String shuoming = jrjh.getJiHuaShuoMing();
						
						allstring = allstring +  "[" + actiondate.toString() + actiontype +  " " + shuoming + "]" + "\n";
					}
				}
				tfldselectedmsg.setText( "" );
				tfldselectedmsg.setText( allstring );
				
				//
				Comparable datekey = panelbkcje.getCurSelectedBarDate ();
				panelbkwkzhanbi.highLightSpecificBarColumn (datekey);
				panelgegucje.highLightSpecificBarColumn (datekey);
				panelgeguwkzhanbi.highLightSpecificBarColumn (datekey);
				panelGeguDapanZhanBi.highLightSpecificBarColumn (datekey);
//				////显示选择周的分析记录
//				String selectdate = datekey.toString();
//				LocalDate selectdate1 = CommonUtility.formateStringToDate(selectdate);
				
			}
		});

		panelgegucje.getChartPanel().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				//显示选择的tooltip
				String tooltip = panelgegucje.getToolTipSelected ();
//				tfldselectedmsg.setText(tooltip);
				//显示选择周的分析记录等
				ArrayList<JiaRuJiHua> fxjg = panelgegucje.getCurSelectedFengXiJieGuo ();
				String allstring = tooltip + "\n";
				if(fxjg !=null) {
					for(JiaRuJiHua jrjh : fxjg) {
						LocalDate actiondate = jrjh.getJiaRuDate();
						String actiontype = jrjh.getGuanZhuType();
						String shuoming = jrjh.getJiHuaShuoMing();
						
						allstring = allstring +  "[" + actiondate.toString() + actiontype +  " " + shuoming + "]" + "\n";
					}
				}
				tfldselectedmsg.setText( "" );
				tfldselectedmsg.setText( allstring );
				//
				Comparable datekey = panelgegucje.getCurSelectedBarDate ();
				panelbkcje.highLightSpecificBarColumn (datekey);
				panelbkwkzhanbi.highLightSpecificBarColumn (datekey);
				panelgeguwkzhanbi.highLightSpecificBarColumn (datekey);
				panelGeguDapanZhanBi.highLightSpecificBarColumn (datekey);
			}
		});
		panelgeguwkzhanbi.getChartPanel().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				//显示选择的tooltip
				String tooltip = panelgeguwkzhanbi.getToolTipSelected ();
//				tfldselectedmsg.setText(tooltip);
				//显示选择周的分析记录等
				ArrayList<JiaRuJiHua> fxjg = panelgeguwkzhanbi.getCurSelectedFengXiJieGuo ();
				String allstring = tooltip;
				if(fxjg !=null) {
					for(JiaRuJiHua jrjh : fxjg) {
						LocalDate actiondate = jrjh.getJiaRuDate();
						String actiontype = jrjh.getGuanZhuType();
						String shuoming = jrjh.getJiHuaShuoMing();
						
						allstring = allstring +  "[" + actiondate.toString() + actiontype +  " " + shuoming + "]" + "\n";
					}
				}
				tfldselectedmsg.setText( "" );
				tfldselectedmsg.setText( allstring );
				//
				Comparable datekey = panelgeguwkzhanbi.getCurSelectedBarDate ();
				panelbkcje.highLightSpecificBarColumn (datekey);
				panelbkwkzhanbi.highLightSpecificBarColumn (datekey);
				panelgegucje.highLightSpecificBarColumn (datekey);
				panelGeguDapanZhanBi.highLightSpecificBarColumn (datekey);
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
							refreshCurentBanKuaiFengXiResult (selectedbk);
							displayNodeInfo(selectedbk);
							cbxsearchbk.updateUserSelectedNode(selectedbk);
					} else 	{
						JOptionPane.showMessageDialog(null,"股票/板块代码有误或名称拼音有误！","Warning", JOptionPane.WARNING_MESSAGE);
					}

					
				}
			}
			
//			private boolean checkCodeInputFormat(String stockcode) 
//			{
//				// TODO Auto-generated method stub
////				System.out.println(Pattern.matches("\\d{6}$","2223") );
////				System.out.println(Pattern.matches("^\\d{6}","600138zhong") );
////				System.out.println(Pattern.matches("\\d{6}$","000123") );
////				System.out.println(Pattern.matches("\\d{6}[\u4E00-\u9FA5A-Za-z0-9_]+$","000123abccc") );
////				System.out.println(Pattern.matches("\\d{6}[\u4E00-\u9FA5A-Za-z0-9_]+$","000123") );
////				System.out.println(Pattern.matches("^\\d{6}{6,100}$","000123中青旅理论") );
////				System.out.println(Pattern.matches("\\d{6}[\u4E00-\u9FA5A-Za-z0-9_]$","ccccc000123abccc") );
//				
//				//或者是6位全数字，或者是前面6位是数字
//					if( Pattern.matches("\\d{6}$",stockcode)  || Pattern.matches("\\d{6}[\u4E00-\u9FA5A-Za-z0-9_]+$",stockcode) )
//						return true;
//					else return false;
//			}
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
					
					findInputedNodeInTable (nodecode);
//						hightlightSpecificSector ((Stock)userinputnode);
//						refreshGeGuFengXiResult ((Stock)userinputnode);
					
				}
				
				if(arg0.getStateChange() == ItemEvent.DESELECTED) {
//					BkChanYeLianTreeNode userinputnode = cbxstockcode.getUserInputNode();
//					System.out.println("test");
				
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
	    		panelGeguDapanZhanBi.resetDate();
	    		((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).deleteAllRows();
//	    		initializeBanKuaiZhanBiByGrowthRate ();
	    		
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
	    		panelGeguDapanZhanBi.resetDate();
	    		((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).deleteAllRows();
//	    		initializeBanKuaiZhanBiByGrowthRate ();
	    		
	    		lastselecteddate = startday;

			}
		});
		
	
		editorPanebankuai.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				String selbk = editorPanebankuai.getSelectedBanKuai(); 
				String selbkcode = selbk.trim().substring(1, 7);

				int rowindex = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuaiRowIndex(selbkcode);
				if(rowindex != -1) {
					int modelRow = tableBkZhanBi.convertRowIndexToView(rowindex);
					tableBkZhanBi.setRowSelectionInterval(modelRow, modelRow);
					tableBkZhanBi.scrollRectToVisible(new Rectangle(tableBkZhanBi.getCellRect(modelRow, 0, true)));
					
					BanKuai selectedbk = ((BanKuaiInfoTableModel)tableBkZhanBi.getModel()).getBanKuai(rowindex);
					refreshCurentBanKuaiFengXiResult (selectedbk);
					displayNodeInfo(selectedbk);
					patchParsedFile (selectedbk);
					
					//
					String stockcode = cbxstockcode.getSelectedItem().toString().substring(0, 6);
					findInputedNodeInTable (stockcode);
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
					cbxstockcode.updateUserSelectedNode (selectstock); 
					displayStockSuoShuBanKuai(selectstock);
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
				Stock selectstock = ((BanKuaiGeGuTableModel)tablexuandingzhou.getModel()).getStock (modelRow);
	
				if (arg0.getClickCount() == 1) {
					hightlightSpecificSector (selectstock);
					refreshGeGuFengXiResult (selectstock);
					selectstock = (Stock)cbxstockcode.updateUserSelectedNode(selectstock);
					displayStockSuoShuBanKuai(selectstock);
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
				Stock selectstock = ((BanKuaiGeGuTableModel)tablexuandingminustwo.getModel()).getStock (modelRow);
	
				if (arg0.getClickCount() == 1) {
					hightlightSpecificSector (selectstock);
					refreshGeGuFengXiResult (selectstock);
					selectstock = (Stock)cbxstockcode.updateUserSelectedNode(selectstock);
					displayStockSuoShuBanKuai(selectstock);
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
				Stock selectstock = ((BanKuaiGeGuTableModel)tablexuandingminusone.getModel()).getStock (modelRow);
	
				if (arg0.getClickCount() == 1) {
					hightlightSpecificSector (selectstock);
					 refreshGeGuFengXiResult (selectstock);
					 selectstock = (Stock)cbxstockcode.updateUserSelectedNode(selectstock);
					 displayStockSuoShuBanKuai(selectstock);
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
				Stock selectstock = ((BanKuaiGeGuTableModel)tablexuandingplusone.getModel()).getStock (modelRow);
	
				if (arg0.getClickCount() == 1) {
					hightlightSpecificSector (selectstock);
					refreshGeGuFengXiResult (selectstock);
					selectstock = (Stock)cbxstockcode.updateUserSelectedNode(selectstock);
					displayStockSuoShuBanKuai(selectstock);
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
				Stock selectstock = ((BanKuaiGeGuTableModel)tablexuandingplustwo.getModel()).getStock (modelRow);
	
				if (arg0.getClickCount() == 1) {
					hightlightSpecificSector (selectstock);
					refreshGeGuFengXiResult (selectstock);
					selectstock = (Stock)cbxstockcode.updateUserSelectedNode(selectstock);
					displayStockSuoShuBanKuai(selectstock);
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
				refreshCurentBanKuaiFengXiResult (selectedbk);
				displayNodeInfo(selectedbk);
				cbxsearchbk.updateUserSelectedNode(selectedbk);
				patchParsedFile (selectedbk);
				//装配每日模型文件
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
			    		panelGeguDapanZhanBi.resetDate();
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
			    		panelGeguDapanZhanBi.resetDate();
			    		((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel()).deleteAllRows();
//			    		initializeBanKuaiZhanBiByGrowthRate ();
			    		
			    		lastselecteddate = newdate;
			    		
//			    		btnresetdate.setEnabled(false);
		    		}
					
				}
			
		});
		
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
		    
//		    System.out.println(linuxpath);
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
		treeroot.setParseFileStockSet(stockinfile);
		
	}
	/*
	 * 在个股表中发现输入的个股
	 */
	protected void findInputedNodeInTable(String nodecode) 
	{
		int rowindex = ((BanKuaiGeGuTableModel)tableGuGuZhanBiInBk.getModel() ).getStockRowIndex(nodecode);
		if(rowindex <0) {
			tableGuGuZhanBiInBk.getSelectionModel().clearSelection();
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
		} else {
			int modelRow = tablexuandingplustwo.convertRowIndexToView(rowindex);
			int curselectrow = tablexuandingplustwo.getSelectedRow();
			if( curselectrow != modelRow) {
				tablexuandingplustwo.setRowSelectionInterval(modelRow, modelRow);
				tablexuandingplustwo.scrollRectToVisible(new Rectangle(tablexuandingplustwo.getCellRect(modelRow, 0, true)));
			}
		}
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
	private BanKuaiInfoTable tableBkZhanBi;
//	private JTable tableGuGuZhanBiInBk;
	private BanKuaiGeGuTable tableGuGuZhanBiInBk;
	private BanKuaiFengXiBarChartGgBkZbPnl panelbkwkzhanbi;
	private JTextField tfldweight;
	private BanKuaiFengXiBarChartGgBkZbPnl panelgeguwkzhanbi;
	private BanKuaiFengXiPieChartPnl pnllastestggzhanbi;
	private BanKuaiFengXiPieChartPnl panelLastWkGeGuZhanBi;
	private BanKuaiFengXiPieChartPnl panelselectwkgeguzhanbi;
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
	private JTabbedPane tabbedPane;
	private BanKuaiFengXiBarChartGgDpZbPnl panelGeguDapanZhanBi;
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
	private JPanel paneltimeline;
	private JCheckBox chckbxmaxwk;
	private JTextField tflddisplaymaxwk;
	
	private void initializeGui() {
		setTitle("\u677F\u5757\u5206\u6790");
		setBounds(100, 100, 1912, 1016);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.EAST);
		
		JPanel panel = new JPanel();
		JPanel panel_1 = new JPanel();
		
		pnllastestggzhanbi = new BanKuaiFengXiPieChartPnl();
		pnllastestggzhanbi.setBorder(new TitledBorder(null, "\u677F\u5757\u5F53\u524D\u5468\u4E2A\u80A1\u5360\u6BD4", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		panelLastWkGeGuZhanBi = new BanKuaiFengXiPieChartPnl();
		panelLastWkGeGuZhanBi.setBorder(new TitledBorder(null, "\u677F\u5757\u4E0A\u4E00\u5468\u4E2A\u80A1\u5360\u6BD4", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		panelselectwkgeguzhanbi = new BanKuaiFengXiPieChartPnl();
		panelselectwkgeguzhanbi.setBorder(new TitledBorder(null, "\u9009\u5B9A\u5468\u5360\u6BD4", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JScrollPane scrollPane_3 = new JScrollPane();
		
		JPanel panel_2 = new JPanel();
		
		JScrollPane scrollPanestockbk = new JScrollPane();
		
		editorPanebankuai = new BanKuaiListEditorPane();
		scrollPanestockbk.setViewportView(editorPanebankuai);
		
		paneltimeline = new JPanel();
		paneltimeline.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
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
							.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 382, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)))
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
							.addGroup(gl_contentPanel.createSequentialGroup()
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addComponent(paneltimeline, GroupLayout.DEFAULT_SIZE, 812, Short.MAX_VALUE)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(scrollPane_3, GroupLayout.PREFERRED_SIZE, 254, GroupLayout.PREFERRED_SIZE)
								.addGap(25))
							.addGroup(gl_contentPanel.createSequentialGroup()
								.addGap(12)
								.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 1091, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED)))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(scrollPanestockbk, GroupLayout.PREFERRED_SIZE, 1091, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)))
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGap(8)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(pnllastestggzhanbi, GroupLayout.PREFERRED_SIZE, 372, Short.MAX_VALUE)
								.addComponent(panelLastWkGeGuZhanBi, Alignment.TRAILING, 0, 0, Short.MAX_VALUE)))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(panelselectwkgeguzhanbi, GroupLayout.PREFERRED_SIZE, 372, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addGap(144)
									.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 650, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(scrollPanestockbk, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 850, GroupLayout.PREFERRED_SIZE))))
						.addGroup(Alignment.LEADING, gl_contentPanel.createParallelGroup(Alignment.LEADING)
							.addComponent(paneltimeline, GroupLayout.PREFERRED_SIZE, 213, GroupLayout.PREFERRED_SIZE)
							.addComponent(scrollPane_3, GroupLayout.PREFERRED_SIZE, 204, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(pnllastestggzhanbi, GroupLayout.PREFERRED_SIZE, 311, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(panelLastWkGeGuZhanBi, GroupLayout.PREFERRED_SIZE, 289, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(panelselectwkgeguzhanbi, GroupLayout.PREFERRED_SIZE, 301, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		
		tfldselectedmsg = new JTextArea();
		scrollPane_3.setViewportView(tfldselectedmsg);
		tfldselectedmsg.setLineWrap(true);
		
		
		tfldselectedmsg.setEditable(false);
		tfldselectedmsg.setColumns(10);
		
		panelbkcje = new BanKuaiFengXiBarChartCjePnl();
		panelbkcje.setBorder(new TitledBorder(null, "\u677F\u5757\u6210\u4EA4\u989D", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		panelbkwkzhanbi = new BanKuaiFengXiBarChartGgBkZbPnl();
		
		panelbkwkzhanbi.setBorder(new TitledBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)), "\u677F\u5757\u534A\u5E74\u5185\u5468\u5360\u6BD4", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		
		panelgegucje = new BanKuaiFengXiBarChartCjePnl();
		panelgegucje.setBorder(new TitledBorder(null, "\u4E2A\u80A1\u6210\u4EA4\u989D", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		tabbedPanegeguzhanbi = new JTabbedPane(JTabbedPane.TOP);
		
		panelGeguDapanZhanBi = new BanKuaiFengXiBarChartGgDpZbPnl();
		panelGeguDapanZhanBi.setBorder(new TitledBorder(null, "\u4E2A\u80A1\u5927\u76D8\u5360\u6BD4", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		tabbedPanegeguzhanbi.addTab("\u4E2A\u80A1\u5927\u76D8\u5360\u6BD4", null, panelGeguDapanZhanBi, null);
		
		panelgeguwkzhanbi = new BanKuaiFengXiBarChartGgBkZbPnl();
		tabbedPanegeguzhanbi.addTab("\u4E2A\u80A1\u677F\u5757\u5360\u6BD4", null, panelgeguwkzhanbi, null);
		panelgeguwkzhanbi.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "\u4E2A\u80A1\u677F\u5757\u5468\u5360\u6BD4", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
						.addComponent(panelbkwkzhanbi, GroupLayout.DEFAULT_SIZE, 528, Short.MAX_VALUE)
						.addComponent(panelbkcje, GroupLayout.PREFERRED_SIZE, 528, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_2.createParallelGroup(Alignment.TRAILING)
						.addComponent(panelgegucje, GroupLayout.PREFERRED_SIZE, 537, Short.MAX_VALUE)
						.addComponent(tabbedPanegeguzhanbi, GroupLayout.PREFERRED_SIZE, 537, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_panel_2.setVerticalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_2.createParallelGroup(Alignment.BASELINE)
						.addComponent(panelbkcje, GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
						.addComponent(panelgegucje, GroupLayout.PREFERRED_SIZE, 256, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
						.addComponent(panelbkwkzhanbi, GroupLayout.DEFAULT_SIZE, 348, Short.MAX_VALUE)
						.addComponent(tabbedPanegeguzhanbi, GroupLayout.DEFAULT_SIZE, 348, Short.MAX_VALUE))
					.addContainerGap())
		);
		panel_2.setLayout(gl_panel_2);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		
		JTabbedPane tabbedPane_1 = new JTabbedPane(JTabbedPane.TOP);
		cbxstockcode = new JStockComboBox(6);
		
		cbxsearchbk = new JStockComboBox(4);

		cbxsearchbk.setEditable(true);
		
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap(221, Short.MAX_VALUE)
					.addComponent(cbxstockcode, GroupLayout.PREFERRED_SIZE, 115, GroupLayout.PREFERRED_SIZE)
					.addGap(20))
				.addGroup(Alignment.LEADING, gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)
						.addComponent(cbxsearchbk, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)
						.addComponent(tabbedPane_1, GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addComponent(cbxsearchbk, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(tabbedPane_1, GroupLayout.PREFERRED_SIZE, 370, GroupLayout.PREFERRED_SIZE)
					.addGap(10)
					.addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 370, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(cbxstockcode, GroupLayout.PREFERRED_SIZE, 31, GroupLayout.PREFERRED_SIZE)
					.addGap(14))
		);
		
		sclpleft = new JScrollPane();
		tabbedPane_1.addTab("\u677F\u5757\u5360\u6BD4", null, sclpleft, null);
		
		tableBkZhanBi = new BanKuaiInfoTable(this.stockmanager,this.bkcyl);	
		
		sclpleft.setViewportView(tableBkZhanBi);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		tabbedPane_1.addTab("\u677F\u5757\u4FE1\u606F", null, scrollPane_2, null);
		
		editorPanenodeinfo = new DisplayBkGgInfoEditorPane();
		scrollPane_2.setViewportView(editorPanenodeinfo);
		editorPanenodeinfo.setEditable(false);
		
		JScrollPane scrollPanedangqian = new JScrollPane();
		tabbedPane.addTab("当前周", null, scrollPanedangqian, null);
		tabbedPane.setBackgroundAt(0, Color.ORANGE);
		
		
		tableGuGuZhanBiInBk = new BanKuaiGeGuTable (this.stockmanager);
		tableGuGuZhanBiInBk.hideZhanBiColumn(1);
		tableGuGuZhanBiInBk.sortByZhanBiGrowthRate();
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
		tablexuandingzhou.hideZhanBiColumn(1);

		scrollPanexuanding.setViewportView(tablexuandingzhou);
		
		JScrollPane scrollPanexuandingminusone = new JScrollPane();
		tabbedPane.addTab("\u9009\u5B9A\u5468-1", null, scrollPanexuandingminusone, null);
		tabbedPane.setBackgroundAt(2, Color.LIGHT_GRAY);
		
		tablexuandingminusone = new BanKuaiGeGuTable (this.stockmanager);
		tablexuandingminusone.hideZhanBiColumn(1);
		scrollPanexuandingminusone.setViewportView(tablexuandingminusone);
		
		JScrollPane scrollPanexuandingminustwo = new JScrollPane();
		tabbedPane.addTab("\u9009\u5B9A\u5468-2", null, scrollPanexuandingminustwo, null);
		
		tablexuandingminustwo = new BanKuaiGeGuTable (this.stockmanager);
		tablexuandingminustwo.hideZhanBiColumn(1);
		scrollPanexuandingminustwo.setViewportView(tablexuandingminustwo);
		
		JScrollPane scrollPanexuandingplusone = new JScrollPane();
		tabbedPane.addTab("\u9009\u5B9A\u5468+1", null, scrollPanexuandingplusone, null);
		
		tablexuandingplusone = new BanKuaiGeGuTable (this.stockmanager);
		tablexuandingplusone.hideZhanBiColumn(1);
		scrollPanexuandingplusone.setViewportView(tablexuandingplusone);
		
		JScrollPane scrollPanexuandingplustwo = new JScrollPane();
		tabbedPane.addTab("\u9009\u5B9A\u5468+2", null, scrollPanexuandingplustwo, null);
		
		tablexuandingplustwo = new BanKuaiGeGuTable (this.stockmanager);
		tablexuandingplustwo.hideZhanBiColumn(1);
		scrollPanexuandingplustwo.setViewportView(tablexuandingplustwo);
		
//		tableBkZhanBi = new BanKuaiGeGuTable (this.stockmanager);
		
		
		panel_1.setLayout(gl_panel_1);
		
		dateChooser = new JDateChooser();
		dateChooser.setDateFormatString("yyyy-MM-dd");
		
		btnresetdate = new JButton("\u91CD\u7F6E");
		btnresetdate.setEnabled(false);
		
		btnsixmonthbefore = new JButton("<");
		
		btnsixmonthbefore.setToolTipText("\u5411\u524D6\u4E2A\u6708");
		
		btnsixmonthafter = new JButton(">");
		btnsixmonthafter.setToolTipText("\u5411\u540E6\u4E2A\u6708");
		
		lblNewLabel = new JLabel("\u4E0A\u8BC1");
		
		JLabel lblNewLabel_1 = new JLabel("\u6DF1\u8BC1");
		
		lblszcje = new JLabel("New label");
		
		lblshcje = new JLabel("New label");
		
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
							.addComponent(lblNewLabel)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblszcje, GroupLayout.PREFERRED_SIZE, 124, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(lblNewLabel_1)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(lblshcje, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
					.addContainerGap(60, Short.MAX_VALUE))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
						.addComponent(dateChooser, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
							.addComponent(btnsixmonthbefore, GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
							.addComponent(btnsixmonthafter, GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
							.addComponent(btnresetdate, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel)
						.addComponent(lblszcje)
						.addComponent(lblNewLabel_1)
						.addComponent(lblshcje))
					.addGap(8))
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
			
			JCheckBox chckbxNewCheckBox = new JCheckBox("\u5254\u9664\u80A1\u7968\u6743\u91CD<=");
			
			tfldweight = new JTextField();
			tfldweight.setText("0");
			tfldweight.setColumns(10);
			
			ckboxshowcje = new JCheckBox("\u7A81\u51FA\u663E\u793A\u6210\u4EA4\u989D\u5927\u4E8E()\u4EBF");
			ckboxshowcje.setForeground(Color.YELLOW);
			
	
			
			tfldshowcje = new JTextField();
			tfldshowcje.setForeground(Color.BLUE);
			tfldshowcje.setColumns(10);
			
			ckboxparsefile = new JCheckBox("\u6BCF\u65E5\u5206\u6790\u6587\u4EF6");
			ckboxparsefile.setForeground(Color.ORANGE);
			
			
			tfldparsedfile = new JTextField();
			tfldparsedfile.setForeground(Color.ORANGE);
			tfldparsedfile.setEditable(false);
			tfldparsedfile.setColumns(10);
			
			btnChosPasFile = new JButton("\u9009\u62E9\u6587\u4EF6");
			btnChosPasFile.setForeground(Color.ORANGE);
			
			JLabel label = new JLabel("17.12.18.15.26");
			label.setVerticalAlignment(SwingConstants.BOTTOM);
			
			chckbxmaxwk = new JCheckBox("\u7A81\u51FA\u5360\u6BD4MAXWK>=");
			
			chckbxmaxwk.setForeground(Color.RED);
			
			tflddisplaymaxwk = new JTextField();
			tflddisplaymaxwk.setText("4");
			tflddisplaymaxwk.setColumns(10);
			
			GroupLayout gl_buttonPane = new GroupLayout(buttonPane);
			gl_buttonPane.setHorizontalGroup(
				gl_buttonPane.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_buttonPane.createSequentialGroup()
						.addGap(33)
						.addComponent(chckbxNewCheckBox)
						.addGap(14)
						.addComponent(tfldweight, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(ckboxshowcje)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(tfldshowcje, GroupLayout.PREFERRED_SIZE, 51, GroupLayout.PREFERRED_SIZE)
						.addGap(18)
						.addComponent(ckboxparsefile)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(tfldparsedfile, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(btnChosPasFile)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(chckbxmaxwk)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(tflddisplaymaxwk, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
						.addGap(147)
						.addComponent(label, GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE)
						.addGap(389)
						.addComponent(okButton, GroupLayout.PREFERRED_SIZE, 71, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
						.addGap(172))
			);
			gl_buttonPane.setVerticalGroup(
				gl_buttonPane.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_buttonPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(ckboxshowcje)
						.addComponent(tfldshowcje, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(ckboxparsefile)
						.addComponent(tfldparsedfile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnChosPasFile)
						.addComponent(chckbxNewCheckBox)
						.addComponent(tfldweight, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(label)
						.addComponent(chckbxmaxwk)
						.addComponent(tflddisplaymaxwk, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(okButton)
						.addComponent(cancelButton))
			);
			buttonPane.setLayout(gl_buttonPane);
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