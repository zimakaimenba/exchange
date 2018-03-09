package com.exchangeinfomanager.bankuaifengxi;

import java.awt.Color;
import java.awt.Paint;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData;
import com.exchangeinfomanager.asinglestockinfo.ChenJiaoZhanBiInGivenPeriod;
import com.exchangeinfomanager.asinglestockinfo.DaPan;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;

public class BanKuaiFengXiBarChartGgBkZbPnl extends BanKuaiFengXiBarChartPnl
{

	public BanKuaiFengXiBarChartGgBkZbPnl() 
	{
		super ();
		super.plot.setRenderer(new CustomRendererForGgBkZhanBi() );
		((CustomRendererForGgBkZhanBi) plot.getRenderer()).setBarPainter(new StandardBarPainter());
	}
	
	private static Logger logger = Logger.getLogger(BanKuaiFengXiBarChartGgBkZbPnl.class);

	/*
	 * ��鰴�������ĳ���Ľ��׶�
	 */
	public void setBanKuaiCjlZhanBi (BanKuai node, LocalDate displayedenddate1,String period)
	{
		LocalDate requireend = displayedenddate1.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = displayedenddate1.with(DayOfWeek.MONDAY).minus(this.shoulddisplayedmonthnum,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		
		this.setBanKuaiCjlZhanBi (node,requirestart,requireend,period);
	}
	public void setBanKuaiCjlZhanBi (BanKuai node,LocalDate startdate,LocalDate enddate,String period)
	{
		this.curdisplayednode = node;

		BkChanYeLianTreeNode.NodeXPeriodData nodexdata = node.getNodeXPeroidData(period);
		displayDataToGui (nodexdata,startdate,enddate,period,"�ɽ���");
	}
	/*
	 * ��Ʊ���������ĳ���Ľ��׶�
	 */
	public void setStockCjlZhanBi (Stock node,BanKuai upnode, LocalDate displayedenddate1,String period)
	{
		LocalDate requireend = displayedenddate1.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = displayedenddate1.with(DayOfWeek.MONDAY).minus(this.shoulddisplayedmonthnum,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		
		this.setStockCjlZhanBi (node,upnode,requirestart,requireend,period);
	}
	public void setStockCjlZhanBi (Stock node,BanKuai upnode,LocalDate startdate,LocalDate enddate,String period)
	{
		this.curdisplayednode = node;
		String bkcode = upnode.getMyOwnCode();
		BkChanYeLianTreeNode.NodeXPeriodData nodexdata = node.getStockXPeriodDataForABanKuai(bkcode, period);
		displayDataToGui (nodexdata,startdate,enddate,period,"�ɽ���");
	}

	private void displayDataToGui (BkChanYeLianTreeNode.NodeXPeriodData nodexdata,LocalDate startdate,LocalDate enddate,String period,String indicater)
	{
		DaPan dapan = (DaPan)this.curdisplayednode.getRoot();
		
		LocalDate requireend = enddate.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = startdate.with(DayOfWeek.SATURDAY);
		
		barchartdataset = new DefaultCategoryDataset();
		double highestHigh =0.0; //������ʾ��Χ

		LocalDate tmpdate = requirestart;
		do  {
			ChenJiaoZhanBiInGivenPeriod tmprecord = nodexdata.getSpecficRecord(tmpdate,0);
			if(tmprecord != null) {
				Double ggbkratio;
				if(indicater.equals("�ɽ���") )
					 ggbkratio = tmprecord.getCjlZhanBi();
				else
					ggbkratio = tmprecord.getCjeZhanBi();
				
				LocalDate lastdayofweek = tmprecord.getRecordsDayofEndofWeek();
				barchartdataset.setValue(ggbkratio,"ռ��",lastdayofweek);
				
				if(ggbkratio > highestHigh)
					highestHigh = ggbkratio;
			} else {
				if( !dapan.isDaPanXiuShi(tmpdate,period) ) {
					barchartdataset.setValue(0.0,"ռ��",tmpdate);
//					datafx.addValue(0, "�������", tmpdate);
				} 
			}
			
			if(period.equals(ChenJiaoZhanBiInGivenPeriod.WEEK))
				tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
			else if(period.equals(ChenJiaoZhanBiInGivenPeriod.DAY))
				tmpdate = tmpdate.plus(1, ChronoUnit.DAYS) ;
			else if(period.equals(ChenJiaoZhanBiInGivenPeriod.MONTH))
				tmpdate = tmpdate.plus(1, ChronoUnit.MONTHS) ;
			
		} while (tmpdate.isBefore( requireend) || tmpdate.isEqual(requireend));
		
		setPanelTitle (indicater + "ռ��",requireend);
		
		operationAfterDataSetup (nodexdata,requirestart,requireend,highestHigh,period);
	}

	
	/*
	 * ������/���ɰ��ɽ��� ռ��
	 */
	/*
	 * ��鰴�������ĳ���Ľ��׶�
	 */
	public void setBanKuaiCjeZhanBi (BanKuai node,LocalDate displayedenddate1,String period)
	{
		LocalDate requireend = displayedenddate1.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = displayedenddate1.with(DayOfWeek.MONDAY).minus(this.shoulddisplayedmonthnum,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		
		this.setBanKuaiCjeZhanBi (node,requirestart,requireend,period);
	}
	public void setBanKuaiCjeZhanBi (BanKuai node,LocalDate startdate,LocalDate enddate,String period)
	{
		this.curdisplayednode = node;
//		String bkcode = upnode.getMyOwnCode();
		BkChanYeLianTreeNode.NodeXPeriodData nodexdata = node.getNodeXPeroidData(period);
		displayDataToGui (nodexdata,startdate,enddate,period,"�ɽ���");
	}
	/*
	 * ��Ʊ���������ĳ���Ľ��׶�
	 */
	public void setStockCjeZhanBi (Stock node,BkChanYeLianTreeNode upnode, LocalDate displayedenddate1,String period)
	{
		LocalDate requireend = displayedenddate1.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = displayedenddate1.with(DayOfWeek.MONDAY).minus(this.shoulddisplayedmonthnum,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
		
		this.setStockCjeZhanBi (node,upnode,requirestart,requireend,period);
	}
	public void setStockCjeZhanBi (Stock node,BkChanYeLianTreeNode upnode,LocalDate startdate,LocalDate enddate,String period)
	{
		this.curdisplayednode = node;
		String bkcode = upnode.getMyOwnCode();
		BkChanYeLianTreeNode.NodeXPeriodData nodexdata = node.getStockXPeriodDataForABanKuai(bkcode, period);
		displayDataToGui (nodexdata,startdate,enddate,period,"�ɽ���");
	}
	/*
	 * ��ʾ�ƶ�ʱ�����ڵ�����
	 */
//	public void setNodeCjeZhanBiByWeek (BkChanYeLianTreeNode node, LocalDate startdate,LocalDate enddate, DaPan dapan)
//	{
//		this.curdisplayednode = node;
//		LocalDate requireend = enddate.with(DayOfWeek.SATURDAY);;
//		LocalDate requirestart = startdate.with(DayOfWeek.SATURDAY);;
//		
//		barchartdataset = new DefaultCategoryDataset();
//		double highestHigh =0.0; //������ʾ��Χ
//		
//		for(LocalDate tmpdate = requirestart;tmpdate.isBefore( requireend) || tmpdate.isEqual(requireend); tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ){
//			ChenJiaoZhanBiInGivenPeriod tmprecord = node.getSpecficChenJiaoErRecord(tmpdate);
//			if(tmprecord != null) {
//				Double ggbkratio = tmprecord.getCjeZhanBi();
//				LocalDate lastdayofweek = tmprecord.getRecordsDayofEndofWeek();
//				barchartdataset.setValue(ggbkratio,"ռ��",lastdayofweek);
//				
//				if(ggbkratio > highestHigh)
//					highestHigh = ggbkratio;
//			} else {
//				if( !dapan.isThisWeekXiuShi(tmpdate) ) {
//					barchartdataset.setValue(0.0,"ռ��",tmpdate);
////					datafx.addValue(0, "�������", tmpdate);
//				} else //Ϊ��˵�������г�û�н���
//					continue;
//			}
//			
//		}
//		
//		setPanelTitle ("�ɽ���ռ��",enddate);
//		
//		operationAfterDataSetup (nodexdata,requirestart,requireend,highestHigh,period);
//	}

	/*
	 * ͻ����ʾһЩԤ������
	 */
	private void operationAfterDataSetup (BkChanYeLianTreeNode.NodeXPeriodData nodexdata,LocalDate startdate, LocalDate enddate, double highestHigh, String period) 
	{
		CustomRendererForGgBkZhanBi render = (CustomRendererForGgBkZhanBi)super.plot.getRenderer();
		render.setDisplayNode(this.curdisplayednode);
		render.setDateSet (barchartdataset);
		render.setDisplayNodeXPeriod (nodexdata);
		
		DecimalFormat decimalformate = new DecimalFormat("%#0.000");
		render.setItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}",decimalformate));
		render.setItemLabelsVisible(true);

		//
		if(this.curdisplayednode.getType() == 4) { //���
			CustomToolTipGeneratorForBkZhanBi custotooltip = new CustomToolTipGeneratorForBkZhanBi();
			custotooltip.setDisplayNode(curdisplayednode);
			render.setSeriesToolTipGenerator(0,custotooltip);
		} else if(this.curdisplayednode.getType() == 6) { //gegu
			CustomToolTipGeneratorForGgZhanBi custotooltip = new CustomToolTipGeneratorForGgZhanBi();
			custotooltip.setDisplayNode(curdisplayednode);
			render.setSeriesToolTipGenerator(0,custotooltip);
		}
		
		//���з��������ticklable��ʾ��ɫ
		CategoryLabelCustomizableCategoryAxis axis = (CategoryLabelCustomizableCategoryAxis)super.plot.getDomainAxis();
		axis.setDisplayNode(this.curdisplayednode,period);
		
		super.plot.getRangeAxis().setRange(0, highestHigh*1.12);
		
		super.plot.setDataset(barchartdataset);
		
//		super.setBarFenXiSingle();
		
	}

}

class CustomRendererForGgBkZhanBi extends BanKuaiFengXiBarRenderer 
{
	private static final long serialVersionUID = 1L;
   
    public CustomRendererForGgBkZhanBi() {
        super();
        super.displayedmaxwklevel = 4;
//        getItemLabelGenerator
    }

//    public void setDisplayNodeXPeriod(NodeXPeriodData nodexdata1) 
//    {
//		this.nodexdata = nodexdata1;
//	}

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
    	 
    	ChenJiaoZhanBiInGivenPeriod nodefx = nodexdata.getSpecficRecord(selecteddate, 0);
//    	if(node.getType() == 4)
//    		nodefx = ((BanKuai)node).getNodeFengXiResultForSpecificDate (selecteddate);
//    	else if(node.getType() == 6)
//    		nodefx = ((Stock)node).getNodeFengXiResultForSpecificDate (selecteddate);
    		
		Integer maxweek;
		if(nodefx != null)
			 maxweek = nodefx.getGgbkzhanbimaxweek();
		else 
			maxweek = 0;
		
		if(maxweek >= super.displayedmaxwklevel)
			return Color.MAGENTA;
		else 
			return Color.black;
    }
    
}

class CustomToolTipGeneratorForBkZhanBi extends BanKuaiFengXiBarToolTipGenerator 
{
//    private BkChanYeLianTreeNode node;
//	private NodeXPeriodData nodexdata;

	public String generateToolTip(CategoryDataset dataset, int row, int column)  
    {
		String selected = dataset.getColumnKey(column).toString();
    	LocalDate selecteddate = CommonUtility.formateStringToDate(selected);
    	 
    	ChenJiaoZhanBiInGivenPeriod nodefx = nodexdata.getSpecficRecord(selecteddate, 0);
//		if(node.getType() == 4 )
//			nodefx = ((BanKuai)node).getNodeFengXiResultForSpecificDate (selecteddate);
//		else if(node.getType() == 6 ) 
//			nodefx = ((Stock)node).getNodeFengXiResultForSpecificDate (selecteddate);
		
		if(nodefx == null)
			return "";
		
		String tooltip = selected.toString() + " ";
		if(node.getType() == 4 ) { //���
			Double curzhanbidata = (Double)dataset.getValue(row, column);  //ռ��
			Double zhanbigrowthrate = nodefx.getGgbkzhanbigrowthrate();
			Double cjezhanbi = nodefx.getGgbkcjegrowthzhanbi();
			Integer maxweek = nodefx.getGgbkzhanbimaxweek();
			
			DecimalFormat decimalformate = new DecimalFormat("%#0.000");
			try {
				tooltip = tooltip +  "ռ��" + decimalformate.format(curzhanbidata) ;
			} catch (java.lang.IllegalArgumentException e ) {
				tooltip = tooltip  +  "ռ��NULL";
			}
//			try {
//				tooltip = tooltip +   "ռ�ȱ仯("	+ decimalformate.format(zhanbigrowthrate) +  ")";
//			} catch (java.lang.IllegalArgumentException e ) {
//				tooltip = tooltip  +  "ռ�ȱ仯(NULL)";
//			}
			try {
				tooltip = tooltip +  "���MaxWk=" + maxweek.toString() ;
			} catch (java.lang.IllegalArgumentException e ) {
				tooltip = tooltip + "���MaxWk=NULL";
			}
			
			return tooltip;
		}
    	
		return "";
    }
    
//    public void setDisplayNode (BkChanYeLianTreeNode curdisplayednode) 
//    {
//    	this.node = curdisplayednode;
//    }
//    public void setDisplayNodeXPeriod(NodeXPeriodData nodexdata1) 
//    {
//		this.nodexdata = nodexdata1;
//	}
}


