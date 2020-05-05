package com.exchangeinfomanager.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.regex.*;
import javax.swing.*;
import javax.swing.text.*;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import com.profesorfalken.jpowershell.PowerShell;
import com.profesorfalken.jpowershell.PowerShellResponse;

import javax.swing.plaf.metal.MetalScrollBarUI;

public class ScrollBarSearchHighlighterTest {
  private static final Highlighter.HighlightPainter highlightPainter
    = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
  private static final String pattern = "Swing";
  private static final String initTxt =
    "Trail: Creating a GUI with JFC/Swing\n" +
    "Lesson: Learning Swing by Example\n" +
    "This lesson explains the concepts you need to\n" +
    " use Swing components in building a user interface.\n" +
    " First we examine the simplest Swing application you can write.\n" +
    " Then we present several progressively complicated examples of creating\n" +
    " user interfaces using components in the javax.swing package.\n" +
    " We cover several Swing components, such as buttons, labels, and text areas.\n" +
    " The handling of events is also discussed,\n" +
    " as are layout management and accessibility.\n" +
    " This lesson ends with a set of questions and exercises\n" +
    " so you can test yourself on what you've learned.\n" +
    "http://docs.oracle.com/javase/tutorial/uiswing/learn/index.html\n";

  private final JTextArea textArea   = new JTextArea();
  private final JScrollPane scroll   = new JScrollPane(textArea);
  private final JScrollBar scrollbar = new JScrollBar(JScrollBar.VERTICAL);
  public JComponent makeUI() 
  {
	PowerShellResponse response = PowerShell.executeSingleCommand("E://firstscript.ps1");
	
  
    textArea.setEditable(false);
    textArea.setLineWrap(true);
    textArea.setText(initTxt+initTxt+initTxt);
    scrollbar.setUnitIncrement(10);

    scrollbar.setUI(new MetalScrollBarUI() {
      @Override protected void paintTrack(
            Graphics g, JComponent c, Rectangle trackBounds) {
        super.paintTrack(g, c, trackBounds);
        Rectangle rect = textArea.getBounds();
        double sy = trackBounds.getHeight() / rect.getHeight();
        AffineTransform at = AffineTransform.getScaleInstance(1.0, sy);
        Highlighter highlighter = textArea.getHighlighter();
        g.setColor(Color.YELLOW);
        try{
          for(Highlighter.Highlight hh: highlighter.getHighlights()) {
            Rectangle r = textArea.modelToView(hh.getStartOffset());
            Rectangle s = at.createTransformedShape(r).getBounds();
            int h = 2; //Math.max(2, s.height-2);
            g.fillRect(trackBounds.x+2, trackBounds.y+1+s.y, trackBounds.width, h);
          }
        } catch(BadLocationException e) {
          e.printStackTrace();
        }
      }
    });
    scroll.setVerticalScrollBar(scrollbar);

    Box box = Box.createHorizontalBox();
    box.add(Box.createHorizontalGlue());
    box.add(new JButton(new AbstractAction("highlight") {
      @Override public void actionPerformed(ActionEvent e) {
        setHighlight(textArea, pattern);
      }
    }));
    box.add(Box.createHorizontalStrut(2));
    box.add(new JButton(new AbstractAction("clear") {
      @Override public void actionPerformed(ActionEvent e) {
        textArea.getHighlighter().removeAllHighlights();
        scrollbar.repaint();
      }
    }));

    JPanel p = new JPanel(new BorderLayout());
    p.add(scroll);
    p.add(box, BorderLayout.SOUTH);
    return p;
  }
  
  public void setHighlight(JTextComponent jtc, String pattern) {
    Highlighter highlighter = jtc.getHighlighter();
    highlighter.removeAllHighlights();
    Document doc = jtc.getDocument();
    try{
      String text = doc.getText(0, doc.getLength());
      Matcher matcher = Pattern.compile(pattern).matcher(text);
      int pos = 0;
      while(matcher.find(pos)) {
        int start = matcher.start();
        int end   = matcher.end();
        highlighter.addHighlight(start, end, highlightPainter);
        pos = end;
      }
    }catch(BadLocationException e) {
      e.printStackTrace();
    }
    scroll.repaint();
  }
  public void removeHighlights(JTextComponent jtc) {
    Highlighter hilite = jtc.getHighlighter();
    Highlighter.Highlight[] hilites = hilite.getHighlights();
    for(int i=0; i<hilites.length; i++) {
      if(hilites[i].getPainter() instanceof
             DefaultHighlighter.DefaultHighlightPainter) {
        hilite.removeHighlight(hilites[i]);
      }
    }
    scroll.repaint();
  }
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      @Override public void run() {
        createAndShowGUI();
      }
    });
  }
  public static void createAndShowGUI() {
    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.getContentPane().add(new ScrollBarSearchHighlighterTest().makeUI());
    frame.setSize(320, 240);
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
}