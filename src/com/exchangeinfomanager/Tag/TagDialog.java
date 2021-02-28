package com.exchangeinfomanager.Tag;

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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.exchangeinfomanager.News.Labels.ColorChooser;
import com.exchangeinfomanager.guifactory.JLabelFactory;
import com.exchangeinfomanager.guifactory.JPanelFactory;
import com.exchangeinfomanager.guifactory.JTextFactory;


public class TagDialog<T extends Tag> extends JDialog 
{

    protected static final int WIDTH = 270;
    protected static final int HEIGHT = 250;
    protected static final int PADDING = 20;

    protected JTextArea descriptionField;
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
        
        this.descriptionField = JTextFactory.createTextArea();
        this.descriptionField.setBorder(BorderFactory.createMatteBorder(0, 10, 0, 0, this.centerPanel.getBackground()));
        descriptionField.setLineWrap(true);
    }


    private void createCenterPanel() {
        this.centerPanel = JPanelFactory.createFixedSizePanel(WIDTH, HEIGHT, PADDING);
        this.centerPanel.setLayout(new BoxLayout(this.centerPanel, BoxLayout.Y_AXIS));

        JPanel panel = JPanelFactory.createFixedSizePanel(new BorderLayout());
        panel.add(this.colorButton, BorderLayout.LINE_START);
        panel.add(this.nameField, BorderLayout.CENTER);
        this.centerPanel.add(panel);
        this.centerPanel.add(Box.createVerticalStrut(20));
        
        JScrollPane sclpmonthnews = new JScrollPane ();
        sclpmonthnews.setViewportView(this.descriptionField);
        
        this.centerPanel.add(sclpmonthnews);

        super.add(this.centerPanel);
        super.setTitle("Create Tag");
        super.setModalityType(ModalityType.APPLICATION_MODAL);
        super.setSize(new Dimension(WIDTH, HEIGHT));
        super.setResizable(false);
    }

    public T getLabel() {
        meetingLabel.setName(nameField.getText().trim () );
        meetingLabel.setColor(colorButton.getBackground());
        meetingLabel.setDescription(this.descriptionField.getText().trim() );
        return meetingLabel;
    }

    public void setLabel(T label) {
        this.meetingLabel = label;
        this.nameField.setText(label.getName());
        this.descriptionField.setText(label.getDescription() );
        
        if(label instanceof NodeInsertedTag)
        	this.colorButton.setBackground(  ((NodeInsertedTag)label).getNodeMachColor()   );
        else
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

