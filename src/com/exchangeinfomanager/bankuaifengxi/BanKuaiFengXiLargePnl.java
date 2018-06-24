package com.exchangeinfomanager.bankuaifengxi;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.StockGivenPeriodDataItem;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiInfoTableModel;
import com.exchangeinfomanager.bankuaifengxi.CandleStick.BanKuaiFengXiCandlestickPnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiCategoryBarChartPnl;
import com.exchangeinfomanager.bankuaifengxi.CategoryBar.BanKuaiFengXiNodeCombinedCategoryPnl;
import com.exchangeinfomanager.bankuaifengxi.ai.JiaRuJiHua;
import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.database.BanKuaiDbOperation;

public  class BanKuaiFengXiLargePnl extends JPanel implements BarChartPanelHightLightColumnListener
{
	private BanKuaiFengXiNodeCombinedCategoryPnl centerPanel;
	private JTextArea tfldselectedmsg;
	private BkChanYeLianTreeNode displaynode;
	private LocalDate displaystartdate;
	private LocalDate displayenddate;
	private String displayperiod;
	private BanKuaiFengXiCandlestickPnl nodekpnl;
	private BanKuaiDbOperation bkdbopt;

	public BanKuaiFengXiLargePnl (BkChanYeLianTreeNode node, LocalDate displayedstartdate1,LocalDate displayedenddate1,String period)
	{
		this.displaynode = node;
		this.displaystartdate = displayedstartdate1;
		this.displayenddate = displayedenddate1;
		this.displayperiod = period;
		this.bkdbopt = new BanKuaiDbOperation ();
		createGui ();
		createEvents ();
		
		updateData (node,displayedstartdate1,displayedenddate1,period);
	}
	
	private void createEvents() 
	{
		centerPanel.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(BanKuaiFengXiCategoryBarChartPnl.SELECTED_PROPERTY)) {
                    @SuppressWarnings("unchecked")
                    String selectedinfo = evt.getNewValue().toString();
                    
					LocalDate datekey = LocalDate.parse(selectedinfo.substring(0, 10));
    				
    				String tooltip = selectedinfo.substring(10,selectedinfo.length());
    				ArrayList<JiaRuJiHua> fxjg = getZdgzFx (displaynode,datekey,displayperiod);
    				setUserSelectedColumnMessage(tooltip,fxjg);
    				
    				nodekpnl.highLightSpecificBarColumn(datekey);
                }
            }
        });
		
	}
    /*
     * 
     */
    private  ArrayList<JiaRuJiHua> getZdgzFx( BkChanYeLianTreeNode curdisplayednode, LocalDate localDate,String period) 
    {
		ArrayList<JiaRuJiHua> fxresult = bkdbopt.getZdgzFxjgForANodeOfGivenPeriod (curdisplayednode.getMyOwnCode(),localDate,period);
    	return fxresult;
	}
	/*
	 * 显示用户点击bar column后应该提示的信息
	 */
	private void setUserSelectedColumnMessage(String selttooltips,ArrayList<JiaRuJiHua> fxjg) 
	{
		String allstring = selttooltips + "\n";
		if(fxjg !=null) {
			for(JiaRuJiHua jrjh : fxjg) {
				LocalDate actiondate = jrjh.getJiaRuDate();
				String actiontype = jrjh.getGuanZhuType();
				String shuoming = jrjh.getJiHuaShuoMing();
				
				allstring = allstring +  "[" + actiondate.toString() + actiontype +  " " + shuoming + "]" + "\n";
			}
		}
		
		tfldselectedmsg.setText( allstring + tfldselectedmsg.getText() + "\n");
		 tfldselectedmsg.setCaretPosition(0);
	}

	private void updateData(BkChanYeLianTreeNode node, LocalDate displayedstartdate1, LocalDate displayedenddate1,
			String period) {
		centerPanel.updatedDate(node, displayedstartdate1,displayedenddate1, period);
		nodekpnl.updatedDate(node, displayedstartdate1,displayedenddate1,  StockGivenPeriodDataItem.DAY);
	}

	private void createGui() 
	{
		this.setLayout(new BorderLayout());

		this.nodekpnl = new BanKuaiFengXiCandlestickPnl ();
		this.centerPanel = new BanKuaiFengXiNodeCombinedCategoryPnl ("vertical");
		

		tfldselectedmsg = new JTextArea();
		tfldselectedmsg.setLineWrap(true);
		JScrollPane scrollPaneuserselctmsg = new JScrollPane (); 
		scrollPaneuserselctmsg.setViewportView(tfldselectedmsg);

		this.add(this.nodekpnl,BorderLayout.NORTH);
		this.add(scrollPaneuserselctmsg,BorderLayout.EAST);
		this.add(centerPanel,BorderLayout.CENTER);
	}

	@Override
	public void highLightSpecificBarColumn(LocalDate selecteddate) 
	{
		centerPanel.highLightSpecificBarColumn(selecteddate);
		nodekpnl.highLightSpecificBarColumn(selecteddate);
	}

	@Override
	public void highLightSpecificBarColumn(Integer columnindex) {
		// TODO Auto-generated method stub
		
	}

}
