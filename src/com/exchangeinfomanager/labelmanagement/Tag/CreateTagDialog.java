package com.exchangeinfomanager.labelmanagement.Tag;


import javax.swing.*;

import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.JLabelFactory;
import com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.JPanelFactory;
import com.exchangeinfomanager.labelmanagement.TagService;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.sql.SQLException;

@SuppressWarnings("all")
public class CreateTagDialog  extends TagDialog<Tag>
{
    public CreateTagDialog(TagService labelService) {
        super(labelService);
        JLabel createButton = JLabelFactory.createOrangeButton("Create");

        createButton.addMouseListener(new CreateController());

        JPanel layoutPanel = JPanelFactory.createFixedSizePanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        layoutPanel.add(createButton);
        this.centerPanel.add(layoutPanel);
    }

    private class CreateController extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            try {
                labelService.createTag(getLabel());
            } catch (SQLException e1) {
                e1.printStackTrace();
            } 
            setVisible(false);
        }
    }
}
