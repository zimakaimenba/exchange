package com.exchangeinfomanager.asinglestockinfo;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountInfoBasic;
import com.exchangeinfomanager.asinglestockinfo.BanKuai.BanKuaiNodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData;
import com.google.common.base.Strings;

public class Stock extends BkChanYeLianTreeNode {

	public Stock(String myowncode1,String name)
	{
		super(myowncode1,name);
		super.nodetype = BanKuaiAndStockBasic.TDXGG;
		
		if(myowncode1.startsWith("60"))
			super.setSuoShuJiaoYiSuo("sh");
		else
			super.setSuoShuJiaoYiSuo("sz");
		
//		suoShuTdxBanKuaiData = new HashMap<String,StockOfBanKuai> ();
		
		super.nodewkdata = new StockNodeXPeriodData (ChenJiaoZhanBiInGivenPeriod.WEEK) ;
		super.nodedaydata = new StockNodeXPeriodData (ChenJiaoZhanBiInGivenPeriod.DAY) ;
		super.nodemonthdata = new StockNodeXPeriodData (ChenJiaoZhanBiInGivenPeriod.MONTH) ;
	}
	
	private String checklistXml;
	private static Logger logger = Logger.getLogger(Stock.class);
	
//	private Multimap<String> chiCangAccountNameList; //持有该股票的所有账户的名字
	private HashMap<String,AccountInfoBasic> chiCangAccounts; //持有该股票的所有账户<账户，账户信息>
//	private HashMap<String,StockOfBanKuai> suoShuTdxBanKuaiData; //所属通达信系统板块 <板块code,板块名字>
//	private  HashMap<String,Integer> sysBanKuaiWeight; //所属通达信系统板块权重
	private HashSet<String> suoShuZdyBanKuai; //所属自定义板块
	private HashMap<String, String> suoShuCurSysBanKuai; //个股当前所属的通达信板块

//	private DaPan dapan;
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
	
	public void addNewChiCangAccount (AccountInfoBasic acnt)
	{
		if(chiCangAccounts == null)
			chiCangAccounts = new HashMap<String,AccountInfoBasic>();
		try {
			String acntname = acnt.getAccountName();
			if(!chiCangAccounts.containsKey(acntname))
				chiCangAccounts.put(acntname, acnt);
		} catch(java.lang.NullPointerException e) {
			e.printStackTrace();
			
		}
	}
	
	public void removeChiCangAccount (AccountInfoBasic acnt)
	{
		String acntname = acnt.getAccountName();
		chiCangAccounts.remove(acntname);
	}
	
	/**
	 * @return the chiCangAccountNameList
	 */
	public HashMap<String, AccountInfoBasic> getChiCangAccounts () {
		return chiCangAccounts;
	}
	public ArrayList<String> getChiCangAccountsNameList() 
	{
		
		return new  ArrayList<String>(chiCangAccounts.keySet());
	}

	/**
	 * @return the checklistXml
	 */
	public String getChecklistXml() {
		return checklistXml;
	}
	/**
	 * @param checklistXml the checklistXml to set
	 */
	public void setChecklistXml(String checklistXml) {
		this.checklistXml = checklistXml;
	}

	/*
	 * 
	 */
	public boolean isInTdxBanKuai (String tdxbk)
	{
		if(this.suoShuCurSysBanKuai.containsKey(tdxbk))
			return true;
		else 
			return false;
	}
	/**
	 * @return the suoShuBanKuai
	 */
	public HashMap<String,String> getGeGuCurSuoShuTDXSysBanKuaiList() {
		return suoShuCurSysBanKuai;
	}

	/**
	 * @param tmpsysbk the suoShuBanKuai to set
	 */
	public void setGeGuCurSuoShuTDXSysBanKuaiList(HashMap<String,String> tmpsysbk) {
		this.suoShuCurSysBanKuai = tmpsysbk;
	}
 
	
	/**
	 * @return the suoShuBanKuai
	 */
//	public HashSet<String> getSuoShuTDXZdyBanKuai() {
//		return suoShuZdyBanKuai;
//	}

	/**
	 * @param tmpbk the suoShuBanKuai to set
	 */
//	public void setSuoShuTDXZdyBanKuai(HashSet<String> tmpbk) {
//		this.suoShuZdyBanKuai = tmpbk;
//	}
	
	ArrayList<String> nodeallchanyelian ;
	
	public void setGeGuAllChanYeLianInfo(ArrayList<String> ggcyl) 
	{
		this.nodeallchanyelian = ggcyl;
	}
	public ArrayList<String> getGeGuAllChanYeLianInfo() 
	{
		return this.nodeallchanyelian;
	}
/*
 * (non-Javadoc)
 * @see com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode#setParseFileStockSet(java.util.HashSet)
 */
//	public  void setParseFileStockSet (HashSet<String> parsefilestockset2)
//	 {
//	    	if(super.parsefilestockset == null) {
//	    		this.parsefilestockset = new HashSet<String> ();
//	    		this.parsefilestockset = parsefilestockset2;
//	    	} else
//	    		this.parsefilestockset = parsefilestockset2;
//	 }
	
//	public void setStockXPeriodDataForABanKuai (String bankuaicode,StockOfBanKuai bkofst)
//	{
//		suoShuTdxBanKuaiData.put(bankuaicode, bkofst);
//	}
	
	/*
	 * 
	 */
	protected Boolean isTingPai (LocalDate requireddate,int difference, String period )
	{
		NodeXPeriodDataBasic nodexdate = this.getNodeXPeroidData(period);
		ChenJiaoZhanBiInGivenPeriod nodexcurdatedata = nodexdate.getSpecficRecord(requireddate, difference);
		if( nodexcurdatedata != null) 
			return false;
		else {
			LocalDate rstartday = nodexdate.getRecordsStartDate();
			LocalDate rendday = nodexdate.getRecordsEndDate();
			
			boolean dapanxiushi = ((DaPan)getRoot()).isDaPanXiuShi(requireddate,0,period );
			
			if(requireddate.isAfter(rstartday) && requireddate.isBefore(rendday) && !dapanxiushi) //如果日期数据范围内且没有数据，且大盘没有休市，说明停牌
				return true;
			else
				return null; //日期在数据范围外，就没办法判断，所以返回null
		}
	}
	/*
	 * 判断本周期是停牌后复牌
	 */
	protected Boolean isFuPaiAfterTingPai (LocalDate requireddate,int difference,String period )
	{
		NodeXPeriodDataBasic nodexdate = this.getNodeXPeroidData(period);
		ChenJiaoZhanBiInGivenPeriod nodexcurdatedata = nodexdate.getSpecficRecord(requireddate, difference);
		if( nodexcurdatedata == null) 
			return null;
		
		int index = -1;
		while ( ((DaPan)getRoot()).isDaPanXiuShi(requireddate, index ,period) ) { //上周可能大盘修饰
			index --;
		}
		
		ChenJiaoZhanBiInGivenPeriod lastcjlrecord = nodexdate.getSpecficRecord (requireddate,index);
		if(lastcjlrecord == null) {//休市前还是空，说明要么是新板块要么停牌的
			return true;
		} else 
			return false;
	}
	 
	
	/*
	 * stock 的这个内部类只针对自身对大盘的数据，自身对板块的数据在StockOfBanKuai
	 */
	public class StockNodeXPeriodData extends NodeXPeriodData
	{
		public StockNodeXPeriodData (String nodeperiodtype1) 
		{
			super(nodeperiodtype1);
		
		}
		
		@Override
		public Integer getChenJiaoErMaxWeekOfSuperBanKuai(LocalDate requireddate) 
		{
			if(periodlist == null)
				return null;
			
			ChenJiaoZhanBiInGivenPeriod curcjlrecord = this.getSpecficRecord (requireddate,0);
			if( curcjlrecord == null) 
				return null;
			
			Double curcje = curcjlrecord.getMyOwnChengJiaoEr();
			int maxweek = 0;

			DaPan dapan = (DaPan)getRoot();
			for(int index = -1;index >= -100000; index--) { //目前记录不可能有10000个周期，所以10000足够
				if( dapan.isDaPanXiuShi(requireddate, index ,getNodeperiodtype())  ) //大盘还可能休市
					continue;
				
				if(isTingPai(requireddate,index,getNodeperiodtype()))
					return maxweek;
				
				ChenJiaoZhanBiInGivenPeriod lastcjlrecord = this.getSpecficRecord (requireddate,index);
				if(lastcjlrecord == null ) //可能到了记录的头部了，或者是个诞生时间不长的板块
					return maxweek;
				
				Double lastcje = lastcjlrecord.getMyOwnChengJiaoEr();
				if(curcje > lastcje)
					maxweek ++;
				else
					break;
			}
			
			curcjlrecord.setGgBkCjeZhanbiMaxweek(maxweek);
			return maxweek;
		}
		/*
		 * 对上级板块的成交额占比是多少周内的最大值
		 */
		public Integer getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(LocalDate requireddate) 
		{
			if(periodlist == null)
				return null;
			
			ChenJiaoZhanBiInGivenPeriod curcjlrecord = this.getSpecficRecord (requireddate,0);
			if( curcjlrecord == null) 
				return null;
			
			DaPan dapan = (DaPan)getRoot();
			
			Double curzhanbiratio = curcjlrecord.getCjeZhanBi();
			int maxweek = 0;
			for(int index = -1;index >= -100000; index--) {
				if( dapan.isDaPanXiuShi(requireddate, index ,getNodeperiodtype())  ) //大盘还可能休市
					continue;
				
				if(isTingPai(requireddate,index,getNodeperiodtype())) //本周也可能停牌，停牌前的就不计算了
					return maxweek;
				
				ChenJiaoZhanBiInGivenPeriod lastcjlrecord = this.getSpecficRecord (requireddate,index);
				
				if(lastcjlrecord == null ) //可能到了记录的头部了，或者是个诞生时间不长的板块
					return maxweek;
				
				Double lastzhanbiratio = lastcjlrecord.getCjeZhanBi();
				if(curzhanbiratio > lastzhanbiratio)
					maxweek ++;
				else
					break;
			}
			
			curcjlrecord.setGgBkCjeZhanbiMaxweek(maxweek);
			return maxweek;
		}
		
		@Override
		public Double getChenJiaoErChangeGrowthRateOfSuperBanKuai(LocalDate requireddate) 
		{
			if(periodlist == null)
				return null;
			
			ChenJiaoZhanBiInGivenPeriod curcjlrecord = this.getSpecficRecord (requireddate,0);
			if( curcjlrecord == null) 
				return null;
			
			//判断上级板块(大盘或者板块)是否缩量,所以了没有比较的意义，直接返回-100；
			String nodept = super.getNodeperiodtype();
			DaPan dapan = (DaPan)getRoot();
			Double dpcjediff = dapan.getNodeXPeroidData(nodept).getChengJiaoErDifferenceWithLastPeriod(requireddate);
			if( dpcjediff<0 || dpcjediff == null ) //大盘缩量，
				return -100.0;
			
			int index = -1;
			while ( ((DaPan)getRoot()).isDaPanXiuShi(requireddate, index ,super.getNodeperiodtype()) ) { //上周可能大盘修饰
				index --;
			}
			
			ChenJiaoZhanBiInGivenPeriod lastcjlrecord = this.getSpecficRecord (requireddate,index);
			if(lastcjlrecord == null) { //休市前还是空，说明是新股或者是停复牌
				Double curggcje = curcjlrecord.getMyOwnChengJiaoEr(); //新股停复牌所有成交量都应该计算入
				return curggcje/dpcjediff;
			}
			
			Double curcje = curcjlrecord.getMyOwnChengJiaoEr();
			Double lastcje = lastcjlrecord.getMyOwnChengJiaoEr();
			Double cjechange = curcje - lastcje; //个股成交量的变化
			
			return cjechange/dpcjediff;
		}

//		@Override
//		public ChenJiaoZhanBiInGivenPeriod getNodeFengXiResultForSpecificDate(LocalDate requireddate)
//		{
//			if(periodlist == null)
//				return null;
//			
//			ChenJiaoZhanBiInGivenPeriod curcjlrecord = this.getSpecficRecord (requireddate,0);
//			if( curcjlrecord == null) 
//				return null;
//			
//			this.getChengJiaoErDifferenceWithLastPeriod(requireddate);
//			this.getChenJiaoErChangeGrowthRateOfSuperBanKuai(requireddate);
//			this.getChenJiaoErZhanBiGrowthRateOfSuperBanKuai(requireddate);
//			this.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(requireddate);
//			if(periodlist == null)
//				return null;
//			
////			NodeFengXiData nodefx = new NodeFengXiData (super.getMyOwnCode(),requireddate);
//			
//			//计算成交额变化贡献率，个体增量占板块增量的百分比
//			//检查板块的成交量是增加，缩量没有比较意义则直接返回
//			NodeXPeriodData myupbankuaidata = myupbankuai.getNodeXPeroidData(super.getNodeperiodtype());
//			Double bkcjediff = myupbankuaidata.getChengJiaoErDifferenceOfPeriod (requireddate); 
//			
//			Integer index = super.getRequiredRecordsPostion (requireddate);
//			if( index != null) {
//				ChenJiaoZhanBiInGivenPeriod curcjlrecord = super.periodlist.get(index); //自己当前周成交量
//				
//				if( getChengJiaoErDifferenceOfPeriod(requireddate) == null && super.periodlist.get(index) != null ) {
////					logger.debug(this.getMyOwnCode() + this.getMyOwnName() + "可能是一个新个股或板块");
//					//计算成交额变化贡献率，个体增量占板块增量的百分比
//					if( bkcjediff == null || bkcjediff <0)
//						curcjlrecord.setGgbkcjegrowthzhanbi(-100.0);
//					else {
//						Double curggcje = curcjlrecord.getMyOwnChengJiaoEr(); //停牌后所有成交量都应该计算入
//						curcjlrecord.setGgbkcjegrowthzhanbi( curggcje/bkcjediff );
//					}
//					//计算给定周的成交额和板块的占比是多少周期内的最大占比
//					curcjlrecord.setGgbkzhanbimaxweek(0);
//					// 计算给定周的成交额和板块占比增速
//					curcjlrecord.setGgbkzhanbigrowthrate(100.0);
//					//计算给定轴的成交额和大盘的占比是多少周期内的最大占比
//					curcjlrecord.setGgdpzhanbimaxweek(0);
//					curcjlrecord.setGgdpzhanbigrowthrate (100.0); //和大盘占比的增速
//					//成交额是多少周最大
//					curcjlrecord.setGgbkcjemaxweek(0);
//					
//					return curcjlrecord;
//				} else if(this.isFuPaiAfterTingPai (requireddate)) { //说明是停牌的复牌
//					//计算成交额变化贡献率，个体增量占板块增量的百分比
//					Double curggcje = curcjlrecord.getMyOwnChengJiaoEr();//停牌后所有成交量都应该计算入
//					if( bkcjediff <0 || bkcjediff == null)
//						curcjlrecord.setGgbkcjegrowthzhanbi(-100.0);
//					else
//						curcjlrecord.setGgbkcjegrowthzhanbi(curggcje/bkcjediff);
//					//计算给定周的成交额和板块的占比是多少周期内的最大占比
//					curcjlrecord.setGgbkzhanbimaxweek(0);
//					// 计算给定周的成交额和板块占比增速
//					curcjlrecord.setGgbkzhanbigrowthrate(100.0);
//					//计算给定轴的成交额和大盘的占比是多少周期内的最大占比
//					curcjlrecord.setGgdpzhanbimaxweek(0);
//					curcjlrecord.setGgdpzhanbigrowthrate (100.0); //和大盘占比的增速
//					//成交额是多少周最大
//					curcjlrecord.setGgbkcjemaxweek(0);
//
//					
//					return curcjlrecord;
//				} else {
//					//计算成交额变化贡献率，个体增量占板块增量的百分比
////					Double curggcje = curcjlrecord.getMyOwnChengJiaoEr();
////					ChenJiaoZhanBiInGivenPeriod lastcjlrecord = super.wkcjeperiodlist.get(index-1); //自己上周成交量
////					Double lastggcje = lastcjlrecord.getMyOwnChengJiaoEr();
////					Double gegucjechange = curggcje - lastggcje; //个股成交量的变化
//					
//					Double gegucjechange = getChengJiaoErDifferenceOfPeriod(requireddate);
//					if( bkcjediff <0 || bkcjediff == null)
//						curcjlrecord.setGgbkcjegrowthzhanbi(-100.0);
//					else
//						curcjlrecord.setGgbkcjegrowthzhanbi( gegucjechange/bkcjediff );
//					
//					//计算给定周的成交额和板块的占比是多少周期内的最大占比
//					Double curzhanbiratio = curcjlrecord.getCjeZhanBi();
//					Integer maxweek = 0;
//					for(int i = index -1;i>=0;i--) {
//						ChenJiaoZhanBiInGivenPeriod lastcjlrecord = periodlist.get(i );
//						Double lastzhanbiratio = lastcjlrecord.getCjeZhanBi();
//						LocalDate lastdate = lastcjlrecord.getRecordsDayofEndofWeek();
//
//						if(curzhanbiratio > lastzhanbiratio)
//								maxweek ++;
//						else
//							break;
//						
//						if( this.isFuPaiAfterTingPai (lastdate) ) //
//							break;
//					}
//					curcjlrecord.setGgbkzhanbimaxweek(maxweek);
//					
//					//成交额是多少周最大
//					Double curcje = curcjlrecord.getMyOwnChengJiaoEr();
//					maxweek = 0;
//					for(int i = index -1;i>=0;i--) {
//						ChenJiaoZhanBiInGivenPeriod lastcjlrecord = periodlist.get(i );
//						Double lastcje = lastcjlrecord.getMyOwnChengJiaoEr();
//						LocalDate lastdate = lastcjlrecord.getRecordsDayofEndofWeek();
//
//						if(curcje > lastcje)
//								maxweek ++;
//						else
//							break;
//							
//						if( this.isFuPaiAfterTingPai (lastdate) ) //
//								break;
//					}
//					curcjlrecord.setGgbkcjemaxweek(maxweek);
//
//					// 计算给定周的成交额和板块占比增速
//					ChenJiaoZhanBiInGivenPeriod lastcjlrecord = periodlist.get(index -1 );				
//					Double lastzhanbiratio = lastcjlrecord.getCjeZhanBi();
//					Double zhanbigrowthrate = (curzhanbiratio - lastzhanbiratio)/lastzhanbiratio;
//
//					curcjlrecord.setGgbkzhanbigrowthrate( zhanbigrowthrate);
//					
//					//计算给定轴的成交额和大盘的占比是多少周期内的最大占比
//					ChenJiaoZhanBiInGivenPeriod bkcjlrecord = myupbankuaidata.getSpecficRecord(requireddate,0);//通过所属板块来得到大盘成交量
//					Double curdpzhanbiratio = curcjlrecord.getMyOwnChengJiaoEr() /  bkcjlrecord.getUpLevelChengJiaoEr(); //个股成交量和大盘成交量当周占比
//					Integer dpmaxweek = 0;
//					for(int i = index -1;i>=0;i--) {
//						lastcjlrecord = periodlist.get(i);
//						LocalDate lastdate = lastcjlrecord.getRecordsDayofEndofWeek();
//						
//						ChenJiaoZhanBiInGivenPeriod lastbkcjlrecord = myupbankuaidata.getSpecficRecord(lastdate,0);
//						Double lastratio = lastcjlrecord.getMyOwnChengJiaoEr() / lastbkcjlrecord.getUpLevelChengJiaoEr();
//						
//						if(i == (index -1) ) { //在这里顺便计算一下和大盘占比的增速
//							Double dpzhanbigrowthrate = (curdpzhanbiratio - lastratio)/lastratio;
//							curcjlrecord.setGgdpzhanbigrowthrate(dpzhanbigrowthrate);
//						}
//						
//						if(curdpzhanbiratio > lastratio)
//							dpmaxweek ++;
//						else
//							break;
//						
//						if(this.isFuPaiAfterTingPai (lastdate) ) //查到连续周到头为止
//							break;
//					}
//					curcjlrecord.setGgdpzhanbimaxweek(dpmaxweek);
//					
//					//计算给定轴的成交额和大盘的占比
//					curcjlrecord.setGgdpzhanbi(curdpzhanbiratio);
//					
//					return curcjlrecord;
//					
//				}
//			}
//			return null;
//		}
	}
}

