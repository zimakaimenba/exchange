package com.exchangeinfomanager.bankuaifengxi.CategoryBar;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.text.DecimalFormat;
import java.time.LocalDate;

import org.apache.log4j.Logger;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.CategoryDataset;
import org.jfree.ui.TextAnchor;

import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;

public class  BanKuaiFengXiCategoryBarRenderer extends BarRenderer
{

	public BanKuaiFengXiCategoryBarRenderer() 
	{
		super ();
		
		this.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12,TextAnchor.HALF_ASCENT_CENTER));
        this.setItemLabelAnchorOffset(5);
//        this.setItemLabelsVisible(true);
        this.setBaseItemLabelsVisible(true);
//        renderer.setSeriesItemLabelsVisible(0, Boolean.TRUE);
        this.setMaximumBarWidth(.5);
        this.setMinimumBarLength(.5);
        this.setItemMargin(-2);
//        BarRenderer.setDefaultShadowsVisible(false);
        this.setDrawBarOutline(false);
        
        this.setBarPainter(new StandardBarPainter());
        this.setShadowVisible(false);
        
	}
	
	private static Logger logger = Logger.getLogger(BanKuaiFengXiCategoryBarRenderer.class);
	private static final long serialVersionUID = 1L;
//	private Paint[] colors;
    protected int shouldcolumn = -1;
    protected int shouldcolumnlast = -2;
    protected int displayedmaxwklevel ;
    protected int displayedminwklevel ;
//    private String barCharType;
	protected TDXNodes node;
	protected CategoryDataset chartdataset;
	protected NodeXPeriodData nodexdata;
	protected Color displayedcolumncolorindex;
	protected Color lastdisplayedcolumncolorindex;
	/*
	 * 
	 */
	public void setDisplayNodeXPeriod(NodeXPeriodData nodexdata1) 
    {
		this.nodexdata = nodexdata1;
		BanKuaiFengXiCategoryBarToolTipGenerator tooltipgenerator = (BanKuaiFengXiCategoryBarToolTipGenerator)this.getBaseToolTipGenerator();
    	tooltipgenerator.setDisplayNodeXPeriod(nodexdata1);
    	
    	BkfxItemLabelGenerator labelgenerator = (BkfxItemLabelGenerator)this.getBaseItemLabelGenerator();
    	labelgenerator.setDisplayNodeXPeriod(nodexdata1);
	}
	/*
	 * 
	 */
	public void setBarColumnShouldChangeColor (int column)
    {
		if(this.shouldcolumn != column) { //说明用户的选择有了改变，需要修改
			this.shouldcolumnlast = this.shouldcolumn;
			this.shouldcolumn = column;
		}
    }
	/*
	 * 
	 */
	public void resetBarColumnShouldChangeColor ()
	{
		this.shouldcolumn = -1;
	    this.shouldcolumnlast = -2;
	}
	/*
	 * 
	 */
    public void setDisplayNode (TDXNodes curdisplayednode) 
    {
    	this.node = curdisplayednode;
    	
    	try {
    		BanKuaiFengXiCategoryBarToolTipGenerator tooltipgenerator = (BanKuaiFengXiCategoryBarToolTipGenerator)this.getBaseToolTipGenerator();
    		tooltipgenerator.setDisplayNode(curdisplayednode);
    	} catch (java.lang.NullPointerException e) {
    		e.printStackTrace();
    		return;
    	}
    	
    }
    /*
     * 
     */
    public void setDisplayMaxwkLevel (int maxl) 
    {
    	this.displayedmaxwklevel = maxl;
    	BkfxItemLabelGenerator lbg = (BkfxItemLabelGenerator)this.getBaseItemLabelGenerator();
    	lbg.setDisplayedMaxWkLevel(maxl);
    }
    public void setDisplayMinwkLevel(Integer cjezbdporbkmin)
    {
    	this.displayedminwklevel = cjezbdporbkmin;
    	BkfxItemLabelGenerator lbg = (BkfxItemLabelGenerator)this.getBaseItemLabelGenerator();
    	lbg.setDisplayedMinWkLevel(cjezbdporbkmin);
		
	}
    /*
     * 用户可以自定义显示颜色
     */
	public void setBarDisplayedColor(Color colorindex) 
	{
		this.displayedcolumncolorindex = colorindex;
	}
	/*
	 * 
	 */
	public void hideBarMode ()
	{
//		BarRenderer.setDefaultShadowsVisible(false);
//		this.lastdisplayedcolumncolorindex = this.displayedcolumncolorindex;//先保存
		this.displayedcolumncolorindex = Color.WHITE;
	}
	public void unhideBarMode ()
	{
//		BarRenderer.setDefaultShadowsVisible(false);
		this.displayedcolumncolorindex = this.lastdisplayedcolumncolorindex; 
	}
	/*
	 * (non-Javadoc)
	 * @see org.jfree.chart.renderer.AbstractRenderer#getItemPaint(int, int)
	 */
	public Paint getItemPaint(final int row, final int column) 
    {
		 GradientPaint notwholeweekcolor = new GradientPaint(
		            0.0f, 0.0f, displayedcolumncolorindex, 
		            0.0f, 0.0f, new Color(64, 0, 0)
		        );
		 GradientPaint shouldcolumnlastnotwholeweekcolor = new GradientPaint(
		            0.0f, 0.0f, Color.blue.darker(), 
		            0.0f, 0.0f, new Color(64, 0, 0)
		        );
		 GradientPaint shouldcolumnnotwholeweekcolor = new GradientPaint(
		            0.0f, 0.0f, new Color (51,153,255), 
		            0.0f, 0.0f, new Color(64, 0, 0)
		        );
		 
		 CategoryPlot plot = getPlot ();
	     CategoryDataset dataset = plot.getDataset();
		 String selected =  dataset.getColumnKey(column).toString();
	     LocalDate selecteddate = CommonUtility.formateStringToDate(selected);
	     
	     Integer exchangesdaynumber = 5;
	     try{
	    	 exchangesdaynumber  = nodexdata.getExchangeDaysNumberForthePeriod(selecteddate,0);
	     } catch (java.lang.NullPointerException e) {
	    	 exchangesdaynumber = 5;
	     }
	     
	     	if(column == shouldcolumnlast && exchangesdaynumber == 5) //完整周
		    	return Color.blue.darker();
	     	else if(column == shouldcolumnlast && exchangesdaynumber != 5 ) //不完整周
	     		return shouldcolumnlastnotwholeweekcolor;
		    else if(column == shouldcolumn && exchangesdaynumber == 5)
		    	return new Color (51,153,255);
		    else if(column == shouldcolumn && exchangesdaynumber != 5)
		    	return shouldcolumnnotwholeweekcolor;
	        else  if(exchangesdaynumber != 5) {
	        	return notwholeweekcolor;
	        } else
	            return displayedcolumncolorindex;
   }
	
 

}

abstract class  BkfxItemLabelGenerator extends StandardCategoryItemLabelGenerator 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected BkChanYeLianTreeNode node;
	protected NodeXPeriodData nodexdata;
	protected int displayedmaxwklevel;
	protected int displayedminwklevel;
//	String decimalformate;
	public BkfxItemLabelGenerator (DecimalFormat decimalFormat)
	{
		super ("{2}",decimalFormat);
	}
	@Override
	abstract public String generateColumnLabel(CategoryDataset dataset, int arg1) ;

	@Override
	abstract public String generateLabel(CategoryDataset dataset, int row, int column) ;

	@Override
	abstract public String generateRowLabel(CategoryDataset arg0, int arg1) ;
	
	public void setDisplayNode (BkChanYeLianTreeNode curdisplayednode) 
    {
    	this.node = curdisplayednode;
    }
    public void setDisplayNodeXPeriod(NodeXPeriodData nodexdata1) 
    {
		this.nodexdata = nodexdata1;
	}
    public void setDisplayedMaxWkLevel (int maxwk)
    {
    	this.displayedmaxwklevel = maxwk;
    }
    public void setDisplayedMinWkLevel (int minwk)
    {
    	this.displayedminwklevel = minwk;
    }
}
