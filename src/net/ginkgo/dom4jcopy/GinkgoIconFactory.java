package net.ginkgo.dom4jcopy;

import javax.swing.ImageIcon;

public class GinkgoIconFactory {
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
    
    public ImageIcon getIcon(GinkgoNode node, boolean leaf, boolean expanded){
        
        if(leaf) {
            if (node.getStatus() == GinkgoNode.BKGEGU) icon = activeLeafIcon;
            else if (node.getStatus() == GinkgoNode.SUBBK) icon = completeLeafIcon;
            else if (node.getStatus() == GinkgoNode.TDXBK) icon = nowLeafIcon;
            else icon = inactiveLeafIcon;
        }
        else if (expanded) {
            if (node.getStatus() == GinkgoNode.BKGEGU) icon = activeOpenIcon;
            else if (node.getStatus() == GinkgoNode.SUBBK) icon = completeOpenIcon;
            else if (node.getStatus() == GinkgoNode.TDXBK) icon = nowOpenIcon;
            else icon = inactiveOpenIcon;
        } else {
            if (node.getStatus() == GinkgoNode.BKGEGU) icon = activeClosedIcon;
            else if (node.getStatus() == GinkgoNode.SUBBK) icon = completeClosedIcon;
            else if (node.getStatus() == GinkgoNode.TDXBK) icon = nowClosedIcon;
            else icon = inactiveClosedIcon;            
        }
        return icon;
    }
    
}
