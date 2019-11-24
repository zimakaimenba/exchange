package com.exchangeinfomanager.labelmanagement.Tag;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Dialog.ModalityType;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.ColorChooser;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.JLabelFactory;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.JPanelFactory;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.JTextFactory;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.LabelService;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.Meeting;
import com.exchangeinfomanager.labelmanagement.TagService;


public class TagDialog<T extends Tag> extends JDialog 
{

    protected static final int WIDTH = 270;
    protected static final int HEIGHT = 150;
    protected static final int PADDING = 20;

    protected JTextField nameField;
    protected JLabel colorButton;
    protected JPanel centerPanel;
    protected ColorChooser colorChooser = new ColorChooser();
    protected TagService labelService;

    private T meetingLabel;

    public TagDialog(TagService labelService) {
        this.labelService = labelService;
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
        meetingLabel.setName(nameField.getText());
        meetingLabel.setColor(colorButton.getBackground());
        return meetingLabel;
    }

    public void setLabel(T label) {
        this.meetingLabel = label;
        this.nameField.setText(label.getName());
        this.colorButton.setBackground(label.getColor());
    }

    private class ColorController extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            meetingLabel.setColor(((JLabel) e.getSource()).getBackground());
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

