package com.exchangeinfomanager.asinglestockinfo;

import java.util.HashSet;

import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.TreeRelated;

public class SubBanKuai extends BkChanYeLianTreeNode {

	public SubBanKuai( String myowncode1,String name) {
		super(myowncode1,name);
		nodetype = BanKuaiAndStockBasic.SUBBK;
		
		super.nodetreerelated = new TreeRelated (this);
	}

	@Override
	public NodeXPeriodDataBasic getNodeXPeroidData(String period) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
