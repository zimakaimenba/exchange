package com.exchangeinfomanager.bankuaifengxi;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
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

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.DaPan;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;

public class BanKuaiFengXiBarChartGgDpZbPnl extends BanKuaiFengXiBarChartPnl 
{

	public BanKuaiFengXiBarChartGgDpZbPnl() 
	{
		super ();
		super.plot.setRenderer(new CustomRendererForGgDpZhanBi() );
		((CustomRendererForGgDpZhanBi) plot.getRenderer()).setBarPainter(new StandardBarPainter());
	}
	
	/*
	 * ���ɺʹ��̵�ռ��
	 */
	public void setNodeAndDaPanZhanBiByWeek (BkChanYeLianTreeNode node, LocalDate displayedenddate1, DaPan dapan)
	{
		this.curdisplayednode = node;
//		this.displayedenddate = displayedenddate1;
		LocalDate requireend = displayedenddate1.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = displayedenddate1.with(DayOfWeek.MONDAY).minus(this.shoulddisplayedmonthnum,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		
		barchartdataset = new DefaultCategoryDataset();
//		datafx = new DefaultCategoryDataset();
		
		for(LocalDate tmpdate = requirestart;tmpdate.isBefore( requireend) || tmpdate.isEqual(requireend); tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ){
			ChenJiaoZhanBiInGivenPeriod tmpggrecord = node.getSpecficChenJiaoErRecord(tmpdate);
			ChenJiaoZhanBiInGivenPeriod tmpdprecord = dapan.getSpecficChenJiaoErRecord(tmpdate); //���ص�����֤�����ڵ�ĳ����¼������uplevel��¼�����������̵ĳɽ���
			
			if(tmpggrecord != null) {
				Double ggchenjiaoer = tmpggrecord.getMyOwnChengJiaoEr();
				Double dpchenjiaoer = tmpdprecord.getUpLevelChengJiaoEr(); //���ص�����֤�����ڵ�ĳ����¼������uplevel��¼�����������̵ĳɽ���
				Double ggdpratio = ggchenjiaoer/dpchenjiaoer; 
				LocalDate lastdayofweek = tmpggrecord.getRecordsDayofEndofWeek();
				
				barchartdataset.setValue(ggdpratio,"ռ��",lastdayofweek);
				
//				if(tmpggrecord.hasFengXiJieGuo ())
//					datafx.addValue(ggdpratio/5, "�������", lastdayofweek);
//				else
//					datafx.addValue(0, "�������", lastdayofweek);
			} else {
				if( !dapan.isThisWeekXiuShi(tmpdate) ) {
					barchartdataset.setValue(0.0,"ռ��",tmpdate);
//					datafx.addValue(0, "�������", tmpdate);
				} else //Ϊ��˵�������г�û�н���
					continue;
			}
		}
		
		CustomRendererForGgDpZhanBi render = (CustomRendererForGgDpZhanBi)super.plot.getRenderer();
		render.setDisplayNode(node);
		render.setDateSet (barchartdataset);
		
		DecimalFormat decimalformate = new DecimalFormat("%#0.000");
		render.setItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}",decimalformate));
		render.setItemLabelsVisible(true);
		
		CustomToolTipGeneratorForGgZhanBi custotooltip = new CustomToolTipGeneratorForGgZhanBi();
		custotooltip.setDisplayNode(curdisplayednode);
		render.setSeriesToolTipGenerator(0,custotooltip);
        
		//���з��������ticklable��ʾ��ɫ
		CategoryLabelCustomizableCategoryAxis axis = (CategoryLabelCustomizableCategoryAxis)super.plot.getDomainAxis();
		axis.setDisplayNode(node);
//		render.setBarCharType("���ɴ���ռ��");
		
		super.plot.setDataset(barchartdataset);
		
		setPanelTitle ("ռ��",displayedenddate1);
		
//		super.setBarFenXiSingle();
		
//		super.setDaZiJinValueMarker(0.001); //����0.1˵������ǿ�ƣ��ʽ�ռ�Ķ�
//		super.setDaZiJinValueMarker(0.0005); //����0.1˵������ǿ�ƣ��ʽ�ռ�Ķ�
		
		final IntervalMarker target = new IntervalMarker(0.0004, 0.001);
        target.setLabel("ǿ������");
        target.setLabelFont(new Font("SansSerif", Font.ITALIC, 11));
        target.setLabelAnchor(RectangleAnchor.LEFT);
        target.setLabelTextAnchor(TextAnchor.CENTER_LEFT);
        target.setPaint(new Color(222, 222, 255, 128));
        plot.addRangeMarker(target, Layer.BACKGROUND);
	}

}

class CustomRendererForGgDpZhanBi extends BanKuaiFengXiBarRenderer 
{
	private static final long serialVersionUID = 1L;
   
    public CustomRendererForGgDpZhanBi() {
        super();
        
    }
    public Paint getItemPaint(final int row, final int column) 
    {
        if(column == shouldcolumn)
            return Color.blue;
        else   
            return Color.RED;
   }
    public Paint getItemLabelPaint(final int row, final int column)
    {
		String selected = super.chartdataset.getColumnKey(column).toString();
    	LocalDate selecteddate = CommonUtility.formateStringToDate(selected);
    	 
    	ChenJiaoZhanBiInGivenPeriod nodefx = null;
		nodefx = ((Stock)node).getNodeFengXiResultForSpecificDate (selecteddate);
		Integer maxweek;
		if(nodefx != null)
			 maxweek = nodefx.getGgdpzhanbimaxweek();
		else 
			maxweek = 0;
		if(maxweek >=4)
			return Color.red;
		else 
			return Color.black;
    }
       
}

class CustomToolTipGeneratorForGgZhanBi implements CategoryToolTipGenerator 
{
    private BkChanYeLianTreeNode node;

	public String generateToolTip(CategoryDataset dataset, int row, int column)  
    {
		String selected = dataset.getColumnKey(column).toString();
    	LocalDate selecteddate = CommonUtility.formateStringToDate(selected);
    	 
    	ChenJiaoZhanBiInGivenPeriod nodefx = null;
		if(node.getType() == 4 )
			nodefx = ((BanKuai)node).getNodeFengXiResultForSpecificDate (selecteddate);
		else if(node.getType() == 6 ) 
			nodefx = ((Stock)node).getNodeFengXiResultForSpecificDate (selecteddate);
		
		if(nodefx == null)
			return "";
		
		String tooltip = selected.toString();
		if(node.getType() == 6 ) { //����,���ɴ��̱�ֻ�и��ɣ������а��
			Double curzhanbidata = nodefx.getCjlZhanBi();  //ռ��
			Double zhanbigrowthrate = nodefx.getGgbkzhanbigrowthrate();
			Double cjezhanbi = nodefx.getGgbkcjegrowthzhanbi();
			Integer maxweek = nodefx.getGgbkzhanbimaxweek();
			Double dpzhanbi = nodefx.getGgdpzhanbi();
			Integer dpzhanbimaxweek = nodefx.getGgdpzhanbimaxweek();
			
			
			DecimalFormat decimalformate = new DecimalFormat("%#0.000");
			try {
				tooltip = tooltip + "���ռ��" + decimalformate.format(curzhanbidata);
			} catch (java.lang.IllegalArgumentException e ) {
				tooltip = tooltip + "���ռ��NULL" ;
			}
			try {
				tooltip = tooltip +  "ռ�ȱ仯("+ decimalformate.format(zhanbigrowthrate) +  ")";
			} catch (java.lang.IllegalArgumentException e ) {
				tooltip = tooltip +  "ռ�ȱ仯(NULL)";
			}
			try {
				tooltip = tooltip + "MaxWeek=" + maxweek.toString();
			} catch (java.lang.IllegalArgumentException e ) {
				e.printStackTrace();
//				tooltip = tooltip +
			}
			try {
				tooltip = tooltip + "����ռ��" + decimalformate.format(dpzhanbi);
			} catch (java.lang.IllegalArgumentException e ) {
				tooltip = tooltip + "����ռ��NULL" ;
			}
			try {
				tooltip = tooltip + "DpMaxWk=" + dpzhanbimaxweek.toString();
			} catch (java.lang.IllegalArgumentException e ) {
				tooltip = tooltip + "DpMaxWk=NULL";
			}
			
			return tooltip;
		}
    	
		return "";
    }
    
    public void setDisplayNode (BkChanYeLianTreeNode curdisplayednode) 
    {
    	this.node = curdisplayednode;
    }
}



