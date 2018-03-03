package com.exchangeinfomanager.bankuaifengxi;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.DaPan;

public class BanKuaiFengXiBarGgDpLargePeriodChartPnl extends BanKuaiFengXiBarLargePeriodChartPnl 
{
	public BanKuaiFengXiBarGgDpLargePeriodChartPnl (BkChanYeLianTreeNode node, LocalDate displayedenddate1, DaPan dapan)
	{
		super (node,displayedenddate1,dapan);
		
		dataset = updateDataset(node,displayedenddate1,dapan); 
		mainPlot.setDataset(dataset);
		
//		chart.fireChartChanged();//必须有这句
	}
	
    protected XYDataset updateDataset(BkChanYeLianTreeNode node, LocalDate displayedenddate1, DaPan dapan) 
    {
//    	this.curdisplayednode = node;
    	LocalDate requirestart = node.getWkRecordsStartDate().with(DayOfWeek.SATURDAY);
		LocalDate requireend = node.getWkRecordsEndDate().with(DayOfWeek.SATURDAY);;
		
		TimeSeries s2 = new TimeSeries("个股大盘成交额占比");
		double highestHigh =0.0; //设置显示范围
//		datafx = new DefaultCategoryDataset();
		
		for(LocalDate tmpdate = requirestart;tmpdate.isBefore( requireend) || tmpdate.isEqual(requireend); tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ){
			ChenJiaoZhanBiInGivenPeriod tmpggrecord = node.getSpecficChenJiaoErRecord(tmpdate);
			ChenJiaoZhanBiInGivenPeriod tmpdprecord = dapan.getSpecficChenJiaoErRecord(tmpdate); //返回的是上证或深圳的某个记录，里面uplevel记录的是整个大盘的成交额
			
			if(tmpggrecord != null) {
				Double ggchenjiaol = tmpggrecord.getMyOwnChengJiaoEr();
				Double dpchenjiaol = tmpdprecord.getUpLevelChengJiaoEr() ; //通达信在输出板块成交量的时候，只输出手数，所以要*100，而输出个股的成交量是包含手数的，也是奇怪。返回的是上证或深圳的某个记录，里面uplevel记录的是整个大盘的成交量
				Double ggdpratio = ggchenjiaol/dpchenjiaol; 
				LocalDate lastdayofweek = tmpggrecord.getRecordsDayofEndofWeek();
				
				int tmpyear = lastdayofweek.getYear();
				int tmpmonth = lastdayofweek.getMonthValue();
				int tmpdayofmonth = lastdayofweek.getDayOfMonth();
				Day tmpday = new Day (tmpdayofmonth,tmpmonth,tmpyear);
				s2.add(tmpday,ggdpratio);
				
				if(ggdpratio > highestHigh)
					highestHigh = ggdpratio;

			} else {
				if( !dapan.isThisWeekXiuShi(tmpdate) ) {
					int tmpyear = tmpdate.getYear();
					int tmpmonth = tmpdate.getMonthValue();
					int tmpdayofmonth = tmpdate.getDayOfMonth();
					Day tmpday = new Day (tmpdayofmonth,tmpmonth,tmpyear);
					s2.add(tmpday,0);
//					datafx.addValue(0, "分析结果", tmpdate);
				} else //为空说明该周市场没有交易
					continue;
			}
		}
		
		final TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(s2);
        
        return dataset;
    }
}
