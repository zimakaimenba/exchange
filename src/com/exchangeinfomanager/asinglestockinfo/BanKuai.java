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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import com.exchangeinfomanager.bankuaifengxi.ChenJiaoZhanBiInGivenPeriod;
import com.exchangeinfomanager.commonlib.CommonUtility;

public class BanKuai extends BkChanYeLianTreeNode
{

	public BanKuai(String bkcode,String name ) 
	{
		super(bkcode,name);
		nodetype = BanKuaiAndStockBasic.TDXBK;
	}
	
	private DaPan dapan;
	private HashMap<String, Stock> allbkge;
	
	private String suoshujiaoyisuo;
	private boolean notexporttogehpi = false;

	 /**
	 * @return the tmpallbkge
	 */
	public HashMap<String, Stock> getAllBanKuaiGeGu() {
		return allbkge;
	}
	/*
	 * ֻ�����ƶ����ڵ����гɽ����ĸ���,�гɽ�����˵�������Ǹð��ĸ���
	 */
	public HashMap<String, Stock> getSpecificPeriodBanKuaiGeGu(LocalDate requireddate) {
		if(this.allbkge == null || this.allbkge.isEmpty())
			return null;
		
		LocalDate bkstart = this.getRecordsStartDate();
		LocalDate bkend = this.getRecordsEndDate();
		if(requireddate.isBefore(bkstart) || requireddate.isAfter(bkend) ) //��ǰû�и����ڵļ�¼
			return null;

		HashMap<String, Stock> result = new HashMap<String, Stock> ();
		for (Map.Entry<String, Stock> entry : allbkge.entrySet()) {  
			  Stock stock = entry.getValue();
			  ChenJiaoZhanBiInGivenPeriod records = stock.getSpecficChenJiaoErRecord(requireddate);
			  if(records != null)
				  result.put(stock.getMyOwnCode(), stock);
		}
		
		return result;
	}
	/*
	 * 
	 */
	public Stock getBanKuaiGeGu (String stockcode)
	{
		if(this.allbkge == null) 
			this.allbkge = new HashMap<String, Stock> ();
		
		try {
			Stock stock = this.allbkge.get(stockcode);
			if(stock == null) {
				stock = new Stock (stockcode, "");
				stock.setMyUpBanKuai(this); //�Ѱ��ĳɽ������룬����
				this.allbkge.put(stockcode, stock);
//				stock.setMyUpBanKuai(this); //�Ѱ��ĳɽ������룬����
				return stock;
			} else
				return stock;
		} catch ( java.lang.NullPointerException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	/*
	 * 
	 */
	public Boolean isBanKuaiGeGu (Stock stock)
	{
		if(this.allbkge == null) 
			return false;
		
		try {
			String stockcode = stock.getMyOwnCode();
			Stock tmpstock = this.allbkge.get(stockcode);
			if(tmpstock == null) 
				return false;
			 else
				return true;
		} catch ( java.lang.NullPointerException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	/*
	 * 
	 */
	public void addNewBanKuaiGeGu (Stock stock) {
		if(this.allbkge == null) 
			this.allbkge = new HashMap<String, Stock> ();
		
		if(!this.isBanKuaiGeGu (stock) ) {
			String stockcode = stock.getMyOwnCode();
			stock.setMyUpBanKuai(this); //�Ѱ��ĳɽ������룬����
			this.allbkge.put(stockcode, stock);
		}
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
	 * GEPHI part
	 */
	public void setNotExportToGephi ()
	 {
		 this.notexporttogehpi = true;
	 }
	 public boolean shouldNotExportToGephi ()
	 {
		 return this.notexporttogehpi;
	 }
	 /*
	  * 
	  */
	 public void setDaPan (DaPan dp)
	 {
		 this.dapan = dp; //��֤�ɽ���¼
	 }
	 /*
	  * 
	  */
	 protected Boolean isThisWeekDaPanXiuShi (LocalDate date) 
	 {
		 if(this.dapan.isThisWeekXiuShi (date) )
			 return true;
		 else
			 return false;
	 }
//		/*
//		 * �жϱ�����ͣ�ƺ���
//		 */
//		protected Boolean checkIsFuPaiAfterTingPai (LocalDate requireddate )
//		{
//			Integer index = this.getRequiredRecordsPostion (this.cjeperiodlist,requireddate);
//			if( index != null) {
//				ChenJiaoZhanBiInGivenPeriod curcjlrecord = cjeperiodlist.get(index);
//				ChenJiaoZhanBiInGivenPeriod lastcjlrecord = cjeperiodlist.get(index -1 );
//				LocalDate curweeknum = curcjlrecord.getRecordsDayofEndofWeek();
//				LocalDate lastweeknum = lastcjlrecord.getRecordsDayofEndofWeek() ;
//				long diffwknum = ChronoUnit.WEEKS.between (curweeknum,lastweeknum);
//			 
//				if(diffwknum > 2 ) { //˵����ͣ�Ƶĸ���  
//					return true;	
//				} else if(diffwknum == 2) { //�����Ǵ���������ɵģ�
//					LocalDate dapanlastweekdate = curweeknum.with(DayOfWeek.MONDAY).minus(1,ChronoUnit.WEEKS);
//					if( this.isThisWeekDaPanXiuShi(dapanlastweekdate) )
//						return false;
//					else 
//						return true;
//				} else
//					return false;
//			}
//			
//			return null;
//		}
		
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
						
//							System.out.println(this.getMyOwnCode() + this.getMyOwnName() + "������һ���¸��ɻ���");
						return 100.0;
					}
				
					ChenJiaoZhanBiInGivenPeriod curcjlrecord = cjeperiodlist.get(index);
					ChenJiaoZhanBiInGivenPeriod lastcjlrecord = cjeperiodlist.get(index -1 );
						
					Double curzhanbiratio = curcjlrecord.getCjlZhanBi();
					Double lastzhanbiratio = lastcjlrecord.getCjlZhanBi();
					double zhanbigrowthrate = (curzhanbiratio - lastzhanbiratio)/lastzhanbiratio;
//					System.out.println(super.getMyOwnCode() + super.getMyOwnName() + "ռ������" + requireddate.toString() + zhanbigrowthrate);
					return zhanbigrowthrate;
			}
			
			System.out.println(this.getMyOwnCode() + this.getMyOwnName() + requireddate.toString() + "ռ������Ϊnull");
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
					if(index != 0)
//						System.out.println(this.getMyOwnCode() + this.getMyOwnName() + "������һ���¸��ɻ���");
					return 0;
				}

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
			
			return maxweek;
		}
	 	/*
		 * ����ɽ���仯������
		 */
		public Double getChenJiaoErChangeGrowthRateForAGivenPeriod (LocalDate requireddate)
		{
			if(cjeperiodlist == null)
				return null;
			
			//�ж��ϼ����(���̻��߰��)�Ƿ�����,������û�бȽϵ����壬ֱ�ӷ���-100��
//			LocalDate tplastdate = curweeknum.minus(1,ChronoUnit.WEEKS);
			Double dpcjediff = this.dapan.getChengJiaoErDifferenceOfLastWeek(requireddate);
			if( dpcjediff<0 || dpcjediff == null ) //����������
				return -100.0;
			
			Integer index = super.getRequiredRecordsPostion (requireddate);
			if( index != null) {
				ChenJiaoZhanBiInGivenPeriod curcjlrecord = cjeperiodlist.get(index); //�Լ���ǰ�ܳɽ���

				ChenJiaoZhanBiInGivenPeriod lastcjlrecord = null;
				try {
					 lastcjlrecord = cjeperiodlist.get(index -1 );
				} catch ( java.lang.ArrayIndexOutOfBoundsException e) {
					System.out.println(this.getMyOwnCode() + this.getMyOwnName() + "�����Ǹ��°��" );
				
					Double curggcje = curcjlrecord.getMyOwnChengJiaoEr(); //ͣ�ƺ����гɽ�����Ӧ�ü�����
					return curggcje/dpcjediff;
				}

				//�������
				Double curcje = curcjlrecord.getMyOwnChengJiaoEr();
				Double lastcje = lastcjlrecord.getMyOwnChengJiaoEr();
				Double cjechange = curcje - lastcje; //���ɳɽ����ı仯
				
				return cjechange/dpcjediff;
			}

			return -100.0;
		}
		/*
		 * ������ʹ��̳ɽ����ĸ�������
		 */
		public ChenJiaoZhanBiInGivenPeriod getNodeFengXiResultForSpecificDate (LocalDate requireddate)
		{
			if(cjeperiodlist == null)
				return null;
			
//			NodeFengXiData nodefx = new NodeFengXiData (super.getMyOwnCode(),requireddate);
			Double dpcjediff = this.dapan.getChengJiaoErDifferenceOfLastWeek(requireddate);
			
			Integer index = super.getRequiredRecordsPostion (requireddate);
			if( index != null) {
				ChenJiaoZhanBiInGivenPeriod curcjlrecord = cjeperiodlist.get(index); //�Լ���ǰ�ܳɽ���
				
				if( super.getChengJiaoErDifferenceOfLastWeek(requireddate) == null && super.cjeperiodlist.get(index) != null ) {
//					System.out.println(this.getMyOwnCode() + this.getMyOwnName() + "������һ���¸��ɻ���");
					//����ɽ���仯������
					if( dpcjediff<0 || dpcjediff == null ) {//����������
						curcjlrecord.setGgbkcjegrowthzhanbi(-100.0);
					} else {
						Double curggcje = curcjlrecord.getMyOwnChengJiaoEr(); //ͣ�ƺ����гɽ�����Ӧ�ü�����
						curcjlrecord.setGgbkcjegrowthzhanbi( curggcje/dpcjediff);
					}
					//��������ܵĳɽ���ռ���Ƕ��������ڵ����ռ��
					curcjlrecord.setGgbkzhanbimaxweek(0);
					//��������ܵĳɽ���ռ������
					curcjlrecord.setGgbkzhanbigrowthrate(0.0);
					
					return curcjlrecord;
				} else {
					ChenJiaoZhanBiInGivenPeriod lastcjlrecord = cjeperiodlist.get(index -1 );
					////����ɽ���仯������ 
					if( dpcjediff<0 ) {//����������
						curcjlrecord.setGgbkcjegrowthzhanbi(-100.0);
					} else {
//						Double curcje = curcjlrecord.getMyOwnChengJiaoEr();
//						Double lastcje = lastcjlrecord.getMyOwnChengJiaoEr();
//						
//						Double cjechange = curcje - lastcje; //���ɳɽ����ı仯
						Double cjechange = super.getChengJiaoErDifferenceOfLastWeek(requireddate);
						curcjlrecord.setGgbkcjegrowthzhanbi(cjechange/dpcjediff);
					}
					
					//��������ܵĳɽ���ռ������
					Double curzhanbiratio = curcjlrecord.getCjlZhanBi();
					Double lastzhanbiratio = lastcjlrecord.getCjlZhanBi();
					double zhanbigrowthrate = (curzhanbiratio - lastzhanbiratio)/lastzhanbiratio;
					curcjlrecord.setGgbkzhanbigrowthrate(zhanbigrowthrate);
					
					//��������ܵĳɽ���ռ���Ƕ��������ڵ����ռ��
					Integer maxweek = 0;
					for(int i = index -1;i>=0;i--) {
							lastcjlrecord = cjeperiodlist.get(i );
							lastzhanbiratio = lastcjlrecord.getCjlZhanBi();
							if(curzhanbiratio > lastzhanbiratio)
								maxweek ++;
							else
								break;
					}
					curcjlrecord.setGgbkzhanbimaxweek(maxweek);
					
					return curcjlrecord;
				}
				
			}
			
			return null;
		}
		

}
