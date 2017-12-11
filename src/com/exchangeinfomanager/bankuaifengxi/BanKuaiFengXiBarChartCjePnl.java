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
		DecimalFormat decimalformate = new DecimalFormat("#0.000");
		((CustomRendererForCje) plot.getRenderer()).setItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}",decimalformate));
		cjerender.setItemLabelsVisible(true);
		
		CustomToolTipGeneratorForChenJiaoEr custotooltip = new CustomToolTipGeneratorForChenJiaoEr();
		cjerender.setSeriesToolTipGenerator(0,custotooltip);

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
        if(column == shouldcolumn)
            return Color.blue;
        else 
        	return Color.orange;
   }

//    public void setBarColumnShouldChangeColor (int column)
//    {
//    	this.shouldcolumn = column;
//    }

//    public void setDisplayNode (BkChanYeLianTreeNode curdisplayednode) 
//    {
//    	this.node = curdisplayednode;
//    }
    
}


class CustomToolTipGeneratorForChenJiaoEr implements CategoryToolTipGenerator  
{
//    private BkChanYeLianTreeNode node;

	public String generateToolTip(CategoryDataset dataset, int row, int column)  
    {
		String selected = dataset.getColumnKey(column).toString();
    	LocalDate selecteddate = CommonUtility.formateStringToDate(selected);
    	
    	Double curcje = (Double)dataset.getValue(row, column);
    	
    	DecimalFormat decimalformate = new DecimalFormat("#0.000");
    	return selecteddate + "�ɽ���" + decimalformate.format(curcje) ;
    }
    
//    public void setDisplayNode (BkChanYeLianTreeNode curdisplayednode) 
//    {
//    	this.node = curdisplayednode;
//    }

}
