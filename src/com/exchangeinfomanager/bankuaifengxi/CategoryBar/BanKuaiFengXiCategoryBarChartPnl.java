package com.exchangeinfomanager.bankuaifengxi.CategoryBar;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.ToolTipManager;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.annotations.Annotation;
import org.jfree.chart.annotations.CategoryPointerAnnotation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.CategoryItemEntity;

import org.jfree.chart.plot.CategoryMarker;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.ui.Layer;
import org.jfree.ui.LengthAdjustmentType;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;

import com.exchangeinfomanager.bankuaifengxi.BanKuaiGeGuMatchConditionListener;
import com.exchangeinfomanager.bankuaifengxi.BarChartPanelDataChangedListener;
import com.exchangeinfomanager.bankuaifengxi.BarChartPanelHightLightColumnListener;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.TDXNodes;

import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;

import java.awt.Color;

import java.awt.Font;
import java.awt.Paint;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.BoxLayout;

import javax.swing.JMenuItem;


public abstract class BanKuaiFengXiCategoryBarChartPnl extends JPanel 
	implements BarChartPanelDataChangedListener, BarChartPanelHightLightColumnListener , BanKuaiGeGuMatchConditionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Create the panel.
	 */
	public BanKuaiFengXiCategoryBarChartPnl(String charttype1) 
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		createChartPanel();
		
		createEvent ();
		
		charttype = charttype1;
	}

	private static Logger logger = Logger.getLogger(BanKuaiFengXiCategoryBarChartPnl.class);
	protected String charttype;
	private TDXNodes curdisplayednode;	
	protected String globeperiod = "WEEK";
	protected int shoulddisplayedmonthnum;
//	private String rowKey;
	private Boolean allowdrawannoation;
	
	protected CategoryPlot plot;
	protected ChartPanel chartPanel;
	protected DefaultCategoryDataset barchartdataset ;
	protected DefaultCategoryDataset linechartdataset;
	protected DefaultCategoryDataset averagelinechartdataset; //大盘周平均成交额
	protected JFreeChart barchart;
	private List<Marker> categorymarkerlist;
	protected List<Marker> valuemarkerlist;
	private boolean enableChangeDisplayDateRange; //可以鼠标改变显示范围
	private boolean displayhuibuquekou = true;
	private boolean displayzhangdieting = true;
//	private Boolean displayaveragedailycje = false;
//	private Boolean displayzhanbishujuinline = false;
//	private SystemConfigration sysconfig;
	
	public static final String SELECTED_PROPERTY = "selected";
	public static final String MOUSEDOUBLECLICK_PROPERTY = "mousedoubleclick";
	public static final String DISPLAYZHANGDIETING = "zhangdieting";
	public static final String DISPLAYQUEKOUDATA = "quekou";
	public static final String ONLYSHOWCJEZBBARDATA = "onlyshowcjezhanbibardata";
	public static final String ONLYSHOWCJLZBBARDATA = "onlyshowcjlzhanbibardata";
	public static final String CJEZBTOLINE = "cjezbtoline";
	public static final String CJLZBTOLINE = "cjlzbtoline";
	public static final String AVERAGEDAILYCJE = "averagedailycjeline";
	public static final String AVERAGEDAILYCJL = "averagedailycjlline";
	public static final String COMPAREAVERAGEDAILYCJEWITHDAPAN = "compareaveragedailycjewithdapan";
	public static final String COMPAREAVERAGEDAILYCJLWITHDAPAN = "compareaveragedailycjlwithdapan";
	public static final String DISPLAYNODENEWRANGEDATA = "displaynodenewrangedata";

	protected boolean selectchanged;

	protected LocalDate dateselected;
	protected LocalDate displaydatestarted;
	protected LocalDate displaydateended;
	protected Double yaxisvaluewhenmouseclicked;
	
	protected PropertyChangeSupport pcs = new PropertyChangeSupport(this); //	https://stackoverflow.com/questions/4690892/passing-a-value-between-components/4691447#4691447
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
	        pcs.addPropertyChangeListener(listener);
	}
	/*
	 * 
	 */
	public void setDrawQueKouLine (Boolean draw)
	{
		this.displayhuibuquekou = draw;
	}
	public Boolean shouldDrawQueKouLine ()
	{
		return this.displayhuibuquekou;
	}
	public void setChangeNodeDisplayDateRange (Boolean enable)
	{
		enableChangeDisplayDateRange = enable;
	}
	public Boolean couldChangeNodeDisplayDateRange ()
	{
		return enableChangeDisplayDateRange ;
	}
	/*
	 * 
	 */
	public void setDrawZhangDieTingLine (Boolean draw)
	{
		this.displayzhangdieting = draw;
	}
	public Boolean shouldDrawZhangDieTingLine ()
	{
		return this.displayzhangdieting;
	}
	
//	public void setDisplayZhanBiInLine (Boolean draw)
//	{
//		this.displayzhanbishujuinline = draw;
//		if(this.displayzhanbishujuinline) 
//			this.mntmCjeCjlZblineDate.setText("X 占比柱图转线图");
//		else
//			this.mntmCjeCjlZblineDate.setText("占比柱图转线图");
//	}
//	public Boolean shouldDisplayZhanBiInLine ()
//	{
//		return this.displayzhanbishujuinline;
//	}
	/*
	 * 
	 */
	public void setAllowDrawAnnoation (Boolean allowornot)
	{
		this.allowdrawannoation = allowornot;
	}
	public Boolean isAllowDrawAnnoation ()
	{
		if(this.allowdrawannoation == null)
			return false;
		else
			return this.allowdrawannoation ;
	}
	/*
	 * 
	 */
	public LocalDate getCurSelectedDate ()
	{
		return this.dateselected;
	}
	/*
	 * 
	 */
	protected void setCurDisplayNode (TDXNodes curdisplayednode1, LocalDate start, LocalDate end , String period) 
	{
		this.curdisplayednode = curdisplayednode1;
		this.globeperiod = period;
//		this.rowKey = this.curdisplayednode.getMyOwnCode();
		 
		BanKuaiFengXiCategoryBarRenderer render = (BanKuaiFengXiCategoryBarRenderer)plot.getRenderer();
		render.setDisplayNode(this.curdisplayednode);
		
		NodeXPeriodData nodexdata = this.curdisplayednode.getNodeXPeroidData(period);
		render.setDisplayNodeXPeriod(nodexdata);
		
		if(curdisplayednode1.getMyOwnCode().equals("999999")) {
			this.setDaZiJinValueMarker(100000000000.0);
		} else 
		if(curdisplayednode1.getType() == BkChanYeLianTreeNode.DAPAN ) {
			this.setDaZiJinValueMarker(300000000000.0);
		}
		
		displaydatestarted = start;
		displaydateended = end;
	}
	public TDXNodes getCurDisplayedNode ()
	{
		return this.curdisplayednode;
	}
	protected String getRowKey ()
	{
		return this.curdisplayednode.getMyOwnCode();
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
		this.plot.addRangeMarker(3,marker, Layer.FOREGROUND);
	}
	/*
	 * 
	 */
	public void setAnnotations (LocalDate columnkey )
	{
	     //特别标记选定的周，作用在界面上操作后会体会出来。
//        plot.clearAnnotations();
        List<Annotation> ann = plot.getAnnotations();
        Iterator<Annotation> i = ann.iterator();
        while (i.hasNext()) {
        	Annotation tmpan = i.next(); // must be called before you can call i.remove()
        	if(tmpan instanceof CategoryPointerAnnotation) {
        		String tmptext = ((CategoryPointerAnnotation)tmpan).getText();
            	if(tmptext.equals("选定周"))
            		i.remove();
        	}
        }
        
        if(isAllowDrawAnnoation ())
        	markASpecificColumn (columnkey,"选定周");
	}
	/*
	 *特别标记某个日期 
	 */
	protected void markASpecificColumn (LocalDate markdate,String markstring)
	{
		Number dvalue = barchartdataset.getValue( this.getRowKey(), markdate );
        try {
	        
	        CategoryPointerAnnotation pointeronemonth = new CategoryPointerAnnotation(markstring, markdate, dvalue.doubleValue() , 180 );
//	        pointeronemonth.setBaseRadius(0.0);
//	        pointeronemonth.setTipRadius(25.0);
	        pointeronemonth.setFont(new Font("SansSerif", Font.BOLD, 9));
	        pointeronemonth.setPaint(Color.BLACK);
	        pointeronemonth.setTextAnchor(TextAnchor.CENTER);
			this.plot.addAnnotation(pointeronemonth);
        } catch (java.lang.NullPointerException e) {}
	}
	/*
	 * 
	 */
	public LocalDate getCurrentDisplayOhlcStartDate ()
	{
		return displaydatestarted;
	}
	public LocalDate getCurrentDisplayOhlcEndDate ()
	{
		return displaydateended;
	}
	/*
	 * 区分出bar的年份或者月份
	 */
	protected void decorateXaxisWithYearOrMonth (String monthoryear)
	{
//		CategoryLabelCustomizableCategoryAxis domainAxis = (CategoryLabelCustomizableCategoryAxis)this.plot.getDomainAxis();
		List<LocalDate> columnlocaldates = barchartdataset.getColumnKeys();
		for(int i =0 ; i < columnlocaldates.size(); i ++)  {
			int curyear = columnlocaldates.get(i).getYear();
			int curmonth = columnlocaldates.get(i).getMonthValue();
			int lastyear;
			int lastmonth;
			try {
				 lastyear = columnlocaldates.get(i-1).getYear();
				 lastmonth = columnlocaldates.get(i-1).getMonthValue();
			} catch (java.lang.IndexOutOfBoundsException ex) {
				lastyear = curyear;
				lastmonth = curmonth;
			}
			
			if(curyear != lastyear && monthoryear.toLowerCase().equals("year")) {
				CategoryMarker marker = new CategoryMarker(columnlocaldates.get(i));  // position is the value on the axis
				marker.setPaint(Color.gray);
				marker.setAlpha(0.5f);
				marker.setLabelAnchor(RectangleAnchor.TOP);
				marker.setLabelTextAnchor(TextAnchor.TOP_CENTER);
				marker.setLabelOffsetType(LengthAdjustmentType.CONTRACT);
				marker.setLabel(String.valueOf(curyear)); // see JavaDoc for labels, colors, strokes
				marker.setDrawAsLine(true);
				this.plot.addDomainMarker(marker,Layer.BACKGROUND);
				this.categorymarkerlist.add(marker);
			}
			if(monthoryear.toLowerCase().equals("month") && curmonth != lastmonth ) {
				CategoryMarker marker = new CategoryMarker(columnlocaldates.get(i));  // position is the value on the axis
				marker.setPaint(Color.gray);
				marker.setAlpha(0.5f);
				marker.setLabelAnchor(RectangleAnchor.TOP);
				marker.setLabelTextAnchor(TextAnchor.TOP_CENTER);
				marker.setLabelOffsetType(LengthAdjustmentType.CONTRACT);
				marker.setLabel(String.valueOf(String.valueOf(curyear).substring(2, 4) + "-" + curmonth)); // see JavaDoc for labels, colors, strokes
				marker.setDrawAsLine(true);
				this.plot.addDomainMarker(marker,Layer.BACKGROUND);
				this.categorymarkerlist.add(marker);
			}
		}
	}
	/*
	 * 
	 */
	protected void setPanelTitle (String type, LocalDate displayedenddate1)
	{
		String nodecode = curdisplayednode.getMyOwnCode();
		String nodename = curdisplayednode.getMyOwnName();
		
//		LocalDate requireend = displayedenddate1.with(DayOfWeek.SATURDAY);
//		LocalDate requirestart = displayedenddate1.with(DayOfWeek.MONDAY).minus(sysconfig.banKuaiFengXiMonthRange(),ChronoUnit.MONTHS);

//		String tooltip = "\"" + nodecode + nodename + "\"" + type 
//				+ "从" + CommonUtility.formatDateYYYY_MM_DD(requirestart) 
//				+ "到" + CommonUtility.formatDateYYYY_MM_DD(requireend);
    	this.setToolTipText(nodecode+ nodename );
	}
	/*
	 * 
	 */
	public void resetLineDate ()
	{
		if(linechartdataset != null)
			linechartdataset.clear();
		
		plot.getRangeAxis(3).setRange(0.0, 1.0);
	}
	protected void resetExtremeZhanbiLevelMarkerlist ()
	{
		for(Marker marker : this.valuemarkerlist) 
			this.plot.removeRangeMarker(marker);
		
//		this.plot.clearDomainMarkers();
		this.valuemarkerlist.clear();
	}
	public void resetDate ()
	{
		if(barchartdataset != null)
			barchartdataset.clear();
		
		resetLineDate ();
		
		for(Marker marker : this.categorymarkerlist) 
			this.plot.removeDomainMarker(marker);
		this.categorymarkerlist.clear();
		
		resetExtremeZhanbiLevelMarkerlist ();

		this.plot.clearAnnotations();
		
		this.chartPanel.removeAll();
		
		((BanKuaiFengXiCategoryBarRenderer)plot.getRenderer(0)).resetBarColumnShouldChangeColor();
		
		this.barchart.fireChartChanged();//必须有这句
	}
	/*
    * 
    */
    private void createEvent ()
    {
    	chartPanel.addChartMouseListener(new ChartMouseListener() {
    		int xbeforeclick; int xafterclick;

    	    public void chartMouseClicked(ChartMouseEvent cme) {

    	    	java.awt.event.MouseEvent me = cme.getTrigger();
    	    	
    	    	if (me.getClickCount() == 2) {
    	    		PropertyChangeEvent evt;
    	    		if(dateselected != null)
    	    			evt = new PropertyChangeEvent(this, BanKuaiFengXiCategoryBarChartPnl.MOUSEDOUBLECLICK_PROPERTY, curdisplayednode.getMyOwnCode(), charttype + "," + dateselected.toString() );
    	    		else
    	    			evt = new PropertyChangeEvent(this, BanKuaiFengXiCategoryBarChartPnl.MOUSEDOUBLECLICK_PROPERTY, curdisplayednode.getMyOwnCode(), charttype + ",MoustDoubleClicked" );
    	    		
    	            pcs.firePropertyChange(evt);
    	    		
    	    	} else if (me.getClickCount() == 1) {
//    	    		xbeforeclick = me.getX();
    	    		
    	    		try {
    	    			Point2D po = chartPanel.translateScreenToJava2D(cme.getTrigger().getPoint());
            	        Rectangle2D plotArea = chartPanel.getScreenDataArea();
            	        yaxisvaluewhenmouseclicked = plot.getRangeAxis(0).java2DToValue(po.getY(), plotArea, plot.getRangeAxisEdge());
            	        
////            	        plot.removeDomainMarker(marker);
//            	        ValueMarker marker = new ValueMarker(chartY);
//            	        marker.setPaint(Color.red);
//            	        plot.addRangeMarker(3,marker, Layer.FOREGROUND);
            	        
    	    			CategoryItemEntity xyitem = (CategoryItemEntity) cme.getEntity(); // get clicked entity
    	    	        LocalDate columnkey = LocalDate.parse(xyitem.getColumnKey().toString());
            	        String selectedtooltip = xyitem.getToolTipText();
            	        
            	        setCurSelectedBarInfo (columnkey,selectedtooltip);

            	        selectchanged = true;
        	    	} catch ( java.lang.ClassCastException e ) {selectchanged = false;}

    	    	}
    	    }
    	    @Override
			public void chartMouseMoved(ChartMouseEvent cme) {
    	    	ToolTipManager.sharedInstance().setDismissDelay(60000); //让tooltips显示时间延长
//    	    	java.awt.event.MouseEvent me = cme.getTrigger();
//    	    	xafterclick = me.getX();
//    	    	if(xafterclick - xbeforeclick >50 )
//    	    		System.out.println("mouse have move oer 50");
			}
    	});
    	chartPanel.addMouseListener(new MouseListener() {
    		Point  pointStart = null;
            Point pointEnd   = null;
			
			@Override
			public void mousePressed(MouseEvent e) {
				 pointStart = e.getPoint();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if(!enableChangeDisplayDateRange )
					return;
				
				pointEnd = e.getPoint();
				if( ! (Math.abs(this.pointStart.x - this.pointEnd.x) > 50) || ! (Math.abs(this.pointStart.y - this.pointEnd.y) < 10)  )
					return;
				PropertyChangeEvent evt;
				if(this.pointStart.x - this.pointEnd.x >0)
					evt = new PropertyChangeEvent(this, BanKuaiFengXiCategoryBarChartPnl.DISPLAYNODENEWRANGEDATA, curdisplayednode.getMyOwnCode(), "FORWARD" );
				else
					evt = new PropertyChangeEvent(this, BanKuaiFengXiCategoryBarChartPnl.DISPLAYNODENEWRANGEDATA, curdisplayednode.getMyOwnCode(), "BACKWARD" );
				pcs.firePropertyChange(evt);
			}

			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
			@Override
			public void mouseClicked(MouseEvent e) {}
    	});
    	
//    	chartPanel.addMouseMotionListener(new MouseMotionAdapter() {
//             public void mouseMoved(MouseEvent e) {
//                 pointEnd = e.getPoint();
//             }
//
//             public void mouseDragged(MouseEvent e) {
//                 pointEnd = e.getPoint();
//                 repaint();
//             }
//         });
    }
    
    public abstract void reDisplayNodeDataOnDirection(String forwardbackward);
//    /*
//     * 用户可以定义显示的颜色
//     */
//    public void setBarDisplayedColor (Color colorindex)
//    {
//    	((BanKuaiFengXiCategoryBarRenderer)plot.getRenderer()).setBarDisplayedColor(colorindex);
//    }
	/*
     * 设置要突出显示的bar
     */
    @Override
    public void highLightSpecificBarColumn (LocalDate selecteddate)
    {
    	if(selecteddate == null)
    		return;
   	
    	int indexforbar = this.barchartdataset.getColumnIndex(selecteddate) ;
    	if(indexforbar != -1)
    		((BanKuaiFengXiCategoryBarRenderer)plot.getRenderer(0)).setBarColumnShouldChangeColor(indexforbar);
    	
    	int indexforline = this.linechartdataset.getColumnIndex(selecteddate) ;
    	if(indexforline != -1) {
    		((BanKuaiFengXiCategoryLineRenderer)plot.getRenderer(3)).setBarColumnShouldChangeColor(indexforline);
    	}
    	
        this.dateselected = selecteddate;
        //特别标记选定的周，作用在界面上操作后会体会出来。
//        plot.clearAnnotations();
        
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
    	BkfxItemLabelGenerator itemlabelg = (BkfxItemLabelGenerator)((BanKuaiFengXiCategoryBarRenderer)plot.getRenderer()).getBaseItemLabelGenerator();
    	itemlabelg.setDisplayedMaxWkLevel(maxl);
    	barchart.fireChartChanged();//必须有这句
    }

	/*
	 * 
	 */
	public void setCurSelectedBarInfo (LocalDate newdate,String selectedtooltip) 
	{
        this.dateselected = newdate ;

        PropertyChangeEvent evt = new PropertyChangeEvent(this, SELECTED_PROPERTY, "",  this.dateselected );
        pcs.firePropertyChange(evt);
    }
	/*
	 * 
	 */
	abstract public String getToolTipSelected ();
	/*
	 * 
	 */
	public void displayQueKou (boolean display)
	{
		if(display)
			this.displayhuibuquekou = true;
		else 
			this.displayhuibuquekou = false;
	}
    
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
//    	BanKuaiFengXiCategoryBarRenderer renderer = new BanKuaiFengXiCategoryBarRenderer ();
//        DecimalFormat decimalformate = new DecimalFormat("%#0.000");
//        renderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}",decimalformate));
//        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12,TextAnchor.HALF_ASCENT_CENTER));
//        renderer.setItemLabelAnchorOffset(5);
//        renderer.setItemLabelsVisible(true);
//        renderer.setBaseItemLabelsVisible(true);
//        renderer.setMaximumBarWidth(.5);
//        renderer.setMinimumBarLength(.5);
//        renderer.setItemMargin(-2);
        
        plot = new CategoryPlot(); 
//        plot.setBackgroundPaint(Color.GRAY); 
//        plot.setDomainGridlinePaint(Color.white); 
//        plot.setRangeGridlinePaint(Color.white);
//        LegendTitle legend = new LegendTitle(plot); 
//        legend.setPosition(RectangleEdge.TOP); 
        plot.setRangePannable(true);
        

        CategoryLabelCustomizableCategoryAxis domainaxis = new CategoryLabelCustomizableCategoryAxis("");
        plot.setDomainAxis(domainaxis);
        domainaxis.setVisible(false);
//        domainaxis.setAxisLineVisible(true);
//        domainaxis.setTickLabelsVisible(true);
//        domainaxis.setTickMarksVisible(true);
        
        barchartdataset = new DefaultCategoryDataset();
        plot.setDataset(0,barchartdataset);
        plot.setRangeAxis(0,new NumberAxis(""));
        plot.getRangeAxis(0).setVisible(false);
        plot.mapDatasetToRangeAxis(0, 0);
        //line part
        linechartdataset = new DefaultCategoryDataset();
        plot.setDataset(3, linechartdataset);
//        BanKuaiFengXiCategoryLineRenderer lineqkrenderer = new BanKuaiFengXiCategoryLineRenderer ();
//        plot.setRenderer(3, lineqkrenderer);
        ValueAxis rangeAxis2 = new NumberAxis("");
        rangeAxis2.setVisible(false);
        plot.setRangeAxis(3, rangeAxis2);
		plot.mapDatasetToRangeAxis(3, 3);
		
//		plot.mapDatasetToDomainAxis(0, 0);
//		plot.mapDatasetToDomainAxis(3, 0);
		
		plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
        //
        barchart = new JFreeChart(plot);  
        barchart.removeLegend();
        barchart.setNotify(true);
//      barchart.setDomainZoomable(true);

        chartPanel = new ChartPanel(barchart);
//      chartPanel = new TooltipChartPanel(barchart);
//      chartPanel.setHorizontalAxisTrace(true); //十字显示
//      chartPanel.setPreferredSize(new Dimension(1500, 350));
        chartPanel.setVerticalAxisTrace(true);
        chartPanel.setDomainZoomable(true);
        this.add(chartPanel);
        
		this.categorymarkerlist = new ArrayList<> ();
		this.valuemarkerlist = new ArrayList<> ();
   }
 
    protected  JPopupMenu getPopupMenu() 
    {
    	return chartPanel.getPopupMenu();
    }
    /*
     * 
     */
	protected void redecorateExtremeZhanbiLevel(Double[] zblevel) 
	{
		resetExtremeZhanbiLevelMarkerlist ();
		barchart.setNotify(false);
		if(zblevel[0] != null) {
			ValueMarker downmarker = new ValueMarker(zblevel[0]);
			downmarker.setPaint(Color.red);
		    plot.addRangeMarker(0,downmarker, Layer.FOREGROUND);
		    this.valuemarkerlist.add(downmarker);
		}
		if(zblevel[1] != null) {
			ValueMarker upmarker = new ValueMarker(zblevel[1]);
			upmarker.setPaint(Color.GREEN);
		    plot.addRangeMarker(0,upmarker, Layer.FOREGROUND);
		    this.valuemarkerlist.add(upmarker);
		}
		barchart.setNotify(true);
		this.barchart.fireChartChanged();//必须有这句
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
        super.setAxisLineVisible(false);
    }
    
    public Paint getTickLabelPaint(Comparable category)
    {
    	if(this.node == null)
    		return Color.black;
    	else {
    		LocalDate selecteddate = CommonUtility.formateStringToDate(category.toString());
    		NodeXPeriodData nodexdata = ((TDXNodes)node).getNodeXPeroidData(period);
    		
    		try{
    		if(nodexdata == null)
    			return Color.black;
    			
    		else if(nodexdata.hasFxjgInPeriod(selecteddate, 0)) 
        		return Color.magenta.darker();
        	else 
        		return Color.BLACK;
    		} catch (java.lang.NullPointerException e) {
//    			System.out.println(period);
    			return Color.BLACK;
    			
    		}
//    		return null;
    		
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
