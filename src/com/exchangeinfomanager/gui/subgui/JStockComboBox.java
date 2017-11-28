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
		this.setEditable(true);
		this.setForeground(Color.RED);
		bkdbopt = new BanKuaiDbOperation ();
		createEvents ();
	}

	private BanKuaiDbOperation bkdbopt;
	private BkChanYeLianTreeNode nodeshouldbedisplayed;
	
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
		this.addItem(stockcode+stocname);
		this.setSelectedItem(stockcode+stocname);
		preSearch(stock);
		updateStockCombox();
		return nodeshouldbedisplayed;
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
	 * ��ȡ�û�����ĸ��ɵĻ�����Ϣ 
	 */
	private void preSearch(Stock stock) 
	{
		nodeshouldbedisplayed = bkdbopt.getStockBasicInfo (stock);
		nodeshouldbedisplayed = bkdbopt.getTDXBanKuaiForAStock ((Stock)nodeshouldbedisplayed); //ͨ���Ű����Ϣ
	}
	/*
	 * ��ȡ�û�code�İ�����ɵĻ�����Ϣ 
	 */
	private void preSearch(String nodecode) 
	{
		 ArrayList<BkChanYeLianTreeNode> nodeslist = bkdbopt.getNodesBasicInfo (nodecode);
		 if(nodeslist.size() == 0) {
			 JOptionPane.showMessageDialog(null,"��Ʊ/�����벻���ڣ����ٴ�������ȷ��Ʊ���룡");
			 return;
		 }
		 
		 if(nodeslist.size()>1) { 
			 //��ʾJLIST�����û�ѡ��
			 SelectMultiNode userselection = new SelectMultiNode(nodeslist);
			 int exchangeresult = JOptionPane.showConfirmDialog(null, userselection, "��ѡ��", JOptionPane.OK_CANCEL_OPTION);
			 if(exchangeresult == JOptionPane.CANCEL_OPTION)
					return;
			 
			 int userselected = userselection.getUserSelection();
			 nodeshouldbedisplayed = nodeslist.get(userselected);
		 } else
			 nodeshouldbedisplayed = nodeslist.get(0);
		 
		 if(nodeshouldbedisplayed.getType() == 6) { //�Ǹ���
//					if(accountschicangconfig.isSystemChiCang(stockcode)) {
//						nodeshouldbedisplayed = accountschicangconfig.setStockChiCangAccount((Stock)nodeshouldbedisplayed);
//					} 
					
//					nodeshouldbedisplayed = bkdbopt.getCheckListsXMLInfo ((Stock)nodeshouldbedisplayed);
//					nodeshouldbedisplayed = bkdbopt.getZdgzMrmcZdgzYingKuiFromDB((Stock)nodeshouldbedisplayed);
					nodeshouldbedisplayed = bkdbopt.getTDXBanKuaiForAStock ((Stock)nodeshouldbedisplayed); //ͨ���Ű����Ϣ
					
					
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
				JOptionPane.showMessageDialog(null,"��Ʊ/����������","Warning", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			preSearch(nodecode);
			updateStockCombox();
		} catch(java.lang.NullPointerException ex)	{
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "�������Ʊ���룡","Warning", JOptionPane.WARNING_MESSAGE);
			return;
		} catch(java.lang.StringIndexOutOfBoundsException ex2) {
			ex2.printStackTrace();
			JOptionPane.showMessageDialog(null,"��Ʊ��������");
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
//		System.out.println(Pattern.matches("^\\d{6}{6,100}$","000123����������") );
//		System.out.println(Pattern.matches("\\d{6}[\u4E00-\u9FA5A-Za-z0-9_]$","ccccc000123abccc") );
		
		//������6λȫ���֣�������ǰ��6λ������
			if( Pattern.matches("\\d{6}$",stockcode)  || Pattern.matches("\\d{6}[\u4E00-\u9FA5A-Za-z0-9_]+$",stockcode) )
				return true;
			else return false;
	}

	private String formatStockCode (String stockcode)
	{
		return stockcode.substring(0,6).trim(); 
	}
	
	private void updateStockCombox() 
	{
		boolean isaddItem=true;
		int updateItem = -1;
		String tmp= formatStockCode((String)this.getSelectedItem());//�п�����ԭ��������ģ�Ҫ�Ѵ���ѡ�������
		

	   	  //�ж��û����������Ŀ�Ƿ����ظ��������ظ������ӵ�JComboBox�С�
	   	  try{
	   			  for(int i=0;i< this.getItemCount();i++) {
	   				  String curitem = this.getItemAt(i).toString(); 
		   	  	  	  if (curitem.substring(0, 6).equals(tmp) && curitem.length()>6 ){
		   	  	  	  	 isaddItem = false;
		   	  	  	  	 break;
		   	  	  	  }
			   	  	 if (curitem.substring(0, 6).equals(tmp) && curitem.length()>6 ){
			   	  		 updateItem = i;
		   	  	  	  	 break;
		   	  	  	  }
		   	  	  }
	   	  	  
	   	  	  
	   	  	  if (isaddItem){
	  			  tmp = nodeshouldbedisplayed.getMyOwnCode().trim() + nodeshouldbedisplayed.getMyOwnName().trim();
	  			  this.insertItemAt(tmp,0);//������Ŀtmp��0����λ��(��һ����).
	   	  	  }
	   	  	  if(updateItem >= 0) {
	   	  		  this.removeItemAt(updateItem);
	   	  		  tmp = nodeshouldbedisplayed.getMyOwnCode().trim() + nodeshouldbedisplayed.getMyOwnName().trim();
	  			  this.insertItemAt(tmp,0);//������Ŀtmp��0����λ��(��һ����).
	   	  	  }
	   	  }catch(NumberFormatException ne){
	   		
	   	  }
		
	}


}
