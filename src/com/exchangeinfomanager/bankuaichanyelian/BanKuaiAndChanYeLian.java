package com.exchangeinfomanager.bankuaichanyelian;

import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.StockDbOperations;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.exchangeinfomanager.tongdaxinreport.TDXFormatedOpt;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;

import net.ginkgo.dom4jcopy.SubnodeButton;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JSeparator;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;

public class BanKuaiAndChanYeLian extends JPanel 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 * @param stockInfoManager 
	 * @param bkdbopt2 
	 * @param zdgzbkxmlhandler 
	 * @param cylxmlhandler 
	 */
	public BanKuaiAndChanYeLian (StockInfoManager stockInfoManager2) 
	{
		this.stockInfoManager = stockInfoManager2;
		
		this.bkdbopt = new BanKuaiDbOperation ();
		this.cylxmhandler = new ChanYeLianXMLHandler2 ();
		this.zdgzbkxmlhandler = new TwelveZhongDianGuanZhuXmlHandler ();
		
		initializeGui ();
		initializeSysConfig ();
		zdgzbkmap = zdgzbkxmlhandler.getZdgzBanKuaiFromXmlAndCylTree(treechanyelian);
		initializeAllDaLeiZdgzTableFromXml (null);
		initializeSingleDaLeiZdgzTableFromXml (0);
		
		createEvents ();
	}
	


	public static final int UP=0, LEFT=1, RIGHT=2, DOWN=3, NONE=4;
	
	private SystemConfigration sysconfig;
	HashMap<String, ArrayList<BkChanYeLianTreeNode>> zdgzbkmap;
	private ChanYeLianXMLHandler2 cylxmhandler;
    private TwelveZhongDianGuanZhuXmlHandler zdgzbkxmlhandler;
    private BkChanYeLianTree treechanyelian;
    private BanKuaiDbOperation bkdbopt;
	private StockInfoManager stockInfoManager;
	private boolean cylneedsave; //标记产业链树有更改
	private boolean zdgzxmlneedsave; //标记重点关注有更改

    
    boolean editingNodeText = false;
	private String currentselectedtdxbk = "";

	
	private void initializeSysConfig()
	{
		sysconfig = SystemConfigration.getInstance();
	}

	/*
	 * 
	 */
	private void initializeAllDaLeiZdgzTableFromXml (String daleiname)
	{
		((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).refresh(zdgzbkmap);
		((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).fireTableDataChanged();
		
		if(daleiname == null)
			tableCurZdgzbk.setRowSelectionInterval(0,0);
		else {
			int cursorpostion = ((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).getDaLeiIndex(daleiname);
			tableCurZdgzbk.setRowSelectionInterval(cursorpostion,cursorpostion);
		}
	}
	
	/*
	 * 参数为操作后光标位置
	 */
	private void initializeSingleDaLeiZdgzTableFromXml (int row )
	{
		String selectedalei = getCurSelectedDaLei ();
		if( selectedalei != null) {
			((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).refresh(zdgzbkmap,selectedalei);
			((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).fireTableDataChanged();
		}
		
		if(((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).getRowCount() != 0)
			tableZdgzBankDetails.setRowSelectionInterval(row,row);
	}
	private String getCurSelectedDaLei ()
    {
    	int row = tableCurZdgzbk.getSelectedRow();
		if(row <0) {
			return null;
		} 
		
		 String selecteddalei = ((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).getZdgzDaLei (row);
		 tableCurZdgzbk.setRowSelectionInterval(row, row);
		 return  selecteddalei;
    }
	/*
	 * 统一界面上各个部件鼠标点击后的动作，保住一致。
	 */
	private void unifyDisplaysInDifferentCompOnGui (String selecteddalei,BkChanYeLianTreeNode bknode) 
	{
		String tdxbk = null;
		if(bknode != null) {
			tdxbk = bknode.getTdxBk();
			currentselectedtdxbk = tdxbk;
		} else
			currentselectedtdxbk = "";
		
		if(selecteddalei == null && bknode.getInZdgzCandidateCount() > 0) //通过node找到大类，可能为null
			selecteddalei = ((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).getCylNodesDaLei (bknode);
		//for 重点关注股票池表
		if(selecteddalei != null) {

			int rowInZdgz = ((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).getDaLeiIndex(selecteddalei);

			((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).fireTableDataChanged();
		 	tableCurZdgzbk.setRowSelectionInterval(rowInZdgz,rowInZdgz);
		} else //不存在任何大类里面
				((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).fireTableDataChanged();
		
		
		
		//for sub重点关注
		if( selecteddalei != null) {
				((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).refresh(zdgzbkmap,selecteddalei);
				((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).fireTableDataChanged();
				
				if( ((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).getRowCount() != 0) {
					int rowinsubzdgz = ((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).getGuanZhuBanKuaiInfoIndex (bknode);
					if(rowinsubzdgz != -1)
						tableZdgzBankDetails.setRowSelectionInterval(rowinsubzdgz,rowinsubzdgz);
					else 
						tableZdgzBankDetails.setRowSelectionInterval(0,0);
				}
			} else {
				//不存在任何大类里面
				((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).deleteAllRows ();
				((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).fireTableDataChanged();
			}
	
		//for tree
		if(bknode != null)
			treechanyelian.expandTreePathAllNode( new TreePath(bknode.getPath()) );
	   		
	    //for 个股Talble 
		if(bknode != null) {
			HashSet<String> stockinparsefile = bknode.getParseFileStockSet ();
		    
			       	//鼠标点击某个树，读出所属的通达信板块，在读出该板块的产业链子板块 
				   	DefaultListModel<String> listmodel = ((DefaultListModel<String>)lstTags.getModel());
				   	listmodel.removeAllElements();
			       	
			       	ArrayList<String> tmpsubbk = bkdbopt.getSubBanKuai (tdxbk);
			       	Collator collator = Collator.getInstance(Locale.CHINESE); //用中文排序
			   		Collections.sort(tmpsubbk,collator);
			       	for(String str:tmpsubbk)
			       		( (DefaultListModel<String>)lstTags.getModel() ) .addElement(str );
			       	
			       	//读出该板块所有的个股
			       	HashMap<String,String> tmpallbkge = bkdbopt.getTDXBanKuaiGeGuOfHyGnFg (tdxbk);
			       	//if(stockinparsefile.size() !=0 )
			       	
			       	((BanKuaiGeGuTableModel)(tablebkgegu.getModel())).deleteAllRows();
			   		((BanKuaiGeGuTableModel)(tablebkgegu.getModel())).fireTableDataChanged();
			       	((BanKuaiGeGuTableModel)(tablebkgegu.getModel())).refresh(tmpallbkge,stockinparsefile);
			       	((BanKuaiGeGuTableModel)(tablebkgegu.getModel())).fireTableDataChanged();
		}
	}


	
	/*
	 * 加子节点
	 */
	  private void addSubnodeButtonMouseMoved(java.awt.event.MouseEvent evt) //GEN-FIRST:event_addSubnodeButtonMouseMoved 
	  {
	        SubnodeButton button = (SubnodeButton) addSubnodeButton;
	        String key;
	        if (System.getProperty("os.name").startsWith("Mac OS X")) key = "CMD";
	        else key = "CTRL";
	        int x = evt.getX();
	        //System.out.print("x=" + x + " ");
	        int y = evt.getY();
	        //System.out.print(y);
	        //System.out.print("y=" + y + " ");
	        if (y<19 && x+y<30 && x<19) {
	            button.setDirection(BanKuaiAndChanYeLian.UP);
	            button.setIcon(addAboveIcon);
	            button.setToolTipText("Add above ("+key+"-UP)");
	        }
	        else if (y>=19 && x-y < 0 && x<19){
	            button.setDirection(BanKuaiAndChanYeLian.DOWN);
	            button.setIcon(addBelowIcon);
	            button.setToolTipText("Add below ("+key+"-DOWN)");
	        }
	        else if (x+y>30 && x-y>0){
	            button.setDirection(BanKuaiAndChanYeLian.RIGHT);
	            button.setIcon(addChildIcon);
	            button.setToolTipText("Add subnode ("+key+"-RIGHT)");
	        }
	        else {
	            button.setDirection(BanKuaiAndChanYeLian.NONE);
	            button.setIcon(addSubnodeIcon);
	            button.setToolTipText("Add subnode");
	        }
	   }
	
	  private void addSubnodeButtonMouseExited(java.awt.event.MouseEvent evt) //GEN-FIRST:event_addSubnodeButtonMouseExited 
	  {
	        addSubnodeButton.setIcon(addSubnodeIcon);
	        addSubnodeButton.setToolTipText("Add subnode");
	  }
	  
	  private void addSubnodeButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_addSubnodeButtonActionPerformed 
	  {
		  cylneedsave = true;
			 
			 int direction = ((SubnodeButton)addSubnodeButton).getDirection();
			 
			 if(lstTags.getSelectedValue() == null) {
				 JOptionPane.showMessageDialog(null,"请选择一个子板块!");
				 return;
			 }
				
			 String subbkname = lstTags.getSelectedValue().toString();
			 
			 treechanyelian.addNewNode (BkChanYeLianTreeNode.SUBBK,subbkname,direction);
			 
		}//GEN-LAST:event_addSubnodeButtonActionPerformed

		    
	  /*
	     * 鼠标点击某个树，读出所属的通达信板块，在读出该板块的产业链子板块 和通信性板块的相关个股
	     */
	    private void treeMousePressed(java.awt.event.MouseEvent evt) //GEN-FIRST:event_treeMousePressed 
	    {
	        TreePath closestPath = treechanyelian.getClosestPathForLocation(evt.getX(), evt.getY());
	       
	        //System.out.println(closestPath);
	        if(closestPath != null) {
	            Rectangle pathBounds = treechanyelian.getPathBounds(closestPath);
	            int maxY = (int) pathBounds.getMaxY();
	            int minX = (int) pathBounds.getMinX();
	            int maxX = (int) pathBounds.getMaxX();
	            if (evt.getY() > maxY) treechanyelian.clearSelection();
	            else if (evt.getX() < minX || evt.getX() > maxX) treechanyelian.clearSelection();
	        }
	        getReleatedInfoAndActionsForTreePathNode ( closestPath);
//	        BkChanYeLianTreeNode bknode = (BkChanYeLianTreeNode) closestPath.getLastPathComponent();
//	        unifyDisplaysInDifferentCompOnGui (null,bknode);
	               
	    }//GEN-LAST:event_treeMousePressed
	    
	    /*
	     * 和选择板块相关的子产业链，个股 
	     */
	    private void getReleatedInfoAndActionsForTreePathNode (TreePath closestPath)
	    {
	    	 BkChanYeLianTreeNode bknode = (BkChanYeLianTreeNode) closestPath.getPathComponent(1);
	    	 String tdxbk = bknode.getUserObject().toString();
	    	 HashSet<String> stockinparsefile = bknode.getParseFileStockSet ();
	         if(!tdxbk.equals(currentselectedtdxbk)) { //和当前的板块不一样，
	  	       	//鼠标点击某个树，读出所属的通达信板块，在读出该板块的产业链子板块 
	        	DefaultListModel<String> listmodel = ((DefaultListModel<String>)lstTags.getModel());
	        	listmodel.removeAllElements();
	  	       	currentselectedtdxbk = tdxbk;
	  	       	ArrayList<String> tmpsubbk = bkdbopt.getSubBanKuai (currentselectedtdxbk);
	  	       	Collator collator = Collator.getInstance(Locale.CHINESE); //用中文排序
	  	   		Collections.sort(tmpsubbk,collator);
	  	       	for(String str:tmpsubbk)
	  	       		( (DefaultListModel<String>)lstTags.getModel() ) .addElement(str );
	  	       	
	  	       	//读出该板块所有的个股
	  	       	HashMap<String,String> tmpallbkge = bkdbopt.getTDXBanKuaiGeGuOfHyGnFg (currentselectedtdxbk);
	  	       	//if(stockinparsefile.size() !=0 )
	  	       	
	  	       	((BanKuaiGeGuTableModel)(tablebkgegu.getModel())).deleteAllRows();
	  	   		((BanKuaiGeGuTableModel)(tablebkgegu.getModel())).fireTableDataChanged();
	  	       	((BanKuaiGeGuTableModel)(tablebkgegu.getModel())).refresh(tmpallbkge,stockinparsefile);
	  	       	((BanKuaiGeGuTableModel)(tablebkgegu.getModel())).fireTableDataChanged();
	  	       	
	  	       	int row = tableCurZdgzbk.getSelectedRow();
	  	       	((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).fireTableDataChanged();
	  	       	tableCurZdgzbk.setRowSelectionInterval(row,row);
	  	      
	         }
	    }
	    
	    private void addGeGuButtonMouseMoved(java.awt.event.MouseEvent evt) //GEN-FIRST:event_addSubnodeButtonMouseMoved 
	    {
	  	        SubnodeButton button = (SubnodeButton) addGeGuButton;
	  	        String key;
	  	        if (System.getProperty("os.name").startsWith("Mac OS X")) key = "CMD";
	  	        else key = "CTRL";
	  	        int x = evt.getX();
	  	        //System.out.print("x=" + x + " ");
	  	        int y = evt.getY();
	  	        //System.out.print(y);
	  	        //System.out.print("y=" + y + " ");
	  	        if (y<19 && x+y<30 && x<19) {
	  	            button.setDirection(BanKuaiAndChanYeLian.UP);
	  	            button.setIcon(addAboveIcon);
	  	            button.setToolTipText("Add above ("+key+"-UP)");
	  	        }
	  	        else if (y>=19 && x-y < 0 && x<19){
	  	            button.setDirection(BanKuaiAndChanYeLian.DOWN);
	  	            button.setIcon(addBelowIcon);
	  	            button.setToolTipText("Add below ("+key+"-DOWN)");
	  	        }
	  	        else if (x+y>30 && x-y>0){
	  	            button.setDirection(BanKuaiAndChanYeLian.RIGHT);
	  	            button.setIcon(addChildIcon);
	  	            button.setToolTipText("Add subnode ("+key+"-RIGHT)");
	  	        }
	  	        else {
	  	            button.setDirection(BanKuaiAndChanYeLian.NONE);
	  	            button.setIcon(addSubnodeIcon);
	  	            button.setToolTipText("Add subnode");
	  	        }
	  	    }
	    
	    
	    private void addGeGuButtonMouseExited(java.awt.event.MouseEvent evt)  
	    {
	    	addGeGuButton.setIcon(addSubnodeIcon);
	    	addGeGuButton.setToolTipText("添加个股");
	    }
	    
	    private void addGeGuButtonActionPerformed(java.awt.event.ActionEvent evt) 
	    {
	    	cylneedsave = true;
	    	addGeGunode(((SubnodeButton)addGeGuButton).getDirection());
	    }
	    
	    public void addGeGunode(int direction)
	    {
	   	int row = tablebkgegu.getSelectedRow();
	  		if(row <0) {
	  			JOptionPane.showMessageDialog(null,"请选择一个股票!","Warning",JOptionPane.WARNING_MESSAGE);
	  			return;
	  		}
	  		
	  		String gegucodename = ((BanKuaiGeGuTableModel)(tablebkgegu.getModel())).getStockCodeAndName(row);
	  		
	  		 treechanyelian.addNewNode (BkChanYeLianTreeNode.BKGEGU,gegucodename,direction);
	     }

	    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
			  if(treechanyelian.deleteNodes () )
				  cylneedsave = true;
		        
		    }//GEN-LAST:event_deleteButtonActionPerformed

	   
		   /*
		    * 产业链树上定位用户选择的板块，只选择到板块一级，子板块不找
		    */
		   protected TreePath findBanKuaiInTree(String bkinputed) 
		   {
		    	@SuppressWarnings("unchecked")
		    	TreePath bkpath = null ;
		    	BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)treechanyelian.getModel().getRoot();
			    Enumeration<BkChanYeLianTreeNode> e = treeroot.depthFirstEnumeration();
			    while (e.hasMoreElements()) {
			    	BkChanYeLianTreeNode node = e.nextElement();
			    	String bkHypy = node.getHanYuPingYin();
			    	String bkName = node.getUserObject().toString();
			    	
			        if (bkHypy.equalsIgnoreCase(bkinputed) || bkName.equalsIgnoreCase(bkinputed)) {
			             bkpath = new TreePath(node.getPath());
			             treechanyelian.setSelectionPath(bkpath);
			     	     treechanyelian.scrollPathToVisible(bkpath);
			     	     treechanyelian.expandTreePathAllNode(bkpath);
			     	     
			             getReleatedInfoAndActionsForTreePathNode (bkpath); //显示和板块相关的子产业链和个股
			             
			     	     return bkpath;
			        }
			    }
			    
			    return null;
			}
		    public boolean isXmlEdited ()
		    {
		    	return cylneedsave ;
		    }

		    /*
		     * 选择要分析的板块文件
		     */
		    private void selectBanKuaiParseFile ()
			{
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
				    
				    String linuxpath;
				    if(chooser.getSelectedFile().isDirectory())
				    	linuxpath = (chooser.getSelectedFile()+ "\\").replace('\\', '/');
				    else
				    	linuxpath = (chooser.getSelectedFile()).toString().replace('\\', '/');
				    
				    System.out.println(linuxpath);
				    tfldparsefilename.setText(linuxpath);
				    
//				    File recordspath = chooser.getSelectedFile();
//				    File[] filesList = recordspath.listFiles();
//			        for(File f : filesList){
//
//			            if(f.isFile() && f.getName().endsWith("txt")){
//			                ((DefaultListModel)listLeft.getModel()).addElement(f.getName());
//			            }
//			        }
//				    FileInputStream xmlfileinput = null;
//					try {
//						xmlfileinput = new FileInputStream(linuxpath);
//					} catch (FileNotFoundException e) {
//						e.printStackTrace();
//					}
					
					File parsefile = new File(linuxpath);
					List<String> readparsefileLines = null;
					try {
						readparsefileLines = Files.readLines(parsefile,Charsets.UTF_8,new ParseBanKuaiFielProcessor ());
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					HashSet<String> stockinfile = new HashSet<String> (readparsefileLines);
					
					treechanyelian.updateTreeParseFileInfo(stockinfile);
					
					((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).fireTableDataChanged();
					((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).fireTableDataChanged();
				}
				
			}   
			
			
			private void notesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_notesButtonActionPerformed
		        
		        if (notesScrollPane.isVisible()) {
		            notesScrollPane.setVisible(false);
		            //notesScrollPane.setSize(jSplitPane.getWidth()/2, jSplitPane.getHeight());
		            //this.setSize(this.getWidth() - notesScrollPane.getWidth(), this.getHeight());
		            //jSplitPane.setDividerSize(5);
		            jSplitPane.setDividerLocation(0.7);
		        }
		        else {
		            int treeWidth = treeScrollPane.getWidth(); 
		            notesScrollPane.setVisible(true);
		            //notesScrollPane.setSize(jSplitPane.getWidth()/2, jSplitPane.getHeight());
		            //this.setSize(this.getWidth() + notesScrollPane.getWidth(), this.getHeight());
		            
		            //jSplitPane.setDividerSize(5);
		            jSplitPane.setDividerLocation(0.7);
		            //jSplitPane.setDividerLocation(treeWidth);
		        }
		    }//GEN-LAST:event_notesButtonActionPerformed


		    private void notesPaneFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_notesPaneFocusLost
		        if(editingNodeText){
		            notesNode.setNoteText(notesPane.getText());
		            editingNodeText = false;
		            notesPane.setEditable(false);
		        }
		    }//GEN-LAST:event_notesPaneFocusLost

		    private void notesPaneFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_notesPaneFocusGained
		        
		        if (treechanyelian.getSelectionCount()==1 && notesNode != null){
		            treechanyelian.stopEditing();
		            editingNodeText = true;
		            notesPane.setEditable(true);
		            notesPane.getCaret().setVisible(true);
		        } else {
		            treeScrollPane.requestFocusInWindow();
		            editingNodeText = false;
		            notesPane.setEditable(false);
		        }
		    }//GEN-LAST:event_notesPaneFocusGained

		    /*
		     * 保存2个XML 
		     */
	public boolean saveCylXmlAndZdgzXml () //GEN-FIRST:event_saveButtonActionPerformed 
	{
		if(cylneedsave == true) {
			BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)treechanyelian.getModel().getRoot();
			if(!cylxmhandler.saveTreeToChanYeLianXML(treeroot) )
				JOptionPane.showMessageDialog(null, "保存产业链XML失败！请查找原因。","Warning", JOptionPane.WARNING_MESSAGE);
			
		}
		 
		if(zdgzxmlneedsave == true) {
			if( !zdgzbkxmlhandler.saveAllZdgzbkToXml () )
				JOptionPane.showMessageDialog(null, "保存重点关注股票池XML失败！请查找原因。","Warning", JOptionPane.WARNING_MESSAGE);
		}

		return true;
	}	    
	private void createEvents() 
	{
		btnopencylxml.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				cylxmhandler.openChanYeLianXmlInWinSystem ();
			}
		});
		
		btnopenzdgzxml.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent arg0) {
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				zdgzbkxmlhandler.openZdgzXmlInWinSystem ();
			}
		});
		
		btnSaveAll.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				saveCylXmlAndZdgzXml ();
			}
		});
		
		btnGenTDXCode.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				TDXFormatedOpt.parseZdgzBkToTDXCode(zdgzbkxmlhandler.getZdgzBkDetail ());
			}
		});
		
		btndeldalei.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				deleteDaLeiGuPiaoChi ();
			}
		});
		
		btnadddalei.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent arg0) 
			{
				String newdaleiname = JOptionPane.showInputDialog(null,"请输入新的股票池名称:","添加股票池", JOptionPane.QUESTION_MESSAGE);
				if( !newdaleiname.isEmpty() && !zdgzbkmap.keySet().contains(newdaleiname)) {
					ArrayList<BkChanYeLianTreeNode> tmpgzbklist = new ArrayList<BkChanYeLianTreeNode> (); 
					zdgzbkmap.put(newdaleiname, tmpgzbklist);
					
					initializeAllDaLeiZdgzTableFromXml (newdaleiname);
					initializeSingleDaLeiZdgzTableFromXml (0);
					
					zdgzxmlneedsave = true;
				} else
					JOptionPane.showMessageDialog(null,"股票池名称已经存在！","Warning",JOptionPane.WARNING_MESSAGE);
			}
		});

		
				btnChsParseFile.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) {
						selectBanKuaiParseFile ();
						
					}
				});
				
				
				tablebkgegu.addMouseListener(new MouseAdapter() {
		        	@Override
		        	public void mouseClicked(MouseEvent arg0) 
		        	{
		        		 if (arg0.getClickCount() == 2) {
							 int  view_row = tablebkgegu.rowAtPoint(arg0.getPoint()); //获得视图中的行索引
							 int  view_col = tablebkgegu.columnAtPoint(arg0.getPoint()); //获得视图中的列索引
							 int  model_row = tablebkgegu.convertRowIndexToModel(view_row);//将视图中的行索引转化为数据模型中的行索引
							 int  model_col = tablebkgegu.convertColumnIndexToModel(view_col);//将视图中的列索引转化为数据模型中的列索引
							 
							 
							 int row = tablebkgegu.getSelectedRow();
							 //int column = tblSearchResult.getSelectedColumn();
							 //String stockcode = tblSearchResult.getModel().getValueAt(row, 0).toString().trim();
							 String stockcode = tablebkgegu.getModel().getValueAt(model_row, 0).toString().trim();
							 System.out.println(stockcode);
							 stockInfoManager.getcBxstockcode().setSelectedItem(stockcode);
							 stockInfoManager.preUpdateSearchResultToGui(stockcode);
						 }
		        	}
		        });
				
				tableZdgzBankDetails.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) 
					{
						int row = tableZdgzBankDetails.getSelectedRow();
						if(row <0) {
							JOptionPane.showMessageDialog(null,"请选择一个产业链","Warning",JOptionPane.WARNING_MESSAGE);
							return;
						} 
						
						int rowInZdgz = tableCurZdgzbk.getSelectedRow();
						String zdgzdalei = ((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).getZdgzDaLei(rowInZdgz);
						
						BkChanYeLianTreeNode selectedgzbk = ((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).getGuanZhuBanKuaiInfo(row);
						
						unifyDisplaysInDifferentCompOnGui (zdgzdalei,selectedgzbk);
					}


				});
				
				btnCylRemoveFromZdgz.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) 
					{
						removeTreeChanYeLianNodeFromZdgz (arg0);
					}
				});

				
			
				btnCylAddToZdgz.addMouseListener(new MouseAdapter() {
					 @Override
					public void mouseClicked(MouseEvent arg0) 
					{
						 adddTreeChanYeLianNodeToZdgz ( arg0);
		
					}
				});

				
				
				buttonremoveoffical.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) 
					{
						removeChanYeLianFromOffical ();
					}
				});
				
				
				buttonaddofficial.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) 
					{
						addChanYeLianToOffical ();
					}
					

				});
				
				tableCurZdgzbk.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) 
					{
						int row = tableCurZdgzbk.getSelectedRow();
						if(row <0) {
							JOptionPane.showMessageDialog(null,"请选择一个大类","Warning",JOptionPane.WARNING_MESSAGE);
							return;
						} 
						
						//initializeSingleDaLeiZdgzTableFromXml (0);
						String selecteddalei = ((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).getZdgzDaLei (row);
						ArrayList<BkChanYeLianTreeNode> selectedgzbklist = ((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).getDaLeiChanYeLianList(selecteddalei);
						BkChanYeLianTreeNode selectedgzbk = null ;
						try {
							 selectedgzbk = selectedgzbklist.get(0);
						} catch ( java.lang.IndexOutOfBoundsException e) {
							
						}
						
						unifyDisplaysInDifferentCompOnGui(selecteddalei,selectedgzbk);
						

					}

//					private void unifyDisplaysInDifferentCompOnGui(BkChanYeLianTreeNode selectedgzbk,int row) 
//					{
//						treechanyelian.expandTreePathAllNode( new TreePath(selectedgzbk.getPath()) );
//						
//						//相关table高选对应的产业链
//						currentselectedtdxbk = selectedgzbk.getUserObject().toString();
//						
//						((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).fireTableDataChanged();
//						tableCurZdgzbk.setRowSelectionInterval(row,row);
//						
//						String tdxbk = selectedgzbk.getTdxBk();
//						findBanKuaiInTree(tdxbk);
//						
//					}
				});
				
				tfldfindgegu.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) {
						tfldfindgegu.setText("");
					}
				});
				
				tfldfindgegu.addActionListener(new AbstractAction() {
					public void actionPerformed(ActionEvent arg0) {
						String bkinputed = tfldfindgegu.getText();
						int rowindex = ((BanKuaiGeGuTableModel)tablebkgegu.getModel()).getStockRowIndex(bkinputed);
						tablebkgegu.setRowSelectionInterval(rowindex, rowindex);
						tablebkgegu.scrollRectToVisible(new Rectangle(tablebkgegu.getCellRect(rowindex, 0, true)));
						
					}
				});
				
				tfldfindbk.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) {
						tfldfindbk.setText("");
					}
				});
				
				tfldfindbk.addActionListener(new AbstractAction() {
					public void actionPerformed(ActionEvent arg0) {
						String bkinputed = tfldfindbk.getText();
						findBanKuaiInTree (bkinputed);
					}
				});
				
				btnfindbk.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) 
					{
						String bkinputed = tfldfindbk.getText();
						findBanKuaiInTree (bkinputed);
					}
				});
				
				addKeyListener(new KeyAdapter() {
					@Override
					public void keyTyped(KeyEvent e) 
					{
						 int id = e.getID();
						 String keyString;
					        if (id == KeyEvent.KEY_TYPED) {
					            char c = e.getKeyChar();
					            keyString = "key character = '" + c + "'";
					            System.out.println(keyString);
					        } else {
					        	int keyCode = e.getKeyCode();
					            keyString = "key code = " + keyCode
					                    + " ("
					                    + KeyEvent.getKeyText(keyCode)
					                    + ")";
					        }
					        
					}
				});
				

		        treechanyelian.addMouseListener(new java.awt.event.MouseAdapter() {
		            public void mousePressed(java.awt.event.MouseEvent evt) {
		                treeMousePressed(evt);
		            }
		        });

		        
				btnAddSubBk.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) 
					{
						String newsubbk = JOptionPane.showInputDialog(null,"请输入子板块名称:","增加子版块", JOptionPane.QUESTION_MESSAGE);
						if(newsubbk == null)
							return;
						
//						if(bkdbopt.getSysBkSet().contains(newsubbk) ) {
//							JOptionPane.showMessageDialog(null,"输入子版块名称与通达信板块名称冲突,请重新输入!");
//							return ;
//						}
						
						TreePath closestPath = treechanyelian.getSelectionPath();
				        System.out.println(closestPath);
				        String tdxbk = closestPath.getPathComponent(1).toString();
				        
						int autoIncKeyFromApi = bkdbopt.addNewSubBanKuai (newsubbk.trim(),tdxbk); 
						if(autoIncKeyFromApi>0)
							( (DefaultListModel<String>)lstTags.getModel() ) .addElement(newsubbk );
					}
				});
				
				notesButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) 
					{
						notesButtonActionPerformed(arg0);
					}
				});
				


				addGeGuButton.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			            public void mouseMoved(java.awt.event.MouseEvent evt) {
			            	addGeGuButtonMouseMoved(evt);
			            }
			        });

				addGeGuButton.addMouseListener(new java.awt.event.MouseAdapter() {
			            public void mouseExited(java.awt.event.MouseEvent evt) {
			            	addGeGuButtonMouseExited(evt);
			            }
			        });
				addGeGuButton.addActionListener(new java.awt.event.ActionListener() {
			            public void actionPerformed(java.awt.event.ActionEvent evt) {
			                addGeGuButtonActionPerformed(evt);
			            }
			        });
			

				
		        addSubnodeButton.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
		            public void mouseMoved(java.awt.event.MouseEvent evt) {
		                addSubnodeButtonMouseMoved(evt);
		            }
		        });
		        addSubnodeButton.addMouseListener(new java.awt.event.MouseAdapter() {
		            public void mouseExited(java.awt.event.MouseEvent evt) {
		                addSubnodeButtonMouseExited(evt);
		            }
		        });
		        addSubnodeButton.addActionListener(new java.awt.event.ActionListener() {
		            public void actionPerformed(java.awt.event.ActionEvent evt) {
		                addSubnodeButtonActionPerformed(evt);
		            }
		        });
		        

		        
		        deleteButton.addActionListener(new java.awt.event.ActionListener() {
		            public void actionPerformed(java.awt.event.ActionEvent evt) {
		                deleteButtonActionPerformed(evt);
		            }
		        }); 
		        

		        notesPane.addFocusListener(new java.awt.event.FocusAdapter() {
		            public void focusGained(java.awt.event.FocusEvent evt) {
		                //notesPaneFocusGained(evt);
		            }
		            public void focusLost(java.awt.event.FocusEvent evt) {
		                //notesPaneFocusLost(evt);
		            }
		        });
		        notesPane.addKeyListener(new java.awt.event.KeyAdapter() {
		            public void keyTyped(java.awt.event.KeyEvent evt) {
		               // notesPaneKeyTyped(evt);
		            }
		        });
			}
	    
	    
	private void deleteDaLeiGuPiaoChi () 
	{
		int row = tableCurZdgzbk.getSelectedRow();
		if(row <0) {
			JOptionPane.showMessageDialog(null,"请选择一个股票池","Warning",JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		String daleiname = ((ZdgzBanKuaiDetailXmlTableModel)tableCurZdgzbk.getModel()).getZdgzDaLei(row);
		int n = JOptionPane.showConfirmDialog(null, "确定删除" + daleiname + "?", "删除股票池",JOptionPane.YES_NO_OPTION);//i=0/1  
		if(n == 0) {
			ArrayList<BkChanYeLianTreeNode> daleicyl = zdgzbkxmlhandler.getASubDaiLeiDetail(daleiname);
			for(BkChanYeLianTreeNode tmpgzbkinfo : daleicyl ) {
				 treechanyelian.removeZdgzBkCylInfoFromTreeNode (tmpgzbkinfo,false);
			}
			
			zdgzbkxmlhandler.deleteDaLei(daleiname);
			
			initializeAllDaLeiZdgzTableFromXml (null);
			initializeSingleDaLeiZdgzTableFromXml (0);
			
			zdgzxmlneedsave = true;
		}
		
	}
	
    
	private void removeTreeChanYeLianNodeFromZdgz(MouseEvent arg0) 
	{
		int row = tableZdgzBankDetails.getSelectedRow();
		if(row <0) {
			JOptionPane.showMessageDialog(null,"请选择一个板块产业链","Warning",JOptionPane.WARNING_MESSAGE);
			return;
		}

		String daleiname = getCurSelectedDaLei();
		 if( daleiname != null) {
			  BkChanYeLianTreeNode gzcyl = ((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).getGuanZhuBanKuaiInfo(row);
			 
			 treechanyelian.removeZdgzBkCylInfoFromTreeNode (gzcyl,false);
			 zdgzbkxmlhandler.removeGuanZhuBanKuai(daleiname, gzcyl);
			 initializeAllDaLeiZdgzTableFromXml (daleiname);
			 initializeSingleDaLeiZdgzTableFromXml (0);
			 
			 zdgzxmlneedsave = true;
			 
		 }
		 else
			 JOptionPane.showMessageDialog(null,"请选择一个大类","Warning",JOptionPane.WARNING_MESSAGE);
		
	}

	private void addChanYeLianToOffical ()
	{
		int row = tableZdgzBankDetails.getSelectedRow();
		if(row <0) {
			JOptionPane.showMessageDialog(null,"请选择一个产业链","Warning",JOptionPane.WARNING_MESSAGE);
			return ;
		}
		
		String selectedalei = getCurSelectedDaLei ();
		if( selectedalei != null) {
			 BkChanYeLianTreeNode gzbk = ((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).getGuanZhuBanKuaiInfo(row);
			 gzbk.setOfficallySelected(true);
			 
			 treechanyelian.addZdgzBkCylInfoToTreeNode(gzbk,true);
			 initializeAllDaLeiZdgzTableFromXml (selectedalei);
			 initializeSingleDaLeiZdgzTableFromXml (row);

			 zdgzxmlneedsave = true;
		} else
			 JOptionPane.showMessageDialog(null,"请选择一个大类","Warning",JOptionPane.WARNING_MESSAGE);
	}
	private void removeChanYeLianFromOffical ()
	{
		int row = tableZdgzBankDetails.getSelectedRow();
		if(row <0) {
			JOptionPane.showMessageDialog(null,"请选择一个产业链","Warning",JOptionPane.WARNING_MESSAGE);
			return ;
		}
		
		String selectedalei = getCurSelectedDaLei ();
		if( selectedalei != null) {
			 BkChanYeLianTreeNode gzbk = ((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).getGuanZhuBanKuaiInfo(row);
			 if(!gzbk.isOfficallySelected()) {
				 JOptionPane.showMessageDialog(null,"选择的产业链只是候补关注，不在当前重点关注股票池中,不用移出！","Warning",JOptionPane.WARNING_MESSAGE);
				 return;
			 }
				 
			 if(JOptionPane.showConfirmDialog(null, "是否直接从候补产业链中删除？","Warning", JOptionPane.YES_NO_OPTION) == 0) {
					zdgzbkxmlhandler.removeGuanZhuBanKuai(selectedalei, gzbk);
					initializeSingleDaLeiZdgzTableFromXml (0);
					treechanyelian.removeZdgzBkCylInfoFromTreeNode (gzbk,false);
				} else {
					initializeSingleDaLeiZdgzTableFromXml (row);
					treechanyelian.removeZdgzBkCylInfoFromTreeNode (gzbk,true);
				}
			 initializeAllDaLeiZdgzTableFromXml(selectedalei);
			 
			 
			 
			 zdgzxmlneedsave = true;
			 
		} else
			 JOptionPane.showMessageDialog(null,"请选择一个大类","Warning",JOptionPane.WARNING_MESSAGE);
		
	}
		
	
	private void adddTreeChanYeLianNodeToZdgz(MouseEvent arg0) 
	{
		 String daleiname = getCurSelectedDaLei();
		 if( daleiname != null) {
			 TreePath path = treechanyelian.getSelectionPath();
			 BkChanYeLianTreeNode parent = (BkChanYeLianTreeNode) treechanyelian.getSelectionPath().getLastPathComponent();

			 boolean isofficallyselected = false;
			 if(JOptionPane.showConfirmDialog(null, "是否直接加入重点关注？","Warning", JOptionPane.YES_NO_OPTION) == 1) {
				 parent.setOfficallySelected(false);
			 } else {
				 parent.setOfficallySelected(true);
			 }
			 parent.setSelectedtime( formatDate(new Date() ) );
			 
			 treechanyelian.addZdgzBkCylInfoToTreeNode(parent,false);
			 zdgzbkxmlhandler.addNewGuanZhuBanKuai(daleiname, parent);
			 initializeAllDaLeiZdgzTableFromXml (daleiname);
			 int rowindex = ((CurZdgzBanKuaiTableModel)tableZdgzBankDetails.getModel()).getGuanZhuBanKuaiInfoIndex(parent);
			 initializeSingleDaLeiZdgzTableFromXml (rowindex);
			
			 zdgzxmlneedsave = true;
		 }
		 else
			 JOptionPane.showMessageDialog(null,"请选择一个大类","Warning",JOptionPane.WARNING_MESSAGE);
		 
	}
	
	private String formatDate(Date tmpdate)
	{
		SimpleDateFormat formatterhwy=new SimpleDateFormat("yy-MM-dd");
		return formatterhwy.format(tmpdate);
		//return formatterhwy;
	}


	private JTextField tfldparsefilename;
	private JTable tableCurZdgzbk;
	private JTable tableZdgzBankDetails;
	private JTable tablebkgegu;
	private JTextField tfldfindbk;
	private JTextField tfldfindgegu;
	private JButton btnCylAddToZdgz;
	private JButton btnCylRemoveFromZdgz;
	private JButton deleteButton;
	private JButton notesButton;
	private JTextPane notesPane;
	private JButton btnfindbk;
	private JButton btnfindgegu;
	private JScrollPane scrollPanegegu;
	private JList<String> lstTags;
	private JButton btnAddSubBk;
	private JButton buttonaddofficial;
	private JButton buttonremoveoffical;
	private JButton btnChsParseFile;
	private JButton addGeGuButton;
	private JButton addSubnodeButton;
	private JButton btnGenTDXCode;
	private JButton btnadddalei;
	private JButton btndeldalei;
	ImageIcon addBelowIcon, addAboveIcon, addChildIcon, addSubnodeIcon;
	private JScrollPane notesScrollPane;
	private JScrollPane treeScrollPane;
	private JSplitPane jSplitPane;
	private JButton btnSaveAll;
	private JButton btnopenzdgzxml;
	private JButton btnopencylxml;

	private void initializeGui() 
	{
		JPanel panel = new JPanel();
		
		JPanel panelzdgz = new JPanel();
		
		JPanel panelcyltree = new JPanel();
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.TRAILING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(panelzdgz, GroupLayout.PREFERRED_SIZE, 920, Short.MAX_VALUE)
						.addComponent(panelcyltree, GroupLayout.DEFAULT_SIZE, 920, Short.MAX_VALUE)
						.addComponent(panel, GroupLayout.PREFERRED_SIZE, 893, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 61, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panelzdgz, GroupLayout.PREFERRED_SIZE, 296, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panelcyltree, GroupLayout.DEFAULT_SIZE, 466, Short.MAX_VALUE)
					.addGap(76))
		);
		
		btnCylAddToZdgz = new JButton("\u52A0\u5165\u91CD\u70B9\u5173\u6CE8");
		
		btnCylRemoveFromZdgz = new JButton("\u79FB\u9664\u91CD\u70B9\u5173\u6CE8");
		
		addSubnodeButton = new SubnodeButton();
		addSubnodeButton.setIcon(new ImageIcon(BanKuaiAndChanYeLian.class.getResource("/images/subnode24.png")));
		
		addGeGuButton = new SubnodeButton();
		addGeGuButton.setIcon(new ImageIcon(BanKuaiAndChanYeLian.class.getResource("/images/subnode24.png")));
		
		btnAddSubBk = new JButton("\u589E\u52A0\u5B50\u677F\u5757");
		
		JScrollPane scrollPanesubbk = new JScrollPane();
		
		scrollPanegegu = new JScrollPane();
		
		jSplitPane = new JSplitPane();
		jSplitPane.setResizeWeight(1.0);
		
		deleteButton = new JButton("\u5220\u9664\u8282\u70B9");
		deleteButton.setIcon(null);
		
		notesButton = new JButton("\u589E\u52A0\u5907\u6CE8");
		
		tfldfindbk = new JTextField();
		tfldfindbk.setColumns(10);
		
		btnfindbk = new JButton("\u5B9A\u4F4D\u677F\u5757");
		
		tfldfindgegu = new JTextField();
		tfldfindgegu.setColumns(10);
		
		btnfindgegu = new JButton("\u5B9A\u4F4D\u4E2A\u80A1");
		
		btnopencylxml = new JButton("\u6253\u5F00XML");
		
		GroupLayout gl_panelcyltree = new GroupLayout(panelcyltree);
		gl_panelcyltree.setHorizontalGroup(
			gl_panelcyltree.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelcyltree.createSequentialGroup()
					.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelcyltree.createSequentialGroup()
							.addContainerGap()
							.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panelcyltree.createSequentialGroup()
									.addComponent(btnCylAddToZdgz)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(btnCylRemoveFromZdgz)
									.addGap(267)
									.addComponent(btnAddSubBk))
								.addGroup(gl_panelcyltree.createSequentialGroup()
									.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING)
										.addComponent(jSplitPane, GroupLayout.PREFERRED_SIZE, 379, GroupLayout.PREFERRED_SIZE)
										.addGroup(gl_panelcyltree.createSequentialGroup()
											.addComponent(deleteButton)
											.addPreferredGap(ComponentPlacement.UNRELATED)
											.addComponent(notesButton)
											.addPreferredGap(ComponentPlacement.RELATED)
											.addComponent(btnopencylxml)))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING)
										.addGroup(gl_panelcyltree.createSequentialGroup()
											.addGap(6)
											.addComponent(addGeGuButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addGap(17)
											.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING)
												.addGroup(gl_panelcyltree.createSequentialGroup()
													.addComponent(tfldfindbk, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
													.addPreferredGap(ComponentPlacement.UNRELATED)
													.addComponent(btnfindbk)
													.addGap(18)
													.addComponent(tfldfindgegu, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
													.addPreferredGap(ComponentPlacement.UNRELATED)
													.addComponent(btnfindgegu))
												.addComponent(scrollPanegegu, GroupLayout.PREFERRED_SIZE, 427, GroupLayout.PREFERRED_SIZE)))
										.addGroup(gl_panelcyltree.createSequentialGroup()
											.addGap(81)
											.addComponent(scrollPanesubbk, GroupLayout.PREFERRED_SIZE, 426, GroupLayout.PREFERRED_SIZE))))))
						.addGroup(gl_panelcyltree.createSequentialGroup()
							.addGap(395)
							.addComponent(addSubnodeButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(24, Short.MAX_VALUE))
		);
		gl_panelcyltree.setVerticalGroup(
			gl_panelcyltree.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelcyltree.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelcyltree.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnCylAddToZdgz)
						.addComponent(btnCylRemoveFromZdgz)
						.addComponent(btnAddSubBk))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelcyltree.createSequentialGroup()
							.addComponent(scrollPanesubbk, GroupLayout.PREFERRED_SIZE, 82, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(scrollPanegegu, 0, 0, Short.MAX_VALUE))
						.addComponent(jSplitPane, GroupLayout.PREFERRED_SIZE, 380, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelcyltree.createParallelGroup(Alignment.BASELINE)
							.addComponent(tfldfindbk, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(btnfindbk)
							.addComponent(tfldfindgegu, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(btnfindgegu))
						.addGroup(gl_panelcyltree.createParallelGroup(Alignment.BASELINE)
							.addComponent(deleteButton, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
							.addComponent(notesButton)
							.addComponent(btnopencylxml)))
					.addGap(12))
				.addGroup(gl_panelcyltree.createSequentialGroup()
					.addGap(69)
					.addComponent(addSubnodeButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 190, Short.MAX_VALUE)
					.addComponent(addGeGuButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(141))
		);
		
		treeScrollPane = new JScrollPane();
		jSplitPane.setLeftComponent(treeScrollPane);
		
		treechanyelian = initializeBkChanYeLianXMLTree();
		treeScrollPane.setViewportView(treechanyelian);
		
		notesScrollPane = new JScrollPane();
		jSplitPane.setRightComponent(notesScrollPane);
		
		notesPane = new JTextPane();
		notesScrollPane.setViewportView(notesPane);
		
		BanKuaiGeGuTableModel bkgegumapmdl = new BanKuaiGeGuTableModel(null,null);
		tablebkgegu = new  JTable(bkgegumapmdl)
		{
			private static final long serialVersionUID = 1L;

			public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
					 
			        Component comp = super.prepareRenderer(renderer, row, col);
			        BanKuaiGeGuTableModel tablemodel = (BanKuaiGeGuTableModel)this.getModel(); 
			        HashSet<String> stockinparsefile = tablemodel.getStockInParseFile();
			        Object value = tablemodel.getValueAt(row, col);
			        
			        if (!isRowSelected(row)) {
			        	comp.setBackground(getBackground());
			        	comp.setForeground(getForeground());
			        	int modelRow = convertRowIndexToModel(row);
			        	String stockcode = (String)getModel().getValueAt(modelRow, 0);
						if(stockinparsefile.contains(stockcode)) {
							//comp.setBackground(Color.YELLOW);
							comp.setForeground(Color.BLUE);
						}
			        }
			        
			        return comp;
			    }
				    

			
			public String getToolTipText(MouseEvent e) 
			{
                String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);

                try {
                    tip = getValueAt(rowIndex, colIndex).toString();
                } catch (RuntimeException e1) {
                	e1.printStackTrace();
                }
                return tip;
            } 
		};
		scrollPanegegu.setViewportView(tablebkgegu);
		
		lstTags = new JList<String>(new DefaultListModel<String>());
		scrollPanesubbk.setViewportView(lstTags);
		panelcyltree.setLayout(gl_panelcyltree);
		
		btnGenTDXCode = new JButton("\u751F\u6210TDX\u4EE3\u7801");
		
		JScrollPane scrollPaneDaLeiDetail = new JScrollPane();
		
		buttonaddofficial = new JButton("->");
		
		buttonremoveoffical = new JButton("<-");
		
		JScrollPane scrollPaneDaLei = new JScrollPane();
		
		btnadddalei = new JButton("\u589E\u52A0\u80A1\u7968\u6C60");
				
		btndeldalei = new JButton("\u5220\u9664\u80A1\u7968\u6C60");
		
		
		JSeparator separator_1 = new JSeparator();
		
		btnopenzdgzxml = new JButton("\u6253\u5F00XML");
		
		GroupLayout gl_panelzdgz = new GroupLayout(panelzdgz);
		gl_panelzdgz.setHorizontalGroup(
			gl_panelzdgz.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panelzdgz.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelzdgz.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelzdgz.createSequentialGroup()
							.addComponent(scrollPaneDaLeiDetail, GroupLayout.PREFERRED_SIZE, 318, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panelzdgz.createParallelGroup(Alignment.LEADING)
								.addComponent(buttonremoveoffical)
								.addComponent(buttonaddofficial)))
						.addComponent(btnGenTDXCode))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panelzdgz.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelzdgz.createSequentialGroup()
							.addComponent(btnadddalei)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btndeldalei)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btnopenzdgzxml))
						.addComponent(scrollPaneDaLei, GroupLayout.PREFERRED_SIZE, 505, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(26, Short.MAX_VALUE))
				.addGroup(gl_panelzdgz.createSequentialGroup()
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(separator_1, GroupLayout.PREFERRED_SIZE, 906, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		gl_panelzdgz.setVerticalGroup(
			gl_panelzdgz.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelzdgz.createSequentialGroup()
					.addGroup(gl_panelzdgz.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelzdgz.createSequentialGroup()
							.addGap(89)
							.addComponent(buttonaddofficial)
							.addGap(77)
							.addComponent(buttonremoveoffical))
						.addGroup(gl_panelzdgz.createSequentialGroup()
							.addContainerGap()
							.addGroup(gl_panelzdgz.createParallelGroup(Alignment.BASELINE)
								.addComponent(btnadddalei)
								.addComponent(btndeldalei)
								.addComponent(btnGenTDXCode)
								.addComponent(btnopenzdgzxml))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panelzdgz.createParallelGroup(Alignment.LEADING, false)
								.addComponent(scrollPaneDaLei, 0, 0, Short.MAX_VALUE)
								.addComponent(scrollPaneDaLeiDetail, GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE))))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(separator_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		
		//ZdgzBanKuaiDetailXmlTableModel xmlaccountsmodel = new ZdgzBanKuaiDetailXmlTableModel( );
		//tableZdgzBankDetails = new  JTable(xmlaccountsmodel)
		CurZdgzBanKuaiTableModel curzdgzbkmodel = new CurZdgzBanKuaiTableModel ();
		tableZdgzBankDetails = new  JTable(curzdgzbkmodel)
		{
			private static final long serialVersionUID = 1L;
			public String getToolTipText(MouseEvent e) 
			{
                String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);

                try {
                    tip = getValueAt(rowIndex, colIndex).toString();
                } catch (RuntimeException e1) {
                	e1.printStackTrace();
                }
                return tip;
            } 
		};
		tableZdgzBankDetails.setToolTipText("tableZdgzBankDetails");
		int preferedwidth = 170;
		tableZdgzBankDetails.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(preferedwidth);
		tableZdgzBankDetails.getTableHeader().getColumnModel().getColumn(0).setMinWidth(preferedwidth);
		tableZdgzBankDetails.getTableHeader().getColumnModel().getColumn(0).setWidth(preferedwidth);
		tableZdgzBankDetails.getTableHeader().getColumnModel().getColumn(0).setPreferredWidth(preferedwidth);

		scrollPaneDaLeiDetail.setViewportView(tableZdgzBankDetails);
		
		
		
//		CurZdgzBanKuaiTableModel curzdgzbkmodel = new CurZdgzBanKuaiTableModel (); 
//		tableCurZdgzbk = new  JTable(curzdgzbkmodel) {
		ZdgzBanKuaiDetailXmlTableModel xmlaccountsmodel = new ZdgzBanKuaiDetailXmlTableModel( );
		tableCurZdgzbk = new  JTable(xmlaccountsmodel) {
			private static final long serialVersionUID = 1L;

			public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
				
				Border outside = new MatteBorder(1, 0, 1, 0, Color.RED);
				Border inside = new EmptyBorder(0, 1, 0, 1);
				Border highlight = new CompoundBorder(outside, inside);
				 
		        Component comp = super.prepareRenderer(renderer, row, col);
		        JComponent jc = (JComponent)comp;
		        ZdgzBanKuaiDetailXmlTableModel tablemodel = (ZdgzBanKuaiDetailXmlTableModel)this.getModel(); 
		        Object value = tablemodel.getValueAt(row, col);
		        
		        if (!isRowSelected(row)) {
		        	comp.setBackground(this.getBackground()); 
		        	int modelRow = convertRowIndexToModel(row);
		        	String chanyelian = (String)getModel().getValueAt(modelRow, 1);
					if(chanyelian != null && !currentselectedtdxbk.equals("") && chanyelian.contains(currentselectedtdxbk)) 
						jc.setBorder( highlight );
		        }
		        
		        if (isRowSelected(row)) {
		        	comp.setBackground(this.getSelectionBackground());
		        	
		        	int modelRow = convertRowIndexToModel(row);
		        	String chanyelian = (String)getModel().getValueAt(modelRow, 1);
					if(chanyelian != null && !currentselectedtdxbk.equals("") && chanyelian.contains(currentselectedtdxbk)) 
						jc.setBorder( highlight );
		        	
		        }
		        
		        return comp;
		    }

			public String getToolTipText(MouseEvent e) {
                String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);

                try {
                    tip = getValueAt(rowIndex, colIndex).toString();
                } catch (RuntimeException e1) {
                    //catch null pointer exception if mouse is over an empty line
                }

                return tip;
            }
		};
		tableCurZdgzbk.setToolTipText("tableZdgzBankDetails");
		
		
		tableCurZdgzbk.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(70);
		tableCurZdgzbk.getTableHeader().getColumnModel().getColumn(0).setMinWidth(70);
		tableCurZdgzbk.getTableHeader().getColumnModel().getColumn(0).setWidth(70);
		tableCurZdgzbk.getTableHeader().getColumnModel().getColumn(0).setPreferredWidth(70);
		tableCurZdgzbk.getTableHeader().getColumnModel().getColumn(1).setMaxWidth(390);
		tableCurZdgzbk.getTableHeader().getColumnModel().getColumn(1).setMinWidth(390);
		tableCurZdgzbk.getTableHeader().getColumnModel().getColumn(1).setWidth(390);
		tableCurZdgzbk.getTableHeader().getColumnModel().getColumn(1).setPreferredWidth(390);
//		tableZdgzBankDetails.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//		tableZdgzBankDetails.setPreferredScrollableViewportSize(java.awt.Toolkit.getDefaultToolkit().getScreenSize());
		scrollPaneDaLei.setViewportView(tableCurZdgzbk);
		panelzdgz.setLayout(gl_panelzdgz);
		
		JLabel label = new JLabel("\u89E3\u6790\u677F\u5757\u6587\u4EF6");
		
		tfldparsefilename = new JTextField();
		tfldparsefilename.setColumns(10);
		
		btnChsParseFile = new JButton("\u9009\u62E9\u6587\u4EF6");
		
		JSeparator separator = new JSeparator();
		
		btnSaveAll = new JButton("\u4FDD\u5B58\u4FEE\u6539");
		
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel.createSequentialGroup()
							.addContainerGap()
							.addComponent(label)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(tfldparsefilename, GroupLayout.PREFERRED_SIZE, 565, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnChsParseFile)
							.addGap(33)
							.addComponent(btnSaveAll))
						.addComponent(separator, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 905, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(label)
						.addComponent(tfldparsefilename, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnChsParseFile, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnSaveAll))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(separator, GroupLayout.PREFERRED_SIZE, 6, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		panel.setLayout(gl_panel);
		setLayout(groupLayout);
		
		java.util.ArrayList<java.awt.Image> imageList = new java.util.ArrayList<java.awt.Image>();
        imageList.add(new javax.swing.ImageIcon(getClass().getResource("/images/ginkgo16.png")).getImage());
        imageList.add(new javax.swing.ImageIcon(getClass().getResource("/images/ginkgo18.png")).getImage());
        imageList.add(new javax.swing.ImageIcon(getClass().getResource("/images/ginkgo20.png")).getImage());
        imageList.add(new javax.swing.ImageIcon(getClass().getResource("/images/ginkgo24.png")).getImage());
        imageList.add(new javax.swing.ImageIcon(getClass().getResource("/images/ginkgo32.png")).getImage());
        imageList.add(new javax.swing.ImageIcon(getClass().getResource("/images/ginkgo36.png")).getImage());
        
        //setIconImages(imageList);
        addBelowIcon = new javax.swing.ImageIcon(getClass().getResource("/images/subnodeBelow24.png"));
        addAboveIcon = new javax.swing.ImageIcon(getClass().getResource("/images/subnodeAbove24.png"));
        addSubnodeIcon = new javax.swing.ImageIcon(getClass().getResource("/images/subnode24.png"));
        addChildIcon = new javax.swing.ImageIcon(getClass().getResource("/images/subnodeChild24.png"));
		

		
	}
	
	private  BkChanYeLianTree initializeBkChanYeLianXMLTree()
	{
        
		BkChanYeLianTreeNode topNode = cylxmhandler.getBkChanYeLianXMLTree();
		
		BkChanYeLianTree tmptreechanyelian = new BkChanYeLianTree(topNode);
		return tmptreechanyelian;
		

	}
}


/*
 * 12个大类某个具体大类的关注内容表
 */
class CurZdgzBanKuaiTableModel extends AbstractTableModel 
{
	private HashMap<String,ArrayList<BkChanYeLianTreeNode>> gzbkmap;
	private String cbxDale;

	String[] jtableTitleStrings = {  "板块产业链","创建时间","入选","发现"};
	
	CurZdgzBanKuaiTableModel ()
	{

	}

	public void refresh (HashMap<String,ArrayList<BkChanYeLianTreeNode>> zdgzbkmap2,String cbxDale2) 
	{
		this.gzbkmap =  zdgzbkmap2;
		this.cbxDale = cbxDale2;
	}
	public int getGuanZhuBanKuaiInfoIndex (BkChanYeLianTreeNode parent)
	{
		String currentdalei = this.cbxDale;
		ArrayList<BkChanYeLianTreeNode> tmpgzbkinfo = this.gzbkmap.get(currentdalei);
		
//		boolean findexsitsamenode = false;
		int findsimilarnode = -1;
		
		//for(BkChanYeLianTreeNode bkcylnode : tmpgzbkinfo) {
		for(int i=0;i<tmpgzbkinfo.size();i++) {
			String cylinmapnode = tmpgzbkinfo.get(i).getChanYeLian().trim();
			String cylinverfiednode = parent.getChanYeLian().trim();

			if(cylinmapnode.equals(cylinverfiednode) ) 
				return i;
			else if(cylinmapnode.contains(cylinverfiednode) )
				findsimilarnode = i;
		}

		return findsimilarnode;
	}
	public BkChanYeLianTreeNode getGuanZhuBanKuaiInfo (int rowindex)
	{
		String currentdalei = this.cbxDale;
		ArrayList<BkChanYeLianTreeNode> tmpgzbkinfo = this.gzbkmap.get(currentdalei);
		return tmpgzbkinfo.get(rowindex);
	}

	 public int getRowCount() 
	 {
		 try {
			 String currentdalei = this.cbxDale;  
			 ArrayList<BkChanYeLianTreeNode> tmpgzbklist = gzbkmap.get(currentdalei);
			 return tmpgzbklist.size();
		 } catch (java.lang.NullPointerException e) {
			 return 0;
		 }
	 }

	    @Override
	    public int getColumnCount() 
	    {
	        return jtableTitleStrings.length;
	    
	    } 
//	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	if(gzbkmap == null)
	    		return null;
	    	
	    	String currentdalei = this.cbxDale;  
			ArrayList<BkChanYeLianTreeNode> tmpgzbklist = gzbkmap.get(currentdalei);
			if(tmpgzbklist == null)
				return null;
			BkChanYeLianTreeNode tmpgzbk = tmpgzbklist.get(rowIndex);
	    	
	    	Object value = "??";

	    	switch (columnIndex) {
            case 0:
                value = tmpgzbk.getChanYeLian();
                break;
            case 1:
            	try {
            		value = tmpgzbk.getSelectedtime();
            	} catch (java.lang.NullPointerException e) {
            		value = "";
            	}
            	
                break;
            case 2:
                value = new Boolean(tmpgzbk.isOfficallySelected() );
                break;
            case 3:
            	if(tmpgzbk.getParseFileStockSet() != null && tmpgzbk.getParseFileStockSet().size() >0)
            		value = new Boolean(true );
            	else 
            		value = new Boolean(false );
                break;
	    	}
	    	

	    	return value;
	  }

     public Class<?> getColumnClass(int columnIndex) {
		      Class clazz = String.class;
		      switch (columnIndex) {
		      case 0:
		    	  clazz = String.class;
		    	  break;
		        case 1:
			          clazz = String.class;
			          break;
		        case 2:
			          clazz = Boolean.class;
			          break;
		        case 3:
			          clazz = Boolean.class;
			          break;
		      }
		      
		      return clazz;
		}
	    
	    public String getColumnName(int column){ 
	    	return jtableTitleStrings[column];
	    }//设置表格列名 

	    public boolean isCellEditable(int row,int column) {
	    	return false;
		}
	    
	    public void deleteAllRows()
	    {
			this.cbxDale = "";
	    }
//	    public void deleteRow(int row)
//	    {
//	    	String currentdalei = cbxDale.getSelectedItem().toString();  
//			ArrayList<GuanZhuBanKuaiInfo> tmpgzbklist = gzbkmap.get(currentdalei);
//			tmpgzbklist.remove(row);
//	    }
	    
}


/*
 * 重点关注板块表
 */
class ZdgzBanKuaiDetailXmlTableModel extends AbstractTableModel 
{
	private HashMap<String, ArrayList<BkChanYeLianTreeNode>> gzbkmap;
	
	private String[] jtableTitleStrings = { "股票池", "关注板块","发现"};
	private ArrayList<String> gzdalei;
	private boolean foundstockinparsefile = false;
	
	ZdgzBanKuaiDetailXmlTableModel ()
	{
	}

	public void refresh (HashMap<String,ArrayList<BkChanYeLianTreeNode>> zdgzbkmap) 
	{
		this.gzbkmap =  zdgzbkmap;
		this.gzdalei = new ArrayList<String>(zdgzbkmap.keySet() );
	}

	 public int getRowCount() 
	 {
		return gzdalei.size();
	 }
	 
	 public String getZdgzDaLei (int row)
	 {
		 return (String)gzdalei.get(row);
	 }
	 public int getDaLeiIndex (String dalei)
	 {
		 return this.gzdalei.indexOf(dalei);
	 }
	 public ArrayList<BkChanYeLianTreeNode>  getDaLeiChanYeLianList (String dalei)
	 {
		 return gzbkmap.get(dalei) ;
	 }

	    @Override
	    public int getColumnCount() 
	    {
	        return jtableTitleStrings.length;
	    
	    } 
//	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	if(gzbkmap == null)
	    		return null;
	    	
	    	
	    	Object value = "??";
	    	
	    	switch (columnIndex) {
            case 0:
                value = gzdalei.get(rowIndex);
                break;
            case 1:
            	String result = "";
            	try {
            		ArrayList<BkChanYeLianTreeNode> zdgzsub = this.getDaLeiChanYeLianList(this.getZdgzDaLei(rowIndex)) ;// gzbkmap.get(getZdgzDaLei(rowIndex) );
            		if(zdgzsub.size() == 0)
            			foundstockinparsefile = false;
            		
            		for(BkChanYeLianTreeNode gznode : zdgzsub) {
            			if(gznode.isOfficallySelected()) {
            				String chanyelian = gznode.getChanYeLian(); 
            				
            				String seltime = "";
            				if(gznode.getSelectedtime() != null)
            					seltime = gznode.getSelectedtime();
            				result = result + chanyelian + "(" + seltime +")" + " | " + " ";
            				
            				if(gznode.getParseFileStockSet() != null && gznode.getParseFileStockSet().size() > 0)
            					foundstockinparsefile = true;
            				else
            					foundstockinparsefile = false;
            			}
            		}

            	 } catch (java.lang.NullPointerException e) {
            		 
            	 }
            	value = result;
                break;
            case 2:
                value = new Boolean (foundstockinparsefile);
                break;
	    	}
        return value;
	  }

     public Class<?> getColumnClass(int columnIndex) {
		      Class clazz = String.class;
		      switch (columnIndex) {
		      case 0:
		    	  clazz = String.class;
		    	  break;
		        case 1:
			          clazz = String.class;
			          break;
		        case 2:
			          clazz = Boolean.class;
			          break;
		      }
		      
		      return clazz;
		}
	    
//	    @Override
//	    public Class<?> getColumnClass(int columnIndex) {
//	        return // Return the class that best represents the column...
//	    }
	    
	    public String getColumnName(int column){ 
	    	return jtableTitleStrings[column];
	    }//设置表格列名 
		

	    public boolean isCellEditable(int row,int column) {
	    	return false;
		}

		public String getCylNodesDaLei(BkChanYeLianTreeNode bknode) 
		{
			for(String daleiname : this.gzdalei) {
				ArrayList<BkChanYeLianTreeNode> zdgzsub = gzbkmap.get(daleiname );
        		if(zdgzsub.size() == 0)
        			continue;
        		
        		for(BkChanYeLianTreeNode gznode : zdgzsub) {
        			String cylinmapnode = gznode.getChanYeLian().trim();
        			String cylinverfiednode = bknode.getChanYeLian().trim();
        			
        			if(cylinmapnode.contains(cylinverfiednode) )
        				return daleiname;
        			
        		}
			}
			return null;
		}
	    
}


class BanKuaiGeGuTableModel extends AbstractTableModel 
{
	private HashMap<String,String> bkgegumap; //包含股票代码和股票名称
	String[] jtableTitleStrings = { "股票代码", "股票名称"};
	private ArrayList<String> bkgeguname;
	private HashSet<String> stockcodeinparsefile;
	
	BanKuaiGeGuTableModel (HashMap<String,String> bkgegu,HashSet<String> stockcodeinparsefile2)
	{
		if(bkgegu != null) {
			this.bkgegumap = bkgegu;
			this.bkgeguname = new ArrayList<String> (bkgegu.keySet() );
		}
		if(stockcodeinparsefile2 != null)
			this.stockcodeinparsefile = stockcodeinparsefile2;
		else 
			this.stockcodeinparsefile = new HashSet<String> ();
		
		
	}

	public void refresh  (HashMap<String,String> bkgegu,HashSet<String> stockcodeinparsefile2)
	{
		this.bkgegumap = bkgegu;
		bkgeguname = new ArrayList<String> (bkgegu.keySet() );

		Collator collator = Collator.getInstance(Locale.CHINESE); //用中文排序
 		Collections.sort(bkgeguname,collator);
		
 		this.stockcodeinparsefile = stockcodeinparsefile2;
	}
	public HashSet<String> getStockInParseFile ()
	{
		return this.stockcodeinparsefile;
	}
	 public int getRowCount() 
	 {
		 if(this.bkgegumap == null)
			 return 0;
		 else if(this.bkgegumap.isEmpty()  )
			 return 0;
		 else
			 return this.bkgegumap.size();
	 }

	    @Override
	    public int getColumnCount() 
	    {
	        return jtableTitleStrings.length;
	    } 
	    
	    public Object getValueAt(int rowIndex, int columnIndex) 
	    {
	    	if(bkgegumap.isEmpty())
	    		return null;
	    	
	    	Object value = "??";
	    	switch (columnIndex) {
            case 0:
                value = bkgeguname.get(rowIndex);
                break;
            case 1:
            	value = bkgegumap.get( bkgeguname.get(rowIndex) );
                break;
	    	}

        return value;
	  }

     public Class<?> getColumnClass(int columnIndex) {
		      Class clazz = String.class;
		      switch (columnIndex) {
		      case 0:
		    	  clazz = String.class;
		    	  break;
		        case 1:
			          clazz = String.class;
			          break;
		      }
		      
		      return clazz;
		}
	    
//	    @Override
//	    public Class<?> getColumnClass(int columnIndex) {
//	        return // Return the class that best represents the column...
//	    }
	    
	    public String getColumnName(int column){ 
	    	return jtableTitleStrings[column];
	    }//设置表格列名 
		

	    public boolean isCellEditable(int row,int column) {
	    	return false;
		}
	    public String getStockCodeAndName (int row) 
	    {
	    	return bkgeguname.get(row) + bkgegumap.get( bkgeguname.get(row) );
	    }

	    
	    public void deleteAllRows()
	    {
	    	if(this.bkgegumap == null)
				 return ;
			 else 
				 bkgegumap.clear();
	    }
	    
	    public int getStockRowIndex (String stockcode) 
	    {
	    	int index = bkgeguname.indexOf(stockcode);
	    	return index;
	    }
	    
}


/*
 * google guava files 类，可以直接处理读出的line
 */
class ParseBanKuaiFielProcessor implements LineProcessor<List<String>> 
{
   
    private List<String> stocklists = Lists.newArrayList();
   
    @Override
    public boolean processLine(String line) throws IOException {
    	if(line.trim().length() ==7)
    		stocklists.add(line.substring(1));
        return true;
    }
    @Override
    public List<String> getResult() {
        return stocklists;
    }
}

