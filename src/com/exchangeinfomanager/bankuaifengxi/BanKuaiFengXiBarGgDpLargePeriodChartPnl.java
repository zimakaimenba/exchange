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
		
//		chart.fireChartChanged();//���������
	}
	
    protected XYDataset updateDataset(BkChanYeLianTreeNode node, LocalDate displayedenddate1, DaPan dapan) 
    {
//    	this.curdisplayednode = node;
    	LocalDate requirestart = node.getWkRecordsStartDate().with(DayOfWeek.SATURDAY);
		LocalDate requireend = node.getWkRecordsEndDate().with(DayOfWeek.SATURDAY);;
		
		TimeSeries s2 = new TimeSeries("���ɴ��̳ɽ���ռ��");
		double highestHigh =0.0; //������ʾ��Χ
//		datafx = new DefaultCategoryDataset();
		
		for(LocalDate tmpdate = requirestart;tmpdate.isBefore( requireend) || tmpdate.isEqual(requireend); tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ){
			ChenJiaoZhanBiInGivenPeriod tmpggrecord = node.getSpecficChenJiaoErRecord(tmpdate);
			ChenJiaoZhanBiInGivenPeriod tmpdprecord = dapan.getSpecficChenJiaoErRecord(tmpdate); //���ص�����֤�����ڵ�ĳ����¼������uplevel��¼�����������̵ĳɽ���
			
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
				if( !dapan.isThisWeekXiuShi(tmpdate) ) {
					int tmpyear = tmpdate.getYear();
					int tmpmonth = tmpdate.getMonthValue();
					int tmpdayofmonth = tmpdate.getDayOfMonth();
					Day tmpday = new Day (tmpdayofmonth,tmpmonth,tmpyear);
					s2.add(tmpday,0);
//					datafx.addValue(0, "�������", tmpdate);
				} else //Ϊ��˵�������г�û�н���
					continue;
			}
		}
		
		final TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(s2);
        
        return dataset;
    }
}
