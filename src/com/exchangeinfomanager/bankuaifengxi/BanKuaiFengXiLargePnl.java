package com.exchangeinfomanager.bankuaifengxi;

import java.awt.BorderLayout;
import java.time.LocalDate;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.StockGivenPeriodDataItem;
import com.exchangeinfomanager.bankuaifengxi.CandleStick.BanKuaiFengXiCandlestickPnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiNodeCombinedCategoryPnl;

public class BanKuaiFengXiLargePnl extends JPanel implements BarChartPanelHightLightColumnListener
{
	private BanKuaiFengXiNodeCombinedCategoryPnl centerPanel;
	private JTextArea tfldselectedmsg;
	private BkChanYeLianTreeNode displaynode;
	private LocalDate displaystartdate;
	private LocalDate displayenddate;
	private String displayperiod;
	private BanKuaiFengXiCandlestickPnl nodekpnl;

	public BanKuaiFengXiLargePnl (BkChanYeLianTreeNode node, LocalDate displayedstartdate1,LocalDate displayedenddate1,String period)
	{
		this.displaynode = node;
		this.displaystartdate = displayedstartdate1;
		this.displayenddate = displayedenddate1;
		this.displayperiod = period;
		createGui ();
		
		updateData (node,displayedstartdate1,displayedenddate1,period);
	}
	
	private void updateData(BkChanYeLianTreeNode node, LocalDate displayedstartdate1, LocalDate displayedenddate1,
			String period) {
		centerPanel.updateData(node, displayedstartdate1,displayedenddate1,  period);
		nodekpnl.updatedDate(node, displayedstartdate1,displayedenddate1,  StockGivenPeriodDataItem.DAY);
	}

	private void createGui() 
	{
		this.setLayout(new BorderLayout());

		this.nodekpnl = new BanKuaiFengXiCandlestickPnl ();
		this.centerPanel = new BanKuaiFengXiNodeCombinedCategoryPnl ();
		

		tfldselectedmsg = new JTextArea();
		tfldselectedmsg.setLineWrap(true);
		JScrollPane scrollPaneuserselctmsg = new JScrollPane (); 
		scrollPaneuserselctmsg.setViewportView(tfldselectedmsg);

		this.add(this.nodekpnl,BorderLayout.NORTH);
		this.add(scrollPaneuserselctmsg,BorderLayout.EAST);
		this.add(centerPanel,BorderLayout.CENTER);
	}

	@Override
	public void highLightSpecificBarColumn(Comparable<String> selecteddate) 
	{
		centerPanel.highLightSpecificBarColumn(selecteddate);
		nodekpnl.highLightSpecificBarColumn(selecteddate);
	}

	@Override
	public void highLightSpecificBarColumn(Integer columnindex) {
		// TODO Auto-generated method stub
		
	}


}
