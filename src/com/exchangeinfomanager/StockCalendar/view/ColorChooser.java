package com.exchangeinfomanager.StockCalendar.view;

import javax.swing.*;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.exchangeinfomanager.StockCalendar.ColorScheme;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

@SuppressWarnings("all")
public class ColorChooser extends JDialog {

	private JButton btnclose;
    private JPanel centerPanel;
    private static final int WIDTH = 450;
    private static final int HEIGHT = 450;
    static final int ROWS = 8;
    static final int COLUMNS = 5;
    JColorChooser colorChooser ;

    private Color color = ColorScheme.ORANGE_LIGHT;

    public ColorChooser() {
    	colorChooser = new JColorChooser();
    	btnclose = new JButton("È·¶¨");
        this.initCenterPanel();
        this.initColorChooserEvents();
    }

    private void initCenterPanel() {
    	this.centerPanel = JPanelFactory.createFixedSizePanel(WIDTH, HEIGHT);
        this.centerPanel.setLayout(new BoxLayout(this.centerPanel, BoxLayout.PAGE_AXIS));
        
        this.centerPanel.add(colorChooser);
        this.centerPanel.add(Box.createVerticalStrut(5));
        JPanel p = JPanelFactory.createPanel(new FlowLayout(FlowLayout.RIGHT));
        p.add(btnclose);
        this.centerPanel.add(p);
        this.centerPanel.add(Box.createVerticalStrut(5));
        
        super.add(this.centerPanel);
        
        super.setModalityType(ModalityType.APPLICATION_MODAL);
        super.setSize(new Dimension(WIDTH, HEIGHT));
        super.setResizable(true);
    }

    private void initColorChooserEvents() {
//        super.add(this.centerPanel);
//        
//        super.setModalityType(ModalityType.APPLICATION_MODAL);
//        super.setSize(new Dimension(WIDTH, HEIGHT));
//        super.setResizable(true);
        
        ColorSelectionModel model = colorChooser.getSelectionModel();
        ChangeListener changeListener = new ChangeListener() {
          public void stateChanged(ChangeEvent changeEvent) {
            Color newForegroundColor = colorChooser.getColor();
            color = newForegroundColor;
          }
        };
        model.addChangeListener(changeListener);
        
        btnclose.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) 
			{
				setVisible(false);
			}
		});
    }

    public static void main(String[] args) {
        new ColorChooser().setVisible(true);
    }

    public Color getColor() {
        return this.color;
    }

    private class ChooseController extends MouseAdapter{

        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            color = ((JLabel)e.getSource()).getBackground();
            setVisible(false);
        }
    }
}
