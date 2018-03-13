package com.exchangeinfomanager.asinglestockinfo;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.Locale;

import org.apache.log4j.Logger;

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
		
		super.nodewkdata = new StockOfBanKuaiNodeXPeriodData (ChenJiaoZhanBiInGivenPeriod.WEEK) ;
		super.nodedaydata = null; //���ɶ԰�飬�������ݲ������壬���ߺ�������
		super.nodemonthdata = new StockOfBanKuaiNodeXPeriodData (ChenJiaoZhanBiInGivenPeriod.MONTH) ;
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
		if(period.equals(ChenJiaoZhanBiInGivenPeriod.MONTH))
			return (StockOfBanKuaiNodeXPeriodData) this.nodemonthdata;
		else if(period.equals(ChenJiaoZhanBiInGivenPeriod.WEEK))
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
//		public Integer getChenJiaoErMaxWeekOfSuperBanKuai(LocalDate requireddate) 
//		{
//			if(periodlist == null)
//				return null;
//			
//			ChenJiaoZhanBiInGivenPeriod curcjlrecord = this.getSpecficRecord (requireddate,0);
//			if( curcjlrecord == null) 
//				return null;
//			
//			Double curcje = curcjlrecord.getMyOwnChengJiaoEr();
//			int maxweek = 0;
//			
//			DaPan dapan = (DaPan)getRoot();
//			for(int index = -1;index >= -100000; index--) { //Ŀǰ��¼��������10000�����ڣ�����10000�㹻
//				if( dapan.isDaPanXiuShi(requireddate, index ,getNodeperiodtype())  ) //���̻���������
//					continue;
//
//				if(isTingPai(requireddate,index,getNodeperiodtype())) //����Ҳ����ͣ�ƣ�ͣ��ǰ�ľͲ�������
//					return maxweek;
//				
//				ChenJiaoZhanBiInGivenPeriod lastcjlrecord = this.getSpecficRecord (requireddate,index);
//				Double lastcje = lastcjlrecord.getMyOwnChengJiaoEr();
//				if(curcje > lastcje)
//					maxweek ++;
//				else
//					break;
//			}
//			
//			curcjlrecord.setGgBkCjeZhanbiMaxweek(maxweek);
//			return maxweek;
//		}
		/*
		 * ���ϼ����ĳɽ���ռ���Ƕ������ڵ����ֵ
		 */
//		public Integer getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(LocalDate requireddate) 
//		{
//			if(periodlist == null)
//				return null;
//			
//			ChenJiaoZhanBiInGivenPeriod curcjlrecord = this.getSpecficRecord (requireddate,0);
//			if( curcjlrecord == null) 
//				return null;
//			
//			DaPan dapan = (DaPan)getRoot();
//			Double curzhanbiratio = curcjlrecord.getCjeZhanBi();
//			int maxweek = 0;
//			for(int index = -1;index >= -100000; index--) {
//				if( dapan.isDaPanXiuShi(requireddate, index ,getNodeperiodtype())  ) //���̻���������
//					continue;
//				
//				if(isTingPai(requireddate,index,getNodeperiodtype())) //����Ҳ����ͣ�ƣ�ͣ��ǰ�ľͲ�������
//					return maxweek;
//				
//				ChenJiaoZhanBiInGivenPeriod lastcjlrecord = this.getSpecficRecord (requireddate,index);
//				
//				Double lastzhanbiratio = lastcjlrecord.getCjeZhanBi();
//				if(curzhanbiratio > lastzhanbiratio)
//					maxweek ++;
//				else
//					break;
//			}
//			
//			curcjlrecord.setGgBkCjeZhanbiMaxweek(maxweek);
//			return maxweek;
//		}
//		@Override
		/*
		 * (non-Javadoc)
		 * @see com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData#getChenJiaoErChangeGrowthRateOfSuperBanKuai(java.time.LocalDate)
		 * �������ú�����ͬ�ĵط����������ǰ��ĳɽ��������Ǵ��̵ĳɽ���
		 */
//		public Double getChenJiaoErChangeGrowthRateOfSuperBanKuai(LocalDate requireddate) 
//		{
//			if(periodlist == null)
//				return null;
//			
//			//�ж��ϼ����(���̻��߰��)�Ƿ�����,������û�бȽϵ����壬ֱ�ӷ���-100��
//			String nodept = super.getNodeperiodtype();
//			Double bkcjediff = bankuai.getNodeXPeroidData(nodept).getChengJiaoErDifferenceWithLastPeriod(requireddate);//�������ú�����ͬ�ĵط����������ǰ��ĳɽ��������Ǵ��̵ĳɽ���
//			if( bkcjediff == null || bkcjediff<0  ) //�ϼ����������
//				return -100.0;
//			
//			ChenJiaoZhanBiInGivenPeriod curcjlrecord = this.getSpecficRecord (requireddate,0);
//			if( curcjlrecord == null) 
//				return null;
//			
//			int index = -1;
//			while ( ((DaPan)getRoot()).isDaPanXiuShi(requireddate, index ,super.getNodeperiodtype()) ) { //���ܿ��ܴ�������
//				index --;
//			}
//			
//			ChenJiaoZhanBiInGivenPeriod lastcjlrecord = this.getSpecficRecord (requireddate,index);
//			if(lastcjlrecord == null) { //����ǰ���ǿգ�˵��Ҫ���°�顣���û��ͣ�Ƶ�
//				Double curggcje = curcjlrecord.getMyOwnChengJiaoEr(); //�°�����гɽ�����Ӧ�ü�����
//				return curggcje/bkcjediff;
//			}
//			
//			Double curcje = curcjlrecord.getMyOwnChengJiaoEr();
//			Double lastcje = lastcjlrecord.getMyOwnChengJiaoEr();
//			Double cjechange = curcje - lastcje; //���ɳɽ����ı仯
//			
//			return cjechange/bkcjediff;
//		}
		/*
		 * ������ɺʹ���/���ɽ����ĸ�������
		 */
//		public ChenJiaoZhanBiInGivenPeriod getNodeFengXiResultForSpecificDate (LocalDate requireddate)
//		{
//			return null;
//		}

	}


}