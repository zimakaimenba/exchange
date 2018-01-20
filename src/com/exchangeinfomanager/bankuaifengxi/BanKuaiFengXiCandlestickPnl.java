package com.exchangeinfomanager.bankuaifengxi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.text.AttributedString;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
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
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.data.xy.XYDataset;

import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.DaPan;
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
public class BanKuaiFengXiCandlestickPnl extends JPanel 
{
	private static Logger logger = Logger.getLogger(BanKuaiFengXiCandlestickPnl.class);
	protected BkChanYeLianTreeNode curdisplayednode;
	
	private static final DateFormat READABLE_TIME_FORMAT = new SimpleDateFormat("kk:mm:ss");

	private OHLCSeries ohlcSeries;
//	private TimeSeries volumeSeries;

	private ChenJiaoZhanBiInGivenPeriod candelChartIntervalFirstPrint = null;

	private Integer shoulddisplayedmonthnum;

	private BanKuaiDbOperation bkdbopt;
	private SystemConfigration sysconfig;
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

		 sysconfig = SystemConfigration.getInstance();
		 this.shoulddisplayedmonthnum = sysconfig.banKuaiFengXiMonthRange() -3;
		 bkdbopt = new BanKuaiDbOperation ();
	}
	
	 
	
	public void setNodeCandleStickDate (BkChanYeLianTreeNode node, LocalDate displayedenddate)
	{
		this.curdisplayednode = node;
		LocalDate requireend = displayedenddate.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = displayedenddate.with(DayOfWeek.MONDAY).minus(this.shoulddisplayedmonthnum,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		
		ohlcSeries = new OHLCSeries("KPrice");
		ArrayList<ChenJiaoZhanBiInGivenPeriod> daydata = node.getDayChenJiaoErZhanBiInGivenPeriod();
		double lowestLow =10000.0;  double highestHigh =0.0; 
		for(ChenJiaoZhanBiInGivenPeriod dayrecord : daydata) {
			Double open = dayrecord.getOpenPrice();
			Double low = dayrecord.getLowPrice();
			Double close = dayrecord.getClosePrice();
			Double high = dayrecord.getHighPrice();
			
			logger.debug(open + "," + high.toString() + "," + low + "," + close);
			LocalDate actionday = dayrecord.getRecordsDayofEndofWeek ();
			int year = actionday.getYear();
			int month = actionday.getMonthValue();
			int day = actionday.getDayOfMonth();
			ohlcSeries.add(new Day(day,month,year), open, high, low, close);
			
			if(low < lowestLow && low !=0) //股价不可能为0，为0，说明停牌，无需计算
				lowestLow = low;
			if(high > highestHigh && high !=0)
				highestHigh = high;
		}

		candlestickChart.getXYPlot().getRangeAxis().setRange(lowestLow*0.95, highestHigh*1.05);
		
		candlestickDataset.addSeries(ohlcSeries);
		
		chartPanel.setHorizontalAxisTrace(true); //十字显示
        chartPanel.setVerticalAxisTrace(true);

        setPanelTitle ( node,  displayedenddate);
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
//		candlestickChart.getXYPlot().setDomainGridlinePaint(Color.lightGray);
//		candlestickChart.getXYPlot().setRangeGridlinePaint(Color.lightGray);
//		axis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
		
		candlestickChart.removeLegend();
		candlestickChart.setNotify(true);
		
		// Create new chart panel
		chartPanel = new ChartPanel(candlestickChart);
//		chartPanel.setPreferredSize(new java.awt.Dimension(1200, 500));
		// Enable zooming
		chartPanel.setMouseZoomable(true);
		chartPanel.setMouseWheelEnabled(true);
		
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
		standardChartTheme.setExtraLargeFont(new Font("隶书",Font.BOLD,10) );
		standardChartTheme.setRegularFont(new Font("隶书",Font.BOLD,10) );
		standardChartTheme.setLargeFont(new Font("隶书",Font.BOLD,10));
		ChartFactory.setChartTheme(standardChartTheme);
		this.add(chartPanel);
	}
	
	private void setPanelTitle (BkChanYeLianTreeNode node, LocalDate displayedenddate)
	{
		String nodecode = node.getMyOwnCode();
		String nodename = node.getMyOwnName();
		
		LocalDate requireend = displayedenddate.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = displayedenddate.with(DayOfWeek.MONDAY).minus(this.shoulddisplayedmonthnum,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);

		((TitledBorder)this.getBorder()).setTitle(nodecode+ nodename
    												+ "从" + requireend.toString() + "到" + requirestart
    												+ "日线K线");
    	this.repaint();
    	this.setToolTipText(nodecode+ nodename );
	}


	public void resetDate()
	{
//		ohlcSeries = new OHLCSeries("Price");
//		candlestickDataset.addSeries(ohlcSeries);
		candlestickDataset.removeAllSeries();
		
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
        super.setUseOutlinePaint(true);
	}
	
	private static Logger logger = Logger.getLogger(BanKuaiFengXiCandlestickRenderer.class);
	private static final long serialVersionUID = 1L;
	private Comparable hightime;
	
	private final Paint colorRaising = Color.RED;
	private final Paint colorFalling = Color.GREEN;
	private final Paint colorUnknown = Color.GRAY;
	private final Paint colorTransparent = Color.BLACK;
	
	private LocalDate datebeselectedinweek = LocalDate.of(2017,8,16 );

	@Override
    public Paint getItemPaint(int series, int item) 
	{
		OHLCSeriesCollection highLowData = (OHLCSeriesCollection) getPlot().getDataset(series);
		
         int tempcount = highLowData.getSeriesCount();
         OHLCSeries ser = highLowData.getSeries(series);
//         RegularTimePeriod per = (Day)ser.getPeriod(item);
         Day per = (Day)ser.getPeriod(item);
         Date date = per.getEnd();
         LocalDate ldate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().with(DayOfWeek.FRIDAY);
         LocalDate tmpdate = datebeselectedinweek.with(DayOfWeek.FRIDAY) ;
        
        	 
         
         
//         ComparableObjectItem dataitem = ser.getDataItem(item);
//         Number kekxx = highLowData.getX(series, item);
//         logger.debug(kekxx);
        
        Number curClose = highLowData.getClose(series, item);
        Number prevClose = highLowData.getClose(series, item>0 ? item-1 : 0);
        if(  ldate.equals(datebeselectedinweek.with(DayOfWeek.FRIDAY) ) )
       	 return Color.BLUE;
        else if (prevClose.doubleValue() <=  curClose.doubleValue()) {
            return Color.red;
        } else {
            return Color.green;
        }
    }


    @Override
    public void drawItem(Graphics2D g2, XYItemRendererState state,
            Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot,
            ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset,
            int series, int item, CrosshairState crosshairState, int pass) {

    	OHLCSeriesCollection highLowData = (OHLCSeriesCollection) dataset;
    	
    	int tempcount = highLowData.getSeriesCount();
        OHLCSeries ser = highLowData.getSeries(series);
//        RegularTimePeriod per = (Day)ser.getPeriod(item);
        Day per = (Day)ser.getPeriod(item);
        Date date = per.getEnd();
        LocalDate ldate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().with(DayOfWeek.FRIDAY);
        LocalDate tmpdate = datebeselectedinweek.with(DayOfWeek.FRIDAY) ;
        
       	 
        
        double yOpen = highLowData.getOpenValue(series, item);
        double yClose = highLowData.getCloseValue(series, item);        

        if(  ldate.equals(datebeselectedinweek.with(DayOfWeek.FRIDAY) ) ) {
        	setUpPaint(colorTransparent);
            setDownPaint(colorTransparent);
        }
        // set color for filled candle
        else if (yClose >= yOpen) {
            setUpPaint(colorRaising);
            setDownPaint(colorFalling);
        }

        // set color for hollow (not filled) candle
        else {
            setUpPaint(colorFalling);
            setDownPaint(colorFalling);
        }

        // call parent method
        super.drawItem(g2, state, dataArea, info, plot, domainAxis, rangeAxis, dataset, series, item, crosshairState, pass);
    }   

	
	public void setHighLightKTimeRange (Comparable selecteddate)
	{
		this.hightime = selecteddate;
	}
}

