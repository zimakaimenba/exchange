package com.exchangeinfomanager.asinglestockinfo;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.time.LocalDate;
import java.util.HashSet;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.exchangeinfomanager.asinglestockinfo.BkChanYeLianTreeNode.TreeRelated;



public class BkChanYeLianTreeCellRenderer extends DefaultTreeCellRenderer 
{
	public BkChanYeLianTreeCellRenderer ()
	{
//		pnlrenderer = new JPanel();
		lblnodenameandcount = new JLabel (" ");
		this.bkcyliconfactory = new BkChanYeLianTreeIconFactory ();
		
//		pnlrenderer.add(lblnodenameandcount);
	}
	
	private JLabel lblnodenameandcount;
	//private JPanel pnlrenderer ;
	private BkChanYeLianTreeIconFactory bkcyliconfactory;
	
	 public Component getTreeCellRendererComponent(JTree tree, Object value,  
             boolean isSelected, boolean expanded, boolean leaf, int row,  
             boolean hasFocus) 
     {  
		 Component returnValue = null;
		 if(value !=null && value instanceof DefaultMutableTreeNode) {
			 
	         int nodetype = ((BkChanYeLianTreeNode)value).getType();
	         
	         String bktreenodename;
	         if(nodetype == BkChanYeLianTreeNode.BKGEGU || nodetype == BkChanYeLianTreeNode.TDXGG)
	        	 bktreenodename = ((BkChanYeLianTreeNode)value).getMyOwnCode() + ((BkChanYeLianTreeNode)value).getMyOwnName();
	         else
	        	 bktreenodename = ((BkChanYeLianTreeNode)value).getMyOwnName();
	         
	         // 每日板块分析信息
	         Integer patchfilestocknum = 0; Boolean selfisin = false;
	         if(nodetype == BkChanYeLianTreeNode.TDXBK ) {
	        	 LocalDate diswk = ((BkChanYeLianTree)tree).getCurrentDisplayedWk ();
	        	 TreeRelated tmptreerelated = ((BkChanYeLianTreeNode)value).getNodeTreerelated (); 
	        	 patchfilestocknum = ((BanKuai.BanKuaiTreeRelated)tmptreerelated).getStocksNumInParsedFileForSpecificDate (diswk);
	        	 
	        	 selfisin = ((BanKuai.BanKuaiTreeRelated)tmptreerelated).selfIsMatchModel (diswk);
	         }
	         
	         if( patchfilestocknum != null  && patchfilestocknum > 0) {
	        	 lblnodenameandcount.setText( bktreenodename + " " + "(" + patchfilestocknum + ")"  ); 
	         } else
	        	 lblnodenameandcount.setText( bktreenodename );
	         
	         BkChanYeLianTreeNode node = (BkChanYeLianTreeNode) value;
	        //ICON
	         
//	         lblnodenameandcount.setIcon(bkcyliconfactory.getIcon(node, leaf, expanded)); //
	         lblnodenameandcount.setIcon(bkcyliconfactory.getIcon(node));
	         
	         //各种状态下的COLOR
	         if(patchfilestocknum != null && patchfilestocknum > 0) //板块含有满足模型的个股 
	        	 lblnodenameandcount.setForeground(Color.ORANGE);
	         else 
	        	 lblnodenameandcount.setForeground(this.getForeground());
	         
	         if(selfisin) {//板块自身满足模型,用粗体
	        	 Font font = new Font("黑体",Font.BOLD + Font.ITALIC,14);
	        	 lblnodenameandcount.setFont(font);
	         } else {
	        	 Font font=new Font("宋体",Font.PLAIN,14); 
		         lblnodenameandcount.setFont(font);
	         }
	         
	         
	         //如果是要删除的节点，用特殊的字体表示
//	         if(node.getNodetreerelated().shouldBeRemovedWhenSaveXml()) {
//	        	 Font font=new Font("黑体",Font.BOLD + Font.ITALIC,14); 
//		         lblnodenameandcount.setFont(font);
//	         } else {
//	        	 Font font=new Font("宋体",Font.PLAIN,14); 
//		         lblnodenameandcount.setFont(font);
//	         }
	        	 
	         if(isSelected) {
	        	 lblnodenameandcount.setOpaque(true);
	        	 lblnodenameandcount.setBackground(this.getBackgroundSelectionColor());
	         } else {
	        	 lblnodenameandcount.setOpaque(true);
	        	 lblnodenameandcount.setBackground(this.getBackgroundNonSelectionColor());
	         }

	         setEnabled(tree.isEnabled());
	         returnValue = lblnodenameandcount;
	         
		 }
		 
		 if (returnValue == null) {
		      returnValue = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		    }
//		 
//         String stringValue = tree.convertValueToText(value, isSelected,expanded, leaf, row, hasFocus);  
         return returnValue;
     }

}
