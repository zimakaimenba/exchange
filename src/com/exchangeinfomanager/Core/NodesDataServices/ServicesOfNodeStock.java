package com.exchangeinfomanager.Core.NodesDataServices;

import java.time.LocalDate;

import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.Core.Nodes.Stock;

public interface ServicesOfNodeStock
{
	public BkChanYeLianTreeNode getStockGuDong(Stock node, String gudongtype,LocalDate requiredstart, LocalDate requiredend);
	public BkChanYeLianTreeNode refreshStockGuDong(Stock stock, Boolean onlyimportwithjigougudong, Boolean forcetorefrshallgudong);
	public BkChanYeLianTreeNode checkStockHasHuangQinGuoQieAndMingXin(Stock node, LocalDate requiredstart, LocalDate requiredend);
	public BkChanYeLianTreeNode getNodeSuoShuBanKuaiList (BkChanYeLianTreeNode node);
	
}
