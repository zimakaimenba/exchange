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

import org.apache.log4j.Logger;
import org.jfree.chart.annotations.CategoryPointerAnnotation;
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
import com.exchangeinfomanager.nodes.stocknodexdata.StockNodesXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;

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
        super.plot.setRangeAxis(2, rangeaxis);
        
        createGuiAndEvents ();
	}
	
	
	private static Logger logger = Logger.getLogger(BanKuaiFengXiCategoryBarChartCjeZhanbiPnl.class);
	private DefaultCategoryDataset linechartdatasetforcjlzb;
	protected JMenuItem mntmCjlZblineDate;
	protected JMenuItem mntmClearLineData;
	private Boolean displayzhanbishujuinline = false;
	
	private void createGuiAndEvents () 
	{
		mntmCjlZblineDate = new JMenuItem("占比柱图转线图");
		chartPanel.getPopupMenu().add(mntmCjlZblineDate);
		
		mntmClearLineData = new JMenuItem("突出占比数据");
		chartPanel.getPopupMenu().add(mntmClearLineData);
		
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
		
		if(this.shouldDisplayZhanBiInLine() != null && this.shouldDisplayZhanBiInLine() ) {
			super.resetLineDate ();
			this.dipalyCjlZBLineDataToGui ( super.getCurDisplayedNode().getNodeXPeroidData(period),period );
		}
		
		super.barchart.setNotify(true);
	}
	/*
	 * 
	 */
	public Double displayBarDataToGui(NodeXPeriodData nodexdata, LocalDate startdate, LocalDate enddate,
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
			Double cjlzb = nodexdata.getChenJiaoLiangZhanBi(wkfriday, 0);
			if(cjlzb != null) {
				super.barchartdataset.setValue(cjlzb,super.getRowKey(),wkfriday );

				if(cjlzb > highestHigh)
					highestHigh = cjlzb;
				
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
				Integer mrjl = ((StockNodesXPeriodData)nodexdata).hasMaiRuJiLuInPeriod(wkfriday, 0);
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
				Integer mcjl = ((StockNodesXPeriodData)nodexdata).hasMaiChuJiLuInPeriod(wkfriday, 0);
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
		
		xiuShiLeftRangeAxixAfterDispalyDate (nodexdata,startdate,enddate,highestHigh,period);
		
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
	 
	private void xiuShiLeftRangeAxixAfterDispalyDate (NodeXPeriodData nodexdata,LocalDate startdate, LocalDate enddate, double highestHigh, String period)
	{
		if(highestHigh == 0.0)
			return;
		
		BanKuaiFengXiCategoryBarRenderer render0 = (BanKuaiFengXiCategoryBarRenderer)super.plot.getRenderer(0);
		render0.setDisplayNode(this.getCurDisplayedNode());
		render0.setDisplayNodeXPeriod (nodexdata);
		
//		BanKuaiFengXiCategoryCjlZhanbiLineRenderer render2 = (BanKuaiFengXiCategoryCjeZhanbiLineRenderer)super.plot.getRenderer(2);
//		render2.setDisplayNode(this.getCurDisplayedNode());
//		render2.setDisplayNodeXPeriod (nodexdata);
		
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

	public Double dipalyCjlZBLineDataToGui (NodeXPeriodData nodexdata,String period)
	{
		if(!this.shouldDisplayZhanBiInLine() ) 
			return null;
		
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
			Double cjlzb = nodexdata.getChenJiaoLiangZhanBi(wkfriday, 0);
			
			if(cjlzb != null) {
				this.linechartdatasetforcjlzb.setValue(cjlzb,super.getRowKey(), wkfriday);
				
				if(cjlzb > highestHigh)
					highestHigh = cjlzb;
			} else if( !dapan.isDaPanXiuShi(tmpdate,0,period) ) 
				this.linechartdatasetforcjlzb.setValue(0.0,super.getRowKey(),wkfriday);

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
		
//		CategoryItemRenderer fourthrenderer = plot.getRenderer(2);
//		((BanKuaiFengXiCategoryCjeZhanbiLineRenderer)fourthrenderer).setKLearningClusterBaseLineId (-1);
	}
	public Boolean shouldDisplayZhanBiInLine ()
	{
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
	public void BanKuaiGeGuMatchConditionValuesChanges(BanKuaiGeGuMatchCondition expc) 
	{
		Integer cjezbtoupleveldpmax = expc.getSettingDpMaxWk();
		Double cjemin = expc.getSettingChenJiaoErMin();
		Double cjemax = expc.getSettingChenJiaoErMax();
		Integer cjemaxwk = expc.getSettingChenJiaoErMaxWk();
		Double shoowhsl = expc.getSettingHuanShouLv();
	
//		Integer cjezbdporbkmax = expc.getSettinDpmaxwk();
//		Integer cjezbdporbkmin = expc.getSettingDpminwk();
//		Double cjemin = expc.getSettingCjemin();
//		Double cjemax = expc.getSettingCjeMax();
//		Integer cjemaxwk = expc.getSettingCjemaxwk();
//		Double shoowhsl = expc.getSettingHsl();
//		
//		if(cjezbdporbkmax != null) {
//			((BanKuaiFengXiCategoryBarRenderer)plot.getRenderer()).setDisplayMaxwkLevel (cjezbdporbkmax);
//			this.barchart.fireChartChanged();//必须有这句
//		}
//		if(cjezbdporbkmin != null) {
//			((BanKuaiFengXiCategoryBarRenderer)plot.getRenderer()).setDisplayMinwkLevel (cjezbdporbkmin);
//			this.barchart.fireChartChanged();//必须有这句
//		}
		
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

    	Integer maxweek = nodexdata.getChenJiaoLiangZhanBiMaxWeekOfSuperBanKuai(selecteddate,0);
    	Integer minweek = nodexdata.getChenJiaoLiangZhanBiMinWeekOfSuperBanKuai(selecteddate,0);
    	
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
    	
		Integer maxweek = nodexdata.getChenJiaoLiangZhanBiMaxWeekOfSuperBanKuai(selecteddate,0);
		Integer minweek = nodexdata.getChenJiaoLiangZhanBiMinWeekOfSuperBanKuai(selecteddate,0);
		
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
			 html = nodexdata.getNodeXDataInHtml(dapan,selecteddate, 0);
		} else {
			html = nodexdata.getNodeXDataInHtml(dapan,selecteddate, 0);
		}
		
		return html;
    }
}