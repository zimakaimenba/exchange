package com.exchangeinfomanager.News.ExternalNewsType;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.exchangeinfomanager.Services.ServicesForNews;
import com.exchangeinfomanager.guifactory.JLabelFactory;
import com.exchangeinfomanager.guifactory.JPanelFactory;

public class ModifyExternalNewsDialog extends ExternalNewsDialog<ExternalNewsType> 
{


    public ModifyExternalNewsDialog(ServicesForNews NewsService) 
    {
    	super(NewsService);
        super.setTitle("ÐÞ¸Ä");

        // let's create delete and update buttons
        JLabel deleteButton = JLabelFactory.createPinkButton("É¾³ý");
        JLabel updateButton = JLabelFactory.createOrangeButton("¸üÐÂ");

        // then add controllers (listeners)
        deleteButton.addMouseListener(new DeleteController());
        updateButton.addMouseListener(new UpdateController());

        // and add them to the panel
        JPanel layoutPanel = JPanelFactory.createFixedSizePanel(TITLE_SIZE);
        layoutPanel.add(deleteButton);
        layoutPanel.add(Box.createHorizontalStrut(5));
        layoutPanel.add(Box.createHorizontalStrut(20));
        layoutPanel.add(updateButton);
        this.centerPanel.add(layoutPanel);
        this.centerPanel.add(Box.createVerticalStrut(PADDING));
    }
    
    public Boolean setMeeting(ExternalNewsType event)
    {
      	super.setNews( event);
        
    	centerPanel.revalidate();
    	centerPanel.repaint();
    	
        return true;
    }

    private class DeleteController extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            try {
            	NewsService.deleteNews(getNews());
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
            	NewsService.updateNews(getNews());
            } catch (SQLException e1) {
                e1.printStackTrace();
            } 
            setVisible(false);
        }
    }



}
