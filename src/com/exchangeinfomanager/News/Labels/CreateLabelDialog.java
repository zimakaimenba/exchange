package com.exchangeinfomanager.News.Labels;

import javax.swing.*;

import com.exchangeinfomanager.News.News;
import com.exchangeinfomanager.News.NewsLabelServices;
import com.exchangeinfomanager.Services.ServicesForNewsLabel;
import com.exchangeinfomanager.guifactory.JLabelFactory;
import com.exchangeinfomanager.guifactory.JPanelFactory;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.sql.SQLException;

@SuppressWarnings("all")
public class CreateLabelDialog extends NewsLabelDialog<News.Label> {
    public CreateLabelDialog(ServicesForNewsLabel labelService) {
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
            	labelservice.createLabel(getLabel());
            } catch (SQLException e1) {
                e1.printStackTrace();
            } 
            setVisible(false);
        }
    }
}
