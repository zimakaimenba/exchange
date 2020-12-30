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
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.treerelated.NodesTreeRelated;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

public class BanKuaiGeGuTableRendererFromPropertiesFile extends BanKuaiGeGuBasicTableRenderer 
{
//	private Properties prop;

	public BanKuaiGeGuTableRendererFromPropertiesFile(Properties prop) 
	{
		super (prop);
		
//		this.prop = prop;
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
	    
//	    String columnname = tablemodel.getColumnName(col);
//	    if (comp instanceof JLabel && (columnname.contains("大盘CJEZB增长率") 
//	    		|| columnname.contains("板块成交额贡献") )) { //用百分比显示
//	    	String valuepect = "";
//	    	try {
//        		 double formatevalue = NumberFormat.getInstance(Locale.CHINA).parse(value.toString()).doubleValue();
//        		 
//        		 NumberFormat percentFormat = NumberFormat.getPercentInstance(Locale.CHINA);
//	    	     percentFormat.setMinimumFractionDigits(1);
//            	 valuepect = percentFormat.format (formatevalue );
//        	} catch (java.lang.NullPointerException e) {
//        		valuepect = "";
//	    	}catch (java.lang.NumberFormatException e)   	{
//        		e.printStackTrace();
//        	} catch (ParseException e) {
//				e.printStackTrace();
//			}
//        	((JLabel)comp).setText(valuepect);
//        }
	    
	    Color foreground = Color.BLACK, background = Color.white;
	    Object[] textbackgroundforegroundfont = super.getTableCellRendererBackgroundForegroundColor(table, stockofbank, value, row, col);
	    String valuepect = (String)textbackgroundforegroundfont[0];
	    if(!Strings.isNullOrEmpty(valuepect))
	    	((JLabel)comp).setText(valuepect);
	    
	    background = (Color)textbackgroundforegroundfont[1];
	    comp.setBackground(background);
	    
	    foreground = (Color)textbackgroundforegroundfont[2];
	    comp.setForeground(foreground);
	    
	    Font font = (Font)textbackgroundforegroundfont[3];
	    comp.setFont(font);
	    
    	
//	    String current_column_bg_kw = null;
//	    switch (col) {
//	    case 0:
//        	String column_bg_kw0  = prop.getProperty ("0column_background_highlight_keyword");
//        	current_column_bg_kw = column_bg_kw0;
//        	String column_bg_color0  = prop.getProperty ("0column_background_hightlight_color");
//        	
//        	String column_fg_kw0  = prop.getProperty ("0column_foreground_highlight_keyword");
//        	String column_fg_color0  = prop.getProperty ("0column_foreground_hightlight_color");
//        	
//        	if(column_fg_kw0 != null)
//        		foreground = rendererOperationsForColumnForgroundHighLight (stockofbank,column_fg_kw0,column_fg_color0);
//        	if(column_bg_kw0 != null)
//        		background = rendererOperationsForColumnBackgroundHighLight (stockofbank,column_bg_kw0, column_bg_color0);
//
//        	break;
//        case 1:
//        	String column_bg_kw1  = prop.getProperty ("1column_background_highlight_keyword");
//        	current_column_bg_kw = column_bg_kw1;
//        	String column_bg_color1  = prop.getProperty ("1column_background_hightlight_color");
//        	
//        	String column_fg_kw1  = prop.getProperty ("1column_foreground_highlight_keyword");
//        	String column_fg_color1  = prop.getProperty ("1column_foreground_hightlight_color");
//        	
//        	if(column_fg_kw1 != null)
//        		foreground = rendererOperationsForColumnForgroundHighLight (stockofbank,column_fg_kw1,column_fg_color1);
//        	if(column_bg_kw1 != null)
//        		background = rendererOperationsForColumnBackgroundHighLight (stockofbank,column_bg_kw1, column_bg_color1);
//
//        	break;
//        case 2:
//        	String column_bg_kw2  = prop.getProperty ("2column_background_highlight_keyword");
//        	current_column_bg_kw = column_bg_kw2;
//        	String column_bg_color2  = prop.getProperty ("2column_background_hightlight_color");
//        	
//        	String column_fg_kw2  = prop.getProperty ("2column_foreground_highlight_keyword");
//        	String column_fg_color2  = prop.getProperty ("2column_foreground_hightlight_color");
//        	
//        	if(column_fg_kw2 != null)
//        		foreground = rendererOperationsForColumnForgroundHighLight (stockofbank,column_fg_kw2,column_fg_color2);
//        	if(column_bg_kw2 != null)
//        		background = rendererOperationsForColumnBackgroundHighLight (stockofbank,column_bg_kw2, column_bg_color2);
//
//        	break;
//        case 3:
//        	String column_bg_kw3  = prop.getProperty ("3column_background_highlight_keyword");
//        	current_column_bg_kw = column_bg_kw3;
//        	String column_bg_color3  = prop.getProperty ("3column_background_hightlight_color");
//        	
//        	String column_fg_kw3  = prop.getProperty ("3column_foreground_highlight_keyword");
//        	String column_fg_color3  = prop.getProperty ("3column_foreground_hightlight_color");
//        	
//        	if(column_fg_kw3 != null)
//        		foreground = rendererOperationsForColumnForgroundHighLight (stockofbank,column_fg_kw3,column_fg_color3);
//        	if(column_bg_kw3 != null)
//        		background = rendererOperationsForColumnBackgroundHighLight (stockofbank,column_bg_kw3, column_bg_color3);
//
//        	break;
//        case 4:
//        	String column_bg_kw4  = prop.getProperty ("4column_background_highlight_keyword");
//        	current_column_bg_kw = column_bg_kw4;
//        	String column_bg_color4  = prop.getProperty ("4column_background_hightlight_color");
//        	
//        	String column_fg_kw4  = prop.getProperty ("4column_foreground_highlight_keyword");
//        	String column_fg_color4  = prop.getProperty ("4column_foreground_hightlight_color");
//        	
//        	if(column_fg_kw4 != null)
//        		foreground = rendererOperationsForColumnForgroundHighLight (stockofbank,column_fg_kw4,column_fg_color4);
//        	if(column_bg_kw4 != null)
//        		background = rendererOperationsForColumnBackgroundHighLight (stockofbank,column_bg_kw4, column_bg_color4);
//
//        	break;
//        case 5:
//        	String column_bg_kw5  = prop.getProperty ("5column_background_highlight_keyword");
//        	current_column_bg_kw = column_bg_kw5;
//        	String column_bg_color5  = prop.getProperty ("5column_background_hightlight_color");
//        	
//        	String column_fg_kw5  = prop.getProperty ("5column_foreground_highlight_keyword");
//        	String column_fg_color5  = prop.getProperty ("5column_foreground_hightlight_color");
//        	
//        	if(column_fg_kw5 != null)
//        		foreground = rendererOperationsForColumnForgroundHighLight (stockofbank,column_fg_kw5,column_fg_color5 );
//        	if(column_bg_kw5 != null)
//        		background = rendererOperationsForColumnBackgroundHighLight (stockofbank,column_bg_kw5, column_bg_color5);
//
//        	break;
//        case 6:
//        	String column_bg_kw6  = prop.getProperty ("6column_background_highlight_keyword");
//        	current_column_bg_kw = column_bg_kw6;
//        	String column_bg_color6  = prop.getProperty ("6column_background_hightlight_color");
//        	
//        	String column_fg_kw6  = prop.getProperty ("6column_foreground_highlight_keyword");
//        	String column_fg_color6  = prop.getProperty ("6column_foreground_hightlight_color");
//        	
//        	if(column_fg_kw6 != null)
//        		foreground = rendererOperationsForColumnForgroundHighLight (stockofbank,column_fg_kw6,column_fg_color6);
//        	if(column_bg_kw6 != null)
//        		background = rendererOperationsForColumnBackgroundHighLight (stockofbank,column_bg_kw6, column_bg_color6);
//
//        	break;
//        case 7:
//        	String column_bg_kw7  = prop.getProperty ("7column_background_highlight_keyword");
//        	current_column_bg_kw = column_bg_kw7;
//        	String column_bg_color7  = prop.getProperty ("7column_background_hightlight_color");
//        	
//        	String column_fg_kw7  = prop.getProperty ("7column_foreground_highlight_keyword");
//        	String column_fg_color7  = prop.getProperty ("7column_foreground_hightlight_color");
//        	
//        	if(column_fg_kw7 != null)
//        		foreground = rendererOperationsForColumnForgroundHighLight (stockofbank,column_fg_kw7,column_fg_color7);
//        	if(column_bg_kw7 != null)
//        		background = rendererOperationsForColumnBackgroundHighLight (stockofbank,column_bg_kw7, column_bg_color7);
//
//        	break;
//	    };
	    
//	    String current_column_bg_kw = prop.getProperty ( String.valueOf(col) + "column_background_highlight_keyword");;
//	    if(stock.wetherHasReiewedToday() && table.isRowSelected(row) ) {
//        	Font defaultFont = this.getFont();
//        	Font font = new Font(defaultFont.getName(), Font.ITALIC,defaultFont.getSize());
//        	comp.setFont(font);
//        } else
//        if(stock.wetherHasReiewedToday() && !table.isRowSelected(row) && !current_column_bg_kw.equalsIgnoreCase("infengxifile") ) { //已经浏览过的个股，全部灰色，不会混淆，更加清晰
//            	Font defaultFont = this.getFont();
//            	Font font = new Font(defaultFont.getName(), Font.ITALIC,defaultFont.getSize());
//            	comp.setFont(font);
//            	background = Color.gray;
//        } else
//        if (stock.wetherHasReiewedToday() && !table.isRowSelected(row) && current_column_bg_kw.equalsIgnoreCase("infengxifile") ) {//已经浏览过的个股，全部灰色，不会混淆，更加清晰
//        	Font defaultFont = this.getFont();
//        	Font font = new Font(defaultFont.getName(), Font.ITALIC,defaultFont.getSize());
//        	comp.setFont(font);
//        	if(background == Color.WHITE)
//        		background = Color.gray;
//        }
//
//	    if(table.isRowSelected(row)) {
//		    String rowselectedindex = prop.getProperty ("rowselectedindex");
//		    if(rowselectedindex == null && col == 1)
//	    		background = new Color(102,102,255);
//		    else
//			if(rowselectedindex != null && col == Integer.parseInt(rowselectedindex.trim() ) )  //	    if( columnname.equals("名称") ) { //个股名称
//	    		background = new Color(102,102,255);
//	    }
	    
//	    comp.setBackground(background);
//    	comp.setForeground(foreground);
    	
	    return comp;
	}
	
//	private String  getCompValue (String value, String columnkeywords)
//	{
//		String valueresult = "";
//		
//		switch (columnkeywords) {
//		case "bankuaichengjiaoergongxian" :
//			try {
//       		 double formatevalue = NumberFormat.getInstance(Locale.CHINA).parse(value.toString()).doubleValue();
//       		 
//       		 NumberFormat percentFormat = NumberFormat.getPercentInstance(Locale.CHINA);
//	    	     percentFormat.setMinimumFractionDigits(1);
//	    	     valueresult = percentFormat.format (formatevalue );
//			} catch (java.lang.NullPointerException e) {
//				valueresult = "";
//	    	}catch (java.lang.NumberFormatException e)   	{
//	    		e.printStackTrace();
//	    	} catch (ParseException e) {
//				e.printStackTrace();
//			}
//			break;
//		}
//		
//		return value;
//	}
	
}


