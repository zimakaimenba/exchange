package com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules;

import java.awt.Color;
import java.time.LocalDate;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Priority;
import org.jeasy.rules.annotation.Rule;


import com.exchangeinfomanager.bankuaifengxi.BanKuaiGeGuMatchCondition;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.StockNodesXPeriodData;

@Rule(name = "ChenJiaoEr Rule", description = "if it rains then take an umbrella" )
public class RuleOfLiuTongShiZhi //extends BasicRule //extends RuleOfNodeXdataBasic
{
	private Color foreground = Color.BLACK, background = Color.white; 
	private Boolean specialarea = false;
	private String analysisresultforvoice = "";
	@Condition
    public boolean evaluate(@Fact("evanode") TDXNodes evanode, 
    		@Fact("evadate") LocalDate evadate,
    		@Fact("evaperiod") String evaperiod,
    		@Fact("evacond") BanKuaiGeGuMatchCondition evacond ) 
	{
		Double ltszmin ;
	    Double ltszmax ;
	    try {
	    	ltszmax = evacond.getSettingLiuTongShiZhiMax().doubleValue() *  100000000;
	    } catch (Exception e) {
	    	ltszmax = 10000000000000.0;
	    }
	    try {
	    	ltszmin = evacond.getSettingLiuTongShiZhiMin() * 100000000;
	    } catch (Exception e) {
	    	ltszmin = 10000000000000.0;
	    }
	    LocalDate requireddate = evadate;
	    String period = evaperiod;
	    NodeXPeriodData nodexdata = evanode.getNodeXPeroidData(period);//   bk.getStockXPeriodDataForABanKuai(stockofbank.getMyOwnCode(), period);
	    Double curltsz = ((StockNodesXPeriodData)nodexdata).getSpecificTimeLiuTongShiZhi(requireddate, 0);
	    if(curltsz == null) //有时候周一网易的数据还没有导入，导致没有流通市值数据，先用上一周的数据顶一下，毕竟不会相差太大
	    	curltsz = ((StockNodesXPeriodData)nodexdata).getSpecificTimeLiuTongShiZhi(requireddate, -1);
	    try {
		    if( curltsz >= ltszmin && curltsz <= ltszmax ) {
		    	if(curltsz >= 1500000000.0 && curltsz <= 3900000000.0) { //对于这个范围内的个股要特别重视，特别显示
		    		specialarea = true ;
		    		analysisresultforvoice = analysisresultforvoice + "流通市值" + Math.floor(curltsz/100000000) + "亿。";
		    		return true;
		    	}
		    	else  {
		    		analysisresultforvoice = analysisresultforvoice + "流通市值" + Math.floor(curltsz/100000000) + "亿。";
		    		return true;
		    	}
		    }
		    else {
		    	analysisresultforvoice = analysisresultforvoice + "流通市值" + Math.floor(curltsz/100000000) + "亿。";
		    	return false;
		    }
	    } catch (java.lang.NullPointerException e) {
	    	return false;
	    }
    }
    
    @Action
    public void execute(@Fact("evanode") TDXNodes evanode, @Fact("evadate") LocalDate evadate, @Fact("evaperiod") String evaperiod,
    		@Fact("evacond") BanKuaiGeGuMatchCondition evacond )
    {
    	if(specialarea)
    		background = new Color(201,0,102) ;
    	else
    		background = Color.MAGENTA ;
    }
    
    @Priority //优先级注解：return 数值越小，优先级越高
    public int getPriority(){
        return 1;
    }
    
    public Color getBackGround ()
    {
    	return this.background;
    }
    
    public String getAnalysisResult ()
    {
    	return this.analysisresultforvoice;
    }

    // MUST IMPLEMENT THIS METHOD
//    @Override 
    public String getName() {
        return "ChenJiaoEr Rule";
    }

}
