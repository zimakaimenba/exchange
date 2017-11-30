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
		standardChartTheme.setExtraLargeFont(new Font("����",Font.BOLD,20) );
		standardChartTheme.setRegularFont(new Font("����",Font.BOLD,20) );
		standardChartTheme.setLargeFont(new Font("����",Font.BOLD,20));
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
	 * ���/��Ʊ���ܽ��׶�
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
				barchartdataset.setValue(chenjiaoer,"�ɽ���",lastdayofweek);
			} else {
				if( !dapan.isThisWeekXiuShi(tmpdate) )
					barchartdataset.setValue(0.0,"�ɽ���",tmpdate);
				else //Ϊ��˵�������г�û�н���
					continue;
			}
		}

		DecimalFormat decimalformate = new DecimalFormat("#0.000");
		((CustomRenderer) plot.getRenderer()).setItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}",decimalformate));
		CustomToolTipGeneratorForChenJiaoEr custotooltip = new CustomToolTipGeneratorForChenJiaoEr();
		custotooltip.setDisplayNode(curdisplayednode);
		((CustomRenderer) plot.getRenderer()).setSeriesToolTipGenerator(0,custotooltip);
		((CustomRenderer) plot.getRenderer()).setToolTipGenerator(new CustomToolTipGeneratorForChenJiaoEr());
		((CustomRenderer)plot.getRenderer()).setBarCharType("�ɽ���");
		plot.setDataset(barchartdataset);
		
		setPanelTitle ("�ɽ���",displayedenddate1);
		
//		((CustomToolTipGeneratorForChenJiaoEr)((CustomRenderer) plot.getRenderer()).getSeriesToolTipGenerator (0)).s.;
	}
	/*
	 * ������/���ɰ�� ռ��
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
				barchartdataset.setValue(chenjiaoer,"ռ��",lastdayofweek);
			} else {
				if( !dapan.isThisWeekXiuShi(tmpdate) )
					barchartdataset.setValue(0.0,"ռ��",tmpdate);
				else //Ϊ��˵�������г�û�н���
					continue;
			}
			
		}
		
		DecimalFormat decimalformate = new DecimalFormat("%#0.000");
		((CustomRenderer) plot.getRenderer()).setItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}",decimalformate));
//		((CustomRenderer) plot.getRenderer()).setSeriesToolTipGenerator(0,new CustomToolTipGeneratorForZhanBi());
		CustomToolTipGeneratorForZhanBi custotooltip = new CustomToolTipGeneratorForZhanBi();
		custotooltip.setDisplayNode(curdisplayednode);
		((CustomRenderer) plot.getRenderer()).setToolTipGenerator(custotooltip);
        
		((CustomRenderer)plot.getRenderer()).setBarCharType("ռ��");
		plot.setDataset(barchartdataset);
		
		setPanelTitle ("ռ��",displayedenddate1);
	}
	/*
	 * ���ɺʹ��̵�ռ��
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
			ChenJiaoZhanBiInGivenPeriod tmpdprecord = dapan.getSpecficChenJiaoErRecord(tmpdate); //���ص�����֤�����ڵ�ĳ����¼������uplevel��¼�����������̵ĳɽ���
			
			if(tmpggrecord != null) {
				Double ggchenjiaoer = tmpggrecord.getMyOwnChengJiaoEr();
				Double dpchenjiaoer = tmpdprecord.getUpLevelChengJiaoEr(); //���ص�����֤�����ڵ�ĳ����¼������uplevel��¼�����������̵ĳɽ���
				Double ggdpratio = ggchenjiaoer/dpchenjiaoer; 
				LocalDate lastdayofweek = tmpggrecord.getRecordsDayofEndofWeek();
				
				barchartdataset.setValue(ggdpratio,"ռ��",lastdayofweek);
			} else {
				if( !dapan.isThisWeekXiuShi(tmpdate) )
					barchartdataset.setValue(0.0,"ռ��",tmpdate);
				else //Ϊ��˵�������г�û�н���
					continue;
			}
		}
		
		DecimalFormat decimalformate = new DecimalFormat("%#0.000");
		((CustomRenderer) plot.getRenderer()).setItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}",decimalformate));
		((CustomRenderer) plot.getRenderer()).setSeriesToolTipGenerator(0,new CustomToolTipGeneratorForZhanBi());
		CustomToolTipGeneratorForZhanBi custotooltip = new CustomToolTipGeneratorForZhanBi();
		custotooltip.setDisplayNode(curdisplayednode);
		((CustomRenderer) plot.getRenderer()).setToolTipGenerator(custotooltip);
        
		((CustomRenderer)plot.getRenderer()).setBarCharType("ռ��");
		plot.setDataset(barchartdataset);
		
		setPanelTitle ("ռ��",displayedenddate1);
		
		this.setDaZiJinValueMarker(0.001); //����0.1˵������ǿ�ƣ��ʽ�ռ�Ķ�
		this.setDaZiJinValueMarker(0.0005); //����0.1˵������ǿ�ƣ��ʽ�ռ�Ķ�
	}
	/*
	 * 
	 */
	public void setDaZiJinValueMarker (double d)
	{
		ValueMarker marker = new ValueMarker (d);
//		marker.setLabel("�ȵ�ռ�Ⱦ�����");
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
																+ "��" + CommonUtility.formatDateYYYY_MM_DD(requirestart) 
																+ "��" + CommonUtility.formatDateYYYY_MM_DD(requireend) );
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
     * ����Ҫͻ����ʾ��bar
     */
    public void highLightSpecificBarColumn (Comparable selecteddate)
    {
    	int cindex = barchartdataset.getColumnIndex(selecteddate) ;
    	((CustomRenderer)plot.getRenderer()).setBarColumnShouldChangeColor(cindex);
    	
        dateselected = selecteddate;
        barchart.fireChartChanged();//���������
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
        else   if(this.barCharType.equals("ռ��"))
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
		
		if(node.getType() == 4 ) { //���
			Double curzhanbidata = (Double)dataset.getValue(row, column);  //ռ��
			Double zhanbigrowthrate = nodefx.getGgbkzhanbigrowthrate();
			Double cjezhanbi = nodefx.getGgbkcjegrowthzhanbi();
			Integer maxweek = nodefx.getGgbkzhanbimaxweek();
			
			DecimalFormat decimalformate = new DecimalFormat("%#0.000");
			try {
				tooltip = tooltip +  "ռ��" + decimalformate.format(curzhanbidata) ;
			} catch (java.lang.IllegalArgumentException e ) {
				tooltip = tooltip  +  "ռ��NULL";
			}
			try {
				tooltip = tooltip +   "ռ�ȱ仯("	+ decimalformate.format(zhanbigrowthrate) +  ")";
			} catch (java.lang.IllegalArgumentException e ) {
				tooltip = tooltip  +  "ռ�ȱ仯(NULL)";
			}
			try {
				tooltip = tooltip +  "MaxWeek=" + maxweek.toString() ;
			} catch (java.lang.IllegalArgumentException e ) {
				tooltip = tooltip + "MaxWeek=NULL";
			}
			
//			tooltip = tooltip + "ռ��" + decimalformate.format(curzhanbidata) 
//						+  "ռ�ȱ仯("	+ decimalformate.format(zhanbigrowthrate) +  ")" 
//						+ "MaxWeek=" + maxweek.toString()
//						;
			
			return tooltip;
		} else if(node.getType() == 6 ) { //����
			Double curzhanbidata = nodefx.getCjlZhanBi();  //ռ��
			Double zhanbigrowthrate = nodefx.getGgbkzhanbigrowthrate();
			Double cjezhanbi = nodefx.getGgbkcjegrowthzhanbi();
			Integer maxweek = nodefx.getGgbkzhanbimaxweek();
			Double dpzhanbi = nodefx.getGgdpzhanbi();
			Integer dpzhanbimaxweek = nodefx.getGgdpzhanbimaxweek();
			
			
			DecimalFormat decimalformate = new DecimalFormat("%#0.000");
			try {
				tooltip = tooltip + "���ռ��" + decimalformate.format(curzhanbidata);
			} catch (java.lang.IllegalArgumentException e ) {
				tooltip = tooltip + "���ռ��NULL" ;
			}
			try {
				tooltip = tooltip +  "ռ�ȱ仯("+ decimalformate.format(zhanbigrowthrate) +  ")";
			} catch (java.lang.IllegalArgumentException e ) {
				tooltip = tooltip +  "ռ�ȱ仯(NULL)";
			}
			try {
				tooltip = tooltip + "MaxWeek=" + maxweek.toString();
			} catch (java.lang.IllegalArgumentException e ) {
				e.printStackTrace();
//				tooltip = tooltip +
			}
			try {
				tooltip = tooltip + "����ռ��" + decimalformate.format(dpzhanbi);
			} catch (java.lang.IllegalArgumentException e ) {
				tooltip = tooltip + "����ռ��NULL" ;
			}
			try {
				tooltip = tooltip + "DpMaxWk=" + dpzhanbimaxweek.toString();
			} catch (java.lang.IllegalArgumentException e ) {
				tooltip = tooltip + "DpMaxWk=NULL";
			}
//			tooltip = tooltip + "���ռ��" + decimalformate.format(curzhanbidata) 
//						+  "ռ�ȱ仯("+ decimalformate.format(zhanbigrowthrate) +  ")" 
//						+ "MaxWeek=" + maxweek.toString()
//						+ "����ռ��" + decimalformate.format(dpzhanbi)
//						+ "DpMaxWk=" + dpzhanbimaxweek.toString();
//						;
			
			return tooltip;
			
			
//			Double curzhanbidata = (Double)dataset.getValue(row, column);  //ռ��
//	    	if(curzhanbidata == null) //��Щͣ�ƻ��߻�û�м���ð�飬����û�����ݻ�ΪNULL
//	    		return "";
//	    	
//	    	Double zhanbigrowthrate = null;
//	    	if(node.getType() == 4 ) { //���
//	    		zhanbigrowthrate = ((BanKuai)this.node).getChenJiaoLiangZhanBiGrowthRateForAGivenPeriod (selecteddate); //ռ��������
//	    	} else if(node.getType() == 6 ) { //����
//	    		zhanbigrowthrate = ((Stock)this.node).getChenJiaoLiangZhanBiGrowthRateForAGivenPeriod (selecteddate); //ռ��������
//	    	}
//	    	
//	    	Integer maxweek = null;
//	    	if(node.getType() == 4 ) { //���
//	    		maxweek = ((BanKuai)this.node).getChenJiaoLiangZhanBiMaxWeekForAGivenPeriod (selecteddate); //ռ��������
//	    	} else if(node.getType() == 6 ) { //����
//	    		maxweek = ((Stock)this.node).getChenJiaoLiangZhanBiMaxWeekForAGivenPeriod (selecteddate); //ռ��������
//	    	}
//	    	    	
//	        DecimalFormat decimalformate = new DecimalFormat("%#0.000");
//	        try {
//	        	tooltip = tooltip + "ռ��" + decimalformate.format(curzhanbidata);
//	        } catch (java.lang.IllegalArgumentException e ) {
////	        	e.printStackTrace();
//	        	System.out.println(curzhanbidata);
//	        	tooltip = tooltip + "ռ��" + "NULL";
//	        }
//	        try {
//	        	tooltip = tooltip + "ռ�ȱ仯(" + decimalformate.format(zhanbigrowthrate);
//	        } catch (java.lang.IllegalArgumentException e ) {
////	        	e.printStackTrace();
//	        	System.out.println(zhanbigrowthrate);
//	        	tooltip = tooltip + "ռ�ȱ仯(" + "NULL";
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
    	return selecteddate + "�ɽ���" + decimalformate.format(curcje) ;
    }
    
    public void setDisplayNode (BkChanYeLianTreeNode curdisplayednode) 
    {
    	this.node = curdisplayednode;
    }

}

