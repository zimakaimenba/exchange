package com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules;

import java.awt.Color;
import java.time.LocalDate;

import org.apache.log4j.Logger;
import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Priority;
import org.jeasy.rules.annotation.Rule;

import com.exchangeinfomanager.Core.Nodes.TDXNodes;
import com.exchangeinfomanager.Core.Nodexdata.NodeXPeriodData;
import com.exchangeinfomanager.bankuaifengxi.HighlightAndExportNodes.BanKuaiAndGeGuMatchingConditions;
import com.exchangeinfomanager.bankuaifengxi.bankuaigegutable.BanKuaiGeGuBasicTable;

@Rule(name = "ChenJiaoEr ZhanBi MaxWk Rule", description = "if it rains then take an umbrella" )
public class RuleOfCjeZbDpMaxWk 
{
	private static Logger logger = Logger.getLogger(RuleOfCjeZbDpMaxWk.class);
	
	private Color foreground = Color.BLACK, background = Color.white;
	boolean lianxufangliang = false;
	String analysisresultforvoice = "";
	boolean iszjezbdpmatched = false;
	@Condition
	public boolean evaluate(@Fact("evanode") TDXNodes evanode,
			@Fact("evadate") LocalDate evadate,  
			@Fact("evaperiod") String evaperiod,
    		@Fact("evacond") BanKuaiAndGeGuMatchingConditions evacond ) 
	{
		NodeXPeriodData nodexdata = evanode.getNodeXPeroidData(evaperiod);
		Integer cjedpmaxwk = nodexdata.getChenJiaoErZhanBiMaxWeekForDaPan (evadate);
		if(cjedpmaxwk!= null && cjedpmaxwk > 0 ) {
    		int settingmaxfazhi;
	    	try { settingmaxfazhi = evacond.getSettingCjeZbDpMaxWkMin();
	    	} catch (java.lang.NullPointerException e) { 		settingmaxfazhi = 100000000;}
	    	
	    	Integer lianxuflnum = nodexdata.getChenJiaoErZhanBiDpMaxWkLianXuFangLiangPeriodNumber (evadate, settingmaxfazhi);
	    	 
	    	if(cjedpmaxwk >= settingmaxfazhi &&  lianxuflnum >=2 ) {//连续放量,深色显示
	    		background = new Color(102,0,0) ;
	    		analysisresultforvoice = analysisresultforvoice + "成交额占比MAXWEEK等于" + cjedpmaxwk + "周。" + "成交额占比连续达标" + lianxuflnum + "周。"; 
	    		return true;
	    	}
	    	else if( cjedpmaxwk >= settingmaxfazhi &&  lianxuflnum <2 ) {
	    		background = new Color(255,0,0) ;
	    		analysisresultforvoice = analysisresultforvoice + "成交额占比MAXWEEK等于" + cjedpmaxwk + "周。";
	    		return true;
	    	}
	    	else 
	    		 return false;
    	} 
    	
    	if(cjedpmaxwk == null || cjedpmaxwk <= 0 ) {
    		Integer cjedpminwk = null;
    		try { cjedpminwk = 0- nodexdata.getChenJiaoErZhanBiMinWeekForDaPan (evadate);
    		} catch (java.lang.NullPointerException ex) { 
//    			ex.printStackTrace();
    			return false;}
    		
    		int minfazhi;
	    	try {		minfazhi = evacond.getSettingCjeZbDpMinWkMax();
	    	} catch (java.lang.NullPointerException e) {minfazhi = -100000000;}
	    	
//    		minfazhi = 0 - minfazhi; //min都用负数表示
    		if(cjedpminwk <= minfazhi) { background = Color.GREEN ; return true;}
    		else return false;
    	}
    	
    	return false;
	}
	
	@Action
    public void execute(@Fact("evanode") TDXNodes evanode, 
    		@Fact("evadate") LocalDate evadate,  
    		@Fact("evaperiod") String evaperiod,
    		@Fact("evacond") BanKuaiAndGeGuMatchingConditions evacond )
    {
		iszjezbdpmatched  = true;
    }
    
    @Priority //优先级注解：return 数值越小，优先级越高
    public int getPriority(){
        return 1;
    }
    
    public Color getBackGround ()
    {
    	return this.background;
    }
    public Boolean getRuleResult ()
    {
    	return iszjezbdpmatched;
    }
    public String getAnalysisResultVoice ()
    {
    	return this.analysisresultforvoice;
    }

    // MUST IMPLEMENT THIS METHOD
//    @Override 
    public String getName() {
        return "ChenJiaoEr ZhanBi MaxWk Rule";
    }

}
