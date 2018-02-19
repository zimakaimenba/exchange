package com.exchangeinfomanager.StockCalendar.view;

import javax.swing.*;

import com.exchangeinfomanager.StockCalendar.Cache;
import com.exchangeinfomanager.StockCalendar.InsertedMeeting;
import com.exchangeinfomanager.StockCalendar.MeetingService;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.sql.SQLException;

@SuppressWarnings("all")
public class ModifyMeetingDialog extends MeetingDialog<InsertedMeeting> {

    public ModifyMeetingDialog(MeetingService meetingService, Cache cache) {
        super(meetingService, cache);
        super.setTitle("ÐÞ¸Ä");
        super.startTimeChooser.setEnabled(false);

        // let's create delete and update buttons
        JLabel deleteButton = JLabelFactory.createPinkButton("É¾³ý");
        JLabel updateButton = JLabelFactory.createOrangeButton("¸üÐÂ");

        // then add controllers (listeners)
        deleteButton.addMouseListener(new DeleteController());
        updateButton.addMouseListener(new UpdateController());

        // and add them to the panel
        JPanel layoutPanel = JPanelFactory.createFixedSizePanel(TITLE_SIZE);
        layoutPanel.add(deleteButton);
        layoutPanel.add(Box.createHorizontalStrut(150));
        layoutPanel.add(updateButton);
        this.centerPanel.add(layoutPanel);
        this.centerPanel.add(Box.createVerticalStrut(PADDING));
    }

    private class DeleteController extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            try {
                meetingService.deleteMeeting(getMeeting());
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            setVisible(false);
        }
    }

    private class UpdateController extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            try {
                meetingService.updateMeeting(getMeeting());
            } catch (SQLException e1) {
                e1.printStackTrace();
            } 
            setVisible(false);
        }
    }
}
