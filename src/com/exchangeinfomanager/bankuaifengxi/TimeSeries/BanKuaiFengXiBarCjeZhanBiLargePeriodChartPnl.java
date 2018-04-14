package com.exchangeinfomanager.bankuaifengxi.TimeSeries;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.Locale;

import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.DaPan;

public class BanKuaiFengXiBarCjeZhanBiLargePeriodChartPnl extends BanKuaiFengXiBarLargePeriodChartPnl 
{
	public BanKuaiFengXiBarCjeZhanBiLargePeriodChartPnl (BkChanYeLianTreeNode node, LocalDate displayedenddate1,String period)
	{
		super (node,displayedenddate1,period);
		super.chart.setNotify(false);
	    NumberFormat format = NumberFormat.getPercentInstance();
        format.setMaximumFractionDigits(3); // etc.
        DateFormat dateformate = DateFormat.getDateInstance();
        ((CustomXYBarRenderer)super.mainPlot.getRenderer()).setBaseItemLabelGenerator(new StandardXYItemLabelGenerator(StandardXYItemLabelGenerator.DEFAULT_ITEM_LABEL_FORMAT, dateformate, format) );
        ((CustomXYBarRenderer)super.mainPlot.getRenderer()).setBaseToolTipGenerator(new CustomXYPlotCjeZbToolTipGenerator());
        
		dataset = updateDataset(node,displayedenddate1,period); 
		mainPlot.setDataset(dataset);
		
		super.chart.setNotify(true);
	}
	@Override
    protected XYDataset updateDataset(BkChanYeLianTreeNode node, LocalDate displayedenddate1,String period) 
    {
		NodeXPeriodDataBasic nodexdata = node.getNodeXPeroidData(period);
    	LocalDate requirestart = nodexdata.getRecordsStartDate().with(DayOfWeek.SATURDAY);
		LocalDate requireend = nodexdata.getRecordsEndDate().with(DayOfWeek.SATURDAY);;
		TimeSeries rangecjezb = nodexdata.getRangeChengJiaoErZhanBi(requirestart, requireend);

		TimeSeriesCollection dataset = (TimeSeriesCollection) super.mainPlot.getDataset();
		dataset.setNotify(false);
        dataset.addSeries(rangecjezb);
        
//        ((CustomXYBarRenderer)super.mainPlot.getRenderer()).setDisplayNode(node);
        ((CustomXYBarRenderer)super.mainPlot.getRenderer()).setDisplayNodeXPeriodData (nodexdata);
        
        dataset.setNotify(true);
        return dataset;
    }



}


class CustomXYPlotCjeZbToolTipGenerator extends CustomXYPlotToolTipGenerator 
{
//	protected BkChanYeLianTreeNode node;
//	protected NodeXPeriodDataBasic nodexdata;
	
	@Override
    public String generateToolTip(XYDataset dataset, int series, int item) 
	{
		//系统一直以星期日为一周开始，显示要调整为当周五
       Date d = new Date((long) dataset.getXValue(series, item));
       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
       TemporalField fieldUS = WeekFields.of(Locale.US).dayOfWeek();
   	   LocalDate selecteddate = CommonUtility.formateStringToDate(sdf.format(d)).with(fieldUS , 6);
   	 
		String tooltip = selecteddate.toString() + " ";
		 
			Double curzhanbidata = dataset.getYValue(series, item);   //占比
			DecimalFormat decimalformate = new DecimalFormat("%#0.000");
			try {
				tooltip = tooltip + "占比" + decimalformate.format(curzhanbidata);
			} catch (java.lang.IllegalArgumentException e ) {
				tooltip = tooltip + "占比NULL" ;
			}
			
			Integer maxweek = 0;
			try {
				 maxweek = nodexdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(selecteddate,0);
				 tooltip = tooltip + "MAXWK= " + maxweek;
			} catch (java.lang.NullPointerException e) {
				 maxweek = 0;
				 tooltip = tooltip + "MAXWK= 0" ;
			}

			return tooltip;
    }
	
//    public void setDisplayNode (BkChanYeLianTreeNode curdisplayednode) 
//    {
//    	this.node = curdisplayednode;
//    }
//    public void setDisplayNodeXPeriod(NodeXPeriodDataBasic nodexdata1) 
//    {
//		this.nodexdata = nodexdata1;
//	}
}

