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
import com.exchangeinfomanager.systemconfigration.SetupSystemConfiguration;
import com.google.common.collect.Multimap;


public class JStockComboBox extends  JComboBox<String>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SetupSystemConfiguration sysconfig;
//	private BanKuaiAndStockTree bkstktree;
//	private AllCurrentTdxBKAndStoksTree allbkstock;

	public JStockComboBox() 
	{
		super();
		
		this.onlyselectnodetype = -1;
		generalSetup ();
	}
	
	public JStockComboBox(int onlyselecttype) //����ָ��ֻѡ������ݿ��ж���ĳ�����͵�node
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
	     
//	    bkstktree = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks();
		
		sysconfig = new SetupSystemConfiguration();
		
		createEvents ();
		
		jPopupMenue = new JPopupMenu();
		if(this.onlyselectnodetype != null && this.onlyselectnodetype == BkChanYeLianTreeNode.TDXBK ) {
			this.createMenuForBanKuai();
		} else
		if(this.onlyselectnodetype != null && this.onlyselectnodetype == BkChanYeLianTreeNode.TDXGG ) {
			this.createMenuForStock();
		} else
		if(this.onlyselectnodetype == -1)  {
			this.createMenuForBanKuai();
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
	public BkChanYeLianTreeNode updateUserSelectedNode (String nodecode)
	{
		BkChanYeLianTreeNode nodeshouldbedisplayed_bk = null; BkChanYeLianTreeNode nodeshouldbedisplayed_gg = null;
		if(this.onlyselectnodetype == -1) {
		 nodeshouldbedisplayed_gg = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(nodecode, BkChanYeLianTreeNode.TDXGG);
		 nodeshouldbedisplayed_bk = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(nodecode, BkChanYeLianTreeNode.TDXBK);
		} else
		if( this.onlyselectnodetype == BkChanYeLianTreeNode.TDXBK )
			nodeshouldbedisplayed_bk = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(nodecode, BkChanYeLianTreeNode.TDXBK);
		else
		if( this.onlyselectnodetype == BkChanYeLianTreeNode.TDXGG )
			nodeshouldbedisplayed_gg = CreateExchangeTree.CreateTreeOfBanKuaiAndStocks().getSpecificNodeByHypyOrCode(nodecode, BkChanYeLianTreeNode.TDXGG);
		
		BkChanYeLianTreeNode nodeshouldbedisplayed = null;
		if(nodeshouldbedisplayed_bk != null  && nodeshouldbedisplayed_gg != null) { //���͸��ɶ��иô������
			List<BkChanYeLianTreeNode> nodeslist = new ArrayList<> ();
			nodeslist.add(nodeshouldbedisplayed_bk);
			nodeslist.add(nodeshouldbedisplayed_gg);
			
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
			
		} else 
		if(nodeshouldbedisplayed_bk != null  ) {
			nodeshouldbedisplayed = nodeshouldbedisplayed_bk;
		} else
		if (nodeshouldbedisplayed_gg != null) {
			nodeshouldbedisplayed = nodeshouldbedisplayed_gg;
		} else
		if(nodeshouldbedisplayed_bk == null  && nodeshouldbedisplayed_gg == null)  { //�����Ǵ��ǻ۵İ����
			BkChanYeLianTreeNode nodeshouldbedisplayed_dzh = CreateExchangeTree.CreateTreeOfDZHBanKuaiAndStocks().getSpecificNodeByHypyOrCode(nodecode, BkChanYeLianTreeNode.TDXBK);
			if(nodeshouldbedisplayed_dzh != null)
				nodeshouldbedisplayed = nodeshouldbedisplayed_dzh;
		} else {
			JOptionPane.showMessageDialog(null, "δ�ҵ��ø��ɻ��飬���ܴ��뻹δ��ͨ����/���ǻۼ��롣");
			return null;
		}
		
		this.updateUserSelectedNode(nodeshouldbedisplayed);
//		if(nodeshouldbedisplayed.getType() == BkChanYeLianTreeNode.TDXGG)
//			this.updateUserSelectedNode((Stock)nodeshouldbedisplayed);
//		else
//			this.updateUserSelectedNode((BanKuai)nodeshouldbedisplayed);
		
		return nodeshouldbedisplayed;
	}
	
	private void createMenuForBanKuai ()
	{
		JMenuItem menuItemgjqgz = new JMenuItem("ͬ�����ڹ�ע���");
		JMenuItem menuItemgqsbk = new JMenuItem("ͬ��ǿ�ư��");
		JMenuItem menuItemrsbk = new JMenuItem("ͬ�����ư��");
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
		JMenuItem menuItemgchicang = new JMenuItem("ͬ���ֲ�");
		JMenuItem menuItemgpreviouschicang = new JMenuItem("ͬ����ʷ�ֲ�");
		JMenuItem menuItemguanzhu = new JMenuItem("ͬ�����ڹ�ע����"); 
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
		JOptionPane.showMessageDialog(null, datachoose,"ѡ���ע���ɵ�ʱ���", JOptionPane.OK_CANCEL_OPTION);
		
		Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
		setCursor(hourglassCursor);
		
		LocalDate searchstart = datachoose.getDatachoosestart();
		LocalDate searchend = datachoose.getDatachooseend();
		
		String zdyguanzhubkname = sysconfig.getCurZdyBanKuaiOfGuanZhuGeGu ();
		BanKuaiDbOperation bkdbopt = new BanKuaiDbOperation ();
		 Set<BkChanYeLianTreeNode> result = bkdbopt.sysnRecentGuanZhuGeGu (zdyguanzhubkname,searchstart,searchend);
		for(BkChanYeLianTreeNode node : result ) {
//			Stock node = (Stock)bkstktree.getSpecificNodeByHypyOrCode(geguname, BkChanYeLianTreeNode.TDXGG);
			this.updateUserSelectedNode (node);
		}
		
//		((JStockComboBoxNodeRenderer)this.getRenderer()).setGuanZhuGeGuList(result);
		
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
		JOptionPane.showMessageDialog(null, datachoose,"ѡ���ע���ɵ�ʱ���", JOptionPane.OK_CANCEL_OPTION);
		
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


