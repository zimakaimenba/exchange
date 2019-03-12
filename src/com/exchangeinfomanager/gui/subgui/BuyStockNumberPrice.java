package com.exchangeinfomanager.gui.subgui;

import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

import java.util.ArrayList;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JComboBox;

import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountInfoBasic;
import com.exchangeinfomanager.AccountAndChiCang.AccountAndChiCangConfiguration;



import javax.swing.JTextField;
import javax.swing.JCheckBox;
import com.toedter.calendar.JDateChooser;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import java.awt.event.ItemListener;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.awt.Color;
import java.awt.event.ItemEvent;

public class BuyStockNumberPrice extends JPanel 
{

	private static final long serialVersionUID = 1L;


	public BuyStockNumberPrice(String stockcode,AccountAndChiCangConfiguration accountschicangconfig, boolean sellbuy) 
	{
		initializeGui ();
		
		this.stockcode = stockcode;
		tfldStockCode.setText(stockcode);
		this.sellbuy = sellbuy;
		
		if(accountschicangconfig !=null ) {
			this.accountschicangconfig = accountschicangconfig;
			initializeAccount ();
		} else //=null，说明是以前成交过的记录，是从数据库中读出的以前成交的，是为了恢复交易记录 要把挂单设置为false
			chckbxNewCheckBox.setSelected(false);
	}
	
	private AccountAndChiCangConfiguration accountschicangconfig;
	private String stockcode;
	private boolean sellbuy;
	private int profitdabaseid;
	private String actionzhanghu;
	private Double profit = null;
	private String actiontype;
	private int autoIncKeyFromApi;
	private boolean needactionprecheck; //导入时候无需做资金检查，单独每笔记录需要做资金合法性检查
	
	public void setFormatedShuoMing (int zonggushu)
	{
		String shuomingresult;
		double tmpchengben = this.getJiaoyiGushu()*this.getJiaoyiJiage();
		NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
		if(this.isBuySell()) {
			shuomingresult = "'" + "(" + this.getJiaoyiGushu() + " * " + currencyFormat.format(this.getJiaoyiJiage()) + "=" + (0-tmpchengben)     + ")" + "|" + "共"+ zonggushu + "股"+ "|" + this.getJiaoYiShuoMing()+ "'" ;
		} else {
			shuomingresult = "'" + "(" + (0-this.getJiaoyiGushu()) + " * " + currencyFormat.format(this.getJiaoyiJiage()) + "=" + tmpchengben + ")" +  "|"  + "共"+ zonggushu + "股"+ "|" +this.getJiaoYiShuoMing() + "'" ;
		}
		
		this.setJiaoYiShuoMing(shuomingresult);
	}
	
	public void setStockcode (String stockcode)
	{
		this.stockcode = stockcode;
		tfldStockCode.setText(stockcode);
		if(tfldStockCode.getText().isEmpty())
			lbljiaoyidaima.setForeground(Color.red);
	}
	public boolean isBuySell ()
	{
		return sellbuy;
	}
	public void setBuySell (boolean sellbuy)
	{
		this.sellbuy = sellbuy;
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
		}
		
		if(!sellbuy){ //卖出，只能有持仓的和融券账户
			ArrayList<String> rzrqrongquannamelist = accountschicangconfig.getRzrqrongqyanaccountsnamelist();
			ArrayList<String> chichangacntlist = null;
			if(accountschicangconfig.isSystemChiCang(this.stockcode) )
			{
				 ArrayList<AccountInfoBasic> stockchicangacnt = accountschicangconfig.getStockChiCangAccount(this.stockcode);
				 for(AccountInfoBasic tmpacnt : stockchicangacnt)
					 cbxZhanghu .addItem( tmpacnt.getAccountName() );
			}
			
//			try {
//				for(String str:chichangacntlist)
//					cbxZhanghu.addItem(str);
//
//			} catch (Exception e) {
//				
//			}
			for(String str:rzrqrongquannamelist)
				cbxZhanghu.addItem(str);

		}
		
	}

	public void setJiaoYiShuoMing(String shuoming)
	{
		tfdShuoMing.setText(shuoming);
		tfdShuoMing.setCaretPosition(0);
	}
	
	public void resetInput ()
	{
		tfldJiage.setText("");
		tfldGushu.setText("");
		chckbxNewCheckBox.setSelected(true);
		dChsActionDay.setDate(new Date());
		tfdShuoMing.setText("");
	}

	public String getJiaoyiZhanghu ()
	{
		if(actionzhanghu != null)
			return actionzhanghu;
		else 
			return (String)cbxZhanghu.getSelectedItem();
		
	}
	public void setJiaoyiZhanghu (String zhanghu)
	{
		this.actionzhanghu = zhanghu;
		cbxZhanghu.addItem(zhanghu);
	}
	public void setJiaoYiGuShu (int gushu)
	{
		tfldGushu.setText(String.valueOf(gushu));
	}
	public int getJiaoyiGushu()
	{
		int jygs ;
		try {
			jygs = Integer.parseInt(tfldGushu.getText());
		} catch (java.lang.NumberFormatException e) {
			jygs = -1;
		}
		
		
		return jygs;
	}
	public double getJiaoyiJiage ()
	{
		double jyjg;
		try {
			jyjg = Double.parseDouble(tfldJiage.getText());
		} catch (java.lang.NumberFormatException e) {
			jyjg = -1;
		}
		
		return jyjg;
	}
	public void setJiaoYiJiaGe (double jiage)
	{
		tfldJiage.setText(String.valueOf(jiage));
		tfldJiage.setCaretPosition(0);
	}
	public boolean getGuadan ()
	{
		return chckbxNewCheckBox.isSelected();
	}
	public void setGuadan (boolean isguadan)
	{
		chckbxNewCheckBox.setSelected(isguadan);
	}
	
	public void setDatabaseid(int autoIncKeyFromApi) 
	{
		this.autoIncKeyFromApi = autoIncKeyFromApi;
	}
	public int getDatabaseid() 
	{
		return this.autoIncKeyFromApi;
	}
	


	public String getActionType() 
	{
		if(this.getJiaoyiJiage() == 0.0) {
			this.actiontype = "送转股";
			return this.actiontype;
		}
		
		boolean guadan = this.getGuadan();
		String guadanaction;
		 if (guadan) 
			 guadanaction = "挂单";
		else guadanaction = " ";
		
		String actionstockaccount = this.getJiaoyiZhanghu();
		String buysell;
		if(this.sellbuy)
			buysell= "买入";
		else buysell= "卖出";

		if(actionstockaccount.contains("融券")) {
			this.setActiontype("融券" + buysell + guadanaction );
		} else if(actionstockaccount.contains("融资")) {
			this.setActiontype("融资" + buysell + guadanaction );
		} else if(actionstockaccount.contains("信用普通")) {
			this.setActiontype("信用普通" + buysell + guadanaction );
		} else {
			this.setActiontype(buysell + guadanaction );
		}
		return this.actiontype;
	}
	
	private void setActiontype(String actiontype) {
		this.actiontype = actiontype;
	}
	
	public Double getProfit ()
	{
		return profit;
	}
	public void setProfit (double profit2)
	{
		this.profit = profit2;
	}
	public String getJiaoYiShuoMing()
	{
		return tfdShuoMing.getText().trim();
	}
	public LocalDateTime getActionDay  ()
	{
		return this.dChsActionDay.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}
	public void setActionDay (LocalDateTime datetime) 
	{
		this.dChsActionDay.setDate(Date.from(datetime.atZone(ZoneId.systemDefault()).toInstant()));
	}
	public void setActionDay (Date date) 
	{
		this.dChsActionDay.setDate(date);
	}
	public int getProfitDatabaseid ()
	{
		return profitdabaseid;
	}
	
	public String getStockcode ()
	{
		return this.tfldStockCode.getText();
	}
	public void createEvents()
	{
		cbxZhanghu.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) 
			{
				actionzhanghu = (String)cbxZhanghu.getSelectedItem(); 
			}
		});
	}
	
	private JLabel lblNewLabel_3;
	private JTextField tfdShuoMing;
	private JLabel lblNewLabel;
	private JLabel lblNewLabel_1;
	private JLabel lblNewLabel_2;
	private JDateChooser dChsActionDay;
	private JTextField tfldStockCode;
	private JTextField tfldGushu;
	private JComboBox<String> cbxZhanghu;
	private JTextField tfldJiage;
	private JCheckBox chckbxNewCheckBox;
	private JLabel lbljiaoyidaima;
	private void initializeGui() 
	{
		lblNewLabel = new JLabel("\u4EA4\u6613\u8D26\u6237");
		
		cbxZhanghu = new JComboBox<String>();

		
		lblNewLabel_1 = new JLabel("\u4EA4\u6613\u4EF7\u683C");
		
		tfldJiage = new JTextField();
		tfldJiage.setColumns(10);
		
		lblNewLabel_2 = new JLabel("\u4EA4\u6613\u80A1\u6570");
		
		tfldGushu = new JTextField();
		tfldGushu.setColumns(10);
		
		chckbxNewCheckBox = new JCheckBox("\u6302\u5355");
		chckbxNewCheckBox.setSelected(true);
		
		lblNewLabel_3 = new JLabel("\u4EA4\u6613\u8BF4\u660E");
		
		tfdShuoMing = new JTextField();
		tfdShuoMing.setColumns(10);
		
		dChsActionDay = new JDateChooser();
		dChsActionDay.setDateFormatString("yyyy-MM-dd HH:mm:ss");
		dChsActionDay.setDate(new Date());
		
		tfldStockCode = new JTextField();
		tfldStockCode.setColumns(10);
		
		lbljiaoyidaima = new JLabel("\u80A1\u7968\u4EE3\u7801");
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblNewLabel_1)
							.addGap(1))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblNewLabel_3)
							.addGap(1))
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addComponent(lbljiaoyidaima)
								.addComponent(lblNewLabel))
							.addGap(1))
						.addComponent(chckbxNewCheckBox))
					.addGap(1)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(3)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(tfldJiage, GroupLayout.PREFERRED_SIZE, 82, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(lblNewLabel_2)
									.addGap(18)
									.addComponent(tfldGushu, GroupLayout.PREFERRED_SIZE, 119, GroupLayout.PREFERRED_SIZE))
								.addComponent(cbxZhanghu, GroupLayout.PREFERRED_SIZE, 280, GroupLayout.PREFERRED_SIZE)
								.addComponent(dChsActionDay, GroupLayout.PREFERRED_SIZE, 280, GroupLayout.PREFERRED_SIZE)
								.addComponent(tfldStockCode, GroupLayout.PREFERRED_SIZE, 272, GroupLayout.PREFERRED_SIZE))
							.addGap(73))
						.addGroup(groupLayout.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(tfdShuoMing, GroupLayout.PREFERRED_SIZE, 280, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(30, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addContainerGap(10, Short.MAX_VALUE)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(dChsActionDay, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
						.addComponent(chckbxNewCheckBox))
					.addGap(5)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(tfldStockCode, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
						.addComponent(lbljiaoyidaima))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
						.addComponent(cbxZhanghu, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
					.addGap(4)
					.addGroup(groupLayout.createSequentialGroup()
						.addGap(7)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(lblNewLabel_1)
							.addComponent(tfldJiage, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
							.addComponent(lblNewLabel_2)
							.addComponent(tfldGushu, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)))
					.addGap(4)
					.addGroup(groupLayout.createSequentialGroup()
						.addGap(8)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
							.addComponent(lblNewLabel_3)
							.addComponent(tfdShuoMing, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)))
					.addGap(63))
		);
		setLayout(groupLayout);

	
		
	}
	
}
