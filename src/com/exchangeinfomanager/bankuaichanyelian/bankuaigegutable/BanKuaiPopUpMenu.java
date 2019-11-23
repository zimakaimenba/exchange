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

	//因为板块有2种表现形式，树和表，所以争对2个形式做了两套
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
		menuItemAddNews = new JMenuItem("添加新闻");
		menuItemAddToGz = new JMenuItem("板块分析");
		menuItemSetting = new JMenuItem("板块设置");
		menuItemBkInfo = new JMenuItem("板块信息");
		menuItemAddBkGJRQ = new JMenuItem("添加关键日期");
		
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
