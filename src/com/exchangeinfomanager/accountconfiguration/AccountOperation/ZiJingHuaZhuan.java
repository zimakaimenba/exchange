package com.exchangeinfomanager.accountconfiguration.AccountOperation;

import javax.swing.JPanel;
import javax.swing.JComboBox;
import net.miginfocom.swing.MigLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.hsqldb.lib.StringUtil;

import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountInfoBasic;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.mysql.jdbc.StringUtils;
import com.toedter.calendar.JDateChooser;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class ZiJingHuaZhuan extends JPanel {

	private static final long serialVersionUID = 1L;
	

	public ZiJingHuaZhuan(AccountInfoBasic actionzhanghu) 
	{
		this.actionzhanghu = actionzhanghu;
		//this.hasxinyongzhanghu = hasxinyongzhanghu2; //标记是否有信用账户
		sysconfig = SystemConfigration.getInstance();
		gjcbuysellmaps = new HashMap<String,String> ();
		gjctosystemmaps = new HashMap<String,String> ();
		gjcshuomingmmaps = new HashMap<String,String> ();
		this.quanshangzijingguanjianci (actionzhanghu);
		initializeGui ();
		createEvents();
	}

	private SystemConfigration sysconfig;
	private HashMap<String,String> gjcbuysellmaps ;
	private HashMap<String,String> gjctosystemmaps;
	private HashMap<String, String> gjcshuomingmmaps;
	private JTextField tfdZijing;
	private JComboBox<String> cbxaccount;
	private AccountInfoBasic actionzhanghu;
	private JDateChooser dateChooser;
	private String actionaccount; 
	private JTextField tfldshuoming;
	private boolean zhuanruchu;
	
	
	
	private void createEvents() 
	{
		cbxaccount.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) 
			{
				if(e.getStateChange() == ItemEvent.SELECTED) {
					
					String curaction = cbxaccount.getSelectedItem().toString();
					String shuoming = gjcshuomingmmaps.get(curaction);
					tfldshuoming.setText("");
					tfldshuoming.setText(shuoming);
				}
				
				if(e.getStateChange() == ItemEvent.DESELECTED) {
					
				}
			}
		});
	}
	public boolean isZhuanRuChu ()
	{
		String actionacnt = cbxaccount.getSelectedItem().toString();
		this.zhuanruchu = Boolean.parseBoolean( gjcbuysellmaps.get(actionacnt) );
		return this.zhuanruchu;
	}
	
	public void setShuoMing (String shuoming2)
	{
		this.tfldshuoming.setText(shuoming2);
	}
	public String getShuoMing ()
	{
		return this.tfldshuoming.getText();
	}
//	public void setActionType(String actiontype2)
//	{
//		this.actiontype = actiontype2;
//	}
//	public String getActionType()
//	{
//		return this.actiontype;
//	}
	public double getZijing ()
	{
		try {
			return Double.parseDouble( tfdZijing.getText() );
		} catch (java.lang.NumberFormatException e) {
			return 0;
		}
	}
	public void setZiJing (double zijing) 
	{
		tfdZijing.setText(String.valueOf(zijing));
	}

	public String getZhuanRuOrChuActionAccount ()
	{
		String actionqstype = cbxaccount.getSelectedItem().toString();
		String systemstr = gjctosystemmaps.get(actionqstype);
		if(systemstr.equals("xianjing")) {
			
			String basicname = "";
			int index = actionzhanghu.getAccountName().indexOf('-');
			if(index == -1)
				basicname = actionzhanghu.getAccountName();
			else 
				basicname = actionzhanghu.getAccountName().substring(0, index);

			
			String cashname = basicname + "- 现金账户";  
//			String rzrqxyptname = basicname + "- 信用普通子账户";
//			String rzrqrzname = basicname + "- 融资子账户";
//			String rzrqrqname = basicname + "- 融券子账户";
			
			return cashname;
		} else {
			return null;
		}
		
	}
	public String getQuanShangZhuanRuOrChuLeiXing ()
	{
		return (String)cbxaccount.getSelectedItem();
	}
	public void setQuanShangZhuanRuOrChuLeiXing (String actionacnt)
	{
//		if( ((DefaultComboBoxModel) cbxaccount.getModel()).getIndexOf(actionacnt) == -1)
//			((DefaultComboBoxModel) cbxaccount.getModel()).addElement(actionacnt);
		cbxaccount.setSelectedItem(actionacnt);
		zhuanruchu = Boolean.parseBoolean( gjcbuysellmaps.get(actionacnt) );

	}
	public void setActionDate (Date actiondate)
	{
		dateChooser.setDate(actiondate);
	}
	public Date getActionDate ()
	{
		return dateChooser.getDate();
	}

	private void quanshangzijingguanjianci (AccountInfoBasic actionzhanghu)
	{
		String suoshuquanshang = actionzhanghu.getSuoShuQuanShngYingHang ();
		 
		if(suoshuquanshang.contains("招商") ) {
			
			File sysconfigfilexml = new File (sysconfig.getQuanShangJiaoYiSheZhi());
	    	FileInputStream xmlfileinput = null;
			try {
				xmlfileinput = new FileInputStream(sysconfigfilexml);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			Element xmlroot = null;
			try {
				SAXReader reader = new SAXReader();
				Document xmldoc = reader.read(xmlfileinput);
				xmlroot = xmldoc.getRootElement();
			} catch (DocumentException e) {
				e.printStackTrace();
			}
			
			Element jiaoyiele = xmlroot.element("ZiJingGuanJianCiZhaoShang");
			Iterator it = jiaoyiele.elementIterator();
	   	 	while (it.hasNext()) {
	   	 	  Element element = (Element) it.next();
	   	 	  String qsgjc = element.getText();
	   	 	  String mairuchu = element.attribute("iszhuanru").getText().toLowerCase();
	   	 	  gjcbuysellmaps.put(qsgjc,mairuchu);
	   	 	  
	   	 	  String systemtype = element.attribute("shuxing").getText().toLowerCase();
	   	 	  gjctosystemmaps.put(qsgjc, systemtype);
	   	 	  
	   	 	  if(  element.attribute("shuoming") != null ) {
	   	 		  String shuoming = element.attribute("shuoming").getText().toLowerCase();
	   	 		  gjcshuomingmmaps.put(qsgjc, shuoming);
	   	 	  }
	   	 	}
		}
	}
	
	private void initializeGui() 
	{
		DefaultComboBoxModel cbxmodel = new DefaultComboBoxModel ();
		
		for(String str:gjcbuysellmaps.keySet())
			cbxmodel.addElement(str);
		cbxaccount = new JComboBox<String>(cbxmodel);
		
		
		JLabel lblNewLabel = new JLabel("\u9009\u62E9\u8F6C\u5165\u8F6C\u51FA\u8D26\u6237");
		
		JLabel lblNewLabel_1 = new JLabel("\u8F93\u5165\u8D44\u91D1\u91D1\u989D");
		
		tfdZijing = new JTextField();
		tfdZijing.setColumns(10);
		
		JLabel label = new JLabel("\u65E5\u671F");
		
		dateChooser = new JDateChooser();
		
		JLabel label_1 = new JLabel("\u8BF4\u660E");
		
		tfldshuoming = new JTextField();
		tfldshuoming.setColumns(10);
		setLayout(new MigLayout("", "[96px][203px]", "[26px][27px][26px][21px]"));
		add(label_1, "cell 0 3,alignx left,aligny top");
		add(tfldshuoming, "cell 1 3,growx,aligny top");
		add(lblNewLabel_1, "cell 0 2,alignx left,aligny center");
		add(tfdZijing, "cell 1 2,grow");
		add(lblNewLabel, "cell 0 1,alignx left,aligny bottom");
		add(cbxaccount, "cell 1 1,grow");
		add(label, "cell 0 0,alignx left,aligny bottom");
		add(dateChooser, "cell 1 0,grow");

		String curaction = cbxaccount.getSelectedItem().toString();
		String shuoming = gjcshuomingmmaps.get(curaction);
		tfldshuoming.setText("");
		tfldshuoming.setText(shuoming);
	}
}
