package com.exchangeinfomanager.gui.subgui;

import javax.swing.JPanel;

import java.util.ArrayList;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountInfoBasic;
import com.exchangeinfomanager.AccountAndChiCang.AccountAndChiCangConfiguration;

import javax.swing.JComboBox;
import net.miginfocom.swing.MigLayout;

public class GengGaiZhangHu extends JPanel 
{
	private boolean sellbuy;
	private String stockcode;
	/**
	 * Create the panel.
	 */
	public GengGaiZhangHu(String stockcode, String currentacnt, boolean sellbuy, AccountAndChiCangConfiguration accountschicangconfig) 
	{
		initializeGui ();
		this.accountschicangconfig = accountschicangconfig;
		this.sellbuy = sellbuy;
		this.stockcode = stockcode;
		txdCurAcnt.setText(currentacnt);
		
		initializeAccount ();
	}
	
	private void initializeAccount() 
	{
		if(sellbuy) { //买入，每个账户都可以
			ArrayList<String> putongnamelist = accountschicangconfig.getPutongaccountsnamelist();
			ArrayList<String> rzrqputongnamelist = accountschicangconfig.getRzrqputongaccountsnamelist();
			ArrayList<String> rzrqrongzinamelist = accountschicangconfig.getRzrqrongziaccountsnamelist();
			ArrayList<String> rzrqrongquannamelist = accountschicangconfig.getRzrqrongqyanaccountsnamelist();
			
			for(String str:putongnamelist)
				cbxZhanghu.addItem(str);
			for(String str:rzrqputongnamelist)
				cbxZhanghu.addItem(str);
			for(String str:rzrqrongzinamelist)
				cbxZhanghu.addItem(str);
			for(String str:rzrqrongquannamelist)
				cbxZhanghu.addItem(str);
			
//			for(int i=0;i<rzrqputongnamelist.size();i++)
//				cbxZhanghu.addItem(rzrqputongnamelist.get(i));
//			
//			for(int i=0;i<rzrqrongzinamelist.size();i++)
//				cbxZhanghu.addItem(rzrqrongzinamelist.get(i));
//			
//			for(int i=0;i<rzrqrongquannamelist.size();i++)
//				cbxZhanghu.addItem(rzrqrongquannamelist.get(i));
		}
		
		if(!sellbuy){ //卖出，只能有持仓的和融券账户
			ArrayList<String> rzrqrongquannamelist = accountschicangconfig.getRzrqrongqyanaccountsnamelist();
			ArrayList<AccountInfoBasic> chichangacntlist = null;
			if(accountschicangconfig.getStockChiCangAccount(this.stockcode) != null)
			{
				chichangacntlist = accountschicangconfig.getStockChiCangAccount(this.stockcode);
			}
			
			try {
				for(AccountInfoBasic tmpacnt : chichangacntlist)
					cbxZhanghu.addItem(tmpacnt.getAccountName());
//				for (int i = 0; i < chichangacntlist.size(); i++)
//					cbxZhanghu.addItem(chichangacntlist.get(i));
			} catch (Exception e) {
				
			}
			for(String str:rzrqrongquannamelist)
				cbxZhanghu.addItem(str);
//			for(int i=0;i<rzrqrongquannamelist.size();i++)
//				cbxZhanghu.addItem(rzrqrongquannamelist.get(i));
		}
		
	}
	
	private JTextField txdCurAcnt;
	private AccountAndChiCangConfiguration accountschicangconfig;
	private JComboBox<String> cbxZhanghu;
	private void initializeGui() 
	{
		JLabel lblNewLabel = new JLabel("\u5F53\u524D\u8D26\u6237");
		
		txdCurAcnt = new JTextField();
		txdCurAcnt.setEditable(false);
		txdCurAcnt.setColumns(10);
		
		JLabel label = new JLabel("\u65B0\u8D26\u6237");
		
		cbxZhanghu = new JComboBox<String>();
		setLayout(new MigLayout("", "[48px][190px]", "[21px][23px]"));
		add(lblNewLabel, "cell 0 0,alignx left,aligny center");
		add(txdCurAcnt, "cell 1 0,growx,aligny top");
		add(label, "cell 0 1,alignx left,aligny center");
		add(cbxZhanghu, "cell 1 1,grow");

		
	}

	public String getNewAccount() 
	{
		return (String)cbxZhanghu.getSelectedItem();
	}
}
