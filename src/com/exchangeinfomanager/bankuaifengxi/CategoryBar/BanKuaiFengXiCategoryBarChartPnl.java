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
import org.jfree.chart.annotations.CategoryPointerAnnotation;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.ChartEntity;

import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryMarker;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.Layer;
import org.jfree.ui.LengthAdjustmentType;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;


import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiInfoTableModel;

import com.exchangeinfomanager.bankuaifengxi.BarChartHightLightFxDataValueListener;
import com.exchangeinfomanager.bankuaifengxi.BarChartPanelDataChangedListener;
import com.exchangeinfomanager.bankuaifengxi.BarChartPanelHightLightColumnListener;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.nodexdata.NodeXPeriodDataBasic;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.google.common.base.Strings;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public abstract class BanKuaiFengXiCategoryBarChartPnl extends JPanel 
	implements BarChartPanelDataChangedListener, BarChartPanelHightLightColumnListener ,BarChartHightLightFxDataValueListener
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
	}
	
	private static Logger logger = Logger.getLogger(BanKuaiFengXiCategoryBarChartPnl.class);
	private TDXNodes curdisplayednode;	
	protected String globeperiod = "WEEK";
	protected int shoulddisplayedmonthnum;
	private String rowKey;
	private Boolean allowdrawannoation;
	
	
	protected CategoryPlot plot;
	protected ChartPanel chartPanel;
	protected DefaultCategoryDataset barchartdataset ;
//	protected DefaultCategoryDataset linechartdataset;
	protected JFreeChart barchart;
	private List<CategoryMarker> categorymarkerlist;
	
	protected boolean displayhuibuquekou;
	private SystemConfigration sysconfig;
	
	public static final String SELECTED_PROPERTY = "selected";
	public static final String MOUSEDOUBLECLICK_PROPERTY = "mousedoubleclick";
	protected boolean selectchanged;
	private String tooltipselected;
	protected LocalDate dateselected;
	
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this); //	https://stackoverflow.com/questions/4690892/passing-a-value-between-components/4691447#4691447
	public void addPropertyChangeListener(PropertyChangeListener listener) {
	        pcs.addPropertyChangeListener(listener);
	}
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
	protected void setCurDisplayNode (TDXNodes curdisplayednode1, String period) 
	{
		this.curdisplayednode = curdisplayednode1;
		this.rowKey = this.curdisplayednode.getMyOwnCode();
		 
		BanKuaiFengXiCategoryBarRenderer render = (BanKuaiFengXiCategoryBarRenderer)plot.getRenderer();
		render.setDisplayNode(this.curdisplayednode);
		
		NodeXPeriodDataBasic nodexdata = this.curdisplayednode.getNodeXPeroidData(period);
		render.setDisplayNodeXPeriod(nodexdata);
		
		
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
		this.plot.addRangeMarker(marker);
	}
	/*
	 * 
	 */
	public void setAnnotations (LocalDate columnkey )
	{
	     //特别标记选定的周，作用在界面上操作后会体会出来。
        plot.clearAnnotations();
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
        } catch (java.lang.NullPointerException e) {
        }
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
			} catch (java.lang.ArrayIndexOutOfBoundsException e) {
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
	public void resetDate ()
	{
		if(barchartdataset != null)
			barchartdataset.clear();
		
		if(linechartdataset != null)
			linechartdataset.clear();
		
		for(CategoryMarker marker : this.categorymarkerlist) {
			this.plot.removeDomainMarker(marker);
		}
		this.categorymarkerlist.clear();
		
//		List<CategoryPointerAnnotation> pointerlist = this.plot.getAnnotations();
//		for(CategoryPointerAnnotation pointer : pointerlist) 
//			this.plot.removeAnnotation(pointer);
		
//		if(!this.isAllowDrawAnnoation())
		this.plot.clearAnnotations();
		
		this.chartPanel.removeAll();
		
		this.barchart.fireChartChanged();//必须有这句
	}
	
   /*
    * 
    */
    private void createEvent ()
    {
    	chartPanel.addChartMouseListener(new ChartMouseListener() {

    	    public void chartMouseClicked(ChartMouseEvent cme) {
    	    	
    	    	
    	    	java.awt.event.MouseEvent me = cme.getTrigger();
    	    	if (me.getClickCount() == 2) {
    	    		PropertyChangeEvent evt;
    	    		if(dateselected != null)
    	    			evt = new PropertyChangeEvent(this, BanKuaiFengXiCategoryBarChartPnl.MOUSEDOUBLECLICK_PROPERTY, "", dateselected.toString() );
    	    		else
    	    			evt = new PropertyChangeEvent(this, BanKuaiFengXiCategoryBarChartPnl.MOUSEDOUBLECLICK_PROPERTY, "", "MoustDoubleClicked" );
    	    		
    	            pcs.firePropertyChange(evt);
    	    		
    	    	} else if (me.getClickCount() == 1) {
    	    		try {
    	    			CategoryItemEntity xyitem = (CategoryItemEntity) cme.getEntity(); // get clicked entity
    	    	        LocalDate columnkey = LocalDate.parse(xyitem.getColumnKey().toString());
            	        String selectedtooltip = xyitem.getToolTipText();
            	        
            	        setCurSelectedBarInfo (columnkey,selectedtooltip);
            	        
            	   

            	        selectchanged = true;
        	    	} catch ( java.lang.ClassCastException e ) {
            	        selectchanged = false;
        	    	}

    	    	}
    	    }
    	    @Override
			public void chartMouseMoved(ChartMouseEvent arg0) {
    	    	ToolTipManager.sharedInstance().setDismissDelay(60000); //让tooltips显示时间延长
			}
    	});
    	
//    	mntmHideBars.addActionListener(new ActionListener() {
//			@Override
//
//			public void actionPerformed(ActionEvent evt) 
//			{
//				
//			}
//    	});

    	
//    	mntmFenXiJiLu.addActionListener(new ActionListener() {
//			@Override
//
//			public void actionPerformed(ActionEvent evt) 
//			{
//				if(curdisplayednode == null)
//					return;
//				
//				String bkcode = curdisplayednode.getMyOwnCode();
//				JiaRuJiHua jiarujihua = new JiaRuJiHua ( bkcode,"分析结果" ); 
//				LocalDate curselectdate = CommonUtility.formateStringToDate( getCurSelectedBarDate ().toString() );
//				jiarujihua.setJiaRuDate (curselectdate);
//				int exchangeresult = JOptionPane.showConfirmDialog(null, jiarujihua, "输入分析结果", JOptionPane.OK_CANCEL_OPTION);
//				if(exchangeresult == JOptionPane.CANCEL_OPTION)
//					return;
//				
//				int autoIncKeyFromApi =	bkdbopt.setZdgzRelatedActions (jiarujihua);
//				
//				jiarujihua = null;
//			}
//			
//		});
    }
    /*
     * 用户可以定义显示的颜色
     */
    public void setBarDisplayedColor (Color colorindex)
    {
    	((BanKuaiFengXiCategoryBarRenderer)plot.getRenderer()).setBarDisplayedColor(colorindex);
    }
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
    	if(indexforline != -1)
    		((BanKuaiFengXiCategoryLineRenderer)plot.getRenderer(3)).setBarColumnShouldChangeColor(indexforline);
    	
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
//        String oldText = this.dateselected + this.tooltipselected;
        this.dateselected = newdate ;
        
//        String outputhtml = "";
//        org.jsoup.nodes.Document outputdoc = Jsoup.parse(outputhtml);
//        org.jsoup.select.Elements outputcontent = outputdoc.select("body");
//        for(org.jsoup.nodes.Element outputbody : outputcontent) {
//        	org.jsoup.nodes.Element div = outputbody.appendElement("div");
//        	org.jsoup.nodes.Element nodecode = div.appendElement("nodecode");
//    		nodecode.appendText(this.curdisplayednode.getMyOwnCode() + this.curdisplayednode.getMyOwnName());
//    		
//    		org.jsoup.nodes.Element nodetype = div.appendElement("nodetype");
//    		nodetype.appendText(String.valueOf(this.curdisplayednode.getType() ) );
//    				
//    		org.jsoup.nodes.Document doc = Jsoup.parse(selectedtooltip);
//    		org.jsoup.select.Elements lis = doc.select("dl");
//    		
////    			org.jsoup.select.Elements lis = body.select("dl");
//    			for(org.jsoup.nodes.Element li : lis) {
//    				div.appendChild(li);
//    			}
//    		
//        }
//        
//		String htmlstring = outputdoc.toString();
		
//		PropertyChangeEvent evt = new PropertyChangeEvent(this, SELECTED_PROPERTY, "",  htmlstring );
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
	/*
	 * 
	 */
//    public ChartPanel getChartPanel() {
//        return chartPanel;
//    }
    
	private JMenuItem mntmFenXiJiLu;
	protected DefaultCategoryDataset linechartdataset;
	protected JMenuItem mntmHideBars;
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
        
        barchartdataset = new DefaultCategoryDataset(); 
        
        plot = new CategoryPlot(); 
//        plot.setBackgroundPaint(Color.GRAY); 
//        plot.setDomainGridlinePaint(Color.white); 
//        plot.setRangeGridlinePaint(Color.white);
//        LegendTitle legend = new LegendTitle(plot); 
//        legend.setPosition(RectangleEdge.TOP); 
        plot.setDataset(0,barchartdataset); 
//        
        plot.setRangeAxis(0,new NumberAxis(""));
        plot.setRangePannable(true);

        CategoryLabelCustomizableCategoryAxis domainaxis = new CategoryLabelCustomizableCategoryAxis("");
        plot.setDomainAxis(domainaxis);
        
        //line part,for QueKou
        linechartdataset = new DefaultCategoryDataset();  
        BanKuaiFengXiCategoryLineRenderer linerenderer = new BanKuaiFengXiCategoryLineRenderer ();
        plot.setDataset(3, linechartdataset);
        plot.setRenderer(3, linerenderer);
        ValueAxis rangeAxis2 = new NumberAxis("");
        plot.setRangeAxis(3, rangeAxis2);
//        plot.setDomainAxis(3, domainaxis);
        
        plot.mapDatasetToRangeAxis(0, 0);
		plot.mapDatasetToRangeAxis(3, 3);
		
//		plot.mapDatasetToDomainAxis(0, 0);
//		plot.mapDatasetToDomainAxis(3, 0);
		
		plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
        //
        barchart = new JFreeChart(plot);  
        barchart.removeLegend();
        barchart.setNotify(true);
//        barchart.setDomainZoomable(true);

        chartPanel = new ChartPanel(barchart);
//         chartPanel = new TooltipChartPanel(barchart);
//        chartPanel.setHorizontalAxisTrace(true); //十字显示
//        chartPanel.setPreferredSize(new Dimension(1500, 350));
        chartPanel.setVerticalAxisTrace(true);
        chartPanel.setDomainZoomable(true);
        this.add(chartPanel);
        
//        JPopupMenu popupMenu = new JPopupMenu();
//		mntmFenXiJiLu = new JMenuItem("分析记录");
//		mntmqkopenup = new JMenuItem("统计新开向上跳空缺口");
//		mntmqkopendown = new JMenuItem("统计新开向下跳空缺口");
//		mntmqkhuibuup = new JMenuItem("统计回补上跳空缺口");
//		mntmqkhuibudown = new JMenuItem("统计回补下跳空缺口");
//        mntmHideBars = new JMenuItem("隐藏占比数据");
//		chartPanel.getPopupMenu().add(mntmHideBars);
		
		this.categorymarkerlist = new ArrayList<> ();
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
    		NodeXPeriodDataBasic nodexdata = ((TDXNodes)node).getNodeXPeroidData(period);
    		
    		try{
    		if(nodexdata == null)
    			return Color.black;
    		else if(nodexdata.hasFxjgInPeriod(selecteddate, 0)) 
        		return Color.magenta.darker();
        	else 
        		return Color.black;
    		} catch (java.lang.NullPointerException e) {
//    			System.out.println(period);
    			return Color.black;
    			
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
