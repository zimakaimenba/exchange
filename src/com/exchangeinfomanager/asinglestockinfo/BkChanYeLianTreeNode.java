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
        
        //�ڵ㺺��ƴ��
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
	private ArrayList<String> hanyupingyin; //����ƴ��
    protected int nodetype;
	private String myname; 
	private String suoshujiaoyisuo;
	
	private NodeJiBenMian nodejbm;
	private TreeRelated nodetreerelated;
	protected NodeXPeriodDataBasic nodewkdata;
	protected NodeXPeriodDataBasic nodedaydata;
	protected NodeXPeriodDataBasic nodemonthdata;
	
//	protected ArrayList<ChenJiaoZhanBiInGivenPeriod> wkcjeperiodlist; //����Լ����ܳɽ���¼�Լ�һЩ�������
	
	

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
        	logger.debug(this.getUserObject().toString()+ "ƴ����NULL" );
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
	 * �Ͳ�ҵ������صļ���
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
			protected HashSet<String> parsefilestockset; //�������ļ��ں��еĸð���б�
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
			

			//���������о���ʵ��
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
			 * @return the node�ڵ�ǰ����λ�ã�Ҳ���ǲ�ҵ������code����
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
			 * ��ҵ���������code,���ﷵ������
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
	  * ��������Ϣ
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
		 * ĳ��ʱ�����ڼ�¼����ؼ���
		 */
		 public abstract class NodeXPeriodData implements NodeXPeriodDataBasic
		 {
			public NodeXPeriodData (String nodeperiodtype1)
			{
				this.nodeperiodtype = nodeperiodtype1;
			}

			private String nodeperiodtype;
			protected ArrayList<ChenJiaoZhanBiInGivenPeriod> periodlist; //����Լ��ĳɽ���¼�Լ�һЩ�������
			
			public String getNodeperiodtype() {
				return nodeperiodtype;
			}
//			public void setNodeperiodtype(String nodeperiodtype) {
//				this.nodeperiodtype = nodeperiodtype;
//			}
		
			//�ͳɽ�����صĺ���
			public void setNodePeriodRecords (ArrayList<ChenJiaoZhanBiInGivenPeriod> periodlist1)
			{
				 this.periodlist = periodlist1;
			}
			/*
			 * ��ȡ����
			 */
//			protected ArrayList<ChenJiaoZhanBiInGivenPeriod> getNodePeriodRecords ()
//			{
//				return this.periodlist;
//			}
			/*
			 * ֻ����ͷβ�ӣ����������м�� 
			 */
			public boolean addRecordsForAGivenPeriod (ArrayList<ChenJiaoZhanBiInGivenPeriod> periodlist1,LocalDate position)
			{
				if(periodlist1.isEmpty())
					return false;
				
				if(this.periodlist == null || this.periodlist.isEmpty() ) {
					this.periodlist = periodlist1;
					return true;
				}
				else {
					ChenJiaoZhanBiInGivenPeriod firstrecord = this.periodlist.get(0);
					LocalDate firstday = firstrecord.getRecordsDayofEndofWeek();
					if(position.isBefore(firstday) || position.isEqual(firstday)) {
//						logger.debug("add before" + this.wkcjeperiodlist.size());
						this.periodlist.addAll(0, periodlist1);
//						logger.debug("add after" + this.wkcjeperiodlist.size());
						return true;
					}
					
					ChenJiaoZhanBiInGivenPeriod lastrecord = this.periodlist.get(this.periodlist.size()-1);
					LocalDate lastday = lastrecord.getRecordsDayofEndofWeek();
					if(position.isAfter(lastday) || position.isEqual(lastday)) {
						this.periodlist.addAll(this.periodlist.size()-1, periodlist1);
						return true;
					}
					
				}
				
				return false;
			}

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
			 * �ڽ��׼�¼���ҵ���Ӧ��/�յ�λ��,difference��ƫ������
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
//					if(tmpcjzb.getRecordsType().toUpperCase().equals(ChenJiaoZhanBiInGivenPeriod.WEEK)) { //������������ݣ�ֻҪ��������ͬ�����ɷ���
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
//					} else if(tmpcjzb.getRecordsType().toUpperCase().equals(ChenJiaoZhanBiInGivenPeriod.DAY)) { //������������ݣ�������ͬ�ſɷ���
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
			 * ���ָ���ܵļ�¼
			 */
			public ChenJiaoZhanBiInGivenPeriod getSpecficRecord(LocalDate requireddate, int difference) 
			{
				if(periodlist == null)
					return null;

				String nodeperiod = getNodeperiodtype();
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
				for(ChenJiaoZhanBiInGivenPeriod tmpcjzb : periodlist) {
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

			/*
			 * ����ָ�����ں������ڵĳɽ�����ʺ�stock/bankuai��dapan���Լ��ļ��㷽��
			 */
			public Double getChengJiaoErDifferenceWithLastPeriod(LocalDate requireddate)
			{
				if(periodlist == null)
					return null;
				
				ChenJiaoZhanBiInGivenPeriod curcjlrecord = this.getSpecficRecord (requireddate,0);
				if( curcjlrecord == null) 
					return null;
				
				int index = -1;
				while ( ((DaPan)getRoot()).isDaPanXiuShi(requireddate, index ,getNodeperiodtype()) ) { //���ܿ��ܴ�������
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

			/*
			 * ���ϼ����ĳɽ���ռ������
			 */
			public Double getChenJiaoErZhanBiGrowthRateOfSuperBanKuai(LocalDate requireddate) 
			{
				if(periodlist == null)
					return null;
				
				ChenJiaoZhanBiInGivenPeriod curcjlrecord = this.getSpecficRecord (requireddate,0);
				if( curcjlrecord == null) 
					return null;
				
				int index = -1;
				while ( ((DaPan)getRoot()).isDaPanXiuShi(requireddate, index ,getNodeperiodtype()) ) { //���ܿ��ܴ�������
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
			public Integer getChenJiaoErMaxWeekOfSuperBanKuai(LocalDate requireddate) 
			{
				if(periodlist == null)
					return null;
				
				ChenJiaoZhanBiInGivenPeriod curcjlrecord = this.getSpecficRecord (requireddate,0);
				if( curcjlrecord == null) 
					return null;
				
				int index = -1;
				DaPan dapan = (DaPan)getRoot();
				while ( ((DaPan)getRoot()).isDaPanXiuShi(requireddate, index ,getNodeperiodtype()) ) { //���ܿ��ܴ�������
					index --;
				}
				
				ChenJiaoZhanBiInGivenPeriod lastcjlrecord = this.getSpecficRecord (requireddate,index);
				if(lastcjlrecord == null) { //����ǰ���ǿգ�˵��Ҫ���°�顣���û��ͣ�Ƶ�
					logger.debug(getMyOwnCode() + getMyOwnName() + "������һ���¸��ɻ���");
					return 100;
				}
				
				Double curcje = curcjlrecord.getMyOwnChengJiaoEr();
				int maxweek = 0;
				for(;index >= -100000; index--) {
					lastcjlrecord = this.getSpecficRecord (requireddate,index);
					
					if(lastcjlrecord == null) //Ҫ���ǵ��˼�¼����ͷ��
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
				
				int index = -1;
				DaPan dapan = (DaPan)getRoot();
				while ( ((DaPan)getRoot()).isDaPanXiuShi(requireddate, index ,getNodeperiodtype()) ) { //���ܿ��ܴ�������
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
			
			/*
			 * ����ɽ���仯�����ʣ������ɽ���ı仯ռ�����ϼ����ɽ����������ı���
			 */
			public abstract Double getChenJiaoErChangeGrowthRateOfSuperBanKuai (LocalDate requireddate);
			
			/*
			 * һ���Լ�����������
			 */
			public abstract ChenJiaoZhanBiInGivenPeriod getNodeFengXiResultForSpecificDate (LocalDate requireddate);
			


		 }
		 
		 
}


