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
//	public  void setParseFileStockSet (HashSet<String> parsefilestockset2)
//	 {
//	    	if(super.parsefilestockset == null) {
//	    		this.parsefilestockset = new HashSet<String> ();
//	    		this.parsefilestockset = parsefilestockset2;
//	    	} else
//	    		this.parsefilestockset = parsefilestockset2;
//	 }
	/*
	 * 
	 */
	public void setMyUpBanKuai (BanKuai bankuai)
	{
		this.myupbankuai = bankuai;
	}

	/*
	 * 判断本周是停牌后复牌
	 */
	protected Boolean checkIsFuPaiAfterTingPai (LocalDate requireddate )
	{
		Integer index = super.getRequiredRecordsPostion (requireddate);
		if( index != null) {
			ChenJiaoZhanBiInGivenPeriod curcjlrecord = cjeperiodlist.get(index);
			ChenJiaoZhanBiInGivenPeriod lastcjlrecord = null;
			try {
				 lastcjlrecord = cjeperiodlist.get(index -1 );
			} catch (java.lang.ArrayIndexOutOfBoundsException e) { //到了记录的第一条，怎么办
				return true;
			}
			
			LocalDate curweeknum = curcjlrecord.getRecordsDayofEndofWeek();
			LocalDate lastweeknum = lastcjlrecord.getRecordsDayofEndofWeek();
			
			long diffwknum = Math.abs(ChronoUnit.WEEKS.between (curweeknum,lastweeknum));
			
			if(diffwknum == 1) //差一周，正常交易
				return false;
			
			int alldapanxiushi = 0;
			for(int i=1;i<diffwknum;i++) {
				LocalDate dapanlastweekdate = curweeknum.with(DayOfWeek.MONDAY).minus(i,ChronoUnit.WEEKS);
				if( this.myupbankuai.isThisWeekDaPanXiuShi(dapanlastweekdate) )
					alldapanxiushi ++;
			}
			if(alldapanxiushi == diffwknum-1)
				return false;
			else 
				return true;
			
			 
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
//						System.out.println(this.getMyOwnCode() + this.getMyOwnName() + requireddate.toString() + "可能是一个新个股或板块");
						return 100.0;
				}
				

//				LocalDate curweeknum = curcjlrecord.getRecordsDayofEndofWeek();
//				LocalDate lastweeknum = lastcjlrecord.getRecordsDayofEndofWeek();
				
//				long diffwknum = Math.abs(ChronoUnit.WEEKS.between (curweeknum,lastweeknum));
//				if(diffwknum >1) {
//					if(this.checkIsFuPaiAfterTingPai (requireddate)) { //说明是停牌的复牌
//						return 100.0;
//					} 
//				}
				
				if(this.checkIsFuPaiAfterTingPai (requireddate)) { //说明是停牌的复牌
					return 100.0;
				}
				
				ChenJiaoZhanBiInGivenPeriod curcjlrecord = cjeperiodlist.get(index);
				ChenJiaoZhanBiInGivenPeriod lastcjlrecord = cjeperiodlist.get(index -1 );				
				Double curzhanbiratio = curcjlrecord.getCjlZhanBi();
				Double lastzhanbiratio = lastcjlrecord.getCjlZhanBi();
				Double zhanbigrowthrate = (curzhanbiratio - lastzhanbiratio)/lastzhanbiratio;
//				System.out.println(this.getMyOwnCode() + this.getMyOwnName() + requireddate.toString() + "占比增速为" + zhanbigrowthrate.toString());
				return zhanbigrowthrate;
		}
		
		System.out.println(this.getMyOwnCode() + this.getMyOwnName() + requireddate.toString() + "占比增速为null");
		return null;
	}
	/*
	 * 计算给定周的成交额和板块占比是多少周期内的最大占比
	 */
	public Integer getChenJiaoLiangZhanBiMaxWeekForAGivenPeriod (LocalDate requireddate)
	{
		if(cjeperiodlist == null)
			return null;

		int maxweek = 0;
		Integer index = super.getRequiredRecordsPostion (requireddate);
		if( index != null) {
			if( super.getChengJiaoErDifferenceOfLastWeek(requireddate) == null && super.cjeperiodlist.get(index) != null ) {
//				System.out.println(this.getMyOwnCode() + this.getMyOwnName() + "可能是一个新个股或板块");
				return 0;
			}
			
			if(this.checkIsFuPaiAfterTingPai (requireddate)) { //说明是停牌的复牌
				return 0;
			} else {

				ChenJiaoZhanBiInGivenPeriod curcjlrecord = cjeperiodlist.get(index);
//				ChenJiaoZhanBiInGivenPeriod lastcjlrecord =  null;
					
				Double curzhanbiratio = curcjlrecord.getCjlZhanBi();
				for(int i = index -1;i>=0;i--) {
					ChenJiaoZhanBiInGivenPeriod lastcjlrecord = cjeperiodlist.get(i );
					Double lastzhanbiratio = lastcjlrecord.getCjlZhanBi();
					LocalDate lastdate = lastcjlrecord.getRecordsDayofEndofWeek();
					
					if(curzhanbiratio > lastzhanbiratio)
							maxweek ++;
					else
						break;
					
					if( this.checkIsFuPaiAfterTingPai (lastdate) )
						break;
				}
			}
		}
		
		return maxweek;
	}
	/*
	 * 计算给定轴的成交额和大盘的占比是多少周期内的最大占比
	 */
	public Integer getChenJiaoLiangDaPanZhanBiMaxWeekForAGivenPeriod (LocalDate requireddate)
	{
		if(cjeperiodlist == null)
			return null;

		int maxweek = 0;
		Integer index = super.getRequiredRecordsPostion (requireddate);
		if( index != null) {
			if( super.getChengJiaoErDifferenceOfLastWeek(requireddate) == null && super.cjeperiodlist.get(index) != null ) {
//				System.out.println(this.getMyOwnCode() + this.getMyOwnName() + "可能是一个新个股或板块");
				return 0;
			}
			
			if(this.checkIsFuPaiAfterTingPai (requireddate)) { //说明是停牌的复牌
				return 0;
			} else {
				ChenJiaoZhanBiInGivenPeriod curcjlrecord = cjeperiodlist.get(index);
				ChenJiaoZhanBiInGivenPeriod bkcjlrecord = this.myupbankuai.getSpecficChenJiaoErRecord(requireddate);
				
				Double curzhanbiratio = curcjlrecord.getMyOwnChengJiaoEr() /  bkcjlrecord.getUpLevelChengJiaoEr(); //个股成交量和大盘成交量
				for(int i = index -1;i>=0;i--) {
					ChenJiaoZhanBiInGivenPeriod lastcjlrecord = cjeperiodlist.get(i);
					LocalDate lastdate = lastcjlrecord.getRecordsDayofEndofWeek();
					
					ChenJiaoZhanBiInGivenPeriod lastbkcjlrecord = this.myupbankuai.getSpecficChenJiaoErRecord(lastdate);
					Double lastratio = lastcjlrecord.getMyOwnChengJiaoEr() / lastbkcjlrecord.getUpLevelChengJiaoEr();
					
					if(curzhanbiratio > lastratio)
						maxweek ++;
					else
						break;
					
					if(this.checkIsFuPaiAfterTingPai (lastdate) )
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
		if(   bkcjediff == null || bkcjediff <0)
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
	/*
	 * 计算个股和大盘/板块成交量的各个数据
	 */
	public ChenJiaoZhanBiInGivenPeriod getNodeFengXiResultForSpecificDate (LocalDate requireddate)
	{
		if(cjeperiodlist == null)
			return null;
		
//		NodeFengXiData nodefx = new NodeFengXiData (super.getMyOwnCode(),requireddate);
		
		//计算成交额变化贡献率，个体增量占板块增量的百分比
		//检查板块的成交量是增加，缩量没有比较意义则直接返回
		Double bkcjediff = this.myupbankuai.getChengJiaoErDifferenceOfLastWeek (requireddate); 
		
		
		Integer index = super.getRequiredRecordsPostion (requireddate);
		if( index != null) {
			ChenJiaoZhanBiInGivenPeriod curcjlrecord = super.cjeperiodlist.get(index); //自己当前周成交量
			
			if( super.getChengJiaoErDifferenceOfLastWeek(requireddate) == null && super.cjeperiodlist.get(index) != null ) {
//				System.out.println(this.getMyOwnCode() + this.getMyOwnName() + "可能是一个新个股或板块");
				//计算成交额变化贡献率，个体增量占板块增量的百分比
				if( bkcjediff == null || bkcjediff <0)
					curcjlrecord.setGgbkcjegrowthzhanbi(-100.0);
				else {
					Double curggcje = curcjlrecord.getMyOwnChengJiaoEr(); //停牌后所有成交量都应该计算入
					curcjlrecord.setGgbkcjegrowthzhanbi( curggcje/bkcjediff );
				}
				//计算给定周的成交额和板块的占比是多少周期内的最大占比
				curcjlrecord.setGgbkzhanbimaxweek(0);
				// 计算给定周的成交额和板块占比增速
				curcjlrecord.setGgbkzhanbigrowthrate(100.0);
				//计算给定轴的成交额和大盘的占比是多少周期内的最大占比
				curcjlrecord.setGgdpzhanbimaxweek(0);
				//成交额是多少周最大
				curcjlrecord.setGgbkcjemaxweek(0);
				
				return curcjlrecord;
			} else if(this.checkIsFuPaiAfterTingPai (requireddate)) { //说明是停牌的复牌
				//计算成交额变化贡献率，个体增量占板块增量的百分比
				Double curggcje = curcjlrecord.getMyOwnChengJiaoEr();//停牌后所有成交量都应该计算入
				if( bkcjediff <0 || bkcjediff == null)
					curcjlrecord.setGgbkcjegrowthzhanbi(-100.0);
				else
					curcjlrecord.setGgbkcjegrowthzhanbi(curggcje/bkcjediff);
				//计算给定周的成交额和板块的占比是多少周期内的最大占比
				curcjlrecord.setGgbkzhanbimaxweek(0);
				// 计算给定周的成交额和板块占比增速
				curcjlrecord.setGgbkzhanbigrowthrate(100.0);
				//计算给定轴的成交额和大盘的占比是多少周期内的最大占比
				curcjlrecord.setGgdpzhanbimaxweek(0);
				//成交额是多少周最大
				curcjlrecord.setGgbkcjemaxweek(0);

				
				return curcjlrecord;
			} else {
				//计算成交额变化贡献率，个体增量占板块增量的百分比
//				Double curggcje = curcjlrecord.getMyOwnChengJiaoEr();
//				ChenJiaoZhanBiInGivenPeriod lastcjlrecord = super.cjeperiodlist.get(index-1); //自己上周成交量
//				Double lastggcje = lastcjlrecord.getMyOwnChengJiaoEr();
//				Double gegucjechange = curggcje - lastggcje; //个股成交量的变化
				
				Double gegucjechange = super.getChengJiaoErDifferenceOfLastWeek(requireddate);
				if( bkcjediff <0 || bkcjediff == null)
					curcjlrecord.setGgbkcjegrowthzhanbi(-100.0);
				else
					curcjlrecord.setGgbkcjegrowthzhanbi( gegucjechange/bkcjediff );
				
				//计算给定周的成交额和板块的占比是多少周期内的最大占比
				Double curzhanbiratio = curcjlrecord.getCjlZhanBi();
				Integer maxweek = 0;
				for(int i = index -1;i>=0;i--) {
					ChenJiaoZhanBiInGivenPeriod lastcjlrecord = cjeperiodlist.get(i );
					Double lastzhanbiratio = lastcjlrecord.getCjlZhanBi();
					LocalDate lastdate = lastcjlrecord.getRecordsDayofEndofWeek();

					if(curzhanbiratio > lastzhanbiratio)
							maxweek ++;
					else
						break;
					
					if( this.checkIsFuPaiAfterTingPai (lastdate) ) //
						break;
				}
				curcjlrecord.setGgbkzhanbimaxweek(maxweek);
				
				//成交额是多少周最大
				Double curcje = curcjlrecord.getMyOwnChengJiaoEr();
				maxweek = 0;
				for(int i = index -1;i>=0;i--) {
					ChenJiaoZhanBiInGivenPeriod lastcjlrecord = cjeperiodlist.get(i );
					Double lastcje = lastcjlrecord.getMyOwnChengJiaoEr();
					LocalDate lastdate = lastcjlrecord.getRecordsDayofEndofWeek();

					if(curcje > lastcje)
							maxweek ++;
					else
						break;
						
					if( this.checkIsFuPaiAfterTingPai (lastdate) ) //
							break;
				}
				curcjlrecord.setGgbkcjemaxweek(maxweek);

				// 计算给定周的成交额和板块占比增速
				ChenJiaoZhanBiInGivenPeriod lastcjlrecord = cjeperiodlist.get(index -1 );				
				Double lastzhanbiratio = lastcjlrecord.getCjlZhanBi();
				Double zhanbigrowthrate = (curzhanbiratio - lastzhanbiratio)/lastzhanbiratio;

				curcjlrecord.setGgbkzhanbigrowthrate( zhanbigrowthrate);
				
				//计算给定轴的成交额和大盘的占比是多少周期内的最大占比
				ChenJiaoZhanBiInGivenPeriod bkcjlrecord = this.myupbankuai.getSpecficChenJiaoErRecord(requireddate);//通过所属板块来得到大盘成交量
				Double curdpzhanbiratio = curcjlrecord.getMyOwnChengJiaoEr() /  bkcjlrecord.getUpLevelChengJiaoEr(); //个股成交量和大盘成交量当周占比
				Integer dpmaxweek = 0;
				for(int i = index -1;i>=0;i--) {
					lastcjlrecord = cjeperiodlist.get(i);
					LocalDate lastdate = lastcjlrecord.getRecordsDayofEndofWeek();
					
					ChenJiaoZhanBiInGivenPeriod lastbkcjlrecord = this.myupbankuai.getSpecficChenJiaoErRecord(lastdate);
					Double lastratio = lastcjlrecord.getMyOwnChengJiaoEr() / lastbkcjlrecord.getUpLevelChengJiaoEr();
					
					if(curdpzhanbiratio > lastratio)
						dpmaxweek ++;
					else
						break;
					
					if(this.checkIsFuPaiAfterTingPai (lastdate) ) //查到连续周到头为止
						break;
				}
				curcjlrecord.setGgdpzhanbimaxweek(dpmaxweek);
				
				//计算给定轴的成交额和大盘的占比
				curcjlrecord.setGgdpzhanbi(curdpzhanbiratio);
				
				return curcjlrecord;
				
			}
		}
		
		return null;
	}
	

}

