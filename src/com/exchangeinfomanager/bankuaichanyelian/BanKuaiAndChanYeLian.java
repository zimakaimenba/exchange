package com.exchangeinfomanager.bankuaichanyelian;

import javax.swing.JPanel;

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
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.TreePath;

import com.exchangeinfomanager.bankuai.gui.GuanZhuBanKuaiInfo;

import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.StockDbOperations;
import com.exchangeinfomanager.database.TwelveZhongDianGuanZhuXmlHandler;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;

import net.ginkgo.dom4jcopy.Ginkgo2;
import net.ginkgo.dom4jcopy.SubnodeButton;

import javax.swing.JButton;
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
	public BanKuaiAndChanYeLian (StockInfoManager stockInfoManager2, BanKuaiDbOperation bkdbopt2,StockDbOperations stockdbopt, TwelveZhongDianGuanZhuXmlHandler zdgzbkxmlhandler2, ChanYeLianXMLHandler2 cylxmlhandler2) 
	{
		this.stockInfoManager = stockInfoManager2;
		this.bkdbopt = bkdbopt2;
		
		this.cylxmhandler = cylxmlhandler2;
		this.zdgzbkxmlhandler = zdgzbkxmlhandler2;
		daleidetailmap = zdgzbkxmlhandler.getZhongDianGuanZhuBanKuai ();
		
		initializeGui ();
		initializeSysConfig ();
		initializeAllDaLeiZdgzTableFromXml (null);
		initializeSingleDaLeiZdgzTableFromXml (0);
		updateZdgzInfoToBkCylTree ();
		
		createEvents ();
	}
	


	public static final int UP=0, LEFT=1, RIGHT=2, DOWN=3, NONE=4;

	private HashMap<String, ArrayList<GuanZhuBanKuaiInfo>> daleidetailmap;
	private StockInfoManager stockInfoManager;
    //javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
	//BkChanYeLianTreeNode topNode,  viewNodeParent, notesNode=null;
    //int viewNodeIndex;
    //DefaultTreeModel treeModel;
    boolean modifiedTitle = false;
    boolean editingNodeText = false;
    boolean ignoreExpansion = false;
//    int savedNodeCount = 0;
//    int currentNodeCount = 0;
    private ChanYeLianXMLHandler2 cylxmhandler;
    private TwelveZhongDianGuanZhuXmlHandler zdgzbkxmlhandler;
    private BkChanYeLianTree treechanyelian;
    BanKuaiDbOperation bkdbopt;
	private SystemConfigration sysconfig;
	private String currentselectedtdxbk;
	
	private void initializeSysConfig()
	{
		sysconfig = SystemConfigration.getInstance();
	}

	private void initializeAllDaLeiZdgzTableFromXml (String daleiname )
	{
		
		((ZdgzBanKuaiDetailXmlTableModel)tableZdgzBankDetails.getModel()).refresh(daleidetailmap);
		((ZdgzBanKuaiDetailXmlTableModel)tableZdgzBankDetails.getModel()).fireTableDataChanged();
		
		if(daleiname == null)
			tableZdgzBankDetails.setRowSelectionInterval(0,0);
		else {
			int cursorpostion = ((ZdgzBanKuaiDetailXmlTableModel)tableZdgzBankDetails.getModel()).getDaLeiIndex(daleiname);
			tableZdgzBankDetails.setRowSelectionInterval(cursorpostion,cursorpostion);
		}
	}
	
	/*
	 * 参数为操作后光标位置
	 */
	private void initializeSingleDaLeiZdgzTableFromXml (int row)
	{
		String selectedalei = getCurSelectedDaLei ();
		if( selectedalei != null) {
			((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).refresh(daleidetailmap,selectedalei);
			((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).fireTableDataChanged();
		}
		
		if(((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).getRowCount() != 0)
			tableCurZdgzbk.setRowSelectionInterval(row,row);
	}
	private String getCurSelectedDaLei ()
    {
    	int row = tableZdgzBankDetails.getSelectedRow();
		if(row <0) {
			return null;
		} 
		
		 String selecteddalei = ((ZdgzBanKuaiDetailXmlTableModel)tableZdgzBankDetails.getModel()).getZdgzDaLei (row);
		 tableZdgzBankDetails.setRowSelectionInterval(row, row);
		 return  selecteddalei;
    }
	
	/*
	 * 把重点关注的信息update到tree上去
	 */
	private void updateZdgzInfoToBkCylTree() 
	{
		//private HashMap<String, ArrayList<GuanZhuBanKuaiInfo>> daleidetailmap;
		for(ArrayList<GuanZhuBanKuaiInfo> tmpsubgzbkinfolist : daleidetailmap.values()) {
			
			for(GuanZhuBanKuaiInfo subgzbkinfo : tmpsubgzbkinfolist) {
				String bkcyl = subgzbkinfo.getBkchanyelian();
				boolean isofficallslt = subgzbkinfo.isOfficallySelected();
				treechanyelian.updateZdgzInfoToBkCylTreeNode (bkcyl,isofficallslt,true,true); //如果是offic slt，则两个都要加，否则只加一个
			}
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
			 saveButton.setEnabled(true);
			 
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
	       
	        System.out.println(closestPath);
	        if(closestPath != null) {
	            Rectangle pathBounds = treechanyelian.getPathBounds(closestPath);
	            int maxY = (int) pathBounds.getMaxY();
	            int minX = (int) pathBounds.getMinX();
	            int maxX = (int) pathBounds.getMaxX();
	            if (evt.getY() > maxY) treechanyelian.clearSelection();
	            else if (evt.getX() < minX || evt.getX() > maxX) treechanyelian.clearSelection();
	        }
	        getReleatedInfoAndActionsForTreePathNode (closestPath);
	               
	    }//GEN-LAST:event_treeMousePressed
	    
	    /*
	     * 
	     */
	    private void getReleatedInfoAndActionsForTreePathNode (TreePath closestPath)
	    {
	    	 String tdxbk = closestPath.getPathComponent(1).toString();
	         if(!tdxbk.equals(currentselectedtdxbk)) { //和当前的板块不一样，
	  	       	//鼠标点击某个树，读出所属的通达信板块，在读出该板块的产业链子板块 
	  	       	( (DefaultListModel<String>)lstTags.getModel() ) .removeAllElements();
	  	       	currentselectedtdxbk = tdxbk;
	  	       	ArrayList<String> tmpsubbk = bkdbopt.getSubBanKuai (currentselectedtdxbk);
	  	       	Collator collator = Collator.getInstance(Locale.CHINESE); //用中文排序
	  	   		Collections.sort(tmpsubbk,collator);
	  	       	for(String str:tmpsubbk)
	  	       		( (DefaultListModel<String>)lstTags.getModel() ) .addElement(str );
	  	       	
	  	       	
	  	       	((BanKuaiGeGuTableModel)(tablebkgegu.getModel())).deleteAllRows();
	  	   		((BanKuaiGeGuTableModel)(tablebkgegu.getModel())).fireTableDataChanged();
	  	
	  	   		HashMap<String,String> tmpallbkge = bkdbopt.getTDXBanKuaiGeGuOfHyGnFg (currentselectedtdxbk);
	  	       	((BanKuaiGeGuTableModel)(tablebkgegu.getModel())).refresh(tmpallbkge);
	  	       	((BanKuaiGeGuTableModel)(tablebkgegu.getModel())).fireTableDataChanged();
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
	  	            button.setDirection(Ginkgo2.UP);
	  	            button.setIcon(addAboveIcon);
	  	            button.setToolTipText("Add above ("+key+"-UP)");
	  	        }
	  	        else if (y>=19 && x-y < 0 && x<19){
	  	            button.setDirection(Ginkgo2.DOWN);
	  	            button.setIcon(addBelowIcon);
	  	            button.setToolTipText("Add below ("+key+"-DOWN)");
	  	        }
	  	        else if (x+y>30 && x-y>0){
	  	            button.setDirection(Ginkgo2.RIGHT);
	  	            button.setIcon(addChildIcon);
	  	            button.setToolTipText("Add subnode ("+key+"-RIGHT)");
	  	        }
	  	        else {
	  	            button.setDirection(Ginkgo2.NONE);
	  	            button.setIcon(addSubnodeIcon);
	  	            button.setToolTipText("Add subnode");
	  	        }
	  	    }//GEN-LAST:event_addSubnodeButtonMouseMoved
	    private void addGeGuButtonMouseExited(java.awt.event.MouseEvent evt) //GEN-FIRST:event_addSubnodeButtonMouseExited 
	    {
	    	addGeGuButton.setIcon(addSubnodeIcon);
	    	addGeGuButton.setToolTipText("添加个股");
	    }
	    
	    private void addGeGuButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_addSubnodeButtonActionPerformed 
	    {
	    	saveButton.setEnabled(true);
	    	addGeGunode(((SubnodeButton)addGeGuButton).getDirection());
	    }//GEN-LAST:event_addSubnodeButtonActionPerformed
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
				  saveButton.setEnabled(true);
		        
		    }//GEN-LAST:event_deleteButtonActionPerformed

		   public boolean saveButtonActionPerformed (java.awt.event.ActionEvent evt) //GEN-FIRST:event_saveButtonActionPerformed 
		   {
			   BkChanYeLianTreeNode treeroot = (BkChanYeLianTreeNode)treechanyelian.getModel().getRoot();
				cylxmhandler.saveTreeToChanYeLianXML(treeroot);
				saveButton.setEnabled(false); 

				return false;
		   }

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
			    	
			    	TreePath path = new TreePath(node.getPath());
			    	String chanyelian = "";
			    	for(int i=1;i<path.getPathCount()-1;i++) {
						 chanyelian = chanyelian + path.getPathComponent(i).toString() + "->";
					 }
			    	System.out.println("tree path=" + chanyelian);
			        if (bkHypy.equalsIgnoreCase(bkinputed) || bkName.equalsIgnoreCase(bkinputed)) {
			             bkpath = new TreePath(node.getPath());
			             getReleatedInfoAndActionsForTreePathNode (bkpath);
			             treechanyelian.setSelectionPath(bkpath);
			     	     treechanyelian.scrollPathToVisible(bkpath);
			     	     return bkpath;
			        }
			    }
			    
			    return null;
			}
		    public boolean isXmlEdited ()
		    {
		    	return saveButton.isEnabled();
		    }

		    private void selectBanKuaiParseFile ()
			{
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
				    System.out.println(chooser.getSelectedFile());
				    String linuxpath = (chooser.getSelectedFile()+ "\\").replace('\\', '/');
				    System.out.println(linuxpath);
				    tfldparsefilename.setText(linuxpath);
				    
				    FileInputStream xmlfileinput = null;
					try {
						xmlfileinput = new FileInputStream(linuxpath);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					
					File parsefile = new File(linuxpath);
					treechanyelian.updateTreeParseFileInfo(parsefile);
				}
				
			}   
			
			private void chanYeLianAddOpertaions (GuanZhuBanKuaiInfo gzbk) 
			{
				String daleiname = getCurSelectedDaLei();
				String chanyelian = gzbk.getBkchanyelian();
					 
					 initializeAllDaLeiZdgzTableFromXml (daleiname);
					 int rowindex = ((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).getGuanZhuBanKuaiInfoIndex(gzbk);
					 initializeSingleDaLeiZdgzTableFromXml (rowindex);
					 treechanyelian.updateZdgzInfoToBkCylTreeNode (chanyelian,true,false,true);
				     btnSaveZdgz.setEnabled(true);
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

	    
	private void createEvents() 
	{
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
				
				tableCurZdgzbk.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) 
					{
						int row = tableCurZdgzbk.getSelectedRow();
						if(row <0) {
							JOptionPane.showMessageDialog(null,"请选择一个产业链","Warning",JOptionPane.WARNING_MESSAGE);
							return;
						} 
						
						GuanZhuBanKuaiInfo selectedgzbk = ((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).getGuanZhuBanKuaiInfo(row);
						String tdxbk = selectedgzbk.getTdxbk();
						findBanKuaiInTree(tdxbk);
					}
				});
				
				btnCylRemoveFromZdgz.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) 
					{

						int row = tableCurZdgzbk.getSelectedRow();
						if(row <0) {
							JOptionPane.showMessageDialog(null,"请选择产业链","Warning",JOptionPane.WARNING_MESSAGE);
							return;
						}

						String daleiname = getCurSelectedDaLei();
						 if( daleiname != null) {
							 GuanZhuBanKuaiInfo gzcyl = ((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).getGuanZhuBanKuaiInfo(row);
							 String gzcylinfo = gzcyl.getBkchanyelian();
							 zdgzbkxmlhandler.removeGuanZhuBanKuai(daleiname, gzcyl);
							 
							 initializeAllDaLeiZdgzTableFromXml (daleiname);
							 initializeSingleDaLeiZdgzTableFromXml (0);
							 
							 treechanyelian.updateZdgzInfoToBkCylTreeNode (gzcylinfo,gzcyl.isOfficallySelected(),true ,false);
							 
							 btnSaveZdgz.setEnabled(true);
							 
						 }
						 else
							 JOptionPane.showMessageDialog(null,"请选择一个大类","Warning",JOptionPane.WARNING_MESSAGE);
						 
					    
					}
				});

				
			
				btnCylAddToZdgz.addMouseListener(new MouseAdapter() {
					 @Override
					public void mouseClicked(MouseEvent arg0) 
					{
						 TreePath path = treechanyelian.getSelectionPath();
						 BkChanYeLianTreeNode parent = (BkChanYeLianTreeNode) treechanyelian.getSelectionPath().getLastPathComponent();
						 
						 GuanZhuBanKuaiInfo tmpgzbk = new GuanZhuBanKuaiInfo ();
						 String tdxbk = path.getPathComponent(1).toString();
						 String chanyelian = "";
						 for(int i=1;i<path.getPathCount();i++)  //不是个股，一直保存到底
							 chanyelian = chanyelian + path.getPathComponent(i).toString() + "->";
						 System.out.println(chanyelian);
						 
						 tmpgzbk.setSelectedtime(new Date ());
						 tmpgzbk.setBkchanyelian(chanyelian.substring(0, chanyelian.length()-2)); //去掉最后的->
						 tmpgzbk.setTdxbk(tdxbk);
						 if(JOptionPane.showConfirmDialog(null, "是否直接加入重点关注？","Warning", JOptionPane.YES_NO_OPTION) == 1) {
							 tmpgzbk.setOfficallySelected(false);
							 //treechanyelian.updateZdgzInfoToBkCylTreeNode (chanyelian,tmpgzbk.isOfficallySelected() ,true);
						 } else {
							 tmpgzbk.setOfficallySelected(true);
//							 treechanyelian.updateZdgzInfoToBkCylTreeNode (chanyelian,tmpgzbk.isOfficallySelected() ,true);
						 }

						 String daleiname = getCurSelectedDaLei();
						 if( daleiname != null) {
							 zdgzbkxmlhandler.addNewGuanZhuBanKuai(daleiname, tmpgzbk);
							 
							 initializeAllDaLeiZdgzTableFromXml (daleiname);
							 int rowindex = ((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).getGuanZhuBanKuaiInfoIndex(tmpgzbk);
							 initializeSingleDaLeiZdgzTableFromXml (rowindex);
							 if(tmpgzbk.isOfficallySelected())
								 treechanyelian.updateZdgzInfoToBkCylTreeNode (chanyelian,true,true ,true);
							 else 
								 treechanyelian.updateZdgzInfoToBkCylTreeNode (chanyelian,false,true ,true);

							 btnSaveZdgz.setEnabled(true);
						 }
						 else
							 JOptionPane.showMessageDialog(null,"请选择一个大类","Warning",JOptionPane.WARNING_MESSAGE);
					}
				});

				
				btnSaveZdgz.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) 
					{
						if( zdgzbkxmlhandler.saveAllZdgzbkToXml () )
							btnSaveZdgz.setEnabled(false);
						else
							JOptionPane.showMessageDialog(null, "保存XML失败！请查找原因。","Warning", JOptionPane.WARNING_MESSAGE);
					}
				});
				
				buttonremoveoffical.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) 
					{
						int row = tableCurZdgzbk.getSelectedRow();
						if(row <0) {
							JOptionPane.showMessageDialog(null,"请选择一个产业链","Warning",JOptionPane.WARNING_MESSAGE);
							return ;
						}
						
						String selectedalei = getCurSelectedDaLei ();
						if( selectedalei != null) {
							 GuanZhuBanKuaiInfo gzbk = ((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).getGuanZhuBanKuaiInfo(row);
							 gzbk.setOfficallySelected(false);
							 initializeAllDaLeiZdgzTableFromXml(selectedalei);
							 
							 String chanyelian = gzbk.getBkchanyelian();
							 if(JOptionPane.showConfirmDialog(null, "是否直接从候补产业链中删除？","Warning", JOptionPane.YES_NO_OPTION) == 0) {
									zdgzbkxmlhandler.removeGuanZhuBanKuai(selectedalei, gzbk);
									initializeSingleDaLeiZdgzTableFromXml (1);
									treechanyelian.updateZdgzInfoToBkCylTreeNode (chanyelian,true,true,false);
								} else {
									initializeSingleDaLeiZdgzTableFromXml (row);
									treechanyelian.updateZdgzInfoToBkCylTreeNode (chanyelian,true,false,false);
								}
								
								btnSaveZdgz.setEnabled(true);
						} else
							 JOptionPane.showMessageDialog(null,"请选择一个大类","Warning",JOptionPane.WARNING_MESSAGE);
					}
				});
				
				
				buttonaddofficial.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) 
					{
						int row = tableCurZdgzbk.getSelectedRow();
						if(row <0) {
							JOptionPane.showMessageDialog(null,"请选择一个产业链","Warning",JOptionPane.WARNING_MESSAGE);
							return ;
						}
						
						String selectedalei = getCurSelectedDaLei ();
						if( selectedalei != null) {
							 GuanZhuBanKuaiInfo gzbk = ((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).getGuanZhuBanKuaiInfo(row);
							 String chanyelian = gzbk.getBkchanyelian();
							 gzbk.setOfficallySelected(true);
							 
							 	initializeAllDaLeiZdgzTableFromXml (selectedalei);
								initializeSingleDaLeiZdgzTableFromXml (row);
								treechanyelian.updateZdgzInfoToBkCylTreeNode (chanyelian,true,false,true);
								
								btnSaveZdgz.setEnabled(true);
						} else
							 JOptionPane.showMessageDialog(null,"请选择一个大类","Warning",JOptionPane.WARNING_MESSAGE);
					}
				});
				
				tableZdgzBankDetails.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent arg0) 
					{
						int row = tableZdgzBankDetails.getSelectedRow();
						if(row <0) {
							JOptionPane.showMessageDialog(null,"请选择一个大类","Warning",JOptionPane.WARNING_MESSAGE);
							return;
						} 
						
						initializeSingleDaLeiZdgzTableFromXml (0);
//						 String selecteddalei = ((ZdgzBanKuaiDetailXmlTableModel)tableZdgzBankDetails.getModel()).getZdgzDaLei (row);
//						 ((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).refresh(zdgzbkxmlhandler.getZhongDianGuanZhuBanKuai (),selecteddalei);
//						 ((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).fireTableDataChanged();
//						 
						 
//						 cbxDale.setSelectedItem(selecteddalei);
					}
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
		        
		        saveButton.addActionListener(new java.awt.event.ActionListener() {
		            public void actionPerformed(java.awt.event.ActionEvent evt) {
		                saveButtonActionPerformed(evt);
		            }
		        });
		        notesPane.addFocusListener(new java.awt.event.FocusAdapter() {
		            public void focusGained(java.awt.event.FocusEvent evt) {
		                notesPaneFocusGained(evt);
		            }
		            public void focusLost(java.awt.event.FocusEvent evt) {
		                notesPaneFocusLost(evt);
		            }
		        });
		        notesPane.addKeyListener(new java.awt.event.KeyAdapter() {
		            public void keyTyped(java.awt.event.KeyEvent evt) {
		                notesPaneKeyTyped(evt);
		            }
		        });
			}
			
    
	    
	    
	    
	private JTextField tfldparsefilename;
	private JTable tableZdgzBankDetails;
	private JTable tableCurZdgzbk;
	private JTable tablebkgegu;
	private JTextField tfldfindbk;
	private JTextField tfldfindgegu;
	private JButton btnCylAddToZdgz;
	private JButton btnCylRemoveFromZdgz;
	private JButton saveButton;
	private JButton deleteButton;
	private JButton notesButton;
	private JTextPane notesPane;
	private JButton btnfindbk;
	private JButton btnfindgegu;
	private JScrollPane scrollPanegegu;
	private JList lstTags;
	private JButton btnAddSubBk;
	private JButton buttonaddofficial;
	private JButton buttonremoveoffical;
	private JButton btnChsParseFile;
	private JButton addGeGuButton;
	private JButton addSubnodeButton;
	private JButton btnSaveZdgz;
	private JButton btnGenTDXCode;
	private JButton btnadddalei;
	private JButton btndeldalei;
	ImageIcon addBelowIcon, addAboveIcon, addChildIcon, addSubnodeIcon;
	private JScrollPane notesScrollPane;
	private JScrollPane treeScrollPane;
	private JSplitPane jSplitPane;

	private void initializeGui() 
	{
		JPanel panel = new JPanel();
		
		JPanel panelzdgz = new JPanel();
		
		JPanel panelcyltree = new JPanel();
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(panelcyltree, GroupLayout.DEFAULT_SIZE, 920, Short.MAX_VALUE)
						.addComponent(panelzdgz, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 920, Short.MAX_VALUE)
						.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
							.addGap(10)
							.addComponent(panel, GroupLayout.PREFERRED_SIZE, 883, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 61, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panelzdgz, GroupLayout.PREFERRED_SIZE, 362, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panelcyltree, GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
					.addContainerGap())
		);
		
		btnCylAddToZdgz = new JButton("\u52A0\u5165\u91CD\u70B9\u5173\u6CE8");
		
		btnCylRemoveFromZdgz = new JButton("\u79FB\u9664\u91CD\u70B9\u5173\u6CE8");
		
		addSubnodeButton = new JButton("");
		addSubnodeButton.setIcon(new ImageIcon(BanKuaiAndChanYeLian2.class.getResource("/images/subnode24.png")));
		
		addGeGuButton = new JButton("");
		addGeGuButton.setIcon(new ImageIcon(BanKuaiAndChanYeLian2.class.getResource("/images/subnode24.png")));
		
		btnAddSubBk = new JButton("\u589E\u52A0\u5B50\u677F\u5757");
		
		JScrollPane scrollPanesubbk = new JScrollPane();
		
		scrollPanegegu = new JScrollPane();
		
		jSplitPane = new JSplitPane();
		
		saveButton = new JButton("\u4FDD\u5B58\u4EA7\u4E1A\u94FE\u6811");
		saveButton.setEnabled(false);
		
		deleteButton = new JButton("\u5220\u9664\u8282\u70B9");
		deleteButton.setIcon(null);
		
		notesButton = new JButton("\u589E\u52A0\u5907\u6CE8");
		
		tfldfindbk = new JTextField();
		tfldfindbk.setColumns(10);
		
		btnfindbk = new JButton("\u5B9A\u4F4D\u677F\u5757");
		
		tfldfindgegu = new JTextField();
		tfldfindgegu.setColumns(10);
		
		btnfindgegu = new JButton("\u5B9A\u4F4D\u4E2A\u80A1");
		GroupLayout gl_panelcyltree = new GroupLayout(panelcyltree);
		gl_panelcyltree.setHorizontalGroup(
			gl_panelcyltree.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelcyltree.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelcyltree.createSequentialGroup()
							.addComponent(jSplitPane, GroupLayout.PREFERRED_SIZE, 379, GroupLayout.PREFERRED_SIZE)
							.addGap(17)
							.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panelcyltree.createSequentialGroup()
									.addComponent(addSubnodeButton)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(scrollPanesubbk, GroupLayout.PREFERRED_SIZE, 426, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_panelcyltree.createSequentialGroup()
									.addComponent(addGeGuButton)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING)
										.addGroup(gl_panelcyltree.createSequentialGroup()
											.addComponent(tfldfindbk, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(ComponentPlacement.UNRELATED)
											.addComponent(btnfindbk)
											.addGap(18)
											.addComponent(tfldfindgegu, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
											.addPreferredGap(ComponentPlacement.UNRELATED)
											.addComponent(btnfindgegu))
										.addComponent(scrollPanegegu, GroupLayout.PREFERRED_SIZE, 427, GroupLayout.PREFERRED_SIZE)))))
						.addGroup(gl_panelcyltree.createSequentialGroup()
							.addComponent(btnCylAddToZdgz)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btnCylRemoveFromZdgz)
							.addGap(267)
							.addComponent(btnAddSubBk))
						.addGroup(gl_panelcyltree.createSequentialGroup()
							.addComponent(saveButton)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(deleteButton)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(notesButton)))
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
					.addGroup(gl_panelcyltree.createParallelGroup(Alignment.LEADING, false)
						.addGroup(gl_panelcyltree.createSequentialGroup()
							.addComponent(scrollPanesubbk, GroupLayout.PREFERRED_SIZE, 82, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(scrollPanegegu, 0, 0, Short.MAX_VALUE))
						.addComponent(jSplitPane, GroupLayout.PREFERRED_SIZE, 416, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panelcyltree.createParallelGroup(Alignment.BASELINE)
						.addComponent(saveButton)
						.addComponent(deleteButton, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
						.addComponent(notesButton)
						.addComponent(tfldfindbk, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnfindbk)
						.addComponent(tfldfindgegu, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnfindgegu))
					.addGap(12))
				.addGroup(gl_panelcyltree.createSequentialGroup()
					.addGap(67)
					.addComponent(addSubnodeButton)
					.addPreferredGap(ComponentPlacement.RELATED, 218, Short.MAX_VALUE)
					.addComponent(addGeGuButton)
					.addGap(139))
		);
		
		treeScrollPane = new JScrollPane();
		jSplitPane.setLeftComponent(treeScrollPane);
		
		treechanyelian = initializeBkChanYeLianXMLTree();
		treeScrollPane.setViewportView(treechanyelian);
		
		notesScrollPane = new JScrollPane();
		jSplitPane.setRightComponent(notesScrollPane);
		
		notesPane = new JTextPane();
		notesScrollPane.setViewportView(notesPane);
		
		BanKuaiGeGuTableModel bkgegumapmdl = new BanKuaiGeGuTableModel(null);
		tablebkgegu = new  JTable(bkgegumapmdl)
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

		scrollPanegegu.setViewportView(tablebkgegu);
		
		lstTags = new JList();
		scrollPanesubbk.setViewportView(lstTags);
		panelcyltree.setLayout(gl_panelcyltree);
		
		btnSaveZdgz = new JButton("\u4FDD\u5B58\u91CD\u70B9\u5173\u6CE8");
		
		btnGenTDXCode = new JButton("\u751F\u6210TDX\u4EE3\u7801");
		
		JScrollPane scrollPaneDaLeiDetail = new JScrollPane();
		
		buttonaddofficial = new JButton("->");
		
		buttonremoveoffical = new JButton("<-");
		
		JScrollPane scrollPaneDaLei = new JScrollPane();
		
		btnadddalei = new JButton("\u589E\u52A0\u80A1\u7968\u6C60");
		
		btndeldalei = new JButton("\u5220\u9664\u80A1\u7968\u6C60");
		
		JSeparator separator_1 = new JSeparator();
		GroupLayout gl_panelzdgz = new GroupLayout(panelzdgz);
		gl_panelzdgz.setHorizontalGroup(
			gl_panelzdgz.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelzdgz.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelzdgz.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelzdgz.createSequentialGroup()
							.addComponent(scrollPaneDaLeiDetail, GroupLayout.PREFERRED_SIZE, 318, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panelzdgz.createParallelGroup(Alignment.LEADING)
								.addComponent(buttonremoveoffical)
								.addComponent(buttonaddofficial)))
						.addGroup(gl_panelzdgz.createSequentialGroup()
							.addComponent(btnSaveZdgz)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnGenTDXCode)))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panelzdgz.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelzdgz.createSequentialGroup()
							.addComponent(btnadddalei)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btndeldalei))
						.addComponent(scrollPaneDaLei, GroupLayout.PREFERRED_SIZE, 505, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(12, Short.MAX_VALUE))
				.addGroup(Alignment.TRAILING, gl_panelzdgz.createSequentialGroup()
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
								.addComponent(btnSaveZdgz)
								.addComponent(btnGenTDXCode)
								.addComponent(btnadddalei)
								.addComponent(btndeldalei))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panelzdgz.createParallelGroup(Alignment.LEADING)
								.addComponent(scrollPaneDaLei, GroupLayout.PREFERRED_SIZE, 308, GroupLayout.PREFERRED_SIZE)
								.addComponent(scrollPaneDaLeiDetail, GroupLayout.PREFERRED_SIZE, 308, GroupLayout.PREFERRED_SIZE))))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(separator_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		
		CurZdgzBanKuaiTableModel curzdgzbkmodel = new CurZdgzBanKuaiTableModel (); 
		tableCurZdgzbk = new  JTable(curzdgzbkmodel)
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
		int preferedwidth = 170;
		tableCurZdgzbk.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(preferedwidth);
		tableCurZdgzbk.getTableHeader().getColumnModel().getColumn(0).setMinWidth(preferedwidth);
		tableCurZdgzbk.getTableHeader().getColumnModel().getColumn(0).setWidth(preferedwidth);
		tableCurZdgzbk.getTableHeader().getColumnModel().getColumn(0).setPreferredWidth(preferedwidth);

		scrollPaneDaLei.setViewportView(tableCurZdgzbk);
		
		ZdgzBanKuaiDetailXmlTableModel xmlaccountsmodel = new ZdgzBanKuaiDetailXmlTableModel( );
		tableZdgzBankDetails = new JTable(xmlaccountsmodel){

			private static final long serialVersionUID = 1L;

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
		
		
		tableZdgzBankDetails.getTableHeader().getColumnModel().getColumn(0).setMaxWidth(100);
		tableZdgzBankDetails.getTableHeader().getColumnModel().getColumn(0).setMinWidth(100);
		tableZdgzBankDetails.getTableHeader().getColumnModel().getColumn(0).setWidth(100);
		tableZdgzBankDetails.getTableHeader().getColumnModel().getColumn(0).setPreferredWidth(100);
//		tableZdgzBankDetails.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//		tableZdgzBankDetails.setPreferredScrollableViewportSize(java.awt.Toolkit.getDefaultToolkit().getScreenSize());
		scrollPaneDaLeiDetail.setViewportView(tableZdgzBankDetails);
		panelzdgz.setLayout(gl_panelzdgz);
		
		JLabel label = new JLabel("\u89E3\u6790\u677F\u5757\u6587\u4EF6");
		
		tfldparsefilename = new JTextField();
		tfldparsefilename.setColumns(10);
		
		btnChsParseFile = new JButton("\u9009\u62E9\u6587\u4EF6");
		
		JSeparator separator = new JSeparator();
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(label)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(tfldparsefilename, GroupLayout.PREFERRED_SIZE, 565, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnChsParseFile)
					.addContainerGap(67, Short.MAX_VALUE))
				.addGroup(Alignment.TRAILING, gl_panel.createSequentialGroup()
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(separator, GroupLayout.PREFERRED_SIZE, 895, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
						.addComponent(label)
						.addComponent(tfldparsefilename, GroupLayout.PREFERRED_SIZE, 29, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnChsParseFile, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(separator, GroupLayout.PREFERRED_SIZE, 6, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		panel.setLayout(gl_panel);
		setLayout(groupLayout);

		
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
	private HashMap<String,ArrayList<GuanZhuBanKuaiInfo>> gzbkmap;
	private String cbxDale;

	String[] jtableTitleStrings = {  "板块产业链","创建时间","入选"};
	
	CurZdgzBanKuaiTableModel ()
	{
//		this.gzbkmap =  gzbkmap;
//		this.cbxDale = cbxDale2;
	}

	public void refresh (HashMap<String,ArrayList<GuanZhuBanKuaiInfo>> gzbkmap,String cbxDale2) 
	{
		this.gzbkmap =  gzbkmap;
		this.cbxDale = cbxDale2;
	}
	public int getGuanZhuBanKuaiInfoIndex (GuanZhuBanKuaiInfo gzbk)
	{
		String currentdalei = this.cbxDale;
		ArrayList<GuanZhuBanKuaiInfo> tmpgzbkinfo = this.gzbkmap.get(currentdalei);
		return tmpgzbkinfo.indexOf(gzbk);
	}
	public GuanZhuBanKuaiInfo getGuanZhuBanKuaiInfo (int rowindex)
	{
		String currentdalei = this.cbxDale;
		ArrayList<GuanZhuBanKuaiInfo> tmpgzbkinfo = this.gzbkmap.get(currentdalei);
		return tmpgzbkinfo.get(rowindex);
	}

	 public int getRowCount() 
	 {
		 try {
			 String currentdalei = this.cbxDale;  
			 ArrayList<GuanZhuBanKuaiInfo> tmpgzbklist = gzbkmap.get(currentdalei);
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
			ArrayList<GuanZhuBanKuaiInfo> tmpgzbklist = gzbkmap.get(currentdalei);
			GuanZhuBanKuaiInfo tmpgzbk = tmpgzbklist.get(rowIndex);
	    	
	    	Object value = "??";

	    	switch (columnIndex) {
            case 0:
                value = tmpgzbk.getBkchanyelian();
                break;
            case 1:
            	value = tmpgzbk.getSelectedtime();
                break;
            case 2:
                value = new Boolean(tmpgzbk.isOfficallySelected() );
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
	    
	    public String getColumnName(int column){ 
	    	return jtableTitleStrings[column];
	    }//设置表格列名 

	    public boolean isCellEditable(int row,int column) {
	    	return false;
		}
	    
	    public void deleteAllRows()
	    {
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
	private HashMap<String,ArrayList<GuanZhuBanKuaiInfo>> gzbkmap;
	
	String[] jtableTitleStrings = { "股票池", "关注板块"};
	private ArrayList<String> gzdalei;
	
	ZdgzBanKuaiDetailXmlTableModel ()
	{
//		this.gzbkmap =  gzbkmap;
//		try {
//			this.gzdalei = new ArrayList<String>(gzbkmap.keySet() );
//		} catch (java.lang.NullPointerException e)		{
//			
//		}
	}

	

	public void refresh (HashMap<String,ArrayList<GuanZhuBanKuaiInfo>> gzbkmap) 
	{
		this.gzbkmap =  gzbkmap;
		this.gzdalei = new ArrayList<String>(gzbkmap.keySet() );
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
            		for( GuanZhuBanKuaiInfo tmpgzcyl : gzbkmap.get(gzdalei.get(rowIndex) )) {
            			if(tmpgzcyl.isOfficallySelected()) {
            				String chanyelian =  tmpgzcyl.getBkchanyelian();
    	            		String seltime = tmpgzcyl.getSelectedtime();
    	            		result = result + chanyelian + "(" + seltime +")" + "|" + " ";
            			}
            			
            		}
            	 } catch (java.lang.NullPointerException e) {
            		 
            	 }
            	value = result;
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
	    

	    
}


class BanKuaiGeGuTableModel extends AbstractTableModel 
{
	private HashMap<String,String> bkgegumap;
	String[] jtableTitleStrings = { "股票代码", "股票名称"};
	private ArrayList<String> bkgeguname;
	
	BanKuaiGeGuTableModel (HashMap<String,String> bkgegu)
	{
		if(bkgegu != null) {
			this.bkgegumap = bkgegu;
			this.bkgeguname = new ArrayList<String> (bkgegu.keySet() );
		}
	}

	public void refresh  (HashMap<String,String> bkgegu)
	{
		this.bkgegumap = bkgegu;
		bkgeguname = new ArrayList<String> (bkgegu.keySet() );

		Collator collator = Collator.getInstance(Locale.CHINESE); //用中文排序
 		Collections.sort(bkgeguname,collator);
		
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