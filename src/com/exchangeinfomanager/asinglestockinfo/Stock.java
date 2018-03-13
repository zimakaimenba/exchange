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
	
//	private Multimap<String> chiCangAccountNameList; //���иù�Ʊ�������˻�������
	private HashMap<String,AccountInfoBasic> chiCangAccounts; //���иù�Ʊ�������˻�<�˻����˻���Ϣ>
//	private HashMap<String,StockOfBanKuai> suoShuTdxBanKuaiData; //����ͨ����ϵͳ��� <���code,�������>
//	private  HashMap<String,Integer> sysBanKuaiWeight; //����ͨ����ϵͳ���Ȩ��
	private HashSet<String> suoShuZdyBanKuai; //�����Զ�����
	private HashMap<String, String> suoShuCurSysBanKuai; //���ɵ�ǰ������ͨ���Ű��

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
			
			if(requireddate.isAfter(rstartday) && requireddate.isBefore(rendday) && !dapanxiushi) //����������ݷ�Χ����û�����ݣ��Ҵ���û�����У�˵��ͣ��
				return true;
			else
				return null; //���������ݷ�Χ�⣬��û�취�жϣ����Է���null
		}
	}
	/*
	 * �жϱ�������ͣ�ƺ���
	 */
	protected Boolean isFuPaiAfterTingPai (LocalDate requireddate,int difference,String period )
	{
		NodeXPeriodDataBasic nodexdate = this.getNodeXPeroidData(period);
		ChenJiaoZhanBiInGivenPeriod nodexcurdatedata = nodexdate.getSpecficRecord(requireddate, difference);
		if( nodexcurdatedata == null) 
			return null;
		
		int index = -1;
		while ( ((DaPan)getRoot()).isDaPanXiuShi(requireddate, index ,period) ) { //���ܿ��ܴ�������
			index --;
		}
		
		ChenJiaoZhanBiInGivenPeriod lastcjlrecord = nodexdate.getSpecficRecord (requireddate,index);
		if(lastcjlrecord == null) {//����ǰ���ǿգ�˵��Ҫô���°��Ҫôͣ�Ƶ�
			return true;
		} else 
			return false;
	}
	 
	
	/*
	 * stock ������ڲ���ֻ�������Դ��̵����ݣ�����԰���������StockOfBanKuai
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
			for(int index = -1;index >= -100000; index--) { //Ŀǰ��¼��������10000�����ڣ�����10000�㹻
				if( dapan.isDaPanXiuShi(requireddate, index ,getNodeperiodtype())  ) //���̻���������
					continue;
				
				if(isTingPai(requireddate,index,getNodeperiodtype()))
					return maxweek;
				
				ChenJiaoZhanBiInGivenPeriod lastcjlrecord = this.getSpecficRecord (requireddate,index);
				if(lastcjlrecord == null ) //���ܵ��˼�¼��ͷ���ˣ������Ǹ�����ʱ�䲻���İ��
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
		 * ���ϼ����ĳɽ���ռ���Ƕ������ڵ����ֵ
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
				if( dapan.isDaPanXiuShi(requireddate, index ,getNodeperiodtype())  ) //���̻���������
					continue;
				
				if(isTingPai(requireddate,index,getNodeperiodtype())) //����Ҳ����ͣ�ƣ�ͣ��ǰ�ľͲ�������
					return maxweek;
				
				ChenJiaoZhanBiInGivenPeriod lastcjlrecord = this.getSpecficRecord (requireddate,index);
				
				if(lastcjlrecord == null ) //���ܵ��˼�¼��ͷ���ˣ������Ǹ�����ʱ�䲻���İ��
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
			
			//�ж��ϼ����(���̻��߰��)�Ƿ�����,������û�бȽϵ����壬ֱ�ӷ���-100��
			String nodept = super.getNodeperiodtype();
			DaPan dapan = (DaPan)getRoot();
			Double dpcjediff = dapan.getNodeXPeroidData(nodept).getChengJiaoErDifferenceWithLastPeriod(requireddate);
			if( dpcjediff<0 || dpcjediff == null ) //����������
				return -100.0;
			
			int index = -1;
			while ( ((DaPan)getRoot()).isDaPanXiuShi(requireddate, index ,super.getNodeperiodtype()) ) { //���ܿ��ܴ�������
				index --;
			}
			
			ChenJiaoZhanBiInGivenPeriod lastcjlrecord = this.getSpecficRecord (requireddate,index);
			if(lastcjlrecord == null) { //����ǰ���ǿգ�˵�����¹ɻ�����ͣ����
				Double curggcje = curcjlrecord.getMyOwnChengJiaoEr(); //�¹�ͣ�������гɽ�����Ӧ�ü�����
				return curggcje/dpcjediff;
			}
			
			Double curcje = curcjlrecord.getMyOwnChengJiaoEr();
			Double lastcje = lastcjlrecord.getMyOwnChengJiaoEr();
			Double cjechange = curcje - lastcje; //���ɳɽ����ı仯
			
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
//			//����ɽ���仯�����ʣ���������ռ��������İٷֱ�
//			//�����ĳɽ��������ӣ�����û�бȽ�������ֱ�ӷ���
//			NodeXPeriodData myupbankuaidata = myupbankuai.getNodeXPeroidData(super.getNodeperiodtype());
//			Double bkcjediff = myupbankuaidata.getChengJiaoErDifferenceOfPeriod (requireddate); 
//			
//			Integer index = super.getRequiredRecordsPostion (requireddate);
//			if( index != null) {
//				ChenJiaoZhanBiInGivenPeriod curcjlrecord = super.periodlist.get(index); //�Լ���ǰ�ܳɽ���
//				
//				if( getChengJiaoErDifferenceOfPeriod(requireddate) == null && super.periodlist.get(index) != null ) {
////					logger.debug(this.getMyOwnCode() + this.getMyOwnName() + "������һ���¸��ɻ���");
//					//����ɽ���仯�����ʣ���������ռ��������İٷֱ�
//					if( bkcjediff == null || bkcjediff <0)
//						curcjlrecord.setGgbkcjegrowthzhanbi(-100.0);
//					else {
//						Double curggcje = curcjlrecord.getMyOwnChengJiaoEr(); //ͣ�ƺ����гɽ�����Ӧ�ü�����
//						curcjlrecord.setGgbkcjegrowthzhanbi( curggcje/bkcjediff );
//					}
//					//��������ܵĳɽ���Ͱ���ռ���Ƕ��������ڵ����ռ��
//					curcjlrecord.setGgbkzhanbimaxweek(0);
//					// ��������ܵĳɽ���Ͱ��ռ������
//					curcjlrecord.setGgbkzhanbigrowthrate(100.0);
//					//���������ĳɽ���ʹ��̵�ռ���Ƕ��������ڵ����ռ��
//					curcjlrecord.setGgdpzhanbimaxweek(0);
//					curcjlrecord.setGgdpzhanbigrowthrate (100.0); //�ʹ���ռ�ȵ�����
//					//�ɽ����Ƕ��������
//					curcjlrecord.setGgbkcjemaxweek(0);
//					
//					return curcjlrecord;
//				} else if(this.isFuPaiAfterTingPai (requireddate)) { //˵����ͣ�Ƶĸ���
//					//����ɽ���仯�����ʣ���������ռ��������İٷֱ�
//					Double curggcje = curcjlrecord.getMyOwnChengJiaoEr();//ͣ�ƺ����гɽ�����Ӧ�ü�����
//					if( bkcjediff <0 || bkcjediff == null)
//						curcjlrecord.setGgbkcjegrowthzhanbi(-100.0);
//					else
//						curcjlrecord.setGgbkcjegrowthzhanbi(curggcje/bkcjediff);
//					//��������ܵĳɽ���Ͱ���ռ���Ƕ��������ڵ����ռ��
//					curcjlrecord.setGgbkzhanbimaxweek(0);
//					// ��������ܵĳɽ���Ͱ��ռ������
//					curcjlrecord.setGgbkzhanbigrowthrate(100.0);
//					//���������ĳɽ���ʹ��̵�ռ���Ƕ��������ڵ����ռ��
//					curcjlrecord.setGgdpzhanbimaxweek(0);
//					curcjlrecord.setGgdpzhanbigrowthrate (100.0); //�ʹ���ռ�ȵ�����
//					//�ɽ����Ƕ��������
//					curcjlrecord.setGgbkcjemaxweek(0);
//
//					
//					return curcjlrecord;
//				} else {
//					//����ɽ���仯�����ʣ���������ռ��������İٷֱ�
////					Double curggcje = curcjlrecord.getMyOwnChengJiaoEr();
////					ChenJiaoZhanBiInGivenPeriod lastcjlrecord = super.wkcjeperiodlist.get(index-1); //�Լ����ܳɽ���
////					Double lastggcje = lastcjlrecord.getMyOwnChengJiaoEr();
////					Double gegucjechange = curggcje - lastggcje; //���ɳɽ����ı仯
//					
//					Double gegucjechange = getChengJiaoErDifferenceOfPeriod(requireddate);
//					if( bkcjediff <0 || bkcjediff == null)
//						curcjlrecord.setGgbkcjegrowthzhanbi(-100.0);
//					else
//						curcjlrecord.setGgbkcjegrowthzhanbi( gegucjechange/bkcjediff );
//					
//					//��������ܵĳɽ���Ͱ���ռ���Ƕ��������ڵ����ռ��
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
//					//�ɽ����Ƕ��������
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
//					// ��������ܵĳɽ���Ͱ��ռ������
//					ChenJiaoZhanBiInGivenPeriod lastcjlrecord = periodlist.get(index -1 );				
//					Double lastzhanbiratio = lastcjlrecord.getCjeZhanBi();
//					Double zhanbigrowthrate = (curzhanbiratio - lastzhanbiratio)/lastzhanbiratio;
//
//					curcjlrecord.setGgbkzhanbigrowthrate( zhanbigrowthrate);
//					
//					//���������ĳɽ���ʹ��̵�ռ���Ƕ��������ڵ����ռ��
//					ChenJiaoZhanBiInGivenPeriod bkcjlrecord = myupbankuaidata.getSpecficRecord(requireddate,0);//ͨ������������õ����̳ɽ���
//					Double curdpzhanbiratio = curcjlrecord.getMyOwnChengJiaoEr() /  bkcjlrecord.getUpLevelChengJiaoEr(); //���ɳɽ����ʹ��̳ɽ�������ռ��
//					Integer dpmaxweek = 0;
//					for(int i = index -1;i>=0;i--) {
//						lastcjlrecord = periodlist.get(i);
//						LocalDate lastdate = lastcjlrecord.getRecordsDayofEndofWeek();
//						
//						ChenJiaoZhanBiInGivenPeriod lastbkcjlrecord = myupbankuaidata.getSpecficRecord(lastdate,0);
//						Double lastratio = lastcjlrecord.getMyOwnChengJiaoEr() / lastbkcjlrecord.getUpLevelChengJiaoEr();
//						
//						if(i == (index -1) ) { //������˳�����һ�ºʹ���ռ�ȵ�����
//							Double dpzhanbigrowthrate = (curdpzhanbiratio - lastratio)/lastratio;
//							curcjlrecord.setGgdpzhanbigrowthrate(dpzhanbigrowthrate);
//						}
//						
//						if(curdpzhanbiratio > lastratio)
//							dpmaxweek ++;
//						else
//							break;
//						
//						if(this.isFuPaiAfterTingPai (lastdate) ) //�鵽�����ܵ�ͷΪֹ
//							break;
//					}
//					curcjlrecord.setGgdpzhanbimaxweek(dpmaxweek);
//					
//					//���������ĳɽ���ʹ��̵�ռ��
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

