package com.exchangeinfomanager.gui.subgui;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;

import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.sun.rowset.CachedRowSetImpl;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;

public class BanKuaiFengXiBarChartPnl extends JPanel {

	/**
	 * Create the panel.
	 */
	public BanKuaiFengXiBarChartPnl() 
	{
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
		standardChartTheme.setExtraLargeFont(new Font("隶书",Font.BOLD,20) );
		standardChartTheme.setRegularFont(new Font("隶书",Font.BOLD,20) );
		standardChartTheme.setLargeFont(new Font("隶书",Font.BOLD,20));
		ChartFactory.setChartTheme(standardChartTheme);
		
		bkdbopt = new BanKuaiDbOperation ();
		
		createChartPanel();
		createEvent ();
	}
	
	private final JPanel contentPanel = new JPanel();
	private CategoryPlot plot;
	private ChartPanel chartPanel;
	private JPanel controlPanel;
	private int currentDisplayedBkIndex ;
	private ArrayList<String> displaybkcodelist;
	private DefaultCategoryDataset barchartdataset ;
	private BanKuaiDbOperation bkdbopt;
	private HashMap<String, String> displaybkmap;
	private JFreeChart barchart;
	private Date datedisplayed ;
	
	/*
	 * 板块大盘占比
	 */
	public void setBanKuaiNeededDisplay (HashMap<String,String> displaybkmap2,String firstshowbankuainame, Date daterange,int monthrange)
	{
		displaybkmap = displaybkmap2;
		displaybkcodelist = new ArrayList<String>(displaybkmap.keySet());
		ArrayList<String> displaybknamelist = new ArrayList<String>(displaybkmap.values());
		currentDisplayedBkIndex = displaybknamelist.indexOf(firstshowbankuainame.trim());

		datedisplayed = daterange;
		Date startdate = CommonUtility.getDateOfSpecificMonthAgo(daterange,monthrange);
		Date enddate = CommonUtility.getLastDayOfWeek(daterange);

        createDataset(currentDisplayedBkIndex,startdate,enddate);
//      createControlPanel();
	}

	public void resetDate ()
	{
		barchartdataset = new DefaultCategoryDataset();
		plot.setDataset(barchartdataset);
		if(barchart !=null) {
	        barchart.setTitle("板块成交量占比");
    	}
	}
    private void createDataset(int start, Date startdate, Date enddate) {
    	barchartdataset = new DefaultCategoryDataset();
    	CachedRowSetImpl rs = bkdbopt.getBanKuaiZhanBi (displaybkcodelist.get(start),startdate,enddate );

    	try {
    		while (rs.next()) {
    			String weeknumber =rs.getString("CALWEEK");
    			String weeklastday = rs.getString("EndOfWeekDate");
    			Double zhanbi = rs.getDouble("占比");
    			barchartdataset.setValue(zhanbi,"板块占比",weeklastday);
    		}
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	} finally {
    		try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		rs = null;
    	}
    	
    	plot.setDataset(barchartdataset);
    	
    	if(barchart !=null) {
    		String bkcode = displaybkcodelist.get(start);
	    	String bkname = displaybkmap.get(bkcode);
	        barchart.setTitle("'"+ bkcode + bkname +"'板块成交量占比");
    	}
    }
    
    @SuppressWarnings("deprecation")
	private void createChartPanel() 
    {
//    	https://www.youtube.com/watch?v=YV80Titt9Q4
    	BarRenderer renderer = new BarRenderer ();
        DecimalFormat decimalformate = new DecimalFormat("%#0.000");
        renderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}",decimalformate));
        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12,TextAnchor.HALF_ASCENT_CENTER));
        renderer.setItemLabelsVisible(true);
        renderer.setBaseItemLabelsVisible(true);
        renderer.setSeriesToolTipGenerator(0,new CustomToolTipGenerator());
        renderer.setToolTipGenerator(new CustomToolTipGenerator());
        
        plot = new CategoryPlot(); 
        LegendTitle legend = new LegendTitle(plot); 
        legend.setPosition(RectangleEdge.TOP); 
        plot.setDataset(barchartdataset); 
        plot.setRenderer(renderer); 
        plot.setDomainAxis(new CategoryAxis("周数")); 
        plot.setRangeAxis(new NumberAxis("占比"));

        barchart = new JFreeChart(plot);
        barchart.removeLegend();
//        if(displaybkcodelist != null) {
//	        String bkcode = displaybkcodelist.get(start);
//	    	String bkname = displaybkmap.get(bkcode);
//	    	barchart.setTitle("'"+ bkcode + bkname +"'板块成交量占比");
//        }
        

        chartPanel = new ChartPanel(barchart);
        this.add(chartPanel);
        
    }
    private void createEvent ()
    {
    	chartPanel.addChartMouseListener(new ChartMouseListener() {

    	    public void chartMouseClicked(ChartMouseEvent e) {
    	        System.out.println("chart mouse click " + e.getEntity());
    	    }


			@Override
			public void chartMouseMoved(ChartMouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

    	});
    }

//    private void createChartPanel2(int start) {
////        CategoryAxis xAxis = new CategoryAxis("Category");
////        NumberAxis yAxis = new NumberAxis("Value");
//    	String bkcode = suosubkcodelist.get(start);
//    	String bkname = suoshubkmap.get(bkcode);
//    	JFreeChart barchart = ChartFactory.createBarChart( "'"+ bkcode + bkname +"'板块成交量占比", "周数", "占比", barchartdataset,PlotOrientation.VERTICAL, true,true,false);
//        BarRenderer renderer = new BarRenderer();
//        plot = barchart.getCategoryPlot();
//        plot.setRangeGridlinePaint(Color.BLACK);
////        plot = new CategoryPlot(barchartdataset, xAxis, yAxis, renderer);
////        JFreeChart chart = new JFreeChart("BoxAndWhiskerDemo", plot);
//        chartPanel = new ChartPanel(barchart);
//    }

    private void createControlPanel() {
        controlPanel = new JPanel();
        controlPanel.add(new JButton(new AbstractAction("\u22b2Prev") {

            @Override
            public void actionPerformed(ActionEvent e) {
            	currentDisplayedBkIndex -= 1;
                if (currentDisplayedBkIndex < 0) {
                	currentDisplayedBkIndex = 0;
                    return;
                }
                
        		Date startdate = CommonUtility.getFirstDayOfWeek(datedisplayed);
        		Date enddate = CommonUtility.getLastDayOfWeek(datedisplayed);
                createDataset(currentDisplayedBkIndex,startdate,enddate);
                plot.setDataset(barchartdataset);
            }
        }));
        controlPanel.add(new JButton(new AbstractAction("Next\u22b3") {

            @Override
            public void actionPerformed(ActionEvent e) {
            	currentDisplayedBkIndex += 1;
                if (currentDisplayedBkIndex >= displaybkcodelist.size()) {
                	currentDisplayedBkIndex = displaybkcodelist.size();
                    return;
                }
                Date startdate = CommonUtility.getFirstDayOfWeek(datedisplayed);
        		Date enddate = CommonUtility.getLastDayOfWeek(datedisplayed);
                createDataset(currentDisplayedBkIndex,startdate,enddate);
                plot.setDataset(barchartdataset);
            }
        }));
    }

    public ChartPanel getChartPanel() {
        return chartPanel;
    }

    public JPanel getControlPanel() {
        return controlPanel;
    }
	
	

}


class CustomToolTipGenerator implements CategoryToolTipGenerator  {
    public String generateToolTip(CategoryDataset dataset, int row, int column)  
    {
    	Double datecur = (Double)dataset.getValue(row, column);
    	if(column >=1) {
    		Double datelast = (Double)dataset.getValue(row, column-1);
            Double ratio = (datecur - datelast)/datelast;

            DecimalFormat decimalformate = new DecimalFormat("%#0.000");
            
            Comparable test = dataset.getColumnKey(column);
//            List tst = dataset.getColumnKeys();
//        	return "(" + String.valueOf(datecur) + ")" + "(" + String.valueOf(datelast) + ")" + "(" + decimalformate.format(ratio) + ")";
        	return test + "占比变化(" + decimalformate.format(ratio) + ")";
    	} else
    		return "";
    	
    	
    }
}