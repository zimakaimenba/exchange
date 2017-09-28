package com.exchangeinfomanager.gui.subgui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;

import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.sun.rowset.CachedRowSetImpl;

public class BanKuaiFengXiPieChartPnl extends JPanel {

	/**
	 * Create the panel.
	 */
	public BanKuaiFengXiPieChartPnl() {

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
	private JPanel controlPanel;
	private ChartPanel piechartPanel;
	private BanKuaiDbOperation bkdbopt;
	private JFreeChart piechart;
	private Date datedisplayed ;
	private PiePlot pieplot;
	
	private DefaultPieDataset piechartdataset;

	
	
	public void setBanKuaiNeededDisplay (String tdxbkname,String tdxbkcode, Date datedisplayed2)
	{
		datedisplayed = datedisplayed2;
		Date startdate = CommonUtility.getFirstDayOfWeek(datedisplayed);
		Date enddate = CommonUtility.getLastDayOfWeek(datedisplayed);

        createDataset(tdxbkname,tdxbkcode,startdate,enddate);
//        createControlPanel();

		
	}
	public void resetDate ()
	{
		piechartdataset = new DefaultPieDataset();
		pieplot.setDataset(piechartdataset);
		if(piechart !=null) {
			piechart.setTitle("板块成交量占比");
    	}
	}
	

    private void createDataset(String tdxbkname, String tdxbkcode,Date startdate, Date enddate) 
    {
    	HashMap<String, Stock> tmpallbkge = bkdbopt.getTDXBanKuaiGeGuOfHyGnFg (tdxbkname,tdxbkcode,startdate,enddate,true );
    	
    	piechartdataset = new DefaultPieDataset();
    	for(String ggcode: tmpallbkge.keySet()) {
    		Stock tmpstockinfo = tmpallbkge.get(ggcode);
    		String stockname = tmpstockinfo.getMyOwnName();
    		HashMap<String, Double> stockchengjiaoe = tmpstockinfo.getSysBanKuaiChenJiaoE();
    		double cje = stockchengjiaoe.get(tdxbkcode);
    		piechartdataset.setValue(ggcode+stockname,cje);
    	}
    	pieplot.setDataset(piechartdataset);
    }
    
    @SuppressWarnings("deprecation")
	private void createChartPanel() 
    {
//    	https://www.youtube.com/watch?v=YV80Titt9Q4
//    	BarRenderer renderer = new BarRenderer ();
//    	renderer.setToolTipGenerator(new CustomToolTipGenerator() );
//        renderer.setBaseItemLabelsVisible(true);
//        DecimalFormat decimalformate = new DecimalFormat("%#0.000");
//        renderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}",decimalformate));
//        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12,TextAnchor.HALF_ASCENT_CENTER));
//        renderer.setItemLabelsVisible(true);
        
    	pieplot = new PiePlot(); 
//        LegendTitle legend = new LegendTitle(pieplot); 
//        legend.setPosition(RectangleEdge.TOP); 
        pieplot.setDataset(piechartdataset); 
//        pieplot.setLabelGenerator(null);
        
        piechart = new JFreeChart(pieplot);
        piechart.removeLegend();

        piechartPanel = new ChartPanel(piechart);
        this.add(piechartPanel);
        
    }
    private void createEvent ()
    {
    	piechartPanel.addChartMouseListener(new ChartMouseListener() {

    	    public void chartMouseClicked(ChartMouseEvent e) {
    	        System.out.println("chart mouse click " + e.getEntity());
    	        try {
    	        	
    	        } catch (Exception ex) {
    	        	ex.printStackTrace();
    	        }
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
//            	currentDisplayedBkIndex -= 1;
//                if (currentDisplayedBkIndex < 0) {
//                	currentDisplayedBkIndex = 0;
//                    return;
//                }
//                
//        		Date startdate = CommonUtility.getFirstDayOfWeek(datedisplayed);
//        		Date enddate = CommonUtility.getLastDayOfWeek(datedisplayed);
//                createDataset(currentDisplayedBkIndex,startdate,enddate);
//                plot.setDataset(barchartdataset);
            }
        }));
        controlPanel.add(new JButton(new AbstractAction("Next\u22b3") {

            @Override
            public void actionPerformed(ActionEvent e) {
//            	currentDisplayedBkIndex += 1;
//                if (currentDisplayedBkIndex >= suosubkcodelist.size()) {
//                	currentDisplayedBkIndex = suosubkcodelist.size();
//                    return;
//                }
//                Date startdate = CommonUtility.getFirstDayOfWeek(datedisplayed);
//        		Date enddate = CommonUtility.getLastDayOfWeek(datedisplayed);
//                createDataset(currentDisplayedBkIndex,startdate,enddate);
//                plot.setDataset(barchartdataset);
            }
        }));
    }

//    public ChartPanel getChartPanel() {
//        return chartPanel;
//    }
//
//    public JPanel getControlPanel() {
//        return controlPanel;
//    }
	
	

}