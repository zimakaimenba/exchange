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
import com.exchangeinfomanager.asinglestockinfo.ChenJiaoZhanBiInGivenPeriod;
import com.exchangeinfomanager.asinglestockinfo.DaPan;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData;
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
	public BanKuaiFengXiBarLargePeriodChartPnl(BkChanYeLianTreeNode node, LocalDate displayedenddate1, String period) 
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
        NodeXPeriodData nodexdata = node.getNodeXPeroidData(period);
        LocalDate requirestart = nodexdata.getRecordsStartDate().with(DayOfWeek.SATURDAY);
		LocalDate requireend = nodexdata.getRecordsEndDate().with(DayOfWeek.SATURDAY);;
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
        
       
       dataset = getDataset(node,displayedenddate1,period);
        
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

    private XYDataset getDataset(BkChanYeLianTreeNode node, LocalDate displayedenddate1, String period) 
    {
    	this.curdisplayednode = node;
//    	NodeXPeriodData nodexdata = node.getNodeXPeroidData(period);
//    	LocalDate requirestart = nodexdata.getRecordsStartDate().with(DayOfWeek.SATURDAY);
//		LocalDate requireend = nodexdata.getRecordsEndDate().with(DayOfWeek.SATURDAY);;
		
		TimeSeries s1 = new TimeSeries("Series");
		
		final TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(s1);
        
        return dataset;
    }

    private JScrollBar getScrollBar(final DateAxis domainAxis){
        final double r1 = domainAxis.getLowerBound();
        final double r2 = domainAxis.getUpperBound();
        JScrollBar scrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 100, 100, 0, 400);
        scrollBar.addAdjustmentListener( new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                double x = e.getValue() *60 *60 * 1000;
                domainAxis.setRange(r1+x, r2+x);
            }
        });
        return scrollBar;
    }
    
    protected abstract XYDataset updateDataset (BkChanYeLianTreeNode node, LocalDate displayedenddate1,String period);
}

class CustomXYPlotToolTipGenerator implements XYToolTipGenerator 
{
	protected BkChanYeLianTreeNode node;
	protected NodeXPeriodData nodexdata;
	
	@Override
    public String generateToolTip(XYDataset dataset, int series, int item) 
	{
       Date d = new Date((long) dataset.getXValue(series, item));
       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

   	   LocalDate selecteddate = CommonUtility.formateStringToDate(sdf.format(d));
   	 
   	   ChenJiaoZhanBiInGivenPeriod nodefx = nodexdata.getSpecficRecord(selecteddate, 0);
//		if(node.getType() == 4 )
//			nodefx = ((BanKuai)node).getNodeFengXiResultForSpecificDate (selecteddate);
//		else if(node.getType() == 6 ) 
//			nodefx = ((Stock)node).getNodeFengXiResultForSpecificDate (selecteddate);
		
		if(nodefx == null)
			return "";
		
		String tooltip = selecteddate.toString() + " ";
		if(node.getType() == 6 ) { //个股,个股大盘比只有个股，不会有板块
			Double curzhanbidata = nodefx.getCjlZhanBi();  //占比
			Double zhanbigrowthrate = nodefx.getGgbkzhanbigrowthrate();
			Double cjezhanbi = nodefx.getGgbkcjegrowthzhanbi();
			Integer maxweek = nodefx.getGgbkzhanbimaxweek();
			Double dpzhanbi = nodefx.getGgdpzhanbi();
			Integer dpzhanbimaxweek = nodefx.getGgdpzhanbimaxweek();
			
			
			DecimalFormat decimalformate = new DecimalFormat("%#0.000");
			try {
				tooltip = tooltip + "大盘占比" + decimalformate.format(dpzhanbi);
			} catch (java.lang.IllegalArgumentException e ) {
				tooltip = tooltip + "大盘占比NULL" ;
			}
			try {
				tooltip = tooltip + "大盘MaxWk=" + dpzhanbimaxweek.toString();
			} catch (java.lang.IllegalArgumentException e ) {
				tooltip = tooltip + "大盘MaxWk=NULL";
			}
			try {
				tooltip = tooltip + "板块占比" + decimalformate.format(curzhanbidata);
			} catch (java.lang.IllegalArgumentException e ) {
				tooltip = tooltip + "板块占比NULL" ;
			}
//			try {
//				tooltip = tooltip +  "占比变化("+ decimalformate.format(zhanbigrowthrate) +  ")";
//			} catch (java.lang.IllegalArgumentException e ) {
//				tooltip = tooltip +  "占比变化(NULL)";
//			}
			try {
				tooltip = tooltip + "板块MaxWk=" + maxweek.toString();
			} catch (java.lang.IllegalArgumentException e ) {
				e.printStackTrace();
//				tooltip = tooltip +
			}
			
			
			return tooltip;
		}
   	
		return "";
    }
	
    public void setDisplayNode (BkChanYeLianTreeNode curdisplayednode) 
    {
    	this.node = curdisplayednode;
    }
    public void setDisplayNodeXPeriod(NodeXPeriodData nodexdata1) 
    {
		this.nodexdata = nodexdata1;
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
	protected int highlightercolumn = -1;
	protected BkChanYeLianTreeNode node;
	protected NodeXPeriodData nodexdata;
   	@Override
    public void paintBarShadow(Graphics2D g2, XYBarRenderer renderer, int row, int column, RectangularShape bar, RectangleEdge base, boolean pegShadow) {
   		
   	}

   	public void paintBar(Graphics2D g2, XYBarRenderer renderer, int row, int column, RectangularShape bar, RectangleEdge base) 
    {
    	bar.setFrame(bar.getX(), bar.getY(), bar.getWidth() +4, bar.getHeight());
    	    	
    	XYDataset dataset = renderer.getPlot().getDataset();
    	    	
    	if(highlightercolumn == column)
    		g2.setColor(Color.BLUE.darker());
    	else
    		g2.setColor(Color.RED.darker());
    	
    	g2.fill(bar);
    	g2.draw(bar);
    }
   	
    public void setDisplayNode (BkChanYeLianTreeNode curdisplayednode) 
    {
    	this.node = curdisplayednode;
    }

    public void setDisplayNodeXPeriod(NodeXPeriodData nodexdata1) 
    {
		this.nodexdata = nodexdata1;
	}
	public void setBarColumnShouldChangeColor (int column)
    {
    	this.highlightercolumn = column;
    }
};