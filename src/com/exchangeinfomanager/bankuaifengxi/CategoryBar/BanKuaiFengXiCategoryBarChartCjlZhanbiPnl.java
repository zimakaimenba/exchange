package com.exchangeinfomanager.bankuaifengxi.CategoryBar;

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
import com.exchangeinfomanager.asinglestockinfo.DaPan;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;

public class BanKuaiFengXiCategoryBarChartCjlZhanbiPnl extends BanKuaiFengXiCategoryBarChartPnl 
{

	public BanKuaiFengXiCategoryBarChartCjlZhanbiPnl() 
	{
		super ();
		super.plot.setRenderer(new BanKuaiFengXiCategoryBarRenderer() );
		((BanKuaiFengXiCategoryBarRenderer) plot.getRenderer()).setBarPainter(new StandardBarPainter());
	}
	
	private static Logger logger = Logger.getLogger(BanKuaiFengXiCategoryBarChartCjlZhanbiPnl.class);
	@Override
	public void updatedDate (BkChanYeLianTreeNode node, LocalDate date, int difference,String period)
	{
		if(period.equals(ChenJiaoZhanBiInGivenPeriod.DAY))
			date = date.plus(difference,ChronoUnit.DAYS);
		else if(period.equals(ChenJiaoZhanBiInGivenPeriod.WEEK))
			date = date.plus(difference,ChronoUnit.WEEKS);
		else if(period.equals(ChenJiaoZhanBiInGivenPeriod.MONTH))
			date = date.plus(difference,ChronoUnit.MONTHS);

		setNodeCjlZhanBi(node,date,period);
	}
	/*
	 * 个股和大盘的成交量占比
	 */
	public void setNodeCjlZhanBi (BkChanYeLianTreeNode node, LocalDate displayedenddate1,String period)
	{
		LocalDate requireend = displayedenddate1.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = displayedenddate1.with(DayOfWeek.MONDAY).minus(this.shoulddisplayedmonthnum,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		
		this.setNodeAndDaPanCjlZhanBi(node,requirestart,requireend,period);
	}
	public void setNodeAndDaPanCjlZhanBi (BkChanYeLianTreeNode node, LocalDate startdate,LocalDate enddate, String period)
	{
		this.curdisplayednode = node;
		super.globeperiod = period;
		NodeXPeriodDataBasic nodexdata = node.getNodeXPeroidData(period);
		this.displayNodeZhanBi (nodexdata,startdate,enddate,period,"成交量");
	}
	public void displayNodeZhanBi (NodeXPeriodDataBasic nodexdata, LocalDate startdate,LocalDate enddate,String period,String indicater)
	{
		DaPan dapan = (DaPan)this.curdisplayednode.getRoot();
		
		LocalDate requireend = enddate.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = startdate.with(DayOfWeek.SATURDAY);
		
		barchartdataset = new DefaultCategoryDataset();
		double highestHigh =0.0; //设置显示范围

		LocalDate tmpdate = requirestart;
		do  {
			ChenJiaoZhanBiInGivenPeriod tmprecord = nodexdata.getSpecficRecord(tmpdate,0);
			if(tmprecord != null) {
				Double ggbkratio = tmprecord.getCjlZhanBi();
				
				LocalDate lastdayofweek = tmprecord.getRecordsDayofEndofWeek();
				barchartdataset.setValue(ggbkratio,"占比",lastdayofweek);
				
				if(ggbkratio > highestHigh)
					highestHigh = ggbkratio;
			} else {
				if( !dapan.isDaPanXiuShi(tmpdate,0,period) ) {
					barchartdataset.setValue(0.0,"占比",tmpdate);
//					datafx.addValue(0, "分析结果", tmpdate);
				} 
			}
			
			if(period.equals(ChenJiaoZhanBiInGivenPeriod.WEEK))
				tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
			else if(period.equals(ChenJiaoZhanBiInGivenPeriod.DAY))
				tmpdate = tmpdate.plus(1, ChronoUnit.DAYS) ;
			else if(period.equals(ChenJiaoZhanBiInGivenPeriod.MONTH))
				tmpdate = tmpdate.plus(1, ChronoUnit.MONTHS) ;
			
		} while (tmpdate.isBefore( requireend) || tmpdate.isEqual(requireend));
		
		setPanelTitle ("成交额" + period + "占比",requireend);
		
		operationAfterDataSetup (nodexdata,requirestart,requireend,highestHigh,period);
	}

	/*
	 * 
	 */
	private void operationAfterDataSetup (NodeXPeriodDataBasic nodexdata,LocalDate startdate, LocalDate enddate, double highestHigh, String period) 
	{
		CustomRendererForZhanBi render = (CustomRendererForZhanBi)super.plot.getRenderer();
		render.setDisplayNode(this.curdisplayednode);
		render.setDateSet (barchartdataset);
		render.setDisplayNodeXPeriod (nodexdata);
		
		DecimalFormat decimalformate = new DecimalFormat("%#0.000");
		render.setItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}",decimalformate));
		render.setItemLabelsVisible(true);


		CustomToolTipGeneratorForZhanBi custotooltip = new CustomToolTipGeneratorForZhanBi();
		custotooltip.setDisplayNode(curdisplayednode);
		render.setSeriesToolTipGenerator(0,custotooltip);
		
		//如有分析结果，ticklable显示红色
		CategoryLabelCustomizableCategoryAxis axis = (CategoryLabelCustomizableCategoryAxis)super.plot.getDomainAxis();
		axis.setDisplayNode(this.curdisplayednode,period);
		
		super.plot.getRangeAxis().setRange(0, highestHigh*1.12);
		
		super.plot.setDataset(barchartdataset);
		
//		super.setBarFenXiSingle();
		
//		super.setDaZiJinValueMarker(0.001); //大于0.1说明个股强势，资金占的多
//		super.setDaZiJinValueMarker(0.0005); //大于0.1说明个股强势，资金占的多
//		
//		final IntervalMarker target = new IntervalMarker(0.0004, 0.001);
//        target.setLabel("强势区域");
//        target.setLabelFont(new Font("SansSerif", Font.ITALIC, 11));
//        target.setLabelAnchor(RectangleAnchor.LEFT);
//        target.setLabelTextAnchor(TextAnchor.CENTER_LEFT);
//        target.setPaint(new Color(222, 222, 255, 128));
//        plot.addRangeMarker(target, Layer.BACKGROUND);
		
	}
	@Override
	public void updatedDate(BkChanYeLianTreeNode node, LocalDate startdate, LocalDate enddate, String period) {
		// TODO Auto-generated method stub
		
	}



}

//class CustomRendererForGgDpZhanBi extends BanKuaiFengXiBarRenderer 
//{
//	private static final long serialVersionUID = 1L;
//   
//    public CustomRendererForGgDpZhanBi() {
//        super();
//        super.displayedmaxwklevel = 4;
//    }
//    public void setDisplayNodeXPeriod(NodeXPeriodData nodexdata) 
//    {
//		// TODO Auto-generated method stub
//		
//	}
//	public Paint getItemPaint(final int row, final int column) 
//    {
//        if(column == shouldcolumn)
//            return Color.blue;
//        else   
//            return Color.RED;
//   }
//    public Paint getItemLabelPaint(final int row, final int column)
//    {
//		String selected = super.chartdataset.getColumnKey(column).toString();
//    	LocalDate selecteddate = CommonUtility.formateStringToDate(selected);
//    	 
//    	ChenJiaoZhanBiInGivenPeriod nodefx = nodexdata.getSpecficRecord(selecteddate, 0);
////    	if(node.getType() == 4)
////    		nodefx = ((BanKuai)node).getNodeFengXiResultForSpecificDate (selecteddate);
////    	else if(node.getType() == 6)
////    		nodefx = ((Stock)node).getNodeFengXiResultForSpecificDate (selecteddate);
//    	
//		Integer maxweek;
//		if(nodefx != null)
//			 maxweek = nodefx.getGgdpzhanbimaxweek();
//		else 
//			maxweek = 0;
//		
//		if(maxweek >= super.displayedmaxwklevel)
//			return Color.red;
//		else 
//			return Color.black;
//    }
//       
//}

//class CustomToolTipGeneratorForGgZhanBi extends BanKuaiFengXiBarToolTipGenerator 
//{
////    private BkChanYeLianTreeNode node;
//
//	public String generateToolTip(CategoryDataset dataset, int row, int column)  
//    {
//		String selected = dataset.getColumnKey(column).toString();
//    	LocalDate selecteddate = CommonUtility.formateStringToDate(selected);
//    	 
//    	ChenJiaoZhanBiInGivenPeriod nodefx = nodexdata.getSpecficRecord(selecteddate, 0);
////		if(node.getType() == 4 )
////			nodefx = ((BanKuai)node).getNodeFengXiResultForSpecificDate (selecteddate);
////		else if(node.getType() == 6 ) 
////			nodefx = ((Stock)node).getNodeFengXiResultForSpecificDate (selecteddate);
//		
//		if(nodefx == null)
//			return "";
//		
//		String tooltip = selected.toString() + " ";
//		if(node.getType() == 6 ) { //个股,个股大盘比只有个股，不会有板块
//			Double curzhanbidata = nodefx.getCjlZhanBi();  //占比
//			Double zhanbigrowthrate = nodefx.getGgbkzhanbigrowthrate();
//			Double cjezhanbi = nodefx.getGgbkcjegrowthzhanbi();
//			Integer maxweek = nodefx.getGgbkzhanbimaxweek();
//			Double dpzhanbi = nodefx.getGgdpzhanbi();
//			Integer dpzhanbimaxweek = nodefx.getGgdpzhanbimaxweek();
//			
//			
//			DecimalFormat decimalformate = new DecimalFormat("%#0.000");
//			try {
//				tooltip = tooltip + "大盘占比" + decimalformate.format(dpzhanbi);
//			} catch (java.lang.IllegalArgumentException e ) {
//				tooltip = tooltip + "大盘占比NULL" ;
//			}
//			try {
//				tooltip = tooltip + "大盘MaxWk=" + dpzhanbimaxweek.toString();
//			} catch (java.lang.IllegalArgumentException e ) {
//				tooltip = tooltip + "大盘MaxWk=NULL";
//			}
//			try {
//				tooltip = tooltip + "板块占比" + decimalformate.format(curzhanbidata);
//			} catch (java.lang.IllegalArgumentException e ) {
//				tooltip = tooltip + "板块占比NULL" ;
//			}
////			try {
////				tooltip = tooltip +  "占比变化("+ decimalformate.format(zhanbigrowthrate) +  ")";
////			} catch (java.lang.IllegalArgumentException e ) {
////				tooltip = tooltip +  "占比变化(NULL)";
////			}
//			try {
//				tooltip = tooltip + "板块MaxWk=" + maxweek.toString();
//			} catch (java.lang.IllegalArgumentException e ) {
//				e.printStackTrace();
////				tooltip = tooltip +
//			}
//			
//			
//			return tooltip;
//		}
//    	
//		return "";
//    }
//    
////    public void setDisplayNode (BkChanYeLianTreeNode curdisplayednode) 
////    {
////    	this.node = curdisplayednode;
////    }
//}



