package com.exchangeinfomanager.bankuaifengxi.CategoryBar;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.CategoryPointerAnnotation;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.exchangeinfomanager.bankuaifengxi.ExportCondition;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.DaPan;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.nodexdata.NodeXPeriodDataBasic;
import com.exchangeinfomanager.nodes.nodexdata.StockNodeXPeriodData;
import com.exchangeinfomanager.nodes.nodexdata.TDXNodeGivenPeriodDataItem;
import com.exchangeinfomanager.nodes.nodexdata.TDXNodesXPeriodData;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;

public class BanKuaiFengXiCategoryBarChartCjlZhanbiPnl extends BanKuaiFengXiCategoryBarChartPnl 
{

	public BanKuaiFengXiCategoryBarChartCjlZhanbiPnl() 
	{
		super ();

		super.plot.setRenderer(0,new CustomCategroyRendererForCjlZhanBi() );
	
		
		
//		linechartdataset = new XYSeriesCollection();
//		plot.setDataset(1, linechartdataset);
		
        
        // change the rendering order so the primary dataset appears "behind" the 
        // other datasets...
//        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
	}
	
	
	private static Logger logger = Logger.getLogger(BanKuaiFengXiCategoryBarChartCjeZhanbiPnl.class);
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.bankuaifengxi.BarChartPanelDataChangedListener#updatedDate(com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode, java.time.LocalDate, int, java.lang.String)
	 * ����ռ������
	 */
	public void updatedDate (TDXNodes node, LocalDate startdate, LocalDate enddate,String period)
	{
//		this.setNodeCjeZhanBi (node,startdate,enddate,period);
		barchartdataset.clear();
		
		super.setCurDisplayNode(node );
		super.globeperiod = period;
		NodeXPeriodDataBasic nodexdata = node.getNodeXPeroidData(period);
		
		displayDataToGui (nodexdata,startdate,enddate,period);
		
//		CustomCategroyRendererForZhanBi render = (CustomCategroyRendererForZhanBi)super.plot.getRenderer();
//		render.setDisplayNode(this.curdisplayednode);
//		render.setDisplayNodeXPeriod (nodexdata);
//		
//		//���з��������ticklable��ʾ��ɫ
		CategoryLabelCustomizableCategoryAxis axis = (CategoryLabelCustomizableCategoryAxis)super.plot.getDomainAxis();
		axis.setDisplayNode(this.getCurDisplayedNode(),period);
		
	}
	/*
	 * 
	 */
	private void displayDataToGui (NodeXPeriodDataBasic nodexdata,LocalDate startdate,LocalDate enddate,String period)
	{
		DaPan dapan;
		if(this.getCurDisplayedNode().getType() == BkChanYeLianTreeNode.BKGEGU) {
			BanKuai bk = ((StockOfBanKuai)this.getCurDisplayedNode()).getBanKuai();
			dapan = (DaPan)bk.getRoot();
		}
		else
			dapan = (DaPan)super.getCurDisplayedNode().getRoot();
		
		LocalDate requireend = enddate.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = startdate.with(DayOfWeek.MONDAY);

		super.barchart.setNotify(false);
		
		double highestHigh =0.0; int qkmax =0;//������ʾ��Χ
	
		LocalDate tmpdate = requirestart;
		do {
			LocalDate wkfriday = tmpdate.with(DayOfWeek.FRIDAY);
			//ZhanBi Part
			Double cjezb = nodexdata.getChenJiaoErZhanBi(tmpdate, 0);
			if(cjezb != null) {
				barchartdataset.setValue(cjezb,super.getRowKey(),wkfriday );

				if(cjezb > highestHigh)
					highestHigh = cjezb;
			} else {
				if( !dapan.isDaPanXiuShi(tmpdate,0,period) ) {
					cjezb = 0.0;
					barchartdataset.setValue(0.0,super.getRowKey(),wkfriday );
				} 
			}
			
			//�Ը����й�ע��¼��ʱ��
			if(super.getCurDisplayedNode().getType() == BkChanYeLianTreeNode.TDXGG) { //�Ը����й�ע��¼��ʱ��
					Integer gzjl = ((StockNodeXPeriodData)nodexdata).hasGzjlInPeriod(tmpdate, 0);
					if(gzjl != null) {
						double angle; Color paintcolor;String label;
						if(gzjl == 1) {
							angle = 10 * Math.PI/4;
							paintcolor = Color.YELLOW;
							label = "jr";
						}	else {						
							angle = 10 * Math.PI/4;
							paintcolor = Color.YELLOW;
							label = "yc";
						}
						CategoryPointerAnnotation cpa = new CategoryPointerAnnotation(label, wkfriday , cjezb, angle);
	//					cpa.setBaseRadius(0.0);
	//			        cpa.setTipRadius(25.0);
				        cpa.setFont(new Font("SansSerif", Font.BOLD, 10));
				        cpa.setPaint(paintcolor);
				        cpa.setTextAnchor(TextAnchor.CENTER);
						super.plot.addAnnotation(cpa);
					}
			}

			//QueKou Line Part
			Integer opneupquekou = ( (TDXNodesXPeriodData) nodexdata).getQueKouTongJiOpenUp(tmpdate, 0);
			Integer opendownquekou = ( (TDXNodesXPeriodData) nodexdata).getQueKouTongJiOpenDown(tmpdate, 0);
			Integer huibuupquekou = ( (TDXNodesXPeriodData) nodexdata).getQueKouTongJiHuiBuUp(tmpdate, 0);
			Integer huibudowquekou = ( (TDXNodesXPeriodData) nodexdata).getQueKouTongJiHuiBuDown(tmpdate, 0);	
			 
			if(opneupquekou != null) {
				super.linechartdataset.setValue(opneupquekou, "QueKouOpenUp", wkfriday );
				if (opneupquekou > qkmax)
					qkmax = opneupquekou ;
			}	else
				super.linechartdataset.setValue(0, "QueKouOpenUp", wkfriday );
			
			if(huibudowquekou != null) {
				super.linechartdataset.setValue(huibudowquekou, "QueKouHuiBuDownQk", wkfriday );
				if (huibudowquekou > qkmax)
					qkmax = huibudowquekou ;
			}	else
				super.linechartdataset.setValue(0, "QueKouHuiBuDownQk", wkfriday );
			
			if(opendownquekou != null && opendownquekou > qkmax) 
				qkmax = opendownquekou;
			if(huibuupquekou != null && huibuupquekou >qkmax)
				qkmax = huibuupquekou;

			
			
			if(period.equals(TDXNodeGivenPeriodDataItem.WEEK))
				tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
			else if(period.equals(TDXNodeGivenPeriodDataItem.DAY))
				tmpdate = tmpdate.plus(1, ChronoUnit.DAYS) ;
			else if(period.equals(TDXNodeGivenPeriodDataItem.MONTH))
				tmpdate = tmpdate.plus(1, ChronoUnit.MONTHS) ;
			
		} while (tmpdate.isBefore( requireend) || tmpdate.isEqual(requireend));
		
		setPanelTitle ("�ɽ���" + period + super.getCurDisplayedNode().getMyOwnCode(),requireend);
		
		super.barchart.setNotify(true);
		operationAfterDataSetup (nodexdata,requirestart,requireend,highestHigh,qkmax,period);
	}
	/*
	 * ͻ����ʾһЩԤ������
	 */
	private void operationAfterDataSetup (NodeXPeriodDataBasic nodexdata,LocalDate startdate, LocalDate enddate, double highestHigh, int upqkmax, String period) 
	{
		if(highestHigh == 0.0)
			return;
		
		CustomCategroyRendererForZhanBi render = (CustomCategroyRendererForZhanBi)super.plot.getRenderer();
		render.setDisplayNode(this.getCurDisplayedNode());
		render.setDisplayNodeXPeriod (nodexdata);
		
		//���з��������ticklable��ʾ��ɫ
		CategoryLabelCustomizableCategoryAxis axis = (CategoryLabelCustomizableCategoryAxis)super.plot.getDomainAxis(0);
		axis.setDisplayNode(this.getCurDisplayedNode(),period);
		
		try{
			super.plot.getRangeAxis(0).setRange(0, highestHigh*1.12);
		} catch (java.lang.IllegalArgumentException e) {
			e.printStackTrace();
			super.plot.getRangeAxis(0).setRange(0, 1);
			
		}
		
		try{
			super.plot.getRangeAxis(3).setRange(0, upqkmax*1.12);
		} catch (java.lang.IllegalArgumentException e) {
//			e.printStackTrace();
			super.plot.getRangeAxis(3).setRange(0, 1);
//			super.linechartdataset.clear();
		}
		
		super.plot.getRenderer(3).setSeriesPaint(0, Color.PINK);
		super.plot.getRenderer(3).setSeriesPaint(1, new Color(255,102,0));
		
		super.decorateXaxisWithYearOrMonth("month".trim());
		
		

	}

	@Override
	public void hightLightFxValues(ExportCondition expc) 
	{
		Integer cjezbdporbkmax = expc.getSettinDpmaxwk();
		Integer cjezbdporbkmin = expc.getSettingDpminwk();
		Double cjemin = expc.getSettingCjemin();
		Double cjemax = expc.getSettingCjeMax();
		Integer cjemaxwk = expc.getSettingCjemaxwk();
		Double shoowhsl = expc.getSettingHsl();
		
		if(cjezbdporbkmax != null) {
			((BanKuaiFengXiCategoryBarRenderer)plot.getRenderer()).setDisplayMaxwkLevel (cjezbdporbkmax);
			this.barchart.fireChartChanged();//���������
		}
		if(cjezbdporbkmin != null) {
			((BanKuaiFengXiCategoryBarRenderer)plot.getRenderer()).setDisplayMinwkLevel (cjezbdporbkmin);
			this.barchart.fireChartChanged();//���������
		}
	}

	@Override
	public String getToolTipSelected() 
	{
		int indexx = barchartdataset.getColumnIndex(super.dateselected);
		
		CustomCategroyToolTipGeneratorForCjlZhanBi ttpg = 	
		(CustomCategroyToolTipGeneratorForCjlZhanBi)(((CustomCategroyRendererForCjlZhanBi) plot.getRenderer()).getBaseToolTipGenerator());
		
		String tooltips = ttpg.generateToolTip(super.barchartdataset, 0, indexx);
		
		return tooltips;
	}

}
/*
 * 
 */
class CustomCategroyRendererForCjlZhanBi extends BanKuaiFengXiCategoryBarRenderer 
{
	private static final long serialVersionUID = 1L;
   
    public CustomCategroyRendererForCjlZhanBi() {
        super();
        super.displayedmaxwklevel = 4;
        super.displayedminwklevel = 8;
        super.displayedcolumncolorindex = Color.pink.darker();
        
        this.setBarPainter(new StandardBarPainter());
		
        BanKuaiFengXiCategoryBarToolTipGenerator custotooltip = new CustomCategroyToolTipGeneratorForCjlZhanBi();
		this.setBaseToolTipGenerator(custotooltip);
		
//		DecimalFormat decimalformate = new DecimalFormat("%#0.000");
//		this.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}",decimalformate));

		
		BkfxItemLabelGenerator labelgenerator = new BkfxItemLabelGeneratorForCjlZhanBi ();
		this.setBaseItemLabelGenerator(labelgenerator);
		this.setBaseItemLabelsVisible(true);
		labelgenerator.setDisplayedMaxWkLevel(super.displayedmaxwklevel);
		labelgenerator.setDisplayedMinWkLevel(super.displayedminwklevel);
    }

    public Paint getItemLabelPaint(final int row, final int column)
    {
    	CategoryPlot plot = getPlot ();
    	CategoryDataset dataset = plot.getDataset();
		String selected =  dataset.getColumnKey(column).toString();
    	LocalDate selecteddate = CommonUtility.formateStringToDate(selected);

    	Integer maxweek = nodexdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(selecteddate,0);
    	Integer minweek = nodexdata.getChenJiaoErZhanBiMinWeekOfSuperBanKuai(selecteddate,0);
    	
		if(maxweek != null  && maxweek >= super.displayedmaxwklevel)
			return Color.MAGENTA;
		else if (minweek != null && minweek != 0 && minweek >=  super.displayedminwklevel)
			return Color.GREEN;
		else
			return Color.black;
    }
    
}

class BkfxItemLabelGeneratorForCjlZhanBi extends BkfxItemLabelGenerator 
{
	public BkfxItemLabelGeneratorForCjlZhanBi ()
	{
		super (new DecimalFormat("%#0.000"));
	}
	@Override
	public String generateColumnLabel(CategoryDataset dataset, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String generateLabel(CategoryDataset dataset, int row, int column) {
		String selected =  dataset.getColumnKey(column).toString();
		
    	LocalDate selecteddate = CommonUtility.formateStringToDate(selected);
//    	if(selecteddate.equals(LocalDate.parse("2018-11-16"))) {
//    		String result2 = "";
//    	}
    	
		Integer maxweek = nodexdata.getChenJiaoLiangZhanBiMaxWeekOfSuperBanKuai(selecteddate,0);
		Integer minweek = nodexdata.getChenJiaoLiangZhanBiMinWeekOfSuperBanKuai(selecteddate,0);
		
		String result = "";
		if(maxweek != null && maxweek >= super.displayedmaxwklevel) {
			NumberFormat nf = this.getNumberFormat();
			result =  nf.format(dataset.getValue(row, column));
		} else if (minweek != null && minweek != 0 && minweek >=  super.displayedminwklevel) {
			NumberFormat nf = this.getNumberFormat();
			result =  nf.format(dataset.getValue(row, column));
		}
		
		return result;
	}

	@Override
	public String generateRowLabel(CategoryDataset arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}
	
}

class CustomCategroyToolTipGeneratorForCjlZhanBi extends BanKuaiFengXiCategoryBarToolTipGenerator 
{
	public String generateToolTip(CategoryDataset dataset, int row, int column)  
    {
		String selected = dataset.getColumnKey(column).toString();
    	LocalDate selecteddate = CommonUtility.formateStringToDate(selected);
    	 
		
		
		Double curzhanbidata = (Double)dataset.getValue(row, column);  //ռ��
		if(curzhanbidata == null)
			return null;

			Integer maxweek = nodexdata.getChenJiaoLiangZhanBiMaxWeekOfSuperBanKuai(selecteddate,0);//nodefx.getGgbkzhanbimaxweek();
			if(maxweek == null)
				return null;
			
			Integer minweek = nodexdata.getChenJiaoLiangZhanBiMinWeekOfSuperBanKuai(selecteddate,0);
			if(minweek == null)
				return null;
			
			Double hsl = null ;
			if(super.node.getType() == BkChanYeLianTreeNode.TDXGG) {
				hsl = ((StockNodeXPeriodData)nodexdata).getSpecificTimeHuanShouLv(selecteddate, 0);
			}
			
			
			String htmlstring = "";
			org.jsoup.nodes.Document doc = Jsoup.parse(htmlstring);
			Elements body = doc.getElementsByTag("body");
			for(Element elbody : body) {
				 org.jsoup.nodes.Element dl = elbody.appendElement("dl");
				 
				 org.jsoup.nodes.Element li5 = dl.appendElement("li");
				 li5.appendText(selecteddate.toString()); 
				
				String htmltext = null;
				
				try {
					DecimalFormat decimalformate = new DecimalFormat("%#0.00000");
					htmltext = "CJLռ��" + decimalformate.format(curzhanbidata) ;
					
					org.jsoup.nodes.Element li1 = dl.appendElement("li");
					li1.appendText(htmltext);
				} catch (java.lang.IllegalArgumentException e ) {
//					htmltext = "ռ��ռ��NULL" ;
				}
				
				 
				try {
					 htmltext = "CJLռ��MaxWk=" + maxweek.toString() ;
					 
					 org.jsoup.nodes.Element li2 = dl.appendElement("li");
					 li2.appendText(htmltext);
				} catch (java.lang.IllegalArgumentException e ) {
//					htmltext = "ռ��MaxWk=NULL"  ;
				}
				 
				 
				try {
					htmltext = "CJLռ��MinWk=" + minweek.toString() ;
					org.jsoup.nodes.Element li3 = dl.appendElement("li");
					 li3.appendText(htmltext);
				} catch (java.lang.IllegalArgumentException e ) {
//					htmltext = "ռ��MinWk=NULL" ;
				}
				 
				 
				try {
//					DecimalFormat decimalformate = new DecimalFormat("%.3f");
					if(hsl != null) {
						htmltext = "HSL=" + String.format("%.3f", hsl);
						
						org.jsoup.nodes.Element li4 = dl.appendElement("li");
						li4.appendText(htmltext);
					}
					
				} catch (java.lang.IllegalArgumentException e ) {
//					htmltext = "HSL=NULL";
				} catch (java.lang.NullPointerException ex) {
//					htmltext = "HSL=NULL";
				}
				
			}
			
			htmlstring = doc.toString();
			return htmlstring;
    }
}