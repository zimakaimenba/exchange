package com.exchangeinfomanager.bankuaifengxi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;

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
public class JfreeCandlestickChart extends JPanel 
{

	protected BkChanYeLianTreeNode curdisplayednode;
	
	private static final DateFormat READABLE_TIME_FORMAT = new SimpleDateFormat("kk:mm:ss");

	private OHLCSeries ohlcSeries;
//	private TimeSeries volumeSeries;

	private static final int MIN = 60000;
	// Every minute
	private int timeInterval = 1;
	private ChenJiaoZhanBiInGivenPeriod candelChartIntervalFirstPrint = null;
	private double open = 0.0;
	private double close = 0.0;
	private double low = 0.0;
	private double high = 0.0;
	private long volume = 0;

	private Integer shoulddisplayedmonthnum;

	private BanKuaiDbOperation bkdbopt;
	private SystemConfigration sysconfig;

	public JfreeCandlestickChart() 
	{
		StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
		standardChartTheme.setExtraLargeFont(new Font("隶书",Font.BOLD,20) );
		standardChartTheme.setRegularFont(new Font("隶书",Font.BOLD,20) );
		standardChartTheme.setLargeFont(new Font("隶书",Font.BOLD,20));
		ChartFactory.setChartTheme(standardChartTheme);
		
		// Create new chart
		final JFreeChart candlestickChart = createChart();
		// Create new chart panel
		final ChartPanel chartPanel = new ChartPanel(candlestickChart);
		chartPanel.setPreferredSize(new java.awt.Dimension(1200, 500));
		// Enable zooming
		chartPanel.setMouseZoomable(true);
		chartPanel.setMouseWheelEnabled(true);
		add(chartPanel, BorderLayout.CENTER);
		
		 sysconfig = SystemConfigration.getInstance();
		this.shoulddisplayedmonthnum = sysconfig.banKuaiFengXiMonthRange() -3;
		bkdbopt = new BanKuaiDbOperation ();
	}
	
	public void setNodeCandleStickDate (BkChanYeLianTreeNode node, LocalDate displayedenddate1)
	{
		
	}

	private JFreeChart createChart() {

		/**
		 * Creating candlestick subplot
		 */
		// Create OHLCSeriesCollection as a price dataset for candlestick chart
		OHLCSeriesCollection candlestickDataset = new OHLCSeriesCollection();
		ohlcSeries = new OHLCSeries("Price");
		candlestickDataset.addSeries(ohlcSeries);
		// Create candlestick chart priceAxis
		NumberAxis priceAxis = new NumberAxis("Price");
		priceAxis.setAutoRangeIncludesZero(false);
		// Create candlestick chart renderer
		CandlestickRenderer candlestickRenderer = new CandlestickRenderer(CandlestickRenderer.WIDTHMETHOD_AVERAGE,
				false, new CustomHighLowItemLabelGenerator(new SimpleDateFormat("kk:mm"), new DecimalFormat("0.000")));
		// Create candlestickSubplot
		XYPlot candlestickSubplot = new XYPlot(candlestickDataset, null, priceAxis, candlestickRenderer);
		candlestickSubplot.setBackgroundPaint(Color.white);

		/**
		 * Creating volume subplot
		 */
		// creates TimeSeriesCollection as a volume dataset for volume chart
//		TimeSeriesCollection volumeDataset = new TimeSeriesCollection();
//		volumeSeries = new TimeSeries("Volume");
//		volumeDataset.addSeries(volumeSeries);
//		// Create volume chart volumeAxis
//		NumberAxis volumeAxis = new NumberAxis("Volume");
//		volumeAxis.setAutoRangeIncludesZero(false);
//		// Set to no decimal
//		volumeAxis.setNumberFormatOverride(new DecimalFormat("0"));
//		// Create volume chart renderer
//		XYBarRenderer timeRenderer = new XYBarRenderer();
//		timeRenderer.setShadowVisible(false);
//		timeRenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator("Volume--> Time={1} Size={2}",
//				new SimpleDateFormat("kk:mm"), new DecimalFormat("0")));
//		// Create volumeSubplot
//		XYPlot volumeSubplot = new XYPlot(volumeDataset, null, volumeAxis, timeRenderer);
//		volumeSubplot.setBackgroundPaint(Color.white);

		/**
		 * Create chart main plot with two subplots (candlestickSubplot,
		 * volumeSubplot) and one common dateAxis
		 */
		// Creating charts common dateAxis
		DateAxis dateAxis = new DateAxis("Time");
		dateAxis.setDateFormatOverride(new SimpleDateFormat("kk:mm"));
		// reduce the default left/right margin from 0.05 to 0.02
		dateAxis.setLowerMargin(0.02);
		dateAxis.setUpperMargin(0.02);
		// Create mainPlot
		CombinedDomainXYPlot mainPlot = new CombinedDomainXYPlot(dateAxis);
		mainPlot.setGap(10.0);
		mainPlot.add(candlestickSubplot, 3);
//		mainPlot.add(volumeSubplot, 1);
		mainPlot.setOrientation(PlotOrientation.VERTICAL);

		JFreeChart chart = new JFreeChart("", JFreeChart.DEFAULT_TITLE_FONT, mainPlot, true);
		chart.removeLegend();
		return chart;
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
	/**
	 * Fill series with data.
	 *
	 * @param t the t
	 */
	public void addCandel(Day time, double o, double h, double l, double c, long v) {
		try {
			// Add bar to the data. Let's repeat the same bar
//			FixedMillisecond t = new FixedMillisecond(
//					READABLE_TIME_FORMAT.parse(TimeUtils.convertToReadableTime(time)));
			ohlcSeries.add(time, o, h, l, c);
//			volumeSeries.add(time, v);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Aggregate the (open, high, low, close, volume) based on the predefined time interval (1 minute)
	 *
	 * @param t the t
	 */
//	public void onTrade(Trade t) {
//		double price = t.getPrice();
//		if (candelChartIntervalFirstPrint != null) {
//			long time = t.getTime();
//			if (timeInterval == (int) ((time / MIN) - (candelChartIntervalFirstPrint.getTime() / MIN))) {
//				// Set the period close price
//				close = MathUtils.roundDouble(price, MathUtils.TWO_DEC_DOUBLE_FORMAT);
//				// Add new candle
//				addCandel(time, open, high, low, close, volume);
//				// Reset the intervalFirstPrint to null
//				candelChartIntervalFirstPrint = null;
//			} else {
//				// Set the current low price
//				if (MathUtils.roundDouble(price, MathUtils.TWO_DEC_DOUBLE_FORMAT) < low)
//					low = MathUtils.roundDouble(price, MathUtils.TWO_DEC_DOUBLE_FORMAT);
//
//				// Set the current high price
//				if (MathUtils.roundDouble(price, MathUtils.TWO_DEC_DOUBLE_FORMAT) > high)
//					high = MathUtils.roundDouble(price, MathUtils.TWO_DEC_DOUBLE_FORMAT);
//
//				volume += t.getSize();
//			}
//		} else {
//			// Set intervalFirstPrint
//			candelChartIntervalFirstPrint = t;
//			// the first trade price in the day (day open price)
//			open = MathUtils.roundDouble(price, MathUtils.TWO_DEC_DOUBLE_FORMAT);
//			// the interval low
//			low = MathUtils.roundDouble(price, MathUtils.TWO_DEC_DOUBLE_FORMAT);
//			// the interval high
//			high = MathUtils.roundDouble(price, MathUtils.TWO_DEC_DOUBLE_FORMAT);
//			// set the initial volume
//			volume = t.getSize();
//		}
//	}

}

