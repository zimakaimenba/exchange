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
import java.util.Set;
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

import com.exchangeinfomanager.bankuaifengxi.BarChartPanelDataChangedListener;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.nodexdata.NodeXPeriodDataBasic;
import com.exchangeinfomanager.nodes.nodexdata.TDXNodeGivenPeriodDataItem;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.google.common.io.Files;
import com.sun.rowset.CachedRowSetImpl;

public class BanKuaiFengXiPieChartCjePnl extends BanKuaiFengXiPieChartPnl 
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
	@Override
	public void updateDate (TDXNodes node, LocalDate date, int difference,String period) 
	{
		if(period.equals(TDXNodeGivenPeriodDataItem.DAY))
			date = date.plus(difference+pianyiliang,ChronoUnit.DAYS);
		else if(period.equals(TDXNodeGivenPeriodDataItem.WEEK))
			date = date.plus(difference+this.pianyiliang,ChronoUnit.WEEKS);
		else if(period.equals(TDXNodeGivenPeriodDataItem.MONTH))
			date = date.plus(difference+this.pianyiliang,ChronoUnit.MONTHS);
		
		setBanKuaiCjeNeededDisplay(node,date,period);
	}
	
	private void setBanKuaiCjeNeededDisplay (TDXNodes bankuai,LocalDate weeknumber, String period) 
	{
		this.curdisplaybk = bankuai;
		this.displayedweeknumber = weeknumber;
		
		Set<StockOfBanKuai> tmpallbkge = ((BanKuai)bankuai).getSpecificPeriodBanKuaiGeGu (weeknumber,0,period);
		
		piechartdataset.clear();
		super.piechart.setNotify(false);
    	
    	if(tmpallbkge == null || tmpallbkge.isEmpty())
    		return;
    	
    	for ( StockOfBanKuai tmpstock : tmpallbkge) {
    		String ggcode = tmpstock.getMyOwnCode();
    		String stockname = tmpstock.getMyOwnName();
    		
    		//找到对应周的数据
    		NodeXPeriodDataBasic stockxdataforbk = tmpstock.getStock().getNodeXPeroidData(period);
    		Double cje = stockxdataforbk.getChengJiaoEr(weeknumber,0);
    		if(cje != null) {
       	    	if(stockname != null)
        	    		piechartdataset.setValue(ggcode+stockname,cje);
       	    	else 
        	    		piechartdataset.setValue(ggcode,cje);
    		}
    	}
    	
    	super.piechart.setNotify(true);

		setPanelTitle ("成交额占比");
		
	}

	


}

