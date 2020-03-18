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

import com.exchangeinfomanager.NodesServices.SvsForNodeOfBanKuai;
import com.exchangeinfomanager.NodesServices.SvsForNodeOfStock;
import com.exchangeinfomanager.Trees.BanKuaiAndStockTree;
import com.exchangeinfomanager.Trees.CreateExchangeTree;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.AccountInfoBasic;
import com.exchangeinfomanager.accountconfiguration.AccountsInfo.StockChiCangInfo;
import com.exchangeinfomanager.database.AccountDbOperation;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.gui.subgui.DateRangeSelectPnl;
import com.exchangeinfomanager.gui.subgui.SelectMultiNode;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.google.common.collect.Multimap;


public class JStockComboBox extends  JComboBox<String>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SystemConfigration sysconfig;
	private BanKuaiAndStockTree bkstktree;
//	private AllCurrentTdxBKAndStoksTree allbkstock;

	public JStockComboBox() 
	{
		super();
		
		this.onlyselectnodetype = -1;
		generalSetup ();
		
	}
	
	public JStockComboBox(int onlyselecttype) //用户可以指定只选择从数据库中读出某种类型的node
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
	     
	    bkstktree = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks();
		
		sysconfig = SystemConfigration.getInstance();
		
		createEvents ();
	}

//	private BanKuaiDbOperation bkdbopt;
	private Integer onlyselectnodetype;
	/*
	 * 
	 */
	public BkChanYeLianTreeNode getUserInputNode ()
	{
		Object selectitem = ((JStockComboBoxModel)this.getModel()).getSelectedItem(); 
		BkChanYeLianTreeNode nodeshouldbedisplayed = (BkChanYeLianTreeNode) selectitem;
//		nodeshouldbedisplayed = preSearch( nodeshouldbedisplayed.getMyOwnCode(), onlyselectnodetype);
		
		return nodeshouldbedisplayed;
	}
	/*
	 * 
	 */
	public BkChanYeLianTreeNode updateUserSelectedNode (BkChanYeLianTreeNode node)
	{
//		SvsForNodeOfStock svsbk = new SvsForNodeOfStock ();
//		svsbk.getNodeJiBenMian(stock);
		
		Integer alreadyin = ((JStockComboBoxModel)getModel()).hasTheNode(node.getMyOwnCode());
		if(alreadyin == -1) {
			((JStockComboBoxModel)getModel()).addElement( node );
			((JStockComboBoxModel)getModel()).setSelectedItem(node);
		}
		return node;
	}
	/*
	 * 
	 */
//	public BkChanYeLianTreeNode updateUserSelectedNode (Stock stock)
//	{
////		SvsForNodeOfStock svsbk = new SvsForNodeOfStock ();
////		svsbk.getNodeJiBenMian(stock);
//		
//		Integer alreadyin = ((JStockComboBoxModel)getModel()).hasTheNode(stock.getMyOwnCode());
//		if(alreadyin == -1) {
//			((JStockComboBoxModel)getModel()).addElement( stock );
//			((JStockComboBoxModel)getModel()).setSelectedItem(stock);
//		}
//		return stock;
//	}
//	/*
//	 * 
//	 */
//	public BkChanYeLianTreeNode updateUserSelectedNode (BanKuai bk)
//	{
////		SvsForNodeOfBanKuai svsbk = new SvsForNodeOfBanKuai ();
////		svsbk.getNodeJiBenMian(bk);
//		
//		Integer alreadyin = ((JStockComboBoxModel)getModel()).hasTheNode(bk.getMyOwnCode());
//		if(alreadyin == -1) {
//			((JStockComboBoxModel)getModel()).addElement( bk );
//			((JStockComboBoxModel)getModel()).setSelectedItem(bk );
//		}
//		return bk;
//	}
	/*
	 * 
	 */
	public BkChanYeLianTreeNode updateUserSelectedNode (String nodecode)
	{
//		BkChanYeLianTreeNode nodeshouldbedisplayed = preSearch (nodecode,nodetype);
		BkChanYeLianTreeNode nodeshouldbedisplayed1 = bkstktree.getSpecificNodeByHypyOrCode(nodecode, BkChanYeLianTreeNode.TDXGG);
		BkChanYeLianTreeNode nodeshouldbedisplayed2 = bkstktree.getSpecificNodeByHypyOrCode(nodecode, BkChanYeLianTreeNode.TDXBK);
		
		BkChanYeLianTreeNode nodeshouldbedisplayed = null;
		if(nodeshouldbedisplayed1 != null  && nodeshouldbedisplayed2 != null) { //板块和个股都有该代码出现
			List<BkChanYeLianTreeNode> nodeslist = new ArrayList<> ();
			nodeslist.add(nodeshouldbedisplayed1);
			nodeslist.add(nodeshouldbedisplayed2);
			
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
		if(nodeshouldbedisplayed1 != null  ) {
			nodeshouldbedisplayed = nodeshouldbedisplayed1;
		} else
		if (nodeshouldbedisplayed2 != null) {
			nodeshouldbedisplayed = nodeshouldbedisplayed2;
		} else
		if(nodeshouldbedisplayed1 == null  && nodeshouldbedisplayed2 == null)  
			return null;
		
		this.updateUserSelectedNode(nodeshouldbedisplayed);
//		if(nodeshouldbedisplayed.getType() == BkChanYeLianTreeNode.TDXGG)
//			this.updateUserSelectedNode((Stock)nodeshouldbedisplayed);
//		else
//			this.updateUserSelectedNode((BanKuai)nodeshouldbedisplayed);
		
		return nodeshouldbedisplayed;
	}
	/*
	 * 获取用户code的板块或个股的基本信息 
	 */
//	private BkChanYeLianTreeNode preSearch(String nodecode,Integer nodetype) 
//	{
//		 BkChanYeLianTreeNode nodeshouldbedisplayed = null;
//		 ArrayList<BkChanYeLianTreeNode> nodeslist = bkdbopt.getNodesBasicInfo (nodecode);
//		 if(nodeslist.size() == 0) {
//			 setEditorToNull ();
//			 JOptionPane.showMessageDialog(null,"股票/板块代码不存在，请再次输入正确股票代码！");
//			 return null;
//		 }
//		 
//		 if( nodeslist.size() > 1 ) { 
//			 if(nodetype != -1) { //用户指定了只要什么类型的node
//				 for( BkChanYeLianTreeNode tmpnode : nodeslist) 
//					 if(tmpnode.getType() == nodetype) {
//						 nodeshouldbedisplayed = tmpnode;
//						 break;
//					 }
//				 
//			 } else { //没有指定，就显示JLIST，让用户选择
//				 SelectMultiNode userselection = new SelectMultiNode(nodeslist);
//				 int exchangeresult = JOptionPane.showConfirmDialog(null, userselection, "请选择", JOptionPane.OK_CANCEL_OPTION);
//				 if(exchangeresult == JOptionPane.CANCEL_OPTION)
//						return null;
//				 
//				 try {
//					 int userselected = userselection.getUserSelection();
//					 nodeshouldbedisplayed = nodeslist.get(userselected);
//				 } catch (java.lang.ArrayIndexOutOfBoundsException e) { //用户没有选择直接回车的情况
//					 nodeshouldbedisplayed = nodeslist.get(0);
//				 }
//			 }
//		 } else
//			 nodeshouldbedisplayed = nodeslist.get(0);
//		 
////		 if(nodeshouldbedisplayed.getType() == BkChanYeLianTreeNode.TDXGG) { //是个股
////					nodeshouldbedisplayed = bkdbopt.getTDXBanKuaiForAStock ((Stock)nodeshouldbedisplayed); //通达信板块信息
////		 }	
//		 
//		 return nodeshouldbedisplayed;
//	}
	
	private void createEvents() 
	{
		JPopupMenu jPopupMenue = new JPopupMenu();
		JMenuItem menuItemgchicang = new JMenuItem("同步持仓");
		JMenuItem menuItemgpreviouschicang = new JMenuItem("同步历史持仓");
		JMenuItem menuItemguanzhu = new JMenuItem("同步近期关注个股"); 
		menuItemgchicang.setEnabled(false);
		jPopupMenue.add(menuItemgchicang);
		jPopupMenue.add(menuItemgpreviouschicang);
		jPopupMenue.add(menuItemguanzhu);
		
		if(this.onlyselectnodetype != null && this.onlyselectnodetype == BkChanYeLianTreeNode.TDXBK ) {
			menuItemguanzhu.setEnabled(false);
			menuItemgpreviouschicang.setEnabled(false);
		}
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
						
					} catch (java.lang.ClassCastException ex) {
						ex.printStackTrace();
					}
					Integer result = ((JStockComboBoxModel)getModel()).hasTheNode (nodecode);
					BkChanYeLianTreeNode node;
					if(result == -1) {
						node = updateUserSelectedNode (nodecode);

					}
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
		
		String zdyguanzhubkname = sysconfig.getCurZdyBanKuaiOfGuanZhuGeGu ();
		BanKuaiDbOperation bkdbopt = new BanKuaiDbOperation ();
		Set<String> result = bkdbopt.sysnRecentGuanZhuGeGu (zdyguanzhubkname,searchstart,searchend);
		for(String geguname : result ) {
			Stock node = (Stock)bkstktree.getSpecificNodeByHypyOrCode(geguname, BkChanYeLianTreeNode.TDXGG);
			if(node != null)
				this.updateUserSelectedNode (node);
			else
				System.out.println(geguname + "未找到该股。");
		}
		
		((JStockComboBoxNodeRenderer)this.getRenderer()).setGuanZhuGeGuList(result);
		
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
			Stock node = (Stock)bkstktree.getSpecificNodeByHypyOrCode(stockcode, BkChanYeLianTreeNode.TDXGG); 
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


	
	/*
	 * 已经废弃
	 */
//	private void updateStockCombox (String tmp)
//	{
//		boolean isaddItem = true;
////		int updateItem = -1;
//
//	   	  //判断用户所输入的项目是否有重复，若有重复则不增加到JComboBox中。
//	   	  try{
//	   			  for(int i=0;i< this.getItemCount();i++) {
//	   				  	String curitem = this.getItemAt(i).toString();
//		   				if(curitem.equals(tmp)  ) { // 已经有了，不用有任何操作
//		   					isaddItem = false;
//		   					this.setSelectedIndex(i);
//		   					break;
//		   				}
////		   	  	  	  if(curitem.substring(0, 6).equals(tmp) && curitem.length()>6 ) { //
////		   	  	  	  	 isaddItem = false;
////		   	  	  	  	 break;
////		   	  	  	  }
//		   				String curstring = curitem.substring(0, 6);
//			   	  	  if(curitem.substring(0, 6).equals(tmp)  ) { // 有了，但只有code,没有名字
//			   	  		  
//			   	  		isaddItem = false;
////			   	  		 updateItem = i;
//		   	  	  	  	 break;
//		   	  	  	  }
//		   	  	  }
//	   	  	  
//	   	  	  
//	   	  	  if (isaddItem){
//	   	  		  try{
//	   	  			tmp = nodeshouldbedisplayed.getMyOwnCode().trim() + nodeshouldbedisplayed.getMyOwnName().trim();
//		  			  this.insertItemAt(tmp,0);//插入项目tmp到0索引位置(第一列中).
//		  			  this.setSelectedIndex(0);
//	   	  		  } catch (java.lang.NullPointerException e) {
//	   	  			  
//	   	  		  }
//	  			  
//	   	  	  }
////	   	  	  if(updateItem >= 0) {
////	   	  		  this.removeItemAt(updateItem);
////	   	  		  tmp = nodeshouldbedisplayed.getMyOwnCode().trim() + nodeshouldbedisplayed.getMyOwnName().trim();
////	  			  this.insertItemAt(tmp,0);//插入项目tmp到0索引位置(第一列中).
////	  			  this.setSelectedIndex(0);
////	   	  	  }
//	   	  }catch(NumberFormatException ne){
//	   		
//	   	  }
//	}
//	private void updateStockCombox() 
//	{
//		String tmp = formatStockCode( (String)this.getEditor().getItem() );//有可能是原来输入过的，要把代码选择出来。
//		updateStockCombox (tmp);
//	}


}


