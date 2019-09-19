package com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.ohlc.OHLCItem;

import com.exchangeinfomanager.commonlib.CommonUtility;

public class NodeGivenPeriodDataItemForJFC extends OHLCItem implements NodeGivenPeriodDataItem
{											  
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public NodeGivenPeriodDataItemForJFC(String nodecode,String datatype,RegularTimePeriod period,
			double open, double high, double low, double close,double myamount,double myvolumne) 
	{
		super (period,open,high,low,close);
		
		this.myowncode = nodecode;
		this.myownchengjiaoer = myamount;
		this.myownchengjiaoliang = myvolumne;
		this.regulartimeperiod = period;
				
		if( datatype.toUpperCase().equals(NodeGivenPeriodDataItem.DAY)
				|| datatype.toUpperCase().equals(NodeGivenPeriodDataItem.WEEK)
				|| datatype.toUpperCase().equals(NodeGivenPeriodDataItem.SIXTYMINUTES))
			this.dataperiodtype = datatype;
		else
			this.dataperiodtype = null;
	}
	
	public String getMyOwnCode() {
		return myowncode;
	}
	public String getDataPeriodType() {
		return dataperiodtype;
	}
	public String getMyUplevelCode() {
		return myuplevelcode;
	}
	public void setMyUplevelCode(String myuplevelcode) {
		this.myuplevelcode = myuplevelcode;
	}
	public Double getMyOwnChengJiaoLiang() {
		return myownchengjiaoliang;
	}
	public void setMyOwnChengJiaoLiang(Double myownchengjiaoliang) {
		this.myownchengjiaoliang = myownchengjiaoliang;
	}
	public Double getUplevelChengJiaoLiang() {
		return uplevelchengjiaoliang;
	}
	public void setUplevelChengJiaoLiang(Double uplevelchengjiaoliang) {
		this.uplevelchengjiaoliang = uplevelchengjiaoliang;
	}
	public Double getNodeToDpChenJiaoLiangZhanbi() {
		return nodedpcjlzhanbi;
	}
	public void setNodeToDpChenJiaoLiangZhanbi(Double ggdpcjlzhanbi) {
		this.nodedpcjlzhanbi = ggdpcjlzhanbi;
	}
	public Double getMyOwnChengJiaoEr() {
		return myownchengjiaoer;
	}
	public void setMyOwnChengJiaoEr(Double myownchengjiaoer) {
		this.myownchengjiaoer = myownchengjiaoer;
	}
	public Double getUplevelChengJiaoEr() {
		return uplevelchengjiaoer;
	}
	public void setUplevelChengJiaoEr(Double uplevelchengjiaoer) {
		this.uplevelchengjiaoer = uplevelchengjiaoer;
	}
	public Double getNodeToDpChenJiaoErZhanbi() {
		return nodedpcjezhanbi;
	}
	public void setNodeToDpChenJiaoErZhanbi(Double ggdpcjezhanbi) {
		this.nodedpcjezhanbi = ggdpcjezhanbi;
	}
	public Integer getExchangeDaysNumber() {
		return exchangedaysnumber;
	}
	public void setExchangeDaysNumber(Integer exchangedaysnumber) {
		this.exchangedaysnumber = exchangedaysnumber;
	}
	public Double getHuanShouLv() {
		return huanshoulv;
	}
	public void setHuanShouLv(Double huanshoulv) {
		this.huanshoulv = huanshoulv;
	}
	public Double getZongShiZhi() {
		return zongshizhi;
	}
	public void setZongShiZhi(Double zongshizhi) {
		this.zongshizhi = zongshizhi;
	}
	public Double getLiuTongShiZhi() {
		return liutongshizhi;
	}
	public void setLiuTongShiZhi(Double liutongshizhi) {
		this.liutongshizhi = liutongshizhi;
	}
	public Double getPeriodHighestZhangDieFu() {
		return periodhighestzhangdiefu;
	}
	public void setPeriodHighestZhangDieFu(Double periodhighestzhangdiefu) {
		this.periodhighestzhangdiefu = periodhighestzhangdiefu;
	}
	public Double getPeriodLowestZhangDieFu() {
		return periodlowestzhangdiefu;
	}
	public void setPeriodLowestZhangDieFu(Double periodlowestzhangdiefu) {
		this.periodlowestzhangdiefu = periodlowestzhangdiefu;
	}
	public Integer getHasFengXiJieGuo() {
		return hasfengxijieguo;
	}
	public void setHasFengXiJieGuo(Integer hasfengxijieguo) {
		this.hasfengxijieguo = hasfengxijieguo;
	}
	public Integer getZhangTingNumber() {
		return zhangtingnumber;
	}
	public void setZhangTingNumber(Integer zhangtingnumber) {
		this.zhangtingnumber = zhangtingnumber;
	}
	public Integer getDieTingNumber() {
		return dietingnumber;
	}
	public void setDieTingNumber(Integer dietingnumber) {
		this.dietingnumber = dietingnumber;
	}
	public RegularTimePeriod getJFreeChartPeriod (String nodeperiodtype)
	{
		return this.regulartimeperiod;
	}

	private String myowncode;
	private RegularTimePeriod regulartimeperiod;
	private String dataperiodtype; //日线，周线，月线，年线
	private String myuplevelcode;
	
	private Double myownchengjiaoliang; //成交量
	private Double uplevelchengjiaoliang; //上级板块的成交量
	private Double nodedpcjlzhanbi;  //
	
	private Double myownchengjiaoer; //成交额
	private Double uplevelchengjiaoer; //上级板块的成交额
	private Double nodedpcjezhanbi;  //
	
	
	private Integer exchangedaysnumber; //个股板块每周的交易日数目
	
	
	private Double huanshoulv;
	private Double zongshizhi;
	private Double liutongshizhi;
	private Double periodhighestzhangdiefu;
	private Double periodlowestzhangdiefu;
	
	private Integer hasfengxijieguo; //有加入关注，分析结果等等
	
	
	private Integer zhangtingnumber;
	private Integer dietingnumber;

}
