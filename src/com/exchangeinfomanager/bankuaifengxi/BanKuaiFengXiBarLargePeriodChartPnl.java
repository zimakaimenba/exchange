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
import org.jfree.chart.plot.CategoryPlot;
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
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
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
//	private int highlightercolumn = 4;
	
    @SuppressWarnings("deprecation")
	public BanKuaiFengXiBarLargePeriodChartPnl(BkChanYeLianTreeNode node, LocalDate displayedenddate1, String period) 
    {
    	this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    	renderer = new CustomXYBarRenderer (.3);

    	domainAxis = new DateAxis();
        NodeXPeriodDataBasic nodexdata = node.getNodeXPeroidData(period);
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
    /*
     * 
     */
    public void setHighLightMaxWeekNumber(int maxwk) 
    {
    	((CustomXYBarRenderer)mainPlot.getRenderer()).setHighLightMaxWeekNumber(maxwk);
    }
    private XYDataset getDataset(BkChanYeLianTreeNode node, LocalDate displayedenddate1, String period) 
    {
    	this.curdisplayednode = node;
		final TimeSeriesCollection dataset = new TimeSeriesCollection();
        return dataset;
    }

    private JScrollBar getScrollBar(final DateAxis domainAxis)
    {
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
	protected NodeXPeriodDataBasic nodexdata;
	
	@Override
    public String generateToolTip(XYDataset dataset, int series, int item) 
	{
       Date d = new Date((long) dataset.getXValue(series, item));
       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
   	   LocalDate selecteddate = CommonUtility.formateStringToDate(sdf.format(d));
   	 
		String tooltip = selecteddate.toString() + " ";
		 
			Double curzhanbidata = dataset.getYValue(series, item);   //占比
			DecimalFormat decimalformate = new DecimalFormat("%#0.000");
			try {
				tooltip = tooltip + "占比" + decimalformate.format(curzhanbidata);
			} catch (java.lang.IllegalArgumentException e ) {
				tooltip = tooltip + "占比NULL" ;
			}
			
			Integer maxweek;
			try {
				 maxweek = nodexdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(selecteddate);
				 tooltip = tooltip + "MAXWK" + maxweek;
			} catch (java.lang.NullPointerException e) {
				 maxweek = 0;
				 tooltip = tooltip + "MAXWK=0" ;
			}
			

			
			return tooltip;

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

class CustomXYBarRenderer extends XYBarRenderer 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BkChanYeLianTreeNode node;
	private NodeXPeriodDataBasic nodexdata;
	private int highlightmaxwk = 10000;
	public CustomXYBarRenderer (double margin)
	{
		super (margin);
		super.setMargin(0.4);
	    super.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12,TextAnchor.HALF_ASCENT_CENTER));
	    super.setItemLabelAnchorOffset(5);
	    super.setItemLabelsVisible(true);
	    super.setBaseItemLabelsVisible(true);
	    NumberFormat format = NumberFormat.getPercentInstance();
        format.setMaximumFractionDigits(3); // etc.
        DateFormat dateformate = DateFormat.getDateInstance();
//        renderer.setBaseItemLabelGenerator(new  StandardXYItemLabelGenerator ("{0} {1} {2}", format, format));
        super.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator(StandardXYItemLabelGenerator.DEFAULT_ITEM_LABEL_FORMAT, dateformate, format) );
        super.setBaseItemLabelsVisible(true);
        super.setBaseToolTipGenerator(new CustomXYPlotToolTipGenerator());
        super.setBarPainter(new CustomXYBarPainter ());
	}
	
//	protected void drawItemLabel(Graphics2D g2, XYDataset dataset, int series, int item, XYPlot plot, XYItemLabelGenerator generator, Rectangle2D bar, boolean negative)
//	{
//		 g2.setColor(Color.cyan);
//		
//	}
    public Paint getItemLabelPaint(final int row, final int column)
    {
    	XYPlot plot = getPlot ();
    	XYDataset dataset = plot.getDataset();
		double selectedy = dataset.getY(row, column).doubleValue();
//		TimeSeries series = (TimeSeries) dataset.getSeriesKey(1);
		Date d = new Date((long) dataset.getXValue(0, column));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		LocalDate selecteddate = CommonUtility.formateStringToDate(sdf.format(d)); 
		Integer maxweek = nodexdata.getChenJiaoErMaxWeekOfSuperBanKuai(selecteddate);
		
		if(maxweek !=null && maxweek >= highlightmaxwk)
			return Color.RED;
		else 
			return Color.black;
    }
	
    public void setDisplayNode (BkChanYeLianTreeNode curdisplayednode) 
    {
    	this.node = curdisplayednode;
    }

    public void setDisplayNodeXPeriodData(NodeXPeriodDataBasic nodexdata1) 
    {
		this.nodexdata = nodexdata1;
	}
    public void setHighLightMaxWeekNumber(int maxwk) 
    {
    	this.highlightmaxwk = maxwk;
    }


}


class CustomXYBarPainter implements XYBarPainter 
{
	protected int highlightercolumn = -1;
	protected BkChanYeLianTreeNode node;
	protected NodeXPeriodDataBasic nodexdata;
   	@Override
    public void paintBarShadow(Graphics2D g2, XYBarRenderer renderer, int row, int column, RectangularShape bar, RectangleEdge base, boolean pegShadow) {
   		
   	}

   	public void paintBar(Graphics2D g2, XYBarRenderer renderer, int row, int column, RectangularShape bar, RectangleEdge base) 
    {
    	bar.setFrame(bar.getX(), bar.getY(), bar.getWidth() , bar.getHeight());
    	    	
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

    public void setDisplayNodeXPeriodData(NodeXPeriodDataBasic nodexdata1) 
    {
		this.nodexdata = nodexdata1;
	}
	public void setBarColumnShouldChangeColor (int column)
    {
    	this.highlightercolumn = column;
    }
};