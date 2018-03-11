package com.exchangeinfomanager.asinglestockinfo;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.ChenJiaoZhanBiInGivenPeriod;
import com.exchangeinfomanager.asinglestockinfo.BanKuai.BanKuaiNodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic;

public class DaPan extends BkChanYeLianTreeNode
{

	public DaPan(String dpcode,String dpname) 
	{
		super(dpcode,dpname);
		super.nodetype = BanKuaiAndStockBasic.DAPAN;
		
		super.nodewkdata = new DaPanNodeXPeriodData (ChenJiaoZhanBiInGivenPeriod.WEEK) ;
		super.nodedaydata = new DaPanNodeXPeriodData (ChenJiaoZhanBiInGivenPeriod.DAY) ;
		super.nodemonthdata = new DaPanNodeXPeriodData (ChenJiaoZhanBiInGivenPeriod.MONTH) ;
	}
	private static Logger logger = Logger.getLogger(DaPan.class);
	private BanKuai shanghai; //上证成交记录
	private BanKuai shenzhen; //深证成交记录
	
	public void setShangHai (BanKuai sh)
	{
		this.shanghai = sh;
	}
	public void setShenZhen(BanKuai sz)
	{
		this.shenzhen = sz;
	}
	public NodeXPeriodDataBasic getNodeXPeroidData (String period)
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
	
	public class DaPanNodeXPeriodData implements NodeXPeriodDataBasic
	{
		private String nodeperiodtype;
		public DaPanNodeXPeriodData (String nodeperiodtype1)
		{
			this.nodeperiodtype = nodeperiodtype1;
		}
		
		@Override
		public String getNodeperiodtype() 
		{
			return this.nodeperiodtype;
		}
		/*
		 * 获得指定周期的记录
		 */
		@Override
		public ChenJiaoZhanBiInGivenPeriod getSpecficRecord(LocalDate requireddate, int differentce) 
		{
//			Double dapanchaer = 0.0;
			String recordsperiod = getNodeperiodtype();
			NodeXPeriodDataBasic periodrecords = shanghai.getNodeXPeroidData(recordsperiod);
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
		 * 计算指定周期和上周期的成交额差额
		 */
		public Double getChengJiaoErDifferenceWithLastPeriod (LocalDate requireddate)
		{
			Double dapanchaer = null;
			
			String recordsperiod = getNodeperiodtype();
			NodeXPeriodDataBasic shanghaiperiodrecords = shanghai.getNodeXPeroidData(recordsperiod);
			ChenJiaoZhanBiInGivenPeriod currecord = shanghaiperiodrecords.getSpecficRecord(requireddate,0);
			if( currecord != null) {
					try {
						ChenJiaoZhanBiInGivenPeriod lastrecord = shanghaiperiodrecords.getSpecficRecord(requireddate,-1);
						 
						 Double curcje = currecord.getMyOwnChengJiaoEr();
						 Double lastcje = lastrecord.getMyOwnChengJiaoEr();
						 
						 dapanchaer = curcje - lastcje; 
					} catch (java.lang.ArrayIndexOutOfBoundsException e) { 		//到了记录的第一个记录了
							logger.debug(getMyOwnCode() + getMyOwnName() + "可能是一个新个股或板块");
						 dapanchaer = 0.0;
					}
			}
			
			NodeXPeriodDataBasic shenzhenperiodrecords = shenzhen.getNodeXPeroidData(recordsperiod);
			currecord = shenzhenperiodrecords.getSpecficRecord(requireddate,0);
			if( currecord != null) {
					try {
						ChenJiaoZhanBiInGivenPeriod lastrecord = shenzhenperiodrecords.getSpecficRecord(requireddate,-1);
						 
						 Double curcje = currecord.getMyOwnChengJiaoEr();
						 Double lastcje = lastrecord.getMyOwnChengJiaoEr();
						 
						 dapanchaer = dapanchaer + curcje - lastcje; 
					} catch (java.lang.ArrayIndexOutOfBoundsException e) { 		//到了记录的第一个记录了
//							logger.debug(this.getMyOwnCode() + this.getMyOwnName() + "可能是一个新个股或板块");
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
		
		@Override
		public void setNodePeriodRecords(ArrayList<ChenJiaoZhanBiInGivenPeriod> periodlist1) {
			// TODO Auto-generated method stub
			
		}
		@Override
		public boolean addRecordsForAGivenPeriod(ArrayList<ChenJiaoZhanBiInGivenPeriod> periodlist1,
				LocalDate position) {
			// TODO Auto-generated method stub
			return false;
		}
		@Override
		public LocalDate getRecordsStartDate() {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public LocalDate getRecordsEndDate() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Integer getChenJiaoErMaxWeekOfSuperBanKuai(LocalDate requireddate) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	

	
}
