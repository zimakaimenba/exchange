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
import com.exchangeinfomanager.asinglestockinfo.ChenJiaoZhanBiInGivenPeriod;
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
		
		TimeSeries s2 = new TimeSeries("个股板块成交额占比");
		double highestHigh =0.0; //设置显示范围

		for(LocalDate tmpdate = requirestart;tmpdate.isBefore( requireend) || tmpdate.isEqual(requireend); tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ){
			ChenJiaoZhanBiInGivenPeriod tmprecord = nodexdata.getSpecficRecord(tmpdate,0);
			if(tmprecord != null) {
				Double ggbkratio = tmprecord.getCjeZhanBi();
				LocalDate lastdayofweek = tmprecord.getRecordsDayofEndofWeek();
				int tmpyear = lastdayofweek.getYear();
				int tmpmonth = lastdayofweek.getMonthValue();
				int tmpdayofmonth = lastdayofweek.getDayOfMonth();
				Day tmpday = new Day (tmpdayofmonth,tmpmonth,tmpyear);
				s2.add(tmpday,ggbkratio);
				
				if(ggbkratio > highestHigh)
					highestHigh = ggbkratio;
			} else { //没有数据，看看是不是大盘停牌还是该股当周没有数据
//				if( !dapan.isThisWeekXiuShi(tmpdate) ) { //大盘没有停牌
//					int tmpyear = tmpdate.getYear();
//					int tmpmonth = tmpdate.getMonthValue();
//					int tmpdayofmonth = tmpdate.getDayOfMonth();
//					Day tmpday = new Day (tmpdayofmonth,tmpmonth,tmpyear);
//					s2.add(tmpday,0);
////					datafx.addValue(0, "分析结果", tmpdate);
//				} else //为空说明该周市场没有交易
//					continue;
			}
		}
		
		final TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(s2);
        
        return dataset;
    }



}
