package com.exchangeinfomanager.News.Labels;

import javax.swing.*;

import com.exchangeinfomanager.News.InsertedNews;
import com.exchangeinfomanager.News.ServicesForNewsLabel;
import com.exchangeinfomanager.guifactory.JLabelFactory;
import com.exchangeinfomanager.guifactory.JPanelFactory;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.sql.SQLException;

@SuppressWarnings("all")
public class ModifyLabelDialog extends NewsLabelDialog<InsertedNews.Label>
{
    public ModifyLabelDialog(ServicesForNewsLabel labelService) {
        super(labelService);
        JLabel deleteButton = JLabelFactory.createPinkButton("Delete");
        JLabel updateButton = JLabelFactory.createOrangeButton("Update");

        deleteButton.addMouseListener(new DeleteController());
        updateButton.addMouseListener(new UpdateController());

        JPanel layoutPanel = JPanelFactory.createFixedSizePanel();
        layoutPanel.add(deleteButton);
        layoutPanel.add(Box.createHorizontalStrut(5));
        layoutPanel.add(updateButton);
        this.centerPanel.add(layoutPanel);

    }

    private class UpdateController extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            try {
            	labelservice.updateLabel(getLabel());
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            setVisible(false);
        }
    }
    private class DeleteController extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            try {
            	labelservice.deleteLabel(getLabel());
            } catch (SQLException e1) {
                e1.printStackTrace();
            } 
            setVisible(false);
        }
    }
}
