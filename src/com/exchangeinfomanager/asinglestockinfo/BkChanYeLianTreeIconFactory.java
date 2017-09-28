package com.exchangeinfomanager.asinglestockinfo;

import javax.swing.ImageIcon;
import javax.swing.ImageIcon;

public class BkChanYeLianTreeIconFactory {

	public BkChanYeLianTreeIconFactory() {
		// TODO Auto-generated constructor stub
	}
	
	  	ImageIcon completeLeafIcon = new ImageIcon(getClass().getResource("/images/check15.png"));
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
	            else if (node.getType() == BkChanYeLianTreeNode.SUBBK) icon = completeLeafIcon;
	            else if (node.getType() == BkChanYeLianTreeNode.TDXBK) icon = nowLeafIcon;
	            else icon = inactiveLeafIcon;
	        }
	        else if (expanded) {
	            if (node.getType() == BkChanYeLianTreeNode.BKGEGU) icon = activeOpenIcon;
	            else if (node.getType() == BkChanYeLianTreeNode.SUBBK) icon = completeOpenIcon;
	            else if (node.getType() == BkChanYeLianTreeNode.TDXBK) icon = nowOpenIcon;
	            else icon = inactiveOpenIcon;
	        } else {
	            if (node.getType() == BkChanYeLianTreeNode.BKGEGU) icon = activeClosedIcon;
	            else if (node.getType() == BkChanYeLianTreeNode.SUBBK) icon = completeClosedIcon;
	            else if (node.getType() == BkChanYeLianTreeNode .TDXBK) icon = nowClosedIcon;
	            else icon = inactiveClosedIcon;            
	        }
	        return icon;
	    }

}
