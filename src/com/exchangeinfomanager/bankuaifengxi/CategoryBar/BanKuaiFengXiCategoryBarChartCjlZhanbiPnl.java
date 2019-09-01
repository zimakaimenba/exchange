package com.exchangeinfomanager.bankuaifengxi.CategoryBar;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.CategoryPointerAnnotation;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.exchangeinfomanager.bankuaifengxi.ExportCondition;
import com.exchangeinfomanager.commonlib.CommonUtility;
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

public class BanKuaiFengXiCategoryBarChartCjlZhanbiPnl extends BanKuaiFengXiCategoryBarChartPnl 
{

	public BanKuaiFengXiCategoryBarChartCjlZhanbiPnl() 
	{
		super ();

		super.plot.setRenderer(0,new CustomCategroyRendererForCjlZhanBi() );
	
		
		
//		linechartdataset = new XYSeriesCollection();
//		plot.setDataset(1, linechartdataset);
		
        
        // change the rendering order so the primary dataset appears "behind" the 
        // other datasets...
//        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
	}
	
	
	private static Logger logger = Logger.getLogger(BanKuaiFengXiCategoryBarChartCjeZhanbiPnl.class);
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.bankuaifengxi.BarChartPanelDataChangedListener#updatedDate(com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode, java.time.LocalDate, int, java.lang.String)
	 * 升级占比数据
	 */
	public void updatedDate (TDXNodes node, LocalDate startdate, LocalDate enddate,String period)
	{
//		this.setNodeCjeZhanBi (node,startdate,enddate,period);
		barchartdataset.clear();
		
		super.setCurDisplayNode(node,period );
//		super.globeperiod = period;
//		BanKuaiFengXiCategoryBarRenderer render = (BanKuaiFengXiCategoryBarRenderer)super.plot.getRenderer();
//		render.setDisplayNode(node);
		
		NodeXPeriodDataBasic nodexdata = node.getNodeXPeroidData(period);
		
		displayDataToGui (nodexdata,startdate,enddate,period);
		
//		CustomCategroyRendererForZhanBi render = (CustomCategroyRendererForZhanBi)super.plot.getRenderer();
//		render.setDisplayNode(this.curdisplayednode);
//		render.setDisplayNodeXPeriod (nodexdata);
//		
//		//如有分析结果，ticklable显示红色
		CategoryLabelCustomizableCategoryAxis axis = (CategoryLabelCustomizableCategoryAxis)super.plot.getDomainAxis();
		axis.setDisplayNode(this.getCurDisplayedNode(),period);
		
	}
	/*
	 * 
	 */
	private void displayDataToGui (NodeXPeriodDataBasic nodexdata,LocalDate startdate,LocalDate enddate,String period)
	{
		DaPan dapan;
		if(this.getCurDisplayedNode().getType() == BkChanYeLianTreeNode.BKGEGU) {
			BanKuai bk = ((StockOfBanKuai)this.getCurDisplayedNode()).getBanKuai();
			dapan = (DaPan)bk.getRoot();
		}
		else
			dapan = (DaPan)super.getCurDisplayedNode().getRoot();
		
		LocalDate requireend = enddate.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = startdate.with(DayOfWeek.MONDAY);

		super.barchart.setNotify(false);
		
		double highestHigh =0.0; int qkmax =0;//设置显示范围
	
		LocalDate tmpdate = requirestart;
		do {
			LocalDate wkfriday = tmpdate.with(DayOfWeek.FRIDAY);
			//ZhanBi Part
			Double cjlzb = nodexdata.getChenJiaoLiangZhanBi(tmpdate, 0);
			if(cjlzb != null) {
				barchartdataset.setValue(cjlzb,super.getRowKey(),wkfriday );

				if(cjlzb > highestHigh)
					highestHigh = cjlzb;
			} else {
				if( !dapan.isDaPanXiuShi(tmpdate,0,period) ) {
					cjlzb = 0.0;
					barchartdataset.setValue(0.0,super.getRowKey(),wkfriday );
				} 
			}
			
			
			if(period.equals(TDXNodeGivenPeriodDataItem.WEEK))
				tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
			else if(period.equals(TDXNodeGivenPeriodDataItem.DAY))
				tmpdate = tmpdate.plus(1, ChronoUnit.DAYS) ;
			else if(period.equals(TDXNodeGivenPeriodDataItem.MONTH))
				tmpdate = tmpdate.plus(1, ChronoUnit.MONTHS) ;
			
		} while (tmpdate.isBefore( requireend) || tmpdate.isEqual(requireend));
		
		setPanelTitle ("成交量" + period + super.getCurDisplayedNode().getMyOwnCode(),requireend);
		
		super.barchart.setNotify(true);
		operationAfterDataSetup (nodexdata,requirestart,requireend,highestHigh,qkmax,period);
	}
	/*
	 * 突出显示一些预先设置
	 */
	private void operationAfterDataSetup (NodeXPeriodDataBasic nodexdata,LocalDate startdate, LocalDate enddate, double highestHigh, int upqkmax, String period) 
	{
		if(highestHigh == 0.0)
			return;
		
		BanKuaiFengXiCategoryBarRenderer render = (BanKuaiFengXiCategoryBarRenderer)super.plot.getRenderer();
		render.setDisplayNode(this.getCurDisplayedNode());
		render.setDisplayNodeXPeriod (nodexdata);
		
		//如有分析结果，ticklable显示红色
		CategoryLabelCustomizableCategoryAxis axis = (CategoryLabelCustomizableCategoryAxis)super.plot.getDomainAxis(0);
		axis.setDisplayNode(this.getCurDisplayedNode(),period);
		
		try{
			super.plot.getRangeAxis(0).setRange(0, highestHigh*1.12);
		} catch (java.lang.IllegalArgumentException e) {
			e.printStackTrace();
			super.plot.getRangeAxis(0).setRange(0, 1);
			
		}
		
		try{
			super.plot.getRangeAxis(3).setRange(0, upqkmax*1.12);
		} catch (java.lang.IllegalArgumentException e) {
//			e.printStackTrace();
			super.plot.getRangeAxis(3).setRange(0, 1);
//			super.linechartdataset.clear();
		}
		
		super.plot.getRenderer(3).setSeriesPaint(0, Color.PINK);
		super.plot.getRenderer(3).setSeriesPaint(1, new Color(255,102,0));
		
		super.decorateXaxisWithYearOrMonth("month".trim());
		
		

	}

	@Override
	public void hightLightFxValues(ExportCondition expc) 
	{
//		Integer cjezbdporbkmax = expc.getSettinDpmaxwk();
//		Integer cjezbdporbkmin = expc.getSettingDpminwk();
//		Double cjemin = expc.getSettingCjemin();
//		Double cjemax = expc.getSettingCjeMax();
//		Integer cjemaxwk = expc.getSettingCjemaxwk();
//		Double shoowhsl = expc.getSettingHsl();
//		
//		if(cjezbdporbkmax != null) {
//			((BanKuaiFengXiCategoryBarRenderer)plot.getRenderer()).setDisplayMaxwkLevel (cjezbdporbkmax);
//			this.barchart.fireChartChanged();//必须有这句
//		}
//		if(cjezbdporbkmin != null) {
//			((BanKuaiFengXiCategoryBarRenderer)plot.getRenderer()).setDisplayMinwkLevel (cjezbdporbkmin);
//			this.barchart.fireChartChanged();//必须有这句
//		}
	}

	@Override
	public String getToolTipSelected() 
	{
		int indexx = barchartdataset.getColumnIndex(super.dateselected);
		
		CustomCategroyToolTipGeneratorForCjlZhanBi ttpg = 	
		(CustomCategroyToolTipGeneratorForCjlZhanBi)(((CustomCategroyRendererForCjlZhanBi) plot.getRenderer()).getBaseToolTipGenerator());
		
		String tooltips = ttpg.generateToolTip(super.barchartdataset, 0, indexx);
		
		return tooltips;
	}

}
/*
 * 
 */
class CustomCategroyRendererForCjlZhanBi extends BanKuaiFengXiCategoryBarRenderer 
{
	private static final long serialVersionUID = 1L;
   
    public CustomCategroyRendererForCjlZhanBi() {
        super();
        super.displayedmaxwklevel = 4;
        super.displayedminwklevel = 8;
        super.displayedcolumncolorindex = new Color(255,153,153);
        
        this.setBarPainter(new StandardBarPainter());
		
        BanKuaiFengXiCategoryBarToolTipGenerator custotooltip = new CustomCategroyToolTipGeneratorForCjlZhanBi();
		this.setBaseToolTipGenerator(custotooltip);
		
//		DecimalFormat decimalformate = new DecimalFormat("%#0.000");
//		this.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}",decimalformate));

		
		BkfxItemLabelGenerator labelgenerator = new BkfxItemLabelGeneratorForCjlZhanBi ();
		this.setBaseItemLabelGenerator(labelgenerator);
		this.setBaseItemLabelsVisible(true);
		labelgenerator.setDisplayedMaxWkLevel(super.displayedmaxwklevel);
		labelgenerator.setDisplayedMinWkLevel(super.displayedminwklevel);
    }

    public Paint getItemLabelPaint(final int row, final int column)
    {
    	CategoryPlot plot = getPlot ();
    	CategoryDataset dataset = plot.getDataset();
		String selected =  dataset.getColumnKey(column).toString();
    	LocalDate selecteddate = CommonUtility.formateStringToDate(selected);

    	Integer maxweek = nodexdata.getChenJiaoLiangZhanBiMaxWeekOfSuperBanKuai(selecteddate,0);
    	Integer minweek = nodexdata.getChenJiaoLiangZhanBiMinWeekOfSuperBanKuai(selecteddate,0);
    	
		if(maxweek != null  && maxweek >= super.displayedmaxwklevel)
			return Color.MAGENTA;
		else if (minweek != null && minweek != 0 && minweek >=  super.displayedminwklevel)
			return Color.GREEN;
		else
			return Color.black;
    }
    
}

class BkfxItemLabelGeneratorForCjlZhanBi extends BkfxItemLabelGenerator 
{
	public BkfxItemLabelGeneratorForCjlZhanBi ()
	{
		super (new DecimalFormat("%#0.000"));
	}
	@Override
	public String generateColumnLabel(CategoryDataset dataset, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String generateLabel(CategoryDataset dataset, int row, int column) {
//		String selected =  dataset.getColumnKey(column).toString();
//		
//    	LocalDate selecteddate = CommonUtility.formateStringToDate(selected);
////    	if(selecteddate.equals(LocalDate.parse("2018-11-16"))) {
////    		String result2 = "";
////    	}
//    	
//		Integer maxweek = nodexdata.getChenJiaoLiangZhanBiMaxWeekOfSuperBanKuai(selecteddate,0);
//		Integer minweek = nodexdata.getChenJiaoLiangZhanBiMinWeekOfSuperBanKuai(selecteddate,0);
//		
//		String result = "";
//		if(maxweek != null && maxweek >= super.displayedmaxwklevel) {
//			NumberFormat nf = this.getNumberFormat();
//			result =  nf.format(dataset.getValue(row, column));
//		} else if (minweek != null && minweek != 0 && minweek >=  super.displayedminwklevel) {
//			NumberFormat nf = this.getNumberFormat();
//			result =  nf.format(dataset.getValue(row, column));
//		}
//		
//		return result;
		return null;
	}

	@Override
	public String generateRowLabel(CategoryDataset arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}
	
}

class CustomCategroyToolTipGeneratorForCjlZhanBi extends BanKuaiFengXiCategoryBarToolTipGenerator 
{
	public String generateToolTip(CategoryDataset dataset, int row, int column)  
    {
		String selected = dataset.getColumnKey(column).toString();
    	LocalDate selecteddate = CommonUtility.formateStringToDate(selected);
    	
		String html;
		DaPan dapan = (DaPan)  super.node.getRoot();
		if(super.node.getType() == BkChanYeLianTreeNode.TDXGG ) {
			 html = ( (StockNodeXPeriodData) nodexdata).getNodeXDataInHtml(dapan,selecteddate, 0);
		} else {
			html = nodexdata.getNodeXDataInHtml(dapan,selecteddate, 0);
		}
		
		return html;
    }
}