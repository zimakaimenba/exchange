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

import org.apache.log4j.Logger;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.Stock.StockNodeXPeriodData;
import com.exchangeinfomanager.commonlib.CommonUtility;

public class BanKuai extends BkChanYeLianTreeNode
{

	public static String  HASGGWITHSELFCJL = "HASGGWITHSELFCJL" , HASGGNOSELFCJL = "HASGGNOSELFCJL"
						, NOGGWITHSELFCJL = "NOGGWITHSELFCJL" , NOGGNOSELFCJL = "NOGGNOSELFCJL"; // ͨ�������涨��İ���м��֣�1.�и��������гɽ������� 2. �и��������޳ɽ������� 3.�޸��������гɽ�������
	
	public BanKuai(String bkcode,String name ) 
	{
		super(bkcode,name);
		super.nodetype = BanKuaiAndStockBasic.TDXBK;
		
		super.nodewkdata = new BanKuaiNodeXPeriodData (ChenJiaoZhanBiInGivenPeriod.WEEK) ;
		super.nodedaydata = new BanKuaiNodeXPeriodData (ChenJiaoZhanBiInGivenPeriod.DAY) ;
		super.nodemonthdata = new BanKuaiNodeXPeriodData (ChenJiaoZhanBiInGivenPeriod.MONTH) ;
	}
	
	private static Logger logger = Logger.getLogger(BanKuai.class);
	private HashMap<String, Stock> allbkge;
	private String bankuaileixing; // ͨ�������涨��İ���м��֣�1.�и��������гɽ������� 2. �и��������޳ɽ������� 3.�޸��������гɽ������� 
	private boolean notexporttogehpi = false;

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
	public void setBanKuaiLeiXing (String leixing)
	{
		if(leixing == null)
			this.bankuaileixing = this.NOGGNOSELFCJL;
		else if(leixing.equals(this.HASGGNOSELFCJL) || leixing.equals(this.HASGGWITHSELFCJL) || leixing.equals(this.NOGGWITHSELFCJL) || leixing.equals(this.NOGGNOSELFCJL) )
			this.bankuaileixing = leixing;
	}
	public String getBanKuaiLeiXing ()	{
		return this.bankuaileixing;
	}
	 /**
	 * @return the tmpallbkge
	 */
	public HashMap<String, Stock> getAllCurrentBanKuaiGeGu() {
		return allbkge;
	}
	/*
	 * ֻ����ָ�������гɽ����ĸ���,�гɽ�����˵�������Ǹð��ĸ���,û�гɽ���˵��Ҫ���Ѿ����Ǹð��ĸ��ɣ�Ҫ������ͣ��
	 */
	public HashMap<String, Stock> getSpecificPeriodBanKuaiGeGu(LocalDate requireddate,String period) 
	{
		if(this.allbkge == null || this.allbkge.isEmpty())
			return null;
		
		LocalDate bkstart = getNodeXPeroidData(period).getRecordsStartDate();
		LocalDate bkend = getNodeXPeroidData(period).getRecordsEndDate();
		if(requireddate.isBefore(bkstart) || requireddate.isAfter(bkend) ) //��ǰû�и����ڵļ�¼
			return null;

		HashMap<String, Stock> result = new HashMap<String, Stock> ();
		for (Map.Entry<String, Stock> entry : allbkge.entrySet()) {  
			  Stock stock = entry.getValue();
			  NodeXPeriodData stockxperioddata = stock.getStockXPeriodDataForABanKuai(this.getMyOwnCode(),period);
			  if(stockxperioddata != null) {
				  ChenJiaoZhanBiInGivenPeriod records = stockxperioddata.getSpecficRecord(requireddate, 0);
				  if(records != null)
					  result.put(stock.getMyOwnCode(), stock);
			  }
		}
		
		return result;
	}
	/*
	 * ���ĳһ������
	 */
	public Stock getBanKuaiGeGu (String stockcode)
	{
		if(this.allbkge == null) 
			this.allbkge = new HashMap<String, Stock> ();
		
		try {
			Stock stock = this.allbkge.get(stockcode);
			if(stock == null) {
				return null;
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
	private Boolean isBanKuaiGeGu (Stock stock)
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
			this.allbkge.put(stockcode, stock);
		}
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
	 * GEPHI part
	 */
//	public void setNotExportToGephi ()
//	 {
//		 this.notexporttogehpi = true;
//	 }
//	 public boolean shouldNotExportToGephi ()
//	 {
//		 return this.notexporttogehpi;
//	 }
	 
	public class BanKuaiNodeXPeriodData extends NodeXPeriodData
	{
			public BanKuaiNodeXPeriodData (String nodeperiodtype1)
			{
				super(nodeperiodtype1);
			}

			@Override
			public ChenJiaoZhanBiInGivenPeriod getSpecficRecord(LocalDate requireddate, int difference) 
			{
				if(periodlist == null)
					return null;

				String nodeperiod = super.getNodeperiodtype();
				LocalDate expectedate = null;
				if(nodeperiod.equals(ChenJiaoZhanBiInGivenPeriod.WEEK)) { //������������ݣ�ֻҪ��������ͬ�����ɷ���
					expectedate = requireddate.plus(difference,ChronoUnit.WEEKS);
				} else if(nodeperiod.equals(ChenJiaoZhanBiInGivenPeriod.DAY)) { //������������ݣ�������ͬ�ſɷ���
					expectedate = requireddate.plus(difference,ChronoUnit.DAYS);
				}  else if(nodeperiod.equals(ChenJiaoZhanBiInGivenPeriod.MONTH)) { //������������ݣ�������ͬ�ſɷ���
					expectedate = requireddate.plus(difference,ChronoUnit.MONTHS);
				}
				
				int index = -1;
				ChenJiaoZhanBiInGivenPeriod foundrecord = null;
				for(ChenJiaoZhanBiInGivenPeriod tmpcjzb : super.periodlist) {
					if(nodeperiod.equals(ChenJiaoZhanBiInGivenPeriod.MONTH)) { //������������ݣ�ֻҪ��������ͬ�����ɷ���
						index ++;
						
						int year = expectedate.getYear();
						int month = expectedate.getMonthValue();
						
						int yearnum = tmpcjzb.getRecordsYear();
						int monthnum = tmpcjzb.getRecordsMonth();
						
						if(month == monthnum && yearnum == year) {
							foundrecord = tmpcjzb;
							break;
						}
					} else	if(nodeperiod.equals(ChenJiaoZhanBiInGivenPeriod.WEEK)) { //������������ݣ�ֻҪ��������ͬ�����ɷ���
						index ++;
						
						int year = expectedate.getYear();
						WeekFields weekFields = WeekFields.of(Locale.getDefault()); 
						int weekNumber = expectedate.get(weekFields.weekOfWeekBasedYear());
						
						int yearnum = tmpcjzb.getRecordsYear();
						int wknum = tmpcjzb.getRecordsWeek();
						
						if(wknum == weekNumber && yearnum == year) {
							foundrecord = tmpcjzb;
							break;
						}
					} else if(nodeperiod.equals(ChenJiaoZhanBiInGivenPeriod.DAY)) { //������������ݣ�������ͬ�ſɷ���
						index ++;
						LocalDate recordsday = tmpcjzb.getRecordsDayofEndofWeek();
						if(recordsday.isEqual(requireddate)) {
							foundrecord = tmpcjzb;
							break;
						}
					}
				}
				
				return foundrecord;
			}

			@Override
			public Double getChengJiaoErDifferenceWithLastPeriod(LocalDate requireddate)
			{
				if(periodlist == null)
					return null;
				
				ChenJiaoZhanBiInGivenPeriod curcjlrecord = this.getSpecficRecord (requireddate,0);
				if( curcjlrecord == null) 
					return null;
				
				int index = -1;
				while ( ((DaPan)getRoot()).isDaPanXiuShi(requireddate, index ,super.getNodeperiodtype()) ) { //���ܿ��ܴ�������
					index --;
				}
				
				ChenJiaoZhanBiInGivenPeriod lastcjlrecord = this.getSpecficRecord (requireddate,index);
				if(lastcjlrecord == null) //����ǰ���ǿգ�˵��Ҫ���°�顣���û��ͣ�Ƶ�
					return null;
				
				Double curcje = curcjlrecord.getMyOwnChengJiaoEr();
				Double lastcje = lastcjlrecord.getMyOwnChengJiaoEr();
				
				curcjlrecord.setGgBkCjeDifferenceWithLastPeriod (curcje - lastcje);
				return curcje - lastcje;
			}

			@Override
			public Double getChenJiaoErZhanBiGrowthRateOfSuperBanKuai(LocalDate requireddate) 
			{
				if(periodlist == null)
					return null;
				
				ChenJiaoZhanBiInGivenPeriod curcjlrecord = this.getSpecficRecord (requireddate,0);
				if( curcjlrecord == null) 
					return null;
				
				int index = -1;
				while ( ((DaPan)getRoot()).isDaPanXiuShi(requireddate, index ,super.getNodeperiodtype()) ) { //���ܿ��ܴ�������
					index --;
				}
				
				ChenJiaoZhanBiInGivenPeriod lastcjlrecord = this.getSpecficRecord (requireddate,index);
				if(lastcjlrecord == null) { //����ǰ���ǿգ�˵��Ҫ���°�顣���û��ͣ�Ƶ�
					logger.debug(getMyOwnCode() + getMyOwnName() + "������һ���¸��ɻ���");
					return 100.0;
				}
				
				Double curzhanbiratio = curcjlrecord.getCjeZhanBi();
				Double lastzhanbiratio = lastcjlrecord.getCjeZhanBi();
				Double zhanbigrowthrate = (curzhanbiratio - lastzhanbiratio)/lastzhanbiratio;
				logger.debug(getMyOwnCode() + getMyOwnName() + "ռ������" + requireddate.toString() + zhanbigrowthrate);
				
				curcjlrecord.setGgBkCjeZhanbiGrowthRate(zhanbigrowthrate);
				return zhanbigrowthrate;
			}

			@Override
			public Integer getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(LocalDate requireddate) 
			{
				if(periodlist == null)
					return null;
				
				ChenJiaoZhanBiInGivenPeriod curcjlrecord = this.getSpecficRecord (requireddate,0);
				if( curcjlrecord == null) 
					return null;
				
				int index = -1;
				while ( ((DaPan)getRoot()).isDaPanXiuShi(requireddate, index ,super.getNodeperiodtype()) ) { //���ܿ��ܴ�������
					index --;
				}
				
				ChenJiaoZhanBiInGivenPeriod lastcjlrecord = this.getSpecficRecord (requireddate,index);
				if(lastcjlrecord == null) { //����ǰ���ǿգ�˵��Ҫ���°�顣���û��ͣ�Ƶ�
					logger.debug(getMyOwnCode() + getMyOwnName() + "������һ���¸��ɻ���");
					return 100;
				}
				
				Double curzhanbiratio = curcjlrecord.getCjeZhanBi();
				int maxweek = 0;
				for(;index >= -100000; index--) {
					lastcjlrecord = this.getSpecficRecord (requireddate,index);
					
					if(lastcjlrecord == null) //Ҫ���ǵ��˼�¼����ͷ��
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
				Double dpcjediff = ((DaPan)getRoot()).getNodeXPeroidData(nodept).getChengJiaoErDifferenceWithLastPeriod(requireddate);
				if( dpcjediff<0 || dpcjediff == null ) {//����������
					curcjlrecord.setGgBkCjeGrowthRateToSuperBanKuaiCjeGrowth (-100.0);
					return -100.0;
				}
				
				int index = -1;
				while ( ((DaPan)getRoot()).isDaPanXiuShi(requireddate, index ,super.getNodeperiodtype()) ) { //���ܿ��ܴ�������
					index --;
				}
				
				ChenJiaoZhanBiInGivenPeriod lastcjlrecord = this.getSpecficRecord (requireddate,index);
				if(lastcjlrecord == null) { //����ǰ���ǿգ�˵��Ҫ���°�顣���û��ͣ�Ƶ�
					Double curggcje = curcjlrecord.getMyOwnChengJiaoEr(); //�°�����гɽ�����Ӧ�ü�����
					return curggcje/dpcjediff;
				}
				
				Double curcje = curcjlrecord.getMyOwnChengJiaoEr();
				Double lastcje = lastcjlrecord.getMyOwnChengJiaoEr();
				Double cjechange = curcje - lastcje; //���ɳɽ����ı仯
				
				curcjlrecord.setGgBkCjeGrowthRateToSuperBanKuaiCjeGrowth (cjechange/dpcjediff);
				return cjechange/dpcjediff;
			}

			@Override
			public ChenJiaoZhanBiInGivenPeriod getNodeFengXiResultForSpecificDate(LocalDate requireddate) 
			{ 	
				return null;
//				if(periodlist == null)
//					return null;
//				
//				ChenJiaoZhanBiInGivenPeriod curcjlrecord = this.getSpecficRecord (requireddate,0);
//				if( curcjlrecord == null) 
//					return null;
//				
//				this.getChengJiaoErDifferenceWithLastPeriod(requireddate);
//				this.getChenJiaoErChangeGrowthRateOfSuperBanKuai(requireddate);
//				this.getChenJiaoErZhanBiGrowthRateOfSuperBanKuai(requireddate);
//				this.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(requireddate);
//				
//				return curcjlrecord;
				// TODO Auto-generated method stub
//				if(periodlist == null)
//					return null;
//				
//				String nodept = super.getNodeperiodtype();
//				
//				ChenJiaoZhanBiInGivenPeriod currecord = this.getSpecficRecord (requireddate,0); //�Լ���ǰ�ܳɽ���
//				if( currecord != null) {
//					
//					DaPan dapan =  (DaPan)getRoot();
//					NodeXPeriodData dpxdata = dapan.getNodeXPeroidData(nodept);
//					Double dpcjediff = dpxdata.getChengJiaoErDifferenceOfPeriod(requireddate);
//			
//					if( getChengJiaoErDifferenceOfPeriod(requireddate) == null  ) {
//						logger.debug(getMyOwnCode() + getMyOwnName() + "������һ���¸��ɻ���");
//						//����ɽ���仯������
//						if( dpcjediff<0 || dpcjediff == null ) {//����������
//							currecord.setGgbkcjegrowthzhanbi(-100.0);
//						} else {
//							Double curggcje = currecord.getMyOwnChengJiaoEr(); //ͣ�ƺ����гɽ�����Ӧ�ü�����
//							currecord.setGgbkcjegrowthzhanbi( curggcje/dpcjediff);
//						}
//						//��������ܵĳɽ���ռ���Ƕ��������ڵ����ռ��
//						currecord.setGgbkzhanbimaxweek(0);
//						//��������ܵĳɽ���ռ������
//						currecord.setGgbkzhanbigrowthrate(0.0);
//						//�ɽ����Ƕ��������ڵ����
//						currecord.setGgbkcjemaxweek(0);
//						
//						return currecord;
//					} else {
//						ChenJiaoZhanBiInGivenPeriod lastrecord = this.getSpecficRecord (requireddate,-1);
//						////����ɽ���仯������ 
//						if( dpcjediff<0 ) {//����������
//							currecord.setGgbkcjegrowthzhanbi(-100.0);
//						} else {
////							Double curcje = curcjlrecord.getMyOwnChengJiaoEr();
////							Double lastcje = lastcjlrecord.getMyOwnChengJiaoEr();
////							
////							Double cjechange = curcje - lastcje; //���ɳɽ����ı仯
//							Double cjechange = getChengJiaoErDifferenceOfPeriod(requireddate);
//							currecord.setGgbkcjegrowthzhanbi(cjechange/dpcjediff);
//						}
//						
//						//��������ܵĳɽ���ռ������
//						Double curzhanbiratio = currecord.getCjeZhanBi();
//						Double lastzhanbiratio = lastrecord.getCjeZhanBi();
//						double zhanbigrowthrate = (curzhanbiratio - lastzhanbiratio)/lastzhanbiratio;
//						currecord.setGgbkzhanbigrowthrate(zhanbigrowthrate);
//						
//						//��������ܵĳɽ���ռ���Ƕ��������ڵ����ռ��
//						Integer maxweek = 0;
//						for(int i = super.getRequiredRecordsPostion(requireddate) -1;i>=0;i--) {
//								lastrecord = periodlist.get(i );
//								lastzhanbiratio = lastrecord.getCjeZhanBi();
//								if(curzhanbiratio > lastzhanbiratio)
//									maxweek ++;
//								else
//									break;
//						}
//						currecord.setGgbkzhanbimaxweek(maxweek);
//						
//						//�ɽ����Ƕ��������ڵ����
//						maxweek = 0;
//						Double curcje = currecord.getMyOwnChengJiaoEr();
//						for(int i = super.getRequiredRecordsPostion(requireddate) -1;i>=0;i--) {
//							lastrecord = periodlist.get(i );
//							Double lastcje = lastrecord.getMyOwnChengJiaoEr();
//							if(curcje > lastcje)
//								maxweek ++;
//							else
//								break;
//						}
//						currecord.setGgbkcjemaxweek(maxweek);
//						
//						return currecord;
//					}
//					
//				}
//				
//				return null;
			}
			
			
	}
	
	

}
