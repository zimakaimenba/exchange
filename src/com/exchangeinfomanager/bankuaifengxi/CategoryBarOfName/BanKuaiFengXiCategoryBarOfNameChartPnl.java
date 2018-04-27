package com.exchangeinfomanager.bankuaifengxi.CategoryBarOfName;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
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
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.TextAnchor;


import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Paint;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;

public abstract class BanKuaiFengXiCategoryBarOfNameChartPnl extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	public BanKuaiFengXiCategoryBarOfNameChartPnl() 
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		createChartPanel();
		
		createEvent ();
		
		sysconfig = SystemConfigration.getInstance();
		this.shoulddisplayedmonthnum = sysconfig.banKuaiFengXiMonthRange() -3;
		
		bkdbopt = new BanKuaiDbOperation ();
	}
	
	private static Logger logger = Logger.getLogger(BanKuaiFengXiCategoryBarOfNameChartPnl.class);
	protected String globeperiod = "WEEK";
	protected CategoryPlot plot;
	protected ChartPanel chartPanel;
	protected DefaultCategoryDataset barchartdataset ;
	protected JFreeChart barchart;
	
	private SystemConfigration sysconfig;
	protected int shoulddisplayedmonthnum;
	private BanKuaiDbOperation bkdbopt;
	
//	private Set<BarChartPanelHightLightColumnListener> chartpanelhighlightlisteners;
	
	public static final String SELECTED_PROPERTY = "selected";
	protected boolean selectchanged;
	private String tooltipselected;
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this); //	https://stackoverflow.com/questions/4690892/passing-a-value-between-components/4691447#4691447
	private String currentselectedstock;
	public void addPropertyChangeListener(PropertyChangeListener listener) {
	        pcs.addPropertyChangeListener(listener);
	}
	/*
	 * 
	 */
	public void resetDate ()
	{
		if(barchartdataset != null)
			barchartdataset.clear();
		
		chartPanel.removeAll();
	}
   /*
    * 
    */
    private void createEvent ()
    {
    	chartPanel.addChartMouseListener(new ChartMouseListener() {

    	    public void chartMouseClicked(ChartMouseEvent cme) {
    	    	try {
    	    		CategoryItemEntity xyitem = (CategoryItemEntity) cme.getEntity(); // get clicked entity
        	        String columnkey = xyitem.getColumnKey().toString();
        	        String selectedtooltip = xyitem.getToolTipText();
        	        highLightSpecificBarColumn (columnkey);
        
//        	        setCurSelectedBarInfo (columnkey,selectedtooltip);

        	        selectchanged = true;
    	    	} catch ( java.lang.ClassCastException e ) {
        	        selectchanged = false;
    	    	}
    	    }

			@Override
			public void chartMouseMoved(ChartMouseEvent arg0) {
			}

    	});
    }
    /*
     * 
     */
    public void highLightSpecificBarColumn (String selectedstock)
    {
    	if(selectedstock == null)
    		return;
    	
    	int cindex = barchartdataset.getColumnIndex(selectedstock) ;
    	if(cindex == -1)
    		return ;
    	this.currentselectedstock = selectedstock;
    	
    	((BanKuaiFengXiCategoryBarOfNameRenderer)plot.getRenderer()).setBarColumnShouldChangeColor(cindex);
    	((CategoryLabelCustomizableCategoryOfNameAxis)plot.getDomainAxis()).setBarLabelShouldChangeColor(selectedstock);
        this.barchart.fireChartChanged();//必须有这句
    }
    /*
     * 
     */
	public String getCurSelectedStock ()
	{
		return this.currentselectedstock;
	}
	/*
	 * 
	 */
	public void setCurSelectedBarInfo (String columnkey,String selectedtooltip) 
	{
        String oldText = columnkey + this.tooltipselected;
        this.tooltipselected =  "";
        PropertyChangeEvent evt = new PropertyChangeEvent(this, SELECTED_PROPERTY, oldText,  columnkey + this.tooltipselected );
        pcs.firePropertyChange(evt);
    }
	/*
	 * 
	 */
    public ChartPanel getChartPanel() {
        return chartPanel;
    }
    
	/*
	 * 突出显示一些预先设置
	 */
	protected void operationAfterDataSetup (String title) 
	{
		title = "个股市值范围:" + title;
		Font font = new Font("Dialog", Font.BOLD, 12);
		TextTitle tt = new TextTitle(title,font); 
		this.barchart.setTitle(tt);
	}
    
    @SuppressWarnings("deprecation")
	private void createChartPanel() 
    {
		StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
		standardChartTheme.setExtraLargeFont(new Font("隶书",Font.BOLD,10) );
		standardChartTheme.setRegularFont(new Font("隶书",Font.BOLD,10) );
		standardChartTheme.setLargeFont(new Font("隶书",Font.BOLD,10));
		ChartFactory.setChartTheme(standardChartTheme);
		ChartFactory.setChartTheme(StandardChartTheme.createJFreeTheme());
//      ChartFactory.setChartTheme(StandardChartTheme.createDarknessTheme()());
		
		//bar parts
		BanKuaiFengXiCategoryBarOfNameRenderer renderer = new BanKuaiFengXiCategoryBarOfNameRenderer ();
        
        barchartdataset = new DefaultCategoryDataset(); 
        
        plot = new CategoryPlot(); 
//        plot.setBackgroundPaint(Color.GRAY); 
//        plot.setDomainGridlinePaint(Color.white); 
//        plot.setRangeGridlinePaint(Color.white);
//        LegendTitle legend = new LegendTitle(plot); 
//        legend.setPosition(RectangleEdge.TOP); 
        plot.setDataset(barchartdataset); 
        plot.setRenderer(renderer); 
        plot.setRangeAxis(new NumberAxis(""));
        plot.setRangePannable(false);
        Font font = new Font("Dialog", Font.BOLD, 8);
        plot.getRangeAxis().setTickLabelFont(font);

        plot.setDomainAxis(new CategoryLabelCustomizableCategoryOfNameAxis(""));
        plot.getDomainAxis().setLowerMargin(0.01);
        plot.getDomainAxis().setUpperMargin(0.01);
        plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
        //
        barchart = new JFreeChart(plot);
        barchart.removeLegend();
        barchart.setNotify(true);
//        barchart.setDomainZoomable(true);

        chartPanel = new ChartPanel(barchart);
//      chartPanel = new TooltipChartPanel(barchart);
//      chartPanel.setHorizontalAxisTrace(true); //十字显示
        chartPanel.setPreferredSize(new Dimension(1500, 350));
        chartPanel.setVerticalAxisTrace(true);
        chartPanel.setDomainZoomable(true);
        
        this.add(chartPanel);
   }
}

class BanKuaiFengXiCategoryBarOfNameRenderer extends BarRenderer
{
	private int shouldcolumn = -1;

	public BanKuaiFengXiCategoryBarOfNameRenderer ()
	{
		super ();
		
		super.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12,TextAnchor.HALF_ASCENT_CENTER));
		super.setItemLabelAnchorOffset(5);
		super.setItemLabelsVisible(true);
		super.setBaseItemLabelsVisible(true);
//		super.setMaximumBarWidth(.35);
//		super.setMinimumBarLength(.35);
		super.setItemMargin(-0.5); //The double specified in setItemMargin(double percent) is the percentage of the overall length of the category axis that will be used for the space between the bars within the same category (default is .2 or 20%). The smaller this value is, the larger the bars will be.
		super.setBaseToolTipGenerator(new BanKuaiFengXiCategoryBarOfNameToolTipGenerator () );
		
		DecimalFormat decimalformate = new DecimalFormat(",###");//("#0.000");
		super.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}",decimalformate));
		super.setBaseItemLabelsVisible(true);
		
		super.setBarPainter(new StandardBarPainter());
//		super.setDefaultBarPainter(new StandardBarPainter());
	}
	
	/*
	 * 
	 */
	public void setBarColumnShouldChangeColor (int column)
    {
    	this.shouldcolumn = column;
    }
	public Paint getItemPaint(final int row, final int column) 
    {
        if(column == shouldcolumn) {
          return Color.CYAN;
        } else
        	return super.getItemPaint(row, column);
        
   }
//    public Paint getItemLabelPaint(final int row, final int column)
//    {
//    	CategoryPlot plot = getPlot ();
//    	CategoryDataset dataset = plot.getDataset();
//		String selected =  dataset.getColumnKey(column).toString();
//		
//    	LocalDate selecteddate = CommonUtility.formateStringToDate(selected);
//    	 
//		Integer maxweek = nodexdata.getChenJiaoErMaxWeekOfSuperBanKuai(selecteddate,0);
//		
//		if(maxweek !=null && maxweek >= super.displayedmaxwklevel)
//			return Color.CYAN;
//		else 
//			return Color.black;
//    }
    
//    @Override
//    public LegendItem getLegendItem(int dataset, int series) 
//    {
//        LegendItem legendItem = super.getLegendItem(dataset, series);
//        logger.debug(dataset + " " + series + " " + legendItem.getShape());
//        // modify legendItem here
//        return legendItem;
//    }
}

/*
 * 设置X轴，如果有分析结果就显示为橙色
 */
class CategoryLabelCustomizableCategoryOfNameAxis extends CategoryAxis {

    private static final long serialVersionUID = 1L;
	private String period;
	private String highlightcategory;

    public CategoryLabelCustomizableCategoryOfNameAxis(String label) {
        super(label);
        super.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
        super.setLowerMargin(0.01);
        super.setUpperMargin(0.01);
        
        Font font = new Font("Dialog", Font.BOLD, 8);
        this.setTickLabelFont(font);
    }
    
    public Paint getTickLabelPaint(Comparable category)
    {
    		if(category.toString().equals(highlightcategory))
    			return Color.MAGENTA;
    		else  
        		return super.getTickLabelPaint();
    }
//    public String getLabel(String label)
//    {
//    	return label.substring(6,label.length()-1);
//    }
     /*
	 * 
	 */
	public void setBarLabelShouldChangeColor (String category)
    {
    	this.highlightcategory = category;
    }
}

class BanKuaiFengXiCategoryBarOfNameToolTipGenerator implements CategoryToolTipGenerator 
{
	protected BkChanYeLianTreeNode node;
//  private static Logger logger = Logger.getLogger(CustomToolTipGeneratorForChenJiaoEr.class);
	protected NodeXPeriodDataBasic nodexdata;

	@Override
	public  String generateToolTip(CategoryDataset dataset, int row, int column)
	{
		String selected = dataset.getColumnKey(column).toString();
    	Double curhsl = (Double)dataset.getValue(row, column);
    	
    	return "'" + selected + "'" + "换手" + curhsl.toString();
	}

}