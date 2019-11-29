package com.exchangeinfomanager.bankuaichanyelian.chanyeliannews;

import javax.swing.*;

import com.exchangeinfomanager.guifactory.JLabelFactory;
import com.exchangeinfomanager.guifactory.JPanelFactory;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.sql.SQLException;

@SuppressWarnings("all")
public class ModifyMeetingDialog extends MeetingDialog<InsertedMeeting> 
{

    public ModifyMeetingDialog(EventService meetingService, Cache cache) 
    {
        super(meetingService, cache);
        super.setTitle("修改");

        // let's create delete and update buttons
        JLabel deleteButton = JLabelFactory.createPinkButton("删除");
//        JLabel removeButton = JLabelFactory.createOrangeButton("解除关联");
        JLabel updateButton = JLabelFactory.createOrangeButton("更新");

        // then add controllers (listeners)
        deleteButton.addMouseListener(new DeleteController());
//        removeButton.addMouseListener(new RemoveController());
        updateButton.addMouseListener(new UpdateController());

        // and add them to the panel
        JPanel layoutPanel = JPanelFactory.createFixedSizePanel(TITLE_SIZE);
        layoutPanel.add(deleteButton);
        layoutPanel.add(Box.createHorizontalStrut(5));
//        layoutPanel.add(removeButton);
        layoutPanel.add(Box.createHorizontalStrut(20));
        layoutPanel.add(updateButton);
        this.centerPanel.add(layoutPanel);
        this.centerPanel.add(Box.createVerticalStrut(PADDING));
    }
    
    public Boolean setMeeting(InsertedMeeting event)
    {
      	super.setMeeting( event);
    	
    	if(event.getMeetingType() == Meeting.QIANSHI || event.getMeetingType() == Meeting.RUOSHI ) {
    		newsownersField.setVisible(false);
    		newstitleField.setVisible(true);
    		newstitleField.setEnabled(false);
    	}
    		
    	if(event.getMeetingType() == Meeting.ZHISHUDATE ) {
        	endTimeChooser.setVisible(true);
        } 
        
    	centerPanel.revalidate();
    	centerPanel.repaint();
    	
        return true;
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
