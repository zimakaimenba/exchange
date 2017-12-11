package com.exchangeinfomanager.bankuaifengxi;

import java.awt.Color;
import java.awt.Paint;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.DaPan;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;

public class BanKuaiFengXiBarChartGgBkZbPnl extends BanKuaiFengXiBarChartPnl
{

	public BanKuaiFengXiBarChartGgBkZbPnl() 
	{
		super ();
		super.plot.setRenderer(new CustomRendererForGgBkZhanBi() );
		((CustomRendererForGgBkZhanBi) plot.getRenderer()).setBarPainter(new StandardBarPainter());
	}
	

	
	/*
	 * 板块大盘/个股板块 占比
	 */
	public void setNodeZhanBiByWeek (BkChanYeLianTreeNode node, LocalDate displayedenddate1, DaPan dapan)
	{
		this.curdisplayednode = node;
//		this.displayedenddate = displayedenddate1;
		LocalDate requireend = displayedenddate1.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = displayedenddate1.with(DayOfWeek.MONDAY).minus(this.shoulddisplayedmonthnum,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		
		barchartdataset = new DefaultCategoryDataset();
		datafx = new DefaultCategoryDataset();
		
		for(LocalDate tmpdate = requirestart;tmpdate.isBefore( requireend) || tmpdate.isEqual(requireend); tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ){
			ChenJiaoZhanBiInGivenPeriod tmprecord = node.getSpecficChenJiaoErRecord(tmpdate);
			if(tmprecord != null) {
				Double chenjiaoer = tmprecord.getCjlZhanBi();
				LocalDate lastdayofweek = tmprecord.getRecordsDayofEndofWeek();
				barchartdataset.setValue(chenjiaoer,"占比",lastdayofweek);
				
				if(tmprecord.hasFengXiJieGuo ())
					datafx.addValue(chenjiaoer/5, "分析结果", lastdayofweek);
				else
					datafx.addValue(0, "分析结果", lastdayofweek);
			} else {
				if( !dapan.isThisWeekXiuShi(tmpdate) ) {
					barchartdataset.setValue(0.0,"占比",tmpdate);
					datafx.addValue(0, "分析结果", tmpdate);
				} else //为空说明该周市场没有交易
					continue;
			}
			
		}
		
		CustomRendererForGgBkZhanBi render = (CustomRendererForGgBkZhanBi)super.plot.getRenderer();
		render.setDisplayNode(node);
		render.setDateSet (barchartdataset);
		
		DecimalFormat decimalformate = new DecimalFormat("%#0.000");
		render.setItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}",decimalformate));
		render.setItemLabelsVisible(true);

		if(node.getType() == 4) { //板块
			CustomToolTipGeneratorForBkZhanBi custotooltip = new CustomToolTipGeneratorForBkZhanBi();
			custotooltip.setDisplayNode(curdisplayednode);
			render.setSeriesToolTipGenerator(0,custotooltip);
		} else if(node.getType() == 6) { //gegu
			CustomToolTipGeneratorForGgZhanBi custotooltip = new CustomToolTipGeneratorForGgZhanBi();
			custotooltip.setDisplayNode(curdisplayednode);
			render.setSeriesToolTipGenerator(0,custotooltip);
		}
        
		super.plot.setDataset(barchartdataset);
		
		super.setBarFenXiSingle();
		
		setPanelTitle ("占比",displayedenddate1);
	}

}

class CustomRendererForGgBkZhanBi extends BanKuaiFengXiBarRenderer 
{
	private static final long serialVersionUID = 1L;
   
    public CustomRendererForGgBkZhanBi() {
        super();
        
    }

    public Paint getItemPaint(final int row, final int column) 
    {
        if(column == shouldcolumn)
            return Color.blue;
        else   
            return Color.RED;
   }
    public Paint getItemLabelPaint(final int row, final int column)
    {
		String selected = super.chartdataset.getColumnKey(column).toString();
    	LocalDate selecteddate = CommonUtility.formateStringToDate(selected);
    	 
    	ChenJiaoZhanBiInGivenPeriod nodefx = null;
    	if(node.getType() == 4)
    		nodefx = ((BanKuai)node).getNodeFengXiResultForSpecificDate (selecteddate);
    	else if(node.getType() == 6)
    		nodefx = ((Stock)node).getNodeFengXiResultForSpecificDate (selecteddate);
    		
		Integer maxweek;
		if(nodefx != null)
			 maxweek = nodefx.getGgbkzhanbimaxweek();
		else 
			maxweek = 0;
		
		if(maxweek >=4)
			return Color.red;
		else 
			return Color.black;
    }
//    public Paint getItemLabelPaint(final int row, final int column)
//    {
//    	if(column == 3)
//    		return Color.RED;
//    	else
//    		return Color.black; 
//    }
    
}

class CustomToolTipGeneratorForBkZhanBi implements CategoryToolTipGenerator 
{
    private BkChanYeLianTreeNode node;

	public String generateToolTip(CategoryDataset dataset, int row, int column)  
    {
		String selected = dataset.getColumnKey(column).toString();
    	LocalDate selecteddate = CommonUtility.formateStringToDate(selected);
    	 
    	ChenJiaoZhanBiInGivenPeriod nodefx = null;
		if(node.getType() == 4 )
			nodefx = ((BanKuai)node).getNodeFengXiResultForSpecificDate (selecteddate);
		else if(node.getType() == 6 ) 
			nodefx = ((Stock)node).getNodeFengXiResultForSpecificDate (selecteddate);
		
		if(nodefx == null)
			return "";
		
		String tooltip = selected.toString();
		if(node.getType() == 4 ) { //板块
			Double curzhanbidata = (Double)dataset.getValue(row, column);  //占比
			Double zhanbigrowthrate = nodefx.getGgbkzhanbigrowthrate();
			Double cjezhanbi = nodefx.getGgbkcjegrowthzhanbi();
			Integer maxweek = nodefx.getGgbkzhanbimaxweek();
			
			DecimalFormat decimalformate = new DecimalFormat("%#0.000");
			try {
				tooltip = tooltip +  "占比" + decimalformate.format(curzhanbidata) ;
			} catch (java.lang.IllegalArgumentException e ) {
				tooltip = tooltip  +  "占比NULL";
			}
			try {
				tooltip = tooltip +   "占比变化("	+ decimalformate.format(zhanbigrowthrate) +  ")";
			} catch (java.lang.IllegalArgumentException e ) {
				tooltip = tooltip  +  "占比变化(NULL)";
			}
			try {
				tooltip = tooltip +  "MaxWeek=" + maxweek.toString() ;
			} catch (java.lang.IllegalArgumentException e ) {
				tooltip = tooltip + "MaxWeek=NULL";
			}
			
			return tooltip;
		}
    	
		return "";
    }
    
    public void setDisplayNode (BkChanYeLianTreeNode curdisplayednode) 
    {
    	this.node = curdisplayednode;
    }
}


