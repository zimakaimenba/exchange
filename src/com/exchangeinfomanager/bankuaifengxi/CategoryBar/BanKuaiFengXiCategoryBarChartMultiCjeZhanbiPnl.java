package com.exchangeinfomanager.bankuaifengxi.CategoryBar;

import java.awt.Color;
import java.awt.Paint;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.time.Week;

import com.exchangeinfomanager.bankuaifengxi.ExportCondition;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.DaPan;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.nodexdata.NodeXPeriodDataBasic;
import com.exchangeinfomanager.nodes.nodexdata.TDXNodeGivenPeriodDataItem;

public class BanKuaiFengXiCategoryBarChartMultiCjeZhanbiPnl extends BanKuaiFengXiCategoryBarChartPnl
{

	public BanKuaiFengXiCategoryBarChartMultiCjeZhanbiPnl() 
	{
		super ();

		super.plot.setRenderer(new CustomMultiCategroyRendererForZhanBi() );
		DecimalFormat decimalformate = new DecimalFormat("%#0.000");
		((CustomMultiCategroyRendererForZhanBi) plot.getRenderer()).setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}",decimalformate));
		((CustomMultiCategroyRendererForZhanBi) plot.getRenderer()).setBaseItemLabelsVisible(true);
		
		((CustomMultiCategroyRendererForZhanBi) plot.getRenderer()).setBarPainter(new StandardBarPainter());
		
//		CustomCategroyToolTipGeneratorForZhanBi custotooltip = new CustomCategroyToolTipGeneratorForZhanBi();
//		((CustomCategroyRendererForZhanBi) plot.getRenderer()).setSeriesToolTipGenerator(0,custotooltip);
//		((CustomMultiCategroyRendererForZhanBi) plot.getRenderer()).setBaseToolTipGenerator(custotooltip);
		
        //line part
        linechartdataset = new DefaultCategoryDataset();
        BanKuaiFengXiCategoryLineRenderer linerenderer = new BanKuaiFengXiCategoryLineRenderer ();
        plot.setDataset(1, linechartdataset);
        plot.setRenderer(1, linerenderer);
        ValueAxis rangeAxis2 = new NumberAxis("");
        plot.setRangeAxis(1, rangeAxis2);
        
        // change the rendering order so the primary dataset appears "behind" the 
        // other datasets...
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
	}
	
	private static Logger logger = Logger.getLogger(BanKuaiFengXiCategoryBarChartCjeZhanbiPnl.class);
	
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.bankuaifengxi.BarChartPanelDataChangedListener#updatedDate(com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode, java.time.LocalDate, int, java.lang.String)
	 * 多个数据集时候用
	 */
	public void updatedMultiDate (List<BkChanYeLianTreeNode> nodelist, LocalDate date, int difference, String period)
	{
		if(period.equals(TDXNodeGivenPeriodDataItem.DAY))
			date = date.plus(difference,ChronoUnit.DAYS);
		else if(period.equals(TDXNodeGivenPeriodDataItem.WEEK))
			date = date.plus(difference,ChronoUnit.WEEKS);
		else if(period.equals(TDXNodeGivenPeriodDataItem.MONTH))
			date = date.plus(difference,ChronoUnit.MONTHS);
			
//		setNodeCjeZhanBi(node,date,period);
		LocalDate requireend = date.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = date.with(DayOfWeek.MONDAY).minus(this.shoulddisplayedmonthnum,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		
		this.updatedMultiDate (nodelist,requirestart,requireend,period);
	}
	/*
	 * 
	 */
	public void updatedMultiDate ( List<BkChanYeLianTreeNode> nodelist, LocalDate startdate, LocalDate enddate,String period)
	{
//		this.setNodeCjeZhanBi (node,startdate,enddate,period);
		barchartdataset.clear();
		
		for(BkChanYeLianTreeNode node : nodelist) {
			super.curdisplayednode = (TDXNodes) node;
			super.globeperiod = period;
			NodeXPeriodDataBasic nodexdata = ((TDXNodes)node).getNodeXPeroidData(period);
			
			displayDataToGui (nodexdata,startdate,enddate,period);
		}
	}
	/*
	 * 
	 */
	private void displayDataToGui (NodeXPeriodDataBasic nodexdata,LocalDate startdate,LocalDate enddate,String period)
	{
		DaPan dapan = (DaPan)this.curdisplayednode.getRoot();
		
		LocalDate requireend = enddate.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = startdate.with(DayOfWeek.MONDAY);

		super.barchart.setNotify(false);
		
		double highestHigh =0.0; //设置显示范围
		
		TimeSeries rangecjezb = nodexdata.getRangeChengJiaoErZhanBi(startdate, enddate);
	
		LocalDate tmpdate = requirestart;
		do  {
			org.jfree.data.time.Week tmpwk = new Week(Date.from(tmpdate.atStartOfDay(ZoneId.systemDefault()).toInstant()) );
			TimeSeriesDataItem cjerecord = rangecjezb.getDataItem(tmpwk);
			tmpwk = null;
			if(cjerecord != null) {
				double cjezb = cjerecord.getValue().doubleValue();
				barchartdataset.setValue(cjezb,curdisplayednode.getMyOwnCode(),tmpdate.with(DayOfWeek.FRIDAY));
				
				if(cjezb > highestHigh)
					highestHigh = cjezb;
			} else {
				if( !dapan.isDaPanXiuShi(tmpdate,0,period) ) {
					barchartdataset.setValue(0.0,curdisplayednode.getMyOwnCode(),tmpdate);
				} 
			}
			
			if(period.equals(TDXNodeGivenPeriodDataItem.WEEK))
				tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
			else if(period.equals(TDXNodeGivenPeriodDataItem.DAY))
				tmpdate = tmpdate.plus(1, ChronoUnit.DAYS) ;
			else if(period.equals(TDXNodeGivenPeriodDataItem.MONTH))
				tmpdate = tmpdate.plus(1, ChronoUnit.MONTHS) ;
			
		} while (tmpdate.isBefore( requireend) || tmpdate.isEqual(requireend));
		
		setPanelTitle ("成交额" + period + curdisplayednode.getMyOwnCode(),requireend);
		
		super.barchart.setNotify(true);
		operationAfterDataSetup (nodexdata,requirestart,requireend,highestHigh,period);
	}
	/*
	 * 突出显示一些预先设置
	 */
	private void operationAfterDataSetup (NodeXPeriodDataBasic nodexdata,LocalDate startdate, LocalDate enddate, double highestHigh, String period) 
	{
//		CustomMultiCategroyRendererForZhanBi render = (CustomMultiCategroyRendererForZhanBi)super.plot.getRenderer();
//		render.setDisplayNode(this.curdisplayednode);
//		render.setDisplayNodeXPeriod (nodexdata);
		
		//如有分析结果，ticklable显示红色
		CategoryLabelCustomizableCategoryAxis axis = (CategoryLabelCustomizableCategoryAxis)super.plot.getDomainAxis();
		axis.setDisplayNode(this.curdisplayednode,period);
		
//		super.plot.getRangeAxis().setRange(0, highestHigh*1.12);
	}
	/*
	 * 
	 */
	@Override
	public void hightLightFxValues(ExportCondition expc) 
	{
		Integer cjezbtoupleveldpmax = expc.getSettinDpmaxwk();
		((BanKuaiFengXiCategoryBarRenderer)plot.getRenderer()).setDisplayMaxwkLevel (cjezbtoupleveldpmax);
		this.barchart.fireChartChanged();//必须有这句
		
	}
//	public void hightLightFxValues(Integer cjezdpkmax,Integer cjezbbkmax, Double cjemin, Double cjemax, Integer cjemaxwk,Double showhsl,Double ltszmin,Double ltszmax) 
//	{
//		
//	}
	@Override
	public void updatedDate(TDXNodes node, LocalDate date, int difference, String period) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void updatedDate(TDXNodes node, LocalDate startdate, LocalDate enddate, String period) {
		// TODO Auto-generated method stub
		
	}
//	@Override
//	public void hightLightFxValues(Integer cjezbtoupleveldpmax, Double cjemin, Double cjemax, Integer cjemaxwk, Double shoowhsl) {
//		// TODO Auto-generated method stub
//		((BanKuaiFengXiCategoryBarRenderer)plot.getRenderer()).setDisplayMaxwkLevel (cjezbtoupleveldpmax);
//		this.barchart.fireChartChanged();//必须有这句
//	}
	@Override
	public String getToolTipSelected() {
		// TODO Auto-generated method stub
		return null;
	}

}
/*
 * 
 */
class CustomMultiCategroyRendererForZhanBi extends BanKuaiFengXiCategoryBarRenderer
{
	public CustomMultiCategroyRendererForZhanBi ()
	{
		super ();
		super.displayedcolumncolorindex = Color.RED.darker();
	}
}
