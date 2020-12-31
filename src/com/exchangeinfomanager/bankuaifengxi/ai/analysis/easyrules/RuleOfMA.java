package com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules;

import java.awt.Color;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Priority;
import org.jeasy.rules.annotation.Rule;
import org.jfree.data.time.ohlc.OHLCItem;

import com.exchangeinfomanager.bankuaifengxi.BanKuaiGeGuMatchCondition;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.NodeXPeriodData;
import com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForJFC.StockXPeriodDataForJFC;
import com.exchangeinfomanager.nodes.stocknodexdata.NodexdataForJFC.TDXNodesXPeriodDataForJFC;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;
import com.google.common.base.Strings;

@Rule(name = "Moving Average Rule", description = "if it rains then take an umbrella" )
public class RuleOfMA 
{
	private Color foreground = Color.BLACK, background = Color.white;
	private String analysisresultforvoice = "";
	
	@Condition
	public boolean evaluate(@Fact("evanode") TDXNodes evanode, 
			@Fact("evadate") LocalDate evadate, @Fact("evadatedifference") Integer evadatedifference, 
			@Fact("evaperiod") String evaperiod,
    		@Fact("evacond") BanKuaiGeGuMatchCondition evacond ) 
	{
		if(evanode.getType() == BkChanYeLianTreeNode.TDXBK)
			return false;
		
		NodeXPeriodData nodexdataday = evanode.getNodeXPeroidData(NodeGivenPeriodDataItem.DAY);
		DayOfWeek dayOfWeek = evadate.getDayOfWeek();
	    int dayOfWeekIntValue = dayOfWeek.getValue();
	    Double[] dailyma = null;
	    for(int i = 0;i < dayOfWeekIntValue;i++) { //�����п��ܵ�����ͣ�Ƶģ����ͣ�ƣ����ҵ����������ݵ�������
	    	dailyma = nodexdataday.getNodeOhlcMA (evadate,0-i);
	    	if(dailyma != null)
	    		break;
	    }
		
		if (dailyma != null) {
//			OHLCItem ohlcdata = ((TDXNodesXPeriodDataForJFC)nodexdataday).getSpecificDateOHLCData (evadate,0);
//			Double close = ohlcdata.getCloseValue();
			//����K�ߵ�CLOSE
			StockXPeriodDataForJFC nodexdata = (StockXPeriodDataForJFC)evanode.getNodeXPeroidData(evaperiod);//   bk.getStockXPeriodDataForABanKuai(stockofbank.getMyOwnCode(), period);
			OHLCItem ohlcdata = ((TDXNodesXPeriodDataForJFC)nodexdata).getSpecificDateOHLCData (evadate,0);
		    Double close = ohlcdata.getCloseValue();

		    analysisresultforvoice = analysisresultforvoice + "�����̼�" + close;
			if(dailyma[4] != null && close >= dailyma[4])
				analysisresultforvoice = analysisresultforvoice + "�ɼ۴���60���ߡ�";
			else
				analysisresultforvoice = analysisresultforvoice + "�ɼ�С��60���ߡ�";
			if(dailyma[6] != null  && close >= dailyma[6])
				analysisresultforvoice = analysisresultforvoice + "�ɼ۴���MA250��";
			else
				analysisresultforvoice = analysisresultforvoice + "�ɼ�С��MA250��";
		}
		
		String displayma = evacond.getSettingMaFormula();
    	if (!Strings.isNullOrEmpty(displayma)) {
		    Boolean checkresult = nodexdataday.checkCloseComparingToMAFormula(displayma,evadate,0);
		    if( checkresult != null && checkresult) {
		    	return true;
		    }
    	}
    	
    	return false;
	}
	
	@Action
    public void execute(@Fact("evanode") TDXNodes evanode,
    		@Fact("evadate") LocalDate evadate, @Fact("evadatedifference") Integer evadatedifference, 
    		@Fact("evaperiod") String evaperiod,
    		@Fact("evacond") BanKuaiGeGuMatchCondition evacond )
    {
		background = new Color(0,153,153) ;
    }
    
    @Priority //���ȼ�ע�⣺return ��ֵԽС�����ȼ�Խ��
    public int getPriority()
    {
        return 1;
    }
    
    public Color getBackGround ()
    {
    	return this.background;
    }
    private Boolean analysisresult = false;
	public Boolean getAnalysisResult ()
    {
    	return this.analysisresult;
    }
    
    public String getAnalysisResultVoice ()
    {
    	return this.analysisresultforvoice;
    }

    // MUST IMPLEMENT THIS METHOD
//    @Override 
    public String getName() {
        return "Weekly Average ChenJiaoEr Rule";
    }
}
