package com.exchangeinfomanager.asinglestockinfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.swing.tree.*;

import org.apache.log4j.Logger;
import org.jfree.data.ComparableObjectItem;
import org.jfree.data.time.Day;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;

import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;



public abstract class BkChanYeLianTreeNode  extends InvisibleNode implements  BanKuaiAndStockBasic
{

    public BkChanYeLianTreeNode(String myowncode, String name)
    {
		super(myowncode);

		if(name != null)
			this.myname = name;
		else 
			name = "";
        
        //节点汉语拼音
    	HanYuPinYing hypy = new HanYuPinYing ();
    	hanyupingyin = new ArrayList<String> ();
   		String codehypy = hypy.getBanKuaiNameOfPinYin(myowncode); 
   		String namehypy = hypy.getBanKuaiNameOfPinYin(name );
   		hanyupingyin.add(codehypy);
   		hanyupingyin.add(namehypy);
   		
   		//NodeJiBenMian  TreeRelated
   		this.nodejbm = new BkChanYeLianTreeNode.NodeJiBenMian ();
  }
	
    private Logger logger = Logger.getLogger(BkChanYeLianTreeNode.class);
	private ArrayList<String> hanyupingyin; //汉语拼音
    protected int nodetype;
	private String myname; 
	private String suoshujiaoyisuo;
	
	private NodeJiBenMian nodejbm;
	protected TreeRelated nodetreerelated;
	protected NodeXPeriodDataBasic nodewkdata;
	protected NodeXPeriodDataBasic nodedaydata;
	protected NodeXPeriodDataBasic nodemonthdata;
	
	public String getSuoShuJiaoYiSuo ()
	{
		return this.suoshujiaoyisuo;
	}
	public void setSuoShuJiaoYiSuo (String jys)
	{
		this.suoshujiaoyisuo = jys;
	}

	public NodeJiBenMian getNodeJiBenMian ()
	{
		return this.nodejbm;
	}

	public TreeRelated getNodeTreerelated ()
	{
		return this.nodetreerelated;
	}
  
    public boolean checktHanYuPingYin (String nameorhypy)
    {
    	if(Strings.isNullOrEmpty(nameorhypy) )
    		return false;
    	boolean found = false;

    	try {
    		for(String parthypy: this.hanyupingyin)
        		if(parthypy.equals(nameorhypy)) {
        			found = true;
        			return found;
        		}
    	} catch (java.lang.NullPointerException ex) {
        	logger.debug(this.getUserObject().toString()+ "拼音是NULL" );
    	}
    	
    	return found;
    	
    }
    	@Override
	public int getType() {
		return this.nodetype;
	}
	
	@Override
	public String getMyOwnName() {
		// TODO Auto-generated method stub
		return this.myname;
	}
	@Override
	public void setMyOwnName(String bankuainame) {
		this.myname = bankuainame;
		
	}
	@Override
	public String getMyOwnCode() {
		// TODO Auto-generated method stub
		return this.getUserObject().toString().trim();
	}
	@Override
	public String getCreatedTime() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setCreatedTime(String bankuaicreatedtime) {
		// TODO Auto-generated method stub
		
	}
	/*
	 * 
	 */
//	public abstract NodeXPeriodDataBasic getNodeXPeroidData (String period);
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
	/*
	 * 和产业链树相关的计算
	 */
	public class TreeRelated
	{
			public TreeRelated (BkChanYeLianTreeNode treenode1)
			{
				treenode = treenode1; 
				expanded = false;
		        shouldberemovedwhensavexml = false;
			}
			
			private BkChanYeLianTreeNode treenode;
			private boolean expanded;
			private boolean isofficallyselected ;
			private int inzdgzofficalcount =0;
			private int inzdgzcandidatecount =0;
			private LocalDate selectedToZdgzTime;
			private LocalDate addedtocyltreetime;
			private boolean shouldberemovedwhensavexml;
			
			public void setShouldBeRemovedWhenSaveXml ( ) 
			{
				this.shouldberemovedwhensavexml = true;
			}
			public boolean shouldBeRemovedWhenSaveXml ( )
			{
				return this.shouldberemovedwhensavexml;
			}

		    public void setExpansion(boolean expanded){
		        this.expanded = expanded;
		    }
		    
		    public boolean isExpanded(){
		        return expanded;
		    }
		    
		    public void increaseZdgzOfficalCount ()
		    {
		    	inzdgzofficalcount ++;
//		    	logger.debug(this.getUserObject().toString() + "node offical count = " + inzdgzofficalcount);
		    }
		    public void decreaseZdgzOfficalCount ()
		    {
		    	inzdgzofficalcount --;
		    	if(inzdgzofficalcount == 0 )
		    		isofficallyselected = false;
//		    	logger.debug(this.getUserObject().toString() + "node offical count = " + inzdgzofficalcount);
		    }
		    
		    public void increaseZdgzCandidateCount ()
		    {
		    	inzdgzcandidatecount ++;
//		    	logger.debug(this.getUserObject().toString() +"node cand count = " + inzdgzcandidatecount);
		    }
		    public void decreasedgzCandidateCount ()
		    {
		    	inzdgzcandidatecount --;
//		    	logger.debug(this.getUserObject().toString() + "node cand count = " + inzdgzcandidatecount);
		    }
		    public int getInZdgzOfficalCount ()
		    {
		    	return inzdgzofficalcount;
		    	
		    }
		    public int getInZdgzCandidateCount ()
		    {
		    	return inzdgzcandidatecount; 
		    }
		    public void setOfficallySelected (boolean ofslt)
			{
				this.isofficallyselected = ofslt;
			}
			public boolean isOfficallySelected ()
			{
				return this.isofficallyselected;
			}
			
			/**
			 * @return the tdxbk
			 */
			public String getChanYeLianSuoShuTdxBanKuaiName() 
			{
				TreeNode[] nodepath = treenode.getPath();
				String tdxbk =	 ((BkChanYeLianTreeNode)nodepath[1]).getUserObject().toString() ;
				return tdxbk;
			}

			/**
			 * @return the node在当前树的位置，也就是产业链，以code返回
			 */
			public String getNodeCurLocatedChanYeLian() {
				 String bkchanyelianincode = "";
				 TreeNode[] nodepath = treenode.getPath();
				 for(int i=1;i<nodepath.length;i++) {
					 bkchanyelianincode = bkchanyelianincode + ((BkChanYeLianTreeNode)nodepath[i]).getMyOwnCode() + "->";
				 }

				return bkchanyelianincode.substring(0,bkchanyelianincode.length()-2);
			}
			/*
			 * 产业链保存的是code,这里返回名字
			 */
			public String getNodeCurLocatedChanYeLianByName ()
			{
				String bkchanyelianinname = "";
				 TreeNode[] nodepath = treenode.getPath();
				 for(int i=1;i<nodepath.length;i++) {
					 bkchanyelianinname = bkchanyelianinname + ((BkChanYeLianTreeNode)nodepath[i]).getMyOwnName() + "->";
				 }

				return bkchanyelianinname.substring(0,bkchanyelianinname.length()-2);
			}
			/**
			 * @return the selectedtime
			 */
			public String getSelectedToZdgzTime() {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM-dd");
				return selectedToZdgzTime.format(formatter);
			}
			/**
			 * @param selectedtime the selectedtime to set
			 */
			public void setSelectedToZdgzTime(LocalDate selectedtime) {
				this.selectedToZdgzTime = selectedtime;
			}
			public void setSelectedToZdgzTime(String selectedtime2) 
			{
//				SimpleDateFormat formatterhwy=new SimpleDateFormat("yy-MM-dd");
//				Date date = new Date() ;
//				try {
//					date = formatterhwy.parse(selectedtime2);
//				} catch (ParseException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				
				this.selectedToZdgzTime = CommonUtility.formateStringToDate(selectedtime2);
			}
			public void setAddedToCylTreeTime (String addedtime2)
			{
				this.addedtocyltreetime	 = CommonUtility.formateStringToDate(addedtime2);
			}
		
		}
	 /*
	  * 基本面信息
	  */
	 public class NodeJiBenMian 
	 {
			public NodeJiBenMian ()
			{
//				nodejbm = this;
//				this.myowncode = nodecode;
			}
			
			private String myowncode;
			private LocalDate gainiantishidate;
			private String gainiantishi;
			private LocalDate quanshangpingjidate;
			private String quanshangpingji;
			private LocalDate fumianxiaoxidate;
			private String fumianxiaoxi;
			private String zhengxiangguan;
			private String fuxiangguan;
			private String jingZhengDuiShou;
			private String keHuCustom;
			private String suoshujiaoyisuo;
			private Object[][] zdgzmrmcykRecords ;
			
			public String getMyowncode() {
				return myowncode;
			}

			public LocalDate getGainiantishidate() {
				return gainiantishidate;
			}
			public void setGainiantishidate(LocalDate gainiantishidate) {
				this.gainiantishidate = gainiantishidate;
			}
			public String getGainiantishi() {
				return gainiantishi;
			}
			public void setGainiantishi(String gainiantishi) {
				this.gainiantishi = gainiantishi;
			}
			public LocalDate getQuanshangpingjidate() {
				return quanshangpingjidate;
			}
			public void setQuanshangpingjidate(LocalDate quanshangpingjidate) {
				this.quanshangpingjidate = quanshangpingjidate;
			}
			public String getQuanshangpingji() {
				return quanshangpingji;
			}
			public void setQuanshangpingji(String quanshangpingji) {
				this.quanshangpingji = quanshangpingji;
			}
			public LocalDate getFumianxiaoxidate() {
				return fumianxiaoxidate;
			}
			public void setFumianxiaoxidate(LocalDate fumianxiaoxidate) {
				this.fumianxiaoxidate = fumianxiaoxidate;
			}
			public String getFumianxiaoxi() {
				return fumianxiaoxi;
			}
			public void setFumianxiaoxi(String fumianxiaoxi) {
				this.fumianxiaoxi = fumianxiaoxi;
			}
			public String getZhengxiangguan() {
				return zhengxiangguan;
			}
			public void setZhengxiangguan(String zhengxiangguan) {
				this.zhengxiangguan = zhengxiangguan;
			}
			public String getFuxiangguan() {
				return fuxiangguan;
			}
			public void setFuxiangguan(String fuxiangguan) {
				this.fuxiangguan = fuxiangguan;
			}
			public String getJingZhengDuiShou() {
				return jingZhengDuiShou;
			}
			public void setJingZhengDuiShou(String jingZhengDuiShou) {
				this.jingZhengDuiShou = jingZhengDuiShou;
			}
			public String getKeHuCustom() {
				return keHuCustom;
			}
			public void setKeHuCustom(String keHuCustom) {
				this.keHuCustom = keHuCustom;
			}
			public String getSuoshujiaoyisuo() {
				return suoshujiaoyisuo;
			}
			public void setSuoshujiaoyisuo(String suoshujiaoyisuo) {
				this.suoshujiaoyisuo = suoshujiaoyisuo;
			}
			public Object[][] getZdgzmrmcykRecords() {
				return zdgzmrmcykRecords;
			}
			public void setZdgzmrmcykRecords(Object[][] zdgzmrmcykRecords) {
				this.zdgzmrmcykRecords = zdgzmrmcykRecords;
			}
			/**
			 * @return the zdgzmrmcykRecords
			 */
			public Object[][] getZdgzMrmcZdgzYingKuiRecords() {
				return zdgzmrmcykRecords;
			}
		}
	 
		/*
		 * 某个时间周期记录的相关计算
		 */
		 public abstract class NodeXPeriodData implements NodeXPeriodDataBasic
		 {
			public NodeXPeriodData (String nodeperiodtype1)
			{
				this.nodeperiodtype = nodeperiodtype1;
				stockohlc = new OHLCSeries(nodeperiodtype1);
				stockamo = new TimeSeries(nodeperiodtype1);
				stockvol = new TimeSeries(nodeperiodtype1);;
				stockamozhanbi = new TimeSeries(nodeperiodtype1);
				stockvolzhanbi = new TimeSeries(nodeperiodtype1);
				stockfxjg = new TimeSeries(nodeperiodtype1);
				stockexchangedaysnumber = new TimeSeries(nodeperiodtype1);
			}

			private String nodeperiodtype;
			protected OHLCSeries stockohlc; //每日成交的OHLC
			protected TimeSeries stockamo; //成交额
			protected TimeSeries stockvol; //成交量
			protected TimeSeries stockamozhanbi; //成交额占比
			protected TimeSeries stockvolzhanbi; //成交量占比
			protected TimeSeries stockfxjg; //分析结果
			protected TimeSeries stockexchangedaysnumber ; //周期的交易日，如每周有几天交易，只保存不是5天的，
	
			public void addNewXPeriodData (StockGivenPeriodDataItem kdata)
			{
				try {
					stockohlc.setNotify(false);
					stockohlc.add(kdata);
				} catch (org.jfree.data.general.SeriesException e) {
					logger.debug(kdata.getMyOwnCode() + kdata.getPeriod() + "数据已经存在（" + kdata.getPeriod().getStart() + "," + kdata.getPeriod().getEnd() + ")");
				}
				
				try {
					stockamo.setNotify(false);
					stockamo.add(kdata.getPeriod(),kdata.getMyOwnChengJiaoEr(),false);
//					stockvol.add(kdata.getPeriod(),kdata.getMyownchengjiaoliang(),false);
					if(kdata.getCjeZhanBi() != null) {
						stockamozhanbi.setNotify(false);
						stockamozhanbi.add(kdata.getPeriod(),kdata.getCjeZhanBi(),false);
					}
//					if(kdata.getCjlZhanBi() != null)
//						stockvolzhanbi.add(kdata.getPeriod(),kdata.getCjlZhanBi(),false);
				} catch (org.jfree.data.general.SeriesException e) {
					logger.debug(kdata.getMyOwnCode() + kdata.getPeriod() + "数据已经存在（" + kdata.getPeriod().getStart() + "," + kdata.getPeriod().getEnd() + ")");
//					e.printStackTrace();
				}
				
				try{
					stockexchangedaysnumber.setNotify(false);
					if(kdata.getExchangeDaysNumber() != null && kdata.getExchangeDaysNumber() != 5) //5天是默认的，完全不用存
						stockexchangedaysnumber.add(kdata.getPeriod(),kdata.getExchangeDaysNumber(),false);
					
				} catch (org.jfree.data.general.SeriesException e) {
					logger.debug(kdata.getMyOwnCode() + kdata.getPeriod() + "数据已经存在（" + kdata.getPeriod().getStart() + "," + kdata.getPeriod().getEnd() + ")");
//					e.printStackTrace();
				}
			}
			
			/*
			 * (non-Javadoc)
			 * @see com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic#hasFxjgInPeriod(java.time.LocalDate, int)
			 */
			public Boolean hasFxjgInPeriod (LocalDate requireddate,int difference)
			{
				TimeSeriesDataItem fxjgitem = stockfxjg.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
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
				try {
					stockfxjg.add(period,fxjg);
//					 addOrUpdate()
				} catch (org.jfree.data.general.SeriesException e) {
//					e.printStackTrace();
				}
			}
			/*
			 * 
			 */
			public OHLCSeries getOHLCData ()
			{
				return this.stockohlc;
			}
			/*
			 * 
			 */
			public OHLCSeries getRangeOHLCData (LocalDate requiredstart,LocalDate requiredend)
			{
				OHLCSeries tmpohlc = new OHLCSeries ("Kxian");
				int itemcount = this.stockohlc.getItemCount();
				for(int i=0;i<itemcount;i++) {
					RegularTimePeriod dataitemp = this.stockohlc.getPeriod(i);
					LocalDate startd = dataitemp.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					LocalDate endd = dataitemp.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					
					if( (startd.isAfter(requiredstart) || startd.equals(requiredstart) )  && ( endd.isBefore(requiredend) || endd.equals(requiredend) ))
						tmpohlc.add( (OHLCItem) this.stockohlc.getDataItem(i) );
				}
				
				return tmpohlc;
			}
			/*
			 * (non-Javadoc)
			 * @see com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic#getOHLCData(java.time.LocalDate, int)
			 */
			public OHLCItem getSpecificDateOHLCData (LocalDate requireddate,int difference)
			{
				int itemcount = this.stockohlc.getItemCount();
				for(int i=0;i<itemcount;i++) {
					RegularTimePeriod dataitemp = this.stockohlc.getPeriod(i);
					if(dataitemp.equals(this.getJFreeChartFormateTimePeriod(requireddate,0)) )
						return (OHLCItem) this.stockohlc.getDataItem(i);
				}
				
				return null;
			}
			/*
			 * (non-Javadoc)
			 * @see com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic#getNodeperiodtype()
			 */
			public String getNodeperiodtype() {
				return nodeperiodtype;
			}
			/*
			 * 
			 */
//			public TimeSeries getChengJiaoEr ()
//			{
//				return stockamo; //成交额
//			}
			/*
			 * 
			 */
//			private TimeSeries getRangeChengJiaoEr (LocalDate requiredstart,LocalDate requiredend)
//			{
//				RegularTimePeriod start = this.getJFreeChartFormateTimePeriod(requiredstart,0);
//				RegularTimePeriod end = this.getJFreeChartFormateTimePeriod(requiredend,0);
//				try {
//					return this.stockamo.createCopy( start,  end);
//				} catch (CloneNotSupportedException e) {
//					e.printStackTrace();
//					return null;
//				}
//			}
			/*
			 * 
			 */
			public TimeSeries getRangeChengJiaoErZhanBi (LocalDate requiredstart,LocalDate requiredend)
			{
				RegularTimePeriod start = this.getJFreeChartFormateTimePeriod(requiredstart,0);
				RegularTimePeriod end = this.getJFreeChartFormateTimePeriod(requiredend,0);
				try {
					return this.stockamozhanbi.createCopy( start,  end);
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
					return null;
				}
			} 
			/*
			 * 
			 */
			public Double getChenJiaoErZhanBi (LocalDate requireddate,int difference)
			{
				if(stockohlc == null)
					return null;
				
				TimeSeriesDataItem curcjlrecord = null;
//				if(difference >=0 )
					curcjlrecord = stockamozhanbi.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
				
				if( curcjlrecord == null) 
					return null;
				else
					return curcjlrecord.getValue().doubleValue();
			}
			/*
			 * 
			 */
			public Double getChengJiaoEr (LocalDate requireddate,int difference)
			{
				if(stockohlc == null)
					return null;
				
				TimeSeriesDataItem curcjlrecord = null;
//				if(difference >=0 )
					curcjlrecord = stockamo.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
				
				if( curcjlrecord == null) 
					return null;
				else
					return curcjlrecord.getValue().doubleValue();
			}
			/*
			 * (non-Javadoc)
			 * @see com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic#getRecordsStartDate()
			 */
			public LocalDate getRecordsStartDate ()
			{
				if(stockamo.getItemCount() == 0)
					return null;
//				
//				RegularTimePeriod firstperiod = stockohlc.getPeriod(0);
				RegularTimePeriod firstperiod = stockamo.getTimePeriod( 0);
				LocalDate startdate = firstperiod.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();;
				LocalDate enddate = firstperiod.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();;
			
				TemporalField fieldUS = WeekFields.of(Locale.US).dayOfWeek();
				LocalDate mondayday = startdate.with(fieldUS, 2);
				
				return mondayday;
			}
			/*
			 * 
			 */
			public LocalDate getRecordsEndDate ()
			{
				if(stockamo.getItemCount() == 0)
					return null;
				
//				int itemcount = stockohlc.getItemCount();
//				RegularTimePeriod firstperiod = stockohlc.getPeriod(itemcount-1);
				int itemcount = stockamo.getItemCount();
				RegularTimePeriod firstperiod = stockamo.getTimePeriod( itemcount - 1 );
				LocalDate startdate = firstperiod.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				LocalDate enddate = firstperiod.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();;
			
				LocalDate saturday = enddate.with(DayOfWeek.SATURDAY);
				return saturday;
			}
			/*
			 * (non-Javadoc)
			 * @see com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic#hasRecordInThePeriod(java.time.LocalDate, int)
			 */
			public Boolean hasRecordInThePeriod (LocalDate requireddate, int difference) 
			{
				if(stockohlc == null)
					return null;
			
				 TimeSeriesDataItem stockamovaule = stockamo.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference) );
				 if(stockamovaule != null)
					 return true;
				 else
					 return false;
			}
			
			/*
			 * 计算指定周期和上周期的成交额差额，适合stock/bankuai，dapan有自己的计算方法
			 */
			public Double getChengJiaoErDifferenceWithLastPeriod(LocalDate requireddate,int difference)
			{
				if(stockohlc == null)
					return null;
				
				TimeSeriesDataItem curcjlrecord = stockamo.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference) );
				if( curcjlrecord == null) 
					return null;
				
				int index = -1;
				DaPan dapan = (DaPan)getRoot();
				while ( dapan.isDaPanXiuShi(requireddate, index ,getNodeperiodtype()) && index >-1000 ) { //上周可能大盘修饰
					index --;
				}
				
				TimeSeriesDataItem lastcjlrecord = stockamo.getDataItem( getJFreeChartFormateTimePeriod (requireddate,index) );
				if(lastcjlrecord == null) //休市前还是空，说明要是新板块。板块没有停牌的
					return null;
				
				Double curcje = curcjlrecord.getValue().doubleValue();
				Double lastcje = lastcjlrecord.getValue().doubleValue();
				
				return curcje - lastcje;
			}
			/*
			 * 对上级板块的成交额占比增速
			 */
			public Double getChenJiaoErZhanBiGrowthRateOfSuperBanKuai(LocalDate requireddate,int difference) 
			{
				if(stockohlc == null)
					return null;
				
				TimeSeriesDataItem curcjlrecord = this.stockamozhanbi.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
				if( curcjlrecord == null) 
					return null;
				
				int index = -1;
				DaPan dapan = (DaPan)getRoot();
				while ( dapan.isDaPanXiuShi(requireddate, index ,getNodeperiodtype()) && index >-1000) { //上周可能大盘修饰 
					index --;
				}
				
				TimeSeriesDataItem lastcjlrecord = stockamozhanbi.getDataItem( getJFreeChartFormateTimePeriod (requireddate,index) );
				if(lastcjlrecord == null) { //休市前还是空，说明要是新板块。板块没有停牌的
					logger.debug(getMyOwnCode() + getMyOwnName() + "可能是一个新个股或板块");
					return 100.0;
				}
				
				Double curzhanbiratio = curcjlrecord.getValue().doubleValue();
				Double lastzhanbiratio = lastcjlrecord.getValue().doubleValue();
				Double zhanbigrowthrate = (curzhanbiratio - lastzhanbiratio)/lastzhanbiratio;
				logger.debug(getMyOwnCode() + getMyOwnName() + "占比增速" + requireddate.toString() + zhanbigrowthrate);
				
				return zhanbigrowthrate;
			}
			/*
			 * 
			 */
			protected RegularTimePeriod getJFreeChartFormateTimePeriod (LocalDate requireddate,int difference) 
			{
				String nodeperiod = getNodeperiodtype();
				LocalDate expectedate = null;
				RegularTimePeriod period = null;
				if(nodeperiod.equals(StockGivenPeriodDataItem.WEEK)) { //如果是周线数据，只要数据周相同，即可返回
					expectedate = requireddate.plus(difference,ChronoUnit.WEEKS);
					period = new org.jfree.data.time.Week (Date.from(expectedate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
				} else if(nodeperiod.equals(StockGivenPeriodDataItem.DAY)) { //如果是日线数据，必须相同才可返回
					expectedate = requireddate.plus(difference,ChronoUnit.DAYS);
					period = new org.jfree.data.time.Day(Date.from(expectedate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
				}  else if(nodeperiod.equals(StockGivenPeriodDataItem.MONTH)) { //如果是日线数据，必须相同才可返回
					expectedate = requireddate.plus(difference,ChronoUnit.MONTHS);
				}
				
				return period;
			}
			@Override
			public abstract Integer getChenJiaoErMaxWeekOfSuperBanKuai(LocalDate requireddate,int difference) ;
			/*
			 * 对上级板块的成交额占比是多少周内的最大值
			 */
			public abstract Integer getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(LocalDate requireddate,int difference);
			/*
			 * 
			 */
			public abstract Integer getChenJiaoErZhanBiMinWeekOfSuperBanKuai(LocalDate requireddate,int difference);
			/*
			 * 计算成交额变化贡献率，即板块成交额的变化占整个上级板块成交额增长量的比率
			 */
			public Double getChenJiaoErChangeGrowthRateOfSuperBanKuai (LocalDate requireddate,int difference) 
			{
				if(stockohlc == null)
					return null;
				
				TimeSeriesDataItem curcjlrecord = this.stockamo.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
				if( curcjlrecord == null) 
					return null;
				
				//判断上级板块(大盘或者板块)是否缩量,所以了没有比较的意义，直接返回-100；
				String nodept = getNodeperiodtype();
				Double dpcjediff = ((DaPan)getRoot()).getNodeXPeroidData(nodept).getChengJiaoErDifferenceWithLastPeriod(requireddate,difference);
				if( dpcjediff<0 || dpcjediff == null ) {//大盘缩量，
					return -100.0;
				}
				
				int index = -1;
				while ( ((DaPan)getRoot()).isDaPanXiuShi(requireddate, index ,getNodeperiodtype()) && index >-1000 ) { //前面周可能大盘修饰，-1000是给一个查询的边界，大盘或板块不可能休市超过1000周
					index --;
				}
				
				TimeSeriesDataItem lastcjlrecord = stockamo.getDataItem( getJFreeChartFormateTimePeriod (requireddate,index) );
				if(lastcjlrecord == null) { //休市前还是空，说明要是新板块。板块没有停牌的
					Double curggcje = curcjlrecord.getValue().doubleValue(); //新板块所有成交量都应该计算入
					return curggcje/dpcjediff;
				}
				
				Double curcje = curcjlrecord.getValue().doubleValue();
				Double lastcje = lastcjlrecord.getValue().doubleValue();
				Double cjechange = curcje - lastcje; //个股成交量的变化
				
				return cjechange/dpcjediff;
			}
			
			 /*
			  * 计算指定周有几个交易日
			  */
			 public Integer getExchangeDaysNumberForthePeriod (LocalDate requireddate,int difference)
			 {
				 if(stockexchangedaysnumber == null)
					 return null;
				 
				 TimeSeriesDataItem curdaynumberrecord = this.stockexchangedaysnumber.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
				 if( curdaynumberrecord == null) 
						return 5;
				 else
					 return curdaynumberrecord.getValue().intValue();
				 
			 }
			 
		 } //END OF 
		
		 
		 
}


