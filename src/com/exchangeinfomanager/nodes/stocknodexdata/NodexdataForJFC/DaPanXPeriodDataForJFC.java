package com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForJFC;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.exchangeinfomanager.bankuaifengxi.QueKou;
import com.exchangeinfomanager.commonlib.FormatDoubleToShort;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.DaPan;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;
import com.google.common.collect.Multimap;


public class DaPanXPeriodDataForJFC implements NodeXPeriodData
{
	private String nodeperiodtype;
	private String nodecode;
	
	public DaPanXPeriodDataForJFC (String nodeperiodtype1,BanKuai shanghai, BanKuai shenzhen)
	{
		this.nodecode = "000000";
		this.nodeperiodtype = nodeperiodtype1;
		this.shanghai = shanghai;
		this.shenzhen = shenzhen;
	}
	
	@Override
	public String getNodeperiodtype() 
	{
		return this.nodeperiodtype;
	}
	
	private BanKuai shanghai;
	private BanKuai shenzhen;

	/*
	 * 
	 */
	public Double getChengJiaoErDifferenceWithLastPeriod (LocalDate requireddate,int difference)
	{
		Double dapanchaer = null;
		
		String recordsperiod = getNodeperiodtype();
		NodeXPeriodData shanghaiperiodrecords = shanghai.getNodeXPeroidData(recordsperiod);
		Double shcurrecord = shanghaiperiodrecords.getChengJiaoErDifferenceWithLastPeriod(requireddate,difference);
		
		NodeXPeriodData shenzhenperiodrecords = shenzhen.getNodeXPeroidData(recordsperiod);
		Double szcurrecord = shenzhenperiodrecords.getChengJiaoErDifferenceWithLastPeriod(requireddate,difference);
		
		try{
			dapanchaer = szcurrecord + shcurrecord ;
		} catch (java.lang.NullPointerException e) {
			dapanchaer = 0.0;
//			e.printStackTrace();
		}
		return dapanchaer ;
	}

	@Override
	public Double getChengJiaoEr(LocalDate requireddate, int difference) 
	{
		String recordsperiod = getNodeperiodtype();
		NodeXPeriodData shanghaiperiodrecords = shanghai.getNodeXPeroidData(recordsperiod);
		Double shcurrecord = shanghaiperiodrecords.getChengJiaoEr(requireddate,difference);
		if(shcurrecord == null)
			return null;
		
		NodeXPeriodData shenzhenperiodrecords = shenzhen.getNodeXPeroidData(recordsperiod);
		Double szcurrecord = shenzhenperiodrecords.getChengJiaoEr(requireddate,difference);
		
		Double dapancje = null;
		try {
		 dapancje = shcurrecord + szcurrecord;
		} catch (java.lang.NullPointerException e) {
//			System.out.println(requireddate);
		}
		return dapancje;
	}
	@Override
	public LocalDate getLocalDateOfSpecificIndexOfOHLCData(Integer index)
	{
		String recordsperiod = getNodeperiodtype();
		NodeXPeriodData shanghaiperiodrecords = shanghai.getNodeXPeroidData(recordsperiod);
		return shanghaiperiodrecords.getLocalDateOfSpecificIndexOfOHLCData(index);
	}

	@Override
	public Integer getExchangeDaysNumberForthePeriod(LocalDate requireddate, int difference) 
	{
		String recordsperiod = getNodeperiodtype();
		NodeXPeriodData shanghaiperiodrecords = shanghai.getNodeXPeroidData(recordsperiod);
		Integer exchangedays = shanghaiperiodrecords.getExchangeDaysNumberForthePeriod(requireddate,difference);
		return exchangedays;
	}
	@Override
	public Double getChengJiaoErDailyAverageDifferenceWithLastPeriod(LocalDate requireddate, int difference) 
	{
		String recordsperiod = getNodeperiodtype();
		
		NodeXPeriodData shanghaiperiodrecords = shanghai.getNodeXPeroidData(recordsperiod);
		Double shdiff = shanghaiperiodrecords.getChengJiaoErDailyAverageDifferenceWithLastPeriod(requireddate,difference);
		
		NodeXPeriodData shenzhenperiodrecords = shenzhen.getNodeXPeroidData(recordsperiod);
		LocalDate start = shenzhenperiodrecords.getOHLCRecordsStartDate();
		LocalDate end = shenzhenperiodrecords.getOHLCRecordsEndDate();
		Double szcurrecord = shenzhenperiodrecords.getChengJiaoErDailyAverageDifferenceWithLastPeriod(requireddate,difference);
		
		if(shdiff == null)
			return null;
		try {
			double result = shdiff + szcurrecord; 
			return result;
		} catch ( java.lang.NullPointerException e) {
			e.printStackTrace();
			return null;
		}
		
		
	}
	
	@Override
	public String getNodeXDataInHtml(TDXNodes superbk, LocalDate requireddate, int difference)
	{
		DecimalFormat decimalformate = new DecimalFormat("#0.000"); //",###";
    	NumberFormat percentFormat = NumberFormat.getPercentInstance(Locale.CHINA);
    	percentFormat.setMinimumFractionDigits(4);
    	DecimalFormat decimalformate2 = new DecimalFormat("%#0.00000");
			
		String htmlstring = "";
		org.jsoup.nodes.Document doc = Jsoup.parse(htmlstring);
		Elements body = doc.getElementsByTag("body");
		for(Element elbody : body) {
			org.jsoup.nodes.Element dl = elbody.appendElement("dl");
			 
			 org.jsoup.nodes.Element lidate = dl.appendElement("li");
			 org.jsoup.nodes.Element fontdate = lidate.appendElement("font");
			 fontdate.appendText(requireddate.toString());
			 fontdate.attr("color", "#17202A");
			 
			 Double curcje = this.getChengJiaoEr(requireddate, 0);
			 String cjedanwei = null;
			 try{
					cjedanwei = FormatDoubleToShort.getNumberChineseDanWei(curcje);
					curcje = FormatDoubleToShort.formateDoubleToShort (curcje);
			 } catch (java.lang.NullPointerException e) {
//					e.printStackTrace();
					return "";
			 }
			 org.jsoup.nodes.Element licje = dl.appendElement("li");
			 org.jsoup.nodes.Element fontcje = licje.appendElement("font");
			 fontcje.appendText("成交额" + decimalformate.format(curcje) + cjedanwei);
			 fontcje.attr("color", "#AF7AC5");
			 
			 Double avecje = this.getAverageDailyChengJiaoErOfWeek (requireddate, 0);
			 cjedanwei = null;
			 try{
					cjedanwei = FormatDoubleToShort.getNumberChineseDanWei(avecje);
					avecje = FormatDoubleToShort.formateDoubleToShort (avecje);
			 } catch (java.lang.NullPointerException e) {
//					e.printStackTrace();
					return "";
			 }
			 org.jsoup.nodes.Element liavecje = dl.appendElement("li");
			 org.jsoup.nodes.Element fontavecje = liavecje.appendElement("font");
			 fontavecje.appendText("周日平均成交额" + decimalformate.format(avecje) + cjedanwei);
			 fontavecje.attr("color", "#AF7AC5");
			 
			 Integer avecjemaxwk = this.getAverageDailyChenJiaoErMaxWeekOfSuperBanKuai(requireddate,0);
			 org.jsoup.nodes.Element liavecjemaxwk = dl.appendElement("li");
			 org.jsoup.nodes.Element fontavecjemaxwk = liavecjemaxwk.appendElement("font");
			 fontavecjemaxwk.appendText("周日平均成交额MaxWK= " + avecjemaxwk);
			 fontavecjemaxwk.attr("color", "#AF7AC5");
			 
			 Integer cjemaxwk = null;
		     try{
		    	cjemaxwk = this.getChenJiaoErMaxWeekOfSuperBanKuai(requireddate,0);//显示成交额是多少周最大,成交额多少周最小没有意义，因为如果不是完整周成交量就是会很小
		     } catch (java.lang.NullPointerException e) {
		    		
		     }
			 if(cjemaxwk!= null && cjemaxwk>0) {
				 org.jsoup.nodes.Element licjemaxwk = dl.appendElement("li");
				 org.jsoup.nodes.Element fontcjemaxwk = licjemaxwk.appendElement("font");
				 fontcjemaxwk.appendText("成交额MaxWk=" + cjemaxwk);
				 fontcjemaxwk.attr("color", "#AF7AC5 ");
			 } 
			 
			 //
			 Double curcjl = this.getChengJiaoLiang(requireddate, 0);
	    	 String cjldanwei = FormatDoubleToShort.getNumberChineseDanWei(curcjl);
	    	 curcjl = FormatDoubleToShort.formateDoubleToShort (curcjl);
			 org.jsoup.nodes.Element licjl = dl.appendElement("li");
			 org.jsoup.nodes.Element fontcjl = licjl.appendElement("font");
			 fontcjl.appendText("成交量" + decimalformate.format(curcjl) + cjldanwei);
			 fontcjl.attr("color", "#641E16");
			 //
			 Integer cjlmaxwk = null;
		     try{
		    		cjlmaxwk = this.getChenJiaoLiangMaxWeekOfSuperBanKuai(requireddate,0);//显示cjl是多少周最大
		     } catch (java.lang.NullPointerException e) {
		     }
			 if(cjlmaxwk != null && cjlmaxwk>0) {
				 org.jsoup.nodes.Element licjlmaxwk = dl.appendElement("li");
				 org.jsoup.nodes.Element fontcjlmaxwk = licjlmaxwk.appendElement("font");
				 fontcjlmaxwk.appendText("成交量MaxWk=" + cjlmaxwk);
				 fontcjlmaxwk.attr("color", "#641E16");
			 } 
		}
		
		return doc.toString();
	}
	@Override
	public void addNewXPeriodData(NodeGivenPeriodDataItem kdata) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Boolean isLocalDateReachFristDayInHistory(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setShangShiRiQi(LocalDate requireddate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getNodeCode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean hasFxjgInPeriod(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addFxjgToPeriod(RegularTimePeriod period, Integer fxjg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resetQueKouTongJiJieGuo() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addQueKouTongJiJieGuo(LocalDate qkdate, Integer nodeqktjopenup1, Integer nodeqktjhuibuup1,
			Integer nodeqktjopendown1, Integer nodeqktjhuibudown1, Boolean addbasedoncurdata) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<QueKou> getPeriodQueKou() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPeriodQueKou(List<QueKou> qkl) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addZhangDieTingTongJiJieGuo(LocalDate tjdate, Integer nodeztnum, Integer nodedtnum,
			Boolean addbasedoncurdata) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Integer getZhangTingTongJi(LocalDate requireddate, Integer difference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getDieTingTongJi(LocalDate requireddate, Integer difference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getQueKouTongJiOpenUp(LocalDate requireddate, Integer difference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getQueKouTongJiOpenDown(LocalDate requireddate, Integer difference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getQueKouTongJiHuiBuUp(LocalDate requireddate, Integer difference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getQueKouTongJiHuiBuDown(LocalDate requireddate, Integer difference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocalDate getQueKouRecordsStartDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocalDate getQueKouRecordsEndDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getChenJiaoErZhanBi(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getChenJiaoErZhanBiGrowthRateOfSuperBanKuai(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getChenJiaoErZhanBiMinWeekOfSuperBanKuai(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getChenJiaoLiangZhanBi(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getChenJiaoLiangZhanBiGrowthRateOfSuperBanKuai(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getChenJiaoLiangZhanBiMaxWeekOfSuperBanKuai(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getChenJiaoLiangZhanBiMinWeekOfSuperBanKuai(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getIndexOfSpecificDateOHLCData(LocalDate requireddate, int difference)
	{
		// TODO Auto-generated method stub
		String recordsperiod = getNodeperiodtype();
		NodeXPeriodData shanghaiperiodrecords = shanghai.getNodeXPeroidData(recordsperiod);
		Integer index = shanghaiperiodrecords.getIndexOfSpecificDateOHLCData(requireddate, difference);
		return index;
	}

	@Override
	public Double getSpecificOHLCZhangDieFu(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocalDate getOHLCRecordsStartDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocalDate getOHLCRecordsEndDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getChenJiaoErMaxWeekOfSuperBanKuai(LocalDate requireddate, int difference) 
	{
 		Double dpcurcje = this.getChengJiaoEr(requireddate, 0);
		
		if(dpcurcje == null)
			return null;
		
		int maxweek = 0;
		String recordsperiod = getNodeperiodtype();
		NodeXPeriodData shanghaiperiodrecords = shanghai.getNodeXPeroidData(recordsperiod);
		
		Integer indexofcur = shanghaiperiodrecords.getIndexOfSpecificDateOHLCData(requireddate, 0);
		if(indexofcur == 0)
			return maxweek;
		
		Boolean indexclosetofront = false;
		for(int i = -1; indexclosetofront == false ; i--) {
			LocalDate expecteddate = getRequiredLocalDate (requireddate,i);
			
			Integer indexoflast = shanghaiperiodrecords.getIndexOfSpecificDateOHLCData(requireddate, i);
			if(indexoflast != null && indexoflast == 0) //大盘成交额为空要不是休市，要不是到达记录顶点，要区分
				indexclosetofront = true;
			
			Double expectcje = this.getChengJiaoEr (expecteddate,0);
			
			if(expectcje == null && indexclosetofront == false) //还没到顶点，说明是休市，继续往前找
				continue;
			
			if(expectcje != null && dpcurcje > expectcje)
				maxweek ++;
			else
				break;
		}
		
		return maxweek;
	}

	@Override
	public Double getChenJiaoErChangeGrowthRateOfSuperBanKuai(TDXNodes superbk, LocalDate requireddate,
			int difference) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public Double getChengJiaoLiang(LocalDate requireddate, int difference) 
	{
		String recordsperiod = getNodeperiodtype();
		NodeXPeriodData shanghaiperiodrecords = shanghai.getNodeXPeroidData(recordsperiod);
		Double shcurrecord = shanghaiperiodrecords.getChengJiaoLiang(requireddate,difference);
		if(shcurrecord == null)
			return null;
		
		NodeXPeriodData shenzhenperiodrecords = shenzhen.getNodeXPeroidData(recordsperiod);
		Double szcurrecord = shenzhenperiodrecords.getChengJiaoLiang(requireddate,difference);
		
		Double dapancjl = null;
		try {
		 dapancjl = shcurrecord + szcurrecord;
		} catch (java.lang.NullPointerException e) {
//			System.out.println(requireddate);
		}
		return dapancjl;
	}
	@Override
	public Double getChenJiaoErChangeGrowthRateOfSuperBanKuaiOnDailyAverage(TDXNodes superbk, LocalDate requireddate,
			int difference) 
	{
		return null;
	}

	@Override
	public Double getChenJiaoLiangDifferenceWithLastPeriod(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getChenJiaoLiangMaxWeekOfSuperBanKuai(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getChenJiaoLiangChangeGrowthRateOfSuperBanKuai(TDXNodes superbk, LocalDate requireddate,
			int difference) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Double[] getNodeOhlcSMA(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean checkCloseComparingToMAFormula(String maformula, LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Multimap<LocalDate, LocalDate> isMacdButtomDivergenceInSpecificMonthRange(LocalDate requireddate,
			int difference, int monthrange) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Multimap<LocalDate, LocalDate> isMacdTopDivergenceInSpecificMonthRange(LocalDate requireddate,
			int difference, int monthrange) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getAverageDailyChengJiaoErOfWeek(LocalDate requireddate, int difference) 
	{
		Double cje = this.getChengJiaoEr(requireddate, 0);
		if(cje != null) {
			Integer daynum = this.getExchangeDaysNumberForthePeriod(requireddate, 0);
			if(daynum != null)
				return cje/daynum;
			else
				return cje/5;
		} else
			return null;
	}
	
	@Override
	public Integer getAverageDailyChenJiaoErMaxWeekOfSuperBanKuai(LocalDate requireddate, int difference) 
	{
		Double curcje = this.getAverageDailyChengJiaoErOfWeek(requireddate, difference);
		int maxweek = 0;
		
		String recordsperiod = getNodeperiodtype();
		NodeXPeriodData shanghaiperiodrecords = shanghai.getNodeXPeroidData(recordsperiod);
		int index = this.getIndexOfSpecificDateOHLCData(requireddate, difference);
		for(int i = index-1;i >=0; i--) {
			
			LocalDate lastcjlrecorddate = shanghaiperiodrecords.getLocalDateOfSpecificIndexOfOHLCData(i);
			if(lastcjlrecorddate == null ) //可能到了记录的头部了，或者是个诞生时间不长的板块
				return maxweek;
			
			Double lastcje = this.getAverageDailyChengJiaoErOfWeek(lastcjlrecorddate,0);
			if(curcje > lastcje)
				maxweek ++;
			else
				break;
		}

		return maxweek;
	}

	@Override
	public Double getSpecificTimeRangeOHLCHightestZhangFu(LocalDate requiredstart, LocalDate requiredend) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getNodeXDataCsvData(TDXNodes superbk, LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void resetAllData() {
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * 
	 */
	protected RegularTimePeriod getJFreeChartFormateTimePeriod (LocalDate requireddate,int difference) 
	{
		String nodeperiod = this.getNodeperiodtype();
		LocalDate expectedate = null;
		RegularTimePeriod period = null;
		if(nodeperiod.equals(NodeGivenPeriodDataItem.WEEK)) { 
			expectedate = requireddate.plus(difference,ChronoUnit.WEEKS);
			java.sql.Date lastdayofweek = java.sql.Date.valueOf(expectedate);
			period = new org.jfree.data.time.Week (lastdayofweek);
//			period = new org.jfree.data.time.Week (Date.from(expectedate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
		} else if(nodeperiod.equals(NodeGivenPeriodDataItem.DAY)) {
			expectedate = requireddate.plus(difference,ChronoUnit.DAYS);
			java.sql.Date lastdayofweek = java.sql.Date.valueOf(expectedate);
			period = new org.jfree.data.time.Day (lastdayofweek);
//			period = new org.jfree.data.time.Day(Date.from(expectedate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
		}  else if(nodeperiod.equals(NodeGivenPeriodDataItem.MONTH)) {
			expectedate = requireddate.plus(difference,ChronoUnit.MONTHS);
		}
		
		return period;
	}
	protected LocalDate getRequiredLocalDate (LocalDate requireddate,int difference)
	{
		String nodeperiod = this.getNodeperiodtype();
		LocalDate expectedate = null;
		if(nodeperiod.equals(NodeGivenPeriodDataItem.WEEK)) { 
			expectedate = requireddate.plus(difference,ChronoUnit.WEEKS);
		} else if(nodeperiod.equals(NodeGivenPeriodDataItem.DAY)) {
			expectedate = requireddate.plus(difference,ChronoUnit.DAYS);
		}  else if(nodeperiod.equals(NodeGivenPeriodDataItem.MONTH)) {
			expectedate = requireddate.plus(difference,ChronoUnit.MONTHS);
		}
		
		return expectedate;
	}

	@Override
	public LocalDate getAmoRecordsStartDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocalDate getAmoRecordsEndDate() {
		// TODO Auto-generated method stub
		return null;
	}

		@Override
	public Integer getCjeDpMaxLianXuFangLiangPeriodNumber(LocalDate requireddate, int difference, int settindpgmaxwk) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Integer getAverageDailyCjeLianXuFangLiangPeriodNumber(LocalDate requireddate, int difference) {
		// TODO Auto-generated method stub
		return null;
	}


}

