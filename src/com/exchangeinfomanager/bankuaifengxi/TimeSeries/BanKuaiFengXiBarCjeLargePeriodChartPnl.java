package com.exchangeinfomanager.bankuaifengxi.TimeSeries;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.RectangularShape;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.Locale;

import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;

import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic;
import com.exchangeinfomanager.commonlib.CommonUtility;

public class BanKuaiFengXiBarCjeLargePeriodChartPnl extends BanKuaiFengXiBarLargePeriodChartPnl
{
	public BanKuaiFengXiBarCjeLargePeriodChartPnl (BkChanYeLianTreeNode node, LocalDate displayedenddate1,String period)
	{
		super (node,displayedenddate1,period);
		((CustomXYBarRenderer)super.mainPlot.getRenderer()).setBarPainter(new CustomXYBarCjePainter());
		super.chart.setNotify(false);
		NumberFormat format = NumberFormat.getNumberInstance();
//        format.setMaximumFractionDigits(30000); // etc.
        DateFormat dateformate = DateFormat.getDateInstance();
        ((CustomXYBarRenderer)super.mainPlot.getRenderer()).setBaseItemLabelGenerator(new StandardXYItemLabelGenerator(StandardXYItemLabelGenerator.DEFAULT_ITEM_LABEL_FORMAT, dateformate, format) );
        ((CustomXYBarRenderer)super.mainPlot.getRenderer()).setBaseToolTipGenerator(new CustomXYPlotCjeToolTipGenerator());
        
		dataset = updateDataset(node,displayedenddate1,period); 
		mainPlot.setDataset(dataset);
		super.chart.setNotify(true);
	}
	@Override
    protected XYDataset updateDataset(BkChanYeLianTreeNode node, LocalDate displayedenddate1,String period) 
    {
		NodeXPeriodDataBasic nodexdata = node.getNodeXPeroidData(period);
    	LocalDate requirestart = nodexdata.getRecordsStartDate().with(DayOfWeek.SATURDAY);
		LocalDate requireend = nodexdata.getRecordsEndDate().with(DayOfWeek.SATURDAY);;
		TimeSeries rangecjezb = nodexdata.getRangeChengJiaoEr(requirestart, requireend);

		TimeSeriesCollection dataset = (TimeSeriesCollection) super.mainPlot.getDataset();
		dataset.setNotify(false);
        dataset.addSeries(rangecjezb);
        
//        ((CustomXYBarRenderer)super.mainPlot.getRenderer()).setDisplayNode(node);
        ((CustomXYBarRenderer)super.mainPlot.getRenderer()).setDisplayNodeXPeriodData (nodexdata);
        
        dataset.setNotify(true);
        return dataset;
    }


}

class CustomXYBarCjePainter extends CustomXYBarPainter 
{
  	@Override
    public void paintBarShadow(Graphics2D g2, XYBarRenderer renderer, int row, int column, RectangularShape bar, RectangleEdge base, boolean pegShadow) {
   		
   	}

   	public void paintBar(Graphics2D g2, XYBarRenderer renderer, int row, int column, RectangularShape bar, RectangleEdge base) 
    {
    	bar.setFrame(bar.getX(), bar.getY(), bar.getWidth() , bar.getHeight());
    	    	
//    	XYDataset dataset = renderer.getPlot().getDataset();
    	    	
    	if(highlightercolumn == column)
    		g2.setColor(Color.BLUE.darker());
    	else
    		g2.setColor(Color.ORANGE.darker());
    	
    	g2.fill(bar);
    	g2.draw(bar);
    }
   	
}

class CustomXYPlotCjeToolTipGenerator extends CustomXYPlotToolTipGenerator 
{
//	protected BkChanYeLianTreeNode node;
//	protected NodeXPeriodDataBasic nodexdata;
	
	@Override
    public String generateToolTip(XYDataset dataset, int series, int item) 
	{
		//系统一直以星期日为一周开始，显示要调整为当周五
       Date d = new Date((long) dataset.getXValue(series, item));
       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
       TemporalField fieldUS = WeekFields.of(Locale.US).dayOfWeek();
   	   LocalDate selecteddate = CommonUtility.formateStringToDate(sdf.format(d)).with(fieldUS , 6);
   	 
		String tooltip = selecteddate.toString() + " ";
		 
			Double curcje = dataset.getYValue(series, item);   //占比
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
			
			Integer maxweek = 0;
			try {
				 maxweek = nodexdata.getChenJiaoErMaxWeekOfSuperBanKuai(selecteddate,0);
			} catch (java.lang.NullPointerException e) {
				 maxweek = 0;
			}

			return selecteddate + " " + "成交额" + decimalformate.format(curcje) + danwei +  "成交额MaxWk=" + maxweek;
    }
	
//    public void setDisplayNode (BkChanYeLianTreeNode curdisplayednode) 
//    {
//    	this.node = curdisplayednode;
//    }
//    public void setDisplayNodeXPeriod(NodeXPeriodDataBasic nodexdata1) 
//    {
//		this.nodexdata = nodexdata1;
//	}
}
