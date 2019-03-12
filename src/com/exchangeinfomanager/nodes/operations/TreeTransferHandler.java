package com.exchangeinfomanager.nodes.operations;

import javax.swing.JComponent;
import javax.swing.TransferHandler;
import javax.swing.JTree;
import javax.swing.tree.*;

import org.apache.log4j.Logger;

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

class TreeTransferHandler extends TransferHandler 
{
    
    private DataFlavor nodeFlavor;
    private static Logger logger = Logger.getLogger(TreeTransferHandler.class);
    
    public TreeTransferHandler(){
        
        try {
            String mimeType = DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" +
                    javax.swing.tree.DefaultMutableTreeNode[].class.getName() + "\"";
            nodeFlavor = new DataFlavor(mimeType);
        } catch (ClassNotFoundException ex) {
            logger.error("ClassNotFound:%s\n"+ ex.getLocalizedMessage());
        }
                
    }
    
    /*protected void exportDone(JComponent source, Transferable data, int action){
        System.out.printf("exportDone:%s(%d)\n",source.getClass().toString(),action);
    }*/
    
    public boolean canImport(TransferHandler.TransferSupport support) {
        if (support.isDataFlavorSupported(nodeFlavor)) return true;
        else return false;
        
    }
    
    public boolean importData(TransferHandler.TransferSupport support) {
        try {
            JTree.DropLocation treeDrop = (javax.swing.JTree.DropLocation) support.getDropLocation();
            JTree tree = (JTree) support.getComponent();
            DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
            
            DefaultMutableTreeNode[] sourceNodes =
                    (DefaultMutableTreeNode []) support.getTransferable().getTransferData(nodeFlavor);
            DefaultMutableTreeNode destinationNode =
                    (DefaultMutableTreeNode) treeDrop.getPath().getLastPathComponent();
            
            
            // This while loop moves the destination to the bottom of an expanded branch above the destination
            int destChildIndex = treeDrop.getChildIndex();
            if (destChildIndex > 0) {
                while (destinationNode.getChildCount()>0 && 
                        tree.isExpanded(new TreePath(((DefaultMutableTreeNode)
                                destinationNode.getChildAt(destChildIndex - 1)).getPath()))){
                    destinationNode = (DefaultMutableTreeNode) destinationNode.getChildAt(destChildIndex - 1);
                    destChildIndex = destinationNode.getChildCount();
                }
            }
                
            if (destChildIndex == -1) destChildIndex = destinationNode.getChildCount();
            for (int i=0; i<sourceNodes.length; i++){
                if (sourceNodes[i] != destinationNode) {

                    DefaultMutableTreeNode sourceNodeParent = (DefaultMutableTreeNode) sourceNodes[i].getParent();
                    int sourceNodeIndex = sourceNodeParent.getIndex(sourceNodes[i]);

                    if (sourceNodeParent == destinationNode && sourceNodeIndex < destChildIndex) destChildIndex--;

                    sourceNodeParent.remove(sourceNodes[i]);
                    treeModel.nodesWereRemoved(sourceNodeParent, new int[] {sourceNodeIndex}, 
                            new Object[] {sourceNodes[i]});

                    destinationNode.insert(sourceNodes[i], destChildIndex);
                    treeModel.nodesWereInserted(destinationNode, new int[] {destinationNode.getIndex(sourceNodes[i])});
                    destChildIndex++;
                }
            }    
            return true;
            
        } catch (UnsupportedFlavorException ex) {
            System.out.printf("UnspportedFlavorException: %s", ex.getLocalizedMessage());
            System.exit(-1);
        } catch (IOException ex){
            System.out.printf("TransferData IOException: %s", ex.getLocalizedMessage());
            System.exit(-1);
        }
        return false;
    }
    
    protected Transferable createTransferable(JComponent c){
        JTree tree = (JTree) c;
        TreePath[] treePaths = tree.getSelectionPaths();
        DefaultMutableTreeNode[] nodes = new DefaultMutableTreeNode[treePaths.length];
        for (int i=0; i<treePaths.length; i++){
            nodes[i]= (DefaultMutableTreeNode) treePaths[i].getLastPathComponent();
        }
        
        return new nodeTransferable(nodes);
    }
    
    public int getSourceActions(JComponent c){
        return MOVE;
    }
    
    public class nodeTransferable implements Transferable {
            
        DefaultMutableTreeNode[] nodes;
        
        public nodeTransferable(DefaultMutableTreeNode[] nodes){
            this.nodes = nodes;
        }
        
        public Object getTransferData(DataFlavor flavor){
            if (flavor == nodeFlavor) return nodes;
            else return null;
        }
        
        public DataFlavor[] getTransferDataFlavors(){
            return new DataFlavor[] {nodeFlavor};
        }
        
        public boolean isDataFlavorSupported(DataFlavor flavor){
            if (flavor == nodeFlavor) return true;
            else return false;
        }
    }
    
    
}
