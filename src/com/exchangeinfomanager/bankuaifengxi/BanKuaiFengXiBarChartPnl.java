package com.exchangeinfomanager.bankuaifengxi;

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

import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
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
//	private int currentDisplayedBkIndex ;
	private ArrayList<String> displaybkcodelist;
	private DefaultCategoryDataset barchartdataset ;
	private BanKuaiDbOperation bkdbopt;
	private HashMap<String, String> displaybkmap;
	private JFreeChart barchart;
	private Date datedisplayed ;
	private String curdisplayedbankcode;
	
	
	/*
	 * 板块大盘占比
	 */
	public void setBanKuaiWithDaPanNeededDisplay (HashMap<String,String> displaybkmap2,String firstshowbankuainame, Date daterange,int monthrange)
	{
		displaybkmap = displaybkmap2;
		displaybkcodelist = new ArrayList<String>(displaybkmap.keySet());
		ArrayList<String> displaybknamelist = new ArrayList<String>(displaybkmap.values());
		int currentDisplayedBkIndex = displaybknamelist.indexOf(firstshowbankuainame.trim());
		curdisplayedbankcode = displaybkcodelist.get(currentDisplayedBkIndex);

		datedisplayed = daterange;
		Date startdate = CommonUtility.getDateOfSpecificMonthAgo(daterange,monthrange);
		Date enddate = CommonUtility.getLastDayOfWeek(daterange);

        barchartdataset = new DefaultCategoryDataset();
    	CachedRowSetImpl rs = bkdbopt.getBanKuaiZhanBi (curdisplayedbankcode,startdate,enddate );

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
         
//      createControlPanel();
	}
	/*
	 * 板块大盘占比
	 */
	public void setBanKuaiWithDaPanNeededDisplay (BkChanYeLianTreeNode node)
	{
		barchartdataset = new DefaultCategoryDataset();
		
		ArrayList<ChenJiaoZhanBiInGivenPeriod> cjezb = node.getChenJiaoErZhanBiInGivenPeriod ();
		if(cjezb == null )
			return ;
		for(ChenJiaoZhanBiInGivenPeriod tmpcjezb : cjezb) {
			Date lastdayofweek = tmpcjezb.getDayofEndofWeek();
			Double zhanbi = tmpcjezb.getCjlZhanBi();
			barchartdataset.setValue(zhanbi,"板块占比",lastdayofweek);
		}
	
		plot.setDataset(barchartdataset);
	}

	/*
	 * 设置个股在某板块的占比半年信息
	 */
	public void setStockWithBanKuaiNeededDisplay (String tdxbk, String stockcode,Date daterange)
	{
		datedisplayed = daterange;
		Date startdate = CommonUtility.getDateOfSpecificMonthAgo(daterange,6);
		Date enddate = CommonUtility.getLastDayOfWeek(daterange);

        createDatasetOfStockWithBanKuai(tdxbk,stockcode,startdate,enddate);
//        createChartPanel(currentDisplayedBkIndex);
//        createControlPanel();
	}
	private void createDatasetOfStockWithBanKuai(String tdxbk, String stockcode, Date startdate, Date enddate) {
		barchartdataset = new DefaultCategoryDataset();
    	CachedRowSetImpl rs = bkdbopt.getGeGuZhanBi (tdxbk,stockcode,startdate,enddate );
    	int row =0;
    	try {
    		while (rs.next()) {
    			String weeknumber =rs.getString("CALWEEK");
    			String weeklastday = rs.getString("EndOfWeekDate");
    			Double zhanbi = rs.getDouble("占比");
    			barchartdataset.setValue(zhanbi,"股票占比",weeklastday);
    			row ++;
    		}
    		rs.close();
    		rs = null;
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    	plot.setDataset(barchartdataset);
    	
//    	if(barchart !=null) {
//	        barchart.setTitle("'"+ stockcode + bkname +"'板块成交量占比");
//    	}
		
	}
	public String getCurDisplayedBanKuaiCode ()
	{
		return this.curdisplayedbankcode;
	}
	public void resetDate ()
	{
		barchartdataset = new DefaultCategoryDataset();
		plot.setDataset(barchartdataset);
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

//    public ChartPanel getChartPanel() {
//        return chartPanel;
//    }
//
//    public JPanel getControlPanel() {
//        return controlPanel;
//    }
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