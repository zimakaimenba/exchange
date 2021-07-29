package com.exchangeinfomanager.Core.Nodexdata.NodexdataForJFC;

import org.apache.log4j.Logger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.DayOfWeek;
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

import com.exchangeinfomanager.Core.Nodes.BanKuai;
import com.exchangeinfomanager.Core.Nodes.DaPan;
import com.exchangeinfomanager.Core.Nodes.TDXNodes;
import com.exchangeinfomanager.Core.Nodexdata.NodeXPeriodData;
import com.exchangeinfomanager.Core.Nodexdata.ohlcvaprimarydata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.bankuaifengxi.BanKuaiFengXi;
import com.exchangeinfomanager.bankuaifengxi.QueKou;
import com.exchangeinfomanager.commonlib.FormatDoubleToShort;
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
	
	private static Logger logger = Logger.getLogger(DaPanXPeriodDataForJFC.class);
	
	private BanKuai shanghai;
	private BanKuai shenzhen;

	/*
	 * 
	 */
	public Double getChengJiaoErDifferenceWithLastPeriod (LocalDate requireddate)
	{
		Double dapanchaer = null;
		
		String recordsperiod = getNodeperiodtype();
		NodeXPeriodData shanghaiperiodrecords = shanghai.getNodeXPeroidData(recordsperiod);
		Double shcurrecord = shanghaiperiodrecords.getChengJiaoErDifferenceWithLastPeriod(requireddate);
		
		NodeXPeriodData shenzhenperiodrecords = shenzhen.getNodeXPeroidData(recordsperiod);
		Double szcurrecord = shenzhenperiodrecords.getChengJiaoErDifferenceWithLastPeriod(requireddate);
		
		try{
			dapanchaer = szcurrecord + shcurrecord ;
		} catch (java.lang.NullPointerException e) {
			dapanchaer = 0.0;
//			e.printStackTrace();
		}
		return dapanchaer ;
	}

	@Override
	public Double getChengJiaoEr(LocalDate requireddate) 
	{
		String recordsperiod = getNodeperiodtype();
		NodeXPeriodData shanghaiperiodrecords = shanghai.getNodeXPeroidData(recordsperiod);
		Double shcurrecord = shanghaiperiodrecords.getChengJiaoEr(requireddate);
		if(shcurrecord == null)
			return null;
		
		NodeXPeriodData shenzhenperiodrecords = shenzhen.getNodeXPeroidData(recordsperiod);
		Double szcurrecord = shenzhenperiodrecords.getChengJiaoEr(requireddate);
		
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
	public Integer getExchangeDaysNumberForthePeriod(LocalDate requireddate) 
	{
		String recordsperiod = getNodeperiodtype();
		NodeXPeriodData shanghaiperiodrecords = shanghai.getNodeXPeroidData(recordsperiod);
		Integer exchangedays = shanghaiperiodrecords.getExchangeDaysNumberForthePeriod(requireddate);
		return exchangedays;
	}
	@Override
	public Double getChengJiaoErDailyAverageDifferenceWithLastPeriod(LocalDate requireddate) 
	{
		String recordsperiod = getNodeperiodtype();
		
		NodeXPeriodData shanghaiperiodrecords = shanghai.getNodeXPeroidData(recordsperiod);
		Double shdiff = shanghaiperiodrecords.getChengJiaoErDailyAverageDifferenceWithLastPeriod(requireddate);
		
		NodeXPeriodData shenzhenperiodrecords = shenzhen.getNodeXPeroidData(recordsperiod);
		Double szdiff = shenzhenperiodrecords.getChengJiaoErDailyAverageDifferenceWithLastPeriod(requireddate);
		
		if(shdiff == null)
			return null;
		try {
			double result = shdiff + szdiff; 
			return result;
		} catch ( java.lang.NullPointerException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public String getNodeXDataInHtml(TDXNodes superbk, LocalDate requireddate)
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
			 
			 Double curcje = this.getChengJiaoEr(requireddate);
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
			 fontcje.appendText("�ɽ���" + decimalformate.format(curcje) + cjedanwei);
			 fontcje.attr("color", "#AF7AC5");
			 
			 Double avecje = this.getAverageDailyChengJiaoErOfWeek (requireddate);
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
			 fontavecje.appendText("����ƽ���ɽ���" + decimalformate.format(avecje) + cjedanwei);
			 fontavecje.attr("color", "#AF7AC5");
			 
			 Double avecjegrowingrate = null;;
			 try {
				 avecjegrowingrate = this.getAverageDailyChenJiaoErGrowingRate(requireddate);
				 if(avecjegrowingrate != null) {
					 htmlstring = "���վ��ɽ���������" + percentFormat.format (avecjegrowingrate) ;
					 org.jsoup.nodes.Element liavecjechangerate = dl.appendElement("li");
					 org.jsoup.nodes.Element fontavecjechangerate = liavecjechangerate.appendElement("font");
					 fontavecjechangerate.appendText(htmlstring);
					 fontavecjechangerate.attr("color", "#AF7AC5 ");
				 }
			 } catch (java.lang.NullPointerException e) {    }
			 
			 Integer avecjemaxwk = this.getAverageDailyChenJiaoErMaxWeek (requireddate);
			 org.jsoup.nodes.Element liavecjemaxwk = dl.appendElement("li");
			 org.jsoup.nodes.Element fontavecjemaxwk = liavecjemaxwk.appendElement("font");
			 fontavecjemaxwk.appendText("����ƽ���ɽ���MaxWK= " + avecjemaxwk);
			 fontavecjemaxwk.attr("color", "#AF7AC5");
			 
			 Integer cjemaxwk = null;
		     try{
		    	cjemaxwk = this.getChenJiaoErMaxWeek (requireddate);//��ʾ�ɽ����Ƕ��������,�ɽ����������Сû�����壬��Ϊ������������ܳɽ������ǻ��С
		     } catch (java.lang.NullPointerException e) {
		    		
		     }
			 if(cjemaxwk!= null && cjemaxwk>0) {
				 org.jsoup.nodes.Element licjemaxwk = dl.appendElement("li");
				 org.jsoup.nodes.Element fontcjemaxwk = licjemaxwk.appendElement("font");
				 fontcjemaxwk.appendText("�ɽ���MaxWk=" + cjemaxwk);
				 fontcjemaxwk.attr("color", "#AF7AC5 ");
			 } 
			 
			 //
			 Double curcjl = this.getChengJiaoLiang(requireddate);
	    	 String cjldanwei = FormatDoubleToShort.getNumberChineseDanWei(curcjl);
	    	 curcjl = FormatDoubleToShort.formateDoubleToShort (curcjl);
			 org.jsoup.nodes.Element licjl = dl.appendElement("li");
			 org.jsoup.nodes.Element fontcjl = licjl.appendElement("font");
			 fontcjl.appendText("�ɽ���" + decimalformate.format(curcjl) + cjldanwei);
			 fontcjl.attr("color", "#641E16");
			 //
			 Integer cjlmaxwk = null;
		     try{
		    		cjlmaxwk = this.getChenJiaoLiangMaxWeek (requireddate);//��ʾcjl�Ƕ��������
		     } catch (java.lang.NullPointerException e) {
		     }
			 if(cjlmaxwk != null && cjlmaxwk>0) {
				 org.jsoup.nodes.Element licjlmaxwk = dl.appendElement("li");
				 org.jsoup.nodes.Element fontcjlmaxwk = licjlmaxwk.appendElement("font");
				 fontcjlmaxwk.appendText("�ɽ���MaxWk=" + cjlmaxwk);
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
	public Boolean isLocalDateReachFristDayInHistory(LocalDate requireddate) {
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
		return "000000";
	}

	@Override
	public Boolean hasFxjgInPeriod(LocalDate requireddate) {
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
	public Integer getZhangTingTongJi(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getDieTingTongJi(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getQueKouTongJiOpenUp(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getQueKouTongJiOpenDown(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getQueKouTongJiHuiBuUp(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getQueKouTongJiHuiBuDown(LocalDate requireddate) {
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
	public Double getChenJiaoErZhanBi(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}

	

	@Override
	public Double getChenJiaoLiangZhanBi(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getIndexOfSpecificDateOHLCData(LocalDate requireddate)
	{
		// TODO Auto-generated method stub
		String recordsperiod = getNodeperiodtype();
		NodeXPeriodData shanghaiperiodrecords = shanghai.getNodeXPeroidData(recordsperiod);
		Integer index = shanghaiperiodrecords.getIndexOfSpecificDateOHLCData(requireddate);
		return index;
	}

	@Override
	public Double getSpecificOHLCZhangDieFu(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LocalDate getOHLCRecordsStartDate() {
		// TODO Auto-generated method stub
		String recordsperiod = getNodeperiodtype();
		NodeXPeriodData shanghaiperiodrecords = shanghai.getNodeXPeroidData(recordsperiod);
		return shanghaiperiodrecords.getOHLCRecordsStartDate ();
	}

	@Override
	public LocalDate getOHLCRecordsEndDate() {
		// TODO Auto-generated method stub
		String recordsperiod = getNodeperiodtype();
		NodeXPeriodData shanghaiperiodrecords = shanghai.getNodeXPeroidData(recordsperiod);
		return shanghaiperiodrecords.getOHLCRecordsEndDate ();
	}
	public OHLCSeries getOHLCData ()
	{
		String recordsperiod = getNodeperiodtype();
		NodeXPeriodData shanghaiperiodrecords = shanghai.getNodeXPeroidData(recordsperiod);
		return ((TDXNodesXPeriodDataForJFC)shanghaiperiodrecords).getOHLCData();
	}
	public TimeSeries getMA250 ()
	{
		String recordsperiod = getNodeperiodtype();
		NodeXPeriodData shanghaiperiodrecords = shanghai.getNodeXPeroidData(recordsperiod);
		return ((TDXNodesXPeriodDataForJFC)shanghaiperiodrecords).getMA250();
	}
	public TimeSeries getMA60 ()
	{
		String recordsperiod = getNodeperiodtype();
		NodeXPeriodData shanghaiperiodrecords = shanghai.getNodeXPeroidData(recordsperiod);
		return ((TDXNodesXPeriodDataForJFC)shanghaiperiodrecords).getMA60();
	}
	@Override
	public Integer getChenJiaoErMaxWeek (LocalDate requireddate) 
	{
 		Double dpcurcje = this.getChengJiaoEr(requireddate);
		
		if(dpcurcje == null)	return null;
		
		int maxweek = 0;
		String recordsperiod = getNodeperiodtype();
		NodeXPeriodData shanghaiperiodrecords = shanghai.getNodeXPeroidData(recordsperiod);
		
		Integer indexofcur = shanghaiperiodrecords.getIndexOfSpecificDateOHLCData(requireddate);
		if(indexofcur == 0)	return maxweek;
		
		Boolean indexclosetofront = false;
		for(int i = 1; indexclosetofront == false ; i++) {
			LocalDate expecteddate = requireddate.minus(i,ChronoUnit.WEEKS).with(DayOfWeek.FRIDAY);	
			
			Integer indexoflast = shanghaiperiodrecords.getIndexOfSpecificDateOHLCData(requireddate);
			if(indexoflast != null && indexoflast == 0) //���̳ɽ���Ϊ��Ҫ�������У�Ҫ���ǵ����¼���㣬Ҫ����
				indexclosetofront = true;
			
			Double expectcje = this.getChengJiaoEr (expecteddate);
			if(expectcje == null && indexclosetofront == false) //��û�����㣬˵�������У�������ǰ��
				continue;
			
			if(expectcje != null && dpcurcje > expectcje)
				maxweek ++;
			else	break;
		}
		
		return maxweek;
	}

	@Override
	public Double getChenJiaoErChangeGrowthRateOfSuperBanKuai(TDXNodes superbk, LocalDate requireddate	) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public Double getChengJiaoLiang(LocalDate requireddate) 
	{
		String recordsperiod = getNodeperiodtype();
		NodeXPeriodData shanghaiperiodrecords = shanghai.getNodeXPeroidData(recordsperiod);
		Double shcurrecord = shanghaiperiodrecords.getChengJiaoLiang(requireddate);
		if(shcurrecord == null)
			return null;
		
		NodeXPeriodData shenzhenperiodrecords = shenzhen.getNodeXPeroidData(recordsperiod);
		Double szcurrecord = shenzhenperiodrecords.getChengJiaoLiang(requireddate);
		
		Double dapancjl = null;
		try {	dapancjl = shcurrecord + szcurrecord;
		} catch (java.lang.NullPointerException e) {e.printStackTrace();}
		return dapancjl;
	}
	
	@Override
	public Double getChenJiaoErChangeGrowthRateOfSuperBanKuaiOnDailyAverage(TDXNodes superbk, LocalDate requireddate) 
	{
		return null;
	}

	@Override
	public Double getChenJiaoLiangDifferenceWithLastPeriod(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public Double getChenJiaoLiangChangeGrowthRateOfSuperBanKuai(TDXNodes superbk, LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public Double[] getNodeOhlcSMA(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getAverageDailyChengJiaoErOfWeek(LocalDate requireddate) 
	{
		Double cje = this.getChengJiaoEr(requireddate);
		if(cje != null) {
			Integer daynum = this.getExchangeDaysNumberForthePeriod(requireddate);
			if(daynum != null)	return cje/daynum;
			else	return cje/5;
		} else	return null;
	}
	
	@Override
	public Integer getAverageDailyChenJiaoErMaxWeek (LocalDate requireddate) 
	{
		Double curcje = this.getAverageDailyChengJiaoErOfWeek(requireddate);
		int maxweek = 0;
		
		String recordsperiod = getNodeperiodtype();
		NodeXPeriodData shanghaiperiodrecords = shanghai.getNodeXPeroidData(recordsperiod);
		int index = this.getIndexOfSpecificDateOHLCData(requireddate);
		for(int i = index-1;i >=0; i--) {
			
			LocalDate lastcjlrecorddate = shanghaiperiodrecords.getLocalDateOfSpecificIndexOfOHLCData(i);
			if(lastcjlrecorddate == null ) //���ܵ��˼�¼��ͷ���ˣ������Ǹ�����ʱ�䲻���İ��
				return maxweek;
			try {
				Double lastcje = this.getAverageDailyChengJiaoErOfWeek(lastcjlrecorddate);
				if(curcje > lastcje)	maxweek ++;
				else	break;
			} catch (java.lang.NullPointerException e) {
//				logger.info(requireddate.toString() + " value is null.");
				break;
			}
		}

		return maxweek;
	}

	@Override
	public Double getSpecificTimeRangeOHLCHightestZhangFu(LocalDate requiredstart, LocalDate requiredend) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getNodeXDataCsvData(TDXNodes superbk, LocalDate requireddate) {
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
	protected RegularTimePeriod getJFreeChartFormateTimePeriod (LocalDate requireddate) 
	{
		String nodeperiod = this.getNodeperiodtype();
		RegularTimePeriod period = null;
		if(nodeperiod.equals(NodeGivenPeriodDataItem.WEEK)) { 
			java.sql.Date lastweek = java.sql.Date.valueOf(requireddate);
			period = new org.jfree.data.time.Week (lastweek);
		} else if(nodeperiod.equals(NodeGivenPeriodDataItem.DAY)) {
			java.sql.Date lastdayofweek = java.sql.Date.valueOf(requireddate);
			period = new org.jfree.data.time.Day (lastdayofweek);
		}  else if(nodeperiod.equals(NodeGivenPeriodDataItem.MONTH)) {
			java.sql.Date lastdayofweek = java.sql.Date.valueOf(requireddate);
			period = new org.jfree.data.time.Month (lastdayofweek);
		}
		
		return period;
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
	public Double[] getNodeOhlcMA(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void calNodeOhlcMA() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void calNodeAMOMA() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Double[] getNodeAMOMA(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Double getAverageDailyChengJiaoLiangOfWeek(LocalDate requireddate) 
	{
		Double cje = this.getChengJiaoLiang(requireddate);
		if(cje != null) {
			Integer daynum = this.getExchangeDaysNumberForthePeriod(requireddate);
			if(daynum != null)
				return cje/daynum;
			else
				return cje/5;
		} 
		
		return null;
	}
	@Override
	public Double getAverageDailyChenJiaoErGrowingRate(LocalDate requireddate) 
	{
		Double avecjecur = this.getAverageDailyChengJiaoErOfWeek (requireddate);
		requireddate = requireddate.minus(1,ChronoUnit.WEEKS).with(DayOfWeek.FRIDAY);  
		Double avecjelast = this.getAverageDailyChengJiaoErOfWeek (requireddate);
		
		return (avecjecur - avecjelast) / avecjelast;
	}

	@Override
	public Double getChenJiaoLiangChangeGrowthRateOfSuperBanKuaiOnDailyAverage(TDXNodes superbk, LocalDate requireddate	) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getChengJiaoLiangDailyAverageDifferenceWithLastPeriod(LocalDate requireddate) 
	{
		String recordsperiod = getNodeperiodtype();
		
		NodeXPeriodData shanghaiperiodrecords = shanghai.getNodeXPeroidData(recordsperiod);
		Double shdiff = shanghaiperiodrecords.getChengJiaoLiangDailyAverageDifferenceWithLastPeriod(requireddate);
		
		NodeXPeriodData shenzhenperiodrecords = shenzhen.getNodeXPeroidData(recordsperiod);
		Double szcurrecord = shenzhenperiodrecords.getChengJiaoLiangDailyAverageDifferenceWithLastPeriod(requireddate);
		
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
	public TimeSeries getAMOData() {
		String recordsperiod = getNodeperiodtype();
		NodeXPeriodData shanghaiperiodrecords = shanghai.getNodeXPeroidData(recordsperiod);
		TimeSeries shamodata = shanghaiperiodrecords.getAMOData();
		return shamodata;
	}
	@Override
	public TimeSeries getVOLData() {
		String recordsperiod = getNodeperiodtype();
		NodeXPeriodData shanghaiperiodrecords = shanghai.getNodeXPeroidData(recordsperiod);
		TimeSeries shvoldata = shanghaiperiodrecords.getVOLData();
		return shvoldata;
	}

	@Override
	public Object getNodeDataByKeyWord(String keyword, LocalDate date, String... maformula) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeNodeDataFromSpecificDate(LocalDate requireddate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setNotCalWholeWeekMode(LocalDate date) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public LocalDate isInNotCalWholeWeekMode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getChenJiaoErZhanBiGrowthRateForDaPan(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getChenJiaoErZhanBiMaxWeekForDaPan(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getChenJiaoErZhanBiMinWeekForDaPan(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getChenJiaoErZhanBiMinestWeekForDaPanInSpecificPeriod(LocalDate requireddate, int checkrange) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getChenJiaoLiangZhanBiGrowthRateForDaPan(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getChenJiaoLiangZhanBiMaxWeekForDaPan(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getChenJiaoLiangZhanBiMinWeekForDaPan(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getAverageDailyChenJiaoErLianXuFangLiangPeriodNumber(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getChenJiaoLiangMaxWeek(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer getAverageDailyChenJiaoLiangMaxWeek(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean checkCloseComparingToMAFormula(LocalDate requireddate, String maformula) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Multimap<LocalDate, LocalDate> isMacdButtomDivergenceInSpecificMonthRange(LocalDate requireddate,
			int monthrange) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Multimap<LocalDate, LocalDate> isMacdTopDivergenceInSpecificMonthRange(LocalDate requireddate,
			int monthrange) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Integer getChenJiaoErZhanBiDpMaxWkLianXuFangLiangPeriodNumber(LocalDate requireddate, int settindpgmaxwk) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double getAverageDailyChenJiaoLiangGrowingRate(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addDaPanChenJiaoErZhanBiGrowingRate(LocalDate requireddate, Double cjezbgr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addDaPanChenJiaoLiangZhanBiGrowingRate(LocalDate requireddate, Double cjezbgr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Double[] getSpcificDateOHLCData(LocalDate requireddate) {
		// TODO Auto-generated method stub
		return null;
	}

	
}

