package com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForJFC;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.ohlc.OHLCItem;

public class TDXGongShi 
{
	public TDXGongShi ()
	{
	}
	/**
	 * K_AGAIN20:= (CLOSE>=MA20 AND OPEN<MA20 AND REF(CLOSE,1)<REF(MA20,1) )
								OR (CLOSE<MA20 AND OPEN>=MA20 AND REF(CLOSE,1)>REF(MA20,1) )
                                OR (REF(CLOSE,1)<REF(MA20,1) AND CLOSE>=MA20)
								OR (REF(CLOSE,1)>=REF(MA20,1) AND CLOSE<MA20)
                                ;
	 * 
	 */
	public Boolean K_AGAIN20 (TDXNodesXPeriodDataForJFC tdxxdata, LocalDate date )
	{
		OHLCItem dataohlc = tdxxdata.getSpecificDateOHLCData (date , 0);
		Double[] mas = tdxxdata.getNodeOhlcMA(date, 0);
		double c = dataohlc.getCloseValue();
		double o = dataohlc.getOpenValue();
		double ma20 = mas[2]; 
		
		OHLCItem refDataohlc = tdxxdata.getSpecificDateOHLCData (date , -1);
		Double[] refMas = tdxxdata.getNodeOhlcMA(date, -1);
		double refC = refDataohlc.getCloseValue();
		double refMa20 = refMas[2];
		
		Boolean result = ( c >= ma20 && o <ma20  &&  refC < refMa20 )
						 || ( c < ma20 && o >= ma20 && refC > refMa20) 
						 || ( refC < refMa20 && c > ma20)
						 || ( refC >=refMa20 && c <ma20)
						;
		
		return result;
	}
	/**
	 * K_AGAIN5:= (REF(CLOSE,1)<REF(MA5,1) AND CLOSE>=MA5)
				  OR (REF(CLOSE,1)>=REF(MA5,1) AND CLOSE<MA5)
                  ;
	 */
	public Boolean K_AGAIN5 (TDXNodesXPeriodDataForJFC tdxxdata, LocalDate date )
	{
		OHLCItem dataohlc = tdxxdata.getSpecificDateOHLCData (date , 0);
		Double[] mas = tdxxdata.getNodeOhlcMA(date, 0);
		double c = dataohlc.getCloseValue();
		double o = dataohlc.getOpenValue();
		double ma5 = mas[0]; 
		
		OHLCItem refDataohlc = tdxxdata.getSpecificDateOHLCData (date , -1);
		Double[] refMas = tdxxdata.getNodeOhlcMA(date, -1);
		double refC = refDataohlc.getCloseValue();
		double refMa5 = refMas[0];
		
		Boolean result = (refC < refMa5 && c >=ma5 )
						|| ( refC >= refMa5 && c <ma5 )
				;
		
		return result;
	}
	/**
	 * RK_DAYREFDW20:= BARSLAST(K_AGAIN20 AND CLOSE<MA20);{ÉÏ´ÎµøÆÆMA20µÄÈÕÆÚ}
		RK_DAYREFDW20B:=  REF(RK_DAYREFDW20,RK_DAYREFDW20+1)+RK_DAYREFDW20+1;{ÉÏÉÏ´ÎµøÆÆMA20µÄÖÜÆÚ}
		RK_DAYREFDW20BB:= REF(RK_DAYREFDW20B,RK_DAYREFDW20B)+RK_DAYREFDW20B;{ÉÏÉÏÉÏ´ÎµøÆÆMA20µÄÖÜÆÚ}
	 */
	public List<LocalDate> RK_DAYREFDW20 (TDXNodesXPeriodDataForJFC tdxxdata, LocalDate date , int times) 
	{
		List<LocalDate> dayrefdw20list = new ArrayList<> ();
		
		int ohlcitemcount = tdxxdata.getIndexOfSpecificDateOHLCData(date, 0);;
		for( int i= 0; i<=ohlcitemcount; i++) {
			OHLCItem dataohlc = tdxxdata.getSpecificDateOHLCData (date , ohlcitemcount- i );
			RegularTimePeriod curperiod = dataohlc.getPeriod();
			LocalDate curstart = curperiod.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//			LocalDate curend = curperiod.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			
			Double[] mas = tdxxdata.getNodeOhlcMA(curstart, 0);
			double c = dataohlc.getCloseValue();
//			double o = dataohlc.getOpenValue();
			double ma20 = mas[2];
			
			Boolean k_again20 = this.K_AGAIN20 (tdxxdata,curstart);
			if(k_again20 && c<ma20) {
				dayrefdw20list.add(curstart);
				
				times --;
				if(times ==0)
					break;
			}
		}
		
		return dayrefdw20list;
		
	}
	/**
	 * RK_DAYREFDW20:= BARSLAST(K_AGAIN20 AND CLOSE<MA20);{上次跌破MA20的日期}
		RK_DAYREFDW20B:=  REF(RK_DAYREFDW20,RK_DAYREFDW20+1)+RK_DAYREFDW20+1;{上上次跌破MA20的周期}
		RK_DAYREFDW20BB:= REF(RK_DAYREFDW20B,RK_DAYREFDW20B)+RK_DAYREFDW20B;{上上上次跌破MA20的周期}
	 */
	public List<LocalDate> RK_DAYREFUP20 (TDXNodesXPeriodDataForJFC tdxxdata, LocalDate date , int times)
	{
		List<LocalDate> dayrefdw20list = new ArrayList<> ();
		
		int ohlcitemcount = tdxxdata.getIndexOfSpecificDateOHLCData(date, 0);;
		for( int i= 0; i<=ohlcitemcount; i++) {
			OHLCItem dataohlc = tdxxdata.getSpecificDateOHLCData (date , ohlcitemcount- i );
			RegularTimePeriod curperiod = dataohlc.getPeriod();
			LocalDate curstart = curperiod.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//			LocalDate curend = curperiod.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			
			Double[] mas = tdxxdata.getNodeOhlcMA(curstart, 0);
			double c = dataohlc.getCloseValue();
//			double o = dataohlc.getOpenValue();
			double ma20 = mas[2];
			
			Boolean k_again20 = this.K_AGAIN20 (tdxxdata,curstart);
			if(k_again20 && c>=ma20) {
				dayrefdw20list.add(curstart);
				
				times --;
				if(times ==0)
					break;
			}
		}
		
		return dayrefdw20list;
	}
	/**
	 * {大调整雏形阶段,出现雏形说明已可能已经开始要大跌了,但需要结合下面8种形态}
		RK_TIAOZH_BAC:= CLOSE<MA20
				  AND COUNT(MA20>MA10 AND MA10>MA5 AND CLOSE<MA5,RK_NWAGADAYNUM+1) >0  {存在极限空头排列}
	              AND COUNT(CLOSE<MA60 AND (MA60-CLOSE)/MA60>=0.008,RK_NWAGADAYNUM+1) >1    {破MA60是重要标志}
				  AND COUNT(MA10<MA20,RK_NWAGADAYNUM+1) > COUNT(MA10>MA20,RK_NWAGADAYNUM+1) {10<20的日子要更多}
				  AND COUNT(MA20<REF(MA20,1) ,RK_NWAGADAYNUM+1) >0 {MA20要拐头向下}
				  AND COUNT(MA20<MA30 AND MA30<REF(MA30,1) AND MA20<=REF(MA20,1),RK_NWAGADAYNUM+1) >=2 {30要大于MA20}
                  AND ( COUNT(MA30>=MA20 ,RK_NWAGADAYNUM+1) > COUNT(MA30<MA20 ,RK_NWAGADAYNUM+1) {MA30>MA20的日子更多}
				        OR COUNT(MA30>REF(MA30,1),RK_NWAGADAYNUM+1) <=1
				        OR COUNT(MA20>REF(MA20,1),RK_NWAGADAYNUM+1) < COUNT(MA20<REF(MA20,1),RK_NWAGADAYNUM+1)
                     )
	 */
	public void RK_TIAOZH_BAC (TDXNodesXPeriodDataForJFC tdxxdata, LocalDate date)
	{
		Boolean closesmallerma20 = false;
		Boolean kongtouexit = false; Boolean downma60 = false; 
		int ma20smllerma30combinedcond = 0;
		int ma10biggerma20 = 0; 
		int ma20biggerma30 = 0;
		int ma30biggerrefma30 = 0;
		int ma20biggerrefma20 = 0;
		
		List<LocalDate> dayrefdw20list = this.RK_DAYREFDW20(tdxxdata, date, 1);
		LocalDate firstdayrefdw20 = dayrefdw20list.get(0);
		Integer indexoffirstdayrefdw20 = tdxxdata.getIndexOfSpecificDateOHLCData(firstdayrefdw20, 0);
		Integer indexofcurday = tdxxdata.getIndexOfSpecificDateOHLCData(date, 0);
		for(int i= indexoffirstdayrefdw20; i<= indexofcurday ; i++) {
			OHLCItem tmpdataohlc = tdxxdata.getSpecificDateOHLCData (date , i );
			RegularTimePeriod tmpperiod = tmpdataohlc.getPeriod();
			LocalDate tmpstart = tmpperiod.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			Double[] mas = tdxxdata.getNodeOhlcMA(tmpstart, 0);
			double c = tmpdataohlc.getCloseValue();
			double ma5 = mas[0]; double ma10 = mas[1]; double ma20 = mas[2]; double ma30 = mas[3];double ma60 = mas[4];

			if(ma20 > ma10 && ma10 > ma5 && c <ma5)
				kongtouexit = true;
			
			if(c <ma60 && (ma60 -c ) /ma60 >=0.008 )
				downma60 = true;
			
			if(ma10 >= ma20) 
				ma10biggerma20 ++;
						
			OHLCItem tmprefdataohlc = tdxxdata.getSpecificDateOHLCData (date , i-1 );
			RegularTimePeriod tmprefperiod = tmprefdataohlc.getPeriod();
			LocalDate tmprefstart = tmprefperiod.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			Double[] refmas = tdxxdata.getNodeOhlcMA(tmprefstart, 0);
			double refc = tmprefdataohlc.getCloseValue();
			double refma5 = refmas[0]; double refma10 = refmas[1]; double refma20 = refmas[2]; double refma30 = refmas[3]; double refma60 = refmas[4];
			if(ma20 < ma30 && ma30 < refma30 && ma20 < refma20)
				ma20smllerma30combinedcond ++;
			
			if(ma20 >= ma30 )
				ma20biggerma30 ++;
			
			if(ma30 >= refma30)
				ma30biggerrefma30 ++;
			if(ma20 >= refma20)
				ma20biggerrefma20 ++;
		}
		
		return ;
	}
	/*
	 * {MA5 4日平均增长率}RA_MA5AVEPCET:= (POW(MAAMO5/REF(MAAMO5,3),1/3)-1)*100;
	 */
	public Double RA_MA5AVEPCET  (TDXNodesXPeriodDataForJFC tdxxdata, LocalDate date)
	{
		Double[] curamoma = tdxxdata.getNodeAMOMA (date, 0);
		Double[] refamoma = tdxxdata.getNodeAMOMA (date, -3);
		
		double pow = java.lang.Math.pow(curamoma[0]/refamoma[0], 1/3);
		
		return (pow -1) /100;
	}
	/*
	 * {4日平均放量增长率}RA_4DAYRIJUN:= (POW(AMO/REF(AMO,3),1/3)-1)*100 ;
	 */
	public Double RA_4DAYRIJUN (TDXNodesXPeriodDataForJFC tdxxdata, LocalDate date)
	{
		 Double curamo = tdxxdata.getChengJiaoEr(date, 0);
		 Double refamo = tdxxdata.getChengJiaoEr(date, -3);
		
		 double pow = java.lang.Math.pow(curamo/refamo, 1/3);
		 return (pow -1) /100;
	}
	
}
