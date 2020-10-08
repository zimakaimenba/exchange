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

@Rule(name = "QueKou Rule", description = "if it rains then take an umbrella" )
public class RuleOfQueKou
{
	private Color foreground = Color.BLACK, background = Color.white;
	private String analysisresultforvoice = "";
	private Boolean isquekoumatched = false;
	
	@Condition
	public boolean evaluate(@Fact("evanode") TDXNodes evanode,
			@Fact("evadate") LocalDate evadate,@Fact("evadatedifference") Integer evadatedifference, 
			@Fact("evaperiod") String evaperiod,
    		@Fact("evacond") BanKuaiGeGuMatchCondition evacond ) 
	{
		
		Boolean hlqk = evacond.hasHuiBuDownQueKou();
	    if(hlqk ) {
	    	NodeXPeriodData nodexdata = evanode.getNodeXPeroidData(evaperiod);//   bk.getStockXPeriodDataForABanKuai(stockofbank.getMyOwnCode(), period);
	    	
		    Integer hbqkdown = nodexdata.getQueKouTongJiHuiBuDown(evadate, 0);
		    Integer openupqk = nodexdata.getQueKouTongJiOpenUp(evadate, 0);
		    Integer opendown = nodexdata.getQueKouTongJiOpenDown(evadate,0);
		    if( (hbqkdown != null && hbqkdown >0) ||  (openupqk != null && openupqk>0)  ) {//ȱ�� 
		    	analysisresultforvoice = analysisresultforvoice + "��������������ȱ�ڡ�";
		    	return true;
		    } 	else {
		    	if(opendown != null  && opendown >0)
		    		analysisresultforvoice = analysisresultforvoice + "��������������ȱ�ڡ�";
		    	return false;
		    }
	    } else
	    	return false;
	}
	
	@Action
    public void execute(@Fact("evanode") TDXNodes evanode, 
    		@Fact("evadate") LocalDate evadate,@Fact("evadatedifference") Integer evadatedifference, 
    		@Fact("evaperiod") String evaperiod,
    		@Fact("evacond") BanKuaiGeGuMatchCondition evacond )
    {
		background = Color.PINK ;
		isquekoumatched = true;
    }
    
    @Priority //���ȼ�ע�⣺return ��ֵԽС�����ȼ�Խ��
    public int getPriority(){
        return 1;
    }
    public Boolean getRuleResult ()
    {
    	return isquekoumatched;
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
        return "QueKou Rule";
    }


}
