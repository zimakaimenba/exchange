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
import org.jeasy.rules.annotation.Rule;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.BasicRule;
import org.jeasy.rules.core.DefaultRulesEngine;
//import org.bouncycastle.util.Integers;
import org.jfree.data.time.ohlc.OHLCItem;
//import org.jfree.data.time.ohlc.OHLCSeries;

import com.exchangeinfomanager.Tag.Tag;
import com.exchangeinfomanager.bankuaifengxi.BanKuaiGeGuMatchCondition;
import com.exchangeinfomanager.bankuaifengxi.CandleStick.CandleStickColorFactory;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfCjeZbDpMaxWk;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfGeGuPrice;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfGeGuZhangFu;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfLiuTongShiZhi;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfMA;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfQueKou;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfWeeklyAverageChenJiaoErMaxWk;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.StockOfBanKuai;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.StockNodesXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForJFC.StockXPeriodDataForJFC;
import com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForJFC.TDXNodesXPeriodDataForJFC;
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
//		{ "����", "����","�߼���������","���ɽ����","����CJEZB������","CJEDpMaxWk","����CJLZB������","CJLDpMaxWk"};
		
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
	    //������ʱ���ɱ�����ͻ����ʾ�������иð������и���
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
	    
	    if (comp instanceof JLabel && (columnname.contains("����CJEZB������") 
	    		|| columnname.contains("���ɽ����") )) { //�ðٷֱ���ʾ
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
	    LocalDate requireddate = tablemodel.getShowCurDate();
	    String period = tablemodel.getCurDisplayPeriod();
	    
	    Facts facts = new Facts();
        facts.put("evanode", stock);
        facts.put("evadate", requireddate);
        facts.put("evaperiod", period);
        facts.put("evacond", matchcond);
        
        Rules rules = new Rules();
        RuleOfLiuTongShiZhi cjeRule =  new RuleOfLiuTongShiZhi ();
        rules.register(cjeRule);
        
        RuleOfGeGuZhangFu zfRule = new RuleOfGeGuZhangFu ();
        rules.register(zfRule);
        
        RuleOfGeGuPrice priceRule = new RuleOfGeGuPrice ();
        rules.register(priceRule);
        
        RuleOfQueKou qkRule = new RuleOfQueKou ();
        rules.register(qkRule);
        
        RuleOfCjeZbDpMaxWk dpmaxwkRule = new RuleOfCjeZbDpMaxWk ();
        rules.register(dpmaxwkRule);
        
        RuleOfWeeklyAverageChenJiaoErMaxWk averagecjemaxwkRule = new RuleOfWeeklyAverageChenJiaoErMaxWk ();
        rules.register(averagecjemaxwkRule);
        
        RuleOfMA maRule = new RuleOfMA ();
        rules.register(maRule);
     // fire rules on known facts
        RulesEngine rulesEngine = new DefaultRulesEngine();
        rulesEngine.fire(rules, facts);
        
	    
	    if( comp instanceof JLabel &&  columnname.contains("����")  ) { //��ǰѡ��ѡ�� // ||   stock.wetherHasReiewedToday()
	    	
		    NodeXPeriodData nodexdata = stock.getNodeXPeroidData(period);
		    Double zhangdiefu = nodexdata.getSpecificOHLCZhangDieFu (requireddate,0);
		    background = CandleStickColorFactory.getCandelStickColor(zhangdiefu);
	    } 
	    
	    if( columnname.equals("����") ) { //��������
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
	    if( columnname.contains("��������")) { //��ͨ��ֵ col == 2
	         background = cjeRule.getBackGround ();
	    } else  
	    if( columnname.contains("����CJEZB������")  ) { //�Ƿ�>= col == 4  �͹ɼ�����
	    	background = zfRule.getBackGround();
			foreground = priceRule.getForeGround();
	    }  else 
	    if(columnname.contains("���ɽ����")   ) { //ͻ���ز�ȱ�� col == 3
	    	background = qkRule.getBackGround();
	    } else  
	    if( columnname.contains("CJEDpMaxWk")    && value != null  ) { 	    //ͻ����ʾcjedpMAXWK>=�ĸ��� col == 5
	    	background = dpmaxwkRule.getBackGround(); 
	    } else  
	    if( columnname.contains("��ƽ���ɽ���MAXWK")   && value != null  ) { //col == 7
	    	background = averagecjemaxwkRule.getBackGround();
	    } else 
	    if( columnname.contains("����ƽ���ɽ�������")   && value != null) { //ͻ��MA,Ĭ��Ϊ���� 
	    	background = maRule.getBackGround();
	    }
	    
	    comp.setBackground(background);
    	comp.setForeground(foreground);
    	
	    return comp;
	}
	
}
