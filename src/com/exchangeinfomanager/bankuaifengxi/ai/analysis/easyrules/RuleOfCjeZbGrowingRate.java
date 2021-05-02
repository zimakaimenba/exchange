package com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules;


import java.awt.Color;
import java.time.LocalDate;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Priority;
import org.jeasy.rules.annotation.Rule;

import com.exchangeinfomanager.bankuaifengxi.HighlightAndExportNodes.BanKuaiGeGuMatchCondition;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;

@Rule(name = "ChenJiaoEr ZhanBi Growing Rate Rule", description = "if it rains then take an umbrella" )
public class RuleOfCjeZbGrowingRate 
{
	private Color foreground = Color.BLACK, background = Color.white;
	boolean checkresult = false;
	String analysisresultforvoice = "";
	
	@Condition
	public boolean evaluate(@Fact("evanode") TDXNodes evanode,
			@Fact("evadate") LocalDate evadate, @Fact("evadatedifference") Integer evadatedifference, 
			@Fact("evaperiod") String evaperiod,
    		@Fact("evacond") BanKuaiGeGuMatchCondition evacond ) 
	{
		NodeXPeriodData nodexdata = evanode.getNodeXPeroidData(evaperiod);
		Double cjedpzbgr = nodexdata.getChenJiaoErZhanBiGrowthRateOfSuperBanKuai(evadate,evadatedifference);
		if(cjedpzbgr!= null && cjedpzbgr > 0 ) {
			if(evacond.getCjezbGrowingRateMin() == null && evacond.getCjezbGrowingRateMax() == null)
				return false;
			
    		Double grmin = null; Double grmax = null;
	    	try {
	    		grmin = evacond.getCjezbGrowingRateMin() * 1 /100;
	    	} catch (java.lang.NullPointerException e) {
	    		grmin = -100000000.0;
	    	}
	    	try {
	    		grmax = evacond.getCjezbGrowingRateMax() * 1 /100;
	    	} catch (java.lang.NullPointerException e) {
	    		grmax = 100000000.0;
	    	}
	    	
	    	if(cjedpzbgr >= grmin && cjedpzbgr <= grmax) 	    		
	    		return true;
	    	else
	    		return false;
    	} 
    	
    	return false;
	}
	
	@Action
    public void execute(@Fact("evanode") TDXNodes evanode, 
    		@Fact("evadate") LocalDate evadate, @Fact("evadatedifference") Integer evadatedifference, 
    		@Fact("evaperiod") String evaperiod,
    		@Fact("evacond") BanKuaiGeGuMatchCondition evacond )
    {
		checkresult = true;
		foreground = Color.yellow;
    }
    
    @Priority //优先级注解：return 数值越小，优先级越高
    public int getPriority(){
        return 1;
    }
    
    public Color getForeGround ()
    {
    	return this.foreground;
    }
        
    public String getAnalysisResultVoiceMsg ()
    {
    	return this.analysisresultforvoice;
    }

    // MUST IMPLEMENT THIS METHOD
//    @Override 
    public String getName() {
        return "ChenJiaoEr ZhanBi Growing Rate Rules";
    }

}
