package com.exchangeinfomanager.bankuaifengxi;

import java.awt.Color;
import java.awt.Paint;

import org.apache.log4j.Logger;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;

import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData;

public class  BanKuaiFengXiBarRenderer extends BarRenderer
{

	public BanKuaiFengXiBarRenderer() 
	{
		super ();
	}
	
	private static Logger logger = Logger.getLogger(BanKuaiFengXiBarRenderer.class);
	private static final long serialVersionUID = 1L;
//	private Paint[] colors;
    protected int shouldcolumn = -1;
    protected int displayedmaxwklevel = 4;
    private String barCharType;
	protected BkChanYeLianTreeNode node;
	protected CategoryDataset chartdataset;
	protected NodeXPeriodDataBasic nodexdata;
	
	public void setDisplayNodeXPeriod(NodeXPeriodDataBasic nodexdata1) 
    {
		this.nodexdata = nodexdata1;
	}
	
	public void setBarColumnShouldChangeColor (int column)
    {
    	this.shouldcolumn = column;
    }
    /*
     * 设置bar是占比还是成交量，显示不同的barcolor
     */
//    public void setBarCharType(String type) 
//    {
//    	this.barCharType = type;
//    }
	
    public void setDisplayNode (BkChanYeLianTreeNode curdisplayednode) 
    {
    	this.node = curdisplayednode;
    }
    
    public void setDateSet (CategoryDataset dataset)
    {
    	this.chartdataset = dataset;
    }
    public void setDisplayMaxwkLevel (int maxl) 
    {
    	this.displayedmaxwklevel = maxl;
    }
 

}
