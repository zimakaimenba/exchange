package com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules;

import java.awt.Color;
import java.time.LocalDate;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Priority;
import org.jeasy.rules.annotation.Rule;

import com.exchangeinfomanager.bankuaifengxi.BanKuaiGeGuMatchCondition;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;

@Rule(name = "ChenJiaoEr ZhanBi MaxWk Rule", description = "if it rains then take an umbrella" )
public class RuleOfCjeZbDpMaxWk 
{
	private Color foreground = Color.BLACK, background = Color.white;
	boolean lianxufangliang = false;
	String analysisresultforvoice = "";
	@Condition
	public boolean evaluate(@Fact("evanode") TDXNodes evanode,
			@Fact("evadate") LocalDate evadate, @Fact("evadatedifference") Integer evadatedfference, 
			@Fact("evaperiod") String evaperiod,
    		@Fact("evacond") BanKuaiGeGuMatchCondition evacond ) 
	{
		NodeXPeriodData nodexdata = evanode.getNodeXPeroidData(evaperiod);
		Integer cjedpmaxwk = nodexdata.getChenJiaoErZhanBiMaxWeekOfSuperBanKuai (evadate,evadatedfference);
		if(cjedpmaxwk!= null && cjedpmaxwk > 0 ) {
    		int maxfazhi;
	    	try {
	    		maxfazhi = evacond.getSettingDpMaxWk();
	    	} catch (java.lang.NullPointerException e) {
	    		maxfazhi = 100000000;
	    	}
	    	
	    	Integer lianxuflnum = nodexdata.getCjeDpMaxLianXuFangLiangPeriodNumber (evadate,evadatedfference, maxfazhi);
	    	 
	    	if(cjedpmaxwk >= maxfazhi &&  lianxuflnum >=2 ) {//��������,��ɫ��ʾ
	    		background = new Color(102,0,0) ;
	    		analysisresultforvoice = analysisresultforvoice + "�ɽ���ռ��MAXWEEK����" + cjedpmaxwk + "�ܡ�" + "�ɽ���ռ���������" + lianxuflnum + "�ܡ�"; 
	    		return true;
	    	}
	    	else if( cjedpmaxwk >= maxfazhi &&  lianxuflnum <2 ) {
	    		background = new Color(255,0,0) ;
	    		analysisresultforvoice = analysisresultforvoice + "�ɽ���ռ��MAXWEEK����" + cjedpmaxwk + "�ܡ�";
	    		return true;
	    	}
	    	else 
	    		 return false;
    	} 
    	
    	if(cjedpmaxwk == null || cjedpmaxwk <= 0 ) {
    		Integer cjedpminwk = 0- nodexdata.getChenJiaoErZhanBiMinWeekOfSuperBanKuai(evadate,evadatedfference);
    		int minfazhi;
	    	try {
	    		minfazhi = evacond.getSettingDpMinWk();
	    	} catch (java.lang.NullPointerException e) {
	    		minfazhi = 100000000;
	    	}
	    	
    		minfazhi = 0 - minfazhi; //min���ø�����ʾ
    		if(cjedpminwk <= minfazhi) {
    			background = Color.GREEN ;
    			return true;
    		}
    		else 
    			return true;
    	}
    	
    	return false;
	}
	
	@Action
    public void execute(@Fact("evanode") TDXNodes evanode, @Fact("evadate") LocalDate evadate, @Fact("evaperiod") String evaperiod,
    		@Fact("evacond") BanKuaiGeGuMatchCondition evacond )
    {
		
    }
    
    @Priority //���ȼ�ע�⣺return ��ֵԽС�����ȼ�Խ��
    public int getPriority(){
        return 1;
    }
    
    public Color getBackGround ()
    {
    	return this.background;
    }
    
    public String getAnalysisResult ()
    {
    	return this.analysisresultforvoice;
    }

    // MUST IMPLEMENT THIS METHOD
//    @Override 
    public String getName() {
        return "ChenJiaoEr ZhanBi MaxWk Rule";
    }

}
