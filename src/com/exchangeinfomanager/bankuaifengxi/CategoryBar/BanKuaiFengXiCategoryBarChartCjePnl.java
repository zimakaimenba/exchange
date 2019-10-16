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
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.ui.TextAnchor;

import com.exchangeinfomanager.bankuaifengxi.ExportCondition;

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
	public BanKuaiFengXiCategoryBarChartCjePnl() 
	{
		super ();
		super.plot.setRenderer(0,new CustomRendererForCje() );
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
				if( !dapan.isDaPanXiuShi(tmpdate,0,period) ) 
					barchartdataset.setValue(0.0,super.getRowKey(),wkfriday);
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
			
			if(super.getCurDisplayedNode().getType() != BkChanYeLianTreeNode.DAPAN) {
				Double avecje = nodexdata.getAverageDailyChengJiaoErOfWeek(wkfriday, 0);
				
				if(avecje != null) {
					linechartdataset.setValue(avecje,"AverageDailyCje", wkfriday);
					
					if(avecje > avecjeaxix)
						avecjeaxix = avecje;
				} else {
					if( !dapan.isDaPanXiuShi(tmpdate,0,period) ) 
						linechartdataset.setValue(0.0,"AverageDailyCje",wkfriday);
				}
				
			}
			
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
	 * 
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
		
		if(super.barchartdataset.getColumnCount() ==0 )
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
		super.plot.getRenderer(3).setSeriesPaint(2, new Color(204,0,0) );
		super.plot.getRenderer(3).setSeriesPaint(3, new Color(204,0,0) );
		
	}

	/*
	 * 
	 */
	@Override
	public void hightLightFxValues(ExportCondition expc) {
//		Integer cjezbtoupleveldpmax = expc.getSettinDpmaxwk();
//		Double cjemin = expc.getSettingCjemin();
//		Double cjemax = expc.getSettingCjeMax();
		Integer cjemaxwk = expc.getSettingCjemaxwk();
//		Double shoowhsl = expc.getSettingHsl();
		
		if(cjemaxwk != null) {
			((BanKuaiFengXiCategoryBarRenderer)plot.getRenderer()).setDisplayMaxwkLevel (cjemaxwk);
			this.barchart.fireChartChanged();//必须有这句
		}
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

}

class CustomRendererForCje extends BanKuaiFengXiCategoryBarRenderer 
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(CustomRendererForCje.class);
   
    public CustomRendererForCje() {
        super();
        super.displayedmaxwklevel = 7;
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
    	 
		Integer maxweek = nodexdata.getChenJiaoErMaxWeekOfSuperBanKuai(selecteddate,0);
		
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
    		maxweek = nodexdata.getChenJiaoErMaxWeekOfSuperBanKuai(selecteddate,0);
    	} catch ( java.lang.NullPointerException e) {
    		return null;
    	}
		
		String result = "";
		if(maxweek != null && maxweek >= super.displayedmaxwklevel) {
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
		
//		String selected = dataset.getColumnKey(column).toString();
//    	LocalDate selecteddate = CommonUtility.formateStringToDate(selected);
//    	
//    	Double curcje = (Double)dataset.getValue(row, column);
//    	String cjedanwei = getNumberChineseDanWei(curcje);
//    	curcje = formateCjeToShort (curcje);
////    	if(curcje >= 100000000) {
////    		curcje = curcje / 100000000;
////    		danwei = "亿";
////    	}  	else if(curcje >= 10000000 && curcje <100000000) {
////    		curcje = curcje / 10000000;
////    		danwei = "千万";
////    	}  	else if(curcje >= 1000000 && curcje <10000000) {
////    		curcje = curcje / 1000000;
////    		danwei = "百万";
////    	}
//    		
//    	DecimalFormat decimalformate = new DecimalFormat("#0.000"); //",###";
//    	NumberFormat percentFormat = NumberFormat.getPercentInstance(Locale.CHINA);
//    	percentFormat.setMinimumFractionDigits(4);
//		
//    	Integer maxwk;
//    	try{
//    		maxwk = nodexdata.getChenJiaoErMaxWeekOfSuperBanKuai(selecteddate,0);//显示成交额是多少周最大
//    	} catch (java.lang.NullPointerException e) {
//    		return null;
//    	}
//		DaPan dapan = (DaPan)  super.node.getRoot();
//		Double changerate = nodexdata.getChenJiaoErChangeGrowthRateOfSuperBanKuai(dapan,selecteddate,0);//成交额大盘变化贡献率
//		
//		Double liutongshizhi = null; String liutongshizhidanwei = null;
//		if(super.node.getType() == BkChanYeLianTreeNode.TDXGG) {
//			liutongshizhi = ((StockNodeXPeriodData)nodexdata).getSpecificTimeLiuTongShiZhi(selecteddate, 0);
//			if(liutongshizhi == null) {
//				liutongshizhidanwei = "";
//			} else {
//				liutongshizhidanwei = getNumberChineseDanWei(liutongshizhi);
//				liutongshizhi = formateCjeToShort (liutongshizhi);
//			}
//		}
//		
//		String htmlstring = "";
//		org.jsoup.nodes.Document doc = Jsoup.parse(htmlstring);
//		Elements body = doc.getElementsByTag("body");
//		for(Element elbody : body) {
//			 org.jsoup.nodes.Element dl = elbody.appendElement("dl");
//			 
//			 org.jsoup.nodes.Element li3 = dl.appendElement("li");
//			 li3.appendText(selecteddate.toString());
//			 
//			 org.jsoup.nodes.Element li = dl.appendElement("li");
//			 li.appendText("成交额" + decimalformate.format(curcje) + cjedanwei);
//			 
//			 org.jsoup.nodes.Element li2 = dl.appendElement("li");
//			 li2.appendText("成交额MaxWk=" + maxwk);
//			 
//			 
//			 try{
//				 htmlstring = "成交额大盘变化贡献率" + percentFormat.format (changerate) ;
//				 
//				 org.jsoup.nodes.Element li4 = dl.appendElement("li");
//				 li4.appendText(htmlstring);
//			 } catch (java.lang.IllegalArgumentException e) {
////				 li4.appendText("成交额大盘变化贡献率NULL" );
//			 }
//			 
//			 if(super.node.getType() == BkChanYeLianTreeNode.TDXGG) {
//				 
//				 try{
//					 htmlstring = "流通周平均市值" + decimalformate.format(liutongshizhi) + liutongshizhidanwei ;
//					 
//					 org.jsoup.nodes.Element li5 = dl.appendElement("li");
//					 li5.appendText(htmlstring);
//				 } catch (java.lang.IllegalArgumentException e) {
////					 li5.appendText("流通周平均市值=NULL");
//				 }
//			 }
//			 
//			 if(super.node.getType() == BkChanYeLianTreeNode.TDXGG || super.node.getType() == BkChanYeLianTreeNode.TDXBK ) {
//				 Integer opneupquekou = ( (TDXNodesXPeriodData) nodexdata).getQueKouTongJiOpenUp(selecteddate, 0);
//				 if( opneupquekou != null) {
//					 org.jsoup.nodes.Element li6 = dl.appendElement("li");
//					 li6.appendText("缺口OpenUp =" + opneupquekou);
//				 }
//				 Integer opendownquekou = ( (TDXNodesXPeriodData) nodexdata).getQueKouTongJiOpenDown(selecteddate, 0);
//				 if( opendownquekou != null) {
//					 org.jsoup.nodes.Element li7 = dl.appendElement("li");
//					 li7.appendText("缺口OpenDown =" + opendownquekou);
//				 }
//				 Integer huibuupquekou = ( (TDXNodesXPeriodData) nodexdata).getQueKouTongJiHuiBuUp(selecteddate, 0);
//				 if( huibuupquekou != null) {
//					 org.jsoup.nodes.Element li8 = dl.appendElement("li");
//					 li8.appendText("缺口HuiBuUp =" + huibuupquekou );
//				 }
//				 Integer huibudowquekou = ( (TDXNodesXPeriodData) nodexdata).getQueKouTongJiHuiBuDown(selecteddate, 0);
//				 if( huibudowquekou != null) {
//					 org.jsoup.nodes.Element li9 = dl.appendElement("li");
//					 li9.appendText("缺口HuiBuDown =" + huibudowquekou );
//				 }
//			 }
//			 
//			 
//		}
//		
//		htmlstring = doc.toString();
//		return htmlstring;
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