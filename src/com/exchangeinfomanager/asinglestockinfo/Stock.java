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
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;

import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountInfoBasic;
import com.exchangeinfomanager.asinglestockinfo.BanKuai.BanKuaiNodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.TreeRelated;
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
		
		super.nodewkdata = new StockNodeXPeriodData (StockGivenPeriodDataItem.WEEK) ;
		super.nodedaydata = new StockNodeXPeriodData (StockGivenPeriodDataItem.DAY) ;
//		super.nodemonthdata = new StockNodeXPeriodData (StockGivenPeriodDataItem.MONTH) ;
		super.nodetreerelated = new TreeRelated (this);
		
	}
	
	private String checklistXml;
	private static Logger logger = Logger.getLogger(Stock.class);
	
//	private Multimap<String> chiCangAccountNameList; //���иù�Ʊ�������˻�������
	private HashMap<String,AccountInfoBasic> chiCangAccounts; //���иù�Ʊ�������˻�<�˻����˻���Ϣ>
//	private HashMap<String,StockOfBanKuai> suoShuTdxBanKuaiData; //����ͨ����ϵͳ��� <���code,�������>
//	private  HashMap<String,Integer> sysBanKuaiWeight; //����ͨ����ϵͳ���Ȩ��
//	private HashSet<String> suoShuZdyBanKuai; //�����Զ�����
	private HashMap<String, String> suoShuCurSysBanKuai; //���ɵ�ǰ������ͨ���Ű��
	
//	private DaPan dapan;
//	public NodeXPeriodDataBasic getNodeXPeroidData (String period)
//	{
//		if(period.equals(StockGivenPeriodDataItem.WEEK))
//			return nodewkdata;
//		else if(period.equals(StockGivenPeriodDataItem.MONTH))
//			return nodemonthdata;
//		else if(period.equals(StockGivenPeriodDataItem.DAY))
//			return nodedaydata;
//		else 
//			return null;
//	}
	
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
	 * 
	 */
	protected Boolean isTingPai (LocalDate requireddate,int difference, String period )
	{
		NodeXPeriodDataBasic nodexdate = this.getNodeXPeroidData(period);
		if( !nodexdate.hasRecordInThePeriod(requireddate, difference) ) {
			boolean dapanxiushi = ((DaPan)getRoot()).isDaPanXiuShi(requireddate,difference,period );
			if(dapanxiushi)
				return false;
			else
				return true;
		} else
			return false;
	}
	
	
	/*
	 * �жϱ�������ͣ�ƺ���
	 */
//	protected Boolean isFuPaiAfterTingPai (LocalDate requireddate,int difference,String period )
//	{
//		NodeXPeriodDataBasic nodexdate = this.getNodeXPeroidData(period);
//		 StockGivenPeriodDataItem nodexcurdatedata = nodexdate.getSpecficRecord(requireddate, difference);
//		if( nodexcurdatedata == null) 
//			return null;
//		
//		int index = -1;
//		while ( ((DaPan)getRoot()).isDaPanXiuShi(requireddate, index ,period) ) { //���ܿ��ܴ�������
//			index --;
//		}
//		
//		 StockGivenPeriodDataItem lastcjlrecord = nodexdate.getSpecficRecord (requireddate,index);
//		if(lastcjlrecord == null) {//����ǰ���ǿգ�˵��Ҫô���°��Ҫôͣ�Ƶ�
//			return true;
//		} else 
//			return false;
//	}
	 
	
	/*
	 * stock ������ڲ���ֻ�������Դ��̵����ݣ�����԰���������StockOfBanKuai
	 */
	public class StockNodeXPeriodData extends NodeXPeriodData
	{
		public StockNodeXPeriodData (String nodeperiodtype1) 
		{
			super(nodeperiodtype1);
			stockhuanshoulv = new TimeSeries(nodeperiodtype1);
			stockliutongshizhi = new TimeSeries(nodeperiodtype1);
			stockzongshizhi = new TimeSeries(nodeperiodtype1);
		}
		
		private  TimeSeries stockhuanshoulv; //������
		private  TimeSeries stockliutongshizhi; //��ͨ��ֵ
		private  TimeSeries stockzongshizhi; //����ֵ
		/*
		* (non-Javadoc)
		* @see com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData#addNewXPeriodData(com.exchangeinfomanager.asinglestockinfo.StockGivenPeriodDataItem)
		*/
		public Boolean isVeryVeryNewXinStock ()
		{
			if(super.getNodeperiodtype().equals(StockGivenPeriodDataItem.WEEK)) {
				if(super.stockamo.getItemCount() >=4)
					return false;
				else 
					return true;
			} else if(super.getNodeperiodtype().equals(StockGivenPeriodDataItem.DAY)) {
				if(super.stockamo.getItemCount() >= 20)
					return false;
				else 
					return true;
			}  else if(super.getNodeperiodtype().equals(StockGivenPeriodDataItem.MONTH)) {
				if(super.stockamo.getItemCount() > 1)
					return false;
				else 
					return true;
			}
			return null;
		}
		/*
		 * (non-Javadoc)
		 * @see com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData#addNewXPeriodData(com.exchangeinfomanager.asinglestockinfo.StockGivenPeriodDataItem)
		 */
		public void addNewXPeriodData (StockGivenPeriodDataItem kdata)
		{
			try {
				stockohlc.setNotify(false);
				stockohlc.add(kdata);
			} catch (org.jfree.data.general.SeriesException e) {
//				System.out.println(kdata.getMyOwnCode() + kdata.getPeriod() + "�����Ѿ����ڣ�" + kdata.getPeriod().getStart() + "," + kdata.getPeriod().getEnd() + ")");
			}
			try {
				stockamo.setNotify(false);
				stockamo.add(kdata.getPeriod(),kdata.getMyOwnChengJiaoEr(),false);
//				stockvol.add(kdata.getPeriod(),kdata.getMyownchengjiaoliang(),false);
				if(kdata.getCjeZhanBi() != null) {
					stockamozhanbi.setNotify(false);
					stockamozhanbi.add(kdata.getPeriod(),kdata.getCjeZhanBi(),false);
				}
//				if(kdata.getCjlZhanBi() != null)
//					stockvolzhanbi.add(kdata.getPeriod(),kdata.getCjlZhanBi(),false);
			} catch (org.jfree.data.general.SeriesException e) {
//				System.out.println(kdata.getMyOwnCode() + kdata.getPeriod() + "�����Ѿ����ڣ�" + kdata.getPeriod().getStart() + "," + kdata.getPeriod().getEnd() + ")");
//				e.printStackTrace();
			}
			
			try {
				stockhuanshoulv.setNotify(false);
				stockhuanshoulv.add(kdata.getPeriod(),kdata.getHuanshoulv(),false);
			} catch (org.jfree.data.general.SeriesException e) {
//				System.out.println(kdata.getMyOwnCode() + kdata.getPeriod() + "�����Ѿ����ڣ�" + kdata.getPeriod().getStart() + "," + kdata.getPeriod().getEnd() + ")");
//				e.printStackTrace();
			} 
			try {
				stockliutongshizhi.setNotify(false);
				stockliutongshizhi.add(kdata.getPeriod(),kdata.getLiutongshizhi(),false);
			} catch (org.jfree.data.general.SeriesException e) {
//				System.out.println(kdata.getMyOwnCode() + kdata.getPeriod() + "�����Ѿ����ڣ�" + kdata.getPeriod().getStart() + "," + kdata.getPeriod().getEnd() + ")");
//				e.printStackTrace();
			}
			try {	
				stockzongshizhi.setNotify(false);
				stockzongshizhi.add(kdata.getPeriod(),kdata.getZongshizhi(),false);
				
			} catch (org.jfree.data.general.SeriesException e) {
//				System.out.println(kdata.getMyOwnCode() + kdata.getPeriod() + "�����Ѿ����ڣ�" + kdata.getPeriod().getStart() + "," + kdata.getPeriod().getEnd() + ")");
//				e.printStackTrace();
			}
		}
		/*
		 * (non-Javadoc)
		 * @see com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData#getChenJiaoErMaxWeekOfSuperBanKuai(java.time.LocalDate, int)
		 */
		public Double getSpecificTimeHuanShouLv (LocalDate requireddate,int difference)
		{
			try{
				TimeSeriesDataItem curhslrecord = stockhuanshoulv.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
				Double curhsl = curhslrecord.getValue().doubleValue();
				
				return curhsl;
			} catch (java.lang.NullPointerException e) {
				e.printStackTrace();
				return null;
			}
			
		}
		/*
		 * (non-Javadoc)
		 * @see com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData#getChenJiaoErMaxWeekOfSuperBanKuai(java.time.LocalDate, int)
		 */
		public double getSpecificTimeZongShiZhi (LocalDate requireddate,int difference)
		{
			TimeSeriesDataItem curzszrecord = stockzongshizhi.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
			Double curzsz = null ;
			try {
				curzsz = curzszrecord.getValue().doubleValue();
			} catch (Exception e) {
				e.printStackTrace();
				logger.info(" ");
			}
			
			return curzsz;
		}
		/*
		 * (non-Javadoc)
		 * @see com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData#getChenJiaoErMaxWeekOfSuperBanKuai(java.time.LocalDate, int)
		 */
		public Double getSpecificTimeLiuTongShiZhi (LocalDate requireddate,int difference)
		{
			TimeSeriesDataItem curltszrecord = stockliutongshizhi.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
			try{
				Double curltsz = curltszrecord.getValue().doubleValue();
				return curltsz;
			} catch (java.lang.NullPointerException e ) {
				return null;
			}
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
			for(int index = -1;index >= -100; index--) { //Ŀǰ��¼��������10000�����ڣ�����10000�㹻
				if( dapan.isDaPanXiuShi(requireddate, index ,getNodeperiodtype())  ) //���̻���������
					continue;
				
				if(isTingPai(requireddate,index,getNodeperiodtype()))
					return maxweek;
				
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
				if( dapan.isDaPanXiuShi(requireddate, index ,getNodeperiodtype())  ) { //���̻���������
//					logger.debug("dapanͣ��");
					continue;
				}
				
				if(isTingPai(requireddate,index,getNodeperiodtype())) //����Ҳ����ͣ�ƣ�ͣ��ǰ�ľͲ�������
					return maxweek;
				
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

