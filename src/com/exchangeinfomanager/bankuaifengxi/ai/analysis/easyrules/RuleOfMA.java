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

import com.exchangeinfomanager.bankuaifengxi.HighlightAndExportNodes.BanKuaiAndGeGuMatchingConditions;
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
			@Fact("evadate") LocalDate evadate,  
			@Fact("evaperiod") String evaperiod,
    		@Fact("evacond") BanKuaiAndGeGuMatchingConditions evacond ) 
	{
		if( !BkChanYeLianTreeNode.isBanKuai(evanode) && evanode.getType() != BkChanYeLianTreeNode.TDXGG )
			return false;
		
		String displayma = evacond.getSettingMaFormula();
		if (Strings.isNullOrEmpty(displayma)) return false;
	
		NodeXPeriodData nodexdataday = evanode.getNodeXPeroidData(NodeGivenPeriodDataItem.DAY);
	    OHLCItem ohlcdata =  ((TDXNodesXPeriodDataForJFC)nodexdataday).getSpecificDateOHLCData (evadate);
		LocalDate expectdate = evadate;
		if(ohlcdata == null) { //�����п��ܵ�����ͣ�Ƶģ����ͣ�ƣ����ҵ����������ݵ�������
			String nodeperiod = evaperiod;
			if( nodeperiod.equals(NodeGivenPeriodDataItem.DAY ) ) {
				DayOfWeek dayOfWeek = evadate.getDayOfWeek();
				int dayOfWeekIntValue = dayOfWeek.getValue();
				for(int i = 0;i < dayOfWeekIntValue;i++) { 
					expectdate = evadate.plus(0-i,ChronoUnit.DAYS);
					OHLCItem ohlcdataexpectdate =  ((TDXNodesXPeriodDataForJFC)nodexdataday).getSpecificDateOHLCData (expectdate);
					if(ohlcdataexpectdate != null) {
//						expectdate = adjustDate(expectdate);
						evadate = expectdate;
			    		break;
					}
			    }
			} else if( nodeperiod.equals(NodeGivenPeriodDataItem.WEEK ) ) { //һ��û�����ݣ�˵��ͣ�ƣ�Ŀǰ����MA����ô�ã�ֱ�ӻ�F
				return false;
			}
		}
		
		Double[] dailyma = nodexdataday.getNodeOhlcMA (evadate);
		if (dailyma != null) {
			//����K�ߵ�CLOSE
			TDXNodesXPeriodDataForJFC nodexdata = (TDXNodesXPeriodDataForJFC)evanode.getNodeXPeroidData(evaperiod);//   bk.getStockXPeriodDataForABanKuai(stockofbank.getMyOwnCode(), period);
			ohlcdata = ((TDXNodesXPeriodDataForJFC)nodexdata).getSpecificDateOHLCData (evadate);
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
		} else
			return true; //û�о������ݣ�������ϵͳû�е��룬���ԾͲ����κ��жϣ�Ĭ��Ϊ�С�
		
	    Boolean checkresult = nodexdataday.checkCloseComparingToMAFormula(evadate, displayma);
	    if( checkresult != null && checkresult)	    	return true;
	    
    	return false;
	}
	
	@Action
    public void execute(@Fact("evanode") TDXNodes evanode,
    		@Fact("evadate") LocalDate evadate,  
    		@Fact("evaperiod") String evaperiod,
    		@Fact("evacond") BanKuaiAndGeGuMatchingConditions evacond )
    {
		analysisresult = true;
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
        return "RULE MA";
    }
    
    protected LocalDate adjustDate(LocalDate dateneedtobeadjusted)
	 {
		 LocalDate friday;
		 DayOfWeek dayofweek = dateneedtobeadjusted.getDayOfWeek();
		 if( dayofweek.equals(DayOfWeek.SUNDAY)  ) {
			friday = dateneedtobeadjusted.minus(2,ChronoUnit.DAYS);
			return friday;
		 } else if (  dayofweek.equals(DayOfWeek.SATURDAY)  ) {
			friday = dateneedtobeadjusted.minus(1,ChronoUnit.DAYS);
			return friday;
		 }
					
		return dateneedtobeadjusted;
	 }
}
