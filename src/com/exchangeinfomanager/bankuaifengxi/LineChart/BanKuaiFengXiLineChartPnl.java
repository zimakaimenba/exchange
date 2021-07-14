package com.exchangeinfomanager.bankuaifengxi.LineChart;

import java.awt.Font;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.data.category.DefaultCategoryDataset;

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
	public BanKuaiFengXiLineChartPnl ()
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		createChartPanel();
	}
	
	public void updatedDate (TDXNodes node, LocalDate startdate, LocalDate enddate,String period,String[] displayedinfokeywords)
	{
		this.setCurDisplayNode( node,startdate, enddate, period );
		
		this.preparingdisplayDataToGui (node,startdate,enddate,period, displayedinfokeywords);
	}
	
	private void preparingdisplayDataToGui(TDXNodes node, LocalDate startdate, LocalDate requireend, String period ,String[] displayedinfokeywords)
	{
		if(dataset != null)	dataset.clear();
		chart.setNotify(false);
		
		LocalDate caldate = startdate;
		NodeXPeriodData nodexdataday = node.getNodeXPeroidData(period);
		 
			for(String keyword : displayedinfokeywords) {
				LocalDate tmpdate = startdate;
				do  {
					Double nodekwdata = (Double)nodexdataday.getNodeDataByKeyWord(keyword, caldate, "");
					dataset.addValue(nodekwdata, keyword, caldate);
					
					if(period.equals(NodeGivenPeriodDataItem.WEEK))
						tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
					else if(period.equals(NodeGivenPeriodDataItem.DAY))
						tmpdate = tmpdate.plus(1, ChronoUnit.DAYS) ;
					else if(period.equals(NodeGivenPeriodDataItem.MONTH))
						tmpdate = tmpdate.plus(1, ChronoUnit.MONTHS) ;
				} while (tmpdate.isBefore( requireend) || tmpdate.isEqual(requireend));
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
			standardChartTheme.setExtraLargeFont(new Font("¡• È",Font.BOLD,20) );
			standardChartTheme.setRegularFont(new Font("¡• È",Font.BOLD,20) );
			standardChartTheme.setLargeFont(new Font("¡• È",Font.BOLD,20));
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
