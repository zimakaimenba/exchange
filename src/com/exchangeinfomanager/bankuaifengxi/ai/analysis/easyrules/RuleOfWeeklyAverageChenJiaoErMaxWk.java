package com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules;

import java.awt.Color;
import java.time.LocalDate;

import org.apache.log4j.Logger;
import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Priority;
import org.jeasy.rules.annotation.Rule;

import com.exchangeinfomanager.Core.Nodes.TDXNodes;
import com.exchangeinfomanager.Core.Nodexdata.NodeXPeriodData;
import com.exchangeinfomanager.bankuaifengxi.HighlightAndExportNodes.BanKuaiAndGeGuMatchingConditions;
import com.exchangeinfomanager.bankuaifengxi.bankuaigegutable.BanKuaiGeGuBasicTable;

@Rule(name = "Weekly Average ChenJiaoEr Rule", description = "if it rains then take an umbrella" )
public class RuleOfWeeklyAverageChenJiaoErMaxWk 
{
	private static Logger logger = Logger.getLogger(RuleOfWeeklyAverageChenJiaoErMaxWk.class);
	
	private Color foreground = Color.BLACK, background = Color.white;
	private String analysisresultforvoice = "";
	private Boolean isweeklyavergecjemaxwkmatched = false;
	@Condition
	public boolean evaluate(@Fact("evanode") TDXNodes evanode, 
			@Fact("evadate") LocalDate evadate,  
			@Fact("evaperiod") String evaperiod,
    		@Fact("evacond") BanKuaiAndGeGuMatchingConditions evacond ) 
	{
		if(evacond == null)
			return false;
		
		NodeXPeriodData nodexdata = evanode.getNodeXPeroidData(evaperiod);
		int dpmaxwk;
		try { dpmaxwk = nodexdata.getAverageDailyChenJiaoErMaxWeek (evadate);
		} catch (java.lang.NullPointerException ex) { 
//			ex.printStackTrace();
			return false;
		}
    	
		int lianxuxjeflnum = nodexdata.getAverageDailyChenJiaoErLianXuFangLiangPeriodNumber(evadate);
		
    	Integer settingcjemaxwk = evacond.getSettingChenJiaoErMaxWkMin();
    	if(settingcjemaxwk == null) settingcjemaxwk =  10000000;
    	
    	if( dpmaxwk >=settingcjemaxwk && lianxuxjeflnum <2) {
    		analysisresultforvoice = analysisresultforvoice + "周平均成交额MAXWEEK等于" + dpmaxwk + "。" ;
    		background = Color.CYAN ;
    		return true;
    	}
    	else if( dpmaxwk >=settingcjemaxwk && lianxuxjeflnum >=2) {
    		analysisresultforvoice = analysisresultforvoice + "周平均成交额MAXWEEK等于" + dpmaxwk + "。" ;
    		background =  new Color (0,153,153);
    		return true;
    	}
    	else
    		 return false;
	}
	
	@Action
    public void execute(@Fact("evanode") TDXNodes evanode, 
    		@Fact("evadate") LocalDate evadate, 
    		@Fact("evaperiod") String evaperiod,
    		@Fact("evacond") BanKuaiAndGeGuMatchingConditions evacond )
    {
		isweeklyavergecjemaxwkmatched = true;
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
    	return isweeklyavergecjemaxwkmatched;
    }
    public String getAnalysisResultVoice ()
    {
    	return this.analysisresultforvoice;
    }

    // MUST IMPLEMENT THIS METHOD
//    @Override 
    public String getName() {
        return "Weekly Average ChenJiaoEr MAX WK Rule";
    }

}
