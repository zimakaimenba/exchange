package com.exchangeinfomanager.bankuaifengxi;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.PlotEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.sun.rowset.CachedRowSetImpl;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.geom.Rectangle2D;
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
		
//		bkdbopt = new BanKuaiDbOperation ();
		
		createChartPanel();
		createEvent ();
	}
	
//	private final JPanel contentPanel = new JPanel();
	private CategoryPlot plot;
	private ChartPanel chartPanel;
//	private JPanel controlPanel;
//	private int currentDisplayedBkIndex ;
//	private ArrayList<String> displaybkcodelist;
	private DefaultCategoryDataset barchartdataset ;
//	private BanKuaiDbOperation bkdbopt;
//	private HashMap<String, String> displaybkmap;
	private JFreeChart barchart;
//	private Date datedisplayed ;
	private String dateselected;
	private BkChanYeLianTreeNode curdisplayednode;
	

	/*
	 * 板块大盘占比
	 */
	public void setBanKuaiWithDaPanNeededDisplay (BkChanYeLianTreeNode node)
	{
		this.curdisplayednode = node;
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
	public BkChanYeLianTreeNode getCurDisplayedNode ()
	{
		return this.curdisplayednode;
	}

	public void resetDate ()
	{
		barchartdataset = new DefaultCategoryDataset();
		plot.setDataset(barchartdataset);
		((CustomRenderer)plot.getRenderer()).setBarColumnShouldChangeColor(-1);
	}
	
	public String getCurSelectedBarDate ()
	{
		return dateselected;
	}
//	public void setCurSelectedBarDate (String sltdate)
//	{
//		
//	}
	

    @SuppressWarnings("deprecation")
	private void createChartPanel() 
    {
//    	https://www.youtube.com/watch?v=YV80Titt9Q4
//    	BarRenderer renderer = new BarRenderer ();
    	CustomRenderer renderer = new CustomRenderer ();
        DecimalFormat decimalformate = new DecimalFormat("%#0.000");
        renderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}",decimalformate));
        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12,TextAnchor.HALF_ASCENT_CENTER));
        renderer.setItemLabelsVisible(true);
        renderer.setBaseItemLabelsVisible(true);
        renderer.setSeriesToolTipGenerator(0,new CustomToolTipGenerator());
        renderer.setToolTipGenerator(new CustomToolTipGenerator());
//        renderer.setSeriesPaint(0, Color.blue);
//        renderer.setDefaultBarPainter(new StandardBarPainter());
        
        plot = new CategoryPlot(); 
        LegendTitle legend = new LegendTitle(plot); 
        legend.setPosition(RectangleEdge.TOP); 
        plot.setDataset(barchartdataset); 
        plot.setRenderer(renderer); 
        plot.setDomainAxis(new CategoryAxis("")); 
        plot.setRangeAxis(new NumberAxis("占比"));
        ((BarRenderer) plot.getRenderer()).setBarPainter(new StandardBarPainter());
        CategoryAxis axis = plot.getDomainAxis();
        axis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_45);

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

    	    public void chartMouseClicked(ChartMouseEvent cme) {
    	    	try {
    	    		CategoryItemEntity xyitem=(CategoryItemEntity) cme.getEntity(); // get clicked entity
//        	        System.out.println(xyitem);
        	        CategoryDataset dataset = xyitem.getDataset(); // get data set
        	        int cindex = dataset.getColumnIndex(xyitem.getColumnKey()) ;
        	        ((CustomRenderer)plot.getRenderer()).setBarColumnShouldChangeColor(cindex);
        	        
        	         Comparable selecteddate = xyitem.getColumnKey();
        	         dateselected = selecteddate.toString();
    	    	} catch ( java.lang.ClassCastException e ) {
    	    		PlotEntity xyitem1 = (PlotEntity) cme.getEntity();
    	    		xyitem1.getPlot();
    	    	}
    	            	        
    	    }


			@Override
			public void chartMouseMoved(ChartMouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

    	});
    }

    public ChartPanel getChartPanel() {
        return chartPanel;
    }

}

class CustomRenderer extends BarRenderer {

    private Paint[] colors;
    int shouldcolumn = -1;
    public CustomRenderer() {
        super();
    }

    public Paint getItemPaint(final int row, final int column) 
    {
        if(column == shouldcolumn)
            return Color.blue;
        else  
            return Color.RED;
            
   }
    public void setBarColumnShouldChangeColor (int column)
    {
    	this.shouldcolumn = column;
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
    	} else {
    		Comparable test = dataset.getColumnKey(column);
    		return test.toString();
    	}
    	
    	
    }
}


class XYSelectionRenderer extends XYLineAndShapeRenderer {
    private Shape selectedShape = new Rectangle2D.Double(-8, -8, 16, 16);
    private int selectedSeries = -1;
    private int selectedItem = -1;
    private boolean selectionActive;

    public boolean isSelectionActive(){
        return (selectedSeries > -1 && selectedItem > -1);
    }
    
    public Shape getItemShape(int series, int item){
        if(series == selectedSeries && item == selectedItem){
            return selectedShape;
        }
        return super.getItemShape(series, item);
    }

    public void setSelectedSeries(int series){
        this.selectedSeries = series;
    }

    public void setSelectedItem(int item){
        this.selectedItem = item;
    }
}

