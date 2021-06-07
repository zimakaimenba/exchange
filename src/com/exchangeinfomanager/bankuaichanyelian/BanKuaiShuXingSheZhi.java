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

import com.exchangeinfomanager.News.Labels.ColorChooser;
import com.exchangeinfomanager.NodesServices.ServicesForNodeBanKuai;
import com.exchangeinfomanager.database.BanKuaiDZHDbOperation;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.guifactory.JLabelFactory;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.google.common.collect.Multimap;
import com.exchangeinfomanager.nodes.BanKuai;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import net.miginfocom.swing.MigLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.Font;


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
	protected ColorChooser colorChooser = new ColorChooser();
	private JLabel colorButton;

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
		cbxnotimport.setSelected( ! ((BanKuai)settingnode).getBanKuaiOperationSetting().isImportdailytradingdata() );
		cbxnotgephi.setSelected( ! ((BanKuai)settingnode).getBanKuaiOperationSetting().isExporttogehpi() );
		cbxnotbkfx.setSelected( ! ((BanKuai)settingnode).getBanKuaiOperationSetting().isShowinbkfxgui() );
		cbxnotshowincyltree.setSelected( ! ((BanKuai)settingnode).getBanKuaiOperationSetting().isShowincyltree() );
		chkbxnotexportwklyfile.setSelected( ! ((BanKuai)settingnode).getBanKuaiOperationSetting().isExportTowWlyFile() );
		chbxnotimportgegu.setSelected( ! ((BanKuai)settingnode).getBanKuaiOperationSetting().isImportBKGeGu() );
		
		Color bg = ((BanKuai)settingnode).getBanKuaiOperationSetting().getBanKuaiLabelColor() ;
		if(bg != null)	colorButton.setBackground(bg);
		else	colorButton.setBackground(Color.BLUE );
		
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
						
//			if(cbxnotimport.isSelected()) {
//				cbxnotbkfx.setEnabled(false);
//				cbxnotgephi.setEnabled(false);
//				cbxnotshowincyltree.setEnabled(false);
//				chkbxnotexportwklyfile.setEnabled(false);
//			}
				
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
					!chbxnotimportgegu.isSelected(),
					colorButton.getBackground()
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
		cbxnotshowincyltree =  new JCheckBox("不出现在产业链树");
		cbxnotshowincyltree.setFont(new Font("宋体", Font.PLAIN, 11));
		
		chbxnotimportgegu = new JCheckBox("\u4E0D\u5BFC\u5165\u677F\u5757\u4E2A\u80A1");
		chbxnotimportgegu.setFont(new Font("宋体", Font.PLAIN, 11));
		
		cbxnotimport = new JCheckBox("\u4E0D\u5BFC\u5165\u6BCF\u65E5\u4EA4\u6613\u6570\u636E");
		cbxnotimport.setFont(new Font("宋体", Font.PLAIN, 11));
		
		cbxnotbkfx = new JCheckBox("\u4E0D\u505A\u677F\u5757\u5206\u6790");
		cbxnotbkfx.setFont(new Font("宋体", Font.PLAIN, 11));
		
		cbxnotgephi = new JCheckBox("\u4E0D\u5BFC\u51FA\u5230Gephi");
		cbxnotgephi.setFont(new Font("宋体", Font.PLAIN, 11));
		
		chkbxnotexportwklyfile = new JCheckBox("\u4E0D\u5BFC\u51FA\u5230\u6BCF\u5468\u5206\u6790\u6587\u4EF6");
		chkbxnotexportwklyfile.setFont(new Font("宋体", Font.PLAIN, 11));
		
		colorButton = JLabelFactory.createLabel("", 40, 50);
		colorButton.setBackground(Color.BLUE);
        colorButton.addMouseListener(new ColorChooserController());
        
		buttonapplybksetting = new JButton("\u5E94\u7528");
		
		JLabel lblbkcolor = new JLabel("\u677F\u5757\u663E\u793A\u989C\u8272");
		lblbkcolor.setFont(new Font("宋体", Font.PLAIN, 11));
		setLayout(new MigLayout("", "[145px][48px]", "[23px][23px][23px][23px][23px][23px][15px][1px][23px]"));
		add(chbxnotimportgegu, "cell 0 0,alignx left,aligny top");
		add(cbxnotimport, "cell 0 1,alignx left,aligny top");
		add(cbxnotbkfx, "cell 0 2,alignx left,aligny top");
		add(cbxnotgephi, "cell 0 3,alignx left,aligny top");
		add(cbxnotshowincyltree, "cell 0 4,alignx left,aligny top");
		add(chkbxnotexportwklyfile, "cell 0 5,alignx left,aligny top");
		add(colorButton, "cell 1 6,alignx left,aligny top");
		add(buttonapplybksetting, "cell 0 8,alignx left,aligny top");
		add(lblbkcolor, "cell 0 6,alignx left,aligny top");
	}
	
	private class ColorChooserController extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            colorChooser.setVisible(true);
            colorButton.setBackground(colorChooser.getColor());
        }
    }
}
