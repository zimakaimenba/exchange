package com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules;

import java.awt.Color;
import java.time.LocalDate;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Priority;
import org.jeasy.rules.annotation.Rule;

import com.exchangeinfomanager.bankuaifengxi.HighlightAndExportNodes.BanKuaiAndGeGuMatchingConditions;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForJFC.StockXPeriodDataForJFC;
import com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForJFC.TDXNodesXPeriodDataForJFC;

@Rule(name = "GeGuZhangFu Rule", description = "if it rains then take an umbrella" )
public class RuleOfGeGuDailyHighestZhangFuInWeek 
{
	private String analysisresultforvoice = "";
	private Color foreground = Color.BLACK, background = Color.white;
	private Boolean isgeguzhangfumatched = false;
	
	@Condition
	public boolean evaluate(@Fact("evanode") TDXNodes evanode,
			@Fact("evadate") LocalDate evadate,  
			@Fact("evaperiod") String evaperiod,
    		@Fact("evacond") BanKuaiAndGeGuMatchingConditions evacond ) 
	{
//		if(evanode.getType() == BkChanYeLianTreeNode.TDXBK )
//			return false;
		
		Double zfmax = evacond.getSettingDailyZhangDieFuMax();
    	Double zfmin = evacond.getSettingDailyZhangDieFuMin();
    	
    	if(zfmax == null && zfmin == null ) 
    		return false;
    	
		if(zfmax == null  ) zfmax = 1000000.0;
    	if( zfmin == null ) zfmin = -1000000.0;
    	
    	StockXPeriodDataForJFC nodexdata = null;
    	try { nodexdata = (StockXPeriodDataForJFC)evanode.getNodeXPeroidData(evaperiod);//   bk.getStockXPeriodDataForABanKuai(stockofbank.getMyOwnCode(), period);
    	} catch (java.lang.ClassCastException e) {return false;}
    	
//		OHLCItem ohlcdata = ((TDXNodesXPeriodDataForJFC)nodexdata).getSpecificDateOHLCData (evadate,0);
    	Double wkzhangdiefu = nodexdata.getSpecificOHLCZhangDieFu (evadate);
    	if(wkzhangdiefu != null)
    		analysisresultforvoice = analysisresultforvoice + "�����Ƿ��ٷ�֮" +  Math.floor(wkzhangdiefu * 100);
    	
		Double wkhighdiefu = nodexdata.getSpecificTimeLowestZhangDieFu(evadate);
		if(wkhighdiefu != null && wkhighdiefu < -0.09)
			analysisresultforvoice = analysisresultforvoice + "�����д���ٷ�֮" +  Math.floor(wkhighdiefu * 100);
		
		Double wkhighzhangfu = nodexdata.getSpecificTimeHighestZhangDieFu(evadate);
		
			if(wkhighzhangfu == null) return false;
		    else if( wkhighzhangfu >= zfmin && wkhighzhangfu <= zfmax ) {
		    	analysisresultforvoice = analysisresultforvoice + "��������Ƿ��ٷ�֮" +  Math.floor(wkhighzhangfu * 100); 
		    	return true;
		    }
		    else return false;
		 
	}
	
	@Action
    public void execute(@Fact("evanode") TDXNodes evanode, 
    		@Fact("evadate") LocalDate evadate, 
    		@Fact("evaperiod") String evaperiod,
    		@Fact("evacond") BanKuaiAndGeGuMatchingConditions evacond )
    {
		background = Color.pink ;
		isgeguzhangfumatched = true;
    }
    
    @Priority //���ȼ�ע�⣺return ��ֵԽС�����ȼ�Խ��
    public int getPriority(){
        return 1;
    }
    public Boolean getRuleResult ()
    {
    	return isgeguzhangfumatched;
    }
    public Color getBackGround ()
    {
    	return this.background;
    }
    
    public String getAnalysisResultVoice ()
    {
    	return this.analysisresultforvoice;
    }

    // MUST IMPLEMENT THIS METHOD
//    @Override 
    public String getName() {
        return "GeGu Highest ZhangFu Rule";
    }

}
