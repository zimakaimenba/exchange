package com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockTree;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.bankuaichanyelian.BanKuaiGuanLi;
import com.exchangeinfomanager.bankuaichanyelian.BanKuaiShuXingSheZhi;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.ChanYeLianNewsPanel;
import com.exchangeinfomanager.bankuaifengxi.ai.WeeklyFenXiWizard;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.StockCalendarAndNewDbOperation;
import com.exchangeinfomanager.gui.StockInfoManager;

public class BanKuaiPopUpMenuForTree extends BanKuaiPopUpMenu
{

	private BanKuaiAndStockTree cyltree;
	private BanKuaiInfoTable bankuaitable;
	
//	private BanKuaiAndChanYeLian bkcyl;
	private BanKuaiGuanLi bkgldialog = null;
//	private StockInfoManager stockmanager;
	

	//因为板块有2种表现形式，树和表，所以争对2个形式做了两套
	public BanKuaiPopUpMenuForTree (BanKuaiAndStockTree bkcyltree2) 
	{
		super ();
		
		this.cyltree = bkcyltree2;
//		this.bkcyl = bkcyl2;
	}
	
	@Override
	protected void createEvents() 
	{
		menuItemAddToGz.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				try {
					TreePath closestPath = cyltree.getSelectionPath();
					BkChanYeLianTreeNode selectednode = (BkChanYeLianTreeNode)closestPath.getLastPathComponent();
					
					 
				} catch (java.lang.NullPointerException ex) {
					JOptionPane.showMessageDialog(null,"请选择产业板块！","Warning",JOptionPane.WARNING_MESSAGE);
				}
				
				
			}
		});
		
		menuItemSetting.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				try {
					TreePath closestPath = cyltree.getSelectionPath();
					BanKuai selectednode = (BanKuai)closestPath.getLastPathComponent();
					
					BanKuaiShuXingSheZhi  bksetting = new BanKuaiShuXingSheZhi (selectednode);
					JOptionPane.showMessageDialog(null, bksetting, selectednode.getMyOwnCode()+selectednode.getMyOwnName()+ "板块设置", JOptionPane.OK_CANCEL_OPTION);
					bksetting = null;
					 
				} catch (java.lang.NullPointerException ex) {
					JOptionPane.showMessageDialog(null,"请选择产业板块！","Warning",JOptionPane.WARNING_MESSAGE);
				}
				
				
			}
		});
		
		menuItemAddNews.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				try {
					TreePath closestPath = cyltree.getSelectionPath();
					BkChanYeLianTreeNode selectednode = (BkChanYeLianTreeNode)closestPath.getLastPathComponent();
					addBanKuaiNews (selectednode);
					 
				} catch (java.lang.NullPointerException ex) {
					JOptionPane.showMessageDialog(null,"请选择产业板块！","Warning",JOptionPane.WARNING_MESSAGE);
				}
				
				
			}
		});
		
//		menuItemMakeLongTou.addActionListener(new ActionListener() {
//			@Override
//
//			public void actionPerformed(ActionEvent evt) {
//				String bkcode = null;
//				try {
//					TreePath closestPath = cyltree.getSelectionPath();
//					BkChanYeLianTreeNode selectednode = (BkChanYeLianTreeNode)closestPath.getLastPathComponent();
//					 bkcode = selectednode.getMyOwnCode();
//				} catch (java.lang.NullPointerException ex) {
//					JOptionPane.showMessageDialog(null,"请选择产业板块！","Warning",JOptionPane.WARNING_MESSAGE);
//				}
//				
//				addBanKuaiReDian (bkcode);
//			}
//			
//		});
		
		menuItemAddToGz.addActionListener(new ActionListener() {
			@Override

			public void actionPerformed(ActionEvent evt) {
				
				try {
					TreePath closestPath = cyltree.getSelectionPath();
					BkChanYeLianTreeNode selectednode = (BkChanYeLianTreeNode)closestPath.getLastPathComponent();
					LocalDate fxdate = cyltree.getCurrentDisplayedWk ();
					addGuanZhu(selectednode, fxdate);
				} catch (java.lang.NullPointerException ex) {
					JOptionPane.showMessageDialog(null,"请选择产业板块！","Warning",JOptionPane.WARNING_MESSAGE);
				}
			}
			
		});

	}


	@Override
	public void addBanKuaiNews(BkChanYeLianTreeNode node) 
	{
		ChanYeLianNewsPanel cylnews = new ChanYeLianNewsPanel (node.getMyOwnCode());
		cylnews.setVisible(true);
		
	}
}
