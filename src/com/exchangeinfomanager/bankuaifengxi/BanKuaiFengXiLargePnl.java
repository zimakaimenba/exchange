package com.exchangeinfomanager.bankuaifengxi;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;


import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.StockGivenPeriodDataItem;

import com.exchangeinfomanager.bankuaifengxi.CandleStick.BanKuaiFengXiCandlestickPnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartCjeZhanbiPnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartPnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiNodeCombinedCategoryPnl;

/*
 * 可以显示某个node大周期的占比数据
 */
public  class BanKuaiFengXiLargePnl extends JPanel implements BarChartPanelHightLightColumnListener
{
//	private BkChanYeLianTreeNode displaynode;
//	private LocalDate displaystartdate;
//	private LocalDate displayenddate;
//	private String displayperiod;
//	private BanKuaiDbOperation bkdbopt;

	public BanKuaiFengXiLargePnl (BkChanYeLianTreeNode nodebkbelonged, BkChanYeLianTreeNode node, LocalDate displayedstartdate1,LocalDate displayedenddate1,String period)
	{
//		this.displaynode = node;
//		this.displaystartdate = displayedstartdate1;
//		this.displayenddate = displayedenddate1;
//		this.displayperiod = period;
//		this.bkdbopt = new BanKuaiDbOperation ();
		createGui ();
		createEvents ();
		
		updateData (nodebkbelonged, node,displayedstartdate1,displayedenddate1,period);
	}
	
	private void createEvents() 
	{
		this.nodebkcjezblargepnl.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.SELECTED_PROPERTY)) {
                    @SuppressWarnings("unchecked")
                    String selectedinfo = evt.getNewValue().toString();
                    
					LocalDate datekey = LocalDate.parse(selectedinfo.substring(0, 10));
    				
    				String tooltip = selectedinfo.substring(10,selectedinfo.length());
    				setUserSelectedColumnMessage(tooltip);
    				
    				nodecombinedpnl.highLightSpecificBarColumn(datekey);
    				
    				nodekpnl.highLightSpecificBarColumn(datekey);
                }
            }
        });
		
		nodecombinedpnl.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.SELECTED_PROPERTY)) {
                    @SuppressWarnings("unchecked")
                    String selectedinfo = evt.getNewValue().toString();
                    
					LocalDate datekey = LocalDate.parse(selectedinfo.substring(0, 10));
    				
    				String tooltip = selectedinfo.substring(10,selectedinfo.length());
    				setUserSelectedColumnMessage(tooltip);
    				
    				nodebkcjezblargepnl.highLightSpecificBarColumn(datekey);
    				nodekpnl.highLightSpecificBarColumn(datekey);
                }
            }
        });
		
	}
	/*
	 * 显示用户点击bar column后应该提示的信息
	 */
	private void setUserSelectedColumnMessage(String selttooltips) 
	{
		String allstring = selttooltips + "\n";
		tfldselectedmsg.setText( allstring + tfldselectedmsg.getText() + "\n");
		tfldselectedmsg.setCaretPosition(0);
	}

	private void updateData(BkChanYeLianTreeNode nodebkbelogned, BkChanYeLianTreeNode node, LocalDate displayedstartdate1, LocalDate displayedenddate1,
			String period) 
	{
//		centerPanel.updatedDate(node, displayedstartdate1,displayedenddate1, period);
		if(nodebkbelogned != null)
			this.nodebkcjezblargepnl.updatedDate(nodebkbelogned, displayedstartdate1, displayedenddate1, period);
		
		this.nodecombinedpnl.updatedDate(node, displayedstartdate1,displayedenddate1, period);
		this.nodekpnl.updatedDate(node, displayedstartdate1,displayedenddate1,  StockGivenPeriodDataItem.DAY);
	}
	
	@Override
	public void highLightSpecificBarColumn(LocalDate selecteddate) 
	{
//		centerPanel.highLightSpecificBarColumn(selecteddate);
		this.nodebkcjezblargepnl.highLightSpecificBarColumn(selecteddate);
		this.nodecombinedpnl.highLightSpecificBarColumn(selecteddate);
		this.nodekpnl.highLightSpecificBarColumn(selecteddate);
	}

	@Override
	public void highLightSpecificBarColumn(Integer columnindex) {
		// TODO Auto-generated method stub
		
	}


//	private BanKuaiFengXiNodeCombinedCategoryPnl centerPanel;
	private JPanel centerPanel;
	private BanKuaiFengXiNodeCombinedCategoryPnl nodecombinedpnl;
	private  BanKuaiFengXiCategoryBarChartPnl nodebkcjezblargepnl;
	private JTextArea tfldselectedmsg;
	private BanKuaiFengXiCandlestickPnl nodekpnl;
	private void createGui() 
	{
		this.setLayout(new BorderLayout());

		this.nodekpnl = new BanKuaiFengXiCandlestickPnl ();
//		this.centerPanel = new BanKuaiFengXiNodeCombinedCategoryPnl ("vertical");
		this.centerPanel = new JPanel ();
		this.centerPanel.setLayout(new BoxLayout(this.centerPanel, BoxLayout.Y_AXIS));
		this.centerPanel.setPreferredSize(new Dimension(1640, 705)); //设置显示框的大小
		
		this.nodecombinedpnl = new BanKuaiFengXiNodeCombinedCategoryPnl ("vertical");
		this.nodebkcjezblargepnl = new BanKuaiFengXiCategoryBarChartCjeZhanbiPnl ();
		this.nodebkcjezblargepnl.setBarDisplayedColor(Color.RED.brighter());
		
		this.centerPanel.add(this.nodecombinedpnl);
		this.centerPanel.add(this.nodebkcjezblargepnl);
		

		tfldselectedmsg = new JTextArea();
		tfldselectedmsg.setLineWrap(true);
		JScrollPane scrollPaneuserselctmsg = new JScrollPane (); 
		scrollPaneuserselctmsg.setViewportView(tfldselectedmsg);

		this.add(this.nodekpnl,BorderLayout.NORTH);
		this.add(scrollPaneuserselctmsg,BorderLayout.EAST);
		this.add(centerPanel,BorderLayout.CENTER);
	}

	
}
