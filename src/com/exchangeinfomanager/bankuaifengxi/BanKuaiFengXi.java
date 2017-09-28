package com.exchangeinfomanager.bankuaifengxi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.RectangleEdge;

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.bankuaichanyelian.BkChanYeLianTree;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.StockDbOperations;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ImageIcon;
import net.miginfocom.swing.MigLayout;
import javax.swing.JScrollPane;
import com.toedter.components.JLocaleChooser;
import com.toedter.calendar.JDateChooser;
import javax.swing.JCheckBox;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.beans.PropertyChangeEvent;
import javax.swing.JTable;
import java.awt.event.MouseAdapter;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import java.awt.GridLayout;
import javax.swing.border.MatteBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;

public class BanKuaiFengXi extends JDialog {

	/**
	 * Create the dialog.
	 */
	public BanKuaiFengXi(BkChanYeLianTree bkcyltree2,String parsedfilename2)
	{
		initializeGui ();
		this.bkcyltree = bkcyltree2; 
		if(parsedfilename2 != null)
			this.tfldparsedfile.setText(parsedfilename2);
		this.bkdbopt = new BanKuaiDbOperation ();
		this.stockdbopt = new StockDbOperations ();
		
		createEvents ();
//		initializeBanKuaiZhanBiByGrowthRate ();
		
	}
	
	private BkChanYeLianTree bkcyltree;
	private BanKuaiDbOperation bkdbopt;
	private StockDbOperations stockdbopt;
	
	private JFreeChart piechartwklast2;
	private JFreeChart piechartwklast;
	private JFreeChart piechartwkcur;
	private PiePlot pieplotwklast;
	private PiePlot pieplotwkcur;
	private PiePlot pieplotwklast2;
	private DefaultPieDataset piechartdatasetwkcur;
	private DefaultPieDataset piechartdatasetwklast;
	private DefaultPieDataset piechartdatasetwklast2;

	/*
	 * 所有板块占比增长率的排名
	 */
	private void initializeBanKuaiZhanBiByGrowthRate ()
	{
		Date bkfxdate = dateChooser.getDate();
		Date startdate = CommonUtility.getFirstDayOfWeek(bkfxdate);
		Date enddate = CommonUtility.getLastDayOfWeek(bkfxdate);
			
		HashMap<String, Double> bkzbmap = bkdbopt.getAllTdxBanKuaiOfChenJiaoErWeekZhanBiOrderByZhanBiGrowthRate (startdate,enddate);
		HashMap<String, Double> zszbmap = bkdbopt.getAllZhiShuBanKuaiOfChenJiaoErWeekZhanBiOrderByZhanBiGrowthRate(startdate,enddate);
		
		bkzbmap.putAll(zszbmap);
		
		if(bkzbmap != null) {
//			HashMap<String, BanKuai> curtdxbanklist = bkdbopt.getTDXBanKuaiList();
//			HashMap<String, BanKuai> curzsbanklist = bkdbopt.getTDXAllZhiShuList();
//			curtdxbanklist.putAll(curzsbanklist);
			
			HashMap<String, BanKuai> curtdxbanklist = bkdbopt.getTDXAllZhiShuAndBanKuai ();
			
			((BanKuaiFengXiZhanBiPaiMingTableModel)tableBkZhanBi.getModel()).refresh(curtdxbanklist, bkzbmap);
		}
	}
	
	private void initializeBanKuaiZhanBiByGrowthRate (String bkcode, String bkname)
	{
		Date bkfxdate = dateChooser.getDate();
		Date startdate = CommonUtility.getFirstDayOfWeek(bkfxdate);
		Date enddate = CommonUtility.getLastDayOfWeek(bkfxdate);
		
		
	}

	
	
	
	private void initializeBanKuaiZhanBiByZhanBi() 
	{
		Date bkfxdate = dateChooser.getDate();
		DefaultCategoryDataset barchartdataset = bkdbopt.getBanKuaiFengXiBarInfoOfChenJiaoErZhanBiOrderByZhanBi (bkfxdate);
		
		StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
		standardChartTheme.setExtraLargeFont(new Font("隶书",Font.BOLD,20) );
		standardChartTheme.setRegularFont(new Font("隶书",Font.BOLD,20) );
		standardChartTheme.setLargeFont(new Font("隶书",Font.BOLD,20));
		ChartFactory.setChartTheme(standardChartTheme);
		JFreeChart barchart = ChartFactory.createBarChart("板块成交量占比", "板块名称", "占比", barchartdataset,PlotOrientation.HORIZONTAL, true,true,false);
		CategoryPlot p = barchart.getCategoryPlot();
		p.setRangeGridlinePaint(Color.blue);
		ChartPanel chartPanel = new ChartPanel( barchart );
				
		sclpleft.setViewportView(chartPanel);
		
	}
	

	private JFreeChart setJFreeChartPlotPropety (JFreeChart piechart)
	{
		PiePlot plot = (PiePlot) piechart.getPlot();
		 LegendTitle legend = new LegendTitle(pieplotwkcur); 
	        legend.setPosition(RectangleEdge.TOP); 
//	    	pieplotwkcur.setIgnoreZeroValues(true);
//	    	pieplotwkcur.setCircular(false);
//	    	pieplotwkcur.setOutlinePaint(null);
//	    	pieplotwkcur.setLabelFont(new Font("SansSerif", Font.PLAIN, 8));
//	    	pieplotwkcur.setLabelGap(0.02);
//	    	pieplotwkcur.setLabelOutlinePaint(null);
//	    	pieplotwkcur.setLabelShadowPaint(null);
//	    	pieplotwkcur.setLabelBackgroundPaint(Color.WHITE);
//	    	pieplotwkcur.setSectionOutlinePaint(key, Color.WHITE);
//	    	pieplotwkcur.setSectionOutlineStroke(key ,new BasicStroke(0f));
//	    	pieplotwkcur.setLabelGenerator(null);
//	    	pieplotwkcur.setSectionOutlinesVisible(false);
	        
	        piechart.removeLegend();
	        
	        return piechart;
	}
  
//    private void createPieChartPanel (JScrollPane scrollPane,ChartPanel piechartPanel,JFreeChart piechart,PiePlot pieplot,DefaultPieDataset piedataset)
//    {
////    	BarRenderer renderer = new BarRenderer ();
////    	renderer.setToolTipGenerator(new CustomToolTipGenerator() );
////        renderer.setBaseItemLabelsVisible(true);
////        DecimalFormat decimalformate = new DecimalFormat("%#0.000");
////        renderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}",decimalformate));
////        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12,TextAnchor.HALF_ASCENT_CENTER));
////        renderer.setItemLabelsVisible(true);
//        
//    	pieplot = new PiePlot(); 
//        LegendTitle legend = new LegendTitle(pieplot); 
//        legend.setPosition(RectangleEdge.TOP); 
//        pieplot.setDataset(piedataset); 
////    	pieplotwkcur.setIgnoreZeroValues(true);
////    	pieplotwkcur.setCircular(false);
////    	pieplotwkcur.setOutlinePaint(null);
////    	pieplotwkcur.setLabelFont(new Font("SansSerif", Font.PLAIN, 8));
////    	pieplotwkcur.setLabelGap(0.02);
////    	pieplotwkcur.setLabelOutlinePaint(null);
////    	pieplotwkcur.setLabelShadowPaint(null);
////    	pieplotwkcur.setLabelBackgroundPaint(Color.WHITE);
////    	pieplotwkcur.setSectionOutlinePaint(key, Color.WHITE);
////    	pieplotwkcur.setSectionOutlineStroke(key ,new BasicStroke(0f));
////    	pieplotwkcur.setLabelGenerator(null);
////    	pieplotwkcur.setSectionOutlinesVisible(false);
//        
//        piechart = new JFreeChart(pieplot);
//        piechart.removeLegend();
//
//        piechartPanel = new ChartPanel(piechart);
//        
//    	scrollPane.setViewportView(piechartPanelwkcur);
//        //设置显示到图片最右边
//        Rectangle bounds = scrollPane.getViewport().getViewRect();
//        Dimension size = scrollPane.getViewport().getViewSize();
//        int x = (size.width - bounds.width) ;
//        int y = (size.height - bounds.height) ;
//        scrollPane.getViewport().setViewPosition(new Point(x, 0));
//    }
	private void diaplyGeGuZhanBiWeekCur(String tdxbkname, String tdxbkcode, Date bkfxdate) 
	{
//		Date monday = CommonUtility.getFirstDayOfWeek(bkfxdate);
//    	Date lastday = CommonUtility.getLastDayOfWeek(bkfxdate);
//    	HashMap<String, ASingleStockInfo> tmpallbkge = bkdbopt.getTDXBanKuaiGeGuOfHyGnFg (tdxbkname,tdxbkcode,monday,lastday,true );
//    	
//    	piechartdatasetwkcur = new DefaultPieDataset();
//    	for(String ggcode: tmpallbkge.keySet()) {
//    		ASingleStockInfo tmpstockinfo = tmpallbkge.get(ggcode);
//    		String stockname = tmpstockinfo.getStockname();
//    		HashMap<String, Double> stockchengjiaoe = tmpstockinfo.getSysBanKuaiChenJiaoE();
//    		double cje = stockchengjiaoe.get(tdxbkcode);
//    		piechartdatasetwkcur.setValue(ggcode+stockname,cje);
//    	}
//    	pieplotwkcur.setDataset(piechartdatasetwkcur);
		
	}
	private void diaplyGeGuZhanBiWeekLast(String tdxbkname, String tdxbkcode, Date bkfxdate) 
	{
//		LocalDate mondaylocal = CommonUtility.getDateFromWeekAndYear(CommonUtility.getWeekNumber(bkfxdate)-2,CommonUtility.getYearNumber(bkfxdate),1);
//		Date monday = Date.from(mondaylocal.atStartOfDay(ZoneId.systemDefault()).toInstant());
//		
//		LocalDate saterdaylocal = CommonUtility.getDateFromWeekAndYear(CommonUtility.getWeekNumber(bkfxdate)-2,CommonUtility.getYearNumber(bkfxdate),6);
//		Date saterday = Date.from(saterdaylocal.atStartOfDay(ZoneId.systemDefault()).toInstant());
//
//		HashMap<String, ASingleStockInfo> tmpallbkge = bkdbopt.getTDXBanKuaiGeGuOfHyGnFg (tdxbkname,tdxbkcode,monday,saterday,true );
//    	
//    	piechartdatasetwklast = new DefaultPieDataset();
//    	for(String ggcode: tmpallbkge.keySet()) {
//    		ASingleStockInfo tmpstockinfo = tmpallbkge.get(ggcode);
//    		String stockname = tmpstockinfo.getStockname();
//    		HashMap<String, Double> stockchengjiaoe = tmpstockinfo.getSysBanKuaiChenJiaoE();
//    		double cje = stockchengjiaoe.get(tdxbkcode);
//    		piechartdatasetwklast.setValue(ggcode+stockname,cje);
//    	}
//    	pieplotwklast.setDataset(piechartdatasetwklast);
		
	}
	private void diaplyGeGuZhanBiWeekLast2(String tdxbkname, String tdxbkcode, Date bkfxdate) 
	{
//		LocalDate mondaylocal = CommonUtility.getDateFromWeekAndYear(CommonUtility.getWeekNumber(bkfxdate)-3,CommonUtility.getYearNumber(bkfxdate),1);
//		Date monday = Date.from(mondaylocal.atStartOfDay(ZoneId.systemDefault()).toInstant());
//		
//		LocalDate saterdaylocal = CommonUtility.getDateFromWeekAndYear(CommonUtility.getWeekNumber(bkfxdate)-3,CommonUtility.getYearNumber(bkfxdate),6);
//		Date saterday = Date.from(saterdaylocal.atStartOfDay(ZoneId.systemDefault()).toInstant());
//
//		HashMap<String, ASingleStockInfo> tmpallbkge = bkdbopt.getTDXBanKuaiGeGuOfHyGnFg (tdxbkname,tdxbkcode,monday,saterday,true );
//    	
//    	piechartdatasetwklast2 = new DefaultPieDataset();
//    	for(String ggcode: tmpallbkge.keySet()) {
//    		ASingleStockInfo tmpstockinfo = tmpallbkge.get(ggcode);
//    		String stockname = tmpstockinfo.getStockname();
//    		HashMap<String, Double> stockchengjiaoe = tmpstockinfo.getSysBanKuaiChenJiaoE();
//    		double cje = stockchengjiaoe.get(tdxbkcode);
//    		piechartdatasetwklast2.setValue(ggcode+stockname,cje);
//    	}
//    	pieplotwklast2.setDataset(piechartdatasetwklast2);
		
	}


    protected void displayGeGuWeekZhanBi(String tdxbkname, String tdxbkcode, Date bkfxdate) 
    {
    	diaplyGeGuZhanBiWeekCur (tdxbkname,tdxbkcode,bkfxdate);
    	diaplyGeGuZhanBiWeekLast(tdxbkname,tdxbkcode,bkfxdate);
    	diaplyGeGuZhanBiWeekLast2(tdxbkname,tdxbkcode,bkfxdate);
	}

	private void createEvents() 
	{
		tableBkZhanBi.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				int row = tableBkZhanBi.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择一个账户","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				String bankcode = ((BanKuaiFengXiZhanBiPaiMingTableModel)tableBkZhanBi.getModel()).getBanKuaiCode(row);
				String bankname = ((BanKuaiFengXiZhanBiPaiMingTableModel)tableBkZhanBi.getModel()).getBanKuaiName(row);
				Date bkfxdate = dateChooser.getDate();
//				displayGeGuWeekZhanBi (bankname,bankcode,bkfxdate);
				
			}
		});
		
		dateChooser.addPropertyChangeListener(new PropertyChangeListener() {
		    @Override
		    public void propertyChange(PropertyChangeEvent e) {
		    	if("date".equals(e.getPropertyName()))
		    		initializeBanKuaiZhanBiByGrowthRate ();
//		        System.out.println(e.getPropertyName()+ ": " + e.getNewValue());
		    }
		});
		
	}

	  /*
     * 创建个股成交额占比panel
     */
    @SuppressWarnings("deprecation")
	private void createPieChartPanelWkCur( ) 
    {
//    	BarRenderer renderer = new BarRenderer ();
//    	renderer.setToolTipGenerator(new CustomToolTipGenerator() );
//        renderer.setBaseItemLabelsVisible(true);
//        DecimalFormat decimalformate = new DecimalFormat("%#0.000");
//        renderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}",decimalformate));
//        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12,TextAnchor.HALF_ASCENT_CENTER));
//        renderer.setItemLabelsVisible(true);
        
    	pieplotwkcur = new PiePlot(); 
        LegendTitle legend = new LegendTitle(pieplotwkcur); 
        legend.setPosition(RectangleEdge.TOP); 
    	pieplotwkcur.setDataset(piechartdatasetwkcur); 
//    	pieplotwkcur.setIgnoreZeroValues(true);
//    	pieplotwkcur.setCircular(false);
//    	pieplotwkcur.setOutlinePaint(null);
//    	pieplotwkcur.setLabelFont(new Font("SansSerif", Font.PLAIN, 8));
//    	pieplotwkcur.setLabelGap(0.02);
//    	pieplotwkcur.setLabelOutlinePaint(null);
//    	pieplotwkcur.setLabelShadowPaint(null);
//    	pieplotwkcur.setLabelBackgroundPaint(Color.WHITE);
//    	pieplotwkcur.setSectionOutlinePaint(key, Color.WHITE);
//    	pieplotwkcur.setSectionOutlineStroke(key ,new BasicStroke(0f));
//    	pieplotwkcur.setLabelGenerator(null);
//    	pieplotwkcur.setSectionOutlinesVisible(false);
        
//    	piechartwkcur = new JFreeChart(pieplotwkcur);
//    	piechartwkcur.removeLegend();
//        int x = (size.width - bounds.width) ;
//        int y = (size.height - bounds.height) ;
    }

	  /*
     * 创建个股成交额占比panel
     */
    @SuppressWarnings("deprecation")
	private void createPieChartPanelWkLast2( ) 
    {
//    	BarRenderer renderer = new BarRenderer ();
//    	renderer.setToolTipGenerator(new CustomToolTipGenerator() );
//        renderer.setBaseItemLabelsVisible(true);
//        DecimalFormat decimalformate = new DecimalFormat("%#0.000");
//        renderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}",decimalformate));
//        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12,TextAnchor.HALF_ASCENT_CENTER));
//        renderer.setItemLabelsVisible(true);
        
    	pieplotwklast2 = new PiePlot(); 
        LegendTitle legend = new LegendTitle(pieplotwklast2); 
        legend.setPosition(RectangleEdge.TOP); 
        pieplotwklast2.setDataset(piechartdatasetwklast2); 
//    	pieplotwkcur.setIgnoreZeroValues(true);
//    	pieplotwkcur.setCircular(false);
//    	pieplotwkcur.setOutlinePaint(null);
//    	pieplotwkcur.setLabelFont(new Font("SansSerif", Font.PLAIN, 8));
//    	pieplotwkcur.setLabelGap(0.02);
//    	pieplotwkcur.setLabelOutlinePaint(null);
//    	pieplotwkcur.setLabelShadowPaint(null);
//    	pieplotwkcur.setLabelBackgroundPaint(Color.WHITE);
//    	pieplotwkcur.setSectionOutlinePaint(key, Color.WHITE);
//    	pieplotwkcur.setSectionOutlineStroke(key ,new BasicStroke(0f));
//    	pieplotwkcur.setLabelGenerator(null);
//    	pieplotwkcur.setSectionOutlinesVisible(false);
        
        piechartwklast2 = new JFreeChart(pieplotwklast2);
        piechartwklast2.removeLegend();
//        int x = (size.width - bounds.width) ;
//        int y = (size.height - bounds.height) ;
    }
	  /*
     * 创建个股成交额占比panel
     */
    @SuppressWarnings("deprecation")
	private void createPieChartPanelWkLast( ) 
    {
//    	BarRenderer renderer = new BarRenderer ();
//    	renderer.setToolTipGenerator(new CustomToolTipGenerator() );
//        renderer.setBaseItemLabelsVisible(true);
//        DecimalFormat decimalformate = new DecimalFormat("%#0.000");
//        renderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}",decimalformate));
//        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12,TextAnchor.HALF_ASCENT_CENTER));
//        renderer.setItemLabelsVisible(true);
        
    	pieplotwklast = new PiePlot(); 
        LegendTitle legend = new LegendTitle(pieplotwklast); 
        legend.setPosition(RectangleEdge.TOP); 
        pieplotwklast.setDataset(piechartdatasetwklast); 
//    	pieplotwkcur.setIgnoreZeroValues(true);
//    	pieplotwkcur.setCircular(false);
//    	pieplotwkcur.setOutlinePaint(null);
//    	pieplotwkcur.setLabelFont(new Font("SansSerif", Font.PLAIN, 8));
//    	pieplotwkcur.setLabelGap(0.02);
//    	pieplotwkcur.setLabelOutlinePaint(null);
//    	pieplotwkcur.setLabelShadowPaint(null);
//    	pieplotwkcur.setLabelBackgroundPaint(Color.WHITE);
//    	pieplotwkcur.setSectionOutlinePaint(key, Color.WHITE);
//    	pieplotwkcur.setSectionOutlineStroke(key ,new BasicStroke(0f));
//    	pieplotwkcur.setLabelGenerator(null);
//    	pieplotwkcur.setSectionOutlinesVisible(false);
        
        piechartwklast = new JFreeChart(pieplotwklast);
//        piechartwklast.removeLegend();
//        int x = (size.width - bounds.width) ;
//        int y = (size.height - bounds.height) ;
    }


	



	private final JPanel contentPanel = new JPanel();
	private JTextField tfldparsedfile;
	private JButton btnchosefile;
	private JButton okButton;
	private JButton cancelButton;
	private JDateChooser dateChooser;
	private JScrollPane sclpleft;
	private JTable tableBkZhanBi;
	private JTable tableGuGuZhanBiInBk;
	private void initializeGui() {
		setTitle("\u677F\u5757\u5206\u6790");
		setBounds(100, 100, 1870, 993);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		
		JPanel panel_1 = new JPanel();
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		
		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		
		JPanel panel_5 = new JPanel();
		panel_5.setBorder(new TitledBorder(null, "Test", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GroupLayout gl_contentPanel = new GroupLayout(contentPanel);
		gl_contentPanel.setHorizontalGroup(
			gl_contentPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.TRAILING)
						.addComponent(panel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 1824, Short.MAX_VALUE)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 254, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addComponent(panel_2, GroupLayout.DEFAULT_SIZE, 753, Short.MAX_VALUE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(panel_3, GroupLayout.PREFERRED_SIZE, 805, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_contentPanel.createSequentialGroup()
									.addComponent(panel_4, GroupLayout.PREFERRED_SIZE, 1019, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(panel_5, GroupLayout.PREFERRED_SIZE, 545, GroupLayout.PREFERRED_SIZE)))))
					.addContainerGap())
		);
		gl_contentPanel.setVerticalGroup(
			gl_contentPanel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 48, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPanel.createSequentialGroup()
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(panel_3, GroupLayout.PREFERRED_SIZE, 410, GroupLayout.PREFERRED_SIZE)
								.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 410, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
							.addGroup(gl_contentPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(panel_4, GroupLayout.PREFERRED_SIZE, 409, GroupLayout.PREFERRED_SIZE)
								.addComponent(panel_5, GroupLayout.PREFERRED_SIZE, 413, GroupLayout.PREFERRED_SIZE))
							.addContainerGap())
						.addComponent(panel_1, GroupLayout.DEFAULT_SIZE, 847, Short.MAX_VALUE)))
		);
		
		BanKuaiFengXiZhanBiPaiMingTableModel tablelast2 = new BanKuaiFengXiZhanBiPaiMingTableModel ();
		
		BanKuaiFengXiZhanBiPaiMingTableModel tablelast2gr = new BanKuaiFengXiZhanBiPaiMingTableModel ();
		
		BanKuaiFengXiZhanBiPaiMingTableModel tablelast = new BanKuaiFengXiZhanBiPaiMingTableModel ();
		
		BanKuaiFengXiZhanBiPaiMingTableModel tablelastgr = new BanKuaiFengXiZhanBiPaiMingTableModel ();
		
		BanKuaiFengXiZhanBiPaiMingTableModel tablecurwk = new BanKuaiFengXiZhanBiPaiMingTableModel ();
		
		BanKuaiFengXiZhanBiPaiMingTableModel tablecurwkgr = new BanKuaiFengXiZhanBiPaiMingTableModel ();
		
		sclpleft = new JScrollPane();
		
		JButton btnNewButton = new JButton("\u7BA1\u7406\u677F\u5757");
		
		JScrollPane scrollPane_6 = new JScrollPane();
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane_6, GroupLayout.PREFERRED_SIZE, 234, GroupLayout.PREFERRED_SIZE)
						.addComponent(sclpleft, GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
						.addComponent(btnNewButton))
					.addContainerGap())
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_panel_1.createSequentialGroup()
					.addGap(6)
					.addComponent(btnNewButton)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(sclpleft, GroupLayout.PREFERRED_SIZE, 374, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane_6, GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		tableGuGuZhanBiInBk = new JTable();
		scrollPane_6.setViewportView(tableGuGuZhanBiInBk);
		
		BanKuaiFengXiZhanBiPaiMingTableModel bkzb = new BanKuaiFengXiZhanBiPaiMingTableModel ();
		tableBkZhanBi = new JTable(bkzb){
			private static final long serialVersionUID = 1L;
			public String getToolTipText(MouseEvent e) 
			{
                String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);

                try {
                    tip = getValueAt(rowIndex, colIndex).toString();
                } catch (RuntimeException e1) {
                	e1.printStackTrace();
                }
                return tip;
            } 
		};
		
		
		sclpleft.setViewportView(tableBkZhanBi);
		panel_1.setLayout(gl_panel_1);
		
		JLabel lblNewLabel = new JLabel("\u89E3\u6790\u677F\u5757\u6587\u4EF6");
		
		tfldparsedfile = new JTextField();
		tfldparsedfile.setColumns(10);
		
		btnchosefile = new JButton("");
		btnchosefile.setIcon(new ImageIcon(BanKuaiFengXi.class.getResource("/images/open24.png")));
		
		dateChooser = new JDateChooser();
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(7)
					.addComponent(lblNewLabel)
					.addGap(4)
					.addComponent(tfldparsedfile, GroupLayout.PREFERRED_SIZE, 621, GroupLayout.PREFERRED_SIZE)
					.addGap(4)
					.addComponent(btnchosefile)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(dateChooser, GroupLayout.PREFERRED_SIZE, 224, GroupLayout.PREFERRED_SIZE)
					.addGap(835))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(16)
							.addComponent(lblNewLabel))
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(7)
							.addComponent(tfldparsedfile, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panel.createSequentialGroup()
							.addGap(7)
							.addGroup(gl_panel.createParallelGroup(Alignment.LEADING, false)
								.addComponent(dateChooser, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(btnchosefile, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
					.addContainerGap(8, Short.MAX_VALUE))
		);
		panel.setLayout(gl_panel);
		contentPanel.setLayout(gl_contentPanel);
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				getRootPane().setDefaultButton(okButton);
			}
			{
				cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
			}
			
			JCheckBox chckbxNewCheckBox = new JCheckBox("\u6309\u5360\u6BD4\u6392\u5E8F(\u9ED8\u8BA4\u6309\u5360\u6BD4\u589E\u957F\u7387\u6392\u5E8F)");
			chckbxNewCheckBox.setEnabled(false);
			
			JLabel lblNewLabel_1 = new JLabel("New label");
			buttonPane.setLayout(new MigLayout("", "[229px][1062px][71px][84px]", "[34px]"));
			buttonPane.add(chckbxNewCheckBox, "cell 0 0,growx,aligny center");
			buttonPane.add(okButton, "cell 2 0,growx,aligny center");
			buttonPane.add(cancelButton, "cell 3 0,growx,aligny center");
			buttonPane.add(lblNewLabel_1, "cell 1 0,grow");
		}
		
		createPieChartPanelWkCur ();
		createPieChartPanelWkLast ();
		createPieChartPanelWkLast2 ();
	}
}


class BanKuaiFengXiZhanBiPaiMingTableModel extends AbstractTableModel 
{
	String[] jtableTitleStrings = { "代码", "名称","占比增长率"};
	HashMap<String,Double> zhanbigrowthratemap;
	HashMap<String,BanKuai> bkmap;
	
	BanKuaiFengXiZhanBiPaiMingTableModel ()
	{
		
	}

	public void refresh  (HashMap<String,BanKuai> curbkzslist, HashMap<String,Double> zhanbigrowthratemap2)
	{
		this.bkmap = curbkzslist;
		
		this.zhanbigrowthratemap = zhanbigrowthratemap2.entrySet().stream()
	                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
	                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
	                        (oldValue, newValue) -> oldValue, LinkedHashMap::new))
	                ;
		this.fireTableDataChanged();
	}

	 public int getRowCount() 
	 {
		 if(this.zhanbigrowthratemap == null)
			 return 0;
		 else 
			 return this.zhanbigrowthratemap.size();
	 }

	    @Override
	    public int getColumnCount() 
	    {
	        return jtableTitleStrings.length;
	    } 
	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	if(bkmap.isEmpty())
	    		return null;
	    	
	    	String[] bkcodeArray = zhanbigrowthratemap.keySet().toArray(new String[zhanbigrowthratemap.keySet().size()]);
	    	String bkcode = bkcodeArray[rowIndex];
	    	BanKuai thisbk = bkmap.get(bkcode);
	    	String thisbkname = thisbk.getMyOwnName(); 
	    	NumberFormat percentFormat = NumberFormat.getPercentInstance();
	    	
	    	Object value = "??";
	    	switch (columnIndex) {
            case 0:
                value = bkcode;
                break;
            case 1: 
            	value = thisbkname;
            	break;
            case 2:
            	value = percentFormat.format(zhanbigrowthratemap.get(bkcode));
            	break;
	    	}

        return value;
	  }

     public Class<?> getColumnClass(int columnIndex) {
		      Class clazz = String.class;
		      switch (columnIndex) {
		      case 0:
		    	  clazz = String.class;
		    	  break;
		        case 1:
			          clazz = String.class;
			          break;
		        case 2:
			          clazz = String.class;
			          break;
		      }
		      
		      return clazz;
	 }
	    
	    public String getColumnName(int column) { 
	    	return jtableTitleStrings[column];
	    }//设置表格列名 
		

	    public boolean isCellEditable(int row,int column) {
	    	return false;
		}
	    public String getBanKuaiCode (int row) 
	    {
	    	return (String)this.getValueAt(row,0);
	    }
	    public String getBanKuaiName (int row) 
	    {
	    	return (String)this.getValueAt(row,1);
	    } 
	    
}