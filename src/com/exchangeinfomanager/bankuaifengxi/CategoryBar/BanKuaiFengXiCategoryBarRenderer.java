package com.exchangeinfomanager.bankuaifengxi.CategoryBar;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.time.LocalDate;

import org.apache.log4j.Logger;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;

import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData;
import com.exchangeinfomanager.commonlib.CommonUtility;

public class  BanKuaiFengXiCategoryBarRenderer extends BarRenderer
{

	public BanKuaiFengXiCategoryBarRenderer() 
	{
		super ();
	}
	
	private static Logger logger = Logger.getLogger(BanKuaiFengXiCategoryBarRenderer.class);
	private static final long serialVersionUID = 1L;
//	private Paint[] colors;
    protected int shouldcolumn = -1;
    protected int shouldcolumnlast = -2;
    protected int displayedmaxwklevel = 4;
    private String barCharType;
	protected BkChanYeLianTreeNode node;
	protected CategoryDataset chartdataset;
	protected NodeXPeriodDataBasic nodexdata;
	protected Color displayedcolorindex;
	/*
	 * 单个个股用这个
	 */
	public void setDisplayNodeXPeriod(NodeXPeriodDataBasic nodexdata1) 
    {
		this.nodexdata = nodexdata1;
		BanKuaiFengXiCategoryBarToolTipGenerator tooltipgenerator = (BanKuaiFengXiCategoryBarToolTipGenerator)this.getBaseToolTipGenerator();
    	tooltipgenerator.setDisplayNodeXPeriod(nodexdata1);
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
    public void setDisplayNode (BkChanYeLianTreeNode curdisplayednode) 
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
    }
    /*
     * 用户可以自定义显示颜色
     */
	public void setBarDisplayedColor(Color colorindex) 
	{
		this.displayedcolorindex = colorindex;
	}
	/*
	 * (non-Javadoc)
	 * @see org.jfree.chart.renderer.AbstractRenderer#getItemPaint(int, int)
	 */
	public Paint getItemPaint(final int row, final int column) 
    {
		 GradientPaint notwholeweekcolor = new GradientPaint(
		            0.0f, 0.0f, displayedcolorindex, 
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
	            return displayedcolorindex;
		 
//	    if(column == shouldcolumnlast)
//	    	return Color.blue.darker();
//	    else if(column == shouldcolumn)
//	    	return new Color (51,153,255);
//        else  if(exchangesdaynumber != 5) {
//        	return notwholeweekcolor;
//        } else
//            return displayedcolorindex;
   }
 

}
