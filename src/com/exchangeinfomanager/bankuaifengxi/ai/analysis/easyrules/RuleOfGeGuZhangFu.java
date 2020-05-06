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
import com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForJFC.StockXPeriodDataForJFC;

@Rule(name = "GeGuZhangFu Rule", description = "if it rains then take an umbrella" )
public class RuleOfGeGuZhangFu 
{
	private String analysisresultforvoice = "";
	private Color foreground = Color.BLACK, background = Color.white;
	
	@Condition
	public boolean evaluate(@Fact("evanode") TDXNodes evanode, @Fact("evadate") LocalDate evadate, @Fact("evaperiod") String evaperiod,
    		@Fact("evacond") BanKuaiGeGuMatchCondition evacond ) 
	{
		Double zfmax = evacond.getSettingZhangFuMax();
    	Double zfmin = evacond.getSettingZhangFuMin();
    	
    	if(zfmax == null && zfmin == null ) {
    		return false;
    	} else
		if(zfmax == null && zfmin != null )
	    		zfmax = 1000000.0;
    	else 
    	if(zfmax != null && zfmin == null )
    		zfmin = -1000000.0;
    	else
    	if(zfmax == null && zfmin == null ) {
    		zfmin = 1000000.0;
    		zfmax = -1000000.0;
    	}
    	
    	StockXPeriodDataForJFC nodexdata = (StockXPeriodDataForJFC)evanode.getNodeXPeroidData(evaperiod);//   bk.getStockXPeriodDataForABanKuai(stockofbank.getMyOwnCode(), period);
//		OHLCItem ohlcdata = ((TDXNodesXPeriodDataForJFC)nodexdata).getSpecificDateOHLCData (evadate,0);
		Double wkzhangfu = nodexdata.getSpecificTimeHighestZhangDieFu(evadate, 0);
		Double wkdiefu = nodexdata.getSpecificTimeLowestZhangDieFu(evadate, 0);
		if(wkdiefu != null && wkdiefu < -0.09)
			analysisresultforvoice = analysisresultforvoice + "本周有大跌百分之" +  Math.floor(wkdiefu * 100);
		
		if(zfmax != null || zfmin != null ) {
			if(wkzhangfu == null)
		    	return false;
		    else if( wkzhangfu >= zfmin && wkzhangfu <= zfmax ) {
		    	analysisresultforvoice = analysisresultforvoice + "本周最高涨幅百分之" +  Math.floor(wkzhangfu * 100); 
		    	
		    	return true;
		    }
		    else
		    	return false;
		} else
			return false;
	}
	
	@Action
    public void execute(@Fact("evanode") TDXNodes evanode, @Fact("evadate") LocalDate evadate, @Fact("evaperiod") String evaperiod,
    		@Fact("evacond") BanKuaiGeGuMatchCondition evacond )
    {
		background = Color.pink ;
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
        return "GeGuZhangFu Rule";
    }

}
