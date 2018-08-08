package com.exchangeinfomanager.bankuaifengxi.CategoryBarOfName;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import com.exchangeinfomanager.asinglestockinfo.Stock.StockNodeXPeriodData;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartCjeZhanbiPnl;
import com.exchangeinfomanager.asinglestockinfo.Stock;



public class BanKuaiFengXiCategoryBarChartHuanShouLvPnl extends BanKuaiFengXiCategoryBarOfNameChartPnl
{

	public BanKuaiFengXiCategoryBarChartHuanShouLvPnl() 
	{
		super ();
	}
	
	private static Logger logger = Logger.getLogger(BanKuaiFengXiCategoryBarChartCjeZhanbiPnl.class);
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.bankuaifengxi.BarChartPanelDataChangedListener#updatedDate(com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode, java.time.LocalDate, int, java.lang.String)
	 * 升级占比数据
	 */
	public void updatedDate (List<Stock> stocks, List<Double> shizhirange,String shizhitype,LocalDate displayedenddate1,String period,String title)
	{
		displayDataToGui (stocks,shizhirange,shizhitype,displayedenddate1,period,title);
	}
	private void displayDataToGui (List<Stock> stocks, List<Double> shizhirange,String shizhitype,LocalDate lastselecteddate2,String period,String title)
	{
		barchartdataset.clear();
		super.barchart.setNotify(false);
		
		double highestHigh = 0.0; //设置显示范围
		
		for(int i=0;i<shizhirange.size();i++) {
			String rangeindex = "Level" + i;
			
			Double upshizhi = shizhirange.get(i) * 100000000;
			Double lowshizhi ;
			if(i == 0)
				lowshizhi = 0.0;
			else
				lowshizhi = shizhirange.get(i-1) * 100000000;
			
			for(Stock stock : stocks) {
				StockNodeXPeriodData nodexdata = (StockNodeXPeriodData)stock.getNodeXPeroidData(period);
				Double liutongshizhi = nodexdata.getSpecificTimeZongShiZhi(lastselecteddate2, 0);
				if(liutongshizhi < upshizhi  && liutongshizhi >= lowshizhi) {
					String stocklable = stock.getMyOwnCode() + stock.getMyOwnName();
					Double hsl = nodexdata.getSpecificTimeHuanShouLv(lastselecteddate2, 0);
					barchartdataset.setValue(hsl,rangeindex,stocklable );
					
					if(hsl > highestHigh)
						highestHigh = hsl;
				}
			}
		}

		super.barchart.setNotify(true);
		if(highestHigh != 0.0)
			super.plot.getRangeAxis().setRange(0, highestHigh*1.05);
		operationAfterDataSetup (title);
	}

}

//class CustomCategroyRendererForZhanBi extends BanKuaiFengXiCategoryBarRenderer 
//{
//	private static final long serialVersionUID = 1L;
//   
//    public CustomCategroyRendererForZhanBi() {
//        super();
//        super.displayedmaxwklevel = 4;
//    }
//
//	public Paint getItemPaint(final int row, final int column) 
//    {
//        if(column == shouldcolumn)
//            return Color.blue;
//        else   
//            return Color.RED.darker();
//   }
//    
//    public Paint getItemLabelPaint(final int row, final int column)
//    {
//    	CategoryPlot plot = getPlot ();
//    	CategoryDataset dataset = plot.getDataset();
//		String selected =  dataset.getColumnKey(column).toString();
//    	LocalDate selecteddate = CommonUtility.formateStringToDate(selected);
//
//    	Integer maxweek = nodexdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(selecteddate,0);
//
//		if(maxweek != null  && maxweek >= super.displayedmaxwklevel)
//			return Color.MAGENTA;
//		else 
//			return Color.black;
//    }
//    
//}
//
//class CustomCategroyToolTipGeneratorForZhanBi extends BanKuaiFengXiCategoryBarToolTipGenerator 
//{
//	public String generateToolTip(CategoryDataset dataset, int row, int column)  
//    {
//		String selected = dataset.getColumnKey(column).toString();
//    	LocalDate selecteddate = CommonUtility.formateStringToDate(selected);
//    	 
//		String tooltip = selected.toString() + " ";
//		
//		Double curzhanbidata = (Double)dataset.getValue(row, column);  //占比
//		if(curzhanbidata == null)
//			return null;
//
//			Integer maxweek = nodexdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(selecteddate,0);//nodefx.getGgbkzhanbimaxweek();
//			if(maxweek == null)
//				return null;
//			
//			DecimalFormat decimalformate = new DecimalFormat("%#0.000");
//			try {
//				tooltip = tooltip +  "占比" + decimalformate.format(curzhanbidata) ;
//			} catch (java.lang.IllegalArgumentException e ) {
//				tooltip = tooltip  +  "占比NULL";
//			}
////			try {
////				tooltip = tooltip +   "占比变化("	+ decimalformate.format(zhanbigrowthrate) +  ")";
////			} catch (java.lang.IllegalArgumentException e ) {
////				tooltip = tooltip  +  "占比变化(NULL)";
////			}
//			try {
//				tooltip = tooltip +  "占比MaxWk=" + maxweek.toString() ;
//			} catch (java.lang.IllegalArgumentException e ) {
//				tooltip = tooltip + "占比MaxWk=NULL";
//			}
//			
//			return tooltip;
//    }
//}