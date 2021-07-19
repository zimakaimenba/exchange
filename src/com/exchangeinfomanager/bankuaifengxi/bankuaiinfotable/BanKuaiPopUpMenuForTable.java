package com.exchangeinfomanager.bankuaifengxi.bankuaiinfotable;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.HashSet;

import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.tree.TreePath;

import com.exchangeinfomanager.A.MainGui.StockInfoManager;
import com.exchangeinfomanager.Core.Nodes.BanKuai;
import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.TagManagment.JDialogForTagSearchMatrixPanelForAddSysNewsToNode;
import com.exchangeinfomanager.bankuaichanyelian.BanKuaiGuanLi;
import com.exchangeinfomanager.bankuaichanyelian.BanKuaiShuXingSheZhi;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.NewsPnl2.TDXNodesZhiShuGJRQPnl;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.NewsPnl2.TDXNodsInforPnl;
import com.exchangeinfomanager.bankuaifengxi.ai.WeeklyFenXiWizard;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.StockCalendarAndNewDbOperation;

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
	/*
	 * (non-Javadoc)
	 * @see com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiPopUpMenu#createEvents()
	 */
	protected void showGeGuInfoWin() 
	{
		 int  view_row = this.bankuaitable.getSelectedRow();
		 int  model_row = this.bankuaitable.convertRowIndexToModel(view_row);//将视图中的行索引转化为数据模型中的行索引
		 
		 BanKuai bankuai = (BanKuai) ((BanKuaiInfoTableModel)this.bankuaitable.getModel()).getNode(model_row);
		 this.stockmanager.getcBxstockcode().updateUserSelectedNode(bankuai );
		 this.stockmanager.toFront();
	}
	
	protected void createEvents () 
	{
		menuItemAddBkGJRQ.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				int row = bankuaitable.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择一个板块","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				int modelRow = bankuaitable.convertRowIndexToModel(row); 
				BanKuai bankuai = (BanKuai) ((BanKuaiInfoTableModel) bankuaitable.getModel()).getNode(modelRow);

				TDXNodesZhiShuGJRQPnl gjrq = new TDXNodesZhiShuGJRQPnl (bankuai);
				gjrq.setVisible(true);
			}
			
		});

		menuItemBkInfo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				int row = bankuaitable.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择一个板块","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				int modelRow = bankuaitable.convertRowIndexToModel(row); 
				BanKuai bankuai = (BanKuai) ((BanKuaiInfoTableModel) bankuaitable.getModel()).getNode(modelRow);

				showGeGuInfoWin ();
			}
			
		});

		menuItemSetting.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				int row = bankuaitable.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择一个板块","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				int modelRow = bankuaitable.convertRowIndexToModel(row); 
				BanKuai bankuai = (BanKuai) ((BanKuaiInfoTableModel) bankuaitable.getModel()).getNode(modelRow);

//				BanKuaiShuXingSheZhi  bksetting = new BanKuaiShuXingSheZhi (bankuai);
//				JOptionPane.showMessageDialog(null, bksetting, bankuai.getMyOwnCode()+bankuai.getMyOwnName()+ "板块设置", JOptionPane.OK_CANCEL_OPTION);
//				bksetting = null;
				
				if(bkgldialog == null ) {
					bkgldialog = new BanKuaiGuanLi();
					bkgldialog.setModal(false);

					bkgldialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				} 
				
				bkgldialog.toFront();
				bkgldialog.setVisible(true);
				
//				if(!bkgldialog.isVisible() ) {
//					bkgldialog.toFront();
//					bkgldialog.setVisible(true);
//				 } 
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
				BanKuai bankuai = (BanKuai) ((BanKuaiInfoTableModel) bankuaitable.getModel()).getNode(modelRow);

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
				BanKuai bankuai = (BanKuai) ((BanKuaiInfoTableModel) bankuaitable.getModel()).getNode(modelRow);
				
				
				LocalDate fxdate = ((BanKuaiInfoTableModel)bankuaitable.getModel()).getCurDisplayedDate();

				addGuanZhu (bankuai,fxdate);
			}
			
		});
		
	}

	@Override
	public void addBanKuaiNews(BkChanYeLianTreeNode node) 
	{
		JDialogForTagSearchMatrixPanelForAddSysNewsToNode newlog 
    		= new JDialogForTagSearchMatrixPanelForAddSysNewsToNode (  node);
	    newlog.setModal(true);
	    newlog.setVisible(true);
		
	}

}

