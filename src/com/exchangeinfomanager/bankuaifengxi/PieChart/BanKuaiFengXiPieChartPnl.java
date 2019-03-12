package com.exchangeinfomanager.bankuaifengxi.PieChart;

import java.awt.Font;
import java.io.File;
import java.time.LocalDate;

import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.PieToolTipGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;


import com.exchangeinfomanager.bankuaifengxi.BarChartPanelDataChangedListener;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.google.common.io.Files;

public abstract class BanKuaiFengXiPieChartPnl extends JPanel implements BarChartPanelDataChangedListener
{

	public BanKuaiFengXiPieChartPnl ()
	{
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
		standardChartTheme.setExtraLargeFont(new Font("隶书",Font.BOLD,20) );
		standardChartTheme.setRegularFont(new Font("隶书",Font.BOLD,20) );
		standardChartTheme.setLargeFont(new Font("隶书",Font.BOLD,20));
		ChartFactory.setChartTheme(standardChartTheme);
		
		createChartPanel();
		createEvent ();
	}
	
	private static Logger logger = Logger.getLogger(BanKuaiFengXiPieChartCjePnl.class);
	protected BkChanYeLianTreeNode curdisplaynode;
//	private Date displayedenddate;
	protected LocalDate displayedweeknumber;
//	private JPanel controlPanel;
	protected ChartPanel piechartPanel;
//	private BanKuaiDbOperation bkdbopt;
	protected JFreeChart piechart;
//	private Date datedisplayed ;
	protected PiePlot pieplot;
	protected Comparable lasthightlightKey = null; //用于客户设置突出的section
	protected DefaultPieDataset piechartdataset;
	protected TDXNodes curdisplaybk;
//	private SystemConfigration sysconfig;
	
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
        
    	piechartdataset = new DefaultPieDataset();
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
//	    		private Comparable lastKey;

	    	    public void chartMouseClicked(ChartMouseEvent e) {
	    	        java.awt.event.MouseEvent me = e.getTrigger();
	    	        if (me.getClickCount() == 2) {
	    	        	logger.debug("chart mouse click " + e.getEntity());
	    	        	 try {
	    	    	        	
	    	    	        	pieplot.setLabelFont(new Font("Arial Unicode MS", 0, 15)); //让图片上的label字大一些
//	    	    	        	pieplot.setSimpleLabels(true);
//	    	    	        	File chartfile = new File("D:\\chart.png");
	    	    	        	File tmpreportfolder = Files.createTempDir(); 
	    	    	    		File chartfile = new File(tmpreportfolder + "chart.png");
	    	    	    		
	    	    	        	if(chartfile.exists())
	    	    	        		chartfile.delete();
	    	    	        	ChartUtilities.saveChartAsPNG(chartfile, piechart, 1200, 1200);
	    	    	        	
	    	    	        	ShowLargePieChartPnl slc = new ShowLargePieChartPnl (chartfile.getAbsolutePath());
	    	    	        	int exchangeresult = JOptionPane.showConfirmDialog(null, slc, "板块股票占比", JOptionPane.OK_CANCEL_OPTION);

	    	    				if(exchangeresult == JOptionPane.CANCEL_OPTION)
	    	    					return;

	    	    				pieplot.setLabelFont(new Font("Arial Unicode MS", 0, 10));
	    	    	        	
	    	    	        } catch (Exception ex) {
	    	    	        	ex.printStackTrace();
	    	    	        }
	    	        }
	    	        
	    	        if (me.getClickCount() == 1) {
	    	        	ChartEntity entity = e.getEntity();
	    	            if (entity instanceof PieSectionEntity) {
	    	                PieSectionEntity section = (PieSectionEntity) entity;
//	    	                PiePlot plot = (PiePlot) piechart.getPlot();
//	    	                if (lasthightlightKey != null) {
//	    	        			pieplot.setExplodePercent(lasthightlightKey, 0);
//	    	                }
	    	                Comparable key = section.getSectionKey();
//	    	                plot.setExplodePercent(key, 0.05);
	    	                lasthightlightKey = key;
//	    	                logger.dubug(lasthightlightKey.toString());
	    	            }
	    	        	
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
		
		protected void setPanelTitle (String type)
		{
			String nodecode = curdisplaybk.getMyOwnCode();
			String nodename = curdisplaybk.getMyOwnName();
			
//			Date endday = CommonUtility.getLastDayOfWeek(displayedenddate );
//	    	Date startday = CommonUtility.getFirstDayOfWeek( CommonUtility.getDateOfSpecificMonthAgo(displayedenddate ,sysconfig.banKuaiFengXiMonthRange() ) );
	    	((TitledBorder)this.getBorder()).setTitle(nodecode+ nodename + type + "周" + displayedweeknumber );
//																+ "从" + CommonUtility.formatDateYYYY_MM_DD(startday) 
//																+ "到" + CommonUtility.formatDateYYYY_MM_DD(endday) );
	    	this.repaint();
	    	this.setToolTipText(nodecode+ nodename );
		}
		
		public void resetDate ()
		{
//			piechartdataset = null;
//			piechartdataset = new DefaultPieDataset();
//			pieplot.setDataset(piechartdataset);
			if(piechartdataset != null)
				piechartdataset.clear();
			
			this.piechartPanel.removeAll();
		}

		public Comparable getCurHightLightStock ()
		{
			return this.lasthightlightKey; 
		}
		public JPanel getPiePanel ()
		{
			return this.piechartPanel;
		}

   
}



class PieCustomToolTipGenerator implements  PieToolTipGenerator 
{

	@Override
	public String generateToolTip(PieDataset piedataset, Comparable key) 
	{
		Number data = piedataset.getValue(key);  
		return key.toString() +"<" + data + ">";
	}
   
}