package com.exchangeinfomanager.bankuaifengxi;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.*;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.DaPan;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.commonlib.CommonUtility;

import javax.swing.*;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public abstract class BanKuaiFengXiBarLargePeriodChartPnl extends JPanel 
{
	protected XYPlot mainPlot;
	protected DateAxis domainAxis ;
	protected NumberAxis rangeAxis ;
	protected XYBarRenderer renderer ;
	protected XYDataset dataset ;
	protected JFreeChart chart;
	protected ChartPanel chartPanel;
	protected BkChanYeLianTreeNode curdisplayednode;
	private int highlightercolumn = 4;
	
    @SuppressWarnings("deprecation")
	public BanKuaiFengXiBarLargePeriodChartPnl(BkChanYeLianTreeNode node, LocalDate displayedenddate1, DaPan dapan) 
    {
    	this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

//    	renderer = new CustomXYBarRenderer (.3);
        renderer = new XYBarRenderer(.3);
        renderer.setMargin(0.4);
        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12,TextAnchor.HALF_ASCENT_CENTER));
        renderer.setItemLabelAnchorOffset(5);
        renderer.setItemLabelsVisible(true);
        renderer.setBaseItemLabelsVisible(true);
//        renderer.setBarPainter(new StandardXYBarPainter());
        NumberFormat format = NumberFormat.getPercentInstance();
        format.setMaximumFractionDigits(3); // etc.
        DateFormat dateformate = DateFormat.getDateInstance();
//        renderer.setBaseItemLabelGenerator(new  StandardXYItemLabelGenerator ("{0} {1} {2}", format, format));
        renderer.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator(StandardXYItemLabelGenerator.DEFAULT_ITEM_LABEL_FORMAT, dateformate, format) );
        renderer.setBaseItemLabelsVisible(true);
        renderer.setBaseToolTipGenerator(new CustomXYPlotToolTipGenerator());
        
        renderer.setBarPainter(new CustomXYBarPainter ());

        domainAxis = new DateAxis();
        LocalDate requirestart = node.getWkRecordsStartDate().with(DayOfWeek.SATURDAY);
		LocalDate requireend = node.getWkRecordsEndDate().with(DayOfWeek.SATURDAY);;
        Day d1 = new Day(requirestart.getDayOfMonth(),requirestart.getMonthValue(),requirestart.getYear());
        Day d2 = new Day(requireend.getDayOfMonth(),requireend.getMonthValue(),requireend.getYear());
        domainAxis.setAutoRange(false);
        domainAxis.setRange(new DateRange(d1.getFirstMillisecond(), d2.getFirstMillisecond()));
        domainAxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
        domainAxis.setDateFormatOverride(new SimpleDateFormat("dd-MM-yyyy")); 
//        domainAxis.setLowerMargin(0.02);                  // reduce the default margins
//        domainAxis.setUpperMargin(0.02);
        
        rangeAxis = new NumberAxis();
//	    rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
//	    rangeAxis.setAutoRangeIncludesZero(true);
        
       
       dataset = getDataset(node,displayedenddate1,dapan);
        
        mainPlot = new XYPlot(dataset, domainAxis, rangeAxis, renderer);
        mainPlot.setDomainGridlinesVisible(true);
        mainPlot.setBackgroundPaint(Color.lightGray); 
        mainPlot.setDomainGridlinePaint(Color.white); 
        mainPlot.setRangeGridlinePaint(Color.white); 
//        mainPlot.setAxisOffset(new Spacer(100, 15D, 15D, 15D, 15D)); 
        mainPlot.setDomainCrosshairVisible(true); 
        mainPlot.setRangeCrosshairVisible(true); 
        
        
        chart = new JFreeChart(null, null, mainPlot, true);
        chart.removeLegend();
        chart.setNotify(true);
        
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(1400, 400));
        chartPanel.setVerticalAxisTrace(true);
        chartPanel.setDomainZoomable(true);
        

        this.add(chartPanel);
//        this.add(getScrollBar(domainAxis), BorderLayout.SOUTH);
        this.add(getScrollBar(domainAxis));
//        this.pack();
    }

    private XYDataset getDataset(BkChanYeLianTreeNode node, LocalDate displayedenddate1, DaPan dapan) 
    {
    	this.curdisplayednode = node;
    	LocalDate requirestart = node.getWkRecordsStartDate().with(DayOfWeek.SATURDAY);
		LocalDate requireend = node.getWkRecordsEndDate().with(DayOfWeek.SATURDAY);;
		
		TimeSeries s1 = new TimeSeries("Series");
//		double highestHigh =0.0; //设置显示范围
//
//		for(LocalDate tmpdate = requirestart;tmpdate.isBefore( requireend) || tmpdate.isEqual(requireend); tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ){
//			ChenJiaoZhanBiInGivenPeriod tmprecord = node.getSpecficChenJiaoErRecord(tmpdate);
//			if(tmprecord != null) {
//				Double ggbkratio = tmprecord.getCjlZhanBi();
//				LocalDate lastdayofweek = tmprecord.getRecordsDayofEndofWeek();
//				int tmpyear = lastdayofweek.getYear();
//				int tmpmonth = lastdayofweek.getMonthValue();
//				int tmpdayofmonth = lastdayofweek.getDayOfMonth();
//				Day tmpday = new Day (tmpdayofmonth,tmpmonth,tmpyear);
//				s1.add(tmpday,ggbkratio);
//				
//				if(ggbkratio > highestHigh)
//					highestHigh = ggbkratio;
//			} else { //没有数据，看看是不是大盘停牌还是该股当周没有数据
//				if( !dapan.isThisWeekXiuShi(tmpdate) ) { //大盘没有停牌
//					int tmpyear = tmpdate.getYear();
//					int tmpmonth = tmpdate.getMonthValue();
//					int tmpdayofmonth = tmpdate.getDayOfMonth();
//					Day tmpday = new Day (tmpdayofmonth,tmpmonth,tmpyear);
//					s1.add(tmpday,0);
////					datafx.addValue(0, "分析结果", tmpdate);
//				} else //为空说明该周市场没有交易
//					continue;
//			}
//		}
		
		final TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(s1);
        
        return dataset;
    }

    private JScrollBar getScrollBar(final DateAxis domainAxis){
        final double r1 = domainAxis.getLowerBound();
        final double r2 = domainAxis.getUpperBound();
        JScrollBar scrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 100, 0, 400);
        scrollBar.addAdjustmentListener( new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                double x = e.getValue() *60 *60 * 1000;
                domainAxis.setRange(r1+x, r2+x);
            }
        });
        return scrollBar;
    }
    
    protected abstract XYDataset updateDataset (BkChanYeLianTreeNode node, LocalDate displayedenddate1, DaPan dapan);

}

class CustomXYPlotToolTipGenerator implements XYToolTipGenerator 
{
	@Override
    public String generateToolTip(XYDataset dataset, int series, int item) 
	{
       Date d = new Date((long) dataset.getXValue(series, item));
       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
       String s = "日期:" + sdf.format(d) + "占比:";
       DecimalFormat decimalformate = new DecimalFormat("%#0.000");
       Number zhanbi = dataset.getY(series, item);
       s += decimalformate.format(zhanbi);;
       return s;
    }
	
}

//class CustomXYBarRenderer extends XYBarRenderer 
//{
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 1L;
//
//	public CustomXYBarRenderer (double margin)
//	{
//		super (margin);
//		super.setMargin(0.4);
//        super.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12,TextAnchor.HALF_ASCENT_CENTER));
//        super.setItemLabelAnchorOffset(5);
//        super.setItemLabelsVisible(true);
//        super.setBaseItemLabelsVisible(true);
////        renderer.setBarPainter(new StandardXYBarPainter());
//        DecimalFormat decimalformate = new DecimalFormat("%#0.000");
//        super.setItemLabelGenerator(new  StandardXYItemLabelGenerator ());
//        super.setItemLabelsVisible(true);
//        super.setBaseToolTipGenerator(new CustomXYPlotToolTipGenerator());
//	}
//	
//	public XYBarPainter	getBarPainter()
//	{
//	       XYBarPainter painter = new XYBarPainter() 
//	        {
//		       	@Override
//		        public void paintBarShadow(Graphics2D g2, XYBarRenderer renderer, int row, int column, RectangularShape bar, RectangleEdge base, boolean pegShadow) {
//		       		
//		       	}
//
//		       	public void paintBar(Graphics2D g2, XYBarRenderer renderer, int row, int column, RectangularShape bar, RectangleEdge base) 
//		        {
//		        	bar.setFrame(bar.getX(), bar.getY(), bar.getWidth() +4, bar.getHeight());
//		        	
//		        	if(4 == column)
//		        		g2.setColor(Color.BLUE.darker());
//		        	else
//		        		g2.setColor(Color.RED.darker());
//		        	g2.fill(bar);
//		        	g2.draw(bar);
//		        }
//	       };
//		
//	       return painter;
//	}
//	
//	@Override
//    public Paint getItemPaint(int row, int col) {
//        if (col == 4) {
//            return Color.BLUE.darker();
//        } else {
//            return super.getItemPaint(row, col);
//        }
//    }
//}


class CustomXYBarPainter implements XYBarPainter 
{
	private int highlightercolumn = 4;
   	@Override
    public void paintBarShadow(Graphics2D g2, XYBarRenderer renderer, int row, int column, RectangularShape bar, RectangleEdge base, boolean pegShadow) {
   		
   	}

   	public void paintBar(Graphics2D g2, XYBarRenderer renderer, int row, int column, RectangularShape bar, RectangleEdge base) 
    {
    	bar.setFrame(bar.getX(), bar.getY(), bar.getWidth() +4, bar.getHeight());
    	
    	if(highlightercolumn == column)
    		g2.setColor(Color.BLUE.darker());
    	else
    		g2.setColor(Color.RED.darker());
    	g2.fill(bar);
    	g2.draw(bar);
    }
};