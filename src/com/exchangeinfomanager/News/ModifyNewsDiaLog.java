package com.exchangeinfomanager.News;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.exchangeinfomanager.Services.ServicesForNews;
import com.exchangeinfomanager.guifactory.JLabelFactory;
import com.exchangeinfomanager.guifactory.JPanelFactory;

public class ModifyNewsDiaLog extends NewsDialog<InsertedNews> 
{

    public ModifyNewsDiaLog(ServicesForNews NewsService, NewsCache cache) 
    {
        super(NewsService, cache);
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
    
    public Boolean setNews(InsertedNews event)
    {
      	super.setNews( event);
        return true;
    }

    private class DeleteController extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            try {
                newsService.deleteNews(getNews());
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
                newsService.updateNews(getNews());
            } catch (SQLException e1) {
                e1.printStackTrace();
            } 
            setVisible(false);
        }
    }

}

