package com.exchangeinfomanager.bankuaifengxi.CategoryBar;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.PlotEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.category.SlidingCategoryDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;
//import org.jfree.chart.renderer.junit.RendererChangeDetector;

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.DaPan;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiInfoTableModel;
import com.exchangeinfomanager.bankuaifengxi.BarChartPanelDataChangedListener;
import com.exchangeinfomanager.bankuaifengxi.BarChartPanelHightLightColumnListener;
import com.exchangeinfomanager.bankuaifengxi.TooltipChartPanel.TooltipChartPanel;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.gui.subgui.JiaRuJiHua;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.sun.rowset.CachedRowSetImpl;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
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
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public abstract class BanKuaiFengXiCategoryBarChartPnl extends JPanel implements BarChartPanelDataChangedListener, BarChartPanelHightLightColumnListener  
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	public BanKuaiFengXiCategoryBarChartPnl() 
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		createChartPanel();
		createEvent ();
		
		sysconfig = SystemConfigration.getInstance();
		this.shoulddisplayedmonthnum = sysconfig.banKuaiFengXiMonthRange() -3;
		
		bkdbopt = new BanKuaiDbOperation ();
	}
	
	private static Logger logger = Logger.getLogger(BanKuaiFengXiCategoryBarChartPnl.class);
	protected BkChanYeLianTreeNode curdisplayednode;	
	protected String globeperiod = "WEEK";
//	private LocalDate displayedenddate;
//	private String pnltitle;
	protected CategoryPlot plot;
	protected ChartPanel chartPanel;
	protected DefaultCategoryDataset barchartdataset ;
//	protected SlidingCategoryDataset barchartdataset;
	protected JFreeChart barchart;
	private Comparable<String> dateselected;
	private ArrayList<JiaRuJiHua> selectedfxjg;
	private String tooltipselected;
	private SystemConfigration sysconfig;
	protected int shoulddisplayedmonthnum;
	private BanKuaiDbOperation bkdbopt;
	protected boolean selectchanged;
	private Set<BarChartPanelHightLightColumnListener> chartpanelhighlightlisteners;

	/*
	 * 
	 */
	public void setDaZiJinValueMarker (double d)
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

		String tooltip = "\"" + nodecode + nodename + "\"" + type 
				+ "从" + CommonUtility.formatDateYYYY_MM_DD(requirestart) 
				+ "到" + CommonUtility.formatDateYYYY_MM_DD(requireend);
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
        	        CategoryDataset dataset = xyitem.getDataset(); // get data set
        	        Comparable<String> columnkey = xyitem.getColumnKey();
        	        highLightSpecificBarColumn (columnkey);
        	        dateselected = columnkey;
        	        tooltipselected = xyitem.getToolTipText();
        	        
        	        getZdgzFx (CommonUtility.formateStringToDate(columnkey.toString()),globeperiod);
        	        
        	        selectchanged = true;
        	        
        	        chartpanelhighlightlisteners.forEach(l -> l.highLightSpecificBarColumn(columnkey));
        	         
    	    	} catch ( java.lang.ClassCastException e ) {
//    	    		PlotEntity xyitem1 = (PlotEntity) cme.getEntity();
//    	    		xyitem1.getPlot();
//    	    		dateselected = null;
//        	        tooltipselected = null;
        	        selectchanged = false;
    	    	}
    	    }

			@Override
			public void chartMouseMoved(ChartMouseEvent arg0) {
			}

    	});
    	
    	mntmFenXiJiLu.addActionListener(new ActionListener() {
			@Override

			public void actionPerformed(ActionEvent evt) 
			{
				if(curdisplayednode == null)
					return;
				
				String bkcode = curdisplayednode.getMyOwnCode();
				JiaRuJiHua jiarujihua = new JiaRuJiHua ( bkcode,"分析结果" ); 
				LocalDate curselectdate = CommonUtility.formateStringToDate( getCurSelectedBarDate ().toString() );
				jiarujihua.setJiaRuDate (curselectdate);
				int exchangeresult = JOptionPane.showConfirmDialog(null, jiarujihua, "输入分析结果", JOptionPane.OK_CANCEL_OPTION);
				if(exchangeresult == JOptionPane.CANCEL_OPTION)
					return;
				
				int autoIncKeyFromApi =	bkdbopt.setZdgzRelatedActions (jiarujihua);
				
				jiarujihua = null;
			}
			
		});
    }
    /*
     * 
     */
    protected void getZdgzFx(LocalDate localDate,String period) 
    {
    	ArrayList<JiaRuJiHua> fxresult = bkdbopt.getZdgzFxjgForANodeOfGivenPeriod (this.curdisplayednode.getMyOwnCode(),localDate);
    	this.selectedfxjg = fxresult;
	}
	/*
     * 设置要突出显示的bar
     */
    @Override
    public void highLightSpecificBarColumn (Comparable selecteddate)
    {
    	if(selecteddate == null)
    		return;
    	
    	int cindex = barchartdataset.getColumnIndex(selecteddate) ;
    	((BanKuaiFengXiCategoryBarRenderer)plot.getRenderer()).setBarColumnShouldChangeColor(cindex);
    	
        this.dateselected = selecteddate;
        this.barchart.fireChartChanged();//必须有这句
    }
    @Override
    public void highLightSpecificBarColumn (Integer columnindex)
    {
    	
    }
	@Override
	public void setHightLightColumnListeners(Set<BarChartPanelHightLightColumnListener> chartpanelhighlightlisteners1) {
		this.chartpanelhighlightlisteners = chartpanelhighlightlisteners1;
		
	}
    /*
     * 
     */
    public boolean isSelectedColumnChanged ()
    {
    	return this.selectchanged;
    }
    /*
     * 设置要突出显示成交量或者占比MAXWK的阀值
     */
    public void setDisplayMaxwkLevel  (int maxl) 
    {
    	((BanKuaiFengXiCategoryBarRenderer)plot.getRenderer()).setDisplayMaxwkLevel(maxl);
    	 barchart.fireChartChanged();//必须有这句
    }
    /*
     * 
     */
	public Comparable<String> getCurSelectedBarDate ()
	{
		return dateselected;
	}
	public String getToolTipSelected ()
	{
		if(tooltipselected != null)
			return this.curdisplayednode.getMyOwnCode() + this.curdisplayednode.getMyOwnName() + ": " + tooltipselected;
		else
			return null;
	}
	public ArrayList<JiaRuJiHua> getCurSelectedFengXiJieGuo ()
	{
		return this.selectedfxjg;
	}
	/*
	 * 
	 */
    public ChartPanel getChartPanel() {
        return chartPanel;
    }
    
	private JMenuItem mntmFenXiJiLu;
    @SuppressWarnings("deprecation")
	private void createChartPanel() 
    {
		StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
		standardChartTheme.setExtraLargeFont(new Font("隶书",Font.BOLD,20) );
		standardChartTheme.setRegularFont(new Font("隶书",Font.BOLD,20) );
		standardChartTheme.setLargeFont(new Font("隶书",Font.BOLD,20));
		ChartFactory.setChartTheme(standardChartTheme);
		
//    	https://www.youtube.com/watch?v=YV80Titt9Q4
//    	BarRenderer renderer = new BarRenderer ();
    	BanKuaiFengXiCategoryBarRenderer renderer = new BanKuaiFengXiCategoryBarRenderer ();
//        DecimalFormat decimalformate = new DecimalFormat("%#0.000");
//        renderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}",decimalformate));
        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12,TextAnchor.HALF_ASCENT_CENTER));
        renderer.setItemLabelAnchorOffset(5);
        renderer.setItemLabelsVisible(true);
        renderer.setBaseItemLabelsVisible(true);
        renderer.setMaximumBarWidth(.5);
        renderer.setMinimumBarLength(.5);
        renderer.setItemMargin(-2);

        
        barchartdataset = new DefaultCategoryDataset(); 
        
        plot = new CategoryPlot(); 
//        plot.setBackgroundPaint(Color.GRAY); 
//        plot.setDomainGridlinePaint(Color.white); 
//        plot.setRangeGridlinePaint(Color.white);
//        LegendTitle legend = new LegendTitle(plot); 
//        legend.setPosition(RectangleEdge.TOP); 
        plot.setDataset(barchartdataset); 
        plot.setRenderer(renderer); 
        plot.setDomainAxis(new CategoryLabelCustomizableCategoryAxis(""));
        plot.setRangeAxis(new NumberAxis(""));
        plot.setRangePannable(true);
        
        barchart = new JFreeChart(plot);
        barchart.removeLegend();
        barchart.setNotify(true);
//        barchart.setDomainZoomable(true);

        chartPanel = new ChartPanel(barchart);
//         chartPanel = new TooltipChartPanel(barchart);
//        chartPanel.setHorizontalAxisTrace(true); //十字显示
        chartPanel.setPreferredSize(new Dimension(1500, 350));
        chartPanel.setVerticalAxisTrace(true);
        chartPanel.setDomainZoomable(true);
        this.add(chartPanel);
//        this.add(getScrollBar(axis));
        
        
        
        JPopupMenu popupMenu = new JPopupMenu();
		mntmFenXiJiLu = new JMenuItem("分析记录");
//		popupMenu.add(mntmNewMenuItem);
		chartPanel.getPopupMenu().add(mntmFenXiJiLu);
   }
    
    /*
     * 
     */
    private JScrollBar getScrollBar( CategoryAxis axis)
    {
        double r1 = axis.getLowerMargin();
        double r2 = axis.getUpperMargin();
        JScrollBar scrollBar = new JScrollBar(JScrollBar.HORIZONTAL, 100, 100, 0, 400);
        scrollBar.addAdjustmentListener( new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                double x = e.getValue() *60 *60 * 1000;
//                axis.set.setRange(r1+x, r2+x);
            }
        });
        return scrollBar;
    }
    
    
}

/*
 * 设置X轴，如果有分析结果就显示为橙色
 */
class CategoryLabelCustomizableCategoryAxis extends CategoryAxis {

    private static final long serialVersionUID = 1L;
    protected BkChanYeLianTreeNode node;
	private String period;

    public CategoryLabelCustomizableCategoryAxis(String label) {
        super(label);
        super.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);
        super.setLowerMargin(0.01);
        super.setUpperMargin(0.01);
    }
    
    public Paint getTickLabelPaint(Comparable category)
    {
    	if(this.node == null)
    		return Color.black;
    	else {
    		LocalDate selecteddate = CommonUtility.formateStringToDate(category.toString());
    		NodeXPeriodDataBasic nodexdata = node.getNodeXPeroidData(period);
    		
    		if(nodexdata == null)
    			return Color.black;
    		else if(nodexdata.hasFxjgInPeriod(selecteddate, 0)) 
        		return Color.magenta.darker();
        	else 
        		return Color.black;
    	}
    }
    
    public void setDisplayNode (BkChanYeLianTreeNode curdisplayednode,String period) 
    {
    	this.node = curdisplayednode;
    	this.period = period;
    }
   
}
