package com.exchangeinfomanager.bankuaifengxi;

import java.awt.Color;
import java.awt.Font;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;

import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;

public class BanKuaiFengXiGanttChartPnl extends JPanel {

	/**
	 * Create the panel.
	 */
	public BanKuaiFengXiGanttChartPnl()
	{
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
		standardChartTheme.setExtraLargeFont(new Font("宋体",Font.BOLD,10) );
		standardChartTheme.setRegularFont(new Font("宋体",Font.BOLD,10) );
		standardChartTheme.setLargeFont(new Font("宋体",Font.BOLD,0));
		ChartFactory.setChartTheme(standardChartTheme);

		createChartPanel();
//		createEvent ();
		
//		sysconfig = SystemConfigration.getInstance();
//		this.shoulddisplayedmonthnum = sysconfig.banKuaiFengXiMonthRange() -3;
//		bkdbopt = new BanKuaiDbOperation ();

	}
	
	private  ChartPanel chartPanel;
	private  CategoryPlot plot;
    private  CategoryItemRenderer renderer;
	
	private void createChartPanel() 
    {
		 final IntervalCategoryDataset dataset = createSampleDataset();

	        // create the chart...
	        final JFreeChart chart = ChartFactory.createGanttChart(
	            null,  // chart title
	            null,              // domain axis label
	            null,              // range axis label
	            dataset,             // data
	            false,                // include legend
	            true,                // tooltips
	            false                // urls
	        );
	        
	        plot = (CategoryPlot) chart.getPlot();

	        renderer = plot.getRenderer();
	        renderer.setSeriesPaint(0, Color.blue);

	        // add the chart to a panel...
	        chartPanel = new ChartPanel(chart);
	        this.add(chartPanel);
    }
	
	 private IntervalCategoryDataset createSampleDataset() 
	 {

	        final TaskSeries s1 = new TaskSeries("Scheduled");
	        
	        final Task t1 = new Task("开始关注", date(1, Calendar.APRIL, 2001), date(5, Calendar.APRIL, 2001)  );
	        t1.setPercentComplete(1.00);
	        s1.add(t1);
	        
	        final Task t2 = new Task("正式关注", date(9, Calendar.APRIL, 2001), date(9, Calendar.APRIL, 2001)     );
	        t2.setPercentComplete(1.00);
	        s1.add(t2);

	        // here is a task split into two subtasks...
	        final Task t3 = new Task(
	            "买入", 
	            date(10, Calendar.APRIL, 2001), date(5, Calendar.MAY, 2001)
	        );
	        final Task st31 = new Task(
	            "卖出", 
	            date(10, Calendar.APRIL, 2001), date(25, Calendar.APRIL, 2001)
	        );
	        st31.setPercentComplete(1.0);
	        final Task st32 = new Task(
	            "分析记录", 
	            date(1, Calendar.MAY, 2001), date(5, Calendar.MAY, 2001)
	        );
	        st32.setPercentComplete(1.0);
	        t3.addSubtask(st31);
	        t3.addSubtask(st32);
	        s1.add(t3);

	        // and another...
	        final Task t4 = new Task(
	            "卖出", 
	            date(6, Calendar.MAY, 2001), date(30, Calendar.MAY, 2001)
	        );
	        final Task st41 = new Task(
	             "加入新闻", 
	             date(6, Calendar.MAY, 2001), date(10, Calendar.MAY, 2001)
	        );
	        st41.setPercentComplete(1.0);
	        final Task st42 = new Task(
	            "Design 2", 
	            date(15, Calendar.MAY, 2001), date(20, Calendar.MAY, 2001)
	        );
	        st42.setPercentComplete(1.0);
	        final Task st43 = new Task(
	            "Design 3", 
	            date(23, Calendar.MAY, 2001), date(30, Calendar.MAY, 2001)
	        );
	        st43.setPercentComplete(0.50);
	        t4.addSubtask(st41);
	        t4.addSubtask(st42);
	        t4.addSubtask(st43);
	        s1.add(t4);

	        final Task t5 = new Task(
	            "标记热点/龙头", date(2, Calendar.JUNE, 2001), date(2, Calendar.JUNE, 2001)
	        ); 
	        s1.add(t5);
	                        
	        final Task t6 = new Task(
	            "Alpha Implementation", date(3, Calendar.JUNE, 2001), date(31, Calendar.JULY, 2001)
	        );
	        t6.setPercentComplete(0.60);
	        
	        s1.add(t6);
	        
	        final Task t7 = new Task(
	            "Design Review", date(1, Calendar.AUGUST, 2001), date(8, Calendar.AUGUST, 2001)
	        );
	        t7.setPercentComplete(0.0);
	        s1.add(t7);
	        
	        final Task t8 = new Task(
	            "Revised Design Signoff", 
	            date(10, Calendar.AUGUST, 2001), date(10, Calendar.AUGUST, 2001)
	        );
	        t8.setPercentComplete(0.0);
	        s1.add(t8);
	        
	        final Task t9 = new Task(
	            "Beta Implementation", 
	            date(12, Calendar.AUGUST, 2001), date(12, Calendar.SEPTEMBER, 2001)
	        );
	        t9.setPercentComplete(0.0);
	        s1.add(t9);
	        
	        final Task t10 = new Task(
	            "Testing", date(13, Calendar.SEPTEMBER, 2001), date(31, Calendar.OCTOBER, 2001)
	        );
	        t10.setPercentComplete(0.0);
	        s1.add(t10);
	        
	        final Task t11 = new Task(
	            "Final Implementation", 
	            date(1, Calendar.NOVEMBER, 2001), date(15, Calendar.NOVEMBER, 2001)
	        );
	        t11.setPercentComplete(0.0);
	        s1.add(t11);
	        
	        final Task t12 = new Task(
	            "Signoff", date(28, Calendar.NOVEMBER, 2001), date(30, Calendar.NOVEMBER, 2001)
	        );
	        t12.setPercentComplete(0.0);
	        s1.add(t12);

	        final TaskSeriesCollection collection = new TaskSeriesCollection();
	        collection.add(s1);

	      
	        return collection;
	    }
	 
	 private static Date date(final int day, final int month, final int year) {

	        final Calendar calendar = Calendar.getInstance();
	        calendar.set(year, month, day);
	        final Date result = calendar.getTime();
	        return result;

	    }
	 

}
