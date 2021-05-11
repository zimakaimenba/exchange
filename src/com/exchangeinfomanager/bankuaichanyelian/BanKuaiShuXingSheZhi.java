package com.exchangeinfomanager.bankuaichanyelian;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.exchangeinfomanager.database.BanKuaiDZHDbOperation;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.ServicesForNodeBanKuai;
import com.google.common.collect.Multimap;
import com.exchangeinfomanager.nodes.BanKuai;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import net.miginfocom.swing.MigLayout;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;


public class BanKuaiShuXingSheZhi extends JPanel 
{
	private JCheckBox cbxnotgephi;
	private JButton buttonapplybksetting;
	private JCheckBox cbxnotimport;
	private JCheckBox cbxnotbkfx;
	private JCheckBox cbxnotshowincyltree;
	private BkChanYeLianTreeNode settingnode;
//	private BanKuaiDbOperation bkdbopt;
	private JCheckBox chkbxnotexportwklyfile;
	private JCheckBox chbxnotimportgegu;
	

	/**
	 * Create the panel.
	 */
	public BanKuaiShuXingSheZhi(BkChanYeLianTreeNode node) 
	{
		this.settingnode = node;
		
		initializeGui ();
		createEvetns ();
		
		setSettingNode (node);
	}
	
	
	public BanKuaiShuXingSheZhi() 
	{
		initializeGui ();
		createEvetns ();
	}
	
	private void initializeGuiValue()
	{
		cbxnotimport.setSelected( ! ((BanKuai)settingnode).isImportdailytradingdata() );
		cbxnotgephi.setSelected( ! ((BanKuai)settingnode).isExporttogehpi() );
		cbxnotbkfx.setSelected( ! ((BanKuai)settingnode).isShowinbkfxgui() );
		cbxnotshowincyltree.setSelected( ! ((BanKuai)settingnode).isShowincyltree() );
		chkbxnotexportwklyfile.setSelected( ! ((BanKuai)settingnode).isExportTowWlyFile() );
		chbxnotimportgegu.setSelected( ! ((BanKuai)settingnode).isImportBKGeGu() );
		
		if( ((BanKuai)settingnode).getBanKuaiLeiXing().equals(BanKuai.NOGGNOSELFCJL)) { //没有个股没有成交量的板块肯定不做板块分析等动作
			cbxnotimport.setSelected(true);
			cbxnotgephi.setSelected(true);
			cbxnotbkfx.setSelected(true);
			cbxnotshowincyltree.setSelected(true);
			chkbxnotexportwklyfile.setSelected(true);
		}
	}


	public void setSettingNode (BkChanYeLianTreeNode node)
	{
		this.settingnode = node;
		
		if(settingnode.getType() == BkChanYeLianTreeNode.TDXBK || settingnode.getType() == BkChanYeLianTreeNode.DZHBK) {
			
			cbxnotimport.setEnabled(true);
			cbxnotbkfx.setEnabled(true);
			cbxnotgephi.setEnabled(true);
			cbxnotshowincyltree.setEnabled(true);
			buttonapplybksetting.setEnabled(true);
			chkbxnotexportwklyfile.setEnabled(true);
			
			initializeGuiValue ();
						
			if(cbxnotimport.isSelected()) {
				cbxnotbkfx.setEnabled(false);
				cbxnotgephi.setEnabled(false);
				cbxnotshowincyltree.setEnabled(false);
				chkbxnotexportwklyfile.setEnabled(false);
			}
				
		} else { //个股，不能设置
			cbxnotimport.setSelected(false);
			cbxnotbkfx.setSelected(false);
			cbxnotgephi.setSelected(false);
			cbxnotshowincyltree.setSelected(false);
			buttonapplybksetting.setEnabled(false);
			chkbxnotexportwklyfile.setEnabled(false);
		}
		

	}
	private void createEvetns() 
	{
		chkbxnotexportwklyfile.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				buttonapplybksetting.setEnabled(true);
			}
		});
		cbxnotbkfx.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				buttonapplybksetting.setEnabled(true);
			}
		});
		cbxnotgephi.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				buttonapplybksetting.setEnabled(true);
			}
		});
		cbxnotshowincyltree.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				buttonapplybksetting.setEnabled(true);
			}
		});
		
		cbxnotimport.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				
				if(cbxnotimport.isSelected()) {
					
					cbxnotbkfx.setSelected(true);
					cbxnotgephi.setSelected(true);
					cbxnotshowincyltree.setSelected(true);
					chkbxnotexportwklyfile.setSelected(true);
					chbxnotimportgegu.setSelected(true);
					
					cbxnotbkfx.setEnabled(false);
					cbxnotgephi.setEnabled(false);
					cbxnotshowincyltree.setEnabled(false);
					chkbxnotexportwklyfile.setEnabled(false);
					chbxnotimportgegu.setEnabled(false);
				} else {
					cbxnotbkfx.setEnabled(true);
					cbxnotgephi.setEnabled(true);
					cbxnotshowincyltree.setEnabled(true);
					chkbxnotexportwklyfile.setEnabled(true);
					chbxnotimportgegu.setEnabled(true);
					buttonapplybksetting.setEnabled(true);
				}
			}
		});
		
		buttonapplybksetting.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
					applaySetttingToDb ();

			}
		});
		
	}

	protected void applaySetttingToDb() 
	{
		
		ServicesForNodeBanKuai svsbk = ((BanKuai)settingnode).getBanKuaiService (true);
		svsbk.updateBanKuaiBasicOperationsSettings(settingnode,!cbxnotimport.isSelected(),
					!cbxnotgephi.isSelected(),!cbxnotbkfx.isSelected(),
					!cbxnotshowincyltree.isSelected(),
					!chkbxnotexportwklyfile.isSelected(),
					!chbxnotimportgegu.isSelected()
					);
		((BanKuai)settingnode).getBanKuaiService (false);
//		((BanKuai)settingnode).setImportdailytradingdata(!cbxnotimport.isSelected());
//		((BanKuai)settingnode).setExporttogehpi(!cbxnotgephi.isSelected());
//		((BanKuai)settingnode).setShowinbkfxgui(!cbxnotbkfx.isSelected());
//		((BanKuai)settingnode).setShowincyltree(!cbxnotshowincyltree.isSelected());
//		((BanKuai)settingnode).setExportTowWlyFile(!chkbxnotexportwklyfile.isSelected());
//		((BanKuai)settingnode).setImportBKGeGu(!chbxnotimportgegu.isSelected());
		 
		buttonapplybksetting.setEnabled(false);
		
	}


	private void initializeGui() {
		// TODO Auto-generated method stub
		this.setBorder(new TitledBorder(new CompoundBorder(new BevelBorder(BevelBorder.RAISED, new Color(240, 240, 240), new Color(255, 255, 255), new Color(105, 105, 105), new Color(160, 160, 160)), new LineBorder(new Color(180, 180, 180), 5)), "\u677F\u5757\u5C5E\u6027\u8BBE\u7F6E", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		
		cbxnotimport = new JCheckBox("\u4E0D\u5BFC\u5165\u6BCF\u65E5\u4EA4\u6613\u6570\u636E");
		setLayout(new MigLayout("", "[133px,grow][]", "[23px][][23px][23px][][][][]"));
		cbxnotshowincyltree =  new JCheckBox("不出现在产业链树");
		add(cbxnotimport, "cell 0 0,alignx left,aligny top");
		
		chbxnotimportgegu = new JCheckBox("\u4E0D\u5BFC\u5165\u677F\u5757\u4E2A\u80A1");
		add(chbxnotimportgegu, "cell 0 1");
		
		cbxnotbkfx = new JCheckBox("\u4E0D\u505A\u677F\u5757\u5206\u6790");
		add(cbxnotbkfx, "cell 0 2,alignx left,aligny top");
		
		cbxnotgephi = new JCheckBox("\u4E0D\u5BFC\u51FA\u5230Gephi");
		add(cbxnotgephi, "cell 0 3,alignx left,aligny top");
		add(cbxnotshowincyltree, "cell 0 4,alignx left,aligny top");
		
		chkbxnotexportwklyfile = new JCheckBox("\u4E0D\u5BFC\u51FA\u5230\u6BCF\u5468\u5206\u6790\u6587\u4EF6");
		
		
		add(chkbxnotexportwklyfile, "cell 0 5");
		
		buttonapplybksetting = new JButton("\u5E94\u7528");
		add(buttonapplybksetting, "cell 0 6");
	}
}
