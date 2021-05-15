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
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.SwingWorker.StateValue;

import org.apache.commons.lang3.SerializationUtils;

import com.exchangeinfomanager.Trees.BanKuaiAndStockTree;
import com.exchangeinfomanager.Trees.CreateExchangeTree;
import com.exchangeinfomanager.bankuaifengxi.BanKuaiFengXi;
import com.exchangeinfomanager.bankuaifengxi.NodeChenJiaoErComparator;
import com.exchangeinfomanager.bankuaifengxi.bankuaiinfotable.BanKuaiInfoTableModel;
import com.exchangeinfomanager.bankuaifengxi.xmlhandlerforbkfx.ServicesForBkfxEbkOutPutFileDirectRead;
import com.exchangeinfomanager.commonlib.SerialUtils;
import com.exchangeinfomanager.commonlib.SystemAudioPlayed;
import com.exchangeinfomanager.commonlib.JComboCheckBox.JComboCheckBox;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.guifactory.JLabelFactory;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.TDXNodes;
import com.exchangeinfomanager.nodes.stocknodexdata.ohlcvadata.NodeGivenPeriodDataItem;
import com.exchangeinfomanager.systemconfigration.SetupSystemConfiguration;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.google.gson.Gson;

public class BkfxHightLightPnl extends JPanel {

	private Properties prop;
	private BanKuaiGeGuMatchCondition globeexpc;
	private List<BanKuaiGeGuMatchCondition> exportcond;
	private ExportTask exporttask;
	private LocalDate curselectdate;
	private String globeperiod;
	private SetupSystemConfiguration sysconfig;
	private StockInfoManager stockmanager;
	private JTextField tfldextramin;
	private JTextField tfldextramax;
//	private LocalDate curhighdate;
	
	public static final String TIMESHOULDCHANGE_PROPERTY = "timeshouldchange";
	/**
	 * Create the panel.
	 */
	public BkfxHightLightPnl(Properties prop1,BanKuaiGeGuMatchCondition expc1) 
	{
		this.prop = prop1;
		this.globeexpc = expc1;
		this.sysconfig = new SetupSystemConfiguration();
		this.exportcond = new ArrayList<> ();
		
		createMainBorardGui ();
		createSecondBorardGui ();
		setupPredefinedExportFormula ();
	}
	
	
	private void setupPredefinedExportFormula() {
		String PreSetExportConditionFormulaCount = prop.getProperty("PreSetExportConditionFormulaCount");
		if(PreSetExportConditionFormulaCount == null  || Integer.parseInt(PreSetExportConditionFormulaCount) == 0)
			return ;
		List<String> formula = new ArrayList<> ();
		for(int i=0;i<Integer.parseInt(PreSetExportConditionFormulaCount);i++) {
			String PreSetExportConditionFormula  = prop.getProperty ("PreSetExportConditionFormula" + String.valueOf(i+1) );
			if(PreSetExportConditionFormula != null)
				formula.add(PreSetExportConditionFormula);
		}
		if(!formula.isEmpty())
			this.globeexpc.setPredefinedExportConditionFormula (formula);
	}


	private void createMainBorardGui() 
	{
		String HightLightMainBoradItemCount  = prop.getProperty ("HightLightMainBoradItemCount");
		int itemCount;
		if(HightLightMainBoradItemCount == null)
			return ;
		else
			itemCount = Integer.parseInt(HightLightMainBoradItemCount);
		
		for(int i=0;i<itemCount;i++) {
			String propname = "HightLightMainBoradItem" + String.valueOf(i) + "_name";
			String name  = prop.getProperty (propname);
			
			String propkw = "HightLightMainBoradItem" + String.valueOf(i) + "_keyword";
			String keyword = prop.getProperty (propkw);
			
			String propstatus = "HightLightMainBoradItem" + String.valueOf(i) + "_status";
			String status = prop.getProperty (propstatus);
			
			String propcolor = "HightLightMainBoradItem" + String.valueOf(i) + "_backgroundcolor";
			String color = prop.getProperty (propcolor);
			
			String propinfo = "HightLightMainBoradItem" + String.valueOf(i) + "_info";
			String info = prop.getProperty (propinfo);
			
			if(keyword == null)
				continue;
			
			if(keyword.equals("ExportTiaoJianGeGu") ) {
				JLabel btnaddexportcond = JLabelFactory.createButton("",35, 25);
				btnaddexportcond.setHorizontalAlignment(SwingConstants.LEFT);
				btnaddexportcond.setToolTipText("<html>\u5BFC\u51FA\u6761\u4EF6\u8BBE\u7F6E(\u659C\u4F53\u9009\u9879\u4E0D\u5728\u5BFC\u51FA\u8303\u56F4\u5185):<br></html>");
				btnaddexportcond.setIcon(new ImageIcon(BanKuaiFengXi.class.getResource("/images/add-circular-outlined-button.png")));
				btnaddexportcond.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) 
					{
						if(SwingUtilities.isLeftMouseButton(arg0) ) {
							initializeExportConditions (btnaddexportcond);
						} else 
						if (SwingUtilities.isRightMouseButton(arg0)) {
							if( exportcond != null) { 
								exportcond.clear();
								
								btnaddexportcond.setText(String.valueOf(0));
								btnaddexportcond.setToolTipText("<html>导出条件设置(鼠标右键删除设置)<br></html>");
							}
						} else if (SwingUtilities.isMiddleMouseButton(arg0)) {							
						}
					}
				});
				
				this.add(btnaddexportcond);
				
				JProgressBar progressBarExport = new JProgressBar();
				progressBarExport.setPreferredSize(new Dimension(85, 25));
				progressBarExport.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) {
						if (exporttask == null)  
				        	exportBanKuaiWithGeGuOnCondition();
				         else 
				        	exporttask.cancel(true);
					}
				});
				progressBarExport.setString("点击导出条件个股");
				progressBarExport.setToolTipText("点击导出条件个股");
		        progressBarExport.setStringPainted(true);
				
		        this.add(progressBarExport);
		        
				continue;
			} else
			if(keyword.equals("FengXiFile") ) {
				JCheckBox ckboxparsefile = new JCheckBox(name);
				ckboxparsefile.setName(keyword);
				ckboxparsefile.setBackground(Color.ORANGE);
				ckboxparsefile.setToolTipText(name);
				ckboxparsefile.setFont(new Font("宋体", Font.PLAIN, 12));
				ckboxparsefile.setForeground(Color.BLACK);
				if(color != null)
					ckboxparsefile.setBackground(Color.decode(color) );
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
				
				refreshMainBoardHightLight(ckboxparsefile);
			} else { //其他
				JCheckBox ckbxhighlight = new JCheckBox(name);
				ckbxhighlight.setName(keyword);
				if(color != null)
					ckbxhighlight.setBackground(Color.decode(color) );
				ckbxhighlight.setFont(new Font("宋体", Font.PLAIN, 12));
				ckbxhighlight.setForeground(new Color(0, 0, 0) );
				if(status == null)
					ckbxhighlight.setSelected(false);
				else
				if(status.equalsIgnoreCase("SELECTED"))
					ckbxhighlight.setSelected(true);
				else
					ckbxhighlight.setSelected(false);
				ckbxhighlight.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						refreshMainBoardHightLight (ckbxhighlight);
					}
				});
				
				this.add(ckbxhighlight);
				
				if(info == null) {
					refreshMainBoardHightLight (ckbxhighlight);
					continue;
				}
				
				List<String> infolist = Splitter.on(",").omitEmptyStrings().splitToList(info); 
				for(int j=0;j<infolist.size();j++) {
					String txfldinfo = infolist.get(j);
					JTextField tmptxfield = new JTextField();
					tmptxfield.setPreferredSize(new Dimension(30, 25));
					if(color != null)
						tmptxfield.setForeground(Color.decode(color) );
					if(infolist.size() ==1) {
						tmptxfield.setText(txfldinfo);
						tmptxfield.setName(keyword);
					} else {
						if(txfldinfo.equals("MIN") || txfldinfo.equals("MAX"))
							tmptxfield.setText("");
						else
							tmptxfield.setText(txfldinfo);
						if(j==0)
							tmptxfield.setName(keyword + "MIN");
						else
							tmptxfield.setName(keyword + "MAX");
					}
									
					this.add(tmptxfield);
				}
				
				refreshMainBoardHightLight (ckbxhighlight);
			}
		}
		
		String HightLightSecondBoradItemCount  = prop.getProperty ("HightLightSecondBoradItemCount");
		if(HightLightSecondBoradItemCount == null)
			return ;
		else
			itemCount = Integer.parseInt(HightLightSecondBoradItemCount);
	}
	private void createSecondBorardGui() 
	{
		String HightLightSecondBoradItemCount  = prop.getProperty ("HightLightSecondBoradItemCount");
		int itemCount;
		if(HightLightSecondBoradItemCount == null)
			return ;
		else
			itemCount = Integer.parseInt(HightLightSecondBoradItemCount);
		
		JLabel morelabel = new JLabel("更多:  " );
		this.add(morelabel);
		
		tfldextramin = new JTextField();
		tfldextramin.setPreferredSize(new Dimension(30, 25));
		tfldextramax = new JTextField();
		tfldextramax.setPreferredSize(new Dimension(30, 25));
		
		Vector<JCheckBox> v = new Vector<>();
		for(int i=0;i<itemCount;i++) {
			String propname = "HightLightSecondBoradItem" + String.valueOf(i) + "_name";
			String name  = prop.getProperty (propname);
			
			String propkw = "HightLightSecondBoradItem" + String.valueOf(i) + "_keyword";
			String keyword = prop.getProperty (propkw);
			
			String propstatus = "HightLightSecondBoradItem" + String.valueOf(i) + "_status";
			String status = prop.getProperty (propstatus);
			
			String propcolor = "HightLightSecondBoradItem" + String.valueOf(i) + "_backgroundcolor";
			String color = prop.getProperty (propcolor);
			
			String propinfo = "HightLightSecondBoradItem" + String.valueOf(i) + "_info";
			String info = prop.getProperty (propinfo);
			
			if(keyword == null)
				continue;
			
			JCheckBox ckbxhighlight = new JCheckBox(name,false);
			ckbxhighlight.setName(keyword);
			if(color != null)
				ckbxhighlight.setForeground(Color.decode(color) );
			if(status == null)
				ckbxhighlight.setSelected(false);
			else
			if(status.equalsIgnoreCase("SELECTED"))
				ckbxhighlight.setSelected(true);
			else
				ckbxhighlight.setSelected(false);
			
			List<String> infolist = Splitter.on(",").omitEmptyStrings().splitToList(info); 
			for(int j=0;j<infolist.size();j++) {
				String txfldinfo = infolist.get(j);
				if(infolist.size() ==1) {
					tfldextramin.setText(txfldinfo);
					tfldextramin.setName(keyword);
				} else {
					if(txfldinfo.equals("MIN") && j==0 )
						tfldextramin.setText("");
					else
					if(txfldinfo.equals("MAX") && j==1 )
						tfldextramax.setText("");
					else 
					if(j == 0)
						tfldextramin.setText(txfldinfo);
					else
					if(j==1)
						tfldextramax.setText(txfldinfo);
				}
			}
			refreshSecondBoardHightLights (ckbxhighlight);
			v.add(ckbxhighlight);
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
                    	refreshSecondBoardHightLights (selectitem);
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
	
	private String getSecondboardItemInfo (String keyword)
	{
		String HightLightSecondBoradItemCount  = prop.getProperty ("HightLightSecondBoradItemCount");
		int itemCount;
		if(HightLightSecondBoradItemCount == null)
			return null ;
		else
			itemCount = Integer.parseInt(HightLightSecondBoradItemCount);
		
		for(int i=0;i<itemCount;i++) {
			String propname = "HightLightSecondBoradItem" + String.valueOf(i) + "_name";
			String name  = prop.getProperty (propname);
			
			String propkw = "HightLightSecondBoradItem" + String.valueOf(i) + "_keyword";
			String tmpkeyword = prop.getProperty (propkw);
			
			String propstatus = "HightLightSecondBoradItem" + String.valueOf(i) + "_status";
			String status = prop.getProperty (propstatus);
			
			String propcolor = "HightLightSecondBoradItem" + String.valueOf(i) + "_backgroundcolor";
			String color = prop.getProperty (propcolor);
			
			String propinfo = "HightLightSecondBoradItem" + String.valueOf(i) + "_info";
			String info = prop.getProperty (propinfo);
			
			if(keyword == null)
				continue;
			
			if(tmpkeyword.equalsIgnoreCase(keyword))
				return info;
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

	protected void refreshMainBoardHightLight(JCheckBox ckbxhighlight) 
	{
		String keyword = ckbxhighlight.getName();
		switch (keyword) {
		case "CLOSEVSMA":
			JTextField txtfldma = getHighlightTextField(keyword);
			if(ckbxhighlight.isSelected() ) 
				globeexpc.setSettingMaFormula(txtfldma.getText());
			else
				globeexpc.setSettingMaFormula(null);
			
			break;
		case "LiuTongShiZhi":
			JTextField txtfldltszmin = getHighlightTextField(keyword + "MIN");
			JTextField txtfldltszmax = getHighlightTextField(keyword + "MAX");
			Double showltszmin = null; Double showltszmax = null;
			if(ckbxhighlight.isSelected()) {
				if( !Strings.isNullOrEmpty(txtfldltszmin.getText()) ) 
					showltszmin =  Double.parseDouble( txtfldltszmin.getText() );

				if( !Strings.isNullOrEmpty(txtfldltszmax.getText()) ) 
					showltszmax = Double.parseDouble( txtfldltszmax.getText() );
			} 
			globeexpc.setSettingLiuTongShiZhi(showltszmin ,showltszmax);

			break;
		case "ZongShiZhi":
			JTextField txtfldzszmin = getHighlightTextField(keyword + "MIN");
			JTextField txtfldzszmax = getHighlightTextField(keyword + "MAX");
			Double showzszmin = null;Double showzszmax = null;
			if(ckbxhighlight.isSelected()) {
				if( !Strings.isNullOrEmpty(txtfldzszmin.getText()) ) 
					showzszmin =  Double.parseDouble( txtfldzszmin.getText() );
				
				if( !Strings.isNullOrEmpty(txtfldzszmax.getText()) ) 
					showzszmax = Double.parseDouble( txtfldzszmax.getText() );
			}	
			globeexpc.setSettingZongShiZhix(showzszmin,showzszmax );
			
			break;
		case "CjeZbDpMaxWk":
			JTextField txtflddpmaxwk = getHighlightTextField(keyword);
			if(ckbxhighlight.isSelected() ) 
				globeexpc.setSettingDpMaxWk(Integer.parseInt( txtflddpmaxwk.getText() ) );
			else
				globeexpc.setSettingDpMaxWk(null );
			
			break;
		case "CjeZbDpMinWk":
			JTextField txtflddpminwk = getHighlightTextField(keyword);
			if(ckbxhighlight.isSelected() ) 
				globeexpc.setSettingDpMinWk(Integer.parseInt(txtflddpminwk.getText())  );
			else
				globeexpc.setSettingDpMinWk (null);
			
			break;
		case "AverageChenJiaoErMaxWeek" :
			JTextField txtfldAverageCjeMaxWk = getHighlightTextField(keyword);
			if(ckbxhighlight.isSelected() ) 
				globeexpc.setSettingChenJiaoErMaxWk(  Integer.parseInt( txtfldAverageCjeMaxWk.getText()) );
			else
				globeexpc.setSettingChenJiaoErMaxWk(null);
			
			break;
		case "HuanShouLv":
			JTextField txtfldhsl = getHighlightTextField(keyword);
			if(ckbxhighlight.isSelected())
				globeexpc.setSettingHuanShouLv(Double.parseDouble( txtfldhsl.getText() ) );
			else 
				globeexpc.setSettingHuanShouLv( null );
			break;
		case "QueKou" :
			if(ckbxhighlight.isSelected()) {
				globeexpc.setHuiBuDownQueKou(true);
				globeexpc.setZhangTing(true);
			} else {
				globeexpc.setHuiBuDownQueKou(false);
				globeexpc.setZhangTing(false);
			}
			
			break;
		case "DailyZhangDieFuRangeInWeek":
			Double zdfmin = null; Double zdfmax = null;
			if(ckbxhighlight.isSelected() ) {
				JTextField tfldzhangfumin = getHighlightTextField(keyword + "MIN");
				JTextField tfldzhangfumax = getHighlightTextField(keyword + "MAX");
				if( !Strings.isNullOrEmpty(tfldzhangfumin.getText()) ) 
					zdfmin = Double.parseDouble( tfldzhangfumin.getText() );
				if( !Strings.isNullOrEmpty(tfldzhangfumax.getText()) ) 
					zdfmax = Double.parseDouble( tfldzhangfumax.getText() );
			}
			
			globeexpc.setSettingDailyZhangDieFu (zdfmin, zdfmax );
			break;
		}
		
	}
	private void setupSecondBoardTextfieldValues (JCheckBox ckbxhighlight)
	{
		String keyword = ckbxhighlight.getName() ;
		switch (keyword) {
		case "GuJiaCLOSE": 
			tfldextramin.setEnabled(true); tfldextramax.setEnabled(true);
			if(this.globeexpc.getSettingSotckPriceMax() == null || this.globeexpc.getSettingSotckPriceMax() >100000) {
					tfldextramax.setText("");
			}	else
				tfldextramax.setText(this.globeexpc.getSettingSotckPriceMax().toString());

			if(this.globeexpc.getSettingSotckPriceMin() == null || this.globeexpc.getSettingSotckPriceMin() <0) {
    				tfldextramin.setText("");
    		}
    		else
    			tfldextramin.setText(this.globeexpc.getSettingSotckPriceMin().toString());
		
			break;
		case "ChenJiaoEr":
			tfldextramin.setEnabled(true);tfldextramax.setEnabled(true);
			if(this.globeexpc.getSettingChenJiaoErMin() == null) {
					tfldextramin.setText("5");
			}
    		else
    			tfldextramin.setText(this.globeexpc.getSettingChenJiaoErMin().toString());
			
    		if(this.globeexpc.getSettingChenJiaoErMax() == null) {
    				tfldextramax.setText(" ");
    		}
    		else
    			tfldextramax.setText(this.globeexpc.getSettingChenJiaoErMax().toString());
			break;
		case "LastWkDpcjezbGrowingRate" :
			tfldextramin.setEnabled(true);tfldextramax.setEnabled(true);
			if(this.globeexpc.getCjezbGrowingRateMin() == null) {
					tfldextramin.setText("");
			}
			else
				tfldextramin.setText(this.globeexpc.getCjezbGrowingRateMin().toString());
			
    		if(this.globeexpc.getCjezbGrowingRateMax() == null) {
    				tfldextramax.setText(" ");
    		}
    		else
    			tfldextramax.setText(this.globeexpc.getCjezbGrowingRateMax().toString());
			break;
		}
	}
	private void refreshSecondBoardHightLights(JCheckBox ckbxhighlight)
	{
		String keyword = ckbxhighlight.getName() ;
		switch (keyword) {
		case "GuJiaCLOSE":
			Double pricemin = null; Double pricemax = null;
			if(ckbxhighlight.isSelected() ) {
				if(!Strings.isNullOrEmpty(tfldextramin.getText()  ) )  
					pricemin = Double.parseDouble(tfldextramin.getText()  );
				
				if(!Strings.isNullOrEmpty(tfldextramax.getText()  ) )  
					pricemax = Double.parseDouble(tfldextramax.getText()  );
			} 
			this.globeexpc.setSettingStockPriceLevel(pricemin,pricemax);
			break;
		case "ChenJiaoEr" :
			Double showcjemin = null; Double showcjemax = null;
			if(ckbxhighlight.isSelected()) {
				
				if( !Strings.isNullOrEmpty(tfldextramin.getText().trim()) ) 
					showcjemin =  Double.parseDouble( tfldextramin.getText()  );
				
				if( !Strings.isNullOrEmpty(tfldextramax.getText().trim()) ) 
					showcjemax = Double.parseDouble(  tfldextramax.getText() );
			}
			globeexpc.setSettingChenJiaoEr (showcjemin, showcjemax);
			break;
		case "LastWkDpcjezbGrowingRate":
			Double showcjegrmin = null; Double showcjegrmax = null;
			if(ckbxhighlight.isSelected()) {
				
				if( !Strings.isNullOrEmpty(tfldextramin.getText().trim()) && !tfldextramin.getText().trim().equalsIgnoreCase("MIN")  ) 
					showcjegrmin =  Double.parseDouble(tfldextramin.getText() );

				if( !Strings.isNullOrEmpty(tfldextramax.getText().trim()) && !tfldextramax.getText().trim().equalsIgnoreCase("MAX")  ) 
					showcjegrmax =  Double.parseDouble(tfldextramax.getText() );
			} 
			globeexpc.setCjezbGrowingRate (showcjegrmin, showcjegrmax);
			break;
		}
	}
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
	private JProgressBar getProgressBar () {
		for (Component tmpcomp : this.getComponents() ) {
            	if(tmpcomp instanceof JProgressBar)
            		return (JProgressBar)  tmpcomp;
        }
        return null;
	}
	/*
	 * 
	 */
	public void setCurrentDisplayDate (LocalDate date)
	{
		this.curselectdate = date;
	}
	public void setCurrentExportPeriod (String period)
	{
		this.globeperiod = period;
	}
	public void setStockInfoManager (StockInfoManager stkm)
	{
		this.stockmanager = stkm;
	}
	
	protected void initializeExportConditions (JLabel btnaddexportcond) 
	{
		if( exportcond == null)
			exportcond = new ArrayList<BanKuaiGeGuMatchCondition> ();
		
		try {
			BanKuaiGeGuMatchCondition expcCloned =  (BanKuaiGeGuMatchCondition) this.globeexpc.clone();
//			ExtraExportConditions extraexportcondition = new ExtraExportConditions (expcCloned);
			ExtraExportConditionsPnl extraexportcondition = new ExtraExportConditionsPnl (expcCloned,curselectdate);
			int extraresult = JOptionPane.showConfirmDialog(null,extraexportcondition , "附加导出条件:", JOptionPane.OK_CANCEL_OPTION);
			if(extraresult == JOptionPane.OK_OPTION) { //其他导出条件 
				
				expcCloned = extraexportcondition.getSettingCondition ();
				if( expcCloned.shouldExportOnlyCurrentBanKuai() ) {
//					expcCloned.setSettingBanKuai(exportbk);
				}
				exportcond.add(expcCloned);
				
				btnaddexportcond.setText(String.valueOf(exportcond.size() ));
				String tooltips = btnaddexportcond.getToolTipText() + "<html>" + expcCloned.getConditionsDescriptions() + "<br></html>";
				btnaddexportcond.setToolTipText(tooltips);
			}
		} catch (CloneNotSupportedException e) {e.printStackTrace();}

		return;
	}
	
	/*
	 * 把当前的板块当周符合条件的导出
	 */
	private void exportBanKuaiWithGeGuOnCondition ()
	{
		if(exportcond == null || exportcond.size() == 0) {
			JOptionPane.showMessageDialog(null,"未设置导出条件，请先设置导出条件！");
			return;
		}

		String msg =  "导出耗时较长，请先确认条件是否正确。\n是否导出？";
		int exchangeresult = JOptionPane.showConfirmDialog(null,msg , "确实导出？", JOptionPane.OK_CANCEL_OPTION);
		if(exchangeresult == JOptionPane.CANCEL_OPTION)
			return;

		if(curselectdate == null)
			curselectdate = LocalDate.now();
		String dateshowinfilename = null;
		if(globeperiod == null  || globeperiod.equals(NodeGivenPeriodDataItem.WEEK))
			dateshowinfilename = "week" + curselectdate.with(DayOfWeek.FRIDAY).toString().replaceAll("-","");
		else if(globeperiod.equals(NodeGivenPeriodDataItem.DAY))
			dateshowinfilename = "day" + curselectdate.toString().replaceAll("-","");
		else if(globeperiod.equals(NodeGivenPeriodDataItem.MONTH))
			dateshowinfilename = "month" +  curselectdate.withDayOfMonth(curselectdate.lengthOfMonth()).toString().replaceAll("-","");
		String exportfilename = sysconfig.getTDXModelMatchExportFile () + "TDX模型个股" + dateshowinfilename + ".EBK";
		File filefmxx = new File( exportfilename );
		if( !filefmxx.getParentFile().exists() ) {  
            //如果目标文件所在的目录不存在，则创建父目录  
            if(!filefmxx.getParentFile().mkdirs()) {  
                System.out.println("创建目标文件所在目录失败！");  
                return ;  
            }  
        }  
		try {
				if (filefmxx.exists()) {
					filefmxx.delete();
					filefmxx.createNewFile();
				} else
					filefmxx.createNewFile();
		} catch (Exception e) {		e.printStackTrace();return ;}
		
		if(this.stockmanager != null)
			this.stockmanager.setGetNodeDataFromDbWhenSystemIdleThreadStatus(false);
		if(globeperiod == null)
			globeperiod = NodeGivenPeriodDataItem.WEEK;
		
		exporttask = new ExportTask(exportcond, curselectdate,globeperiod,filefmxx);
		exporttask.addPropertyChangeListener(new PropertyChangeListener() {
		      @Override
		      public void propertyChange(final PropertyChangeEvent eventexport) 
		      {  
		    	  JProgressBar progressBarExport = getProgressBar ();
			      	switch (eventexport.getPropertyName()) {
			        case "progress":
			        	progressBarExport.setIndeterminate(false);
			        	progressBarExport.setString("正在导出..." + (Integer) eventexport.getNewValue() + "%(,点击取消导出)");
			        	progressBarExport.setToolTipText("点击取消导出");
			          break;
			        case "state":
			          switch ((StateValue) eventexport.getNewValue()) {
			          case DONE:
//			        	exportCancelAction.putValue(Action.NAME, "导出条件个股");
			            try {
			              final int count = exporttask.get();
			              int exchangeresult = JOptionPane.showConfirmDialog(null, "导出完成，是否打开" + filefmxx.getAbsolutePath() + "查看","导出完成", JOptionPane.OK_CANCEL_OPTION);
			      		  if(exchangeresult == JOptionPane.CANCEL_OPTION) {
			      			  progressBarExport.setString(" ");
			      			  return;
			      		  }
			      		  try {
			      			String path = filefmxx.getAbsolutePath();
			      			Runtime.getRuntime().exec("explorer.exe /select," + path);
			      		  } catch (IOException e1) {	e1.printStackTrace();}
			      		  
			      		  progressBarExport.setString(" ");
			      		  stockmanager.setGetNodeDataFromDbWhenSystemIdleThreadStatus(true);
			      		  System.gc();
			            } catch (final CancellationException e) {
			            	try {	exporttask.get();	} catch (InterruptedException | ExecutionException | CancellationException e1) {e1.printStackTrace();	}
			            	progressBarExport.setIndeterminate(false);
			            	progressBarExport.setValue(0);
			            	JOptionPane.showMessageDialog(null, "导出条件个股被终止！", "导出条件个股",JOptionPane.WARNING_MESSAGE);
			            	progressBarExport.setString("导出设置条件个股");
			            	stockmanager.setGetNodeDataFromDbWhenSystemIdleThreadStatus(true);
			            } catch (final Exception e) {  }
	
			            exporttask = null;
			            break;
			          case STARTED:
			          case PENDING:
//			        	  exportCancelAction.putValue(Action.NAME, "取消导出");
			        	  progressBarExport.setVisible(true);
			        	  progressBarExport.setIndeterminate(true);
			            break;
			          }
			          break;
			        }
			      }
		    });
		
		exporttask.execute();
//		try {
//			exporttask.get();
//		} catch (InterruptedException | ExecutionException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	/*
	 * 根据星期几已经本周几个交易日自动设置成交量的值
	 */
//	private void setHighLightChenJiaoEr() 
//	{
//		LocalDate curselectdate = dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//		if(LocalDate.now().with(DayOfWeek.FRIDAY).equals(curselectdate.with(DayOfWeek.FRIDAY) ) ) { //说明在当前周，按星期来设置
//			if(curselectdate.getDayOfWeek() == DayOfWeek.MONDAY )
//				tfldshowcje.setText("0");
//			else if(curselectdate.getDayOfWeek() == DayOfWeek.TUESDAY )
//				tfldshowcje.setText("1.2");
//			else if(curselectdate.getDayOfWeek() == DayOfWeek.WEDNESDAY )
//				tfldshowcje.setText("2.5");
//			else if(curselectdate.getDayOfWeek() == DayOfWeek.THURSDAY )
//				tfldshowcje.setText("3.7");
//			else if(curselectdate.getDayOfWeek() == DayOfWeek.FRIDAY )
//				tfldshowcje.setText("4.9");
//			else if(curselectdate.getDayOfWeek() == DayOfWeek.SATURDAY || curselectdate.getDayOfWeek() == DayOfWeek.SUNDAY)
//				tfldshowcje.setText("5.8");
//			
//		} else { //应该根据该周有多少个交易日来设置，而不是简单的按1.2*5 约=5.8
//			int tradingdays = this.bkdbopt.getTradingDaysOfTheWeek (curselectdate);
//			if(tradingdays < 5)
//				tfldshowcje.setText(String.valueOf(1.15*tradingdays).substring(0, 3) );
//			else
//				tfldshowcje.setText("5.8");
//		}
//		
//	}

}

/*
* 閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓缂寸尨鎷锋椂澶敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹涓閿熸枻鎷烽敓绔鎷蜂笓閿熻剼杈炬嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹GUI閿熺潾鍑ゆ嫹浣块敓鏂ゆ嫹
* http://www.javacreed.com/swing-worker-example/
* https://github.com/javacreed/swing-worker-example/blob/master/src/main/java/com/javacreed/examples/swing/worker/part3/Application.java
*/
class ExportTask extends SwingWorker<Integer, String>  
{
	private LocalDate selectiondate;
	private File outputfile;
	private List<BanKuaiGeGuMatchCondition> expclist;
	private String period;
	 
	
	public ExportTask (List<BanKuaiGeGuMatchCondition> exportcond,LocalDate selectiondate,String period,File outputfile)
	{
		this.expclist = exportcond;
		this.selectiondate = selectiondate;
		this.period = period;
		this.outputfile = outputfile;
	}
	/*
	 * Main task. Executed in background thread.
	 */
	@Override
	public Integer doInBackground() 
	{
		Charset charset = Charset.forName("GBK") ;
	
		ArrayList<TDXNodes> outputnodeslist = new ArrayList<TDXNodes> ();
		
		String ouputfilehead2 = "";
		for(BanKuaiGeGuMatchCondition expc : expclist) {
			if (isCancelled()) 
				 return null;
			
			setProgress(30);
			try{
				ExportMatchedNode2 exportaction = new ExportMatchedNode2 (expc);
				Set<TDXNodes> outputnodes = exportaction.checkTDXNodeMatchedCurSettingConditons(selectiondate, period);
				for(TDXNodes tmpnode : outputnodes) {
					if(!outputnodeslist.contains(tmpnode))
						outputnodeslist.add(tmpnode);
				}
				ouputfilehead2 = ouputfilehead2 + expc.getConditionsDescriptions ();
			} catch(Exception e) {	e.printStackTrace();}
			
			setProgress(50);
		}	
		
		//对导出的板块和个股按照周成交额进行排序，这样导入到通达信后能自动按照成交额排序
		Collections.sort(outputnodeslist, new NodeChenJiaoErComparator(selectiondate,0,period) );
		
		try {
			Files.append("<导出日期:" + selectiondate.toString() + ">"+ System.getProperty("line.separator") ,outputfile, charset);
			Files.append("<导出周期:" + period + ">"+ System.getProperty("line.separator") ,outputfile, charset);
			Files.append("<" + ouputfilehead2 + ">" + System.getProperty("line.separator") ,outputfile, charset);
		} catch (IOException e1) {			e1.printStackTrace();} 
		
		//写入板块分析文件
		for(BkChanYeLianTreeNode node : outputnodeslist) {
			 String outputstock;
			 String cjs = ((TDXNodes)node).getNodeJiBenMian().getSuoShuJiaoYiSuo();
			 if(cjs.trim().toLowerCase().equals("sh"))
				outputstock= "1" + node.getMyOwnCode().trim();
			else
				outputstock ="0" + node.getMyOwnCode().trim();
			
			 outputstock = outputstock +  System.getProperty("line.separator");
			 try {
					Files.append(outputstock,outputfile, charset);
				} catch (IOException e) {
					e.printStackTrace();
			}
		}
	
		//生产gephi文件
	//	GephiFilesGenerator gephigenerator = new GephiFilesGenerator (allbksks);
	//	gephigenerator.generatorGephiFile(outputfile, dateChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), globeperiod);
			
			setProgress( 90 );
			setProgress( 100 );
			
			outputnodeslist = null;
			outputfile = null;
	
			return 100;
	}
	
	/*
	 * Executed in event dispatching thread
	 */
	@Override
	public void done() 
	{
		try {
			int i = get();
		} catch (InterruptedException | ExecutionException | CancellationException e) {}
	  
		SystemAudioPlayed.playSound();
	}
	public void cancel ()
	{
		try {		int i = get ();
		} catch (InterruptedException | ExecutionException e) {			e.printStackTrace();}
	}
}