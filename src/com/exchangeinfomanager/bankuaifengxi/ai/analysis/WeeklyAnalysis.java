package com.exchangeinfomanager.bankuaifengxi.ai.analysis;

import java.awt.Color;
import java.time.LocalDate;

import javax.swing.JLabel;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jfree.data.time.ohlc.OHLCItem;

import com.exchangeinfomanager.bankuaifengxi.BanKuaiGeGuMatchCondition;
import com.exchangeinfomanager.bankuaifengxi.CandleStick.CandleStickColorFactory;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfCjeZbDpMaxWk;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfGeGuPrice;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfGeGuZhangFu;
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

	public static String BanKuaiGeGuMatchConditionAnalysis (TDXNodes node, LocalDate requireddate, BanKuaiGeGuMatchCondition matchcond)
	{
			String analysisresult = "";
		
		 	Facts facts = new Facts();
	     	facts.put("evanode", node);
	     	facts.put("evadate", requireddate);
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
	        
	        analysisresult = analysisresult + maRule.getAnalysisResult(); //MA����
	        if(!Strings.isNullOrEmpty( dpmaxwkRule.getAnalysisResult()  )  ) {
	        	analysisresult = analysisresult + dpmaxwkRule.getAnalysisResult() ;
	        }
	        if(!Strings.isNullOrEmpty( averagecjemaxwkRule.getAnalysisResult()  )  ) {
	        	analysisresult = analysisresult + averagecjemaxwkRule.getAnalysisResult() ;
	        }
	        if(!Strings.isNullOrEmpty( ltszRule.getAnalysisResult()  )  ) {
	        	analysisresult = analysisresult + ltszRule.getAnalysisResult() ;
	        }
	        if(!Strings.isNullOrEmpty( zfRule.getAnalysisResult()  )  ) {
	        	analysisresult = analysisresult + zfRule.getAnalysisResult() ;
	        }
	        if(!Strings.isNullOrEmpty( qkRule.getAnalysisResult()  )  ) {
	        	analysisresult = analysisresult + qkRule.getAnalysisResult() ;
	        }
		return node.getMyOwnName() + requireddate.getYear() + "��" + requireddate.getMonthValue() + "��" + requireddate.getDayOfMonth() + "��"
	        + analysisresult;
	}
}