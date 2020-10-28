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

@Rule(name = "ChenJiaoEr  Rule", description = "if it rains then take an umbrella" )
public class RuleOfChenJiaoEr 
{
	private Color foreground = Color.BLACK, background = Color.white;
	boolean lianxufangliang = false;
	String analysisresultforvoice = "";
	boolean iszjezbdpmatched = false;
	@Condition
	public boolean evaluate(@Fact("evanode") TDXNodes evanode,
			@Fact("evadate") LocalDate evadate, @Fact("evadatedifference") Integer evadatedifference, 
			@Fact("evaperiod") String evaperiod,
    		@Fact("evacond") BanKuaiGeGuMatchCondition evacond ) 
	{
		
		Double cjemax = evacond.getSettingChenJiaoErMax();
    	Double cjemin = evacond.getSettingChenJiaoErMin();
    	if(cjemin != null && cjemax == null) {
    		cjemax = 1000000000000.0;
		} else
		if(cjemax != null && cjemin == null) {
			cjemin  = 0.0;
		} else
		if(cjemin == null && cjemax == null) {
			cjemax = 1000000000000.0;
			cjemin = 1000000000000.0;
		}
    	cjemax = cjemax * 100000000.0;
		cjemin = cjemin * 100000000.0;
    
	    
	    NodeXPeriodData nodexdata = evanode.getNodeXPeroidData(evaperiod);//   bk.getStockXPeriodDataForABanKuai(stockofbank.getMyOwnCode(), period);
	    Double curcje = nodexdata.getChengJiaoEr(evadate, 0);
	    if( curcje >= cjemin && curcje <= cjemax ) 
	    	return true;
    	
    	return false;
	}
	
	@Action
    public void execute(@Fact("evanode") TDXNodes evanode, 
    		@Fact("evadate") LocalDate evadate, @Fact("evadatedifference") Integer evadatedifference, 
    		@Fact("evaperiod") String evaperiod,
    		@Fact("evacond") BanKuaiGeGuMatchCondition evacond )
    {
		iszjezbdpmatched  = true;
		background = Color.yellow ;
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
        return "ChenJiaoEr Rule";
    }

}
