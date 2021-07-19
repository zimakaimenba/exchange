package com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules;

import java.awt.Color;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Priority;
import org.jeasy.rules.annotation.Rule;

import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.Core.Nodes.TDXNodes;
import com.exchangeinfomanager.Core.Nodexdata.NodeXPeriodData;
import com.exchangeinfomanager.Core.Nodexdata.StockNodesXPeriodData;
import com.exchangeinfomanager.bankuaifengxi.HighlightAndExportNodes.BanKuaiAndGeGuMatchingConditions;

@Rule(name = "RuleOfZongShiZhi Rule", description = "if it rains then take an umbrella" )
public class RuleOfZongShiZhi 
{
	private Color foreground = Color.BLACK, background = Color.white; 
	private Boolean specialarea = false;
	private String analysisresultforvoice = "";
	@Condition
    public boolean evaluate(@Fact("evanode") TDXNodes evanode, 
    		@Fact("evadate") LocalDate evadate,  
    		@Fact("evaperiod") String evaperiod,
    		@Fact("evacond") BanKuaiAndGeGuMatchingConditions evacond ) 
	{
		if(evanode.getType() == BkChanYeLianTreeNode.TDXBK || evanode.getType() == BkChanYeLianTreeNode.DZHBK)
			return false;
		
		Double ltszmin ;
	    Double ltszmax ;
	    try {
	    	ltszmax = evacond.getSettingZongShiZhiMax().doubleValue() ;
	    } catch (Exception e) {
	    	ltszmax = 10000000000000.0;
	    }
	    try {
	    	ltszmin = evacond.getSettingZongShiZhiMin() ;
	    } catch (Exception e) {
	    	ltszmin = 10000000000000.0;
	    }
	    LocalDate requireddate = evadate;
	    String period = evaperiod;
	    NodeXPeriodData nodexdata = evanode.getNodeXPeroidData(period);//   bk.getStockXPeriodDataForABanKuai(stockofbank.getMyOwnCode(), period);
	    Double curltsz = null;
	    try {
	    	curltsz = ((StockNodesXPeriodData)nodexdata).getSpecificTimeZongShiZhi(requireddate);
	    } catch (java.lang.ClassCastException e) {
//    		e.printStackTrace();
    		return false;
    	}
	    if(curltsz == null) //��ʱ����һ���׵����ݻ�û�е��룬����û����ͨ��ֵ���ݣ�������һ�ܵ����ݶ�һ�£��Ͼ��������̫��
	    	requireddate = requireddate.minus(1,ChronoUnit.WEEKS);
	    	curltsz = ((StockNodesXPeriodData)nodexdata).getSpecificTimeZongShiZhi(requireddate);
	    try {
		    if( curltsz >= ltszmin && curltsz <= ltszmax ) {
		    	if(curltsz >= 1500000000.0 && curltsz <= 3900000000.0) { //���������Χ�ڵĸ���Ҫ�ر����ӣ��ر���ʾ
		    		specialarea = true ;
		    		analysisresultforvoice = analysisresultforvoice + "����ֵ" + Math.floor(curltsz/100000000) + "�ڡ�";
		    		return true;
		    	}
		    	else  {
		    		analysisresultforvoice = analysisresultforvoice + "����ֵ" + Math.floor(curltsz/100000000) + "�ڡ�";
		    		return true;
		    	}
		    }
		    else {
		    	analysisresultforvoice = analysisresultforvoice + "����ֵ" + Math.floor(curltsz/100000000) + "�ڡ�";
		    	return false;
		    }
	    } catch (java.lang.NullPointerException e) {
	    	return false;
	    }
    }
    
    @Action
    public void execute(@Fact("evanode") TDXNodes evanode, 
    		@Fact("evadate") LocalDate evadate,  
    		@Fact("evaperiod") String evaperiod,
    		@Fact("evacond") BanKuaiAndGeGuMatchingConditions evacond )
    {
    	if(specialarea)
    		background = new Color(201,0,102) ;
    	else
    		background = Color.MAGENTA ;
    }
    
    @Priority //���ȼ�ע�⣺return ��ֵԽС�����ȼ�Խ��
    public int getPriority(){
        return 1;
    }
    private Boolean analysisresult = false;
	public Boolean getAnalysisResult ()
    {
    	return this.analysisresult;
    }
    public Color getBackGround ()
    {
    	return this.background;
    }
    
    public String getAnalysisResultVoice ()
    {
    	return this.analysisresultforvoice;
    }

    // MUST IMPLEMENT THIS METHOD
//    @Override 
    public String getName() {
        return "ChenJiaoEr Rule";
    }
}
