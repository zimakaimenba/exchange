package com.exchangeinfomanager.bankuaichanyelian;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.BanKuai;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import net.miginfocom.swing.MigLayout;


public class BanKuaiShuXingSheZhi extends JPanel {

	
	
	private JCheckBox cbxnotgephi;
	private JButton buttonapplybksetting;
	private JCheckBox cbxnotimport;
	private JCheckBox cbxnotbkfx;
	private JCheckBox cbxnotshowincyltree;
	private BkChanYeLianTreeNode settingnode;
	private BanKuaiDbOperation bkdbopt;
	

	/**
	 * Create the panel.
	 */
	public BanKuaiShuXingSheZhi(BkChanYeLianTreeNode node) 
	{
		this.settingnode = node;
		this.bkdbopt = new BanKuaiDbOperation ();
		
		initializeGui ();
		createEvetns ();
		
		setSettingNode (node);
	}
	
	
	public BanKuaiShuXingSheZhi() 
	{
		this.bkdbopt = new BanKuaiDbOperation ();
		
		initializeGui ();
		createEvetns ();
	}
	
	private void initializeGuiValue()
	{
		cbxnotimport.setSelected( !((BanKuai)settingnode).isImportdailytradingdata() );
		cbxnotgephi.setSelected( !((BanKuai)settingnode).isExporttogehpi() );
		cbxnotbkfx.setSelected( !((BanKuai)settingnode).isShowinbkfxgui() );
		cbxnotshowincyltree.setSelected( !((BanKuai)settingnode).isShowincyltree() );
	}


	public void setSettingNode (BkChanYeLianTreeNode node)
	{
		this.settingnode = node;
		
		if(settingnode.getType() == BkChanYeLianTreeNode.TDXBK) {
			
			cbxnotimport.setEnabled(true);
			cbxnotbkfx.setEnabled(true);
			cbxnotgephi.setEnabled(true);
			cbxnotshowincyltree.setEnabled(true);
			buttonapplybksetting.setEnabled(true);
			
			initializeGuiValue ();
						
			if(cbxnotimport.isSelected()) {
				cbxnotbkfx.setEnabled(false);
				cbxnotgephi.setEnabled(false);
//				cbxnotshowincyltree.setSelected(false);
			}
				
		} else { //���ɣ���������
			cbxnotimport.setSelected(false);
			cbxnotbkfx.setSelected(false);
			cbxnotgephi.setSelected(false);
			cbxnotshowincyltree.setSelected(false);
			buttonapplybksetting.setEnabled(false);
		}
		

	}
	private void createEvetns() {
		
		cbxnotimport.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if(cbxnotimport.isSelected()) {
					cbxnotbkfx.setSelected(true);
					cbxnotgephi.setSelected(true);
					cbxnotshowincyltree.setSelected(true);
					
					cbxnotbkfx.setEnabled(false);
					cbxnotgephi.setEnabled(false);
					cbxnotshowincyltree.setSelected(false);
				} else {
					cbxnotbkfx.setEnabled(true);
					cbxnotgephi.setEnabled(true);
					cbxnotshowincyltree.setSelected(false);
					
					buttonapplybksetting.setEnabled(true);
				}
				
			}
		});
		
		buttonapplybksetting.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				if(settingnode.getType() == BkChanYeLianTreeNode.TDXBK) {
					
					bkdbopt.updateBanKuaiExportGephiBkfxOperation (settingnode.getMyOwnCode(),!cbxnotimport.isSelected(),
																!cbxnotgephi.isSelected(),!cbxnotbkfx.isSelected(),
																!cbxnotshowincyltree.isSelected());
					
					((BanKuai)settingnode).setImportdailytradingdata(!cbxnotimport.isSelected());
					((BanKuai)settingnode).setExporttogehpi(!cbxnotgephi.isSelected());
					((BanKuai)settingnode).setShowinbkfxgui(!cbxnotbkfx.isSelected());
					((BanKuai)settingnode).setShowincyltree(!cbxnotshowincyltree.isSelected());
					
					buttonapplybksetting.setEnabled(false);
				}
			}
		});
		
	}

	private void initializeGui() {
		// TODO Auto-generated method stub
		this.setBorder(new TitledBorder(new CompoundBorder(new BevelBorder(BevelBorder.RAISED, new Color(240, 240, 240), new Color(255, 255, 255), new Color(105, 105, 105), new Color(160, 160, 160)), new LineBorder(new Color(180, 180, 180), 5)), "\u677F\u5757\u5C5E\u6027\u8BBE\u7F6E", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		
		cbxnotimport = new JCheckBox("\u4E0D\u5BFC\u5165\u6BCF\u65E5\u4EA4\u6613\u6570\u636E");
		setLayout(new MigLayout("", "[133px][]", "[23px][23px][23px][][]"));
		cbxnotshowincyltree =  new JCheckBox("�������ڲ�ҵ����");
		add(cbxnotimport, "cell 0 0,alignx left,aligny top");
		
		cbxnotbkfx = new JCheckBox("\u4E0D\u505A\u677F\u5757\u5206\u6790");
		add(cbxnotbkfx, "cell 0 1,alignx left,aligny top");
		
		cbxnotgephi = new JCheckBox("\u4E0D\u5BFC\u51FA\u5230Gephi");
		add(cbxnotgephi, "cell 0 2,alignx left,aligny top");
		add(cbxnotshowincyltree, "cell 0 3,alignx left,aligny top");
		
		buttonapplybksetting = new JButton("\u5E94\u7528");
		add(buttonapplybksetting, "cell 0 4");
	}
}