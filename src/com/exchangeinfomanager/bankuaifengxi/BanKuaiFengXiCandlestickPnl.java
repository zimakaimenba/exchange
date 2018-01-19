package com.exchangeinfomanager.bankuaifengxi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Paint;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
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
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.HighLowRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
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

	protected BkChanYeLianTreeNode curdisplayednode;
	
	private static final DateFormat READABLE_TIME_FORMAT = new SimpleDateFormat("kk:mm:ss");

	private OHLCSeries ohlcSeries;
//	private TimeSeries volumeSeries;

	private static final int MIN = 60000;
	// Every minute
	private int timeInterval = 1;
	private ChenJiaoZhanBiInGivenPeriod candelChartIntervalFirstPrint = null;
//	private double open = 0.0;
//	private double close = 0.0;
//	private double low = 0.0;
//	private double high = 0.0;
//	private long volume = 0;

	private static Logger logger = Logger.getLogger(BanKuaiFengXiCandlestickPnl.class);
	private Integer shoulddisplayedmonthnum;

	private BanKuaiDbOperation bkdbopt;
	private SystemConfigration sysconfig;
	private OHLCSeriesCollection candlestickDataset;

	private XYPlot candlestickSubplot;

//	private NumberAxis priceAxis;
	private LogAxis priceAxis;

	private ChartPanel chartPanel;

	private JFreeChart candlestickChart;

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
		
		ohlcSeries = new OHLCSeries("Price");
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
//		priceAxis.setRange(lowestLow*0.95, highestHigh*1.05);
		
		chartPanel.setHorizontalAxisTrace(true); //十字显示
        chartPanel.setVerticalAxisTrace(true);
		
		candlestickDataset.addSeries(ohlcSeries);
		candlestickSubplot.setDataset(candlestickDataset);
		setPanelTitle ();
	}
	
//	private JFreeChart createChart () {
//		JFreeChart chart = ChartFactory.createHighLowChart(
//	            "OHLC Demo 3",
//	            "Time",
//	            "Price",
//	            dataset,
//	            true
//	        );
//	        XYPlot plot = (XYPlot) chart.getPlot();
//	        HighLowRenderer renderer = (HighLowRenderer) plot.getRenderer();
//	        renderer.setBaseStroke(new BasicStroke(2.0f));
//	        renderer.setSeriesPaint(0, Color.blue);
//
//	        DateAxis axis = (DateAxis) plot.getDomainAxis();
//	        axis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
//
//	        NumberAxis yAxis1 = (NumberAxis) plot.getRangeAxis();
//	        yAxis1.setAutoRangeIncludesZero(false);
//
//	        NumberAxis yAxis2 = new NumberAxis("Price 2");
//	        yAxis2.setAutoRangeIncludesZero(false);
//	        plot.setRangeAxis(1, yAxis2);
//	        plot.setDataset(1, createDataset2());
//	        plot.setRenderer(1, new CandlestickRenderer(10.0));
//	        plot.mapDatasetToRangeAxis(1, 1);
//	        ChartUtilities.applyCurrentTheme(chart);
//	        return chart;
//	}

	private void createChartPanel() {

		/**
		 * Creating candlestick subplot
		 */
		// Create OHLCSeriesCollection as a price dataset for candlestick chart
		candlestickDataset = new OHLCSeriesCollection();
		
		ohlcSeries = new OHLCSeries("KXian");
		candlestickDataset.addSeries(ohlcSeries);
		
		
		
		// Create candlestick chart priceAxis
//		priceAxis = new NumberAxis("Price");
		priceAxis = new LogAxis("Price");
//		priceAxis.setAutoRangeIncludesZero(false);
		priceAxis.setAutoRange(true);
		priceAxis.setUpperMargin(0.0);   
		priceAxis.setLowerMargin(0.0);  
		
        
		// Create candlestick chart renderer
		CandlestickRenderer candlestickRenderer = new CandlestickRenderer();
//		CandlestickRenderer candlestickRenderer = new CandlestickRenderer(CandlestickRenderer.WIDTHMETHOD_AVERAGE,
//				false, new CustomHighLowItemLabelGenerator(new SimpleDateFormat("kk:mm"), new DecimalFormat("0.000")));
//		candlestickRenderer.setUseOutlinePaint(true);
		candlestickRenderer.setAutoWidthMethod(CandlestickRenderer.WIDTHMETHOD_SMALLEST); //CandlestickRenderer.WIDTHMETHOD_AVERAGE,
		candlestickRenderer.setDownPaint(new Color(0xFFFFFF));
		candlestickRenderer.setUpPaint(new Color(0x000000));
		
		// Create candlestickSubplot
		candlestickSubplot = new XYPlot(candlestickDataset, null, priceAxis, candlestickRenderer);
//		candlestickSubplot.setBackgroundPaint(Color.);
		candlestickSubplot.setDomainGridlinesVisible(true);
		candlestickSubplot.setDomainGridlinePaint(Color.lightGray);
		candlestickSubplot.setRangeGridlinePaint(Color.lightGray);


		candlestickChart = ChartFactory.createCandlestickChart("", "","", candlestickDataset, true);
		candlestickChart.removeLegend();
		
		// Create new chart panel
		chartPanel = new ChartPanel(candlestickChart);
//		chartPanel.setPreferredSize(new java.awt.Dimension(1200, 500));
		// Enable zooming
		chartPanel.setMouseZoomable(false);
		chartPanel.setMouseWheelEnabled(false);
		
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
		standardChartTheme.setExtraLargeFont(new Font("隶书",Font.BOLD,20) );
		standardChartTheme.setRegularFont(new Font("隶书",Font.BOLD,20) );
		standardChartTheme.setLargeFont(new Font("隶书",Font.BOLD,20));
		ChartFactory.setChartTheme(standardChartTheme);
		this.add(chartPanel);
	}
	
	private void setPanelTitle ()
	{
		String nodecode = curdisplayednode.getMyOwnCode();
		String nodename = curdisplayednode.getMyOwnName();
		
//		Date endday = CommonUtility.getLastDayOfWeek(displayedenddate );
//    	Date startday = CommonUtility.getFirstDayOfWeek( CommonUtility.getDateOfSpecificMonthAgo(displayedenddate ,sysconfig.banKuaiFengXiMonthRange() ) );
    	((TitledBorder)this.getBorder()).setTitle(nodecode+ nodename  );
//															+ "从" + CommonUtility.formatDateYYYY_MM_DD(startday) 
//															+ "到" + CommonUtility.formatDateYYYY_MM_DD(endday) );
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

class BanKuaiFengXiCandlestickRenderer extends CandlestickRenderer {

    @Override
    public Paint getItemPaint(int row, int column) {

        //determine up or down candle 
        XYDataset dataset = getPlot().getDataset();
        OHLCSeriesCollection highLowData = (OHLCSeriesCollection) dataset;
        int series = row, item = column;
        Number yOpen = highLowData.getOpen(series, item);
        Number yClose = highLowData.getClose(series, item);
        boolean isUpCandle = yClose.doubleValue() > yOpen.doubleValue();

        //return the same color as that used to fill the candle
        if (isUpCandle) {
            return getUpPaint();
        }
        else {
            return getDownPaint();
        }
    }
}

