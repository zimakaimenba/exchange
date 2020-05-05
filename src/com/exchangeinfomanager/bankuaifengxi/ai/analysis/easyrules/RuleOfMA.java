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
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;
import com.google.common.base.Strings;

@Rule(name = "Moving Average Rule", description = "if it rains then take an umbrella" )
public class RuleOfMA 
{
	private Color foreground = Color.BLACK, background = Color.white;
	
	@Condition
	public boolean evaluate(@Fact("evanode") TDXNodes evanode, @Fact("evadate") LocalDate evadate, @Fact("evaperiod") String evaperiod,
    		@Fact("evacond") BanKuaiGeGuMatchCondition evacond ) 
	{
		NodeXPeriodData nodexdataday = evanode.getNodeXPeroidData(NodeGivenPeriodDataItem.DAY);
		
		String displayma = evacond.getSettingMaFormula();
    	
    	if (!Strings.isNullOrEmpty(displayma)) {
		    Boolean checkresult = nodexdataday.checkCloseComparingToMAFormula(displayma,evadate,0);
		    if( checkresult != null && checkresult)
			    return true;
    	}
    	
    	return false;
	}
	
	@Action
    public void execute(@Fact("evanode") TDXNodes evanode, @Fact("evadate") LocalDate evadate, @Fact("evaperiod") String evaperiod,
    		@Fact("evacond") BanKuaiGeGuMatchCondition evacond )
    {
		background = new Color(0,153,153) ;
    }
    
    @Priority //优先级注解：return 数值越小，优先级越高
    public int getPriority(){
        return 1;
    }
    
    public Color getBackGround ()
    {
    	return this.background;
    }

    // MUST IMPLEMENT THIS METHOD
//    @Override 
    public String getName() {
        return "Weekly Average ChenJiaoEr Rule";
    }
}
