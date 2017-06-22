package net.ginkgo.dom4jcopy;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.*;

import com.exchangeinfomanager.checkboxtree.CheckBoxTreeNode;

public class CustomTreeCellRenderer extends DefaultTreeCellRenderer{

	private static final long serialVersionUID = 1L;
	GinkgoIconFactory ginkgoIconFactory;
 
    
    public CustomTreeCellRenderer(){
        ginkgoIconFactory = new GinkgoIconFactory();
        
    }
    
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                                                  boolean leaf, int row, boolean hasFocus)
    {

        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        
        
        //System.out.println(value.getClass().toString());
        if (value.getClass().toString().equals("class net.ginkgo.dom4jcopy.GinkgoNode")) {
            GinkgoNode node = (GinkgoNode) value;
            setIcon(ginkgoIconFactory.getIcon(node, leaf, expanded));
        }
       
        return this;
    }
    
}
