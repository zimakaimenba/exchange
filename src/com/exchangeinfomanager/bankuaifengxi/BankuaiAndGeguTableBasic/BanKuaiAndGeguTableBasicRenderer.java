package com.exchangeinfomanager.bankuaifengxi.BankuaiAndGeguTableBasic;

import java.awt.Color;
import java.awt.Font;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;

import com.exchangeinfomanager.bankuaifengxi.CandleStick.CandleStickColorFactory;
import com.exchangeinfomanager.bankuaifengxi.HighlightAndExportNodes.BanKuaiGeGuMatchCondition;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfChenJiaoEr;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfCjeZbDpMaxWk;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfCjeZbGrowingRate;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfGeGuDailyHighestZhangFuInWeek;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfGeGuPrice;

import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfHuanShouLv;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfLiuTongShiZhi;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfMA;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfQueKou;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfWeeklyAverageChenJiaoErMaxWk;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfZongShiZhi;
import com.exchangeinfomanager.commonlib.Season;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.treerelated.NodesTreeRelated;
import com.google.common.base.Splitter;
import com.google.common.collect.Range;

import Jama.util.Maths;

public class BanKuaiAndGeguTableBasicRenderer extends DefaultTableCellRenderer
{
	protected BandKuaiAndGeGuTableBasicModel tablemodel;
	protected Properties prop;
	
	protected BanKuaiAndGeguTableBasicRenderer (Properties prop1) {
		super ();
		this.prop = prop1;
	}
	
	protected Object[] getTableCellRendererBackgroundForegroundColor (JTable table, TDXNodes node,Object value, int row,  int col)
	{
		String valuetext =""; Font font = this.getFont();Color foreground = Color.BLACK, background = Color.white;
		
	    String current_column_bg_kw = null; String column_info_valueformat ="";
	    switch (col) {
	    case 0:
	    	Object[] column0rendererresults = getRendererResultsByColumnIndex (node,value,row,col);
	    	current_column_bg_kw = (String) column0rendererresults[0];
	    	valuetext = (String) column0rendererresults[1];
	    	background = (Color)column0rendererresults[2];
	    	foreground = (Color)column0rendererresults[3];
	    	font = (Font)column0rendererresults[4];
        	break;
        case 1:
        	Object[] column0rendererresults1 = getRendererResultsByColumnIndex (node,value,row,col);
	    	current_column_bg_kw = (String) column0rendererresults1[0];
	    	valuetext = (String) column0rendererresults1[1];
	    	background = (Color)column0rendererresults1[2];
	    	foreground = (Color)column0rendererresults1[3];
	    	font = (Font)column0rendererresults1[4];
        	break;
        case 2:
        	Object[] column0rendererresults2 = getRendererResultsByColumnIndex (node,value,row,col);
	    	current_column_bg_kw = (String) column0rendererresults2[0];
	    	valuetext = (String) column0rendererresults2[1];
	    	background = (Color)column0rendererresults2[2];
	    	foreground = (Color)column0rendererresults2[3];
	    	font = (Font)column0rendererresults2[4];
        	break;
        case 3:
        	Object[] column0rendererresults3 = getRendererResultsByColumnIndex (node,value,row,col);
	    	current_column_bg_kw = (String) column0rendererresults3[0];
	    	valuetext = (String) column0rendererresults3[1];
	    	background = (Color)column0rendererresults3[2];
	    	foreground = (Color)column0rendererresults3[3];
	    	font = (Font)column0rendererresults3[4];
        	break;
        case 4:
        	Object[] column0rendererresults4 = getRendererResultsByColumnIndex (node,value,row,col);
	    	current_column_bg_kw = (String) column0rendererresults4[0];
	    	valuetext = (String) column0rendererresults4[1];
	    	background = (Color)column0rendererresults4[2];
	    	foreground = (Color)column0rendererresults4[3];
	    	font = (Font)column0rendererresults4[4];
        	break;
        case 5:
        	Object[] column0rendererresults5 = getRendererResultsByColumnIndex (node,value,row,col);
	    	current_column_bg_kw = (String) column0rendererresults5[0];
	    	valuetext = (String) column0rendererresults5[1];
	    	background = (Color)column0rendererresults5[2];
	    	foreground = (Color)column0rendererresults5[3];
	    	font = (Font)column0rendererresults5[4];
        	break;
        case 6:
        	Object[] column0rendererresults6 = getRendererResultsByColumnIndex (node,value,row,col);
	    	current_column_bg_kw = (String) column0rendererresults6[0];
	    	valuetext = (String) column0rendererresults6[1];
	    	background = (Color)column0rendererresults6[2];
	    	foreground = (Color)column0rendererresults6[3];
	    	font = (Font)column0rendererresults6[4];
        	break;
        case 7:
        	Object[] column0rendererresults7 = getRendererResultsByColumnIndex (node,value,row,col);
	    	current_column_bg_kw = (String) column0rendererresults7[0];
	    	valuetext = (String) column0rendererresults7[1];
	    	background = (Color)column0rendererresults7[2];
	    	foreground = (Color)column0rendererresults7[3];
	    	font = (Font)column0rendererresults7[4];
        	break;
        case 8:
        	Object[] column0rendererresults8 = getRendererResultsByColumnIndex (node,value,row,col);
	    	current_column_bg_kw = (String) column0rendererresults8[0];
	    	valuetext = (String) column0rendererresults8[1];
	    	background = (Color)column0rendererresults8[2];
	    	foreground = (Color)column0rendererresults8[3];
	    	font = (Font)column0rendererresults8[4];
        	break;
        case 9:
        	Object[] column0rendererresults9 = getRendererResultsByColumnIndex (node,value,row,col);
	    	current_column_bg_kw = (String) column0rendererresults9[0];
	    	valuetext = (String) column0rendererresults9[1];
	    	background = (Color)column0rendererresults9[2];
	    	foreground = (Color)column0rendererresults9[3];
	    	font = (Font)column0rendererresults9[4];
        	break;
        case 10:
        	Object[] column0rendererresults10 = getRendererResultsByColumnIndex (node,value,row,col);
	    	current_column_bg_kw = (String) column0rendererresults10[0];
	    	valuetext = (String) column0rendererresults10[1];
	    	background = (Color)column0rendererresults10[2];
	    	foreground = (Color)column0rendererresults10[3];
	    	font = (Font)column0rendererresults10[4];
        	break;
        case 11:
        	Object[] column0rendererresults11 = getRendererResultsByColumnIndex (node,value,row,col);
	    	current_column_bg_kw = (String) column0rendererresults11[0];
	    	valuetext = (String) column0rendererresults11[1];
	    	background = (Color)column0rendererresults11[2];
	    	foreground = (Color)column0rendererresults11[3];
	    	font = (Font)column0rendererresults11[4];
        	break;
	    };
	    
		Stock stock = null ; Boolean reviewedtoday = false; 
		if(node instanceof StockOfBanKuai) {
			stock = ((StockOfBanKuai)node).getStock();
			if(stock.getShuJuJiLuInfo().wetherHasReiewedToday())
				reviewedtoday = true;
		} else
			reviewedtoday = node.getShuJuJiLuInfo().wetherHasReiewedToday();
		
		LocalDate curdisplaydate = ((BandKuaiAndGeGuTableBasicModel)table.getModel()).getCurDisplayedDate();
		Range<LocalDate> range = ((TDXNodes)node).isInDuanQiGuanZhuRange (curdisplaydate); 
        if(range != null)  
        	background = new Color(102,178,255);
	    
	    
	    if(reviewedtoday && table.isRowSelected(row) ) 
        	font = new Font(font.getName(), font.getStyle() | Font.ITALIC,font.getSize());
        else
        if(reviewedtoday && !table.isRowSelected(row) && ( current_column_bg_kw == null || !current_column_bg_kw.equalsIgnoreCase("infengxifile") ) ) { //已经浏览过的个股，全部灰色，不会混淆，更加清晰
            	font = new Font(font.getName(), font.getStyle() | Font.ITALIC,font.getSize());
            	background = Color.gray;
        } else
        if (reviewedtoday && !table.isRowSelected(row) && current_column_bg_kw != null && current_column_bg_kw.equalsIgnoreCase("infengxifile") ) {//已经浏览过的个股，全部灰色，不会混淆，更加清晰
        	font = new Font(font.getName(), font.getStyle() | Font.ITALIC,font.getSize());
        	if(background == Color.WHITE)
        		background = Color.gray;
        }

	    if(table.isRowSelected(row)) {
		    String rowselectedindex = prop.getProperty ("rowselectedindex");
		    if(rowselectedindex == null && col == 1)
	    		background = new Color(102,102,255);
		    else
			if(rowselectedindex != null && col == Integer.parseInt(rowselectedindex.trim() ) )  //	    if( columnname.equals("名称") ) { //个股名称
	    		background = new Color(102,102,255);
	    }
	    
	    Object[] textbackgroundforegroundfont = {valuetext,background, foreground, font};
	    return textbackgroundforegroundfont;
	}
	private Object[] getRendererResultsByColumnIndex (TDXNodes node,Object value, Integer row,  Integer col)
	{
		String valuetext =""; Font font = this.getFont();Color foreground = Color.BLACK, background = Color.WHITE;
	    	    
		String column_bg_kw  = prop.getProperty (col.toString() + "column_background_highlight_keyword");
		String column_bg_color  = prop.getProperty (col.toString() + "column_background_hightlight_color");
		if(column_bg_kw != null   && column_bg_kw.contains("OTHERWISE")) {
			List<String> bgkwlist = Splitter.on("OTHERWISE").omitEmptyStrings().splitToList(column_bg_kw); 
			for(String bgkw : bgkwlist ) {
				Color tmpbackground = null;
				if(bgkw != null)
		    		tmpbackground = rendererOperationsForColumnBackgroundHighLight (node,bgkw.trim(), column_bg_color);
				if(tmpbackground != null && !tmpbackground.equals(background) && !tmpbackground.equals(Color.WHITE))
					background = tmpbackground;
			}
		} else {
			if(column_bg_kw != null)
				background = rendererOperationsForColumnBackgroundHighLight (node,column_bg_kw, column_bg_color);
		}
		
    	String column_fg_kw  = prop.getProperty (col.toString() + "column_foreground_highlight_keyword");
    	String column_fg_color  = prop.getProperty (col.toString() + "column_foreground_hightlight_color");
    	if(column_fg_kw != null && column_fg_kw.contains("OTHERWISE")) {
    		List<String> fgkwlist = Splitter.on("OTHERWISE").omitEmptyStrings().splitToList(column_fg_kw); 
			for(String fgkw : fgkwlist ) {
				Color tmpforground = null;
				if(fgkw != null)
		    		tmpforground = rendererOperationsForColumnForgroundHighLight (node,fgkw.trim(), column_bg_color);
				if(tmpforground != null && !tmpforground.equals(background) && !tmpforground.equals(Color.BLACK))
					background = tmpforground;
			}
    	} else {
    		if(column_fg_kw != null)
        		foreground = rendererOperationsForColumnForgroundHighLight (node,column_fg_kw,column_fg_color);
    	}
    	
    	String column_kw  = prop.getProperty (col.toString() + "column_info_keyword");
    	String column_info_valueformat = prop.getProperty (col.toString() + "column_info_valueformat"); 
    	if(column_info_valueformat != null)
    		valuetext = rendererOperationsForColumnInfoFormat (node,value, column_kw,column_info_valueformat);

    	font = rendererOperationsForColumnFont (node, "HasHqgq"); //其次看有没有皇亲国戚
    	
    	Object[] keywordtextbackgroundforegroundfont = {column_bg_kw, valuetext,background, foreground, font};
	    return keywordtextbackgroundforegroundfont;
	}
	
	private String rendererOperationsForColumnInfoFormat(TDXNodes node, Object value,	String column_keyword, String column_info_valueformat)
	{
		if(value == null)
			return null;
		String valuepect = "";
		switch (column_keyword) {
		case "CjeZbGrowRate":
			if(!column_info_valueformat.equalsIgnoreCase("PERCENT"))
				return "";
			valuepect = rendererOperationForPercent (value);
			break;
		case "CjlZbGrowRate":
			if(!column_info_valueformat.equalsIgnoreCase("PERCENT"))
				return "";
			valuepect = rendererOperationForPercent (value);
			break;
		case "BanKuaiChengJiaoErGongXian":
			if(!column_info_valueformat.equalsIgnoreCase("PERCENT"))
				return "";
			valuepect = rendererOperationForPercent (value);
			break;
		case "BanKuaiChengJiaoLiangGongXian":
			if(!column_info_valueformat.equalsIgnoreCase("PERCENT"))
				return "";
			valuepect = rendererOperationForPercent (value);
			break;
		case "TimeRangeZhangFu" :
			if(!column_info_valueformat.equalsIgnoreCase("PERCENT"))
				return "";
			valuepect = rendererOperationForPercent (value);
			break;
		case "ChenJiaoEr":
			if(!column_info_valueformat.equalsIgnoreCase("REDUCT"))
				return "";
			Double cje = Double.parseDouble(value.toString()) / 100000000;
			double count = Math.pow(10, 2);
	    	try {
	    				Double output = Math.round(  (Double)cje * count) / count;
	    				valuepect = output.toString();
	    	} catch ( java.lang.ClassCastException e) {	e.printStackTrace(); valuepect ="";}
			break;
		}	
		
    	return valuepect;
	}
	private String rendererOperationForPercent (Object value) {
		
		String  valuepect = "";
		 //用百分比显示
    	try {
    		 double formatevalue = NumberFormat.getInstance(Locale.CHINA).parse(value.toString()).doubleValue();
    		 
    		 NumberFormat percentFormat = NumberFormat.getPercentInstance(Locale.CHINA);
    	     percentFormat.setMinimumFractionDigits(2);
        	 valuepect = percentFormat.format (formatevalue );
    	} catch (java.lang.NullPointerException e) {		valuepect = "";
    	}catch (java.lang.NumberFormatException e)  { e.printStackTrace();
    	} catch (ParseException e) {e.printStackTrace();}
    	
    	return valuepect;
	}
	/*
	 * 
	 */
	protected Font rendererOperationsForColumnFont ( TDXNodes node, String column_keyword )
	{
		Font defaultFont = this.getFont();
		if(node.getType() != BkChanYeLianTreeNode.BKGEGU )
			return defaultFont;
		
		LocalDate requireddate = tablemodel.getCurDisplayedDate();
		switch (column_keyword) {
		case "HasHqgq":
				Boolean has = false;
				LocalDate maxcbrq = ((StockOfBanKuai)node).getStock().getNodeJiBenMian().getLastestCaiBaoDate();
				try {
					long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(requireddate,maxcbrq);
					if( java.lang.Math.abs(daysBetween) >= 280 ) //有些股票机构股东数据是很久以前的，没意义
						has = false;
					else 
						has = ((StockOfBanKuai)node).getStock().getNodeJiBenMian().hasHqgqGuDong(maxcbrq);
				} catch (java.lang.NullPointerException e) {has = false;}
				
				if(has) defaultFont =  new Font("Hei", Font.BOLD,defaultFont.getSize() + 1);
			
			break;
		case "HasMinXing":
			has = false;
			maxcbrq = ((StockOfBanKuai)node).getStock().getNodeJiBenMian().getLastestCaiBaoDate();
			try {
				long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(requireddate,maxcbrq);
				if( java.lang.Math.abs(daysBetween) >= 280 ) //有些股票机构股东数据是很久以前的，没意义
					has = false;
				else 
					has = ((StockOfBanKuai)node).getStock().getNodeJiBenMian().hasMinXingGuDong(maxcbrq);
			} catch (java.lang.NullPointerException e) {has = false;}
			
			if(has) defaultFont =  new Font("Hei", Font.BOLD,defaultFont.getSize());
		
		break;
		}
		
		return defaultFont;
	}
	/*
	 * 
	 */
	protected Color rendererOperationsForColumnForgroundHighLight ( TDXNodes node, String column_keyword, String predefinedcolor )
	{
		Color foreground = Color.BLACK;
		
		LocalDate requireddate = tablemodel.getCurDisplayedDate();
	    String period = tablemodel.getCurDisplayPeriod();
	    BanKuaiGeGuMatchCondition matchcond = tablemodel.getDisplayMatchCondition ();
		
	    Facts lwfacts = new Facts();
	    if(node instanceof StockOfBanKuai)
	    	lwfacts.put("evanode", ((StockOfBanKuai)node).getStock() );
	    else
	    	lwfacts.put("evanode", node);
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
        } catch (Exception ex  ) {ex.printStackTrace();}
        
        NodeXPeriodData nodexdata = node.getNodeXPeroidData(period);
    	Double lwzhangdiefu = nodexdata.getSpecificOHLCZhangDieFu (requireddate,-1);
    	Double zhangdiefu = nodexdata.getSpecificOHLCZhangDieFu (requireddate,0);
    	
		switch (column_keyword) {
		case "HasHqgq":
			Boolean has = ((StockOfBanKuai)node).getStock().getNodeJiBenMian().hasHqgqGuDong(requireddate);
			if(has) foreground = Color.RED;
			break;
		case "ExtremeCjlZhanbi" :
			Double[] extremecjl = node.getNodeCjlZhanbiLevel();
			Double zhanbi = nodexdata.getChenJiaoLiangZhanBi(requireddate, 0);
			if(extremecjl[0] != null && zhanbi < extremecjl[0])
				foreground = Color.RED;
			else
			if(extremecjl[1] != null && zhanbi > extremecjl[1])
				foreground = Color.GREEN;
			break;
		case "ExtremeCjeZhanbi" :
			Double[] extremecje = node.getNodeCjeZhanbiLevel();
			Double cjezhanbi = nodexdata.getChenJiaoErZhanBi(requireddate, 0);
			if(extremecje[0] != null && cjezhanbi < extremecje[0])
				foreground = Color.RED;
			else
			if(extremecje[1] != null && cjezhanbi > extremecje[1])
				foreground = Color.GREEN;
			break;
        case "QuanZhongInBankuai":
        	if(node instanceof BanKuai)
        		break;
        	
	    	Integer weight = ((StockOfBanKuai)node).getBanKuai().getGeGuSuoShuBanKuaiWeight( node.getMyOwnCode() );
	    	if(weight == null)
	    		foreground = Color.BLACK;
	    	else
	    	if(((StockOfBanKuai)node).isBkLongTou())
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
        
        case "LastWkDpcjezbGrowingRate": 
        	foreground = lwcjezbgr.getForeGround();
        	break;
        case "LastWkDpCjezbMatch":
	    	if(lwzhangdiefu != null &&  lwzhangdiefu <0 && lwdpmaxwkRule.getRuleResult() && zhangdiefu !=null &&  zhangdiefu >0 )
	    		foreground = Color.YELLOW;
        	break;
        case "LastWkAverageCjeMatch" :
	    	if(lwzhangdiefu != null && lwzhangdiefu <0 && lwaveragecjemaxwk.getRuleResult() && zhangdiefu != null && zhangdiefu >0 )
	    		foreground = Color.YELLOW;
        	break;
	    };
	    
	    return foreground;
	}
	/*
	 * 注意：这里传过来的node有可能是stockofbankuai,要转为stock
	 */
	protected Color rendererOperationsForColumnBackgroundHighLight ( TDXNodes node, String column_keyword, String predefinedcolor ) 
	{
//		Stock stock = stockofbank.getStock();
		LocalDate requireddate = tablemodel.getCurDisplayedDate();
	    String period = tablemodel.getCurDisplayPeriod();
	    BanKuaiGeGuMatchCondition matchcond = tablemodel.getDisplayMatchCondition ();
		
		Facts facts = new Facts();
		if(node instanceof StockOfBanKuai)
	    	facts.put("evanode", ((StockOfBanKuai)node).getStock() );
	    else
	    	facts.put("evanode", node);
        facts.put("evadate", requireddate);
        facts.put("evadatedifference", 0);
        facts.put("evaperiod", period);
        facts.put("evacond", matchcond);
        
        Rules rules = new Rules();
//        RuleOfLiuTongShiZhi ltszRule =  new RuleOfLiuTongShiZhi ();
//        rules.register(ltszRule);
        
//        RuleOfZongShiZhi zszRule = new RuleOfZongShiZhi ();
//        rules.register(zszRule);
        
//        RuleOfGeGuDailyHighestZhangFuInWeek zfRule = new RuleOfGeGuDailyHighestZhangFuInWeek ();
//        rules.register(zfRule);
        
//        RuleOfGeGuPrice priceRule = new RuleOfGeGuPrice ();
//        rules.register(priceRule);
        
//        RuleOfQueKou qkRule = new RuleOfQueKou ();
//        rules.register(qkRule);
        
//        RuleOfCjeZbDpMaxWk cjezbdpmaxwkRule = new RuleOfCjeZbDpMaxWk ();
//        rules.register(cjezbdpmaxwkRule);
        
//        RuleOfWeeklyAverageChenJiaoErMaxWk averagecjemaxwkRule = new RuleOfWeeklyAverageChenJiaoErMaxWk ();
//        rules.register(averagecjemaxwkRule);
        
//        RuleOfMA maRule = new RuleOfMA ();
//        rules.register(maRule);
        
//        RuleOfChenJiaoEr cjeRule = new RuleOfChenJiaoEr ();
//        rules.register(cjeRule);
        
//        RuleOfHuanShouLv hslRule = new RuleOfHuanShouLv ();
//        rules.register(hslRule);
        
     // fire rules on known facts
        RulesEngine rulesEngine = new DefaultRulesEngine();
//        rulesEngine.fire(rules, facts);
        
        NodeXPeriodData nodexdata = node.getNodeXPeroidData(period);
		Color background = Color.WHITE;
		switch (column_keyword) {
		case "ExtremeCjlZhanbi" :
			Double[] extremecjl = node.getNodeCjlZhanbiLevel();
			Double zhanbi = nodexdata.getChenJiaoLiangZhanBi(requireddate, 0);
			if(extremecjl[0] != null && zhanbi < extremecjl[0])
				background = Color.RED;
			else
			if(extremecjl[1] != null && zhanbi > extremecjl[1])
				background = Color.GREEN;
			break;
			
		case "ExtremeCjeZhanbi" :
			Double[] extremecje = node.getNodeCjeZhanbiLevel();
			Double cjezhanbi = nodexdata.getChenJiaoErZhanBi(requireddate, 0);
			if(extremecje[0] != null && cjezhanbi < extremecje[0])
				background = Color.RED;
			else
			if(extremecje[1] != null && cjezhanbi > extremecje[1])
				background = Color.GREEN;
			break;
			
        case "ZhangDieFu":
        	if(node instanceof StockOfBanKuai)
        		 nodexdata = ((StockOfBanKuai)node).getStock().getNodeXPeroidData(period);
    	    else
    	    	 nodexdata = node.getNodeXPeroidData(period);
		    Double zhangdiefu = nodexdata.getSpecificOHLCZhangDieFu (requireddate,0);
		    if(predefinedcolor != null && !predefinedcolor.toUpperCase().equals("SYSTEM") )
		    	background = Color.decode( predefinedcolor );
		    else if( predefinedcolor != null && predefinedcolor.toUpperCase().equals("SYSTEM") )
		    	background = CandleStickColorFactory.getCandelStickColor(zhangdiefu);
		    else background = CandleStickColorFactory.getCandelStickColor(zhangdiefu);
        	break;
        	
        case "InFengXiFile":
        	NodesTreeRelated stofbktree ;
        	if(node instanceof StockOfBanKuai)
        		stofbktree = ((StockOfBanKuai)node).getStock().getNodeTreeRelated();
        	else
        		stofbktree = node.getNodeTreeRelated();
        	if(stofbktree == null)
        		background = Color.WHITE;
        	else {
        		Boolean isin = stofbktree.selfIsMatchModel(requireddate);
    	    	if(isin != null && isin  ) 
    	    		if(predefinedcolor != null && !predefinedcolor.toUpperCase().equals("SYSTEM") )
    			    	background = Color.decode( predefinedcolor );
    	    		else if(predefinedcolor != null && predefinedcolor.toUpperCase().equals("SYSTEM") )
    	    			background = Color.ORANGE;
    			    else background = Color.ORANGE;  
    	    	else 
    	    		background = Color.WHITE;
        	}
        	break;
        case "DailyZhangDieFuRangeInWeek":
        	RuleOfGeGuDailyHighestZhangFuInWeek zfRule = new RuleOfGeGuDailyHighestZhangFuInWeek ();
            rules.register(zfRule);
            rulesEngine.fire(rules, facts);
            
        	if( zfRule.getRuleResult() )	
        		if(predefinedcolor != null && !predefinedcolor.toUpperCase().equals("SYSTEM") )
    		    	background = Color.decode( predefinedcolor );
        		else if(predefinedcolor != null && predefinedcolor.toUpperCase().equals("SYSTEM") )
        			background = Color.PINK;
    		    else background = Color.PINK;
        	break;
        case "QueKou":
        	RuleOfQueKou qkRule = new RuleOfQueKou ();
            rules.register(qkRule);
            rulesEngine.fire(rules, facts);
            
        	if(qkRule.getRuleResult()  )	
        		if(predefinedcolor != null && !predefinedcolor.toUpperCase().equals("SYSTEM") )
    		    	background = Color.decode( predefinedcolor );
        		else if(predefinedcolor != null && predefinedcolor.toUpperCase().equals("SYSTEM") )
        			background = Color.PINK;
    		    else background = Color.PINK;
        	break;
        case "CjeZbDpMaxWk":
        	RuleOfCjeZbDpMaxWk cjezbdpmaxwkRule = new RuleOfCjeZbDpMaxWk ();
            rules.register(cjezbdpmaxwkRule);
            rulesEngine.fire(rules, facts);
            
        	if(predefinedcolor != null && !predefinedcolor.toUpperCase().equals("SYSTEM") && cjezbdpmaxwkRule.getRuleResult() )
		    	background = Color.decode( predefinedcolor );
        	else if(predefinedcolor != null && predefinedcolor.toUpperCase().equals("SYSTEM") )
        		background = cjezbdpmaxwkRule.getBackGround();
		    else background = cjezbdpmaxwkRule.getBackGround();
        	break;
        case "CLOSEVSMA" :
        	RuleOfMA maRule = new RuleOfMA ();
            rules.register(maRule);
            rulesEngine.fire(rules, facts);
            
        	if(predefinedcolor != null && !predefinedcolor.toUpperCase().equals("SYSTEM") && maRule.getAnalysisResult() )
		    	background = Color.decode( predefinedcolor );
        	else if(predefinedcolor != null && predefinedcolor.toUpperCase().equals("SYSTEM") )
        		background = maRule.getBackGround();
		    else   	background = maRule.getBackGround();
        	break;
        case "AverageChenJiaoErMaxWeek" : 
        	RuleOfWeeklyAverageChenJiaoErMaxWk averagecjemaxwkRule = new RuleOfWeeklyAverageChenJiaoErMaxWk ();
            rules.register(averagecjemaxwkRule);
            rulesEngine.fire(rules, facts);
            
        	if(predefinedcolor != null && !predefinedcolor.toUpperCase().equals("SYSTEM") && averagecjemaxwkRule.getRuleResult() )
		    	background = Color.decode( predefinedcolor );
        	else if(predefinedcolor != null && predefinedcolor.toUpperCase().equals("SYSTEM") )
        		background = averagecjemaxwkRule.getBackGround();
		    else  	background = averagecjemaxwkRule.getBackGround();
        	break;
        case "LiuTongShiZhi":
        	RuleOfLiuTongShiZhi ltszRule =  new RuleOfLiuTongShiZhi ();
            rules.register(ltszRule);
            rulesEngine.fire(rules, facts);
            
        	if(predefinedcolor != null && !predefinedcolor.toUpperCase().equals("SYSTEM") && ltszRule.getAnalysisResult())
		    	background = Color.decode( predefinedcolor );
        	else if(predefinedcolor != null && predefinedcolor.toUpperCase().equals("SYSTEM") )
        		background = ltszRule.getBackGround ();
		    else  	background = ltszRule.getBackGround ();
        	break;
        case "ZongShiZhi":
        	 RuleOfZongShiZhi zszRule = new RuleOfZongShiZhi ();
             rules.register(zszRule);
             rulesEngine.fire(rules, facts);
             
        	if(predefinedcolor != null && !predefinedcolor.toUpperCase().equals("SYSTEM") && zszRule.getAnalysisResult())
		    	background = Color.decode( predefinedcolor );
        	else if(predefinedcolor != null && predefinedcolor.toUpperCase().equals("SYSTEM") )
        		background = zszRule.getBackGround ();
		    else  	background = zszRule.getBackGround ();
        	break;
        case "ChenJiaoEr" :
        	RuleOfChenJiaoEr cjeRule = new RuleOfChenJiaoEr ();
            rules.register(cjeRule);
            rulesEngine.fire(rules, facts);
            
        	if(predefinedcolor != null && !predefinedcolor.toUpperCase().equals("SYSTEM") && cjeRule.getRuleResult() )
		    	background = Color.decode( predefinedcolor );
        	else if(predefinedcolor != null && predefinedcolor.toUpperCase().equals("SYSTEM") )
        		background = cjeRule.getBackGround();
        	else background = cjeRule.getBackGround();
        	break;
        case "HuanShouLv" :
        	RuleOfHuanShouLv hslRule = new RuleOfHuanShouLv ();
            rules.register(hslRule);
            rulesEngine.fire(rules, facts);
            
        	if(predefinedcolor != null && !predefinedcolor.toUpperCase().equals("SYSTEM") && hslRule.getRuleResult() )
		    	background = Color.decode( predefinedcolor );
        	else if(predefinedcolor != null && predefinedcolor.toUpperCase().equals("SYSTEM") )
        	 	background = hslRule.getBackGround();
		    else   	background = hslRule.getBackGround();
        	break;
        case "GuJiaCLOSE" :
        	RuleOfGeGuPrice priceRule = new RuleOfGeGuPrice ();
        	rules.register(priceRule);
        	rulesEngine.fire(rules, facts);
        	
        	if(predefinedcolor != null && !predefinedcolor.toUpperCase().equals("SYSTEM") && priceRule.getAnalysisResult() )
		    	background = Color.decode( predefinedcolor );
        	else if(predefinedcolor != null && predefinedcolor.toUpperCase().equals("SYSTEM") )
        	  	background = priceRule.getBackGround();
		    else   	background = priceRule.getBackGround();
	    };
	    
	    return background;
	}

}
