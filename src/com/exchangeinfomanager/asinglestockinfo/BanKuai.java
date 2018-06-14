package com.exchangeinfomanager.asinglestockinfo;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
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
import java.util.Set;

import org.apache.log4j.Logger;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.time.Week;

import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.Stock.StockNodeXPeriodData;
import com.exchangeinfomanager.bankuaifengxi.BanKuaiFengXi.ExportCondition;
import com.exchangeinfomanager.commonlib.CommonUtility;

public class BanKuai extends BkChanYeLianTreeNode
{

	public static String  HASGGWITHSELFCJL = "HASGGWITHSELFCJL" , HASGGNOSELFCJL = "HASGGNOSELFCJL"
						, NOGGWITHSELFCJL = "NOGGWITHSELFCJL" , NOGGNOSELFCJL = "NOGGNOSELFCJL"; // ͨ�������涨��İ���м��֣�1.�и��������гɽ������� 2. �и��������޳ɽ������� 3.�޸��������гɽ�������
	
	public BanKuai(String bkcode,String name ) 
	{
		super(bkcode,name);
		super.nodetype = BanKuaiAndStockBasic.TDXBK;
		
		super.nodewkdata = new BanKuaiNodeXPeriodData (StockGivenPeriodDataItem.WEEK) ;
		super.nodedaydata = new BanKuaiNodeXPeriodData (StockGivenPeriodDataItem.DAY) ;
//		super.nodemonthdata = new BanKuaiNodeXPeriodData (StockGivenPeriodDataItem.MONTH) ;
		super.nodetreerelated = new BanKuai.BanKuaiTreeRelated (this);
	}
	
//	private static Logger logger = Logger.getLogger(BanKuai.class);
//	private HashMap<String, Stock> allbkge;
	private String bankuaileixing; // ͨ�������涨��İ���м��֣�1.�и��������гɽ������� 2. �и��������޳ɽ������� 3.�޸��������гɽ������� 
	private boolean exporttogehpi = true;
	private boolean importdailytradingdata = true;
	private boolean showinbkfxgui = true;

//	public  TreeRelated getNodeTreeRelated ()
//	{
//		return super.nodetreerelated;
//	}
	public boolean isExporttogehpi() {
		return exporttogehpi;
	}
	/*
	 * �����Ƿ񵼳����ݵ�Gephi
	 */
	public void setExporttogehpi(boolean exporttogehpi) {
		this.exporttogehpi = exporttogehpi;
	}
	/*
	 * �����Ƿ�Ҫÿ�쵼�뽻�����ݣ�����Ϊfalse������ÿ�쵼�����ݵ�ʱ������
	 */
	public boolean isImportdailytradingdata() {
		return importdailytradingdata;
	}
	/*
	 * �����Ƿ���ÿ�ս�������
	 */
	public void setImportdailytradingdata(boolean importdailytradingdata) {
		this.importdailytradingdata = importdailytradingdata;
	}
	/*
	 * 
	 */
	public boolean isShowinbkfxgui() {
		return showinbkfxgui;
	}
	/*
	 * �����Ƿ���ʾ�ڰ���������
	 */
	public void setShowinbkfxgui(boolean showinbkfxgui) {
		this.showinbkfxgui = showinbkfxgui;
	}
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode#getNodeXPeroidData(java.lang.String)
	 * ���ĳ�����ڵ�����
	 */
	public NodeXPeriodDataBasic getNodeXPeroidData (String period)
	{
		if(period.equals(StockGivenPeriodDataItem.WEEK))
			return nodewkdata;
		else if(period.equals(StockGivenPeriodDataItem.MONTH))
			return nodemonthdata;
		else if(period.equals(StockGivenPeriodDataItem.DAY))
			return nodedaydata;
		else 
			return null;
	}
//	/*
//	 * ����zhouqirangeָ��period���ڵ�selecteddate��ǰ�������ںϲ�����
//	 */
//	public  TimeSeries getRangedMatchConditionStockNum (ArrayList<ExportCondition> initialzedcon,String period,int zhouqirange)
//	{
//		TimeSeries rangestocknum = new TimeSeries("stocknum");
//		NodeXPeriodDataBasic nodexdata = this.getNodeXPeroidData(period);
//		
//		LocalDate startdate = nodexdata.getRecordsStartDate();
//		LocalDate enddate = nodexdata.getRecordsEndDate();
//		LocalDate tmpdate = startdate;
//		do  {
//			//����Ӧ�ø�������������ѡ���������ͣ�������Ϊ�������ߣ��Ͳ�ϸ����
//				org.jfree.data.time.Week tmpwk = new Week(Date.from(tmpdate.atStartOfDay(ZoneId.systemDefault()).toInstant()) );
//			int matchresult = getPeriodMatchConditionStockNum (initialzedcon, tmpdate, period, zhouqirange);
//			rangestocknum.add(tmpwk, matchresult);
//			
//			if(period.equals(StockGivenPeriodDataItem.WEEK))
//				tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
//			else if(period.equals(StockGivenPeriodDataItem.DAY))
//				tmpdate = tmpdate.plus(1, ChronoUnit.DAYS) ;
//			else if(period.equals(StockGivenPeriodDataItem.MONTH))
//				tmpdate = tmpdate.plus(1, ChronoUnit.MONTHS) ;
//			
//		} while (tmpdate.isBefore( enddate) || tmpdate.isEqual(enddate));
//		
//		return rangestocknum;
//		
//	}
	/*
	 * ����㷨�����ʱ�䣬��ʱ���á����ڼ�����ָ��ʱ�������趨�����İ����ɸ���
	 */
//	public Integer getPeriodMatchConditionStockNum (ArrayList<ExportCondition> initialzedcon,LocalDate selecteddate,String period,int zhouqirange)
//	{
//		if(this.getType() != BanKuaiAndStockBasic.TDXBK)
//			return null;
//		if( ((BanKuai)this).getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL) 
//		 ||  ((BanKuai)this).getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL) //��Щָ����û�и��ɺͳɽ����ģ�������ȽϷ�Χ 
//		 ||  ((BanKuai)this).getBanKuaiLeiXing().equals(BanKuai.NOGGWITHSELFCJL) ) //�������и��ɵİ��
//			return null;
//		
//		Set<StockOfBanKuai> rowbkallgg = new HashSet<StockOfBanKuai> ();
//		for(int rangeindex = 0;rangeindex <= zhouqirange ; rangeindex ++) { //�Ȱ��⼸�ܵĸ��ɶ��ҳ����������ظ�����
//				rowbkallgg.addAll( ((BanKuai)this).getSpecificPeriodBanKuaiGeGu(selecteddate,rangeindex,period) );
//		}
//		
//		HashSet<String> matchednum = new HashSet<String> ();
//		for (ExportCondition expcon : initialzedcon) {
//			Double settingcje = expcon.getSettingcje() ;
//			Integer settindpgmaxwk = expcon.getSettindpgmaxwk();
//			Integer settinbkgmaxwk = expcon.getSettinbkgmaxwk();
//			Integer seetingcjemaxwk = expcon.getSettingcjemaxwk();
//		
//			for (StockOfBanKuai stockofbankuai : rowbkallgg) {
//				NodeXPeriodDataBasic stockxdataforbk = stockofbankuai.getNodeXPeroidData(period);
//				boolean nodeevermatch = false;
//				for(int rangeindex = 0;rangeindex <= zhouqirange ; rangeindex ++) {
//					if(!stockxdataforbk.hasRecordInThePeriod(selecteddate, rangeindex))
//						 continue;
//						
//						 Double recordcje = stockxdataforbk.getChengJiaoEr(selecteddate, rangeindex);
//						 Integer recordmaxbkwk = stockxdataforbk.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(selecteddate,rangeindex) ;
//						 
//						 Stock ggstock = stockofbankuai.getStock();
//						 NodeXPeriodDataBasic stockxdata = ggstock.getNodeXPeroidData(period);
//						 Integer recordmaxdpwk = stockxdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(selecteddate,rangeindex);
//						 Integer recordmaxcjewk = stockxdata.getChenJiaoErMaxWeekOfSuperBanKuai(selecteddate,rangeindex);
//						 
//						 if(recordcje >= settingcje &&  recordmaxbkwk >= settinbkgmaxwk
//									 && recordmaxcjewk >= seetingcjemaxwk && recordmaxdpwk >=  settindpgmaxwk)  { //�������������� ;
//							 nodeevermatch = true;
//						 }
//
//					}
//					
//				if(nodeevermatch)
//					matchednum.add(stockofbankuai.getMyOwnCode()) ;
//			}
//		}
//		
//		int result = matchednum.size();
//		rowbkallgg = null;
//		matchednum = null;
//		return result;
//	}
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
	public Set<StockOfBanKuai> getSpecificPeriodBanKuaiGeGu(LocalDate requireddate,int difference,String period) 
	{
		HashSet<StockOfBanKuai> result = new HashSet<StockOfBanKuai> ();
		
		if (this.getChildCount() >= 0) {
            for (Enumeration e= this.children(); e.hasMoreElements(); ) {
            	BkChanYeLianTreeNode childnode = (BkChanYeLianTreeNode)e.nextElement();
            	if(childnode.getType() == BanKuaiAndStockBasic.BKGEGU) {
            		NodeXPeriodDataBasic stockxperioddata = childnode.getNodeXPeroidData(period);
                    if(stockxperioddata != null) {
                    	 boolean records = stockxperioddata.hasRecordInThePeriod(requireddate, difference);
      				  if(records )
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
                	NodeXPeriodDataBasic perioddata = ((StockOfBanKuai)childnode).getNodeXPeroidData(period);
            		return perioddata;
                }
            }
        }
		return null;
	}
	/*
	 * 
	 */
	public class BanKuaiTreeRelated extends TreeRelated
	{
		public BanKuaiTreeRelated (BkChanYeLianTreeNode treenode1)
		{
			super(treenode1);
		}
		public boolean selfismatchmodel;
		private HashMap<LocalDate,Integer> stocknumsinparsedfile;
		
		private void setSelfIsMatchModel (boolean selfinset)
		{
			this.selfismatchmodel = selfinset;
		}
		public boolean getSelfIsMatchModel ()
		{
			return this.selfismatchmodel;
		}
		public void setStocksNumInParsedFile (LocalDate parsefiledate, boolean selfinset, Integer stocksnum)
		{
			if(stocknumsinparsedfile == null) {
				stocknumsinparsedfile = new HashMap<LocalDate,Integer> ();
				LocalDate friday = parsefiledate.with(DayOfWeek.FRIDAY);
				if(stocksnum >0)
					stocknumsinparsedfile.put(friday, stocksnum);
			} else {
				LocalDate friday = parsefiledate.with(DayOfWeek.FRIDAY);
				if(stocksnum >0)
					stocknumsinparsedfile.put(friday, stocksnum);
			}
			
			this.setSelfIsMatchModel(selfinset);
		}
		public Integer getStocksNumInParsedFileForSpecificDate (LocalDate requiredate)
		{
			try {
				LocalDate friday = requiredate.with(DayOfWeek.FRIDAY);
				return stocknumsinparsedfile.get(friday);
			} catch (java.lang.NullPointerException e) {
				return null;
			}
		}
		
	}

	 
	public class BanKuaiNodeXPeriodData extends NodeXPeriodData 
	{
			public BanKuaiNodeXPeriodData (String nodeperiodtype1)
			{
				super(nodeperiodtype1);
			}
			@Override
			public Integer getChenJiaoErMaxWeekOfSuperBanKuai(LocalDate requireddate,int difference) 
			{
				if(stockohlc == null)
					return null;
				
				TimeSeriesDataItem curcjlrecord = stockamo.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
				if( curcjlrecord == null) 
					return null;
				
				Double curcje = curcjlrecord.getValue().doubleValue();
				int maxweek = 0;
				
				DaPan dapan = (DaPan)getRoot();
				for(int index = -1;index >= -100000; index--) { //Ŀǰ��¼��������10000�����ڣ�����10000�㹻
					if( dapan.isDaPanXiuShi(requireddate, index ,getNodeperiodtype())  ) //���̻���������
						continue;
					
					TimeSeriesDataItem lastcjlrecord = stockamo.getDataItem( getJFreeChartFormateTimePeriod (requireddate,index) );
					if(lastcjlrecord == null ) //���ܵ��˼�¼��ͷ���ˣ������Ǹ�����ʱ�䲻���İ��
						return maxweek;
					
					Double lastcje = lastcjlrecord.getValue().doubleValue();
					if(curcje > lastcje)
						maxweek ++;
					else
						break;
				}

				return maxweek;
			}
			/*
			 * ���ϼ����ĳɽ���ռ���Ƕ������ڵ����ֵ
			 */
			public Integer getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(LocalDate requireddate,int difference) 
			{
				if(stockohlc == null)
					return null;
				
				TimeSeriesDataItem curcjlrecord = this.stockamozhanbi.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
				if( curcjlrecord == null) 
					return null;

				DaPan dapan = (DaPan)getRoot();
				
				Double curzhanbiratio = curcjlrecord.getValue().doubleValue();
				int maxweek = 0;
				for(int index = -1;index >= -100000; index--) {
					if( dapan.isDaPanXiuShi(requireddate, index ,getNodeperiodtype())  ) //���̻���������
						continue;
					
					TimeSeriesDataItem lastcjlrecord = stockamozhanbi.getDataItem( getJFreeChartFormateTimePeriod (requireddate,index) );
					if(lastcjlrecord == null ) //���ܵ��˼�¼��ͷ���ˣ������Ǹ�����ʱ�䲻���İ��
						return maxweek;

					Double lastzhanbiratio = lastcjlrecord.getValue().doubleValue();
					if(curzhanbiratio > lastzhanbiratio)
						maxweek ++;
					else
						break;
				}
				
				return maxweek;
			}

	}

}
