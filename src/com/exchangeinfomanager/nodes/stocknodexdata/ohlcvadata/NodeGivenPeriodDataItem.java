package com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.function.Function;

import org.jfree.data.time.RegularTimePeriod;
import org.ta4j.core.Bar;
import org.ta4j.core.BaseBar;
import org.ta4j.core.num.Num;


public interface  NodeGivenPeriodDataItem
{
	public static String  DAY = "DAY", WEEK = "WEEK", SIXTYMINUTES = "SIXTYMINUTES", MONTH= "MONTH";

	
	public String getMyOwnCode() ;
	public String getDataPeriodType() ;
	public String getMyUplevelCode() ;
	public void setMyUplevelCode(String myuplevelcode) ;
	public Double getMyOwnChengJiaoLiang() ;
	
	public void setMyOwnChengJiaoLiang(Double myownchengjiaoliang);
	public Double getUplevelChengJiaoLiang() ;
	public void setUplevelChengJiaoLiang(Double uplevelchengjiaoliang) ;
	public Double getNodeToDpChenJiaoLiangZhanbi() ;
	public void setNodeToDpChenJiaoLiangZhanbi(Double ggdpcjlzhanbi) ;
	public Double getMyOwnChengJiaoEr() ;
	public void setMyOwnChengJiaoEr(Double myownchengjiaoer) ;
	public Double getUplevelChengJiaoEr() ;
	public void setUplevelChengJiaoEr(Double uplevelchengjiaoer) ;
	public Double getNodeToDpChenJiaoErZhanbi() ;
	public void setNodeToDpChenJiaoErZhanbi(Double ggdpcjezhanbi) ;
	public Integer getExchangeDaysNumber() ;
	public void setExchangeDaysNumber(Integer exchangedaysnumber) ;
	public Double getHuanShouLv() ;
	public void setHuanShouLv(Double huanshoulv) ;
	public Double getZongShiZhi() ;
	public void setZongShiZhi(Double zongshizhi) ;
	public Double getLiuTongShiZhi() ;
	public void setLiuTongShiZhi(Double liutongshizhi) ;
	public Double getPeriodHighestZhangDieFu() ;
	public void setPeriodHighestZhangDieFu(Double periodhighestzhangdiefu) ;
	public Double getPeriodLowestZhangDieFu() ;
	public void setPeriodLowestZhangDieFu(Double periodlowestzhangdiefu) ;
	public Integer getHasFengXiJieGuo() ;
	public void setHasFengXiJieGuo(Integer hasfengxijieguo) ;
	public Integer getZhangTingNumber() ;
	public void setZhangTingNumber(Integer zhangtingnumber) ;
	public Integer getDieTingNumber() ;
	public void setDieTingNumber(Integer dietingnumber) ;
	public RegularTimePeriod getJFreeChartPeriod (String nodeperiodtype);
		
}
