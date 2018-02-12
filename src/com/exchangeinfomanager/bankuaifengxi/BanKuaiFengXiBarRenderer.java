package com.exchangeinfomanager.bankuaifengxi;

import java.awt.Color;
import java.awt.Paint;

import org.apache.log4j.Logger;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;

import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;

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
	
	
	public void setBarColumnShouldChangeColor (int column)
    {
    	this.shouldcolumn = column;
    	
    }

	
	
    /*
     * ����bar��ռ�Ȼ��ǳɽ�������ʾ��ͬ��barcolor
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
