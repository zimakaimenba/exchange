package com.exchangeinfomanager.bankuaifengxi.HighlightAndExportNodes;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker.StateValue;

import com.exchangeinfomanager.Trees.CreateExchangeTree;
import com.exchangeinfomanager.bankuaifengxi.BanKuaiFengXi;
import com.exchangeinfomanager.bankuaifengxi.ai.analysis.easyrules.RuleOfCjeZbDpMaxWk;
import com.exchangeinfomanager.bankuaifengxi.xmlhandlerforbkfx.ServicesForBkfxEbkOutPutFileDirectRead;
import com.exchangeinfomanager.commonlib.JComboCheckBox.JComboCheckBox;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.guifactory.JLabelFactory;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.systemconfigration.SetupSystemConfiguration;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;

public class BkfxHightLightForGeGuPropertyFilePnl extends JPanel {

	private Properties mainboardprop;
	private Properties secondboardprop;
	private BanKuaiAndGeGuMatchingConditions globeexpc;
	private LocalDate curselectdate;
	private SetupSystemConfiguration sysconfig;
	private JTextField tfldextramin;
	private JTextField tfldextramax;

	public static final String TIMESHOULDCHANGE_PROPERTY = "timeshouldchange";
	/**
	 * Create the panel.
	 */
	public BkfxHightLightForGeGuPropertyFilePnl(String mainboardpropertiesfile,String secondboardpropertiesfile, BanKuaiAndGeGuMatchingConditions expc1) 
	{
//		this.mbpropfile = mainboardpropertiesfile;
//		this.sbpropfile = secondboardpropertiesfile;
		this.globeexpc = expc1;
		this.sysconfig = new SetupSystemConfiguration();
		
		this.mainboardprop = setupBkfxSettingProperties ( mainboardpropertiesfile);
		this.secondboardprop = setupBkfxSettingProperties ( secondboardpropertiesfile);
		createMainBorardGui ();
		createSecondBorardGui ();
	}
	
	private Properties setupBkfxSettingProperties(  String propertiesfile ) 
	{
		try {
			Properties prop = new Properties();
			FileInputStream inputStream = new FileInputStream(propertiesfile);
			if (inputStream != null) 	prop.load(inputStream);
			 
			inputStream.close();
			
			return prop;
		} catch (Exception e) {	e.printStackTrace();} finally {}
		
		return null;
	}
	
	private void createSecondBorardGui() 
	{
		String HightLightSecondBoradItemCount  = this.secondboardprop.getProperty ("columnmaxnumber");
		int itemCount;
		if(HightLightSecondBoradItemCount == null)			return ;
		else	itemCount = Integer.parseInt(HightLightSecondBoradItemCount);
		
		JLabel morelabel = new JLabel("更多:  " );
		this.add(morelabel);
		
		tfldextramin = new JTextField();
		tfldextramin.setPreferredSize(new Dimension(30, 25));
		tfldextramax = new JTextField();
		tfldextramax.setPreferredSize(new Dimension(30, 25));
		
		Vector<JCheckBox> v = new Vector<>();
		for(int i=0;i<itemCount;i++) {
			String proptpnl = String.valueOf(i) + "column_background_highlight_tohighlightpnl";
			String topnl = secondboardprop.getProperty (proptpnl);
			if(topnl != null && !topnl.equalsIgnoreCase("false"))	createBackGroundComponentsforSecondBoard(v, i);
			
			proptpnl = String.valueOf(i) + "column_foreground_highlight_tohighlightpnl";
			topnl = secondboardprop.getProperty (proptpnl);
			if(topnl != null && !topnl.equalsIgnoreCase("false"))	createForeGroundComponentsforSecondBoard(v, i);
		}
		
		JComboCheckBox cbbxmore = new JComboCheckBox(v);
		cbbxmore.setPreferredSize(new Dimension(120, 25));
		setupSecondBoardTextfieldValues((JCheckBox) cbbxmore.getSelectedItem());
		cbbxmore.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	SwingUtilities.invokeLater(new Runnable()
                {
                    public void run()
                    {
                    	JComboCheckBox check = ( JComboCheckBox ) e.getSource();
                    	JCheckBox selectitem = (JCheckBox)check.getSelectedItem();
                    	String min = null; if(!Strings.isNullOrEmpty(tfldextramin.getText()  )) min =tfldextramin.getText()  ;
            			String max = null; if(!Strings.isNullOrEmpty(tfldextramax.getText()  ) ) max = tfldextramax.getText();  
            			refreshHightLightValueBasedOnBoardInput (selectitem,min,max);
                    }
                });
            }
        } );
		cbbxmore.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) 
			{
				SwingUtilities.invokeLater(new Runnable()
			    {
			        public void run()
			        {
			        	JComboCheckBox check = ( JComboCheckBox ) e.getSource();
                    	JCheckBox selectitem = (JCheckBox)check.getSelectedItem();
                    	setupSecondBoardTextfieldValues (selectitem);
			        }
			    });
				
			}
		});
		this.add(cbbxmore);
		this.add(tfldextramin);
		this.add(tfldextramax);
	}
	private void createBackGroundComponentsforSecondBoard(Vector<JCheckBox> v, int i)
	{
		String propkw = String.valueOf(i) + "column_background_highlight_keyword";
		String keyword = secondboardprop.getProperty (propkw);
		if(keyword == null)	return;
		
		String propname = String.valueOf(i) + "column_background_highlight_info";
		String name  = secondboardprop.getProperty (propname);
		String propcolor = String.valueOf(i) + "column_background_highlight_color";
		String color = secondboardprop.getProperty (propcolor);
		
		List<String> kwinfolist = Splitter.on("OTHERWISE").omitEmptyStrings().splitToList(keyword);
		for(int m=0;m< kwinfolist.size(); m++) {
			String ckbkey =  kwinfolist.get(m);
			
//			if(ckbkey.equalsIgnoreCase("InFengXiFile")) {
//				String propckbname  = String.valueOf(i) + "column_background_highlight_" + ckbkey +  "_Label";
//				String ckbname  = secondboardprop.getProperty (propckbname);
//				createBackGroundComponentsforHighLightOfInFengXiFile (ckbname, keyword, color); 
//				continue;
//			}
			
			String propckbname  = String.valueOf(i) + "column_background_highlight_" + ckbkey +  "_Label";
			String ckbname  = secondboardprop.getProperty (propckbname);
			JCheckBox ckbxhighlight = new JCheckBox(ckbname);
			ckbxhighlight.setName(ckbkey);
			
			if(color != null && !color.equalsIgnoreCase("SYSTEM"))	ckbxhighlight.setBackground(Color.decode(color) );
			else if(color != null && color.equalsIgnoreCase("SYSTEM")) { Color systemcolor = getSystemColorForHighLight(ckbkey);  ckbxhighlight.setBackground(systemcolor);}
			else color = "#FF33FF";
			
			ckbxhighlight.setFont(new Font("宋体", Font.PLAIN, 12));
			ckbxhighlight.setForeground(new Color(0, 0, 0) );
			ckbxhighlight.setSelected(true);
			
			String propoperator  = String.valueOf(i) + "column_background_highlight_" + ckbkey +  "_operator"; 
			String operator = secondboardprop.getProperty (propoperator);
			if(operator == null || operator.equalsIgnoreCase("NONE")) 		continue;
				
			String prophighvalue = String.valueOf(i) + "column_background_highlight_" + ckbkey +  "_value";
			String highlightvalue = secondboardprop.getProperty (prophighvalue);
//			List<String> valuelist = Splitter.on(",").omitEmptyStrings().splitToList(highlightvalue);
			List<String> valuelist = new ArrayList<>(Splitter.on(",").omitEmptyStrings().splitToList(highlightvalue));
			ListIterator<String> iter = valuelist.listIterator();
			while(iter.hasNext()) {
				String tmpstr = iter.next().trim();
			    if(tmpstr.equals("MIN") || tmpstr.equals("MAX")) 
			        iter.set("");
			}
			JTextField tmptxfield = new JTextField();
			tmptxfield.setPreferredSize(new Dimension(30, 25));
			if(color != null && !color.equalsIgnoreCase("SYSTEM"))	tmptxfield.setForeground(Color.decode(color) );
			else if(color != null && color.equalsIgnoreCase("SYSTEM")) { Color systemcolor = getSystemColorForHighLight(ckbkey);  tmptxfield.setForeground(systemcolor);}
			
			switch (operator) {
			case "\'>=\'": tfldextramin.setText(valuelist.get(0));
				break;
			case "\'<=\'": tfldextramax.setText(valuelist.get(0));
				break;
			case "RANGE":  if(!valuelist.get(0).trim().equalsIgnoreCase("MIN"))  tfldextramin.setText(valuelist.get(0));
						   if(!valuelist.get(1).trim().equalsIgnoreCase("MAX"))  tfldextramax.setText(valuelist.get(1));
				break;
			}
			String min = null; if(!Strings.isNullOrEmpty(tfldextramin.getText()  )) min =tfldextramin.getText()  ;
			String max = null; if(!Strings.isNullOrEmpty(tfldextramax.getText()  ) ) max = tfldextramax.getText();  
			refreshHightLightValueBasedOnBoardInput (ckbxhighlight,min,max);
			v.add(ckbxhighlight);
		}
	
	}
	/*
	 * 
	 */
	private void createForeGroundComponentsforSecondBoard(Vector<JCheckBox> v, int i) 
	{
		String propkw = String.valueOf(i) + "column_foreground_highlight_keyword";
		String keyword = secondboardprop.getProperty (propkw);
		if(keyword == null)			return;
		
		String propcolor = String.valueOf(i) + "column_foreground_highlight_color";
		String color = secondboardprop.getProperty (propcolor);
		
		List<String> kwinfolist = Splitter.on("OTHERWISE").omitEmptyStrings().splitToList(keyword);
		for(int m=0;m< kwinfolist.size(); m++) {
			String ckbkey =  kwinfolist.get(m);
			
//			if(ckbkey.equalsIgnoreCase("InFengXiFile")) {
//				String propckbname  = String.valueOf(i) + "column_foreground_highlight_" + ckbkey +  "_Label";
//				String ckbname  = secondboardprop.getProperty (propckbname);
//				createBackGroundComponentsforHighLightOfInFengXiFile (ckbname, keyword, color); 
//				continue;
//			}
			
			String propckbname  = String.valueOf(i) + "column_background_highlight_" + ckbkey +  "_Label";
			String ckbname  = secondboardprop.getProperty (propckbname);
			JCheckBox ckbxhighlight = new JCheckBox(ckbname);
			ckbxhighlight.setName(ckbkey);
			
			if(color != null && !color.equalsIgnoreCase("SYSTEM"))	ckbxhighlight.setBackground(Color.decode(color) );
			else if(color != null && color.equalsIgnoreCase("SYSTEM")) { Color systemcolor = getSystemColorForHighLight(ckbkey);  ckbxhighlight.setBackground(systemcolor);}
			else color = "#FF33FF";
			
			ckbxhighlight.setFont(new Font("宋体", Font.PLAIN, 12));
			ckbxhighlight.setForeground(new Color(0, 0, 0) );
			ckbxhighlight.setSelected(true);
			
			String propoperator  = String.valueOf(i) + "column_foreground_highlight_" + ckbkey +  "_operator"; 
			String operator = secondboardprop.getProperty (propoperator);
			if(operator == null || operator.equalsIgnoreCase("NONE")) 		continue;
				
			String prophighvalue = String.valueOf(i) + "column_foreground_highlight_" + ckbkey +  "_value";
			String highlightvalue = secondboardprop.getProperty (prophighvalue);
//			List<String> valuelist = Splitter.on(",").omitEmptyStrings().splitToList(highlightvalue);
			List<String> valuelist = new ArrayList<>(Splitter.on(",").omitEmptyStrings().splitToList(highlightvalue));
			ListIterator<String> iter = valuelist.listIterator();
			while(iter.hasNext()) {
				String tmpstr = iter.next().trim();
			    if(tmpstr.equals("MIN") || tmpstr.equals("MAX")) 
			        iter.set("");
			}
			JTextField tmptxfield = new JTextField();
			tmptxfield.setPreferredSize(new Dimension(30, 25));
			if(color != null && !color.equalsIgnoreCase("SYSTEM"))	tmptxfield.setForeground(Color.decode(color) );
			else if(color != null && color.equalsIgnoreCase("SYSTEM")) { Color systemcolor = getSystemColorForHighLight(ckbkey);  tmptxfield.setForeground(systemcolor);}
			
			switch (operator) {
			case "\'>=\'": tfldextramin.setText(valuelist.get(0));
				break;
			case "\'<=\'": tfldextramax.setText(valuelist.get(0));
				break;
			case "RANGE": if(!valuelist.get(0).trim().equalsIgnoreCase("MIN"))  tfldextramin.setText(valuelist.get(0));
						  if(!valuelist.get(1).trim().equalsIgnoreCase("MAX"))  tfldextramax.setText(valuelist.get(1));
				break;
			}
			
			String min = null; if(!Strings.isNullOrEmpty(tfldextramin.getText()  )) min =tfldextramin.getText()  ;
			String max = null; if(!Strings.isNullOrEmpty(tfldextramax.getText()  ) ) max = tfldextramax.getText();  
			refreshHightLightValueBasedOnBoardInput (ckbxhighlight,min,max);
			v.add(ckbxhighlight);
		}
	}
	/*
	 * 
	 */
	private void createMainBorardGui() 
	{
		String columnmaxnumber   = mainboardprop.getProperty ("columnmaxnumber");
		int itemCount;
		if(columnmaxnumber  == null)	return ;
		else	itemCount = Integer.parseInt(columnmaxnumber );
		
		for(int i=0;i<itemCount;i++) {
			String proptpnl = String.valueOf(i) + "column_background_highlight_tohighlightpnl";
			String topnl = mainboardprop.getProperty (proptpnl);
			if(topnl != null && !topnl.equalsIgnoreCase("false"))	createBackGroundComponentsforMainBoard(i);
			
			proptpnl = String.valueOf(i) + "column_foreground_highlight_tohighlightpnl";
			topnl = mainboardprop.getProperty (proptpnl);
			if(topnl != null && !topnl.equalsIgnoreCase("false"))	createForeGroundComponentsforMainBoard(i);;
		}
	}
	private void createForeGroundComponentsforMainBoard (int i)
	{
		String propkw = String.valueOf(i) + "column_foreground_highlight_keyword";
		String keyword = mainboardprop.getProperty (propkw);
		if(keyword == null)			return;
		
		String propcolor = String.valueOf(i) + "column_foreground_highlight_color";
		String color = mainboardprop.getProperty (propcolor);
		
		List<String> kwinfolist = Splitter.on("OTHERWISE").omitEmptyStrings().splitToList(keyword);
		for(int m=0;m< kwinfolist.size(); m++) {
			String ckbkey =  kwinfolist.get(m);
			
			if(ckbkey.equalsIgnoreCase("InFengXiFile")) { 
				String propckbname  = String.valueOf(i) + "column_foreground_highlight_" + ckbkey +  "_Label";
				String ckbname  = mainboardprop.getProperty (propckbname);
				createBackGroundComponentsforHighLightOfInFengXiFile (ckbname, keyword, color); 
				continue;
			}
			
			String propckbname  = String.valueOf(i) + "column_foreground_highlight_" + ckbkey +  "_Label";
			String ckbname  = mainboardprop.getProperty (propckbname);
			JCheckBox ckbxhighlight = new JCheckBox(ckbname);
			ckbxhighlight.setName(ckbkey);
			
			if(color != null && !color.equalsIgnoreCase("SYSTEM"))	ckbxhighlight.setBackground(Color.decode(color) );
			else if(color != null && color.equalsIgnoreCase("SYSTEM")) { Color systemcolor = getSystemColorForHighLight(ckbkey);  ckbxhighlight.setBackground(systemcolor);}
			
			ckbxhighlight.setFont(new Font("宋体", Font.PLAIN, 12));
			ckbxhighlight.setForeground(new Color(0, 0, 0) );
			ckbxhighlight.setSelected(true);
			
			ckbxhighlight.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					JTextField txtfldltszmin = getHighlightTextField(ckbkey + "_MIN");
					JTextField txtfldltszmax = getHighlightTextField(ckbkey + "_MAX");
					String min = null ; if(txtfldltszmin != null) min = txtfldltszmin.getText();
					String max = null ; if(txtfldltszmax != null) max = txtfldltszmax.getText();
					refreshHightLightValueBasedOnBoardInput (ckbxhighlight, min, max);
				}
			});
			
			this.add(ckbxhighlight);
			
			String propoperator  = String.valueOf(i) + "column_foreground_highlight_" + ckbkey +  "_operator"; 
			String operator = mainboardprop.getProperty (propoperator);
			if(operator == null   || operator.equalsIgnoreCase("NONE"))			continue;
				
			String prophighvalue = String.valueOf(i) + "column_foreground_highlight_" + ckbkey +  "_value";
			String highlightvalue = mainboardprop.getProperty (prophighvalue);
			List<String> valuelist = Splitter.on(",").omitEmptyStrings().splitToList(highlightvalue);
			JTextField tmptxfield = new JTextField();
			tmptxfield.setPreferredSize(new Dimension(30, 25));
			if(color != null && !color.equalsIgnoreCase("SYSTEM"))	tmptxfield.setForeground(Color.decode(color) );
			else if(color != null && color.equalsIgnoreCase("SYSTEM")) { Color systemcolor = getSystemColorForHighLight(ckbkey);  tmptxfield.setBackground(systemcolor);}
			
			switch (operator) {
			case "\'>=\'": tmptxfield.setName(ckbkey + "_MIN");
						tmptxfield.setText(valuelist.get(0));
						this.add(tmptxfield);
						
				break;
			case "\'<=\'": tmptxfield.setName(ckbkey + "_MAX");
						tmptxfield.setText(valuelist.get(0));
						this.add(tmptxfield);
				break;
			case "RANGE": tmptxfield.setName(ckbkey + "_MIN");
						  tmptxfield.setText(valuelist.get(0));
						  this.add(tmptxfield);
						JTextField tmptxfieldmax = new JTextField();
						tmptxfieldmax.setName(ckbkey + "_MAX");
						tmptxfieldmax.setText(valuelist.get(1));
						tmptxfieldmax.setPreferredSize(new Dimension(30, 25));
						if(color != null && !color.equalsIgnoreCase("SYSTEM"))	tmptxfieldmax.setForeground(Color.decode(color) );
						else if(color != null && color.equalsIgnoreCase("SYSTEM")) { Color systemcolor = getSystemColorForHighLight(ckbkey);  tmptxfieldmax.setBackground(systemcolor);}
						this.add(tmptxfieldmax);
				break;
			}
			
			JTextField txtfldltszmin = getHighlightTextField(keyword + "_MIN");
			JTextField txtfldltszmax = getHighlightTextField(keyword + "_MAX");
			String min = null ; if(txtfldltszmin != null) min = txtfldltszmin.getText();
			String max = null ; if(txtfldltszmax != null) max = txtfldltszmax.getText();
			refreshHightLightValueBasedOnBoardInput (ckbxhighlight, min, max);
		}
	}
	private void createBackGroundComponentsforMainBoard (int i)
	{
		String propkw = String.valueOf(i) + "column_background_highlight_keyword";
		String keyword = mainboardprop.getProperty (propkw);
		if(keyword == null)			return;
		
		String propcolor = String.valueOf(i) + "column_background_highlight_color";
		String color = mainboardprop.getProperty (propcolor);
		
		List<String> kwinfolist = Splitter.on("OTHERWISE").omitEmptyStrings().splitToList(keyword);
		for(int m=0;m< kwinfolist.size(); m++) {
			String ckbkey =  kwinfolist.get(m).trim();
			
			if(ckbkey.equalsIgnoreCase("InFengXiFile")) {
				String propckbname  = String.valueOf(i) + "column_background_highlight_" + ckbkey +  "_Label";
				String ckbname  = mainboardprop.getProperty (propckbname);
				createBackGroundComponentsforHighLightOfInFengXiFile (ckbname, keyword, color); 
				continue;
			}
			
			String propckbname  = String.valueOf(i) + "column_background_highlight_" + ckbkey +  "_Label";
			String ckbname  = mainboardprop.getProperty (propckbname.trim());
			JCheckBox ckbxhighlight = new JCheckBox(ckbname);
			ckbxhighlight.setName(ckbkey);
			
			if(color != null && !color.equalsIgnoreCase("SYSTEM"))	ckbxhighlight.setBackground(Color.decode(color) );
			else if(color != null && color.equalsIgnoreCase("SYSTEM")) { Color systemcolor = getSystemColorForHighLight(ckbkey);  ckbxhighlight.setBackground(systemcolor);}
			
			ckbxhighlight.setFont(new Font("宋体", Font.PLAIN, 12));
			ckbxhighlight.setForeground(new Color(0, 0, 0) );
			ckbxhighlight.setSelected(true);
			
			ckbxhighlight.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					JTextField txtfldltszmin = getHighlightTextField(ckbkey + "_MIN");
					JTextField txtfldltszmax = getHighlightTextField(ckbkey + "_MAX");
					String min = null ; if(txtfldltszmin != null) min = txtfldltszmin.getText();
					String max = null ; if(txtfldltszmax != null) max = txtfldltszmax.getText();
					refreshHightLightValueBasedOnBoardInput (ckbxhighlight, min, max);
				}
			});
			
			this.add(ckbxhighlight);
			
			String propoperator  = String.valueOf(i) + "column_background_highlight_" + ckbkey +  "_operator"; 
			String operator = mainboardprop.getProperty (propoperator);
			if(operator == null) continue;
			
			String prophighvalue = String.valueOf(i) + "column_background_highlight_" + ckbkey +  "_value";
			String highlightvalue = mainboardprop.getProperty (prophighvalue);
			if(highlightvalue == null) continue;
//			List<String> valuelist1 = Splitter.on(",").omitEmptyStrings().splitToList(highlightvalue);
			List<String> valuelist = new ArrayList<>(Splitter.on(",").omitEmptyStrings().splitToList(highlightvalue));
			ListIterator<String> iter = valuelist.listIterator();
			while(iter.hasNext()) {
				String tmpstr = iter.next().trim();
			    if(tmpstr.equals("MIN") || tmpstr.equals("MAX")) 
			        iter.set("");
			}
			JTextField tmptxfield = new JTextField();
			tmptxfield.setPreferredSize(new Dimension(30, 25));
			if(color != null && !color.equalsIgnoreCase("SYSTEM"))	tmptxfield.setForeground(Color.decode(color) );
			else if(color != null && color.equalsIgnoreCase("SYSTEM")) { Color systemcolor = getSystemColorForHighLight(ckbkey);  tmptxfield.setBackground(systemcolor);}
			switch (operator) {
			case "\'>=\'": tmptxfield.setName(ckbkey + "_MIN");
						tmptxfield.setText(valuelist.get(0));
						this.add(tmptxfield);
						
				break;
			case "\'<=\'": tmptxfield.setName(ckbkey + "_MAX");
						tmptxfield.setText(valuelist.get(0));
						this.add(tmptxfield);
				break;
			case "RANGE": tmptxfield.setName(ckbkey + "_MIN");
						  if(!valuelist.get(0).equalsIgnoreCase("MIN")) tmptxfield.setText(valuelist.get(0));
						  this.add(tmptxfield);
						JTextField tmptxfieldmax = new JTextField();
						tmptxfieldmax.setName(ckbkey + "_MAX");
						if(!valuelist.get(1).equalsIgnoreCase("MAX")) tmptxfieldmax.setText(valuelist.get(1)); 
						tmptxfieldmax.setPreferredSize(new Dimension(30, 25));
						if(color != null && !color.equalsIgnoreCase("SYSTEM"))	tmptxfieldmax.setBackground(Color.decode(color) );
						else if(color != null && color.equalsIgnoreCase("SYSTEM")) { Color systemcolor = getSystemColorForHighLight(ckbkey);  tmptxfieldmax.setBackground(systemcolor);}
						this.add(tmptxfieldmax);
				break;
			}
			
			JTextField txtfldltszmin = getHighlightTextField(ckbkey + "_MIN");
			JTextField txtfldltszmax = getHighlightTextField(ckbkey + "_MAX");
			String min = null ; if(txtfldltszmin != null) min = txtfldltszmin.getText();
			String max = null ; if(txtfldltszmax != null) max = txtfldltszmax.getText();
			refreshHightLightValueBasedOnBoardInput (ckbxhighlight, min, max);
		}
	}
	private void createBackGroundComponentsforHighLightOfInFengXiFile( String ckbname, String keyword, String color) 
	{
		JCheckBox ckboxparsefile = new JCheckBox(ckbname);
		ckboxparsefile.setName(keyword);
		ckboxparsefile.setToolTipText(ckbname);
		ckboxparsefile.setFont(new Font("宋体", Font.PLAIN, 12));
		ckboxparsefile.setForeground(Color.BLACK);
		if(color != null)	ckboxparsefile.setBackground(Color.decode(color) );
		ckboxparsefile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				String keyword = ckboxparsefile.getName();
				JTextField tmptfldparsedfile = getHighlightTextField(keyword);
				if(ckboxparsefile.isSelected()) {
					if( Strings.isNullOrEmpty(tmptfldparsedfile.getText() )) {
						Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
						setCursor(hourglassCursor);
						
						chooseParsedFile (null,ckboxparsefile, tmptfldparsedfile);

						hourglassCursor = null;
						Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
						setCursor(hourglassCursor2);
					}
				} else
					tmptfldparsedfile.setText("");
			}
		});
		
		this.add(ckboxparsefile);
		
		JTextField tfldparsedfile = new JTextField();
		tfldparsedfile.setName(keyword);
		if(color != null)
			tfldparsedfile.setBackground(Color.decode(color) );
		tfldparsedfile.setColumns(12);
		tfldparsedfile.setToolTipText(tfldparsedfile.getText());
		
		this.add(tfldparsedfile);
	}

	/*
	 * 
	 */
	private Color getSystemColorForHighLight(String ckbkey) 
	{
		ckbkey = ckbkey.trim();
		Color color = Color.WHITE;
		switch(ckbkey) {
		case "CjeZbDpMaxWk": 
			color = new Color(255,0,0) ;
	     	break;
		case "CjeZbDpMinWk":
			color = Color.GREEN ;
	     	break;
		case "AverageChenJiaoErMaxWeek":
			color = Color.decode("#00FFFF");
		}
	 
		return color;
	}
	/*
	 * 
	 */
	protected void refreshHightLightValueBasedOnBoardInput(JCheckBox ckbxhighlight,String min, String max) 
	{
		String keyword = ckbxhighlight.getName();
		switch (keyword) {
		case "CLOSEVSMA":
			if(ckbxhighlight.isSelected() && !Strings.isNullOrEmpty(min) && !min.trim().equalsIgnoreCase("MIN")  ) 
				globeexpc.setSettingMaFormula(min);
			else
				globeexpc.setSettingMaFormula(null);
			
			break;
		case "LiuTongShiZhi":
			Double showltszmin = null; Double showltszmax = null;
			if(ckbxhighlight.isSelected()) {
				if( !Strings.isNullOrEmpty(min) && !min.trim().equalsIgnoreCase("MIN") ) 
					showltszmin =  Double.parseDouble( min );

				if( !Strings.isNullOrEmpty(max) && !Strings.isNullOrEmpty(max.trim()) && !max.trim().equalsIgnoreCase("MAX")  ) 
					showltszmax = Double.parseDouble(max );
			} 
			globeexpc.setSettingLiuTongShiZhi(showltszmin ,showltszmax);

			break;
		case "ZongShiZhi":
			Double showzszmin = null;Double showzszmax = null;
			if(ckbxhighlight.isSelected()) {
				if( !Strings.isNullOrEmpty(min) && !min.trim().equalsIgnoreCase("MIN") ) 
					showzszmin =  Double.parseDouble( min );
				
				if( !Strings.isNullOrEmpty(max) &&  !max.trim().equalsIgnoreCase("MAX")) 
					showzszmax = Double.parseDouble( max );
			}	
			globeexpc.setSettingZongShiZhix(showzszmin,showzszmax );
			
			break;
		case "CjeZbDpMaxWk":
			Integer cjedpmaxmin = null;
			if(ckbxhighlight.isSelected()  && !Strings.isNullOrEmpty(min) &&  !min.trim().equalsIgnoreCase("MIN") )
				cjedpmaxmin = Integer.parseInt( min );
			
			Integer cjedpmaxmax = null;
			if(ckbxhighlight.isSelected() && !Strings.isNullOrEmpty(max) &&  !max.trim().equalsIgnoreCase("MAX") )
				cjedpmaxmax = Integer.parseInt(max );
			
			globeexpc.setSettingCjeZbDpMaxWk(cjedpmaxmin, cjedpmaxmax);
			break;
		case "CjeZbDpMinWk":
			Integer cjedpminmin = null;
			if(ckbxhighlight.isSelected() &&  !Strings.isNullOrEmpty(min) &&  !min.trim().equalsIgnoreCase("MIN") )
				cjedpminmin = Integer.parseInt( min );
			
			Integer cjedpminmax = null;
			if(ckbxhighlight.isSelected() &&  !Strings.isNullOrEmpty(max) &&  !max.trim().equalsIgnoreCase("MAX") )
				cjedpminmax = Integer.parseInt( max );
			
			globeexpc.setSettingCjeZbDpMaxWk(cjedpminmin, cjedpminmax);
			break;
		case "AverageChenJiaoErMaxWeek" :
			Integer cjemxwkmin = null; Integer cjemxwkmax = null;
			if(ckbxhighlight.isSelected() &&  !Strings.isNullOrEmpty(min) &&  !min.trim().equalsIgnoreCase("MIN") ) 
				cjemxwkmin  = Integer.parseInt( min );
			if(ckbxhighlight.isSelected() &&  !Strings.isNullOrEmpty(max) &&  !min.trim().equalsIgnoreCase("MAX") ) 
				cjemxwkmax  = Integer.parseInt( max );
				
			globeexpc.setSettingChenJiaoErMaxWk(  cjemxwkmin,cjemxwkmax );
			break;
		case "HuanShouLv":
			Double hslmin = null; Double hslmax = null;
			if(ckbxhighlight.isSelected() &&  !Strings.isNullOrEmpty(min) &&  !min.trim().equalsIgnoreCase("MIN") )
				hslmin = Double.parseDouble( min ) ;
			if(ckbxhighlight.isSelected() &&  !Strings.isNullOrEmpty(max) &&  !min.trim().equalsIgnoreCase("MAX") )
				hslmin = Double.parseDouble( max ) ;
			
			globeexpc.setSettingHuanShouLv(hslmin,hslmax);
			break;
		case "QueKou" :
			if(ckbxhighlight.isSelected()) globeexpc.setHuiBuDownQueKou(true);	
			else  globeexpc.setHuiBuDownQueKou(false);	
			break;
		case "DailyZhangDieFuRangeInWeek":
			Double zdfmin = null; Double zdfmax = null;
			if(ckbxhighlight.isSelected() ) {
//				JTextField tfldzhangfumin = getHighlightTextField(keyword + "_MIN");
//				JTextField tfldzhangfumax = getHighlightTextField(keyword + "_MAX");
				if( !Strings.isNullOrEmpty(min) &&  !min.trim().equalsIgnoreCase("MIN")  ) 
					zdfmin = Double.parseDouble( min );
				if( !Strings.isNullOrEmpty(max) && !max.trim().equalsIgnoreCase("MAX") ) 
					zdfmax = Double.parseDouble( max );
			}
			
			globeexpc.setSettingDailyZhangDieFu (zdfmin, zdfmax );
			break;
		case "ChenJiaoEr" :
			Double cjemin = null; Double cjemax = null;
			if(ckbxhighlight.isSelected() ) {
//				JTextField tfldcjemin = getHighlightTextField(keyword + "_MIN");
//				JTextField tfldcjemax = getHighlightTextField(keyword + "_MAX");
				if( !Strings.isNullOrEmpty(min) && !min.equalsIgnoreCase("MIN") ) 
					cjemin = Double.parseDouble( min );
				if( !Strings.isNullOrEmpty(max)  && !max.equalsIgnoreCase("MAX")) 
					cjemax = Double.parseDouble( max );
			}
			
			globeexpc.setSettingChenJiaoEr(cjemin, cjemax );
		case "LastWkDpcjezbGrowingRate":
			Double showcjegrmin = null; Double showcjegrmax = null;
			if(ckbxhighlight.isSelected()) {
				
				if( !Strings.isNullOrEmpty(min) && !min.trim().equalsIgnoreCase("MIN")  ) 
					showcjegrmin =  Double.parseDouble(min );

				if( !Strings.isNullOrEmpty(max) && !max.trim().equalsIgnoreCase("MAX")  ) 
					showcjegrmax =  Double.parseDouble(max );
			} 
			globeexpc.setCjezbGrowingRate (showcjegrmin, showcjegrmax);
			break;
		case "LastWkCjeZbDpMaxWk" :
			break;
		case "GuJiaCLOSE":
			Double pricemin = null; Double pricemax = null;
			if(ckbxhighlight.isSelected() ) {
				if(!Strings.isNullOrEmpty(min) ) pricemin = Double.parseDouble(min);
				if(!Strings.isNullOrEmpty(max) ) pricemax = Double.parseDouble(max);
			} 
			this.globeexpc.setSettingStockPriceLevel(pricemin,pricemax);
			break;
		}
	}
	/*
	 * 
	 */
	private JTextField getHighlightTextField(String keywords) {
        for (Component tmpcomp : this.getComponents() ) {
        	
            String name = tmpcomp.getName();
            if (name != null && name.equalsIgnoreCase(keywords)) {
            	if(tmpcomp instanceof JTextField)
            		return (JTextField)  tmpcomp;
            }
        }
        return null;
    }

	/*
	 * 
	 */
	protected void chooseParsedFile(String filename, JCheckBox ckboxparsefile, JTextField tfldparsedfile) 
	{
		//先选择文件
		if(filename == null) {
			String parsedpath = sysconfig.getTDXModelMatchExportFile ();
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			chooser.setCurrentDirectory(new File(parsedpath) );
			
			if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			    if(chooser.getSelectedFile().isDirectory())
			    	filename = (chooser.getSelectedFile()+ "\\").replace('\\', '/');
			    else
			    	filename = (chooser.getSelectedFile()).toString().replace('\\', '/');
			} else
				return ;
		}
		
		if(!filename.endsWith("EBK") && !filename.endsWith("XML")) { //不是板块文件
			tfldparsedfile.setText("文件错误！请重新选择文件！");
			ckboxparsefile.setSelected(false);
			return ;
		}
		
		ServicesForBkfxEbkOutPutFileDirectRead bkfxfh = new ServicesForBkfxEbkOutPutFileDirectRead ();
		LocalDate edbfiledate = bkfxfh.setBkfeOutPutFile(filename);
		if(edbfiledate != null) {
			int exchangeresult = JOptionPane.showConfirmDialog(null,"文件指定日期是" + edbfiledate + "。是否更改到该日期？", "是否更改日期？", JOptionPane.OK_CANCEL_OPTION);
			if(exchangeresult != JOptionPane.CANCEL_OPTION) {
				bkfxfh.resetBkfxFileDate(edbfiledate);
				try {
					this.firePropertyChange(TIMESHOULDCHANGE_PROPERTY, "", edbfiledate);
				} catch (Exception e) {	e.printStackTrace();}
			 } else
				 bkfxfh.resetBkfxFileDate(curselectdate);
		}
				
		 bkfxfh.patchOutPutFileToTrees (CreateExchangeTree.CreateTreeOfBanKuaiAndStocks() );
		 tfldparsedfile.setText(filename);
	}

	
	private void setupSecondBoardTextfieldValues (JCheckBox ckbxhighlight)
	{
		String keyword = ckbxhighlight.getName() ;
		switch (keyword) {
		case "GuJiaCLOSE": 
			tfldextramin.setEnabled(true); tfldextramax.setEnabled(true);
			if(this.globeexpc.getSettingSotckPriceMax() == null || this.globeexpc.getSettingSotckPriceMax() >100000) 
					tfldextramax.setText("");
			else tfldextramax.setText(this.globeexpc.getSettingSotckPriceMax().toString());

			if(this.globeexpc.getSettingSotckPriceMin() == null || this.globeexpc.getSettingSotckPriceMin() <0) 
    				tfldextramin.setText("");
    		else tfldextramin.setText(this.globeexpc.getSettingSotckPriceMin().toString());
		
			break;
		case "ChenJiaoEr":
			tfldextramin.setEnabled(true);tfldextramax.setEnabled(true);
			if(this.globeexpc.getSettingChenJiaoErMin() == null) 	tfldextramin.setText("5");
			else	tfldextramin.setText(this.globeexpc.getSettingChenJiaoErMin().toString());
			
    		if(this.globeexpc.getSettingChenJiaoErMax() == null) tfldextramax.setText(" ");
    		else tfldextramax.setText(this.globeexpc.getSettingChenJiaoErMax().toString());
			break;
		case "LastWkDpcjezbGrowingRate" :
			tfldextramin.setEnabled(true);tfldextramax.setEnabled(true);
			if(this.globeexpc.getCjezbGrowingRateMin() == null) tfldextramin.setText("");
			else tfldextramin.setText(this.globeexpc.getCjezbGrowingRateMin().toString());
			
    		if(this.globeexpc.getCjezbGrowingRateMax() == null) tfldextramax.setText(" ");
    		else tfldextramax.setText(this.globeexpc.getCjezbGrowingRateMax().toString());
			break;
		case "HuanShouLv":
			tfldextramin.setEnabled(true);tfldextramax.setEnabled(true);
			if(this.globeexpc.getSettingHuanShouLvMin() == null) tfldextramin.setText("");
			else tfldextramin.setText(this.globeexpc.getSettingHuanShouLvMin().toString());
			if(this.globeexpc.getSettingHuanShouLvMax() == null) tfldextramax.setText("");
			else tfldextramax.setText(this.globeexpc.getSettingHuanShouLvMax().toString());
			break;
		case "LiuTongShiZhi":
			tfldextramin.setEnabled(true);tfldextramax.setEnabled(true);
			if(this.globeexpc.getSettingLiuTongShiZhiMin() == null) 	tfldextramin.setText("200");
			else	tfldextramin.setText(this.globeexpc.getSettingLiuTongShiZhiMin().toString());
			
    		if(this.globeexpc.getSettingLiuTongShiZhiMax() == null) tfldextramax.setText(" ");
    		else tfldextramax.setText(this.globeexpc.getSettingLiuTongShiZhiMax().toString());
    		break;
		}
	}
	/*
	 * 
	 */
	public void setCurrentDisplayDate (LocalDate date)
	{
		this.curselectdate = date;
	}
//	 
}
