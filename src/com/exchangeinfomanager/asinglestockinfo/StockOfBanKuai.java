package com.exchangeinfomanager.asinglestockinfo;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.jfree.data.time.TimeSeriesDataItem;

import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.TreeRelated;
import com.exchangeinfomanager.asinglestockinfo.Stock.StockNodeXPeriodData;
/*
 * ���ĸ��ɺ���ͨ���̵ĸ������������ƣ�����ͬ���ڳɽ�����һ���Ƕ԰��ĳɽ�����һ���ǶԴ��̵ĳɽ�����
 * StockOfBanKuai ��Ӧ��������ͨ��ֵ������ֵ��Щ���ԣ�Ӧ��ֻ�гɽ����ͳɽ������ԣ�����Ȩ�أ�
 */
public class StockOfBanKuai extends BkChanYeLianTreeNode
{
	public StockOfBanKuai (BanKuai bankuai1,Stock stock1) 
	{
		super (stock1.getMyOwnCode(),stock1.getMyOwnName());
		super.nodetype = BanKuaiAndStockBasic.BKGEGU;
		
		this.bankuai = bankuai1;
		this.stock = stock1;
		
		super.nodewkdata = null;
		super.nodewkdata = new StockOfBanKuaiNodeXPeriodData (StockGivenPeriodDataItem.WEEK) ;
		super.nodedaydata = null; //�Ծ������������˵˵���������ݲ������壬���ߺ������壬�ر��Ǹ�������ڰ����������ݡ�
//		super.nodemonthdata = new StockOfBanKuaiNodeXPeriodData (StockGivenPeriodDataItem.MONTH) ;
		
		super.nodetreerelated = null;
		super.nodetreerelated = new StockOfBanKuai.StockOfBanKuaiTreeRelated(this);
	}
	
//	private static Logger logger = Logger.getLogger(StockOfBanKuai.class);
	private BanKuai bankuai;
	private Stock stock;
	private Integer quanzhong;
	private Boolean isbklongtou;
	
	public Boolean isBkLongTou ()
	{
		if(this.isbklongtou == null)
			return false;
		else
			return this.isbklongtou;
	}
	public void setBkLongTou (Boolean isbklongtou1)
	{
		this.isbklongtou = isbklongtou1; 
	}
	
	public void setStockQuanZhong (Integer quanzhong)
	{
		this.quanzhong = quanzhong; 
		
	}
	public Stock getStock ()
	{
		return this.stock;
	}
	public BanKuai getBanKuai ()
	{
		return this.bankuai;
	}
	public Integer getQuanZhong ()
	{
		return this.quanzhong;
	}
//	@Override
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.asinglestockinfo.Stock#getNodeXPeroidData(java.lang.String)
	 */
//	public NodeXPeriodDataBasic getNodeXPeroidData(String period) {
//		
//		if(period.equals(StockGivenPeriodDataItem.MONTH))
//			return (StockOfBanKuaiNodeXPeriodData) super.nodemonthdata;
//		else if(period.equals(StockGivenPeriodDataItem.WEEK))
//			return (StockOfBanKuaiNodeXPeriodData)super.nodewkdata;
//		else 
//			return null;
//		
////		NodeXPeriodDataBasic nodexdata = getStockXPeriodDataForBanKuai (period);
////		return nodexdata;
//	}
	
//	private NodeXPeriodDataBasic getStockXPeriodDataForBanKuai (String period)
//	{
//		if(period.equals(StockGivenPeriodDataItem.MONTH))
//			return (StockOfBanKuaiNodeXPeriodData) super.nodemonthdata;
//		else if(period.equals(StockGivenPeriodDataItem.WEEK))
//			return super.nodewkdata;
//		else 
//			return null;
//	}
	
	/*
	 * 
	 */
	public class StockOfBanKuaiTreeRelated extends TreeRelated
	{
		public StockOfBanKuaiTreeRelated (BkChanYeLianTreeNode treenode1)
		{
			super(treenode1);
		}
		
		private HashMap<LocalDate,Boolean> isinparsedfile;
		public void setStocksNumInParsedFile (LocalDate parsefiledate, Boolean isin)
		{
			if(isinparsedfile == null) {
				isinparsedfile = new HashMap<LocalDate,Boolean> ();
				LocalDate friday = parsefiledate.with(DayOfWeek.FRIDAY);
				if(isin)
					isinparsedfile.put(friday, isin);
			} else {
				LocalDate friday = parsefiledate.with(DayOfWeek.FRIDAY);
				if(isin)
					isinparsedfile.put(friday, isin);
			}
		}
		public Boolean isInBanKuaiFengXiResultFileForSpecificDate (LocalDate requiredate)
		{
			LocalDate friday = requiredate.with(DayOfWeek.FRIDAY);
			try {
				Boolean result = isinparsedfile.get(friday);
				return result;
			} catch(java.lang.NullPointerException e) {
				return null;
			}
		}
		
	}
	
	public class StockOfBanKuaiNodeXPeriodData extends NodeXPeriodData
	{
		public StockOfBanKuaiNodeXPeriodData (String nodeperiodtype1) 
		{
			super(nodeperiodtype1);
		}
	//		@Override
		/*
		 * (non-Javadoc)
		 * @see com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData#getChenJiaoErChangeGrowthRateOfSuperBanKuai(java.time.LocalDate)
		 * �������ú�����ͬ�ĵط����������ǰ��ĳɽ��������Ǵ��̵ĳɽ���
		 */
		public Double getChenJiaoErChangeGrowthRateOfSuperBanKuai(LocalDate requireddate,int difference) 
		{
				if(stockohlc == null)
					return null;
				
				TimeSeriesDataItem curcjlrecord = this.stockamo.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
				if( curcjlrecord == null) 
					return null;
				
				//�ж��ϼ����(���̻��߰��)�Ƿ�����,������û�бȽϵ����壬ֱ�ӷ���-100��
				String nodept = getNodeperiodtype();
				NodeXPeriodDataBasic bkxdata = bankuai.getNodeXPeroidData(nodept);
				Double bkcjediff = bkxdata.getChengJiaoErDifferenceWithLastPeriod(requireddate,difference);
				if( bkcjediff == null || bkcjediff < 0   ) {//���������
					return -100.0;
				}
				
				int index = difference-1;
				while ( ((DaPan)getRoot()).isDaPanXiuShi(requireddate, index ,getNodeperiodtype()) ) { //���ܿ��ܴ�������
					index --;
				}
				
				TimeSeriesDataItem lastcjlrecord = stockamo.getDataItem( getJFreeChartFormateTimePeriod (requireddate,index) );
				if(lastcjlrecord == null) { //����ǰ���ǿգ�˵����ͣ�ƺ�����
					Double curggcje = curcjlrecord.getValue().doubleValue(); //�°�����гɽ�����Ӧ�ü�����
					return curggcje/bkcjediff;
				}
				
				Double curcje = curcjlrecord.getValue().doubleValue();
				Double lastcje = lastcjlrecord.getValue().doubleValue();
				Double cjechange = curcje - lastcje; //���ɳɽ����ı仯
				
				return cjechange/bkcjediff;
		}
		@Override
		public Integer getChenJiaoErMaxWeekOfSuperBanKuai(LocalDate requireddate, int difference) {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public Integer getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(LocalDate requireddate, int difference) {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public Integer getChenJiaoErZhanBiMinWeekOfSuperBanKuai(LocalDate requireddate, int difference) {
			// TODO Auto-generated method stub
			return null;
		}
	}
}