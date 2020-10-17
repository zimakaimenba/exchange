package com.exchangeinfomanager.ServicesOfDisplayNodeInfo;

import java.time.LocalDate;

import com.exchangeinfomanager.Services.ServicesOfNodeJiBenMianInfo;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.TDXNodes;

public class DisplayNodeExchangeDataServices implements ServicesOfNodeJiBenMianInfo
{
	private BkChanYeLianTreeNode node;
	private LocalDate date;
	private String period;

	public DisplayNodeExchangeDataServices (BkChanYeLianTreeNode node, LocalDate date, String peirod)
	{
		this.node = node;
		this.date = date;
		this.period = peirod;
	}

	@Override
	public String getRequiredHtmlInfo() 
	{
		String htmlstring = ((TDXNodes)node).getNodeXPeroidDataInHtml(this.date,this.period);
		return htmlstring;
	}
}
