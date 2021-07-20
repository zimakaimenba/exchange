package com.exchangeinfomanager.Core.Trees;

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

import com.exchangeinfomanager.Core.Nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.Core.Nodes.CylTreeNestedSetNode;
import com.exchangeinfomanager.Core.exportimportrelated.BanKuaiTreeRelated;
import com.exchangeinfomanager.Core.exportimportrelated.NodesTreeRelated;;

public class BkChanYeLianTreeCellRenderer extends DefaultTreeCellRenderer 
{
	public BkChanYeLianTreeCellRenderer ()
	{
		lblnodenameandcount = new JLabel (" ");
		this.bkcyliconfactory = new BkChanYeLianTreeIconFactory ();
	}
	
	private JLabel lblnodenameandcount;
	private BkChanYeLianTreeIconFactory bkcyliconfactory;
	
	 public Component getTreeCellRendererComponent(JTree tree, Object value,  
             boolean isSelected, boolean expanded, boolean leaf, int row,  
             boolean hasFocus) 
     {  
		 Component returnValue = null;
		 if(value !=null && value instanceof CylTreeNestedSetNode) {
			 lblnodenameandcount.setToolTipText(  ((CylTreeNestedSetNode)value).getMyOwnCode()  );
			 
	         int nodetype = ((CylTreeNestedSetNode)value).getType();
	         String bktreenodename =  ((CylTreeNestedSetNode)value).getMyOwnName();
	         lblnodenameandcount.setText( bktreenodename );
	         
	        //ICON
	         lblnodenameandcount.setIcon(bkcyliconfactory.getIcon((CylTreeNestedSetNode) value));
	         
	         //各种状态下的COLOR
	         if(nodetype == BkChanYeLianTreeNode.DZHBK)
	        	 lblnodenameandcount.setForeground(Color.BLUE);
	         else	lblnodenameandcount.setForeground(this.getForeground());
	         
        	 Font font=new Font("宋体",Font.PLAIN,14); 
	         lblnodenameandcount.setFont(font);
	         
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
		      returnValue = super.getTreeCellRendererComponent(tree, returnValue, selected, expanded, leaf, row, hasFocus);
		  }
		 
		 return returnValue;
     }

}
