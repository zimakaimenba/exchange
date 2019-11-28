package com.exchangeinfomanager.Trees;

import javax.swing.ImageIcon;

import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

import javax.swing.ImageIcon;

public class BkChanYeLianTreeIconFactory {

	public BkChanYeLianTreeIconFactory() {
		// TODO Auto-generated constructor stub
	}
	
	  	ImageIcon completeLeafIcon = new ImageIcon(getClass().getResource("/images/blueCircle15.png"));
	    ImageIcon completeClosedIcon = new ImageIcon(getClass().getResource("/images/greenClosed15.png"));
	    ImageIcon completeOpenIcon = new ImageIcon(getClass().getResource("/images/greenOpen15.png"));
	    
	    ImageIcon activeLeafIcon = new ImageIcon(getClass().getResource("/images/blueCircle15.png"));
	    ImageIcon activeClosedIcon = new ImageIcon(getClass().getResource("/images/blueClosed15.png"));
	    ImageIcon activeOpenIcon = new ImageIcon(getClass().getResource("/images/blueOpen15.png"));
	    
	    ImageIcon inactiveLeafIcon = new ImageIcon(getClass().getResource("/images/greyCircle15.png"));
	    ImageIcon inactiveClosedIcon = new ImageIcon(getClass().getResource("/images/greyClosed15.png"));
	    ImageIcon inactiveOpenIcon = new ImageIcon(getClass().getResource("/images/greyOpen15.png"));
	    
	    ImageIcon nowLeafIcon = new ImageIcon(getClass().getResource("/images/star15.png"));
	    ImageIcon nowClosedIcon = new ImageIcon(getClass().getResource("/images/starClosed15.png"));
	    ImageIcon nowOpenIcon = new ImageIcon(getClass().getResource("/images/starOpen15.png"));
	    
	    ImageIcon icon;
	    
	    public ImageIcon getIcon(BkChanYeLianTreeNode node, boolean leaf, boolean expanded){
	        
	        if(leaf) {
	            if (node.getType() == BkChanYeLianTreeNode.BKGEGU) icon = activeLeafIcon;
	            else if (node.getType() == BkChanYeLianTreeNode.TDXBK) icon = nowLeafIcon;
	            else icon = inactiveLeafIcon;
	        }
	        else if (expanded) {
	            if (node.getType() == BkChanYeLianTreeNode.BKGEGU) icon = activeOpenIcon;
	            else if (node.getType() == BkChanYeLianTreeNode.TDXBK) icon = nowOpenIcon;
	            else icon = inactiveOpenIcon;
	        } else {
	            if (node.getType() == BkChanYeLianTreeNode.BKGEGU) icon = activeClosedIcon;
	            else if (node.getType() == BkChanYeLianTreeNode .TDXBK) icon = nowClosedIcon;
	            else icon = inactiveClosedIcon;            
	        }
	        return icon;
	    }
	    
	    ImageIcon guPiaoChiHasChildIcon = new ImageIcon(getClass().getResource("/images/starClosed15.png"));
	    ImageIcon guPiaoChiNoChildIcon = new ImageIcon(getClass().getResource("/images/greyClosed15.png"));
	    
	    ImageIcon subGuPiaoHasChildIcon = new ImageIcon(getClass().getResource("/images/star15.png"));
	    ImageIcon subGuPiaoNoChildIcon = new ImageIcon(getClass().getResource("/images/greyCircle15.png"));
	    
	    ImageIcon banKuaiHasChildIcon = new ImageIcon(getClass().getResource("/images/flagred.png"));
	    ImageIcon banKuaiNoChildIcon = new ImageIcon(getClass().getResource("/images/flaggrey.png"));
	    
	    ImageIcon subBanKuaiHasChildIcon = new ImageIcon(getClass().getResource("/images/star15.png"));
	    ImageIcon subBanKuaiNoChildIcon = new ImageIcon(getClass().getResource("/images/star15.png"));
	    
	    ImageIcon stockIcon = new ImageIcon(getClass().getResource("/images/check15.png"));
	    
	    
	    public ImageIcon getIcon(BkChanYeLianTreeNode node)
	    {
	    	if (node.getType() == BkChanYeLianTreeNode.GPC) {
	    		if(node.getChildCount() > 0)
	    			icon =  guPiaoChiHasChildIcon;
	    		else icon = guPiaoChiNoChildIcon;
	    	} else	    		
	    	if (node.getType() == BkChanYeLianTreeNode.SUBGPC) {
	    		if(node.getChildCount() > 0)
	    			icon = subGuPiaoHasChildIcon;
	    		else icon = subGuPiaoNoChildIcon;
	    	} else
	    	if (node.getType() == BkChanYeLianTreeNode.TDXBK) {
	    		if(node.getChildCount() > 0)
	    			icon = banKuaiHasChildIcon;
	    		else icon = banKuaiNoChildIcon;
	    	} else
	    	if (node.getType() == BkChanYeLianTreeNode.TDXGG) {
	    			icon = stockIcon;
	    	} else
	    		icon = stockIcon;
	    	
			return icon;
	    }

}
