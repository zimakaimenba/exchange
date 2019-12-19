package com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.log4j.Logger;
import org.bouncycastle.util.Integers;
import org.jfree.data.time.ohlc.OHLCItem;

import com.exchangeinfomanager.Tag.Tag;
import com.exchangeinfomanager.bankuaifengxi.BanKuaiGeGuMatchCondition;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.StockNodesXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.nodes.treerelated.NodesTreeRelated;
import com.exchangeinfomanager.nodes.treerelated.StockOfBanKuaiTreeRelated;
import com.google.common.base.Strings;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.udojava.evalex.Expression;


public class BanKuaiGeGuTableRenderer extends DefaultTableCellRenderer 
{

	public BanKuaiGeGuTableRenderer() 
	{
		super ();
	}
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(BanKuaiGeGuTableRenderer.class);
	
	private Border outside = new MatteBorder(1, 0, 1, 0, Color.RED);
	private Border inside = new EmptyBorder(0, 1, 0, 1);
	private Border highlight = new CompoundBorder(outside, inside);

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row,int col) 
	{
//		{ "代码", "名称","高级排序排名","板块成交额贡献","大盘CJEZB增长率","CJEDpMaxWk","大盘CJLZB增长率","CJLDpMaxWk"};
		
	    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
	    JComponent jc = (JComponent)comp;
	    
	    if (comp instanceof JLabel && (col == 3 || col == 4 || col == 6)) { //用百分比显示
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
				e.printStackTrace();
			}
        	((JLabel)comp).setText(valuepect);
        }
	    
	    BanKuaiGeGuTableModel tablemodel =  (BanKuaiGeGuTableModel)table.getModel() ;
	    BanKuaiGeGuMatchCondition matchcond = tablemodel.getDisplayMatchCondition ();
	    int modelRow = table.convertRowIndexToModel(row);
	    StockOfBanKuai stockofbank = ( (BanKuaiGeGuTableModel)table.getModel() ).getStock(modelRow);
	    Stock stock = stockofbank.getStock();
	    
	    if(stock.wetherHasReiewedToday()) {
        	Font defaultFont = this.getFont();
        	Font font = new Font(defaultFont.getName(), Font.BOLD + Font.ITALIC,defaultFont.getSize());
        	comp.setFont(font);
        }
	    
	    
	    Collection<Tag> keywordsset = tablemodel.getCurrentHighlightKeyWords ();
	    if(keywordsset != null) {
	    	Collection<Tag> curtags = stock.getNodeTags ();
		    SetView<Tag> intersection = Sets.intersection((HashSet<Tag>)keywordsset, (HashSet<Tag>)curtags);
		    if( !intersection.isEmpty() && intersection.size() == keywordsset.size() ) {
		    	jc.setBorder( highlight );
		    }
	    }
	    
	    
	    Color foreground = Color.BLACK, background = Color.white;
	    
	    if( (table.isRowSelected(row) || stock.wetherHasReiewedToday() ) && col == 0 ) { //当前选择选择
	    	LocalDate requireddate = tablemodel.getShowCurDate();
		    String period = tablemodel.getCurDisplayPeriod();
		    NodeXPeriodData nodexdata = stock.getNodeXPeroidData(period);
		    Double zhangdiefu = nodexdata.getSpecificOHLCZhangDieFu (requireddate,0);
	    
		    if(zhangdiefu != null  && zhangdiefu > 0 )
		    	background = Color.RED;
		    else if(zhangdiefu != null  &&  zhangdiefu < 0 )
		    	background = Color.GREEN;
		    else
		    	background = Color.WHITE;
//		    //周线阳线
//		    if(close >= closelast && zhangtingnum >0 && dieting == 0)
//		    	background =  new Color(255,255,255);
//		    else if(close >= closelast && dieting >0 && zhangtingnum >0)
//		    	background = new Color(255,102,102);
//		    else if(close >= closelast && dieting >0 && zhangtingnum == 0)
//		    	background = new Color(255,128,0);
//		    else if(close >= closelast && dieting == 0 && zhangtingnum ==0)
//		    	background = new Color(255,153,153);
//		    //周线阴线
//		    else if(close < closelast && dieting == 0 && zhangtingnum >0)
//		    	background = new Color(204,153,153)	;
//		    else if(close < closelast && dieting >0 && zhangtingnum >0)
//		    	background = new Color(128,255,0);
//		    else if(close < closelast && dieting >0 && zhangtingnum ==0)
//		    	background = new Color(0,153,0);
//		    else if(close < closelast && dieting == 0 && zhangtingnum ==0)
//		    	background = new Color(51,255,51);
	    } 
	    
	    if( col == 1 ) { //个股名称
	    	LocalDate requireddate = tablemodel.getShowCurDate();
	 		NodesTreeRelated stofbktree = stock.getNodeTreeRelated();
    	
	 		Boolean isin = stofbktree.selfIsMatchModel(requireddate);
	    	if(isin != null && isin  ) 
		    		background = Color.ORANGE;  
	    	else
		    		background = Color.white;
	    	
	    	if(stockofbank.isBkLongTou())
	    		foreground = Color.RED;
	    	else 
	    		foreground = Color.BLACK;

	    } else 
	    if( col == 2) { //流通市值
	    	Double ltszmin ;
		    Double ltszmax ;
		    try {
		    	ltszmax = matchcond.getSettingLiuTongShiZhiMax().doubleValue() *  100000000;
		    } catch (Exception e) {
		    	ltszmax = 10000000000000.0;
		    }
		    try {
		    	ltszmin = matchcond.getSettingLiuTongShiZhiMin() * 100000000;
		    } catch (Exception e) {
		    	ltszmin = 10000000000000.0;
		    }
		    LocalDate requireddate = tablemodel.getShowCurDate();
		    String period = tablemodel.getCurDisplayPeriod();
		    NodeXPeriodData nodexdata = stock.getNodeXPeroidData(period);//   bk.getStockXPeriodDataForABanKuai(stockofbank.getMyOwnCode(), period);
		    Double curltsz = ((StockNodesXPeriodData)nodexdata).getSpecificTimeLiuTongShiZhi(requireddate, 0);
		    try {
			    if( curltsz >= ltszmin && curltsz <= ltszmax ) 
			    	background = Color.MAGENTA ;
			    else
			    	background = Color.white;
		    } catch (java.lang.NullPointerException e) {
		    	background = Color.white;
		    }
	    	
	    } else  
	    if( col == 4 && value != null ) { //成交额>=
	    	Double cjemax = matchcond.getSettingChenJiaoErMax();
	    	Double cjemin = matchcond.getSettingChenJiaoErMin();
	    	if(cjemin != null && cjemax == null) {
	    		cjemax = 1000000000000.0;
			} else
			if(cjemax != null && cjemin == null) {
				cjemin  = 0.0;
			} else
			if(cjemin == null && cjemax == null) {
				cjemax = 1000000000000.0;
				cjemin = 1000000000000.0;
			}
	    	cjemax = cjemax * 100000000.0;
			cjemin = cjemin * 100000000.0;
	    
		    LocalDate requireddate = tablemodel.getShowCurDate();
		    String period = tablemodel.getCurDisplayPeriod();
		    NodeXPeriodData nodexdata = stock.getNodeXPeroidData(period);//   bk.getStockXPeriodDataForABanKuai(stockofbank.getMyOwnCode(), period);
		    Double curcje = nodexdata.getChengJiaoEr(requireddate, 0);
		    if( curcje >= cjemin && curcje <= cjemax ) 
		    	background = Color.yellow ;
		    else
		    	background = Color.white;
	    }  else 
	    if(col == 3   && value != null) { //突出回补缺口
	    	Boolean zt = matchcond.hasZhangTing();
		    Boolean hlqk = matchcond.hasHuiBuDownQueKou();
		    if(!hlqk && !zt)
		    	background = Color.white ;
		    else {
		    	LocalDate requireddate = tablemodel.getShowCurDate();
			    String period = tablemodel.getCurDisplayPeriod();
		    	NodeXPeriodData nodexdata = stock.getNodeXPeroidData(period);//   bk.getStockXPeriodDataForABanKuai(stockofbank.getMyOwnCode(), period);
			    Integer hbqkdown = nodexdata.getQueKouTongJiHuiBuDown(requireddate, 0);
			    Integer openupqk = nodexdata.getQueKouTongJiOpenUp(requireddate, 0);
			    Integer zhangting = nodexdata.getZhangTingTongJi(requireddate, 0);
			    
			    if( (hbqkdown != null && hbqkdown >0) ||  (openupqk != null && openupqk>0)  )
			    	 background = Color.PINK ;
		    	else 
		    	if( zhangting != null && zhangting >0)
		    		background = Color.PINK ;
		    	else
		    		background = Color.white ;
		    }
	    } else  
	    if( col == 5    && value != null  ) { 	    //突出显示cjedpMAXWK>=的个股
	    	int cjedpmaxwk = Integer.parseInt( tablemodel.getValueAt(modelRow, 5).toString() );
	    	
	    	if(cjedpmaxwk > 0 ) {
	    		int maxfazhi;
		    	try {
		    		maxfazhi = matchcond.getSettingDpMaxWk();
		    	} catch (java.lang.NullPointerException e) {
		    		maxfazhi = 100000000;
		    	}
		    	
	    		LocalDate requireddate = tablemodel.getShowCurDate();
			    String period = tablemodel.getCurDisplayPeriod();
			    NodeXPeriodData nodexdata = stock.getNodeXPeroidData(period);//   bk.getStockXPeriodDataForABanKuai(stockofbank.getMyOwnCode(), period);
		    	Integer lianxuflnum = nodexdata.getCjeLianXuFangLiangPeriodNumber (requireddate,0, maxfazhi);
		    	 
		    	if(cjedpmaxwk >= maxfazhi &&  lianxuflnum >=2 ) //连续放量,深色显示
		    		background = new Color(102,0,0) ;
		    	else if( cjedpmaxwk >= maxfazhi &&  lianxuflnum <2 )
		    		 background = new Color(255,0,0) ;
		    	else 
		    		 background = Color.white ;
	    	} 
	    	
	    	if(cjedpmaxwk < 0 ) {
	    		int minfazhi;
		    	try {
		    		minfazhi = matchcond.getSettingDpMinWk();
		    	} catch (java.lang.NullPointerException e) {
		    		minfazhi = 100000000;
		    	}
		    	
	    		minfazhi = 0 - minfazhi; //min都用负数表示
	    		if(cjedpmaxwk <= minfazhi)
	    			background = Color.GREEN ;
	    		else
	    			background = Color.white ;
	    	} 
	    } else  
	    if( col == 7   && value != null  ) { 	    //突出显示cjldpMAXWK>=的个股
		    	int cjldpmaxwk = Integer.parseInt( tablemodel.getValueAt(modelRow, 7).toString() );
		    	
		    	if(cjldpmaxwk > 0 ) {
		    		int maxfazhi;
			    	try {
			    		maxfazhi = matchcond.getSettingDpMaxWk();
			    	} catch (java.lang.NullPointerException e) {
			    		maxfazhi = 100000000;
			    	}
			    	
		    		LocalDate requireddate = tablemodel.getShowCurDate();
				    String period = tablemodel.getCurDisplayPeriod();
				    NodeXPeriodData nodexdata = stock.getNodeXPeroidData(period);//   bk.getStockXPeriodDataForABanKuai(stockofbank.getMyOwnCode(), period);
			    	Integer lianxuflnum = nodexdata.getCjlLianXuFangLiangPeriodNumber (requireddate,0, maxfazhi);
			    	 
			    	if(cjldpmaxwk >= maxfazhi &&  lianxuflnum >=2 ) //连续放量,深色显示
			    		background = new Color(102,0,0) ;
			    	else if( cjldpmaxwk >= maxfazhi &&  lianxuflnum <2 )
			    		 background = new Color(255,0,0) ;
			    	else 
			    		 background = Color.white ;
		    	}
		    	
		    	if(cjldpmaxwk < 0 ) {
		    		int minfazhi;
			    	try {
			    		minfazhi = matchcond.getSettingDpMinWk();
			    	} catch (java.lang.NullPointerException e) {
			    		minfazhi = 100000000;
			    	}
			    	
		    		minfazhi = 0 - minfazhi; //min都用负数表示
		    		if(cjldpmaxwk <= minfazhi)
		    			background = Color.GREEN ;
		    		else
		    			background = Color.white ;
		    	}
	    } else 
	    if( col == 6   && value != null) { //突出MA,默认为大于
	    	String displayma = matchcond.getSettingMaFormula();
	    	background = Color.white ;
	    	if (!Strings.isNullOrEmpty(displayma)) {
	    		NodeXPeriodData nodexdataday = stock.getNodeXPeroidData(NodeGivenPeriodDataItem.DAY);
			    
			    LocalDate requireddate = tablemodel.getShowCurDate();
			    Boolean checkresult = nodexdataday.checkCloseComparingToMAFormula(displayma,requireddate,0);
			    if( checkresult != null && checkresult)
				    background = new Color(0,153,153) ;
	    	}
	    		
	    }
	    
	    comp.setBackground(background);
    	comp.setForeground(foreground);
    	
	    return comp;
	}
	
}
