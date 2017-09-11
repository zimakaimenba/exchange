package com.exchangeinfomanager.gui.subgui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.ui.RectangleEdge;

import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.sun.rowset.CachedRowSetImpl;

public class GeGuBanKuaiZhanBiFengXi extends JDialog {

	private final JPanel contentPanel = new JPanel();
//    private static final int COLS = 20;
//    private static final int VISIBLE = 4;
//    private static final int ROWS = 5;
//    private static final int VALUES = 10;
//    private static final Random rnd = new Random();
//    private List<String> columns;
//    private List<List<List<Double>>> data;
//    private DefaultBoxAndWhiskerCategoryDataset dataset;
    private CategoryPlot plot;
    private ChartPanel chartPanel;
    private JPanel controlPanel;
//    private int start = 0;
    
    private int currentDisplayedBkIndex ;
	private ArrayList<String> suosubkcodelist;
	private DefaultCategoryDataset barchartdataset ;
	private BanKuaiDbOperation bkdbopt;
	private HashMap<String, String> suoshubkmap;
	private JFreeChart barchart;


	/**
	 * Create the dialog.
	 * @param suosubk 
	 */
	public GeGuBanKuaiZhanBiFengXi(HashMap<String,String> suosubk) {
		initializeGui ();
		
		StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
		standardChartTheme.setExtraLargeFont(new Font("隶书",Font.BOLD,20) );
		standardChartTheme.setRegularFont(new Font("隶书",Font.BOLD,20) );
		standardChartTheme.setLargeFont(new Font("隶书",Font.BOLD,20));
		ChartFactory.setChartTheme(standardChartTheme);
		
		bkdbopt = new BanKuaiDbOperation ();
		currentDisplayedBkIndex = 0;
		suosubkcodelist = new ArrayList<String>(suosubk.keySet());
		suoshubkmap = suosubk;
//		createData();
        createDataset(currentDisplayedBkIndex);
        createChartPanel(currentDisplayedBkIndex);
        createControlPanel();
	}
	
//	 private void createData() {
//	        columns = new ArrayList<String>(COLS);
//	        data = new ArrayList<List<List<Double>>>();
//	        for (int i = 0; i < COLS; i++) {
//	            String name = "Category" + String.valueOf(i + 1);
//	            columns.add(name);
//	            List<List<Double>> list = new ArrayList<List<Double>>();
//	            for (int j = 0; j < ROWS; j++) {
//	                list.add(createValues());
//	            }
//	            data.add(list);
//	        }
//	    }
//
//	    private List<Double> createValues() {
//	        List<Double> list = new ArrayList<Double>();
//	        for (int i = 0; i < VALUES; i++) {
//	            list.add(rnd.nextGaussian());
//	        }
//	        return list;
//	    }

	    private void createDataset(int start) {
	    	barchartdataset = new DefaultCategoryDataset();
	    	CachedRowSetImpl rs = bkdbopt.getBanKuaiZhanBi (suosubkcodelist.get(start));
	    	int row =0;
	    	try {
	    		while (rs.next()) {
	    			String weeknumber =rs.getString("CALWEEK");
	    			Double zhanbi = rs.getDouble("占比");
	    			barchartdataset.setValue(zhanbi,"板块占比",weeknumber);
	    			row ++;
	    		}
	    	} catch (SQLException e) {
	    		// TODO Auto-generated catch block
	    		e.printStackTrace();
	    	}
	    	
	    	if(barchart !=null) {
	    		String bkcode = suosubkcodelist.get(start);
		    	String bkname = suoshubkmap.get(bkcode);
		        barchart.setTitle("'"+ bkcode + bkname +"'板块成交量占比");
	    	}
	    }
	    
	    private void createChartPanel(int start) 
	    {
	        CategoryItemRenderer renderer = new StackedBarRenderer(); 
	        renderer.setBaseItemLabelsVisible(true); 
	        plot = new CategoryPlot(); 
	        LegendTitle legend = new LegendTitle(plot); 
	        legend.setPosition(RectangleEdge.TOP); 
	        plot.setDataset(barchartdataset); 
	        plot.setRenderer(renderer); 
	        plot.setDomainAxis(new CategoryAxis("周数")); 
	        plot.setRangeAxis(new NumberAxis("占比"));

	        barchart = new JFreeChart(plot);
	        String bkcode = suosubkcodelist.get(start);
	    	String bkname = suoshubkmap.get(bkcode);
	        barchart.setTitle("'"+ bkcode + bkname +"'板块成交量占比");

	        chartPanel = new ChartPanel(barchart);
	    }

//	    private void createChartPanel2(int start) {
////	        CategoryAxis xAxis = new CategoryAxis("Category");
////	        NumberAxis yAxis = new NumberAxis("Value");
//	    	String bkcode = suosubkcodelist.get(start);
//	    	String bkname = suoshubkmap.get(bkcode);
//	    	JFreeChart barchart = ChartFactory.createBarChart( "'"+ bkcode + bkname +"'板块成交量占比", "周数", "占比", barchartdataset,PlotOrientation.VERTICAL, true,true,false);
//	        BarRenderer renderer = new BarRenderer();
//	        plot = barchart.getCategoryPlot();
//	        plot.setRangeGridlinePaint(Color.BLACK);
////	        plot = new CategoryPlot(barchartdataset, xAxis, yAxis, renderer);
////	        JFreeChart chart = new JFreeChart("BoxAndWhiskerDemo", plot);
//	        chartPanel = new ChartPanel(barchart);
//	    }

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
	                createDataset(currentDisplayedBkIndex);
	                plot.setDataset(barchartdataset);
	            }
	        }));
	        controlPanel.add(new JButton(new AbstractAction("Next\u22b3") {

	            @Override
	            public void actionPerformed(ActionEvent e) {
	            	currentDisplayedBkIndex += 1;
	                if (currentDisplayedBkIndex >= suosubkcodelist.size()) {
	                	currentDisplayedBkIndex = suosubkcodelist.size();
	                    return;
	                }
	                createDataset(currentDisplayedBkIndex);
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

	    
	private void initializeGui() {
		setBounds(100, 100, 900, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
	}

	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		try {
//			GeGuBanKuaiZhanBiFengXi dialog = new GeGuBanKuaiZhanBiFengXi(null);
//			
////			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
////			dialog.setVisible(true);
//			
////			BoxAndWhiskerDemo demo = new BoxAndWhiskerDemo();
//			dialog.add(dialog.getChartPanel(), BorderLayout.CENTER);
//			dialog.add(dialog.getControlPanel(), BorderLayout.SOUTH);
//			dialog.pack();
//			dialog.setLocationRelativeTo(null);
//			dialog.setVisible(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

}

//	HashMap<String, String> suosubk = stockbasicinfo.getSuoShuTDXSysBanKuai();
//for(String bkcode:suosubk.keySet()) {
//	StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
//	standardChartTheme.setExtraLargeFont(new Font("隶书",Font.BOLD,20) );
//	standardChartTheme.setRegularFont(new Font("隶书",Font.BOLD,20) );
//	standardChartTheme.setLargeFont(new Font("隶书",Font.BOLD,20));
//	ChartFactory.setChartTheme(standardChartTheme);
//	DefaultCategoryDataset barchartdataset = new DefaultCategoryDataset();
//	
//	CachedRowSetImpl rs = bkdbopt.getBanKuaiZhanBi (bkcode);
//	int row =0;
//	try {
//		while (rs.next()) {
//			String weeknumber =rs.getString("CALWEEK");
//			Double zhanbi = rs.getDouble("占比");
//			barchartdataset.setValue(zhanbi,"板块占比",weeknumber);
//			row ++;
//		}
//	} catch (SQLException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//	
//	JFreeChart barchart = ChartFactory.createBarChart("'" + bkcode + suosubk.get(bkcode) + "'"+"板块成交量占比", "周数", "占比", barchartdataset,PlotOrientation.VERTICAL, true,true,false);
//	try {
//		File tmpreportfolder = Files.createTempDir();
//		File tmprecordfile = new File(tmpreportfolder + bkcode + ".jpg"); //new File("d:\\chart.jpg")
//		int chartxlength = 35*row;
//	     ChartUtilities.saveChartAsJPEG(tmprecordfile, barchart, chartxlength, 300);
//	     
//	     ImageIcon icon = new ImageIcon(tmpreportfolder + bkcode + ".jpg");
//	     JLabel labelchart = new JLabel(icon);
//	     scrollPanechenjl.add(labelchart);
//	     scrollPanechenjl.setViewportView(labelchart);
//	     
//	} catch (IOException e) {
//		e.printStackTrace();
//	     System.err.println("Problem occurred creating chart.");
//	}
//}
//
//
