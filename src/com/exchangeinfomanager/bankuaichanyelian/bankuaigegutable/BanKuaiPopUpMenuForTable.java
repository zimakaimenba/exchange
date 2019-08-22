package com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.HashSet;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.tree.TreePath;

import com.exchangeinfomanager.bankuaichanyelian.BanKuaiGuanLi;
import com.exchangeinfomanager.bankuaichanyelian.BanKuaiShuXingSheZhi;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.ChanYeLianNewsPanel;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Meeting;
import com.exchangeinfomanager.bankuaifengxi.ai.WeeklyFenXiWizard;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.StockCalendarAndNewDbOperation;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

public class BanKuaiPopUpMenuForTable extends BanKuaiPopUpMenu
{

	private BanKuaiInfoTable bankuaitable;
	
	private BanKuaiGuanLi bkgldialog = null;
	private StockInfoManager stockmanager;
	

	//因为板块有2种表现形式，树和表，所以争对2个形式做了两套
	public BanKuaiPopUpMenuForTable (StockInfoManager stockmanager1,BanKuaiInfoTable bankuaitable1) 
	{
		this.stockmanager = stockmanager1;
		this.bankuaitable =  bankuaitable1;
	}

	protected void createEvents () 
	{
		menuItemSetting.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				int row = bankuaitable.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择一个板块","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				int modelRow = bankuaitable.convertRowIndexToModel(row); 
				BanKuai bankuai = ((BanKuaiInfoTableModel) bankuaitable.getModel()).getBanKuai(modelRow);

				BanKuaiShuXingSheZhi  bksetting = new BanKuaiShuXingSheZhi (bankuai);
				JOptionPane.showMessageDialog(null, bksetting, bankuai.getMyOwnCode()+bankuai.getMyOwnName()+ "板块设置", JOptionPane.OK_CANCEL_OPTION);
				bksetting = null;
			}
			
		});

		
		menuItemAddNews.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				int row = bankuaitable.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择一个板块","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				int modelRow = bankuaitable.convertRowIndexToModel(row); 
				BanKuai bankuai = ((BanKuaiInfoTableModel) bankuaitable.getModel()).getBanKuai(modelRow);

				addBanKuaiNews (bankuai);
			}
			
		});
		
		menuItemAddToGz.addActionListener(new ActionListener() {
			@Override

			public void actionPerformed(ActionEvent evt) {
				int row = bankuaitable.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择一个板块","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				int modelRow = bankuaitable.convertRowIndexToModel(row); 
				BanKuai bankuai = ((BanKuaiInfoTableModel) bankuaitable.getModel()).getBanKuai(modelRow);
				
				
				LocalDate fxdate = ((BanKuaiInfoTableModel)bankuaitable.getModel()).getCurDisplayedDate();

				addGuanZhu (bankuai,fxdate);
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

