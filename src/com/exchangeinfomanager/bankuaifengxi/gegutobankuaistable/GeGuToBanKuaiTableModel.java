package com.exchangeinfomanager.bankuaifengxi.gegutobankuaistable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import com.exchangeinfomanager.Core.Nodes.BanKuai;
import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.Core.Nodes.DaPan;
import com.exchangeinfomanager.Core.Nodes.Stock;
import com.exchangeinfomanager.Core.Nodes.StockOfBanKuai;
import com.exchangeinfomanager.Core.Nodexdata.NodeXPeriodData;
import com.exchangeinfomanager.Core.Trees.CreateExchangeTree;
import com.exchangeinfomanager.bankuaifengxi.BankuaiAndGeguTableBasic.BandKuaiAndGeGuTableBasicModel;
import com.exchangeinfomanager.bankuaifengxi.BankuaiAndGeguTableBasic.SortByKeyWords.NodeChenJiaoErComparator;

public class GeGuToBanKuaiTableModel  extends BandKuaiAndGeGuTableBasicModel 
{

	public GeGuToBanKuaiTableModel(Properties prop) 
	{
		super (prop);
	}
	
	public void refresh  (Stock stock,LocalDate wknum,String period)
	{
		super.curnode = stock;
		super.showwknum = wknum;
		super.curperiod = period;

		entryList = null;
		entryList = new ArrayList<>( );
		Set<BkChanYeLianTreeNode> curbklist = stock.getGeGuCurSuoShuTDXSysBanKuaiList();
		for(BkChanYeLianTreeNode tmpbk : curbklist) {
			if( ((BanKuai)tmpbk).getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL) 
					||  ((BanKuai)tmpbk).getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL)  ) //有些指数是没有个股和成交量的，不列入比较范围
				continue;
			
			if( !((BanKuai)tmpbk).getBanKuaiOperationSetting().isShowinbkfxgui() )
				continue;
			
			entryList.add(tmpbk);
		}
		
		Set<BkChanYeLianTreeNode> curdzhbklist = stock.getGeGuCurSuoShuDZHSysBanKuaiList();
		for(BkChanYeLianTreeNode tmpbk : curdzhbklist) {
			if( ((BanKuai)tmpbk).getBanKuaiLeiXing().equals(BanKuai.HASGGNOSELFCJL) 
					||  ((BanKuai)tmpbk).getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL)  ) //有些指数是没有个股和成交量的，不列入比较范围
				continue;
			
			if( !((BanKuai)tmpbk).getBanKuaiOperationSetting().isShowinbkfxgui() )
				continue;
			
			entryList.add(tmpbk);
		}
		
    	this.fireTableDataChanged();
	}
	public Object getValueAt(int rowIndex, int columnIndex) 
	{
			Object value = super.getValueAt(rowIndex, columnIndex);
    	
	    	return value;
	}
	
	public Class<?> getColumnClass(int columnIndex) 
	{ 
			Class clazz = super.getColumnClass(columnIndex);
			return clazz;
	}
}
