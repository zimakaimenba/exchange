
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
//import javax.swing.ListCellRenderer;
import javax.swing.DefaultListCellRenderer;

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BanKuaiAndStockBasic;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.bankuaichanyelian.bankuaigegutable.BanKuaiGeGuTableModel;
import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.gui.subgui.SelectMultiNode;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;

public class JStockComboBox extends  JComboBox<String>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SystemConfigration sysconfig;

	public JStockComboBox() 
	{
		super();
		createEvents ();
		this.setEditable(true);
		this.setRenderer(new JStockComboBoxRenderer());
//		this.setForeground(Color.RED);
		bkdbopt = new BanKuaiDbOperation ();
		sysconfig = SystemConfigration.getInstance();
	}
	public JStockComboBox(int onlyselecttype) //用户可以指定只选择从数据库中读出某种类型的node
	{
		super();
		
		this.setRenderer(new JStockComboBoxRenderer());
		this.setEditable(true);
//		this.setForeground(Color.RED);
		this.bkdbopt = new BanKuaiDbOperation ();
		sysconfig = SystemConfigration.getInstance();
		this.onlyselectnodetype = onlyselecttype;
		
		createEvents ();
		
	}

	private BanKuaiDbOperation bkdbopt;
	private BkChanYeLianTreeNode nodeshouldbedisplayed;
	private Integer onlyselectnodetype;
	 

	/*

	 * 
	 */
	public BkChanYeLianTreeNode getUserInputNode ()
	{
		statChangeActions ();
		return nodeshouldbedisplayed;
	}
	/*
	 * 
	 */
	public BkChanYeLianTreeNode updateUserSelectedNode (Stock stock)
	{
		String stockcode = stock.getMyOwnCode();
		String stocname = stock.getMyOwnName();

		preSearch(stock.getMyOwnCode(),BanKuaiAndStockBasic.TDXGG);
		updateStockCombox(stockcode+stocname);
		return nodeshouldbedisplayed;
	}
	public BkChanYeLianTreeNode updateUserSelectedNode (BanKuai bk)
	{
		this.nodeshouldbedisplayed = bk;
		
		String bkcode = bk.getMyOwnCode();
		String bkname = bk.getMyOwnName();
		preSearch(bk.getMyOwnCode(),BanKuaiAndStockBasic.TDXBK);
		updateStockCombox(bkcode+bkname);
		return this.nodeshouldbedisplayed;
	}
	/*
	 * 
	 */
	public BkChanYeLianTreeNode updateUserSelectedNode (String stockcode,Integer nodetype)
	{
//		this.addItem(stockcode+stocname);
//		this.setSelectedItem(stockcode);
		preSearch(stockcode,nodetype);
//		String tmp = formatStockCode( (String)this.getEditor().getItem() );//有可能是原来输入过的，要把代码选择出来。
		if(nodeshouldbedisplayed != null) {
			updateStockCombox(stockcode);
			return nodeshouldbedisplayed;
		} else
			return null;
		
	}
	/*
	 * 
	 */
	private void statChangeActions()
	{
		String nodecode;
		try	{
			nodecode = formatStockCode( (String)this.getEditor().getItem() );
			if(!checkCodeInputFormat(nodecode)) {
				return;
			}
			
			if(this.nodeshouldbedisplayed != null && nodecode.equals( this.nodeshouldbedisplayed.getMyOwnCode() ) )
				return;
			
			if(this.onlyselectnodetype != null) { //用户指定了只要什么类型的node
				preSearch(nodecode,this.onlyselectnodetype);
				if(nodeshouldbedisplayed != null && nodeshouldbedisplayed.getType() == this.onlyselectnodetype )
					updateStockCombox(nodecode);
				else 
					nodeshouldbedisplayed = null; //不是用户指定的类型，直接NULL
			} else {
				preSearch(nodecode,-1);
				if(nodeshouldbedisplayed != null)
					updateStockCombox(nodecode);
			}
			
		} catch(java.lang.NullPointerException ex)	{
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "请输入股票代码！","Warning", JOptionPane.WARNING_MESSAGE);
			return;
		} catch(java.lang.StringIndexOutOfBoundsException ex2) {
			this.nodeshouldbedisplayed = null;
			return;
		}
	}
	/*
	 * 获取用户输入的个股的基本信息 
	 */
//	private void preSearch(Stock stock) 
//	{
//		nodeshouldbedisplayed = bkdbopt.getStockBasicInfo (stock);
//		nodeshouldbedisplayed = bkdbopt.getTDXBanKuaiForAStock ((Stock)nodeshouldbedisplayed); //通达信板块信息
//	}
	/*
	 * 获取用户code的板块或个股的基本信息 
	 */
	private Integer preSearch(String nodecode,Integer nodetype) 
	{
		nodeshouldbedisplayed = null;
		 ArrayList<BkChanYeLianTreeNode> nodeslist = bkdbopt.getNodesBasicInfo (nodecode);
		 if(nodeslist.size() == 0) {
			 JOptionPane.showMessageDialog(null,"股票/板块代码不存在，请再次输入正确股票代码！");
			 return null;
		 }
		 
		 if(nodeslist.size()>1) { 
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
		 
		 if(nodeshouldbedisplayed.getType() == BanKuaiAndStockBasic.TDXGG) { //是个股
//					if(accountschicangconfig.isSystemChiCang(stockcode)) {
//						nodeshouldbedisplayed = accountschicangconfig.setStockChiCangAccount((Stock)nodeshouldbedisplayed);
//					} 
					
//					nodeshouldbedisplayed = bkdbopt.getCheckListsXMLInfo ((Stock)nodeshouldbedisplayed);
//					nodeshouldbedisplayed = bkdbopt.getZdgzMrmcZdgzYingKuiFromDB((Stock)nodeshouldbedisplayed);
					nodeshouldbedisplayed = bkdbopt.getTDXBanKuaiForAStock ((Stock)nodeshouldbedisplayed); //通达信板块信息
					
					
//					nodeshouldbedisplayed = bkcyl.getStockChanYeLianInfo ((Stock)nodeshouldbedisplayed);
					
//					nodeshouldbedisplayed = bkcyl.getStockChanYeLianInfo ((Stock)nodeshouldbedisplayed);
		 }	
		 
		 return 1;
	}
	
	private void createEvents() 
	{
		JPopupMenu jPopupMenue = new JPopupMenu();
		JMenuItem menuItemguanzhu = new JMenuItem("同步近期关注个股"); 
		JMenuItem menuItemgchicang = new JMenuItem("同步持仓");
		menuItemgchicang.setEnabled(false);
		jPopupMenue.add(menuItemguanzhu);
		jPopupMenue.add(menuItemgchicang);
		if(this.onlyselectnodetype != null && this.onlyselectnodetype == BanKuaiAndStockBasic.TDXBK ) {
			menuItemguanzhu.setEnabled(false);
		}
		
		menuItemguanzhu.addActionListener(new ActionListener() {

	            @Override
	            public void actionPerformed(ActionEvent e) {
	            	sysnRecentGuanZhu ();
	            }
	        });
		 
		this.getEditor().getEditorComponent().addMouseListener(new MouseAdapter() {
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
//					statChangeActions ();
				}
			}
			
		});
		
	
	}
	
	private void sysnRecentGuanZhu() 
	{
		
		DateRangeSelectPnl datachoose = new DateRangeSelectPnl (); 
		JOptionPane.showMessageDialog(null, datachoose,"选择关注个股的时间段", JOptionPane.OK_CANCEL_OPTION);
		
		LocalDate searchstart = datachoose.getDatachoosestart();
		LocalDate searchend = datachoose.getDatachooseend();
		
		String zdyguanzhubkname = sysconfig.getCurZdyBanKuaiOfGuanZhuGeGu ();
		Set<String> result = bkdbopt.sysnRecentGuanZhuGeGu (zdyguanzhubkname,searchstart,searchend);
		for(String geguname : result ) {
			this.updateUserSelectedNode (geguname,BanKuaiAndStockBasic.TDXGG);
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

	private String formatStockCode (String stockcode)
	{
		if(stockcode.length() >6)
			return stockcode.substring(0,6).trim();
		else
			return stockcode;
	}
	
	private void updateStockCombox (String tmp)
	{
		boolean isaddItem = true;
//		int updateItem = -1;

	   	  //判断用户所输入的项目是否有重复，若有重复则不增加到JComboBox中。
	   	  try{
	   			  for(int i=0;i< this.getItemCount();i++) {
	   				  	String curitem = this.getItemAt(i).toString();
		   				if(curitem.equals(tmp)  ) { // 已经有了，不用有任何操作
		   					isaddItem = false;
		   					this.setSelectedIndex(i);
		   					break;
		   				}
//		   	  	  	  if(curitem.substring(0, 6).equals(tmp) && curitem.length()>6 ) { //
//		   	  	  	  	 isaddItem = false;
//		   	  	  	  	 break;
//		   	  	  	  }
		   				String curstring = curitem.substring(0, 6);
			   	  	  if(curitem.substring(0, 6).equals(tmp)  ) { // 有了，但只有code,没有名字
			   	  		  
			   	  		isaddItem = false;
//			   	  		 updateItem = i;
		   	  	  	  	 break;
		   	  	  	  }
		   	  	  }
	   	  	  
	   	  	  
	   	  	  if (isaddItem){
	   	  		  try{
	   	  			tmp = nodeshouldbedisplayed.getMyOwnCode().trim() + nodeshouldbedisplayed.getMyOwnName().trim();
		  			  this.insertItemAt(tmp,0);//插入项目tmp到0索引位置(第一列中).
		  			  this.setSelectedIndex(0);
	   	  		  } catch (java.lang.NullPointerException e) {
	   	  			  
	   	  		  }
	  			  
	   	  	  }
//	   	  	  if(updateItem >= 0) {
//	   	  		  this.removeItemAt(updateItem);
//	   	  		  tmp = nodeshouldbedisplayed.getMyOwnCode().trim() + nodeshouldbedisplayed.getMyOwnName().trim();
//	  			  this.insertItemAt(tmp,0);//插入项目tmp到0索引位置(第一列中).
//	  			  this.setSelectedIndex(0);
//	   	  	  }
	   	  }catch(NumberFormatException ne){
	   		
	   	  }
	}
//	private void updateStockCombox() 
//	{
//		String tmp = formatStockCode( (String)this.getEditor().getItem() );//有可能是原来输入过的，要把代码选择出来。
//		updateStockCombox (tmp);
//	}


}


