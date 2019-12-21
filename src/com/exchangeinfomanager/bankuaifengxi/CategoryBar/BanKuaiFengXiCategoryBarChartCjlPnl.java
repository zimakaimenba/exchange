package com.exchangeinfomanager.bankuaifengxi.CategoryBar;

import java.awt.Color;
import java.awt.Paint;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.apache.log4j.Logger;
import org.jfree.chart.LegendItem;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;

import com.exchangeinfomanager.bankuaifengxi.BanKuaiGeGuMatchCondition;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.DaPan;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;

public class BanKuaiFengXiCategoryBarChartCjlPnl extends BanKuaiFengXiCategoryBarChartPnl 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public BanKuaiFengXiCategoryBarChartCjlPnl() 
	{
		super ();
		super.plot.setRenderer(0,new CustomRendererForCjl() );
	}
	public void updatedDate (TDXNodes node, LocalDate startdate, LocalDate enddate,String period)
	{
//		BanKuaiFenagXiCategoryBarRenderer render = (BanKuaiFengXiCategoryBarRenderer)super.plot.getRenderer();
//		render.setDisplayNode(node);
		
		this.setBanKuaiJiaoYiLiang (node,startdate,enddate,period);
	}
	
	 private static Logger logger = Logger.getLogger(BanKuaiFengXiCategoryBarChartCjePnl.class);
	/*
	 * 板块按周相对于某板块的交易额
	 */
	public void setBanKuaiJiaoYiLiang (TDXNodes node, LocalDate displayedenddate1,String period)
	{
		LocalDate requireend = displayedenddate1.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = displayedenddate1.with(DayOfWeek.MONDAY).minus(this.shoulddisplayedmonthnum,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		
		this.setBanKuaiJiaoYiLiang (node,requirestart,requireend,period);
	}
	/*
	 * 
	 */
	public void setBanKuaiJiaoYiLiang (TDXNodes node,LocalDate startdate,LocalDate enddate,String period)
	{
		super.setCurDisplayNode( node,period );
		NodeXPeriodData nodexdata = node.getNodeXPeroidData(period);
		displayDataToGui (nodexdata,startdate,enddate,period);
	}
	/*
	 * 
	 */
	private void displayDataToGui (NodeXPeriodData nodexdata,LocalDate startdate,LocalDate enddate,String period) 
	{
		DaPan dapan;
		if(super.getCurDisplayedNode().getType() == BkChanYeLianTreeNode.BKGEGU) {
			BanKuai bk = ((StockOfBanKuai)super.getCurDisplayedNode() ).getBanKuai();
			dapan = (DaPan)bk.getRoot();
		} else
			dapan = (DaPan)(this.getCurDisplayedNode().getRoot());
		
		LocalDate requireend = enddate.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = startdate.with(DayOfWeek.SATURDAY);
		
		barchartdataset.clear();
		super.barchart.setNotify(false);
		
		double highestHigh =0.0; int qkmax = 0;//设置显示范围
		
		LocalDate tmpdate = requirestart;
		do  {
			//这里应该根据周期类型来选择日期类型，现在因为都是周线，就不细化了
			Double cjl = nodexdata.getChengJiaoLiang(tmpdate, 0);
			LocalDate wkfriday = tmpdate.with(DayOfWeek.FRIDAY);
			if(cjl != null) {
				barchartdataset.setValue(cjl,super.getRowKey(), wkfriday);
				
				if(cjl > highestHigh)
					highestHigh = cjl;
			} else {
				if( !dapan.isDaPanXiuShi(tmpdate,0,period) ) {
					barchartdataset.setValue(0.0,super.getRowKey(),wkfriday);
				} 
			}
			
			if(period.equals(NodeGivenPeriodDataItem.WEEK))
				tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
			else if(period.equals(NodeGivenPeriodDataItem.DAY))
				tmpdate = tmpdate.plus(1, ChronoUnit.DAYS) ;
			else if(period.equals(NodeGivenPeriodDataItem.MONTH))
				tmpdate = tmpdate.plus(1, ChronoUnit.MONTHS) ;
			
		} while (tmpdate.isBefore( requireend) || tmpdate.isEqual(requireend));

		super.barchart.setNotify(true);
		xiuShiGuiAfterDispalyDate (nodexdata,requirestart,requireend,highestHigh,qkmax,period);
	}
	/*
	 * 
	 */
	private void xiuShiGuiAfterDispalyDate (NodeXPeriodData nodexdata,LocalDate startdate, LocalDate enddate, double highestHigh, int downqkmax, String period)
	{
		if(highestHigh == 0.0)
			return;
		
//		CustomRendererForCje cjerender = (CustomRendererForCje) super.plot.getRenderer(0); 
//		cjerender.setDisplayNode(this.getCurDisplayedNode() );
//		cjerender.setDisplayNodeXPeriod (nodexdata);
			
		CategoryLabelCustomizableCategoryAxis axis = (CategoryLabelCustomizableCategoryAxis)super.plot.getDomainAxis(0);
		axis.setDisplayNode(this.getCurDisplayedNode(),period);
		
		try{
			super.plot.getRangeAxis(0).setRange(0, highestHigh*1.12);
		} catch(java.lang.IllegalArgumentException e) {
//			e.printStackTrace();
			super.plot.getRangeAxis(0).setRange(0, 1);
		}
		
		try{
			super.plot.getRangeAxis(3).setRange(0, downqkmax*1.12);
		} catch(java.lang.IllegalArgumentException e) {
			super.plot.getRangeAxis(3).setRange(0, 1);
//			super.linechartdataset.clear();
//			e.printStackTrace();
			
		}
		
		super.plot.getRenderer(3).setSeriesPaint(0, new Color(0,102,0) );
		super.plot.getRenderer(3).setSeriesPaint(1, new Color(102,255,102) );
		
		setPanelTitle ("成交量",enddate);
		super.decorateXaxisWithYearOrMonth("year".trim());
	}
	/*
	 * 
	 */
//	@Override
//	public void hightLightFxValues(Integer cjezdpkmax,Integer cjezbbkmax, Double cjemin, Double cjemax, Integer cjemaxwk
//									,Double showhsl,Double ltszmin,Double ltszmax) 
//	{
//		
//	}
//	@Override
//	public void hightLightFxValues(Integer cjezbtoupleveldpmax, Double cjemin,Double cjemax, Integer cjemaxwk, Double shoowhsl) {
//		// TODO Auto-generated method stub
//		if(cjemaxwk != null) {
//			((BanKuaiFengXiCategoryBarRenderer)plot.getRenderer()).setDisplayMaxwkLevel (cjemaxwk);
//			this.barchart.fireChartChanged();//必须有这句
//		}
//		
//	}
	
	/*
	 * 
	 */
	@Override
	public String getToolTipSelected ()
	{
		int indexx = barchartdataset.getColumnIndex(super.dateselected);
		
		CustomCategoryToolTipGeneratorForChenJiaoLiang ttpg = 	
		(CustomCategoryToolTipGeneratorForChenJiaoLiang)(((CustomRendererForCjl) plot.getRenderer()).getBaseToolTipGenerator());
		
		String tooltips = ttpg.generateToolTip(super.barchartdataset, 0, indexx);
		
		return tooltips;
	}
	@Override
	public void BanKuaiGeGuMatchConditionValuesChanges(BanKuaiGeGuMatchCondition expc) 
	{
		Integer cjezbtoupleveldpmax = expc.getSettingDpMaxWk();
		Double cjemin = expc.getSettingChenJiaoErMin();
		Double cjemax = expc.getSettingChenJiaoErMax();
		Integer cjemaxwk = expc.getSettingChenJiaoErMaxWk();
		Double shoowhsl = expc.getSettingHuanShouLv();
		
		if(cjemaxwk != null) {
			((BanKuaiFengXiCategoryBarRenderer)plot.getRenderer()).setDisplayMaxwkLevel (cjemaxwk);
			this.barchart.fireChartChanged();//必须有这句
		}
		
	}

}

class CustomRendererForCjl extends BanKuaiFengXiCategoryBarRenderer 
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(CustomRendererForCje.class);
   
    public CustomRendererForCjl() {
        super();
        super.displayedmaxwklevel = 7;
        super.displayedcolumncolorindex = new Color(255,178,202);
        
        this.setBarPainter(new StandardBarPainter());
		
        CustomCategoryToolTipGeneratorForChenJiaoLiang custotooltip = new CustomCategoryToolTipGeneratorForChenJiaoLiang();
		this.setBaseToolTipGenerator(custotooltip);
		
//		DecimalFormat decimalformate = new DecimalFormat(",###");//("#0.000");
//		this.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}",decimalformate));
		
		BkfxItemLabelGenerator labelgenerator = new BkfxItemLabelGeneratorForCjl ();
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

class BkfxItemLabelGeneratorForCjl extends BkfxItemLabelGenerator 
{
	public BkfxItemLabelGeneratorForCjl ()
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



class CustomCategoryToolTipGeneratorForChenJiaoLiang extends BanKuaiFengXiCategoryBarToolTipGenerator  
{
	public String generateToolTip(CategoryDataset dataset, int row, int column)  
    {
		String selected = dataset.getColumnKey(column).toString();
    	LocalDate selecteddate = CommonUtility.formateStringToDate(selected);
//    	nodexdata = super.node.getNodeXPeroidData(period);
    			
		String html;
		DaPan dapan = (DaPan)  super.node.getRoot();
		if(super.node.getType() == BkChanYeLianTreeNode.TDXGG ) {
			 html = nodexdata.getNodeXDataInHtml(dapan,selecteddate, 0);
		} else {
			html = nodexdata.getNodeXDataInHtml(dapan,selecteddate, 0);
		}
		
		return html;
    }
	
}