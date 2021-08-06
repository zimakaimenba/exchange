package com.exchangeinfomanager.bankuaifengxi.CategoryBar;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.jfree.chart.annotations.CategoryPointerAnnotation;
import org.jfree.chart.annotations.CategoryTextAnnotation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;

import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;

import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.Core.Nodes.DaPan;
import com.exchangeinfomanager.Core.Nodes.TDXNodes;
import com.exchangeinfomanager.Core.NodesDataServices.ServicesForNode;
import com.exchangeinfomanager.Core.Nodexdata.NodeXPeriodData;
import com.exchangeinfomanager.Core.Nodexdata.StockNodesXPeriodData;
import com.exchangeinfomanager.Core.Nodexdata.ohlcvaprimarydata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.Core.Trees.CreateExchangeTree;
import com.exchangeinfomanager.bankuaifengxi.HighlightAndExportNodes.BanKuaiAndGeGuMatchingConditions;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.gui.subgui.DateRangeSelectPnl;

public class BanKuaiFengXiCategoryBarChartCjlZhanbiPnl extends BanKuaiFengXiCategoryBarChartPnl 
{

	public BanKuaiFengXiCategoryBarChartCjlZhanbiPnl() 
	{
		super ("CJLZHANBI");

		super.plot.setRenderer(0,new CustomCategroyRendererForCjlZhanBi() );
//		super.plot.setRenderer(3, new BanKuaiFengXiCategoryCjlZhanbiLineRenderer () );
        
        linechartdatasetforcjlzb = new DefaultCategoryDataset();
		super.plot.setDataset(2, linechartdatasetforcjlzb);
		super.plot.setRenderer(2, new BanKuaiFengXiCategoryCjeZhanbiLineRenderer () );
        ValueAxis rangeaxis = plot.getRangeAxis(0);
//        super.plot.setRangeAxis(2, rangeaxis);
        
        createGuiAndEvents ();
	}
	
	
	private static Logger logger = Logger.getLogger(BanKuaiFengXiCategoryBarChartCjeZhanbiPnl.class);
	private DefaultCategoryDataset linechartdatasetforcjlzb;
	protected JMenuItem mntmCjlZblineDate;
	protected JMenuItem mntmClearLineData;
	protected JMenuItem mntmSetZhanBiExtremeLevel;
	private Boolean displayzhanbishujuinline = false;
	
	private void createGuiAndEvents () 
	{
		mntmCjlZblineDate = new JMenuItem("占比柱图转线图");
		chartPanel.getPopupMenu().add(mntmCjlZblineDate,0);
		
		mntmClearLineData = new JMenuItem("突出占比数据");
		chartPanel.getPopupMenu().add(mntmClearLineData,1);
		
		mntmSetZhanBiExtremeLevel =  new JMenuItem("设置Cjl占比上下限");
		chartPanel.getPopupMenu().add(mntmSetZhanBiExtremeLevel,2);
		
	
		mntmSetZhanBiExtremeLevel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				setupNodeZhanbiDownLevel ();				
			}
		});
		
		mntmCjlZblineDate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				if(mntmCjlZblineDate.getText().contains("X")) {
					setDisplayZhanBiInLine (false);
					
					PropertyChangeEvent evtzd = new PropertyChangeEvent(this, BanKuaiFengXiCategoryBarChartPnl.CJLZBTOLINE, getCurDisplayedNode().getMyOwnCode(), "notcjecjlzbtoline" );
		            pcs.firePropertyChange(evtzd);
		            
				} else {
					setDisplayZhanBiInLine (true);
					
					PropertyChangeEvent evtzd = new PropertyChangeEvent(this, BanKuaiFengXiCategoryBarChartPnl.CJLZBTOLINE, getCurDisplayedNode().getMyOwnCode(), "cjecjlzbtoline" );
		            pcs.firePropertyChange(evtzd);
				}
				
				
			}
		});
		
		mntmClearLineData.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				PropertyChangeEvent evtqk = new PropertyChangeEvent(this, BanKuaiFengXiCategoryBarChartPnl.ONLYSHOWCJLZBBARDATA, getCurDisplayedNode().getMyOwnCode(), "onlyshowcjlzhanbibardata" );
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
		Double[] zblevel = node.getNodeJiBenMian().getNodeCjlZhanbiLevel();
		
		ExtremeZhanbiSettingPnl zhanbisetting = new ExtremeZhanbiSettingPnl (zblevel,super.yaxisvaluewhenmouseclicked); 
		JOptionPane.showMessageDialog(null, zhanbisetting,"设置占比上下限", JOptionPane.OK_CANCEL_OPTION);
		Double min = zhanbisetting.getExtremeZhanbiMin ();
		Double max = zhanbisetting.getExtremeZhanbiMax ();
		ServicesForNode svs = node.getServicesForNode(true);
		svs.setNodeCjlExtremeUpDownZhanbiLevel (node,min,max);
		svs = null;
		
		super.redecorateExtremeZhanbiLevel(node.getNodeJiBenMian().getNodeCjlZhanbiLevel());
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
		barchartdataset.clear();
		super.setCurDisplayNode(node,startdate, enddate, period );

		if(node.getType() == BkChanYeLianTreeNode.DAPAN) {
	        mntmCjlZblineDate.setEnabled(false);
	        mntmClearLineData.setEnabled(false);
		} else {
			mntmCjlZblineDate.setEnabled(true);
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
		
		if( this.shouldDisplayZhanBiInLine() ) {
			super.resetLineDate ();
			this.dipalyCjlZBLineDataToGui ( super.getCurDisplayedNode().getNodeXPeroidData(period),period );
		}
		
		super.highLightSpecificBarColumn(super.getCurSelectedDate());
		super.barchart.setNotify(true);
	}
	/*
	 * 
	 */
	public Double displayBarDataToGui(NodeXPeriodData nodexdata, LocalDate startdate, LocalDate enddate,
			String period)
	{
		DaPan dapan = (DaPan)CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getModel().getRoot(); //alwayse use tdx tree fro dapan;
		
		LocalDate requireend = enddate;//.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = startdate;//.with(DayOfWeek.MONDAY);

		double highestHigh =0.0; //设置显示范围
		double lowestLow = 100000.0;
	
		LocalDate tmpdate = requirestart;
		do {
			LocalDate wkfriday = tmpdate.with(DayOfWeek.FRIDAY);
			//ZhanBi Part
			Double cjlzb = nodexdata.getChenJiaoLiangZhanBi(wkfriday);
			if(cjlzb != null) {
				super.barchartdataset.setValue(cjlzb,super.getRowKey(),wkfriday );

				if(cjlzb > highestHigh)
					highestHigh = cjlzb;
				if(cjlzb < lowestLow)
					lowestLow = cjlzb;
				
//				//标记该NODE本周是阳线还是阴线
//				Double zhangdiefu = nodexdata.getSpecificOHLCZhangDieFu(wkfriday);
//				if(zhangdiefu != null && zhangdiefu >0) {
//					CategoryTextAnnotation cpa  = new CategoryTextAnnotation ("\u21B1", wkfriday , 0.0);
//					//cpa.setBaseRadius(0.0);
//					// cpa.setTipRadius(25.0);
//					cpa.setFont(new Font("SansSerif", Font.BOLD, 10));
//					cpa.setPaint(Color.RED);
//					cpa.setTextAnchor(TextAnchor.CENTER);
//					super.plot.addAnnotation(cpa);
//				}
				
//				标记大盘该周是涨还是跌
//				NodeXPeriodData dpnodexdata = dapan.getNodeXPeroidData(period);
				Double dpzdf = nodexdata.getSpecificOHLCZhangDieFu(wkfriday);
				if(dpzdf != null && dpzdf > 0 ) {
					CategoryTextAnnotation cpa  = new CategoryTextAnnotation ("\u21B1", wkfriday ,cjlzb);
					//cpa.setBaseRadius(0.0);
					//cpa.setTipRadius(25.0);
//					cpa.setFont(new Font("SansSerif", Font.BOLD, 10));
					cpa.setPaint(Color.RED);
					cpa.setTextAnchor(TextAnchor.CENTER);
					super.plot.addAnnotation(cpa);
				} else if(dpzdf != null && dpzdf <= 0 ) {
					CategoryTextAnnotation cpa  = new CategoryTextAnnotation ("\u21B1", wkfriday , cjlzb);
					//cpa.setBaseRadius(0.0);
					//cpa.setTipRadius(25.0);
//					cpa.setFont(new Font("SansSerif", Font.BOLD, 10));
					cpa.setPaint(Color.GREEN);
					cpa.setTextAnchor(TextAnchor.CENTER);
					super.plot.addAnnotation(cpa);
				}
				
			} else {
				if( !dapan.isDaPanXiuShi(wkfriday,period) ) {
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
					Integer gzjl = ((StockNodesXPeriodData)nodexdata).hasGzjlInPeriod(wkfriday);
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
						CategoryPointerAnnotation cpa = new CategoryPointerAnnotation(label, wkfriday , cjlzb, angle);
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
				Integer mrjl = ((StockNodesXPeriodData)nodexdata).hasMaiRuJiLuInPeriod(wkfriday);
				if(mrjl != null) {
					double angle; Color paintcolor;String label;
					angle = 30 * Math.PI/4;
					paintcolor = Color.RED;
					label = "mr";
					CategoryPointerAnnotation cpa = new CategoryPointerAnnotation(label, wkfriday , cjlzb, angle);
//					cpa.setBaseRadius(0.0);
//			        cpa.setTipRadius(25.0);
			        cpa.setFont(new Font("SansSerif", Font.BOLD, 10));
			        cpa.setPaint(paintcolor);
			        cpa.setTextAnchor(TextAnchor.CENTER);
					super.plot.addAnnotation(cpa);
				}
				Integer mcjl = ((StockNodesXPeriodData)nodexdata).hasMaiChuJiLuInPeriod(wkfriday);
				if(mcjl != null) {
					double angle; Color paintcolor;String label;
					angle = 30 * Math.PI/4;
					paintcolor = Color.GREEN.darker();
					label = "mc";
					CategoryPointerAnnotation cpa = new CategoryPointerAnnotation(label, wkfriday , cjlzb, angle);
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
		
//		标记大盘该周是涨还是跌
		tmpdate = requirestart;
		do {
			LocalDate wkfriday = tmpdate.with(DayOfWeek.FRIDAY);
			
			NodeXPeriodData dpnodexdata = dapan.getNodeXPeroidData(period);
			Double dpzdf = dpnodexdata.getSpecificOHLCZhangDieFu(wkfriday);
			if(dpzdf != null && dpzdf > 0 ) {
				CategoryTextAnnotation cpa  = new CategoryTextAnnotation ("\u2B08", wkfriday ,lowestLow * 0.7);
				//cpa.setBaseRadius(0.0);
				//cpa.setTipRadius(25.0);
//				cpa.setFont(new Font("SansSerif", Font.BOLD, 10));
				cpa.setPaint(Color.RED);
				cpa.setTextAnchor(TextAnchor.CENTER);
				super.plot.addAnnotation(cpa);
			} else if(dpzdf != null && dpzdf <= 0 ) {
				CategoryTextAnnotation cpa  = new CategoryTextAnnotation ("\u2B08", wkfriday , lowestLow * 0.7);
				//cpa.setBaseRadius(0.0);
				//cpa.setTipRadius(25.0);
//				cpa.setFont(new Font("SansSerif", Font.BOLD, 10));
				cpa.setPaint(Color.GREEN);
				cpa.setTextAnchor(TextAnchor.CENTER);
				super.plot.addAnnotation(cpa);
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
		
		Double[] zblevel = super.getCurDisplayedNode().getNodeJiBenMian().getNodeCjlZhanbiLevel();
		if(zblevel[0] != null && zblevel[0] < lowestLow)
			lowestLow = zblevel[0]; 
		if(zblevel[1] != null && zblevel[1] > highestHigh)
			highestHigh = zblevel[1];
		
		BanKuaiFengXiCategoryBarRenderer render0 = (BanKuaiFengXiCategoryBarRenderer)super.plot.getRenderer(0);
		render0.setDisplayNode(this.getCurDisplayedNode());
		render0.setDisplayNodeXPeriod (nodexdata);
		
		//如有分析结果，ticklable显示红色
		CategoryLabelCustomizableCategoryAxis axis = (CategoryLabelCustomizableCategoryAxis)super.plot.getDomainAxis(0);
		axis.setDisplayNode(this.getCurDisplayedNode(),period);
		
		try{
			super.plot.getRangeAxis(0).setRange(lowestLow * 0.7, highestHigh*1.12);
		} catch (java.lang.IllegalArgumentException e) {
			e.printStackTrace();
			super.plot.getRangeAxis(0).setRange(0, 1);
		}
		
		super.decorateXaxisWithYearOrMonth("month".trim());
		super.redecorateExtremeZhanbiLevel (zblevel);
	}

	public Double dipalyCjlZBLineDataToGui (NodeXPeriodData nodexdata,String period)
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
			Double cjlzb = nodexdata.getChenJiaoLiangZhanBi(wkfriday);
			
			if(cjlzb != null) {
				this.linechartdatasetforcjlzb.setValue(cjlzb,super.getRowKey(), wkfriday);
				
				if(cjlzb > highestHigh)
					highestHigh = cjlzb;
				if(cjlzb < lowestLow)
					lowestLow = cjlzb;
			} else if( !dapan.isDaPanXiuShi(tmpdate,period) ) 
				this.linechartdatasetforcjlzb.setValue(0.0,super.getRowKey(),wkfriday);

			if(period.equals(NodeGivenPeriodDataItem.WEEK))
				tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
			else if(period.equals(NodeGivenPeriodDataItem.DAY))
				tmpdate = tmpdate.plus(1, ChronoUnit.DAYS) ;
			else if(period.equals(NodeGivenPeriodDataItem.MONTH))
				tmpdate = tmpdate.plus(1, ChronoUnit.MONTHS) ;
			
		} while (tmpdate.isBefore( requireend) || tmpdate.isEqual(requireend));
		
		this.xiuShiRightRangeAxixAfterDispalyDate(nodexdata, highestHigh, lowestLow,  true);
		
		return highestHigh;
	}
	private void xiuShiRightRangeAxixAfterDispalyDate (NodeXPeriodData nodexdata,Double rightrangeaxixmax, Double rightrangeaxixmin,  Boolean forcetochange)
	{

		if(rightrangeaxixmax == 0)
			return;
		
		int row = super.linechartdataset.getRowCount();
		int column = super.linechartdataset.getColumnCount();
		
		Range rangeData = super.plot.getRangeAxis(3).getRange();
		if(forcetochange || row == 1) {
			
			super.plot.getRangeAxis(3).setRange(rightrangeaxixmin * 0.7, rightrangeaxixmax*1.12);
		} else {
					
			double upbound = rangeData.getUpperBound();
			try{
				if(rightrangeaxixmax*1.12 > upbound)
					super.plot.getRangeAxis(3).setRange(rightrangeaxixmin * 0.7, rightrangeaxixmax*1.12);
				else if (upbound >= rightrangeaxixmax * 200)  //原来的坐标过大，会导致小坐标的线根本看不到，也是要更换坐标的。
					super.plot.getRangeAxis(3).setRange(rightrangeaxixmin * 0.7, rightrangeaxixmax*1.12);
				
			} catch(java.lang.IllegalArgumentException e) {	super.plot.getRangeAxis(3).setRange(0, 1);}
		}
		
		super.plot.getRenderer(3).setSeriesPaint(0, new Color(0,102,0) );
		super.plot.getRenderer(3).setSeriesPaint(1, new Color(102,255,102) );
		super.plot.getRenderer(3).setSeriesPaint(2, Color.RED.darker() );
	}

	public void resetLineDate ()
	{
		super.resetLineDate();
		if(linechartdatasetforcjlzb != null)
			linechartdatasetforcjlzb.clear();
		
		plot.getRangeAxis(3).setRange(0.0, 1.0);
	}
	
	public void setDisplayZhanBiInLine (Boolean draw)
	{
		this.displayzhanbishujuinline = draw;
		if(this.displayzhanbishujuinline) 
			this.mntmCjlZblineDate.setText("X 占比柱图转线图");
		else
			this.mntmCjlZblineDate.setText("占比柱图转线图");
	}
	public void resetDate ()
	{
		super.resetDate();
		
		if(linechartdatasetforcjlzb != null)
			linechartdatasetforcjlzb.clear();
	}
	public Boolean shouldDisplayZhanBiInLine ()
	{
		if(displayzhanbishujuinline == null)
			return false;
		else
			return this.displayzhanbishujuinline;
	}

	@Override
	public String getToolTipSelected() 
	{
		int indexx = barchartdataset.getColumnIndex(super.dateselected);
		
		CustomCategroyToolTipGeneratorForCjlZhanBi ttpg = 	
		(CustomCategroyToolTipGeneratorForCjlZhanBi)(((CustomCategroyRendererForCjlZhanBi) plot.getRenderer()).getBaseToolTipGenerator());
		
		String tooltips = ttpg.generateToolTip(super.barchartdataset, 0, indexx);
		
		return tooltips;
	}

	@Override
	public void BanKuaiAndGeGuMatchingConditionValuesChanges(BanKuaiAndGeGuMatchingConditions expc) 
	{
//		Integer cjezbtoupleveldpmax = expc.getSettingDpMaxWk();
//		Double cjemin = expc.getSettingChenJiaoErMin();
//		Double cjemax = expc.getSettingChenJiaoErMax();
//		Integer cjemaxwk = expc.getSettingChenJiaoErMaxWk();
//		Double shoowhsl = expc.getSettingHuanShouLv();
		
	}
	

}
/*
 * 
 */
class CustomCategroyRendererForCjlZhanBi extends BanKuaiFengXiCategoryBarRenderer 
{
	private static final long serialVersionUID = 1L;
   
    public CustomCategroyRendererForCjlZhanBi() {
        super();
        super.displayedmaxwklevel = 4;
        super.displayedminwklevel = 8;
        try {
        	String readingsettinginprop  = super.prop.getProperty ("CjlZhanbiColumnColor");
            if(readingsettinginprop != null) {
            	super.displayedcolumncolorindex = Color.decode( readingsettinginprop );
                super.lastdisplayedcolumncolorindex = Color.decode( readingsettinginprop );
            } else {
            	super.displayedcolumncolorindex = Color.yellow;
                super.lastdisplayedcolumncolorindex = Color.yellow;
            }
        } catch (java.lang.NullPointerException e) {
        	super.displayedcolumncolorindex = Color.yellow;
            super.lastdisplayedcolumncolorindex = Color.yellow;
        }
           
        this.setBarPainter(new StandardBarPainter());
		
        BanKuaiFengXiCategoryBarToolTipGenerator custotooltip = new CustomCategroyToolTipGeneratorForCjlZhanBi();
		this.setBaseToolTipGenerator(custotooltip);
		
//		DecimalFormat decimalformate = new DecimalFormat("%#0.000");
//		this.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}",decimalformate));

		
		BkfxItemLabelGenerator labelgenerator = new BkfxItemLabelGeneratorForCjlZhanBi ();
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

    	Integer maxweek = nodexdata.getChenJiaoLiangZhanBiMaxWeekForDaPan (selecteddate);
    	Integer minweek = nodexdata.getChenJiaoLiangZhanBiMinWeekForDaPan (selecteddate);
    	
		if(maxweek != null  && maxweek >= super.displayedmaxwklevel)
			return Color.MAGENTA;
		else if (minweek != null && minweek != 0 && minweek >=  super.displayedminwklevel)
			return Color.GREEN;
		else
			return Color.black;
    }
    
}

class BkfxItemLabelGeneratorForCjlZhanBi extends BkfxItemLabelGenerator 
{
	public BkfxItemLabelGeneratorForCjlZhanBi ()
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
    	
		Integer maxweek = nodexdata.getChenJiaoLiangZhanBiMaxWeekForDaPan (selecteddate);
		Integer minweek = nodexdata.getChenJiaoLiangZhanBiMinWeekForDaPan (selecteddate);
		
		String result = "";
		if(maxweek != null && maxweek >= super.displayedmaxwklevel) {
//			NumberFormat nf = this.getNumberFormat();
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

class CustomCategroyToolTipGeneratorForCjlZhanBi extends BanKuaiFengXiCategoryBarToolTipGenerator 
{
	public String generateToolTip(CategoryDataset dataset, int row, int column)  
    {
		String selected = dataset.getColumnKey(column).toString();
    	LocalDate selecteddate = CommonUtility.formateStringToDate(selected);
    	
		String html;
		DaPan dapan = (DaPan)  super.node.getRoot();
		if(super.node.getType() == BkChanYeLianTreeNode.TDXGG ) {
			 html = nodexdata.getNodeXDataInHtml(dapan,selecteddate);
		} else {
			html = nodexdata.getNodeXDataInHtml(dapan,selecteddate);
		}
		
		return html;
    }
}