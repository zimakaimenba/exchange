package com.exchangeinfomanager.bankuaifengxi;

import java.awt.Color;
import java.awt.Paint;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import javax.swing.border.TitledBorder;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.DaPan;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;

public class BanKuaiFengXiBarChartCjePnl extends BanKuaiFengXiBarChartPnl 
{

	public BanKuaiFengXiBarChartCjePnl() 
	{
		super ();
		super.plot.setRenderer(new CustomRendererForCje() );
		((CustomRendererForCje) plot.getRenderer()).setBarPainter(new StandardBarPainter());
	}
	

	/*
	 * ���/��Ʊ���ܽ��׶�
	 */
	public void setNodeJiaoYiErByWeek (BkChanYeLianTreeNode node,LocalDate displayedenddate1,DaPan dapan)
	{
		this.curdisplayednode = node;

		LocalDate requireend = displayedenddate1.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = displayedenddate1.with(DayOfWeek.MONDAY).minus(this.shoulddisplayedmonthnum,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		
//		this.displayedenddate = requireend; 
		
		barchartdataset = new DefaultCategoryDataset();
//		datafx = new DefaultCategoryDataset();
		
		for(LocalDate tmpdate = requirestart;tmpdate.isBefore( requireend) || tmpdate.isEqual(requireend); tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ){
			ChenJiaoZhanBiInGivenPeriod tmprecord = node.getSpecficChenJiaoErRecord(tmpdate);
			if(tmprecord != null) {
				Double chenjiaoer = tmprecord.getMyOwnChengJiaoEr();
				LocalDate lastdayofweek = tmprecord.getRecordsDayofEndofWeek();
				barchartdataset.setValue(chenjiaoer,"�ɽ���",lastdayofweek);
				
//				if(tmprecord.hasFengXiJieGuo ())
//					datafx.addValue(chenjiaoer/10, "�������", lastdayofweek);
//				else
//					datafx.addValue(0, "�������", lastdayofweek);
			} else {
				if( !dapan.isThisWeekXiuShi(tmpdate) ) {
					barchartdataset.setValue(0.0,"�ɽ���",tmpdate);
//					datafx.addValue(0, "�������", tmpdate);
				} else //Ϊ��˵�������г�û�н���
					continue;
			}
		}
		
		CustomRendererForCje cjerender = (CustomRendererForCje) super.plot.getRenderer(); 
		cjerender.setDisplayNode(node);
		cjerender.setDateSet (barchartdataset);
		DecimalFormat decimalformate = new DecimalFormat("#0.000");
		((CustomRendererForCje) plot.getRenderer()).setItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}",decimalformate));
		cjerender.setItemLabelsVisible(true);
		
		CustomToolTipGeneratorForChenJiaoEr custotooltip = new CustomToolTipGeneratorForChenJiaoEr();
		custotooltip.setDisplayNode(node);
		cjerender.setSeriesToolTipGenerator(0,custotooltip);
			
		CategoryLabelCustomizableCategoryAxis axis = (CategoryLabelCustomizableCategoryAxis)super.plot.getDomainAxis();
		axis.setDisplayNode(node);
		
		super.plot.setDataset(barchartdataset);
		
//		super.setBarFenXiSingle();
		
		setPanelTitle ("�ɽ���",displayedenddate1);
	}
}

class CustomRendererForCje extends BanKuaiFengXiBarRenderer 
{
	private static final long serialVersionUID = 1L;
//	private Paint[] colors;
//    private int shouldcolumn = -1;
//    private String barCharType;
//	private BkChanYeLianTreeNode node;
   
    public CustomRendererForCje() {
        super();
        
    }

    public Paint getItemPaint(final int row, final int column) 
    {
        if(column == shouldcolumn) {
          return Color.blue;
        }
        else 
        	return Color.orange;
   }
    public Paint getItemLabelPaint(final int row, final int column)
    {
		String selected = super.chartdataset.getColumnKey(column).toString();
    	LocalDate selecteddate = CommonUtility.formateStringToDate(selected);
    	 
    	ChenJiaoZhanBiInGivenPeriod nodefx = null;
    	if(node.getType() == 4)
    		nodefx = ((BanKuai)node).getNodeFengXiResultForSpecificDate (selecteddate);
    	else if(node.getType() == 6)
    		nodefx = ((Stock)node).getNodeFengXiResultForSpecificDate (selecteddate);
    		
		Integer maxweek;
		if(nodefx != null)
			 maxweek = nodefx.getGgbkcjemaxweek();
		else 
			maxweek = 0;
		
		if(maxweek >=7)
			return Color.red;
		else 
			return Color.black;
    }
    
    @Override
    public LegendItem getLegendItem(int dataset, int series) {
        LegendItem legendItem = super.getLegendItem(dataset, series);
        System.out.println(dataset + " " + series + " " + legendItem.getShape());
        // modify legendItem here
        return legendItem;
    }
    
}


class CustomToolTipGeneratorForChenJiaoEr implements CategoryToolTipGenerator  
{
    private BkChanYeLianTreeNode node;

	public String generateToolTip(CategoryDataset dataset, int row, int column)  
    {
		String selected = dataset.getColumnKey(column).toString();
    	LocalDate selecteddate = CommonUtility.formateStringToDate(selected);
    	
    	Double curcje = (Double)dataset.getValue(row, column);
    	
    	DecimalFormat decimalformate = new DecimalFormat("#0.000");
//    	return selecteddate + "�ɽ���" + decimalformate.format(curcje) ;
		
		//��ʾ�ɽ����Ƕ��������
//		String selected = dataset.getColumnKey(column).toString();
//    	LocalDate selecteddate = CommonUtility.formateStringToDate(selected);
    	 
    	ChenJiaoZhanBiInGivenPeriod nodefx = null;
		if(node.getType() == 4 )
			nodefx = ((BanKuai)node).getNodeFengXiResultForSpecificDate (selecteddate);
		else if(node.getType() == 6 ) 
			nodefx = ((Stock)node).getNodeFengXiResultForSpecificDate (selecteddate);
		
		if(nodefx == null)
			return "";
		
//		String tooltip = selected.toString();
		Integer maxwk = nodefx.getGgbkcjemaxweek();
		
		return selecteddate + "�ɽ���" + decimalformate.format(curcje) +  "CjeMaxWk=" + maxwk;
		
    }
    
    public void setDisplayNode (BkChanYeLianTreeNode curdisplayednode) 
    {
    	this.node = curdisplayednode;
    }

}
