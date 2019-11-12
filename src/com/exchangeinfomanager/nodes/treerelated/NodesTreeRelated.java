package com.exchangeinfomanager.nodes.treerelated;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.tree.TreeNode;

import com.exchangeinfomanager.commonlib.CommonUtility;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

/*
 * 锟酵诧拷业锟斤拷锟斤拷锟斤拷氐募锟斤拷锟�
 */
public class NodesTreeRelated
{
		public NodesTreeRelated (BkChanYeLianTreeNode treenode1)
		{
			treenode = treenode1; 
			expanded = false;
		}
		
		private BkChanYeLianTreeNode treenode;
		private boolean expanded;

	    public void setExpansion(boolean expanded){
	        this.expanded = expanded;
	    }
	    
	    public boolean isExpanded(){
	        return expanded;
	    }
		/**
		 * @return the tdxbk
		 */
		public String getChanYeLianSuoShuTdxBanKuaiName() 
		{
			TreeNode[] nodepath = treenode.getPath();
			String tdxbk =	 ((BkChanYeLianTreeNode)nodepath[1]).getUserObject().toString() ;
			return tdxbk;
		}

		/**
		 * @return the node
		 */
		public String getNodeCurLocatedChanYeLian() {
			 String bkchanyelianincode = "";
			 TreeNode[] nodepath = treenode.getPath();
			 for(int i=1;i<nodepath.length;i++) {
				 bkchanyelianincode = bkchanyelianincode + ((BkChanYeLianTreeNode)nodepath[i]).getMyOwnCode() + "->";
			 }

			return bkchanyelianincode.substring(0,bkchanyelianincode.length()-2);
		}
		/*
		 * 
		 */
		public String getNodeCurLocatedChanYeLianByName ()
		{
			String bkchanyelianinname = "";
			 TreeNode[] nodepath = treenode.getPath();
			 for(int i=1;i<nodepath.length;i++) {
				 bkchanyelianinname = bkchanyelianinname + ((BkChanYeLianTreeNode)nodepath[i]).getMyOwnName() + "->";
			 }

			return bkchanyelianinname.substring(0,bkchanyelianinname.length()-2);
		}
		
	
	}
 	
