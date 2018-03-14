package com.exchangeinfomanager.bankuaifengxi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;
import java.text.AttributedString;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
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
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
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
import org.jfree.ui.TextAnchor;

import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.DaPan;
import com.exchangeinfomanager.asinglestockinfo.StockGivenPeriodDataItem;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;

//import com.fx.jfree.chart.model.Trade;
//import com.fx.jfree.chart.utils.MathUtils;
//import com.fx.jfree.chart.utils.TimeUtils;

/**
 * The Class JfreeCandlestickChart.
 * 
 * @author ashraf
 */
@SuppressWarnings("serial")
public class BanKuaiFengXiCandlestickPnl extends JPanel implements BarChartPanelDataChangedListener
{
	private static Logger logger = Logger.getLogger(BanKuaiFengXiCandlestickPnl.class);
	protected BkChanYeLianTreeNode curdisplayednode;

	private OHLCSeries ohlcSeries;
//	private TimeSeries volumeSeries;

//	private ChenJiaoZhanBiInGivenPeriod candelChartIntervalFirstPrint = null;
//
//	private Integer shoulddisplayedmonthnum;
//
//	private BanKuaiDbOperation bkdbopt;
//	private SystemConfigration sysconfig;
	private OHLCSeriesCollection candlestickDataset;

//	private XYPlot candlestickSubplot;

	private NumberAxis priceAxis;
//	private LogAxis priceAxis;

	private ChartPanel chartPanel;

	private JFreeChart candlestickChart;

	private DateAxis dayAxis;

	public BanKuaiFengXiCandlestickPnl() 
	{
		// Create new chart
		 createChartPanel();

//		 sysconfig = SystemConfigration.getInstance();
//		 this.shoulddisplayedmonthnum = sysconfig.banKuaiFengXiMonthRange() -3;
//		 bkdbopt = new BanKuaiDbOperation ();
	}
	
	@Override
	public void updatedDate(BkChanYeLianTreeNode node, LocalDate date, int difference,String period) 
	{
		// TODO Auto-generated method stub
		if(period.equals(StockGivenPeriodDataItem.DAY))
			date = date.plus(difference,ChronoUnit.DAYS);
		else if(period.equals(StockGivenPeriodDataItem.WEEK))
			date = date.plus(difference,ChronoUnit.WEEKS);
		else if(period.equals(StockGivenPeriodDataItem.MONTH))
			date = date.plus(difference,ChronoUnit.MONTHS);
		
		LocalDate requireend = date.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = date.with(DayOfWeek.MONDAY).minus(9,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		
		setNodeCandleStickDate ( node,  requirestart,  requireend, period);
	}
	/*
	 * 显示指定周期的K线
	 */
	public void setNodeCandleStickDate (BkChanYeLianTreeNode node, LocalDate nodestartday, LocalDate nodeendday,String period)
	{
		this.curdisplayednode = node;
		NodeXPeriodDataBasic nodexdata = node.getNodeXPeroidData(period);
		ohlcSeries = nodexdata.getRangeOHLCData(nodestartday, nodeendday);

		double lowestLow =10000.0;  double highestHigh =0.0; 
		int itemcount = ohlcSeries.getItemCount();
		for(int i=0;i<itemcount;i++){
				Double low = ( (OHLCItem)ohlcSeries.getDataItem(i) ).getLowValue();
				Double high = ( (OHLCItem)ohlcSeries.getDataItem(i) ).getHighValue();
				
				if(low < lowestLow && low !=0) //股价不可能为0，为0，说明停牌，无需计算
					lowestLow = low;
				if(high > highestHigh && high !=0)
					highestHigh = high;
		}
		
		candlestickChart.getXYPlot().getRangeAxis().setRange(lowestLow*0.98, highestHigh*1.02);
		
		candlestickDataset.addSeries(ohlcSeries);
		
		chartPanel.setHorizontalAxisTrace(true); //十字显示
        chartPanel.setVerticalAxisTrace(true);
        

        setPanelTitle ( node, nodestartday, nodeendday);
	}

	/*
	 * 显示K线的同时突出显示指定周的K线，并计算后期指定周期内的最大值最小值
	 */
	public void setNodeCandleStickDate (BkChanYeLianTreeNode node, LocalDate nodestartday, LocalDate nodeendday,LocalDate highlightweekdate,String period)
	{
		this.curdisplayednode = node;
		NodeXPeriodDataBasic nodexdata = node.getNodeXPeroidData(period);
		ohlcSeries = nodexdata.getRangeOHLCData(nodestartday, nodeendday);

		double lowestLow =10000.0;  double highestHigh =0.0; 
		
		Day onemonthhighdate = null, twomonthhighdate=null;
		double onemonthhigh = 0.0, onemonthlessclose = 0.0; //指定周期后1个月的最大值最小值
		double twomonthhigh = 0.0, towmonthlessclose = 0.0; //指定周期后2个月的最大值最小值
		int itemcount = ohlcSeries.getItemCount();
		for(int i=0;i<itemcount;i++){
			Double low  = ( (OHLCItem)ohlcSeries.getDataItem(i) ).getLowValue();
			Double high = ( (OHLCItem)ohlcSeries.getDataItem(i) ).getHighValue();
			
				//为显示寻找最大最小值，以便Y轴做合适调整
				if(low < lowestLow && low !=0) //股价不可能为0，为0，说明停牌，无需计算
					lowestLow = low;
				if(high > highestHigh && high !=0)
					highestHigh = high;
				
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
		
		//设置显示范围，避免某些情况下太小
		candlestickChart.getXYPlot().getRangeAxis().setRange(lowestLow*0.98, highestHigh*1.05);
		
		candlestickDataset.addSeries(ohlcSeries);

		chartPanel.setHorizontalAxisTrace(true); //十字显示
        chartPanel.setVerticalAxisTrace(true);
        
      //高亮显示选中的那一周，本来要划一个长方形，觉得实现太麻烦，改在renderer里面高亮
//         XYShapeAnnotation shapeAnnotation = new XYShapeAnnotation(
//                new Rectangle2D.Double( fromX - xFactor, fromY - yFactor, xFactor+xFactor, yFactor+yFactor ),
//                20, Color.BLUE );
////        shapeAnnotation.setToolTipText( toolTip );
//        candlestickChart.getXYPlot().addAnnotation( shapeAnnotation );
        ((BanKuaiFengXiCandlestickRenderer)candlestickChart.getXYPlot().getRenderer()).setHighLightKTimeRange (highlightweekdate);
        
        //draw annotation
        double millisonemonth = onemonthhighdate.getFirstMillisecond();
        final XYPointerAnnotation pointeronemonth = new XYPointerAnnotation(String.valueOf(onemonthhigh), millisonemonth, onemonthhigh, 0 );
        pointeronemonth.setBaseRadius(0.0);
        pointeronemonth.setTipRadius(25.0);
        pointeronemonth.setFont(new Font("SansSerif", Font.BOLD, 9));
        pointeronemonth.setPaint(Color.YELLOW);
        pointeronemonth.setTextAnchor(TextAnchor.CENTER);
		candlestickChart.getXYPlot().addAnnotation(pointeronemonth);
		
		double millistwomonth = twomonthhighdate.getFirstMillisecond();
        final XYPointerAnnotation pointertwomonth = new XYPointerAnnotation(String.valueOf(twomonthhigh) , millistwomonth, twomonthhigh, 0);
        pointertwomonth.setBaseRadius(0.0);
        pointertwomonth.setTipRadius(25.0);
        pointertwomonth.setFont(new Font("SansSerif", Font.BOLD, 9));
        pointertwomonth.setPaint(Color.YELLOW);
        pointertwomonth.setTextAnchor(TextAnchor.CENTER);
		candlestickChart.getXYPlot().addAnnotation(pointertwomonth);

        setPanelTitle ( node,  nodestartday,nodeendday);
        
        candlestickChart.fireChartChanged ();
		
	}
		
	private void createChartPanel() 
	{
		/**
		 * Creating candlestick subplot
		 */
		// Create OHLCSeriesCollection as a price dataset for candlestick chart
		candlestickDataset = new OHLCSeriesCollection();
		
//		ohlcSeries = new OHLCSeries("KXian");
//		candlestickDataset.addSeries(ohlcSeries);
		
		// Create candlestick chart priceAxis
		priceAxis = new NumberAxis();
//		priceAxis = new LogAxis("Price");
//		priceAxis.setAutoRangeIncludesZero(false);
//		priceAxis.setAutoRange(true);
		priceAxis.setUpperMargin(0.0);   
		priceAxis.setLowerMargin(0.0);  
		
		dayAxis = new DateAxis (); 
        
		// Create candlestick chart renderer
		BanKuaiFengXiCandlestickRenderer candlestickRenderer = new BanKuaiFengXiCandlestickRenderer ();//new CandlestickRenderer();

		candlestickChart = ChartFactory.createCandlestickChart("", "","", candlestickDataset, true);
		candlestickChart.getXYPlot().setRenderer(candlestickRenderer);
		candlestickChart.getXYPlot().setRangeAxis(priceAxis);
		candlestickChart.getXYPlot().setDomainAxis(dayAxis);
		candlestickChart.getXYPlot().setDomainGridlinesVisible(true);
		candlestickChart.getXYPlot().setBackgroundPaint(Color.BLACK);
//		candlestickChart.getXYPlot().setDomainGridlinePaint(Color.lightGray);
//		candlestickChart.getXYPlot().setRangeGridlinePaint(Color.lightGray);
		candlestickChart.removeLegend();
		candlestickChart.setNotify(true);
		
		// Create new chart panel
		chartPanel = new ChartPanel(candlestickChart);
//		chartPanel.setPreferredSize(new java.awt.Dimension(1200, 500));
		// Enable zooming
		chartPanel.setMouseZoomable(true);
		chartPanel.setMouseWheelEnabled(true);
		chartPanel.setHorizontalAxisTrace(false);
	    chartPanel.setVerticalAxisTrace(false);
	    chartPanel.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent arg0) {
				
			}
		});
		
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
		standardChartTheme.setExtraLargeFont(new Font("隶书",Font.BOLD,10) );
		standardChartTheme.setRegularFont(new Font("隶书",Font.BOLD,10) );
		standardChartTheme.setLargeFont(new Font("隶书",Font.BOLD,10));
		ChartFactory.setChartTheme(standardChartTheme);
		this.add(chartPanel);
	}
	
	private void setPanelTitle (BkChanYeLianTreeNode node, LocalDate requirestart,LocalDate requireend)
	{
		String nodecode = node.getMyOwnCode();
		String nodename = node.getMyOwnName();
		
		((TitledBorder)this.getBorder()).setTitle(nodecode+ nodename
    												+ "从" + requirestart.toString() + "到" + requireend
    												+ "日线K线");
    	this.repaint();
    	this.setToolTipText(nodecode+ nodename );
	}


	public void resetDate()
	{
//		ohlcSeries = new OHLCSeries("Price");
//		candlestickDataset.addSeries(ohlcSeries);
		candlestickDataset.removeAllSeries();
		((BanKuaiFengXiCandlestickRenderer)candlestickChart.getXYPlot().getRenderer()).setHighLightKTimeRange (null);
		List<XYPointerAnnotation> pointerlist = candlestickChart.getXYPlot().getAnnotations();
		for(XYPointerAnnotation pointer : pointerlist) 
			candlestickChart.getXYPlot().removeAnnotation(pointer);
		
	}


}

class BanKuaiFengXiCandlestickRenderer extends CandlestickRenderer 
{  //https://stackoverflow.com/questions/29152688/is-it-possible-to-show-hollow-candle-in-candlestick-jfreechart

	public BanKuaiFengXiCandlestickRenderer ()
	{
		setDrawVolume(false);
        setUpPaint(colorUnknown); // use unknown color if error
        setDownPaint(colorUnknown); // use unknown color if error
        super.setAutoWidthMethod(CandlestickRenderer.WIDTHMETHOD_AVERAGE); //CandlestickRenderer.WIDTHMETHOD_AVERAGE,
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
	private final Paint colorSelected = Color.BLUE;
	
	private LocalDate datebeselectedinweek ;

	@Override
    public Paint getItemPaint(int series, int item) 
	{
		OHLCSeriesCollection highLowData = (OHLCSeriesCollection) getPlot().getDataset(series);
		
         int tempcount = highLowData.getSeriesCount();
         OHLCSeries ser = highLowData.getSeries(series);

         Day per = (Day)ser.getPeriod(item);
         Date date = per.getEnd();
         LocalDate ldate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().with(DayOfWeek.FRIDAY);
         try {
//        	 LocalDate tmpdate = datebeselectedinweek.with(DayOfWeek.FRIDAY) ;
        	 
        	 Number curClose = highLowData.getClose(series, item);
             Number prevClose = highLowData.getClose(series, item>0 ? item-1 : 0);
             double closepercent = (curClose.doubleValue() - prevClose.doubleValue())/prevClose.doubleValue();
             
             if(  datebeselectedinweek !=null && ldate.equals(datebeselectedinweek.with(DayOfWeek.FRIDAY) ) )
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

        if(  datebeselectedinweek != null && ldate.equals(datebeselectedinweek.with(DayOfWeek.FRIDAY) ) ) {
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

	
	public void setHighLightKTimeRange (LocalDate selecteddate)
	{
		this.datebeselectedinweek = selecteddate;
	}
}

