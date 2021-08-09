package com.exchangeinfomanager.bankuaifengxi.bankuaiinfotable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;

import com.exchangeinfomanager.Core.Nodes.BanKuai;
import com.exchangeinfomanager.Core.Nodes.Stock;
import com.exchangeinfomanager.Core.Nodes.StockOfBanKuai;
import com.exchangeinfomanager.Core.Nodexdata.NodexdataForJFC.TDXNodesXPeriodDataForJFC;
import com.exchangeinfomanager.Core.Nodexdata.ohlcvaprimarydata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.Core.exportimportrelated.NodesTreeRelated;
import com.exchangeinfomanager.bankuaifengxi.BankuaiAndGeguTableBasic.BanKuaiAndGeguTableBasicRenderer;
import com.exchangeinfomanager.bankuaifengxi.BankuaiAndGeguTableBasic.BandKuaiAndGeGuTableBasicModel;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfCjeZbDpMaxWk;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfWeeklyAverageChenJiaoErMaxWk;
import com.exchangeinfomanager.bankuaifengxi.bankuaigegutable.BanKuaiGeGuBasicTableModel;
import com.exchangeinfomanager.bankuaifengxi.bankuaigegutable.BanKuaiGeGuBasicTableRenderer;
import com.exchangeinfomanager.bankuaifengxi.bankuaigegutable.BanKuaiGeGuTableModelFromPropertiesFile;
import com.google.common.base.Strings;
import com.google.common.collect.Range;

public class BanKuaiInfoTableRenderer extends BanKuaiAndGeguTableBasicRenderer 
{

//	private BanKuaiInfoTableModel tablemodel;

	protected BanKuaiInfoTableRenderer(Properties prop1) {
		super(prop1);
	}
	
	private Border outsidepos = new MatteBorder(1, 0, 1, 0, Color.RED);
	private Border insidepos = new EmptyBorder(0, 1, 0, 1);
	private Border highlightpos = new CompoundBorder(outsidepos, insidepos);
	
	private Border outsideneg = new MatteBorder(1, 0, 1, 0, Color.GREEN);
	private Border insideneg = new EmptyBorder(0, 1, 0, 1);
	private Border highlightneg = new CompoundBorder(outsideneg, insideneg);
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row,int col) 
	{
//		{ "代码", "名称","高级排序排名","板块成交额贡献","大盘CJEZB增长率","CJEDpMaxWk","大盘CJLZB增长率","CJLDpMaxWk"};
		
	    Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
	    JComponent jc = (JComponent)comp;
	    
	    int modelRow = table.convertRowIndexToModel(row);
	    BanKuai bankuai = (BanKuai) ( (BanKuaiInfoTableModel)table.getModel() ).getNode(modelRow);
	    
	    String bktype = bankuai.getBanKuaiLeiXing();
        if(bktype.equals(BanKuai.NOGGWITHSELFCJL)) {
        	Font defaultFont = this.getFont();
        	Font font = new Font(defaultFont.getName(),Font.ITALIC,defaultFont.getSize());
        	comp.setFont(font);
        }
        
      //为不同情况突出显示不同的颜色
        Color foreground = super.getForeground(), background = Color.white;
        if(!bankuai.getBanKuaiOperationSetting().isExportTowWlyFile() )	foreground = Color.GRAY;
        
        tablemodel = (BanKuaiInfoTableModel) table.getModel();
        BanKuai intsbk = ((BanKuaiInfoTableModel)tablemodel).getInterSectionBanKuai();
        if(intsbk != null) {
	        Set<String> socialsetpos = intsbk.getSocialFriendsSetPostive();
	        if(socialsetpos.contains(bankuai.getMyOwnCode()))  	
	        	jc.setBorder( highlightpos );
        	
	        Set<String> socialsetneg = intsbk.getSocialFriendsSetNegtive();
	        if(socialsetneg.contains(bankuai.getMyOwnCode()))
	        	jc.setBorder( highlightneg );
        }
       
	    Object[] textbackgroundforegroundfont = super.getTableCellRendererBackgroundForegroundColor(table, bankuai, value, row, col);
	    String valuepect = (String)textbackgroundforegroundfont[0];
	    if(!Strings.isNullOrEmpty(valuepect))
	    	((JLabel)comp).setText(valuepect);
	    
	    background = (Color)textbackgroundforegroundfont[1];
	    comp.setBackground(background);
	    
	    foreground = (Color)textbackgroundforegroundfont[2];
	    comp.setForeground(foreground);
	    
	    Font font = (Font)textbackgroundforegroundfont[3];
	    comp.setFont(font);

	    return comp;
	    
	}
	
}
