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
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.jfree.chart.annotations.CategoryPointerAnnotation;
import org.jfree.chart.annotations.CategoryTextAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.Layer;
import org.jfree.ui.TextAnchor;

import com.exchangeinfomanager.Trees.CreateExchangeTree;
import com.exchangeinfomanager.bankuaifengxi.HighlightAndExportNodes.BanKuaiGeGuMatchCondition;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.DaPan;
import com.exchangeinfomanager.nodes.ServicesForNode;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.nodes.stocknodexdata.StockNodesXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.TDXNodesXPeriodExternalData;

public class BanKuaiFengXiCategoryBarChartCjeZhanbiPnl extends BanKuaiFengXiCategoryBarChartPnl
{
	public BanKuaiFengXiCategoryBarChartCjeZhanbiPnl() 
	{
		super ("CJEZHANBI");
		super.plot.setRenderer(0, new CustomCategroyRendererForCjeZhanBi() );
        super.plot.setRenderer(3, new BanKuaiFengXiCategoryCjeZhanbiLineRenderer () );
        
        linechartdatasetforcjezb = new DefaultCategoryDataset();
		super.plot.setDataset(2, linechartdatasetforcjezb);
		super.plot.setRenderer(2, new BanKuaiFengXiCategoryCjeZhanbiLineRenderer () );
        ValueAxis rangeaxis = plot.getRangeAxis(0);
//        super.plot.setRangeAxis(2, rangeaxis);
        
        createGuiAndEvents ();
	}
	
	private static final long serialVersionUID = 1L;	
	private static Logger logger = Logger.getLogger(BanKuaiFengXiCategoryBarChartCjeZhanbiPnl.class);
	protected JMenuItem mntmCjeZblineDate;
	protected JMenuItem mntmClearLineData;
	protected JMenuItem mntmSetZhanBiUpLevel;
	protected JMenuItem mntmSetZhanBiDownLevel;
	private Boolean displayzhanbishujuinline = false;
	private DefaultCategoryDataset linechartdatasetforcjezb;
	private JMenuItem mntmSetZhanBiExtremeLevel;
	
	private void createGuiAndEvents () 
	{
		mntmCjeZblineDate = new JMenuItem("占比柱图转线图");
		chartPanel.getPopupMenu().add(mntmCjeZblineDate,0);
		
		mntmClearLineData = new JMenuItem("突出占比数据");
		chartPanel.getPopupMenu().add(mntmClearLineData,1);
		
		mntmSetZhanBiExtremeLevel =  new JMenuItem("设置Cje占比上下限");
		chartPanel.getPopupMenu().add(mntmSetZhanBiExtremeLevel,2);
		
	
		mntmSetZhanBiExtremeLevel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				setupNodeZhanbiDownLevel ();				
			}
		});
		
		mntmCjeZblineDate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				if(mntmCjeZblineDate.getText().contains("X")) {
					setDisplayZhanBiInLine (false);
					
					PropertyChangeEvent evtzd = new PropertyChangeEvent(this, BanKuaiFengXiCategoryBarChartPnl.CJEZBTOLINE, getCurDisplayedNode().getMyOwnCode(), "notcjecjlzbtoline" );
		            pcs.firePropertyChange(evtzd);
		            
				} else {
					setDisplayZhanBiInLine (true);
					
					PropertyChangeEvent evtzd = new PropertyChangeEvent(this, BanKuaiFengXiCategoryBarChartPnl.CJEZBTOLINE, getCurDisplayedNode().getMyOwnCode(), "cjecjlzbtoline" );
		            pcs.firePropertyChange(evtzd);
				}
			}
		});
		
		mntmClearLineData.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				PropertyChangeEvent evtqk = new PropertyChangeEvent(this, BanKuaiFengXiCategoryBarChartPnl.ONLYSHOWCJEZBBARDATA, getCurDisplayedNode().getMyOwnCode(), "onlyshowcjezhanbibardata" );
	            pcs.firePropertyChange(evtqk);
		
			}
			
		});
	}
	
	/*
	 * 
	 */
	protected void setupNodeZhanbiDownLevel() 
	{
		TDXNodes node = super.getCurDisplayedNode();
		Double[] zblevel = node.getNodeCjeZhanbiLevel();
		
		ExtremeZhanbiSettingPnl zhanbisetting = new ExtremeZhanbiSettingPnl (zblevel,super.yaxisvaluewhenmouseclicked); 
		JOptionPane.showMessageDialog(null, zhanbisetting,"设置占比上下限", JOptionPane.OK_CANCEL_OPTION);
		
		Double min = zhanbisetting.getExtremeZhanbiMin ();
		Double max = zhanbisetting.getExtremeZhanbiMax ();
		ServicesForNode svs = node.getServicesForNode(true);
		svs.setNodeCjeExtremeUpDownZhanbiLevel (node,min,max);
		svs = null;
		
		super.redecorateExtremeZhanbiLevel(node.getNodeCjeZhanbiLevel());
	}
	public void reDisplayNodeDataOnDirection(String forwardbackward)
	{
		long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(super.displaydatestarted,super.displaydateended);
		forwardbackward = forwardbackward.toUpperCase();
		switch (forwardbackward) {
		case "FORWARD":
			LocalDate newdisplaystart = super.displaydatestarted.minusDays(java.lang.Math.abs(daysBetween) ); //有些股票机构股东数据是很久以前的，没意义
			LocalDate newdisplayend = super.displaydateended.minusDays(java.lang.Math.abs(daysBetween)) ;
			this.updatedDate(super.getCurDisplayedNode(), newdisplaystart, newdisplayend, super.globeperiod);
			break;
		case "BACKWARD":
			LocalDate newdisplaystart1 = super.displaydatestarted.plusDays(java.lang.Math.abs(daysBetween) ); //有些股票机构股东数据是很久以前的，没意义
			LocalDate newdisplayend1 = super.displaydateended.plusDays(java.lang.Math.abs(daysBetween)) ;
			
			if(newdisplayend1.isAfter(LocalDate.now())) {
				newdisplayend = LocalDate.now();
				newdisplaystart1 = newdisplayend.minus(36,ChronoUnit.MONTHS).with(DayOfWeek.MONDAY);
			}
			
			this.updatedDate(super.getCurDisplayedNode(), newdisplaystart1, newdisplayend1, super.globeperiod);
			break;	
		}
	}
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.bankuaifengxi.BarChartPanelDataChangedListener#updatedDate(com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode, java.time.LocalDate, int, java.lang.String)
	 * 升级占比数据
	 */
	public void updatedDate (TDXNodes node, LocalDate startdate, LocalDate enddate,String period)
	{
		super.setCurDisplayNode(node,startdate, enddate, period );
		
		if(node.getType() == BkChanYeLianTreeNode.DAPAN) {
			mntmCjeZblineDate.setEnabled(false);
	        mntmClearLineData.setEnabled(false);
		} else {
			mntmCjeZblineDate.setEnabled(true);
	        mntmClearLineData.setEnabled(true);
		}
		
		preparingdisplayDataToGui (node,startdate,enddate,period);
	
//		//如有分析结果，ticklable显示红色
//		CategoryLabelCustomizableCategoryAxis axis = (CategoryLabelCustomizableCategoryAxis)super.plot.getDomainAxis();
//		axis.setDisplayNode(this.getCurDisplayedNode(),period);
		
	}
	/*
	 * 
	 */
	private void preparingdisplayDataToGui (TDXNodes node,LocalDate startdate,LocalDate enddate,String period)
	{
		this.resetDate();
		
		super.barchart.setNotify(false);
		
		((BanKuaiFengXiCategoryBarRenderer)super.plot.getRenderer()).unhideBarMode();
		
		NodeXPeriodData nodexdata = node.getNodeXPeroidData(period);
//		((TDXNodesXPeriodExternalData)nodexdata).calulateKLearnResult ();
//		((TDXNodesXPeriodExternalData)nodexdata).calulateApacheMathKLearnResult ();
		
		Double leftrangeaxix = displayBarDataToGui (nodexdata,startdate,enddate,period);
		
		if(super.shouldDrawQueKouLine()) {
			Integer qkmax = displayQueKouLineDataToGui(nodexdata,period);
		}
		if(super.shouldDrawZhangDieTingLine() ) {
			Integer zdt = displayZhangDieTingLineDataToGui(nodexdata,period);
		}
		if(this.shouldDisplayZhanBiInLine() ) {
			super.resetLineDate ();
			this.dipalyCjeZBLineDataToGui ( super.getCurDisplayedNode().getNodeXPeroidData(period),period );
		}
		
		super.barchart.setNotify(true);
	}
	/*
	 * 
	 */
	public Double displayBarDataToGui(NodeXPeriodData nodexdata, LocalDate startdate, LocalDate enddate,
			String period) 
	{
		DaPan dapan = (DaPan)CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getModel().getRoot(); //alwayse use tdx tree fro dapan;
//		if(this.getCurDisplayedNode().getType() == BkChanYeLianTreeNode.BKGEGU) {
//			BanKuai bk = ((StockOfBanKuai)this.getCurDisplayedNode()).getBanKuai();
//			dapan = (DaPan)bk.getRoot();
//		}
//		else
//			dapan = (DaPan)super.getCurDisplayedNode().getRoot();
		
		LocalDate requireend = enddate.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = startdate.with(DayOfWeek.MONDAY);

		double highestHigh =0.0; //设置显示范围
		double lowestLow = 10000000.0;
	
		LocalDate tmpdate = requirestart;
		do {
			LocalDate wkfriday = tmpdate.with(DayOfWeek.FRIDAY);
			//ZhanBi Part
			Double cjezb = nodexdata.getChenJiaoErZhanBi(wkfriday, 0);
			if(cjezb != null) {
				super.barchartdataset.setValue(cjezb,super.getRowKey(),wkfriday );

				if(cjezb > highestHigh)
					highestHigh = cjezb;
				
				if(cjezb < lowestLow)
					lowestLow = cjezb;
				
				
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
	//					cpa.setBaseRadius(0.0);
	//			        cpa.setTipRadius(25.0);
				        cpa.setFont(new Font("SansSerif", Font.BOLD, 10));
				        cpa.setPaint(paintcolor);
				        cpa.setTextAnchor(TextAnchor.CENTER);
						super.plot.addAnnotation(cpa);
					}
			}
			//对个股有买入卖出记录的时候
			if(super.getCurDisplayedNode().getType() == BkChanYeLianTreeNode.TDXGG) { 
				Integer mrjl = ((StockNodesXPeriodData)nodexdata).hasMaiRuJiLuInPeriod(wkfriday, 0);
				if(mrjl != null) {
					double angle; Color paintcolor;String label;
					angle = 30 * Math.PI/4;
					paintcolor = Color.RED;
					label = "mr";
					CategoryPointerAnnotation cpa = new CategoryPointerAnnotation(label, wkfriday , cjezb, angle);
//					cpa.setBaseRadius(0.0);
//			        cpa.setTipRadius(25.0);
			        cpa.setFont(new Font("SansSerif", Font.BOLD, 10));
			        cpa.setPaint(paintcolor);
			        cpa.setTextAnchor(TextAnchor.CENTER);
					super.plot.addAnnotation(cpa);
				}
				Integer mcjl = ((StockNodesXPeriodData)nodexdata).hasMaiChuJiLuInPeriod(wkfriday, 0);
				if(mcjl != null) {
					double angle; Color paintcolor;String label;
					angle = 30 * Math.PI/4;
					paintcolor = Color.GREEN.darker();
					label = "mc";
					CategoryPointerAnnotation cpa = new CategoryPointerAnnotation(label, wkfriday , cjezb, angle);
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
		
		xiuShiLeftRangeAxixAfterDispalyDate (nodexdata,startdate,enddate,highestHigh,lowestLow,period);
		
		return highestHigh;

	}
	/*
	 * 成交额占比线形数据
	 */
	public Double dipalyCjeZBLineDataToGui (NodeXPeriodData nodexdata,String period)
	{
		if(!this.shouldDisplayZhanBiInLine() ) 
			return null;
		
		((BanKuaiFengXiCategoryBarRenderer)super.plot.getRenderer()).hideBarMode();
		
		if(super.barchartdataset.getColumnCount() ==0 )
			return null;
		
		DaPan dapan = (DaPan)CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getModel().getRoot(); //alwayse use tdx tree fro dapan;
		
		LocalDate indexrequirestart = (LocalDate) barchartdataset.getColumnKey(0);
		LocalDate indexrequireend = (LocalDate) barchartdataset.getColumnKey(barchartdataset.getColumnCount()-1);
		
		LocalDate requireend = indexrequireend.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = indexrequirestart.with(DayOfWeek.SATURDAY);
		
		Double highestHigh = 0.0;//设置显示范围 
		Double lowestLow = 1000000.0;
		
		LocalDate tmpdate = requirestart; 
		do {
			LocalDate wkfriday = tmpdate.with(DayOfWeek.FRIDAY);
			Double cjezb = nodexdata.getChenJiaoErZhanBi(wkfriday, 0);
			
			if(cjezb != null) {
				this.linechartdatasetforcjezb.setValue(cjezb,super.getRowKey(), wkfriday);
				
				if(cjezb > highestHigh)
					highestHigh = cjezb;
				
				if(cjezb < lowestLow)
					lowestLow = cjezb;
				
			} else if( !dapan.isDaPanXiuShi(tmpdate,0,period) ) 
				this.linechartdatasetforcjezb.setValue(0.0,super.getRowKey(),wkfriday);

			if(period.equals(NodeGivenPeriodDataItem.WEEK))
				tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
			else if(period.equals(NodeGivenPeriodDataItem.DAY))
				tmpdate = tmpdate.plus(1, ChronoUnit.DAYS) ;
			else if(period.equals(NodeGivenPeriodDataItem.MONTH))
				tmpdate = tmpdate.plus(1, ChronoUnit.MONTHS) ;
			
		} while (tmpdate.isBefore( requireend) || tmpdate.isEqual(requireend));
		
		this.xiuShiRightRangeAxixAfterDispalyDate(nodexdata, highestHigh, lowestLow,true);
		
		return highestHigh;
	}
	/*
	 * 涨跌停线型数据
	 */
	Integer displayZhangDieTingLineDataToGui(NodeXPeriodData nodexdata, String period) 
	{
		((BanKuaiFengXiCategoryBarRenderer)super.plot.getRenderer()).unhideBarMode();
		
		if(super.barchartdataset.getColumnCount() ==0 )
			return null;
		
		DaPan dapan = (DaPan)CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getModel().getRoot(); //alwayse use tdx tree fro dapan;
		
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
		
		this.xiuShiRightRangeAxixAfterDispalyDate(nodexdata, Double.valueOf(zdtmax), 0.0,false);
		return zdtmax;
	}
	/*
	 * 
	 */
	Integer displayQueKouLineDataToGui(NodeXPeriodData nodexdata, String period) 
	{
		((BanKuaiFengXiCategoryBarRenderer)super.plot.getRenderer()).unhideBarMode();
		
		DaPan dapan = (DaPan)CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getModel().getRoot(); //alwayse use tdx tree fro dapan;
//		if(super.getCurDisplayedNode().getType() == BkChanYeLianTreeNode.BKGEGU) {
//			BanKuai bk = ((StockOfBanKuai)super.getCurDisplayedNode() ).getBanKuai();
//			dapan = (DaPan)bk.getRoot();
//		} else
//			dapan = (DaPan)(this.getCurDisplayedNode().getRoot());
		
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
		
		this.xiuShiRightRangeAxixAfterDispalyDate(nodexdata, Double.valueOf(qkmax),0.0, false);
		
		return qkmax;

	}
	/**
	 * 
	 * @param nodexdata
	 * @param rightrangeaxixmax
	 * @param forcetochange: 强制更新坐标，不管现在坐标的值
	 */
	 
	private void xiuShiRightRangeAxixAfterDispalyDate (NodeXPeriodData nodexdata,Double rightrangeaxixmax, Double rightrangeaxixmin, Boolean forcetochange)
	{
		if(rightrangeaxixmax == 0)
			return;
		
		int row = super.linechartdataset.getRowCount();
//		int column = super.linechartdataset.getColumnCount();
		
		Range rangeData = super.plot.getRangeAxis(3).getRange();
		if(forcetochange || row == 1) 
			super.plot.getRangeAxis(3).setRange(rightrangeaxixmin * 0.7, rightrangeaxixmax*1.12);
		else {
			double upbound = rangeData.getUpperBound();
			try{
				if(rightrangeaxixmax*1.12 > upbound)
					super.plot.getRangeAxis(3).setRange(rightrangeaxixmin * 0.7, rightrangeaxixmax*1.12);
				else if (upbound >= rightrangeaxixmax * 200)  //原来的坐标过大，会导致小坐标的线根本看不到，也是要更换坐标的。
					super.plot.getRangeAxis(3).setRange(rightrangeaxixmin * 0.7, rightrangeaxixmax*1.12);
				
			} catch(java.lang.IllegalArgumentException e) {	super.plot.getRangeAxis(3).setRange(0, 1);
//				super.linechartdataset.clear();
//				e.printStackTrace();
			}
		}
		
		super.plot.getRenderer(3).setSeriesPaint(0, new Color(0,102,0) );
		super.plot.getRenderer(3).setSeriesPaint(1, new Color(102,255,102) );
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
	 
	private void xiuShiLeftRangeAxixAfterDispalyDate (NodeXPeriodData nodexdata,LocalDate startdate, LocalDate enddate, double highestHigh, double lowestLow, String period)
	{
		if(highestHigh == 0.0)
			return;
		
		Double[] zblevel = super.getCurDisplayedNode().getNodeCjeZhanbiLevel();
		if(zblevel[0] != null && zblevel[0] < lowestLow)
			lowestLow = zblevel[0]; 
		if(zblevel[1] != null && zblevel[1] > highestHigh)
			highestHigh = zblevel[1];
		
		BanKuaiFengXiCategoryBarRenderer render0 = (BanKuaiFengXiCategoryBarRenderer)super.plot.getRenderer(0);
		render0.setDisplayNode(this.getCurDisplayedNode());
		render0.setDisplayNodeXPeriod (nodexdata);
		
		BanKuaiFengXiCategoryCjeZhanbiLineRenderer render2 = (BanKuaiFengXiCategoryCjeZhanbiLineRenderer)super.plot.getRenderer(2);
		render2.setDisplayNode(this.getCurDisplayedNode());
		render2.setDisplayNodeXPeriod (nodexdata);
		
		//如有分析结果，ticklable显示红色
		CategoryLabelCustomizableCategoryAxis axis = (CategoryLabelCustomizableCategoryAxis)super.plot.getDomainAxis(0);
		axis.setDisplayNode(this.getCurDisplayedNode(),period);
		
		try{
			super.plot.getRangeAxis(0).setRange(lowestLow * 0.7, highestHigh*1.12);
		} catch (java.lang.IllegalArgumentException e) {
			e.printStackTrace();
			super.plot.getRangeAxis(0).setRange(0, 1);
		}
		
//		if(zblevel[0] != null) {
//			ValueMarker downmarker = new ValueMarker(zblevel[0]);
//			downmarker.setPaint(Color.red);
//		    plot.addRangeMarker(0,downmarker, Layer.FOREGROUND);
//		    super.valuemarkerlist.add(downmarker);
//		}
//		if(zblevel[1] != null) {
//			ValueMarker upmarker = new ValueMarker(zblevel[1]);
//			upmarker.setPaint(Color.GREEN);
//		    plot.addRangeMarker(0,upmarker, Layer.FOREGROUND);
//		    super.valuemarkerlist.add(upmarker);
//		}
		
//		setPanelTitle ("成交额",enddate);
		super.decorateXaxisWithYearOrMonth("month".trim());
		super.redecorateExtremeZhanbiLevel (zblevel);
	}

	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartPnl#resetLineDate()
	 */
	public void resetLineDate ()
	{
		super.resetLineDate();
		if(linechartdatasetforcjezb != null)
			linechartdatasetforcjezb.clear();
		
		plot.getRangeAxis(3).setRange(0.0, 1.0);
	}
	public void resetDate ()
	{
		super.resetDate();
		
		if(linechartdatasetforcjezb != null)
			linechartdatasetforcjezb.clear();
		
		CategoryItemRenderer fourthrenderer = plot.getRenderer(2);
		((BanKuaiFengXiCategoryCjeZhanbiLineRenderer)fourthrenderer).setKLearningClusterBaseLineId (-1);
	}
	
	public void setDisplayZhanBiInLine (Boolean draw)
	{
		this.displayzhanbishujuinline = draw;
		if(this.displayzhanbishujuinline) 
			this.mntmCjeZblineDate.setText("X 占比柱图转线图");
		else
			this.mntmCjeZblineDate.setText("占比柱图转线图");
	}
	
	public Boolean shouldDisplayZhanBiInLine ()
	{
		if(this.displayzhanbishujuinline == null )
			return false;
		else
			
		return this.displayzhanbishujuinline;
	}
	
	@Override
    public void highLightSpecificBarColumn (LocalDate selecteddate)
    {
		if(selecteddate == null)
    		return;
		
		super.highLightSpecificBarColumn (selecteddate);
		
		int indexforline = this.linechartdatasetforcjezb.getColumnIndex(selecteddate) ;
    	if(indexforline != -1) {
    		CategoryItemRenderer fourthrenderer = plot.getRenderer(2);
    		if(fourthrenderer instanceof BanKuaiFengXiCategoryCjeZhanbiLineRenderer) {
    			((BanKuaiFengXiCategoryLineRenderer)fourthrenderer).setBarColumnShouldChangeColor(indexforline);
    			
    			if(super.getCurDisplayedNode().getType() != BkChanYeLianTreeNode.DAPAN  ) {
    				NodeXPeriodData nodexdata = super.getCurDisplayedNode().getNodeXPeroidData(super.globeperiod);
        			((TDXNodesXPeriodExternalData)nodexdata).calulateSpecificDateAMOZhanBiApacheMathKLearnResult(selecteddate);
        			Integer selectclusteringid = ((TDXNodesXPeriodExternalData)nodexdata).getSpecificDateAMOZhanBiApacheMathKLearnClusteringID (selecteddate);
        			if(selectclusteringid != null)
        				((BanKuaiFengXiCategoryCjeZhanbiLineRenderer)fourthrenderer).setKLearningClusterBaseLineId (selectclusteringid);
    			}
    		}
    	}
    	
        super.barchart.fireChartChanged();//必须有这句
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
	@Override
	public void BanKuaiGeGuMatchConditionValuesChanges(BanKuaiGeGuMatchCondition expc) 
	{
		Integer cjezbtoupleveldpmax = expc.getSettingDpMaxWk();
		Double cjemin = expc.getSettingChenJiaoErMin();
		Double cjemax = expc.getSettingChenJiaoErMax();
		Integer cjemaxwk = expc.getSettingChenJiaoErMaxWk();
		Double shoowhsl = expc.getSettingHuanShouLv();
	
		Integer cjezbdporbkmax = expc.getSettingDpMaxWk();
		Integer cjezbdporbkmin = expc.getSettingDpMinWk();
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
        try {
        	String readingsettinginprop  = super.prop.getProperty ("CjeZhanbiColumnColor");
            if(readingsettinginprop != null) {
            	super.displayedcolumncolorindex = Color.decode( readingsettinginprop );
                super.lastdisplayedcolumncolorindex = Color.decode( readingsettinginprop );
            } else {
            	super.displayedcolumncolorindex = Color.YELLOW;
                super.lastdisplayedcolumncolorindex = Color.YELLOW;
            }
        } catch (java.lang.NullPointerException e) {
        	super.displayedcolumncolorindex = Color.yellow;
            super.lastdisplayedcolumncolorindex = Color.yellow;
        }
        
        
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
    	
		Integer maxweek = nodexdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai(selecteddate,0);
		Integer minweek = nodexdata.getChenJiaoErZhanBiMinWeekOfSuperBanKuai(selecteddate,0);
		
		String result = "";
		if(maxweek != null && maxweek >= super.displayedmaxwklevel) {
//			NumberFormat nf = this.getNumberFormat(); //显示占比数字
//			result =  nf.format(dataset.getValue(row, column));
			result = maxweek.toString(); //显示占比MAXWEEK
		} else if (minweek != null && minweek != 0 && minweek >=  super.displayedminwklevel) {
//			NumberFormat nf = this.getNumberFormat();
//			result =  nf.format(dataset.getValue(row, column));
			result = "-" + minweek.toString();
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