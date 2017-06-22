/*  
 CCmI Editor - A Collaborative Cross-Modal Diagram Editing Tool
  
 Copyright (C) 2011  Queen Mary University of London (http://ccmi.eecs.qmul.ac.uk/)

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package com.exchangeinfomanager.checkboxtree;

import java.awt.event.MouseAdapter;  
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.JTree;  
import javax.swing.tree.DefaultTreeModel;  
import javax.swing.tree.TreePath;  
import javax.swing.tree.TreeSelectionModel; 

import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.exchangeinfomanager.systemconfigration.SystemConfigration;


public class CheckBoxTree extends JTree 
{  
	  
    //private static final long serialVersionUID = 1L;  
	private String stockcode;
	
	static Element xmlroot;
	private String checklistsitems;
	private Date updatedDate;
	private boolean infochangedsign = false;
	
	SystemConfigration sysconfig;

	public CheckBoxTree(CheckBoxTreeNode checkNode, String stockcode) 
    {  
        super(checkNode);  
        if(stockcode!= null)
        	this.stockcode = stockcode;
        this.setCellRenderer(new CheckBoxTreeCellRenderer());  
        this.setShowsRootHandles(true);  
        this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION );  
        this.putClientProperty("JTree.lineStyle", "Angled");   
        
        this.epdTree(true);
        this.addLister(this);  
        
        this.sysconfig = SystemConfigration.getInstance();
        
    }  
	
	public void setStockcode(String stockcode)
	{
		this.stockcode = stockcode;
	}
  
	public void epdTree(boolean readsavesign) 
    {
    	CheckBoxTreeNode root = (CheckBoxTreeNode)this.getModel().getRoot();
     	
    	String roottxt;
    	if(readsavesign) { //显示树的数据	
//    		try {
//    			 roottxt = this.stockcode + " (" + (this.checklistsitems.length()-10)/6 + ") " + " Last Records: " + this.checklistsitems.substring(0, 10); //一个标志&&TC01一共是6位，所以要计算有几个标志就/6
//    		} catch(java.lang.NullPointerException e) {
//    			 roottxt = this.stockcode + "  (" + "0" + ")";
//    		} catch( java.lang.StringIndexOutOfBoundsException e) {
//    			roottxt = this.stockcode + "  (" + "0" + ")";
//    		}
//    		root.setUserObject(roottxt);
    		
    		expandTree(this, new TreePath(root));
    	} else {
//    		if(true == infochangedsign) { //树的选择项有过改动，要存储
//    			this.checklistsitems = "";
//        		 updatedDate = new Date(); 
//            	//this.checklistsitems = date + this.checklistsitems;
//        		saveTreeItems(this, new TreePath(root));
//    		}
    		//树的选择项有过改动，要存储
    		this.checklistsitems = "";
    		saveTreeItems(this, new TreePath(root));
    		if(true == infochangedsign) 
    			updatedDate = new Date(); 
    	}
    }
	
	private void saveTreeItems(JTree tree, TreePath parent)
	{
		CheckBoxTreeNode node = (CheckBoxTreeNode) parent.getLastPathComponent();

		if (node.getChildCount() >= 0) {
        	String tmp = node.getXmltag();
        	//System.out.println(tmp);
       		if(node.isSelected)
       			this.checklistsitems = this.checklistsitems + "&&" + node.getXmltag();

       		for (Enumeration<?> e = node.children(); e.hasMoreElements();) {
        	   CheckBoxTreeNode n = (CheckBoxTreeNode) e.nextElement();
               TreePath path = parent.pathByAddingChild(n);
               saveTreeItems(tree, path);
           }
        }
	}
    
    private void expandTree(JTree tree, TreePath parent) 
    {
    	CheckBoxTreeNode node = (CheckBoxTreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
        	String tmp = node.getXmltag();
        	//System.out.println(tmp);
        	
        	try {
				if (this.checklistsitems.contains(tmp.trim()))
					node.setSelected(true);
			} catch (java.lang.NullPointerException e) {
				node.setSelected(false);
			}
        	
        	for (Enumeration<?> e = node.children(); e.hasMoreElements();) {
        	   CheckBoxTreeNode n = (CheckBoxTreeNode) e.nextElement();
               TreePath path = parent.pathByAddingChild(n);
               expandTree(tree, path);
           }
        }
        
        tree.expandPath(parent);
    }
    
    
    
    /**
   	 * @return the checklistsitems
   	 */
   	public String getChecklistsitems() {
   		return this.checklistsitems;
   	}
   	public Date getUpdatedDate () {
   		return this.updatedDate;
   	}



   	/**
   	 * @param checklistsitems the checklistsitems to set
   	 */
   	public void setChecklistsitems(String checklistsitems) 
   	{
   		this.checklistsitems = checklistsitems;
   	}

    /*** 
     * 添加点击事件使其选中父节点时 
     * 子节点也选中 
     * @param tree  
     */  
    private void addLister(final JTree tree) 
    {  
    	this.addMouseListener(new MouseAdapter() {
    		
            public void mouseClicked(MouseEvent e) {  
                super.mouseClicked(e);  
                int row = tree.getRowForLocation(e.getX(),e.getY());  
                TreePath path = tree.getPathForRow(row);  
                if (path != null) {  
                	CheckBoxTreeNode node = (CheckBoxTreeNode) path.getLastPathComponent();  
                	
                    node.setSelected(!node.isSelected);  
                    infochangedsign = true; //说明树的选择有改变了。
                    if(node.getSelectionMode() == CheckBoxTreeNode.DIG_IN_SELECTION) {  
                        if(node.isSelected) {  
                            tree.expandPath(path);  
                        } else {  
                            if(!node.isRoot()) {  
                                tree.collapsePath(path);  
                            }  
                        }  
                    }
                    
                    //响应事件更新树  
                    ((DefaultTreeModel) tree.getModel()).nodeChanged(node);  
                    tree.revalidate();  
                    tree.repaint();  
                }  
            }  
        });  
    }  
  
    
  
      
}  