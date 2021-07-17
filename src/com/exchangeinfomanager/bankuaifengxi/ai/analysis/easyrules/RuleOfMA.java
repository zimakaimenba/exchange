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

import com.exchangeinfomanager.bankuaifengxi.HighlightAndExportNodes.BanKuaiAndGeGuMatchingConditions;
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
			@Fact("evadate") LocalDate evadate,  
			@Fact("evaperiod") String evaperiod,
    		@Fact("evacond") BanKuaiAndGeGuMatchingConditions evacond ) 
	{
		if( !BkChanYeLianTreeNode.isBanKuai(evanode) && evanode.getType() != BkChanYeLianTreeNode.TDXGG )
			return false;
		
		String displayma = evacond.getSettingMaFormula();
		if (Strings.isNullOrEmpty(displayma)) return false;
	
		NodeXPeriodData nodexdataday = evanode.getNodeXPeroidData(NodeGivenPeriodDataItem.DAY);
	    OHLCItem ohlcdata =  ((TDXNodesXPeriodDataForJFC)nodexdataday).getSpecificDateOHLCData (evadate);
		LocalDate expectdate = evadate;
		if(ohlcdata == null) { //日线有可能当日是停牌的，如果停牌，就找到本周有数据的最新天
			String nodeperiod = evaperiod;
			if( nodeperiod.equals(NodeGivenPeriodDataItem.DAY ) ) {
				DayOfWeek dayOfWeek = evadate.getDayOfWeek();
				int dayOfWeekIntValue = dayOfWeek.getValue();
				for(int i = 0;i < dayOfWeekIntValue;i++) { 
					expectdate = evadate.plus(0-i,ChronoUnit.DAYS);
					OHLCItem ohlcdataexpectdate =  ((TDXNodesXPeriodDataForJFC)nodexdataday).getSpecificDateOHLCData (expectdate);
					if(ohlcdataexpectdate != null) {
//						expectdate = adjustDate(expectdate);
						evadate = expectdate;
			    		break;
					}
			    }
			} else if( nodeperiod.equals(NodeGivenPeriodDataItem.WEEK ) ) { //一周没有数据，说明停牌，目前周线MA不怎么用，直接回F
				return false;
			}
		}
		
		Double[] dailyma = nodexdataday.getNodeOhlcMA (evadate);
		if (dailyma != null) {
			//用周K线的CLOSE
			TDXNodesXPeriodDataForJFC nodexdata = (TDXNodesXPeriodDataForJFC)evanode.getNodeXPeroidData(evaperiod);//   bk.getStockXPeriodDataForABanKuai(stockofbank.getMyOwnCode(), period);
			ohlcdata = ((TDXNodesXPeriodDataForJFC)nodexdata).getSpecificDateOHLCData (evadate);
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
		} else
			return true; //没有均线数据，可能是系统没有导入，所以就不还任何判断，默认为有。
		
	    Boolean checkresult = nodexdataday.checkCloseComparingToMAFormula(evadate, displayma);
	    if( checkresult != null && checkresult)	    	return true;
	    
    	return false;
	}
	
	@Action
    public void execute(@Fact("evanode") TDXNodes evanode,
    		@Fact("evadate") LocalDate evadate,  
    		@Fact("evaperiod") String evaperiod,
    		@Fact("evacond") BanKuaiAndGeGuMatchingConditions evacond )
    {
		analysisresult = true;
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
        return "RULE MA";
    }
    
    protected LocalDate adjustDate(LocalDate dateneedtobeadjusted)
	 {
		 LocalDate friday;
		 DayOfWeek dayofweek = dateneedtobeadjusted.getDayOfWeek();
		 if( dayofweek.equals(DayOfWeek.SUNDAY)  ) {
			friday = dateneedtobeadjusted.minus(2,ChronoUnit.DAYS);
			return friday;
		 } else if (  dayofweek.equals(DayOfWeek.SATURDAY)  ) {
			friday = dateneedtobeadjusted.minus(1,ChronoUnit.DAYS);
			return friday;
		 }
					
		return dateneedtobeadjusted;
	 }
}
