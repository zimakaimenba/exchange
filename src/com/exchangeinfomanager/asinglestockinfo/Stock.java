package com.exchangeinfomanager.asinglestockinfo;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountInfoBasic;
import com.exchangeinfomanager.bankuaifengxi.ChenJiaoZhanBiInGivenPeriod;
import com.google.common.base.Strings;

public class Stock extends BkChanYeLianTreeNode {

	public Stock(String myowncode1,String name)
	{
		super(myowncode1,name);
		nodetype = BanKuaiAndStockBasic.BKGEGU;
	}
	
	private String suoshujiaoyisuo;
//	private boolean aNewDtockCodeIndicate = false;
//	private Date gainiantishidate;
//	private String gainiantishi;
//	private Date quanshangpingjidate;
//	private String quanshangpingji;
//	private Date fumianxiaoxidate;
//	private String fumianxiaoxi;
//	private String zhengxiangguan;
//	private String fuxiangguan;
//	private String jingZhengDuiShou;
//	private String keHuCustom;
	private String checklistXml;
	
	private Object[][] zdgzmrmcykRecords ;

//	private Multimap<String> chiCangAccountNameList; //持有该股票的所有账户的名字
	private HashMap<String,AccountInfoBasic> chiCangAccounts; //持有该股票的所有账户<账户，账户信息>
	private HashMap<String,String> suoShuSysBanKuai; //所属通达信系统板块 <板块code,板块名字>
	private  HashMap<String,Integer> sysBanKuaiWeight; //所属通达信系统板块权重
		
	private HashSet<String> suoShuZdyBanKuai; //所属自定义板块
	
//	private ArrayList<ChenJiaoZhanBiInGivenPeriod> myuplevelperiodlist; //所属板块成交记录
	private BanKuai myupbankuai;
			
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
	/**
	 * @return the zdgzmrmcykRecords
	 */
	public Object[][] getZdgzMrmcZdgzYingKuiRecords() {
		return zdgzmrmcykRecords;
	}
	/**
	 * @param zdgzmrmcykRecords the zdgzmrmcykRecords to set
	 */
	public void setZdgzmrmcykRecords(Object[][] zdgzmrmcykRecords) {
		this.zdgzmrmcykRecords = zdgzmrmcykRecords;
	}

	public boolean isInTdxBanKuai (String tdxbk)
	{
		if(this.suoShuSysBanKuai.size() == 0) //size为0，个股不可能完全没有板块，为0说明和数据库连接有问题，那都返回true
			return true;
		if(this.suoShuSysBanKuai.containsKey(tdxbk))
			return true;
		else 
			return false;
	}
	/**
	 * @return the suoShuBanKuai
	 */
	public HashMap<String,String> getGeGuSuoShuTDXSysBanKuaiList() {
		return suoShuSysBanKuai;
	}

	/**
	 * @param tmpsysbk the suoShuBanKuai to set
	 */
	public void setGeGuSuoShuTDXSysBanKuaiList(HashMap<String,String> tmpsysbk) {
		this.suoShuSysBanKuai = tmpsysbk;
	}
	/**
	 * @return the sysBanKuaiWeight
	 */
	public HashMap<String, Integer> getGeGuSuoShuBanKuaiWeight() {
		return sysBanKuaiWeight;
	}
	/**
	 * @param sysBanKuaiWeight the sysBanKuaiWeight to set
	 */
	public void setGeGuSuoShuBanKuaiWeight(HashMap<String, Integer> sysBanKuaiWeight) {
		this.sysBanKuaiWeight = sysBanKuaiWeight;
	}
	
	/**
	 * @return the suoShuBanKuai
	 */
	public HashSet<String> getSuoShuTDXZdyBanKuai() {
		return suoShuZdyBanKuai;
	}

	/**
	 * @param tmpbk the suoShuBanKuai to set
	 */
	public void setSuoShuTDXZdyBanKuai(HashSet<String> tmpbk) {
		this.suoShuZdyBanKuai = tmpbk;
	}
	
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
	public  void setParseFileStockSet (HashSet<String> parsefilestockset2)
	 {
	    	if(super.parsefilestockset == null) {
	    		this.parsefilestockset = new HashSet<String> ();
	    		this.parsefilestockset = parsefilestockset2;
	    	}
	    	else
	    		this.parsefilestockset = parsefilestockset2;
	 }
	/*
	 * 
	 */
	public void setMyUpBanKuai (BanKuai bankuai)
	{
		this.myupbankuai = bankuai;
	}

	/*
	 * 计算板块本周和上周成交额增长率百分比
	 */
//	public Double getChenJiaoErGrowthRateForAGivenPeriod (LocalDate requireddate)
//	{
//		if(cjeperiodlist == null)
//			return null;
//		
//		Integer index = this.getRequiredRecordsPostion (this.cjeperiodlist,requireddate);
//		if( index != null) {
//			ChenJiaoZhanBiInGivenPeriod curcjlrecord = cjeperiodlist.get(index); //自己当前周成交量
//			Double curupbkcje = curcjlrecord.getUpLevelChengJiaoEr ();
//			LocalDate curweeknum = curcjlrecord.getRecordsDayofEndofWeek();
//			
//			//判断上级板块(大盘或者板块)是否缩量,所以了没有比较的意义，直接返回-100；
//			LocalDate tplastdate = curweeknum.minus(1,ChronoUnit.WEEKS);
//			ChenJiaoZhanBiInGivenPeriod myuplevelrecord = myupbankuai.getSpecficChenJiaoErRecord(tplastdate); //从上级板块中找出上周的成交量
//			Double tingpailastupbkcje;
//			if(myuplevelrecord != null) {
//				tingpailastupbkcje = myuplevelrecord.getMyOwnChengJiaoEr(); //板块或大盘上周的成交量，而不是停牌那周的成交量
//			} else
//				return -100.0;
//			
//			if(curupbkcje < tingpailastupbkcje) //大盘缩量，
//				return -100.0;
//			
//			ChenJiaoZhanBiInGivenPeriod lastcjlrecord = null;
//			try {
//				 lastcjlrecord = cjeperiodlist.get(index -1 );
//			} catch ( java.lang.ArrayIndexOutOfBoundsException e) {
//				return 10000.0;
//			}
//
//			LocalDate lastweeknum = lastcjlrecord.getRecordsDayofEndofWeek() ;
//			long dayss = Math.abs(java.time.temporal.ChronoUnit.DAYS.between (curweeknum,lastweeknum));
//			if(dayss > 7) { //说明是停牌的复牌  
//				return 10000.0;
//			} else { //上周没有停牌
//				Double curggcje = curcjlrecord.getMyOwnChengJiaoEr();
//				Double lastggcje = lastcjlrecord.getMyOwnChengJiaoEr();
//				
//				double cjegrowthrate = (curggcje - lastggcje)/lastggcje;
//				return cjegrowthrate;
//			}
//
//		}
//		
//		return 0.0;
//		
//	}
	/*
	 * 判断本周是停牌后复牌
	 */
	protected Boolean checkIsFuPaiAfterTingPai (LocalDate requireddate )
	{
		Integer index = super.getRequiredRecordsPostion (requireddate);
		if( index != null) {
			ChenJiaoZhanBiInGivenPeriod curcjlrecord = cjeperiodlist.get(index);
			ChenJiaoZhanBiInGivenPeriod lastcjlrecord = cjeperiodlist.get(index -1 );
			LocalDate curweeknum = curcjlrecord.getRecordsDayofEndofWeek();
			LocalDate lastweeknum = lastcjlrecord.getRecordsDayofEndofWeek();
			
			long diffwknum = ChronoUnit.WEEKS.between (curweeknum,lastweeknum);
			int alldapanxiushi = 0;
			for(int i=1;i<diffwknum;i++) {
				LocalDate dapanlastweekdate = curweeknum.with(DayOfWeek.MONDAY).minus(i,ChronoUnit.WEEKS);
				if( this.myupbankuai.isThisWeekDaPanXiuShi(dapanlastweekdate) )
					alldapanxiushi ++;
			}
			if(alldapanxiushi == diffwknum)
				return true;
			else 
				return false;
			
//			if(diffwknum > 2 ) { //说明是停牌的复牌  ，大盘休市不可能超过1周
//				return true;	
//			} else if(diffwknum == 2) { //可能是大盘休市造成的，
//				LocalDate dapanlastweekdate = curweeknum.with(DayOfWeek.MONDAY).minus(1,ChronoUnit.WEEKS);
//				if( this.myupbankuai.isThisWeekDaPanXiuShi(dapanlastweekdate) )
//					return false;
//				else 
//					return true;
//			} else
//				return false;
			
			
		}
		
		return null;
	}
	/*
	 * 计算给定周的成交额占比增速
	 */
	public Double getChenJiaoLiangZhanBiGrowthRateForAGivenPeriod (LocalDate requireddate)
	{
		if(cjeperiodlist == null)
			return null;
		
		Integer index = super.getRequiredRecordsPostion (requireddate);
		if( index != null) {
				if( super.getChengJiaoErDifferenceOfLastWeek(requireddate) == null && super.cjeperiodlist.get(index) != null ) {
						System.out.println(this.getMyOwnCode() + this.getMyOwnName() + "可能是一个新个股或板块");
						return 100.0;
				}
			
				if(this.checkIsFuPaiAfterTingPai (requireddate)) { //说明是停牌的复牌
					return 100.0;
				} else {
					ChenJiaoZhanBiInGivenPeriod curcjlrecord = cjeperiodlist.get(index);
					ChenJiaoZhanBiInGivenPeriod lastcjlrecord = cjeperiodlist.get(index -1 );
						
					Double curzhanbiratio = curcjlrecord.getCjlZhanBi();
					Double lastzhanbiratio = lastcjlrecord.getCjlZhanBi();
					double zhanbigrowthrate = (curzhanbiratio - lastzhanbiratio)/lastzhanbiratio;
					return zhanbigrowthrate;
				}
		}
		
		return null;
	}
	/*
	 * 计算给定周的成交额占比是多少周期内的最大占比
	 */
	public Integer getChenJiaoLiangZhanBiMaxWeekForAGivenPeriod (LocalDate requireddate)
	{
		if(cjeperiodlist == null)
			return null;

		int maxweek = 0;
		Integer index = super.getRequiredRecordsPostion (requireddate);
		if( index != null) {
			if( super.getChengJiaoErDifferenceOfLastWeek(requireddate) == null && super.cjeperiodlist.get(index) != null ) {
				System.out.println(this.getMyOwnCode() + this.getMyOwnName() + "可能是一个新个股或板块");
				return 0;
			}
			
			if(this.checkIsFuPaiAfterTingPai (requireddate)) { //说明是停牌的复牌
				return 0;
			} else {

				ChenJiaoZhanBiInGivenPeriod curcjlrecord = cjeperiodlist.get(index);
				ChenJiaoZhanBiInGivenPeriod lastcjlrecord =  null;
					
				Double curzhanbiratio = curcjlrecord.getCjlZhanBi();
				for(int i = index -1;i>=0;i--) {
						lastcjlrecord = cjeperiodlist.get(i );
						Double lastzhanbiratio = lastcjlrecord.getCjlZhanBi();
						if(curzhanbiratio > lastzhanbiratio)
							maxweek ++;
						else
							break;
				}
			}
		}
		
		return maxweek;
	}
	/*
	 * 计算成交额变化贡献率，个体增量占板块增量的百分比
	 */
	public Double getChenJiaoErChangeGrowthRateForAGivenPeriod (LocalDate requireddate)
	{
		if(cjeperiodlist == null)
			return null;
		
		//检查板块的成交量是增加，缩量没有比较意义则直接返回
		Double bkcjediff = this.myupbankuai.getChengJiaoErDifferenceOfLastWeek (requireddate); 
		if( bkcjediff <0 || bkcjediff == null)
			return -100.0;
		
		Integer index = super.getRequiredRecordsPostion (requireddate);
		if( index != null) {
				ChenJiaoZhanBiInGivenPeriod curcjlrecord = super.cjeperiodlist.get(index); //自己当前周成交量
				
				if( super.getChengJiaoErDifferenceOfLastWeek(requireddate) == null && super.cjeperiodlist.get(index) != null ) {
//					System.out.println(this.getMyOwnCode() + this.getMyOwnName() + "可能是个新板块" );
					Double curggcje = curcjlrecord.getMyOwnChengJiaoEr(); //停牌后所有成交量都应该计算入

					return curggcje/bkcjediff;
				}
				
				if(this.checkIsFuPaiAfterTingPai (requireddate)) { //说明是停牌的复牌  
					Double curggcje = curcjlrecord.getMyOwnChengJiaoEr();//停牌后所有成交量都应该计算入

					return curggcje/bkcjediff;

				} else { //上周没有停牌
					Double curggcje = curcjlrecord.getMyOwnChengJiaoEr();
					ChenJiaoZhanBiInGivenPeriod lastcjlrecord = super.cjeperiodlist.get(index-1); //自己当前周成交量
					Double lastggcje = lastcjlrecord.getMyOwnChengJiaoEr();
					
					Double gegucjechange = curggcje - lastggcje; //个股成交量的变化
					
					return gegucjechange/bkcjediff;
				}
		}
		
		return  -100.0;
	}
	

}
