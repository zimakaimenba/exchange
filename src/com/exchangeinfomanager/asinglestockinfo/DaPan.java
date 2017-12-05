package com.exchangeinfomanager.asinglestockinfo;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.exchangeinfomanager.bankuaifengxi.ChenJiaoZhanBiInGivenPeriod;

public class DaPan extends BkChanYeLianTreeNode
{

	public DaPan(String dpcode,String dpname) 
	{
		super(dpcode,dpname);
	}
	
	private BanKuai sheperiodlist; //上证成交记录
	private BanKuai szeperiodlist; //深证成交记录
	
	public void setShangHai (BanKuai sh)
	{
		this.sheperiodlist = sh;
	}
	public void setShenZhen(BanKuai sz)
	{
		this.szeperiodlist = sz;
	}
	/*
	 * 
	 */
	public Boolean  isThisWeekXiuShi (LocalDate date)
	{
		if(this.szeperiodlist.getSpecficChenJiaoErRecord(date) == null)
			return true;
		else
			return false;
	}
	/*
	 * 
	 */
	/*
	 * 获得指定周的记录
	 */
	public ChenJiaoZhanBiInGivenPeriod getSpecficChenJiaoErRecord (LocalDate requireddate)
	{
		Double dapanchaer = 0.0;
		Integer shindex = this.sheperiodlist.getRequiredRecordsPostion(requireddate);
		if( shindex != null) {
				ChenJiaoZhanBiInGivenPeriod curcjlrecord = this.sheperiodlist.getChenJiaoErZhanBiInGivenPeriod().get(shindex);
				Double curcje = curcjlrecord.getMyOwnChengJiaoEr();
				Double upcje = curcjlrecord.getUpLevelChengJiaoEr();
				dapanchaer = dapanchaer + curcje; 
				return curcjlrecord;
		}
		
//		Integer szindex = this.szeperiodlist.getRequiredRecordsPostion(requireddate);
//		if( shindex != null) {
//				ChenJiaoZhanBiInGivenPeriod curcjlrecord = this.szeperiodlist.getChenJiaoErZhanBiInGivenPeriod().get(szindex);
//				Double curcje = curcjlrecord.getMyOwnChengJiaoEr();
//				dapanchaer = dapanchaer + curcje;
//		}

		return null;
	}
	/*
	 * 计算指定周和上周的成交量差额
	 */
	public Double getChengJiaoErDifferenceOfLastWeek (LocalDate requireddate)
	{
//		LocalDate lastwkdate = requireddate.with(DayOfWeek.MONDAY).minus(1,ChronoUnit.WEEKS).with(DayOfWeek.MONDAY);
		Double dapanchaer = null;
		Integer shindex = this.sheperiodlist.getRequiredRecordsPostion(requireddate);
		if( shindex != null) {
				ChenJiaoZhanBiInGivenPeriod curcjlrecord = this.sheperiodlist.getChenJiaoErZhanBiInGivenPeriod().get(shindex);
				try {
					ChenJiaoZhanBiInGivenPeriod lastcjlrecord = this.sheperiodlist.getChenJiaoErZhanBiInGivenPeriod().get(shindex-1);
					 
					 Double curcje = curcjlrecord.getMyOwnChengJiaoEr();
					 Double lastcje = lastcjlrecord.getMyOwnChengJiaoEr();
					 
					 dapanchaer = curcje - lastcje; 
				} catch (java.lang.ArrayIndexOutOfBoundsException e) { 		//到了记录的第一个记录了
//						System.out.println(this.getMyOwnCode() + this.getMyOwnName() + "可能是一个新个股或板块");
					 dapanchaer = 0.0;
				}
		}
		
		Integer szindex = this.szeperiodlist.getRequiredRecordsPostion(requireddate);
		if( shindex != null) {
				ChenJiaoZhanBiInGivenPeriod curcjlrecord = this.szeperiodlist.getChenJiaoErZhanBiInGivenPeriod().get(szindex);
				try {
					ChenJiaoZhanBiInGivenPeriod lastcjlrecord = this.szeperiodlist.getChenJiaoErZhanBiInGivenPeriod().get(szindex-1);
					 
					 Double curcje = curcjlrecord.getMyOwnChengJiaoEr();
					 Double lastcje = lastcjlrecord.getMyOwnChengJiaoEr();
					 
					 dapanchaer = dapanchaer + curcje - lastcje; 
				} catch (java.lang.ArrayIndexOutOfBoundsException e) { 		//到了记录的第一个记录了
//						System.out.println(this.getMyOwnCode() + this.getMyOwnName() + "可能是一个新个股或板块");
					 dapanchaer = dapanchaer + 0.0;
				}
		}
		
		return dapanchaer;
		
	}

	
}
