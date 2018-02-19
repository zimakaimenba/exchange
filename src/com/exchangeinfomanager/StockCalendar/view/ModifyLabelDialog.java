package com.exchangeinfomanager.StockCalendar.view;

import javax.swing.*;

import com.exchangeinfomanager.StockCalendar.InsertedMeeting;
import com.exchangeinfomanager.StockCalendar.LabelService;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.sql.SQLException;

@SuppressWarnings("all")
public class ModifyLabelDialog extends LabelDialog<InsertedMeeting.Label> {
    public ModifyLabelDialog(LabelService labelService) {
        super(labelService);
        JLabel deleteButton = JLabelFactory.createPinkButton("Delete");
        JLabel updateButton = JLabelFactory.createOrangeButton("Update");

        deleteButton.addMouseListener(new DeleteController());
        updateButton.addMouseListener(new UpdateController());

        JPanel layoutPanel = JPanelFactory.createFixedSizePanel();
        layoutPanel.add(deleteButton);
        layoutPanel.add(Box.createHorizontalStrut(20));
        layoutPanel.add(updateButton);
        this.centerPanel.add(layoutPanel);

    }

    private class UpdateController extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            try {
                labelService.updateLabel(getLabel());
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
                labelService.deleteLabel(getLabel());
            } catch (SQLException e1) {
                e1.printStackTrace();
            } 
            setVisible(false);
        }
    }
}
