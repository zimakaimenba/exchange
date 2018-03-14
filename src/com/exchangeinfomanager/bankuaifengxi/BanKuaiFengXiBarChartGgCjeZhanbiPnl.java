package com.exchangeinfomanager.bankuaifengxi;

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
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.time.Week;

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.DaPan;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.asinglestockinfo.StockGivenPeriodDataItem;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;

public class BanKuaiFengXiBarChartGgCjeZhanbiPnl extends BanKuaiFengXiBarChartPnl
{

	public BanKuaiFengXiBarChartGgCjeZhanbiPnl() 
	{
		super ();
		super.plot.setRenderer(new CustomRendererForZhanBi() );
		((CustomRendererForZhanBi) plot.getRenderer()).setBarPainter(new StandardBarPainter());
	}
	
	private static Logger logger = Logger.getLogger(BanKuaiFengXiBarChartGgCjeZhanbiPnl.class);
	
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
	/*
	 * ��鰴�������ĳ���Ľ��׶�
	 */
	public void setNodeCjeZhanBi (BkChanYeLianTreeNode node,LocalDate displayedenddate1,String period)
	{
		LocalDate requireend = displayedenddate1.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = displayedenddate1.with(DayOfWeek.MONDAY).minus(this.shoulddisplayedmonthnum,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		
		this.setNodeCjeZhanBi (node,requirestart,requireend,period);
	}
	public void setNodeCjeZhanBi (BkChanYeLianTreeNode node,LocalDate startdate,LocalDate enddate,String period)
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
		
		barchartdataset = new DefaultCategoryDataset();
		double highestHigh =0.0; //������ʾ��Χ
		
		TimeSeries rangecjezb = nodexdata.getRangeChengJiaoErZhanBi(startdate, enddate);
//		int itemcount = rangecjezb.getItemCount();
//		for(int i=0;i<itemcount;i++) {
//			TimeSeriesDataItem cjezbrecord = rangecjezb.getDataItem(i);
//			RegularTimePeriod datestr = cjezbrecord.getPeriod();
//			LocalDate latdayofweek = datestr.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().with(DayOfWeek.FRIDAY);
//			double zb = cjezbrecord.getValue().doubleValue();
//			barchartdataset.setValue(zb,"ռ��",latdayofweek);
//			
//			if(zb > highestHigh)
//				highestHigh = zb;
//		}
		
		LocalDate tmpdate = requirestart;
		do  {
			org.jfree.data.time.Week tmpwk = new Week(Date.from(tmpdate.atStartOfDay(ZoneId.systemDefault()).toInstant()) );
			TimeSeriesDataItem cjerecord = rangecjezb.getDataItem(tmpwk);
			if(cjerecord != null) {
				double cjezb = cjerecord.getValue().doubleValue();
				barchartdataset.setValue(cjezb,"ռ��",tmpdate.with(DayOfWeek.FRIDAY));
				
				if(cjezb > highestHigh)
					highestHigh = cjezb;
			} else {
				if( !dapan.isDaPanXiuShi(tmpdate,0,period) ) {
					barchartdataset.setValue(0.0,"ռ��",tmpdate);
				} 
			}
			
			if(period.equals(StockGivenPeriodDataItem.WEEK))
				tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
			else if(period.equals(StockGivenPeriodDataItem.DAY))
				tmpdate = tmpdate.plus(1, ChronoUnit.DAYS) ;
			else if(period.equals(StockGivenPeriodDataItem.MONTH))
				tmpdate = tmpdate.plus(1, ChronoUnit.MONTHS) ;
			
		} while (tmpdate.isBefore( requireend) || tmpdate.isEqual(requireend));
		
		setPanelTitle ("�ɽ���" + period + "ռ��",requireend);
		
		operationAfterDataSetup (nodexdata,requirestart,requireend,highestHigh,period);
	}
	/*
	 * ͻ����ʾһЩԤ������
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
		custotooltip.setDisplayNodeXPeriod(nodexdata);
		render.setSeriesToolTipGenerator(0,custotooltip);
		
		//���з��������ticklable��ʾ��ɫ
		CategoryLabelCustomizableCategoryAxis axis = (CategoryLabelCustomizableCategoryAxis)super.plot.getDomainAxis();
		axis.setDisplayNode(this.curdisplayednode,period);
		
		super.plot.getRangeAxis().setRange(0, highestHigh*1.12);
		
		super.plot.setDataset(barchartdataset);
	}

}

class CustomRendererForZhanBi extends BanKuaiFengXiBarRenderer 
{
	private static final long serialVersionUID = 1L;
   
    public CustomRendererForZhanBi() {
        super();
        super.displayedmaxwklevel = 4;
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

    	Integer maxweek = nodexdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(selecteddate);

		if(maxweek != null  && maxweek >= super.displayedmaxwklevel)
			return Color.MAGENTA;
		else 
			return Color.black;
    }
    
}

class CustomToolTipGeneratorForZhanBi extends BanKuaiFengXiBarToolTipGenerator 
{
	public String generateToolTip(CategoryDataset dataset, int row, int column)  
    {
		String selected = dataset.getColumnKey(column).toString();
    	LocalDate selecteddate = CommonUtility.formateStringToDate(selected);
    	 
//    	ChenJiaoZhanBiInGivenPeriod nodefx = nodexdata.getSpecficRecord(selecteddate, 0);
		
		String tooltip = selected.toString() + " ";
		
		Double curzhanbidata = (Double)dataset.getValue(row, column);  //ռ��
		if(curzhanbidata == null)
			return null;

			Integer maxweek = nodexdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(selecteddate);//nodefx.getGgbkzhanbimaxweek();
			if(maxweek == null)
				return null;
			
			DecimalFormat decimalformate = new DecimalFormat("%#0.000");
			try {
				tooltip = tooltip +  "ռ��" + decimalformate.format(curzhanbidata) ;
			} catch (java.lang.IllegalArgumentException e ) {
				tooltip = tooltip  +  "ռ��NULL";
			}
//			try {
//				tooltip = tooltip +   "ռ�ȱ仯("	+ decimalformate.format(zhanbigrowthrate) +  ")";
//			} catch (java.lang.IllegalArgumentException e ) {
//				tooltip = tooltip  +  "ռ�ȱ仯(NULL)";
//			}
			try {
				tooltip = tooltip +  "MaxWk=" + maxweek.toString() ;
			} catch (java.lang.IllegalArgumentException e ) {
				tooltip = tooltip + "MaxWk=NULL";
			}
			
			return tooltip;
    }
}

