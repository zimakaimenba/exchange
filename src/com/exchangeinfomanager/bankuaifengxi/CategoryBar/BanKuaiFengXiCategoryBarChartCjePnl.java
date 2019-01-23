package com.exchangeinfomanager.bankuaifengxi.CategoryBar;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;

import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.DaPan;
import com.exchangeinfomanager.asinglestockinfo.Stock;
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
	public void hightLightFxValues(Integer cjezdpkmax,Integer cjezbbkmax, Double cjemin, Double cjemax, Integer cjemaxwk,Double showhsl) 
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
    	String danwei = "";
    	if(curcje >= 100000000) {
    		curcje = curcje / 100000000;
    		danwei = "亿";
    	}  	else if(curcje >= 10000000 && curcje <100000000) {
    		curcje = curcje / 10000000;
    		danwei = "千万";
    	}  	else if(curcje >= 1000000 && curcje <10000000) {
    		curcje = curcje / 1000000;
    		danwei = "百万";
    	}
    		
    	DecimalFormat decimalformate = new DecimalFormat("#0.000"); //",###";
    	NumberFormat percentFormat = NumberFormat.getPercentInstance(Locale.CHINA);
    	percentFormat.setMinimumFractionDigits(1);
		
		//显示成交额是多少周最大
		Integer maxwk = nodexdata.getChenJiaoErMaxWeekOfSuperBanKuai(selecteddate,0);
		Double changerate = nodexdata.getChenJiaoErChangeGrowthRateOfSuperBanKuai(selecteddate,0);
		
		String htmlstring = "";
		org.jsoup.nodes.Document doc = Jsoup.parse(htmlstring);
		Elements body = doc.getElementsByTag("body");
		for(Element elbody : body) {
			 org.jsoup.nodes.Element dl = elbody.appendElement("dl");
			 
			 org.jsoup.nodes.Element li3 = dl.appendElement("li");
			 li3.appendText(selecteddate.toString());
			 
			 org.jsoup.nodes.Element li = dl.appendElement("li");
			 li.appendText("成交额" + decimalformate.format(curcje) + danwei);
			 
			 org.jsoup.nodes.Element li2 = dl.appendElement("li");
			 li2.appendText("成交额MaxWk=" + maxwk);
			 
			 org.jsoup.nodes.Element li4 = dl.appendElement("li");
			 try{
				 li4.appendText("成交额变化贡献率" + percentFormat.format (changerate) );
			 } catch (java.lang.IllegalArgumentException e) {
				 li4.appendText("成交额变化贡献率NULL" );
			 }
		}
		
		htmlstring = doc.toString();
		return htmlstring;
    }
}