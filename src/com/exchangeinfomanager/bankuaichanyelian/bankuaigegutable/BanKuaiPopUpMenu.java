package com.exchangeinfomanager.gui.subgui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.bankuaichanyelian.BkChanYeLianTree;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiInfoTable;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiInfoTableModel;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.ChanYeLianNewsPanel;
import com.exchangeinfomanager.database.BanKuaiDbOperation;

public class BanKuaiPopUpMenu extends JPopupMenu
{

	private BkChanYeLianTree cyltree;
	private BanKuaiInfoTable bankuaitable;
	private JMenuItem menuItemAddNews;
	private JMenuItem menuItemAddToGz;
	private JMenuItem menuItemMakeLongTou;
	private BanKuaiDbOperation bkdbopt;

	public BanKuaiPopUpMenu(BkChanYeLianTree treechanyelian) 
	{
		this.cyltree = treechanyelian;
		this.bkdbopt = new BanKuaiDbOperation ();
		createMenuItems ();
		createEventsForTree ();
	}
	

	public BanKuaiPopUpMenu(BanKuaiInfoTable bktable) 
	{
		this.bankuaitable = bktable;
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
				String bkcode = null;
				try {
					TreePath closestPath = cyltree.getSelectionPath();
					BkChanYeLianTreeNode selectednode = (BkChanYeLianTreeNode)closestPath.getLastPathComponent();
					 bkcode = selectednode.getMyOwnCode();
				} catch (java.lang.NullPointerException ex) {
					JOptionPane.showMessageDialog(null,"��ѡ���ҵ��飡","Warning",JOptionPane.WARNING_MESSAGE);
				}

				addZdgz (bkcode);
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
		JiaRuJiHua jiarujihua = new JiaRuJiHua ( bkcode,"�����ע" ); 
		int exchangeresult = JOptionPane.showConfirmDialog(null, jiarujihua, "�����ע", JOptionPane.OK_CANCEL_OPTION);
		if(exchangeresult == JOptionPane.CANCEL_OPTION)
			return;
		
		int autoIncKeyFromApi =	bkdbopt.setZdgzRelatedActions (jiarujihua);
		
	}

	protected void addBanKuaiReDian(String bkcode)
	{
		JiaRuJiHua jiarujihua = new JiaRuJiHua ( bkcode,"�����ȵ�" ); 
		int exchangeresult = JOptionPane.showConfirmDialog(null, jiarujihua, "�����ȵ�", JOptionPane.OK_CANCEL_OPTION);
		if(exchangeresult == JOptionPane.CANCEL_OPTION)
			return;
		
		int autoIncKeyFromApi =	bkdbopt.setZdgzRelatedActions (jiarujihua);
	}

	protected void addBanKuaiNews(String bkcode) 
	{
		ChanYeLianNewsPanel cylnews = new ChanYeLianNewsPanel (bkcode);
		int exchangeresult = JOptionPane.showConfirmDialog(null, cylnews, "���Ӱ������", JOptionPane.OK_CANCEL_OPTION);

		if(exchangeresult == JOptionPane.CANCEL_OPTION)
			return;
		
		bkdbopt.addBanKuaiNews(bkcode, cylnews.getInputedNews());
	}

	private void createMenuItems() 
	{
		menuItemAddNews = new JMenuItem("�������");
		menuItemAddToGz = new JMenuItem("��ʼ��ע");
		menuItemMakeLongTou = new JMenuItem("���Ϊ�����ȵ�");
		
		this.add(menuItemAddNews);
		this.add(menuItemMakeLongTou);
		this.add(menuItemAddToGz);
	}

}
