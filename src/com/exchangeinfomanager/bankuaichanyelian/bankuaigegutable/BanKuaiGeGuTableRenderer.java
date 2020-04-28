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
import com.exchangeinfomanager.bankuaifengxi.CandleStick.CandleStickColorFactory;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.StockNodesXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForJFC.StockXPeriodDataForJFC;
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
	    		|| columnname.contains("板块成交额贡献") || columnname.contains("大盘CJLZB增长率"))) { //用百分比显示  col == 3 || col == 4 || col == 6
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
	    
	    if( comp instanceof JLabel &&  columnname.contains("代码")  ) { //当前选择选择 // ||   stock.wetherHasReiewedToday()
	    	LocalDate requireddate = tablemodel.getShowCurDate();
		    String period = tablemodel.getCurDisplayPeriod();
		    NodeXPeriodData nodexdata = stock.getNodeXPeroidData(period);
		    Double zhangdiefu = nodexdata.getSpecificOHLCZhangDieFu (requireddate,0);
		    background = CandleStickColorFactory.getCandelStickColor(zhangdiefu);
	    } 
	    
	    if( columnname.equals("名称") ) { //个股名称
	    	LocalDate requireddate = tablemodel.getShowCurDate();
	 		NodesTreeRelated stofbktree = stock.getNodeTreeRelated();
    	
	 		Boolean isin = stofbktree.selfIsMatchModel(requireddate);
	    	if(isin != null && isin  ) 
		    		background = Color.ORANGE;  
	    	else 
	    		background = Color.white;
	    	
	    	if(table.isRowSelected(row))
	    		background = new Color(102,102,255);
	    	
	    	Integer weight = stockofbank.getBanKuai().getGeGuSuoShuBanKuaiWeight(stock.getMyOwnCode());
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
	    } else 
	    if( columnname.contains("排序排名")) { //流通市值 col == 2
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
		    if(curltsz == null) //有时候周一网易的数据还没有导入，导致没有流通市值数据，先用上一周的数据顶一下，毕竟不会相差太大
		    	curltsz = ((StockNodesXPeriodData)nodexdata).getSpecificTimeLiuTongShiZhi(requireddate, -1);
		    try {
			    if( curltsz >= ltszmin && curltsz <= ltszmax ) {
			    	if(curltsz >= 1500000000.0 && curltsz <= 3900000000.0)  //对于这个范围内的个股要特别重视，特别显示
			    		background = new Color(201,0,102) ;
			    	else  
			    		background = Color.MAGENTA ;
			    }
			    else
			    	background = Color.white;
		    } catch (java.lang.NullPointerException e) {
		    	background = Color.white;
		    }
	    	
	    } else  
	    if( columnname.contains("大盘CJEZB增长率")  ) { //涨幅>= col == 4
	    	Double zfmax = matchcond.getSettingZhangFuMax();
	    	Double zfmin = matchcond.getSettingZhangFuMin();
	    	
	    	if(zfmax == null && zfmin == null ) {
	    		background = Color.white;
	    	} else {
	    		if(zfmax == null && zfmin != null )
		    		zfmax = 1000000.0;
		    	else 
		    	if(zfmax != null && zfmin == null )
		    		zfmin = -1000000.0;
		    	else
		    	if(zfmax == null && zfmin == null ) {
		    		zfmin = 1000000.0;
		    		zfmax = -1000000.0;
		    	}

		    	LocalDate requireddate = tablemodel.getShowCurDate();
			    String period = tablemodel.getCurDisplayPeriod();
			    StockXPeriodDataForJFC nodexdata = (StockXPeriodDataForJFC)stock.getNodeXPeroidData(period);//   bk.getStockXPeriodDataForABanKuai(stockofbank.getMyOwnCode(), period);
			    Double wkzhangfu = nodexdata.getSpecificTimeHighestZhangDieFu(requireddate, 0);
			    if(wkzhangfu == null)
			    	background = Color.white;
			    else if( wkzhangfu >= zfmin && wkzhangfu <= zfmax ) 
			    	background = Color.pink ;
			    else
			    	background = Color.white;
	    	}
	    	
	    }  else 
	    if(columnname.contains("板块成交额贡献")   ) { //突出回补缺口 col == 3
	    	background = Color.white ;
		    Boolean hlqk = matchcond.hasHuiBuDownQueKou();
		    if(hlqk ) {
		    	LocalDate requireddate = tablemodel.getShowCurDate();
			    String period = tablemodel.getCurDisplayPeriod();
		    	NodeXPeriodData nodexdata = stock.getNodeXPeroidData(period);//   bk.getStockXPeriodDataForABanKuai(stockofbank.getMyOwnCode(), period);
			    Integer hbqkdown = nodexdata.getQueKouTongJiHuiBuDown(requireddate, 0);
			    Integer openupqk = nodexdata.getQueKouTongJiOpenUp(requireddate, 0);
			    
			    if( (hbqkdown != null && hbqkdown >0) ||  (openupqk != null && openupqk>0)  )
			    	 background = Color.PINK ;
		    	else
		    		background = Color.white ;
		    }
	    } else  
	    if( columnname.contains("CJEDpMaxWk")    && value != null  ) { 	    //突出显示cjedpMAXWK>=的个股 col == 5
	    	int cjedpmaxwk = Integer.parseInt( tablemodel.getValueAt(modelRow, col).toString() );
	    	
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
		    	Integer lianxuflnum = nodexdata.getCjeDpMaxLianXuFangLiangPeriodNumber (requireddate,0, maxfazhi);
		    	 
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
	    if( columnname.contains("周平均成交额MAXWK")   && value != null  ) { //col == 7
	    	int dpmaxwk = Integer.parseInt( tablemodel.getValueAt(modelRow, col).toString() );
	    	
	    	Integer cjemaxwk = matchcond.getSettingChenJiaoErMaxWk();
	    	if(cjemaxwk == null)
	    		cjemaxwk =  10000000;
	    	
	    	if( dpmaxwk >=cjemaxwk )
	    		 background = Color.CYAN ;
	    	else 
	    		 background = Color.white ;
	    } else 
	    if( columnname.contains("大盘CJLZB增长率")   && value != null) { //突出MA,默认为大于 col == 6
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
