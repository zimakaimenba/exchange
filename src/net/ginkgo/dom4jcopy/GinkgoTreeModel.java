package net.ginkgo.dom4jcopy;

import javax.swing.tree.*;

public class GinkgoTreeModel extends DefaultTreeModel {
 
	private static final long serialVersionUID = 1L;

	public GinkgoTreeModel(GinkgoNode topNode){
        super(topNode);
    }
    
    @Override
    public void nodeChanged(TreeNode node){
        super.nodeChanged(node);
        System.out.println("Node changed");
    }
    
}
