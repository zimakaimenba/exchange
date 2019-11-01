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
import org.jfree.chart.annotations.CategoryPointerAnnotation;
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
import com.exchangeinfomanager.nodes.stocknodexdata.StockNodesXPeriodData;

public class BanKuaiFengXiCategoryBarChartCjeZhanbiPnl extends BanKuaiFengXiCategoryBarChartPnl
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public BanKuaiFengXiCategoryBarChartCjeZhanbiPnl() 
	{
		super ();
		super.plot.setRenderer(0,new CustomCategroyRendererForCjeZhanBi() );
	}
	
	
	private static Logger logger = Logger.getLogger(BanKuaiFengXiCategoryBarChartCjeZhanbiPnl.class);
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.bankuaifengxi.BarChartPanelDataChangedListener#updatedDate(com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode, java.time.LocalDate, int, java.lang.String)
	 * 升级占比数据
	 */
	public void updatedDate (TDXNodes node, LocalDate startdate, LocalDate enddate,String period)
	{
		super.setCurDisplayNode(node,period );
		
		preparingdisplayDataToGui (node,startdate,enddate,period);
	
//		//如有分析结果，ticklable显示红色
		CategoryLabelCustomizableCategoryAxis axis = (CategoryLabelCustomizableCategoryAxis)super.plot.getDomainAxis();
		axis.setDisplayNode(this.getCurDisplayedNode(),period);
		
	}
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
		if(super.shouldDisplayZhanBiInLine() ) {
			super.resetLineDate ();
			this.dipalyCjeCjlZBLineDataToGui ( super.getCurDisplayedNode().getNodeXPeroidData(period),period );
		}
		
		super.barchart.setNotify(true);
	}
	/*
	 * 
	 */
	private Double displayBarDataToGui(NodeXPeriodData nodexdata, LocalDate startdate, LocalDate enddate,
			String period) 
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

		double highestHigh =0.0; //设置显示范围
	
		LocalDate tmpdate = requirestart;
		do {
			LocalDate wkfriday = tmpdate.with(DayOfWeek.FRIDAY);
			//ZhanBi Part
			Double cjezb = nodexdata.getChenJiaoErZhanBi(wkfriday, 0);
			if(cjezb != null) {
				super.barchartdataset.setValue(cjezb,super.getRowKey(),wkfriday );

				if(cjezb > highestHigh)
					highestHigh = cjezb;
				
				//标记该NODE本周是阳线还是阴线
				Double zhangdiefu = nodexdata.getSpecificOHLCZhangDieFu(wkfriday, 0);
				if(zhangdiefu != null && zhangdiefu >0) {
					CategoryTextAnnotation cpa  = new CategoryTextAnnotation ("\u21B1", wkfriday , 0.0);
					//cpa.setBaseRadius(0.0);
					// cpa.setTipRadius(25.0);
					cpa.setFont(new Font("SansSerif", Font.BOLD, 10));
					cpa.setPaint(Color.RED);
					cpa.setTextAnchor(TextAnchor.CENTER);
					super.plot.addAnnotation(cpa);
				}
				
			} else {
				if( !dapan.isDaPanXiuShi(wkfriday,0,period) ) {
					super.barchartdataset.setValue(0.0,super.getRowKey(),wkfriday );
				} else { //大盘休市
					if(period.equals(NodeGivenPeriodDataItem.WEEK))
						tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
					else if(period.equals(NodeGivenPeriodDataItem.DAY))
						tmpdate = tmpdate.plus(1, ChronoUnit.DAYS) ;
					else if(period.equals(NodeGivenPeriodDataItem.MONTH))
						tmpdate = tmpdate.plus(1, ChronoUnit.MONTHS) ;
					
					continue;
				}
			}
			
			//对个股有关注记录的时候
			if(super.getCurDisplayedNode().getType() == BkChanYeLianTreeNode.TDXGG) { //对个股有关注记录的时候
					Integer gzjl = ((StockNodesXPeriodData)nodexdata).hasGzjlInPeriod(wkfriday, 0);
					if(gzjl != null) {
						double angle; Color paintcolor;String label;
						if(gzjl == 1) {
							angle = 10 * Math.PI/4;
							paintcolor = Color.BLUE;
							label = "jr";
						}	else {						
							angle = 10 * Math.PI/4;
							paintcolor = Color.BLUE;
							label = "yc";
						}
						CategoryPointerAnnotation cpa = new CategoryPointerAnnotation(label, wkfriday , cjezb, angle);
//						CategoryTextAnnotation cpa = new CategoryTextAnnotation (label, wkfriday , cjezb);
	//					cpa.setBaseRadius(0.0);
	//			        cpa.setTipRadius(25.0);
				        cpa.setFont(new Font("SansSerif", Font.BOLD, 10));
				        cpa.setPaint(paintcolor);
				        cpa.setTextAnchor(TextAnchor.CENTER);
						super.plot.addAnnotation(cpa);
					}
			}
			
			if(period.equals(NodeGivenPeriodDataItem.WEEK))
				tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
			else if(period.equals(NodeGivenPeriodDataItem.DAY))
				tmpdate = tmpdate.plus(1, ChronoUnit.DAYS) ;
			else if(period.equals(NodeGivenPeriodDataItem.MONTH))
				tmpdate = tmpdate.plus(1, ChronoUnit.MONTHS) ;
			
		} while (tmpdate.isBefore( requireend) || tmpdate.isEqual(requireend));
		
		xiuShiLeftRangeAxixAfterDispalyDate (nodexdata,startdate,enddate,highestHigh,period);
		
		return highestHigh;

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
		do {
			LocalDate wkfriday = tmpdate.with(DayOfWeek.FRIDAY);
			Double cjezb = nodexdata.getChenJiaoErZhanBi(wkfriday, 0);
			
			if(cjezb != null) {
				super.linechartdataset.setValue(cjezb,super.getRowKey(), wkfriday);
//				super.linechartdataset.setValue(cjezb,"nodezb", wkfriday);
				
				if(cjezb > highestHigh)
					highestHigh = cjezb;
			} else if( !dapan.isDaPanXiuShi(tmpdate,0,period) ) 
					super.linechartdataset.setValue(0.0,super.getRowKey(),wkfriday);

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
	Integer displayZhangDieTingLineDataToGui(NodeXPeriodData nodexdata, String period) 
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
				 
				if(zttj != null) { //确保右边坐标combined pnl显示同样的值
					super.linechartdataset.setValue(zttj, "ZhangTing", wkfriday );
					if (zttj > zdtmax)
						zdtmax = zttj ;
					if (dttj != null  && dttj > zdtmax)
						zdtmax = dttj;
				}	else if( !dapan.isDaPanXiuShi(wkfriday,0,period) )
						super.linechartdataset.setValue(0, "ZhangTing", wkfriday );
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
	Integer displayQueKouLineDataToGui(NodeXPeriodData nodexdata, String period) 
	{
		((BanKuaiFengXiCategoryBarRenderer)super.plot.getRenderer()).unhideBarMode();
		
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

		int qkmax =0;//设置显示范围
	
		LocalDate tmpdate = requirestart;
		do {
			LocalDate wkfriday = tmpdate.with(DayOfWeek.FRIDAY);
			
			//QueKou Line Part
			Integer opneupquekou = nodexdata.getQueKouTongJiOpenUp(wkfriday, 0);
			Integer opendownquekou = nodexdata.getQueKouTongJiOpenDown(wkfriday, 0);
			Integer huibuupquekou =  nodexdata.getQueKouTongJiHuiBuUp(wkfriday, 0);
			Integer huibudowquekou = nodexdata.getQueKouTongJiHuiBuDown(wkfriday, 0);	
			 
			if(opneupquekou != null) {
				super.linechartdataset.setValue(opneupquekou, "QueKouOpenUp", wkfriday );
				if (opneupquekou > qkmax)
					qkmax = opneupquekou ;
			}	else if( !dapan.isDaPanXiuShi(wkfriday,0,period) ) 
					super.linechartdataset.setValue(0, "QueKouOpenUp", wkfriday );
			
			if(huibudowquekou != null) {
				super.linechartdataset.setValue(huibudowquekou, "QueKouHuiBuDownQk", wkfriday );
				if (huibudowquekou > qkmax)
					qkmax = huibudowquekou ;
			}	else if( !dapan.isDaPanXiuShi(wkfriday,0,period) )
					super.linechartdataset.setValue(0, "QueKouHuiBuDownQk", wkfriday );
			
			if(opendownquekou != null && opendownquekou > qkmax) //确保右边坐标combined pnl显示同样的值
				qkmax = opendownquekou;
			if(huibuupquekou != null && huibuupquekou >qkmax)
				qkmax = huibuupquekou;

			
			
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
	
	/**
	 * 
	 * @param nodexdata
	 * @param rightrangeaxixmax
	 * @param forcetochange: 强制更新坐标，不管现在坐标的值
	 */
	 
	private void xiuShiRightRangeAxixAfterDispalyDate (NodeXPeriodData nodexdata,Double rightrangeaxixmax, Boolean forcetochange)
	{
		if(rightrangeaxixmax == 0)
			return;
		
		int row = super.linechartdataset.getRowCount();
		int column = super.linechartdataset.getColumnCount();
		
		Range rangeData = super.plot.getRangeAxis(3).getRange();
		if(forcetochange || row == 1) {
			
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
//		super.plot.getRenderer(3).setSeriesPaint(2, new Color(255,255,51) );
		super.plot.getRenderer(3).setSeriesPaint(2, Color.RED.darker() );
				
	}
	/**
	 * 
	 * @param nodexdata
	 * @param startdate
	 * @param enddate
	 * @param highestHigh
	 * @param period
	 */
	 
	private void xiuShiLeftRangeAxixAfterDispalyDate (NodeXPeriodData nodexdata,LocalDate startdate, LocalDate enddate, double highestHigh, String period)
	{
		if(highestHigh == 0.0)
			return;
		
		BanKuaiFengXiCategoryBarRenderer render = (BanKuaiFengXiCategoryBarRenderer)super.plot.getRenderer();
		render.setDisplayNode(this.getCurDisplayedNode());
		render.setDisplayNodeXPeriod (nodexdata);
		
		//如有分析结果，ticklable显示红色
		CategoryLabelCustomizableCategoryAxis axis = (CategoryLabelCustomizableCategoryAxis)super.plot.getDomainAxis(0);
		axis.setDisplayNode(this.getCurDisplayedNode(),period);
		
		try{
			super.plot.getRangeAxis(0).setRange(0, highestHigh*1.12);
		} catch (java.lang.IllegalArgumentException e) {
			e.printStackTrace();
			super.plot.getRangeAxis(0).setRange(0, 1);
			
		}
		
//		setPanelTitle ("成交额",enddate);
		super.decorateXaxisWithYearOrMonth("month".trim());
	}

	@Override
	public void hightLightFxValues(ExportCondition expc) 
	{
		Integer cjezbdporbkmax = expc.getSettinDpmaxwk();
		Integer cjezbdporbkmin = expc.getSettingDpminwk();
//		Double cjemin = expc.getSettingCjemin();
//		Double cjemax = expc.getSettingCjeMax();
//		Integer cjemaxwk = expc.getSettingCjemaxwk();
//		Double shoowhsl = expc.getSettingHsl();
		
		if(cjezbdporbkmax != null) {
			((BanKuaiFengXiCategoryBarRenderer)plot.getRenderer()).setDisplayMaxwkLevel (cjezbdporbkmax);
			this.barchart.fireChartChanged();//必须有这句
		}
		if(cjezbdporbkmin != null) {
			((BanKuaiFengXiCategoryBarRenderer)plot.getRenderer()).setDisplayMinwkLevel (cjezbdporbkmin);
			this.barchart.fireChartChanged();//必须有这句
		}
	}

	@Override
	public String getToolTipSelected() 
	{
		int indexx = barchartdataset.getColumnIndex(super.dateselected);
		
		BanKuaiFengXiCategoryBarToolTipGenerator ttpg = 	
		(CustomCategroyToolTipGeneratorForCjeZhanBi)(((CustomCategroyRendererForCjeZhanBi) plot.getRenderer()).getBaseToolTipGenerator());
		
		String tooltips = ttpg.generateToolTip(super.barchartdataset, 0, indexx);
		
		return tooltips;
	}

}
/*
 * 
 */
class CustomCategroyRendererForCjeZhanBi extends BanKuaiFengXiCategoryBarRenderer 
{
	private static final long serialVersionUID = 1L;
   
    public CustomCategroyRendererForCjeZhanBi () 
    {
        super();
        super.displayedmaxwklevel = 4;
        super.displayedminwklevel = 8;
        super.displayedcolumncolorindex = new Color(255,153,153);//Color.RED.darker();
        super.lastdisplayedcolumncolorindex = new Color(255,153,153);//Color.RED.darker();
        
        BanKuaiFengXiCategoryBarToolTipGenerator custotooltip = new CustomCategroyToolTipGeneratorForCjeZhanBi();
		this.setBaseToolTipGenerator(custotooltip);
		
//		DecimalFormat decimalformate = new DecimalFormat("%#0.000");
//		this.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}",decimalformate));

		
		BkfxItemLabelGenerator labelgenerator = new BkfxItemLabelGeneratorForCjeZhanBi ();
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

class BkfxItemLabelGeneratorForCjeZhanBi extends BkfxItemLabelGenerator 
{
	public BkfxItemLabelGeneratorForCjeZhanBi ()
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
    	
		Integer maxweek = nodexdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(selecteddate,0);
		Integer minweek = nodexdata.getChenJiaoErZhanBiMinWeekOfSuperBanKuai(selecteddate,0);
		
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

class CustomCategroyToolTipGeneratorForCjeZhanBi extends BanKuaiFengXiCategoryBarToolTipGenerator 
{
	public String generateToolTip(CategoryDataset dataset, int row, int column)  
    {
		String selected = dataset.getColumnKey(column).toString();
    	LocalDate selecteddate = CommonUtility.formateStringToDate(selected);
    			
		String html;
		DaPan dapan = (DaPan)  super.node.getRoot();
		if(super.node.getType() == BkChanYeLianTreeNode.TDXGG ) {
			 html =  nodexdata.getNodeXDataInHtml(dapan,selecteddate, 0);
		} else {
			html = nodexdata.getNodeXDataInHtml(dapan,selecteddate, 0);
		}
		
		return html;
    }
}