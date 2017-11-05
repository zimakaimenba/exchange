package com.exchangeinfomanager.bankuaifengxi;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.PlotEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;
//import org.jfree.chart.renderer.junit.RendererChangeDetector;

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.sun.rowset.CachedRowSetImpl;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.geom.Rectangle2D;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;

public class BanKuaiFengXiBarChartPnl extends JPanel 
{
	/**
	 * Create the panel.
	 */
	public BanKuaiFengXiBarChartPnl() 
	{
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
		standardChartTheme.setExtraLargeFont(new Font("隶书",Font.BOLD,20) );
		standardChartTheme.setRegularFont(new Font("隶书",Font.BOLD,20) );
		standardChartTheme.setLargeFont(new Font("隶书",Font.BOLD,20));
		ChartFactory.setChartTheme(standardChartTheme);

		createChartPanel();
		createEvent ();
		
		sysconfig = SystemConfigration.getInstance();
	}
	
	private BkChanYeLianTreeNode curdisplayednode;	
	private Date displayedenddate;
	private String pnltitle;
	private CategoryPlot plot;
	private ChartPanel chartPanel;
	private DefaultCategoryDataset barchartdataset ;
	private JFreeChart barchart;
	private Comparable dateselected;
	private SystemConfigration sysconfig;
	

	
	/*
	 * 板块/股票按周交易额
	 */
	public void setNodeJiaoYiErByWeek (BkChanYeLianTreeNode node,Date displayedenddate1)
	{
		this.curdisplayednode = node;
		this.displayedenddate = displayedenddate1;
		
		barchartdataset = new DefaultCategoryDataset();
		
		ArrayList<ChenJiaoZhanBiInGivenPeriod> cjezb = node.getChenJiaoErZhanBiInGivenPeriod ();
		if(cjezb == null )
			return ;
		for(ChenJiaoZhanBiInGivenPeriod tmpcjezb : cjezb) {
			Date lastdayofweek = tmpcjezb.getDayofEndofWeek();
			Double zhanbi = tmpcjezb.getMyOwnChengJiaoEr();
			barchartdataset.setValue(zhanbi,"成交额",lastdayofweek);
		}
	
		DecimalFormat decimalformate = new DecimalFormat("#0.000");
		((CustomRenderer) plot.getRenderer()).setItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}",decimalformate));
		((CustomRenderer) plot.getRenderer()).setSeriesToolTipGenerator(0,new CustomToolTipGeneratorForChenJiaoEr());
		((CustomRenderer) plot.getRenderer()).setToolTipGenerator(new CustomToolTipGeneratorForChenJiaoEr());
		
		plot.setDataset(barchartdataset);
		
		setPanelTitle ("成绩额");
		
	}
	/*
	 * 板块大盘/个股板块 占比
	 */
	public void setNodeZhanBiByWeek (BkChanYeLianTreeNode node, Date displayedenddate1)
	{
		this.curdisplayednode = node;
		this.displayedenddate = displayedenddate1;
		
		barchartdataset = new DefaultCategoryDataset();
		
		ArrayList<ChenJiaoZhanBiInGivenPeriod> cjezb = node.getChenJiaoErZhanBiInGivenPeriod ();
		if(cjezb == null )
			return ;
		for(ChenJiaoZhanBiInGivenPeriod tmpcjezb : cjezb) {
			Date lastdayofweek = tmpcjezb.getDayofEndofWeek();
			Double zhanbi = tmpcjezb.getCjlZhanBi();
			barchartdataset.setValue(zhanbi,"占比",lastdayofweek);
		}
	
		
		DecimalFormat decimalformate = new DecimalFormat("%#0.000");
		((CustomRenderer) plot.getRenderer()).setItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}",decimalformate));
		((CustomRenderer) plot.getRenderer()).setSeriesToolTipGenerator(0,new CustomToolTipGeneratorForZhanBi());
		((CustomRenderer) plot.getRenderer()).setToolTipGenerator(new CustomToolTipGeneratorForZhanBi());
        
				
		plot.setDataset(barchartdataset);
		
		setPanelTitle ("占比");
	}
	
	private void setPanelTitle (String type)
	{
		String nodecode = curdisplayednode.getMyOwnCode();
		String nodename = curdisplayednode.getMyOwnName();
		
		Date endday = CommonUtility.getLastDayOfWeek(displayedenddate );
    	Date startday = CommonUtility.getFirstDayOfWeek( CommonUtility.getDateOfSpecificMonthAgo(displayedenddate ,sysconfig.banKuaiFengXiMonthRange() ) );
    	((TitledBorder)this.getBorder()).setTitle( "\"" + nodecode+ nodename + "\"" + type 
															+ "从" + CommonUtility.formatDateYYYY_MM_DD(startday) 
															+ "到" + CommonUtility.formatDateYYYY_MM_DD(endday) );
    	this.repaint();
    	this.setToolTipText(nodecode+ nodename );
	}
	/*
	 * 
	 */
	public BkChanYeLianTreeNode getCurDisplayedNode ()
	{
		return this.curdisplayednode;
	}
	/*
	 * 
	 */
	public void resetDate ()
	{
		barchartdataset = new DefaultCategoryDataset();
		plot.setDataset(barchartdataset);
		((CustomRenderer)plot.getRenderer()).setBarColumnShouldChangeColor(-1);
	}
	/*
	 * 
	 */
    @SuppressWarnings("deprecation")
	private void createChartPanel() 
    {
//    	https://www.youtube.com/watch?v=YV80Titt9Q4
//    	BarRenderer renderer = new BarRenderer ();
    	CustomRenderer renderer = new CustomRenderer ();
//        DecimalFormat decimalformate = new DecimalFormat("%#0.000");
//        renderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}",decimalformate));
        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12,TextAnchor.HALF_ASCENT_CENTER));
        renderer.setItemLabelAnchorOffset(5);
        renderer.setItemLabelsVisible(true);
        renderer.setBaseItemLabelsVisible(true);
//        renderer.setSeriesToolTipGenerator(0,new CustomToolTipGeneratorForZhanBi());
//        renderer.setToolTipGenerator(new CustomToolTipGeneratorForZhanBi());
//        renderer.setSeriesPaint(0, Color.blue);
//        renderer.setDefaultBarPainter(new StandardBarPainter());
        
        plot = new CategoryPlot(); 
        LegendTitle legend = new LegendTitle(plot); 
        legend.setPosition(RectangleEdge.TOP); 
        plot.setDataset(barchartdataset); 
        plot.setRenderer(renderer); 
        plot.setDomainAxis(new CategoryAxis("")); 
        plot.setRangeAxis(new NumberAxis(""));
        ((CustomRenderer) plot.getRenderer()).setBarPainter(new StandardBarPainter());
        CategoryAxis axis = plot.getDomainAxis();
        axis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);

        barchart = new JFreeChart(plot);
        barchart.removeLegend();

        chartPanel = new ChartPanel(barchart);
        this.add(chartPanel);
        
    }
    
    private void createEvent ()
    {
    	chartPanel.addChartMouseListener(new ChartMouseListener() {

    	    public void chartMouseClicked(ChartMouseEvent cme) {
    	    	try {
    	    		CategoryItemEntity xyitem=(CategoryItemEntity) cme.getEntity(); // get clicked entity
//        	        System.out.println(xyitem);
        	        CategoryDataset dataset = xyitem.getDataset(); // get data set
        	        Comparable columnkey = xyitem.getColumnKey();
        	        highLightSpecificBarColumn (columnkey);
//        	        int cindex = dataset.getColumnIndex(columnkey) ;
//        	        ((CustomRenderer)plot.getRenderer()).setBarColumnShouldChangeColor(cindex);
        	        
//        	         Comparable selecteddate = xyitem.getColumnKey();
        	         dateselected = columnkey;
        	         
    	    	} catch ( java.lang.ClassCastException e ) {
    	    		PlotEntity xyitem1 = (PlotEntity) cme.getEntity();
    	    		xyitem1.getPlot();
    	    	}
    	    }

			@Override
			public void chartMouseMoved(ChartMouseEvent arg0) {
			}

    	});
    }
    /*
     * 设置要突出显示的bar
     */
    public void highLightSpecificBarColumn (Comparable selecteddate)
    {
    	int cindex = barchartdataset.getColumnIndex(selecteddate) ;
    	((CustomRenderer)plot.getRenderer()).setBarColumnShouldChangeColor(cindex);
    	
//    	((CustomRenderer)plot.getRenderer()).addChangeListener(new RendererChangeListener () );
        dateselected = selecteddate;
        
//        this.getChartPanel().repaint();
    }
    /*
     * 
     */
	public Comparable getCurSelectedBarDate ()
	{
		return dateselected;
	}
	/*
	 * 
	 */
    public ChartPanel getChartPanel() {
        return chartPanel;
    }

}

class CustomRenderer extends BarRenderer 
{
	private static final long serialVersionUID = 1L;
//	private Paint[] colors;
    int shouldcolumn = -1;
   
    public CustomRenderer() {
        super();
    }

    public Paint getItemPaint(final int row, final int column) 
    {
        if(column == shouldcolumn)
            return Color.blue;
        else   
            return Color.RED;
//        	return super.getItemPaint(row, column);
   }
    public void setBarColumnShouldChangeColor (int column)
    {
    	this.shouldcolumn = column;
    }
    
    
}

class CustomToolTipGeneratorForZhanBi implements CategoryToolTipGenerator  {
    public String generateToolTip(CategoryDataset dataset, int row, int column)  
    {
    	Double datecur = (Double)dataset.getValue(row, column);
    	if(column >=1) {
    		Double datelast = (Double)dataset.getValue(row, column-1);
            Double ratio = (datecur - datelast)/datelast;

            DecimalFormat decimalformate = new DecimalFormat("%#0.000");
            int maxweek = getMaxWeekOfAGivenPeriod (dataset,row,column);
            Comparable columndate = dataset.getColumnKey(column);
            
        	return columndate + "占比" + decimalformate.format(datecur) + "占比变化(" + decimalformate.format(ratio) + ")" + "MaxWeek=" + maxweek;
    	} else {
    		Comparable test = dataset.getColumnKey(column);
    		return test.toString();
    	}
    }
    /*
     * 计算占比是几周来的最大值
     */
    private int getMaxWeekOfAGivenPeriod (CategoryDataset dataset, int row, int column)
    {
    	Double datecur = (Double)dataset.getValue(row, column);
    	int maxweek =0;
    	for(int i= column-1;i>=0;i--) {
    		Double datelast = (Double)dataset.getValue(row, i);
    		if(datecur > datelast)
    			maxweek ++;
    		else 
    			break;
    	}
    	
    	return maxweek;
    }
}

class CustomToolTipGeneratorForChenJiaoEr implements CategoryToolTipGenerator  {
    public String generateToolTip(CategoryDataset dataset, int row, int column)  
    {
    	Double datecur = (Double)dataset.getValue(row, column);
    	
    	DecimalFormat decimalformate = new DecimalFormat("#0.000");
    	Comparable columndate = dataset.getColumnKey(column);
        
    	return columndate + "成交额" + decimalformate.format(datecur) ;
    }

}

