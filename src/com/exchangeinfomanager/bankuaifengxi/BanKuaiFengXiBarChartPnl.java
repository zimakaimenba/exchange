package com.exchangeinfomanager.bankuaifengxi;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
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
import org.jfree.chart.plot.ValueMarker;
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
import com.exchangeinfomanager.asinglestockinfo.DaPan;
import com.exchangeinfomanager.asinglestockinfo.Stock;
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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JMenuItem;

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
		this.shoulddisplayedmonthnum = sysconfig.banKuaiFengXiMonthRange() -3;
	}
	
	protected BkChanYeLianTreeNode curdisplayednode;	
//	private LocalDate displayedenddate;
//	private String pnltitle;
	protected CategoryPlot plot;
	protected ChartPanel chartPanel;
	protected DefaultCategoryDataset barchartdataset ;
	protected JFreeChart barchart;
	private Comparable dateselected;
	private String tooltipselected;
	private SystemConfigration sysconfig;
	protected int shoulddisplayedmonthnum;

	/*
	 * 
	 */
	protected void setDaZiJinValueMarker (double d)
	{
		ValueMarker marker = new ValueMarker (d);
//		marker.setLabel("热点占比警戒线");
		marker.setLabelTextAnchor(TextAnchor.TOP_CENTER);
		marker.setPaint(Color.BLACK);
		plot.addRangeMarker(marker);
	}
	/*
	 * 
	 */
	protected void setPanelTitle (String type, LocalDate displayedenddate1)
	{
		String nodecode = curdisplayednode.getMyOwnCode();
		String nodename = curdisplayednode.getMyOwnName();
		
		LocalDate requireend = displayedenddate1.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = displayedenddate1.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange(),ChronoUnit.MONTHS);
		
		try {
	    	((TitledBorder)this.getBorder()).setTitle( "\"" + nodecode+ nodename + "\"" + type 
																+ "从" + CommonUtility.formatDateYYYY_MM_DD(requirestart) 
																+ "到" + CommonUtility.formatDateYYYY_MM_DD(requireend) );
	    	this.repaint();
		} catch (java.lang.NullPointerException e) {
			e.printStackTrace();
		}
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
		((BanKuaiFengXiBarRenderer)plot.getRenderer()).setBarColumnShouldChangeColor(-1);
	}
   
    private void createEvent ()
    {
    	chartPanel.addChartMouseListener(new ChartMouseListener() {

    	    public void chartMouseClicked(ChartMouseEvent cme) {
    	    	try {
    	    		CategoryItemEntity xyitem=(CategoryItemEntity) cme.getEntity(); // get clicked entity
        	        CategoryDataset dataset = xyitem.getDataset(); // get data set
        	        Comparable columnkey = xyitem.getColumnKey();
        	        highLightSpecificBarColumn (columnkey);
        	        dateselected = columnkey;
        	        tooltipselected = xyitem.getToolTipText();
        	         
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
    	((BanKuaiFengXiBarRenderer)plot.getRenderer()).setBarColumnShouldChangeColor(cindex);
    	
        dateselected = selecteddate;
        barchart.fireChartChanged();//必须有这句
    }
    /*
     * 
     */
	public Comparable getCurSelectedBarDate ()
	{
		return dateselected;
	}
	public String getToolTipSelected ()
	{
		return tooltipselected;
	}
	/*
	 * 
	 */
    public ChartPanel getChartPanel() {
        return chartPanel;
    }
	/*
	 * 
	 */
    JMenuItem mntmNewMenuItem;
    @SuppressWarnings("deprecation")
	private void createChartPanel() 
    {
//    	https://www.youtube.com/watch?v=YV80Titt9Q4
//    	BarRenderer renderer = new BarRenderer ();
    	BanKuaiFengXiBarRenderer renderer = new BanKuaiFengXiBarRenderer ();
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
        plot.setRangePannable(true);
//        plot.setDomainPannable(true);
//        ((BanKuaiFengXiBarRenderer) plot.getRenderer()).setBarPainter(new StandardBarPainter());
        CategoryAxis axis = plot.getDomainAxis();
        axis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);

        barchart = new JFreeChart(plot);
        barchart.removeLegend();
        barchart.setNotify(true);

        chartPanel = new ChartPanel(barchart);
        this.add(chartPanel);
        
        
        JPopupMenu popupMenu = new JPopupMenu();
		mntmNewMenuItem = new JMenuItem("分析记录");
//		popupMenu.add(mntmNewMenuItem);
		chartPanel.getPopupMenu().add(mntmNewMenuItem);
        
    }

}

