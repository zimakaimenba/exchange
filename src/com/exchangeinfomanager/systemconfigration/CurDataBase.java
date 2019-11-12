package com.exchangeinfomanager.systemconfigration;

import javax.swing.JPanel;
import javax.swing.JLabel;

import javax.swing.JTextField;




import net.miginfocom.swing.MigLayout;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

public class CurDataBase extends JPanel 
{
//	private EncryptAndDecypt encyptanddecypt;

	/**
	 * Create the panel.
	 */
	public CurDataBase (String sourcename) 
	{
		initializeGui ();
		tfldname.setText(sourcename);
		setLayout(new MigLayout("", "[72px][238px,grow]", "[24px][][30px][][][25px][31px]"));
		
		label = new JLabel("\u6570\u636E\u5E93\u7C7B\u578B");
		add(label, "cell 0 1,alignx trailing");
		
		comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"Access", "MySql", "SQL Server", "FoxPro"}));
		add(comboBox, "cell 1 1,growx");
		add(lblNewLabel_1, "cell 0 2,alignx left,aligny center");
		add(lblNewLabel, "cell 0 0,alignx right,aligny center");
		
		lblAccess = new JLabel("access: \u6570\u636E\u5E93\u6587\u4EF6\u5168\u8DEF\u5F84+\u6587\u4EF6\u540D");
		add(lblAccess, "cell 1 3");
		
		lblMysql = new JLabel("Mysql: \u4F8B\uFF1Alocalhost:3306/stockinfomanagementtest");
		add(lblMysql, "cell 1 4");
		add(lblNewLabel_2, "cell 0 5,alignx left,aligny center");
		add(lblNewLabel_3, "cell 0 6,alignx left,aligny center");
		add(tfldpswd, "cell 1 6,alignx left,growy");
		add(tflduser, "cell 1 5,alignx left,growy");
		add(tfldname, "cell 1 0,alignx center,growy");
		add(tflddbconstr, "cell 1 2,grow");
		curuseddbs = false;
	}

	private boolean curuseddbs;
	
	public void setCurDatabaseType (String type)
	{
		comboBox.setSelectedItem(type);
	}
	public String getCurDatabaseType ()
	{
		return comboBox.getSelectedItem().toString();
	}

	public void setPassWord(String trim) 
	{
		tfldpswd.setText( trim);
	}
	public String getPassWord() 
	{
		return tfldpswd.getText();
	}

	public void setUser(String trim) 
	{
		tflduser.setText(trim);
	}
	public String getUser() 
	{
		return tflduser.getText();
	}
	public void setDataBaseConStr(String text) 
	{
		tflddbconstr.setText(text);
	}
	public String getDataBaseConStr() 
	{
		return toUNIXpath(tflddbconstr.getText());
	}
	public String getCurDataBaseName() 
	{
		return tfldname.getText();
	}
	public void setCurrentSelectedDbs (boolean curuseddbs)
	{
		this.curuseddbs = curuseddbs;
	}
	public boolean getCurrentSelectedDbs ()
	{
		return this.curuseddbs;
	}
	private  String toUNIXpath(String filePath) 
	{
		    return filePath.replace('\\', '/');
	 }
	

	private JTextField tfldname;
	private JTextField tflddbconstr;
	private JTextField tflduser;
	private JTextField tfldpswd;
	private JLabel lblNewLabel_1;
	private JLabel lblNewLabel;
	private JLabel lblNewLabel_2;
	private JLabel lblNewLabel_3;
	private JLabel label;
	private JComboBox comboBox;
	private JLabel lblAccess;
	private JLabel lblMysql;
	


	private void initializeGui() 
	{
		lblNewLabel = new JLabel("\u6570\u636E\u5E93\u6E90\u540D");
		
		tfldname = new JTextField();
		tfldname.setColumns(10);
		
		lblNewLabel_1 = new JLabel("\u6570\u636E\u5E93\u8FDE\u63A5\u5B57");
		
		tflddbconstr = new JTextField();
		tflddbconstr.setColumns(10);
		
		lblNewLabel_2 = new JLabel("\u7528\u6237\u540D");
		
		tflduser = new JTextField();
		tflduser.setColumns(10);
		
		lblNewLabel_3 = new JLabel("\u5BC6\u7801");
		
		tfldpswd = new JTextField();
		tfldpswd.setColumns(10);

		this.setFocusable(true);
		
	}
	
	

}
