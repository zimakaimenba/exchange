package com.exchangeinfomanager.commonlib.jstockcombobox;

import java.awt.Color;
import java.awt.Component;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.ListCellRenderer;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
//import javax.swing.ListCellRenderer;
import javax.swing.DefaultListCellRenderer;


import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiGeGuTableModel;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.gui.subgui.SelectMultiNode;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.nodes.operations.AllCurrentTdxBKAndStoksTree;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.google.common.base.Strings;

public class JStockComboBox extends  JComboBox<String>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SystemConfigration sysconfig;
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
		this.setRenderer(new JStockComboBoxRenderer());
		this.setModel( new JStockComboBoxModel () );
//		this.setForeground(Color.RED);
		bkdbopt = new BanKuaiDbOperation ();
		sysconfig = SystemConfigration.getInstance();
//		allbkstock = AllCurrentTdxBKAndStoksTree.getInstance();
		
		createEvents ();
	}

	private BanKuaiDbOperation bkdbopt;
//	private BkChanYeLianTreeNode nodeshouldbedisplayed;
	private Integer onlyselectnodetype;
	/*
	 * 
	 */
	public BkChanYeLianTreeNode getUserInputNode ()
	{
//		Object item = this.getEditor().getItem();
		Object selectitem = ((JStockComboBoxModel)this.getModel()).getSelectedItem(); 
		BkChanYeLianTreeNode nodeshouldbedisplayed = (BkChanYeLianTreeNode) selectitem;
		nodeshouldbedisplayed = preSearch( nodeshouldbedisplayed.getMyOwnCode(), onlyselectnodetype);
		
//		this.revalidate();
		
		return nodeshouldbedisplayed;
	}
	/*
	 * 
	 */
	public BkChanYeLianTreeNode updateUserSelectedNode (Stock stock)
	{
//		this.nodeshouldbedisplayed = stock;

		preSearch(stock.getMyOwnCode(),BkChanYeLianTreeNode.TDXGG);
		
		Integer alreadyin = ((JStockComboBoxModel)getModel()).hasTheNode(stock.getMyOwnCode());
		if(alreadyin == -1) {
			((JStockComboBoxModel)getModel()).addElement( stock );
			((JStockComboBoxModel)getModel()).setSelectedItem(stock.getMyOwnCode() + stock.getMyOwnName() );
			
//			this.revalidate();
		}
		return stock;
	}
	public BkChanYeLianTreeNode updateUserSelectedNode (BanKuai bk)
	{
//		this.nodeshouldbedisplayed = bk;
		
		preSearch(bk.getMyOwnCode(),BkChanYeLianTreeNode.TDXBK);
		
		Integer alreadyin = ((JStockComboBoxModel)getModel()).hasTheNode(bk.getMyOwnCode());
		if(alreadyin == -1) {
			((JStockComboBoxModel)getModel()).addElement( bk );
			((JStockComboBoxModel)getModel()).setSelectedItem(bk.getMyOwnCode() + bk.getMyOwnName() );
			
//			this.revalidate();
		}

		return bk;
	}
	/*
	 * 
	 */
	public BkChanYeLianTreeNode updateUserSelectedNode (String nodecode,Integer nodetype)
	{
		BkChanYeLianTreeNode nodeshouldbedisplayed = preSearch (nodecode,nodetype);
		if(nodeshouldbedisplayed != null) {
			((JStockComboBoxModel)getModel()).addElement( nodeshouldbedisplayed );
			((JStockComboBoxModel)getModel()).setSelectedItem(nodeshouldbedisplayed.getMyOwnCode() + nodeshouldbedisplayed.getMyOwnName() );
			
//			this.revalidate();
			return nodeshouldbedisplayed;
		} else
			return null;
		
		
	}
	/*
	 * 获取用户code的板块或个股的基本信息 
	 */
	private BkChanYeLianTreeNode preSearch(String nodecode,Integer nodetype) 
	{
		 BkChanYeLianTreeNode nodeshouldbedisplayed = null;
		 ArrayList<BkChanYeLianTreeNode> nodeslist = bkdbopt.getNodesBasicInfo (nodecode);
		 if(nodeslist.size() == 0) {
			 setEditorToNull ();
			 JOptionPane.showMessageDialog(null,"股票/板块代码不存在，请再次输入正确股票代码！");
			 return null;
		 }
		 
		 if( nodeslist.size() > 1 ) { 
			 if(nodetype != -1) { //用户指定了只要什么类型的node
				 for( BkChanYeLianTreeNode tmpnode : nodeslist) 
					 if(tmpnode.getType() == nodetype) {
						 nodeshouldbedisplayed = tmpnode;
						 break;
					 }
				 
			 } else { //没有指定，就显示JLIST，让用户选择
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
			 }
		 } else
			 nodeshouldbedisplayed = nodeslist.get(0);
		 
		 if(nodeshouldbedisplayed.getType() == BkChanYeLianTreeNode.TDXGG) { //是个股
//					if(accountschicangconfig.isSystemChiCang(stockcode)) {
//						nodeshouldbedisplayed = accountschicangconfig.setStockChiCangAccount((Stock)nodeshouldbedisplayed);
//					} 
					
//					nodeshouldbedisplayed = bkdbopt.getCheckListsXMLInfo ((Stock)nodeshouldbedisplayed);
//					nodeshouldbedisplayed = bkdbopt.getZdgzMrmcZdgzYingKuiFromDB((Stock)nodeshouldbedisplayed);
					nodeshouldbedisplayed = bkdbopt.getTDXBanKuaiForAStock ((Stock)nodeshouldbedisplayed); //通达信板块信息
					
					
//					nodeshouldbedisplayed = bkcyl.getStockChanYeLianInfo ((Stock)nodeshouldbedisplayed);
					
//					nodeshouldbedisplayed = bkcyl.getStockChanYeLianInfo ((Stock)nodeshouldbedisplayed);
		 }	
		 
		 return nodeshouldbedisplayed;
	}
	
	private void createEvents() 
	{
		JPopupMenu jPopupMenue = new JPopupMenu();
		JMenuItem menuItemguanzhu = new JMenuItem("同步近期关注个股"); 
		JMenuItem menuItemgchicang = new JMenuItem("同步持仓");
		menuItemgchicang.setEnabled(false);
		jPopupMenue.add(menuItemguanzhu);
		jPopupMenue.add(menuItemgchicang);
		if(this.onlyselectnodetype != null && this.onlyselectnodetype == BkChanYeLianTreeNode.TDXBK ) {
			menuItemguanzhu.setEnabled(false);
		}
		
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
		
		this.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent  e)
			{
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					String nodecode = formatStockCode( (String)getEditor().getItem() );
					Integer result = ((JStockComboBoxModel)getModel()).hasTheNode (nodecode);
					if(result == -1) 
						updateUserSelectedNode (nodecode,onlyselectnodetype);
					
				}
			}
			
		});
		
	
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
	private void sysnRecentGuanZhu() 
	{
		
		DateRangeSelectPnl datachoose = new DateRangeSelectPnl (24); 
		JOptionPane.showMessageDialog(null, datachoose,"选择关注个股的时间段", JOptionPane.OK_CANCEL_OPTION);
		
		LocalDate searchstart = datachoose.getDatachoosestart();
		LocalDate searchend = datachoose.getDatachooseend();
		
		String zdyguanzhubkname = sysconfig.getCurZdyBanKuaiOfGuanZhuGeGu ();
		Set<String> result = bkdbopt.sysnRecentGuanZhuGeGu (zdyguanzhubkname,searchstart,searchend);
		for(String geguname : result ) {
			this.updateUserSelectedNode (geguname,BkChanYeLianTreeNode.TDXGG);
		}
		
		((JStockComboBoxRenderer)this.getRenderer()).setGuanZhuGeGuList(result);
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


