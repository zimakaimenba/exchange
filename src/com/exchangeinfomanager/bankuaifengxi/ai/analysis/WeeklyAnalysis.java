package com.exchangeinfomanager.bankuaifengxi.ai.analysis;

import java.awt.Color;
import java.time.LocalDate;

import javax.swing.JLabel;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jfree.data.time.ohlc.OHLCItem;

import com.exchangeinfomanager.bankuaifengxi.CandleStick.CandleStickColorFactory;
import com.exchangeinfomanager.bankuaifengxi.HighlightAndExportNodes.BanKuaiAndGeGuMatchingConditions;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfCjeZbDpMaxWk;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfGeGuPrice;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfLiuTongShiZhi;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfMA;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfQueKou;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfWeeklyAverageChenJiaoErMaxWk;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.StockNodesXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForJFC.StockXPeriodDataForJFC;
import com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForJFC.TDXNodesXPeriodDataForJFC;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.nodes.treerelated.NodesTreeRelated;
import com.google.common.base.Strings;
import com.google.common.collect.Range;

public class WeeklyAnalysis 
{

	public static String BanKuaiGeGuMatchConditionAnalysis (TDXNodes node, LocalDate requireddate, BanKuaiAndGeGuMatchingConditions matchcond)
	{
			String analysisresult = "";
		
		 	Facts facts = new Facts();
	     	facts.put("evanode", node);
	     	facts.put("evadate", requireddate);
	     	facts.put("evadatedifference", 0);
	        facts.put("evaperiod", NodeGivenPeriodDataItem.WEEK);
	        facts.put("evacond", matchcond);
	        
	        Rules rules = new Rules();
	        RuleOfLiuTongShiZhi ltszRule =  new RuleOfLiuTongShiZhi ();
	        rules.register(ltszRule);
	        
	        RuleOfGeGuZhangFu zfRule = new RuleOfGeGuZhangFu ();
	        rules.register(zfRule);
	        
	        RuleOfGeGuPrice priceRule = new RuleOfGeGuPrice ();
	        rules.register(priceRule);
	        
	        RuleOfQueKou qkRule = new RuleOfQueKou ();
	        rules.register(qkRule);
	        
	        RuleOfCjeZbDpMaxWk dpmaxwkRule = new RuleOfCjeZbDpMaxWk ();
	        rules.register(dpmaxwkRule);
	        
	        RuleOfWeeklyAverageChenJiaoErMaxWk averagecjemaxwkRule = new RuleOfWeeklyAverageChenJiaoErMaxWk ();
	        rules.register(averagecjemaxwkRule);
	        
	        RuleOfMA maRule = new RuleOfMA ();
	        rules.register(maRule);
	     // fire rules on known facts
	        RulesEngine rulesEngine = new DefaultRulesEngine();
	        rulesEngine.fire(rules, facts);	
	        
	        analysisresult = analysisresult + maRule.getAnalysisResult(); //MA分析
	        if(!Strings.isNullOrEmpty( dpmaxwkRule.getAnalysisResultVoice()  )  ) {
	        	analysisresult = analysisresult + dpmaxwkRule.getAnalysisResultVoice() ;
	        }
	        if(!Strings.isNullOrEmpty( averagecjemaxwkRule.getAnalysisResultVoice()  )  ) {
	        	analysisresult = analysisresult + averagecjemaxwkRule.getAnalysisResultVoice() ;
	        }
	        if(!Strings.isNullOrEmpty( ltszRule.getAnalysisResultVoice()  )  ) {
	        	analysisresult = analysisresult + ltszRule.getAnalysisResultVoice() ;
	        }
	        if(!Strings.isNullOrEmpty( zfRule.getAnalysisResultVoice()  )  ) {
	        	analysisresult = analysisresult + zfRule.getAnalysisResultVoice() ;
	        }
	        if(!Strings.isNullOrEmpty( qkRule.getAnalysisResult()  )  ) {
	        	analysisresult = analysisresult + qkRule.getAnalysisResult() ;
	        }
		return node.getMyOwnName() + requireddate.getYear() + "年" + requireddate.getMonthValue() + "月" + requireddate.getDayOfMonth() + "日"
	        + analysisresult;
	}
}
