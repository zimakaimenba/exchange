package com.exchangeinfomanager.asinglestockinfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.tree.*;

import com.exchangeinfomanager.bankuaichanyelian.HanYuPinYing;
import com.exchangeinfomanager.bankuaifengxi.ChenJiaoZhanBiInGivenPeriod;
import com.exchangeinfomanager.commonlib.CommonUtility;
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
	
	private String suoshutdxbkzscode; //所属通达信板块指数代码
	private ArrayList<String> hanyupingyin; //汉语拼音
    protected int nodetype;
	private String myname; 
	private boolean expanded;
	protected HashSet<String> parsefilestockset; //板块解析文件内含有的该板块列表
	private boolean isofficallyselected ;
	private int inzdgzofficalcount =0;
	private int inzdgzcandidatecount =0;
	private Date selectedToZdgzTime;
	private Date addedtocyltreetime;
	private boolean shouldberemovedwhensavexml;
	
	private Date gainiantishidate;
	private String gainiantishi;
	private Date quanshangpingjidate;
	private String quanshangpingji;
	private Date fumianxiaoxidate;
	private String fumianxiaoxi;
	private String zhengxiangguan;
	private String fuxiangguan;
	private String jingZhengDuiShou;
	private String keHuCustom;
	private String suoshujiaoyisuo;
	
	private ArrayList<ChenJiaoZhanBiInGivenPeriod> cjeperiodlist;
	
	
	
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
//    	this.parsefilestockset = parsefilestockset2;
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
        	System.out.println(this.getUserObject().toString()+ "拼音是NULL" );
    	}
    	
    	return found;
    	
    }
    public void increaseZdgzOfficalCount ()
    {
    	inzdgzofficalcount ++;
    	System.out.println(this.getUserObject().toString() + "node offical count = " + inzdgzofficalcount);
    }
    public void decreaseZdgzOfficalCount ()
    {
    	inzdgzofficalcount --;
    	if(inzdgzofficalcount == 0 )
    		isofficallyselected = false;
    	System.out.println(this.getUserObject().toString() + "node offical count = " + inzdgzofficalcount);
    }
    
    public void increaseZdgzCandidateCount ()
    {
    	inzdgzcandidatecount ++;
    	System.out.println(this.getUserObject().toString() +"node cand count = " + inzdgzcandidatecount);
    }
    public void decreasedgzCandidateCount ()
    {
    	inzdgzcandidatecount --;
    	System.out.println(this.getUserObject().toString() + "node cand count = " + inzdgzcandidatecount);
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
		SimpleDateFormat formatterhwy=new SimpleDateFormat("yy-MM-dd");
		return formatterhwy.format(selectedToZdgzTime);
	}
	/**
	 * @param selectedtime the selectedtime to set
	 */
	public void setSelectedToZdgzTime(Date selectedtime) {
		this.selectedToZdgzTime = selectedtime;
	}
	public void setSelectedToZdgzTime(String selectedtime2) 
	{
		SimpleDateFormat formatterhwy=new SimpleDateFormat("yy-MM-dd");
		Date date = new Date() ;
		try {
			date = formatterhwy.parse(selectedtime2);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.selectedToZdgzTime = date;
	}
	public void setAddedToCylTreeTime (String addedtime2)
	{
		this.addedtocyltreetime	 = CommonUtility.formateDateYYMMDD(addedtime2);
		
	}

	public void clearCurParseFileStockSet() {
		if(this.parsefilestockset != null)
			this.parsefilestockset.clear();
	}
	@Override
	public int getType() {
		return this.nodetype;
	}
	
//	public void setSuoSuTdxBanKuai(String tdxbkcode) {
//		this.suoshutdxbkzscode = tdxbkcode;
//	}
//	@Override
//	public String getChanYeLianSuoSuTdxBanKuai() {
//		// TODO Auto-generated method stub
//		return this.suoshutdxbkzscode;
//	}
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

	public Date getGainiantishidate() {
		return gainiantishidate;
	}
	public void setGainiantishidate(Date gainiantishidate) {
		this.gainiantishidate = gainiantishidate;
	}
	public String getGainiantishi() {
		return gainiantishi;
	}
	public void setGainiantishi(String gainiantishi) {
		this.gainiantishi = gainiantishi;
	}
	public Date getQuanshangpingjidate() {
		return quanshangpingjidate;
	}
	public void setQuanshangpingjidate(Date quanshangpingjidate) {
		this.quanshangpingjidate = quanshangpingjidate;
	}
	public String getQuanshangpingji() {
		return quanshangpingji;
	}
	public void setQuanshangpingji(String quanshangpingji) {
		this.quanshangpingji = quanshangpingji;
	}
	public Date getFumianxiaoxidate() {
		return fumianxiaoxidate;
	}
	public void setFumianxiaoxidate(Date fumianxiaoxidate) {
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

	public ArrayList<ChenJiaoZhanBiInGivenPeriod> getChenJiaoErZhanBiInGivenPeriod ()
	{
		return this.cjeperiodlist;
	}
	public void setChenJiaoErZhanBiInGivenPeriod (ArrayList<ChenJiaoZhanBiInGivenPeriod> cjlperiodlist1)
	{
		this.cjeperiodlist = cjlperiodlist1;
	}
	/*
	 * 计算成交额变化百分比
	 */
	public Double getChenJiaoErGrowthRateForAGivenPeriod (int weeknumber)
	{
		if(cjeperiodlist == null)
			return null;
		
		int index = -1;
		boolean foundwk = false;
		for(ChenJiaoZhanBiInGivenPeriod tmpcjzb : cjeperiodlist) {
			index ++;
			int wknum = CommonUtility.getWeekNumber( tmpcjzb.getDayofEndofWeek() );
			if(wknum == weeknumber) {
				foundwk = true;
				break;
			}
		}
		
		if(foundwk) {
			if( cjeperiodlist.size()>=2) {
				ChenJiaoZhanBiInGivenPeriod curcjlrecord = cjeperiodlist.get(index);
				ChenJiaoZhanBiInGivenPeriod lastcjlrecord = cjeperiodlist.get(index -1 );
				Double curcje = curcjlrecord.getMyOwnChengJiaoEr();
				Double lastcje = lastcjlrecord.getMyOwnChengJiaoEr();
				double cjegrowthrate = (curcje - lastcje)/lastcje;
				return cjegrowthrate;
			} else if(cjeperiodlist.size() == 1) { //只有一个记录，说明是新的板块
				return 10000.0;
			}	
		}
		
		return 0.0;
		
	}
	/*
	 * 计算成交额变化贡献率
	 */
	public Double getChenJiaoErChangeGrowthRateForAGivenPeriod (int weeknumber)
	{
		if(cjeperiodlist == null)
			return null;
		
		int index = -1;
		boolean foundwk = false;
		for(ChenJiaoZhanBiInGivenPeriod tmpcjzb : cjeperiodlist) {
			index ++;
			int wknum = CommonUtility.getWeekNumber( tmpcjzb.getDayofEndofWeek() );
			if(wknum == weeknumber) {
				foundwk = true;
				break;
			}
		}
		
		if(foundwk) {
			if( cjeperiodlist.size()>=2) {
				ChenJiaoZhanBiInGivenPeriod curcjlrecord = cjeperiodlist.get(index);
				ChenJiaoZhanBiInGivenPeriod lastcjlrecord = cjeperiodlist.get(index -1 );
				
				Double curupbkcje = curcjlrecord.getUpLevelChengJiaoEr ();
				Double lastupbkcje = lastcjlrecord.getUpLevelChengJiaoEr ();
				if(curupbkcje < lastupbkcje)
					return -100.0;
				
				Double curggcje = curcjlrecord.getMyOwnChengJiaoEr();
				Double lastggcje = lastcjlrecord.getMyOwnChengJiaoEr();
				
				Double bkcjechange = curupbkcje - lastupbkcje;
				Double gegucjechange = curggcje - lastggcje;
				
				return gegucjechange/bkcjechange;
			} else if(cjeperiodlist.size() == 1) { //只有一个记录，说明是新的板块
				return 10000.0;
			}	
		}
		
		return 0.0;
		
	}
	
	/*
	 * 计算给定周的成交额占比增速
	 */
	public Double getChenJiaoLiangZhanBiGrowthRateForAGivenPeriod (int weeknumber)
	{
//		ChenJiaoZhanBiInGivenPeriod foundedcje = null;
		if(cjeperiodlist == null)
			return null;
		
		int index = -1;
		boolean foundwk = false;
		for(ChenJiaoZhanBiInGivenPeriod tmpcjzb : cjeperiodlist) {
			index ++;
			int wknum = CommonUtility.getWeekNumber( tmpcjzb.getDayofEndofWeek() );
			if(wknum == weeknumber) {
				foundwk = true;
				break;
			}
		}
		
		if(foundwk) {
			if( cjeperiodlist.size()>=2) {
				ChenJiaoZhanBiInGivenPeriod curcjlrecord = cjeperiodlist.get(index);
				ChenJiaoZhanBiInGivenPeriod lastcjlrecord = cjeperiodlist.get(index -1 );
				Double curzhanbiratio = curcjlrecord.getCjlZhanBi();
				Double lastzhanbiratio = lastcjlrecord.getCjlZhanBi();
				double zhanbigrowthrate = (curzhanbiratio - lastzhanbiratio)/lastzhanbiratio;
				return zhanbigrowthrate;
			} else if(cjeperiodlist.size() == 1) { //只有一个记录，说明是新的板块
				return 10000.0;
			}	
		}
		
		return 0.0;
		
	}
	/*
	 * 计算给定周的成交额占比是多少周期内的最大占比
	 */
	public Integer getChenJiaoLiangZhanBiMaxWeekForAGivenPeriod (int weeknumber)
	{
//		ChenJiaoZhanBiInGivenPeriod foundedcje = null;
		if(cjeperiodlist == null)
			return null;
		
		int index = -1;
		boolean foundwk = false;
		for(ChenJiaoZhanBiInGivenPeriod tmpcjzb : cjeperiodlist) {
			index ++;
			int wknum = CommonUtility.getWeekNumber( tmpcjzb.getDayofEndofWeek() );
			if(wknum == weeknumber) {
				foundwk = true;
				break;
			}
		}
		
		int maxweek = 0;
		if(foundwk) {
			
			ChenJiaoZhanBiInGivenPeriod curcjlrecord = cjeperiodlist.get(index);
			Double curzhanbiratio = curcjlrecord.getCjlZhanBi();
			
			for(int i = index -1;i>=0;i--) {
				ChenJiaoZhanBiInGivenPeriod lastcjlrecord = cjeperiodlist.get(i );
				Double lastzhanbiratio = lastcjlrecord.getCjlZhanBi();
				if(curzhanbiratio > lastzhanbiratio)
					maxweek ++;
				else
					break;
			}
		}
		
		return maxweek;
		
	}
	
    
}


