package com.exchangeinfomanager.bankuaifengxi.bankuaigegubasictable;

import java.awt.Color;
import java.time.LocalDate;

import javax.swing.table.DefaultTableCellRenderer;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;

import com.exchangeinfomanager.bankuaifengxi.BanKuaiGeGuMatchCondition;
import com.exchangeinfomanager.bankuaifengxi.CandleStick.CandleStickColorFactory;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfChenJiaoEr;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfCjeZbDpMaxWk;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfCjeZbGrowingRate;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfGeGuPrice;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfGeGuZhangFu;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfHuanShouLv;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfLiuTongShiZhi;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfMA;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfQueKou;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfWeeklyAverageChenJiaoErMaxWk;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.treerelated.NodesTreeRelated;

public class BanKuaiGeGuBasicTableRenderer extends DefaultTableCellRenderer
{
	protected BanKuaiGeGuBasicTableModel tablemodel;
	
	protected BanKuaiGeGuBasicTableRenderer () {
		super ();
	}
	
	/*
	 * 
	 */
	protected Color rendererOperationsForColumnForgroundHighLight ( StockOfBanKuai stockofbank, String column_keyword )
	{
		Color foreground = Color.BLACK;
		
		Stock stock = stockofbank.getStock();
		LocalDate requireddate = tablemodel.getShowCurDate();
	    String period = tablemodel.getCurDisplayPeriod();
	    BanKuaiGeGuMatchCondition matchcond = tablemodel.getDisplayMatchCondition ();
		
	    Facts lwfacts = new Facts();
        lwfacts.put("evanode", stock);
        lwfacts.put("evadate", requireddate);
        lwfacts.put("evadatedifference", -1);
        lwfacts.put("evaperiod", period);
        lwfacts.put("evacond", matchcond);
        
        Rules lwrules = new Rules();
        
        RuleOfCjeZbDpMaxWk lwdpmaxwkRule = new RuleOfCjeZbDpMaxWk ();
        lwrules.register(lwdpmaxwkRule);
        
        RuleOfWeeklyAverageChenJiaoErMaxWk lwaveragecjemaxwk = new RuleOfWeeklyAverageChenJiaoErMaxWk ();
        lwrules.register(lwaveragecjemaxwk);
        
        RuleOfCjeZbGrowingRate lwcjezbgr = new RuleOfCjeZbGrowingRate ();
        lwrules.register(lwcjezbgr);
        
        RulesEngine lwrulesEngine = new DefaultRulesEngine();
        try {
        	lwrulesEngine.fire(lwrules, lwfacts);
        } catch (Exception ex  ) {
        	ex.printStackTrace();
        }
        
        NodeXPeriodData nodexdata = stock.getNodeXPeroidData(period);
    	Double lwzhangdiefu = nodexdata.getSpecificOHLCZhangDieFu (requireddate,-1);
    	Double zhangdiefu = nodexdata.getSpecificOHLCZhangDieFu (requireddate,0);
    	
		switch (column_keyword) {
        case "quanzhonginbankuai":
	    	Integer weight = stockofbank.getBanKuai().getGeGuSuoShuBanKuaiWeight( stock.getMyOwnCode() );
	    	if(weight == null)
	    		foreground = Color.BLACK;
	    	else
	    	if(stockofbank.isBkLongTou())
	    		foreground = Color.RED;
	    	else 
	    	if(weight <6 && weight >3) 
	    		foreground = new Color(128,128,128);
	    	else 
	    	if(weight <=3 )
	    		foreground = new Color(192,192,192);
	    	else
	    		foreground = Color.BLACK;
        	break;
        
        case "lastwkdpcjezbgrowingrate":
        	foreground = lwcjezbgr.getForeGround();
        	break;
        case "lastwkdpcjezbmatch":
        	
	    	if(lwzhangdiefu != null &&  lwzhangdiefu <0 && lwdpmaxwkRule.getRuleResult() && zhangdiefu !=null &&  zhangdiefu >0 )
	    		foreground = Color.YELLOW;
        	break;
        case "lastwkaveragecjematch" :
	    	if(lwzhangdiefu != null && lwzhangdiefu <0 && lwaveragecjemaxwk.getRuleResult() && zhangdiefu != null && zhangdiefu >0 )
	    		foreground = Color.YELLOW;
        	break;
	    };
	    
	    return foreground;
	}
	/*
	 * 
	 */
	protected void rendererOperationsForColumnInfomation ( StockOfBanKuai stockofbank, String column_keyword ) 
	{
		switch (column_keyword) {
        case "":
        	
        	break;
	    };
		
	}
	/*
	 * 
	 */
	protected Color rendererOperationsForColumnBackgroundHighLight ( StockOfBanKuai stockofbank, String column_keyword ) 
	{
		Stock stock = stockofbank.getStock();
		LocalDate requireddate = tablemodel.getShowCurDate();
	    String period = tablemodel.getCurDisplayPeriod();
	    BanKuaiGeGuMatchCondition matchcond = tablemodel.getDisplayMatchCondition ();
		
		Facts facts = new Facts();
        facts.put("evanode", stock);
        facts.put("evadate", requireddate);
        facts.put("evadatedifference", 0);
        facts.put("evaperiod", period);
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
        
        RuleOfCjeZbDpMaxWk cjezbdpmaxwkRule = new RuleOfCjeZbDpMaxWk ();
        rules.register(cjezbdpmaxwkRule);
        
        RuleOfWeeklyAverageChenJiaoErMaxWk averagecjemaxwkRule = new RuleOfWeeklyAverageChenJiaoErMaxWk ();
        rules.register(averagecjemaxwkRule);
        
        RuleOfMA maRule = new RuleOfMA ();
        rules.register(maRule);
        
        RuleOfChenJiaoEr cjeRule = new RuleOfChenJiaoEr ();
        rules.register(cjeRule);
        
        RuleOfHuanShouLv hslRule = new RuleOfHuanShouLv ();
        rules.register(hslRule);
        
     // fire rules on known facts
        RulesEngine rulesEngine = new DefaultRulesEngine();
        rulesEngine.fire(rules, facts);
        
		Color background = Color.white;
		switch (column_keyword) {
        case "zhangdiefu":
        	NodeXPeriodData nodexdata = stock.getNodeXPeroidData(period);
		    Double zhangdiefu = nodexdata.getSpecificOHLCZhangDieFu (requireddate,0);
		    background = CandleStickColorFactory.getCandelStickColor(zhangdiefu);
        	break;
        	
        case "infengxifile":
        	NodesTreeRelated stofbktree = stock.getNodeTreeRelated();
        	
	 		Boolean isin = stofbktree.selfIsMatchModel(requireddate);
	    	if(isin != null && isin  ) 
		    		background = Color.ORANGE;  
	    	else 
	    		background = Color.white;
        	break;
        case "quekouzhangfu":
        	if(qkRule.getRuleResult() || zfRule.getRuleResult() )	
	    		background = Color.PINK;
        	break;
        case "CjeZbDpMaxWk":
        	background = cjezbdpmaxwkRule.getBackGround();
        	break;
        case "dayujunxian" :
        	background = maRule.getBackGround();
        	break;
        case "averagecjemaxwk" :
        	background = averagecjemaxwkRule.getBackGround();
        	break;
        case "liutongshizhi":
        	background = ltszRule.getBackGround ();
        	break;
        case "chengjiaoer" :
        	background = cjeRule.getBackGround();
        	break;
        case "huanshoulv" :
        	background = hslRule.getBackGround();
        	break;
	    };
	    
	    return background;
		
	}

}
