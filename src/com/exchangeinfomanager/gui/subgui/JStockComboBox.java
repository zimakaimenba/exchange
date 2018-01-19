package com.exchangeinfomanager.gui.subgui;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import com.exchangeinfomanager.asinglestockinfo.BanKuai;
import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode;
import com.exchangeinfomanager.asinglestockinfo.Stock;
import com.exchangeinfomanager.database.BanKuaiDbOperation;

public class JStockComboBox extends  JComboBox<String>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JStockComboBox() 
	{
		super();
		createEvents ();
		this.setEditable(true);
		this.setForeground(Color.RED);
		bkdbopt = new BanKuaiDbOperation ();
	}
	
	public JStockComboBox(int onlyselecttype) //用户可以指定只选择从数据库中读出某种类型的node
	{
		super();
		createEvents ();
		this.setEditable(true);
		this.setForeground(Color.RED);
		this.bkdbopt = new BanKuaiDbOperation ();
		this.onlyselectnodetype = onlyselecttype;
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
//		this.addItem(stockcode+stocname);
//		this.setSelectedItem(stockcode+stocname);
		preSearch(stock);
		updateStockCombox(stockcode+stocname);
		return nodeshouldbedisplayed;
	}
	public BkChanYeLianTreeNode updateUserSelectedNode (BanKuai bk)
	{
		this.nodeshouldbedisplayed = bk;
		String bkcode = bk.getMyOwnCode();
		String bkname = bk.getMyOwnName();
		updateStockCombox(bkcode+bkname);
		return this.nodeshouldbedisplayed;
	}
	/*
	 * 
	 */
	public BkChanYeLianTreeNode updateUserSelectedNode (String stockcode)
	{
//		this.addItem(stockcode+stocname);
		this.setSelectedItem(stockcode);
		preSearch(stockcode);
		updateStockCombox();
		return nodeshouldbedisplayed;
	}
	/*
	 * 获取用户输入的个股的基本信息 
	 */
	private void preSearch(Stock stock) 
	{
		nodeshouldbedisplayed = bkdbopt.getStockBasicInfo (stock);
		nodeshouldbedisplayed = bkdbopt.getTDXBanKuaiForAStock ((Stock)nodeshouldbedisplayed); //通达信板块信息
	}
	/*
	 * 获取用户code的板块或个股的基本信息 
	 */
	private void preSearch(String nodecode) 
	{
		 ArrayList<BkChanYeLianTreeNode> nodeslist = bkdbopt.getNodesBasicInfo (nodecode);
		 if(nodeslist.size() == 0) {
			 JOptionPane.showMessageDialog(null,"股票/板块代码不存在，请再次输入正确股票代码！");
			 return;
		 }
		 
		 if(nodeslist.size()>1) { 
			 if(this.onlyselectnodetype != null) { //用户指定了只要什么类型的node
				 for( BkChanYeLianTreeNode tmpnode : nodeslist) 
					 if(tmpnode.getType() == this.onlyselectnodetype) {
						 nodeshouldbedisplayed = tmpnode;
						 break;
					 }
				 
			 } else { //没有指定，就显示JLIST，让用户选择
				 SelectMultiNode userselection = new SelectMultiNode(nodeslist);
				 int exchangeresult = JOptionPane.showConfirmDialog(null, userselection, "请选择", JOptionPane.OK_CANCEL_OPTION);
				 if(exchangeresult == JOptionPane.CANCEL_OPTION)
						return;
				 
				 int userselected = userselection.getUserSelection();
				 nodeshouldbedisplayed = nodeslist.get(userselected);
			 }
		 } else
			 nodeshouldbedisplayed = nodeslist.get(0);
		 
		 if(nodeshouldbedisplayed.getType() == 6) { //是个股
//					if(accountschicangconfig.isSystemChiCang(stockcode)) {
//						nodeshouldbedisplayed = accountschicangconfig.setStockChiCangAccount((Stock)nodeshouldbedisplayed);
//					} 
					
//					nodeshouldbedisplayed = bkdbopt.getCheckListsXMLInfo ((Stock)nodeshouldbedisplayed);
//					nodeshouldbedisplayed = bkdbopt.getZdgzMrmcZdgzYingKuiFromDB((Stock)nodeshouldbedisplayed);
					nodeshouldbedisplayed = bkdbopt.getTDXBanKuaiForAStock ((Stock)nodeshouldbedisplayed); //通达信板块信息
					
					
//					nodeshouldbedisplayed = bkcyl.getStockChanYeLianInfo ((Stock)nodeshouldbedisplayed);
					
//					nodeshouldbedisplayed = bkcyl.getStockChanYeLianInfo ((Stock)nodeshouldbedisplayed);
		 }				
	}
	
	private void createEvents() 
	{
		this.getEditor().getEditorComponent().addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				//System.out.println("this is the test");
				
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				setEditorToNull ();
			}
			
		});
		
		this.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent  e)
			{
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
				{
				}
			}
			
		});
		
		this.addItemListener(new ItemListener() 
		{
			public void itemStateChanged(ItemEvent e)
			{
				if(e.getStateChange() == ItemEvent.SELECTED) {
					
//					statChangeActions ();
					
				}
				
				if(e.getStateChange() == ItemEvent.DESELECTED) {
				
				}
			}
						
		});
		
	}
	
	private void setEditorToNull() 
	{
		this.getEditor().setItem("");
	}

	private void statChangeActions()
	{
		String nodecode;
		try	{
			nodecode = formatStockCode((String)this.getSelectedItem());
			if(!checkCodeInputFormat(nodecode)) {
//				JOptionPane.showMessageDialog(null,"股票/板块代码有误！","Warning", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			if(this.nodeshouldbedisplayed != null && nodecode.equals( this.nodeshouldbedisplayed.getMyOwnCode() ) )
				return;
			
			preSearch(nodecode);
			updateStockCombox();
		} catch(java.lang.NullPointerException ex)	{
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "请输入股票代码！","Warning", JOptionPane.WARNING_MESSAGE);
			return;
		} catch(java.lang.StringIndexOutOfBoundsException ex2) {
//			ex2.printStackTrace();
			this.nodeshouldbedisplayed = null;
//			JOptionPane.showMessageDialog(null,"股票代码有误！");
			return;
		}
		
	}
	

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
		return stockcode.substring(0,6).trim(); 
	}
	
	private void updateStockCombox (String tmp)
	{
		boolean isaddItem = true;
		int updateItem = -1;

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
	  			  tmp = nodeshouldbedisplayed.getMyOwnCode().trim() + nodeshouldbedisplayed.getMyOwnName().trim();
	  			  this.insertItemAt(tmp,0);//插入项目tmp到0索引位置(第一列中).
	  			  this.setSelectedIndex(0);
	   	  	  }
	   	  	  if(updateItem >= 0) {
	   	  		  this.removeItemAt(updateItem);
	   	  		  tmp = nodeshouldbedisplayed.getMyOwnCode().trim() + nodeshouldbedisplayed.getMyOwnName().trim();
	  			  this.insertItemAt(tmp,0);//插入项目tmp到0索引位置(第一列中).
	  			this.setSelectedIndex(0);
	   	  	  }
	   	  }catch(NumberFormatException ne){
	   		
	   	  }
	}
	private void updateStockCombox() 
	{
		String tmp = formatStockCode((String)this.getSelectedItem());//有可能是原来输入过的，要把代码选择出来。
		updateStockCombox (tmp);
	}


}
