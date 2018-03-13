package com.exchangeinfomanager.asinglestockinfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
import org.jfree.data.time.Day;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;

import com.exchangeinfomanager.bankuaichanyelian.HanYuPinYing;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;



public abstract class BkChanYeLianTreeNode  extends DefaultMutableTreeNode implements  BanKuaiAndStockBasic
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
   		this.nodetreerelated = new BkChanYeLianTreeNode.TreeRelated (this);
   		
  }
	
    private static Logger logger = Logger.getLogger(BkChanYeLianTreeNode.class);
	private ArrayList<String> hanyupingyin; //汉语拼音
    protected int nodetype;
	private String myname; 
	private String suoshujiaoyisuo;
	
	private NodeJiBenMian nodejbm;
	private TreeRelated nodetreerelated;
	protected NodeXPeriodDataBasic nodewkdata;
	protected NodeXPeriodDataBasic nodedaydata;
	protected NodeXPeriodDataBasic nodemonthdata;
	
	
//	protected ArrayList<ChenJiaoZhanBiInGivenPeriod> wkcjeperiodlist; //板块自己的周成交记录以及一些分析结果
	
	

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

	public TreeRelated getNodetreerelated ()
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
	public abstract NodeXPeriodDataBasic getNodeXPeroidData (String period);
	
	/*
	 * 和产业链树相关的计算
	 */
	public class TreeRelated
	{
			public TreeRelated (BkChanYeLianTreeNode treenode1)
			{
				nodetreerelated = this;
				
				treenode = treenode1; 
				expanded = false;
		        shouldberemovedwhensavexml = false;
			}
			
			private BkChanYeLianTreeNode treenode;
			private boolean expanded;
			protected HashSet<String> parsefilestockset; //板块解析文件内含有的该板块列表
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
			

			//子类里面有具体实现
		    public  void setParseFileStockSet (HashSet<String> parsefilestockset2)
		    {
		    	if(this.parsefilestockset == null) {
		    		this.parsefilestockset = new HashSet<String> ();
		    		this.parsefilestockset = parsefilestockset2;
		    	} else
		    		this.parsefilestockset = parsefilestockset2;
		    }
		    public HashSet<String> getParseFileStockSet ()
		    {
		    	if(this.parsefilestockset == null)
		    		return new HashSet<String> ();
		    	else
		    		return this.parsefilestockset;
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

			public void clearCurParseFileStockSet() {
				if(this.parsefilestockset != null)
					this.parsefilestockset.clear();
			}


			
		}
	 /*
	  * 基本面信息
	  */
	 public class NodeJiBenMian 
	 {
			public NodeJiBenMian ()
			{
				nodejbm = this;
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
				stockgivenperioddataseries = new OHLCSeries(nodeperiodtype1);
				
//				stockgivenperioddataseries.get
			}

			private String nodeperiodtype;
//			protected ArrayList<ChenJiaoZhanBiInGivenPeriod> periodlist; //板块自己的成交记录以及一些分析结果
			
			protected OHLCSeries stockgivenperioddataseries; //板块自己的成交记录以及一些分析结果
			
			public String getNodeperiodtype() {
				return nodeperiodtype;
			}
			public void addNewXPeriodData (LocalDate data,Double open,Double high,Double low,Double close)
			{
				int year = data.getYear();
				int month = data.getMonthValue();
				int day = data.getDayOfMonth();
				OHLCItem kdata = new OHLCItem(new Day(day,month,year), open, high, low, close);
				stockgivenperioddataseries.add(kdata);
			}
			
//			public void setNodeperiodtype(String nodeperiodtype) {
//				this.nodeperiodtype = nodeperiodtype;
//			}
		
			//和成交量相关的函数
//			public void setNodePeriodRecords (ArrayList<ChenJiaoZhanBiInGivenPeriod> periodlist1)
//			{
//				 this.periodlist = periodlist1;
//			}
			/*
			 * 获取数据
			 */
//			protected ArrayList<ChenJiaoZhanBiInGivenPeriod> getNodePeriodRecords ()
//			{
//				return this.periodlist;
//			}
			/*
			 * 只能在头尾加，不允许在中间加 
			 */
//			public boolean addRecordsForAGivenPeriod (ArrayList<ChenJiaoZhanBiInGivenPeriod> periodlist1,LocalDate position)
//			{
//				if(periodlist1.isEmpty())
//					return false;
//				
//				if(this.periodlist == null || this.periodlist.isEmpty() ) {
//					this.periodlist = periodlist1;
//					return true;
//				}
//				else {
//					ChenJiaoZhanBiInGivenPeriod firstrecord = this.periodlist.get(0);
//					LocalDate firstday = firstrecord.getRecordsDayofEndofWeek();
//					if(position.isBefore(firstday) || position.isEqual(firstday)) {
////						logger.debug("add before" + this.wkcjeperiodlist.size());
//						this.periodlist.addAll(0, periodlist1);
////						logger.debug("add after" + this.wkcjeperiodlist.size());
//						return true;
//					}
//					
//					ChenJiaoZhanBiInGivenPeriod lastrecord = this.periodlist.get(this.periodlist.size()-1);
//					LocalDate lastday = lastrecord.getRecordsDayofEndofWeek();
//					if(position.isAfter(lastday) || position.isEqual(lastday)) {
//						this.periodlist.addAll(this.periodlist.size()-1, periodlist1);
//						return true;
//					}
//					
//				}
//				
//				return false;
//			}

			public LocalDate getRecordsStartDate ()
			{
				ChenJiaoZhanBiInGivenPeriod tmprecords;
				try {
					tmprecords = this.periodlist.get(0);
				} catch (java.lang.NullPointerException e) {
					return null;
				} catch (java.lang.IndexOutOfBoundsException e) {
					return null;
				}
				
				LocalDate startdate = tmprecords.getRecordsDayofEndofWeek();
				LocalDate mondayday = startdate.with(DayOfWeek.MONDAY);
				
				return mondayday;
			}
			/*
			 * 
			 */
			public LocalDate getRecordsEndDate ()
			{
				ChenJiaoZhanBiInGivenPeriod tmprecords;
				try {
					tmprecords = this.periodlist.get(this.periodlist.size()-1);
				} catch (java.lang.NullPointerException e) {
					return null;
				} catch (java.lang.IndexOutOfBoundsException e) {
					return null;
				}
				
				LocalDate enddate = tmprecords.getRecordsDayofEndofWeek().with(DayOfWeek.SATURDAY);
				return enddate;
			}
			/*
			 * 在交易记录中找到对应周/日的位置,difference是偏移量，
			 */
//			protected Integer getRequiredRecordsPostion (LocalDate requireddate,int difference)
//			{
//				Integer index = -1;
//				Boolean found = false;
//				
//				ChenJiaoZhanBiInGivenPeriod expectedrecord = this.getSpecficRecord(requireddate, difference);
////				expectedrecord.getRecordsDayofEndofWeek();
//				
//				for(ChenJiaoZhanBiInGivenPeriod tmpcjzb : this.periodlist) {
//					if(tmpcjzb.getRecordsType().toUpperCase().equals(ChenJiaoZhanBiInGivenPeriod.WEEK)) { //如果是周线数据，只要数据周相同，即可返回
//						index ++;
//						
//						int year = expectedrecord.getRecordsYear();
//						int weeknumber = expectedrecord.getRecordsWeek();
//						
//						int yearnum = tmpcjzb.getRecordsYear();
//						int wknum = tmpcjzb.getRecordsWeek();
//						
//						if(wknum == weeknumber && yearnum == year) {
//							found = true;
//							break;
//						}
//					} else if(tmpcjzb.getRecordsType().toUpperCase().equals(ChenJiaoZhanBiInGivenPeriod.DAY)) { //如果是日线数据，必须相同才可返回
//						index ++;
//						LocalDate recordsday = tmpcjzb.getRecordsDayofEndofWeek();
//						if(recordsday.isEqual(requireddate)) {
//							found = true;
//							break;
//						}
//					}
//					
//				}
//				if(!found)
//					return null;
//				else
//					return index;
//			}

			@Override
			
			/*
			 * 获得指定周的记录
			 */
			public ChenJiaoZhanBiInGivenPeriod getSpecficRecord(LocalDate requireddate, int difference) 
			{
				if(periodlist == null)
					return null;

				String nodeperiod = getNodeperiodtype();
				LocalDate expectedate = null;
				if(nodeperiod.equals(ChenJiaoZhanBiInGivenPeriod.WEEK)) { //如果是周线数据，只要数据周相同，即可返回
					expectedate = requireddate.plus(difference,ChronoUnit.WEEKS);
				} else if(nodeperiod.equals(ChenJiaoZhanBiInGivenPeriod.DAY)) { //如果是日线数据，必须相同才可返回
					expectedate = requireddate.plus(difference,ChronoUnit.DAYS);
				}  else if(nodeperiod.equals(ChenJiaoZhanBiInGivenPeriod.MONTH)) { //如果是日线数据，必须相同才可返回
					expectedate = requireddate.plus(difference,ChronoUnit.MONTHS);
				}
				
				int index = -1;
				ChenJiaoZhanBiInGivenPeriod foundrecord = null;
				for(ChenJiaoZhanBiInGivenPeriod tmpcjzb : periodlist) {
					if(nodeperiod.equals(ChenJiaoZhanBiInGivenPeriod.MONTH)) { //如果是周线数据，只要数据周相同，即可返回
						index ++;
						
						int year = expectedate.getYear();
						int month = expectedate.getMonthValue();
						
						int yearnum = tmpcjzb.getRecordsYear();
						int monthnum = tmpcjzb.getRecordsMonth();
						
						if(month == monthnum && yearnum == year) {
							foundrecord = tmpcjzb;
							break;
						}
					} else	if(nodeperiod.equals(ChenJiaoZhanBiInGivenPeriod.WEEK)) { //如果是周线数据，只要数据周相同，即可返回
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
					} else if(nodeperiod.equals(ChenJiaoZhanBiInGivenPeriod.DAY)) { //如果是日线数据，必须相同才可返回
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

			/*
			 * 计算指定周期和上周期的成交额差额，适合stock/bankuai，dapan有自己的计算方法
			 */
			public Double getChengJiaoErDifferenceWithLastPeriod(LocalDate requireddate)
			{
				if(periodlist == null)
					return null;
				
				ChenJiaoZhanBiInGivenPeriod curcjlrecord = this.getSpecficRecord (requireddate,0);
				if( curcjlrecord == null) 
					return null;
				
				int index = -1;
				while ( ((DaPan)getRoot()).isDaPanXiuShi(requireddate, index ,getNodeperiodtype()) ) { //上周可能大盘修饰
					index --;
				}
				
				ChenJiaoZhanBiInGivenPeriod lastcjlrecord = this.getSpecficRecord (requireddate,index);
				if(lastcjlrecord == null) //休市前还是空，说明要是新板块。板块没有停牌的
					return null;
				
				Double curcje = curcjlrecord.getMyOwnChengJiaoEr();
				Double lastcje = lastcjlrecord.getMyOwnChengJiaoEr();
				
				curcjlrecord.setGgBkCjeDifferenceWithLastPeriod (curcje - lastcje);
				return curcje - lastcje;
			}

			/*
			 * 对上级板块的成交额占比增速
			 */
			public Double getChenJiaoErZhanBiGrowthRateOfSuperBanKuai(LocalDate requireddate) 
			{
				if(periodlist == null)
					return null;
				
				ChenJiaoZhanBiInGivenPeriod curcjlrecord = this.getSpecficRecord (requireddate,0);
				if( curcjlrecord == null) 
					return null;
				
				int index = -1;
				while ( ((DaPan)getRoot()).isDaPanXiuShi(requireddate, index ,getNodeperiodtype()) ) { //上周可能大盘修饰
					index --;
				}
				
				ChenJiaoZhanBiInGivenPeriod lastcjlrecord = this.getSpecficRecord (requireddate,index);
				if(lastcjlrecord == null) { //休市前还是空，说明要是新板块。板块没有停牌的
					logger.debug(getMyOwnCode() + getMyOwnName() + "可能是一个新个股或板块");
					return 100.0;
				}
				
				Double curzhanbiratio = curcjlrecord.getCjeZhanBi();
				Double lastzhanbiratio = lastcjlrecord.getCjeZhanBi();
				Double zhanbigrowthrate = (curzhanbiratio - lastzhanbiratio)/lastzhanbiratio;
				logger.debug(getMyOwnCode() + getMyOwnName() + "占比增速" + requireddate.toString() + zhanbigrowthrate);
				
				curcjlrecord.setGgBkCjeZhanbiGrowthRate(zhanbigrowthrate);
				return zhanbigrowthrate;
			}

			@Override
			public abstract Integer getChenJiaoErMaxWeekOfSuperBanKuai(LocalDate requireddate) ;
			/*
			 * 对上级板块的成交额占比是多少周内的最大值
			 */
			public abstract Integer getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(LocalDate requireddate); 
			/*
			 * 计算成交额变化贡献率，即板块成交额的变化占整个上级板块成交额增长量的比率
			 */
			public abstract Double getChenJiaoErChangeGrowthRateOfSuperBanKuai (LocalDate requireddate);
			
			/*
			 * 一次性计算所有数据
			 */
//			public abstract ChenJiaoZhanBiInGivenPeriod getNodeFengXiResultForSpecificDate (LocalDate requireddate);
		 }
		 
		 
}


