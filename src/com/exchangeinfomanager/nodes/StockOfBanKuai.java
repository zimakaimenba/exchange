package com.exchangeinfomanager.nodes;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.jfree.data.time.TimeSeriesDataItem;

import com.exchangeinfomanager.nodes.nodexdata.StockOfBanKuaiNodeXPeriodData;
import com.exchangeinfomanager.nodes.nodexdata.TDXNodeGivenPeriodDataItem;
import com.exchangeinfomanager.nodes.treerelated.StockOfBanKuaiTreeRelated;
/*
 * ���ĸ��ɺ���ͨ���̵ĸ������������ƣ�����ͬ���ڳɽ�����һ���Ƕ԰��ĳɽ�����һ���ǶԴ��̵ĳɽ�����
 * StockOfBanKuai ��Ӧ��������ͨ��ֵ������ֵ��Щ���ԣ�Ӧ��ֻ�гɽ����ͳɽ������ԣ�����Ȩ�أ�
 */
public class StockOfBanKuai extends TDXNodes
{
	public StockOfBanKuai (BanKuai bankuai1,Stock stock1) 
	{
		super (stock1.getMyOwnCode(),stock1.getMyOwnName());
		super.nodetype = BkChanYeLianTreeNode.BKGEGU;
		
		this.bankuai = bankuai1;
		this.stock = stock1;
		
		super.nodewkdata = new StockOfBanKuaiNodeXPeriodData (stock1.getMyOwnCode(),bankuai1.getMyOwnCode(),TDXNodeGivenPeriodDataItem.WEEK);
		super.nodedaydata = null; //�Ծ������������˵˵���������ݲ������壬���ߺ������壬�ر��Ǹ�������ڰ����������ݡ�
//		super.nodemonthdata = new StockOfBanKuaiNodeXPeriodData (StockGivenPeriodDataItem.MONTH) ;
		
		super.nodetreerelated = null;
		super.nodetreerelated = new StockOfBanKuaiTreeRelated(this);
	}
	
//	private static Logger logger = Logger.getLogger(StockOfBanKuai.class);
	private BanKuai bankuai;
	private LocalDate joindate;
	private LocalDate leftdate;
	private Stock stock;
	private Integer quanzhong;
	private Boolean isbklongtou;
	
	public LocalDate getJoinBanKuaiDate() {
		return joindate;
	}
	public void setJoinBanKuaiDate(LocalDate joindate) {
		this.joindate = joindate;
	}
	public LocalDate getLeftBanKuaiDate() {
		return leftdate;
	}
	public void setLeftBanKuaiDate(LocalDate leftdate) {
		this.leftdate = leftdate;
	}
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
	
}