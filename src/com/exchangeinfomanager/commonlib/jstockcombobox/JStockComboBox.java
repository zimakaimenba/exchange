
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
	public JStockComboBox(int onlyselecttype) //�û�����ָ��ֻѡ������ݿ��ж���ĳ�����͵�node
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
//		String tmp = formatStockCode( (String)this.getEditor().getItem() );//�п�����ԭ��������ģ�Ҫ�Ѵ���ѡ�������
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
			
			if(this.onlyselectnodetype != null) { //�û�ָ����ֻҪʲô���͵�node
				preSearch(nodecode,this.onlyselectnodetype);
				if(nodeshouldbedisplayed != null && nodeshouldbedisplayed.getType() == this.onlyselectnodetype )
					updateStockCombox(nodecode);
				else 
					nodeshouldbedisplayed = null; //�����û�ָ�������ͣ�ֱ��NULL
			} else {
				preSearch(nodecode,-1);
				if(nodeshouldbedisplayed != null)
					updateStockCombox(nodecode);
			}
			
		} catch(java.lang.NullPointerException ex)	{
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "�������Ʊ���룡","Warning", JOptionPane.WARNING_MESSAGE);
			return;
		} catch(java.lang.StringIndexOutOfBoundsException ex2) {
			this.nodeshouldbedisplayed = null;
			return;
		}
	}
	/*
	 * ��ȡ�û�����ĸ��ɵĻ�����Ϣ 
	 */
//	private void preSearch(Stock stock) 
//	{
//		nodeshouldbedisplayed = bkdbopt.getStockBasicInfo (stock);
//		nodeshouldbedisplayed = bkdbopt.getTDXBanKuaiForAStock ((Stock)nodeshouldbedisplayed); //ͨ���Ű����Ϣ
//	}
	/*
	 * ��ȡ�û�code�İ�����ɵĻ�����Ϣ 
	 */
	private Integer preSearch(String nodecode,Integer nodetype) 
	{
		nodeshouldbedisplayed = null;
		 ArrayList<BkChanYeLianTreeNode> nodeslist = bkdbopt.getNodesBasicInfo (nodecode);
		 if(nodeslist.size() == 0) {
			 JOptionPane.showMessageDialog(null,"��Ʊ/�����벻���ڣ����ٴ�������ȷ��Ʊ���룡");
			 return null;
		 }
		 
		 if(nodeslist.size()>1) { 
			 if(nodetype != -1) { //�û�ָ����ֻҪʲô���͵�node
				 for( BkChanYeLianTreeNode tmpnode : nodeslist) 
					 if(tmpnode.getType() == nodetype) {
						 nodeshouldbedisplayed = tmpnode;
						 break;
					 }
				 
			 } else { //û��ָ��������ʾJLIST�����û�ѡ��
				 SelectMultiNode userselection = new SelectMultiNode(nodeslist);
				 int exchangeresult = JOptionPane.showConfirmDialog(null, userselection, "��ѡ��", JOptionPane.OK_CANCEL_OPTION);
				 if(exchangeresult == JOptionPane.CANCEL_OPTION)
						return null;
				 
				 try {
					 int userselected = userselection.getUserSelection();
					 nodeshouldbedisplayed = nodeslist.get(userselected);
				 } catch (java.lang.ArrayIndexOutOfBoundsException e) { //�û�û��ѡ��ֱ�ӻس������
					 nodeshouldbedisplayed = nodeslist.get(0);
				 }
			 }
		 } else
			 nodeshouldbedisplayed = nodeslist.get(0);
		 
		 if(nodeshouldbedisplayed.getType() == BanKuaiAndStockBasic.TDXGG) { //�Ǹ���
//					if(accountschicangconfig.isSystemChiCang(stockcode)) {
//						nodeshouldbedisplayed = accountschicangconfig.setStockChiCangAccount((Stock)nodeshouldbedisplayed);
//					} 
					
//					nodeshouldbedisplayed = bkdbopt.getCheckListsXMLInfo ((Stock)nodeshouldbedisplayed);
//					nodeshouldbedisplayed = bkdbopt.getZdgzMrmcZdgzYingKuiFromDB((Stock)nodeshouldbedisplayed);
					nodeshouldbedisplayed = bkdbopt.getTDXBanKuaiForAStock ((Stock)nodeshouldbedisplayed); //ͨ���Ű����Ϣ
					
					
//					nodeshouldbedisplayed = bkcyl.getStockChanYeLianInfo ((Stock)nodeshouldbedisplayed);
					
//					nodeshouldbedisplayed = bkcyl.getStockChanYeLianInfo ((Stock)nodeshouldbedisplayed);
		 }	
		 
		 return 1;
	}
	
	private void createEvents() 
	{
		JPopupMenu jPopupMenue = new JPopupMenu();
		JMenuItem menuItemguanzhu = new JMenuItem("ͬ�����ڹ�ע����"); 
		JMenuItem menuItemgchicang = new JMenuItem("ͬ���ֲ�");
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
		JOptionPane.showMessageDialog(null, datachoose,"ѡ���ע���ɵ�ʱ���", JOptionPane.OK_CANCEL_OPTION);
		
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
//		System.out.println(Pattern.matches("^\\d{6}{6,100}$","000123����������") );
//		System.out.println(Pattern.matches("\\d{6}[\u4E00-\u9FA5A-Za-z0-9_]$","ccccc000123abccc") );
		
		//������6λȫ���֣�������ǰ��6λ������
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

	   	  //�ж��û����������Ŀ�Ƿ����ظ��������ظ������ӵ�JComboBox�С�
	   	  try{
	   			  for(int i=0;i< this.getItemCount();i++) {
	   				  	String curitem = this.getItemAt(i).toString();
		   				if(curitem.equals(tmp)  ) { // �Ѿ����ˣ��������κβ���
		   					isaddItem = false;
		   					this.setSelectedIndex(i);
		   					break;
		   				}
//		   	  	  	  if(curitem.substring(0, 6).equals(tmp) && curitem.length()>6 ) { //
//		   	  	  	  	 isaddItem = false;
//		   	  	  	  	 break;
//		   	  	  	  }
		   				String curstring = curitem.substring(0, 6);
			   	  	  if(curitem.substring(0, 6).equals(tmp)  ) { // ���ˣ���ֻ��code,û������
			   	  		  
			   	  		isaddItem = false;
//			   	  		 updateItem = i;
		   	  	  	  	 break;
		   	  	  	  }
		   	  	  }
	   	  	  
	   	  	  
	   	  	  if (isaddItem){
	   	  		  try{
	   	  			tmp = nodeshouldbedisplayed.getMyOwnCode().trim() + nodeshouldbedisplayed.getMyOwnName().trim();
		  			  this.insertItemAt(tmp,0);//������Ŀtmp��0����λ��(��һ����).
		  			  this.setSelectedIndex(0);
	   	  		  } catch (java.lang.NullPointerException e) {
	   	  			  
	   	  		  }
	  			  
	   	  	  }
//	   	  	  if(updateItem >= 0) {
//	   	  		  this.removeItemAt(updateItem);
//	   	  		  tmp = nodeshouldbedisplayed.getMyOwnCode().trim() + nodeshouldbedisplayed.getMyOwnName().trim();
//	  			  this.insertItemAt(tmp,0);//������Ŀtmp��0����λ��(��һ����).
//	  			  this.setSelectedIndex(0);
//	   	  	  }
	   	  }catch(NumberFormatException ne){
	   		
	   	  }
	}
//	private void updateStockCombox() 
//	{
//		String tmp = formatStockCode( (String)this.getEditor().getItem() );//�п�����ԭ��������ģ�Ҫ�Ѵ���ѡ�������
//		updateStockCombox (tmp);
//	}


}


