package com.exchangeinfomanager.News.ExternalNewsType;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.exchangeinfomanager.NodesServices.SvsForNodeOfBanKuai;
import com.exchangeinfomanager.NodesServices.SvsForNodeOfDaPan;
import com.exchangeinfomanager.NodesServices.SvsForNodeOfStock;
import com.exchangeinfomanager.Services.ServicesForNews;
import com.exchangeinfomanager.guifactory.JLabelFactory;
import com.exchangeinfomanager.guifactory.JPanelFactory;
import com.exchangeinfomanager.nodes.BkChanYeLianTreeNode;

public class ModifyExternalNewsDialog extends ExternalNewsDialog<ExternalNewsType> 
{


    public ModifyExternalNewsDialog(ServicesForNews NewsService) 
    {
    	super(NewsService);
        super.setTitle("�޸�");

        // let's create delete and update buttons
        JLabel deleteButton = JLabelFactory.createPinkButton("ɾ��");
        JLabel updateButton = JLabelFactory.createOrangeButton("����");

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
            	ExternalNewsType mt = getNews();
            	String owncode = mt.getNewsOwnerCodes();
            	
            	SvsForNodeOfBanKuai svsbk = new SvsForNodeOfBanKuai ();
          		SvsForNodeOfStock svsstock = new SvsForNodeOfStock (); 
          		SvsForNodeOfDaPan svsdapan = new SvsForNodeOfDaPan (); 
          		
          		BkChanYeLianTreeNode bk = svsbk.getNode(owncode);
          		BkChanYeLianTreeNode stock = svsstock.getNode(owncode);
          		BkChanYeLianTreeNode dapan = svsdapan.getNode(owncode);
          		
          		if(bk == null && stock == null && dapan == null) {
          			JOptionPane.showMessageDialog(null,"������ɴ��벻���ڣ�");
              		setVisible(true);
              		return;
          		}
          		
          		if(bk != null)
          			mt.setNode(bk);
          		else if(stock != null)
          			mt.setNode(stock);
          		else if(dapan != null)
          			mt.setNode(dapan);
          		
            	NewsService.updateNews(mt);
            	
            	svsbk = null;
            	svsstock = null;
            	svsdapan = null;
            } catch (SQLException e1) {
                e1.printStackTrace();
            } 
            setVisible(false);
        }
    }



}
