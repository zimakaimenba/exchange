package com.exchangeinfomanager.asinglestockinfo;

import java.util.HashSet;

public class SubBanKuai extends BkChanYeLianTreeNode {

	public SubBanKuai( String myowncode1,String name) {
		super(myowncode1,name);
		nodetype = BanKuaiAndStockBasic.SUBBK;
	}

//	public  void setParseFileStockSet (HashSet<String> parsefilestockset2)
//	 {
//	    	if(super.parsefilestockset == null)
//	    		this.parsefilestockset = new HashSet<String> ();
//	    	else
//	    		this.parsefilestockset.addAll(parsefilestockset2);
//	 }

	@Override
	public NodeXPeriodDataBasic getNodeXPeroidData(String period) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
