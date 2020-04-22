package com.exchangeinfomanager.bankuaifengxi.CandleStick;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.text.AttributedString;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.swing.BoxLayout;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.ToolTipManager;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.annotations.CategoryPointerAnnotation;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.annotations.XYShapeAnnotation;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.CategoryMarker;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.HighLowRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.ComparableObjectItem;
import org.jfree.data.general.SeriesDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimePeriodAnchor;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.time.Week;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
import org.jfree.data.xy.OHLCDataItem;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.Layer;
import org.jfree.ui.LengthAdjustmentType;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.ta4j.core.Bar;


import com.exchangeinfomanager.News.News;
import com.exchangeinfomanager.News.ExternalNewsType.InsertedExternalNews;
import com.exchangeinfomanager.News.ExternalNewsType.ZhiShuBoLang;
import com.exchangeinfomanager.bankuaifengxi.BarChartPanelDataChangedListener;
import com.exchangeinfomanager.bankuaifengxi.BarChartPanelHightLightColumnListener;
import com.exchangeinfomanager.bankuaifengxi.QueKou;

import com.exchangeinfomanager.commonlib.CommonUtility;

import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForJFC.TDXNodesXPeriodDataForJFC;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.google.common.base.Splitter;
import com.google.common.collect.Multimap;

/**
 * The Class JfreeCandlestickChart.
 * 
 * @author ashraf
 */
@SuppressWarnings("serial")
public class BanKuaiFengXiCandlestickPnl extends JPanel implements BarChartPanelDataChangedListener,BarChartPanelHightLightColumnListener
{
	public BanKuaiFengXiCandlestickPnl() 
	{
		super ();
		super.setBorder(new TitledBorder(null, "\u677F\u5757/\u4E2A\u80A1K\u7EBF\u8D70\u52BF", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		createChartPanel();
		createEvents ();
	}

	private static Logger logger = Logger.getLogger(BanKuaiFengXiCandlestickPnl.class);
	public static final String ZHISHU_PROPERTY = "combinedzhishu";
//	private SystemConfigration sysconfig;
	protected TDXNodes curdisplayednode;
	protected TDXNodes curdisplayedsupernode;
	protected String globeperiod;

//	private OHLCSeries ohlcSeries;
	private OHLCSeriesCollection candlestickDataset;
	
//	private OHLCSeries dapanohlcSeries;
	private OHLCSeriesCollection dapancandlestickDataset;

	private NumberAxis priceAxis;
	private ChartPanel chartPanel;
	
	private TimeSeriesCollection maDataSet ;

	private JFreeChart candlestickChart;

	private DateAxis dayAxis;
	private JMenuItem mntmbankuai;
	private JMenuItem mntmzhishu;
	private boolean displayhuibuquekou;
	private boolean displaymacddivergence = true;
	private boolean hasmacddivergence;
	
	private List<ValueMarker> categorymarkerlist; //指数关键日期的marker
	private JMenuItem mntmamo;

	public TDXNodes getCurDisplayedNode ()
	{
		return this.curdisplayednode;
	}
	public LocalDate getDispalyStartDate ()
	{
		OHLCSeries ohlcSeries = candlestickDataset.getSeries(0);
		Integer ohlccount = candlestickDataset.getSeries(0).getItemCount();
		if (ohlccount == null || ohlccount ==0 )
			return null;
		
		OHLCItem kxiandatacurwk = (OHLCItem) ohlcSeries.getDataItem(0);
		RegularTimePeriod curperiod = kxiandatacurwk.getPeriod();
		LocalDate curstart = curperiod.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//		LocalDate curend = curperiod.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		
		return curstart;
	}
	public LocalDate getDispalyEndDate ()
	{
		OHLCSeries ohlcSeries = candlestickDataset.getSeries(0);
		Integer ohlccount = candlestickDataset.getSeries(0).getItemCount();
		if (ohlccount == null || ohlccount ==0 )
			return null;
		
		OHLCItem kxiandatacurwk = (OHLCItem) ohlcSeries.getDataItem(ohlcSeries.getItemCount()-1);
		RegularTimePeriod curperiod = kxiandatacurwk.getPeriod();
		LocalDate curend = curperiod.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//		LocalDate curend = curperiod.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		
		return curend;
	}
	
	@Override
	public void updatedDate(TDXNodes node, LocalDate startdate, LocalDate enddate,String period) 
	{
		candlestickChart.setNotify(false);
		this.resetDate();
		
		this.curdisplayednode = node;
		this.globeperiod = period;
		setNodeCandleStickDate2 ( node,  startdate,  enddate, period , 0);
		
//		displayZhiShuGuanJianRiQi ();
		if(node.getType() == TDXNodes.TDXGG && this.displayhuibuquekou )
			displayQueKouToChart ();
		
		if(node.getType() == TDXNodes.TDXGG && this.displaymacddivergence )
			displayMACDDivergenceToChart (NodeGivenPeriodDataItem.DAY);
		
		if(this.checkDatesShunXu (startdate,enddate) )
			setPanelTitle ( node, startdate, enddate);
		else
			setPanelTitle ( node,  enddate,startdate);
		
		setNodeAMOData (node, period);
		
		candlestickChart.setNotify(true);
	}
	/*
	 * node和上级板块同时显示
	 */
	public void updatedDate(TDXNodes superbk, TDXNodes node, LocalDate requirestart, LocalDate requireend ,String period)
	{
		this.curdisplayednode = node;
		this.curdisplayedsupernode = superbk;
		this.globeperiod = period;
		
		candlestickChart.setNotify(false);
		this.resetDate();
		
		setNodeCandleStickDate2 ( node,  requirestart,  requireend, period , 0);
		setNodeCandleStickDate2 ( superbk,  requirestart,  requireend, period , 1);
		

		if(node.getType() == TDXNodes.TDXGG && this.displayhuibuquekou )
			displayQueKouToChart ();
		
		if(node.getType() == TDXNodes.TDXGG && this.displaymacddivergence )
			displayMACDDivergenceToChart (NodeGivenPeriodDataItem.DAY);
		
		setPanelTitle ( node, requirestart, requireend);
		
//		setNodeAMOData (node,requirestart,  requireend, period);
	}
	
	/*
	 * 个股K线 , 方法2效率略微高一点
	 */
	private void setNodeCandleStickDate2(TDXNodes node, LocalDate requirestart, LocalDate requireend,
			String period, int indexofseries) 
	{
		// TODO Auto-generated method stub
		OHLCSeries tmpohlcSeries =  new OHLCSeries ("Kxian"); ;
		OHLCSeriesCollection tmpcandlestickDataset;
		tmpcandlestickDataset = (OHLCSeriesCollection)candlestickChart.getXYPlot().getDataset(indexofseries);
		
		TimeSeries tmpma250ts = new TimeSeries ("ma250"); 
		TimeSeries tmpma60ts = new TimeSeries ("ma60");
			
		tmpohlcSeries.setNotify(false);
		tmpcandlestickDataset.setNotify(false);
		
		TDXNodesXPeriodDataForJFC nodexdata = (TDXNodesXPeriodDataForJFC) node.getNodeXPeroidData(period);
		LocalDate nodestart = nodexdata.getOHLCRecordsStartDate();
		LocalDate nodeend = nodexdata.getOHLCRecordsEndDate();
		if(nodestart == null)
			return;
		
		Interval result = getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval (nodestart,nodeend, requirestart, requireend);
		DateTime overlapstartdt = result.getStart();
		DateTime overlapenddt = result.getEnd();
		
		LocalDate overlapldstartday = LocalDate.of(overlapstartdt.getYear(), overlapstartdt.getMonthOfYear(), overlapstartdt.getDayOfMonth());
		LocalDate overlapldendday = LocalDate.of(overlapenddt.getYear(), overlapenddt.getMonthOfYear(), overlapenddt.getDayOfMonth());
		
		Integer indexstart = null; //确保START/END日期都有数据
		do {
			indexstart = nodexdata.getIndexOfSpecificDateOHLCData(overlapldstartday, 0);
			if(indexstart == null) {
				if(period.equals(NodeGivenPeriodDataItem.WEEK))
					overlapldstartday = overlapldstartday.plus(1, ChronoUnit.WEEKS) ;
				else if(period.equals(NodeGivenPeriodDataItem.DAY))
					overlapldstartday = overlapldstartday.plus(1, ChronoUnit.DAYS) ;
				else if(period.equals(NodeGivenPeriodDataItem.MONTH))
					overlapldstartday = overlapldstartday.plus(1, ChronoUnit.MONTHS) ;
			}
		} while (indexstart == null);
		Integer indexend = null;
		do {
			indexend = nodexdata.getIndexOfSpecificDateOHLCData(overlapldendday, 0);
			if(indexend == null) {
				if(period.equals(NodeGivenPeriodDataItem.WEEK))
					overlapldendday = overlapldendday.minus(1, ChronoUnit.WEEKS) ;
				else if(period.equals(NodeGivenPeriodDataItem.DAY))
					overlapldendday = overlapldendday.minus(1, ChronoUnit.DAYS) ;
				else if(period.equals(NodeGivenPeriodDataItem.MONTH))
					overlapldendday = overlapldendday.minus(1, ChronoUnit.MONTHS) ;
			}
		} while (indexend == null);
		
		double lowestLow =10000.0;  double highestHigh = 0.0;
		for(int i= indexstart; i<= indexend; i++) {
			OHLCItem tmpohlc =  (OHLCItem) nodexdata.getOHLCData().getDataItem(i);
			
			try {
				tmpohlcSeries.add(tmpohlc);
			} catch (org.jfree.data.general.SeriesException e) {
				e.printStackTrace();
			}
			
			Double low = tmpohlc.getLowValue();
			Double high = tmpohlc.getHighValue();
			
			if(low < lowestLow && low !=0) {//股价不可能为0，为0，说明停牌，无需计算
				lowestLow = low;
			}
			if(high > highestHigh && high !=0) {
				highestHigh = high;
			}
			
			RegularTimePeriod tmpperiod = tmpohlc.getPeriod();
			TimeSeriesDataItem tmpma250 = nodexdata.getMA250().getDataItem(tmpperiod);
			TimeSeriesDataItem tmpma60 = nodexdata.getMA60().getDataItem(tmpperiod);
			if(tmpma250 != null)
				tmpma250ts.add(tmpma250);
			if(tmpma60 != null)
				tmpma60ts.add(tmpma60);

		}


		tmpohlcSeries.setNotify(false);
		tmpcandlestickDataset.setNotify(false);
		
		try {
			candlestickChart.getXYPlot().getRangeAxis(indexofseries).setRange(lowestLow*0.98, highestHigh*1.02);
		} catch (java.lang.IllegalArgumentException e ) {
//			e.printStackTrace();
		}
		
//		if(indexofseries == 0) {
//			ohlcSeries = tmpohlcSeries;
//		} else {
//			dapanohlcSeries = tmpohlcSeries;
//		}
		tmpcandlestickDataset.addSeries(tmpohlcSeries);
		this.maDataSet.addSeries(tmpma250ts);
		this.maDataSet.addSeries(tmpma60ts);
		
		this.maDataSet.setNotify(true);
		tmpohlcSeries.setNotify(true);
        tmpcandlestickDataset.setNotify(true);
        candlestickChart.setNotify(true);
        tmpohlcSeries.setNotify(false);
        tmpcandlestickDataset.setNotify(false);
        candlestickChart.setNotify(false);
        this.maDataSet.setNotify(false);
	}
	/*
	 *AMO利用板块/指数的OHLC显示 
	 */
	private void setNodeAMOData (TDXNodes node,	String period)
	{
		dapancandlestickDataset.removeAllSeries();
		
		OHLCSeries dapanohlcSeries = new OHLCSeries ("AMO");
		
		OHLCSeries ohlcSeries = candlestickDataset.getSeries(0);
		OHLCSeriesCollection tmpdapanDataset = (OHLCSeriesCollection)candlestickChart.getXYPlot().getDataset(1);
//		tmpdapanDataset.setNotify(false);
		TDXNodesXPeriodDataForJFC nodexdata = (TDXNodesXPeriodDataForJFC) node.getNodeXPeroidData(period);
		TimeSeries amodata = nodexdata.getAMOData();
		Double highestHigh = 0.0;
		for(int j=1;j < ohlcSeries.getItemCount();j++) {
			
			OHLCItem kxiandatacurwk = (OHLCItem) ohlcSeries.getDataItem(j);
			RegularTimePeriod curperiod = kxiandatacurwk.getPeriod();
//			LocalDate curstart = curperiod.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			TimeSeriesDataItem tmpamo = amodata.getDataItem(curperiod);
			Double amo = tmpamo.getValue().doubleValue();
			OHLCItem tmpohlcitem = new OHLCItem( curperiod, 0, amo, 0, amo);
			dapanohlcSeries.add(tmpohlcitem);
			
			if(amo > highestHigh)
				highestHigh = amo;
		}
		
		tmpdapanDataset.addSeries(dapanohlcSeries);
	
		try {
			candlestickChart.getXYPlot().getRangeAxis(1).setRange(0, highestHigh*1.02);
		} catch (java.lang.IllegalArgumentException e ) {
//			e.printStackTrace();
		}
		
		try {
			tmpdapanDataset.setNotify(true);
	        candlestickDataset.setNotify(true);
	        candlestickChart.setNotify(true);
	        tmpdapanDataset.setNotify(false);
	        candlestickDataset.setNotify(false);
	        candlestickChart.setNotify(false);
		} catch (java.lang.NullPointerException e) {
			
		}
	}
	/*
	 * 
	 */
	private Boolean checkDatesShunXu (LocalDate date1, LocalDate date2) 
	{
		if (date1.isBefore(date2))
			return true;
		else
			return false;
	}
	/*
	 * 个股和板块的K线可以重叠显示 
	 */
	private void setNodeCandleStickDate(TDXNodes node, LocalDate requirestart, LocalDate requireend,
			String period, int indexofseries) 
	{
		// TODO Auto-generated method stub
		OHLCSeries tmpohlcSeries =  new OHLCSeries ("Kxian"); ;
		OHLCSeriesCollection tmpcandlestickDataset;
		tmpcandlestickDataset = (OHLCSeriesCollection)candlestickChart.getXYPlot().getDataset(indexofseries);
		
		TimeSeries tmpma250ts = new TimeSeries ("ma250"); 
		TimeSeries tmpma60ts = new TimeSeries ("ma60");
			
		tmpohlcSeries.setNotify(false);
		tmpcandlestickDataset.setNotify(false);
			
		TDXNodesXPeriodDataForJFC nodexdata = (TDXNodesXPeriodDataForJFC) node.getNodeXPeroidData(period);
		LocalDate tmpdate = requirestart;
		double lowestLow =10000.0;  double highestHigh = 0.0;
		do  {
			OHLCItem tmpohlc = nodexdata.getSpecificDateOHLCData(tmpdate, 0);
			if(tmpohlc == null) {
				if(period.equals(NodeGivenPeriodDataItem.WEEK))
					tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
				else if(period.equals(NodeGivenPeriodDataItem.DAY))
					tmpdate = tmpdate.plus(1, ChronoUnit.DAYS) ;
				else if(period.equals(NodeGivenPeriodDataItem.MONTH))
					tmpdate = tmpdate.plus(1, ChronoUnit.MONTHS) ;
				
				continue;
			}
			
			try {
				tmpohlcSeries.add(tmpohlc);
			} catch (org.jfree.data.general.SeriesException e) {
				e.printStackTrace();
			}
			
			Double low = tmpohlc.getLowValue();
			Double high = tmpohlc.getHighValue();
			
			if(low < lowestLow && low !=0) {//股价不可能为0，为0，说明停牌，无需计算
				lowestLow = low;
			}
			if(high > highestHigh && high !=0) {
				highestHigh = high;
			}
			
			RegularTimePeriod tmpperiod = tmpohlc.getPeriod();
			TimeSeriesDataItem tmpma250 = nodexdata.getMA250().getDataItem(tmpperiod);
			TimeSeriesDataItem tmpma60 = nodexdata.getMA60().getDataItem(tmpperiod);
			if(tmpma250 != null)
				tmpma250ts.add(tmpma250);
			if(tmpma60 != null)
				tmpma60ts.add(tmpma60);
			
			if(period.equals(NodeGivenPeriodDataItem.WEEK))
				tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
			else if(period.equals(NodeGivenPeriodDataItem.DAY))
				tmpdate = tmpdate.plus(1, ChronoUnit.DAYS) ;
			else if(period.equals(NodeGivenPeriodDataItem.MONTH))
				tmpdate = tmpdate.plus(1, ChronoUnit.MONTHS) ;
			
		} while (tmpdate.isBefore( requireend) || tmpdate.isEqual(requireend));

		tmpohlcSeries.setNotify(false);
		tmpcandlestickDataset.setNotify(false);
		
		try {
			candlestickChart.getXYPlot().getRangeAxis(indexofseries).setRange(lowestLow*0.98, highestHigh*1.02);
		} catch (java.lang.IllegalArgumentException e ) {
//			e.printStackTrace();
		}
		
//		if(indexofseries == 0) {
//			ohlcSeries = tmpohlcSeries;
//		} else {
//			dapanohlcSeries = tmpohlcSeries;
//		}
		tmpcandlestickDataset.addSeries(tmpohlcSeries);
		
		tmpohlcSeries.setNotify(true);
        tmpcandlestickDataset.setNotify(true);
        candlestickChart.setNotify(true);
        tmpohlcSeries.setNotify(false);
        tmpcandlestickDataset.setNotify(false);
        candlestickChart.setNotify(false);
	}
	/*
	 * 
	 */
	private Interval getTimeIntervalOfNodeTimeIntervalWithRequiredTimeInterval
	(LocalDate requiredstartday,LocalDate requiredendday,LocalDate nodestart,LocalDate nodeend)
	{
		if(nodestart == null)
		return null;
		
		DateTime nodestartdt= new DateTime(nodestart.getYear(), nodestart.getMonthValue(), nodestart.getDayOfMonth(), 0, 0, 0, 0);
		DateTime nodeenddt = new DateTime(nodeend.getYear(), nodeend.getMonthValue(), nodeend.getDayOfMonth(), 0, 0, 0, 0);
		Interval nodeinterval = new Interval(nodestartdt, nodeenddt);
		
		DateTime requiredstartdt= new DateTime(requiredstartday.getYear(), requiredstartday.getMonthValue(), requiredstartday.getDayOfMonth(), 0, 0, 0, 0);
		DateTime requiredenddt= new DateTime(requiredendday.getYear(), requiredendday.getMonthValue(), requiredendday.getDayOfMonth(), 0, 0, 0, 0);
		Interval requiredinterval = new Interval(requiredstartdt,requiredenddt);
		
		Interval overlapinterval = requiredinterval.overlap(nodeinterval);
		
		return overlapinterval;
	}
	/*
	 * 
	 */
	private List<QueKou> queKouTongJi ()
	{
		List<QueKou> qklist = new ArrayList<QueKou> ();
		
		OHLCSeries ohlcSeries = candlestickDataset.getSeries(0);
		Integer ohlccount = candlestickDataset.getSeries(0).getItemCount();
		if (ohlccount == null || ohlccount ==0 )
			return null;
		for(int j=1;j < ohlcSeries.getItemCount();j++) {
			
			OHLCItem kxiandatacurwk = (OHLCItem) ohlcSeries.getDataItem(j);
			RegularTimePeriod curperiod = kxiandatacurwk.getPeriod();
			LocalDate curstart = curperiod.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			
			OHLCItem kxiandatalastwk = (OHLCItem) ohlcSeries.getDataItem(j-1);
			
			Double curhigh = kxiandatacurwk.getHighValue();
			Double curlow = kxiandatacurwk.getLowValue();

			Iterator itr = qklist.iterator(); 
			while (itr.hasNext()) 
			{ 
				QueKou tmpqk = (QueKou)itr.next();
				if (!tmpqk.hasQueKouHuiBu() ) {
					String huibuinfo = tmpqk.checkQueKouHuiBu(curstart,curlow, curhigh);
					if(tmpqk.hasQueKouHuiBu() && tmpqk.getQueKouHuiBuDaysNumber() <= 5  )
						itr.remove(); 
				}
			} 
			
			//看看有没有产生新的缺口
			double lasthigh = kxiandatalastwk.getHighValue();
			double lastlow = kxiandatalastwk.getLowValue();
			
			
			if(curlow > lasthigh) {
				Double newqkup = curlow;
				Double newqkdown = lasthigh;
				QueKou newqk = new QueKou (this.curdisplayednode.getMyOwnCode(),curstart,newqkdown,newqkup,true);
				qklist.add(0, newqk);
			}
			else 
			if(curhigh < lastlow) {
				Double newqkup = lastlow;
				Double newqkdown = curhigh;
				QueKou newqk = new QueKou (this.curdisplayednode.getMyOwnCode(),curstart,newqkdown,newqkup,false);
				qklist.add(0, newqk);
			}
		}
		
		return qklist;
	}
	/*
	 * 
	 */
	public void displayMACDDivergenceToChart (String checkperiod)
	{
		if(this.getDispalyEndDate() == null)
			return;
		
		NodeXPeriodData nodexdata = this.curdisplayednode.getNodeXPeroidData(checkperiod);
		Multimap<LocalDate, LocalDate> mdvgc = nodexdata.isMacdButtomDivergenceInSpecificMonthRange(this.getDispalyEndDate(), 0, 4);
		
		if(mdvgc == null)
			return;
		
		if(mdvgc.size() >0)
			this.hasmacddivergence = true;
		
		Set<LocalDate> keysets = mdvgc.keySet();
		for(LocalDate basedate : keysets) {
			long basexPoint = new org.jfree.data.time.Day (java.sql.Date.valueOf(basedate)).getMiddleMillisecond();
			OHLCItem basebar = ((TDXNodesXPeriodDataForJFC)nodexdata).getSpecificDateOHLCData(basedate, 0);
			double baselowprice = basebar.getLowValue();
			
			XYPointerAnnotation xypointerannotation = new XYPointerAnnotation("背S", basexPoint, baselowprice, 200D);//
			xypointerannotation.setText("背S");
			xypointerannotation.setTextAnchor(TextAnchor.BOTTOM_RIGHT);
			xypointerannotation.setPaint(Color.yellow);
			this.candlestickChart.getXYPlot().addAnnotation(xypointerannotation);
			
			Collection<LocalDate> divdatelist = mdvgc.get(basedate);
			for(LocalDate divdate : divdatelist) {
				long divxPoint = new org.jfree.data.time.Day (java.sql.Date.valueOf(divdate)).getMiddleMillisecond();
				OHLCItem divbar = ((TDXNodesXPeriodDataForJFC)nodexdata).getSpecificDateOHLCData(divdate, 0);
				double divlowprice = divbar.getLowValue();
				
				//For creating line on mouse click.
//				XYLineAnnotation xYLineAnnotation = new XYLineAnnotation(basexPoint, baselowprice, divxPoint, divlowprice, new BasicStroke(1.0f), Color.blue);
				//For creating a pointer at a specific location
				XYPointerAnnotation xypointerannotationend = new XYPointerAnnotation("背E", divxPoint, divlowprice, 200D);//
				xypointerannotationend.setText("背E");
				xypointerannotationend.setTextAnchor(TextAnchor.BOTTOM_RIGHT);
				xypointerannotationend.setPaint(Color.yellow);
				this.candlestickChart.getXYPlot().addAnnotation(xypointerannotationend);
				
			}
		}
		
		keysets = null;
		mdvgc = null;

	}
	
	/*
	 * 
	 */
	private void displayQueKouToChart ()
	{
//		ohlcSeries.setNotify(false);
        candlestickDataset.setNotify(false);
        candlestickChart.setNotify(false);
        
        OHLCSeries ohlcSeries = candlestickDataset.getSeries(0);
		Integer ohlccount = candlestickDataset.getSeries(0).getItemCount();
		if (ohlccount == null || ohlccount ==0 )
			return ;

		List<QueKou> qklist = queKouTongJi ();
		for(QueKou tmpqk : qklist) {
			LocalDate qkdate = tmpqk.getQueKouDate();
			logger.debug("debug");
			LocalDate qkhuidate = tmpqk.getQueKouHuiBuDate();
			if(qkhuidate == null) {
				Double qkup = tmpqk.getQueKouUp();
				Double qkdown = tmpqk.getQueKouDown();
				
//				IntervalMarker marker = new IntervalMarker(qkup, qkdown);
//				marker.setLabelTextAnchor(TextAnchor.TOP_CENTER);
//				marker.setPaint(Color.yellow);
//				marker.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
//				this.candlestickChart.getXYPlot().addRangeMarker(marker);
				
//				setDaZiJinValueMarker(qkup);
//				setDaZiJinValueMarker(qkdown);
				continue;
			}
			
			java.sql.Date sqlqkdate = null;
			try {
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
				 sqlqkdate = new java.sql.Date(format.parse(qkdate.toString()).getTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			org.jfree.data.time.Day qkday = new org.jfree.data.time.Day (sqlqkdate);
			
			java.sql.Date sqlqkhbdate = null;
			try {
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
				 sqlqkhbdate = new java.sql.Date(format.parse(qkhuidate.toString()).getTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			org.jfree.data.time.Day qkhbday = new org.jfree.data.time.Day (sqlqkhbdate);
			
			Double qkup = tmpqk.getQueKouUp();
			//draw annotation
	        try {
		        double millisonemonth = qkday.getFirstMillisecond();
		        XYPointerAnnotation pointeronemonth = new XYPointerAnnotation(String.valueOf(qkup), millisonemonth, qkup, 0 );
		        pointeronemonth.setBaseRadius(0.0);
		        pointeronemonth.setTipRadius(25.0);
		        pointeronemonth.setFont(new Font("SansSerif", Font.BOLD, 9));
		        pointeronemonth.setPaint(Color.CYAN);
		        pointeronemonth.setTextAnchor(TextAnchor.CENTER);
				candlestickChart.getXYPlot().addAnnotation(pointeronemonth);
	        } catch (java.lang.NullPointerException e) {
	        	e.printStackTrace();
	        }
	        
	        try {
		        double millisonemonth = qkhbday.getFirstMillisecond();
		        XYPointerAnnotation pointeronemonth = new XYPointerAnnotation("回补", millisonemonth, qkup, 0 );
		        pointeronemonth.setBaseRadius(0.0);
		        pointeronemonth.setTipRadius(25.0);
		        pointeronemonth.setFont(new Font("SansSerif", Font.BOLD, 9));
		        if(tmpqk.getQueKouLeiXing() ) //上缺口被回补，绿色
		        	pointeronemonth.setPaint(Color.GREEN);
		        else
		        	pointeronemonth.setPaint(Color.PINK);
		        pointeronemonth.setTextAnchor(TextAnchor.CENTER);
				candlestickChart.getXYPlot().addAnnotation(pointeronemonth);
	        } catch (java.lang.NullPointerException e) {
	        	e.printStackTrace();
	        }
	        
//	        try{
//		        double millisonemonth = qkhbday.getFirstMillisecond();
//		        ValueMarker marker = new  ValueMarker(millisonemonth);  // position is the value on the axis
//				marker.setPaint(Color.YELLOW);
//				float[] dashPattern = { 6, 2 };
//				marker.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, dashPattern, 0));
//				marker.setAlpha(0.5f);
//				marker.setLabelAnchor(RectangleAnchor.TOP);
//				marker.setLabelTextAnchor(TextAnchor.TOP_CENTER);
//				marker.setLabelOffsetType(LengthAdjustmentType.CONTRACT);
//				marker.setLabel( "testestesttestest" ); // see JavaDoc for labels, colors, strokes
//				marker.setLabelPaint(Color.YELLOW);
//	//			candlestickChart.getXYPlot().addDomainMarker(marker,Layer.FOREGROUND);
//				candlestickChart.getXYPlot().addDomainMarker(marker);
//				
//				categorymarkerlist.add(marker);
//				
//				
//	    } catch (java.lang.NullPointerException e) {
//	        	e.printStackTrace();
//	    }
	        
	 
		}
		
		qklist = null;
		 ohlcSeries.setNotify(true);
	     candlestickDataset.setNotify(true);
	     candlestickChart.setNotify(true);
	     ohlcSeries.setNotify(false);
	     candlestickDataset.setNotify(false);
	     candlestickChart.setNotify(false);
	}
	/*
	 * 
	 */
	public void displayQueKou (boolean display)
	{
		if(display)
			this.displayhuibuquekou = true;
		else {
			this.displayhuibuquekou = false;
			
			List<XYPointerAnnotation> pointerlist = candlestickChart.getXYPlot().getAnnotations();
			for(XYPointerAnnotation pointer : pointerlist) 
				candlestickChart.getXYPlot().removeAnnotation(pointer);
			return;
		}
			
	}
	public void displayMACDDivergence (boolean display)
	{
		this.displaymacddivergence = display;
	}
	public Boolean hasMACDDivergence ()
	{
		return this.hasmacddivergence;
	}
	/*
	 * 
	 */
	private void setDaZiJinValueMarker (double d)
	{
		ValueMarker marker = new ValueMarker (d);
//		marker.setLabel("热点占比警戒线");
		marker.setLabelTextAnchor(TextAnchor.TOP_CENTER);
		marker.setPaint(Color.yellow);
		marker.setLabelAnchor(RectangleAnchor.BOTTOM_RIGHT);
		marker.setStroke(new BasicStroke(10.0f));
		this.candlestickChart.getXYPlot().addRangeMarker(1, marker,Layer.BACKGROUND);
	}
	/*
	 * 
	 */
	public void displayNodeNewsToGui( Collection<News> newszhishukeylists)
	{
		for (News tmpmeeting: newszhishukeylists ) {
		Paint drawcolor;
		
		LocalDate zhishudate = tmpmeeting.getStart();
		if(zhishudate == null)
			return;
		try {
			if(zhishudate.isBefore(this.getDispalyStartDate()) || zhishudate.isAfter(this.getDispalyEndDate() ))
				return;
		} catch (java.lang.NullPointerException e) {
			return;
		}
		
		java.sql.Date sqlqkhbdate = null;
		try {
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
			 sqlqkhbdate = new java.sql.Date(format.parse(zhishudate.toString()).getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (java.lang.NullPointerException e) {
			return;
		}
		org.jfree.data.time.Day markerday = new org.jfree.data.time.Day (sqlqkhbdate);
		
		try{
			long millisonemonth = markerday.getFirstMillisecond();
			double highestHigh = candlestickChart.getXYPlot().getRangeAxis(0).getRange().getUpperBound();
			double ylocation = ( highestHigh /1.02) * 1.01;
			XYPointerAnnotation pointertwomonth = new XYPointerAnnotation("\u004e" , millisonemonth, ylocation, 0);
	        pointertwomonth.setBaseRadius(0.0);
	        pointertwomonth.setTipRadius(25.0);
	        pointertwomonth.setFont(new Font("SansSerif", Font.BOLD, 9));
	        pointertwomonth.setPaint(Color.YELLOW);
	        pointertwomonth.setTextAnchor(TextAnchor.CENTER);
	        pointertwomonth.setToolTipText(tmpmeeting.getStart() + tmpmeeting.getTitle());
	        
			candlestickChart.getXYPlot().addAnnotation(pointertwomonth);
			
	    } catch (java.lang.NullPointerException e) {
	        	e.printStackTrace();
	    }
		
		}
	}
	/*
	 * 指数关键日期
	 */
	public void displayZhiShuGuanJianRiQiToGui(Collection<News> newszhishukeylists) 
	{
		if(this.getDispalyStartDate() == null)
			return;
		
		SystemConfigration syscon = SystemConfigration.getInstance();
		List<String> corezhishu = syscon.getCoreZhiShuCodeList();
		
		for (News tmpmeeting: newszhishukeylists ) {
		
		
		LocalDate zhishudate = tmpmeeting.getStart();
		if(zhishudate == null)
			return;
		if(zhishudate.isBefore(this.getDispalyStartDate()) || zhishudate.isAfter(this.getDispalyEndDate() ))
			return;
		
		String zhishucode = tmpmeeting.getNewsOwnerCodes();
		List<String> zhishulist = Splitter.on("|").omitEmptyStrings().splitToList(zhishucode); //0|000001|T1001|440101
		for(String tmpzhishu : zhishulist) {
			
			if(corezhishu.contains(tmpzhishu)) { //核心指数肯定要显示
				Color zhishucolor = null;
				if(tmpzhishu.equals("999999"))
					zhishucolor = Color.YELLOW;
				else if(tmpzhishu.equals("000016"))
					zhishucolor = new Color(102,178,255);
				else if(tmpzhishu.equals("399006"))
					zhishucolor = new Color(255,51,255);
				
				drawDefinedMarker (zhishudate,zhishucolor ); 
				
				LocalDate zhishuend = ((InsertedExternalNews)tmpmeeting).getEnd();
				if(zhishuend == null)
					break;
				if(zhishuend.isBefore(this.getDispalyStartDate()) || zhishuend.isAfter(this.getDispalyEndDate() ))
					break;
				drawDefinedMarker (zhishuend,zhishucolor );
				
				break;
			} else if( curdisplayednode.getMyOwnCode().equals(tmpzhishu) ) { //其他板块只在自己板块的时候显示
				
				Color bankuaicolor = new Color(51,255,153);
				drawDefinedMarker (zhishudate,bankuaicolor ); 
				
				LocalDate zhishuend =  ((InsertedExternalNews)tmpmeeting).getEnd();
				if(zhishuend == null)
					continue;
				if(zhishuend.isBefore(this.getDispalyStartDate()) || zhishuend.isAfter(this.getDispalyEndDate() ))
					continue;
				drawDefinedMarker (zhishuend,bankuaicolor );
			} else if( this.curdisplayedsupernode !=null && this.curdisplayedsupernode.getMyOwnCode().equals(tmpzhishu) ) {
				Color bankuaicolor = new Color(51,255,153);
				drawDefinedMarker (zhishudate,bankuaicolor ); 
				
				LocalDate zhishuend =  ((InsertedExternalNews)tmpmeeting).getEnd();
				if(zhishuend == null)
					continue;
				if(zhishuend.isBefore(this.getDispalyStartDate()) || zhishuend.isAfter(this.getDispalyEndDate() ))
					continue;
				drawDefinedMarker (zhishuend,bankuaicolor );
			}
			
		}
		}
		
	}
	/*
	 * 
	 */
	private void drawDefinedMarker (LocalDate zhishudate,Color drawcolor)
	{
		java.sql.Date sqlqkhbdate = null;
		try {
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
			 sqlqkhbdate = new java.sql.Date(format.parse(zhishudate.toString()).getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (java.lang.NullPointerException e) {
			return;
		}
		org.jfree.data.time.Day markerday = new org.jfree.data.time.Day (sqlqkhbdate);
		
		try{
	        long millisonemonth = markerday.getFirstMillisecond();
	        ValueMarker marker = new  ValueMarker(millisonemonth);  // position is the value on the axis
			marker.setPaint(drawcolor);
			float[] dashPattern = { 6, 2 };
			marker.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, dashPattern, 0));
			marker.setAlpha(0.5f);
			marker.setLabelAnchor(RectangleAnchor.TOP);
			marker.setLabelTextAnchor(TextAnchor.TOP_CENTER);
			marker.setLabelOffsetType(LengthAdjustmentType.CONTRACT);
//			marker.setLabel( tmpmeeting.getDescription() ); // see JavaDoc for labels, colors, strokes
			marker.setLabelPaint(drawcolor);
			
			
			candlestickChart.getXYPlot().addDomainMarker(marker);
			
			categorymarkerlist.add(marker);
			
			
	    } catch (java.lang.NullPointerException e) {
	        	e.printStackTrace();
	    } 
	}
	/*
	 * 显示某个阶段的最大最小值，现在不用了
	 */
//	private void displayRangeHighLowValue (boolean display) 
//	{
//		
//		ohlcSeries.setNotify(false);
//        candlestickDataset.setNotify(false);
//        candlestickChart.setNotify(false);
//        
//		if(ohlcSeries == null || ohlcSeries.getItemCount() == 0)
//			return ;
//		
//		if(!display) { //取消所有标记
//			List<XYPointerAnnotation> pointerlist = candlestickChart.getXYPlot().getAnnotations();
//			for(XYPointerAnnotation pointer : pointerlist) 
//				candlestickChart.getXYPlot().removeAnnotation(pointer);
//			return;
//		}
//		
//		//增加标记
//		double lowestLow = 10000.0;  double highestHigh = 0.0; 
//		Day lowdate = null, highdate = null;
//		
//		int itemcount = ohlcSeries.getItemCount();
//		for(int i=0;i<itemcount;i++) {
//				RegularTimePeriod dataitemp = ohlcSeries.getPeriod(i);
//				Double low = ( (OHLCItem)ohlcSeries.getDataItem(i) ).getLowValue();
//				Double high = ( (OHLCItem)ohlcSeries.getDataItem(i) ).getHighValue();
//				
//				if(low < lowestLow && low !=0) {//股价不可能为0，为0，说明停牌，无需计算
//					lowestLow = low;
//					lowdate = new Day(dataitemp.getEnd());
//				}
//				if(high > highestHigh && high !=0) {
//					highestHigh = high;
//					highdate = new Day(dataitemp.getEnd());
//				}
//		}
//		
//        //draw annotation
//        try {
//	        double millisonemonth = lowdate.getFirstMillisecond();
//	        XYPointerAnnotation pointeronemonth = new XYPointerAnnotation(String.valueOf(lowestLow), millisonemonth, lowestLow, 0 );
//	        pointeronemonth.setBaseRadius(0.0);
//	        pointeronemonth.setTipRadius(25.0);
//	        pointeronemonth.setFont(new Font("SansSerif", Font.BOLD, 9));
//	        pointeronemonth.setPaint(Color.CYAN);
//	        pointeronemonth.setTextAnchor(TextAnchor.CENTER);
//			candlestickChart.getXYPlot().addAnnotation(pointeronemonth);
//        } catch (java.lang.NullPointerException e) {
//        }
//		try {
//			double millistwomonth = highdate.getFirstMillisecond();
//	        XYPointerAnnotation pointertwomonth = new XYPointerAnnotation(String.valueOf(highestHigh) , millistwomonth, highestHigh, 0);
//	        pointertwomonth.setBaseRadius(0.0);
//	        pointertwomonth.setTipRadius(25.0);
//	        pointertwomonth.setFont(new Font("SansSerif", Font.BOLD, 9));
//	        pointertwomonth.setPaint(Color.YELLOW);
//	        pointertwomonth.setTextAnchor(TextAnchor.CENTER);
//			candlestickChart.getXYPlot().addAnnotation(pointertwomonth);
//		} catch (java.lang.NullPointerException e) {
//	    }
//		
//	     ohlcSeries.setNotify(true);
//	     candlestickDataset.setNotify(true);
//	     candlestickChart.setNotify(true);
//	     ohlcSeries.setNotify(false);
//	     candlestickDataset.setNotify(false);
//	     candlestickChart.setNotify(false);
//		
//	}
	/*
	 * 显示某天的最高值
	 */
	public void highLightSpecificDateCandleStick (LocalDate highlightweekdate,String period,boolean highlow)
	{
		OHLCSeries ohlcSeries = candlestickDataset.getSeries(0);
		Integer ohlccount = candlestickDataset.getSeries(0).getItemCount();
		if (ohlccount == null || ohlccount ==0 )
			return ;
		
		ohlcSeries.setNotify(false);
        candlestickDataset.setNotify(false);
        candlestickChart.setNotify(false);
        
        ((BanKuaiFengXiCandlestickRenderer)candlestickChart.getXYPlot().getRenderer(0)).setHighLightKTimeRange (highlightweekdate,period);
        ((BanKuaiFengXiCandlestickZhiShuRenderer)candlestickChart.getXYPlot().getRenderer(1)).setHighLightKTimeRange (highlightweekdate,period);
        
        if(!highlow) {
        	 ohlcSeries.setNotify(true);
             candlestickDataset.setNotify(true);
             candlestickChart.setNotify(true);
             ohlcSeries.setNotify(false);
             candlestickDataset.setNotify(false);
             candlestickChart.setNotify(false);
             
        	return ;
        }
		
      //如果本周有指数关键日期marker，同时高亮指数关键日期
        for(ValueMarker marker :this.categorymarkerlist ) {
        	double milliseconds =  marker.getValue(); 
        	LocalDate markerdate =  Instant.ofEpochMilli((long)milliseconds).atZone(ZoneId.systemDefault()).toLocalDate();
        	
        	Boolean result = CommonUtility.isInSameWeek(highlightweekdate, markerdate);
        	if(result) {
        		float[] dashPattern = { 6, 2 };
        		marker.setStroke(new BasicStroke(2, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10, dashPattern, 0));
    			marker.setAlpha(0.5f);
        	} else {
        		float[] dashPattern = { 6, 2 };
        		marker.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, dashPattern, 0));
    			marker.setAlpha(0.5f);
        	}
        	
        }
        //draw annotation 1/2月后的最高点
//		Day onemonthhighdate = null, twomonthhighdate=null;
//		double onemonthhigh = 0.0, onemonthlessclose = 0.0; //指定周期后1个月的最大值最小值
//		double twomonthhigh = 0.0, towmonthlessclose = 0.0; //指定周期后2个月的最大值最小值
//		int itemcount = ohlcSeries.getItemCount();
//		for(int i=0;i<itemcount;i++){
////			Double low  = ( (OHLCItem)ohlcSeries.getDataItem(i) ).getLowValue();
//			Double high = ( (OHLCItem)ohlcSeries.getDataItem(i) ).getHighValue();
//			
//				//高亮显示选中的那一周，本来要划一个长方形，觉得实现太麻烦，改在renderer里面高亮
//				//寻找1月/2月最大值
//				RegularTimePeriod dataitemp = ohlcSeries.getPeriod(i);
//				LocalDate endd = dataitemp.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//				long betweendays = Math.abs(endd.toEpochDay() - highlightweekdate.toEpochDay() );
//				if(endd.isAfter(highlightweekdate) && betweendays <= 30) {
//					if(high > onemonthhigh) {
//						onemonthhigh = high;	
//						onemonthhighdate = new Day(dataitemp.getEnd());
//					}
//				} 
//				if(endd.isAfter(highlightweekdate) && betweendays <= 60  ) {
//					if(high > twomonthhigh) {
//						twomonthhigh = high;
//						twomonthhighdate = new Day(dataitemp.getEnd());
//					}
//				}
//		}
	    
    
//        try {
//	        double millisonemonth = onemonthhighdate.getFirstMillisecond();
//	        XYPointerAnnotation pointeronemonth = new XYPointerAnnotation(String.valueOf(onemonthhigh), millisonemonth, onemonthhigh, 0 );
//	        pointeronemonth.setBaseRadius(0.0);
//	        pointeronemonth.setTipRadius(25.0);
//	        pointeronemonth.setFont(new Font("SansSerif", Font.BOLD, 9));
//	        pointeronemonth.setPaint(Color.YELLOW);
//	        pointeronemonth.setTextAnchor(TextAnchor.CENTER);
//			candlestickChart.getXYPlot().addAnnotation(pointeronemonth);
//        } catch (java.lang.NullPointerException e) {
//        	
//        }
//		try {
//			double millistwomonth = twomonthhighdate.getFirstMillisecond();
//	        XYPointerAnnotation pointertwomonth = new XYPointerAnnotation(String.valueOf(twomonthhigh) , millistwomonth, twomonthhigh, 0);
//	        pointertwomonth.setBaseRadius(0.0);
//	        pointertwomonth.setTipRadius(25.0);
//	        pointertwomonth.setFont(new Font("SansSerif", Font.BOLD, 9));
//	        pointertwomonth.setPaint(Color.YELLOW);
//	        pointertwomonth.setTextAnchor(TextAnchor.CENTER);
//			candlestickChart.getXYPlot().addAnnotation(pointertwomonth);
//		} catch (java.lang.NullPointerException e) {
//	        	
//	    }
        
        ohlcSeries.setNotify(true);
        candlestickDataset.setNotify(true);
        candlestickChart.setNotify(true);
        ohlcSeries.setNotify(false);
        candlestickDataset.setNotify(false);
        candlestickChart.setNotify(false);
	}
	/*
	 * 
	 */
	private void createEvents() 
	{
		chartPanel.addChartMouseListener(new ChartMouseListener() {

    	 
    	    @Override
			public void chartMouseMoved(ChartMouseEvent arg0) {
    	    	ToolTipManager.sharedInstance().setDismissDelay(60000); //让tooltips显示时间延长
			}

			@Override
			public void chartMouseClicked(ChartMouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		} );
		
		mntmbankuai.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mntmbankuai.setText("X 叠加板块指数");
				mntmzhishu.setText("叠加指定大盘指数");
				mntmamo.setText("叠加成交额");
				combinedZhiShuKXian ("bankuaizhisu");
			}
		});
		
		mntmzhishu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mntmbankuai.setText("叠加板块指数");
				mntmzhishu.setText("X 叠加指定大盘指数");
				mntmamo.setText("叠加成交额");
				combinedZhiShuKXian ("dapanzhishu");
			}
		});
		
		mntmamo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
//				mntmbankuai.setText("叠加板块指数");
//				mntmzhishu.setText("叠加指定大盘指数");
//				mntmamo.setText("X 叠加成交额");

				combinedAMO ();
			}
		});
	}
	protected void combinedAMO() 
	{
		mntmbankuai.setText("叠加板块指数");
		mntmzhishu.setText("叠加指定大盘指数");
		mntmamo.setText("X 叠加成交额");
		
		this.setNodeAMOData (this.curdisplayednode, this.globeperiod);
		
	}
	protected void combinedZhiShuKXian(String zhishu) 
	{
//		PropertyChangeEvent evt = new PropertyChangeEvent(this, SELECTED_PROPERTY, oldText, this.dateselected.toString() + this.tooltipselected );
//        this.firePropertyChange(evt);
		
//		String danpanzhishu = JOptionPane.showInputDialog(null,"请输入叠加的大盘指数","叠加指数", JOptionPane.QUESTION_MESSAGE);
		try {
			this.firePropertyChange(ZHISHU_PROPERTY, "", zhishu);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/*
	 * 
	 */
	private void createChartPanel() 
	{
		/**
		 * Creating candlestick subplot
		 */
		// Create OHLCSeriesCollection as a price dataset for candlestick chart
		candlestickDataset = new OHLCSeriesCollection();
//		ohlcSeries = new OHLCSeries("KXian");
//		candlestickDataset.addSeries(ohlcSeries);
		dayAxis = new DateAxis ();
		
		// Create candlestick chart renderer
		candlestickChart = ChartFactory.createCandlestickChart("", "","", candlestickDataset, true);
		
//		candlestickChart.getXYPlot().setDataset(0,candlestickDataset);
		BanKuaiFengXiCandlestickRenderer candlestickRenderer = new BanKuaiFengXiCandlestickRenderer ();//new CandlestickRenderer();
		candlestickChart.getXYPlot().setRenderer(0,candlestickRenderer);
		// Create candlestick chart priceAxis
				priceAxis = new NumberAxis();
//				priceAxis = new LogAxis("Price");
//				priceAxis.setAutoRangeIncludesZero(false);
//				priceAxis.setAutoRange(true);
				priceAxis.setUpperMargin(0.0);   
				priceAxis.setLowerMargin(0.0); 
		
		candlestickChart.getXYPlot().setRangeAxis(0,priceAxis);
		candlestickChart.getXYPlot().setDomainAxis(dayAxis);
		candlestickChart.getXYPlot().setDomainGridlinesVisible(true);
		candlestickChart.getXYPlot().setBackgroundPaint(Color.BLACK);
		candlestickChart.getXYPlot().setRangePannable(true);
//		candlestickChart.getXYPlot().setDomainGridlinePaint(Color.lightGray);
//		candlestickChart.getXYPlot().setRangeGridlinePaint(Color.lightGray);
		candlestickChart.removeLegend();
		candlestickChart.setNotify(true);
		
        //dapan zhishu/bankuai zhishu part
		
		dapancandlestickDataset = new OHLCSeriesCollection();
		candlestickChart.getXYPlot().setDataset(1,dapancandlestickDataset);
		candlestickChart.getXYPlot().setRenderer(1, new BanKuaiFengXiCandlestickZhiShuRenderer());
		//均线显示
		maDataSet = new TimeSeriesCollection();
		candlestickChart.getXYPlot().setDataset(3, maDataSet);
		candlestickChart.getXYPlot().setRenderer(3, new  XYLineAndShapeRenderer (true, false) );
		//成交额数据
		
		ValueAxis rangeAxis2 = new NumberAxis("");
		rangeAxis2.setUpperMargin(0.0);   
		rangeAxis2.setLowerMargin(0.0); 
		candlestickChart.getXYPlot().setRangeAxis(1, rangeAxis2);
      
		candlestickChart.getXYPlot().mapDatasetToRangeAxis(0, 0);
		candlestickChart.getXYPlot().mapDatasetToRangeAxis(1, 1);
		candlestickChart.getXYPlot().mapDatasetToRangeAxis(3, 0);
      // change the rendering order so the primary dataset appears "behind" the 
      // other datasets...
		candlestickChart.getXYPlot().setDatasetRenderingOrder(DatasetRenderingOrder.REVERSE);
		
		// Create new chart panel
		chartPanel = new ChartPanel(candlestickChart);
//		chartPanel.setPreferredSize(new java.awt.Dimension(1200, 500));
		// Enable zooming
//		chartPanel.setMouseZoomable(true);
//		chartPanel.setMouseWheelEnabled(true);
		chartPanel.setHorizontalAxisTrace(true);
	    chartPanel.setVerticalAxisTrace(true);
	    
		chartPanel.setPreferredSize(new Dimension(1400, 300));

	    this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
		standardChartTheme.setExtraLargeFont(new Font("隶书",Font.BOLD,10) );
		standardChartTheme.setRegularFont(new Font("隶书",Font.BOLD,10) );
		standardChartTheme.setLargeFont(new Font("隶书",Font.BOLD,10));
		ChartFactory.setChartTheme(standardChartTheme);
		
//		JPopupMenu popupMenu = new JPopupMenu();
		mntmamo = new JMenuItem("叠加成交额");
		mntmbankuai = new JMenuItem("叠加板块指数");
		mntmzhishu = new JMenuItem("叠加指定大盘指数");

		chartPanel.getPopupMenu().add(mntmamo);
		chartPanel.getPopupMenu().add(mntmbankuai);
		chartPanel.getPopupMenu().add(mntmzhishu);

		
		this.categorymarkerlist = new ArrayList<> ();
			
		this.add(chartPanel);
	}
	
	private void setPanelTitle (TDXNodes node, LocalDate requirestart,LocalDate requireend)
	{
		try {
			String nodecode = node.getMyOwnCode();
			String nodename = node.getMyOwnName();
			
			((TitledBorder)this.getBorder()).setTitle(nodecode+ nodename
	    												+ "从" + requirestart.toString() + "到" + requireend
	    												+ "日线K线");
		} catch (java.lang.NullPointerException e) {
			((TitledBorder)this.getBorder()).setTitle("");
		}
		
    	this.repaint();
	}


	public void resetDate()
	{
		
		candlestickDataset.removeAllSeries();
		((BanKuaiFengXiCandlestickRenderer)candlestickChart.getXYPlot().getRenderer()).setHighLightKTimeRange (null,null);
		
//		List<XYPointerAnnotation> pointerlist = candlestickChart.getXYPlot().getAnnotations();
//		for(XYPointerAnnotation pointer : pointerlist) 
//			candlestickChart.getXYPlot().removeAnnotation(pointer);
		
		candlestickChart.getXYPlot().clearAnnotations();
		
		dapancandlestickDataset.removeAllSeries();
		
		for(ValueMarker marker : this.categorymarkerlist) {
			candlestickChart.getXYPlot().removeDomainMarker(marker);
		}
		this.categorymarkerlist.clear();
		
		chartPanel.removeAll();
		this.setPanelTitle(null, null, null);
		
		try {
//			ohlcSeries.setNotify(true);
	        candlestickDataset.setNotify(true);
	        candlestickChart.setNotify(true);
//	        ohlcSeries.setNotify(false);
	        candlestickDataset.setNotify(false);
	        candlestickChart.setNotify(false);
		} catch (java.lang.NullPointerException e) {
			
		}
		
		this.maDataSet.removeAllSeries();
		
		this.hasmacddivergence = false;
	
	}
	@Override
	public void highLightSpecificBarColumn(LocalDate selecteddate) 
	{
		if(selecteddate == null)
			return;
		
		this.highLightSpecificDateCandleStick(selecteddate, NodeGivenPeriodDataItem.DAY, true);
	}
	@Override
	public void highLightSpecificBarColumn(Integer columnindex) {
		// TODO Auto-generated method stub
		
	}
	
}
/*
 * 显示大盘指数用的renderer 
 */
class BanKuaiFengXiCandlestickZhiShuRenderer extends CandlestickRenderer
{
	public BanKuaiFengXiCandlestickZhiShuRenderer ()
	{
		super ();
		
		DateFormat dateFormatter =  new SimpleDateFormat("yyyy-MM-dd");
		NumberFormat numberFormatter = NumberFormat.getInstance(Locale.CHINA);;
		CustomHighLowItemLabelGenerator tooltipgenerator = new CustomHighLowItemLabelGenerator (dateFormatter,numberFormatter);
		this.setBaseToolTipGenerator(tooltipgenerator);
		
		this.setBaseItemLabelGenerator(tooltipgenerator);
		this.setBaseItemLabelsVisible(true);
	}
	
	private final Paint  colorZhiShu = Color.GRAY.darker();
	private final Paint  colorSelected = Color.LIGHT_GRAY;
	private final Paint  colorFormerSelected = Color.GRAY.brighter();
	
	private LocalDate datebeselectedinweeklast;
	private LocalDate datebeselectedinweek;
	
	public void setHighLightKTimeRange (LocalDate selecteddate,String period)
	{
		this.datebeselectedinweeklast = this.datebeselectedinweek; 
		this.datebeselectedinweek = selecteddate;
	}
	@Override
    public Paint getItemPaint(int series, int item) 
	{
		return colorZhiShu;
	}
	@Override
    public void drawItem(Graphics2D g2, XYItemRendererState state,
            Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot,
            ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset,
            int series, int item, CrosshairState crosshairState, int pass)
    {
     
    	OHLCSeriesCollection highLowData = (OHLCSeriesCollection) dataset;
    	
    	int tempcount = highLowData.getSeriesCount();
        OHLCSeries ser = highLowData.getSeries(series);
//        RegularTimePeriod per = (Day)ser.getPeriod(item);
        Day per = (Day)ser.getPeriod(item);
        Date date = per.getEnd();
        LocalDate ldate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().with(DayOfWeek.FRIDAY);

        if(  this.datebeselectedinweeklast !=null && ldate.equals(this.datebeselectedinweeklast.with(DayOfWeek.FRIDAY) ) ) {
        	LocalDate tmpdate = datebeselectedinweeklast.with(DayOfWeek.FRIDAY) ;
        	setUpPaint(colorFormerSelected);
            setDownPaint(colorFormerSelected);
        }
        else if(  datebeselectedinweek != null && ldate.equals(datebeselectedinweek.with(DayOfWeek.FRIDAY) ) ) {
        	LocalDate tmpdate = datebeselectedinweek.with(DayOfWeek.FRIDAY) ;
        	setUpPaint(colorSelected);
            setDownPaint(colorSelected);
        } else {
    		setUpPaint(colorZhiShu);
            setDownPaint(colorZhiShu);
        }

        // call parent method
        super.drawItem(g2, state, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item, crosshairState, pass);
    }   
}
/*
 * 
 */
class BanKuaiFengXiCandlestickRenderer extends CandlestickRenderer 
{  //https://stackoverflow.com/questions/29152688/is-it-possible-to-show-hollow-candle-in-candlestick-jfreechart

	public BanKuaiFengXiCandlestickRenderer ()
	{
		super ();
		setDrawVolume(false);
        setUpPaint(colorUnknown); // use unknown color if error
        setDownPaint(colorUnknown); // use unknown color if error
        super.setAutoWidthMethod(CandlestickRenderer.WIDTHMETHOD_AVERAGE); //CandlestickRenderer.WIDTHMETHOD_AVERAGE,
        
        DateFormat dateFormatter =  new SimpleDateFormat("yyyy-MM-dd");
		NumberFormat numberFormatter = NumberFormat.getInstance(Locale.CHINA);;
		CustomHighLowItemLabelGenerator tooltipgenerator = new CustomHighLowItemLabelGenerator (dateFormatter,numberFormatter);
		this.setBaseToolTipGenerator(tooltipgenerator);
		
		this.setBaseItemLabelGenerator(tooltipgenerator);
		this.setBaseItemLabelsVisible(true);
	}
	
	private static Logger logger = Logger.getLogger(BanKuaiFengXiCandlestickRenderer.class);
	private static final long serialVersionUID = 1L;
	
	//不同的涨跌用不同的K线颜色标记，和TDX一样
	private final Paint colorZhangTing = Color.RED; 
	private final Paint colorDaDaZhang = Color.YELLOW;
	private final Paint colorDaZhang = Color.YELLOW.brighter();
	private final Paint colorRaising = Color.RED;
	
	private final Paint colorDieTing = Color.GREEN.darker();
	private final Paint colorDaDaDie = Color.GREEN;
	private final Paint colorDaDie = Color.GREEN.brighter();
	
	private final Paint colorFalling = Color.CYAN.brighter();
	private final Paint colorUnknown = Color.GRAY;
	private final Paint colorTransparent = Color.BLACK;
	private final Paint colorSelected = new Color (51,153,255);
	private final Paint colorFormerSelected = Color.BLUE;
	
	private LocalDate datebeselectedinweek ;
	private LocalDate datebeselectedinweeklast ;

	@Override
    public Paint getItemPaint(int series, int item) 
	{
		OHLCSeriesCollection highLowData = (OHLCSeriesCollection) getPlot().getDataset(series);
		
//         int tempcount = highLowData.getSeriesCount();
         OHLCSeries ser = highLowData.getSeries(series);

         Day per = (Day)ser.getPeriod(item);
         Date date = per.getEnd();
         LocalDate ldate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().with(DayOfWeek.FRIDAY);
         try {
        	 Number curClose = highLowData.getClose(series, item);
             Number prevClose = highLowData.getClose(series, item>0 ? item-1 : 0);
             double closepercent = (curClose.doubleValue() - prevClose.doubleValue())/prevClose.doubleValue();
             
             
             if(  this.datebeselectedinweeklast !=null && ldate.equals(this.datebeselectedinweeklast.with(DayOfWeek.FRIDAY) ) )
            	 return colorFormerSelected;
             else 
             if(  datebeselectedinweek !=null && ldate.equals(datebeselectedinweek.with(DayOfWeek.FRIDAY) ) )
            	 return colorSelected;
             else 
             if(prevClose.doubleValue() <=  curClose.doubleValue() && closepercent>=0.095 )
            	 return this.colorZhangTing;
             else 
             if (prevClose.doubleValue() <=  curClose.doubleValue() && closepercent<0.095 && closepercent >=0.075) {
                 return colorDaDaZhang;
             } else if (prevClose.doubleValue() <=  curClose.doubleValue() && closepercent<0.075 && closepercent >=0.05) {
                 return colorDaZhang;
             } else if (prevClose.doubleValue() <=  curClose.doubleValue() && closepercent<0.05) {
                 return colorRaising;
                 
             } else if (prevClose.doubleValue() >  curClose.doubleValue() && closepercent <= -0.090) {
                 return colorDieTing;
             } else if (prevClose.doubleValue() >  curClose.doubleValue() && closepercent > -0.090 && closepercent <= -0.07) {
                 return colorDaDaDie;
             } else if (prevClose.doubleValue() >  curClose.doubleValue() && closepercent > -0.070 && closepercent <= -0.05) {
                 return colorDaDie;
             } else {
            	 return colorFalling;
             }
         } catch (java.lang.NullPointerException e) {
        	 e.printStackTrace();
//        	 Number curClose = highLowData.getClose(series, item);
//             Number prevClose = highLowData.getClose(series, item>0 ? item-1 : 0);
//             if (prevClose.doubleValue() <=  curClose.doubleValue()) 
//                 return colorRaising;
//             else 
//                 return colorFalling;
         }
		return null;
         
    }

    @Override
    public void drawItem(Graphics2D g2, XYItemRendererState state,
            Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot,
            ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset,
            int series, int item, CrosshairState crosshairState, int pass)
    {
    	OHLCSeriesCollection highLowData = (OHLCSeriesCollection) dataset;
    	
    	int tempcount = highLowData.getSeriesCount();
        OHLCSeries ser = highLowData.getSeries(series);
//        RegularTimePeriod per = (Day)ser.getPeriod(item);
        Day per = (Day)ser.getPeriod(item);
        Date date = per.getEnd();
        LocalDate ldate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().with(DayOfWeek.FRIDAY);
   
        double yOpen = highLowData.getOpenValue(series, item);
        double yClose = highLowData.getCloseValue(series, item);  
        Number prevClose = highLowData.getClose(series, item>0 ? item-1 : 0);
        double closepercent = (yClose - prevClose.doubleValue())/prevClose.doubleValue();

        if(  this.datebeselectedinweeklast !=null && ldate.equals(this.datebeselectedinweeklast.with(DayOfWeek.FRIDAY) ) ) {
        	LocalDate tmpdate = datebeselectedinweeklast.with(DayOfWeek.FRIDAY) ;
        	setUpPaint(colorFormerSelected);
            setDownPaint(colorFormerSelected);
        }
        else if(  datebeselectedinweek != null && ldate.equals(datebeselectedinweek.with(DayOfWeek.FRIDAY) ) ) {
        	LocalDate tmpdate = datebeselectedinweek.with(DayOfWeek.FRIDAY) ;
        	setUpPaint(colorSelected);
            setDownPaint(colorSelected);
        }
        // set color for filled candle
        else if (yClose >= yOpen && closepercent >= 0.095 ) {
        	setUpPaint(colorZhangTing);
            setDownPaint(colorZhangTing);
            
        } else if (yClose >= yOpen && closepercent < 0.095 && closepercent>=0.075 ) {
        	setUpPaint(colorDaDaZhang);
            setDownPaint(colorDaDaZhang);
        } else if (yClose >= yOpen && closepercent < 0.075 && closepercent >= 0.05) {
        	setUpPaint(colorDaZhang);
            setDownPaint(colorDaZhang);
        } else if (yClose >= yOpen && closepercent < 0.05 ) {
        	setUpPaint(colorTransparent);
            setDownPaint(colorTransparent);
        }

        // set color for hollow (not filled) candle
        else if (yClose < yOpen && closepercent <= -0.090 ) {
        	setUpPaint(colorDieTing);
            setDownPaint(colorDieTing);
        } else if (yClose < yOpen && closepercent > -0.090 && closepercent <= -0.070 ) {
        	setUpPaint(colorDaDaDie);
            setDownPaint(colorDaDaDie);
        } else if (yClose < yOpen && closepercent > -0.070 && closepercent <= -0.050 ) {
        	setUpPaint(colorDaDie);
            setDownPaint(colorDaDie);
        } else {
        	setUpPaint(colorRaising);
            setDownPaint(colorFalling);
        }

        // call parent method
        super.drawItem(g2, state, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item, crosshairState, pass);
    }   

	
	public void setHighLightKTimeRange (LocalDate selecteddate,String period)
	{
		this.datebeselectedinweeklast = this.datebeselectedinweek; 
		this.datebeselectedinweek = selecteddate;
	}
}

