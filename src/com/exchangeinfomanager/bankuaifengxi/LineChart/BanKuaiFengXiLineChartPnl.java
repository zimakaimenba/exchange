package com.exchangeinfomanager.bankuaifengxi.LineChart;

import java.awt.Color;
import java.awt.Font;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.plot.CategoryMarker;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.data.Range;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.Layer;
import org.jfree.ui.LengthAdjustmentType;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;

import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;



public class BanKuaiFengXiLineChartPnl extends JPanel 
{
	private TDXNodes node;
	private LocalDate startdate;
	private LocalDate enddate;
	private String period;
	private List<Marker> categorymarkerlist;
	private ArrayList valuemarkerlist;
	public BanKuaiFengXiLineChartPnl ()
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		createChartPanel();
		
		this.categorymarkerlist = new ArrayList<> ();
		this.valuemarkerlist = new ArrayList<> ();
	}
	
	public void updatedDate (TDXNodes node, LocalDate startdate, LocalDate enddate,String period,String[] displayedinfokeywords)
	{
		this.setCurDisplayNode( node,startdate, enddate, period );
		
		this.preparingdisplayDataToGui (node,startdate,enddate,period, displayedinfokeywords);
	}
	public void resetData ()
	{
		if(dataset != null)	dataset.clear();
		for(Marker marker : this.categorymarkerlist) 
			((CategoryPlot)chart.getPlot()).removeDomainMarker(marker);
		this.categorymarkerlist.clear();
	}
	private void preparingdisplayDataToGui(TDXNodes node, LocalDate startdate, LocalDate requireend, String period ,String[] displayedinfokeywords)
	{
		chart.setNotify(false);
		resetData ();
		
		List<LocalDate> columnlocaldates = new ArrayList<> (); 
		Double highesthigh = 0.0; Double lowestlow = 0.0;
		NodeXPeriodData nodexdataday = node.getNodeXPeroidData(period);
		for(String keyword : displayedinfokeywords) {
				LocalDate tmpdate = startdate;
				do  {
					Double nodekwdata = (Double)nodexdataday.getNodeDataByKeyWord(keyword, tmpdate, "");
					if(nodekwdata != 100.0) {
						dataset.addValue(nodekwdata, keyword, tmpdate);
						
						if(nodekwdata > highesthigh) highesthigh = nodekwdata ;
						if(nodekwdata < lowestlow)   lowestlow = nodekwdata ;
					}
					
					DayOfWeek dayofweek = tmpdate.getDayOfWeek();
					if(dayofweek.equals(DayOfWeek.FRIDAY) ) 
						columnlocaldates.add(tmpdate);
					
					if(period.equals(NodeGivenPeriodDataItem.WEEK))
						tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
					else if(period.equals(NodeGivenPeriodDataItem.DAY))
						tmpdate = tmpdate.plus(1, ChronoUnit.DAYS) ;
					else if(period.equals(NodeGivenPeriodDataItem.MONTH))
						tmpdate = tmpdate.plus(1, ChronoUnit.MONTHS) ;
				} while (tmpdate.isBefore( requireend) || tmpdate.isEqual(requireend));
			}
		//decorate part
		Range curaxisrange = ((CategoryPlot)chart.getPlot()).getRangeAxis(0).getRange();
		double curaxisupper = curaxisrange.getUpperBound();
		double curaxislow = curaxisrange.getLowerBound();
		curaxisupper = highesthigh*1.12;
		curaxislow = lowestlow * 1.12;  
		try{	((CategoryPlot)chart.getPlot()).getRangeAxis(0).setRange(curaxislow, curaxisupper);
		} catch(java.lang.IllegalArgumentException e) {
			((CategoryPlot)chart.getPlot()).getRangeAxis(0).setRange(0, 1);
		}
		
		ValueMarker vmarker = new ValueMarker (0.0);
		vmarker.setLabelTextAnchor(TextAnchor.TOP_CENTER);
		vmarker.setPaint(Color.BLACK);
		((CategoryPlot)chart.getPlot()).addRangeMarker(0,vmarker, Layer.FOREGROUND);
		
		for(LocalDate tmpdate : columnlocaldates ) {
			CategoryMarker marker = new CategoryMarker(tmpdate);  // position is the value on the axis
			marker.setPaint(Color.gray);
			marker.setAlpha(0.5f);
			marker.setLabelAnchor(RectangleAnchor.TOP);
			marker.setLabelTextAnchor(TextAnchor.TOP_CENTER);
			marker.setLabelOffsetType(LengthAdjustmentType.CONTRACT);
//			marker.setLabel(String.valueOf(curyear)); // see JavaDoc for labels, colors, strokes
			marker.setDrawAsLine(true);
			((CategoryPlot)chart.getPlot()).addDomainMarker(marker,Layer.BACKGROUND);
			this.categorymarkerlist.add(marker);
		}
		
		chart.setNotify(true);
	}

	private void setCurDisplayNode(TDXNodes node, LocalDate startdate, LocalDate enddate, String period) 
	{
		this.node = node;
		this.startdate = startdate;
		this.enddate =enddate;
		this.period = period;
	}

	DefaultCategoryDataset dataset;
	 JFreeChart chart;
	 @SuppressWarnings("deprecation")
		private void createChartPanel() 
	    {
			StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
			standardChartTheme.setExtraLargeFont(new Font("Á¥Êé",Font.PLAIN,9) );
			standardChartTheme.setRegularFont(new Font("Á¥Êé",Font.PLAIN,9) );
			standardChartTheme.setLargeFont(new Font("Á¥Êé",Font.PLAIN,9));
			ChartFactory.setChartTheme(standardChartTheme);
			
			 // Create dataset
			dataset = new DefaultCategoryDataset();
		    // Create chart
		    chart = ChartFactory.createLineChart(
		        "", // Chart title
		        "", // X-Axis Label
		        "", // Y-Axis Label
		        dataset
		        );

		    ChartPanel linepanel = new ChartPanel(chart);
		    
		    
		    this.add(linepanel);
			
	    }
}
