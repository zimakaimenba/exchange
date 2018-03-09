package com.exchangeinfomanager.bankuaifengxi;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.apache.log4j.Logger;
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
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.ChenJiaoZhanBiInGivenPeriod;
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
	
	private static Logger logger = Logger.getLogger(BanKuaiFengXiBarChartGgDpZbPnl.class);
	/*
	 * 个股和大盘的成交量占比
	 */
	public void setNodeAndDaPanCjlZhanBi (BkChanYeLianTreeNode node, LocalDate displayedenddate1,String period)
	{
		LocalDate requireend = displayedenddate1.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = displayedenddate1.with(DayOfWeek.MONDAY).minus(this.shoulddisplayedmonthnum,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		
		this.setNodeAndDaPanCjlZhanBi(node,requirestart,requireend,period);
	}
	public void setNodeAndDaPanCjlZhanBi (BkChanYeLianTreeNode node, LocalDate startdate,LocalDate enddate, String period)
	{
		this.curdisplayednode = node;
		this.displayNodeZhanBi (node,startdate,enddate,period,"成交量");
	}
	/*
	 * 个股和大盘的成交额占比
	 */
	public void setNodeAndDaPanCjeZhanBi (BkChanYeLianTreeNode node, LocalDate displayedenddate1,String period)
	{
		LocalDate requireend = displayedenddate1.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = displayedenddate1.with(DayOfWeek.MONDAY).minus(this.shoulddisplayedmonthnum,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		
		this.setNodeAndDaPanCjlZhanBi(node,requirestart,requireend,period);
	}
	public void setNodeAndDaPanCjeZhanBi (BkChanYeLianTreeNode node, LocalDate startdate,LocalDate enddate, String period)
	{
		this.curdisplayednode = node;
		this.displayNodeZhanBi (node,startdate,enddate,period,"成交额");
	}
	public void displayNodeZhanBi (BkChanYeLianTreeNode node, LocalDate startdate,LocalDate enddate,String period,String indicater)
	{
		DaPan dapan = (DaPan)node.getRoot();
		
		LocalDate requireend = enddate.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = startdate.with(DayOfWeek.SATURDAY);
		
		NodeXPeriodData nodexdata = node.getNodeXPeroidData(period);
		NodeXPeriodData dpxdata = dapan.getNodeXPeroidData(period);
		
		barchartdataset = new DefaultCategoryDataset();
		double highestHigh =0.0; //设置显示范围
		
		LocalDate tmpdate = requirestart;
		do {
			ChenJiaoZhanBiInGivenPeriod tmpggrecord = nodexdata.getSpecficRecord(tmpdate,0);
			ChenJiaoZhanBiInGivenPeriod tmpdprecord = dpxdata.getSpecficRecord(tmpdate,0); 
			
			if(tmpggrecord != null) {
				Double ggdpratio = null;
				LocalDate lastdayofweek = null;
				if(indicater.equals("成交量") ) {
					Double ggchenjiaol = tmpggrecord.getMyownchengjiaoliang();
					Double dpchenjiaol = tmpdprecord.getMyownchengjiaoliang() * 100; //通达信在输出板块成交量的时候，只输出手数，所以要*100，而输出个股的成交量是包含手数的，也是奇怪。返回的是上证或深圳的某个记录，里面uplevel记录的是整个大盘的成交量
					ggdpratio = ggchenjiaol/dpchenjiaol; 
					lastdayofweek = tmpggrecord.getRecordsDayofEndofWeek();
				} else {
					Double ggchenjiaoer = tmpggrecord.getMyOwnChengJiaoEr();
					Double dpchenjiaoer = tmpdprecord.getUpLevelChengJiaoEr(); //返回的是上证或深圳的某个记录，里面uplevel记录的是整个大盘的成交额
					ggdpratio = ggchenjiaoer/dpchenjiaoer; 
					lastdayofweek = tmpggrecord.getRecordsDayofEndofWeek();
				}
				
				barchartdataset.setValue(ggdpratio,"占比",lastdayofweek);
				
				if(ggdpratio > highestHigh)
					highestHigh = ggdpratio;

			} else {
				if( !dapan.isDaPanXiuShi(tmpdate,period) ) {
					barchartdataset.setValue(0.0,"占比",tmpdate);
				} 
			}
			
			if(period.equals(ChenJiaoZhanBiInGivenPeriod.WEEK))
				tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
			else if(period.equals(ChenJiaoZhanBiInGivenPeriod.DAY))
				tmpdate = tmpdate.plus(1, ChronoUnit.DAYS) ;
			else if(period.equals(ChenJiaoZhanBiInGivenPeriod.MONTH))
				tmpdate = tmpdate.plus(1, ChronoUnit.MONTHS) ;
			
		} while (tmpdate.isBefore( requireend) || tmpdate.isEqual(requireend));

		
		setPanelTitle (indicater + "占比",requireend);
		
		operationAfterDataSetup (nodexdata,requirestart,requireend,highestHigh,period);
	}

	/*
	 * 个股和大盘的成交额占比
	 */
//	public void setNodeAndDaPanCjeZhanBiByWeek (BkChanYeLianTreeNode node, LocalDate displayedenddate1, DaPan dapan)
//	{
		
//		this.curdisplayednode = node;
//		this.displayedenddate = displayedenddate1;
//		LocalDate requireend = displayedenddate1.with(DayOfWeek.SATURDAY);
//		LocalDate requirestart = displayedenddate1.with(DayOfWeek.MONDAY).minus(this.shoulddisplayedmonthnum,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
//		
//		this.setNodeAndDaPanCjeZhanBiByWeek(node,requirestart,requireend,dapan);
//		barchartdataset = new DefaultCategoryDataset();
//		double highestHigh =0.0; //设置显示范围
////		datafx = new DefaultCategoryDataset();
//		
//		for(LocalDate tmpdate = requirestart;tmpdate.isBefore( requireend) || tmpdate.isEqual(requireend); tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ){
//			ChenJiaoZhanBiInGivenPeriod tmpggrecord = node.getSpecficChenJiaoErRecord(tmpdate);
//			ChenJiaoZhanBiInGivenPeriod tmpdprecord = dapan.getSpecficChenJiaoErRecord(tmpdate); //返回的是上证或深圳的某个记录，里面uplevel记录的是整个大盘的成交额
//			
//			if(tmpggrecord != null) {
//				Double ggchenjiaoer = tmpggrecord.getMyOwnChengJiaoEr();
//				Double dpchenjiaoer = tmpdprecord.getUpLevelChengJiaoEr(); //返回的是上证或深圳的某个记录，里面uplevel记录的是整个大盘的成交额
//				Double ggdpratio = ggchenjiaoer/dpchenjiaoer; 
//				LocalDate lastdayofweek = tmpggrecord.getRecordsDayofEndofWeek();
//				
//				barchartdataset.setValue(ggdpratio,"占比",lastdayofweek);
//				
//				if(ggdpratio > highestHigh)
//					highestHigh = ggdpratio;
//
//			} else {
//				if( !dapan.isThisWeekXiuShi(tmpdate) ) {
//					barchartdataset.setValue(0.0,"占比",tmpdate);
////					datafx.addValue(0, "分析结果", tmpdate);
//				} else //为空说明该周市场没有交易
//					continue;
//			}
//		}
//		
//		setPanelTitle ("成交额占比",displayedenddate1);
//		
//		operationAfterDataSetup (node,highestHigh);
//	}
//	public void setNodeAndDaPanCjeZhanBiByWeek (BkChanYeLianTreeNode node, LocalDate startdate,LocalDate enddate, DaPan dapan)
//	{
//		
//		this.curdisplayednode = node;
//		this.displayedenddate = displayedenddate1;
//		LocalDate requireend = enddate.with(DayOfWeek.SATURDAY);
//		LocalDate requirestart = startdate.with(DayOfWeek.SATURDAY);
//		
//		barchartdataset = new DefaultCategoryDataset();
//		double highestHigh =0.0; //设置显示范围
////		datafx = new DefaultCategoryDataset();
//		
//		for(LocalDate tmpdate = requirestart;tmpdate.isBefore( requireend) || tmpdate.isEqual(requireend); tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ){
//			ChenJiaoZhanBiInGivenPeriod tmpggrecord = node.getSpecficChenJiaoErRecord(tmpdate);
//			ChenJiaoZhanBiInGivenPeriod tmpdprecord = dapan.getSpecficChenJiaoErRecord(tmpdate); //返回的是上证或深圳的某个记录，里面uplevel记录的是整个大盘的成交额
//			
//			if(tmpggrecord != null) {
//				Double ggchenjiaoer = tmpggrecord.getMyOwnChengJiaoEr();
//				Double dpchenjiaoer = tmpdprecord.getUpLevelChengJiaoEr(); //返回的是上证或深圳的某个记录，里面uplevel记录的是整个大盘的成交额
//				Double ggdpratio = ggchenjiaoer/dpchenjiaoer; 
//				LocalDate lastdayofweek = tmpggrecord.getRecordsDayofEndofWeek();
//				
//				barchartdataset.setValue(ggdpratio,"占比",lastdayofweek);
//				
//				if(ggdpratio > highestHigh)
//					highestHigh = ggdpratio;
//
//			} else {
//				if( !dapan.isThisWeekXiuShi(tmpdate) ) {
//					barchartdataset.setValue(0.0,"占比",tmpdate);
////					datafx.addValue(0, "分析结果", tmpdate);
//				} else //为空说明该周市场没有交易
//					continue;
//			}
//		}
//		
//		setPanelTitle ("成交额占比",requireend);
//		
//		operationAfterDataSetup (node,highestHigh);
//	}
	
	/*
	 * 
	 */
	private void operationAfterDataSetup (BkChanYeLianTreeNode.NodeXPeriodData nodexdata,LocalDate startdate, LocalDate enddate, double highestHigh, String period) 
	{
		
		CustomRendererForGgDpZhanBi render = (CustomRendererForGgDpZhanBi)super.plot.getRenderer();
		render.setDisplayNode(this.curdisplayednode);
		render.setDateSet (barchartdataset);
		render.setDisplayNodeXPeriod (nodexdata);
		
		DecimalFormat decimalformate = new DecimalFormat("%#0.000");
		render.setItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}",decimalformate));
		render.setItemLabelsVisible(true);
		
		CustomToolTipGeneratorForGgZhanBi custotooltip = new CustomToolTipGeneratorForGgZhanBi();
		custotooltip.setDisplayNode(curdisplayednode);
		render.setSeriesToolTipGenerator(0,custotooltip);
        
		//如有分析结果，ticklable显示红色
		CategoryLabelCustomizableCategoryAxis axis = (CategoryLabelCustomizableCategoryAxis)super.plot.getDomainAxis();
		axis.setDisplayNode(this.curdisplayednode,period);
//		render.setBarCharType("个股大盘占比");
		
		super.plot.getRangeAxis().setRange(0, highestHigh*1.12);
		
		super.plot.setDataset(barchartdataset);
		
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
        super.displayedmaxwklevel = 4;
    }
    public void setDisplayNodeXPeriod(NodeXPeriodData nodexdata) 
    {
		// TODO Auto-generated method stub
		
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
    	 
    	ChenJiaoZhanBiInGivenPeriod nodefx = nodexdata.getSpecficRecord(selecteddate, 0);
//    	if(node.getType() == 4)
//    		nodefx = ((BanKuai)node).getNodeFengXiResultForSpecificDate (selecteddate);
//    	else if(node.getType() == 6)
//    		nodefx = ((Stock)node).getNodeFengXiResultForSpecificDate (selecteddate);
    	
		Integer maxweek;
		if(nodefx != null)
			 maxweek = nodefx.getGgdpzhanbimaxweek();
		else 
			maxweek = 0;
		
		if(maxweek >= super.displayedmaxwklevel)
			return Color.red;
		else 
			return Color.black;
    }
       
}

class CustomToolTipGeneratorForGgZhanBi extends BanKuaiFengXiBarToolTipGenerator 
{
//    private BkChanYeLianTreeNode node;

	public String generateToolTip(CategoryDataset dataset, int row, int column)  
    {
		String selected = dataset.getColumnKey(column).toString();
    	LocalDate selecteddate = CommonUtility.formateStringToDate(selected);
    	 
    	ChenJiaoZhanBiInGivenPeriod nodefx = nodexdata.getSpecficRecord(selecteddate, 0);
//		if(node.getType() == 4 )
//			nodefx = ((BanKuai)node).getNodeFengXiResultForSpecificDate (selecteddate);
//		else if(node.getType() == 6 ) 
//			nodefx = ((Stock)node).getNodeFengXiResultForSpecificDate (selecteddate);
		
		if(nodefx == null)
			return "";
		
		String tooltip = selected.toString() + " ";
		if(node.getType() == 6 ) { //个股,个股大盘比只有个股，不会有板块
			Double curzhanbidata = nodefx.getCjlZhanBi();  //占比
			Double zhanbigrowthrate = nodefx.getGgbkzhanbigrowthrate();
			Double cjezhanbi = nodefx.getGgbkcjegrowthzhanbi();
			Integer maxweek = nodefx.getGgbkzhanbimaxweek();
			Double dpzhanbi = nodefx.getGgdpzhanbi();
			Integer dpzhanbimaxweek = nodefx.getGgdpzhanbimaxweek();
			
			
			DecimalFormat decimalformate = new DecimalFormat("%#0.000");
			try {
				tooltip = tooltip + "大盘占比" + decimalformate.format(dpzhanbi);
			} catch (java.lang.IllegalArgumentException e ) {
				tooltip = tooltip + "大盘占比NULL" ;
			}
			try {
				tooltip = tooltip + "大盘MaxWk=" + dpzhanbimaxweek.toString();
			} catch (java.lang.IllegalArgumentException e ) {
				tooltip = tooltip + "大盘MaxWk=NULL";
			}
			try {
				tooltip = tooltip + "板块占比" + decimalformate.format(curzhanbidata);
			} catch (java.lang.IllegalArgumentException e ) {
				tooltip = tooltip + "板块占比NULL" ;
			}
//			try {
//				tooltip = tooltip +  "占比变化("+ decimalformate.format(zhanbigrowthrate) +  ")";
//			} catch (java.lang.IllegalArgumentException e ) {
//				tooltip = tooltip +  "占比变化(NULL)";
//			}
			try {
				tooltip = tooltip + "板块MaxWk=" + maxweek.toString();
			} catch (java.lang.IllegalArgumentException e ) {
				e.printStackTrace();
//				tooltip = tooltip +
			}
			
			
			return tooltip;
		}
    	
		return "";
    }
    
//    public void setDisplayNode (BkChanYeLianTreeNode curdisplayednode) 
//    {
//    	this.node = curdisplayednode;
//    }
}



