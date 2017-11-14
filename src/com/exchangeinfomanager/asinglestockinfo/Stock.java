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

//	private Multimap<String> chiCangAccountNameList; //���иù�Ʊ�������˻�������
	private HashMap<String,AccountInfoBasic> chiCangAccounts; //���иù�Ʊ�������˻�<�˻����˻���Ϣ>
	private HashMap<String,String> suoShuSysBanKuai; //����ͨ����ϵͳ��� <���code,�������>
	private  HashMap<String,Integer> sysBanKuaiWeight; //����ͨ����ϵͳ���Ȩ��
		
	private HashSet<String> suoShuZdyBanKuai; //�����Զ�����
	
//	private ArrayList<ChenJiaoZhanBiInGivenPeriod> myuplevelperiodlist; //�������ɽ���¼
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
		if(this.suoShuSysBanKuai.size() == 0) //sizeΪ0�����ɲ�������ȫû�а�飬Ϊ0˵�������ݿ����������⣬�Ƕ�����true
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
	 * �����鱾�ܺ����ܳɽ��������ʰٷֱ�
	 */
//	public Double getChenJiaoErGrowthRateForAGivenPeriod (LocalDate requireddate)
//	{
//		if(cjeperiodlist == null)
//			return null;
//		
//		Integer index = this.getRequiredRecordsPostion (this.cjeperiodlist,requireddate);
//		if( index != null) {
//			ChenJiaoZhanBiInGivenPeriod curcjlrecord = cjeperiodlist.get(index); //�Լ���ǰ�ܳɽ���
//			Double curupbkcje = curcjlrecord.getUpLevelChengJiaoEr ();
//			LocalDate curweeknum = curcjlrecord.getRecordsDayofEndofWeek();
//			
//			//�ж��ϼ����(���̻��߰��)�Ƿ�����,������û�бȽϵ����壬ֱ�ӷ���-100��
//			LocalDate tplastdate = curweeknum.minus(1,ChronoUnit.WEEKS);
//			ChenJiaoZhanBiInGivenPeriod myuplevelrecord = myupbankuai.getSpecficChenJiaoErRecord(tplastdate); //���ϼ�������ҳ����ܵĳɽ���
//			Double tingpailastupbkcje;
//			if(myuplevelrecord != null) {
//				tingpailastupbkcje = myuplevelrecord.getMyOwnChengJiaoEr(); //����������ܵĳɽ�����������ͣ�����ܵĳɽ���
//			} else
//				return -100.0;
//			
//			if(curupbkcje < tingpailastupbkcje) //����������
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
//			if(dayss > 7) { //˵����ͣ�Ƶĸ���  
//				return 10000.0;
//			} else { //����û��ͣ��
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
	 * �жϱ�����ͣ�ƺ���
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
			
//			if(diffwknum > 2 ) { //˵����ͣ�Ƶĸ���  ���������в����ܳ���1��
//				return true;	
//			} else if(diffwknum == 2) { //�����Ǵ���������ɵģ�
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
	 * ��������ܵĳɽ���ռ������
	 */
	public Double getChenJiaoLiangZhanBiGrowthRateForAGivenPeriod (LocalDate requireddate)
	{
		if(cjeperiodlist == null)
			return null;
		
		Integer index = super.getRequiredRecordsPostion (requireddate);
		if( index != null) {
				if( super.getChengJiaoErDifferenceOfLastWeek(requireddate) == null && super.cjeperiodlist.get(index) != null ) {
						System.out.println(this.getMyOwnCode() + this.getMyOwnName() + "������һ���¸��ɻ���");
						return 100.0;
				}
			
				if(this.checkIsFuPaiAfterTingPai (requireddate)) { //˵����ͣ�Ƶĸ���
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
	 * ��������ܵĳɽ���ռ���Ƕ��������ڵ����ռ��
	 */
	public Integer getChenJiaoLiangZhanBiMaxWeekForAGivenPeriod (LocalDate requireddate)
	{
		if(cjeperiodlist == null)
			return null;

		int maxweek = 0;
		Integer index = super.getRequiredRecordsPostion (requireddate);
		if( index != null) {
			if( super.getChengJiaoErDifferenceOfLastWeek(requireddate) == null && super.cjeperiodlist.get(index) != null ) {
				System.out.println(this.getMyOwnCode() + this.getMyOwnName() + "������һ���¸��ɻ���");
				return 0;
			}
			
			if(this.checkIsFuPaiAfterTingPai (requireddate)) { //˵����ͣ�Ƶĸ���
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
	 * ����ɽ���仯�����ʣ���������ռ��������İٷֱ�
	 */
	public Double getChenJiaoErChangeGrowthRateForAGivenPeriod (LocalDate requireddate)
	{
		if(cjeperiodlist == null)
			return null;
		
		//�����ĳɽ��������ӣ�����û�бȽ�������ֱ�ӷ���
		Double bkcjediff = this.myupbankuai.getChengJiaoErDifferenceOfLastWeek (requireddate); 
		if( bkcjediff <0 || bkcjediff == null)
			return -100.0;
		
		Integer index = super.getRequiredRecordsPostion (requireddate);
		if( index != null) {
				ChenJiaoZhanBiInGivenPeriod curcjlrecord = super.cjeperiodlist.get(index); //�Լ���ǰ�ܳɽ���
				
				if( super.getChengJiaoErDifferenceOfLastWeek(requireddate) == null && super.cjeperiodlist.get(index) != null ) {
//					System.out.println(this.getMyOwnCode() + this.getMyOwnName() + "�����Ǹ��°��" );
					Double curggcje = curcjlrecord.getMyOwnChengJiaoEr(); //ͣ�ƺ����гɽ�����Ӧ�ü�����

					return curggcje/bkcjediff;
				}
				
				if(this.checkIsFuPaiAfterTingPai (requireddate)) { //˵����ͣ�Ƶĸ���  
					Double curggcje = curcjlrecord.getMyOwnChengJiaoEr();//ͣ�ƺ����гɽ�����Ӧ�ü�����

					return curggcje/bkcjediff;

				} else { //����û��ͣ��
					Double curggcje = curcjlrecord.getMyOwnChengJiaoEr();
					ChenJiaoZhanBiInGivenPeriod lastcjlrecord = super.cjeperiodlist.get(index-1); //�Լ���ǰ�ܳɽ���
					Double lastggcje = lastcjlrecord.getMyOwnChengJiaoEr();
					
					Double gegucjechange = curggcje - lastggcje; //���ɳɽ����ı仯
					
					return gegucjechange/bkcjediff;
				}
		}
		
		return  -100.0;
	}
	

}
