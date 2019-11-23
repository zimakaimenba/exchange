package com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;

import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;

import com.exchangeinfomanager.bankuaichanyelian.BanKuaiGuanLi;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.InsertedMeeting;
import com.exchangeinfomanager.bankuaifengxi.ai.WeeklyFenXiWizard;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.StockCalendarAndNewDbOperation;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

public abstract class BanKuaiPopUpMenu extends JPopupMenu
{
	protected JMenuItem menuItemAddNews;
	protected JMenuItem menuItemAddToGz;
	protected JMenuItem menuItemSetting;
	protected JMenuItem menuItemBkInfo;
	protected JMenuItem menuItemAddBkGJRQ;
	
//	private JMenuItem menuItemMakeLongTou;
	protected BanKuaiDbOperation bkdbopt;
//	private BanKuaiAndChanYeLian bkcyl;
	protected StockCalendarAndNewDbOperation newsdbopt;
	

//	protected JMenuItem menuItemQiangShi;
//	protected JMenuItem menuItemRuoShi;

	//��Ϊ�����2�ֱ�����ʽ�����ͱ���������2����ʽ��������
	public BanKuaiPopUpMenu() 
	{
		this.bkdbopt = new BanKuaiDbOperation ();
		this.newsdbopt = new StockCalendarAndNewDbOperation ();
		createMenuItems ();

		createEvents ();
	}
	public JPopupMenu getPopupMenu ()
	{
		return this;
	}
	private void createMenuItems() 
	{
		menuItemAddNews = new JMenuItem("�������");
		menuItemAddToGz = new JMenuItem("������");
		menuItemSetting = new JMenuItem("�������");
		menuItemBkInfo = new JMenuItem("�����Ϣ");
		menuItemAddBkGJRQ = new JMenuItem("��ӹؼ�����");
		
		this.add(menuItemBkInfo);
		this.add(menuItemSetting);
		this.add(menuItemAddToGz);
		this.add(menuItemAddNews);
		this.add(menuItemAddBkGJRQ);
//		this.add(menuItemRuoShi);
		
	}
	/*
	 * 
	 */
	abstract protected void createEvents ();
	/*
	 * 
	 */
	abstract public void addBanKuaiNews (BkChanYeLianTreeNode node);
	/*
	 * 
	 */
	protected void addGuanZhu(BkChanYeLianTreeNode node,LocalDate fxdate) 
	{
		WeeklyFenXiWizard ggfx = new WeeklyFenXiWizard ( node,fxdate);
    	ggfx.setSize(new Dimension(1400, 800));
    	ggfx.setModalityType(Dialog.ModalityType.APPLICATION_MODAL); // prevent user from doing something else
    	ggfx.setLocationRelativeTo(null);
    	if(!ggfx.isVisible() ) 
    		ggfx.setVisible(true);
    	ggfx.toFront();
    	
    	ggfx = null;
	}

}
