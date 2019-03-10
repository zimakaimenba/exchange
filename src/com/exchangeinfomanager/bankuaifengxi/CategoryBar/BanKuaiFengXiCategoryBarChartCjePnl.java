package com.exchangeinfomanager.bankuaifengxi.CategoryBar;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.time.Week;
import org.jfree.data.time.ohlc.OHLCItem;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.ui.TextAnchor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.DaPan;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.asinglestockinfo.Stock.StockNodeXPeriodData;
import com.exchangeinfomanager.bankuaifengxi.QueKou;
import com.exchangeinfomanager.asinglestockinfo.StockGivenPeriodDataItem;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.ConnectDataBase;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;

public class BanKuaiFengXiCategoryBarChartCjePnl extends BanKuaiFengXiCategoryBarChartPnl 
{

	public BanKuaiFengXiCategoryBarChartCjePnl() 
	{
		super ();
		super.plot.setRenderer(new CustomRendererForCje() );
		
//		((CustomRendererForCje) plot.getRenderer()).setBarPainter(new StandardBarPainter());
//		
//		CustomCategoryToolTipGeneratorForChenJiaoEr custotooltip = new CustomCategoryToolTipGeneratorForChenJiaoEr();
//		((CustomRendererForCje) plot.getRenderer()).setBaseToolTipGenerator(custotooltip);
//		
////		DecimalFormat decimalformate = new DecimalFormat(",###");//("#0.000");
////		((CustomRendererForCje) plot.getRenderer()).setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}",decimalformate));
////		((CustomRendererForCje) plot.getRenderer()).setBaseItemLabelsVisible(true);
//		
//		BkfxItemLabelGenerator labelgenerator = new BkfxItemLabelGenerator ();
//		((CustomRendererForCje) plot.getRenderer()).setBaseItemLabelGenerator(labelgenerator);
	}
	public void updatedDate (BkChanYeLianTreeNode node, LocalDate date, int difference,String period)
	{
		if(period.equals(StockGivenPeriodDataItem.DAY))
			date = date.plus(difference,ChronoUnit.DAYS);
		else if(period.equals(StockGivenPeriodDataItem.WEEK))
			date = date.plus(difference,ChronoUnit.WEEKS);
		else if(period.equals(StockGivenPeriodDataItem.MONTH))
			date = date.plus(difference,ChronoUnit.MONTHS);
			
		setBanKuaiJiaoYiEr(node,date,period);
	}
	public void updatedDate (BkChanYeLianTreeNode node, LocalDate startdate, LocalDate enddate,String period)
	{
		this.setBanKuaiJiaoYiEr (node,startdate,enddate,period);
	}
	
	 private static Logger logger = Logger.getLogger(BanKuaiFengXiCategoryBarChartCjePnl.class);
	/*
	 * 板块按周相对于某板块的交易额
	 */
	public void setBanKuaiJiaoYiEr (BkChanYeLianTreeNode node, LocalDate displayedenddate1,String period)
	{
		LocalDate requireend = displayedenddate1.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = displayedenddate1.with(DayOfWeek.MONDAY).minus(this.shoulddisplayedmonthnum,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		
		this.setBanKuaiJiaoYiEr (node,requirestart,requireend,period);
	}
	/*
	 * 
	 */
	public void setBanKuaiJiaoYiEr (BkChanYeLianTreeNode node,LocalDate startdate,LocalDate enddate,String period)
	{
		this.curdisplayednode = node;
		NodeXPeriodDataBasic nodexdata = node.getNodeXPeroidData(period);
		displayDataToGui (nodexdata,startdate,enddate,period);
	}
	/*
	 * 
	 */
	private void displayDataToGui (NodeXPeriodDataBasic nodexdata,LocalDate startdate,LocalDate enddate,String period) 
	{
		DaPan dapan = (DaPan)this.curdisplayednode.getRoot();
		
		LocalDate requireend = enddate.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = startdate.with(DayOfWeek.SATURDAY);
		
		barchartdataset.clear();
		super.barchart.setNotify(false);
		
		double highestHigh =0.0; //设置显示范围
		
//		TimeSeries rangecje = nodexdata.getRangeChengJiaoEr(startdate, enddate);
//		TimeSeries rangecje =  nodexdata.getChengJiaoEr ();
		LocalDate tmpdate = requirestart;
		do  {
			//这里应该根据周期类型来选择日期类型，现在因为都是周线，就不细化了
//				org.jfree.data.time.Week tmpwk = new Week(Date.from(tmpdate.atStartOfDay(ZoneId.systemDefault()).toInstant()) );
//			TimeSeriesDataItem cjerecord = rangecje.getDataItem(tmpwk);
//			tmpwk = null;
			Double cje = nodexdata.getChengJiaoEr(tmpdate, 0);
			if(cje != null) {
//				double cje = cjerecord.getValue().doubleValue();
				
				barchartdataset.setValue(cje,"占比",tmpdate.with(DayOfWeek.FRIDAY));
				
				if(cje > highestHigh)
					highestHigh = cje;
			} else {
				if( !dapan.isDaPanXiuShi(tmpdate,0,period) ) {
					barchartdataset.setValue(0.0,"占比",tmpdate);
				} 
			}
			
			if(period.equals(StockGivenPeriodDataItem.WEEK))
				tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
			else if(period.equals(StockGivenPeriodDataItem.DAY))
				tmpdate = tmpdate.plus(1, ChronoUnit.DAYS) ;
			else if(period.equals(StockGivenPeriodDataItem.MONTH))
				tmpdate = tmpdate.plus(1, ChronoUnit.MONTHS) ;
			
		} while (tmpdate.isBefore( requireend) || tmpdate.isEqual(requireend));

		super.barchart.setNotify(true);
		xiuShiGuiAfterDispalyDate (nodexdata,requirestart,requireend,highestHigh,period);
	}
	/*
	 * 
	 */
//	private List<QueKou> queKouTongJi ()
//	{
//		List<QueKou> qklist = new ArrayList<QueKou> ();
//		
//		NodeXPeriodDataBasic nodexdata = super.curdisplayednode.getNodeXPeroidData(StockGivenPeriodDataItem.DAY);
//		OHLCSeries nodeohlc = nodexdata.getOHLCData();
//		for(int j=1;j < ohlcSeries.getItemCount();j++) {
//			
//			OHLCItem kxiandatacurwk = (OHLCItem) ohlcSeries.getDataItem(j);
//			RegularTimePeriod curperiod = kxiandatacurwk.getPeriod();
//			LocalDate curstart = curperiod.getStart().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//			LocalDate curend = curperiod.getEnd().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//			
//			OHLCItem kxiandatalastwk = (OHLCItem) ohlcSeries.getDataItem(j-1);
//			
//			Double curhigh = kxiandatacurwk.getHighValue();
//			Double curlow = kxiandatacurwk.getLowValue();
//
//			Iterator itr = qklist.iterator(); 
//			while (itr.hasNext()) 
//			{ 
//				QueKou tmpqk = (QueKou)itr.next();
//				if (!tmpqk.isQueKouHuiBu() ) {
//					String huibuinfo = tmpqk.checkQueKouHuiBu(curstart,curlow, curhigh);
//					if(tmpqk.isQueKouHuiBu() && !tmpqk.shouldUpdatedInDb())
//						itr.remove(); 
//				}
//			} 
//			
//			//看看有没有产生新的缺口
//			double lasthigh = kxiandatalastwk.getHighValue();
//			double lastlow = kxiandatalastwk.getLowValue();
//			
//			Boolean tiaokongup = false; Boolean tiaokongdown = false;
//			if(curlow > lasthigh) {
//				Double newqkup = curlow;
//				Double newqkdown = lasthigh;
//				QueKou newqk = new QueKou (this.curdisplayednode.getMyOwnCode(),curstart,newqkdown,newqkup,true);
//				qklist.add(0, newqk);
//			}
//			else 
//			if(curhigh < lastlow) {
//				Double newqkup = lastlow;
//				Double newqkdown = curhigh;
//				QueKou newqk = new QueKou (this.curdisplayednode.getMyOwnCode(),curstart,newqkdown,newqkup,false);
//				qklist.add(0, newqk);
//			}
//		}
//		
//		return qklist;
//	}
//	/*
//	 * 
//	 */
//	private void displayQueKouToChart ()
//	{
//		ohlcSeries.setNotify(false);
//        candlestickDataset.setNotify(false);
//        candlestickChart.setNotify(false);
//        
//		if(ohlcSeries == null || ohlcSeries.getItemCount() == 0)
//			return ;
//
//		List<QueKou> qklist = queKouTongJi ();
//		for(QueKou tmpqk : qklist) {
//			LocalDate qkdate = tmpqk.getQueKouDate();
//			LocalDate qkhuidate = tmpqk.getQueKouHuiBuDate();
//			if(qkhuidate == null)
//				continue;
//			
//			java.sql.Date sqlqkdate = null;
//			try {
//				DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
//				 sqlqkdate = new java.sql.Date(format.parse(qkdate.toString()).getTime());
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
//			org.jfree.data.time.Day qkday = new org.jfree.data.time.Day (sqlqkdate);
//			
//			java.sql.Date sqlqkhbdate = null;
//			try {
//				DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
//				 sqlqkhbdate = new java.sql.Date(format.parse(qkhuidate.toString()).getTime());
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
//			org.jfree.data.time.Day qkhbday = new org.jfree.data.time.Day (sqlqkhbdate);
//			
//			Double qkup = tmpqk.getQueKouUp();
//			//draw annotation
//	        try {
//		        double millisonemonth = qkday.getFirstMillisecond();
//		        XYPointerAnnotation pointeronemonth = new XYPointerAnnotation(String.valueOf(qkup), millisonemonth, qkup, 0 );
//		        pointeronemonth.setBaseRadius(0.0);
//		        pointeronemonth.setTipRadius(25.0);
//		        pointeronemonth.setFont(new Font("SansSerif", Font.BOLD, 9));
//		        pointeronemonth.setPaint(Color.CYAN);
//		        pointeronemonth.setTextAnchor(TextAnchor.CENTER);
//				candlestickChart.getXYPlot().addAnnotation(pointeronemonth);
//	        } catch (java.lang.NullPointerException e) {
//	        }
//	        
//	        try {
//		        double millisonemonth = qkhbday.getFirstMillisecond();
//		        XYPointerAnnotation pointeronemonth = new XYPointerAnnotation("回补", millisonemonth, qkup, 0 );
//		        pointeronemonth.setBaseRadius(0.0);
//		        pointeronemonth.setTipRadius(25.0);
//		        pointeronemonth.setFont(new Font("SansSerif", Font.BOLD, 9));
//		        pointeronemonth.setPaint(Color.YELLOW);
//		        pointeronemonth.setTextAnchor(TextAnchor.CENTER);
//				candlestickChart.getXYPlot().addAnnotation(pointeronemonth);
//	        } catch (java.lang.NullPointerException e) {
//	        }
//			
//		}
//		
//		 ohlcSeries.setNotify(true);
//	     candlestickDataset.setNotify(true);
//	     candlestickChart.setNotify(true);
//	     ohlcSeries.setNotify(false);
//	     candlestickDataset.setNotify(false);
//	     candlestickChart.setNotify(false);
//	}
	/*
	 * 
	 */
	private void xiuShiGuiAfterDispalyDate (NodeXPeriodDataBasic nodexdata,LocalDate startdate, LocalDate enddate, double highestHigh, String period)
	{
		CustomRendererForCje cjerender = (CustomRendererForCje) super.plot.getRenderer(); 
		cjerender.setDisplayNode(this.curdisplayednode);
		cjerender.setDisplayNodeXPeriod (nodexdata);
			
		CategoryLabelCustomizableCategoryAxis axis = (CategoryLabelCustomizableCategoryAxis)super.plot.getDomainAxis();
		axis.setDisplayNode(this.curdisplayednode,period);
		
		try{
			super.plot.getRangeAxis().setRange(0, highestHigh*1.12);
		} catch(java.lang.IllegalArgumentException e) {
			e.printStackTrace();
		}
	
		setPanelTitle ("成交额",enddate);
		super.decorateXaxisWithYearOrMonth("year".trim());
	}
	/*
	 * 
	 */
	@Override
	public void hightLightFxValues(Integer cjezdpkmax,Integer cjezbbkmax, Double cjemin, Double cjemax, Integer cjemaxwk
									,Double showhsl,Double ltszmin,Double ltszmax) 
	{
		
	}
	@Override
	public void hightLightFxValues(Integer cjezbtoupleveldpmax, Double cjemin,Double cjemax, Integer cjemaxwk, Double shoowhsl) {
		// TODO Auto-generated method stub
		if(cjemaxwk != null) {
			((BanKuaiFengXiCategoryBarRenderer)plot.getRenderer()).setDisplayMaxwkLevel (cjemaxwk);
			this.barchart.fireChartChanged();//必须有这句
		}
		
	}
	/*
	 * 
	 */
	@Override
	public String getToolTipSelected ()
	{
		int indexx = barchartdataset.getColumnIndex(super.dateselected);
		
		CustomCategoryToolTipGeneratorForChenJiaoEr ttpg = 	
		(CustomCategoryToolTipGeneratorForChenJiaoEr)(((CustomRendererForCje) plot.getRenderer()).getBaseToolTipGenerator());
		
		String tooltips = ttpg.generateToolTip(super.barchartdataset, 0, indexx);
		
		return tooltips;
	}
}

class CustomRendererForCje extends BanKuaiFengXiCategoryBarRenderer 
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(CustomRendererForCje.class);
   
    public CustomRendererForCje() {
        super();
        super.displayedmaxwklevel = 7;
        super.displayedcolumncolorindex = Color.orange;
        
        this.setBarPainter(new StandardBarPainter());
		
		CustomCategoryToolTipGeneratorForChenJiaoEr custotooltip = new CustomCategoryToolTipGeneratorForChenJiaoEr();
		this.setBaseToolTipGenerator(custotooltip);
		
//		DecimalFormat decimalformate = new DecimalFormat(",###");//("#0.000");
//		this.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}",decimalformate));
		
		BkfxItemLabelGenerator labelgenerator = new BkfxItemLabelGeneratorForCje ();
		this.setBaseItemLabelGenerator(labelgenerator);
		this.setBaseItemLabelsVisible(true);
		
		labelgenerator.setDisplayedMaxWkLevel(super.displayedmaxwklevel);
    }

    public Paint getItemLabelPaint(final int row, final int column)
    {
    	CategoryPlot plot = getPlot ();
    	CategoryDataset dataset = plot.getDataset();
		String selected =  dataset.getColumnKey(column).toString();
		
    	LocalDate selecteddate = CommonUtility.formateStringToDate(selected);
    	 
		Integer maxweek = nodexdata.getChenJiaoErMaxWeekOfSuperBanKuai(selecteddate,0);
		
		if(maxweek != null && maxweek >= super.displayedmaxwklevel)
			return Color.CYAN;
		else 
			return Color.black;
    }
    
    @Override
    public LegendItem getLegendItem(int dataset, int series) 
    {
        LegendItem legendItem = super.getLegendItem(dataset, series);
        logger.debug(dataset + " " + series + " " + legendItem.getShape());
        // modify legendItem here
        return legendItem;
    }
    
}

class BkfxItemLabelGeneratorForCje extends BkfxItemLabelGenerator 
{
	public BkfxItemLabelGeneratorForCje ()
	{
		super (new DecimalFormat(",###"));
	}
	@Override
	public String generateColumnLabel(CategoryDataset dataset, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String generateLabel(CategoryDataset dataset, int row, int column) {
		String selected =  dataset.getColumnKey(column).toString();
		
    	LocalDate selecteddate = CommonUtility.formateStringToDate(selected);
//    	if(selecteddate.equals(LocalDate.parse("2018-11-16"))) {
//    		String result2 = "";
//    	}
    	
		Integer maxweek = nodexdata.getChenJiaoErMaxWeekOfSuperBanKuai(selecteddate,0);
		
		String result = "";
		if(maxweek != null && maxweek >= super.displayedmaxwklevel) {
			NumberFormat nf = this.getNumberFormat();
			result =  nf.format(dataset.getValue(row, column));
		} 
		
		return result;
	}

	@Override
	public String generateRowLabel(CategoryDataset arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}
	
}



class CustomCategoryToolTipGeneratorForChenJiaoEr extends BanKuaiFengXiCategoryBarToolTipGenerator  
{
	public String generateToolTip(CategoryDataset dataset, int row, int column)  
    {
		String selected = dataset.getColumnKey(column).toString();
    	LocalDate selecteddate = CommonUtility.formateStringToDate(selected);
    	
    	Double curcje = (Double)dataset.getValue(row, column);
    	String cjedanwei = getNumberChineseDanWei(curcje);
    	curcje = formateCjeToShort (curcje);
//    	if(curcje >= 100000000) {
//    		curcje = curcje / 100000000;
//    		danwei = "亿";
//    	}  	else if(curcje >= 10000000 && curcje <100000000) {
//    		curcje = curcje / 10000000;
//    		danwei = "千万";
//    	}  	else if(curcje >= 1000000 && curcje <10000000) {
//    		curcje = curcje / 1000000;
//    		danwei = "百万";
//    	}
    		
    	DecimalFormat decimalformate = new DecimalFormat("#0.000"); //",###";
    	NumberFormat percentFormat = NumberFormat.getPercentInstance(Locale.CHINA);
    	percentFormat.setMinimumFractionDigits(4);
		
		Integer maxwk = nodexdata.getChenJiaoErMaxWeekOfSuperBanKuai(selecteddate,0);//显示成交额是多少周最大
		Double changerate = nodexdata.getChenJiaoErChangeGrowthRateOfSuperBanKuai(selecteddate,0);//成交额大盘变化贡献率
		
		Double liutongshizhi = null; String liutongshizhidanwei = null;
		if(super.node.getType() == BanKuaiAndStockBasic.TDXGG) {
			liutongshizhi = ((StockNodeXPeriodData)nodexdata).getSpecificTimeLiuTongShiZhi(selecteddate, 0);
			if(liutongshizhi == null) {
				liutongshizhidanwei = "";
			} else {
				liutongshizhidanwei = getNumberChineseDanWei(liutongshizhi);
				liutongshizhi = formateCjeToShort (liutongshizhi);
			}
			
		}
		
		String htmlstring = "";
		org.jsoup.nodes.Document doc = Jsoup.parse(htmlstring);
		Elements body = doc.getElementsByTag("body");
		for(Element elbody : body) {
			 org.jsoup.nodes.Element dl = elbody.appendElement("dl");
			 
			 org.jsoup.nodes.Element li3 = dl.appendElement("li");
			 li3.appendText(selecteddate.toString());
			 
			 org.jsoup.nodes.Element li = dl.appendElement("li");
			 li.appendText("成交额" + decimalformate.format(curcje) + cjedanwei);
			 
			 org.jsoup.nodes.Element li2 = dl.appendElement("li");
			 li2.appendText("成交额MaxWk=" + maxwk);
			 
			 org.jsoup.nodes.Element li4 = dl.appendElement("li");
			 try{
				 li4.appendText("成交额大盘变化贡献率" + percentFormat.format (changerate) );
			 } catch (java.lang.IllegalArgumentException e) {
				 li4.appendText("成交额大盘变化贡献率NULL" );
			 }
			 
			 if(super.node.getType() == BanKuaiAndStockBasic.TDXGG) {
				 org.jsoup.nodes.Element li5 = dl.appendElement("li");
				 try{
					 li5.appendText("流通周平均市值" + decimalformate.format(liutongshizhi) + liutongshizhidanwei );
				 } catch (java.lang.IllegalArgumentException e) {
					 li5.appendText("流通周平均市值=NULL");
				 }
			 }
		}
		
		htmlstring = doc.toString();
		return htmlstring;
    }
	
	private Double formateCjeToShort(Double curcje) 
	{
		if(curcje >= 100000000) {
			curcje = curcje / 100000000;
    		
    	}  	else if(curcje >= 10000000 && curcje <100000000) {
    		curcje = curcje / 10000000;
    		
    	}  	else if(curcje >= 1000000 && curcje <10000000) {
    		curcje = curcje / 1000000;
    		
    	} else if(curcje >= 100000 && curcje <1000000) {
    		curcje = curcje / 100000;
    		
    	} else if(curcje >= 10000 && curcje <100000) {
    		curcje = curcje / 10000;
    		
    	}
		return curcje;
	}

	private String getNumberChineseDanWei (Double number) 
	{
		String danwei = "";
    	if(number >= 100000000) {
//    		number = number / 100000000;
    		danwei = "亿";
    	}  	else if(number >= 10000000 && number <100000000) {
//    		number = number / 10000000;
    		danwei = "千万";
    	}  	else if(number >= 1000000 && number <10000000) {
//    		number = number / 1000000;
    		danwei = "百万";
    	} else if(number >= 100000 && number <1000000) {
//    		number = number / 100000;
    		danwei = "十万";
    	} else if(number >= 10000 && number <100000) {
//    		number = number / 10000;
    		danwei = "万";
    	}
    	
    	return danwei;
		
	}
}