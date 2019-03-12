package com.exchangeinfomanager.nodes.operations;

//Example from http://www.crionics.com/products/opensource/faq/swing_ex/SwingExamples.html
//http://www.java2s.com/Code/Java/Swing-Components/InvisibleNodeTreeExample.htm
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
* @version 1.0 01/12/99
*/
class InvisibleNodeTreeExample extends JFrame {
public InvisibleNodeTreeExample() {
 super("InvisibleNode TreeExample");

 String[] strs = { "swing", // 0
     "platf", // 1
     "basic", // 2
     "metal", // 3
     "JTree", // 4
     "metalchild" //5
     }; 

 InvisibleNode[] nodes = new InvisibleNode[strs.length];
 for (int i = 0; i < strs.length; i++) {
   nodes[i] = new InvisibleNode(strs[i]);
 }
 nodes[0].add(nodes[1]);
 nodes[1].add(nodes[2]);
 nodes[1].add(nodes[3]);
 nodes[0].add(nodes[4]);
 nodes[3].add(nodes[5]);
 nodes[3].setVisible(false);
 InvisibleTreeModel ml = new InvisibleTreeModel(nodes[0]);
 ml.activateFilter(true);
 JTree tree = new JTree(ml);
 tree.setCellRenderer(new DefaultTreeCellRenderer() {
   public Component getTreeCellRendererComponent(JTree tree,
       Object value, boolean sel, boolean expanded, boolean leaf,
       int row, boolean hasFocus) {
     super.getTreeCellRendererComponent(tree, value, sel, expanded,
         leaf, row, hasFocus);
     if (!((InvisibleNode) value).isVisible()) {
       setForeground(Color.yellow);
     }
     return this;
   }
 });
 JScrollPane sp = new JScrollPane(tree);

 ModePanel mp = new ModePanel(ml);
 ButtonPanel bp = new ButtonPanel(tree);
 Box box_right = new Box(BoxLayout.Y_AXIS);
 box_right.add(mp);
 box_right.add(bp);

 Box box = new Box(BoxLayout.X_AXIS);
 box.add(sp);
 box.add(Box.createVerticalGlue());
 box.add(box_right);
 getContentPane().add(box, BorderLayout.CENTER);
}

class ModePanel extends JPanel {
 ModePanel(final InvisibleTreeModel model) {
   setLayout(new GridLayout(2, 1));
   setBorder(new TitledBorder("View Mode"));
   ButtonGroup group = new ButtonGroup();
   JRadioButton b_all = new JRadioButton("all");
   JRadioButton b_hide = new JRadioButton("hide");
   add(b_all);
   add(b_hide);
   group.add(b_all);
   group.add(b_hide);
   b_all.addActionListener(new ActionListener() {
     public void actionPerformed(ActionEvent e) {
       if (model.isActivatedFilter()) {
         model.activateFilter(false);
         model.reload();
       }
     }
   });
   b_hide.addActionListener(new ActionListener() {
     public void actionPerformed(ActionEvent e) {
       if (!model.isActivatedFilter()) {
         model.activateFilter(true);
         model.reload();
       }
     }
   });
   b_hide.setSelected(true);
 }
}

class ButtonPanel extends JPanel {
 ButtonPanel(final JTree tree) {
   setLayout(new GridLayout(2, 1));
   setBorder(new TitledBorder("Change Node"));
   JButton b_visible = new JButton("Visible");
   JButton b_invisible = new JButton("Invisible");
   //b_invisible.setForeground(Color.yellow);
   add(b_visible);
   add(b_invisible);
   b_visible.addActionListener(new ActionListener() {
     public void actionPerformed(ActionEvent e) {
       setNodeVisible(tree, true);
     }
   });
   b_invisible.addActionListener(new ActionListener() {
     public void actionPerformed(ActionEvent e) {
       setNodeVisible(tree, false);
     }
   });
 }

 private void setNodeVisible(final JTree tree, boolean isVisible) {
   DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
   TreePath[] path = tree.getSelectionPaths();
   InvisibleNode node = null;
   for (int i = 0; i < path.length; i++) {
     node = (InvisibleNode) path[i].getLastPathComponent();
     if (!(node == model.getRoot())) {
       node.setVisible(isVisible);
     } else {
       System.out.println("refused: root node");
     }
   }
   if (path.length == 1) {
     model.nodeChanged(node);
   } else {
     model.reload();
   }
 }
}

public static void main(String args[]) {
 try {
     UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
 } catch (Exception evt) {}

 InvisibleNodeTreeExample frame = new InvisibleNodeTreeExample();
 frame.addWindowListener(new WindowAdapter() {
   public void windowClosing(WindowEvent e) {
     System.exit(0);
   }
 });
 frame.setSize(300, 180);
 frame.setVisible(true);
}
}



