package com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules;

import java.awt.Color;
import java.time.LocalDate;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Priority;
import org.jeasy.rules.annotation.Rule;
import org.jfree.data.time.ohlc.OHLCItem;

import com.exchangeinfomanager.bankuaifengxi.BanKuaiGeGuMatchCondition;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForJFC.StockXPeriodDataForJFC;
import com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForJFC.TDXNodesXPeriodDataForJFC;

@Rule(name = "GeGuPrice Rule", description = "if it rains then take an umbrella" )
public class RuleOfGeGuPrice
{
	private Color foreground = Color.BLACK, background = Color.white;
	private String analysisresultforvoice = "";
	
	@Condition
	public boolean evaluate(@Fact("evanode") TDXNodes evanode, 
			@Fact("evadate") LocalDate evadate, @Fact("evadatedifference") Integer evadatedifference, 
			@Fact("evaperiod") String evaperiod,
    		@Fact("evacond") BanKuaiGeGuMatchCondition evacond ) 
	{
		Double pricemin = evacond.getSettingSotckPriceMin();
    	Double pricemax = evacond.getSettingSotckPriceMax();
    	
    	if(pricemin == null && pricemax == null ) {
    		return false;
    	} else
		if(pricemax == null && pricemin != null )
			pricemax = 1000000.0;
    	else 
    	if(pricemax != null && pricemin == null )
    		pricemin = -1000000.0;
    	else
    	if(pricemax == null && pricemin == null ) {
    		pricemin = 1000000.0;
    		pricemax = -1000000.0;
    	}
    	
    	StockXPeriodDataForJFC nodexdata = (StockXPeriodDataForJFC)evanode.getNodeXPeroidData(evaperiod);//   bk.getStockXPeriodDataForABanKuai(stockofbank.getMyOwnCode(), period);
		OHLCItem ohlcdata = ((TDXNodesXPeriodDataForJFC)nodexdata).getSpecificDateOHLCData (evadate,0);
	    Double close = ohlcdata.getCloseValue();
	    
	    if(pricemin != null || pricemax != null ) {
			if(close == null)
				return false;
			else 
			if(close >= pricemin && close <= pricemax)
				return true;
			else
				return false;
		} 
	    
	    return false;
	}
	
	@Action
    public void execute(@Fact("evanode") TDXNodes evanode,
    		@Fact("evadate") LocalDate evadate,@Fact("evadatedifference") Integer evadatedifference,  
    		@Fact("evaperiod") String evaperiod,
    		@Fact("evacond") BanKuaiGeGuMatchCondition evacond )
    {
		foreground = Color.blue;
    }
    
    @Priority //优先级注解：return 数值越小，优先级越高
    public int getPriority(){
        return 1;
    }
    
    public Color getForeGround ()
    {
    	return this.foreground;
    }
    
    public String getAnalysisResult ()
    {
    	return this.analysisresultforvoice;
    }

    // MUST IMPLEMENT THIS METHOD
//    @Override 
    public String getName() {
        return "GeGuPrice Rule";
    }

}
