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
	        shouldberemovedwhensavexml = false;
		}
		
		private BkChanYeLianTreeNode treenode;
		private boolean expanded;
		private boolean isofficallyselected ;
		private int inzdgzofficalcount =0;
		private int inzdgzcandidatecount =0;
		private LocalDate selectedToZdgzTime;
		private LocalDate addedtocyltreetime;
		private boolean shouldberemovedwhensavexml;
		
		public void setShouldBeRemovedWhenSaveXml ( ) 
		{
			this.shouldberemovedwhensavexml = true;
		}
		public boolean shouldBeRemovedWhenSaveXml ( )
		{
			return this.shouldberemovedwhensavexml;
		}

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
		 * @return the node锟节碉拷前锟斤拷锟斤拷位锟矫ｏ拷也锟斤拷锟角诧拷业锟斤拷锟斤拷锟斤拷code锟斤拷锟斤拷
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
		 * 锟斤拷业锟斤拷锟斤拷锟斤拷锟斤拷锟絚ode,锟斤拷锟斤返锟斤拷锟斤拷锟斤拷
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
		/**
		 * @return the selectedtime
		 */
		public String getSelectedToZdgzTime() {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM-dd");
			return selectedToZdgzTime.format(formatter);
		}
		/**
		 * @param selectedtime the selectedtime to set
		 */
		public void setSelectedToZdgzTime(LocalDate selectedtime) {
			this.selectedToZdgzTime = selectedtime;
		}
		public void setSelectedToZdgzTime(String selectedtime2) 
		{
//			SimpleDateFormat formatterhwy=new SimpleDateFormat("yy-MM-dd");
//			Date date = new Date() ;
//			try {
//				date = formatterhwy.parse(selectedtime2);
//			} catch (ParseException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			this.selectedToZdgzTime = CommonUtility.formateStringToDate(selectedtime2);
		}
		public void setAddedToCylTreeTime (String addedtime2)
		{
			this.addedtocyltreetime	 = CommonUtility.formateStringToDate(addedtime2);
		}
	
	}
 	
