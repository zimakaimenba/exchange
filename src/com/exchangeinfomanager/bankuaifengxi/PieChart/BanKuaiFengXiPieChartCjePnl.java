package com.exchangeinfomanager.bankuaifengxi.PieChart;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.File;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.PieToolTipGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;
import org.w3c.dom.events.MouseEvent;

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.asinglestockinfo.Stock.StockNodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.StockGivenPeriodDataItem;
import com.exchangeinfomanager.asinglestockinfo.StockOfBanKuai;
import com.exchangeinfomanager.bankuaifengxi.BarChartPanelDataChangedListener;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.google.common.io.Files;
import com.sun.rowset.CachedRowSetImpl;

public class BanKuaiFengXiPieChartCjePnl extends BanKuaiFengXiPieChartPnl implements BarChartPanelDataChangedListener
{
	private int pianyiliang;

	/**
	 * Create the panel.
	 */
	public BanKuaiFengXiPieChartCjePnl(int pianyiliang) 
	{
		super ();
		this.pianyiliang = pianyiliang;
	}
	
	public void updatedDate(BkChanYeLianTreeNode node, LocalDate date, int difference,String period)
	{
		if(period.equals(StockGivenPeriodDataItem.DAY))
			date = date.plus(difference+pianyiliang,ChronoUnit.DAYS);
		else if(period.equals(StockGivenPeriodDataItem.WEEK))
			date = date.plus(difference+this.pianyiliang,ChronoUnit.WEEKS);
		else if(period.equals(StockGivenPeriodDataItem.MONTH))
			date = date.plus(difference+this.pianyiliang,ChronoUnit.MONTHS);
		
		setBanKuaiCjeNeededDisplay((BanKuai)node,10,date,period);
	}
	@Override
	public void updatedDate(BkChanYeLianTreeNode node, LocalDate startdate, LocalDate enddate, String period) {
		// TODO Auto-generated method stub
	}
	
	public void setBanKuaiCjeNeededDisplay (BanKuai bankuai,int weightgate,LocalDate weeknumber, String period) 
	{
		this.curdisplaybk = bankuai;
		this.displayedweeknumber = weeknumber;
		
		ArrayList<StockOfBanKuai> tmpallbkge = bankuai.getAllCurrentBanKuaiGeGu();
		
//		piechartdataset = new DefaultPieDataset();
		piechartdataset.clear();
		super.piechart.setNotify(false);
    	
    	if(tmpallbkge == null || tmpallbkge.isEmpty())
    		return;
    	
    	for ( StockOfBanKuai tmpstock : tmpallbkge) {
    		String ggcode = tmpstock.getMyOwnCode();
    		String stockname = tmpstock.getMyOwnName();
    		
    		//找到对应周的数据
    		NodeXPeriodDataBasic stockxdataforbk = tmpstock.getNodeXPeroidData(period);
    		Double cje = stockxdataforbk.getChengJiaoEr(weeknumber,0);
    		if(cje != null) {
       	    	if(stockname != null)
        	    		piechartdataset.setValue(ggcode+stockname,cje);
       	    	else 
        	    		piechartdataset.setValue(ggcode,cje);
    		}
    	}
    	
    	super.piechart.setNotify(true);
//    	pieplot.setDataset(piechartdataset);
//		createCjeDataset(bankuai.getMyOwnCode(),tmpallbkge,weightgate,weeknumber);
		setPanelTitle ("成交额占比");
		
	}


}

