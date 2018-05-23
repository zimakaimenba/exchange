package com.exchangeinfomanager.bankuaifengxi.CategoryBar;

import java.awt.Color;
import java.awt.Paint;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.time.Week;

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.DaPan;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.asinglestockinfo.Stock.StockNodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.StockGivenPeriodDataItem;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;

public class BanKuaiFengXiCategoryBarChartCjeZhanbiPnl extends BanKuaiFengXiCategoryBarChartPnl
{

	public BanKuaiFengXiCategoryBarChartCjeZhanbiPnl() 
	{
		super ();

		super.plot.setRenderer(new CustomCategroyRendererForZhanBi() );
		
		((CustomCategroyRendererForZhanBi) plot.getRenderer()).setBarPainter(new StandardBarPainter());
		
		CustomCategroyToolTipGeneratorForZhanBi custotooltip = new CustomCategroyToolTipGeneratorForZhanBi();
//		((CustomCategroyRendererForZhanBi) plot.getRenderer()).setSeriesToolTipGenerator(0,custotooltip);
		((CustomCategroyRendererForZhanBi) plot.getRenderer()).setBaseToolTipGenerator(custotooltip);
		
		DecimalFormat decimalformate = new DecimalFormat("%#0.000");
		((CustomCategroyRendererForZhanBi) plot.getRenderer()).setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}",decimalformate));
		((CustomCategroyRendererForZhanBi) plot.getRenderer()).setBaseItemLabelsVisible(true);
		
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
	 * 
	 */
	public void updateConditionMatchData (BkChanYeLianTreeNode node, String period,TimeSeries matchdata)
	{
		super.barchart.setNotify(false);
		
		if(linechartdataset != null)
			linechartdataset.clear();
		
		int maxnum = 0;
		if(node.getType() == BanKuaiAndStockBasic.TDXBK ) {
			DaPan dapan = (DaPan)this.curdisplayednode.getRoot();
			
			NodeXPeriodDataBasic nodexdata = node.getNodeXPeroidData(period);
			LocalDate tmpdate = nodexdata.getRecordsStartDate();
			LocalDate dataenddate = nodexdata.getRecordsEndDate();
			
			do  {
				org.jfree.data.time.Week tmpwk = new Week(Date.from(tmpdate.atStartOfDay(ZoneId.systemDefault()).toInstant()) );
				TimeSeriesDataItem cjerecord = matchdata.getDataItem(tmpwk);
				tmpwk = null;
				if(cjerecord != null) {
					int matchnum = cjerecord.getValue().intValue();
					linechartdataset.setValue(matchnum,"matchnumber",tmpdate.with(DayOfWeek.FRIDAY));
					
					if(matchnum > maxnum)
						maxnum = matchnum; 
				} else {
					if( !dapan.isDaPanXiuShi(tmpdate,0,period) ) {
						linechartdataset.setValue(0.0,"matchnumber",tmpdate);
					} 
				}
				
				if(period.equals(StockGivenPeriodDataItem.WEEK))
					tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
				else if(period.equals(StockGivenPeriodDataItem.DAY))
					tmpdate = tmpdate.plus(1, ChronoUnit.DAYS) ;
				else if(period.equals(StockGivenPeriodDataItem.MONTH))
					tmpdate = tmpdate.plus(1, ChronoUnit.MONTHS) ;
				
			} while (tmpdate.isBefore( dataenddate ) || tmpdate.isEqual(dataenddate ));
		}
		
		super.plot.getRangeAxis(1).setRange(0,maxnum *1.5);
		
		super.barchart.setNotify(true);
		
	}
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.bankuaifengxi.BarChartPanelDataChangedListener#updatedDate(com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode, java.time.LocalDate, int, java.lang.String)
	 * 升级占比数据
	 */
	public void updatedDate (BkChanYeLianTreeNode node, LocalDate date, int difference, String period)
	{
		if(period.equals(StockGivenPeriodDataItem.DAY))
			date = date.plus(difference,ChronoUnit.DAYS);
		else if(period.equals(StockGivenPeriodDataItem.WEEK))
			date = date.plus(difference,ChronoUnit.WEEKS);
		else if(period.equals(StockGivenPeriodDataItem.MONTH))
			date = date.plus(difference,ChronoUnit.MONTHS);
			
		setNodeCjeZhanBi(node,date,period);
	}
	public void updatedDate (BkChanYeLianTreeNode node, LocalDate startdate, LocalDate enddate,String period)
	{
		this.setNodeCjeZhanBi (node,startdate,enddate,period);
	}
	/*
	 * 板块按周相对于某板块的交易额
	 */
	private void setNodeCjeZhanBi (BkChanYeLianTreeNode node,LocalDate displayedenddate1,String period)
	{
		LocalDate requireend = displayedenddate1.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = displayedenddate1.with(DayOfWeek.MONDAY).minus(this.shoulddisplayedmonthnum,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		
		this.setNodeCjeZhanBi (node,requirestart,requireend,period);
	}
	private void setNodeCjeZhanBi (BkChanYeLianTreeNode node,LocalDate startdate,LocalDate enddate,String period)
	{
		this.curdisplayednode = node;
		super.globeperiod = period;
		NodeXPeriodDataBasic nodexdata = node.getNodeXPeroidData(period);
		displayDataToGui (nodexdata,startdate,enddate,period);
	}
	private void displayDataToGui (NodeXPeriodDataBasic nodexdata,LocalDate startdate,LocalDate enddate,String period)
	{
		DaPan dapan = (DaPan)this.curdisplayednode.getRoot();
		
		LocalDate requireend = enddate.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = startdate.with(DayOfWeek.MONDAY);
		
		barchartdataset.clear();
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
				barchartdataset.setValue(cjezb,"占比",tmpdate.with(DayOfWeek.FRIDAY));
				
				if(cjezb > highestHigh)
					highestHigh = cjezb;
			} else {
				if( !dapan.isDaPanXiuShi(tmpdate,0,period) ) {
					barchartdataset.setValue(0.0,"占比",tmpdate);
				} 
			}
			
			if(period.equals(StockGivenPeriodDataItem.WEEK))
				tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
			else if(period.equals(StockGivenPeriodDataItem.DAY))
				tmpdate = tmpdate.plus(1, ChronoUnit.DAYS) ;
			else if(period.equals(StockGivenPeriodDataItem.MONTH))
				tmpdate = tmpdate.plus(1, ChronoUnit.MONTHS) ;
			
		} while (tmpdate.isBefore( requireend) || tmpdate.isEqual(requireend));
		
		setPanelTitle ("成交额" + period + "占比",requireend);
		
		super.barchart.setNotify(true);
		operationAfterDataSetup (nodexdata,requirestart,requireend,highestHigh,period);
	}
	/*
	 * 突出显示一些预先设置
	 */
	private void operationAfterDataSetup (NodeXPeriodDataBasic nodexdata,LocalDate startdate, LocalDate enddate, double highestHigh, String period) 
	{
		CustomCategroyRendererForZhanBi render = (CustomCategroyRendererForZhanBi)super.plot.getRenderer();
		render.setDisplayNode(this.curdisplayednode);
		render.setDisplayNodeXPeriod (nodexdata);
		
		//如有分析结果，ticklable显示红色
		CategoryLabelCustomizableCategoryAxis axis = (CategoryLabelCustomizableCategoryAxis)super.plot.getDomainAxis();
		axis.setDisplayNode(this.curdisplayednode,period);
		
		super.plot.getRangeAxis().setRange(0, highestHigh*1.12);
	}
	/*
	 * 
	 */
	@Override
	public void hightLightFxValues(Integer cjezdpkmax,Integer cjezbbkmax, Double cje, Integer cjemax,Double showhsl) 
	{
		((BanKuaiFengXiCategoryBarRenderer)plot.getRenderer()).setDisplayMaxwkLevel (cjezbbkmax);
		this.barchart.fireChartChanged();//必须有这句
	}

}

class CustomCategroyRendererForZhanBi extends BanKuaiFengXiCategoryBarRenderer 
{
	private static final long serialVersionUID = 1L;
   
    public CustomCategroyRendererForZhanBi() {
        super();
        super.displayedmaxwklevel = 4;
    }

	public Paint getItemPaint(final int row, final int column) 
    {
        if(column == shouldcolumn)
            return Color.blue;
        else   
            return Color.RED.darker();
   }
    
    public Paint getItemLabelPaint(final int row, final int column)
    {
    	CategoryPlot plot = getPlot ();
    	CategoryDataset dataset = plot.getDataset();
		String selected =  dataset.getColumnKey(column).toString();
    	LocalDate selecteddate = CommonUtility.formateStringToDate(selected);

    	Integer maxweek = nodexdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(selecteddate,0);

		if(maxweek != null  && maxweek >= super.displayedmaxwklevel)
			return Color.MAGENTA;
		else 
			return Color.black;
    }
    
}

class CustomCategroyToolTipGeneratorForZhanBi extends BanKuaiFengXiCategoryBarToolTipGenerator 
{
	public String generateToolTip(CategoryDataset dataset, int row, int column)  
    {
		String selected = dataset.getColumnKey(column).toString();
    	LocalDate selecteddate = CommonUtility.formateStringToDate(selected);
    	 
		String tooltip = selected.toString() + " ";
		
		Double curzhanbidata = (Double)dataset.getValue(row, column);  //占比
		if(curzhanbidata == null)
			return null;

			Integer maxweek = nodexdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(selecteddate,0);//nodefx.getGgbkzhanbimaxweek();
			if(maxweek == null)
				return null;
			
			Double hsl = null ;
			if(super.node.getType() == BanKuaiAndStockBasic.TDXGG) {
				hsl = ((StockNodeXPeriodData)nodexdata).getSpecificTimeHuanShouLv(selecteddate, 0);
			}
			
			DecimalFormat decimalformate = new DecimalFormat("%#0.000");
			try {
				tooltip = tooltip +  "占比" + decimalformate.format(curzhanbidata) ;
			} catch (java.lang.IllegalArgumentException e ) {
				tooltip = tooltip  +  "占比NULL";
			}
//			try {
//				tooltip = tooltip +   "占比变化("	+ decimalformate.format(zhanbigrowthrate) +  ")";
//			} catch (java.lang.IllegalArgumentException e ) {
//				tooltip = tooltip  +  "占比变化(NULL)";
//			}
			try {
				tooltip = tooltip +  "占比MaxWk=" + maxweek.toString() ;
			} catch (java.lang.IllegalArgumentException e ) {
				tooltip = tooltip + "占比MaxWk=NULL";
			}
			try {
				tooltip = tooltip +  "HSL=" + hsl.toString() ;
			} catch (java.lang.IllegalArgumentException e ) {
				
			} catch (java.lang.NullPointerException ex) {
				
			}
			
			
			return tooltip;
    }
}