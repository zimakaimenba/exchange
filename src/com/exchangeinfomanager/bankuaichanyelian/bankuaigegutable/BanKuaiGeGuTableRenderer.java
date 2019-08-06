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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.apache.log4j.Logger;
import org.jfree.data.time.ohlc.OHLCItem;

import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.nodexdata.NodeXPeriodDataBasic;
import com.exchangeinfomanager.nodes.nodexdata.StockNodeXPeriodData;
import com.exchangeinfomanager.nodes.nodexdata.TDXNodeGivenPeriodDataItem;
import com.exchangeinfomanager.nodes.nodexdata.TDXNodesXPeriodData;
import com.exchangeinfomanager.nodes.treerelated.StockOfBanKuaiTreeRelated;
import com.udojava.evalex.Expression;


public class BanKuaiGeGuTableRenderer extends DefaultTableCellRenderer 
{

	public BanKuaiGeGuTableRenderer() 
	{
		super ();
	}
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(BanKuaiGeGuTableRenderer.class);

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row,int col) 
	{
//		{ "代码", "名称","高级排序排名","板块成交额贡献","大盘CJEZB增长率","CJEDpMaxWk","大盘CJLZB增长率","CJLDpMaxWk"};
		
	    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

	    String valuepect = "";
	    if (comp instanceof JLabel && (col == 3 || col == 4 || col == 6)) { //用百分比显示

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
	    int modelRow = table.convertRowIndexToModel(row);
	    StockOfBanKuai stockofbank = ( (BanKuaiGeGuTableModel)table.getModel() ).getStock(modelRow);
	    Stock stock = stockofbank.getStock();
	    BanKuai bankuai = stockofbank.getBanKuai();
	    
	    if(stock.wetherHasReiewedToday()) {
        	Font defaultFont = this.getFont();
        	Font font = new Font(defaultFont.getName(), Font.BOLD + Font.ITALIC,defaultFont.getSize());
        	comp.setFont(font);
        }
	    
	    Color foreground = Color.BLACK, background = Color.white;

	    if( col == 1 ) { //个股名称
	    	LocalDate requireddate = tablemodel.getShowCurDate();
	 		StockOfBanKuaiTreeRelated stofbktree = (StockOfBanKuaiTreeRelated)stockofbank.getNodeTreeRelated();
    	
	 		Boolean isin = stofbktree.isInBanKuaiFengXiResultFileForSpecificDate(requireddate);
	    	if(isin != null && isin  ) 
		    		background = Color.ORANGE;  
	    	else
		    		background = Color.white;
	    	
	    	if(stockofbank.isBkLongTou())
	    		foreground = Color.RED;
	    	else 
	    		foreground = Color.BLACK;
	    } else if( col == 2) { //流通市值
	    	Double ltszmin = tablemodel.getDisplayLiuTongShiZhiMin() ;
		    Double ltszmax = tablemodel.getDisplayLiuTongShiZhiMax() ;
		    
		    LocalDate requireddate = tablemodel.getShowCurDate();
		    String period = tablemodel.getCurDisplayPeriod();
		    StockNodeXPeriodData nodexdata = (StockNodeXPeriodData)stock.getNodeXPeroidData(period);//   bk.getStockXPeriodDataForABanKuai(stockofbank.getMyOwnCode(), period);
		    Double curltsz = nodexdata.getSpecificTimeLiuTongShiZhi(requireddate, 0);
		    try {
			    if( curltsz >= ltszmin && curltsz <= ltszmax ) 
			    	background = Color.MAGENTA ;
			    else
			    	background = Color.white;
		    } catch (java.lang.NullPointerException e) {
		    	background = Color.white;
		    }
	    	
	    } else  if( col == 4 && value != null ) { //成交额>=
		    Double cjemin = tablemodel.getDisplayChenJiaoErMin ();
		    Double cjemax = tablemodel.getDisplayChenJiaoErMax ();
		    
		    LocalDate requireddate = tablemodel.getShowCurDate();
		    String period = tablemodel.getCurDisplayPeriod();
		    NodeXPeriodDataBasic nodexdata = stock.getNodeXPeroidData(period);//   bk.getStockXPeriodDataForABanKuai(stockofbank.getMyOwnCode(), period);
		    Double curcje = nodexdata.getChengJiaoEr(requireddate, 0);
		    if( curcje >= cjemin && curcje <= cjemax ) 
		    	background = Color.yellow ;
		    else
		    	background = Color.white;
	    }  else if(col == 3   && value != null) { //突出回补缺口
	    	
		    Boolean hlqk = tablemodel.shouldHighlightHuiBuDownQueKou();
		    if(!hlqk)
		    	background = Color.white ;
		    else {
		    	LocalDate requireddate = tablemodel.getShowCurDate();
			    String period = tablemodel.getCurDisplayPeriod();
		    	TDXNodesXPeriodData nodexdata = (TDXNodesXPeriodData)stock.getNodeXPeroidData(period);//   bk.getStockXPeriodDataForABanKuai(stockofbank.getMyOwnCode(), period);
			    Integer hbqkdown = nodexdata.getQueKouTongJiHuiBuDown(requireddate, 0);
			    Integer openupqk = nodexdata.getQueKouTongJiOpenUp(requireddate, 0);
			    if( (hbqkdown != null && hbqkdown >0) ||  (openupqk != null && openupqk>0)  )
			    	 background = Color.PINK ;
		    	 else 
		    		 background = Color.white ;
		    }
		    
	    } else  if( col == 5    && value != null  ) { 	    //突出显示cjedpMAXWK>=的个股
	    	int cjedpmaxwk = Integer.parseInt( tablemodel.getValueAt(modelRow, 5).toString() );
	    	int maxfazhi = tablemodel.getDisplayCjeZhanBiDPMaxWk();
	    	if(cjedpmaxwk > 0 ) {
	    		LocalDate requireddate = tablemodel.getShowCurDate();
			    String period = tablemodel.getCurDisplayPeriod();
			    TDXNodesXPeriodData nodexdata = (TDXNodesXPeriodData)stock.getNodeXPeroidData(period);//   bk.getStockXPeriodDataForABanKuai(stockofbank.getMyOwnCode(), period);
		    	Integer lianxuflnum = nodexdata.getCjeLianXuFangLiangPeriodNumber (requireddate,0, maxfazhi);
		    	 
		    	if(cjedpmaxwk >= maxfazhi &&  lianxuflnum >=2 ) //连续放量,深色显示
		    		background = new Color(102,0,0) ;
		    	else if( cjedpmaxwk >= maxfazhi &&  lianxuflnum <2 )
		    		 background = new Color(255,0,0) ;
		    	else 
		    		 background = Color.white ;
	    	} else {
	    		int minfazhi = 0 - tablemodel.getDisplayCjeZhanBiDPMinWk(); //min都用负数表示
	    		if(cjedpmaxwk <= minfazhi)
	    			background = Color.GREEN ;
	    		else
	    			background = Color.white ;
	    		
	    	} 
	    } else  if( col == 7   && value != null  ) { 	    //突出显示cjldpMAXWK>=的个股
		    	int cjldpmaxwk = Integer.parseInt( tablemodel.getValueAt(modelRow, 7).toString() );
		    	int maxfazhi = tablemodel.getDisplayCjeZhanBiDPMaxWk();
		    	if(cjldpmaxwk > 0 ) {
		    		LocalDate requireddate = tablemodel.getShowCurDate();
				    String period = tablemodel.getCurDisplayPeriod();
				    TDXNodesXPeriodData nodexdata = (TDXNodesXPeriodData)stock.getNodeXPeroidData(period);//   bk.getStockXPeriodDataForABanKuai(stockofbank.getMyOwnCode(), period);
			    	Integer lianxuflnum = nodexdata.getCjlLianXuFangLiangPeriodNumber (requireddate,0, maxfazhi);
			    	 
			    	if(cjldpmaxwk >= maxfazhi &&  lianxuflnum >=2 ) //连续放量,深色显示
			    		background = new Color(102,0,0) ;
			    	else if( cjldpmaxwk >= maxfazhi &&  lianxuflnum <2 )
			    		 background = new Color(255,0,0) ;
			    	else 
			    		 background = Color.white ;
		    	} else {
		    		int minfazhi = 0 - tablemodel.getDisplayCjeZhanBiDPMinWk(); //min都用负数表示
		    		if(cjldpmaxwk <= minfazhi)
		    			background = Color.GREEN ;
		    		else
		    			background = Color.white ;
		    	}
	    } else if( col == 6   && value != null) { //突出MA,默认为大于
	    	String displayma = tablemodel.getDisplayMAFormula();
	    	background = Color.white ;
	    	if (displayma != null) {
			    TDXNodesXPeriodData nodexdataday = (TDXNodesXPeriodData)stock.getNodeXPeroidData(TDXNodeGivenPeriodDataItem.DAY);
			    
			    LocalDate requireddate = tablemodel.getShowCurDate();
			    LocalDate tmpdate = requireddate.with(DayOfWeek.FRIDAY);
			    
			    OHLCItem ohlcdata;
			    do { //日线数据比较复杂 ，用户选择的日子可能不是交易日，可能是停牌日，就直接算周五，如果周五没有数据，往前1天，直到有数据为止或本周结束
			    	ohlcdata = nodexdataday.getSpecificDateOHLCData(tmpdate, 0);
			    	if(ohlcdata != null)  //没有停牌
			    		break;
			    	
			    	tmpdate = tmpdate.minus(1,ChronoUnit.DAYS);
			    } while (CommonUtility.isInSameWeek(requireddate, tmpdate) );
			    
			    if (ohlcdata != null) {
			    	Double close = (Double)ohlcdata.getCloseValue();
				    Double[] maresult = nodexdataday.getNodeOhlcMA(tmpdate, 0);
				    Boolean result = checkCloseComparingToMAsettings (close,maresult,displayma);
				    if( result != null && result)
				    	background = new Color(0,153,153) ;
			    }
	    	}
	    		
	    }
 
    	comp.setBackground(background);
    	comp.setForeground(foreground);
    	
	    if(table.isRowSelected(row) && col == 0 ) {
	    	LocalDate requireddate = tablemodel.getShowCurDate();
		    String period = tablemodel.getCurDisplayPeriod();
		    TDXNodesXPeriodData nodexdata = (TDXNodesXPeriodData)stock.getNodeXPeroidData(period);
		    OHLCItem ohlcdata = nodexdata.getSpecificDateOHLCData(requireddate, 0);
		    double open = ohlcdata.getOpenValue();
		    double close = ohlcdata.getCloseValue();
		    if(close >= open)
		    	comp.setBackground(Color.RED);
		    else
		    	comp.setBackground(Color.GREEN);
	    }
	    
	    return comp;
	}
	
	private Boolean checkCloseComparingToMAsettings (Double close,Double[] maresult,String maformula)
	{
		try{
			if(maformula.contains(">\'250\'") || maformula.contains(">=\'250\'") || maformula.contains("<\'250\'") || maformula.contains("<=\'250\'") ) {
				if (maresult[6] != null)
					maformula = maformula.replace("\'250\'",  maresult[6].toString() ) ;
				else
					maformula = maformula.replace("\'250\'",  String.valueOf( 10000000000.0 ) ) ;
			}
		} catch (java.lang.NullPointerException e) {
			e.printStackTrace();
		}
		
	    if(maformula.contains(">\'120\'") || maformula.contains(">=\'120\'") || maformula.contains("<\'120\'") || maformula.contains("<=\'120\'")) {
	    	if (maresult[5] != null)
	    		maformula = maformula.replace("\'120\'",  maresult[5].toString() ) ;
	    	else
				maformula = maformula.replace("\'120\'",  String.valueOf( 10000000000.0 ) ) ;
	    }
	    
	    if(maformula.contains(">\'60\'") || maformula.contains(">=\'60\'") || maformula.contains("<\'60\'") || maformula.contains("<=\'60\'") ) {
	    	if(maresult[4] != null)
	    		maformula = maformula.replace("\'60\'",  maresult[4].toString() ) ;
	    	else
				maformula = maformula.replace("\'60\'",  String.valueOf( 10000000000.0 ) ) ;
	    }
	    	
	    
	    if(maformula.contains(">\'30\'") || maformula.contains(">=\'30\'") || maformula.contains("<\'30\'") || maformula.contains("<=\'30\'") ) {
	    	if(maresult[3] != null)
	    		maformula = maformula.replace("\'30\'",  maresult[3].toString() ) ;
	    	else
				maformula = maformula.replace("\'30\'",  String.valueOf( 10000000000.0 ) ) ;
	    }
	    
	    if(maformula.contains(">\'20\'") || maformula.contains(">=\'20\'") || maformula.contains("<\'20\'") || maformula.contains("<=\'20\'") ) {
	    	if(maresult[2] != null)
	    		maformula = maformula.replace("\'20\'",  maresult[2].toString() ) ;
	    	else
				maformula = maformula.replace("\'20\'",  String.valueOf( 10000000000.0 ) ) ;
	    }
	    
	    if(maformula.contains(">\'10\'") || maformula.contains(">=\'10\'") || maformula.contains("<\'10\'") || maformula.contains("<=\'10\'")) {
	    	if(maresult[1] != null)
	    		maformula = maformula.replace("\'10\'",  maresult[1].toString() ) ;
	    	else
				maformula = maformula.replace("\'10\'",  String.valueOf( 10000000000.0 ) ) ;
	    }
	    
	    if(maformula.contains(">\'5\'") || maformula.contains(">=\'5\'") || maformula.contains("<\'5\'") || maformula.contains("<=\'5\'") ) {
	    	if(maresult[0] != null)
	    		maformula = maformula.replace("\'5\'",  maresult[0].toString() ) ;
	    	else
				maformula = maformula.replace("\'5\'",  String.valueOf( 10000000000.0 ) ) ;
	    }
	    
	    BigDecimal result1 = new Expression(maformula).with("x",String.valueOf(close)).eval(); //https://github.com/uklimaschewski/EvalEx
	    String sesultstr = result1.toString();
	    if(sesultstr.equals("0"))
	    	return false;
	    else 
	    	return true;
	    
	    
//		ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
//	      Map<String, Object> vars = new HashMap<String, Object>();
//	      vars.put("x", close);
////	      vars.put("y", 2);
////	      vars.put("z", 1);
//      try {
//    	  Boolean result = (Boolean)engine.eval(maformula, new SimpleBindings(vars));
//		  return result;
//		} catch (ScriptException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return null;
//		}
      

	}
	
}
