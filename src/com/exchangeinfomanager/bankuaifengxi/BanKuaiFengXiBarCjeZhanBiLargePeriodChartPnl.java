package com.exchangeinfomanager.bankuaifengxi;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.DaPan;

public class BanKuaiFengXiBarCjeZhanBiLargePeriodChartPnl extends BanKuaiFengXiBarLargePeriodChartPnl 
{
	public BanKuaiFengXiBarCjeZhanBiLargePeriodChartPnl (BkChanYeLianTreeNode node, LocalDate displayedenddate1,String period)
	{
		super (node,displayedenddate1,period);
		DaPan tmpdp = (DaPan)node.getRoot();
		dataset = updateDataset(node,displayedenddate1,period); 
		mainPlot.setDataset(dataset);
	}
	@Override
    protected XYDataset updateDataset(BkChanYeLianTreeNode node, LocalDate displayedenddate1,String period) 
    {
		NodeXPeriodDataBasic nodexdata = node.getNodeXPeroidData(period);
    	LocalDate requirestart = nodexdata.getRecordsStartDate().with(DayOfWeek.SATURDAY);
		LocalDate requireend = nodexdata.getRecordsEndDate().with(DayOfWeek.SATURDAY);;
		TimeSeries rangecjezb = nodexdata.getRangeChengJiaoErZhanBi(requirestart, requireend);
		
		double highestHigh = rangecjezb.getMaxY();; //…Ë÷√œ‘ æ∑∂Œß

		final TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(rangecjezb);
        
        return dataset;
    }



}
