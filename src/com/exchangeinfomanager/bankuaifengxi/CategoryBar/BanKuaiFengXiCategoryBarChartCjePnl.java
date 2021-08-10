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
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.TextAnchor;

import com.exchangeinfomanager.Core.Nodes.BanKuai;
import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.Core.Nodes.DaPan;
import com.exchangeinfomanager.Core.Nodes.StockOfBanKuai;
import com.exchangeinfomanager.Core.Nodes.TDXNodes;
import com.exchangeinfomanager.Core.Nodexdata.NodeXPeriodData;
import com.exchangeinfomanager.Core.Nodexdata.ohlcvaprimarydata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.Core.Trees.CreateExchangeTree;
import com.exchangeinfomanager.bankuaifengxi.HighlightAndExportNodes.BanKuaiAndGeGuMatchingConditions;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.commonlib.Season;

public class BanKuaiFengXiCategoryBarChartCjePnl extends BanKuaiFengXiCategoryBarChartPnl 
{

	/**
	 * 
	 */
	public BanKuaiFengXiCategoryBarChartCjePnl() 
	{
		super ("CJE");
		
		chartPanel.setHorizontalAxisTrace(true); //ʮ����ʾ
		
		super.plot.setRenderer(0,new CustomRendererForCje() );
        super.plot.setRenderer(3, new BanKuaiFengXiCategoryLineRenderer ());
        
		averagelinechartdataset = new DefaultCategoryDataset();
		super.plot.setDataset(4, averagelinechartdataset);
		super.plot.setRenderer(4, new BanKuaiFengXiCategoryLineRenderer () );
        ValueAxis rangeaxis = plot.getRangeAxis(0);
        super.plot.setRangeAxis(4, rangeaxis);
        super.plot.getRenderer(4).setSeriesPaint(1, Color.CYAN );
        
        createGuiAndEvents ();
	}
	private static final long serialVersionUID = 1L;
	protected  TDXNodes shouldDisplayBarOfSuperBanKuaiCjeInsteadOfSelfCje;
	private Boolean displayaveragedailycje = false;
	protected JMenuItem mntmHideZdt;
	protected JMenuItem mntmHideQueKouData;
	protected JMenuItem mntmAveDailyCjeLineData;
	protected JMenuItem mntmCompareAveCjeWithSpecificNode;
	
	private void createGuiAndEvents ()
	{
		mntmHideZdt = new JMenuItem("ͻ���ǵ�ͣ����");
		chartPanel.getPopupMenu().add(mntmHideZdt,0);
        mntmHideQueKouData = new JMenuItem("ͻ��ȱ������");
        chartPanel.getPopupMenu().add(mntmHideQueKouData,1);
        mntmAveDailyCjeLineData = new JMenuItem("ͻ������ƽ���ɽ���");
		chartPanel.getPopupMenu().add(mntmAveDailyCjeLineData,2);
		mntmCompareAveCjeWithSpecificNode = new JMenuItem("�Աȴ�������ƽ���ɽ���");
		chartPanel.getPopupMenu().add(mntmCompareAveCjeWithSpecificNode,3);
		
	   	mntmAveDailyCjeLineData.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent evt) {
					
					PropertyChangeEvent evtzd = new PropertyChangeEvent(this, BanKuaiFengXiCategoryBarChartPnl.AVERAGEDAILYCJE, getCurDisplayedNode().getMyOwnCode(), "averagedailycjeline" );
		            pcs.firePropertyChange(evtzd);
				}
				
			});
	    	
	    	mntmHideZdt.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent evt) {

					PropertyChangeEvent evtzd = new PropertyChangeEvent(this, BanKuaiFengXiCategoryBarChartPnl.DISPLAYZHANGDIETING, getCurDisplayedNode().getMyOwnCode(), "zhangdieting" );
		            pcs.firePropertyChange(evtzd);
				}
				
			});
	    	mntmHideQueKouData.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent evt) {
					
					PropertyChangeEvent evtqk = new PropertyChangeEvent(this, BanKuaiFengXiCategoryBarChartPnl.DISPLAYQUEKOUDATA, getCurDisplayedNode().getMyOwnCode(), "quekou" );
		            pcs.firePropertyChange(evtqk);
			
				}
				
			});
	    	
	    	mntmCompareAveCjeWithSpecificNode.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent evt) {
					PropertyChangeEvent evtzd = new PropertyChangeEvent(this, BanKuaiFengXiCategoryBarChartPnl.COMPAREAVERAGEDAILYCJEWITHDAPAN, getCurDisplayedNode().getMyOwnCode(), "compareaveragedailycjewithdapan" );
		            pcs.firePropertyChange(evtzd);
				}
				
			});
		
	}
	public void reDisplayNodeDataOnDirection(String forwardbackward)
	{
		long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(super.displaydatestarted,super.displaydateended);
		forwardbackward = forwardbackward.toUpperCase();
		switch (forwardbackward) {
		case "FORWARD":
			LocalDate newdisplaystart = super.displaydatestarted.minusDays(java.lang.Math.abs(daysBetween) ); //��Щ��Ʊ�����ɶ������Ǻܾ���ǰ�ģ�û����
			LocalDate newdisplayend = super.displaydateended.minusDays(java.lang.Math.abs(daysBetween)) ;
			this.updatedDate(super.getCurDisplayedNode(), newdisplaystart, newdisplayend, super.globeperiod);
			break;
		case "BACKWARD":
			LocalDate newdisplaystart1 = super.displaydatestarted.plusDays(java.lang.Math.abs(daysBetween) ); //��Щ��Ʊ�����ɶ������Ǻܾ���ǰ�ģ�û����
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
	 * 
	 */
	public void updatedDate (TDXNodes node, LocalDate startdate, LocalDate enddate,String period)
	{
		super.setCurDisplayNode( node,startdate, enddate, period );
		
		if(node.getType() == BkChanYeLianTreeNode.DAPAN) {
//	        mntmCjeCjlZblineDate.setEnabled(false);
//	        mntmClearLineData.setEnabled(false);
	        mntmCompareAveCjeWithSpecificNode.setEnabled(false);
		} else
			mntmCompareAveCjeWithSpecificNode.setEnabled(true);
		
		this.preparingdisplayDataToGui (node,startdate,enddate,period);
	}
	
	 private static Logger logger = Logger.getLogger(BanKuaiFengXiCategoryBarChartCjePnl.class);
	/*
	 * 
	 */
	private void preparingdisplayDataToGui (TDXNodes node,LocalDate startdate,LocalDate enddate,String period)
	{
		this.resetDate();
		super.barchart.setNotify(false);
		
		((BanKuaiFengXiCategoryBarRenderer)super.plot.getRenderer()).unhideBarMode();
		super.plot.getRangeAxis(0).setRange(0, 1);//�������꣬������������
		
		NodeXPeriodData nodexdata = node.getNodeXPeroidData(period);
		
		if(shouldDisplayBarOfSuperBanKuaiCjeInsteadOfSelfCje == null) { //��ʾnode�Լ��ĳɽ�����ɳɽ����ƽ���ɽ����𲻻�̫�󣬿�����ͬһ������ϵ��ʾ
			Double leftrangeaxix = displayBarDataToGui (nodexdata,startdate,enddate,period);
//			Double avecje = displayAverageDailyCjeOfWeekLineDataToGuiUsingLeftAxis(nodexdata,startdate,enddate,period); //��ʾnode������ƽ���ɽ������������
			
			if(super.shouldDrawQueKouLine()) {
				Integer qkmax = displayQueKouLineDataToGui(nodexdata,startdate,enddate,period);
			}
			if(super.shouldDrawZhangDieTingLine() ) {
				Integer zdt = displayZhangDieTingLineDataToGui(nodexdata,startdate,enddate,period);
			}
			if(this.shouldDrawAverageDailyCjeOfWeekLine() ) {
				((BanKuaiFengXiCategoryBarRenderer)super.plot.getRenderer()).unhideBarMode();
				 Double avecje2 = displayAverageDailyCjeOfWeekLineDataToGuiUsingLeftAxis(nodexdata,startdate,enddate,period);
			}
			
		} else { //��ʾ���̵�ƽ���ɽ�����̵�ƽ���ɽ���ȸ���ƽ���ɽ����ܶ࣬�����ö����ĳɽ�������ϵ��
			Double leftrangeaxix = displayAverageBarDataToGui (nodexdata,startdate,enddate,period);
			
			NodeXPeriodData nodexdataOfSuperBk = shouldDisplayBarOfSuperBanKuaiCjeInsteadOfSelfCje.getNodeXPeroidData(period);
//			Double avecje2 = displayAverageDailyCjeOfWeekLineDataToGuiUsingLeftAxis(nodexdataOfSuperBk,startdate,enddate,period);
			Double avecjeaxix = displayAverageDailyCjeOfWeekLineDataToGuiUsingRightAxix(nodexdataOfSuperBk ,startdate,enddate,period);
			((BanKuaiFengXiCategoryBarRenderer)super.plot.getRenderer()).setBarDisplayedColor( "CjeAverageColumnColor" );
		}
		
//		if(super.shouldDisplayZhanBiInLine() ) {
//			this.resetLineDate ();
//			this.dipalyCjeCjlZBLineDataToGui (nodexdata,startdate,enddate,period);
//		}
		super.highLightSpecificBarColumn(super.getCurSelectedDate());
		super.barchart.setNotify(true);
	}
	/*
	 *��ʾ��������ƽ������ 
	 */
	public Double displayAverageBarDataToGui(NodeXPeriodData nodexdata,LocalDate startdate,LocalDate enddate,String period)
	{
		DaPan dapan = (DaPan)CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getModel().getRoot(); //alwayse use tdx tree fro dapan;
		
		LocalDate requireend = enddate.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = startdate.with(DayOfWeek.SATURDAY);
		
		double highestHigh =0.0; //������ʾ��Χ
		
		LocalDate tmpdate = requirestart;
		do  {
			//����Ӧ�ø�������������ѡ���������ͣ�������Ϊ�������ߣ��Ͳ�ϸ����
			Double cje = nodexdata.getAverageDailyChengJiaoErOfWeek(tmpdate);
			LocalDate wkfriday = tmpdate.with(DayOfWeek.FRIDAY);
			if(cje != null) {
				barchartdataset.setValue( cje,super.getRowKey(), wkfriday);
				
				if(cje > highestHigh)
					highestHigh = cje;
				
				Double zhangdiefu = nodexdata.getSpecificOHLCZhangDieFu(tmpdate);
				if(zhangdiefu != null && zhangdiefu >0) {
					CategoryTextAnnotation cpa  = new CategoryTextAnnotation ("\u21B1", wkfriday , cje);
					//cpa.setBaseRadius(0.0);
					//cpa.setTipRadius(25.0);
					cpa.setFont(new Font("SansSerif", Font.BOLD, 10));
					cpa.setPaint(Color.RED);
					cpa.setTextAnchor(TextAnchor.CENTER);
					super.plot.addAnnotation(cpa);
				} else {
					CategoryTextAnnotation cpa  = new CategoryTextAnnotation ("\u21B1", wkfriday , cje);
					//cpa.setBaseRadius(0.0);
					//cpa.setTipRadius(25.0);
					cpa.setFont(new Font("SansSerif", Font.BOLD, 10));
					cpa.setPaint(Color.GREEN);
					cpa.setTextAnchor(TextAnchor.CENTER);
					super.plot.addAnnotation(cpa);
				}
			} else {
				if( !dapan.isDaPanXiuShi(tmpdate,period) ) {
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

//		��Ǵ��̸������ǻ��ǵ�
		tmpdate = requirestart;
		do {
			LocalDate wkfriday = tmpdate.with(DayOfWeek.FRIDAY);
			
			NodeXPeriodData dpnodexdata = dapan.getNodeXPeroidData(period);
			Double dpzdf = dpnodexdata.getSpecificOHLCZhangDieFu(wkfriday);
			if(dpzdf != null && dpzdf > 0 ) {
				CategoryTextAnnotation cpa  = new CategoryTextAnnotation ("\u2B08", wkfriday ,0.0);
				//cpa.setBaseRadius(0.0);
				//cpa.setTipRadius(25.0);
//				cpa.setFont(new Font("SansSerif", Font.BOLD, 10));
				cpa.setPaint(Color.RED);
				cpa.setTextAnchor(TextAnchor.CENTER);
				super.plot.addAnnotation(cpa);
			} else if(dpzdf != null && dpzdf <= 0 ) {
				CategoryTextAnnotation cpa  = new CategoryTextAnnotation ("\u2B08", wkfriday ,  0.0);
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

		xiuShiLeftRangeAxixAfterDispalyDate (nodexdata,startdate,enddate,highestHigh,period);
		
		return highestHigh;
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
		
		LocalDate requireend = enddate;//.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = startdate;//.with(DayOfWeek.SATURDAY);
		
		double highestHigh =0.0; //������ʾ��Χ
		
		LocalDate tmpdate = requirestart;
		do  {
			//����Ӧ�ø�������������ѡ���������ͣ�������Ϊ�������ߣ��Ͳ�ϸ����
			Double cje = nodexdata.getChengJiaoEr(tmpdate);
			LocalDate wkfriday = tmpdate.with(DayOfWeek.FRIDAY);
			if(cje != null) {
				barchartdataset.setValue(cje,super.getRowKey(), wkfriday);
				
				if(cje > highestHigh)
					highestHigh = cje;

				Double zhangdiefu = nodexdata.getSpecificOHLCZhangDieFu(tmpdate);
				if(zhangdiefu != null && zhangdiefu >0) {
					CategoryTextAnnotation cpa  = new CategoryTextAnnotation ("\u21B1", wkfriday , cje);
					//cpa.setBaseRadius(0.0);
					// cpa.setTipRadius(25.0);
					cpa.setFont(new Font("SansSerif", Font.BOLD, 10));
					cpa.setPaint(Color.RED);
					cpa.setTextAnchor(TextAnchor.CENTER);
					super.plot.addAnnotation(cpa);
				} else {
					CategoryTextAnnotation cpa  = new CategoryTextAnnotation ("\u21B1", wkfriday , cje);
					//cpa.setBaseRadius(0.0);
					//cpa.setTipRadius(25.0);
					cpa.setFont(new Font("SansSerif", Font.BOLD, 10));
					cpa.setPaint(Color.GREEN);
					cpa.setTextAnchor(TextAnchor.CENTER);
					super.plot.addAnnotation(cpa);
				}
			} else {
				if( !dapan.isDaPanXiuShi(tmpdate,period) ) {
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
	public Integer displayZhangDieTingLineDataToGui(NodeXPeriodData nodexdata, LocalDate startdate, LocalDate enddate, String period) 
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
		
		int zdtmax = 0;//������ʾ��Χ
		LocalDate tmpdate = requirestart; 
		do  {
			LocalDate wkfriday = tmpdate.with(DayOfWeek.FRIDAY);
			
			//ȱ�� Line Part
			if(super.getCurDisplayedNode().getType() != BkChanYeLianTreeNode.DAPAN) {
				Integer zttj = nodexdata.getZhangTingTongJi(tmpdate);
				Integer dttj = nodexdata.getDieTingTongJi(tmpdate);

				if(dttj != null) { //ȷ���ұ�����combined pnl��ʾͬ����ֵ
					super.linechartdataset.setValue(dttj, "DieTing", wkfriday );
					if (dttj > zdtmax)
						zdtmax = dttj ;
					if (zttj != null  && zttj > zdtmax)
						zdtmax = zttj;
				}	else
						if( !dapan.isDaPanXiuShi(tmpdate,period) )
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
	public Double dipalyCjeCjlZBLineDataToGui (NodeXPeriodData nodexdata,LocalDate startdate, LocalDate enddate, String period)
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
		
		Double highestHigh = 0.0;//������ʾ��Χ
		LocalDate tmpdate = requirestart; 
		do  {
			Double cje = nodexdata.getChengJiaoEr(tmpdate);
			LocalDate wkfriday = tmpdate.with(DayOfWeek.FRIDAY);
			if(cje != null) {
				super.linechartdataset.setValue(cje,super.getRowKey(), wkfriday);
				
				if(cje > highestHigh)
					highestHigh = cje;
			} else {
				if( !dapan.isDaPanXiuShi(tmpdate,period) ) 
					super.linechartdataset.setValue(0.0,super.getRowKey(),wkfriday);
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
	public Double displayAverageDailyCjeOfWeekLineDataToGuiUsingLeftAxis (NodeXPeriodData nodexdata,LocalDate startdate, LocalDate enddate, String period)
	{
		DaPan dapan;
		if(super.getCurDisplayedNode().getType() == BkChanYeLianTreeNode.BKGEGU) {
			BanKuai bk = ((StockOfBanKuai)super.getCurDisplayedNode() ).getBanKuai();
			dapan = (DaPan)bk.getRoot();
		} else
			dapan = (DaPan)(this.getCurDisplayedNode().getRoot());
		
//		if(super.barchartdataset.getColumnCount() ==0 )
//			return null;
		
		LocalDate indexrequirestart = startdate;
		LocalDate indexrequireend = enddate;
		
		LocalDate requireend = indexrequireend.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = indexrequirestart.with(DayOfWeek.SATURDAY);
		
		Double avecjeaxix = 0.0;
		LocalDate tmpdate = requirestart; 
		do  {
			LocalDate wkfriday = tmpdate.with(DayOfWeek.FRIDAY);
			
//			if(super.getCurDisplayedNode().getType() != BkChanYeLianTreeNode.DAPAN) {
				Double avecje =  nodexdata.getAverageDailyChengJiaoErOfWeek(wkfriday);
				
				if(avecje != null) {
					averagelinechartdataset.setValue( 5* avecje,"AverageDailyCje", wkfriday);
					
					if(5* avecje > avecjeaxix)
						avecjeaxix = 5*avecje;
				} else {
					if( !dapan.isDaPanXiuShi(tmpdate,period) ) 
						averagelinechartdataset.setValue(0.0,"AverageDailyCje",wkfriday);
				}
				
//			}
			
			if(period.equals(NodeGivenPeriodDataItem.WEEK))
				tmpdate = tmpdate.plus(1, ChronoUnit.WEEKS) ;
			else if(period.equals(NodeGivenPeriodDataItem.DAY))
				tmpdate = tmpdate.plus(1, ChronoUnit.DAYS) ;
			else if(period.equals(NodeGivenPeriodDataItem.MONTH))
				tmpdate = tmpdate.plus(1, ChronoUnit.MONTHS) ;
			
		} while (tmpdate.isBefore( requireend) || tmpdate.isEqual(requireend));
		
//		this.xiuShiRightRangeAxixAfterDispalyDate(nodexdata, avecjeaxix, true);
		xiuShiLeftRangeAxixAfterDispalyDate (nodexdata,startdate,enddate,avecjeaxix,period);
		
		return avecjeaxix;
	}
	/*
	 * 
	 */
	public Double displayAverageDailyCjeOfWeekLineDataToGuiUsingRightAxix (NodeXPeriodData nodexdata,LocalDate startdate, LocalDate enddate, String period)
	{
		DaPan dapan = (DaPan)CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getModel().getRoot(); //alwayse use tdx tree fro dapan;
		
		LocalDate indexrequirestart = startdate;
		LocalDate indexrequireend = enddate;//(LocalDate) barchartdataset.getColumnKey(barchartdataset.getColumnCount()-1);
		
		LocalDate requireend = indexrequireend.with(DayOfWeek.SATURDAY);
		LocalDate requirestart = indexrequirestart.with(DayOfWeek.SATURDAY);
		
		Double avecjeaxix = 0.0;
		LocalDate tmpdate = requirestart; 
		do  {
			LocalDate wkfriday = tmpdate.with(DayOfWeek.FRIDAY);
			
			Double avecje = nodexdata.getAverageDailyChengJiaoErOfWeek(wkfriday);
			if(avecje != null) {
					super.linechartdataset.setValue(avecje,"AverageDailyCje", wkfriday);
					
					if(avecje > avecjeaxix)
						avecjeaxix = avecje;
					
//					Double dpzdf = nodexdata.getSpecificOHLCZhangDieFu(wkfriday);
//					if(dpzdf != null && dpzdf > 0 ) {
//						CategoryTextAnnotation cpa  = new CategoryTextAnnotation ("\u2B08", wkfriday ,avecje);
//						//cpa.setBaseRadius(0.0);
//						//cpa.setTipRadius(25.0);
////						cpa.setFont(new Font("SansSerif", Font.BOLD, 10));
//						cpa.setPaint(Color.RED);
//						cpa.setTextAnchor(TextAnchor.CENTER);
//						super.plot.addAnnotation(cpa);
//					} else if(dpzdf != null && dpzdf <= 0 ) {
//						CategoryTextAnnotation cpa  = new CategoryTextAnnotation ("\u2B08", wkfriday , avecje);
//						//cpa.setBaseRadius(0.0);
//						//cpa.setTipRadius(25.0);
////						cpa.setFont(new Font("SansSerif", Font.BOLD, 10));
//						cpa.setPaint(Color.GREEN);
//						cpa.setTextAnchor(TextAnchor.CENTER);
//						super.plot.addAnnotation(cpa);
//					} 
				} else {
					if( !dapan.isDaPanXiuShi(tmpdate,period) ) 
						super.linechartdataset.setValue(0.0,"AverageDailyCje",wkfriday);
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
	 * ��ʾȱ������
	 */
	public Integer displayQueKouLineDataToGui (NodeXPeriodData nodexdata,LocalDate startdate, LocalDate enddate, String period) 
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
		
		int qkmax = 0;//������ʾ��Χ
		LocalDate tmpdate = requirestart; 
		do  {
			LocalDate wkfriday = tmpdate.with(DayOfWeek.FRIDAY);
			
			//ȱ�� Line Part
			if(super.getCurDisplayedNode().getType() != BkChanYeLianTreeNode.DAPAN) {
				Integer opneupquekou =  nodexdata.getQueKouTongJiOpenUp(wkfriday);
				Integer opendownquekou =  nodexdata.getQueKouTongJiOpenDown(wkfriday);
				Integer huibuupquekou = nodexdata.getQueKouTongJiHuiBuUp(wkfriday);
				Integer huibudowquekou =  nodexdata.getQueKouTongJiHuiBuDown(wkfriday);	
				 
				if(opendownquekou != null) {
					super.linechartdataset.setValue(opendownquekou, "QueKouOpenDown", wkfriday );
					if (opendownquekou > qkmax)
						qkmax = opendownquekou ;
				}	else
					if( !dapan.isDaPanXiuShi(wkfriday,period) )
						super.linechartdataset.setValue(0, "QueKouOpenDown", wkfriday );
				
				if(huibuupquekou != null) {
					super.linechartdataset.setValue(huibuupquekou, "QueKouHuiBuUpQk", wkfriday );
					if (huibuupquekou > qkmax)
						qkmax = huibuupquekou ;
				}	else
					if( !dapan.isDaPanXiuShi(tmpdate,period) )
						super.linechartdataset.setValue(0, "QueKouHuiBuUpQk", wkfriday );
				
				if(opneupquekou != null && opneupquekou > qkmax) //ȷ���ұ�����combined pnl��ʾͬ����ֵ
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
		
		Range curaxisrange = super.plot.getRangeAxis(0).getRange();
		double curaxisupper = curaxisrange.getUpperBound();
		if(curaxisupper > highestHigh*1.12)
			return;
		
		try{
			super.plot.getRangeAxis(0).setRange(0, highestHigh*1.12);
		} catch(java.lang.IllegalArgumentException e) {
//			e.printStackTrace();
			super.plot.getRangeAxis(0).setRange(0, 1);
		}
		
		setPanelTitle ("�ɽ���",enddate);
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
		if(forcetochange || row == 1) { //rowֻ��һ������������ȫΪ��ռ�ã�����ֱ�Ӹ���
			
			super.plot.getRangeAxis(3).setRange(0, rightrangeaxixmax*1.12);
		} else {
			double upbound = rangeData.getUpperBound();
			try{
				if(rightrangeaxixmax*1.12 > upbound)
					super.plot.getRangeAxis(3).setRange(0, rightrangeaxixmax*1.12);
				else if (upbound >= rightrangeaxixmax * 200)  //ԭ����������󣬻ᵼ��С������߸�����������Ҳ��Ҫ��������ġ�
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
		
		this.barchart.fireChartChanged();//���������
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
	public TDXNodes getSettingSpecificSuperBanKuai ()
	{
		return shouldDisplayBarOfSuperBanKuaiCjeInsteadOfSelfCje;
	}
	public void setDrawAverageDailyCjeOfWeekLine(Boolean draw) {
		this.displayaveragedailycje = draw;
	}
	public boolean shouldDrawAverageDailyCjeOfWeekLine() {
		// TODO Auto-generated method stub
		return this.displayaveragedailycje;
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
    	
        super.barchart.fireChartChanged();//���������
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

	/*
	 * 
	 */
	public void setDisplayBarOfSpecificBanKuaiCjeCjlInsteadOfSelfCjeCjl (TDXNodes supernode) 
	{
		this.shouldDisplayBarOfSuperBanKuaiCjeInsteadOfSelfCje = supernode;
	}
	@Override
	public void BanKuaiAndGeGuMatchingConditionValuesChanges(BanKuaiAndGeGuMatchingConditions expc) {
		Integer cjemaxwk = expc.getSettingChenJiaoErMaxWkMin();
				
		if(cjemaxwk != null) {
			((BanKuaiFengXiCategoryBarRenderer)plot.getRenderer()).setDisplayMaxwkLevel (cjemaxwk);
			this.barchart.fireChartChanged();//���������
		}
		
	}

}

class CustomRendererForCje extends BanKuaiFengXiCategoryBarRenderer 
{
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(CustomRendererForCje.class);
   
    public CustomRendererForCje() 
    {
        super();
        super.displayedmaxwklevel = 3;
        try {
        	String readingsettinginprop  = super.prop.getProperty ("CjeColumnColor");
            if(readingsettinginprop != null) {
            	super.displayedcolumncolorindex = Color.decode( readingsettinginprop );
                super.lastdisplayedcolumncolorindex = Color.decode( readingsettinginprop );
            } else {
            	super.displayedcolumncolorindex = Color.RED;
                super.lastdisplayedcolumncolorindex = Color.RED;
            }
        } catch (java.lang.NullPointerException e) {
        	super.displayedcolumncolorindex = Color.RED;
            super.lastdisplayedcolumncolorindex = Color.RED;
        }
        
        this.setBarPainter(new StandardBarPainter());
        
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
    	 
		Integer maxweek = nodexdata.getAverageDailyChenJiaoErMaxWeek(selecteddate);
		
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
    		maxweek = nodexdata.getAverageDailyChenJiaoErMaxWeek(selecteddate);
    	} catch ( java.lang.NullPointerException e) {
    		return null;
    	}
		
		String result = "";
		if(maxweek != null && maxweek >= super.displayedmaxwklevel) {
//			NumberFormat nf = this.getNumberFormat();
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
    			
		String html;
		DaPan dapan = (DaPan)  super.node.getRoot();
		if(super.node.getType() == BkChanYeLianTreeNode.TDXGG ) {
			 html = nodexdata.getNodeXDataInHtml(dapan,selecteddate);
		} else {
			html = nodexdata.getNodeXDataInHtml(dapan,selecteddate);
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
    		danwei = "��";
    	}  	else if(number >= 10000000 && number <100000000) {
//    		number = number / 10000000;
    		danwei = "ǧ��";
    	}  	else if(number >= 1000000 && number <10000000) {
//    		number = number / 1000000;
    		danwei = "����";
    	} else if(number >= 100000 && number <1000000) {
//    		number = number / 100000;
    		danwei = "ʮ��";
    	} else if(number >= 10000 && number <100000) {
//    		number = number / 10000;
    		danwei = "��";
    	}
    	
    	return danwei;
		
	}
}