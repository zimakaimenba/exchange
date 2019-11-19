package com.exchangeinfomanager.commonlib.JComboCheckBox;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

public final class MainPanel extends JPanel {
	  private MainPanel() {
	    super(new BorderLayout());

	    CheckableItem[] m = {
	      new CheckableItem("aaa", false),
	      new CheckableItem("bbbbb", true),
	      new CheckableItem("111", false),
	      new CheckableItem("33333", true),
	      new CheckableItem("2222", true),
	      new CheckableItem("ccccccc", false)
	    };

	    JPanel p = new JPanel(new GridLayout(0, 1));
	    p.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
	    p.add(new JLabel("Default:"));
	    p.add(new JComboBox<>(m));
	    p.add(Box.createVerticalStrut(20));
	    p.add(new JLabel("CheckedComboBox:"));
	    p.add(new CheckedComboBox<>(new DefaultComboBoxModel<>(m)));
	    // p.add(new CheckedComboBox<>(new CheckableComboBoxModel<>(m)));

	    add(p, BorderLayout.NORTH);
	    setPreferredSize(new Dimension(320, 240));
	  }

	  public static void main(String... args) {
	    EventQueue.invokeLater(new Runnable() {
	      @Override public void run() {
	        createAndShowGui();
	      }
	    });
	  }

	  public static void createAndShowGui() {
	    try {
	      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
	      ex.printStackTrace();
	    }
	    JFrame frame = new JFrame("CheckedComboBox");
	    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	    frame.getContentPane().add(new MainPanel());
	    frame.pack();
	    frame.setLocationRelativeTo(null);
	    frame.setVisible(true);
	  }
	}
