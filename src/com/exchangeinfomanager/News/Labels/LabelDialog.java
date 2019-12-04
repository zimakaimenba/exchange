package com.exchangeinfomanager.News.Labels;

import javax.swing.*;

import com.exchangeinfomanager.News.News;
import com.exchangeinfomanager.Services.ServicesForNewsLabel;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.ColorChooser;
import com.exchangeinfomanager.guifactory.JLabelFactory;
import com.exchangeinfomanager.guifactory.JPanelFactory;
import com.exchangeinfomanager.guifactory.JTextFactory;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@SuppressWarnings("all")
public class LabelDialog<T extends News.Label> extends JDialog 
{

    protected static final int WIDTH = 270;
    protected static final int HEIGHT = 150;
    protected static final int PADDING = 20;

    protected JTextField nameField;
    protected JLabel colorButton;
    protected JPanel centerPanel;
    protected ColorChooser colorChooser = new ColorChooser();
    protected ServicesForNewsLabel labelservice;

    private T NewsLabel;

    public LabelDialog(ServicesForNewsLabel labelservice) {
        this.labelservice = labelservice;
        this.createUI();
        this.createCenterPanel();
    }

    private void createUI() {
        this.centerPanel = JPanelFactory.createPanel();
        this.colorButton = JLabelFactory.createLabel("", 40, 50);
        this.colorButton.addMouseListener(new ColorChooserController());
        this.nameField = JTextFactory.createTextField();
        this.nameField.setBorder(BorderFactory.createMatteBorder(0, 10, 0, 0, this.centerPanel.getBackground()));
    }


    private void createCenterPanel() {
        this.centerPanel = JPanelFactory.createFixedSizePanel(WIDTH, HEIGHT, PADDING);
        this.centerPanel.setLayout(new BoxLayout(this.centerPanel, BoxLayout.Y_AXIS));

        JPanel panel = JPanelFactory.createFixedSizePanel(new BorderLayout());

        panel.add(this.colorButton, BorderLayout.LINE_START);
        panel.add(this.nameField, BorderLayout.CENTER);
        this.centerPanel.add(panel);
        this.centerPanel.add(Box.createVerticalStrut(20));

        super.add(this.centerPanel);
        super.setTitle("Modify label");
        super.setModalityType(ModalityType.APPLICATION_MODAL);
        super.setSize(new Dimension(WIDTH, HEIGHT));
        super.setResizable(false);
    }

    public T getLabel() {
        NewsLabel.setName(nameField.getText());
        NewsLabel.setColor(colorButton.getBackground());
        return NewsLabel;
    }

    public void setLabel(T label) {
        this.NewsLabel = label;
        this.nameField.setText(label.getName());
        this.colorButton.setBackground(label.getColor());
    }

    private class ColorController extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            NewsLabel.setColor(((JLabel) e.getSource()).getBackground());
        }
    }

    private class ColorChooserController extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            colorChooser.setVisible(true);
            colorButton.setBackground(colorChooser.getColor());
        }
    }

}

