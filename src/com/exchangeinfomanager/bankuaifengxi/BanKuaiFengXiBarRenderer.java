package com.exchangeinfomanager.bankuaifengxi;

import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;

import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;

public class  BanKuaiFengXiBarRenderer extends BarRenderer
{

	public BanKuaiFengXiBarRenderer() 
	{
		super ();
		
	}
	
	private static final long serialVersionUID = 1L;
//	private Paint[] colors;
    protected int shouldcolumn = -1;
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
 

}
