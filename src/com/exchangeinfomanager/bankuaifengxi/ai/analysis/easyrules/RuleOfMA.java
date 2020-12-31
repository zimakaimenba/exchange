package com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules;

import java.awt.Color;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Priority;
import org.jeasy.rules.annotation.Rule;
import org.jfree.data.time.ohlc.OHLCItem;

import com.exchangeinfomanager.bankuaifengxi.BanKuaiGeGuMatchCondition;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForJFC.StockXPeriodDataForJFC;
import com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForJFC.TDXNodesXPeriodDataForJFC;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;
import com.google.common.base.Strings;

@Rule(name = "Moving Average Rule", description = "if it rains then take an umbrella" )
public class RuleOfMA 
{
	private Color foreground = Color.BLACK, background = Color.white;
	private String analysisresultforvoice = "";
	
	@Condition
	public boolean evaluate(@Fact("evanode") TDXNodes evanode, 
			@Fact("evadate") LocalDate evadate, @Fact("evadatedifference") Integer evadatedifference, 
			@Fact("evaperiod") String evaperiod,
    		@Fact("evacond") BanKuaiGeGuMatchCondition evacond ) 
	{
		if(evanode.getType() == BkChanYeLianTreeNode.TDXBK)
			return false;
		
		NodeXPeriodData nodexdataday = evanode.getNodeXPeroidData(NodeGivenPeriodDataItem.DAY);
		DayOfWeek dayOfWeek = evadate.getDayOfWeek();
	    int dayOfWeekIntValue = dayOfWeek.getValue();
	    Double[] dailyma = null;
	    for(int i = 0;i < dayOfWeekIntValue;i++) { //日线有可能当日是停牌的，如果停牌，就找到本周有数据的最新天
	    	dailyma = nodexdataday.getNodeOhlcMA (evadate,0-i);
	    	if(dailyma != null)
	    		break;
	    }
		
		if (dailyma != null) {
//			OHLCItem ohlcdata = ((TDXNodesXPeriodDataForJFC)nodexdataday).getSpecificDateOHLCData (evadate,0);
//			Double close = ohlcdata.getCloseValue();
			//用周K线的CLOSE
			StockXPeriodDataForJFC nodexdata = (StockXPeriodDataForJFC)evanode.getNodeXPeroidData(evaperiod);//   bk.getStockXPeriodDataForABanKuai(stockofbank.getMyOwnCode(), period);
			OHLCItem ohlcdata = ((TDXNodesXPeriodDataForJFC)nodexdata).getSpecificDateOHLCData (evadate,0);
		    Double close = ohlcdata.getCloseValue();

		    analysisresultforvoice = analysisresultforvoice + "周收盘价" + close;
			if(dailyma[4] != null && close >= dailyma[4])
				analysisresultforvoice = analysisresultforvoice + "股价大于60均线。";
			else
				analysisresultforvoice = analysisresultforvoice + "股价小于60均线。";
			if(dailyma[6] != null  && close >= dailyma[6])
				analysisresultforvoice = analysisresultforvoice + "股价大于MA250。";
			else
				analysisresultforvoice = analysisresultforvoice + "股价小于MA250。";
		}
		
		String displayma = evacond.getSettingMaFormula();
    	if (!Strings.isNullOrEmpty(displayma)) {
		    Boolean checkresult = nodexdataday.checkCloseComparingToMAFormula(displayma,evadate,0);
		    if( checkresult != null && checkresult) {
		    	return true;
		    }
    	}
    	
    	return false;
	}
	
	@Action
    public void execute(@Fact("evanode") TDXNodes evanode,
    		@Fact("evadate") LocalDate evadate, @Fact("evadatedifference") Integer evadatedifference, 
    		@Fact("evaperiod") String evaperiod,
    		@Fact("evacond") BanKuaiGeGuMatchCondition evacond )
    {
		background = new Color(0,153,153) ;
    }
    
    @Priority //优先级注解：return 数值越小，优先级越高
    public int getPriority()
    {
        return 1;
    }
    
    public Color getBackGround ()
    {
    	return this.background;
    }
    private Boolean analysisresult = false;
	public Boolean getAnalysisResult ()
    {
    	return this.analysisresult;
    }
    
    public String getAnalysisResultVoice ()
    {
    	return this.analysisresultforvoice;
    }

    // MUST IMPLEMENT THIS METHOD
//    @Override 
    public String getName() {
        return "Weekly Average ChenJiaoEr Rule";
    }
}
