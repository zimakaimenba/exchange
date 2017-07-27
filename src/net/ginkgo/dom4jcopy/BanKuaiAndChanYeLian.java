package net.ginkgo.dom4jcopy;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Collator;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.metal.MetalScrollBarUI;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.DefaultEditorKit;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;


import com.exchangeinfomanager.bankuai.gui.GuanZhuBanKuaiInfo;

import com.exchangeinfomanager.database.BanKuaiDbOperation;
import com.exchangeinfomanager.database.ChanYeLianXMLHandler;
import com.exchangeinfomanager.database.StockDbOperations;
import com.exchangeinfomanager.database.TwelveZhongDianGuanZhuXmlHandler;
import com.exchangeinfomanager.gui.StockInfoManager;
import com.exchangeinfomanager.systemconfigration.SystemConfigration;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTable;
import java.awt.event.KeyAdapter;
import javax.swing.JSeparator;


public class BanKuaiAndChanYeLian extends JPanel 
{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private HashMap<String, ArrayList<GuanZhuBanKuaiInfo>> daleidetailmap;

	/**
	 * Create the panel.
	 * @param stockInfoManager 
	 * @param bkdbopt2 
	 * @param zdgzbkxmlhandler 
	 * @param cylxmlhandler 
	 */
	public BanKuaiAndChanYeLian (StockInfoManager stockInfoManager, BanKuaiDbOperation bkdbopt2,StockDbOperations stockdbopt, TwelveZhongDianGuanZhuXmlHandler zdgzbkxmlhandler2, ChanYeLianXMLHandler cylxmlhandler2) 
	{
		
		this.bkdbopt = bkdbopt2;
		initializeGui ();
		this.cylxmhandler = cylxmlhandler2;
		this.zdgzbkxmlhandler = zdgzbkxmlhandler2;
		daleidetailmap = zdgzbkxmlhandler.getZhongDianGuanZhuBanKuai ();
		
		initializeSysConfig ();
		initializeBkChanYeLianXMLTree ();
		initializeAllDaLeiZdgzTableFromXml (null);
		initializeSingleDaLeiZdgzTableFromXml (0);
		
		createEvents ();
	}
	
	public static final int UP=0, LEFT=1, RIGHT=2, DOWN=3, NONE=4;

    //javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
    GinkgoNode topNode, viewNode, viewNodeParent, notesNode=null;
    int viewNodeIndex;
    DefaultTreeModel treeModel;
    boolean modifiedTitle = false;
    boolean editingNodeText = false;
    boolean ignoreExpansion = false;
    int savedNodeCount = 0;
    int currentNodeCount = 0;
    private ChanYeLianXMLHandler cylxmhandler;
    private TwelveZhongDianGuanZhuXmlHandler zdgzbkxmlhandler;

    BanKuaiDbOperation bkdbopt;
	private SystemConfigration sysconfig;
	private String currentselectedtdxbk;
	
	private void initializeSysConfig()
	{
		sysconfig = SystemConfigration.getInstance();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private  void initializeBkChanYeLianXMLTree()
	{
		treechanyelian.setDragEnabled(false);
		treechanyelian.setDragEnabled(true);
		treechanyelian.setDropMode(javax.swing.DropMode.ON_OR_INSERT);
		treechanyelian.setEditable(true);
		treechanyelian.setCellRenderer(new CustomTreeCellRenderer());
		treechanyelian.setRootVisible(false);
//      treechanyelian.setDropMode(javax.swing.DropMode.ON_OR_INSERT);
      //treechanyelian.setEditable(false);
//      treechanyelian.setRootVisible(false);
    
		  topNode = new GinkgoNode("JTree");
	      viewNode = topNode;
	      treechanyelian.setTransferHandler(new TreeTransferHandler());
	      treeModel = new DefaultTreeModel(topNode);
	      treechanyelian.setModel(treeModel);
	      
	      
      //CellEditor cellEditor = new CellEditor(this); 
//      treechanyelian.setCellEditor(new CustomTreeCellEditor(treechanyelian, (DefaultTreeCellRenderer) treechanyelian.getCellRenderer(),
//          cellEditor));
      //treechanyelian.setInvokesStopCellEditing(true);
      
    	topNode.removeAllChildren();
    	savedNodeCount = 0;
		topNode = cylxmhandler.getBkChanYeLianXMLTree();		
		treeModel.setRoot(topNode);
        viewNode = topNode;
        treeModel.nodeStructureChanged(topNode);
        setModifiedTitle(false);
        currentNodeCount = savedNodeCount;
        updateNodeCount();
        updateExpansion();

	}
	
    
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
         	
         	 GinkgoNode parent = (GinkgoNode) closestPath.getLastPathComponent();
         	 expandTreeNode (treechanyelian,parent);
         }

    }
    /*
     * 展开所选取的节点
     */
    private static void expandTreeNode(JTree aTree, GinkgoNode aNode) {  
        if (aNode.isLeaf()) {  
          return;  
        }  
        aTree.expandPath(new TreePath( ( (GinkgoNode) aNode).getPath()));  
        int n = aNode.getChildCount();  
        for (int i = 0; i <n; i++) {  
          expandTreeNode(aTree, (GinkgoNode) aNode.getChildAt(i));  
        }  
     } 

	public boolean saveButtonActionPerformed (java.awt.event.ActionEvent evt) //GEN-FIRST:event_saveButtonActionPerformed 
    {
		cylxmhandler.saveTreeToChanYeLianXML(topNode);
		saveButton.setEnabled(false); 

		return false;
    }
	 /*
	  * 设置每个node的性质
	  */
	 private void changeStatus(int newStatus){
	        if (treechanyelian.getSelectionCount() > 0) {
	            for (TreePath path : treechanyelian.getSelectionPaths()) {
	                GinkgoNode currentNode = (GinkgoNode) path.getLastPathComponent();
	                currentNode.setStatus(newStatus);
	                treeModel.nodeChanged(currentNode);
	            }
	            setModifiedTitle(true);
	        }
	 }  
	 
	 private void addSubnodeButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_addSubnodeButtonActionPerformed 
	 {
		 saveButton.setEnabled(true);
		 addSubnode(((SubnodeButton)addSubnodeButton).getDirection());
	 }//GEN-LAST:event_addSubnodeButtonActionPerformed

	    //public void addSubnode(int direction,String newsubnodename)
	 public void addSubnode(int direction)
	 {
		 if(lstTags.getSelectedValue() == null) {
			 JOptionPane.showMessageDialog(null,"请选择一个子板块!");
			 return;
		 }
			
		 String subbkname = lstTags.getSelectedValue().toString();
		 
         if (treechanyelian.getSelectionCount() == 1) {

	            GinkgoNode newNode = new GinkgoNode(subbkname);
	            newNode.setStatus(GinkgoNode.SUBBK);
	            if (direction == Ginkgo2.RIGHT){
	                GinkgoNode parent = (GinkgoNode) treechanyelian.getSelectionPath().getLastPathComponent();
	                
	                boolean enableoperation = checkNodeDuplicate (parent,newNode);
                	if( enableoperation ) {
	                		JOptionPane.showMessageDialog(null,"同级中已经存在相同名称子版块或与父节点重名，不能重复添加!");
	                		return;
	                }
	                
	                if( parent.getStatus() != GinkgoNode.BKGEGU) { //父节点不是个股，可以加
	                	String suoshubkcode = parent.getTDXBanKuaiZhiShuCode();
	                	newNode.setTDXBanKuaiZhiShuCode(suoshubkcode);
	                	parent.add(newNode);
		                parent.setExpansion(true);
	                } else { ////父节点是个股，不可以加
	                	System.out.println("父节点是个股，不能加板块");
	                	direction = Ginkgo2.DOWN;
	                	//return;
	                }
	                
	            } 
	            
	            if (direction != Ginkgo2.RIGHT){
	                GinkgoNode currentNode = (GinkgoNode) treechanyelian.getSelectionPath().getLastPathComponent();
	                GinkgoNode parent = (GinkgoNode) currentNode.getParent();
	                
	                boolean enableoperation = checkNodeDuplicate (parent,newNode);
                	if( enableoperation ) {
	                		JOptionPane.showMessageDialog(null,"同级中已经存在相同名称子版块或与父节点重名，不能重复添加!");
	                		return;
	                }
	                
	                int childIndex = parent.getIndex(currentNode);
	                if (direction==Ginkgo2.DOWN) parent.insert(newNode, childIndex+1);
	                else if (direction==Ginkgo2.UP) parent.insert(newNode, childIndex);
	                else return;
	            }

	            treeModel.nodesWereInserted(newNode.getParent(), new int[] {newNode.getParent().getIndex(newNode)});
	            treechanyelian.startEditingAtPath(new TreePath(newNode.getPath()));
	            setModifiedTitle(false);
	            addNodeCount();
	        }
	  }
	    
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
	    
	    private void addSubnodeButtonMouseExited(java.awt.event.MouseEvent evt) //GEN-FIRST:event_addSubnodeButtonMouseExited 
	    {
	        addSubnodeButton.setIcon(addSubnodeIcon);
	        addSubnodeButton.setToolTipText("Add subnode");
	    }//GEN-LAST:event_addSubnodeButtonMouseExited


	    public void addGeGunode(int direction)
		 {
	    	int row = tablebkgegu.getSelectedRow();
			if(row <0) {
				JOptionPane.showMessageDialog(null,"请选择一个股票!","Warning",JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			String gegucodename = ((BanKuaiGeGuTableModel)(tablebkgegu.getModel())).getStockCodeAndName(row);
			 
	        if (treechanyelian.getSelectionCount() == 1) {
		            GinkgoNode newNode = new GinkgoNode(gegucodename);
		            newNode.setStatus(GinkgoNode.BKGEGU);
		            if (direction == Ginkgo2.RIGHT){
		                GinkgoNode parent = (GinkgoNode) treechanyelian.getSelectionPath().getLastPathComponent();
		                
		                boolean enableoperation = checkNodeDuplicate (parent,newNode);
	                	if( enableoperation ) {
		                		JOptionPane.showMessageDialog(null,"同级中已经存在相同名称个股或与父节点重名，不能重复添加!");
		                		return;
		                }

	                	if( parent.getStatus() != GinkgoNode.BKGEGU) { //父节点不是个股，可以加
		                	String suoshubkcode = parent.getTDXBanKuaiZhiShuCode();
		                	newNode.setTDXBanKuaiZhiShuCode(suoshubkcode);
			                parent.add(newNode);
			                parent.setExpansion(true);
		                } else { ////父节点是个股，不可以加
		                	System.out.println("父节点已经是个股了");
		                	direction = Ginkgo2.DOWN;
		                }
		            } 
		            
		            if (direction != Ginkgo2.RIGHT){
		                GinkgoNode currentNode = (GinkgoNode) treechanyelian.getSelectionPath().getLastPathComponent();
		                GinkgoNode parent = (GinkgoNode) currentNode.getParent();
		                
		                boolean enableoperation = checkNodeDuplicate (parent,newNode);
	                	if( enableoperation ) {
		                		JOptionPane.showMessageDialog(null,"同级中已经存在相同名称个股或与父节点重名，不能重复添加!");
		                		return;
		                }
		                
		                int childIndex = parent.getIndex(currentNode);
		                if (direction==Ginkgo2.DOWN) parent.insert(newNode, childIndex+1);
		                else if (direction==Ginkgo2.UP) parent.insert(newNode, childIndex);
		                else return;
		            }

		            treeModel.nodesWereInserted(newNode.getParent(), new int[] {newNode.getParent().getIndex(newNode)});
		            treechanyelian.startEditingAtPath(new TreePath(newNode.getPath()));
		            setModifiedTitle(true);
		            addNodeCount();
		        }
		  }

	    private boolean checkNodeDuplicate(GinkgoNode parent,  GinkgoNode newNode) 
	    {
	    	String gegucodename = newNode.getUserObject().toString().trim();
	    	String parentname = parent.getUserObject().toString().trim();
	    	
	    	if(gegucodename.equals(parentname))
	    		return true;
	    	
	    	int childnum = parent.getChildCount();
            for(int i=0;i<childnum;i++) {
            	GinkgoNode tmpnode = (GinkgoNode)parent.getChildAt(i);
            	String tmpnodename = tmpnode.getUserObject().toString();
            	System.out.println(tmpnodename);
            	if(tmpnodename.equals(gegucodename)) {
            		return true;
            	}
            }
            
            
			
			return false;
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
	    }//GEN-LAST:event_addSubnodeButtonMouseExited
	    private void addGeGuButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_addSubnodeButtonActionPerformed 
		{
	    	saveButton.setEnabled(true);
	    	addGeGunode(((SubnodeButton)addGeGuButton).getDirection());
		}//GEN-LAST:event_addSubnodeButtonActionPerformed

	    
	    
	    private void moveButtonMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_moveButtonMouseMoved
	        MoveButton button = (MoveButton)moveButton;
	        if (evt.getX()<12 && evt.getX()<1 + evt.getY() && evt.getX()<34 - evt.getY()){
	            button.setIcon(moveLeftIcon);
	            button.setToolTipText("Move left (ALT-LEFT)");
	            button.setDirection(Ginkgo2.LEFT);
	        }
	        else if (evt.getY()<13){
	            button.setIcon(moveUpIcon);
	            button.setToolTipText("Move up (ALT-UP)");
	            button.setDirection(Ginkgo2.UP);
	        }
	        else if (evt.getY()>22){
	            button.setIcon(moveDownIcon);
	            button.setToolTipText("Move down (ALT-DOWN)");
	            button.setDirection(Ginkgo2.DOWN);
	        }
	        else {
	            button.setIcon(moveIcon);
	            button.setToolTipText("Move subnode");
	            button.setDirection(Ginkgo2.NONE);
	        }
	    }//GEN-LAST:event_moveButtonMouseMoved

	    private void moveButtonMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_moveButtonMouseExited
	        moveButton.setIcon(moveIcon);
	        moveButton.setToolTipText("Move subnode");
	    }//GEN-LAST:event_moveButtonMouseExited

	    private void moveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveButtonActionPerformed
	        moveNodes(((MoveButton)moveButton).getDirection());
	    }//GEN-LAST:event_moveButtonActionPerformed

	    public void moveNodes(int direction){

	        if (treechanyelian.getSelectionCount()==0) return;
	        
	        TreePath parentPath = treechanyelian.getSelectionPath().getParentPath();
	        TreePath[] treePaths = treechanyelian.getSelectionPaths();
	        final GinkgoNode parentNode = (GinkgoNode) parentPath.getLastPathComponent();
	        //String tmpstr = parentPath.toString();
	        if(parentNode.getStatus() == GinkgoNode.TDXBK || parentPath.toString().equals("[JTree]"))
	        	return;
	        
	        //unselect non-siblings
	        for (TreePath treePath : treePaths)
	            if (!parentPath.equals(treePath.getParentPath()))
	                treechanyelian.removeSelectionPath(treePath);
	        treePaths = treechanyelian.getSelectionPaths();
	        
	        sortPaths(treePaths);
	        
	        GinkgoNode node=null, newParentNode;
	        int index;
	        if (direction==Ginkgo2.UP){
	            treechanyelian.clearSelection();
	            for (TreePath treePath : treePaths){
	                node = (GinkgoNode) treePath.getLastPathComponent();
	                index = parentNode.getIndex(node);
	                if (index == 0) {
	                    treechanyelian.setSelectionPaths(treePaths);
	                    return;
	                }
	                else {
	                    parentNode.remove(index);
	                    treeModel.nodesWereRemoved(parentNode, new int[] {index}, new Object[] {node});
	                    parentNode.insert(node, index-1);
	                    treeModel.nodesWereInserted(parentNode, new int[] {index - 1});
	                }
	            }
	            treechanyelian.setSelectionPaths(treePaths);
	            treechanyelian.scrollPathToVisible(treePaths[0]);
	        }
	        else if (direction==Ginkgo2.DOWN){
	            treechanyelian.clearSelection();
	            for (int i=treePaths.length - 1; i>=0; i--){
	                node = (GinkgoNode) treePaths[i].getLastPathComponent();
	                index = parentNode.getIndex(node);
	                if (index == parentNode.getChildCount()-1) {
	                    treechanyelian.setSelectionPaths(treePaths);
	                    return;
	                }
	                else {
	                    parentNode.remove(index);
	                    treeModel.nodesWereRemoved(parentNode, new int[] {index}, new Object[] {node});
	                    parentNode.insert(node, index+1);
	                    treeModel.nodesWereInserted(parentNode, new int[] {index + 1});
	                }
	            }
	            treechanyelian.setSelectionPaths(treePaths);
	            treechanyelian.scrollPathToVisible(treePaths[treePaths.length-1]);
	            
	        }
	        else if (direction==Ginkgo2.LEFT)
	            
	            if (parentNode != topNode) {
	                treechanyelian.clearSelection();
	                if (viewNode == parentNode) newParentNode = viewNodeParent;
	                else newParentNode = (GinkgoNode)parentNode.getParent();
	            
	                for (TreePath treePath : treePaths){
	                    node = (GinkgoNode) treePath.getLastPathComponent();
	                    index = parentNode.getIndex(node);
	                    parentNode.remove(index);
	                    treeModel.nodesWereRemoved(parentNode, new int[]{index}, new Object[]{node});
	                    newParentNode.add(node);
	                    treeModel.nodesWereInserted(newParentNode, new int[] {newParentNode.getIndex(node)});
	                    if (parentNode != viewNode) treechanyelian.addSelectionPath(new TreePath(node.getPath()));
	                }
	                treechanyelian.scrollPathToVisible(new TreePath(node.getPath()));
	            }
	        
	    }
	    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
	        deleteNodes();
	    }//GEN-LAST:event_deleteButtonActionPerformed
	    public void deleteNodes(){

	        if (treechanyelian.getSelectionCount() > 0){
	            TreePath[] treePaths = treechanyelian.getSelectionPaths();
	            sortPaths(treePaths);
	            int topRow = treechanyelian.getRowForPath(treePaths[0]);
	            for (TreePath path : treePaths){
	                GinkgoNode child = (GinkgoNode) path.getLastPathComponent();
	                GinkgoNode parent = (GinkgoNode) child.getParent();
	                if (parent != null && child.getRoot()==viewNode){
	                    deleteNodeCount(countNodes(child));
	                    int childIndex = parent.getIndex(child);
	                    parent.remove(child);
	                    treeModel.nodesWereRemoved(parent, new int[] {childIndex}, new Object[] {child});
	                    if (parent.getChildCount()==0) parent.setExpansion(false);
	                }
	            }
	            setModifiedTitle(true);
	            if (treechanyelian.getVisibleRowCount()>0) treechanyelian.setSelectionRow(topRow);
	           
	            saveButton.setEnabled(true);
	        }
	    }
	    

	    


//		 private void addButtonActionPerformed(java.awt.event.ActionEvent evt) 
//		 {//GEN-FIRST:event_addButtonActionPerformed
//		        GinkgoNode newNode = new GinkgoNode("New Node", this);
//		        newNode.setStatus(GinkgoNode.TDXBK);
//		        viewNode.add(newNode);
//		        treeModel.nodesWereInserted(viewNode, new int[] {viewNode.getIndex(newNode)});
//		        
//		        treechanyelian.startEditingAtPath(new TreePath(newNode.getPath()));
//		        setModifiedTitle(true);
//		        addNodeCount();
//		 }//GEN-LAST:event_addButtonActionPerformed
		 
		 
//
//		private void openButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openButtonActionPerformed
//	        if (fc.showOpenDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION){
//	            try {
//	                Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(fc.getSelectedFile());
//	                
//	                topNode.removeAllChildren();
//	                savedNodeCount = 0;
//
//	                importFromDOM(topNode, doc.getDocumentElement());
//	                treeModel.setRoot(topNode);
//	                viewNode = topNode;
//	                treeModel.nodeStructureChanged(topNode);
//	                //this.setTitle(fc.getSelectedFile().getName());
//	                setModifiedTitle(false);
//	                currentNodeCount = savedNodeCount;
//	                updateNodeCount();
//	                updateExpansion();
//
//	            } catch (ParserConfigurationException | SAXException | IOException ex) {
//	                System.out.printf("Caught Error:\n%s\n",ex.getLocalizedMessage());
//	                System.exit(-1);
//	            }
//	        }
//	    }//GEN-LAST:event_openButtonActionPerformed
	
	
	
    private void addNodeCount(){
        currentNodeCount++;
        updateNodeCount();
    }
    
    private void deleteNodeCount(int nodes){
        currentNodeCount -= nodes;
        updateNodeCount();
    }
    
    private void updateNodeCount(){
    }
    
    public void moveCursorUp(){
        treechanyelian.stopEditing();
        int currentRow = treechanyelian.getSelectionRows()[0];
        if (treechanyelian.getSelectionCount()==1 && currentRow>0)
            treechanyelian.setSelectionRow(currentRow - 1);
    }
    
    public void moveCursorDown(){
        treechanyelian.stopEditing();
        int currentRow = treechanyelian.getSelectionRows()[0];
        if (treechanyelian.getSelectionCount()==1 && currentRow<treechanyelian.getRowCount() - 1)
            treechanyelian.setSelectionRow(currentRow + 1); 
    }
    
   
    
//    private void saveAsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsButtonActionPerformed
//        if (fc.showSaveDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION){
//            if (!fc.getSelectedFile().getName().contains("."))
//                    fc.setSelectedFile(new java.io.File(fc.getSelectedFile().getAbsolutePath()+".xml"));
//           saveFile();
//        }
//    }//GEN-LAST:event_saveAsButtonActionPerformed

   
        
//    private void greenButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_greenButtonActionPerformed
//        changeStatus(GinkgoNode.COMPLETE);
//    }//GEN-LAST:event_greenButtonActionPerformed
//
//    private void blueButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_blueButtonActionPerformed
//        changeStatus(GinkgoNode.ACTIVE);
//    }//GEN-LAST:event_blueButtonActionPerformed

    
    


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

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        
        if (this.getWidth()<420 ){
            //jToolBar2.setVisible(true);
            //jSeparator2.setVisible(false);
            
            //jToolBar2.add(zoomInButton);
            //jToolBar2.add(zoomOutButton);
            //jToolBar2.add(zoomAllButton);
            //jToolBar2.add(jSeparator3);
            
            //jToolBar2.add(openButton);
            //jToolBar2.add(saveButton);
            //jToolBar2.add(saveAsButton);
        } else {
           // jToolBar2.setVisible(false);
            //jSeparator2.setVisible(true);
            //jToolBar1.add(zoomInButton);
            //jToolBar1.add(zoomOutButton);
            //jToolBar1.add(zoomAllButton);
            //jToolBar1.add(jSeparator3);
            
            //jToolBar1.add(openButton);
            //jToolBar1.add(saveButton);
            //jToolBar1.add(saveAsButton);          
        }
        
    }//GEN-LAST:event_formComponentResized

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

//    private void treeValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_treeValueChanged
//    // Update notes pane to reflect treechanyelian selection
//        if (editingNodeText) notesNode.setNoteText(notesPane.getText());
//        if (treechanyelian.getSelectionCount() == 1) {
//            notesNode = (GinkgoNode) treechanyelian.getSelectionPath().getLastPathComponent();
//            notesPane.setText(notesNode.getNoteText());
//        } else notesPane.setText(null);
//        editingNodeText = false;
//    }//GEN-LAST:event_treeValueChanged

    private void notesPaneKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_notesPaneKeyTyped
        setModifiedTitle(true);
    }//GEN-LAST:event_notesPaneKeyTyped

    private void zoomInButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomInButtonActionPerformed
        if (treechanyelian.getSelectionCount()==1) {
            attachChild();
            viewNode = (GinkgoNode) treechanyelian.getSelectionPath().getLastPathComponent();            
            detachChild();
            treeModel.setRoot(viewNode);
            if (viewNode.getChildCount()>0) viewNode.setExpansion(true);
            updateExpansion();
        }
    }//GEN-LAST:event_zoomInButtonActionPerformed

    private void detachChild(){
        if (viewNode != topNode){
            viewNodeParent = (GinkgoNode) viewNode.getParent();
            viewNodeIndex = viewNodeParent.getIndex(viewNode);
            viewNodeParent.remove(viewNode);
        }
    }
    
    private void attachChild(){
        if (viewNode != topNode)
            viewNodeParent.insert(viewNode, viewNodeIndex);
    }
    
    private void zoomOutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomOutButtonActionPerformed
        if (viewNode != topNode) {
            attachChild();
            viewNode = (GinkgoNode) viewNode.getParent();
            detachChild();
            treeModel.setRoot(viewNode);

            updateExpansion();
        }
    }//GEN-LAST:event_zoomOutButtonActionPerformed

    private void zoomAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomAllButtonActionPerformed
        attachChild();
        viewNode = topNode;
        treeModel.setRoot(topNode);

        updateExpansion();
    }//GEN-LAST:event_zoomAllButtonActionPerformed

    private void treeTreeExpanded(javax.swing.event.TreeExpansionEvent evt) {//GEN-FIRST:event_treeTreeExpanded
        if (!ignoreExpansion)
        ((GinkgoNode) evt.getPath().getLastPathComponent()).setExpansion(true);
    }//GEN-LAST:event_treeTreeExpanded

    private void treeTreeCollapsed(javax.swing.event.TreeExpansionEvent evt) {//GEN-FIRST:event_treeTreeCollapsed
        if (!ignoreExpansion)
        ((GinkgoNode) evt.getPath().getLastPathComponent()).setExpansion(false);
    }//GEN-LAST:event_treeTreeCollapsed

        
    

    
    
    public void nodeChanged(){
        setModifiedTitle(false);
    }
    
    private void updateExpansion(){
        ignoreExpansion = true;
        recurseExpansion(viewNode);
        ignoreExpansion = false;
    }
    
    private void recurseExpansion(GinkgoNode node){
        Enumeration e = node.children();
        
        while (e.hasMoreElements())
            recurseExpansion((GinkgoNode) e.nextElement());

        if (node.isExpanded()) 
        	treechanyelian.expandPath(new TreePath(node.getPath()));
        else
            if (!node.isLeaf() && node != topNode)
                treechanyelian.collapsePath(new TreePath(node.getPath()));
    }
    
        
    @SuppressWarnings("unchecked")
    private void sortPaths(TreePath[] treePaths){
        Arrays.sort(treePaths, new java.util.Comparator(){
            public int compare(Object path1, Object path2){
                int row1 = treechanyelian.getRowForPath((TreePath) path1);
                int row2 = treechanyelian.getRowForPath((TreePath) path2);
                if (row1 < row2) return -1;
                else if (row1 > row2) return 1;
                else return 0;
        }});
    }
    
        
    private int countNodes(GinkgoNode node){
        int nodeCount = 0;
        Enumeration e = node.breadthFirstEnumeration();
        while (e.hasMoreElements()){
            e.nextElement();
            nodeCount++;
        }
        return nodeCount;
    }
    
    private void setModifiedTitle(boolean setting){
        /**
         * When enabled, changes the title to have an asterisk to 
         * indicate something has changed and not been saved.
         * 
         * @param setting the new document modification status status
         */
        
        
        if (setting == false) modifiedTitle = false;
        else if (!modifiedTitle){
            //this.setTitle(this.getTitle()+ "*");
            modifiedTitle = true;
        }
    }

    private void initializeChanYeLianTree() 
	{

        
//        tree.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "startEditing");
//        tree.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "ginkgoDelete");
//        tree.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "ginkgoDelete");
//        
//        tree.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, KeyEvent.ALT_DOWN_MASK), "ginkgoMoveUp");
//        tree.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, KeyEvent.ALT_DOWN_MASK), "ginkgoMoveDown");
//        tree.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.ALT_DOWN_MASK), "ginkgoMoveLeft");
//        
//        if (System.getProperty("os.name").startsWith("Mac OS X")){
//
//            tree.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP,KeyEvent.META_DOWN_MASK), "ginkgoAddAbove");
//            tree.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,KeyEvent.META_DOWN_MASK), "ginkgoAddBelow");
//            tree.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,KeyEvent.META_DOWN_MASK), "ginkgoAddChild");
//
//            notesPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.META_DOWN_MASK),
//                    DefaultEditorKit.copyAction);
//            notesPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.META_DOWN_MASK),
//                    DefaultEditorKit.pasteAction);
//            notesPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.META_DOWN_MASK),
//                    DefaultEditorKit.cutAction);
//            notesPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.META_DOWN_MASK),
//                    DefaultEditorKit.selectAllAction);
//        }
//        
//        else {
//            tree.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP,KeyEvent.CTRL_DOWN_MASK), "ginkgoAddAbove");
//            tree.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,KeyEvent.CTRL_DOWN_MASK), "ginkgoAddBelow");
//            tree.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT,KeyEvent.CTRL_DOWN_MASK), "ginkgoAddChild");
//        }
        
        
        
//        tree.getActionMap().put("ginkgoDelete", new javax.swing.AbstractAction(){
//            public void actionPerformed(ActionEvent e){
//                deleteNodes(); }});
//        tree.getActionMap().put("ginkgoMoveUp", new javax.swing.AbstractAction(){
//            public void actionPerformed(ActionEvent e){
//                moveNodes(Ginkgo2.UP); }});
//        tree.getActionMap().put("ginkgoMoveDown", new javax.swing.AbstractAction(){
//            public void actionPerformed(ActionEvent e){
//                moveNodes(Ginkgo2.DOWN); }});
//        tree.getActionMap().put("ginkgoMoveLeft", new javax.swing.AbstractAction(){
//            public void actionPerformed(ActionEvent e){
//                moveNodes(Ginkgo2.LEFT); }});
//        tree.getActionMap().put("ginkgoAddAbove", new javax.swing.AbstractAction(){
//            public void actionPerformed(ActionEvent e){
//                addSubnode(Ginkgo2.UP); }});
//        tree.getActionMap().put("ginkgoAddBelow", new javax.swing.AbstractAction(){
//            public void actionPerformed(ActionEvent e){
//                addSubnode(Ginkgo2.DOWN); }});
//        tree.getActionMap().put("ginkgoAddChild", new javax.swing.AbstractAction(){
//            public void actionPerformed(ActionEvent e){
//                addSubnode(Ginkgo2.RIGHT); }});

		
	}
    
    protected TreePath findBanKuaiInTree(String bkinputed) 
	{
    	@SuppressWarnings("unchecked")
    	TreePath bkpath = null ;
	    Enumeration<GinkgoNode> e = topNode.depthFirstEnumeration();
	    while (e.hasMoreElements()) {
	    	GinkgoNode node = e.nextElement();
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
	
	private void createEvents() 
	{
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
		
		btnRightToLeft.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{

				int row = tableCurZdgzbk.getSelectedRow();
				if(row <0) {
					JOptionPane.showMessageDialog(null,"请选择产业链","Warning",JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				
				 //HashMap<String, ArrayList<GuanZhuBanKuaiInfo>> allzdgzbkmap = zdgzbkxmlhandler.getZhongDianGuanZhuBanKuai ();
//				 ArrayList<GuanZhuBanKuaiInfo> tmpzdgzbklist = allzdgzbkmap.get(cbxDale.getSelectedItem().toString());
//				 tmpzdgzbklist.remove(row);
//				 allzdgzbkmap.put(cbxDale.getSelectedItem().toString(), tmpzdgzbklist);
				
				 //String daleiname = cbxDale.getSelectedItem().toString();
				String daleiname = getCurSelectedDaLei();
				 if( daleiname != null) {
					 GuanZhuBanKuaiInfo gzbk = ((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).getGuanZhuBanKuaiInfo(row);
					 zdgzbkxmlhandler.removeGuanZhuBanKuai(daleiname, gzbk);
				 }
				 else
					 JOptionPane.showMessageDialog(null,"请选择一个大类","Warning",JOptionPane.WARNING_MESSAGE);
				 
				 initializeAllDaLeiZdgzTableFromXml (daleiname);
				 initializeSingleDaLeiZdgzTableFromXml (0);
				 
				 
				 
					
				 //((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).refresh(allzdgzbkmap);
//				 ((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).refresh(zdgzbkxmlhandler.getZhongDianGuanZhuBanKuai (),daleiname );
//				 ((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).fireTableDataChanged();
				 
				 //((ZdgzBanKuaiDetailXmlTableModel)tableZdgzBankDetails.getModel()).refresh(allzdgzbkmap);
//				 ((ZdgzBanKuaiDetailXmlTableModel)tableZdgzBankDetails.getModel()).refresh(zdgzbkxmlhandler.getZhongDianGuanZhuBanKuai () );
//				 ((ZdgzBanKuaiDetailXmlTableModel)tableZdgzBankDetails.getModel()).fireTableDataChanged();
				 
			     btnSaveZdgz.setEnabled(true);
			    
			}
		});

		
		btnLeftToRight.addMouseListener(new MouseAdapter() {
			 @Override
			public void mouseClicked(MouseEvent arg0) 
			{
				 TreePath path = treechanyelian.getSelectionPath();
				 GinkgoNode parent = (GinkgoNode) treechanyelian.getSelectionPath().getLastPathComponent();
				 
//				 System.out.println(path.toString());
//				 System.out.print(path.getPathCount() );
//				 System.out.print(parent.getStatus());
				 
				 GuanZhuBanKuaiInfo tmpgzbk = new GuanZhuBanKuaiInfo ();
				 String tdxbk = path.getPathComponent(1).toString();
				 String chanyelian = "";
				 if( parent.getStatus() != GinkgoNode.BKGEGU) //是个股的情况和不是个股的情况需要分开处理
					 for(int i=1;i<path.getPathCount();i++) { //不是个股，一直保存到底
						 chanyelian = chanyelian + path.getPathComponent(i).toString() + "->";
					 }
				 else { //是个股，最后一个个股不用保存
					 for(int i=1;i<path.getPathCount()-1;i++) {
						 chanyelian = chanyelian + path.getPathComponent(i).toString() + "->";
					 }
				 }
				 System.out.println(chanyelian);
				 
				 
				 
				 tmpgzbk.setSelectedtime(new Date ());
				 tmpgzbk.setBkchanyelian(chanyelian.substring(0, chanyelian.length()-2)); //去掉最后的->
				 tmpgzbk.setTdxbk(tdxbk);
				 if(JOptionPane.showConfirmDialog(null, "是否直接加入重点关注？","Warning", JOptionPane.YES_NO_OPTION) == 1) {
					 tmpgzbk.setOfficallySelected(false);
				 } else
					 tmpgzbk.setOfficallySelected(true);

				 String daleiname = getCurSelectedDaLei();
				 if( daleiname != null)
					 zdgzbkxmlhandler.addNewGuanZhuBanKuai(daleiname, tmpgzbk);
				 else
					 JOptionPane.showMessageDialog(null,"请选择一个大类","Warning",JOptionPane.WARNING_MESSAGE);
//				 HashMap<String, ArrayList<GuanZhuBanKuaiInfo>> allzdgzbkmap = zdgzbkxmlhandler.getZhongDianGuanZhuBanKuai ();
//				 ArrayList<GuanZhuBanKuaiInfo> tmpzdgzbklist; //右边显示的内容
//				 try {
//					tmpzdgzbklist = allzdgzbkmap.get(cbxDale.getSelectedItem().toString());
//					tmpzdgzbklist.add(tmpgzbk);
//				} catch (java.lang.NullPointerException e) {
//					tmpzdgzbklist = new ArrayList<GuanZhuBanKuaiInfo> ();
//					tmpzdgzbklist.add(tmpgzbk);
//				}
//				allzdgzbkmap.put(cbxDale.getSelectedItem().toString(), tmpzdgzbklist);
				 
				 initializeAllDaLeiZdgzTableFromXml (daleiname);
				 int rowindex = ((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).getGuanZhuBanKuaiInfoIndex(tmpgzbk);
				 initializeSingleDaLeiZdgzTableFromXml (rowindex);
				 
//				 ((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).refresh(zdgzbkxmlhandler.getZhongDianGuanZhuBanKuai (),daleiname);
//				 ((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).fireTableDataChanged();
//				 
//				 ((ZdgzBanKuaiDetailXmlTableModel)tableZdgzBankDetails.getModel()).refresh(zdgzbkxmlhandler.getZhongDianGuanZhuBanKuai ());
//				 ((ZdgzBanKuaiDetailXmlTableModel)tableZdgzBankDetails.getModel()).fireTableDataChanged();
				 
			     btnSaveZdgz.setEnabled(true);
			     
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
					 //zdgzbkxmlhandler.addNewGuanZhuBanKuai(selectedalei, gzbk);
				} else
					 JOptionPane.showMessageDialog(null,"请选择一个大类","Warning",JOptionPane.WARNING_MESSAGE);
				
				if(JOptionPane.showConfirmDialog(null, "是否直接从候补产业链中删除？","Warning", JOptionPane.YES_NO_OPTION) == 0) {
					GuanZhuBanKuaiInfo gzbk = ((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).getGuanZhuBanKuaiInfo(row);
					zdgzbkxmlhandler.removeGuanZhuBanKuai(selectedalei, gzbk);
					initializeSingleDaLeiZdgzTableFromXml (1);
				} else
					initializeSingleDaLeiZdgzTableFromXml (row);
				
				btnSaveZdgz.setEnabled(true);
				
//				((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).refresh(zdgzbkxmlhandler.getZhongDianGuanZhuBanKuai (),selectedalei);
//				((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).fireTableDataChanged();
				
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
					 gzbk.setOfficallySelected(true);
					 //zdgzbkxmlhandler.addNewGuanZhuBanKuai(selectedalei, gzbk);
				} else
					 JOptionPane.showMessageDialog(null,"请选择一个大类","Warning",JOptionPane.WARNING_MESSAGE);
				
				initializeAllDaLeiZdgzTableFromXml (selectedalei);
				initializeSingleDaLeiZdgzTableFromXml (row);
				btnSaveZdgz.setEnabled(true);
//				((ZdgzBanKuaiDetailXmlTableModel)tableZdgzBankDetails.getModel()).refresh(daleidetailmap);
//				((ZdgzBanKuaiDetailXmlTableModel)tableZdgzBankDetails.getModel()).fireTableDataChanged();
//				
//				((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).refresh(zdgzbkxmlhandler.getZhongDianGuanZhuBanKuai (),selectedalei);
//				((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).fireTableDataChanged();
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
//				 String selecteddalei = ((ZdgzBanKuaiDetailXmlTableModel)tableZdgzBankDetails.getModel()).getZdgzDaLei (row);
//				 ((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).refresh(zdgzbkxmlhandler.getZhongDianGuanZhuBanKuai (),selecteddalei);
//				 ((CurZdgzBanKuaiTableModel)tableCurZdgzbk.getModel()).fireTableDataChanged();
//				 
				 
//				 cbxDale.setSelectedItem(selecteddalei);
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
		
        treechanyelian.addTreeExpansionListener(new javax.swing.event.TreeExpansionListener() {
            public void treeExpanded(javax.swing.event.TreeExpansionEvent evt) {
                treeTreeExpanded(evt);
            }
            public void treeCollapsed(javax.swing.event.TreeExpansionEvent evt) {
                treeTreeCollapsed(evt);
            }
        });
        treechanyelian.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                treeMousePressed(evt);
            }
        });
//        treechanyelian.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
//            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
//                treeValueChanged(evt);
//            }
//        });
        
		btnAddSubBk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				String newsubbk = JOptionPane.showInputDialog(null,"请输入子板块名称:","增加子版块", JOptionPane.QUESTION_MESSAGE);
				if(newsubbk == null)
					return;
				
//				if(bkdbopt.getSysBkSet().contains(newsubbk) ) {
//					JOptionPane.showMessageDialog(null,"输入子版块名称与通达信板块名称冲突,请重新输入!");
//					return ;
//				}
				
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
        
        moveButton.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                moveButtonMouseMoved(evt);
            }
        });
        moveButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                moveButtonMouseExited(evt);
            }
        });
        moveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveButtonActionPerformed(evt);
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
	

	

	private JPanel contentPane;
	ImageIcon moveLeftIcon, moveUpIcon, moveDownIcon, moveIcon;
    ImageIcon addBelowIcon, addAboveIcon, addChildIcon, addSubnodeIcon;
    private JScrollPane scrollPaneTags;
    private JList lstTags;
    private JButton btnAddSubBk;
    private JScrollPane scrollPanegegu;
    private javax.swing.JButton addGeGuButton;
    private javax.swing.JButton addSubnodeButton;
    private javax.swing.JButton blueButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton greenButton;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JSplitPane jSplitPane;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JButton moveButton;
    private javax.swing.JButton notesButton;
    private javax.swing.JTextPane notesPane;
    private javax.swing.JScrollPane notesScrollPane;
    private javax.swing.JButton openButton;
    private javax.swing.JButton saveAsButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JTree treechanyelian;
    private javax.swing.JScrollPane treeScrollPane;
    private javax.swing.JButton zoomAllButton;
    private javax.swing.JButton zoomInButton;
    private javax.swing.JButton zoomOutButton;
    private JTable tablebkgegu;
    private JTextField tfldfindbk;
    private JButton btnfindbk;
    private JTextField tfldfindgegu;
    private JPanel panelcyl;
    private JPanel panelzdgz;
    private JTextField textField;
    private JButton btnSaveZdgz;
    private JButton btnGenTDXCode;
    private JButton btnadddalei;
    private JButton btndeldalei;
    private JButton btnChsParseFile;
    private JButton btnLeftToRight;
    private JButton btnRightToLeft;
    private JTable tableCurZdgzbk;
    private JTable tableZdgzBankDetails;
    private JButton buttonremoveoffical;
    private JButton buttonaddofficial;
	
	private void initializeGui() 
	{
		
		JPanel panelparsefile = new JPanel();
		
		GroupLayout groupLayout = new GroupLayout(this);
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(panelparsefile, GroupLayout.DEFAULT_SIZE, 878, Short.MAX_VALUE)
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addContainerGap(16, Short.MAX_VALUE)
					.addComponent(panelparsefile, GroupLayout.PREFERRED_SIZE, 870, GroupLayout.PREFERRED_SIZE))
		);
		
		JLabel lblNewLabel = new JLabel("\u89E3\u6790\u677F\u5757\u6587\u4EF6");
		
		textField = new JTextField();
		textField.setColumns(10);
		
		btnChsParseFile = new JButton("\u9009\u62E9\u6587\u4EF6");
		
		panelzdgz = new JPanel();
		
		JScrollPane scrollPanesglzdgz = new JScrollPane();
		
		JScrollPane scrollPaneallzdgz = new JScrollPane();
		
		btnSaveZdgz = new JButton("\u4FDD\u5B58\u91CD\u70B9\u5173\u6CE8");
		
		btnSaveZdgz.setEnabled(false);
		
		btnGenTDXCode = new JButton("\u751F\u6210TDX\u4EE3\u7801");
		
		btnadddalei = new JButton("\u589E\u52A0\u5927\u7C7B");
		
		btndeldalei = new JButton("\u5220\u9664\u5927\u7C7B");
		
		buttonaddofficial = new JButton("->");
		
		
		buttonremoveoffical = new JButton("<-");
		
		
		panelcyl = new JPanel();
		
		btnAddSubBk = new JButton("\u6DFB\u52A0\u5B50\u677F\u5757");
		
		
		scrollPaneTags = new JScrollPane();
		
		lstTags = new JList<String>(new DefaultListModel<String>());
		scrollPaneTags.setViewportView(lstTags);
		
		
		jSplitPane = new JSplitPane();
		jSplitPane.setDividerSize(0);
		jSplitPane.setResizeWeight(1.0);
		
		treeScrollPane = new JScrollPane();
		//		treeScrollPane.setUI(new MetalScrollBarUI() {
		//		      @Override protected void paintTrack(    Graphics g, JComponent c, Rectangle trackBounds) {
		//		          super.paintTrack(g, c, trackBounds);
		//		          Rectangle rect = treeScrollPane.getBounds();
		//		          double sy = trackBounds.getHeight() / rect.getHeight();
		//		          AffineTransform at = AffineTransform.getScaleInstance(1.0, sy);
		//		          Highlighter highlighter = textArea.getHighlighter();
		//		          g.setColor(Color.YELLOW);
		//		          try{
		//		            for(Highlighter.Highlight hh: highlighter.getHighlights()) {
		//		              Rectangle r = textArea.modelToView(hh.getStartOffset());
		//		              Rectangle s = at.createTransformedShape(r).getBounds();
		//		              int h = 2; //Math.max(2, s.height-2);
		//		              g.fillRect(trackBounds.x+2, trackBounds.y+1+s.y, trackBounds.width, h);
		//		            }
		//		          } catch(BadLocationException e) {
		//		            e.printStackTrace();
		//		          }
		//		        }
		//		      });
				jSplitPane.setLeftComponent(treeScrollPane);
				
				treechanyelian = new JTree();
				treeScrollPane.setViewportView(treechanyelian);
				
				notesScrollPane = new JScrollPane();
				jSplitPane.setRightComponent(notesScrollPane);
				
				notesPane = new JTextPane();
				notesScrollPane.setViewportView(notesPane);
				
        notesScrollPane.setVisible(false);
        notesScrollPane.setSize(300, notesScrollPane.getHeight());
        jSplitPane.setResizeWeight(0.0);
        jSplitPane.setDividerSize(0);
        
        scrollPanegegu = new JScrollPane();
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
        
        tfldfindbk = new JTextField();
        
        		
        		tfldfindbk.setColumns(10);
        		
        		btnfindbk = new JButton("\u5B9A\u4F4D\u677F\u5757");
        		
        		tfldfindgegu = new JTextField();
        		tfldfindgegu.setColumns(10);
        		
        		JButton button = new JButton("\u5B9A\u4F4D\u4E2A\u80A1");
        		
        		
        		addSubnodeButton = new SubnodeButton();
        		addSubnodeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/subnode24.png"))); // NOI18N
        		addSubnodeButton.setToolTipText("添加子板块");
        		addSubnodeButton.setFocusable(false);
        		addSubnodeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        		addSubnodeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        		
        		
        		
        		addGeGuButton = new SubnodeButton();
        		addGeGuButton.setIcon(new ImageIcon(Ginkgo2.class.getResource("/images/subnode24.png")));
        		addGeGuButton.setToolTipText("添加个股");
        		addGeGuButton.setFocusable(false);
        		addGeGuButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        		addGeGuButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        		
        		moveButton = new MoveButton();
        		moveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/move24.png"))); // NOI18N
        		moveButton.setToolTipText("Move node");
        		moveButton.setFocusable(false);
        		moveButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        		moveButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        		
        		
        		deleteButton = new JButton("");
        		deleteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/minus_red20.png"))); // NOI18N
        		deleteButton.setToolTipText("Remove node (DELETE)");
        		deleteButton.setFocusable(false);
        		deleteButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        		deleteButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        		
        		saveButton = new JButton("");
        		saveButton.setEnabled(false);
        		saveButton.setIcon(new ImageIcon(Ginkgo2.class.getResource("/images/save24.png")));
        		saveButton.setToolTipText("Save");
        		saveButton.setFocusable(false);
        		saveButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        		saveButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        		
        		
        		notesButton = new JButton("");
        		notesButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/document24.png"))); // NOI18N
        		notesButton.setToolTipText("Notes");
        		notesButton.setFocusable(false);
        		notesButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        		notesButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        		
        		btnLeftToRight = new JButton("\u52A0\u5165\u91CD\u70B9\u5173\u6CE8");
        		
        		btnRightToLeft = new JButton("\u79FB\u9664\u91CD\u70B9\u5173\u6CE8");
        		GroupLayout gl_panelcyl = new GroupLayout(panelcyl);
        		gl_panelcyl.setHorizontalGroup(
        			gl_panelcyl.createParallelGroup(Alignment.LEADING)
        				.addGroup(gl_panelcyl.createSequentialGroup()
        					.addContainerGap()
        					.addGroup(gl_panelcyl.createParallelGroup(Alignment.LEADING)
        						.addGroup(gl_panelcyl.createSequentialGroup()
        							.addComponent(saveButton)
        							.addPreferredGap(ComponentPlacement.RELATED)
        							.addComponent(moveButton, GroupLayout.PREFERRED_SIZE, 58, GroupLayout.PREFERRED_SIZE)
        							.addPreferredGap(ComponentPlacement.RELATED)
        							.addComponent(deleteButton)
        							.addPreferredGap(ComponentPlacement.RELATED)
        							.addComponent(notesButton)
        							.addPreferredGap(ComponentPlacement.RELATED)
        							.addComponent(tfldfindbk, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        							.addPreferredGap(ComponentPlacement.RELATED)
        							.addComponent(btnfindbk))
        						.addGroup(gl_panelcyl.createSequentialGroup()
        							.addComponent(jSplitPane, GroupLayout.PREFERRED_SIZE, 361, GroupLayout.PREFERRED_SIZE)
        							.addPreferredGap(ComponentPlacement.RELATED)
        							.addGroup(gl_panelcyl.createParallelGroup(Alignment.LEADING, false)
        								.addComponent(addSubnodeButton, 0, 0, Short.MAX_VALUE)
        								.addComponent(addGeGuButton, GroupLayout.PREFERRED_SIZE, 44, Short.MAX_VALUE)))
        						.addGroup(gl_panelcyl.createSequentialGroup()
        							.addComponent(btnLeftToRight)
        							.addGap(34)
        							.addComponent(btnRightToLeft)))
        					.addGap(13)
        					.addGroup(gl_panelcyl.createParallelGroup(Alignment.TRAILING)
        						.addGroup(gl_panelcyl.createSequentialGroup()
        							.addComponent(tfldfindgegu, GroupLayout.PREFERRED_SIZE, 96, GroupLayout.PREFERRED_SIZE)
        							.addPreferredGap(ComponentPlacement.RELATED)
        							.addComponent(button))
        						.addComponent(scrollPanegegu, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE)
        						.addComponent(btnAddSubBk)
        						.addComponent(scrollPaneTags, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE))
        					.addGap(80))
        		);
        		gl_panelcyl.setVerticalGroup(
        			gl_panelcyl.createParallelGroup(Alignment.LEADING)
        				.addGroup(gl_panelcyl.createSequentialGroup()
        					.addContainerGap()
        					.addGroup(gl_panelcyl.createParallelGroup(Alignment.BASELINE)
        						.addComponent(btnLeftToRight)
        						.addComponent(btnAddSubBk)
        						.addComponent(btnRightToLeft))
        					.addPreferredGap(ComponentPlacement.UNRELATED)
        					.addGroup(gl_panelcyl.createParallelGroup(Alignment.LEADING)
        						.addGroup(gl_panelcyl.createSequentialGroup()
        							.addGroup(gl_panelcyl.createParallelGroup(Alignment.LEADING)
        								.addComponent(jSplitPane, GroupLayout.PREFERRED_SIZE, 357, GroupLayout.PREFERRED_SIZE)
        								.addGroup(gl_panelcyl.createSequentialGroup()
        									.addGap(34)
        									.addComponent(addSubnodeButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        									.addGap(159)
        									.addComponent(addGeGuButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
        							.addPreferredGap(ComponentPlacement.RELATED)
        							.addGroup(gl_panelcyl.createParallelGroup(Alignment.LEADING, false)
        								.addComponent(deleteButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        								.addGroup(gl_panelcyl.createParallelGroup(Alignment.BASELINE)
        									.addComponent(tfldfindbk, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
        									.addComponent(btnfindbk))
        								.addComponent(saveButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        								.addComponent(moveButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        								.addComponent(notesButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        						.addGroup(gl_panelcyl.createSequentialGroup()
        							.addComponent(scrollPaneTags, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
        							.addPreferredGap(ComponentPlacement.RELATED)
        							.addComponent(scrollPanegegu, 0, 0, Short.MAX_VALUE)
        							.addPreferredGap(ComponentPlacement.RELATED)
        							.addGroup(gl_panelcyl.createParallelGroup(Alignment.BASELINE)
        								.addComponent(button)
        								.addComponent(tfldfindgegu, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        							.addGap(4)))
        					.addGap(43))
        		);
        		panelcyl.setLayout(gl_panelcyl);
		
		JSeparator separator = new JSeparator();
		GroupLayout gl_panelzdgz = new GroupLayout(panelzdgz);
		gl_panelzdgz.setHorizontalGroup(
			gl_panelzdgz.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelzdgz.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelzdgz.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelzdgz.createSequentialGroup()
							.addComponent(btnSaveZdgz)
							.addPreferredGap(ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
							.addComponent(btnGenTDXCode)
							.addGap(57))
						.addGroup(gl_panelzdgz.createSequentialGroup()
							.addComponent(scrollPanesglzdgz, GroupLayout.PREFERRED_SIZE, 251, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panelzdgz.createParallelGroup(Alignment.LEADING)
								.addComponent(buttonremoveoffical)
								.addComponent(buttonaddofficial))
							.addPreferredGap(ComponentPlacement.RELATED)))
					.addGroup(gl_panelzdgz.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelzdgz.createSequentialGroup()
							.addComponent(btnadddalei)
							.addGap(18)
							.addComponent(btndeldalei))
						.addGroup(gl_panelzdgz.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(scrollPaneallzdgz, GroupLayout.PREFERRED_SIZE, 519, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(42, Short.MAX_VALUE))
				.addGroup(Alignment.TRAILING, gl_panelzdgz.createSequentialGroup()
					.addGroup(gl_panelzdgz.createParallelGroup(Alignment.TRAILING)
						.addGroup(Alignment.LEADING, gl_panelzdgz.createSequentialGroup()
							.addContainerGap()
							.addComponent(separator, GroupLayout.DEFAULT_SIZE, 848, Short.MAX_VALUE))
						.addComponent(panelcyl, GroupLayout.DEFAULT_SIZE, 858, Short.MAX_VALUE))
					.addGap(20))
		);
		gl_panelzdgz.setVerticalGroup(
			gl_panelzdgz.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelzdgz.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelzdgz.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnadddalei)
						.addComponent(btndeldalei)
						.addComponent(btnSaveZdgz)
						.addComponent(btnGenTDXCode))
					.addGroup(gl_panelzdgz.createParallelGroup(Alignment.LEADING)
						.addGroup(Alignment.TRAILING, gl_panelzdgz.createSequentialGroup()
							.addGap(29)
							.addComponent(buttonaddofficial)
							.addPreferredGap(ComponentPlacement.RELATED, 110, Short.MAX_VALUE)
							.addComponent(buttonremoveoffical)
							.addGap(65))
						.addGroup(gl_panelzdgz.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_panelzdgz.createParallelGroup(Alignment.BASELINE)
								.addComponent(scrollPanesglzdgz, GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
								.addComponent(scrollPaneallzdgz, GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE))))
					.addGap(7)
					.addComponent(separator, GroupLayout.PREFERRED_SIZE, 5, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panelcyl, GroupLayout.PREFERRED_SIZE, 463, GroupLayout.PREFERRED_SIZE)
					.addGap(47))
		);
		
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
		//scrollPane.setViewportView(tableZdgzBankDetails);

		scrollPaneallzdgz.setViewportView(tableZdgzBankDetails);
		
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

		scrollPanesglzdgz.setViewportView(tableCurZdgzbk);
		panelzdgz.setLayout(gl_panelzdgz);
		
		JSeparator separator_1 = new JSeparator();
		GroupLayout gl_panelparsefile = new GroupLayout(panelparsefile);
		gl_panelparsefile.setHorizontalGroup(
			gl_panelparsefile.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelparsefile.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panelparsefile.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelparsefile.createSequentialGroup()
							.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 93, GroupLayout.PREFERRED_SIZE)
							.addGap(18)
							.addComponent(textField, GroupLayout.PREFERRED_SIZE, 352, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btnChsParseFile)
							.addContainerGap(314, Short.MAX_VALUE))
						.addComponent(panelzdgz, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 878, Short.MAX_VALUE)
						.addGroup(gl_panelparsefile.createSequentialGroup()
							.addComponent(separator_1, GroupLayout.PREFERRED_SIZE, 865, GroupLayout.PREFERRED_SIZE)
							.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
		);
		gl_panelparsefile.setVerticalGroup(
			gl_panelparsefile.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelparsefile.createSequentialGroup()
					.addGap(20)
					.addGroup(gl_panelparsefile.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel, GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
						.addComponent(textField, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnChsParseFile))
					.addGap(7)
					.addComponent(separator_1, GroupLayout.PREFERRED_SIZE, 4, GroupLayout.PREFERRED_SIZE)
					.addGap(7)
					.addComponent(panelzdgz, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		panelparsefile.setLayout(gl_panelparsefile);
		
		setLayout(groupLayout);
		
        java.util.ArrayList<java.awt.Image> imageList = new java.util.ArrayList<java.awt.Image>();
        imageList.add(new javax.swing.ImageIcon(getClass().getResource("/images/ginkgo16.png")).getImage());
        imageList.add(new javax.swing.ImageIcon(getClass().getResource("/images/ginkgo18.png")).getImage());
        imageList.add(new javax.swing.ImageIcon(getClass().getResource("/images/ginkgo20.png")).getImage());
        imageList.add(new javax.swing.ImageIcon(getClass().getResource("/images/ginkgo24.png")).getImage());
        imageList.add(new javax.swing.ImageIcon(getClass().getResource("/images/ginkgo32.png")).getImage());
        imageList.add(new javax.swing.ImageIcon(getClass().getResource("/images/ginkgo36.png")).getImage());
        
        //setIconImages(imageList);
		
        moveLeftIcon = new javax.swing.ImageIcon(getClass().getResource("/images/moveleft24.png"));
        moveUpIcon = new javax.swing.ImageIcon(getClass().getResource("/images/moveup24.png"));
        moveDownIcon = new javax.swing.ImageIcon(getClass().getResource("/images/movedown24.png"));
        moveIcon = new javax.swing.ImageIcon(getClass().getResource("/images/move24.png"));
        addBelowIcon = new javax.swing.ImageIcon(getClass().getResource("/images/subnodeBelow24.png"));
        addAboveIcon = new javax.swing.ImageIcon(getClass().getResource("/images/subnodeAbove24.png"));
        addSubnodeIcon = new javax.swing.ImageIcon(getClass().getResource("/images/subnode24.png"));
        addChildIcon = new javax.swing.ImageIcon(getClass().getResource("/images/subnodeChild24.png"));
       // jToolBar2.setVisible(false);
        
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
		 if(gzdalei != null)
			 return gzdalei.size();
		 else 
			 return 0;
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


