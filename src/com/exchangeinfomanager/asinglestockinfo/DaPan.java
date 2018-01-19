package com.exchangeinfomanager.asinglestockinfo;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.apache.log4j.Logger;

import com.exchangeinfomanager.bankuaifengxi.ChenJiaoZhanBiInGivenPeriod;

public class DaPan extends BkChanYeLianTreeNode
{

	public DaPan(String dpcode,String dpname) 
	{
		super(dpcode,dpname);
	}
	private static Logger logger = Logger.getLogger(DaPan.class);
	private BanKuai sheperiodlist; //��֤�ɽ���¼
	private BanKuai szeperiodlist; //��֤�ɽ���¼
	
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
	 * ���ָ���ܵļ�¼
	 */
	public ChenJiaoZhanBiInGivenPeriod getSpecficChenJiaoErRecord (LocalDate requireddate)
	{
		Double dapanchaer = 0.0;
		Integer shindex = this.sheperiodlist.getRequiredRecordsPostion(requireddate);
		if( shindex != null) {
				ChenJiaoZhanBiInGivenPeriod curcjlrecord = this.sheperiodlist.getWkChenJiaoErZhanBiInGivenPeriod().get(shindex);
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
	 * ����ָ���ܺ����ܵĳɽ������
	 */
	public Double getChengJiaoErDifferenceOfLastWeek (LocalDate requireddate)
	{
//		LocalDate lastwkdate = requireddate.with(DayOfWeek.MONDAY).minus(1,ChronoUnit.WEEKS).with(DayOfWeek.MONDAY);
		Double dapanchaer = null;
		Integer shindex = this.sheperiodlist.getRequiredRecordsPostion(requireddate);
		if( shindex != null) {
				ChenJiaoZhanBiInGivenPeriod curcjlrecord = this.sheperiodlist.getWkChenJiaoErZhanBiInGivenPeriod().get(shindex);
				try {
					ChenJiaoZhanBiInGivenPeriod lastcjlrecord = this.sheperiodlist.getWkChenJiaoErZhanBiInGivenPeriod().get(shindex-1);
					 
					 Double curcje = curcjlrecord.getMyOwnChengJiaoEr();
					 Double lastcje = lastcjlrecord.getMyOwnChengJiaoEr();
					 
					 dapanchaer = curcje - lastcje; 
				} catch (java.lang.ArrayIndexOutOfBoundsException e) { 		//���˼�¼�ĵ�һ����¼��
//						logger.debug(this.getMyOwnCode() + this.getMyOwnName() + "������һ���¸��ɻ���");
					 dapanchaer = 0.0;
				}
		}
		
		Integer szindex = this.szeperiodlist.getRequiredRecordsPostion(requireddate);
		if( shindex != null) {
				ChenJiaoZhanBiInGivenPeriod curcjlrecord = this.szeperiodlist.getWkChenJiaoErZhanBiInGivenPeriod().get(szindex);
				try {
					ChenJiaoZhanBiInGivenPeriod lastcjlrecord = this.szeperiodlist.getWkChenJiaoErZhanBiInGivenPeriod().get(szindex-1);
					 
					 Double curcje = curcjlrecord.getMyOwnChengJiaoEr();
					 Double lastcje = lastcjlrecord.getMyOwnChengJiaoEr();
					 
					 dapanchaer = dapanchaer + curcje - lastcje; 
				} catch (java.lang.ArrayIndexOutOfBoundsException e) { 		//���˼�¼�ĵ�һ����¼��
//						logger.debug(this.getMyOwnCode() + this.getMyOwnName() + "������һ���¸��ɻ���");
					 dapanchaer = dapanchaer + 0.0;
				}
		}
		
		return dapanchaer;
		
	}

	
}
