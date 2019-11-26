package com.exchangeinfomanager.commonlib.jstockcombobox; 

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;

import java.util.Set;
import java.util.regex.Pattern;

import javax.swing.JComboBox;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.gui.subgui.DateRangeSelectPnl;
import com.exchangeinfomanager.gui.subgui.SelectMultiNode;
import com.exchangeinfomanager.nodes.BanKuai;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.Stock;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;


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
	
	public JStockComboBox(int onlyselecttype) //�û�����ָ��ֻѡ������ݿ��ж���ĳ�����͵�node
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
	     
//		this.setRenderer(new JStockComboBoxRenderer());
		

		bkdbopt = new BanKuaiDbOperation ();
		sysconfig = SystemConfigration.getInstance();
//		allbkstock = AllCurrentTdxBKAndStoksTree.getInstance();
		
		createEvents ();
	}

	private BanKuaiDbOperation bkdbopt;
	private Integer onlyselectnodetype;
	/*
	 * 
	 */
	public BkChanYeLianTreeNode getUserInputNode ()
	{
		Object selectitem = ((JStockComboBoxModel)this.getModel()).getSelectedItem(); 
		BkChanYeLianTreeNode nodeshouldbedisplayed = (BkChanYeLianTreeNode) selectitem;
		nodeshouldbedisplayed = preSearch( nodeshouldbedisplayed.getMyOwnCode(), onlyselectnodetype);
		
		return nodeshouldbedisplayed;
	}
	/*
	 * 
	 */
	public BkChanYeLianTreeNode updateUserSelectedNode (Stock stock)
	{
		preSearch(stock.getMyOwnCode(),BkChanYeLianTreeNode.TDXGG);
		
		Integer alreadyin = ((JStockComboBoxModel)getModel()).hasTheNode(stock.getMyOwnCode());
		if(alreadyin == -1) {
			((JStockComboBoxModel)getModel()).addElement( stock );
			((JStockComboBoxModel)getModel()).setSelectedItem(stock);
		}
		return stock;
	}
	public BkChanYeLianTreeNode updateUserSelectedNode (BanKuai bk)
	{
		preSearch(bk.getMyOwnCode(),BkChanYeLianTreeNode.TDXBK);
		
		Integer alreadyin = ((JStockComboBoxModel)getModel()).hasTheNode(bk.getMyOwnCode());
		if(alreadyin == -1) {
			((JStockComboBoxModel)getModel()).addElement( bk );
			((JStockComboBoxModel)getModel()).setSelectedItem(bk );
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
			((JStockComboBoxModel)getModel()).setSelectedItem(nodeshouldbedisplayed);

			return nodeshouldbedisplayed;
		} else
			return null;
	}
	/*
	 * ��ȡ�û�code�İ�����ɵĻ�����Ϣ 
	 */
	private BkChanYeLianTreeNode preSearch(String nodecode,Integer nodetype) 
	{
		 BkChanYeLianTreeNode nodeshouldbedisplayed = null;
		 ArrayList<BkChanYeLianTreeNode> nodeslist = bkdbopt.getNodesBasicInfo (nodecode);
		 if(nodeslist.size() == 0) {
			 setEditorToNull ();
			 JOptionPane.showMessageDialog(null,"��Ʊ/�����벻���ڣ����ٴ�������ȷ��Ʊ���룡");
			 return null;
		 }
		 
		 if( nodeslist.size() > 1 ) { 
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
		 
		 if(nodeshouldbedisplayed.getType() == BkChanYeLianTreeNode.TDXGG) { //�Ǹ���
//					if(accountschicangconfig.isSystemChiCang(stockcode)) {
//						nodeshouldbedisplayed = accountschicangconfig.setStockChiCangAccount((Stock)nodeshouldbedisplayed);
//					} 
					
//					nodeshouldbedisplayed = bkdbopt.getCheckListsXMLInfo ((Stock)nodeshouldbedisplayed);
//					nodeshouldbedisplayed = bkdbopt.getZdgzMrmcZdgzYingKuiFromDB((Stock)nodeshouldbedisplayed);
					nodeshouldbedisplayed = bkdbopt.getTDXBanKuaiForAStock ((Stock)nodeshouldbedisplayed); //ͨ���Ű����Ϣ
					
					
//					nodeshouldbedisplayed = bkcyl.getStockChanYeLianInfo ((Stock)nodeshouldbedisplayed);
					
//					nodeshouldbedisplayed = bkcyl.getStockChanYeLianInfo ((Stock)nodeshouldbedisplayed);
		 }	
		 
		 return nodeshouldbedisplayed;
	}
	
	private void createEvents() 
	{
		JPopupMenu jPopupMenue = new JPopupMenu();
		JMenuItem menuItemguanzhu = new JMenuItem("ͬ�����ڹ�ע����"); 
		JMenuItem menuItemgchicang = new JMenuItem("ͬ���ֲ�");
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
						node = updateUserSelectedNode (nodecode,onlyselectnodetype);

					}
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
		JOptionPane.showMessageDialog(null, datachoose,"ѡ���ע���ɵ�ʱ���", JOptionPane.OK_CANCEL_OPTION);
		
		LocalDate searchstart = datachoose.getDatachoosestart();
		LocalDate searchend = datachoose.getDatachooseend();
		
		String zdyguanzhubkname = sysconfig.getCurZdyBanKuaiOfGuanZhuGeGu ();
		Set<String> result = bkdbopt.sysnRecentGuanZhuGeGu (zdyguanzhubkname,searchstart,searchend);
		for(String geguname : result ) {
			this.updateUserSelectedNode (geguname,BkChanYeLianTreeNode.TDXGG);
		}
		
		((JStockComboBoxNodeRenderer)this.getRenderer()).setGuanZhuGeGuList(result);
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


	
	/*
	 * �Ѿ�����
	 */
//	private void updateStockCombox (String tmp)
//	{
//		boolean isaddItem = true;
////		int updateItem = -1;
//
//	   	  //�ж��û����������Ŀ�Ƿ����ظ��������ظ������ӵ�JComboBox�С�
//	   	  try{
//	   			  for(int i=0;i< this.getItemCount();i++) {
//	   				  	String curitem = this.getItemAt(i).toString();
//		   				if(curitem.equals(tmp)  ) { // �Ѿ����ˣ��������κβ���
//		   					isaddItem = false;
//		   					this.setSelectedIndex(i);
//		   					break;
//		   				}
////		   	  	  	  if(curitem.substring(0, 6).equals(tmp) && curitem.length()>6 ) { //
////		   	  	  	  	 isaddItem = false;
////		   	  	  	  	 break;
////		   	  	  	  }
//		   				String curstring = curitem.substring(0, 6);
//			   	  	  if(curitem.substring(0, 6).equals(tmp)  ) { // ���ˣ���ֻ��code,û������
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
//		  			  this.insertItemAt(tmp,0);//������Ŀtmp��0����λ��(��һ����).
//		  			  this.setSelectedIndex(0);
//	   	  		  } catch (java.lang.NullPointerException e) {
//	   	  			  
//	   	  		  }
//	  			  
//	   	  	  }
////	   	  	  if(updateItem >= 0) {
////	   	  		  this.removeItemAt(updateItem);
////	   	  		  tmp = nodeshouldbedisplayed.getMyOwnCode().trim() + nodeshouldbedisplayed.getMyOwnName().trim();
////	  			  this.insertItemAt(tmp,0);//������Ŀtmp��0����λ��(��һ����).
////	  			  this.setSelectedIndex(0);
////	   	  	  }
//	   	  }catch(NumberFormatException ne){
//	   		
//	   	  }
//	}
//	private void updateStockCombox() 
//	{
//		String tmp = formatStockCode( (String)this.getEditor().getItem() );//�п�����ԭ��������ģ�Ҫ�Ѵ���ѡ�������
//		updateStockCombox (tmp);
//	}


}


