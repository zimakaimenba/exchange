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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic;
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
//	private HashMap<String, Stock> allbkge;
	private String bankuaileixing; // ͨ�������涨��İ���м��֣�1.�и��������гɽ������� 2. �и��������޳ɽ������� 3.�޸��������гɽ������� 
	private boolean notexporttogehpi = false;

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
	public ArrayList<StockOfBanKuai> getAllCurrentBanKuaiGeGu() 
	{
		ArrayList<StockOfBanKuai> stocklist = new ArrayList<StockOfBanKuai> ();
		int childcount = this.getChildCount();
		if (this.getChildCount() >= 0) {
            for (Enumeration e= this.children(); e.hasMoreElements(); ) {
            	BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode)e.nextElement();
            	if(childnode.getType() == BanKuaiAndStockBasic.BKGEGU) 
                    stocklist.add((StockOfBanKuai)childnode);
                }
        }
		return stocklist;
    }
	/*
	 * ֻ����ָ�������гɽ����ĸ���,�гɽ�����˵�������Ǹð��ĸ���,û�гɽ���˵��Ҫ���Ѿ����Ǹð��ĸ��ɣ�Ҫ������ͣ��
	 */
	public ArrayList<StockOfBanKuai> getSpecificPeriodBanKuaiGeGu(LocalDate requireddate,String period) 
	{
		ArrayList<StockOfBanKuai> result = new ArrayList<StockOfBanKuai> ();
		
		if (this.getChildCount() >= 0) {
            for (Enumeration e= this.children(); e.hasMoreElements(); ) {
            	BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode)e.nextElement();
            	if(childnode.getType() == BanKuaiAndStockBasic.BKGEGU) {
            		NodeXPeriodDataBasic stockxperioddata = childnode.getNodeXPeroidData(period);
                    if(stockxperioddata != null) {
                    	ChenJiaoZhanBiInGivenPeriod records = stockxperioddata.getSpecficRecord(requireddate, 0);
      				  if(records != null)
      					  result.add((StockOfBanKuai)childnode);
                    }
            	}
                
            }
        }
		return result;
	}
	/*
	 * ���ĳһ������
	 */
	public StockOfBanKuai getBanKuaiGeGu (String stockcode)
	{
		if (this.getChildCount() >= 0) {
            for (Enumeration e= this.children(); e.hasMoreElements(); ) {
            	BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode)e.nextElement();
            	if(childnode.getType() == BanKuaiAndStockBasic.BKGEGU && childnode.getMyOwnCode().equals(stockcode))
                	return (StockOfBanKuai)childnode;
            }
        }
		
		return null;
	}
	/*
	 *����������ܻ��stock��stockofbankuai 
	 */
//	private Boolean isBanKuaiGeGu (Stock stock)
//	{
//		if (this.getChildCount() >= 0) {
//            for (Enumeration e= this.children(); e.hasMoreElements(); ) {
//                StockOfBanKuai childnode = (StockOfBanKuai)e.nextElement();
//                if(childnode.getStock().getMyOwnCode().equals(stock.getMyOwnCode())) {
//                	return true;
//                }
//            }
//        }
//		
//		return false;
//	}
	/*
	 * 
	 */
	public void addNewBanKuaiGeGu (StockOfBanKuai stock) 
	{
		boolean hasalreadybeenbankuaigegu = false;
		
		if (this.getChildCount() >= 0) {
            for (Enumeration e= this.children(); e.hasMoreElements(); ) {
            	BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode)e.nextElement();
            	if(childnode.getType() == BanKuaiAndStockBasic.BKGEGU && childnode.getMyOwnCode().equals(stock.getMyOwnCode()))
                	hasalreadybeenbankuaigegu = true;
                
            }
        }
		if(!hasalreadybeenbankuaigegu)
			this.add(stock);
	}
	
	/**
	 * @return the sysBanKuaiWeight
	 */
	public Integer getGeGuSuoShuBanKuaiWeight(String stockcode) 
	{
		int childcount = this.getChildCount();
		if (this.getChildCount() >= 0) {
            for (Enumeration e= this.children(); e.hasMoreElements(); ) {
            	BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode)e.nextElement();
                
                if( childnode.getMyOwnCode().equals(stockcode)) {
    				Integer quanzhong = ((StockOfBanKuai)childnode).getQuanZhong();
    				return quanzhong;
                }
            }
        }
		return null;
	}
	public void setGeGuSuoShuBanKuaiWeight(String stockcode , Integer quanzhong) 
	{
		int childcount = this.getChildCount();
		if (this.getChildCount() >= 0) {
            for (Enumeration e= this.children(); e.hasMoreElements(); ) {
            	BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode)e.nextElement();
                
                if(childnode.getMyOwnCode().equals(stockcode)) {
                	 ((StockOfBanKuai)childnode).setStockQuanZhong(quanzhong);
                }
            }
        }
	}
	public NodeXPeriodDataBasic getStockXPeriodDataForABanKuai (String stockcode,String period)
	{
		int childcount = this.getChildCount();
		if (this.getChildCount() >= 0) {
            for (Enumeration e= this.children(); e.hasMoreElements(); ) {
            	BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode)e.nextElement();
                
                if(childnode.getMyOwnCode().equals(stockcode)) {
                	NodeXPeriodDataBasic perioddata = ((StockOfBanKuai)childnode).getStockXPeriodDataForBanKuai(period);
            		return perioddata;
                }
            }
        }
		return null;
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

//			@Override
//			public ChenJiaoZhanBiInGivenPeriod getNodeFengXiResultForSpecificDate(LocalDate requireddate) 
//			{ 	
//				return null;
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
//			}


	
			
			
	}
	
	

}
