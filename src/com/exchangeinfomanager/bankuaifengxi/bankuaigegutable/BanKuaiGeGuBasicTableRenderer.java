package com.exchangeinfomanager.bankuaifengxi.bankuaigegutable;

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
	protected Color rendererOperationsForColumnForgroundHighLight ( StockOfBanKuai stockofbank, String column_keyword, String predefinedcolor )
	{
		Color foreground = Color.BLACK;
		
		Stock stock = stockofbank.getStock();
		LocalDate requireddate = tablemodel.getCurDisplayedDate();
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
	protected Color rendererOperationsForColumnBackgroundHighLight ( StockOfBanKuai stockofbank, String column_keyword, String predefinedcolor ) 
	{
		Stock stock = stockofbank.getStock();
		LocalDate requireddate = tablemodel.getCurDisplayedDate();
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
		    if(predefinedcolor != null && !predefinedcolor.toUpperCase().equals("SYSTEM") )
		    	background = Color.decode( predefinedcolor );
		    else if( predefinedcolor != null && predefinedcolor.toUpperCase().equals("SYSTEM") )
		    	background = CandleStickColorFactory.getCandelStickColor(zhangdiefu);
		    else background = CandleStickColorFactory.getCandelStickColor(zhangdiefu);
        	break;
        	
        case "infengxifile":
        	NodesTreeRelated stofbktree = stock.getNodeTreeRelated();
        	
	 		Boolean isin = stofbktree.selfIsMatchModel(requireddate);
	    	if(isin != null && isin  ) 
	    		if(predefinedcolor != null && !predefinedcolor.toUpperCase().equals("SYSTEM") )
			    	background = Color.decode( predefinedcolor );
	    		else if(predefinedcolor != null && predefinedcolor.toUpperCase().equals("SYSTEM") )
	    			background = Color.ORANGE;
			    else background = Color.ORANGE;  
	    	else 
	    		background = Color.white;
        	break;
        case "quekouzhangfu":
        	if(qkRule.getRuleResult() || zfRule.getRuleResult() )	
        		if(predefinedcolor != null && !predefinedcolor.toUpperCase().equals("SYSTEM") )
    		    	background = Color.decode( predefinedcolor );
        		else if(predefinedcolor != null && predefinedcolor.toUpperCase().equals("SYSTEM") )
        			background = Color.PINK;
    		    else background = Color.PINK;
        	break;
        case "CjeZbDpMaxWk":
        	if(predefinedcolor != null && !predefinedcolor.toUpperCase().equals("SYSTEM") && cjezbdpmaxwkRule.getRuleResult() )
		    	background = Color.decode( predefinedcolor );
        	else if(predefinedcolor != null && predefinedcolor.toUpperCase().equals("SYSTEM") )
        		background = cjezbdpmaxwkRule.getBackGround();
		    else background = cjezbdpmaxwkRule.getBackGround();
        	break;
        case "dayujunxian" :
        	if(predefinedcolor != null && !predefinedcolor.toUpperCase().equals("SYSTEM") && maRule.getAnalysisResult() )
		    	background = Color.decode( predefinedcolor );
        	else if(predefinedcolor != null && predefinedcolor.toUpperCase().equals("SYSTEM") )
        		background = maRule.getBackGround();
		    else   	background = maRule.getBackGround();
        	break;
        case "averagecjemaxwk" :
        	if(predefinedcolor != null && !predefinedcolor.toUpperCase().equals("SYSTEM") && averagecjemaxwkRule.getRuleResult() )
		    	background = Color.decode( predefinedcolor );
        	else if(predefinedcolor != null && predefinedcolor.toUpperCase().equals("SYSTEM") )
        		background = averagecjemaxwkRule.getBackGround();
		    else  	background = averagecjemaxwkRule.getBackGround();
        	break;
        case "liutongshizhi":
        	if(predefinedcolor != null && !predefinedcolor.toUpperCase().equals("SYSTEM") && ltszRule.getAnalysisResult())
		    	background = Color.decode( predefinedcolor );
        	else if(predefinedcolor != null && predefinedcolor.toUpperCase().equals("SYSTEM") )
        		background = ltszRule.getBackGround ();
		    else  	background = ltszRule.getBackGround ();
        	break;
        case "chengjiaoer" :
        	if(predefinedcolor != null && !predefinedcolor.toUpperCase().equals("SYSTEM") && cjeRule.getRuleResult() )
		    	background = Color.decode( predefinedcolor );
        	else if(predefinedcolor != null && predefinedcolor.toUpperCase().equals("SYSTEM") )
        		background = cjeRule.getBackGround();
        	else background = cjeRule.getBackGround();
        	break;
        case "huanshoulv" :
        	if(predefinedcolor != null && !predefinedcolor.toUpperCase().equals("SYSTEM") && hslRule.getRuleResult() )
		    	background = Color.decode( predefinedcolor );
        	else if(predefinedcolor != null && predefinedcolor.toUpperCase().equals("SYSTEM") )
        	 	background = hslRule.getBackGround();
		    else   	background = hslRule.getBackGround();
        	break;
        case "gujia" :
        	if(predefinedcolor != null && !predefinedcolor.toUpperCase().equals("SYSTEM") && priceRule.getAnalysisResult() )
		    	background = Color.decode( predefinedcolor );
        	else if(predefinedcolor != null && predefinedcolor.toUpperCase().equals("SYSTEM") )
        	  	background = priceRule.getBackGround();
		    else   	background = priceRule.getBackGround();
	    };
	    
	    return background;
		
	}

}
