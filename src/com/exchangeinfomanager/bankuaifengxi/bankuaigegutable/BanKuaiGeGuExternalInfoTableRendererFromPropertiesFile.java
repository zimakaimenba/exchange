package com.exchangeinfomanager.bankuaifengxi.bankuaigegutable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.time.LocalDate;
import java.util.Properties;

import javax.swing.JComponent;
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
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfGeGuPrice;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfGeGuZhangFu;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfHuanShouLv;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfLiuTongShiZhi;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfMA;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfQueKou;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfWeeklyAverageChenJiaoErMaxWk;
import com.exchangeinfomanager.bankuaifengxi.bankuaigegubasictable.BanKuaiGeGuBasicTableModel;
import com.exchangeinfomanager.bankuaifengxi.bankuaigegubasictable.BanKuaiGeGuBasicTableRenderer;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.treerelated.NodesTreeRelated;

public class BanKuaiGeGuExternalInfoTableRendererFromPropertiesFile extends BanKuaiGeGuBasicTableRenderer 
{
	private Properties prop;
//	private BanKuaiGeGuExternalInfoTableModelFromPropertiesFile tablemodel;

	public BanKuaiGeGuExternalInfoTableRendererFromPropertiesFile(Properties prop) 
	{
		super ();
		
		this.prop = prop;
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row,int col) 
	{
//		{ "代码", "名称","高级排序排名","板块成交额贡献","大盘CJEZB增长率","CJEDpMaxWk","大盘CJLZB增长率","CJLDpMaxWk"};
		
	    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
	    JComponent jc = (JComponent)comp;
	    
	    tablemodel =  (BanKuaiGeGuExternalInfoTableModelFromPropertiesFile)table.getModel() ;
   	    
	    int modelRow = table.convertRowIndexToModel(row);
	    StockOfBanKuai stockofbank = ( (BanKuaiGeGuBasicTableModel)table.getModel() ).getStock(modelRow);
	    Stock stock = stockofbank.getStock();
	    
	    if(stock.wetherHasReiewedToday() ) {
        	Font defaultFont = this.getFont();
        	Font font = new Font(defaultFont.getName(), Font.BOLD + Font.ITALIC,defaultFont.getSize());
        	comp.setFont(font);
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
	    };
	    
	    String columnname = tablemodel.getColumnName(col);
	    if( columnname.equals("名称") ) { //个股名称
	    	if(table.isRowSelected(row))
	    		background = new Color(102,102,255);
	    } 
	    
	    comp.setBackground(background);
    	comp.setForeground(foreground);
    	
	    return comp;
	}
	
	
}
