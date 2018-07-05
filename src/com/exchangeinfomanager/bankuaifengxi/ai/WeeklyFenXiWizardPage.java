package com.exchangeinfomanager.bankuaifengxi.ai;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalDate;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.exchangeinfomanager.asinglestockinfo.AllCurrentTdxBKAndStoksTree;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.bankuaichanyelian.BanKuaiAndChanYeLian2;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.github.cjwizard.WizardPage;

public abstract class WeeklyFenXiWizardPage extends WizardPage 
{

	protected BkChanYeLianTreeNode displaynode;
	protected LocalDate displaydate;
	protected WeeklyFengXiXmlHandler fxmlhandler;
	protected AllCurrentTdxBKAndStoksTree allbksks;
	protected BanKuaiAndChanYeLian2 bkcyl;
	protected SystemConfigration sysconfig;
	protected BanKuaiDbOperation bkdbopt;
	protected ZhongDianGuanZhu zdgzinfo;

	protected PropertyChangeSupport pcs = new PropertyChangeSupport(this); //	https://stackoverflow.com/questions/4690892/passing-a-value-between-components/4691447#4691447
	public void addPropertyChangeListener(PropertyChangeListener listener) {
	        pcs.addPropertyChangeListener(listener);
	}
	/*
	 * 
	 */
	public WeeklyFenXiWizardPage(String title, String description, BkChanYeLianTreeNode displaynode2, LocalDate selectdate2,WeeklyFengXiXmlHandler xmlhandler2) 
	{
		super(title,description);
		this.displaynode = displaynode2;
		this.fxmlhandler = xmlhandler2;
		this.displaydate = selectdate2;

		this.allbksks = AllCurrentTdxBKAndStoksTree.getInstance();
		this.bkcyl = BanKuaiAndChanYeLian2.getInstance();
		
		this.sysconfig = SystemConfigration.getInstance();
		this.bkdbopt = new BanKuaiDbOperation ();
		
		if(this.fxmlhandler != null)
			this.zdgzinfo = this.fxmlhandler.getZhongDianGuanZhu();
	}
	
	/*
	 * 
	 */
	abstract public String getWeeklyFenXiComments ();

}
