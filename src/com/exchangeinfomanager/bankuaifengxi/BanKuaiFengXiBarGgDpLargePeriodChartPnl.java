package com.exchangeinfomanager.bankuaifengxi;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.ChenJiaoZhanBiInGivenPeriod;
import com.exchangeinfomanager.asinglestockinfo.DaPan;

public class BanKuaiFengXiBarGgDpLargePeriodChartPnl extends BanKuaiFengXiBarLargePeriodChartPnl 
{
	public BanKuaiFengXiBarGgDpLargePeriodChartPnl (BkChanYeLianTreeNode node, LocalDate displayedenddate1, String period)
	{
		super (node,displayedenddate1,period);
		
		dataset = updateDataset(node,displayedenddate1,period); 
		mainPlot.setDataset(dataset);
		
//		chart.fireChartChanged();//���������
	}
	
    protected XYDataset updateDataset(BkChanYeLianTreeNode node, LocalDate displayedenddate1, String period) 
    {
    	this.curdisplayednode = node;
    	NodeXPeriodData nodexdata = node.getNodeXPeroidData(period);
    	LocalDate requirestart = nodexdata.getRecordsStartDate().with(DayOfWeek.SATURDAY);
		LocalDate requireend = nodexdata.getRecordsEndDate().with(DayOfWeek.SATURDAY);;

		DaPan dapan = (DaPan)node.getRoot();
		NodeXPeriodData dpxdata = node.getNodeXPeroidData(period);
		
		TimeSeries s2 = new TimeSeries("���ɴ��̳ɽ���ռ��");
		double highestHigh =0.0; //������ʾ��Χ
//		datafx = new DefaultCategoryDataset();
		
		LocalDate tmpdate = requirestart;
		do {
			ChenJiaoZhanBiInGivenPeriod tmpggrecord = nodexdata.getSpecficRecord(tmpdate,0);
			ChenJiaoZhanBiInGivenPeriod tmpdprecord = dpxdata.getSpecficRecord(tmpdate,0); //���ص�����֤�����ڵ�ĳ����¼������uplevel��¼�����������̵ĳɽ���
			
			if(tmpggrecord != null) {
				Double ggchenjiaol = tmpggrecord.getMyOwnChengJiaoEr();
				Double dpchenjiaol = tmpdprecord.getUpLevelChengJiaoEr() ; //ͨ������������ɽ�����ʱ��ֻ�������������Ҫ*100����������ɵĳɽ����ǰ��������ģ�Ҳ����֡����ص�����֤�����ڵ�ĳ����¼������uplevel��¼�����������̵ĳɽ���
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
				if( !dapan.isDaPanXiuShi(tmpdate,period) ) {
					int tmpyear = tmpdate.getYear();
					int tmpmonth = tmpdate.getMonthValue();
					int tmpdayofmonth = tmpdate.getDayOfMonth();
					Day tmpday = new Day (tmpdayofmonth,tmpmonth,tmpyear);
					s2.add(tmpday,0);
//					datafx.addValue(0, "�������", tmpdate);
				} 
			}
			
			if(period.equals(ChenJiaoZhanBiInGivenPeriod.WEEK))
				tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
			else if(period.equals(ChenJiaoZhanBiInGivenPeriod.DAY))
				tmpdate = tmpdate.plus(1, ChronoUnit.DAYS) ;
			else if(period.equals(ChenJiaoZhanBiInGivenPeriod.MONTH))
				tmpdate = tmpdate.plus(1, ChronoUnit.MONTHS) ;
			
		} while (tmpdate.isBefore( requireend) || tmpdate.isEqual(requireend));
		
		final TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(s2);
        
        return dataset;
    }

	
}
