package com.exchangeinfomanager.bankuaifengxi.BankuaiAndGeguTableBasic;

import java.awt.Color;
import java.awt.Font;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Properties;

import javax.swing.JTable;
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
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfGeGuDailyHighestZhangFuInWeek;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfGeGuPrice;

import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfHuanShouLv;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfLiuTongShiZhi;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfMA;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfQueKou;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfWeeklyAverageChenJiaoErMaxWk;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfZongShiZhi;
import com.exchangeinfomanager.commonlib.Season;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.treerelated.NodesTreeRelated;
import com.google.common.collect.Range;

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
		
	    String current_column_bg_kw = null;
	    switch (col) {
	    case 0:
        	String column_bg_kw0  = prop.getProperty ("0column_background_highlight_keyword");
        	current_column_bg_kw = column_bg_kw0;
        	String column_bg_color0  = prop.getProperty ("0column_background_hightlight_color");
        	
        	String column_fg_kw0  = prop.getProperty ("0column_foreground_highlight_keyword");
        	String column_fg_color0  = prop.getProperty ("0column_foreground_hightlight_color");
        	
        	if(column_fg_kw0 != null)
        		foreground = rendererOperationsForColumnForgroundHighLight (node,column_fg_kw0,column_fg_color0);
        	if(column_bg_kw0 != null)
        		background = rendererOperationsForColumnBackgroundHighLight (node,column_bg_kw0, column_bg_color0);
        	
        	String column_info_valueformat = prop.getProperty ("0column_info_valueformat"); 
        	if(column_info_valueformat != null)
        		valuetext = rendererOperationsForColumnInfoFormat (node,value, column_info_valueformat);
        	
        	font = rendererOperationsForColumnFont (node, "hashqgq");
        	
        	break;
        case 1:
        	String column_bg_kw1  = prop.getProperty ("1column_background_highlight_keyword");
        	current_column_bg_kw = column_bg_kw1;
        	String column_bg_color1  = prop.getProperty ("1column_background_hightlight_color");
        	
        	String column_fg_kw1  = prop.getProperty ("1column_foreground_highlight_keyword");
        	String column_fg_color1  = prop.getProperty ("1column_foreground_hightlight_color");
        	
        	if(column_fg_kw1 != null)
        		foreground = rendererOperationsForColumnForgroundHighLight (node,column_fg_kw1,column_fg_color1);
        	if(column_bg_kw1 != null)
        		background = rendererOperationsForColumnBackgroundHighLight (node,column_bg_kw1, column_bg_color1);
        	
        	column_info_valueformat = prop.getProperty ("1column_info_valueformat"); 
        	if(column_info_valueformat != null)
        		valuetext = rendererOperationsForColumnInfoFormat (node,value, column_info_valueformat);

        	font = rendererOperationsForColumnFont (node, "hashqgq");
        	break;
        case 2:
        	String column_bg_kw2  = prop.getProperty ("2column_background_highlight_keyword");
        	current_column_bg_kw = column_bg_kw2;
        	String column_bg_color2  = prop.getProperty ("2column_background_hightlight_color");
        	
        	String column_fg_kw2  = prop.getProperty ("2column_foreground_highlight_keyword");
        	String column_fg_color2  = prop.getProperty ("2column_foreground_hightlight_color");
        	
        	if(column_fg_kw2 != null)
        		foreground = rendererOperationsForColumnForgroundHighLight (node,column_fg_kw2,column_fg_color2);
        	if(column_bg_kw2 != null)
        		background = rendererOperationsForColumnBackgroundHighLight (node,column_bg_kw2, column_bg_color2);
        	
        	column_info_valueformat = prop.getProperty ("2column_info_valueformat"); 
        	if(column_info_valueformat != null)
        		valuetext = rendererOperationsForColumnInfoFormat (node,value, column_info_valueformat);
        	
        	font = rendererOperationsForColumnFont (node, "hashqgq");
        	break;
        case 3:
        	String column_bg_kw3  = prop.getProperty ("3column_background_highlight_keyword");
        	current_column_bg_kw = column_bg_kw3;
        	String column_bg_color3  = prop.getProperty ("3column_background_hightlight_color");
        	
        	String column_fg_kw3  = prop.getProperty ("3column_foreground_highlight_keyword");
        	String column_fg_color3  = prop.getProperty ("3column_foreground_hightlight_color");
        	
        	if(column_fg_kw3 != null)
        		foreground = rendererOperationsForColumnForgroundHighLight (node,column_fg_kw3,column_fg_color3);
        	if(column_bg_kw3 != null)
        		background = rendererOperationsForColumnBackgroundHighLight (node,column_bg_kw3, column_bg_color3);
        	
        	column_info_valueformat = prop.getProperty ("3column_info_valueformat"); 
        	if(column_info_valueformat != null)
        		valuetext = rendererOperationsForColumnInfoFormat (node,value, column_info_valueformat);
        	
        	font = rendererOperationsForColumnFont (node, "hashqgq");
        	break;
        case 4:
        	String column_bg_kw4  = prop.getProperty ("4column_background_highlight_keyword");
        	current_column_bg_kw = column_bg_kw4;
        	String column_bg_color4  = prop.getProperty ("4column_background_hightlight_color");
        	
        	String column_fg_kw4  = prop.getProperty ("4column_foreground_highlight_keyword");
        	String column_fg_color4  = prop.getProperty ("4column_foreground_hightlight_color");
        	
        	if(column_fg_kw4 != null)
        		foreground = rendererOperationsForColumnForgroundHighLight (node,column_fg_kw4,column_fg_color4);
        	if(column_bg_kw4 != null)
        		background = rendererOperationsForColumnBackgroundHighLight (node,column_bg_kw4, column_bg_color4);
        	
        	column_info_valueformat = prop.getProperty ("4column_info_valueformat"); 
        	if(column_info_valueformat != null)
        		valuetext = rendererOperationsForColumnInfoFormat (node,value, column_info_valueformat);
        	
        	font = rendererOperationsForColumnFont (node, "hashqgq");
        	break;
        case 5:
        	String column_bg_kw5  = prop.getProperty ("5column_background_highlight_keyword");
        	current_column_bg_kw = column_bg_kw5;
        	String column_bg_color5  = prop.getProperty ("5column_background_hightlight_color");
        	
        	String column_fg_kw5  = prop.getProperty ("5column_foreground_highlight_keyword");
        	String column_fg_color5  = prop.getProperty ("5column_foreground_hightlight_color");
        	
        	if(column_fg_kw5 != null)
        		foreground = rendererOperationsForColumnForgroundHighLight (node,column_fg_kw5,column_fg_color5 );
        	if(column_bg_kw5 != null)
        		background = rendererOperationsForColumnBackgroundHighLight (node,column_bg_kw5, column_bg_color5);
        	
        	column_info_valueformat = prop.getProperty ("5column_info_valueformat"); 
        	if(column_info_valueformat != null)
        		valuetext = rendererOperationsForColumnInfoFormat (node,value, column_info_valueformat);
        	
        	font = rendererOperationsForColumnFont (node, "hashqgq");
        	break;
        case 6:
        	String column_bg_kw6  = prop.getProperty ("6column_background_highlight_keyword");
        	current_column_bg_kw = column_bg_kw6;
        	String column_bg_color6  = prop.getProperty ("6column_background_hightlight_color");
        	
        	String column_fg_kw6  = prop.getProperty ("6column_foreground_highlight_keyword");
        	String column_fg_color6  = prop.getProperty ("6column_foreground_hightlight_color");
        	
        	if(column_fg_kw6 != null)
        		foreground = rendererOperationsForColumnForgroundHighLight (node,column_fg_kw6,column_fg_color6);
        	if(column_bg_kw6 != null)
        		background = rendererOperationsForColumnBackgroundHighLight (node,column_bg_kw6, column_bg_color6);
        	
        	column_info_valueformat = prop.getProperty ("6column_info_valueformat"); 
        	if(column_info_valueformat != null)
        		valuetext = rendererOperationsForColumnInfoFormat (node,value, column_info_valueformat);
        	
        	font = rendererOperationsForColumnFont (node, "hashqgq");
        	break;
        case 7:
        	String column_bg_kw7  = prop.getProperty ("7column_background_highlight_keyword");
        	current_column_bg_kw = column_bg_kw7;
        	String column_bg_color7  = prop.getProperty ("7column_background_hightlight_color");
        	
        	String column_fg_kw7  = prop.getProperty ("7column_foreground_highlight_keyword");
        	String column_fg_color7  = prop.getProperty ("7column_foreground_hightlight_color");
        	
        	if(column_fg_kw7 != null)
        		foreground = rendererOperationsForColumnForgroundHighLight (node,column_fg_kw7,column_fg_color7);
        	if(column_bg_kw7 != null)
        		background = rendererOperationsForColumnBackgroundHighLight (node,column_bg_kw7, column_bg_color7);
        	
        	column_info_valueformat = prop.getProperty ("7column_info_valueformat"); 
        	if(column_info_valueformat != null)
        		valuetext = rendererOperationsForColumnInfoFormat (node,value, column_info_valueformat);

        	font = rendererOperationsForColumnFont (node, "hashqgq");
        	break;
        case 8:
        	String column_bg_kw8  = prop.getProperty ("8column_background_highlight_keyword");
        	current_column_bg_kw = column_bg_kw8;
        	String column_bg_color8  = prop.getProperty ("8column_background_hightlight_color");
        	
        	String column_fg_kw8  = prop.getProperty ("8column_foreground_highlight_keyword");
        	String column_fg_color8  = prop.getProperty ("8column_foreground_hightlight_color");
        	
        	if(column_fg_kw8 != null)
        		foreground = rendererOperationsForColumnForgroundHighLight (node,column_fg_kw8,column_fg_color8);
        	if(column_bg_kw8 != null)
        		background = rendererOperationsForColumnBackgroundHighLight (node,column_bg_kw8, column_bg_color8);
        	
        	column_info_valueformat = prop.getProperty ("8column_info_valueformat"); 
        	if(column_info_valueformat != null)
        		valuetext = rendererOperationsForColumnInfoFormat (node,value, column_info_valueformat);

        	font = rendererOperationsForColumnFont (node, "hashqgq");
        	break;
        case 9:
        	String column_bg_kw9  = prop.getProperty ("9column_background_highlight_keyword");
        	current_column_bg_kw = column_bg_kw9;
        	String column_bg_color9  = prop.getProperty ("9column_background_hightlight_color");
        	
        	String column_fg_kw9  = prop.getProperty ("9column_foreground_highlight_keyword");
        	String column_fg_color9  = prop.getProperty ("9column_foreground_hightlight_color");
        	
        	if(column_fg_kw9 != null)
        		foreground = rendererOperationsForColumnForgroundHighLight (node,column_fg_kw9,column_fg_color9);
        	if(column_bg_kw9 != null)
        		background = rendererOperationsForColumnBackgroundHighLight (node,column_bg_kw9, column_bg_color9);
        	
        	column_info_valueformat = prop.getProperty ("9column_info_valueformat"); 
        	if(column_info_valueformat != null)
        		valuetext = rendererOperationsForColumnInfoFormat (node,value, column_info_valueformat);

        	font = rendererOperationsForColumnFont (node, "hashqgq");
        	break;

        case 10:
        	String column_bg_kw10  = prop.getProperty ("10column_background_highlight_keyword");
        	current_column_bg_kw = column_bg_kw10;
        	String column_bg_color10  = prop.getProperty ("10column_background_hightlight_color");
        	
        	String column_fg_kw10  = prop.getProperty ("10column_foreground_highlight_keyword");
        	String column_fg_color10  = prop.getProperty ("10column_foreground_hightlight_color");
        	
        	if(column_fg_kw10 != null)
        		foreground = rendererOperationsForColumnForgroundHighLight (node,column_fg_kw10,column_fg_color10);
        	if(column_bg_kw10 != null)
        		background = rendererOperationsForColumnBackgroundHighLight (node,column_bg_kw10, column_bg_color10);
        	
        	column_info_valueformat = prop.getProperty ("10column_info_valueformat"); 
        	if(column_info_valueformat != null)
        		valuetext = rendererOperationsForColumnInfoFormat (node,value, column_info_valueformat);

        	font = rendererOperationsForColumnFont (node, "hashqgq");
        	break;
	    };
	    
		Stock stock = null ; Boolean reviewedtoday = false; 
		if(node instanceof StockOfBanKuai) {
			stock = ((StockOfBanKuai)node).getStock();
			if(stock.wetherHasReiewedToday())
				reviewedtoday = true;
		} else
			reviewedtoday = node.wetherHasReiewedToday();
		
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
	
	private String rendererOperationsForColumnInfoFormat(TDXNodes node, Object value,	String column_info_valueformat)
	{
		if(!column_info_valueformat.equalsIgnoreCase("PERCENT"))
			return "";
		
		if(value == null)
			return null;
		
		if(value.equals("??"))
			return (String) value;
		
		 //用百分比显示
    	String valuepect = "";
    	try {
    		 double formatevalue = NumberFormat.getInstance(Locale.CHINA).parse(value.toString()).doubleValue();
    		 
    		 NumberFormat percentFormat = NumberFormat.getPercentInstance(Locale.CHINA);
    	     percentFormat.setMinimumFractionDigits(2);
        	 valuepect = percentFormat.format (formatevalue );
    	} catch (java.lang.NullPointerException e) {
    		valuepect = "";
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
		case "hashqgq":
			Boolean has = ((StockOfBanKuai)node).getStock().getNodeJiBenMian().hasHqgqGuDong(requireddate);
			if(has) defaultFont =  new Font(defaultFont.getName(), Font.BOLD,defaultFont.getSize());
			else {
				LocalDate maxcbrq = ((StockOfBanKuai)node).getStock().getNodeJiBenMian().getLastestCaiBaoDate();
				has = ((StockOfBanKuai)node).getStock().getNodeJiBenMian().hasHqgqGuDong(maxcbrq);
				if(has) defaultFont =  new Font("Hei", Font.BOLD,defaultFont.getSize());
			}
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
		case "hashqgq":
			Boolean has = ((StockOfBanKuai)node).getStock().getNodeJiBenMian().hasHqgqGuDong(requireddate);
			if(has) foreground = Color.RED;
			break;
		case "extremecjlzhanbi" :
			Double[] extremecjl = node.getNodeCjlZhanbiLevel();
			Double zhanbi = nodexdata.getChenJiaoLiangZhanBi(requireddate, 0);
			if(extremecjl[0] != null && zhanbi < extremecjl[0])
				foreground = Color.RED;
			else
			if(extremecjl[1] != null && zhanbi > extremecjl[1])
				foreground = Color.GREEN;
			break;
		case "extremecjezhanbi" :
			Double[] extremecje = node.getNodeCjeZhanbiLevel();
			Double cjezhanbi = nodexdata.getChenJiaoErZhanBi(requireddate, 0);
			if(extremecje[0] != null && cjezhanbi < extremecje[0])
				foreground = Color.RED;
			else
			if(extremecje[1] != null && cjezhanbi > extremecje[1])
				foreground = Color.GREEN;
			break;
        case "quanzhonginbankuai":
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
        RuleOfLiuTongShiZhi ltszRule =  new RuleOfLiuTongShiZhi ();
        rules.register(ltszRule);
        
        RuleOfZongShiZhi zszRule = new RuleOfZongShiZhi ();
        rules.register(zszRule);
        
        RuleOfGeGuDailyHighestZhangFuInWeek zfRule = new RuleOfGeGuDailyHighestZhangFuInWeek ();
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
        
        NodeXPeriodData nodexdata = node.getNodeXPeroidData(period);
		Color background = Color.white;
		switch (column_keyword) {
		case "extremecjlzhanbi" :
			Double[] extremecjl = node.getNodeCjlZhanbiLevel();
			Double zhanbi = nodexdata.getChenJiaoLiangZhanBi(requireddate, 0);
			if(extremecjl[0] != null && zhanbi < extremecjl[0])
				background = Color.RED;
			else
			if(extremecjl[1] != null && zhanbi > extremecjl[1])
				background = Color.GREEN;
			break;
			
		case "extremecjezhanbi" :
			Double[] extremecje = node.getNodeCjeZhanbiLevel();
			Double cjezhanbi = nodexdata.getChenJiaoErZhanBi(requireddate, 0);
			if(extremecje[0] != null && cjezhanbi < extremecje[0])
				background = Color.RED;
			else
			if(extremecje[1] != null && cjezhanbi > extremecje[1])
				background = Color.GREEN;
			break;
			
        case "zhangdiefu":
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
        	
        case "infengxifile":
        	NodesTreeRelated stofbktree ;
        	if(node instanceof StockOfBanKuai)
        		stofbktree = ((StockOfBanKuai)node).getStock().getNodeTreeRelated();
        	else
        		stofbktree = node.getNodeTreeRelated();
        	if(stofbktree == null)
        		background = Color.white;
        	else {
        		Boolean isin = stofbktree.selfIsMatchModel(requireddate);
    	    	if(isin != null && isin  ) 
    	    		if(predefinedcolor != null && !predefinedcolor.toUpperCase().equals("SYSTEM") )
    			    	background = Color.decode( predefinedcolor );
    	    		else if(predefinedcolor != null && predefinedcolor.toUpperCase().equals("SYSTEM") )
    	    			background = Color.ORANGE;
    			    else background = Color.ORANGE;  
    	    	else 
    	    		background = Color.white;
        	}
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
        case "zongshizhi":
        	if(predefinedcolor != null && !predefinedcolor.toUpperCase().equals("SYSTEM") && ltszRule.getAnalysisResult())
		    	background = Color.decode( predefinedcolor );
        	else if(predefinedcolor != null && predefinedcolor.toUpperCase().equals("SYSTEM") )
        		background = zszRule.getBackGround ();
		    else  	background = zszRule.getBackGround ();
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
