package com.exchangeinfomanager.asinglestockinfo;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.jfree.data.time.TimeSeriesDataItem;

import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.Stock.StockNodeXPeriodData;

public class StockOfBanKuai extends Stock
{
	public StockOfBanKuai (BanKuai bankuai1,Stock stock1) 
	{
		super (stock1.getMyOwnCode(),stock1.getMyOwnName());
		super.nodetype = BanKuaiAndStockBasic.BKGEGU;
		
		this.bankuai = bankuai1;
		this.stock = stock1;
		
		super.nodewkdata = new StockOfBanKuaiNodeXPeriodData (StockGivenPeriodDataItem.WEEK) ;
		super.nodedaydata = null; //���ɶ԰�飬�������ݲ������壬���ߺ�������
		super.nodemonthdata = new StockOfBanKuaiNodeXPeriodData (StockGivenPeriodDataItem.MONTH) ;
	}
	
	private static Logger logger = Logger.getLogger(StockOfBanKuai.class);
	private BanKuai bankuai;
	private Stock stock;
	private Integer quanzhong;
	
	public void setStockQuanZhong (Integer quanzhong)
	{
		this.quanzhong = quanzhong; 
	}
	public Stock getStock ()
	{
		return this.stock;
	}
	public BanKuai getBanKuai ()
	{
		return this.bankuai;
	}
	public Integer getQuanZhong ()
	{
		return this.quanzhong;
	}
	@Override
	public NodeXPeriodDataBasic getNodeXPeroidData(String period) {
		NodeXPeriodDataBasic nodexdata = getStockXPeriodDataForBanKuai (period);
		return nodexdata;
	}
	
	public NodeXPeriodDataBasic getStockXPeriodDataForBanKuai (String period)
	{
		if(period.equals(StockGivenPeriodDataItem.MONTH))
			return (StockOfBanKuaiNodeXPeriodData) this.nodemonthdata;
		else if(period.equals(StockGivenPeriodDataItem.WEEK))
			return this.nodewkdata;
		else 
			return null;
	}
	
	public class StockOfBanKuaiNodeXPeriodData extends StockNodeXPeriodData
	{
		public StockOfBanKuaiNodeXPeriodData (String nodeperiodtype1) 
		{
			super(nodeperiodtype1);
		}
	//		@Override
		/*
		 * (non-Javadoc)
		 * @see com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData#getChenJiaoErChangeGrowthRateOfSuperBanKuai(java.time.LocalDate)
		 * �������ú�����ͬ�ĵط����������ǰ��ĳɽ��������Ǵ��̵ĳɽ���
		 */
		public Double getChenJiaoErChangeGrowthRateOfSuperBanKuai(LocalDate requireddate) 
		{
				if(stockohlc == null)
					return null;
				
				TimeSeriesDataItem curcjlrecord = this.stockamo.getDataItem( getJFreeChartFormateTimePeriod(requireddate,0));
				if( curcjlrecord == null) 
					return null;
				
				//�ж��ϼ����(���̻��߰��)�Ƿ�����,������û�бȽϵ����壬ֱ�ӷ���-100��
				String nodept = getNodeperiodtype();
				NodeXPeriodDataBasic bkxdata = bankuai.getNodeXPeroidData(nodept);
				Double bkcjediff = bkxdata.getChengJiaoErDifferenceWithLastPeriod(requireddate);
				if( bkcjediff<0 || bkcjediff == null ) {//���������
					return -100.0;
				}
				
				int index = -1;
				while ( ((DaPan)getRoot()).isDaPanXiuShi(requireddate, index ,getNodeperiodtype()) ) { //���ܿ��ܴ�������
					index --;
				}
				
				TimeSeriesDataItem lastcjlrecord = stockamo.getDataItem( getJFreeChartFormateTimePeriod (requireddate,index) );
				if(lastcjlrecord == null) { //����ǰ���ǿգ�˵����ͣ�ƺ�����
					Double curggcje = curcjlrecord.getValue().doubleValue(); //�°�����гɽ�����Ӧ�ü�����
					return curggcje/bkcjediff;
				}
				
				Double curcje = curcjlrecord.getValue().doubleValue();
				Double lastcje = lastcjlrecord.getValue().doubleValue();
				Double cjechange = curcje - lastcje; //���ɳɽ����ı仯
				
				return cjechange/bkcjediff;
		}
	}
}