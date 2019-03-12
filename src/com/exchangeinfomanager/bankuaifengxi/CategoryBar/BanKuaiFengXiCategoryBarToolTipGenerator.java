package com.exchangeinfomanager.bankuaifengxi.CategoryBar;

import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.data.category.CategoryDataset;

import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.nodexdata.NodeXPeriodDataBasic;


public class BanKuaiFengXiCategoryBarToolTipGenerator implements CategoryToolTipGenerator 
{
	protected TDXNodes node;
//  private static Logger logger = Logger.getLogger(CustomToolTipGeneratorForChenJiaoEr.class);
	protected NodeXPeriodDataBasic nodexdata;

	@Override
	public  String generateToolTip(CategoryDataset arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return null;
	}
	
    public void setDisplayNode (TDXNodes curdisplayednode) 
    {
    	this.node = curdisplayednode;
    }
    public void setDisplayNodeXPeriod(NodeXPeriodDataBasic nodexdata1) 
    {
		this.nodexdata = nodexdata1;
	}
    

}
