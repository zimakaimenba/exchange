package com.exchangeinfomanager.bankuaifengxi;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.DaPan;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;

public class BanKuaiFengXiBarChartGgDpZbPnl extends BanKuaiFengXiBarChartPnl 
{

	public BanKuaiFengXiBarChartGgDpZbPnl() 
	{
		super ();
		super.plot.setRenderer(new CustomRendererForGgDpZhanBi() );
		((CustomRendererForGgDpZhanBi) plot.getRenderer()).setBarPainter(new StandardBarPainter());
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
//		datafx = new DefaultCategoryDataset();
		
		for(LocalDate tmpdate = requirestart;tmpdate.isBefore( requireend) || tmpdate.isEqual(requireend); tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ){
			ChenJiaoZhanBiInGivenPeriod tmpggrecord = node.getSpecficChenJiaoErRecord(tmpdate);
			ChenJiaoZhanBiInGivenPeriod tmpdprecord = dapan.getSpecficChenJiaoErRecord(tmpdate); //返回的是上证或深圳的某个记录，里面uplevel记录的是整个大盘的成交额
			
			if(tmpggrecord != null) {
				Double ggchenjiaoer = tmpggrecord.getMyOwnChengJiaoEr();
				Double dpchenjiaoer = tmpdprecord.getUpLevelChengJiaoEr(); //返回的是上证或深圳的某个记录，里面uplevel记录的是整个大盘的成交额
				Double ggdpratio = ggchenjiaoer/dpchenjiaoer; 
				LocalDate lastdayofweek = tmpggrecord.getRecordsDayofEndofWeek();
				
				barchartdataset.setValue(ggdpratio,"占比",lastdayofweek);
				
//				if(tmpggrecord.hasFengXiJieGuo ())
//					datafx.addValue(ggdpratio/5, "分析结果", lastdayofweek);
//				else
//					datafx.addValue(0, "分析结果", lastdayofweek);
			} else {
				if( !dapan.isThisWeekXiuShi(tmpdate) ) {
					barchartdataset.setValue(0.0,"占比",tmpdate);
//					datafx.addValue(0, "分析结果", tmpdate);
				} else //为空说明该周市场没有交易
					continue;
			}
		}
		
		CustomRendererForGgDpZhanBi render = (CustomRendererForGgDpZhanBi)super.plot.getRenderer();
		render.setDisplayNode(node);
		render.setDateSet (barchartdataset);
		
		DecimalFormat decimalformate = new DecimalFormat("%#0.000");
		render.setItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}",decimalformate));
		render.setItemLabelsVisible(true);
		
		CustomToolTipGeneratorForGgZhanBi custotooltip = new CustomToolTipGeneratorForGgZhanBi();
		custotooltip.setDisplayNode(curdisplayednode);
		render.setSeriesToolTipGenerator(0,custotooltip);
        
		//如有分析结果，ticklable显示红色
		CategoryLabelCustomizableCategoryAxis axis = (CategoryLabelCustomizableCategoryAxis)super.plot.getDomainAxis();
		axis.setDisplayNode(node);
//		render.setBarCharType("个股大盘占比");
		
		super.plot.setDataset(barchartdataset);
		
		setPanelTitle ("占比",displayedenddate1);
		
//		super.setBarFenXiSingle();
		
//		super.setDaZiJinValueMarker(0.001); //大于0.1说明个股强势，资金占的多
//		super.setDaZiJinValueMarker(0.0005); //大于0.1说明个股强势，资金占的多
		
		final IntervalMarker target = new IntervalMarker(0.0004, 0.001);
        target.setLabel("强势区域");
        target.setLabelFont(new Font("SansSerif", Font.ITALIC, 11));
        target.setLabelAnchor(RectangleAnchor.LEFT);
        target.setLabelTextAnchor(TextAnchor.CENTER_LEFT);
        target.setPaint(new Color(222, 222, 255, 128));
        plot.addRangeMarker(target, Layer.BACKGROUND);
	}

}

class CustomRendererForGgDpZhanBi extends BanKuaiFengXiBarRenderer 
{
	private static final long serialVersionUID = 1L;
   
    public CustomRendererForGgDpZhanBi() {
        super();
        
    }
    public Paint getItemPaint(final int row, final int column) 
    {
        if(column == shouldcolumn)
            return Color.blue;
        else   
            return Color.RED;
   }
    public Paint getItemLabelPaint(final int row, final int column)
    {
		String selected = super.chartdataset.getColumnKey(column).toString();
    	LocalDate selecteddate = CommonUtility.formateStringToDate(selected);
    	 
    	ChenJiaoZhanBiInGivenPeriod nodefx = null;
		nodefx = ((Stock)node).getNodeFengXiResultForSpecificDate (selecteddate);
		Integer maxweek;
		if(nodefx != null)
			 maxweek = nodefx.getGgdpzhanbimaxweek();
		else 
			maxweek = 0;
		if(maxweek >=4)
			return Color.red;
		else 
			return Color.black;
    }
       
}

class CustomToolTipGeneratorForGgZhanBi implements CategoryToolTipGenerator 
{
    private BkChanYeLianTreeNode node;

	public String generateToolTip(CategoryDataset dataset, int row, int column)  
    {
		String selected = dataset.getColumnKey(column).toString();
    	LocalDate selecteddate = CommonUtility.formateStringToDate(selected);
    	 
    	ChenJiaoZhanBiInGivenPeriod nodefx = null;
		if(node.getType() == 4 )
			nodefx = ((BanKuai)node).getNodeFengXiResultForSpecificDate (selecteddate);
		else if(node.getType() == 6 ) 
			nodefx = ((Stock)node).getNodeFengXiResultForSpecificDate (selecteddate);
		
		if(nodefx == null)
			return "";
		
		String tooltip = selected.toString();
		if(node.getType() == 6 ) { //个股,个股大盘比只有个股，不会有板块
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
			
			return tooltip;
		}
    	
		return "";
    }
    
    public void setDisplayNode (BkChanYeLianTreeNode curdisplayednode) 
    {
    	this.node = curdisplayednode;
    }
}



