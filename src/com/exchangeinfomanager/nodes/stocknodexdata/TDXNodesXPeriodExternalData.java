package com.exchangeinfomanager.nodes.stocknodexdata;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.ta4j.core.Bar;

import com.exchangeinfomanager.bankuaifengxi.QueKou;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;

public abstract class TDXNodesXPeriodExternalData implements NodeXPeriodData 
{
	private Logger logger = Logger.getLogger(TDXNodesXPeriodExternalData.class);
	
	public TDXNodesXPeriodExternalData (String nodecode,String nodeperiodtype1)
	{
		this.nodecode = nodecode;
		this.nodeperiodtype = nodeperiodtype1;
		
		nodeamozhanbi = new TimeSeries(nodeperiodtype1);
		nodevolzhanbi = new TimeSeries(nodeperiodtype1);
		
		nodeexchangedaysnumber = new TimeSeries(nodeperiodtype1);
	}
	
	private String nodecode;
	private String nodeperiodtype;

	protected TimeSeries nodeamozhanbi; 
	protected TimeSeries nodevolzhanbi; 
	protected TimeSeries nodefxjg; //
	protected TimeSeries nodeexchangedaysnumber ; //
	 // ȱ�ڵ�ͳ�ƽ������ֱ�Ӵ��ȱ�ڵ�ԭ�����Ѿ���ohlc�ˣ����Լ����ȱ�ڣ������ר�Ŵ��ȱ��list�е��ظ���������ȱ��ͳ������
	protected TimeSeries nodeqktjopenup;
	protected TimeSeries nodeqktjhuibuup;
	protected TimeSeries nodeqktjopendown;
	protected TimeSeries nodeqktjhuibudown;
	protected List<QueKou> qklist;
	
	protected TimeSeries nodezhangtingnum;
	protected TimeSeries nodedietingnum;
	private LocalDate firstdayinhistory;
	
	public void addNewXPeriodData (NodeGivenPeriodDataItem kdata)
	{
		if(kdata.getNodeToDpChenJiaoErZhanbi() == null) 
			return ;
		
		try {
			nodeamozhanbi.setNotify(false);
			nodeamozhanbi.add(kdata.getJFreeChartPeriod(this.nodeperiodtype),kdata.getNodeToDpChenJiaoErZhanbi(),false);
			
			nodevolzhanbi.setNotify(false);
			nodevolzhanbi.add(kdata.getJFreeChartPeriod(this.nodeperiodtype),kdata.getNodeToDpChenJiaoLiangZhanbi(),false);
		} catch (org.jfree.data.general.SeriesException e) {
		}
		
		try{
			nodeexchangedaysnumber.setNotify(false);
			if(kdata.getExchangeDaysNumber() != null && kdata.getExchangeDaysNumber() != 5) //
				nodeexchangedaysnumber.add(kdata.getJFreeChartPeriod(this.nodeperiodtype),kdata.getExchangeDaysNumber(),false);
			
		} catch (org.jfree.data.general.SeriesException e) {
		}
		
		try {
			if(nodezhangtingnum == null)
				nodezhangtingnum = new TimeSeries(this.nodeperiodtype);
			if(nodedietingnum == null)
				nodedietingnum = new TimeSeries(this.nodeperiodtype);
			
			if(kdata.getZhangTingNumber() != null && kdata.getZhangTingNumber() !=0 )
				nodezhangtingnum.add(kdata.getJFreeChartPeriod(this.nodeperiodtype), kdata.getZhangTingNumber() );
			
			if(kdata.getDieTingNumber() != null && kdata.getDieTingNumber() !=0 )
				nodedietingnum.add(kdata.getJFreeChartPeriod(this.nodeperiodtype), kdata.getDieTingNumber() );
			
		} catch (org.jfree.data.general.SeriesException e) {
		}
	}
	
	/*
	 * �ж������Ƿ������ݿ���ʷ��¼�ĵ�һ��
	 */
	public Boolean isLocalDateReachFristDayInHistory (LocalDate requireddate,int difference)
	{
		if(this.firstdayinhistory == null)
			return null;
		
		if(this.nodeperiodtype.equals(NodeGivenPeriodDataItem.DAY) ) {
			if(requireddate.equals(this.firstdayinhistory))
				return true;
			else
				return false;
		} else if(this.nodeperiodtype.equals(NodeGivenPeriodDataItem.WEEK) ) {
			Boolean checkresult = CommonUtility.isInSameWeek(requireddate, this.firstdayinhistory);
			return checkresult;
		}
		
		return null;
	}
	public void setShangShiRiQi (LocalDate requireddate)
	{
		this.firstdayinhistory = requireddate;
	}
	/*
	 * 
	 */
	public String getNodeCode ()
	{
		return this.nodecode;
	}
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic#getNodeperiodtype()
	 */
	public String getNodeperiodtype() {
		return nodeperiodtype;
	}
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic#hasFxjgInPeriod(java.time.LocalDate, int)
	 */
	public Boolean hasFxjgInPeriod (LocalDate requireddate,int difference)
	{
		if(nodefxjg == null)
			return false;
		
		TimeSeriesDataItem fxjgitem = nodefxjg.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
		if(fxjgitem == null)
			return false;
		else
			 return true;
	}
	/*
	 * 
	 */
	public void addFxjgToPeriod (RegularTimePeriod period,Integer fxjg)
	{
		if(nodefxjg == null)
			nodefxjg = new TimeSeries(this.nodeperiodtype);
		
		try {
			nodefxjg.add(period,fxjg);
//			 addOrUpdate()
		} catch (org.jfree.data.general.SeriesException e) {
//			e.printStackTrace();
		}
	}
	/*
	 * 
	 */
	public void resetQueKouTongJiJieGuo ()
	{
		if(nodeqktjopenup != null)
			nodeqktjopenup.clear();
		if(nodeqktjhuibuup != null)
			nodeqktjhuibuup.clear();
		if(nodeqktjopendown != null)
			nodeqktjopendown.clear();
		if(nodeqktjhuibudown != null)
			nodeqktjhuibudown.clear();
	}
	/*
	 * 
	 */
	public void addZhangDieTingTongJiJieGuo (LocalDate tjdate,Integer nodeztnum,Integer nodedtnum,Boolean addbasedoncurdata)
	{
		if( this.nodezhangtingnum == null)
			this.nodezhangtingnum = new TimeSeries(this.nodeperiodtype);
		
		if( this.nodedietingnum == null)
			this.nodedietingnum = new TimeSeries(this.nodeperiodtype);
		
		RegularTimePeriod period = getJFreeChartFormateTimePeriod(tjdate,0);
		try {
			if(nodeztnum != null  )
			if(addbasedoncurdata) {
				TimeSeriesDataItem dataitem = this.nodezhangtingnum.getDataItem(period);
				if(dataitem == null) 
					nodezhangtingnum.add(period,nodeztnum);
				else {
					int count = dataitem.getValue().intValue();
					nodezhangtingnum.addOrUpdate(period, count + nodeztnum);
				}
			} else
				nodezhangtingnum.addOrUpdate(period,nodeztnum);

		} catch (org.jfree.data.general.SeriesException e) {
			e.printStackTrace();
		}
		
		try {
			if(nodedtnum != null  )
			if(addbasedoncurdata) {
				TimeSeriesDataItem dataitem = this.nodedietingnum.getDataItem(period);
				if(dataitem == null) 
					nodedietingnum.add(period,nodedtnum);
				else {
					int count = dataitem.getValue().intValue();
					nodedietingnum.addOrUpdate(period, count + nodedtnum);
				}
			} else
				nodedietingnum.addOrUpdate(period,nodedtnum);

		} catch (org.jfree.data.general.SeriesException e) {
			e.printStackTrace();
		}
	}
	/*
	 * ����addbasedoncurdata��true�����е�ֵ�ϸ��£�false��������ֵ
	 */
	public void addQueKouTongJiJieGuo (LocalDate qkdate,
			Integer nodeqktjopenup1,Integer nodeqktjhuibuup1,Integer nodeqktjopendown1,Integer nodeqktjhuibudown1,
			Boolean addbasedoncurdata)
	{
		
		if( nodeqktjopenup == null)
			nodeqktjopenup = new TimeSeries(this.nodeperiodtype);
		
		if( nodeqktjhuibuup == null)
			nodeqktjhuibuup = new TimeSeries(this.nodeperiodtype);
		
		if( nodeqktjopendown == null)
			nodeqktjopendown = new TimeSeries(this.nodeperiodtype);
		
		if( nodeqktjhuibudown == null)
			nodeqktjhuibudown = new TimeSeries(this.nodeperiodtype);
		
		RegularTimePeriod period = getJFreeChartFormateTimePeriod(qkdate,0);
		try {
			if(nodeqktjopenup1 != null  ) //nodeqktjopenup1���Ե���0���������ͳ��ȱ�ڵĿ�ʼ/����ʱ��
			if(addbasedoncurdata) {
				TimeSeriesDataItem dataitem = nodeqktjopenup.getDataItem(period);
				if(dataitem == null) 
					nodeqktjopenup.add(period,nodeqktjopenup1);
				else {
					int count = dataitem.getValue().intValue();
					nodeqktjopenup.addOrUpdate(period, count + nodeqktjopenup1);
				}
			} else
				nodeqktjopenup.addOrUpdate(period,nodeqktjopenup1);

		} catch (org.jfree.data.general.SeriesException e) {
			e.printStackTrace();
		}
		
		try {
			if(nodeqktjhuibuup1 != null && nodeqktjhuibuup1 !=0 )
			if(addbasedoncurdata) {
				TimeSeriesDataItem dataitem = nodeqktjhuibuup.getDataItem(period);
				if(dataitem == null)
					nodeqktjhuibuup.add(period,nodeqktjhuibuup1);
				else {
					int count = dataitem.getValue().intValue();
					nodeqktjhuibuup.addOrUpdate(period, count + nodeqktjhuibuup1);
				}
					
			} else
				nodeqktjhuibuup.addOrUpdate(period,nodeqktjhuibuup1);
		} catch (org.jfree.data.general.SeriesException e) {
			e.printStackTrace();
		}
		
		try {
			if(nodeqktjopendown1 != null && nodeqktjopendown1 !=0 )
			if(addbasedoncurdata) {
				TimeSeriesDataItem dataitem = nodeqktjopendown.getDataItem(period);
				if(dataitem == null)
					nodeqktjopendown.add(period,nodeqktjopendown1);
				else {
					int count = dataitem.getValue().intValue();
					nodeqktjopendown.addOrUpdate(period, count + nodeqktjopendown1);
				}
					
			} else
				nodeqktjopendown.addOrUpdate(period,nodeqktjopendown1);

		} catch (org.jfree.data.general.SeriesException e) {
			e.printStackTrace();
		}
		
		try {
			if(nodeqktjhuibudown1 != null && nodeqktjhuibudown1 !=0 )
			if(addbasedoncurdata) {
				TimeSeriesDataItem dataitem = nodeqktjhuibudown.getDataItem(period);
				if(dataitem == null)
					nodeqktjhuibudown.add(period,nodeqktjhuibudown1);
				else {
					int count = dataitem.getValue().intValue();
					nodeqktjhuibudown.addOrUpdate(period, count + 1);
				}
					
			} else
				nodeqktjhuibudown.addOrUpdate(period,nodeqktjhuibudown1);

		} catch (org.jfree.data.general.SeriesException e) {
			e.printStackTrace();
		}
	}
	public List<QueKou> getPeriodQueKou ()
	{
		return this.qklist;
	}
	public void setPeriodQueKou (List<QueKou> qkl)
	{
		this.qklist = qkl;
	}
	/*
	 * 
	 */
	public Integer getZhangTingTongJi (LocalDate requireddate, Integer difference)
	{
		if(this.nodezhangtingnum == null)
			return null;
		
		TimeSeriesDataItem zttjitem = nodezhangtingnum.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
		if(zttjitem == null)
			return null;
		else
			 return (Integer)zttjitem.getValue().intValue();			
					
	}
	public Integer getDieTingTongJi (LocalDate requireddate, Integer difference)
	{
		if(this.nodedietingnum == null)
			return null;
		
		TimeSeriesDataItem dttjitem = nodedietingnum.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
		if(dttjitem == null)
			return null;
		else
			 return (Integer)dttjitem.getValue().intValue();			
					
	}
	/*
	 * 
	 */
	public Integer getQueKouTongJiOpenUp (LocalDate requireddate, Integer difference)
	{
		if(this.nodeqktjopenup == null)
			return null;
		
		TimeSeriesDataItem qktjitem = nodeqktjopenup.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
		if(qktjitem == null)
			return null;
		else
			 return (Integer)qktjitem.getValue().intValue(); 
		
	}
	public Integer getQueKouTongJiOpenDown (LocalDate requireddate, Integer difference)
	{
		if(this.nodeqktjopendown == null)
			return null;
		
		TimeSeriesDataItem qktjitem = nodeqktjopendown.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
		if(qktjitem == null)
			return null;
		else
			 return (Integer)qktjitem.getValue().intValue(); 
		
	}
	public Integer getQueKouTongJiHuiBuUp (LocalDate requireddate, Integer difference)
	{
		if(this.nodeqktjhuibuup == null)
			return null;
		
		TimeSeriesDataItem qktjitem = nodeqktjhuibuup.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
		if(qktjitem == null)
			return null;
		else
			 return (Integer)qktjitem.getValue().intValue(); 
		
	}
	public Integer getQueKouTongJiHuiBuDown (LocalDate requireddate, Integer difference)
	{
		if(this.nodeqktjhuibudown == null)
			return null;
		
		TimeSeriesDataItem qktjitem = nodeqktjhuibudown.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
		if(qktjitem == null)
			return null;
		else
			 return qktjitem.getValue().intValue(); 
	}
	/*
	 * 
	 */
	public  LocalDate  getQueKouRecordsStartDate ()
	{ 
		if(nodeqktjopenup == null)
			return null;
		
		if(nodeqktjopenup.getItemCount() == 0)
			return null;

		RegularTimePeriod firstperiod = nodeqktjopenup.getTimePeriod( 0);
		LocalDate startdate = firstperiod.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();;
		LocalDate enddate = firstperiod.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();;
	
		if(this.nodeperiodtype == NodeGivenPeriodDataItem.WEEK) {
			TemporalField fieldUS = WeekFields.of(Locale.US).dayOfWeek();
			LocalDate mondayday = startdate.with(fieldUS, 2);
			return mondayday;
		} else if(this.nodeperiodtype == NodeGivenPeriodDataItem.DAY) {
			return startdate;
		}
		
		return null;
	}
	/*
	 * 
	 */
	public  LocalDate  getQueKouRecordsEndDate ()
	{ 
		if(nodeqktjopenup == null || nodeqktjopenup.getItemCount() == 0)
			return null;

		int itemcount = nodeqktjopenup.getItemCount();
		RegularTimePeriod firstperiod = nodeqktjopenup.getTimePeriod( itemcount - 1 );
		LocalDate startdate = firstperiod.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();;
		LocalDate enddate = firstperiod.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();;
	
		if(this.nodeperiodtype == NodeGivenPeriodDataItem.WEEK) {
			LocalDate saturday = enddate.with(DayOfWeek.SATURDAY);
			return saturday;
		} else if(this.nodeperiodtype == NodeGivenPeriodDataItem.DAY) {
			return enddate;
		}
		
		return null;
	}
	/*
	 * 
	 */
	public Double getChenJiaoErZhanBi (LocalDate requireddate,int difference)
	{
		if(nodeamozhanbi == null)
			return null;
		
		TimeSeriesDataItem curcjlrecord = null;
//		if(difference >=0 )
			curcjlrecord = nodeamozhanbi.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
		
		if( curcjlrecord == null) 
			return null;
		else
			return curcjlrecord.getValue().doubleValue();
	}
	/*
	 * 
	 */
	public Double getChenJiaoErZhanBiGrowthRateOfSuperBanKuai(LocalDate requireddate,int difference) 
	{
		RegularTimePeriod expectedperiod = getJFreeChartFormateTimePeriod(requireddate,difference);
		TimeSeriesDataItem curcjlrecord = this.nodeamozhanbi.getDataItem(expectedperiod );
		if( curcjlrecord == null) 
			return null;
		
		int index = this.nodeamozhanbi.getIndex(getJFreeChartFormateTimePeriod(requireddate,difference));
		
		try {
			TimeSeriesDataItem lastcjlrecord = nodeamozhanbi.getDataItem( index -1  );
			if(lastcjlrecord == null) { //����ǰ���ǿգ�˵��Ҫ���°�顣���û��ͣ�Ƶ�
				logger.debug(getNodeCode() + "������һ���¸��ɻ���");
				return 100.0;
			}
			
			Double curzhanbiratio = curcjlrecord.getValue().doubleValue();
			Double lastzhanbiratio = lastcjlrecord.getValue().doubleValue();
			Double zhanbigrowthrate = (curzhanbiratio - lastzhanbiratio)/lastzhanbiratio;
//			logger.debug(getNodeCode() + "ռ������" + requireddate.toString() + zhanbigrowthrate);
			
			return zhanbigrowthrate;
			
		} catch (java.lang.ArrayIndexOutOfBoundsException e) {
			return 100.0;
		}

	}
	/*
	 * ���ϼ����ĳɽ���ռ���Ƕ������ڵ����ֵ
	 */
	public Integer getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(LocalDate requireddate,int difference) 
	{
		TimeSeriesDataItem curcjlrecord = this.nodeamozhanbi.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
		if( curcjlrecord == null) 
			return null;
					
		Double curzhanbiratio = curcjlrecord.getValue().doubleValue();
		
		int index = nodeamozhanbi.getIndex(getJFreeChartFormateTimePeriod(requireddate,difference) );
		
		int maxweek = 0;
		for(int i = index - 1; i >= 0; i--) {
			
			
			TimeSeriesDataItem lastcjlrecord = nodeamozhanbi.getDataItem( i );
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
	/*
	 * 
	 */
	public Integer getChenJiaoErZhanBiMinWeekOfSuperBanKuai(LocalDate requireddate,int difference)
	{
		if(this.nodeamozhanbi == null)
			return null;
		
		TimeSeriesDataItem curcjlrecord = this.nodeamozhanbi.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
		if( curcjlrecord == null) 
			return null;

		Double curzhanbiratio = curcjlrecord.getValue().doubleValue();
		
		int index = nodeamozhanbi.getIndex(getJFreeChartFormateTimePeriod(requireddate,difference) );
		int minweek = 0;
		for(int i = index -1; i >= 0; i --) {
			TimeSeriesDataItem lastcjlrecord = nodeamozhanbi.getDataItem( i );
			if(lastcjlrecord == null ) //���ܵ��˼�¼��ͷ���ˣ������Ǹ�����ʱ�䲻���İ��
				return minweek;

			Double lastzhanbiratio = lastcjlrecord.getValue().doubleValue();
			if(curzhanbiratio < lastzhanbiratio)
				minweek ++;
			else
				break;
		}
		
		return minweek;
	}
	
	/*
	  * ����ָ�����м���������
	  */
	 public Integer getExchangeDaysNumberForthePeriod (LocalDate requireddate,int difference)
	 {
		 if(nodeexchangedaysnumber == null)
			 return null;
		 
		 TimeSeriesDataItem curdaynumberrecord = this.nodeexchangedaysnumber.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
		 if( curdaynumberrecord == null) 
				return 5;
		 else
			 return curdaynumberrecord.getValue().intValue();
		 
	 }
	 
	 /*
		 * 
		 */
		public Double getChenJiaoLiangZhanBiGrowthRateOfSuperBanKuai(LocalDate requireddate, int difference) 
		{
			TimeSeriesDataItem curcjlrecord = this.nodevolzhanbi.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
			if( curcjlrecord == null) 
				return null;
			
			int index = this.nodevolzhanbi.getIndex( getJFreeChartFormateTimePeriod(requireddate,difference));
			
			try {
				TimeSeriesDataItem lastcjlrecord = nodevolzhanbi.getDataItem( index -1  );
				if(lastcjlrecord == null) { //����ǰ���ǿգ�˵��Ҫ���°�顣���û��ͣ�Ƶ�
					logger.debug(getNodeCode() + "������һ���¸��ɻ���");
					return 100.0;
				}
				
				Double curzhanbiratio = curcjlrecord.getValue().doubleValue();
				Double lastzhanbiratio = lastcjlrecord.getValue().doubleValue();
				Double zhanbigrowthrate = (curzhanbiratio - lastzhanbiratio)/lastzhanbiratio;
				logger.debug(getNodeCode() + "ռ������" + requireddate.toString() + zhanbigrowthrate);
				
				return zhanbigrowthrate;
				
			} catch (java.lang.ArrayIndexOutOfBoundsException e) {
				return 100.0;
			}

		}
		/*
		 * 
		 */
		public Integer getChenJiaoLiangZhanBiMaxWeekOfSuperBanKuai(LocalDate requireddate, int difference) 
		{
			TimeSeriesDataItem curcjlrecord = this.nodevolzhanbi.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
			if( curcjlrecord == null) 
				return null;
						
			Double curzhanbiratio = curcjlrecord.getValue().doubleValue();
			
			int index = nodevolzhanbi.getIndex(getJFreeChartFormateTimePeriod(requireddate,difference) );
			
			int maxweek = 0;
			for(int i = index - 1; i >= 0; i--) {
				
				
				TimeSeriesDataItem lastcjlrecord = nodevolzhanbi.getDataItem( i );
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
		/*
		 * 
		 */
		public Integer getChenJiaoLiangZhanBiMinWeekOfSuperBanKuai(LocalDate requireddate, int difference) 
		{
			if(this.nodevolzhanbi == null)
				return null;
			
			TimeSeriesDataItem curcjlrecord = this.nodevolzhanbi.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
			if( curcjlrecord == null) 
				return null;

			Double curzhanbiratio = curcjlrecord.getValue().doubleValue();
			
			int index = nodevolzhanbi.getIndex(getJFreeChartFormateTimePeriod(requireddate,difference) );
			int minweek = 0;
			for(int i = index -1; i >= 0; i --) {
				TimeSeriesDataItem lastcjlrecord = nodevolzhanbi.getDataItem( i );
				if(lastcjlrecord == null ) //���ܵ��˼�¼��ͷ���ˣ������Ǹ�����ʱ�䲻���İ��
					return minweek;

				Double lastzhanbiratio = lastcjlrecord.getValue().doubleValue();
				if(curzhanbiratio < lastzhanbiratio)
					minweek ++;
				else
					break;
			}
			
			return minweek;
		}
		/*
		 * 
		 */
		public Double getChenJiaoLiangZhanBi(LocalDate requireddate, int difference) 
		{
			if(this.nodevolzhanbi == null)
				return null;
			
			TimeSeriesDataItem curcjlrecord = null;
			curcjlrecord = nodevolzhanbi.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
			
			if( curcjlrecord == null) 
				return null;
			else
				return curcjlrecord.getValue().doubleValue();
		}
		/*
		 * 
		 */
		protected RegularTimePeriod getJFreeChartFormateTimePeriod (LocalDate requireddate,int difference) 
		{
			String nodeperiod = this.getNodeperiodtype();
			LocalDate expectedate = null;
			RegularTimePeriod period = null;
			if(nodeperiod.equals(NodeGivenPeriodDataItem.WEEK)) { 
				expectedate = requireddate.plus(difference,ChronoUnit.WEEKS);
				java.sql.Date lastdayofweek = java.sql.Date.valueOf(expectedate);
				period = new org.jfree.data.time.Week (lastdayofweek);
//				period = new org.jfree.data.time.Week (Date.from(expectedate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
			} else if(nodeperiod.equals(NodeGivenPeriodDataItem.DAY)) {
				expectedate = requireddate.plus(difference,ChronoUnit.DAYS);
				java.sql.Date lastdayofweek = java.sql.Date.valueOf(expectedate);
				period = new org.jfree.data.time.Day (lastdayofweek);
//				period = new org.jfree.data.time.Day(Date.from(expectedate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
			}  else if(nodeperiod.equals(NodeGivenPeriodDataItem.MONTH)) {
				expectedate = requireddate.plus(difference,ChronoUnit.MONTHS);
			}
			
			return period;
		}
		 /*
		  * �û����������������/�յ����ڣ����תΪ���ܵ�����
		  */
		 protected LocalDate adjustDate(LocalDate dateneedtobeadjusted,int difference )
		 {
			 LocalDate friday;
			 LocalDate expectedate = dateneedtobeadjusted.plus(difference,ChronoUnit.DAYS);
			 DayOfWeek dayofweek = expectedate.getDayOfWeek();
			 if( dayofweek.equals(DayOfWeek.SUNDAY)  ) {
				friday = dateneedtobeadjusted.minus(2,ChronoUnit.DAYS);
				return friday;
			 } else if (  dayofweek.equals(DayOfWeek.SATURDAY)  ) {
				friday = dateneedtobeadjusted.minus(1,ChronoUnit.DAYS);
				return friday;
			 }
						
			return expectedate;
		 }

		 


}