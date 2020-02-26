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
import org.jfree.chart.LegendItem;
import org.jfree.chart.annotations.CategoryTextAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.TextAnchor;

import com.exchangeinfomanager.bankuaifengxi.BanKuaiGeGuMatchCondition;
import com.exchangeinfomanager.commonlib.CommonUtility;

import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.DaPan;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;

public class BanKuaiFengXiCategoryBarChartCjePnl extends BanKuaiFengXiCategoryBarChartPnl 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected DefaultCategoryDataset averagelinechartdataset;
	
	public BanKuaiFengXiCategoryBarChartCjePnl() 
	{
		super ();
		super.plot.setRenderer(0,new CustomRendererForCje() );
		
		averagelinechartdataset = new DefaultCategoryDataset();
		plot.setDataset(4, averagelinechartdataset);
	    BanKuaiFengXiCategoryLineRenderer lineqkrenderer = new BanKuaiFengXiCategoryLineRenderer ();
        plot.setRenderer(4, lineqkrenderer);
        ValueAxis rangeaxis = plot.getRangeAxis(0);
        plot.setRangeAxis(4, rangeaxis);
        super.plot.getRenderer(4).setSeriesPaint(1, Color.CYAN );
	}
	
	public void updatedDate (TDXNodes node, LocalDate startdate, LocalDate enddate,String period)
	{
		super.setCurDisplayNode( node,period );
		
		this.preparingdisplayDataToGui (node,startdate,enddate,period);
	}
	
	 private static Logger logger = Logger.getLogger(BanKuaiFengXiCategoryBarChartCjePnl.class);
	/*
	 * 
	 */
	private void preparingdisplayDataToGui (TDXNodes node,LocalDate startdate,LocalDate enddate,String period)
	{
		super.resetDate();
		super.barchart.setNotify(false);
		
		((BanKuaiFengXiCategoryBarRenderer)super.plot.getRenderer()).unhideBarMode();
		
		NodeXPeriodData nodexdata = node.getNodeXPeroidData(period);
		Double leftrangeaxix = displayBarDataToGui (nodexdata,startdate,enddate,period);
		
		if(super.shouldDrawQueKouLine()) {
			Integer qkmax = displayQueKouLineDataToGui(nodexdata,period);
		}
		if(super.shouldDrawZhangDieTingLine() ) {
			Integer zdt = displayZhangDieTingLineDataToGui(nodexdata,period);
		}
		if(super.shouldDrawAverageDailyCjeOfWeekLine() ) {
			Double avecje = displayAverageDailyCjeOfWeekLineDataToGui(nodexdata,period);
		}
		
		if(super.shouldDisplayZhanBiInLine() ) {
			super.resetLineDate ();
			this.dipalyCjeCjlZBLineDataToGui (super.getCurDisplayedNode().getNodeXPeroidData(period),period);
		}

		super.barchart.setNotify(true);
	}
	/*
	 * 
	 */
	public Double displayBarDataToGui (NodeXPeriodData nodexdata,LocalDate startdate,LocalDate enddate,String period) 
	{
		DaPan dapan;
		if(super.getCurDisplayedNode().getType() == BkChanYeLianTreeNode.BKGEGU) {
			BanKuai bk = ((StockOfBanKuai)super.getCurDisplayedNode() ).getBanKuai();
			dapan = (DaPan)bk.getRoot();
		} else
			dapan = (DaPan)(this.getCurDisplayedNode().getRoot());
		
		LocalDate requireend = enddate.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = startdate.with(DayOfWeek.SATURDAY);
		
		double highestHigh =0.0; //设置显示范围
		
		LocalDate tmpdate = requirestart;
		do  {
			//这里应该根据周期类型来选择日期类型，现在因为都是周线，就不细化了
			Double cje = nodexdata.getChengJiaoEr(tmpdate, 0);
			LocalDate wkfriday = tmpdate.with(DayOfWeek.FRIDAY);
			if(cje != null) {
				barchartdataset.setValue(cje,super.getRowKey(), wkfriday);
				
				if(cje > highestHigh)
					highestHigh = cje;
				
				//标记周日平均成交额
				if(!super.shouldDrawAverageDailyCjeOfWeekLine() ) { //如果已经要划线了，这里就不划了
					Double avecje = 2 * nodexdata.getAverageDailyChengJiaoErOfWeek(wkfriday, 0);
					averagelinechartdataset.setValue(avecje,"AverageDailyCje", wkfriday);
				}

				//标记大盘成交量该周是涨还是跌
				NodeXPeriodData dpnodexdata = dapan.getNodeXPeroidData(period);
				Double dpdiff = dpnodexdata.getChengJiaoErDifferenceWithLastPeriod(tmpdate, 0);
				if(dpdiff != null && dpdiff >0) {
					CategoryTextAnnotation cpa  = new CategoryTextAnnotation ("\u2B08", wkfriday , 0.0);
					//cpa.setBaseRadius(0.0);
					// cpa.setTipRadius(25.0);
					cpa.setFont(new Font("SansSerif", Font.BOLD, 10));
					cpa.setPaint(Color.RED);
					cpa.setTextAnchor(TextAnchor.CENTER);
					super.plot.addAnnotation(cpa);
				}
				
			} else {
				if( !dapan.isDaPanXiuShi(tmpdate,0,period) ) {
					barchartdataset.setValue(0.0,super.getRowKey(),wkfriday);
					averagelinechartdataset.setValue(0.0,"AverageDailyCje",wkfriday);
				}
			}

			if(period.equals(NodeGivenPeriodDataItem.WEEK))
				tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
			else if(period.equals(NodeGivenPeriodDataItem.DAY))
				tmpdate = tmpdate.plus(1, ChronoUnit.DAYS) ;
			else if(period.equals(NodeGivenPeriodDataItem.MONTH))
				tmpdate = tmpdate.plus(1, ChronoUnit.MONTHS) ;
		} while (tmpdate.isBefore( requireend) || tmpdate.isEqual(requireend));

//		super.barchart.setNotify(true);
		xiuShiLeftRangeAxixAfterDispalyDate (nodexdata,startdate,enddate,highestHigh,period);
		
		return highestHigh;
	}
	/*
	 * 
	 */
	public Integer displayZhangDieTingLineDataToGui(NodeXPeriodData nodexdata, String period) 
	{
		((BanKuaiFengXiCategoryBarRenderer)super.plot.getRenderer()).unhideBarMode();
		
		if(super.barchartdataset.getColumnCount() ==0 )
			return null;
		
		DaPan dapan;
		if(super.getCurDisplayedNode().getType() == BkChanYeLianTreeNode.BKGEGU) {
			BanKuai bk = ((StockOfBanKuai)super.getCurDisplayedNode() ).getBanKuai();
			dapan = (DaPan)bk.getRoot();
		} else
			dapan = (DaPan)(this.getCurDisplayedNode().getRoot());
		
		LocalDate indexrequirestart = (LocalDate) barchartdataset.getColumnKey(0);
		LocalDate indexrequireend = (LocalDate) barchartdataset.getColumnKey(barchartdataset.getColumnCount()-1);
		
		LocalDate requireend = indexrequireend.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = indexrequirestart.with(DayOfWeek.SATURDAY);
		
		int zdtmax = 0;//设置显示范围
		LocalDate tmpdate = requirestart; 
		do  {
			LocalDate wkfriday = tmpdate.with(DayOfWeek.FRIDAY);
			
			//缺口 Line Part
			if(super.getCurDisplayedNode().getType() != BkChanYeLianTreeNode.DAPAN) {
				Integer zttj = nodexdata.getZhangTingTongJi(tmpdate, 0);
				Integer dttj = nodexdata.getDieTingTongJi(tmpdate, 0);

				if(dttj != null) { //确保右边坐标combined pnl显示同样的值
					super.linechartdataset.setValue(dttj, "DieTing", wkfriday );
					if (dttj > zdtmax)
						zdtmax = dttj ;
					if (zttj != null  && zttj > zdtmax)
						zdtmax = zttj;
				}	else
						if( !dapan.isDaPanXiuShi(tmpdate,0,period) )
							super.linechartdataset.setValue(0, "DieTing", wkfriday );
			}
			
			
			if(period.equals(NodeGivenPeriodDataItem.WEEK))
				tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
			else if(period.equals(NodeGivenPeriodDataItem.DAY))
				tmpdate = tmpdate.plus(1, ChronoUnit.DAYS) ;
			else if(period.equals(NodeGivenPeriodDataItem.MONTH))
				tmpdate = tmpdate.plus(1, ChronoUnit.MONTHS) ;
			
		} while (tmpdate.isBefore( requireend) || tmpdate.isEqual(requireend));
		
		this.xiuShiRightRangeAxixAfterDispalyDate(nodexdata, Double.valueOf(zdtmax), false);
		return zdtmax;
		
	}
	/*
	 * 
	 */
	public Double dipalyCjeCjlZBLineDataToGui (NodeXPeriodData nodexdata,String period)
	{
		((BanKuaiFengXiCategoryBarRenderer)super.plot.getRenderer()).hideBarMode();
		
		if(super.barchartdataset.getColumnCount() ==0 )
			return null;
		
		DaPan dapan;
		if(super.getCurDisplayedNode().getType() == BkChanYeLianTreeNode.BKGEGU) {
			BanKuai bk = ((StockOfBanKuai)super.getCurDisplayedNode() ).getBanKuai();
			dapan = (DaPan)bk.getRoot();
		} else
			dapan = (DaPan)(this.getCurDisplayedNode().getRoot());
		
		LocalDate indexrequirestart = (LocalDate) barchartdataset.getColumnKey(0);
		LocalDate indexrequireend = (LocalDate) barchartdataset.getColumnKey(barchartdataset.getColumnCount()-1);
		
		LocalDate requireend = indexrequireend.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = indexrequirestart.with(DayOfWeek.SATURDAY);
		
		Double highestHigh = 0.0;//设置显示范围
		LocalDate tmpdate = requirestart; 
		do  {
			Double cje = nodexdata.getChengJiaoEr(tmpdate, 0);
			LocalDate wkfriday = tmpdate.with(DayOfWeek.FRIDAY);
			if(cje != null) {
				linechartdataset.setValue(cje,super.getRowKey(), wkfriday);
				
				if(cje > highestHigh)
					highestHigh = cje;
			} else {
				if( !dapan.isDaPanXiuShi(tmpdate,0,period) ) 
					linechartdataset.setValue(0.0,super.getRowKey(),wkfriday);
			}

			if(period.equals(NodeGivenPeriodDataItem.WEEK))
				tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
			else if(period.equals(NodeGivenPeriodDataItem.DAY))
				tmpdate = tmpdate.plus(1, ChronoUnit.DAYS) ;
			else if(period.equals(NodeGivenPeriodDataItem.MONTH))
				tmpdate = tmpdate.plus(1, ChronoUnit.MONTHS) ;
			
		} while (tmpdate.isBefore( requireend) || tmpdate.isEqual(requireend));
		
		this.xiuShiRightRangeAxixAfterDispalyDate(nodexdata, highestHigh, true);
		
		return highestHigh;
		
	}
	/*
	 * 
	 */
	public Double displayAverageDailyCjeOfWeekLineDataToGui (NodeXPeriodData nodexdata,String period)
	{
		((BanKuaiFengXiCategoryBarRenderer)super.plot.getRenderer()).unhideBarMode();
		
		DaPan dapan;
		if(super.getCurDisplayedNode().getType() == BkChanYeLianTreeNode.BKGEGU) {
			BanKuai bk = ((StockOfBanKuai)super.getCurDisplayedNode() ).getBanKuai();
			dapan = (DaPan)bk.getRoot();
		} else
			dapan = (DaPan)(this.getCurDisplayedNode().getRoot());
		
		if(super.barchartdataset.getColumnCount() ==0 )
			return null;
		
		LocalDate indexrequirestart = (LocalDate) barchartdataset.getColumnKey(0);
		LocalDate indexrequireend = (LocalDate) barchartdataset.getColumnKey(barchartdataset.getColumnCount()-1);
		
		LocalDate requireend = indexrequireend.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = indexrequirestart.with(DayOfWeek.SATURDAY);
		
		Double avecjeaxix = 0.0;
		LocalDate tmpdate = requirestart; 
		do  {
			LocalDate wkfriday = tmpdate.with(DayOfWeek.FRIDAY);
			
//			if(super.getCurDisplayedNode().getType() != BkChanYeLianTreeNode.DAPAN) {
				Double avecje = nodexdata.getAverageDailyChengJiaoErOfWeek(wkfriday, 0);
				
				if(avecje != null) {
					linechartdataset.setValue(avecje,"AverageDailyCje", wkfriday);
					
					if(avecje > avecjeaxix)
						avecjeaxix = avecje;
				} else {
					if( !dapan.isDaPanXiuShi(tmpdate,0,period) ) 
						linechartdataset.setValue(0.0,"AverageDailyCje",wkfriday);
				}
				
//			}
			
			if(period.equals(NodeGivenPeriodDataItem.WEEK))
				tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
			else if(period.equals(NodeGivenPeriodDataItem.DAY))
				tmpdate = tmpdate.plus(1, ChronoUnit.DAYS) ;
			else if(period.equals(NodeGivenPeriodDataItem.MONTH))
				tmpdate = tmpdate.plus(1, ChronoUnit.MONTHS) ;
			
		} while (tmpdate.isBefore( requireend) || tmpdate.isEqual(requireend));
		
		this.xiuShiRightRangeAxixAfterDispalyDate(nodexdata, avecjeaxix, true);
		
		return avecjeaxix;
	}
	/*
	 * 显示缺口数据
	 */
	public Integer displayQueKouLineDataToGui (NodeXPeriodData nodexdata,String period) 
	{
		((BanKuaiFengXiCategoryBarRenderer)super.plot.getRenderer()).unhideBarMode();
		
		DaPan dapan;
		if(super.getCurDisplayedNode().getType() == BkChanYeLianTreeNode.BKGEGU) {
			BanKuai bk = ((StockOfBanKuai)super.getCurDisplayedNode() ).getBanKuai();
			dapan = (DaPan)bk.getRoot();
		} else
			dapan = (DaPan)(this.getCurDisplayedNode().getRoot());
		
		if(super.barchartdataset.getColumnCount() == 0 )
			return null;
		
		LocalDate indexrequirestart = (LocalDate) barchartdataset.getColumnKey(0);
		LocalDate indexrequireend = (LocalDate) barchartdataset.getColumnKey(barchartdataset.getColumnCount()-1);
		
		LocalDate requireend = indexrequireend.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = indexrequirestart.with(DayOfWeek.SATURDAY);
		
		int qkmax = 0;//设置显示范围
		LocalDate tmpdate = requirestart; 
		do  {
			LocalDate wkfriday = tmpdate.with(DayOfWeek.FRIDAY);
			
			//缺口 Line Part
			if(super.getCurDisplayedNode().getType() != BkChanYeLianTreeNode.DAPAN) {
				Integer opneupquekou =  nodexdata.getQueKouTongJiOpenUp(wkfriday, 0);
				Integer opendownquekou =  nodexdata.getQueKouTongJiOpenDown(wkfriday, 0);
				Integer huibuupquekou = nodexdata.getQueKouTongJiHuiBuUp(wkfriday, 0);
				Integer huibudowquekou =  nodexdata.getQueKouTongJiHuiBuDown(wkfriday, 0);	
				 
				if(opendownquekou != null) {
					super.linechartdataset.setValue(opendownquekou, "QueKouOpenDown", wkfriday );
					if (opendownquekou > qkmax)
						qkmax = opendownquekou ;
				}	else
					if( !dapan.isDaPanXiuShi(wkfriday,0,period) )
						super.linechartdataset.setValue(0, "QueKouOpenDown", wkfriday );
				
				if(huibuupquekou != null) {
					super.linechartdataset.setValue(huibuupquekou, "QueKouHuiBuUpQk", wkfriday );
					if (huibuupquekou > qkmax)
						qkmax = huibuupquekou ;
				}	else
					if( !dapan.isDaPanXiuShi(tmpdate,0,period) )
						super.linechartdataset.setValue(0, "QueKouHuiBuUpQk", wkfriday );
				
				if(opneupquekou != null && opneupquekou > qkmax) //确保右边坐标combined pnl显示同样的值
					qkmax = opneupquekou;
				if(huibudowquekou != null && huibudowquekou >qkmax)
					qkmax = huibudowquekou;
			}
			
			
			if(period.equals(NodeGivenPeriodDataItem.WEEK))
				tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
			else if(period.equals(NodeGivenPeriodDataItem.DAY))
				tmpdate = tmpdate.plus(1, ChronoUnit.DAYS) ;
			else if(period.equals(NodeGivenPeriodDataItem.MONTH))
				tmpdate = tmpdate.plus(1, ChronoUnit.MONTHS) ;
			
		} while (tmpdate.isBefore( requireend) || tmpdate.isEqual(requireend));
		
		this.xiuShiRightRangeAxixAfterDispalyDate(nodexdata, Double.valueOf(qkmax), false);
		
		return qkmax;

	}
	/*
	 * 
	 */
	private void xiuShiLeftRangeAxixAfterDispalyDate (NodeXPeriodData nodexdata,LocalDate startdate, LocalDate enddate, double highestHigh, String period)
	{
		if(highestHigh == 0.0)
			return;
		
		CustomRendererForCje cjerender = (CustomRendererForCje) super.plot.getRenderer(0); 
		cjerender.setDisplayNode(this.getCurDisplayedNode() );
		cjerender.setDisplayNodeXPeriod (nodexdata);
			
		CategoryLabelCustomizableCategoryAxis axis = (CategoryLabelCustomizableCategoryAxis)super.plot.getDomainAxis(0);
		axis.setDisplayNode(this.getCurDisplayedNode(),period);
		
		try{
			super.plot.getRangeAxis(0).setRange(0, highestHigh*1.12);
		} catch(java.lang.IllegalArgumentException e) {
//			e.printStackTrace();
			super.plot.getRangeAxis(0).setRange(0, 1);
		}
		
		setPanelTitle ("成交额",enddate);
		super.decorateXaxisWithYearOrMonth("year".trim());
	}
	/**
	 * 
	 * @param nodexdata
	 * @param rightrangeaxixmax
	 * @param forcetochange
	 */
	private void xiuShiRightRangeAxixAfterDispalyDate (NodeXPeriodData nodexdata,Double rightrangeaxixmax, Boolean forcetochange)
	{
		if(rightrangeaxixmax == 0)
			return;
		
		int row = super.linechartdataset.getRowCount();
		int column = super.linechartdataset.getColumnCount();
		
		Range rangeData = super.plot.getRangeAxis(3).getRange();
		if(forcetochange || row == 1) { //row只有一个，那坐标完全为其占用，可以直接更新
			
			super.plot.getRangeAxis(3).setRange(0, rightrangeaxixmax*1.12);
		} else {
			double upbound = rangeData.getUpperBound();
			try{
				if(rightrangeaxixmax*1.12 > upbound)
					super.plot.getRangeAxis(3).setRange(0, rightrangeaxixmax*1.12);
				else if (upbound >= rightrangeaxixmax * 200)  //原来的坐标过大，会导致小坐标的线根本看不到，也是要更换坐标的。
					super.plot.getRangeAxis(3).setRange(0, rightrangeaxixmax*1.12);
				
			} catch(java.lang.IllegalArgumentException e) {
				super.plot.getRangeAxis(3).setRange(0, 1);
//				super.linechartdataset.clear();
//				e.printStackTrace();
				
			}
		}
		
		
		super.plot.getRenderer(3).setSeriesPaint(0, new Color(0,102,0) );
		super.plot.getRenderer(3).setSeriesPaint(1, new Color(102,255,102) );
		super.plot.getRenderer(3).setSeriesPaint(2, new Color(178,102,255) );
		super.plot.getRenderer(3).setSeriesPaint(3, new Color(178,102,255) );
		
	}
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartPnl#resetLineDate()
	 */
	public void resetLineDate ()
	{
		super.resetLineDate();
		if(averagelinechartdataset != null)
			averagelinechartdataset.clear();
		
		plot.getRangeAxis(3).setRange(0.0, 1.0);
	}
	public void resetDate ()
	{
		super.resetDate();
		
		if(averagelinechartdataset != null)
			averagelinechartdataset.clear();
	}

	/*
	 * 
	 */
	@Override
	public String getToolTipSelected ()
	{
		int indexx = barchartdataset.getColumnIndex(super.dateselected);
		
		CustomCategoryToolTipGeneratorForChenJiaoEr ttpg = 	
		(CustomCategoryToolTipGeneratorForChenJiaoEr)(((CustomRendererForCje) plot.getRenderer()).getBaseToolTipGenerator());
		
		String tooltips = ttpg.generateToolTip(super.barchartdataset, 0, indexx);
		
		return tooltips;
	}

	@Override
	public void BanKuaiGeGuMatchConditionValuesChanges(BanKuaiGeGuMatchCondition expc) 
	{
		Integer cjezbtoupleveldpmax = expc.getSettingDpMaxWk();
		Double cjemin = expc.getSettingChenJiaoErMin();
		Double cjemax = expc.getSettingChenJiaoErMax();
		Integer cjemaxwk = expc.getSettingChenJiaoErMaxWk();
		Double shoowhsl = expc.getSettingHuanShouLv();
	
		
		if(cjemaxwk != null) {
			((BanKuaiFengXiCategoryBarRenderer)plot.getRenderer()).setDisplayMaxwkLevel (cjemaxwk);
			this.barchart.fireChartChanged();//必须有这句
		}
		
	}

}

class CustomRendererForCje extends BanKuaiFengXiCategoryBarRenderer 
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(CustomRendererForCje.class);
   
    public CustomRendererForCje() {
        super();
        super.displayedmaxwklevel = 3;
        super.displayedcolumncolorindex = Color.ORANGE;
        super.lastdisplayedcolumncolorindex = Color.ORANGE;
        
		CustomCategoryToolTipGeneratorForChenJiaoEr custotooltip = new CustomCategoryToolTipGeneratorForChenJiaoEr();
		this.setBaseToolTipGenerator(custotooltip);
		
//		DecimalFormat decimalformate = new DecimalFormat(",###");//("#0.000");
//		this.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}",decimalformate));
		
		BkfxItemLabelGenerator labelgenerator = new BkfxItemLabelGeneratorForCje ();
		this.setBaseItemLabelGenerator(labelgenerator);
		this.setBaseItemLabelsVisible(true);
		
		labelgenerator.setDisplayedMaxWkLevel(super.displayedmaxwklevel);
    }

    public Paint getItemLabelPaint(final int row, final int column)
    {
    	CategoryPlot plot = getPlot ();
    	CategoryDataset dataset = plot.getDataset();
		String selected =  dataset.getColumnKey(column).toString();
		
    	LocalDate selecteddate = CommonUtility.formateStringToDate(selected);
    	 
		Integer maxweek = nodexdata.getAverageDailyChenJiaoErMaxWeekOfSuperBanKuai(selecteddate,0);
		
		if(maxweek != null && maxweek >= super.displayedmaxwklevel)
			return new Color(0,204,204);
		else 
			return Color.black;
    }
    
    @Override
    public LegendItem getLegendItem(int dataset, int series) 
    {
        LegendItem legendItem = super.getLegendItem(dataset, series);
        logger.debug(dataset + " " + series + " " + legendItem.getShape());
        // modify legendItem here
        return legendItem;
    }
    
}

class BkfxItemLabelGeneratorForCje extends BkfxItemLabelGenerator 
{
	public BkfxItemLabelGeneratorForCje ()
	{
		super (new DecimalFormat(",###"));
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

    	Integer maxweek;
    	try{
    		maxweek = nodexdata.getAverageDailyChenJiaoErMaxWeekOfSuperBanKuai(selecteddate,0);
    	} catch ( java.lang.NullPointerException e) {
    		return null;
    	}
		
		String result = "";
		if(maxweek != null && maxweek >= super.displayedmaxwklevel) {
			NumberFormat nf = this.getNumberFormat();
//			result =  nf.format(dataset.getValue(row, column));
			result =  maxweek.toString();
		} 
		
		return result;
	}

	@Override
	public String generateRowLabel(CategoryDataset arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}
	
}



class CustomCategoryToolTipGeneratorForChenJiaoEr extends BanKuaiFengXiCategoryBarToolTipGenerator  
{
	public String generateToolTip(CategoryDataset dataset, int row, int column)  
    {
		String selected = dataset.getColumnKey(column).toString();
    	LocalDate selecteddate = CommonUtility.formateStringToDate(selected);
//    	nodexdata = super.node.getNodeXPeroidData(period);
    			
		String html;
		DaPan dapan = (DaPan)  super.node.getRoot();
		if(super.node.getType() == BkChanYeLianTreeNode.TDXGG ) {
			 html = nodexdata.getNodeXDataInHtml(dapan,selecteddate, 0);
		} else {
			html = nodexdata.getNodeXDataInHtml(dapan,selecteddate, 0);
		}
		
		return html;
    }
	
	private Double formateCjeToShort(Double curcje) 
	{
		if(curcje >= 100000000) {
			curcje = curcje / 100000000;
    		
    	}  	else if(curcje >= 10000000 && curcje <100000000) {
    		curcje = curcje / 10000000;
    		
    	}  	else if(curcje >= 1000000 && curcje <10000000) {
    		curcje = curcje / 1000000;
    		
    	} else if(curcje >= 100000 && curcje <1000000) {
    		curcje = curcje / 100000;
    		
    	} else if(curcje >= 10000 && curcje <100000) {
    		curcje = curcje / 10000;
    		
    	}
		return curcje;
	}

	private String getNumberChineseDanWei (Double number) 
	{
		String danwei = "";
    	if(number >= 100000000) {
//    		number = number / 100000000;
    		danwei = "亿";
    	}  	else if(number >= 10000000 && number <100000000) {
//    		number = number / 10000000;
    		danwei = "千万";
    	}  	else if(number >= 1000000 && number <10000000) {
//    		number = number / 1000000;
    		danwei = "百万";
    	} else if(number >= 100000 && number <1000000) {
//    		number = number / 100000;
    		danwei = "十万";
    	} else if(number >= 10000 && number <100000) {
//    		number = number / 10000;
    		danwei = "万";
    	}
    	
    	return danwei;
		
	}
}