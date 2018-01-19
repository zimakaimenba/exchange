package com.exchangeinfomanager.accountconfiguration.AccountOperation;

import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.LayoutStyle.ComponentPlacement;

import org.apache.log4j.Logger;

import javax.swing.JTextField;
import javax.swing.JRadioButton;
import net.miginfocom.swing.MigLayout;
import javax.swing.JComboBox;
import com.jgoodies.forms.layout.FormLayout;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class NewAccountCreating extends JPanel 
{
	/**
	 * Create the panel.
	 */
	public NewAccountCreating() 
	{
		//initializeAccountConfig ();
		initiliazeGui();
		createEvents ();
	}
	
//	private static Logger logger = Logger.getLogger(ImportQuanShangJiaoYiRecords.class);
	private String accountid;
	private String accountname;
	private Boolean rongzirongquan;
	private Boolean jihuo;
	private String xyzhid;
	
	
	private JTextField tfldAccountID;
	private JTextField tfldAccountName;
	private JRadioButton rdbtnRzrq;
	private JRadioButton rdbtnljjh;
	private JLabel lblRzrqToPutong;
	private ArrayList<String> availableputongaccounts;
	
	private JLabel lblDescption3;
	private JTextField xyzhcode;
	private JLabel label;
	private JLabel label_1;
	private JTextField tfldQuanshang;
	
	
//	private void initializeAccountConfig() {
//		// TODO Auto-generated method stub
//		accountsconfig = AccountConfiguration.getInstance();
//	}

	
	public Boolean getJihuo ()
	{
		return rdbtnljjh.isSelected();
	}
	public void setJihuo (Boolean jihuo)
	{
		this.jihuo = jihuo;
		rdbtnljjh.setSelected(jihuo);
	}

	/**
	 * @return the tfldAccountID
	 */
	public String getAccountid() 
	{
		return tfldAccountID.getText();
	}
	public void setAccountid (String accountid)
	{
		this.accountid = accountid;
		tfldAccountID.setText(this.accountid);
		
		tfldAccountID.setEnabled(false);
		tfldAccountName.setEnabled(false);
		tfldQuanshang.setEnabled(false);
	}

	/**
	 * @return the tfldAccountName
	 */
	public String getAccountName() 
	{
		return tfldAccountName.getText();
	}
	public void setAccountName (String accountname)
	{
		this.accountname = accountname;
		tfldAccountName.setText(this.accountname);
	}
	public String getXinYongAccountId ()
	{
		this.xyzhid = xyzhcode.getText();
		return this.xyzhid ;
	}
	public void setXinYongAccountId (String xyzhid)
	{
		this.xyzhid = xyzhid;
		xyzhcode.setText(this.xyzhid);
		
		if( this.xyzhid != null) {
			xyzhcode.setEnabled(false);
			rdbtnRzrq.setSelected(true);
			rdbtnRzrq.setEnabled(false);
		}
		
	}
	/**
	 * @return the rdbtnNewRadioButton
	 */
	public Boolean isRongziRongquan() {
		return rdbtnRzrq.isSelected();
	}
	public void setRongziRongquan(Boolean rzrq) 
	{
		this.rongzirongquan = rzrq;
		rdbtnRzrq.setSelected(this.rongzirongquan);
	}
	


	

	private void initiliazeGui() 
	{
		// TODO Auto-generated method stub
		JLabel lblNewLabel = new JLabel("\u8BBE\u7F6E\u65B0\u5E10\u53F7\uFF0C\u8D26\u53F7\u4E00\u65E6\u8BBE\u7ACB\u65E0\u6CD5\u5220\u9664\uFF01");
		
		JLabel lblAccountID = new JLabel("\u8D26\u6237\u4EE3\u7801");
		
		tfldAccountID = new JTextField();
		tfldAccountID.setColumns(10);
		
		JLabel lblAccountName = new JLabel("\u8D26\u6237\u540D\u79F0");
		
		tfldAccountName = new JTextField();
		tfldAccountName.setColumns(10);
		setLayout(new MigLayout("", "[73px][11px][170px,grow]", "[15px][21px][21px][21px][21px][23px][21px][23px][][][]"));
		
		label_1 = new JLabel("\u6240\u5C5E\u5238\u5546");
		add(label_1, "cell 0 3");
		
		tfldQuanshang = new JTextField();
		add(tfldQuanshang, "cell 2 3,growx");
		tfldQuanshang.setColumns(10);
		
		rdbtnRzrq = new JRadioButton("\u751F\u6210\u878D\u8D44\u878D\u5238\u4FE1\u7528\u8D26\u6237");
		
		add(rdbtnRzrq, "cell 0 5 3 1,alignx left,aligny top");
		add(lblNewLabel, "cell 0 0 3 1,alignx left,aligny top");
		add(lblAccountID, "cell 0 1,alignx left,aligny center");
		add(tfldAccountID, "cell 2 1,growx,aligny top");
		add(lblAccountName, "cell 0 2,alignx left,aligny center");
		add(tfldAccountName, "cell 2 2,growx,aligny top");
		
		lblRzrqToPutong = new JLabel("\u4FE1\u7528\u8D26\u6237\u4EE3\u7801");
		add(lblRzrqToPutong, "cell 0 6");
		
		xyzhcode = new JTextField();
		xyzhcode.setEnabled(false);
		add(xyzhcode, "cell 2 6,growx");
		xyzhcode.setColumns(10);
		
		rdbtnljjh = new JRadioButton("\u7ACB\u5373\u6FC0\u6D3B");
		rdbtnljjh.setSelected(true);
		add(rdbtnljjh, "cell 0 8,alignx center,aligny top");
		
		lblDescption3 = new JLabel("* \u878D\u8D44\u8D26\u6237\u4F1A\u5EFA\u7ACB3\u4E2A\u5B50\u8D26\u6237");
		add(lblDescption3, "cell 2 9");
		
		label = new JLabel("* \u81EA\u52A8\u5EFA\u7ACB\u73B0\u91D1\u8D26\u6237");
		add(label, "cell 2 10");
		
	}
	
	


	private void createEvents() 
	{
		rdbtnRzrq.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				if(xyzhid != null) {
					xyzhcode.setEnabled(false);
					rdbtnRzrq.setSelected(true);
					rdbtnRzrq.setEnabled(false);
				} else {
					xyzhcode.setEnabled(true);
				}
				

			}
		});
	}
	public String getSuoShuQuanShang() {
		return tfldQuanshang.getText();
	}
	public void setSuoShuQuanShang (String quanshang)
	{
		tfldQuanshang.setText(quanshang);
	}
	
}
