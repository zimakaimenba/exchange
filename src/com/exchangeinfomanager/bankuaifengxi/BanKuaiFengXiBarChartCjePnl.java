package com.exchangeinfomanager.bankuaifengxi;

import java.awt.Color;
import java.awt.Paint;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.ChenJiaoZhanBiInGivenPeriod;
import com.exchangeinfomanager.asinglestockinfo.DaPan;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.ConnectDataBase;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;

public class BanKuaiFengXiBarChartCjePnl extends BanKuaiFengXiBarChartPnl 
{

	public BanKuaiFengXiBarChartCjePnl() 
	{
		super ();
		super.plot.setRenderer(new CustomRendererForCje() );
		((CustomRendererForCje) plot.getRenderer()).setBarPainter(new StandardBarPainter());
	}
	
//	 private static Logger logger = Logger.getLogger(BanKuaiFengXiBarChartCjePnl.class);
	/*
	 * 板块按周相对于某板块的交易额
	 */
	public void setBanKuaiJiaoYiEr (BanKuai node, LocalDate displayedenddate1,String period)
	{
		LocalDate requireend = displayedenddate1.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = displayedenddate1.with(DayOfWeek.MONDAY).minus(this.shoulddisplayedmonthnum,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		
		this.setBanKuaiJiaoYiEr (node,requirestart,requireend,period);
	}
	public void setBanKuaiJiaoYiEr (BanKuai node,LocalDate startdate,LocalDate enddate,String period)
	{
		this.curdisplayednode = node;
//		String bkcode = upnode.getMyOwnCode();
		BkChanYeLianTreeNode.NodeXPeriodData nodexdata = node.getNodeXPeroidData(period);
		displayDataToGui (nodexdata,startdate,enddate,period);
	}
	/*
	 * 股票按周相对于某板块的交易额
	 */
	public void setStockJiaoYiEr (Stock node,BkChanYeLianTreeNode upnode, LocalDate displayedenddate1,String period)
	{
		LocalDate requireend = displayedenddate1.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = displayedenddate1.with(DayOfWeek.MONDAY).minus(this.shoulddisplayedmonthnum,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		
		this.setStockJiaoYiEr (node,upnode,requirestart,requireend,period);
	}
	public void setStockJiaoYiEr (Stock node,BkChanYeLianTreeNode upnode,LocalDate startdate,LocalDate enddate,String period)
	{
		this.curdisplayednode = node;
		String bkcode = upnode.getMyOwnCode();
		BkChanYeLianTreeNode.NodeXPeriodData nodexdata = node.getStockXPeriodDataForABanKuai(bkcode, period);
		displayDataToGui (nodexdata,startdate,enddate,period);
	}
	private void displayDataToGui (BkChanYeLianTreeNode.NodeXPeriodData nodexdata,LocalDate startdate,LocalDate enddate,String period) 
	{
		DaPan dapan = (DaPan)this.curdisplayednode.getRoot();
		
		LocalDate requireend = enddate.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = startdate.with(DayOfWeek.SATURDAY);
		
		barchartdataset = new DefaultCategoryDataset();
		double highestHigh =0.0; //设置显示范围
		
		LocalDate tmpdate = requirestart;
		do {
			
			ChenJiaoZhanBiInGivenPeriod tmprecord = nodexdata.getSpecficRecord(tmpdate,0);
			if(tmprecord != null) {
				Double chenjiaoer = tmprecord.getMyOwnChengJiaoEr();
				LocalDate lastdayofweek = tmprecord.getRecordsDayofEndofWeek();
				barchartdataset.setValue(chenjiaoer,"成交额",lastdayofweek);
				
				if(chenjiaoer > highestHigh)
					highestHigh = chenjiaoer;
			} else {
				if( !dapan.isDaPanXiuShi(tmpdate,0,period) ) {
					barchartdataset.setValue(0.0,"成交额",tmpdate);
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
		
		xiuShiGuiAfterDispalyDate (nodexdata,requirestart,requireend,highestHigh,period);
	}
	
	private void xiuShiGuiAfterDispalyDate (BkChanYeLianTreeNode.NodeXPeriodData nodexdata,LocalDate startdate, LocalDate enddate, double highestHigh, String period)
	{
		CustomRendererForCje cjerender = (CustomRendererForCje) super.plot.getRenderer(); 
		cjerender.setDisplayNode(this.curdisplayednode);
		cjerender.setDateSet (barchartdataset);
		cjerender.setDisplayNodeXPeriod (nodexdata);
		DecimalFormat decimalformate = new DecimalFormat(",###");//("#0.000");
		((CustomRendererForCje) plot.getRenderer()).setItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}",decimalformate));
		cjerender.setItemLabelsVisible(true);
		
		CustomToolTipGeneratorForChenJiaoEr custotooltip = new CustomToolTipGeneratorForChenJiaoEr();
		custotooltip.setDisplayNode(this.curdisplayednode);
		custotooltip.setDisplayNodeXPeriod (nodexdata);
		cjerender.setSeriesToolTipGenerator(0,custotooltip);
			
		CategoryLabelCustomizableCategoryAxis axis = (CategoryLabelCustomizableCategoryAxis)super.plot.getDomainAxis();
		axis.setDisplayNode(this.curdisplayednode,period);
		
		super.plot.getRangeAxis().setRange(0, highestHigh*1.12);
		
		super.plot.setDataset(barchartdataset);
		
//		super.setBarFenXiSingle();
		
		setPanelTitle ("成交额",enddate);
	}
}

class CustomRendererForCje extends BanKuaiFengXiBarRenderer 
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(CustomRendererForCje.class);
//	private NodeXPeriodData nodexdata;
//	private Paint[] colors;
//    private int shouldcolumn = -1;
//    private String barCharType;
//	private BkChanYeLianTreeNode node;
   
    public CustomRendererForCje() {
        super();
        super.displayedmaxwklevel = 7;
    }

//    public void setDisplayNodeXPeriod(NodeXPeriodData nodexdata1) 
//    {
//		this.nodexdata = nodexdata1;
//	}

	public Paint getItemPaint(final int row, final int column) 
    {
        if(column == shouldcolumn) {
          return Color.blue;
        }
        else 
        	return Color.orange;
   }
    public Paint getItemLabelPaint(final int row, final int column)
    {
		String selected = super.chartdataset.getColumnKey(column).toString();
    	LocalDate selecteddate = CommonUtility.formateStringToDate(selected);
    	 
    	ChenJiaoZhanBiInGivenPeriod nodefx = nodexdata.getSpecficRecord(selecteddate, 0);
//    	if(node.getType() == 4) {
//    		nodefx = ((BanKuai)node).getNodeFengXiResultForSpecificDate (selecteddate);
//    	}  	else if(node.getType() == 6) {
//    		nodefx = ((Stock)node).getNodeFengXiResultForSpecificDate (selecteddate);
//    	}
    		
		Integer maxweek =0;
		if(nodefx != null)
			 maxweek = nodefx.getGgBkCjeMaxweek();
		
		if(maxweek >= super.displayedmaxwklevel)
			return Color.CYAN;
		else 
			return Color.black;
    }
    
    @Override
    public LegendItem getLegendItem(int dataset, int series) 
    {
        LegendItem legendItem = super.getLegendItem(dataset, series);
        logger.debug(dataset + " " + series + " " + legendItem.getShape());
        // modify legendItem here
        return legendItem;
    }
    
}


class CustomToolTipGeneratorForChenJiaoEr extends BanKuaiFengXiBarToolTipGenerator  
{
//    private BkChanYeLianTreeNode node;
////    private static Logger logger = Logger.getLogger(CustomToolTipGeneratorForChenJiaoEr.class);
//	private NodeXPeriodData nodexdata;

	public String generateToolTip(CategoryDataset dataset, int row, int column)  
    {
		String selected = dataset.getColumnKey(column).toString();
    	LocalDate selecteddate = CommonUtility.formateStringToDate(selected);
    	
    	Double curcje = (Double)dataset.getValue(row, column);
    	String danwei = "";
    	if(curcje >= 100000000) {
    		curcje = curcje / 100000000;
    		danwei = "亿";
    	}  	else if(curcje >= 10000000 && curcje <100000000) {
    		curcje = curcje / 10000000;
    		danwei = "千万";
    	}  	else if(curcje >= 1000000 && curcje <10000000) {
    		curcje = curcje / 1000000;
    		danwei = "百万";
    	}
    		
    	DecimalFormat decimalformate = new DecimalFormat("#0.000"); //",###";
//    	return selecteddate + "成交额" + decimalformate.format(curcje) ;
		
		//显示成交额是多少周最大
//		String selected = dataset.getColumnKey(column).toString();
//    	LocalDate selecteddate = CommonUtility.formateStringToDate(selected);
    	 
    	ChenJiaoZhanBiInGivenPeriod nodefx = nodexdata.getSpecficRecord(selecteddate, 0);
//		if(node.getType() == 4 )
//			nodefx = ((BanKuai)node).getNodeFengXiResultForSpecificDate (selecteddate);
//		else if(node.getType() == 6 ) 
//			nodefx = ((Stock)node).getNodeFengXiResultForSpecificDate (selecteddate);
		
		if(nodefx == null)
			return "";
		
//		String tooltip = selected.toString();
		Integer maxwk = nodefx.getGgBkCjeMaxweek();
		
		return selecteddate + " " + "成交额" + decimalformate.format(curcje) + danwei +  "成交额MaxWk=" + maxwk;
		
    }
    


}
