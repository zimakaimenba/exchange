package com.exchangeinfomanager.bankuaifengxi.bankuaigegutable;

import java.awt.Color;

import java.awt.Component;
import java.awt.Font;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.log4j.Logger;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;

import com.exchangeinfomanager.Tag.Tag;
import com.exchangeinfomanager.bankuaifengxi.BanKuaiGeGuMatchCondition;
import com.exchangeinfomanager.bankuaifengxi.CandleStick.CandleStickColorFactory;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfCjeZbDpMaxWk;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfCjeZbGrowingRate;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfGeGuPrice;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfGeGuZhangFu;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfLiuTongShiZhi;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfMA;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfQueKou;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfWeeklyAverageChenJiaoErMaxWk;
import com.exchangeinfomanager.bankuaifengxi.bankuaigegubasictable.BanKuaiGeGuBasicTableModel;
import com.exchangeinfomanager.bankuaifengxi.bankuaigegubasictable.BanKuaiGeGuBasicTableRenderer;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.treerelated.NodesTreeRelated;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public class BanKuaiGeGuTableRendererFromPropertiesFile extends BanKuaiGeGuBasicTableRenderer 
{
	private Properties prop;

	public BanKuaiGeGuTableRendererFromPropertiesFile(Properties prop) 
	{
		super ();
		
		this.prop = prop;
	}
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(BanKuaiGeGuBasicTableRenderer.class);
//	BanKuaiGeGuTableModelFromPropertiesFile tablemodel;
	//
	private Border outsidefortag = new MatteBorder(1, 0, 1, 0, Color.RED);
	private Border insidefortag = new EmptyBorder(0, 1, 0, 1);
	private Border highlightfortag = new CompoundBorder(outsidefortag, insidefortag);
	//
	private Border outsideforintersectionbk = new MatteBorder(1, 0, 1, 0, Color.BLUE.brighter());
	private Border insideforintersectionbk = new EmptyBorder(0, 1, 0, 1);
	private Border highlightforintersectionbk = new CompoundBorder(outsideforintersectionbk, insideforintersectionbk);

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row,int col) 
	{
//		{ "代码", "名称","高级排序排名","板块成交额贡献","大盘CJEZB增长率","CJEDpMaxWk","大盘CJLZB增长率","CJLDpMaxWk"};
		
	    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
	    JComponent jc = (JComponent)comp;
	    
	    tablemodel =  (BanKuaiGeGuTableModelFromPropertiesFile)table.getModel() ;
   	    
	    int modelRow = table.convertRowIndexToModel(row);
	    StockOfBanKuai stockofbank = ( (BanKuaiGeGuBasicTableModel)table.getModel() ).getStock(modelRow);
	    Stock stock = stockofbank.getStock();
	    
	    if(stock.wetherHasReiewedToday() ) {
        	Font defaultFont = this.getFont();
        	Font font = new Font(defaultFont.getName(), Font.BOLD + Font.ITALIC,defaultFont.getSize());
        	comp.setFont(font);
        }
	    //对于临时个股表，可以突出显示出正表中该板块的所有个股
	    BanKuai interbk = tablemodel.getInterSetctionBanKuai();
	    if(interbk!= null) {
	    	if( interbk.getBanKuaiGeGu (stock.getMyOwnCode())  != null)
	    		jc.setBorder( highlightforintersectionbk );
	    }
	    //
	    Collection<Tag> keywordsset = tablemodel.getCurrentHighlightKeyWords ();
	    if(keywordsset != null) {
	    	Collection<Tag> curtags = stock.getNodeTags ();
		    SetView<Tag> intersection = Sets.intersection((HashSet<Tag>)keywordsset, (HashSet<Tag>)curtags);
		    if( !intersection.isEmpty() && intersection.size() == keywordsset.size() ) {
		    	jc.setBorder( highlightfortag );
		    }
	    }
	    
	    String columnname = tablemodel.getColumnName(col);
	    if (comp instanceof JLabel && (columnname.contains("大盘CJEZB增长率") 
	    		|| columnname.contains("板块成交额贡献") )) { //用百分比显示
	    	String valuepect = "";
	    	try {
        		 double formatevalue = NumberFormat.getInstance(Locale.CHINA).parse(value.toString()).doubleValue();
        		 
        		 NumberFormat percentFormat = NumberFormat.getPercentInstance(Locale.CHINA);
	    	     percentFormat.setMinimumFractionDigits(1);
            	 valuepect = percentFormat.format (formatevalue );
        	} catch (java.lang.NullPointerException e) {
        		valuepect = "";
	    	}catch (java.lang.NumberFormatException e)   	{
        		e.printStackTrace();
        	} catch (ParseException e) {
//				e.printStackTrace();
			}
        	((JLabel)comp).setText(valuepect);
        }
	    
	    Color foreground = Color.BLACK, background = Color.white;
	    
	    switch (col) {
	    case 0:
        	String column_bg_kw0  = prop.getProperty ("0column_background_highlight_keyword");
        	String column_bg_color0  = prop.getProperty ("0column_background_hightlight_color");
        	
        	String column_fg_kw0  = prop.getProperty ("0column_foreground_highlight_keyword");
        	String column_fg_color0  = prop.getProperty ("0column_foreground_hightlight_color");
        	
        	if(column_fg_kw0 != null)
        		foreground = rendererOperationsForColumnForgroundHighLight (stockofbank,column_fg_kw0,column_fg_color0);
        	if(column_bg_kw0 != null)
        		background = rendererOperationsForColumnBackgroundHighLight (stockofbank,column_bg_kw0, column_bg_color0);
//        	rendererOperationsForColumnInfomation (stock,column_kw0);
        	break;
        case 1:
        	String column_bg_kw1  = prop.getProperty ("1column_background_highlight_keyword");
        	String column_bg_color1  = prop.getProperty ("1column_background_hightlight_color");
        	
        	String column_fg_kw1  = prop.getProperty ("1column_foreground_highlight_keyword");
        	String column_fg_color1  = prop.getProperty ("1column_foreground_hightlight_color");
        	
        	if(column_fg_kw1 != null)
        		foreground = rendererOperationsForColumnForgroundHighLight (stockofbank,column_fg_kw1,column_fg_color1);
        	if(column_bg_kw1 != null)
        		background = rendererOperationsForColumnBackgroundHighLight (stockofbank,column_bg_kw1, column_bg_color1);
//        	rendererOperationsForColumnInfomation (stock,column_kw0);
        	break;
        case 2:
        	String column_bg_kw2  = prop.getProperty ("2column_background_highlight_keyword");
        	String column_bg_color2  = prop.getProperty ("2column_background_hightlight_color");
        	
        	String column_fg_kw2  = prop.getProperty ("2column_foreground_highlight_keyword");
        	String column_fg_color2  = prop.getProperty ("2column_foreground_hightlight_color");
        	
        	if(column_fg_kw2 != null)
        		foreground = rendererOperationsForColumnForgroundHighLight (stockofbank,column_fg_kw2,column_fg_color2);
        	if(column_bg_kw2 != null)
        		background = rendererOperationsForColumnBackgroundHighLight (stockofbank,column_bg_kw2, column_bg_color2);
//        	rendererOperationsForColumnInfomation (stock,column_kw0);
        	break;
        case 3:
        	String column_bg_kw3  = prop.getProperty ("3column_background_highlight_keyword");
        	String column_bg_color3  = prop.getProperty ("3column_background_hightlight_color");
        	
        	String column_fg_kw3  = prop.getProperty ("3column_foreground_highlight_keyword");
        	String column_fg_color3  = prop.getProperty ("3column_foreground_hightlight_color");
        	
        	if(column_fg_kw3 != null)
        		foreground = rendererOperationsForColumnForgroundHighLight (stockofbank,column_fg_kw3,column_fg_color3);
        	if(column_bg_kw3 != null)
        		background = rendererOperationsForColumnBackgroundHighLight (stockofbank,column_bg_kw3, column_bg_color3);
//        	rendererOperationsForColumnInfomation (stock,column_kw0);
        	break;
        case 4:
        	String column_bg_kw4  = prop.getProperty ("4column_background_highlight_keyword");
        	String column_bg_color4  = prop.getProperty ("4column_background_hightlight_color");
        	
        	String column_fg_kw4  = prop.getProperty ("4column_foreground_highlight_keyword");
        	String column_fg_color4  = prop.getProperty ("4column_foreground_hightlight_color");
        	
        	if(column_fg_kw4 != null)
        		foreground = rendererOperationsForColumnForgroundHighLight (stockofbank,column_fg_kw4,column_fg_color4);
        	if(column_bg_kw4 != null)
        		background = rendererOperationsForColumnBackgroundHighLight (stockofbank,column_bg_kw4, column_bg_color4);
//        	rendererOperationsForColumnInfomation (stock,column_kw0);
        	break;
        case 5:
        	String column_bg_kw5  = prop.getProperty ("5column_background_highlight_keyword");
        	String column_bg_color5  = prop.getProperty ("5column_background_hightlight_color");
        	
        	String column_fg_kw5  = prop.getProperty ("5column_foreground_highlight_keyword");
        	String column_fg_color5  = prop.getProperty ("5column_foreground_hightlight_color");
        	
        	if(column_fg_kw5 != null)
        		foreground = rendererOperationsForColumnForgroundHighLight (stockofbank,column_fg_kw5,column_fg_color5 );
        	if(column_bg_kw5 != null)
        		background = rendererOperationsForColumnBackgroundHighLight (stockofbank,column_bg_kw5, column_bg_color5);
//        	rendererOperationsForColumnInfomation (stock,column_kw0);
        	break;
        case 6:
        	String column_bg_kw6  = prop.getProperty ("6column_background_highlight_keyword");
        	String column_bg_color6  = prop.getProperty ("6column_background_hightlight_color");
        	
        	String column_fg_kw6  = prop.getProperty ("6column_foreground_highlight_keyword");
        	String column_fg_color6  = prop.getProperty ("6column_foreground_hightlight_color");
        	
        	if(column_fg_kw6 != null)
        		foreground = rendererOperationsForColumnForgroundHighLight (stockofbank,column_fg_kw6,column_fg_color6);
        	if(column_bg_kw6 != null)
        		background = rendererOperationsForColumnBackgroundHighLight (stockofbank,column_bg_kw6, column_bg_color6);
//        	rendererOperationsForColumnInfomation (stock,column_kw0);
        	break;
        case 7:
        	String column_bg_kw7  = prop.getProperty ("7column_background_highlight_keyword");
        	String column_bg_color7  = prop.getProperty ("7column_background_hightlight_color");
        	
        	String column_fg_kw7  = prop.getProperty ("7column_foreground_highlight_keyword");
        	String column_fg_color7  = prop.getProperty ("7column_foreground_hightlight_color");
        	
        	if(column_fg_kw7 != null)
        		foreground = rendererOperationsForColumnForgroundHighLight (stockofbank,column_fg_kw7,column_fg_color7);
        	if(column_bg_kw7 != null)
        		background = rendererOperationsForColumnBackgroundHighLight (stockofbank,column_bg_kw7, column_bg_color7);
//        	rendererOperationsForColumnInfomation (stock,column_kw0);
        	break;
	    };
	    
	    if( columnname.equals("名称") ) { //个股名称
	    	if(table.isRowSelected(row))
	    		background = new Color(102,102,255);
	    } 
	    
	    comp.setBackground(background);
    	comp.setForeground(foreground);
    	
	    return comp;
	}
//	/*
//	 * 
//	 */
//	private Color rendererOperationsForColumnForgroundHighLight ( StockOfBanKuai stockofbank, String column_keyword )
//	{
//		Color foreground = Color.BLACK;
//		
//		Stock stock = stockofbank.getStock();
//		LocalDate requireddate = tablemodel.getShowCurDate();
//	    String period = tablemodel.getCurDisplayPeriod();
//	    BanKuaiGeGuMatchCondition matchcond = tablemodel.getDisplayMatchCondition ();
//		
//	    Facts lwfacts = new Facts();
//        lwfacts.put("evanode", stock);
//        lwfacts.put("evadate", requireddate);
//        lwfacts.put("evadatedifference", -1);
//        lwfacts.put("evaperiod", period);
//        lwfacts.put("evacond", matchcond);
//        
//        Rules lwrules = new Rules();
//        
//        RuleOfCjeZbDpMaxWk lwdpmaxwkRule = new RuleOfCjeZbDpMaxWk ();
//        lwrules.register(lwdpmaxwkRule);
//        
//        RuleOfWeeklyAverageChenJiaoErMaxWk lwaveragecjemaxwk = new RuleOfWeeklyAverageChenJiaoErMaxWk ();
//        lwrules.register(lwaveragecjemaxwk);
//        
//        RuleOfCjeZbGrowingRate lwcjezbgr = new RuleOfCjeZbGrowingRate ();
//        lwrules.register(lwcjezbgr);
//        
//        RulesEngine lwrulesEngine = new DefaultRulesEngine();
//        try {
//        	lwrulesEngine.fire(lwrules, lwfacts);
//        } catch (Exception ex  ) {
//        	ex.printStackTrace();
//        }
//        
//        NodeXPeriodData nodexdata = stock.getNodeXPeroidData(period);
//    	Double lwzhangdiefu = nodexdata.getSpecificOHLCZhangDieFu (requireddate,-1);
//    	Double zhangdiefu = nodexdata.getSpecificOHLCZhangDieFu (requireddate,0);
//    	
//		switch (column_keyword) {
//        case "quanzhonginbankuai":
//	    	Integer weight = stockofbank.getBanKuai().getGeGuSuoShuBanKuaiWeight( stock.getMyOwnCode() );
//	    	if(weight == null)
//	    		foreground = Color.BLACK;
//	    	else
//	    	if(stockofbank.isBkLongTou())
//	    		foreground = Color.RED;
//	    	else 
//	    	if(weight <6 && weight >3) 
//	    		foreground = new Color(128,128,128);
//	    	else 
//	    	if(weight <=3 )
//	    		foreground = new Color(192,192,192);
//	    	else
//	    		foreground = Color.BLACK;
//        	break;
//        
//        case "lastwkdpcjezbgrowingrate":
//        	foreground = lwcjezbgr.getForeGround();
//        	break;
//        case "lastwkdpcjezbmatch":
//        	
//	    	if(lwzhangdiefu != null &&  lwzhangdiefu <0 && lwdpmaxwkRule.getRuleResult() && zhangdiefu !=null &&  zhangdiefu >0 )
//	    		foreground = Color.YELLOW;
//        	break;
//        case "lastwkaveragecjematch" :
//	    	if(lwzhangdiefu != null && lwzhangdiefu <0 && lwaveragecjemaxwk.getRuleResult() && zhangdiefu != null && zhangdiefu >0 )
//	    		foreground = Color.YELLOW;
//        	break;
//	    };
//	    
//	    return foreground;
//	}
//	/*
//	 * 
//	 */
//	private void rendererOperationsForColumnInfomation ( StockOfBanKuai stockofbank, String column_keyword ) 
//	{
//		switch (column_keyword) {
//        case "":
//        	
//        	break;
//	    };
//		
//	}
//	/*
//	 * 
//	 */
//	private Color rendererOperationsForColumnBackgroundHighLight ( StockOfBanKuai stockofbank, String column_keyword ) 
//	{
//		Stock stock = stockofbank.getStock();
//		LocalDate requireddate = tablemodel.getShowCurDate();
//	    String period = tablemodel.getCurDisplayPeriod();
//	    BanKuaiGeGuMatchCondition matchcond = tablemodel.getDisplayMatchCondition ();
//		
//		Facts facts = new Facts();
//        facts.put("evanode", stock);
//        facts.put("evadate", requireddate);
//        facts.put("evadatedifference", 0);
//        facts.put("evaperiod", period);
//        facts.put("evacond", matchcond);
//        
//        Rules rules = new Rules();
//        RuleOfLiuTongShiZhi ltszRule =  new RuleOfLiuTongShiZhi ();
//        rules.register(ltszRule);
//        
//        RuleOfGeGuZhangFu zfRule = new RuleOfGeGuZhangFu ();
//        rules.register(zfRule);
//        
//        RuleOfGeGuPrice priceRule = new RuleOfGeGuPrice ();
//        rules.register(priceRule);
//        
//        RuleOfQueKou qkRule = new RuleOfQueKou ();
//        rules.register(qkRule);
//        
//        RuleOfCjeZbDpMaxWk cjezbdpmaxwkRule = new RuleOfCjeZbDpMaxWk ();
//        rules.register(cjezbdpmaxwkRule);
//        
//        RuleOfWeeklyAverageChenJiaoErMaxWk averagecjemaxwkRule = new RuleOfWeeklyAverageChenJiaoErMaxWk ();
//        rules.register(averagecjemaxwkRule);
//        
//        RuleOfMA maRule = new RuleOfMA ();
//        rules.register(maRule);
//     // fire rules on known facts
//        RulesEngine rulesEngine = new DefaultRulesEngine();
//        rulesEngine.fire(rules, facts);
//        
//		Color background = Color.white;
//		switch (column_keyword) {
//        case "zhangdiefu":
//        	NodeXPeriodData nodexdata = stock.getNodeXPeroidData(period);
//		    Double zhangdiefu = nodexdata.getSpecificOHLCZhangDieFu (requireddate,0);
//		    background = CandleStickColorFactory.getCandelStickColor(zhangdiefu);
//        	break;
//        	
//        case "infengxifile":
//        	NodesTreeRelated stofbktree = stock.getNodeTreeRelated();
//        	
//	 		Boolean isin = stofbktree.selfIsMatchModel(requireddate);
//	    	if(isin != null && isin  ) 
//		    		background = Color.ORANGE;  
//	    	else 
//	    		background = Color.white;
//        	break;
//        case "quekouzhangfu":
//        	if(qkRule.getRuleResult() || zfRule.getRuleResult() )	
//	    		background = Color.PINK;
//        	break;
//        case "CjeZbDpMaxWk":
//        	background = cjezbdpmaxwkRule.getBackGround();
//        	break;
//        case "dayujunxian" :
//        	background = maRule.getBackGround();
//        	break;
//        case "averagecjemaxwk" :
//        	background = averagecjemaxwkRule.getBackGround();
//        	break;
//        case "liutongshizhi":
//        	background = ltszRule.getBackGround ();
//        	break;
//	    };
//	    
//	    return background;
//		
//	}
	
}


