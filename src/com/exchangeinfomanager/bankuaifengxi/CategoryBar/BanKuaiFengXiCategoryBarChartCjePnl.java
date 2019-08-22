package com.exchangeinfomanager.bankuaifengxi.CategoryBar;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import org.jfree.data.Range;
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

import com.exchangeinfomanager.bankuaifengxi.ExportCondition;
import com.exchangeinfomanager.bankuaifengxi.QueKou;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.ConnectDataBase;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.DaPan;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.nodexdata.NodeXPeriodDataBasic;
import com.exchangeinfomanager.nodes.nodexdata.StockNodeXPeriodData;
import com.exchangeinfomanager.nodes.nodexdata.TDXNodeGivenPeriodDataItem;
import com.exchangeinfomanager.nodes.nodexdata.TDXNodesXPeriodData;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;

public class BanKuaiFengXiCategoryBarChartCjePnl extends BanKuaiFengXiCategoryBarChartPnl 
{

	public BanKuaiFengXiCategoryBarChartCjePnl() 
	{
		super ();
		super.plot.setRenderer(0,new CustomRendererForCje() );
		
		
	}
	
	public void updatedDate (TDXNodes node, LocalDate startdate, LocalDate enddate,String period)
	{
		super.setCurDisplayNode( node,period );
		
		this.preparingdisplayDataToGui (node,startdate,enddate,period);
	}
	
	 private static Logger logger = Logger.getLogger(BanKuaiFengXiCategoryBarChartCjePnl.class);
//	/*
//	 * ��鰴�������ĳ���Ľ��׶�
//	 */
//	public void setBanKuaiJiaoYiEr (TDXNodes node, LocalDate displayedenddate1,String period)
//	{
//		LocalDate requireend = displayedenddate1.with(DayOfWeek.SATURDAY);
//		LocalDate requirestart = displayedenddate1.with(DayOfWeek.MONDAY).minus(this.shoulddisplayedmonthnum,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
//		
//		this.preparingdisplayDataToGui (node,requirestart,requireend,period);
//	}
	/*
	 * 
	 */
	private void preparingdisplayDataToGui (TDXNodes node,LocalDate startdate,LocalDate enddate,String period)
	{
		NodeXPeriodDataBasic nodexdata = node.getNodeXPeroidData(period);
//		
		super.resetDate();
//		barchartdataset.clear();
		super.barchart.setNotify(false);
		
		Double leftrangeaxix = displayBarDataToGui (nodexdata,startdate,enddate,period);

		Integer qkmax = displayQueKouLineDataToGui(nodexdata,period);
		
		Integer zdt = displayZhangDieTingLineDataToGui(nodexdata,period);
		
//		xiuShiLeftRangeAxixAfterDispalyDate (nodexdata,startdate,enddate,leftrangeaxix,period);
//		xiuShiRightRangeAxixAfterDispalyDate(nodexdata,rightrangeaxix,period);
		
		super.barchart.setNotify(true);
	}
	/*
	 * 
	 */
	public Integer displayZhangDieTingLineDataToGui(NodeXPeriodDataBasic nodexdata, String period) 
	{
		if(super.barchartdataset.getColumnCount() ==0 )
			return null;
		
//		if(linequekouchartdataset != null)
//			linequekouchartdataset.clear();
		
		LocalDate indexrequirestart = (LocalDate) barchartdataset.getColumnKey(0);
		LocalDate indexrequireend = (LocalDate) barchartdataset.getColumnKey(barchartdataset.getColumnCount()-1);
		
		LocalDate requireend = indexrequireend.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = indexrequirestart.with(DayOfWeek.SATURDAY);
		
		int zdtmax = 0;//������ʾ��Χ
		LocalDate tmpdate = requirestart; 
		do  {
			LocalDate wkfriday = tmpdate.with(DayOfWeek.FRIDAY);
			
			//ȱ�� Line Part
			if(super.getCurDisplayedNode().getType() != BkChanYeLianTreeNode.DAPAN) {
				Integer zttj = ( (TDXNodesXPeriodData) nodexdata).getZhangTingTongJi(tmpdate, 0);
				Integer dttj = ( (TDXNodesXPeriodData) nodexdata).getDieTingTongJi(tmpdate, 0);
				 
//				if(zttj != null) {
//					super.linezdtchartdataset.setValue(zttj, "ZhangTing", wkfriday );
//					if (zttj > zdtmax)
//						zdtmax = zttj ;
//				}	else
//					super.linezdtchartdataset.setValue(0, "ZhangTing", wkfriday );
				
				if(dttj != null) {
					super.linequekouchartdataset.setValue(dttj, "DieTing", wkfriday );
					if (dttj > zdtmax)
						zdtmax = dttj ;
				}	else
					super.linequekouchartdataset.setValue(0, "DieTing", wkfriday );
				
			
			}
			
			
			if(period.equals(TDXNodeGivenPeriodDataItem.WEEK))
				tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
			else if(period.equals(TDXNodeGivenPeriodDataItem.DAY))
				tmpdate = tmpdate.plus(1, ChronoUnit.DAYS) ;
			else if(period.equals(TDXNodeGivenPeriodDataItem.MONTH))
				tmpdate = tmpdate.plus(1, ChronoUnit.MONTHS) ;
			
		} while (tmpdate.isBefore( requireend) || tmpdate.isEqual(requireend));
		
		this.xiuShiRightRangeAxixAfterDispalyDate(nodexdata, zdtmax, period);
		return zdtmax;
		
	}
	/*
	 * 
	 */
	public Double displayBarDataToGui (NodeXPeriodDataBasic nodexdata,LocalDate startdate,LocalDate enddate,String period) 
	{
		DaPan dapan;
		if(super.getCurDisplayedNode().getType() == BkChanYeLianTreeNode.BKGEGU) {
			BanKuai bk = ((StockOfBanKuai)super.getCurDisplayedNode() ).getBanKuai();
			dapan = (DaPan)bk.getRoot();
		} else
			dapan = (DaPan)(this.getCurDisplayedNode().getRoot());
		
		LocalDate requireend = enddate.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = startdate.with(DayOfWeek.SATURDAY);
		
//		barchartdataset.clear();
//		super.barchart.setNotify(false);
		
		double highestHigh =0.0; //������ʾ��Χ
		
		LocalDate tmpdate = requirestart;
		do  {
			//����Ӧ�ø�������������ѡ���������ͣ�������Ϊ�������ߣ��Ͳ�ϸ����
			Double cje = nodexdata.getChengJiaoEr(tmpdate, 0);
			LocalDate wkfriday = tmpdate.with(DayOfWeek.FRIDAY);
			if(cje != null) {
				barchartdataset.setValue(cje,super.getRowKey(), wkfriday);
				
				if(cje > highestHigh)
					highestHigh = cje;
			} else {
				if( !dapan.isDaPanXiuShi(tmpdate,0,period) ) {
					barchartdataset.setValue(0.0,super.getRowKey(),wkfriday);
				} 
			}
			
			if(period.equals(TDXNodeGivenPeriodDataItem.WEEK))
				tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
			else if(period.equals(TDXNodeGivenPeriodDataItem.DAY))
				tmpdate = tmpdate.plus(1, ChronoUnit.DAYS) ;
			else if(period.equals(TDXNodeGivenPeriodDataItem.MONTH))
				tmpdate = tmpdate.plus(1, ChronoUnit.MONTHS) ;
			
		} while (tmpdate.isBefore( requireend) || tmpdate.isEqual(requireend));

//		super.barchart.setNotify(true);
//		xiuShiGuiAfterDispalyDate (nodexdata,requirestart,requireend,highestHigh,qkmax,period);
		xiuShiLeftRangeAxixAfterDispalyDate (nodexdata,startdate,enddate,highestHigh,period);
		
		return highestHigh;
	}
	/*
	 * 
	 */
	public Integer displayQueKouLineDataToGui (NodeXPeriodDataBasic nodexdata,String period) 
	{
		if(super.barchartdataset.getColumnCount() ==0 )
			return null;
		
//		if(linequekouchartdataset != null)
//			linequekouchartdataset.clear();
		
		LocalDate indexrequirestart = (LocalDate) barchartdataset.getColumnKey(0);
		LocalDate indexrequireend = (LocalDate) barchartdataset.getColumnKey(barchartdataset.getColumnCount()-1);
		
		LocalDate requireend = indexrequireend.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = indexrequirestart.with(DayOfWeek.SATURDAY);
		
		int qkmax = 0;//������ʾ��Χ
		LocalDate tmpdate = requirestart; 
		do  {
			LocalDate wkfriday = tmpdate.with(DayOfWeek.FRIDAY);
			
			//ȱ�� Line Part
			if(super.getCurDisplayedNode().getType() != BkChanYeLianTreeNode.DAPAN) {
				Integer opneupquekou = ( (TDXNodesXPeriodData) nodexdata).getQueKouTongJiOpenUp(tmpdate, 0);
				Integer opendownquekou = ( (TDXNodesXPeriodData) nodexdata).getQueKouTongJiOpenDown(tmpdate, 0);
				Integer huibuupquekou = ( (TDXNodesXPeriodData) nodexdata).getQueKouTongJiHuiBuUp(tmpdate, 0);
				Integer huibudowquekou = ( (TDXNodesXPeriodData) nodexdata).getQueKouTongJiHuiBuDown(tmpdate, 0);	
				 
				if(opendownquekou != null) {
					super.linequekouchartdataset.setValue(opendownquekou, "QueKouOpenDown", wkfriday );
					if (opendownquekou > qkmax)
						qkmax = opendownquekou ;
				}	else
					super.linequekouchartdataset.setValue(0, "QueKouOpenDown", wkfriday );
				
				if(huibuupquekou != null) {
					super.linequekouchartdataset.setValue(huibuupquekou, "QueKouHuiBuUpQk", wkfriday );
					if (huibuupquekou > qkmax)
						qkmax = huibuupquekou ;
				}	else
					super.linequekouchartdataset.setValue(0, "QueKouHuiBuUpQk", wkfriday );
				
				if(opneupquekou != null && opneupquekou > qkmax) 
					qkmax = opneupquekou;
				if(huibudowquekou != null && huibudowquekou >qkmax)
					qkmax = huibudowquekou;
			}
			
			
			if(period.equals(TDXNodeGivenPeriodDataItem.WEEK))
				tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
			else if(period.equals(TDXNodeGivenPeriodDataItem.DAY))
				tmpdate = tmpdate.plus(1, ChronoUnit.DAYS) ;
			else if(period.equals(TDXNodeGivenPeriodDataItem.MONTH))
				tmpdate = tmpdate.plus(1, ChronoUnit.MONTHS) ;
			
		} while (tmpdate.isBefore( requireend) || tmpdate.isEqual(requireend));
		
		this.xiuShiRightRangeAxixAfterDispalyDate(nodexdata, qkmax, period);
		
		return qkmax;

	}
	/*
	 * 
	 */
	private void xiuShiLeftRangeAxixAfterDispalyDate (NodeXPeriodDataBasic nodexdata,LocalDate startdate, LocalDate enddate, double highestHigh, String period)
	{
		if(highestHigh == 0.0)
			return;
		
		CustomRendererForCje cjerender = (CustomRendererForCje) super.plot.getRenderer(0); 
		cjerender.setDisplayNode(this.getCurDisplayedNode() );
		cjerender.setDisplayNodeXPeriod (nodexdata);
			
		CategoryLabelCustomizableCategoryAxis axis = (CategoryLabelCustomizableCategoryAxis)super.plot.getDomainAxis(0);
		axis.setDisplayNode(this.getCurDisplayedNode(),period);
		
		try{
			super.plot.getRangeAxis(0).setRange(0, highestHigh*1.12);
		} catch(java.lang.IllegalArgumentException e) {
//			e.printStackTrace();
			super.plot.getRangeAxis(0).setRange(0, 1);
		}
		
//		try{
//			super.plot.getRangeAxis(3).setRange(0, downqkmax*1.12);
//		} catch(java.lang.IllegalArgumentException e) {
//			super.plot.getRangeAxis(3).setRange(0, 1);
//			super.linechartdataset.clear();
//			e.printStackTrace();
//			
//		}
//		
//		super.plot.getRenderer(3).setSeriesPaint(0, new Color(0,102,0) );
//		super.plot.getRenderer(3).setSeriesPaint(1, new Color(102,255,102) );
		
		setPanelTitle ("�ɽ���",enddate);
		super.decorateXaxisWithYearOrMonth("year".trim());
	}
	private void xiuShiRightRangeAxixAfterDispalyDate (NodeXPeriodDataBasic nodexdata,int rightrangeaxixmax, String period)
	{
		if(rightrangeaxixmax == 0)
			return;
		
		Range rangeData = super.plot.getRangeAxis(3).getRange();
		double upbound = rangeData.getUpperBound();
		try{
			if(rightrangeaxixmax*1.12 > upbound)
				super.plot.getRangeAxis(3).setRange(0, rightrangeaxixmax*1.12);
		} catch(java.lang.IllegalArgumentException e) {
			super.plot.getRangeAxis(3).setRange(0, 1);
//			super.linechartdataset.clear();
//			e.printStackTrace();
			
		}
		
		super.plot.getRenderer(3).setSeriesPaint(0, new Color(0,102,0) );
		super.plot.getRenderer(3).setSeriesPaint(1, new Color(102,255,102) );
		super.plot.getRenderer(3).setSeriesPaint(2, new Color(204,0,0) );
		
	}

	/*
	 * 
	 */
	@Override
	public void hightLightFxValues(ExportCondition expc) {
//		Integer cjezbtoupleveldpmax = expc.getSettinDpmaxwk();
//		Double cjemin = expc.getSettingCjemin();
//		Double cjemax = expc.getSettingCjeMax();
		Integer cjemaxwk = expc.getSettingCjemaxwk();
//		Double shoowhsl = expc.getSettingHsl();
		
		if(cjemaxwk != null) {
			((BanKuaiFengXiCategoryBarRenderer)plot.getRenderer()).setDisplayMaxwkLevel (cjemaxwk);
			this.barchart.fireChartChanged();//���������
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

    	Integer maxweek;
    	try{
    		maxweek = nodexdata.getChenJiaoErMaxWeekOfSuperBanKuai(selecteddate,0);
    	} catch ( java.lang.NullPointerException e) {
    		return null;
    	}
		
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
//    	nodexdata = super.node.getNodeXPeroidData(period);
    			
		String html;
		DaPan dapan = (DaPan)  super.node.getRoot();
		if(super.node.getType() == BkChanYeLianTreeNode.TDXGG ) {
			 html = ( (StockNodeXPeriodData) nodexdata).getNodeXDataInHtml(dapan,selecteddate, 0);
		} else {
			html = nodexdata.getNodeXDataInHtml(dapan,selecteddate, 0);
		}
		
		return html;
		
//		String selected = dataset.getColumnKey(column).toString();
//    	LocalDate selecteddate = CommonUtility.formateStringToDate(selected);
//    	
//    	Double curcje = (Double)dataset.getValue(row, column);
//    	String cjedanwei = getNumberChineseDanWei(curcje);
//    	curcje = formateCjeToShort (curcje);
////    	if(curcje >= 100000000) {
////    		curcje = curcje / 100000000;
////    		danwei = "��";
////    	}  	else if(curcje >= 10000000 && curcje <100000000) {
////    		curcje = curcje / 10000000;
////    		danwei = "ǧ��";
////    	}  	else if(curcje >= 1000000 && curcje <10000000) {
////    		curcje = curcje / 1000000;
////    		danwei = "����";
////    	}
//    		
//    	DecimalFormat decimalformate = new DecimalFormat("#0.000"); //",###";
//    	NumberFormat percentFormat = NumberFormat.getPercentInstance(Locale.CHINA);
//    	percentFormat.setMinimumFractionDigits(4);
//		
//    	Integer maxwk;
//    	try{
//    		maxwk = nodexdata.getChenJiaoErMaxWeekOfSuperBanKuai(selecteddate,0);//��ʾ�ɽ����Ƕ��������
//    	} catch (java.lang.NullPointerException e) {
//    		return null;
//    	}
//		DaPan dapan = (DaPan)  super.node.getRoot();
//		Double changerate = nodexdata.getChenJiaoErChangeGrowthRateOfSuperBanKuai(dapan,selecteddate,0);//�ɽ�����̱仯������
//		
//		Double liutongshizhi = null; String liutongshizhidanwei = null;
//		if(super.node.getType() == BkChanYeLianTreeNode.TDXGG) {
//			liutongshizhi = ((StockNodeXPeriodData)nodexdata).getSpecificTimeLiuTongShiZhi(selecteddate, 0);
//			if(liutongshizhi == null) {
//				liutongshizhidanwei = "";
//			} else {
//				liutongshizhidanwei = getNumberChineseDanWei(liutongshizhi);
//				liutongshizhi = formateCjeToShort (liutongshizhi);
//			}
//		}
//		
//		String htmlstring = "";
//		org.jsoup.nodes.Document doc = Jsoup.parse(htmlstring);
//		Elements body = doc.getElementsByTag("body");
//		for(Element elbody : body) {
//			 org.jsoup.nodes.Element dl = elbody.appendElement("dl");
//			 
//			 org.jsoup.nodes.Element li3 = dl.appendElement("li");
//			 li3.appendText(selecteddate.toString());
//			 
//			 org.jsoup.nodes.Element li = dl.appendElement("li");
//			 li.appendText("�ɽ���" + decimalformate.format(curcje) + cjedanwei);
//			 
//			 org.jsoup.nodes.Element li2 = dl.appendElement("li");
//			 li2.appendText("�ɽ���MaxWk=" + maxwk);
//			 
//			 
//			 try{
//				 htmlstring = "�ɽ�����̱仯������" + percentFormat.format (changerate) ;
//				 
//				 org.jsoup.nodes.Element li4 = dl.appendElement("li");
//				 li4.appendText(htmlstring);
//			 } catch (java.lang.IllegalArgumentException e) {
////				 li4.appendText("�ɽ�����̱仯������NULL" );
//			 }
//			 
//			 if(super.node.getType() == BkChanYeLianTreeNode.TDXGG) {
//				 
//				 try{
//					 htmlstring = "��ͨ��ƽ����ֵ" + decimalformate.format(liutongshizhi) + liutongshizhidanwei ;
//					 
//					 org.jsoup.nodes.Element li5 = dl.appendElement("li");
//					 li5.appendText(htmlstring);
//				 } catch (java.lang.IllegalArgumentException e) {
////					 li5.appendText("��ͨ��ƽ����ֵ=NULL");
//				 }
//			 }
//			 
//			 if(super.node.getType() == BkChanYeLianTreeNode.TDXGG || super.node.getType() == BkChanYeLianTreeNode.TDXBK ) {
//				 Integer opneupquekou = ( (TDXNodesXPeriodData) nodexdata).getQueKouTongJiOpenUp(selecteddate, 0);
//				 if( opneupquekou != null) {
//					 org.jsoup.nodes.Element li6 = dl.appendElement("li");
//					 li6.appendText("ȱ��OpenUp =" + opneupquekou);
//				 }
//				 Integer opendownquekou = ( (TDXNodesXPeriodData) nodexdata).getQueKouTongJiOpenDown(selecteddate, 0);
//				 if( opendownquekou != null) {
//					 org.jsoup.nodes.Element li7 = dl.appendElement("li");
//					 li7.appendText("ȱ��OpenDown =" + opendownquekou);
//				 }
//				 Integer huibuupquekou = ( (TDXNodesXPeriodData) nodexdata).getQueKouTongJiHuiBuUp(selecteddate, 0);
//				 if( huibuupquekou != null) {
//					 org.jsoup.nodes.Element li8 = dl.appendElement("li");
//					 li8.appendText("ȱ��HuiBuUp =" + huibuupquekou );
//				 }
//				 Integer huibudowquekou = ( (TDXNodesXPeriodData) nodexdata).getQueKouTongJiHuiBuDown(selecteddate, 0);
//				 if( huibudowquekou != null) {
//					 org.jsoup.nodes.Element li9 = dl.appendElement("li");
//					 li9.appendText("ȱ��HuiBuDown =" + huibudowquekou );
//				 }
//			 }
//			 
//			 
//		}
//		
//		htmlstring = doc.toString();
//		return htmlstring;
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
    		danwei = "��";
    	}  	else if(number >= 10000000 && number <100000000) {
//    		number = number / 10000000;
    		danwei = "ǧ��";
    	}  	else if(number >= 1000000 && number <10000000) {
//    		number = number / 1000000;
    		danwei = "����";
    	} else if(number >= 100000 && number <1000000) {
//    		number = number / 100000;
    		danwei = "ʮ��";
    	} else if(number >= 10000 && number <100000) {
//    		number = number / 10000;
    		danwei = "��";
    	}
    	
    	return danwei;
		
	}
}