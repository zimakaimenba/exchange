package com.exchangeinfomanager.bankuaifengxi.CategoryBar;

import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.data.category.CategoryDataset;
//import org.jsoup.Jsoup;

import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic.NodeXPeriodDataBasic;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
//import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.NodeXPeriodData;

public class BanKuaiFengXiCategoryBarToolTipGenerator implements CategoryToolTipGenerator 
{
	protected BkChanYeLianTreeNode node;
//  private static Logger logger = Logger.getLogger(CustomToolTipGeneratorForChenJiaoEr.class);
	protected NodeXPeriodDataBasic nodexdata;

	@Override
	public  String generateToolTip(CategoryDataset arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return null;
	}
	
    public void setDisplayNode (BkChanYeLianTreeNode curdisplayednode) 
    {
    	this.node = curdisplayednode;
    }
    public void setDisplayNodeXPeriod(NodeXPeriodDataBasic nodexdata1) 
    {
		this.nodexdata = nodexdata1;
	}
    

}
