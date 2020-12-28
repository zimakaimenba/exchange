package com.exchangeinfomanager.bankuaifengxi.CategoryBar;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import javax.swing.JMenuItem;

import org.apache.log4j.Logger;
import org.jfree.chart.LegendItem;
import org.jfree.chart.annotations.CategoryTextAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
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

public class BanKuaiFengXiCategoryBarChartCjlPnl extends BanKuaiFengXiCategoryBarChartPnl 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TDXNodes shouldDisplayBarOfSuperBanKuaiCjeInsteadOfSelfCje;
	private JMenuItem mntmAveDailyCjlLineData;
	private JMenuItem mntmCompareAveCjlWithSpecificNode;
	private Boolean displayaveragedailycjl = false;
	
	public BanKuaiFengXiCategoryBarChartCjlPnl() 
	{
		super ("CJL");
		super.plot.setRenderer(0,new CustomRendererForCjl() );
		super.plot.setRenderer(3, new BanKuaiFengXiCategoryLineRenderer ());
	        
		averagelinechartdataset = new DefaultCategoryDataset();
		super.plot.setDataset(4, averagelinechartdataset);
		super.plot.setRenderer(4, new BanKuaiFengXiCategoryLineRenderer () );
	    ValueAxis rangeaxis = plot.getRangeAxis(0);
	    super.plot.setRangeAxis(4, rangeaxis);
	    super.plot.getRenderer(4).setSeriesPaint(1, Color.CYAN );
	    
	    createGuiAndEvents ();
	}
	/*
	 * 
	 */
	private void createGuiAndEvents ()
	{
        mntmAveDailyCjlLineData = new JMenuItem("突出周日平均成交量");
		chartPanel.getPopupMenu().add(mntmAveDailyCjlLineData);
		mntmCompareAveCjlWithSpecificNode = new JMenuItem("对比大盘周日平均成交量");
		chartPanel.getPopupMenu().add(mntmCompareAveCjlWithSpecificNode);
		
	   	mntmAveDailyCjlLineData.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent evt) {
					
					PropertyChangeEvent evtzd = new PropertyChangeEvent(this, BanKuaiFengXiCategoryBarChartPnl.AVERAGEDAILYCJL, getCurDisplayedNode().getMyOwnCode(), "averagedailycjeline" );
		            pcs.firePropertyChange(evtzd);
				}
				
			});
	   	mntmCompareAveCjlWithSpecificNode.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent evt) {
					PropertyChangeEvent evtzd = new PropertyChangeEvent(this, BanKuaiFengXiCategoryBarChartPnl.COMPAREAVERAGEDAILYCJLWITHDAPAN, getCurDisplayedNode().getMyOwnCode(), "compareaveragedailycjewithdapan" );
		            pcs.firePropertyChange(evtzd);
				}
				
			});
		
	}
	/*
	 * 
	 */
	public TDXNodes getSettingSpecificSuperBanKuai ()
	{
		return shouldDisplayBarOfSuperBanKuaiCjeInsteadOfSelfCje;
	}
	
	public void updatedDate (TDXNodes node, LocalDate startdate, LocalDate enddate,String period)
	{
		super.setCurDisplayNode( node,startdate, enddate, period );
		
		if(node.getType() == BkChanYeLianTreeNode.DAPAN) {
//	        mntmCjeCjlZblineDate.setEnabled(false);
//	        mntmClearLineData.setEnabled(false);
	        mntmCompareAveCjlWithSpecificNode.setEnabled(false);
		}
		
		this.preparingdisplayDataToGui (node,startdate,enddate,period);
	}
	
	 private static Logger logger = Logger.getLogger(BanKuaiFengXiCategoryBarChartCjePnl.class);
	 private void preparingdisplayDataToGui (TDXNodes node,LocalDate startdate,LocalDate enddate,String period)
	 {
			this.resetDate();
			super.barchart.setNotify(false);
			
			((BanKuaiFengXiCategoryBarRenderer)super.plot.getRenderer()).unhideBarMode();
			super.plot.getRangeAxis(0).setRange(0, 1);//重置坐标，否则会出现问题
			
			NodeXPeriodData nodexdata = node.getNodeXPeroidData(period);
			
			 
			if(shouldDisplayBarOfSuperBanKuaiCjeInsteadOfSelfCje == null) { //显示node自己的成交额，个股成交额和平均成交额差别不会太大，可以用同一个坐标系显示
				Double leftrangeaxix = displayBarDataToGui (nodexdata,startdate,enddate,period);
//				Double avecje = displayAverageDailyCjeOfWeekLineDataToGuiUsingLeftAxis(nodexdata,startdate,enddate,period); //显示node的周线平均成交额的线形数据
				
				if(this.shouldDrawAverageDailyCjlOfWeekLine() ) {
					((BanKuaiFengXiCategoryBarRenderer)super.plot.getRenderer()).unhideBarMode();
					 Double avecje2 = displayAverageDailyCjlOfWeekLineDataToGuiUsingLeftAxis(nodexdata,startdate,enddate,period);
				}
				
			} else { //显示大盘的平均成交额，大盘的平均成交额比个股平均成交额大很多，所以用独立的成交量坐标系，
				NodeXPeriodData nodexdataOfSuperBk = shouldDisplayBarOfSuperBanKuaiCjeInsteadOfSelfCje.getNodeXPeroidData(period);
				Double leftrangeaxix = displayAverageBarDataToGui (nodexdata,startdate,enddate,period);
//				Double avecje2 = displayAverageDailyCjeOfWeekLineDataToGuiUsingLeftAxis(nodexdataOfSuperBk,startdate,enddate,period);
				Double avecjeaxix = displayAverageDailyCjlOfWeekLineDataToGuiUsingRightAxix(nodexdataOfSuperBk ,startdate,enddate,period);
				((BanKuaiFengXiCategoryBarRenderer)super.plot.getRenderer()).setBarDisplayedColor( "CjlAverageColumnColor" );
			}
			
//			if(super.shouldDisplayZhanBiInLine() ) {
//				this.resetLineDate ();
//				this.dipalyCjeCjlZBLineDataToGui (nodexdata,startdate,enddate,period);
//			}

			super.barchart.setNotify(true);
	 }
	 /*
		 * 
		 */
		public Double displayBarDataToGui (NodeXPeriodData nodexdata,LocalDate startdate,LocalDate enddate,String period) 
		{
			((BanKuaiFengXiCategoryBarRenderer)super.plot.getRenderer()).unhideBarMode();
			
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
				Double cjl = nodexdata.getChengJiaoLiang(tmpdate, 0);
				LocalDate wkfriday = tmpdate.with(DayOfWeek.FRIDAY);
				if(cjl != null) {
					barchartdataset.setValue(cjl,super.getRowKey(), wkfriday);
					
					if(cjl > highestHigh)
						highestHigh = cjl;
					
					//标记周日平均成交额
//					if(!super.shouldDrawAverageDailyCjeOfWeekLine() ) { //如果已经要划线了，这里就不划了
//						Double avecje = 2 * nodexdata.getAverageDailyChengJiaoErOfWeek(wkfriday, 0);
//						this.averagelinechartdataset.setValue(avecje,"AverageDailyCje", wkfriday);
//					}l

					Double zhangdiefu = nodexdata.getSpecificOHLCZhangDieFu(tmpdate, 0);
					if(zhangdiefu != null && zhangdiefu >0) {
						CategoryTextAnnotation cpa  = new CategoryTextAnnotation ("\u2B08", wkfriday , 0.0);
						//cpa.setBaseRadius(0.0);
						// cpa.setTipRadius(25.0);
						cpa.setFont(new Font("SansSerif", Font.BOLD, 10));
						cpa.setPaint(Color.RED);
						cpa.setTextAnchor(TextAnchor.CENTER);
						super.plot.addAnnotation(cpa);
					} else {
						CategoryTextAnnotation cpa  = new CategoryTextAnnotation ("\u21B1", wkfriday , 0.0);
						//cpa.setBaseRadius(0.0);
						//cpa.setTipRadius(25.0);
						cpa.setFont(new Font("SansSerif", Font.BOLD, 10));
						cpa.setPaint(Color.GREEN);
						cpa.setTextAnchor(TextAnchor.CENTER);
						super.plot.addAnnotation(cpa);
					}
					//标记大盘成交量该周是涨还是跌;;
//					NodeXPeriodData dpnodexdata = dapan.getNodeXPeroidData(period);
//					Double dpdiff = dpnodexdata.getChengJiaoErDifferenceWithLastPeriod(tmpdate, 0);
//					if(dpdiff != null && dpdiff >0) {
//						CategoryTextAnnotation cpa  = new CategoryTextAnnotation ("\u2B08", wkfriday , 0.0);
//						//cpa.setBaseRadius(0.0);
//						// cpa.setTipRadius(25.0);
//						cpa.setFont(new Font("SansSerif", Font.BOLD, 10));
//						cpa.setPaint(Color.RED);
//						cpa.setTextAnchor(TextAnchor.CENTER);
//						super.plot.addAnnotation(cpa);
//					} else {
//						CategoryTextAnnotation cpa  = new CategoryTextAnnotation ("\u21B1", wkfriday , 0.0);
//						//cpa.setBaseRadius(0.0);
//						//cpa.setTipRadius(25.0);
//						cpa.setFont(new Font("SansSerif", Font.BOLD, 10));
//						cpa.setPaint(Color.GREEN);
//						cpa.setTextAnchor(TextAnchor.CENTER);
//						super.plot.addAnnotation(cpa);
//					}
					
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

//			super.barchart.setNotify(true);
			xiuShiLeftRangeAxixAfterDispalyDate (nodexdata,startdate,enddate,highestHigh,period);
			
			return highestHigh;
		}
		/*
		 * 
		 */
		public Double displayAverageDailyCjlOfWeekLineDataToGuiUsingLeftAxis (NodeXPeriodData nodexdata,LocalDate startdate, LocalDate enddate, String period)
		{
			DaPan dapan;
			if(super.getCurDisplayedNode().getType() == BkChanYeLianTreeNode.BKGEGU) {
				BanKuai bk = ((StockOfBanKuai)super.getCurDisplayedNode() ).getBanKuai();
				dapan = (DaPan)bk.getRoot();
			} else
				dapan = (DaPan)(this.getCurDisplayedNode().getRoot());
			
//			if(super.barchartdataset.getColumnCount() ==0 )
//				return null;
			
			LocalDate indexrequirestart = startdate;
			LocalDate indexrequireend = enddate;
			
			LocalDate requireend = indexrequireend.with(DayOfWeek.SATURDAY);
			LocalDate requirestart = indexrequirestart.with(DayOfWeek.SATURDAY);
			
			Double avecjeaxix = 0.0;
			LocalDate tmpdate = requirestart; 
			do  {
				LocalDate wkfriday = tmpdate.with(DayOfWeek.FRIDAY);
				
//				if(super.getCurDisplayedNode().getType() != BkChanYeLianTreeNode.DAPAN) {
					Double avecjl =  nodexdata.getAverageDailyChengJiaoLiangOfWeek(wkfriday, 0);
					
					if(avecjl != null) {
						averagelinechartdataset.setValue( 5* avecjl,"AverageDailyCje", wkfriday);
						
						if(5* avecjl > avecjeaxix)
							avecjeaxix = 5*avecjl;
					} else {
						if( !dapan.isDaPanXiuShi(tmpdate,0,period) ) 
							averagelinechartdataset.setValue(0.0,"AverageDailyCje",wkfriday);
					}
					
//				}
				
				if(period.equals(NodeGivenPeriodDataItem.WEEK))
					tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
				else if(period.equals(NodeGivenPeriodDataItem.DAY))
					tmpdate = tmpdate.plus(1, ChronoUnit.DAYS) ;
				else if(period.equals(NodeGivenPeriodDataItem.MONTH))
					tmpdate = tmpdate.plus(1, ChronoUnit.MONTHS) ;
				
			} while (tmpdate.isBefore( requireend) || tmpdate.isEqual(requireend));
			
//			this.xiuShiRightRangeAxixAfterDispalyDate(nodexdata, avecjeaxix, true);
			xiuShiLeftRangeAxixAfterDispalyDate (nodexdata,startdate,enddate,avecjeaxix,period);
			
			return avecjeaxix;
		}
		/*
		 *显示的是周日平均数据 
		 */
		public Double displayAverageBarDataToGui(NodeXPeriodData nodexdata,LocalDate startdate,LocalDate enddate,String period)
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
				Double cjl = nodexdata.getAverageDailyChengJiaoLiangOfWeek(tmpdate, 0);
				LocalDate wkfriday = tmpdate.with(DayOfWeek.FRIDAY);
				if(cjl != null) {
					barchartdataset.setValue( cjl,super.getRowKey(), wkfriday);
					
					if(cjl > highestHigh)
						highestHigh = cjl;
					
					Double zhangdiefu = nodexdata.getSpecificOHLCZhangDieFu(tmpdate, 0);
					if(zhangdiefu != null && zhangdiefu >0) {
						CategoryTextAnnotation cpa  = new CategoryTextAnnotation ("\u21B1", wkfriday , 0.0);
						//cpa.setBaseRadius(0.0);
						//cpa.setTipRadius(25.0);
						cpa.setFont(new Font("SansSerif", Font.BOLD, 10));
						cpa.setPaint(Color.RED);
						cpa.setTextAnchor(TextAnchor.CENTER);
						super.plot.addAnnotation(cpa);
					} else {
						CategoryTextAnnotation cpa  = new CategoryTextAnnotation ("\u21B1", wkfriday , 0.0);
						//cpa.setBaseRadius(0.0);
						//cpa.setTipRadius(25.0);
						cpa.setFont(new Font("SansSerif", Font.BOLD, 10));
						cpa.setPaint(Color.GREEN);
						cpa.setTextAnchor(TextAnchor.CENTER);
						super.plot.addAnnotation(cpa);
					}
					//标记大盘成交量该周是涨还是跌
//					NodeXPeriodData dpnodexdata = dapan.getNodeXPeroidData(period);
//					Double dpdiff = dpnodexdata.getChengJiaoErDifferenceWithLastPeriod(tmpdate, 0);
//					if(dpdiff != null && dpdiff >0) {
//						CategoryTextAnnotation cpa  = new CategoryTextAnnotation ("\u2B08", wkfriday , 0.0);
//						//cpa.setBaseRadius(0.0);
//						//cpa.setTipRadius(25.0);
//						cpa.setFont(new Font("SansSerif", Font.BOLD, 10));
//						cpa.setPaint(Color.RED);
//						cpa.setTextAnchor(TextAnchor.CENTER);
//						super.plot.addAnnotation(cpa);
//					}
					
				} else {
					if( !dapan.isDaPanXiuShi(tmpdate,0,period) ) {
						barchartdataset.setValue(0.0,super.getRowKey(),wkfriday);
						this.averagelinechartdataset.setValue(0.0,"AverageDailyCje",wkfriday);
					}
				}

				if(period.equals(NodeGivenPeriodDataItem.WEEK))
					tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
				else if(period.equals(NodeGivenPeriodDataItem.DAY))
					tmpdate = tmpdate.plus(1, ChronoUnit.DAYS) ;
				else if(period.equals(NodeGivenPeriodDataItem.MONTH))
					tmpdate = tmpdate.plus(1, ChronoUnit.MONTHS) ;
			} while (tmpdate.isBefore( requireend) || tmpdate.isEqual(requireend));

//			super.barchart.setNotify(true);
			xiuShiLeftRangeAxixAfterDispalyDate (nodexdata,startdate,enddate,highestHigh,period);
			
			return highestHigh;
		}
		/*
		 * 
		 */
		private void xiuShiLeftRangeAxixAfterDispalyDate (NodeXPeriodData nodexdata,LocalDate startdate, LocalDate enddate, double highestHigh, String period)
		{
			if(highestHigh == 0.0)
				return;
			
			CustomRendererForCjl cjlrender = (CustomRendererForCjl) super.plot.getRenderer(0); 
			cjlrender.setDisplayNode(this.getCurDisplayedNode() );
			cjlrender.setDisplayNodeXPeriod (nodexdata);
				
			CategoryLabelCustomizableCategoryAxis axis = (CategoryLabelCustomizableCategoryAxis)super.plot.getDomainAxis(0);
			axis.setDisplayNode(this.getCurDisplayedNode(),period);
			
			Range curaxisrange = super.plot.getRangeAxis(0).getRange();
			double curaxisupper = curaxisrange.getUpperBound();
			if(curaxisupper > highestHigh*1.12)
				return;
			
			try{
				super.plot.getRangeAxis(0).setRange(0, highestHigh*1.12);
			} catch(java.lang.IllegalArgumentException e) {
//				e.printStackTrace();
				super.plot.getRangeAxis(0).setRange(0, 1);
			}
			
			setPanelTitle ("成交量",enddate);
			super.decorateXaxisWithYearOrMonth("year".trim());
		}

	 
	 
	 /*
	  * 
	  */
	 public Double displayAverageDailyCjlOfWeekLineDataToGuiUsingRightAxix (NodeXPeriodData nodexdata,LocalDate startdate, LocalDate enddate, String period)
	 {
			DaPan dapan;
			if(super.getCurDisplayedNode().getType() == BkChanYeLianTreeNode.BKGEGU) {
				BanKuai bk = ((StockOfBanKuai)super.getCurDisplayedNode() ).getBanKuai();
				dapan = (DaPan)bk.getRoot();
			} else
				dapan = (DaPan)(this.getCurDisplayedNode().getRoot());
			
			LocalDate indexrequirestart = startdate;
			LocalDate indexrequireend = enddate;//(LocalDate) barchartdataset.getColumnKey(barchartdataset.getColumnCount()-1);
			
			LocalDate requireend = indexrequireend.with(DayOfWeek.SATURDAY);
			LocalDate requirestart = indexrequirestart.with(DayOfWeek.SATURDAY);
			
			Double avecjeaxix = 0.0;
			LocalDate tmpdate = requirestart; 
			do  {
				LocalDate wkfriday = tmpdate.with(DayOfWeek.FRIDAY);
				
				Double avecjl = nodexdata.getAverageDailyChengJiaoLiangOfWeek(wkfriday, 0);
					
					if(avecjl != null) {
						linechartdataset.setValue(avecjl,"AverageDailyCje", wkfriday);
						
						if(avecjl > avecjeaxix)
							avecjeaxix = avecjl;
					} else {
						if( !dapan.isDaPanXiuShi(tmpdate,0,period) ) 
							linechartdataset.setValue(0.0,"AverageDailyCje",wkfriday);
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
//					super.linechartdataset.clear();
//					e.printStackTrace();
					
				}
			}
			
			super.plot.getRenderer(3).setSeriesPaint(0, new Color(0,102,0) );
			super.plot.getRenderer(3).setSeriesPaint(1, new Color(102,255,102) );
			super.plot.getRenderer(3).setSeriesPaint(2, new Color(178,102,255) );
			super.plot.getRenderer(3).setSeriesPaint(3, new Color(178,102,255) );
			
		}
	
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
		
		public void setDisplayBarOfSpecificBanKuaiCjlInsteadOfSelfCjl (TDXNodes supernode) 
		{
			this.shouldDisplayBarOfSuperBanKuaiCjeInsteadOfSelfCje = supernode;
		}
		public void setDrawAverageDailyCjlOfWeekLine(Boolean draw) {
			this.displayaveragedailycjl  = draw;
			
		}
		public boolean shouldDrawAverageDailyCjlOfWeekLine() {
				// TODO Auto-generated method stub
				return this.displayaveragedailycjl;
		}


		@Override
	    public void highLightSpecificBarColumn (LocalDate selecteddate)
	    {
			if(selecteddate == null)
	    		return;
			
			super.highLightSpecificBarColumn (selecteddate);
			
			int indexforline = super.linechartdataset.getColumnIndex(selecteddate) ;
	    	if(indexforline != -1) {
	    		CategoryItemRenderer fourthrenderer = plot.getRenderer(4);
	    		if(fourthrenderer instanceof BanKuaiFengXiCategoryLineRenderer)
	    			((BanKuaiFengXiCategoryLineRenderer)fourthrenderer).setBarColumnShouldChangeColor(indexforline);
	    	}
	    	
	        super.barchart.fireChartChanged();//必须有这句
	    }
	/*
	 * 
	 */
	@Override
	public String getToolTipSelected ()
	{
		int indexx = barchartdataset.getColumnIndex(super.dateselected);
		
		CustomCategoryToolTipGeneratorForChenJiaoLiang ttpg = 	
		(CustomCategoryToolTipGeneratorForChenJiaoLiang)(((CustomRendererForCjl) plot.getRenderer()).getBaseToolTipGenerator());
		
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

class CustomRendererForCjl extends BanKuaiFengXiCategoryBarRenderer 
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(CustomRendererForCje.class);
   
    public CustomRendererForCjl() {
        super();
        super.displayedmaxwklevel = 3;
        try {
        	 String readingsettinginprop  = super.prop.getProperty ("CjlColumnColor");
             if(readingsettinginprop != null) {
             	super.displayedcolumncolorindex = Color.decode( readingsettinginprop );
                 super.lastdisplayedcolumncolorindex = Color.decode( readingsettinginprop );
             } else {
             	super.displayedcolumncolorindex = Color.MAGENTA;
                 super.lastdisplayedcolumncolorindex = Color.MAGENTA;
             }
        } catch (java.lang.NullPointerException e) {
        	super.displayedcolumncolorindex = Color.MAGENTA;
            super.lastdisplayedcolumncolorindex = Color.MAGENTA;
        }
       
        this.setBarPainter(new StandardBarPainter());
		
        CustomCategoryToolTipGeneratorForChenJiaoLiang custotooltip = new CustomCategoryToolTipGeneratorForChenJiaoLiang();
		this.setBaseToolTipGenerator(custotooltip);
		
//		DecimalFormat decimalformate = new DecimalFormat(",###");//("#0.000");
//		this.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}",decimalformate));
		
		BkfxItemLabelGenerator labelgenerator = new BkfxItemLabelGeneratorForCjl ();
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

class BkfxItemLabelGeneratorForCjl extends BkfxItemLabelGenerator 
{
	public BkfxItemLabelGeneratorForCjl ()
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



class CustomCategoryToolTipGeneratorForChenJiaoLiang extends BanKuaiFengXiCategoryBarToolTipGenerator  
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
	
}