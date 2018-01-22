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
import java.time.Period;
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
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.annotations.XYShapeAnnotation;
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
import org.jfree.ui.TextAnchor;

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
	
	/*
	 * ��ʾK�ߵ�ͬʱͻ����ʾָ���ܵ�K�ߣ����������ָ�������ڵ����ֵ��Сֵ
	 */
	public void setNodeCandleStickDate (BkChanYeLianTreeNode node, LocalDate nodestartday, LocalDate nodeendday,LocalDate highlightweekdate)
	{
		this.curdisplayednode = node;
//		LocalDate requireend = displayedenddate.with(DayOfWeek.SATURDAY);
//		LocalDate requirestart = displayedenddate.with(DayOfWeek.MONDAY).minus(this.shoulddisplayedmonthnum,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		
		ohlcSeries = new OHLCSeries("KPrice");
//		ArrayList<ChenJiaoZhanBiInGivenPeriod> daydata = node.getWkChenJiaoErZhanBiInGivenPeriod();
		double lowestLow =10000.0;  double highestHigh =0.0; 
		
		Day onemonthhighdate = null, twomonthhighdate=null;
		double onemonthhigh = 0.0, onemonthlessclose = 0.0; //ָ�����ں�1���µ����ֵ��Сֵ
		double twomonthhigh = 0.0, towmonthlessclose = 0.0; //ָ�����ں�2���µ����ֵ��Сֵ
		for(LocalDate tmpdate = nodestartday;tmpdate.isBefore( nodeendday) || tmpdate.isEqual(nodeendday); tmpdate = tmpdate.plus(1, ChronoUnit.DAYS) ){
			ChenJiaoZhanBiInGivenPeriod tmprecord = node.getSpecficChenJiaoErRecord(tmpdate);
			if(tmprecord != null) {
				Double open = tmprecord.getOpenPrice();
				Double low = tmprecord.getLowPrice();
				Double close = tmprecord.getClosePrice();
				Double high = tmprecord.getHighPrice();
				logger.debug(tmpdate.toString()  + " :" + open + "," + high.toString() + "," + low + "," + close);
				
				int year = tmpdate.getYear();
				int month = tmpdate.getMonthValue();
				int day = tmpdate.getDayOfMonth();
				ohlcSeries.add(new Day(day,month,year), open, high, low, close);
				
				//Ϊ��ʾѰ�������Сֵ���Ա�Y�������ʵ���
				if(low < lowestLow && low !=0) //�ɼ۲�����Ϊ0��Ϊ0��˵��ͣ�ƣ��������
					lowestLow = low;
				if(high > highestHigh && high !=0)
					highestHigh = high;
				
				//������ʾѡ�е���һ�ܣ�����Ҫ��һ�������Σ�����ʵ��̫�鷳������renderer�������
				
				//Ѱ��1��/2�����ֵ
				Period intervalPeriod = Period.between(highlightweekdate, tmpdate);
				if(tmpdate.isAfter(highlightweekdate) && intervalPeriod.getDays() <= 30) {
					if(high > onemonthhigh) {
						onemonthhigh = high;	
						onemonthhighdate = new Day(day,month,year);
					}
				} 
				if(tmpdate.isAfter(highlightweekdate) && intervalPeriod.getDays() <= 60  ) {
					if(high > twomonthhigh) {
						twomonthhigh = high;
						twomonthhighdate = new Day(day,month,year);
					}
				}
				
			}
		}


		candlestickChart.getXYPlot().getRangeAxis().setRange(lowestLow*0.95, highestHigh*1.05);
		
		candlestickDataset.addSeries(ohlcSeries);
		
		chartPanel.setHorizontalAxisTrace(true); //ʮ����ʾ
        chartPanel.setVerticalAxisTrace(true);
        
      //������ʾѡ�е���һ�ܣ�����Ҫ��һ�������Σ�����ʵ��̫�鷳������renderer�������
//         XYShapeAnnotation shapeAnnotation = new XYShapeAnnotation(
//                new Rectangle2D.Double( fromX - xFactor, fromY - yFactor, xFactor+xFactor, yFactor+yFactor ),
//                20, Color.BLUE );
////        shapeAnnotation.setToolTipText( toolTip );
//        candlestickChart.getXYPlot().addAnnotation( shapeAnnotation );
        
        //draw annotation
        double millis = onemonthhighdate.getFirstMillisecond();
        final XYPointerAnnotation pointer = new XYPointerAnnotation("1�������", millis, onemonthhigh, 3.0 * Math.PI / 4.0);
		pointer.setBaseRadius(35.0);
		pointer.setTipRadius(10.0);
		pointer.setFont(new Font("SansSerif", Font.PLAIN, 9));
		pointer.setPaint(Color.blue);
		pointer.setTextAnchor(TextAnchor.HALF_ASCENT_RIGHT);
		candlestickChart.getXYPlot().addAnnotation(pointer);

        setPanelTitle ( node,  nodeendday);
		
	}
	/*
	 * ��ʾָ�����ڵ�K��
	 */
	public void setNodeCandleStickDate (BkChanYeLianTreeNode node, LocalDate nodestartday, LocalDate nodeendday)
	{
		this.curdisplayednode = node;
//		LocalDate requireend = displayedenddate.with(DayOfWeek.SATURDAY);
//		LocalDate requirestart = displayedenddate.with(DayOfWeek.MONDAY).minus(this.shoulddisplayedmonthnum,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		
		ohlcSeries = new OHLCSeries("KPrice");
//		ArrayList<ChenJiaoZhanBiInGivenPeriod> daydata = node.getWkChenJiaoErZhanBiInGivenPeriod();
		double lowestLow =10000.0;  double highestHigh =0.0; 
		for(LocalDate tmpdate = nodestartday;tmpdate.isBefore( nodeendday) || tmpdate.isEqual(nodeendday); tmpdate = tmpdate.plus(1, ChronoUnit.DAYS) ){
			ChenJiaoZhanBiInGivenPeriod tmprecord = node.getSpecficChenJiaoErRecord(tmpdate);
			if(tmprecord != null) {
				Double open = tmprecord.getOpenPrice();
				Double low = tmprecord.getLowPrice();
				Double close = tmprecord.getClosePrice();
				Double high = tmprecord.getHighPrice();
				logger.debug(tmpdate.toString()  + " :" + open + "," + high.toString() + "," + low + "," + close);
				
				int year = tmpdate.getYear();
				int month = tmpdate.getMonthValue();
				int day = tmpdate.getDayOfMonth();
				ohlcSeries.add(new Day(day,month,year), open, high, low, close);
				
				if(low < lowestLow && low !=0) //�ɼ۲�����Ϊ0��Ϊ0��˵��ͣ�ƣ��������
					lowestLow = low;
				if(high > highestHigh && high !=0)
					highestHigh = high;
			}
		}

		candlestickChart.getXYPlot().getRangeAxis().setRange(lowestLow*0.95, highestHigh*1.05);
		
		candlestickDataset.addSeries(ohlcSeries);
		
		chartPanel.setHorizontalAxisTrace(true); //ʮ����ʾ
        chartPanel.setVerticalAxisTrace(true);
        

        setPanelTitle ( node,  nodeendday);
	}
	
	private void highlightKOfGivenPeriod(LocalDate period, String markerTyp)
	{//https://stackoverflow.com/questions/6603450/drawing-shapes-in-jfreechart
		
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
		standardChartTheme.setExtraLargeFont(new Font("����",Font.BOLD,10) );
		standardChartTheme.setRegularFont(new Font("����",Font.BOLD,10) );
		standardChartTheme.setLargeFont(new Font("����",Font.BOLD,10));
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
    												+ "��" + requireend.toString() + "��" + requirestart
    												+ "����K��");
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
	private final Paint colorTransparent = Color.YELLOW;
	
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
        
       
        Number curClose = highLowData.getClose(series, item);
        Number prevClose = highLowData.getClose(series, item>0 ? item-1 : 0);
        if(  ldate.equals(datebeselectedinweek.with(DayOfWeek.FRIDAY) ) )
       	 return Color.YELLOW;
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

