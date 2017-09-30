package com.exchangeinfomanager.gui.subgui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.File;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.PieToolTipGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;

import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.google.common.io.Files;
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
	Comparable lasthightlightKey = null; //用于客户设置突出的section
	private DefaultPieDataset piechartdataset;

	
	
	public void setBanKuaiNeededDisplay (String tdxbkname,String tdxbkcode, Date datedisplayed2, int weightgate)
	{
		datedisplayed = datedisplayed2;
		Date startdate = CommonUtility.getFirstDayOfWeek(datedisplayed);
		Date enddate = CommonUtility.getLastDayOfWeek(datedisplayed);

		if (lasthightlightKey != null) {
			pieplot.setExplodePercent(lasthightlightKey, 0);
        }
        createDataset(tdxbkname,tdxbkcode,startdate,enddate,weightgate);
//        createControlPanel();

		
	}
	public void resetDate ()
	{
		piechartdataset = new DefaultPieDataset();
		pieplot.setDataset(piechartdataset);
//		if(piechart !=null) {
//			piechart.setTitle("板块成交量占比");
//    	}
	}
	

    private void createDataset(String tdxbkname, String tdxbkcode,Date startdate, Date enddate, int weightgate) 
    {
    	HashMap<String, Stock> tmpallbkge = bkdbopt.getTDXBanKuaiGeGuOfHyGnFg (tdxbkname,tdxbkcode,startdate,enddate,true );
    	
    	piechartdataset = new DefaultPieDataset();
    	for(String ggcode: tmpallbkge.keySet()) {
    		Stock tmpstockinfo = tmpallbkge.get(ggcode);
    		String stockname = tmpstockinfo.getMyOwnName();
    		HashMap<String, Double> stockchengjiaoe = tmpstockinfo.getSysBanKuaiChenJiaoE();
//    		stockchengjiaoe = stockchengjiaoe.entrySet().stream()
//	                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
//	                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
//	                        (oldValue, newValue) -> oldValue, LinkedHashMap::new))
//	                ;
    		double cje = stockchengjiaoe.get(tdxbkcode);
    		
    		HashMap<String, Integer> geguweightmap = tmpstockinfo.getGeGuSuoShuBanKuaiWeight ();
    		int geguweight = geguweightmap.get(tdxbkcode);
    		if(geguweight > weightgate )
    			if(stockname != null)
    				piechartdataset.setValue(ggcode+stockname,cje);
    			else 
    				piechartdataset.setValue(ggcode,cje);
    	}
    	pieplot.setDataset(piechartdataset);
    }
    
    @SuppressWarnings("deprecation")
	private void createChartPanel() 
    {
//		http://www.jfree.org/jfreechart/api/javadoc/src-html/org/jfree/chart/demo/PieChartDemo1.html
//    	https://www.youtube.com/watch?v=YV80Titt9Q4
    	
//    	BarRenderer renderer = new BarRenderer ();
//    	renderer.setToolTipGenerator(new CustomToolTipGenerator() );
//        renderer.setBaseItemLabelsVisible(true);
//        DecimalFormat decimalformate = new DecimalFormat("%#0.000");
//        renderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}",decimalformate));
//        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12,TextAnchor.HALF_ASCENT_CENTER));
//        renderer.setItemLabelsVisible(true);
        
    	pieplot = new PiePlot();
//    	pieplot.setBackgroundPaint(null);
    	pieplot.setInteriorGap(0.04);
    	pieplot.setOutlineVisible(false);
    	pieplot.setToolTipGenerator(new PieCustomToolTipGenerator() );
//        LegendTitle legend = new LegendTitle(pieplot); 
//        legend.setPosition(RectangleEdge.TOP); 
        	 
//        pieplot.setLabelLinkPaint(Color.WHITE);
//        pieplot.setLabelLinkStroke(new BasicStroke(2.0f));
//        pieplot.setLabelOutlineStroke(null);
//        pieplot.setLabelPaint(Color.WHITE);
//        pieplot.setLabelBackgroundPaint(null);
//        pieplot.setLabelGenerator(null);
    	PieSectionLabelGenerator labelGenerator = new StandardPieSectionLabelGenerator("{0}{1}({2})"); //If you need the %, use in label format {0} = {2} instead of {0} = {1} and will be displayed California = 10%.
    	pieplot.setLabelGenerator(labelGenerator);
        
    	pieplot.setDataset(piechartdataset);
    	
    	piechart = new JFreeChart(pieplot);
    	
//    	TextTitle source = new TextTitle("Source: http://www.bbc.co.uk/news/business-15489523", 
//    			                new Font("Courier New", Font.PLAIN, 12));
//    	source.setPaint(Color.WHITE);
//    	source.setPosition(RectangleEdge.BOTTOM);
//    	source.setHorizontalAlignment(HorizontalAlignment.RIGHT);
//    	piechart.addSubtitle(source);
        
        piechart.removeLegend();

        piechartPanel = new ChartPanel(piechart);
        piechartPanel.setMouseWheelEnabled(true);
//        panel.setPreferredSize(new Dimension(600, 300));
        this.add(piechartPanel);
        
    }
   
    private void createEvent ()
    {
    	piechartPanel.addChartMouseListener(new ChartMouseListener() {
//    		private Comparable lastKey;

    	    public void chartMouseClicked(ChartMouseEvent e) {
    	        System.out.println("chart mouse click " + e.getEntity());
    	        try {
    	        	
    	        	pieplot.setLabelFont(new Font("Arial Unicode MS", 0, 15)); //让图片上的label字大一些
//    	        	pieplot.setSimpleLabels(true);
//    	        	File chartfile = new File("D:\\chart.png");
    	        	File tmpreportfolder = Files.createTempDir(); 
    	    		File chartfile = new File(tmpreportfolder + "chart.png");
    	    		
    	        	if(chartfile.exists())
    	        		chartfile.delete();
    	        	ChartUtilities.saveChartAsPNG(chartfile, piechart, 1200, 1200);
    	        	
    	        	ShowLargePieChartPnl slc = new ShowLargePieChartPnl (chartfile.getAbsolutePath());
    	        	int exchangeresult = JOptionPane.showConfirmDialog(null, slc, "板块股票占比", JOptionPane.OK_CANCEL_OPTION);
    				System.out.print(exchangeresult);
    				if(exchangeresult == JOptionPane.CANCEL_OPTION)
    					return;
    	        	
    	        	
    	        	pieplot.setLabelFont(new Font("Arial Unicode MS", 0, 10));
    	        	
    	        } catch (Exception ex) {
    	        	ex.printStackTrace();
    	        }
    	    }


			@Override
			public void chartMouseMoved(ChartMouseEvent arg0) {
				ChartEntity entity = arg0.getEntity();
	            if (entity instanceof PieSectionEntity) {
	                PieSectionEntity section = (PieSectionEntity) entity;
	                PiePlot plot = (PiePlot) piechart.getPlot();
	                if (lasthightlightKey != null) {
	        			pieplot.setExplodePercent(lasthightlightKey, 0);
	                }
	                Comparable key = section.getSectionKey();
	                plot.setExplodePercent(key, 0.05);
	                lasthightlightKey = key;
	            }
				
			}

    	});
    }
    
    public void hightlightSpecificSector (String tdxnameandcode) 
    {
		
		if (lasthightlightKey != null) {
			pieplot.setExplodePercent(lasthightlightKey, 0);
        }

    	pieplot.setExplodePercent(tdxnameandcode, 0.1);
    	
    	lasthightlightKey = tdxnameandcode;
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


class PieCustomToolTipGenerator implements  PieToolTipGenerator 
{

	@Override
	public String generateToolTip(PieDataset piedataset, Comparable key) 
	{
		Number data = piedataset.getValue(key);  
		return key.toString() + data;
	}
   
}