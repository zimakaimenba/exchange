package com.exchangeinfomanager.bankuaichanyelian;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.HashSet;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;



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
			 
	         int nodetype = ((BkChanYeLianTreeNode)value).getNodeType();
	         String bktreenodename;
	         if(nodetype != BkChanYeLianTreeNode.BKGEGU)
	        	 bktreenodename = ((BkChanYeLianTreeNode)value).getUserObject().toString();
	         else
	        	 bktreenodename = ((BkChanYeLianTreeNode)value).getNodeOwnCode() + ((BkChanYeLianTreeNode)value).getUserObject().toString();
	         
	         

	         HashSet<String> parsefilestockset = ((BkChanYeLianTreeNode)value).getParseFileStockSet ();
	         if(parsefilestockset !=null && parsefilestockset.size() !=0) {
	        	 lblnodenameandcount.setText( bktreenodename + " " + "(" + parsefilestockset.size() + ")"  ); 
	         } else
	        	 lblnodenameandcount.setText( bktreenodename );
	         
	         BkChanYeLianTreeNode node = (BkChanYeLianTreeNode) value;
	        //ICON
	         
	         lblnodenameandcount.setIcon(bkcyliconfactory.getIcon(node, leaf, expanded));
	         
	         //各种状态下的COLOR
	         if(node.getInZdgzOfficalCount() >0 ) {
	        	 lblnodenameandcount.setForeground(Color.RED);
	         } else if(node.getInZdgzCandidateCount() >0 ) {
	        	 lblnodenameandcount.setForeground(Color.ORANGE);
	         } else if(node.getParseFileStockSet().size()>0) {
	        	 lblnodenameandcount.setForeground(Color.BLUE);
	         } else 
	        	 lblnodenameandcount.setForeground(this.getForeground());
	         
	         //如果是要删除的节点，用特殊的字体表示
	         if(node.shouldBeRemovedWhenSaveXml()) {
	        	 Font font=new Font("黑体",Font.BOLD + Font.ITALIC,14); 
		         lblnodenameandcount.setFont(font);
	         } else {
	        	 Font font=new Font("宋体",Font.PLAIN,14); 
		         lblnodenameandcount.setFont(font);
	         }
	        	 
	         
	        	 
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
