package com.exchangeinfomanager.nodes.operations;

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

import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;
import com.exchangeinfomanager.nodes.treerelated.NodesTreeRelated;
import com.exchangeinfomanager.nodes.treerelated.BanKuaiTreeRelated;;

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
	         
	         // ÿ�հ�������Ϣ
	         Integer patchfilestocknum = 0; Boolean selfisin = false;
	         if(nodetype == BkChanYeLianTreeNode.TDXBK ) {
	        	 LocalDate diswk = ((BanKuaiAndStockTree)tree).getCurrentDisplayedWk ();
	        	 NodesTreeRelated tmptreerelated = ((BkChanYeLianTreeNode)value).getNodeTreeRelated (); 
	        	 patchfilestocknum = ((BanKuaiTreeRelated)tmptreerelated).getStocksNumInParsedFileForSpecificDate (diswk);
	        	 
	        	 selfisin = ((BanKuaiTreeRelated)tmptreerelated).selfIsMatchModel (diswk);
	         }
	         
	         if( patchfilestocknum != null  && patchfilestocknum > 0) {
	        	 lblnodenameandcount.setText( bktreenodename + " " + "(" + patchfilestocknum + ")"  ); 
	         } else
	        	 lblnodenameandcount.setText( bktreenodename );
	         
	         BkChanYeLianTreeNode node = (BkChanYeLianTreeNode) value;
	        //ICON
	         
//	         lblnodenameandcount.setIcon(bkcyliconfactory.getIcon(node, leaf, expanded)); //
	         lblnodenameandcount.setIcon(bkcyliconfactory.getIcon(node));
	         
	         //����״̬�µ�COLOR
	         if(patchfilestocknum != null && patchfilestocknum > 0) //��麬������ģ�͵ĸ��� 
	        	 lblnodenameandcount.setForeground(Color.ORANGE);
	         else 
	        	 lblnodenameandcount.setForeground(this.getForeground());
	         
	         if(selfisin) {//�����������ģ��,�ô���
	        	 Font font = new Font("����",Font.BOLD + Font.ITALIC,14);
	        	 lblnodenameandcount.setFont(font);
	         } else {
	        	 Font font=new Font("����",Font.PLAIN,14); 
		         lblnodenameandcount.setFont(font);
	         }
	         
	         
	         //�����Ҫɾ���Ľڵ㣬������������ʾ
//	         if(node.getNodetreerelated().shouldBeRemovedWhenSaveXml()) {
//	        	 Font font=new Font("����",Font.BOLD + Font.ITALIC,14); 
//		         lblnodenameandcount.setFont(font);
//	         } else {
//	        	 Font font=new Font("����",Font.PLAIN,14); 
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