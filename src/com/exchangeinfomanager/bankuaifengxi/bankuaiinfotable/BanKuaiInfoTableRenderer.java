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
	    
	    tablemodel =  (BanKuaiInfoTableModel)table.getModel();
   	    
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
        
        int rowcurselected = table.getSelectedRow();
        if(rowcurselected != -1) {
        	int modelRowcurselected = table.convertRowIndexToModel(rowcurselected);
        	String bkcode = bankuai.getMyOwnCode();
			BanKuai bkcurselected = (BanKuai) ((BanKuaiInfoTableModel)tablemodel).getNode(modelRowcurselected);  
	        Set<String> socialsetpos = bkcurselected.getSocialFriendsSetPostive();
	        if(socialsetpos.contains(bkcode))  	jc.setBorder( highlightpos );
        	
	        Set<String> socialsetneg = bkcurselected.getSocialFriendsSetNegtive();
	        if(socialsetneg.contains(bkcode))    jc.setBorder( highlightneg );
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
	    
//        if (comp instanceof JLabel && col == 0) {
//        	TDXNodesXPeriodDataForJFC nodexdata = (TDXNodesXPeriodDataForJFC) bankuai.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
//        	Double zhangdiefu = nodexdata.getSpecificOHLCZhangDieFu (curdate,0);
//		    if(zhangdiefu != null  && zhangdiefu > 0 )
//		    	background = Color.RED;
//		    else if(zhangdiefu != null  &&  zhangdiefu < 0 )
//		    	background = Color.GREEN;
//		    else
//		    	background = Color.WHITE;
//        	
//        } 
        
//        BanKuaiGeGuMatchCondition matchcond = tablemodel.getDisplayMatchCondition ();
//        RuleOfCjeZbDpMaxWk dpmaxwkRule = null;
//        RuleOfWeeklyAverageChenJiaoErMaxWk averagecjemaxwkRule = null;
//        if(matchcond != null) {
//        	Facts facts = new Facts();
//	        facts.put("evanode", bankuai);
//	        facts.put("evadate", curdate);
//	        facts.put("evadatedifference", 0);
//	        facts.put("evaperiod", NodeGivenPeriodDataItem.WEEK);
//	        facts.put("evacond", matchcond);
//	        
//	        Rules rules = new Rules();
//	        
//	        dpmaxwkRule = new RuleOfCjeZbDpMaxWk ();
//	        rules.register(dpmaxwkRule);
//	        
//	        averagecjemaxwkRule = new RuleOfWeeklyAverageChenJiaoErMaxWk ();
//	        rules.register(averagecjemaxwkRule);
//	        
//	     // fire rules on known facts
//	        RulesEngine rulesEngine = new DefaultRulesEngine();
//	        rulesEngine.fire(rules, facts);
//        }

//        if (comp instanceof JLabel && ( col == 3 ||   col == 4  )) {
//        	try {
//        		background = new Color(51,204,255);
//        	} catch (java.lang.NullPointerException e) {
//        		background = Color.WHITE;
//        	}
//        }
//        if (comp instanceof JLabel &&   col == 4  ) {
//        	try {
//        		background = dpmaxwkRule.getBackGround();
//        	} catch (java.lang.NullPointerException e) {
//        		background = Color.WHITE;
//        	}
//        }
//        if (comp instanceof JLabel && ( col == 6  ) ) {
//        	try {
//        		background = averagecjemaxwkRule.getBackGround();
//	        } catch (java.lang.NullPointerException e) {
//        		background = Color.WHITE;
//        	}
//        }
//        if (comp instanceof JLabel && ( col == 7  ) ) {
//        	NodeXPeriodData nodexdata = bankuai.getNodeXPeroidData(NodeGivenPeriodDataItem.WEEK);
//        	Integer avgdailycjemaxwk = nodexdata.getAverageDailyChenJiaoErMaxWeekOfSuperBanKuai(curdate,0);
//        	if(avgdailycjemaxwk != null && avgdailycjemaxwk > 0) 
//        		background = Color.RED;
//		    else if ( avgdailycjemaxwk != null && avgdailycjemaxwk <= 0 )
//		       	background = Color.GREEN;
//		    else
//		       	background = Color.WHITE;
//        }
        //"板块代码", "名称","CJE占比增长率","CJE占比","CJL占比增长率","CJL占比","大盘成交额增长贡献率","成交额排名"
//        if (comp instanceof JLabel && (col == 2 ||  col == 3   )) {
//        	String valuetext =  ((JLabel)comp).getText();
//        	if(valuetext == null || valuetext.length() == 0)
//        		return null;
//        	
//        	String valuepect = null;
//        	try {
//        		 double formatevalue = NumberFormat.getInstance(Locale.CHINA).parse(valuetext).doubleValue();
//        		 
//        		 NumberFormat percentFormat = NumberFormat.getPercentInstance(Locale.CHINA);
//	    	    	 percentFormat.setMinimumFractionDigits(1);
//            	 valuepect = percentFormat.format (formatevalue );
//        	} catch (java.lang.NumberFormatException e)   	{
//        		e.printStackTrace();
//        	} catch (ParseException e) {
//				e.printStackTrace();
//			}
//        	((JLabel)comp).setText(valuepect);
//        }
        
//        if( col == 1  ) { //关注/在板块文件中 这2个操作互斥，优先关注板块的颜色
//        	
//        	try {
//        		NodesTreeRelated tmptreerelated = bankuai.getNodeTreeRelated();
//        		Boolean matchmodel = tmptreerelated.selfIsMatchModel(curdate);
//        		if(matchmodel )
//		        	background = Color.ORANGE;
//		        else
//		        	background = Color.white;
//        		
//        		Range<LocalDate> indqgz = bankuai.isInDuanQiGuanZhuRange (curdate);
//        		if(indqgz != null)
//        			background = new Color(102,178,255);
//        		
//        		if(table.isRowSelected(row))
//    	    		background = new Color(102,102,255);
//        		
//        		Range<LocalDate> inqsgz = bankuai.isInQiangShiBanKuaiRange (curdate);
//        		if(inqsgz != null)
//        			foreground = Color.RED;
//        		
//        		Range<LocalDate> inrsgz = bankuai.isInRuoShiBanKuaiRange (curdate);
//        		if(inrsgz != null)
//        			foreground = Color.GREEN;
//	        	
//        	} catch (java.lang.NullPointerException e) {
//        		background = Color.white;
//        		foreground = Color.BLACK;
//        	}
//
//        }
        
//       comp.setBackground(background);
//       comp.setForeground(foreground);

       return comp;
	    
	}

}
