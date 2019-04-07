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
import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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
import org.jfree.data.ComparableObjectItem;
import org.jfree.data.general.SeriesDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimePeriodAnchor;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
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

import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.InsertedMeeting;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Meeting;
import com.exchangeinfomanager.bankuaifengxi.BarChartPanelDataChangedListener;
import com.exchangeinfomanager.bankuaifengxi.BarChartPanelHightLightColumnListener;
import com.exchangeinfomanager.bankuaifengxi.QueKou;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartPnl;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.nodexdata.NodeXPeriodDataBasic;
import com.exchangeinfomanager.nodes.nodexdata.TDXNodeGivenPeriodDataItem;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;

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

	private OHLCSeries ohlcSeries;
	private OHLCSeriesCollection candlestickDataset;
	
	private OHLCSeries dapanohlcSeries;
	private OHLCSeriesCollection dapancandlestickDataset;
	
	

	private NumberAxis priceAxis;
	private ChartPanel chartPanel;

	private JFreeChart candlestickChart;

	private DateAxis dayAxis;
	private JMenuItem mntmbankuai;
//	private JMenuItem mntmshzhishu;
//	private JMenuItem mntmszzhishu;
//	private JMenuItem mntmcybzhishu;
	private JMenuItem mntmzhishu;
	private boolean displayhuibuquekou;
	private JMenuItem mntmguanjiandate;

	public TDXNodes getCurDisplayedNode ()
	{
		return this.curdisplayednode;
	}
	public LocalDate getDispalyStartDate ()
	{
		OHLCItem kxiandatacurwk = (OHLCItem) ohlcSeries.getDataItem(0);
		RegularTimePeriod curperiod = kxiandatacurwk.getPeriod();
		LocalDate curstart = curperiod.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//		LocalDate curend = curperiod.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		
		return curstart;
	}
	public LocalDate getDispalyEndDate ()
	{
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
		setNodeCandleStickDate ( node,  startdate,  enddate, period , 0);
		
		if(node.getType() == TDXNodes.TDXGG && this.displayhuibuquekou )
			displayQueKouToChart ();
		
		if(this.checkDatesShunXu (startdate,enddate) )
			setPanelTitle ( node, startdate, enddate);
		else
			setPanelTitle ( node,  enddate,startdate);
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
		
		setNodeCandleStickDate ( node,  requirestart,  requireend, period , 0);
		setNodeCandleStickDate ( superbk,  requirestart,  requireend, period , 1);
		
		if(node.getType() == TDXNodes.TDXGG && this.displayhuibuquekou )
			displayQueKouToChart ();
		
		setPanelTitle ( node, requirestart, requireend);
	}
	/*
	 * 
	 */
	public void chongdieZhiShu (TDXNodes superzhishu, String period)
	{
		//计算当前node显示的时间范围
		RegularTimePeriod ohlcdate = ( (OHLCItem)ohlcSeries.getDataItem(0) ).getPeriod();
	}
	/*
	 * 个股和板块的K线可以重叠显示 
	 */
	private void setNodeCandleStickDate(TDXNodes node, LocalDate requirestart, LocalDate requireend,
			String period, int indexofseries) 
	{
		// TODO Auto-generated method stub
		OHLCSeries tmpohlcSeries ;
		OHLCSeriesCollection tmpcandlestickDataset;
//		if(indexofseries == 0) {
			tmpcandlestickDataset = (OHLCSeriesCollection)candlestickChart.getXYPlot().getDataset(indexofseries);
//			tmpohlcSeries = tmpcandlestickDataset.getSeries(indexofseries);
//		}
		
		NodeXPeriodDataBasic nodexdata = node.getNodeXPeroidData(period);
		tmpohlcSeries = nodexdata.getRangeOHLCData(requirestart, requireend);
		
		tmpohlcSeries.setNotify(false);
		tmpcandlestickDataset.setNotify(false);
		
		double lowestLow =10000.0;  double highestHigh = 0.0; 
		
		int itemcount = tmpohlcSeries.getItemCount();
		for(int i=0;i<itemcount;i++) {
//				RegularTimePeriod dataitemp = ohlcSeries.getPeriod(i);
				Double low = ( (OHLCItem)tmpohlcSeries.getDataItem(i) ).getLowValue();
				Double high = ( (OHLCItem)tmpohlcSeries.getDataItem(i) ).getHighValue();
				
				if(low < lowestLow && low !=0) {//股价不可能为0，为0，说明停牌，无需计算
					lowestLow = low;
				}
				if(high > highestHigh && high !=0) {
					highestHigh = high;
				}
		}
		try {
			candlestickChart.getXYPlot().getRangeAxis(indexofseries).setRange(lowestLow*0.98, highestHigh*1.02);
		} catch (java.lang.IllegalArgumentException e ) {
//			e.printStackTrace();
		}
		
		if(indexofseries == 0) {
			ohlcSeries = tmpohlcSeries;
		} else {
			dapanohlcSeries = tmpohlcSeries;
		}
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
	private Boolean checkDatesShunXu (LocalDate date1, LocalDate date2) 
	{
		if (date1.isBefore(date2))
			return true;
		else
			return false;
	}
	/*
	 * 
	 */
	private List<QueKou> queKouTongJi ()
	{
		List<QueKou> qklist = new ArrayList<QueKou> ();
		
//		NodeXPeriodDataBasic nodexdata = curdisplayednode.getNodeXPeroidData(period);
//		OHLCSeries nodeohlc = nodexdata.getOHLCData();
		for(int j=1;j < ohlcSeries.getItemCount();j++) {
			
			OHLCItem kxiandatacurwk = (OHLCItem) ohlcSeries.getDataItem(j);
			RegularTimePeriod curperiod = kxiandatacurwk.getPeriod();
			LocalDate curstart = curperiod.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//			LocalDate curend = curperiod.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			
			OHLCItem kxiandatalastwk = (OHLCItem) ohlcSeries.getDataItem(j-1);
			
			Double curhigh = kxiandatacurwk.getHighValue();
			Double curlow = kxiandatacurwk.getLowValue();

			Iterator itr = qklist.iterator(); 
			while (itr.hasNext()) 
			{ 
				QueKou tmpqk = (QueKou)itr.next();
				if (!tmpqk.isQueKouHuiBu() ) {
					String huibuinfo = tmpqk.checkQueKouHuiBu(curstart,curlow, curhigh);
					if(tmpqk.isQueKouHuiBu() && tmpqk.getQueKouHuiBuDaysNumber() <= 5  )
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
	private void displayQueKouToChart ()
	{
		ohlcSeries.setNotify(false);
        candlestickDataset.setNotify(false);
        candlestickChart.setNotify(false);
        
		if(ohlcSeries == null || ohlcSeries.getItemCount() == 0)
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
	        }
			
		}
		
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
	public void updateZhiShuKeyDates(Collection<InsertedMeeting> zhishukeylists) 
	{
		java.sql.Date sqlqkdate = null;
		try {
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
			 sqlqkdate = new java.sql.Date(format.parse("2019-02-26".toString()).getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		org.jfree.data.time.Day qkday = new org.jfree.data.time.Day (sqlqkdate);
		
		CategoryMarker marker = new CategoryMarker(qkday);  // position is the value on the axis
		marker.setPaint(Color.MAGENTA);
		marker.setAlpha(0.5f);
		marker.setLabelAnchor(RectangleAnchor.TOP);
		marker.setLabelTextAnchor(TextAnchor.TOP_CENTER);
		marker.setLabelOffsetType(LengthAdjustmentType.CONTRACT);
//		marker.setLabel(String.valueOf(curyear)); // see JavaDoc for labels, colors, strokes
		marker.setDrawAsLine(true);
		this.candlestickChart.getXYPlot().addDomainMarker(marker,Layer.FOREGROUND);
//		this.categorymarkerlist.add(marker);
		
      try {
        double millisonemonth = qkday.getFirstMillisecond();
        XYPointerAnnotation pointeronemonth = new XYPointerAnnotation(String.valueOf("test"), millisonemonth, 2998, 0 );
        pointeronemonth.setBaseRadius(0.0);
        pointeronemonth.setTipRadius(25.0);
        pointeronemonth.setFont(new Font("SansSerif", Font.BOLD, 9));
        pointeronemonth.setPaint(Color.CYAN);
        pointeronemonth.setTextAnchor(TextAnchor.CENTER);
		candlestickChart.getXYPlot().addAnnotation(pointeronemonth);
		
		
		setDaZiJinValueMarker (millisonemonth);
    } catch (java.lang.NullPointerException e) {
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
	public void highLightSpecificDateCandleStickWithHighLowValue (LocalDate highlightweekdate,String period,boolean highlow)
	{
		if(ohlcSeries == null )
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
		
		Day onemonthhighdate = null, twomonthhighdate=null;
		double onemonthhigh = 0.0, onemonthlessclose = 0.0; //指定周期后1个月的最大值最小值
		double twomonthhigh = 0.0, towmonthlessclose = 0.0; //指定周期后2个月的最大值最小值
		
		int itemcount = ohlcSeries.getItemCount();
		for(int i=0;i<itemcount;i++){
//			Double low  = ( (OHLCItem)ohlcSeries.getDataItem(i) ).getLowValue();
			Double high = ( (OHLCItem)ohlcSeries.getDataItem(i) ).getHighValue();
			
				//高亮显示选中的那一周，本来要划一个长方形，觉得实现太麻烦，改在renderer里面高亮
				//寻找1月/2月最大值
				RegularTimePeriod dataitemp = ohlcSeries.getPeriod(i);
				LocalDate endd = dataitemp.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				long betweendays = Math.abs(endd.toEpochDay() - highlightweekdate.toEpochDay() );
				if(endd.isAfter(highlightweekdate) && betweendays <= 30) {
					if(high > onemonthhigh) {
						onemonthhigh = high;	
						onemonthhighdate = new Day(dataitemp.getEnd());
					}
				} 
				if(endd.isAfter(highlightweekdate) && betweendays <= 60  ) {
					if(high > twomonthhigh) {
						twomonthhigh = high;
						twomonthhighdate = new Day(dataitemp.getEnd());
					}
				}
		}
        
        //draw annotation 1/2月后的最高点
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
				combinedZhiShuKXian ("bankuaizhisu");
			}
		});
		
		mntmzhishu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mntmbankuai.setText("叠加板块指数");
				mntmzhishu.setText("X 叠加指定大盘指数");
				combinedZhiShuKXian ("dapanzhishu");
			}
		});
		
		mntmguanjiandate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				mntmguanjiandate.setText("X 叠加指数关键日期");
				combinedZhiShuKXian ("zhishuguanjianriqi");
			}
		});
		
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
//		candlestickChart.getXYPlot().setDomainGridlinePaint(Color.lightGray);
//		candlestickChart.getXYPlot().setRangeGridlinePaint(Color.lightGray);
		candlestickChart.removeLegend();
		candlestickChart.setNotify(true);
		
        //dapan zhishu/bankuai zhishu part
		
		dapancandlestickDataset = new OHLCSeriesCollection();
		candlestickChart.getXYPlot().setDataset(1,dapancandlestickDataset);
		candlestickChart.getXYPlot().setRenderer(1, new BanKuaiFengXiCandlestickZhiShuRenderer());
		ValueAxis rangeAxis2 = new NumberAxis("");
		rangeAxis2.setUpperMargin(0.0);   
		rangeAxis2.setLowerMargin(0.0); 
		candlestickChart.getXYPlot().setRangeAxis(1, rangeAxis2);
      
		candlestickChart.getXYPlot().mapDatasetToRangeAxis(0, 0);
		candlestickChart.getXYPlot().mapDatasetToRangeAxis(1, 1); 
      // change the rendering order so the primary dataset appears "behind" the 
      // other datasets...
		candlestickChart.getXYPlot().setDatasetRenderingOrder(DatasetRenderingOrder.REVERSE);
		
		// Create new chart panel
		chartPanel = new ChartPanel(candlestickChart);
//		chartPanel.setPreferredSize(new java.awt.Dimension(1200, 500));
		// Enable zooming
//		chartPanel.setMouseZoomable(true);
//		chartPanel.setMouseWheelEnabled(true);
//		chartPanel.setHorizontalAxisTrace(false);
//	    chartPanel.setVerticalAxisTrace(false);
	    
		chartPanel.setPreferredSize(new Dimension(1400, 250));

	    this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
		standardChartTheme.setExtraLargeFont(new Font("隶书",Font.BOLD,10) );
		standardChartTheme.setRegularFont(new Font("隶书",Font.BOLD,10) );
		standardChartTheme.setLargeFont(new Font("隶书",Font.BOLD,10));
		ChartFactory.setChartTheme(standardChartTheme);
		
//		JPopupMenu popupMenu = new JPopupMenu();
		mntmbankuai = new JMenuItem("叠加板块指数");
		mntmzhishu = new JMenuItem("叠加指定大盘指数");
		mntmguanjiandate = new JMenuItem("叠加指数关键日期");
//			popupMenu.add(mntmNewMenuItem);
		chartPanel.getPopupMenu().add(mntmbankuai);
		chartPanel.getPopupMenu().add(mntmzhishu);
		chartPanel.getPopupMenu().add(mntmguanjiandate);
			
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
		
		chartPanel.removeAll();
		this.setPanelTitle(null, null, null);
		
		try {
			ohlcSeries.setNotify(true);
	        candlestickDataset.setNotify(true);
	        candlestickChart.setNotify(true);
	        ohlcSeries.setNotify(false);
	        candlestickDataset.setNotify(false);
	        candlestickChart.setNotify(false);
		} catch (java.lang.NullPointerException e) {
			
		}
	
	}
	@Override
	public void highLightSpecificBarColumn(LocalDate selecteddate) 
	{
		if(selecteddate == null)
			return;
		
		LocalDate selectdate1 = CommonUtility.formateStringToDate(selecteddate.toString());
		this.highLightSpecificDateCandleStickWithHighLowValue(selectdate1, TDXNodeGivenPeriodDataItem.DAY, true);
		
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
//        super.setBaseToolTipGenerator(null);
//        super.setUseOutlinePaint(true);
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
             else if(  datebeselectedinweek !=null && ldate.equals(datebeselectedinweek.with(DayOfWeek.FRIDAY) ) )
            	 return colorSelected;
             else if(prevClose.doubleValue() <=  curClose.doubleValue() && closepercent>=0.095 )
            	 return this.colorZhangTing;
             else if (prevClose.doubleValue() <=  curClose.doubleValue() && closepercent<0.095 && closepercent >=0.075) {
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

