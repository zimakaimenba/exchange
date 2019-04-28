package com.exchangeinfomanager.bankuaifengxi.CategoryBar;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;

import org.apache.log4j.Logger;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.DefaultCategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.ui.TextAnchor;

import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.nodexdata.NodeXPeriodDataBasic;

public class  BanKuaiFengXiCategoryBarRenderer extends BarRenderer
{

	public BanKuaiFengXiCategoryBarRenderer() 
	{
		super ();
		
		this.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12,TextAnchor.HALF_ASCENT_CENTER));
        this.setItemLabelAnchorOffset(5);
//        this.setItemLabelsVisible(true);
        this.setBaseItemLabelsVisible(true);
        this.setMaximumBarWidth(.5);
        this.setMinimumBarLength(.5);
        this.setItemMargin(-2);
        
        
//        BkfxItemLabelGenerator labelgenerator = new BkfxItemLabelGenerator ();
//		this.setBaseItemLabelGenerator(labelgenerator);
		
	}
	
	private static Logger logger = Logger.getLogger(BanKuaiFengXiCategoryBarRenderer.class);
	private static final long serialVersionUID = 1L;
//	private Paint[] colors;
    protected int shouldcolumn = -1;
    protected int shouldcolumnlast = -2;
    protected int displayedmaxwklevel ;
    protected int displayedminwklevel ;
    private String barCharType;
	protected TDXNodes node;
	protected CategoryDataset chartdataset;
	protected NodeXPeriodDataBasic nodexdata;
	protected Color displayedcolumncolorindex;
	/*
	 * 单个个股用这个
	 */
	public void setDisplayNodeXPeriod(NodeXPeriodDataBasic nodexdata1) 
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
    public void setDisplayNode (TDXNodes curdisplayednode) 
    {
    	this.node = curdisplayednode;
    	BanKuaiFengXiCategoryBarToolTipGenerator tooltipgenerator = (BanKuaiFengXiCategoryBarToolTipGenerator)this.getBaseToolTipGenerator();
    	tooltipgenerator.setDisplayNode(curdisplayednode);
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
	     	else if(column == shouldcolumnlast && exchangesdaynumber != 5) //不完整周
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

	protected BkChanYeLianTreeNode node;
	protected NodeXPeriodDataBasic nodexdata;
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
    public void setDisplayNodeXPeriod(NodeXPeriodDataBasic nodexdata1) 
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
