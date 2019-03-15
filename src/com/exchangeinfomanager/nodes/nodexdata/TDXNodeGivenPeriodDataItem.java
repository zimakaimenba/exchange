package com.exchangeinfomanager.nodes.nodexdata;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.ohlc.OHLCItem;

import com.exchangeinfomanager.commonlib.CommonUtility;

public class TDXNodeGivenPeriodDataItem extends OHLCItem
{											  
	public TDXNodeGivenPeriodDataItem(String nodecode,String datatype,RegularTimePeriod period,
			double open, double high, double low, double close,double myamount,double myvolumne,
			Double huanshoulv,Double zongshizhi,Double liutongshizhi
			) 
	{
		super (period,open,high,low,close);
		
		this.myowncode = nodecode;
		this.myownchengjiaoer = myamount;
		this.myownchengjiaoliang = myvolumne;
		this.regulartimeperiod = period;
		this.huanshoulv = huanshoulv;
		this.zongshizhi = zongshizhi;
		this.liutongshizhi = liutongshizhi;
		
		if( datatype.toUpperCase().equals(TDXNodeGivenPeriodDataItem.DAY)
				|| datatype.toUpperCase().equals(TDXNodeGivenPeriodDataItem.WEEK)
				|| datatype.toUpperCase().equals(TDXNodeGivenPeriodDataItem.SIXTYMINUTES))
			this.dataperiodtype = datatype;
		else
			this.dataperiodtype = null;
	}
	
	public static String  DAY = "DAY", WEEK = "WEEK", SIXTYMINUTES = "SIXTYMINUTES", MONTH= "MONTH";
	private String dataperiodtype; //���ߣ����ߣ����ߣ�����
	private RegularTimePeriod regulartimeperiod;
	
	private String myowncode;
	private String myuplevelcode;

	private LocalDate  dayofendoftheweek;
	
	private Double myownchengjiaoliang; //�ɽ���
	private Double uplevelchengjiaoliang; //�ϼ����ĳɽ���
	private Double myownchengjiaoer; //�ɽ���
	private Double uplevelchengjiaoer; //�ϼ����ĳɽ���
	private Double ggbkcjezhanbigrowthrate;  //���ɰ��ռ������
	private Integer ggbkcjezhanbimaxweek;  //���ɰ��ռ�������
	private Integer ggbkcjemaxweek; //���ɰ��ɽ��������
	private Double ggbkcjegrowthratetosuperbankuaicjegrowth;  //���ɰ��ɽ���������
	private Double huanshoulv;
	private Double zongshizhi;
	private Double liutongshizhi;
	private Double periodhighestzhangdiefu;
	private Double periodlowestzhangdiefu;
	
	private Integer hasfengxijieguo; //�м����ע����������ȵ�
	
	
	private double ggbkcjedifference; //���ɵĳɽ�����ںͺ����������ڵĲ��
	private Integer exchangedaysnumber; //���ɰ��ÿ�ܵĽ�������Ŀ
	/**
	 * @return the myowncode
	 */
	public String getMyOwnCode() {
		return myowncode;
	}
	/**
	 * @param myowncode the myowncode to set
	 */
//	public void setMyOwnCode(String myowncode) {
//		this.myowncode = myowncode;
//	}
	public String getRecordsType () {
		return this.dataperiodtype;
	}
//	/**
//	 * @return the myuplevelcode
//	 */
//	public String getMyUpLevelCode() {
//		return myuplevelcode;
//	}
//	/**
//	 * @param myuplevelcode the myuplevelcode to set
//	 */
//	public void setMyUpLevelCode(String myuplevelcode) {
//		this.myuplevelcode = myuplevelcode;
//	}
	/**
	 * @return the year
	 */
	public int getRecordsYear() 
	{
		return this.dayofendoftheweek.getYear();
	}
	public int getRecordsMonth () 
	{
		return this.dayofendoftheweek.getMonthValue();
	}
	/**
	 * @return the week
	 */
	public int getRecordsWeek() 
	{
		WeekFields weekFields = WeekFields.of(Locale.getDefault()); 
		int weekNumber = this.dayofendoftheweek.get(weekFields.weekOfWeekBasedYear());
		return weekNumber;
	}
	/**
	 * @return the dayofendoftheweek
	 */
	public LocalDate getRecordsDayofEndofWeek() {
		
		return dayofendoftheweek;
	}
	/**
	 * @param dayofendoftheweek the dayofendoftheweek to set
	 */
	public void setRecordsDayofEndofWeek(java.sql.Date dayofendoftheweek) 
	{
		try {
			this.dayofendoftheweek = dayofendoftheweek.toLocalDate();
//			this.dayofendoftheweek = dayofendoftheweek.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		} catch (java.lang.UnsupportedOperationException e) {
			e.printStackTrace();
		}
	}
	/**
	 * @return the myownchengjiaoliang
	 */
	public Double getMyOwnChengJiaoEr() {
		return myownchengjiaoer;
	}
	/**
	 * @param myownchengjiaoliang the myownchengjiaoliang to set
	 */
//	public void setMyOwnChengJiaoEr(Double myownchengjiaoliang) {
//		this.myownchengjiaoer = myownchengjiaoliang;
//	}
	/**
	 * @return the uplevelchengjiaoliang
	 */
	public Double getUpLevelChengJiaoEr() {
		return uplevelchengjiaoer;
	}
	/**
	 * @param uplevelchengjiaoliang the uplevelchengjiaoliang to set
	 */
	public void setUpLevelChengJiaoEr(Double uplevelchengjiaoliang) {
		this.uplevelchengjiaoer = uplevelchengjiaoliang;
	}

	/**
	 * @������ɺ����ϼ��Ĵ��̻���ĳɽ���ı�ֵ
	 */
	public Double getCjeZhanBi()  
	{
		try {
			Double zhanbi = this.myownchengjiaoer/this.uplevelchengjiaoer; 
			return zhanbi;
		} catch (java.lang.NullPointerException e) {
			return null;
		}
	}
	
	public Double getMyownchengjiaoliang() {
		return myownchengjiaoliang;
	}
//	public void setMyownchengjiaoliang(Double myownchengjiaoliang) {
//		this.myownchengjiaoliang = myownchengjiaoliang;
//	}
	public Double getUplevelchengjiaoliang() {
		return uplevelchengjiaoliang;
	}
	public void setUplevelchengjiaoliang(Double uplevelchengjiaoliang) {
		this.uplevelchengjiaoliang = uplevelchengjiaoliang;
	}
	/*
	 * ����/���ĳɽ���ռ��
	 */
	public Double getCjlZhanBi()  
	{
		try {
			return this.myownchengjiaoliang/this.uplevelchengjiaoliang;
		} catch (java.lang.NullPointerException e) {
			return null;
		}
			
	}
	

	/**
	 * @return the ggbkzhanbigrowthrate
	 */
	public Double getGgBkCjeZhanbiGrowthRate() {
		return ggbkcjezhanbigrowthrate;
	}
	/**
	 * @param ggbkzhanbigrowthrate the ggbkzhanbigrowthrate to set
	 */
	public void setGgBkCjeZhanbiGrowthRate(Double ggbkzhanbigrowthrate) {
		this.ggbkcjezhanbigrowthrate = ggbkzhanbigrowthrate;
	}
	/**
	 * @return the ggbkzhanbimaxweek
	 */
	public Integer getGgBkCjeZhanbiMaxweek() {
		return ggbkcjezhanbimaxweek;
	}
	/**
	 * @param ggbkzhanbimaxweek the ggbkzhanbimaxweek to set
	 */
	public void setGgBkCjeZhanbiMaxweek(Integer ggbkzhanbimaxweek) {
		this.ggbkcjezhanbimaxweek = ggbkzhanbimaxweek;
	}
	/**
	 * @return the ggbkcjegrowthzhanbi
	 */
	public Double getGgBkCjeGrowthRateToSuperBanKuaiCjeGrowth() {
		return ggbkcjegrowthratetosuperbankuaicjegrowth;
	}
	/**
	 * @param ggbkcjegrowthzhanbi the ggbkcjegrowthzhanbi to set
	 */
	public void setGgBkCjeGrowthRateToSuperBanKuaiCjeGrowth(Double ggbkcjegrowthzhanbi) {
		this.ggbkcjegrowthratetosuperbankuaicjegrowth = ggbkcjegrowthzhanbi;
	}
//	/**
//	 * @return the ggdpzhanbigrowthrate
//	 */
//	public Double getGgdpzhanbigrowthrate() {
//		return ggdpcjezhanbigrowthrate;
//	}
//	/**
//	 * @param ggdpzhanbigrowthrate the ggdpzhanbigrowthrate to set
//	 */
//	public void setGgdpzhanbigrowthrate(Double ggdpzhanbigrowthrate) {
//		this.ggdpcjezhanbigrowthrate = ggdpzhanbigrowthrate;
//	}
	/**
	 * @return the ggbkcjemaxweek
	 */
	public Integer getGgBkCjeMaxweek() {
		return ggbkcjemaxweek;
	}
	/**
	 * @param ggbkcjemaxweek the ggbkcjemaxweek to set
	 */
	public void setGgBkCjeMaxweek(Integer ggbkcjemaxweek) {
		this.ggbkcjemaxweek = ggbkcjemaxweek;
	}
	public void setFengXiJIeGuo (Integer fxjg)
	{
		this.hasfengxijieguo =  fxjg;
		
	}
	public Integer getFengXiJieGuo ()
	{
		return this.hasfengxijieguo;
	}
	
	/*
	 * ������ɽ���ͺ�����������(����δͣ��ǰ����)�Ĳ��
	 */
	public void setGgBkCjeDifferenceWithLastPeriod(double d) {
		this.ggbkcjedifference = d;
		
	}
	public Double getHuanshoulv() {
		return huanshoulv;
	}
	public void setHuanshoulv(Double huanshoulv) {
		this.huanshoulv = huanshoulv;
	}
	public Double getZongshizhi() {
		return zongshizhi;
	}
	public void setZongshizhi(Double zongshizhi) {
		this.zongshizhi = zongshizhi;
	}
	public Double getLiutongshizhi() {
		return liutongshizhi;
	}
	public void setLiutongshizhi(Double liutongshizhi) {
		this.liutongshizhi = liutongshizhi;
	}

	public Double getPeriodhighestzhangdiefu() {
		return periodhighestzhangdiefu;
	}
	public void setPeriodhighestzhangdiefu(Double periodhighestzhangdiefu) {
		this.periodhighestzhangdiefu = periodhighestzhangdiefu;
	}
	public Double getPeriodlowestzhangdiefu() {
		return periodlowestzhangdiefu;
	}
	public void setPeriodlowestzhangdiefu(Double periodlowestzhangdiefu) {
		this.periodlowestzhangdiefu = periodlowestzhangdiefu;
	}
	public void setExchangeDaysNumber(Integer daysnumber) {
		this.exchangedaysnumber = daysnumber;
	}
	public Integer getExchangeDaysNumber() {
		// TODO Auto-generated method stub
		return this.exchangedaysnumber;
	}

}