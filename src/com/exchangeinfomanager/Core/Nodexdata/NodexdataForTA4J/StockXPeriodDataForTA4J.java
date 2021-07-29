package com.exchangeinfomanager.Core.Nodexdata.NodexdataForTA4J;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jsoup.Jsoup;

import com.exchangeinfomanager.Core.Nodes.DaPan;
import com.exchangeinfomanager.Core.Nodes.TDXNodes;
import com.exchangeinfomanager.Core.Nodexdata.StockNodesXPeriodData;
import com.exchangeinfomanager.Core.Nodexdata.ohlcvaprimarydata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.commonlib.FormatDoubleToShort;


public class StockXPeriodDataForTA4J extends TDXNodesXPeriodDataForTA4J implements StockNodesXPeriodData
{
		public StockXPeriodDataForTA4J (String nodecode, String nodeperiodtype1) 
		{
			super(nodecode , nodeperiodtype1);
			
			stockhuanshoulv = new TimeSeries(nodeperiodtype1);
			stockliutongshizhi = new TimeSeries(nodeperiodtype1);
			stockzongshizhi = new TimeSeries(nodeperiodtype1);
			periodhighestzhangdiefu = new TimeSeries(nodeperiodtype1);
			periodlowestzhangdiefu = new TimeSeries(nodeperiodtype1);
		}
		
		private Logger logger = Logger.getLogger(StockXPeriodDataForTA4J.class);
		
		private  TimeSeries stockhuanshoulv; //������
		private  TimeSeries stockliutongshizhi; //��ͨ��ֵ
		private  TimeSeries stockzongshizhi; //����ֵ
		private  TimeSeries periodhighestzhangdiefu; //����Ƿ�
		private  TimeSeries periodlowestzhangdiefu; //����Ƿ�
		private  TimeSeries stockgzjl; //��ע��¼
		
		/*
		 * (non-Javadoc)
		 * @see com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic#hasFxjgInPeriod(java.time.LocalDate, int)
		 */
		public Integer hasGzjlInPeriod (LocalDate requireddate,int difference)
		{
			if(this.stockgzjl == null)
				return null;
			
			TimeSeriesDataItem gzjlitem = stockgzjl.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
			if(gzjlitem == null)
				return null;
			
			Integer value = (Integer)gzjlitem.getValue();
			return value;
		}
		/*
		 * 
		 */
		public void addGzjlToPeriod (RegularTimePeriod period,Integer fxjg) //1��������ע��0�����Ƴ���ע
		{
			if(this.stockgzjl == null)
				stockgzjl = new TimeSeries(super.getNodeperiodtype() );
			
			try {
				stockgzjl.add(period,fxjg);
//				 addOrUpdate()
			} catch (org.jfree.data.general.SeriesException e) {
//				e.printStackTrace();
			}
		}
		/*
		 * (non-Javadoc)
		 * @see com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData#addNewXPeriodData(com.exchangeinfomanager.asinglestockinfo.TDXNodeGivenPeriodDataItem)
		 */
		public void addNewXPeriodData (NodeGivenPeriodDataItem kdata)
		{
			super.addNewXPeriodData(kdata);
			
			try {
				stockhuanshoulv.setNotify(false);
				if(kdata.getHuanShouLv() != null && kdata.getHuanShouLv() !=0.0 )
					stockhuanshoulv.add(kdata.getJFreeChartPeriod(super.getNodeperiodtype()),kdata.getHuanShouLv(),false);
			} catch (org.jfree.data.general.SeriesException e) {
			} 
			try {
				stockliutongshizhi.setNotify(false);
				if(kdata.getLiuTongShiZhi() != null && kdata.getLiuTongShiZhi() !=  0.0 )
					stockliutongshizhi.add(kdata.getJFreeChartPeriod(super.getNodeperiodtype()),kdata.getLiuTongShiZhi(),false);
			} catch (org.jfree.data.general.SeriesException e) {
//				System.out.println(kdata.getMyOwnCode() + kdata.getPeriod() + "�����Ѿ����ڣ�" + kdata.getPeriod().getStart() + "," + kdata.getPeriod().getEnd() + ")");
//				e.printStackTrace();
			}
			try {	
				stockzongshizhi.setNotify(false);
				if( kdata.getZongShiZhi() != null && kdata.getZongShiZhi() != 0)
					stockzongshizhi.add(kdata.getJFreeChartPeriod(super.getNodeperiodtype()),kdata.getZongShiZhi(),false);
				
			} catch (org.jfree.data.general.SeriesException e) {
//				System.out.println(kdata.getMyOwnCode() + kdata.getPeriod() + "�����Ѿ����ڣ�" + kdata.getPeriod().getStart() + "," + kdata.getPeriod().getEnd() + ")");
//				e.printStackTrace();
			}
			try {	
				periodhighestzhangdiefu.setNotify(false);
				if( kdata.getPeriodHighestZhangDieFu() != null && kdata.getPeriodHighestZhangDieFu() != 0)
					periodhighestzhangdiefu.add(kdata.getJFreeChartPeriod(super.getNodeperiodtype()),kdata.getPeriodHighestZhangDieFu(),false);
				
			} catch (org.jfree.data.general.SeriesException e) {
//				System.out.println(kdata.getMyOwnCode() + kdata.getPeriod() + "�����Ѿ����ڣ�" + kdata.getPeriod().getStart() + "," + kdata.getPeriod().getEnd() + ")");
//				e.printStackTrace();
			}
			try {	
				periodlowestzhangdiefu.setNotify(false);
				if( kdata.getPeriodLowestZhangDieFu() != null && kdata.getPeriodLowestZhangDieFu() != 0)
					periodlowestzhangdiefu.add(kdata.getJFreeChartPeriod(super.getNodeperiodtype()), kdata.getPeriodLowestZhangDieFu(),false);
				
			} catch (org.jfree.data.general.SeriesException e) {
//				System.out.println(kdata.getMyOwnCode() + kdata.getPeriod() + "�����Ѿ����ڣ�" + kdata.getPeriod().getStart() + "," + kdata.getPeriod().getEnd() + ")");
//				e.printStackTrace();
			}
		}
		/*
		 * (non-Javadoc)
		 * @see com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData#getChenJiaoErMaxWeekOfSuperBanKuai(java.time.LocalDate, int)
		 */
		public Double getSpecificTimeHuanShouLv (LocalDate requireddate,int difference)
		{
			try{
				TimeSeriesDataItem curhslrecord = stockhuanshoulv.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
				Double curhsl = curhslrecord.getValue().doubleValue();
				
				return curhsl;
			} catch (java.lang.NullPointerException e) {
//				e.printStackTrace();
				return null;
			}
			
		}
		/*
		 * (non-Javadoc)
		 * @see com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData#getChenJiaoErMaxWeekOfSuperBanKuai(java.time.LocalDate, int)
		 */
		public Double getSpecificTimeZongShiZhi (LocalDate requireddate,int difference)
		{
			TimeSeriesDataItem curzszrecord = stockzongshizhi.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
			Double curzsz = null ;
			try {
				curzsz = curzszrecord.getValue().doubleValue();
			} catch (Exception e) {
//				e.printStackTrace();
				
			}
			
			return curzsz;
		}
		/*
		 * �õ�ĳ��ʱ�ڵ��ǵ���
		 */
		public Double getSpecificTimeHighestZhangDieFu (LocalDate requireddate,int difference)
		{
			TimeSeriesDataItem curhighzdfrecord = periodhighestzhangdiefu.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
//			TimeSeriesDataItem curhighzdfrecord = periodhighestzhangdiefu.getDataItem( getJFreeChartFormateTimePeriod(LocalDate.parse("2019-01-04"),difference));
			Double curhzdf = null ;
			try {
				curhzdf = curhighzdfrecord.getValue().doubleValue();
			} catch (Exception e) {
//				e.printStackTrace();
				logger.info( super.getNodeCode() + ":" + requireddate + difference    + "��û�����ݣ�������ͣ��" );
				return null;
			}
			
			return curhzdf;
		}
		/*
		 * 
		 */
		public Double getSpecificTimeLowestZhangDieFu (LocalDate requireddate,int difference)
		{
			TimeSeriesDataItem curlowzdfrecord = periodlowestzhangdiefu.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
			Double curlzdf = null ;
			try {
				curlzdf = curlowzdfrecord.getValue().doubleValue();
			} catch (Exception e) {
				e.printStackTrace();
				logger.info(" ");
			}
			
			return curlzdf;
		}
		/*
		 * (non-Javadoc)
		 * @see com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData#getChenJiaoErMaxWeekOfSuperBanKuai(java.time.LocalDate, int)
		 */
		public Double getSpecificTimeLiuTongShiZhi (LocalDate requireddate,int difference)
		{
			TimeSeriesDataItem curltszrecord = stockliutongshizhi.getDataItem( getJFreeChartFormateTimePeriod(requireddate,difference));
			try{
				Double curltsz = curltszrecord.getValue().doubleValue();
				return curltsz;
			} catch (java.lang.NullPointerException e ) {
				return null;
			}
		}
		/*
		 * (non-Javadoc)
		 * @see com.exchangeinfomanager.nodes.nodexdata.NodeXPeriodDataBasic#getNodeXDataInHtml(java.time.LocalDate, int)
		 */
		@Override
		public String getNodeXDataInHtml(TDXNodes superbk,LocalDate requireddate, int difference) 
		{
			String datahtml = super.getNodeXDataInHtml(superbk,requireddate,difference);
			
			org.jsoup.nodes.Document doc = Jsoup.parse(datahtml);
			org.jsoup.select.Elements content = doc.select("body");
			org.jsoup.select.Elements dl = content.select("dl");
			
			
			Double liutongshizhi = null; String liutongshizhidanwei = null;
			liutongshizhi = this.getSpecificTimeLiuTongShiZhi(requireddate, 0);
			if(liutongshizhi != null) {
					liutongshizhidanwei = FormatDoubleToShort.getNumberChineseDanWei(liutongshizhi);
					liutongshizhi = FormatDoubleToShort.formateDoubleToShort( liutongshizhi);
					
					try{
			    		DecimalFormat decimalformate = new DecimalFormat("#0.000"); //",###";
			        	NumberFormat percentFormat = NumberFormat.getPercentInstance(Locale.CHINA);
			        	percentFormat.setMinimumFractionDigits(4);
						org.jsoup.nodes.Element li5 =  dl.get(0).appendElement("li");
						li5.appendText( "��ͨ��ƽ����ֵ" + decimalformate.format(liutongshizhi) + liutongshizhidanwei );
					} catch (java.lang.IllegalArgumentException e) {
						
					} catch (java.lang.IndexOutOfBoundsException e) {
						e.printStackTrace();
					}
			}
	    	
				 
			Double hsl = null ;
			hsl = this.getSpecificTimeHuanShouLv(requireddate, 0);	 
			try {
					DecimalFormat decimalformate2 = new DecimalFormat("%.3f");
					if(hsl != null) {
						org.jsoup.nodes.Element li4 = dl.get(0).appendElement("li");
						li4.appendText( "������" + String.format("%.3f", hsl) );
					}
					
				} catch (java.lang.IllegalArgumentException e ) {

				} catch (java.lang.NullPointerException ex) {

				}
			
			return doc.toString();
		}
		


	}