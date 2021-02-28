package com.exchangeinfomanager.News;

import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.exchangeinfomanager.guifactory.JLabelFactory;
import com.exchangeinfomanager.guifactory.JPanelFactory;


public class CreateNewsDialog  extends NewsDialog<News> {

//  private News News;

  public CreateNewsDialog(ServicesForNews NewsService)
  {
      super(NewsService);
      super.setTitle("Create");
      
      // lets create button and add controller to it
      JLabel createButton = JLabelFactory.createOrangeButton("Create");
      createButton.addMouseListener(new CreateController());
      
      // add button to the main panel
      JPanel layoutPanel = JPanelFactory.createFixedSizePanel(new FlowLayout(FlowLayout.RIGHT), 35);
      layoutPanel.add(createButton);
      super.newCenterPanel.add(layoutPanel);
      super.newCenterPanel.add(Box.createVerticalStrut(PADDING));
  }
  /*
   * (non-Javadoc)
   * @see com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.NewsDialog#setNews(com.exchangeinfomanager.bankuaichanyelian.chanyeliannews.News)
   */
  public Boolean setNews(News event)
  {
  	super.setNews( event);
  	return true;
  }
  /*
   * 
   */
  private class CreateController extends MouseAdapter {

      @Override
      public void mouseClicked(MouseEvent e) {
          super.mouseClicked(e);

          try {
          	News mt = getNews();
          	if(mt.getTitle().length() >150) {
          		JOptionPane.showMessageDialog(null,"新闻标题过长！");
          		setVisible(true);
          	} else { 
          		newsService.createNews(mt);
          		setVisible(false);
          	}
          } catch (com.mysql.jdbc.MysqlDataTruncation e2) {
          	e2.printStackTrace();
      	}catch (SQLException e1) {
              e1.printStackTrace();
          }
      }
  }
}
