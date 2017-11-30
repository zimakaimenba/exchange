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
	
	private BkChanYeLianTreeNode curdisplayednode;	
	private LocalDate displayedenddate;
	private String pnltitle;
	private CategoryPlot plot;
	private ChartPanel chartPanel;
	private DefaultCategoryDataset barchartdataset ;
	private JFreeChart barchart;
	private Comparable dateselected;
	private String tooltipselected;
	private SystemConfigration sysconfig;
	private int shoulddisplayedmonthnum;
	

	
	/*
	 * 板块/股票按周交易额
	 */
	public void setNodeJiaoYiErByWeek (BkChanYeLianTreeNode node,LocalDate displayedenddate1,DaPan dapan)
	{
		this.curdisplayednode = node;

		LocalDate requireend = displayedenddate1.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = displayedenddate1.with(DayOfWeek.MONDAY).minus(this.shoulddisplayedmonthnum,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		
//		this.displayedenddate = requireend; 
		
		barchartdataset = new DefaultCategoryDataset();
		
		for(LocalDate tmpdate = requirestart;tmpdate.isBefore( requireend) || tmpdate.isEqual(requireend); tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ){
			ChenJiaoZhanBiInGivenPeriod tmprecord = node.getSpecficChenJiaoErRecord(tmpdate);
			if(tmprecord != null) {
				Double chenjiaoer = tmprecord.getMyOwnChengJiaoEr();
				LocalDate lastdayofweek = tmprecord.getRecordsDayofEndofWeek();
				barchartdataset.setValue(chenjiaoer,"成交额",lastdayofweek);
			} else {
				if( !dapan.isThisWeekXiuShi(tmpdate) )
					barchartdataset.setValue(0.0,"成交额",tmpdate);
				else //为空说明该周市场没有交易
					continue;
			}
		}

		DecimalFormat decimalformate = new DecimalFormat("#0.000");
		((CustomRenderer) plot.getRenderer()).setItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}",decimalformate));
		CustomToolTipGeneratorForChenJiaoEr custotooltip = new CustomToolTipGeneratorForChenJiaoEr();
		custotooltip.setDisplayNode(curdisplayednode);
		((CustomRenderer) plot.getRenderer()).setSeriesToolTipGenerator(0,custotooltip);
		((CustomRenderer) plot.getRenderer()).setToolTipGenerator(new CustomToolTipGeneratorForChenJiaoEr());
		((CustomRenderer)plot.getRenderer()).setBarCharType("成交额");
		plot.setDataset(barchartdataset);
		
		setPanelTitle ("成交额",displayedenddate1);
		
//		((CustomToolTipGeneratorForChenJiaoEr)((CustomRenderer) plot.getRenderer()).getSeriesToolTipGenerator (0)).s.;
	}
	/*
	 * 板块大盘/个股板块 占比
	 */
	public void setNodeZhanBiByWeek (BkChanYeLianTreeNode node, LocalDate displayedenddate1, DaPan dapan)
	{
		this.curdisplayednode = node;
//		this.displayedenddate = displayedenddate1;
		LocalDate requireend = displayedenddate1.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = displayedenddate1.with(DayOfWeek.MONDAY).minus(this.shoulddisplayedmonthnum,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		
		barchartdataset = new DefaultCategoryDataset();
		
		for(LocalDate tmpdate = requirestart;tmpdate.isBefore( requireend) || tmpdate.isEqual(requireend); tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ){
			ChenJiaoZhanBiInGivenPeriod tmprecord = node.getSpecficChenJiaoErRecord(tmpdate);
			if(tmprecord != null) {
				Double chenjiaoer = tmprecord.getCjlZhanBi();
				LocalDate lastdayofweek = tmprecord.getRecordsDayofEndofWeek();
				barchartdataset.setValue(chenjiaoer,"占比",lastdayofweek);
			} else {
				if( !dapan.isThisWeekXiuShi(tmpdate) )
					barchartdataset.setValue(0.0,"占比",tmpdate);
				else //为空说明该周市场没有交易
					continue;
			}
			
		}
		
		DecimalFormat decimalformate = new DecimalFormat("%#0.000");
		((CustomRenderer) plot.getRenderer()).setItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}",decimalformate));
//		((CustomRenderer) plot.getRenderer()).setSeriesToolTipGenerator(0,new CustomToolTipGeneratorForZhanBi());
		CustomToolTipGeneratorForZhanBi custotooltip = new CustomToolTipGeneratorForZhanBi();
		custotooltip.setDisplayNode(curdisplayednode);
		((CustomRenderer) plot.getRenderer()).setToolTipGenerator(custotooltip);
        
		((CustomRenderer)plot.getRenderer()).setBarCharType("占比");
		plot.setDataset(barchartdataset);
		
		setPanelTitle ("占比",displayedenddate1);
	}
	/*
	 * 个股和大盘的占比
	 */
	public void setNodeAndDaPanZhanBiByWeek (BkChanYeLianTreeNode node, LocalDate displayedenddate1, DaPan dapan)
	{
		this.curdisplayednode = node;
//		this.displayedenddate = displayedenddate1;
		LocalDate requireend = displayedenddate1.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = displayedenddate1.with(DayOfWeek.MONDAY).minus(this.shoulddisplayedmonthnum,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		
		barchartdataset = new DefaultCategoryDataset();
		
		for(LocalDate tmpdate = requirestart;tmpdate.isBefore( requireend) || tmpdate.isEqual(requireend); tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ){
			ChenJiaoZhanBiInGivenPeriod tmpggrecord = node.getSpecficChenJiaoErRecord(tmpdate);
			ChenJiaoZhanBiInGivenPeriod tmpdprecord = dapan.getSpecficChenJiaoErRecord(tmpdate); //返回的是上证或深圳的某个记录，里面uplevel记录的是整个大盘的成交额
			
			if(tmpggrecord != null) {
				Double ggchenjiaoer = tmpggrecord.getMyOwnChengJiaoEr();
				Double dpchenjiaoer = tmpdprecord.getUpLevelChengJiaoEr(); //返回的是上证或深圳的某个记录，里面uplevel记录的是整个大盘的成交额
				Double ggdpratio = ggchenjiaoer/dpchenjiaoer; 
				LocalDate lastdayofweek = tmpggrecord.getRecordsDayofEndofWeek();
				
				barchartdataset.setValue(ggdpratio,"占比",lastdayofweek);
			} else {
				if( !dapan.isThisWeekXiuShi(tmpdate) )
					barchartdataset.setValue(0.0,"占比",tmpdate);
				else //为空说明该周市场没有交易
					continue;
			}
		}
		
		DecimalFormat decimalformate = new DecimalFormat("%#0.000");
		((CustomRenderer) plot.getRenderer()).setItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}",decimalformate));
		((CustomRenderer) plot.getRenderer()).setSeriesToolTipGenerator(0,new CustomToolTipGeneratorForZhanBi());
		CustomToolTipGeneratorForZhanBi custotooltip = new CustomToolTipGeneratorForZhanBi();
		custotooltip.setDisplayNode(curdisplayednode);
		((CustomRenderer) plot.getRenderer()).setToolTipGenerator(custotooltip);
        
		((CustomRenderer)plot.getRenderer()).setBarCharType("占比");
		plot.setDataset(barchartdataset);
		
		setPanelTitle ("占比",displayedenddate1);
		
		this.setDaZiJinValueMarker(0.001); //大于0.1说明个股强势，资金占的多
		this.setDaZiJinValueMarker(0.0005); //大于0.1说明个股强势，资金占的多
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
	private void setPanelTitle (String type, LocalDate displayedenddate1)
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
		((CustomRenderer)plot.getRenderer()).setBarColumnShouldChangeColor(-1);
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
    	((CustomRenderer)plot.getRenderer()).setBarColumnShouldChangeColor(cindex);
    	
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
        plot.setRangePannable(true);
//        plot.setDomainPannable(true);
        ((CustomRenderer) plot.getRenderer()).setBarPainter(new StandardBarPainter());
        CategoryAxis axis = plot.getDomainAxis();
        axis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);

        barchart = new JFreeChart(plot);
        barchart.removeLegend();
        barchart.setNotify(true);

        chartPanel = new ChartPanel(barchart);
        this.add(chartPanel);
        
    }

}

class CustomRenderer extends BarRenderer 
{
	private static final long serialVersionUID = 1L;
//	private Paint[] colors;
    private int shouldcolumn = -1;
    private String barCharType;
   
    public CustomRenderer() {
        super();
    }

    public Paint getItemPaint(final int row, final int column) 
    {
        if(column == shouldcolumn)
            return Color.blue;
        else   if(this.barCharType.equals("占比"))
            return Color.RED;
        else 
        	return Color.orange;
   }
    public void setBarColumnShouldChangeColor (int column)
    {
    	this.shouldcolumn = column;
    }
    public void setBarCharType(String type) 
    {
    	this.barCharType = type;
    }
    
    
}

class CustomToolTipGeneratorForZhanBi implements CategoryToolTipGenerator  {
    private BkChanYeLianTreeNode node;

	public String generateToolTip(CategoryDataset dataset, int row, int column)  
    {
		String selected = dataset.getColumnKey(column).toString();
    	LocalDate selecteddate = CommonUtility.formateStringToDate(selected);
    	String tooltip = selected.toString();
    	 
    	ChenJiaoZhanBiInGivenPeriod nodefx = null;
		if(node.getType() == 4 )
			nodefx = ((BanKuai)node).getNodeFengXiResultForSpecificDate (selecteddate);
		else if(node.getType() == 6 ) 
			nodefx = ((Stock)node).getNodeFengXiResultForSpecificDate (selecteddate);
		
		if(nodefx == null)
			return "";
		
		if(node.getType() == 4 ) { //板块
			Double curzhanbidata = (Double)dataset.getValue(row, column);  //占比
			Double zhanbigrowthrate = nodefx.getGgbkzhanbigrowthrate();
			Double cjezhanbi = nodefx.getGgbkcjegrowthzhanbi();
			Integer maxweek = nodefx.getGgbkzhanbimaxweek();
			
			DecimalFormat decimalformate = new DecimalFormat("%#0.000");
			try {
				tooltip = tooltip +  "占比" + decimalformate.format(curzhanbidata) ;
			} catch (java.lang.IllegalArgumentException e ) {
				tooltip = tooltip  +  "占比NULL";
			}
			try {
				tooltip = tooltip +   "占比变化("	+ decimalformate.format(zhanbigrowthrate) +  ")";
			} catch (java.lang.IllegalArgumentException e ) {
				tooltip = tooltip  +  "占比变化(NULL)";
			}
			try {
				tooltip = tooltip +  "MaxWeek=" + maxweek.toString() ;
			} catch (java.lang.IllegalArgumentException e ) {
				tooltip = tooltip + "MaxWeek=NULL";
			}
			
//			tooltip = tooltip + "占比" + decimalformate.format(curzhanbidata) 
//						+  "占比变化("	+ decimalformate.format(zhanbigrowthrate) +  ")" 
//						+ "MaxWeek=" + maxweek.toString()
//						;
			
			return tooltip;
		} else if(node.getType() == 6 ) { //个股
			Double curzhanbidata = nodefx.getCjlZhanBi();  //占比
			Double zhanbigrowthrate = nodefx.getGgbkzhanbigrowthrate();
			Double cjezhanbi = nodefx.getGgbkcjegrowthzhanbi();
			Integer maxweek = nodefx.getGgbkzhanbimaxweek();
			Double dpzhanbi = nodefx.getGgdpzhanbi();
			Integer dpzhanbimaxweek = nodefx.getGgdpzhanbimaxweek();
			
			
			DecimalFormat decimalformate = new DecimalFormat("%#0.000");
			try {
				tooltip = tooltip + "板块占比" + decimalformate.format(curzhanbidata);
			} catch (java.lang.IllegalArgumentException e ) {
				tooltip = tooltip + "板块占比NULL" ;
			}
			try {
				tooltip = tooltip +  "占比变化("+ decimalformate.format(zhanbigrowthrate) +  ")";
			} catch (java.lang.IllegalArgumentException e ) {
				tooltip = tooltip +  "占比变化(NULL)";
			}
			try {
				tooltip = tooltip + "MaxWeek=" + maxweek.toString();
			} catch (java.lang.IllegalArgumentException e ) {
				e.printStackTrace();
//				tooltip = tooltip +
			}
			try {
				tooltip = tooltip + "大盘占比" + decimalformate.format(dpzhanbi);
			} catch (java.lang.IllegalArgumentException e ) {
				tooltip = tooltip + "大盘占比NULL" ;
			}
			try {
				tooltip = tooltip + "DpMaxWk=" + dpzhanbimaxweek.toString();
			} catch (java.lang.IllegalArgumentException e ) {
				tooltip = tooltip + "DpMaxWk=NULL";
			}
//			tooltip = tooltip + "板块占比" + decimalformate.format(curzhanbidata) 
//						+  "占比变化("+ decimalformate.format(zhanbigrowthrate) +  ")" 
//						+ "MaxWeek=" + maxweek.toString()
//						+ "大盘占比" + decimalformate.format(dpzhanbi)
//						+ "DpMaxWk=" + dpzhanbimaxweek.toString();
//						;
			
			return tooltip;
			
			
//			Double curzhanbidata = (Double)dataset.getValue(row, column);  //占比
//	    	if(curzhanbidata == null) //有些停牌或者还没有加入该板块，该周没有数据会为NULL
//	    		return "";
//	    	
//	    	Double zhanbigrowthrate = null;
//	    	if(node.getType() == 4 ) { //板块
//	    		zhanbigrowthrate = ((BanKuai)this.node).getChenJiaoLiangZhanBiGrowthRateForAGivenPeriod (selecteddate); //占比增长率
//	    	} else if(node.getType() == 6 ) { //个股
//	    		zhanbigrowthrate = ((Stock)this.node).getChenJiaoLiangZhanBiGrowthRateForAGivenPeriod (selecteddate); //占比增长率
//	    	}
//	    	
//	    	Integer maxweek = null;
//	    	if(node.getType() == 4 ) { //板块
//	    		maxweek = ((BanKuai)this.node).getChenJiaoLiangZhanBiMaxWeekForAGivenPeriod (selecteddate); //占比增长率
//	    	} else if(node.getType() == 6 ) { //个股
//	    		maxweek = ((Stock)this.node).getChenJiaoLiangZhanBiMaxWeekForAGivenPeriod (selecteddate); //占比增长率
//	    	}
//	    	    	
//	        DecimalFormat decimalformate = new DecimalFormat("%#0.000");
//	        try {
//	        	tooltip = tooltip + "占比" + decimalformate.format(curzhanbidata);
//	        } catch (java.lang.IllegalArgumentException e ) {
////	        	e.printStackTrace();
//	        	System.out.println(curzhanbidata);
//	        	tooltip = tooltip + "占比" + "NULL";
//	        }
//	        try {
//	        	tooltip = tooltip + "占比变化(" + decimalformate.format(zhanbigrowthrate);
//	        } catch (java.lang.IllegalArgumentException e ) {
////	        	e.printStackTrace();
//	        	System.out.println(zhanbigrowthrate);
//	        	tooltip = tooltip + "占比变化(" + "NULL";
//	        }
//	        tooltip = tooltip +  ")" + "MaxWeek=" + maxweek.toString();
//	        
//	        return tooltip;
		}
    	
		return "";
    }
    
    public void setDisplayNode (BkChanYeLianTreeNode curdisplayednode) 
    {
    	this.node = curdisplayednode;
    }
}

class CustomToolTipGeneratorForChenJiaoEr implements CategoryToolTipGenerator  {
    private BkChanYeLianTreeNode node;

	public String generateToolTip(CategoryDataset dataset, int row, int column)  
    {
		String selected = dataset.getColumnKey(column).toString();
    	LocalDate selecteddate = CommonUtility.formateStringToDate(selected);
    	
    	Double curcje = (Double)dataset.getValue(row, column);
    	
    	DecimalFormat decimalformate = new DecimalFormat("#0.000");
    	return selecteddate + "成交额" + decimalformate.format(curcje) ;
    }
    
    public void setDisplayNode (BkChanYeLianTreeNode curdisplayednode) 
    {
    	this.node = curdisplayednode;
    }

}

