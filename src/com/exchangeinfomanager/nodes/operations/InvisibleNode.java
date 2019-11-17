package com.exchangeinfomanager.nodes.operations;


import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

//http://www.java2s.com/Code/Java/Swing-Components/InvisibleNodeTreeExample.htm
public class InvisibleNode extends DefaultMutableTreeNode    
{

	protected boolean isVisible;
	
	public InvisibleNode() {
	 this(null);
	}
	
	public InvisibleNode(Object userObject) {
	 this(userObject, true, true);
	}
	
	public InvisibleNode(Object userObject, boolean allowsChildren,
	   boolean isVisible) {
	 super(userObject, allowsChildren);
	 this.isVisible = isVisible;
	}
	
	public TreeNode getChildAt(int index, boolean filterIsActive) {
	 if (!filterIsActive) {
	   return super.getChildAt(index);
	 }
	 if (children == null) {
	   throw new ArrayIndexOutOfBoundsException("node has no children");
	 }
	
	 int realIndex = -1;
	 int visibleIndex = -1;
	 Enumeration e = children.elements();
	 while (e.hasMoreElements()) {
	   InvisibleNode node = (InvisibleNode) e.nextElement();
	   if (node.isVisible()) {
	     visibleIndex++;
	   }
	   realIndex++;
	   if (visibleIndex == index) {
	     return (TreeNode) children.elementAt(realIndex);
	   }
	 }
	
	 throw new ArrayIndexOutOfBoundsException("index unmatched");
	 //return (TreeNode)children.elementAt(index);
	}
	
	public int getChildCount(boolean filterIsActive) {
	 if (!filterIsActive) {
	   return super.getChildCount();
	 }
	 if (children == null) {
	   return 0;
	 }
	
	 int count = 0;
	 Enumeration e = children.elements();
	 while (e.hasMoreElements()) {
	   InvisibleNode node = (InvisibleNode) e.nextElement();
	   if (node.isVisible()) {
	     count++;
	   }
	 }
	
	 return count;
	}
	
	public void setVisible(boolean visible) {
	 this.isVisible = visible;
	}
	
	public boolean isVisible() {
	 return isVisible;
	}
	
	 
}