package com.exchangeinfomanager.asinglestockinfo;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.apache.log4j.Logger;

import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.ChenJiaoZhanBiInGivenPeriod;
import com.exchangeinfomanager.asinglestockinfo.BanKuai.BanKuaiNodeXPeriodData;

public class DaPan extends BkChanYeLianTreeNode
{

	public DaPan(String dpcode,String dpname) 
	{
		super(dpcode,dpname);
		
		super.nodewkdata = new DaPanNodeXPeriodData (ChenJiaoZhanBiInGivenPeriod.WEEK) ;
		super.nodedaydata = new DaPanNodeXPeriodData (ChenJiaoZhanBiInGivenPeriod.DAY) ;
		super.nodemonthdata = new DaPanNodeXPeriodData (ChenJiaoZhanBiInGivenPeriod.MONTH) ;
	}
	private static Logger logger = Logger.getLogger(DaPan.class);
	private BanKuai shanghai; //��֤�ɽ���¼
	private BanKuai shenzhen; //��֤�ɽ���¼
	
	public void setShangHai (BanKuai sh)
	{
		this.shanghai = sh;
	}
	public void setShenZhen(BanKuai sz)
	{
		this.shenzhen = sz;
	}
	public NodeXPeriodData getNodeXPeroidData (String period)
	{
		if(period.equals(ChenJiaoZhanBiInGivenPeriod.WEEK))
			return nodewkdata;
		else if(period.equals(ChenJiaoZhanBiInGivenPeriod.MONTH))
			return nodemonthdata;
		else if(period.equals(ChenJiaoZhanBiInGivenPeriod.DAY))
			return nodedaydata;
		else 
			return null;
	}
	/*
	 * 
	 */
	public Boolean  isDaPanXiuShi (LocalDate date,int difference,String period)
	{
		if(this.shanghai.getNodeXPeroidData(period).getSpecficRecord(date,difference) == null)
			return true;
		else
			return false;
	}
	
	public class DaPanNodeXPeriodData extends NodeXPeriodData
	{
		public DaPanNodeXPeriodData (String nodeperiodtype1)
		{
			super(nodeperiodtype1);
		}
		/*
		 * ���ָ�����ڵļ�¼
		 */
		@Override
		public ChenJiaoZhanBiInGivenPeriod getSpecficRecord(LocalDate requireddate, int differentce) 
		{
//			Double dapanchaer = 0.0;
			String recordsperiod = super.getNodeperiodtype();
			NodeXPeriodData periodrecords = shanghai.getNodeXPeroidData(recordsperiod);
			ChenJiaoZhanBiInGivenPeriod record = periodrecords.getSpecficRecord(requireddate,differentce);
			if( record != null) {
					
//					Double curcje = record.getMyOwnChengJiaoEr();
//					Double upcje = record.getUpLevelChengJiaoEr();
//					dapanchaer = dapanchaer + curcje; 
					return record;
			}
			
//			Integer szindex = this.szeperiodlist.getRequiredRecordsPostion(requireddate);
//			if( shindex != null) {
//					ChenJiaoZhanBiInGivenPeriod curcjlrecord = this.szeperiodlist.getChenJiaoErZhanBiInGivenPeriod().get(szindex);
//					Double curcje = curcjlrecord.getMyOwnChengJiaoEr();
//					dapanchaer = dapanchaer + curcje;
//			}

			return null;
		}
		/*
		 * ����ָ�����ں������ڵĳɽ�����
		 */
		public Double getChengJiaoErDifferenceWithLastPeriod (LocalDate requireddate)
		{
//			LocalDate lastwkdate = requireddate.with(DayOfWeek.MONDAY).minus(1,ChronoUnit.WEEKS).with(DayOfWeek.MONDAY);
			Double dapanchaer = null;
			
			String recordsperiod = super.getNodeperiodtype();
			NodeXPeriodData shanghaiperiodrecords = shanghai.getNodeXPeroidData(recordsperiod);
			ChenJiaoZhanBiInGivenPeriod currecord = shanghaiperiodrecords.getSpecficRecord(requireddate,0);
			if( currecord != null) {
					try {
						ChenJiaoZhanBiInGivenPeriod lastrecord = shanghaiperiodrecords.getSpecficRecord(requireddate,-1);
						 
						 Double curcje = currecord.getMyOwnChengJiaoEr();
						 Double lastcje = lastrecord.getMyOwnChengJiaoEr();
						 
						 dapanchaer = curcje - lastcje; 
					} catch (java.lang.ArrayIndexOutOfBoundsException e) { 		//���˼�¼�ĵ�һ����¼��
							logger.debug(getMyOwnCode() + getMyOwnName() + "������һ���¸��ɻ���");
						 dapanchaer = 0.0;
					}
			}
			
			NodeXPeriodData shenzhenperiodrecords = shenzhen.getNodeXPeroidData(recordsperiod);
			currecord = shenzhenperiodrecords.getSpecficRecord(requireddate,0);
			if( currecord != null) {
					try {
						ChenJiaoZhanBiInGivenPeriod lastrecord = shenzhenperiodrecords.getSpecficRecord(requireddate,-1);
						 
						 Double curcje = currecord.getMyOwnChengJiaoEr();
						 Double lastcje = lastrecord.getMyOwnChengJiaoEr();
						 
						 dapanchaer = dapanchaer + curcje - lastcje; 
					} catch (java.lang.ArrayIndexOutOfBoundsException e) { 		//���˼�¼�ĵ�һ����¼��
//							logger.debug(this.getMyOwnCode() + this.getMyOwnName() + "������һ���¸��ɻ���");
						 dapanchaer = dapanchaer + 0.0;
					}
			}
			
			return dapanchaer;
		}
		@Override
		public Double getChenJiaoErZhanBiGrowthRateOfSuperBanKuai(LocalDate requireddate) {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public Integer getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(LocalDate requireddate) {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public Double getChenJiaoErChangeGrowthRateOfSuperBanKuai(LocalDate requireddate) {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public ChenJiaoZhanBiInGivenPeriod getNodeFengXiResultForSpecificDate(LocalDate requireddate) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	

	
}
