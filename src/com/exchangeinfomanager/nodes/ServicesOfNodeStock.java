package com.exchangeinfomanager.nodes;

import java.time.LocalDate;

public interface ServicesOfNodeStock
{
	public BkChanYeLianTreeNode getStockGuDong(Stock node, String gudongtype,LocalDate requiredstart, LocalDate requiredend);
	public BkChanYeLianTreeNode refreshStockGuDong(Stock stock, Boolean onlyimportwithjigougudong, Boolean forcetorefrshallgudong);
	public BkChanYeLianTreeNode checkStockHasHuangQinGuoQieAndMingXin(Stock node, LocalDate requiredstart, LocalDate requiredend);
	public BkChanYeLianTreeNode getNodeSuoShuBanKuaiList (BkChanYeLianTreeNode node);
	
}
