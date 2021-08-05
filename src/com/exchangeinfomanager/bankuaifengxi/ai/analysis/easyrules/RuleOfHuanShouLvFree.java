package com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules;

import java.awt.Color;
import java.time.LocalDate;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Priority;
import org.jeasy.rules.annotation.Rule;

import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.Core.Nodes.TDXNodes;
import com.exchangeinfomanager.Core.Nodexdata.NodexdataForJFC.StockXPeriodDataForJFC;
import com.exchangeinfomanager.bankuaifengxi.HighlightAndExportNodes.BanKuaiAndGeGuMatchingConditions;

@Rule(name = "HSLF Rule", description = "if it rains then take an umbrella" )
public class RuleOfHuanShouLvFree 
{

	private Color foreground = Color.BLACK, background = Color.white;
	boolean lianxufangliang = false;
	String analysisresultforvoice = "";
	boolean iszjezbdpmatched = false;
	@Condition
	public boolean evaluate(@Fact("evanode") TDXNodes evanode,
			@Fact("evadate") LocalDate evadate,  
			@Fact("evaperiod") String evaperiod,
    		@Fact("evacond") BanKuaiAndGeGuMatchingConditions evacond ) 
	{
		if(evacond == null) return false;
		
		if(evanode.getType() != BkChanYeLianTreeNode.TDXGG) return false;
		
		Double shouldhslmin = evacond.getSettingHuanShouLvFreeMin();
		Double shouldhslmax = evacond.getSettingHuanShouLvFreeMax();
		
		if(shouldhslmin == null && shouldhslmax == null ) return false;
		
		if(shouldhslmin == null) shouldhslmin = 0.0;
		if(shouldhslmax == null) shouldhslmax = 10000.0;

		StockXPeriodDataForJFC nodexdata = (StockXPeriodDataForJFC) evanode.getNodeXPeroidData(evaperiod);//   bk.getStockXPeriodDataForABanKuai(stockofbank.getMyOwnCode(), period);
	    Double hslf = nodexdata.getSpecificTimeHuanShouLvFree(evadate);
    	if(hslf != null && hslf >= shouldhslmin && hslf <= shouldhslmax) return true;
    	
    	return false;
	}
	
	@Action
    public void execute(@Fact("evanode") TDXNodes evanode, 
    		@Fact("evadate") LocalDate evadate,  
    		@Fact("evaperiod") String evaperiod,
    		@Fact("evacond") BanKuaiAndGeGuMatchingConditions evacond )
    {
		iszjezbdpmatched  = true;
		background = Color.BLUE.brighter() ;
    }
    
    @Priority //优先级注解：return 数值越小，优先级越高
    public int getPriority(){
        return 1;
    }
    
    public Color getBackGround ()
    {
    	return this.background;
    }
    public Boolean getRuleResult ()
    {
    	return iszjezbdpmatched;
    }
    public String getAnalysisResultVoice ()
    {
    	return this.analysisresultforvoice;
    }

    // MUST IMPLEMENT THIS METHOD
//    @Override 
    public String getName() {
        return "HUANSHOULVFREE";
    }
}
