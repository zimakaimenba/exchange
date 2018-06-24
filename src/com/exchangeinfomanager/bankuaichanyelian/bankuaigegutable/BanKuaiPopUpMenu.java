package com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTree;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.bankuaichanyelian.BanKuaiAndChanYeLian;
import com.exchangeinfomanager.bankuaichanyelian.BanKuaiGuanLi;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.ChanYeLianNewsPanel;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.InsertedMeeting;
import com.exchangeinfomanager.bankuaifengxi.ai.JiaRuJiHua;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.StockCalendarAndNewDbOperation;
import com.exchangeinfomanager.gui.StockInfoManager;

public class BanKuaiPopUpMenu extends JPopupMenu
{

	private BkChanYeLianTree cyltree;
	private BanKuaiInfoTable bankuaitable;
	private JMenuItem menuItemAddNews;
	private JMenuItem menuItemAddToGz;
	private JMenuItem menuItemMakeLongTou;
	private BanKuaiDbOperation bkdbopt;
//	private BanKuaiAndChanYeLian bkcyl;
	private BanKuaiGuanLi bkgldialog = null;
	private StockInfoManager stockmanager;
	private StockCalendarAndNewDbOperation newsdbopt;

	public BanKuaiPopUpMenu(StockInfoManager stockmanager1,BkChanYeLianTree bkcyltree2) 
	{
		this.cyltree = bkcyltree2;
//		this.bkcyl = bkcyl2;
		this.stockmanager = stockmanager1;
		this.bkdbopt = new BanKuaiDbOperation ();
		this.newsdbopt = new StockCalendarAndNewDbOperation ();
		createMenuItems ();
		menuItemAddToGz.setEnabled(false);
		createEventsForTree ();
	}
	

	public BanKuaiPopUpMenu(BanKuaiInfoTable bktable,StockInfoManager stockmanager1) 
	{
		this.bankuaitable = bktable;
		this.stockmanager = stockmanager1;
//		this.bkcyl = bkcyl2;
		this.bkdbopt = new BanKuaiDbOperation ();
		createMenuItems ();
		createEventsForTable ();
	}

	private void createEventsForTree() 
	{
		menuItemAddNews.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt)
			{
				String bkcode = null;
				try {
					TreePath closestPath = cyltree.getSelectionPath();
					BkChanYeLianTreeNode selectednode = (BkChanYeLianTreeNode)closestPath.getLastPathComponent();
					 bkcode = selectednode.getMyOwnCode();
				} catch (java.lang.NullPointerException ex) {
					JOptionPane.showMessageDialog(null,"��ѡ���ҵ��飡","Warning",JOptionPane.WARNING_MESSAGE);
				}
				
				addBanKuaiNews (bkcode);
			}
		});
		
		menuItemMakeLongTou.addActionListener(new ActionListener() {
			@Override

			public void actionPerformed(ActionEvent evt) {
				String bkcode = null;
				try {
					TreePath closestPath = cyltree.getSelectionPath();
					BkChanYeLianTreeNode selectednode = (BkChanYeLianTreeNode)closestPath.getLastPathComponent();
					 bkcode = selectednode.getMyOwnCode();
				} catch (java.lang.NullPointerException ex) {
					JOptionPane.showMessageDialog(null,"��ѡ���ҵ��飡","Warning",JOptionPane.WARNING_MESSAGE);
				}
				
				addBanKuaiReDian (bkcode);
			}
			
		});
		
		menuItemAddToGz.addActionListener(new ActionListener() {
			@Override

			public void actionPerformed(ActionEvent evt) {
//				String bkcode = null;
//				try {
//					TreePath closestPath = cyltree.getSelectionPath();
//					BkChanYeLianTreeNode selectednode = (BkChanYeLianTreeNode)closestPath.getLastPathComponent();
//					 bkcode = selectednode.getMyOwnCode();
//				} catch (java.lang.NullPointerException ex) {
//					JOptionPane.showMessageDialog(null,"��ѡ���ҵ��飡","Warning",JOptionPane.WARNING_MESSAGE);
//				}
//
//				addZdgz (bkcode);
			}
			
		});
		
	}
	
	private void createEventsForTable() 
	{
		menuItemAddNews.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				int row = bankuaitable.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"��ѡ��һ�����","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				int modelRow = bankuaitable.convertRowIndexToModel(row); 
				BanKuai bankuai = ((BanKuaiInfoTableModel) bankuaitable.getModel()).getBanKuai(modelRow);
				String bkcode = bankuai.getMyOwnCode();

				addBanKuaiNews (bkcode);
			}
			
		});
		
		menuItemMakeLongTou.addActionListener(new ActionListener() {
			@Override

			public void actionPerformed(ActionEvent evt) {
				int row = bankuaitable.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"��ѡ��һ�����","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				int modelRow = bankuaitable.convertRowIndexToModel(row); 
				BanKuai bankuai = ((BanKuaiInfoTableModel) bankuaitable.getModel()).getBanKuai(modelRow);
				String bkcode = bankuai.getMyOwnCode();
				
				addBanKuaiReDian (bkcode);
			}
			
		});
		
		menuItemAddToGz.addActionListener(new ActionListener() {
			@Override

			public void actionPerformed(ActionEvent evt) {
				int row = bankuaitable.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"��ѡ��һ�����","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				int modelRow = bankuaitable.convertRowIndexToModel(row); 
				BanKuai bankuai = ((BanKuaiInfoTableModel) bankuaitable.getModel()).getBanKuai(modelRow);
				String bkcode = bankuai.getMyOwnCode();

				addZdgz (bkcode);
			}
			
		});
		
	}
	
	protected void addZdgz(String bkcode) 
	{
//		JiaRuJiHua jiarujihua = new JiaRuJiHua ( bkcode,"�����ע" ); 
//		int exchangeresult = JOptionPane.showConfirmDialog(null, jiarujihua, "�����ע", JOptionPane.OK_CANCEL_OPTION);
//		if(exchangeresult == JOptionPane.CANCEL_OPTION)
//			return;
//		
//		int autoIncKeyFromApi =	bkdbopt.setZdgzRelatedActions (jiarujihua);
		BanKuaiAndChanYeLian bkcyltst = new BanKuaiAndChanYeLian (this.stockmanager);
		bkcyltst.findBanKuaiInTree(bkcode );
		int exchangeresult = JOptionPane.showConfirmDialog(null, bkcyltst, "�����ע", JOptionPane.OK_CANCEL_OPTION);
	}



		



	protected void addBanKuaiReDian(String bkcode)
	{
		JiaRuJiHua jiarujihua = new JiaRuJiHua ( bkcode,"�ȵ���" ); 
		int exchangeresult = JOptionPane.showConfirmDialog(null, jiarujihua, "�ȵ���", JOptionPane.OK_CANCEL_OPTION);
		if(exchangeresult == JOptionPane.CANCEL_OPTION)
			return;
		
//		int autoIncKeyFromApi =	bkdbopt.setZdgzRelatedActions (jiarujihua);
		InsertedMeeting insetmeeting = newsdbopt.setReDianBanKuaiLongTouGeGuToShangYeXinWen(jiarujihua);
	}

	protected void addBanKuaiNews(String bkcode) 
	{
		ChanYeLianNewsPanel cylnews = new ChanYeLianNewsPanel (bkcode);
		cylnews.setVisible(true);
//		int exchangeresult = JOptionPane.showConfirmDialog(null, cylnews, "���Ӱ������", JOptionPane.OK_CANCEL_OPTION);
//
//		if(exchangeresult == JOptionPane.CANCEL_OPTION)
//			return;
		
//		bkdbopt.addBanKuaiNews(bkcode, cylnews.getInputedNews());
	}

	private void createMenuItems() 
	{
		menuItemAddNews = new JMenuItem("�������");
		menuItemAddToGz = new JMenuItem("��ʼ��ע");
		menuItemMakeLongTou = new JMenuItem("����ȵ���");
		
		this.add(menuItemAddNews);
		this.add(menuItemMakeLongTou);
		this.add(menuItemAddToGz);
	}

}
