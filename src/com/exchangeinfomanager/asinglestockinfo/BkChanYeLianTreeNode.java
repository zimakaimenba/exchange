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

import com.exchangeinfomanager.bankuaichanyelian.HanYuPinYing;
import com.exchangeinfomanager.bankuaifengxi.ChenJiaoZhanBiInGivenPeriod;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;



public class BkChanYeLianTreeNode  extends DefaultMutableTreeNode implements  BanKuaiAndStockBasic
{

    public BkChanYeLianTreeNode(String myowncode, String name   )
    {
		super(myowncode);
		if(name != null)
			this.myname = name;
		else 
			name = "";
        expanded = false;
        shouldberemovedwhensavexml = false;
        
    	HanYuPinYing hypy = new HanYuPinYing ();
    	hanyupingyin = new ArrayList<String> ();
   		String codehypy = hypy.getBanKuaiNameOfPinYin(myowncode); 
   		String namehypy = hypy.getBanKuaiNameOfPinYin(name );
   		hanyupingyin.add(codehypy);
   		hanyupingyin.add(namehypy);
   		
	}
	
    private static Logger logger = Logger.getLogger(BkChanYeLianTreeNode.class);
	private String suoshutdxbkzscode; //所属通达信板块指数代码
	private ArrayList<String> hanyupingyin; //汉语拼音
    protected int nodetype;
	private String myname; 
	private boolean expanded;
	protected HashSet<String> parsefilestockset; //板块解析文件内含有的该板块列表
	private boolean isofficallyselected ;
	private int inzdgzofficalcount =0;
	private int inzdgzcandidatecount =0;
	private LocalDate selectedToZdgzTime;
	private LocalDate addedtocyltreetime;
	private boolean shouldberemovedwhensavexml;
	
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
	
	
	protected ArrayList<ChenJiaoZhanBiInGivenPeriod> wkcjeperiodlist; //板块自己的周成交记录以及一些分析结果
	protected ArrayList<ChenJiaoZhanBiInGivenPeriod> daycjeperiodlist; //板块自己日成交记录以及一些分析结果
//	private BanKuaiDbOperation bkdbopt;
	

	public String getSuoShuJiaoYiSuo ()
	{
		return this.suoshujiaoyisuo;
	}
	public void setSuoShuJiaoYiSuo (String jys)
	{
		this.suoshujiaoyisuo = jys;
	}
	
	
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
    public void increaseZdgzOfficalCount ()
    {
    	inzdgzofficalcount ++;
//    	logger.debug(this.getUserObject().toString() + "node offical count = " + inzdgzofficalcount);
    }
    public void decreaseZdgzOfficalCount ()
    {
    	inzdgzofficalcount --;
    	if(inzdgzofficalcount == 0 )
    		isofficallyselected = false;
//    	logger.debug(this.getUserObject().toString() + "node offical count = " + inzdgzofficalcount);
    }
    
    public void increaseZdgzCandidateCount ()
    {
    	inzdgzcandidatecount ++;
//    	logger.debug(this.getUserObject().toString() +"node cand count = " + inzdgzcandidatecount);
    }
    public void decreasedgzCandidateCount ()
    {
    	inzdgzcandidatecount --;
//    	logger.debug(this.getUserObject().toString() + "node cand count = " + inzdgzcandidatecount);
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
		TreeNode[] nodepath = this.getPath();
		String tdxbk =	 ((BkChanYeLianTreeNode)nodepath[1]).getUserObject().toString() ;
		return tdxbk;
	}

	/**
	 * @return the node在当前树的位置，也就是产业链，以code返回
	 */
	public String getNodeCurLocatedChanYeLian() {
		 String bkchanyelianincode = "";
		 TreeNode[] nodepath = this.getPath();
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
		 TreeNode[] nodepath = this.getPath();
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
//		SimpleDateFormat formatterhwy=new SimpleDateFormat("yy-MM-dd");
//		Date date = new Date() ;
//		try {
//			date = formatterhwy.parse(selectedtime2);
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
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
	
	/**
	 * @return the jingZhengDuiShou
	 */
	public String getJingZhengDuiShou() {
		return jingZhengDuiShou;
	}
	/**
	 * @param jingZhengDuiShou the jingZhengDuiShou to set
	 */
	public void setJingZhengDuiShou(String jingZhengDuiShou) {
		this.jingZhengDuiShou = jingZhengDuiShou;
	}
	/**
	 * @return the keHuCustom
	 */
	public String getKeHuCustom() {
		return keHuCustom;
	}
	/**
	 * @param keHuCustom the keHuCustom to set
	 */
	public void setKeHuCustom(String keHuCustom) {
		this.keHuCustom = keHuCustom;
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
	/**
	 * @param zdgzmrmcykRecords the zdgzmrmcykRecords to set
	 */
	public void setZdgzmrmcykRecords(Object[][] zdgzmrmcykRecords) {
		this.zdgzmrmcykRecords = zdgzmrmcykRecords;
	}
	/**
	 * @return the zdgzmrmcykRecords
	 */
	public Object[][] getZdgzMrmcZdgzYingKuiRecords() {
		return zdgzmrmcykRecords;
	}

	/*
	 * 获取日线数据
	 */
	public ArrayList<ChenJiaoZhanBiInGivenPeriod> getDayChenJiaoErZhanBiInGivenPeriod ()
	{
		return this.daycjeperiodlist;
	}
	public void setDayChenJiaoErZhanBiInGivenPeriod (ArrayList<ChenJiaoZhanBiInGivenPeriod> daylist)
	{
		this.daycjeperiodlist = daylist;
	}
 	//和成交量相关的函数
	/*
	 * 获取周线数据
	 */
	public ArrayList<ChenJiaoZhanBiInGivenPeriod> getWkChenJiaoErZhanBiInGivenPeriod ()
	{
		return this.wkcjeperiodlist;
	}
	/*
	 * 只能在头尾加，不允许在中间加 
	 */
	public boolean addChenJiaoErZhanBiInGivenPeriod (ArrayList<ChenJiaoZhanBiInGivenPeriod> cjlperiodlist1,LocalDate position)
	{
		if(cjlperiodlist1.isEmpty())
			return false;
		
		if(this.wkcjeperiodlist == null || this.wkcjeperiodlist.isEmpty() ) {
			this.wkcjeperiodlist = cjlperiodlist1;
			return true;
		}
		else {
			ChenJiaoZhanBiInGivenPeriod firstrecord = this.wkcjeperiodlist.get(0);
			LocalDate firstday = firstrecord.getRecordsDayofEndofWeek();
			if(position.isBefore(firstday) || position.isEqual(firstday)) {
//				logger.debug("add before" + this.wkcjeperiodlist.size());
				this.wkcjeperiodlist.addAll(0, cjlperiodlist1);
//				logger.debug("add after" + this.wkcjeperiodlist.size());
				return true;
			}
			
			ChenJiaoZhanBiInGivenPeriod lastrecord = this.wkcjeperiodlist.get(this.wkcjeperiodlist.size()-1);
			LocalDate lastday = lastrecord.getRecordsDayofEndofWeek();
			if(position.isAfter(lastday) || position.isEqual(lastday)) {
				this.wkcjeperiodlist.addAll(this.wkcjeperiodlist.size()-1, cjlperiodlist1);
				return true;
			}
			
		}
		
		return false;
	}
	/*
	 * 
	 */
	public LocalDate getRecordsStartDate ()
	{
		ChenJiaoZhanBiInGivenPeriod tmprecords;
		try {
			tmprecords = this.wkcjeperiodlist.get(0);
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
			tmprecords = this.wkcjeperiodlist.get(this.wkcjeperiodlist.size()-1);
		} catch (java.lang.NullPointerException e) {
			return null;
		} catch (java.lang.IndexOutOfBoundsException e) {
			return null;
		}
		
		LocalDate enddate = tmprecords.getRecordsDayofEndofWeek().with(DayOfWeek.SATURDAY);
		return enddate;
	}
	/*
	 * 在交易记录中找到对应周的位置
	 */
	protected Integer getRequiredRecordsPostion (LocalDate requireddate)
	{
		int year = requireddate.getYear();
		WeekFields weekFields = WeekFields.of(Locale.getDefault()); 
		int weeknumber = requireddate.get(weekFields.weekOfWeekBasedYear());
		
		Integer index = -1;
		Boolean found = false;
		for(ChenJiaoZhanBiInGivenPeriod tmpcjzb : this.wkcjeperiodlist) {
			index ++;
			int yearnum = tmpcjzb.getRecordsYear();
			int wknum = tmpcjzb.getRecordsWeek();
			if(wknum == weeknumber && yearnum == year) {
				found = true;
				break;
			}
		}
		if(!found)
			return null;
		else
			return index;
	}
	/*
	 * 获得指定周的记录,适合stock/bankuai，dapan有自己的计算方法
	 */
	public ChenJiaoZhanBiInGivenPeriod getSpecficChenJiaoErRecord (LocalDate requireddate)
	{
		if(wkcjeperiodlist == null)
			return null;
		
		Integer index = this.getRequiredRecordsPostion (requireddate);
		if( index != null) {
			ChenJiaoZhanBiInGivenPeriod curcjlrecord = wkcjeperiodlist.get(index);
			return curcjlrecord;
		} else
			return null;
	}
	/*
	 * 计算指定周和上周的成交量差额，适合stock/bankuai，dapan有自己的计算方法
	 */
	public Double getChengJiaoErDifferenceOfLastWeek (LocalDate requireddate)
	{
		Integer index = this.getRequiredRecordsPostion (requireddate);
		if( index != null) {
				ChenJiaoZhanBiInGivenPeriod curcjlrecord = wkcjeperiodlist.get(index);
				ChenJiaoZhanBiInGivenPeriod lastcjlrecord = null;
				try {
					 lastcjlrecord = wkcjeperiodlist.get(index -1 );
					 
					 Double curcje = curcjlrecord.getMyOwnChengJiaoEr();
					 Double lastcje = lastcjlrecord.getMyOwnChengJiaoEr();
					 
					 return curcje - lastcje; 
				} catch (java.lang.ArrayIndexOutOfBoundsException e) { 		//新的板块或个股//只有一个记录，说明是新的板块
//						logger.debug(this.getMyOwnCode() + this.getMyOwnName() + "可能是一个新个股或板块");
						return null;
				}
		}
		return null;
	}
	
}


