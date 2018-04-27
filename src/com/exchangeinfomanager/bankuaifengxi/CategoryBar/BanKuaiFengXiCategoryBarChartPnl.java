package com.exchangeinfomanager.bankuaifengxi.CategoryBar;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;

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
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.TextAnchor;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.bankuaifengxi.BarChartHightLightFxDataValueListener;
import com.exchangeinfomanager.bankuaifengxi.BarChartPanelDataChangedListener;
import com.exchangeinfomanager.bankuaifengxi.BarChartPanelHightLightColumnListener;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.gui.subgui.JiaRuJiHua;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;

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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
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

public abstract class BanKuaiFengXiCategoryBarChartPnl extends JPanel implements BarChartPanelDataChangedListener, BarChartPanelHightLightColumnListener ,BarChartHightLightFxDataValueListener
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
	protected CategoryPlot plot;
	protected ChartPanel chartPanel;
	protected DefaultCategoryDataset barchartdataset ;
	protected DefaultCategoryDataset linechartdataset;
	protected JFreeChart barchart;
//	private ArrayList<JiaRuJiHua> selectedfxjg;
	
	private SystemConfigration sysconfig;
	protected int shoulddisplayedmonthnum;
	private BanKuaiDbOperation bkdbopt;
	
//	private Set<BarChartPanelHightLightColumnListener> chartpanelhighlightlisteners;
	
	public static final String SELECTED_PROPERTY = "selected";
	protected boolean selectchanged;
	private String tooltipselected;
	private LocalDate dateselected;
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this); //	https://stackoverflow.com/questions/4690892/passing-a-value-between-components/4691447#4691447
	public void addPropertyChangeListener(PropertyChangeListener listener) {
	        pcs.addPropertyChangeListener(listener);
	}
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
//        	        CategoryDataset dataset = xyitem.getDataset(); // get data set
        	        LocalDate columnkey = LocalDate.parse(xyitem.getColumnKey().toString());
        	        String selectedtooltip = xyitem.getToolTipText();
        	        
        	        setCurSelectedBarInfo (columnkey,selectedtooltip);
        	        highLightSpecificBarColumn (columnkey);

        	        selectchanged = true;
    	    	} catch ( java.lang.ClassCastException e ) {
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
     * 设置要突出显示的bar
     */
    @Override
    public void highLightSpecificBarColumn (LocalDate selecteddate)
    {
    	if(selecteddate == null)
    		return;
    	
    	int cindex = barchartdataset.getColumnIndex(selecteddate) ;
    	if(cindex == -1)
    		return ;
    	
    	((BanKuaiFengXiCategoryBarRenderer)plot.getRenderer()).setBarColumnShouldChangeColor(cindex);
    	
        this.dateselected = selecteddate;
        this.barchart.fireChartChanged();//必须有这句
    }
    @Override
    public void highLightSpecificBarColumn (Integer columnindex)
    {
    	
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
	public LocalDate getCurSelectedBarDate ()
	{
		return dateselected;
	}
	/*
	 * 
	 */
	public void setCurSelectedBarInfo (LocalDate newdate,String selectedtooltip) 
	{
        String oldText = this.dateselected + this.tooltipselected;
        this.dateselected = newdate ;
        this.tooltipselected =  this.curdisplayednode.getMyOwnCode() + this.curdisplayednode.getMyOwnName() + ": " + selectedtooltip;
        PropertyChangeEvent evt = new PropertyChangeEvent(this, SELECTED_PROPERTY, oldText, this.dateselected.toString() + this.tooltipselected );
        pcs.firePropertyChange(evt);
    }
	/*
	 * 
	 */
	public String getToolTipSelected ()
	{
		if(tooltipselected != null)
			return this.curdisplayednode.getMyOwnCode() + this.curdisplayednode.getMyOwnName() + ": " + tooltipselected;
		else
			return null;
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
		
		//bar parts
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
        plot.setRangeAxis(new NumberAxis(""));
        plot.setRangePannable(true);

        plot.setDomainAxis(new CategoryLabelCustomizableCategoryAxis(""));
        //
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
        
        JPopupMenu popupMenu = new JPopupMenu();
		mntmFenXiJiLu = new JMenuItem("分析记录");
//		popupMenu.add(mntmNewMenuItem);
		chartPanel.getPopupMenu().add(mntmFenXiJiLu);
   }
 
    
}

/*
 * 设置X轴，如果有分析结果就显示为橙色
 */
class CategoryLabelCustomizableCategoryAxis extends CategoryAxis 
{
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
/*
 * 
 */
class  BanKuaiFengXiCategoryLineRenderer extends LineAndShapeRenderer
{
	public BanKuaiFengXiCategoryLineRenderer ()
	{
		super ();
	}
	
}