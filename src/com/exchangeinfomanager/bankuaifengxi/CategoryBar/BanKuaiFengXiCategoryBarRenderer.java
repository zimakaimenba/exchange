package com.exchangeinfomanager.bankuaifengxi.CategoryBar;

import java.awt.Color;
import java.awt.Paint;

import org.apache.log4j.Logger;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;

import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData;

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
    protected int displayedmaxwklevel = 4;
    private String barCharType;
	protected BkChanYeLianTreeNode node;
	protected CategoryDataset chartdataset;
	protected NodeXPeriodDataBasic nodexdata;
	protected Color displayedcolorindex;
	/*
	 * 
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
    	this.shouldcolumn = column;
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
 

}
