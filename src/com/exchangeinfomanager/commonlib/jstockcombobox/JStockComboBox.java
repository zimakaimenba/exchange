package com.exchangeinfomanager.commonlib.jstockcombobox; 

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.swing.JComboBox;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import com.exchangeinfomanager.Core.Nodes.BanKuai;
import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.Core.Nodes.Stock;
import com.exchangeinfomanager.Core.NodesDataServices.SvsForNodeOfBanKuai;
import com.exchangeinfomanager.Core.NodesDataServices.SvsForNodeOfStock;
import com.exchangeinfomanager.Core.Trees.BanKuaiAndStockTree;
import com.exchangeinfomanager.Core.Trees.CreateExchangeTree;
import com.exchangeinfomanager.StockCalendar.OnCalendarDateChangeListener;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountInfoBasic;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.StockChiCangInfo;
import com.exchangeinfomanager.database.AccountDbOperation;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.gui.subgui.DateRangeSelectPnl;
import com.exchangeinfomanager.gui.subgui.SelectMultiNode;
import com.exchangeinfomanager.systemconfigration.SetupSystemConfiguration;
import com.google.common.collect.Multimap;


public class JStockComboBox extends  JComboBox<String> implements OnCalendarDateChangeListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
//	private SetupSystemConfiguration sysconfig;

	public JStockComboBox() 
	{
		super();
		
		this.onlyselectnodetype = -1;
		generalSetup ();
	}
	
	public JStockComboBox(int onlyselecttype) //可以指定只选择从数据库中读出某种类型的node
	{
		super();
		
		this.onlyselectnodetype = onlyselecttype;
		generalSetup ();
	}
	
	private void generalSetup() 
	{
		this.setEditable(true);
		
		this.setModel( new JStockComboBoxModel (this.onlyselectnodetype) );
		
		this.setRenderer(new JStockComboBoxNodeRenderer());
	    this.setEditor(new JStockComboBoxEditor());

		createEvents ();
		
		jPopupMenue = new JPopupMenu();
		
		if(this.onlyselectnodetype != null || this.onlyselectnodetype == -1) {
			this.createMenuForBanKuai();
			this.createMenuForStock();
		} else
		if( this.onlyselectnodetype == BkChanYeLianTreeNode.TDXBK ||  this.onlyselectnodetype == BkChanYeLianTreeNode.DZHBK ) {
			this.createMenuForBanKuai();
		} else
		if( this.onlyselectnodetype == BkChanYeLianTreeNode.TDXGG ) {
			this.createMenuForStock();
		} 
	}

	private Integer onlyselectnodetype;
	private JPopupMenu jPopupMenue;
	/*
	 * 
	 */
	public BkChanYeLianTreeNode getUserInputNode ()
	{
		Object selectitem = ((JStockComboBoxModel)this.getModel()).getSelectedItem(); 
		BkChanYeLianTreeNode nodeshouldbedisplayed = (BkChanYeLianTreeNode) selectitem;
		return nodeshouldbedisplayed;
	}
	/*
	 * 
	 */
	public BkChanYeLianTreeNode updateUserSelectedNode (BkChanYeLianTreeNode node)
	{
		Integer alreadyin = ((JStockComboBoxModel)getModel()).hasTheNode(node.getMyOwnCode());
		if(alreadyin == -1) {
			((JStockComboBoxModel)getModel()).addElement( node );
			((JStockComboBoxModel)getModel()).setSelectedItem(node);
		}
		return node;
	}
	/*
	 * Class key funtion 
	 */
	public BkChanYeLianTreeNode updateUserSelectedNode (String nodecode)
	{
		BkChanYeLianTreeNode nodeshouldbedisplayed_tdxbk = null; BkChanYeLianTreeNode nodeshouldbedisplayed_gg = null;
		BkChanYeLianTreeNode nodeshouldbedisplayed_dzhbk = null;
		if(this.onlyselectnodetype == -1) {
		 nodeshouldbedisplayed_gg = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(nodecode, BkChanYeLianTreeNode.TDXGG);
		 nodeshouldbedisplayed_tdxbk = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(nodecode, BkChanYeLianTreeNode.TDXBK);
		} else
		if( this.onlyselectnodetype == BkChanYeLianTreeNode.TDXBK )
			nodeshouldbedisplayed_tdxbk = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(nodecode, BkChanYeLianTreeNode.TDXBK);
		else
		if( this.onlyselectnodetype == BkChanYeLianTreeNode.TDXGG )
			nodeshouldbedisplayed_gg = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(nodecode, BkChanYeLianTreeNode.TDXGG);
		else
		if( this.onlyselectnodetype == BkChanYeLianTreeNode.DZHBK )
			nodeshouldbedisplayed_dzhbk = CreateExchangeTree.CreateTreeOfDZHBanKuaiAndStocks().getSpecificNodeByHypyOrCode(nodecode, BkChanYeLianTreeNode.DZHBK);
			
		
		BkChanYeLianTreeNode nodeshouldbedisplayed = null;
		if(nodeshouldbedisplayed_tdxbk != null  && nodeshouldbedisplayed_gg != null) { //板块和个股都有该代码出现
			List<BkChanYeLianTreeNode> nodeslist = new ArrayList<> ();
			nodeslist.add(nodeshouldbedisplayed_tdxbk);
			nodeslist.add(nodeshouldbedisplayed_gg);
			
			SelectMultiNode userselection = new SelectMultiNode(nodeslist);
			 int exchangeresult = JOptionPane.showConfirmDialog(null, userselection, "请选择", JOptionPane.OK_CANCEL_OPTION);
			 if(exchangeresult == JOptionPane.CANCEL_OPTION)
					return null;
			 
			 try {
				 int userselected = userselection.getUserSelection();
				 nodeshouldbedisplayed = nodeslist.get(userselected);
			 } catch (java.lang.ArrayIndexOutOfBoundsException e) { //用户没有选择直接回车的情况
				 nodeshouldbedisplayed = nodeslist.get(0);
			 }
			
		} else 
		if(nodeshouldbedisplayed_tdxbk != null  ) {
			nodeshouldbedisplayed = nodeshouldbedisplayed_tdxbk;
		} else
		if (nodeshouldbedisplayed_gg != null) {
			nodeshouldbedisplayed = nodeshouldbedisplayed_gg;
		} else
		if(nodeshouldbedisplayed_tdxbk == null  && nodeshouldbedisplayed_gg == null)  { //可能是大智慧的板块了
			BkChanYeLianTreeNode nodeshouldbedisplayed_dzh = CreateExchangeTree.CreateTreeOfDZHBanKuaiAndStocks().getSpecificNodeByHypyOrCode(nodecode, BkChanYeLianTreeNode.DZHBK);
			if(nodeshouldbedisplayed_dzh != null)
				nodeshouldbedisplayed = nodeshouldbedisplayed_dzh;
			else {
				JOptionPane.showMessageDialog(null, "未找到该个股或板块，可能代码还未被通达信/大智慧加入。");
				return null;
			}
		} 
		
		this.updateUserSelectedNode(nodeshouldbedisplayed);

		return nodeshouldbedisplayed;
	}
	
	private void createMenuForBanKuai ()
	{
		JMenuItem menuItemgjqgz = new JMenuItem("同步近期关注板块");
		JMenuItem menuItemgqsbk = new JMenuItem("同步强势板块");
		JMenuItem menuItemrsbk = new JMenuItem("同步弱势板块");
		menuItemgjqgz.setEnabled(false);
		menuItemgqsbk.setEnabled(false);
		menuItemrsbk.setEnabled(false);
		jPopupMenue.add(menuItemgjqgz);
		jPopupMenue.add(menuItemgqsbk);
		jPopupMenue.add(menuItemrsbk);
		
		menuItemrsbk.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            	
            }
        });
		menuItemgqsbk.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            	
            }
        });
		menuItemgjqgz.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            	
            }
        });
	}
	private void createMenuForStock ()
	{
		JMenuItem menuItemgchicang = new JMenuItem("同步持仓");
		JMenuItem menuItemgpreviouschicang = new JMenuItem("同步历史持仓");
		JMenuItem menuItemguanzhu = new JMenuItem("同步近期关注个股"); 
		menuItemgchicang.setEnabled(false);
		jPopupMenue.add(menuItemgchicang);
		jPopupMenue.add(menuItemgpreviouschicang);
		jPopupMenue.add(menuItemguanzhu);
		
		menuItemgpreviouschicang.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            	sysnPreviousChiCang ();
            }
        });

		menuItemguanzhu.addActionListener(new ActionListener() {

	            @Override
	            public void actionPerformed(ActionEvent e) {
	            	sysnRecentGuanZhu ();
	            }
	        });

	}
	private void createEvents() 
	{
				 
		this.getEditor().getEditorComponent().addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mousePressed(MouseEvent arg0) {
				//System.out.println("this is the test");
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
                  setEditorToNull ();
              } else if (e.getButton() == MouseEvent.BUTTON3) {
            	  jPopupMenue.show(getEditor().getEditorComponent(), e.getX(),   e.getY());
              }
			}
		});
		
		this.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() 
		{
			public void keyPressed(KeyEvent  e)
			{
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					String nodecode = null;
					try{
						String inputtext = ( (JTextField)getEditor().getEditorComponent()).getText();
						nodecode = formatStockCode( inputtext );
					} catch (java.lang.ClassCastException ex) {	ex.printStackTrace();	}
					Integer result = ((JStockComboBoxModel)getModel()).hasTheNode (nodecode);
					BkChanYeLianTreeNode node;
					if(result == -1) 	node = updateUserSelectedNode (nodecode);
				}
			}
			
		});
	}
	/*
	 * 
	 */
	private void sysnRecentGuanZhu() 
	{
		DateRangeSelectPnl datachoose = new DateRangeSelectPnl (24); 
		JOptionPane.showMessageDialog(null, datachoose,"选择关注个股的时间段", JOptionPane.OK_CANCEL_OPTION);
		
		Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
		setCursor(hourglassCursor);
		
		LocalDate searchstart = datachoose.getDatachoosestart();
		LocalDate searchend = datachoose.getDatachooseend();
		
		String zdyguanzhubkname = new SetupSystemConfiguration().getCurZdyBanKuaiOfGuanZhuGeGu ();
		BanKuaiDbOperation bkdbopt = new BanKuaiDbOperation ();
		 Set<BkChanYeLianTreeNode> result = bkdbopt.sysnRecentGuanZhuGeGu (zdyguanzhubkname,searchstart,searchend);
		for(BkChanYeLianTreeNode node : result ) {
//			Stock node = (Stock)bkstktree.getSpecificNodeByHypyOrCode(geguname, BkChanYeLianTreeNode.TDXGG);
			this.updateUserSelectedNode (node);
		}
		
		hourglassCursor = null;
		Cursor hourglassCursor2 = new Cursor(Cursor.DEFAULT_CURSOR);
		setCursor(hourglassCursor2);
	}
	/*
	 * 
	 */
	protected void sysnPreviousChiCang() 
	{
		DateRangeSelectPnl datachoose = new DateRangeSelectPnl (24); 
		JOptionPane.showMessageDialog(null, datachoose,"选择关注个股的时间段", JOptionPane.OK_CANCEL_OPTION);
		
		LocalDate searchstart = datachoose.getDatachoosestart();
		LocalDate searchend = datachoose.getDatachooseend();
		
		AccountDbOperation acntdbopt = new AccountDbOperation ();
		Multimap<String, StockChiCangInfo> acntresult = acntdbopt.getPreviousStockChiCang(searchstart, searchend);
		Set<String> result = new HashSet<> ();
		for (Map.Entry<String,StockChiCangInfo> entry : acntresult.entries()) {
			String tmpstockacntname = (String) entry.getKey();
			StockChiCangInfo tmpstockchicanginfo = (StockChiCangInfo)entry.getValue();
			
			String stockcode = tmpstockchicanginfo.getChicangcode();
			Stock node = (Stock)CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(stockcode, BkChanYeLianTreeNode.TDXGG); 
			this.updateUserSelectedNode (node);
			result.add(stockcode);
		} 
		
		((JStockComboBoxNodeRenderer)this.getRenderer()).setPreviousChicangGeGuList(result);
		
	}
	/*
	 * 
	 */
	private String formatStockCode (String stockcode)
	{
		if(stockcode.length() >6)
			return stockcode.substring(0,6).trim();
		else
			return stockcode;
	}
	
	/*
	 * 
	 */
	private void setEditorToNull() 
	{
		this.getEditor().setItem("");
	}
	/*
	 * 
	 */
	private boolean checkCodeInputFormat(String stockcode) 
	{
		// TODO Auto-generated method stub
//		System.out.println(Pattern.matches("\\d{6}$","2223") );
//		System.out.println(Pattern.matches("^\\d{6}","600138zhong") );
//		System.out.println(Pattern.matches("\\d{6}$","000123") );
//		System.out.println(Pattern.matches("\\d{6}[\u4E00-\u9FA5A-Za-z0-9_]+$","000123abccc") );
//		System.out.println(Pattern.matches("\\d{6}[\u4E00-\u9FA5A-Za-z0-9_]+$","000123") );
//		System.out.println(Pattern.matches("^\\d{6}{6,100}$","000123中青旅理论") );
//		System.out.println(Pattern.matches("\\d{6}[\u4E00-\u9FA5A-Za-z0-9_]$","ccccc000123abccc") );
		
		//或者是6位全数字，或者是前面6位是数字
			if( Pattern.matches("\\d{6}$",stockcode)  || Pattern.matches("\\d{6}[\u4E00-\u9FA5A-Za-z0-9_]+$",stockcode) )
				return true;
			else return false;
	}

	@Override
	public void dateChanged(LocalDate newdate) {
		((JStockComboBoxModel)this.getModel() ).setCurrentDataDate(newdate);
		
	}
}


