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

@Rule(name = "Weekly Average ChenJiaoEr Rule", description = "if it rains then take an umbrella" )
public class RuleOfWeeklyAverageChenJiaoErMaxWk 
{
	private Color foreground = Color.BLACK, background = Color.white;
	private String analysisresultforvoice = "";
	
	@Condition
	public boolean evaluate(@Fact("evanode") TDXNodes evanode, @Fact("evadate") LocalDate evadate, @Fact("evaperiod") String evaperiod,
    		@Fact("evacond") BanKuaiGeGuMatchCondition evacond ) 
	{
		NodeXPeriodData nodexdata = evanode.getNodeXPeroidData(evaperiod);
		int dpmaxwk = nodexdata.getAverageDailyChenJiaoErMaxWeekOfSuperBanKuai(evadate,0);
    	
    	Integer cjemaxwk = evacond.getSettingChenJiaoErMaxWk();
    	if(cjemaxwk == null)
    		cjemaxwk =  10000000;
    	
    	if( dpmaxwk >=cjemaxwk ) {
    		analysisresultforvoice = analysisresultforvoice + "��ƽ���ɽ���MAXWEEK����" + dpmaxwk + "��" ;
    		return true;
    	}
    	else 
    		 return false;
	}
	
	@Action
    public void execute(@Fact("evanode") TDXNodes evanode, @Fact("evadate") LocalDate evadate, @Fact("evaperiod") String evaperiod,
    		@Fact("evacond") BanKuaiGeGuMatchCondition evacond )
    {
		background = Color.CYAN ;
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
        return "Weekly Average ChenJiaoEr Rule";
    }

}
